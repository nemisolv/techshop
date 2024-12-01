package net.nemisolv.techshop.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.nemisolv.techshop.core._enum.AuthProvider;
import net.nemisolv.techshop.core._enum.MailType;
import net.nemisolv.techshop.core.exception.BadCredentialException;
import net.nemisolv.techshop.core.exception.BadRequestException;
import net.nemisolv.techshop.entity.ConfirmationEmail;
import net.nemisolv.techshop.entity.Role;
import net.nemisolv.techshop.entity.User;
import net.nemisolv.techshop.helper.UserHelper;
import net.nemisolv.techshop.mapper.UserMapper;
import net.nemisolv.techshop.payload.auth.*;
import net.nemisolv.techshop.repository.ConfirmationEmailRepository;
import net.nemisolv.techshop.repository.RoleRepository;
import net.nemisolv.techshop.repository.UserRepository;
import net.nemisolv.techshop.security.UserPrincipal;
import net.nemisolv.techshop.security.context.AuthContext;
import net.nemisolv.techshop.service.AuthService;
import net.nemisolv.techshop.service.EmailService;
import net.nemisolv.techshop.service.JwtService;
import net.nemisolv.techshop.util.ResultCode;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final ConfirmationEmailRepository confirmationEmailRepo;
    private final UserHelper userHelper;
    private final UserMapper userMapper;
    private final EmailService emailService;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            if (!userPrincipal.isEmailVerified()) {
                throw new BadCredentialException(ResultCode.USER_AUTH_ERROR, "Email not verified");
            }

            return generateAuthResponse(userPrincipal);

        } catch (AuthenticationException ex) {
            throw new BadCredentialException(ResultCode.USER_AUTH_ERROR);
        }
    }

    @Override
    @Transactional
    public void registerExternal(RegisterRequest authRequest) throws MessagingException {
        Optional<User> existingUser = userRepo.findByEmail(authRequest.getEmail());

        if (existingUser.isPresent()) {
            handleExistingUserForRegistration(existingUser.get(), authRequest);
        } else {
            createAndSendVerificationEmail(authRequest);
        }
    }

    @Override
    public void registerInternal(RegisterInternalRequest authRequest) {
        if (userRepo.existsByEmail(authRequest.getEmail())) {
            throw new BadCredentialException(ResultCode.USER_ALREADY_EXISTS);
        }
        UserPrincipal currentUser  = AuthContext.getCurrentUser();
//        if(userPrincipal.getAuthorities() )

            Role roleToAssign = roleRepo.findByName(authRequest.getRole())
                    .orElseThrow(() -> new BadRequestException(ResultCode.ROLE_NOT_FOUND));

        // Kiểm tra quyền của user hiện tại
        if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            // Admin có thể gán bất kỳ role nào
        } else if (currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_MANAGER"))) {
            // Manager chỉ được gán các role cụ thể
            if (!isAssignableByManager(roleToAssign)) {
                throw new AccessDeniedException("Managers cannot assign this role.");
            }
        } else {
            throw new AccessDeniedException("Unauthorized to assign roles.");
        }

        User newUser = createUser(authRequest, roleToAssign);
        userRepo.save(newUser);
    }

    @Override
    public void refreshToken(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String refreshToken = extractBearerToken(req.getHeader(HttpHeaders.AUTHORIZATION));
        if (refreshToken == null) return;

        String userEmail = jwtService.extractUsername(refreshToken);
        if (userEmail != null) {
            User user = userRepo.findByEmail(userEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            if (jwtService.isValidToken(refreshToken, UserPrincipal.create(user))) {
                writeAuthResponse(res, generateAuthResponse(UserPrincipal.create(user)));
            }
        }
    }

    @Override
    public void verifyEmail(String token) {
        ConfirmationEmail confirmationEmail = validateConfirmationEmail(token);

        User user = userRepo.findById(confirmationEmail.getUserId())
                .orElseThrow(() -> new BadRequestException(ResultCode.USER_NOT_FOUND));

        user.setEmailVerified(true);
        confirmationEmail.setConfirmedAt(LocalDateTime.now());

        userRepo.save(user);
        confirmationEmailRepo.save(confirmationEmail);
    }

    @Override
    public void forgotPassword(String email) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found"));
        // TODO: Implement email sending logic
    }

    @Override
    public void resetPassword(ResetPasswordRequest resetRequest) {
        ConfirmationEmail confirmationEmail = validateConfirmationEmail(resetRequest.getToken());

        User user = userRepo.findById(confirmationEmail.getUserId())
                .orElseThrow(() -> new BadRequestException(ResultCode.USER_NOT_FOUND));

        user.setPassword(passwordEncoder.encode(resetRequest.getNewPassword()));
        confirmationEmail.setConfirmedAt(LocalDateTime.now());

        userRepo.save(user);
        confirmationEmailRepo.save(confirmationEmail);
    }

    /**
     * Helper Methods
     */

    private AuthenticationResponse generateAuthResponse(UserPrincipal userPrincipal) {
        User user = userRepo.findByEmail(userPrincipal.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return AuthenticationResponse.builder()
                .userData(userMapper.toFullUserInfo(user))
                .accessToken(jwtService.generateToken(userPrincipal))
                .refreshToken(jwtService.generateRefreshToken(userPrincipal))
                .build();
    }

    private void handleExistingUserForRegistration(User user, RegisterRequest authRequest) throws MessagingException {
        if (user.isEmailVerified()) {
            throw new BadCredentialException(ResultCode.USER_ALREADY_EXISTS);
        }

        updateExistingUser(user, authRequest);
        sendVerificationEmail(user);
    }

    private void createAndSendVerificationEmail(RegisterRequest authRequest) throws MessagingException {
        User newUser = createUser(authRequest, null);
        userRepo.save(newUser);

        sendVerificationEmail(newUser);
    }

    private void sendVerificationEmail(User user) throws MessagingException {
        String token = jwtService.generateToken(UserPrincipal.create(user));

        ConfirmationEmail confirmationEmail = ConfirmationEmail.builder()
                .token(token)
                .type(MailType.REGISTRATION_CONFIRMATION)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .userId(user.getId())
                .build();

        confirmationEmailRepo.save(confirmationEmail);
        emailService.sendRegistrationEmail(user, token);
    }

    private User createUser(RegisterRequest authRequest, Role role) {



        return User.builder()
                .email(authRequest.getEmail())
                .username(userHelper.generateUsername(authRequest.getFirstName(), authRequest.getLastName()))
                .password(passwordEncoder.encode(authRequest.getPassword()))
                .firstName(authRequest.getFirstName())
                .lastName(authRequest.getLastName())
                .emailVerified(false)
                .enabled(true)
                .authProvider(AuthProvider.LOCAL)
                .role(role)
                .build();
    }

    private void updateExistingUser(User user, RegisterRequest authRequest) {
        user.setFirstName(authRequest.getFirstName());
        user.setLastName(authRequest.getLastName());
        user.setPassword(passwordEncoder.encode(authRequest.getPassword()));

        confirmationEmailRepo.findByTypeAndUserId(MailType.REGISTRATION_CONFIRMATION, user.getId())
                .forEach(confirmationEmail -> {
                    confirmationEmail.setRevoked(true);
                    confirmationEmailRepo.save(confirmationEmail);
                });

        userRepo.save(user);
    }

    private ConfirmationEmail validateConfirmationEmail(String token) {
        ConfirmationEmail confirmationEmail = confirmationEmailRepo.findByToken(token)
                .orElseThrow(() -> new BadRequestException(ResultCode.AUTH_TOKEN_INVALID));

        if (confirmationEmail.isRevoked() || confirmationEmail.getExpiredAt().isBefore(LocalDateTime.now())
                || confirmationEmail.getConfirmedAt() != null) {
            throw new BadRequestException(ResultCode.AUTH_TOKEN_INVALID);
        }

        return confirmationEmail;
    }

    private String extractBearerToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }

    private void writeAuthResponse(HttpServletResponse res, AuthenticationResponse authResponse) throws IOException {
        new ObjectMapper().writeValue(res.getOutputStream(), authResponse);
    }

    private boolean isAssignableByManager(Role role) {
        List<String> allowedRolesForManager = List.of("ROLE_STAFF", "ROLE_ASSISTANT");
        return allowedRolesForManager.contains(role.getName().toString());
    }

}

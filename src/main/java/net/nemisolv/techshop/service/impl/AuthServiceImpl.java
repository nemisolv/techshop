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
import net.nemisolv.techshop.payload.user.FullUserInfo;
import net.nemisolv.techshop.repository.ConfirmationEmailRepository;
import net.nemisolv.techshop.repository.RoleRepository;
import net.nemisolv.techshop.repository.UserRepository;
import net.nemisolv.techshop.security.UserPrincipal;
import net.nemisolv.techshop.service.AuthService;
import net.nemisolv.techshop.service.EmailService;
import net.nemisolv.techshop.service.JwtService;
import net.nemisolv.techshop.util.ResultCode;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashSet;
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

            User user = (User) authentication.getPrincipal();

            if (!user.isEmailVerified()) {
                throw new BadCredentialException(ResultCode.USER_NOT_VERIFIED);
            }

            return getAuthenticationResponse(user);


            // do not catch specific exception -> risk of leaking information
//        }catch (LockedException ex) {
//            throw new LockedException("User account is locked");
//        }
//        catch (AuthenticationException ex) {
//            throw new BadCredentialsException(ex.getMessage());
//        }
        } catch (AuthenticationException ex) {
            throw new BadCredentialException(ResultCode.USER_AUTH_ERROR);
        }

    }

    @Override
    @Transactional
    public void registerExternal(RegisterExternalRequest authRequest) throws MessagingException {
        Optional<User> user = userRepo.findByEmail(authRequest.getEmail());
        // with normal registration, don't need to specify any role

        if (user.isPresent() ) {
            if(user.get().isEmailVerified()) {
                throw new BadCredentialException(ResultCode.USER_ALREADY_EXISTS);

            }else {
                // update user info
                var userToUpdate = user.get();
                userToUpdate.setFirstName(authRequest.getFirstName());
                userToUpdate.setLastName(authRequest.getLastName());
                userToUpdate.setPassword(passwordEncoder.encode(authRequest.getPassword()));
                userRepo.save(userToUpdate);
                // revoke all old tokens before sending new verification email

                confirmationEmailRepo.findByTypeAndUserId(MailType.REGISTRATION_CONFIRMATION, userToUpdate.getId())
                        .forEach(confirmationEmail -> {
                            confirmationEmail.setRevoked(true);
                            confirmationEmailRepo.save(confirmationEmail);
                        });
                // TODO: send a new verification email
                UserPrincipal userPrincipal = UserPrincipal.create(userToUpdate);
                String token = jwtService.generateToken(userPrincipal);
                emailService.sendRegistrationEmail(userToUpdate, token);


            }
        }else {
            // create new user
            String username = userHelper.generateUsername(authRequest.getFirstName(), authRequest.getLastName());
            User newUser = User.builder()
                    .email(authRequest.getEmail())
                    .username(username)
                    .password(passwordEncoder.encode(authRequest.getPassword()))
                    .firstName(authRequest.getFirstName())
                    .lastName(authRequest.getLastName())
                    .emailVerified(false)
                    .enabled(true)
                    .authProvider(AuthProvider.LOCAL) // default auth provider
                    .build();

            User savedUser = userRepo.save(newUser);

            UserPrincipal userPrincipal = UserPrincipal.create(savedUser);
            // store token
            String token = jwtService.generateToken(userPrincipal);
            emailService.sendRegistrationEmail(savedUser, token);
        }



    }

    @Override
    public void registerInternal(RegisterInternalRequest authRequest) {
        Optional<User> user = userRepo.findByEmail(authRequest.getEmail());
        // with normal registration, don't need to specify any role

        if(user.isPresent()) {
            throw new BadCredentialException(ResultCode.USER_ALREADY_EXISTS);
        }

        Role role = roleRepo.findByName(authRequest.getRole()).orElseThrow(() -> new BadRequestException(ResultCode.ROLE_NOT_FOUND));
        User newUser = User.builder()
                .email(authRequest.getEmail())
                .username(userHelper.generateUsername(authRequest.getFirstName(), authRequest.getLastName()))
                .password(passwordEncoder.encode(authRequest.getPassword()))
                .firstName(authRequest.getFirstName())
                .lastName(authRequest.getLastName())
                .emailVerified(false)
                .enabled(true)
                .authProvider(AuthProvider.LOCAL) // default auth provider
                .role(role)
                .build();

     userRepo.save(newUser);

    }

    @Override
    public void refreshToken(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String authHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        String userEmail = jwtService.extractUsername(refreshToken);
        if(userEmail!=null) {
                var userDetails = userRepo.findByEmail(userEmail)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            UserPrincipal userPrincipal = UserPrincipal.create(userDetails);
                if(jwtService.isValidToken(refreshToken,userPrincipal)) {
                    var newToken = jwtService.generateToken(userPrincipal);

                    var authResponse = AuthenticationResponse.builder()
                            .accessToken(newToken)
                            .refreshToken(refreshToken).build();
                    new ObjectMapper().writeValue(res.getOutputStream(), authResponse);
                }
        }
    }


    @Override
    public void verifyEmail(String token) throws BadRequestException {
        String acc = jwtService.extractUsername(token);
        User user = userRepo.findByEmail(acc).orElseThrow(() -> new BadRequestException(ResultCode.USER_NOT_FOUND));
        Optional<ConfirmationEmail> tokenOptional = confirmationEmailRepo.findByUserIdAndToken(user.getId(), token);
        if(tokenOptional.isEmpty()) {
            throw new BadRequestException(ResultCode.AUTH_TOKEN_INVALID);
        }
        ConfirmationEmail confirmationEmail = tokenOptional.get();
        if(confirmationEmail.isRevoked() ) {
            throw new BadRequestException(ResultCode.AUTH_TOKEN_INVALID);
        }
        if(confirmationEmail.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(ResultCode.AUTH_TOKEN_INVALID);
        }
        // check if email already verified after checking token expired
        if(confirmationEmail.getConfirmedAt() != null) {
            throw new BadRequestException(ResultCode.AUTH_TOKEN_INVALID);
        }

        confirmationEmail.setConfirmedAt(LocalDateTime.now());
        user.setEmailVerified(true);
        userRepo.save(user);
        confirmationEmailRepo.save(confirmationEmail);
    }

    @Override
    public void forgotPassword(String email) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Email not found"));
        // TODO: send mail with reset password link
    }

    @Override
    public void resetPassword(ResetPasswordRequest reset) throws BadRequestException {
        String acc;
        try {
            acc = jwtService.extractUsername(reset.getToken());

        } catch (Exception e) {
            throw new BadRequestException(ResultCode.AUTH_TOKEN_INVALID);
        }
        User user = userRepo.findByEmail(acc).orElseThrow(() -> new BadRequestException(ResultCode.USER_NOT_FOUND));
        Optional<ConfirmationEmail> tokenOptional = confirmationEmailRepo.findByUserIdAndToken(user.getId(), reset.getToken());
        if(tokenOptional.isEmpty()) {
            throw new BadRequestException(ResultCode.AUTH_TOKEN_INVALID);
        }
        ConfirmationEmail confirmationEmail = tokenOptional.get();
        if(confirmationEmail.isRevoked() ) {
            throw new BadRequestException(ResultCode.AUTH_TOKEN_INVALID);
        }
        if(confirmationEmail.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException(ResultCode.AUTH_TOKEN_INVALID);
        }
        if(confirmationEmail.getConfirmedAt() != null) {
            throw new BadRequestException(ResultCode.AUTH_TOKEN_INVALID);
        }

        user.setPassword(passwordEncoder.encode(reset.getNewPassword()));
        userRepo.save(user);
        confirmationEmail.setConfirmedAt(LocalDateTime.now());
        confirmationEmailRepo.save(confirmationEmail);

    }



    private AuthenticationResponse getAuthenticationResponse(User user) {

        // instead of adding user's info to token, let's separate it
        FullUserInfo userInfo =  userMapper.toFullUserInfo(user);
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        String token = jwtService.generateToken(userPrincipal);
        String refreshToken = jwtService.generateRefreshToken(userPrincipal);

        return AuthenticationResponse.builder()
                .userData(userInfo)
                .accessToken(token).refreshToken(refreshToken).build();
    }


}

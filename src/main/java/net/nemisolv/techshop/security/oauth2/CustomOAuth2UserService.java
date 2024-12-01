package net.nemisolv.techshop.security.oauth2;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import net.nemisolv.techshop.core._enum.AuthProvider;
import net.nemisolv.techshop.core.exception.OAuth2AuthenticationProcessionException;
import net.nemisolv.techshop.entity.User;
import net.nemisolv.techshop.helper.UserHelper;
import net.nemisolv.techshop.repository.RoleRepository;
import net.nemisolv.techshop.repository.UserRepository;
import net.nemisolv.techshop.security.UserPrincipal;
import net.nemisolv.techshop.security.oauth2.user.OAuth2UserInfo;
import net.nemisolv.techshop.security.oauth2.user.OAuth2UserInfoFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepo;
    private final UserHelper userHelper;
private final RoleRepository roleRepo;
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        try {
            return processOAuth2User(userRequest, oAuth2User);

        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // trigger OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }

    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOauth2UserInfo(
                userRequest.getClientRegistration().getRegistrationId(),
                oAuth2User.getAttributes());
    //because gitHub does not provide email, we don't need to check for email
//        if (oAuth2UserInfo.getEmail().isEmpty()) {
//            throw new OAuth2AuthenticationProcessionException("Email not found from OAuth2 provider");
//        }
        Optional<User> userOptional = userRepo.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        if(userOptional.isPresent()) {
            user = userOptional.get();
            if (!user.getAuthProvider().equals(AuthProvider.getEnum(userRequest.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationProcessionException("Looks like you're signed up with " +
                        user.getAuthProvider().getValue() + " account. Please use your " + user.getAuthProvider().getValue() +
                        " account to login.");
            }
            user = updateExistingUser(user, oAuth2UserInfo, userRequest);
        } else {
            user = registerNewUser(userRequest, oAuth2UserInfo);
        }

        return UserPrincipal.create(user);
    }

    private User registerNewUser(OAuth2UserRequest userRequest, OAuth2UserInfo oauth2UserInfo) {

        User user = new User();
        setName(oauth2UserInfo.getName(),user);
        user.setEmail(oauth2UserInfo.getEmail());
        String username = userHelper.generateUsername(oauth2UserInfo.getName(),"");
        user.setUsername(username);
        user.setEmailVerified(true);
        user.setAuthProvider(AuthProvider.getEnum(userRequest.getClientRegistration().getRegistrationId()));
        user.setProviderId(oauth2UserInfo.getId());
        user.setImgUrl(oauth2UserInfo.getImageUrl());
        return userRepo.save(user);
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oauth2UserInfo, OAuth2UserRequest userRequest) {
        // allow user to update photo
//        existingUser.setPicture(oauth2UserInfo.getImageUrl());
        existingUser.setProviderId(oauth2UserInfo.getId());
        existingUser.setAuthProvider(AuthProvider.getEnum(userRequest.getClientRegistration().getRegistrationId()));
        return userRepo.save(existingUser);
    }

    private void setName(String name, User user) {
        if(name == null) {
            return;
        }
        if(name.length() >1) {
        String[] names = name.split(" ");
        user.setFirstName(names[0]);
        if(names.length>1) {
            user.setLastName(names[1]);
        }

        }else {
            user.setFirstName(name);
            user.setLastName("");
        }
    }
}
package net.nemisolv.techshop.service;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.nemisolv.techshop.payload.auth.*;

import java.io.IOException;

public interface AuthService {
    AuthenticationResponse authenticate(AuthenticationRequest authRequest);
    void registerExternal(RegisterExternalRequest authRequest) throws MessagingException;

    // this method is used only by the manager or admin to register a new employee, it is not exposed to the public
    void registerInternal(RegisterInternalRequest authRequest) ;
    void refreshToken(HttpServletRequest req, HttpServletResponse res) throws IOException;

    void verifyEmail(String token) ;

    void forgotPassword(String email);

    void resetPassword(ResetPasswordRequest reset) ;
}


package com.nemisolv.payload.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.nemisolv.payload.user.FullUserInfoDTO;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AuthenticationResponse {
    private String accessToken;
    private String refreshToken;
    private FullUserInfoDTO userData;
    private String secretImageUri;
    private boolean mfaEnabled;
}

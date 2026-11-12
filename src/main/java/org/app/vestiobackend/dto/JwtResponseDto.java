package org.app.vestiobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtResponseDto {
    private String accessToken;
    private String idToken;
    private String refreshToken;
    private Long expiresIn;
    private String tokenType;
}

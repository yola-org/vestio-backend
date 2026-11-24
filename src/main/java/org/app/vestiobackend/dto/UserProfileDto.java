package org.app.vestiobackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Boolean emailVerified;
    private Integer age;
    private String gender;
    private String city;
    private String bio;
    private String phoneNumber;
    private String profileImageKey;
}

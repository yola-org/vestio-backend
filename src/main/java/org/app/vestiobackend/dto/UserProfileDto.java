package org.app.vestiobackend.dto;

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
    private Integer age;
    private String gender;
    private String city;
    private String bio;
    private String phoneNumber;
}

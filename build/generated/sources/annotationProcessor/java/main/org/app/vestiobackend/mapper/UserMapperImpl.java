package org.app.vestiobackend.mapper;

import javax.annotation.processing.Generated;
import org.app.vestiobackend.dto.SignupRequestDto;
import org.app.vestiobackend.dto.UserProfileDto;
import org.app.vestiobackend.entity.User;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-02T13:34:53-0500",
    comments = "version: 1.6.2, compiler: IncrementalProcessingEnvironment from gradle-java-compiler-worker-9.8.2.jar, environment: Java 17.0.11 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserProfileDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserProfileDto.UserProfileDtoBuilder userProfileDto = UserProfileDto.builder();

        userProfileDto.id( user.getId() );
        userProfileDto.firstName( user.getFirstName() );
        userProfileDto.lastName( user.getLastName() );
        userProfileDto.email( user.getEmail() );
        userProfileDto.age( user.getAge() );
        userProfileDto.gender( user.getGender() );
        userProfileDto.city( user.getCity() );
        userProfileDto.bio( user.getBio() );
        userProfileDto.phoneNumber( user.getPhoneNumber() );

        return userProfileDto.build();
    }

    @Override
    public User toEntity(UserProfileDto dto) {
        if ( dto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.id( dto.getId() );
        user.firstName( dto.getFirstName() );
        user.lastName( dto.getLastName() );
        user.email( dto.getEmail() );
        user.age( dto.getAge() );
        user.gender( dto.getGender() );
        user.city( dto.getCity() );
        user.bio( dto.getBio() );
        user.phoneNumber( dto.getPhoneNumber() );

        return user.build();
    }

    @Override
    public User signupRequestToUser(SignupRequestDto dto) {
        if ( dto == null ) {
            return null;
        }

        User.UserBuilder user = User.builder();

        user.firstName( dto.getFirstName() );
        user.lastName( dto.getLastName() );
        user.email( dto.getEmail() );

        return user.build();
    }

    @Override
    public void updateUserFromDto(UserProfileDto dto, User user) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getId() != null ) {
            user.setId( dto.getId() );
        }
        if ( dto.getFirstName() != null ) {
            user.setFirstName( dto.getFirstName() );
        }
        if ( dto.getLastName() != null ) {
            user.setLastName( dto.getLastName() );
        }
        if ( dto.getEmail() != null ) {
            user.setEmail( dto.getEmail() );
        }
        if ( dto.getAge() != null ) {
            user.setAge( dto.getAge() );
        }
        if ( dto.getGender() != null ) {
            user.setGender( dto.getGender() );
        }
        if ( dto.getCity() != null ) {
            user.setCity( dto.getCity() );
        }
        if ( dto.getBio() != null ) {
            user.setBio( dto.getBio() );
        }
        if ( dto.getPhoneNumber() != null ) {
            user.setPhoneNumber( dto.getPhoneNumber() );
        }
    }
}

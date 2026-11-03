package org.app.vestiobackend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OnboardingStatusDto {
    private boolean accountCreated;
    private boolean additionalDataAdded;
    private boolean preferencesAdded;
    private boolean publishedSomething;
}

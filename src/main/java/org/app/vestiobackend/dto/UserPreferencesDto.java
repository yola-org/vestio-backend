package org.app.vestiobackend.dto;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class UserPreferencesDto {
    private List<UUID> categoryIds;
    private List<CategoryDto> categories;
    private String gender;
}

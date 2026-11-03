package org.app.vestiobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {
    private UUID id;
    private String name;
    private String description;
    private String image;
    private UUID parentId;
    private SizeGroupDto sizeGroup;
    private List<CategoryDto> subcategories;
}

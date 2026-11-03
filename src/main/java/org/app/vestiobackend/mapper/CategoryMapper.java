package org.app.vestiobackend.mapper;

import org.app.vestiobackend.dto.CategoryDto;
import org.app.vestiobackend.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {SizeGroupMapper.class})
public interface CategoryMapper {
    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "sizeGroup", target = "sizeGroup")
    CategoryDto toDto(Category category);

    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "sizeGroup", ignore = true)
    Category toEntity(CategoryDto dto);
}

package org.app.vestiobackend.mapper;

import org.app.vestiobackend.dto.SizeDto;
import org.app.vestiobackend.dto.SizeGroupDto;
import org.app.vestiobackend.entity.Size;
import org.app.vestiobackend.entity.SizeGroup;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SizeGroupMapper {
    SizeGroupDto toDto(SizeGroup entity);
    SizeDto toDto(Size entity);
}

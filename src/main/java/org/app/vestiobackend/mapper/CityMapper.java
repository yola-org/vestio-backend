package org.app.vestiobackend.mapper;

import org.app.vestiobackend.dto.CityDto;
import org.app.vestiobackend.entity.City;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CityMapper {
    CityDto toDto(City city);
    City toEntity(CityDto dto);
}

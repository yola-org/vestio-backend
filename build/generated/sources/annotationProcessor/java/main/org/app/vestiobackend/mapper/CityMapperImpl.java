package org.app.vestiobackend.mapper;

import javax.annotation.processing.Generated;
import org.app.vestiobackend.dto.CityDto;
import org.app.vestiobackend.entity.City;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-02T13:33:50-0500",
    comments = "version: 1.6.2, compiler: IncrementalProcessingEnvironment from gradle-java-compiler-worker-9.8.2.jar, environment: Java 17.0.11 (Eclipse Adoptium)"
)
@Component
public class CityMapperImpl implements CityMapper {

    @Override
    public CityDto toDto(City city) {
        if ( city == null ) {
            return null;
        }

        CityDto.CityDtoBuilder cityDto = CityDto.builder();

        cityDto.id( city.getId() );
        cityDto.name( city.getName() );

        return cityDto.build();
    }

    @Override
    public City toEntity(CityDto dto) {
        if ( dto == null ) {
            return null;
        }

        City.CityBuilder city = City.builder();

        city.id( dto.getId() );
        city.name( dto.getName() );

        return city.build();
    }
}

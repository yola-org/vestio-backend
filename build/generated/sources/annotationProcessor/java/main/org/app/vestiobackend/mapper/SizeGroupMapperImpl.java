package org.app.vestiobackend.mapper;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.app.vestiobackend.dto.SizeDto;
import org.app.vestiobackend.dto.SizeGroupDto;
import org.app.vestiobackend.entity.Size;
import org.app.vestiobackend.entity.SizeGroup;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-02T15:15:27-0500",
    comments = "version: 1.6.2, compiler: IncrementalProcessingEnvironment from gradle-java-compiler-worker-9.8.2.jar, environment: Java 17.0.11 (Eclipse Adoptium)"
)
@Component
public class SizeGroupMapperImpl implements SizeGroupMapper {

    @Override
    public SizeGroupDto toDto(SizeGroup entity) {
        if ( entity == null ) {
            return null;
        }

        SizeGroupDto.SizeGroupDtoBuilder sizeGroupDto = SizeGroupDto.builder();

        sizeGroupDto.id( entity.getId() );
        sizeGroupDto.name( entity.getName() );
        sizeGroupDto.sizes( sizeListToSizeDtoList( entity.getSizes() ) );

        return sizeGroupDto.build();
    }

    @Override
    public SizeDto toDto(Size entity) {
        if ( entity == null ) {
            return null;
        }

        SizeDto.SizeDtoBuilder sizeDto = SizeDto.builder();

        sizeDto.id( entity.getId() );
        sizeDto.name( entity.getName() );

        return sizeDto.build();
    }

    protected List<SizeDto> sizeListToSizeDtoList(List<Size> list) {
        if ( list == null ) {
            return null;
        }

        List<SizeDto> list1 = new ArrayList<SizeDto>( list.size() );
        for ( Size size : list ) {
            list1.add( toDto( size ) );
        }

        return list1;
    }
}

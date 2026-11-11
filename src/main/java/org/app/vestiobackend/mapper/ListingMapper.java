package org.app.vestiobackend.mapper;

import org.app.vestiobackend.dto.ListingDto;
import org.app.vestiobackend.entity.City;
import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.Size;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ListingMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "cityIds", ignore = true)
    @Mapping(target = "sizeIds", ignore = true)
    ListingDto toDto(Listing listing);

    @AfterMapping
    default void mapCollections(Listing listing, @MappingTarget ListingDto dto) {
        if (listing.getCities() != null) {
            dto.setCityIds(listing.getCities().stream().map(City::getId).toList());
        }
        if (listing.getSizes() != null) {
            dto.setSizeIds(listing.getSizes().stream().map(Size::getId).toList());
        }
    }

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "cities", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "sizes", ignore = true)
    @Mapping(target = "assetKeys", ignore = true)
    Listing toEntity(ListingDto dto);

    @BeanMapping(ignoreByDefault = true, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(target = "title")
    @Mapping(target = "description")
    @Mapping(target = "price")
    @Mapping(target = "gender")
    @Mapping(target = "brandId")
    @Mapping(target = "colors")
    @Mapping(target = "contactTypes")
    @Mapping(target = "condition")
    @Mapping(target = "quantity")
    void updateListingFromDto(ListingDto dto, @MappingTarget Listing listing);
}

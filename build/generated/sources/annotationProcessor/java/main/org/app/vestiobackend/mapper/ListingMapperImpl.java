package org.app.vestiobackend.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.app.vestiobackend.dto.ListingDto;
import org.app.vestiobackend.entity.Category;
import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.entity.enums.ContactType;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-11-02T21:39:36-0500",
    comments = "version: 1.6.2, compiler: IncrementalProcessingEnvironment from gradle-java-compiler-worker-9.8.2.jar, environment: Java 17.0.11 (Eclipse Adoptium)"
)
@Component
public class ListingMapperImpl implements ListingMapper {

    @Override
    public ListingDto toDto(Listing listing) {
        if ( listing == null ) {
            return null;
        }

        ListingDto.ListingDtoBuilder listingDto = ListingDto.builder();

        listingDto.categoryId( listingCategoryId( listing ) );
        listingDto.categoryName( listingCategoryName( listing ) );
        listingDto.userId( listingUserId( listing ) );
        listingDto.id( listing.getId() );
        listingDto.title( listing.getTitle() );
        listingDto.description( listing.getDescription() );
        listingDto.price( listing.getPrice() );
        List<String> list = listing.getColors();
        if ( list != null ) {
            listingDto.colors( new ArrayList<String>( list ) );
        }
        List<ContactType> list1 = listing.getContactTypes();
        if ( list1 != null ) {
            listingDto.contactTypes( new ArrayList<ContactType>( list1 ) );
        }
        listingDto.condition( listing.getCondition() );
        listingDto.status( listing.getStatus() );
        listingDto.quantity( listing.getQuantity() );

        ListingDto listingDtoResult = listingDto.build();

        mapCollections( listing, listingDtoResult );

        return listingDtoResult;
    }

    @Override
    public Listing toEntity(ListingDto dto) {
        if ( dto == null ) {
            return null;
        }

        Listing.ListingBuilder listing = Listing.builder();

        listing.id( dto.getId() );
        listing.title( dto.getTitle() );
        listing.description( dto.getDescription() );
        listing.price( dto.getPrice() );
        List<String> list = dto.getColors();
        if ( list != null ) {
            listing.colors( new ArrayList<String>( list ) );
        }
        List<ContactType> list1 = dto.getContactTypes();
        if ( list1 != null ) {
            listing.contactTypes( new ArrayList<ContactType>( list1 ) );
        }
        listing.condition( dto.getCondition() );
        listing.status( dto.getStatus() );
        listing.quantity( dto.getQuantity() );

        return listing.build();
    }

    private UUID listingCategoryId(Listing listing) {
        Category category = listing.getCategory();
        if ( category == null ) {
            return null;
        }
        return category.getId();
    }

    private String listingCategoryName(Listing listing) {
        Category category = listing.getCategory();
        if ( category == null ) {
            return null;
        }
        return category.getName();
    }

    private UUID listingUserId(Listing listing) {
        User user = listing.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getId();
    }
}

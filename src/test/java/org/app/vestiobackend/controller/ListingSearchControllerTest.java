package org.app.vestiobackend.controller;

import org.app.vestiobackend.entity.Category;
import org.app.vestiobackend.entity.City;
import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.Size;
import com.vestio.common.entity.enums.Condition;
import com.vestio.common.entity.enums.ContactType;
import com.vestio.common.entity.enums.ListingStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ListingSearchControllerTest extends AbstractControllerTest {

    @ParameterizedTest
    @CsvSource({"colors, false", "colors, true", "cityIds, false", "cityIds, true", "sizeIds, false", "sizeIds, true", "conditions, false", "conditions, true",
            "contactTypes, false", "contactTypes, true"})
    void matchesAnySelectedValueWithoutDuplicateResults(String filter, boolean commaSeparated) throws Exception {
        Size otherSize = sizeRepository.save(Size.builder().id(UUID.randomUUID())
                .name("L").sizeGroup(testSizeGroup).build());
        City otherCity = cityRepository.save(City.builder().id(UUID.randomUUID()).name("Rabat").build());
        Listing first = matchingListing();
        first.setColors(List.of("Blue", "BLUE", "Red"));
        first.setCities(List.of(testCity, otherCity));
        first.setSizes(List.of(testSize, otherSize));
        listingRepository.save(first);
        Listing second = matchingListing();
        second.setColors(List.of("Red"));
        second.setCities(List.of(otherCity));
        second.setSizes(List.of(otherSize));
        second.setCondition(Condition.GOOD);
        second.setContactTypes(List.of(ContactType.PARCEL));
        listingRepository.save(second);
        Listing excluded = matchingListing();
        excluded.setColors(List.of("Black"));
        excluded.setCities(List.of());
        excluded.setSizes(List.of());
        excluded.setCondition(Condition.NEW_WITH_TAGS);
        excluded.setContactTypes(List.of(ContactType.MEETUP));
        listingRepository.save(excluded);

        String[] values = switch (filter) {
            case "colors" -> new String[]{"  bLuE  ", " RED "};
            case "cityIds" -> new String[]{testCity.getId().toString(), otherCity.getId().toString()};
            case "sizeIds" -> new String[]{testSize.getId().toString(), otherSize.getId().toString()};
            case "conditions" -> new String[]{"VERY_GOOD", "GOOD"};
            case "contactTypes" -> new String[]{"DELIVERY", "PARCEL"};
            default -> throw new IllegalArgumentException(filter);
        };
        String[] parameters = commaSeparated ? new String[]{String.join(",", values)} : values;
        MockHttpServletRequestBuilder request = get("/api/listings/search").param(filter, parameters);
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", containsInAnyOrder(first.getId().toString(), second.getId().toString())));
        mockMvc.perform(get("/api/listings/search").param(filter, parameters).param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(second.getId().toString()));
        mockMvc.perform(get("/api/listings/search").param(filter, parameters)
                        .param("size", "1").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(first.getId().toString()));
        mockMvc.perform(get("/api/listings/search").param(filter, parameters)
                        .param("size", "1").param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void combinesMultipleSelections() throws Exception {
        Listing match = listingRepository.save(matchingListing());
        Listing wrongColor = matchingListing();
        wrongColor.setColors(List.of("Black"));
        listingRepository.save(wrongColor);
        Listing wrongCity = matchingListing();
        wrongCity.setCities(List.of());
        listingRepository.save(wrongCity);
        Listing wrongSize = matchingListing();
        wrongSize.setSizes(List.of());
        listingRepository.save(wrongSize);
        Listing wrongCondition = matchingListing();
        wrongCondition.setCondition(Condition.NEW_WITH_TAGS);
        listingRepository.save(wrongCondition);
        Listing wrongContact = matchingListing();
        wrongContact.setContactTypes(List.of(ContactType.MEETUP));
        listingRepository.save(wrongContact);

        mockMvc.perform(get("/api/listings/search")
                        .param("colors", "Blue", "Red")
                        .param("cityIds", UUID.randomUUID().toString(), testCity.getId().toString())
                        .param("sizeIds", UUID.randomUUID().toString(), testSize.getId().toString())
                        .param("conditions", "GOOD", "VERY_GOOD")
                        .param("contactTypes", "DELIVERY", "PARCEL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(match.getId().toString()));
    }

    @Test
    void emptySelectionsDoNotRestrictResults() throws Exception {
        listingRepository.save(matchingListing());
        mockMvc.perform(get("/api/listings/search")
                        .param("colors", "").param("cityIds", "").param("sizeIds", "").param("conditions", "").param("contactTypes", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @ParameterizedTest
    @CsvSource({
            "query, '  jAcKeT  '",
            "categoryId, fixture",
            "brandId, fixture",
            "cityIds, fixture",
            "sizeIds, fixture",
            "gender, '  WOMEN  '",
            "colors, '  bLuE  '",
            "conditions, VERY_GOOD",
            "contactTypes, DELIVERY",
            "minPrice, 50",
            "maxPrice, 50"
    })
    void filtersIndividually(String name, String value) throws Exception {
        Listing match = listingRepository.save(matchingListing());
        Listing distractor = createTestListing("Unrelated listing", testUser, testCity, testCategory);
        distractor.setCategory(categoryRepository.save(Category.builder()
                .id(UUID.randomUUID()).name("Accessories").build()));
        distractor.setCities(List.of(cityRepository.save(City.builder()
                .id(UUID.randomUUID()).name("Rabat").build())));
        distractor.setPrice(new BigDecimal(name.equals("maxPrice") ? "75" : "25"));
        listingRepository.save(distractor);

        String parameter = value.equals("fixture") ? filtersFor(match).get(name) : value;
        mockMvc.perform(get("/api/listings/search").param(name, parameter))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(match.getId().toString()));
    }

    @Test
    void categoryBrowsingIncludesDescendantsWhileExactSearchDoesNot() throws Exception {
        Category child = categoryRepository.save(Category.builder()
                .id(UUID.randomUUID()).name("Jackets").parent(testCategory).build());
        Category grandchild = categoryRepository.save(Category.builder()
                .id(UUID.randomUUID()).name("Denim").parent(child).build());
        Category unrelated = categoryRepository.save(Category.builder()
                .id(UUID.randomUUID()).name("Accessories").build());

        Listing parentListing = listingRepository.save(matchingListing());
        Listing grandchildListing = matchingListing();
        grandchildListing.setCategory(grandchild);
        listingRepository.save(grandchildListing);
        Listing hidden = matchingListing();
        hidden.setCategory(child);
        hidden.setStatus(ListingStatus.HIDDEN);
        listingRepository.save(hidden);
        Listing unrelatedListing = matchingListing();
        unrelatedListing.setCategory(unrelated);
        listingRepository.save(unrelatedListing);

        mockMvc.perform(get("/api/listings/search")
                        .param("categoryId", testCategory.getId().toString())
                        .param("includeSubcategories", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
        mockMvc.perform(get("/api/listings/search")
                        .param("categoryId", child.getId().toString())
                        .param("includeSubcategories", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(grandchildListing.getId().toString()));
        mockMvc.perform(get("/api/listings/search")
                        .param("categoryId", testCategory.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(parentListing.getId().toString()));
    }

    @Test
    void combinesEveryFilterAndExcludesListingsThatFailAnyOne() throws Exception {
        Listing match = listingRepository.save(matchingListing());
        Map<String, String> filters = filtersFor(match);
        for (String name : filters.keySet()) {
            Listing distractor = matchingListing();
            switch (name) {
                case "query" -> distractor.setTitle("Coat");
                case "categoryId" -> distractor.setCategory(categoryRepository.save(Category.builder()
                        .id(UUID.randomUUID()).name("Accessories").build()));
                case "brandId" -> distractor.setBrandId(null);
                case "cityIds" -> distractor.setCities(List.of());
                case "sizeIds" -> distractor.setSizes(List.of());
                case "gender" -> distractor.setGender("Men");
                case "colors" -> distractor.setColors(List.of("Black"));
                case "conditions" -> distractor.setCondition(Condition.GOOD);
                case "contactTypes" -> distractor.setContactTypes(List.of(ContactType.MEETUP));
                case "minPrice" -> distractor.setPrice(new BigDecimal("49.99"));
                case "maxPrice" -> distractor.setPrice(new BigDecimal("50.01"));
                default -> throw new IllegalArgumentException(name);
            }
            listingRepository.save(distractor);
        }
        Listing hidden = matchingListing();
        hidden.setStatus(ListingStatus.HIDDEN);
        listingRepository.save(hidden);

        MockHttpServletRequestBuilder request = get("/api/listings/search").param("size", "1");
        filters.forEach(request::param);
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(match.getId().toString()));
        mockMvc.perform(request.param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void searchesDescriptionsCaseInsensitively() throws Exception {
        Listing match = matchingListing();
        match.setTitle("Coat");
        match.setDescription("Contains 50% WOOL");
        listingRepository.save(match);
        Listing distractor = matchingListing();
        distractor.setDescription("Contains 50% cotton");
        listingRepository.save(distractor);

        mockMvc.perform(get("/api/listings/search").param("query", "wool"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(match.getId().toString()));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "SPRING_DATASOURCE_URL", matches = "jdbc:postgresql:.*")
    void matchesTermsAcrossFieldsAndRefreshesTheSearchVectorOnUpdate() throws Exception {
        Listing match = matchingListing();
        match.setDescription("Lined with wool");
        listingRepository.save(match);
        Listing partialMatch = matchingListing();
        partialMatch.setDescription("Lined with cotton");
        listingRepository.save(partialMatch);

        mockMvc.perform(get("/api/listings/search").param("query", "WOOL jacket").param("colors", "blue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(match.getId().toString()));

        match.setDescription(null);
        listingRepository.save(match);
        mockMvc.perform(get("/api/listings/search").param("query", "wool jacket"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "SPRING_DATASOURCE_URL", matches = "jdbc:postgresql:.*")
    void ranksTitleAndCatalogMatchesAcrossCitiesAndWithinOneCity() throws Exception {
        City otherCity = cityRepository.save(City.builder().id(UUID.randomUUID()).name("Rabat").build());
        Category tShirts = categoryRepository.save(Category.builder().id(UUID.randomUUID()).name("T-shirts").build());

        Listing catalogMatch = matchingListing();
        catalogMatch.setTitle("White T-shirt");
        catalogMatch.setCategory(tShirts);
        catalogMatch.setColors(List.of("White", "WHITE"));
        catalogMatch.setCities(List.of(testCity, otherCity));
        listingRepository.save(catalogMatch);

        Listing titleMatch = matchingListing();
        titleMatch.setTitle("White tee");
        listingRepository.save(titleMatch);

        Listing descriptionMatch = matchingListing();
        descriptionMatch.setDescription("White tshirt");
        descriptionMatch.setCities(List.of(otherCity));
        listingRepository.save(descriptionMatch);

        mockMvc.perform(get("/api/listings/search").param("query", "white tshirt"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].id").value(catalogMatch.getId().toString()))
                .andExpect(jsonPath("$[1].id").value(titleMatch.getId().toString()))
                .andExpect(jsonPath("$[2].id").value(descriptionMatch.getId().toString()));

        mockMvc.perform(get("/api/listings/search").param("query", "white tee")
                        .param("cityIds", otherCity.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(catalogMatch.getId().toString()))
                .andExpect(jsonPath("$[1].id").value(descriptionMatch.getId().toString()));
    }

    @Test
    void paginatesAvailableListingsNewestFirstWithoutFilters() throws Exception {
        Listing older = listingRepository.save(matchingListing());
        Listing newer = listingRepository.save(matchingListing());
        for (ListingStatus state : ListingStatus.values()) {
            if (state != ListingStatus.AVAILABLE) {
                Listing unavailable = matchingListing();
                unavailable.setStatus(state);
                listingRepository.save(unavailable);
            }
        }

        mockMvc.perform(get("/api/listings/search").param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(newer.getId().toString()));
        mockMvc.perform(get("/api/listings/search").param("size", "1").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(older.getId().toString()));
        mockMvc.perform(get("/api/listings/search").param("query", "  "))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @ParameterizedTest
    @CsvSource({"colors, 'Blue,   '", "categoryId, invalid", "conditions, invalid", "contactTypes, invalid",
            "cityIds, invalid", "sizeIds, invalid", "conditions, 'GOOD,invalid'", "contactTypes, 'DELIVERY,invalid'",
            "minPrice, -1", "maxPrice, -1", "page, -1", "size, 0", "size, 101"})
    void rejectsInvalidParameters(String name, String value) throws Exception {
        mockMvc.perform(get("/api/listings/search").param(name, value))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsInvertedPriceRange() throws Exception {
        mockMvc.perform(get("/api/listings/search").param("minPrice", "51").param("maxPrice", "50"))
                .andExpect(status().isBadRequest());
    }

    private Listing matchingListing() {
        return Listing.builder()
                .id(UUID.randomUUID())
                .title("Denim Jacket")
                .price(new BigDecimal("50"))
                .quantity(1)
                .status(ListingStatus.AVAILABLE)
                .user(testUser)
                .category(testCategory)
                .brandId(testSizeGroup.getId())
                .cities(List.of(testCity))
                .sizes(List.of(testSize))
                .gender("Women")
                .colors(List.of("Blue", "BLUE"))
                .condition(Condition.VERY_GOOD)
                .contactTypes(List.of(ContactType.DELIVERY, ContactType.PARCEL))
                .build();
    }

    private Map<String, String> filtersFor(Listing listing) {
        Map<String, String> filters = new LinkedHashMap<>();
        filters.put("query", "jacket");
        filters.put("categoryId", testCategory.getId().toString());
        filters.put("brandId", listing.getBrandId().toString());
        filters.put("cityIds", testCity.getId().toString());
        filters.put("sizeIds", testSize.getId().toString());
        filters.put("gender", "women");
        filters.put("colors", "blue");
        filters.put("conditions", "VERY_GOOD");
        filters.put("contactTypes", "DELIVERY");
        filters.put("minPrice", "50");
        filters.put("maxPrice", "50");
        return filters;
    }
}

package org.app.vestiobackend.controller;

import org.app.vestiobackend.entity.Category;
import org.app.vestiobackend.entity.City;
import org.app.vestiobackend.entity.Listing;
import org.app.vestiobackend.entity.enums.Condition;
import org.app.vestiobackend.entity.enums.ContactType;
import org.app.vestiobackend.entity.enums.ListingStatus;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ListingSearchControllerTest extends AbstractControllerTest {

    @ParameterizedTest
    @CsvSource({
            "query, '  jAcKeT  '",
            "categoryId, fixture",
            "brandId, fixture",
            "cityId, fixture",
            "sizeId, fixture",
            "gender, '  WOMEN  '",
            "color, '  bLuE  '",
            "condition, VERY_GOOD",
            "contactType, DELIVERY",
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
                case "cityId" -> distractor.setCities(List.of());
                case "sizeId" -> distractor.setSizes(List.of());
                case "gender" -> distractor.setGender("Men");
                case "color" -> distractor.setColors(List.of("Black"));
                case "condition" -> distractor.setCondition(Condition.GOOD);
                case "contactType" -> distractor.setContactTypes(List.of(ContactType.MEETUP));
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

        mockMvc.perform(get("/api/listings/search").param("query", "WOOL jacket").param("color", "blue"))
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
                        .param("cityId", otherCity.getId().toString()))
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
    @CsvSource({"categoryId, invalid", "condition, invalid", "contactType, invalid",
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
        filters.put("cityId", testCity.getId().toString());
        filters.put("sizeId", testSize.getId().toString());
        filters.put("gender", "women");
        filters.put("color", "blue");
        filters.put("condition", "VERY_GOOD");
        filters.put("contactType", "DELIVERY");
        filters.put("minPrice", "50");
        filters.put("maxPrice", "50");
        return filters;
    }
}

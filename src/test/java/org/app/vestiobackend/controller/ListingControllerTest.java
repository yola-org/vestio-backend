package org.app.vestiobackend.controller;

import org.app.vestiobackend.dto.ListingDto;
import org.app.vestiobackend.entity.Category;
import org.app.vestiobackend.entity.City;
import org.app.vestiobackend.entity.User;
import org.app.vestiobackend.entity.enums.Condition;
import org.app.vestiobackend.entity.enums.ContactType;
import org.app.vestiobackend.entity.enums.ListingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ListingControllerTest extends AbstractControllerTest {

    @Test
    public void createListingShouldReturnOk() throws Exception {
        ListingDto dto = ListingDto.builder()
                .title("Nice Jeans")
                .price(new BigDecimal("25.50"))
                .quantity(1)
                .contactTypes(List.of(ContactType.PARCEL))
                .condition(Condition.VERY_GOOD)
                .cityIds(List.of(testCity.getId()))
                .categoryId(testCategory.getId())
                .sizeIds(List.of(testSize.getId()))
                .build();

        mockMvc.perform(post("/api/listings")
                        .with(authenticatedUser(TEST_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Nice Jeans"))
                .andExpect(jsonPath("$.price").value(25.50))
                .andExpect(jsonPath("$.sizeIds[0]").value(testSize.getId().toString()))
                .andExpect(jsonPath("$.sizeNames[0]").value(testSize.getName()));
    }

    @Test
    public void getAllListingsShouldReturnList() throws Exception {
        ListingDto dto = ListingDto.builder()
                .title("Nice Jeans")
                .price(new BigDecimal("25.50"))
                .quantity(1)
                .contactTypes(List.of(ContactType.PARCEL))
                .condition(Condition.VERY_GOOD)
                .cityIds(List.of(testCity.getId()))
                .categoryId(testCategory.getId())
                .build();
        
        mockMvc.perform(post("/api/listings")
                .with(authenticatedUser(TEST_EMAIL))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/listings")
                        .with(authenticatedUser(TEST_EMAIL)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("Nice Jeans"));
    }

    @Test
    public void deleteListingShouldReturnNoContent() throws Exception {
        ListingDto dto = ListingDto.builder()
                .title("Delete me")
                .price(new BigDecimal("10.00"))
                .quantity(1)
                .contactTypes(List.of(ContactType.MEETUP))
                .condition(Condition.GOOD)
                .cityIds(List.of(testCity.getId()))
                .categoryId(testCategory.getId())
                .build();
        
        MvcResult result = mockMvc.perform(post("/api/listings")
                        .with(authenticatedUser(TEST_EMAIL))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();
        
        String response = result.getResponse().getContentAsString();
        UUID id = UUID.fromString(objectMapper.readTree(response).get("id").asText());

        mockMvc.perform(delete("/api/listings/" + id)
                        .with(authenticatedUser(TEST_EMAIL)))
                .andExpect(status().isNoContent());
    }
}

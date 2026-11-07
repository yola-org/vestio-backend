package org.app.vestiobackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.app.vestiobackend.entity.enums.Condition;
import org.app.vestiobackend.entity.enums.ContactType;
import org.app.vestiobackend.entity.enums.ListingStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingDto {
    private UUID id;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    private String gender;

    private UUID brandId;

    private List<String> colors;

    private List<ContactType> contactTypes;

    @NotNull(message = "Condition is required")
    private Condition condition;

    private ListingStatus status;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    private Integer quantity;

    @NotEmpty(message = "At least one city is required")
    private List<UUID> cityIds;

    @NotNull(message = "Category ID is required")
    private UUID categoryId;

    private List<String> cityNames;
    private String categoryName;
    private List<UUID> sizeIds;
    private List<String> sizeNames;
    private UUID userId;
}

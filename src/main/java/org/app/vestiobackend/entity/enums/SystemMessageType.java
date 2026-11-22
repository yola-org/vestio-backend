package org.app.vestiobackend.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.math.BigDecimal;
import java.util.Map;

public enum SystemMessageType {
    OFFER_ACCEPTED("offer-accepted");

    private final String value;

    SystemMessageType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public void validateParams(Map<String, Object> params) {
        switch (this) {
            case OFFER_ACCEPTED -> {
                if (!(params.get("amount") instanceof Number amount)) {
                    throw new IllegalArgumentException("offer-accepted requires a positive numeric amount");
                }
                try {
                    if (new BigDecimal(amount.toString()).signum() <= 0) {
                        throw new IllegalArgumentException("offer-accepted requires a positive numeric amount");
                    }
                } catch (NumberFormatException exception) {
                    throw new IllegalArgumentException("offer-accepted requires a finite numeric amount", exception);
                }
                if (!(params.get("currency") instanceof String currency) || !currency.matches("[A-Z]{3}")) {
                    throw new IllegalArgumentException("offer-accepted requires a three-letter uppercase currency code");
                }
                for (String name : new String[]{"ownerName", "buyerName", "listingTitle"}) {
                    if (!(params.get(name) instanceof String)) {
                        throw new IllegalArgumentException("offer-accepted requires " + name);
                    }
                }
            }
        }
    }
}

package com.mini_ecommerce.orders_service.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.LinkedHashMap;
import java.util.Map;

public class OrderUpdateRequest {

    @NotNull
    @Positive
    private Integer quantity;

    @NotBlank
    private String status;

    private final Map<String, Object> unknownFields = new LinkedHashMap<>();

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonAnySetter
    public void captureUnknownField(String name, Object value) {
        unknownFields.put(name, value);
    }

    public Map<String, Object> getUnknownFields() {
        return unknownFields;
    }
}

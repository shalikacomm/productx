package com.xproduct.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateDailySaleRequest {

    @NotNull
    private Long fuelTypeId;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal litersSold;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal pricePerLiter;

    @NotNull
    private LocalDate saleDate;
}

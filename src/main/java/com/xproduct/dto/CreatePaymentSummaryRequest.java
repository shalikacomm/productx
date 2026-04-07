package com.xproduct.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreatePaymentSummaryRequest {
    private LocalDate summaryDate;
    private BigDecimal onlineAmount;
    private BigDecimal visaAmount;
    private BigDecimal amexAmount;
    private BigDecimal touchAmount;
    private BigDecimal cashAmount;
}

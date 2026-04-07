package com.xproduct.dto;

import com.xproduct.entity.DailyPaymentSummary;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PaymentSummaryResponse {
    private Long id;
    private Long branchId;
    private String branchName;
    private LocalDate summaryDate;
    private BigDecimal onlineAmount;
    private BigDecimal visaAmount;
    private BigDecimal amexAmount;
    private BigDecimal touchAmount;
    private BigDecimal cashAmount;
    private BigDecimal totalAmount;
    private String recordedByUsername;
    private LocalDateTime createdAt;

    public static PaymentSummaryResponse from(DailyPaymentSummary s) {
        PaymentSummaryResponse r = new PaymentSummaryResponse();
        r.setId(s.getId());
        r.setBranchId(s.getBranch().getId());
        r.setBranchName(s.getBranch().getName());
        r.setSummaryDate(s.getSummaryDate());
        r.setOnlineAmount(s.getOnlineAmount());
        r.setVisaAmount(s.getVisaAmount());
        r.setAmexAmount(s.getAmexAmount());
        r.setTouchAmount(s.getTouchAmount());
        r.setCashAmount(s.getCashAmount());
        r.setTotalAmount(s.getTotalAmount());
        r.setRecordedByUsername(s.getRecordedBy().getUsername());
        r.setCreatedAt(s.getCreatedAt());
        return r;
    }
}

package com.xproduct.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ReportResponse {

    private String periodLabel;
    private String branchName;        // "All Branches" or specific branch name
    private BigDecimal totalRevenue;
    private BigDecimal totalPurchaseCost;
    private BigDecimal grossProfit;
    private List<BranchSummary> branches;   // populated only for all-branch report
    private List<ItemLine> items;           // item-wise breakdown

    @Data
    public static class ItemLine {
        private String itemName;
        private String unit;
        private BigDecimal qtyPurchased;
        private BigDecimal avgPurchasePrice;
        private BigDecimal purchaseCost;
        private BigDecimal qtySold;
        private BigDecimal avgSellingPrice;
        private BigDecimal revenue;
        private BigDecimal profit;
    }

    @Data
    public static class BranchSummary {
        private String branchName;
        private BigDecimal revenue;
        private BigDecimal purchaseCost;
        private BigDecimal profit;
    }
}

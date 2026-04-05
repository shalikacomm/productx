package com.xproduct.service;

import com.xproduct.dto.ReportResponse;
import com.xproduct.entity.DailySale;
import com.xproduct.entity.Grn;
import com.xproduct.repository.DailySaleRepository;
import com.xproduct.repository.GrnRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final DailySaleRepository dailySaleRepository;
    private final GrnRepository grnRepository;

    public ReportResponse generate(LocalDate from, LocalDate to, Long branchId, String periodLabel) {

        List<DailySale> sales = branchId != null
                ? dailySaleRepository.findByBranchIdAndSaleDateBetweenOrderBySaleDateDesc(branchId, from, to)
                : dailySaleRepository.findBySaleDateBetweenOrderBySaleDateDesc(from, to);

        List<Grn> grns = branchId != null
                ? grnRepository.findByBranchIdAndReceivedDateBetweenOrderByReceivedDateDesc(branchId, from, to)
                : grnRepository.findByReceivedDateBetweenOrderByReceivedDateDesc(from, to);

        ReportResponse report = new ReportResponse();
        report.setPeriodLabel(periodLabel);
        report.setBranchName(branchId != null
                ? (sales.isEmpty() ? (grns.isEmpty() ? "Selected Branch" : grns.get(0).getBranch().getName())
                                   : sales.get(0).getBranch().getName())
                : "All Branches");

        // Item-wise aggregation
        report.setItems(buildItemLines(sales, grns));

        // Totals
        BigDecimal totalRevenue = sales.stream()
                .map(DailySale::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCost = grns.stream()
                .map(Grn::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        report.setTotalRevenue(totalRevenue);
        report.setTotalPurchaseCost(totalCost);
        report.setGrossProfit(totalRevenue.subtract(totalCost));

        // Branch-wise summary (only for all-branch report)
        if (branchId == null) {
            report.setBranches(buildBranchSummaries(sales, grns));
        }

        return report;
    }

    private List<ReportResponse.ItemLine> buildItemLines(List<DailySale> sales, List<Grn> grns) {
        Map<Long, ReportResponse.ItemLine> map = new LinkedHashMap<>();

        for (DailySale s : sales) {
            Long id = s.getFuelType().getId();
            ReportResponse.ItemLine line = map.computeIfAbsent(id, k -> {
                ReportResponse.ItemLine l = new ReportResponse.ItemLine();
                l.setItemName(s.getFuelType().getName());
                l.setUnit(s.getFuelType().getUnit());
                l.setQtySold(BigDecimal.ZERO);
                l.setRevenue(BigDecimal.ZERO);
                l.setQtyPurchased(BigDecimal.ZERO);
                l.setPurchaseCost(BigDecimal.ZERO);
                return l;
            });
            line.setQtySold(line.getQtySold().add(s.getLitersSold()));
            line.setRevenue(line.getRevenue().add(s.getTotalAmount()));
        }

        for (Grn g : grns) {
            Long id = g.getFuelType().getId();
            ReportResponse.ItemLine line = map.computeIfAbsent(id, k -> {
                ReportResponse.ItemLine l = new ReportResponse.ItemLine();
                l.setItemName(g.getFuelType().getName());
                l.setUnit(g.getFuelType().getUnit());
                l.setQtySold(BigDecimal.ZERO);
                l.setRevenue(BigDecimal.ZERO);
                l.setQtyPurchased(BigDecimal.ZERO);
                l.setPurchaseCost(BigDecimal.ZERO);
                return l;
            });
            line.setQtyPurchased(line.getQtyPurchased().add(g.getQuantityLiters()));
            line.setPurchaseCost(line.getPurchaseCost().add(g.getTotalAmount()));
        }

        map.values().forEach(l -> {
            l.setProfit(l.getRevenue().subtract(l.getPurchaseCost()));
            // Average purchase price per unit
            if (l.getQtyPurchased().compareTo(BigDecimal.ZERO) > 0) {
                l.setAvgPurchasePrice(l.getPurchaseCost().divide(l.getQtyPurchased(), 2, java.math.RoundingMode.HALF_UP));
            } else {
                l.setAvgPurchasePrice(BigDecimal.ZERO);
            }
            // Average selling price per unit
            if (l.getQtySold().compareTo(BigDecimal.ZERO) > 0) {
                l.setAvgSellingPrice(l.getRevenue().divide(l.getQtySold(), 2, java.math.RoundingMode.HALF_UP));
            } else {
                l.setAvgSellingPrice(BigDecimal.ZERO);
            }
        });
        return new ArrayList<>(map.values());
    }

    private List<ReportResponse.BranchSummary> buildBranchSummaries(List<DailySale> sales, List<Grn> grns) {
        Map<Long, ReportResponse.BranchSummary> map = new LinkedHashMap<>();

        for (DailySale s : sales) {
            Long id = s.getBranch().getId();
            ReportResponse.BranchSummary b = map.computeIfAbsent(id, k -> {
                ReportResponse.BranchSummary bs = new ReportResponse.BranchSummary();
                bs.setBranchName(s.getBranch().getName());
                bs.setRevenue(BigDecimal.ZERO);
                bs.setPurchaseCost(BigDecimal.ZERO);
                return bs;
            });
            b.setRevenue(b.getRevenue().add(s.getTotalAmount()));
        }

        for (Grn g : grns) {
            Long id = g.getBranch().getId();
            ReportResponse.BranchSummary b = map.computeIfAbsent(id, k -> {
                ReportResponse.BranchSummary bs = new ReportResponse.BranchSummary();
                bs.setBranchName(g.getBranch().getName());
                bs.setRevenue(BigDecimal.ZERO);
                bs.setPurchaseCost(BigDecimal.ZERO);
                return bs;
            });
            b.setPurchaseCost(b.getPurchaseCost().add(g.getTotalAmount()));
        }

        map.values().forEach(b -> b.setProfit(b.getRevenue().subtract(b.getPurchaseCost())));
        return new ArrayList<>(map.values());
    }
}

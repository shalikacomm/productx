package com.xproduct.service;

import com.xproduct.dto.CreateDailySaleRequest;
import com.xproduct.dto.DailySaleResponse;
import com.xproduct.entity.*;
import com.xproduct.enums.Role;
import com.xproduct.exception.ApiException;
import com.xproduct.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailySaleService {

    private final DailySaleRepository dailySaleRepository;
    private final StockRepository stockRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final UserRepository userRepository;
    private final BranchRepository branchRepository;

    @Transactional
    public DailySaleResponse recordSale(CreateDailySaleRequest request, String username) {
        User recorder = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        Branch branch;
        if (recorder.getRole() == Role.CLERK) {
            if (request.getBranchId() == null) {
                throw new ApiException("Branch is required for clerk sales entry", HttpStatus.BAD_REQUEST);
            }
            branch = branchRepository.findById(request.getBranchId())
                    .orElseThrow(() -> new ApiException("Branch not found", HttpStatus.NOT_FOUND));
        } else {
            // MANAGER — auto-resolve from their profile
            if (recorder.getBranch() == null) {
                throw new ApiException("Manager is not assigned to a branch", HttpStatus.BAD_REQUEST);
            }
            branch = recorder.getBranch();
        }

        FuelType fuelType = fuelTypeRepository.findById(request.getFuelTypeId())
                .orElseThrow(() -> new ApiException("Fuel type not found", HttpStatus.NOT_FOUND));

        // Check sufficient stock
        Stock stock = stockRepository.findByBranchIdAndFuelTypeId(branch.getId(), fuelType.getId())
                .orElseThrow(() -> new ApiException(
                        "No stock found for " + fuelType.getName() + " at " + branch.getName(), HttpStatus.BAD_REQUEST));

        if (stock.getQuantityLiters().compareTo(request.getLitersSold()) < 0) {
            throw new ApiException(
                    "Insufficient stock. Available: " + stock.getQuantityLiters() + " L, Requested: " + request.getLitersSold() + " L",
                    HttpStatus.BAD_REQUEST);
        }

        BigDecimal total = request.getLitersSold().multiply(request.getPricePerLiter());

        DailySale sale = DailySale.builder()
                .saleDate(request.getSaleDate())
                .branch(branch)
                .fuelType(fuelType)
                .litersSold(request.getLitersSold())
                .pricePerLiter(request.getPricePerLiter())
                .totalAmount(total)
                .recordedBy(recorder)
                .build();

        dailySaleRepository.save(sale);

        // Deduct stock
        stock.setQuantityLiters(stock.getQuantityLiters().subtract(request.getLitersSold()));
        stockRepository.save(stock);

        return DailySaleResponse.from(sale);
    }

    public List<DailySaleResponse> getSalesByBranch(Long branchId) {
        return dailySaleRepository.findByBranchIdOrderBySaleDateDesc(branchId)
                .stream().map(DailySaleResponse::from).collect(Collectors.toList());
    }

    public List<DailySaleResponse> getAllSales() {
        return dailySaleRepository.findAllByOrderBySaleDateDesc()
                .stream().map(DailySaleResponse::from).collect(Collectors.toList());
    }
}

package com.xproduct.service;

import com.xproduct.dto.CreateGrnRequest;
import com.xproduct.dto.GrnResponse;
import com.xproduct.entity.*;
import com.xproduct.enums.Role;
import com.xproduct.exception.ApiException;
import com.xproduct.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GrnService {

    private final GrnRepository grnRepository;
    private final BranchRepository branchRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;

    @Transactional
    public GrnResponse createGrn(CreateGrnRequest request, String username) {
        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        Branch branch = branchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ApiException("Branch not found", HttpStatus.NOT_FOUND));

        // Manager can only add GRN for their own branch
        if (creator.getRole() == Role.MANAGER) {
            if (creator.getBranch() == null || !creator.getBranch().getId().equals(branch.getId())) {
                throw new ApiException("Managers can only add GRN for their own branch", HttpStatus.FORBIDDEN);
            }
        }

        FuelType fuelType = fuelTypeRepository.findById(request.getFuelTypeId())
                .orElseThrow(() -> new ApiException("Fuel type not found", HttpStatus.NOT_FOUND));

        BigDecimal total = request.getQuantityLiters().multiply(request.getPricePerLiter());

        Grn grn = Grn.builder()
                .grnNumber(generateGrnNumber(branch))
                .branch(branch)
                .fuelType(fuelType)
                .quantityLiters(request.getQuantityLiters())
                .pricePerLiter(request.getPricePerLiter())
                .totalAmount(total)
                .supplier(request.getSupplier())
                .receivedDate(request.getReceivedDate())
                .notes(request.getNotes())
                .createdBy(creator)
                .build();

        grnRepository.save(grn);

        // Update stock
        Stock stock = stockRepository.findByBranchIdAndFuelTypeId(branch.getId(), fuelType.getId())
                .orElse(Stock.builder().branch(branch).fuelType(fuelType).quantityLiters(BigDecimal.ZERO).build());
        stock.setQuantityLiters(stock.getQuantityLiters().add(request.getQuantityLiters()));
        stockRepository.save(stock);

        return GrnResponse.from(grn);
    }

    public List<GrnResponse> getAllGrns() {
        return grnRepository.findAllByOrderByCreatedAtDesc()
                .stream().map(GrnResponse::from).collect(Collectors.toList());
    }

    public List<GrnResponse> getGrnsByBranch(Long branchId) {
        return grnRepository.findByBranchIdOrderByCreatedAtDesc(branchId)
                .stream().map(GrnResponse::from).collect(Collectors.toList());
    }

    private String generateGrnNumber(Branch branch) {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String base = "GRN-" + branch.getCode() + "-" + datePart + "-";
        long count = grnRepository.count() + 1;
        return base + String.format("%04d", count);
    }
}

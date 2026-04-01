package com.xproduct.service;

import com.xproduct.dto.StockResponse;
import com.xproduct.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    public List<StockResponse> getAllStock() {
        return stockRepository.findAllByOrderByBranchIdAscFuelTypeIdAsc()
                .stream().map(StockResponse::from).collect(Collectors.toList());
    }

    public List<StockResponse> getStockByBranch(Long branchId) {
        return stockRepository.findByBranchId(branchId)
                .stream().map(StockResponse::from).collect(Collectors.toList());
    }
}

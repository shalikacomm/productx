package com.xproduct.service;

import com.xproduct.dto.FuelTypeResponse;
import com.xproduct.repository.FuelTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FuelTypeService {

    private final FuelTypeRepository fuelTypeRepository;

    public List<FuelTypeResponse> getAllActive() {
        return fuelTypeRepository.findByActiveTrue()
                .stream().map(FuelTypeResponse::from).collect(Collectors.toList());
    }
}

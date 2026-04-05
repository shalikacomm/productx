package com.xproduct.service;

import com.xproduct.dto.CreateItemTypeRequest;
import com.xproduct.dto.FuelTypeResponse;
import com.xproduct.entity.FuelType;
import com.xproduct.exception.ApiException;
import com.xproduct.repository.FuelTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    public FuelTypeResponse create(CreateItemTypeRequest request) {
        if (fuelTypeRepository.existsByCode(request.getCode().toUpperCase())) {
            throw new ApiException("Item type code already exists: " + request.getCode(), HttpStatus.CONFLICT);
        }
        FuelType ft = FuelType.builder()
                .name(request.getName().trim())
                .code(request.getCode().trim().toUpperCase())
                .unit(request.getUnit().trim())
                .active(true)
                .build();
        return FuelTypeResponse.from(fuelTypeRepository.save(ft));
    }

    public void deactivate(Long id) {
        FuelType ft = fuelTypeRepository.findById(id)
                .orElseThrow(() -> new ApiException("Item type not found", HttpStatus.NOT_FOUND));
        ft.setActive(false);
        fuelTypeRepository.save(ft);
    }
}

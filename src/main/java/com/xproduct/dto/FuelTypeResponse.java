package com.xproduct.dto;

import com.xproduct.entity.FuelType;
import lombok.Data;

@Data
public class FuelTypeResponse {
    private Long id;
    private String name;
    private String code;
    private String unit;

    public static FuelTypeResponse from(FuelType f) {
        FuelTypeResponse r = new FuelTypeResponse();
        r.setId(f.getId());
        r.setName(f.getName());
        r.setCode(f.getCode());
        r.setUnit(f.getUnit());
        return r;
    }
}

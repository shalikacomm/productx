package com.xproduct.dto;

import com.xproduct.entity.Branch;
import lombok.Data;

@Data
public class BranchResponse {
    private Long id;
    private String name;
    private String location;
    private String code;

    public static BranchResponse from(Branch branch) {
        BranchResponse r = new BranchResponse();
        r.setId(branch.getId());
        r.setName(branch.getName());
        r.setLocation(branch.getLocation());
        r.setCode(branch.getCode());
        return r;
    }
}

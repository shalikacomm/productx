package com.xproduct.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fuel_types")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FuelType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(unique = true, nullable = false, length = 20)
    private String code;

    @Column(nullable = false, length = 50)
    private String unit;

    @Column(nullable = false)
    private Boolean active = true;
}

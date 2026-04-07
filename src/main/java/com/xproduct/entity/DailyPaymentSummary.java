package com.xproduct.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_payment_summary")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DailyPaymentSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(nullable = false)
    private LocalDate summaryDate;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal onlineAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal visaAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amexAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal touchAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal cashAmount;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by", nullable = false)
    private User recordedBy;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}

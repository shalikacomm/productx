package com.xproduct.service;

import com.xproduct.dto.CreatePaymentSummaryRequest;
import com.xproduct.dto.PaymentSummaryResponse;
import com.xproduct.entity.DailyPaymentSummary;
import com.xproduct.entity.User;
import com.xproduct.exception.ApiException;
import com.xproduct.repository.DailyPaymentSummaryRepository;
import com.xproduct.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyPaymentSummaryService {

    private final DailyPaymentSummaryRepository repository;
    private final UserRepository userRepository;

    @Transactional
    public PaymentSummaryResponse record(CreatePaymentSummaryRequest req, String username) {
        User manager = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));

        if (manager.getBranch() == null) {
            throw new ApiException("Manager is not assigned to a branch", HttpStatus.BAD_REQUEST);
        }

        BigDecimal online = nullSafe(req.getOnlineAmount());
        BigDecimal visa   = nullSafe(req.getVisaAmount());
        BigDecimal amex   = nullSafe(req.getAmexAmount());
        BigDecimal touch  = nullSafe(req.getTouchAmount());
        BigDecimal cash   = nullSafe(req.getCashAmount());
        BigDecimal total  = online.add(visa).add(amex).add(touch).add(cash);

        DailyPaymentSummary summary = DailyPaymentSummary.builder()
                .branch(manager.getBranch())
                .summaryDate(req.getSummaryDate() != null ? req.getSummaryDate() : LocalDate.now())
                .onlineAmount(online)
                .visaAmount(visa)
                .amexAmount(amex)
                .touchAmount(touch)
                .cashAmount(cash)
                .totalAmount(total)
                .recordedBy(manager)
                .build();

        return PaymentSummaryResponse.from(repository.save(summary));
    }

    public List<PaymentSummaryResponse> getByBranch(String username) {
        User manager = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND));
        if (manager.getBranch() == null) return List.of();
        return repository.findByBranchIdOrderBySummaryDateDesc(manager.getBranch().getId())
                .stream().map(PaymentSummaryResponse::from).collect(Collectors.toList());
    }

    public List<PaymentSummaryResponse> getAll() {
        return repository.findAllByOrderBySummaryDateDesc()
                .stream().map(PaymentSummaryResponse::from).collect(Collectors.toList());
    }

    public List<PaymentSummaryResponse> getByBranchId(Long branchId) {
        return repository.findByBranchIdOrderBySummaryDateDesc(branchId)
                .stream().map(PaymentSummaryResponse::from).collect(Collectors.toList());
    }

    private BigDecimal nullSafe(BigDecimal val) {
        return val != null ? val : BigDecimal.ZERO;
    }
}

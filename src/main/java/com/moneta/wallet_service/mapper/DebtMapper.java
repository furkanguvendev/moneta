package com.moneta.wallet_service.mapper;

import com.moneta.wallet_service.dto.request.DebtRequest;
import com.moneta.wallet_service.dto.response.DebtResponse;
import com.moneta.wallet_service.entity.Debt;
import org.springframework.stereotype.Component;

@Component
public class DebtMapper {

    public Debt toEntity(DebtRequest request) {
        if (request == null) return null;

        Debt debt = new Debt();
        debt.setTitle(request.title());
        debt.setTotalAmount(request.totalAmount());
        debt.setRemainingAmount(request.totalAmount());
        debt.setDebtType(request.debtType());
        debt.setDueDate(request.dueDate());
        debt.setTotalInstallments(request.totalInstallments());
        debt.setPaidInstallments(0);
        debt.setCompleted(false);
        return debt;
    }

    public DebtResponse toResponse(Debt entity) {
        if (entity == null) return null;

        return new DebtResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getTotalAmount(),
                entity.getRemainingAmount(),
                entity.getTotalInstallments(),
                entity.getPaidInstallments(),
                entity.getMonthlyInstallment(),
                entity.getDebtType(),
                entity.getDueDate(),
                entity.isCompleted()
        );
    }
}
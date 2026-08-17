package com.moneta.wallet_service.mapper;

import com.moneta.wallet_service.dto.request.TransactionRequest;
import com.moneta.wallet_service.dto.response.TransactionResponse;
import com.moneta.wallet_service.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public Transaction toEntity(TransactionRequest request) {
        if (request == null) return null;

        Transaction transaction = new Transaction();
        transaction.setAmount(request.amount());
        transaction.setDescription(request.description());
        transaction.setTransactionType(request.transactionType());
        transaction.setTotalInstallment(request.totalInstallment() != null ? request.totalInstallment() : 1);
        return transaction;
    }

    public TransactionResponse toResponse(Transaction entity) {
        if (entity == null) return null;

        return new TransactionResponse(
                entity.getId(),
                entity.getAmount(),
                entity.getDescription(),
                entity.getWallet() != null ? entity.getWallet().getId() : null,
                entity.getWallet() != null ? entity.getWallet().getName() : null,
                entity.getCategory() != null ? entity.getCategory().getId() : null,
                entity.getCategory() != null ? entity.getCategory().getName() : null,
                entity.getCategory() != null && entity.getCategory().isMandatory(),
                entity.getTransactionType(),
                entity.getTransactionDate(),
                entity.getInstallmentGroupKey(),
                entity.getCurrentInstallment(),
                entity.getTotalInstallment()
        );
    }

}
package com.moneta.wallet_service.service.impl;

import com.moneta.wallet_service.entity.Transaction;
import com.moneta.wallet_service.entity.Wallet;
import com.moneta.wallet_service.enums.TransactionType;
import com.moneta.wallet_service.repository.TransactionRepository;
import com.moneta.wallet_service.service.TransactionService;
import com.moneta.wallet_service.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletService walletService;

    @Override
    public Transaction getTransactionById(Long transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("İşlem bulunamadı! ID: " + transactionId));
    }

    @Override
    @Transactional // Kritik: Cüzdan güncelleme ve işlem kaydı ya hep yapılır ya hiç.
    public Transaction addTransaction(Long walletId, Transaction transaction) {

        Wallet wallet = walletService.getWalletById(walletId);

        BigDecimal impact = transaction.getTransactionType() == TransactionType.INCOME
                ? transaction.getAmount()
                : transaction.getAmount().negate();

        walletService.updateBalance(walletId, impact);

        transaction.setWallet(wallet);
        return transactionRepository.save(transaction);
    }

    @Override
    public List<Transaction> getTransactions(Long walletId) {
        Wallet wallet = walletService.getWalletById(walletId);
        return wallet.getTransactions();
    }

    @Override
    @Transactional
    public void deleteTransaction(Long transactionId) {
        if (!transactionRepository.existsById(transactionId)) {
            throw new RuntimeException("Silinecek işlem bulunamadı!");
        }
        transactionRepository.deleteById(transactionId);
    }
}
package com.moneta.wallet_service.repository;

import com.moneta.wallet_service.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    List<Wallet> findAllByOwnerUsername(String username);
}

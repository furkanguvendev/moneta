package com.moneta.wallet_service.repository;

import com.moneta.wallet_service.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    @Query("SELECT w FROM Wallet w JOIN FETCH w.user WHERE w.user.id = :userId")
    List<Wallet> findAllByUserIdWithUser(@Param("userId") Long userId);

    List<Wallet> findAllByUserId(Long userId);
}
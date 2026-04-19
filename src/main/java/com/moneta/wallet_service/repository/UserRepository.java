package com.moneta.wallet_service.repository;

import com.moneta.wallet_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

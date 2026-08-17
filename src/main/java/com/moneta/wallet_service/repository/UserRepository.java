package com.moneta.wallet_service.repository;

import com.moneta.wallet_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);

    Optional<User> findByUserNameOrEmail(String username, String email);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.userName = :username OR u.email = :username")
    Optional<User> findByUserNameOrEmailWithRoles(@Param("username") String username);
}
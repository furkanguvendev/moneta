package com.moneta.wallet_service.repository;

import com.moneta.wallet_service.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c WHERE c.user IS NULL OR c.user.id = :userId")
    List<Category> findGlobalAndUserCategories(@Param("userId") Long userId);

    List<Category> findByIsMandatoryTrue();

    boolean existsByNameAndUserIdIsNull(String name);

    boolean existsByNameAndUserId(String name, Long userId);
}

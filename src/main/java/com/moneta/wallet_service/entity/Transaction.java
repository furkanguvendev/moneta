package com.moneta.wallet_service.entity;

import com.moneta.wallet_service.enums.TransactionType;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@ToString(exclude = {"wallet", "category"})
@EqualsAndHashCode(exclude = {"wallet", "category"})
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    private String description;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "investment_simulation_id")
    private Long investmentSimulationId;

    @Column(name = "installment_group_key")
    private String installmentGroupKey;

    @Column(name = "current_installment")
    private Integer currentInstallment;

    @Column(name = "total_installment")
    private Integer totalInstallment;
}
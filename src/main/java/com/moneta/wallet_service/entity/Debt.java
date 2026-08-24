package com.moneta.wallet_service.entity;

import com.moneta.wallet_service.enums.DebtType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "debts")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Debt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private BigDecimal remainingAmount;

    @Column(name = "total_installments", nullable = false)
    private Integer totalInstallments;

    @Column(name = "paid_installments", nullable = false)
    private Integer paidInstallments = 0;

    @Column(name = "monthly_installment", nullable = false)
    private BigDecimal monthlyInstallment;

    @Enumerated(EnumType.STRING)
    @Column(name = "debt_type", nullable = false)
    private DebtType debtType;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "is_completed", nullable = false)
    private boolean isCompleted = false;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "last_payment_year")
    private Integer lastPaymentYear;

    @Column(name = "last_payment_month")
    private Integer lastPaymentMonth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;
}
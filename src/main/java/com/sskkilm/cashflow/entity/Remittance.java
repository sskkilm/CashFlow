package com.sskkilm.cashflow.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
public class Remittance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "remittance_id")
    private Long id;
    private String receivingAccountNumber;
    private Integer amount;
    private Integer accountBalanceSnapshot;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;
    @CreationTimestamp
    private LocalDateTime createdAt;

}

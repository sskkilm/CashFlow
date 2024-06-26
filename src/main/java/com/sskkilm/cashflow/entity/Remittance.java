package com.sskkilm.cashflow.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@Table(indexes = {
        @Index(name = "idx_account_createdAt", columnList = "account_id, created_at DESC")
})
@EntityListeners(AuditingEntityListener.class)
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
    @CreatedDate
    private LocalDateTime createdAt;

}

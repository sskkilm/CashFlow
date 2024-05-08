package com.sskkilm.cashflow.entity;

import com.sskkilm.cashflow.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;
    private String accountNumber;
    private Integer balance;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime modifiedAt;

    public void inactive() {
        this.status = AccountStatus.INACTIVE;
    }

    public void deposit(Integer depositAmount) {
        this.balance += depositAmount;
    }

    public void withdraw(Integer withdrawAmount) {
        this.balance -= withdrawAmount;
    }
}

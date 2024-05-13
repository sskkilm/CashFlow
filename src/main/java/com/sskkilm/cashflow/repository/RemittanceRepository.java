package com.sskkilm.cashflow.repository;

import com.sskkilm.cashflow.entity.Account;
import com.sskkilm.cashflow.entity.Remittance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RemittanceRepository extends JpaRepository<Remittance, Long> {
    List<Remittance> findAllByAccountOrderByCreatedAtDesc(Account account);

    @Query("Select r from Remittance r Join r.account a Where a = :account " +
            "And r.createdAt Between :startDate And :endDate Order By r.createdAt DESC")
    List<Remittance> findAllByAccountOrderByCreatedAtBetweenDesc(
            @Param("account") Account account,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}

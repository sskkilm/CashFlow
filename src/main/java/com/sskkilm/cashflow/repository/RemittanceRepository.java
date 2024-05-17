package com.sskkilm.cashflow.repository;

import com.sskkilm.cashflow.entity.Account;
import com.sskkilm.cashflow.entity.Remittance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface RemittanceRepository extends JpaRepository<Remittance, Long> {
    Slice<Remittance> findAllByAccount(Account account, Pageable pageable);

    @Query("Select r from Remittance r Where r.account = :account " +
            "And r.createdAt Between :startDate And :endDate")
    Page<Remittance> findAllByAccountAndCreatedAt(
            @Param("account") Account account,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

}

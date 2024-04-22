package com.sskkilm.cashflow.repository;

import com.sskkilm.cashflow.entity.Remittance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RemittanceRepository extends JpaRepository<Remittance, Long> {
}

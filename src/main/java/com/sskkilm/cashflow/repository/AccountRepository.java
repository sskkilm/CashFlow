package com.sskkilm.cashflow.repository;

import com.sskkilm.cashflow.entity.Account;
import com.sskkilm.cashflow.entity.User;
import com.sskkilm.cashflow.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUser(User user);

    boolean existsByAccountNumber(String accountNumber);

    List<Account> findAllByUserOrderByCreatedAt(User user);

    List<Account> findAllByUserAndStatusOrderByCreatedAt(User user, AccountStatus status);

    Optional<Account> findByAccountNumber(String accountNumber);
}

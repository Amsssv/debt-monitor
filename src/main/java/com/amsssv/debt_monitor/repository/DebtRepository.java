package com.amsssv.debt_monitor.repository;

import com.amsssv.debt_monitor.entity.Debt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DebtRepository extends JpaRepository<Debt, Long> {
}

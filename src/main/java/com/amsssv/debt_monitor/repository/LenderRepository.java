package com.amsssv.debt_monitor.repository;

import com.amsssv.debt_monitor.entity.Lender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LenderRepository extends JpaRepository<Lender, Long> {
  Optional<Lender> findByName(String name);
}

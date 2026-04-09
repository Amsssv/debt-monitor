package com.amsssv.debt_monitor.repository;

import com.amsssv.debt_monitor.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    Optional<Contact> findByNameAndUser_TelegramUserId(String name, Long telegramUserId);

    List<Contact> findByUser_TelegramUserId(Long telegramUserId);

    @Query("SELECT DISTINCT c FROM Contact c LEFT JOIN FETCH c.debts WHERE c.user.telegramUserId = :telegramUserId")
    List<Contact> findWithDebtsByTelegramUserId(@Param("telegramUserId") Long telegramUserId);
}

package com.example.event_review.Repo;

import com.example.event_review.Entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VerificationCodeRepo extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByEmail(String email);
    Optional<VerificationCode> findByCodeAndEmail(String code, String email);
    void deleteByEmail(String email);
}
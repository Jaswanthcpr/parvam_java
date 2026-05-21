package com.courseportal.repo;

import com.courseportal.model.OtpToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
  Optional<OtpToken> findTopByIdentifierAndUsedFalseOrderByCreatedAtDesc(String identifier);
}


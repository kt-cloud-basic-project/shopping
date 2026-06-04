package com.kt.repository.certify;

import com.kt.domain.certify.Certify;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CertifyRepository extends JpaRepository<Certify, Long> {
    Optional<Certify> findTopByEmailOrderByCreatedAtDesc(String email);

}

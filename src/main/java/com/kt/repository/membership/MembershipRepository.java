package com.kt.repository.membership;

import java.util.Optional;

import com.kt.domain.membership.Membership;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MembershipRepository extends JpaRepository<Membership, Long> {

    Optional<Membership> findByLevel(String level);
}

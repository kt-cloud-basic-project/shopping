package com.kt.repository.faq;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.domain.faq.FAQ;

public interface FAQRepository extends JpaRepository<FAQ, Long> {
}

package com.kt.repository.option;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.domain.option.Option;

public interface OptionRepository extends JpaRepository<Option, Long> {
}

package com.kt.common;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@MappedSuperclass
public abstract class BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

    @CreatedDate
    @Column(updatable = false)
	protected LocalDateTime createdAt;

    @LastModifiedDate
	protected LocalDateTime updatedAt;
}


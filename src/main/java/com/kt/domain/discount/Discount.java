package com.kt.domain.discount;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Discount {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 30)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DiscountTargetType targetType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DiscountType type;

	@Column(nullable = false)
	private boolean isCombinable;

	/**
	 * DB는 null 차단
	 * Java 객체는 null 허용?
	 * 생성 중간 단계에서 불완전 객체 발생 가능 -> Long -> long
	 */
	@Column(nullable = false)
	private long value;

	public Discount(String name, DiscountTargetType targetType, DiscountType type, boolean isCombinable, long value) {
		this.name = name;
		this.targetType = targetType;
		this.type = type;
		this.isCombinable = isCombinable;
		this.value = value;
	}

	public void update(String name, DiscountType type, boolean isCombinable, long value) {
		this.name = name;
		this.type = type;
		this.isCombinable = isCombinable;
		this.value = value;
	}
}

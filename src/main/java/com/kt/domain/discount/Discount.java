package com.kt.domain.discount;

import com.kt.domain.membership.Membership;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
	private DiscountType type;

	@Column(nullable = false)
	private Integer value;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "membership_id")
	private Membership membership;

	public Discount(String name, DiscountType type, Integer value, Membership membership) {
		this.name = name;
		this.type = type;
		this.value = value;
		this.membership = membership;
	}

	public void update(String name, DiscountType type, Integer value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	public Long calcDiscountFinalPrice(Long originalPrice) {
		if (originalPrice == null || originalPrice <= 0) {
			return 0L;
		}

		if (this.type == DiscountType.FIXED_AMOUNT) {
			return Math.max(0, originalPrice - this.value);
		} else {
			return Math.max(0, originalPrice - (originalPrice * this.value / 100));
		}
	}

	public Long calcDiscountAmount(Long originalPrice) {
		return originalPrice - calcDiscountFinalPrice(originalPrice);
	}

}

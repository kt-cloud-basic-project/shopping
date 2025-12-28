package com.kt.domain.discountMembership;

import com.kt.domain.discount.Discount;
import com.kt.domain.membership.Membership;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class DiscountMembership {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "discount_id")
	private Discount discount;

	@OneToOne
	@JoinColumn(name = "membership_id")
	private Membership membership;

	public DiscountMembership(Discount discount, Membership membership) {
		this.discount = discount;
		this.membership = membership;
	}
}

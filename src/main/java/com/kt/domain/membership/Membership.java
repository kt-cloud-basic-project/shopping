package com.kt.domain.membership;

import com.kt.domain.discount.Discount;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Membership {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 30)
	private String level; // BASIC, SILVER, GOLD 등등

	private boolean isDeleted;

	public Membership(String level) {
			this.level = level;
			this.isDeleted = false;
	}

	public void update(String level) {
		this.level = level;
	}

	public void delete() {
		this.isDeleted = true;
	}
}

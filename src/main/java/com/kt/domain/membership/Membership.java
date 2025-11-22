package com.kt.domain.membership;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Membership {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String level; // BASIC, SILVER, GOLD 등등

	public Membership(String level) {
			this.level = level;
	}

	public void update(String level) {
		this.level = level;
	}
}

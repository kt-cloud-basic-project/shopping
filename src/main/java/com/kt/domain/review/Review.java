package com.kt.domain.review;

import com.kt.common.BaseEntity;
import com.kt.domain.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Review extends BaseEntity {

	@Column(nullable = false, length = 80)
	private String title;

	@Column(nullable = false, length = 500)
	private String description;

	@Column(nullable = false)
	private Integer star;

	private boolean isDeleted;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	//TODO: OrderProduct와의 연관관계 설정
	//@ManyToOne
	//@JoinColumn(name = "order_product_id")
	//private OrderProduct product;

	public Review(String title, String description, int star) {
		this.title = title;
		this.description = description;
		this.star = star;
		this.isDeleted = false;
	}

	public void update(String title, String description, Integer star, boolean isDeleted) {
		this.title = title;
		this.description = description;
		this.star = star;
		this.isDeleted = isDeleted;
	}
}

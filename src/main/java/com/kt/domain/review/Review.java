package com.kt.domain.review;


import com.kt.common.BaseEntity;
import com.kt.common.ErrorCode;
import com.kt.common.Preconditions;
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
	/*@ManyToOne
	@JoinColumn(name = "order_product_id")
	private OrderProduct orderProduct;*/

	public Review(User user, String title, String description, Integer star) {
		userParamCheck(user, description, star);

		this.user = user;
		this.title = title;
		this.description = description;
		this.star = star;
		this.isDeleted = false;
	}

	public void update(User user, String description, Integer star) {
		userParamCheck(user, description, star);

		this.description = description;
		this.star = star;
	}

	public void delete() {
		this.isDeleted = true;
	}
}

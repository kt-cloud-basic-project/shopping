package com.kt.domain.review;


import com.kt.common.support.BaseEntity;
import com.kt.common.exception.ErrorCode;
import com.kt.common.support.Preconditions;
import com.kt.domain.orderproduct.OrderProduct;
import com.kt.domain.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

	@ManyToOne
	@JoinColumn(name = "order_product_id")
	private OrderProduct orderProduct;

	public Review(User user, OrderProduct orderProduct, String title, String description, Integer star) {
		userParamCheck(description, star);

		this.user = user;
		this.orderProduct = orderProduct;
		this.title = title;
		this.description = description;
		this.star = star;
		this.isDeleted = false;
	}

	public void update(String title, String description, Integer star) {
		userParamCheck(description, star);

		this.title = title;
		this.description = description;
		this.star = star;
	}

	public void delete() {
		this.isDeleted = true;
	}

	private void userParamCheck(String description, Integer star) {
		Preconditions.validate(!description.isBlank(), ErrorCode.INVALID_PARAMETER);
		Preconditions.validate(star >= 1 && star <= 5, ErrorCode.INVALID_REVIEW_STAR);
	}
}

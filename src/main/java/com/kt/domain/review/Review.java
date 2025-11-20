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

	public Review(User user, String title, String description, int star) {
		Preconditions.validate(!title.isBlank(), ErrorCode.INVALID_PARAMETER);
		Preconditions.validate(!description.isBlank(), ErrorCode.INVALID_PARAMETER);
		Preconditions.validate(star >= 1 && star <= 5, ErrorCode.INVALID_REVIEW_STAR);

		this.user = user;
		this.title = title;
		this.description = description;
		this.star = star;
		this.isDeleted = false;
	}

	public void update(User user, String description, Integer star) {
		Preconditions.validate(this.user.getId().equals(user.getId()), ErrorCode.NOT_REVIEW_AUTHOR);
		Preconditions.validate(!description.isBlank(), ErrorCode.INVALID_PARAMETER);
		Preconditions.validate(star >= 1 && star <= 5, ErrorCode.INVALID_REVIEW_STAR);

		this.description = description;
		this.star = star;
	}

	public void delete(User user) {
		Preconditions.validate(this.user.getId().equals(user.getId()), ErrorCode.NOT_REVIEW_AUTHOR);

		this.isDeleted = true;
	}
}

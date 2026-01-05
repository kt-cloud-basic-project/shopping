package com.kt.dto.wishlist;

import java.time.LocalDateTime;

import com.kt.domain.wishlist.Wishlist;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WishlistResponse {
	private Long id;
	private Long productId;
	private String productName;
	private Long price;
	private LocalDateTime createdAt;

	public static WishlistResponse from(Wishlist wishlist){
		return new WishlistResponse(
			wishlist.getId(),
			wishlist.getProduct().getId(),
			wishlist.getProduct().getName(),
			wishlist.getProduct().getPrice(),
			wishlist.getCreatedAt()
		);
	}
}

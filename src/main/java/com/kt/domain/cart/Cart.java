package com.kt.domain.cart;

import com.kt.common.support.BaseEntity;
import com.kt.domain.product.Product;
import com.kt.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Cart extends BaseEntity {
    @Column(nullable = false)
    private Integer productCount;

    private Long variantId;

	private Long totalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public Cart(Integer productCount, Long variantId, User user, Product product) {
        this.productCount = productCount;
        this.variantId = variantId;
        this.user = user;
        this.product = product;
				this.totalPrice = this.product.getPrice() * productCount;
		}

	public void updateQuantity(Integer productCount) {
		this.productCount = productCount;
		this.totalPrice = this.product.getPrice() * productCount;
	}
}

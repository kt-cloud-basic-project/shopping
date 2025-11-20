package com.kt.domain.cart;

import com.kt.common.BaseEntity;
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

    private String productOption;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public Cart(int productCount, String productOption, User user, Product product) {
        this.productCount = productCount;
        this.productOption = productOption;
        this.user = user;
        this.product = product;
    }
}

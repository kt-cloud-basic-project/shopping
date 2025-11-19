package com.kt.domain.cart;

import com.kt.common.BaseEntity;
import com.kt.domain.product.Product;
import com.kt.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class Cart extends BaseEntity{
    private int productCount;

    private String option;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}

package com.kt.service.cart;

import com.kt.dto.cart.CartCreateRequest;
import com.kt.repository.cart.CartRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;

    public void create(@Valid CartCreateRequest request) {
        //TODO: create 함수 기능 구현
    }

}

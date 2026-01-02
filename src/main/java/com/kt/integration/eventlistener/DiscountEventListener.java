package com.kt.integration.eventlistener;

import com.kt.event.ProductDiscountEvent;
import com.kt.notification.EMailSendService;
import com.kt.notification.MailSendRequest;
import com.kt.repository.cart.CartRepository;
import com.kt.repository.wishlist.WishlistRepository;
import com.kt.service.discount.DiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DiscountEventListener {
    private final DiscountService discountService;
    private final EMailSendService eMailSendService;
    private final MailSendRequest mailSendRequest;
    private final WishlistRepository wishlistRepository;
    private final CartRepository cartRepository;

    @EventListener
    public void sendMail(ProductDiscountEvent event){
        List<String> wishlistEmails = wishlistRepository.findDistinctEmailsByProductId(event.productId());
        List<String> cartEmails = cartRepository.findDistinctEmailsByProductId(event.productId());

        Set<String> distinct_emails = new HashSet<>(wishlistEmails);
        distinct_emails.addAll(cartEmails);

        if (distinct_emails.isEmpty()) return;

        for(String email : distinct_emails){
            String title = "현재 상품" + event.productName() + "이(가) 할인중입니다.";
            String content = "상품명: 에어팟 프로 2세대\n할인율: 15%\n할인가: 289,000원\n링크: https://your-shop.com/products/" + event.productId() + "\n\n지금 구매하면 혜택 적용됩니다.";
            MailSendRequest request = new MailSendRequest(email,title,content);
            eMailSendService.sendEmail(request);
        }

    }
}

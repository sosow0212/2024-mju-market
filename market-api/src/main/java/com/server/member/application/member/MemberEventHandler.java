package com.server.member.application.member;

import com.server.market.domain.product.event.ProductSoldEvent;
import com.server.member.application.member.dto.TradeHistoryCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemberEventHandler {

    private final MemberService memberService;

    @EventListener
    public void handleProductSold(final ProductSoldEvent event) {
        TradeHistoryCreateRequest request = new TradeHistoryCreateRequest(event.getBuyerId(),
                event.getSellerId(),
                event.getProductId(),
                event.getProductOriginalPrice(),
                event.getProductDiscountPrice(),
                event.getUsingCouponIds()
        );

        memberService.saveTradeHistory(request);
    }
}

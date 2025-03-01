package com.server.market.ui.product.dto;

import com.server.market.domain.product.Product;

public record ProductResponse(
        Long productId,
        Long ownerId,
        String title,
        String content,
        Integer price
) {

    public static ProductResponse from(final Product product) {
        return new ProductResponse(
                product.getId(),
                product.getMemberId(),
                product.getDescription().getTitle(),
                product.getDescription().getContent(),
                product.getPrice().getPrice()
        );
    }
}

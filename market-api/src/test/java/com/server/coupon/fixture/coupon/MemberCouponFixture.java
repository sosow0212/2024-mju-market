package com.server.coupon.fixture.coupon;

import com.server.coupon.domain.coupon.MemberCoupon;

public class MemberCouponFixture {

    public static MemberCoupon 멤버_쿠폰_생성() {
        return MemberCoupon.builder()
                .couponId(1L)
                .memberId(1L)
                .build();
    }
}

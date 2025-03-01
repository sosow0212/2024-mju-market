package com.server.member.ui.member;

import com.server.helper.MockBeanInjection;
import com.server.market.domain.product.vo.Location;
import com.server.market.domain.product.vo.ProductStatus;
import com.server.member.domain.member.dto.ProductByMemberResponse;
import com.server.member.domain.member.dto.TradeHistoryResponse;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static com.server.helper.RestDocsHelper.customDocument;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
@AutoConfigureRestDocs
@WebMvcTest(MemberController.class)
class MemberControllerTest extends MockBeanInjection {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void 거래_내역을_조회한다() throws Exception {
        // given
        when(memberService.findTradeHistories(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(List.of(new TradeHistoryResponse(1L, "buyerNickname", "sellerNickname", 1L, "productTitle", 10000, 10, "1,2,3")));

        // when & then
        mockMvc.perform(get("/api/members/{memberId}/histories", 1L)
                        .queryParam("isSeller", String.valueOf(true))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer tokenInfo~")
                ).andExpect(status().isOk())
                .andDo(customDocument("find_trade_histories",
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 토큰 정보")
                        ),
                        queryParameters(
                                parameterWithName("isSeller").description("판매자인지 구매자인지 여부 (true = 판매자, false = 구매자)")
                        ),
                        responseFields(
                                fieldWithPath("[0].tradeHistoryId").description("거래 내역 id"),
                                fieldWithPath("[0].buyerName").description("구매자 닉네임"),
                                fieldWithPath("[0].sellerName").description("판매자 닉네임"),
                                fieldWithPath("[0].productId").description("상품 id"),
                                fieldWithPath("[0].productTitle").description("상품 제목"),
                                fieldWithPath("[0].productOriginPrice").description("상품 정상가"),
                                fieldWithPath("[0].productDiscountPrice").description("상품 할인해서 구매한 가격"),
                                fieldWithPath("[0].usingCouponIds").description("사용한 쿠폰 ids, String 타입으로 ',' 이용해서 묶음")
                        )
                ));
    }

    @Test
    void 상품_판매_내역을_조회한다() throws Exception {
        // given
        when(memberService.findProductHistories(anyLong(), anyLong()))
                .thenReturn(List.of(new ProductByMemberResponse(1L, 1L, "상품명", 10000, Location.BUILDING_CENTER, ProductStatus.WAITING, LocalDateTime.now())));

        // when & then
        mockMvc.perform(get("/api/members/{memberId}/products", 1L)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer tokenInfo~")
                ).andExpect(status().isOk())
                .andDo(customDocument("find_member_products",
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 토큰 정보")
                        ),
                        responseFields(
                                fieldWithPath("[0].productId").description("상품 id"),
                                fieldWithPath("[0].sellerId").description("판매자 id"),
                                fieldWithPath("[0].title").description("상품 제목"),
                                fieldWithPath("[0].price").description("상품 가격"),
                                fieldWithPath("[0].location").description("상품 거래 장소"),
                                fieldWithPath("[0].productStatus").description("상품 거래 상태"),
                                fieldWithPath("[0].createTime").description("상품 업로드 날짜")
                        )
                ));
    }

    @Test
    void 나의_id를_반환한다() throws Exception {
        // when & then
        mockMvc.perform(get("/api/members")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer tokenInfo~")
                ).andExpect(status().isOk())
                .andDo(customDocument("find_my_id",
                        requestHeaders(
                                headerWithName(AUTHORIZATION).description("유저 토큰 정보")
                        ),
                        responseFields(
                                fieldWithPath("id").description("로그인 한 유저의 id")
                        )
                ));
    }
}

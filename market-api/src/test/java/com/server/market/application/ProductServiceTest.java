package com.server.market.application;

import com.server.market.application.product.ProductQueryService;
import com.server.market.application.product.ProductService;
import com.server.market.application.product.dto.ProductCreateRequest;
import com.server.market.application.product.dto.ProductUpdateRequest;
import com.server.market.application.product.dto.UsingCouponRequest;
import com.server.market.domain.product.Product;
import com.server.market.domain.product.ProductRepository;
import com.server.market.domain.product.dto.ProductPagingSimpleResponse;
import com.server.market.domain.product.vo.Location;
import com.server.market.exception.exceptions.ProductNotFoundException;
import com.server.market.exception.exceptions.ProductOwnerNotEqualsException;
import com.server.market.infrastructure.product.ProductFakeRepository;
import com.server.market.infrastructure.product.ProductImageFakeConverter;
import com.server.market.infrastructure.product.ProductImageFakeUploader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.server.market.fixture.ProductFixture.상품_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class ProductServiceTest {

    private ProductService productService;
    private ProductQueryService productQueryService;
    private ProductRepository productRepository;

    @BeforeEach
    void setup() {
        productRepository = new ProductFakeRepository();
        productQueryService = new ProductQueryService(productRepository);
        productService = new ProductService(productRepository, new ProductImageFakeConverter(), new ProductImageFakeUploader());
    }

    @Test
    void 카테고리에_속한_상품을_모두_찾는다() {
        // given
        Product savedProduct = productRepository.save(상품_생성());

        // when
        List<ProductPagingSimpleResponse> result = productQueryService.findAllProductsInCategory(1L, null, 1L, 10);

        // then
        assertSoftly(softly -> {
            softly.assertThat(result).hasSize(1);
            softly.assertThat(result.get(0).id()).isEqualTo(savedProduct.getId());
        });
    }

    @Test
    void 상품을_등록한다() {
        // given
        ProductCreateRequest request = new ProductCreateRequest("new", "new", 10, Location.BUILDING_CENTER, new ArrayList<>());

        // when
        Long id = productService.uploadProduct(1L, 1L, request);

        // then
        assertThat(id).isEqualTo(1L);
    }

    @Test
    void 상품을_id로_조회한다() {
        // given
        Product savedProduct = productRepository.save(상품_생성());

        // when
        Product found = productService.addViewCount(savedProduct.getId(), true);

        // then
        assertSoftly(softly -> {
            softly.assertThat(savedProduct.getStatisticCount().getVisitedCount()).isEqualTo(1L);
            softly.assertThat(savedProduct)
                    .usingRecursiveComparison()
                    .isEqualTo(found);
        });
    }

    @Test
    void 상품이_존재하지_않으면_예외를_발생시킨다() {
        // when & then
        assertThatThrownBy(() -> productService.addViewCount(-1L, true))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void 상품을_업데이트한다() {
        // given
        Product savedProduct = productRepository.save(상품_생성());
        MockMultipartFile file = new MockMultipartFile("name", "origin.jpg", "image", "content".getBytes());
        ProductUpdateRequest request = new ProductUpdateRequest("수정", "수정", 1000, 1L, Location.BUILDING_CENTER, new ArrayList<>(List.of(file)), new ArrayList<>());

        // when
        productService.update(savedProduct.getId(), savedProduct.getMemberId(), request);

        // then
        assertSoftly(softly -> {
            softly.assertThat(savedProduct.getDescription().getTitle()).isEqualTo(request.title());
            softly.assertThat(savedProduct.getDescription().getContent()).isEqualTo(request.content());
            softly.assertThat(savedProduct.getPrice().getPrice()).isEqualTo(1000);
        });
    }

    @Test
    void 상품_업데이트시에_상품이_없다면_예외를_발생시킨다() {
        // given
        Product savedProduct = productRepository.save(상품_생성());
        ProductUpdateRequest request = new ProductUpdateRequest("new", "new", 1000, 2L, Location.BUILDING_CENTER, new ArrayList<>(), new ArrayList<>());

        // when & then
        assertThatThrownBy(() -> productService.update(savedProduct.getId(), -1L, request))
                .isInstanceOf(ProductOwnerNotEqualsException.class);
    }

    @Test
    void 상품_업데이트시에_상품의_주인과_다를시_예외를_발생시킨다() {
        // given
        ProductUpdateRequest request = new ProductUpdateRequest("new", "new", 1000, 2L, Location.BUILDING_CENTER, new ArrayList<>(), new ArrayList<>());

        // when & then
        assertThatThrownBy(() -> productService.update(-1L, -1L, request))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void 상품을_id로_제거한다() {
        // given
        Product savedProduct = productRepository.save(상품_생성());

        // when
        productService.delete(savedProduct.getId(), savedProduct.getMemberId());

        // then
        Optional<Product> found = productRepository.findById(savedProduct.getId());
        assertThat(found).isEmpty();
    }

    @Test
    void 상품제거시에_상품이_존재하지_않으면_예외를_발생시킨다() {
        // when & then
        assertThatThrownBy(() -> productService.delete(-1L, 1L))
                .isInstanceOf(ProductNotFoundException.class);
    }

    @Test
    void 상품_제거시에_상품_주인과_일치하지_않다면_예외를_발생시킨다() {
        // given
        Product savedProduct = productRepository.save(상품_생성());

        // when & then
        assertThatThrownBy(() -> productService.delete(savedProduct.getId(), -1L))
                .isInstanceOf(ProductOwnerNotEqualsException.class);
    }

    @Test
    void 상품을_구매한다() {
        // given
        Product savedProduct = productRepository.save(상품_생성());
        UsingCouponRequest request = new UsingCouponRequest(List.of(), savedProduct.getPrice().getPrice(), 0);

        // when & then
        assertDoesNotThrow(() -> productService.buyProducts(savedProduct.getId(), 1L, request));
    }
}


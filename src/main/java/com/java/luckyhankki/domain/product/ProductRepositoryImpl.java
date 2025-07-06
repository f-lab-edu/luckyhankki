package com.java.luckyhankki.domain.product;

import com.java.luckyhankki.dto.product.*;
import com.java.luckyhankki.dto.product.ProductSearchCondition.PickupDateFilter;
import com.java.luckyhankki.dto.product.ProductSearchCondition.SortType;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.java.luckyhankki.domain.product.QProduct.product;
import static com.querydsl.core.types.dsl.Expressions.numberTemplate;

public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProductRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Slice<ProductWithDistanceResponse> findAllByCondition(ProductSearchRequest request, Pageable pageable) {
        ProductSearchCondition condition = request.condition();
        NumberExpression<Double> distance = calculateDistance(request.userLat(), request.userLon());

        List<ProductWithDistanceResponse> content = queryFactory
                .select(new QProductWithDistanceResponse(
                            new QProductResponse(
                                product.id,
                                product.store.name,
                                product.category.name,
                                product.name,
                                product.priceOriginal,
                                product.priceDiscount,
                                product.stock,
                                product.pickupStartDateTime,
                                product.pickupEndDateTime)
                        , distance.castToNum(BigDecimal.class))
                )
                .from(product)
                .where(
                        product.isActive.isTrue(),  // 상품 활성화 조건은 필수
                        categoryIdEq(condition.categoryId()),
                        pickupDateFilterEq(condition.pickupDate()),
                        keywordContains(condition.keyword())
                )
                .orderBy(getOrderSpecifier(condition, request))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();
        if (hasNext) {
            content.remove(content.size() - 1);
        }
        return new SliceImpl<>(content, pageable, true);
    }

    /**
     * 카테고리 ID에 해당하는 상품 필터링
     *
     * @param categoryId           카테고리 ID
     * @return BooleanExpression - 조건 또는 null
     */
    private BooleanExpression categoryIdEq(Long categoryId) {
        return categoryId != null ? product.category.id.eq(categoryId) : null;
    }

    /**
     * 픽업 날짜 조건 필터링
     * - NOW : 현재 시각 기준
     * - TODAY : 금일 00:00~23:59 사이에 픽업 가능한 상품
     * - TOMORROW : 명일 00:00 이후 픽업 가능한 상품
     *
     * @param pickupDateFilter     날짜 필터 타입
     * @return BooleanExpression - 조건 또는 null
     */
    private BooleanExpression pickupDateFilterEq(PickupDateFilter pickupDateFilter) {
        if (pickupDateFilter == null) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime today = now.toLocalDate().atStartOfDay();
        LocalDateTime tomorrow =  today.plusDays(1);

        return switch (pickupDateFilter) {
            case NOW -> product.pickupStartDateTime.loe(now)
                    .and(product.pickupEndDateTime.goe(now));
            case TODAY -> product.pickupStartDateTime.loe(tomorrow.minusMinutes(1))
                    .and(product.pickupEndDateTime.goe(today));
            case TOMORROW -> product.pickupStartDateTime.goe(tomorrow);
        };
    }

    /**
     * 상품명에 키워드가 포함된 경우 필터링
     *
     * @param keyword   검색 키워드
     * @return BooleanExpression - 조건 또는 null
     */
    private BooleanExpression keywordContains(String keyword) {
        return keyword != null ? product.name.contains(keyword) : null;
    }

    /**
     * Haversine을 이용한 사용자와 가게 간 거리 차이 구하기
     *
     * @param userLat   사용자 위도
     * @param userLon   사용자 경도
     * @return 사용자와 가게 간 거리 차이
     */
    private NumberExpression<Double> calculateDistance(Double userLat, Double userLon) {
        // 위도/경도 필드
        NumberExpression<Double> storeLat = product.store.latitude.doubleValue();
        NumberExpression<Double> storeLon = product.store.longitude.doubleValue();

        // Haversine 공식 사용
        return numberTemplate(Double.class,
                "6371 * acos(" +
                        "cos(radians({0})) * cos(radians({1})) * " +
                        "cos(radians({2}) - radians({3})) + " +
                        "sin(radians({0})) * sin(radians({1}))" +
                        ")",
                userLat, storeLat, storeLon, userLon);
    }

    /**
     * 정렬 기준에 따라 OrderSpecifier 리턴
     * - 거리 가까운 순 default
     *
     * @param condition       조회 조건
     * @return OrderSpecifier
     */
    private OrderSpecifier<?> getOrderSpecifier(ProductSearchCondition condition, ProductSearchRequest request) {
        SortType sortType = (condition.sortType() == null) ? SortType.DISTANCE : condition.sortType();

        return new OrderSpecifier<>(sortType.getOrder(), getTarget(sortType, request));
    }

    /**
     * 정렬 대상 필드 매핑
     * - DISTANCE : 거리 가까운 순
     * - TODO RATING
     * - PRICE : 가격 낮은 순
     *
     * @param sortType      정렬 기준
     * @return Expression - 정렬 대상 필드
     */
    private Expression getTarget(SortType sortType, ProductSearchRequest request) {
        return switch (sortType) {
            case DISTANCE -> calculateDistance(request.userLat(), request.userLon());
            case RATING -> product.id;      //TODO 평점 정렬 구현 필요
            case PRICE -> product.priceDiscount;
        };
    }
}

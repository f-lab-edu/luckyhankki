package com.java.luckyhankki.domain.product;

import com.java.luckyhankki.dto.product.ProductResponse;
import com.java.luckyhankki.dto.product.ProductSearchCondition;
import com.java.luckyhankki.dto.product.ProductSearchCondition.PickupDateFilter;
import com.java.luckyhankki.dto.product.ProductSearchCondition.SortType;
import com.java.luckyhankki.dto.product.QProductResponse;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.time.LocalDateTime;
import java.util.List;

import static com.java.luckyhankki.domain.product.QProduct.product;

public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public ProductRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Slice<ProductResponse> findAllByCondition(ProductSearchCondition condition, Pageable pageable) {
        List<ProductResponse> content = queryFactory
                .select(new QProductResponse(
                        product.id,
                        product.store.name,
                        product.category.name,
                        product.name,
                        product.priceOriginal,
                        product.priceDiscount,
                        product.stock,
                        product.pickupStartDateTime,
                        product.pickupEndDateTime)
                )
                .from(product)
                .where(
                        product.isActive.isTrue(),  // 상품 활성화 조건은 필수
                        categoryIdEq(condition.categoryId()),
                        pickupDateFilterEq(condition.pickupDate()),
                        keywordContains(condition.keyword())
                )
                .orderBy(getOrderSpecifier(condition))
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
     * 정렬 기준에 따라 OrderSpecifier 리턴
     * - 상품 할인가격 오름차순 default (추후 거리순이 default 되도록 변경)
     *
     * @param condition       조회 조건
     * @return OrderSpecifier
     */
    private OrderSpecifier<?> getOrderSpecifier(ProductSearchCondition condition) {
        if (condition.sortType() == null) {
            return new OrderSpecifier<>(Order.ASC, product.priceDiscount);
        }

        SortType sortType = condition.sortType();
        return new OrderSpecifier<>(sortType.getOrder(), getTarget(sortType));
    }

    /**
     * 정렬 대상 필드 매핑
     * - 현재는 가격만 적용, 거리/평점은 추후 구현 예정
     *
     * @param sortType      정렬 기준
     * @return Expression - 정렬 대상 필드
     */
    private Expression getTarget(SortType sortType) {
        return switch (sortType) {
            case DISTANCE -> product.id;    //TODO 거리 정렬 구현 필요
            case RATING -> product.id;      //TODO 평점 정렬 구현 필요
            case PRICE -> product.priceDiscount;
        };
    }
}

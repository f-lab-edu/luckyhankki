-- seller 테이블 데이터 삽입
INSERT IGNORE INTO seller (
    business_number, name, password, email,
    login_fail, created_at, updated_at
)
VALUES (
    '1234567890', '홍길동', 'abc@!12345', 'gildong@test.com',
    0, CURRENT_TIMESTAMP, null
);

-- store 테이블 데이터 삽입
INSERT IGNORE INTO store (
    seller_id, name, phone, address,
    longitude, latitude, is_approved, report_count, created_at, updated_at, is_deleted
)
VALUES (
    1, '길동국밥', '031-123-4567', '경기도 수원시 XX구 XX동',
    126.123456, 36.123456, 1, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
);

-- category 테이블 데이터 삽입
INSERT IGNORE INTO category (category_id, name)
VALUES (1, '음식'),
       (2, '베이커리'),
       (3, '식료품');

-- product 테이블 데이터 삽입
INSERT IGNORE INTO product (
    store_id, category_id, name, price_original, price_discount, stock,
    description, pickup_start_date_time, pickup_end_date_time,
    is_active, created_at, updated_at
)
VALUES (
    1, 1, '1인럭키세트', 28000, 10000, 2,
    '길동국밥의 시그니처 국밥과 럭키반찬들로 이루어진 구성품입니다,', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP + INTERVAL 3 HOUR,
    1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- user 테이블 데이터 삽입
-- 일반 회원
INSERT IGNORE INTO users (
    email, password, name, phone,
    address, longitude, latitude,
    role_type, login_fail_cnt, created_at, updated_at, last_login_at, is_deleted
)
VALUES (
    'user1@test.com', 'user@123!', '김철수', '010-1234-5678',
    '경기도 수원시 XX구 YY동', 126.789012, 36.789012,
    'CUSTOMER', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
);

-- 관리자
INSERT IGNORE INTO users (
    email, password, name, phone,
    address, longitude, latitude,
    role_type, login_fail_cnt, created_at, updated_at, last_login_at, is_deleted
)
VALUES (
    'admin1@test.com', 'admin@123!', '관리자1', '010-0000-1111',
    '서울특별시 강남구 XX구 YY동', 126.333333, 36.222222,
    'ADMIN', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 0
);

-- 리뷰 키워드
INSERT IGNORE INTO keyword (
    keyword, is_deleted, created_at, updated_at
)
VALUES ('음식이 맛있어요', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('픽업이 빨라요', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('가성비 좋아요', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('양이 많아요', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('신선해요', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('친절해요', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

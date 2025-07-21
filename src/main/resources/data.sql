-- category 테이블 데이터 삽입
INSERT IGNORE INTO category (category_id, name)
VALUES (1, '음식'),
       (2, '베이커리'),
       (3, '식료품');

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

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

-- 테스트용 사용자 데이터 삽입
INSERT IGNORE INTO users (
  email, password, name, phone, address, longitude, latitude, role_type, login_fail_cnt, created_at, is_deleted
) VALUES ('user@test.com', '$2a$10$wze0RzvQCvauVd58IJCMJ.I0OtSLm/Subeak0tIczM9lBTY.Ip0fO', '사용자',
          '010-1111-1111', '경기도 수원시 XX구 YY동 ZZ로', 126.123456, 36.123456, 'CUSTOMER', 0,
          CURRENT_TIMESTAMP, 0),
         ('admin@test.com', '$2a$10$qkWxivahP/Zk1dDDPt766OvOEgrcwjazqqxBBFS2whOSDpIUguDby', '관리자',
          '010-2222-2222', '경기도 용인시 XX구 YY동 ZZ로', 126.789123, 36.789123, 'ADMIN', 0,
          CURRENT_TIMESTAMP, 0);
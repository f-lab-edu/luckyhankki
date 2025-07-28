package com.java.luckyhankki.config;

import com.java.luckyhankki.domain.category.Category;
import com.java.luckyhankki.domain.category.CategoryRepository;
import com.java.luckyhankki.domain.product.Product;
import com.java.luckyhankki.domain.seller.Seller;
import com.java.luckyhankki.domain.seller.SellerRepository;
import com.java.luckyhankki.domain.store.Store;
import com.java.luckyhankki.domain.store.StoreRepository;
import com.java.luckyhankki.domain.user.User;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Profile("!test")
@Component
public class DummyDataLoader {

    private static final Random random = new Random();
    private static final Faker faker = new Faker(new Locale("ko", "kr"));

    // 대한민국의 경도와 위도 범위
    private static final double MIN_LONGITUDE = 125.066666; //극서
    private static final double MAX_LONGITUDE = 131.872222; //극동
    private static final double MIN_LATITUDE  = 33.100000; //극남
    private static final double MAX_LATITUDE  = 38.450000; //극북
    private static final Logger log = LoggerFactory.getLogger(DummyDataLoader.class);

    private final SellerRepository sellerRepository;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final JdbcTemplate jdbcTemplate;

    public DummyDataLoader(SellerRepository sellerRepository, StoreRepository storeRepository, CategoryRepository categoryRepository, JdbcTemplate jdbcTemplate) {
        this.sellerRepository = sellerRepository;
        this.storeRepository = storeRepository;
        this.categoryRepository = categoryRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {
        if (sellerRepository.count() > 0) {
            log.info("더미 데이터 생성을 건너뜁니다.");
            return;
        }

        // 사용자(Users) 데이터 생성
        log.info("사용자 더미 데이터 생성 시작");
        generateUsers();
        log.info("사용자 더미 데이터 생성 완료");

        // 판매자(Seller) 데이터 생성
        log.info("판매자 더미 데이터 생성 시작");
        generateSellers();
        log.info("판매자 더미 데이터 생성 완료");

        // 가게(Store) 데이터 생성
        log.info("가게 더미 데이터 생성 시작");
        List<Seller> sellers = sellerRepository.findAll();
        Collections.shuffle(sellers, random);
        generateStores(sellers);
        log.info("가게 더미 데이터 생성 완료");

        // 상품(Product) 데이터 생성
        log.info("상품 더미 데이터 생성 시작");
        List<Store> stores = storeRepository.findAll();
        List<Category> categories = categoryRepository.findAll();
        generateProducts(stores, categories);
        log.info("상품 더미 데이터 생성 완료");

        log.info("모든 더미 데이터 생성 완료");
    }

    private void generateUsers() {
        List<User> users = new ArrayList<>();
        int batchSize = 1000;

        for (int i = 0; i< 100_000; i++) {
            String email = "user" + i + "@gmail.com";
            String password = faker.internet().password(8, 16, true, true, true);
            String name = faker.name().fullName();
            String phone = faker.phoneNumber().cellPhone();
            String address = faker.address().fullAddress();

            BigDecimal longitude = generateRandomLocation(MIN_LONGITUDE, MAX_LONGITUDE);
            BigDecimal latitude = generateRandomLocation(MIN_LATITUDE, MAX_LATITUDE);

            User user = new User(email, name, phone, address, longitude, latitude);
            user.changePassword(password);
            users.add(user);

            if ((i + 1) % batchSize == 0) {
                batchInsertUsers(users);
            }
        }
    }

    private void batchInsertUsers(List<User> users) {
        String sql = "INSERT INTO users (email, password, name, phone, address, longitude, latitude, role_type, login_fail_cnt, created_at, updated_at, last_login_at, is_deleted) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                User user = users.get(i);
                ps.setString(1, user.getEmail());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getName());
                ps.setString(4, user.getPhone());
                ps.setString(5, user.getAddress());
                ps.setBigDecimal(6, user.getLongitude());
                ps.setBigDecimal(7, user.getLatitude());
                ps.setString(8, user.getRoleType().name());
                ps.setInt(9, user.getLoginFailCnt());
                ps.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now()));
                ps.setBoolean(13, false);
            }

            @Override
            public int getBatchSize() {
                return users.size();
            }
        });
        users.clear();
    }

    private void generateSellers() {
        List<Seller> sellers = new ArrayList<>();
        Set<String> businessNumSet = new HashSet<>();

        int batchSize = 1000;

        for (int i = 0; i< 50_000; i++) {
            String businessNumber = generateBusinessNumber(businessNumSet);
            String name = faker.name().fullName();
            String password = faker.internet().password(8, 16, true, true, true);
            String email = "seller" + i + "@gmail.com";

            Seller seller = new Seller(businessNumber, name, password, email);
            seller.changePassword(password);
            sellers.add(seller);

            if ((i + 1) % batchSize == 0) {
                batchInsertSellers(sellers);
            }
        }
    }

    private String generateBusinessNumber(Set<String> businessNumSet) {
        long businessNumMin = 1_000_000_000L;
        long businessNumMax = 9_999_999_999L;
        String businessNumber;

        do {
            long randomLong = faker.number().numberBetween(businessNumMin, businessNumMax + 1);
            businessNumber = String.valueOf(randomLong);
        } while (businessNumSet.contains(businessNumber));
        businessNumSet.add(businessNumber);

        return businessNumber;
    }

    private void batchInsertSellers(List<Seller> sellers) {
        String sql = "INSERT INTO seller (business_number, name, password, email, login_fail, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Seller seller = sellers.get(i);
                ps.setString(1, seller.getBusinessNumber());
                ps.setString(2, seller.getName());
                ps.setString(3, seller.getPassword());
                ps.setString(4, seller.getEmail());
                ps.setInt(5, seller.getLoginFail());
                ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            }

            @Override
            public int getBatchSize() {
                return sellers.size();
            }
        });
        sellers.clear();
    }

    private void generateStores(List<Seller> sellers) {
        List<Store> stores = new ArrayList<>();
        int batchSize = 1000;

        for (int i = 0; i < sellers.size(); i++) {
            Seller seller = sellers.get(i);

            String name = faker.company().name();
            String phone = faker.phoneNumber().phoneNumber();
            String address = faker.address().fullAddress();

            BigDecimal longitude = generateRandomLocation(MIN_LONGITUDE, MAX_LONGITUDE);
            BigDecimal latitude = generateRandomLocation(MIN_LATITUDE, MAX_LATITUDE);

            Store store = new Store(seller, name, phone, address, longitude, latitude);
            store.approveStore();
            stores.add(store);

            if ((i + 1) % batchSize == 0) {
                batchInsertStores(stores);
            }
        }
    }

    private void batchInsertStores(List<Store> stores) {
        String sql = "INSERT INTO store (seller_id, name, phone, address, longitude, latitude, is_approved, report_count, created_at, updated_at, is_active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Store store = stores.get(i);
                ps.setLong(1, store.getSeller().getId());
                ps.setString(2, store.getName());
                ps.setString(3, store.getPhone());
                ps.setString(4, store.getAddress());
                ps.setBigDecimal(5, store.getLongitude());
                ps.setBigDecimal(6, store.getLatitude());
                ps.setBoolean(7, store.isApproved());
                ps.setInt(8, store.getReportCount());
                ps.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
                ps.setBoolean(11, store.isActive());
            }

            @Override
            public int getBatchSize() {
                return stores.size();
            }
        });
        stores.clear();
    }

    private void generateProducts(List<Store> stores, List<Category> categories) {
        List<Product> products = new ArrayList<>();
        int batchSize = 1000;

        for (int i = 0; i < 100_000; i++) {
            Store randomStore = stores.get(random.nextInt(stores.size()));
            Category randomCategory = categories.get(random.nextInt(categories.size()));

            String name = faker.commerce().productName();
            String description = faker.lorem().paragraph();

            int priceOriginal = (faker.number().numberBetween(1000, 100_001) / 100) * 100;
            int discountPercentage = faker.number().numberBetween(1, 100); //1~99%
            // 0원 방지(최소 100원)
            int priceDiscount = Math.max(100, (int) (priceOriginal * (1 - discountPercentage / 100.0) / 100) * 100);
            int stock = faker.number().numberBetween(1, 11);

            LocalDateTime pickupStartDateTime = LocalDateTime.now().plusDays(faker.number().numberBetween(0, 366));
            LocalDateTime pickupEndDateTime = pickupStartDateTime.plusHours(random.nextInt(24) + 1);

            Product product = new Product(
                    randomStore,
                    randomCategory,
                    name,
                    priceOriginal,
                    priceDiscount,
                    stock,
                    description,
                    pickupStartDateTime,
                    pickupEndDateTime
            );
            products.add(product);
            if ((i + 1) % batchSize == 0) {
                batchInsertProducts(products);
            }
        }
    }

    private void batchInsertProducts(List<Product> products) {
        String sql = "INSERT INTO product (store_id, category_id, name, price_original, price_discount, stock, description, pickup_start_date_time, pickup_end_date_time, is_active, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Product product = products.get(i);
                ps.setLong(1, product.getStore().getId());
                ps.setLong(2, product.getCategory().getId());
                ps.setString(3, product.getName());
                ps.setInt(4, product.getPriceOriginal());
                ps.setInt(5, product.getPriceDiscount());
                ps.setInt(6, product.getStock());
                ps.setString(7, product.getDescription());
                ps.setTimestamp(8, Timestamp.valueOf(product.getPickupStartDateTime()));
                ps.setTimestamp(9, Timestamp.valueOf(product.getPickupEndDateTime()));
                ps.setBoolean(10, product.isActive());
                ps.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now()));
            }

            @Override
            public int getBatchSize() {
                return products.size();
            }
        });
        products.clear();
    }

    private static BigDecimal generateRandomLocation(double min, double max) {
        double range = max - min;
        double scaled = random.nextDouble() * range;
        double shifted = scaled + min;

        return BigDecimal.valueOf(shifted).setScale(6, RoundingMode.HALF_UP);
    }

}

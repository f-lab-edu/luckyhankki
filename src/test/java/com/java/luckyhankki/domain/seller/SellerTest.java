package com.java.luckyhankki.domain.seller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class SellerTest {

    @Autowired
    private SellerRepository repository;

    @Test
    @DisplayName("가입 성공하고 조회하면 동일한 사업자번호를 가진 판매자가 조회된다.")
    void should_find_seller_by_business_number_after_join() {
        //given
        Seller seller = Seller.create("1234567890", "판매자1", "password123", "seller@test.com");

        //when
        Seller savedSeller = repository.save(seller);
        Optional<Seller> optionalSeller = repository.findById(savedSeller.getId());

        //then
        assertThat(optionalSeller).isPresent();
        Seller foundSeller = optionalSeller.get();
        assertThat(foundSeller.getBusinessNumber()).isEqualTo("1234567890");
    }
}
package com.java.luckyhankki.domain.keyword;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class KeywordTest {

    @Autowired
    private KeywordRepository repository;

    @Test
    @DisplayName("키워드 저장 후 조회 테스트")
    void saveKeyword_AndFindById() {
        Keyword keyword = new Keyword("음식이 맛있어요");
        Keyword savedKeyword = repository.save(keyword);
        Optional<Keyword> foundKeyword = repository.findById(savedKeyword.getId());

        assertThat(foundKeyword).isPresent();
        Keyword result = foundKeyword.get();
        assertThat(result.getKeyword()).isEqualTo("음식이 맛있어요");
    }
}
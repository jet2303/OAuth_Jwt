package com.example.jwt_oauth.Renewal_230524.DataJpaTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Transactional(readOnly = true)
public class BoardServiceTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void create() {
        // testEntityManager.persist
    }

    @Test
    void read() {

    }

    @Test
    void update() {

    }

    @Test
    void delete() {

    }
}

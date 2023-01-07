package com.example.jwt_oauth.payload;

import org.springframework.boot.test.context.SpringBootTest;

import com.example.jwt_oauth.payload.Pagenation.PagenationBuilder;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

@SpringBootTest
public class PagenationTest {

    @Test
    public void pagenationBuilderTest(){
        Pagenation pagenation = new PagenationBuilder().totalPages(1)
                                                        .totalElements(2)
                                                        .currentPage(3)
                                                        .currentElements(4)
                                                        .build();
        assertEquals(pagenation.getTotalPages(), 1);    
        assertEquals(pagenation.getTotalElements(), 2);
        assertEquals(pagenation.getCurrentPage(), 3);
        assertEquals(pagenation.getCurrentElements(), 4);
    }

}

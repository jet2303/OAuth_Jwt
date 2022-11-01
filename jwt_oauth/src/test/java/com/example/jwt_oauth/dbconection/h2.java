package com.example.jwt_oauth.dbconection;

import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
public class h2 {
    
    @Test
    @DisplayName(value = "H2 DB connection check")
    void testH2DbConnection(){
        final String Driver = "org.h2.Driver";
        final String Url = "jdbc:h2:mem:testdb";
        final String user = "SA";
        final String passwwd = "";

        try{
            Class.forName(Driver);
            Connection conn = DriverManager.getConnection(Url, user, passwwd);
            
            assertNotNull(conn);
            log.info("result = {}", conn);
            log.info("{}, {}", conn.getMetaData().getURL(), conn.getMetaData().getUserName());
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

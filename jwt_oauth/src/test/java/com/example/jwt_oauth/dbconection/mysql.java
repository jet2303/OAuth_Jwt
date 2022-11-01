package com.example.jwt_oauth.dbconection;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class mysql {
    
    @Test
    @DisplayName(value = "Mysql DB connection Check")
    void testMysqlDbConnection(){
        final String Driver = "com.mysql.cj.jdbc.Driver";
        final String Url = "jdbc:mysql://127.0.0.1:3306/study";
        final String user = "root";
        final String passwwd = "root";

        try{
            Class.forName(Driver);
            Connection conn = DriverManager.getConnection(Url, user, passwwd);
            
            assertNotNull(conn);
            // log.info("result = {}", conn);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

package com.example.jwt_oauth.repository.user;

import java.util.Optional;

import org.hibernate.query.NativeQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.jwt_oauth.domain.user.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    Optional<User> findByName(String name);
    
    @Modifying
    @Transactional
    // @Query(value = "UPDATE _user(name, password) "
    //                 + "SET name=:name, password=:encryptPw "
    //               + "WHERE email=:email")
    @Query(value = "UPDATE _user u "
                     +"SET u.name=:name, u.password=:encryptPw "
                   +"WHERE u.email=:email", nativeQuery = true)
    Integer userInfoUpdate(@Param(value = "name")String name, @Param(value = "encryptPw")String password, @Param(value = "email")String email);
}

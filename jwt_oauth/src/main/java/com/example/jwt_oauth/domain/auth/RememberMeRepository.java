package com.example.jwt_oauth.domain.auth;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RememberMeRepository extends JpaRepository<RememberMe, String>{
    
    Optional<RememberMe> findByToken(String token);

    Optional<RememberMe> findBySeries(String series);
    
    void deleteBySeries(String series);

    // @Query("update RememberMe m " 
    //          +"set m.token=:token, m.lastLogin=:lastLogin "
    //        +"where m.series==:series")
    // Optional<RememberMe> updateBySeries(@Param("token") String token, @Param("lastLogin") Date lastLogin, @Param("series") String series);
}

package com.example.jwt_oauth.domain.auth;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RememberMeRepository extends JpaRepository<RememberMe, String>{
    
    Optional<RememberMe> findByToken(String token);

    Optional<RememberMe> findBySeries(String series);
    
    void deleteBySeries(String series);

    // @Query("update Remember_me m set m.token=:token m.last_login=:last_login where m.series==:series")
    // Optional<RememberMe> updateBySeries(RememberMe rememberMe);
}

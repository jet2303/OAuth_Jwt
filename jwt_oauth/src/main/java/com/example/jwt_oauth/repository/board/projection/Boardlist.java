package com.example.jwt_oauth.repository.board.projection;

import java.time.LocalDateTime;

public interface Boardlist {

    Long getId();
    String getTitle();
    // String getName();
    LocalDateTime getCreatedDate();
    
}

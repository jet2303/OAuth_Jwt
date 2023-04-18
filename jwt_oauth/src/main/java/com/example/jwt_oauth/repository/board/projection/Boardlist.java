package com.example.jwt_oauth.repository.board.projection;

import java.time.LocalDateTime;
import java.util.List;

import com.example.jwt_oauth.domain.board.FileInfo;

public interface Boardlist {

    Long getId();
    String getEmail();
    String getUserName();
    String getTitle();
    String getContent();

    LocalDateTime getCreatedDate();
    String getCreatedBy();
    LocalDateTime getModifiedDate();
    String getModifiedBy();
    List<FileInfo> getFileInfoList();
    
}

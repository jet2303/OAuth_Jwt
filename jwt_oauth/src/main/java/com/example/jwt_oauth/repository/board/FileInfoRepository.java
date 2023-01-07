package com.example.jwt_oauth.repository.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.jwt_oauth.domain.board.FileInfo;

@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, Long>{
    
}

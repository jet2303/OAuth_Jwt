package com.example.jwt_oauth.repository.board;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.jwt_oauth.domain.board.BoardInfo;
import com.example.jwt_oauth.domain.board.FileInfo;

@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo, Long>{
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM FileInfo WHERE boardInfo=:boardInfo")
    void deleteByBoardInfo(@Param("boardInfo") BoardInfo boardInfo);
    
}

package com.example.jwt_oauth.repository.board;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.jwt_oauth.domain.board.BoardInfo;

@Repository
public interface BoardRepository extends JpaRepository<BoardInfo, Long>{
    
    Optional<BoardInfo> findById(Long id);
}

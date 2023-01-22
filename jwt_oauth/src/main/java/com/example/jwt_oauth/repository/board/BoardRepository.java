package com.example.jwt_oauth.repository.board;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.jwt_oauth.domain.board.BoardInfo;
import com.example.jwt_oauth.domain.board.BoardStatus;
import com.example.jwt_oauth.repository.board.projection.Boardlist;

@Repository
public interface BoardRepository extends JpaRepository<BoardInfo, Long>{
    
    Optional<BoardInfo> findById(Long id);

    Optional<Page<Boardlist>> findByBoardStatus(BoardStatus boardStatus, Pageable pageable);

    Optional<List<Boardlist>> findByBoardStatus(BoardStatus boardStatus);
}

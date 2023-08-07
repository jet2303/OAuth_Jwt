package com.example.jwt_oauth.repository.board;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.NotBlank;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.jwt_oauth.domain.board.BoardInfo;
import com.example.jwt_oauth.domain.board.BoardStatus;
import com.example.jwt_oauth.domain.dto.BoardInfoDto;
import com.example.jwt_oauth.domain.dto.FileInfoDto;
import com.example.jwt_oauth.repository.board.projection.Boardlist;

import lombok.NonNull;

@Repository
public interface BoardRepository extends JpaRepository<BoardInfo, Long>{
    
    Optional<BoardInfo> findById(Long id);

    Optional<Page<Boardlist>> findByBoardStatus(BoardStatus boardStatus, Pageable pageable);

    Optional<List<Boardlist>> findByBoardStatus(BoardStatus boardStatus);


    @Query(value = "SELECT b "
                +    "FROM BoardInfo b "
                +    "JOIN FETCH b.fileInfoList "
                +   "WHERE b.id=:id")
    Optional<BoardInfo> readQuery(@Param(value = "id") Long id);

    
    @Query(value = "SELECT new com.example.jwt_oauth.domain.dto.BoardListDto(b.id, b.email, b.userName, b.title, b.content, "
                    +"b.createdDate, b.createdBy, b.modifiedDate, b.modifiedBy) "
                    +"FROM BoardInfo b ")
    Optional<List<Boardlist>> getBoardList();

    // new com.example.jwt_oauth.domain.dto.BoardInfoDto(b.title, b.content, b.boardStatus, b.fileInfoDto)
    
    // @Query(value = "SELECT b "
    //                 +"FROM BoardInfo b "
    //                 +"JOIN FETCH b.fileInfoList "
    //                +"WHERE b.id=:id")
    // @Query(value = "SELECT new com.example.jwt_oauth.domain.dto.BoardInfoDto( "
    //                         +"b.id, b.email, b.userName, b.title, "
    //                         +"b.content, b.boardStatus, b.fileInfoList) "
    //                 +"FROM BoardInfo b "
    //                 +"JOIN FETCH b.fileInfoList "
    //                +"WHERE b.id=:id")
    // Optional<BoardInfoDto> findBoardInfo(@Param(value = "id") Long id);

    @Query(value = "SELECT id, email, userName, title, content, boardStatus FROM BoardInfo")
    List<BoardInfo> getboard();

}

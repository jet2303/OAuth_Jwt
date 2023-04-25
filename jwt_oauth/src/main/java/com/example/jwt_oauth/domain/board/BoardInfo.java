package com.example.jwt_oauth.domain.board;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
    * @date : 2023-01-23 
    * @author : AJS
    * @Description: 작성자 이름 추가

    * @date : 2023-03-27 
    * @author : AJS
    * @Description: 작성자 email 추가, 게시글 삭제시 작성자 검증필요
    **/

@Entity
@Getter
@Setter
// @ToString(exclude = "fileInfoList")
@ToString
// @DynamicUpdate   //변경된 필드만 업데이트
public class BoardInfo extends BaseEntity{
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @NotBlank
    private String email;

    @NotBlank
    private String userName;

    @NotBlank
    private String title;

    private String content;

    @Enumerated(EnumType.STRING)
    private BoardStatus boardStatus;

    
    // @OneToMany(mappedBy = "boardInfo",orphanRemoval = true, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OneToMany(mappedBy = "boardInfo",orphanRemoval = true, fetch = FetchType.EAGER)
    private List<FileInfo> fileInfoList = new ArrayList<>();


    public static class BoardInfoBuilder{
        private Long id;

        private String email;

        private String userName;
        
        private String title;

        private String content;

        private BoardStatus boardStatus;

        private List<FileInfo> fileInfoList = new ArrayList<>();

        // public BoardInfoBuilder builder(){
        //     return this;
        // }

        public BoardInfoBuilder id(Long id){
            this.id = id;
            return this;
        }

        public BoardInfoBuilder userName(String userName){
            this.userName = userName;
            return this;
        }

        public BoardInfoBuilder title(String title){
            this.title = title;
            return this;
        }

        public BoardInfoBuilder content(String content){
            this.content = content;
            return this;
        }

        public BoardInfoBuilder boardStatus(BoardStatus boardStatus){
            this.boardStatus = boardStatus;
            return this;
        }

        public BoardInfoBuilder fileInfoList(List<FileInfo> fileInfoList){
            this.fileInfoList.addAll(fileInfoList);
            return this;
        }

        public BoardInfoBuilder email(String email){
            this.email = email;
            return this;
        }

        public BoardInfo build(){
            return new BoardInfo(this);
        }
    }

    public BoardInfo(BoardInfoBuilder boardInfoBuilder){
        this.id = boardInfoBuilder.id;
        this.email = boardInfoBuilder.email;
        this.userName = boardInfoBuilder.userName;
        this.title = boardInfoBuilder.title;
        this.content = boardInfoBuilder.content;
        this.boardStatus = boardInfoBuilder.boardStatus;
        this.fileInfoList = boardInfoBuilder.fileInfoList;
    }

    public BoardInfo(){}
}

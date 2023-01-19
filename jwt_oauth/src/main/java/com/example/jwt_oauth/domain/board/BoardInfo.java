package com.example.jwt_oauth.domain.board;

import java.util.ArrayList;
import java.util.List;

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
import lombok.ToString;

@Entity
@Getter
// @ToString(exclude = "fileInfoList")
@ToString
public class BoardInfo extends BaseEntity{
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    // 작성자 추가
    // @NotBlank
    // private String name;

    private String content;

    @Enumerated(EnumType.STRING)
    private BoardStatus boardStatus;

    @OneToMany(mappedBy = "boardInfo", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<FileInfo> fileInfoList = new ArrayList<>();

    public static class BoardInfoBuilder{
        private Long id;
        
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

        public BoardInfo build(){
            return new BoardInfo(this);
        }
    }

    public BoardInfo(BoardInfoBuilder boardInfoBuilder){
        this.title = boardInfoBuilder.title;
        this.content = boardInfoBuilder.content;
        this.boardStatus = boardInfoBuilder.boardStatus;
        this.fileInfoList = boardInfoBuilder.fileInfoList;
    }

    public BoardInfo(){}
}

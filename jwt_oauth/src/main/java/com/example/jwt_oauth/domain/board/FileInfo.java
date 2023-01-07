package com.example.jwt_oauth.domain.board;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class FileInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String filePath;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private BoardInfo boardInfo;

    public FileInfo(String fileName, String filePath){
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public FileInfo(FileInfoBuilder builder){
        this.fileName = builder.fileName;
        this.filePath = builder.filePath;
        this.boardInfo = builder.boardInfo;
    }
    
    public static class FileInfoBuilder{
        private String fileName;

        private String filePath;

        private BoardInfo boardInfo;

        public FileInfoBuilder fileName(String fileName){
            this.fileName = fileName;
            return this;
        }

        public FileInfoBuilder filePath(String filePath){
            this.filePath = filePath;
            return this;
        }

        public FileInfoBuilder boardInfo(BoardInfo boardInfo){
            this.boardInfo = boardInfo;
            return this;
        }
        public FileInfo build(){
            return new FileInfo(this);
        }
    }    
}

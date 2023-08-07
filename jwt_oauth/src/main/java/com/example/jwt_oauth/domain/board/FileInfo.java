package com.example.jwt_oauth.domain.board;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString(exclude = "boardInfo")
@Builder
public class FileInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="file_id")
    private Long id;

    private String fileName;

    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardInfo boardInfo;

    public FileInfo(String fileName, String filePath){
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public FileInfo(FileInfoBuilder builder){
        this.fileName = builder.fileName;
        this.filePath = builder.filePath;
    }

    public FileInfo(){}
    
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

    // public void setBoardInfo(BoardInfo boardInfo){
    //     this.boardInfo = boardInfo;

    //     // boardInfo.getFileInfoList().forEach(file -> file.setBoardInfo(boardInfo));
        
        
    //     // if(!boardInfo.getFileInfoList().contains(this)){
    //     //     // boardInfo.getFileInfoList().stream().map( file -> boardInfo.addFiles(file));
    //     //     boardInfo.getFileInfoList().forEach( file -> boardInfo.addFiles(file));
    //     // }
    // }
}

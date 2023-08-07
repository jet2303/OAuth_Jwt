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

import org.hibernate.annotations.DynamicUpdate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @date : 2023-01-23
 * @author : AJS
 * @Description: 작성자 이름 추가
 * 
 * @date : 2023-03-27
 * @author : AJS
 * @Description: 작성자 email 추가, 게시글 삭제시 작성자 검증필요
 **/

@Entity
@Getter
@Setter
// @ToString(exclude = "fileInfoList")

@DynamicUpdate // 변경된 필드만 업데이트
@AllArgsConstructor
@Builder
@Accessors(chain = true)
public class BoardInfo extends BaseEntity {

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

    // @OneToMany(mappedBy = "boardInfo",orphanRemoval = true, fetch =
    // FetchType.EAGER, cascade = CascadeType.ALL)
    @Builder.Default
    @OneToMany(mappedBy = "boardInfo", cascade = CascadeType.ALL)
    private List<FileInfo> fileInfoList = new ArrayList<>();

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        String toString = "[ "
                + this.id + " "
                + this.userName + " "
                + this.email + " "
                + this.title + " "
                + this.content + " "
                + this.boardStatus + " "
                + "]";
        return toString;
    }

    public BoardInfo() {
    }

    public void updateFile(List<FileInfo> fileList) {
        if (this.fileInfoList != null) {
            this.fileInfoList.removeAll(this.fileInfoList);
        }
        fileList.forEach(updateFile -> fileInfoList.add(updateFile));
    }

    public void addFile(FileInfo file) {
        this.fileInfoList.add(file);
        file.setBoardInfo(this);
    }

    public void addFiles(List<FileInfo> files) {
        this.fileInfoList = files;
    }
}

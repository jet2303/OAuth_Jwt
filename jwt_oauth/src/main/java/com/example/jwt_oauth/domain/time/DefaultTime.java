package com.example.jwt_oauth.domain.time;

import java.time.LocalDateTime;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;

@Getter
// @MappedSuperclass 어노테이션으로 hibernate 에서 테이블 생성시 상속받는 클래스도 mapping 해줘서 같이 컬럼을 생성해줌
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class DefaultTime {

    
    @CreatedDate
    private LocalDateTime createdDate;
    // private LocalDateTime created_date;

    @LastModifiedDate
    private LocalDateTime modifiedDate;
    // private LocalDateTime modified_date;

    // @CreatedBy
    // private String createdBy;

    // @LastModifiedBy
    // private String modifiedBy;
    
}

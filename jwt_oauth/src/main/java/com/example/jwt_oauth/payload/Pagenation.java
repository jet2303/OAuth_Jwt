package com.example.jwt_oauth.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Pagenation{

    private int totalPages;

    private long totalElements;

    private int currentPage;

    private int currentElements;

    public Pagenation(PagenationBuilder pagenationBuilder){
        this.totalPages = pagenationBuilder.totalPages;
        this.totalElements = pagenationBuilder.totalElements;
        this.currentPage = pagenationBuilder.currentPage;
        this.currentElements = pagenationBuilder.currentElements;
    }

    public static class PagenationBuilder{
        private int totalPages;
        private long totalElements;
        private int currentPage;
        private int currentElements;

        public PagenationBuilder Builder(){
            return this;
        }

        public PagenationBuilder totalPages(int totalPages){
            this.totalPages = totalPages;
            return this;
        }
    
        public PagenationBuilder totalElements(long totalElements){
            this.totalElements = totalElements;
            return this;
        }
    
        public PagenationBuilder currentPage(int currentPage){
            this.currentPage = currentPage;
            return this;
        }
    
        public PagenationBuilder currentElements(int currentElements){
            this.currentElements = currentElements;
            return this;
        }

        public Pagenation build(){
            return new Pagenation(this);
        }
    }
}
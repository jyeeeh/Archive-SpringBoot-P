package com.sloop.archive.common.pagination;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor

public class SearchDTO {
    private int page; // 현재 페이지
    private int maxPage; // 전체 페이지
    private int beginningPage; // 현재 페이지 기준 시작 페이지
    private int endingPage; // 현재 페이지 기준 마지막 페이지
    private int offset; // sql limit-offset

    static public int postsPerPage = 10; // 1 페이지 당 글의 개수는 10
    static public int pageLimit = 5; // 한 페이지에 보여지는 페이지는 5개. 즉, << < 12345 > >>

    private Long contentId; // 콘텐츠 id
    private int searchType; // 검색 유형
    private String searchWord; // 검색어
    private String savedPath; // 저장된 경로


    public SearchDTO(int page , int searchType, String searchWord) {
        this.page = page;
        this.searchType = searchType;
        this.searchWord = searchWord;
    }
}

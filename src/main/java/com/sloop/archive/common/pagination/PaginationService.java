package com.sloop.archive.common.pagination;

import com.sloop.archive.content.mapper.ContentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaginationService {

    private final ContentMapper contentMapper;


    public SearchDTO initialize(int page , int searchType, String searchWord){

        // 콘텐츠 id , 현재 페이지 , 검색 유형 , 검색어 초기화 ( 인스턴스 생성 )
        SearchDTO searchDTO = new SearchDTO(page, searchType, searchWord);

        // 전체 글 개수
        int numOfContents = contentMapper.searchAndCountContentsById(searchDTO);
        log.info("==== 전체 게시글 수 === " + numOfContents);
        // 전체 페이지 수 = ( 전체 글 개수 / 1 페이지 당 글 개수)
        int maxPage = (int)Math.ceil((double) numOfContents / searchDTO.postsPerPage);
        searchDTO.setMaxPage(maxPage);

        // 시작 페이지 = ((현재 페이지 / 페이징 개수)의 올림 - 1) * 페이징 개수 + 1)
        int begginingPage = ((int)(Math.ceil((double)page / searchDTO.pageLimit)) - 1) * searchDTO.pageLimit + 1;
        searchDTO.setBeginningPage(begginingPage);

        // 마지막 페이지
        int endingPage = begginingPage + searchDTO.postsPerPage - 1;
        if (endingPage > maxPage){
            endingPage = maxPage;
        }
        searchDTO.setEndingPage(endingPage);
        log.info("=== searchService === :::: " + searchDTO );
        return searchDTO;
    }
}

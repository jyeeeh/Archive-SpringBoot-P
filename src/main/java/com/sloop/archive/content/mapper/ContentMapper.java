package com.sloop.archive.content.mapper;

import com.sloop.archive.common.pagination.SearchDTO;
import com.sloop.archive.content.domain.ContentDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface ContentMapper {


    List<ContentDTO> findAllList();

    Long regist(ContentDTO contentDTO);

    ContentDTO findById(Long contentId);

    int update(ContentDTO contentDTO);

    void delete(Long contentId);

    // 게시물 검색 + 페이징 전체글 수
    int searchAndCountContentsById(SearchDTO contentId);

    // 게시물 검색 + 페이징 리스트
    ArrayList<ContentDTO> getAllListPagingbyId(SearchDTO searchDTO);


    // ==============================================================
    // 이하 파일 업로드 추가 내용 적으면서 기존 매퍼 수정

    // 미승인 콘텐츠 매퍼
    ArrayList<ContentDTO> getNotApprAllListPagingbyId(SearchDTO searchDTO);


    int approve(ContentDTO contentDTO);

    int notApprove(ContentDTO contentDTO);


    ContentDTO findContentsAndImageByContentId(Long contentId);

    Long findByFilename(String filename);
}

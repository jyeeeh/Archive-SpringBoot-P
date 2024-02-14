package com.sloop.archive.content.service;

import com.sloop.archive.common.pagination.SearchDTO;
import com.sloop.archive.content.domain.ContentDTO;
import com.sloop.archive.content.mapper.ContentMapper;
import com.sloop.archive.content_category.mapper.ContentCategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {

    private final ContentMapper contentMapper;
    private final ContentCategoryMapper contentCategoryMapper;


    public List<ContentDTO> findAll() {
        return contentMapper.findAllList();
    }

    /**
     * 게시글 저장
     * @param contentDTO
     * @return
     */
    @Transactional
    public Long registContent(ContentDTO contentDTO) {

        log.info("서비스단에서의 디티오 정보"+contentDTO);

        Long contentId = contentMapper.regist(contentDTO);

        if(contentId != null && contentId > 0){ // 등록에 성공한 경우 카테고리를 등록한다.
            contentCategoryMapper.insertContentCategory(contentDTO.getCategoryId(), contentDTO.getId());
        }

        return contentId;
    }

    public ContentDTO findById(Long contentId) {
        return contentMapper.findById(contentId);
    }

    public boolean update(ContentDTO contentDTO) {
        log.info("서비스단에서의 업데이트 정보"+contentDTO);
        int result = contentMapper.update(contentDTO);

        // 카테고리 업데이트
        contentCategoryMapper.updateContentCategory(contentDTO.getCategoryId(), contentDTO.getContentId());

        if (result > 0 ){
            return true;
        }
        return false;

    }

    public void delete(Long contentId) {
        contentMapper.delete(contentId);
    }


    /**
     * 콘텐츠 전체 리스트 페이징
     * @param searchDTO
     * @return
     */
    public ArrayList<ContentDTO> findAllContentsList(SearchDTO searchDTO) {
        searchDTO.setOffset((searchDTO.getPage() - 1) * searchDTO.postsPerPage);
        List<ContentDTO> contentDTOList = contentMapper.getAllListPagingbyId(searchDTO);

        // List -> ArrayList
        ArrayList<ContentDTO> contentDTOArrayList = new ArrayList<>(contentDTOList);
        return contentDTOArrayList;
    }

    /**
     * 미승인 콘텐츠 전체 리스트 페이징
     * @param searchDTO
     * @return
     */
    public ArrayList<ContentDTO> findNotApprAllContentsList(SearchDTO searchDTO) {
        searchDTO.setOffset((searchDTO.getPage() - 1) * searchDTO.postsPerPage);
        List<ContentDTO> contentDTOList = contentMapper.getNotApprAllListPagingbyId(searchDTO);

        // List -> ArrayList
        ArrayList<ContentDTO> contentDTOArrayList = new ArrayList<>(contentDTOList);
        return contentDTOArrayList;
    }


    public int approve(ContentDTO contentDTO) {
      return contentMapper.approve(contentDTO);
    }


    public ContentDTO findContentsAndImageByContentId(Long contentId) {
        return contentMapper.findContentsAndImageByContentId(contentId);
    }

    public Long findByFilename(String filename) {
        return contentMapper.findByFilename(filename);
    }

}

package com.sloop.archive.content_category.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ContentCategoryMapper {
    // 콘텐츠 등록 시, tb_content_category 테이블에 등록
    void insertContentCategory(@Param("categoryId") Long categoryId, @Param("contentId") Long id);
    // 콘텐츠 수정 시, tb_content_category 테이블에 수정
    void updateContentCategory(@Param("categoryId") Long categoryId, @Param("contentId") Long id);
}

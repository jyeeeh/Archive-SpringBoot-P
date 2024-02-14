package com.sloop.archive.category.domain;

import lombok.Data;

@Data
public class Category_ContentDTO {
    private Long id;            // 콘텐츠_카테고리 아이디
    private Long categoryId;    // 카테고리 아이디
    private Long contentId;     // 콘텐츠 아이디
}

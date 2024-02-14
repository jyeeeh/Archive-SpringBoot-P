package com.sloop.archive.category.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

@Data
// @JsonAutoDetect
@AllArgsConstructor
@NoArgsConstructor
@Alias("category")
public class CategoryDTO {
    private Long id;            // 카테고리 아이디
    private Long parentId;      // 상위 카테고리 아이디
    private String name;        // 카테고리 이름
    private Integer showFlag;   // 사용여부. 사용 1. 미사용 0
    private Integer depth;      // 깊이
    private Integer orders;     // 같은 깊이 내 순서
    private Integer updateFlag; // 수정여부. 수정 1. 수정 없음 0
    private Long userId;     // 수정한 회원 아이디

    private String parentName;  // 상위 카테고리 이름
    private Integer countNumber;    // 개수


    // 생성자
    public CategoryDTO(Long id){
        this.id = id;
    }
}

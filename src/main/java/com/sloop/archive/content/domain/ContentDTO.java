package com.sloop.archive.content.domain;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Validated
public class ContentDTO {
    private Long id;
    private Long userId;

    private int type;
    private int charge;
    private int view;
    @NotBlank (message = "제목을 입력하세요.")
    private String title;
    @NotBlank (message = "키워드를 입력하세요.")
    private String keyword;
    

    @NotBlank(message = "내용을 입력하세요.")
    private String description;
    private Timestamp registerDate;
    private int updateFlag;
    private Timestamp updateDate;
    private int deleteFlag;
    private Timestamp deleteDate;
    private int approveFlag;
    private Long approveUserId;

    // FileDTO
    private Long contentId;
    private String extension;
    @NotNull
    private String originalName;
    private String savedName;
    private String thumbName;
    private String originalPath;
    private String savedPath;
    private String thumbPath;

    // 파일 DTO 객체
    private List<MultipartFile> files =new ArrayList<>();

    // 카테고리 id
    private Long categoryId;
}

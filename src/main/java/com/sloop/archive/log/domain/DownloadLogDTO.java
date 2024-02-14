package com.sloop.archive.log.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@Builder
public class DownloadLogDTO {
    private Long id;             // 다온로드수id
    private Long user_id;        // 회원id
    private Long content_id;     // 콘텐츠id
    private Timestamp log_timestamp; // 로그날짜

    //Chart.js 필요
    private String date; // 날짜 형식
    private int downloadCount; // 다운로드 수
}

package com.sloop.archive.log.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@Builder
public class AccessLogDTO {
    private Long id;            // 접속로그id
    private Long user_id;       // 회원id
    private int login_flag;     // 로그인 성공여부 default 1(성공), 0(실패)
    private Timestamp log_timestamp; // 로그날짜

    //Chart.js 필요
    private String date; // 날짜 형식
    private int visitCount; // 조회수
}

package com.sloop.archive.user.domain;

import lombok.Getter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@ToString
public class UserInfo {
    // 사용자 관리 리스트 뷰에 출력될 객체 정보를 담은 클래스

    // UserDTO
    private Long id;
    private String userid;
    private String name;

    // UserRoleDTO
    private String role;

    // UserLogDTO
    private Long downloadCount;
    private Long approveCount;
    private Timestamp latestLogin;
}

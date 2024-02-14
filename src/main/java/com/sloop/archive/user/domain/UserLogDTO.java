package com.sloop.archive.user.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Timestamp;

@Getter
@Setter
@ToString
@Builder
public class UserLogDTO {
    private Long userId;
    private Long downloadCount;
    private Long approveCount;
    private Timestamp latestLogin;
}
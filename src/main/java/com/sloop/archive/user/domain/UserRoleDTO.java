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
public class UserRoleDTO {
    private Long id;
    private Long userId;
    private String role;
}
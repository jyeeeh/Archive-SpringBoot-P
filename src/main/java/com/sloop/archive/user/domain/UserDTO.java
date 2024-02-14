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
public class UserDTO {
    private Long id;
    private String userid;
    private String password;
    private String name;
    private String email;
    private String phone;
    private Timestamp registerDate;
    private int updateFlag;
    private Timestamp updateDate;
    private int deleteFlag;
    private Timestamp deleteDate;
}
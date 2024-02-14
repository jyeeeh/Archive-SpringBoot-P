package com.sloop.archive.login.mapper;

import com.sloop.archive.login.request.AdminLoginRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;

@Mapper
public interface LoginMapper {
    HashMap<String, Object> requestAdminLogin(@Param("request") AdminLoginRequest request);
}

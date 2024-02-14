package com.sloop.archive.user.mapper;

import com.sloop.archive.user.domain.*;
import com.sloop.archive.user.request.UserSearchRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    int registerUser(UserDTO userDTO);

    int registerUserRole(UserRoleDTO userRoleDTO);

    int registerUserLog(Long id);

    List<UserInfo> getAllUserInfo();

    int checkId(String id);

    UserForm getUserById(Long id);

    int updateUser(@Param("request")UserUpdateRequest request, @Param("id") Long id);
    int updateUserRole(@Param("role") String role, @Param("id") Long id);

    List<UserInfo> getUserInfoListByUserSearchRequest(@Param("request") UserSearchRequest request);
}

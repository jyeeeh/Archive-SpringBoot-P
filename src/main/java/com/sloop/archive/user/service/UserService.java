package com.sloop.archive.user.service;

import com.sloop.archive.sample.domain.BoardDTO;
import com.sloop.archive.sample.mapper.SampleMapper;
import com.sloop.archive.user.domain.*;
import com.sloop.archive.user.mapper.UserMapper;
import com.sloop.archive.user.request.CheckIdRequest;
import com.sloop.archive.user.request.UserSearchRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserMapper userMapper;

    /**
     * 사용자 등록 화면에서 입력한 폼 데이터를 DTO에 따라 분리 및 DB 저장
     * 사용자를 등록할 때 총 3개의 테이블에 데이터를 insert(tb_user, tb_user_role, tb_user_log)
     * @param userForm 사용자 등록 화면 입력 폼 데이터
     * @return 성공 1, 실패 0
     */
    public int registerUserFormData(UserForm userForm) {
        int result = 0;
        // userForm 객체에서 userDTO에 해당하는 데이터 추출하여 UserDTO에 build
        UserDTO userDTO = UserDTO.builder()
                .userid(userForm.getUserid())
                .password(userForm.getPassword())
                .name(userForm.getName())
                .email(userForm.getEmail())
                .phone(userForm.getPhone())
                .build();
        result = userMapper.registerUser(userDTO);

        // userForm 객체에서 userRoleDTO에 해당하는 데이터 추출하여 userRoleDTO에 build
        UserRoleDTO userRoleDTO = UserRoleDTO.builder()
                .userId(userDTO.getId())    // userDTO데이터 추가한 이후 생성된 id값
                .role(userForm.getRole())
                .build();
        result = userMapper.registerUserRole(userRoleDTO);
        result = userMapper.registerUserLog(userDTO.getId());
        return result;
    }

    /**
     * 사용자 관리 리스트 화면에 보여질 정보 조회
     * @return 사용자 관리 리스트에 최적화된 UserInfo 클래스로 이루어진 리스트
     */
    public List<UserInfo> getAllUserInfo() {
        return userMapper.getAllUserInfo();
    }

    /**
     * 사용자 등록 시 회원 아이디(userid) 중복확인
     * @param request 아이디를 포함하는 객체
     * @return 중복 없는 경우 false, 중복 있는 경우 true
     */
    public boolean checkId(@Valid CheckIdRequest request) {
        //해당 부분은 flag로 하셔도 좋고 exception으로 진행하셔도 좋습니다
        int result = userMapper.checkId(request.getId());
        if(result > 0)
            return false;
        else
            return true;
    }

    /**
     * 회원 조회, 수정시 정보 불러오기
     * @param id 회원 식별자 id
     * @return tb_user DTO
     */
    public UserForm getUserById(Long id) {
        return userMapper.getUserById(id);
    }

    public int updateUser(UserUpdateRequest request, Long id) {
        log.info("validation 통과 = {}", request);
        userMapper.updateUser(request, id);
        return userMapper.updateUserRole(request.getRole(), id);
    }

    public int updateUserRoleById(Long id, String role) {
        return userMapper.updateUserRole(role, id);
    }


    public List<UserInfo> getUserInfoListByUserSearchRequest(UserSearchRequest request) {
        return userMapper.getUserInfoListByUserSearchRequest(request);
    }
}

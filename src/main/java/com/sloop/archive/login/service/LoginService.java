package com.sloop.archive.login.service;

import com.sloop.archive.log.domain.AccessLogDTO;
import com.sloop.archive.log.mapper.LogMapper;
import com.sloop.archive.login.mapper.LoginMapper;
import com.sloop.archive.login.request.AdminLoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService {

    private final LoginMapper loginMapper;

    /**
     * 접속 시도 여부 용도
     */
    private final LogMapper logMapper;

    /**
     * 관리자 로그인을 시도하는 경우 실행
     * @param request
     * @return 로그인 시도 결과 HashMap
     */
    public HashMap<String, Object> requestAdminLogin(AdminLoginRequest request) {
        log.info(String.valueOf(request));
        HashMap<String, Object> loginResult = loginMapper.requestAdminLogin(request);
        // 로그
        AccessLogDTO accessLogDTO;

        if (loginResult == null) { // 해당하는 회원 ID + 비밀번호가 없는 경우

            return null;
            //loginResult = (HashMap<String, Object>) new HashMap<>().put("flag", false);
        }
        else {  // 해당하는 회원 ID + 비밀번호가 없는 경우
            /**
             * 로그인 성공 시 tb_user 테이블의 id 값, login_flag에 1 값 설정
             */
            log.info(String.valueOf(loginResult.get("id")));
            accessLogDTO = AccessLogDTO.builder()
                    .user_id((Long) loginResult.get("id"))
                    .login_flag(1)
                    .build();
            insertAccessLog(accessLogDTO);
            log.info("로그인 성공 로그 찍기"+accessLogDTO);

            if (loginResult.get("role").equals("ROLE_USER")) { // 일반 회원일 경우 관리자 페이지 접근 금지(일반 사용자 페이지로)
                log.info("일반 회원입니다.");
                loginResult.put("adminAccess", false);
            } else { // 스탭이나 관리자 일경우
                log.info("스탭이나 관리자입니다.");
                loginResult.put("adminAccess", true);
            }
            return loginResult; // id, name, role, adminAccess
        }
    }

    //접속 Log
    private void insertAccessLog(AccessLogDTO accessLogDTO){
        logMapper.insertAccessLog(accessLogDTO);
    }
}

package com.sloop.archive.login.controller;

import com.sloop.archive.login.request.AdminLoginRequest;
import com.sloop.archive.login.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;

@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LoginService loginService;

    /**
     * 관리자 로그인 폼 화면 요청 처리
     * @return
     */
    @GetMapping({"/admin","/admin/"})
    public String adminLoginForm(){
        return "login/admin_login";
    }

    /**
     * 관리자 로그인 폼 제출
     * @param request userid, password 포함 request 객체
     * @param session 로그인 세션 생성
     * @return 'ROLE_STAFF' 또는 'ROLE_ADMIN' 일 경우 관리자 페이지 redirect, 아닐 경우 접속 거부 페이지
     */
    @PostMapping("/admin/login")
    public String requestAdminLogin(AdminLoginRequest request, HttpSession session, Model model){
        HashMap<String, Object> loginResult = loginService.requestAdminLogin(request);
        // log.info(String.valueOf(loginResult.size()));
        if (loginResult==null) { // 로그인 실패
            model.addAttribute("loginFlag", "false");
            model.addAttribute("site", "admin");
            return "login/admin_login";
        }
        else {
            if (loginResult.get("adminAccess").equals(false)) { // 일반 회원일 경우
                return "redirect:/";
            }
            else {
                session.setAttribute("loginId", loginResult.get("id"));
                session.setAttribute("loginName", loginResult.get("name"));
                session.setAttribute("loginRole", loginResult.get("role"));
                return "redirect:/admin/content/list";
            }
        }
    }

    @GetMapping("/admin/logout")
    public String adminLogout(HttpServletRequest request) {
        //세션을 삭제
        HttpSession session = request.getSession(false);
        // session이 null이 아니라는건 기존에 세션이 존재했었다는 뜻이므로
        // 세션이 null이 아니라면 session.invalidate()로 세션 삭제해주기.
        if(session != null) {
            session.invalidate();
        }
        return "redirect:/admin";
    }

    /**
     * 사용자 로그인 폼 화면 요청 처리
     * @return
     */
    @GetMapping("/login")
    public String userLoginForm(){
        return "login/login";
    }

    @PostMapping("/login")
    public String requestUserLogin(AdminLoginRequest request, HttpSession session, Model model){
        HashMap<String, Object> loginResult = loginService.requestAdminLogin(request);
        log.info(String.valueOf(loginResult));
        if (loginResult==null) { // 로그인 실패
            model.addAttribute("loginFlag", "false");
            model.addAttribute("site", "user");
            return "login/login";
        }
        else {
            session.setAttribute("loginId", loginResult.get("id"));
            session.setAttribute("loginName", loginResult.get("name"));
            session.setAttribute("loginRole", loginResult.get("role"));
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        //세션을 삭제
        HttpSession session = request.getSession(false);
        // session이 null이 아니라는건 기존에 세션이 존재했었다는 뜻이므로
        // 세션이 null이 아니라면 session.invalidate()로 세션 삭제해주기.
        if(session != null) {
            session.invalidate();
        }
        return "redirect:/";
    }



}

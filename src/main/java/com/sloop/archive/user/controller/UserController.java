package com.sloop.archive.user.controller;

import com.sloop.archive.user.domain.*;
import com.sloop.archive.user.request.CheckIdRequest;
import com.sloop.archive.user.request.UserSearchRequest;
import com.sloop.archive.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/list")
    public String requestUserListPage(Model model){
        List<UserInfo> userInfoList = userService.getAllUserInfo();
        model.addAttribute("userInfoList", userInfoList);
        return "user/list";
    }

//    @PostMapping("/list")
//    @ResponseBody
//    public List<UserInfo> requestUserList(Model model){
//        List<UserInfo> userInfoList = userService.getAllUserInfo();
//        model.addAttribute("userInfoList", userInfoList);
//        return userInfoList;
//    }

    @PostMapping("/search")
    @ResponseBody
    public List<UserInfo> requestSearch(UserSearchRequest request,
                                Model model){
        log.info("request : "+request.getSearchWord()+request.getSearchType()+request.getSearchRole());
        List<UserInfo> userInfoList = userService.getUserInfoListByUserSearchRequest(request);
        log.info(String.valueOf(userInfoList.size()));
        model.addAttribute("userInfoList", userInfoList);
        return userInfoList;
    }

    @GetMapping("/regist")
    public String requestUserRegistForm(Model model){
        //List<UserDTO> userList = userService.getAllUserList();
        //model.addAttribute("userList", userList);
        return "user/register";
    }

    @PostMapping("/regist")
    public String registerUserFormData(@Valid UserForm userForm, Model model){
        int result = 0;
        result = userService.registerUserFormData(userForm);
        if (result > 0){
            return "redirect:/admin/user/list";
        } else {
            return "user/register";
        }
    }

    @GetMapping("/check/id")
    @ResponseBody
    public boolean checkId(@ModelAttribute @Valid CheckIdRequest request, Model model) {
        return userService.checkId(request);
    }

    @GetMapping("/detail")
    public String requestUserDetail(@RequestParam Long id, Model model){
        UserForm userForm= userService.getUserById(id);
        model.addAttribute("id", id);
        model.addAttribute("user", userForm);
        return "user/detail";
    }

    @GetMapping("/edit")
    public String requestUserUpdateForm(@RequestParam Long id, Model model){
        UserForm userForm= userService.getUserById(id);
        model.addAttribute("id", id);
        model.addAttribute("user", userForm);
        return "user/edit";
    }

    @PostMapping("/edit")
    public String updateUserForm(@RequestParam Long id, @Valid UserUpdateRequest request, Model model, HttpSession session){
        int result = 0;
        log.info(String.valueOf(request));
        result = userService.updateUser(request, id);
        if (result > 0){
            if (session.getAttribute("loginId").equals(id)) {
                session.setAttribute("loginRole", request.getRole());
            }
            return "redirect:/admin/user/list";
        } else {
            return "user/edit";
        }
    }

    @PostMapping("/edit_role")
    @ResponseBody
    public String updateUserRole(@RequestParam Long id, @RequestParam String role, Model model, HttpSession session){
        int result = 0;
        result = userService.updateUserRoleById(id, role);

        if(result>0) {
            if (session.getAttribute("loginId").equals(id)) {
                session.setAttribute("loginRole", role);
            }
            return "true";
        }
        else
            return "false";
    }


}
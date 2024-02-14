/**
 * 공지사항 컨트롤러
 */

package com.sloop.archive.notice.controller;

import com.fasterxml.jackson.databind.DatabindContext;
import com.sloop.archive.notice.domain.NoticeDTO;
import com.sloop.archive.notice.service.NoticeService;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;


import java.io.*;
import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/notice") // URL을 클래스 레벨에서 설정
public class NoticeController {

    private final NoticeService noticeService;

    @Value("${noticeUploadPath.path}")
    private String uploadPath;

    /**
     * 공지사항 목록을 조회하는 메서드
     * 페이지 번호를 파라미터로 받아 해당 페이지의 공지사항 목록을 가져온다.
     * 가져온 공지사항 목록은 Model에 추가하여 뷰 페이지에 전달한다.
     * 이 메서드는 "/notice/list" 경로로 GET 요청이 들어올 때 호출된다.
     *
     * @param model 뷰 페이지에 전달할 데이터를 담는 객체
     *              - 뷰 페이지에 전달할 데이터
     *              "noticeList": List<NoticeDTO> noticeList - 조회한 공지사항 목록
     *              "totalPages": int totalPages - 전체 페이지 수
     *              "currentPage": int page - 현재 페이지 번호
     *              "totalPostCount": int totalCount - 총 게시물 수
     * @param page 조회할 페이지 번호
     * @return notice/list
     */
    @GetMapping("/list")
    public String getAllNoticePinnedFirst(Model model, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 10; // 페이지 당 게시글 수
        int totalCount = noticeService.getTotalCount(); // 전체 게시글 수

        // 페이징 처리를 위한 계산
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        if (page < 1) {
            page = 1;
        }
        int start = (page - 1) * pageSize;

        log.info("*****" + start + "   " + pageSize );

        List<NoticeDTO> noticeList = noticeService.getNoticeList(start, pageSize);

        log.info("*****" +noticeList);

        model.addAttribute("noticeList", noticeList);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPostCount", totalCount); // 총 게시물 수 추가

        log.info("공지사항 목록 조회"); // 로그 기록 추가
        return "notice/list";
    }

    /**
     * 단일 공지사항을 조회하는 메서드
     * 공지사항의 ID를 경로 변수로 받아 해당 공지사항의 정보를 가져온다.
     * 가져온 공지사항 정보는 Model에 추가하여 뷰 페이지에 전달합니다.
     * 이 메서드는 "/notice/get/{id}" 경로로 GET 요청이 들어올 때 호출된다.
     *
     * @param id 조회할 공지사항의 ID
     * @param model 뷰 페이지에 전달할 데이터를 담는 객체
     * @return notice/view
     */
    @GetMapping("/get/{id}")
    public String getNoticeById(@PathVariable("id") Long id, Model model) {
        model.addAttribute("notice", noticeService.getNoticeById(id));
        log.info("공지사항 상세 조회"); // 로그 기록 추가
        return "notice/view";
    }


    /**
     * 공지사항을 조회하고 조회수를 증가시키는 메서드
     * 공지사항의 ID를 경로 변수로 받아 해당 공지사항의 정보를 가져온 후 조회수를 증가시킨다.
     * 조회수가 증가된 공지사항 정보는 Model에 추가하여 뷰 페이지에 전달한다.
     * 이 메서드는 "/notice/view/{id}" 경로로 GET 요청이 들어올 때 호출된다.
     *
     * @param id 조회할 공지사항의 ID
     * @param model 뷰 페이지에 전달할 데이터를 담는 객체
     * @return notice/view
     */
    @GetMapping("/view/{id}")
    public String view(@PathVariable("id") Long id, Model model) {
        NoticeDTO notice = noticeService.getNoticeAndUpdateViews(id);
        model.addAttribute("notice", notice);
        return "notice/view";
    }

    /**
     * 공지사항 작성 폼을 보여주는 메서드
     * 이 메서드는 "/notice/saveForm" 경로로 GET 요청이 들어올 때 호출된다.
     *
     * @param model 뷰 페이지에 전달할 데이터를 담는 객체
     * @return notice/save
     */
    @GetMapping("/saveForm")
    public String showNoticeForm(Model model) {
        model.addAttribute("notice", new NoticeDTO());
        log.info("공지사항 등록"); // 로그 기록 추가
        return "notice/save";
    }

    /**
     * 새로운 공지사항을 저장하는 메서드
     * 공지사항 정보를 담은 NoticeDTO 객체를 파라미터로 받아 저장한다.
     * 성공적으로 저장되면 공지사항 목록 페이지로 리다이렉트
     * 이 메서드는 "/notice/save" 경로로 POST 요청이 들어올 때 호출된다.
     *
     * @param notice 저장할 공지사항 정보를 담은 NoticeDTO 객체
     * @return notice/save
     */

    @PostMapping("/save")
    public String saveNotice(HttpSession httpSession, NoticeDTO notice, Model model) {
        try {
            // #######################################################################
            // 수정!
            notice.setUserId((Long)httpSession.getAttribute("loginId"));
//            notice.setUserId(1L);
            noticeService.saveNotice(notice);
            return "redirect:/admin/notice/list";  // 글 작성 성공 시 공지사항 목록 페이지로 리다이렉트
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("notice", notice);
            log.info("공지사항 등록"); // 로그 기록 추가
            return "notice/save";  // 글 작성 실패 시 다시 글 작성 페이지로 이동
        }
    }


    /**
     * 공지사항 수정 폼을 보여주는 메서드
     * 공지사항의 ID를 경로 변수로 받아 해당 공지사항의 정보를 가져온다.
     * 가져온 공지사항 정보는 Model에 추가하여 뷰 페이지에 전달
     * 이 메서드는 "/notice/update/{id}" 경로로 GET 요청이 들어올 때 호출된다.
     *
     * @param id 수정할 공지사항의 ID
     * @param model 뷰 페이지에 전달할 데이터를 담는 객체
     * @return notice/update
     */
    @GetMapping("/update/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("notice", noticeService.getNoticeById(id));
        log.info("공지사항 수정"); // 로그 기록 추가
        return "notice/update";
    }

    /**
     * 공지사항을 수정하는 메서드
     * 공지사항의 ID와 수정할 정보를 담은 NoticeDTO 객체를 파라미터로 받아 수정한다.
     * 성공적으로 수정되면 공지사항 목록 페이지로 리다이렉트
     * 이 메서드는 "/notice/update/{id}" 경로로 POST 요청이 들어올 때 호출된다.
     *
     * @param id 수정할 공지사항의 ID
     * @param notice 수정할 정보를 담은 NoticeDTO 객체
     * @return notice/update
     */
    @PostMapping("/update/{id}")
    public String updateNotice(@PathVariable("id") Long id, NoticeDTO notice, Model model, HttpSession httpSession) {
        try {
            notice.setUserId((Long)httpSession.getAttribute("loginId"));
//            notice.setUserId(1L);

            noticeService.updateNotice(notice);
            return "redirect:/admin/notice/list";  // 글 수정 성공 시 공지사항 목록 페이지로 리다이렉트
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("notice", notice);
            log.info("공지사항 수정"); // 로그 기록 추가
            return "notice/update";  // 글 수정 실패 시 다시 글 수정 페이지로 이동
        }
    }

    /**
     * 공지사항을 삭제하는 메서드
     * 공지사항의 ID를 경로 변수로 받아 해당 공지사항을 삭제
     * 성공적으로 삭제되면 공지사항 목록 페이지로 리다이렉트
     * 이 메서드는 "/notice/delete/{id}" 경로로 POST 요청이 들어올 때 호출된다.
     *
     * @param id 삭제할 공지사항의 ID
     * @return redirect:/notice/list
     */
    @PostMapping("/delete/{id}")
    public String deleteNotice(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        log.info("공지사항 삭제"); // 로그 기록 추가
        return "redirect:/admin/notice/list";
    }

    /**
     * 공지사항 게시글을 검색하는 메서드
     * 사용자가 입력한 검색어를 받아 공지사항을 검색합니다.
     * 검색 결과는 페이지 당 게시글 수(pageSize)에 따라 페이징 처리
     * 먼저, 검색어를 포함하는 게시글의 총 수(totalCount)를 구하고, 이를 기반으로 총 페이지 수(totalPages)를 계산
     * 만약 사용자가 요청한 페이지 번호가 1보다 작거나 총 페이지 수보다 크면, 각각 1과 총 페이지 수로 조정한다.
     * 그리고 나서 해당 페이지에 표시할 게시글 목록을 조회합니다. 이때, 게시글의 시작 위치는 (현재 페이지 번호 - 1) * 페이지 당 게시글 수로 계산한다.
     * 이 메소드의 실행이 완료되면, 검색 로그를 남긴다.
     *
     * @param keyword 사용자가 입력한 검색어. 이 검색어를 기반으로 공지사항의 제목에서 검색을 수행한다.
     * @param page 사용자가 현재 보고 있는 페이지 번호. 기본값 1
     * @param model 뷰에 전달할 데이터를 담고 있는 Model 객체 : 검색 결과 목록, 총 페이지 수, 현재 페이지 번호, 총 게시글 수, 검색어
     * @return 검색 결과를 보여줄 뷰의 이름을 반환. notice/list
     */

    @GetMapping("/search")
    public String searchNotice(@RequestParam(value = "keyword") String keyword, @RequestParam(defaultValue = "1") int page, Model model) {
        int pageSize = 10; // 페이지 당 게시글 수
        int totalCount = noticeService.getSearchCount(keyword); // 검색된 게시글 수

        // 페이징 처리를 위한 계산
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);   // 전체 페이지
        if (page < 1 || totalPages < 1) {
            page = 1;
        } else if (page > totalPages) {
            page = totalPages;
        }
        // int start = (page - 1) * pageSize;
        int start =((int)(Math.ceil((double)page / 5)) - 1) * pageSize; // 시작 페이지

        log.info("keyword" + keyword + "  start" + start + "  ps" + pageSize);
        List<NoticeDTO> noticeList = noticeService.searchNotice(keyword, start, pageSize);

        model.addAttribute("noticeList", noticeList);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPostCount", totalCount); // 총 게시물 수 추가
        model.addAttribute("keyword", keyword); // 검색어 추가

        log.info("공지사항 검색"); // 로그 기록 추가
        return "notice/list";


    }
}

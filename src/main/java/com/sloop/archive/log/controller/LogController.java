package com.sloop.archive.log.controller;

import com.sloop.archive.log.domain.AccessLogDTO;
import com.sloop.archive.log.domain.DownloadLogDTO;
import com.sloop.archive.log.service.LogService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/statistics")
public class LogController {

    private final LogService logService;

    //접속자 수 분석 그래프
    @GetMapping("/access")
    public String getAccessCounts(Model model){
        List<AccessLogDTO> accessCounts = logService.getAccessCountsByDate();
        model.addAttribute("accessCounts", accessCounts);

        return "log/access_log_page";
    }

    //파일다운로드 분석 그래프,콘텐츠 조회 분석 그래프
    @GetMapping("/access2")
    public String getDownLoadCounts(Model model){

        List<DownloadLogDTO> downloadCounts = logService.getDownLoadCounts();
        model.addAttribute("downloadCounts", downloadCounts);

        List<DownloadLogDTO> postHitCounts = logService.postHitCounts();
        model.addAttribute("postHitCounts", postHitCounts);

        return "log/log_page";
    }




}

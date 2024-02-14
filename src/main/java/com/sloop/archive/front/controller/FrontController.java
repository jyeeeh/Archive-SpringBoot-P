package com.sloop.archive.front.controller;

import com.sloop.archive.category.domain.CategoryDTO;
import com.sloop.archive.category.service.CategoryService;
import com.sloop.archive.content.service.ContentService;
import com.sloop.archive.front.service.FrontService;
import com.sloop.archive.log.domain.DownloadLogDTO;
import com.sloop.archive.log.domain.PostLogDTO;
import com.sloop.archive.log.service.LogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class FrontController {

  private final FrontService frontService;
  private final CategoryService categoryService;

  //[jyeeeh]
  private final ContentService contentService;
  private final LogService logService;

  @RequestMapping("")
  public String main(HttpServletRequest req, @RequestParam Map<String, Object> params, Model model) throws Exception  {
    return "redirect:/content/search";
  }

  @RequestMapping(value = "/content/search", method = {RequestMethod.GET})
  public String searchContents(@RequestParam Map<String, Object> params, Model model) throws IOException {
    if(Objects.isNull(params) || params.isEmpty()) {
      params.put("searchContentType", "");
      params.put("searchContentTypeName", "전체");
    }
    model.addAttribute("searchCondition", params);
    model.addAttribute("contents",frontService.searchContents(params));
    return "front/contents";
  }

  @RequestMapping(value = "/content/detail/{id}", method = RequestMethod.GET)
  public String getContentDetail(@PathVariable(name="id") String id, Model model,
                                 @SessionAttribute(name = "loginId", required = false) String sessionId) throws Exception{
    // [jyeeeh]
    // LoginId

    Long userId = Long.parseLong(sessionId);
    log.info("00000 "+sessionId);
    //contentId
//    Long contentId = contentService.findByFilename(filename);
    try {
      Map<String, Object> params = new HashMap<>();
        params.put("searchContentType", "");
        params.put("searchContentTypeName", "전체");
      model.addAttribute("searchCondition", params);
      model.addAttribute("content", frontService.searchContent(id));

      Long contentId = Long.valueOf(id);
      log.info("contentId======"+ id);
      // [jyeeeh]
      // 콘텐츠 조회 로그
      PostLogDTO postLogDTO = PostLogDTO.builder()
              .user_id(userId)
              .content_id(contentId)
              .build();
      insertPostLog(postLogDTO);

    } catch (Exception e) {
      return "front/error";
    }
    return "front/detail";
  }

  @RequestMapping(value = "/cs/notice/list", method = RequestMethod.GET)
  public String noticeList(Model model) {
    Map<String, Object> params = new HashMap<>();
    params.put("searchContentType", "");
    params.put("searchContentTypeName", "전체");
    model.addAttribute("searchCondition", params);
    model.addAttribute("list", frontService.selectNoticeList());
    return "front/notice/list";
  }
  @RequestMapping(value = "/cs/notice/detail/{id}", method = RequestMethod.GET)
  public String noticeDetail(@PathVariable(name="id") String id, Model model) {
    Map<String, Object> params = new HashMap<>();
    params.put("searchContentType", "");
    params.put("searchContentTypeName", "전체");
    model.addAttribute("searchCondition", params);
    model.addAttribute("notice", frontService.selectNoticeById(id));
    return "front/notice/detail";
  }

  @RequestMapping("/content/register")
  public String userContentUpload(Model model) throws Exception  {
    Long parentId = 1L;
    List<CategoryDTO> categoryDTOList = categoryService.getAllSubCategoriesByParentId(parentId);
    model.addAttribute("categoryListForLeftNav", categoryDTOList);

    return "front/content_register";
  }

  //[jyeeeh]
  // 콘텐츠조회 로그
  private void insertPostLog(PostLogDTO postLogDTO){
    logService.insertPostLog(postLogDTO);
  }
}

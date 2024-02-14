package com.sloop.archive.front.service;

import com.amazonaws.HttpMethod;
import com.google.gson.Gson;
import com.sloop.archive.common.fileUtils.FileUtils;
import com.sloop.archive.content.domain.ContentDTO;
import com.sloop.archive.front.domain.ContentSearchDTO;
import com.sloop.archive.front.mapper.FrontMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * MainService
 */
@Slf4j
@Service("mainService")
@RequiredArgsConstructor
public class FrontService {

  private final FrontMapper frontMapper;
  private final FileUtils fileUtils;
  public String searchContents(Map<String, Object> params) throws IOException {
    params.put("keyword", params.get("searchKeyword"));
    if(Objects.nonNull(params.get("searchKeyword"))) {
      String afterStr ="";
      String allKeyword = objToStr(params.get("searchKeyword"));
      if(!allKeyword.isEmpty() && allKeyword.charAt(0) == ',') {
        allKeyword = allKeyword.substring(1);
      }
      if(!allKeyword.isEmpty() && allKeyword.charAt(allKeyword.length() - 1) == ',') {
        allKeyword = allKeyword.substring(0, allKeyword.length()-1);
      }
      afterStr = allKeyword.trim().replace(" ", "").replace("," , "|");
      params.put("keyword", afterStr);
    }

    List<ContentSearchDTO> list = new ArrayList<>();
    list = frontMapper.selectContents(params);
    for(ContentSearchDTO dto : list) {
      String keyName = dto.getSavedName();  // 변수명 변경 ( FileUtils 에서 다운로드 로그 찍기위해 savedName 자체에 upload/를 추가했으므로 해당 변수명을 사용 )
      log.info("키네임1"+keyName);
      dto.setPresignedURL(fileUtils.generatePreSignedUrl(keyName, HttpMethod.GET));
    }
    return new Gson().toJson(list);
  }

  public String searchContent(String id) {
    ContentSearchDTO content = new ContentSearchDTO();
    content = frontMapper.selectContentById(id);
    String keyName = content.getSavedName();  // 변수명 변경 ( FileUtils 에서 다운로드 로그 찍기위해 savedName 자체에 upload/를 추가했으므로 해당 변수명을 사용 )
    log.info("키네임2"+keyName);
    content.setPresignedURL(fileUtils.generatePreSignedUrl(keyName, HttpMethod.GET));

    return new Gson().toJson(content);
  }

  public String selectNoticeList() {
    List<Map<String, Object>> list = new LinkedList<>();
    list = frontMapper.selectNoticeList();
    return new Gson().toJson(list);
  }

  public Map<String, Object> selectNoticeById(String id) {
    Map<String, Object> notice = new HashMap<>();
    notice = frontMapper.selectNotice(id);
    return notice;
  }
  private String objToStr(Object obj) {
    return Objects.nonNull(obj) ? String.valueOf(obj) : "";
  }
}

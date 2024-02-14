package com.sloop.archive.front.mapper;

import com.sloop.archive.content.domain.ContentDTO;
import com.sloop.archive.front.domain.ContentSearchDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface FrontMapper {
    public List<ContentSearchDTO> selectContents(Map<String, Object> paramMap);
    public ContentSearchDTO selectContentById(String id);

    public List<Map<String, Object>> selectNoticeList();
    public Map<String, Object> selectNotice(String id);


}

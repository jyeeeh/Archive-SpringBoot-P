package com.sloop.archive.log.mapper;

import com.sloop.archive.log.domain.AccessLogDTO;
import com.sloop.archive.log.domain.DownloadLogDTO;
import com.sloop.archive.log.domain.PostLogDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface LogMapper {

    //접속 Log
    void insertAccessLog(AccessLogDTO accessLogDTO);

    //접속자 수 분석 그래프
    List<AccessLogDTO> getAccessCountsByDate();

    //파일다운로드 Log
     void insertDownloadLog(DownloadLogDTO downloadLogDTO);

    //파일다운로드 분석 그래프
    List<DownloadLogDTO> getDownLoadCounts();

    //카테고리 조회 Log
    void insertPostLog(PostLogDTO postLogDTO);

    //콘텐츠 조회 분석 그래프
    List<DownloadLogDTO> postHitCounts();

}

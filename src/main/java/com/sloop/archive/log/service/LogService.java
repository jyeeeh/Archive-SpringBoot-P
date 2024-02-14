package com.sloop.archive.log.service;

import com.sloop.archive.log.domain.AccessLogDTO;
import com.sloop.archive.log.domain.DownloadLogDTO;
import com.sloop.archive.log.domain.PostLogDTO;
import com.sloop.archive.log.mapper.LogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogService {

    private final LogMapper logMapper;

    //접속자 수 분석 그래프
    public List<AccessLogDTO> getAccessCountsByDate() {
        return logMapper.getAccessCountsByDate();
    }

    //다운로드 로그 insert
    public void insertDownloadLog(DownloadLogDTO downloadLogDTO) {
         logMapper.insertDownloadLog(downloadLogDTO);
    }

    //파일다운로드 분석 그래프
    public List<DownloadLogDTO> getDownLoadCounts() {
        return logMapper.getDownLoadCounts();
    }

    //카테고리 조회 로그 insert
    public void insertPostLog(PostLogDTO postLogDTO) {
        logMapper.insertPostLog(postLogDTO);
    }

    //콘텐츠 조회 분석 그래프
    public List<DownloadLogDTO> postHitCounts() {
        return logMapper.postHitCounts();
    }
}

package com.sloop.archive.common.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.sloop.archive.common.fileUtils.FileDTO;
import com.sloop.archive.common.fileUtils.FileUtils;
import com.sloop.archive.common.mapper.FileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {


    private final FileMapper fileMapper;


    /**
     * 게시글 번호(contentId)와 파일 정보를 전달받아 업로드 된 파일 정보를
     * 테이블에 저장하는 역할. 만약 게시글을 저장(Insert or Update) 하는 시점에
     * 파일이 없다면 로직을 종료하고 파일이 있으면 모든 요청 객체에 게시글 번호(contentId)를 세팅한 후
     * 테이블에 파일정보를 저장한다.
     * @param contentId
     * @param files
     */
    @Transactional
    public void saveFiles(Long contentId , List<FileDTO> files){

        if (CollectionUtils.isEmpty(files)){
            return;
        }
        for (FileDTO file : files){
            // 여긴 기존 로컬,DB에 저장하는 메서드
            file.setContentId(contentId);
            log.info("서비스 == contentId || "+ contentId);
        }
        fileMapper.saveFiles(files);
    }

    @Transactional
    public void updateFiles(Long contentId , List<FileDTO> files){
        log.info("업데이트 파일즈 도착");
        if (CollectionUtils.isEmpty(files)){
            return;
        }
        for (FileDTO file : files){
            file.setContentId(contentId);
            log.info("업데이트 파일즈");
        }
        fileMapper.updateFiles(files);
    }
}

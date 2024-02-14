package com.sloop.archive.common.fileUtils;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.sloop.archive.content.domain.ContentDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component  // 개발자가 직접 정의한 클래스를 빈으로 등록.
@RequiredArgsConstructor
@Slf4j
@Validated
public class FileUtils {

    /*@Value("${fileUploadPath.path}")
    private String uploadPath;*/

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.s3.upload-temp}")
    private String tempPath;

    private final AmazonS3 s3Client;
    // ck home
    /*private final String uploadPath = Paths.get("D:","upload","temp","bootS3").toString();*/
    // ck school
    private final String uploadPath = Paths.get("D:","upload","temp","bootS3").toString();


    /**
     * PreSigned URL 받아오는 메서드
     * @param filePath
     * @param httpMethod
     * @return
     */
    public String generatePreSignedUrl(String filePath , HttpMethod httpMethod){

     /*   // 현재 시간
        LocalDateTime currentTime = LocalDateTime.now();
        // 5분 후의 날짜 및 시간 계산
        LocalDateTime expirationTime = currentTime.plusMinutes(5);
        // LocalDateTime 을 밀리초로 변환
        long expirationMillis = expirationTime.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
        // DateTimeFormatter을 사용하여 ISO 8601 형식의 문자열로 변환
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE;
        String expirationTimeString = expirationTime.format(formatter);
*/

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MINUTE,5);
        log.info("제너레이트 URL 결과는 ??? === ||| "+ httpMethod);

        return s3Client.generatePresignedUrl(bucketName,filePath,cal.getTime(),httpMethod).toString();

    }

    /**
     * 다중 파일 업로드
     * @param multipartFiles - 파일 객체 List
     * @return DB에 저장할 파일 정보 List
     */
    public List<FileDTO> uploadFiles(final List<MultipartFile> multipartFiles, Long contentId) {
        List<FileDTO> files = new ArrayList<>();
        for (MultipartFile multipartFile : multipartFiles){
            if (multipartFile.isEmpty()){
                continue;
            }
            files.add(uploadFile(multipartFile, contentId));
        }
        log.info("files | " + files);
        return files;

    }


    /**
     * 단일 파일 업로드
     * @param multipartFile
     * @return
     */
    public FileDTO uploadFile(final MultipartFile multipartFile, Long contentId) {

        if (multipartFile.isEmpty()){
            return null ;
        }
        //
        String savedName = generateSaveFileName(multipartFile.getOriginalFilename());
        log.info("저장용 이름 = || " + savedName);
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd")).toString();
        String uploadPath = getUploadPath(today) + File.separator;
        /*String savedPath = getUploadPath(today) + File.separator + savedName;*/
        String extension = savedName.substring(savedName.lastIndexOf("."));
        log.info("extension = || " + extension);

        // 썸네일 이름 , 경로

        String thumbName = "t_" + savedName;
        log.info("썸네일 이름 = || "+ thumbName);
        String thumbPath = getUploadPath(today) + File.separator;
        log.info("썸네일 경로 = || "+ thumbPath);

        File uploadFile = new File(uploadPath+savedName);
        // s3 컨버터 ( 바이트 코드 )
        File fileObj = convertMultiPartFileToFile(multipartFile);

        // 버킷에 실제 저장될 파일명
        String bucketKey = tempPath + savedName;
        String t_BucketKey = tempPath + thumbName;

        try {

            // 파일 저장
            multipartFile.transferTo(uploadFile);
            // S3 에서 해당 파일을 퍼블릭으로 읽기 권한 주면서 업로드 ( 원본 , 썸네일 두개 다 저장)
            // 해당 함수는 S3 에서 퍼블릭으로 열려 있어야 가능.
            /*s3Client.putObject(new PutObjectRequest(bucketName,bucketKey,fileObj).withCannedAcl(CannedAccessControlList.PublicRead));
            s3Client.putObject(new PutObjectRequest(bucketName,t_BucketKey,fileObj).withCannedAcl(CannedAccessControlList.PublicRead));*/


            // 썸네일 생성 ( try catch 안에 있어야함 )


            /*Thumbnailator.createThumbnail(new File(uploadPath+savedName),new File(thumbPath+thumbName),80,80);*/


            // S3 업로드

            s3Client.putObject(new PutObjectRequest(bucketName,bucketKey,fileObj));
            s3Client.putObject(new PutObjectRequest(bucketName,t_BucketKey,fileObj));
            log.info(" S3 파일 업로드 완료 ");

            // 멀티파트 파일 저장 후 삭제
            fileObj.delete();
            uploadFile.delete();
            deletefile(uploadPath+savedName);
            deletefile(thumbPath+thumbName);


        }catch(IOException e){
            throw new RuntimeException(e);
        }

        // 원본 썸네일 AWS S3 객체 URL 받아오기
        String getSavedUrl = s3Client.getUrl(bucketName,bucketKey).toString(); // https: 까지 자른 URL 얻기
        String getThumbUrl = s3Client.getUrl(bucketName,t_BucketKey).toString(); // https: 까지 자른 URL 얻기

        log.info("S3 URL 받아오기 || " + getSavedUrl);

        ContentDTO contentDTO = new ContentDTO();
        return FileDTO.builder()
                .contentId(contentId)
                .originalName(multipartFile.getOriginalFilename())
                .savedName(bucketKey)
                .originalPath(uploadPath)
                .savedPath(uploadPath)
                .extension(extension)
                .thumbName(thumbName)
                .thumbPath(thumbPath)
                .build();
    }

    /**
     * 저장 파일명 생성
     * @param filename 원본 파일명
     * @return 디스크에 저장할 파일명
     */
    private String generateSaveFileName(final String filename){
        String uuid = UUID.randomUUID().toString().replaceAll("-","");
        String extension = StringUtils.getFilenameExtension(filename);
        return uuid + "." + extension;
    }

    /**
     * 업로드 경로 반환
     * @param addPath - 추가 경로
     * @return 업로드 경로
     */
    private String getUploadPath(final String addPath){
        return makeDirectories(uploadPath + File.separator + addPath);
    }

    private String makeDirectories(final String path){
        File dir = new File(path);
        if (dir.exists() == false){
            dir.mkdirs();
        }
        return dir.getPath();
    }

    /**
     * S3에서 byte 코드로 변환해줄 메서드
     * @param uploadFiles;
     * @return convertedFiles;
     */
    private File convertMultiPartFileToFile(MultipartFile uploadFiles) {
        File convertedFile = new File(uploadFiles.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convertedFile)) {
            fos.write(uploadFiles.getBytes());

            log.info("=== fos === " + fos);
        } catch (IOException e) {
            log.error("Error converting multipartFile to file", e);
        }
        log.info("=== convertedFile ===" + convertedFile);
        return convertedFile;
    }

    private void deletefile(String filePath){
        File file = new File(filePath);
        if (file.exists()){
            file.delete();
        }
    }

    private void getThumbNail (String uploadPath , String thumbPath) throws IOException{
        Thumbnailator.createThumbnail(new File(uploadPath),new File(thumbPath),80,80);
    }


    /**
     * 파일 다운로드
     * @param fileName
     * @return
     */
    public byte[] downloadFile(String fileName) {
        S3Object s3Object = s3Client.getObject(bucketName,fileName);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        try{
            byte[] content = IOUtils.toByteArray(inputStream);
            return content;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }
}

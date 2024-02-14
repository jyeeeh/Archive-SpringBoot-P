package com.sloop.archive.notice.controller;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.gson.JsonObject;
import com.sloop.archive.common.fileUtils.FileUtils;
import io.swagger.v3.core.util.Json;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.*;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Controller
@RequestMapping("/admin")
public class CKeditorFileUploadController {
    @Value("${noticeUploadPath.path}")
    String uploadPathValue;

    // 이미지 업로드
    @RequestMapping(value="fileupload.do", method= RequestMethod.POST)
    @ResponseBody
    public void fileUpload(HttpServletRequest req, HttpServletResponse resp,
                           MultipartHttpServletRequest multiFile) throws Exception {
        JsonObject json = new JsonObject();
        PrintWriter printWriter = null;
        OutputStream out = null;

        MultipartFile file = multiFile.getFile("upload");
        String uploadPath = "";
        if(file != null){
            if(file.getSize() > 0 && StringUtils.hasText(file.getName())){
                if(file.getContentType().toLowerCase().startsWith("image/")){


                    try{
                        String fileName = file.getOriginalFilename();   // 파일 풀 네임

                        // 파일 확장자 검사
                        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
                        extension = extension.toLowerCase(); // 소문자로 변경


                        byte[] bytes = file.getBytes();
                        // String uploadPath = req.getServletContext().getRealPath("/img");
                        // 업로드할 디렉터리 경로
                        File uploadFile = new File(uploadPathValue);
                        if(!uploadFile.exists()){   // 디렉토리 미존재 시, 생성
                            uploadFile.mkdirs();
                        }


                        fileName = UUID.randomUUID().toString();
                        // [@@@@@@] ec2로 바꾸기
                        uploadPath = uploadPathValue + "/" + fileName + "." + extension;
                        out = new FileOutputStream(uploadPath);
                        out.write(bytes);

                        printWriter = resp.getWriter();
                        resp.setContentType("text/html");
                        String fileUrl = "/admin/noticeImages/" + fileName + "." + extension;

                        // JSON 데이터로 등록
                        // {"uploaded" : 1, "fileName" : "test.jpg", "url" : "/img/test.jpg"}
                        // 이런 형태로 리턴이 나가야 함
                        json.addProperty("uploaded", 1);
                        json.addProperty("fileName", fileName);
                        json.addProperty("url", fileUrl);

                        printWriter.println(json);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if(out != null){
                            out.close();
                        }
                        if(printWriter != null){
                            printWriter.close();
                        }
                    }
                }
            }
        }
    }


    // 이미지 출력
    @GetMapping("noticeImages/{fileName}")
    public void printImages(@PathVariable(value = "fileName") String fileName, HttpServletResponse response) throws Exception {
        // [@@@@@@] ec2로 바꾸기
        String filePath = uploadPathValue + "/" + fileName;

        File imgFile = new File(filePath);

        //사진 이미지 찾지 못하는 경우 예외처리로 빈 이미지 파일을 설정한다.
        if (imgFile.isFile()) {
            byte[] buf = new byte[1024];
            int readByte = 0;
            int length = 0;
            byte[] imgBuf = null;

            FileInputStream fileInputStream = null;
            ByteArrayOutputStream outputStream = null;
            ServletOutputStream out = null;

            try {
                fileInputStream = new FileInputStream(imgFile);
                outputStream = new ByteArrayOutputStream();
                out = response.getOutputStream();

                while ((readByte = fileInputStream.read(buf)) != -1) {
                    outputStream.write(buf, 0, readByte);
                }

                imgBuf = outputStream.toByteArray();
                length = imgBuf.length;
                out.write(imgBuf, 0, length);
                out.flush();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                outputStream.close();
                fileInputStream.close();
                out.close();
            }
        }
    }

}

package com.sloop.archive.content.controller;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.sloop.archive.category.domain.CategoryDTO;
import com.sloop.archive.category.service.CategoryService;
import com.sloop.archive.common.fileUtils.FileDTO;
import com.sloop.archive.common.fileUtils.FileUtils;
import com.sloop.archive.common.message.MessageDTO;
import com.sloop.archive.common.pagination.PaginationService;
import com.sloop.archive.common.pagination.SearchDTO;
import com.sloop.archive.common.service.FileService;
import com.sloop.archive.content.domain.ContentDTO;
import com.sloop.archive.content.service.ContentService;
import com.sloop.archive.log.domain.DownloadLogDTO;
import com.sloop.archive.log.service.LogService;
import com.sloop.archive.user.domain.UserDTO;
import com.sloop.archive.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/content")
@RequiredArgsConstructor
@Slf4j
public class ContentController {

    private final ContentService contentService;
    private final PaginationService paginationService;
    private final FileService fileService;
    private final FileUtils fileUtils;
    private final UserService userService;
    private final CategoryService categoryService;
    private final LogService logService;

    // 콘텐츠 전체 리스트 불러오기
    /*@GetMapping("list")
    public String list(Model model) {
        List<ContentDTO> contentDTO = contentService.findAll();
        model.addAttribute("contentList",contentDTO);
        return "content/list";
    }*/

    /**
     * 전체 콘텐츠 리스트
     * @param page
     * @param searchType
     * @param searchWord
     * @param model
     * @param contentDTO
     * @return
     * @throws UnsupportedEncodingException
     */
    @GetMapping("list")
    public String list(@RequestParam(value = "page" , required = false ,defaultValue = "1") int page,
                       @RequestParam(value = "searchType", required = false ,defaultValue = "0") int searchType,
                       @RequestParam(value = "searchWord", required = false , defaultValue = "") String searchWord,
                       Model model, ContentDTO contentDTO) throws UnsupportedEncodingException {


        // 검색어 앞뒤 공백 제거
        searchWord = searchWord.trim();

        // 검색 + 페이징을 위한 객체
        SearchDTO searchDTO = paginationService.initialize(page,searchType,searchWord);

        model.addAttribute("searchDTO",searchDTO);
        log.info("=== searchDTO === ::: "+ searchDTO);

        // 글 목록 조회 + 검색 + 페이징
        ArrayList<ContentDTO> contentAllList = contentService.findAllContentsList(searchDTO);

        log.info("contentList 리스트 모음집 == || " + contentAllList);


        // for문을 돌면서 originalPath의 presignedUrl을 originalPath컬럼에 넣어준다.
        for (int i = 0; i < contentAllList.toArray().length; i++){

            // 인코딩 되어있는 파일을 불러올 땐 Decoder로 디코딩 해주자
            String filePath = "upload/" + URLDecoder.decode(contentAllList.get(i).getThumbName(), "utf-8");

            /*String originPath = contentAllList.get(i).getOriginalPath();*/

            log.info(fileUtils.generatePreSignedUrl(filePath,HttpMethod.GET));
            log.info("PreSignedUrl 받아올 S3 객체의 경로 || "+filePath);

            // 결과 값을 대입 해준다.
            contentAllList.get(i).setOriginalPath(fileUtils.generatePreSignedUrl(filePath,HttpMethod.GET));
        }

        model.addAttribute("contentLists",contentAllList);

        return "content/list_arc";
    }

    /**
     * 미승인 콘텐츠 리스트
     * @param page
     * @param searchType
     * @param searchWord
     * @param model
     * @param contentDTO
     * @return
     * @throws UnsupportedEncodingException
     */
    @GetMapping("notAppr")
    public String nptApprlist(@RequestParam(value = "page" , required = false ,defaultValue = "1") int page,
                       @RequestParam(value = "searchType", required = false ,defaultValue = "0") int searchType,
                       @RequestParam(value = "searchWord", required = false , defaultValue = "") String searchWord,
                       Model model, ContentDTO contentDTO) throws UnsupportedEncodingException {


        // 검색어 앞뒤 공백 제거
        searchWord = searchWord.trim();

        // 검색 + 페이징을 위한 객체
        SearchDTO searchDTO = paginationService.initialize(page,searchType,searchWord);

        model.addAttribute("searchDTO",searchDTO);
        log.info("=== searchDTO === ::: "+ searchDTO);

        // 글 목록 조회 + 검색 + 페이징
        ArrayList<ContentDTO> contentAllList = contentService.findNotApprAllContentsList(searchDTO);

        log.info("contentList 리스트 모음집 == || " + contentAllList);


        // for문을 돌면서 originalPath의 presignedUrl을 originalPath컬럼에 넣어준다.
        for (int i = 0; i < contentAllList.toArray().length; i++){

            // 인코딩 되어있는 파일을 불러올 땐 Decoder로 디코딩 해주자
            String filePath = "upload/" + URLDecoder.decode(contentAllList.get(i).getThumbName(), "utf-8");

            /*String originPath = contentAllList.get(i).getOriginalPath();*/

            log.info(fileUtils.generatePreSignedUrl(filePath,HttpMethod.GET));
            log.info("PreSignedUrl 받아올 S3 객체의 경로 || "+filePath);

            // 결과 값을 대입 해준다.
            contentAllList.get(i).setOriginalPath(fileUtils.generatePreSignedUrl(filePath,HttpMethod.GET));
        }

        model.addAttribute("contentLists",contentAllList);

        return "content/not_appr";
    }

    /**
     * 컨텐츠 등록 폼 출력
     * @param contentDTO
     * @return "content/write_ck"
     */
    @GetMapping("regist")
    public String registForm(@ModelAttribute("contents") ContentDTO contentDTO, Model model){
        // categoryListForLeftNav 기본값으로 depth=2인 카테고리(parentId=1)를 조회하여 페이지에 출력한다.
        Long parentId = 1L;
        List<CategoryDTO> categoryDTOList = categoryService.getAllSubCategoriesByParentId(parentId);
        model.addAttribute("categoryListForLeftNav", categoryDTOList);

        return "content/write_ck";
    }


    /**
     * 콘텐츠 등록
     * @param contentDTO
     * @param errors
     * @param model
     * @param sessionId
     * @param sessionRole
     * @return
     */
    @PostMapping("regist")
    public String regist(@Valid @ModelAttribute("contents") ContentDTO contentDTO,
                         BindingResult errors, Model model,
                         @SessionAttribute(name = "loginId", required = false) String sessionId,
                         @SessionAttribute(name = "loginRole", required = false) String sessionRole) {

        if (errors.hasGlobalErrors()){
            MessageDTO message = new MessageDTO("게시물이 등록이 실패했습니다.","regist",RequestMethod.GET,null);
            return showMessageAndRedirect(message,model);
        }
        log.info("현재 세션 인덱스 입니다 = || " + sessionId);
        log.info("현재 세션 권한 입니다 = ||" + sessionRole);
        // 세션에 있는 값으로 로그인 확인하기 ( 추후 권한도 확인 )
        if (sessionId == null){
            MessageDTO message = new MessageDTO("로그인 먼저 해주세요.","/",RequestMethod.GET,null);
            return showMessageAndRedirect(message,model);
        }
        log.info("게시글 등록");
        // 글 등록 sessionId 값을 userId 값으로 넣어야됨.
        Long userId = Long.parseLong(sessionId);
        log.info("userId = || " + userId);


        contentDTO.setUserId(userId);
        log.info("컨트롤러에서 DTO = || "+contentDTO);


        Long id = contentService.registContent(contentDTO);
        log.info("id = || "+contentDTO.getId());
        Long contentId = contentDTO.getId();


        if ( id < 0 ){

            MessageDTO message = new MessageDTO("첨부된 파일이 없습니다.", "regist", RequestMethod.GET, null);
            return showMessageAndRedirect(message, model);

        } else {

            List<FileDTO> files = fileUtils.uploadFiles(contentDTO.getFiles(), contentId);
            log.info("else 문 안의 contentId = || "+contentId);
            fileService.saveFiles(contentId,files);
            log.info("files || "+files);
            MessageDTO message = new MessageDTO("게시물이 등록되었습니다.", "list", RequestMethod.GET, null);
            return showMessageAndRedirect(message, model);
        }
    }

    /**
     * 콘텐츠 수정 폼 출력
     * @param contentId
     * @param model
     * @return
     * @throws UnsupportedEncodingException
     */
    @GetMapping("update")
    public String updateForm(@RequestParam("contentId") Long contentId , Model model) throws UnsupportedEncodingException {



        ContentDTO contentDTO = contentService.findContentsAndImageByContentId(contentId);
        // 게시물 이미지 불러오기 ( Decoder 써야됨 )
        String filePath = "upload/" + URLDecoder.decode(contentDTO.getThumbName(),"utf-8");
        contentDTO.setOriginalPath(fileUtils.generatePreSignedUrl(filePath,HttpMethod.GET));
        log.info(contentDTO.getOriginalPath());
        log.info("업데이트 파일 : "+filePath);

        model.addAttribute("contents",contentDTO);
        return "content/update_ck";
    }

    /**
     * 콘텐츠 수정
     * @param contentDTO
     * @param errors
     * @param model
     * @param contentId
     * @param sessionId
     * @param sessionRole
     * @return
     */
    @PostMapping("update")
    public String update(@Valid @ModelAttribute("contents") ContentDTO contentDTO , BindingResult errors, Model model,
                       @RequestParam("contentId") Long contentId,
                       @SessionAttribute(name = "loginId", required = false) String sessionId,
                       @SessionAttribute(name = "loginRole", required = false) String sessionRole){

        /*if (errors.hasErrors()){
            MessageDTO message = new MessageDTO("게시물이 등록이 실패했습니다.","/admin/content/regist",RequestMethod.GET,null);
            return showMessageAndRedirect(message,model);
        }*/

        log.info("업데이트 하려는 sessionID = || "+sessionId);
        log.info("GetParams = ||" +contentId);

        // sessionId 값을 Long type으로 변경
        Long userId = Long.parseLong(sessionId);
        log.info("transform userId by session = || " + userId);

        if (sessionId == null || sessionId.equals(contentDTO.getUserId())){
            MessageDTO message = new MessageDTO("게시물이 등록이 실패했습니다.","/admin/content/regist",RequestMethod.GET,null);
            return showMessageAndRedirect(message,model);
        }

        contentDTO.setUserId(userId);
        contentDTO.setContentId(contentId);
        boolean result = contentService.update(contentDTO);

        if (!result){
            MessageDTO message = new MessageDTO("게시물이 등록이 실패했습니다.","/admin/content/regist",RequestMethod.GET,null);
            return showMessageAndRedirect(message,model);

        } else {
            // 맞다면 게시물 Update쿼리 시작


            List<FileDTO> files = fileUtils.uploadFiles(contentDTO.getFiles(), contentId);
            log.info("else 문 안의 contentId = || "+contentId);
            fileService.updateFiles(contentId,files);
            log.info("files || "+files);

        }
        MessageDTO message = new MessageDTO("게시물이 수정되었습니다.","list",RequestMethod.GET,null);
        return showMessageAndRedirect(message,model);
    }

    /**
     * 논리 삭제를 위한 update 쿼리 사용
     * @param contentId
     */
    @GetMapping("delete")
    public void delete(@RequestParam("contentId") Long contentId){
        contentService.delete(contentId);
    }


    /**
     * 사용자에게 메시지를 전달하고, 페이지를 리다이렉트 한다.
     * @param params
     * @param model
     * @return
     */
    private String showMessageAndRedirect(final MessageDTO params, Model model){
        model.addAttribute("params",params);
        return "/common/messageRedirect";
    }

    /**
     * 미승인 컨텐츠 승인 기능
     * @param map
     * @param model
     * @return
     */
    @PostMapping("/approve")
    public ResponseEntity<?> approveContent(@RequestBody HashMap<String,Object> map, Model model){
        log.info("어프루브 도착");
        log.info("fgfgfgf"+map.get("id"));
        Integer IntegerContentId = (Integer) map.get("id");
        log.info("인티져어어어어어어 : "+IntegerContentId);
        Long contentId = (IntegerContentId != null) ? IntegerContentId.longValue() : null;
        log.info("승인버튼 누른 후 " + contentId);

        
        // 미승인 버튼
        /*if (approveFlag == 0){
            ContentDTO contentDTO = contentService.findById(contentId);
            boolean result = contentService.notApprove(contentDTO);
            return ResponseEntity.ok(result);
        }*/

        ContentDTO contentDTO = contentService.findById(contentId);
        log.info("아이디로 찾아오기" + contentDTO);

        int result = contentService.approve(contentDTO);
        log.info("승인 결과 = || " +result);
        return ResponseEntity.ok(result);
    }

    /**
     * 파일 다운로드 메서드 실행시 contentId 도 불러와 log에 반영하기
     * @param filename
     * @return
     */
    @GetMapping("/download")
    public ResponseEntity<ByteArrayResource> downloadFile(@RequestParam("filename") String filename, HttpSession session,
                                                          @SessionAttribute(name = "loginId", required = false) String sessionId){

        Long userId = Long.parseLong(sessionId);
        // LoginId
        //Long loginId = Long.parseLong(session.getId());
        //contentId
        Long contentId = contentService.findByFilename(filename);

        // log.info("--------"+loginId);
        //로그
        DownloadLogDTO downloadLogDTO = DownloadLogDTO.builder()
                        .user_id(userId)
                        .content_id(contentId)
                        .build();
        insertDownloadLog(downloadLogDTO);

//        log.info("-----다운로드------"+downloadLogDTO);

        byte[] data = fileUtils.downloadFile(filename);
        fileUtils.generatePreSignedUrl(filename,HttpMethod.GET);

        log.info("session.getId"+userId);
        log.info("Mapper 에서 불러온 = || "+ contentId);


        ByteArrayResource resource = new ByteArrayResource(data);
        return ResponseEntity
                .ok()
                .contentLength(data.length)
                .header("Content-type","application/octet-steam")
                .header("Content-disposition","attachment; filename = \"" + filename + "\"")
                .body(resource);
    }

    // 파일다운로드 로그
    private void insertDownloadLog(DownloadLogDTO downloadLogDTO){
        logService.insertDownloadLog(downloadLogDTO);
    }

    @PostMapping("/detail")
    public ResponseEntity<?> detail(@RequestBody HashMap<String,Object> map , Model model){

        log.info("디테일 도착");
        Integer IntegerContentId = (Integer) map.get("id");
        log.info("디테일 인티져! = || : "+IntegerContentId);
        Long contentId = (IntegerContentId != null) ? IntegerContentId.longValue() : null;
        log.info("롱타입 변환! = || " + contentId);
        ContentDTO contentDTO = contentService.findContentsAndImageByContentId(contentId);
        log.info("디테일 DTO = || "+contentDTO);
        model.addAttribute("contentInModal",contentDTO);
        return ResponseEntity.ok(contentDTO);
    }

}

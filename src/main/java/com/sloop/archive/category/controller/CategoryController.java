package com.sloop.archive.category.controller;

import com.sloop.archive.category.domain.CategoryDTO;
import com.sloop.archive.category.service.CategoryService;
import com.sloop.archive.exceptions.SloopException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Tag(name = "카테고리", description = "카테고리 관리 페이지의 API")
@Slf4j
@RequiredArgsConstructor
@RequestMapping("admin/category")
@Controller
public class CategoryController {
    private final CategoryService categoryService;

    /**
     * 전체 카테고리 json 객체를 반환한다.
     * @return
     */
    @ResponseBody
    @GetMapping({"", "/"})
    public Map<Long, CategoryDTO> getAllCategories(){
        // 전체 카테고리 조회
        List<CategoryDTO> categoryDTOList = categoryService.getAllCategories();
        // List -> Map
        Map<Long, CategoryDTO> categoryMap = listToMapForCategories(categoryDTOList);
        return categoryMap;
    }

    /**
     * 하위 카테고리 목록 json을 반환한다.
     * @param id 상위 카테고리 아이디
     * @return id의 하위 카테고리 json
     */
    @Operation(summary = "하위 카테고리 조회", description = "파라미터로 받은 카테고리의 하위 카테고리 목록을 반환합니다.")
    @Parameter(name = "id", description = "부모 카테고리 아이디")
    @ResponseBody
    @GetMapping("subcategories/{id}")
    public Map<Long, CategoryDTO> getAllSubCategoriesById(@PathVariable Long id){
        try {
            List<CategoryDTO> categoryDTOList;
            // id에 해당하는 카테고리의 모든 자식 카테고리 조회하여 List에 저장
            categoryDTOList = categoryService.getAllSubCategoriesByParentId(id);

            Map<Long, CategoryDTO> categoryMap;
            if (categoryDTOList == null || categoryDTOList.isEmpty()) {   // 하위 카테고리가 존재하지 않을 경우
                // null을 반환하면 ajax 오류가 발생하기 때문에 0과 -1을 넣어준 map을 반환한다.
                categoryMap = new HashMap<>() {{
                    put(0L, new CategoryDTO(-1L));
                }};
            } else { // 하위 카테고리를 json을 위한 Map으로 만듦
                // List -> Map
                categoryMap = listToMapForCategories(categoryDTOList);
            }

            return categoryMap;
        }catch (SloopException e){
            throw new SloopException(HttpStatus.FORBIDDEN, "CategoryController.getAllSubCategoriesById");
        }
    }

    /**
     * 카테고리 관리 페이지
     * @param model
     * @return 왼쪽 사이드 바: parentId를 1로 가지는 카테고리 / 오른쪽 영역: parentId = 1, orders = 1인 카테고리의 하위 카테고리 목록
     */
    @GetMapping("category")
    public String getAllCategories(Model model){
        try {
            Long parentId;
            List<CategoryDTO> categoryDTOList;

            // categoryListForLeftNav 기본값으로 depth=2인 카테고리(parentId=1)를 조회하여 사이드바에 출력한다.
            parentId = 1L;
            categoryDTOList = categoryService.getAllSubCategoriesByParentId(parentId);
            model.addAttribute("categoryListForLeftNav", categoryDTOList);

            // categoryList 기본값으로 depth=2 orders=1을 부모로 가지는 노드들을 메인에 출력한다.
            if (categoryDTOList != null && categoryDTOList.size() > 0) {
                CategoryDTO firstCategoryDTO = categoryDTOList.get(0);
                categoryDTOList = categoryService.getAllSubCategoriesByParentId(firstCategoryDTO.getId());
            }
            model.addAttribute("categoryDTOList", categoryDTOList);
        }catch (SloopException e){
            throw new SloopException(HttpStatus.FORBIDDEN, "CategoryController.getAllCategories");
        }

        return "category/main";
    }

    /**
     * 카테고리의 상세 정보 조회
     * @param id: 조회할 카테고리 아이디
     * @return 해당하는 카테고리 id, 상위 카테고리 id, 이름, 사용여부, 깊이, 순서, 상위 카테고리 이름 반환
     */
    @Operation(summary = "카테고리 조회", description = "파라미터로 받은 카테고리의 상세 정보를 조회합니다.")
    @Parameter(name = "id", description = "조회할 카테고리 아이디")
    @ResponseBody
    @GetMapping("category/{id}")
    public CategoryDTO getCategoryDetails(@PathVariable("id") Long id){
        CategoryDTO categoryDTO;
        try {
            categoryDTO = categoryService.getCategory(id);
        }catch (SloopException e){
            throw new SloopException(HttpStatus.FORBIDDEN, "CategoryController.getCategoryDetails");
        }
        return categoryDTO;
    }

    /**
     * 카테고리 생성
     * @param session: session을 사용하여 로그인된 사용자 id 조회
     * @param categoryDTO: 생성할 카테고리
     * @return
     */
    @Operation(summary = "카테고리 생성", description = "파라미터로 받은 카테고리를 생성합니다.")
    @Parameter(name = "categoryDTO", description = "생성할 카테고리")
    @PostMapping("category")
    public ResponseEntity createCategory(HttpSession session, @RequestBody CategoryDTO categoryDTO){ // [*****] @Valid 나중에 추가하기
        try {
            log.info("createCategory category확인 " + categoryDTO);

            // HttpSession session / 로그인 되어 있는 사용자 id [*****]
            setLoginedUserId(session, categoryDTO);

            // 카테고리 추가
            boolean result = categoryService.createCategory(categoryDTO);
            ResponseEntity responseEntity;
            if (result) { // 성공
                responseEntity = ResponseEntity.status(HttpStatus.OK)
                        .body("{\"msg\": \"success\"}");
            } else {  // 실패
                responseEntity = ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("{\"msg\": \"error\"}");
            }
            return responseEntity;
        }catch (RuntimeException e){
            throw new SloopException(HttpStatus.FORBIDDEN, "CategoryController.createCategory: 상위 카테고리가 존재하지 않습니다.");
        }
    }

    /**
     * 카테고리 수정
     * @param session: session을 사용하여 로그인된 사용자 id 조회
     * @param categoryDTO: 수정할 카테고리
     * @return
     */
    @Operation(summary = "카테고리 수정", description = "파라미터로 받은 카테고리를 수정합니다.")
    @Parameter(name = "categoryDTO", description = "수정할 카테고리")
    @ResponseBody
    @PostMapping("modifyCategory")
    public ResponseEntity modifyCategory(HttpSession session, @RequestBody CategoryDTO categoryDTO){
        log.info("modifyCategory category확인 " + categoryDTO);

        // HttpSession session / 로그인 되어 있는 사용자 id [*****]
        setLoginedUserId(session, categoryDTO);

        boolean result;
        try {
            // 카테고리 수정
            result = categoryService.modifyCategory(categoryDTO);
        }catch (SloopException e){
            throw new SloopException(HttpStatus.FORBIDDEN, "CategoryController.modifyCategory: 수정하는 카테고리가 존재하지 않습니다.");
        }catch (RuntimeException e){
            throw new SloopException(HttpStatus.FORBIDDEN, "CategoryController.modifyCategory: RuntimeException");
        }

        ResponseEntity responseEntity;
        if(result) {    // 수정 성공
            responseEntity = ResponseEntity.status(HttpStatus.OK)
                    .body("{\"msg\": \"카테고리를 수정하였습니다.\"}");
        }else{          // 수정 실패
            responseEntity = ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"msg\": \"상위 카테고리를 다시 지정해 주세요.\"}");
        }
        return responseEntity;
    }


    /**
     * 카테고리 순서 변경
     * @param newOrders: 카테고리 아이디가 순서대로 입력되어 있는 ArrayList
     * @return: 성공, 실패 메세지를 담은 json
     */
    @Operation(summary = "카테고리 순서 변경", description = "파라미터로 받은 카테고리들의 순서를 변경한다.")
    @Parameter(name = "newOrders", description = "수정할 카테고리의 아이디 목록")
    @PutMapping("orders")
    public ResponseEntity<String> modifyOrders(HttpSession session, @RequestParam(value = "newOrders") List<Integer> newOrders){
        for(int i: newOrders){
            log.info(String.valueOf(i));
        }
        // 로그인된 userId 조회
        Long userId = getLoginedUserId(session);

        boolean result;
        // 순서 변경
        result = categoryService.modifyOrders(newOrders, userId);

        ResponseEntity responseEntity;
        if(result){ // 성공
            responseEntity = ResponseEntity.status(HttpStatus.OK)
                    .body("{\"msg\": \"success\"}");
        }else{      // 실패
            responseEntity = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"msg\": \"error\"}");
        }

        return responseEntity;
    }


    /**
     * 카테고리 List를 Map<순서, 카테고리>으로 변환한다.
     * @param categoryDTOList
     * @return categoryMap
     */
    private Map<Long, CategoryDTO> listToMapForCategories(List<CategoryDTO> categoryDTOList){
        Map<Long, CategoryDTO> categoryMap = categoryDTOList
                .stream()
                .collect(Collectors.toMap(
                        i1 -> i1.getOrders().longValue(),
                        i2 -> i2)
                );
        return categoryMap;
    }

    // 세션에 저장되어 있는 로그인된 userId를 categoryDTO에 대입
    private void setLoginedUserId(HttpSession session, CategoryDTO categoryDTO){
        categoryDTO.setUserId((Long)session.getAttribute("loginId"));
    }

    // 세션에 저장되어 있는 로그인된 userId를 조회
    private Long getLoginedUserId(HttpSession session){
        return (Long)session.getAttribute("loginId");
    }

}

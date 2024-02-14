package com.sloop.archive.category.service;

import com.sloop.archive.category.domain.CategoryDTO;
import com.sloop.archive.category.mapper.CategoryMapper;
import com.sloop.archive.exceptions.SloopException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryMapper categoryMapper;

    public List<CategoryDTO> getAllCategories() {
        return categoryMapper.getAllCategories();
    }

    // 하위 카테고리 조회
    public List<CategoryDTO> getAllSubCategoriesByParentId(Long parentId) throws SloopException {
        List<CategoryDTO> resultList;
        try {
            resultList = categoryMapper.getAllSubCategoriesByParentId(parentId);
        }catch (RuntimeException e){
            throw new SloopException();
        }
        return resultList;
    }

    // 키테고리 순서 수정
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean modifyOrders(List<Integer> newOrders, Long userId) {
        Integer result = categoryMapper.updateOrders(newOrders, userId);

        if(result > 0)  {
            return true;
        }
        else {
            return false;
        }
    }

    // 카테고리 생성
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean createCategory(CategoryDTO categoryDTO) throws SloopException {
        log.info("tsetset" + categoryDTO);

        // 상위 카테고리의 존재 여부 확인
        Integer exists = categoryMapper.checkId(categoryDTO.getParentId());
        log.info("exists" + exists);
        if (exists != 1) {    // 존재하지 않을 경우 예외 처리
            log.info("createCategory: throw SloopException");
            throw new SloopException();
        }

        Integer result;
        try {
            // 카테고리 생성
            result = categoryMapper.createCategory(categoryDTO);
        }catch (Exception e){
            log.info("createCategory: throw SloopException");
            throw new SloopException();
        }

        if (result > 0) return true;
        else return false;
    }

    // 카테고리 수정
    @Transactional(rollbackFor = RuntimeException.class)  // [*****]
    public boolean modifyCategory(CategoryDTO categoryDTO) throws RuntimeException {
        boolean status = true;
        // 자기자신을 상위 카테고리로 지정할 수 없다.
        if(categoryDTO.getId() == categoryDTO.getParentId()){
            return false;
        }

        // 카테고리의 기존 상위 카테고리 id와 기존 순서를 조회
        Map<String, Object> map = categoryMapper.selectParentIdAndOrders(categoryDTO.getId());

        if(map == null){    // 기존 카테고리가 존재하지 않는 경우 예외 처리
            throw new SloopException();
        }

        try {
            Integer originalOrders = ObjectToInteger(map.get("orders"));
            Long originalParentId = ObjectToLong(map.get("parentId"));

            // 이름, 사용 여부를 공통적으로 수정한다.
            // 상위 카테고리를 변경한 경우, 새로운 상위 카테고리의 마지막 순서로 지정된다.
            if (originalParentId != categoryDTO.getParentId()) {
                // 기존 상위 카테고리의 하위 카테고리들의 순서를 앞당겨준다.
                categoryMapper.shiftOrders(originalParentId, originalOrders);
                // 새로운 상위 카테고리의 하위 카테고리들의 마지막 순서를 조회한다.
                Integer maxOrders = categoryMapper.getLastOrders(categoryDTO.getParentId());
                // 새로운 상위 카테고리의 마지막에 위치시킨다.
                categoryDTO.setOrders(maxOrders + 1);
            } else {  // 싱위 카테고리를 변경하지 않은 경우, 순서를 유지한다.
                categoryDTO.setOrders(originalOrders);
            }
            categoryMapper.updateCategoryContents(categoryDTO);
        }catch (RuntimeException e){
            throw new RuntimeException();
        }

        return status;
    }

    // 카테고리 조회
    public CategoryDTO getCategory(Long id) throws SloopException {
        CategoryDTO categoryDTO;
        try {
            categoryDTO = categoryMapper.getCategory(id);
        }catch (RuntimeException e){
            throw new SloopException();
        }
        return categoryDTO;
    }

    // Object -> Integer
    private Integer ObjectToInteger(Object object){
        return Integer.parseInt(String.valueOf(object));
    }

    // Object -> Long
    private Long ObjectToLong(Object object){
        return Long.valueOf(String.valueOf(object));
    }
}

package com.sloop.archive.category.mapper;

import com.sloop.archive.category.domain.CategoryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface CategoryMapper {
    List<CategoryDTO> getAllCategories();
    List<CategoryDTO> getAllSubCategoriesByParentId(Long parentId);
    Integer updateOrders(List<Integer> newOrders, @Param("userId") Long userId);
    Integer createCategory(CategoryDTO categoryDTO);
    Map<String, Object> selectParentIdAndOrders(Long id);
    Integer updateCategoryContents(CategoryDTO categoryDTO);
    void shiftOrders(@Param("parentId") Long originalParentId, @Param("orders") Integer originalOrders);
    Integer getLastOrders(Long parentId);

    CategoryDTO getCategory(Long id);

    Integer checkId(Long id);
}

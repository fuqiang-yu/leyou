package com.leyou.item.controller;


import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.service.TbCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 返回json内容
 */
@RestController
public class CategoryController {


    @Autowired
    private TbCategoryService categoryService;

    /**
     * 根据pid查询该分类下的所有分类列表
     */
    //http://api.leyou.com/api/item/category/of/parent?pid=0
    @GetMapping("/category/of/parent")
    public ResponseEntity<List<CategoryDTO>> findByParentId(@RequestParam("pid")Long pid){

        List<CategoryDTO> list = categoryService.findByParentId(pid);
        return ResponseEntity.status(HttpStatus.OK).body(list);
    }


    /**
     * http://api.leyou.com/api/item/category/of/brand/?id=1528
     * 品牌新增中分类数据回显的方法
     */
    @GetMapping("/category/of/brand")
    public ResponseEntity<List<CategoryDTO>> findListByBrandId(@RequestParam("id")Long brandId){

        return ResponseEntity.ok(categoryService.findListByBrandId(brandId));
    }
}

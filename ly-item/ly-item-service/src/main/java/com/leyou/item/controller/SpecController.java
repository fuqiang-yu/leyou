package com.leyou.item.controller;

import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpecParamDTO;
import com.leyou.item.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 处理规格参数组 和 规格参数
 */
@RestController
@RequestMapping("/spec")
public class SpecController {

    @Autowired
    private SpecService specService;

    /**
     * 根据分类id 查询 分组集合
     * @param categoryId
     * @return
     */
    @GetMapping("/groups/of/category")
    public ResponseEntity<List<SpecGroupDTO>> findSpecGroupListByCid(@RequestParam(name = "id")Long categoryId){
        return ResponseEntity.ok(specService.findSpecGroupListByCid(categoryId));
    }

    /**
     * 根据条件查询 规格参数
     * @param groupId
     * @return
     */
    @GetMapping("/params")
    public ResponseEntity<List<SpecParamDTO>> findSpecParamsList(@RequestParam(name = "gid",required = false)Long groupId,
                                                                 @RequestParam(name = "cid",required = false)Long categoryId){
        return ResponseEntity.ok(specService.findSpecParamsList(groupId,categoryId));
    }
}

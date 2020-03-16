package com.leyou.search.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.search.po.GoodsDTO;
import com.leyou.search.po.SearchRequest;
import com.leyou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;
    /**
     * 关键词检索
     * @return
     */
    @PostMapping("/page")
    public ResponseEntity<PageResult<GoodsDTO>> search(@RequestBody SearchRequest searchRequest){
        return ResponseEntity.ok(searchService.search(searchRequest));
    }

    /**
     * 获取过滤条件
     * @param searchRequest
     * @return
     */
    @PostMapping("/filter")
    public ResponseEntity<Map<String, List<?>>> filter(@RequestBody SearchRequest searchRequest){
        return ResponseEntity.ok(searchService.searchFilter(searchRequest));
    }
}

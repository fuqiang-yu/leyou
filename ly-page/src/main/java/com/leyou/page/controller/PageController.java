package com.leyou.page.controller;

import com.leyou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class PageController {

    @Autowired
    private PageService pageService;
    /**
     * 显示商品详情页
     * @return
     */
    @GetMapping("/item/{id}.html")
    public String showItemPage(Model model,
                               @PathVariable(name = "id")Long spuId){
//        获取模板页面需要的动态数据
        Map<String, Object> itemData = pageService.loadItemData(spuId);
//        把数据返回给模板页面
        model.addAllAttributes(itemData);
        return "item";
    }
}

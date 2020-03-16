package com.leyou.page.service;

import com.leyou.item.dto.*;
import com.lytou.item.client.ItemClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PageService {

    @Autowired
    private ItemClient itemClient;
    /**
     * 获取 模板页面需要的动态数据
     * 使用远程调用 feign
     * @param spuId
     */
    public Map<String,Object> loadItemData(Long spuId) {
//        获取spu对象
        SpuDTO spuDTO = itemClient.findSpuById(spuId);
//        分类id的集合
        List<Long> categoryIds = spuDTO.getCategoryIds();
//        categories  spu的分类集合分类1，分类2，分类3
//        获取分类对象的集合
        List<CategoryDTO> categoryDTOList = itemClient.findCategoryListByIds(categoryIds);
//        brand       spu的品牌对象
        Long brandId = spuDTO.getBrandId();
        BrandDTO brandDTO = itemClient.findbrandById(brandId);
//        spuName     spu的名字
        String spuName = spuDTO.getName();
//        subTitle    促销信息
        String subTitle = spuDTO.getSubTitle();
//        detail      spuDetail的对象
        SpuDetailDTO spuDetailDTO = itemClient.findSpuDetailBySpuId(spuId);
//        skus        sku的集合
        List<SkuDTO> skuDTOList = itemClient.findSkuListBySpuId(spuId);
//        specs       获取规格组 和 规格名字的对应
        List<SpecGroupDTO> groupDTOList = itemClient.findSpecsByCid(spuDTO.getCid3());

        Map<String,Object> map = new HashMap<>();
        map.put("categories",categoryDTOList);
        map.put("brand",brandDTO);
        map.put("spuName",spuName);
        map.put("subTitle",subTitle);
        map.put("detail",spuDetailDTO);
        map.put("skus",skuDTOList);
        map.put("specs",groupDTOList);
        return map;
    }

    @Autowired
    private SpringTemplateEngine templateEngine;

//    静态页目录
    private String pagePath = "C:\\workspace\\heima-jee121\\nginx-1.12.2\\html\\item";
    /**
     * 生成静态页面
     */
    public void createHtml(Long spuId){
//        context上下文
        Context context = new Context();
//        获取动态数据
        Map<String, Object> data = this.loadItemData(spuId);
//        把动态数据写入context
        context.setVariables(data);
//        模板解析器（springboot已经整合）
//        静态页的目录
        File dir = new File(pagePath);
        if(!dir.exists()){
            dir.mkdir();
        }
        PrintWriter writer = null;
//        构造静态页面
        File file = new File(dir, spuId + ".html");
        try{
//        构建写文件用的IO
            writer = new PrintWriter(file,"UTF-8");
//        使用templateEngine 生成静态页面
            templateEngine.process("item",context,writer);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            writer.close();
        }
    }
}

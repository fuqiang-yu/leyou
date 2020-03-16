package com.leyou.item.controller;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.entity.TbBrand;
import com.leyou.item.service.TbBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class BrandController {


    @Autowired
    private TbBrandService brandService;

    //http://api.leyou.com/api/item/brand/page?key=&page=1&rows=5&sortBy=id&desc=true

    /**
     * 品牌分页请求
     * @return
     * key:关键字   page：pageNo  rows：pageSize   sortBy：排序的列名  desc 排序的方式
     *
     */
    @GetMapping("/brand/page")
    public ResponseEntity<PageResult<BrandDTO>> queryBrandByPage(@RequestParam(value = "key",required = false)String key,
                                                                 @RequestParam(value = "page",defaultValue = "1")Integer page,
                                                                 @RequestParam(value = "rows",defaultValue = "10")Integer rows,
                                                                 @RequestParam(value = "sortBy",required = false)String sortBy,
                                                                 @RequestParam(value = "desc",defaultValue = "false")Boolean desc){
        return ResponseEntity.status(HttpStatus.OK).body(brandService.queryBrandByPage(key,page,rows,sortBy,desc));

    }

    /**
     * 品牌新增
     * http://api.leyou.com/api/item/brand
     * 参数：品牌对象，品牌和分类关联的分类id（cids）
     */
    @PostMapping("/brand")
    public ResponseEntity<Void> saveBrand(BrandDTO brandDTO,@RequestParam("cids") List<Long> cids){
        brandService.saveBrand(brandDTO,cids);
        //HttpStatus.CREATED = 201
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    /**
     * 查询品牌集合
     * @param ids
     * @return
     */
    @GetMapping("/list")
    public ResponseEntity<List<BrandDTO>> findBrandsByIds(@RequestParam(name = "ids") List<Long> ids){
        Collection<TbBrand> tbBrandCollection = brandService.listByIds(ids);
        if(CollectionUtils.isEmpty(tbBrandCollection)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        List<BrandDTO> brandDTOList = tbBrandCollection.stream().map(tbBrand -> {
            return BeanHelper.copyProperties(tbBrand, BrandDTO.class);
        }).collect(Collectors.toList());
        return ResponseEntity.ok(brandDTOList);
    }
}

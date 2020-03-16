package com.lytou.item.client;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * item服务的feign接口
 */
@FeignClient("item-service")//指定当前接口是哪个微服务的feign接口
public interface ItemClient {

    /**
     * 根据spuid  查询sku集合
     * @param spuId
     * @return
     */
    @GetMapping("/sku/of/spu")
    List<SkuDTO> findSkuListBySpuId(@RequestParam(name = "id") Long spuId);

    /**
     * 根据条件查询 规格参数
     * @param groupId
     * @return
     */
    @GetMapping("/spec/params")
    List<SpecParamDTO> findSpecParamsList(@RequestParam(name = "gid", required = false) Long groupId,
                                          @RequestParam(name = "cid", required = false) Long categoryId,
                                          @RequestParam(name = "searching", required = false) Boolean searching);


    /**
     * 根据spuid 查询spudetail对象
     * @param spuId
     * @return
     */
    @GetMapping("/spu/detail")
    SpuDetailDTO findSpuDetailBySpuId(@RequestParam(name = "id") Long spuId);

    /**
     * 分页查询spu信息
     * @param page
     * @param rows
     * @param key
     * @param saleable
     * @return
     */
    @GetMapping("/spu/page")
    PageResult<SpuDTO> findSpuByPage(@RequestParam(name = "page", defaultValue = "1") Integer page,
                                     @RequestParam(name = "rows", defaultValue = "5") Integer rows,
                                     @RequestParam(name = "key", required = false) String key,
                                     @RequestParam(name = "saleable", required = false) Boolean saleable
    );
    /**
     * 根据品牌id批量查询品牌
     * @param idList 品牌id的集合
     * @return 品牌的集合
     */
    @GetMapping("/brand/list")
    List<BrandDTO> findBrandsByIds(@RequestParam("ids") List<Long> idList);
    /**
     * 根据分类id 集合 查询分类集合
     * @param ids
     * @return
     */
    @GetMapping("/category/list")
    List<CategoryDTO> findCategoryListByIds(@RequestParam(name = "ids")List<Long> ids);
    /**
     * 根据主键id 查询spu对象
     * @param spuId
     * @return
     */
    @GetMapping("/spu/{id}")
    SpuDTO findSpuById(@PathVariable(name = "id")Long spuId);

    /**
     * 根据主键id 查询品牌对象
     * @param brandId
     * @return
     */
    @GetMapping("/brand/{id}")
    BrandDTO findbrandById(@PathVariable(name = "id") Long brandId);
    /**
     * 根据 品牌id集合获取品牌对象及合
     * @param ids
     * @return
     */
    @GetMapping("/brand/list")
    List<BrandDTO> findBrandListByIds(@RequestParam(name = "ids")List<Long> ids);

    /**
     * 查询规格参数组，及组内参数
     * @param id 商品分类id
     * @return 规格组及组内参数
     */
    @GetMapping("/spec/of/category")
    List<SpecGroupDTO> findSpecsByCid(@RequestParam("id") Long id);
}

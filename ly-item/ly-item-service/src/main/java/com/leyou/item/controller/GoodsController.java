package com.leyou.item.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.SkuDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.dto.SpuDetailDTO;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理 spu spuDetail  sku
 */
@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /**
     * 分页查询spu信息
     * @param page
     * @param rows
     * @param key
     * @param saleable
     * @return
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuDTO>> findSpuByPage(@RequestParam(name="page",defaultValue = "1")Integer page,
                                                            @RequestParam(name="rows",defaultValue = "5")Integer rows,
                                                            @RequestParam(name="key",required = false)String key,
                                                            @RequestParam(name="saleable",required = false)Boolean saleable
    ){
        return ResponseEntity.ok(goodsService.findSpuByPage(page,rows,key,saleable));
    }

    /**
     * 新增spu
     * @param spuDTO
     * @return
     */
    @PostMapping("/goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuDTO spuDTO){
        goodsService.saveGoods(spuDTO);
        return ResponseEntity.noContent().build();
    }
    /**
     * 修改上下架状态
     * @param spuId
     * @param saleable
     * @return
     */
    @PutMapping("/spu/saleable")
    public ResponseEntity<Void> updateSaleable(@RequestParam(name = "id")Long spuId,
                                               @RequestParam(name = "saleable")Boolean saleable){
        goodsService.updateSaleable(spuId,saleable);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据spuid 查询spudetail对象
     * @param spuId
     * @return
     */
    @GetMapping("/spu/detail")
    public ResponseEntity<SpuDetailDTO> findSpuDetailBySpuId(@RequestParam(name = "id")Long spuId){
        return ResponseEntity.ok(goodsService.findSpuDetailBySpuId(spuId));
    }

    /**
     * 根据spuid  查询sku集合
     * @param spuId
     * @return
     */
    @GetMapping("/sku/of/spu")
    public ResponseEntity<List<SkuDTO>> findSkuListBySpuId(@RequestParam(name = "id")Long spuId){
        return ResponseEntity.ok(goodsService.findSkuListBySpuId(spuId));
    }

    /**
     * 修改商品信息
     * @param spuDTO
     * @return
     */
    @PutMapping("/goods")
    public ResponseEntity<Void> updateGoods(@RequestBody SpuDTO spuDTO){
        goodsService.updateGoods(spuDTO);
        return ResponseEntity.noContent().build();
    }
}

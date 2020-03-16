package com.leyou.search.test;

import com.leyou.common.vo.PageResult;
import com.leyou.item.dto.SpuDTO;
import com.leyou.search.po.Goods;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.SearchService;
import com.lytou.item.client.ItemClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestDB2ES {

    @Autowired
    private SearchService searchService;
    @Autowired
    private GoodsRepository repository;
    @Autowired
    private ItemClient itemClient;
    /**
     * 把数据库数据导入es
     * 构造goods 对象写入es
     */
    @Test
    public void db2Es(){

        int page=1;
        int rows =50;
        while(true){
    //        远程调用item获取spuDTO
            PageResult<SpuDTO> pageResult = itemClient.findSpuByPage(page, rows, null, true);
            if(pageResult == null || CollectionUtils.isEmpty(pageResult.getItems())){
                break;
            }
    //        分页结果集
            List<SpuDTO> spuDTOList = pageResult.getItems();
    //        goods对象集合
            List<Goods> goodsList = new ArrayList<>();
            for (SpuDTO spuDTO : spuDTOList) {
                //        构造goods 对象
                Goods goods = searchService.createGoods(spuDTO);
                goodsList.add(goods);
            }
    //        把goods 对象写入es,批量操作
            repository.saveAll(goodsList);
            if(spuDTOList.size()<rows){
                break;
            }
            page++;
        }
    }
}

package com.leyou;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
public class ItemController {

    @Autowired
    private ItemService itemService;


    /**
     * {
     *     "timestamp": "2020-02-29T01:32:47.032+0000",
     *     "status": 500,
     *     "error": "Internal Server Error",
     *     "message": "参数错误,没传价格",
     *     "path": "/item"
     * }
     * @param item
     * @return
     */

    //@RequestMapping(value = "/item",method = RequestMethod.POST)
    @PostMapping("item")
    public ResponseEntity<Item> saveItem(@RequestBody Item item){
        // 如果价格为空，则抛出异常，返回400状态码，请求参数有误
        if(item.getPrice() == null){
           // return ResponseEntity.status(400).body(null);
           // throw new RuntimeException("参数错误,没传价格");

            //抛自定义异常的目的是为了，封装后更好的管理返回内容，统一返回内容
            //将状态码和文字进行封装枚举类型处理，更加规范
            throw new LyException(ExceptionEnum.INVALID_PRICE);
        }
        Item result = itemService.saveItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
        //return result;
    }
}
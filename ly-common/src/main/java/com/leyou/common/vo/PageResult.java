package com.leyou.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor   //无参构造方法
@AllArgsConstructor  //全参构造方法
public class PageResult<T> {

    private Long total;     //总条数
    private Long totalPage; //总页数
    private List<T> items;     //当前页数据

}

package com.leyou.common.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 异常拦截增强类
 */
@ControllerAdvice
public class BasicExceptionAdvice {


    /**
     *  通过ExceptionHandler 捕获异常，参数是要捕获异常的类
     * @param e
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleException(RuntimeException e){
         return ResponseEntity.status(400).body(e.getMessage());
    }

}

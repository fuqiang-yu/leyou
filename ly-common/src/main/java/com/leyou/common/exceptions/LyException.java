package com.leyou.common.exceptions;

import com.leyou.common.enums.ExceptionEnum;

/**
 * 自定义异常类，继承RuntimeException
 */
public class LyException extends RuntimeException {

    public LyException(ExceptionEnum en) {
    }
}

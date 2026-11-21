package com.bookmgt.handler;

import com.bookmgt.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result handleMaxUpload() {
        log.info("文件最大不能超过10MB");
        return Result.error("文件最大不能超过10MB");
    }

    @ExceptionHandler(Exception.class)
    public Result handleAllException(Exception e) {
        log.info("服务器内部异常，请稍后重试", e);
        return Result.error("服务器内部异常，请稍后重试");
    }
}

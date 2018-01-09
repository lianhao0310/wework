package com.emily.domain;
import com.alice.emily.utils.LOG;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * Created by emily on 2018/1/8.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger log = LOG.getLogger(GlobalExceptionHandler.class);

    /**
     * 认证异常处理，比如：400
     *
     * @param e
     * @return
     * @throws Exception
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)  // 400
    @ExceptionHandler(value = BindException.class)
    @ResponseBody
    public ResponseData BindErrorHandler( HttpServletResponse response, BindException e) throws Exception {
        ResponseData responseData = new ResponseData();
        if (null != e.getAllErrors() && null != e.getAllErrors().get(0)) {
            responseData.setMessage(e.getAllErrors().get(0).getDefaultMessage());
        }
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        responseData.setCode(HttpStatus.BAD_REQUEST.value());
        responseData.setStatus(false);
        return responseData;
    }

    /**
     * 系统异常处理，比如：404,500
     *
     * @param request
     * @param response
     * @param e
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseData defaultErrorHandler(HttpServletRequest request, HttpServletResponse response, Exception e) throws Exception {
        ResponseData responseData = new ResponseData();
        String message = e.getMessage();
        Integer code = 500;
        if (e instanceof org.springframework.web.servlet.NoHandlerFoundException) {
            code = 404;
        }
        response.setStatus(code);
        responseData.setCode(code);
        responseData.setMessage(message);
        responseData.setStatus(false);
        log.error(e);
        return responseData;
    }
}


package org.personalproj.shortlink.admin.common.convention.exception;

import org.personalproj.shortlink.admin.common.convention.errorcode.IStatusCode;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.common.convention.exception
 * @Author: PzF
 * @CreateTime: 2024-01-27  18:14
 * @Description: 抽象的异常类，对于所有的异常进行基础规约
 * @Version: 1.0
 */
public class AbstractException extends RuntimeException{

    public final String errorCode;

    public final String errorMessage;

    /**
     * @description: 抽象类的构造器，通过传入的异常，描述信息，以及错误码初始化异常的错误状态码以及错误信息，这个抽象类的子类可以被全局拦截器拦截
     * @author: PzF
     * @date: 2024/1/27 18:27
     * @param: [error: 发生的错误对象, message：可以传入自己描述的错误信息, errorCode：自己定义的错误码]
     **/
    public AbstractException(String message, Throwable error, IStatusCode errorCode){
        super(message,error);
        this.errorCode = errorCode.code();
        this.errorMessage = Optional.ofNullable(StringUtils.hasLength(message) ? message : null).orElse(errorCode.message());
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}

package org.personalproj.shortlink.common.convention.result;

import org.personalproj.shortlink.common.convention.errorcode.BaseErrorCode;
import org.personalproj.shortlink.common.convention.exception.AbstractException;

import java.util.Optional;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.common.convention.result
 * @Author: PzF
 * @CreateTime: 2024-01-28  10:53
 * @Description: 根据IStatusCode接口的实现类对象，构建Result返回
 * @Version: 1.0
 */
public class Results {

    /**
     * @description: 成功结果的返回
     * @author: PzF
     * @date: 2024/1/28 10:55
     * @param: [data]
     * @return: org.personalproj.shortlink.common.result.convention.Result<T>
     **/
    public static <T> Result<T> success(T data) {
        return new Result<T>().setData(data).setCode(Result.SUCCESS_CODE);
    }

    public static Result<Void> success() {
        return new Result<Void>().setCode(Result.SUCCESS_CODE);
    }


    /**
     * @description: 基础服务端异常的Result
     * @author: PzF
     * @date: 2024/1/28 11:03
     * @param: []
     * @return: org.personalproj.shortlink.common.result.convention.Result<java.lang.Void>
     **/
    public static Result<Void> failure() {
        return new Result<Void>()
                .setCode(BaseErrorCode.SERVICE_ERROR.code())
                .setMessage(BaseErrorCode.SERVICE_ERROR.message());
    }


    /**
     * @description: 根据传入的异常中的code以及message来生成对应的Result
     * @author: PzF
     * @date: 2024/1/28 11:05
     * @param: [exception]
     * @return: org.personalproj.shortlink.common.result.convention.Result<java.lang.Void>
     **/
    public static Result<Void> failure(AbstractException exception) {
        String errorCode = Optional.ofNullable(exception.getErrorCode())
                .orElse(BaseErrorCode.SERVICE_ERROR.code());
        String errorMessage = Optional.ofNullable(exception.getErrorMessage())
                .orElse(BaseErrorCode.SERVICE_ERROR.message());
        return new Result<Void>()
                .setCode(errorCode)
                .setMessage(errorMessage);
    }


    /**
     * @description: 直接根据传入的errorCode以及errorMessage生成Result
     * @author: PzF
     * @date: 2024/1/28 11:06
     * @param: [errorCode, errorMessage]
     * @return: org.personalproj.shortlink.common.result.convention.Result<java.lang.Void>
     **/
    public static Result<Void> failure(String errorCode, String errorMessage) {
        return new Result<Void>().setCode(errorCode).setMessage(errorMessage);
    }
}

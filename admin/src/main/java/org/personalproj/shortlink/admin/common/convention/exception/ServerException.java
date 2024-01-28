package org.personalproj.shortlink.admin.common.convention.exception;

import org.personalproj.shortlink.admin.common.convention.errorcode.BaseErrorCode;
import org.personalproj.shortlink.admin.common.convention.errorcode.IStatusCode;

import java.util.Optional;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.common.convention.exception
 * @Author: PzF
 * @CreateTime: 2024-01-27  18:44
 * @Description: 服务端异常
 * @Version: 1.0
 */
public class ServerException extends AbstractException{
    public ServerException(String message) {
        this(message, null, BaseErrorCode.SERVICE_ERROR);
    }

    public ServerException(IStatusCode errorCode) {
        this(null, errorCode);
    }

    public ServerException(String message, IStatusCode errorCode) {
        this(message, null, errorCode);
    }

    public ServerException(String message, Throwable throwable, IStatusCode errorCode) {
        super(Optional.ofNullable(message).orElse(errorCode.message()), throwable, errorCode);
    }

    @Override
    public String toString() {
        return "ServiceException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}

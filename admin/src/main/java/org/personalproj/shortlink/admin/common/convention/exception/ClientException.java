package org.personalproj.shortlink.admin.common.convention.exception;

import org.personalproj.shortlink.admin.common.convention.errorcode.BaseErrorCode;
import org.personalproj.shortlink.admin.common.convention.errorcode.IStatusCode;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.admin.common.convention.exception
 * @Author: PzF
 * @CreateTime: 2024-01-27  18:43
 * @Description: TODO
 * @Version: 1.0
 */
public class ClientException extends AbstractException{
    public ClientException(IStatusCode errorCode) {
        this(null, null, errorCode);
    }

    public ClientException(String message) {
        this(message, null, BaseErrorCode.CLIENT_ERROR);
    }

    public ClientException(String message, IStatusCode errorCode) {
        this(message, null, errorCode);
    }

    public ClientException(String message, Throwable throwable, IStatusCode errorCode) {
        super(message, throwable, errorCode);
    }

    @Override
    public String toString() {
        return "ClientException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}

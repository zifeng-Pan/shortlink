package org.personalproj.shortlink.common.convention.exception;

import org.personalproj.shortlink.common.convention.errorcode.BaseErrorCode;
import org.personalproj.shortlink.common.convention.errorcode.IStatusCode;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.common.convention.exception
 * @Author: PzF
 * @CreateTime: 2024-01-27  18:44
 * @Description: 远程调用异常
 * @Version: 1.0
 */
public class RemoteException extends AbstractException{
    public RemoteException(String message) {
        this(message, null, BaseErrorCode.REMOTE_ERROR);
    }

    public RemoteException(String message, IStatusCode errorCode) {
        this(message, null, errorCode);
    }

    public RemoteException(String message, Throwable throwable, IStatusCode errorCode) {
        super(message, throwable, errorCode);
    }

    @Override
    public String toString() {
        return "RemoteException{" +
                "code='" + errorCode + "'," +
                "message='" + errorMessage + "'" +
                '}';
    }
}

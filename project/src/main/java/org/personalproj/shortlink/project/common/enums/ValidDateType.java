package org.personalproj.shortlink.project.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 *
 * 有效期类型
 */
@RequiredArgsConstructor
public enum ValidDateType {
    /**
     *
     * 永久有效期
     */
    PERMANENT(0),

    /**
     *
     * 用户自定义有效期时间
     */
    CUSTOM(1);

    /**
     *
     * 有效期类型状态码
     */
    @Getter
    private final int validStatueCode;
}

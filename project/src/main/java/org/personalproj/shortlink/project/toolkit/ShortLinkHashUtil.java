package org.personalproj.shortlink.project.toolkit;

import cn.hutool.core.lang.hash.MurmurHash;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: org.personalproj.shortlink.project.toolkit
 * @Author: PzF
 * @CreateTime: 2024-02-01  14:00
 * @Description: 原始连接转换成短链接过程中使用的哈希工具类
 * 主要功能就是将原始链接这一字符串，首先转换成十进制，再转换成62进制[6位]
 * @Version: 1.0
 */
public class ShortLinkHashUtil {

    private static final char[] CHARS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    private static final int SIZE = CHARS.length;


    public static final int GENERATE_RETRY_TIMES = 10;


    /**
     * @description: 将字符串转化得到的10进制数转换成62进制数
     * @author: PzF
     * @date: 2024/3/3 13:22
     * @param: [num]
     * @return: java.lang.String
     **/
    private static String convertDec2Base62(long num) {
        StringBuilder stringBuilder = new StringBuilder();
        while (num > 0) {
            int i = (int) (num % SIZE);
            stringBuilder.append(CHARS[i]);
            num /= SIZE;
        }
        return stringBuilder.reverse().toString();
    }

    /**
     * @description: 将字符串转换成62进制的6位字符串
     * @author: PzF
     * @date: 2024/3/3 13:20
     * @param: [string]
     * @return: java.lang.String
     **/
    public static String hash2Base62String(String string) {
        int hash32 = MurmurHash.hash32(string);
        long decNum = hash32 < 0 ? Integer.MAX_VALUE - (long) hash32 : hash32;
        return convertDec2Base62(decNum);
    }


}

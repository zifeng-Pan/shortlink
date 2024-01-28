package org.personalproj.shortlink.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author panzifeng
 * @date 2024/1/26 22:03
 * @description ShortLinkAdminApplication
 */

@SpringBootApplication
@MapperScan("org.personalproj.shortlink.admin.dao.mapper")
public class ShortLinkAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShortLinkAdminApplication.class,args);
    }
}

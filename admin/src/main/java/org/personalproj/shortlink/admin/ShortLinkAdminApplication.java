package org.personalproj.shortlink.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @author panzifeng
 * @date 2024/1/26 22:03
 * @description ShortLinkAdminApplication
 */

@SpringBootApplication
@MapperScan("org.personalproj.shortlink.admin.dao.mapper")
@Import(value = {
        org.personalproj.shortlink.common.config.MyMetaObjectHandler.class,
        org.personalproj.shortlink.common.web.GlobalExceptionHandler.class,
        org.personalproj.shortlink.common.config.MyBatisConfiguration.class
})
public class ShortLinkAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShortLinkAdminApplication.class,args);
    }
}

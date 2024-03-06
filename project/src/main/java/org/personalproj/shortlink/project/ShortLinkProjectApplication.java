package org.personalproj.shortlink.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

/**
 * @BelongsProject: shortlink
 * @BelongsPackage: PACKAGE_NAME
 * @Author: PzF
 * @CreateTime: 2024-02-01  13:56
 * @Description: 短链接核心模块启动组件
 * @Version: 1.0
 */
@SpringBootApplication
@Import(value = {
        org.personalproj.shortlink.common.web.GlobalExceptionHandler.class,
        org.personalproj.shortlink.common.config.MyMetaObjectHandler.class,
        org.personalproj.shortlink.common.config.MyBatisConfiguration.class
})
@MapperScan("org.personalproj.shortlink.project.dao.mapper")
public class ShortLinkProjectApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShortLinkProjectApplication.class);
    }
}

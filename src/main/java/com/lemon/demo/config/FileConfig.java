package com.lemon.demo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Author lemon
 * @Date 2021/8/19
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "file")
public class FileConfig {
    String path;
}

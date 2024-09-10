package com.insigma;

import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.vs.sqlmapper.spring.scan.VSDaoBeanScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication(scanBasePackages = {"com.human_resource", "com.vs","com.insigma"})
@VSDaoBeanScan(basePackages = {"com.vs","com.human_resource"})
@Import({DataSourceConfig.class,com.vs.sqlmapper.spring.DataSourceConfig.class})
public class InsiisWebApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(InsiisWebApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(InsiisWebApplication.class);
    }

}

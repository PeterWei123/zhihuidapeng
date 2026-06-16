package com.qkd.iotserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@SpringBootApplication
public class IotServerApplication implements WebMvcConfigurer {

    @Bean // websocket开关
    public ServerEndpointExporter exporter() {
        return new ServerEndpointExporter();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/file/**").addResourceLocations("file:C:/create/file/");
    }

    public static void main(String[] args) {

        SpringApplication.run(IotServerApplication.class, args);
        System.out.println("java项目启动成功");
    }

}

package cn.edu.xmu.order;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
     * @author jwy
     **/
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.ooad","cn.edu.xmu.order"})
@MapperScan("cn.edu.xmu.order.mapper")
@EnableDubbo(scanBasePackages = "cn.edu.xmu.order.service.impl")
@EnableDiscoveryClient
public class OrderServiceApplication {
    public static void main(String[] args) {
            SpringApplication.run(OrderServiceApplication.class, args);
        }

}


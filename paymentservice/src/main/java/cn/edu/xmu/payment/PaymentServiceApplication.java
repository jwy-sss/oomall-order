package cn.edu.xmu.payment;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author jwy
 **/
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.ooad","cn.edu.xmu.payment"})
@MapperScan("cn.edu.xmu.payment.mapper")
@EnableDubbo(scanBasePackages = "cn.edu.xmu.payment.service.impl")
@EnableDiscoveryClient
public class PaymentServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(cn.edu.xmu.payment.PaymentServiceApplication.class, args);
    }

}
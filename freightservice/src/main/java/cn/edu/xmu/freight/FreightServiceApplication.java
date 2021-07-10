package cn.edu.xmu.freight;


import cn.edu.xmu.freight.dao.FreightDao;
import cn.edu.xmu.freight.service.impl.FreightCaculateServiceImpl;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author jwy
 **/
@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.ooad","cn.edu.xmu.freight"})
@MapperScan("cn.edu.xmu.freight.mapper")
@EnableDubbo(scanBasePackages = "cn.edu.xmu.freight.service.impl")
@EnableDiscoveryClient
public class FreightServiceApplication implements ApplicationRunner {
    public static void main(String[] args) {
        SpringApplication.run(FreightServiceApplication.class, args);
    }

    @Autowired
    FreightDao freightDao;

    @Autowired
    FreightCaculateServiceImpl freightCaculateService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        freightDao.initialize();
//        List<GoodsVo> goodsVos=new ArrayList<>();
//        GoodsVo goodsVo1=new GoodsVo();
//        goodsVo1.setGoodsSkuId(1L);
//        goodsVo1.setQuantity(8);
//
//        GoodsVo goodsVo2=new GoodsVo();
//        goodsVo2.setGoodsSkuId(2L);
//        goodsVo2.setQuantity(4);
//
//        GoodsVo goodsVo3=new GoodsVo();
//        goodsVo3.setGoodsSkuId(3L);
//        goodsVo3.setQuantity(9);
//
//        goodsVos.add(goodsVo1);
//        goodsVos.add(goodsVo2);
//        goodsVos.add(goodsVo3);
//        System.out.println(freightCaculateService.caculateFreight(goodsVos,11L,1L));
    }
}

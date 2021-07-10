package cn.edu.xmu.goodsservice.model.bo;

import cn.edu.xmu.goodsservice.model.po.GoodsSkuPo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yancheng Lai
 * createdBy Yancheng Lai 2020/12/1 19:48
 * modifiedBy Yancheng Lai 19:48
 **/
@Data
public class GoodsSku implements  Serializable {


    public GoodsSku() {

    }
    Long id;
    String skuSn;
    String name;
    Long originalPrice;
    String configuration;
    Long weight;
    String imageUrl;
    Integer inventory;
    String detail;
    LocalDateTime gmtCreate;
    LocalDateTime gmtModified;
    Byte disabled;
    Long goodsSpuId;
    private Byte statecode;
    public enum State {
        WAITING(0, "未上架"),
        INVALID(4, "上架"),
        DELETED(6, "已删除");

        private static final Map<Integer, State> stateMap;

        static { //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
            stateMap = new HashMap();
            for (State enum1 : values()) {
                stateMap.put(enum1.code, enum1);
            }
        }

        private int code;
        private String description;

        State(int code, String description) {
            this.code = code;
            this.description = description;
        }


        public static State getTypeByCode(Byte code) {
            return stateMap.get(code);
        }

        public int getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

}

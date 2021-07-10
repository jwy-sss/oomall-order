package cn.edu.xmu.freight.model.bo;

import cn.edu.xmu.freight.model.po.FreightModelPo;
import cn.edu.xmu.freight.model.vo.FreightRetVo;
import cn.edu.xmu.ooad.model.VoObject;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 创建运费模板bo
 * @author wwq
 * @date created in 2020/11/28 20:28
 */
@Data
public class Freight implements VoObject, Serializable {
    /**
     * 构造函数
     */
    public Freight(){}

    /**
     * 后台运费模板状态
     */
    //无相关状态码

    /**
     * 运费模板类型
     */
    public enum FreightType {
        WEIGHT(0, "以重量计算"),
        PIECE(1, "以数量计算");

        private static final Map<Integer, FreightType> freightTypeMap;

        //由类加载机制，静态块初始加载对应的枚举属性到map中，而不用每次取属性时，遍历一次所有枚举值
        static {
            freightTypeMap = new HashMap();
            for (FreightType enum1 : values()) {
                freightTypeMap.put(enum1.code, enum1);
            }
        }

        private int code;

        private String description;

        FreightType(int code, String description) {
            this.code = code;
            this.description = description;
        }

        public static FreightType getTypeByCode(Integer code) {
            return freightTypeMap.get(code);
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }
    private Long id;
    private Long shopId;
    private String name;
    private Byte type;
    private Boolean defaultModel;
    private Integer unit;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;

    @Override
    public FreightRetVo createVo(){return new FreightRetVo(this);
    }

    @Override
    public Object createSimpleVo() {
        return null;
    }


    public Freight(FreightModelPo po){
        this.id=po.getId();
        this.name=po.getName();
        this.type=po.getType();
        this.unit=po.getUnit();
        if(po.getDefaultModel()==1)
            this.defaultModel=true;
        else
            this.defaultModel=false;
        this.shopId= po.getShopId();
        this.gmtCreate=po.getGmtCreate();
        this.gmtModified=po.getGmtModified();
    }

    /**
     * 用bo对象创建po对象
     * @return
     */
    public FreightModelPo gotFreightPo(){
        FreightModelPo po=new FreightModelPo();
        po.setId(this.id);
        po.setShopId(this.shopId);
        po.setName(this.name);
        po.setType(this.type);
        if(this.defaultModel==null||this.defaultModel==false)
            po.setDefaultModel((byte)0);
        else
            po.setDefaultModel((byte)1);
        po.setUnit(this.unit);
        po.setGmtCreate(this.gmtCreate);
        po.setGmtModified(this.gmtModified);
        return po;
    }
}

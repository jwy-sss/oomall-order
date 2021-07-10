package cn.edu.xmu.order.util;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;

import java.util.*;

public class Common2 {


    /**
     * 处理返回对象
     * @param returnObject 返回的对象
     * @return
     */
    public static Object getInsertOrderRetObject(ReturnObject<VoObject> returnObject,String OrderSn) {
        ResponseCode code = returnObject.getCode();
        switch (code){
            case OK:
                if (OrderSn != null){
                    return ResponseUtil.ok(OrderSn);
                }else{
                    return ResponseUtil.ok();
                }
            default:
                return ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg());
        }
    }

    /**
     * 处理分页返回对象,返回SimpleRetVo
     * @param returnObject 返回的对象
     * @return
     */
    public static Object getPageRetObject(ReturnObject<PageInfo<VoObject>> returnObject) {
        ResponseCode code = returnObject.getCode();
        switch (code){
            case OK:

                PageInfo<VoObject> objs = returnObject.getData();
                if (objs != null){
                    List<Object> voObjs = new ArrayList<>(objs.getList().size());
                    for (Object data : objs.getList()) {
                        if (data instanceof VoObject) {
                            voObjs.add(((VoObject)data).createSimpleVo());
                        }
                    }

                    Map<String, Object> ret = new HashMap<>();
                    ret.put("list", voObjs);
                    ret.put("total", objs.getTotal());
                    ret.put("page", objs.getPageNum());
                    ret.put("pageSize", objs.getPageSize());
                    ret.put("pages", objs.getPages());
                    return ResponseUtil.ok(ret);
                }else{
                    return ResponseUtil.ok();
                }
            default:
                return ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg());
        }
    }

    /**
     * 处理返回对象,返回simpleRetVo对象
     * @param returnObject 返回的对象
     * @return
     */
    public static Object getListRetObject(ReturnObject<List> returnObject) {
        ResponseCode code = returnObject.getCode();
        switch (code){
            case OK:
                List objs = returnObject.getData();
                if (objs != null){
                    List<Object> ret = new ArrayList<>(objs.size());
                    for (Object data : objs) {
                        if (data instanceof VoObject) {
                            ret.add(((VoObject)data).createSimpleVo());
                        }
                    }
                    return ResponseUtil.ok(ret);
                }else{
                    return ResponseUtil.ok();
                }
            default:
                return ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg());
        }
    }

    public static String randomUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}

package cn.edu.xmu.order.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.order.model.bo.Order;
import cn.edu.xmu.order.model.vo.*;
import cn.edu.xmu.order.service.OrderService;
import cn.edu.xmu.order.util.Common2;
import cn.edu.xmu.order.util.SnowFlake;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import lombok.extern.java.Log;
import org.apache.catalina.connector.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单控制器
 * @author jwy
 * Modified at 2020/11/5 13:21
 **/
@Api(value = "订单服务", tags = "order")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/order", produces = "application/json;charset=UTF-8")
public class OrderController {

    private  static  final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    private SnowFlake snowFlake=new SnowFlake(0,0);
//    /**
//     * 检查orderitrm里面的skuid是否是同一个shop的
//     */
//    private Boolean checkVo(OrderVo vo)
//    {
//        Long shopId=vo.getOrderItemList().get(0).getSkuId();
//        for()
//    }
    /**
     * 新建一个订单
     *
     * @author jwy
     * @param vo 订单视图
     * @param bindingResult 校验错误
     * @return Object 订单返回视图
     * createdBy jwy 2020/11/07 22:38
     * modifiedBy
     */
    @ApiOperation(value = "买家申请建立订单", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "OrderVo", name = "vo", value = "可修改的订单信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 900, message = "商品库存不足"),
    })
//    @Audit
    @PostMapping("orders")
    public Object insertOrder(@RequestBody OrderVo vo, BindingResult bindingResult) throws JsonProcessingException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, ClassNotFoundException {
        //@RequestParam Long userId,
        logger.info("insert order");
        Long userId=5l;
//        @LoginUser @ApiIgnore
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.info("validate fail");
            return returnObject;
        }

        Order order = vo.createOrder();
        order.setCustomerId(userId);
        order.setGmtCreate(LocalDateTime.now());
        logger.info("aaaaa"+order);
        ReturnObject<String> retObject = orderService.insertOrder(userId, order);
        if(retObject.getCode().equals(ResponseCode.OK))
        httpServletResponse.setStatus(HttpStatus.CREATED.value());
        return Common.decorateReturnObject(retObject);
    }

    /**
     * 买家查询名下订单 (概要)
     *
     * @author jwy
     * @return Object 简单订单返回视图
     * createdBy jwy 2020/11/24 22:38
     * modifiedBy
     */
    @ApiOperation(value = "买家查询名下订单 (概要)", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "orderSn", value = "订单编号", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "state", value = "限制查询订单的状态 (待付款、未评价等)", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "beginTime", value = "开始时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "endTime", value = "结束时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @GetMapping("orders")
    public Object customerGetAllSimpleOrders(@LoginUser @ApiIgnore Long userId,
                                             @RequestParam(required = false) String orderSn,
                                             @RequestParam(required = false) Integer state,
                                             @RequestParam(required = false) String beginTime,
                                             @RequestParam(required = false) String endTime,
                                             @RequestParam(required = false, defaultValue = "1") Integer page,
                                             @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
            Object returnObject;
            logger.debug("userId"+userId);
            ReturnObject<PageInfo<VoObject>> order = orderService.findSimpleOrderSelective(userId,orderSn, state, beginTime, endTime,page,pageSize);
            returnObject = Common2.getPageRetObject(order);
            return returnObject;
    }


    /**
     * 买家查询名下订单完整信息
     *
     * @author jwy
     * @return Object 订单返回视图
     * createdBy jwy 2020/11/24 22:38
     * modifiedBy
     */
    @ApiOperation(value = "买家查询名下订单完整信息", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "订单id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("orders/{id}")
    public Object customerGetAllOrders(@LoginUser @ApiIgnore Long userId, @PathVariable Long id){

        ReturnObject returnObject;
        ReturnObject<VoObject> order = orderService.findOrderById(userId,id);
        //如果是异常情况
        if(order.getCode().getCode()!=0) {
            returnObject = new ReturnObject(order.getCode(),order.getErrmsg());
            return Common.decorateReturnObject(returnObject);
        }
        //非异常情况
        else
            return Common.getRetObject(order);
    }

    /**
     * 买家标记确认收货
     * @param id: 订单id
     * @return Object
     * createdBy jwy 2020/11/24 20:16
     */
    @ApiOperation(value = "买家标记确认收货")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="用户token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, value = "订单id",dataType="Long", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 800, message = "订单状态禁止"),
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PutMapping("orders/{id}/confirm")
    public Object confirmDelievery(@LoginUser @ApiIgnore Long userId,@PathVariable Long id){
        ReturnObject returnObject=orderService.confirmUserOrder(userId,id);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 买家取消，逻辑删除本人名下订单
     * @param id: 订单id
     * @return Object
     * createdBy jwy 2020/11/24 20:16
     */
    @ApiOperation(value = "买家取消，逻辑删除本人名下订单",notes = "- 需要登录 - 用户本人调用本 API，只能取消，逻辑删除本人的订单 - 发货前取消，完成后逻辑删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="用户token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, value = "订单id",dataType="Long", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 800, message = "订单状态禁止"),
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @DeleteMapping("orders/{id}")
    public Object cancelDeleteOrder(@LoginUser @ApiIgnore Long userId,@PathVariable Long id){

        ReturnObject returnObject=orderService.cancelDeleteOrder(userId,id);
        return Common.decorateReturnObject(returnObject);

    }

    /**
     * 买家将团购订单转为普通订单
     * @param id: 订单id
     * @return Object
     * createdBy jwy 2020/11/24 20:16
     */
    @ApiOperation(value = "买家将团购订单转为普通订单",notes = "在团购过程中，买家可以把团购订单转为普通订单。")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="用户token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, value = "订单id",dataType="Long", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 800, message = "订单状态禁止"),
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PostMapping("orders/{id}/groupon-normal")
    public Object changeToNormalOrder(@LoginUser @ApiIgnore Long userId,@PathVariable Long id){
        ReturnObject returnObject=orderService.changeToNormalOrder(userId,id);
        if(returnObject.getCode().equals(ResponseCode.OK))
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 买家修改本人名下订单
     * @param id: 订单id
     * @return Object
     * createdBy jwy 2020/11/24 23:57
     */
    @ApiOperation(value = "买家修改本人名下订单",notes="用户本人调用本 API，只能修改本人的订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="用户token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, value = "订单id",dataType="Long", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PutMapping("orders/{id}")
    public Object customerChangeOrder(@PathVariable @ApiIgnore Long id, @LoginUser @ApiIgnore Long userId ,@RequestBody OrderSimpleVo vo){
        ReturnObject returnObject=orderService.modifyUserOrder(id,vo,userId);
        return Common.decorateReturnObject(returnObject);

    }

    /**
     * 获得订单所有状态
     * @return Object
     * createdBy cyx 2020/11/25 17:22
     */
    @ApiOperation(value = "获得订单的所有状态", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功") })
    @GetMapping(value = "/orders/states")
    public Object getorderState(){
        Order.State[] states=Order.State.class.getEnumConstants();
        Order.SubState[] subStates=Order.SubState.class.getEnumConstants();
        List<OrderStateVo> stateVos=new ArrayList<OrderStateVo>();
        for(int i=0;i<states.length;i++){
            stateVos.add(new OrderStateVo(states[i].getCode(),states[i].getDescription()));
        }
        for(int i=0;i<subStates.length;i++){
            stateVos.add(new OrderStateVo(subStates[i].getCode(),subStates[i].getDescription()));
        }
        return ResponseUtil.ok(new ReturnObject<List>(stateVos).getData());
    };

    /**
     * 店家查询商户所有订单 (概要)
     *
     * @author cyx
     * @return Object 订单返回视图
     * createdBy cyx 2020/11/25 17:35
     * modifiedBy
     */
    @ApiOperation(value = "店家查询商户所有订单 (概要)", produces = "application/json", notes = "- 需要登录 - 已经被客户逻辑删除的订单也一并返回。")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "shopId", value = "商户id (店员只能查询本商铺)", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "Long", name = "customerId", value = "查询的购买者用户id", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "orderSn", value = "订单编号", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "beginTime", value = "开始时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "endTime", value = "结束时间", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @GetMapping("/shops/{shopId}/orders")
    public Object shopsShopIdOrdersGet(@PathVariable(required = true) Long shopId,
                                       @RequestParam(required = false) Long customerId,
                                       @RequestParam(required = false) String orderSn,
                                       @RequestParam(required = false) String beginTime,
                                       @RequestParam(required = false) String endTime,
                                       @RequestParam(required = false, defaultValue = "1") Integer page,
                                       @RequestParam(required = false, defaultValue = "10") Integer pageSize) {

        Object returnObject;
        logger.debug("shopId"+shopId);
        logger.debug("selectSimpleOrders: page = "+ page +"  pageSize ="+pageSize);
        ReturnObject<PageInfo<VoObject>> order = orderService.shopsShopIdOrdersGet(shopId, customerId, orderSn, beginTime, endTime, page, pageSize);
        returnObject = Common2.getPageRetObject(order);
        return returnObject;
    }

    /**
     * 管理员取消本店铺订单
     * @param id: 订单id
     * @param shopId: 商户id
     * @return Object
     * createdBy cyx 2020/11/25 17:44
     */
    @ApiOperation(value = "管理员取消本店铺订单",notes = "- 需要登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="用户token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="shopId", required = true, value = "商户id (店员只能查询本商铺)",dataType="Long", paramType="path"),
            @ApiImplicitParam(name="id", required = true, value = "订单id",dataType="Long", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 800, message = "订单状态禁止"),
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @DeleteMapping("/shops/{shopId}/orders/{id}")
    public Object shopsShopIdOrdersIdDelete(@PathVariable Long shopId, @PathVariable Long id){
        ReturnObject returnObject=orderService.shopsShopIdOrdersIdDelete(shopId,id);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 店家查询店内订单完整信息（普通，团购，预售）
     *
     * @author cyx
     * @return Object 订单返回视图
     * createdBy cyx 2020/11/25 17:51
     * modifiedBy
     */
    @ApiOperation(value = "店家查询店内订单完整信息（普通，团购，预售）", produces = "application/json", notes = "0普通，1团购，2预售")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(paramType="path", dataType="Long", name="shopId", value = "商户id (店员只能查询本商铺)", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "订单id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @Audit
    @GetMapping("/shops/{shopId}/orders/{id}")
    public Object shopsShopIdOrdersIdGet(@PathVariable(required = true) Long shopId,
                                         @PathVariable(required = true) Long id){
        ReturnObject returnObject;
        ReturnObject<VoObject> order = orderService.shopFindOrderById(shopId,id);
        //如果是异常情况
        if(order.getCode().getCode()!=0) {
            returnObject = new ReturnObject(order.getCode(),order.getErrmsg());
            return Common.decorateReturnObject(returnObject);
        }
        //非异常情况
        else
            return Common.getRetObject(order);
    }

    /**
     * 店家修改订单 (留言)
     * @param shopId: 商户id
     * @param id: 订单id
     * @return Object
     * createdBy cyx 2020/11/25 17:55
     */
    @ApiOperation(value = "店家修改订单 (留言)", notes="- 需要登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="用户token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(paramType="path", dataType="Long", name="shopId", value = "商户id (店员只能查询本商铺)", required = true),
            @ApiImplicitParam(name="id", required = true, value = "订单id",dataType="Long", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PutMapping("/shops/{shopId}/orders/{id}")
    public Object shopsShopIdOrdersIdPut(@PathVariable Long shopId, @PathVariable Long id, @RequestBody OrderMessageVo vo){
        ReturnObject returnObject=orderService.shopModifyUserOrder(shopId,id,vo);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 店家对订单标记发货
     * @param shopId: 商户id
     * @param id: 订单id
     * @return Object
     * createdBy cyx 2020/11/25 18:01
     */
    @ApiOperation(value = "店家对订单标记发货", notes = "- 需要登录 - 商家可以用此 API 将一个状态为待发货的订单改为待收货，并记录运单信息；或是改变一个状态为待收货的订单的运单信息。")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="用户token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="shopId", required = true, value = "商户id",dataType="Long", paramType="path"),
            @ApiImplicitParam(name="id", required = true, value = "订单id",dataType="Long", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PutMapping("/shops/{shopId}/orders/{id}/deliver")
    public Object postfreights(@PathVariable Long shopId, @PathVariable Long id, @RequestBody OrderFreightSnVo vo){
        ReturnObject returnObject=orderService.postFreights(shopId,id,vo);
        return Common.decorateReturnObject(returnObject);
    }

}

////package cn.edu.xmu.order.model.vo;
////
////import cn.edu.xmu.order.model.bo.Order;
////import cn.edu.xmu.order.model.bo.OrderItem;
////import io.swagger.annotations.ApiModel;
////import io.swagger.annotations.ApiModelProperty;
////import lombok.Data;
////
////import java.time.LocalDateTime;
////import java.util.ArrayList;
////import java.util.List;
////
////@Data
////@ApiModel(description ="创建售后订单传值对象")
////public class AfterSaleOrderVo {
////    @ApiModelProperty(value = "订单明细")
////    private List<OrderItemVo> orderItemList;
////
////    @ApiModelProperty(value = "收货人")
////    private String consignee;
////
////    @ApiModelProperty(value = "地区id")
////    private Long regionId;
////
////    @ApiModelProperty(value = "详细地址")
////    private String address;
////
////    @ApiModelProperty(value = "电话")
////    private String mobile;
////
////    @ApiModelProperty(value = "留言")
////    private String message;
//
//import cn.edu.xmu.order.model.bo.Order;
//import cn.edu.xmu.order.model.bo.OrderItem;
//import cn.edu.xmu.order.model.vo.OrderItemVo;
//import io.swagger.annotations.ApiModel;
//import io.swagger.annotations.ApiModelProperty;
//import lombok.Data;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Data
//@ApiModel(description ="创建售后订单传值对象")
//public class AfterSaleOrderVo {
//    @ApiModelProperty(value = "订单明细")
//    private List<OrderItemVo> orderItemList;
//
//    @ApiModelProperty(value = "收货人")
//    private String consignee;
//
//    @ApiModelProperty(value = "地区id")
//    private Long regionId;
//
//    @ApiModelProperty(value = "详细地址")
//    private String address;
//
//    @ApiModelProperty(value = "电话")
//    private String mobile;
//
//    @ApiModelProperty(value = "留言")
//    private String message;

//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Data
//@ApiModel(description ="创建售后订单传值对象")
//public class AfterSaleOrderVo {
//    @ApiModelProperty(value = "订单明细")
//    private List<OrderItemVo> orderItemList;
//
//    @ApiModelProperty(value = "收货人")
//    private String consignee;
//
//    @ApiModelProperty(value = "地区id")
//    private Long regionId;
//
//    @ApiModelProperty(value = "详细地址")
//    private String address;
//
//    @ApiModelProperty(value = "电话")
//    private String mobile;
//
//    @ApiModelProperty(value = "留言")
//    private String message;
//
//    /**
//     * 使用传入的VO创建AfterSaleOrder对象
//     *
//     * @author cyx
//     * @return Order
//     * createdBy cyx 2020/12/2 10:51
//     * modifiedBy
//     */
//    public Order createAfterSaleOrder() {
//        Order order = new Order();
//        order.setRegionId(this.regionId);
//        order.setConsignee(this.consignee);
//        order.setAddress(this.address);
//        order.setMobile(this.mobile);
//        order.setMessage(this.message);
//        order.setState(Order.State.PAIED);//新建状态为代发货
//        order.setOriginPrice(0L);//价格为0
//        //创建订单时 订单类型和SubState不能为空？
//        order.setOrderType(Order.OrderType.NORMAL);
//        order.setSubstate(Order.SubState.ADVANCEPAID);
//        if(null!=this.orderItemList){
//            List<OrderItem> orderItemList=new ArrayList<>(this.orderItemList.size());
//            for(OrderItemVo orderItemVo:this.orderItemList)
//            {
//                OrderItem orderItem=new OrderItem(orderItemVo);
//                orderItem.setGmtCreate(LocalDateTime.now());
//                orderItemList.add(orderItem);
//            }
//            order.setOrderItemList(orderItemList);
//
//        }
//        return order;
//    }
//}

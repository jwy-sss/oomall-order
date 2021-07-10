package cn.edu.xmu.freight.controller;

import cn.edu.xmu.freight.model.bo.Freight;
import cn.edu.xmu.freight.model.vo.FreightVo;
import cn.edu.xmu.freight.model.vo.PieceFreightVo;
import cn.edu.xmu.freight.model.vo.WeightFreightVo;
import cn.edu.xmu.freight.service.FreightService;
import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ReturnObject;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import model.GoodsVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Api(value = "运费", tags = "freight")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/freight", produces = "application/json;charset=UTF-8")
public class FreightController {

    private  static  final Logger logger = LoggerFactory.getLogger(FreightController.class);

    @Autowired
    private FreightService freightService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 买家用运费模板计算一批订单商品的运费
     *
     * @author wwq
     * @return Object 运费
     * createdBy wwq 2020/11/25 22:42
     * modifiedBy
     */
    @ApiOperation(value = "买家用运费模板计算一批订单商品的运费。",notes = "- 需要登录")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name="authorization", value="用户token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="rid", required = true, value = "地区id",dataType="Long", paramType="path"),
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功") })
//    @Audit
    @PostMapping("region/{rid}/price")
    public Object caculateFreight(@PathVariable Long rid, @RequestBody List<GoodsVo> goodsvos, BindingResult bindingResult){
        logger.info("calculateFreight: rid = "+ rid );
        //校验前端数据

        ReturnObject<Long> returnObject =  freightService.caculateFreightpub(goodsvos,rid);
        if(returnObject.getCode().equals(ResponseCode.OK)){
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
        }
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 管理员定义店铺的运费模板
     * @return Object 运费模板
     * createdBy wwq 2020/11/25 22:57
     * modifiedBy
     * @author wwq
     */
    @ApiOperation(value = "管理员定义店铺的运费模板。", notes = "- 需要登录")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name="authorization", value="用户token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="shopId", required = true, value = "店铺id",dataType="Long", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")})
    @Audit
    @PostMapping("/shops/{shopId}/freightmodels")
    public Object insertFreight(@PathVariable Long shopId,@RequestBody @Valid FreightVo vo, BindingResult bindingResult) {
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.info("validate fail");
           return returnObject;
        }
        ReturnObject<VoObject> retObject = freightService.insertFreight(shopId, vo);
        if(retObject.getCode().equals(ResponseCode.OK)){
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
       }
        return Common.getRetObject(retObject);
        //return Common.decorateReturnObject(retObject);
    }

    /**
     * 获得店铺中商品的运费模板
     *
     * @return Object 运费模板
     * createdBy wwq 2020/12/2 15:36
     * modifiedBy
     * @author wwq
     */
    @ApiOperation(value = "获得店铺中商品的运费模板", notes = "- 需要登录")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "Long", name = "id", value = "店铺id", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "name", value = "模板名称", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", required = false),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = false)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")})
    @Audit
    @GetMapping("shops/{id}/freightmodels")
    public Object GetAllFreights(@PathVariable Long id,
                                             @RequestParam(required = false) String name,
                                             @RequestParam(required = false, defaultValue = "1") Integer page,
                                             @RequestParam(required = false, defaultValue = "10") Integer pageSize) {

        Object returnObject;
        logger.debug("shopId"+id);
        logger.debug("selectSimpleOrders: page = "+ page +"  pageSize ="+pageSize);
        ReturnObject<PageInfo<VoObject>> freight = freightService.findSimpleFreightSelective(id,name,page,pageSize);
        returnObject = Common.getPageRetObject(freight);
        return returnObject;
    }

    /**
     * 管理员克隆店铺的运费模板
     *
     * @return Object 运费模板
     * createdBy wwq 2020/11/25 23:26
     * modifiedBy
     * @author wwq
     */
    @ApiOperation(value = "管理员克隆店铺的运费模板。", notes = "- 需要登录 - 模板名称为原加随机数")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="用户token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, value = "模板id",dataType="Long", paramType="path"),
            @ApiImplicitParam(name="shopid", required = true, value = "shopid",dataType="Long", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")})
    @Audit
    @PostMapping("shops/{shopId}/freightmodels/{id}/clone")
    public Object cloneFreightModel(@PathVariable Long id,@PathVariable Long shopId){
        logger.info("clone freight");

        ReturnObject<VoObject> retObject = freightService.cloneFreight(id,shopId);
        httpServletResponse.setStatus(HttpStatus.CREATED.value());
        if(retObject.getCode().equals(ResponseCode.OK)){
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
        }
        return Common.getRetObject(retObject);
    }

    /**
     * 获得运费模板概要。
     * @param id:  运费明细id
     * @return Object
     * createdBy Wang Xiaohan 2020/11/26 15:39
     */
    @ApiOperation(value = "获得运费模板概要")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name="id", required = true, dataType="String", paramType="path",value = "id")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @GetMapping("/shops/{shopId}/freightmodels/{id}")
    public Object GetFreightSimple(@PathVariable Long id,@PathVariable Long shopId){
        logger.debug("getFreightSimple: id = "+ id );
        //由AOP解析token获取departId
        ReturnObject<VoObject> retObject=freightService.getFreightModelSimple(id,shopId);
        //如果是异常情况
        if(retObject.getCode().getCode()!=0) {
            ReturnObject returnObject = new ReturnObject(retObject.getCode(),retObject.getErrmsg());
            return Common.decorateReturnObject(returnObject);
        }
        //非异常情况
        else  return Common.getRetObject(retObject);
    }

    /**
     * 管理员修改店铺的运费模板
     * @param shopId: 店铺id
     * @param id:  运费明细id
     * @return Object
     * createdBy Wang Xiaohan 2020/11/26 15:36
     */
    @ApiOperation(value = "管理员修改店铺的运费模板")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name="shopId", required = true, dataType="String", paramType="path",value = "店铺id"),
            @ApiImplicitParam(name="id", required = true, dataType="String", paramType="path",value = "运费明细id")
    })
    @ApiResponses({
            @ApiResponse(code = 802, message = "运费模板名重复"),
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PutMapping("/shops/{shopId}/freightmodels/{id}")
    public Object changeFreightModel(@PathVariable Long id,@PathVariable Long shopId, @RequestBody FreightVo vo,BindingResult bindingResult){
        logger.debug("changePieceFreight: id = "+ id +" shopId"+shopId+" vo" + vo);
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }
        ReturnObject retObj=  freightService.changeFreight(id, shopId, vo);
        return Common.decorateReturnObject(retObj);
    }

    /**
     * 删除运费模板，需同步删除与商品的。
     * @param shopId: 店铺id
     * @param id:  运费明细id
     * @return Object
     * createdBy Wang Xiaohan 2020/11/26 15:08
     */
    @ApiOperation(value = "删除运费模板，需同步删除与商品的")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name="shopId", required = true, dataType="String", paramType="path",value = "店铺id"),
            @ApiImplicitParam(name="id", required = true, dataType="String", paramType="path",value = "运费明细id")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @DeleteMapping("/shops/{shopId}/freightmodels/{id}")
    public Object delFreight(@PathVariable Long id,@PathVariable Long shopId){
        logger.debug("delFreight: id = "+ id +" shop" + shopId);
        ReturnObject<Object> returnObject =  freightService.deleteFreight(id,shopId);
        return Common.decorateReturnObject(returnObject);
    }
    /**
     * 店家或管理员为商铺定义默认运费模板。
     * @param shopId: 店铺id
     * @param id:  运费明细id
     * @return Object
     * createdBy Wang Xiaohan 2020/11/26 15:34
     */
    @ApiOperation(value = "店家或管理员为商铺定义默认运费模板")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name="shopId", required = true, dataType="String", paramType="path",value = "店铺id"),
            @ApiImplicitParam(name="id", required = true, dataType="String", paramType="path",value = "id")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PostMapping("/shops/{shopId}/freightmodels/{id}/default")
    public Object createFreightDefault(@PathVariable Long id,@PathVariable Long shopId){
        logger.debug("createFreightDefault: id = "+ id +" shop" + shopId);
        ReturnObject retObj=  freightService.defineDefault(id, shopId);
        logger.debug("code"+retObj.getCode().toString());
        if (retObj.getCode().equals(ResponseCode.OK)) {
            httpServletResponse.setStatus(HttpStatus.CREATED.value());
        }
        return Common.decorateReturnObject(retObj);
    }

    /**
     * 管理员定义重量模板明细
     *
     * @return Object 运费模板
     * createdBy wwq 2020/11/25 23:39
     * modifiedBy
     * @author wwq
     */

    @ApiOperation(value = "管理员定义重量模板明细。",notes = "- 需要登录 - 管理员调用本 API 定义默认运费模板。")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name="shopId", value="店铺id", required = true, dataType="Long", paramType="path"),
            @ApiImplicitParam(name="id", required = true, value = "id",dataType="Long", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 803, message = "运费模板中该地区已经定义"),
            @ApiResponse(code = 0, message = "成功") })
    @Audit
    @PostMapping("shops/{shopId}/freightmodels/{id}/weightItems")
    public Object insertWeightFreight(@PathVariable Long id, @PathVariable Long shopId,@RequestBody @Valid WeightFreightVo vo, BindingResult bindingResult){
        logger.debug("insertWeightFreight: id = "+ id + "shopId" + shopId +" vo" + vo);
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.info("validate fail");
            return returnObject;
        }
        ReturnObject<VoObject> retObject =  freightService.insertWeightFreight(id, shopId, vo);
        httpServletResponse.setStatus(HttpStatus.CREATED.value());
        return Common.decorateReturnObject(retObject);
    }

    /**
     * 店家或管理员查询重量运费模板的明细
     *
     * @return Object 运费模板
     * createdBy wwq 2020/11/25 23:49
     * modifiedBy
     * @author wwq
     */
    @ApiOperation(value = "店家或管理员查询重量运费模板的明细。", notes = "- 需要登录")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name="shopId", value="店铺id", required = true, dataType="Long", paramType="path"),
            @ApiImplicitParam(name="id", required = true, value = "id",dataType="Long", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")})
    @Audit
    @GetMapping(value = "shops/{shopId}/freightmodels/{id}/weightItems")
    public Object getWeightFreightDetail(@PathVariable Long id,@PathVariable Long shopId){
        logger.debug("getWeightFreight: id = "+ id + "shopId" + shopId );
        ReturnObject<List> retObject =  freightService.getWeightFreight(id,shopId);
        return Common.getListRetObject(retObject);
    }

    /**
     * 店家或管理员修改重量运费模板明细
     *
     * @return Object 运费模板
     * createdBy wwq 2020/11/25 23:58
     * modifiedBy
     * @author wwq
     */
    @ApiOperation(value = "店家或管理员修改重量运费模板明细。", notes = "- 需要登录 - 店员可以修改本商铺的运费模板。")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name = "shopId", value = "店铺id", required = true, dataType = "Long", paramType = "path"),
            @ApiImplicitParam(name = "id", required = true, value = "运费明细id", dataType = "Long", paramType = "path")
    })
    @ApiResponses({
            @ApiResponse(code = 803, message = "运费模板中该地区已经定义"),
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PutMapping("shops/{shopId}/weightItems/{id}")
    public Object changeWeightFreight(@PathVariable Long id, @PathVariable Long shopId, @RequestBody WeightFreightVo vo,BindingResult bindingResult){
        logger.debug("changeWeightFreight: id = "+ id +" shopId"+shopId+" vo" + vo);
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }
        ReturnObject retObj=  freightService.changeWeightFreight(id, shopId, vo);
        return Common.decorateReturnObject(retObj);
    }

    /**
     * 店家或管理员删掉重量运费模板明细
     *
     * @return Object 运费模板
     * createdBy wwq 2020/11/26 00:18
     * modifiedBy
     * @author wwq
     */
    @ApiOperation(value = "店家或管理员删掉重量运费模板明细。", notes = "- 需要登录 - 店员可以删掉到他本商店的运费模板。")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name="shopId", value="店铺id", required = true, dataType="Long", paramType="path"),
            @ApiImplicitParam(name="id", required = true, value = "运费明细id",dataType="Long", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")})
    @Audit
    @DeleteMapping("/shops/{shopId}/weightItems/{id}")
    public Object delWeightFreight(@PathVariable Long id,@PathVariable Long shopId){
        logger.debug("delWeightFreight: id = "+ id +" shop" + shopId);
        ReturnObject<Object> returnObject =  freightService.deleteWeightFreight(id,shopId);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 管理员定义件数模板明细。
     * @param shopId: 店铺id
     * @param id:  运费明细id
     * @return Object
     * createdBy Wang Xiaohan 2020/11/26 15:00
     */
    @ApiOperation(value = "管理员定义件数模板明细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name="shopId", required = true, dataType="String", paramType="path",value = "店铺id"),
            @ApiImplicitParam(name="id", required = true, dataType="String", paramType="path",value = "id")
    })
    @ApiResponses({
            @ApiResponse(code = 803, message = "运费模板中该地区已经定义"),
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PostMapping("/shops/{shopId}/freightmodels/{id}/pieceItems")
    public Object insertPieceFreight(@PathVariable Long id, @PathVariable Long shopId,@RequestBody @Valid PieceFreightVo vo, BindingResult bindingResult){
        logger.debug("insertPieceFreight: id = "+ id + "shopId" + shopId +" vo" + vo);
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.info("validate fail");
            return returnObject;
        }
        ReturnObject<VoObject> retObject =  freightService.insertPieceFreight(id, shopId, vo);
        httpServletResponse.setStatus(HttpStatus.CREATED.value());
        return Common.decorateReturnObject(retObject);
    }

    /**
     * 店家或管理员查询件数运费模板的明细。
     * @param shopId: 店铺id
     * @param id:  运费明细id
     * @return Object
     * createdBy Wang Xiaohan 2020/11/26 15:19
     */
    @ApiOperation(value = "店家或管理员查询件数运费模板的明细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name="shopId", required = true, dataType="String", paramType="path",value = "店铺id"),
            @ApiImplicitParam(name="id", required = true, dataType="String", paramType="path",value = "id")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @GetMapping("/shops/{shopId}/freightmodels/{id}/pieceItems")
    public Object GetPieceFreight(@PathVariable Long id,@PathVariable Long shopId){
        logger.debug("getPieceFreight: id = "+ id + "shopId" + shopId );
        ReturnObject<List> retObject =  freightService.getPieceFreight(id,shopId);
        return Common.getListRetObject(retObject);
    }


    /**
     * 店家或管理员修改件数运费模板明细。
     * @param shopId: 店铺id
     * @param id:  运费明细id
     * @return Object
     * createdBy Wang Xiaohan 2020/11/26 11:08
     */
    @ApiOperation(value = "店家或管理员修改件数运费模板明细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name="shopId", required = true, dataType="Long", paramType="path",value = "店铺id"),
            @ApiImplicitParam(name="id", required = true, dataType="Long", paramType="path",value = "运费明细id")
    })
    @ApiResponses({
            @ApiResponse(code = 803, message = "运费模板中该地区已经定义"),
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @PutMapping("/shops/{shopId}/pieceItems/{id}")
    public Object changePieceFreight(@PathVariable Long id,@PathVariable Long shopId, @RequestBody PieceFreightVo vo,BindingResult bindingResult){
        logger.debug("changePieceFreight: id = "+ id +" shopId"+shopId+" vo" + vo);
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }
        ReturnObject retObj=  freightService.changePieceFreight(id, shopId, vo);
        return Common.decorateReturnObject(retObj);
    }

    /**
     * 店家或管理员删掉件数运费模板明细。
     * @param shopId: 店铺id
     * @param id:  运费明细id
     * @return Object
     * createdBy Wang Xiaohan 2020/11/26 15:08
     */
    @ApiOperation(value = "店家或管理员删掉件数运费模板明细")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "用户token", required = true),
            @ApiImplicitParam(name="shopId", required = true, dataType="String", paramType="path",value = "店铺id"),
            @ApiImplicitParam(name="id", required = true, dataType="String", paramType="path",value = "运费明细id")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功")
    })
    @Audit
    @DeleteMapping("/shops/{shopId}/pieceItems/{id}")
    public Object delPieceFreight(@PathVariable Long id, @PathVariable Long shopId){
        logger.debug("delPieceFreight: id = "+ id +" shop" + shopId);
        ReturnObject<Object> returnObject =  freightService.deletePieceFreight(id,shopId);
        return Common.decorateReturnObject(returnObject);
    }

}

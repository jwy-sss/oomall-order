package cn.edu.xmu.provider.server;

import cn.edu.xmu.provider.model.po.TimeSegmentPo;
import cn.edu.xmu.provider.model.vo.CustomerVo;

import java.util.List;

public interface OtherForGoodsService {
    /**
     * @description:根据用户id获得UserSimpleRetVo 属性为id和name
     * @author: Feiyan Liu
     * @date: Created at 2020/12/6 19:18
     */
    CustomerVo getUserById(Long id);


    /**
     * @description:获得所有时段
     * @param null
     * @return:
     * @author: Feiyan Liu
     * @date: Created at 2020/12/6 19:22
     */
    List<TimeSegmentPo> getAllTimeSegment();

    /**
     * @description:根据id获取时段信息
     * @author: Feiyan Liu
     * @date: Created at 2020/12/6 19:24
     */
    Boolean checkTimeSegmentById();
}

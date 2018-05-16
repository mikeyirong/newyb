package com.yiban.erp.service.goods;

import com.yiban.erp.constant.GoodsStatus;
import com.yiban.erp.constant.OrderNumberType;
import com.yiban.erp.dao.GoodsDetailMapper;
import com.yiban.erp.dao.GoodsInfoMapper;
import com.yiban.erp.dao.GoodsMapper;
import com.yiban.erp.dao.OptionsMapper;
import com.yiban.erp.dto.GoodsQuery;
import com.yiban.erp.entities.*;
import com.yiban.erp.exception.BizException;
import com.yiban.erp.exception.BizRuntimeException;
import com.yiban.erp.exception.ErrorCode;
import com.yiban.erp.util.UtilTool;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class GoodsService {

    private static final Logger logger = LoggerFactory.getLogger(GoodsService.class);

    @Autowired
    private OptionsMapper optionsMapper;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsInfoMapper goodsInfoMapper;
    @Autowired
    private GoodsDetailMapper goodsDetailMapper;


    public void setGoodsOptionName(List<Goods> goodsList) {
        if (goodsList == null || goodsList.isEmpty()) {
            return;
        }
        //设置对应的Option的Name值
        Set<Long> optionIdSet = new HashSet<>();
        goodsList.stream().forEach(item -> {
            optionIdSet.addAll(item.getOptionIdList());
        });
        Long[] ids = new Long[optionIdSet.size()];
        optionIdSet.toArray(ids);
        List<Options> options = optionsMapper.getByIds(ids);
        goodsList.stream().forEach(item -> item.setOptionName(options));
    }

    public void setGoodsOptionName(Goods goods) {
        if (goods == null) {
            return;
        }
        Set<Long> set = goods.getOptionIdList();
        if (set.size() < 0) {
            return;
        }
        Long[] ids = new Long[set.size()];
        set.toArray(ids);
        List<Options> options = optionsMapper.getByIds(ids);
        goods.setOptionName(options);
    }

    public List<Goods> getGoodsById(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<Goods> goodsList = goodsMapper.selectByIdList(ids);
        setGoodsOptionName(goodsList);
        return goodsList;
    }

    public void setGoodsInfoOptionName(List<GoodsInfo> goodsInfoList) {
        if (goodsInfoList == null || goodsInfoList.isEmpty()) {
            return;
        }
        //设置对应的Option的Name值
        Set<Long> optionIdSet = new HashSet<>();
        goodsInfoList.stream().forEach(item -> {
            optionIdSet.addAll(item.getOptionIds());
        });
        Long[] ids = new Long[optionIdSet.size()];
        optionIdSet.toArray(ids);
        List<Options> options = optionsMapper.getByIds(ids);
        goodsInfoList.stream().forEach(item -> item.setOptions(options));
    }

    public void setGoodsInfoOptionName(GoodsInfo goodsInfo) {
        if (goodsInfo == null || goodsInfo.getOptionIds() == null || goodsInfo.getOptionIds().isEmpty()) {
            return;
        }
        Set<Long> optionIdSet = goodsInfo.getOptionIds();
        Long[] ids = new Long[optionIdSet.size()];
        optionIdSet.toArray(ids);
        List<Options> options = optionsMapper.getByIds(ids);
        goodsInfo.setOptions(options);
    }


    public Long searchListCount(GoodsQuery query) {
        return goodsInfoMapper.searchListCount(query);
    }

    public List<GoodsInfo> searchList(GoodsQuery query) {
        List<GoodsInfo> infos = goodsInfoMapper.searchList(query);
        setGoodsInfoOptionName(infos);
        return infos;
    }

    public Long getChooseListDetailCount(GoodsQuery query) {
        return goodsInfoMapper.getChooseListDetailCount(query);
    }

    public List<Goods> getChooseListDetail(GoodsQuery query){
        List<Goods> goods = goodsInfoMapper.getChooseListDetail(query);
        //设置option的值
        setGoodsOptionName(goods);
        //TODO 设置自定义字段的值

        return goods;
    }


    public GoodsInfo getGoodsInfoById(Long id) throws BizException {
        GoodsInfo goodsInfo = goodsInfoMapper.selectByPrimaryKey(id);
        if (goodsInfo == null) {
            throw new BizException(ErrorCode.GOODS_GET_RESULT_NULL);
        }
        List<GoodsDetail> details = goodsDetailMapper.getByGoodsInfoId(goodsInfo.getId());
        goodsInfo.setGoodsDetails(details);
        setGoodsInfoOptionName(goodsInfo);
        return goodsInfo;
    }


    public void saveGoodsInfo(GoodsInfo goodsInfo, User user) throws BizException {
        if (StringUtils.isEmpty(goodsInfo.getName()) || goodsInfo.getUnit() == null || goodsInfo.getUseSpec() == null) {
            throw new BizException(ErrorCode.PARAMETER_MISSING);
        }
        //如果存在有ID，则认为是修改，如果不存在ID，认为是添加
        if (goodsInfo.getId() != null) {
            updateGoodsInfo(goodsInfo, user);
        }else {
            createdGoodsInfo(goodsInfo, user);
        }
    }

    @Transactional
    public void createdGoodsInfo(GoodsInfo goodsInfo, User user) throws BizException {
        List<GoodsDetail> details = goodsInfo.getGoodsDetails();
        boolean useSpec = goodsInfo.getUseSpec() == null ? false : goodsInfo.getUseSpec();

        //直接把数据入库
        goodsInfo.setCompanyId(user.getCompanyId());
        goodsInfo.setUseSpec(useSpec);
        if (goodsInfo.getStatus() == null) {
            goodsInfo.setStatus(GoodsStatus.ON_SALE.name());
        }
        goodsInfo.setGoodsNo(UtilTool.makeOrderNumber(user.getCompanyId(), OrderNumberType.GOODS));
        if (goodsInfo.getEnable() == null) {
            goodsInfo.setEnable(false);
        }
        goodsInfo.setCreatedBy(user.getNickname());
        goodsInfo.setCreatedTime(new Date());
        int count = goodsInfoMapper.insert(goodsInfo);
        if (count <= 0 || goodsInfo.getId() == null) {
            logger.warn("insert goods info fail.");
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
        //如果是不分规格的，直接造一个detail的值然后入库，如果分规则，需要更加
        if (!useSpec) {
            GoodsDetail detail = new GoodsDetail();
            detail.setCompanyId(user.getCompanyId());
            detail.setGoodsInfoId(goodsInfo.getId());
            detail.setStatus(GoodsStatus.NORMAL.name());
            detail.setSkuKey(getSkuKey(null, goodsInfo.getId()));
            detail.setBarCode(goodsInfo.getBarCode());
            detail.setBatchPrice(goodsInfo.getBatchPrice());
            detail.setRetailPrice(goodsInfo.getRetailPrice());
            detail.setInPrice(goodsInfo.getInPrice());
            detail.setCreatedBy(user.getNickname());
            detail.setCreatedTime(new Date());
            int detailCount = goodsDetailMapper.insert(detail);
            if (detailCount <= 0) {
                throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
            }
        }else {
            for (GoodsDetail detail : details) {
                detail.setCompanyId(user.getCompanyId());
                detail.setGoodsInfoId(goodsInfo.getId());
                detail.setStatus(GoodsStatus.NORMAL.name());
                if (detail.getBarCode() == null && goodsInfo.getBarCode() != null) {
                    detail.setBarCode(goodsInfo.getBarCode());
                }
                detail.setSkuKey(getSkuKey(detail, goodsInfo.getId()));
                detail.setCreatedBy(user.getNickname());
                detail.setCreatedTime(new Date());
            }
            int detailsCount = goodsDetailMapper.insertBatch(details);
            if (detailsCount <= 0) {
                throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
            }
        }
    }

    private String getSkuKey(GoodsDetail detail, Long goodsInfoId) {
        StringBuilder sb = new StringBuilder();
        sb.append(goodsInfoId);
        if (detail == null) {
            return sb.toString();
        }
        //如果不为空，获取三个规格ID，先对规格的ID进行从小到大排序，然后再组合在一起
        long specOneId = detail.getSpecOneId() != null ? detail.getSpecOneId() : -1L;
        long specTwoId = detail.getSpecTwoId() != null ? detail.getSpecTwoId() : -1L;
        long specThreeId = detail.getSpecThreeId() != null ? detail.getSpecThreeId() : -1L;
        long[] sortArray = {specOneId, specTwoId, specThreeId};
        Arrays.sort(sortArray);
        for (long specId : sortArray) {
            if (specId > 0) {
                sb.append("-");
                sb.append(specId);
            }
        }
        return sb.toString();
    }

    @Transactional
    public void updateGoodsInfo(GoodsInfo goodsInfo, User user) throws BizException {

    }

    @Transactional
    public void remove(Long infoId, User user) throws BizException {
        GoodsInfo goodsInfo = goodsInfoMapper.selectByPrimaryKey(infoId);
        if (goodsInfo == null || !goodsInfo.getCompanyId().equals(user.getCompanyId())) {
            logger.warn("get goods info fail by id:{}", infoId);
            throw new BizException(ErrorCode.GOODS_GET_RESULT_NULL);
        }
        //验证当前商品下的所有详情是否有存在使用的情况，如果存在，不能删除
        boolean isGoodsUse = goodsInfoMapper.isGoodsUsed(infoId);
        if (isGoodsUse) {
            logger.warn("goods info is used, can not delete.");
            throw new BizException(ErrorCode.GOODS_USED_CANNOT_DELETE);
        }
        //如果没有关联的，直接修改到删除状态
        goodsInfo.setEnable(false);
        goodsInfo.setStatus(GoodsStatus.DELETE.name());
        goodsInfo.setUpdatedBy(user.getNickname());
        goodsInfo.setUpdatedTime(new Date());
        int count = goodsInfoMapper.updateByPrimaryKeySelective(goodsInfo);
        if (count <= 0) {
            throw new BizRuntimeException(ErrorCode.FAILED_DELETE_FROM_DB);
        }
        logger.info("goods info is update to DELETE status, then update details status to DELETE");
        goodsDetailMapper.deleteByGoodsInfoId(goodsInfo.getId(), user.getNickname(), new Date());
    }

    @Transactional
    public void copyGoodsInfo(Long id, User user) throws BizException {
        GoodsInfo goodsInfo = goodsInfoMapper.selectByPrimaryKey(id);
        if (goodsInfo == null || !goodsInfo.getCompanyId().equals(user.getCompanyId())) {
            logger.warn("get goods info fail by id:{}", id);
            throw new BizException(ErrorCode.GOODS_GET_RESULT_NULL);
        }
        List<GoodsDetail> details = goodsDetailMapper.getByGoodsInfoId(goodsInfo.getId());
        if (details == null || details.isEmpty()) {
            logger.warn("get goods info detail by goods info id result is empty");
            throw new BizException(ErrorCode.GOODS_GET_RESULT_NULL);
        }
        //先直接对goodsInfo 做入库操作
        goodsInfo.setId(null);
        goodsInfo.setName(goodsInfo.getName() + "copy"); //名称在原来的商品名称上加一个copy
        goodsInfo.setGoodsNo(UtilTool.makeOrderNumber(user.getCompanyId(), OrderNumberType.GOODS));
        goodsInfo.setCreatedBy(user.getNickname());
        goodsInfo.setCreatedTime(new Date());
        goodsInfo.setUpdatedBy(user.getNickname());
        goodsInfo.setUpdatedTime(new Date());
        int count = goodsInfoMapper.insert(goodsInfo);
        if (count <=0 || goodsInfo.getId() == null) {
            logger.warn("copy goods info insert db fail.");
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
        logger.info("copy goods info insert info success, id:{}", goodsInfo.getId());
        //需要对每一个detail的goods_info_id 和 sku_key 重置
        for (GoodsDetail detail : details) {
            detail.setId(null);
            detail.setGoodsInfoId(goodsInfo.getId());
            detail.setSkuKey(getSkuKey(detail, goodsInfo.getId()));
            detail.setCreatedBy(user.getNickname());
            detail.setCreatedTime(new Date());
            detail.setUpdatedBy(user.getNickname());
            detail.setUpdatedTime(new Date());
        }
        int detailCount = goodsDetailMapper.insertBatch(details);
        if (detailCount <= 0) {
            throw new BizRuntimeException(ErrorCode.FAILED_INSERT_FROM_DB);
        }
    }


}

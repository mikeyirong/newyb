package com.yiban.erp.controller.good;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yiban.erp.dao.GoodsMapper;
import com.yiban.erp.dto.GoodsQuery;
import com.yiban.erp.entities.Goods;
import com.yiban.erp.entities.GoodsDetail;
import com.yiban.erp.entities.GoodsInfo;
import com.yiban.erp.entities.User;
import com.yiban.erp.service.goods.GoodsService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;


@RestController
@RequestMapping("/goods")
public class GoodsController {
    private static final Logger logger = LoggerFactory.getLogger(GoodsController.class);

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private GoodsService goodsService;

    /**
     * 根据详情信息，全部展开商品详情信息
     * @param user
     * @param catId
     * @param search
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> list(@AuthenticationPrincipal User user,
                                       @RequestParam(required = false) Integer catId,
                                       @RequestParam(required = false) String search,
                                       @RequestParam(required = false) Integer page,
                                       @RequestParam(required = false) Integer size) {
        logger.info("Get goods list, search={}, catId={}, page={}, size={}", search, catId, page, size);
        if (StringUtils.isEmpty(search)) {
            search = null;
        }
        GoodsQuery query = new GoodsQuery();
        query.setCompanyId(user.getCompanyId());
        query.setCategoryId(catId);
        query.setSearch(search);
        query.setPage(page);
        query.setPageSize(size);
        Long count = 0L;
        count = goodsService.getChooseListDetailCount(query);
        List<Goods> details = new ArrayList<>();
        if (count > 0) {
            details = goodsService.getChooseListDetail(query);
        }
        JSONObject result = new JSONObject();
        result.put("total", count);
        result.put("data", JSON.toJSON(details));
        return ResponseEntity.ok().body(result.toJSONString());
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> searchList(@RequestBody GoodsQuery query,
                                             @AuthenticationPrincipal User user) {
        query.setCompanyId(user.getCompanyId());
        Long count = goodsService.searchListCount(query);
        List<GoodsInfo> result = new ArrayList<>();
        if (count > 0) {
            result = goodsService.searchList(query);
        }else if (count == null){
            count = 0L;
        }
        JSONObject response = new JSONObject();
        response.put("count", count);
        response.put("data", result);
        return ResponseEntity.ok(response.toJSONString());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> get(@PathVariable Long id) throws Exception {
        GoodsInfo goodsInfo = goodsService.getGoodsInfoById(id);
        return ResponseEntity.ok().body(JSON.toJSONString(goodsInfo));
    }

    @RequestMapping(value = "/remove/{infoId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> remove(@PathVariable Long infoId,
                                         @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request to remove goods info by id:{}", user.getId(), infoId);
        goodsService.remove(infoId, user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> save(@RequestBody GoodsInfo goodsInfo,
                                      @AuthenticationPrincipal User user) throws Exception {
        logger.info("save add or update goods info :{}", JSON.toJSONString(goodsInfo));
        goodsService.saveGoodsInfo(goodsInfo, user);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/copy/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> copy(@PathVariable Long id,
                                       @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} request copy goods info by modal id:{}", user.getId(), id);
        goodsService.copyGoodsInfo(id, user);
        return ResponseEntity.ok().build();
    }

}
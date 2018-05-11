package com.yiban.erp.controller.good;

import com.alibaba.fastjson.JSON;
import com.yiban.erp.dto.GoodsAttForm;
import com.yiban.erp.entities.GoodsAttribute;
import com.yiban.erp.entities.User;
import com.yiban.erp.service.goods.GoodsAttrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/goods/attribute")
public class GoodsAtrrController {

    private static final Logger logger = LoggerFactory.getLogger(GoodsAtrrController.class);

    @Autowired
    private GoodsAttrService goodsAttrService;

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> attrList(@AuthenticationPrincipal User user) throws Exception {
        List<GoodsAttribute> attForm = goodsAttrService.getGoodsAttList(user.getCompanyId());
        return ResponseEntity.ok().body(JSON.toJSONString(attForm));
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> attrSave(@RequestBody GoodsAttForm goodsAttForm,
                                           @AuthenticationPrincipal User user) throws Exception {
        logger.info("user:{} save goods attribute info:{}", user.getId(), goodsAttForm);
        goodsAttrService.save(goodsAttForm, user);
        return ResponseEntity.ok().build();
    }


}

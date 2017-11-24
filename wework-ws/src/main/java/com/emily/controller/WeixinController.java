package com.emily.controller;

import com.alice.emily.utils.LOG;
import com.alice.emily.utils.logging.Logger;
import com.emily.domain.ResponseData;
import com.emily.dto.ArticleDto;
import com.emily.service.WeixinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;

/**
 * Created by Lianhao on 2017/10/30.
 */
@Api(tags = "weixin")
@Validated
@RestController
@RequestMapping("weixin")
public class WeixinController {

    static final Logger log = LOG.getLogger(WeixinController.class);

    @Autowired
    WeixinService weixinService;

    /**
     * 获取文章信息
     *
     * @param url
     *
     */
    @ApiOperation("获取文章信息")
    @GetMapping("article")
    @ApiIgnore
    public ResponseData<ArticleDto> getArticle(@RequestParam("url") @NotNull String url) {
        try {
            return new ResponseData(weixinService.getArticleByUrl(url));
        } catch (Exception e) {
            log.error("getArticle error: " + e.getMessage());
            return new ResponseData();
        }
    }

}

package com.emily.controller;

import com.alice.emily.utils.LOG;
import com.alice.emily.utils.logging.Logger;
import com.emily.mapper.RequestMapper;
import com.emily.model.ThumbRequest;
import com.emily.service.ImageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.OutputStream;

/**
 * Created by Lianhao on 2017/9/22.
 */
@Api(tags = "图片服务")
@Validated
@Controller
@ApiIgnore
@RequestMapping("/image")
public class ImageController {
    private final Logger log = LOG.getLogger(ImageController.class);
    private final ResourceLoader resourceLoader;
    private final ImageService imageService;
    private final RequestMapper mapper;

    @Autowired
    public ImageController(ResourceLoader resourceLoader, ImageService imageService,
                           RequestMapper mapper) {
        this.resourceLoader = resourceLoader;
        this.imageService = imageService;
        this.mapper = mapper;
    }

    @ApiOperation("获取缩略图")
    @GetMapping("/thumb")
    @ResponseBody
    public void getThumb(@Valid ThumbRequest request, HttpServletResponse response) {
        try {
            byte[] data = imageService.getThumbByPath(mapper.toParam(request));
            response.setContentType("image/png");
            OutputStream stream = response.getOutputStream();
            stream.write(data);
            stream.flush();
            stream.close();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


}

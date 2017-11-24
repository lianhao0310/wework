package com.emily.controller;

import io.swagger.annotations.Api;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Lianhao on 2017/10/30.
 */
@Api(tags = "open")
@Validated
@RestController
@RequestMapping("open")
public class OpenController {

}

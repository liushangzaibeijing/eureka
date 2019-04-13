package com.xiu.ribbon.controller;

import com.xiu.ribbon.service.RibbonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * author   xieqx
 * createTime  2019/3/23
 * desc 需要提供的微服务应用
 */

@RestController
public class SayHiController {

    @Autowired
    private RibbonService ribbonService;

    @RequestMapping("/hi")
    public String sayHi(@RequestParam String name){

       return ribbonService.hi("ribbon");
    }
}

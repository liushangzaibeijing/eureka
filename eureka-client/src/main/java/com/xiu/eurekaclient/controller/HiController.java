package com.xiu.eurekaclient.controller;

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
public class HiController {

    @Value("${server.port}")
    private String port;

    @RequestMapping("/hi")
    public String sayHi(@RequestParam String name){

        return "hi "+name+" I am from "+port;

    }
}

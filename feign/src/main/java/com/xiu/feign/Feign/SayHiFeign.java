package com.xiu.feign.Feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "service-another",fallback = MyFallback.class)
public interface SayHiFeign {

    @RequestMapping("/hi")
    String sayHi(@RequestParam(name="name",required = true) String name);
}

package com.xiu.feign.Feign;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "service-ribbon", url = "https://api.github.com",decode404 = false,
        /*fallback = MyFallback.class*/ fallbackFactory = MyFallBackFactory.class)
public interface GitHubApiFeign {

    @RequestMapping(value = "/search/repositories", method = RequestMethod.GET)
    String searchRepositories(@RequestParam("q") String queryStr);


    @RequestMapping(value = "/search/repositoriea", method = RequestMethod.GET)
    String searchRepositoriea(@RequestParam("q") String queryStr);
}
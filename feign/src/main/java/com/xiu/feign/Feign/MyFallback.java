package com.xiu.feign.Feign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MyFallback implements GitHubApiFeign {

    Logger logger = LoggerFactory.getLogger(MyFallback.class);

    @Override
    public String searchRepositories(String queryStr) {
        logger.info("失败回调操作");
        return null;
    }

    @Override
    public String searchRepositoriea(String queryStr) {
        logger.info("失败回调操作");
        return null;
    }
}

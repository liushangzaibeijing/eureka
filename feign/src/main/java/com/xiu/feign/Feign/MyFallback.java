package com.xiu.feign.Feign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *失败回调类 实现需要失败回调的接口 从而保证其有与实现类一样的方法
 */
@Component
public class MyFallback implements SayHiFeign {
    Logger logger = LoggerFactory.getLogger(MyFallback.class);
    public String searchRepositories(String queryStr) {
        logger.info("失败回调操作");
        return null;
    }
    public String searchRepositoriea(String queryStr) {
        logger.info("失败回调操作");
        return null;
    }

    @Override
    public String sayHi(String name) {
        logger.info("失败回调操作");
        return null;
    }
}

package com.xiu.hystrix.service;

import com.netflix.hystrix.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

public class HystrixService extends HystrixCommand<List> {
        private final static Logger logger = LoggerFactory.getLogger(HystrixService.class);
        private UserService userService;

        public HystrixService(UserService userService) {
            /**
             * 1、HystrixCommandGroupKey 作为分组的名称 如果HystrixThreadPoolKey没有指定
             *   则使用HystrixCommandGroupKey对应的key 作为线程池的名称
             * 2、HystrixCommandKey HystrixCommand 调用的服务名称 后续的监控参数的调用需要使用
             * 3、HystrixThreadPoolKey 该服务对应的所有线程池的命名
             */
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("userService"))
                    .andCommandKey(HystrixCommandKey.Factory.asKey("findAllUsers"))
                    .andCommandPropertiesDefaults(HystrixCommandProperties.Setter()
                            //至少有10个请求，熔断器才进行错误率的计算 设置开启熔断器的错误请求错误10次
                            .withCircuitBreakerRequestVolumeThreshold(10)
                            //熔断器中断请求5秒后会进入半打开状态,放部分流量过去重试
                            .withCircuitBreakerSleepWindowInMilliseconds(5000)
                            //错误率达到50开启熔断保护
                            .withCircuitBreakerErrorThresholdPercentage(50)
                            .withExecutionTimeoutEnabled(true))

                    .andThreadPoolPropertiesDefaults(HystrixThreadPoolProperties
                            .Setter().withCoreSize(10)));
            this.userService = userService;
        }


    @Override
    protected List run() throws Exception {
        return userService.findAllUser();
    }

}

package com.xiu.feign.Feign;

import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class MyFallBackFactory implements FallbackFactory<GitHubApiFeign> {

    private final MyFallback myFallback;

    public MyFallBackFactory(MyFallback myFallback) {
        this.myFallback = myFallback;
    }

    @Override
    public GitHubApiFeign create(Throwable cause) {
        //打印下异常
        System.out.println("失败回调的工厂");
        cause.printStackTrace();
        return myFallback;
    }
}




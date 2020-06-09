package com.xiu.feign;

import feign.Feign;
import feign.Param;
import feign.RequestLine;
import feign.codec.Decoder;
import feign.codec.StringDecoder;
import feign.gson.GsonDecoder;
import org.junit.Test;

import java.util.List;

public class FeignTest {
    interface GitHub {
        @RequestLine("GET /repos/{owner}/{repo}/contributors")
        List<Contributor> contributors(@Param("owner") String owner, @Param("repo") String repo);
    }

    static class Contributor {
        String login;
        int contributions;
    }

    @Test
    public void testFeign(){
        GitHub github = Feign.builder()
                .decoder(new GsonDecoder())
                .target(GitHub.class, "https://api.github.com");

        // 获取贡献者列表，并打印其登录名以及贡献次数
        List<Contributor> contributors = github.contributors("netflix", "feign");
        for (Contributor contributor : contributors) {
            System.out.println(contributor.login + " (" + contributor.contributions + ")");
        }

    }
}

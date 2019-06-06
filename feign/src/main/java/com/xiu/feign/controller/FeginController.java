package com.xiu.feign.controller;

import com.xiu.feign.Feign.GitHubApiFeign;
import com.xiu.feign.Feign.SayHiFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FeginController {

    @Autowired
    private SayHiFeign sayHiFeign;

    @Autowired
    private GitHubApiFeign gitHubApiFeign;

    @RequestMapping("/hi")
    public String sayHi(@RequestParam String name){
       return  sayHiFeign.sayHi(name);
    }


    @RequestMapping("/showReposites")
    public String showReposites(@RequestParam String repositesName){
        return  gitHubApiFeign.searchRepositories(repositesName);
    }

    @RequestMapping("/showRepositea")
    public String showRepositea(@RequestParam String repositesName){
        return  gitHubApiFeign.searchRepositoriea(repositesName);
    }
}

package com.xiu.hystrix.service;

import java.util.ArrayList;
import java.util.List;

public class UserService {

    /**
     * 查询所有的用户列表
     * @return
     */
    public List<String> findAllUser(){

        try {
            Thread.sleep(1000*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<String> users = new ArrayList<>();
        users.add("AAA");
        users.add("BBB");
        users.add("CCC");
        users.add("DDD");

        return users;

    }
}

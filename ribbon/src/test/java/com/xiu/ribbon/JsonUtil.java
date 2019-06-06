package com.xiu.ribbon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.loadbalancer.BaseLoadBalancer;

public class JsonUtil {

    static ObjectMapper mapper = new ObjectMapper();

    public static byte[] obj2byte(Object obj){
        try {
            return mapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            System.out.println("json 转换错误"+e.getMessage());
        }
        return null;
    }

    public static String obj2str(Object obj){
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            System.out.println("json 转换错误"+e.getMessage());
        }
        return null;
    }

}

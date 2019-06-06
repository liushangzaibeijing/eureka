package com.xiu.ribbon;

import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClientConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerClient;

@SpringBootApplication
@EnableEurekaClient
public class RibbonApplication {

	public static void main(String[] args) {
		//DynamicServerListLoadBalancer
//		RibbonClientConfiguration
		SpringApplication.run(RibbonApplication.class, args);
	}

}

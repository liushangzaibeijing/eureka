/*
 * Copyright 2013-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xiu.ribbon;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerRequest;
import org.springframework.cloud.netflix.ribbon.*;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;

/**
 * @author Spencer Gibb
 * @author Dave Syer
 * @author Ryan Baxter
 */
public class RibbonLoadBalancerClient implements LoadBalancerClient {


    private SpringClientFactory clientFactory;

    public RibbonLoadBalancerClient(SpringClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    /**
     * 重构url地址 将服务名相关的服务实例 转换为真实的ip地址
     * @param instance 为此次请求提供服务的请求实例信息 里面包含请求的真实地址 比如真实的host port schema medata(元数据)
     * @param original 此次请求的初始请求的uri地址信息 行如： http://service-name/address?paramName=paramValue ...
     * 并非真实的地址 而该方法就是从ServiceInstance 实例对象中获取获取真实的地址信息，将原始uri中的服务实例名称转换为真实的地址
     * @return 此次请求的真实的url地址
     */
    @Override
    public URI reconstructURI(ServiceInstance instance, URI original) {
        Assert.notNull(instance, "instance can not be null");
        //获取服务名
        String serviceId = instance.getServiceId();
        //根据服务名获取对应的上下文环境
        RibbonLoadBalancerContext context = this.clientFactory
                .getLoadBalancerContext(serviceId);
        //根据服务实例的host 和port 构造一个Server对象
        //用于reconstructURIWithServer()方法获取对应的host 和port信息 构造真实的url地址
        Server server = new Server(instance.getHost(), instance.getPort());
        //获取服务的配置信息 为了其是否为https 请求做判断处理 如是https请求则添加https请求相关的属性
        IClientConfig clientConfig = clientFactory.getClientConfig(serviceId);
        ServerIntrospector serverIntrospector = serverIntrospector(serviceId);
        URI uri = RibbonUtils.updateToHttpsIfNeeded(original, clientConfig,
                serverIntrospector, server);

        //使用真实的地址重构url并返回 供http请求执行
        return context.reconstructURIWithServer(server, uri);
    }

    @Override
    public ServiceInstance choose(String serviceId) {
        Server server = getServer(serviceId);
        if (server == null) {
            return null;
        }
        return new RibbonServer(serviceId, server, isSecure(server, serviceId),
                serverIntrospector(serviceId).getMetadata(server));
    }

    /**
     * 进行用户请求的负载请求转发的核心操作，实现的主要逻辑：
     *       针对用户的一次请求 通过ILoadBalancer(负载均衡的主要实现组件) 获取到一个服务实例信息，并使用http请求去执行
     * 执行服务的请求信息
     * @param serviceId 服务名称（服务的唯一标识）
     * @param request 一次请求服务调用
     * @param <T>
     * @return
     * @throws IOException
     */
    @Override
    public <T> T execute(String serviceId, LoadBalancerRequest<T> request) throws IOException {
        //通过服务名获取到一个具体实现负载均衡的负载均衡器组件 debug获取到其实现类：DynamicServerListLoadBalancer
        //具体的负载均衡功能实现是由其ILoadBalancer和其子类实现的，该类是真正最终实现负载功能的核心类，会在后面的篇幅中着重分析
        //此处按下不表
        ILoadBalancer loadBalancer = getLoadBalancer(serviceId);
        //通过不同的负载均衡策略获取到一个服务实例（默认是RoundRobinRule 线性轮询策略）
        // 这里的负载策略选取 是由IRule及其子类实现 这里也在后面讲解
        Server server = getServer(loadBalancer);
        if (server == null) {
            throw new IllegalStateException("No instances available for " + serviceId);
        }
        //将获取到的服务Server对象包装成一个ServiceInstance 实例信息对象
        RibbonServer ribbonServer = new RibbonServer(serviceId, server, isSecure(server,
                serviceId), serverIntrospector(serviceId).getMetadata(server));
        //调用其重载方法来执行余下的操作
        // 该重载方法主要实现的功能是： 使用httpclient 客户端调用通过
        // 该类中的reconstructURI()重构后的url 执行http请求
        return execute(serviceId, ribbonServer, request);
    }

    @Override
    public <T> T execute(String serviceId, ServiceInstance serviceInstance, LoadBalancerRequest<T> request) throws IOException {
        Server server = null;
        if(serviceInstance instanceof RibbonServer) {
            server = ((RibbonServer)serviceInstance).getServer();
        }
        if (server == null) {
            throw new IllegalStateException("No instances available for " + serviceId);
        }

        RibbonLoadBalancerContext context = this.clientFactory
                .getLoadBalancerContext(serviceId);
        RibbonStatsRecorder statsRecorder = new RibbonStatsRecorder(context, server);

        try {
            T returnVal = request.apply(serviceInstance);
            statsRecorder.recordStats(returnVal);
            return returnVal;
        }
        // catch IOException and rethrow so RestTemplate behaves correctly
        catch (IOException ex) {
            statsRecorder.recordStats(ex);
            throw ex;
        }
        catch (Exception ex) {
            statsRecorder.recordStats(ex);
            ReflectionUtils.rethrowRuntimeException(ex);
        }
        return null;
    }

    private ServerIntrospector serverIntrospector(String serviceId) {
        ServerIntrospector serverIntrospector = this.clientFactory.getInstance(serviceId,
                ServerIntrospector.class);
        if (serverIntrospector == null) {
            serverIntrospector = new DefaultServerIntrospector();
        }
        return serverIntrospector;
    }

    private boolean isSecure(Server server, String serviceId) {
        IClientConfig config = this.clientFactory.getClientConfig(serviceId);
        ServerIntrospector serverIntrospector = serverIntrospector(serviceId);
        return RibbonUtils.isSecure(config, serverIntrospector, server);
    }

    protected Server getServer(String serviceId) {
        return getServer(getLoadBalancer(serviceId));
    }

    protected Server getServer(ILoadBalancer loadBalancer) {
        if (loadBalancer == null) {
            return null;
        }
        return loadBalancer.chooseServer("default"); // TODO: better handling of key
    }

    protected ILoadBalancer getLoadBalancer(String serviceId) {
        return this.clientFactory.getLoadBalancer(serviceId);
    }

    public static class RibbonServer implements ServiceInstance {
        private final String serviceId;
        private final Server server;
        private final boolean secure;
        private Map<String, String> metadata;

        public RibbonServer(String serviceId, Server server) {
            this(serviceId, server, false, Collections.<String, String> emptyMap());
        }

        public RibbonServer(String serviceId, Server server, boolean secure,
                            Map<String, String> metadata) {
            this.serviceId = serviceId;
            this.server = server;
            this.secure = secure;
            this.metadata = metadata;
        }

        @Override
        public String getServiceId() {
            return this.serviceId;
        }

        @Override
        public String getHost() {
            return this.server.getHost();
        }

        @Override
        public int getPort() {
            return this.server.getPort();
        }

        @Override
        public boolean isSecure() {
            return this.secure;
        }

        @Override
        public URI getUri() {
            return DefaultServiceInstance.getUri(this);
        }

        @Override
        public Map<String, String> getMetadata() {
            return this.metadata;
        }

        public Server getServer() {
            return this.server;
        }


    }

}

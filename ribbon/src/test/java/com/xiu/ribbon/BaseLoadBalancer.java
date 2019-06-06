//
//public class BaseLoadBalancer extends AbstractLoadBalancer implements
//        PrimeConnections.PrimeConnectionListener, IClientConfigAware {
//
//    //负载策略规则
//    protected IRule rule = DEFAULT_RULE;
//
//    //所有LoadBalancer 维护的服务 健康检查策略 默认使用线性 ping 来检查维护的服务列表是否正常
//    protected IPingStrategy pingStrategy = DEFAULT_PING_STRATEGY;
//
//    //IPing 检查检查的具体检查方法
//    protected IPing ping = null;
//
//
//    //两者服务信息维护 是使用监听器的形式进行的 该类初始化的时候会注册相关的监听器，
//    //用于监听服务状态的变化并维护服务列表信息
//    //维护的所有服务列表信息
//    @Monitor(name = PREFIX + "AllServerList", type = DataSourceType.INFORMATIONAL)
//    protected volatile List<Server> allServerList = Collections
//            .synchronizedList(new ArrayList<Server>());
//    //伟华的所有状态为up的服务列表信息
//    @Monitor(name = PREFIX + "UpServerList", type = DataSourceType.INFORMATIONAL)
//    protected volatile List<Server> upServerList = Collections
//            .synchronizedList(new ArrayList<Server>());
//
//    public BaseLoadBalancer() {
//        this.name = DEFAULT_NAME;
//        this.ping = null;
//        //设置默认的负载策略 轮询策略
//        setRule(DEFAULT_RULE);
//        //开启 ping 健康检查的，使用定时任务的方式 根据默认的健康检查的册罗
//        setupPingTask();
//        //创建统计相关的对象
//        lbStats = new LoadBalancerStats(DEFAULT_NAME);
//    }
//     /*
//        提供了其他类型的构造方法@
//     */
//
//
//     //获取负载均衡组件的统计监控对象
//    @Override
//    public LoadBalancerStats getLoadBalancerStats() {
//        return lbStats;
//    }
//
//
//    /**
//     * Add a list of servers to the 'allServer' list; does not verify
//     * uniqueness, so you could give a server a greater share by adding it more
//     * than once
//     */
//    /**
//     * LoadBalancer 接口实现 将服务列表放入到allServer 服务列表中
//     * 添加新的服务列表信息到 allServer 所有服务列表中
//     * @param newServers
//     */
//    @Override
//    public void addServers(List<Server> newServers) {
//        if (newServers != null && newServers.size() > 0) {
//            try {
//                ArrayList<Server> newList = new ArrayList<Server>();
//                newList.addAll(allServerList);
//                newList.addAll(newServers);
//                setServersList(newList);
//            } catch (Exception e) {
//                logger.error("LoadBalancer [{}]: Exception while adding Servers", name, e);
//            }
//        }
//    }
//
//
//    /**
//     * Set the list of servers used as the server pool. This overrides existing
//     * server list.
//     */
//    public void setServersList(List lsrv) {
//        Lock writeLock = allServerLock.writeLock();
//        logger.debug("LoadBalancer [{}]: clearing server list (SET op)", name);
//
//        ArrayList<Server> newServers = new ArrayList<Server>();
//        writeLock.lock();
//        try {
//            ArrayList<Server> allServers = new ArrayList<Server>();
//            //遍历所有的服务列表
//            for (Object server : lsrv) {
//                if (server == null) {
//                    continue;
//                }
//
//                //对于字符类型的sertver 构造成Server对象
//                if (server instanceof String) {
//                    server = new Server((String) server);
//                }
//
//                if (server instanceof Server) {
//                    logger.debug("LoadBalancer [{}]:  addServer [{}]", name, ((Server) server).getId());
//                    //在LoadBalancer中维护的所有服务列表allServers中添加服务
//                    allServers.add((Server) server);
//                } else {
//                    throw new IllegalArgumentException(
//                            "Type String or Server expected, instead found:"
//                                    + server.getClass());
//                }
//
//            }
//            boolean listChanged = false;
//            //服务和原来的服务有偏差则需要通知监听器 更新相关的服务列表信息
//            if (!allServerList.equals(allServers)) {
//                listChanged = true;
//                if (changeListeners != null && changeListeners.size() > 0) {
//                    List<Server> oldList = ImmutableList.copyOf(allServerList);
//                    List<Server> newList = ImmutableList.copyOf(allServers);
//                    for (ServerListChangeListener l: changeListeners) {
//                        try {
//                            l.serverListChanged(oldList, newList);
//                        } catch (Exception e) {
//                            logger.error("LoadBalancer [{}]: Error invoking server list change listener", name, e);
//                        }
//                    }
//                }
//            }
//            if (isEnablePrimingConnections()) {
//                for (Server server : allServers) {
//                    if (!allServerList.contains(server)) {
//                        server.setReadyToServe(false);
//                        newServers.add((Server) server);
//                    }
//                }
//                if (primeConnections != null) {
//                    primeConnections.primeConnectionsAsync(newServers, this);
//                }
//            }
//            // This will reset readyToServe flag to true on all servers
//            // regardless whether
//            // previous priming connections are success or not
//            allServerList = allServers;
//            //执行监控检查
//            if (canSkipPing()) {
//                for (Server s : allServerList) {
//                    s.setAlive(true);
//                }
//                upServerList = allServerList;
//            } else if (listChanged) {
//                forceQuickPing();
//            }
//        } finally {
//            writeLock.unlock();
//        }
//    }
//
//    //获取服务列表
//    @Override
//    public List<Server> getServerList(boolean availableOnly) {
//        return (availableOnly ? getReachableServers() : getAllServers());
//    }
//
//    //获取可用的服务列表 所有状态为up的服务列表
//    @Override
//    public List<Server> getReachableServers() {
//        return Collections.unmodifiableList(upServerList);
//    }
//
//    /**
//     * 获取所有的服务列表
//     * @return
//     */
//    @Override
//    public List<Server> getAllServers() {
//        return Collections.unmodifiableList(allServerList);
//    }
//
//    @Override
//    public List<Server> getServerList(ServerGroup serverGroup) {
//        switch (serverGroup) {
//            case ALL:
//                return allServerList;
//            case STATUS_UP:
//                return upServerList;
//            case STATUS_NOT_UP:
//                ArrayList<Server> notAvailableServers = new ArrayList<Server>(
//                        allServerList);
//                ArrayList<Server> upServers = new ArrayList<Server>(upServerList);
//                notAvailableServers.removeAll(upServers);
//                return notAvailableServers;
//        }
//        return new ArrayList<Server>();
//    }
//
//    //标记服务下线
//    public void markServerDown(String id) {
//        boolean triggered = false;
//
//        id = Server.normalizeId(id);
//
//        if (id == null) {
//            return;
//        }
//
//        Lock writeLock = upServerLock.writeLock();
//        writeLock.lock();
//        try {
//            final List<Server> changedServers = new ArrayList<Server>();
//
//            for (Server svr : upServerList) {
//                if (svr.isAlive() && (svr.getId().equals(id))) {
//                    triggered = true;
//                    svr.setAlive(false);
//                    changedServers.add(svr);
//                }
//            }
//
//            if (triggered) {
//                logger.error("LoadBalancer [{}]:  markServerDown called for server [{}]", name, id);
//                notifyServerStatusChangeListener(changedServers);
//            }
//
//        } finally {
//            writeLock.unlock();
//        }
//    }
//}

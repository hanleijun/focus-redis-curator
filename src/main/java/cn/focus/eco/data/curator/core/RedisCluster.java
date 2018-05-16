package cn.focus.eco.data.curator.core;

import com.google.common.collect.Maps;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 1998 - 2017 SOHU Inc., All Rights Reserved.
 * <p>
 * redis cluster to map the tool
 * @author: leijunhan (leijunhan@sohu-inc.com)
 * @date: 2017/12/28
 */
public class RedisCluster {
    private Map<String, Map<String, RedisService>> cluster;

    public Map<String, Map<String, RedisService>> getCluster() {
        return cluster;
    }

    public void setCluster(Map<String, Map<String, RedisService>> cluster) {
        this.cluster = cluster;
    }

    public void addCluster(Map<String, List<RedisService>> cluster) {
        Map<String, Map<String, RedisService>> map = Maps.newConcurrentMap();
        cluster.forEach((k, v) -> {
            Map<String, RedisService> innerMap = Maps.newConcurrentMap();
            v.forEach(redisDao -> {
                JedisConnectionFactory factory = (JedisConnectionFactory)redisDao.getTemplate().getConnectionFactory();
                String hostName = factory.getHostName();
                String port = String.valueOf(factory.getPort());
                innerMap.put(hostName+":"+port, redisDao);
            });
            map.put(k, innerMap);
        });
        this.cluster = map;
    }
}

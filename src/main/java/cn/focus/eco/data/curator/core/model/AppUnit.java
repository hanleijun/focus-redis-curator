package cn.focus.eco.data.curator.core.model;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

import java.util.List;

/**
 * Copyright (C) 1998 - 2017 SOHU Inc., All Rights Reserved.
 * <p>
 *
 * @author: leijunhan (leijunhan@sohu-inc.com)
 * @date: 2018/1/19
 */
public class AppUnit {
    private String appName;
    private List<JedisConnectionFactory> nodes;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public List<JedisConnectionFactory> getNodes() {
        return nodes;
    }

    public void setNodes(List<JedisConnectionFactory> nodes) {
        this.nodes = nodes;
    }
}

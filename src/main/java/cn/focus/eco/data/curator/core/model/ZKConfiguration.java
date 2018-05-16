package cn.focus.eco.data.curator.core.model;

/**
 * Copyright (C) 1998 - 2017 SOHU Inc., All Rights Reserved.
 * <p>
 * bean of zookeeper configuration
 * @author: leijunhan (leijunhan@sohu-inc.com)
 * @date: 2018/1/4
 */
public class ZKConfiguration {
    String zkHost;

    public String getZkHost() {
        return zkHost;
    }

    public void setZkHost(String zkHost) {
        this.zkHost = zkHost;
    }

    @Override
    public String toString() {
        return zkHost;
    }
}

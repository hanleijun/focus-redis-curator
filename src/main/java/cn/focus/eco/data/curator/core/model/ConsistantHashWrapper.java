package cn.focus.eco.data.curator.core.model;

import cn.focus.eco.data.curator.core.ConsistentHash;

import java.util.Map;

/**
 * Copyright (C) 1998 - 2017 SOHU Inc., All Rights Reserved.
 * <p>
 *
 * @author: leijunhan (leijunhan@sohu-inc.com)
 * @date: 2018/1/19
 */
public class ConsistantHashWrapper {
    private Map<String, ConsistentHash> wrapper;

    public Map<String, ConsistentHash> getWrapper() {
        return wrapper;
    }

    public void setWrapper(Map<String, ConsistentHash> wrapper) {
        this.wrapper = wrapper;
    }
}

package cn.focus.eco.data.curator.core;

import cn.focus.eco.data.curator.core.constants.Constants;
import cn.focus.eco.data.curator.core.exception.NoConfDataException;
import cn.focus.eco.data.curator.core.model.ConsistantHashWrapper;
import cn.focus.eco.data.curator.core.util.CommonUtil;
import cn.focus.eco.house.conf.core.CachedConfCenterTemplate;
import cn.focus.eco.house.conf.core.ConfCenterTemplate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Copyright (C) 1998 - 2017 SOHU Inc., All Rights Reserved.
 * <p>
 * changing processing module
 * @author: leijunhan (leijunhan@sohu-inc.com)
 * @date: 2018/1/9
 */

@Component
public class WatchThread extends Thread {
    @Autowired
    private RegisterBean registerBean;
    @Autowired
    private ConfCenterTemplate confCenterTemplate;

    @Override
    public void run() {
        CachedConfCenterTemplate cachedConfCenterTemplate = new CachedConfCenterTemplate.Builder().watch(Constants.REDIS_CLUSTER_CONF_NODE)
                .register(confCenterTemplate)
                .build();
        try {
            cachedConfCenterTemplate.withPathListener((client, event) -> {
                ChildData data1 = event.getData();
                switch (event.getType()) {
                    case CHILD_ADDED:
                        System.out.println("CHILD_ADDED");
                        break;
                    case CHILD_REMOVED:
                        System.out.println("CHILD_REMOVED");
                        break;
                    case CHILD_UPDATED:
                        System.out.println("CHILD_UPDATED");
                        String data = confCenterTemplate.getData(Constants.REDIS_CLUSTER_CONF_PATH);
                        if(StringUtils.isBlank(data)){
                            throw new NoConfDataException("no zk configuration");
                        }
                        Map<String, List<RedisTemplate<String, Object>>> redisTemplates = CommonUtil.generateRedisTemplates(data);

                        Map<String, List<RedisService>> daos = Maps.newConcurrentMap();
                        redisTemplates.forEach((k, v) -> {
                            List<RedisService> services = v.stream().map(t -> new RedisService(t)).collect(Collectors.toList());
                            daos.put(k, services);
                        });
                        RedisCluster cluster = new RedisCluster();
                        cluster.addCluster(daos);

                        Map<String, Object> maps = new HashMap<>();
                        maps.put("cluster", cluster.getCluster());
                        registerBean.register("redisCluster", RedisCluster.class, maps);

                        Map<String, Object> maps2 = new HashMap<>();
                        Map<String, Map<String, RedisService>> map =  cluster.getCluster();
                        Map<String, ConsistentHash> handler = Maps.newConcurrentMap();
                        map.forEach((k, v) -> {
                            ConsistentHash consistentHash = new ConsistentHash();
                            consistentHash.prepare(Lists.newArrayList(v.keySet()));
                            handler.put(k, consistentHash);
                        });
                        maps2.put("wrapper", handler);
                        registerBean.register("consistentHash", ConsistantHashWrapper.class, maps2);
                        break;
                    default:
                        break;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (true) {
        }
    }
}

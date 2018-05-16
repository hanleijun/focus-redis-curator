package cn.focus.eco.data.curator.core;

import cn.focus.eco.data.curator.core.constants.Constants;
import cn.focus.eco.data.curator.core.exception.NoConfDataException;
import cn.focus.eco.data.curator.core.model.ConsistantHashWrapper;
import cn.focus.eco.data.curator.core.model.ZKConfiguration;
import cn.focus.eco.data.curator.core.util.CommonUtil;
import cn.focus.eco.house.conf.core.ConfCenterTemplate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Copyright (C) 1998 - 2017 SOHU Inc., All Rights Reserved.
 * <p>
 * base bean configuration
 * @author: leijunhan (leijunhan@sohu-inc.com)
 * @date: 2018/1/4
 */

@Configuration
public class NewConfigBean {

    @Value("${spring.zk.zkHost}")
    String zkhost;

    @Bean("zkConfiguration")
    public ZKConfiguration zkConfiguration(){
        ZKConfiguration zkConfiguration = new ZKConfiguration();
        zkConfiguration.setZkHost(zkhost);
        return zkConfiguration;
    }

    @Bean
    public ConfCenterTemplate confCenterTemplate(@Autowired ZKConfiguration zkConfiguration) {
        ConfCenterTemplate template = new ConfCenterTemplate.Builder().connect(zkConfiguration.toString()).build();
        return template;
    }

    @Bean(name="redisTemplates")
    public Map<String, List<RedisTemplate<String, Object>>> ecoRedisTemplates(@Autowired ConfCenterTemplate template) throws NoConfDataException {
        String data = template.getData(Constants.REDIS_CLUSTER_CONF_PATH);
        if(StringUtils.isBlank(data)){
            throw new NoConfDataException("no zk configuration");
        }
        return CommonUtil.generateRedisTemplates(data);
    }

    @Bean(name="redisCluster")
    public RedisCluster ecoRedisDaos(@Autowired @Qualifier("redisTemplates") Map<String, List<RedisTemplate<String, Object>>> templates) {
        Map<String, List<RedisService>> daos = Maps.newConcurrentMap();
        templates.forEach((k, v) -> {
            List<RedisService> services = v.stream().map(t -> new RedisService(t)).collect(Collectors.toList());
            daos.put(k, services);
        });
        RedisCluster cluster = new RedisCluster();
        cluster.addCluster(daos);
        return cluster;
    }

    @Bean(name="consistentHash")
    public ConsistantHashWrapper consistentHash(@Autowired @Qualifier("redisCluster") RedisCluster redisCluster) {
        Map<String, Map<String, RedisService>> map =  redisCluster.getCluster();
        Map<String, ConsistentHash> handler = Maps.newConcurrentMap();
        map.forEach((k, v) -> {
            ConsistentHash consistentHash = new ConsistentHash();
            consistentHash.prepare(Lists.newArrayList(v.keySet()));
            handler.put(k, consistentHash);
        });
        ConsistantHashWrapper wrapper = new ConsistantHashWrapper();
        wrapper.setWrapper(handler);
        return wrapper;
    }
}

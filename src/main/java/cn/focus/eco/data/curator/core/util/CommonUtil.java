package cn.focus.eco.data.curator.core.util;

import cn.focus.eco.data.curator.core.model.AppUnit;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 1998 - 2017 SOHU Inc., All Rights Reserved.
 * <p>
 *
 * @author: leijunhan (leijunhan@sohu-inc.com)
 * @date: 2018/1/10
 */
public class CommonUtil {
    public static Map<String, List<RedisTemplate<String, Object>>> generateRedisTemplates(String data) {
        List<AppUnit> connections = JSONObject.parseArray(data, AppUnit.class);
        Map<String, List<RedisTemplate<String, Object>>> map = Maps.newConcurrentMap();
        connections.stream().forEach( u -> {
        List<RedisTemplate<String, Object>> templates = Lists.newCopyOnWriteArrayList();
        u.getNodes().stream().forEach( c ->
        {
            RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(c);
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
            redisTemplate.setHashKeySerializer(new StringRedisSerializer());
            redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
            redisTemplate.afterPropertiesSet();
            // is a must here
            c.afterPropertiesSet();
            templates.add(redisTemplate);
        });
            map.put(u.getAppName(), templates);
        });
        return map;
    }
}

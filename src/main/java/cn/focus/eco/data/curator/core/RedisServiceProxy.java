package cn.focus.eco.data.curator.core;

import cn.focus.eco.data.curator.core.model.ConsistantHashWrapper;
import cn.focus.eco.data.curator.core.util.MultiMap;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Copyright (C) 1998 - 2017 SOHU Inc., All Rights Reserved.
 * <p>
 *
 * @author: leijunhan (leijunhan@sohu-inc.com)
 * @date: 2018/1/12
 */

@Component
public class RedisServiceProxy {

    @Autowired
    private SpringUtil springUtil;

    private static final Logger logger = Logger.getLogger(RedisServiceProxy.class);

    /**
     * 普通缓存获取
     * @param appName 应用名称也即域名前缀
     * @param key 键
     * @return 值
     */
    public Object get(String appName, String key){
        Map<String, Map<String, RedisService>> handler =  getRedisMap();
        Map<String, RedisService> map = handler.get(appName);
        ConsistentHash consistentHash = getConsistentHash(appName);
        String hostName = consistentHash.routeByKey(key);
        RedisService redisService = map.get(hostName);
        long start = System.currentTimeMillis();
        Object re = redisService.get(key);
        long end = System.currentTimeMillis();
        logger.info("[focus-redis-curator] get cost" + (end - start) + "ms");
        return re;
    }

    /**
     * 主从版mGet
     * @param appName 应用名称也即域名前缀
     * @param keys
     * @return
     */
    public Object mGet(String appName, List<String> keys){
        Map<String, Map<String, RedisService>> handler =  getRedisMap();
        Map<String, RedisService> map = handler.get(appName);
        ConsistentHash consistentHash = getConsistentHash(appName);
        MultiMap multiMap = new MultiMap();
        keys.stream().forEach(
                key -> {
                    String hostName = consistentHash.routeByKey(key);
                    multiMap.put(hostName, key);
                }
        );
        List<Object> result = Lists.newArrayList();
        multiMap.getAll().forEach(
                (x, y) -> {
                    RedisService redisService = map.get(x);
                    result.addAll(redisService.mGet((List<String>)y));
                }
        );
        return result;
    }

    /**
     * key 不存在时，为 key 设置指定的值
     * @param appName
     * @param key
     * @return
     */
    public boolean setNx(String appName, String key, Object value){
        Map<String, Map<String, RedisService>> handler =  getRedisMap();
        Map<String, RedisService> map = handler.get(appName);
        ConsistentHash consistentHash = getConsistentHash(appName);
        try{
            String hostName = consistentHash.routeByKey(key);
            RedisService redisService = map.get(hostName);
            boolean re = redisService.setNx(key, value);
            return re;
        }catch (Exception ex){
            logger.info("redis proxy setting error!");
            return false;
        }
    }

    public boolean expire(String appName, String key, long time){
        Map<String, Map<String, RedisService>> handler =  getRedisMap();
        Map<String, RedisService> map = handler.get(appName);
        ConsistentHash consistentHash = getConsistentHash(appName);
        try{
            String hostName = consistentHash.routeByKey(key);
            RedisService redisService = map.get(hostName);
            return redisService.expire(key, time);
        }catch (Exception ex){
            logger.info("redis proxy setting error!");
            return false;
        }
    }

    public void delete(String appName, String key){
        Map<String, Map<String, RedisService>> handler =  getRedisMap();
        Map<String, RedisService> map = handler.get(appName);
        ConsistentHash consistentHash = getConsistentHash(appName);
        try{
            String hostName = consistentHash.routeByKey(key);
            RedisService redisService = map.get(hostName);
            redisService.del(key);
        }catch (Exception ex){
            logger.info("redis proxy setting error!");
        }
    }

    /**
     * 普通缓存放入
     * @param appName 应用名称也即域名前缀
     * @param key 键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String appName,String key,Object value) {
        return StringUtils.isBlank(key) ? false : set(appName, key, value, 0);
    }

    /**
     * 普通缓存放入并设置时间
     * @param appName 应用名称也即域名前缀
     * @param key 键
     * @param value 值
     * @param time 时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String appName, String key,Object value,long time){
        Map<String, Map<String, RedisService>> handler =  getRedisMap();
        Map<String, RedisService> map = handler.get(appName);
        ConsistentHash consistentHash = getConsistentHash(appName);
        try{
            String hostName = consistentHash.routeByKey(key);
            RedisService redisService = map.get(hostName);
            redisService.set(key, value, time);
            return true;
        }catch (Exception ex){
            logger.info("redis proxy setting error!");
            return false;
        }
    }

    private Map<String, Map<String, RedisService>> getRedisMap(){
        return ((RedisCluster)springUtil.getBean("redisCluster")).getCluster();
    }

    private ConsistentHash getConsistentHash(String appName){
        ConsistantHashWrapper wrapper = (ConsistantHashWrapper)springUtil.getBean("consistentHash");
        return wrapper.getWrapper().get(appName);
    }
}

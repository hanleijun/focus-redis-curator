package cn.focus.eco.data.curator.core;

import cn.focus.eco.data.curator.core.constants.Constants;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static cn.focus.eco.data.curator.core.constants.Constants.UTF8;

/**
 * Copyright (C) 1998 - 2017 SOHU Inc., All Rights Reserved.
 * <p>
 *  consistent algorithm router
 * @author: leijunhan (leijunhan@sohu-inc.com)
 * @date: 2017/12/26
 */
public class ConsistentHash {
    private SortedMap<Long,String> ketamaNodes=new TreeMap<Long,String>();
    private Integer numberOfReplicas = Constants.REDIS_CLUSTER_REPLICAS;
    private static HashFunction hashFunction= Hashing.md5(); //guava
    private List<String> nodes;
    private volatile boolean init=false; //标志是否初始化完成

    public void prepare(List<String> nodes){
        this.nodes=nodes;
        init();
    }

    public Integer getNumberOfReplicas() {
        return numberOfReplicas;
    }

    public void setNumberOfReplicas(Integer numberOfReplicas) {
        this.numberOfReplicas = numberOfReplicas;
    }

    public List<String> getNodes() {
        return nodes;
    }

    public void setNodes(List<String> nodes) {
        this.nodes = nodes;
        init();
    }

    /**
     * route the (K, V) to a physical redis node
     * if keyName is the same as a virtual node name, store it to the corresponding physical node,
     * if not, find the next virtual node, and store it to the corresponding physical node
     * @param keyName
     * @return
     */
    public String routeByKey(String keyName){
        if(!init){
            throw new RuntimeException("not initiated yet.");
        }

        long hash=hash(keyName);
        if(!ketamaNodes.containsKey(hash)){
            SortedMap<Long,String> tailMap=ketamaNodes.tailMap(hash);
            if(tailMap.isEmpty()){
                hash=ketamaNodes.firstKey();
            }else{
                hash=tailMap.firstKey();
            }
        }
        return ketamaNodes.get(hash);
    }

    public synchronized void addNode(String node){
        init=false;
        nodes.add(node);
        init();
    }

    public synchronized  void removeNode(String node){
        init = false;
        nodes.remove(node);
        for(int i=0; i<numberOfReplicas; i++){
            String virtualName = node + "#" + i;
            long hash = hash(virtualName);
            ketamaNodes.remove(hash);
        }
        init();
    }

    public void init(){
        for(String node:nodes){
            for(int i=0;i<numberOfReplicas;i++){
                Long k = hash(node+"#"+i);
                ketamaNodes.put(k,node);
            }
        }
        init=true;
    }

    public void printNodes(){
        for(Long key:ketamaNodes.keySet()){
            System.out.println(key + "::" + ketamaNodes.get(key));
        }
    }

    /**
     * 32-bit hash code
     * @param virtualName
     * @return
     */
    public static long hash(String virtualName){
        byte[] digest=hashFunction.hashString(virtualName, UTF8).asBytes();
        long rv = ((long)(digest[3] & 0xFF) << 24)
                | ((long)(digest[2] & 0xFF) << 16)
                | ((long)(digest[1] & 0xFF) << 8)
                | ((long)digest[0] & 0xFF);
        return rv & 0xffffffffL;
    }
}

package cn.focus.eco.data.curator.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** 
 * @description could store <T, list<K>> item in a hashmap,
 * when encounter a sub-item which has the same key, thus the values would
 * be store in a List as the value of item.
 * @author Legend Han(leijunhan@sohu-inc.com) 
 * @date 2015-6-11 上午8:55:02 
 */
public class MultiMap<T,K>{
	private Map<T, List<K>> map;
	private static final int DEFAULT_SIZE = 8192;
	public MultiMap() {
		map = new ConcurrentHashMap<T, List<K>>(DEFAULT_SIZE);
	}
	
	public void put(T key, K value){
		if(map.containsKey(key)){
			((List<K>) (map.get(key))).add(value);
		}else{
			ArrayList<K> list = new ArrayList<K>();
			list.add(value);
			map.put(key, list);
		}
	}
	
	public List<K> get(T key){
		return map.get(key);
	}
	
	public boolean contains(T key){
		return map.containsKey(key);
	}
	
	public void delete(T key){
		map.remove(key);
	}
	
	public void clear(){
		map.clear();
	}

	public Map<T, List<K>> getAll(){
		return map;
	}

}

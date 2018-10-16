package cn.wenqi.redis;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author wenqi
 * @since v
 */
public class RedisMock {

    private RedisMock(){}

    private static final ConcurrentMap<String,String> map=new ConcurrentHashMap<>();

    public static boolean lock(String key){
        long rs=RedisPoolUtil.setnx(key,String.valueOf(System.currentTimeMillis()));
        return rs != 0;
    }

    public static boolean unlock(String key){
        long rs=RedisPoolUtil.del(key);
        return rs != 0;
    }

}

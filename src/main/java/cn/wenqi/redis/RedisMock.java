package cn.wenqi.redis;

/**
 * @author wenqi
 * @since v
 */
public class RedisMock {

    private RedisMock(){}

    public static boolean lock(String key){
        long rs=RedisPoolUtil.setnx(key,String.valueOf(System.currentTimeMillis()));
        return rs != 0;
    }

    public static boolean unlock(String key){
        long rs=RedisPoolUtil.del(key);
        return rs != 0;
    }

}

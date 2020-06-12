package com.mengxuegu.springcloud.service;

import com.mengxuegu.springcloud.config.RedisConfig;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author HuangCheng
 * @date 2020/3/28 11:49
 * @desc: redis工具类
 */

@Component
public class RedisService {

    protected static final Logger logger = Logger.getLogger(RedisService.class);


    @Autowired
    private RedisTemplate redisTemplate;

    private static JedisPool jedisPool;

    @Autowired
    private RedisConfig redisConfig;

    /**
     * 写入缓存
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key,Object value){
        boolean result = false;
        try {
            ValueOperations<Serializable,Object> operations = redisTemplate.opsForValue();
            operations.set(key,value);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存设置时效时间
     * @param key
     * @param value
     * @param expireTime
     * @return
     */
    public boolean setEx(final String key,Object value,Long expireTime){
        boolean result = false;
        try {
            ValueOperations<Serializable,Object> operations = redisTemplate.opsForValue();
            operations.set(key,value);
            redisTemplate.expire(key,expireTime, TimeUnit.SECONDS);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 判断缓存中是否有对应的值
     * @param key
     * @return
     */
    public boolean exists(final String key){
        return redisTemplate.hasKey(key);
    }

    /**
     * 读取缓存
     * @param key
     * @return
     */
    public Object get(final String key){
        Object result = null;
        ValueOperations<Serializable,Object> operations = redisTemplate.opsForValue();
        result = operations.get(key);
        return result;

    }

    /**
     * 删除对应的value
     * @param key
     * @return
     */
    public boolean remove(final String key){
        if(exists(key)){
            Boolean delete = redisTemplate.delete(key);
            return delete;
        }
        return false;
    }

    /**
     * 获取redis连接池
     */
    @PostConstruct
    public void init() {
        jedisPool = redisConfig.redisPoolFactory();
    }

    /**
     * 获取值
     * @param key
     * @param indexdb
     * @return
     */
    public static String get(String key, int indexdb) {
        Jedis jedis = null;
        String value = null;

        try {
            jedis = jedisPool.getResource();//获取一个jedis实例
            jedis.select(indexdb);
            value = jedis.get(key);
        } catch (Exception e) {
            logger.error("错误日志："+e.getMessage());
        } finally {
            jedis.close();
        }
        return value;
    }

    /**
     * redis分布式锁，设置锁
     * @param key
     * @param value
     * @return
     */
    public boolean setnx(String key,String value){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if(jedis == null){
                return false;
            }
            //NX:表示是否存在key,存在就不set
            //PX:表示key过期时间单位设置为毫秒，EX表示秒
            return jedis.set(key,value,"NX","PX",1000*60).equalsIgnoreCase("ok");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return false;
    }

    /**
     * 删除锁
     * @param key
     * @param value
     * @return
     */
    public int delnx(String key,String value){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if(jedis == null){
                return 0;
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("if redis.call('get','").append(key).append("')").append("=='").append(value).append("'")
                    .append(" then ").append("  return redis.call('del','").append(key).append("')").append(" else ")
                    .append(" return 0").append(" end");
            return Integer.valueOf(jedis.eval(stringBuilder.toString()).toString());
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(jedis != null){
                jedis.close();
            }
        }
        return 0;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        //String s="abczaada";
        int n = s.length();
        Set<Character> set = new HashSet<>();//set集合元素无序，不重复
        int i=0,ans=0,j=0;
        while(i<n&&j<n){
            if(set.contains(s.charAt(j))){
                set.remove(s.charAt(i++));  //如果有重复元素，从第一个开始删除之前添加的元素，直到删除与新添加的钙元素重复的元素为止。
            }else{
                set.add(s.charAt(j++));
                ans=Math.max(ans,j-i);//删除了i个元素，因此还剩j-i个元素。
            }
        }
        System.out.println(ans);
    }
}

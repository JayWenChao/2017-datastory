package cn.hy.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**获取操作模板
 * Created by Administrator on 2017/7/20.
 */
public abstract  class RedisServer<T> {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    //获取hash操作
    @Resource
    protected HashOperations<String, String, T> hashOperations;

    protected abstract String getkey();

    /**
     *
     * @param key
     * @param domain
     * @param expire
     */
    public void put(String key,T domain,long expire){

         hashOperations.put(getkey(),key,domain);
         if(expire != -1){

             redisTemplate.expire(getkey(),expire, TimeUnit.SECONDS);
         }
    }

    /**
     * 获取值
     * @param key
     * @return
     */
   public T getvalue(String key){

       return hashOperations.get(getkey(),key);
   }

    /**
     * 删除
     * @param key
     */
   public void delvalue(String key){

      hashOperations.delete(getkey(),key);
   }

    /**
     * 获取hash所有的值
     * @return
     */
   public List<T> getAllValue(){

       return hashOperations.values(getkey());
   }

    /**
     * 获取keys
     * @return
     */
   public Set<String> getAllKeys(){

       return hashOperations.keys(getkey());
   }

    /**
     * 是否存在
     * @param key
     * @return
     */
   public boolean isKeyexists(String key){

       return hashOperations.hasKey(getkey(),key);
   }

    /**
     * 获取实例值
     * @return
     */
   public long size(){

      return  hashOperations.size(getkey());
   }

    /**
     * 删除所有
     */
   public void empty(){

       Set<String> keys = getAllKeys();
       keys.stream().forEach(e -> hashOperations.delete(getkey(),e));
   }



}

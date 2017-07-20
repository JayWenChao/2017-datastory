package cn.hy.com.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Springboot-redis注意的事项
 * 第一步:设置好主机名，端口，以及最大连接空闲数，最小。
 * 第二步:在Redis中的Redis.conf配置文件中，修改bind 127.0.0.1为bind 0.0.0.0,同时打开requirepass "myRedis"认证权限密码
 * 第三步:使用RedisTemplate模板，设置连接jedisConnfactory,同时配置序列化
 * 第四步:实例化Redis五种数据类型
 *
 * Created by Administrator on 2017/7/19.
 */
@Configuration
public class RedisConfig {


    @Value(value = "${spring.redis.host}")
    private String host;
    @Value(value = "${spring.redis.port}")
    private String port;
    @Value(value = "${spring.redis.timeout}")
    private String timeout;
    @Value(value = "${spring.redis.password}")
    private String password;
    @Value(value = "${spring.redis.pool.max-idle}")
    private Integer max_idle;
    @Value(value = "${spring.redis.pool.min-idle}")
    private Integer min_idle;
    @Bean
    public JedisConnectionFactory jedisConnectionFactory(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setJmxEnabled(true);
        config.setLifo(true);
        config.setMaxIdle(max_idle);
        config.setMinIdle(min_idle);
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setPort(Integer.valueOf(port));
        jedisConnectionFactory.setHostName(host);
        jedisConnectionFactory.setTimeout(Integer.valueOf(timeout));
        jedisConnectionFactory.setPassword(password);
        jedisConnectionFactory.setPoolConfig(config);
        jedisConnectionFactory.afterPropertiesSet();
        return jedisConnectionFactory;
    }


    //获取模板
    @Bean
    public RedisTemplate<String,Object> getTemplate(){

        RedisTemplate<String,Object> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new JdkSerializationRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new JdkSerializationRedisSerializer());
        template.setConnectionFactory(jedisConnectionFactory());
        return template;
    }


    //实例化5种格式数据 string hash list zset set

    /**
     * String类型
     * @param redisTemplate
     * @return
     */
    @Bean
    public ValueOperations<String,Object> valueOperations(RedisTemplate<String,Object> redisTemplate){

        return redisTemplate.opsForValue();
    }

    /**
     *List
     * @param redisTemplate
     * @return
     */
    @Bean
    public ListOperations<String,Object> listOperations(RedisTemplate<String,Object> redisTemplate){

        return redisTemplate.opsForList();
    }

    /**
     * Hash
     * @param redisTemplate
     * @return
     */
    @Bean
    public HashOperations<String,String,Object> hashOperations(RedisTemplate<String,Object> redisTemplate){

        return redisTemplate.opsForHash();
    }

    /**
     * Zset
     * @param redisTemplate
     * @return
     */
    @Bean
    public ZSetOperations<String,Object> zSetOperations(RedisTemplate<String,Object> redisTemplate){

        return redisTemplate.opsForZSet();
    }


    /**
     * Set
     * @param redisTemplate
     * @return
     */
    @Bean
    public SetOperations<String,Object> setOperations(RedisTemplate<String,Object> redisTemplate){

        return redisTemplate.opsForSet();
    }

}

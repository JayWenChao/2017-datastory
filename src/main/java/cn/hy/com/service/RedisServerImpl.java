package cn.hy.com.service;

import cn.hy.com.entity.Student;
import org.springframework.stereotype.Service;

/**
 * Created by Administrator on 2017/7/20.
 */
@Service
public class RedisServerImpl extends RedisServer<Student> {

    private static final String REDIS_KEY = "redis:radar";

    @Override
    protected String getkey() {
        return REDIS_KEY;
    }
}

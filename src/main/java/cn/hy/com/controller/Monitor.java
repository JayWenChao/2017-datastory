package cn.hy.com.controller;

import cn.hy.com.entity.Student;
import cn.hy.com.service.RedisServerImpl;
import cn.hy.com.service.StudentService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Created by Administrator on 2017/7/17.
 */
@RestController
public class Monitor {

    private static final Logger logger = Logger.getLogger(Monitor.class);

    @Value(value = "${spring.redis.host}")
    private String host;
    @Autowired
    private StudentService  studentService;

    @Autowired
    private RedisServerImpl redisServer;

    @RequestMapping(value = "/detail/{id}",method = RequestMethod.GET)
    public Object getJobDatail(@PathVariable("id") Integer id, HttpServletRequest request) throws NoSuchFieldException, IllegalAccessException, InstantiationException, InvocationTargetException {



        System.out.println(host+"-----主机------");
        logger.info("数据访问");
        Student student = studentService.selectSingle(id);
        
        Student st = new Student();
        st.setId(8);
        st.setPasswd("cmt");
        st.setUsername("美丽的黄昏");
        redisServer.put("mergekey",student,-1);
        redisServer.put("he",st,-1);
        Set<String> keys = redisServer.getAllKeys();
        keys.stream().forEach(e ->{System.out.println(e);});
        List<Student> allValue =  redisServer.getAllValue();
        String str = allValue.toString();
        System.out.println(str.getClass().getName());
        JSONArray parse = (JSONArray) JSONObject.parse(str);
        for (int i = 0; i <parse.size() ; i++) {

            Student sd = JSONObject.parseObject(parse.get(i).toString(), Student.class);
            System.out.println(sd.getUsername());
        }
        System.out.println(parse.getClass().getName());
        //System.out.println(json);
        long size = redisServer.size();
        System.out.println("数据的长度是:"+size);
        return student;
    }

}

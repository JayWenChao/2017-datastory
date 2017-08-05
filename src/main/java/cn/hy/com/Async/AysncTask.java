package cn.hy.com.Async;


import org.apache.log4j.Logger;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;



/**
 * 异步任务
 */
@Component
public class AysncTask {

    private static final Logger log = Logger.getLogger(AysncTask.class);

    @Async
    public void doFilter(){

        System.out.println("------------测试异步数据-----------");
        log.info("----------elasticsearch-------");
    }

}

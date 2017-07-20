package cn.hy.com;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * 使用Mybaits的ORM映射框架，采用Ehcache缓存机制
 * 使用注释@EnableCacheing
 * 开启定时调度机制，使用注解@EnableScheduling
 */
@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@ComponentScan
@EnableScheduling
public class SpringbootCloudApplication {



	public static void main(String[] args) {

		SpringApplication.run(SpringbootCloudApplication.class, args);
	}
}

package cn.hy.com;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;



@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@Controller
public class SpringbootCloudApplication {


	public static void main(String[] args) {

		SpringApplication.run(SpringbootCloudApplication.class, args);
	}
}

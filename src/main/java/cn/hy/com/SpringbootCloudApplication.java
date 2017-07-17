package cn.hy.com;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@SpringBootApplication
@Configuration
@EnableAutoConfiguration
@Controller
public class SpringbootCloudApplication {

	private static final Logger logger = Logger.getLogger(SpringbootCloudApplication.class);

	@RequestMapping(value = "/thymplate",method = RequestMethod.GET)
	public String getHello(){
        logger.info("为啥会这样");
		System.out.println("-----------");
		return "word";

	};



	public static void main(String[] args) {

		SpringApplication.run(SpringbootCloudApplication.class, args);
	}
}

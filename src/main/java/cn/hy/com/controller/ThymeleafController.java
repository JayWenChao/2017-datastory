package cn.hy.com.controller;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 *
 * Created by Administrator on 2017/7/17.
 */
@Controller
public class ThymeleafController {

    private static final  Logger logger = Logger.getLogger(ThymeleafController.class);

    /**
     * 使用Thymeleaf的坑，找不到404，在springboot-1.4版本以上
     * 一般需要定义Thymeleaf的版本问题
     * @param model
     * @return
     */
    @RequestMapping(value = "/data",method = RequestMethod.GET)
    public String getHello(Model model){

        logger.info("---------------------");
        model.addAttribute("name","thymeleaf");
        return "word";

    }

}

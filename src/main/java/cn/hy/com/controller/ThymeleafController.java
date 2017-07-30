package cn.hy.com.controller;

import cn.hy.com.entity.Student;
import cn.hy.com.service.StudentService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 *
 * springsecurity安全验证后，成功调转的页面，所需的用户名
 * 从springsecurity中
 * Created by Administrator on 2017/7/17.
 */
@Controller
public class ThymeleafController {

    private static final  Logger logger = Logger.getLogger(ThymeleafController.class);

    @Autowired
    private StudentService studentService;
    /**
     * 使用Thymeleaf的坑，找不到404，在springboot-1.4版本以上
     * 一般需要定义Thymeleaf的版本问题
     * @param model
     * @return
     */
    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String getHello(Model model){

        logger.info("---------------------");
        model.addAttribute("name","thymeleaf");
        return "word";

    }


    /**
     * 成功调转的页面
     * @param model
     * @return
     */
    @RequestMapping(value = "/dashboard",method = RequestMethod.GET)
    public String  TryLogin(Model model) {


        //获取安全上下文环境
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        Object principal = authentication.getPrincipal();
        Object details = authentication.getDetails();
        System.out.println(details);
        System.out.println(principal.getClass().getName().equals(UserDetails.class));
        if(principal instanceof UserDetails){

            String username = ((UserDetails) principal).getUsername();
            model.addAttribute("username",username);
        }else{

           return principal.toString();
        }
        return "vue";
    }


}

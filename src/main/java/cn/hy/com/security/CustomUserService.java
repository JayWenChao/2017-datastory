package cn.hy.com.security;

import cn.hy.com.entity.Student;
import cn.hy.com.service.StudentService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取服务
 */
public class CustomUserService implements UserDetailsService{


    @Resource(name = "studentService")
    private StudentService studentService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Student student = studentService.findObject(username);
        if(student ==null){

            throw new UsernameNotFoundException("用户账号不存在");
        }
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        CustomDetail cs = new CustomDetail(student.getId(),student.getUsername(),student.getPasswd(), AuthorityUtils.createAuthorityList("ROLE_USER"));
        return cs;
    }
}

package cn.hy.com.security;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


/**
 * 获取服务
 */
public class CustomUserService implements UserDetailsService{


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        User user = new User("spark","2014", AuthorityUtils.createAuthorityList("ROLE_CLIENT","ROLE_TRUSTED_CLIENT"));
        return user;
    }
}

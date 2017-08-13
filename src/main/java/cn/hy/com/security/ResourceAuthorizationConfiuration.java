package cn.hy.com.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;

/**
 * 资源服务器
 */
@Configuration
@EnableResourceServer
public class ResourceAuthorizationConfiuration extends ResourceServerConfigurerAdapter {

    private static final String RADAR_RESOURCE_ID = "order";
    @Autowired
    private AuthenticationManager authenticationManager;

    public ResourceAuthorizationConfiuration() {
        super();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(RADAR_RESOURCE_ID)
                .authenticationManager(authenticationManager)
                .tokenServices(defaultTokenServices());
    }
    
    public static class OAuth2RequestMatcher implements RequestMatcher
    {

        @Override
        public boolean matches(HttpServletRequest request) {
            String auth = request.getHeader("Authorzation");
            boolean haveOauth2Token = (auth != null) && auth.startsWith("Bearer");
            boolean haveAccesstoken = request.getParameter("access_token") != null;
            return haveAccesstoken || haveAccesstoken;
        }
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {

        /*http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and()
                .requestMatchers()
                .anyRequest()
                .and()
                .anonymous()
                 .and()
                .authorizeRequests()
                .antMatchers("/order/**").authenticated();*/
        http.requestMatcher(new OAuth2RequestMatcher())
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS)
                .permitAll().anyRequest()
                .authenticated();
    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter(){
        JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
        accessTokenConverter.setSigningKey("123");
        return accessTokenConverter;
    }
    @Bean
    public TokenStore tokenStore(){
        JwtTokenStore jwtTokenStore = new JwtTokenStore(jwtAccessTokenConverter());
        return jwtTokenStore;
    }
    @Bean
    public DefaultTokenServices defaultTokenServices(){

        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setAuthenticationManager(authenticationManager);
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setTokenEnhancer(jwtAccessTokenConverter());
        return defaultTokenServices;
    }
}

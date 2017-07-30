package cn.hy.com.jwt;

import io.jsonwebtoken   .*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mobile.device.Device;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtils implements Serializable{

    private static final String CLIMAS_KEY_SUB = "sub";
    private static final String CLIMAS_KEY_CERATEDATE = "date";
    private static final String ClIMAS_KEY_DEVICE = "device";
    //使用spring-mobile获取设备来源
    private static final String AUDIENCE_NOKNOW = "noknow";
    private static final String AUDIENCE_NORMAL = "normal";
    private static final String AUDIENCE_MOBILE = "mobile";
    private static final String AUDIENCE_TABLET = "tablet";
    @Value(value = "${jwt.secret}")
    private String secret;
    @Value(value = "${jwt.expiration}")
    private long expiretion;
    public String getUsernameFromToken(String token){

        String username = null;
        try {
            Claims claims = getClaimsFromToken(token);
            username= claims.getSubject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return username;
    }
   public  String generateToken(Map<String ,Object> claims){

        return Jwts.builder().setClaims(claims)
                .setExpiration(genrateExpireDate())
                .signWith(SignatureAlgorithm.HS256,secret)
                .compact();//数字签名
   }

    /**
     *
     * @param userDetails
     * @param device
     * @return
     */
    public Map<String,Object> genrateclaimsParam(UserDetails userDetails,Device device){

       Map<String,Object> map = new HashMap<>();
       map.put(CLIMAS_KEY_SUB,userDetails.getUsername());
       map.put(ClIMAS_KEY_DEVICE,genrateDivce(device));
       map.put(CLIMAS_KEY_CERATEDATE,new Date());
       return map;
    }
    /**
     * 解析token，获取凭证信息
     * @param token
     * @return
     */
    private Claims getClaimsFromToken(String token){
        Claims claims = null;
        try {
            claims= Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
            return claims;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成过期时间
     * @param
     * @return
     */
    private Date genrateExpireDate(){

        return new Date(System.currentTimeMillis()+expiretion*1000);
    }

    /**
     * 获取设备号
     * @param device
     * @return
     */
    public String genrateDivce(Device device){

        String audience = AUDIENCE_NOKNOW;
        if(device.isMobile()){
            audience = AUDIENCE_MOBILE;
        }else if(device.isTablet()){
            audience = AUDIENCE_TABLET;
        }else if(device.isNormal()){
            audience = AUDIENCE_NORMAL;
        }
        return audience;
    }

}

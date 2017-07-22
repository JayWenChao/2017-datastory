package cn.hy.com.entity;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/7/22.
 */
public class Student implements Serializable{
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    private Integer id;
    private String  username;
    private String  passwd;
}

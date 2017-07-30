package cn.hy.com.dao;

import cn.hy.com.entity.Student;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * Created by Administrator on 2017/7/17.
 */
@Mapper
@CacheConfig(cacheNames = "student")
public interface StudentMapper {

    /**
     *
     * @return
     */
    @Select("SELECT *FROM student")
    public List<Student> selectObject();


    /**
     *
     * @param username
     * @param passwd
     * @return
     */
    @Insert("INSERT INTO student(username,passwd) values(#{username},#{passwd})")
    public Integer insertObject(@Param("username")String username,@Param("passwd") String passwd);


    /**
     *
     * @param id
     * @return
     */
    @Cacheable
    @Select("SELECT *FROM student WHERE id = #{id}")
    public Student selectSingle(@Param("id") Integer id);

    @Select("SELECT *FROM student WHERE username = #{username}")
    public Student findObject(@Param("username") String username);

}

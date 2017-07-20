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
     * @param name
     * @param passwd
     * @return
     */
    @Insert("INSERT INTO student(name,passwd) values(#{name},#{passwd})")
    public Integer insertObject(@Param("name")String name,@Param("passwd") String passwd);


    /**
     *
     * @param id
     * @return
     */
    @Cacheable
    @Select("SELECT *FROM student WHERE id = #{id}")
    public Student selectSingle(@Param("id") Integer id);
}

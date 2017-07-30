package cn.hy.com.service;

import cn.hy.com.dao.StudentMapper;
import cn.hy.com.entity.Student;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Administrator on 2017/7/17.
 */
@Service
@Transactional(timeout=3000)
public class StudentService {


    @Autowired
    private StudentMapper studentMapper;

    public List<Student> selectObject(){

        return studentMapper.selectObject();
    }


    /**
     *
     * @param username
     * @param passwd
     * @return
     */
    public boolean insertObject(String username,String passwd){

        Integer value = studentMapper.insertObject(username, passwd);
        Student st = new Student();
        value = st.getId();
        if(value > 0) return true;
        return false;
    }

    public Student selectSingle(Integer id){
        Student student = studentMapper.selectSingle(id);
        return student;
    }

    /**
     * 验证身份
     * @param username
     * @return
     */
    public Student findObject(String username){

        return studentMapper.findObject(username);
    }
}

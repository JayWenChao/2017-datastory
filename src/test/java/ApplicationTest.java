import cn.hy.com.SpringbootCloudApplication;
import cn.hy.com.entity.Student;
import cn.hy.com.service.StudentService;
import org.apache.log4j.Logger;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * 在高的版本中，测试数据@SpringApplicationConfiguration过时
 * 官方推荐使用@SpringBootTest,同时不能使用@WebAppconfiguration
 * Created by Administrator on 2017/7/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringbootCloudApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApplicationTest {

    private static final Logger logger = Logger.getLogger(ApplicationTest.class);

    @Autowired
    private TransportClient client;
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private TestRestTemplate template;
    @Autowired
    private StudentService studentService;

    @Before
    public void setMockmvc(){

       mockMvc =  MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    }


    @Test
    public void data(){
        logger.info("测试数据");
       /* List<Student> flag = studentService.selectObject();*/
       // Student fg = studentService.selectSingle(3);
        Student object = studentService.findObject("spark");
        System.out.print(object);

    }

    @Test
    public void Mock() throws Exception{
      mockMvc.perform(get("http://localhost:8091/detail/3")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk());
       // System.out.print(perform);
    }

    @Test
    public void webapp(){

        Student forObject = template.getForObject("http://localhost:8091/detail/3", Student.class);
        assertNotNull(forObject);
        assertEquals("阿秀",forObject.getUsername());
    }

    @Test
    public void ES(){

        logger.info("----------elasticsearch-----------");
        GetResponse response = client.prepareGet("bank", "account", "995").execute().actionGet();
        String string = response.getSourceAsString();
        System.out.println(string);
    }

}

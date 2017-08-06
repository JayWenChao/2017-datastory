import cn.hy.com.SpringbootCloudApplication;
import cn.hy.com.entity.Student;
import cn.hy.com.service.StudentService;
import org.apache.log4j.Logger;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequestBuilder;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.get.GetField;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.shard.ShardId;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.SearchShardTarget;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
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


import java.util.*;

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
@SpringBootTest(classes = SpringbootCloudApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
    public void setMockmvc() {

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

    }


    @Test
    public void data() {
        logger.info("测试数据");
       /* List<Student> flag = studentService.selectObject();*/
        // Student fg = studentService.selectSingle(3);
        Student object = studentService.findObject("spark");
        System.out.print(object);

    }

    @Test
    public void Mock() throws Exception {
        mockMvc.perform(get("http://localhost:8091/detail/3")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andDo(print()).andExpect(status().isOk());
        // System.out.print(perform);
    }

    @Test
    public void webapp() {

        Student forObject = template.getForObject("http://localhost:8091/detail/3", Student.class);
        assertNotNull(forObject);
        assertEquals("阿秀", forObject.getUsername());
    }

    //测试连接操作es数据
    @Test
    public void ES() {
        logger.info("----------elasticsearch-----------");
        GetResponse response = client.prepareGet("bank", "account", "995").execute().actionGet();
        String string = response.getSourceAsString();
        long l = response.getVersion();
        System.out.println("版本是: " + l);
        String index = response.getIndex();
        System.out.println("索引是: " + index);
        String type = response.getType();
        System.out.println("类型是:" + type);
        Map<String, GetField> fields = response.getFields();
        System.out.println(fields);
        Set<Map.Entry<String, GetField>> entries = fields.entrySet();
        for (Map.Entry<String, GetField> entry : entries) {

            String key = entry.getKey();
            GetField value = entry.getValue();
            System.out.println(key + "------" + value.getName());
        }
        System.out.println(string);
    }


    /**
     * 创建数据
     */
    @Test

    public void EsDocment() throws Exception {
        logger.info("----------elasticsearch-----------");

        //构建一个JSON
        XContentBuilder builder = XContentFactory.jsonBuilder().startObject()
                .field("username", "first love")
                .field("riqi", new Date())
                .field("msg", "docker spark")
                .field("age", "18")
                .endObject();
        //添加文档
        IndexResponse res = client.prepareIndex("twitter", "tweet", "4")
                .setSource(builder).get();
        ShardId shardId = res.getShardId();
        Index index = shardId.getIndex();
        String indexName = shardId.getIndexName();
        System.out.println(index + "---" + indexName);
        MultiGetResponse multiGetItemResponses = client.prepareMultiGet()
                .add("twitter", "tweet", "4")
                /*.add("twitter", "tweet", "2", "3", "4")*/
              /*  .add("bank", "account", "995")*/.get();

        for (MultiGetItemResponse multiGetItemResponse : multiGetItemResponses) {

            GetResponse rs = multiGetItemResponse.getResponse();
            if (rs != null) {

                String sourceAsString1 = rs.getSourceAsString();
                System.out.println(sourceAsString1);
            }
        }
    }


    /**
     * 构建一个bulk容器，在一次请求中添加一个或者多个实例
     * 或者删除多个实例
     * 第一个坑，这个bulk，添加文档，必须某一个id的字段存在，不然会异常出现
     * 无法写入,可能目前只能构建单个IndexResponse
     *
     * @throws Exception
     */
    @Test
    public void BulkEsSingleRquest() throws Exception {


        BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
        bulkRequestBuilder.add(client.prepareIndex("twitter", "tweet", "1").setSource(
                XContentFactory.jsonBuilder().startObject()
                        .field("user", "yudaotianguang")
                        .field("postDate", new Date())
                        .field("message", "try out Elasticsearch")
                        .endObject()
        ));
        bulkRequestBuilder.add(client.prepareIndex("twitter", "tweet", "3").setSource(

                XContentFactory.jsonBuilder()
                        .field("wocao", "薛凯琪")
                        .field("postsDate", new Date())
                        .field("messages", "周末画报")
                        .endObject()
        ));
        BulkResponse bulkItemResponses = bulkRequestBuilder.get();
        if (bulkItemResponses.hasFailures()) {

            String message = bulkItemResponses.buildFailureMessage();
            System.out.println("构建失败消息:" + message);
            Iterator<BulkItemResponse> iterator = bulkItemResponses.iterator();
            while (iterator.hasNext()) {

                BulkItemResponse next = iterator.next();
                DocWriteResponse response = next.getResponse();
                ShardId shardId = response.getShardId();
                System.out.println("获取分片:" + shardId);
                long version = next.getVersion();
                System.out.println("版本: " + version);
                int itemId = next.getItemId();
                System.out.println("项目id;" + itemId);
            }
        }

        MultiGetRequestBuilder multiGetRequestBuilder = client.prepareMultiGet();
        MultiGetResponse multiGetItemResponses = multiGetRequestBuilder.add("twitter", "tweet", "1")
                .add("twitter", "tweet", "2", "3", "4")
                .add("bank", "account", "995").get();
        for (MultiGetItemResponse ms : multiGetItemResponses) {

            GetResponse response = ms.getResponse();
            if (response != null) {

                Map<String, GetField> fields = response.getFields();
                Set<Map.Entry<String, GetField>> entries = fields.entrySet();
                for (Map.Entry<String, GetField> entry : entries) {

                    String key = entry.getKey();
                    GetField value = entry.getValue();
                    Iterator<Object> iterator = value.iterator();
                    while (iterator.hasNext()) {
                        Object next = iterator.next();
                        System.out.println(next.getClass().getName());
                        System.out.println(next);
                    }
                }
                System.out.println("数据:-------------------");
                System.out.println(response.getSourceAsString());
            }
        }

    }


    /**
     * 匹配查询
     */
    @Test
    public void EsQueryBuilder() {

        //匹配查询
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("user", "mrchao");
        SearchResponse searchResponse = client.prepareSearch("twitter")
                .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
                .setScroll(new TimeValue(60000))
                .setQuery(termQueryBuilder)
                .setSize(100).get();
        do {
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                //Handle the hit...
                float score = hit.getScore();
                System.out.println("得分：" + score);
                String sourceAsString = hit.getSourceAsString();
                System.out.println(sourceAsString);
                SearchShardTarget shard = hit.getShard();
                System.out.println("分片:" + shard.getShardId().getIndexName());
            }

            searchResponse = client.prepareSearchScroll(searchResponse.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
        } while (searchResponse.getHits().getHits().length != 0);

    }

    /**
     * 可以多索引多类型匹配查询
     */
    @Test
    public void EsSearch() {

        SearchResponse response = client.prepareSearch("twitter")
                .setTypes("tweet")
                .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
                .setQuery(QueryBuilders.termQuery("msg", "spark"))
                .setPostFilter(QueryBuilders.rangeQuery("age").from(19).to(20))
                .setFrom(0).setSize(60)
                .setScroll(new TimeValue(60000))
                .setExplain(true)
                .get();
        long totalHits = response.getHits().getTotalHits();
        System.out.println("总共命中:" + totalHits);
        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit sh : hits) {

            String sourceAsString = sh.getSourceAsString();
            Map<String, Object> source = sh.getSource();
            System.out.println(sourceAsString);
            System.out.println(source);

        }
    }

    /**
     * 多次请求集合一次发
     */
    @Test
    public void MultiSearch() {
        logger.info("multi request search");
        SearchRequestBuilder es1 = client.prepareSearch().setQuery(QueryBuilders.queryStringQuery("elasticsearch")).setSize(1);
        SearchRequestBuilder es2 = client.prepareSearch().setQuery(QueryBuilders.termQuery("msg", "spark")).setSize(2);
        MultiSearchResponse items = client.prepareMultiSearch().add(es1).add(es2).get();
        MultiSearchResponse.Item[] responses = items.getResponses();
        for (MultiSearchResponse.Item item : responses) {
            SearchResponse response = item.getResponse();
            if (response != null) {
                SearchHit[] hits = response.getHits().getHits();
                for (SearchHit hit : hits) {
                    String sourceAsString = hit.getSourceAsString();
                    System.out.println(sourceAsString);
                }
            }

        }
    }


    /**
     * 聚合查询
     */
    @Test
    public void EsAggregation() {

        SearchResponse searchResponse = client.prepareSearch()
                .setQuery(QueryBuilders.matchAllQuery())
                //   .addAggregation(AggregationBuilders.terms("msg").field("spark"))
                .addAggregation(AggregationBuilders.dateHistogram("agg2")
                        .field("postDate").dateHistogramInterval(DateHistogramInterval.YEAR)).get();
        Aggregation msg = searchResponse.getAggregations().get("msg");
        searchResponse.getAggregations().get("agg2");
        List<Aggregation> aggregations = searchResponse.getAggregations().asList();
        for (Aggregation agg : aggregations) {

            Map<String, Object> metaData = agg.getMetaData();
            if (metaData != null) {

                Set<Map.Entry<String, Object>> entries = metaData.entrySet();
                Iterator<Map.Entry<String, Object>> iterator = entries.iterator();
                while (iterator.hasNext()) {

                    Map.Entry<String, Object> next = iterator.next();
                    String key = next.getKey();
                    Object value = next.getValue();
                    System.out.println(key + "---" + value + "-------" + value.getClass().getSimpleName());
                }
            }

        }
        System.out.println(aggregations);
     /*   Map<String, Object> metaData = msg.getMetaData();
        for(Map.Entry<String,Object> entry :metaData.entrySet()){

            String key = entry.getKey();
            Object value = entry.getValue();
            System.out.println(key+"----"+value+"----"+value.getClass().getSimpleName());
            System.out.println(metaData);
        }*/
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {

            String sourceAsString = hit.getSourceAsString();
            System.out.println(sourceAsString);


            //searchResponse.getAggregations().
            //
        }
    }


    @Test
    public void MaxMumEs() {

        SearchResponse response = client.prepareSearch("twitter")
                .setTerminateAfter(1000)
                .get();

        if (response.isTerminatedEarly()) {

            Aggregations aggregations = response.getAggregations();
            List<Aggregation> aggregations1 = aggregations.asList();
            if (aggregations1 != null) {

                for (Aggregation agg : aggregations1) {

                    Map<String, Object> metaData = agg.getMetaData();
                    if (metaData != null) {

                        for (Map.Entry<String, Object> entry : metaData.entrySet()) {

                            System.out.println(entry.getKey() + "----" + entry.getValue());
                        }
                    }
                }
            }
            String scrollId = response.getScrollId();
            System.out.println(scrollId);
            long totalHits = response.getHits().getTotalHits();
            System.out.println("sumcount:"+totalHits);
            SearchHit[] hits = response.getHits().getHits();
            for(SearchHit sh : hits){

                Map<String, SearchHitField> fields = sh.getFields();
                if (fields != null){

                    for(Map.Entry<String,SearchHitField> entry : fields.entrySet()){

                        System.out.println(entry.getKey()+"---"+entry.getValue());
                    }
                }
                String nodeId = sh.getShard().getNodeId();
                System.out.println(nodeId);
                System.out.println(sh.getSourceAsString());

            }

        }
    }

    /**
     * ES中query-bool查询使用must匹配match多个字段，是且的意思
     * 使用should是或的意思
     * 而使用must_not就是两个都不
     * 例如
     * curl -XPOST 'localhost:9200/bank/_search?pretty' -d'
     * {
     *     "query":{
     *         "bool":{
     *             "must":[
     *                 {"match":{"address":"Avenue"}},
     *                 {"match":{"address":"Street"}}
     *             ]
     *         }
     *     }
     * }'
     * 命中null
     * 而把must改成should就可以命中两个doc
     *
     */

}

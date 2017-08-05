package cn.hy.com.es;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**因为springboot目前支持elsticsearch2.x和1.x版本
 * 所以使用自带的springboot-data-elasticsearch不行
 * 可以去官网配置Maven依赖
 * elasticsearch5.x版本
 */
@Configuration
@Component
public class ElasticsearchConfiguration implements FactoryBean<TransportClient>, InitializingBean, DisposableBean {

    private static final Logger logger = Logger.getLogger(ElasticsearchConfiguration.class);


    @Value(value = "${spring.data.elasticsearch.cluster-nodes}")
    private String cluternodes;
    @Value(value = "${spring.data.elasticsearch.cluster-name}")
    private String clustername;
    //传输客户端连接
    private TransportClient client;
    //是否嗅探整个集群
    private Boolean clientTrantsportSniff = true;
    //ping 连接超时时间设定
    private String clientPingTimeout = "5s";
    //时间间隔
    private String clientNodesSamplerInterval = "5s";

    @Override
    public void destroy() throws Exception {

        logger.info("closing elasticsearch client");
        try {
            if (client != null) {
                client.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    @Override
    public TransportClient getObject() throws Exception {
        return this.client;
    }

    @Override
    public Class<TransportClient> getObjectType() {
        return TransportClient.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        buildClient();
    }

    protected void buildClient() {

        client = new PreBuiltTransportClient(settings());
        if(StringUtils.isNotEmpty(cluternodes)){

            for(String clusternode : cluternodes.split(",")){

                String[] InetSocket = clusternode.split(":");
                String Address = InetSocket[0];
                Integer port = Integer.valueOf(InetSocket[1]);
                try {
                    client.addTransportAddress(new
                            InetSocketTransportAddress(InetAddress.getByName(Address),port ));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    logger.error(e.getMessage());
                }
            }
            System.out.println("测试elasticsearch连接");
        }
    }

    /**
     * 设置环境
     *
     * @return
     */
    private Settings settings() {
        //环境设置
        Settings settings = Settings.builder().put("cluster.name", clustername)
               .put("client.transport.sniff", clientTrantsportSniff)
                .put("client.transport.ping_timeout", clientPingTimeout)
                .put("client.transport.nodes_sampler_interval", clientNodesSamplerInterval)
                .build();
        return settings;


    }

}

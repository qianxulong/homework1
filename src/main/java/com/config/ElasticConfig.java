package com.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
public class ElasticConfig {

    @Value("${es.host}")
    public String host;
    @Value("${es.port}")
    public int port;
    @Value("${es.scheme}")
    public String scheme;

    @Bean
    public RestHighLevelClient restHighLevelClient() {
        HttpHost httpHost = new HttpHost(host, port, scheme);
        RestClientBuilder restClientBuilder = RestClient.builder(httpHost);
        return new RestHighLevelClient(restClientBuilder);
    }
}

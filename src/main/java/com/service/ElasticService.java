
package com.service;

import com.pojo.Book;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;


@Slf4j
@Service
public class ElasticService {
    @Autowired
    RestHighLevelClient restHighLevelClient;

    private String indexName = "book";


    public boolean createIndex() throws Exception {
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        request.settings(Settings.builder()
                .put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 2));

        Map<String, Object> title = new HashMap<>();
        title.put("type", "text");
        Map<String, Object> sub_title = new HashMap<>();
        sub_title.put("type", "text");
        Map<String, Object> price = new HashMap<>();
        price.put("type", "integer");
        Map<String, Object> properties = new HashMap<>();
        properties.put("title", title);
        properties.put("sub_title", sub_title);
        properties.put("price", price);
        Map<String, Object> mapping = new HashMap<>();
        mapping.put("properties", properties);
        request.mapping(mapping);
        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        return createIndexResponse.isAcknowledged();
    }

    public boolean existIndex() throws Exception {
        GetIndexRequest request = new GetIndexRequest(indexName);
        request.local(false);
        request.humanReadable(true);
        request.includeDefaults(false);
        return restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
    }

    public boolean deleteIndex() throws Exception {
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        AcknowledgedResponse acknowledgedResponse
                = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
        return acknowledgedResponse.isAcknowledged();
    }

    public boolean saveOrUpdate(Book book) throws Exception {
        IndexRequest request = new IndexRequest(indexName);
        request.id(book.getId() + "");
        request.source(JSONObject.toJSONString(book), XContentType.JSON);
        IndexResponse indexResponse =
                restHighLevelClient.index(request, RequestOptions.DEFAULT);
        if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
            System.out.println("created OK");
            return true;
        }
        if (indexResponse.getResult() == DocWriteResponse.Result.UPDATED) {
            System.out.println("update OK");
            return true;
        }
        return false;
    }

    public boolean deleteById(Integer id) throws Exception {
        DeleteRequest request = new DeleteRequest(indexName, id.toString());
        DeleteResponse deleteResponse =
                restHighLevelClient.delete(request, RequestOptions.DEFAULT);
        if (deleteResponse.getResult() == DocWriteResponse.Result.NOT_FOUND) {
            System.out.println("NOT_FOUND");
            return false;
        }
        return true;
    }

    private List<Book> processHits(QueryBuilder queryBuilder) throws Exception {
        //整个请求
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //queryBuilder   query设置内容
        searchSourceBuilder.query(queryBuilder);
        return processHits(searchSourceBuilder);

    }

    private List<Book> processHits(SearchSourceBuilder searchSourceBuilder) throws Exception {
        log.info("searchSourceBuilder = " + searchSourceBuilder);
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.source(searchSourceBuilder);
        //
        SearchResponse searchResponse =
                restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHits hits = searchResponse.getHits();
        System.out.println("总数 = " + hits.getTotalHits());
        SearchHit[] searchHits = hits.getHits();
        List<Book> items = new ArrayList<>();
        for (SearchHit hit : searchHits) {
            String sourceAsString = hit.getSourceAsString();
            Book book = JSONObject.parseObject(sourceAsString, Book.class);
            items.add(book);
        }
        return items;
    }

    public List<Book> queryAll() throws Exception {
        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        return processHits(matchAllQueryBuilder);
    }

    public List<Book> queryMultiMatch(String search) throws Exception {
        MultiMatchQueryBuilder queryBuilder =
                QueryBuilders.multiMatchQuery(search, "title", "sub_title");
        return processHits(queryBuilder);
    }
}
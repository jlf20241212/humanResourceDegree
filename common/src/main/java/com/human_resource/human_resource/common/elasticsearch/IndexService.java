package com.human_resource.human_resource.common.elasticsearch;

import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONException;
import org.springframework.core.ResolvableType;

import java.io.IOException;
import java.util.List;
import org.elasticsearch.client.indices.PutMappingRequest;

import com.human_resource.human_resource.common.elasticsearch.EsService;

public interface IndexService {

    public boolean indexData(List<Long> ids, String table) throws JSONException, IOException;

    public List<String> subscribeTable();

    public String mainTable();

    public List<Long> scanMainDBIds(long offsetId, int size);

    public String indexName();

    public PutMappingRequest getIndexMapping();
}

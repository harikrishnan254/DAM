package com.qa.aem.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

/**
 * Response of /bin/querybuilder.json, used for deserialization.
 * Each hit is a map, for example {"jcr:path": "/content/dam/.../image.jpg"}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueryBuilderResponse {

    private boolean success;
    private int total;
    private List<Map<String, Object>> hits;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Map<String, Object>> getHits() {
        return hits;
    }

    public void setHits(List<Map<String, Object>> hits) {
        this.hits = hits;
    }
}

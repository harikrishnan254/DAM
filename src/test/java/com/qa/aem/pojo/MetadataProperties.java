package com.qa.aem.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The "properties" part of a metadata update. The dc: fields are Dublin Core metadata.
 */
public class MetadataProperties {

    @JsonProperty("dc:title")
    private String title;

    @JsonProperty("dc:description")
    private String description;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

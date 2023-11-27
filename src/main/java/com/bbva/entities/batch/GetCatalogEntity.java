package com.bbva.entities.batch;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class GetCatalogEntity {
    @SerializedName("catalog_id")
    private int catalogId;
    @SerializedName("element_id")
    private int elementId;
    @SerializedName("element_name")
    private String elementName;
    @SerializedName("element_desc")
    private String elementDescription;
    @SerializedName("status_type")
    private int statusType;
    @SerializedName("parent_catalog_id")
    private int parentCatalogId;
    @SerializedName("parent_element_id")
    private int parentElementId;
}

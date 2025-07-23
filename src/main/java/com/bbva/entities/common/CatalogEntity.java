package com.bbva.entities.common;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CatalogEntity {

    @SerializedName("catalog_id")
    private int catalogId;
    @SerializedName("element_id")
    private Integer elementId;
    @SerializedName("element_name")
    private String elementName;
    @SerializedName("element_desc")
    private String elementDesc;
    @SerializedName("status_type")
    private int statusType;
}

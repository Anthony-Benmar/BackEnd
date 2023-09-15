package com.bbva.entities.template;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TemplateEntity {
    @SerializedName("id")
    private Integer id;
    @SerializedName("processCode")
    private String processCode;
    @SerializedName("name")
    private String name;
    @SerializedName("description")
    private String description;
    @SerializedName("time")
    private Integer time;
}

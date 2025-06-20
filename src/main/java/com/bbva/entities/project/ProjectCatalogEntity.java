package com.bbva.entities.project;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ProjectCatalogEntity implements Serializable {

    @SerializedName("sdatool_id")
    private String sdatoolId;

    @SerializedName("project_name")
    private String projectName;

    private String sn1;

    @SerializedName("sn1_desc")
    private String sn1Desc;

    private String sn2;

    @SerializedName("sn2_projectId")
    private String sn2ProjectId;

    @SerializedName("codigo_5_digitos")
    private String codigo5Digitos;
}

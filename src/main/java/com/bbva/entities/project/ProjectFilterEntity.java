package com.bbva.entities.project;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectFilterEntity {
    private String project_id;
    private String project_name;
    private String sdatool_id;
    private String status_type;
    private int records_count;
}
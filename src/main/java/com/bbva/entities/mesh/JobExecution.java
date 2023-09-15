package com.bbva.entities.mesh;

import com.google.gson.annotations.SerializedName;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
public class JobExecution {
    public Integer id;
    public Integer job_id;
    public Integer father_job_id;
    public String lookup_job_name;
    public String job_name;
    public String json_name;
    public String folder;
    public String application;
    public String sub_application;
    public String order_date;
    public String frequency;
    public String job_type;
    public String start_time;
    public String host;
    public String run_as;
    public String execution_date;
    public String status;
}

package com.bbva.entities.government;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class SourceDefinitionEntity {
    private Integer uc_source_id;
    private int use_case_id;
    private String uc_source_name;
    private String uc_source_desc;
    private int source_id;
    private int uc_source_type;
    private int granularity_type;
    private String element_name;
    private String contact_name;
    private String status_type;
    private String user_comment_desc;
    private String uc_frequency_type;
    private String depth_month_number;
    private String ans_desc;
    private int priority_number;
    private int operation_user;
    private Date operation_date;
    private int system_owner_id;
    private List<SourceConceptEntity> sourceConceptEntityList;
    private int project_id;
    private String project_name;
    private String portafolio_code;

}

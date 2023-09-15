package com.bbva.entities.government;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SourceConceptEntity {
    private Integer uc_data_id;
    private String uc_data_code;
    private int uc_source_id;
    private String uc_data_group_desc;
    private String uc_data_func_name;
    private String uc_data_desc;
    private String uc_data_example;
    private String physical_name;
    private int status_type;
    private String user_comment_desc;
    private int operation_user;
    private Date operation_date;
    private int relevant_field_bool;
    private int cci_field_bool;
    private int field_type;
    private String element_name;
    private int granularity_data_type;
    private String field_domain_desc;
    private String field_subdomain_desc;
    private int data_owner_id;
    private int ownership_id;

}

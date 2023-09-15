package com.bbva.dto.government.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsertConceptRequestDTO {
    private Integer uc_data_id;
    private String uc_data_code;
    private Integer uc_source_id;
    private String uc_data_group_desc;
    private String uc_data_func_name;
    private String uc_data_desc;
    private String uc_data_example;
    private String physical_name;
    private Integer status_type;
    private String user_comment_desc;
    private Integer operation_user;
    private Integer relevant_field_bool;
    private Integer cci_field_bool;
    private Integer field_type;
    private Integer granularity_data_type;
    private String field_domain_desc;
    private String field_subdomain_desc;
    private Integer data_owner_id;
    private Integer ownership_id;
}

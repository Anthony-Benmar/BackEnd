package com.bbva.dto.government.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSourceRequestDTO {
    private Integer uc_source_id;
    private Integer project_id;
    private String uc_source_name;
    private String uc_source_desc;
    private Integer source_id;
    private String depth_month_number;
    private String uc_frequency_type;
    private Integer status_type;
    private Integer operation_user;
    private Integer uc_source_type;
    private String ans_desc;
    private Integer system_owner_id;
    private Integer priority_number;
}

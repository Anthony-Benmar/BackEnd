package com.bbva.entities.buc;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class BucPagedFilteredEntity {
    private String records_amount;
    private Integer page;
    private String folio_code;
    private Integer priority;
    private String buc_id;
    private String folio_id;
    private String project_id;
    private String sdatool_id;
    private String project_name;
    private String uc_data_id;
    private String uc_data_code;
    private String uc_data_func_name;
    private String status_buc_type;
    private String element_name;
    private String buc_fields_id;
    private String dictamen_field_name;
    private String old_source_id;
    private String source_name;
    private String records_count;
}
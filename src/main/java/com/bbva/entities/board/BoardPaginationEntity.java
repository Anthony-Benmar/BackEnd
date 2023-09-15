package com.bbva.entities.board;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardPaginationEntity {
    private String source_id;
    private String debt_source_id;
    private String old_source_id;
    private String project_id;
    private String sdatool_id;
    private String project_name;
    private String origin_source_name;
    private String status_type;
    private String status;
    private String debtstatus_source_type;
    private String debtstatus_source;
    private String tipology_type;
    private String tipology;
    private String raw_table_name;
    private String uuaa_debt_type;
    private String master_path_desc;
    private String summ_debt_t;
    private String records_count;

}
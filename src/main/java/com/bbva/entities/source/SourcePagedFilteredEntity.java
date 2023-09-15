package com.bbva.entities.source;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SourcePagedFilteredEntity {
    private String records_amount;
    private String page;
    private String source_id;
    private String old_source_id;
    private String source_name;
    private String source_desc;
    private String status_type;
    private String estatus;
    private String origin_source_type;
    private String origin_source;
    private String master_func_map_id;
    private String master_uuaa;
    private String debtstatus_source_type;
    private String debtstatus_source;
    private String records_count;
}
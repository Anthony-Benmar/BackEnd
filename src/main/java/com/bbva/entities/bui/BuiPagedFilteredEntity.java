package com.bbva.entities.bui;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuiPagedFilteredEntity {
    private String records_amount;
    private String page;
    private String bui_id;
    private String source_id;
    private String old_source_id;
    private String uc_source_name;
    private String folio_id;
    private String folio_code;
    private String project_id;
    private String sdatool_id;
    private String project_name;
    private String resolution_source_type;
    private String resolution_source_type_desc;
    private String ingest_source_type;
    private String ingest_source_type_desc;
    private String status_folio_type;
    private String status_folio_type_desc;
    private String records_count;
}
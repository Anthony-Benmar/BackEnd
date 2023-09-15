package com.bbva.entities.buc;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BucmapEntity {
    private String folio_code;
    private String sdatool;
    private String priority;
    private String field_code;
    private String functional_data;
    private String functional_description;
    private String resolution_state;
    private String resolution;
    private String resolution_comment;
    private String logic_description;
    private String source_id;
    private String old_source_id;
    private String source_name;
    private String dictamen_field_name;
}
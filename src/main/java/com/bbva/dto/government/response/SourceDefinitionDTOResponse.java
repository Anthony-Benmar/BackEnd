package com.bbva.dto.government.response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class SourceDefinitionDTOResponse {
    private int uc_source_id;
    private int use_case_id;
    private String uc_source_name;
    private String uc_source_desc;
    private int uc_source_type;
    private String element_name;
    private String uc_frequency_type;
    private String depth_month_number;
    private String ans_desc;
    private int priority_number;
}

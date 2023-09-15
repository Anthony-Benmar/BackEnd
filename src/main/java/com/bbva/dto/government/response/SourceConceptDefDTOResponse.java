package com.bbva.dto.government.response;
import com.bbva.entities.government.SourceConceptEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SourceConceptDefDTOResponse {
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
    private int project_id;
    private String project_name;
    private String portafolio_code;
    private int system_owner_id;
    private List<SourceConceptEntity> sourceConceptEntityList;
}

package com.bbva.entities.use_case_definition;

import com.bbva.entities.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
@Data
public class UseCaseDefinitionEntity extends BaseEntity {
    private Integer useCaseId;
    private Integer projectId;
    private String stsLink;
    private String msaLink;
}

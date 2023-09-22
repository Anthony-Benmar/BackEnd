package com.bbva.entities.dictionary;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class GenerationEntity {
    
    private Integer generationId;
    private String dictumPhysicalFileName;
    private Integer projectId;
    private String sourceId;
    private String sourceName;
    private Date generationDate;
    private Date generationCompleteDate;
    private String status;
    private Integer dictumTemplateId;
    private Integer dictionaryTemplateId;
    private String employeeId;
    private String dictumLogicalFileName;
    private String dictionaryPhysicalFileName;
    private String dictionaryLogicalFileName;
    private String projectName;
    private Date inactiveDate;
    private byte[] dictionaryFile;

}

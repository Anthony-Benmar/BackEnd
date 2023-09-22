package com.bbva.dto.dictionary.parameter;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenerationSearchParameter {
    
    private Integer projectId;
    private String sourceId;
    private String sourceName;
    private Date startDate;
    private Date endingDate;
    private String employeeId;

}

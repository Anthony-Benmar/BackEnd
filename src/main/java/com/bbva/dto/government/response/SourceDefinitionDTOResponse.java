package com.bbva.dto.government.response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class SourceDefinitionDTOResponse {
    private int ucSourceId;
    private int useCaseId;
    private String ucSourceName;
    private String ucSourceDesc;
    private int ucSourceType;
    private String elementName;
    private String ucFrequencyType;
    private String depthMonthNumber;
    private String ansDesc;
    private int priorityNumber;
    private Integer recordsCount;
}

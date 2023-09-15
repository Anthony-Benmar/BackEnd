package com.bbva.dto.dictionary.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GenerationSearchRequest {
    
    private Integer projectId;
    private String sourceId;
    private String sourceName;
    private String startDate;
    private String endingDate;
    private Boolean ownerRecordOnly;

}

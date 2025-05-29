package com.bbva.dto.reliability.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PendingCustodyJobsDtoResponse {
    private String jobName;
    private String jsonName;
    private String frequencyId;
    private String jobTypeId;
    private String originTypeId;
    private String phaseTypeId;
    private String principalJob;
}

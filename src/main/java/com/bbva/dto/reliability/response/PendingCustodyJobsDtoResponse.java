package com.bbva.dto.reliability.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PendingCustodyJobsDtoResponse {
    private String jobName;
    private String jsonName;
    private String frequency;
    private String jobType;
    private String originType;
    private String phaseType;
}

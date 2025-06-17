package com.bbva.dto.reliability.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class ReliabilityPacksDtoResponse {
    private String pack;
    private Integer domainId;
    private String domainName;
    private Integer productOwnerUserId;
    private Integer useCaseId;
    private String useCase;
    private Integer projectId;
    private String sdaToolId;
    private Integer creatorUserId;
    private String pdfLink;
    private Integer jobCount;
}

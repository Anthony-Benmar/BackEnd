package com.bbva.dto.reliability.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TransferInputDtoRequest {
    private String pack;
    private Integer domainId;
    private Integer productOwnerUserId;
    private Integer useCaseId;
    private Integer projectId;
    private Integer creatorUserId;
    private String pdfLink;
    private Integer jobCount;
    private Integer statusId;
    private Integer sn2;
    private List<JobTransferInputDtoRequest> transferInputDtoRequests;
}

package com.bbva.dto.reliability.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReliabilityPacksDtoResponse {
    private String pack;
    private Integer domainId;
    private String domainName;
    private String  productOwnerEmail;
    private Integer useCaseId;
    private String useCase;
    private Integer projectId;
    private String sdaToolId;
    private String creatorUser;
    private String pdfLink;
    private Integer jobCount;
    private Integer statusId;
    private String  statusName;
    private Integer cambiedit;
    private String  createdAt;
}

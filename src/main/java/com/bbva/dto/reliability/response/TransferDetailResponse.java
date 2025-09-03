package com.bbva.dto.reliability.response;

import lombok.*;
import java.util.List;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class TransferDetailResponse {
    private Header header;
    private List<JobRow> jobs;

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class Header {
        private String  pack;
        private String  sdaToolId;
        private Integer domainId;
        private String  domainName;
        private Integer useCaseId;
        private String  useCase;
        private Integer statusId;
        private String  statusName;
        private String  comments;
        private String  creatorEmail;
        private String  pdfLink;
        private Integer sn2Id;
        private String  sn2Desc;
        private String  productOwnerEmail;
    }

    @Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
    public static class JobRow {
        private String  jobName;
        private String  jsonName;
        private Integer frequencyId;
        private Integer jobTypeId;
        private Integer jobPhaseId;
        private Integer originTypeId;
        private String  inputPaths;
        private String  outputPath;
        private String  bitBucketUrl;
        private String  responsible;
        private Integer useCaseId;
        private Integer domainId;
        private String  isCritical;
        private Integer statusId;
        private String  comments;
    }
}
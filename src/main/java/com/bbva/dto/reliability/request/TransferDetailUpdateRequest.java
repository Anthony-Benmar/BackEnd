package com.bbva.dto.reliability.request;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TransferDetailUpdateRequest {
    private Header header;
    private List<Job> jobs;

    @Getter @Setter
    public static class Header {
        private Integer domainId;
        private Integer useCaseId;
        private String  comments;
    }

    @Getter @Setter
    public static class Job {
        private String  jobName;
        private String  componentName;
        private Integer frequencyId;
        private String  inputPaths;
        private String  outputPath;
        private Integer jobTypeId;
        private Integer useCaseId;
        private String  isCritical;
        private Integer domainId;
        private String  bitBucketUrl;
        private String  responsible;
        private Integer jobPhaseId;
        private Integer originTypeId;
        private String  exception;
        private String  comments;
        private String aplicativoSip;
        private String details;
    }
}

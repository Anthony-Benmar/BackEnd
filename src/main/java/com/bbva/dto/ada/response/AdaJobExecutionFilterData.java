package com.bbva.dto.ada.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
public class AdaJobExecutionFilterData {
    private String jobName;
    private String parentFolder;
    private String orderDate;
    private String orderIdCCR;
    private String orderIdADA;
    private String statusCCR;
    private String statusADA;
    private String serverExecution;
    private String serviceOwner;
    private String isTransferred;
    private String jobType;
    private String frequency;
    private Integer recordsCount;
}

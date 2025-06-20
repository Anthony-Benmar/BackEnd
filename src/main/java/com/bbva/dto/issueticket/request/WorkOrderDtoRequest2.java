package com.bbva.dto.issueticket.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WorkOrderDtoRequest2 {
    private int workOrderId;
    private String feature;
    private Integer jiraProjectId;
    private String jiraProjectName;
    private String folio;
    private int boardId;
    private int projectId;
    private String sourceId;
    private String sourceName;
    private int flowType;
    private String faseId;
    private String sprintEst;
    private String registerUserId;
    private String username;
    private String token;
    private Long expireTokenDate;
    private List<WorkOrderDetailDtoRequest> workOrderDetail;
    private List<String> labels;
    private String e2e;
    private List<String> period;
}

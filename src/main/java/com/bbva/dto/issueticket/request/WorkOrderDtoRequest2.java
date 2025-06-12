package com.bbva.dto.issueticket.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WorkOrderDtoRequest2 {
    public int workOrderId;
    public String feature; //Será el nombre compuesto
    public Integer jiraProjectId;
    public String jiraProjectName;
    public String folio;
    public int boardId;
    public int projectId;
    public String sourceId;
    public String sourceName;
    public int flowType;
    public Integer faseId; //---
    public Integer sprintEst; //---
    public String registerUserId;
    public String username;
    public String token;
    public Long expireTokenDate;
    public List<WorkOrderDetailDtoRequest> workOrderDetail;
}

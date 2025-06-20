package com.bbva.dto.issueticket.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WorkOrderDtoRequest2 {
    public int workOrderId;
    public String feature; // Será el nombre compuesto o summary?
    public Integer jiraProjectId; // Código del Project PAD3, ETC..
    public String jiraProjectName; // Key del proyecto PAD 3 - DEDATIOCL3
    public String folio;
    public int boardId;
    public int projectId;
    public String sourceId;
    public String sourceName;
    public int flowType;

    public String faseId; //--- String también, para ya no hacer la consulta a BD
    public String sprintEst; //--- String

    public String registerUserId;
    public String username;
    public String token;
    public Long expireTokenDate;
    public List<WorkOrderDetailDtoRequest> workOrderDetail;
    public List<String> labels;
    public String e2e;
    //Revisar los campos
    // public String featureName; // Nombre dinámico desde front
    // public String priority; // High/Medium/Low (opcional, default: Medium) -- Trbajar desde el back
    // public String assignee; // Usuario asignado - revisarr
}

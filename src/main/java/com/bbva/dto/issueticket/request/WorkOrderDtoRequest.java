package com.bbva.dto.issueticket.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class WorkOrderDtoRequest {
    public int workOrderId;
    public String workOrderCode;
    public String folio;
    public int boardId;
    public int projectId;
    public String sourceId;
    public String sourceName;
    public int flowType;
    public String registerUserId;
    public String token;
    public Long expireTokenDate;
    public List<WorkOrderDetailDtoRequest> workOrderDetail;
}

package com.bbva.dto.issueticket.request;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

@Getter
@Setter
public class WorkOrderDetailDtoRequest {
    public int workOrderDetailId;
    public int templateId;
    @Nullable
    public String issueCode;
}

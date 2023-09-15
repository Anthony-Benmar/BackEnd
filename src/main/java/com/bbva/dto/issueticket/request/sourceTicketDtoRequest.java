package com.bbva.dto.issueticket.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class sourceTicketDtoRequest {
    public Integer records_amount;
    public int page;
    public int workOrderId;
    public int projectId;
    public int type;
}

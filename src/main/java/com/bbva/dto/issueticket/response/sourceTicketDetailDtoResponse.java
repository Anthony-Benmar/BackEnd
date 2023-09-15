package com.bbva.dto.issueticket.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
@Getter
@Setter
public class sourceTicketDetailDtoResponse {
    public Integer workOrderId;
    public Integer typeId;
    public String typeDesc;
}

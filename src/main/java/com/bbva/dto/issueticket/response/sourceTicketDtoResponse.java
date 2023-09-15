package com.bbva.dto.issueticket.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class sourceTicketDtoResponse {
    public int count;
    public int pages_amount;
    public List<sourceTicketGroupByDtoResponse> data;
}

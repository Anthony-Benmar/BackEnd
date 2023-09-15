package com.bbva.dto.issueticket.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class issueTicketDetailDtoResponse {
    public int workOrderId;
    public int workOrderDetailId;
    public int templateId;
    public String template;
    public int boardId;
    public int typeId;
    public String typeDesc;
    @Nullable
    public String boardName;
    @Nullable
    public String old_source_id;
    @Nullable
    public List<issueJiraDtoResponse> issueTickets;
    public String labelOne;
    public String folio_code;
    public String source;
    public String feature;
    public String projectName;
    public int projectId;
    public int orden;
}

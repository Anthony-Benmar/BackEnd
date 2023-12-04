package com.bbva.dto.issueticket.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class sourceTicketGroupByDtoResponse {
    public String folioCode;
    @Nullable
    public String oldSourceId;
    public String source;
    public Integer boardId;
    @Nullable
    public String boardName;
    public String feature;
    @Nullable
    public Date registerDate;
    public List<sourceTicketDetailDtoResponse> sourceTicketDetail;
}

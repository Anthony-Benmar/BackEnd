package com.bbva.dto.issueticket.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class sourceTicketGroupByDtoResponse {
    public Integer id;
    public String folioCode;
    @Nullable
    public String oldSourceId;
    public String source;
    public Integer boardId;
    @Nullable
    public String boardName;
    public String feature;
    public List<sourceTicketDetailDtoResponse> sourceTicketDetail;
}

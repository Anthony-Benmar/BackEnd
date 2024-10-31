package com.bbva.dto.jira.response;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JiraValidatorListDtoResponse {
    public int count;
    public int pages_amount;
    public List<JiraValidateDtoResponse> data;
}

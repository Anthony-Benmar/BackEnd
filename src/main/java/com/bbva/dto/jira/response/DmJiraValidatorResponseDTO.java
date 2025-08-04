package com.bbva.dto.jira.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DmJiraValidatorResponseDTO {

    private List<DmJiraValidatorMessageDTO> data;

    private int successCount;
    private int warningCount;
    private int errorCount;

}
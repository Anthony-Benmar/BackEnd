package com.bbva.dto.jira.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JiraResponseDTO {
    private List<JiraMessageResponseDTO> data;
    private int successCount;
    private int warningCount;
    private int errorCount;

}

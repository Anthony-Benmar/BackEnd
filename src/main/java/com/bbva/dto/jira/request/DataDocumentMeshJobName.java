package com.bbva.dto.jira.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DataDocumentMeshJobName {
    private String jobName;
    private String state;
}

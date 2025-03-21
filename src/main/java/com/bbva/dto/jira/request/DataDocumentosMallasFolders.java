package com.bbva.dto.jira.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DataDocumentosMallasFolders {
    private String folder;
    private String xml;
    private List<DataDocumentosMallasJobName> jobnames;
}

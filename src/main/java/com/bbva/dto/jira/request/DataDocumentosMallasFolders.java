package com.bbva.dto.jira.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DataDocumentosMallasFolders {
    private String folder;
    private String xml;
    private List<DataDocumentosMallasJobName> jobnames;
}

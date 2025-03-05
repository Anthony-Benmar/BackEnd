package com.bbva.dto.jira.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DataDocumentosMallas {
    private List<DataDocumentosMallasFolders> folders;
}

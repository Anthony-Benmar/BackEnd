package com.bbva.dto.project.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsertProjectDocumentDTO {
    private Integer documentId;
    private Integer projectId;
    private Integer documentType;
    private String documentUrl;
    private String createAuditUser;
}

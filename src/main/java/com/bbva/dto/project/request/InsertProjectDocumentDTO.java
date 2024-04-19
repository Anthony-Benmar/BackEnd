package com.bbva.dto.project.request;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

@Getter
@Setter
public class InsertProjectDocumentDTO {
    public Integer documentId;
    public Integer projectId;
    public Integer documentType;
    public String documentUrl;
    @Nullable
    public String createAuditUser;
}

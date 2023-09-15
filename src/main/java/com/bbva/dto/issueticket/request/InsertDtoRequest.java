package com.bbva.dto.issueticket.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsertDtoRequest {
    private int templateId;
    private int projectId;
    private int boardId;
    private String feature;
    private String folio;
    private int source;
    private String ingesta;
    private String token;
}

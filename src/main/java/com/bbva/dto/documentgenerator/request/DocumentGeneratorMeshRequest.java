package com.bbva.dto.documentgenerator.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DocumentGeneratorMeshRequest {
    private int projectId;
    private String projectDescription;
    private String sdatool;
    private String userName;
    private String employeeId;
    private String name;
    private String token;
    private DataDocumentMesh dataDocumentMesh;
}

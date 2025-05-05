package com.bbva.dto.documentgenerator.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DataDocumentMeshFolder {
    private String folderName;
    private String xmlBefore;
    private String xmlAfter;
    private List<DataDocumentMeshJobName> jobNames;
}

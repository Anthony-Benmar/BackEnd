package com.bbva.dto.mesh.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MeshRelationalDtoResponse {
    public String rowNumber;
    public String id;
    public String parentId;
    public String jobName;
    public String jsonName;
    public String folder;
    public String application;
    public String orderDate;
    public String frequency;
    public String jobType;
    public String executionDate;
    public String status;
}

package com.bbva.dto.project.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProjectFilterByNameOrSdatoolDtoResponse {
    private int count;
    private int pages_amount;
    private List<ProjectFilterByNameOrSdatoolListDtoResponse> data;
}

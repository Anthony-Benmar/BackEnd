package com.bbva.dto.project.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProjectFilterList {
    private List<ProjectFilterByNameOrSdatoolListDtoResponse> projectFilterList;
}

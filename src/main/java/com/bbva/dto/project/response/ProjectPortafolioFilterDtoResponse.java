package com.bbva.dto.project.response;

import com.bbva.entities.project.ProjectPortafolioFilterEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProjectPortafolioFilterDtoResponse {
    private int count;
    private int pages_amount;
//    private List<ProjectPortafolioFilterListDtoResponse> data;
    private List<ProjectPortafolioFilterEntity> data;
}

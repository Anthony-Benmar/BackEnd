package com.bbva.dto.project.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class ProjectInfoFilterAllByDomainDtoResponse {
    public int count;
    public int pages_amount;
    public List<ProjectInfoSelectAllByDomainDtoResponse> data;
}

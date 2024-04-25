package com.bbva.dto.project.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

public class ProjectInfoFilterByDomainDtoResponse {
    public int count;
    public int pages_amount;
    public List<ProjectInfoSelectByDomainDtoResponse> data;
}

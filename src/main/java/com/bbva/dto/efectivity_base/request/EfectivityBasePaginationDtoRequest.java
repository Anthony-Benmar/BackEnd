package com.bbva.dto.efectivity_base.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EfectivityBasePaginationDtoRequest {
    private Integer limit;
    private Integer offset;
    private String sdatoolProject;
    private String sprintDate;
    private String registerDate;
    private String efficiency;
}
package com.bbva.dto.usecasetracking;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UseCaseDto {
    private Integer use_case_id;
    private String name;
    private Integer domain_id;
}

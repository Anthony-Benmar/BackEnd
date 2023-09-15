package com.bbva.dto.project.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectFilterByNameOrSdatoolDtoRequest {
    private Integer records_amount;
    private Integer page;
    private String sdatool;
    private String name;
}

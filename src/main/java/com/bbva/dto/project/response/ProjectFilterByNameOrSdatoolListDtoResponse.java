package com.bbva.dto.project.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectFilterByNameOrSdatoolListDtoResponse {
    private int id;
    private String name;
    private String sdatool;
    private int state;
}

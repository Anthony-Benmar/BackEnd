package com.bbva.dto.project.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectPortafolioFilterListDtoResponse {
    private int id;
    private String name;
    private String sdatool;
    private int state;
    private String period;
    private String portafolioCode;
    private String sponsor;
    private String projectOwner;
    private Boolean projectRegulatory;
    private String projectDomain;
}

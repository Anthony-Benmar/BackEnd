package com.bbva.dto.visa_sources.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VisaSourcePaginationDtoRequest {
    private Integer limit;
    private Integer offset;
    private Integer id;
    private String quarter;
    private String registerDate;
    private String domain;
    private String userStory;
}

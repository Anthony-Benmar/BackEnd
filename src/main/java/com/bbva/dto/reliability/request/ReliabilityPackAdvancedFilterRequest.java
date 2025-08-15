package com.bbva.dto.reliability.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ReliabilityPackAdvancedFilterRequest {
    private String domainName;
    private String useCase;
    private String role;
    private String tab;
    private Integer page = 1;
    private Integer recordsAmount = 10;
}

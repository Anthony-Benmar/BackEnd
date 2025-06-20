package com.bbva.dto.reliability.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReliabilityPackInputFilterRequest {
    private String domainName;
    private String useCase;
    private Integer page;
    private Integer recordsAmount;
}

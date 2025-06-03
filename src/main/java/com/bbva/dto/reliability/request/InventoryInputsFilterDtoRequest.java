package com.bbva.dto.reliability.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InventoryInputsFilterDtoRequest {
    private Integer page;
    private Integer recordsAmount;
    private String domainName;
    private String useCase;
    private String jobType;
    private String frequency;
    private String isCritical;
    private String searchByInputOutputTable;
    private String searchType;
}

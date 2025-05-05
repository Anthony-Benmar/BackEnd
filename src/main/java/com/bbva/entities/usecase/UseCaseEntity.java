package com.bbva.entities.usecase;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UseCaseEntity {
    private Integer useCaseId;
    private String useCaseName;
    private String useCaseDescription;
}
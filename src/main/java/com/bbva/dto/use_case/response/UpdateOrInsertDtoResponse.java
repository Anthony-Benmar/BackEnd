package com.bbva.dto.use_case.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrInsertDtoResponse {
    private Integer lastUpdatedId;
    private Integer updatedRegister;
    private Integer lastInsertId;
    private Integer newRegister;
    private String errorMessage;
}

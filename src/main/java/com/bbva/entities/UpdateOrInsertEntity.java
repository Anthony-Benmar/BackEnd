package com.bbva.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrInsertEntity {
    private Integer lastUpdatedId;
    private Integer updatedRegister;
    private Integer lastInsertId;
    private Integer newRegister;
}
package com.bbva.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrInsertEntity {
    private Integer last_updated_id;
    private Integer updated_register;
    private Integer last_insert_id;
    private Integer new_register;
}
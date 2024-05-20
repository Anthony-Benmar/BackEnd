package com.bbva.entities;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

@Getter
@Setter
public class InsertEntity {
    private Integer last_insert_id; //AND
    @Nullable
    private Integer last_insert_2_id; //>0 correcto sino incorrecto

    private Integer new_register;
    @Nullable
    private Integer new_register_2;
}

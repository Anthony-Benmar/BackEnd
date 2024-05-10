package com.bbva.entities;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;

@Getter
@Setter
public class InsertEntity {
    private Integer last_insert_id;
    private Integer new_register;
    @Nullable
    private Integer last_insert_id_n;
    @Nullable
    private Integer new_register_n;
}

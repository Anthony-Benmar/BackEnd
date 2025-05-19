package com.bbva.dto.use_case.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateOrInsertDtoResponse {
    private Integer last_updated_id;
    private Integer updated_register;
    private Integer last_insert_id;
    private Integer new_register;
    private String error_message;
}

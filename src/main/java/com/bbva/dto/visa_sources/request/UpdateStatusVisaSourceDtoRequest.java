package com.bbva.dto.visa_sources.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStatusVisaSourceDtoRequest {
    private Integer id;
    private String status;
}

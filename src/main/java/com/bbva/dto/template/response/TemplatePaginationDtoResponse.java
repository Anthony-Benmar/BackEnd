package com.bbva.dto.template.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TemplatePaginationDtoResponse {
    private Integer count;
    private Integer pages_amount;
    private List<TemplatePaginationResponse> data;
}

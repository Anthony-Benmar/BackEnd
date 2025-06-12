package com.bbva.dto.ada.response;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
public class AdaJobExecutionFilterResponseDTO {
    private int count;
    private int pages_amount;
    private List<AdaJobExecutionFilterData> data;
}

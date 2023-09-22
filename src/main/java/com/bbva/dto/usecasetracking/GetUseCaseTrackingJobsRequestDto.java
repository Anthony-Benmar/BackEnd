package com.bbva.dto.usecasetracking;

import com.bbva.dto.PaginationMasterDtoRequest;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GetUseCaseTrackingJobsRequestDto extends PaginationMasterDtoRequest {
    private int useCaseId;
    private String orderDate;
}

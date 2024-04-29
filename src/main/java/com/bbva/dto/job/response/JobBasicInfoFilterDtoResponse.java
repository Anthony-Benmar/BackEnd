package com.bbva.dto.job.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class JobBasicInfoFilterDtoResponse {
    public int count;
    public int pages_amount;
    public List<JobBasicInfoSelectDtoResponse> data;
}

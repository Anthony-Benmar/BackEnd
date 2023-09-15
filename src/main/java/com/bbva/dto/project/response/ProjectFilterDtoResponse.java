package com.bbva.dto.project.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ProjectFilterDtoResponse {
    private int count;
    private int pages_amount;
    private ArrayList data;
}

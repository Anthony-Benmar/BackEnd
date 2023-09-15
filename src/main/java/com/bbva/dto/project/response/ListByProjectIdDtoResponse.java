package com.bbva.dto.project.response;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ListByProjectIdDtoResponse {
    private ArrayList<ProjectListForSelectDtoResponse> data;
}

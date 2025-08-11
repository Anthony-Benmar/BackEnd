package com.bbva.dto.project.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRoleAndDevResponse {
    private List<ProjectRoleDetailResponse> detailResponses;
    private ProjectDevResponse projectDevResponse;
}

package com.bbva.dto.project.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectRoleDetailResponse {
    private String participantName;
    private String projectRolType;
    private String roleDescription;
}

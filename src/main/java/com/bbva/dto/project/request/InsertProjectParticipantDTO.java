package com.bbva.dto.project.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InsertProjectParticipantDTO {
    private Integer projectParticipantId;
    private String participantUser;
    private String participantEmail;
    private Integer projectId;
    private Integer projectRolType;
    private Integer piId;
    private String createAuditUser;
}

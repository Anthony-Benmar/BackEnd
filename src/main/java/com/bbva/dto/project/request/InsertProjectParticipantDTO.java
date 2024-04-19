package com.bbva.dto.project.request;

import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.util.Date;

@Getter
@Setter
public class InsertProjectParticipantDTO {
    public Integer projectParticipantId;
    public String participantName;
    public String participantUser;
    public String participantEmail;
    public Integer projectId;
    public Integer projectRolType;
    public Integer piId;
    public Date createAuditDate;
    @Nullable
    public String createAuditUser;
    public Date updateAuditDate;
    private String updateAuditUser;
}

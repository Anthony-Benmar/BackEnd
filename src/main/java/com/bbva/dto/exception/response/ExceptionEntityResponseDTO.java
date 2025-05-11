package com.bbva.dto.exception.response;

import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
@Getter
@Setter
public class ExceptionEntityResponseDTO {
    private Integer sourceId;
    private String tdsDescription;
    private String tdsSource;
    private String requestingProject;
    private String requestStatus;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date registrationDate;
    private String quarterYearSprint;
    private String shutdownCommitmentDate;
    private String shutdownCommitmentStatus;
    private String shutdownProject;
}

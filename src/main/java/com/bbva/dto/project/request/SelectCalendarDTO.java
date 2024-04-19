package com.bbva.dto.project.request;

import lombok.Getter;
import lombok.Setter;


import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
public class SelectCalendarDTO {
    private Integer piId;
    private String piShortName;
    private String piLargeName;
    private Integer piYearId;
    private Integer piQuarterId;
    private Date startDate;
    private Date endDate;
    private Date createAuditDate;
    private String createAuditUser;
    private Date updateAuditDate;
    private String updateAuditUser;
}

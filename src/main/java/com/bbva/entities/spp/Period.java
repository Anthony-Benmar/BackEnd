package com.bbva.entities.spp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Period {
    private String PeriodId;

    private String StartMeetup;

    private String EndMeetup;

    private String StartDemand;

    private String EndDemand;

    private String StartStaffing;

    private String EndStaffing;

    private Double Rate;

    private Integer Hours;

    private Integer CatalogStatus;

    private String StatusName;
}

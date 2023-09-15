package com.bbva.entities.project;
import com.google.gson.annotations.SerializedName;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
public class ProjectPortafolioFilterEntity {

    private Integer projectId;
    private String projectName;
    private String sdatoolId;
    private String portafolioCode;
    private String period;
    private String sponsor;
    private String projectOwner;
    private Boolean isProjectRegulatory;
    private String domain;
    private Integer recordsCount;
    private String periodId;
}
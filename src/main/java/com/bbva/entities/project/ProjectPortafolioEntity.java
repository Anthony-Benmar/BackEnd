package com.bbva.entities.project;

import com.bbva.entities.BaseEntity;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
public class ProjectPortafolioEntity extends BaseEntity {
    private Integer projectId;
    private String projectName;
    private String projectDesc;
    private String sdatoolId;
    private String portafolioCode;
    private Integer projectType;
    private Integer sponsorOwnerId;
    private Integer productOwnerId;
    private Boolean regulatoryProjectBoolean;
    private Integer projectDomainType;
    private String ruleAssociatedLink;
    private String periodId;
}

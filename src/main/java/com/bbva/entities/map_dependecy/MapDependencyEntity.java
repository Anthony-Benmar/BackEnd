package com.bbva.entities.map_dependecy;

import com.bbva.entities.BaseEntity;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
public class MapDependencyEntity extends BaseEntity {
    private Integer mapDependencyId;
    private Integer useCaseId;
    private Integer keyDataProcessType;
    private String processName;
    private Integer sloOwnerId;
    private String arisCode;
    private String dependencyMapLink;
}

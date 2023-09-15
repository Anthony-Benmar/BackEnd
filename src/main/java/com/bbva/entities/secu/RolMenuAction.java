package com.bbva.entities.secu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class RolMenuAction {
    
    private String menuPropiedad;
    private String accionPropiedad;
    private boolean autorizado;
    private Integer rolMenuActionId;
    private Integer rolMenuId;
    private Integer actionId;

}

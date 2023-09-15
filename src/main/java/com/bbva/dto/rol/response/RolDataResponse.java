package com.bbva.dto.rol.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RolDataResponse {
    
    private Integer idRole;
    private String nombre;
    private String descripcion;
    
}

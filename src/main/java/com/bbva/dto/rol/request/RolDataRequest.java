package com.bbva.dto.rol.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RolDataRequest {
    
    private Integer idRole;
    private String nombre;
    private String descripcion;
    private List<Integer> idsMenu;
    private List<RolDataAccionPermissionRequest> idsAcciones; 

}

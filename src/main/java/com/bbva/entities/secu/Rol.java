package com.bbva.entities.secu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor
public class Rol {
    
    private Integer idRole;
    private String nombre;
    private String descripcion;
    private int operationUser;

}

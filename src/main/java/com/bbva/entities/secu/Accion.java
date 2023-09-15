package com.bbva.entities.secu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Accion {
    
    private int menuId;
    private int accionId;
    private String accionDesc;

}

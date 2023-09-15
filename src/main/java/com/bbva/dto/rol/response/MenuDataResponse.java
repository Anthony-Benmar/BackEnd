package com.bbva.dto.rol.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuDataResponse {
    
    private Integer idMenu;
    private String nombre;
    private List<AccionDataResponse> acciones;

}

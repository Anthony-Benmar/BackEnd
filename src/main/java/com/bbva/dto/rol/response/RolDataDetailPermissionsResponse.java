package com.bbva.dto.rol.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolDataDetailPermissionsResponse {
    
    private List<MenuDataResponse> menu;
    private List<Integer> menuSeleccionado;
    private List<String> accionSeleccionado;

}

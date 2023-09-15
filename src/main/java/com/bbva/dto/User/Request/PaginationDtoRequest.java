package com.bbva.dto.User.Request;

import com.bbva.dto.PaginationMasterDtoRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationDtoRequest extends PaginationMasterDtoRequest{
    
    private String registro;
    private String nombreCompleto;
    private String email;
    private int rol;

}

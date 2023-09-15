package com.bbva.dto.User.Response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PaginationDataResponse {
    
    private Integer id;
    private String registro;
    private String nombreCompleto;
    private String email;
    private String fechaHoraAlta;
    private String roles;
    private List<Integer> idsRoles;

}

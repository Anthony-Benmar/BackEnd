package com.bbva.dto.User.Response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class UpdateRolesResponse {
    
    private Integer idUser;
    private List<Integer> roles;

}

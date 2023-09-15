package com.bbva.dto.User.Request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UpdateRolesRequest {
    
    private Integer idUser;
    private List<Integer> roles;

}

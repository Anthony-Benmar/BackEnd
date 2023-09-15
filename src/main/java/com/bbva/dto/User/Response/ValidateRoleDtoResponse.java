package com.bbva.dto.User.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ValidateRoleDtoResponse {
    @JsonProperty("role")
    public String Role;
    @JsonProperty("menus")
    public List<ValidateRoleMenuDtoResponse> Menus;
    public int roleId;
}

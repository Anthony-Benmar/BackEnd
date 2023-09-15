package com.bbva.dto.User.Response;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
public class ValidateDtoResponse {
    @JsonProperty("user")
    public ValidateUserDtoResponse User;
    @JsonProperty("roles")
    public List<ValidateRoleDtoResponse> Roles;
}

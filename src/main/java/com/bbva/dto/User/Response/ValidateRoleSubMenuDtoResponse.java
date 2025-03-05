package com.bbva.dto.User.Response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ValidateRoleSubMenuDtoResponse {
    @JsonProperty("id")
    public Integer ID;
    @JsonProperty("label")
    public String Label;
    @JsonProperty("icon")
    public String Icon;
    @JsonProperty("url")
    public String Url;
    @JsonProperty("order")
    public Integer order;
    @JsonProperty("options")
    public List<ValidateRoleMenuDtoResponse> options;
}

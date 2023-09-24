package com.bbva.dto.issueticket.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class authJiraDtoRequest {
    public String username;
    public String password;
}

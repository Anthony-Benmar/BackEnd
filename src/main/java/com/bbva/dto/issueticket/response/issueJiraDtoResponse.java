package com.bbva.dto.issueticket.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class issueJiraDtoResponse {
    public String issueCode;
    public String issueStatus;
}

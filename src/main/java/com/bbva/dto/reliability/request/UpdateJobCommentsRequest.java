package com.bbva.dto.reliability.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateJobCommentsRequest {
    private String actorRole;
    private String comments;
}

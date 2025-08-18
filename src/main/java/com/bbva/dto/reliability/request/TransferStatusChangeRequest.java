package com.bbva.dto.reliability.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferStatusChangeRequest {
    private String action;
    private String actorRole;
    private String comment;
    private Integer userId;
}

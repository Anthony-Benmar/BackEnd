package com.bbva.dto.reliability.response;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferStatusChangeResponse {
    private String  pack;
    private Integer oldStatus;
    private Integer newStatus;
}

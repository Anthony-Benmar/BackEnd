package com.bbva.entities.issueticket;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
public class WorkOrderInsertResult {
    public Integer workOrderId;
    public Boolean newRegister;
}

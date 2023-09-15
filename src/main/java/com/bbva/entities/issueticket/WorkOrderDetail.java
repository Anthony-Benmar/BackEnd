package com.bbva.entities.issueticket;

import lombok.*;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@Setter
@Getter
@Data
public class WorkOrderDetail {
    public Integer work_order_detail_id;
    public Integer work_order_id;
    public Integer template_id;
    public String issue_code;
    public String issue_status_type;
    public String register_user_id;
    @Nullable
    public Date register_date;
    @Nullable
    public Date end_date;
}

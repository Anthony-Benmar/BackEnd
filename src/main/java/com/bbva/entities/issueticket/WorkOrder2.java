package com.bbva.entities.issueticket;

import lombok.*;

import javax.annotation.Nullable;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
public class WorkOrder2 {
    public int work_order_id;
    public String feature;
    public String folio;
    public int board_id;
    public int project_id;
    public String source_id;
    public String source_name;
    public int flow_type;

    public Integer fase_id;
    public Integer sprint_est;

    public int work_order_type;
    public int status_type;
    public String register_user_id;
    @Nullable
    public Date register_date;
    @Nullable
    public Date end_date;
    @Nullable
    public Integer records_count;
}

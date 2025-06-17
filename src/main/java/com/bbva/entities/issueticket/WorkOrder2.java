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

    // Usar solo en el DTO
    // public Integer fase_id;
    // public String sprint_est; //Sprint 3

    public int work_order_type;
    public int status_type;
    public String register_user_id;
    @Nullable
    public Date register_date;
    @Nullable
    public Date end_date;
    @Nullable
    public Integer records_count;

    //Revisar si se agregan campos
    //public String summary; //Nombre de la feature, compuesto
    //public String projectKey; // DEDATIO - PAD3
    //public String teamBacklog; //Debe ir con nombre



}

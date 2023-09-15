package com.bbva.entities.secu;

import com.bbva.entities.BaseEntity;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LogEntity extends BaseEntity {
    @SerializedName("log_id")
    private int logId;
    @SerializedName("process_id")
    private int processId;
    @SerializedName("period")
    private String period;
    @SerializedName("process_name")
    private String processName;
    @SerializedName("message")
    private String message;

    public LogEntity(int processId, String period, String processName, String message){
        super();
        this.processId = processId;
        this.period = period;
        this.processName = processName;
        this.message = message;
    }

}

package com.bbva.dto.reliability.response;

public class JobExecutionHistoryDtoResponse {
    private Long period;
    private String executionStatus;

    public Long getPeriod() {
        return period;
    }
    public void setPeriod(Long period) {
        this.period = period;
    }
    public String getExecutionStatus() {
        return executionStatus;
    }
    public void setExecutionStatus(String executionStatus) {
        this.executionStatus = executionStatus;
    }
}

package com.bbva.dto.reliability.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class ServicePermissionResponse {
    private String serviceName;
    private Boolean canDeleteJobs;
}

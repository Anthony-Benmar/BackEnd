package com.bbva.dto.reliability.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProjectCustodyInfoDtoResponseTest {

    @Test
    void testProjectCustodyInfoDtoResponse() {
        ProjectCustodyInfoDtoResponse response = new ProjectCustodyInfoDtoResponse();

        response.setUseCase("Use Case X");
        response.setPack("Custody-Pack-01");
        response.setDomainName("Finance");
        response.setProductOwner("Alice Johnson");

        assertEquals("Use Case X", response.getUseCase());
        assertEquals("Custody-Pack-01", response.getPack());
        assertEquals("Finance", response.getDomainName());
        assertEquals("Alice Johnson", response.getProductOwner());
    }
}

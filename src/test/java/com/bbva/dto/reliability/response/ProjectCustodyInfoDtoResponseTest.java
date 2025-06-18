package com.bbva.dto.reliability.response;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ProjectCustodyInfoDtoResponseTest {

    @Test
    void testProjectCustodyInfoDtoResponse() {
        ProjectCustodyInfoDtoResponse response = new ProjectCustodyInfoDtoResponse();

        response.setUseCaseId(1);
        response.setUseCase("Use Case X");
        response.setPack("Custody-Pack-01");
        response.setDomainName("Finance");
        response.setDomainId(2);
        response.setProductOwner("Alice Johnson");
        response.setProductOwnerUserId(1);

        assertEquals(1, response.getUseCaseId());
        assertEquals("Use Case X", response.getUseCase());
        assertEquals("Custody-Pack-01", response.getPack());
        assertEquals("Finance", response.getDomainName());
        assertEquals(2, response.getDomainId());
        assertEquals("Alice Johnson", response.getProductOwner());
        assertEquals(1, response.getProductOwnerUserId());
    }
}

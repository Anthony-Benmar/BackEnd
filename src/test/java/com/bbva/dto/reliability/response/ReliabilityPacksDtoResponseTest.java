package com.bbva.dto.reliability.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReliabilityPacksDtoResponseTest {

    @Test
    void testBuilderAndGetters() {
        ReliabilityPacksDtoResponse dto = ReliabilityPacksDtoResponse.builder()
                .pack("PACK123")
                .domainId(1001)
                .domainName("GRM")
                .productOwnerUserId(201)
                .useCaseId(301)
                .useCase("Use Case X")
                .projectId(401)
                .sdaToolId("SDATOOL_001")
                .creatorUser("USR_501")
                .pdfLink("https://example.com/doc.pdf")
                .jobCount(10)
                .statusId(2)
                .statusName("Aprobado por PO")
                .cambiedit(1)
                .build();

        assertEquals("PACK123", dto.getPack());
        assertEquals(1001, dto.getDomainId());
        assertEquals("GRM", dto.getDomainName());
        assertEquals(201, dto.getProductOwnerUserId());
        assertEquals(301, dto.getUseCaseId());
        assertEquals("Use Case X", dto.getUseCase());
        assertEquals(401, dto.getProjectId());
        assertEquals("SDATOOL_001", dto.getSdaToolId());
        assertEquals("USR_501", dto.getCreatorUser());
        assertEquals("https://example.com/doc.pdf", dto.getPdfLink());
        assertEquals(10, dto.getJobCount());
        assertEquals(2, dto.getStatusId());
        assertEquals("Aprobado por PO", dto.getStatusName());
        assertEquals(1, dto.getCambiedit());
    }

    @Test
    void testAllArgsConstructor() {
        ReliabilityPacksDtoResponse dto = new ReliabilityPacksDtoResponse(
                "PACK999",                 // pack
                2001,                      // domainId
                "DOMX",                    // domainName
                202,                       // productOwnerUserId
                302,                       // useCaseId
                "Use Case Y",              // useCase
                402,                       // projectId
                "SDATOOL_999",             // sdaToolId
                "USR_502",                 // creatorUser
                "https://bbva.com/file.pdf", // pdfLink
                5,                         // jobCount
                1,                         // statusId
                "Aprobado por Reliability",// statusName
                0                          // cambiedit
        );

        assertEquals("PACK999", dto.getPack());
        assertEquals(2001, dto.getDomainId());
        assertEquals("DOMX", dto.getDomainName());
        assertEquals(202, dto.getProductOwnerUserId());
        assertEquals(302, dto.getUseCaseId());
        assertEquals("Use Case Y", dto.getUseCase());
        assertEquals(402, dto.getProjectId());
        assertEquals("SDATOOL_999", dto.getSdaToolId());
        assertEquals("USR_502", dto.getCreatorUser());
        assertEquals("https://bbva.com/file.pdf", dto.getPdfLink());
        assertEquals(5, dto.getJobCount());
        assertEquals(1, dto.getStatusId());
        assertEquals("Aprobado por Reliability", dto.getStatusName());
        assertEquals(0, dto.getCambiedit());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        ReliabilityPacksDtoResponse dto = new ReliabilityPacksDtoResponse();

        dto.setPack("PACK000");
        dto.setDomainId(1111);
        dto.setDomainName("TestDomain");
        dto.setCreatorUser("USR_TEST");
        dto.setJobCount(3);
        dto.setStatusId(5);
        dto.setStatusName("Devuelto por Reliability");
        dto.setCambiedit(1);

        assertEquals("PACK000", dto.getPack());
        assertEquals(1111, dto.getDomainId());
        assertEquals("TestDomain", dto.getDomainName());
        assertEquals("USR_TEST", dto.getCreatorUser());
        assertEquals(3, dto.getJobCount());
        assertEquals(5, dto.getStatusId());
        assertEquals("Devuelto por Reliability", dto.getStatusName());
        assertEquals(1, dto.getCambiedit());
    }
}
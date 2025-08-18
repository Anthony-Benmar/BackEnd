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
                .creatorUserId(501)
                .pdfLink("https://example.com/doc.pdf")
                .jobCount(10)
                .statusId(2)
                .statusName("Aprobado por PO")
                .canEdit(1)
                .canEditComments(0)
                .build();

        assertEquals("PACK123", dto.getPack());
        assertEquals(1001, dto.getDomainId());
        assertEquals("GRM", dto.getDomainName());
        assertEquals(201, dto.getProductOwnerUserId());
        assertEquals(301, dto.getUseCaseId());
        assertEquals("Use Case X", dto.getUseCase());
        assertEquals(401, dto.getProjectId());
        assertEquals("SDATOOL_001", dto.getSdaToolId());
        assertEquals(501, dto.getCreatorUserId());
        assertEquals("https://example.com/doc.pdf", dto.getPdfLink());
        assertEquals(10, dto.getJobCount());
        assertEquals(2, dto.getStatusId());
        assertEquals("Aprobado por PO", dto.getStatusName());
        assertEquals(1, dto.getCanEdit());
        assertEquals(0, dto.getCanEditComments());
    }

    @Test
    void testAllArgsConstructor() {
        ReliabilityPacksDtoResponse dto = new ReliabilityPacksDtoResponse(
                "PACK999",         // pack
                2001,              // domainId
                "DOMX",            // domainName
                202,               // productOwnerUserId
                302,               // useCaseId
                "Use Case Y",      // useCase
                402,               // projectId
                "SDATOOL_999",     // sdaToolId
                502,               // creatorUserId
                "https://bbva.com/file.pdf", // pdfLink
                5,                 // jobCount
                1,                 // statusId
                "Aprobado por Reliability", // statusName
                1,                 // canEdit
                1                  // canEditComments
        );

        assertEquals("PACK999", dto.getPack());
        assertEquals(2001, dto.getDomainId());
        assertEquals("DOMX", dto.getDomainName());
        assertEquals(202, dto.getProductOwnerUserId());
        assertEquals(302, dto.getUseCaseId());
        assertEquals("Use Case Y", dto.getUseCase());
        assertEquals(402, dto.getProjectId());
        assertEquals("SDATOOL_999", dto.getSdaToolId());
        assertEquals(502, dto.getCreatorUserId());
        assertEquals("https://bbva.com/file.pdf", dto.getPdfLink());
        assertEquals(5, dto.getJobCount());
        assertEquals(1, dto.getStatusId());
        assertEquals("Aprobado por Reliability", dto.getStatusName());
        assertEquals(1, dto.getCanEdit());
        assertEquals(1, dto.getCanEditComments());
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        ReliabilityPacksDtoResponse dto = new ReliabilityPacksDtoResponse();

        dto.setPack("PACK000");
        dto.setDomainId(1111);
        dto.setDomainName("TestDomain");
        dto.setJobCount(3);
        dto.setStatusId(5);
        dto.setStatusName("Devuelto por Reliability");
        dto.setCanEdit(0);
        dto.setCanEditComments(1);

        assertEquals("PACK000", dto.getPack());
        assertEquals(1111, dto.getDomainId());
        assertEquals("TestDomain", dto.getDomainName());
        assertEquals(3, dto.getJobCount());
        assertEquals(5, dto.getStatusId());
        assertEquals("Devuelto por Reliability", dto.getStatusName());
        assertEquals(0, dto.getCanEdit());
        assertEquals(1, dto.getCanEditComments());
    }
}

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
    }

    @Test
    void testAllArgsConstructor() {
        ReliabilityPacksDtoResponse dto = new ReliabilityPacksDtoResponse(
                "PACK999", 2001, "DOMX", 202, 302,
                "Use Case Y", 402, "SDATOOL_999",
                502, "https://bbva.com/file.pdf", 5
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
    }

    @Test
    void testNoArgsConstructorAndSetters() {
        ReliabilityPacksDtoResponse dto = new ReliabilityPacksDtoResponse();

        dto.setPack("PACK000");
        dto.setDomainId(1111);
        dto.setDomainName("TestDomain");
        dto.setJobCount(3);

        assertEquals("PACK000", dto.getPack());
        assertEquals(1111, dto.getDomainId());
        assertEquals("TestDomain", dto.getDomainName());
        assertEquals(3, dto.getJobCount());
    }
}

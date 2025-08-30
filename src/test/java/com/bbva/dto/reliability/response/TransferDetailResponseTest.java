package com.bbva.dto.reliability.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransferDetailResponseTest {

    @Test
    void builderAndGetters_shouldWork_withAllNewHeaderFields() {
        // Header con TODOS los campos nuevos
        var header = TransferDetailResponse.Header.builder()
                .pack("PACK12345")
                .sdaToolId("SDATOOL-33319")
                .domainId(1)
                .domainName("FIN")
                .useCaseId(5)
                .useCase("PFM")
                .statusId(3)
                .statusName("En progreso")
                .comments(null) // puede venir null
                .creatorEmail("clinton.huamani@bbva.com")
                .pdfLink("https://drive.google.com/your-pdf-link")
                .sn2Id(21)
                .sn2Desc("DEDRRA-RISK ANALYTICS-85225")
                .productOwnerEmail("po@bbva.com")
                .build();

        var job = TransferDetailResponse.JobRow.builder()
                .jobName("PKBRBCP4028")
                .jsonName("kbrb-pe-spk-inm-riskapprove")
                .frequencyId(4)
                .jobTypeId(5)
                .jobPhaseId(1)
                .originTypeId(2)
                .inputPaths("")
                .outputPath("")
                .bitBucketUrl("")
                .responsible("user@bbva.com")
                .useCaseId(5)
                .domainId(1)
                .isCritical("NO")
                .statusId(3)
                .comments("")
                .build();

        var dto = TransferDetailResponse.builder()
                .header(header)
                .jobs(List.of(job))
                .build();

        // Asserts Header
        assertEquals("PACK12345", dto.getHeader().getPack());
        assertEquals("SDATOOL-33319", dto.getHeader().getSdaToolId());
        assertEquals(1, dto.getHeader().getDomainId());
        assertEquals("FIN", dto.getHeader().getDomainName());
        assertEquals(5, dto.getHeader().getUseCaseId());
        assertEquals("PFM", dto.getHeader().getUseCase());
        assertEquals(3, dto.getHeader().getStatusId());
        assertEquals("En progreso", dto.getHeader().getStatusName());
        assertNull(dto.getHeader().getComments());
        assertEquals("clinton.huamani@bbva.com", dto.getHeader().getCreatorEmail());
        assertEquals("https://drive.google.com/your-pdf-link", dto.getHeader().getPdfLink());
        assertEquals(21, dto.getHeader().getSn2Id());
        assertEquals("DEDRRA-RISK ANALYTICS-85225", dto.getHeader().getSn2Desc());
        assertEquals("po@bbva.com", dto.getHeader().getProductOwnerEmail());

        // Asserts Job
        assertEquals(1, dto.getJobs().size());
        var j0 = dto.getJobs().get(0);
        assertEquals("PKBRBCP4028", j0.getJobName());
        assertEquals("kbrb-pe-spk-inm-riskapprove", j0.getJsonName());
        assertEquals(4, j0.getFrequencyId());
        assertEquals(5, j0.getJobTypeId());
        assertEquals(1, j0.getJobPhaseId());
        assertEquals(2, j0.getOriginTypeId());
        assertEquals("", j0.getInputPaths());
        assertEquals("", j0.getOutputPath());
        assertEquals("", j0.getBitBucketUrl());
        assertEquals("user@bbva.com", j0.getResponsible());
        assertEquals(5, j0.getUseCaseId());
        assertEquals(1, j0.getDomainId());
        assertEquals("NO", j0.getIsCritical());
        assertEquals(3, j0.getStatusId());
        assertEquals("", j0.getComments());
    }

    @Test
    void jsonSerialization_shapeIsAsExpected_andRoundTrip() throws Exception {
        var header = TransferDetailResponse.Header.builder()
                .pack("PACK12345")
                .sdaToolId("SDATOOL-33319")
                .domainId(1)
                .domainName("FIN")
                .useCaseId(5)
                .useCase("Plataforma Unica de Personal Financial Management (PFM)")
                .statusId(3)
                .statusName("En progreso")
                .comments("") // también válido string vacío
                .creatorEmail("clinton.huamani@bbva.com")
                .pdfLink("https://drive.google.com/your-pdf-link")
                .sn2Id(21)
                .sn2Desc("DEDRRA-RISK ANALYTICS-85225")
                .productOwnerEmail("po@bbva.com")
                .build();

        var job = TransferDetailResponse.JobRow.builder()
                .jobName("PKBRBCP4028")
                .jsonName("kbrb-pe-spk-inm-riskapprove")
                .frequencyId(4)
                .jobTypeId(5)
                .jobPhaseId(1)
                .originTypeId(2)
                .inputPaths("")
                .outputPath("")
                .bitBucketUrl("")
                .responsible("user@bbva.com")
                .useCaseId(5)
                .domainId(1)
                .isCritical("NO")
                .statusId(3)
                .comments("")
                .build();

        var dto = TransferDetailResponse.builder()
                .header(header)
                .jobs(List.of(job))
                .build();

        ObjectMapper mapper = new ObjectMapper();

        // Serialize
        String json = mapper.writeValueAsString(dto);
        JsonNode root = mapper.readTree(json);

        // Validar shape
        assertEquals("PACK12345", root.path("header").path("pack").asText());
        assertEquals("SDATOOL-33319", root.path("header").path("sdaToolId").asText());
        assertEquals(3, root.path("header").path("statusId").asInt());
        assertEquals("clinton.huamani@bbva.com", root.path("header").path("creatorEmail").asText());
        assertEquals("https://drive.google.com/your-pdf-link", root.path("header").path("pdfLink").asText());
        assertEquals(21, root.path("header").path("sn2Id").asInt());
        assertEquals("DEDRRA-RISK ANALYTICS-85225", root.path("header").path("sn2Desc").asText());
        assertEquals("po@bbva.com", root.path("header").path("productOwnerEmail").asText());

        assertEquals(1, root.path("jobs").size());
        assertEquals("PKBRBCP4028", root.path("jobs").get(0).path("jobName").asText());
        assertEquals(3, root.path("jobs").get(0).path("statusId").asInt());

        // Round-trip: Deserialize y volver a comparar algunos campos críticos
        var back = mapper.readValue(json, TransferDetailResponse.class);
        assertEquals(dto.getHeader().getPack(), back.getHeader().getPack());
        assertEquals(dto.getHeader().getProductOwnerEmail(), back.getHeader().getProductOwnerEmail());
        assertEquals(dto.getJobs().get(0).getJobName(), back.getJobs().get(0).getJobName());
        assertEquals(dto.getJobs().get(0).getStatusId(), back.getJobs().get(0).getStatusId());
    }

    @Test
    void header_optionalFields_canBeNull_withoutBreaking() throws Exception {
        var header = TransferDetailResponse.Header.builder()
                .pack("PACK0001")
                .sdaToolId(null)
                .domainId(null)
                .domainName(null)
                .useCaseId(null)
                .useCase(null)
                .statusId(2)
                .statusName("Aprobado por PO")
                .comments(null)
                .creatorEmail(null)
                .pdfLink(null)
                .sn2Id(null)
                .sn2Desc(null)
                .productOwnerEmail(null)
                .build();

        var dto = TransferDetailResponse.builder()
                .header(header)
                .jobs(List.of())
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(dto);
        assertTrue(json.contains("\"pack\":\"PACK0001\""));
        // Solo aseguramos que serialize sin explotar y mantenga el pack
        var back = mapper.readValue(json, TransferDetailResponse.class);
        assertEquals("PACK0001", back.getHeader().getPack());
        assertEquals(0, back.getJobs().size());
    }
}
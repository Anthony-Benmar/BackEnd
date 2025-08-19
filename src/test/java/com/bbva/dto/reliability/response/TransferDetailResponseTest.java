package com.bbva.dto.reliability.response;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransferDetailResponseTest {

    @Test
    void builderAndGetters_shouldWork() {
        var header = TransferDetailResponse.Header.builder()
                .pack("PACK12345")
                .sdaToolId("SDATOOL-33319")
                .domainId(1)
                .domainName("FIN")
                .useCaseId(5)
                .useCase("PFM")
                .statusId(3)
                .statusName("En progreso")
                .comments("")   // puede venir null si no hay comentarios
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

        assertEquals("PACK12345", dto.getHeader().getPack());
        assertEquals(1, dto.getJobs().size());
        assertEquals("PKBRBCP4028", dto.getJobs().get(0).getJobName());
        assertEquals(3, dto.getJobs().get(0).getStatusId());
    }

    @Test
    void jsonSerialization_shapeIsAsExpected() throws Exception {
        var header = TransferDetailResponse.Header.builder()
                .pack("PACK12345")
                .sdaToolId("SDATOOL-33319")
                .domainId(1)
                .domainName("FIN")
                .useCaseId(5)
                .useCase("Plataforma Unica de Personal Financial Management (PFM)")
                .statusId(3)
                .statusName("En progreso")
                .comments("")
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
        String json = mapper.writeValueAsString(dto);

        JsonNode root = mapper.readTree(json);
        assertEquals("PACK12345", root.path("header").path("pack").asText());
        assertEquals("SDATOOL-33319", root.path("header").path("sdaToolId").asText());
        assertEquals(3, root.path("header").path("statusId").asInt());
        assertEquals(1, root.path("jobs").size());
        assertEquals("PKBRBCP4028", root.path("jobs").get(0).path("jobName").asText());
        assertEquals(3, root.path("jobs").get(0).path("statusId").asInt());
    }
}
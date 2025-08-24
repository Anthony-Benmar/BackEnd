package com.bbva.resources;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dto.catalog.response.DropDownDto;
import com.bbva.dto.reliability.request.*;
import com.bbva.dto.reliability.response.*;
import com.bbva.service.ReliabilityService;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.ws.rs.core.Response;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReliabilityResourceTest {
    private ReliabilityService reliabilityServiceMock;
    private ReliabilityResource reliabilityResource;

    @BeforeEach
    void setUp() {
        reliabilityServiceMock = mock(ReliabilityService.class);
        reliabilityResource = new ReliabilityResource() {
            {
                try {
                    var field = ReliabilityResource.class.getDeclaredField("reliabilityService");
                    field.setAccessible(true);
                    field.set(this, reliabilityServiceMock);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    @Test
    void testInventoryInputsFilter() {
        InventoryInputsFilterDtoRequest request = new InventoryInputsFilterDtoRequest();

        InventoryInputsFilterDtoResponse responseData = new InventoryInputsFilterDtoResponse();
        IDataResult<InventoryInputsFilterDtoResponse> dataResult = new SuccessDataResult<>(responseData);

        when(reliabilityServiceMock.inventoryInputsFilter(request)).thenReturn(dataResult);

        IDataResult<InventoryInputsFilterDtoResponse> result = reliabilityResource.inventoryInputsFilter(request);

        assertNotNull(result);
        assertEquals(dataResult, result);
        verify(reliabilityServiceMock).inventoryInputsFilter(request);
    }

    @Test
    void testGetExecutionValidationAll() {
        List<String> jobNames = List.of("JOB1", "JOB2");
        IDataResult<List<ExecutionValidationAllDtoResponse>> dataResult = new SuccessDataResult<>(Collections.emptyList());

        when(reliabilityServiceMock.getExecutionValidationAll(jobNames)).thenReturn(dataResult);

        IDataResult<List<ExecutionValidationAllDtoResponse>> result = reliabilityResource.getExecutionValidationAll(null, jobNames);

        assertNotNull(result);
        assertEquals(0, result.data.size());
        verify(reliabilityServiceMock).getExecutionValidationAll(jobNames);
    }

    @Test
    void testGenerateDocumentMeshTracking() {
        InventoryInputsFilterDtoRequest request = new InventoryInputsFilterDtoRequest();
        byte[] mockExcel = "FakeExcelContent".getBytes();

        when(reliabilityServiceMock.generateDocumentInventory(request)).thenReturn(mockExcel);

        Response response = reliabilityResource.generateDocumentMeshTracking(request);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertArrayEquals(mockExcel, (byte[]) response.getEntity());
        assertTrue(response.getHeaderString("Content-Disposition").contains("Inventario_job_control_v1.xlsx"));
    }

    @Test
    void testGetPendingCustodyJobs() {
        String sdatoolId = "SDATOOL123";
        List<PendingCustodyJobsDtoResponse> pendingJobs = Collections.emptyList();
        IDataResult<List<PendingCustodyJobsDtoResponse>> dataResult = new SuccessDataResult<>(pendingJobs);

        when(reliabilityServiceMock.getPendingCustodyJobs(sdatoolId)).thenReturn(dataResult);

        IDataResult<List<PendingCustodyJobsDtoResponse>> result = reliabilityResource.getPendingCustodyJobs(null, sdatoolId);

        assertNotNull(result);
        assertEquals(pendingJobs, result.data);
        verify(reliabilityServiceMock).getPendingCustodyJobs(sdatoolId);
    }

    @Test
    void testGetProjectCustodyInfo() {
        String sdatoolId = "SDATOOL456";
        List<ProjectCustodyInfoDtoResponse> projectCustodyInfo = Collections.emptyList();
        IDataResult<List<ProjectCustodyInfoDtoResponse>> dataResult = new SuccessDataResult<>(projectCustodyInfo);

        when(reliabilityServiceMock.getProjectCustodyInfo(sdatoolId)).thenReturn(dataResult);

        IDataResult<List<ProjectCustodyInfoDtoResponse>> result = reliabilityResource.getProjectCustodyInfo(null, sdatoolId);

        assertNotNull(result);
        assertEquals(projectCustodyInfo, result.data);
        verify(reliabilityServiceMock).getProjectCustodyInfo(sdatoolId);
    }

    @Test
    void testGetExecutionValidation() {
        String jobName = "JOB789";
        ExecutionValidationDtoResponse executionValidationDtoResponse = new ExecutionValidationDtoResponse();
        IDataResult<ExecutionValidationDtoResponse> dataResult = new SuccessDataResult<>(executionValidationDtoResponse);

        when(reliabilityServiceMock.getExecutionValidation(jobName)).thenReturn(dataResult);

        IDataResult<ExecutionValidationDtoResponse> result = reliabilityResource.getExecutionValidation(null, jobName);

        assertNotNull(result);
        assertEquals(executionValidationDtoResponse, result.data);
        verify(reliabilityServiceMock).getExecutionValidation(jobName);
    }

    @Test
    void testUpdateInventoryJobStock() {
        InventoryJobUpdateDtoRequest dto = new InventoryJobUpdateDtoRequest();
        IDataResult<Void> dataResult = new SuccessDataResult<>(null);

        when(reliabilityServiceMock.updateInventoryJobStock(dto)).thenReturn(dataResult);

        IDataResult<Void> result = reliabilityResource.updateInventoryJobStock(dto);

        assertNotNull(result);
        assertEquals(dataResult, result);
        verify(reliabilityServiceMock).updateInventoryJobStock(dto);
    }

    @Test
    void testInsertTransfer() {
        TransferInputDtoRequest dto = new TransferInputDtoRequest();
        IDataResult<Void> dataResult = new SuccessDataResult<>(null);

        when(reliabilityServiceMock.insertTransfer(dto)).thenReturn(dataResult);

        IDataResult<Void> result = reliabilityResource.insertTransfer(dto);

        assertNotNull(result);
        assertEquals(dataResult, result);
        verify(reliabilityServiceMock).insertTransfer(dto);
    }

    @Test
    void testGetReliabilityPacks() {
        ReliabilityPackInputFilterRequest dto = new ReliabilityPackInputFilterRequest();
        dto.setPage(1);
        dto.setRecordsAmount(10);
        dto.setDomainName("SDATOOL123");
        dto.setUseCase("ReliabilityPackInputFilterUseCase");

        PaginationReliabilityPackResponse projectCustodyInfo = new PaginationReliabilityPackResponse();
        IDataResult<PaginationReliabilityPackResponse> dataResult = new SuccessDataResult<>(projectCustodyInfo);

        when(reliabilityServiceMock.getReliabilityPacks(dto)).thenReturn(dataResult);

        IDataResult<PaginationReliabilityPackResponse> result = reliabilityResource.getReliabilityPacks(dto);

        assertNotNull(result);
        assertEquals(projectCustodyInfo, result.data);
        verify(reliabilityServiceMock).getReliabilityPacks(dto);
    }

    @Test
    void testUpdateStatusReliabilityPacksJobStock() {
        List<String> packs = Lists.newArrayList("pack1", "pack2", "pack3", "pack4", "pack5", "pack6");
        IDataResult<Void> dataResult = new SuccessDataResult<>(null, "ReliabilityPacks and JobStock updated successfully");

        when(reliabilityServiceMock.updateStatusReliabilityPacksJobStock(packs)).thenReturn(dataResult);

        IDataResult<Void> result = reliabilityResource.updateStatusReliabilityPacksJobStock(null, packs);

        assertNotNull(result);
    }

    private static DropDownDto ddl(int v, String l) {
        var d = new DropDownDto();
        d.setValue(v);
        d.setLabel(l);
        return d;
    }

    private static Map<String, String> map(String... kv) {
        Map<String, String> m = new HashMap<>();
        for (int i = 0; i < kv.length - 1; i += 2) m.put(kv[i], kv[i + 1]);
        return m;
    }

    @Test
    void originTypes_ok_y_error() {
        when(reliabilityServiceMock.getOriginTypes()).thenReturn(new SuccessDataResult<>(List.of(ddl(1, "Type A"))));
        var ok = reliabilityResource.getOriginTypes();
        assertTrue(ok.success);
        assertEquals(1, ok.data.size());
        assertEquals("Type A", ok.data.get(0).getLabel());
        verify(reliabilityServiceMock).getOriginTypes();

        reset(reliabilityServiceMock);
        when(reliabilityServiceMock.getOriginTypes()).thenReturn(new ErrorDataResult<>(null, "500", "DB down"));
        var err = reliabilityResource.getOriginTypes();
        assertFalse(err.success);
        assertEquals("500", err.status);
        verify(reliabilityServiceMock).getOriginTypes();
    }

    @Test
    void executionHistory_ok_y_error() {
        String okJob = "JOB_OK";
        var hist = List.of(new JobExecutionHistoryDtoResponse());
        when(reliabilityServiceMock.getJobExecutionHistory(okJob)).thenReturn(new SuccessDataResult<>(hist));
        var ok = reliabilityResource.getExecutionHistory(okJob);
        assertTrue(ok.success);
        assertSame(hist, ok.data);
        verify(reliabilityServiceMock).getJobExecutionHistory(okJob);

        reset(reliabilityServiceMock);
        String badJob = "JOB_ERR";
        when(reliabilityServiceMock.getJobExecutionHistory(badJob))
                .thenReturn(new ErrorDataResult<>(null, "500", "Error interno"));
        var err = reliabilityResource.getExecutionHistory(badJob);
        assertFalse(err.success);
        assertEquals("500", err.status);
        assertNull(err.data);
        verify(reliabilityServiceMock).getJobExecutionHistory(badJob);
    }

    @Test
    void sn2Options_ok_y_error() {
        int sn1 = 1027;
        var opts = List.of(ddl(10, "Opción A"), ddl(20, "Opción B"));
        when(reliabilityServiceMock.getSn2Options(sn1)).thenReturn(new SuccessDataResult<>(opts));
        var ok = reliabilityResource.sn2Options(sn1);
        assertTrue(ok.success);
        assertEquals(2, ok.data.size());
        assertEquals(10, ok.data.get(0).getValue());
        verify(reliabilityServiceMock).getSn2Options(sn1);

        reset(reliabilityServiceMock);
        when(reliabilityServiceMock.getSn2Options(42)).thenReturn(new ErrorDataResult<>(null, "500", "Error interno"));
        var err = reliabilityResource.sn2Options(42);
        assertFalse(err.success);
        assertEquals("500", err.status);
        assertNull(err.data);
        verify(reliabilityServiceMock).getSn2Options(42);
    }

    @Test
    void packsV2_role_resolucion_ok() {
        record Caso(String bodyRole, String headerRole, String expectedRole) {
        }
        var casos = List.of(
                new Caso("SM", "KM", "SM"),
                new Caso("", "KM", "KM"),
                new Caso(null, null, null)
        );

        for (var c : casos) {
            var dto = new ReliabilityPackInputFilterRequest();
            dto.setRole(c.bodyRole());
            dto.setTab("EN_PROGRESO");

            when(reliabilityServiceMock.getReliabilityPacksAdvanced(any()))
                    .thenReturn(new SuccessDataResult<>(new PaginationReliabilityPackResponse()));

            var res = reliabilityResource.getReliabilityPacksV2(dto, c.headerRole());
            assertTrue(res.success);

            ArgumentCaptor<ReliabilityPackInputFilterRequest> captor = ArgumentCaptor.forClass(ReliabilityPackInputFilterRequest.class);
            verify(reliabilityServiceMock).getReliabilityPacksAdvanced(captor.capture());
            assertEquals(c.expectedRole(), captor.getValue().getRole());

            reset(reliabilityServiceMock);
        }
    }

    @Test
    void packsV2_propagacion_error() {
        var dto = new ReliabilityPackInputFilterRequest();
        dto.setRole("KM");
        dto.setTab("APROBADOS");
        when(reliabilityServiceMock.getReliabilityPacksAdvanced(any()))
                .thenReturn(new ErrorDataResult<>(null, "500", "Fallo"));
        var res = reliabilityResource.getReliabilityPacksV2(dto, null);
        assertFalse(res.success);
        assertEquals("500", res.status);
        assertNull(res.data);
        verify(reliabilityServiceMock).getReliabilityPacksAdvanced(any());
    }

    @Test
    void changeStatus_ok_y_error() {
        String pack = "PACK_123";
        var req = new TransferStatusChangeRequest();
        req.setActorRole("KM");
        req.setAction("APROBAR");

        var payload = TransferStatusChangeResponse.builder().pack(pack).oldStatus(3).newStatus(4).build();
        when(reliabilityServiceMock.changeTransferStatus(eq(pack), any())).thenReturn(new SuccessDataResult<>(payload, "Estado actualizado"));
        var ok = reliabilityResource.changeTransferStatus(pack, req);
        assertTrue(ok.success);
        assertEquals(4, ok.data.getNewStatus());
        verify(reliabilityServiceMock).changeTransferStatus(eq(pack), any());

        reset(reliabilityServiceMock);
        when(reliabilityServiceMock.changeTransferStatus(eq("PACK_404"), any()))
                .thenReturn(new ErrorDataResult<>(null, "404", "Pack no encontrado"));
        var err = reliabilityResource.changeTransferStatus("PACK_404", req);
        assertFalse(err.success);
        assertEquals("404", err.status);
        verify(reliabilityServiceMock).changeTransferStatus(eq("PACK_404"), any());
    }

    @Test
    void updateJobBySm_ok_y_error() {
        var body = new UpdateJobDtoRequest();
        when(reliabilityServiceMock.updateJobBySm(any()))
                .thenReturn(new SuccessDataResult<>(null, "Job actualizado"));

        var ok = reliabilityResource.updateJobBySm("PACK_X", "JOB_X", body, "SM");
        assertTrue(ok.success);

        ArgumentCaptor<UpdateJobDtoRequest> capt = ArgumentCaptor.forClass(UpdateJobDtoRequest.class);
        verify(reliabilityServiceMock).updateJobBySm(capt.capture());
        assertEquals("PACK_X", capt.getValue().getPack());
        assertEquals("JOB_X", capt.getValue().getJobName());
        assertEquals("SM", capt.getValue().getActorRole());

        reset(reliabilityServiceMock);
        when(reliabilityServiceMock.updateJobBySm(any()))
                .thenReturn(new ErrorDataResult<>(null, "409", "Solo se puede editar cuando fue devuelto"));
        var err = reliabilityResource.updateJobBySm("P1", "J1", new UpdateJobDtoRequest(), "SM");
        assertFalse(err.success);
        assertEquals("409", err.status);
        verify(reliabilityServiceMock).updateJobBySm(any());
    }

    @Test
    void updateCommentsForPack_casos() {
        String pack = "PACK_C";
        Map<String, String> bodySinActor = map("comments", "nota de KM");
        when(reliabilityServiceMock.updateCommentsForPack(pack, "KM", "nota de KM"))
                .thenReturn(new SuccessDataResult<>(null, "Comentarios actualizados"));
        var ok = reliabilityResource.updateCommentsForPack(pack, bodySinActor, "KM");
        assertTrue(ok.success);
        verify(reliabilityServiceMock).updateCommentsForPack(pack, "KM", "nota de KM");

        reset(reliabilityServiceMock);
        Map<String, String> bodyConActor = map("actorRole", "KM", "comments", "otra nota");
        when(reliabilityServiceMock.updateCommentsForPack("PACK_C2", "KM", "otra nota"))
                .thenReturn(new SuccessDataResult<>(null, "ok"));
        var ok2 = reliabilityResource.updateCommentsForPack("PACK_C2", bodyConActor, "SM");
        assertTrue(ok2.success);
        verify(reliabilityServiceMock).updateCommentsForPack("PACK_C2", "KM", "otra nota");
    }

    @Test
    void transferDetail_ok_y_error() {
        var detail = new TransferDetailResponse();
        when(reliabilityServiceMock.getTransferDetail("PACK_OK")).thenReturn(new SuccessDataResult<>(detail));
        var ok = reliabilityResource.getTransferDetail("PACK_OK");
        assertTrue(ok.success);
        assertSame(detail, ok.data);
        verify(reliabilityServiceMock).getTransferDetail("PACK_OK");

        reset(reliabilityServiceMock);
        when(reliabilityServiceMock.getTransferDetail("PACK_404"))
                .thenReturn(new ErrorDataResult<>(null, "404", "Pack no encontrado"));
        var err = reliabilityResource.getTransferDetail("PACK_404");
        assertFalse(err.success);
        assertEquals("404", err.status);
        verify(reliabilityServiceMock).getTransferDetail("PACK_404");
    }

    @Test
    void updateTransferDetail_ok_y_error() {
        String pack = "PACK_OK", role = "SM";
        var body = new TransferDetailUpdateRequest();
        var h = new TransferDetailUpdateRequest.Header();
        h.setComments("c");
        body.setHeader(h);
        var j = new TransferDetailUpdateRequest.Job();
        j.setJobName("JobTest");
        body.setJobs(List.of(j));

        var snap = new TransferDetailResponse();
        when(reliabilityServiceMock.updateTransferDetail(pack, role, body))
                .thenReturn(new SuccessDataResult<>(snap, "OK"));
        var ok = reliabilityResource.updateTransferDetail(pack, body, role);
        assertTrue(ok.success);
        assertSame(snap, ok.data);
        assertEquals("OK", ok.message);
        verify(reliabilityServiceMock).updateTransferDetail(pack, role, body);

        reset(reliabilityServiceMock);
        when(reliabilityServiceMock.updateTransferDetail(
                eq("PACK_ERR"),
                eq("KM"),
                any(TransferDetailUpdateRequest.class)
        )).thenReturn(new ErrorDataResult<>(null, "500", "Error interno"));

        var err = reliabilityResource.updateTransferDetail("PACK_ERR", new TransferDetailUpdateRequest(), "KM");
        assertFalse(err.success);
        assertEquals("500", err.status);
        verify(reliabilityServiceMock).updateTransferDetail(eq("PACK_ERR"), eq("KM"), any(TransferDetailUpdateRequest.class));
    }
}
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

        IDataResult<Void> result = reliabilityResource.updateStatusReliabilityPacksJobStock(null,packs);

        assertNotNull(result);
    }

    @Test
    void testGetOriginTypes() {
        DropDownDto dto = new DropDownDto();
        dto.setValue(1);
        dto.setLabel("Type A");

        List<DropDownDto> originTypes = Collections.singletonList(dto);
        IDataResult<List<DropDownDto>> dataResult = new SuccessDataResult<>(originTypes);

        when(reliabilityServiceMock.getOriginTypes()).thenReturn(dataResult);

        IDataResult<List<DropDownDto>> result = reliabilityResource.getOriginTypes();

        assertNotNull(result);
        assertEquals(1, result.data.size());
        assertEquals(1, result.data.get(0).getValue());
        assertEquals("Type A", result.data.get(0).getLabel());

        verify(reliabilityServiceMock).getOriginTypes();
    }

    @Test
    void testGetExecutionHistory() {
        String jobName = "JOB_TEST";
        JobExecutionHistoryDtoResponse dto = new JobExecutionHistoryDtoResponse();
        List<JobExecutionHistoryDtoResponse> history = List.of(dto);
        IDataResult<List<JobExecutionHistoryDtoResponse>> dataResult =
                new SuccessDataResult<>(history);

        when(reliabilityServiceMock.getJobExecutionHistory(jobName))
                .thenReturn(dataResult);

        IDataResult<List<JobExecutionHistoryDtoResponse>> response =
                reliabilityResource.getExecutionHistory(jobName);

        assertNotNull(response, "La respuesta no debe ser null");
        assertTrue(response.success, "Debe ser un SuccessDataResult");
        assertSame(history, response.data, "Debe devolver la misma lista que el service");
        verify(reliabilityServiceMock).getJobExecutionHistory(jobName);
    }

    @Test
    void testGetExecutionHistoryError() {
        String jobName = "JOB_ERR";
        IDataResult<List<JobExecutionHistoryDtoResponse>> errorResult =
                new ErrorDataResult<>(null, "500", "Error interno");
        when(reliabilityServiceMock.getJobExecutionHistory(jobName))
                .thenReturn(errorResult);

        IDataResult<List<JobExecutionHistoryDtoResponse>> response =
                reliabilityResource.getExecutionHistory(jobName);

        assertFalse(response.success, "Debe ser un ErrorDataResult");
        assertNull(response.data, "En error la data debe ser null");
        assertEquals("500", response.status);
        verify(reliabilityServiceMock).getJobExecutionHistory(jobName);
    }

    @Test
    void testSn2OptionsSuccess() {
        int sn1 = 99;
        DropDownDto dto1 = new DropDownDto();
        dto1.setValue(10);
        dto1.setLabel("Opción A");
        DropDownDto dto2 = new DropDownDto();
        dto2.setValue(20);
        dto2.setLabel("Opción B");
        List<DropDownDto> opts = Arrays.asList(dto1, dto2);

        IDataResult<List<DropDownDto>> dataResult = new SuccessDataResult<>(opts);
        when(reliabilityServiceMock.getSn2Options(sn1)).thenReturn(dataResult);

        IDataResult<List<DropDownDto>> result = reliabilityResource.sn2Options(sn1);

        assertNotNull(result);
        assertEquals(2, result.data.size());
        assertEquals(10, result.data.get(0).getValue());
        assertEquals("Opción A", result.data.get(0).getLabel());
        verify(reliabilityServiceMock).getSn2Options(sn1);
    }

    @Test
    void testSn2OptionsError() {
        int sn1 = 42;
        IDataResult<List<DropDownDto>> errorResult = new ErrorDataResult<>(null, "500", "Error interno");
        when(reliabilityServiceMock.getSn2Options(sn1)).thenReturn(errorResult);

        IDataResult<List<DropDownDto>> result = reliabilityResource.sn2Options(sn1);

        assertFalse(result.success);
        assertNull(result.data);
        assertEquals("500", result.status);
        verify(reliabilityServiceMock).getSn2Options(sn1);
    }

    @Test
    void testGetReliabilityPacksV2_UsaRoleDelBody_siViene() {
        var dto = new ReliabilityPackInputFilterRequest();
        dto.setRole("SM"); // body manda SM
        dto.setTab("EN_PROGRESO");

        var mockResp = new PaginationReliabilityPackResponse();
        var dataResult = new SuccessDataResult<>(mockResp);
        ArgumentCaptor<ReliabilityPackInputFilterRequest> captor =
                ArgumentCaptor.forClass(ReliabilityPackInputFilterRequest.class);

        when(reliabilityServiceMock.getReliabilityPacksAdvanced(any()))
                .thenReturn(dataResult);

        var result = reliabilityResource.getReliabilityPacksV2(dto, "KM"); // header dice KM

        assertNotNull(result);
        assertSame(mockResp, result.data);

        verify(reliabilityServiceMock).getReliabilityPacksAdvanced(captor.capture());
        var dtoEnviado = captor.getValue();
        assertEquals("SM", dtoEnviado.getRole(), "Debe respetar el role del body (tiene precedencia sobre header)");
        assertEquals("EN_PROGRESO", dtoEnviado.getTab());
    }

    @Test
    void testGetReliabilityPacksV2_FallbackAlHeader_siBodyNoTraeRole() {
        var dto = new ReliabilityPackInputFilterRequest();
        dto.setRole(""); // body vacío
        dto.setTab("APROBADOS");

        var mockResp = new PaginationReliabilityPackResponse();
        var dataResult = new SuccessDataResult<>(mockResp);
        ArgumentCaptor<ReliabilityPackInputFilterRequest> captor =
                ArgumentCaptor.forClass(ReliabilityPackInputFilterRequest.class);

        when(reliabilityServiceMock.getReliabilityPacksAdvanced(any()))
                .thenReturn(dataResult);

        var result = reliabilityResource.getReliabilityPacksV2(dto, "KM"); // header KM

        assertNotNull(result);
        assertSame(mockResp, result.data);

        verify(reliabilityServiceMock).getReliabilityPacksAdvanced(captor.capture());
        var dtoEnviado = captor.getValue();
        assertEquals("KM", dtoEnviado.getRole(), "Si body no trae role, debe usar el header");
        assertEquals("APROBADOS", dtoEnviado.getTab());
    }

    @Test
    void testGetReliabilityPacksV2_SinRoleEnBodyNiHeader_pasaNull() {
        var dto = new ReliabilityPackInputFilterRequest(); // role null
        dto.setTab("EN_PROGRESO");

        var mockResp = new PaginationReliabilityPackResponse();
        var dataResult = new SuccessDataResult<>(mockResp);
        ArgumentCaptor<ReliabilityPackInputFilterRequest> captor =
                ArgumentCaptor.forClass(ReliabilityPackInputFilterRequest.class);

        when(reliabilityServiceMock.getReliabilityPacksAdvanced(any()))
                .thenReturn(dataResult);

        var result = reliabilityResource.getReliabilityPacksV2(dto, null);

        assertNotNull(result);

        verify(reliabilityServiceMock).getReliabilityPacksAdvanced(captor.capture());
        var dtoEnviado = captor.getValue();
        assertNull(dtoEnviado.getRole(), "Sin body ni header, debe quedar null");
        assertEquals("EN_PROGRESO", dtoEnviado.getTab());
    }

    @Test
    void testGetReliabilityPacksV2_PropagaErrorDelService() {
        var dto = new ReliabilityPackInputFilterRequest();
        dto.setRole("KM");
        dto.setTab("APROBADOS");

        IDataResult<PaginationReliabilityPackResponse> error =
                new ErrorDataResult<>(null, "500", "Fallo");

        when(reliabilityServiceMock.getReliabilityPacksAdvanced(any()))
                .thenReturn(error);

        var result = reliabilityResource.getReliabilityPacksV2(dto, null);

        assertFalse(result.success);
        assertNull(result.data);
        assertEquals("500", result.status);
        assertEquals("Fallo", result.message);
        verify(reliabilityServiceMock).getReliabilityPacksAdvanced(any());
    }

    @Test
    void testChangeTransferStatus_DelegatesToService_ok() {
        String pack = "PACK_123";
        var req = new TransferStatusChangeRequest();
        req.setActorRole("KM");
        req.setAction("APROBAR");

        var svcResp = TransferStatusChangeResponse.builder()
                .pack(pack).oldStatus(3).newStatus(4).build();
        var dataResult = new SuccessDataResult<>(svcResp, "Estado actualizado");

        ArgumentCaptor<String> packCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<TransferStatusChangeRequest> reqCaptor = ArgumentCaptor.forClass(TransferStatusChangeRequest.class);

        when(reliabilityServiceMock.changeTransferStatus(anyString(), any()))
                .thenReturn(dataResult);

        var result = reliabilityResource.changeTransferStatus(pack, req);

        assertNotNull(result);
        assertTrue(result.success);
        assertEquals("Estado actualizado", result.message);
        assertNotNull(result.data);
        assertEquals(pack, result.data.getPack());
        assertEquals(3, result.data.getOldStatus());
        assertEquals(4, result.data.getNewStatus());

        verify(reliabilityServiceMock).changeTransferStatus(packCaptor.capture(), reqCaptor.capture());
        assertEquals("PACK_123", packCaptor.getValue());
        assertEquals("KM", reqCaptor.getValue().getActorRole());
        assertEquals("APROBAR", reqCaptor.getValue().getAction());
    }

    @Test
    void testChangeTransferStatus_DelegatesToService_error() {
        String pack = "PACK_404";
        var req = new TransferStatusChangeRequest();
        req.setActorRole("KM");
        req.setAction("APROBAR");

        var error = new ErrorDataResult<TransferStatusChangeResponse>(null, "404", "Pack no encontrado");

        when(reliabilityServiceMock.changeTransferStatus(anyString(), any()))
                .thenReturn(error);

        var result = reliabilityResource.changeTransferStatus(pack, req);

        assertNotNull(result);
        assertFalse(result.success);
        assertNull(result.data);
        assertEquals("404", result.status);
        assertEquals("Pack no encontrado", result.message);

        verify(reliabilityServiceMock).changeTransferStatus(eq("PACK_404"), any(TransferStatusChangeRequest.class));
    }

    @Test
    void testUpdateJobBySm_UsaPathYHeader_actorFallback() {
        String pack = "PACK_X";
        String job = "JOB_X";
        String headerRole = "SM";

        UpdateJobDtoRequest body = new UpdateJobDtoRequest(); // sin actorRole -> debe usar header
        IDataResult<Void> ok = new SuccessDataResult<>(null, "Job actualizado");

        ArgumentCaptor<UpdateJobDtoRequest> captor = ArgumentCaptor.forClass(UpdateJobDtoRequest.class);
        when(reliabilityServiceMock.updateJobBySm(any())).thenReturn(ok);

        IDataResult<Void> resp = reliabilityResource.updateJobBySm(pack, job, body, headerRole);

        assertNotNull(resp);
        assertTrue(resp.success);
        verify(reliabilityServiceMock).updateJobBySm(captor.capture());

        UpdateJobDtoRequest enviado = captor.getValue();
        assertEquals(pack, enviado.getPack());
        assertEquals(job, enviado.getJobName());
        assertEquals("SM", enviado.getActorRole(), "Debe tomar el rol del header");
    }

    @Test
    void testUpdateJobBySm_PropagaErrorDelService() {
        UpdateJobDtoRequest body = new UpdateJobDtoRequest();
        when(reliabilityServiceMock.updateJobBySm(any()))
                .thenReturn(new ErrorDataResult<>(null, "409", "Solo se puede editar cuando fue devuelto"));

        IDataResult<Void> resp = reliabilityResource.updateJobBySm("P1", "J1", body, "SM");

        assertFalse(resp.success);
        assertEquals("409", resp.status);
        assertEquals("Solo se puede editar cuando fue devuelto", resp.message);
        verify(reliabilityServiceMock).updateJobBySm(any());
    }

    @Test
    void testUpdateCommentsForPack_UsaHeaderSiBodyNoTraeActor() {
        String pack = "PACK_C";
        UpdateJobCommentsRequest body = new UpdateJobCommentsRequest();
        body.setComments("nota de KM");
        String headerRole = "KM";

        IDataResult<Void> ok = new SuccessDataResult<>(null, "Comentarios actualizados");
        when(reliabilityServiceMock.updateCommentsForPack(pack, headerRole, "nota de KM"))
                .thenReturn(ok);

        IDataResult<Void> resp = reliabilityResource.updateCommentsForPack(pack, body, headerRole);

        assertTrue(resp.success);
        verify(reliabilityServiceMock).updateCommentsForPack(pack, "KM", "nota de KM");
    }

    @Test
    void testUpdateCommentsForPack_UsaActorDelBodySiViene() {
        String pack = "PACK_C2";
        UpdateJobCommentsRequest body = new UpdateJobCommentsRequest();
        body.setActorRole("KM");
        body.setComments("otra nota");

        when(reliabilityServiceMock.updateCommentsForPack(pack, "KM", "otra nota"))
                .thenReturn(new SuccessDataResult<>(null, "ok"));

        IDataResult<Void> resp = reliabilityResource.updateCommentsForPack(pack, body, "SM"); // header distinto

        assertTrue(resp.success);
        verify(reliabilityServiceMock).updateCommentsForPack(pack, "KM", "otra nota");
    }

    @Test
    void testGetTransferDetail_success() {
        String pack = "PACK_OK";
        TransferDetailResponse detail = new TransferDetailResponse();
        IDataResult<TransferDetailResponse> dataResult = new SuccessDataResult<>(detail);

        when(reliabilityServiceMock.getTransferDetail(pack)).thenReturn(dataResult);

        IDataResult<TransferDetailResponse> result = reliabilityResource.getTransferDetail(pack);

        assertNotNull(result);
        assertTrue(result.success);
        assertSame(detail, result.data);
        verify(reliabilityServiceMock).getTransferDetail(pack);
    }

    @Test
    void testGetTransferDetail_error() {
        String pack = "PACK_404";
        IDataResult<TransferDetailResponse> error =
                new ErrorDataResult<>(null, "404", "Pack no encontrado");

        when(reliabilityServiceMock.getTransferDetail(pack)).thenReturn(error);

        IDataResult<TransferDetailResponse> result = reliabilityResource.getTransferDetail(pack);

        assertNotNull(result);
        assertFalse(result.success);
        assertNull(result.data);
        assertEquals("404", result.status);
        assertEquals("Pack no encontrado", result.message);
        verify(reliabilityServiceMock).getTransferDetail(pack);
    }
}

package com.bbva.service;

import com.bbva.common.HttpStatusCodes;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.dao.ReliabilityDao;
import com.bbva.database.mappers.ReliabilityMapper;
import com.bbva.dto.catalog.response.DropDownDto;
import com.bbva.dto.reliability.request.*;
import com.bbva.dto.reliability.response.*;
import com.bbva.util.policy.TransferStatusPolicy;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.poi.ss.usermodel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import com.bbva.dao.ReliabilityDao.PersistenceException;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReliabilityServiceTest {
    private ReliabilityService reliabilityService;
    private ReliabilityDao reliabilityDaoMock;
    private SqlSessionFactory sqlSessionFactoryMock;
    private SqlSession sqlSessionMock;
    private ReliabilityMapper reliabilityMapperMock;

    @BeforeEach
    void setUp() throws Exception {
        sqlSessionFactoryMock = mock(SqlSessionFactory.class);
        sqlSessionMock = mock(SqlSession.class);
        reliabilityService = new ReliabilityService();
        reliabilityDaoMock = mock(ReliabilityDao.class);
        reliabilityMapperMock = mock(ReliabilityMapper.class);
        var field = ReliabilityService.class.getDeclaredField("reliabilityDao");
        field.setAccessible(true);
        field.set(reliabilityService, reliabilityDaoMock);

        when(sqlSessionFactoryMock.openSession()).thenReturn(sqlSessionMock);
        when(sqlSessionMock.getMapper(ReliabilityMapper.class)).thenReturn(reliabilityMapperMock);
    }

    @Test
    void testInventoryInputsFilterSuccess() {
        InventoryInputsFilterDtoRequest dto = new InventoryInputsFilterDtoRequest();
        InventoryInputsFilterDtoResponse mockResponse = new InventoryInputsFilterDtoResponse();

        when(reliabilityDaoMock.inventoryInputsFilter(dto)).thenReturn(mockResponse);

        IDataResult<InventoryInputsFilterDtoResponse> result = reliabilityService.inventoryInputsFilter(dto);

        assertNotNull(result);
        assertTrue(result.success);
        assertEquals(mockResponse, result.data);
        verify(reliabilityDaoMock).inventoryInputsFilter(dto);
    }

    @Test
    void testUpdateInventoryJobStockSuccess() {
        InventoryJobUpdateDtoRequest dto = new InventoryJobUpdateDtoRequest();

        doNothing().when(reliabilityDaoMock).updateInventoryJobStock(dto);

        IDataResult<Void> result = reliabilityService.updateInventoryJobStock(dto);

        assertNotNull(result);
        assertTrue(result.success);
        assertNull(result.data);
        assertEquals("Job stock updated successfully", result.message);
        verify(reliabilityDaoMock).updateInventoryJobStock(dto);
    }

    @Test
    void testUpdateInventoryJobStockError() {
        InventoryJobUpdateDtoRequest dto = new InventoryJobUpdateDtoRequest();
        doThrow(new RuntimeException("Database error")).when(reliabilityDaoMock).updateInventoryJobStock(dto);

        IDataResult<Void> result = reliabilityService.updateInventoryJobStock(dto);

        assertNotNull(result);
        assertFalse(result.success);
        assertNull(result.data);
        assertEquals("500", result.status);
        assertEquals("Database error", result.message);
        verify(reliabilityDaoMock).updateInventoryJobStock(dto);
    }

    @Test
    void testGetPendingCustodyJobsSuccess() {
        String sdatoolId = "123";
        List<PendingCustodyJobsDtoResponse> mockList = List.of(new PendingCustodyJobsDtoResponse());

        when(reliabilityDaoMock.getPendingCustodyJobs(sdatoolId)).thenReturn(mockList);

        var result = reliabilityService.getPendingCustodyJobs(sdatoolId);

        assertNotNull(result);
        assertTrue(result.success);
        assertEquals(mockList, result.data);
        verify(reliabilityDaoMock).getPendingCustodyJobs(sdatoolId);
    }

    @Test
    void testGetPendingCustodyJobsError() {
        String sdatoolId = "123";
        when(reliabilityDaoMock.getPendingCustodyJobs(sdatoolId)).thenThrow(new RuntimeException("Error fetching jobs"));

        IDataResult<List<PendingCustodyJobsDtoResponse>> result = reliabilityService.getPendingCustodyJobs(sdatoolId);

        assertNotNull(result);
        assertFalse(result.success);
        assertNull(result.data);
        assertEquals(HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, result.status);
        assertEquals("Error fetching jobs", result.message);
        verify(reliabilityDaoMock).getPendingCustodyJobs(sdatoolId);
    }

    @Test
    void testGetProjectCustodyInfoSuccess() {
        String sdatoolId = "123";
        List<ProjectCustodyInfoDtoResponse> mockList = List.of(new ProjectCustodyInfoDtoResponse());

        when(reliabilityDaoMock.getProjectCustodyInfo(sdatoolId)).thenReturn(mockList);

        var result = reliabilityService.getProjectCustodyInfo(sdatoolId);

        assertNotNull(result);
        assertTrue(result.success);
        assertEquals(mockList, result.data);
        verify(reliabilityDaoMock).getProjectCustodyInfo(sdatoolId);
    }

    @Test
    void testGetExecutionValidationSuccess() {
        String jobName = "job1";
        ExecutionValidationDtoResponse mockResponse = new ExecutionValidationDtoResponse();

        when(reliabilityDaoMock.getExecutionValidation(jobName)).thenReturn(mockResponse);

        var result = reliabilityService.getExecutionValidation(jobName);

        assertNotNull(result);
        assertTrue(result.success);
        assertEquals(mockResponse, result.data);
        verify(reliabilityDaoMock).getExecutionValidation(jobName);
    }
    @Test
    void testGetExecutionValidationError() {
        String jobName = "job1";
        when(reliabilityDaoMock.getExecutionValidation(jobName)).thenThrow(new RuntimeException("Error fetching validation"));

        IDataResult<ExecutionValidationDtoResponse> result = reliabilityService.getExecutionValidation(jobName);

        assertNotNull(result);
        assertFalse(result.success);
        assertNull(result.data);
        assertEquals(HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, result.status);
        assertEquals("Error fetching validation", result.message);
        verify(reliabilityDaoMock).getExecutionValidation(jobName);
    }
    @Test
    void testGetProjectCustodyInfoError() {
        String sdatoolId = "123";
        when(reliabilityDaoMock.getProjectCustodyInfo(sdatoolId)).thenThrow(new RuntimeException("Error fetching project info"));

        IDataResult<List<ProjectCustodyInfoDtoResponse>> result = reliabilityService.getProjectCustodyInfo(sdatoolId);

        assertNotNull(result);
        assertFalse(result.success);
        assertNull(result.data);
        assertEquals(HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, result.status);
        assertEquals("Error fetching project info", result.message);
        verify(reliabilityDaoMock).getProjectCustodyInfo(sdatoolId);
    }

    @Test
    void getExecutionValidationAll() {
        List<String> jobNames = Arrays.asList("Job1", "Job2");
        List<ExecutionValidationAllDtoResponse> expectedResults = Arrays.asList(
                new ExecutionValidationAllDtoResponse("Job1", "SUCCESS"),
                new ExecutionValidationAllDtoResponse("Job2", "FAILED")
        );

        when(reliabilityDaoMock.getExecutionValidationAll(jobNames)).thenReturn(expectedResults);

        IDataResult<List<ExecutionValidationAllDtoResponse>> result = reliabilityService.getExecutionValidationAll(jobNames);

        assertTrue(result.success);
        verify(reliabilityDaoMock, times(1)).getExecutionValidationAll(jobNames);
    }

    @Test
    void insertTransfer() {
        TransferInputDtoRequest validDto = new TransferInputDtoRequest();
        validDto.setPack("com.example.package");
        validDto.setDomainId(1);
        validDto.setProductOwnerEmail("po@bbva.com"); // <-- cambio
        validDto.setUseCaseId(200);

        IDataResult<Void> result = reliabilityService.insertTransfer(validDto);

        assertTrue(result.success);
        assertEquals("Transfer insert successfully", result.message);
        verify(reliabilityDaoMock, times(1)).insertTransfer(validDto);
    }

    @Test
    void insertTransferWhenPackIsNull() {
        TransferInputDtoRequest invalidDto = new TransferInputDtoRequest();
        invalidDto.setPack(null);
        invalidDto.setDomainId(1);
        invalidDto.setProductOwnerEmail("po@bbva.com"); // <-- cambio
        invalidDto.setUseCaseId(200);

        IDataResult<Void> result = reliabilityService.insertTransfer(invalidDto);

        assertFalse(result.success);
        assertEquals("Pack must not be null or empty", result.message);
        assertEquals(HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, result.status);
        verify(reliabilityDaoMock, never()).insertTransfer(any());
    }

    @Test
    void insertTransferWhenPackIsEmpty() {
        TransferInputDtoRequest invalidDto = new TransferInputDtoRequest();
        invalidDto.setPack("   ");
        invalidDto.setDomainId(1);
        invalidDto.setProductOwnerEmail("po@bbva.com"); // <-- cambio
        invalidDto.setUseCaseId(200);

        IDataResult<Void> result = reliabilityService.insertTransfer(invalidDto);

        assertFalse(result.success);
        assertEquals("Pack must not be null or empty", result.message);
        verify(reliabilityDaoMock, never()).insertTransfer(any());
    }

    @Test
    void insertTransferWhenDomainIdIsNull() {
        TransferInputDtoRequest invalidDto = new TransferInputDtoRequest();
        invalidDto.setPack("com.example.package");
        invalidDto.setDomainId(null);
        invalidDto.setProductOwnerEmail("po@bbva.com"); // <-- cambio
        invalidDto.setUseCaseId(200);

        IDataResult<Void> result = reliabilityService.insertTransfer(invalidDto);

        assertFalse(result.success);
        assertEquals("DomainId must not be null", result.message);
        verify(reliabilityDaoMock, never()).insertTransfer(any());
    }

    @Test
    void insertTransferWhenProductOwnerUserIdIsNull() {
        TransferInputDtoRequest invalidDto = new TransferInputDtoRequest();
        invalidDto.setPack("com.example.package");
        invalidDto.setDomainId(1);
        invalidDto.setProductOwnerEmail(null);
        invalidDto.setUseCaseId(200);

        IDataResult<Void> result = reliabilityService.insertTransfer(invalidDto);

        assertFalse(result.success);
        assertEquals("ProductOwnerEmail must not be null or empty", result.message); // <-- cambio
        verify(reliabilityDaoMock, never()).insertTransfer(any());
    }

    @Test
    void insertTransferWhenUseCaseIdIsNull() {
        TransferInputDtoRequest invalidDto = new TransferInputDtoRequest();
        invalidDto.setPack("com.example.package");
        invalidDto.setDomainId(1);
        invalidDto.setProductOwnerEmail("po@bbva.com"); // <-- cambio
        invalidDto.setUseCaseId(null);

        IDataResult<Void> result = reliabilityService.insertTransfer(invalidDto);

        assertFalse(result.success);
        assertEquals("UseCaseId must not be null", result.message);
        verify(reliabilityDaoMock, never()).insertTransfer(any());
    }

    @Test
    void insertTransferWhenDaoThrowsException() {
        TransferInputDtoRequest validDto = new TransferInputDtoRequest();
        validDto.setPack("com.example.package");
        validDto.setDomainId(1);
        validDto.setProductOwnerEmail("po@bbva.com"); // <-- cambio
        validDto.setUseCaseId(200);

        doThrow(new RuntimeException("Database error")).when(reliabilityDaoMock).insertTransfer(validDto);

        IDataResult<Void> result = reliabilityService.insertTransfer(validDto);

        assertFalse(result.success);
        assertEquals("500", result.status);
        assertTrue(result.message.contains("Database error"));
        verify(reliabilityDaoMock, times(1)).insertTransfer(validDto);
    }

    @Test
    void testGenerateDocumentInventory() throws Exception {
        // Prepara datos simulados que devuelve el mapper
        InventoryInputsDtoResponse inventory = new InventoryInputsDtoResponse();
        inventory.setDomainName("Dom1");
        inventory.setUseCase("UseCase1");
        inventory.setJobName("Job1");
        inventory.setComponentName("Comp1");
        inventory.setJobType("Type1");
        inventory.setIsCritical("Yes");
        inventory.setFrequency("Daily");
        inventory.setInputPaths("input1\ninput2");
        inventory.setOutputPath("output1");
        inventory.setPack("Pack1");

        List<InventoryInputsDtoResponse> mockList = List.of(
                inventory,
                new InventoryInputsDtoResponse(),
                new InventoryInputsDtoResponse()
        );

        when(reliabilityDaoMock.listinventory(any())).thenReturn(mockList);

        InventoryInputsFilterDtoRequest dto = new InventoryInputsFilterDtoRequest();

        byte[] result = reliabilityService.generateDocumentInventory(dto);

        assertNotNull(result);
        assertTrue(result.length > 0);

        // Verifica que el Excel contenga los datos esperados
        try (var workbook = WorkbookFactory.create(new ByteArrayInputStream(result))) {
            var sheet = workbook.getSheet("Inventario");
            assertNotNull(sheet);
            var row = sheet.getRow(1);
            assertEquals("Dom1", row.getCell(0).getStringCellValue());
            assertEquals("UseCase1", row.getCell(1).getStringCellValue());
            assertEquals("", row.getCell(2).getStringCellValue());
            assertEquals("Job1", row.getCell(3).getStringCellValue());
            assertEquals("Comp1", row.getCell(4).getStringCellValue());
            assertEquals("Type1", row.getCell(5).getStringCellValue());
            assertEquals("Yes", row.getCell(6).getStringCellValue());
            assertEquals("Daily", row.getCell(7).getStringCellValue());
            assertEquals("input1\ninput2", row.getCell(8).getStringCellValue());
            assertEquals("output1", row.getCell(9).getStringCellValue());
            assertEquals("Pack1", row.getCell(10).getStringCellValue());
        }
    }

    @Test
    void testGetReliabilityPacks() {
        ReliabilityPackInputFilterRequest validDto = new ReliabilityPackInputFilterRequest();
        validDto.setDomainName("com.example.package");
        validDto.setPage(1);
        validDto.setRecordsAmount(100);
        validDto.setUseCase("UseCase1");

        IDataResult<PaginationReliabilityPackResponse> result = reliabilityService.getReliabilityPacks(validDto);

        assertTrue(result.success);
        verify(reliabilityDaoMock, times(1)).getReliabilityPacks(any());
    }

    @Test
    void testGetReliabilityPacksException() {
        ReliabilityPackInputFilterRequest validDto = new ReliabilityPackInputFilterRequest();
        validDto.setDomainName("DomainName");
        validDto.setPage(1);
        validDto.setRecordsAmount(100);
        validDto.setUseCase("UseCase2");

        doThrow(new RuntimeException("Database error")).when(reliabilityDaoMock).getReliabilityPacks(validDto);
        IDataResult<PaginationReliabilityPackResponse> result = reliabilityService.getReliabilityPacks(validDto);
        assertFalse(result.success);
        verify(reliabilityDaoMock, times(1)).getReliabilityPacks(any());
    }

    @Test
    void testUpdateStatusReliabilityPacksJobStock() {
        List<String> packs = List.of("pack1", "pack2", "pack3", "pack4");
        IDataResult<Void> result = reliabilityService.updateStatusReliabilityPacksJobStock(packs);

        assertTrue(result.success);
        verify(reliabilityDaoMock, times(1)).updateStatusReliabilityPacksJobStock(any());
    }

    @Test
    void testUpdateStatusReliabilityPacksJobStockException() {
        List<String> packs = List.of("pack1", "pack2", "pack3", "pack4");

        doThrow(new RuntimeException("Database error")).when(reliabilityDaoMock).updateStatusReliabilityPacksJobStock(packs);
        IDataResult<Void> result = reliabilityService.updateStatusReliabilityPacksJobStock(packs);
        assertFalse(result.success);
        verify(reliabilityDaoMock, times(1)).updateStatusReliabilityPacksJobStock(any());
    }

    private static void assertOk(IDataResult<?> r){ assertNotNull(r); assertTrue(r.success); }
    private static void assertErr(IDataResult<?> r, String code){ assertNotNull(r); assertFalse(r.success); assertEquals(code, r.status); }

    @Test
    void originTypes_ok_y_error() {
        List<DropDownDto> mockOrigins = List.of();
        when(reliabilityDaoMock.getOriginTypes()).thenReturn(mockOrigins);
        var ok = reliabilityService.getOriginTypes();
        assertOk(ok);
        assertEquals(mockOrigins, ok.data);
        verify(reliabilityDaoMock).getOriginTypes();

        when(reliabilityDaoMock.getOriginTypes()).thenThrow(new RuntimeException("DB down"));
        var err = reliabilityService.getOriginTypes();
        assertErr(err, "500");
        assertTrue(err.message.contains("DB down"));
        verify(reliabilityDaoMock, times(2)).getOriginTypes();
    }

    @Test
    void jobExecutionHistory_ok_y_error() {
        String okName = "JOB_OK";
        var dto = new JobExecutionHistoryDtoResponse();
        var list = List.of(dto);
        when(reliabilityDaoMock.getJobExecutionHistory(okName)).thenReturn(list);
        var ok = reliabilityService.getJobExecutionHistory(okName);
        assertOk(ok);
        assertSame(list, ok.data);
        verify(reliabilityDaoMock).getJobExecutionHistory(okName);

        String badName = "JOB_FAIL";
        when(reliabilityDaoMock.getJobExecutionHistory(badName)).thenThrow(new RuntimeException("DB down"));
        var err = reliabilityService.getJobExecutionHistory(badName);
        assertErr(err, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR);
        assertNull(err.data);
        assertEquals("DB down", err.message);
        verify(reliabilityDaoMock).getJobExecutionHistory(badName);
    }

    @Test
    void sn2Options_ok_y_error() {
        Integer sn1 = 1027;
        var raw1 = new RawSn2DtoResponse(); raw1.setValue(85226); raw1.setRawDesc("DEDRRCM-COLLECTION & MITIGATION-85226");
        var raw2 = new RawSn2DtoResponse(); raw2.setValue(85225); raw2.setRawDesc("DEDRRA-RISK ANALYTICS-85225");
        when(reliabilityDaoMock.fetchRawSn2BySn1(sn1)).thenReturn(List.of(raw1, raw2));
        var ok = reliabilityService.getSn2Options(sn1);
        assertOk(ok);
        assertEquals(2, ok.data.size());
        assertEquals(85226, ok.data.get(0).getValue());
        assertEquals("COLLECTION & MITIGATION", ok.data.get(0).getLabel());
        assertEquals(85225, ok.data.get(1).getValue());
        assertEquals("RISK ANALYTICS", ok.data.get(1).getLabel());
        verify(reliabilityDaoMock).fetchRawSn2BySn1(sn1);

        Integer bad = 9006;
        when(reliabilityDaoMock.fetchRawSn2BySn1(bad)).thenThrow(new RuntimeException("DB error"));
        var err = reliabilityService.getSn2Options(bad);
        assertErr(err, "500");
        assertNull(err.data);
        assertTrue(err.message.contains("DB error"));
        verify(reliabilityDaoMock).fetchRawSn2BySn1(bad);
    }

    @Test
    void packsAdvanced_mapeaCsv_compacto_sin_params() {
        String[][] casos = {
                {"KM",  "EN_PROGRESO", "2,5"},
                {"KM",  "APROBADOS",   "1"},
                {"SM",  "EN_PROGRESO", "3,2,4,5"},
                {"SM",  "APROBADOS",   "1"},
                {"???", "EN_PROGRESO", "2,5"}
        };
        for (String[] c : casos) {
            String role = c[0], tab = c[1], csv = c[2];
            var dto = new ReliabilityPackInputFilterRequest();
            dto.setRole(role);
            dto.setTab(tab);

            try (MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
                mocked.when(() -> TransferStatusPolicy.toCsv(role, tab)).thenReturn(csv);

                // Stub explícito del filtro para evitar NPE por helpers privados
                mocked.when(() -> TransferStatusPolicy.buildPacksFilter(
                                org.mockito.Mockito.anyString(),
                                org.mockito.Mockito.anyString(),
                                org.mockito.Mockito.<java.util.Set<String>>any()))
                        .thenAnswer(inv -> {
                            String roleArg = inv.getArgument(0);
                            String emailArg = inv.getArgument(1);
                            java.util.Set<String> allowed = inv.getArgument(2);
                            return (java.util.function.Predicate<ReliabilityPacksDtoResponse>) row -> {
                                if ("KM".equalsIgnoreCase(roleArg)) {
                                    return allowed != null && allowed.contains(row.getDomainName());
                                } else if ("PO".equalsIgnoreCase(roleArg)) {
                                    String poe = row.getProductOwnerEmail();
                                    return emailArg != null && poe != null && emailArg.equalsIgnoreCase(poe);
                                } else if ("SM".equalsIgnoreCase(roleArg)) {
                                    String cu = row.getCreatorUser();
                                    return emailArg != null && cu != null && emailArg.equalsIgnoreCase(cu);
                                } else {
                                    return true; // NA/???
                                }
                            };
                        });

                mocked.when(() -> TransferStatusPolicy.computeCambieditFlag(
                                org.mockito.Mockito.anyBoolean(),
                                org.mockito.Mockito.anyString(),
                                org.mockito.Mockito.<Integer>any()))
                        .thenCallRealMethod();

                when(reliabilityDaoMock.listTransfersByStatus("", "", csv))
                        .thenReturn(Collections.emptyList());
                if ("KM".equals(role)) {
                    when(reliabilityDaoMock.getKmAllowedDomainNames("")).thenReturn(Collections.emptyList());
                }

                var res = reliabilityService.getReliabilityPacksAdvanced(dto, "");
                assertTrue(res.success);
                verify(reliabilityDaoMock).listTransfersByStatus("", "", csv);
                if ("KM".equals(role)) {
                    verify(reliabilityDaoMock).getKmAllowedDomainNames("");
                }
                reset(reliabilityDaoMock);
            }
        }
    }

    @Test
    void changeTransferStatus_todos() {
        String pack = "PACK1";
        var req = new TransferStatusChangeRequest(); req.setActorRole("SM"); req.setAction("APPROVE");
        when(reliabilityDaoMock.getPackCurrentStatus(pack)).thenReturn(TransferStatusPolicy.EN_PROGRESO);
        try (org.mockito.MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.computeNextStatusOrThrow("SM", TransferStatusPolicy.EN_PROGRESO, TransferStatusPolicy.Action.APPROVE))
                    .thenReturn(TransferStatusPolicy.APROBADO_PO);
            doNothing().when(reliabilityDaoMock).changeTransferStatus(pack, TransferStatusPolicy.APROBADO_PO);
            var ok = reliabilityService.changeTransferStatus(pack, req);
            assertOk(ok);
            assertNotNull(ok.data);
            assertEquals(TransferStatusPolicy.EN_PROGRESO, ok.data.getOldStatus());
            assertEquals(TransferStatusPolicy.APROBADO_PO, ok.data.getNewStatus());
            verify(reliabilityDaoMock).changeTransferStatus(pack, TransferStatusPolicy.APROBADO_PO);
        }

        when(reliabilityDaoMock.getPackCurrentStatus("P404")).thenReturn(null);
        assertErr(reliabilityService.changeTransferStatus("P404", req), "404");

        when(reliabilityDaoMock.getPackCurrentStatus("P1")).thenReturn(3);
        var bad = new TransferStatusChangeRequest(); bad.setActorRole("SM"); bad.setAction("fooBAR");
        assertErr(reliabilityService.changeTransferStatus("P1", bad), "400");
        verify(reliabilityDaoMock, never()).changeTransferStatus(eq("P1"), anyInt());

        when(reliabilityDaoMock.getPackCurrentStatus("P2")).thenReturn(2);
        var req2 = new TransferStatusChangeRequest(); req2.setActorRole("SM"); req2.setAction("APPROVE");
        try (org.mockito.MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.computeNextStatusOrThrow("SM", 2, TransferStatusPolicy.Action.APPROVE))
                    .thenThrow(new IllegalArgumentException("Transición no permitida"));
            assertErr(reliabilityService.changeTransferStatus("P2", req2), "409");
        }

        when(reliabilityDaoMock.getPackCurrentStatus("P3")).thenReturn(3);
        try (org.mockito.MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.computeNextStatusOrThrow("SM", 3, TransferStatusPolicy.Action.APPROVE))
                    .thenReturn(2);
            doThrow(new RuntimeException("DB down")).when(reliabilityDaoMock).changeTransferStatus("P3", 2);
            assertErr(reliabilityService.changeTransferStatus("P3", req), "500");
        }
    }

    @Test
    void updateJobBySm_edicion_todos() {
        var dtoOk = new UpdateJobDtoRequest(); dtoOk.setActorRole("SM"); dtoOk.setPack("P1"); dtoOk.setJobName("JOB_A");
        when(reliabilityDaoMock.getPackCurrentStatus("P1")).thenReturn(TransferStatusPolicy.DEVUELTO_PO);
        try (org.mockito.MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.canEdit("SM", TransferStatusPolicy.DEVUELTO_PO)).thenReturn(1);
            doNothing().when(reliabilityDaoMock).updateJobByPackAndName(dtoOk);
            assertOk(reliabilityService.updateJobBySm(dtoOk));
        }

        var dtoForbidden = new UpdateJobDtoRequest(); dtoForbidden.setActorRole("SM"); dtoForbidden.setPack("P1"); dtoForbidden.setJobName("JOB_A");
        when(reliabilityDaoMock.getPackCurrentStatus("P1")).thenReturn(TransferStatusPolicy.EN_PROGRESO);
        try (org.mockito.MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.canEdit("SM", TransferStatusPolicy.EN_PROGRESO)).thenReturn(0);
            assertErr(reliabilityService.updateJobBySm(dtoForbidden), "409");
        }

        var dto404 = new UpdateJobDtoRequest(); dto404.setActorRole("SM"); dto404.setPack("P404");
        when(reliabilityDaoMock.getPackCurrentStatus("P404")).thenReturn(null);
        assertErr(reliabilityService.updateJobBySm(dto404), "404");

        var dtoDao404 = new UpdateJobDtoRequest(); dtoDao404.setActorRole("SM"); dtoDao404.setPack("P2");
        when(reliabilityDaoMock.getPackCurrentStatus("P2")).thenReturn(TransferStatusPolicy.DEVUELTO_RLB);
        try (org.mockito.MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.canEdit("SM", TransferStatusPolicy.DEVUELTO_RLB)).thenReturn(1);
            doThrow(new PersistenceException("No se encontró el job", null)).when(reliabilityDaoMock).updateJobByPackAndName(dtoDao404);
            assertErr(reliabilityService.updateJobBySm(dtoDao404), "404");
        }

        var dtoDao500 = new UpdateJobDtoRequest(); dtoDao500.setActorRole("SM"); dtoDao500.setPack("P3");
        when(reliabilityDaoMock.getPackCurrentStatus("P3")).thenReturn(TransferStatusPolicy.DEVUELTO_RLB);
        try (org.mockito.MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.canEdit("SM", TransferStatusPolicy.DEVUELTO_RLB)).thenReturn(1);
            doThrow(new RuntimeException("DB down")).when(reliabilityDaoMock).updateJobByPackAndName(dtoDao500);
            assertErr(reliabilityService.updateJobBySm(dtoDao500), "500");
        }
    }

    @Test
    void updateJobBySm_soloComentarios_ok_y_forbidden() {
        var dto = new UpdateJobDtoRequest();
        dto.setActorRole("SM"); dto.setPack("P1"); dto.setJobName("JOB_X"); dto.setComments("Nueva nota");

        when(reliabilityDaoMock.getPackCurrentStatus("P1")).thenReturn(TransferStatusPolicy.EN_PROGRESO);
        try (MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.canWriteJobComment("SM", 3)).thenReturn(1);
            doNothing().when(reliabilityDaoMock).updateJobComment("P1", "JOB_X", "Nueva nota");
            var ok = reliabilityService.updateJobBySm(dto);
            assertTrue(ok.success);
            verify(reliabilityDaoMock).updateJobComment("P1", "JOB_X", "Nueva nota");
            verify(reliabilityDaoMock, never()).updateJobByPackAndName(any());
        }

        reset(reliabilityDaoMock);

        when(reliabilityDaoMock.getPackCurrentStatus("P1")).thenReturn(TransferStatusPolicy.EN_PROGRESO);
        try (MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.canWriteJobComment("SM", 3)).thenReturn(0);
            var err = reliabilityService.updateJobBySm(dto);
            assertFalse(err.success);
            assertEquals("409", err.status);
            assertEquals("No puedes comentar este job en este estado", err.message);
            verify(reliabilityDaoMock, never()).updateJobComment(any(), any(), any());
            verify(reliabilityDaoMock, never()).updateJobByPackAndName(any());
        }
    }

    @Test
    void updateCommentsForPack_todos() {
        when(reliabilityDaoMock.getPackCurrentStatus("P2")).thenReturn(TransferStatusPolicy.APROBADO_PO);
        try (org.mockito.MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.canWriteGeneralComment("KM", TransferStatusPolicy.APROBADO_PO)).thenReturn(1);
            doNothing().when(reliabilityDaoMock).updatePackComments("P2", "nota");
            var ok = reliabilityService.updateCommentsForPack("P2", "KM", "nota");
            assertOk(ok);
            assertEquals("Comentarios actualizados", ok.message);
            verify(reliabilityDaoMock).updatePackComments("P2", "nota");

            mocked.when(() -> TransferStatusPolicy.canWriteGeneralComment("KM", TransferStatusPolicy.APROBADO_PO)).thenReturn(0);
            assertErr(reliabilityService.updateCommentsForPack("P2", "KM", "x"), "409");
        }

        when(reliabilityDaoMock.getPackCurrentStatus("P404")).thenReturn(null);
        assertErr(reliabilityService.updateCommentsForPack("P404", "KM", "x"), "404");

        when(reliabilityDaoMock.getPackCurrentStatus("P2")).thenReturn(TransferStatusPolicy.APROBADO_PO);
        try (org.mockito.MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.canWriteGeneralComment("KM", TransferStatusPolicy.APROBADO_PO)).thenReturn(1);
            doThrow(new PersistenceException("Pack sin jobs para comentar", null)).when(reliabilityDaoMock).updatePackComments("P2", "x");
            assertErr(reliabilityService.updateCommentsForPack("P2", "KM", "x"), "404");

            doThrow(new RuntimeException("DB down")).when(reliabilityDaoMock).updatePackComments("P2", "y");
            assertErr(reliabilityService.updateCommentsForPack("P2", "KM", "y"), "500");
        }
    }

    @Test
    void getTransferDetail_todos() {
        when(reliabilityDaoMock.getTransferDetail("OK")).thenReturn(new TransferDetailResponse());
        assertOk(reliabilityService.getTransferDetail("OK"));
        when(reliabilityDaoMock.getTransferDetail("404")).thenReturn(null);
        assertErr(reliabilityService.getTransferDetail("404"), "404");
        when(reliabilityDaoMock.getTransferDetail("ERR")).thenThrow(new RuntimeException("DB down"));
        var err = reliabilityService.getTransferDetail("ERR");
        assertErr(err, "500");
        assertTrue(err.message.contains("DB down"));
    }

    @Test
    void updateTransferDetail_todos() {
        when(reliabilityDaoMock.getPackCurrentStatus("PX")).thenReturn(null);
        assertErr(reliabilityService.updateTransferDetail("PX", "SM", new TransferDetailUpdateRequest()), "404");

        when(reliabilityDaoMock.getPackCurrentStatus("P1")).thenReturn(TransferStatusPolicy.EN_PROGRESO);
        var dto1 = new TransferDetailUpdateRequest();
        var h1 = new TransferDetailUpdateRequest.Header(); h1.setDomainId(1); dto1.setHeader(h1);
        var r1 = reliabilityService.updateTransferDetail("P1", "SM", dto1);
        assertErr(r1, "409");
        assertTrue(r1.message.contains("solo se permiten comentarios"));

        when(reliabilityDaoMock.getPackCurrentStatus("P2")).thenReturn(TransferStatusPolicy.APROBADO_PO);
        var dto2 = new TransferDetailUpdateRequest();
        var h2 = new TransferDetailUpdateRequest.Header(); h2.setComments("ok"); h2.setDomainId(123); dto2.setHeader(h2);
        var r2 = reliabilityService.updateTransferDetail("P2", "KM", dto2);
        assertErr(r2, "409");
        assertTrue(r2.message.contains("KM solo puede enviar comentarios"));

        when(reliabilityDaoMock.getPackCurrentStatus("P3")).thenReturn(TransferStatusPolicy.EN_PROGRESO);
        var dtoOk = new TransferDetailUpdateRequest();
        var hOk = new TransferDetailUpdateRequest.Header(); hOk.setComments("solo comentario"); dtoOk.setHeader(hOk);
        when(reliabilityDaoMock.getTransferDetail("P3")).thenReturn(new TransferDetailResponse());
        assertOk(reliabilityService.updateTransferDetail("P3", "SM", dtoOk));
        verify(reliabilityDaoMock).updateTransferDetail("P3", dtoOk);
        verify(reliabilityDaoMock).getTransferDetail("P3");

        when(reliabilityDaoMock.getPackCurrentStatus("P4")).thenReturn(1);
        var dtoErr = new TransferDetailUpdateRequest();
        doThrow(new RuntimeException("DB down")).when(reliabilityDaoMock).updateTransferDetail("P4", dtoErr);
        var err = reliabilityService.updateTransferDetail("P4", "SM", dtoErr);
        assertErr(err, "500");
        assertTrue(err.message.contains("DB down"));
    }

    @Test
    void packsAdvanced_tabAprobados_dejaReadonlyEnCero() {
        var dto = new ReliabilityPackInputFilterRequest();
        dto.setRole("KM");
        dto.setTab("APROBADOS");

        var row = new ReliabilityPacksDtoResponse();
        row.setStatusId(1);
        row.setDomainName("CS");

        when(reliabilityDaoMock.listTransfersByStatus("", "", "CSV")).thenReturn(List.of(row));
        when(reliabilityDaoMock.getKmAllowedDomainNames("")).thenReturn(List.of("CS"));

        try (MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.toCsv("KM", "APROBADOS")).thenReturn("CSV");

            // Stub del filtro
            mocked.when(() -> TransferStatusPolicy.buildPacksFilter(
                            Mockito.anyString(), Mockito.anyString(), Mockito.<Set<String>>any()))
                    .thenAnswer(inv -> {
                        String roleArg = inv.getArgument(0);
                        String emailArg = inv.getArgument(1);
                        Set<String> allowed = inv.getArgument(2);
                        return (java.util.function.Predicate<ReliabilityPacksDtoResponse>) r -> {
                            if ("KM".equalsIgnoreCase(roleArg)) {
                                return allowed != null && allowed.contains(r.getDomainName());
                            } else if ("PO".equalsIgnoreCase(roleArg)) {
                                String poe = r.getProductOwnerEmail();
                                return emailArg != null && poe != null && emailArg.equalsIgnoreCase(poe);
                            } else if ("SM".equalsIgnoreCase(roleArg)) {
                                String cu = r.getCreatorUser();
                                return emailArg != null && cu != null && emailArg.equalsIgnoreCase(cu);
                            } else {
                                return true;
                            }
                        };
                    });

            mocked.when(() -> TransferStatusPolicy.computeCambieditFlag(
                            Mockito.anyBoolean(), Mockito.anyString(), Mockito.<Integer>any()))
                    .thenCallRealMethod();

            var res = reliabilityService.getReliabilityPacksAdvanced(dto, "");
            assertTrue(res.success);
            assertEquals(0, res.data.getData().get(0).getCambiedit());
        }
    }

    @Test
    void packsAdvanced_paginacion_y_sinPaginacion() {
        var dto = new ReliabilityPackInputFilterRequest();
        dto.setRole("NA");
        dto.setTab("X");
        dto.setRecordsAmount(2); dto.setPage(1);

        var r1 = new ReliabilityPacksDtoResponse(); r1.setStatusId(1);
        var r2 = new ReliabilityPacksDtoResponse(); r2.setStatusId(1);
        var r3 = new ReliabilityPacksDtoResponse(); r3.setStatusId(1);

        when(reliabilityDaoMock.listTransfersByStatus("", "", "CSV"))
                .thenReturn(List.of(r1, r2, r3));

        try (MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.toCsv("NA", "X")).thenReturn("CSV");

            // Stub del filtro (role "NA" → permite todos)
            mocked.when(() -> TransferStatusPolicy.buildPacksFilter(
                            org.mockito.Mockito.anyString(),
                            org.mockito.Mockito.anyString(),
                            org.mockito.Mockito.<java.util.Set<String>>any()))
                    .thenAnswer(inv -> {
                        String roleArg = inv.getArgument(0);
                        String emailArg = inv.getArgument(1);
                        java.util.Set<String> allowed = inv.getArgument(2);
                        return (java.util.function.Predicate<ReliabilityPacksDtoResponse>) row -> {
                            if ("KM".equalsIgnoreCase(roleArg)) {
                                return allowed != null && allowed.contains(row.getDomainName());
                            } else if ("PO".equalsIgnoreCase(roleArg)) {
                                String poe = row.getProductOwnerEmail();
                                return emailArg != null && poe != null && emailArg.equalsIgnoreCase(poe);
                            } else if ("SM".equalsIgnoreCase(roleArg)) {
                                String cu = row.getCreatorUser();
                                return emailArg != null && cu != null && emailArg.equalsIgnoreCase(cu);
                            } else {
                                return true; // NA
                            }
                        };
                    });

            mocked.when(() -> TransferStatusPolicy.computeCambieditFlag(
                            org.mockito.Mockito.anyBoolean(),
                            org.mockito.Mockito.anyString(),
                            org.mockito.Mockito.<Integer>any()))
                    .thenCallRealMethod();

            var pag = reliabilityService.getReliabilityPacksAdvanced(dto, "");
            assertTrue(pag.success);
            assertEquals(2, pag.data.getData().size());

            dto.setRecordsAmount(0);
            var nopag = reliabilityService.getReliabilityPacksAdvanced(dto, "");
            assertTrue(nopag.success);
            assertEquals(3, nopag.data.getData().size());
        }
    }
    @Test
    void changeTransferStatus_reqNull_returns400() {
        when(reliabilityDaoMock.getPackCurrentStatus("P")).thenReturn(TransferStatusPolicy.EN_PROGRESO);
        var r = reliabilityService.changeTransferStatus("P", null);
        assertFalse(r.success);
        assertEquals("400", r.status);
    }

    @Test
    void packsAdvanced_daoThrows_returns500() {
        var dto = new ReliabilityPackInputFilterRequest();
        dto.setRole("NA"); dto.setTab("X");
        try (MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.toCsv("NA","X")).thenReturn("CSV");

            mocked.when(() -> TransferStatusPolicy.buildPacksFilter(
                            org.mockito.Mockito.anyString(),
                            org.mockito.Mockito.anyString(),
                            org.mockito.Mockito.<java.util.Set<String>>any()))
                    .thenCallRealMethod();
            mocked.when(() -> TransferStatusPolicy.computeCambieditFlag(
                            org.mockito.Mockito.anyBoolean(),
                            org.mockito.Mockito.anyString(),
                            org.mockito.Mockito.<Integer>any()))
                    .thenCallRealMethod();

            when(reliabilityDaoMock.listTransfersByStatus("", "", "CSV"))
                    .thenThrow(new RuntimeException("DB down"));

            var res = reliabilityService.getReliabilityPacksAdvanced(dto, "");
            assertFalse(res.success);
            assertEquals("500", res.status);
        }
    }

    @Test
    void packsAdvanced_defaults_paginaPorDefecto() {
        var dto = new ReliabilityPackInputFilterRequest();
        dto.setRole("NA"); dto.setTab("X");

        var r1 = new ReliabilityPacksDtoResponse(); r1.setStatusId(1);
        when(reliabilityDaoMock.listTransfersByStatus("", "", "CSV"))
                .thenReturn(List.of(r1));

        try (MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.toCsv("NA", "X")).thenReturn("CSV");

            // Stub del filtro
            mocked.when(() -> TransferStatusPolicy.buildPacksFilter(
                            org.mockito.Mockito.anyString(),
                            org.mockito.Mockito.anyString(),
                            org.mockito.Mockito.<java.util.Set<String>>any()))
                    .thenAnswer(inv -> {
                        String roleArg = inv.getArgument(0);
                        String emailArg = inv.getArgument(1);
                        java.util.Set<String> allowed = inv.getArgument(2);
                        return (java.util.function.Predicate<ReliabilityPacksDtoResponse>) row -> {
                            if ("KM".equalsIgnoreCase(roleArg)) {
                                return allowed != null && allowed.contains(row.getDomainName());
                            } else if ("PO".equalsIgnoreCase(roleArg)) {
                                String poe = row.getProductOwnerEmail();
                                return emailArg != null && poe != null && emailArg.equalsIgnoreCase(poe);
                            } else if ("SM".equalsIgnoreCase(roleArg)) {
                                String cu = row.getCreatorUser();
                                return emailArg != null && cu != null && emailArg.equalsIgnoreCase(cu);
                            } else {
                                return true;
                            }
                        };
                    });

            mocked.when(() -> TransferStatusPolicy.computeCambieditFlag(
                            org.mockito.Mockito.anyBoolean(),
                            org.mockito.Mockito.anyString(),
                            org.mockito.Mockito.<Integer>any()))
                    .thenCallRealMethod();

            var res = reliabilityService.getReliabilityPacksAdvanced(dto, "");
            assertTrue(res.success);
            assertEquals(1, res.data.getData().size());
            assertEquals(1, res.data.getPagesAmount());
        }
    }

    @Test
    void sn2Options_sinGuiones_usaRawCompleto() {
        var raw = new RawSn2DtoResponse(); raw.setValue(1); raw.setRawDesc("SIN-GUIONES? no, sin ambos");
        when(reliabilityDaoMock.fetchRawSn2BySn1(1)).thenReturn(List.of(raw));
        var res = reliabilityService.getSn2Options(1);
        assertTrue(res.success);
        assertEquals("SIN-GUIONES? no, sin ambos", res.data.get(0).getLabel());
    }

    @Test
    void updateTransferDetail_kmAprobadoPo_soloGeneralComment_ok() {
        when(reliabilityDaoMock.getPackCurrentStatus("P")).thenReturn(TransferStatusPolicy.APROBADO_PO);
        var dto = new TransferDetailUpdateRequest();
        var h = new TransferDetailUpdateRequest.Header(); h.setComments("ok"); dto.setHeader(h);
        when(reliabilityDaoMock.getTransferDetail("P")).thenReturn(new TransferDetailResponse());
        assertTrue(reliabilityService.updateTransferDetail("P", "KM", dto).success);
        verify(reliabilityDaoMock).updateTransferDetail("P", dto);
        verify(reliabilityDaoMock).getTransferDetail("P");
    }

    @Test
    void packsAdvanced_noAprobados_seteaCambiedit() {
        var dto = new ReliabilityPackInputFilterRequest();
        dto.setRole("KM"); dto.setTab("EN_PROGRESO");

        var row = new ReliabilityPacksDtoResponse();
        row.setStatusId(2);
        row.setDomainName("CS");

        when(reliabilityDaoMock.listTransfersByStatus("", "", "CSV"))
                .thenReturn(List.of(row));
        when(reliabilityDaoMock.getKmAllowedDomainNames("")).thenReturn(List.of("CS"));

        try (MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.toCsv("KM", "EN_PROGRESO")).thenReturn("CSV");

            // Stub del filtro
            mocked.when(() -> TransferStatusPolicy.buildPacksFilter(
                            org.mockito.Mockito.anyString(),
                            org.mockito.Mockito.anyString(),
                            org.mockito.Mockito.<java.util.Set<String>>any()))
                    .thenAnswer(inv -> {
                        String roleArg = inv.getArgument(0);
                        String emailArg = inv.getArgument(1);
                        java.util.Set<String> allowed = inv.getArgument(2);
                        return (java.util.function.Predicate<ReliabilityPacksDtoResponse>) r -> {
                            if ("KM".equalsIgnoreCase(roleArg)) {
                                return allowed != null && allowed.contains(r.getDomainName());
                            } else if ("PO".equalsIgnoreCase(roleArg)) {
                                String poe = r.getProductOwnerEmail();
                                return emailArg != null && poe != null && emailArg.equalsIgnoreCase(poe);
                            } else if ("SM".equalsIgnoreCase(roleArg)) {
                                String cu = r.getCreatorUser();
                                return emailArg != null && cu != null && emailArg.equalsIgnoreCase(cu);
                            } else {
                                return true;
                            }
                        };
                    });

            mocked.when(() -> TransferStatusPolicy.computeCambieditFlag(
                            org.mockito.Mockito.anyBoolean(),
                            org.mockito.Mockito.anyString(),
                            org.mockito.Mockito.<Integer>any()))
                    .thenCallRealMethod();

            // canEdit mockeado para este caso concreto
            mocked.when(() -> TransferStatusPolicy.canEdit("KM", 2)).thenReturn(1);

            var res = reliabilityService.getReliabilityPacksAdvanced(dto, "");
            assertTrue(res.success);
            assertEquals(1, res.data.getData().get(0).getCambiedit());
        }
    }

    @Test
    void updateTransferDetail_soloComentariosPorJob_ok() {
        when(reliabilityDaoMock.getPackCurrentStatus("P")).thenReturn(TransferStatusPolicy.EN_PROGRESO);
        var dto = new TransferDetailUpdateRequest();
        var j  = new TransferDetailUpdateRequest.Job();
        j.setJobName("J1"); j.setComments("nota");
        dto.setJobs(List.of(j));

        when(reliabilityDaoMock.getTransferDetail("P")).thenReturn(new TransferDetailResponse());

        var res = reliabilityService.updateTransferDetail("P", "SM", dto);
        assertTrue(res.success);
        verify(reliabilityDaoMock).updateTransferDetail("P", dto);
    }

    @Test
    void changeTransferStatus_normalizaAccion() {
        when(reliabilityDaoMock.getPackCurrentStatus("P")).thenReturn(TransferStatusPolicy.EN_PROGRESO);
        var req = new TransferStatusChangeRequest(); req.setActorRole("SM"); req.setAction("  approve  ");

        try (MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.computeNextStatusOrThrow("SM",
                            TransferStatusPolicy.EN_PROGRESO, TransferStatusPolicy.Action.APPROVE))
                    .thenReturn(TransferStatusPolicy.APROBADO_PO);
            var r = reliabilityService.changeTransferStatus("P", req);
            assertTrue(r.success);
        }
    }
    @Test
    void generateDocumentInventory_listaVacia_yNulls() throws Exception {
        when(reliabilityDaoMock.listinventory(any())).thenReturn(List.of(new InventoryInputsDtoResponse()));
        var bytes = reliabilityService.generateDocumentInventory(new InventoryInputsFilterDtoRequest());
        assertTrue(bytes.length > 0);

        try (var wb = WorkbookFactory.create(new ByteArrayInputStream(bytes))) {
            var sh = wb.getSheet("Inventario");
            assertNotNull(sh);
            assertEquals("PACK", sh.getRow(0).getCell(10).getStringCellValue());
            assertNotNull(sh.getRow(1));
            assertEquals("", sh.getRow(1).getCell(8).getStringCellValue());
        }
    }

    @Test
    void getTransferDetail_seteaFrequencyChanged_casosNullIgualDistinto() {
        var d = new TransferDetailResponse();

        var jNull = new TransferDetailResponse.JobRow();
        jNull.setOriginalFrequencyId(null);
        jNull.setFrequencyId(5);

        var jEq = new TransferDetailResponse.JobRow();
        jEq.setOriginalFrequencyId(7);
        jEq.setFrequencyId(7);

        var jDiff = new TransferDetailResponse.JobRow();
        jDiff.setOriginalFrequencyId(1);
        jDiff.setFrequencyId(2);

        d.setJobs(java.util.List.of(jNull, jEq, jDiff));

        when(reliabilityDaoMock.getTransferDetail("PACK")).thenReturn(d);

        var res = reliabilityService.getTransferDetail("PACK");
        assertTrue(res.success);
        var jobs = res.data.getJobs();

        assertEquals(Boolean.FALSE, jobs.get(0).getFrequencyChanged());
        assertEquals(Boolean.FALSE, jobs.get(1).getFrequencyChanged());
        assertEquals(Boolean.TRUE,  jobs.get(2).getFrequencyChanged());
    }
}
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

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
        validDto.setProductOwnerUserId(100);
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
        invalidDto.setProductOwnerUserId(100);
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
        invalidDto.setProductOwnerUserId(100);
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
        invalidDto.setProductOwnerUserId(100);
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
        invalidDto.setProductOwnerUserId(null);
        invalidDto.setUseCaseId(200);

        IDataResult<Void> result = reliabilityService.insertTransfer(invalidDto);

        assertFalse(result.success);
        assertEquals("ProductOwnerUserId must not be null", result.message);
        verify(reliabilityDaoMock, never()).insertTransfer(any());
    }

    @Test
    void insertTransferWhenUseCaseIdIsNull() {
        TransferInputDtoRequest invalidDto = new TransferInputDtoRequest();
        invalidDto.setPack("com.example.package");
        invalidDto.setDomainId(1);
        invalidDto.setProductOwnerUserId(100);
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
        validDto.setProductOwnerUserId(100);
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

    @Test
    void testGetOriginTypesSuccess() {
        // dado
        List<DropDownDto> mockOrigins = List.of();
        when(reliabilityDaoMock.getOriginTypes()).thenReturn(mockOrigins);

        IDataResult<List<DropDownDto>> result = reliabilityService.getOriginTypes();

        assertTrue(result.success);
        assertEquals(mockOrigins, result.data);
        verify(reliabilityDaoMock).getOriginTypes();
    }

    @Test
    void testGetOriginTypesError() {
        when(reliabilityDaoMock.getOriginTypes()).thenThrow(new RuntimeException("DB down"));

        IDataResult<List<DropDownDto>> result = reliabilityService.getOriginTypes();

        assertFalse(result.success);
        assertEquals("500", result.status);
        assertTrue(result.message.contains("DB down"));
        verify(reliabilityDaoMock).getOriginTypes();
    }

    @Test
    void testGetJobExecutionHistorySuccess() {
        String jobName = "JOB_OK";
        JobExecutionHistoryDtoResponse dto = new JobExecutionHistoryDtoResponse();
        List<JobExecutionHistoryDtoResponse> mockHistory = List.of(dto);
        when(reliabilityDaoMock.getJobExecutionHistory(jobName)).thenReturn(mockHistory);

        IDataResult<List<JobExecutionHistoryDtoResponse>> result =
                reliabilityService.getJobExecutionHistory(jobName);

        assertNotNull(result);
        assertTrue(result.success, "Debe ser un SuccessDataResult");
        assertSame(mockHistory, result.data, "Debe devolver la misma lista");
        verify(reliabilityDaoMock).getJobExecutionHistory(jobName);
    }

    @Test
    void testGetJobExecutionHistoryError() {
        String jobName = "JOB_FAIL";
        when(reliabilityDaoMock.getJobExecutionHistory(jobName))
                .thenThrow(new RuntimeException("DB down"));

        IDataResult<List<JobExecutionHistoryDtoResponse>> result =
                reliabilityService.getJobExecutionHistory(jobName);

        assertNotNull(result);
        assertFalse(result.success, "Debe ser un ErrorDataResult");
        assertNull(result.data, "En error, data debe ser null");
        assertEquals(HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, result.status);
        assertEquals("DB down", result.message);
        verify(reliabilityDaoMock).getJobExecutionHistory(jobName);
    }

    @Test
    void testGetSn2OptionsSuccess() {
        Integer sn1 = 1027;
        RawSn2DtoResponse raw1 = new RawSn2DtoResponse();
        raw1.setValue(85226);
        raw1.setRawDesc("DEDRRCM-COLLECTION & MITIGATION-85226");
        RawSn2DtoResponse raw2 = new RawSn2DtoResponse();
        raw2.setValue(85225);
        raw2.setRawDesc("DEDRRA-RISK ANALYTICS-85225");

        when(reliabilityDaoMock.fetchRawSn2BySn1(sn1)).thenReturn(List.of(raw1, raw2));

        IDataResult<List<DropDownDto>> result = reliabilityService.getSn2Options(sn1);

        assertTrue(result.success);
        assertNotNull(result.data);
        assertEquals(2, result.data.size());

        DropDownDto opt1 = result.data.get(0);
        assertEquals(85226, opt1.getValue());
        assertEquals("COLLECTION & MITIGATION", opt1.getLabel());

        DropDownDto opt2 = result.data.get(1);
        assertEquals(85225, opt2.getValue());
        assertEquals("RISK ANALYTICS", opt2.getLabel());

        verify(reliabilityDaoMock).fetchRawSn2BySn1(sn1);
    }

    @Test
    void testGetSn2OptionsError() {
        Integer sn1 = 9006;
        when(reliabilityDaoMock.fetchRawSn2BySn1(sn1))
                .thenThrow(new RuntimeException("DB error"));

        IDataResult<List<DropDownDto>> result = reliabilityService.getSn2Options(sn1);

        assertFalse(result.success);
        assertNull(result.data);
        assertEquals("500", result.status);
        assertTrue(result.message.contains("DB error"));

        verify(reliabilityDaoMock).fetchRawSn2BySn1(sn1);
    }

    @Test
    void mapsCsv_km_enProgreso_usa_2_5() {
        var dto = new ReliabilityPackInputFilterRequest();
        dto.setRole("KM");
        dto.setTab("EN_PROGRESO");

        try (MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.toCsv("KM", "EN_PROGRESO")).thenReturn("2,5");
            when(reliabilityDaoMock.listTransfersByStatus(eq(""), eq(""), eq("2,5")))
                    .thenReturn(Collections.emptyList());

            var res = reliabilityService.getReliabilityPacksAdvanced(dto);

            assertTrue(res.success);
            verify(reliabilityDaoMock).listTransfersByStatus("", "", "2,5");
        }
    }

    @Test
    void mapsCsv_km_aprobados_usa_1() {
        var dto = new ReliabilityPackInputFilterRequest();
        dto.setRole("KM");
        dto.setTab("APROBADOS");

        try (MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.toCsv("KM", "APROBADOS")).thenReturn("1");
            when(reliabilityDaoMock.listTransfersByStatus("", "", "1"))
                    .thenReturn(Collections.emptyList());

            var res = reliabilityService.getReliabilityPacksAdvanced(dto);

            assertTrue(res.success);
            verify(reliabilityDaoMock).listTransfersByStatus("", "", "1");
        }
    }

    @Test
    void mapsCsv_sm_enProgreso_usa_3_2_4_5() {
        var dto = new ReliabilityPackInputFilterRequest();
        dto.setRole("SM");
        dto.setTab("EN_PROGRESO");

        try (MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.toCsv("SM", "EN_PROGRESO"))
                    .thenReturn("3,2,4,5");

            when(reliabilityDaoMock.listTransfersByStatus("", "", "3,2,4,5"))
                    .thenReturn(Collections.emptyList());

            var res = reliabilityService.getReliabilityPacksAdvanced(dto);

            assertTrue(res.success);
            verify(reliabilityDaoMock).listTransfersByStatus("", "", "3,2,4,5");
        }
    }

    @Test
    void mapsCsv_sm_aprobados_usa_1() {
        var dto = new ReliabilityPackInputFilterRequest();
        dto.setRole("SM");
        dto.setTab("APROBADOS");

        try (MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.toCsv("SM", "APROBADOS")).thenReturn("1");

            when(reliabilityDaoMock.listTransfersByStatus("", "", "1"))
                    .thenReturn(Collections.emptyList());

            var res = reliabilityService.getReliabilityPacksAdvanced(dto);

            assertTrue(res.success);
            verify(reliabilityDaoMock).listTransfersByStatus("", "", "1");
        }
    }

    @Test
    void mapsCsv_roleDesconocido_fallback_usa_2_5() {
        var dto = new ReliabilityPackInputFilterRequest();
        dto.setRole("???");
        dto.setTab("EN_PROGRESO");

        try (MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.toCsv("???", "EN_PROGRESO")).thenReturn("2,5");

            when(reliabilityDaoMock.listTransfersByStatus("", "", "2,5"))
                    .thenReturn(Collections.emptyList());

            var res = reliabilityService.getReliabilityPacksAdvanced(dto);

            assertTrue(res.success);
            verify(reliabilityDaoMock).listTransfersByStatus("", "", "2,5");
        }
    }

    @Test
    void changeTransferStatus_success() {
        String pack = "PACK1";
        var req = new TransferStatusChangeRequest();
        req.setActorRole("SM");
        req.setAction("APPROVE");

        when(reliabilityDaoMock.getPackCurrentStatus(pack)).thenReturn(TransferStatusPolicy.EN_PROGRESO); // 3

        try (MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {

            mocked.when(() -> TransferStatusPolicy.computeNextStatusOrThrow(
                            "SM", TransferStatusPolicy.EN_PROGRESO, TransferStatusPolicy.Action.APPROVE))
                    .thenReturn(TransferStatusPolicy.APROBADO_PO);

            // DAO actualiza
            doNothing().when(reliabilityDaoMock).changeTransferStatus(pack, TransferStatusPolicy.APROBADO_PO);

            var res = reliabilityService.changeTransferStatus(pack, req);

            assertTrue(res.success);
            assertNotNull(res.data);
            assertEquals(TransferStatusPolicy.EN_PROGRESO, res.data.getOldStatus());
            assertEquals(TransferStatusPolicy.APROBADO_PO, res.data.getNewStatus());
            verify(reliabilityDaoMock).changeTransferStatus(pack, TransferStatusPolicy.APROBADO_PO);
        }
    }

    @Test
    void changeTransferStatus_packNoEncontrado() {
        when(reliabilityDaoMock.getPackCurrentStatus("P404")).thenReturn(null);

        var req = new TransferStatusChangeRequest();
        req.setActorRole("SM");
        req.setAction("APPROVE");

        var res = reliabilityService.changeTransferStatus("P404", req);

        assertFalse(res.success);
        assertEquals("404", res.status);
        assertNull(res.data);
    }

    @Test
    void changeTransferStatus_accionInvalida() {
        when(reliabilityDaoMock.getPackCurrentStatus("P1")).thenReturn(3);

        var req = new TransferStatusChangeRequest();
        req.setActorRole("SM");
        req.setAction("fooBAR");

        var res = reliabilityService.changeTransferStatus("P1", req);

        assertFalse(res.success);
        assertEquals("400", res.status);
        verify(reliabilityDaoMock, never()).changeTransferStatus(anyString(), anyInt());
    }

    @Test
    void changeTransferStatus_transicionNoPermitida() {
        when(reliabilityDaoMock.getPackCurrentStatus("P1")).thenReturn(2); // Aprobado PO

        var req = new TransferStatusChangeRequest();
        req.setActorRole("SM");
        req.setAction("APPROVE");

        try (MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.computeNextStatusOrThrow("SM", 2, TransferStatusPolicy.Action.APPROVE))
                    .thenThrow(new IllegalArgumentException("Transición no permitida"));
            var res = reliabilityService.changeTransferStatus("P1", req);
            assertFalse(res.success);
            assertEquals("409", res.status);
        }
    }

    @Test
    void changeTransferStatus_errorDao() {
        when(reliabilityDaoMock.getPackCurrentStatus("P1")).thenReturn(3);

        var req = new TransferStatusChangeRequest();
        req.setActorRole("SM");
        req.setAction("APPROVE");

        try (MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.computeNextStatusOrThrow("SM", 3, TransferStatusPolicy.Action.APPROVE))
                    .thenReturn(2);
            doThrow(new RuntimeException("DB down")).when(reliabilityDaoMock).changeTransferStatus("P1", 2);

            var res = reliabilityService.changeTransferStatus("P1", req);
            assertFalse(res.success);
            assertEquals("500", res.status);
        }
    }

    // --- updateJobBySm ---

    @Test
    void updateJobBySm_success() {
        var dto = new UpdateJobDtoRequest();
        dto.setActorRole("SM");
        dto.setPack("P1");
        dto.setJobName("JOB_A");

        when(reliabilityDaoMock.getPackCurrentStatus("P1")).thenReturn(TransferStatusPolicy.DEVUELTO_PO); // 4

        try (MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.canEdit("SM", TransferStatusPolicy.DEVUELTO_PO))
                    .thenReturn(1);
            doNothing().when(reliabilityDaoMock).updateJobByPackAndName(dto);

            var res = reliabilityService.updateJobBySm(dto);
            assertTrue(res.success);
            assertEquals("Job actualizado", res.message);
        }
    }

    @Test
    void updateJobBySm_forbiddenCuandoNoDevuelto() {
        var dto = new UpdateJobDtoRequest();
        dto.setActorRole("SM");
        dto.setPack("P1");
        dto.setJobName("JOB_A");

        when(reliabilityDaoMock.getPackCurrentStatus("P1")).thenReturn(TransferStatusPolicy.EN_PROGRESO); // 3

        try (MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.canEdit("SM", TransferStatusPolicy.EN_PROGRESO))
                    .thenReturn(0);
            var res = reliabilityService.updateJobBySm(dto);
            assertFalse(res.success);
            assertEquals("409", res.status);
        }
    }

    @Test
    void updateJobBySm_packNoEncontrado() {
        var dto = new UpdateJobDtoRequest();
        dto.setActorRole("SM");
        dto.setPack("P404");

        when(reliabilityDaoMock.getPackCurrentStatus("P404")).thenReturn(null);

        var res = reliabilityService.updateJobBySm(dto);
        assertFalse(res.success);
        assertEquals("404", res.status);
    }

    @Test
    void updateJobBySm_errorDao404() {
        var dto = new UpdateJobDtoRequest();
        dto.setActorRole("SM");
        dto.setPack("P1");

        when(reliabilityDaoMock.getPackCurrentStatus("P1")).thenReturn(TransferStatusPolicy.DEVUELTO_RLB); // 5

        try (MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.canEdit("SM", 5)).thenReturn(1);
            doThrow(new PersistenceException("No se encontró el job", null))
                    .when(reliabilityDaoMock).updateJobByPackAndName(dto);

            var res = reliabilityService.updateJobBySm(dto);
            assertFalse(res.success);
            assertEquals("404", res.status);
        }
    }

    @Test
    void updateJobBySm_errorDao500() {
        var dto = new UpdateJobDtoRequest();
        dto.setActorRole("SM");
        dto.setPack("P1");

        when(reliabilityDaoMock.getPackCurrentStatus("P1")).thenReturn(5);

        try (MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.canEdit("SM", 5)).thenReturn(1);
            doThrow(new RuntimeException("DB down"))
                    .when(reliabilityDaoMock).updateJobByPackAndName(dto);

            var res = reliabilityService.updateJobBySm(dto);
            assertFalse(res.success);
            assertEquals("500", res.status);
        }
    }

    // --- updateCommentsForPack (KM) ---

    @Test
    void updateCommentsForPack_success() {
        when(reliabilityDaoMock.getPackCurrentStatus("P2")).thenReturn(TransferStatusPolicy.APROBADO_PO); // 2

        try (MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.canWriteGeneralComment("KM", TransferStatusPolicy.APROBADO_PO))
                    .thenReturn(1);

            doNothing().when(reliabilityDaoMock).updatePackComments("P2", "nota");

            var res = reliabilityService.updateCommentsForPack("P2", "KM", "nota");
            assertTrue(res.success);
            assertEquals("Comentarios actualizados", res.message);
            verify(reliabilityDaoMock).updatePackComments("P2", "nota");
        }
    }

    @Test
    void updateCommentsForPack_forbidden() {
        when(reliabilityDaoMock.getPackCurrentStatus("P2")).thenReturn(TransferStatusPolicy.EN_PROGRESO); // 3

        try (MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.canWriteGeneralComment("KM", 3)).thenReturn(0);

            var res = reliabilityService.updateCommentsForPack("P2", "KM", "x");
            assertFalse(res.success);
            assertEquals("409", res.status);
        }
    }

    @Test
    void updateCommentsForPack_packNoEncontrado() {
        when(reliabilityDaoMock.getPackCurrentStatus("P404")).thenReturn(null);

        var res = reliabilityService.updateCommentsForPack("P404", "KM", "x");
        assertFalse(res.success);
        assertEquals("404", res.status);
    }

    @Test
    void updateCommentsForPack_errorDao404() {
        when(reliabilityDaoMock.getPackCurrentStatus("P2")).thenReturn(TransferStatusPolicy.APROBADO_PO); // 2

        try (MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.canWriteGeneralComment("KM", TransferStatusPolicy.APROBADO_PO))
                    .thenReturn(1);

            doThrow(new PersistenceException("Pack sin jobs para comentar", null))
                    .when(reliabilityDaoMock).updatePackComments("P2", "x");

            var res = reliabilityService.updateCommentsForPack("P2", "KM", "x");
            assertFalse(res.success);
            assertEquals("404", res.status);
        }
    }

    @Test
    void updateCommentsForPack_errorDao500() {
        when(reliabilityDaoMock.getPackCurrentStatus("P2")).thenReturn(TransferStatusPolicy.APROBADO_PO); // 2

        try (MockedStatic<TransferStatusPolicy> mocked = mockStatic(TransferStatusPolicy.class)) {
            mocked.when(() -> TransferStatusPolicy.canWriteGeneralComment("KM", TransferStatusPolicy.APROBADO_PO))
                    .thenReturn(1);

            doThrow(new RuntimeException("DB down"))
                    .when(reliabilityDaoMock).updatePackComments("P2", "x");

            var res = reliabilityService.updateCommentsForPack("P2", "KM", "x");
            assertFalse(res.success);
            assertEquals("500", res.status);
        }
    }
    @Test
    void getTransferDetail_success() {
        String pack = "PACK_OK";
        TransferDetailResponse detail = new TransferDetailResponse();

        when(reliabilityDaoMock.getTransferDetail(pack)).thenReturn(detail);

        IDataResult<TransferDetailResponse> res = reliabilityService.getTransferDetail(pack);

        assertNotNull(res);
        assertTrue(res.success);
        assertSame(detail, res.data);
        verify(reliabilityDaoMock).getTransferDetail(pack);
    }

    @Test
    void getTransferDetail_notFound() {
        String pack = "PACK_404";
        when(reliabilityDaoMock.getTransferDetail(pack)).thenReturn(null);

        IDataResult<TransferDetailResponse> res = reliabilityService.getTransferDetail(pack);

        assertNotNull(res);
        assertFalse(res.success);
        assertNull(res.data);
        assertEquals("404", res.status);
        assertEquals("Pack no encontrado", res.message);
        verify(reliabilityDaoMock).getTransferDetail(pack);
    }

    @Test
    void getTransferDetail_error() {
        String pack = "PACK_ERR";
        when(reliabilityDaoMock.getTransferDetail(pack))
                .thenThrow(new RuntimeException("DB down"));

        IDataResult<TransferDetailResponse> res = reliabilityService.getTransferDetail(pack);

        assertNotNull(res);
        assertFalse(res.success);
        assertNull(res.data);
        assertEquals("500", res.status);
        assertTrue(res.message.contains("DB down"));
        verify(reliabilityDaoMock).getTransferDetail(pack);
    }
}
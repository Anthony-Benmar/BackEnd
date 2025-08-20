package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.ReliabilityMapper;
import com.bbva.dto.catalog.response.DropDownDto;
import com.bbva.dto.reliability.request.*;
import com.bbva.dto.reliability.response.*;
import com.bbva.util.JSONUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReliabilityDaoTest {

    private ReliabilityDao reliabilityDao;
    private SqlSessionFactory sqlSessionFactoryMock;
    private SqlSession sqlSessionMock;
    private ReliabilityMapper reliabilityMapperMock;
    private MockedStatic<MyBatisConnectionFactory> mockedFactory;
    private MockedStatic<JSONUtils> mockedJsonUtils;

    @BeforeEach
    void setUp() {
        sqlSessionFactoryMock = mock(SqlSessionFactory.class);
        sqlSessionMock        = mock(SqlSession.class);
        reliabilityMapperMock = mock(ReliabilityMapper.class);

        mockedFactory = mockStatic(MyBatisConnectionFactory.class);
        mockedFactory.when(MyBatisConnectionFactory::getInstance)
                .thenReturn(sqlSessionFactoryMock);

        when(sqlSessionFactoryMock.openSession()).thenReturn(sqlSessionMock);
        when(sqlSessionMock.getMapper(ReliabilityMapper.class))
                .thenReturn(reliabilityMapperMock);

        reliabilityDao = new ReliabilityDao();
    }

    @AfterEach
    void tearDown() {
        mockedFactory.close();
        if (mockedJsonUtils != null) {
            mockedJsonUtils.close();
        }
    }

    @Test
    void testInventoryInputsFilterSuccess() {
        InventoryInputsFilterDtoRequest dto = new InventoryInputsFilterDtoRequest();
        dto.setRecordsAmount(2);
        dto.setPage(1);

        InventoryInputsDtoResponse resp1 = new InventoryInputsDtoResponse();
        resp1.setInputPaths("a\nb\nc");
        InventoryInputsDtoResponse resp2 = new InventoryInputsDtoResponse();
        resp2.setInputPaths(null);
        List<InventoryInputsDtoResponse> mockList = List.of(resp1, resp2, new InventoryInputsDtoResponse());

        when(reliabilityMapperMock.inventoryInputsFilter(any()))
                .thenReturn(mockList);

        InventoryInputsFilterDtoResponse response =
                reliabilityDao.inventoryInputsFilter(dto);

        assertNotNull(response);
        assertEquals(3, response.getCount());
        assertEquals(2, response.getPagesAmount());
        // Check that inputPathsArray is set for the first item
        assertNotNull(response.getData().get(0).getInputPathsArray());
        assertArrayEquals(new String[]{"a","b","c"}, response.getData().get(0).getInputPathsArray());
        // Items without inputPaths remain unchanged
        assertNull(response.getData().get(1).getInputPathsArray());
    }

    @Test
    void testInventoryInputsFilterJsonConversionError() {
        // Force JSONUtils.convertFromObjectToJson to throw
        mockedJsonUtils = mockStatic(JSONUtils.class);
        mockedJsonUtils.when(() -> JSONUtils.convertFromObjectToJson(any()))
                .thenThrow(new RuntimeException("JSON error"));

        InventoryInputsFilterDtoRequest dto = new InventoryInputsFilterDtoRequest();
        dto.setRecordsAmount(0);
        dto.setPage(1);

        List<InventoryInputsDtoResponse> mockList = List.of();
        when(reliabilityMapperMock.inventoryInputsFilter(any()))
                .thenReturn(mockList);

        // Should not throw despite JSON error
        InventoryInputsFilterDtoResponse response =
                reliabilityDao.inventoryInputsFilter(dto);
        assertNotNull(response);
        assertEquals(0, response.getCount());
    }

    @Test
    void testListInventoryException() {
        when(reliabilityMapperMock.inventoryInputsFilter(any()))
                .thenThrow(new RuntimeException("DB down"));

        List<InventoryInputsDtoResponse> result =
                reliabilityDao.listinventory(new InventoryInputsFilterDtoRequest());
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetExecutionValidationException() {
        String jobName = "jobX";
        when(reliabilityMapperMock.getExecutionValidation(jobName))
                .thenThrow(new RuntimeException("DB error"));

        ExecutionValidationDtoResponse resp =
                reliabilityDao.getExecutionValidation(jobName);
        assertNull(resp);
    }

    @Test
    void testGetExecutionValidationAllException() {
        List<String> jobs = Arrays.asList("j1","j2");
        when(reliabilityMapperMock.getExecutionValidation(any()))
                .thenThrow(new RuntimeException("Error"));

        List<ExecutionValidationAllDtoResponse> all =
                reliabilityDao.getExecutionValidationAll(jobs);
        assertNotNull(all);
        assertTrue(all.isEmpty());
    }

    @Test
    void testUpdateInventoryJobStockException() {
        InventoryJobUpdateDtoRequest dto = new InventoryJobUpdateDtoRequest();
        doThrow(new RuntimeException("Update error"))
                .when(reliabilityMapperMock).updateInventoryJobStock(dto);

        // Should not propagate exception
        assertDoesNotThrow(() -> reliabilityDao.updateInventoryJobStock(dto));
    }

    // Existing tests (unchanged)...

    @Test
    void testInventoryInputsFilterMapperThrows() {
        InventoryInputsFilterDtoRequest dto = new InventoryInputsFilterDtoRequest();
        dto.setRecordsAmount(0);
        dto.setPage(1);

        when(reliabilityMapperMock.inventoryInputsFilter(any()))
                .thenThrow(new RuntimeException("DB down"));

        InventoryInputsFilterDtoResponse response =
                reliabilityDao.inventoryInputsFilter(dto);

        assertNotNull(response);
        assertEquals(0, response.getCount());
        assertEquals(1, response.getPagesAmount());
        assertTrue(response.getData().isEmpty());
    }

    @Test
    void testGetPendingCustodyJobsSuccess() {
        String sdatoolId = "123";
        when(reliabilityMapperMock.getPendingCustodyJobs(sdatoolId))
                .thenReturn(List.of(new PendingCustodyJobsDtoResponse()));

        List<PendingCustodyJobsDtoResponse> result =
                reliabilityDao.getPendingCustodyJobs(sdatoolId);

        assertEquals(1, result.size());
    }

    @Test
    void testUpdateInventoryJobStockSuccess() {
        InventoryJobUpdateDtoRequest dto = new InventoryJobUpdateDtoRequest();
        doNothing().when(reliabilityMapperMock).updateInventoryJobStock(dto);

        reliabilityDao.updateInventoryJobStock(dto);

        verify(sqlSessionMock).commit();
    }

    @Test
    void testGetProjectCustodyInfoSuccess() {
        String sdatoolId = "123";
        when(reliabilityMapperMock.getProjectCustodyInfo(sdatoolId))
                .thenReturn(List.of(new ProjectCustodyInfoDtoResponse()));

        List<ProjectCustodyInfoDtoResponse> result =
                reliabilityDao.getProjectCustodyInfo(sdatoolId);

        assertEquals(1, result.size());
    }

    @Test
    void testGetProjectCustodyInfoException() {
        String sdatoolId = "XYZ";
        when(reliabilityMapperMock.getProjectCustodyInfo(sdatoolId))
                .thenThrow(new RuntimeException("oops"));

        List<ProjectCustodyInfoDtoResponse> result =
                reliabilityDao.getProjectCustodyInfo(sdatoolId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetExecutionValidationSuccess() {
        String jobName = "job1";
        when(reliabilityMapperMock.getExecutionValidation(jobName))
                .thenReturn(new ExecutionValidationDtoResponse());

        ExecutionValidationDtoResponse resp =
                reliabilityDao.getExecutionValidation(jobName);

        assertNotNull(resp);
    }

    @Test
    void testGetExecutionValidationAll() {
        List<String> jobs = Arrays.asList("j1","j2");
        ExecutionValidationDtoResponse r1 = new ExecutionValidationDtoResponse();
        r1.setValidation("OK");
        ExecutionValidationDtoResponse r2 = new ExecutionValidationDtoResponse();
        r2.setValidation("FAIL");
        when(reliabilityMapperMock.getExecutionValidation("j1")).thenReturn(r1);
        when(reliabilityMapperMock.getExecutionValidation("j2")).thenReturn(r2);

        List<ExecutionValidationAllDtoResponse> all =
                reliabilityDao.getExecutionValidationAll(jobs);

        assertEquals(2, all.size());
        assertEquals("OK",   all.get(0).getValidation());
        assertEquals("FAIL", all.get(1).getValidation());
    }

    @Test
    void testInsertTransfer() {
        TransferInputDtoRequest dto = new TransferInputDtoRequest();
        dto.setPack("pkg");
        dto.setDomainId(1);

        JobTransferInputDtoRequest job1 = new JobTransferInputDtoRequest();
        job1.setJobName("job1");
        dto.setTransferInputDtoRequests(List.of(job1));

        when(sqlSessionFactoryMock.openSession()).thenReturn(sqlSessionMock);
        when(sqlSessionMock.getMapper(ReliabilityMapper.class))
                .thenReturn(reliabilityMapperMock);

        reliabilityDao.insertTransfer(dto);

        verify(reliabilityMapperMock).insertTranfer(dto);
        verify(reliabilityMapperMock).insertJobStock(job1);
        verify(sqlSessionMock).commit();
    }

    @Test
    void testInsertTransferNoJobs() {
        TransferInputDtoRequest dto = new TransferInputDtoRequest();
        dto.setTransferInputDtoRequests(List.of());
        when(sqlSessionFactoryMock.openSession()).thenReturn(sqlSessionMock);
        when(sqlSessionMock.getMapper(ReliabilityMapper.class))
                .thenReturn(reliabilityMapperMock);

        reliabilityDao.insertTransfer(dto);

        verify(reliabilityMapperMock).insertTranfer(dto);
        verify(reliabilityMapperMock, never()).insertJobStock(any());
    }

    @Test
    void testInsertTransferDatabaseError() {
        TransferInputDtoRequest dto = new TransferInputDtoRequest();
        when(sqlSessionFactoryMock.openSession()).thenReturn(sqlSessionMock);
        when(sqlSessionMock.getMapper(ReliabilityMapper.class))
                .thenThrow(new RuntimeException("DB down"));

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                reliabilityDao.insertTransfer(dto)
        );
        assertEquals(
                "Error al guardar los datos de la transferencia en la base de datos.",
                ex.getMessage()
        );
    }

    @Test
    void testGetReliabilityPacksSuccess() {
        ReliabilityPackInputFilterRequest req = new ReliabilityPackInputFilterRequest();
        req.setPage(1);
        req.setRecordsAmount(5);
        req.setDomainName("D");
        req.setUseCase("U");

        when(reliabilityMapperMock.getReliabilityPacks("D", "U"))
                .thenReturn(List.of(new ReliabilityPacksDtoResponse()));

        PaginationReliabilityPackResponse resp = reliabilityDao.getReliabilityPacks(req);

        assertNotNull(resp);
        assertEquals(1, resp.getCount());
        assertEquals(1, resp.getPagesAmount());
        assertEquals(1, resp.getData().size());
    }

    @Test
    void testUpdateStatusReliabilityPacksJobStockCallsCommit() {
        List<String> packs = List.of("P1","P2");
        reliabilityDao.updateStatusReliabilityPacksJobStock(packs);

        for (String p : packs) {
            verify(reliabilityMapperMock).updateReliabilityStatus(p,1);
            verify(reliabilityMapperMock).updateProjectInfoStatus(p,1);
        }
        verify(sqlSessionMock).commit();
    }

    @Test
    void testGetOriginTypesSuccess() {
        when(reliabilityMapperMock.getOriginTypes())
                .thenReturn(List.of(new DropDownDto(), new DropDownDto()));
        assertEquals(2, reliabilityDao.getOriginTypes().size());
    }

    @Test
    void testGetPendingCustodyJobs_TrimmingWhitespace() {
        String sdatoolId = "XYZ";
        PendingCustodyJobsDtoResponse dto = new PendingCustodyJobsDtoResponse();
        dto.setJobName("   JOB_01   ");
        when(reliabilityMapperMock.getPendingCustodyJobs(sdatoolId))
                .thenReturn(List.of(dto));

        List<PendingCustodyJobsDtoResponse> result =
                reliabilityDao.getPendingCustodyJobs(sdatoolId);

        assertEquals(1, result.size());
        assertEquals("JOB_01", result.get(0).getJobName());
    }

    @Test
    void testGetPendingCustodyJobs_EmptyList() {
        String sdatoolId = "ABC";
        when(reliabilityMapperMock.getPendingCustodyJobs(sdatoolId))
                .thenReturn(Collections.emptyList());

        List<PendingCustodyJobsDtoResponse> result =
                reliabilityDao.getPendingCustodyJobs(sdatoolId);

        assertTrue(result.isEmpty());
    }

    @Test
    void testGetReliabilityPacksPaging() {
        ReliabilityPackInputFilterRequest req = new ReliabilityPackInputFilterRequest();
        req.setDomainName("D");
        req.setUseCase("U");
        req.setRecordsAmount(3);
        req.setPage(2);

        List<ReliabilityPacksDtoResponse> full = new ArrayList<>();
        for (int i = 0; i < 7; i++) full.add(new ReliabilityPacksDtoResponse());
        when(reliabilityMapperMock.getReliabilityPacks("D", "U"))
                .thenReturn(full);

        PaginationReliabilityPackResponse resp =
                reliabilityDao.getReliabilityPacks(req);

        assertEquals(7, resp.getCount());
        assertEquals(3, resp.getPagesAmount());
        assertEquals(3, resp.getData().size());
    }
    @Test
    void testSingletonGetInstance() {
        ReliabilityDao a = ReliabilityDao.getInstance();
        ReliabilityDao b = ReliabilityDao.getInstance();
        assertSame(a, b);
    }

    @Test
    void testGetReliabilityPacksMapperThrows() {
        when(reliabilityMapperMock.getReliabilityPacks(anyString(), anyString()))
                .thenThrow(new RuntimeException("fail packs"));
        ReliabilityPackInputFilterRequest req = new ReliabilityPackInputFilterRequest();
        req.setDomainName("D"); req.setUseCase("U");
        assertThrows(RuntimeException.class, () -> reliabilityDao.getReliabilityPacks(req));
    }

    @Test
    void testGetOriginTypesException() {
        mockedFactory.when(MyBatisConnectionFactory::getInstance)
                .thenReturn(sqlSessionFactoryMock);
        when(sqlSessionFactoryMock.openSession()).thenThrow(new RuntimeException("no session"));
        assertThrows(RuntimeException.class, () -> reliabilityDao.getOriginTypes());
    }

    @Test
    void testUpdateStatusReliabilityPacksJobStockException() {
        doThrow(new RuntimeException("update fail")).when(reliabilityMapperMock).updateReliabilityStatus(anyString(), anyInt());
        assertDoesNotThrow(() -> reliabilityDao.updateStatusReliabilityPacksJobStock(List.of("P1")));
    }
    @Test
    void testGetPendingCustodyJobsException() {
        String sdatoolId = "ERR";
        when(reliabilityMapperMock.getPendingCustodyJobs(sdatoolId))
                .thenThrow(new RuntimeException("DB error"));
        List<PendingCustodyJobsDtoResponse> result =
                reliabilityDao.getPendingCustodyJobs(sdatoolId);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetJobExecutionHistorySuccess() {
        String jobName = "JOB_OK";
        JobExecutionHistoryDtoResponse dto = new JobExecutionHistoryDtoResponse();
        when(reliabilityMapperMock.getJobExecutionHistory(jobName))
                .thenReturn(List.of(dto));

        List<JobExecutionHistoryDtoResponse> result =
                reliabilityDao.getJobExecutionHistory(jobName);

        assertNotNull(result, "La lista no debe ser null");
        assertEquals(1, result.size(), "Debe devolver exactamente un elemento");
        assertSame(dto, result.get(0), "Debe ser el mismo objeto retornado por el mapper");

        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ReliabilityMapper.class);
        verify(reliabilityMapperMock).getJobExecutionHistory(jobName);
    }

    @Test
    void testGetJobExecutionHistoryException() {
        String jobName = "JOB_FAIL";
        when(reliabilityMapperMock.getJobExecutionHistory(jobName))
                .thenThrow(new RuntimeException("DB down"));

        List<JobExecutionHistoryDtoResponse> result =
                reliabilityDao.getJobExecutionHistory(jobName);

        assertNotNull(result, "No debe retornar null aunque haya fallo");
        assertTrue(result.isEmpty(), "Debe retornar lista vacía al capturar excepción");

        verify(sqlSessionFactoryMock).openSession();
    }

    @Test
    void testFetchRawSn2BySn1Success() {
        RawSn2DtoResponse r1 = new RawSn2DtoResponse();
        r1.setValue(100);
        r1.setRawDesc("ABC-DEF-GHI");
        RawSn2DtoResponse r2 = new RawSn2DtoResponse();
        r2.setValue(200);
        r2.setRawDesc("JKL-MNO-PQR");

        when(reliabilityMapperMock.fetchRawSn2BySn1(42))
                .thenReturn(List.of(r1, r2));

        List<RawSn2DtoResponse> resultados = reliabilityDao.fetchRawSn2BySn1(42);

        assertEquals(2, resultados.size());
        assertSame(r1, resultados.get(0));
        assertSame(r2, resultados.get(1));

        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ReliabilityMapper.class);
    }

    @Test
    void testFetchRawSn2BySn1Exception() {
        when(sqlSessionFactoryMock.openSession())
                .thenThrow(new RuntimeException("fail session"));

        List<RawSn2DtoResponse> resultados = reliabilityDao.fetchRawSn2BySn1(99);

        assertNotNull(resultados);
        assertTrue(resultados.isEmpty());
    }

    @Test
    void testListTransfersByStatusSuccess() {
        var expected = List.of(new ReliabilityPacksDtoResponse());
        when(reliabilityMapperMock.listTransfersByStatus("DCSV", "UCSV", "2,5"))
                .thenReturn(expected);

        List<ReliabilityPacksDtoResponse> out =
                reliabilityDao.listTransfersByStatus("DCSV", "UCSV", "2,5");

        assertNotNull(out);
        assertEquals(1, out.size());
        assertSame(expected.get(0), out.get(0));

        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ReliabilityMapper.class);
        verify(reliabilityMapperMock).listTransfersByStatus("DCSV", "UCSV", "2,5");
    }

    @Test
    void testListTransfersByStatusException() {
        when(reliabilityMapperMock.listTransfersByStatus(anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("DB error"));

        List<ReliabilityPacksDtoResponse> out =
                reliabilityDao.listTransfersByStatus("", "", "1");

        assertNotNull(out);
        assertTrue(out.isEmpty(), "Ante excepción, el DAO debe devolver lista vacía");
    }

    @Test
    void testGetPackCurrentStatus_success() {
        String pack = "PACK_OK";
        when(reliabilityMapperMock.getPackStatus(pack)).thenReturn(3);

        Integer st = reliabilityDao.getPackCurrentStatus(pack);

        assertNotNull(st);
        assertEquals(3, st);
        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ReliabilityMapper.class);
        verify(reliabilityMapperMock).getPackStatus(pack);
    }

    @Test
    void testGetPackCurrentStatus_exception_returnsNull() {
        String pack = "PACK_ERR";
        when(reliabilityMapperMock.getPackStatus(pack)).thenThrow(new RuntimeException("DB error"));

        Integer st = reliabilityDao.getPackCurrentStatus(pack);

        assertNull(st, "Ante excepción el DAO debe retornar null");
        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ReliabilityMapper.class);
        verify(reliabilityMapperMock).getPackStatus(pack);
    }

    @Test
    void testChangeTransferStatus_success_commitsAndUpdatesBoth() {
        String pack = "PACK_123";
        int newStatus = 4;

        // no exception -> commit esperado
        assertDoesNotThrow(() -> reliabilityDao.changeTransferStatus(pack, newStatus));

        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ReliabilityMapper.class);
        verify(reliabilityMapperMock).updateReliabilityStatus(pack, newStatus);
        verify(reliabilityMapperMock).updateProjectInfoStatus(pack, newStatus);
        verify(sqlSessionMock).commit();
    }

    @Test
    void testChangeTransferStatus_mapperThrows_wrapsInPersistenceException_sinJPAImport() {
        String pack = "PACK_500";
        int newStatus = 6;

        // El mapper falla en la primera actualización
        doThrow(new RuntimeException("DB down"))
                .when(reliabilityMapperMock).updateReliabilityStatus(pack, newStatus);

        // Como PersistenceException extiende RuntimeException, no necesitamos importar JPA
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reliabilityDao.changeTransferStatus(pack, newStatus));

        // Validamos que efectivamente sea una PersistenceException sin importarla
        assertEquals("PersistenceException", ex.getClass().getSimpleName());
        assertTrue(ex.getMessage().contains("No se pudo actualizar el estado del pack PACK_500"));
        assertNotNull(ex.getCause());
        assertEquals("DB down", ex.getCause().getMessage());

        // No debe hacer commit ni llamar al segundo update cuando falla
        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ReliabilityMapper.class);
        verify(reliabilityMapperMock).updateReliabilityStatus(pack, newStatus);
        verify(reliabilityMapperMock, never()).updateProjectInfoStatus(anyString(), anyInt());
        verify(sqlSessionMock, never()).commit();
    }

    @Test
    void testUpdateJobByPackAndName_success() {
        UpdateJobDtoRequest dto = new UpdateJobDtoRequest();
        dto.setPack("P1"); dto.setJobName("J1");

        when(sqlSessionMock.getMapper(ReliabilityMapper.class))
                .thenReturn(reliabilityMapperMock);
        when(reliabilityMapperMock.updateJobByPackAndName(dto)).thenReturn(1);

        assertDoesNotThrow(() -> reliabilityDao.updateJobByPackAndName(dto));
        verify(sqlSessionMock).commit();
    }

    @Test
    void testUpdateJobByPackAndName_noRows_throwsPersistenceException() {
        UpdateJobDtoRequest dto = new UpdateJobDtoRequest();
        dto.setPack("P1"); dto.setJobName("JX");

        when(sqlSessionMock.getMapper(ReliabilityMapper.class))
                .thenReturn(reliabilityMapperMock);
        when(reliabilityMapperMock.updateJobByPackAndName(dto)).thenReturn(0);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reliabilityDao.updateJobByPackAndName(dto));
        assertEquals("PersistenceException", ex.getClass().getSimpleName());
        assertTrue(ex.getMessage().contains("No se encontró el job en ese pack"));
        verify(sqlSessionMock, never()).commit();
    }

    @Test
    void testUpdatePackComments_success() {
        when(sqlSessionMock.getMapper(ReliabilityMapper.class))
                .thenReturn(reliabilityMapperMock);
        when(reliabilityMapperMock.updatePackComments("P1", "nota")).thenReturn(2); // 2 jobs actualizados

        assertDoesNotThrow(() -> reliabilityDao.updatePackComments("P1", "nota"));
        verify(sqlSessionMock).commit();
    }

    @Test
    void testUpdatePackComments_noRows_throwsPersistenceException() {
        when(sqlSessionMock.getMapper(ReliabilityMapper.class))
                .thenReturn(reliabilityMapperMock);
        when(reliabilityMapperMock.updatePackComments("P1", "nota")).thenReturn(0);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reliabilityDao.updatePackComments("P1", "nota"));

        assertEquals("PersistenceException", ex.getClass().getSimpleName());

        String msg = ex.getMessage() == null ? "" : ex.getMessage().toLowerCase();
        assertTrue(
                msg.contains("pack sin jobs para comentar") ||
                        msg.contains("no se encontró el job") ||
                        msg.contains("pack no encontrado para comentar"),
                "Mensaje inesperado: " + ex.getMessage()
        );

        verify(sqlSessionMock, never()).commit();
    }

    @Test
    void testGetTransferDetail_headerNull_returnsNull() {
        String pack = "PACK_X";

        // El mapper devuelve header null
        when(reliabilityMapperMock.getTransferHeader(pack)).thenReturn(null);

        TransferDetailResponse out = reliabilityDao.getTransferDetail(pack);

        assertNull(out, "Si el header es null, el DAO debe retornar null");
        verify(reliabilityMapperMock).getTransferHeader(pack);
        // No debe pedir los jobs si no hay header
        verify(reliabilityMapperMock, never()).getTransferJobs(anyString());
    }

    @Test
    void testGetTransferDetail_exception_returnsNull() {
        String pack = "PACK_ERR";

        when(sqlSessionFactoryMock.openSession()).thenThrow(new RuntimeException("boom"));

        TransferDetailResponse out = reliabilityDao.getTransferDetail(pack);

        assertNull(out, "Ante excepción, el DAO debe retornar null");
    }

    @Test
    void testUpdateJobComment_success() {
        String pack = "P1";
        String job  = "JOB_A";
        String note = "comentario nuevo";

        when(sqlSessionMock.getMapper(ReliabilityMapper.class))
                .thenReturn(reliabilityMapperMock);
        when(reliabilityMapperMock.updateJobComment(pack, job, note))
                .thenReturn(1);

        assertDoesNotThrow(() -> reliabilityDao.updateJobComment(pack, job, note));

        verify(reliabilityMapperMock).updateJobComment(pack, job, note);
        verify(sqlSessionMock).commit();
    }

    @Test
    void testUpdateJobComment_noRows_throwsPersistenceException() {
        String pack = "P1";
        String job  = "JOB_X";
        String note = "sin match";

        when(sqlSessionMock.getMapper(ReliabilityMapper.class))
                .thenReturn(reliabilityMapperMock);
        when(reliabilityMapperMock.updateJobComment(pack, job, note))
                .thenReturn(0);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reliabilityDao.updateJobComment(pack, job, note));

        assertEquals("PersistenceException", ex.getClass().getSimpleName());
        assertTrue(ex.getMessage().contains("No se encontró el job en ese pack"));

        verify(reliabilityMapperMock).updateJobComment(pack, job, note);
        verify(sqlSessionMock, never()).commit();
    }
}

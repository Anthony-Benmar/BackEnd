package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.ReliabilityMapper;
import com.bbva.dto.catalog.response.DropDownDto;
import com.bbva.dto.reliability.request.*;
import com.bbva.dto.reliability.response.*;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReliabilityDaoTest {

    private ReliabilityDao reliabilityDao;
    private SqlSessionFactory sqlSessionFactoryMock;
    private SqlSession sqlSessionMock;
    private ReliabilityMapper reliabilityMapperMock;
    private MockedStatic<MyBatisConnectionFactory> mockedFactory;

    @BeforeEach
    void setUp() {
        sqlSessionFactoryMock = mock(SqlSessionFactory.class);
        sqlSessionMock = mock(SqlSession.class);
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
    }

    @Test
    void testInventoryInputsFilterSuccess() {
        InventoryInputsFilterDtoRequest dto = new InventoryInputsFilterDtoRequest();
        dto.setDomainName("domain");
        dto.setRecordsAmount(2);
        dto.setPage(1);

        List<InventoryInputsDtoResponse> mockList = List.of(
                new InventoryInputsDtoResponse(),
                new InventoryInputsDtoResponse(),
                new InventoryInputsDtoResponse()
        );
        when(reliabilityMapperMock.inventoryInputsFilter(any()))
                .thenReturn(mockList);

        InventoryInputsFilterDtoResponse response = reliabilityDao.inventoryInputsFilter(dto);

        assertNotNull(response);
        assertEquals(3, response.getCount());
        assertEquals(2, response.getPagesAmount());
        assertEquals(2, response.getData().size());

        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ReliabilityMapper.class);
        verify(reliabilityMapperMock).inventoryInputsFilter(any());
        verify(sqlSessionMock).close();
    }

    @Test
    void testGetPendingCustodyJobsSuccess() {
        String sdatoolId = "123";
        List<PendingCustodyJobsDtoResponse> mockList = List.of(new PendingCustodyJobsDtoResponse());
        when(reliabilityMapperMock.getPendingCustodyJobs(sdatoolId))
                .thenReturn(mockList);

        List<PendingCustodyJobsDtoResponse> result = reliabilityDao.getPendingCustodyJobs(sdatoolId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ReliabilityMapper.class);
        verify(reliabilityMapperMock).getPendingCustodyJobs(sdatoolId);
        verify(sqlSessionMock).close();
    }

    @Test
    void testUpdateInventoryJobStockSuccess() {
        InventoryJobUpdateDtoRequest dto = new InventoryJobUpdateDtoRequest();
        doNothing().when(reliabilityMapperMock).updateInventoryJobStock(dto);

        reliabilityDao.updateInventoryJobStock(dto);

        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ReliabilityMapper.class);
        verify(reliabilityMapperMock).updateInventoryJobStock(dto);
        verify(sqlSessionMock).commit();
        verify(sqlSessionMock).close();
    }

    @Test
    void testGetProjectCustodyInfoSuccess() {
        String sdatoolId = "123";
        List<ProjectCustodyInfoDtoResponse> mockList = List.of(new ProjectCustodyInfoDtoResponse());
        when(reliabilityMapperMock.getProjectCustodyInfo(sdatoolId))
                .thenReturn(mockList);

        List<ProjectCustodyInfoDtoResponse> result = reliabilityDao.getProjectCustodyInfo(sdatoolId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ReliabilityMapper.class);
        verify(reliabilityMapperMock).getProjectCustodyInfo(sdatoolId);
        verify(sqlSessionMock).close();
    }

    @Test
    void testGetExecutionValidationSuccess() {
        String jobName = "job1";
        ExecutionValidationDtoResponse mockResponse = new ExecutionValidationDtoResponse();
        when(reliabilityMapperMock.getExecutionValidation(jobName))
                .thenReturn(mockResponse);

        ExecutionValidationDtoResponse result = reliabilityDao.getExecutionValidation(jobName);

        assertNotNull(result);
        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ReliabilityMapper.class);
        verify(reliabilityMapperMock).getExecutionValidation(jobName);
        verify(sqlSessionMock).close();
    }

    @Test
    void testGetInstance() {
        ReliabilityDao i1 = ReliabilityDao.getInstance();
        ReliabilityDao i2 = ReliabilityDao.getInstance();
        assertSame(i1, i2);
    }

    @Test
    void testGetExecutionValidationAll() {
        List<String> jobNames = Arrays.asList("job1","job2");
        ExecutionValidationDtoResponse resp1 = new ExecutionValidationDtoResponse();
        resp1.setValidation("SUCCESS");
        ExecutionValidationDtoResponse resp2 = new ExecutionValidationDtoResponse();
        resp2.setValidation("FAILED");

        when(sqlSessionFactoryMock.openSession()).thenReturn(sqlSessionMock);
        when(sqlSessionMock.getMapper(ReliabilityMapper.class))
                .thenReturn(reliabilityMapperMock);
        when(reliabilityMapperMock.getExecutionValidation("job1"))
                .thenReturn(resp1);
        when(reliabilityMapperMock.getExecutionValidation("job2"))
                .thenReturn(resp2);

        List<ExecutionValidationAllDtoResponse> all = reliabilityDao.getExecutionValidationAll(jobNames);

        assertEquals(2, all.size());
        assertEquals("SUCCESS", all.get(0).getValidation());
        assertEquals("FAILED",  all.get(1).getValidation());
        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ReliabilityMapper.class);
        verify(sqlSessionMock).close();
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
        verify(sqlSessionMock).close();
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
        verify(sqlSessionMock).close();
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
        assertEquals("Error al guardar los datos de la transferencia en la base de datos.", ex.getMessage());
    }

    @Test
    void testGetReliabilityPacksSuccess() {
        ReliabilityPackInputFilterRequest req = new ReliabilityPackInputFilterRequest();
        req.setPage(1);
        req.setRecordsAmount(5);
        req.setDomainName("D");
        req.setUseCase("U");
        when(sqlSessionMock.getMapper(ReliabilityMapper.class))
                .thenReturn(reliabilityMapperMock);

        PaginationReliabilityPackResponse resp = reliabilityDao.getReliabilityPacks(req);

        assertNotNull(resp);
        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ReliabilityMapper.class);
        verify(sqlSessionMock).close();
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
        List<DropDownDto> mockList = List.of(new DropDownDto(), new DropDownDto());
        when(reliabilityMapperMock.getOriginTypes()).thenReturn(mockList);

        List<DropDownDto> result = reliabilityDao.getOriginTypes();

        assertSame(mockList, result);
        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(ReliabilityMapper.class);
        verify(reliabilityMapperMock).getOriginTypes();
        verify(sqlSessionMock).close();
    }

    // — Corrección clave: aquí seteamos recordsAmount y page para evitar NPE
    @Test
    void testInventoryInputsFilterMapperThrows() {
        InventoryInputsFilterDtoRequest req = new InventoryInputsFilterDtoRequest();
        req.setRecordsAmount(0);
        req.setPage(1);
        when(reliabilityMapperMock.inventoryInputsFilter(any()))
                .thenThrow(new RuntimeException("DB down"));

        InventoryInputsFilterDtoResponse resp = reliabilityDao.inventoryInputsFilter(req);

        assertNotNull(resp);
        assertEquals(0, resp.getCount());
        assertEquals(1, resp.getPagesAmount());
        assertTrue(resp.getData().isEmpty());
    }

    @Test
    void testGetProjectCustodyInfoException() {
        String sdatoolId = "FOO";
        when(reliabilityMapperMock.getProjectCustodyInfo(sdatoolId))
                .thenThrow(new RuntimeException("oops"));

        List<ProjectCustodyInfoDtoResponse> result =
                reliabilityDao.getProjectCustodyInfo(sdatoolId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetReliabilityPacksPaging() {
        ReliabilityPackInputFilterRequest req = new ReliabilityPackInputFilterRequest();
        req.setDomainName("D");
        req.setUseCase("U");
        req.setPage(2);
        req.setRecordsAmount(3);

        List<ReliabilityPacksDtoResponse> full = new ArrayList<>();
        for (int i = 0; i < 7; i++) full.add(new ReliabilityPacksDtoResponse());
        when(reliabilityMapperMock.getReliabilityPacks("D","U")).thenReturn(full);

        PaginationReliabilityPackResponse resp = reliabilityDao.getReliabilityPacks(req);

        assertEquals(7, resp.getCount());
        assertEquals(3, resp.getPagesAmount());
        assertEquals(3, resp.getData().size());
    }
}

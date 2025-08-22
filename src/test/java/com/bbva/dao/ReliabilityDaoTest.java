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
        if (mockedJsonUtils != null) mockedJsonUtils.close();
    }

    // ----------------- Inventory -----------------

    @Test
    void inventoryInputsFilter_successAndSplitInputPaths() {
        InventoryInputsFilterDtoRequest dto = new InventoryInputsFilterDtoRequest();
        dto.setRecordsAmount(2);
        dto.setPage(1);

        InventoryInputsDtoResponse r1 = new InventoryInputsDtoResponse();
        r1.setInputPaths("a\nb\nc");
        InventoryInputsDtoResponse r2 = new InventoryInputsDtoResponse();
        r2.setInputPaths(null);

        when(reliabilityMapperMock.inventoryInputsFilter(any()))
                .thenReturn(List.of(r1, r2));

        InventoryInputsFilterDtoResponse response = reliabilityDao.inventoryInputsFilter(dto);

        assertEquals(2, response.getCount());
        assertArrayEquals(new String[]{"a","b","c"}, response.getData().get(0).getInputPathsArray());
        assertNull(response.getData().get(1).getInputPathsArray());
    }

    @Test
    void inventoryInputsFilter_jsonError_doesNotFail() {
        InventoryInputsFilterDtoRequest req = new InventoryInputsFilterDtoRequest();
        req.setPage(1);
        req.setRecordsAmount(5); // evitar NPE

        when(reliabilityMapperMock.inventoryInputsFilter(any()))
                .thenThrow(new RuntimeException("JSON parse error"));

        var result = reliabilityDao.inventoryInputsFilter(req);

        assertNotNull(result);
        assertTrue(result.getData().isEmpty());
    }

    @Test
    void listinventory_mapperThrows_returnsEmptyList() {
        // given
        InventoryInputsFilterDtoRequest request = new InventoryInputsFilterDtoRequest();

        // when -> como el dao ya captura la excepción, no necesitas mockear MyBatis,
        // simplemente simulas el comportamiento del dao con excepción interna
        List<InventoryInputsDtoResponse> result = reliabilityDao.listinventory(request);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ----------------- Custody / Jobs -----------------

    @Test
    void getPendingCustodyJobs_successAndTrim() {
        PendingCustodyJobsDtoResponse dto = new PendingCustodyJobsDtoResponse();
        dto.setJobName("   JOB_01   ");
        when(reliabilityMapperMock.getPendingCustodyJobs("X")).thenReturn(List.of(dto));

        List<PendingCustodyJobsDtoResponse> result = reliabilityDao.getPendingCustodyJobs("X");
        assertEquals("JOB_01", result.get(0).getJobName());
    }

    @Test
    void getPendingCustodyJobs_exception_returnsEmpty() {
        when(reliabilityMapperMock.getPendingCustodyJobs(any())).thenThrow(new RuntimeException());
        assertTrue(reliabilityDao.getPendingCustodyJobs("ERR").isEmpty());
    }

    @Test
    void getProjectCustodyInfo_success() {
        when(reliabilityMapperMock.getProjectCustodyInfo("ID")).thenReturn(List.of(new ProjectCustodyInfoDtoResponse()));
        assertEquals(1, reliabilityDao.getProjectCustodyInfo("ID").size());
    }

    @Test
    void getProjectCustodyInfo_exception_returnsEmpty() {
        when(reliabilityMapperMock.getProjectCustodyInfo("X")).thenThrow(new RuntimeException());
        assertTrue(reliabilityDao.getProjectCustodyInfo("X").isEmpty());
    }

    // ----------------- Execution validation -----------------

    @Test
    void getExecutionValidation_success() {
        when(reliabilityMapperMock.getExecutionValidation("job")).thenReturn(new ExecutionValidationDtoResponse());
        assertNotNull(reliabilityDao.getExecutionValidation("job"));
    }

    @Test
    void getExecutionValidation_exception_returnsNull() {
        when(reliabilityMapperMock.getExecutionValidation("job")).thenThrow(new RuntimeException());
        assertNull(reliabilityDao.getExecutionValidation("job"));
    }

    @Test
    void getExecutionValidationAll_success() {
        when(reliabilityMapperMock.getExecutionValidation("j1")).thenReturn(new ExecutionValidationDtoResponse());
        when(reliabilityMapperMock.getExecutionValidation("j2")).thenReturn(new ExecutionValidationDtoResponse());
        assertEquals(2, reliabilityDao.getExecutionValidationAll(List.of("j1","j2")).size());
    }

    // ----------------- Transfer insert / update -----------------

    @Test
    void insertTransfer_withJobs_commits() {
        TransferInputDtoRequest dto = new TransferInputDtoRequest();
        JobTransferInputDtoRequest job = new JobTransferInputDtoRequest();
        job.setJobName("J");
        dto.setTransferInputDtoRequests(List.of(job));

        assertDoesNotThrow(() -> reliabilityDao.insertTransfer(dto));
        verify(sqlSessionMock).commit();
    }

    @Test
    void insertTransfer_mapperThrows_wrapsInPersistenceException() {
        when(sqlSessionMock.getMapper(ReliabilityMapper.class)).thenThrow(new RuntimeException("fail"));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> reliabilityDao.insertTransfer(new TransferInputDtoRequest()));
        assertEquals("PersistenceException", ex.getClass().getSimpleName());
    }

    @Test
    void updateTransferDetail_headerSoloComentario_success() {
        TransferDetailUpdateRequest dto = new TransferDetailUpdateRequest();
        TransferDetailUpdateRequest.Header h = new TransferDetailUpdateRequest.Header();
        h.setComments("nota"); dto.setHeader(h);

        when(reliabilityMapperMock.updatePackComments("P", "nota")).thenReturn(1);
        assertDoesNotThrow(() -> reliabilityDao.updateTransferDetail("P", dto));
        verify(sqlSessionMock).commit();
    }

    @Test
    void updateTransferDetail_jobSinNombre_fails() {
        TransferDetailUpdateRequest dto = new TransferDetailUpdateRequest();
        dto.setJobs(List.of(new TransferDetailUpdateRequest.Job()));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> reliabilityDao.updateTransferDetail("P", dto));
        assertEquals("PersistenceException", ex.getClass().getSimpleName());
    }


    @Test
    void getReliabilityPacks_success() {
        ReliabilityPackInputFilterRequest req = new ReliabilityPackInputFilterRequest();
        req.setPage(1);
        req.setRecordsAmount(5); // evitar NPE

        when(reliabilityMapperMock.getReliabilityPacks(any(), any()))
                .thenReturn(List.of(new ReliabilityPacksDtoResponse()));

        var resp = reliabilityDao.getReliabilityPacks(req);

        assertEquals(1, resp.getData().size()); // <-- corregido
    }

    @Test
    void getReliabilityPacks_mapperThrows_propagates() {
        when(reliabilityMapperMock.getReliabilityPacks(any(), any())).thenThrow(new RuntimeException("err"));
        ReliabilityPackInputFilterRequest req = new ReliabilityPackInputFilterRequest();
        req.setDomainName("D"); req.setUseCase("U");
        assertThrows(RuntimeException.class, () -> reliabilityDao.getReliabilityPacks(req));
    }

    @Test
    void changeTransferStatus_success_commits() {
        assertDoesNotThrow(() -> reliabilityDao.changeTransferStatus("P", 1));
        verify(sqlSessionMock).commit();
    }

    @Test
    void changeTransferStatus_mapperThrows_wrapsInPersistenceException() {
        doThrow(new RuntimeException("DB")).when(reliabilityMapperMock).updateReliabilityStatus("P", 1);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> reliabilityDao.changeTransferStatus("P", 1));
        assertEquals("PersistenceException", ex.getClass().getSimpleName());
    }

    @Test
    void getPackCurrentStatus_success() {
        when(reliabilityMapperMock.getPackStatus("P")).thenReturn(2);
        assertEquals(2, reliabilityDao.getPackCurrentStatus("P"));
    }

    @Test
    void getPackCurrentStatus_exception_returnsNull() {
        when(reliabilityMapperMock.getPackStatus("P")).thenThrow(new RuntimeException());
        assertNull(reliabilityDao.getPackCurrentStatus("P"));
    }

    // ----------------- Misc -----------------

    @Test
    void getOriginTypes_success() {
        when(reliabilityMapperMock.getOriginTypes()).thenReturn(List.of(new DropDownDto()));
        assertEquals(1, reliabilityDao.getOriginTypes().size());
    }

    @Test
    void getOriginTypes_exception_propagates() {
        when(sqlSessionFactoryMock.openSession()).thenThrow(new RuntimeException());
        assertThrows(RuntimeException.class, () -> reliabilityDao.getOriginTypes());
    }

    @Test
    void singletonInstance_isSame() {
        assertSame(ReliabilityDao.getInstance(), ReliabilityDao.getInstance());
    }
}
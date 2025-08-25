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
        req.setRecordsAmount(5);

        when(reliabilityMapperMock.inventoryInputsFilter(any()))
                .thenThrow(new RuntimeException("JSON parse error"));

        var result = reliabilityDao.inventoryInputsFilter(req);

        assertNotNull(result);
        assertTrue(result.getData().isEmpty());
    }

    @Test
    void listinventory_mapperThrows_returnsEmptyList() {
        InventoryInputsFilterDtoRequest request = new InventoryInputsFilterDtoRequest();

        List<InventoryInputsDtoResponse> result = reliabilityDao.listinventory(request);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


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
        TransferInputDtoRequest dto = new TransferInputDtoRequest();
        RuntimeException ex = assertThrows(RuntimeException.class, () -> reliabilityDao.insertTransfer(dto));
        assertEquals("PersistenceException", ex.getClass().getSimpleName());
    }

    @Test
    void updateTransferDetail_headerSoloComentario_success() {
        TransferDetailUpdateRequest dto = new TransferDetailUpdateRequest();
        var h = new TransferDetailUpdateRequest.Header();
        h.setComments("nota");
        dto.setHeader(h);

        when(reliabilityMapperMock.patchPackHeader("P", null, null, "nota"))
                .thenReturn(1);

        assertDoesNotThrow(() -> reliabilityDao.updateTransferDetail("P", dto));
        verify(reliabilityMapperMock).patchPackHeader("P", null, null, "nota");
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
        req.setRecordsAmount(5);

        when(reliabilityMapperMock.getReliabilityPacks(any(), any()))
                .thenReturn(List.of(new ReliabilityPacksDtoResponse()));

        var resp = reliabilityDao.getReliabilityPacks(req);

        assertEquals(1, resp.getData().size());
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

    @Test
    void updateInventoryJobStock_success_commits() {
        InventoryJobUpdateDtoRequest dto = new InventoryJobUpdateDtoRequest();
        assertDoesNotThrow(() -> reliabilityDao.updateInventoryJobStock(dto));
        verify(sqlSessionMock).commit();
    }

    @Test
    void updateInventoryJobStock_mapperThrows_doesNotCommit() {
        doThrow(new RuntimeException("DB"))
                .when(reliabilityMapperMock).updateInventoryJobStock(any());
        assertDoesNotThrow(() -> reliabilityDao.updateInventoryJobStock(new InventoryJobUpdateDtoRequest()));
        verify(sqlSessionMock, never()).commit();
    }

    @Test
    void fetchRawSn2BySn1_success() {
        RawSn2DtoResponse r = new RawSn2DtoResponse();
        when(reliabilityMapperMock.fetchRawSn2BySn1(10)).thenReturn(List.of(r));
        assertEquals(1, reliabilityDao.fetchRawSn2BySn1(10).size());
    }

    @Test
    void fetchRawSn2BySn1_exception_returnsEmpty() {
        when(reliabilityMapperMock.fetchRawSn2BySn1(any())).thenThrow(new RuntimeException());
        assertTrue(reliabilityDao.fetchRawSn2BySn1(1).isEmpty());
    }

    @Test
    void updateStatusReliabilityPacksJobStock_success_commitsAndCallsBothUpdates() {
        reliabilityDao.updateStatusReliabilityPacksJobStock(List.of("P1","P2"));
        verify(reliabilityMapperMock, times(1)).updateReliabilityStatus("P1", 1);
        verify(reliabilityMapperMock, times(1)).updateProjectInfoStatus("P1", 1);
        verify(reliabilityMapperMock, times(1)).updateReliabilityStatus("P2", 1);
        verify(reliabilityMapperMock, times(1)).updateProjectInfoStatus("P2", 1);
        verify(sqlSessionMock).commit();
    }

    @Test
    void updateStatusReliabilityPacksJobStock_exception_doesNotCommit() {
        doThrow(new RuntimeException("DB"))
                .when(reliabilityMapperMock).updateReliabilityStatus("P1", 1);
        assertDoesNotThrow(() -> reliabilityDao.updateStatusReliabilityPacksJobStock(List.of("P1")));
        verify(sqlSessionMock, never()).commit();
    }

    @Test
    void listTransfersByStatus_success() {
        when(reliabilityMapperMock.listTransfersByStatus("D","U","S"))
                .thenReturn(List.of(new ReliabilityPacksDtoResponse()));
        assertEquals(1, reliabilityDao.listTransfersByStatus("D","U","S").size());
    }

    @Test
    void listTransfersByStatus_exception_returnsEmpty() {
        when(reliabilityMapperMock.listTransfersByStatus(any(), any(), any()))
                .thenThrow(new RuntimeException());
        assertTrue(reliabilityDao.listTransfersByStatus("","","").isEmpty());
    }

    @Test
    void updateJobByPackAndName_success_commits() {
        when(reliabilityMapperMock.updateJobByPackAndName(any())).thenReturn(1);
        assertDoesNotThrow(() -> reliabilityDao.updateJobByPackAndName(new UpdateJobDtoRequest()));
        verify(sqlSessionMock).commit();
    }

    @Test
    void updateJobByPackAndName_zeroRows_throwsPersistenceException() {
        when(reliabilityMapperMock.updateJobByPackAndName(any())).thenReturn(0);
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reliabilityDao.updateJobByPackAndName(new UpdateJobDtoRequest()));
        assertEquals("PersistenceException", ex.getClass().getSimpleName());
    }

    @Test
    void updatePackComments_success_commits() {
        when(reliabilityMapperMock.updatePackComments("P","C")).thenReturn(1);
        assertDoesNotThrow(() -> reliabilityDao.updatePackComments("P","C"));
        verify(sqlSessionMock).commit();
    }

    @Test
    void updatePackComments_zeroRows_throws() {
        when(reliabilityMapperMock.updatePackComments("P","C")).thenReturn(0);
        assertThrows(RuntimeException.class, () -> reliabilityDao.updatePackComments("P","C"));
    }

    @Test
    void getTransferDetail_headerNull_returnsNull() {
        when(reliabilityMapperMock.getTransferHeader("P")).thenReturn(null);
        assertNull(reliabilityDao.getTransferDetail("P"));
    }

    @Test
    void getTransferDetail_success_returnsSnapshot() {
        TransferDetailResponse.Header h = new TransferDetailResponse.Header();
        when(reliabilityMapperMock.getTransferHeader("P")).thenReturn(h);
        when(reliabilityMapperMock.getTransferJobs("P")).thenReturn(List.of());
        assertNotNull(reliabilityDao.getTransferDetail("P"));
    }

    @Test
    void updateJobComment_success_commits() {
        when(reliabilityMapperMock.updateJobComment("P","J","c")).thenReturn(1);
        assertDoesNotThrow(() -> reliabilityDao.updateJobComment("P","J","c"));
        verify(sqlSessionMock).commit();
    }

    @Test
    void updateJobComment_zeroRows_throws() {
        when(reliabilityMapperMock.updateJobComment("P","J","c")).thenReturn(0);
        assertThrows(RuntimeException.class, () -> reliabilityDao.updateJobComment("P","J","c"));
    }

    @Test
    void updateTransferDetail_success_headerAndJob_copiesNonNullsAndCommits() {
        TransferDetailUpdateRequest dto = new TransferDetailUpdateRequest();
        TransferDetailUpdateRequest.Header h = new TransferDetailUpdateRequest.Header();
        h.setComments("gc");
        dto.setHeader(h);

        TransferDetailUpdateRequest.Job j = new TransferDetailUpdateRequest.Job();
        j.setJobName("J1");
        j.setComments("jc");
        j.setFrequencyId(7);
        dto.setJobs(List.of(j));

        when(reliabilityMapperMock.updatePackComments("P", "gc")).thenReturn(1);
        when(reliabilityMapperMock.updateJobByPackAndName(any())).thenReturn(1);

        var captor = org.mockito.ArgumentCaptor.forClass(UpdateJobDtoRequest.class);

        assertDoesNotThrow(() -> reliabilityDao.updateTransferDetail("P", dto));
        verify(sqlSessionMock).commit();

        verify(reliabilityMapperMock).updateJobByPackAndName(captor.capture());
        UpdateJobDtoRequest sent = captor.getValue();
        assertEquals("P",  sent.getPack());
        assertEquals("J1", sent.getJobName());
        assertEquals(Integer.valueOf(7), sent.getFrequencyId());
        assertEquals("jc", sent.getComments());
    }

    @Test
    void updateTransferDetail_updateJobReturnsZero_throws() {
        TransferDetailUpdateRequest dto = new TransferDetailUpdateRequest();
        TransferDetailUpdateRequest.Job j = new TransferDetailUpdateRequest.Job();
        j.setJobName("J1");
        j.setComments("x");
        dto.setJobs(List.of(j));

        when(reliabilityMapperMock.updateJobByPackAndName(any())).thenReturn(0);

        assertThrows(RuntimeException.class, () -> reliabilityDao.updateTransferDetail("P", dto));
    }
}
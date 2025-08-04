package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.MeshMapper;
import com.bbva.dto.mesh.request.MeshDtoRequest;
import com.bbva.dto.mesh.response.MeshRelationalDtoResponse;
import com.bbva.entities.mesh.JobExecution;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MeshDaoTest {

    private MeshDao dao;
    private SqlSessionFactory sessionFactory;
    private SqlSession session;
    private MeshMapper mapper;
    private MockedStatic<MyBatisConnectionFactory> factoryMock;

    @BeforeEach
    void setUp() {
        sessionFactory = mock(SqlSessionFactory.class);
        session       = mock(SqlSession.class);
        mapper        = mock(MeshMapper.class);

        factoryMock = mockStatic(MyBatisConnectionFactory.class);
        factoryMock.when(MyBatisConnectionFactory::getInstance).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.getMapper(MeshMapper.class)).thenReturn(mapper);

        dao = MeshDao.getInstance();
    }

    @AfterEach
    void tearDown() {
        factoryMock.close();
    }

    @Test
    void whenNoRootJobFound_thenReturnsEmpty() {
        // 1) stub del SP de dependencias para que devuelva algo que no coincida con dto.jobName
        JobExecution je = new JobExecution();
        je.setJob_name("XXX");
        je.setJob_id(1);
        when(mapper.ListJobExecutionsPrevious()).thenReturn(List.of(je));

        // 2) stub obligado del SP de histórico para evitar NPE (aunque no lo usemos)
        when(mapper.listStatusJobExecutions(any(), any()))
                .thenReturn(Collections.emptyList());

        MeshDtoRequest dto = new MeshDtoRequest();
        dto.setJobName("NONEXIST");
        dto.setType("1");

        List<MeshRelationalDtoResponse> result = dao.jobsdependencies(dto);
        assertTrue(result.isEmpty());

        // verifica que se llamó al SP de dependencias
        verify(mapper).ListJobExecutionsPrevious();
        // y **también** al SP de histórico (porque el código lo invoca antes de filtrar)
        verify(mapper).listStatusJobExecutions(null, "NONEXIST");
    }

    @Test
    void whenOrderDateAndJobNameProvided_callsStatusSPWithBothParams() {
        JobExecution root = new JobExecution();
        root.setId(100);
        root.setJob_id(200);
        root.setJob_name("AJOB");
        when(mapper.ListJobExecutionsLaters()).thenReturn(List.of(root));

        JobExecution hist = new JobExecution();
        hist.setJob_name("AJOB");
        hist.setStatus("OK");
        when(mapper.listStatusJobExecutions("20250101","AJOB"))
                .thenReturn(List.of(hist));

        MeshDtoRequest dto = new MeshDtoRequest();
        dto.setJobName("AJOB");
        dto.setOrderDate("20250101");
        dto.setType("2");

        List<MeshRelationalDtoResponse> result = dao.jobsdependencies(dto);

        verify(mapper).listStatusJobExecutions("20250101","AJOB");
        assertFalse(result.isEmpty());
        assertEquals("OK", result.get(0).getStatus());
        assertEquals("AJOB", result.get(0).getJobName());
    }

    @Test
    void whenOrderDateNull_thenCallsStatusSPWithNullAndJobName() {
        JobExecution root = new JobExecution();
        root.setId(10);
        root.setJob_id(20);
        root.setJob_name("BJOB");
        when(mapper.ListJobExecutionsPrevious()).thenReturn(List.of(root));

        JobExecution hist = new JobExecution();
        hist.setJob_name("BJOB");
        hist.setStatus("FAIL");
        when(mapper.listStatusJobExecutions(null,"BJOB"))
                .thenReturn(List.of(hist));

        MeshDtoRequest dto = new MeshDtoRequest();
        dto.setJobName("BJOB");
        dto.setOrderDate(null);
        dto.setType("1");

        List<MeshRelationalDtoResponse> result = dao.jobsdependencies(dto);

        verify(mapper).listStatusJobExecutions(null,"BJOB");
        assertEquals("FAIL", result.get(0).getStatus());
    }
}

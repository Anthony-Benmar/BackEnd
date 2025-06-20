package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.*;
import com.bbva.dto.issueticket.request.WorkOrderDtoRequest2;
import com.bbva.dto.issueticket.request.sourceTicketDtoRequest;
import com.bbva.dto.issueticket.response.issueTicketDtoResponse;
import com.bbva.dto.issueticket.response.sourceTicketDtoResponse;
import com.bbva.dto.jira.request.IssueBulkDto;
import com.bbva.dto.jira.request.IssueDto;
import com.bbva.dto.jira.request.IssueFeatureDto;
import com.bbva.dto.jira.request.IssueUpdate;
import com.bbva.dto.jira.response.IssueResponse;
import com.bbva.entities.board.Board;
import com.bbva.entities.common.CatalogEntity;
import com.bbva.entities.feature.JiraFeatureEntity;
import com.bbva.entities.feature.JiraFeatureEntity2;
import com.bbva.entities.issueticket.WorkOrder;
import com.bbva.entities.issueticket.WorkOrder2;
import com.bbva.entities.issueticket.WorkOrderDetail;
import com.bbva.entities.template.Template;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IssueTicketDaoTest {

    @Mock
    SqlSessionFactory sqlSessionFactory;
    @Mock
    SqlSession session;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getInstance_ShouldReturnSingleton() {
        IssueTicketDao instance1 = IssueTicketDao.getInstance();
        IssueTicketDao instance2 = IssueTicketDao.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    void findRecordWorkOrder_ReturnsValue() {
        try (MockedStatic<MyBatisConnectionFactory> factoryMock = mockStatic(MyBatisConnectionFactory.class)) {
            SqlSessionFactory mockFactory = mock(SqlSessionFactory.class);
            SqlSession mockSession = mock(SqlSession.class);
            IssueTicketMapper mapper = mock(IssueTicketMapper.class);

            factoryMock.when(MyBatisConnectionFactory::getInstance).thenReturn(mockFactory);
            when(mockFactory.openSession()).thenReturn(mockSession);
            when(mockSession.getMapper(IssueTicketMapper.class)).thenReturn(mapper);
            when(mapper.findRecordWorkOrder(any())).thenReturn(3);

            IssueTicketDao dao = new IssueTicketDao();
            int result = dao.findRecordWorkOrder(new WorkOrder());
            assertEquals(3, result);
            verify(mockSession).close();
        }
    }

    @Test
    void findRecordWorkOrder2_ReturnsValue() {
        try (MockedStatic<MyBatisConnectionFactory> factoryMock = mockStatic(MyBatisConnectionFactory.class)) {
            SqlSessionFactory mockFactory = mock(SqlSessionFactory.class);
            SqlSession mockSession = mock(SqlSession.class);
            IssueTicketMapper mapper = mock(IssueTicketMapper.class);

            factoryMock.when(MyBatisConnectionFactory::getInstance).thenReturn(mockFactory);
            when(mockFactory.openSession()).thenReturn(mockSession);
            when(mockSession.getMapper(IssueTicketMapper.class)).thenReturn(mapper);
            when(mapper.findRecordWorkOrder2(any())).thenReturn(5);

            IssueTicketDao dao = new IssueTicketDao();
            int result = dao.findRecordWorkOrder2(new WorkOrder2());
            assertEquals(5, result);
            verify(mockSession).close();
        }
    }

    @Test
    void insertWorkOrderAndDetail_ShouldCallInsertAndCommit() {
        try (MockedStatic<MyBatisConnectionFactory> factoryMock = mockStatic(MyBatisConnectionFactory.class)) {
            SqlSessionFactory mockFactory = mock(SqlSessionFactory.class);
            SqlSession mockSession = mock(SqlSession.class);
            IssueTicketMapper mapper = mock(IssueTicketMapper.class);

            factoryMock.when(MyBatisConnectionFactory::getInstance).thenReturn(mockFactory);
            when(mockFactory.openSession()).thenReturn(mockSession);
            when(mockSession.getMapper(IssueTicketMapper.class)).thenReturn(mapper);

            WorkOrder wo = new WorkOrder();
            wo.work_order_id = 1;
            WorkOrderDetail wod = new WorkOrderDetail(
                    1,        // work_order_detail_id
                    1,        // work_order_id
                    5,        // template_id
                    "ISSUE",  // issue_code
                    "NEW",    // issue_status_type
                    "user",   // register_user_id
                    new Date(), // register_date
                    null      // end_date
            );
            wod.template_id = 5;
            List<WorkOrderDetail> list = List.of(wod);

            IssueTicketDao dao = new IssueTicketDao();
            dao.insertWorkOrderAndDetail(wo, list);

            verify(mapper).insertWorkOrder(wo);
            verify(mapper).InsertDetailList(list);
            verify(mockSession).commit();
            verify(mockSession).close();
        }
    }

    @Test
    void insertWorkOrderAndDetail2_ShouldCallInsertAndCommit() {
        try (MockedStatic<MyBatisConnectionFactory> factoryMock = mockStatic(MyBatisConnectionFactory.class)) {
            SqlSessionFactory mockFactory = mock(SqlSessionFactory.class);
            SqlSession mockSession = mock(SqlSession.class);
            IssueTicketMapper mapper = mock(IssueTicketMapper.class);

            factoryMock.when(MyBatisConnectionFactory::getInstance).thenReturn(mockFactory);
            when(mockFactory.openSession()).thenReturn(mockSession);
            when(mockSession.getMapper(IssueTicketMapper.class)).thenReturn(mapper);

            WorkOrder2 wo = new WorkOrder2();
            wo.work_order_id = 2;
            WorkOrderDetail wod = new WorkOrderDetail(
                    1,        // work_order_detail_id
                    1,        // work_order_id
                    5,        // template_id
                    "ISSUE",  // issue_code
                    "NEW",    // issue_status_type
                    "user",   // register_user_id
                    new Date(), // register_date
                    null      // end_date
            );
            wod.template_id = 7;
            List<WorkOrderDetail> list = List.of(wod);

            IssueTicketDao dao = new IssueTicketDao();
            dao.insertWorkOrderAndDetail2(wo, list);

            verify(mapper).insertWorkOrder2(wo);
            verify(mapper).InsertDetailList(list);
            verify(mockSession).commit();
            verify(mockSession).close();
        }
    }

    @Test
    void UpdateWorkOrder_ShouldReturnTrue() {
        try (MockedStatic<MyBatisConnectionFactory> factoryMock = mockStatic(MyBatisConnectionFactory.class)) {
            SqlSessionFactory mockFactory = mock(SqlSessionFactory.class);
            SqlSession mockSession = mock(SqlSession.class);
            IssueTicketMapper mapper = mock(IssueTicketMapper.class);

            factoryMock.when(MyBatisConnectionFactory::getInstance).thenReturn(mockFactory);
            when(mockFactory.openSession()).thenReturn(mockSession);
            when(mockSession.getMapper(IssueTicketMapper.class)).thenReturn(mapper);

            WorkOrder wo = new WorkOrder();
            IssueTicketDao dao = new IssueTicketDao();
            boolean res = dao.UpdateWorkOrder(wo);

            assertTrue(res);
            verify(mapper).UpdateWorkOrder(wo);
            verify(mockSession).commit();
            verify(mockSession).close();
        }
    }

    @Test
    void InsertWorkOrderDetail_ShouldReturnTrue() {
        try (MockedStatic<MyBatisConnectionFactory> factoryMock = mockStatic(MyBatisConnectionFactory.class)) {
            SqlSessionFactory mockFactory = mock(SqlSessionFactory.class);
            SqlSession mockSession = mock(SqlSession.class);
            IssueTicketMapper mapper = mock(IssueTicketMapper.class);

            factoryMock.when(MyBatisConnectionFactory::getInstance).thenReturn(mockFactory);
            when(mockFactory.openSession()).thenReturn(mockSession);
            when(mockSession.getMapper(IssueTicketMapper.class)).thenReturn(mapper);
            WorkOrderDetail wod = new WorkOrderDetail(
                    1,        // work_order_detail_id
                    1,        // work_order_id
                    5,        // template_id
                    "ISSUE",  // issue_code
                    "NEW",    // issue_status_type
                    "user",   // register_user_id
                    new Date(), // register_date
                    null      // end_date
            );

            List<WorkOrderDetail> details = List.of(wod);

            IssueTicketDao dao = new IssueTicketDao();
            boolean res = dao.InsertWorkOrderDetail(details);
            assertTrue(res);
            verify(mapper).InsertDetailList(details);
            verify(mockSession).commit();
            verify(mockSession).close();
        }
    }

    @Test
    void InsertWorkOrderDetail_WithSession_ShouldReturnTrue() {
        SqlSession mockSession = mock(SqlSession.class);
        IssueTicketMapper mapper = mock(IssueTicketMapper.class);
        when(mockSession.getMapper(IssueTicketMapper.class)).thenReturn(mapper);

        WorkOrderDetail wod = new WorkOrderDetail(
                1,        // work_order_detail_id
                1,        // work_order_id
                5,        // template_id
                "ISSUE",  // issue_code
                "NEW",    // issue_status_type
                "user",   // register_user_id
                new Date(), // register_date
                null      // end_date
        );
        List<WorkOrderDetail> details = List.of(wod);
        IssueTicketDao dao = new IssueTicketDao();
        boolean res = dao.InsertWorkOrderDetail(mockSession, details);
        assertTrue(res);
        verify(mapper).InsertDetailList(details);
    }

    @Test
    void ListWorkOrder_ShouldReturnList() {
        try (MockedStatic<MyBatisConnectionFactory> factoryMock = mockStatic(MyBatisConnectionFactory.class)) {
            SqlSessionFactory mockFactory = mock(SqlSessionFactory.class);
            SqlSession mockSession = mock(SqlSession.class);
            IssueTicketMapper mapper = mock(IssueTicketMapper.class);

            factoryMock.when(MyBatisConnectionFactory::getInstance).thenReturn(mockFactory);
            when(mockFactory.openSession()).thenReturn(mockSession);
            when(mockSession.getMapper(IssueTicketMapper.class)).thenReturn(mapper);

            List<WorkOrder> mockList = List.of(new WorkOrder());
            when(mapper.ListWorkOrder(anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(mockList);

            IssueTicketDao dao = new IssueTicketDao();
            List<WorkOrder> result = dao.ListWorkOrder(1);
            assertEquals(1, result.size());
            verify(mockSession).close();
        }
    }

    @Test
    void ListWorkOrderDetails_ShouldReturnList() {
        try (MockedStatic<MyBatisConnectionFactory> factoryMock = mockStatic(MyBatisConnectionFactory.class)) {
            SqlSessionFactory mockFactory = mock(SqlSessionFactory.class);
            SqlSession mockSession = mock(SqlSession.class);
            IssueTicketMapper mapper = mock(IssueTicketMapper.class);

            factoryMock.when(MyBatisConnectionFactory::getInstance).thenReturn(mockFactory);
            when(mockFactory.openSession()).thenReturn(mockSession);
            when(mockSession.getMapper(IssueTicketMapper.class)).thenReturn(mapper);
            WorkOrderDetail wod = new WorkOrderDetail(
                    1,        // work_order_detail_id
                    1,        // work_order_id
                    5,        // template_id
                    "ISSUE",  // issue_code
                    "NEW",    // issue_status_type
                    "user",   // register_user_id
                    new Date(), // register_date
                    null      // end_date
            );
            List<WorkOrderDetail> mockList = List.of(wod);
            when(mapper.ListWorkOrderDetails(anyInt(), anyInt(), anyInt(), anyInt())).thenReturn(mockList);

            IssueTicketDao dao = new IssueTicketDao();
            List<WorkOrderDetail> result = dao.ListWorkOrderDetails(1);
            assertEquals(1, result.size());
            verify(mockSession).close();
        }
    }
}
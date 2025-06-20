package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.*;
import com.bbva.dto.jira.request.IssueDto;
import com.bbva.entities.board.Board;
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

    private SqlSessionFactory sqlSessionFactoryMock;
    private SqlSession sqlSessionMock;
    private BoardMapper boardMapperMock;
    private TemplateMapper templateMapperMock;
    private MockedStatic<MyBatisConnectionFactory> mockedFactory;
    private IssueTicketMapper issueTicketMapperMock;
    private IssueTicketDao dao;

    @BeforeEach
    void setUp() {
        sqlSessionFactoryMock = mock(SqlSessionFactory.class);
        sqlSessionMock = mock(SqlSession.class);
        boardMapperMock = mock(BoardMapper.class);
        templateMapperMock = mock(TemplateMapper.class);
        issueTicketMapperMock = mock(IssueTicketMapper.class); // <-- esto es lo que te falta!

        mockedFactory = mockStatic(MyBatisConnectionFactory.class);
        mockedFactory.when(MyBatisConnectionFactory::getInstance).thenReturn(sqlSessionFactoryMock);

        when(sqlSessionFactoryMock.openSession()).thenReturn(sqlSessionMock);
        when(sqlSessionMock.getMapper(BoardMapper.class)).thenReturn(boardMapperMock);
        when(sqlSessionMock.getMapper(TemplateMapper.class)).thenReturn(templateMapperMock);
        when(sqlSessionMock.getMapper(IssueTicketMapper.class)).thenReturn(issueTicketMapperMock);

        dao = new IssueTicketDao();
    }

    @AfterEach
    void tearDown() {
        mockedFactory.close();
    }

    @Test
    void getInstance_ShouldReturnSingleton() {
        IssueTicketDao instance1 = IssueTicketDao.getInstance();
        IssueTicketDao instance2 = IssueTicketDao.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    void testGetDataRequestIssueJira_basicMapping() {
        WorkOrder workOrder = new WorkOrder(1, "FEAT", "F1", 1, 123, "SRC1", "SRCNAME", 2, 1, 1, "user", new Date(), null, 0);
        workOrder.feature = "FEAT";
        workOrder.folio = "FOL123";
        workOrder.source_id = "SRCID";
        workOrder.board_id = 100;

        WorkOrderDetail detail = new WorkOrderDetail(10, 1, 111, null, null, null, null, null);
        List<WorkOrderDetail> details = List.of(detail);

        Board board = new Board();
        board.board_id = 100;
        board.project_jira_id = "PJID";
        board.project_jira_key = "PROJKEY";
        board.board_jira_id = "BRD100";

        Template template = new Template();
        template.template_id = 111;
        template.label_one = "LABEL";
        template.name = "Story [fuente]";
        template.description = "desc";

        when(boardMapperMock.list()).thenReturn(List.of(board));
        when(templateMapperMock.list()).thenReturn(List.of(template));

        Map<Integer, IssueDto> result = dao.getDataRequestIssueJira(workOrder, details);

        assertNotNull(result);
        assertEquals(1, result.size());
        IssueDto issue = result.get(111);
        assertNotNull(issue);
        assertNotNull(issue.fields);
        assertEquals("PJID", issue.fields.project.id);
        assertEquals("PROJKEY", issue.fields.project.key);
        assertEquals(List.of("BRD100"), issue.fields.customfield_13300);
        assertEquals("Technical", issue.fields.customfield_10270.value);
        assertEquals("20247", issue.fields.customfield_10270.id);
        assertEquals(false, issue.fields.customfield_10270.disabled);
        assertTrue(issue.fields.labels.contains("P-LABEL"));
        assertTrue(issue.fields.labels.contains("F-FOL123"));
        assertTrue(issue.fields.labels.contains("ID-SRCID"));
        assertEquals("FEAT", issue.fields.customfield_10004);
        assertEquals("Story SRCNAME", issue.fields.summary);
        assertEquals("desc", issue.fields.description);
        assertEquals("Story", issue.fields.issuetype.name);
    }

    @Test
    void testGetDataRequestIssueJira_emptyDetailsReturnsEmptyMap() {
        WorkOrder workOrder = new WorkOrder(1, "FEAT", "F1", 1, 123, "SRC1", "SRCNAME", 2, 1, 1, "user", new Date(), null, 0);

        List<WorkOrderDetail> details = new ArrayList<>();

        when(boardMapperMock.list()).thenReturn(List.of());
        when(templateMapperMock.list()).thenReturn(List.of());

        Map<Integer, IssueDto> result = dao.getDataRequestIssueJira(workOrder, details);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findRecordWorkOrder_returnsMapperValue() {
        WorkOrder workOrder = new WorkOrder();
        when(issueTicketMapperMock.findRecordWorkOrder(workOrder)).thenReturn(42);

        int result = dao.findRecordWorkOrder(workOrder);

        assertEquals(42, result);
        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(IssueTicketMapper.class);
        verify(issueTicketMapperMock).findRecordWorkOrder(workOrder);
        verify(sqlSessionMock).close();
    }

    @Test
    void findRecordWorkOrder_returnsZeroOnException() {
        WorkOrder workOrder = new WorkOrder();
        when(sqlSessionFactoryMock.openSession()).thenThrow(new RuntimeException("DB error"));

        int result = dao.findRecordWorkOrder(workOrder);

        assertEquals(0, result);
        // LOGGER logs the error but we do not assert logs here
    }

    @Test
    void findRecordWorkOrder2_returnsMapperValue() {
        WorkOrder2 workOrder2 = new WorkOrder2();
        when(issueTicketMapperMock.findRecordWorkOrder2(workOrder2)).thenReturn(99);

        int result = dao.findRecordWorkOrder2(workOrder2);

        assertEquals(99, result);
        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(IssueTicketMapper.class);
        verify(issueTicketMapperMock).findRecordWorkOrder2(workOrder2);
        verify(sqlSessionMock).close();
    }

    @Test
    void findRecordWorkOrder2_returnsZeroOnException() {
        WorkOrder2 workOrder2 = new WorkOrder2();
        when(sqlSessionFactoryMock.openSession()).thenThrow(new RuntimeException("DB error"));

        int result = dao.findRecordWorkOrder2(workOrder2);

        assertEquals(0, result);
    }

    @Test
    void insertWorkOrderAndDetail_happyPath_insertsAndCommits() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.work_order_id = 123;

        WorkOrderDetail detail1 = new WorkOrderDetail(
                null, null, null, null, null, null, null, null
        );
        WorkOrderDetail detail2 = new WorkOrderDetail(
                null, null, null, null, null, null, null, null
        );
        List<WorkOrderDetail> details = Arrays.asList(detail1, detail2);

        // Spies para verificar el setWork_order_id
        WorkOrderDetail spyDetail1 = spy(detail1);
        WorkOrderDetail spyDetail2 = spy(detail2);
        List<WorkOrderDetail> spyDetails = Arrays.asList(spyDetail1, spyDetail2);

        dao.insertWorkOrderAndDetail(workOrder, spyDetails);

        verify(issueTicketMapperMock).insertWorkOrder(workOrder);
        verify(issueTicketMapperMock).InsertDetailList(spyDetails);
        verify(sqlSessionMock).commit();
        verify(spyDetail1).setWork_order_id(123);
        verify(spyDetail2).setWork_order_id(123);
        verify(sqlSessionMock).close();
    }

    @Test
    void insertWorkOrderAndDetail_insertThrows_exceptionRollsBackAndLogs() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.work_order_id = 123;
        WorkOrderDetail detail1 = new WorkOrderDetail(
                null, null, null, null, null, null, null, null
        );
        List<WorkOrderDetail> details = List.of(detail1);

        doThrow(new RuntimeException("insert fail")).when(issueTicketMapperMock).insertWorkOrder(workOrder);

        dao.insertWorkOrderAndDetail(workOrder, details);

        verify(issueTicketMapperMock).insertWorkOrder(workOrder);
        verify(sqlSessionMock).rollback();
        verify(sqlSessionMock).close();
    }

    @Test
    void insertWorkOrderAndDetail_insertDetailListThrows_exceptionRollsBackAndLogs() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.work_order_id = 123;
        WorkOrderDetail detail1 = new WorkOrderDetail(
                null, null, null, null, null, null, null, null
        );
        List<WorkOrderDetail> details = List.of(detail1);

        doNothing().when(issueTicketMapperMock).insertWorkOrder(workOrder);
        doThrow(new RuntimeException("insert detail fail")).when(issueTicketMapperMock).InsertDetailList(details);

        dao.insertWorkOrderAndDetail(workOrder, details);

        verify(issueTicketMapperMock).insertWorkOrder(workOrder);
        verify(issueTicketMapperMock).InsertDetailList(details);
        verify(sqlSessionMock).rollback();
        verify(sqlSessionMock).close();
    }

    @Test
    void insertWorkOrderAndDetail_outerTryThrows_exceptionLogs() {
        when(sqlSessionFactoryMock.openSession()).thenThrow(new RuntimeException("connection error"));

        WorkOrder workOrder = new WorkOrder();
        List<WorkOrderDetail> details = List.of();

        dao.insertWorkOrderAndDetail(workOrder, details);

        verify(sqlSessionFactoryMock).openSession();
    }

    @Test
    void insertWorkOrderAndDetail2_happyPath_insertsAndCommits() {
        WorkOrder2 workOrder2 = new WorkOrder2();
        workOrder2.work_order_id = 555;
        WorkOrderDetail detail1 = new WorkOrderDetail(null, null, null, null, null, null, null, null);
        WorkOrderDetail detail2 = new WorkOrderDetail(null, null, null, null, null, null, null, null);
        List<WorkOrderDetail> details = Arrays.asList(detail1, detail2);

        // Spies para verificar el setWork_order_id
        WorkOrderDetail spyDetail1 = spy(detail1);
        WorkOrderDetail spyDetail2 = spy(detail2);
        List<WorkOrderDetail> spyDetails = Arrays.asList(spyDetail1, spyDetail2);

        dao.insertWorkOrderAndDetail2(workOrder2, spyDetails);

        verify(issueTicketMapperMock).insertWorkOrder2(workOrder2);
        verify(issueTicketMapperMock).InsertDetailList(spyDetails);
        verify(sqlSessionMock).commit();
        verify(spyDetail1).setWork_order_id(555);
        verify(spyDetail2).setWork_order_id(555);
        verify(sqlSessionMock).close();
    }

    @Test
    void insertWorkOrderAndDetail2_insertThrows_exceptionRollsBackAndLogs() {
        WorkOrder2 workOrder2 = new WorkOrder2();
        workOrder2.work_order_id = 555;
        WorkOrderDetail detail1 = new WorkOrderDetail(null, null, null, null, null, null, null, null);
        List<WorkOrderDetail> details = List.of(detail1);

        doThrow(new RuntimeException("insert fail")).when(issueTicketMapperMock).insertWorkOrder2(workOrder2);

        dao.insertWorkOrderAndDetail2(workOrder2, details);

        verify(issueTicketMapperMock).insertWorkOrder2(workOrder2);
        verify(sqlSessionMock).rollback();
        verify(sqlSessionMock).close();
    }

    @Test
    void insertWorkOrderAndDetail2_insertDetailListThrows_exceptionRollsBackAndLogs() {
        WorkOrder2 workOrder2 = new WorkOrder2();
        workOrder2.work_order_id = 555;
        WorkOrderDetail detail1 = new WorkOrderDetail(null, null, null, null, null, null, null, null);
        List<WorkOrderDetail> details = List.of(detail1);

        doNothing().when(issueTicketMapperMock).insertWorkOrder2(workOrder2);
        doThrow(new RuntimeException("insert detail fail")).when(issueTicketMapperMock).InsertDetailList(details);

        dao.insertWorkOrderAndDetail2(workOrder2, details);

        verify(issueTicketMapperMock).insertWorkOrder2(workOrder2);
        verify(issueTicketMapperMock).InsertDetailList(details);
        verify(sqlSessionMock).rollback();
        verify(sqlSessionMock).close();
    }

    @Test
    void insertWorkOrderAndDetail2_outerTryThrows_exceptionLogs() {
        when(sqlSessionFactoryMock.openSession()).thenThrow(new RuntimeException("connection error"));

        WorkOrder2 workOrder2 = new WorkOrder2();
        List<WorkOrderDetail> details = List.of();

        dao.insertWorkOrderAndDetail2(workOrder2, details);

        verify(sqlSessionFactoryMock).openSession();
    }

    @Test
    void updateWorkOrder_happyPath_updatesAndCommits() {
        WorkOrder workOrder = new WorkOrder();

        boolean result = dao.UpdateWorkOrder(workOrder);

        assertTrue(result);
        verify(issueTicketMapperMock).UpdateWorkOrder(workOrder);
        verify(sqlSessionMock).commit();
        verify(sqlSessionMock).close();
    }

    @Test
    void updateWorkOrder_exceptionRollsBackAndLogs() {
        WorkOrder workOrder = new WorkOrder();
        // Simula excepción al abrir la sesión (también puedes simularla en UpdateWorkOrder si lo prefieres)
        when(sqlSessionFactoryMock.openSession()).thenThrow(new RuntimeException("DB fail"));

        boolean result = dao.UpdateWorkOrder(workOrder);

        assertFalse(result);
        verify(sqlSessionFactoryMock).openSession();
        // No hace falta verificar más, ya que la excepción ocurre antes de obtener el mapper
    }

    @Test
    void insertWorkOrderDetail_happyPath_insertsAndCommits() {
        WorkOrderDetail detail1 = new WorkOrderDetail(null, null, null, null, null, null, null, null);
        WorkOrderDetail detail2 = new WorkOrderDetail(null, null, null, null, null, null, null, null);
        List<WorkOrderDetail> details = Arrays.asList(detail1, detail2);

        boolean result = dao.InsertWorkOrderDetail(details);

        assertTrue(result);
        verify(issueTicketMapperMock).InsertDetailList(details);
        verify(sqlSessionMock).commit();
        verify(sqlSessionMock).close();
    }

    @Test
    void insertWorkOrderDetail_exceptionLogsAndReturnsFalse() {
        WorkOrderDetail detail1 = new WorkOrderDetail(null, null, null, null, null, null, null, null);
        List<WorkOrderDetail> details = List.of(detail1);

        doThrow(new RuntimeException("DB error")).when(issueTicketMapperMock).InsertDetailList(details);

        boolean result = dao.InsertWorkOrderDetail(details);

        assertFalse(result);
        verify(issueTicketMapperMock).InsertDetailList(details);
        verify(sqlSessionMock).close();
    }

    @Test
    void insertWorkOrderDetail_withSession_happyPath_insertsAndReturnsTrue() {
        WorkOrderDetail detail1 = new WorkOrderDetail(null, null, null, null, null, null, null, null);
        WorkOrderDetail detail2 = new WorkOrderDetail(null, null, null, null, null, null, null, null);
        List<WorkOrderDetail> details = Arrays.asList(detail1, detail2);

        boolean result = dao.InsertWorkOrderDetail(sqlSessionMock, details);

        assertTrue(result);
        verify(sqlSessionMock).getMapper(IssueTicketMapper.class);
        verify(issueTicketMapperMock).InsertDetailList(details);
    }

    @Test
    void insertWorkOrderDetail_withSession_exceptionLogsAndReturnsFalse() {
        WorkOrderDetail detail1 = new WorkOrderDetail(null, null, null, null, null, null, null, null);
        List<WorkOrderDetail> details = List.of(detail1);

        when(sqlSessionMock.getMapper(IssueTicketMapper.class)).thenReturn(issueTicketMapperMock);
        doThrow(new RuntimeException("fail")).when(issueTicketMapperMock).InsertDetailList(details);

        boolean result = dao.InsertWorkOrderDetail(sqlSessionMock, details);

        assertFalse(result);
        verify(sqlSessionMock).getMapper(IssueTicketMapper.class);
        verify(issueTicketMapperMock).InsertDetailList(details);
    }

    @Test
    void listWorkOrder_happyPath_returnsWorkOrderList() {
        List<WorkOrder> expectedList = Arrays.asList(new WorkOrder(), new WorkOrder());
        when(issueTicketMapperMock.ListWorkOrder(1, 0, 77, 0)).thenReturn(expectedList);

        List<WorkOrder> result = dao.ListWorkOrder(77);

        assertEquals(expectedList, result);
        verify(issueTicketMapperMock).ListWorkOrder(1, 0, 77, 0);
        verify(sqlSessionMock).close();
    }

    @Test
    void listWorkOrder_exceptionLogsAndReturnsNull() {
        when(sqlSessionFactoryMock.openSession()).thenThrow(new RuntimeException("fail"));

        List<WorkOrder> result = dao.ListWorkOrder(55);

        assertNull(result);
        verify(sqlSessionFactoryMock).openSession();
    }
}
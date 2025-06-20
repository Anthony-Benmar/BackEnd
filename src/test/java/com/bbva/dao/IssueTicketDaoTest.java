package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.*;
import com.bbva.dto.issueticket.request.WorkOrderDtoRequest2;
import com.bbva.dto.issueticket.request.sourceTicketDtoRequest;
import com.bbva.dto.issueticket.response.issueTicketDtoResponse;
import com.bbva.dto.issueticket.response.sourceTicketDtoResponse;
import com.bbva.dto.issueticket.response.sourceTicketGroupByDtoResponse;
import com.bbva.dto.jira.request.*;
import com.bbva.entities.board.Board;
import com.bbva.entities.common.CatalogEntity;
import com.bbva.entities.feature.JiraFeatureEntity;
import com.bbva.entities.issueticket.WorkOrder;
import com.bbva.entities.issueticket.WorkOrderDetail;
import com.bbva.entities.template.Template;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.lang.reflect.Method;
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
    private CatalogMapper catalogMapperMock;


    @BeforeEach
    void setUp() {
        sqlSessionFactoryMock = mock(SqlSessionFactory.class);
        sqlSessionMock = mock(SqlSession.class);
        boardMapperMock = mock(BoardMapper.class);
        templateMapperMock = mock(TemplateMapper.class);
        issueTicketMapperMock = mock(IssueTicketMapper.class); // <-- esto es lo que te falta!
        catalogMapperMock = mock(CatalogMapper.class);

        mockedFactory = mockStatic(MyBatisConnectionFactory.class);
        mockedFactory.when(MyBatisConnectionFactory::getInstance).thenReturn(sqlSessionFactoryMock);

        when(sqlSessionFactoryMock.openSession()).thenReturn(sqlSessionMock);
        when(sqlSessionMock.getMapper(BoardMapper.class)).thenReturn(boardMapperMock);
        when(sqlSessionMock.getMapper(TemplateMapper.class)).thenReturn(templateMapperMock);
        when(sqlSessionMock.getMapper(IssueTicketMapper.class)).thenReturn(issueTicketMapperMock);

        dao = Mockito.spy(new IssueTicketDao());
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

    @Test
    void listWorkOrderDetails_happyPath_returnsDetailList() {
        WorkOrderDetail detail1 = new WorkOrderDetail(null, null, null, null, null, null, null, null);
        WorkOrderDetail detail2 = new WorkOrderDetail(null, null, null, null, null, null, null, null);
        List<WorkOrderDetail> expectedList = Arrays.asList(detail1, detail2);

        when(issueTicketMapperMock.ListWorkOrderDetails(1, 0, 0, 77)).thenReturn(expectedList);

        List<WorkOrderDetail> result = dao.ListWorkOrderDetails(77);

        assertEquals(expectedList, result);
        verify(issueTicketMapperMock).ListWorkOrderDetails(1, 0, 0, 77);
        verify(sqlSessionMock).close();
    }

    @Test
    void listWorkOrderDetails_exceptionLogsAndReturnsNull() {
        when(sqlSessionFactoryMock.openSession()).thenThrow(new RuntimeException("fail"));

        List<WorkOrderDetail> result = dao.ListWorkOrderDetails(55);

        assertNull(result);
        verify(sqlSessionFactoryMock).openSession();
    }

    @Test
    void getDataRequestIssueJira_happyPath_buildsMapCorrectly() {
        // Prep workOrder
        WorkOrder workOrder = new WorkOrder();
        workOrder.board_id = 1;
        workOrder.folio = "FOL123";
        workOrder.source_id = "SRC999";
        workOrder.feature = "NewFeature";
        workOrder.source_name = "MiFuente";

        // Prep details
        WorkOrderDetail detail1 = new WorkOrderDetail(null, null, 100, null, null, null, null, null);
        WorkOrderDetail detail2 = new WorkOrderDetail(null, null, 200, null, null, null, null, null);
        List<WorkOrderDetail> workOrderDetails = Arrays.asList(detail1, detail2);

        // Prep boards
        Board board = new Board();
        board.board_id = 1;
        board.project_jira_id = "JIRA123";
        board.project_jira_key = "JK";
        board.board_jira_id = "BID-11";
        List<Board> boards = List.of(board);

        // Prep templates
        Template template1 = new Template();
        template1.template_id = 100;
        template1.label_one = "LABEL1";
        template1.name = "Plantilla para [fuente]";
        template1.description = "desc1";
        Template template2 = new Template();
        template2.template_id = 200;
        template2.label_one = "LABEL2";
        template2.name = "Otra Plantilla para [fuente]";
        template2.description = "desc2";
        List<Template> templates = List.of(template1, template2);

        when(sqlSessionMock.getMapper(BoardMapper.class)).thenReturn(boardMapperMock);
        when(boardMapperMock.list()).thenReturn(boards);

        when(sqlSessionMock.getMapper(TemplateMapper.class)).thenReturn(templateMapperMock);
        when(templateMapperMock.list()).thenReturn(templates);

        Map<Integer, IssueDto> result = dao.getDataRequestIssueJira(workOrder, workOrderDetails);

        assertNotNull(result);
        assertEquals(2, result.size());

        IssueDto dto1 = result.get(100);
        assertNotNull(dto1);
        assertEquals("JIRA123", dto1.fields.project.id);
        assertEquals("JK", dto1.fields.project.key);
        assertEquals("BID-11", dto1.fields.customfield_13300.get(0));
        assertTrue(dto1.fields.labels.contains("P-LABEL1"));
        assertTrue(dto1.fields.labels.contains("F-FOL123"));
        assertTrue(dto1.fields.labels.contains("ID-SRC999"));
        assertEquals("Plantilla para MiFuente", dto1.fields.summary);
        assertEquals("desc1", dto1.fields.description);

        IssueDto dto2 = result.get(200);
        assertNotNull(dto2);
        assertEquals("Otra Plantilla para MiFuente", dto2.fields.summary);
        assertEquals("desc2", dto2.fields.description);

        verify(boardMapperMock).list();
        verify(templateMapperMock).list();
        verify(sqlSessionMock).close();
    }

//    @Test
//    void getDataRequestIssueJira_noMatchingBoard_setsBoardFieldsToNull() {
//        // workOrder with a board_id that does not match
//        WorkOrder workOrder = new WorkOrder();
//        workOrder.board_id = 999;
//        workOrder.folio = "FOLIO";
//        workOrder.source_id = "SRCID";
//        workOrder.feature = "FEATURE";
//        workOrder.source_name = "FUENTE";
//        List<WorkOrderDetail> workOrderDetails = List.of(new WorkOrderDetail(null, null, 100, null, null, null, null, null));
//        Board board = new Board();
//        board.board_id = 1; // different
//        List<Board> boards = List.of(board);
//
//        Template template = new Template();
//        template.template_id = 100;
//        template.label_one = "LABEL1";
//        template.name = "Plantilla [fuente]";
//        template.description = "desc1";
//        List<Template> templates = List.of(template);
//
//        when(sqlSessionMock.getMapper(BoardMapper.class)).thenReturn(boardMapperMock);
//        when(boardMapperMock.list()).thenReturn(boards);
//
//        when(sqlSessionMock.getMapper(TemplateMapper.class)).thenReturn(templateMapperMock);
//        when(templateMapperMock.list()).thenReturn(templates);
//
//        Map<Integer, IssueDto> result = dao.getDataRequestIssueJira(workOrder, workOrderDetails);
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        IssueDto dto = result.get(100);
//        // Ahora los campos del board deben ser null y las listas deben contener null.
//        assertNull(dto.fields.project.id);
//        assertNull(dto.fields.project.key);
//        assertNotNull(dto.fields.customfield_13300);
//        assertEquals(1, dto.fields.customfield_13300.size());
//        assertNull(dto.fields.customfield_13300.get(0));
//        // Las demás propiedades de template deben seguir funcionando
//        assertTrue(dto.fields.labels.contains("P-LABEL1"));
//        assertTrue(dto.fields.labels.contains("F-FOLIO"));
//        assertTrue(dto.fields.labels.contains("ID-SRCID"));
//        assertEquals("Plantilla FUENTE", dto.fields.summary);
//        assertEquals("desc1", dto.fields.description);
//        verify(boardMapperMock).list();
//        verify(templateMapperMock).list();
//        verify(sqlSessionMock).close();
//    }
//
//
//    @Test
//    void getDataRequestIssueJira_noTemplatesMatchingDetails_setsTemplateFieldsToNull() {
//        WorkOrder workOrder = new WorkOrder();
//        workOrder.board_id = 1;
//        workOrder.folio = "FOLIO";
//        workOrder.source_id = "SRCID";
//        workOrder.feature = "FEATURE";
//        workOrder.source_name = "FUENTE";
//        List<WorkOrderDetail> workOrderDetails = List.of(new WorkOrderDetail(null, null, 222, null, null, null, null, null));
//        Board board = new Board();
//        board.board_id = 1;
//        board.project_jira_id = "JIRA123";
//        board.project_jira_key = "JK";
//        board.board_jira_id = "BID-11";
//        List<Board> boards = List.of(board);
//
//        // templates with ids that do not match workOrderDetails
//        Template template = new Template();
//        template.template_id = 100;
//        template.label_one = "LABEL1";
//        template.name = "Plantilla [fuente]";
//        template.description = "desc1";
//        List<Template> templates = List.of(template);
//
//        when(sqlSessionMock.getMapper(BoardMapper.class)).thenReturn(boardMapperMock);
//        when(boardMapperMock.list()).thenReturn(boards);
//
//        when(sqlSessionMock.getMapper(TemplateMapper.class)).thenReturn(templateMapperMock);
//        when(templateMapperMock.list()).thenReturn(templates);
//
//        Map<Integer, IssueDto> result = dao.getDataRequestIssueJira(workOrder, workOrderDetails);
//
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        IssueDto dto = result.get(222);
//        // Ahora los campos del template deben ser null o no generados
//        assertEquals("JIRA123", dto.fields.project.id);
//        assertEquals("JK", dto.fields.project.key);
//        assertNotNull(dto.fields.customfield_13300);
//        assertEquals(1, dto.fields.customfield_13300.size());
//        assertEquals("BID-11", dto.fields.customfield_13300.get(0));
//        // Campos de template deben ser null
//        assertNull(dto.fields.summary);
//        assertNull(dto.fields.description);
//        verify(boardMapperMock).list();
//        verify(templateMapperMock).list();
//        verify(sqlSessionMock).close();
//    }

    @Test
    void getDataRequestIssueJira2_happyPath_buildsIssueBulkDtoCorrectly() {
        // Prep workOrder
        WorkOrder workOrder = new WorkOrder();
        workOrder.board_id = 1;
        workOrder.folio = "FOL123";
        workOrder.source_id = "SRC999";
        workOrder.feature = "NewFeature";
        workOrder.source_name = "MiFuente";

        // Prep details
        WorkOrderDetail detail1 = new WorkOrderDetail(null, null, 100, null, null, null, null, null);
        WorkOrderDetail detail2 = new WorkOrderDetail(null, null, 200, null, null, null, null, null);
        List<WorkOrderDetail> workOrderDetails = Arrays.asList(detail1, detail2);

        // Prep board
        Board board = new Board();
        board.board_id = 1;
        board.board_jira_id = "BID-11";

        // Prep feature
        JiraFeatureEntity feature = new JiraFeatureEntity();
        feature.jiraProjectId = 123;
        feature.jiraProjectName = "JK";

        // Prep templates
        Template template1 = new Template();
        template1.template_id = 100;
        template1.label_one = "LABEL1";
        template1.name = "Plantilla para [fuente]";
        template1.description = "desc1";
        Template template2 = new Template();
        template2.template_id = 200;
        template2.label_one = "LABEL2";
        template2.name = "Otra Plantilla para [fuente]";
        template2.description = "desc2";
        List<Template> templates = List.of(template1, template2);

        when(sqlSessionMock.getMapper(BoardMapper.class)).thenReturn(boardMapperMock);
        when(boardMapperMock.boardById(1)).thenReturn(board);

        when(sqlSessionMock.getMapper(TemplateMapper.class)).thenReturn(templateMapperMock);
        when(templateMapperMock.listById(Arrays.asList(100, 200))).thenReturn(templates);

        IssueBulkDto result = dao.getDataRequestIssueJira2(workOrder, workOrderDetails, feature);

        assertNotNull(result);
        assertEquals(2, result.issueUpdates.size());

        IssueUpdate update1 = result.issueUpdates.get(0);
        assertEquals("123", update1.fields.project.id);
        assertEquals("JK", update1.fields.project.key);
        assertEquals("BID-11", update1.fields.customfield_13300.get(0));
        assertTrue(update1.fields.labels.contains("P-LABEL1"));
        assertTrue(update1.fields.labels.contains("F-FOL123"));
        assertTrue(update1.fields.labels.contains("ID-SRC999"));
        assertEquals("plantilla para MiFuente", update1.fields.summary);
        assertEquals("desc1", update1.fields.description);

        IssueUpdate update2 = result.issueUpdates.get(1);
        assertEquals("otra plantilla para MiFuente", update2.fields.summary);
        assertEquals("desc2", update2.fields.description);

        verify(boardMapperMock).boardById(1);
        verify(templateMapperMock).listById(Arrays.asList(100, 200));
        verify(sqlSessionMock).close();
    }

    @Test
    void getDataRequestIssueJira2_boardIsNull_throwsNPE() {
        // Este test simplemente documenta el fallo: actualmente si board es null, lanza NullPointerException
        WorkOrder workOrder = new WorkOrder();
        workOrder.board_id = 99;
        workOrder.folio = "FOLIO";
        workOrder.source_id = "SRCID";
        workOrder.feature = "FEATURE";
        workOrder.source_name = "FUENTE";
        WorkOrderDetail detail = new WorkOrderDetail(null, null, 100, null, null, null, null, null);
        List<WorkOrderDetail> workOrderDetails = List.of(detail);

        JiraFeatureEntity feature = new JiraFeatureEntity();
        feature.jiraProjectId = 1;
        feature.jiraProjectName = "JK";

        Template template = new Template();
        template.template_id = 100;
        template.label_one = "LABEL1";
        template.name = "Plantilla [fuente]";
        template.description = "desc1";
        List<Template> templates = List.of(template);

        when(sqlSessionMock.getMapper(BoardMapper.class)).thenReturn(boardMapperMock);
        when(boardMapperMock.boardById(99)).thenReturn(null);

        when(sqlSessionMock.getMapper(TemplateMapper.class)).thenReturn(templateMapperMock);
        when(templateMapperMock.listById(List.of(100))).thenReturn(templates);

        assertThrows(NullPointerException.class, () -> dao.getDataRequestIssueJira2(workOrder, workOrderDetails, feature));
        verify(boardMapperMock).boardById(99);
        verify(templateMapperMock).listById(List.of(100));
        verify(sqlSessionMock).close();
    }

//    @Test
//    void getTeamBacklogByBoardId_boardIsNull_returnsEmptyString() {
//        when(sqlSessionMock.getMapper(BoardMapper.class)).thenReturn(boardMapperMock);
//        when(boardMapperMock.boardById(15)).thenReturn(null);
//
//        String result = dao.getTeamBacklogByBoardId(15);
//
//        assertEquals("", result);
//        verify(boardMapperMock).boardById(15);
//        verify(sqlSessionMock).close();
//    }

    @Test
    void getDataRequestFeatureJira_happyPath_mapsAllFields() {
        // Dummies para dependencias
        WorkOrderDtoRequest2 dto = new WorkOrderDtoRequest2();
        dto.boardId = 1;
        dto.jiraProjectName = "JIRAKEY";
        dto.feature = "Feature X";
        dto.sprintEst = "Sprint 5";
        dto.setE2e("e2e-1");
        dto.setPeriod(Arrays.asList("2025-Q3"));
        // Mock de getBoardById
        Board board = new Board();
        board.board_jira_id = "BJID-123";
        when(dao.getBoardById(1)).thenReturn(board);

        IssueFeatureDto result = dao.getDataRequestFeatureJira(dto);

        assertNotNull(result);
        assertNotNull(result.fields);
        Fields fields = result.fields;
        assertEquals("JIRAKEY", fields.project.key);
        assertEquals("Feature", fields.issuetype.name);
        assertEquals("Feature X", fields.summary);
        assertEquals("Feature X", fields.customfield_10006);
        assertEquals("Criterios de aceptación a definir", fields.customfield_10260);
        assertEquals("Enabler Delivery", fields.customfield_19001.value);
        assertEquals("Committed", fields.customfield_10265.value);
        assertEquals("Medium", fields.priority.name);
        assertEquals(Arrays.asList("BJID-123"), fields.customfield_13300);
        assertEquals("e2e-1", fields.customfield_12323);
        assertEquals(Arrays.asList("2025-Q3"), fields.customfield_10264);
        assertNotNull(fields.labels);
        // Si tu helper crea labels tipo "feature-feature_x"
        assertNotNull(fields.description);
        // Verifica que la descripción contiene datos del board si aplica
        assertTrue(fields.description.contains("BJID-123"));
    }

//    @Test
//    void getDataRequestFeatureJira_boardNull_doesNotSetCustomField13300() {
//        WorkOrderDtoRequest2 dto = new WorkOrderDtoRequest2();
//        dto.boardId = 2;
//        dto.jiraProjectName = "JIRAKEY";
//        dto.feature = "Feature Y";
//        when(dao.getBoardById(2)).thenReturn(null);
//
//        IssueFeatureDto result = dao.getDataRequestFeatureJira(dto);
//
//        assertNotNull(result);
//        assertNull(result.fields.customfield_13300);
//    }

    @Test
    void getDataRequestFeatureJira_sprintEstimateNull_doesNotSetCustomField10272() {
        WorkOrderDtoRequest2 dto = new WorkOrderDtoRequest2();
        dto.boardId = 3;
        dto.jiraProjectName = "JIRAKEY";
        dto.feature = "Feature Z";
        dto.sprintEst = null;
        when(dao.getBoardById(3)).thenReturn(new Board());

        IssueFeatureDto result = dao.getDataRequestFeatureJira(dto);

        assertNotNull(result);
        assertNull(result.fields.customfield_10272);
    }

    @Test
    void createSelectField_setsValue_reflection() throws Exception {
        Method m = IssueTicketDao.class.getDeclaredMethod("createSelectField", String.class);
        m.setAccessible(true);
        Customfield cf = (Customfield) m.invoke(dao, "foo");
        assertNotNull(cf);
        assertEquals("foo", cf.value);
    }

    @Test
    void generateFeatureDescription_fillsAllFields_reflection() throws Exception {
        Method m = IssueTicketDao.class.getDeclaredMethod("generateFeatureDescription", WorkOrderDtoRequest2.class, String.class);
        m.setAccessible(true);
        WorkOrderDtoRequest2 dto = new WorkOrderDtoRequest2();
        dto.sourceName = "FuenteA";
        dto.sourceId = "SRC-1";
        dto.folio = "FOL-77";
        dto.faseId = "PRE";
        dto.sprintEst = "5";
        dto.flowType = 1;
        dto.jiraProjectName = "JKEY";
        String board = "BID-11";
        String desc = (String) m.invoke(dao, dto, board);

        assertTrue(desc.contains("Fuente: FuenteA"));
        assertTrue(desc.contains("ID Fuente: SRC-1"));
        assertTrue(desc.contains("Folio: FOL-77"));
        assertTrue(desc.contains("Fase: PRE"));
        assertTrue(desc.contains("Sprint Estimado: Sprint 5"));
        assertTrue(desc.contains("Proyecto ID: JKEY"));
        assertTrue(desc.contains("Board ID: BID-11"));
        assertTrue(desc.contains("Esta feature agrupa las stories necesarias"));
    }

    @Test
    void getBoardById_happyPath_returnsBoard() {
        Board board = new Board();
        board.board_id = 42;
        board.name = "Test Board";

        when(boardMapperMock.boardById(42)).thenReturn(board);

        Board result = dao.getBoardById(42);

        assertNotNull(result);
        assertEquals(42, result.board_id);
        assertEquals("Test Board", result.name);
        verify(sqlSessionMock).close();
    }

    @Test
    void getBoardById_exception_returnsNull() {
        when(sqlSessionFactoryMock.openSession()).thenThrow(new RuntimeException("DB error"));

        Board result = dao.getBoardById(500);

        assertNull(result);
    }

    @Test
    void createFeatureLabels_withNonNullLabels_returnsCopy() throws Exception {
        Method m = IssueTicketDao.class.getDeclaredMethod("createFeatureLabels", WorkOrderDtoRequest2.class);
        m.setAccessible(true);
        WorkOrderDtoRequest2 dto = new WorkOrderDtoRequest2();
        dto.setLabels(Arrays.asList("feature-x", "auto", "onboarding"));
        @SuppressWarnings("unchecked")
        List<String> result = (List<String>) m.invoke(dao, dto);

        assertEquals(3, result.size());
        assertTrue(result.contains("feature-x"));
        assertTrue(result.contains("auto"));
        assertTrue(result.contains("onboarding"));
    }

    @Test
    void createFeatureLabels_withNullLabels_returnsEmptyList() throws Exception {
        Method m = IssueTicketDao.class.getDeclaredMethod("createFeatureLabels", WorkOrderDtoRequest2.class);
        m.setAccessible(true);
        WorkOrderDtoRequest2 dto = new WorkOrderDtoRequest2();
        dto.setLabels(null);
        @SuppressWarnings("unchecked")
        List<String> result = (List<String>) m.invoke(dao, dto);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getDataRequestIssueJiraEdit_happyPath_returnsMap() {
        // Arrange datos de entrada
        WorkOrder workOrder = new WorkOrder();
        workOrder.board_id = 1;
        workOrder.folio = "F-01";
        workOrder.source_id = "SRC-33";
        workOrder.source_name = "SCE";
        workOrder.feature = "Mi Feature";

        WorkOrderDetail detail = new WorkOrderDetail(null, null, 100, null, null, null, null, null);
        detail.issue_code = "ISSUE-1";
        detail.template_id = 17;

        List<WorkOrderDetail> detailList = List.of(detail);

        JiraFeatureEntity feature = new JiraFeatureEntity();
        feature.jiraProjectId = 987;
        feature.jiraProjectName = "JKEY";

        // Board y templates de base de datos
        Board board = new Board();
        board.board_id = 1;
        board.board_jira_id = "BJA-1";

        when(boardMapperMock.list()).thenReturn(List.of(board));

        Template template = new Template();
        template.template_id = 17;
        template.label_one = "SOMELABEL";
        template.name = "Plantilla [fuente]";
        template.description = "Desc. de plantilla";
        when(templateMapperMock.list()).thenReturn(List.of(template));

        // Act
        Map<String, IssueDto> result = dao.getDataRequestIssueJiraEdit(workOrder, detailList, feature);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        IssueDto dto = result.get("ISSUE-1");
        assertNotNull(dto);
        assertEquals("987", dto.fields.project.id);
        assertEquals("JKEY", dto.fields.project.key);
        assertEquals(List.of("BJA-1"), dto.fields.customfield_13300);
        assertEquals("Technical", dto.fields.customfield_10270.value);
        assertEquals("20247", dto.fields.customfield_10270.id);
        assertFalse(dto.fields.customfield_10270.disabled);
        assertTrue(dto.fields.labels.contains("P-SOMELABEL"));
        assertTrue(dto.fields.labels.contains("F-F-01"));
        assertTrue(dto.fields.labels.contains("ID-SRC-33"));
        assertEquals("Mi Feature", dto.fields.customfield_10004);
        assertEquals("plantilla SCE", dto.fields.summary); // lower y replace
        assertEquals("Desc. de plantilla", dto.fields.description);
        assertEquals("Story", dto.fields.issuetype.name);
    }

    @Test
    void getDataRequestIssueJiraEdit_happyPath_returnsMap_2() {
        WorkOrder workOrder = new WorkOrder();
        workOrder.board_id = 1;
        workOrder.folio = "F-01";
        workOrder.source_id = "SRC-33";
        workOrder.source_name = "SCE";
        workOrder.feature = "Mi Feature";

        WorkOrderDetail detail = new WorkOrderDetail(null, null, 100, null, null, null, null, null);
        detail.issue_code = "ISSUE-1";
        detail.template_id = 17;

        List<WorkOrderDetail> detailList = List.of(detail);

        JiraFeatureEntity feature = new JiraFeatureEntity();
        feature.jiraProjectId = 987;
        feature.jiraProjectName = "JKEY";

        Board board = new Board();
        board.board_id = 1;
        board.board_jira_id = "BJA-1";

        when(boardMapperMock.list()).thenReturn(List.of(board));

        Template template = new Template();
        template.template_id = 17;
        template.label_one = "SOMELABEL";
        template.name = "Plantilla [fuente]";
        template.description = "Desc. de plantilla";
        when(templateMapperMock.list()).thenReturn(List.of(template));

        Map<String, IssueDto> result = dao.getDataRequestIssueJiraEdit(workOrder, detailList, feature);

        assertNotNull(result);
        assertEquals(1, result.size());
        IssueDto dto = result.get("ISSUE-1");
        assertNotNull(dto);
        assertEquals("987", dto.fields.project.id);
        assertEquals("JKEY", dto.fields.project.key);
        assertEquals(List.of("BJA-1"), dto.fields.customfield_13300);
        assertEquals("Technical", dto.fields.customfield_10270.value);
        assertEquals("20247", dto.fields.customfield_10270.id);
        assertFalse(dto.fields.customfield_10270.disabled);
        assertTrue(dto.fields.labels.contains("P-SOMELABEL"));
        assertTrue(dto.fields.labels.contains("F-F-01"));
        assertTrue(dto.fields.labels.contains("ID-SRC-33"));
        assertEquals("Mi Feature", dto.fields.customfield_10004);
        assertEquals("plantilla SCE", dto.fields.summary); // lower y replace
        assertEquals("Desc. de plantilla", dto.fields.description);
        assertEquals("Story", dto.fields.issuetype.name);
    }

//    @Test
//    void listSources_returnsEmpty_whenNoWorkOrders() {
//        sourceTicketDtoRequest dto = new sourceTicketDtoRequest();
//        dto.page = 1;
//        dto.records_amount = 10;
//        dto.projectId = 1;
//
//        when(boardMapperMock.list()).thenReturn(Collections.emptyList());
//        when(issueTicketMapperMock.ListWorkOrder(1,0,0,1)).thenReturn(Collections.emptyList());
//        when(catalogMapperMock.getListByCatalog(new int[]{1023})).thenReturn(new ArrayList<>());
//
//        sourceTicketDtoResponse response = dao.listSources(dto);
//
//        assertNotNull(response);
//        assertEquals(0, response.count);
//        assertTrue(response.data.isEmpty());
//    }

//    @Test
//    void listSources_returnsSingleResult_whenOneWorkOrder() {
//        sourceTicketDtoRequest dto = new sourceTicketDtoRequest();
//        dto.page = 1;
//        dto.records_amount = 10;
//        dto.projectId = 1;
//
//        Board board = new Board();
//        board.board_id = 10;
//        board.name = "BoardTest";
//        when(boardMapperMock.list()).thenReturn(Arrays.asList(board));
//
//        CatalogEntity catalog = new CatalogEntity(1, 99, "FlowName", 0);
//        catalog.setElementId(99);
//        catalog.setElementName("FlowName");
//        ArrayList<CatalogEntity> catalogList = new ArrayList<>();
//        catalogList.add(catalog);
//        when(catalogMapperMock.getListByCatalog(any(int[].class))).thenReturn(catalogList);        WorkOrder wo = new WorkOrder();
//        wo.work_order_id = 5;
//        wo.board_id = 10;
//        wo.flow_type = 99;
//        wo.source_id = "SRC";
//        wo.folio = "FOL";
//        wo.source_name = "SN";
//        wo.feature = "FT";
//        wo.project_id = 1;
//        wo.register_date = new Date();
//
//        when(issueTicketMapperMock.ListWorkOrder(1,0,0,1)).thenReturn(Arrays.asList(wo));
//
//        sourceTicketDtoResponse response = dao.listSources(dto);
//
//        assertNotNull(response);
//        assertEquals(1, response.count);
//        assertEquals(1, response.data.size());
//        sourceTicketGroupByDtoResponse group = response.data.get(0);
//        // Aquí puedes agregar asserts según los campos públicos del group
//    }

    @Test
    void listIssuesGenerated_returnsEmpty_whenNoTemplatesOrWorkOrder() {
        sourceTicketDtoRequest dto = new sourceTicketDtoRequest();
        dto.type = 1;
        dto.workOrderId = 1;
        dto.page = 1;
        dto.records_amount = 10;

        when(templateMapperMock.list()).thenReturn(Collections.emptyList());
        when(issueTicketMapperMock.ListWorkOrder(1,0,1,0)).thenReturn(Collections.emptyList());
        when(issueTicketMapperMock.ListWorkOrderDetails(1,0,0,1)).thenReturn(Collections.emptyList());

        issueTicketDtoResponse response = dao.listIssuesGenerated(dto);

        assertNotNull(response);
        assertEquals(0, response.count);
        assertTrue(response.data.isEmpty());
    }

    @Test
    void listIssuesGenerated_returnsResults_whenTemplatesAndDetailsPresent() {
        sourceTicketDtoRequest dto = new sourceTicketDtoRequest();
        dto.type = 2;
        dto.workOrderId = 11;
        dto.page = 1;
        dto.records_amount = 10;

        // Crea un template activo y uno inactivo
        Template template1 = new Template();
        template1.template_id = 9; template1.type_id = 2; template1.status = 1; template1.name = "Test1"; template1.label_one = "L1"; template1.orden = 1;
        Template template2 = new Template();
        template2.template_id = 10; template2.type_id = 2; template2.status = 0; template2.name = "Test2"; template2.label_one = "L2"; template2.orden = 2;

        when(templateMapperMock.list()).thenReturn(Arrays.asList(template1, template2));

        // WorkOrder
        WorkOrder wo = new WorkOrder();
        wo.work_order_id = 11;
        wo.board_id = 100;
        when(issueTicketMapperMock.ListWorkOrder(1,0,11,0)).thenReturn(Arrays.asList(wo));

        // WorkOrderDetail asociado a template1
        WorkOrderDetail wod = new WorkOrderDetail();
        wod.work_order_detail_id = 25;
        wod.template_id = 9;
        wod.issue_code = "ISSUE-1";
        wod.issue_status_type = "3";
        when(issueTicketMapperMock.ListWorkOrderDetails(1,0,0,11)).thenReturn(Arrays.asList(wod));

        issueTicketDtoResponse response = dao.listIssuesGenerated(dto);

        assertNotNull(response);
        assertEquals(1, response.count); // Solo hay un template activo con detalle
        assertFalse(response.data.isEmpty());
        var detail = response.data.get(0);
        assertEquals(9, detail.templateId);
        assertEquals(11, detail.workOrderId);
        assertEquals(25, detail.workOrderDetailId);
    }

    @Test
    void listIssuesGenerated_returnsTemplateWithoutDetails_whenNoWorkOrderDetail() {
        sourceTicketDtoRequest dto = new sourceTicketDtoRequest();
        dto.type = 2;
        dto.workOrderId = 15;
        dto.page = 1;
        dto.records_amount = 10;

        // Solo un template activo
        Template template1 = new Template();
        template1.template_id = 8; template1.type_id = 2; template1.status = 1; template1.name = "NoDetails"; template1.label_one = "LBL"; template1.orden = 5;

        when(templateMapperMock.list()).thenReturn(Arrays.asList(template1));

        // WorkOrder
        WorkOrder wo = new WorkOrder();
        wo.work_order_id = 15;
        wo.board_id = 200;
        when(issueTicketMapperMock.ListWorkOrder(1,0,15,0)).thenReturn(Arrays.asList(wo));

        // No detalles
        when(issueTicketMapperMock.ListWorkOrderDetails(1,0,0,15)).thenReturn(Collections.emptyList());

        issueTicketDtoResponse response = dao.listIssuesGenerated(dto);

        assertNotNull(response);
        // El método siempre agregará el template en result_templates porque no hay detalles
        assertEquals(1, response.count);
        assertEquals(1, response.data.size());
        assertEquals(8, response.data.get(0).templateId);
    }
}
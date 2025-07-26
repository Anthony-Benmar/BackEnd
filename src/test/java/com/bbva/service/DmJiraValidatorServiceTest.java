package com.bbva.service;

import com.bbva.dao.DmJiraValidatorLogDao;
import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.dto.jira.request.JiraValidatorByUrlRequest;
import com.bbva.dto.jira.response.DmJiraValidatorMessageDTO;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DmJiraValidatorServiceTest {

    private DmJiraValidatorService service;

    @BeforeEach
    void setUp() {
        service = Mockito.spy(new DmJiraValidatorService());
    }

    @Test
    void testValidateHistoriaDM_withOneInvalidSubtask() throws Exception {
        DmJiraValidatorLogDao mockDao = mock(DmJiraValidatorLogDao.class);
        MockedStatic<DmJiraValidatorLogDao> mockedDaoStatic = Mockito.mockStatic(DmJiraValidatorLogDao.class);
        mockedDaoStatic.when(DmJiraValidatorLogDao::getInstance).thenReturn(mockDao);
        when(mockDao.insertDmJiraValidatorLog(any())).thenReturn(true);

        MockedStatic<MyBatisConnectionFactory> mockedFactory = Mockito.mockStatic(MyBatisConnectionFactory.class);
        SqlSessionFactory sqlSessionFactoryMock = mock(SqlSessionFactory.class);
        SqlSession sqlSessionMock = mock(SqlSession.class);
        when(sqlSessionFactoryMock.openSession()).thenReturn(sqlSessionMock);
        mockedFactory.when(MyBatisConnectionFactory::getInstance).thenReturn(sqlSessionFactoryMock);

        JsonObject ticketMetadata = new JsonObject();
        JsonObject fields = new JsonObject();
        fields.add("issuetype", tipo("Story"));
        fields.add("customfield_13300", array("2461936"));
        fields.add("subtasks", arraySubtask("DEDATIOEN4-9999"));
        ticketMetadata.add("fields", fields);

        JsonObject subtaskFields = new JsonObject();
        subtaskFields.addProperty("summary", "CS - VOLCADO BUI DM");
        subtaskFields.add("issuetype", tipo("Bug"));
        subtaskFields.add("priority", tipo("Low"));
        subtaskFields.add("status", tipo("Deployed"));
        subtaskFields.add("customfield_13300", array("9999999"));
        subtaskFields.addProperty("description", "");
        subtaskFields.add("labels", array("#TTV_Dictamen", "algo_DM"));
        JsonObject subtaskMetadata = new JsonObject();
        subtaskMetadata.add("fields", subtaskFields);

        JiraValidatorByUrlRequest dto = new JiraValidatorByUrlRequest();
        dto.setUrlJira("https://jira.globaldevtools.bbva.com/browse/DEDATIOEN4-1234");
        dto.setUserName("york.yusel.contractor");
        dto.setToken("xxxx");
        dto.setName("York");

        doReturn(ticketMetadata).when(service).getIssueMetadata(dto, "DEDATIOEN4-1234");
        doReturn(subtaskMetadata).when(service).getIssueMetadata(dto, "DEDATIOEN4-9999");

        List<DmJiraValidatorMessageDTO> messages = service.validateHistoriaDM(dto);

        assertEquals(3, messages.size()); // tipo + backlog + subtarea
        assertEquals("success", messages.get(0).getStatus()); // tipo
        assertEquals("success", messages.get(1).getStatus()); // backlog
        assertEquals("error", messages.get(2).getStatus()); // subtarea inválida
        assertTrue(messages.get(2).getMessage().contains("no cumple"));

        mockedDaoStatic.close();
        mockedFactory.close();
    }

    @Test
    void testValidateHistoriaDM_withUrlJiraInvalidBacklogAndNoSubtasks() throws Exception {
        DmJiraValidatorLogDao mockDao = mock(DmJiraValidatorLogDao.class);
        MockedStatic<DmJiraValidatorLogDao> mockedDaoStatic = Mockito.mockStatic(DmJiraValidatorLogDao.class);
        mockedDaoStatic.when(DmJiraValidatorLogDao::getInstance).thenReturn(mockDao);
        when(mockDao.insertDmJiraValidatorLog(any())).thenReturn(true);

        MockedStatic<MyBatisConnectionFactory> mockedFactory = Mockito.mockStatic(MyBatisConnectionFactory.class);
        SqlSessionFactory sqlSessionFactoryMock = mock(SqlSessionFactory.class);
        SqlSession sqlSessionMock = mock(SqlSession.class);
        when(sqlSessionFactoryMock.openSession()).thenReturn(sqlSessionMock);
        mockedFactory.when(MyBatisConnectionFactory::getInstance).thenReturn(sqlSessionFactoryMock);

        JsonObject ticketMetadata = new JsonObject();
        JsonObject fields = new JsonObject();
        fields.add("issuetype", tipo("Story"));
        fields.add("customfield_13300", new JsonArray()); // caso borde: backlog vacío
        fields.add("subtasks", new JsonArray()); // sin subtareas
        ticketMetadata.add("fields", fields);

        JiraValidatorByUrlRequest dto = new JiraValidatorByUrlRequest();
        dto.setUrlJira("https://jira.globaldevtools.bbva.com/browse/DEDATIOEN4-4321"); // Para testear extractIssueKey()
        dto.setUserName("york.yusel.contractor");
        dto.setToken("xxxx");
        dto.setName("York");

        doReturn(ticketMetadata).when(service).getIssueMetadata(dto, "DEDATIOEN4-4321");

        List<DmJiraValidatorMessageDTO> messages = service.validateHistoriaDM(dto);

        assertEquals(2, messages.size()); // solo tipo y backlog
        assertEquals("success", messages.get(0).getStatus()); // tipo Story
        assertEquals("error", messages.get(1).getStatus());   // backlog vacío

        mockedDaoStatic.close();
        mockedFactory.close();
    }


    private JsonObject tipo(String name) {
        JsonObject obj = new JsonObject();
        obj.addProperty("name", name);
        return obj;
    }

    private JsonArray array(String... values) {
        JsonArray arr = new JsonArray();
        for (String val : values) arr.add(val);
        return arr;
    }

    private JsonArray arraySubtask(String... keys) {
        JsonArray arr = new JsonArray();
        for (String key : keys) {
            JsonObject o = new JsonObject();
            o.addProperty("key", key);
            arr.add(o);
        }
        return arr;
    }
}
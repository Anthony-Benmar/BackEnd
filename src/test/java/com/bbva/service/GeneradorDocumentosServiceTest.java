package com.bbva.service;

import com.bbva.dao.ProjectDao;
import com.bbva.dto.jira.request.DataDocumentosMallas;
import com.bbva.dto.jira.request.DataDocumentosMallasFolders;
import com.bbva.dto.jira.request.DataDocumentosMallasJobName;
import com.bbva.dto.jira.request.GeneradorDocumentosMallasRequest;
import com.bbva.dto.project.request.InsertProjectParticipantDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class GeneradorDocumentosServiceTest {
    private GeneradorDocumentosService generadorDocumentosService;
    private ProjectDao projectDaoMock;

    @BeforeEach
    void setUp() {
        projectDaoMock = mock(ProjectDao.class);
        generadorDocumentosService = new GeneradorDocumentosService(projectDaoMock);
    }

    @Test
    void testGenerarC204MallasDocumento() {
        GeneradorDocumentosMallasRequest dtoMock = crearGeneradorDocumentosMallasRequest();
        List<InsertProjectParticipantDTO> mockParticipants = new ArrayList<>();
        mockParticipants.add(crearParticipante(1, "John Doe", "johndoe", "johndoe@example.com", 123, 7));
        mockParticipants.add(crearParticipante(2, "Jane Smith", "janesmith", "janesmith@example.com", 123, 8));
        mockParticipants.add(crearParticipante(3, "Mike Brown", "mikebrown", "mikebrown@example.com", 123, 5));
        mockParticipants.add(crearParticipante(4, "Sara Connor", "saraconnor", "saraconnor@example.com", 123, 6));

        when(projectDaoMock.getProjectParticipants(319)).thenReturn(mockParticipants);

        byte[] result = generadorDocumentosService.generarC204MallasDocumento(dtoMock);

        assertNotNull(result);
        verify(projectDaoMock, times(1)).getProjectParticipants(319);
    }

    @Test
    void testgenerarP110MallasDocumento() {
        GeneradorDocumentosMallasRequest dtoMock = crearGeneradorDocumentosMallasRequest();
        List<InsertProjectParticipantDTO> mockParticipants = new ArrayList<>();
        mockParticipants.add(crearParticipante(1, "John Doe", "johndoe", "johndoe@example.com", 123, 7));
        mockParticipants.add(crearParticipante(2, "Jane Smith", "janesmith", "janesmith@example.com", 123, 8));
        mockParticipants.add(crearParticipante(3, "Mike Brown", "mikebrown", "mikebrown@example.com", 123, 5));
        mockParticipants.add(crearParticipante(4, "Sara Connor", "saraconnor", "saraconnor@example.com", 123, 6));

        when(projectDaoMock.getProjectParticipants(319)).thenReturn(mockParticipants);

        byte[] result = generadorDocumentosService.generarP110MallasDocumento(dtoMock);

        assertNotNull(result);
        verify(projectDaoMock, times(1)).getProjectParticipants(319);
    }


    private GeneradorDocumentosMallasRequest crearGeneradorDocumentosMallasRequest() {
        GeneradorDocumentosMallasRequest request = new GeneradorDocumentosMallasRequest();
        request.setProjectId(319);
        request.setSdatool("SDATOOL-47281");
        request.setProjectDescription("Marcaje 3.0");
        request.setUserName("clinton.huamani");
        request.setEmployeeId("P01234");
        request.setToken("sadf");
        request.setName("ANTHONY WILFREDO AMBROSIO GARCIA");
        
        DataDocumentosMallas dataDocumentosMallas = new DataDocumentosMallas();
        List<DataDocumentosMallasFolders> folders = new ArrayList<>();

        DataDocumentosMallasFolders folder = getDataDocumentosMallasFolders();

        folders.add(folder);

        dataDocumentosMallas.setFolders(folders);

        request.setDataDocumentosMallas(dataDocumentosMallas);

        return request;
    }

    private static DataDocumentosMallasFolders getDataDocumentosMallasFolders() {
        DataDocumentosMallasFolders folder = new DataDocumentosMallasFolders();
        folder.setFolder("CR-PEBILDIA-T05");

        List<DataDocumentosMallasJobName> jobnames = new ArrayList<>();
        DataDocumentosMallasJobName jobName1 = new DataDocumentosMallasJobName();
        jobName1.setJobName("PBILCP0082");
        jobName1.setEstado("nuevo");

        DataDocumentosMallasJobName jobName2 = new DataDocumentosMallasJobName();
        jobName2.setJobName("PBILDP0010");
        jobName2.setEstado("modificado");

        jobnames.add(jobName1);
        jobnames.add(jobName2);

        folder.setJobnames(jobnames);
        folder.setXml("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                + "<!--Exported at 03-12-2024 11:40:35-->\n"
                + "<DEFTABLE xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"Folder.xsd\">\n"
                + "    <FOLDER DATACENTER=\"CTM_CTRLMCCR\" VERSION=\"919\" PLATFORM=\"UNIX\" FOLDER_NAME=\"CR-PEBILDIA-T05\" MODIFIED=\"False\" LAST_UPLOAD=\"20180618171138UTC\" FOLDER_ORDER_METHOD=\"SYSTEM\" REAL_FOLDER_ID=\"15842\" TYPE=\"1\" USED_BY_CODE=\"0\">\n"
                + "<JOB JOBISN=\"0\" APPLICATION=\"BIL-PE-DATIO\" SUB_APPLICATION=\"BIL-MASTER-CCR\" CMDLINE=\"/opt/datio/sentry-pe/dataproc_sentry.py %%SENTRY_JOB %%SENTRY_OPT &apos;%%SENTRY_PARM&apos;\" JOBNAME=\"PBILCP0082\" DESCRIPTION=\"MASTER -  t_pbil_branch_seller_manager\" CREATED_BY=\"XP54462\" RUN_AS=\"sentry\" CRITICAL=\"0\" TASKTYPE=\"Command\" CYCLIC=\"0\" NODEID=\"PE-SENTRY-00\" INTERVAL=\"00001M\" CONFIRM=\"0\" RETRO=\"0\" MAXWAIT=\"3\" MAXRERUN=\"0\" AUTOARCH=\"1\" MAXDAYS=\"0\" MAXRUNS=\"0\" JAN=\"1\" FEB=\"1\" MAR=\"1\" APR=\"1\" MAY=\"1\" JUN=\"1\" JUL=\"1\" AUG=\"1\" SEP=\"1\" OCT=\"1\" NOV=\"1\" DEC=\"1\" DAYS_AND_OR=\"O\" SHIFT=\"Ignore Job\" SHIFTNUM=\"+00\" SYSDB=\"1\" IND_CYCLIC=\"S\" CREATION_USER=\"XP54462\" CREATION_DATE=\"20241031\" CREATION_TIME=\"084436\" RULE_BASED_CALENDAR_RELATIONSHIP=\"O\" APPL_TYPE=\"OS\" CM_VER=\"N/A\" MULTY_AGENT=\"N\" USE_INSTREAM_JCL=\"N\" VERSION_OPCODE=\"N\" IS_CURRENT_VERSION=\"Y\" VERSION_SERIAL=\"1\" VERSION_HOST=\"WVMCCRXA06\" CYCLIC_TOLERANCE=\"0\" CYCLIC_TYPE=\"C\" PARENT_FOLDER=\"CR-PEBILDIA-T05\">\n" +
                "            <VARIABLE NAME=\"%%SENTRY_JOB\" VALUE=\"-ns pe.pbil.app-id-20741.pro -jn pbil-pe-krb-inm-monthlyaggbillingd-01 -o %%ORDERID\" />\n" +
                "            <VARIABLE NAME=\"%%SENTRY_OPT\" VALUE=\"-b\" />\n" +
                "            <VARIABLE NAME=\"%%SENTRY_PARM\" VALUE=\"{&quot;env&quot;:{&quot;CONTROLM_JOB_ID&quot;:&quot;%%JOBNAME&quot;,&quot;CONTROLM_JOB_FLOW&quot;:&quot;%%SCHEDTAB&quot;}}\" />\n" +
                "            <SHOUT WHEN=\"EXECTIME\" TIME=\"&gt;060\" URGENCY=\"R\" DEST=\"EM\" MESSAGE=\"Excedio el tiempo de ejecucion favor de alertar al aplicativo\" DAYSOFFSET=\"0\" />\n" +
                "            <QUANTITATIVE NAME=\"DATIO_SENTRY_PE\" QUANT=\"1\" ONFAIL=\"R\" ONOK=\"R\" />\n" +
                "            <INCOND NAME=\"PBILCP0065-TO-PBILCP0082\" ODATE=\"ODAT\" AND_OR=\"A\" />\n" +
                "            <OUTCOND NAME=\"PBILCP0065-TO-PBILCP0082\" ODATE=\"ODAT\" SIGN=\"-\" />\n" +
                "            <OUTCOND NAME=\"PBILCP0082-TO-PBILDP0010\" ODATE=\"ODAT\" SIGN=\"+\" />\n" +
                "            <ON STMT=\"*\" CODE=\"OK\">\n" +
                "                <DOFORCEJOB TABLE_NAME=\"CR-PEBILDIA-T05\" NAME=\"PBILDP0010\" ODATE=\"ODAT\" REMOTE=\"N\" />\n" +
                "            </ON>a\n" +
                "            <ON STMT=\"*\" CODE=\"NOTOK\">\n" +
                "                <DOMAIL URGENCY=\"R\" DEST=\"incidencia-malla-datio-peru.group@bbva.com;intramis-comercial-per.group@bbva.com\" SUBJECT=\"Cancelado PBILCP0082 - pbil-pe-krb-inm-monthlyaggbillingd-01 - %%$ODATE\" MESSAGE=\"0038Job execution error, process canceled.\" ATTACH_SYSOUT=\"Y\" />\n" +
                "            </ON>\n" +
                "        </JOB>"
                + " </FOLDER>\n"
                + "</DEFTABLE>");
        return folder;
    }

    public static InsertProjectParticipantDTO crearParticipante(Integer id, String name, String user,
                                                                String email, Integer projectId,
                                                                Integer rolType) {
        InsertProjectParticipantDTO participante = new InsertProjectParticipantDTO();
        participante.projectParticipantId = id;
        participante.participantName = name;
        participante.participantUser = user;
        participante.participantEmail = email;
        participante.projectId = projectId;
        participante.projectRolType = rolType;
        participante.piId = 101;
        participante.createAuditDate = new Date();
        participante.createAuditUser = "testUser";
        participante.updateAuditDate = new Date();
        return participante;
    }
}
package com.bbva.service;

import com.bbva.dao.ProjectDao;
import com.bbva.dto.documentgenerator.request.DataDocumentMesh;
import com.bbva.dto.documentgenerator.request.DataDocumentMeshFolder;
import com.bbva.dto.documentgenerator.request.DataDocumentMeshJobName;
import com.bbva.dto.documentgenerator.request.DocumentGeneratorMeshRequest;
import com.bbva.dto.project.request.InsertProjectParticipantDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class DocumentGeneratorServiceTest {
    private DocumentGeneratorService documentGeneratorService;
    private ProjectDao projectDaoMock;

    @BeforeEach
    void setUp() {
        projectDaoMock = mock(ProjectDao.class);
        documentGeneratorService = new DocumentGeneratorService(projectDaoMock);
    }

    @Test
    void testGenerateDocumentMeshCases() {
        DocumentGeneratorMeshRequest dtoMock = createDocumentGeneratorMeshRequest();
        List<InsertProjectParticipantDTO> mockParticipants = mockListParticipants();
        when(projectDaoMock.getProjectParticipants(319)).thenReturn(mockParticipants);

        byte[] result = documentGeneratorService.generateDocumentMeshCases(dtoMock);

        assertNotNull(result);
        verify(projectDaoMock, times(1)).getProjectParticipants(319);
    }

    @Test
    void testGenerateDocumentMeshTracking() {
        DocumentGeneratorMeshRequest dtoMock = createDocumentGeneratorMeshRequest();
        List<InsertProjectParticipantDTO> mockParticipants = mockListParticipants();

        when(projectDaoMock.getProjectParticipants(319)).thenReturn(mockParticipants);

        byte[] result = documentGeneratorService.generateDocumentMeshTracking(dtoMock);

        assertNotNull(result);
        verify(projectDaoMock, times(1)).getProjectParticipants(319);
    }

    @Test
    void testGenerateDocumentMeshTrackingNoParticipants() {
        DocumentGeneratorMeshRequest dtoMock = createDocumentGeneratorMeshRequest();
        List<InsertProjectParticipantDTO> mockParticipants = new ArrayList<>();

        when(projectDaoMock.getProjectParticipants(319)).thenReturn(mockParticipants);

        byte[] result = documentGeneratorService.generateDocumentMeshTracking(dtoMock);

        assertNotNull(result);
        verify(projectDaoMock, times(1)).getProjectParticipants(319);
    }

    @Test
    void testGenerateNameMeshCases() {
        DocumentGeneratorMeshRequest dtoMock = createDocumentGeneratorMeshRequest();
        String result = documentGeneratorService.generateNameMeshCases(dtoMock);
        assertEquals("CR-PEBILDIA-T05, CR-PEMOLMEN-T02", result, "Los nombres de los folders no coinciden");
    }

    @Test
    void testGenerateNameMeshTracking() {
        DocumentGeneratorMeshRequest dtoMock = createDocumentGeneratorMeshRequest();
        String result = documentGeneratorService.generateNameMeshTracking(dtoMock);
        assertEquals("PBIL_PMOL_Diaria_Mensual", result, "Los nombres de los folders no coinciden");
    }

    private DocumentGeneratorMeshRequest createDocumentGeneratorMeshRequest() {
        DocumentGeneratorMeshRequest request = new DocumentGeneratorMeshRequest();
        request.setProjectId(319);
        request.setSdatool("SDATOOL-47281");
        request.setProjectDescription("Marcaje 3.0");
        request.setUserName("clinton.huamani");
        request.setEmployeeId("P01234");
        request.setToken("sadf");
        request.setName("ANTHONY WILFREDO AMBROSIO GARCIA");
        
        DataDocumentMesh dataDocumentMesh = new DataDocumentMesh();
        List<DataDocumentMeshFolder> folders = new ArrayList<>();
        DataDocumentMeshFolder folderPBIL = getDataDocumentMeshFolderPBIL();
        DataDocumentMeshFolder folderPMOL = getDataDocumentMeshFolderPMOL();
        folders.add(folderPBIL);
        folders.add(folderPMOL);
        dataDocumentMesh.setFolderList(folders);
        request.setDataDocumentMesh(dataDocumentMesh);

        return request;
    }

    private static DataDocumentMeshFolder getDataDocumentMeshFolderPBIL() {
        DataDocumentMeshFolder folder = new DataDocumentMeshFolder();
        folder.setFolderName("CR-PEBILDIA-T05");

        List<DataDocumentMeshJobName> jobnames = new ArrayList<>();
        DataDocumentMeshJobName jobName1 = new DataDocumentMeshJobName();
        jobName1.setJobName("PBILCP0082");
        jobName1.setState("nuevo");

        DataDocumentMeshJobName jobName2 = new DataDocumentMeshJobName();
        jobName2.setJobName("PBILDP0010");
        jobName2.setState("modificado");

        jobnames.add(jobName1);
        jobnames.add(jobName2);

        folder.setJobNames(jobnames);
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

    private static DataDocumentMeshFolder getDataDocumentMeshFolderPMOL() {
        DataDocumentMeshFolder folder = new DataDocumentMeshFolder();
        folder.setFolderName("CR-PEMOLMEN-T02");

        List<DataDocumentMeshJobName> jobnames = new ArrayList<>();
        DataDocumentMeshJobName jobName1 = new DataDocumentMeshJobName();
        jobName1.setJobName("PMOLTP0082");
        jobName1.setState("nuevo");

        DataDocumentMeshJobName jobName2 = new DataDocumentMeshJobName();
        jobName2.setJobName("PMOLVP0010");
        jobName2.setState("modificado");

        jobnames.add(jobName1);
        jobnames.add(jobName2);

        folder.setJobNames(jobnames);
        folder.setXml("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"
                + "<!--Exported at 03-12-2024 11:40:35-->\n"
                + "<DEFTABLE xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"Folder.xsd\">\n"
                + "    <FOLDER DATACENTER=\"CTM_CTRLMCCR\" VERSION=\"919\" PLATFORM=\"UNIX\" FOLDER_NAME=\"CR-PEMOLMEN-T02\" MODIFIED=\"False\" LAST_UPLOAD=\"20180618171138UTC\" FOLDER_ORDER_METHOD=\"SYSTEM\" REAL_FOLDER_ID=\"15842\" TYPE=\"1\" USED_BY_CODE=\"0\">\n"
                + "<JOB JOBISN=\"0\" APPLICATION=\"MOL-PE-DATIO\" SUB_APPLICATION=\"MOL-MASTER-CCR\" CMDLINE=\"/opt/datio/sentry-pe/dataproc_sentry.py %%SENTRY_JOB %%SENTRY_OPT &apos;%%SENTRY_PARM&apos;\" JOBNAME=\"PMOLTP0082\" DESCRIPTION=\"MASTER -  t_pbil_branch_seller_manager\" CREATED_BY=\"XP54462\" RUN_AS=\"sentry\" CRITICAL=\"0\" TASKTYPE=\"Command\" CYCLIC=\"0\" NODEID=\"PE-SENTRY-00\" INTERVAL=\"00001M\" CONFIRM=\"0\" RETRO=\"0\" MAXWAIT=\"3\" MAXRERUN=\"0\" AUTOARCH=\"1\" MAXDAYS=\"0\" MAXRUNS=\"0\" JAN=\"1\" FEB=\"1\" MAR=\"1\" APR=\"1\" MAY=\"1\" JUN=\"1\" JUL=\"1\" AUG=\"1\" SEP=\"1\" OCT=\"1\" NOV=\"1\" DEC=\"1\" DAYS_AND_OR=\"O\" SHIFT=\"Ignore Job\" SHIFTNUM=\"+00\" SYSDB=\"1\" IND_CYCLIC=\"S\" CREATION_USER=\"XP54462\" CREATION_DATE=\"20241031\" CREATION_TIME=\"084436\" RULE_BASED_CALENDAR_RELATIONSHIP=\"O\" APPL_TYPE=\"OS\" CM_VER=\"N/A\" MULTY_AGENT=\"N\" USE_INSTREAM_JCL=\"N\" VERSION_OPCODE=\"N\" IS_CURRENT_VERSION=\"Y\" VERSION_SERIAL=\"1\" VERSION_HOST=\"WVMCCRXA06\" CYCLIC_TOLERANCE=\"0\" CYCLIC_TYPE=\"C\" PARENT_FOLDER=\"CR-PEMOLMEN-T05\">\n" +
                "            <VARIABLE NAME=\"%%SENTRY_JOB\" VALUE=\"-ns pe.pbil.app-id-20741.pro -jn pbil-pe-krb-inm-monthlyaggbillingd-01 -o %%ORDERID\" />\n" +
                "            <VARIABLE NAME=\"%%SENTRY_OPT\" VALUE=\"-b\" />\n" +
                "            <VARIABLE NAME=\"%%SENTRY_PARM\" VALUE=\"{&quot;env&quot;:{&quot;CONTROLM_JOB_ID&quot;:&quot;%%JOBNAME&quot;,&quot;CONTROLM_JOB_FLOW&quot;:&quot;%%SCHEDTAB&quot;}}\" />\n" +
                "            <SHOUT WHEN=\"EXECTIME\" TIME=\"&gt;060\" URGENCY=\"R\" DEST=\"EM\" MESSAGE=\"Excedio el tiempo de ejecucion favor de alertar al aplicativo\" DAYSOFFSET=\"0\" />\n" +
                "            <QUANTITATIVE NAME=\"DATIO_SENTRY_PE\" QUANT=\"1\" ONFAIL=\"R\" ONOK=\"R\" />\n" +
                "            <INCOND NAME=\"PMOLCP0065-TO-PMOLCP0082\" ODATE=\"ODAT\" AND_OR=\"A\" />\n" +
                "            <OUTCOND NAME=\"PMOLCP0065-TO-PMOLCP0082\" ODATE=\"ODAT\" SIGN=\"-\" />\n" +
                "            <OUTCOND NAME=\"PMOLCP0082-TO-PMOLDP0010\" ODATE=\"ODAT\" SIGN=\"+\" />\n" +
                "            <ON STMT=\"*\" CODE=\"OK\">\n" +
                "                <DOFORCEJOB TABLE_NAME=\"CR-PEMOLMEN-T05\" NAME=\"PMOLDP0010\" ODATE=\"ODAT\" REMOTE=\"N\" />\n" +
                "            </ON>a\n" +
                "            <ON STMT=\"*\" CODE=\"NOTOK\">\n" +
                "                <DOMAIL URGENCY=\"R\" DEST=\"incidencia-malla-datio-peru.group@bbva.com;intramis-comercial-per.group@bbva.com\" SUBJECT=\"Cancelado PMOLCP0082 - pbil-pe-krb-inm-monthlyaggbillingd-01 - %%$ODATE\" MESSAGE=\"0038Job execution error, process canceled.\" ATTACH_SYSOUT=\"Y\" />\n" +
                "            </ON>\n" +
                "        </JOB>"+
                "<JOB JOBISN=\"0\" APPLICATION=\"MOL-PE-DATIO\" SUB_APPLICATION=\"MOL-MASTER-CCR\" CMDLINE=\"/opt/datio/sentry-pe/dataproc_sentry.py %%SENTRY_JOB %%SENTRY_OPT &apos;%%SENTRY_PARM&apos;\" JOBNAME=\"PMOLVP0010\" DESCRIPTION=\"MASTER -  t_pbil_branch_seller_manager\" CREATED_BY=\"XP54462\" RUN_AS=\"sentry\" CRITICAL=\"0\" TASKTYPE=\"Command\" CYCLIC=\"0\" NODEID=\"PE-SENTRY-00\" INTERVAL=\"00001M\" CONFIRM=\"0\" RETRO=\"0\" MAXWAIT=\"3\" MAXRERUN=\"0\" AUTOARCH=\"1\" MAXDAYS=\"0\" MAXRUNS=\"0\" JAN=\"1\" FEB=\"1\" MAR=\"1\" APR=\"1\" MAY=\"1\" JUN=\"1\" JUL=\"1\" AUG=\"1\" SEP=\"1\" OCT=\"1\" NOV=\"1\" DEC=\"1\" DAYS_AND_OR=\"O\" SHIFT=\"Ignore Job\" SHIFTNUM=\"+00\" SYSDB=\"1\" IND_CYCLIC=\"S\" CREATION_USER=\"XP54462\" CREATION_DATE=\"20241031\" CREATION_TIME=\"084436\" RULE_BASED_CALENDAR_RELATIONSHIP=\"O\" APPL_TYPE=\"OS\" CM_VER=\"N/A\" MULTY_AGENT=\"N\" USE_INSTREAM_JCL=\"N\" VERSION_OPCODE=\"N\" IS_CURRENT_VERSION=\"Y\" VERSION_SERIAL=\"1\" VERSION_HOST=\"WVMCCRXA06\" CYCLIC_TOLERANCE=\"0\" CYCLIC_TYPE=\"C\" PARENT_FOLDER=\"CR-PEMOLMEN-T05\">\n" +
                "            <VARIABLE NAME=\"%%SENTRY_JOB\" VALUE=\"-ns pe.pbil.app-id-20741.pro -jn pbil-pe-krb-inm-monthlyaggbillingd-01 -o %%ORDERID\" />\n" +
                "            <VARIABLE NAME=\"%%SENTRY_OPT\" VALUE=\"-b\" />\n" +
                "            <VARIABLE NAME=\"%%SENTRY_PARM\" VALUE=\"{&quot;env&quot;:{&quot;CONTROLM_JOB_ID&quot;:&quot;%%JOBNAME&quot;,&quot;CONTROLM_JOB_FLOW&quot;:&quot;%%SCHEDTAB&quot;}}\" />\n" +
                "            <SHOUT WHEN=\"EXECTIME\" TIME=\"&gt;060\" URGENCY=\"R\" DEST=\"EM\" MESSAGE=\"Excedio el tiempo de ejecucion favor de alertar al aplicativo\" DAYSOFFSET=\"0\" />\n" +
                "            <QUANTITATIVE NAME=\"DATIO_SENTRY_PE\" QUANT=\"1\" ONFAIL=\"R\" ONOK=\"R\" />\n" +
                "            <INCOND NAME=\"PMOLCP0065-TO-PMOLCP0082\" ODATE=\"ODAT\" AND_OR=\"A\" />\n" +
                "            <OUTCOND NAME=\"PMOLCP0065-TO-PMOLCP0082\" ODATE=\"ODAT\" SIGN=\"-\" />\n" +
                "            <OUTCOND NAME=\"PMOLCP0082-TO-PMOLDP0010\" ODATE=\"ODAT\" SIGN=\"+\" />\n" +
                "            <ON STMT=\"*\" CODE=\"OK\">\n" +
                "                <DOFORCEJOB TABLE_NAME=\"CR-PEMOLMEN-T05\" NAME=\"PMOLDP0010\" ODATE=\"ODAT\" REMOTE=\"N\" />\n" +
                "            </ON>a\n" +
                "            <ON STMT=\"*\" CODE=\"NOTOK\">\n" +
                "                <DOMAIL URGENCY=\"R\" DEST=\"incidencia-malla-datio-peru.group@bbva.com;intramis-comercial-per.group@bbva.com\" SUBJECT=\"Cancelado PMOLCP0082 - pbil-pe-krb-inm-monthlyaggbillingd-01 - %%$ODATE\" MESSAGE=\"0038Job execution error, process canceled.\" ATTACH_SYSOUT=\"Y\" />\n" +
                "            </ON>\n" +
                "        </JOB>"
                + " </FOLDER>\n"
                + "</DEFTABLE>");
        return folder;
    }

    public static InsertProjectParticipantDTO createParticipant(Integer id, String name, String user,
                                                                String email, Integer projectId,
                                                                Integer rolType) {
        InsertProjectParticipantDTO participant = new InsertProjectParticipantDTO();
        participant.projectParticipantId = id;
        participant.participantName = name;
        participant.participantUser = user;
        participant.participantEmail = email;
        participant.projectId = projectId;
        participant.projectRolType = rolType;
        participant.piId = 101;
        participant.createAuditDate = new Date();
        participant.createAuditUser = "testUser";
        participant.updateAuditDate = new Date();
        return participant;
    }
    private List<InsertProjectParticipantDTO> mockListParticipants (){
        List<InsertProjectParticipantDTO>  mockParticipants = new ArrayList<>();
        mockParticipants.add(createParticipant(1, "John Doe", "johndoe", "johndoe@example.com", 123, 7));
        mockParticipants.add(createParticipant(2, "Jane Smith", "janesmith", "janesmith@example.com", 123, 8));
        mockParticipants.add(createParticipant(3, "Mike Brown", "mikebrown", "mikebrown@example.com", 123, 5));
        mockParticipants.add(createParticipant(4, "Sara Connor", "saraconnor", "saraconnor@example.com", 123, 6));
        return  mockParticipants;
    }
}
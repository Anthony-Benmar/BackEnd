package com.bbva.service;

import com.bbva.dao.ProjectDao;
import com.bbva.dto.documentgenerator.request.DataDocumentMesh;
import com.bbva.dto.documentgenerator.request.DataDocumentMeshFolder;
import com.bbva.dto.documentgenerator.request.DataDocumentMeshJobName;
import com.bbva.dto.documentgenerator.request.DocumentGeneratorMeshRequest;
import com.bbva.dto.project.request.InsertProjectParticipantDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class DocumentGeneratorServiceTest {
    private DocumentGeneratorService documentGeneratorService;
    private ProjectDao projectDaoMock;
    private JiraApiService jiraApiService;
    private BitbucketApiService bitbucketApiService;

    @BeforeEach
    void setUp() {
        projectDaoMock = mock(ProjectDao.class);
        bitbucketApiService = mock(BitbucketApiService.class);
        jiraApiService = mock(JiraApiService.class);
        documentGeneratorService = new DocumentGeneratorService(projectDaoMock, bitbucketApiService, jiraApiService);
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
    void testGenerateDocumentMeshCasesWithJiraUrl() throws Exception {
        DocumentGeneratorMeshRequest dtoMock = createDocumentGeneratorMeshRequestWithJiraUrl();
        List<InsertProjectParticipantDTO> mockParticipants = mockListParticipants();
        String fromHash = "e9fe5c5fa60748baf6e0b18308bdf7dde5889d54";
        String toHash = "0e9556f3a69358eea04da74f0c7fa4d4058a70b3";
        String fullPath = "Local/PMFI/CR-PEMFIMEN-T05.xml";
        when(projectDaoMock.getProjectParticipants(319)).thenReturn(mockParticipants);
        when(jiraApiService.buildJiraQueryUrl(List.of(dtoMock.getUrl()))).thenReturn(getJiraUrl());
        when(jiraApiService.GetJiraAsync(dtoMock.getUserName(), dtoMock.getToken(),getJiraUrl())).thenReturn(getJiraResponse());
        when(jiraApiService.GetJiraAsync(dtoMock.getUserName(), dtoMock.getToken(),getJiraPullRequestUrl())).thenReturn(getJiraPullRequestResponse());
        when(bitbucketApiService.getPullRequestChanges(getBitbucketPullRequestUrl(), dtoMock.getUserName(), dtoMock.getToken())).thenReturn(getBitbucketPullRequestChanges());
        when(bitbucketApiService.getPullRequestFileInfo(getBitbucketPullRequestUrl(), dtoMock.getUserName(), dtoMock.getToken(),fullPath,fromHash)).thenReturn(documentGeneratorService.getDocumentXML(getDocumentBefore()));
        when(bitbucketApiService.getPullRequestFileInfo(getBitbucketPullRequestUrl(), dtoMock.getUserName(), dtoMock.getToken(),fullPath,toHash)).thenReturn(documentGeneratorService.getDocumentXML(getDocumentAfter()));
        byte[] result = documentGeneratorService.generateDocumentMeshCases(dtoMock);

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
    void testGenerateDocumentMeshTrackingWithJiraUrl() throws Exception {
        DocumentGeneratorMeshRequest dtoMock = createDocumentGeneratorMeshRequestWithJiraUrl();
        List<InsertProjectParticipantDTO> mockParticipants = mockListParticipants();
        String fromHash = "e9fe5c5fa60748baf6e0b18308bdf7dde5889d54";
        String toHash = "0e9556f3a69358eea04da74f0c7fa4d4058a70b3";
        String fullPath = "Local/PMFI/CR-PEMFIMEN-T05.xml";
        when(projectDaoMock.getProjectParticipants(319)).thenReturn(mockParticipants);
        when(jiraApiService.buildJiraQueryUrl(List.of(dtoMock.getUrl()))).thenReturn(getJiraUrl());
        when(jiraApiService.GetJiraAsync(dtoMock.getUserName(), dtoMock.getToken(),getJiraUrl())).thenReturn(getJiraResponse());
        when(jiraApiService.GetJiraAsync(dtoMock.getUserName(), dtoMock.getToken(),getJiraPullRequestUrl())).thenReturn(getJiraPullRequestResponse());
        when(bitbucketApiService.getPullRequestChanges(getBitbucketPullRequestUrl(), dtoMock.getUserName(), dtoMock.getToken())).thenReturn(getBitbucketPullRequestChanges());
        when(bitbucketApiService.getPullRequestFileInfo(getBitbucketPullRequestUrl(), dtoMock.getUserName(), dtoMock.getToken(),fullPath,fromHash)).thenReturn(documentGeneratorService.getDocumentXML(getDocumentBefore()));
        when(bitbucketApiService.getPullRequestFileInfo(getBitbucketPullRequestUrl(), dtoMock.getUserName(), dtoMock.getToken(),fullPath,toHash)).thenReturn(documentGeneratorService.getDocumentXML(getDocumentAfter()));
        byte[] result = documentGeneratorService.generateDocumentMeshTracking(dtoMock);

        assertNotNull(result);
        verify(projectDaoMock, times(1)).getProjectParticipants(319);
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
        request.setUrl("");

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

    private DocumentGeneratorMeshRequest createDocumentGeneratorMeshRequestWithJiraUrl() {
        DocumentGeneratorMeshRequest request = new DocumentGeneratorMeshRequest();
        request.setProjectId(319);
        request.setSdatool("SDATOOL-45022");
        request.setProjectDescription("Risk Data Transformation Perú");
        request.setUserName("clinton.huamani");
        request.setEmployeeId("P01234");
        request.setToken("sadf");
        request.setName("ANTHONY WILFREDO AMBROSIO GARCIA");
        request.setUrl("DEDFMISYS-656");

        return request;
    }

    private static DataDocumentMeshFolder getDataDocumentMeshFolderPBIL() {
        DataDocumentMeshFolder folder = new DataDocumentMeshFolder();
        folder.setFolderName("CR-PEBILDIA-T05");

        List<DataDocumentMeshJobName> jobnames = new ArrayList<>();
        DataDocumentMeshJobName jobName1 = new DataDocumentMeshJobName();
        jobName1.setJobName("PBILCP0082");
        jobName1.setStatus("nuevo");

        DataDocumentMeshJobName jobName2 = new DataDocumentMeshJobName();
        jobName2.setJobName("PBILDP0010");
        jobName2.setStatus("modificado");

        jobnames.add(jobName1);
        jobnames.add(jobName2);

        folder.setJobNames(jobnames);
        folder.setXmlAfter("""
                <?xml version="1.0" encoding="utf-8"?>
                <!--Exported at 03-12-2024 11:40:35-->
                <DEFTABLE xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="Folder.xsd">
                    <FOLDER DATACENTER="CTM_CTRLMCCR" VERSION="919" PLATFORM="UNIX" FOLDER_NAME="CR-PEBILDIA-T05" MODIFIED="False" LAST_UPLOAD="20180618171138UTC" FOLDER_ORDER_METHOD="SYSTEM" REAL_FOLDER_ID="15842" TYPE="1" USED_BY_CODE="0">
                        <JOB JOBISN="0" APPLICATION="BIL-PE-DATIO" SUB_APPLICATION="BIL-MASTER-CCR"
                             CMDLINE="/opt/datio/sentry-pe/dataproc_sentry.py %%SENTRY_JOB %%SENTRY_OPT '%%SENTRY_PARM'"
                             JOBNAME="PBILCP0082" DESCRIPTION="MASTER -  t_pbil_branch_seller_manager"
                             CREATED_BY="XP54462" RUN_AS="sentry" CRITICAL="0" TASKTYPE="Command" CYCLIC="0"
                             NODEID="PE-SENTRY-00" INTERVAL="00001M" CONFIRM="0" RETRO="0" MAXWAIT="3"
                             MAXRERUN="0" AUTOARCH="1" MAXDAYS="0" MAXRUNS="0" JAN="1" FEB="1" MAR="1"
                             APR="1" MAY="1" JUN="1" JUL="1" AUG="1" SEP="1" OCT="1" NOV="1" DEC="1"
                             DAYS_AND_OR="O" SHIFT="Ignore Job" SHIFTNUM="+00" SYSDB="1" IND_CYCLIC="S"
                             CREATION_USER="XP54462" CREATION_DATE="20241031" CREATION_TIME="084436"
                             RULE_BASED_CALENDAR_RELATIONSHIP="O" APPL_TYPE="OS" CM_VER="N/A" MULTY_AGENT="N"
                             USE_INSTREAM_JCL="N" VERSION_OPCODE="N" IS_CURRENT_VERSION="Y"
                             VERSION_SERIAL="1" VERSION_HOST="WVMCCRXA06" CYCLIC_TOLERANCE="0"
                             CYCLIC_TYPE="C" PARENT_FOLDER="CR-PEBILDIA-T05">
                
                            <VARIABLE NAME="%%SENTRY_JOB" VALUE="-ns pe.pbil.app-id-20741.pro -jn pbil-pe-krb-inm-monthlyaggbillingd-01 -o %%ORDERID"/>
                            <VARIABLE NAME="%%SENTRY_OPT" VALUE="-b"/>
                            <VARIABLE NAME="%%SENTRY_PARM" VALUE="{&quot;env&quot;:{&quot;CONTROLM_JOB_ID&quot;:&quot;%%JOBNAME&quot;,&quot;CONTROLM_JOB_FLOW&quot;:&quot;%%SCHEDTAB&quot;}}"/>
                           \s
                            <SHOUT WHEN="EXECTIME" TIME="&gt;060" URGENCY="R" DEST="EM" MESSAGE="Excedio el tiempo de ejecucion favor de alertar al aplicativo" DAYSOFFSET="0"/>
                           \s
                            <QUANTITATIVE NAME="DATIO_SENTRY_PE" QUANT="1" ONFAIL="R" ONOK="R"/>
                           \s
                            <INCOND NAME="PBILCP0065-TO-PBILCP0082" ODATE="ODAT" AND_OR="A"/>
                            <OUTCOND NAME="PBILCP0065-TO-PBILCP0082" ODATE="ODAT" SIGN="-"/>
                            <OUTCOND NAME="PBILCP0082-TO-PBILDP0010" ODATE="ODAT" SIGN="+"/>
                           \s
                            <ON STMT="*" CODE="OK">
                                <DOFORCEJOB TABLE_NAME="CR-PEBILDIA-T05" NAME="PBILDP0010" ODATE="ODAT" REMOTE="N"/>
                            </ON>
                           \s
                            <ON STMT="*" CODE="NOTOK">
                                <DOMAIL URGENCY="R" DEST="incidencia-malla-datio-peru.group@bbva.com;intramis-comercial-per.group@bbva.com"
                                        SUBJECT="Cancelado PBILCP0082 - pbil-pe-krb-inm-monthlyaggbillingd-01 - %%$ODATE"
                                        MESSAGE="0038Job execution error, process canceled." ATTACH_SYSOUT="Y"/>
                            </ON>
                        </JOB>
                    </FOLDER>
                </DEFTABLE>
                """);
        return folder;
    }

    private static DataDocumentMeshFolder getDataDocumentMeshFolderPMOL() {
        DataDocumentMeshFolder folder = new DataDocumentMeshFolder();
        folder.setFolderName("CR-PEMOLMEN-T02");

        List<DataDocumentMeshJobName> jobnames = new ArrayList<>();
        DataDocumentMeshJobName jobName1 = new DataDocumentMeshJobName();
        jobName1.setJobName("PMOLTP0082");
        jobName1.setStatus("nuevo");

        DataDocumentMeshJobName jobName2 = new DataDocumentMeshJobName();
        jobName2.setJobName("PMOLVP0010");
        jobName2.setStatus("modificado");

        jobnames.add(jobName1);
        jobnames.add(jobName2);

        folder.setJobNames(jobnames);
        folder.setXmlAfter("""
                <?xml version="1.0" encoding="utf-8"?>
                <!--Exported at 03-12-2024 11:40:35-->
                <DEFTABLE xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="Folder.xsd">
                    <FOLDER DATACENTER="CTM_CTRLMCCR" VERSION="919" PLATFORM="UNIX" FOLDER_NAME="CR-PEMOLMEN-T02" MODIFIED="False"
                            LAST_UPLOAD="20180618171138UTC" FOLDER_ORDER_METHOD="SYSTEM" REAL_FOLDER_ID="15842" TYPE="1" USED_BY_CODE="0">
                
                        <JOB JOBISN="0" APPLICATION="MOL-PE-DATIO" SUB_APPLICATION="MOL-MASTER-CCR"
                             CMDLINE="/opt/datio/sentry-pe/dataproc_sentry.py %%SENTRY_JOB %%SENTRY_OPT '%%SENTRY_PARM'"
                             JOBNAME="PMOLTP0082" DESCRIPTION="MASTER -  t_pbil_branch_seller_manager" CREATED_BY="XP54462"
                             RUN_AS="sentry" CRITICAL="0" TASKTYPE="Command" CYCLIC="0" NODEID="PE-SENTRY-00"
                             INTERVAL="00001M" CONFIRM="0" RETRO="0" MAXWAIT="3" MAXRERUN="0" AUTOARCH="1"
                             MAXDAYS="0" MAXRUNS="0" JAN="1" FEB="1" MAR="1" APR="1" MAY="1" JUN="1" JUL="1"
                             AUG="1" SEP="1" OCT="1" NOV="1" DEC="1" DAYS_AND_OR="O" SHIFT="Ignore Job" SHIFTNUM="+00"
                             SYSDB="1" IND_CYCLIC="S" CREATION_USER="XP54462" CREATION_DATE="20241031" CREATION_TIME="084436"
                             RULE_BASED_CALENDAR_RELATIONSHIP="O" APPL_TYPE="OS" CM_VER="N/A" MULTY_AGENT="N"
                             USE_INSTREAM_JCL="N" VERSION_OPCODE="N" IS_CURRENT_VERSION="Y" VERSION_SERIAL="1"
                             VERSION_HOST="WVMCCRXA06" CYCLIC_TOLERANCE="0" CYCLIC_TYPE="C" PARENT_FOLDER="CR-PEMOLMEN-T05">
                
                            <VARIABLE NAME="%%SENTRY_JOB" VALUE="-ns pe.pbil.app-id-20741.pro -jn pbil-pe-krb-inm-monthlyaggbillingd-01 -o %%ORDERID"/>
                            <VARIABLE NAME="%%SENTRY_OPT" VALUE="-b"/>
                            <VARIABLE NAME="%%SENTRY_PARM" VALUE="{&quot;env&quot;:{&quot;CONTROLM_JOB_ID&quot;:&quot;%%JOBNAME&quot;,&quot;CONTROLM_JOB_FLOW&quot;:&quot;%%SCHEDTAB&quot;}}"/>
                            <SHOUT WHEN="EXECTIME" TIME="&gt;060" URGENCY="R" DEST="EM" MESSAGE="Excedio el tiempo de ejecucion favor de alertar al aplicativo" DAYSOFFSET="0"/>
                            <QUANTITATIVE NAME="DATIO_SENTRY_PE" QUANT="1" ONFAIL="R" ONOK="R"/>
                            <INCOND NAME="PMOLCP0065-TO-PMOLCP0082" ODATE="ODAT" AND_OR="A"/>
                            <OUTCOND NAME="PMOLCP0065-TO-PMOLCP0082" ODATE="ODAT" SIGN="-"/>
                            <OUTCOND NAME="PMOLCP0082-TO-PMOLDP0010" ODATE="ODAT" SIGN="+"/>
                
                            <ON STMT="*" CODE="OK">
                                <DOFORCEJOB TABLE_NAME="CR-PEMOLMEN-T05" NAME="PMOLDP0010" ODATE="ODAT" REMOTE="N"/>
                            </ON>
                
                            <ON STMT="*" CODE="NOTOK">
                                <DOMAIL URGENCY="R"
                                        DEST="incidencia-malla-datio-peru.group@bbva.com;intramis-comercial-per.group@bbva.com"
                                        SUBJECT="Cancelado PMOLCP0082 - pbil-pe-krb-inm-monthlyaggbillingd-01 - %%$ODATE"
                                        MESSAGE="0038Job execution error, process canceled." ATTACH_SYSOUT="Y"/>
                            </ON>
                        </JOB>
                
                        <JOB JOBISN="0" APPLICATION="MOL-PE-DATIO" SUB_APPLICATION="MOL-MASTER-CCR"
                             CMDLINE="/opt/datio/sentry-pe/dataproc_sentry.py %%SENTRY_JOB %%SENTRY_OPT '%%SENTRY_PARM'"
                             JOBNAME="PMOLVP0010" DESCRIPTION="MASTER -  t_pbil_branch_seller_manager" CREATED_BY="XP54462"
                             RUN_AS="sentry" CRITICAL="0" TASKTYPE="Command" CYCLIC="0" NODEID="PE-SENTRY-00"
                             INTERVAL="00001M" CONFIRM="0" RETRO="0" MAXWAIT="3" MAXRERUN="0" AUTOARCH="1"
                             MAXDAYS="0" MAXRUNS="0" JAN="1" FEB="1" MAR="1" APR="1" MAY="1" JUN="1" JUL="1"
                             AUG="1" SEP="1" OCT="1" NOV="1" DEC="1" DAYS_AND_OR="O" SHIFT="Ignore Job" SHIFTNUM="+00"
                             SYSDB="1" IND_CYCLIC="S" CREATION_USER="XP54462" CREATION_DATE="20241031" CREATION_TIME="084436"
                             RULE_BASED_CALENDAR_RELATIONSHIP="O" APPL_TYPE="OS" CM_VER="N/A" MULTY_AGENT="N"
                             USE_INSTREAM_JCL="N" VERSION_OPCODE="N" IS_CURRENT_VERSION="Y" VERSION_SERIAL="1"
                             VERSION_HOST="WVMCCRXA06" CYCLIC_TOLERANCE="0" CYCLIC_TYPE="C" PARENT_FOLDER="CR-PEMOLMEN-T05">
                
                            <VARIABLE NAME="%%SENTRY_JOB" VALUE="-ns pe.pbil.app-id-20741.pro -jn pbil-pe-krb-inm-monthlyaggbillingd-01 -o %%ORDERID"/>
                            <VARIABLE NAME="%%SENTRY_OPT" VALUE="-b"/>
                            <VARIABLE NAME="%%SENTRY_PARM" VALUE="{&quot;env&quot;:{&quot;CONTROLM_JOB_ID&quot;:&quot;%%JOBNAME&quot;,&quot;CONTROLM_JOB_FLOW&quot;:&quot;%%SCHEDTAB&quot;}}"/>
                            <SHOUT WHEN="EXECTIME" TIME="&gt;060" URGENCY="R" DEST="EM" MESSAGE="Excedio el tiempo de ejecucion favor de alertar al aplicativo" DAYSOFFSET="0"/>
                            <QUANTITATIVE NAME="DATIO_SENTRY_PE" QUANT="1" ONFAIL="R" ONOK="R"/>
                            <INCOND NAME="PMOLCP0065-TO-PMOLCP0082" ODATE="ODAT" AND_OR="A"/>
                            <OUTCOND NAME="PMOLCP0065-TO-PMOLCP0082" ODATE="ODAT" SIGN="-"/>
                            <OUTCOND NAME="PMOLCP0082-TO-PMOLDP0010" ODATE="ODAT" SIGN="+"/>
                
                            <ON STMT="*" CODE="OK">
                                <DOFORCEJOB TABLE_NAME="CR-PEMOLMEN-T05" NAME="PMOLDP0010" ODATE="ODAT" REMOTE="N"/>
                            </ON>
                
                            <ON STMT="*" CODE="NOTOK">
                                <DOMAIL URGENCY="R"
                                        DEST="incidencia-malla-datio-peru.group@bbva.com;intramis-comercial-per.group@bbva.com"
                                        SUBJECT="Cancelado PMOLCP0082 - pbil-pe-krb-inm-monthlyaggbillingd-01 - %%$ODATE"
                                        MESSAGE="0038Job execution error, process canceled." ATTACH_SYSOUT="Y"/>
                            </ON>
                        </JOB>
                
                    </FOLDER>
                </DEFTABLE>
                """);
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

    private String getJiraUrl(){
        return "https://jira.globaldevtools.bbva.com/rest/api/latest/search?jql=key%20in%20(DEDFMISYS-656)&maxResults=500&json_result=true&expand=changelog&fields=id,key,summary,comment,assignee,reporter,labels,project,updated,due,status,subtasks,description,created,issuetype,issuelinks,attachment,fixVersions,prs,customfield_10260,customfield_10264,customfield_10267,customfield_10270,customfield_18001,customfield_10004,customfield_13300,customfield_13302,customfield_10272,customfield_13301";
    }

    private String getJiraPullRequestUrl(){
        return "https://jira.globaldevtools.bbva.com/rest/dev-status/latest/issue/detail?issueId=11375647&applicationType=stash&dataType=pullrequest";
    }

    private String getBitbucketPullRequestUrl(){
        return "https://bitbucket.globaldevtools.bbva.com/bitbucket/projects/PE_PDIT_APP-ID-31856_DSG/repos/pe-dh-datio-xml-dimensions-controlm/pull-requests/45";
    }

    private JsonNode getBitbucketPullRequestChanges() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree("""
                {
                  "fromHash" : "e9fe5c5fa60748baf6e0b18308bdf7dde5889d54",
                  "toHash" : "0e9556f3a69358eea04da74f0c7fa4d4058a70b3",
                  "values" : [ {
                    "path" : {
                      "components" : [ "Local", "PMFI", "CR-PEMFIMEN-T05.xml" ],
                      "parent" : "Local/PMFI",
                      "name" : "CR-PEMFIMEN-T05.xml",
                      "extension" : "xml",
                      "toString" : "Local/PMFI/CR-PEMFIMEN-T05.xml"
                    },
                    "properties" : {
                      "gitChangeType" : "MODIFY"
                    }
                  }]
                }
                """);
    }

    private String getDocumentBefore (){
        return """
<?xml version="1.0" encoding="utf-8"?>
<!--Exported at 16-04-2025 22:23:24-->
<DEFTABLE xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="Folder.xsd">
   <FOLDER DATACENTER="CTM_CTRLMCCR" VERSION="919" PLATFORM="UNIX" FOLDER_NAME="CR-PEMFIMEN-T05" MODIFIED="False" LAST_UPLOAD="20230119164738UTC" FOLDER_ORDER_METHOD="SYSTEM" REAL_FOLDER_ID="0" TYPE="1" USED_BY_CODE="0">
       <JOB JOBISN="3" APPLICATION="MFI-PE-DATIO" SUB_APPLICATION="MFI-MASTER-CCR" CMDLINE="/opt/datio/sentry-pe/dataproc_sentry.py %%SENTRY_JOB %%SENTRY_OPT &apos;%%SENTRY_PARM&apos;" JOBNAME="PMFICP4095" DESCRIPTION="MASTER - t_pmfi_rslt_bal_monthly_balance" CREATED_BY="P034630" RUN_AS="sentry" CRITICAL="0" TASKTYPE="Command" CYCLIC="0" NODEID="PE-SENTRY-00" INTERVAL="00001M" CONFIRM="0" RETRO="0" DAYS="" MAXWAIT="3" MAXRERUN="0" AUTOARCH="1" MAXDAYS="0" MAXRUNS="0" JAN="1" FEB="1" MAR="1" APR="1" MAY="1" JUN="1" JUL="1" AUG="1" SEP="1" OCT="1" NOV="1" DEC="1" DAYS_AND_OR="O" SHIFT="Ignore Job" SHIFTNUM="+00" SYSDB="1" IND_CYCLIC="S" CREATION_USER="P034630" CREATION_DATE="20250221" CREATION_TIME="082043" RULE_BASED_CALENDAR_RELATIONSHIP="O" APPL_TYPE="OS" CM_VER="N/A" MULTY_AGENT="N" USE_INSTREAM_JCL="N" VERSION_OPCODE="N" IS_CURRENT_VERSION="Y" VERSION_SERIAL="1" VERSION_HOST="WVMCCRXA06" CYCLIC_TOLERANCE="0" CYCLIC_TYPE="C" PARENT_FOLDER="CR-PEMFIMEN-T05">
           <VARIABLE NAME="%%PARM1" VALUE="%%$CALCDATE %%$OYEAR.%%OMONTH.01 -1" />
           <VARIABLE NAME="%%PARM2" VALUE="%%SUBSTR %%PARM1 1 4" />
           <VARIABLE NAME="%%PARM3" VALUE="%%SUBSTR %%PARM1 5 2" />
           <VARIABLE NAME="%%PARM4" VALUE="%%SUBSTR %%PARM1 7 2" />
           <VARIABLE NAME="%%PARM5" VALUE="%%PARM2-%%PARM3-%%PARM4" />
           <VARIABLE NAME="%%SENTRY_JOB" VALUE="-ns pe.pmfi.app-id-20817.pro -jn pmfi-pe-sbx-biz-contractsreportprdwnu0hr2z8-01 -o %%ORDERID" />
           <VARIABLE NAME="%%SENTRY_OPT" VALUE="-b" />
           <VARIABLE NAME="%%SENTRY_PARM" VALUE="{&quot;env&quot;:{&quot;PROCESS_DATE_OVERWRITE&quot;:&quot;%%PARM5&quot;,&quot;CONTROLM_JOB_ID&quot;:&quot;%%JOBNAME&quot;,&quot;CONTROLM_JOB_FLOW&quot;:&quot;%%SCHEDTAB&quot;}}" />
           <SHOUT WHEN="EXECTIME" TIME="&gt;060" URGENCY="R" DEST="EM" MESSAGE="Excedio el tiempo de ejecucion favor de alertar al aplicativo" DAYSOFFSET="0" />
           <QUANTITATIVE NAME="DATIO_SENTRY_PE" QUANT="1" ONFAIL="R" ONOK="R" />
           <INCOND NAME="PMOLCP4118-TO-PMFICP4095" ODATE="ODAT" AND_OR="O" />
           <INCOND NAME="PMOLCP4123-TO-PMFICP4095" ODATE="ODAT" AND_OR="O" />
           <OUTCOND NAME="PMOLCP4118-TO-PMFICP4095" ODATE="ODAT" SIGN="-" />
           <OUTCOND NAME="PMOLCP4123-TO-PMFICP4095" ODATE="ODAT" SIGN="-" />
           <OUTCOND NAME="PMFICP4095-TO-PMFIVP4109" ODATE="ODAT" SIGN="+" />
           <OUTCOND NAME="PMFICP4095-TO-PMFICP4096" ODATE="ODAT" SIGN="+" />
           <ON STMT="*" CODE="OK">
               <DOFORCEJOB TABLE_NAME="CR-PEMFIMEN-T05" NAME="PMFIVP4109" ODATE="ODAT" REMOTE="N" />
               <DOFORCEJOB TABLE_NAME="CR-PEMFIMEN-T05" NAME="PMFICP4096" ODATE="ODAT" REMOTE="N" />
           </ON>
           <ON STMT="*" CODE="NOTOK">
               <DOMAIL URGENCY="R" DEST="soporte_red_datamart.group@bbva.com;financieros-scrum-team.group@bbva.com" SUBJECT="Cancelado PMFICP4095 - pmfi-pe-sbx-biz-contractsreportprdwnu0hr2z8-01 - %%$ODATE" MESSAGE="0038Job execution error, process canceled." ATTACH_SYSOUT="Y" />
           </ON>
       </JOB>
       <JOB JOBISN="4" APPLICATION="MFI-PE-DATIO" SUB_APPLICATION="MFI-HAMMURABI-CCR" CMDLINE="/opt/datio/sentry-pe/dataproc_sentry.py %%SENTRY_JOB %%SENTRY_OPT &apos;%%SENTRY_PARM&apos;" JOBNAME="PMFIVP4109" DESCRIPTION="HAMMURABI - t_pmfi_rslt_bal_monthly_balance" CREATED_BY="P034630" RUN_AS="sentry" CRITICAL="0" TASKTYPE="Command" CYCLIC="0" NODEID="PE-SENTRY-00" INTERVAL="00001M" CONFIRM="0" RETRO="0" DAYS="" MAXWAIT="3" MAXRERUN="0" AUTOARCH="1" MAXDAYS="0" MAXRUNS="0" JAN="1" FEB="1" MAR="1" APR="1" MAY="1" JUN="1" JUL="1" AUG="1" SEP="1" OCT="1" NOV="1" DEC="1" DAYS_AND_OR="O" SHIFT="Ignore Job" SHIFTNUM="+00" SYSDB="1" IND_CYCLIC="S" CREATION_USER="P034630" CREATION_DATE="20250221" CREATION_TIME="082045" RULE_BASED_CALENDAR_RELATIONSHIP="O" APPL_TYPE="OS" CM_VER="N/A" MULTY_AGENT="N" USE_INSTREAM_JCL="N" VERSION_OPCODE="N" IS_CURRENT_VERSION="Y" VERSION_SERIAL="1" VERSION_HOST="WVMCCRXA06" CYCLIC_TOLERANCE="0" CYCLIC_TYPE="C" PARENT_FOLDER="CR-PEMFIMEN-T05">
           <VARIABLE NAME="%%PARM1" VALUE="%%$CALCDATE %%$OYEAR.%%OMONTH.01 -1" />
           <VARIABLE NAME="%%PARM2" VALUE="%%SUBSTR %%PARM1 1 4" />
           <VARIABLE NAME="%%PARM3" VALUE="%%SUBSTR %%PARM1 5 2" />
           <VARIABLE NAME="%%PARM4" VALUE="%%SUBSTR %%PARM1 7 2" />
           <VARIABLE NAME="%%PARM5" VALUE="%%PARM2-%%PARM3-%%PARM4" />
           <VARIABLE NAME="%%SENTRY_JOB" VALUE="-ns pe.pmfi.app-id-20817.pro -jn pmfi-pe-hmm-qlt-rsltbalmonthlybalancem-01 -o %%ORDERID" />
           <VARIABLE NAME="%%SENTRY_OPT" VALUE="-b" />
           <VARIABLE NAME="%%SENTRY_PARM" VALUE="{&quot;env&quot;:{&quot;ODATE&quot;:&quot;%%PARM5&quot;,&quot;CONTROLM_JOB_ID&quot;:&quot;%%JOBNAME&quot;,&quot;CONTROLM_JOB_FLOW&quot;:&quot;%%SCHEDTAB&quot;}}" />
           <SHOUT WHEN="EXECTIME" TIME="&gt;060" URGENCY="R" DEST="EM" MESSAGE="Excedio el tiempo de ejecucion favor de alertar al aplicativo" DAYSOFFSET="0" />
           <QUANTITATIVE NAME="DATIO_SENTRY_PE" QUANT="1" ONFAIL="R" ONOK="R" />
           <INCOND NAME="PMFICP4095-TO-PMFIVP4109" ODATE="ODAT" AND_OR="A" />
           <OUTCOND NAME="PMFICP4095-TO-PMFIVP4109" ODATE="ODAT" SIGN="-" />
           <OUTCOND NAME="PMFIVP4109-CF@OK" ODATE="ODAT" SIGN="+"/>
           <ON STMT="*" CODE="NOTOK">
               <DOMAIL URGENCY="R" DEST="soporte_red_datamart.group@bbva.com;financieros-scrum-team.group@bbva.com" SUBJECT="Cancelado PMFIVP4109 - pmfi-pe-hmm-qlt-rsltbalmonthlybalancem-01 - %%$ODATE" MESSAGE="0038Job execution error, process canceled." ATTACH_SYSOUT="Y" />
           </ON>
       </JOB>
       <JOB JOBISN="5" APPLICATION="MFI-PE-DATIO" SUB_APPLICATION="MFI-MASTER-CCR" CMDLINE="/opt/datio/sentry-pe/dataproc_sentry.py %%SENTRY_JOB %%SENTRY_OPT &apos;%%SENTRY_PARM&apos;" JOBNAME="PMFICP4096" DESCRIPTION="MASTER - t_pmfi_rslt_bal_monthly_balance_l1t" CREATED_BY="P034630" RUN_AS="sentry" CRITICAL="0" TASKTYPE="Command" CYCLIC="0" NODEID="PE-SENTRY-00" INTERVAL="00001M" CONFIRM="0" RETRO="0" DAYS="" MAXWAIT="3" MAXRERUN="0" AUTOARCH="1" MAXDAYS="0" MAXRUNS="0" JAN="1" FEB="1" MAR="1" APR="1" MAY="1" JUN="1" JUL="1" AUG="1" SEP="1" OCT="1" NOV="1" DEC="1" DAYS_AND_OR="O" SHIFT="Ignore Job" SHIFTNUM="+00" SYSDB="1" IND_CYCLIC="S" CREATION_USER="P034630" CREATION_DATE="20250221" CREATION_TIME="082046" RULE_BASED_CALENDAR_RELATIONSHIP="O" APPL_TYPE="OS" CM_VER="N/A" MULTY_AGENT="N" USE_INSTREAM_JCL="N" VERSION_OPCODE="N" IS_CURRENT_VERSION="Y" VERSION_SERIAL="1" VERSION_HOST="WVMCCRXA06" CYCLIC_TOLERANCE="0" CYCLIC_TYPE="C" PARENT_FOLDER="CR-PEMFIMEN-T05">
           <VARIABLE NAME="%%PARM1" VALUE="%%$CALCDATE %%$OYEAR.%%OMONTH.01 -1" />
           <VARIABLE NAME="%%PARM2" VALUE="%%SUBSTR %%PARM1 1 4" />
           <VARIABLE NAME="%%PARM3" VALUE="%%SUBSTR %%PARM1 5 2" />
           <VARIABLE NAME="%%PARM4" VALUE="%%SUBSTR %%PARM1 7 2" />
           <VARIABLE NAME="%%PARM5" VALUE="%%PARM2-%%PARM3-%%PARM4" />
           <VARIABLE NAME="%%SENTRY_JOB" VALUE="-ns pe.pmfi.app-id-20817.pro -jn pmfi-pe-krb-inm-rsltbalmonthlybalancel1tp-01 -o %%ORDERID" />
           <VARIABLE NAME="%%SENTRY_OPT" VALUE="-b" />
           <VARIABLE NAME="%%SENTRY_PARM" VALUE="{&quot;env&quot;:{&quot;PROCESS_DATE&quot;:&quot;%%PARM5&quot;,&quot;CONTROLM_JOB_ID&quot;:&quot;%%JOBNAME&quot;,&quot;CONTROLM_JOB_FLOW&quot;:&quot;%%SCHEDTAB&quot;}}" />
           <SHOUT WHEN="EXECTIME" TIME="&gt;060" URGENCY="R" DEST="EM" MESSAGE="Excedio el tiempo de ejecucion favor de alertar al aplicativo" DAYSOFFSET="0" />
           <QUANTITATIVE NAME="DATIO_SENTRY_PE" QUANT="1" ONFAIL="R" ONOK="R" />
           <INCOND NAME="PMFICP4095-TO-PMFICP4096" ODATE="ODAT" AND_OR="A" />
           <OUTCOND NAME="PMFICP4095-TO-PMFICP4096" ODATE="ODAT" SIGN="-" />
           <OUTCOND NAME="PMFICP4096-TO-PMFIVP4110" ODATE="ODAT" SIGN="+" />
           <ON STMT="*" CODE="OK">
               <DOFORCEJOB TABLE_NAME="CR-PEMFIMEN-T05" NAME="PMFIVP4110" ODATE="ODAT" REMOTE="N" />
           </ON>
           <ON STMT="*" CODE="NOTOK">
               <DOMAIL URGENCY="R" DEST="soporte_red_datamart.group@bbva.com;financieros-scrum-team.group@bbva.com" SUBJECT="Cancelado PMFICP4096 - pmfi-pe-krb-inm-rsltbalmonthlybalancel1tp-01 - %%$ODATE" MESSAGE="0038Job execution error, process canceled." ATTACH_SYSOUT="Y" />
           </ON>
       </JOB>
       <JOB JOBISN="6" APPLICATION="MFI-PE-DATIO" SUB_APPLICATION="MFI-HAMMURABI-CCR" CMDLINE="/opt/datio/sentry-pe/dataproc_sentry.py %%SENTRY_JOB %%SENTRY_OPT &apos;%%SENTRY_PARM&apos;" JOBNAME="PMFIVP4110" DESCRIPTION="HAMMURABI - t_pmfi_rslt_bal_monthly_balance_l1t" CREATED_BY="P034630" RUN_AS="sentry" CRITICAL="0" TASKTYPE="Command" CYCLIC="0" NODEID="PE-SENTRY-00" INTERVAL="00001M" CONFIRM="0" RETRO="0" DAYS="" MAXWAIT="3" MAXRERUN="0" AUTOARCH="1" MAXDAYS="0" MAXRUNS="0" JAN="1" FEB="1" MAR="1" APR="1" MAY="1" JUN="1" JUL="1" AUG="1" SEP="1" OCT="1" NOV="1" DEC="1" DAYS_AND_OR="O" SHIFT="Ignore Job" SHIFTNUM="+00" SYSDB="1" IND_CYCLIC="S" CREATION_USER="P034630" CREATION_DATE="20250221" CREATION_TIME="082048" RULE_BASED_CALENDAR_RELATIONSHIP="O" APPL_TYPE="OS" CM_VER="N/A" MULTY_AGENT="N" USE_INSTREAM_JCL="N" VERSION_OPCODE="N" IS_CURRENT_VERSION="Y" VERSION_SERIAL="1" VERSION_HOST="WVMCCRXA06" CYCLIC_TOLERANCE="0" CYCLIC_TYPE="C" PARENT_FOLDER="CR-PEMFIMEN-T05">
           <VARIABLE NAME="%%PARM1" VALUE="%%$CALCDATE %%$OYEAR.%%OMONTH.01 -1" />
           <VARIABLE NAME="%%PARM2" VALUE="%%SUBSTR %%PARM1 1 4" />
           <VARIABLE NAME="%%PARM3" VALUE="%%SUBSTR %%PARM1 5 2" />
           <VARIABLE NAME="%%PARM4" VALUE="%%SUBSTR %%PARM1 7 2" />
           <VARIABLE NAME="%%PARM5" VALUE="%%PARM2-%%PARM3-%%PARM4" />
           <VARIABLE NAME="%%SENTRY_JOB" VALUE="-ns pe.pmfi.app-id-20817.pro -jn pmfi-pe-hmm-qlt-rsltbalmonthlybalancel1tm-01 -o %%ORDERID" />
           <VARIABLE NAME="%%SENTRY_OPT" VALUE="-b" />
           <VARIABLE NAME="%%SENTRY_PARM" VALUE="{&quot;env&quot;:{&quot;PROCESS_DATE&quot;:&quot;%%PARM5&quot;,&quot;CONTROLM_JOB_ID&quot;:&quot;%%JOBNAME&quot;,&quot;CONTROLM_JOB_FLOW&quot;:&quot;%%SCHEDTAB&quot;}}" />
           <SHOUT WHEN="EXECTIME" TIME="&gt;060" URGENCY="R" DEST="EM" MESSAGE="Excedio el tiempo de ejecucion favor de alertar al aplicativo" DAYSOFFSET="0" />
           <QUANTITATIVE NAME="DATIO_SENTRY_PE" QUANT="1" ONFAIL="R" ONOK="R" />
           <INCOND NAME="PMFICP4096-TO-PMFIVP4110" ODATE="ODAT" AND_OR="A" />
           <OUTCOND NAME="PMFICP4096-TO-PMFIVP4110" ODATE="ODAT" SIGN="-" />
           <OUTCOND NAME="PMFIVP4110-CF@OK" ODATE="ODAT" SIGN="+"/>
           <ON STMT="*" CODE="OK">
               <DOFORCEJOB TABLE_NAME="CR-PEMFIMEN-T05" NAME="PMFITP4001" ODATE="ODAT" REMOTE="N" />
               <DOFORCEJOB TABLE_NAME="CR-PEMFIMEN-T05" NAME="PMFITP4005" ODATE="ODAT" REMOTE="N" />
               <DOFORCEJOB TABLE_NAME="CR-PEMFIMEN-T05" NAME="PMFITP4006" ODATE="ODAT" REMOTE="N" />
               <DOFORCEJOB TABLE_NAME="CR-PEMFIMEN-T05" NAME="PMFITP4007" ODATE="ODAT" REMOTE="N" />
               <DOFORCEJOB TABLE_NAME="CR-PEMFIMEN-T05" NAME="PMFITP4010" ODATE="ODAT" REMOTE="N" />
               <DOFORCEJOB TABLE_NAME="CR-PEMFIMEN-T05" NAME="PMFITP4011" ODATE="ODAT" REMOTE="N" />
               <DOFORCEJOB TABLE_NAME="CR-PEMFIMEN-T05" NAME="PMFITP4023" ODATE="ODAT" REMOTE="N" />
               <DOFORCEJOB TABLE_NAME="CR-PEMFIMEN-T05" NAME="PMFITP4042" ODATE="ODAT" REMOTE="N" />
               <DOFORCEJOB TABLE_NAME="CR-PEMFIMEN-T05" NAME="PMFITP4043" ODATE="ODAT" REMOTE="N" />
               <DOFORCEJOB TABLE_NAME="CR-PEMFIMEN-T05" NAME="PMFITP4044" ODATE="ODAT" REMOTE="N" />
               <DOFORCEJOB TABLE_NAME="CR-PEMFIMEN-T05" NAME="PMFITP4045" ODATE="ODAT" REMOTE="N" />
               <DOFORCEJOB TABLE_NAME="CR-PEMFIMEN-T05" NAME="PMFITP4046" ODATE="ODAT" REMOTE="N" />
               <DOFORCEJOB TABLE_NAME="CR-PEMFIMEN-T05" NAME="PMFITP4047" ODATE="ODAT" REMOTE="N" />
           </ON>
           <ON STMT="*" CODE="NOTOK">
               <DOMAIL URGENCY="R" DEST="soporte_red_datamart.group@bbva.com;financieros-scrum-team.group@bbva.com" SUBJECT="Cancelado PMFIVP4110 - pmfi-pe-hmm-qlt-rsltbalmonthlybalancel1tm-01 - %%$ODATE" MESSAGE="0038Job execution error, process canceled." ATTACH_SYSOUT="Y" />
           </ON>
       </JOB>
       <JOB JOBISN="7" APPLICATION="MFI-PE-DATIO" SUB_APPLICATION="MFI-DATAX-CCR" CMDLINE="datax-agent --transferId %%PARM1 --namespace %%PARM2 --srcParam &quot;PROCESS_DATE:%%CUTOFF_DATE&quot; --srcParam &quot;GROUP:%%PARM8&quot; --srcParam &quot;PARAM_SIZE:%%PARM10&quot; --srcParam &quot;TABLE_PATH:%%PARM9&quot; --dstParam &quot;GROUP:%%PARM8&quot; --dstParam &quot;ODATE:%%PARM3&quot; --cmOrderId %%ORDERID --cmJobId %%JOBNAME --cmJobFlow %%SCHEDTAB" JOBNAME="PMFITP4001" DESCRIPTION="TRANSMISION - t_pmfi_rslt_bal_monthly_balance" CREATED_BY="P034630" RUN_AS="epsilon-ctlm" CRITICAL="0" TASKTYPE="Command" CYCLIC="0" NODEID="datax-ctrlm" INTERVAL="00001M" CONFIRM="0" RETRO="0" DAYS="" MAXWAIT="3" MAXRERUN="0" AUTOARCH="1" MAXDAYS="0" MAXRUNS="0" JAN="1" FEB="1" MAR="1" APR="1" MAY="1" JUN="1" JUL="1" AUG="1" SEP="1" OCT="1" NOV="1" DEC="1" DAYS_AND_OR="O" SHIFT="Ignore Job" SHIFTNUM="+00" SYSDB="1" IND_CYCLIC="S" CREATION_USER="P034630" CREATION_DATE="20250413" CREATION_TIME="154125" RULE_BASED_CALENDAR_RELATIONSHIP="O" APPL_TYPE="OS" MULTY_AGENT="N" USE_INSTREAM_JCL="N" VERSION_OPCODE="N" IS_CURRENT_VERSION="Y" VERSION_SERIAL="1" VERSION_HOST="WVMCCRXA06" CYCLIC_TOLERANCE="0" CYCLIC_TYPE="C" PARENT_FOLDER="CR-PEMFIMEN-T05">
           <VARIABLE NAME="%%PARM1" VALUE="ppmfi_hdfscdrsltbalmonthlybalance_1" />
           <VARIABLE NAME="%%PARM2" VALUE="pe.pmfi.app-id-20817.pro" />
           <VARIABLE NAME="%%PARM3" VALUE="%%$ODATE" />
           <VARIABLE NAME="%%PARM4" VALUE="%%$CALCDATE %%$OYEAR.%%OMONTH.01 -1" />
           <VARIABLE NAME="%%PARM5" VALUE="%%SUBSTR %%PARM4 1 4" />
           <VARIABLE NAME="%%PARM6" VALUE="%%SUBSTR %%PARM4 5 2" />
           <VARIABLE NAME="%%PARM7" VALUE="%%SUBSTR %%PARM4 7 2" />
           <VARIABLE NAME="%%PARM8" VALUE="BCOM_RESTO_1" />
           <VARIABLE NAME="%%PARM9" VALUE="t_pmfi_rslt_bal_monthly_balance_l1t" />
           <VARIABLE NAME="%%PARM10" VALUE="L" />
           <VARIABLE NAME="%%CUTOFF_DATE" VALUE="%%PARM5-%%PARM6-%%PARM7" />
           <INCOND NAME="PMFIVP4110-CF@OK" ODATE="ODAT" AND_OR="A" />
           <ON STMT="*" CODE="NOTOK">
               <DOMAIL URGENCY="R" DEST="soporte_red_datamart.group@bbva.com;local-ingestor-team-1.group@bbva.com" SUBJECT="Cancelado PMFITP4001 - ppmfi_hdfscdrsltbalmonthlybalance_1 - %%$ODATE" MESSAGE="0038Job execution error, process canceled." ATTACH_SYSOUT="Y" />
           </ON>
       </JOB>
   </FOLDER>
</DEFTABLE>
        """;
    }

    private String getDocumentAfter(){
        return """
<?xml version="1.0" encoding="utf-8"?>
<!--Exported at 04-04-2025 09:17:17-->
<DEFTABLE xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:noNamespaceSchemaLocation="Folder.xsd">
    <FOLDER DATACENTER="CTM_CTRLMCCR" VERSION="919" PLATFORM="UNIX" FOLDER_NAME="CR-PEMFIMEN-T05" MODIFIED="False" LAST_UPLOAD="20230119164738UTC" FOLDER_ORDER_METHOD="SYSTEM" REAL_FOLDER_ID="0" TYPE="1" USED_BY_CODE="0">
        <JOB JOBISN="3" APPLICATION="MFI-PE-DATIO" SUB_APPLICATION="MFI-MASTER-CCR" CMDLINE="/opt/datio/sentry-pe/dataproc_sentry.py %%SENTRY_JOB %%SENTRY_OPT &apos;%%SENTRY_PARM&apos;" JOBNAME="PMFICP4095" DESCRIPTION="MASTER - t_pmfi_rslt_bal_monthly_balance" CREATED_BY="P034630" RUN_AS="sentry" CRITICAL="0" TASKTYPE="Command" CYCLIC="0" NODEID="PE-SENTRY-00" INTERVAL="00001M" CONFIRM="0" RETRO="0" DAYS="" MAXWAIT="3" MAXRERUN="0" AUTOARCH="1" MAXDAYS="0" MAXRUNS="0" JAN="1" FEB="1" MAR="1" APR="1" MAY="1" JUN="1" JUL="1" AUG="1" SEP="1" OCT="1" NOV="1" DEC="1" DAYS_AND_OR="O" SHIFT="Ignore Job" SHIFTNUM="+00" SYSDB="1" IND_CYCLIC="S" CREATION_USER="P034630" CREATION_DATE="20250221" CREATION_TIME="082043" RULE_BASED_CALENDAR_RELATIONSHIP="O" APPL_TYPE="OS" CM_VER="N/A" MULTY_AGENT="N" USE_INSTREAM_JCL="N" VERSION_OPCODE="N" IS_CURRENT_VERSION="Y" VERSION_SERIAL="1" VERSION_HOST="WVMCCRXA06" CYCLIC_TOLERANCE="0" CYCLIC_TYPE="C" PARENT_FOLDER="CR-PEMFIMEN-T05">
            <VARIABLE NAME="%%PARM1" VALUE="%%$CALCDATE %%$OYEAR.%%OMONTH.01 -1" />
            <VARIABLE NAME="%%PARM2" VALUE="%%SUBSTR %%PARM1 1 4" />
            <VARIABLE NAME="%%PARM3" VALUE="%%SUBSTR %%PARM1 5 2" />
            <VARIABLE NAME="%%PARM4" VALUE="%%SUBSTR %%PARM1 7 2" />
            <VARIABLE NAME="%%PARM5" VALUE="%%PARM2-%%PARM3-%%PARM4" />
            <VARIABLE NAME="%%SENTRY_JOB" VALUE="-ns pe.pmfi.app-id-20817.pro -jn pmfi-pe-sbx-biz-contractsreportprdwnu0hr2z8-01 -o %%ORDERID" />
            <VARIABLE NAME="%%SENTRY_OPT" VALUE="-b" />
            <VARIABLE NAME="%%SENTRY_PARM" VALUE="{&quot;env&quot;:{&quot;PROCESS_DATE_OVERWRITE&quot;:&quot;%%PARM5&quot;,&quot;CONTROLM_JOB_ID&quot;:&quot;%%JOBNAME&quot;,&quot;CONTROLM_JOB_FLOW&quot;:&quot;%%SCHEDTAB&quot;}}" />
            <SHOUT WHEN="EXECTIME" TIME="&gt;060" URGENCY="R" DEST="EM" MESSAGE="Excedio el tiempo de ejecucion favor de alertar al aplicativo" DAYSOFFSET="0" />
            <QUANTITATIVE NAME="DATIO_SENTRY_PE" QUANT="1" ONFAIL="R" ONOK="R" />
            <INCOND NAME="PMOLCP4118-TO-PMFICP4095" ODATE="ODAT" AND_OR="O" />
            <INCOND NAME="PMOLCP4123-TO-PMFICP4095" ODATE="ODAT" AND_OR="O" />
            <OUTCOND NAME="PMOLCP4118-TO-PMFICP4095" ODATE="ODAT" SIGN="-" />
            <OUTCOND NAME="PMOLCP4123-TO-PMFICP4095" ODATE="ODAT" SIGN="-" />
            <OUTCOND NAME="PMFICP4095-TO-PMFIVP4109" ODATE="ODAT" SIGN="+" />
            <ON STMT="*" CODE="OK">
                <DOFORCEJOB TABLE_NAME="CR-PEMFIMEN-T05" NAME="PMFIVP4109" ODATE="ODAT" REMOTE="N" />
            </ON>
            <ON STMT="*" CODE="NOTOK">
                <DOMAIL URGENCY="R" DEST="soporte_red_datamart.group@bbva.com;financieros-scrum-team.group@bbva.com" SUBJECT="Cancelado PMFICP4095 - pmfi-pe-sbx-biz-contractsreportprdwnu0hr2z8-01 - %%$ODATE" MESSAGE="0038Job execution error, process canceled." ATTACH_SYSOUT="Y" />
            </ON>
        </JOB>
        <JOB JOBISN="4" APPLICATION="MFI-PE-DATIO" SUB_APPLICATION="MFI-HAMMURABI-CCR" CMDLINE="/opt/datio/sentry-pe/dataproc_sentry.py %%SENTRY_JOB %%SENTRY_OPT &apos;%%SENTRY_PARM&apos;" JOBNAME="PMFIVP4109" DESCRIPTION="HAMMURABI - t_pmfi_rslt_bal_monthly_balance" CREATED_BY="P034630" RUN_AS="sentry" CRITICAL="0" TASKTYPE="Command" CYCLIC="0" NODEID="PE-SENTRY-00" INTERVAL="00001M" CONFIRM="0" RETRO="0" DAYS="" MAXWAIT="3" MAXRERUN="0" AUTOARCH="1" MAXDAYS="0" MAXRUNS="0" JAN="1" FEB="1" MAR="1" APR="1" MAY="1" JUN="1" JUL="1" AUG="1" SEP="1" OCT="1" NOV="1" DEC="1" DAYS_AND_OR="O" SHIFT="Ignore Job" SHIFTNUM="+00" SYSDB="1" IND_CYCLIC="S" CREATION_USER="P034630" CREATION_DATE="20250221" CREATION_TIME="082045" RULE_BASED_CALENDAR_RELATIONSHIP="O" APPL_TYPE="OS" CM_VER="N/A" MULTY_AGENT="N" USE_INSTREAM_JCL="N" VERSION_OPCODE="N" IS_CURRENT_VERSION="Y" VERSION_SERIAL="1" VERSION_HOST="WVMCCRXA06" CYCLIC_TOLERANCE="0" CYCLIC_TYPE="C" PARENT_FOLDER="CR-PEMFIMEN-T05">
            <VARIABLE NAME="%%PARM1" VALUE="%%$CALCDATE %%$OYEAR.%%OMONTH.01 -1" />
            <VARIABLE NAME="%%PARM2" VALUE="%%SUBSTR %%PARM1 1 4" />
            <VARIABLE NAME="%%PARM3" VALUE="%%SUBSTR %%PARM1 5 2" />
            <VARIABLE NAME="%%PARM4" VALUE="%%SUBSTR %%PARM1 7 2" />
            <VARIABLE NAME="%%PARM5" VALUE="%%PARM2-%%PARM3-%%PARM4" />
            <VARIABLE NAME="%%SENTRY_JOB" VALUE="-ns pe.pmfi.app-id-20817.pro -jn pmfi-pe-hmm-qlt-rsltbalmonthlybalancem-01 -o %%ORDERID" />
            <VARIABLE NAME="%%SENTRY_OPT" VALUE="-b" />
            <VARIABLE NAME="%%SENTRY_PARM" VALUE="{&quot;env&quot;:{&quot;ODATE&quot;:&quot;%%PARM5&quot;,&quot;CONTROLM_JOB_ID&quot;:&quot;%%JOBNAME&quot;,&quot;CONTROLM_JOB_FLOW&quot;:&quot;%%SCHEDTAB&quot;}}" />
            <SHOUT WHEN="EXECTIME" TIME="&gt;060" URGENCY="R" DEST="EM" MESSAGE="Excedio el tiempo de ejecucion favor de alertar al aplicativo" DAYSOFFSET="0" />
            <QUANTITATIVE NAME="DATIO_SENTRY_PE" QUANT="1" ONFAIL="R" ONOK="R" />
            <INCOND NAME="PMFICP4095-TO-PMFIVP4109" ODATE="ODAT" AND_OR="A" />
            <OUTCOND NAME="PMFICP4095-TO-PMFIVP4109" ODATE="ODAT" SIGN="-" />
            <OUTCOND NAME="PMFIVP4109-TO-PMFICP4096" ODATE="ODAT" SIGN="+" />
            <OUTCOND NAME="PMFIVP4109-CF@OK" ODATE="ODAT" SIGN="+"/>
            <ON STMT="*" CODE="OK">
                <DOFORCEJOB TABLE_NAME="CR-PEMFIMEN-T05" NAME="PMFICP4096" ODATE="ODAT" REMOTE="N" />
            </ON>
            <ON STMT="*" CODE="NOTOK">
                <DOMAIL URGENCY="R" DEST="soporte_red_datamart.group@bbva.com;financieros-scrum-team.group@bbva.com" SUBJECT="Cancelado PMFIVP4109 - pmfi-pe-hmm-qlt-rsltbalmonthlybalancem-01 - %%$ODATE" MESSAGE="0038Job execution error, process canceled." ATTACH_SYSOUT="Y" />
            </ON>
        </JOB>
        <JOB JOBISN="5" APPLICATION="MFI-PE-DATIO" SUB_APPLICATION="MFI-MASTER-CCR" CMDLINE="/opt/datio/sentry-pe/dataproc_sentry.py %%SENTRY_JOB %%SENTRY_OPT &apos;%%SENTRY_PARM&apos;" JOBNAME="PMFICP4096" DESCRIPTION="MASTER - t_pmfi_rslt_bal_monthly_balance_l1t" CREATED_BY="P034630" RUN_AS="sentry" CRITICAL="0" TASKTYPE="Command" CYCLIC="0" NODEID="PE-SENTRY-00" INTERVAL="00001M" CONFIRM="0" RETRO="0" DAYS="" MAXWAIT="3" MAXRERUN="0" AUTOARCH="1" MAXDAYS="0" MAXRUNS="0" JAN="1" FEB="1" MAR="1" APR="1" MAY="1" JUN="1" JUL="1" AUG="1" SEP="1" OCT="1" NOV="1" DEC="1" DAYS_AND_OR="O" SHIFT="Ignore Job" SHIFTNUM="+00" SYSDB="1" IND_CYCLIC="S" CREATION_USER="P034630" CREATION_DATE="20250221" CREATION_TIME="082046" RULE_BASED_CALENDAR_RELATIONSHIP="O" APPL_TYPE="OS" CM_VER="N/A" MULTY_AGENT="N" USE_INSTREAM_JCL="N" VERSION_OPCODE="N" IS_CURRENT_VERSION="Y" VERSION_SERIAL="1" VERSION_HOST="WVMCCRXA06" CYCLIC_TOLERANCE="0" CYCLIC_TYPE="C" PARENT_FOLDER="CR-PEMFIMEN-T05">
            <VARIABLE NAME="%%PARM1" VALUE="%%$CALCDATE %%$OYEAR.%%OMONTH.01 -1" />
            <VARIABLE NAME="%%PARM2" VALUE="%%SUBSTR %%PARM1 1 4" />
            <VARIABLE NAME="%%PARM3" VALUE="%%SUBSTR %%PARM1 5 2" />
            <VARIABLE NAME="%%PARM4" VALUE="%%SUBSTR %%PARM1 7 2" />
            <VARIABLE NAME="%%PARM5" VALUE="%%PARM2-%%PARM3-%%PARM4" />
            <VARIABLE NAME="%%SENTRY_JOB" VALUE="-ns pe.pmfi.app-id-20817.pro -jn pmfi-pe-krb-inm-rsltbalmonthlybalancel1tp-01 -o %%ORDERID" />
            <VARIABLE NAME="%%SENTRY_OPT" VALUE="-b" />
            <VARIABLE NAME="%%SENTRY_PARM" VALUE="{&quot;env&quot;:{&quot;PROCESS_DATE&quot;:&quot;%%PARM5&quot;,&quot;CONTROLM_JOB_ID&quot;:&quot;%%JOBNAME&quot;,&quot;CONTROLM_JOB_FLOW&quot;:&quot;%%SCHEDTAB&quot;}}" />
            <SHOUT WHEN="EXECTIME" TIME="&gt;060" URGENCY="R" DEST="EM" MESSAGE="Excedio el tiempo de ejecucion favor de alertar al aplicativo" DAYSOFFSET="0" />
            <QUANTITATIVE NAME="DATIO_SENTRY_PE" QUANT="1" ONFAIL="R" ONOK="R" />
            <INCOND NAME="PMFIVP4109-TO-PMFICP4096" ODATE="ODAT" AND_OR="A" />
            <OUTCOND NAME="PMFIVP4109-TO-PMFICP4096" ODATE="ODAT" SIGN="-" />
            <OUTCOND NAME="PMFICP4096-TO-PMFIVP4110" ODATE="ODAT" SIGN="+" />
            <ON STMT="*" CODE="OK">
                <DOFORCEJOB TABLE_NAME="CR-PEMFIMEN-T05" NAME="PMFIVP4110" ODATE="ODAT" REMOTE="N" />
            </ON>
            <ON STMT="*" CODE="NOTOK">
                <DOMAIL URGENCY="R" DEST="soporte_red_datamart.group@bbva.com;financieros-scrum-team.group@bbva.com" SUBJECT="Cancelado PMFICP4096 - pmfi-pe-krb-inm-rsltbalmonthlybalancel1tp-01 - %%$ODATE" MESSAGE="0038Job execution error, process canceled." ATTACH_SYSOUT="Y" />
            </ON>
        </JOB>
        <JOB JOBISN="6" APPLICATION="MFI-PE-DATIO" SUB_APPLICATION="MFI-HAMMURABI-CCR" CMDLINE="/opt/datio/sentry-pe/dataproc_sentry.py %%SENTRY_JOB %%SENTRY_OPT &apos;%%SENTRY_PARM&apos;" JOBNAME="PMFIVP4110" DESCRIPTION="HAMMURABI - t_pmfi_rslt_bal_monthly_balance_l1t" CREATED_BY="P034630" RUN_AS="sentry" CRITICAL="0" TASKTYPE="Command" CYCLIC="0" NODEID="PE-SENTRY-00" INTERVAL="00001M" CONFIRM="0" RETRO="0" DAYS="" MAXWAIT="3" MAXRERUN="0" AUTOARCH="1" MAXDAYS="0" MAXRUNS="0" JAN="1" FEB="1" MAR="1" APR="1" MAY="1" JUN="1" JUL="1" AUG="1" SEP="1" OCT="1" NOV="1" DEC="1" DAYS_AND_OR="O" SHIFT="Ignore Job" SHIFTNUM="+00" SYSDB="1" IND_CYCLIC="S" CREATION_USER="P034630" CREATION_DATE="20250221" CREATION_TIME="082048" RULE_BASED_CALENDAR_RELATIONSHIP="O" APPL_TYPE="OS" CM_VER="N/A" MULTY_AGENT="N" USE_INSTREAM_JCL="N" VERSION_OPCODE="N" IS_CURRENT_VERSION="Y" VERSION_SERIAL="1" VERSION_HOST="WVMCCRXA06" CYCLIC_TOLERANCE="0" CYCLIC_TYPE="C" PARENT_FOLDER="CR-PEMFIMEN-T05">
            <VARIABLE NAME="%%PARM1" VALUE="%%$CALCDATE %%$OYEAR.%%OMONTH.01 -1" />
            <VARIABLE NAME="%%PARM2" VALUE="%%SUBSTR %%PARM1 1 4" />
            <VARIABLE NAME="%%PARM3" VALUE="%%SUBSTR %%PARM1 5 2" />
            <VARIABLE NAME="%%PARM4" VALUE="%%SUBSTR %%PARM1 7 2" />
            <VARIABLE NAME="%%PARM5" VALUE="%%PARM2-%%PARM3-%%PARM4" />
            <VARIABLE NAME="%%SENTRY_JOB" VALUE="-ns pe.pmfi.app-id-20817.pro -jn pmfi-pe-hmm-qlt-rsltbalmonthlybalancel1tm-01 -o %%ORDERID" />
            <VARIABLE NAME="%%SENTRY_OPT" VALUE="-b" />
            <VARIABLE NAME="%%SENTRY_PARM" VALUE="{&quot;env&quot;:{&quot;PROCESS_DATE&quot;:&quot;%%PARM5&quot;,&quot;CONTROLM_JOB_ID&quot;:&quot;%%JOBNAME&quot;,&quot;CONTROLM_JOB_FLOW&quot;:&quot;%%SCHEDTAB&quot;}}" />
            <SHOUT WHEN="EXECTIME" TIME="&gt;060" URGENCY="R" DEST="EM" MESSAGE="Excedio el tiempo de ejecucion favor de alertar al aplicativo" DAYSOFFSET="0" />
            <QUANTITATIVE NAME="DATIO_SENTRY_PE" QUANT="1" ONFAIL="R" ONOK="R" />
            <INCOND NAME="PMFICP4096-TO-PMFIVP4110" ODATE="ODAT" AND_OR="A" />
            <OUTCOND NAME="PMFICP4096-TO-PMFIVP4110" ODATE="ODAT" SIGN="-" />
            <OUTCOND NAME="PMFIVP4110-CF@OK" ODATE="ODAT" SIGN="+"/>
            <ON STMT="*" CODE="NOTOK">
                <DOMAIL URGENCY="R" DEST="soporte_red_datamart.group@bbva.com;financieros-scrum-team.group@bbva.com" SUBJECT="Cancelado PMFIVP4110 - pmfi-pe-hmm-qlt-rsltbalmonthlybalancel1tm-01 - %%$ODATE" MESSAGE="0038Job execution error, process canceled." ATTACH_SYSOUT="Y" />
            </ON>
        </JOB>
    </FOLDER>
</DEFTABLE>
                """;
    }


    private String getJiraPullRequestResponse(){
        return """
                {
                  "detail" : [ {
                    "pullRequests" : [ {
                      "id" : "#45",
                      "name" : "[DEDFMISYS-656] Modificación de los folders CR-PEMFIMEN-T05 y CR-PEMOLMEN-T06",
                      "commentCount" : 0,
                      "status" : "MERGED",
                      "url" : "https://bitbucket.globaldevtools.bbva.com/bitbucket/projects/PE_PDIT_APP-ID-31856_DSG/repos/pe-dh-datio-xml-dimensions-controlm/pull-requests/45",
                      "lastUpdate" : "2025-04-22T21:35:19.406+0000"
                    } ]
                  } ]
                }
                """;
    }

    private String getJiraResponse(){
        return """
                {
                  "expand" : "names,schema",
                  "startAt" : 0,
                  "maxResults" : 500,
                  "total" : 1,
                  "issues" : [ {
                	"expand" : "operations,versionedRepresentations,editmeta,changelog,renderedFields",
                	"id" : "11375647",
                	"self" : "https://jira.globaldevtools.bbva.com/rest/api/latest/issue/11375647",
                	"key" : "DEDFMISYS-656"
                  } ]
                }
                """;
    }

}
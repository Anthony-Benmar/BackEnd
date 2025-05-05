package com.bbva.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class BitbucketApiServiceTest {

    @Test
    void testGetPullRequestChanges() throws IOException {
        String pullRequestUrl = getBitbucketPullRequestUrl();
        String userName = "user";
        String token = "token";

        BitbucketApiService bitbucketApiService = spy(new BitbucketApiService());

        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        JsonNode expectedJson = new ObjectMapper().readTree(getBitbucketPullRequestChanges());

        doReturn(mockConnection).when(bitbucketApiService).createAuthenticatedConnection(anyString(), eq(userName), eq(token));
        doReturn(expectedJson).when(bitbucketApiService).getJsonResponse(mockConnection);

        JsonNode result = bitbucketApiService.getPullRequestChanges(pullRequestUrl, userName, token);

        assertNotNull(result);
    }

    @Test
    void testGetPullRequestInfo() throws IOException {
        String pullRequestUrl = getBitbucketPullRequestUrl();
        String userName = "user";
        String token = "token";

        BitbucketApiService bitbucketApiService = spy(new BitbucketApiService());

        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        JsonNode expectedJson = new ObjectMapper().readTree(getBitbucketPullRequestChanges());

        doReturn(mockConnection).when(bitbucketApiService).createAuthenticatedConnection(anyString(), eq(userName), eq(token));
        doReturn(expectedJson).when(bitbucketApiService).getJsonResponse(mockConnection);

        JsonNode result = bitbucketApiService.getPullRequestInfo(pullRequestUrl, userName, token);

        assertNotNull(result);
    }

    @Test
    void testGetPullRequestFileInfo() throws IOException, ParserConfigurationException, SAXException {
        String pullRequestUrl = getBitbucketPullRequestUrl();
        String userName = "user";
        String token = "token";
        String fileRoute = "Local/PMFI/CR-PEMFIMEN-T05.xml";
        String fromHash = "e9fe5c5fa60748baf6e0b18308bdf7dde5889d54";
        BitbucketApiService bitbucketApiService = spy(new BitbucketApiService());
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);
        Document mockDocument = mock(Document.class);
        doReturn(mockConnection).when(bitbucketApiService).createAuthenticatedConnection(anyString(), eq(userName), eq(token));
        doReturn(mockDocument).when(bitbucketApiService).getXmlResponse(mockConnection);

        Document result = bitbucketApiService.getPullRequestFileInfo(pullRequestUrl, userName, token, fileRoute, fromHash);

        assertNotNull(result);
    }

    @Test
    void testCreateAuthenticatedConnection() throws IOException {
        BitbucketApiService service = new BitbucketApiService();
        String user = "user";
        String token = "token";
        String url = "http://localhost";

        HttpURLConnection connection = service.createAuthenticatedConnection(url, user, token);

        assertEquals("GET", connection.getRequestMethod());
    }

    @Test
    void testGetJsonResponse() throws IOException {
        BitbucketApiService service = new BitbucketApiService();
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(getBitbucketPullRequestChanges().getBytes());
        when(mockConnection.getInputStream()).thenReturn(inputStream);

        JsonNode result = service.getJsonResponse(mockConnection);

        assertNotNull(result);
    }

    @Test
    void testGetXmlResponse() throws Exception {
        BitbucketApiService service = new BitbucketApiService();
        HttpURLConnection mockConnection = mock(HttpURLConnection.class);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(getDocumentAfter().getBytes());
        when(mockConnection.getInputStream()).thenReturn(inputStream);

        Document doc = service.getXmlResponse(mockConnection);

        assertNotNull(doc);
    }

    private String getBitbucketPullRequestUrl(){
        return "https://bitbucket.globaldevtools.bbva.com/bitbucket/projects/PE_PDIT_APP-ID-31856_DSG/repos/pe-dh-datio-xml-dimensions-controlm/pull-requests/45";
    }

    private String getBitbucketPullRequestChanges(){
        return """
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


}
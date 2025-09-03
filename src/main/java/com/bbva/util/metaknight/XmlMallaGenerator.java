package com.bbva.util.metaknight;

import com.bbva.core.HandledException;
import com.bbva.dto.metaknight.request.MallaRequestDto;

public class XmlMallaGenerator {

    public String generarFlujoCompletoXml(MallaRequestDto datos) throws HandledException {
        try {
            String jobTransfer = generarJobTransfer(datos);
            String jobCopy = generarJobCopy(datos);
            String jobFilewatcher = generarJobFilewatcher(datos);
            String jobHammurabiStaging = generarJobHammurabiStaging(datos);
            String jobKirbyRaw = generarJobKirbyRaw(datos);
            String jobHammurabiRaw = generarJobHammurabiRaw(datos);
            String jobKirbyMaster = generarJobKirbyMaster(datos);
            String jobHammurabiMaster = generarJobHammurabiMaster(datos);

            String jobErase1 = generarJobErase1(datos);
            String jobErase2 = generarJobErase2(datos);

            StringBuilder xmlOutput = new StringBuilder();
            xmlOutput.append(jobTransfer).append("\n");
            xmlOutput.append(jobCopy).append("\n");
            xmlOutput.append(jobFilewatcher).append("\n");
            xmlOutput.append(jobHammurabiStaging).append("\n");
            xmlOutput.append(jobKirbyRaw).append("\n");
            xmlOutput.append(jobHammurabiRaw).append("\n");
            xmlOutput.append(jobKirbyMaster).append("\n");
            xmlOutput.append(jobHammurabiMaster).append("\n");

            // INSERTAR L1T después del Hammurabi Master
            if (datos.getKrbL1tJobname() != null && datos.getHmmL1tJobname() != null) {
                String jobKirbyL1t = generarJobKirbyL1T(datos);
                String jobHammurabiL1t = generarJobHammurabiL1T(datos);

                xmlOutput.append(jobKirbyL1t).append("\n");
                xmlOutput.append(jobHammurabiL1t).append("\n");
            }

            xmlOutput.append(jobErase1).append("\n");
            xmlOutput.append(jobErase2);

            return xmlOutput.toString().trim();

        } catch (Exception e) {
            throw new HandledException("XML_GENERATION_ERROR",
                    "Error generando plantilla XML: " + e.getMessage(), e);
        }
    }

    private String generarJobTransfer(MallaRequestDto datos) {
        return String.format("""
            <JOB JOBISN="0" APPLICATION="CTD-PE-DATIO" SUB_APPLICATION="CTD-DATAX-CCR" CMDLINE="datax-agent --transferId %%PARM1 --namespace %%PARM2 --dstParam &quot;DATE:%%PARM3&quot; --cmOrderId %%ORDERID --cmJobId %%JOBNAME --cmJobFlow %%SCHEDTAB" JOBNAME="%s" DESCRIPTION="TRANSFER - %s_{ODATE}.csv" CREATED_BY="%s" RUN_AS="epsilon-ctlm" CRITICAL="0" TASKTYPE="Command" CYCLIC="0" NODEID="datax-ctrlm" INTERVAL="00001M" CONFIRM="0" RETRO="0" DAYSCAL="PEHABILE" DAYS="" TIMEFROM="%s" MAXWAIT="3" MAXRERUN="0" AUTOARCH="1" MAXDAYS="0" MAXRUNS="0" JAN="1" FEB="1" MAR="1" APR="1" MAY="1" JUN="1" JUL="1" AUG="1" SEP="1" OCT="1" NOV="1" DEC="1" DAYS_AND_OR="O" SHIFT="Ignore Job" SHIFTNUM="+00" SYSDB="1" IND_CYCLIC="S" CREATION_USER="%s" CREATION_DATE="%s" CREATION_TIME="%s" RULE_BASED_CALENDAR_RELATIONSHIP="O" APPL_TYPE="OS" MULTY_AGENT="N" USE_INSTREAM_JCL="N" VERSION_OPCODE="N" IS_CURRENT_VERSION="Y" VERSION_SERIAL="1" VERSION_HOST="WVMCCRXA06" CYCLIC_TOLERANCE="0" CYCLIC_TYPE="C" PARENT_FOLDER="%s">
                    <VARIABLE NAME="%%PARM1" VALUE="%s" />
                    <VARIABLE NAME="%%PARM2" VALUE="%s.pe.pro" />
                    <VARIABLE NAME="%%PARM3" VALUE="%%$ODATE" />
                    <OUTCOND NAME="%s-TO-%s" ODATE="ODAT" SIGN="+" />
                    <ON STMT="*" CODE="OK">
                        <DOFORCEJOB TABLE_NAME="%s" NAME="%s" ODATE="ODAT" REMOTE="N" />
                    </ON>
                    <ON STMT="*" CODE="NOTOK">
                        <DOMAIL URGENCY="R" DEST="%s" SUBJECT="Cancelado %s -  - %%$ODATE" MESSAGE="0038Job execution error, process canceled." ATTACH_SYSOUT="Y" />
                    </ON>
            </JOB>""",
                datos.getTransferJobname(), datos.getTransferSourceName(), datos.getCreationUser(),
                datos.getTransferTimeFrom(), datos.getCreationUser(), datos.getCreationDate(),
                datos.getCreationTime(), datos.getParentFolder(), datos.getTransferName(),
                datos.getTransferUuaaRaw(), datos.getTransferJobname(), datos.getCopyJobname(),
                datos.getParentFolder(), datos.getCopyJobname(), datos.getTeamEmail(),
                datos.getTransferJobname()
        );
    }

    private String generarJobCopy(MallaRequestDto datos) {
        return String.format("""
            <JOB JOBISN="0" APPLICATION="CTD-PE-DATIO" SUB_APPLICATION="CTD-HDFS-CCR" CMDLINE="/opt/datio/sentry-pe/dataproc_sentry.py %%SENTRY_JOB %%SENTRY_OPT &apos;%%SENTRY_PARM&apos;" JOBNAME="%s" DESCRIPTION="COPY (HDFS) -   %s_{ODATE}.csv" CREATED_BY="%s" RUN_AS="sentry" CRITICAL="0" TASKTYPE="Command" CYCLIC="0" NODEID="PE-SENTRY-00" INTERVAL="00001M" CONFIRM="0" RETRO="0" DAYSCAL="" DAYS="" MAXWAIT="0" MAXRERUN="0" AUTOARCH="1" MAXDAYS="0" MAXRUNS="0" JAN="1" FEB="1" MAR="1" APR="1" MAY="1" JUN="1" JUL="1" AUG="1" SEP="1" OCT="1" NOV="1" DEC="1" DAYS_AND_OR="O" SHIFT="Ignore Job" SHIFTNUM="+00" SYSDB="1" IND_CYCLIC="S" CREATION_USER="%s" CREATION_DATE="%s" CREATION_TIME="%s" RULE_BASED_CALENDAR_RELATIONSHIP="O" APPL_TYPE="OS" CM_VER="N/A" MULTY_AGENT="N" USE_INSTREAM_JCL="N" VERSION_OPCODE="N" IS_CURRENT_VERSION="Y" VERSION_SERIAL="1" VERSION_HOST="WVMCCRXA06" CYCLIC_TOLERANCE="0" CYCLIC_TYPE="C" PARENT_FOLDER="%s">
                    <VARIABLE NAME="%%PARM1" VALUE="CP" />
                    <VARIABLE NAME="%%PARM2" VALUE="%%$ODATE" />
                    <VARIABLE NAME="%%PARM3" VALUE="/in/staging/datax/%s/%s_%%PARM2..csv" />
                    <VARIABLE NAME="%%PARM4" VALUE="/in/staging/ratransmit/external/%s/%s_%%PARM2..csv" />
                    <VARIABLE NAME="%%PARM5" VALUE="false" />
                    <VARIABLE NAME="%%SENTRY_JOB" VALUE="-ns %s -jn %s-pe-dfs-rmv-hdfs-01 -o %%ORDERID" />
                    <VARIABLE NAME="%%SENTRY_OPT" VALUE="-b" />
                    <VARIABLE NAME="%%SENTRY_PARM" VALUE="{&quot;env&quot;:{&quot;operation&quot;:&quot;%%PARM1&quot;,&quot;srcPath&quot;:&quot;%%PARM3&quot;,&quot;targetPath&quot;:&quot;%%PARM4&quot;,&quot;failIfFileNotFound&quot;:&quot;%%PARM5&quot;,&quot;CONTROLM_JOB_ID&quot;:&quot;%%JOBNAME&quot;,&quot;CONTROLM_JOB_FLOW&quot;:&quot;%%SCHEDTAB&quot;}}" />
                    <SHOUT WHEN="EXECTIME" TIME="&gt;060" URGENCY="R" DEST="EM" MESSAGE="Excedio el tiempo de ejecucion favor de alertar al aplicativo" DAYSOFFSET="0" />
                    <QUANTITATIVE NAME="DATIO_SENTRY_PE" QUANT="1" ONFAIL="R" ONOK="R" />
                    <INCOND NAME="%s-TO-%s" ODATE="ODAT" AND_OR="A" />
                    <OUTCOND NAME="%s-TO-%s" ODATE="ODAT" SIGN="-" />
                    <OUTCOND NAME="%s-TO-%s" ODATE="ODAT" SIGN="+" />
                    <ON STMT="*" CODE="OK">
                        <DOFORCEJOB TABLE_NAME="%s" NAME="%s" ODATE="ODAT" REMOTE="N" />
                    </ON>
                    <ON STMT="*" CODE="NOTOK">
                        <DOMAIL URGENCY="R" DEST="%s" SUBJECT="Cancelado %s - %s-pe-dfs-rmv-hdfs-01 - %%$ODATE" MESSAGE="0038Job execution error, process canceled." ATTACH_SYSOUT="Y" />
                    </ON>
            </JOB>""",
                datos.getCopyJobname(), datos.getTransferSourceName(), datos.getCreationUser(),
                datos.getCreationUser(), datos.getCreationDate(), datos.getCreationTime(),
                datos.getParentFolder(), datos.getTransferUuaaRaw(), datos.getTransferSourceName(),
                datos.getCopyUuaaRaw(), datos.getTransferSourceName(), datos.getNamespace(),
                datos.getUuaaLowercase(), datos.getTransferJobname(), datos.getCopyJobname(),
                datos.getTransferJobname(), datos.getCopyJobname(), datos.getCopyJobname(),
                datos.getFwJobname(), datos.getParentFolder(), datos.getFwJobname(),
                datos.getTeamEmail(), datos.getCopyJobname(), datos.getUuaaLowercase()
        );
    }

    private String generarJobFilewatcher(MallaRequestDto datos) {
        return String.format("""
            <JOB JOBISN="0" APPLICATION="CTD-PE-DATIO" SUB_APPLICATION="CTD-FWATCHER-CCR" CMDLINE="ctmfw /in/staging/external/%s/%s_%%PARM1..csv CREATE %s" JOBNAME="%s" DESCRIPTION="FILEWATCHER-%s_{ODATE}..csv" CREATED_BY="%s" RUN_AS="ag700" CRITICAL="0" TASKTYPE="Command" CYCLIC="0" NODEID="lcvppeaxft00" INTERVAL="00005M" CONFIRM="0" RETRO="0" DAYSCAL="" DAYS="" MAXWAIT="0" MAXRERUN="0" AUTOARCH="1" MAXDAYS="0" MAXRUNS="0" JAN="1" FEB="1" MAR="1" APR="1" MAY="1" JUN="1" JUL="1" AUG="1" SEP="1" OCT="1" NOV="1" DEC="1" DAYS_AND_OR="O" SHIFT="Ignore Job" SHIFTNUM="+00" SYSDB="1" IND_CYCLIC="E" CREATION_USER="%s" CREATION_DATE="%s" CREATION_TIME="%s" RULE_BASED_CALENDAR_RELATIONSHIP="O" APPL_TYPE="OS" MULTY_AGENT="N" USE_INSTREAM_JCL="N" VERSION_OPCODE="N" IS_CURRENT_VERSION="Y" VERSION_SERIAL="1" VERSION_HOST="WVMCCRXA05" CYCLIC_TIMES_SEQUENCE="1135,1137" CYCLIC_TOLERANCE="0" CYCLIC_TYPE="C" PARENT_FOLDER="%s">
                    <VARIABLE NAME="%%PARM1" VALUE="%%$ODATE" />
                    <INCOND NAME="%s-TO-%s" ODATE="ODAT" AND_OR="A" />
                    <OUTCOND NAME="%s-TO-%s" ODATE="ODAT" SIGN="-" />
                    <OUTCOND NAME="%s-TO-%s" ODATE="ODAT" SIGN="+" />
                    <ON STMT="*" CODE="COMPSTAT EQ 7">
                        <DOMAIL URGENCY="R"  DEST="%s" SUBJECT="Sin insumo FileWatcher %%JOBNAME - %%$ODATE" MESSAGE="0116No se encontro el archivo /in/staging/datax/%s/%s_%%PARM1..csv o no supera el peso minimo &gt;0b." ATTACH_SYSOUT="D" />
                        <DOACTION ACTION="OK" />
                    </ON>
                    <ON STMT="*" CODE="COMPSTAT EQ 0">
                        <DOFORCEJOB TABLE_NAME="%s" NAME="%s" ODATE="ODAT" REMOTE="N" />
                    </ON>
                    <ON STMT="*" CODE="NOTOK">
                        <DOMAIL URGENCY="R"  DEST="%s" SUBJECT="Cancelado %%JOBNAME - %%PARM1" ATTACH_SYSOUT="D" />
                    </ON>
            </JOB>""",
                datos.getCopyUuaaRaw(), datos.getTransferSourceName(), datos.getCreateNums(),
                datos.getFwJobname(), datos.getTransferSourceName(), datos.getCreationUser(),
                datos.getCreationUser(), datos.getCreationDate(), datos.getCreationTime(),
                datos.getParentFolder(), datos.getCopyJobname(), datos.getFwJobname(),
                datos.getCopyJobname(), datos.getFwJobname(), datos.getFwJobname(),
                datos.getHmmStgJobname(), datos.getTeamEmail(), datos.getTransferUuaaRaw(),
                datos.getTransferSourceName(), datos.getParentFolder(), datos.getHmmStgJobname(),
                datos.getTeamEmail()
        );
    }

    private String generarJobHammurabiStaging(MallaRequestDto datos) {
        return String.format("""
            <JOB JOBISN="0" APPLICATION="CTD-PE-DATIO" SUB_APPLICATION="CTD-HAMMURABI-CCR" CMDLINE="/opt/datio/sentry-pe/dataproc_sentry.py %%SENTRY_JOB %%SENTRY_OPT &apos;%%SENTRY_PARM&apos;" JOBNAME="%s" DESCRIPTION="HAMMURABI - STAGING - %s_{ODATE}.csv" CREATED_BY="%s" RUN_AS="sentry" CRITICAL="0" TASKTYPE="Command" CYCLIC="0" NODEID="PE-SENTRY-00" INTERVAL="00001M" CONFIRM="0" RETRO="0" DAYSCAL="" DAYS="" MAXWAIT="0" MAXRERUN="0" AUTOARCH="1" MAXDAYS="0" MAXRUNS="0" JAN="1" FEB="1" MAR="1" APR="1" MAY="1" JUN="1" JUL="1" AUG="1" SEP="1" OCT="1" NOV="1" DEC="1" DAYS_AND_OR="O" SHIFT="Ignore Job" SHIFTNUM="+00" SYSDB="1" IND_CYCLIC="S" CREATION_USER="%s" CREATION_DATE="%s" CREATION_TIME="%s" RULE_BASED_CALENDAR_RELATIONSHIP="O" APPL_TYPE="OS" CM_VER="N/A" MULTY_AGENT="N" USE_INSTREAM_JCL="N" VERSION_OPCODE="N" IS_CURRENT_VERSION="Y" VERSION_SERIAL="1" VERSION_HOST="WVMCCRXA06" CYCLIC_TOLERANCE="0" CYCLIC_TYPE="C" PARENT_FOLDER="%s">
                    <VARIABLE NAME="%%PARM1" VALUE="%%$OYEAR-%%OMONTH-%%ODAY" />
                    <VARIABLE NAME="%%PARM2" VALUE="%%$ODATE" />
                    <VARIABLE NAME="%%SENTRY_JOB" VALUE="-ns %s -jn %s -o %%ORDERID" />
                    <VARIABLE NAME="%%SENTRY_OPT" VALUE="-b" />
                    <VARIABLE NAME="%%SENTRY_PARM" VALUE="{&quot;env&quot;:{&quot;CUTOFF_DATE&quot;:&quot;%%PARM1&quot;,&quot;ODATE&quot;:&quot;%%PARM2&quot;,&quot;CONTROLM_JOB_ID&quot;:&quot;%%JOBNAME&quot;,&quot;CONTROLM_JOB_FLOW&quot;:&quot;%%SCHEDTAB&quot;}}" />
                    <SHOUT WHEN="EXECTIME" TIME="&gt;060" URGENCY="R" DEST="EM" MESSAGE="Excedio el tiempo de ejecucion favor de alertar al aplicativo" DAYSOFFSET="0" />
                    <QUANTITATIVE NAME="DATIO_SENTRY_PE" QUANT="1" ONFAIL="R" ONOK="R" />
                    <INCOND NAME="%s-TO-%s" ODATE="ODAT" AND_OR="A" />
                    <OUTCOND NAME="%s-TO-%s" ODATE="ODAT" SIGN="-" />
                    <OUTCOND NAME="%s-TO-%s" ODATE="ODAT" SIGN="+" />
                    <ON STMT="*" CODE="OK">
                        <DOFORCEJOB TABLE_NAME="%s" NAME="%s" ODATE="ODAT" REMOTE="N" />
                    </ON>
                    <ON STMT="*" CODE="NOTOK">
                        <DOMAIL URGENCY="R" DEST="%s" SUBJECT="Cancelado %s - %s - %%$ODATE" MESSAGE="0038Job execution error, process canceled." ATTACH_SYSOUT="Y" />
                    </ON>
            </JOB>""",
                datos.getHmmStgJobname(), datos.getTransferSourceName(), datos.getCreationUser(),
                datos.getCreationUser(), datos.getCreationDate(), datos.getCreationTime(),
                datos.getParentFolder(), datos.getNamespace(), datos.getHmmStgJobid(),
                datos.getFwJobname(), datos.getHmmStgJobname(), datos.getFwJobname(),
                datos.getHmmStgJobname(), datos.getHmmStgJobname(), datos.getKrbRawJobname(),
                datos.getParentFolder(), datos.getKrbRawJobname(), datos.getTeamEmail(),
                datos.getHmmStgJobname(), datos.getHmmStgJobid()
        );
    }

    private String generarJobKirbyRaw(MallaRequestDto datos) {
        return String.format("""
            <JOB JOBISN="0" APPLICATION="CTD-PE-DATIO" SUB_APPLICATION="CTD-RAW-CCR" CMDLINE="/opt/datio/sentry-pe/dataproc_sentry.py %%SENTRY_JOB %%SENTRY_OPT &apos;%%SENTRY_PARM&apos;" JOBNAME="%s" DESCRIPTION="KIRBY - RAW - %s" CREATED_BY="%s" RUN_AS="sentry" CRITICAL="0" TASKTYPE="Command" CYCLIC="0" NODEID="PE-SENTRY-00" INTERVAL="00001M" CONFIRM="0" RETRO="0" DAYSCAL="" DAYS="" MAXWAIT="0" MAXRERUN="0" AUTOARCH="1" MAXDAYS="0" MAXRUNS="0" JAN="1" FEB="1" MAR="1" APR="1" MAY="1" JUN="1" JUL="1" AUG="1" SEP="1" OCT="1" NOV="1" DEC="1" DAYS_AND_OR="O" SHIFT="Ignore Job" SHIFTNUM="+00" SYSDB="1" IND_CYCLIC="S" CREATION_USER="%s" CREATION_DATE="%s" CREATION_TIME="%s" RULE_BASED_CALENDAR_RELATIONSHIP="O" APPL_TYPE="OS" CM_VER="N/A" MULTY_AGENT="N" USE_INSTREAM_JCL="N" VERSION_OPCODE="N" IS_CURRENT_VERSION="Y" VERSION_SERIAL="1" VERSION_HOST="WVMCCRXA06" CYCLIC_TOLERANCE="0" CYCLIC_TYPE="C" PARENT_FOLDER="%s">
                    <VARIABLE NAME="%%PARM1" VALUE="%%$ODATE" />
                    <VARIABLE NAME="%%SENTRY_JOB" VALUE="-ns %s -jn %s -o %%ORDERID" />
                    <VARIABLE NAME="%%SENTRY_OPT" VALUE="-b" />
                    <VARIABLE NAME="%%SENTRY_PARM" VALUE="{&quot;env&quot;:{&quot;DATE&quot;:&quot;%%PARM1&quot;,&quot;ODATE&quot;:&quot;%%PARM1&quot;,&quot;CONTROLM_JOB_ID&quot;:&quot;%%JOBNAME&quot;,&quot;CONTROLM_JOB_FLOW&quot;:&quot;%%SCHEDTAB&quot;}}" />
                    <SHOUT WHEN="EXECTIME" TIME="&gt;060" URGENCY="R" DEST="EM" MESSAGE="Excedio el tiempo de ejecucion favor de alertar al aplicativo" DAYSOFFSET="0" />
                    <QUANTITATIVE NAME="DATIO_SENTRY_PE" QUANT="1" ONFAIL="R" ONOK="R" />
                    <INCOND NAME="%s-TO-%s" ODATE="ODAT" AND_OR="A" />
                    <OUTCOND NAME="%s-TO-%s" ODATE="ODAT" SIGN="-" />
                    <OUTCOND NAME="%s-TO-%s" ODATE="ODAT" SIGN="+" />
                    <ON STMT="*" CODE="OK">
                        <DOFORCEJOB TABLE_NAME="%s" NAME="%s" ODATE="ODAT" REMOTE="N" />
                    </ON>
                    <ON STMT="*" CODE="NOTOK">
                        <DOMAIL URGENCY="R" DEST="%s" SUBJECT="Cancelado %s - %s - %%$ODATE" MESSAGE="0038Job execution error, process canceled." ATTACH_SYSOUT="Y" />
                    </ON>
            </JOB>""",
                datos.getKrbRawJobname(), datos.getRawSourceName(), datos.getCreationUser(),
                datos.getCreationUser(), datos.getCreationDate(), datos.getCreationTime(),
                datos.getParentFolder(), datos.getNamespace(), datos.getKrbRawJobid(),
                datos.getHmmStgJobname(), datos.getKrbRawJobname(), datos.getHmmStgJobname(),
                datos.getKrbRawJobname(), datos.getKrbRawJobname(), datos.getHmmRawJobname(),
                datos.getParentFolder(), datos.getHmmRawJobname(), datos.getTeamEmail(),
                datos.getKrbRawJobname(), datos.getKrbRawJobid()
        );
    }

    private String generarJobHammurabiRaw(MallaRequestDto datos) {
        return String.format("""
            <JOB JOBISN="0" APPLICATION="CTD-PE-DATIO" SUB_APPLICATION="CTD-HAMMURABI-CCR" CMDLINE="/opt/datio/sentry-pe/dataproc_sentry.py %%SENTRY_JOB %%SENTRY_OPT &apos;%%SENTRY_PARM&apos;" JOBNAME="%s" DESCRIPTION="HAMMURABI - RAW - %s" CREATED_BY="%s" RUN_AS="sentry" CRITICAL="0" TASKTYPE="Command" CYCLIC="0" NODEID="PE-SENTRY-00" INTERVAL="00001M" CONFIRM="0" RETRO="0" DAYSCAL="" DAYS="" MAXWAIT="0" MAXRERUN="0" AUTOARCH="1" MAXDAYS="0" MAXRUNS="0" JAN="1" FEB="1" MAR="1" APR="1" MAY="1" JUN="1" JUL="1" AUG="1" SEP="1" OCT="1" NOV="1" DEC="1" DAYS_AND_OR="O" SHIFT="Ignore Job" SHIFTNUM="+00" SYSDB="1" IND_CYCLIC="S" CREATION_USER="%s" CREATION_DATE="%s" CREATION_TIME="%s" RULE_BASED_CALENDAR_RELATIONSHIP="O" APPL_TYPE="OS" CM_VER="N/A" MULTY_AGENT="N" USE_INSTREAM_JCL="N" VERSION_OPCODE="N" IS_CURRENT_VERSION="Y" VERSION_SERIAL="1" VERSION_HOST="WVMCCRXA06" CYCLIC_TOLERANCE="0" CYCLIC_TYPE="C" PARENT_FOLDER="%s">
                    <VARIABLE NAME="%%PARM1" VALUE="%%$OYEAR-%%OMONTH-%%ODAY" />
                    <VARIABLE NAME="%%PARM2" VALUE="%%$ODATE" />
                    <VARIABLE NAME="%%SENTRY_JOB" VALUE="-ns %s -jn %s -o %%ORDERID" />
                    <VARIABLE NAME="%%SENTRY_OPT" VALUE="-b" />
                    <VARIABLE NAME="%%SENTRY_PARM" VALUE="{&quot;env&quot;:{&quot;CUTOFF_DATE&quot;:&quot;%%PARM1&quot;,&quot;DATE&quot;:&quot;%%PARM2&quot;,&quot;ODATE&quot;:&quot;%%PARM2&quot;,&quot;CONTROLM_JOB_ID&quot;:&quot;%%JOBNAME&quot;,&quot;CONTROLM_JOB_FLOW&quot;:&quot;%%SCHEDTAB&quot;}}" />
                    <SHOUT WHEN="EXECTIME" TIME="&gt;060" URGENCY="R" DEST="EM" MESSAGE="Excedio el tiempo de ejecucion favor de alertar al aplicativo" DAYSOFFSET="0" />
                    <QUANTITATIVE NAME="DATIO_SENTRY_PE" QUANT="1" ONFAIL="R" ONOK="R" />
                    <INCOND NAME="%s-TO-%s" ODATE="ODAT" AND_OR="A" />
                    <OUTCOND NAME="%s-TO-%s" ODATE="ODAT" SIGN="-" />
                    <OUTCOND NAME="%s-TO-%s" ODATE="ODAT" SIGN="+" />
                    <ON STMT="*" CODE="OK">
                        <DOFORCEJOB TABLE_NAME="%s" NAME="%s" ODATE="ODAT" REMOTE="N" />
                    </ON>
                    <ON STMT="*" CODE="NOTOK">
                        <DOMAIL URGENCY="R" DEST="%s" SUBJECT="Cancelado %s - %s - %%$ODATE" MESSAGE="0038Job execution error, process canceled." ATTACH_SYSOUT="Y" />
                    </ON>
            </JOB>""",
                datos.getHmmRawJobname(), datos.getRawSourceName(), datos.getCreationUser(),
                datos.getCreationUser(), datos.getCreationDate(), datos.getCreationTime(),
                datos.getParentFolder(), datos.getNamespace(), datos.getHmmRawJobid(),
                datos.getKrbRawJobname(), datos.getHmmRawJobname(), datos.getKrbRawJobname(),
                datos.getHmmRawJobname(), datos.getHmmRawJobname(), datos.getKrbMasterJobname(),
                datos.getParentFolder(), datos.getKrbMasterJobname(), datos.getTeamEmail(),
                datos.getHmmRawJobname(), datos.getHmmRawJobid()
        );
    }

    private String generarJobKirbyMaster(MallaRequestDto datos) {
        return String.format("""
            <JOB JOBISN="0" APPLICATION="CTD-PE-DATIO" SUB_APPLICATION="CTD-MASTER-CCR" CMDLINE="/opt/datio/sentry-pe/dataproc_sentry.py %%SENTRY_JOB %%SENTRY_OPT &apos;%%SENTRY_PARM&apos;" JOBNAME="%s" DESCRIPTION="KIRBY - MASTER - %s" CREATED_BY="%s" RUN_AS="sentry" CRITICAL="0" TASKTYPE="Command" CYCLIC="0" NODEID="PE-SENTRY-00" INTERVAL="00001M" CONFIRM="0" RETRO="0" DAYSCAL="" DAYS="" MAXWAIT="0" MAXRERUN="0" AUTOARCH="1" MAXDAYS="0" MAXRUNS="0" JAN="1" FEB="1" MAR="1" APR="1" MAY="1" JUN="1" JUL="1" AUG="1" SEP="1" OCT="1" NOV="1" DEC="1" DAYS_AND_OR="O" SHIFT="Ignore Job" SHIFTNUM="+00" SYSDB="1" IND_CYCLIC="S" CREATION_USER="%s" CREATION_DATE="%s" CREATION_TIME="%s" RULE_BASED_CALENDAR_RELATIONSHIP="O" APPL_TYPE="OS" CM_VER="N/A" MULTY_AGENT="N" USE_INSTREAM_JCL="N" VERSION_OPCODE="N" IS_CURRENT_VERSION="Y" VERSION_SERIAL="1" VERSION_HOST="WVMCCRXA06" CYCLIC_TOLERANCE="0" CYCLIC_TYPE="C" PARENT_FOLDER="%s">
                    <VARIABLE NAME="%%PARM1" VALUE="%%$ODATE" />
                    <VARIABLE NAME="%%SENTRY_JOB" VALUE="-ns %s -jn %s -o %%ORDERID" />
                    <VARIABLE NAME="%%SENTRY_OPT" VALUE="-b" />
                    <VARIABLE NAME="%%SENTRY_PARM" VALUE="{&quot;env&quot;:{&quot;DATE&quot;:&quot;%%PARM1&quot;,&quot;CONTROLM_JOB_ID&quot;:&quot;%%JOBNAME&quot;,&quot;CONTROLM_JOB_FLOW&quot;:&quot;%%SCHEDTAB&quot;}}" />
                    <SHOUT WHEN="EXECTIME" TIME="&gt;060" URGENCY="R" DEST="EM" MESSAGE="Excedio el tiempo de ejecucion favor de alertar al aplicativo" DAYSOFFSET="0" />
                    <QUANTITATIVE NAME="DATIO_SENTRY_PE" QUANT="1" ONFAIL="R" ONOK="R" />
                    <INCOND NAME="%s-TO-%s" ODATE="ODAT" AND_OR="A" />
                    <OUTCOND NAME="%s-TO-%s" ODATE="ODAT" SIGN="-" />
                    <OUTCOND NAME="%s-TO-%s" ODATE="ODAT" SIGN="+" />
                    <ON STMT="*" CODE="OK">
                        <DOFORCEJOB TABLE_NAME="%s" NAME="%s" ODATE="ODAT" REMOTE="N" />
                    </ON>
                    <ON STMT="*" CODE="NOTOK">
                        <DOMAIL URGENCY="R" DEST="%s" SUBJECT="Cancelado %s - %s - %%$ODATE" MESSAGE="0038Job execution error, process canceled." ATTACH_SYSOUT="Y" />
                    </ON>
            </JOB>""",
                datos.getKrbMasterJobname(), datos.getMasterSourceName(), datos.getCreationUser(),
                datos.getCreationUser(), datos.getCreationDate(), datos.getCreationTime(),
                datos.getParentFolder(), datos.getNamespace(), datos.getKrbMasterJobid(),
                datos.getHmmRawJobname(), datos.getKrbMasterJobname(), datos.getHmmRawJobname(),
                datos.getKrbMasterJobname(), datos.getKrbMasterJobname(), datos.getHmmMasterJobname(),
                datos.getParentFolder(), datos.getHmmMasterJobname(), datos.getTeamEmail(),
                datos.getKrbMasterJobname(), datos.getKrbMasterJobid()
        );
    }

    private String generarJobHammurabiMaster(MallaRequestDto datos) {
        String nextJob1, nextJob2;

        if (datos.getKrbL1tJobname() != null && datos.getHmmL1tJobname() != null) {
            nextJob1 = datos.getKrbL1tJobname();
            nextJob2 = null;
        } else {
            nextJob1 = datos.getErase1Jobname();
            nextJob2 = datos.getErase2Jobname();
        }

        if (nextJob2 != null) {
            return String.format("""
            <JOB JOBISN="0" APPLICATION="CTD-PE-DATIO" SUB_APPLICATION="CTD-HAMMURABI-CCR" CMDLINE="/opt/datio/sentry-pe/dataproc_sentry.py %%SENTRY_JOB %%SENTRY_OPT &apos;%%SENTRY_PARM&apos;" JOBNAME="%s" DESCRIPTION="HAMMURABI - MASTER - %s" CREATED_BY="%s" RUN_AS="sentry" CRITICAL="0" TASKTYPE="Command" CYCLIC="0" NODEID="PE-SENTRY-00" INTERVAL="00001M" CONFIRM="0" RETRO="0" DAYSCAL="" DAYS="" MAXWAIT="0" MAXRERUN="0" AUTOARCH="1" MAXDAYS="0" MAXRUNS="0" JAN="1" FEB="1" MAR="1" APR="1" MAY="1" JUN="1" JUL="1" AUG="1" SEP="1" OCT="1" NOV="1" DEC="1" DAYS_AND_OR="O" SHIFT="Ignore Job" SHIFTNUM="+00" SYSDB="1" IND_CYCLIC="S" CREATION_USER="%s" CREATION_DATE="%s" CREATION_TIME="%s" RULE_BASED_CALENDAR_RELATIONSHIP="O" APPL_TYPE="OS" CM_VER="N/A" MULTY_AGENT="N" USE_INSTREAM_JCL="N" VERSION_OPCODE="N" IS_CURRENT_VERSION="Y" VERSION_SERIAL="1" VERSION_HOST="WVMCCRXA06" CYCLIC_TOLERANCE="0" CYCLIC_TYPE="C" PARENT_FOLDER="%s">
                    <VARIABLE NAME="%%PARM1" VALUE="%%$OYEAR-%%OMONTH-%%ODAY" />
                    <VARIABLE NAME="%%PARM2" VALUE="%%$ODATE" />
                    <VARIABLE NAME="%%SENTRY_JOB" VALUE="-ns %s -jn %s -o %%ORDERID" />
                    <VARIABLE NAME="%%SENTRY_OPT" VALUE="-b" />
                    <VARIABLE NAME="%%SENTRY_PARM" VALUE="{&quot;env&quot;:{&quot;CUTOFF_DATE&quot;:&quot;%%PARM1&quot;,&quot;DATE&quot;:&quot;%%PARM2&quot;,&quot;CONTROLM_JOB_ID&quot;:&quot;%%JOBNAME&quot;,&quot;CONTROLM_JOB_FLOW&quot;:&quot;%%SCHEDTAB&quot;}}" />
                    <SHOUT WHEN="EXECTIME" TIME="&gt;060" URGENCY="R" DEST="EM" MESSAGE="Excedio el tiempo de ejecucion favor de alertar al aplicativo" DAYSOFFSET="0" />
                    <QUANTITATIVE NAME="DATIO_SENTRY_PE" QUANT="1" ONFAIL="R" ONOK="R" />
                    <INCOND NAME="%s-TO-%s" ODATE="ODAT" AND_OR="A" />
                    <OUTCOND NAME="%s-TO-%s" ODATE="ODAT" SIGN="-" />
                    <OUTCOND NAME="%s-TO-%s" ODATE="ODAT" SIGN="+" />
                    <OUTCOND NAME="%s-TO-%s" ODATE="ODAT" SIGN="+" />
                    <OUTCOND NAME="%s-CF@OK" ODATE="ODAT" SIGN="+" />
                    <ON STMT="*" CODE="OK">
                        <DOFORCEJOB TABLE_NAME="%s" NAME="%s" ODATE="ODAT" REMOTE="N" />
                        <DOFORCEJOB TABLE_NAME="%s" NAME="%s" ODATE="ODAT" REMOTE="N" />
                    </ON>
                    <ON STMT="*" CODE="NOTOK">
                        <DOMAIL URGENCY="R" DEST="%s;ada_dhm_pe.group@bbva.com" SUBJECT="Cancelado %s - %s - %%$ODATE" MESSAGE="0038Job execution error, process canceled." ATTACH_SYSOUT="Y" />
                    </ON>
             </JOB>""",
                    datos.getHmmMasterJobname(), datos.getMasterSourceName(), datos.getCreationUser(),
                    datos.getCreationUser(), datos.getCreationDate(), datos.getCreationTime(),
                    datos.getParentFolder(), datos.getNamespace(), datos.getHmmMasterJobid(),
                    datos.getKrbMasterJobname(), datos.getHmmMasterJobname(), datos.getKrbMasterJobname(),
                    datos.getHmmMasterJobname(), datos.getHmmMasterJobname(), nextJob1,  // ERASE1
                    datos.getHmmMasterJobname(), nextJob2,                              // ERASE2
                    datos.getHmmMasterJobname(),
                    datos.getParentFolder(), nextJob1, datos.getParentFolder(), nextJob2,
                    datos.getTeamEmail(), datos.getHmmMasterJobname(), datos.getHmmMasterJobid()
            );
        } else {
            return String.format("""
            <JOB JOBISN="0" APPLICATION="CTD-PE-DATIO" SUB_APPLICATION="CTD-HAMMURABI-CCR" CMDLINE="/opt/datio/sentry-pe/dataproc_sentry.py %%SENTRY_JOB %%SENTRY_OPT &apos;%%SENTRY_PARM&apos;" JOBNAME="%s" DESCRIPTION="HAMMURABI - MASTER - %s" CREATED_BY="%s" RUN_AS="sentry" CRITICAL="0" TASKTYPE="Command" CYCLIC="0" NODEID="PE-SENTRY-00" INTERVAL="00001M" CONFIRM="0" RETRO="0" DAYSCAL="" DAYS="" MAXWAIT="0" MAXRERUN="0" AUTOARCH="1" MAXDAYS="0" MAXRUNS="0" JAN="1" FEB="1" MAR="1" APR="1" MAY="1" JUN="1" JUL="1" AUG="1" SEP="1" OCT="1" NOV="1" DEC="1" DAYS_AND_OR="O" SHIFT="Ignore Job" SHIFTNUM="+00" SYSDB="1" IND_CYCLIC="S" CREATION_USER="%s" CREATION_DATE="%s" CREATION_TIME="%s" RULE_BASED_CALENDAR_RELATIONSHIP="O" APPL_TYPE="OS" CM_VER="N/A" MULTY_AGENT="N" USE_INSTREAM_JCL="N" VERSION_OPCODE="N" IS_CURRENT_VERSION="Y" VERSION_SERIAL="1" VERSION_HOST="WVMCCRXA06" CYCLIC_TOLERANCE="0" CYCLIC_TYPE="C" PARENT_FOLDER="%s">
                    <VARIABLE NAME="%%PARM1" VALUE="%%$OYEAR-%%OMONTH-%%ODAY" />
                    <VARIABLE NAME="%%PARM2" VALUE="%%$ODATE" />
                    <VARIABLE NAME="%%SENTRY_JOB" VALUE="-ns %s -jn %s -o %%ORDERID" />
                    <VARIABLE NAME="%%SENTRY_OPT" VALUE="-b" />
                    <VARIABLE NAME="%%SENTRY_PARM" VALUE="{&quot;env&quot;:{&quot;CUTOFF_DATE&quot;:&quot;%%PARM1&quot;,&quot;DATE&quot;:&quot;%%PARM2&quot;,&quot;CONTROLM_JOB_ID&quot;:&quot;%%JOBNAME&quot;,&quot;CONTROLM_JOB_FLOW&quot;:&quot;%%SCHEDTAB&quot;}}" />
                    <SHOUT WHEN="EXECTIME" TIME="&gt;060" URGENCY="R" DEST="EM" MESSAGE="Excedio el tiempo de ejecucion favor de alertar al aplicativo" DAYSOFFSET="0" />
                    <QUANTITATIVE NAME="DATIO_SENTRY_PE" QUANT="1" ONFAIL="R" ONOK="R" />
                    <INCOND NAME="%s-TO-%s" ODATE="ODAT" AND_OR="A" />
                    <OUTCOND NAME="%s-TO-%s" ODATE="ODAT" SIGN="-" />
                    <OUTCOND NAME="%s-TO-%s" ODATE="ODAT" SIGN="+" />
                    <OUTCOND NAME="%s-CF@OK" ODATE="ODAT" SIGN="+" />
                    <ON STMT="*" CODE="OK">
                        <DOFORCEJOB TABLE_NAME="%s" NAME="%s" ODATE="ODAT" REMOTE="N" />
                    </ON>
                    <ON STMT="*" CODE="NOTOK">
                        <DOMAIL URGENCY="R" DEST="%s;ada_dhm_pe.group@bbva.com" SUBJECT="Cancelado %s - %s - %%$ODATE" MESSAGE="0038Job execution error, process canceled." ATTACH_SYSOUT="Y" />
                    </ON>
             </JOB>""",
                    datos.getHmmMasterJobname(), datos.getMasterSourceName(), datos.getCreationUser(),
                    datos.getCreationUser(), datos.getCreationDate(), datos.getCreationTime(),
                    datos.getParentFolder(), datos.getNamespace(), datos.getHmmMasterJobid(),
                    datos.getKrbMasterJobname(), datos.getHmmMasterJobname(), datos.getKrbMasterJobname(),
                    datos.getHmmMasterJobname(), datos.getHmmMasterJobname(), nextJob1, // Kirby L1T
                    datos.getHmmMasterJobname(),
                    datos.getParentFolder(), nextJob1,
                    datos.getTeamEmail(), datos.getHmmMasterJobname(), datos.getHmmMasterJobid()
            );
        }
    }

    private String generarJobErase1(MallaRequestDto datos) {
        return String.format("""
             <JOB JOBISN="0" APPLICATION="CTD-PE-DATIO" SUB_APPLICATION="CTD-HDFS-CCR" CMDLINE="/opt/datio/sentry-pe/dataproc_sentry.py %%SENTRY_JOB %%SENTRY_OPT &apos;%%SENTRY_PARM&apos;" JOBNAME="%s" DESCRIPTION="REMOVE (HDFS) - %s_{ODATE}.csv" CREATED_BY="%s" RUN_AS="sentry" CRITICAL="0" TASKTYPE="Command" CYCLIC="0" NODEID="PE-SENTRY-00" INTERVAL="00001M" CONFIRM="0" RETRO="0" DAYSCAL="" DAYS="" MAXWAIT="0" MAXRERUN="0" AUTOARCH="1" MAXDAYS="0" MAXRUNS="0" JAN="1" FEB="1" MAR="1" APR="1" MAY="1" JUN="1" JUL="1" AUG="1" SEP="1" OCT="1" NOV="1" DEC="1" DAYS_AND_OR="O" SHIFT="Ignore Job" SHIFTNUM="+00" SYSDB="1" IND_CYCLIC="S" CREATION_USER="%s" CREATION_DATE="%s" CREATION_TIME="%s" RULE_BASED_CALENDAR_RELATIONSHIP="O" APPL_TYPE="OS" CM_VER="N/A" MULTY_AGENT="N" USE_INSTREAM_JCL="N" VERSION_OPCODE="N" IS_CURRENT_VERSION="Y" VERSION_SERIAL="1" VERSION_HOST="WVMCCRXA06" CYCLIC_TOLERANCE="0" CYCLIC_TYPE="C" PARENT_FOLDER="%s">
                    <VARIABLE NAME="%%PARM1" VALUE="REMOVE" />
                    <VARIABLE NAME="%%PARM2" VALUE="%%$ODATE" />
                    <VARIABLE NAME="%%PARM3" VALUE="/in/staging/datax/%s/%s_%%PARM2..csv" />
                    <VARIABLE NAME="%%SENTRY_JOB" VALUE="-ns %s -jn %s-pe-dfs-rmv-hdfs-01 -o %%ORDERID" />
                    <VARIABLE NAME="%%SENTRY_OPT" VALUE="-b" />
                    <VARIABLE NAME="%%SENTRY_PARM" VALUE="{&quot;env&quot;:{&quot;operation&quot;:&quot;%%PARM1&quot;,&quot;sourcePath&quot;:&quot;%%PARM3&quot;,&quot;CONTROLM_JOB_ID&quot;:&quot;%%JOBNAME&quot;,&quot;CONTROLM_JOB_FLOW&quot;:&quot;%%SCHEDTAB&quot;}}" />
                    <SHOUT WHEN="EXECTIME" TIME="&gt;060" URGENCY="R" DEST="EM" MESSAGE="Excedio el tiempo de ejecucion favor de alertar al aplicativo" DAYSOFFSET="0" />
                    <QUANTITATIVE NAME="DATIO_SENTRY_PE" QUANT="1" ONFAIL="R" ONOK="R" />
                    <INCOND NAME="%s-TO-%s" ODATE="ODAT" AND_OR="A" />
                    <OUTCOND NAME="%s-TO-%s" ODATE="ODAT" SIGN="-" />
                    <ON STMT="*" CODE="NOTOK">
                        <DOMAIL URGENCY="R" DEST="%s" SUBJECT="Cancelado %s - %s-pe-dfs-rmv-hdfs-01 - %%$ODATE" MESSAGE="0038Job execution error, process canceled." ATTACH_SYSOUT="Y" />
                    </ON>
            </JOB>""",
                datos.getErase1Jobname(), datos.getTransferSourceName(), datos.getCreationUser(),
                datos.getCreationUser(), datos.getCreationDate(), datos.getCreationTime(),
                datos.getParentFolder(), datos.getTransferUuaaRaw(), datos.getTransferSourceName(),
                datos.getNamespace(), datos.getUuaaLowercase(), datos.getHmmMasterJobname(),
                datos.getErase1Jobname(), datos.getHmmMasterJobname(), datos.getErase1Jobname(),
                datos.getTeamEmail(), datos.getErase1Jobname(), datos.getUuaaLowercase()
        );
    }

    private String generarJobErase2(MallaRequestDto datos) {
        return String.format("""
            <JOB JOBISN="0" APPLICATION="CTD-PE-DATIO" SUB_APPLICATION="CTD-HDFS-CCR" CMDLINE="/opt/datio/sentry-pe/dataproc_sentry.py %%SENTRY_JOB %%SENTRY_OPT &apos;%%SENTRY_PARM&apos;" JOBNAME="%s" DESCRIPTION="REMOVE (HDFS) - %s_{ODATE}.csv" CREATED_BY="%s" RUN_AS="sentry" CRITICAL="0" TASKTYPE="Command" CYCLIC="0" NODEID="PE-SENTRY-00" INTERVAL="00001M" CONFIRM="0" RETRO="0" DAYSCAL="" DAYS="" MAXWAIT="0" MAXRERUN="0" AUTOARCH="1" MAXDAYS="0" MAXRUNS="0" JAN="1" FEB="1" MAR="1" APR="1" MAY="1" JUN="1" JUL="1" AUG="1" SEP="1" OCT="1" NOV="1" DEC="1" DAYS_AND_OR="O" SHIFT="Ignore Job" SHIFTNUM="+00" SYSDB="1" IND_CYCLIC="S" CREATION_USER="%s" CREATION_DATE="%s" CREATION_TIME="%s" RULE_BASED_CALENDAR_RELATIONSHIP="O" APPL_TYPE="OS" CM_VER="N/A" MULTY_AGENT="N" USE_INSTREAM_JCL="N" VERSION_OPCODE="N" IS_CURRENT_VERSION="Y" VERSION_SERIAL="1" VERSION_HOST="WVMCCRXA06" CYCLIC_TOLERANCE="0" CYCLIC_TYPE="C" PARENT_FOLDER="%s">
                    <VARIABLE NAME="%%PARM1" VALUE="REMOVE" />
                    <VARIABLE NAME="%%PARM2" VALUE="%%$ODATE" />
                    <VARIABLE NAME="%%PARM3" VALUE="/in/staging/ratransmit/external/%s/%s_%%PARM2..csv" />
                    <VARIABLE NAME="%%SENTRY_JOB" VALUE="-ns %s -jn %s-pe-dfs-rmv-hdfs-01 -o %%ORDERID" />
                    <VARIABLE NAME="%%SENTRY_OPT" VALUE="-b" />
                    <VARIABLE NAME="%%SENTRY_PARM" VALUE="{&quot;env&quot;:{&quot;operation&quot;:&quot;%%PARM1&quot;,&quot;sourcePath&quot;:&quot;%%PARM3&quot;,&quot;CONTROLM_JOB_ID&quot;:&quot;%%JOBNAME&quot;,&quot;CONTROLM_JOB_FLOW&quot;:&quot;%%SCHEDTAB&quot;}}" />
                    <SHOUT WHEN="EXECTIME" TIME="&gt;060" URGENCY="R" DEST="EM" MESSAGE="Excedio el tiempo de ejecucion favor de alertar al aplicativo" DAYSOFFSET="0" />
                    <QUANTITATIVE NAME="DATIO_SENTRY_PE" QUANT="1" ONFAIL="R" ONOK="R" />
                    <INCOND NAME="%s-TO-%s" ODATE="ODAT" AND_OR="A" />
                    <OUTCOND NAME="%s-TO-%s" ODATE="ODAT" SIGN="-" />
                    <ON STMT="*" CODE="NOTOK">
                        <DOMAIL URGENCY="R" DEST="%s" SUBJECT="Cancelado %s - %s-pe-dfs-rmv-hdfs-01 - %%$ODATE" MESSAGE="0038Job execution error, process canceled." ATTACH_SYSOUT="Y" />
                    </ON>
            </JOB>""",
                datos.getErase2Jobname(), datos.getTransferSourceName(), datos.getCreationUser(),
                datos.getCreationUser(), datos.getCreationDate(), datos.getCreationTime(),
                datos.getParentFolder(), datos.getCopyUuaaRaw(), datos.getTransferSourceName(),
                datos.getNamespace(), datos.getUuaaLowercase(), datos.getHmmMasterJobname(),
                datos.getErase2Jobname(), datos.getHmmMasterJobname(), datos.getErase2Jobname(),
                datos.getTeamEmail(), datos.getErase2Jobname(), datos.getUuaaLowercase()
        );
    }

    private String generarJobKirbyL1T(MallaRequestDto datos) {
        return String.format("""
        <JOB JOBISN="0" APPLICATION="CTD-PE-DATIO" SUB_APPLICATION="CTD-MASTER-CCR" CMDLINE="/opt/datio/sentry-pe/dataproc_sentry.py %%SENTRY_JOB %%SENTRY_OPT &apos;%%SENTRY_PARM&apos;" JOBNAME="%s" DESCRIPTION="KIRBY - L1T - %s" CREATED_BY="%s" RUN_AS="sentry" CRITICAL="0" TASKTYPE="Command" CYCLIC="0" NODEID="PE-SENTRY-00" INTERVAL="00001M" CONFIRM="0" RETRO="0" DAYSCAL="" DAYS="" MAXWAIT="0" MAXRERUN="0" AUTOARCH="1" MAXDAYS="0" MAXRUNS="0" JAN="1" FEB="1" MAR="1" APR="1" MAY="1" JUN="1" JUL="1" AUG="1" SEP="1" OCT="1" NOV="1" DEC="1" DAYS_AND_OR="O" SHIFT="Ignore Job" SHIFTNUM="+00" SYSDB="1" IND_CYCLIC="S" CREATION_USER="%s" CREATION_DATE="%s" CREATION_TIME="%s" RULE_BASED_CALENDAR_RELATIONSHIP="O" APPL_TYPE="OS" CM_VER="N/A" MULTY_AGENT="N" USE_INSTREAM_JCL="N" VERSION_OPCODE="N" IS_CURRENT_VERSION="Y" VERSION_SERIAL="1" VERSION_HOST="WVMCCRXA06" CYCLIC_TOLERANCE="0" CYCLIC_TYPE="C" PARENT_FOLDER="%s">
                <VARIABLE NAME="%%PARM1" VALUE="%%$ODATE" />
                <VARIABLE NAME="%%SENTRY_JOB" VALUE="-ns %s -jn %s -o %%ORDERID" />
                <VARIABLE NAME="%%SENTRY_OPT" VALUE="-b" />
                <VARIABLE NAME="%%SENTRY_PARM" VALUE="{&quot;env&quot;:{&quot;DATE&quot;:&quot;%%PARM1&quot;,&quot;CONTROLM_JOB_ID&quot;:&quot;%%JOBNAME&quot;,&quot;CONTROLM_JOB_FLOW&quot;:&quot;%%SCHEDTAB&quot;}}" />
                <SHOUT WHEN="EXECTIME" TIME="&gt;060" URGENCY="R" DEST="EM" MESSAGE="Excedio el tiempo de ejecucion favor de alertar al aplicativo" DAYSOFFSET="0" />
                <QUANTITATIVE NAME="DATIO_SENTRY_PE" QUANT="1" ONFAIL="R" ONOK="R" />
                <INCOND NAME="%s-TO-%s" ODATE="ODAT" AND_OR="A" />
                <OUTCOND NAME="%s-TO-%s" ODATE="ODAT" SIGN="-" />
                <OUTCOND NAME="%s-TO-%s" ODATE="ODAT" SIGN="+" />
                <ON STMT="*" CODE="OK">
                    <DOFORCEJOB TABLE_NAME="%s" NAME="%s" ODATE="ODAT" REMOTE="N" />
                </ON>
                <ON STMT="*" CODE="NOTOK">
                    <DOMAIL URGENCY="R" DEST="%s" SUBJECT="Cancelado %s - %s - %%$ODATE" MESSAGE="0038Job execution error, process canceled." ATTACH_SYSOUT="Y" />
                </ON>
        </JOB>""",
                datos.getKrbL1tJobname(), datos.getL1tSourceName(), datos.getCreationUser(),
                datos.getCreationUser(), datos.getCreationDate(), datos.getCreationTime(),
                datos.getParentFolder(), datos.getNamespace(), datos.getKrbL1tJobid(),
                datos.getHmmMasterJobname(), datos.getKrbL1tJobname(), datos.getHmmMasterJobname(),
                datos.getKrbL1tJobname(), datos.getKrbL1tJobname(), datos.getHmmL1tJobname(),
                datos.getParentFolder(), datos.getHmmL1tJobname(), datos.getTeamEmail(),
                datos.getKrbL1tJobname(), datos.getKrbL1tJobid()
        );
    }

    private String generarJobHammurabiL1T(MallaRequestDto datos) {
        return String.format("""
        <JOB JOBISN="0" APPLICATION="CTD-PE-DATIO" SUB_APPLICATION="CTD-HAMMURABI-CCR" CMDLINE="/opt/datio/sentry-pe/dataproc_sentry.py %%SENTRY_JOB %%SENTRY_OPT &apos;%%SENTRY_PARM&apos;" JOBNAME="%s" DESCRIPTION="HAMMURABI - L1T - %s" CREATED_BY="%s" RUN_AS="sentry" CRITICAL="0" TASKTYPE="Command" CYCLIC="0" NODEID="PE-SENTRY-00" INTERVAL="00001M" CONFIRM="0" RETRO="0" DAYSCAL="" DAYS="" MAXWAIT="0" MAXRERUN="0" AUTOARCH="1" MAXDAYS="0" MAXRUNS="0" JAN="1" FEB="1" MAR="1" APR="1" MAY="1" JUN="1" JUL="1" AUG="1" SEP="1" OCT="1" NOV="1" DEC="1" DAYS_AND_OR="O" SHIFT="Ignore Job" SHIFTNUM="+00" SYSDB="1" IND_CYCLIC="S" CREATION_USER="%s" CREATION_DATE="%s" CREATION_TIME="%s" RULE_BASED_CALENDAR_RELATIONSHIP="O" APPL_TYPE="OS" CM_VER="N/A" MULTY_AGENT="N" USE_INSTREAM_JCL="N" VERSION_OPCODE="N" IS_CURRENT_VERSION="Y" VERSION_SERIAL="1" VERSION_HOST="WVMCCRXA06" CYCLIC_TOLERANCE="0" CYCLIC_TYPE="C" PARENT_FOLDER="%s">
                <VARIABLE NAME="%%PARM1" VALUE="%%$OYEAR-%%OMONTH-%%ODAY" />
                <VARIABLE NAME="%%PARM2" VALUE="%%$ODATE" />
                <VARIABLE NAME="%%SENTRY_JOB" VALUE="-ns %s -jn %s -o %%ORDERID" />
                <VARIABLE NAME="%%SENTRY_OPT" VALUE="-b" />
                <VARIABLE NAME="%%SENTRY_PARM" VALUE="{&quot;env&quot;:{&quot;DATE&quot;:&quot;%%PARM1&quot;,&quot;ODATE&quot;:&quot;%%PARM2&quot;,&quot;CONTROLM_JOB_ID&quot;:&quot;%%JOBNAME&quot;,&quot;CONTROLM_JOB_FLOW&quot;:&quot;%%SCHEDTAB&quot;}}" />
                <SHOUT WHEN="EXECTIME" TIME="&gt;060" URGENCY="R" DEST="EM" MESSAGE="Excedio el tiempo de ejecucion favor de alertar al aplicativo" DAYSOFFSET="0" />
                <QUANTITATIVE NAME="DATIO_SENTRY_PE" QUANT="1" ONFAIL="R" ONOK="R" />
                <INCOND NAME="%s-TO-%s" ODATE="ODAT" AND_OR="A" />
                <OUTCOND NAME="%s-TO-%s" ODATE="ODAT" SIGN="-" />
                <OUTCOND NAME="%s-TO-%s" ODATE="ODAT" SIGN="+" />
                <OUTCOND NAME="%s-TO-%s" ODATE="ODAT" SIGN="+" />
                <ON STMT="*" CODE="OK">
                    <DOFORCEJOB TABLE_NAME="%s" NAME="%s" ODATE="ODAT" REMOTE="N" />
                    <DOFORCEJOB TABLE_NAME="%s" NAME="%s" ODATE="ODAT" REMOTE="N" />
                </ON>
                <ON STMT="*" CODE="NOTOK">
                    <DOMAIL URGENCY="R" DEST="%s" SUBJECT="Cancelado %s - %s - %%$ODATE" MESSAGE="0038Job execution error, process canceled." ATTACH_SYSOUT="Y" />
                </ON>
        </JOB>""",
                datos.getHmmL1tJobname(), datos.getL1tSourceName(), datos.getCreationUser(),
                datos.getCreationUser(), datos.getCreationDate(), datos.getCreationTime(),
                datos.getParentFolder(), datos.getNamespace(), datos.getHmmL1tJobid(),
                datos.getKrbL1tJobname(), datos.getHmmL1tJobname(),
                datos.getKrbL1tJobname(), datos.getHmmL1tJobname(),
                datos.getHmmL1tJobname(), datos.getErase1Jobname(),
                datos.getHmmL1tJobname(), datos.getErase2Jobname(),
                datos.getParentFolder(), datos.getErase1Jobname(),
                datos.getParentFolder(), datos.getErase2Jobname(),
                datos.getTeamEmail(), datos.getHmmL1tJobname(), datos.getHmmL1tJobid()
        );
    }
}
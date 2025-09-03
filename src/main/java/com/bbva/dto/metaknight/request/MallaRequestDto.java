package com.bbva.dto.metaknight.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MallaRequestDto {

    private String creationUser;
    private String parentFolder;
    private String uuaa;
    private String namespace;
    private String teamEmail;

    private String transferJobname;
    private String transferSourceName;
    private String transferTimeFrom;
    private String transferName;
    private String transferUuaaRaw;

    private String copyJobname;
    private String copyUuaaRaw;

    private String fwJobname;
    private String createNums;

    private String hmmStgJobname;
    private String hmmStgJobid;

    private String krbRawJobname;
    private String krbRawJobid;
    private String rawSourceName;

    private String hmmRawJobname;
    private String hmmRawJobid;

    private String krbMasterJobname;
    private String masterSourceName;
    private String krbMasterJobid;

    private String hmmMasterJobname;
    private String hmmMasterJobid;

    private String erase1Jobname;
    private String erase2Jobname;

    private String creationDate;
    private String creationTime;

    private String uuaaLowercase;

    private String krbL1tJobname;
    private String krbL1tJobid;
    private String hmmL1tJobname;
    private String hmmL1tJobid;
    private String l1tSourceName;
}
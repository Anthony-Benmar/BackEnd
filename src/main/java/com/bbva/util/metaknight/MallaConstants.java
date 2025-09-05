package com.bbva.util.metaknight;

public final class MallaConstants {

    private MallaConstants() {
    }
    public static final String DATIO_SUFFIX = "_datio.xml";
    public static final String ADA_SUFFIX = "_ada.xml";
    public static final String MALLA_FOLDER = "malla/";
    public static final String DEFAULT_CREATE_NUMS = "1 30 10 3 5";
    public static final String DEFAULT_COPY_UUAA_RAW = "pext";
    public static final String HMM_STG_ID_PATTERN = "%s-pe-hmm-qlt-%ss-01";
    public static final String KRB_RAW_ID_PATTERN = "%s-pe-krb-inr-%s-01";
    public static final String HMM_RAW_ID_PATTERN = "%s-pe-hmm-qlt-%sr-01";
    public static final String KRB_MASTER_ID_PATTERN = "%s-pe-krb-inm-%sp-01";
    public static final String HMM_MASTER_ID_PATTERN = "%s-pe-hmm-qlt-%sm-01";
    public static final String KRB_L1T_ID_PATTERN = "%s-pe-krb-inm-%sl1tp-01";
    public static final String HMM_L1T_ID_PATTERN = "%s-pe-hmm-qlt-%sl1tm-01";

    public static final class Validation {
        public static final int MIN_JOBNAME_LENGTH = 8;
        public static final int MAX_JOBNAME_LENGTH = 20;
        public static final String JOBNAME_REGEX = "^[A-Za-z0-9]+$";
        public static final String UUAA_REGEX = "^[A-Za-z]{4}$";

        private Validation() {}
    }
}
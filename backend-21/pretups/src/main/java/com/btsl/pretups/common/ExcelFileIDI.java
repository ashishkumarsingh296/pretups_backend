package com.btsl.pretups.common;

public interface ExcelFileIDI {
    public String BATCH_FOC_INITIATE = "BATCH_FOC_INITIATE";
    public String BATCH_FOC_APPRV1 = "BATCH_FOC_APPRV1";
    public String BATCH_FOC_APPRV2 = "BATCH_FOC_APPRV2";
    public String BATCH_FOC_APPRV3 = "BATCH_FOC_APPRV3";
    public String BATCH_FOC_ENQ = "BATCH_FOC_ENQ";
    public String BATCH_USER_INITIATE = "BATCH_USER_INITIATE";
    public String BATCH_USER_APPROVE = "BATCH_USER_APPROVE";
    public String BATCH_OPT_USER_INITIATE = "BATCH_OPT_USER_INITIATE";
    public String BATCH_CHNL_USER_MODIFY = "BATCH_CHNL_USER_MODIFY";
    public String BATCH_TRF_RULES_CREATION = "BATCH_TRF_RULES_CREATION";
    public String PROMOTIONAL_BATCH_TRF_RULE = "PROMOTIONAL_BATCH_TRF_RULE";

    public String BATCH_C2C_INITIATE = "BATCH_C2C_INITIATE";
    public String BATCH_C2C_APPRV = "BATCH_C2C_APPRV";
    public String BATCH_C2C_ENQ = "BATCH_C2C_ENQ";
    public String BATCH_MOD_C2S_CARDGROUP = "BATCH_MOD_C2S_CARDGROUP";
    public String BATCH_MOD_P2P_CARDGROUP = "BATCH_MOD_P2P_CARDGROUP";
    public String RET_SUBS_MAPPING_ENQ = "RETAILER_SUBSCRIBER_MAPPING_ENQ";
    public String BATCH_ASSOCIATE_PROFILE = "BATCH_ASSOCIATE_PROFILE";
    // added by lohit for direct pay out
    public String BATCH_DP_INITIATE = "BATCH_DP_INITIATE";
    public String BATCH_DP_APPRV1 = "BATCH_DP_APPRV1";
    public String BATCH_DP_APPRV2 = "BATCH_DP_APPRV2";
    public String BATCH_DP_APPRV3 = "BATCH_DP_APPRV3";

    // Added by Vikarm for bulk commission payout
    public String BATCH_DIRECT_PAY_OUT = "BATCH_DIRECT_PAY_OUT";
    public String USER_CLOSING_BAL = "USER_CLO_BAL";
    // Added By Puneet for User Migration
    public String MIG_USER_INIT = "MIG_USER_INIT";

    // Added by Chhaya for Messages
    public String MESSAGES_LIST = "MESSAGES_LIST";
    public String MESSAGES_INITIATE = "MESSAGES_INITIATE";
    public String BATCH_O2C_INITIATE = "BATCH_O2C_INITIATE";

    // Added by Amit Raheja
    public String C2S_TRF_ENQ = "C2S_TRF_ENQ";
    // Addition ends

    // Added for c2c or c2s transaction specific dial
    public String BATCH_C2STRANSSUMMARY_RPT = "BATCH_C2STRANSSUMMARY_RPT";
    public String BATCH_C2CTRANSSUMMARY_RPT = "BATCH_C2CTRANSSUMMARY_RPT";

    public String BATCH_O2C_APPRV1 = "BATCH_O2C_APPRV1";
    public String BATCH_O2C_APPRV2 = "BATCH_O2C_APPRV2";
    public String BATCH_O2C_APPRV3 = "BATCH_O2C_APPRV3";

    // added by gaurav pandey for batch modify commission profile

    public String BATCH_MODIFY_COMM_PROFILE = "BATCH_MODIFY_COMM_PROFILE";
    // added by gaurav
    public String CELL_ID_UPLOAD = "CELL_ID_UPLOAD";
    public String COS_MGMT = "COS_MGMT";
    public String SERVICE_ID_UPLOAD = "SERVICE_ID_UPLOAD";

    // ADDED BY ANUPAM MALVIYA FOR USER DEFAULT CONFIGURATION
    public String USER_DEFAULT_CONFIG_MGT = "USER_DEFAULT_CONFIG_MGT";// 9th
                                                                      // october
                                                                      // for DMS
                                                                      // Configuration

    // added by akanksha for batch grade management
    public String BULK_GRADE_ASSOC_UPLOAD = "BULK_GRADE_ASSOC_UPLOAD";

    // Added by shashank for batch bar for deletion
    public String BATCH_BAR_FOR_DELETION = "BATCH_BAR_FOR_DELETION";
    public String BATCH_BAR_FOR_DELETION_SEC = "BATCH_BAR_FOR_DELETION_SEC";
    public String BATCH_USER_DETAIL = "BATCH_USER_ADDNL_DETAIL";
    // Excel File AssoDeassoUser Heading labels
    public String BATCH_USER_ASSODEASSOUSER = "ASSODEASSO_BATCH_USER";

    // LMS Point Adjustment
    public String BATCH_LPT_INITIATE = "BATCH_LPT_INITIATE";
    public String BATCH_LPT_APPRV1 = "BATCH_LPT_APPRV1";
    public String BATCH_LPT_APPRV2 = "BATCH_LPT_APPRV2";
    public String BATCH_LPT_APPRV3 = "BATCH_LPT_APPRV3";
    
    
    //Merging from 6.4.0 Idea to 6.6.0 bulk c2s reversal
    
    public String BATCH_C2S_TXN_REV="BATCH_C2S_TXN_REV";
    
    
	public String BATCH_C2S_TRFRL_UPLOAD="BATCH_C2S_TRFRL_UPLOAD";
	//download to xls
	public String O2C_TRF_ENQ="O2C_TRF_ENQ";
	public String USER_BAL_ENQ="USER_BAL_ENQ";
	public String USER_THRESHOLD_ENQ="USER_THRESHOLD_ENQ";
	public String C2C_TRF_ENQ="C2C_TRF_ENQ";
	public String VOUCHER_RESEND_PIN_ENQ="VOUCHER_RESEND_PIN_ENQ";
	public String STATUS_CHANGE_BATCH_FILEEXT="CHANNEL_USER_STATUS_CHANGE_BATCH_FILEEXT";
	
	//Operator Batch C2C
	public String OPT_BATCH_C2C_INITIATE="OPT_BATCH_C2C_INITIATE";
	public String OPT_BATCH_C2C_APPRV="OPT_BATCH_C2C_APPRV";
}

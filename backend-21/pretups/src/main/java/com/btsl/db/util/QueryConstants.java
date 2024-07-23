package com.btsl.db.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SqlParameterEncoder;

/**
 * QueryConstants holds the specific db prop file and a default prop file (oracle)
 */
public class QueryConstants {  
	private static final Log LOG = LogFactory.getLog(QueryConstants.class.getName());
	public static final String PRETUPS_DB = "pretups.db";
	public static final String DB_ORACLE = "oracle";
	public static final String DB_POSTGRESQL = "postgresql";
	
	public static final String QUERY_PRODUCER = "QUERY_PRODUCER";
	public static final String DAO_PRODUCER = "DAO_PRODUCER";
	
	public static final String ID_GENERATOR_QRY = "IDGeneratorQry";
	public static final String LOAD_CONTROLLER_QRY = "LoadControllerQry";
	public static final String MENU_QRY = "MenuQry";
	public static final String SERVICES_QRY = "ServicesQry";
	public static final String PREFERENCE_WEB_QRY = "PreferenceWebQry";
	public static final String LOGIN_QRY =  "LoginQry";
	public static final String POS_KEY_QRY =  "PosKeyQry";
	public static final String PAYMENT_QRY = "PaymentQry";
	public static final String CARD_GROUP_QRY = "CardGroupSetQry";
	public static final String PROFILE_QRY = "ProfileQry";
	public static final String TRANSFER_PROFILE_QRY ="TransferProfileQry";
	public static final String ACTIVATION_BONUSLMS_QRY="ActivationBonusLMSQry";
	public static final String NETWORK_QRY ="NetworkQry";
	public static final String FOC_BATCHTRANSFER_WEB_QRY="FOCBatchTransferWebQry";
	public static final String VOMS_BATCHES_QRY="VomsBatchesQry";
	public static final String SUBSCRIBER_TXN_QRY="SubscriberTxnQry";
	
	public static final String REST_SUB_QRY = "RestrictedSubscriberQry";
	public static final String SCH_BATCH_QRY = "ScheduleBatchDetailQry";
	public static final String ROUTING_CONT_QRY = "RoutingControlQry";
	public static final String SOS_TXN_QRY="SOSTxnQry";
	public static final String VOMS_VOUCHER_UMNIAH_QRY="VOMSVoucherUmniahQry";
	public static final String SOS_QRY = "SOSQry";
	public static final String DOMAIN_QRY = "DomainQry";
	public static final String GRP_TYPE_QRY = "GroupTypeQry";
	public static final String IAT_TRANSFER_QRY = "IATTransferQry";
	public static final String LOYALITY_STOCK_QRY = "LoyalityStockQry";
	public static final String CHN_TRNSFR_QRY = "ChannelTransferWebQry";
	public static final String NETWORK_SUMMARY_QRY = "NetworkSummaryQry";
	
	public static final String VOMS_BATCHES_WEB_QRY = "VomsBatchesWebQry";
	public static final String CHANNEL_USER_TRANSFER_QRY = "ChannelUserTransferQry";
	public static final String VOMS_VOUCHER_WEB_QRY="VomsVoucherWebQry";
	public static final String GEO_DOMAIN_WEB_QRY="GeographicalDomainWebQry";
	public static final String SEVICE_SELECTOR_INTERFACE_MAPP_QRY="ServiceSelectorInterfaceMappingQry";
	public static final String NETWORK_STOCK_WEB_QUERY="NetworkStockWebQry";
	public static final String SERVICE_SELECTOR_MAPPING_QRY="ServiceSelectorMappingQry";	
	public static final String ROUTING_QRY ="RoutingQry";
	public static final String IAT_RESTRICTED_SUBSC_QRY = "IATRestrictedSubscriberQry";
	public static final String USER_REPORT_QRY = "UserReportQry";
	public static final String USERTXN_QRY="UserTxnQry";
	public static final String LOYALTY_TXN_QRY="LoyaltyTxnQry";
	public static final String P2P_QUERY_HISTORY_QRY="P2PQueryHistoryQry";
	public static final String ROUTING_TXN_QRY="RoutingTxnQry";
	public static final String NW_SERVICE_QRY = "NetworkServiceQry";
	public static final String CP2_LOGIN_QRY = "CP2PLoginQry";
	public static final String CP2P_REGISTRATION_QRY = "CP2PRegistrationQry";
	public static final String SLAB_GENERATOR_QRY="SlabGeneratorQry";
	public static final String MCD_QRY="MCDQry";
	public static final String PROCESS_STATUS_QRY="ProcessStatusQry";
	public static final String RECEIVER_TRANSFER_QRY="ReceiverTransferQry";
	public static final String MESSAGE_GATEWAY_QRY="MessageGatewayQry";
	public static final String INTERFACE_NTW_MAPP_QRY="InterfaceNetworkMappingQry";
	public static final String VOMS_PRODUCT_QRY="VomsProductQry";
	public static final String SUMMARY_QRY="SummaryQry";
	public static final String ACTIVATION_BONUS_QRY="ActivationBonusQry";
	public static final String  BATCH_USER_WEB_QRY="BatchUserWebQry";
	public static final String  USER_TRANS_ENQ_QRY = "UserTransferEnquiryQry";	
	public static final String TRANSFER_QRY = "TransferQry";	
	public static final String  COMM_PROFILE_WEB_QRY = "CommissionProfileWebQry";
	public static final String SENDSMS_TOCHADM_TRANS_QRY="SendSMSToChannelAdmin4HourlyTransQry";
	public static final String SUBSCRIBER_TRANSFER_QRY="SubscriberTransferQry";
	public static final String CARD_GROUP_DAO_QRY = "CardGroupQry";
	public static final String VOMS_VOUCHER_QRY = "VOMSVoucherQry";
	public static final String C2S_TRANSFER_QRY = "C2STransferQry";
    public static final String POS_KEY_WEB_QRY = "PosKeyWebQry"; 
	public static final String SOS_WEB_QRY = "SOSWebQry";
	public static final String IAT_RESTRICTED_SUBSCRIBER_WEB_QRY = "IATRestrictedSubscriberWebQry";
	public static final String ACTIVATION_BONUS_LMS_WEB_QRY="ActivationBonusLMSWebQry";
	public static final String NETWORK_VAS_MAPPING_QRY = "NetworkVASMappingQry";
	public static final String DAILY_REPORT_ANALYSIS = "DailyReportAnalysisQry";
	public static final String TRANSFER_ENQUIRY_QRY="TransferEnquiryQry";
	public static final String C2S_TRANSFER_WEB_QRY = "C2STransferWebQry";
	public static final String BULK_PUSH_WEB_QRY = "BulkPushWebQry";
	public static final String SERVICES_WEB_QRY = "ServicesWebQry";
	public static final String USER_QRY="UserQry";
	public static final String OAUTH_QRY="OAuthQry";
	public static final String TRANSFER_WEB_QUERY="TransferWebQry";
	public static final String VOMS_VOUCHER_TXN_QRY="VomsVoucherTxnQry";
	public static final String NETWORK_WEB_QUERY="NetworkWebQry";
	public static final String SIM_WEB_QRY="SimWebQry";
	public static final String POINT_ENQUIRY_QRY="PointEnquiryQry";
	public static final String GEO_DOMAIN_QRY="GeographicalDomainQry";
	public static final String C2C_BATCH_TRANSFER_WEB_QRY = "C2CBatchTransferWebQry";
	public static final String C2S_TRANSFER_TXN_QRY = "C2STransferTxnQry";
	public static final String VOMS_ENQUIRY_QRY = "VomsEnquiryQry";
	public static final String WHITE_LIST_QRY = "WhiteListQry";
	public static final String USER_BALANCE_QRY =  "UserBalancesQry";
	public static final String O2C_BATCH_WD_WEB_QRY="O2CBatchWithdrawWebQry";
	public static final String USER_LEVEL_TRF_RULE_QRY="UserLevelTrfRuleQry";
	public static final String USER_C2S_C2C_TRANS_SUMMARY_QRY ="BatchC2SC2CTransSummaryQry";
	
	public static final String NETWORK_STOCK_QRY ="NetworkStockQry";
	public static final String QUEUE_TABLE_ORY="QueueTableQry";
	public static final String BATCH_MODIFY_CARDGROUP_QRY="BatchModifyCardGroupQry";
	public static final String VAS_CAT_WEB_QRY="VASCategoryWebQry";
	public static final String BATCH_OPT_USER_QRY="BatchOPTUserQry";
	public static final String LPT_BATCH_TRANSFER_QRY="LPTBatchTransferQry";
	public static final String CHANNEL_TRANSFER_QRY="ChannelTransferQry";
	public static final String PREFERNCE_TXN_QRY="PreferenceTxnQry";
	public static final String USSD_USER_QRY ="UssdUserQry";
	public static final String SUBSCRIBER_QRY ="SubscriberQry";
	
	public static final String BATCH_USER_QRY ="BatchUserQry";
	public static final String GEO_DOMAIN_TXN_QRY="GeographicalDomainTxnQry";
	public static final String ACTIVATION_BONUS_WEB_QRY ="ActivationBonusWebQry";
	public static final String CHANNEL_USER_REPORT_QRY ="ChannelUserReportQry"; 
	public static final String EXT_USER_QRY="ExtUserQry";
	public static final String USER_MIGRATION_TOOL_QRY="UserMigrationToolQry";
	public static final String USER_MIGRATION_QRY="UserMigrationQry";
	public static final String CHANNELUSER_TRANSFER_WEB_QRY="ChannelUserTransferWebQry";

	
	public static final String FOC_BATCH_TRANSFER_QRY = "FOCBatchTransferQry";
	public static final String O2C_BATCH_WITHRAW_QRY ="O2CBatchWithdrawQry";
	public static final String BATCH_O2C_TRANSFER_WEB_QRY ="BatchO2CTransferWebQry";
	public static final String BATCH_O2C_TRANSFER_QRY = "BatchO2CTransferQry";
	public static final String USER_WEB_QRY="UserWebQry";
	public static final String C2C_BATCH_TRANSFER_QRY="C2CBatchTransferQry";
	public static final String OPERATOR_C2C_BATCH_TRANSFER_QRY="OperatorC2CBatchTransferQry";
	public static final String AUTO_O2C_QRY="AutoO2CQry";
	public static final String CHANNEL_USER_TXN_QRY="ChannelUserTxnQry";
	public static final String CHANNEL_USER_WEB_QRY="ChannelUserWebQry";
	public static final String PIN_PASSWORD_ALERT_QRY="PinPasswordAlertQry";
	public static final String DAILY_C2SROAM_RECHARGE_REPORT_QRY="DailyC2SRoamRechargeReportQry";
	public static final String CHANNEL_USER_QRY = "ChannelUserQry";
	public static final String LMS_OPT_IN_OUT_PROMOTION_PROCESS_QRY = "LMSOptInOutPromotionProcessQry";
	public static final String LMS_OPT_IN_OUT_REF_TARGET_CAL_QRY = "LMSOptInOutReferenceTargetCalculationQry";
	public static final String LMS_POINTS_REDMPTION_PROCESS_QRY = "LmsPointsRedemptionProcessQry";
	public static final String LMS_PROGRESSIVE_MESSAGES_QRY = "LMSProgressiveMessagesQry";
	public static final String LMS_PROMOTIONAL_PROCESS_QRY = "LMSPromotionProcessQry";
	public static final String LIFECYCLE_CHANGEUSERSTATUS_PROCESS_QRY="LifeCycleChangeStatusProcessQry";
	public static final String MONTHLY_C2SROAM_RECHARGE_REPORT_QRY="MonthlyC2SRoamRechargeReportQry";
	public static final String TRANSFER_PROFILE_WEB_QRY= "TransferProfileWebQry";
	public static final String ACTIVATION_BONUS_CAL_QRY = "ActivationBonusCalculationQry";
	public static final String ACTIVATION_BONUS_REDEMPTION_QRY = "ActivationBonusRedemptionQry";
	public static final String AUTO_C2C_TRANSFER_PROCESS_QRY = "AutoC2CTransferProcessQry";
	public static final String CATEGORY_WEB_QRY = "CategoryWebQry";
	public static final String DOMAIN_WEB_QRY = "DomainWebQry";
	public static final String HANDLE_UNSETTLED_CASES_QRY="HandleUnsettledCasesQry";
	public static final String NETWORK_DAILY_CLOSING_STOCK_QRY="NetworkDailyClosingStockQry";
	public static final String COMMON_UTIL_QRY="CommonUtilQry";
	public static final String VOUCHER_BUNDLE_WEB_QRY ="VoucherBundleWebQry";
	public static final String VOMS_CATEGORY_WEB_QRY ="VomsCategoryWebQry";
	public static final String HANDLE_UNSETTLED_P2P_CASES_QRY ="HandleUnsettledP2PCasesQry";
	public static final String AESKEYSTORE_QRY="AESKeystoreQry";
	public static final String VOMSSNIFFER_QRY="VOMSSnifferQry";
	public static final String DAILY_DETAILS_USER_BALANCE_QRY = "DailyDetailsUserBalanceQry";
	public static final String DAILY_SUMMARY_CHANNEL_TXN_PROCESS_QRY = "DailySummaryChannelTxnProcessQry";
	public static final String DIRECT_PAYOUT_QRY = "DirectPayOutQry";
	public static final String HOURLY_COUNT_DETAIL_ALERT_QRY ="HourlyCountDetailAlertQry";
	public static final String KPI_PROCESS_QRY ="KPIProcessQry";
	public static final String LMS_REFERENCE_TARGET_CAL_NEW_QRY = "LMSReferenceTargetCalculationNewQry";
	public static final String LMS_TARGET_VS_ACHIEVEMENT_REPORT_QRY = "LMSTargetVsAchievementReportQry";
	public static final String RUN_LMS_TARGET_CREDIT_NEW_QRY = "RunLMSForTargetCreditNewQry";
	public static final String BATCH_USER_CREATION_EXCEL_RW_POI_QRY = "BatchUserCreationExcelRWPOIQry";
	public static final String SCHEDULE_MULTIPLE_CREDIT_TRANSFER_PROCESS_QRY = "ScheduledMultipleCreditTransferProcessQry";
	public static final String COMMULATIVE_INVENTORY_AMT_REPORT_QRY = "CummulativeInventoryAmtReportQry";
	public static final String CELL_ID_CACHE_QRY = "CellIdCacheQry";
	public static final String BARRED_USER_QRY = "BarredUserQry";
	public static final String RESTRICTED_SUBSCRIBER_WEB_QRY="RestrictedSubscriberWebQry";
	public static final String LOYALTY_POINTS_REDEMPTION_QRY="LoyaltyPointsRedemptionQry";
	public static final String PASSWORD_QRY="PasswordQry";
	
	public static final String VOMS_CHANGE_BATCH_STATUS ="VomsChangeBatchStatusQry";
	public static final String USER_PHONES_QRY ="UserPhonesQry";
	public static final String HANDLE_UNSETTLED_COMBINED_CASE = "HandleUnsettledCombinedCasesQry";
	public static final String LOYALTY_DAO = "LoyaltyDAOQry";
	public static final String BURN_RATE_ALERT_PROCESS_QRY = "BurnRateIndicatorProcessQry";

	public static final String CHANNEL_2_CHANNEL_TRANSFER_RET_WD_REPORT_QRY ="Channel2ChannelTransferRetWidRptQry";
	public static final String O2C_TRANSFER_DETAILS_REPORT_QRY = "O2CTransferDetailsRptQry";
	public static final String USER_BAL_MOVEMENT_REPORT_QRY = "UserDailyBalanceMovementRptQuery";
	public static final String ADDITIONAL_COMMISSION_DETAILS_REPORT_QRY = "AdditionalCommissionDetailsReportQry";	
	public static final String USER_BAL_SUMMARY_REPORT_QRY="UserZeroBalanceCounterSummaryQry";
	public static final String O2C_TRANSFER_NUMBER_ASK_QRY = "O2CTransfernumberAckRptQry";
	public static final String STAFFC2C_TRANSFER_DETAILS_REPORT_QRY = "StaffC2CTransferdetailsRptQry";
	  
	
	public static final  Properties properties = new Properties(); 
	public static final  Properties defaultProperties = new Properties(); 

	public static final String ZERO_BALANCE_COUNTER_DETAILS_REPORT_QRY = "ZeroBalanceCounterDetailsRptQry"; 
	public static final String C2S_Transfer_Details_REPORT_QRY = "C2STransferRptQry";
	public static final String LMS_REDEMPTION_RET_WID_REPORT_QRY="LmsRedemptionRetWidRptQry"; 
	public static final String ADDITIONAL_COMMISSION_SUMMARY_REPORT_QRY = "AdditionalCommissionSummaryReportQry";
	public static final String OPERATION_SUMMARY_REPORT_QRY = "OperationSummaryReportQry";
	public static final String EXTERNAL_USR_REPORT_QRY = "ChannelUserOperatorUserRolesQuery"; 
	public static final String STAFF_C2C_REPORT_QRY="StaffSelfC2CQuery";
	
	public static final String VOMS_REPORT_QRY = "VomsRptQry";
	public static final String CHANNEL_TRANSFER_REPORT_QRY ="ChannelTransferReportQry"; 
	public static final String LOAN_PROFILE_QRY = "LoanProfileQry";
	private QueryConstants() {
		/*Not to be instantiated */
	}
	
    public static void load(String fileName) throws IOException {

		System.out.println("Inside QueryConstants loading...................... 1222222222211111111111112222222 ");
    	FileInputStream fileInputStream = null;
    	try{
    		final File file = new File(fileName);
    		fileInputStream = new FileInputStream(file);
    		properties.load(fileInputStream);
    	}
    	
    	catch(Exception e)
    	{
			e.printStackTrace();
    		LOG.errorTrace("QueryConstants:load()", e);
    	}
    	
    	finally{
    		if(fileInputStream != null)
    		{
    			try{
            		if(fileInputStream != null){
            			fileInputStream.close();	
            		}
            	}catch(Exception e){
            		 LOG.errorTrace("QueryConstants:load()", e);
            	}
    		}
    	}
    }

	/*
    writing another fucntion to accept input stream as parameter
     */
	public static void load(InputStream in) throws IOException {
		try{
			properties.load(in);
			in.close();
		}finally {
			if(in!=null){
				in.close();
			}
		}
	}
    
    public static void loadDefault(String fileName) throws IOException {
    	FileInputStream fileInputStream = null;
    	try{
    		final File file = new File(fileName);
    		fileInputStream = new FileInputStream(file);
    		defaultProperties.load(fileInputStream);
    	}
    	catch(Exception e)
    	{
    		LOG.errorTrace("QueryConstants:load()", e);
    	}
    	finally{
    		if(fileInputStream != null)
    		{
    			try{
            		if(fileInputStream != null){
            			fileInputStream.close();	
            		}
            	}catch(Exception e){
            		 LOG.errorTrace("QueryConstants:load()", e);
            	}
    		}
    	}
    }
    
    public static String getDefaultDAOName(String propertyName) {
    	String value = "";
    	if(!BTSLUtil.isNullString(SqlParameterEncoder.encodeParams(properties.getProperty(propertyName))))
    		value =  SqlParameterEncoder.encodeParams(defaultProperties.getProperty(propertyName)).trim();
    	return value;
    }

    public static String getDAOName(String propertyName) {
    	String value = "";
    	if(!BTSLUtil.isNullString(SqlParameterEncoder.encodeParams(properties.getProperty(propertyName))))
    		value =  SqlParameterEncoder.encodeParams(properties.getProperty(propertyName)).trim();
    	return value;
    }
}

package com.btsl.pretups.common;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.springframework.context.NoSuchMessageException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.AdditionalCommissionDetailsReportQry;
import com.btsl.pretups.channel.reports.businesslogic.AdditionalCommissionSummaryReportQry;
import com.btsl.pretups.channel.reports.businesslogic.Channel2ChannelTransferRetWidRptQry;
import com.btsl.pretups.channel.reports.businesslogic.ChannelUserOperatorUserRolesQuery;
import com.btsl.pretups.channel.reports.businesslogic.O2CTransferDetailsRptQry;
import com.btsl.pretups.channel.reports.businesslogic.O2CTransfernumberAckRptQry;
import com.btsl.pretups.channel.reports.businesslogic.OperationSummaryRptQry;
import com.btsl.pretups.channel.reports.businesslogic.UserDailyBalanceMovementRptQuery;
import com.btsl.pretups.channel.reports.businesslogic.UserZeroBalanceCounterSummaryQry;
import com.btsl.pretups.channel.reports.businesslogic.ZeroBalanceCounterDetailsRptQry;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.channel.reports.web.UsersReportModel;
import com.web.pretups.channel.transfer.web.ChannelTransferAckModel;

/**
 * @author pankaj.kumar
 *
 */
public class DownloadCSVReports{
	public static final Log log = LogFactory.getLog(DownloadCSVReports.class.getName());
	private static String c2cTrfDetails = Constants.getProperty("C2C_TRANSFER_DETAILS_REPORTS_DOWNLOAD_FILE_NAME");
	private static String addCommTrfDetails = Constants.getProperty("ADDITIONAL_COMMISSION_DETAILS_REPORTS_DOWNLOAD_FILE_NAME");
	private static String o2cTrfDetails = Constants.getProperty("O2C_TRANSFER_DETAILS_REPORTS_DOWNLOAD_FILE_NAME");
	private static String userBalMovSum = Constants.getProperty("USER_BALANCE_MOVEMENT_SUMMARY_REPORTS_DOWNLOAD_FILE_NAME");
	private static String path=Constants.getProperty("REPORTS_DOWNLOAD_PATH");
	private static String zeroBalanceCounterDetailsFileName = Constants.getProperty("ZERO_BALANCE_COUNTER_DETAIL_REPORTS_DOWNLOAD_FILE_NAME");
	private static String addCommTrfSummary = Constants.getProperty("ADDITIONAL_COMMISSION_SUMMARY_REPORTS_DOWNLOAD_FILE_NAME");
	private static String o2cTrfAckDetails = Constants.getProperty("O2C_TRANSFER_ACK_DETAILS_REPORTS_DOWNLOAD_FILE_NAME");
	private static String operationSummaryReports = Constants.getProperty("OPERATION_SUMMARY_REPORTS_DOWNLOAD_FILE_NAME");
	
    O2CTransferDetailsRptQry o2cTransferDetailsRptQry;
    private ChannelUserOperatorUserRolesQuery channelUserOperatprUserRolesQry;
	private Channel2ChannelTransferRetWidRptQry channel2ChannelTransferRetWidRptQry;
	private AdditionalCommissionDetailsReportQry additionalCommissionDetailsReportQry;
	private UserZeroBalanceCounterSummaryQry userZeroBalanceCounterSummaryQry;
	private ZeroBalanceCounterDetailsRptQry zeroBalanceCounterDetailsRptQry;
	private UserDailyBalanceMovementRptQuery userDailyBalanceMovementRptQuery;
	private AdditionalCommissionSummaryReportQry additionalCommissionSummaryReportQry;
	private O2CTransfernumberAckRptQry o2CTransferAckRptQry;
	private OperationSummaryRptQry operationSummaryRptQry;
	
	public DownloadCSVReports(){
		o2cTransferDetailsRptQry = (O2CTransferDetailsRptQry)ObjectProducer.getObject(QueryConstants.O2C_TRANSFER_DETAILS_REPORT_QRY, QueryConstants.QUERY_PRODUCER);
		channel2ChannelTransferRetWidRptQry = (Channel2ChannelTransferRetWidRptQry)ObjectProducer.getObject(QueryConstants.CHANNEL_2_CHANNEL_TRANSFER_RET_WD_REPORT_QRY, QueryConstants.QUERY_PRODUCER);
		additionalCommissionDetailsReportQry = (AdditionalCommissionDetailsReportQry) ObjectProducer.getObject(QueryConstants.ADDITIONAL_COMMISSION_DETAILS_REPORT_QRY, QueryConstants.QUERY_PRODUCER);
		userZeroBalanceCounterSummaryQry = (UserZeroBalanceCounterSummaryQry) ObjectProducer.getObject(QueryConstants.USER_BAL_SUMMARY_REPORT_QRY, QueryConstants.QUERY_PRODUCER);
		zeroBalanceCounterDetailsRptQry = (ZeroBalanceCounterDetailsRptQry)ObjectProducer.getObject(QueryConstants.ZERO_BALANCE_COUNTER_DETAILS_REPORT_QRY, QueryConstants.QUERY_PRODUCER);
		userDailyBalanceMovementRptQuery = (UserDailyBalanceMovementRptQuery)ObjectProducer.getObject(QueryConstants.USER_BAL_MOVEMENT_REPORT_QRY,QueryConstants.QUERY_PRODUCER);
		additionalCommissionSummaryReportQry = (AdditionalCommissionSummaryReportQry) ObjectProducer.getObject(QueryConstants.ADDITIONAL_COMMISSION_SUMMARY_REPORT_QRY, QueryConstants.QUERY_PRODUCER);
		o2CTransferAckRptQry = (O2CTransfernumberAckRptQry)ObjectProducer.getObject(QueryConstants.O2C_TRANSFER_NUMBER_ASK_QRY, QueryConstants.QUERY_PRODUCER);
		channelUserOperatprUserRolesQry = (ChannelUserOperatorUserRolesQuery) ObjectProducer
				.getObject(QueryConstants.EXTERNAL_USR_REPORT_QRY,
						QueryConstants.QUERY_PRODUCER);
		operationSummaryRptQry = (OperationSummaryRptQry)ObjectProducer.getObject(QueryConstants.OPERATION_SUMMARY_REPORT_QRY, QueryConstants.QUERY_PRODUCER);
	}
	
	public String prepareData(UsersReportModel usersReportModel, String rptCode, Connection con) throws InterruptedException
	{
		if (log.isDebugEnabled())
			log.debug("prepareData Entered: ", rptCode);
		String filePath = "";
		switch (rptCode) {
		/*Channel to Channel transfer Details for Channel user OPTIN/OUT == ALL and STAFF RPT == N*/
		case "C2CRWTR03":
			filePath = prepareDataC2cRetWidTransferChannelUserUnion(usersReportModel,con);
			break;

			/*Channel to Channel transfer Details for Channel user OPTIN/OUT == ALL and STAFF RPT == Y*/
		case "C2CRWTR07":
			filePath = prepareDataC2cRetWidTransferChannelUserUnionStaff(usersReportModel,con);
			break;

			/*Channel to Channel transfer Details for Channel user OPTIN/OUT != ALL and STAFF RPT == N*/
		case "C2CRWTR04":
			filePath = prepareDataC2cRetWidTransferChannelUser(usersReportModel,con);
			break;

			/*Channel to Channel transfer Details for Channel user OPTIN/OUT != ALL and STAFF RPT == Y*/
		case "C2CRWTR08":
			filePath = prepareDataC2cRetWidTransferChnlUserStaff(usersReportModel,con);
			break;

			/*Channel to Channel transfer Details for Operator user OPTIN/OUT == ALL and STAFF RPT == N*/
		case "C2CRWTR02":
			filePath = prepareDataC2cRetWidTransferUnionList(usersReportModel,con);
			break;

			/*Channel to Channel transfer Details for Operator user OPTIN/OUT == ALL and STAFF RPT == Y*/
		case "C2STRANSFER06":
			filePath = prepareDataC2cRetWidTransferUnionStaff(usersReportModel,con);
			break;

			/*Channel to Channel transfer Details for Operator user OPTIN/OUT != ALL and STAFF RPT == N*/
		case "C2CRWTR01":
			filePath = prepareDataC2cRetWidTransferList(usersReportModel,con);
			break;

			/*Channel to Channel transfer Details for Operator user OPTIN/OUT != ALL and STAFF RPT == Y*/
		case "C2CRWTR05":
			filePath = prepareDataC2cRetWidTransferStaff(usersReportModel,con);
			break;

		case "02CTRFDET01":
			filePath = prepareDataO2CTransferDetailsForOperatorUsers(usersReportModel,con);
			break;

		case "02CTRFDET02":
			filePath = prepareDataO2CTransferDetailsForChannelUser(usersReportModel,con);

			/*case "02CTRFDLY01":
			filePath = prepareDataO2CTransferDetailsDaily(usersReportModel,con);
		default:*/
			break;

		case "USERBALMOV01":
			filePath = prepareDataUserBalMovSumForOperatorUser(usersReportModel,con);
			break;
			
		case "USERBALMOV02":
			filePath = prepareDataUserBalMovSumForChannelUser(usersReportModel,con);
			break;

		case "ADDCOMDT01":
			filePath = prepareAddCommDetailsOptUser(usersReportModel,con);
			break;

		case "ADDCOMDTCH01":
			filePath = prepareAddCommDetailsChnlUser(usersReportModel,con);
			break;
			
		case "ZBALSUM001":
			filePath = prepareDatazeroBalSumm(usersReportModel, con);
			break;

		case "ASSCOMSUM01":
			filePath = prepareAddCommSummaryOptDaily(usersReportModel,con);
			break;

		case "ASSCOMSUM02":
			filePath = prepareAddCommSummaryOptMonthly(usersReportModel,con);
			break;
			
		case "ADCOMSMYCH01":
			filePath = prepareAddCommSummaryChnlDaily(usersReportModel,con);
			break;

		case "ADCOMSMYCH02":
			filePath = prepareAddCommSummaryChnlMonthly(usersReportModel,con);
			break;	
			
			
		case "C2SSWCHUS01":
			filePath = prepareDataExternalUserReport(usersReportModel, con);

			break;
		case "C2SSWCHUS02":
			filePath = prepareDataExternalUserReport(usersReportModel, con);
			break;
			
		case "OPSUMMAIN01":
			filePath = prepareOperationSummaryReport(usersReportModel,con);
			break;
		case "OPSUMTOTAL02":
			filePath = prepareOperationSummaryReport(usersReportModel,con);
			break;
		case "O2STRSUMRY02":
			filePath = prepareOperationSummaryReport(usersReportModel,con);
			break;
		case "O2STRSUMRY01":
			filePath = prepareOperationSummaryReport(usersReportModel,con);
			break;	
		default:
         	 if(log.isDebugEnabled()){
         		log.debug("Default Value " ,rptCode);
         	 }
		}

		return filePath;

	}
	private String prepareOperationSummaryReport(UsersReportModel usersReportModel, Connection con) {
		final String methodName = "prepareOperationSummaryReport";
		PreparedStatement pstmt = null;
		if(log.isDebugEnabled())
		{
			log.debug(methodName, usersReportModel.getUserType()+ " "+usersReportModel.getRadioNetCode());
		}
		
		String filePath = "";
		String fileName = "";


		try {
			fileName = operationSummaryReports + BTSLUtil.getFileNameStringFromDate(new Date())
					+ ".csv";
			filePath = path;
			File fileDir = new File(filePath);
			if (!fileDir.isDirectory()) {
				fileDir.mkdirs();
			}
			final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
			con.setAutoCommit(false);
			
			
			 pstmt = prepareOperationReportCondition(usersReportModel, con,
					pstmt);
			pstmt.setFetchSize(1000);
			MutableBoolean mutableBoolean = new MutableBoolean(false);
			MutableBoolean filewritten = new MutableBoolean(false);
			final String columnHeader;
			final String reportTopHeaders;
			StringBuilder sb =  new StringBuilder(1024);
			reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.label.report")).append(" ; ").append( "")
					.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.label.networkname")).append(" : ").append( usersReportModel.getNetworkName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.rptcode")).append(" : ").append( usersReportModel.getrptCode()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.label.zone")).append(" : ").append( usersReportModel.getZoneName()).append(" , ")
                    .append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.label.channelcategory")).append(" : " ).append( usersReportModel.getCategoryName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.label.channeldomain")).append(" : " ).append( usersReportModel.getDomainName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.label.channelcategoryuser")).append(" : " ).append( usersReportModel.getUserName()).append(" , ")
					
					.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.label.fromdate")).append(" : " ).append( usersReportModel.getFromDate()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.label.todate")).append(" : " ).append( usersReportModel.getToDate()).toString();
			
			sb.setLength(0);
			columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.date")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.label.channelcategoryuser")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.msisdn")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.startBalance")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.o2ctrfinCount")).append(",")
					
											.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.o2ctrfinAmount")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.c2ctrfinCount")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.c2ctrfinAmount")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.c2cReturnandWithdrawinCount")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.c2cReturnandWithdrawinAmount")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.o2ctrfoutCount")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.o2ctrfoutAmount")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.c2ctrfoutCount")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.c2ctrfoutAmount")).append(",")
											
											.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.c2cReturnandWithdrawoutCount")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.c2cReturnandWithdrawoutAmount")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.c2strfcount")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.c2strfamount")).append(",")
											
											
											.append(PretupsRestUtil.getMessageString("pretups.channelReportsSummary.operationSummaryReport.endingbalance")).toString();
											
											

			final String[] resultSetColumnName = {"trans_date","user_name","msisdn","opening_balance","o2c_transfer_in_count",
					"o2c_transfer_in_amount","c2c_transfer_in_count","c2c_transfer_in_amount","c2c_return_plus_with_in_count","c2c_return_plus_with_in_amount",
					"o2c_return_plus_with_out_count","o2c_return_plus_with_out_amount","c2c_transfer_out_count","c2c_transfer_out_amount","c2c_return_plus_with_out_count","c2c_return_plus_with_out_amount","c2s_transfer_out_count","c2s_transfer_out_amount",
					"closing_balance"};
			CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,	columnHeader,reportTopHeaders);
			CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);
			this.executeReaderWriterThread(reader,  csvWriter);
		}catch (InterruptedException | ParseException | SQLException | NoSuchMessageException e) 
		{
			log.errorTrace(methodName, e);
		}
		finally{
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
			if(log.isDebugEnabled())
				log.debug(methodName, PretupsI.EXITED);
		}
		return filePath + fileName;
	}

	private PreparedStatement prepareOperationReportCondition(
			UsersReportModel usersReportModel, Connection con,
			PreparedStatement pstmt) {
		if(TypesI.CHANNEL_USER_TYPE.equalsIgnoreCase(usersReportModel.getUserType()) && (PretupsI.ACCOUNT_TYPE_MAIN).equals(usersReportModel.getRadioNetCode())){ 
			 pstmt = operationSummaryRptQry.loadOperationSummaryChannelUserMainReport(usersReportModel, con);				    
		 }
		 if(TypesI.CHANNEL_USER_TYPE.equalsIgnoreCase(usersReportModel.getUserType()) && (PretupsI.ACCOUNT_TYPE_TOTAL).equals(usersReportModel.getRadioNetCode())){ 
			 pstmt = operationSummaryRptQry.loadOperationSummaryChannelUserTotalReport(usersReportModel,con);				    
		 }
		 if(TypesI.OPERATOR_USER_TYPE.equalsIgnoreCase(usersReportModel.getUserType()) && (PretupsI.ACCOUNT_TYPE_MAIN).equals(usersReportModel.getRadioNetCode())){ 
			 pstmt = operationSummaryRptQry.loadOperationSummaryOperatorMainReport(usersReportModel, con);				    
		 }
		 if(TypesI.OPERATOR_USER_TYPE.equalsIgnoreCase(usersReportModel.getUserType()) && (PretupsI.ACCOUNT_TYPE_TOTAL).equals(usersReportModel.getRadioNetCode())){ 
			 pstmt = operationSummaryRptQry.loadOperationSummaryOperatorTotalReport(usersReportModel, con);				    
		 }
		return pstmt;
	}

	public String prepareDataForZeroBalanceCounterDetails(UsersReportModel usersReportModel, String rptCode, Connection con) throws SQLException, ParseException
	{
		if (log.isDebugEnabled()){
			log.debug("prepareDataForZeroBalanceCounterDetails Entered: ", rptCode);  
		}
		String methodName="prepareDataForZeroBalanceCounterDetails";
		String filePath="";   
		try {
			filePath= prepareDataZerBalCouterDetailsForChannelUser(usersReportModel,con);
		}  catch (ParseException | SQLException e) {
			
			log.errorTrace(methodName, e);
		}
		return filePath;
	}
	@SuppressWarnings("resource")
	private String prepareDataZerBalCouterDetailsForChannelUser(UsersReportModel usersReportModel, Connection con) throws SQLException, ParseException {
		
				 final String methodName = "prepareDataZerBalCouterDetailsForChannelUser";
				 PreparedStatement pstmt = null;		
				 if(log.isDebugEnabled()){
					log.debug(methodName, usersReportModel.getUserType());
				}
				String filePath = "";
				String fileName = "";
	        try {
	        	fileName = zeroBalanceCounterDetailsFileName + BTSLUtil.getFileNameStringFromDate(new Date())+ ".csv";
				filePath = path;
				File fileDir = new File(filePath);
				if (!fileDir.isDirectory()) {
					fileDir.mkdirs();   
				}
				final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
				con.setAutoCommit(false);				
				 if(TypesI.CHANNEL_USER_TYPE.equalsIgnoreCase(usersReportModel.getUserType())){ 
				     pstmt=zeroBalanceCounterDetailsRptQry.loadoZeroBalCounterChnlUserDetailsReportQry(con, usersReportModel);				    
				 }
				 if(TypesI.OPERATOR_USER_TYPE.equalsIgnoreCase(usersReportModel.getUserType())){
					 pstmt=zeroBalanceCounterDetailsRptQry.loadoZeroBalCounterDetailsReportQry(con, usersReportModel); 					
				 }      
				 pstmt.setFetchSize(1000);
			    MutableBoolean mutableBoolean = new MutableBoolean(false);
			    MutableBoolean filewritten = new MutableBoolean(false);	
			    StringBuilder sb =  new StringBuilder(1024);
			    final String reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.label.zerobalancecounterdetails.header")).append(" ; ").append( "")
			    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.label.zerobalancecounterdetails.thresholdtype")).append(usersReportModel.getThresholdName()).append(" , ")
			    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.label.zerobalancecounterdetails.fromdate")).append(usersReportModel.getFromDate()).append(" , ")
			    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.label.zerobalancecounterdetails.todate")).append(usersReportModel.getToDate()).append(" , ")	
			    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.label.zerobalancecounterdetails.msisdn")).append(usersReportModel.getMsisdn()).append(" , ")
			    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.label.zerobalancecounterdetails.zone")).append(usersReportModel.getZoneName()).append(" , ")
			    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.label.zerobalancecounterdetails.domainCode")).append(usersReportModel.getDomainName()).append(" , ")	
			    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.label.zerobalancecounterdetails.channelcategory")).append(usersReportModel.getCategoryName()).append(" , ")	  
	                    .append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.label.zerobalancecounterdetails.channelcategoryuser")).append(usersReportModel.getUserName()).toString() ;	                                                      	                   
			    sb.setLength(0);         
			    final String columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.zerobalancecounterdetails.columnHeader.userName")).append(",")
			    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.zerobalancecounterdetails.columnHeader.msisdn")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.zerobalancecounterdetails.columnHeader.userStatus")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.zerobalancecounterdetails.columnHeader.entryDateTime")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.zerobalancecounterdetails.columnHeader.transferId")).append(",")							
					    .append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.zerobalancecounterdetails.columnHeader.transactionType")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.zerobalancecounterdetails.columnHeader.categoryName")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.zerobalancecounterdetails.columnHeader.productName")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.zerobalancecounterdetails.columnHeader.recordType")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.zerobalancecounterdetails.columnHeader.previousBalance")).append(",")
	                    .append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.zerobalancecounterdetails.columnHeader.thresholdValue")).toString();	
			    
			   final String[] resultSetColumnName = { "user_name", "msisdn","user_status","entry_date_time","transfer_id","transaction_type","category_name", "product_name", "record_type", "previous_balance", "threshold_value" };					                             					                             					                              					                              					                              					                              					                              					                             					                             					                            					                             					                             					                            							
			   
			   CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,	columnHeader,reportTopHeaders);
			   CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);
			   this.executeReaderWriterThread(reader,  csvWriter);
		} 
		catch (InterruptedException | ParseException | SQLException e){
			log.errorTrace(methodName, e);
		}
		finally{
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
		}
		return filePath + fileName;
	}
	/**
	 * @param csvReportReader
	 * @param csvReportWriter
	 * @throws InterruptedException
	 * Note Generic method will be use in multiple location in download reports
	 */
	public  void executeReaderWriterThread(CSVReportReader csvReportReader, CSVReportWriter csvReportWriter) throws InterruptedException{
		final String methodName = "executeReaderWriterThread";
		Thread w = new Thread(csvReportWriter, "Writer Thread");
		Thread r = new Thread(csvReportReader, "Reader Thread");
		w.setUncaughtExceptionHandler((thread, exception) -> {
			log.debug("Exception occurred in ", thread.getName());
			log.errorTrace(methodName, exception);
		});
		r.setUncaughtExceptionHandler((thread, exception) -> {
			log.debug("Exception occurred in ", thread.getName());
			log.errorTrace(methodName, exception);
		});

		log.debug("Starting thread to strat writing on the file", w.getName());
		long startTime = System.currentTimeMillis();
		w.start();
		log.debug("Starting thread to strat reading on the file", r.getName());
		r.start();
		w.join();
		long endTime = System.currentTimeMillis();
		log.debug("Total time in reading data from DB and wrting data on csv file ",
				(endTime - startTime) + "miliseconds");
	}
/**
 * 	
 * @param usersReportModel
 * @param con
 * @return
 */
		private String prepareDataC2cRetWidTransferChannelUserUnion(UsersReportModel usersReportModel, Connection con) {
		final String methodName = "prepareDataC2cRetWidTransferChannelUserUnion";
		if(log.isDebugEnabled())
			log.debug(methodName, PretupsI.ENTERED);
		PreparedStatement pstmt = null;
		String filePath = "";
		String fileName = "";
        try {
	        	fileName = c2cTrfDetails + BTSLUtil.getFileNameStringFromDate(new Date())+ ".csv";
				filePath = path;
				File fileDir = new File(filePath);
				if (!fileDir.isDirectory()) {
					fileDir.mkdirs();
				}
				final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
				con.setAutoCommit(false);
				pstmt = channel2ChannelTransferRetWidRptQry.loadC2cRetWidTransferChannelUserUnionListQry(con,usersReportModel);
	        	pstmt.setFetchSize(1000);
				MutableBoolean mutableBoolean = new MutableBoolean(false);
				MutableBoolean filewritten = new MutableBoolean(false);
				StringBuilder sb =  new StringBuilder(1024);
				final String reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.report")).append(" ; ").append("")
						.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.rptcode")).append(" : ").append(usersReportModel.getrptCode()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.transfersubtype")).append(" : ").append(usersReportModel.getTxnSubTypeName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.inout")).append(" : " ).append( usersReportModel.getTransferInOrOutName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.fromdate")).append(" : ").append(usersReportModel.getFromDate()).append(" ").append(usersReportModel.getFromTime()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.todate")).append(" : " ).append( usersReportModel.getToDate()).append(" ").append( usersReportModel.getToTime()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.zone")).append(" : " ).append( usersReportModel.getZoneName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.domain")).append(" : " ).append( usersReportModel.getDomainName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.searchcategory")).append(" : ").append( usersReportModel.getFromtransferCategoryName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.searchuser")).append(" : " ).append( usersReportModel.getUserName()).append(",")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.transactionusrcat")).append(" : ").append( usersReportModel.getTotransferCategoryName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.transactionusr")).append(" : " ).append( usersReportModel.getTouserName()).toString();

				sb.setLength(0);
				final String columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.fromUser")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.toUser")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferid")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.source")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferSubType")).append(",")
											
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferDate")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.modifiedOn")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.productName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.senderCatName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.recCatCode")).append(",")
						
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferMRP")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.mrp")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.commission")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.otf")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.tax3")).append(",")
						
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.senderDR")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.receiverCR")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.payableAmt")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.netPayableAmt")).toString();
						
				final String[] resultSetColumnName = {"from_user","to_user","transfer_id","SOURCE","transfer_sub_type","transfer_date","modified_ON","product_name","sender_category_name",
						"receiver_category_name","transfer_mrp","mrp","commision","otf_amount","tax3_value","sender_debit_quantity","receiver_credit_quantity","payable_amount","net_payable_amount"};
			

			CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,	columnHeader,reportTopHeaders);
			CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);

			this.executeReaderWriterThread(reader,  csvWriter);
		} 
		catch (InterruptedException | ParseException | SQLException e) 
		{
			log.errorTrace(methodName, e);
		}
		finally{
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
			if(log.isDebugEnabled())
				log.debug(methodName, PretupsI.EXITED);
		}
		return filePath + fileName;

	} 
/**
 * 
 * @param usersReportModel
 * @param con
 * @return
 * @throws InterruptedException
 */
		private String prepareDataC2cRetWidTransferChannelUserUnionStaff(UsersReportModel usersReportModel,
				Connection con) throws InterruptedException{
			final String methodName = "prepareDataC2cRetWidTransferChannelUserUnionStaff";
			if(log.isDebugEnabled())
				log.debug(methodName, PretupsI.ENTERED);
			PreparedStatement pstmt = null;
			String filePath = "";
			String fileName = "";
			try{
				fileName = c2cTrfDetails + BTSLUtil.getFileNameStringFromDate(new Date())+ ".csv";
				filePath = path;
				File fileDir = new File(filePath);
				if (!fileDir.isDirectory()) {
					fileDir.mkdirs();
				}
				final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
				con.setAutoCommit(false);
				pstmt = channel2ChannelTransferRetWidRptQry.loadC2cRetWidTransferChannelUserUnionStaffListQry(con,usersReportModel);
	        	pstmt.setFetchSize(1000);
				MutableBoolean mutableBoolean = new MutableBoolean(false);
				MutableBoolean filewritten = new MutableBoolean(false);
				StringBuilder sb =  new StringBuilder(1024);
				final String reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.report")).append(" ; ").append( "")
						.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.rptcode")).append(" : ").append( usersReportModel.getrptCode()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.transfersubtype")).append(" : ").append( usersReportModel.getTxnSubTypeName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.inout")).append(" : " ).append( usersReportModel.getTransferInOrOutName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.fromdate")).append(" : ").append( usersReportModel.getFromDate()).append(" ").append( usersReportModel.getFromTime()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.todate")).append(" : " ).append( usersReportModel.getToDate()).append(" ").append( usersReportModel.getToTime()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.zone")).append(" : " ).append( usersReportModel.getZoneName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.domain")).append(" : " ).append( usersReportModel.getDomainName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.searchcategory")).append(" : ").append( usersReportModel.getFromtransferCategoryName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.searchuser")).append(" : " ).append( usersReportModel.getUserName()).append(",")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.transactionusrcat")).append(" : ").append( usersReportModel.getTotransferCategoryName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.transactionusr")).append(" : " ).append( usersReportModel.getTouserName()).toString();
				sb.setLength(0);
				final String columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.ownerProfile")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.parentProfile")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.fromUser")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.toUser")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.initiatorUser")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferid")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.source")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferSubType")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferDate")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.modifiedOn")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.productName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.senderCatName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.recCatCode")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferMRP")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.mrp")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.commission")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.tax3")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.senderDR")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.receiverCR")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.payableAmt")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.netPayableAmt")).toString();
				
				final String[] resultSetColumnName = {"owner_profile","parent_profile","from_user","to_user","initiator_user",
													  "transfer_id","SOURCE","transfer_sub_type","transfer_date","modified_ON",
													  "product_name","sender_category_name","receiver_category_name","transfer_mrp","mrp",
													  "commision","tax3_value","sender_debit_quantity","receiver_credit_quantity","payable_amount",
													  "net_payable_amount"};
			

				
				
				
				
			CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,	columnHeader,reportTopHeaders);
			CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);

			this.executeReaderWriterThread(reader,  csvWriter);
			} 
			catch (InterruptedException | ParseException | SQLException e) 
			{
				log.errorTrace(methodName, e);
			}
			finally{
				try{
			        if (pstmt!= null){
			        	pstmt.close();
			        }
			      }
			      catch (SQLException e){
			    	  log.error("An error occurred closing statement.", e);
			      }
				if(log.isDebugEnabled())
					log.debug(methodName, PretupsI.EXITED);
			}
			return filePath + fileName;
		}
/**
 * 	
 * @param usersReportModel
 * @param con
 * @return
 * @throws InterruptedException
 */
		private String prepareDataC2cRetWidTransferChannelUser(UsersReportModel usersReportModel, Connection con) throws InterruptedException{
			final String methodName = "prepareDataC2cRetWidTransferChannelUser";
			if(log.isDebugEnabled())
				log.debug(methodName, PretupsI.ENTERED);
			PreparedStatement pstmt = null;
			String filePath = "";
			String fileName = "";
			try{
				fileName = c2cTrfDetails + BTSLUtil.getFileNameStringFromDate(new Date())+ ".csv";
				filePath = path;
				File fileDir = new File(filePath);
				if (!fileDir.isDirectory()) {
					fileDir.mkdirs();
				}
				final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
				con.setAutoCommit(false);
				pstmt = channel2ChannelTransferRetWidRptQry.loadC2cRetWidTransferChannelUserListQry(con,usersReportModel);
	        	pstmt.setFetchSize(1000);
				MutableBoolean mutableBoolean = new MutableBoolean(false);
				MutableBoolean filewritten = new MutableBoolean(false);
				StringBuilder sb =  new StringBuilder(1024);
				final String reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.report")).append(" ; ").append( "")
						.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.rptcode")).append(" : ").append( usersReportModel.getrptCode()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.transfersubtype")).append(" : ").append( usersReportModel.getTxnSubTypeName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.inout")).append(" : " ).append( usersReportModel.getTransferInOrOutName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.fromdate")).append(" : ").append( usersReportModel.getFromDate()).append(" ").append( usersReportModel.getFromTime()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.todate")).append(" : " ).append( usersReportModel.getToDate()).append(" ").append( usersReportModel.getToTime()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.zone")).append(" : " ).append( usersReportModel.getZoneName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.domain")).append(" : " ).append( usersReportModel.getDomainName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.searchcategory")).append(" : ").append( usersReportModel.getFromtransferCategoryName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.searchuser")).append(" : " ).append( usersReportModel.getUserName()).append(",")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.transactionusrcat")).append(" : ").append( usersReportModel.getTotransferCategoryName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.transactionusr")).append(" : " ).append( usersReportModel.getTouserName()).toString();
				sb.setLength(0);
				final String columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.ownerProfile")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.parentProfile")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.fromUser")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.fromMSISDN")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.toUser")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.toMSISDN")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferid")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.grphDomainName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferSubType")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferDate")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.modifiedOn")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.productName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.senderCatName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.recCatCode")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferMRP")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.mrp")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.commission")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.tax3")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.senderDR")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.receiverCR")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.payableAmt")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.netPayableAmt")).toString();
											
				final String[] resultSetColumnName = {"owner_profile","parent_profile","from_user","from_msisdn","to_user",
													  "to_msisdn","transfer_id","GRPH_DOMAIN_NAME","transfer_sub_type","transfer_date",
													  "modified_ON","product_name","sender_category_name","receiver_category_name","transfer_mrp",
													  "mrp","commision","tax3_value","sender_debit_quantity","receiver_credit_quantity",
													  "payable_amount","net_payable_amount"};
			

			CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,	columnHeader,reportTopHeaders);
			CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);

			this.executeReaderWriterThread(reader,  csvWriter);
			} 
			catch (InterruptedException | ParseException | SQLException e) 
			{
				log.errorTrace(methodName, e);
			}
			finally{
				try{
			        if (pstmt!= null){
			        	pstmt.close();
			        }
			      }
			      catch (SQLException e){
			    	  log.error("An error occurred closing statement.", e);
			      }
				if(log.isDebugEnabled())
					log.debug(methodName, PretupsI.EXITED);
			}
			return filePath + fileName;
		}
/**
 * 	
 * @param usersReportModel
 * @param con
 * @return
 * @throws InterruptedException
 */
		private String prepareDataC2cRetWidTransferChnlUserStaff(UsersReportModel usersReportModel, Connection con)throws InterruptedException {
			final String methodName = "prepareDataC2cRetWidTransferChnlUserStaff";
			if(log.isDebugEnabled())
				log.debug(methodName, PretupsI.ENTERED);
			PreparedStatement pstmt = null;
			String filePath = "";
			String fileName = "";
			try{
				fileName = c2cTrfDetails + BTSLUtil.getFileNameStringFromDate(new Date())+ ".csv";
				filePath = path;
				File fileDir = new File(filePath);
				if (!fileDir.isDirectory()) {
					fileDir.mkdirs();
				}
				final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
				con.setAutoCommit(false);
				pstmt = channel2ChannelTransferRetWidRptQry.loadC2cRetWidTransferChnlUserStaffListQry(con,usersReportModel);
	        	pstmt.setFetchSize(1000);
				MutableBoolean mutableBoolean = new MutableBoolean(false);
				MutableBoolean filewritten = new MutableBoolean(false);	
				StringBuilder sb =  new StringBuilder(1024);
				final String reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.report")).append(" ; ").append( "")
						.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.rptcode")).append(" : ").append( usersReportModel.getrptCode()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.transfersubtype")).append(" : ").append( usersReportModel.getTxnSubTypeName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.inout")).append(" : " ).append( usersReportModel.getTransferInOrOutName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.fromdate")).append(" : ").append( usersReportModel.getFromDate()).append(" ").append( usersReportModel.getFromTime()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.todate")).append(" : " ).append( usersReportModel.getToDate()).append(" ").append( usersReportModel.getToTime()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.zone")).append(" : " ).append( usersReportModel.getZoneName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.domain")).append(" : " ).append( usersReportModel.getDomainName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.searchcategory")).append(" : ").append( usersReportModel.getFromtransferCategoryName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.searchuser")).append(" : " ).append( usersReportModel.getUserName()).append(",")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.transactionusrcat")).append(" : ").append( usersReportModel.getTotransferCategoryName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.transactionusr")).append(" : " ).append( usersReportModel.getTouserName()).toString();
				sb.setLength(0);
				final String columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.ownerProfile")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.parentProfile")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.fromUser")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.toUser")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.initiatorUser")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferid")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferSubType")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferDate")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.modifiedOn")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.productName")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.senderCatName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.recCatCode")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferMRP")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.mrp")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.commission")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.tax3")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.senderDR")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.receiverCR")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.payableAmt")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.netPayableAmt")).toString(); 
				
				final String[] resultSetColumnName = {"owner_profile","parent_profile","from_user","to_user","initiator_user",
													  "transfer_id","transfer_sub_type","transfer_date","modified_ON","product_name",
													  "sender_category_name","receiver_category_name","transfer_mrp","mrp","commision",
													  "tax3_value","sender_debit_quantity","receiver_credit_quantity","payable_amount","net_payable_amount"};
			

			CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,	columnHeader,reportTopHeaders);
			CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);

			this.executeReaderWriterThread(reader,  csvWriter);
			} 
			catch (InterruptedException | ParseException | SQLException e) 
			{
				log.errorTrace(methodName, e);
			}
			finally{
				try{
			        if (pstmt!= null){
			        	pstmt.close();
			        }
			      }
			      catch (SQLException e){
			    	  log.error("An error occurred closing statement.", e);
			      }
				if(log.isDebugEnabled())
					log.debug(methodName, PretupsI.EXITED);
			}
			return filePath + fileName;
		}
/**
 * 		
 * @param usersReportModel
 * @param con
 * @return
 * @throws InterruptedException
 */
		private String prepareDataC2cRetWidTransferUnionList(UsersReportModel usersReportModel, Connection con)throws InterruptedException {
			final String methodName = "prepareDataC2cRetWidTransferUnionList";
			if(log.isDebugEnabled())
				log.debug(methodName, PretupsI.ENTERED);
			PreparedStatement pstmt = null;
			String filePath = "";
			String fileName = "";
			try{
				fileName = c2cTrfDetails + BTSLUtil.getFileNameStringFromDate(new Date())+ ".csv";
				filePath = path;
				File fileDir = new File(filePath);
				if (!fileDir.isDirectory()) {
					fileDir.mkdirs();
				}
				final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
				con.setAutoCommit(false);
				pstmt = channel2ChannelTransferRetWidRptQry.loadC2cRetWidTransferUnionListQry(con,usersReportModel);
	        	pstmt.setFetchSize(1000);
				MutableBoolean mutableBoolean = new MutableBoolean(false);
				MutableBoolean filewritten = new MutableBoolean(false);
				StringBuilder sb =  new StringBuilder(1024);
				final String reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.report")).append(" ; ").append( "")
						.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.rptcode")).append(" : ").append( usersReportModel.getrptCode()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.transfersubtype")).append(" : ").append( usersReportModel.getTxnSubTypeName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.inout")).append(" : " ).append( usersReportModel.getTransferInOrOutName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.fromdate")).append(" : ").append( usersReportModel.getFromDate()).append(" ").append( usersReportModel.getFromTime()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.todate")).append(" : " ).append( usersReportModel.getToDate()).append(" ").append( usersReportModel.getToTime()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.zone")).append(" : " ).append( usersReportModel.getZoneName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.domain")).append(" : " ).append( usersReportModel.getDomainName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.searchcategory")).append(" : ").append( usersReportModel.getFromtransferCategoryName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.searchuser")).append(" : " ).append( usersReportModel.getUserName()).append(",")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.transactionusrcat")).append(" : ").append( usersReportModel.getTotransferCategoryName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.transactionusr")).append(" : " ).append( usersReportModel.getTouserName()).toString();
				sb.setLength(0);
				final String columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.fromUser")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.fromMSISDN")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.fromUserGeo")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.fromOwnerGeo")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.senderCatName")).append(",")
						
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.fromEXTCODE")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.toUser")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.toMSISDN")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.toOwnerGeo")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.recCatCode")).append(",")
						
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.toEXTCODE")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.source")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferSubType")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferid")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferDate")).append(",")
						
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.modifiedOn")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.productName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferMRP")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.mrp")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.commission")).append(",")
						
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.otf")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.tax3")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.senderDR")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.receiverCR")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.payableAmt")).append(",")
						
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.netPayableAmt")).toString();
				
				final String[] resultSetColumnName = {"from_user","from_msisdn","from_user_geo","from_owner_geo","sender_category_name",
													  "from_ext_code","to_user","to_msisdn","to_owner_geo","receiver_category_name",
													  "to_ext_code","SOURCE","transfer_sub_type","transfer_id","transfer_date",
													  "modified_ON","product_name","transfer_mrp","mrp","commision",
													  "otf_amount","tax3_value","sender_debit_quantity","receiver_credit_quantity","payable_amount",
													  "net_payable_amount"};
			

			CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,	columnHeader,reportTopHeaders);
			CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);

			this.executeReaderWriterThread(reader,  csvWriter);
			} 
			catch (InterruptedException | ParseException | SQLException e) 
			{
				log.errorTrace(methodName, e);
			}
			finally{
				try{
			        if (pstmt!= null){
			        	pstmt.close();
			        }
			      }
			      catch (SQLException e){
			    	  log.error("An error occurred closing statement.", e);
			      }
				if(log.isDebugEnabled())
					log.debug(methodName, PretupsI.EXITED);
			}
			return filePath + fileName;
		}
/**
 * 	
 * @param usersReportModel
 * @param con
 * @return
 * @throws InterruptedException
 */
		private String prepareDataC2cRetWidTransferUnionStaff(UsersReportModel usersReportModel, Connection con)throws InterruptedException {
			final String methodName = "prepareDataC2cRetWidTransferUnionStaff";
			if(log.isDebugEnabled())
				log.debug(methodName, PretupsI.ENTERED);
			PreparedStatement pstmt = null;
			String filePath = "";
			String fileName = "";
			try{
				fileName = c2cTrfDetails + BTSLUtil.getFileNameStringFromDate(new Date())+ ".csv";
				filePath = path;
				File fileDir = new File(filePath);
				if (!fileDir.isDirectory()) {
					fileDir.mkdirs();
				}
				final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
				con.setAutoCommit(false);
				pstmt = channel2ChannelTransferRetWidRptQry.loadC2cRetWidTransferUnionStaffListQry(con,usersReportModel);
	        	pstmt.setFetchSize(1000);
				MutableBoolean mutableBoolean = new MutableBoolean(false);
				MutableBoolean filewritten = new MutableBoolean(false);
				StringBuilder sb =  new StringBuilder(1024);
				final String reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.report")).append(" ; ").append( "")
						.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.rptcode")).append(" : ").append( usersReportModel.getrptCode()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.transfersubtype")).append(" : ").append( usersReportModel.getTxnSubTypeName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.inout")).append(" : " ).append( usersReportModel.getTransferInOrOutName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.fromdate")).append(" : ").append( usersReportModel.getFromDate()).append(" ").append( usersReportModel.getFromTime()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.todate")).append(" : " ).append( usersReportModel.getToDate()).append(" ").append( usersReportModel.getToTime()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.zone")).append(" : " ).append( usersReportModel.getZoneName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.domain")).append(" : " ).append( usersReportModel.getDomainName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.searchcategory")).append(" : ").append( usersReportModel.getFromtransferCategoryName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.searchuser")).append(" : " ).append( usersReportModel.getUserName()).append(",")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.transactionusrcat")).append(" : ").append( usersReportModel.getTotransferCategoryName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.transactionusr")).append(" : " ).append( usersReportModel.getTouserName()).toString();
				sb.setLength(0);
				final String columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.fromUser")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.toUser")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.initiatorUser")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferid")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.source")).append(",")
											
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferSubType")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferDate")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.modifiedOn")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.senderCatName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.recCatCode")).append(",")
											
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.productName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferMRP")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.mrp")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.commission")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.tax3")).append(",")
											
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.senderDR")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.receiverCR")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.payableAmt")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.netPayableAmt")).toString();
				
				final String[] resultSetColumnName = {"from_user","to_user","initiator_user","transfer_id","SOURCE",
													  "transfer_sub_type","transfer_date","modified_ON","sender_category_name","receiver_category_name",
													  "product_name","transfer_mrp","mrp","commision","tax3_value",
													  "sender_debit_quantity","receiver_credit_quantity","payable_amount","net_payable_amount"};
			

			CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,	columnHeader,reportTopHeaders);
			CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);

			this.executeReaderWriterThread(reader,  csvWriter);
			} 
			catch (InterruptedException | ParseException | SQLException e) 
			{
				log.errorTrace(methodName, e);
			}
			finally{
				try{
			        if (pstmt!= null){
			        	pstmt.close();
			        }
			      }
			      catch (SQLException e){
			    	  log.error("An error occurred closing statement.", e);
			      }
				if(log.isDebugEnabled())
					log.debug(methodName, PretupsI.EXITED);
			}
			return filePath + fileName;
		} 
/**
 * 		
 * @param usersReportModel
 * @param con
 * @return
 * @throws InterruptedException
 */
		private String prepareDataC2cRetWidTransferList(UsersReportModel usersReportModel, Connection con) throws InterruptedException{
			final String methodName = "prepareDataC2cRetWidTransferList";
			if(log.isDebugEnabled())
				log.debug(methodName, PretupsI.ENTERED);
			PreparedStatement pstmt = null;
			String filePath = "";
			String fileName = "";
			try{
				fileName = c2cTrfDetails + BTSLUtil.getFileNameStringFromDate(new Date())+ ".csv";
				filePath = path;
				File fileDir = new File(filePath);
				if (!fileDir.isDirectory()) {
					fileDir.mkdirs();
				}
				final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
				con.setAutoCommit(false);
				pstmt = channel2ChannelTransferRetWidRptQry.loadC2cRetWidTransferListQry(con,usersReportModel);
	        	pstmt.setFetchSize(1000);
				MutableBoolean mutableBoolean = new MutableBoolean(false);
				MutableBoolean filewritten = new MutableBoolean(false);
				StringBuilder sb =  new StringBuilder(1024);
				final String reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.report")).append(" ; ").append( "")
						.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.rptcode")).append(" : ").append( usersReportModel.getrptCode()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.transfersubtype")).append(" : ").append( usersReportModel.getTxnSubTypeName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.inout")).append(" : " ).append( usersReportModel.getTransferInOrOutName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.fromdate")).append(" : ").append( usersReportModel.getFromDate()).append(" ").append( usersReportModel.getFromTime()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.todate")).append(" : " ).append( usersReportModel.getToDate()).append(" ").append( usersReportModel.getToTime()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.zone")).append(" : " ).append( usersReportModel.getZoneName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.domain")).append(" : " ).append( usersReportModel.getDomainName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.searchcategory")).append(" : ").append( usersReportModel.getFromtransferCategoryName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.searchuser")).append(" : " ).append( usersReportModel.getUserName()).append(",")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.transactionusrcat")).append(" : ").append( usersReportModel.getTotransferCategoryName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.transactionusr")).append(" : " ).append( usersReportModel.getTouserName()).toString();
				sb.setLength(0);
				final String columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.fromUser")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.fromMSISDN")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.fromUserGeo")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.fromOwnerGeo")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.senderCatName")).append(",")
											
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.toUser")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.toMSISDN")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.toUserGeo")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.toOwnerGeo")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.recCatCode")).append(",")
											
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.source")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferSubType")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferid")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferDate")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.modifiedOn")).append(",")
											
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.productName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferMRP")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.mrp")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.commission")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.tax3")).append(",")
											
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.senderDR")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.receiverCR")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.payableAmt")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.netPayableAmt")).toString();
				
				final String[] resultSetColumnName = {"from_user","from_msisdn","from_user_geo","from_owner_geo","sender_category_name",
													  "to_user","to_msisdn","to_user_geo","to_owner_geo","receiver_category_name",
													  "SOURCE","transfer_sub_type","transfer_id","transfer_date","modified_ON",
													  "product_name","transfer_mrp","mrp","commision","tax3_value",
													  "sender_debit_quantity","receiver_credit_quantity","payable_amount","net_payable_amount"};
			

			CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,	columnHeader,reportTopHeaders);
			CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);

			this.executeReaderWriterThread(reader,  csvWriter);
			} 
			catch (InterruptedException | ParseException | SQLException e) 
			{
				log.errorTrace(methodName, e);
			}
			finally{
				try{
			        if (pstmt!= null){
			        	pstmt.close();
			        }
			      }
			      catch (SQLException e){
			    	  log.error("An error occurred closing statement.", e);
			      }
				if(log.isDebugEnabled())
					log.debug(methodName, PretupsI.EXITED);
			}
			return filePath + fileName;
		} 
/**
 * 	
 * @param usersReportModel
 * @param con
 * @return
 * @throws InterruptedException
 */
		private String prepareDataC2cRetWidTransferStaff(UsersReportModel usersReportModel, Connection con)throws InterruptedException {
			final String methodName = "prepareDataC2cRetWidTransferStaff";
			if(log.isDebugEnabled())
				log.debug(methodName, PretupsI.ENTERED);
			PreparedStatement pstmt = null;
			String filePath = "";
			String fileName = "";
			try{
				fileName = c2cTrfDetails + BTSLUtil.getFileNameStringFromDate(new Date())+ ".csv";
				filePath = path;
				File fileDir = new File(filePath);
				if (!fileDir.isDirectory()) {
					fileDir.mkdirs();
				}
				final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
				con.setAutoCommit(false);
				pstmt = channel2ChannelTransferRetWidRptQry.loadC2cRetWidTransferStaffListQry(con,usersReportModel);
	        	pstmt.setFetchSize(1000);
				MutableBoolean mutableBoolean = new MutableBoolean(false);
				MutableBoolean filewritten = new MutableBoolean(false);
				StringBuilder sb =  new StringBuilder(1024);
				final String reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.report")).append(" ; ").append( "")
						.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.rptcode")).append(" : ").append( usersReportModel.getrptCode()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.transfersubtype")).append(" : ").append( usersReportModel.getTxnSubTypeName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.inout")).append(" : " ).append( usersReportModel.getTransferInOrOutName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.fromdate")).append(" : ").append( usersReportModel.getFromDate()).append(" ").append( usersReportModel.getFromTime()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.todate")).append(" : " ).append( usersReportModel.getToDate()).append(" ").append( usersReportModel.getToTime()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.zone")).append(" : " ).append( usersReportModel.getZoneName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.domain")).append(" : " ).append( usersReportModel.getDomainName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.searchcategory")).append(" : ").append( usersReportModel.getFromtransferCategoryName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.searchuser")).append(" : " ).append( usersReportModel.getUserName()).append(",")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.transactionusrcat")).append(" : ").append( usersReportModel.getTotransferCategoryName()).append(" , ")
                        .append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.transactionusr")).append(" : " ).append( usersReportModel.getTouserName()).toString();
				sb.setLength(0);
				final String columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.fromUser")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.toUser")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.initiatorUser")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferid")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.source")).append(",")
											
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferSubType")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferDate")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.modifiedOn")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.productName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.senderCatName")).append(",")
											
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.recCatCode")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.transferMRP")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.mrp")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.commission")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.tax3")).append(",")
											
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.senderDR")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.receiverCR")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.payableAmt")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.tableHeader.netPayableAmt")).toString();
				
				final String[] resultSetColumnName = {"from_user","to_user","initiator_user","transfer_id","SOURCE",
													  "transfer_sub_type","transfer_date","modified_ON","product_name","sender_category_name",
													  "receiver_category_name","transfer_mrp","mrp","commision","tax3_value",
													  "sender_debit_quantity","receiver_credit_quantity","payable_amount","net_payable_amount"};
			

			CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,	columnHeader,reportTopHeaders);
			CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);

			this.executeReaderWriterThread(reader,  csvWriter);
			} 
			catch (InterruptedException | ParseException | SQLException e) 
			{
				log.errorTrace(methodName, e);
			}
			finally{
				try{
			        if (pstmt!= null){
			        	pstmt.close();
			        }
			      }
			      catch (SQLException e){
			    	  log.error("An error occurred closing statement.", e);
			      }
				if(log.isDebugEnabled())
					log.debug(methodName, PretupsI.EXITED);
			}
			return filePath + fileName;
		}
		
	

	/**
	 * 
	 * @param usersReportModel
	 * @param con
	 * @return
	 */
	public String prepareAddCommDetailsOptUser(UsersReportModel usersReportModel, Connection con){
		String filePath = "";
		String methodName = "prepareAddCommDetailsOptUser";
		OperatorUtil operatorUtil = new OperatorUtil();
		try {
			if(operatorUtil.getNewDataAftrTbleMerging(BTSLUtil.getDateFromDateString(usersReportModel.getCurrentDate()),null))
				filePath = prepareDataAdditionalCommDetailsOperatorUser(usersReportModel, con);
			else
				filePath = prepareDataAdditionalCommDetailsOldOperatorUser(usersReportModel, con);
		} catch (BTSLBaseException | ParseException e) {
			if(log.isDebugEnabled())
				log.debug(methodName, e.getMessage());
			log.errorTrace(methodName, e);
		} 
		
		return filePath;
	}
	
	/**
	 * 
	 * @param usersReportModel
	 * @param con
	 * @return
	 */
	public String prepareAddCommDetailsChnlUser(UsersReportModel usersReportModel, Connection con){
		String filePath = "";
		String methodName = "prepareAddCommDetailsChnlUser";
		OperatorUtil operatorUtil = new OperatorUtil();
		try {
			if(operatorUtil.getNewDataAftrTbleMerging(BTSLUtil.getDateFromDateString(usersReportModel.getCurrentDate()),null))
				filePath = prepareDataAdditionalCommDetailsChannelUser(usersReportModel, con);
			else
				filePath = prepareDataAdditionalCommDetailsOldChannelUser(usersReportModel, con);
		} catch (BTSLBaseException | ParseException e) {
			if(log.isDebugEnabled())
				log.debug(methodName, e.getMessage());
			log.errorTrace(methodName, e);
		} 
		
		return filePath;
	}
	
	
	/**
	 * 
	 * @param usersReportModel
	 * @param con
	 * @return
	 */
	public String prepareDataAdditionalCommDetailsOperatorUser(UsersReportModel usersReportModel, Connection con)
	{
		final String methodName = "prepareDataAdditionalCommDetailsOperatorUser";
		PreparedStatement pstmt = null;
		if(log.isDebugEnabled())
		{
			log.debug(methodName, usersReportModel.getUserType());
		}
		String filePath = "";
		String fileName = "";


		try {
			fileName = addCommTrfDetails + BTSLUtil.getFileNameStringFromDate(new Date())
					+ ".csv";
			filePath = path;
			File fileDir = new File(filePath);
			if (!fileDir.isDirectory()) {
				fileDir.mkdirs();
			}
			final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
			con.setAutoCommit(false);
			pstmt = additionalCommissionDetailsReportQry.loadAdditionalCommisionDetailsOperatorQry(con, usersReportModel);

			pstmt.setFetchSize(1000);
			MutableBoolean mutableBoolean = new MutableBoolean(false);
			MutableBoolean filewritten = new MutableBoolean(false);
			final String columnHeader;
			final String reportTopHeaders;
			StringBuilder sb =  new StringBuilder(1024);
			reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.additionalCommissionDataDetail.label.report")).append(" ; ").append( "")
					.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.rptcode")).append(" : ").append( usersReportModel.getrptCode()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDetail.label.currentDate")).append(" : ").append( usersReportModel.getCurrentDate()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDetail.label.fromTime")).append(" : " ).append( usersReportModel.getFromTime()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDetail.label.toTime")).append(" : " ).append( usersReportModel.getToTime()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDetail.label.msisdn")).append(" : " ).append( usersReportModel.getMsisdn()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.zone")).append(" : " ).append( usersReportModel.getZoneName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.domain")).append(" : " ).append( usersReportModel.getDomainName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.searchcategory")).append(" : ").append( usersReportModel.getCategoryName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.searchuser")).append(" : " ).append( usersReportModel.getUserName()).toString();
			sb.setLength(0);		
			columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.transferId")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.adjustmentId")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.time")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.userName")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.msisdn")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.categoryName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.grphDomainName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.parentName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.parentMsisdn")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.parentCat")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.parentGeoName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.ownerName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.ownerMsisdn")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.ownerGeo")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.ownerCat")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.name")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.receiverMsisdn")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.commissionType")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.transferAmount")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.marginAmount")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.marginRate")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.otfTyep")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.otfRate")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.otfAmount")).toString();

			final String[] resultSetColumnName = {"transfer_id","adjustment_id","time","user_name","msisdn",
					"category_name","grph_domain_name","parent_name","parent_msisdn","parent_cat",
					"parent_geo_name","owner_user","owner_msisdn","owner_geo","owner_cat",
					"name","receiver_msisdn","commission_type","transfer_value","margin_amount","margin_rate","otf_type","otf_rate","otf_amount"};
			CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,	columnHeader,reportTopHeaders);
			CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);
			this.executeReaderWriterThread(reader,  csvWriter);
		}catch (InterruptedException | ParseException | SQLException | NoSuchMessageException e) 
		{
			log.errorTrace(methodName, e);
		}
		finally{
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
			if(log.isDebugEnabled())
				log.debug(methodName, PretupsI.EXITED);
		}
		return filePath + fileName;
	}
	
	
	/**
	 * 
	 * @param usersReportModel
	 * @param con
	 * @return
	 */
	public String prepareDataAdditionalCommDetailsOldOperatorUser(UsersReportModel usersReportModel, Connection con)
	{
		final String methodName = "prepareDataAdditionalCommDetailsOldOperatorUser";
		PreparedStatement pstmt = null;
		if(log.isDebugEnabled())
		{
			log.debug(methodName, usersReportModel.getUserType());
		}
		String filePath = "";
		String fileName = "";


		try {
			fileName = addCommTrfDetails + BTSLUtil.getFileNameStringFromDate(new Date())
					+ ".csv";
			filePath = path;
			File fileDir = new File(filePath);
			if (!fileDir.isDirectory()) {
				fileDir.mkdirs();
			}
			final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
			con.setAutoCommit(false);
			pstmt = additionalCommissionDetailsReportQry.loadAdditionalCommisionDetailsOperatorOldQry(con, usersReportModel);
			pstmt.setFetchSize(1000);
			MutableBoolean mutableBoolean = new MutableBoolean(false);
			MutableBoolean filewritten = new MutableBoolean(false);
			StringBuilder sb =  new StringBuilder(1024);
			final String reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.additionalCommissionDataDetail.label.report")).append(" ; ").append( "")
					.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.rptcode")).append(" : ").append( usersReportModel.getrptCode()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDetail.label.currentDate")).append(" : ").append( usersReportModel.getCurrentDate()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDetail.label.fromTime")).append(" : " ).append( usersReportModel.getFromTime()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDetail.label.toTime")).append(" : " ).append( usersReportModel.getToTime()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDetail.label.msisdn")).append(" : " ).append( usersReportModel.getMsisdn()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.zone")).append(" : " ).append( usersReportModel.getZoneName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.domain")).append(" : " ).append( usersReportModel.getDomainName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.searchcategory")).append(" : ").append( usersReportModel.getCategoryName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.searchuser")).append(" : " ).append( usersReportModel.getUserName()).toString();
			sb.setLength(0);
			final String columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.transferId")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.time")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.userName")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.msisdn")).append(",")

												.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.categoryName")).append(",")
												.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.grphDomainName")).append(",")
												.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.parentName")).append(",")
												.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.parentMsisdn")).append(",")
												.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.parentCat")).append(",")

												.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.parentGeoName")).append(",")
												.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.ownerName")).append(",")
												.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.ownerMsisdn")).append(",")
												.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.ownerGeo")).append(",")
												.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.ownerCat")).append(",")

												.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.name")).append(",")
												.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.receiverMsisdn")).append(",")
												.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.commissionType")).append(",")
												.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.transferAmount")).append(",")
												.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.marginAmount")).append(",")
												.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.marginRate")).toString();


			final String[] resultSetColumnName = {"transfer_id","time","user_name","msisdn",
					"category_name","grph_domain_name","parent_name","parent_msisdn","parent_cat",
					"parent_geo_name","owner_user","owner_msisdn","owner_geo","owner_cat",
					"name","receiver_msisdn","commission_type","transfer_value","margin_amount","margin_rate"};
			CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,	columnHeader,reportTopHeaders);
			CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);

			this.executeReaderWriterThread(reader,  csvWriter);
		}catch (InterruptedException | ParseException | SQLException e) 
		{
			log.errorTrace(methodName, e);
		}
		finally{
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
			if(log.isDebugEnabled())
				log.debug(methodName, PretupsI.EXITED);
		}
		return filePath + fileName;
	}
	
	
	/**
	 * 
	 * @param usersReportModel
	 * @param con
	 * @return
	 */
	public String prepareDataAdditionalCommDetailsChannelUser(UsersReportModel usersReportModel, Connection con)
	{
		final String methodName = "prepareDataAdditionalCommDetailsChannelUser";
		PreparedStatement pstmt = null;
		if(log.isDebugEnabled())
		{
			log.debug(methodName, usersReportModel.getUserType());
		}
		String filePath = "";
		String fileName = "";


		try {
			fileName = addCommTrfDetails + BTSLUtil.getFileNameStringFromDate(new Date())
					+ ".csv";
			filePath = path;
			File fileDir = new File(filePath);
			if (!fileDir.isDirectory()) {
				fileDir.mkdirs();
			}
			final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
			con.setAutoCommit(false);
			pstmt = additionalCommissionDetailsReportQry.loadAdditionalCommisionDetailsChannelQry(con, usersReportModel);
			StringBuilder sb =  new StringBuilder(1024);
			pstmt.setFetchSize(1000);
			MutableBoolean mutableBoolean = new MutableBoolean(false);
			MutableBoolean filewritten = new MutableBoolean(false);
			final String reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.additionalCommissionDataDetail.label.report")).append(" ; ").append( "")
					.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.rptcode")).append(" : ").append( usersReportModel.getrptCode()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDetail.label.currentDate")).append(" : ").append( usersReportModel.getCurrentDate()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDetail.label.fromTime")).append(" : " ).append( usersReportModel.getFromTime()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDetail.label.toTime")).append(" : " ).append( usersReportModel.getToTime()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDetail.label.msisdn")).append(" : " ).append( usersReportModel.getMsisdn()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.zone")).append(" : " ).append( usersReportModel.getZoneName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.domain")).append(" : " ).append( usersReportModel.getDomainName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.searchcategory")).append(" : ").append( usersReportModel.getCategoryName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.searchuser")).append(" : " ).append( usersReportModel.getUserName()).toString();
			sb.setLength(0);
			final String columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.adjustmentId")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.time")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.userName")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.msisdn")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.categoryName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.grphDomainName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.parentName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.parentMsisdn")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.parentCat")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.parentGeoName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.ownerName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.ownerMsisdn")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.ownerGeo")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.ownerCat")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.name")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.receiverMsisdn")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.transferAmount")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.marginAmount")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.marginRate")).toString();


			final String[] resultSetColumnName = {"adjustment_id","time","user_name","msisdn",
					"category_name","grph_domain_name","parent_name","parent_msisdn","parent_cat",
					"parent_geo_name","owner_user","owner_msisdn","owner_geo","owner_cat",
					"name","receiver_msisdn","transfer_value","margin_amount","margin_rate"};
			CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,	columnHeader,reportTopHeaders);
			CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);
			this.executeReaderWriterThread(reader,  csvWriter);
		} catch (InterruptedException | ParseException | SQLException | NoSuchMessageException e) 
		{
			log.errorTrace(methodName, e);
		}
		finally{
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
		}
		return filePath + fileName;
	}
	
	
	/**
	 * 
	 * @param usersReportModel
	 * @param con
	 * @return
	 */
	public String prepareDataAdditionalCommDetailsOldChannelUser(UsersReportModel usersReportModel, Connection con)
	{
		final String methodName = "prepareDataAdditionalCommDetailsOldChannelUser";
		PreparedStatement pstmt = null;
		if(log.isDebugEnabled())
		{
			log.debug(methodName, usersReportModel.getUserType());
		}
		String filePath = "";
		String fileName = "";


		try {
			fileName = addCommTrfDetails + BTSLUtil.getFileNameStringFromDate(new Date())
					+ ".csv";
			filePath = path;
			File fileDir = new File(filePath);
			if (!fileDir.isDirectory()) {
				fileDir.mkdirs();
			}
			final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
			con.setAutoCommit(false);
			pstmt = additionalCommissionDetailsReportQry.loadAdditionalCommisionDetailsChannelOldQry(con, usersReportModel);
			pstmt.setFetchSize(1000);
			MutableBoolean mutableBoolean = new MutableBoolean(false);
			MutableBoolean filewritten = new MutableBoolean(false);
			StringBuilder sb =  new StringBuilder(1024);
			final String reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.additionalCommissionDataDetail.label.report")).append(" ; ").append( "")
					.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.rptcode")).append(" : ").append( usersReportModel.getrptCode()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDetail.label.currentDate")).append(" : ").append( usersReportModel.getCurrentDate()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDetail.label.fromTime")).append(" : " ).append( usersReportModel.getFromTime()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDetail.label.toTime")).append(" : " ).append( usersReportModel.getToTime()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDetail.label.msisdn")).append(" : " ).append( usersReportModel.getMsisdn()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.zone")).append(" : " ).append( usersReportModel.getZoneName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.domain")).append(" : " ).append( usersReportModel.getDomainName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.searchcategory")).append(" : ").append( usersReportModel.getCategoryName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.searchuser")).append(" : " ).append( usersReportModel.getUserName()).toString();
			sb.setLength(0);
			final String columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.adjustmentId")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.time")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.userName")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.msisdn")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.categoryName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.grphDomainName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.parentName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.parentMsisdn")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.parentCat")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.parentGeoName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.ownerName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.ownerMsisdn")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.ownerGeo")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.ownerCat")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.name")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.receiverMsisdn")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.transferAmount")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.marginAmount")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataDetail.tableHeader.marginRate")).toString();


			final String[] resultSetColumnName = {"adjustment_id","time","user_name","msisdn",
					"category_name","grph_domain_name","parent_name","parent_msisdn","parent_cat",
					"parent_geo_name","owner_user","owner_msisdn","owner_geo","owner_cat",
					"name","receiver_msisdn","transfer_value","margin_amount","margin_rate"};
			CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,	columnHeader,reportTopHeaders);
			CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);
			this.executeReaderWriterThread(reader,  csvWriter);
		} catch (InterruptedException | ParseException | SQLException | NoSuchMessageException e) 
		{
			log.errorTrace(methodName, e);
		}
		finally{
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
		}
		return filePath + fileName;
	}
	
	@SuppressWarnings("resource")
	public String prepareDataO2CTransferDetailsForChannelUser(UsersReportModel usersReportModel, Connection con)
	 {
		final String methodName = "prepareDataO2CTransferDetailsForChannelUser";
		 PreparedStatement pstmt = null;
		if(log.isDebugEnabled())
		{
			log.debug(methodName, usersReportModel.getUserType());
		}
		String filePath = "";
		String fileName = "";
		
	        try {
	        	fileName = o2cTrfDetails + BTSLUtil.getFileNameStringFromDate(new Date())
						+ ".csv";
				filePath = path;
				File fileDir = new File(filePath);
				if (!fileDir.isDirectory()) {
					fileDir.mkdirs();
				}
				final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
				con.setAutoCommit(false);
				
				
				pstmt=o2cTransferDetailsRptQry.loado2cTransferDetailsChannelUserReportQry(usersReportModel,con);
				pstmt.setFetchSize(1000);
	        	
			StringBuilder sb =  new StringBuilder(1024);	
			MutableBoolean mutableBoolean = new MutableBoolean(false);
			MutableBoolean filewritten = new MutableBoolean(false);
			final String reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.o2ctransferdetails.label.report")).append(" ; ").append( "")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.o2ctransferdetails.label.networkname")).append(" : ").append( usersReportModel.getNetworkName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.o2ctransferdetails.label.zone")).append(" : ").append( usersReportModel.getZoneName()).append(" , ")
                    .append(PretupsRestUtil.getMessageString("pretups.c2s.reports.o2ctransferdetails.label.user")).append(" : " ).append( usersReportModel.getUserName()).append(" , ")
                    .append(PretupsRestUtil.getMessageString("pretups.c2s.reports.o2ctransferdetails.label.trfcategory")).append(" : ").append( usersReportModel.getTransferCategoryName()).append(" , ")
                    .append(PretupsRestUtil.getMessageString("pretups.c2s.reports.o2ctransferdetails.label.domain")).append(" : " ).append( usersReportModel.getDomainName()).append(" , ")
                    .append(PretupsRestUtil.getMessageString("pretups.c2s.reports.o2ctransferdetails.label.category")).append(" : " ).append( usersReportModel.getFromtransferCategoryName()).append(" , ")
                    
                    .append(PretupsRestUtil.getMessageString("pretups.c2s.reports.o2ctransferdetails.label.fromdate")).append(" : ").append( usersReportModel.getFromDate()).append(" ").append(usersReportModel.getFromTime()).append(", ")
                    .append(PretupsRestUtil.getMessageString("pretups.c2s.reports.o2ctransferdetails.label.todate")).append(" : " ).append( usersReportModel.getToDate()).append(" ").append(usersReportModel.getToTime()).toString();
			//final String columnHeader = "From User,To User,Trf.No,Trf.Category,Trf.Sub-Type,Trf.Date,Transfer Time,Product,External Trf.No,External Trf.Date,Transaction Mode,Requested	Quantity,Approved Quantity,Tax1,Tax2,Comm.,CBC,Reciever Quantity,Payable Amount,Net Payable Amount";
			
			sb.setLength(0);
			final String columnHeader =sb.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.fromUser")).append(",") 
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.toUser")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.transferid")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.transferCategory")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.transferSubType")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.transferDate")).append(",")
					
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.modifiedOn")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.productName")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.txn_number")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.txn_date")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.transactionMode")).append(",")
					
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.transactionName")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.approvedAmount")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.tax1")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.tax2")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.commissionValue")).append(",")
					
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.otfAmount")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.recieverQuantity")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.payableAmount")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.netPayableAmount")).toString();
			final String[] resultSetColumnName = {"from_user","to_user","transfer_id","transfer_category","transfer_sub_type","transfer_date","modified_on","product_name","ext_txn_no","ext_txn_date","transaction_mode","required_quantity","approved_quantity","tax1_value","tax2_value","commission_value","otf_amount","receiver_credit_quantity","payable_amount","net_payable_amount"};
			

			CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,
					columnHeader,reportTopHeaders);
			CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);

			this.executeReaderWriterThread(reader,  csvWriter);
		} 
		catch (InterruptedException | ParseException | SQLException e) 
		{
			log.errorTrace(methodName, e);
		}
		finally{
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
		}
		return filePath + fileName;

	}
	@SuppressWarnings("resource")
	public String prepareDataO2CTransferDetailsForOperatorUsers(UsersReportModel usersReportModel, Connection con)
	 {
		final String methodName = "prepareDataO2CTransferDetailsForOperatorUsers";
		 PreparedStatement pstmt = null;
		if(log.isDebugEnabled())
		{
			log.debug(methodName, usersReportModel.getUserType());
		}
		String filePath = "";
		String fileName = "";
		
		 
	        try {
	        	fileName = o2cTrfDetails + BTSLUtil.getFileNameStringFromDate(new Date())
						+ ".csv";
				filePath = path;
				File fileDir = new File(filePath);
				if (!fileDir.isDirectory()) {
					fileDir.mkdirs();
				}
				final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
				con.setAutoCommit(false);
				pstmt= o2cTransferDetailsRptQry.loado2cTransferDetailsReportQry(usersReportModel,con);
				pstmt.setFetchSize(1000);
	        	MutableBoolean mutableBoolean = new MutableBoolean(false);
			MutableBoolean filewritten = new MutableBoolean(false);
			StringBuilder sb =  new StringBuilder(1024);
			final String reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.o2ctransferdetails.label.report")).append(" ; ").append( "")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.o2ctransferdetails.label.networkname")).append(" : ").append( usersReportModel.getNetworkName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.o2ctransferdetails.label.zone")).append(" : ").append( usersReportModel.getZoneName()).append(" , ")
                    .append(PretupsRestUtil.getMessageString("pretups.c2s.reports.o2ctransferdetails.label.user")).append(" : " ).append( usersReportModel.getUserName()).append(" , ")
                    .append(PretupsRestUtil.getMessageString("pretups.c2s.reports.o2ctransferdetails.label.trfcategory")).append(" : ").append( usersReportModel.getTransferCategoryName()).append(" , ")
                    .append(PretupsRestUtil.getMessageString("pretups.c2s.reports.o2ctransferdetails.label.domain")).append(" : " ).append( usersReportModel.getDomainName()).append(" , ")
                    .append(PretupsRestUtil.getMessageString("pretups.c2s.reports.o2ctransferdetails.label.category")).append(" : " ).append( usersReportModel.getFromtransferCategoryName()).append(" , ")
                    
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.o2ctransferdetails.label.fromdate")).append(" : ").append( usersReportModel.getFromDate()).append(" ").append(usersReportModel.getFromTime()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.o2ctransferdetails.label.todate")).append(" : " ).append( usersReportModel.getToDate()).append(" ").append(usersReportModel.getToTime()).toString();
			sb.setLength(0);
			//final String columnHeader = "From User,To User,Mobile No,Trf.No,Trf.Category,Trf.Sub-Type,Trf.Date,Modified On,Request Gateway Type,Pmt Inst Type,Pmt Inst Date,Pmt Inst No,Product,External Trf.No,External Trf.Date,Transaction Mode,Requested Quantity,Approved Quantity	,Comm.,CBC,Reciever Quantity,Payable Amount,Net Payable Amount";
			final String columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.fromUser")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.toUser")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.mobileNumber")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.transferid")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.transferCategory")).append(",")
					
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.transferSubType")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.transferDate")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.modifiedOn")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.requestGatewayType")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.paymentInstType")).append(",")
					
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.paymentInstDate")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.paymentInstNumber")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.productName")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.txn_number")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.txn_date")).append(",")
					
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.transactionMode")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.transactionName")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.approvedAmount")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.commissionValue")).append(",")
					
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.otfAmount")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.recieverQuantity")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.payableAmount")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.o2c.reports.c2ctransferretwid.tableHeader.netPayableAmount")).toString();
			final String[] resultSetColumnName = {"from_user","to_user","to_msisdn","transfer_id","transfer_category","transfer_sub_type","transfer_date","modified_on","REQUEST_GATEWAY_TYPE","pmt_inst_type","pmt_inst_date","pmt_inst_no","product_name","ext_txn_no","ext_txn_date","transaction_mode","required_quantity","approved_quantity","commission_value","otf_amount","receiver_credit_quantity","payable_amount","net_payable_amount"};
			

			CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,
					columnHeader,reportTopHeaders);
			CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);

			this.executeReaderWriterThread(reader,  csvWriter);
		} 
		catch (InterruptedException | ParseException | SQLException e) 
		{
			log.errorTrace(methodName, e);
		}
		finally{
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
		}
		return filePath + fileName;

	}
	@SuppressWarnings("resource")
	public String prepareDatazeroBalSumm(UsersReportModel usersReportModel,
			Connection con) {
		final String methodName = "prepareDatazeroBalSumm";

		SimpleDateFormat timeStampFormat;
		Timestamp fromDateTimeValue = null;
		Timestamp toDateTimeValue = null;
		Date fromDateParse;
		Date toDateParse;
		Date fromDate = null;
		Date toDate = null;

		if (!BTSLUtil.isNullString(usersReportModel.getFromDate())) {
			try {
				fromDate = BTSLUtil.getDateFromDateString(
						usersReportModel.getFromDate() + " 00:00:00",
						PretupsI.TIMESTAMP_DATESPACEHHMMSS);
			} catch (ParseException e) {
				log.errorTrace(methodName, e);
			}
			usersReportModel.setFromDateTime(fromDate);
			timeStampFormat = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);
			try {
				fromDateParse = timeStampFormat.parse(BTSLUtil
						.getDateTimeStringFromDate(usersReportModel
								.getFromDateTime()));
				fromDateTimeValue = new Timestamp(fromDateParse.getTime());
			} catch (ParseException e) {
				log.errorTrace(methodName, e);
			}

		}
		if (!BTSLUtil.isNullString(usersReportModel.getToDate())) {
			try {
				toDate = BTSLUtil.getDateFromDateString(
						usersReportModel.getToDate() + " 23:59:00",
						PretupsI.TIMESTAMP_DATESPACEHHMMSS);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				log.errorTrace(methodName, e);
			}
			usersReportModel.setToDateTime(toDate);
			timeStampFormat = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);
			try {
				toDateParse = timeStampFormat.parse(BTSLUtil
						.getDateTimeStringFromDate(usersReportModel
								.getToDateTime()));
				toDateTimeValue = new Timestamp(toDateParse.getTime());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				log.errorTrace(methodName, e);
			}

		}

		PreparedStatement pstmt = null;
		if (log.isDebugEnabled()) {
			log.debug(methodName, "usersReportModel.getUserType()="+usersReportModel.getUserType());
		}
		String filePath = "";
		String fileName = "";

		try {
			fileName = "zeroBalSumm"
					+ BTSLUtil.getFileNameStringFromDate(new Date()) + ".csv";
			
			filePath = path;
			File fileDir = new File(filePath);
			if (!fileDir.isDirectory()) {
				fileDir.mkdirs();
			}

			
			final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
			con.setAutoCommit(false);

			if (TypesI.OPERATOR_USER_TYPE.equalsIgnoreCase(usersReportModel
					.getUserType())) {
				pstmt = userZeroBalanceCounterSummaryQry
						.loadUserBalanceReportQry(usersReportModel, con,
								fromDateTimeValue, toDateTimeValue);
				MutableBoolean mutableBoolean = new MutableBoolean(false);
				MutableBoolean filewritten = new MutableBoolean(false);
				StringBuilder sb =  new StringBuilder(1024);
				final String reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.header.heading")).append(" ; ").append("")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.header.rptcode")).append(" : ")
						.append( usersReportModel.getrptCode()).append(" , ")						
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.header.fromdate")).append(" : ")
						.append( usersReportModel.getFromDate()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.header.todate")).append(" : ")
						.append( usersReportModel.getToDate()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.header.zone")).append(" : ")
						.append( usersReportModel.getZoneName()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.header.domain")).append(" : ")
						.append( usersReportModel.getDomainName()).toString();
				sb.setLength(0);		
				final String columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.tableHeader.toUser")).append(",")
						
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.tableHeader.Mobileno")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.tableHeader.Status")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.tableHeader.CategoryName")).append(",")
						
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.tableHeader.ParentName")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.tableHeader.ParentMobileNo")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.tableHeader.OwnerName")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.tableHeader.OwnerMobileNo")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.tableHeader.EntryDate")).append(",")
						
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.tableHeader.ProductName")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.tableHeader.RecordType")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.tableHeader.ThresholdCount")).toString();
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				
				final String[] resultSetColumnName = { "user_name", "msisdn",
						"user_status", "category_name", "parent_name",
						"parent_msisdn", "owner_name", "owner_msisdn",
						"entry_date", "product_name", "record_type",
						"threshold_count" };

				CSVReportWriter csvWriter = new CSVReportWriter(queue,
						filePath, fileName, mutableBoolean, filewritten,
						columnHeader, reportTopHeaders);
				CSVReportReader reader = new CSVReportReader(queue, pstmt,
						mutableBoolean, resultSetColumnName);

				this.executeReaderWriterThread(reader, csvWriter);

			}
			
			if (TypesI.CHANNEL_USER_TYPE.equalsIgnoreCase(usersReportModel
					.getUserType())) {
				pstmt = userZeroBalanceCounterSummaryQry
						.loadzeroBalSummChannelUserReportQry(usersReportModel,
								con, fromDateTimeValue, toDateTimeValue);

				MutableBoolean mutableBoolean = new MutableBoolean(false);
				MutableBoolean filewritten = new MutableBoolean(false);
				StringBuilder sb =  new StringBuilder(1024);
				final String reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.header.heading")).append(" ; ").append("")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.header.rptcode")).append(" : ")
						.append( usersReportModel.getrptCode()).append(" , ")						
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.header.fromdate")).append(" : ")
						.append( usersReportModel.getFromDate()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.header.todate")).append(" : ")
						.append( usersReportModel.getToDate()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.header.zone")).append(" : ")
						.append( usersReportModel.getZoneName()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.header.domain")).append(" : ")
						.append( usersReportModel.getDomainName()).toString();
				sb.setLength(0);			
			//	final String columnHeader = "From User,Mobile no,Status,Category Name,Entry Date,Product Name,Record Type,Threshold Count";
				final String columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.tableHeader.toUser")).append(",")
						
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.tableHeader.Mobileno")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.tableHeader.Status")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.tableHeader.CategoryName")).append(",")
						
						
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.tableHeader.EntryDate")).append(",")
						
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.tableHeader.ProductName")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.tableHeader.RecordType")).append(",")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.tableHeader.ThresholdCount")).toString();
				final String[] resultSetColumnName = { "user_name", "msisdn",
						"user_status", "category_name", "entry_date",
						"product_name", "record_type", "threshold_count" };

				CSVReportWriter csvWriter = new CSVReportWriter(queue,
						filePath, fileName, mutableBoolean, filewritten,
						columnHeader, reportTopHeaders);
				CSVReportReader reader = new CSVReportReader(queue, pstmt,
						mutableBoolean, resultSetColumnName);

				this.executeReaderWriterThread(reader, csvWriter);
			}
		} catch (InterruptedException | ParseException | SQLException e) {
			log.errorTrace(methodName, e);
		} finally {
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
		}
		return filePath + fileName;

	}			
	
	
	/**
	 * @param usersReportModel
	 * @param con
	 * @return
	 */

	public String prepareDataUserBalMovSumForChannelUser(UsersReportModel usersReportModel, Connection con)
	 {
		final String methodName = "prepareDataUserBalMovSumForChannelUser";
		 PreparedStatement pstmt = null;
		if(log.isDebugEnabled())
		{
			log.debug(methodName, usersReportModel.getUserType());
		}
		String filePath = "";
		String fileName = "";
		
		 
	        try {
	        	fileName = userBalMovSum + BTSLUtil.getFileNameStringFromDate(new Date())
						+ ".csv";
				filePath = path;
				File fileDir = new File(filePath);
				if (!fileDir.isDirectory()) {
					fileDir.mkdirs();
				}
				final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
				con.setAutoCommit(false);
				pstmt= userDailyBalanceMovementRptQuery.dailyBalanceMovementChnlUserRpt(usersReportModel, con);
				pstmt.setFetchSize(1000);
	        	MutableBoolean mutableBoolean = new MutableBoolean(false);
			MutableBoolean filewritten = new MutableBoolean(false);
			StringBuilder sb =  new StringBuilder(1024);
			final String reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.label.report")).append(" ; ").append( "")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.label.networkname")).append(" : ").append( usersReportModel.getNetworkName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.label.zone")).append(" : ").append( usersReportModel.getZoneName()).append(" , ")
                    .append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.label.domain")).append(" : " ).append( usersReportModel.getDomainName()).append(" , ")
                    .append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.label.currentdate")).append(" : ").append( BTSLUtil.getDateStringFromDate(new java.util.Date())).append(" , ")
                    .append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.label.category")).append(" : " ).append( usersReportModel.getParentCategoryCode()).append(" , ")
                    .append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.label.user")).append(" : " ).append( usersReportModel.getUserName()).append(" , ")
                    
                    .append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.label.fromdate")).append(" : ").append( usersReportModel.getFromDate()).append(" , ")
                    .append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.label.todate")).append(" : " ).append( usersReportModel.getToDate()).toString();
			
			sb.setLength(0);
			final String columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.transferDate")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.userName")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.msisdn")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.externalCode")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.graphDomainName")).append(",")
					
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.parentName")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.parentMsisdn")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.grandName")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.grandMsisdn")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.grandGeo")).append(",")
					
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.ownerGeo")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.productName")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.openingBalance")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.stockBought")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.stockReturn")).append(",")
					
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.channelTransfer")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.channelReturn")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.c2sTransfer")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.closingBalance")).append(",")
					
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.reconStatus")).toString();
			final String[] resultSetColumnName = {"transfer_date","user_name","msisdn","external_code","grph_domain_name","parent_name","parent_msisdn","grand_name","grand_msisdn","grand_geo","owner_geo","product_name","opening_balance","o2c_transfer_in_amount","stock_return","channel_transfer","channel_return","c2s_transfer_out_amount","closing_balance","recon_status"};
			

			CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,
					columnHeader,reportTopHeaders);
			CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);

			this.executeReaderWriterThread(reader,  csvWriter);
		} 
		catch (InterruptedException | ParseException | SQLException e) 
		{
			log.errorTrace(methodName, e);
		}
		finally{
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
		}
		return filePath + fileName;

	}
			
	/**
	 * @param usersReportModel
	 * @param con
	 * @return
	 */
	
	public String prepareDataUserBalMovSumForOperatorUser(UsersReportModel usersReportModel, Connection con)
	 {
		final String methodName = "prepareDataUserBalMovSumForOperatorUser";
		 PreparedStatement pstmt = null;
		if(log.isDebugEnabled())
		{
			log.debug(methodName, usersReportModel.getUserType());
		}
		String filePath = "";
		String fileName = "";
		
		 
	        try {
	        	fileName = userBalMovSum + BTSLUtil.getFileNameStringFromDate(new Date())
						+ ".csv";
				filePath = path;
				File fileDir = new File(filePath);
				if (!fileDir.isDirectory()) {
					fileDir.mkdirs();
				}
				final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
				con.setAutoCommit(false);
				pstmt= userDailyBalanceMovementRptQuery.dailyBalanceMovementOptRpt(usersReportModel, con);
				pstmt.setFetchSize(1000);
	        	MutableBoolean mutableBoolean = new MutableBoolean(false);
			MutableBoolean filewritten = new MutableBoolean(false);
			StringBuilder sb =  new StringBuilder(1024);
			final String reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.label.report")).append(" ; ").append( "")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.label.networkname")).append(" : ").append( usersReportModel.getNetworkName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.label.zone")).append(" : ").append( usersReportModel.getZoneName()).append(" , ")
                    .append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.label.domain")).append(" : " ).append( usersReportModel.getDomainName()).append(" , ")
                    .append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.label.currentdate")).append(" : ").append( BTSLUtil.getDateStringFromDate(new java.util.Date())).append(" , ")
                    .append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.label.category")).append(" : " ).append( usersReportModel.getParentCategoryCode()).append(" , ")
                    .append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.label.user")).append(" : " ).append( usersReportModel.getUserName()).append(" , ")
                    
                    .append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.label.fromdate")).append(" : ").append( usersReportModel.getFromDate()).append(" , ")
                    .append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.label.todate")).append(" : " ).append( usersReportModel.getToDate()).toString();
			
			sb.setLength(0);
			final String columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.transferDate")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.userName")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.msisdn")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.externalCode")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.graphDomainName")).append(",")
					
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.pName")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.pMsisdn")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.ownerName")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.ownerMsisdn")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.productName")).append(",")
					
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.openingBalance")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.stockBought")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.stockReturn")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.channelTransfer")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.channelReturn")).append(",")
					
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.netBalance")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.netLifting")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.c2sTransfer")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.reconStatus")).append(",")
					
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.dailyUserBalMovement.tableHeader.closingBalance")).toString();
			final String[] resultSetColumnName = {"transfer_date","user_name","msisdn","external_code","grph_domain_name","p_name","p_msisdn","owner_name","owner_msisdn","product_name","opening_balance","stock_bought","stock_return","channel_transfer","channel_return","net_balance","net_lifting","c2s_transfer","recon_value","closing_balance"};
			

			CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,
					columnHeader,reportTopHeaders);
			CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);

			this.executeReaderWriterThread(reader,  csvWriter);
		} 
		catch (InterruptedException | ParseException | SQLException e) 
		{
			log.errorTrace(methodName, e);
		}
		finally{
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
		}
		return filePath + fileName;

	}
	
	
	
	/**
	 * 
	 * @param usersReportModel
	 * @param con
	 * @return
	 */
	public String prepareAddCommSummaryOptDaily(UsersReportModel usersReportModel, Connection con)
	{
		final String methodName = "prepareAddCommSummaryOptDaily";
		PreparedStatement pstmt = null;
		if(log.isDebugEnabled())
		{
			log.debug(methodName, usersReportModel.getUserType());
		}
		String filePath = "";
		String fileName = "";


		try {
			fileName = addCommTrfSummary + BTSLUtil.getFileNameStringFromDate(new Date())
					+ ".csv";
			filePath = path;
			File fileDir = new File(filePath);
			if (!fileDir.isDirectory()) {
				fileDir.mkdirs();
			}
			final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
			con.setAutoCommit(false);
			pstmt = additionalCommissionSummaryReportQry.loadAdditionalCommisionDetailsOperatorDailyQry(con, usersReportModel);

			pstmt.setFetchSize(1000);
			MutableBoolean mutableBoolean = new MutableBoolean(false);
			MutableBoolean filewritten = new MutableBoolean(false);
			final String columnHeader;
			final String reportTopHeaders;
			StringBuilder sb =  new StringBuilder(1024);
			reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.additionalCommissionDataDetail.label.report")).append(" ; ").append( "")
					.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.rptcode")).append(" : ").append( usersReportModel.getrptCode()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.zone")).append(" : ").append( usersReportModel.getZoneName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.domain")).append(" : " ).append( usersReportModel.getDomainName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.category")).append(" : " ).append( usersReportModel.getCategoryName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.serviceType")).append(" : " ).append( usersReportModel.getServiceTypeName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.reportType")).append(" : " ).append( usersReportModel.getRadioNetCode()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.fromDate")).append(" : " ).append( usersReportModel.getFromDate()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.toDate")).append(" : " ).append( usersReportModel.getToDate()).toString();
			
			sb.setLength(0);
			columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.transferdate")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.loginId")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.userName")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.msisdn")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.categoryName")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.grphDomainName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.parentName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.parentMsisdn")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.parentCat")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.parentGeoName")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.ownerName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.ownerMsisdn")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.ownerGeo")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.ownerCat")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.serviceTypeName")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.selectorName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.transactionCount")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.differentialAmount")).toString();
											
											

			final String[] resultSetColumnName = {"trans_date","login_id","user_name","msisdn","category_name",
					"grph_domain_name","parent_name","parent_msisdn","parent_cat","parent_geo",
					"owner_name","owner_msisdn","owner_category","owner_geo","service_type_name",
					"selector_name","transaction_count","differential_amount"};
			CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,	columnHeader,reportTopHeaders);
			CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);
			this.executeReaderWriterThread(reader,  csvWriter);
		}catch (InterruptedException | ParseException | SQLException | NoSuchMessageException e) 
		{
			log.errorTrace(methodName, e);
		}
		finally{
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
			if(log.isDebugEnabled())
				log.debug(methodName, PretupsI.EXITED);
		}
		return filePath + fileName;
	}
	
	/**
	 * 
	 * @param usersReportModel
	 * @param con
	 * @return
	 */
	public String prepareAddCommSummaryOptMonthly(UsersReportModel usersReportModel, Connection con)
	{
		final String methodName = "prepareAddCommSummaryOptMonthly";
		PreparedStatement pstmt = null;
		if(log.isDebugEnabled())
		{
			log.debug(methodName, usersReportModel.getUserType());
		}
		String filePath = "";
		String fileName = "";


		try {
			fileName = addCommTrfSummary + BTSLUtil.getFileNameStringFromDate(new Date())
					+ ".csv";
			filePath = path;
			File fileDir = new File(filePath);
			if (!fileDir.isDirectory()) {
				fileDir.mkdirs();
			}
			final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
			con.setAutoCommit(false);
			pstmt = additionalCommissionSummaryReportQry.loadAdditionalCommisionDetailsOperatorMonthlyQry(con, usersReportModel);

			pstmt.setFetchSize(1000);
			MutableBoolean mutableBoolean = new MutableBoolean(false);
			MutableBoolean filewritten = new MutableBoolean(false);
			final String columnHeader;
			final String reportTopHeaders;
			StringBuilder sb =  new StringBuilder(1024);
			reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.additionalCommissionDataDetail.label.report")).append(" ; ").append( "")
					.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.rptcode")).append(" : ").append( usersReportModel.getrptCode()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.zone")).append(" : ").append( usersReportModel.getZoneName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.domain")).append(" : " ).append( usersReportModel.getDomainName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.category")).append(" : " ).append( usersReportModel.getCategoryName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.serviceType")).append(" : " ).append( usersReportModel.getServiceTypeName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.reportType")).append(" : " ).append( usersReportModel.getRadioNetCode()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.fromMonth")).append(" : " ).append( usersReportModel.getFromMonth()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.toMonth")).append(" : " ).append( usersReportModel.getToMonth()).toString();
			
			sb.setLength(0);
			columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.transferdate")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.loginId")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.userName")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.msisdn")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.categoryName")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.grphDomainName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.parentName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.parentMsisdn")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.parentCat")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.parentGeoName")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.ownerName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.ownerMsisdn")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.ownerGeo")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.ownerCat")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.serviceTypeName")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.selectorName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.transactionCount")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.differentialAmount")).toString();
											
											

			final String[] resultSetColumnName = {"trans_date","login_id","user_name","msisdn","category_name",
					"grph_domain_name","parent_name","parent_msisdn","parent_cat","parent_geo",
					"owner_name","owner_msisdn","owner_category","owner_geo","service_type_name",
					"selector_name","transaction_count","differential_amount"};
			CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,	columnHeader,reportTopHeaders);
			CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);
			this.executeReaderWriterThread(reader,  csvWriter);
		}catch (InterruptedException | ParseException | SQLException | NoSuchMessageException e) 
		{
			log.errorTrace(methodName, e);
		}
		finally{
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
			if(log.isDebugEnabled())
				log.debug(methodName, PretupsI.EXITED);
		}
		return filePath + fileName;
	}
	
	
	/**
	 * 
	 * @param usersReportModel
	 * @param con
	 * @return
	 */
	public String prepareAddCommSummaryChnlDaily(UsersReportModel usersReportModel, Connection con)
	{
		final String methodName = "prepareAddCommSummaryChnlDaily";
		PreparedStatement pstmt = null;
		if(log.isDebugEnabled())
		{
			log.debug(methodName, usersReportModel.getUserType());
		}
		String filePath = "";
		String fileName = "";


		try {
			fileName = addCommTrfSummary + BTSLUtil.getFileNameStringFromDate(new Date())
					+ ".csv";
			filePath = path;
			File fileDir = new File(filePath);
			if (!fileDir.isDirectory()) {
				fileDir.mkdirs();
			}
			final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
			con.setAutoCommit(false);
			pstmt = additionalCommissionSummaryReportQry.loadAdditionalCommisionDetailsChannelDailyQry(con, usersReportModel);

			pstmt.setFetchSize(1000);
			MutableBoolean mutableBoolean = new MutableBoolean(false);
			MutableBoolean filewritten = new MutableBoolean(false);
			final String columnHeader;
			final String reportTopHeaders;
			StringBuilder sb =  new StringBuilder(1024);
			reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.additionalCommissionDataDetail.label.report")).append(" ; ").append( "")
					.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.rptcode")).append(" : ").append( usersReportModel.getrptCode()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.zone")).append(" : ").append( usersReportModel.getZoneName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.domain")).append(" : " ).append( usersReportModel.getDomainName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.category")).append(" : " ).append( usersReportModel.getCategoryName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.serviceType")).append(" : " ).append( usersReportModel.getServiceTypeName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.reportType")).append(" : " ).append( usersReportModel.getRadioNetCode()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.fromDate")).append(" : " ).append( usersReportModel.getFromDate()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.toDate")).append(" : " ).append( usersReportModel.getToDate()).toString();
			
			sb.setLength(0);
			columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.transferdate")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.loginId")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.userName")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.msisdn")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.categoryName")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.grphDomainName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.parentName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.parentMsisdn")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.parentCat")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.parentGeoName")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.ownerName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.ownerMsisdn")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.ownerGeo")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.ownerCat")).append(",")
											
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.grandName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.grandMsisdn")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.grandCategory")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.grandGeo")).append(",")
											
											
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.serviceTypeName")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.selectorName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.transactionCount")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.differentialAmount")).toString();
											
											

			final String[] resultSetColumnName = {"trans_date","login_id","user_name","msisdn","category_name",
					"grph_domain_name","parent_name","parent_msisdn","parent_cat","parent_geo",
					"owner_name","owner_msisdn","owner_category","owner_geo","grand_name","grand_msisdn","grand_category","grand_geo_domain",
					"service_type_name","selector_name","transaction_count","differential_amount"};
			CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,	columnHeader,reportTopHeaders);
			CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);
			this.executeReaderWriterThread(reader,  csvWriter);
		}catch (InterruptedException | ParseException | SQLException | NoSuchMessageException e) 
		{
			log.errorTrace(methodName, e);
		}
		finally{
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
			if(log.isDebugEnabled())
				log.debug(methodName, PretupsI.EXITED);
		}
		return filePath + fileName;
	}
	
	
	/**
	 * 
	 * @param usersReportModel
	 * @param con
	 * @return
	 */
	public String prepareAddCommSummaryChnlMonthly(UsersReportModel usersReportModel, Connection con)
	{
		final String methodName = "prepareAddCommSummaryChnlMonthly";
		PreparedStatement pstmt = null;
		if(log.isDebugEnabled())
		{
			log.debug(methodName, usersReportModel.getUserType());
		}
		String filePath = "";
		String fileName = "";


		try {
			fileName = addCommTrfSummary + BTSLUtil.getFileNameStringFromDate(new Date())
					+ ".csv";
			filePath = path;
			File fileDir = new File(filePath);
			if (!fileDir.isDirectory()) {
				fileDir.mkdirs();
			}
			final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
			con.setAutoCommit(false);
			pstmt = additionalCommissionSummaryReportQry.loadAdditionalCommisionDetailsChannelMonthlyQry(con, usersReportModel);

			pstmt.setFetchSize(1000);
			MutableBoolean mutableBoolean = new MutableBoolean(false);
			MutableBoolean filewritten = new MutableBoolean(false);
			final String columnHeader;
			final String reportTopHeaders;
			StringBuilder sb =  new StringBuilder(1024);
			reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.additionalCommissionDataDetail.label.report")).append(" ; ").append( "")
					.append(PretupsRestUtil.getMessageString("pretups.c2c.reports.c2ctransferretwid.label.rptcode")).append(" : ").append( usersReportModel.getrptCode()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.zone")).append(" : ").append( usersReportModel.getZoneName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.domain")).append(" : " ).append( usersReportModel.getDomainName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.category")).append(" : " ).append( usersReportModel.getCategoryName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.serviceType")).append(" : " ).append( usersReportModel.getServiceTypeName()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.reportType")).append(" : " ).append( usersReportModel.getRadioNetCode()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.fromMonth")).append(" : " ).append( usersReportModel.getFromMonth()).append(" , ")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionSummary.label.toMonth")).append(" : " ).append( usersReportModel.getToMonth()).toString();
			
			sb.setLength(0);
			columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.transferdate")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.loginId")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.userName")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.msisdn")).append(",")
					.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.categoryName")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.grphDomainName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.parentName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.parentMsisdn")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.parentCat")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.parentGeoName")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.ownerName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.ownerMsisdn")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.ownerGeo")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.ownerCat")).append(",")
											
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.grandName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.grandMsisdn")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.grandCategory")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.grandGeo")).append(",")
											
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.serviceTypeName")).append(",")

											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.selectorName")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.transactionCount")).append(",")
											.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.additionalCommissionDataSummary.tableHeader.differentialAmount")).toString();
											
											

			final String[] resultSetColumnName = {"trans_date","login_id","user_name","msisdn","category_name",
					"grph_domain_name","parent_name","parent_msisdn","parent_cat","parent_geo",
					"owner_name","owner_msisdn","owner_category","owner_geo","grand_name","grand_msisdn","grand_category","grand_geo_domain",
					"service_type_name","selector_name","transaction_count","differential_amount"};
			CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,	columnHeader,reportTopHeaders);
			CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);
			this.executeReaderWriterThread(reader,  csvWriter);
		}catch (InterruptedException | ParseException | SQLException | NoSuchMessageException e) 
		{
			log.errorTrace(methodName, e);
		}
		finally{
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
			if(log.isDebugEnabled())
				log.debug(methodName, PretupsI.EXITED);
		}
		return filePath + fileName;
	}
/**
 * 	
 * @param channelTransferAckModel
 * @param rptCode
 * @param con
 * @return
 * @throws InterruptedException
 */
	
	public String prepareDataAck(ChannelTransferAckModel channelTransferAckModel, String rptCode, Connection con) throws InterruptedException
	{
		if (log.isDebugEnabled())
			log.debug("prepareData Entered: ", rptCode);
	
				final String methodName = "prepareDataO2CTransferAck";
				if(log.isDebugEnabled())
					log.debug(methodName, PretupsI.ENTERED);
				PreparedStatement pstmt = null;
				String filePath = "";
				String fileName = "";
		        try {
			        	fileName = o2cTrfAckDetails + BTSLUtil.getFileNameStringFromDate(new Date())+ ".csv";
						filePath = path;
						File fileDir = new File(filePath);
						if (!fileDir.isDirectory()) {
							fileDir.mkdirs();
						}
						final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
						con.setAutoCommit(false);
						pstmt = o2CTransferAckRptQry.loado2cTransfeAskChannelUserReportQry(channelTransferAckModel,con);
			        	pstmt.setFetchSize(1000);
			        	MutableBoolean mutableBoolean = new MutableBoolean(false);
			        	StringBuilder sb =  new StringBuilder(1024);
						MutableBoolean filewritten = new MutableBoolean(false);
						final String reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.details")).append(" ; ").append( "")
								.append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.transnum")).append(" : ").append( channelTransferAckModel.getTransferNum()).append(" , ")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.userName")).append(" : " ).append( channelTransferAckModel.getUserName()).append(" , ")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.networkName")).append(" : ").append( channelTransferAckModel.getNetworkName()).append(" , ")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.geographicalDomain")).append(" : " ).append( channelTransferAckModel.getGegoraphyDomainName()).append(" , ")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.Domain")).append(" : " ).append( channelTransferAckModel.getDomainName()).append(" , ")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.msisdn")).append(" : " ).append( channelTransferAckModel.getMsisdn()).toString();
						sb.setLength(0);      
						final String columnHeader =sb.append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.transnum")).append(",")								
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.userName")).append(", ")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.status")).append(" , ")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.geographicalDomain")).append(",")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.Domain")).append(",")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.msisdn")).append(", ")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.categoryName")).append(", ")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.commissionProfileName")).append(",")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.transferProfileName")).append(",")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.transferType")).append(",")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.transferDate")).append(",")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.erpCode")).append(",")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.externalTxnNumber")).append(" ,")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.externalTxnDate")).append(" ,")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.transferCategory")).append(",")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.refrenceNum")).append(",")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.address")).append(" ,")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.productshortcode")).append(" ,")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.productName")).append(",")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.mrpinr")).append(" ,")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.qty")).append(" ,")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.approvedQty")).append(",")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.levoneqty")).append(" ,")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.levtwoqty")).append(" ,")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.levthrqty")).append(" ,")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.tax1tpye")).append(" ,")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.tax1rate")).append(" ,")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.tax1Amounts")).append(" ,")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.tax2tpye")).append(" ,")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.tax2rate")).append(" ,")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.tax2Amount")).append(" ,")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.commissiontype")).append(" ,")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.commissionrate")).append(" ,")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.commissionvalue")).append(" ,")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.cbctype")).append(" ,")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.cbcRate")).append(" ,")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.cbcAmount")).append(" ,")
                                .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.tds")).append(" ,")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.mrpamt")).append(" ,") 
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.pamt")).append(",")
						        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.netPayableAmt")).append(" ,")
						        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.receiverCreditQuantity")).append(" ,")
						        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.paymentinstrumenttype")).append(" ,")
						        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.paymentinstrumentnum")).append(" ,")
						        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.paymentinstrumentdate")).append(" ,")
						        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.paymentinstrumentamount")).append(" ,")
						        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.remarks")).append(" ,")
						        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.approverremark")).append(",")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.approverremarktwo")).append(" ,")
		                        .append(PretupsRestUtil.getMessageString("pretups.O2Ctransferacknowledgement.label.approverremarkthree")).append("").toString();

							
						final String[] resultSetColumnName = {"transfer_id","user_name","status","grph_domain_name","domain_name","msisdn","category_name","comm_profile_set_name","profile_name","transfer_type","transfer_date","external_code","ext_txn_no","ext_txn_date",
								"transfer_category","reference_no","address1","product_code","product_name","user_unit_price","required_quantity","approved_quantity","first_level_approved_quantity","second_level_approved_quantity","third_level_approved_quantity","tax1_type","tax1_rate","tax1_value","tax2_rate","tax2_type","tax2_value",
								"commission_type","commission_rate","commission_value","otf_type","otf_rate","otf_amount","commission_value","mrp","payable_amount","net_payable_amount","receiver_credit_quantity","pmt_inst_type","pmt_inst_no","pmt_inst_date","pmt_inst_amount","channel_user_remarks",
								"first_approver_remarks",
								"second_approver_remarks","third_approver_remarks"};
					

					CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,	columnHeader,reportTopHeaders);
					CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);

					this.executeReaderWriterThread(reader,  csvWriter);
				} 
				catch (InterruptedException | ParseException | SQLException e) 
				{
					log.errorTrace(methodName, e);
				}
				finally{
					
					if(log.isDebugEnabled())
						log.debug(methodName, PretupsI.EXITED);
				}
				return filePath + fileName;
				
				
				
			}
	
	
	
	/**
	 * @param usersReportModel
	 * @param con
	 * @return
	 */
	@SuppressWarnings("resource")
	public String prepareDataExternalUserReport(
			UsersReportModel usersReportModel, Connection con) {
		final String methodName = "prepareDataExternalUserReport";

		PreparedStatement pstmt = null;
		if (log.isDebugEnabled()) {
			log.debug(methodName, "usersReportModel.getUserType()="+usersReportModel.getUserType());
		}
		String filePath = "";
		String fileName = "";

		try {
			fileName = "ExternalUserReport"
					+ BTSLUtil.getFileNameStringFromDate(new Date()) + ".csv";

			filePath = path;
			File fileDir = new File(filePath);
			if (!fileDir.isDirectory()) {
				fileDir.mkdirs();
			}

			final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
			con.setAutoCommit(false);

			if (TypesI.OPERATOR_USER_TYPE.equalsIgnoreCase(usersReportModel
					.getUserType())) {
				pstmt = channelUserOperatprUserRolesQry
						.loadExternalUserRolesOperatorReportQry(
								usersReportModel, con);
				MutableBoolean mutableBoolean = new MutableBoolean(false);
				MutableBoolean filewritten = new MutableBoolean(false);
				StringBuilder sb =  new StringBuilder(1024);
				final String reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.heading")).append(" ; ").append("")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.rptcode")).append(" : ")
						.append( usersReportModel.getrptCode()).append(" , ")

						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.networkname")).append(" : ")
						.append( usersReportModel.getNetworkName()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.zone")).append(" : ")
						.append( usersReportModel.getZoneName()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.domain")).append(" : ")
						.append( usersReportModel.getDomainName()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.date")).append(" : ")
						.append( usersReportModel.getCurrentDate()
).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.cat")).append(" : ")
						.append( usersReportModel.getCategoryName()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.username")).append(" : ")
						.append( usersReportModel.getUserName()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.statusname")).append(" : ")
						.append( usersReportModel.getStatus()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.sorttype")).append(" : ") .append( usersReportModel.getSorttypeName()).toString();
				sb.setLength(0);
				final String columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.tableheader.parentname")).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.tableheader.parentmsisdn")).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.tableheader.ownername")).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.tableheader.ownermsisdn")).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.tableheader.catcode")).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.tableheader.status")).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.tableheader.username")).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.tableheader.loginid")).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.tableheader.msisdn")).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.tableheader.catname")).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.tableheader.domain")).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.tableheader.grphcode")).append( ",")
				.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.tableheader.roletype")).append( ",")
				.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.tableheader.rolename")).toString();

				final String[] resultSetColumnName = { "parent_name", "parent_msisdn",
						"owner_name", "owner_msisdn", "category_code",
						"status", "user_name", "login_id",
						"msisdn", "category_name", "domain_name",
						"grph_domain_code","roletype","role_name" };

				CSVReportWriter csvWriter = new CSVReportWriter(queue,
						filePath, fileName, mutableBoolean, filewritten,
						columnHeader, reportTopHeaders);
				CSVReportReader reader = new CSVReportReader(queue, pstmt,
						mutableBoolean, resultSetColumnName);

				this.executeReaderWriterThread(reader, csvWriter);

			}

			if (TypesI.CHANNEL_USER_TYPE.equalsIgnoreCase(usersReportModel
					.getUserType())) {
				pstmt = channelUserOperatprUserRolesQry
						.loadExternalUserRolesChannelReportQry(
								usersReportModel, con);

				MutableBoolean mutableBoolean = new MutableBoolean(false);
				MutableBoolean filewritten = new MutableBoolean(false);
				StringBuilder sb =  new StringBuilder(1024);
				final String reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.heading")).append(" ; ").append("")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.rptcode")).append(" : ")
						.append( usersReportModel.getrptCode()).append(" , ")

						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.networkname")).append(" : ")
						.append( usersReportModel.getNetworkName()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.zone")).append(" : ")
						.append( usersReportModel.getZoneName()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.domain")).append(" : ")
						.append( usersReportModel.getDomainName()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.date")).append(" : ")
						.append( usersReportModel.getCurrentDate()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.cat")).append(" : ")
						.append( usersReportModel.getCategoryName()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.username")).append(" : ")
						.append( usersReportModel.getUserName()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.statusname")).append(" : ")
						.append( usersReportModel.getStatus()).append(" , ")
						.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.header.sorttype")).append(" : ") 
						.append( usersReportModel.getSorttypeName()).toString();

				sb.setLength(0);
				final String columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.tableheader.catcode")).append( ",")
				.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.tableheader.status")).append( ",")
				.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.tableheader.username")).append( ",")
				.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.tableheader.loginid")).append( ",")
				.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.tableheader.msisdn")).append( ",")
				.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.tableheader.catname")).append( ",")
				.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.tableheader.domain")).append( ",")
				.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.tableheader.grphcode")).append( ",")
				.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.tableheader.roletype")).append( ",")
				.append(PretupsRestUtil.getMessageString("pretups.c2s.reports.external.tableheader.rolename")).toString();
				final String[] resultSetColumnName = { "category_code",
						"status", "user_name", "login_id",
						"msisdn", "category_name", "domain_name",
						"grph_domain_code","roletype","role_name" };

				CSVReportWriter csvWriter = new CSVReportWriter(queue,
						filePath, fileName, mutableBoolean, filewritten,
						columnHeader, reportTopHeaders);
				CSVReportReader reader = new CSVReportReader(queue, pstmt,
						mutableBoolean, resultSetColumnName);

				this.executeReaderWriterThread(reader, csvWriter);
			}
		} catch (InterruptedException | ParseException | SQLException e) {
			log.errorTrace(methodName, e);
		} finally {
			try{
		        if (pstmt!= null){
		        	pstmt.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
		}
		return filePath + fileName;

	}	
				
	public static String getDateForCalendarType(String field) {
		StringBuilder strBuilder = new StringBuilder();
		String calendarsystem = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CALENDAR_SYSTEM);
		String calendardateFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CALENDER_DATE_FORMAT);
		if(PretupsI.GREGORIAN.equalsIgnoreCase(calendarsystem)) {
			strBuilder.append(field);
		} else {
			strBuilder.append("to_char(").append(field).append(",'").append(calendardateFormat).append("','").
			append(PretupsI.NLS_CALENDAR).append("=").append(calendarsystem).append("')");
		}
		return strBuilder.toString();
	}

	public static String getDateTimeForCalendarType(String field) {
        StringBuilder strBuilder = new StringBuilder();
        String calendarsystem = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CALENDAR_SYSTEM);
		String dateTimeFormat = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.DATE_TIME_FORMAT);
		
        if(PretupsI.GREGORIAN.equalsIgnoreCase(calendarsystem)) {
              strBuilder.append(field);
        } else {
              strBuilder.append("to_char(").append(field).append(",'").append(dateTimeFormat).append("','").
              append(PretupsI.NLS_CALENDAR).append("=").append(calendarsystem).append("')");
        }
        return strBuilder.toString();
  }

	
	/**
	 * @param csvReportReader
	 * @param csvReportWriter
	 * @throws InterruptedException
	 * Note Generic method will be use in multiple location in download reports
	 */
	public  void executeReaderWriterThread(CSVEtopReportReader csvReportReader, CSVReportWriter csvReportWriter) throws InterruptedException{
		final String methodName = "executeReaderWriterThread";
		Thread w = new Thread(csvReportWriter, "Writer Thread");
		Thread r = new Thread(csvReportReader, "Reader Thread");
		w.setUncaughtExceptionHandler((thread, exception) -> {
			log.debug("Exception occurred in ", thread.getName());
			log.errorTrace(methodName, exception);
		});
		r.setUncaughtExceptionHandler((thread, exception) -> {
			log.debug("Exception occurred in ", thread.getName());
			log.errorTrace(methodName, exception);
		});

		log.debug("Starting thread to strat writing on the file", w.getName());
		long startTime = System.currentTimeMillis();
		w.start();
		log.debug("Starting thread to strat reading on the file", r.getName());
		r.start();
		w.join();
		long endTime = System.currentTimeMillis();
		log.debug("Total time in reading data from DB and wrting data on csv file ",
				(endTime - startTime) + "miliseconds");
	}
	
	
				
	}

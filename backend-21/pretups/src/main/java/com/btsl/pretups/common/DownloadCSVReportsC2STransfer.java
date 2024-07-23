package com.btsl.pretups.common;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Predicate;

import org.apache.commons.lang3.mutable.MutableBoolean;

import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.reports.businesslogic.C2STransferRptQry;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.channel.reports.web.UsersReportForm;
import com.web.pretups.channel.reports.web.UsersReportModel;

/**
 * @author tarun.kumar
 *
 */
public class DownloadCSVReportsC2STransfer {

	public static final Log log = LogFactory.getLog(DownloadCSVReportsC2STransfer.class.getName());
	private static String name = Constants.getProperty("REPORTS_DOWNLOAD_NAME");
	private static String path=Constants.getProperty("REPORTS_DOWNLOAD_PATH");
	private C2STransferRptQry c2STransferRptQry;
	private static String c2sTransferFileName = Constants.getProperty("C2S_TRANSFER_REPORTS_DOWNLOAD_FILE_NAME");
	/**
	 * return c2STransferRptQry
	 */
	public DownloadCSVReportsC2STransfer(){
			c2STransferRptQry = (C2STransferRptQry)ObjectProducer.getObject(QueryConstants.C2S_Transfer_Details_REPORT_QRY, QueryConstants.QUERY_PRODUCER);			
	}
	/**
	 * @param usersReportModel
	 * @param rptCode
	 * @param con
	 * @return
	 */
	public String prepareDataForC2STransferDetails(	UsersReportModel usersReportModel, String rptCode, Connection con) {
		if (log.isDebugEnabled()){
			log.debug("prepareDataForC2STransferDetails Entered: ", rptCode);  
		}
		String filePath ;		
		switch (rptCode) {		
		case  "c2sTransferChannelUserNew":			
			filePath = prepareDataC2sTransferChannelUserNew(usersReportModel,con);					
			break;	
		case "C2STRANSFER02":
			filePath = prepareDataC2sTransferC2STRANSFER02(usersReportModel,con);					
			break;
		case "c2sTransferChannelUserStaffNew":
			filePath = prepareDataC2sTransferChannelUserStaffNew(usersReportModel,con);					
			break;
		case "C2STRANSFER04":
			filePath = prepareDataC2sTransferC2STRANSFER04(usersReportModel,con);					
			break;
		case "c2sTransferNew":
			filePath = prepareDataC2sTransferNew(usersReportModel,con);					
			break;
		case "C2STRANSFER01":
			filePath = prepareDataC2sTransferC2STRANSFER01(usersReportModel,con);					
			break;
		case "c2sTransferStaffNew":
			filePath = prepareDataC2sTransferStaffNew(usersReportModel,con);					
			break;
		case "C2STRANSFER03":
			filePath = prepareDataC2STRANSFER03(usersReportModel,con);					
			break;
		default: filePath = "Invalid filePath";
            break;
		}
		return filePath;
	}
	
	
	public String prepareData(UsersReportForm thisForm, String rptCode, Connection con)
	{
		if (log.isDebugEnabled())
			log.debug("prepareData Entered: ", rptCode);
		String filePath = "";
		switch (rptCode) {
		
		case "ETOPBUNCHARG":
			filePath = prepareDataForEtopBundleCharge(thisForm, con);
			break;
		
		case "ETOPINCDRCCHARG":
			filePath = prepareDataForIncreaseDecreaseReoprt(thisForm, con);
			break;
				
		case "TOTALSALES01":
			filePath = prepareTotalSalesData(thisForm, con);
			break;	
		case "TOTALC2S01":
			filePath = prepareTotalC2SData(thisForm, con);
			break;

		case "ETOPO2CPYMTTYPE":
			filePath = prepareDataForEtopO2CPaymentReport(thisForm, con);
			break;	
					
		default:
			break;
		}
		return filePath;

	}
	
	/**
	 * @param usersReportModel
	 * @param con
	 * @return
	 */
	private String prepareDataC2STRANSFER03(UsersReportModel usersReportModel, Connection con) {
		
		 final String methodName = "prepareDataC2STRANSFER03";
		 PreparedStatement pstmt = null;		
		 if(log.isDebugEnabled()){
			log.debug(methodName, usersReportModel.getUserType());
		}
		String filePath = "";
		String fileName = "";
      try {
  	    fileName = c2sTransferFileName + BTSLUtil.getFileNameStringFromDate(new Date())+ ".csv"; 
		filePath = path;
		File fileDir = new File(filePath);
		if (!fileDir.isDirectory()) {
			fileDir.mkdirs();   
		}
		final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
		con.setAutoCommit(false);	
		 pstmt = c2STransferRptQry.loadC2sTransferStaffReport(con,usersReportModel);	
		     
		createCSVReportC2STransfer(usersReportModel, pstmt, filePath, fileName, queue);
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
	 * @param usersReportModel
	 * @param con
	 * @return
	 */
	private String prepareDataC2sTransferStaffNew(UsersReportModel usersReportModel, Connection con) {
		
		 final String methodName = "prepareDataC2sTransferStaffNew";
		 PreparedStatement pstmt = null;		
		 if(log.isDebugEnabled()){
			log.debug(methodName, usersReportModel.getUserType());
		}
		String filePath = "";
		String fileName = "";
      try {
  	    fileName = c2sTransferFileName + BTSLUtil.getFileNameStringFromDate(new Date())+ ".csv"; 
		filePath = path;
		File fileDir = new File(filePath);
		if (!fileDir.isDirectory()) {
			fileDir.mkdirs();   
		}
		final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
		con.setAutoCommit(false);	
		pstmt = c2STransferRptQry.loadC2sTransferStaffNewReport(con,usersReportModel);			
		     
		createCSVReportC2STransfer(usersReportModel, pstmt, filePath, fileName, queue);
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
	 * @param usersReportModel
	 * @param con
	 * @return
	 */
	private String prepareDataC2sTransferC2STRANSFER01(UsersReportModel usersReportModel, Connection con) {
		
		 final String methodName = "prepareDataC2sTransferC2STRANSFER01";
		 PreparedStatement pstmt = null;		
		 if(log.isDebugEnabled()){
			log.debug(methodName, usersReportModel.getUserType());
		}
		String filePath = "";
		String fileName = "";
      try {
  	    fileName = c2sTransferFileName + BTSLUtil.getFileNameStringFromDate(new Date())+ ".csv"; 
		filePath = path;
		File fileDir = new File(filePath);
		if (!fileDir.isDirectory()) {
			fileDir.mkdirs();   
		}
		final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
		con.setAutoCommit(false);	
		pstmt = c2STransferRptQry.loadC2sTransferReport(con,usersReportModel);				
		     
		createCSVReportC2STransfer(usersReportModel, pstmt, filePath, fileName, queue);
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
	 * @param usersReportModel
	 * @param con
	 * @return
	 */
	private String prepareDataC2sTransferNew(UsersReportModel usersReportModel, Connection con) {
		
		 final String methodName = "prepareDataC2sTransferNew";
		 PreparedStatement pstmt = null;		
		 if(log.isDebugEnabled()){
			log.debug(methodName, usersReportModel.getUserType());
		}
		String filePath = "";
		String fileName = "";
      try {
  	    fileName = c2sTransferFileName+ BTSLUtil.getFileNameStringFromDate(new Date())+ ".csv"; 
		filePath = path;
		File fileDir = new File(filePath);
		if (!fileDir.isDirectory()) {
			fileDir.mkdirs();   
		}
		final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
		con.setAutoCommit(false);	
		pstmt = c2STransferRptQry.loadC2sTransferNewReport(con,usersReportModel);				
		     
		createCSVReportC2STransfer(usersReportModel, pstmt, filePath, fileName, queue);
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
	 * @param usersReportModel
	 * @param con
	 * @return
	 */
	private String prepareDataC2sTransferC2STRANSFER04(UsersReportModel usersReportModel, Connection con) {
		
		 final String methodName = "prepareDataC2sTransferC2STRANSFER04";
		 PreparedStatement pstmt = null;		
		 if(log.isDebugEnabled()){
			log.debug(methodName, usersReportModel.getUserType());
		}
		String filePath = "";
		String fileName = "";
      try {
  	    fileName = c2sTransferFileName + BTSLUtil.getFileNameStringFromDate(new Date())+ ".csv"; 
		filePath = path;
		File fileDir = new File(filePath);
		if (!fileDir.isDirectory()) {
			fileDir.mkdirs();   
		}
		final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
		con.setAutoCommit(false);	
		pstmt = c2STransferRptQry.loadC2sTransferChannelUserStaffReport(con,usersReportModel);			
		     
		createCSVReportC2STransfer(usersReportModel, pstmt, filePath, fileName, queue);
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
	 * @param usersReportModel
	 * @param con
	 * @return
	 */
	private String prepareDataC2sTransferChannelUserStaffNew(UsersReportModel usersReportModel, Connection con) {
		
		 final String methodName = "prepareDataC2sTransferChannelUserStaffNew";
		 PreparedStatement pstmt = null;		
		 if(log.isDebugEnabled()){
			log.debug(methodName, usersReportModel.getUserType());
		}
		String filePath = "";
		String fileName = "";
      try {
  	    fileName = c2sTransferFileName + BTSLUtil.getFileNameStringFromDate(new Date())+ ".csv"; 
		filePath = path;
		File fileDir = new File(filePath);
		if (!fileDir.isDirectory()) {
			fileDir.mkdirs();   
		}
		final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
		con.setAutoCommit(false);	
		pstmt = c2STransferRptQry.loadC2sTransferChannelUserStaffNewReport(con,usersReportModel);		
		     
		createCSVReportC2STransfer(usersReportModel, pstmt, filePath, fileName, queue);
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
	 * @param usersReportModel
	 * @param con
	 * @return
	 */
	private String prepareDataC2sTransferC2STRANSFER02(UsersReportModel usersReportModel, Connection con) {
		
		 final String methodName = "prepareDataC2sTransferC2STRANSFER02";
		 PreparedStatement pstmt = null;		
		 if(log.isDebugEnabled()){
			log.debug(methodName, usersReportModel.getUserType());
		}
		String filePath = "";
		String fileName = "";
       try {
   	    fileName = c2sTransferFileName + BTSLUtil.getFileNameStringFromDate(new Date())+ ".csv"; 
		filePath = path;
		File fileDir = new File(filePath);
		if (!fileDir.isDirectory()) {
			fileDir.mkdirs();   
		}
		final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
		con.setAutoCommit(false);	
		pstmt = c2STransferRptQry.loadC2sTransferChannelUserReport(con,usersReportModel);	
		     
		createCSVReportC2STransfer(usersReportModel, pstmt, filePath, fileName, queue);
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
	 * @param usersReportModel
	 * @param con
	 * @return
	 */
	private String prepareDataC2sTransferChannelUserNew(UsersReportModel usersReportModel, Connection con) {
		
		 final String methodName = "prepareDataC2sTransferChannelUserNew";
		 PreparedStatement pstmt = null;		
		 if(log.isDebugEnabled()){
			log.debug(methodName, usersReportModel.getUserType());
		}
		String filePath = "";
		String fileName = "";
       try {
   	    fileName = c2sTransferFileName + BTSLUtil.getFileNameStringFromDate(new Date())+ ".csv"; 
		filePath = path;
		File fileDir = new File(filePath);
		if (!fileDir.isDirectory()) {
			fileDir.mkdirs();   
		}
		final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
		con.setAutoCommit(false);	
		pstmt = c2STransferRptQry.loadC2sTransferChannelUserNewReport(con,usersReportModel);	
		     
		createCSVReportC2STransfer(usersReportModel, pstmt, filePath, fileName, queue);
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
	
	
	private void createCSVReportC2STransfer(UsersReportModel usersReportModel,PreparedStatement pstmt, String filePath, String fileName,final BlockingQueue<String> queue) throws SQLException,			InterruptedException {
		
		pstmt.setFetchSize(1000);
	    MutableBoolean mutableBoolean = new MutableBoolean(false);
	    MutableBoolean filewritten = new MutableBoolean(false);
	    
	    if(PretupsI.NO.equals(usersReportModel.getStaffReport())){ 
	    	usersReportModel.setStaffReport("No");
	    	
	    }else{
	    	usersReportModel.setStaffReport("Yes");
	    	
	    }
	    StringBuilder sb =  new StringBuilder(1024);
	    final String reportTopHeaders = sb.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.label.c2stransfer.header")).append(" ; ").append( "")
	    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.reportTopHeaders.serviceType")).append( usersReportModel.getServiceTypeName()).append(" , ")
	    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.reportTopHeaders.transferStatus")).append( usersReportModel.getTransferStatusName()).append(" , ")
	    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.reportTopHeaders.staff")).append( usersReportModel.getStaffReport()).append(" , ")	
	    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.reportTopHeaders.date")).append( usersReportModel.getDate()).append(" , ")
	    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.reportTopHeaders.fromTime")).append( usersReportModel.getFromTime()).append(" , ")
	    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.reportTopHeaders.toTime")).append( usersReportModel.getToTime()).append(" , ")	
	    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.reportTopHeaders.msisdn")).append( usersReportModel.getMsisdn()).append(" , ")
	    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.reportTopHeaders.domainCode")).append( usersReportModel.getDomainName()).append(" , ")
	    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.reportTopHeaders.channelcategory")).append( usersReportModel.getCategoryName()).append(" , ")    	
                .append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.reportTopHeaders.channelcategoryuser")).append( usersReportModel.getUserName()).toString() ;
	    sb.setLength(0);	    	                    
	    final String columnHeader = sb.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.c2stransfer.columnHeader.transactionId")).append(",")
	    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.c2stransfer.columnHeader.transferTime")).append(",")	
	    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.c2stransfer.columnHeader.requestSource")).append(",")
	    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.c2stransfer.columnHeader.userName")).append(",")
	    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.c2stransfer.columnHeader.senderMobileNumber")).append(",")
	    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.c2stransfer.columnHeader.receiverMobileNumber")).append(",")		
	    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.c2stransfer.columnHeader.ServiceClass")).append(",")
	    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.c2stransfer.columnHeader.service")).append(",")
	    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.c2stransfer.columnHeader.SubService")).append(",")
	    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.c2stransfer.columnHeader.requestAmount")).append(",")
	    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.c2stransfer.columnHeader.creditAmount")).append(",")	    			    		
	    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.c2stransfer.columnHeader.bonus")).append(",")				
	    		.append(PretupsRestUtil.getMessageString("pretups.channel.user.reports.c2stransfer.columnHeader.processFee")).toString();												 																											              
	    
	   final String[] resultSetColumnName = { "transfer_id", "transfer_date_time","request_gateway_type","user_name","sender_msisdn","reciever_msisdn","service_class_name","service_name", "subservice_name", "transfer_value", "receiver_transfer_value", "receiver_bonus_value","receiver_access_fee" };					                             					                             					                              					                              					                              					                              					                              					                             					                             					                            					                             					                             					                            								   
	   CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,	columnHeader,reportTopHeaders);
	   CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);
	   DownloadCSVReports downloadCSVReports=new DownloadCSVReports();
	   downloadCSVReports.executeReaderWriterThread(reader,  csvWriter);
	}
	
	
	public String prepareDataForEtopBundleCharge(UsersReportForm thisForm, Connection con)
	 {
		//local_index_implemented
		int amountMultFactor = (int) PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR);
				final String methodName = "prepareDataForEtopBundleCharge";
				if (log.isDebugEnabled()) {
					log.debug(methodName, " Entered with : Network " + thisForm.getNetworkCode() + " Report Header  " + thisForm.getReportHeaderName() + " Amount mul factor "
							+ amountMultFactor + " Network Name  " + thisForm.getNetworkName() + " Zone name  "
							+ thisForm.getZoneName() + " Domain name " + thisForm.getDomainName() + " Category name " + thisForm.getCategoryName()
							+ " User name " + thisForm.getUserName() + " From date time " + thisForm.getFromDateTime() + " To date time  " + thisForm.getToDateTime()
							+ " User id " + thisForm.getUserID() + " Parent category code " + thisForm.getParentCategoryCode() + " Domain code "
							+ thisForm.getDomainCode() + " Zone Code " + thisForm.getZoneCode() + " Login user id " + thisForm.getLoginUserID() + " reportonlydateformat is "
							+ Constants.getProperty("report.onlydateformat") + " reportsystemdatetime.format is "
							+ Constants.getProperty("report.systemdatetime.format") + " Service Type " + thisForm.getServiceTypeId() + " Service type name "
							+ thisForm.getServiceTypedesc() + " Transfer status " + thisForm.getTransferStatus() + " transfer status name "
							+ thisForm.getTransferStatusName() + " report.onlytimeformat is " + Constants.getProperty("report.onlytimeformat") + " reposrting db is "
							+ thisForm.isReportingDB() +" sub service "+ thisForm.getCardGroupSubServiceID() + "card group code " + thisForm.getCardGroupCode() + "from amount " +thisForm.getFromAmount() + "To amount "+thisForm.getToAmount() + "subscriber msisdn "+thisForm.getToMsisdn());
				}
		
		LogFactory.printLog(methodName, thisForm.toString(), log);
		String filePath = "";
		String fileName = "";
		
		PreparedStatement pstmt = null;
		try {
			
			fileName = name + BTSLUtil.getFileNameStringFromDate(new Date())
					+ ".csv";
			filePath = path;
			File fileDir = new File(filePath);
			if (!fileDir.isDirectory()) {
				fileDir.mkdirs();
			}
			final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
			con.setAutoCommit(false);
			pstmt = c2STransferRptQry.getEtopBundleChargeQuery(con,thisForm);	
			
			createCSVReportForEtopBundleCharge(thisForm, pstmt, filePath, fileName, queue);
			
			
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
	
	
	private void createCSVReportForEtopBundleCharge(UsersReportForm vomsReportForm,PreparedStatement pstmt, String filePath, String fileName,final BlockingQueue<String> queue) throws SQLException,InterruptedException 
	{
		final String methodName="createCSVReportForEtopBundleCharge";
		String persiandate= "";
		Date currentDate = new Date();

		try {
			persiandate= BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(currentDate));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			log.errorTrace(methodName, e1);
		}
		
			pstmt.setFetchSize(1000);
		    MutableBoolean mutableBoolean = new MutableBoolean(false);
		    MutableBoolean filewritten = new MutableBoolean(false);
		    StringBuilder sb =  new StringBuilder(1024);
		    String reportTopHeaders1=Constants.getProperty("ETOP_BUNDLE_REPORTS_TOP_HEADER_1");
		    String columnHeader=Constants.getProperty("ETOP_BUNDLE_REPORTS_COLUMN_HEADER");
		    String reportTopHeaders2=Constants.getProperty("ETOP_BUNDLE_REPORTS_TOP_HEADER_2");
		    
		    String reportTopHeadersFinal = sb.append(reportTopHeaders1).append(" ; ").append( "")
		    		.append(reportTopHeaders2).append(" : " ).append(persiandate).toString();
		   
		   final String[] resultSetColumnName = { "transfer_date_time","user_name","payment_type","requested_quantity","paid_amount","applied_charge","tax_value","commission","transfer_status","service_name","Reciever_MSISDN","sender_msisdn","subService_name","card_group"};					                             					                             					                              					                              					                              					                              					                              					                             					                             					                            					                             					                             					                            								   
		   CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,	columnHeader,reportTopHeadersFinal);
		   CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);
		   DownloadCSVReports downloadCSVReports=new DownloadCSVReports();
		   downloadCSVReports.executeReaderWriterThread(reader,  csvWriter);
		}
	
	
	public String prepareDataForIncreaseDecreaseReoprt(UsersReportForm thisForm, Connection con)
	 {
		//local_index_implemented
				final String methodName = "prepareDataForIncreaseDecreaseReoprt";
				int amountMultFactor = (int) PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR);
				if (log.isDebugEnabled()) {
					log.debug(methodName, " Entered with : Network " + thisForm.getNetworkCode() + " Report Header  " + thisForm.getReportHeaderName() + " Amount mul factor "
							+ amountMultFactor + " Network Name  " + thisForm.getNetworkName() + " Zone name  "
							+ thisForm.getZoneName() + " Domain name " + thisForm.getDomainName() + " Category name " + thisForm.getCategoryName()
							+ " User name " + thisForm.getUserName() + " From date time " + thisForm.getFromDateTime() + " To date time  " + thisForm.getToDateTime()
							+ " User id " + thisForm.getUserID() + " Parent category code " + thisForm.getParentCategoryCode() + " Domain code "
							+ thisForm.getDomainCode() + " Zone Code " + thisForm.getZoneCode() + " Login user id " + thisForm.getLoginUserID() + " reportonlydateformat is "
							+ Constants.getProperty("report.onlydateformat") + " reportsystemdatetime.format is "
							+ Constants.getProperty("report.systemdatetime.format") + " Service Type " + thisForm.getServiceTypeId() + " Service type name "
							+ thisForm.getServiceTypedesc() + " report.onlytimeformat is " + Constants.getProperty("report.onlytimeformat") + " reposrting db is "
							+ thisForm.isReportingDB() +" sub service "+ thisForm.getCardGroupSubServiceID() + "Tranasaction id " + thisForm.getTransferNumber());
				}
		
		LogFactory.printLog(methodName, thisForm.toString(), log);
		String filePath = "";
		String fileName = "";
		
		PreparedStatement pstmt = null;
		try {
			
			fileName = name + BTSLUtil.getFileNameStringFromDate(new Date())
					+ ".csv";
			filePath = path;
			File fileDir = new File(filePath);
			if (!fileDir.isDirectory()) {
				fileDir.mkdirs();
			}
			final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
			con.setAutoCommit(false);
			
			String kindOfTranasction="";
			if(!BTSLUtil.isNullString(thisForm.getCardGroupSubServiceID()))
			{
				String[] s = thisForm.getCardGroupSubServiceID().split(":");
				kindOfTranasction=s[1];
				thisForm.setKindOfTransaction(kindOfTranasction);
				if (log.isDebugEnabled()) {
					log.debug(methodName,"kindOfTranasction is " + kindOfTranasction);
				}
			}
			Predicate<String> p_c2s=(str)->str.equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_C2SRC) || str.equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_C2SBRC);
			
			if(p_c2s.test(kindOfTranasction))
			pstmt = c2STransferRptQry.getC2SIncreaseDecreaseQuery(con,thisForm);
			
			
			Predicate<String> p_c2cdecr=(str)->str.equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_C2CWITHDRW) || str.equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_C2CREVSAL) || str.equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_C2C);
			
			if(p_c2cdecr.test(kindOfTranasction))
			pstmt = c2STransferRptQry.getC2CWithRevIncreaseDecreaseQuery(con,thisForm);
			
			
			Predicate<String> p_o2cdecr=(str)->str.equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_O2CWITHDRW) || str.equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_O2CREVSAL)  ;
			
			if(p_o2cdecr.test(kindOfTranasction))
			pstmt = c2STransferRptQry.getO2CWithRevIncreaseDecreaseQuery(con,thisForm);
			
			Predicate<String> p_o2cpymttype=(str)->str.equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_O2CASH) || str.equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_O2CCONGMNT) ||  str.equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_O2CONLINE) || str.equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_O2CFOC) ;
			
			if(p_o2cpymttype.test(kindOfTranasction))
			pstmt = c2STransferRptQry.getO2CPaymentTypeIncreaseDecreaseQuery(con,thisForm);
			
			
			
			
			createCSVReportForIncreaseDecreaseReport(thisForm, pstmt, filePath, fileName, queue);
			
			
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
	
	
	private void createCSVReportForIncreaseDecreaseReport(UsersReportForm vomsReportForm,PreparedStatement pstmt, String filePath, String fileName,final BlockingQueue<String> queue) throws SQLException,InterruptedException 
	{
		final String methodName="createCSVReportForIncreaseDecreaseReport";
		String persiandate= "";
		Date currentDate = new Date();

		try {
			persiandate= BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(currentDate));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			log.errorTrace(methodName, e1);
		}
		
			pstmt.setFetchSize(1000);
		    MutableBoolean mutableBoolean = new MutableBoolean(false);
		    MutableBoolean filewritten = new MutableBoolean(false);
		    StringBuilder sb =  new StringBuilder(1024);
		    String reportTopHeaders1=Constants.getProperty("ETOP_INCDRC_REPORTS_TOP_HEADER_1");
		    String columnHeader=Constants.getProperty("ETOP_INCDRC_REPORTS_COLUMN_HEADER");
		    String reportTopHeaders2=Constants.getProperty("ETOP_INCDRC_REPORTS_TOP_HEADER_2");
		    
		    String quantityType="";
		    String kindOfTransactions="";
		    
		    //Predicate<String> p_quantype=(str)->str.equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_C2SRC) || str.equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_C2SRC) || str.equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_C2SRC); 
			
		    Predicate<String> p_quantypedec=(str)->str.equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_O2CWITHDRW) || str.equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_O2CREVSAL) || str.equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_C2CWITHDRW) || str.equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_C2CREVSAL) || str.equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_C2SRC) || str.equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_C2SBRC)  ;
		    
		    if(p_quantypedec.test(vomsReportForm.getKindOfTransaction()))
		    	quantityType="Decrease";
		    
		    Predicate<String> p_quantypeinc=(str)->str.equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_O2CASH) || str.equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_O2CCONGMNT) || str.equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_O2CONLINE) || str.equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_O2CFOC) || str.equalsIgnoreCase(PretupsI.KIND_OF_TRANSACTION_C2C) ;
		    
		    if(p_quantypeinc.test(vomsReportForm.getKindOfTransaction()))
		    	quantityType="Increase";
		    
		    
				if(PretupsI.KIND_OF_TRANSACTION_O2CASH.equalsIgnoreCase(vomsReportForm.getKindOfTransaction()))
				kindOfTransactions="O2C CASH";
				else if (PretupsI.KIND_OF_TRANSACTION_O2CCONGMNT.equalsIgnoreCase(vomsReportForm.getKindOfTransaction()))		
				kindOfTransactions="O2C CONSIGNMENT";
				else if (PretupsI.KIND_OF_TRANSACTION_O2CONLINE.equalsIgnoreCase(vomsReportForm.getKindOfTransaction()))		
				kindOfTransactions="O2C ONLINE";
				else if (PretupsI.KIND_OF_TRANSACTION_O2CFOC.equalsIgnoreCase(vomsReportForm.getKindOfTransaction()))		
				kindOfTransactions="O2C FOC";
				else if (PretupsI.KIND_OF_TRANSACTION_C2C.equalsIgnoreCase(vomsReportForm.getKindOfTransaction()))		
				kindOfTransactions="O2C";
				else if (PretupsI.KIND_OF_TRANSACTION_O2CWITHDRW.equalsIgnoreCase(vomsReportForm.getKindOfTransaction()))		
				kindOfTransactions="O2C WITHDRAW";
				else if (PretupsI.KIND_OF_TRANSACTION_O2CREVSAL.equalsIgnoreCase(vomsReportForm.getKindOfTransaction()))		
				kindOfTransactions="O2C REVERSAL";
				else if (PretupsI.KIND_OF_TRANSACTION_C2CWITHDRW.equalsIgnoreCase(vomsReportForm.getKindOfTransaction()))		
				kindOfTransactions="C2C WITHDRAW";
				else if (PretupsI.KIND_OF_TRANSACTION_C2CREVSAL.equalsIgnoreCase(vomsReportForm.getKindOfTransaction()))		
				kindOfTransactions="C2C REVERSAL";
				else if (PretupsI.KIND_OF_TRANSACTION_C2SRC.equalsIgnoreCase(vomsReportForm.getKindOfTransaction()))		
				kindOfTransactions="C2S RECHARGE";
				else if (PretupsI.KIND_OF_TRANSACTION_C2SBRC.equalsIgnoreCase(vomsReportForm.getKindOfTransaction()))		
				kindOfTransactions="C2S BUNDLE RECHARGE";
					    
		    
		    
		   String reportTopHeadersFinal = sb.append(reportTopHeaders1).append(" ; ").append( "")						
						.append(reportTopHeaders2).append(" : ").append(persiandate).append(" ; ").append("Quantity Type").append(": ").append(quantityType).append(" ; ").append("").append("Kind Of Tranasctions ").append(" : ").append(kindOfTransactions).toString();
		   
		   final String[] resultSetColumnName = { "transfer_date_time","distributor_name","distributor_msisdn","amount_of_transaction","post_balance","transfer_id","payment_type"};					                             					                             					                              					                              					                              					                              					                              					                             					                             					                            					                             					                             					                            								   
		   CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,	columnHeader,reportTopHeadersFinal);
		   CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);
		   DownloadCSVReports downloadCSVReports=new DownloadCSVReports();
		   downloadCSVReports.executeReaderWriterThread(reader,  csvWriter);
		}


//for total sales and total c2s(recharge and bundle) reports
	public String prepareTotalSalesData(UsersReportForm thisForm, Connection con) {

		final String methodName = "prepareTotalSalesData";
		if(log.isDebugEnabled())
		{
			log.debug(methodName, " Entered with : " + thisForm.getUserID()+ ",Fromdate  " + thisForm.getFromDateTime()+ ",To date " + thisForm.getToDateTime());
		}
		
		LogFactory.printLog(methodName, thisForm.toString(), log);
		String filePath = "";
		String fileName = "";
		//String persiandate= "";
		Date currentDate = new Date();
		
		PreparedStatement pstmt = null;
		
		try {
			
			fileName = name + BTSLUtil.getFileNameStringFromDate(new Date())
					+ ".csv";
			filePath = path;
			File fileDir = new File(filePath);
			if (!fileDir.isDirectory()) {
				fileDir.mkdirs();
			}
			final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
			con.setAutoCommit(false);
			
			pstmt = c2STransferRptQry.getTotalSalesQuery(con,thisForm);
			
			createCSVReportForTotalSales(thisForm, pstmt, filePath, fileName, queue);
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
	
	private void createCSVReportForTotalSales(UsersReportForm thisForm,PreparedStatement pstmt, String filePath, String fileName,final BlockingQueue<String> queue) throws SQLException,InterruptedException 
	{
		final String methodName="createCSVReportForTotalSales";
		String persiandate= "";
		String fromdate= "";
		String todate= "";
		Date currentDate = new Date();
		try {
			persiandate= BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(currentDate));
			fromdate= BTSLDateUtil.getLocaleDateTimeFromDate(thisForm.getFromDateTime());
			todate= BTSLDateUtil.getLocaleDateTimeFromDate(thisForm.getToDateTime());
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			log.errorTrace(methodName, e1);
		}
		
			pstmt.setFetchSize(1000);
		    MutableBoolean mutableBoolean = new MutableBoolean(false);
		    MutableBoolean filewritten = new MutableBoolean(false);
		    
		    StringBuilder sb =  new StringBuilder(1024);
		    final String reportTopHeaders = sb.append(Constants.getProperty("TOTAL_SALES_TOP_HEADER")).append(";").append(Constants.getProperty("VoucherStatisticsReport_ReportDate")).append(" :").append(persiandate ).append(";").append( Constants.getProperty("VoucherStatisticsReport_FromDate")).append(" :").append(fromdate).append(";").append( Constants.getProperty("VoucherStatisticsReport_EndDate")).append(" :").append(todate).toString();
            
			final String columnHeader = Constants.getProperty("ETopUpReport_TotalSalesReportHeader");
			
			final String[] resultSetColumnName = { "trans_date", "NAME", "MSISDN", "BOUGHT", "RECHARGE", "BUNDLE", "OTHERS", "CLOSING_BALANCE" };
			CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,	columnHeader,reportTopHeaders);
			CSVReportReader reader = new CSVReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);
			DownloadCSVReports downloadCSVReports=new DownloadCSVReports();
		   downloadCSVReports.executeReaderWriterThread(reader,  csvWriter);
		}
	
	
	public String prepareTotalC2SData(UsersReportForm thisForm, Connection con) {

		final String methodName = "prepareTotalC2SData";
		if(log.isDebugEnabled())
		{
			log.debug(methodName, " Entered with : " + thisForm.getUserID()+ ",Fromdate  " + thisForm.getFromDateTime()+ ",To date " + thisForm.getToDateTime());
		}
		
		LogFactory.printLog(methodName, thisForm.toString(), log);
		String filePath = "";
		String fileName = "";
		//String persiandate= "";
		Date currentDate = new Date();
		
		PreparedStatement pstmt = null;
		
		try {
			
			fileName = name + BTSLUtil.getFileNameStringFromDate(new Date())
					+ ".csv";
			filePath = path;
			File fileDir = new File(filePath);
			if (!fileDir.isDirectory()) {
				fileDir.mkdirs();
			}
			final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
			con.setAutoCommit(false);
			
			pstmt = c2STransferRptQry.getTotalC2SQuery(con,thisForm);
			
			createCSVReportForTotalC2S(thisForm, pstmt, filePath, fileName, queue);
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
	
	private void createCSVReportForTotalC2S(UsersReportForm thisForm,PreparedStatement pstmt, String filePath, String fileName,final BlockingQueue<String> queue) throws SQLException,InterruptedException 
	{
		final String methodName="createCSVReportForTotalC2S";
		String persiandate= "";
		String fromdate= "";
		String todate= "";
		Date currentDate = new Date();
		try {
			persiandate= BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(currentDate));
			fromdate= BTSLDateUtil.getLocaleDateTimeFromDate(thisForm.getFromDateTime());
			todate= BTSLDateUtil.getLocaleDateTimeFromDate(thisForm.getToDateTime());
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			log.errorTrace(methodName, e1);
		}
		
			pstmt.setFetchSize(1000);
		    MutableBoolean mutableBoolean = new MutableBoolean(false);
		    MutableBoolean filewritten = new MutableBoolean(false);
		    
		    StringBuilder sb =  new StringBuilder(1024);
		    final String reportTopHeaders = sb.append(Constants.getProperty("TOTAL_C2S_TOP_HEADER")).append(";").append(Constants.getProperty("VoucherStatisticsReport_ReportDate")).append(" :").append(persiandate ).append(";").append( Constants.getProperty("VoucherStatisticsReport_FromDate")).append(" :").append(fromdate).append(";").append( Constants.getProperty("VoucherStatisticsReport_EndDate")).append(" :").append(todate).toString();
            
			final String columnHeader = Constants.getProperty("ETopUpReport_TotalC2SReportHeader");
			
			final String[] resultSetColumnName = { "trans_date",  "RECHARGE", "BUNDLE" };
			CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,	columnHeader,reportTopHeaders);
			CSVTotalC2SReportReader reader = new CSVTotalC2SReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);
			DownloadCSVReportsC2STransfer downloadCSVReports=new DownloadCSVReportsC2STransfer();
			   downloadCSVReports.executeReaderWriterThread(reader,  csvWriter);
		}	
	
	/**
	 * @param csvReportReader
	 * @param csvReportWriter
	 * @throws InterruptedException
	 * Note Generic method will be use in createCSVReportForTotalC2S in download reports
	 */
	public  void executeReaderWriterThread(CSVTotalC2SReportReader csvReportReader, CSVReportWriter csvReportWriter) throws InterruptedException{
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
	
	private void createCSVReportForEtopO2CPayment(UsersReportForm vomsReportForm,PreparedStatement pstmt, String filePath, String fileName,final BlockingQueue<String> queue) throws SQLException,InterruptedException 
	{
		final String methodName="createCSVReportForEtopO2CPayment";
		String persiandate= "";
		Date currentDate = new Date();

		try {
			persiandate= BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(currentDate));
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			log.errorTrace(methodName, e1);
		}
		
			pstmt.setFetchSize(1000);
		    MutableBoolean mutableBoolean = new MutableBoolean(false);
		    MutableBoolean filewritten = new MutableBoolean(false);
		    
		    String reportTopHeaders1=Constants.getProperty("ETOP_O2CPYMNT_REPORTS_TOP_HEADER_1");
		    String columnHeader=Constants.getProperty("ETOP_O2CPYMNT_REPORTS_COLUMN_HEADER");
		    String reportTopHeaders2=Constants.getProperty("ETOP_O2CPYMNT_REPORTS_TOP_HEADER_2");
		    
		    StringBuilder sb =  new StringBuilder(1024); 
		   String reportTopHeadersFinal = sb.append(reportTopHeaders1).append(" ; ").append( "")
				   .append( reportTopHeaders2).append(" : " ).append( persiandate).toString();
		   
		   final String[] resultSetColumnName = { "distributor_name","distributor_msisdn","bank_name","payment_type","requested_amount","unit_amount","unit_price","total_amount","discount","extra_charge","discount_amount","extra_charge_amount","amount_after_discount","amount_after_extra_charge","tax","amount_paid","payment_date","transaction_bank_number","bank_status","payment_status","first_approver_remarks","SECOND_APPROVER_REMARKS","THIRD_APPROVER_REMARKS"};					                             					                             					                              					                              					                              					                              					                              					                             					                             					                            					                             					                             					                            								   
		   CSVReportWriter csvWriter = new CSVReportWriter(queue, filePath, fileName, mutableBoolean, filewritten,	columnHeader,reportTopHeadersFinal);
		   CSVEtopReportReader reader = new CSVEtopReportReader(queue, pstmt, mutableBoolean, resultSetColumnName);
		   DownloadCSVReports downloadCSVReports=new DownloadCSVReports();
		   downloadCSVReports.executeReaderWriterThread(reader,  csvWriter);
		}
		
		
		public String prepareDataForEtopO2CPaymentReport(UsersReportForm thisForm, Connection con)
	 {
		//local_index_implemented
				final String methodName = "prepareDataForEtopO2CPaymentReport";
				int amountMultFactor = (int) PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR);
				if (log.isDebugEnabled()) {
					log.debug(methodName, " Entered with : Network " + thisForm.getNetworkCode() + " Report Header  " + thisForm.getReportHeaderName() + " Amount mul factor "
							+ amountMultFactor + " Network Name  " + thisForm.getNetworkName() + " Zone name  "
							+ thisForm.getZoneName() + " Domain name " + thisForm.getDomainName() + " Category name " + thisForm.getCategoryName()
							+ " User name " + thisForm.getUserName() + " From date time " + thisForm.getFromDateTime() + " To date time  " + thisForm.getToDateTime()
							+ " User id " + thisForm.getUserID() + " Parent category code " + thisForm.getParentCategoryCode() + " Domain code "
							+ thisForm.getDomainCode() + " Zone Code " + thisForm.getZoneCode() + " Login user id " + thisForm.getLoginUserID() + " reportonlydateformat is "
							+ Constants.getProperty("report.onlydateformat") + " reportsystemdatetime.format is "
							+ Constants.getProperty("report.systemdatetime.format") + " Service Type " + thisForm.getServiceTypeId() + " Service type name "
							+ thisForm.getServiceTypedesc() + " Transfer status " + thisForm.getTransferStatus() + " transfer status name "
							+ thisForm.getTransferStatusName() + " report.onlytimeformat is " + Constants.getProperty("report.onlytimeformat") + " reposrting db is "
							+ thisForm.isReportingDB() +"Bank name "+thisForm.getPaymentGatewayType()+ "payment type "+thisForm.getPaymentInstCode());
				}
		
		LogFactory.printLog(methodName, thisForm.toString(), log);
		String filePath = "";
		String fileName = "";
		
		PreparedStatement pstmt = null;
		try {
			
			fileName = name + BTSLUtil.getFileNameStringFromDate(new Date())
					+ ".csv";
			filePath = path;
			File fileDir = new File(filePath);
			if (!fileDir.isDirectory()) {
				fileDir.mkdirs();
			}
			final BlockingQueue<String> queue = new ArrayBlockingQueue<>(1000);
			con.setAutoCommit(false);
			pstmt = c2STransferRptQry.getO2CPaymentTypeQuery(con,thisForm);	
			
			createCSVReportForEtopO2CPayment(thisForm, pstmt, filePath, fileName, queue);
			
			
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
		
		
}

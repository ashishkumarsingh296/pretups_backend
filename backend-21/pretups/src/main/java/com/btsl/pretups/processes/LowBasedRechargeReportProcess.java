package com.btsl.pretups.processes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;

public class LowBasedRechargeReportProcess {

	private static Log LOG = LogFactory.getLog(LowBasedRechargeReportProcess.class.getName());

	private static String NETWORK_CODE;

	private static String FILE_HEADERS;

	private static String FILE_PATH;

	/**
     * to ensure no class instantiation 
     */
    private LowBasedRechargeReportProcess(){
    	
    }
	public static void main(String[] args) {
		final  String methodName = "main";
		try{
			if(args.length<3){
				LOG.info(methodName, "Usage : LowBasedRechargeReportProcess [Constants file] [LogConfig file][Network Code] Missing");
				return;
			}
			else{
				File constantsFile = Constants.validateFilePath(args[0]);
				if(!constantsFile.exists()){
					LOG.info(methodName, " Constants File Not Found .............");
					return;
				}
				File logconfigFile = Constants.validateFilePath(args[1]);
				if(!logconfigFile.exists()){
					LOG.info(methodName, " Logconfig File Not Found .............");
					return;
				}
				ConfigServlet.loadProcessCache(constantsFile.toString(),logconfigFile.toString());

				NETWORK_CODE = args[2];

				if(BTSLUtil.isNullString(NETWORK_CODE)){
					LOG.info(methodName, " Network Code Not Found .............");
					return;
				}

				FILE_PATH = Constants.getProperty("LB_TXN_REPORT_SCHEDULE")+NETWORK_CODE+"/C2S/";

				FILE_HEADERS=Constants.getProperty("FILE_HEADER_NAME_LOWBASED");

				if(null == FILE_HEADERS){
					LOG.info(methodName, "FILE_HEADER_NAME_LOWBASED or LB_TXN_REPORT_SCHEDULE entry missing in [Constants file]");
					return;
				}
				process();
			}
		}
		catch(Exception ex){
			LOG.errorTrace(methodName, ex);
			ConfigServlet.destroyProcessCache();
			return;
		}
		finally{
			if (LOG.isDebugEnabled()) {
				LOG.info(methodName," Exiting");
			}
			ConfigServlet.destroyProcessCache();
		}
	}

	private static void process() throws BTSLBaseException
	{
		final String methodName = "process";
		Connection con=null;
		MComConnectionI mcomCon = null;
		ProcessStatusVO  processVO=null;
		ProcessBL processBL = null;
		if(LOG.isDebugEnabled()) {
			LOG.debug(methodName,"Entered");
		}
		try{  
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			processBL=new ProcessBL();
			//locking process id for particular network
			processVO=processBL.checkProcessUnderProcess(con,PretupsI.LOWBASE_REPORT_PROCESS_ID);
			if(processVO!=null && processVO.isStatusOkBool()){

				loadLowBaseDataListForReport(con, processVO);
			}
		}
		catch(BTSLBaseException be){
			LOG.error(methodName, "BTSLBaseException : " + be.getMessage());
			if(con!=null) {
				try{
					con.rollback();
				} 
				catch(Exception e1)
				{LOG.errorTrace(methodName, e1);}
			}
			LOG.errorTrace(methodName,be);
			throw be;
		}
		catch(Exception e){
			LOG.error(methodName, "Exception : " + e.getMessage());
			if(con!=null) {
				try{
					con.rollback();
				} 
				catch(Exception e1)
				{
					LOG.errorTrace(methodName, e1);
				}
			}
			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"LowBasedRechargeReportProcess[process]","","",""," LowBasedRechargeReportProcess not executed successfully.");
			throw new BTSLBaseException("LowBasedRechargeReportProcess",methodName,PretupsErrorCodesI.DIRECT_PAY_OUT_GENERAL_EXCEPTION);
		}
		finally{
			if(processVO!=null && processVO.isStatusOkBool()){
				try{
					Date currentDate= new Date();
					processVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
					processVO.setExecutedUpto(BTSLUtil.addDaysInUtilDate(currentDate, -1));// executed up to yesterday
					processVO.setStartDate(currentDate);	
					processVO.setExecutedOn(currentDate);
					processVO.setRecordCount(0);
					ProcessStatusDAO processDAO=new ProcessStatusDAO();
					if(processDAO.updateProcessDetail(con,processVO)>0) {
						con.commit();
					} else {
						con.rollback();
						if(LOG.isDebugEnabled()) {
							LOG.error(methodName,"  couldn't able to download low base rechrage reports for networkCode "+NETWORK_CODE);
						}
					}
				}
				catch(Exception e){
					if (LOG.isDebugEnabled()) {
						LOG.error(methodName,"  couldn't able to download low base rechrage reports for networkCode "+NETWORK_CODE);
					}
					LOG.errorTrace(methodName,e);
				}
			}

			if(mcomCon != null){mcomCon.close("LowBasedRechargeReportProcess#process");mcomCon=null;}
			con=null;
			if(LOG.isDebugEnabled()) {
				LOG.debug(methodName, "Exiting..... ");
			}
		}
	}

	private static File generateLowBaseReportFile( Date dateOfReport){
		String methodName = "generateLowBaseReportFile";

		LOG.debug(methodName," entered ");

		File newFile1=new File(FILE_PATH);
		if(! newFile1.isDirectory())
			newFile1.mkdirs();
		SimpleDateFormat sdf1= new SimpleDateFormat("ddMMyy");
		String fileName=FILE_PATH+NETWORK_CODE+"_LBTransactionReport_"+sdf1.format(dateOfReport)+".csv";
		File newFile = new File(fileName);
		LOG.debug(methodName,"Exiting. fileName "+fileName);

		return newFile;
	}

	private static void writeLowBaseReportDataInFile( Writer out, String dataToWite, boolean isLastColumn)  {
		LOG.debug("writeLowBaseReportDataInFile", " entered column value "+dataToWite);
		try{
			if(isLastColumn){
				out.write(dataToWite);
				out.write("\n");
			}
			else{
				out.write(dataToWite+",");
			}
		}
		catch( IOException ex){
			LOG.error("LowBasedRechargeReportProcess :  writeLowBaseReportDataInFile : Exception while writing into report file ",ex);
		} 
	}

	private static void  writeLowBaseReportHeaderInFile( Writer out){
		try{
			out.write(FILE_HEADERS+"\n");
		}
		catch( IOException ex){
			LOG.error("LowBasedRechargeReportProcess :  writeLowBaseReportHeaderInFile : Exception while writing report header ", "Exception ", ex);	
		} 
	}

	private static void  loadLowBaseDataListForReport(Connection con, ProcessStatusVO  processVO) throws BTSLBaseException{
		//local_index_implemented
		final String methodName = "loadLowBaseDataListForReport";
		if (LOG.isDebugEnabled()) {
			LOG.debug(methodName, " Entered");
		}
		int noOfDaysToGenenarateReport;
		ResultSet rs=null;
		PreparedStatement pstmt=null;
		Writer out = null;
		try{
			StringBuilder selectLowBaseRechargeListSql = new StringBuilder();
			selectLowBaseRechargeListSql.append("select ct.SENDER_MSISDN,ct.RECEIVER_MSISDN, ct.TRANSFER_ID,ct.TRANSFER_DATE_TIME,ct.NETWORK_CODE,ct.TRANSFER_VALUE,ct.SENDER_ID,u.USER_NAME,ct.SOURCE_TYPE,ct.TRANSFER_STATUS,   u.PARENT_ID, ");
			selectLowBaseRechargeListSql.append(" (SELECT dist_users.USER_NAME from users dist_users Where u.PARENT_ID = dist_users.USER_ID  and u.PARENT_ID <> 'ROOT') DIST_NAME ");
			selectLowBaseRechargeListSql.append(" from C2S_TRANSFERS ct INNER JOIN users u ON ct.SENDER_ID=u.USER_ID ");
			selectLowBaseRechargeListSql.append(" where ct.transfer_date=? AND ct.LOW_BASED_RECHARGE = 'Y' ");
			LOG.debug(methodName, " selectLowBaseRechargeListSql "+selectLowBaseRechargeListSql.toString());

			Date processExecutedUpto = processVO.getExecutedUpto();
			/*no of days for which reports to be generated is from last executed date to yesterday*/
			noOfDaysToGenenarateReport =  BTSLUtil.getDifferenceInUtilDates( processExecutedUpto , BTSLUtil.addDaysInUtilDate(new Date(), -1));
			LOG.debug(methodName, " noOfDaysToGenenarateReport "+noOfDaysToGenenarateReport);

			if(noOfDaysToGenenarateReport <= 0){
				throw new BTSLBaseException(methodName,PretupsErrorCodesI.LB_REPORT_ALREADY_PROCESSED);
			}

			for(int i =1; i<=noOfDaysToGenenarateReport; i++){
				pstmt =  con.prepareStatement(selectLowBaseRechargeListSql.toString());
				Date lowBaseReportForTheDay = BTSLUtil.addDaysInUtilDate(processExecutedUpto, i);
				pstmt.setDate(1, new java.sql.Date(lowBaseReportForTheDay.getTime()));
				rs=pstmt.executeQuery();

				LOG.debug(methodName, "no low base reports : rs.getFetchSize "+rs.getFetchSize());

				if(!rs.next())
					continue;
				else{

					File fileGenerated = generateLowBaseReportFile(lowBaseReportForTheDay);
					try{
						out = new OutputStreamWriter(new FileOutputStream(fileGenerated));
					} catch (IOException ex ) {
						LOG.error("LowBasedRechargeReportProcess :  getFileWiseWriter : Exception while getting writer for file ", ex);	
					}
					
					writeLowBaseReportHeaderInFile( out);

					do{
						writeLowBaseReportDataInFile(out, rs.getString("SENDER_MSISDN"), false);
						writeLowBaseReportDataInFile(out, rs.getString("RECEIVER_MSISDN"), false);
						writeLowBaseReportDataInFile(out, rs.getString("TRANSFER_ID"), false);
						Timestamp timestamp = rs.getTimestamp("TRANSFER_DATE_TIME");
						writeLowBaseReportDataInFile(out,BTSLUtil.getDateTimeStringFromDate( new Date(timestamp.getTime()),"dd/MM/yy hh:mm:ss a" ), false);
						writeLowBaseReportDataInFile(out, PretupsBL.getDisplayAmount(rs.getLong("TRANSFER_VALUE")), false);
						writeLowBaseReportDataInFile(out, rs.getString("SENDER_ID"), false);
						writeLowBaseReportDataInFile(out, rs.getString("USER_NAME"), false);
						String parrentId = rs.getString("PARENT_ID");

						if("ROOT".equals(parrentId)){
							writeLowBaseReportDataInFile( out, " ", false);
							writeLowBaseReportDataInFile(out, " ", false);
						}
						else{
							writeLowBaseReportDataInFile( out, parrentId, false);
							writeLowBaseReportDataInFile(out, rs.getString("DIST_NAME"), false);
						}
						writeLowBaseReportDataInFile( out, rs.getString("SOURCE_TYPE"), false);
						String txnStatus = rs.getString("TRANSFER_STATUS");
						writeLowBaseReportDataInFile( out, txnStatus, false);

						if(PretupsI.TXN_STATUS_SUCCESS.equals(txnStatus))
							writeLowBaseReportDataInFile(out, "Recharge Successful", true);
						else
							writeLowBaseReportDataInFile(out,  " ", true);

					}
					while(rs.next());
				}
			}

		}
		catch (SQLException sqle) {
			LOG.error(methodName, "SQLException " + sqle.getMessage());

			LOG.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "loadLowBalDataListForReport[process]", "", "", "",
					"Not able to load low balance data from DB"  + "FAIL" + sqle.getMessage());

		}
		catch (Exception e) {
			LOG.error(methodName, "Exception " + e.getMessage());

			LOG.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "loadLowBalDataListForReport[process]", "", "", "",
					"Not able to load low balance data"  + "FAIL" + e.getMessage());
		}
		finally{
			try{
				if (out!=null){
					out.close();
				}
			}catch(Exception e){
				LOG.error(methodName, "Exception " + e.getMessage());
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}

			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			try {
				if (rs != null) {
					rs.close();
				}

			} catch (Exception e) {
				LOG.errorTrace(methodName, e);
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(methodName,"exiting" );
			}

		}

	}
}

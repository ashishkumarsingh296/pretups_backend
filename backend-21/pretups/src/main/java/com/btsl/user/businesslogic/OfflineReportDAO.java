/**
 * @(#)UserDAO.java
 *                  Copyright(c) 2005, Bharti Telesoft Ltd.
 *                  All Rights Reserved
 * 
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  Author Date History
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  Mohit Goel 22/06/2005 Initial Creation
 *                  Sandeep Goel 12/12/2005 Modification
 *                  Shashank Gaur 29/03/2013 Modification(Barred For Deletion)
 *                  ------------------------------------------------------------
 *                  -------------------------------------
 *                  This class is used for User Insertion/Updation
 * 
 */
package com.btsl.user.businesslogic;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// commented for DB2import oracle.jdbc.OraclePreparedStatement;
import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ReportMasterRespVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.processes.businesslogic.DailyReportAnalysisQry;
import com.btsl.pretups.user.businesslogic.OfflineReportTaskIDInfo;
import com.btsl.pretups.user.businesslogic.ViewOfflineReportStatusVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * author Subesh KCV 
 *
 */
public class OfflineReportDAO {
	 
    public static boolean flag = false;
    private static final String EXCEPTION = "EXCEPTION: ";
    private static final String SQL_EXCEPTION = "SQL EXCEPTION: ";
    private static final String QUERY_KEY = "Query: ";
    

    /**
     * Commons Logging instance.
     */
    private final Log _log = LogFactory.getLog(this.getClass().getName());
    
    private DailyReportAnalysisQry dailyRptAnalysiQry = (DailyReportAnalysisQry)ObjectProducer.getObject(QueryConstants.DAILY_REPORT_ANALYSIS, QueryConstants.QUERY_PRODUCER);
    
    /**
     * Method to fetch report processor bean name by report ID.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param reportID
     *            
     * 
     * @return ReportMasterRespVO
     * @throws BTSLBaseException
     */
    public ReportMasterRespVO getReportMasterByID(Connection p_con, String reportID) throws BTSLBaseException {
        final String methodName = "getReportMasterByID";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: reportID=");
        	loggerValue.append(reportID);
       
        	_log.debug(methodName, loggerValue);
        }
        
        ReportMasterRespVO reportMasterRespVO = null;
        StringBuilder strBuff = new StringBuilder();

        
            strBuff.append("SELECT REPORT_ID,REPORT_NAME,FILE_NAME_PREFIX,RPT_PROCESSOR_BEAN_NAME FROM report_master WHERE UPPER(report_id) = UPPER(?)");
        
 try {
        String sqlSelect = strBuff.toString();
        if(_log.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			_log.debug(methodName, loggerValue);
		}
        try( PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);)
        {
        pstmtSelect.setString(1, reportID);
	       try(ResultSet rs = pstmtSelect.executeQuery();)
	       {
	       
	    	   if (rs.next()) {
	    		   reportMasterRespVO= new ReportMasterRespVO();
	    		   reportMasterRespVO.setReportID(rs.getString("REPORT_ID"));
	    		   reportMasterRespVO.setReportName(rs.getString("REPORT_NAME"));
	    		   reportMasterRespVO.setReportProcessorBeanName(rs.getString("RPT_PROCESSOR_BEAN_NAME"));
	    		   reportMasterRespVO.setFileNamePrefix(rs.getString("FILE_NAME_PREFIX"));
	    		   }
	       }
       } 
        
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[isUserLoginExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[isUserLoginExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
        
        	LogFactory.printLog(methodName, "Exiting: reportID="+reportID  , _log);
        }
    
 return reportMasterRespVO;
    }
    
    
    //Long.toString(UserMigrationDAO.getNextID(p_con, TypesI.USERID, TypesI.ALL, p_networkCode, null)
    
    
    
    
    /**
     * Method for adding offline report
     * 
     * @param p_con
     *            java.sql.Connection
     * @param OfflineReportReqVO
     *            offlineReportReqVO
     * @return insertCount int
     * @throws BTSLBaseException
     */
    public int addOfflineReportProcess(Connection p_con, OfflineReportReqVO offlineReportReqVO) throws BTSLBaseException {
        int insertCount = 0;
        final String methodName = "addOfflineReportProcess";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: offlineReportReqVO=");
        	loggerValue.append(offlineReportReqVO);
        	_log.debug(methodName, loggerValue);
        }
        try {
            StringBuilder strBuff = new StringBuilder();
           // strBuff.append("INSERT INTO OFFLINE_REPORT_PROCESS(REPORT_PROCESS_ID,REPORT_ID,FILE_NAME,");
           // strBuff.append("REPORT_INITIATED_BY,EXECUTION_START_TIME,STATUS,");
            //strBuff.append("INSTANCE_ID,CREATED_ON,TOTAL_RECORDS");
         //  strBuff.append(",RPT_JSON_REQ )");
            //strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?) ");
            
            strBuff.append("INSERT INTO OFFLINE_REPORT_PROCESS(REPORT_PROCESS_ID,REPORT_ID,FILE_NAME,REPORT_INITIATED_BY,EXECUTION_START_TIME,STATUS,INSTANCE_ID,CREATED_ON,TOTAL_RECORDS,RPT_JSON_REQ)  VALUES (?,?,?,?,?,?,?,?,?,?) ");   
            String insertQuery = strBuff.toString();
            if(_log.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(insertQuery);
    			_log.debug(methodName, loggerValue);
    		}
          
            try( PreparedStatement  psmtInsert = p_con.prepareStatement(insertQuery);)
            {
           
          
            psmtInsert.setString(1, offlineReportReqVO.getReport_TaskID());
            psmtInsert.setString(2, offlineReportReqVO.getReport_ID());
            psmtInsert.setString(3, offlineReportReqVO.getFile_Name());
            psmtInsert.setString(4, offlineReportReqVO.getReport_initiatedBy());
            psmtInsert.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(new Date()));
            psmtInsert.setString(6, PretupsI.OFFLINE_STATUS_INITIATED);
            psmtInsert.setString(7, Constants.getProperty("INSTANCE_ID"));
            psmtInsert.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(new Date()));
            psmtInsert.setLong(9, 0l);
            String jsonstr = offlineReportReqVO.getRpt_JsonReq();
            if(dailyRptAnalysiQry.checkDB().equals(PretupsI.DB_ORACLE)) {
            Clob clob = p_con.createClob();
            clob.setString(1, jsonstr );
            psmtInsert.setClob(10,clob);
            }else {
            	//postgres
            	psmtInsert.setString(10,jsonstr);	
            }
            
            insertCount = psmtInsert.executeUpdate();
            p_con.commit();
            }
    
            
        /** sample code  	
        	 StringBuilder strBuff = new StringBuilder();
             strBuff.append("INSERT INTO temptest (REPORT_ID,REPORT_NAME)values(?,?)");
             String insertQuery = strBuff.toString();
             try( PreparedStatement  psmtInsert = p_con.prepareStatement(insertQuery);)
             {
             psmtInsert.setString(1, offlineReportReqVO.getReport_TaskID());
             psmtInsert.setString(2, offlineReportReqVO.getFile_Name());
             insertCount = psmtInsert.executeUpdate();
             }         
            */
            
            
            
          
        
        }// end of try
        catch (SQLException sqle) {
        	loggerValue.setLength(0); 
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OfflineReportDAO[addOfflineReportProcess]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } // end of catch
        catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OfflineReportDAO[addOfflineReportProcess]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } // end of catch
        finally {
            LogFactory.printLog(methodName, "Exiting: insertCount=" + insertCount, _log);
           
        } // end of finally

        return insertCount;
    }


    
    /**
     * Method to fetch report processor bean name by report ID.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param reportID
     *            
     * 
     * @return List<ViewOfflineReportStatusVO>
     * @throws BTSLBaseException
     */
    public List<ViewOfflineReportStatusVO> getAllOfflineReportProcessStatus(Connection p_con,String intiatedUserId) throws BTSLBaseException {
        final String methodName = "getAllOfflineReportProcessStatus";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.append("Get all offline Report process status");
        	_log.debug(methodName, loggerValue);
        }
        
        ViewOfflineReportStatusVO viewOfflineReportStatusVO = null;
        StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT  orp.REPORT_PROCESS_ID,rm.report_name,orp.EXECUTION_START_TIME , orp.EXECUTION_END_TIME, orp.FILE_NAME , u.user_name , ");
        strBuff.append( " ORP.STATUS,ORP.TOTAL_RECORDS FROM OFFLINE_REPORT_PROCESS orp ,REPORT_MASTER rm ,users u ");
        strBuff.append( "  WHERE orp.REPORT_ID =rm.report_id AND orp.REPORT_INITIATED_BY =u.user_id    and  orp.REPORT_INITIATED_BY=?  ORDER BY ORP.CREATED_ON DESC"); 

        
        List<ViewOfflineReportStatusVO> reportStatusList = null;    
        
 try {
        String sqlSelect = strBuff.toString();
        
        if(_log.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			_log.debug(methodName, loggerValue);
		}
        try( PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);)
        {
        	pstmtSelect.setString(1, intiatedUserId);
           try(ResultSet rs = pstmtSelect.executeQuery();)
	       {
        	   reportStatusList= new ArrayList<ViewOfflineReportStatusVO>();
	    	   while(rs.next()) {
	    		   viewOfflineReportStatusVO= new ViewOfflineReportStatusVO();
	    		   viewOfflineReportStatusVO.setReportProcessTaskID(rs.getString("REPORT_PROCESS_ID"));
	    		   viewOfflineReportStatusVO.setReportName(rs.getString("report_name"));
	    		   viewOfflineReportStatusVO.setInitiatedDateTime(String.valueOf(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("EXECUTION_START_TIME"), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATETIME_FORMAT))));
	    		   viewOfflineReportStatusVO.setFileName(rs.getString("FILE_NAME"));
	    		   viewOfflineReportStatusVO.setReportStatus(rs.getString("STATUS"));
	    		    String reportEndTime=rs.getTimestamp("EXECUTION_END_TIME")+"";
	    		    if(rs.getTimestamp("EXECUTION_END_TIME")!=null) {
		    		    if(reportEndTime!=null && reportEndTime.trim().length()>0) {
		    		    	viewOfflineReportStatusVO.setRptDowldCompletionTime(String.valueOf(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("EXECUTION_END_TIME"), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATETIME_FORMAT))));
		    		    }	
	    		    }
	    		    reportStatusList.add(viewOfflineReportStatusVO);
	    		   
	    		   }
	       }
       } 
        
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[isUserLoginExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[isUserLoginExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
        
        	LogFactory.printLog(methodName, "Exiting: Initiated userID="+intiatedUserId  , _log);
        }
    
 return reportStatusList;
    }


    
    
    /**
     * Method to update the report task execution status..
     * 
     * @param p_con
     *            java.sql.Connection
     * @param reportID
     *            
     * 
     * 
     * @throws BTSLBaseException
     */
    public void updateOfflineReportTaskStatus(Connection p_con,String status ,String reportTaskID,String totalNumberofRecords,boolean executionEndTimeupdate) throws BTSLBaseException {
        final String methodName = "updateOfflineReportTaskStatus";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.append("Get all offline Report process status");
        	_log.debug(methodName, loggerValue);
        }
        StringBuilder strBuff = new StringBuilder();
        if(executionEndTimeupdate) {
        strBuff.append(" update OFFLINE_REPORT_PROCESS    set STATUS = ? ,EXECUTION_END_TIME =? ,TOTAL_RECORDS= ? WHERE REPORT_PROCESS_ID=? ");
        }else {
        	strBuff.append(" update OFFLINE_REPORT_PROCESS    set STATUS = ?  WHERE REPORT_PROCESS_ID=? ");	
        }
        
 try {
        String sqlSelect = strBuff.toString();
        
        if(_log.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			_log.debug(methodName, loggerValue);
		}
        try( PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);)
        {
        	Date currentDate = new Date();
	    	pstmtSelect.setString(1, status);
	    	if(executionEndTimeupdate) {
	        	pstmtSelect.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(currentDate));
	        	if(totalNumberofRecords==null) {
	        		totalNumberofRecords="0";
	        	}
	        	pstmtSelect.setLong(3, Long.valueOf(totalNumberofRecords));
	        	pstmtSelect.setString(4, reportTaskID);
	    	} else {
	    		pstmtSelect.setString(2, reportTaskID);	
	    	}
        	
        	int updateDBStatus = pstmtSelect.executeUpdate();
        	p_con.commit();
       } 
        
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OfflineReportDAO[updateOfflineReportTaskStatus]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OfflineReportDAO[updateOfflineReportTaskStatus]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	LogFactory.printLog(methodName, "Exiting:  Update status for  taskID ="+reportTaskID+ " completed."  , _log);
        }
    
    
    }
    

    
    
    public int deleteOfflineReportTaskStatus(Connection p_con,String reportTaskID ) throws BTSLBaseException {
        final String methodName = "deleteOfflineReportTaskStatus";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.append("Get all offline Report process status");
        	_log.debug(methodName, loggerValue);
        }
        StringBuilder strBuff = new StringBuilder();
       
        strBuff.append(" delete from  OFFLINE_REPORT_PROCESS WHERE REPORT_PROCESS_ID=? ");
        int deleteStatus;
        
 try {
        String sqlSelect = strBuff.toString();
        
        if(_log.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			_log.debug(methodName, loggerValue);
		}
        
        try( PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);)
        {
        		pstmtSelect.setString(1, reportTaskID);	
	    	 deleteStatus = pstmtSelect.executeUpdate();
        	p_con.commit();
       } 
        
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OfflineReportDAO[updateOfflineReportTaskStatus]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OfflineReportDAO[updateOfflineReportTaskStatus]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	LogFactory.printLog(methodName, "Exiting:  Update status for  taskID ="+reportTaskID+ " completed."  , _log);
        }
    
    return  deleteStatus;
    }
    

    
    /**
     * Method to fetch Offline report task info by  report Task ID.
     * 
     * @param p_con
     *            java.sql.Connection
     * @param reportID
     *            
     * 
     * @return OfflineReportTaskIDInfo
     * @throws BTSLBaseException
     */
    public OfflineReportTaskIDInfo getOfflineReportTaskStatusInfo(Connection p_con,String reportTaskID) throws BTSLBaseException {
        final String methodName = "getOfflineReportTaskStatusInfo";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.append("Get Offline report task status by ID.");
        	_log.debug(methodName, loggerValue);
        }
        
        
        StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT REPORT_PROCESS_ID, REPORT_ID, FILE_NAME, REPORT_INITIATED_BY, EXECUTION_START_TIME, EXECUTION_END_TIME, STATUS, INSTANCE_ID, CREATED_ON, TOTAL_RECORDS, RPT_JSON_REQ");
        strBuff.append(" FROM OFFLINE_REPORT_PROCESS WHERE  REPORT_PROCESS_ID =? ");
        OfflineReportTaskIDInfo offlineReportTaskIDInfo = null;
        
 try {
        String sqlSelect = strBuff.toString();
        
        if(_log.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			_log.debug(methodName, loggerValue);
		}
        try( PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);)
        {
        	pstmtSelect.setString(1, reportTaskID);
           try(ResultSet rs = pstmtSelect.executeQuery();)
	       {
        	 
	    	   while(rs.next()) {
	    		   offlineReportTaskIDInfo=new OfflineReportTaskIDInfo();
	    		   offlineReportTaskIDInfo.setReportProcessTaskID(rs.getString("REPORT_PROCESS_ID"));
	    		   offlineReportTaskIDInfo.setInitiatedDateTime(String.valueOf(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("EXECUTION_START_TIME"), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATETIME_FORMAT))));
	    		   offlineReportTaskIDInfo.setFileName(rs.getString("FILE_NAME"));
	    		   offlineReportTaskIDInfo.setReportStatus(rs.getString("STATUS"));
	    		   if(rs.getTimestamp("EXECUTION_END_TIME")!=null) {
	    		    String reportEndTime=rs.getTimestamp("EXECUTION_END_TIME")+"";
		    		    if(reportEndTime!=null && reportEndTime.trim().length()>0) {
		    		    	offlineReportTaskIDInfo.setRptDowldCompletionTime(String.valueOf(BTSLUtil.getDateTimeStringFromDate(rs.getTimestamp("EXECUTION_END_TIME"), (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATETIME_FORMAT))));
		    		    }	
	    		   }
	    		   }
	       }
       } 
        
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[isUserLoginExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[isUserLoginExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
        
        	LogFactory.printLog(methodName, "Exiting: getOfflineReportTaskStatusInfo ="+reportTaskID  , _log);
        }
    
 return offlineReportTaskIDInfo;
    }


    
    
    /**
     * Check already Same report is in Progress...
     * 
     * @param p_con
     *            java.sql.Connection
     * @param reportID
     *            
     * 
     * @return checkSameReportAlreadyExecuting
     * @throws BTSLBaseException
     */
    public boolean checkSameReportAlreadyExecuting(Connection p_con,String reportID,String userID) throws BTSLBaseException {
        final String methodName = "checkSameReportAlreadyExecuting";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.append("Get Offline report task status by ID.");
        	_log.debug(methodName, loggerValue);
        }
        boolean reportExecutioninProgress=false; 
        
        StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT REPORT_PROCESS_ID, REPORT_ID, FILE_NAME, REPORT_INITIATED_BY, EXECUTION_START_TIME, EXECUTION_END_TIME, STATUS, INSTANCE_ID, CREATED_ON, TOTAL_RECORDS, RPT_JSON_REQ");
        strBuff.append(" FROM OFFLINE_REPORT_PROCESS WHERE  REPORT_ID =?  and REPORT_INITIATED_BY=?  and (STATUS='INPROGRESS' OR STATUS='INITIATED'  ) and to_char(CREATED_ON,'dd-mm-yyyy')=?  ORDER BY CREATED_ON DESC ");
        OfflineReportTaskIDInfo offlineReportTaskIDInfo = null;
        
 try {
        String sqlSelect = strBuff.toString();
        
        if(_log.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			_log.debug(methodName, loggerValue);
		}
        try( PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);)
        {
        	pstmtSelect.setString(1, reportID);
        	pstmtSelect.setString(2, userID);
        	//pstmtSelect.setString(3, PretupsI.OFFLINE_STATUS_INPROGRESS);
        	String currentDate = BTSLDateUtil.getDateInFormat(BTSLDateUtil.getSystemLocaleCurrentDate(), "dd-MM-yyyy");
        	pstmtSelect.setString(3,currentDate);
        	
           try(ResultSet rs = pstmtSelect.executeQuery();)
	       {
        	 
	    	   while(rs.next()) {
	    		   reportExecutioninProgress=true;
	    		   break;
	    		   }
	       }
       } 
        
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[isUserLoginExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[isUserLoginExist]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
        
        	LogFactory.printLog(methodName, "Exiting: checkSameReportAlreadyExecuting ="+reportID  , _log);
        }
    
 return reportExecutioninProgress;
    }

    /**
     * Check already Same report is in Progress...
     * 
     * @param p_con
     *            java.sql.Connection
     * @param userID
     *            
     * 
     * @return int
     * @throws BTSLBaseException
     */
    public int getTotCountRprtRunningParallelByUser(Connection p_con,String userID) throws BTSLBaseException {
        final String methodName = "checkSameReportAlreadyExecuting";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
        	loggerValue.append("Get Offline report task status by ID.");
        	_log.debug(methodName, loggerValue);
        }
        boolean reportExecutioninProgress=false; 
        
        StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT count(REPORT_PROCESS_ID) as totCount ");
        strBuff.append(" FROM OFFLINE_REPORT_PROCESS WHERE    REPORT_INITIATED_BY=?  and status=?  and to_char(CREATED_ON,'dd-mm-yyyy')=?  ");
        OfflineReportTaskIDInfo offlineReportTaskIDInfo = null;
        int totalExecCount=0;
 try {
        String sqlSelect = strBuff.toString();
        
        if(_log.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			_log.debug(methodName, loggerValue);
		}
        try( PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);)
        {
        
        	pstmtSelect.setString(1, userID);
        	pstmtSelect.setString(2, PretupsI.OFFLINE_STATUS_INPROGRESS);
        	pstmtSelect.setString(3, BTSLDateUtil.getDateInFormat(BTSLDateUtil.getSystemLocaleCurrentDate(), "dd-MM-yyyy"));
        	
           try(ResultSet rs = pstmtSelect.executeQuery();)
	       {
        	 
	    	   while(rs.next()) {
	    		   totalExecCount=rs.getInt("totCount");
	    		   
	    		   }
	       }
       } 
        
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OfflineDAO[getTotCountRprtRunningParallelByUser]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			_log.error(methodName, loggerValue);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OfflineDAO[getTotCountRprtRunningParallelByUser]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	
        
        	LogFactory.printLog(methodName, "Exiting: checkSameReportAlreadyExecuting ="+userID  , _log);
        }
    
 return totalExecCount;
    }
    
    
}


package com.btsl.pretups.processes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.LoanProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.LoanProfileDetailsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;

import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.user.requesthandler.LastLoanEnqRequestHandler;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserLoanDAO;
import com.btsl.user.businesslogic.UserLoanVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

public class DWHLoanData {
	
	private static String _dwhFileLabelForMaster = null;
	private static String _dwhFileNameForMaster = null;
	private static String _masterDirectoryPathAndName = null;
	private static String _finalMasterDirectoryPath = null;
	private static long _maxFileLength = 0;
	private static String _childDirectory = null;
    private static ProcessStatusVO _processStatusVO;
    private static ProcessBL _processBL = null;
    private static ArrayList _fileNameLst = new ArrayList();
	private static String _fileEXT = null;
	private static boolean _ambiUnderCheckRequired = true;
	private static Log _logger = LogFactory.getLog(DWHLoanData.class.getName());
	
	
	
	public static OperatorUtilI calculatorI = null;


	
	
	public static void main(String arg[]) {
        final String METHOD_NAME = "main";
        Date date = null;
        
        try {
            if (arg.length < 2) {
                System.out.println("Usage : DWHLoanData [Constants file] [LogConfig file]");
                return;
            }
            
            final File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                System.out.println("DWHFileCreation" + " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                System.out.println("DWHFileCreation" + " Logconfig File Not Found .............");
                return;
            }
            
            
            try {
                if (arg.length == 4) {
                    final String inputDate = arg[3];
                    if (!BTSLUtil.isNullString(inputDate)) {
                        date = BTSLUtil.getDateFromDateString(inputDate, "dd/MM/yyyy");
                        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        if (!date.before(BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(new Date())))) {
                            System.out.println("DWHFileCreation" + " process will execute only for current or previous date ");
                            return;
                           
                        }
                        System.out.println("DWHFileCreation" + " DWHFileCreation process is going to execute only till.............date:" + date);

                        System.out.println("DWHFileCreation" + " DWHFileCreation process is going to execute only for.............date:" + date);
                    }

                }
                
                if (arg.length >= 3) {
                    if (PretupsI.YES.equalsIgnoreCase(arg[2])) {
                    	   _ambiUnderCheckRequired = true;
                    } else {
                        _ambiUnderCheckRequired = false;
                    }
                }
                
                
            }catch (Exception e) {
                _ambiUnderCheckRequired = true;
                _logger.errorTrace(METHOD_NAME, e);
            }
            
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());

        }// end of try
        
        catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, " Error in Loading Files ...........................: " + e.getMessage());
            }
            _logger.errorTrace(METHOD_NAME, e);
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
        	
			final String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
			try {
				calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
			} catch (Exception e) {
				_logger.errorTrace(DWHLoanData.class, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHLoanData[initialize]", "", "", "",
						"Exception while loading the class at the call:" + e.getMessage());
			}
        	
        	
            process(date);
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting..... ");
            }
            ConfigServlet.destroyProcessCache();
        }
	}
	
	
	
	private static void process(Date p_date) throws BTSLBaseException {
        Date processedUpto = null;
        Date dateCount = null;
        Date currentDate = new Date();
        Connection con = null;
        String processId = null;
        boolean statusOk = false;
        ProcessStatusDAO processStatusDAO = null;
        int beforeInterval = 0;
        int maxDoneDateUpdateCount = 0;
        final String methodName = "process";
        long masterCount=0 ;
        String isSuccess=null;
        String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
        
        try {
        	_logger.debug(methodName, "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
        	currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);
            // getting all the required parameters from Constants.props
            loadConstantParameters();
            
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(methodName, " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[process]", "", "",
                    "", "DATABASE Connection is NULL");
                return;
            }
            // getting process id
            processId = ProcessI.DWHLOAN_PROCESSID;
            // method call to check status of the process
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            beforeInterval = (int) _processStatusVO.getBeforeInterval() / (60 * 24);
            
            
            if(p_date == null && statusOk) {
            	con.commit();
                // method call to find maximum date till which process has been
                // executed
                processedUpto = _processStatusVO.getExecutedUpto();
                
                if(processedUpto != null) {
                    // ID DWH002 to check whether process has been executed till
                    // current date or not
                    if (processedUpto.compareTo(currentDate) == 0) {
                        throw new BTSLBaseException("DWHFileCreation", methodName, PretupsErrorCodesI.DWH_PROCESS_ALREADY_EXECUTED_TILL_TODAY);
                    }
                    
                    // adding 1 in processed upto date as we have to start from
                    // the next day till which process has been executed
                    processedUpto = BTSLUtil.addDaysInUtilDate(processedUpto, 1);
                    
                    //dateCount = BTSLUtil.getSQLDateFromUtilDate(processedUpto);
                    
                    for (dateCount = BTSLUtil.getSQLDateFromUtilDate(processedUpto); dateCount.before(BTSLUtil.addDaysInUtilDate(currentDate, -beforeInterval)); dateCount = BTSLUtil
                            .addDaysInUtilDate(dateCount, 1)){
                    	
                    	
                	   
	                  
	
	                    _childDirectory = createDirectory(_masterDirectoryPathAndName, processId, dateCount);
	                    loadDWHLoanData(con, dateCount,  _childDirectory, _dwhFileNameForMaster, _dwhFileLabelForMaster, _fileEXT, 1);
	                    
	                    
	                    _processStatusVO.setExecutedUpto(dateCount);
	                    _processStatusVO.setExecutedOn(currentDate);
	                    processStatusDAO = new ProcessStatusDAO();
	                    maxDoneDateUpdateCount = processStatusDAO.updateProcessDetail(con, _processStatusVO);
	                
	
	                    // if the process is successful, transaction is
	                    // commit, else rollback
	                    if (maxDoneDateUpdateCount > 0) {
	                        moveFilesToFinalDirectory(_masterDirectoryPathAndName, _finalMasterDirectoryPath, processId, dateCount);
	                        con.commit();
	                    } else {
	                        deleteAllFiles();
	                        con.rollback();
	                        throw new BTSLBaseException("DWHLoanData", methodName, PretupsErrorCodesI.DWH_COULD_NOT_UPDATE_MAX_DONE_DATE);
	                    }
	                    // DWH002 sleep has been added after processing
	                    // records of one day
	                    Thread.sleep(500);
	                }
                }
            	
            	
            }
        
        
        
        
        }
        catch (BTSLBaseException be) {
            _logger.error(methodName, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(methodName, be);
            throw be;
        
        }
        catch (Exception e) {
            if (_fileNameLst.size() > 0) {
                deleteAllFiles();
            }
            _logger.error(methodName, "Exception : " + e.getMessage());
            _logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DWHLoanData[process]", "", "", "",
                " DWHLoanData process could not be executed successfully.");
            throw new BTSLBaseException("DWHLoanData", methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }
        finally {


            // if the status was marked as under process by this method call,
            // only then it is marked as complete on termination
            if (statusOk) {
                try {
                    if (markProcessStatusAsComplete(con, processId) == 1) {
                        try {
                            con.commit();
                        } catch (Exception e) {
                            _logger.errorTrace(methodName, e);
                        }
                    } else {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            _logger.errorTrace(methodName, e);
                        }
                    }
                } catch (Exception e) {
                    _logger.errorTrace(methodName, e);
                }
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (Exception ex) {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug(methodName, "Exception closing connection ");
                    }
                    _logger.errorTrace(methodName, ex);
                }
            }

            _logger.debug(methodName, "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "Exiting..... ");
            }
        }
        
    }
	
	
    private static void loadConstantParameters() throws BTSLBaseException {
        final String METHOD_NAME = "loadConstantParameters";
        if (_logger.isDebugEnabled()) {
            _logger.debug("loadParameters", " Entered: ");
        }
        try {

        	_dwhFileLabelForMaster = Constants.getProperty("DWH_LOAN_FILE_LABEL");
            if (BTSLUtil.isNullString(_dwhFileLabelForMaster)) {
                _logger.error(METHOD_NAME, " Could not find file label for master data in the Constants file.");
            } else {
                _logger.debug(METHOD_NAME, " _dwhFileLabelForMaster=" + _dwhFileLabelForMaster);
            }

            _dwhFileNameForMaster = Constants.getProperty("DWH_LOAN_FILE_NAME");
            if (BTSLUtil.isNullString(_dwhFileNameForMaster)) {
                _logger.error(METHOD_NAME, " Could not find file name for master data in the Constants file.");
            } else {
                _logger.debug(METHOD_NAME, " _dwhFileNameForMaster=" + _dwhFileNameForMaster);
            }

//            _masterDirectoryPathAndName = Constants.getProperty("DWH_MASTER_DIRECTORY");
            _masterDirectoryPathAndName = Constants.getProperty("DWH_LOAN_DIRECTORY");
            if (BTSLUtil.isNullString(_masterDirectoryPathAndName)) {
                _logger.error(METHOD_NAME, " Could not find directory path in the Constants file.");
            } else {
                _logger.debug(METHOD_NAME, " _masterDirectoryPathAndName=" + _masterDirectoryPathAndName);
            }
            _finalMasterDirectoryPath = Constants.getProperty("DWH_FINAL_DIRECTORY");
            if (BTSLUtil.isNullString(_finalMasterDirectoryPath)) {
                _logger.error(METHOD_NAME, " Could not find final directory path in the Constants file.");
            } else {
                _logger.debug(METHOD_NAME, " finalMasterDirectoryPath=" + _finalMasterDirectoryPath);
            }

            if (BTSLUtil.isNullString(_dwhFileLabelForMaster)  || BTSLUtil.isNullString(_dwhFileNameForMaster) || BTSLUtil.isNullString(_masterDirectoryPathAndName) || BTSLUtil.isNullString(_finalMasterDirectoryPath)) {
                throw new BTSLBaseException("DWHFileCreation", "loadConstantParameters", PretupsErrorCodesI.DWH_COULD_NOT_FIND_DATA_IN_CONSTANTS_FILE);
            }
            try {
                _fileEXT = Constants.getProperty("DWH_FILE_EXT");
            } catch (Exception e) {
                _fileEXT = ".csv";
                _logger.errorTrace(METHOD_NAME, e);
            }
            _logger.debug(METHOD_NAME, " _fileEXT=" + _fileEXT);
            try {
                _maxFileLength = Long.parseLong(Constants.getProperty("DWH_MAX_FILE_LENGTH"));
            } catch (Exception e) {
                _maxFileLength = 1000;
                _logger.errorTrace(METHOD_NAME, e);
            }
            _logger.debug(METHOD_NAME, " _maxFileLength=" + _maxFileLength);
            _logger.debug(METHOD_NAME, " Required information successfuly loaded from Constants.props...............: ");
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[loadConstantParameters]", "", "", "",
                "Message:" + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            final BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[loadConstantParameters]", "", "", "",
                "Message:" + btslMessage);
            throw new BTSLBaseException("DWHFileCreation", METHOD_NAME, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }

    }
    
    public static void loadDWHLoanData(Connection con, Date p_beingProcessedDate,String p_dirPath, String p_fileName, String p_fileLabel, String p_fileEXT, int p_fileNumber) throws BTSLBaseException{
		
		
		final String methodName = "loadDWHLoanData";
		if(_logger.isDebugEnabled()) {
			_logger.debug(methodName, "Entered date= " + p_beingProcessedDate);
		}
		
		//ArrayList<DWHLoanVO> userLoanList = new ArrayList();
		PreparedStatement pstmtSelect1 = null;
		PreparedStatement pstmtSelect2 = null;
		ResultSet rs1 = null;
		ResultSet rs2= null;
		
		try {
    		System.out.println("MYDATE:" + BTSLUtil.getTimestampFromUtilDate(p_beingProcessedDate));
    		System.out.println("MYDATE+1:" + BTSLUtil.getTimestampFromUtilDate(BTSLUtil.addDaysInUtilDate(p_beingProcessedDate, 1)));
			
    		
			StringBuilder sb = new StringBuilder("SELECT CULI.PROFILE_ID, CULI.SETTLEMENT_ID Settlement_id, CULI.LAST_LOAN_TXN_ID, CULI.LAST_LOAN_DATE , gp.user_name Grand_parent_name, gp.msisdn Grand_parent_msisdn, PRNT_USR.user_name parent_name, PRNT_USR.msisdn parent_msisdn, up.MSISDN retailer_msisdn,");
			sb.append(" CULI.SETTLEMENT_DATE Settlement_date_time, CULI.LOAN_THREHOLD  Alerting_balance, CULI.LOAN_AMOUNT, NVL(TO_CHAR(cul.LOAN_GIVEN), 'N') LOAN_GIVEN , CULI.BALANCE_BEFORE_LOAN , u.user_id, CULI.product_code, CULI.SETTLEMENT_LOAN_AMOUNT, CULI.SETTLEMENT_LOAN_INTEREST ");
			sb.append(" FROM CHANNEL_USER_LOAN_INFO cul FULL OUTER JOIN CHANNEL_USER_LOAN_INFO_HISTORY culi  ON cul.LAST_LOAN_TXN_ID  = culi.LAST_LOAN_TXN_ID AND  cul.USER_ID =culi.USER_ID ,users u , users PRNT_USR, users gp, user_phones up");
			sb.append(" WHERE culi.USER_ID =u.USER_ID AND u.STATUS <> 'N' AND u.STATUS <> 'C'");
			sb.append(" AND u.user_id=up.user_id");
			sb.append(" AND PRNT_USR.user_id = (CASE u.parent_id WHEN 'ROOT' THEN u.user_id ELSE u.parent_id END)  AND  gp.user_id = (CASE PRNT_USR.parent_id WHEN 'ROOT' THEN PRNT_USR.user_id ELSE PRNT_USR.parent_id END)");
			sb.append(" AND (CULI.LAST_LOAN_DATE>= ? AND CULI.LAST_LOAN_DATE< ?) AND culi.TRANS_TYPE ='L' ");
			
			
			StringBuilder sb2 = new StringBuilder("SELECT CULI.PROFILE_ID, CULI.SETTLEMENT_ID Settlement_id, CULI.LAST_LOAN_TXN_ID, CULI.LAST_LOAN_DATE , gp.user_name Grand_parent_name, gp.msisdn Grand_parent_msisdn, PRNT_USR.user_name parent_name, PRNT_USR.msisdn parent_msisdn, up.MSISDN retailer_msisdn,");
			sb2.append(" CULI.SETTLEMENT_DATE Settlement_date_time, CULI.LOAN_THREHOLD  Alerting_balance, CULI.LOAN_AMOUNT, CULI.LOAN_GIVEN , CULI.BALANCE_BEFORE_LOAN , u.user_id, CULI.product_code, CULI.SETTLEMENT_LOAN_AMOUNT, CULI.SETTLEMENT_LOAN_INTEREST ");
			sb2.append(" FROM CHANNEL_USER_LOAN_INFO_HISTORY culi ,users u , users PRNT_USR, users gp, user_phones up");
			sb2.append(" WHERE culi.USER_ID =u.USER_ID AND u.STATUS <> 'N' AND u.STATUS <> 'C'");
			sb2.append(" AND u.user_id=up.user_id");
			sb2.append(" AND PRNT_USR.user_id = (CASE u.parent_id WHEN 'ROOT' THEN u.user_id ELSE u.parent_id END)  AND  gp.user_id = (CASE PRNT_USR.parent_id WHEN 'ROOT' THEN PRNT_USR.user_id ELSE PRNT_USR.parent_id END)");
			sb2.append(" AND (CULI.SETTLEMENT_DATE>= ? AND CULI.SETTLEMENT_DATE< ?) AND culi.TRANS_TYPE ='S' ");
			
			
			String selectQuery = sb.toString();
			String selectQuery2 = sb2.toString();
    		if(_logger.isDebugEnabled()) {
    			_logger.debug(methodName, "SelectQry= " + selectQuery);
    		}
    		
    		if(_logger.isDebugEnabled()) {
    			_logger.debug(methodName, "SelectQry= " + selectQuery2);
    		}
    		
    		pstmtSelect1 = con.prepareStatement(selectQuery);
    		pstmtSelect2 = con.prepareStatement(selectQuery2);
    		int i = 1;
    		
    		Date d1 = p_beingProcessedDate;
    		Date d2 = BTSLUtil.addDaysInUtilDate(p_beingProcessedDate, 1);
    		
    		pstmtSelect1.setTimestamp(i++,  BTSLUtil.getTimestampFromUtilDate(d1));
    		pstmtSelect1.setTimestamp(i++,  BTSLUtil.getTimestampFromUtilDate(d2));
    		int j = 1;
    		pstmtSelect2.setTimestamp(j++,  BTSLUtil.getTimestampFromUtilDate(d1));
    		pstmtSelect2.setTimestamp(j++,  BTSLUtil.getTimestampFromUtilDate(d2));
    		rs1 = pstmtSelect1.executeQuery();
    		rs2 = pstmtSelect2.executeQuery();
    		
            _logger.debug("loadDWHLoanData", "Memory after loading master data: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime
                    .getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
            
            // method call to write data in files
            writeDataInFile(con, p_dirPath, p_fileName, p_fileLabel, p_beingProcessedDate, p_fileEXT, rs1, rs2, p_fileNumber);
            
            
            _logger.debug("loadDWHLoanData", "Memory after writing master files: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime
                .getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
    		
		}
		
        catch (SQLException sqle) {
            _logger.error(methodName, "SQLException: " + sqle.getMessage());
            _logger.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHLoanDataDAO", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("DWHLoanData", methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            _logger.error(methodName, "Exception: " + e.getMessage());
            _logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHLoanDataDAO", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("DWHLoanData", methodName, "error.general.processing");
        } // end of catch
        finally {
        	OracleUtil.closeQuietly(rs1);
            OracleUtil.closeQuietly(pstmtSelect1);
        	OracleUtil.closeQuietly(rs2);
            OracleUtil.closeQuietly(pstmtSelect2);

            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, "Exiting:");
            }
        } // end of finally
		
		
	}
    




	private static String createDirectory(String p_directoryPathAndName, String p_processId, Date p_beingProcessedDate) throws BTSLBaseException {
        final String METHOD_NAME = "createDirectory";
        if (_logger.isDebugEnabled()) {
            _logger.debug("createDirectory",
                " Entered: p_directoryPathAndName=" + p_directoryPathAndName + " p_processId=" + p_processId + " p_beingProcessedDate=" + p_beingProcessedDate);
        }
        String dirName = null;
        try {
            boolean success = false;
            final File parentDir = new File(p_directoryPathAndName);
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            p_beingProcessedDate = BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate);
            // child directory name includes a file name and being processed
            // date, month and year
            dirName = p_directoryPathAndName + File.separator + p_processId + "_" + p_beingProcessedDate.toString().substring(8, 10) + p_beingProcessedDate.toString()
                .substring(5, 7) + p_beingProcessedDate.toString().substring(2, 4);
            final File newDir = new File(dirName);
            if (!newDir.exists()) {
                success = newDir.mkdirs();
            } else {
                success = true;
            }
            if (!success) {
                throw new BTSLBaseException("DWHFileCreation", "createDirectory", PretupsErrorCodesI.COULD_NOT_CREATE_DIR);
            }
        } catch (BTSLBaseException be) {
            _logger.error("createDirectory", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception ex) {
            _logger.error("createDirectory", "Exception : " + ex.getMessage());
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[createDirectory]", "", "", "",
                "SQLException:" + ex.getMessage());
            throw new BTSLBaseException("DWHFileCreation", "createDirectory", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("createDirectory", "Exiting dirName=" + dirName);
            }
        }
        return dirName;
    }
	
	
    private static void writeDataInFile(Connection con, String p_dirPath, String p_fileName, String p_fileLabel,
			Date p_beingProcessedDate, String p_fileEXT, ResultSet rs1, ResultSet rs2, int p_fileNumber) throws BTSLBaseException{
        if (_logger.isDebugEnabled()) {
            _logger
                .debug(
                    "writeDataInFile",
                    " Entered:  p_dirPath=" + p_dirPath + " p_fileName=" + p_fileName + " p_fileLabel=" + p_fileLabel + " p_beingProcessedDate=" + p_beingProcessedDate + " p_fileEXT=" + p_fileEXT + "p_fileNumber=" + p_fileNumber);
        }
        final String METHOD_NAME = "writeDataInFile";
        long recordsWrittenInFile = 0;
        PrintWriter out = null;
        int fileNumber = 1;
        String fileName = null;
        File newFile = null;
        String fileData = null;
        String fileHeader = null;
        String fileFooter = null;
        
        try {
        	SimpleDateFormat sm1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        	java.text.SimpleDateFormat source = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
            // generating file name
            fileNumber = p_fileNumber;
            // if the length of file number is 1, two zeros are added as prefix
            if (Integer.toString(fileNumber).length() == 1) {
                fileName = p_dirPath + File.separator + p_fileName + "00" + fileNumber + p_fileEXT;
            } else if (Integer.toString(fileNumber).length() == 2) {
                fileName = p_dirPath + File.separator + p_fileName + "0" + fileNumber + p_fileEXT;
            } else if (Integer.toString(fileNumber).length() == 3) {
                fileName = p_dirPath + File.separator + p_fileName + fileNumber + p_fileEXT;
            }
            
            _logger.debug(METHOD_NAME, "  fileName=" + fileName);
    		
            newFile = new File(fileName);
            _fileNameLst.add(fileName);
            out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
    	
            if ("Y".equalsIgnoreCase(Constants.getProperty("ADD_HEADER_FOOTER"))) {
                fileHeader = constructFileHeader(p_beingProcessedDate, fileNumber, p_fileLabel);
                out.write(fileHeader);
            }
            
            // traverse first resultset
            
            while(rs1 != null && rs1.next()) {
            	
            	long loanAmt = rs1.getLong("LOAN_AMOUNT");
            	long alertingBal = rs1.getLong("Alerting_balance");
            	long balBeforeLoan = rs1.getLong("BALANCE_BEFORE_LOAN");
            	String loanGiven = rs1.getString("LOAN_GIVEN");
            	
            	
            	out.write("Loan" + ",");
            	out.write(rs1.getString("LAST_LOAN_TXN_ID") + ",");
            	out.write(rs1.getString("Grand_parent_msisdn") + ",");
            	out.write(rs1.getString("Grand_parent_name") + ",");
            	out.write(rs1.getString("parent_msisdn") + ",");
            	out.write(rs1.getString("parent_name") + ",");
            	out.write(rs1.getString("retailer_msisdn") + ",");
            	java.util.Date date = source.parse(rs1.getString("LAST_LOAN_DATE") );
				
				String strDate = sm1.format(date);
			
            	out.write(strDate + ",");
            	out.write(PretupsBL.getDisplayAmount(alertingBal) + ",");
            	out.write(PretupsBL.getDisplayAmount(balBeforeLoan) + ",");
            	
            	out.write(PretupsBL.getDisplayAmount(loanAmt) + ",");
            	//premium
	        	//calculate premium till date
            	String userId = rs1.getString("USER_ID");
				LoanProfileDAO  loanProfileDAO= new LoanProfileDAO();
				
				UserLoanVO vo = new UserLoanVO();
				vo.setLast_loan_date(rs1.getTimestamp("LAST_LOAN_DATE"));
				vo.setLoan_amount(Long.parseLong(PretupsBL.getDisplayAmount(loanAmt)));
				vo.setProfile_id(Integer.parseInt(rs1.getString("PROFILE_ID")));
				vo.setUser_id(userId);
				if(PretupsI.AGENT_ALLOWED_YES.equals(loanGiven)) {
				ArrayList<LoanProfileDetailsVO>  loanProfileList = loanProfileDAO.loadLoanProfileSlabs(con, String.valueOf(vo.getProfile_id()));
	        	

				
				long premium = calculatorI.calculatePremium(vo, loanProfileList);
	            //end
            	
            	out.write(String.valueOf(premium) + ",");
            	//total
            	long totalAmtDue = Long.parseLong(PretupsBL.getDisplayAmount(loanAmt)) + premium;
            	out.write(String.valueOf(totalAmtDue) + ",");
            	
				}
				else {
					out.write(PretupsBL.getDisplayAmount(rs1.getLong("SETTLEMENT_LOAN_INTEREST")) + ",");
					out.write(PretupsBL.getDisplayAmount(rs1.getLong("SETTLEMENT_LOAN_AMOUNT")) + ",");
				}
            	//out.write("Credit Received transaction id" + ",");
            	java.util.Date settlementDate = source.parse(rs1.getString("Settlement_date_time") );
				
			
            	out.write(sm1.format(settlementDate) + ",");
            	
            	//out.write(rs1.getTimestamp("Settlement_date_time") + ",");
            	out.write(rs1.getString("Settlement_id") + "\n");
            	
            	out.flush();
            	recordsWrittenInFile++;
            	
            	
            }
            
            
            
            // traverse second resultset
            while(rs2 != null && rs2.next()) {
            	//System.out.println("inside rs2 loop");
            	
            	long loanAmt = rs2.getLong("LOAN_AMOUNT");
            	long alertingBal = rs2.getLong("Alerting_balance");
            	long balBeforeLoan = rs2.getLong("BALANCE_BEFORE_LOAN");
            	
            	out.write("Settlement" + ",");
            	out.write(rs2.getString("LAST_LOAN_TXN_ID") + ",");
            	out.write(rs2.getString("Grand_parent_msisdn") + ",");
            	out.write(rs2.getString("Grand_parent_name") + ",");
            	out.write(rs2.getString("parent_msisdn") + ",");
            	out.write(rs2.getString("parent_name") + ",");
            	out.write(rs2.getString("retailer_msisdn") + ",");
            	java.util.Date date = source.parse(rs2.getString("LAST_LOAN_DATE") );
				String strDate = sm1.format(date);
			
            	out.write(strDate + ",");
            	out.write(PretupsBL.getDisplayAmount(alertingBal) + ",");
            	out.write(PretupsBL.getDisplayAmount(balBeforeLoan) + ",");
            	
            	out.write(PretupsBL.getDisplayAmount(loanAmt) + ",");
            	//premium
	        	//calculate premium till date
            	String userId = rs2.getString("USER_ID");
				LoanProfileDAO  loanProfileDAO= new LoanProfileDAO();
				
				UserLoanVO vo = new UserLoanVO();
				vo.setLast_loan_date(rs2.getTimestamp("LAST_LOAN_DATE"));
				vo.setLoan_amount(Long.parseLong(PretupsBL.getDisplayAmount(loanAmt)));
				vo.setProfile_id(Integer.parseInt(rs2.getString("PROFILE_ID")));
				vo.setUser_id(userId);
				out.write(PretupsBL.getDisplayAmount(rs2.getLong("SETTLEMENT_LOAN_INTEREST")) + ",");
				out.write(PretupsBL.getDisplayAmount(rs2.getLong("SETTLEMENT_LOAN_AMOUNT")) + ",");
		
            	
            	//out.write("Credit Received transaction id" + ",");
            	//out.write(rs2.getTimestamp("Settlement_date_time") + ",");
            	
				java.util.Date settlementDate = source.parse(rs2.getString("Settlement_date_time") );
				
				
            	out.write(sm1.format(settlementDate) + ",");
            
				out.write(rs2.getString("Settlement_id") + "\n");
            	
            	out.flush();
            	recordsWrittenInFile++;
            	
            	
            }
            
        }catch (SQLException e) {
            deleteAllFiles();
            _logger.debug(METHOD_NAME, "SQLException: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[writeDataInFile]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("DWHLoanData", METHOD_NAME, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } catch (IOException e) {
            deleteAllFiles();
            _logger.debug(METHOD_NAME, "IOException: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[writeDataInFile]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("DWHLoanData", METHOD_NAME, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } catch (Exception e) {
            deleteAllFiles();
            _logger.debug(METHOD_NAME, "Exception: " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[writeDataInFile]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("DWHLoanData", METHOD_NAME, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (out != null) {
                out.close();
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting ");
            }
        }
        

        
        
        
    }
    
    
    private static String constructFileHeader(Date p_beingProcessedDate, long p_fileNumber, String p_fileLabel) {
        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        final StringBuffer fileHeaderBuf = new StringBuffer("");
        fileHeaderBuf.append("Present Date=" + sdf.format(new Date()));
        fileHeaderBuf.append("\n" + "For Date=" + sdf.format(p_beingProcessedDate));
        fileHeaderBuf.append("\n" + "File Number=" + p_fileNumber);
        fileHeaderBuf.append("\n" + p_fileLabel);
        fileHeaderBuf.append("\n" + "[STARTDATA]" + "\n");
        return fileHeaderBuf.toString();
    }
    
    private static void moveFilesToFinalDirectory(String p_oldDirectoryPath, String p_finalDirectoryPath, String p_processId, Date p_beingProcessedDate) throws BTSLBaseException {
        final String METHOD_NAME = "moveFilesToFinalDirectory";
        if (_logger.isDebugEnabled()) {
            _logger
                .debug(
                    "moveFilesToFinalDirectory",
                    " Entered: p_oldDirectoryPath=" + p_oldDirectoryPath + " p_finalDirectoryPath=" + p_finalDirectoryPath + " p_processId=" + p_processId + " p_beingProcessedDate=" + p_beingProcessedDate);
        }

        String oldFileName = null;
        String newFileName = null;
        File oldFile = null;
        File newFile = null;
        File parentDir = new File(p_finalDirectoryPath);
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        p_beingProcessedDate = BTSLUtil.getSQLDateFromUtilDate(p_beingProcessedDate);
        // child directory name includes a file name and being processed date,
        // month and year
        final String oldDirName = p_oldDirectoryPath + File.separator + p_processId + "_" + p_beingProcessedDate.toString().substring(8, 10) + p_beingProcessedDate.toString()
            .substring(5, 7) + p_beingProcessedDate.toString().substring(2, 4);
        final String newDirName = p_finalDirectoryPath + File.separator + p_processId + "_" + p_beingProcessedDate.toString().substring(8, 10) + p_beingProcessedDate
            .toString().substring(5, 7) + p_beingProcessedDate.toString().substring(2, 4);
        File oldDir = new File(oldDirName);
        File newDir = new File(newDirName);
        if (!newDir.exists()) {
            newDir.mkdirs();
        }

        if (_logger.isDebugEnabled()) {
            _logger.debug("moveFilesToFinalDirectory", " newDirName=" + newDirName);
        }

        final int size = _fileNameLst.size();
        try {
            for (int i = 0; i < size; i++) {
                oldFileName = (String) _fileNameLst.get(i);
                oldFile = new File(oldFileName);
                newFileName = oldFileName.replace(p_oldDirectoryPath, p_finalDirectoryPath);
                newFile = new File(newFileName);
                oldFile.renameTo(newFile);
                if (_logger.isDebugEnabled()) {
                    _logger.debug("moveFilesToFinalDirectory", " File " + oldFileName + " is moved to " + newFileName);
                }
            }// end of for loop
            _fileNameLst.clear();
            if (oldDir.exists()) {
                oldDir.delete();
            }
            _logger.debug("moveFilesToFinalDirectory", " File " + oldFileName + " is moved to " + newFileName);
        } catch (Exception e) {
            _logger.error("moveFilesToFinalDirectory", "Exception " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[moveFilesToFinalDirectory]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("DWHFileCreation", "deleteAllFiles", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (oldFile != null) {
                oldFile = null;
            }
            if (newFile != null) {
                newFile = null;
            }
            if (parentDir != null) {
                parentDir = null;
            }
            if (newDir != null) {
                newDir = null;
            }
            if (oldDir != null) {
                oldDir = null;
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("moveFilesToFinalDirectory", "Exiting.. ");
            }
        } // end of finally
    }

    private static void deleteAllFiles() throws BTSLBaseException {
        final String METHOD_NAME = "deleteAllFiles";
        if (_logger.isDebugEnabled()) {
            _logger.debug("deleteAllFiles", " Entered: ");
        }
        int size = 0;
        if (_fileNameLst != null) {
            size = _fileNameLst.size();
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug("deleteAllFiles", " : Number of files to be deleted " + size);
        }
        String fileName = null;
        File newFile = null;
        for (int i = 0; i < size; i++) {
            try {
                fileName = (String) _fileNameLst.get(i);
                newFile = new File(fileName);
                newFile.delete();
                if (_logger.isDebugEnabled()) {
                    _logger.debug("", fileName + " file deleted");
                }
            } catch (Exception e) {
                _logger.error("deleteAllFiles", "Exception " + e.getMessage());
                _logger.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[deleteAllFiles]", "", "", "",
                    "Exception:" + e.getMessage());
                throw new BTSLBaseException("DWHFileCreation", "deleteAllFiles", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
            }
        }// end of for loop
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[deleteAllFiles]", "", "", "",
            " Message: DWHFileCreation process has found some error, so deleting all the files.");
        if (_fileNameLst != null && _fileNameLst.isEmpty()) {
            _fileNameLst.clear();
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug("deleteAllFiles", " : Exiting.............................");
        }
    }
    
    
    private static int markProcessStatusAsComplete(Connection p_con, String p_processId) throws BTSLBaseException {
        final String METHOD_NAME = "markProcessStatusAsComplete";
        if (_logger.isDebugEnabled()) {
            _logger.debug("markProcessStatusAsComplete", " Entered:  p_processId:" + p_processId);
        }
        int updateCount = 0;
        final Date currentDate = new Date();
        final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        _processStatusVO.setProcessID(p_processId);
        _processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        _processStatusVO.setStartDate(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetail(p_con, _processStatusVO);
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("markProcessStatusAsComplete", "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DWHFileCreation[markProcessStatusAsComplete]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("DWHFileCreation", "markProcessStatusAsComplete", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;

    }
}

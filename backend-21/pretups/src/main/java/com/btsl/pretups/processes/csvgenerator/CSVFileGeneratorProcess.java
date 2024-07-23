package com.btsl.pretups.processes.csvgenerator;

/**
 * @(#)CSVFileGeneratorProcess
 *                             Copyright(c) 2006, Bharti Telesoft Ltd.
 *                             All Rights Reserved
 * 
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Author Date History
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 *                             Shishupal Singh 22/06/2011 Initial Creation
 *                             ------------------------------------------------
 *                             -------------------------------------------------
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.DateSorting;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.btsl.util.SqlParameterEncoder;
import com.ibm.icu.util.Calendar;

public class CSVFileGeneratorProcess {
    private static ArrayList fileNameLst = new ArrayList();
    private static ProcessStatusVO processStatusVO;
    private static ProcessBL processBL = null;
    private static HashMap csvMap = new HashMap();
    private static Properties csvproperties = new Properties();

    // C:\\jakarta-tomcat-5.5.6\\jakarta-tomcat-5.5.7\\webapps\\nigeria\\WEB-INF\\classes\\configfiles\\Constants.props
    // C:\\jakarta-tomcat-5.5.6\\jakarta-tomcat-5.5.7\\webapps\\nigeria\\WEB-INF\\classes\\configfiles\\LogConfig.props
    // C:\\jakarta-tomcat-5.5.6\\jakarta-tomcat-5.5.7\\webapps\\nigeria\\WEB-INF\\classes\\configfiles\\csvConfigFile.props
    private static Log logger = LogFactory.getLog(CSVFileGeneratorProcess.class.getName());
    // 07-MAR-2014 for OCI client
    private static String finalMasterDirectoryPath = null;// use to store the
                                                           // final master
                                                           // directory path in
                                                           // which the master
                                                           // and transaction
                                                           // data files will be
                                                           // moved after all
                                                           // files creation
    private static String fileEXT = ".csv";// use to store the extension of the
                                            // files, which are going to create
                                            // by the process
    private static Hashtable<String, Long> fileNameMap = null;
    private static TreeMap<String, Object> fileRecordMap = null;
    private static String processId = null;
    private static final String processName = "CSVFileGeneratorProcess";

    // Ended Here
    
    /**
     * to ensure no class instantiation 
     */
    private CSVFileGeneratorProcess(){
    	
    }
    public static void main(String arg[]) {
        final String methodName = "main";
        try {
            if ((arg.length != 3) && (arg.length != 2)) {
            	if (logger.isDebugEnabled())
            		logger.debug("main" , "Usage :"+ processName +" [Constants file] [LogConfig file] [csvConfigFile file]");
                    return;
            }
            File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
            	if (logger.isDebugEnabled())
            		logger.debug("main", processName + " Constants File Not Found .............");
                return;
            }
            File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
            	if (logger.isDebugEnabled())
            		logger.debug("main",processName + " Logconfig File Not Found .............");
                return;
            }

            File csvConfigFile = new File(arg[2]);
            if (!csvConfigFile.exists()) {
            	if (logger.isDebugEnabled())
            	logger.debug("main",processName+ " csvConfigFile.props File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            try(FileInputStream fileInputStream=new FileInputStream(csvConfigFile))
            {
            csvproperties.load(fileInputStream);
            }

        } catch (Exception e) {
            if (logger.isDebugEnabled())
                logger.debug("main", " Error in Loading Files ...........................: " + e.getMessage());
            logger.errorTrace(methodName, e);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            process();
        } catch (BTSLBaseException be) {
            logger.error("main", "BTSLBaseException : " + be.getMessage());
            logger.errorTrace(methodName, be);
        } finally {
            if (logger.isDebugEnabled())
                logger.debug("main", "Exiting..... ");
            ConfigServlet.destroyProcessCache();
        }
    }

    private static void process() throws BTSLBaseException {
        final String methodName = "process";
        Date processedUpto = null;
        Date currentDateTime = new Date();
        Connection con = null;
        boolean statusOk = false;

        try {
            // 07-MAR-2014 for OCI client
            fileNameMap = new Hashtable<String, Long>();
            fileRecordMap = new TreeMap<String, Object>(new DateSorting());
            // Ended Here

            logger.debug(methodName, "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            Calendar cal = BTSLDateUtil.getInstance();
            currentDateTime = cal.getTime(); // Current Date
            // getting all the required parameters from csvconfigfile.props
            loadConstantParameters();

            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (logger.isDebugEnabled())
                    logger.debug(methodName, " DATABASE Connection is NULL ");
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, processName+"[process]", "", "", "", "DATABASE Connection is NULL");
                return;
            }
            processId = "CSVGENERATOR";
            processBL = new ProcessBL();
            processStatusVO = processBL.checkProcessUnderProcess(con, processId);
            statusOk = processStatusVO.isStatusOkBool();
            if (statusOk) {
                con.commit();
                processedUpto = processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    CSVFileVO csvfilevo = null;
                    // method call to create master directory and child
                    // directory if does not exist
                    Iterator itr = csvMap.keySet().iterator();
                    try {
                        while (itr.hasNext()) {
                            csvfilevo = (CSVFileVO) csvMap.get((String) itr.next());
                            fetchQuery(con, processedUpto, csvfilevo.getDirName(), csvfilevo.getPrefixName(), csvfilevo.getHeaderName(), csvfilevo.getExtName(), csvfilevo.getQueryName(), csvfilevo.getTempTable(), Long.parseLong(csvproperties.getProperty("MAX_ROWS")));
                            Thread.sleep(500);
                        }
                    } catch (Exception e) {
                        logger.errorTrace(methodName, e);
                        logger.error(methodName, "csvfilevo=" + csvfilevo.toString() + "  Exception : " + e.getMessage());
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, processName+"[process]", "", "", "", "csvfilevo=" + csvfilevo.toString() + " Exception =" + e.getMessage());
                    }
                    processStatusVO.setExecutedUpto(currentDateTime);
                    processStatusVO.setExecutedOn(currentDateTime);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, processName+"[process]", "", "", "", " CSVFileGeneratorProcess process has been executed successfully.");
                } else
                    throw new BTSLBaseException(processName, methodName, PretupsErrorCodesI.DWH_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
            }
        } catch (BTSLBaseException be) {
            logger.error(methodName, "BTSLBaseException : " + be.getMessage());
            logger.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            if (!(fileNameLst.isEmpty()))
                deleteAllFiles();
            logger.error(methodName, "Exception : " + e.getMessage());
            logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, processName+"[process]", "", "", "", " Exception =" + e.getMessage());
            throw new BTSLBaseException(processName, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            // 07-MAR-2014 for OCI client
            Iterator<String> itr = csvMap.keySet().iterator();
            while (itr.hasNext()) {
                CSVFileVO csvfilevo = (CSVFileVO) csvMap.get((String) itr.next());
                finalMasterDirectoryPath = csvfilevo.getDirName();
                if (!BTSLUtil.isNullString(csvfilevo.getExtName()))
                    fileEXT = csvfilevo.getExtName();
                if (!BTSLUtil.isNullString(csvfilevo.getProcessId()))
                    processId = csvfilevo.getProcessId();
                String isSummaryFileReq = "N";
                isSummaryFileReq = csvproperties.getProperty("SUMMARY_FILE_REQUIRED");
                if (BTSLUtil.isNullString(isSummaryFileReq)) {
                    isSummaryFileReq = "N";
                }
                if ("Y".equalsIgnoreCase(isSummaryFileReq))
                    try {
                        writeFileSummary(finalMasterDirectoryPath, fileEXT, processId);
                    } catch (Exception e) {
                        logger.errorTrace(methodName, e);
                    }
            }
            processId = "CSVGENERATOR";
            // Ended Here

            // if the status was marked as under process by this method call,
            // only then it is marked as complete on termination
            if (statusOk) {
                try {
                    if (markProcessStatusAsComplete(con, processId) == 1)
                        try {
                            con.commit();
                        } catch (Exception e) {
                            logger.errorTrace(methodName, e);
                        }
                    else
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            logger.errorTrace(methodName, e);
                        }
                } catch (Exception e) {
                    logger.errorTrace(methodName, e);
                }
                try {
                    if (con != null)
                        con.close();
                } catch (Exception ex) {
                    logger.errorTrace(methodName, ex);
                    if (logger.isDebugEnabled())
                        logger.debug(methodName, "Exception closing connection ");
                }
            }
            logger.debug(methodName, "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            if (logger.isDebugEnabled())
                logger.debug(methodName, "Exiting..... ");
        }
    }

    private static void loadConstantParameters() throws BTSLBaseException {
        final String methodName = "loadConstantParameters";
        if (logger.isDebugEnabled())
            logger.debug("loadParameters", " Entered: ");
        try {
            initialize(SqlParameterEncoder.encodeParams(csvproperties.getProperty("PROCESS_ID")));
            logger.debug(methodName, " Required information successfuly loaded from csvConfigFile.properties...............: ");
        } catch (BTSLBaseException be) {
            logger.error(methodName, "BTSLBaseException : " + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, processName+"[loadConstantParameters]", "", "", "", "Message:" + be.getMessage());
            logger.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            logger.error(methodName, "Exception : " + e.getMessage());
            logger.errorTrace(methodName, e);
            BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, processName+"[loadConstantParameters]", "", "", "", "Message:" + btslMessage);
            throw new BTSLBaseException(processName, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }

    }

    

    private static void fetchQuery(Connection pcon, Date pbeingProcessedDate, String pdirPath, String pfileName, String pfileLabel, String pfileEXT, String psqlQuery, String ptempTbl, long pmaxFileLength) throws BTSLBaseException {
        final String methodName = "fetchQuery";
        if (logger.isDebugEnabled())
            logger.debug(methodName, " Entered: pbeingProcessedDate=" + pbeingProcessedDate + " pdirPath=" + pdirPath + " pfileName=" + pfileName + " pfileLabel=" + pfileLabel + " pfileEXT=" + pfileEXT + " ptempTbl=" + ptempTbl + " pmaxFileLength=" + pmaxFileLength);

        StringBuilder c2sInsertQrueryBuilder=new StringBuilder("insert into ");
        c2sInsertQrueryBuilder.append(ptempTbl);
        c2sInsertQrueryBuilder.append(" ");
        c2sInsertQrueryBuilder.append(psqlQuery);
        String c2sInsertQuery=c2sInsertQrueryBuilder.toString();
        if (logger.isDebugEnabled())
            logger.debug(methodName, "sql insert query:" + c2sInsertQuery);
        PreparedStatement c2sInsertPstmt = null;
        PreparedStatement c2sTruncatePstmt = null;

		try{
			
			String query ="truncate table "+ptempTbl;
			boolean matchFound = BTSLUtil.validateTableNameCommon(ptempTbl);
            if (!matchFound) {
                throw new BTSLBaseException("CSVFileGeneratorProcess", methodName, "error.not.a.valid.table.name");	
            }
            else{
			c2sTruncatePstmt=pcon.prepareStatement(query); 
			c2sTruncatePstmt.executeUpdate();
			pcon.commit();
            }
			
			String[] inStrArray= null;
			String pprocessIDs=csvproperties.getProperty("PROCESS_ID");
			
			
			 inStrArray = pprocessIDs.split(",");
			    if(BTSLUtil.isNullArray(inStrArray))
			        throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS3_NO_INTERFACEIDS); 
				for(int i=0,size=inStrArray.length;i<size;i++)
				{
					processId = inStrArray[i].trim();
				}
			
				
	        int n = Integer.parseInt(csvproperties.getProperty(processId+"_DYN_ARG"));
	        
               if (processId.equals(PretupsI.BURN_RATE_PROCESS))
            		   {
            	    int multiplicationFactor = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AMOUNT_MULT_FACTOR))).intValue();
            		DateFormat df = new SimpleDateFormat(PretupsI.DATE_FORMAT);
            		Date dateobj = new Date();
            		Date currentDate = df.parse(df.format(dateobj));
            	   
            		c2sInsertPstmt=pcon.prepareStatement(c2sInsertQuery);
            		
            		 int i =0;
                        i++;
        				c2sInsertPstmt.setInt(i,multiplicationFactor);
        				i++;
        				c2sInsertPstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(currentDate));
        				i++;
        				c2sInsertPstmt.setDate(i, BTSLUtil.getSQLDateFromUtilDate(currentDate));

        			
            		
            		   }
               else
               {
			String lastnmonths = csvproperties.getProperty(processId+"_LAST_N_MONTHS");
			c2sInsertPstmt=pcon.prepareStatement(c2sInsertQuery);
			
			int m=1;
			for(int i=1;i<=n;i++){
				
				c2sInsertPstmt.setString(m,lastnmonths);
			}
			
               }
			int insertCount=c2sInsertPstmt.executeUpdate();
			pcon.commit();
		    logger.debug(methodName,"Memory after loading sql query data: Total:"+Runtime.getRuntime().totalMemory()/1049576+" Free:"+Runtime.getRuntime().freeMemory()/1049576+" for date:"+pbeingProcessedDate);

            // method call to write data in the files
            writeDataInFile(pcon, pdirPath, pfileName, pfileLabel, pbeingProcessedDate, pfileEXT, pmaxFileLength, ptempTbl, insertCount);

            logger.debug(methodName, "Memory after writing data files: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576 + " for date:" + pbeingProcessedDate);
        } catch (BTSLBaseException be) {
            logger.error(methodName, "BTSLBaseException : " + be.getMessage());
            logger.errorTrace(methodName, be);
            throw be;
        } catch (SQLException sqe) {
            logger.error(methodName, "SQLException " + sqe.getMessage());
            logger.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, processName+"[fetchQuery]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException(processName, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            logger.error(methodName, "Exception : " + ex.getMessage());
            logger.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, processName+"[fetchQuery]", "", "", "", "SQLException:" + ex.getMessage());
            throw new BTSLBaseException(processName, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (c2sInsertPstmt != null)
                try {
                    c2sInsertPstmt.close();
                } catch (Exception ex) {
                    logger.errorTrace(methodName, ex);
                }
            if (c2sTruncatePstmt != null)
                try {
                    c2sTruncatePstmt.close();
                } catch (Exception ex) {
                    logger.errorTrace(methodName, ex);
                }
            if (logger.isDebugEnabled())
                logger.debug(methodName, "Exiting ");
        }
    }

    private static void writeDataInFile(Connection pcon, String pdirPath, String pfileName, String pfileLabel, Date pbeingProcessedDate, String pfileEXT, long pmaxFileLength, String ptempTbl, int pmaxCount) throws BTSLBaseException {
        final String methodName = "writeDataInFile";
        if (logger.isDebugEnabled())
            logger.debug(methodName, " Entered:  pdirPath=" + pdirPath + " pfileName=" + pfileName + " pfileLabel=" + pfileLabel + " pbeingProcessedDate=" + pbeingProcessedDate + " pfileEXT=" + pfileEXT + " pmaxFileLength=" + pmaxFileLength);
        long recordsWrittenInFile = 0;
        PrintWriter out = null;
        int fileNumber = 0;
        String fileName = null;
        File newFile = null;
        String fileData = null;
        String fileHeader = null;
        String fileFooter = null;
        PreparedStatement selectStmt = null;
        ResultSet rst1 = null;
        String selectQuery = null;
        try {
            SimpleDateFormat sdf = null;
            try {
                sdf = new SimpleDateFormat(SqlParameterEncoder.encodeParams(csvproperties.getProperty("FILE_SUBSTR_DATE_FORMAT")));
            } catch (Exception e) {
                sdf = new SimpleDateFormat("_ddMMyy_hhmmss_");
                logger.errorTrace(methodName, e);
            }
            File dir = new File(pdirPath);
            if(!dir.exists())
            dir.mkdir();
            // generating file name
            fileNumber = 1;
            Date date = new Date();
            String finalDate = PretupsI.EMPTY;
            if("_ddMMyy_hhmmss_".equalsIgnoreCase(sdf.toString())) {
            	finalDate = "_" + BTSLUtil.getDateStrForName(date) + "_" + 
            						BTSLDateUtil.getSystemLocaleTime(date, PretupsI.TIME_FORMAT_HHMMSS_WOSEPARATOR, false) + "_";
            } else {
            	finalDate = sdf.format(date);
            }
            // if the length of file number is 1, two zeros are added as prefix
            if (Integer.toString(fileNumber).length() == 1)
                fileName = pdirPath +"/"+ pfileName + finalDate + "00" + fileNumber + pfileEXT;
            // if the length of file number is 2, one zero is added as prefix
            else if (Integer.toString(fileNumber).length() == 2)
                fileName = pdirPath + File.separator + pfileName + finalDate + "0" + fileNumber + pfileEXT;
            // else no zeros are added
            else if (Integer.toString(fileNumber).length() == 3)
                fileName = pdirPath + File.separator + pfileName + finalDate + fileNumber + pfileEXT;

            logger.debug(methodName, "  fileName=" + fileName);

            newFile = new File(fileName);
            fileNameLst.add(fileName);

            out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));

            fileHeader = constructFileHeader(fileNumber, pfileLabel);
            out.write(fileHeader);

            String dbConnected = Constants.getProperty(QueryConstants.PRETUPS_DB);
            StringBuilder selectQueryBuilder=new StringBuilder();
			 if (QueryConstants.DB_POSTGRESQL.equals(dbConnected)){
				 selectQueryBuilder.append("select * from ");
				 selectQueryBuilder.append(ptempTbl);
				 selectQueryBuilder.append(" LIMIT");
				 selectQueryBuilder.append(pmaxFileLength);
			 }
			 else
			 {
				 selectQueryBuilder.append("select * from ");
				 selectQueryBuilder.append(ptempTbl);
				 selectQueryBuilder.append(" where row_number>? and rownum<=");
				 selectQueryBuilder.append(pmaxFileLength);
				 selectQueryBuilder.append(" order by row_number");
				}
			selectQuery=selectQueryBuilder.toString();
            boolean matchFound = BTSLUtil.validateTableNameCommon(ptempTbl);
            if (!matchFound) {
                throw new BTSLBaseException("CSVFileGeneratorProcess", methodName, "error.not.a.valid.table.name");	
            }
            else{
            selectStmt = pcon.prepareStatement(selectQuery);
            }
            int count = 0;

            while (true) {
            	if(!QueryConstants.DB_POSTGRESQL.equals(dbConnected))
            	{
                selectStmt.setInt(1, count);
            	}

                rst1 = selectStmt.executeQuery();

                while (rst1.next()) {
                    fileData = SqlParameterEncoder.encodeParams(rst1.getString(2));
                    out.write(fileData + "\n");
                    recordsWrittenInFile++;
                    count++;
                    try {
                        generateDataFileSummary(pbeingProcessedDate, recordsWrittenInFile, fileName);
                    } catch (Exception e) {
                        logger.errorTrace(methodName, e);
                    }
                    // Ended Here
                    if (recordsWrittenInFile >= pmaxFileLength) {
                        fileFooter = constructFileFooter(recordsWrittenInFile);
                        out.write(fileFooter);

                        recordsWrittenInFile = 0;
                        fileNumber = fileNumber + 1;
                        //out.close();

                        // if the length of file number is 1, two zeros are
                        // added as prefix
                        if (Integer.toString(fileNumber).length() == 1)
                            fileName = pdirPath + File.separator + pfileName + finalDate + "00" + fileNumber + pfileEXT;
                        // if the length of file number is 2, one zero is added
                        // as prefix
                        else if (Integer.toString(fileNumber).length() == 2)
                            fileName = pdirPath + File.separator + pfileName + finalDate + "0" + fileNumber + pfileEXT;
                        // else no zeros are added
                        else if (Integer.toString(fileNumber).length() == 3)
                            fileName = pdirPath + File.separator + pfileName + finalDate + fileNumber + pfileEXT;

                        logger.debug(methodName, "  fileName=" + fileName);
                        newFile = new File(fileName);
                        //out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
                        BTSLUtil.closeOpenStream(out, newFile);
                        fileNameLst.add(fileName);
                        fileHeader = constructFileHeader(fileNumber, pfileLabel);
                        out.write(fileHeader);
                    }
                }
                if (count == pmaxCount)
                    break;
                else
                    selectStmt.clearParameters();
            }
            // if number of records are not zero then footer is appended as file
            // is deleted
            if (recordsWrittenInFile > 0) {
                fileFooter = constructFileFooter(recordsWrittenInFile);
                out.write(fileFooter);
            } else {
                if (out != null)
                    out.close();
            }
            if (out != null)
                out.close();
        } catch (Exception e) {
            deleteAllFiles();
            logger.debug(methodName, "Exception: " + e.getMessage());
            logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, processName+"[writeDataInFile]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(processName, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (selectStmt != null)
                try {
                    selectStmt.close();
                } catch (Exception e) {
                    logger.errorTrace(methodName, e);
                }
            
            if (rst1 != null)
                try {
                    rst1.close();
                } catch (Exception e) {
                    logger.errorTrace(methodName, e);
                }
            
            if (out != null)
                out.close();
            if (logger.isDebugEnabled())
                logger.debug(methodName, "Exiting ");
        }
    }

    private static int markProcessStatusAsComplete(Connection pcon, String pprocessId) throws BTSLBaseException {
        final String methodName = "markProcessStatusAsComplete";
        if (logger.isDebugEnabled())
            logger.debug(methodName, " Entered:  pprocessId:" + pprocessId);
        int updateCount = 0;
        Date currentDate = new Date();
        ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        processStatusVO.setProcessID(pprocessId);
        processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        processStatusVO.setStartDate(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetail(pcon, processStatusVO);
        } catch (Exception e) {
            logger.errorTrace(methodName, e);
            logger.error(methodName, "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, processName+"[markProcessStatusAsComplete]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(processName, methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (logger.isDebugEnabled())
                logger.debug(methodName, "Exiting: updateCount=" + updateCount);
        } // end of finally
        return updateCount;

    }

    private static void deleteAllFiles() throws BTSLBaseException {
        final String methodName = "deleteAllFiles";
        if (logger.isDebugEnabled())
            logger.debug("deleteAllFiles", " Entered: ");
        int size = 0;
        if (fileNameLst != null)
            size = fileNameLst.size();
        if (logger.isDebugEnabled())
            logger.debug("deleteAllFiles", " : Number of files to be deleted " + size);
        String fileName;
        File newFile;
        for (int i = 0; i < size; i++) {
            try {
                fileName = (String) fileNameLst.get(i);
                newFile = new File(fileName);
                newFile.delete();
                if (logger.isDebugEnabled())
                    logger.debug("", fileName + " file deleted");
            } catch (Exception e) {
                logger.error(methodName, "Exception " + e.getMessage());
                logger.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, processName+"[deleteAllFiles]", "", "", "", "Exception:" + e.getMessage());
                throw new BTSLBaseException(processName, "deleteAllFiles", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
            }
        }// end of for loop
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, processName+"[deleteAllFiles]", "", "", "", " Message: CSVFileGeneratorProcess process has found some error, so deleting all the files.");
        if (fileNameLst != null && fileNameLst.isEmpty())
            fileNameLst.clear();
        if (logger.isDebugEnabled())
            logger.debug(methodName, " : Exiting.............................");
    }

    private static String constructFileHeader(long pfileNumber, String pfileLabel) {
        final String methodName = "constructFileHeader";
        SimpleDateFormat sdf = null;
        try {
            sdf = new SimpleDateFormat(SqlParameterEncoder.encodeParams(csvproperties.getProperty("DATE_TIME_FORMAT")));
        } catch (Exception e) {
            sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);
            logger.errorTrace(methodName, e);
        }
        StringBuilder fileHeaderBuf = new StringBuilder("");
        fileHeaderBuf.append("Present Date and Time=" + BTSLDateUtil.getLocaleTimeStamp(sdf.format(new Date())));
        fileHeaderBuf.append("\n" + "File Number=" + pfileNumber);
        fileHeaderBuf.append("\n" + pfileLabel);
        fileHeaderBuf.append("\n" + "[STARTDATA]" + "\n");
        return fileHeaderBuf.toString();
    }

    private static String constructFileFooter(long pnoOfRecords) {
    	StringBuilder fileHeaderBuf = null;
        fileHeaderBuf = new StringBuilder("");
        fileHeaderBuf.append("[ENDDATA]" + "\n");
        fileHeaderBuf.append("Number of records=" + Long.toString(pnoOfRecords));
        return fileHeaderBuf.toString();
    }

    public static void initialize(String pprocessIDs) throws BTSLBaseException {
        final String methodName = "initialize";
        if (logger.isDebugEnabled())
            logger.debug(methodName, "Entered pprocessIDs::" + pprocessIDs);
        String processId = null;
        String[] inStrArray = null;
        try {
            CSVFileVO csvFileVO;
            inStrArray = pprocessIDs.split(",");
            if (BTSLUtil.isNullArray(inStrArray))
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS3_NO_INTERFACEIDS);
            for (int i = 0, size = inStrArray.length; i < size; i++) {
                processId = inStrArray[i].trim();
                csvFileVO = new CSVFileVO();
                csvFileVO.setProcessId(processId);
                csvFileVO.setQueryName(SqlParameterEncoder.encodeParams(csvproperties.getProperty(processId + "_QRY")));
                csvFileVO.setDirName(SqlParameterEncoder.encodeParams(csvproperties.getProperty(processId + "_DIR")));
                csvFileVO.setExtName(SqlParameterEncoder.encodeParams(csvproperties.getProperty(processId + "_EXT")));
                csvFileVO.setHeaderName(SqlParameterEncoder.encodeParams(csvproperties.getProperty(processId + "_HEADER")));
                csvFileVO.setPrefixName(SqlParameterEncoder.encodeParams(csvproperties.getProperty(processId + "_PREFIX_NAME")));
                csvFileVO.setTempTable(SqlParameterEncoder.encodeParams(csvproperties.getProperty(processId + "_TEMP_TBL")));
                if (logger.isDebugEnabled())
                    logger.debug(processName+"[initialize]", "csvFileVO::" + csvFileVO);
                csvMap.put(processId, csvFileVO);
            }
        } catch (BTSLBaseException be) {
            logger.errorTrace(methodName, be);
            logger.error(methodName, "BTSLBaseException be:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            logger.errorTrace(methodName, e);
            logger.error(methodName, "Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, processName+"[initialize]", "String of pprocessIDs ids=" + pprocessIDs, "", "", "While initializing the processIDs for the CSV file generator process =" + processId + " get Exception=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS3_NODE_INITIALIZATION);
        } finally {
            if (logger.isDebugEnabled())
                logger.debug(methodName, "Exited csvMap::" + csvMap);
        }
    }

    /**
     * @author diwakar
     * @date : 04-MAR-2014
     * @param pbeingProcessedDate
     * @param precordsWrittenInFile
     * @param pfileName
     * @throws BTSLBaseException
     */
    private static void generateDataFileSummary(Date pbeingProcessedDate, long precordsWrittenInFile, String pfileName) throws BTSLBaseException {
        final String methodName = "generateDataFileSummary";
        if (logger.isDebugEnabled())
            logger.debug(processName, " Entered: generateDataFileSummary pbeingProcessedDate=" + pbeingProcessedDate + ", precordsWrittenInFile=" + precordsWrittenInFile + ", pfileName=" + pfileName);
        try {
            String processDateStr = BTSLUtil.getDateStringFromDate(pbeingProcessedDate);
            if (fileRecordMap.isEmpty()) {
                fileNameMap.put(pfileName, precordsWrittenInFile);
                fileRecordMap.put(processDateStr, fileNameMap);
            } else {
                if (fileRecordMap.containsKey(processDateStr)) {
                    fileNameMap = (Hashtable<String, Long>) fileRecordMap.get(processDateStr);
                    fileNameMap.put(pfileName, precordsWrittenInFile);
                    // Added By Diwakar on 07-MAR-2014 for OCI client
                    fileRecordMap.put(processDateStr, fileNameMap);
                    // Ended Here
                } else {
                    fileNameMap = new Hashtable<String, Long>();
                    fileNameMap.put(pfileName, precordsWrittenInFile);
                    fileRecordMap.put(processDateStr, fileNameMap);
                }
            }

        } catch (Exception e) {
            logger.errorTrace(methodName, e);
            logger.debug(processName, " generateDataFileSummary() While recoding file list Exception: " + e.getMessage());
        } finally {
            if (logger.isDebugEnabled())
                logger.debug(processName, "Exiting generateDataFileSummary() ");
        }
    }

    /**
     * @author diwakar
     * @date : 04-MAR-2014
     * @param pdirPath
     * @param pfileEXT
     * @throws BTSLBaseException
     */
    private static void writeFileSummary(String pdirPath, String pfileEXT, String pprocessId) throws BTSLBaseException {
        final String methodName = "writeFileSummary";
        if (logger.isDebugEnabled())
            logger.debug(processName, " Entered: writeFileSummary() pdirPath=" + pdirPath + ", pfileEXT=" + pfileEXT);
        PrintWriter out = null;
        File newFile = null;
        try {
            String fileName = null;
            String fileData = null;
            String fileHeader = null;
            String processDate = null;
            // Changed on 05-MAR-2014
  
            fileName = pdirPath + File.separator + pprocessId + "Trans_Stat_" + BTSLUtil.getDateTimeStringFromDate(new Date(), "ddMMyy") + pfileEXT;
            // Ended Here
            logger.debug(processName, " writeFileSummary() fileName=" + fileName);

            newFile = new File(fileName);
            boolean isFileAlreadyExists = false;
            newFile = new File(fileName);
            if (!newFile.exists()) {
                newFile.createNewFile();
                isFileAlreadyExists = false;
            } else {
                isFileAlreadyExists = true;
            }
            // Added by Diwakar on 07-MAR-2014 for OCI client
            String transSeperator = null;
            try {
                transSeperator = Constants.getProperty("DAILY_USER_BALANCE_TRANSCATION_STAT_SEPERATOR");
            } catch (RuntimeException e) {
                logger.errorTrace(methodName, e);
                transSeperator = ";";
            }
            if (newFile.exists()) {
                if (!isFileAlreadyExists) {
                    out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
                } else {
                    out = new PrintWriter(new BufferedWriter(new FileWriter(newFile, true /*
                                                                                           * append
                                                                                           * =
                                                                                           * true
                                                                                           */)));
                }
                fileHeader = "Date" + transSeperator + "Files_Number" + transSeperator + "File_Name" + transSeperator + "Total_Records";
                out.write(fileHeader + "\n");
                Hashtable<String, Long> fileRecord = null;
                fileRecordMap.comparator();
                Set<String> keyList = fileRecordMap.keySet();
                Iterator<String> itrProcessDate = keyList.iterator();
                Iterator<String> itrFile = null;
                String file = null;
                while (itrProcessDate.hasNext()) {
                   int i = 0;
                    file = null;
                    fileData = null;
                    itrFile = null;
                    processDate = null;
                    processDate = itrProcessDate.next();
                    fileRecord = (Hashtable) fileRecordMap.get(processDate);
                    itrFile = (fileRecord.keySet()).iterator();
                    fileData = processDate + transSeperator + new Integer(fileRecord.size()).toString() + transSeperator;
                    while (itrFile.hasNext()) {
                        file = itrFile.next().toString();
                        fileData = fileData + file + transSeperator + fileRecord.get(file).toString();
                        out.append(fileData + "\n");
                        i++;
                    }
                }
                out.flush();
            } else {
                logger.error(processName, " writeFileSummary() fileName=" + fileName + "does not exists on system.");
            }
        } catch (Exception e) {
            logger.debug(processName, "Exception writeFileSummary(): " + e.getMessage());
            logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, processName+"[writeFileSummary]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(processName, "writeFileSummary()", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (out != null)
                out.close();
            if (fileRecordMap != null)
                fileRecordMap.clear();
            if (fileNameMap != null)
                fileNameMap.clear();
            if (logger.isDebugEnabled())
                logger.debug(processName, "Exiting writeFileSummary() ");
        }
    }

}

package com.btsl.pretups.processes.csvgenerator;

/**
 * @(#)CSVFileGenerator4NewUserApprovedProcess
 *                                             Copyright(c) 2014, Mahindra
 *                                             Comviva Technologies Ltd.
 *                                             All Rights Reserved
 * 
 *                                             --------------------------------
 *                                             --
 *                                             --------------------------------
 *                                             -------------------------------
 *                                             Author Date History
 *                                             --------------------------------
 *                                             --
 *                                             --------------------------------
 *                                             -------------------------------
 *                                             Diwakar 11/04/2014 Initial
 *                                             Creation
 *                                             --------------------------------
 *                                             --
 *                                             --------------------------------
 *                                             -------------------------------
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
import java.text.ParseException;
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
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.processes.businesslogic.DateSorting;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.OracleUtil;
import com.btsl.util.SqlParameterEncoder;
import com.ibm.icu.util.Calendar;

public class CSVFileGenerator4NewUserApprovedProcess {
    private static ArrayList<String> _fileNameLst = new ArrayList<String>();
    private static ProcessStatusVO _processStatusVO;
    private static ProcessBL _processBL = null;
    private static HashMap<String, CSVFileVO> _csvMap = new HashMap<String, CSVFileVO>();
    private static Properties _csvproperties = new Properties();

    private static Log _logger = LogFactory.getLog(CSVFileGenerator4NewUserApprovedProcess.class.getName());
    // 07-MAR-2014
    private static String _finalMasterDirectoryPath = null;// use to store the
                                                           // final master
                                                           // directory path in
                                                           // which the master
                                                           // and transaction
                                                           // data files will be
                                                           // moved after all
                                                           // files creation
    private static String _fileEXT = ".csv";// use to store the extension of the
                                            // files, which are going to create
                                            // by the process
    private static Hashtable<String, Long> _fileNameMap = null;
    private static TreeMap<String, Object> _fileRecordMap = null;

    // Ended Here
    
    /**
     * to ensure no class instantiation 
     */
    private CSVFileGenerator4NewUserApprovedProcess(){
    	
    }
    public static void main(String arg[]) {
        final String methodName = "main";
        try {
            if (arg.length != 3) {
                if (arg.length != 2) {
                    System.out.println("Usage : CSVFileGenerator4NewUserApprovedProcess [Constants file] [LogConfig file] [csvConfigFile4NewUsers file]");
                    return;
                }
            }
            File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                System.out.println("CSVFileGenerator4NewUserApprovedProcess" + " Constants File Not Found .............");
                return;
            }
            File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                System.out.println("CSVFileGenerator4NewUserApprovedProcess" + " Logconfig File Not Found .............");
                return;
            }

            File csvConfigFile = new File(arg[2]);
            if (!csvConfigFile.exists()) {
                System.out.println("CSVFileGenerator4NewUserApprovedProcess" + " csvConfigFile4NewUsers.props File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            try(FileInputStream fileInputStream = new FileInputStream(csvConfigFile))
            {
            _csvproperties.load(fileInputStream);
            }

        } catch (Exception e) {
            if (_logger.isDebugEnabled())
                _logger.debug(methodName, " Error in Loading Files ...........................: " + e.getMessage());
            _logger.errorTrace(methodName, e);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            process();
        } catch (BTSLBaseException be) {
            _logger.error(methodName, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(methodName, be);
        } finally {
            if (_logger.isDebugEnabled())
                _logger.debug(methodName, "Exiting..... ");
            ConfigServlet.destroyProcessCache();
        }
    }

    private static void process() throws BTSLBaseException {
        Date processedUpto = null;
        Date currentDateTime = new Date();
        Connection con = null;
        String processId = null;
        boolean statusOk = false;

        final String methodName = "process";
        try {
            // 07-MAR-2014
            _fileNameMap = new Hashtable<String, Long>();
            // commented below line on 07-MAR-2014
            // _fileRecordMap=new TreeMap <String,Object>();
            _fileRecordMap = new TreeMap<String, Object>(new DateSorting());
            // Ended Here

            _logger.debug(methodName, "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            Calendar cal = BTSLDateUtil.getInstance();
            currentDateTime = cal.getTime(); // Current Date
            // getting all the required parameters from
            // csvConfigFile4NewUsers.props
            loadConstantParameters();

            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled())
                    _logger.debug(methodName, " DATABASE Connection is NULL ");
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CSVFileGenerator4NewUserApprovedProcess[process]", "", "", "", "DATABASE Connection is NULL");
                return;
            }
            processId = ProcessI.NEW_USERS_APPROVED_CSV;
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            if (statusOk) {
                con.commit();
                processedUpto = _processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    CSVFileVO csvfilevo = null;
                    // method call to create master directory and child
                    // directory if does not exist
                    // String
                    // _childDirectory=createDirectory(_masterDirectoryPathAndName,processId,dateCount);
                    // method call to fetch transaction data and write it in
                    // files
                    Iterator<String> itr = _csvMap.keySet().iterator();
                    try {
                        while (itr.hasNext()) {
                            csvfilevo = (CSVFileVO) _csvMap.get((String) itr.next());
                            fetchQuery(con, processedUpto, currentDateTime, SqlParameterEncoder.encodeParams(_csvproperties.getProperty("DATE_TIME_FORMAT")), SqlParameterEncoder.encodeParams(_csvproperties.getProperty("SQL_DATE_TIME_FORMAT")), csvfilevo.getDirName(), csvfilevo.getPrefixName(), csvfilevo.getHeaderName(), csvfilevo.getExtName(), Long.parseLong(_csvproperties.getProperty("MAX_ROWS")));
                            Thread.sleep(500);
                        }
                    } catch (Exception e) {
                        _logger.errorTrace(methodName, e);
                        _logger.error(methodName, "csvfilevo=" + csvfilevo.toString() + "  Exception : " + e.getMessage());
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "CSVFileGenerator4NewUserApprovedProcess[process]", "", "", "", "csvfilevo=" + csvfilevo.toString() + " Exception =" + e.getMessage());
                    }
                    _processStatusVO.setExecutedUpto(currentDateTime);
                    _processStatusVO.setExecutedOn(currentDateTime);
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "CSVFileGenerator4NewUserApprovedProcess[process]", "", "", "", " CSVFileGenerator4NewUserApprovedProcess process has been executed successfully.");
                } else
                    throw new BTSLBaseException("CSVFileGenerator4NewUserApprovedProcess", methodName, PretupsErrorCodesI.DWH_PROCESS_EXECUTED_UPTO_DATE_NOT_FOUND);
            }
        } catch (BTSLBaseException be) {
            _logger.error(methodName, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            if (_fileNameLst.size() > 0)
                deleteAllFiles();
            _logger.error(methodName, "Exception : " + e.getMessage());
            _logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "CSVFileGenerator4NewUserApprovedProcess[process]", "", "", "", " Exception =" + e.getMessage());
            throw new BTSLBaseException("CSVFileGenerator4NewUserApprovedProcess", methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            // 07-MAR-2014
            Iterator<String> itr = _csvMap.keySet().iterator();
            while (itr.hasNext()) {
                CSVFileVO csvfilevo = (CSVFileVO) _csvMap.get((String) itr.next());
                _finalMasterDirectoryPath = csvfilevo.getDirName();
                if (!BTSLUtil.isNullString(csvfilevo.getExtName()))
                    _fileEXT = csvfilevo.getExtName();
                if (!BTSLUtil.isNullString(csvfilevo.getProcessId()))
                    processId = csvfilevo.getProcessId();
                String isSummaryFileReq = "N";
                isSummaryFileReq = _csvproperties.getProperty("SUMMARY_FILE_REQUIRED");
                if (BTSLUtil.isNullString(isSummaryFileReq)) {
                    isSummaryFileReq = "N";
                }
                if ("Y".equalsIgnoreCase(isSummaryFileReq))
                    try {
                        writeFileSummary(_finalMasterDirectoryPath, _fileEXT, processId);
                    } catch (Exception e) {
                        _logger.errorTrace(methodName, e);
                    }
            }
            processId = ProcessI.NEW_USERS_APPROVED_CSV;
            // Ended Here

            // if the status was marked as under process by this method call,
            // only then it is marked as complete on termination
            if (statusOk) {
                try {
                    if (markProcessStatusAsComplete(con, processId) == 1)
                        try {
                            con.commit();
                        } catch (Exception e) {
                            _logger.errorTrace(methodName, e);
                        }
                    else
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            _logger.errorTrace(methodName, e);
                        }
                } catch (Exception e) {
                    _logger.errorTrace(methodName, e);
                }
                try {
                    if (con != null)
                        con.close();
                } catch (Exception ex) {
                    _logger.errorTrace(methodName, ex);
                    if (_logger.isDebugEnabled())
                        _logger.debug(methodName, "Exception closing connection ");
                }
            }
            _logger.debug(methodName, "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            if (_logger.isDebugEnabled())
                _logger.debug(methodName, "Exiting..... ");
        }
    }

    private static void loadConstantParameters() throws BTSLBaseException {
        if (_logger.isDebugEnabled())
            _logger.debug("loadParameters", " Entered: ");
        final String methodName = "loadConstantParameters";
        try {
            initialize(SqlParameterEncoder.encodeParams(_csvproperties.getProperty("PROCESS_ID")));
            _logger.debug(methodName, " Required information successfuly loaded from csvConfigFile.properties...............: ");
        } catch (BTSLBaseException be) {
            _logger.error(methodName, "BTSLBaseException : " + be.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CSVFileGenerator4NewUserApprovedProcess[loadConstantParameters]", "", "", "", "Message:" + be.getMessage());
            _logger.errorTrace(methodName, be);
            throw be;
        } catch (Exception e) {
            _logger.error(methodName, "Exception : " + e.getMessage());
            _logger.errorTrace(methodName, e);
            BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CSVFileGenerator4NewUserApprovedProcess[loadConstantParameters]", "", "", "", "Message:" + btslMessage);
            throw new BTSLBaseException("CSVFileGenerator4NewUserApprovedProcess", methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }

    }

    private static void fetchQuery(Connection p_con, Date p_hadProcessedDate, Date p_beingProcessedDate, String p_dateFormat, String p_sqlDateFormat, String p_dirPath, String p_fileName, String p_fileLabel, String p_fileEXT, long p_maxFileLength) throws BTSLBaseException, ParseException {
        final String methodName = "fetchQuery";
        if (_logger.isDebugEnabled())
            _logger.debug(methodName, " Entered: p_hadProcessedDate = " + p_hadProcessedDate + "p_beingProcessedDate=" + p_beingProcessedDate + "p_dateFormat=" + p_dateFormat + "p_sqlDateFormat=" + p_sqlDateFormat + " p_dirPath=" + p_dirPath + " p_fileName=" + p_fileName + " p_fileLabel=" + p_fileLabel + " p_fileEXT=" + p_fileEXT);
        StringBuffer qrySelect = null;
        qrySelect = new StringBuffer(" SELECT U.USER_ID, U.PARENT_ID, U.OWNER_ID, U.EXTERNAL_CODE, U.MSISDN, U.LOGIN_ID, U.CATEGORY_CODE, C.CATEGORY_NAME, ");
        qrySelect.append(" C.DOMAIN_CODE, D.DOMAIN_NAME, U.USER_NAME, U.CITY, U.STATE,U.COUNTRY, U.EMAIL, U.CONTACT_PERSON, U.CONTACT_NO, U.CREATED_BY, ");
        qrySelect.append(" U.CREATED_ON, U.LEVEL1_APPROVED_BY, U.LEVEL1_APPROVED_ON, U.LEVEL2_APPROVED_BY, U.LEVEL2_APPROVED_ON");
        qrySelect.append(" FROM USERS U, CATEGORIES C, DOMAINS D ");
        qrySelect.append(" WHERE U.STATUS = '" + PretupsI.USER_STATUS_ACTIVE + "' ");
        qrySelect.append(" AND U.PREVIOUS_STATUS = '" + PretupsI.USER_STATUS_NEW + "' ");
        qrySelect.append(" AND U.USER_TYPE = '" + PretupsI.USER_TYPE_CHANNEL + "' ");
        qrySelect.append(" AND U.CATEGORY_CODE = C.CATEGORY_CODE ");
        qrySelect.append(" AND C.DOMAIN_CODE = D.DOMAIN_CODE ");
        qrySelect.append(" AND U.MODIFIED_ON >=  TO_DATE('" + BTSLUtil.getDateTimeStringFromDate(p_hadProcessedDate, p_dateFormat) + "','" + p_sqlDateFormat + "') ");
        qrySelect.append(" AND U.MODIFIED_ON <=  TO_DATE('" + BTSLUtil.getDateTimeStringFromDate(p_beingProcessedDate, p_dateFormat) + "','" + p_sqlDateFormat + "') ");

        if (_logger.isDebugEnabled())
            _logger.debug(methodName, "sql slect query:" + qrySelect);
        PreparedStatement newUsersApprovedPstmt = null;
        ResultSet rs = null;
        try {
            newUsersApprovedPstmt = p_con.prepareStatement(qrySelect.toString());
            rs = newUsersApprovedPstmt.executeQuery();
            ArrayList<UserVO> userVoList = new ArrayList<UserVO>(1000);
            UserVO userVo = null;
            CategoryVO categoryVO = null;

            while (rs.next()) {
                userVo = new UserVO();
                userVo.setUserID(rs.getString("USER_ID"));
                userVo.setParentID(rs.getString("PARENT_ID"));
                userVo.setOwnerID(rs.getString("OWNER_ID"));
                userVo.setExternalCode(rs.getString("EXTERNAL_CODE"));
                userVo.setMsisdn(rs.getString("MSISDN"));
                userVo.setLoginID(rs.getString("LOGIN_ID"));

                categoryVO = new CategoryVO();
                categoryVO.setCategoryCode(rs.getString("CATEGORY_CODE"));
                categoryVO.setCategoryName(rs.getString("CATEGORY_NAME"));
                categoryVO.setDomainCodeforCategory(rs.getString("DOMAIN_CODE"));
                categoryVO.setDomainName(rs.getString("DOMAIN_NAME"));
                userVo.setCategoryVO(categoryVO);

                userVo.setUserName(rs.getString("USER_NAME"));
                userVo.setCity(rs.getString("CITY"));
                userVo.setState(rs.getString("STATE"));
                userVo.setCountry(rs.getString("COUNTRY"));
                userVo.setEmail(rs.getString("EMAIL"));
                userVo.setContactPerson(rs.getString("CONTACT_PERSON"));
                userVo.setContactNo(rs.getString("CONTACT_NO"));
                userVo.setCreatedBy(rs.getString("CREATED_BY"));
                userVo.setCreatedOn(rs.getDate("CREATED_ON"));
                userVo.setLevel1ApprovedBy(rs.getString("LEVEL1_APPROVED_BY"));
                userVo.setLevel1ApprovedOn(rs.getDate("LEVEL1_APPROVED_ON"));
                userVo.setLevel2ApprovedBy(rs.getString("LEVEL2_APPROVED_BY"));
                userVo.setLevel2ApprovedOn(rs.getDate("LEVEL2_APPROVED_ON"));

                userVoList.add(userVo);
            }
            _logger.debug(methodName, "Memory after loading sql query data: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);

            // method call to write data in the files
            if (userVoList != null && userVoList.size() > 0) {
                writeDataInFile(userVoList, p_dirPath, p_fileName, p_fileLabel, p_beingProcessedDate, p_fileEXT, p_maxFileLength);
                _logger.info(methodName, "Process has been executed successfully.");
            } else {
                _logger.info(methodName, "Process has been executed successfully with no record found.");
            }

            _logger.debug(methodName, "Memory after writing data files: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576 + " for date:" + p_beingProcessedDate);
        } catch (BTSLBaseException be) {
            _logger.error(methodName, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(methodName, be);
            throw be;
        } catch (SQLException sqe) {
            _logger.error(methodName, "SQLException " + sqe.getMessage());
            _logger.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CSVFileGenerator4NewUserApprovedProcess[fetchQuery]", "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("CSVFileGenerator4NewUserApprovedProcess", methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _logger.error(methodName, "Exception : " + ex.getMessage());
            _logger.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CSVFileGenerator4NewUserApprovedProcess[fetchQuery]", "", "", "", "SQLException:" + ex.getMessage());
            throw new BTSLBaseException("CSVFileGenerator4NewUserApprovedProcess", methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        }// end of catch
        finally {
            if (newUsersApprovedPstmt != null)
                try {
                    newUsersApprovedPstmt.close();
                } catch (Exception ex) {
                    _logger.errorTrace(methodName, ex);
                }
                try {
                	if (rs != null)
                    rs.close();
                } catch (Exception ex) {
                    _logger.errorTrace(methodName, ex);
                }

            if (_logger.isDebugEnabled())
                _logger.debug(methodName, "Exiting ");
        }
    }

    private static void writeDataInFile(ArrayList<UserVO> p_userVoList, String p_dirPath, String p_fileName, String p_fileLabel, Date p_beingProcessedDate, String p_fileEXT, long p_maxFileLength) throws BTSLBaseException {
        final String methodName = "writeDataInFile";
        if (_logger.isDebugEnabled())
            _logger.debug(methodName, " Entered:  p_dirPath=" + p_dirPath + " p_fileName=" + p_fileName + " p_fileLabel=" + p_fileLabel + " p_beingProcessedDate=" + p_beingProcessedDate + " p_fileEXT=" + p_fileEXT + " p_maxFileLength=" + p_maxFileLength);
        long recordsWrittenInFile = 0;
        PrintWriter out = null;
        int fileNumber = 0;
        String fileName = null;
        File newFile = null;
        String fileData = null;
        String fileHeader = null;
        String fileFooter = null;
        PreparedStatement selectStmt = null;
        String separator = ";";
        try {
            separator = SqlParameterEncoder.encodeParams(_csvproperties.getProperty("NEW_USER_APPROVED_DATA_SEPARATOR"));
        } catch (RuntimeException e1) {
            _logger.errorTrace(methodName, e1);
            separator = ";";
        }
        try {
            SimpleDateFormat sdf = null;
            try {
                sdf = new SimpleDateFormat(SqlParameterEncoder.encodeParams(_csvproperties.getProperty("FILE_SUBSTR_DATE_FORMAT")));
            } catch (Exception e) {
                _logger.errorTrace(methodName, e);
                sdf = new SimpleDateFormat("_ddMMyy_hhmmss_");
            }
            Date date = new Date();
            String finalDate = PretupsI.EMPTY;
            if("_ddMMyy_hhmmss_".equalsIgnoreCase(sdf.toString())) {
            	finalDate = "_" + BTSLUtil.getDateStrForName(date)+ "_" + 
            						BTSLDateUtil.getSystemLocaleTime(date, PretupsI.TIME_FORMAT_HHMMSS_WOSEPARATOR, false) + "_";
            } else {
            	finalDate = sdf.format(date);
            }
            // generating file name
            fileNumber = 1;
            // if the length of file number is 1, two zeros are added as prefix
            if (Integer.toString(fileNumber).length() == 1)
                fileName = p_dirPath + File.separator + p_fileName + finalDate + "00" + fileNumber + p_fileEXT;
            // if the length of file number is 2, one zero is added as prefix
            else if (Integer.toString(fileNumber).length() == 2)
                fileName = p_dirPath + File.separator + p_fileName + finalDate + "0" + fileNumber + p_fileEXT;
            // else no zeros are added
            else if (Integer.toString(fileNumber).length() == 3)
                fileName = p_dirPath + File.separator + p_fileName + finalDate + fileNumber + p_fileEXT;

            _logger.debug(methodName, "  fileName=" + fileName);

            newFile = new File(fileName);
            _fileNameLst.add(fileName);

            out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));

            fileHeader = constructFileHeader(fileNumber, p_fileLabel);
            out.write(fileHeader);

            int count = 0;
            UserVO userVo = null;
            Iterator<UserVO> userVoIter = p_userVoList.iterator();
            while (userVoIter.hasNext()) {
                userVo = userVoIter.next();
                fileData = userVo.getUserID() + separator + userVo.getParentID() + separator + userVo.getOwnerID() + separator + userVo.getExternalCode() + separator + userVo.getMsisdn() + separator + userVo.getLoginID() + separator + userVo.getCategoryVO().getCategoryCode() + separator + userVo.getCategoryVO().getCategoryName() + separator + userVo.getCategoryVO().getDomainCodeforCategory() + separator + userVo.getCategoryVO().getDomainName() + separator + userVo.getUserName() + separator + userVo.getCity() + separator + userVo.getState() + separator + userVo.getCountry() + separator + userVo.getEmail() + separator + userVo.getContactPerson() + separator + userVo.getContactNo() + separator + userVo.getCreatedBy() + separator + userVo.getCreatedOn() + separator + userVo.getLevel1ApprovedBy() + separator + userVo.getLevel1ApprovedOn() + separator + userVo.getLevel2ApprovedBy() + separator + userVo.getLevel2ApprovedOn();
                out.write(fileData + "\n");
                recordsWrittenInFile++;
                count++;
                // 07-MAR-2014
                if (recordsWrittenInFile > 0)
                    try {
                        generateDataFileSummary(p_beingProcessedDate, recordsWrittenInFile, fileName);
                    } catch (Exception e) {
                        _logger.errorTrace(methodName, e);
                    }
                // Ended Here
                if (recordsWrittenInFile >= p_maxFileLength) {
                    fileFooter = constructFileFooter(recordsWrittenInFile);
                    out.write(fileFooter);

                    recordsWrittenInFile = 0;
                    fileNumber = fileNumber + 1;
                    //out.close();

                    // if the length of file number is 1, two zeros are added as
                    // prefix
                    if (Integer.toString(fileNumber).length() == 1)
                        fileName = p_dirPath + File.separator + p_fileName + finalDate + "00" + fileNumber + p_fileEXT;
                    // if the length of file number is 2, one zero is added as
                    // prefix
                    else if (Integer.toString(fileNumber).length() == 2)
                        fileName = p_dirPath + File.separator + p_fileName + finalDate + "0" + fileNumber + p_fileEXT;
                    // else no zeros are added
                    else if (Integer.toString(fileNumber).length() == 3)
                        fileName = p_dirPath + File.separator + p_fileName + finalDate + fileNumber + p_fileEXT;

                    _logger.debug(methodName, "  fileName=" + fileName);
                    newFile = new File(fileName);
                    //out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
                    BTSLUtil.closeOpenStream(out, newFile);
                    _fileNameLst.add(fileName);
                    fileHeader = constructFileHeader(fileNumber, p_fileLabel);
                    out.write(fileHeader);
                }
            }

            // if number of records are not zero then footer is appended as file
            // is deleted
            if (recordsWrittenInFile > 0) {
                fileFooter = constructFileFooter(recordsWrittenInFile);
                out.write(fileFooter);
            }
        } catch (Exception e) {
            deleteAllFiles();
            _logger.debug(methodName, "Exception: " + e.getMessage());
            _logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CSVFileGenerator4NewUserApprovedProcess[writeDataInFile]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("CSVFileGenerator4NewUserApprovedProcess", methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
        	try{
        		if (out != null) {
                    out.close();
                }
        	}catch(Exception e){
        		_logger.errorTrace(methodName, e);
        	}
            if (selectStmt != null)
                try {
                    selectStmt.close();
                } catch (Exception e) {
                    _logger.errorTrace(methodName, e);
                }
            ;
            if (_logger.isDebugEnabled())
                _logger.debug(methodName, "Exiting ");
        }
    }

    private static int markProcessStatusAsComplete(Connection p_con, String p_processId) throws BTSLBaseException {
        final String methodName = "markProcessStatusAsComplete";
        if (_logger.isDebugEnabled())
            _logger.debug(methodName, " Entered:  p_processId:" + p_processId);

        int updateCount = 0;
        Date currentDate = new Date();
        ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        _processStatusVO.setProcessID(p_processId);
        _processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        _processStatusVO.setStartDate(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetail(p_con, _processStatusVO);
        } catch (Exception e) {
            _logger.errorTrace(methodName, e);
            _logger.error(methodName, "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CSVFileGenerator4NewUserApprovedProcess[markProcessStatusAsComplete]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("CSVFileGenerator4NewUserApprovedProcess", methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled())
                _logger.debug(methodName, "Exiting: updateCount=" + updateCount);
        } // end of finally
        return updateCount;
    }

    private static void deleteAllFiles() throws BTSLBaseException {
        final String methodName = "deleteAllFiles";
        if (_logger.isDebugEnabled())
            _logger.debug(methodName, " Entered: ");

        int size = 0;
        if (_fileNameLst != null)
            size = _fileNameLst.size();
        if (_logger.isDebugEnabled())
            _logger.debug(methodName, " : Number of files to be deleted " + size);
        String fileName = null;
        File newFile = null;
        for (int i = 0; i < size; i++) {
            try {
                fileName = (String) _fileNameLst.get(i);
                newFile = new File(fileName);
                newFile.delete();
                if (_logger.isDebugEnabled())
                    _logger.debug("", fileName + " file deleted");
            } catch (Exception e) {
                _logger.error(methodName, "Exception " + e.getMessage());
                _logger.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CSVFileGenerator4NewUserApprovedProcess[deleteAllFiles]", "", "", "", "Exception:" + e.getMessage());
                throw new BTSLBaseException("CSVFileGenerator4NewUserApprovedProcess", methodName, PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
            }
        }// end of for loop
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CSVFileGenerator4NewUserApprovedProcess[deleteAllFiles]", "", "", "", " Message: CSVFileGenerator4NewUserApprovedProcess process has found some error, so deleting all the files.");
        if (_fileNameLst != null && _fileNameLst.isEmpty())
            _fileNameLst.clear();
        if (_logger.isDebugEnabled())
            _logger.debug(methodName, " : Exiting.............................");
    }

    private static String constructFileHeader(long p_fileNumber, String p_fileLabel) {
        final String METHOD_NAME = "constructFileHeader";
        SimpleDateFormat sdf = null;
        try {
            sdf = new SimpleDateFormat(SqlParameterEncoder.encodeParams(_csvproperties.getProperty("DATE_TIME_FORMAT")));
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);
        }

        StringBuffer fileHeaderBuf = new StringBuffer("");
        fileHeaderBuf.append("Present Date and Time=" + BTSLDateUtil.getLocaleTimeStamp(sdf.format(new Date())));
        fileHeaderBuf.append("\n" + "File Number=" + p_fileNumber);
        fileHeaderBuf.append("\n" + p_fileLabel);
        fileHeaderBuf.append("\n" + "[STARTDATA]" + "\n");

        return fileHeaderBuf.toString();
    }

    private static String constructFileFooter(long p_noOfRecords) {
        StringBuffer fileHeaderBuf = null;
        fileHeaderBuf = new StringBuffer("");
        fileHeaderBuf.append("[ENDDATA]" + "\n");
        fileHeaderBuf.append("Number of records=" + p_noOfRecords);

        return fileHeaderBuf.toString();
    }

    public static void initialize(String p_processIDs) throws BTSLBaseException {
        final String methodName = "initialize";
        if (_logger.isDebugEnabled())
            _logger.debug(methodName, "Entered p_processIDs::" + p_processIDs);
        String processId = null;
        String[] inStrArray = null;
        try {
            CSVFileVO csvFileVO = null;
            inStrArray = p_processIDs.split(",");
            if (BTSLUtil.isNullArray(inStrArray))
                throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS3_NO_INTERFACEIDS);
            for (int i = 0, size = inStrArray.length; i < size; i++) {
                processId = inStrArray[i].trim();
                csvFileVO = new CSVFileVO();
                csvFileVO.setProcessId(processId);
                csvFileVO.setDirName(SqlParameterEncoder.encodeParams(_csvproperties.getProperty("NEW_USER_APPROVED_DIR")));
                csvFileVO.setExtName(SqlParameterEncoder.encodeParams(_csvproperties.getProperty("NEW_USER_APPROVED_EXT")));
                csvFileVO.setHeaderName(SqlParameterEncoder.encodeParams(_csvproperties.getProperty("NEW_USER_APPROVED_HEADER")));
                csvFileVO.setPrefixName(SqlParameterEncoder.encodeParams(_csvproperties.getProperty("NEW_USER_APPROVED_PREFIX_NAME")));

                if (_logger.isDebugEnabled())
                    _logger.debug("CSVFileGenerator4NewUserApprovedProcess[initialize]", "csvFileVO::" + csvFileVO);
                _csvMap.put(processId, csvFileVO);
            }
        } catch (BTSLBaseException be) {
            _logger.error(methodName, "BTSLBaseException be:" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _logger.errorTrace(methodName, e);
            _logger.error(methodName, "Exception e::" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CSVFileGenerator4NewUserApprovedProcess[initialize]", "String of p_processIDs ids=" + p_processIDs, "", "", "While initializing the processIDs for the CSV file generator process =" + processId + " get Exception=" + e.getMessage());
            throw new BTSLBaseException(InterfaceErrorCodesI.ERROR_CS3_NODE_INITIALIZATION);
        } finally {
            if (_logger.isDebugEnabled())
                _logger.debug(methodName, "Exited _csvMap::" + _csvMap);
        }
    }

    /**
     * @author diwakar
     * @date : 04-MAR-2014
     * @param p_beingProcessedDate
     * @param p_recordsWrittenInFile
     * @param p_fileName
     * @throws BTSLBaseException
     */
    private static void generateDataFileSummary(Date p_beingProcessedDate, long p_recordsWrittenInFile, String p_fileName) throws BTSLBaseException {
        final String methodName = "CSVFileGenerator4NewUserApprovedProcess";
        if (_logger.isDebugEnabled())
            _logger.debug(methodName, " Entered: generateDataFileSummary p_beingProcessedDate=" + p_beingProcessedDate + ", p_recordsWrittenInFile=" + p_recordsWrittenInFile + ", p_fileName=" + p_fileName);
        try {
            String processDateStr = BTSLUtil.getDateStringFromDate(p_beingProcessedDate);
            if (_fileRecordMap.isEmpty()) {
                _fileNameMap.put(p_fileName, p_recordsWrittenInFile);
                _fileRecordMap.put(processDateStr, _fileNameMap);
            } else {
                if (_fileRecordMap.containsKey(processDateStr)) {
                    _fileNameMap = (Hashtable<String, Long>) _fileRecordMap.get(processDateStr);
                    _fileNameMap.put(p_fileName, p_recordsWrittenInFile);
                    // Added By Diwakar on 07-MAR-2014
                    _fileRecordMap.put(processDateStr, _fileNameMap);
                    // Ended Here
                } else {
                    _fileNameMap = new Hashtable<String, Long>();
                    _fileNameMap.put(p_fileName, p_recordsWrittenInFile);
                    _fileRecordMap.put(processDateStr, _fileNameMap);
                }
            }

        } catch (Exception e) {
            _logger.errorTrace(methodName, e);
            _logger.debug(methodName, " generateDataFileSummary() While recoding file list Exception: " + e.getMessage());
        } finally {
            if (_logger.isDebugEnabled())
                _logger.debug(methodName, "Exiting generateDataFileSummary() ");
        }
    }

    /**
     * @author diwakar
     * @date : 04-MAR-2014
     * @param p_dirPath
     * @param p_fileEXT
     * @throws BTSLBaseException
     */
    private static void writeFileSummary(String p_dirPath, String p_fileEXT, String p_processId) throws BTSLBaseException {
        final String methodName = "CSVFileGenerator4NewUserApprovedProcess";
        if (_logger.isDebugEnabled())
            _logger.debug(methodName, " Entered: writeFileSummary() p_dirPath=" + p_dirPath + ", p_fileEXT=" + p_fileEXT);
        PrintWriter out = null;
        File newFile = null;
        try {
            String fileName = null;
            String fileData = null;
            String fileHeader = null;
            String processDate = null;
            // Changed on 05-MAR-2014
            // Commented below line
            // fileName=p_dirPath+File.separator+"fileRecordSumm_"+BTSLUtil.getDateTimeStringFromDate(new
            // Date(),"ddMMyyyy_HHmmss")+p_fileEXT;
            fileName = p_dirPath + File.separator + p_processId + "Trans_Stat_" + BTSLUtil.getDateStrForName(new Date()) + p_fileEXT;
            // Ended Here
            _logger.debug(methodName, " writeFileSummary() fileName=" + fileName);

            newFile = new File(fileName);
            newFile.createNewFile();
            // Added by Diwakar on 07-MAR-2014
            String transSeperator = null;
            try {
                transSeperator = SqlParameterEncoder.encodeParams(_csvproperties.getProperty("NEW_USER_APPROVED_DATA_SEPARATOR"));
            } catch (RuntimeException e) {
                _logger.errorTrace(methodName, e);
                transSeperator = ";";
            }
            if (newFile.exists()) {
                // _fileNameLst.add(fileName);
                out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
                fileHeader = "Date" + transSeperator + "Files_Number" + transSeperator + "File_Name" + transSeperator + "Total_Records";
                out.write(fileHeader + "\n");
                Hashtable<String, Long> fileRecord = null;
                _fileRecordMap.comparator();
                Set<String> keyList = _fileRecordMap.keySet();
                Iterator<String> itrProcessDate = keyList.iterator();
                Iterator<String> itrFile = null;
                String file = null;
                int i = 0;
                while (itrProcessDate.hasNext()) {
                    i = 0;
                    file = null;
                    fileData = null;
                    itrFile = null;
                    processDate = null;
                    processDate = itrProcessDate.next();
                    fileRecord = (Hashtable) _fileRecordMap.get(processDate);
                    itrFile = (fileRecord.keySet()).iterator();
                    fileData = processDate + transSeperator + new Integer(fileRecord.size()).toString() + transSeperator;
                    while (itrFile.hasNext()) {
                        file = itrFile.next().toString();
                        fileData = fileData + file + transSeperator + fileRecord.get(file).toString();
                        out.write(fileData + "\n");
                        i++;
                    }
                }
                out.flush();
            } else {
                _logger.error(methodName, " writeFileSummary() fileName=" + fileName + "does not exists on system.");
            }
        } catch (Exception e) {
            _logger.debug(methodName, "Exception writeFileSummary(): " + e.getMessage());
            _logger.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CSVFileGenerator4NewUserApprovedProcess[writeFileSummary]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(methodName, "writeFileSummary()", PretupsErrorCodesI.DWH_ERROR_EXCEPTION);
        } finally {
            if (out != null)
                out.close();
            if (_logger.isDebugEnabled())
                _logger.debug(methodName, "Exiting writeFileSummary() ");
        }
    }
}

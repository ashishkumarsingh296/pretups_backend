package com.btsl.pretups.processes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

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
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

public class UserBalanceScheduleReport {

    private static String _userBalenceReportGeneratePath = null;
    private static String _userBalanceFileLabel = null;
    private static String _userBalanceQuery = null;
    private static String _userBalanceFinalDirPath = null;
    private static String _userBalanceFileName = null;
    private static String _fileExt = ".csv";
    private static int _userbalanceMaxFileLength = 0;
    private static String _addHeaderFooter = null;
    private static String _reqCategory = null;
    private static Log _log = LogFactory.getLog(UserBalanceScheduleReport.class.getName());
    private static String _childDirectory;
    private static ArrayList _fileNameLst = new ArrayList();
    private static boolean _processExecuted = false;


    /**
     * to ensure no class instantiation 
     */
    private UserBalanceScheduleReport(){
    	
    }
    public static void main(String arg[]) {
        final String METHOD_NAME = "main";
        try {
            if (arg.length != 5) {
                _log.info(METHOD_NAME, "Usage : UserBalanceScheduleReport [Constants file] [LogConfig file] [Query File] [AddHeaderFooter] [Category]");
                return;
            }
            final File constantsFile = new File(arg[0]);
            if (!constantsFile.exists()) {
                _log.info(METHOD_NAME, "UserBalanceScheduleReport" + " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists()) {
                _log.info(METHOD_NAME, "UserBalanceScheduleReport" + " Logconfig File Not Found .............");
                return;
            }
            final File queryFile = new File(arg[2]);
            if (!queryFile.exists()) {
                _log.info(METHOD_NAME, "UserBalanceScheduleReport" + " Query File Not Found .............");
                return;
            }
            _addHeaderFooter = arg[3];
            if (BTSLUtil.isNullString(_addHeaderFooter)) {
                _log.info(METHOD_NAME, "UserBalanceScheduleReport" + " Add Header and Footer parameter not Found .............");
                return;
            }
            _reqCategory = arg[4].trim();
            if (BTSLUtil.isNullString(_reqCategory)) {
                _log.info(METHOD_NAME, "UserBalanceScheduleReport" + " Category code is not mentioned so we are picking the all users data ...........");
                _reqCategory = "ALL";
            }

            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            Constants.load(queryFile.toString());
        }// end of try
        catch (Exception e) {
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, " Error in Loading Files ...........................: " + e.getMessage());
            }
            _log.errorTrace(METHOD_NAME, e);
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
            process();
        } catch (BTSLBaseException be) {
            _log.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting..... ");
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * method call to generate o2c and c2c transaction data(through external
     * gateway) file
     */
    private static void process() throws BTSLBaseException {
        Date currentDate = new Date();
        ArrayList arrList = null;
        String _categoryName = null;
        Connection con = null;
        final String METHOD_NAME = "process";
        try {
            _log.debug(METHOD_NAME, "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            currentDate = BTSLUtil.getSQLDateFromUtilDate(currentDate);

            // getting all the required parameters from Constants.props
            loadConstantParameters();

            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_log.isDebugEnabled()) {
                    _log.debug(METHOD_NAME, " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalanceScheduleReport[process]",
                    "", "", "", "DATABASE Connection is NULL");
                return;
            }
            // method call to create master directory and child directory if
            // does not exist
            _childDirectory = createDirectory(_userBalenceReportGeneratePath);

            // method call to load transaction data
            arrList = loadChannelUserData(con, _userBalanceQuery);

            if (!("ALL".equalsIgnoreCase(_reqCategory))) {
                _categoryName = loadCategoryNameByCode(con, _reqCategory);
            } else {
                _categoryName = "ALL";
            }
            writeDataIntoFile(_childDirectory, arrList, _fileExt, _userBalanceFileLabel, _userbalanceMaxFileLength, _categoryName);

            // if the process is successful, transaction is commit, else
            // rollback
            if (_processExecuted) {
                moveFilesToFinalDirectory(_userBalenceReportGeneratePath, _userBalanceFinalDirPath);
                _log.debug("UserBalanceScheduleReport", METHOD_NAME, PretupsErrorCodesI.USER_BALANCE_SCHEDULE_EXECUTED_SUCCESS);
                con.commit();
            } else {
                deleteAllFiles();
                con.rollback();
                throw new BTSLBaseException("UserBalanceScheduleReport", METHOD_NAME, PretupsErrorCodesI.USER_BALANCE_SCHEDULE_EXCEPTION);
            }

        }// end of try
        catch (BTSLBaseException be) {
            _log.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _processExecuted = false;
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            if (_fileNameLst.size() > 0) {
                deleteAllFiles();
            }
            _processExecuted = false;
            _log.error(METHOD_NAME, "Exception : " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "UserBalanceScheduleReport[process]", "", "", "",
                " UserBalanceScheduleReport process could not be executed successfully.");
            throw new BTSLBaseException("UserBalanceScheduleReport", METHOD_NAME, PretupsErrorCodesI.USER_BALANCE_SCHEDULE_EXCEPTION);
        } finally {
            // if the status was marked as under process by this method call,
            // only then it is marked as complete on termination
            if (_processExecuted = false) {
                try {
                    con.commit();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
            } else {
                try {
                    con.rollback();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
            }
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception ex) {
                if (_log.isDebugEnabled()) {
                    _log.debug(METHOD_NAME, "Exception closing connection ");
                }
                _log.errorTrace(METHOD_NAME, ex);
            }

            _log.debug(METHOD_NAME, "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting..... ");
            }
        }
    }

    /**
     * Load all constants parameters from Constants.props Query from
     * query.props.
     */
    private static void loadConstantParameters() throws BTSLBaseException {
        final String METHOD_NAME = "loadConstantParameters";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Entered: ");
        }
        try {
            _userBalanceFileLabel = Constants.getProperty("USER_BALANCE_REPORT_FILE_LABEL");
            if (BTSLUtil.isNullString(_userBalanceFileLabel)) {
                _log.error(METHOD_NAME, " Could not find file label for user balance report in Configuration file.");
            } else {
                _log.debug(METHOD_NAME, " _userBalanceFileLabel=" + _userBalanceFileLabel);
            }

            _userBalenceReportGeneratePath = Constants.getProperty("USER_BALANCE_REPORT_GEN_PATH");
            if (BTSLUtil.isNullString(_userBalenceReportGeneratePath)) {
                _log.error(METHOD_NAME, " Could not find generated directory path for user balance report in Configuration file.");
            } else {
                _log.debug(METHOD_NAME, " _userBalenceReportGeneratePath=" + _userBalenceReportGeneratePath);
            }

            String query = null;
            if ("ALL".equalsIgnoreCase(_reqCategory)) {
                query = "USER_BALANCE_REPORT_QUERY_ALL";

            } else {
                query = "USER_BALANCE_REPORT_QUERY_MULTIPLE";
            }

            _userBalanceQuery = Constants.getProperty(query);
            if (BTSLUtil.isNullString(_userBalanceQuery)) {
                _log.error(METHOD_NAME, " Could not find query in query file .");
            } else {
                _log.debug(METHOD_NAME, " _userBalanceQuery=" + _userBalanceQuery);
            }

            _userBalanceFinalDirPath = Constants.getProperty("USER_BALANCE_REPORT_FINAL_DIRPATH");
            if (BTSLUtil.isNullString(_userBalanceFinalDirPath)) {
                _log.error(METHOD_NAME, " Could not find final directory path for user balance report in Configuration file.");
            } else {
                _log.debug(METHOD_NAME, "_userBalanceFinalDirPath =" + _userBalanceFinalDirPath);
            }

            try {
                _userbalanceMaxFileLength = Integer.parseInt(Constants.getProperty("USER_BALANCE_REPORT_FILE_LENGTH"));
            } catch (NumberFormatException ne) {
                _log.error(METHOD_NAME, " Could not find file length in Constants file.");
                _userbalanceMaxFileLength = 10000;
                _log.errorTrace(METHOD_NAME, ne);
            }
            _log.debug(METHOD_NAME, "_userbalanceMaxFileLength =" + _userbalanceMaxFileLength);

            _userBalanceFileName = Constants.getProperty("USER_BALANCE_REPORT_FILE_NAME");
            if (BTSLUtil.isNullString(_userBalanceFileName)) {
                _log.error(METHOD_NAME, " Could not find file name Configuration file.");
            } else {
                _log.debug(METHOD_NAME, "_userBalanceFileName =" + _userBalanceFileName);
            }

            _log.debug(METHOD_NAME, " Required information successfuly loaded from Constants.props...............: ");

            if (BTSLUtil.isNullString(_userBalanceFileLabel) || BTSLUtil.isNullString(_userBalenceReportGeneratePath) || BTSLUtil.isNullString(_userBalanceQuery) || BTSLUtil
                .isNullString(_userBalanceFinalDirPath) || BTSLUtil.isNullString(_userBalanceFileName)) {
                throw new BTSLBaseException("UserBalanceScheduleReport", METHOD_NAME, PretupsErrorCodesI.USER_BALANCE_SCHEDULE_PARAMETER_LOADED);
            }
        } catch (BTSLBaseException e) {
            _log.error(METHOD_NAME, "Exception : " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            final BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.USER_BALANCE_SCHEDULE_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalanceScheduleReport[loadConstantParameters]",
                "", "", "", "Message:" + btslMessage);
            throw new BTSLBaseException("UserBalanceScheduleReport", METHOD_NAME, PretupsErrorCodesI.USER_BALANCE_SCHEDULE_EXCEPTION);
        } catch (Exception e) {
            _log.error(METHOD_NAME, "Exception : " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            final BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.USER_BALANCE_SCHEDULE_EXCEPTION);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalanceScheduleReport[loadConstantParameters]",
                "", "", "", "Message:" + btslMessage);
            throw new BTSLBaseException("UserBalanceScheduleReport", METHOD_NAME, PretupsErrorCodesI.USER_BALANCE_SCHEDULE_EXCEPTION);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, " Exiting: ");
            }
        }
    }

    /**
     * This method will create directory at the path defined in Constants.props,
     * if it does not exist
     * 
     * @param p_directoryPathAndName
     *            String
     * @param p_processId
     *            String
     * @param p_beingProcessedDate
     *            Date
     * @throws BTSLBaseException
     * @return String
     */
    private static String createDirectory(String p_directoryPathAndName) throws BTSLBaseException {
        final String METHOD_NAME = "createDirectory";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Entered: p_directoryPathAndName=" + p_directoryPathAndName);
        }
        String dirName = null;

        try {
            boolean success = false;
            final File parentDir = new File(p_directoryPathAndName);
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            // child directory name includes a file name and being processed
            // date,
            dirName = p_directoryPathAndName;

            final File newDir = new File(dirName);
            if (!newDir.exists()) {
                success = newDir.mkdirs();
            } else {
                success = true;
            }
            if (!success) {
                throw new BTSLBaseException("UserBalanceScheduleReport", METHOD_NAME, PretupsErrorCodesI.COULD_NOT_CREATE_DIR);
            }
        } catch (BTSLBaseException be) {
            _log.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception ex) {
            _log.error(METHOD_NAME, "Exception : " + ex.getMessage());
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalanceScheduleReport[createDirectory]", "", "",
                "", "SQLException:" + ex.getMessage());
            throw new BTSLBaseException("UserBalanceScheduleReport", METHOD_NAME, PretupsErrorCodesI.USER_BALANCE_SCHEDULE_EXCEPTION);
        }// end of catch
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting dirName=" + dirName);
            }
        }
        return dirName;
    }

    /**
     * This method will fetch all the required transactions data from database
     * 
     * @param p_con
     *            Connection
     * @param p_beingProcessedDate
     *            Date
     * @param p_query
     *            String
     * @return map
     * @throws SQLException
     *             ,Exception
     */
    private static ArrayList loadChannelUserData(Connection p_con, String p_query) throws BTSLBaseException {
        final String METHOD_NAME = "loadChannelUserData";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Entered: p_query=" + p_query);
        }

        PreparedStatement channelTxnSelectPstmt = null;
        ResultSet rs = null;
        final ArrayList arrList = new ArrayList();
        String _tempString = null;
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, "channel user data select query:" + p_query + ",_reqCategory=" + _reqCategory);
        }
        try {
            String category = "";

            // Changed for SQL injection Prevention
            String[] categoryCode = null;
            int index = 1;
            if (!("ALL".equalsIgnoreCase(_reqCategory))) {
                categoryCode = _reqCategory.split(",");
                for (int i = 0; i < categoryCode.length; i++) {
                    if (!("".equals(category))) {
                        category = category + ",";
                    }
                    category += "?";
                }
                p_query = p_query.replaceAll("REPLACE_DATA", category);
            }
            channelTxnSelectPstmt = p_con.prepareStatement(p_query);
            if (categoryCode != null) {
                for (int j = 0; j < categoryCode.length; j++) {
                    channelTxnSelectPstmt.setString(index++, categoryCode[j]);
                }
            }
            rs = channelTxnSelectPstmt.executeQuery();
            // end of change
            _log.debug(METHOD_NAME, "Memory after loading channel transaction data: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime
                .getRuntime().freeMemory() / 1049576);

            while (rs.next()) {
                _tempString = rs.getString(1);
                arrList.add(_tempString);
            }
        } catch (SQLException sqe) {
            _log.error(METHOD_NAME, "SQLException " + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalanceScheduleReport[loadChannelUserData]", "",
                "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("UserBalanceScheduleReport", METHOD_NAME, PretupsErrorCodesI.USER_BALANCE_SCHEDULE_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _log.error(METHOD_NAME, "Exception : " + ex.getMessage());
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalanceScheduleReport[loadChannelUserData]", "",
                "", "", "SQLException:" + ex.getMessage());
            throw new BTSLBaseException("UserBalanceScheduleReport", METHOD_NAME, PretupsErrorCodesI.USER_BALANCE_SCHEDULE_EXCEPTION);
        }// end of catch
        finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }
            if (channelTxnSelectPstmt != null) {
                try {
                    channelTxnSelectPstmt.close();
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }

            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting " + arrList.size());
            }
        }// end of finally
        return arrList;
    }

    /**
     * Call method to generate file and write data in the file
     * 
     * @param p_gatewayCodeType
     *            String []
     * @param p_dirName
     *            String
     * @param p_fileLabel
     *            String
     * @param p_processUpto
     *            Date
     * @param p_fileEXT
     *            String
     * @param p_maxFileLength
     *            long
     * @param p_map
     *            HashMap
     * @param p_gatewayCode
     *            String []
     * @param p_type
     *            String []
     * @return void
     * @throws Exception
     */
    private static void writeDataIntoFile(String p_dirName, ArrayList p_arrList, String p_fileExt, String p_fileLabel, int p_maxFileLength, String p_categoryName) throws BTSLBaseException {
        final String METHOD_NAME = "writeDataIntoFile";
        if (_log.isDebugEnabled()) {
            _log
                .debug(
                    METHOD_NAME,
                    " Entered: p_dirName=" + p_dirName + " ,p_arrList=" + p_arrList.size() + ",p_fileExt=" + p_fileExt + ",p_fileLabel=" + p_fileLabel + ",p_maxFileLength=" + p_maxFileLength + ",p_categoryName=" + p_categoryName);
        }
        int recordsWrittenInFile = 1;
        PrintWriter out = null;
        int fileNumber = 0;
        File newFile = null;
        String fileName = null;
        String tempFileName = null;
        String fileData = null;
        String fileHeader = null;
        String fileFooter = null;
        Iterator itr = null;
        final SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_hhmmss");
        final Date currentDate = new Date();
        try {
            // generating file name
            fileNumber = 1;
            tempFileName = _userBalanceFileName.trim() + "_" + sdf.format(currentDate);
            // if the length of file number is 1, two zeros are added as prefix
            if (Integer.toString(fileNumber).length() == 1) {
                fileName = p_dirName + File.separator + tempFileName + "00" + fileNumber + p_fileExt;
            }
            _log.debug("WriteDataIntoFile", "  fileName=" + fileName);

            newFile = new File(fileName);
            _fileNameLst.add(fileName);
            out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
            // addition of header and footer optional on the basis of passed
            // argument Y or N

            if ("Y".equalsIgnoreCase(_addHeaderFooter)) {
                fileHeader = constructFileHeader(fileNumber, p_fileLabel, p_categoryName);
                out.write(fileHeader);
            }
            // traverse list and write data into csv file

            itr = p_arrList.iterator();
            while (itr.hasNext()) {
                fileData = recordsWrittenInFile + "," + itr.next().toString();
                out.write(fileData + "\n");
                recordsWrittenInFile++;
                if (recordsWrittenInFile > p_maxFileLength) {
                    if ("Y".equalsIgnoreCase(_addHeaderFooter)) {
                        fileFooter = constructFileFooter(recordsWrittenInFile - 1);
                        out.write(fileFooter);
                    }
                    recordsWrittenInFile = 1;
                    fileNumber = fileNumber + 1;
                    //out.close();

                    // if the length of file number is 1, two zeros are added as
                    // prefix
                    if (Integer.toString(fileNumber).length() == 1) {
                        fileName = p_dirName + File.separator + tempFileName + "00" + fileNumber + p_fileExt;
                    } else if (Integer.toString(fileNumber).length() == 2) {
                        fileName = p_dirName + File.separator + tempFileName + "0" + fileNumber + p_fileExt;
                    } else if (Integer.toString(fileNumber).length() == 3) {
                        fileName = p_dirName + File.separator + tempFileName + fileNumber + p_fileExt;
                    }

                    _log.debug(METHOD_NAME, "  fileName=" + fileName);

                    newFile = new File(fileName);
                    BTSLUtil.closeOpenStream(out, newFile);
                    _fileNameLst.add(fileName);
                    //out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
                    // addition of header and footer optional on the basis of
                    // entry in Constants.props
                    if ("Y".equalsIgnoreCase(_addHeaderFooter)) {
                        fileHeader = constructFileHeader(fileNumber, p_fileLabel, p_categoryName);
                        out.write(fileHeader);
                    }
                }
            }
            // clear array list
            p_arrList.clear();

            // if number of records are not zero then footer is appended
            if (recordsWrittenInFile > 1) {
                _processExecuted = true;
                // header and footer optional on the basis of entry in
                // Constants.props
                if ("Y".equalsIgnoreCase(_addHeaderFooter)) {
                    fileFooter = constructFileFooter(recordsWrittenInFile - 1);
                    out.write(fileFooter);
                }
            } else {
                boolean isDeleted = newFile.delete();
                if(isDeleted){
                 _log.debug(METHOD_NAME, "File deleted successfully");
                }
                _fileNameLst.remove(_fileNameLst.size() - 1);
            }
        } catch (Exception e) {
            deleteAllFiles();
            _log.debug(METHOD_NAME, "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalanceScheduleReport[writeDataIntoFile]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("UserBalanceScheduleReport", METHOD_NAME, PretupsErrorCodesI.USER_BALANCE_SCHEDULE_EXCEPTION);
        } finally {
        	try{
        		if (out != null) {
                    out.close();
                }
        	}catch(Exception e){
        		_log.errorTrace(METHOD_NAME, e);
        	}
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting _processExecuted=" + _processExecuted);
            }
        }
    }

    /**
     * This method will delete all the files if some error is encountered after
     * file creation and files need to be deleted.
     * 
     * @throws BTSLBaseException
     * @return void
     */
    private static void deleteAllFiles() throws BTSLBaseException {
        final String METHOD_NAME = "deleteAllFiles";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Entered: ");
        }
        int size = 0;
        size = _fileNameLst.size();

        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " : Number of files to be deleted " + size);
        }
        String fileName = null;
        File newFile = null;
        for (int i = 0; i < size; i++) {
            try {
                fileName = (String) _fileNameLst.get(i);
                newFile = new File(fileName);
                boolean isDeleted = newFile.delete();
                if(isDeleted){
                 _log.debug(METHOD_NAME, "File deleted successfully");
                }
                if (_log.isDebugEnabled()) {
                    _log.debug("", fileName + " file deleted");
                }
            } catch (Exception e) {
                _log.error(METHOD_NAME, "Exception " + e.getMessage());
                _log.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalanceScheduleReport[deleteAllFiles]", "",
                    "", "", "Exception:" + e.getMessage());
                throw new BTSLBaseException("UserBalanceScheduleReport", METHOD_NAME, PretupsErrorCodesI.USER_BALANCE_SCHEDULE_EXCEPTION);
            }
        }// end of for loop
        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalanceScheduleReport[deleteAllFiles]", "", "", "",
            " Message: UserBalanceScheduleReport process has found some error, so deleting all the files.");
        if (_fileNameLst.isEmpty()) {
            _fileNameLst.clear();
        }
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " : Exiting.............................");
        }
    }

    /**
     * This method is used to constuct file header
     * 
     * @param p_beingProcessedDate
     *            Date
     * @param p_fileNumber
     *            long
     * @param p_fileLabel
     *            String
     * @return String
     */
    private static String constructFileHeader(long p_fileNumber, String p_fileLabel, String p_categoryName) {
        final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.TIMESTAMP_DDMMYYYYHHMMSS);
        final StringBuffer fileHeaderBuf = new StringBuffer("");
        fileHeaderBuf.append("\n" + " Schedule Date=" + sdf.format(new Date()));
        fileHeaderBuf.append("\n" + " Channel User Category=" + p_categoryName);
        fileHeaderBuf.append("\n" + " File Number=" + p_fileNumber);
        fileHeaderBuf.append("\n" + p_fileLabel + "\n");

        return fileHeaderBuf.toString();
    }

    /**
     * This method is used to constuct file footer
     * 
     * @param p_noOfRecords
     *            long
     * @return String
     */
    private static String constructFileFooter(long p_noOfRecords) {
        StringBuffer fileHeaderBuf = null;
        fileHeaderBuf = new StringBuffer("");
        fileHeaderBuf.append(" Number of records=" + p_noOfRecords);
        return fileHeaderBuf.toString();
    }

    /**
     * This method will copy all the created files to another location. the
     * process will generate files in a particular directroy. if the process
     * thats has to read files strarts before copletion of the file generation,
     * errors will occur. so a different directory is created and files are
     * moved to that final directory.
     * 
     * @param p_oldDirectoryPath
     *            String
     * @param p_finalDirectoryPath
     *            String
     * @throws BTSLBaseException
     * @return String
     */
    private static void moveFilesToFinalDirectory(String p_oldDirectoryPath, String p_finalDirectoryPath) throws BTSLBaseException {
        final String METHOD_NAME = "moveFilesToFinalDirectory";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Entered: p_oldDirectoryPath=" + p_oldDirectoryPath + " p_finalDirectoryPath=" + p_finalDirectoryPath);
        }

        String oldFileName = null;
        String newFileName = null;
        File oldFile = null;
        File newFile = null;
        final Date currentDate = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
        File parentDir = new File(p_finalDirectoryPath);
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        // child directory name includes a file name and being processed date,
        // month and year
        final String oldDirPath = p_oldDirectoryPath + File.separator + "_" + sdf.format(currentDate);
        final String newDirPath = p_finalDirectoryPath + File.separator + "_" + sdf.format(currentDate);
        File oldDir = new File(oldDirPath);
        File newDir = new File(newDirPath);
        if (!newDir.exists()) {
            newDir.mkdirs();
        }

        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " newDirPath=" + newDirPath);
        }

        final int size = _fileNameLst.size();
        try {
            for (int i = 0; i < size; i++) {
                oldFileName = (String) _fileNameLst.get(i);
                oldFile = new File(oldFileName);
                newFileName = oldFileName.replace(p_oldDirectoryPath, p_finalDirectoryPath);
                newFile = new File(newFileName);
                if(oldFile.renameTo(newFile))
                {
                    _log.debug(METHOD_NAME, "File renamed successfully");
                }
                if (_log.isDebugEnabled()) {
                    _log.debug(METHOD_NAME, " File " + oldFileName + " is moved to " + newFileName);
                }
            }// end of for loop
            _fileNameLst.clear();
            if (oldDir.exists()) {
            	boolean isDeleted = oldDir.delete();
                if(isDeleted){
                 _log.debug(METHOD_NAME, "Directory deleted successfully");
                }
            }
            _log.debug(METHOD_NAME, " File " + oldFileName + " is moved to " + newFileName);

        } catch (Exception e) {
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalanceScheduleReport[moveFilesToFinalDirectory]",
                "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("UserBalanceScheduleReport", "deleteAllFiles", PretupsErrorCodesI.USER_BALANCE_SCHEDULE_EXCEPTION);
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
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting.. ");
            }
        } // end of finally
    }

    /**
     * This method will fetch all the required transactions data from database
     * 
     * @param p_con
     *            Connection
     * @param p_beingProcessedDate
     *            Date
     * @param p_query
     *            String
     * @return map
     * @throws SQLException
     *             ,Exception
     */
    private static String loadCategoryNameByCode(Connection p_con, String p_category) throws BTSLBaseException {
        final String METHOD_NAME = "loadCategoryNameByCode";
        if (_log.isDebugEnabled()) {
            _log.debug(METHOD_NAME, " Entered: p_category=" + p_category);
        }

        PreparedStatement channelTxnSelectPstmt = null;
        ResultSet rs = null;
        final ArrayList arrList = new ArrayList();
        final StringBuffer sbf = new StringBuffer();
        String _categoryName = "";

        try {
            // changed for sql injection prevention
            int startIndex = 1;
            sbf.append(" SELECT category_name FROM CATEGORIES WHERE category_code IN (");

            final String[] categoryCode = _reqCategory.split(",");
            for (int i = 0; i < categoryCode.length; i++) {
                if (i != 0) {
                    sbf.append(",");
                }
                sbf.append("?");
            }
            sbf.append(")");

            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "query=" + sbf.toString());
            }

            channelTxnSelectPstmt = p_con.prepareStatement(sbf.toString());
            for (int i = 0; i < categoryCode.length; i++) {
                channelTxnSelectPstmt.setString(startIndex++, categoryCode[i]);
            }
            rs = channelTxnSelectPstmt.executeQuery();
            // end of change
            while (rs.next()) {
                _categoryName = _categoryName + rs.getString(1) + ",";
            }
            final int index = _categoryName.lastIndexOf(",");
            _categoryName = _categoryName.substring(0, index);
        } catch (SQLException sqe) {
            _log.error(METHOD_NAME, "SQLException " + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalanceScheduleReport[loadCategoryNameByCode]",
                "", "", "", "SQLException:" + sqe.getMessage());
            throw new BTSLBaseException("UserBalanceScheduleReport", METHOD_NAME, PretupsErrorCodesI.USER_BALANCE_SCHEDULE_EXCEPTION);
        }// end of catch
        catch (Exception ex) {
            _log.error(METHOD_NAME, "Exception : " + ex.getMessage());
            _log.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalanceScheduleReport[loadCategoryNameByCode]",
                "", "", "", "SQLException:" + ex.getMessage());
            throw new BTSLBaseException("UserBalanceScheduleReport", METHOD_NAME, PretupsErrorCodesI.USER_BALANCE_SCHEDULE_EXCEPTION);
        }// end of catch
        finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }
            if (channelTxnSelectPstmt != null) {
                try {
                    channelTxnSelectPstmt.close();
                } catch (Exception ex) {
                    _log.errorTrace(METHOD_NAME, ex);
                }
            }

            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Exiting " + _categoryName);
            }
        }// end of finally
        return _categoryName;
    }
}

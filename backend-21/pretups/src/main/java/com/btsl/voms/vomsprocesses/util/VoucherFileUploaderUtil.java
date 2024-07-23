package com.btsl.voms.vomsprocesses.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.SSLException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.TypesI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.login.LoginDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
// commented for DB2
import com.btsl.util.Constants;
import com.btsl.voms.vomscommon.VOMSI;

/**
 * @(#)VoucherFileUploaderUtil.java
 *                                  Copyright(c) 2005, Bharti Telesoft Ltd.
 *                                  All Rights Reserved
 * 
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Author Date History
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  ---------
 *                                  Siddhartha Srivastava 21/07/06 Initial
 *                                  Creation
 *                                  Gurjeet Singh Bedi 21/07/06 Modified
 *                                  --------------------------------------------
 *                                  --------------------------------------------
 *                                  --------
 * 
 */
public class VoucherFileUploaderUtil {
    private static Log _log = LogFactory.getLog(VoucherFileUploaderUtil.class.getName());
    public final static String AUTH_TYPE_CAT = "CAT";
    public final static String AUTH_TYPE_ROLE = "ROLE";
    public final static String UPLOAD_ROLE_CODE = "VOMSUPLD";

    /**
     * This method is used to accept data from the user. If user doesn't enter
     * data it asks again and again
     * 
     * @param displayString
     * @return
     */
    public static String dataFromUser(String displayString) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug(" DataFromUser ", " Entered:: " + displayString);
        }
        String data = "";
        boolean flag = true;
        final String METHOD_NAME = "dataFromUser";
        try {
            while (flag) {

                data = getUserInputFromConsole();
                if (!BTSLUtil.isNullString(data)) {
                    flag = false;
                } else if (_log.isDebugEnabled()) {
                    _log.debug(" dataFromUser ", " \n Please Enter Data. Field Can't be left Blank.\n");
                }
            }
        } catch (BTSLBaseException be) {
            _log.error(" dataFromUser ", "BTSLBaseException" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.error(" dataFromUser ", "Exception" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileUploaderUtil[dataFromUser]", "", "", "", "Not able to get input data from console from user");
            throw new BTSLBaseException("VoucherFileUploaderUtil", "dataFromUser", PretupsErrorCodesI.VOUCHER_UPLOAD_INTERNAL_ERROR_DATA_RETRIEVAL);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" dataFromUser ", " Exiting with :: " + data);
            }
        }
        return data;
    }

    /**
     * This method accepts data from the user in from of number
     * 
     * @param displayString
     * @return
     */
    public static int dataFromUserLong(String displayString) throws BTSLBaseException {
        final String methodName = "dataFromUserLong";
        if (_log.isDebugEnabled()) {
            _log.debug(" dataFromUserLong ", " Entered:: " + displayString);
        }
        String data = "";
        int numberValue = 0;
        boolean flag = true;
        final String METHOD_NAME = "dataFromUserLong";
        try {
            while (flag) {

                data = getUserInputFromConsole();
                if (!BTSLUtil.isNullString(data)) {
                    try {
                        numberValue = Integer.parseInt(data);
                        if (numberValue <= 0) {
                            _log.info(methodName, "\n The Value Should be greater than 0. Please Enter Again.\n");
                        } else {
                            flag = false;
                        }
                    } catch (Exception e) {
                        _log.errorTrace(METHOD_NAME, e);
                        _log.info(methodName, "\n Invalid Value. Please Enter Again.\n");
                    }
                } else {
                    _log.info(methodName, "\n Please Enter Data. Field Can't be left Blank.\n");
                }
            }
        } catch (BTSLBaseException be) {
            _log.error("VoucherFileUploaderUtil[dataFromUserLong]", "BTSLBaseException =" + be.getMessage());
            throw be;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(" dataFromUserLong ", "Exception e=" + e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileUploaderUtil[dataFromUserLong]", "", "", "", "Not able to get input data from console from user");
            throw new BTSLBaseException("VoucherFileUploaderUtil", "dataFromUserLong", PretupsErrorCodesI.VOUCHER_UPLOAD_INTERNAL_ERROR_DATA_RETRIEVAL);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" dataFromUserLong ", " dataFromUserLong Exiting :: " + numberValue);
            }
        }
        return numberValue;
    }

    /**
     * This method accepts raw input from user
     * 
     * @return
     */
    public static String getUserInputFromConsole() throws BTSLBaseException {
    	 final String METHOD_NAME = "getUserInputFromConsole";
    	if (_log.isDebugEnabled()) {
            _log.debug(" getUserInputFromConsole ", " Entered ...... ");
        }
        String line = "";
        InputStreamReader stdin = new InputStreamReader(System.in);
        try(BufferedReader console = new BufferedReader(stdin);)
        {
       
        
            line = console.readLine();
        } catch (IOException ioex) {
            _log.errorTrace(METHOD_NAME, ioex);
            _log.error(" getUserInputFromConsole ", " Getting Exception e=" + ioex.getMessage());
            throw new BTSLBaseException("VoucherFileUploaderUtil", "getUserInputFromConsole", PretupsErrorCodesI.VOUCHER_UPLOAD_INTERNAL_ERROR_DATA_RETRIEVAL);
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error(" getUserInputFromConsole ", " Getting Exception e=" + e.getMessage());
            throw new BTSLBaseException("VoucherFileUploaderUtil", "getUserInputFromConsole", PretupsErrorCodesI.VOUCHER_UPLOAD_INTERNAL_ERROR_DATA_RETRIEVAL);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" getUserInputFromConsole ", " Exiting with data...... " + line);
            }
        }
        return line;
    }

    /**
     * This method compares whether the user given file exists or not
     * 
     * @param fileNameLike
     * @param path
     * @return
     */
    public static boolean isFileExists(String fileName, String path) {
        if (_log.isDebugEnabled()) {
            _log.debug(" isFileExists", " Entered with fileName=" + fileName + "  path=" + path);
        }
        boolean flag = false;
        String userdir = path;
        if (BTSLUtil.isNullString(userdir)) {
            userdir = System.getProperty("user.dir");
        }
        File f = new File(userdir.trim());
        String[] directoryList = null;
        directoryList = f.list();
        if (directoryList == null) {
            directoryList = new String[0];
        }
        String rough = BTSLUtil.NullToString(fileName).trim();
        int dirLength = directoryList.length;
        for (int i = 0; i < dirLength; i++) {
            if (directoryList[i].indexOf(rough) != -1) {
                flag = true;
                break;
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug(" isFileExists ", " Exiting with flag=" + flag);
        }
        return flag;
    }

    /**
     * This method will move the processed file in seperate folder
     * 
     * @param p_fileName
     * @param p_file
     * @return boolean
     */
    public static void moveFileToAnotherDirectory(String p_fileName1, String pathWithFileName1, String path2) throws BTSLBaseException, ParseException {
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered with :: p_fileName1=");
        	loggerValue.append(p_fileName1);
        	loggerValue.append("  pathWithFileName1=");
        	loggerValue.append(pathWithFileName1);
        	loggerValue.append("path2=");
        	loggerValue.append(path2);
            _log.debug(" moveFileToAnotherDirectory ", loggerValue);
        }
        boolean flag = false;
        String[] fileExt = null;
        try {
            // added to concatenate current date and time with file name
            // (manisha jain 11/01/08)
            fileExt = p_fileName1.split("\\.");
            p_fileName1 = (fileExt[0]).concat("_").concat(BTSLUtil.getFileNameStringFromDate(new Date()).concat(".").concat(fileExt[1]));
            File fileRead = new File(pathWithFileName1);
            File fileArchive = new File(path2);
            if (!fileArchive.isDirectory()) {
                fileArchive.mkdirs();
            }
            fileArchive = new File(path2 + File.separator + p_fileName1);
            flag = fileRead.renameTo(fileArchive);

            File tempFile = new File(path2 + File.separator + p_fileName1);
            if (!tempFile.exists()) {
                _log.debug(" moveFileToAnotherDirectory ", " Unable to Move File to backup location (" + path2 + ")+ Please Contact System admin ...............");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileUploaderUtil[moveFileToAnotherDirectory]", "", "", "", " Unable to Move File to backup location (" + path2 + ")");
                throw new BTSLBaseException("VoucherFileUploaderUtil ", " moveFileToAnotherDirectory ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
            }
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileUploaderUtil[moveFileToAnotherDirectory]", "", "", "", "Successfully moved the File " + p_fileName1 + " to backup location (" + path2 + ")");
        } catch (BTSLBaseException be) {
            _log.error("VoucherFileUploaderUtil[moveFileToAnotherDirectory]", "BTSLBaseException =" + be.getMessage());
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VoucherFileUploaderUtil[moveFileToAnotherDirectory]","","","","BTSLBaseException:"+be.getMessage());
            throw be;
        }// end of BTSLBaseException
        catch (ParseException pe) {
            _log.error("VoucherFileUploaderUtil[moveFileToAnotherDirectory]", "ParseException =" + pe.getMessage());
            throw pe;
        }// end of ParseException
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" moveFileToAnotherDirectory ", " Exiting with flag=" + flag);
                // EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"VoucherFileUploaderUtil[moveFileToAnotherDirectory]","","","","Successfully moved the File "+p_fileName1+" to backup location ("+path2+")");
            }
        }

    }// end of moveFileToArchive

    /**
     * This method loads the next sequence number from the IDS table against the
     * SEQNUM ID
     * 
     * @param con
     * @param docYear
     * @param docType
     * @param docLocation
     * @return long
     * @throws SSLException
     * @throws Exception
     */
    public static long loadNextSeqNumber(Connection con, int docYear, String docType, String docLocation) throws BTSLBaseException {
        final String METHOD_NAME = "loadNextSeqNumber";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("Entered in loadNextSeqNumber in VoucherFileUploaderUtil:");
        	loggerValue.append(docYear);
        	loggerValue.append(" ");
        	loggerValue.append(docType);
        	loggerValue.append(" ");
            _log.debug(" loadNextSeqNumber ", loggerValue);
        }
        long seriesNum = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery = null;
        // DB220120123for update WITH RS
        if (PretupsI.DATABASE_TYPE_DB2.equalsIgnoreCase(Constants.getProperty("databasetype"))) {
            sqlQuery = "SELECT last_no FROM ids WHERE id_year=? AND id_type=? AND network_code=? FOR UPDATE with RS";
        } else {
            sqlQuery = "SELECT last_no FROM ids WHERE id_year=? AND id_type=? AND network_code=? FOR UPDATE NOWAIT";
        }
        try {
            ps = con.prepareStatement(sqlQuery);
            ps.setString(1, String.valueOf(docYear));
            ps.setString(2, docType);
            ps.setString(3, docLocation);
            rs = ps.executeQuery();

            if (_log.isDebugEnabled()) {
                _log.debug(" loadNextSeqNumber ", "Inside loadNextSeqNumber after executing the query");
            }
            if (rs.next()) {
                seriesNum = rs.getLong(1);
                seriesNum = seriesNum + 1;
            } // end of if rs.next()
            if (seriesNum == 0) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileProcessor[loadNextSeqNumber]", "", "", "", " No entry found in IDs for updation of voucher sequence number");
                throw new BTSLBaseException("VoucherFileUploaderUtil ", " loadNextSeqNumber ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }
            return seriesNum;
        }// end of try
        catch (BTSLBaseException be) {
            _log.error("VoucherFileUploaderUtil[loadNextSeqNumber]", "BTSLBaseException =" + be.getMessage());
            // EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"VoucherFileUploaderUtil[loadNextSeqNumber]","","","","BTSLBaseException:"+be.getMessage());
            throw be;
        }// end of BTSLBaseException
        catch (SQLException sqle) {
            _log.errorTrace(METHOD_NAME, sqle);
            _log.error(" loadNextSeqNumber ", " SQL Exception in loadNextSeqNumber of VoucherFileUploadeUtil:" + sqle.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileUploaderUtil[loadNextSeqNumber]", "", "", "", "Exception:" + sqle.getMessage());
            throw new BTSLBaseException("VoucherFileUploaderUtil", "loadNextSeqNumber", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        }// end of catch
        catch (Exception e) {
            _log.error(" loadNextSeqNumber ", " Exception Inside loadNextSeqNumber in VoucherFileUploadeUtil" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileUploaderUtil[loadNextSeqNumber]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("VoucherFileUploaderUtil", "loadNextSeqNumber", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(" loadNextSeqNumber ", "Exiting from the loadNextSeqNumber method in VoucherFileUploadeUtil seriesNum=" + seriesNum);
            }
        } // end of finally

    } // end of getNextDocSeries

    /**
     * This method updates the IDS table after the final sequence number has
     * been used for the current
     * upload of voucher file.This helps in maintaining of proper sequence
     * number across multiple loading
     * 
     * @param con
     * @param docYear
     * @param docType
     * @param docLocation
     * @param seriesNum
     * @return int
     * @throws SSLException
     * @throws Exception
     */
    public static int updateFinalSeqNumber(Connection con, int docYear, String docType, String docLocation, long seriesNum) throws BTSLBaseException {
        final String METHOD_NAME = "updateFinalSeqNumber";
        if (_log.isDebugEnabled()) {
            _log.debug("updateFinalSeqNumber", "Entered in updateFinalSeqNumber in VoucherFileUploaderUtil:" + docYear + " " + docType + " " + docLocation);// companycode
        }
        // by
        // sidd
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery = null;
        int updateNum = 0;
        try {
            sqlQuery = "UPDATE ids SET last_no=? WHERE id_year=? AND id_type=? AND network_code=?";
            ps = con.prepareStatement(sqlQuery);
            if (_log.isDebugEnabled()) {
                _log.debug("updateFinalSeqNumber", "After preparement statement,Inside updateFinalSeqNumber in VoucherFileUploaderUtil");
            }
            ps.setLong(1, seriesNum);
            ps.setString(2, String.valueOf(docYear));
            ps.setString(3, docType);
            ps.setString(4, docLocation);

            updateNum = ps.executeUpdate();
            if (updateNum == 0) {
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileProcessor[updateFinalSeqNumber]", "", "", "", " No entry found in IDs for updation of voucher sequence number");
                throw new BTSLBaseException("VoucherFileUploaderUtil ", " updateFinalSeqNumber ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_CONFIG_ERROR);
            }// end of updateNum==0
        }// end of try
        catch (BTSLBaseException be) {
            _log.error("VoucherFileUploaderUtil[updateFinalSeqNumber]", "BTSLBaseException =" + be.getMessage());
            throw be;
        } catch (SQLException sqle) {
            _log.error("updateFinalSeqNumber", "SQL Exception in updateFinalSeqNumber of VoucherFileUploadeUtil:" + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileUploaderUtil[updateFinalSeqNumber]", "", "", "", "SQLException:" + sqle.getMessage());
            throw new BTSLBaseException("VoucherFileUploaderUtil", "updateFinalSeqNumber", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        }// end of catch
        catch (Exception e) {
            _log.error("updateFinalSeqNumber", "Exception Inside updateFinalSeqNumber in VoucherFileUploadeUtil" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileUploaderUtil[updateFinalSeqNumber]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("VoucherFileUploaderUtil", "updateFinalSeqNumber", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        }// end of catch
        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("updateFinalSeqNumber", "Exiting from the updateFinalSeqNumber method in VoucherFileUploadeUtil");
            }
        } // end of finally
        return updateNum;
    } // end of getNextDocSeries

    /**
     * This method returns total number of error
     * 
     * @param str
     * @return int
     */
    public static int countError(String str) {
        int count = 0;
        int index = 0;
        int strLength = BTSLUtil.NullToString(str).length();
        while (true) {

            index = str.indexOf("\n");
            if (index != -1 && strLength > index + 2) {
                str = str.substring(index + 2);
                strLength = str.length();
            } else {
                break;
            }
            count++;
        }
        return count;
    }

    /**
     * Thsi method is used for checking whether any user with the given loginID
     * and password exists
     * 
     * @param p_con
     * @param p_loginID
     * @param p_password
     * @param p_locale
     * @return ChannelUserVO
     * @throws BTSLBaseException
     */
    public static ChannelUserVO validateUser(Connection p_con, String p_loginID, String p_password, String p_locale) throws BTSLBaseException {
        final String methodName = "validateUser";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append(" Entered with p_loginID= ");
        	loggerValue.append(p_loginID);
        	loggerValue.append(" p_password=");
        	loggerValue.append(p_password);
        	loggerValue.append(" p_locale=");
        	loggerValue.append(p_locale);
            _log.debug(" validateUser ", loggerValue);
        }
        ChannelUserVO channelUserVO = null;
        try {
            Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            channelUserVO = (new LoginDAO().loadUserDetails(p_con, p_loginID, p_password, locale));
            if (channelUserVO == null) {
                _log.info(methodName, " User ID/Password is wrong (No such user exists)........... ");
                _log.error(" validateUser ", " User ID/Password is wrong ...........");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileUploaderUtil[validateUser]", "", "", "", "User Not authrorized,User ID/Password is wrong");
                throw new BTSLBaseException("VoucherFileUploaderUtil", "validateUser", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_INVALID_USER);
            }
            // Start Moldova cahnges by Ved 24/07/07
            String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            OperatorUtilI operatorUtili = null;
            try {
                operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileUploaderUtil[validateUser]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
            }
            if (!operatorUtili.validateTransactionPassword(channelUserVO, p_password)) {
                _log.info(methodName, " User ID/Password is wrong (No such user exists)........... ");
                _log.error(" validateUser ", " User ID/Password is wrong ...........");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileUploaderUtil[validateUser]", "", "", "", "User Not authrorized, User ID/Password is wrong");
                throw new BTSLBaseException("VoucherFileUploaderUtil", "validateUser", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_INVALID_USER);
            }

            /*
             * if(!BTSLUtil.decryptText(channelUserVO.getPassword()).
             * equalsIgnoreCase(p_password))
             * {
             * System.out.println(
             * " User ID/Password is wrong (No such user exists)........... ");
             * _log.error(" validateUser ",
             * " User ID/Password is wrong ...........");
             * EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,
             * EventStatusI
             * .RAISED,EventLevelI.INFO,"VoucherFileUploaderUtil[validateUser]"
             * ,"","","","User Not authrorized, User ID/Password is wrong");
             * throw new
             * BTSLBaseException("VoucherFileUploaderUtil","validateUser"
             * ,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_INVALID_USER);
             * }
             */
            // End Moldova cahnges by Ved 24/07/07

            // only super admin is allowed to perform the process
            if (!(channelUserVO.getCategoryCode()).equals(VOMSI.SUPER_ADMIN)) {
                _log.info(methodName, " Only the superadmin is allowed to run this process ........... ");
                _log.error(" validateUser ", "Only the superadmin is allowed to run this process ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileUploaderUtil[validateUser]", "", "", "", "User not authorized for processing voucher file");
                throw new BTSLBaseException("VoucherFileUploaderUtil", "validateUser", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_INVALID_USER);
            }
            return channelUserVO;
        } catch (BTSLBaseException be) {
            _log.error("VoucherFileUploaderUtil[validateUser]", "BTSLBaseException =" + be.getMessage());
            throw be;
        }// end of BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileUploaderUtil[validateUser]", "", "", "", "User not authorized for file processing");
            throw new BTSLBaseException("VoucherFileUploaderUtil", "validateUser", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        }// end of Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" validateUser ", " Exiting with channelUserVO=" + channelUserVO);
            }
        }// end of finally
    }

    /**
     * This method loads the Constants.props and ProccessLogConfig file and
     * checks whether the process is already running or not
     * 
     * @param arg1
     * @param arg2
     * @throws BTSLBaseException
     * @throws Exception
     */
    public static void loadCachesAndLogFiles(String p_arg1, String p_arg2) throws BTSLBaseException {
        final String methodName = "loadCachesAndLogFiles";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append(" Entered with p_arg1=");
        	loggerValue.append(p_arg1);
        	loggerValue.append(" p_arg2=");
        	loggerValue.append(p_arg2);
            _log.debug(" loadCachesAndLogFiles ", " Entered with p_arg1=" + p_arg1 + " p_arg2=" + p_arg2);
        }
        File logconfigFile = null;
        File constantsFile = null;
        try {
            constantsFile = new File(p_arg1);
            if (!constantsFile.exists()) {
                _log.debug(methodName, "VoucherFileUploaderUtil loadCachesAndLogFiles Constants file not found on location:: " + constantsFile.toString());
                _log.error("VoucherFileUploaderUtil[loadCachesAndLogFiles]", " Constants file not found on location:: " + constantsFile.toString());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "VoucherFileUploaderUtil[loadCachesAndLogFiles]", "", "", "", " The Constants file doesn't exists at the path specified. ");
                throw new BTSLBaseException("VoucherFileUploaderUtil ", " loadCachesAndLogFiles ", PretupsErrorCodesI.VOUCHER_MISSING_CONST_FILE);
            }

            logconfigFile = new File(p_arg2);
            if (!logconfigFile.exists()) {
                _log.debug(methodName, "VoucherFileUploaderUtil loadCachesAndLogFiles Logconfig file not found on location:: " + logconfigFile.toString());
                _log.error("VoucherFileProcessor[loadCachesAndLogFiles]", " ProcessLogConfig file not found on location:: " + logconfigFile.toString());
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileProcessor[loadCachesAndLogFiles]", "", "", "", " The ProcessLogConfig file doesn't exists  at the path specified. ");
                throw new BTSLBaseException("VoucherFileProcessor ", "loadCachesAndLogFiles ", PretupsErrorCodesI.VOUCHER_MISSING_LOG_FILE);
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        } catch (BTSLBaseException be) {
            _log.error("VoucherFileUploaderUtil[loadCachesAndLogFiles]", "BTSLBaseException =" + be.getMessage());
            throw be;
        }// end of BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            _log.error("VoucherFileUploaderUtil[loadCachesAndLogFiles]", " Exception =" + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileUploaderUtil[loadCachesAndLogFiles]", "", "", "", "Exception=" + e.getMessage());
            throw new BTSLBaseException("VoucherFileUploaderUtil ", " loadCachesAndLogFiles ", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        }// end of Exception
        finally {
            if (logconfigFile != null) {
                logconfigFile = null;
            }
            if (constantsFile != null) {
                constantsFile = null;
            }
            if (_log.isDebugEnabled()) {
                _log.debug("VoucherFileUploaderUtil[loadCachesAndLogFiles]", " Exiting..........");
            }
        }// end of finally
    }

    /**
     * This method extracts the data from the p_data string starting from the
     * pattern p_pattern
     * 
     * @param data
     * @param pattern
     * @param p_valueSeparator
     * @return String
     */
    public static String extractData(String p_data, String p_pattern, String p_valueSeparator) {
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append(" Entered with data  =  ");
        	loggerValue.append(p_data);
        	loggerValue.append(" pattern=");
        	loggerValue.append(p_pattern);
        	loggerValue.append(" p_valueSeparator=");
        	loggerValue.append(p_valueSeparator);
            _log.debug("extractData", loggerValue);
        }
        String extractedString = null;
        int index = 0;
        int index1 = 0;
        int index2 = 0;
        final String METHOD_NAME = "extractData";
        try {
            index = p_data.indexOf(p_pattern);
            index1 = p_data.indexOf(p_valueSeparator, index + p_pattern.length());
            index2 = p_data.indexOf(p_valueSeparator, index1 + 1);
            extractedString = p_data.substring(index1 + 1, index2);
        } catch (Exception e) {
            _log.error(" extractData", " Exception  = " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileUploaderUtil[extractData]", "", "", "", " Getting Exception=" + e.getMessage());
            return extractedString;
        }// end of Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" extractData", "  Exiting with extractedString = " + extractedString);
            }
        }// end of finally
        return extractedString;
    }

    /**
     * This method is used to check whether the size of p_data is equal to
     * p_length or not
     * 
     * @param p_data
     * @param p_minLength
     * @param p_maxLength
     * @return boolean
     */
    public static boolean isValidDataLength(int p_dataLength, int p_minLength, int p_maxLength) {
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append("  Entered p_dataLength = ");
        	loggerValue.append(p_dataLength);
        	loggerValue.append(" length=");
        	loggerValue.append(p_minLength);
        	loggerValue.append(" length=");
        	loggerValue.append(p_maxLength);
            _log.debug(" isValidDataLength", loggerValue);
            
        }
        boolean isValid = false;
        final String METHOD_NAME = "isValidDataLength";
        try {
            if (!(p_dataLength >= p_minLength && p_dataLength <= p_maxLength)) {
                return isValid;
            }
            isValid = true;
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            _log.error("VoucherFileUploaderUtil [isValidDataLength]", " Exception  = " + e.getMessage());
            return isValid;
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" isValidDataLength", " Exiting with isValid...=" + isValid);
            }
        }// end of finally block
        return isValid;
    }

    /**
     * This meethod is used to move the file in destination location with new
     * name after the file has been processed.
     * 
     * @param File
     *            p_inputFile - input file
     * @param String
     *            p_moveLocation - destination
     * @param String
     *            p_newFileName - new name under which to store the file
     * @throws BTSLBaseException
     */
    public void moveFileToAnotherDirectory(File p_inputFile, String p_moveLocation, String p_newFileName) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append(" Entered p_inputFile = ");
        	loggerValue.append(p_inputFile);
            _log.debug("moveFileToAnotherDirectory ", " Entered p_inputFile = " + p_inputFile);
        }
        final String METHOD_NAME = "moveFileToAnotherDirectory";
        boolean success = false;
        try {
            File destFile = new File(p_moveLocation + File.separator);

            // Move file to new directory
            if (_log.isDebugEnabled()) {
                _log.debug("moveFileToAnotherDirectory", "moving the file " + p_inputFile + " to the p_moveLocation = " + p_moveLocation);
            }
            if (destFile.exists()) {
                if (_log.isDebugEnabled()) {
                    _log.debug("moveFileToAnotherDirectory", " destFile Exist");
                }
            }
            success = p_inputFile.renameTo(new File(destFile, p_newFileName));
            if (!success) {
                _log.debug("moveFileToAnotherDirectory", "The file" + p_inputFile + " could not be moved to location " + p_moveLocation);
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileProcessor[moveFile]", "", "", "", "The voucher file could not be moved successfully to the destination folder");
                throw new BTSLBaseException(this, "moveFileToAnotherDirectory", PretupsErrorCodesI.VOUCHER_ERROR_FILE_NOT_MOVED_SUCCESSFULLY);
            }
        }// end of try-block
        catch (BTSLBaseException be) {
            _log.error("moveFileToAnotherDirectory ", " BTSLBaseException be=" + be.getMessage());
            // EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.MAJOR,"VoucherFileProcessor[moveFile]","","","","Exception:"+be.getMessage());
            throw be;
        }// end of catch-BTSLBaseException
        catch (Exception e) {
            _log.error("moveFileToAnotherDirectory ", " Exception e=" + e.getMessage());// log
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "VoucherFileProcessor[moveFile]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "moveFileToAnotherDirectory", e.getMessage());
        }// end of catch-Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug("moveFileToAnotherDirectory", " Exited with success = " + success);
            }
        }// end of finally
    }// end of moveFile

    /**
     * Method to authenticate the user for running the voucher upload process
     * 
     * @param p_con
     * @param p_loginID
     * @param p_password
     * @param p_locale
     * @param p_authType
     * @return
     * @throws BTSLBaseException
     */
    public static ChannelUserVO validateUserWithRole(Connection p_con, String p_loginID, String p_password, String p_locale, String p_authType) throws BTSLBaseException {
        final String methodName = "validateUserWithRole";
        if (_log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append(" Entered with p_loginID= ");
        	loggerValue.append(p_loginID);
        	loggerValue.append(" p_password=");
        	loggerValue.append(p_password);
        	loggerValue.append(" p_locale=");
        	loggerValue.append(p_locale);
        	loggerValue.append(" p_authType=");
        	loggerValue.append(p_authType);
            _log.debug(" validateUserWithRole ", loggerValue);
        }
        ChannelUserVO channelUserVO = null;
        try {
            Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            channelUserVO = (new LoginDAO().loadUserDetails(p_con, p_loginID, p_password, locale));
            if (channelUserVO == null) {
                _log.info(methodName, " User ID/Password is wrong (No such user exists)........... ");
                _log.error(" validateUserWithRole ", " User ID/Password is wrong ...........");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileUploaderUtil[validateUserWithRole]", "", "", "", "User Not authrorized,User ID/Password is wrong");
                throw new BTSLBaseException("VoucherFileUploaderUtil", "validateUserWithRole", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_INVALID_USER);
            }
            // Start Moldova cahnges by Ved 24/07/07
            String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            OperatorUtilI operatorUtili = null;
            try {
                operatorUtili = (OperatorUtilI) Class.forName(utilClass).newInstance();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VoucherFileUploaderUtil[validateUserWithRole]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
            }
            if (!operatorUtili.validateTransactionPassword(channelUserVO, p_password)) {
                _log.info(methodName, " User ID/Password is wrong (No such user exists)........... ");
                _log.error(" validateUserWithRole ", " User ID/Password is wrong ...........");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileUploaderUtil[validateUserWithRole]", "", "", "", "User Not authrorized, User ID/Password is wrong");
                throw new BTSLBaseException("VoucherFileUploaderUtil", "validateUserWithRole", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_INVALID_USER);
            }

            /*
             * if(!BTSLUtil.decryptText(channelUserVO.getPassword()).
             * equalsIgnoreCase(p_password))
             * {
             * System.out.println(
             * " User ID/Password is wrong (No such user exists)........... ");
             * _log.error(" validateUserWithRole ",
             * " User ID/Password is wrong ...........");
             * EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,
             * EventStatusI.RAISED,EventLevelI.INFO,
             * "VoucherFileUploaderUtil[validateUserWithRole]"
             * ,"","","","User Not authrorized, User ID/Password is wrong");
             * throw new
             * BTSLBaseException("VoucherFileUploaderUtil","validateUserWithRole"
             * ,PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_INVALID_USER);
             * }
             */
            // End Moldova cahnges by Ved 24/07/07

            // only super admin is allowed to perform the process
            if (AUTH_TYPE_CAT.equalsIgnoreCase(p_authType) && !(channelUserVO.getCategoryCode()).equals(VOMSI.SUPER_ADMIN)) {
                _log.info(methodName, " Only the superadmin is allowed to run this process ........... ");
                _log.error(" validateUserWithRole ", "Only the superadmin is allowed to run this process ");
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileUploaderUtil[validateUserWithRole]", "", "", "", "User not authorized for processing voucher file");
                throw new BTSLBaseException("VoucherFileUploaderUtil", "validateUserWithRole", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_INVALID_USER);
            } else if (AUTH_TYPE_ROLE.equalsIgnoreCase(p_authType)) {
                UserDAO userDAO = new UserDAO();
                boolean roleAvailable = false;
                if ("Y".equalsIgnoreCase(channelUserVO.getCategoryVO().getFixedRoles())) {
                    roleAvailable = userDAO.isFixedRoleAndExist(p_con, channelUserVO.getCategoryCode(), UPLOAD_ROLE_CODE, TypesI.OPERATOR_USER_TYPE);
                } else {
                    roleAvailable = userDAO.isAssignedRoleAndExist(p_con, channelUserVO.getUserID(), UPLOAD_ROLE_CODE, TypesI.OPERATOR_USER_TYPE);
                }

                if (!roleAvailable) {
                    _log.info(methodName, " User not authorized to run the process , Insufficient Priviliges........... ");
                    _log.error(" validateUserWithRole ", "User not authorized to run the process , Insufficient Priviliges ");
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileUploaderUtil[validateUserWithRole]", "", "", "", "User not authorized to run the process , Insufficient Priviliges");
                    throw new BTSLBaseException("VoucherFileUploaderUtil", "validateUserWithRole", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_INVALID_USER);
                }
            }
            return channelUserVO;
        } catch (BTSLBaseException be) {
            _log.error("VoucherFileUploaderUtil[validateUserWithRole]", "BTSLBaseException =" + be.getMessage());
            throw be;
        }// end of BTSLBaseException
        catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO, "VoucherFileUploaderUtil[validateUserWithRole]", "", "", "", "User not authorized for file processing");
            throw new BTSLBaseException("VoucherFileUploaderUtil", "validateUserWithRole", PretupsErrorCodesI.VOUCHER_UPLOAD_PROCESS_GENERAL_ERROR);
        }// end of Exception
        finally {
            if (_log.isDebugEnabled()) {
                _log.debug(" validateUserWithRole ", " Exiting with channelUserVO=" + channelUserVO);
            }
        }// end of finally
    }
    /*	*//**
     * This method is used to check whether the length of PIN is in
     * specified range or not.
     * 
     * @param p_data
     * @return boolean
     */
    /*
     * public static boolean isValidPINLength(String p_data)
     * {
     * if(_log.isDebugEnabled())_log.debug(" isValidPINLength","  Entered p_data = "
     * +p_data);
     * boolean isValid = false;
     * try
     * {
     * if (p_data.length()>=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MIN_LENGTH))).intValue() &&
     * p_data.length()<=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MAX_LENGTH))).intValue())
     * isValid=true;
     * Long.parseLong(p_data);
     * isValid = true;
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * _log.error("VoucherFileUploaderUtil [isValidPINLength]"," Exception  = "+e
     * .getMessage());
     * return isValid;
     * }//end of catch-Exception
     * finally
     * {
     * if(_log.isDebugEnabled())_log.debug(" isValidPINLength",
     * " Exiting with isValid...="+isValid);
     * }//end of finally block
     * return isValid;
     * }
     *//**
     * This method is used to check whether the length of Serial no is in
     * specified range or not.
     * 
     * @param p_data
     * @return boolean
     */
    /*
     * public static boolean isValidSerialNoLength(String p_data)
     * {
     * if(_log.isDebugEnabled())_log.debug(" isValidSerialNoLength",
     * "  Entered p_data = "+p_data);
     * boolean isValid = false;
     * try
     * {
     * if (p_data.length()>=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MIN_LENGTH))).intValue() &&
     * p_data.length()<=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MAX_LENGTH))).intValue())
     * isValid=true;
     * Long.parseLong(p_data);
     * isValid = true;
     * }
     * catch(Exception e)
     * {
     * e.printStackTrace();
     * _log.error("VoucherFileUploaderUtil [isValidSerialNoLength]"," Exception  = "
     * +e.getMessage());
     * return isValid;
     * }//end of catch-Exception
     * finally
     * {
     * if(_log.isDebugEnabled())_log.debug(" isValidSerialNoLength",
     * " Exiting with isValid...="+isValid);
     * }//end of finally block
     * return isValid;
     * }
     */
}

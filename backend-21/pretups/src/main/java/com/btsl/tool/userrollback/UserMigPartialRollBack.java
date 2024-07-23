package com.btsl.tool.userrollback;

/**
 * @(#)UserMigPartialRollBack.java
 *                                 Copyright(c) 2010, Comviva Technologies Ltd.
 *                                 All Rights Reserved
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Author Date History
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Ashish Kumar Todia June 02,2010 Initial
 *                                 Creation
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 ------
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;

public class UserMigPartialRollBack {

    private static Log _log = LogFactory.getLog(UserMigPartialRollBack.class.getName());

    private String fileextension = ".csv"; // EXTESION OF THE INPUT,OUTPUT,FAIL_RECORDS
                                   // FILE..
    private int totalRecodsSupported = 10000; // MAXIMUM NUMBER OF RECORDS THAT IS TO BE
                                      // THERE IN THE INPUT FILE..
    private File _fileObjectSucess = null;
    private File _fileObjectFail = null;
    private FileWriter fwriterSucess = null;
    private FileWriter fwriterFail = null;
    private String delimeter = ",";
    private String newLine = "\n";
    private PreparedStatement psmt = null;
    private PreparedStatement psmtSelectQuery = null; // psmt
    private PreparedStatement psmtUpdateQuery = null; // psmt1
    private PreparedStatement psmtSelect = null;
    private PreparedStatement psmtUpdateStatus = null;
    private PreparedStatement psmtUpdateLogin = null;
    private PreparedStatement psmtLUD = null;
    private PreparedStatement psmtUpdateOldUsrBalance = null;
    private PreparedStatement psmtUpdateNewUsrBal = null;
    private PreparedStatement psmtGetFromUserBalance = null;
    private PreparedStatement psmtDelUsr = null;

    /**
     * This method validates the input/output file extension
     * 
     * @param p_inputFile
     * @param p_outputFile
     * @return boolean
     */
    public boolean validateFileExtesion(String p_inputFile, String p_outputFile) {
        UserMigrRollbackLog.log("validateFileExtesion", "Entered p_inputFile: " + p_inputFile + "p_outputFile: " + p_outputFile);
        boolean validationStatus = false;
        int i = p_inputFile.indexOf(fileextension);
        int j = p_outputFile.indexOf(fileextension);
        if (((p_inputFile.length() - i) == (fileextension.length())) && ((p_outputFile.length() - j) == (fileextension.length())))
            validationStatus = true;
        else
            validationStatus = false;
        UserMigrRollbackLog.log("validateFileExtesion", "Exited: validationStatus= " + validationStatus);
        return validationStatus;
    }

    /**
     * This method validates the msisdn is numerical and the total number of the
     * records .
     * 
     * @param p_inputFile
     * @return boolean
     */
    public boolean ValidateInputFileData(String p_inputFile) {
        boolean isFileValid = true;
        int rowcount = 0;
        final String methodName = "ValidateInputFileData";
        UserMigrRollbackLog.log(methodName, "Entered p_inputFile: " + p_inputFile);
        File fileObject = new File(p_inputFile);
        String thisLine = null;
        BufferedReader input = null;
        FileReader fileReader = null;
        String str[] = null;
        try {
            fileReader = new FileReader(fileObject);
            input = new BufferedReader(fileReader);
            while (!(thisLine = ((input.readLine()).toUpperCase())).startsWith("EOF")) {
                str = thisLine.split(",");
                String retMSISDN = BTSLUtil.NullToString(str[0].trim());
                if (BTSLUtil.isNullString(retMSISDN)) {
                    UserMigrRollbackLog.log("UserMigPartialRollBack", "ValidateInputFileData MSISDN NULL " + retMSISDN + "line: " + thisLine);
                    throw new BTSLBaseException("UserMigPartialRollBack ", methodName, "MSISDN NULL " + retMSISDN + "line: " + thisLine);
                } else {
                    rowcount++;
                    if (!BTSLUtil.isNumeric(retMSISDN)) {
                        isFileValid = false;
                        UserMigrRollbackLog.log("UserMigPartialRollBack", "ValidateInputFileData MSISDN NULL " + retMSISDN + "is NON NUMERIC");
                        throw new BTSLBaseException("UserMigPartialRollBack ", methodName, "MSISDN NULL " + retMSISDN + "is NON NUMERIC");
                    }
                    if (rowcount > totalRecodsSupported)
                        isFileValid = false;
                }
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    _log.errorTrace(methodName, e);
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    _log.errorTrace(methodName, e);
                }
            }
            UserMigrRollbackLog.log(methodName, "Exiting : isFileValid" + isFileValid);
        }
        return isFileValid;
    }

    /**
     * This method will load the Constant.props and the LogConfig.props file.
     * 
     * @param p_arg1
     * @param p_arg2
     */
    public static void loadCachesAndLogFiles(String p_arg1, String p_arg2) throws BTSLBaseException {
        final String methodName = "loadCachesAndLogFiles";
        UserMigrRollbackLog.log(methodName, "Entered p_arg1: " + p_arg1 + " p_arg2: " + p_arg2);
        File logconfigFile = null;
        File constantsFile = null;
        try {
            constantsFile = new File(p_arg1);
            if (!constantsFile.exists()) {
                UserMigrRollbackLog.log(methodName, " Constant.props does not exist");
                throw new BTSLBaseException("UserMigPartialRollBack ", methodName, "Constant.props does not exist");
            }
            logconfigFile = new File(p_arg2);
            if (!logconfigFile.exists()) {
                UserMigrRollbackLog.log(methodName, " LogConfig.props does not exist");
                throw new BTSLBaseException("UserMigPartialRollBack ", methodName, "LogConfig.props does not exist");
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
            UserMigrRollbackLog.log(methodName, "Exiting :");
            if (logconfigFile != null)
                logconfigFile = null;
            if (constantsFile != null)
                constantsFile = null;
        }
    }

    /**
     * This method to avoid the issue of OPEN_CURSORS.
     * 
     * @param con
     */
    public void createPreparedStatements(Connection con) {
        final String methodName = "createPreparedStatements";
        UserMigrRollbackLog.log(methodName, "Entered :");
        // ismigrated.
        String query = "select REFERENCE_ID from users where MSISDN=? AND STATUS NOT IN ('C','N')";
        // getUserIDFromMsisdn.
        String selectQuery = "SELECT USER_ID FROM USERS WHERE MSISDN=? AND STATUS NOT IN ('C','N')"; // get
                                                                                                     // userid
                                                                                                     // for
                                                                                                     // the
                                                                                                     // msisdn.
        String updateQuery = "UPDATE USERS SET LOGIN_ID=? WHERE USER_ID=?"; // set
                                                                            // loginId=UserId
                                                                            // for
                                                                            // the
                                                                            // new
                                                                            // migrated
                                                                            // user.

        // activateOldUser.
        String selectQueryAOU = "SELECT USER_ID,OLD_LOGIN_ID FROM USERS WHERE REFERENCE_ID=? AND STATUS ='N'"; // fetches
                                                                                                               // the
                                                                                                               // old
                                                                                                               // UserId.
        String updateQueryAOU = "UPDATE USERS SET STATUS=? WHERE USER_ID=?"; // updates
                                                                             // the
                                                                             // status
                                                                             // ='Y'
                                                                             // for
                                                                             // old
                                                                             // userID
        String updateLoginIDQueryAOU = "UPDATE USERS SET LOGIN_ID=? WHERE USER_ID=?"; // updates
                                                                                      // the
                                                                                      // old
                                                                                      // loginId
                                                                                      // for
                                                                                      // the
                                                                                      // old
                                                                                      // userId.

        // loadUserDetailsForTransfer..
        String selectQueryLUD = "select u.user_name,ug.GRPH_DOMAIN_CODE,u.NETWORK_CODE,up.PHONE_LANGUAGE,up.COUNTRY from users u,USER_PHONES up,USER_GEOGRAPHIES ug where u.user_id=up.user_id and u.user_id=ug.user_id and u.user_id=?";

        // updateBalanceFromNewToOld.
        String getFromUserBalance = "select BALANCE from user_balances where user_id =?"; // fromuserbalcnce
        String updateToUser = "update user_balances set BALANCE=? where user_id =?"; // update
                                                                                     // touserbalance.
        String updateNewUsrPrvBal = "UPDATE USER_BALANCES SET BALANCE=? WHERE USER_ID=?"; // touserid.

        // deleteUserByUserID
        String queryDeletedUserID = "UPDATE USERS SET STATUS=? WHERE USER_ID=?";

        // setting the prepared statments for removing OPEN_CURSOR ISSUE.
        try {
            // ismigrated.
            if (con != null) {
                psmt = con.prepareStatement(query);

                // getUserIDFromMsisdn.
                psmtSelectQuery = con.prepareStatement(selectQuery);
                psmtUpdateQuery = con.prepareStatement(updateQuery);

                // activateOldUser.
                psmtSelect = con.prepareStatement(selectQueryAOU);
                psmtUpdateStatus = con.prepareStatement(updateQueryAOU);
                psmtUpdateLogin = con.prepareStatement(updateLoginIDQueryAOU);

                // loadUserDetailsForTransfer
                psmtLUD = con.prepareStatement(selectQueryLUD);

                // updateBalanceFromNewToOld
                psmtGetFromUserBalance = con.prepareStatement(getFromUserBalance);
                psmtUpdateOldUsrBalance = con.prepareStatement(updateToUser);
                psmtUpdateNewUsrBal = con.prepareStatement(updateNewUsrPrvBal);

                // deleteUserByUserID
                psmtDelUsr = con.prepareStatement(queryDeletedUserID);
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
            UserMigrRollbackLog.log(methodName, "Exited :");
        }
    }

    /**
     * This method will check whether the msisdn is migrated Previously.
     * 
     * @param p_con
     * @param p_msisdn
     * @return boolean
     */
    public boolean isMsisdnMigrated(Connection p_con, String p_msisdn, PreparedStatement psmt) {
        final String methodName = "isMsisdnMigrated";
        UserMigrRollbackLog.log(methodName, "Entered p_msisdn: " + p_msisdn);
        boolean isMigrated = false;
        // String
        // query="select REFERENCE_ID from users where MSISDN=? AND STATUS NOT IN ('C','N')";
        // PreparedStatement psmt =null;
        ResultSet rs = null;
        String refId = null;
        try {
            psmt.clearParameters();
            if (psmt != null) {
                psmt.setString(1, p_msisdn);
                rs = psmt.executeQuery();
                while (rs.next()) {
                    refId = rs.getString("REFERENCE_ID");
                }
                if (!BTSLUtil.isNullString(refId))
                    isMigrated = true;
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
            try {
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            UserMigrRollbackLog.log(methodName, "Exiting : isMigrated" + isMigrated);
        }
        return isMigrated;
    }

    /**
     * This is the main rollback Controlling method.
     * 
     * 1)Loads the Cache and Log files.
     * 2)Load the msisdn records from the input .csv file.
     * 3)Checks the msisdn is migrated or not.
     * 4)Gets the current userId(newUserId or migrated userid) for the msisdn.
     * 5)Gets the old userId or (previous UserId) from newUserId,based on
     * refernceId.
     * 6)Loads the toUserVo (oldUserId) and fromUserVo (newUserID).
     * 7)Update the balance and previous balance of toUserVo equal to the
     * balance of fromUserVo
     * 8)Update the balance of the fromUserVo equal to ZERO balance.
     * 9)Update the status and loginId equal to userId for the newUserVo.
     * 10)Finaly ,if all goes well,COMMIT otherwise ROLLBACK.
     * 
     * @param args
     *            []
     */
    public void process(String args[]) {
        final String methodName = "process";
        UserMigrRollbackLog.log(methodName, "Entered args[]: " + args);
        Connection con = null;
        MComConnectionI mcomCon = null;
        String inputfilepath = args[2];
        String rollbackupOutPutFile = null;
        ArrayList<UserRollBackVo> msisdnlist = null;
        UserRollBackVo userRollBack = null;
        int count = 0;
        boolean success = false;
        try {
            loadCachesAndLogFiles(args[0], args[1]);
            String dir = Constants.getProperty("DIR_PATH");
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            if (BTSLUtil.isNullString(dir)) {
                UserMigrRollbackLog.log(methodName, "DIR_PATH : " + dir + " is not defined in the property file.");
                throw new BTSLBaseException(methodName, "DIR_PATH is not defined in the property file.");
            }
            File newDir = new File(dir);
            if (!newDir.exists())
                success = newDir.mkdirs();
            else
                success = true;
            if (!success)
                throw new BTSLBaseException(methodName, "Not Able To Create the" + dir + " Direcoty.");
            rollbackupOutPutFile = dir + args[3];
            createOutPutFile(rollbackupOutPutFile);
            if (con == null)
                throw new BTSLBaseException("UserMigPartialRollBack ", methodName, "Error in Creating Connection.");
            if (inputfilepath == null)
                throw new BTSLBaseException("UserMigPartialRollBack ", methodName, "Error in loading FILE PATH.");
            msisdnlist = processRecoreds(inputfilepath);
            // code commented for sonar Dodgy - Redundant nullcheck of value
               // known to be non-null
            Iterator it = msisdnlist.iterator();
            new UserMigPartialRollBack().createPreparedStatements(con);
            while (it.hasNext()) {
                userRollBack = (UserRollBackVo) it.next();
                UserMigrRollbackLog.log(userRollBack.getLineNumber(), userRollBack.getMsisdn(), "", "", "", "", "MIGRATION STARTS");
                boolean isMigratedMsisdn = isMsisdnMigrated(con, userRollBack.getMsisdn(), psmt);
                if (!isMigratedMsisdn) {
                    UserMigrRollbackLog.log(methodName, "ERROR GIVEN MSISDN IS NOT A MIGRATED MSISDN: migrated status =" + isMigratedMsisdn);
                    UserMigrRollbackLog.log(userRollBack.getLineNumber(), userRollBack.getMsisdn(), "", "", "", "", "ERROR GIVEN MSISDN IS NOT A MIGRATED MSISDN");
                    writeFailToOutPutFile(fwriterFail, userRollBack.getMsisdn(), "ERROR GIVEN MSISDN IS NOT A MIGRATED MSISDN ", userRollBack.getLineNumber());
                    continue;
                }
                // deactivating the new user and getting the userid of new user.

                userRollBack.setNewUserId(getUserIDFromMsisdn(con, userRollBack.getMsisdn(), psmtSelectQuery, psmtUpdateQuery));
                // get olduser id and activate the old user.
                if (BTSLUtil.isNullString(userRollBack.getNewUserId())) {
                    UserMigrRollbackLog.log(methodName, "ERROR IN LOADING MIGRATED USER USERID : newuserid =" + userRollBack.getNewUserId());
                    UserMigrRollbackLog.log(userRollBack.getLineNumber(), userRollBack.getMsisdn(), "", "", "", "", "ERROR IN LOADING MIGRATED NEW USER USERID");
                    writeFailToOutPutFile(fwriterFail, userRollBack.getMsisdn(), "ERROR IN LOADING MIGRATED USER USERID", userRollBack.getLineNumber());
                    continue;
                }

                userRollBack.setOldUserID(activateOldUser(con, userRollBack.getNewUserId(), psmtSelect, psmtUpdateStatus, psmtUpdateLogin));
                if (BTSLUtil.isNullString(userRollBack.getOldUserID())) {
                    UserMigrRollbackLog.log(methodName, "ERROR IN LOADING OLD USER USERID");
                    UserMigrRollbackLog.log(userRollBack.getLineNumber(), userRollBack.getMsisdn(), "", "", "", "", "ERROR IN LOADING OLD USER USERID");
                    writeFailToOutPutFile(fwriterFail, userRollBack.getMsisdn(), "ERROR IN LOADING OLD USER USERID", userRollBack.getLineNumber());
                    continue;
                }

                this.loadUserDetailsForTransfer(userRollBack, psmtLUD);

                count = updateBalanceFromNewToOld(con, userRollBack, userRollBack.getLineNumber(), psmtGetFromUserBalance, psmtUpdateOldUsrBalance, psmtUpdateNewUsrBal);

                deleteUserByUserID(con, userRollBack.getNewUserId(), psmtDelUsr);
                if (count > 0) {
                	mcomCon.finalCommit();
                    UserMigrRollbackLog.log(userRollBack.getLineNumber(), userRollBack.getMsisdn(), userRollBack.getOldUserID(), userRollBack.getNewUserId(), "", "", "USER ROLLBACK IS SUCCESSFULL.");

                } else {
                	mcomCon.finalRollback();
                    UserMigrRollbackLog.log(userRollBack.getLineNumber(), userRollBack.getMsisdn(), userRollBack.getOldUserID(), userRollBack.getNewUserId(), "", "", "USER ROLLBACKED FAILED.");
                }
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
            UserMigrRollbackLog.log(methodName, "Exiting :");
            try {
                // clossing FileWriter stream after writing the all details.
                fwriterSucess.close();
                fwriterFail.close();
				if (mcomCon != null) {
					mcomCon.close("UserMigPartialRollBack#process");
					mcomCon = null;
				}
                renameFile(inputfilepath); // renaming the inputfile after the
                                           // execution.
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
        }
    }

    /**
     * This method loads the users details.
     * 
     * @param p_con
     * @param p_userRollbackVo
     */
    private void loadUserDetailsForTransfer(UserRollBackVo p_userRollbackVo, PreparedStatement psmt) {
        final String methodName = "loadUserDetailsForTransfer";
        UserMigrRollbackLog.log(methodName, "userRollbackVo OldUserId :" + p_userRollbackVo.getOldUserID() + " NewUserID : " + p_userRollbackVo.getNewUserId());
        // String

        ResultSet rs = null;
        try {
            if (psmt != null) {
 	
                psmt.setString(1, p_userRollbackVo.getOldUserID());
                try{
                rs = psmt.executeQuery();
                while (rs.next()) {
                    p_userRollbackVo.setOldUserName(rs.getString("USER_NAME"));
                    p_userRollbackVo.setOldUserGeog(rs.getString("GRPH_DOMAIN_CODE"));
                    p_userRollbackVo.setOldUserNetworkCode(rs.getString("NETWORK_CODE"));
                    p_userRollbackVo.setOldUserPhoneLang(rs.getString("PHONE_LANGUAGE"));
                    p_userRollbackVo.setOldUserCountry(rs.getString("COUNTRY"));
                }
                psmt.clearParameters();
                }
                finally{
                	if(rs!=null)
                		rs.close();
                }
                rs = null;
                psmt.setString(1, p_userRollbackVo.getNewUserId());
                rs = psmt.executeQuery();
                while (rs.next()) {
                    p_userRollbackVo.setNewUserName(rs.getString("USER_NAME"));
                    p_userRollbackVo.setNewUserGeog(rs.getString("GRPH_DOMAIN_CODE"));
                    p_userRollbackVo.setNewUserNetworkCode(rs.getString("NETWORK_CODE"));
                    p_userRollbackVo.setNewUserPhoneLang(rs.getString("PHONE_LANGUAGE"));
                    p_userRollbackVo.setNewUserCountry(rs.getString("COUNTRY"));
                }
            }
        } catch (SQLException sqlexp) {
            _log.errorTrace(methodName, sqlexp);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
            try {
                rs.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            UserMigrRollbackLog.log(methodName, "Exiting : ");
        }
    }

    /**
     * This method update the balance for oldUserId from new UserId and set the
     * new UserId's balance ZERO.
     * 
     * @param p_con
     * @param p_fromChannelUserVo
     * @param p_toChannelUserVO
     * @param p_rollbackupOutPutFile
     * @param p_lineno
     * @return
     */
    private int updateBalanceFromNewToOld(Connection p_con, UserRollBackVo p_userrollbackVo, int p_lineno, PreparedStatement psmtGetFromUserBalance, PreparedStatement psmtUpdateOldUsrBalance, PreparedStatement psmtUpdateNewUsrBal) {
        final String methodName = "updateBalanceFromNewToOld";
        UserMigrRollbackLog.log(methodName, "Entered p_fromChannelUserId: " + p_userrollbackVo.getNewUserId() + "p_toChannelUserId: " + p_userrollbackVo.getOldUserID());
        UserMigrRollbackLog.log(p_lineno, p_userrollbackVo.getMsisdn(), p_userrollbackVo.getNewUserId(), p_userrollbackVo.getOldUserID(), p_userrollbackVo.getOldUserGeog(), p_userrollbackVo.getNewUserGeog(), "Entered : updateBalanceFromNewToOld ");
        int count = 0;

        ResultSet rsGetUserBal = null;
        String zeroBalance = "0";
        String transferedValue = null;

        try {
            if ((psmtUpdateOldUsrBalance != null) && (psmtUpdateNewUsrBal != null) && (psmtGetFromUserBalance != null)) {

                psmtGetFromUserBalance.setString(1, p_userrollbackVo.getNewUserId());
                rsGetUserBal = psmtGetFromUserBalance.executeQuery();
                while (rsGetUserBal.next()) {
                    transferedValue = rsGetUserBal.getString("BALANCE");
                    psmtUpdateOldUsrBalance.setString(1, transferedValue);
                    psmtUpdateOldUsrBalance.setString(2, p_userrollbackVo.getOldUserID());
                    count = psmtUpdateOldUsrBalance.executeUpdate();
                    if (count > 0) {
                        psmtUpdateNewUsrBal.setString(1, zeroBalance);
                        psmtUpdateNewUsrBal.setString(2, p_userrollbackVo.getNewUserId());
                        count = psmtUpdateNewUsrBal.executeUpdate();
                    } else {
                        count = 0;
                    }
                    psmtGetFromUserBalance.clearParameters();
                    psmtUpdateOldUsrBalance.clearParameters();
                    psmtUpdateNewUsrBal.clearParameters();
                }
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
        	try{
    	    	if (rsGetUserBal != null)
    	    		rsGetUserBal.close();
    	    	} 
        	catch (Exception e){
    	    	_log.errorTrace(methodName, e);
    	    }
            UserMigrRollbackLog.log(methodName, "Exiting: count" + count);
            UserMigrRollbackLog.log(p_lineno, p_userrollbackVo.getMsisdn(), p_userrollbackVo.getNewUserId(), p_userrollbackVo.getOldUserID(), p_userrollbackVo.getOldUserGeog(), p_userrollbackVo.getNewUserGeog(), "Exiting : updateBalanceFromNewToOld ");
        }
        if (count > 0) {
            writeSucessFullToOutPutFile(fwriterSucess, p_userrollbackVo, transferedValue, p_lineno);
            if (PretupsI.YES.equalsIgnoreCase(Constants.getProperty("ALLOW_SMS_ON_ROLLBACK")))
                sendMessageToMigratedUsers(p_con, p_userrollbackVo);
        } else {
            writeFailToOutPutFile(fwriterFail, p_userrollbackVo.getMsisdn(), "ERROR WHILE ADJUSTING THE BALANCE OF USERS", p_lineno);
        }
        return count;
    }

    /**
     * This method creates the output files and put header data in them.
     * 
     * @param p_rollbackupOutPutFile
     */
    private void createOutPutFile(String p_rollbackupOutPutFile) {
        final String methodName = "createOutPutFile";
        UserMigrRollbackLog.log(methodName, "Exiting: p_rollbackupOutPutFile" + p_rollbackupOutPutFile);
        _fileObjectSucess = new File(p_rollbackupOutPutFile);
        String failfile[] = p_rollbackupOutPutFile.split(fileextension);
        _fileObjectFail = new File(failfile[0] + "_Fail" + fileextension);
        String thisLineSucess = null;
        String thisLineFail = null;
        try {
            fwriterSucess = new FileWriter(_fileObjectSucess);
            fwriterFail = new FileWriter(_fileObjectFail);
            // code commented for sonar Dodgy - Redundant nullcheck of value
               // known to be non-null
            Date currendate = new Date();
            thisLineSucess = "ROLLBACK TAKEN ON :" + currendate.toString() + "\n" + "\n";
            fwriterSucess.append(thisLineSucess);
            thisLineFail = "ROLLBACK TAKEN ON :" + currendate.toString() + "\n" + "\n";
            fwriterFail.append(thisLineFail);
            thisLineSucess = "RECORD NUMBER" + delimeter + "USER MSISDN" + delimeter + "FROM USERID" + delimeter + "TO USERID" + delimeter + "TRANSFERED VALUE" + "\n";
            thisLineFail = "RECORD NUMBER" + delimeter + "USER MSISDN" + delimeter + " NOT MIGRATED DUE TO " + delimeter + " ERROR MESSAGE" + "\n";
            fwriterSucess.append(thisLineSucess);
            fwriterFail.append(thisLineFail);
            fwriterSucess.flush();
            fwriterFail.flush();
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
            UserMigrRollbackLog.log(methodName, "Exited :");
        }
    }

    /**
     * This method writes the failed rollback records to the output_fail.csv
     * file
     * 
     * @param p_fwriterFail
     * @param p_msisdn
     * @param message
     * @param p_lineno
     */
    private void writeFailToOutPutFile(FileWriter p_fwriterFail, String p_msisdn, String message, int p_lineno) {
        final String methodName = "writeFailToOutPutFile";
        UserMigrRollbackLog.log(methodName, "Entered: p_fwriterFail" + p_fwriterFail + "p_msisdn: " + p_msisdn + "message: " + message + "p_lineno: " + p_lineno);
        String thisLine = null;
        try {
            if (p_fwriterFail == null) {
                UserMigrRollbackLog.log(methodName, "Error in RollBack OUT put file ");
                throw new BTSLBaseException("UserMigPartialRollBack ", methodName, "Error in RollBack OUT put file..");
            }
            thisLine = p_lineno + delimeter + p_msisdn + delimeter + "NOT MIGRATED DUE TO" + delimeter + message + newLine;
            UserMigrRollbackLog.log(methodName, "Line Printed to File :" + thisLine);
            p_fwriterFail.append(thisLine);
            p_fwriterFail.flush();
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
            UserMigrRollbackLog.log(methodName, "Exited :");
        }
    }

    /**
     * This method writes the succesfull rollbacked records to the output file.
     * 
     * @param p_fwriterSucess
     * @param p_fromChannelUserVo
     * @param p_toChannelUserVO
     * @param p_transferedValue
     * @param p_lineno
     */
    private void writeSucessFullToOutPutFile(FileWriter p_fwriterSucess, UserRollBackVo p_userrollbackVo, String p_transferedValue, int p_lineno) {
        final String methodName = "writeSucessFullToOutPutFile";
        UserMigrRollbackLog.log(methodName, "Entered  p_fromChannelUserId: " + p_userrollbackVo.getNewUserId() + "p_toChannelUserID: " + p_userrollbackVo.getOldUserID() + "p_transferedValue: " + p_transferedValue);
        UserMigrRollbackLog.log(p_lineno, p_userrollbackVo.getMsisdn(), p_userrollbackVo.getNewUserId(), p_userrollbackVo.getOldUserID(), p_userrollbackVo.getOldUserID(), p_userrollbackVo.getNewUserId(), "Entered : writeSucessFullToOutPutFile");
        String thisLine = null;
        try {
            if (p_fwriterSucess == null)
                throw new BTSLBaseException("UserMigPartialRollBack ", methodName, "Error in RollBack OUT put file..");
            thisLine = p_lineno + delimeter + p_userrollbackVo.getMsisdn() + delimeter + p_userrollbackVo.getNewUserId() + delimeter + p_userrollbackVo.getNewUserId() + delimeter + p_transferedValue + newLine;
            UserMigrRollbackLog.log(p_lineno, p_userrollbackVo.getMsisdn(), p_userrollbackVo.getNewUserId(), p_userrollbackVo.getNewUserId(), p_userrollbackVo.getOldUserGeog(), p_userrollbackVo.getNewUserGeog(), thisLine);
            p_fwriterSucess.append(thisLine);
            p_fwriterSucess.flush();
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
            UserMigrRollbackLog.log(methodName, "Exiting :");
            UserMigrRollbackLog.log(p_lineno, p_userrollbackVo.getMsisdn(), p_userrollbackVo.getNewUserId(), p_userrollbackVo.getOldUserID(), p_userrollbackVo.getOldUserGeog(), p_userrollbackVo.getNewUserGeog(), "Exited : writeSucessFullToOutPutFile");
        }
    }

    /**
     * This file load the records from input file into an ArrayList.
     * 
     * @param p_filename
     * @return
     */
    public ArrayList processRecoreds(String p_filename) throws IOException, Exception {
        final String methodName = "processRecoreds";
        UserMigrRollbackLog.log(methodName, "Entered p_filename: " + p_filename);
        File _fileObject = new File(p_filename);
        ArrayList<UserRollBackVo> msisdnlist = new ArrayList<UserRollBackVo>();
        String thisLine = null;
        BufferedReader input = null;
        FileReader fileReader = null;
        UserRollBackVo userRollBack = null;
        int lineNumber = 0;
        String str[] = null;
        try {
            fileReader = new FileReader(_fileObject);
            input = new BufferedReader(fileReader);
            while (!(thisLine = ((input.readLine()).toUpperCase())).startsWith("EOF")) {
                lineNumber++;
                str = thisLine.split(",");

                String msisdn = BTSLUtil.NullToString(str[0].trim());
                if (BTSLUtil.isNullString(msisdn)) {
                    UserMigrRollbackLog.log(methodName, "MSISDN NULL " + msisdn + "line: " + thisLine);
                    continue;
                } else {
                    if (!BTSLUtil.isNumeric(msisdn)) {
                        UserMigrRollbackLog.log(methodName, "MSISDN NULL " + msisdn + "is NON NUMERIC");
                        continue;
                    } else {
                        userRollBack = new UserRollBackVo();
                        userRollBack.setLineNumber(lineNumber);
                        userRollBack.setMsisdn(msisdn);
                        msisdnlist.add(userRollBack);
                    }
                }
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    _log.errorTrace(methodName, e);
                }
            }
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    _log.errorTrace(methodName, e);
                }
            }
            UserMigrRollbackLog.log(methodName, "Exiting : msisdnlist.size()=" + msisdnlist.size());
        }
        return msisdnlist;
    }

    /**
     * this method updated the status of the new User (migrated user) to
     * DELETED.
     * 
     * @param p_con
     * @param p_userId
     */
    public void deleteUserByUserID(Connection p_con, String p_userId, PreparedStatement psmt) {
        final String methodName = "deleteUserByUserID";
        UserMigrRollbackLog.log(methodName, "Entered p_userId: " + p_userId);

        int count = 0;

        try {
            if (psmt != null) {

                psmt.setString(1, PretupsI.STATUS_DELETE);
                psmt.setString(2, p_userId);
                count = psmt.executeUpdate();
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
            UserMigrRollbackLog.log(methodName, "Exiting count: " + count);
        }
    }

    /**
     * This method fetches the UserID for the msisdn.
     * 
     * @param con
     * @param p_msisdn
     * @return
     */
    public String getUserIDFromMsisdn(Connection con, String p_msisdn, PreparedStatement psmt, PreparedStatement psmt1) {
        final String methodName = "getUserIDFromMsisdn";
        UserMigrRollbackLog.log(methodName, "Entered p_msisdn: " + p_msisdn);

        ResultSet rs = null;
        String userid = null;

        try {

            if ((psmt != null) && (psmt1 != null)) {
                psmt.setString(1, p_msisdn);
                rs = psmt.executeQuery();
                while (rs.next()) {
                    userid = rs.getString("USER_ID");
                    psmt1.setString(1, userid);
                    psmt1.setString(2, userid);
                    psmt1.executeUpdate();
                }
            }
        } catch (Exception e) {
        	
            _log.errorTrace(methodName, e);
        } finally {
        	try{
    	    	if (rs != null)
    	    		rs.close();
    	    	} 
        	catch (Exception e){
    	    	_log.errorTrace(methodName, e);
    	    }
            UserMigrRollbackLog.log(methodName, "Exiting userid: " + userid);
        }
        return userid;
    }

    /**
     * This method activates the old user and deletes the new User (Migrated
     * one).
     * 
     * @param p_con
     * @param p_userid
     * @return
     */
    public String activateOldUser(Connection p_con, String p_newuserid, PreparedStatement psmtSelect, PreparedStatement psmtUpdateStatus, PreparedStatement psmtUpdateLogin) {
        final String methodName = "activateOldUser";
        UserMigrRollbackLog.log(methodName, "Entered p_userid: " + p_newuserid);

        ResultSet rs = null;
        String oldUserid = null;
        String oldLoginId = null;

        int count = 0;
        try {
            if ((psmtSelect != null) && (psmtUpdateStatus != null) && (psmtUpdateLogin != null)) {

                psmtSelect.setString(1, p_newuserid);
                rs = psmtSelect.executeQuery();
                while (rs.next()) {
                    oldUserid = rs.getString("USER_ID");
                    oldLoginId = rs.getString("OLD_LOGIN_ID");

                    psmtUpdateStatus.setString(1, PretupsI.STATUS_ACTIVE);
                    psmtUpdateStatus.setString(2, oldUserid);
                    psmtUpdateStatus.executeUpdate();

                    psmtUpdateLogin.setString(1, oldLoginId);
                    psmtUpdateLogin.setString(2, oldUserid);
                    count = psmtUpdateLogin.executeUpdate();
                }
            }
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
        	try
    	    {
    	    	if (rs != null)
    	    		rs.close();
    	    	} catch (Exception e)
    	    {
    	    	_log.errorTrace(methodName, e);
    	    }
            UserMigrRollbackLog.log("activeOldUser", "Exiting oldUserid: " + oldUserid + " Count :" + count);
        }
        return oldUserid;
    }

    /**
     * Main method for the UserMigPartialRollBack Process.
     * args[0]=Constant.props
     * args[1]=LogConfig.props
     * args[2]=inputFileForRollBack.cvs
     * args[3]=outputFileForRollBack.cvs
     */
    public static void main(String[] args) {
        long starttime = System.currentTimeMillis();
        final String methodName = "main";
        UserMigrRollbackLog.log(methodName, "Entered: Constant.props=" + args[0] + "LogConfig.props=" + args[1] + "InputFile=" + args[2] + "OutputFile=" + args[3]);
        try {
            if (args.length != 4) {
                UserMigrRollbackLog.log(methodName, "ERROR IN COMMANDLINE ARGUMENTS");
                throw new BTSLBaseException("UserMigPartialRollBack ", " main ", "ERROR IN COMMANDLINE ARGUMENTS");// change
                                                                                                                   // error
                                                                                                                   // code
            }
            UserMigPartialRollBack usermigrollbackOBJ = new UserMigPartialRollBack();
            boolean fileExtValStatus = usermigrollbackOBJ.validateFileExtesion(args[2], args[3]);
            if (fileExtValStatus == false) {
                UserMigrRollbackLog.log(methodName, "ERROR IN EXTENSION FOR INPUT AND OUTPUT FILE EXTENSION");
                throw new BTSLBaseException("UserMigPartialRollBack ", " main ", "ERROR IN EXTENSION FOR INPUT AND OUTPUT FILE EXTENSION");// change
                                                                                                                                           // error
                                                                                                                                           // code
            }
            boolean isFileValid = usermigrollbackOBJ.ValidateInputFileData(args[2]);
            if (isFileValid)
                usermigrollbackOBJ.process(args);
            else {
                throw new BTSLBaseException("UserMigPartialRollBack ", " main ", "Input File Constains ");
            }

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
            long endTime = System.currentTimeMillis();
            UserMigrRollbackLog.log(methodName, "Exiting: Total time taken for the execution= " + (endTime - starttime) / 1000 + " sec");
        }
    }

    /**
     * To load the to user parent and from parent user details for sending sms
     * after the rollback process.
     * 
     * @param p_con
     * @param p_fromChannelUserVo
     * @param p_toChannelUserVo
     */
    public void loadParentDetails(Connection p_con, UserRollBackVo p_userrollbackVo)

    {
        final String methodName = "loadParentDetails";
        UserMigrRollbackLog.log(methodName, "Entered with fromChannelUser = " + p_userrollbackVo.getNewUserId() + " toChannelUser = " + p_userrollbackVo.getOldUserID());
        String selectlocale = "select PHONE_LANGUAGE,COUNTRY FROM USER_PHONES WHERE USER_ID=?";
        String selectPrntDtl = "select p.USER_NAME,p.MSISDN from users u,users p where (p.user_id=case when u.PARENT_ID ='ROOT' then u.user_id else u.parent_id end)and u.user_id=? and u.status='Y'";

        UserMigrRollbackLog.log(methodName, "selectlocale : " + selectlocale);
        UserMigrRollbackLog.log(methodName, "selectPrntDtl : " + selectPrntDtl);
        ResultSet rsSelectlocale = null;
        ResultSet rsSelectPrnt = null;
        try (PreparedStatement psmtLocale = p_con.prepareStatement(selectlocale);
        	 PreparedStatement psmtSelectPr = p_con.prepareStatement(selectPrntDtl);){
         

            psmtLocale.setString(1, p_userrollbackVo.getNewUserId());
            rsSelectlocale = psmtLocale.executeQuery();
            while (rsSelectlocale.next()) {
                p_userrollbackVo.setNewUserPhoneLang(rsSelectlocale.getString("PHONE_LANGUAGE"));
                p_userrollbackVo.setNewUserCountry(rsSelectlocale.getString("COUNTRY"));
            }
            psmtLocale.clearParameters();
            psmtSelectPr.setString(1, p_userrollbackVo.getNewUserId());
            try{
            rsSelectPrnt = psmtSelectPr.executeQuery();
            while (rsSelectPrnt.next()) {
                p_userrollbackVo.setNewUserParentName(rsSelectPrnt.getString("USER_NAME"));
                p_userrollbackVo.setNewUserParentMsisdn(rsSelectPrnt.getString("MSISDN"));
            }
            }
            finally{
            	if(rsSelectPrnt!=null)
            	rsSelectPrnt.close();
            }
            psmtSelectPr.clearParameters();
            rsSelectPrnt = null;
            psmtSelectPr.setString(1, p_userrollbackVo.getOldUserID());
            rsSelectPrnt = psmtSelectPr.executeQuery();
            while (rsSelectPrnt.next()) {
                p_userrollbackVo.setOldUserParentName(rsSelectPrnt.getString("USER_NAME"));
                p_userrollbackVo.setOldUserParentMsisdn(rsSelectPrnt.getString("MSISDN"));
            }

        } catch (SQLException sqlexp) {
            _log.errorTrace(methodName, sqlexp);
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
        } finally {
            try {
                rsSelectPrnt.close();
                rsSelectlocale.close();
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            UserMigrRollbackLog.log(methodName, "Exited : ");
        }
    }

    /**
     * This will push the SMS to the user/from Parent user /To Parent User.
     * 
     * @param p_con
     * @param p_fromChannelUserVo
     * @param p_toChannelUserVo
     */
    public void sendMessageToMigratedUsers(Connection p_con, UserRollBackVo p_userrollbackVo) {
        UserMigrRollbackLog.log("sendMessageToMigratedUsers Entered :", " networkCode: " + p_userrollbackVo.getOldUserNetworkCode() + " user_phone_language : " + p_userrollbackVo.getNewUserPhoneLang() + " user_country_code: " + p_userrollbackVo.getNewUserCountry());
        loadParentDetails(p_con, p_userrollbackVo);
        String[] arr = new String[2];
        String networkCode = p_userrollbackVo.getNewUserNetworkCode();
        arr[0] = p_userrollbackVo.getNewUserParentName();
        arr[1] = p_userrollbackVo.getOldUserParentName();
        Locale locale = new Locale(p_userrollbackVo.getNewUserPhoneLang(), p_userrollbackVo.getNewUserCountry());
        BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.CHANNELUSER_TRANSFERUSERHIERARCHY_MSG_SUCCESS_WITHOUTPRODUCT, arr);
        PushMessage pushMessage = new PushMessage(p_userrollbackVo.getMsisdn(), btslMessage, null, null, locale, networkCode);
        pushMessage.push();

        // From parent user
        BTSLMessages btslMessageFrom = new BTSLMessages(PretupsErrorCodesI.CHANNELUSER_TRANSFERUSERHIERARCHY_SENDER_PARENT, new String[] { p_userrollbackVo.getNewUserName() });
        PushMessage pushMessageFrom = new PushMessage(p_userrollbackVo.getNewUserParentMsisdn(), btslMessageFrom, null, null, locale, networkCode);
        pushMessageFrom.push();

        // To Parent User
        // to avoid the replicated sms to same parent .
        if (!(p_userrollbackVo.getNewUserParentMsisdn().equalsIgnoreCase(p_userrollbackVo.getOldUserParentMsisdn()))) {
            BTSLMessages btslMessageTo = new BTSLMessages(PretupsErrorCodesI.CHANNELUSER_TRANSFERUSERHIERARCHY_RECEIVER_PARENT, new String[] { p_userrollbackVo.getNewUserName() });
            PushMessage pushMessageTo = new PushMessage(p_userrollbackVo.getOldUserParentMsisdn(), btslMessageTo, null, null, locale, networkCode);
            pushMessageTo.push();
        }
        UserMigrRollbackLog.log("sendMessageToMigratedUsers", "Exited :");
    }

    /**
     * To rename input file after the execution.
     * 
     * @param p_file
     */
    public void renameFile(String p_file) {
        UserMigrRollbackLog.log("renameFile Entered :", p_file);
        String fileextension = ".csv";
        File f = new File(p_file);
        Date curdate = new Date();
        String s = "_EXECUTED-" + curdate.getDate() + "-" + curdate.getMonth() + "-" + curdate.getYear();
        String failfile[] = p_file.split(fileextension);
        f.renameTo(new File(failfile[0] + s + fileextension));
        UserMigrRollbackLog.log("renameFile Exiting File :" + p_file + " Renamed to :", failfile[0] + s + fileextension);
    }

}
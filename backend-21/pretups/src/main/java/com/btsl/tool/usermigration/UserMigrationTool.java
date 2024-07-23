package com.btsl.tool.usermigration;

/**
 * @(#)UserMigrationTool.java
 *                            Copyright(c) 2010, Comviva Technologies Ltd.
 *                            All Rights Reserved
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Author Date History
 *                            --------------------------------------------------
 *                            -----------------------------------------------
 *                            Vinay Singh June 05,2010 Initial Creation
 *                            Ashish Kumar Todia June 14,2010 Modification.
 *                            --------------------------------------------------
 *                            ----------------------------------------------
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.user.businesslogic.UserCategoryVO;
import com.btsl.pretups.user.businesslogic.UserGeoDomainVO;
import com.btsl.pretups.user.businesslogic.UserMessageVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.ibm.icu.util.Calendar;

public class UserMigrationTool {
    private static Log _log = LogFactory.getLog(UserMigrationTool.class.getName());
    private static List<UserMigrationVO> _userMigrationList = new ArrayList<UserMigrationVO>();
    private static List<UserMigrationVO> _finalUserMigrList = new ArrayList<UserMigrationVO>();
    private static List<UserMigrationVO> _errorUserList = new ArrayList<UserMigrationVO>();
    private static List<UserMigrationVO> _errorParentUserList = new ArrayList<UserMigrationVO>();
    private static HashMap<String, UserCategoryVO> _catCodeMap = new HashMap<String, UserCategoryVO>();
    private static HashMap<String, UserGeoDomainVO> _userGeoDomCodeMap = new HashMap<String, UserGeoDomainVO>();
    private static HashMap<String, HashMap<String, UserMessageVO>> _profileGradeMap = new HashMap<String, HashMap<String, UserMessageVO>>();
    private static String dir = null;
    private static HashMap<String, String> _migrationDetailMap = new HashMap<String, String>();

   private static final Comparator<UserMigrationVO> DECREASING_SEQ_NO = new Comparator<UserMigrationVO>() {
        public int compare(UserMigrationVO vo1, UserMigrationVO vo2) {
            return vo1.getToUserCatCodeSeqNo().compareTo(vo2.getToUserCatCodeSeqNo());
        }
    };
    
    /**
	 * to ensure no class instantiation 
	 */
    private UserMigrationTool() {
        
    }

    /**
     * Main method which will take the below parameter as an arguments:
     * 1. Constants file
     * 2. UserLogConfig file
     * 3. File which contains the user's number and geographical domain code in
     * csv file format.
     */
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        Connection con = null;
        final String methodName = "main";
        UserMigrDetailLog.log(methodName, "", "Entered : ");
        boolean success = false;
        try {
            if (args.length != 3) {
                System.out.println(" Please provide the Constants file or UserLogConfig file or User list file in csv format or UserMigrationConfig.");
                return;
            }
            // Check for the Constants.props
            File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                UserMigrDetailLog.log("UserMigrationTool main() Constants file not found on location:: ", "", constantsFile.toString());
                return;
            }
            // Check for the UserLogconfig.props
            File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists()) {
                UserMigrDetailLog.log("UserMigrationTool main() UserLogconfig file not found on location:: ", "", logconfigFile.toString());
                return;
            }
            // Check for the From User List file
            File UserMigrationFile = new File(args[2]);
            if (!UserMigrationFile.exists()) {
                UserMigrDetailLog.log("UserMigrationTool main() To User List file not found on location:: ", "", UserMigrationFile.toString());
                return;
            }
            // To load the process & constants file cache.
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            try {
                // Upload the input files
                uploadFiles(UserMigrationFile.toString());
                // Create an oracle connection
                con = OracleUtil.getSingleConnection();
                // check for the directory.
                dir = Constants.getProperty("DIR_PATH");
                if (BTSLUtil.isNullString(dir)) {
                    UserMigrDetailLog.log("validateFromUserDetails", "", "DIR_PATH is not defined in the property file.");
                    throw new BTSLBaseException("validateFromUserDetails", "DIR_PATH is not defined in the property file.");
                }
                File newDir = new File(dir);
                if (!newDir.exists())
                    success = newDir.mkdirs();
                else
                    success = true;
                if (!success)
                    throw new BTSLBaseException("validateFromUserDetails", "Not Able To Create the" + dir + " Direcoty.");
                // validation of the input records.
                validateUserDetails(con);

                // sort the final validated list based on category code sequence
                // number.
                try {
                    Collections.sort(_finalUserMigrList, DECREASING_SEQ_NO);
                } catch (Exception e) {
                    UserMigrDetailLog.log(methodName, "", "Exception while sorting the user's list. Error is: " + e);
                    _log.errorTrace(methodName, e);
                }

                _migrationDetailMap.put("UPLOADED_USERS", String.valueOf(_userMigrationList.size()));
                _migrationDetailMap.put("VALID_USERS", String.valueOf(_finalUserMigrList.size()));
                _migrationDetailMap.put("INVALID_USERS", String.valueOf(_errorUserList.size()));
                _migrationDetailMap.put("INVALID_PARENT_USERS", String.valueOf(_errorParentUserList.size()));

                // migration of users starts from here.
                if (_finalUserMigrList.size() > 0 && _errorParentUserList.size() == 0) {
                    migrateUsers(con);
                    renameFile(args[2]); // renaming after execution.
                } else {
                    UserMigrDetailLog.log("UserMigrationTool main()", "", "Invalid parent/child relation Record found in Error User List For Migration.");
                    // UserMigrDetailLog.log("UserMigrationTool main()", "",
                    // "No Valid Record Found For Migration.");
                }
            } catch (Exception e) {
                UserMigrDetailLog.log("UserMigrationTool main()", "", "Error in main is " + e);
                _log.errorTrace(methodName, e);
                return;
            }
        } catch (BTSLBaseException be) {
            UserMigrDetailLog.log(methodName, "", "BTSLBaseException error is " + be);
            _log.errorTrace(methodName, be);
        } catch (Exception e) {
            UserMigrDetailLog.log(methodName, "", "Exception error is " + e);
            _log.errorTrace(methodName, e);
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException excp) {
                    _log.errorTrace(methodName, excp);
                }
            }
            long endTime = System.currentTimeMillis();
            UserMigrDetailLog.log("UserMigrationTool main()", "", "Uploaded user list size=" + _migrationDetailMap.get("UPLOADED_USERS"));
            if (_migrationDetailMap.get("INVALID_PARENT_USERS") == null) {
                UserMigrDetailLog.log("UserMigrationTool main()", "", "Invalid user list size=" + _migrationDetailMap.get("INVALID_USERS"));
                UserMigrDetailLog.log("UserMigrationTool main()", "", "Valid user list size=" + _migrationDetailMap.get("VALID_USERS"));
            } else {
                UserMigrDetailLog.log("UserMigrationTool main()", "", "Invalid parent details(active child missing in file) list size=" + _migrationDetailMap.get("INVALID_PARENT_USERS"));
            }
            // UserMigrDetailLog.log("UserMigrationTool main()","",
            // "Invalid parent details(active child missing in file) list size="+_migrationDetailMap.get("INVALID_PARENT_USERS"));
            if (_migrationDetailMap.get("INVALID_PARENT_USERS") == null || "0".equals(_migrationDetailMap.get("INVALID_PARENT_USERS"))) {
                if (_migrationDetailMap.get("MIGRATED_USER") == null)
                    UserMigrDetailLog.log("UserMigrationTool main()", "", "User migrated successfully= 0");
                else
                    UserMigrDetailLog.log("UserMigrationTool main()", "", "User migrated successfully=" + _migrationDetailMap.get("MIGRATED_USER"));
                if (_migrationDetailMap.get("FAILED_USER_WHILE_MIGRATION") == null)
                    UserMigrDetailLog.log("validateFromUserDetails", "", "Error user list size while migration= 0");
                else
                    UserMigrDetailLog.log("UserMigrationTool main()", "", "Error user list size while migration=" + _migrationDetailMap.get("FAILED_USER_WHILE_MIGRATION"));
            } else {
                UserMigrDetailLog.log("UserMigrationTool main()", "", "Users not migrated because of some parent users not moving all their childs.");
            }
            UserMigrDetailLog.log("UserMigrationTool main() Exiting :", "Total time taken for the execution= ", +(endTime - startTime) / 1000 + " sec");
            ConfigServlet.destroyProcessCache();

            _userMigrationList = null;
            _errorUserList = null;
            _finalUserMigrList = null;
            _errorParentUserList = null;
        }
    }

    /**
     * Load the From user list file & To user list file.
     * 
     * @param fileName
     *            String
     * @throws IOException
     * @throws Exception
     */
    public static void uploadFiles(String p_userMigrationFile) throws IOException {
        final String methodName = "uploadFiles";
        UserMigrDetailLog.log(methodName, "", "Entered : p_userMigrationFile = " + p_userMigrationFile);
        BufferedReader br = null;
        FileReader fileReader = null;
        String line = null;
        String str[] = null;
        int counter = 1;
        UserMigrationVO userMigrationVO = null;
        try {// Ved-Check
            fileReader = new FileReader(p_userMigrationFile);
            br = new BufferedReader(fileReader);
            br.readLine(); // to skip the header part of the input file.
            while (br.read() != -1) {
                line = br.readLine();
                counter++;
                if (BTSLUtil.isNullString(line)) {
                    UserMigrDetailLog.log(methodName, "", "Blanck line= " + counter);
                    continue;
                }
                if (!line.contains(",")) {
                    UserMigrDetailLog.log(methodName, "", "line no= " + counter + " Text doesn't contain comma=" + line);
                    continue;
                }
                str = line.split(",");
                userMigrationVO = new UserMigrationVO();
                /*
                 * File format:
                 * 0-fromparentMsisdn
                 * 1-fromparentGeo
                 * 2-fromparentCatCode
                 * 3-fromuserMsisdn,
                 * 4-fromuserGeo,
                 * 5-fromuserCatCode,
                 * 6-toparentMsisdn,
                 * 7-toparentGeo,
                 * 8-toparentCatCode,
                 * 9-touserGeo,
                 * 10-touserCatCode
                 */
                if (str.length >= 11) {
                    if (str[0].trim().length() == 7)
                        userMigrationVO.setFromParentMsisdn("9" + str[0].trim());
                    else
                        userMigrationVO.setFromParentMsisdn(str[0].trim());
                    userMigrationVO.setFromParentGeoCode(str[1].trim());
                    userMigrationVO.setFromParentCatCode(str[2].trim());
                    userMigrationVO.setFromUserMsisdn(str[3].trim());
                    userMigrationVO.setFromUserGeoCode(str[4].trim());
                    userMigrationVO.setFromUserCatCode(str[5].trim());
                    // To user info
                    userMigrationVO.setToParentMsisdn(str[6].trim());
                    userMigrationVO.setToParentGeoCode(str[7].trim());
                    userMigrationVO.setToParentCatCode(str[8].trim());
                    userMigrationVO.setToUserMsisdn(userMigrationVO.getFromUserMsisdn());
                    userMigrationVO.setToUserGeoCode(str[9].trim());
                    userMigrationVO.setToUserCatCode(str[10].trim());
                    userMigrationVO.setLineNumber(counter);
                } else {
                    UserMigrDetailLog.log(methodName, "", "line no= " + counter + " str.length=" + str.length + " Invalide record");
                    continue;
                }
                // Add the VO in to the list
                _userMigrationList.add(userMigrationVO);
            }
        } catch (IOException ioe) {
            UserMigrDetailLog.log(methodName, "", "IOException error is " + ioe);
            _log.errorTrace(methodName, ioe);
            throw ioe;
        } catch (Exception e) {
            if (br != null)
                br.close();
            UserMigrDetailLog.log(methodName, "", "Exception error is " + e);
            _log.errorTrace(methodName, e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    _log.errorTrace(methodName, e);
                }
            }
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    _log.errorTrace(methodName, e);
                }
            }
            UserMigrDetailLog.log(methodName, "", "Exiting with _userList size=" + _userMigrationList.size());
        }
    }

    /**
     * Validate the from user file, whether the user exist in the system or not.
     * 
     * @param Connection
     *            p_con
     * @throws BTSLBaseException
     * @throws Exception
     */
    public static void validateUserDetails(Connection p_con) throws BTSLBaseException, Exception {
        final String methodName = "validateUserDetails";
        UserMigrDetailLog.log(methodName, "", "Entered:");
        UserMigrationDAO userMigrationDAO = null;
        try {
            userMigrationDAO = new UserMigrationDAO();
            _finalUserMigrList = userMigrationDAO.validateFromUsers(p_con, _userMigrationList, _errorUserList);
            // Arrange the _validatedUserList data based on the sequence number
            // of to_user.

            // validation for childs count into the System and
            // _finalUserMigrList(shishupal)
            userMigrationDAO.validateChildCounts(_userMigrationList, _errorParentUserList, _migrationDetailMap);
            // end here (shishupal)
        } catch (BTSLBaseException be) {
            UserMigrDetailLog.log(methodName, "", "BTSLBaseException error is: " + be);
            _log.errorTrace(methodName, be);
            throw be;
        } finally {
            if (_errorUserList.size() > 0) {
                // Ved- check
                // create dir and move at top of the process.
                UserMigrDetailLog.log("loadUserDetails", "", "dir=" + dir);
                Writer output = null;
                String errorFile = "InvalidUserList.csv";
                File errorListFile = new File(dir + errorFile);
                String errorMsg = "";
                output = new BufferedWriter(new FileWriter(errorListFile));
                for (int i = 0; i < _errorUserList.size(); i++) {
                    errorMsg = "Line No=" + _errorUserList.get(i).getLineNumber() + ", MSISDN=" + _errorUserList.get(i).getFromUserMsisdn() + ", Error message=" + _errorUserList.get(i).getMessage();
                    output.write(errorMsg);
                    output.write("\n");
                }
                output.close();
            }
            UserMigrDetailLog.log(methodName, "", "Exiting");
        }
    }

    /**
     * Migrate the user from current hierarchy to new hierarchy.
     * 
     * @param p_con
     * @throws Exception
     */
    public static void migrateUsers(Connection p_con) throws BTSLBaseException, Exception {
        final String methodName = "migrateUsers";
        UserMigrDetailLog.log(methodName, "", "Entered ");
        UserMigrationDAO userMigDAO = null;
        try {
            userMigDAO = new UserMigrationDAO();
            _catCodeMap = userMigDAO.loadCategoryMap(p_con, _profileGradeMap);
            _userGeoDomCodeMap = userMigDAO.loadGeoDomainCode(p_con);
            userMigDAO.userMigrationProcess(p_con, _finalUserMigrList, _catCodeMap, _userGeoDomCodeMap, _profileGradeMap, _migrationDetailMap);
        } catch (BTSLBaseException be) {
            UserMigrDetailLog.log(methodName, "", "BTSLBaseException error is: " + be);
            _log.errorTrace(methodName, be);
            throw be;
        }
    }

    /**
     * To rename the input file after migrating the users.
     * 
     * @param p_file
     */
    public static void renameFile(String p_file) {
        UserMigrDetailLog.log("renameFile Entered :", "", p_file);
        String fileextension = ".csv";
        File f = new File(p_file);

        String s = "_EXECUTED-" + Calendar.DATE + "-" + Calendar.MONTH + "-" + Calendar.YEAR;
        String failfile[] = p_file.split(fileextension);
        f.renameTo(new File(failfile[0] + s + fileextension));
        UserMigrDetailLog.log("renameFile Exiting File :" + p_file + " Renamed to :", "", failfile[0] + s + fileextension);
    }
}

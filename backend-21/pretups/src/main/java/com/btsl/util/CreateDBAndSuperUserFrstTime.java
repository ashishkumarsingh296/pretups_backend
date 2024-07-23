package com.btsl.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.btsl.common.TypesI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;

public class CreateDBAndSuperUserFrstTime {

    private static Log _log = LogFactory.getLog(CreateDBAndSuperUserFrstTime.class.getName());

    /**
   	 * to ensure no class instantiation 
   	 */
    private CreateDBAndSuperUserFrstTime() {
    }

    private static String usrLoginID = null;
    private static String usrPass = null;
    private static String usrConfirmPass = null;

    private static String rptDBloginID = null;
    private static String rptDBPass = null;
    private static String filePath = null;
    private static File fSourceStore = null;
    private static Connection con = null;
    private static Connection rptConn = null;

    private static String sourceConstFile = "Constants.props";
    private static String desFile = "Constants_temp.props";

    private static InputStreamReader isr = null;
    private static BufferedReader br = null;
    private static int retryCount = 3;
    private static String consoleMeassage = "Got some internal error while updating system configurations. Try again";

    public static void renameFile(String file, String toFile) {
        final String methodName = "renameFile";
        if (_log.isDebugEnabled()) {
            _log.debug("methodName", "Entered: source file=" + file + " destination file=" + toFile);
        }

        final File toBeRenamed = new File(filePath + file);

        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {
            if (_log.isDebugEnabled()) {
                _log.debug("methodName", "File does not exist :: " + file);
            }
            return;
        }

        final File newFile = new File(filePath + toFile);

        // Rename
        if (toBeRenamed.renameTo(newFile)) {
            if (_log.isDebugEnabled()) {
                _log.debug("methodName", "File has been renamed");
            }
        } else {
            if (_log.isDebugEnabled()) {
                _log.debug("methodName", "Error renmaing file");
            }
        }
    }

    public static boolean getDBUserInputFromConsole(String p_dbType) {
        final String methodName = "getDBUserInputFromConsole";
        if (_log.isDebugEnabled()) {
            _log.debug("methodName", "Entered");
        }
        boolean invalid = true;

        try {

            if (("onlineDb").equals(p_dbType)) {
                _log.info(methodName, "Enter Online Database Details");
            } else {
                _log.info(methodName, "Enter Reporting Database Details");
            }
            invalid = getLoginDetails(p_dbType);

        } catch (Exception e) {
            if (_log.isDebugEnabled()) {
                _log.debug("methodName", "Exception " + getStackTraceAsString(e));
            }
            return false;
        }
        if (_log.isDebugEnabled()) {
            _log.debug("methodName", "Exiting");
        }
        return true;
    }

    public static Connection validateDBConnection(String p_dbType, String p_userloginID, String p_userPass) {
        final String methodName = "validateDBConnection";
        if (_log.isDebugEnabled()) {
            _log.debug("methodName", "Entered to validate DB connection, p_userloginID ::" + p_userloginID + "p_userPass ::" + p_userPass);
        }
        Connection con = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Constants.load(fSourceStore.getAbsolutePath());

            if ("onlineDb".equals(p_dbType)) {
                con = DriverManager.getConnection(Constants.getProperty("datasourceurl"), p_userloginID, p_userPass);
            } else {
                con = DriverManager.getConnection(Constants.getProperty("reportdbdatasourceurl"), p_userloginID, p_userPass);
            }

            if (con != null) {
                _log.info(methodName, "Connection Test Success !");
            }

            if (_log.isDebugEnabled()) {
                _log.debug("methodName", "Exiting after getting valid DB connection");
            }

        } catch (ClassNotFoundException e) {
            if (_log.isDebugEnabled()) {
                _log.debug("methodName", "ClassNotFoundException, Database Connection Fail" + getStackTraceAsString(e));
            }
            return null;
        } catch (IOException IOEx) {
            if (_log.isDebugEnabled()) {
                _log.debug("methodName", "IO Exception while reading Constants.props file" + getStackTraceAsString(IOEx));
            }
            return null;
        } catch (SQLException e2) {
            if (_log.isDebugEnabled()) {
                _log.debug("methodName", "SQLException, Database Connection Fail" + getStackTraceAsString(e2));
            }
            if (e2.getErrorCode() == 1017) {
                _log.info(methodName, "             Authentication failed due to Invalid loginID/Password        \n\n   ");
            }
            return null;
        }
        catch (URISyntaxException e) {
        	       	_log.errorTrace(methodName, e);
        	      	return null;
        			}
        
        
        return con;
    }

    public static boolean IsRecordExist(Connection con) {
        final String methodName = "IsRecordExist";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }

        boolean recordExist = false;
        final String sqlSelect = "select 1 from users";
        ResultSet rs = null;
        PreparedStatement pstmt = null;
        try {
            pstmt = con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                recordExist = true;
                _log.info(methodName, "Super user is already exist in the system, check it again");
                break;
            }
        } catch (Exception e) {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "SQLException, Database Connection Fail" + getStackTraceAsString(e));
            }
            _log.info(methodName, consoleMeassage);
            return recordExist = true;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
        }
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Exiting with recordExist::" + recordExist);
        }

        return recordExist;
    }

    public static boolean isConfigFileUpdated() {
        final String methodName = "isConfigFileUpdated";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }

        String incomming = "";
        FileReader frStore = null;
        FileWriter fwStore = null;		
        BufferedReader brStore = null;
        BufferedWriter bwStore = null;
        try {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "fSourceStore :: " + fSourceStore);
            }
            final File fDestStore = new File(filePath + desFile);
            frStore = new FileReader(fSourceStore);
            fwStore = new FileWriter(fDestStore);
            brStore = new BufferedReader(frStore);
            bwStore = new BufferedWriter(fwStore);

            boolean bSearch = true;
            while (bSearch == true) {
                incomming = brStore.readLine();

                if (incomming == null) {
                    // End of File
                    bwStore.close();
                    brStore.close();
                    frStore.close();
                    fwStore.close();
                    if (fSourceStore.delete()) {
                        renameFile(desFile, sourceConstFile);
                    }
                    bSearch = false;
                } else if (incomming.startsWith("userid=")) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Updating config file for Online DB login ID");
                    }
                    bwStore.write("userid=" + usrLoginID);
                    bwStore.newLine();
                } else if (incomming.startsWith("passwd=")) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Updating config file for Online DB password");
                    }
                    bwStore.write("passwd=" + usrPass);
                    bwStore.newLine();
                } else if (incomming.startsWith("reportdbuserid=")) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Updating config file for RPT DB login ID");
                    }
                    bwStore.write("reportdbuserid=" + rptDBloginID);
                    bwStore.newLine();
                } else if (incomming.startsWith("reportdbpasswd")) {
                    if (_log.isDebugEnabled()) {
                        _log.debug(methodName, "Updating config file for RPT DB password");
                    }
                    bwStore.write("reportdbpasswd=" + rptDBPass);
                    bwStore.newLine();
                } else {
                    // Keep information in file
                    bwStore.write(incomming);
                    bwStore.newLine();
                }
            }
            return true;
        } catch (IOException e) {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Got some internal error while updating system configurations. Try again " + getStackTraceAsString(e));
            }
            _log.info(methodName, consoleMeassage);
            return false;
        } catch (Exception e) {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Got some internal error while updating system configurations. Try again " + getStackTraceAsString(e));
            }
            _log.info(methodName, consoleMeassage);
            return false;
        } finally {
            if (brStore != null) {
                try {
                    brStore.close();
                } catch (IOException e) {
                    _log.errorTrace(methodName, e);
                }
            }
            if (bwStore != null) {
                try {
                    bwStore.close();
                } catch (IOException e) {
                    _log.errorTrace(methodName, e);
                }
            }
            if (frStore != null) {
                try {
                	frStore.close();
                } catch (IOException e) {
                    _log.errorTrace(methodName, e);
                }
            }
            if (fwStore != null) {
                try {
                	fwStore.close();
                } catch (IOException e) {
                    _log.errorTrace(methodName, e);
                }
            }
        }
    }

    public static void updateSuperUserVo(ChannelUserVO channelUserVO) {
        final String methodName = "updateSuperUserVo";
        if (_log.isDebugEnabled()) {
            _log.debug("methodName", "Entered ");
        }

        String usaeName = null;
        String networkID = null;
        String msisdn = null;
        isr = new InputStreamReader(System.in);
        br = new BufferedReader(isr);

        try {

            _log.info(methodName, "Enter Super User Details");

            _log.info(methodName, "Enter Super user name :- ");
            usaeName = br.readLine();

            getLoginDetails("apps");

            _log.info(methodName, "Enter Super user network Code :- ");
            networkID = br.readLine();
            _log.info(methodName, "Enter Super contact Info :- ");
            msisdn = br.readLine();

            channelUserVO.setUserName(usaeName);
            channelUserVO.setLoginID(usrLoginID);

            final String password = OneWayHashingAlgoUtil.getInstance().encrypt(usrPass);
            channelUserVO.setPassword(password);
            channelUserVO.setNetworkID(networkID);
            channelUserVO.setMsisdn(msisdn);

            br.close();
            final Date currentDate = new Date();
            channelUserVO.setUserID("SU0001");
            channelUserVO.setCategoryCode("SUADM");
            channelUserVO.setParentID("SU0001");
            channelUserVO.setOwnerID("SU0001");
            channelUserVO.setCreatedBy("ADMIN");
            channelUserVO.setCreatedOn(currentDate);
            channelUserVO.setModifiedBy("SU0001");
            channelUserVO.setModifiedOn(currentDate);
            channelUserVO.setAllowedIps("");
            channelUserVO.setAllowedDays("");
            channelUserVO.setFromTime("");
            channelUserVO.setToTime("");
            channelUserVO.setEmpCode("");
            channelUserVO.setContactNo("");
            channelUserVO.setStatus(PretupsI.USER_STATUS_ACTIVE);// Y New
            channelUserVO.setPreviousStatus(PretupsI.USER_STATUS_NEW);// N New
            channelUserVO.setEmail("");
            // Added by deepika aggarwal
            channelUserVO.setCompany("");
            channelUserVO.setFax("");
            channelUserVO.setFirstName("");
            channelUserVO.setLastName("");
            // end added by deepika aggarwal
            channelUserVO.setPasswordModifiedOn(currentDate);
            channelUserVO.setDesignation("");
            channelUserVO.setDepartmentCode("");
            channelUserVO.setDivisionCode("");
            channelUserVO.setUserType(TypesI.OPERATOR_USER_TYPE);
            channelUserVO.setUserCode("");

            channelUserVO.setAddress1("");
            channelUserVO.setAddress2("");
            channelUserVO.setCity("");
            channelUserVO.setState("");
            channelUserVO.setCountry("");
            channelUserVO.setSsn("");
            channelUserVO.setUserNamePrefix("");
            channelUserVO.setExternalCode("");
            channelUserVO.setShortName("");
            channelUserVO.setAppointmentDate(currentDate);
            channelUserVO.setActivatedOn(currentDate);

            if (_log.isDebugEnabled()) {
                _log.debug("methodName", "Exiting with channelUserVO ::" + channelUserVO.toString());
            }

        } catch (IOException IOEx) {
            if (_log.isDebugEnabled()) {
                _log.debug("methodName", "Got IOException ::" + getStackTraceAsString(IOEx));
            }

        } catch (Exception e) {
            if (_log.isDebugEnabled()) {
                _log.debug("methodName", "Got Exception ::" + getStackTraceAsString(e));
            }

        } finally {
        	try {
				if(br !=null) {
					br.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
        	try {
				if(isr !=null) {
					isr.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
        }
    }

    /**
     * @param args
     */
    /*public static void main(String[] args) {

        final String methodName = "main";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }

        try {

            filePath = BTSLUtil.getFilePath(CreateDBAndSuperUserFrstTime.class);
            final int endPoint = filePath.indexOf("com");

            filePath = filePath.subSequence(0, endPoint).toString();
            filePath = filePath.concat("configfiles" + File.separator);
            org.apache.log4j.PropertyConfigurator.configure(filePath + "LogConfig.props");

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Entered ");
            }

            fSourceStore = new File(filePath + sourceConstFile);

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Config filePath ::" + filePath);
            }

            _log.info(methodName, "Enter 1 for creating first time setup");
            _log.info(methodName, "Enter 2 for updating AES Keys");

            _log.debug(methodName, fSourceStore);

            isr = new InputStreamReader(System.in);
            br = new BufferedReader(isr);

            if (2 == Integer.parseInt(br.readLine().toString())) {
                updateCredtDBKey();
                return;
            } else if (2 == Integer.parseInt(br.readLine().toString())) {

                br.close();
                isr.close();

                if (!getDBUserInputFromConsole("onlineDb")) {
                    return;
                }

                con = validateDBConnection("onlineDb", usrLoginID, usrPass);

                if (con == null) {
                    return;
                }

                if (!getDBUserInputFromConsole("rptDB")) {
                    return;
                }

                rptConn = validateDBConnection("rptDB", rptDBloginID, rptDBPass);
                if (rptConn == null) {
                    return;
                } else {
                    try {
                        rptConn.commit();
                    } catch (Exception e) {
                        _log.errorTrace(methodName, e);
                    }
                }

                final PasswordFromUser passwordFromUser = new PasswordFromUser();
                if (!passwordFromUser.updatePasswordInformation(con, rptConn)) {
                    return;
                }

                if (con != null) {
                    if (IsRecordExist(con)) {
                        return;
                    }
                } else {
                    return;
                }

                // if input details are valid then encrypt it & update
                // cofiguration file for Online DB.
                usrLoginID = BTSLUtil.encrypt3DesAesText(usrLoginID);
                usrPass = BTSLUtil.encrypt3DesAesText(usrPass);

                // if input details are valid then encrypt it & update
                // cofiguration file for RPT DB
                rptDBloginID = BTSLUtil.encrypt3DesAesText(rptDBloginID);
                rptDBPass = BTSLUtil.encrypt3DesAesText(rptDBPass);

                if (!isConfigFileUpdated()) {
                    return;
                }
                // Insert super user details
                // insert data into users table     

                final ChannelUserVO channelUserVO = new ChannelUserVO();

                updateSuperUserVo(channelUserVO);

                final UserDAO userDAO = new UserDAO();

                final int userCount = userDAO.addUser(con, channelUserVO);

                if (userCount <= 0) {
                    con.rollback();
                }
            }

        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e1) {
                _log.errorTrace(methodName, e1);
            }
        } finally {
            try {
                if (con != null) {
                    con.commit();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (rptConn != null) {
                    rptConn.close();
                }
            } catch (Exception e1) {
                _log.errorTrace(methodName, e1);
            }
        }
    }*/
    
    public static boolean getLoginDetails(String p_dbType) {
        final String methodName = "getLoginDetails";
        boolean isInvalid = true;
        try {
            isr = new InputStreamReader(System.in);
            br = new BufferedReader(isr);

            if (getLoginIDAndValidate(p_dbType) || getPasswordAndValidate(p_dbType)) {
                isInvalid = false;
            }
        } catch (Exception e) {
            _log.error("getLoginDetails ", ": The password could not be retreived ");
            _log.info(methodName, "Not able to get input data (Password) from console");
        } finally {
        	try {
				if(br !=null) {
					br.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
        	try {
				if(isr !=null) {
					isr.close();
				}
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
        }

        return isInvalid;

    }

    public static boolean getLoginIDAndValidate(String p_dbType) throws IOException {
        final String methodName = "getLoginIDAndValidate";
        boolean invalid = false;
        String loginID = null;
        // asks the user to input the password field usng the password class
        final PasswordField passfield = new PasswordField();
        try {
            _log.info(methodName, "Enter login ID :- ");
            loginID = br.readLine();
            if (("onlineDb".equals(p_dbType) || "rptDB".equals(p_dbType)) && loginID.length() == 0) {
                _log.info(methodName, "Login ID can not be balnk");
                return invalid = true;
            }

            // validatepassword
            if ((("apps").equals(p_dbType)) && (loginID.length() < 6 || loginID.length() > 8)) {
                for (int i = 0; i < retryCount; i++) {
                    _log.info(methodName, "LoginID length should be in between 6 to 8. Enter LoginID again:- ");
                    _log.info(methodName, "Enter login ID :- ");
                    loginID = br.readLine();
                    if (loginID.length() >= 6 && loginID.length() <= 8) {
                        invalid = false;
                        break;
                    }
                }
            }
            if ("rptDB".equals(p_dbType)) {
                rptDBloginID = loginID;
            } else {
                usrLoginID = loginID;
            }
        } catch (IOException e) {
            _log.error("getLoginIDAndValidate ", ": The password could not be retreived ");
            _log.info(methodName, "Not able to get input data (Password) from console");
            throw e;
        }

        return invalid;
    }

    public static boolean getPasswordAndValidate(String p_dbType) throws IOException {
        final String methodName = "getPasswordAndValidate";
        boolean invalid = false;
        String pass = null;
        // asks the user to input the password field usng the password class
        final PasswordField passfield = new PasswordField();
        try {
            _log.info(methodName, "Enter password :- ");
            pass = passfield.getPassword("");

            if (("onlineDb".equals(p_dbType) || "rptDB".equals(p_dbType)) && pass.length() == 0) {
                _log.info(methodName, "Password can not be balnk");
                return invalid = true;
            }

            // validatepassword
            if ("apps".equals(p_dbType) && (pass.length() < 6 || pass.length() > 8)) {
                for (int i = 0; i < retryCount; i++) {
                    _log.info(methodName, "Password length should be in between 6 to 8. Enter Password again:- ");
                    pass = passfield.getPassword("");
                    if (pass.length() >= 6 && pass.length() <= 8) {
                        invalid = false;
                        break;
                    }
                }
            }
            _log.info(methodName, "Enter confirm password :- ");
            usrConfirmPass = passfield.getPassword("");

            if (pass != null && !pass.equals(usrConfirmPass)) {
                _log.info(methodName, "Error :- Entered password and confirm should be same.");
                getPasswordAndValidate(p_dbType);
            }

            if ("rptDB".equals(p_dbType)) {
                rptDBPass = pass;
            } else {
                usrPass = pass;
            }

        } catch (IOException e) {
            _log.error("getDBUserInputFromConsole ", ": The password could not be retreived ");
            _log.info(methodName, consoleMeassage);
            throw e;
        }

        return invalid;
    }

    /**
     * Gets the exception stack trace as a string.
     * 
     * @param exception
     * @return
     */
    public static String getStackTraceAsString(Exception exception) {
        final StringWriter sw = new StringWriter();
        try(final PrintWriter pw = new PrintWriter(sw);)
        {
        pw.print(" [ ");
        pw.print(exception.getClass().getName());
        pw.print(" ] ");
        pw.print(exception.getMessage());
        exception.printStackTrace(pw);
        return sw.toString();
        }
        }

    public static boolean updateCredtDBKey() {
        final String methodName = "updateCredtDBKey";
        if (_log.isDebugEnabled()) {
            _log.debug("methodName", "Entered ");
        }
        Connection onLineCon = null;
        Connection rptCon = null;
        boolean isUpdated = false;
        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");
            Constants.load(fSourceStore.getAbsolutePath());

            _log.debug(methodName, "==" + Constants.getProperty("datasourceurl") + "::" + BTSLUtil.decrypt3DesAesText(Constants.getProperty("userid")) + "::" + BTSLUtil
                .decrypt3DesAesText(Constants.getProperty("passwd")));

            _log.debug(methodName,
                "==" + Constants.getProperty("reportdbdatasourceurl") + "::" + BTSLUtil.decrypt3DesAesText(Constants.getProperty("reportdbuserid")) + "::" + BTSLUtil
                    .decrypt3DesAesText(Constants.getProperty("reportdbpasswd")));

            onLineCon = DriverManager.getConnection(Constants.getProperty("datasourceurl"), BTSLUtil.decrypt3DesAesText(Constants.getProperty("userid")), BTSLUtil
                .decrypt3DesAesText(Constants.getProperty("passwd")));

            rptCon = DriverManager.getConnection(Constants.getProperty("reportdbdatasourceurl"), BTSLUtil.decrypt3DesAesText(Constants.getProperty("reportdbuserid")),
                BTSLUtil.decrypt3DesAesText(Constants.getProperty("reportdbpasswd")));

            if (onLineCon != null) {
                _log.info(methodName, "Primary DB Connection Test Success !");
            }

            if (rptCon != null) {
                _log.info(methodName, "Secondary DB Connection Test Success !");
            }

            final PasswordFromUser passwordFromUser = new PasswordFromUser();
            isUpdated = passwordFromUser.updatePasswordInformation(onLineCon, rptCon);

            return isUpdated;

        } catch (ClassNotFoundException e) {
            if (_log.isDebugEnabled()) {
                _log.debug("methodName", "ClassNotFoundException, Database Connection Fail" + getStackTraceAsString(e));
            }
            return false;
        } catch (IOException IOEx) {
            if (_log.isDebugEnabled()) {
                _log.debug("methodName", "IO Exception while reading Constants.props file" + getStackTraceAsString(IOEx));
            }
            return false;
        } catch (SQLException e2) {
            if (_log.isDebugEnabled()) {
                _log.debug("methodName", "SQLException, Database Connection Fail" + getStackTraceAsString(e2));
            }
            if (e2.getErrorCode() == 1017) {
                _log.info(methodName, "             Authentication failed due to Invalid loginID/Password        \n\n   ");
            }
            return false;
        } 
        catch (URISyntaxException e) {
        	_log.errorTrace(methodName, e);
        	return false;
		}finally {

            if (_log.isDebugEnabled()) {
                _log.debug("methodName", "Exiting after getting valid DB connection");
            }

        }
    }
}

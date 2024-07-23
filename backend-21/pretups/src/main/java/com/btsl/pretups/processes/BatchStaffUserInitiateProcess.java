/*
 * @# BatchStaffUserInitiateProcess.java
 * 
 * Created by Created on History
 * ------------------------------------------------------------------------------
 * --
 * Ved Sharma Feb 02, 2010 Modified
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2010 Comviva.
 */

package com.btsl.pretups.processes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

// import oracle.jdbc.OraclePreparedStatement;


import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.pretups.xl.BatchUserCreationExcelRW;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;

public class BatchStaffUserInitiateProcess {
    private static Log _log = LogFactory.getLog(BatchStaffUserInitiateProcess.class.getName());
    private static String error1;
    private static String error2;

    public static void main(String[] arg) {
        final String METHOD_NAME = "main";
        try {
            if (arg.length != 5)// check the argument length
            {
                System.out.println("BatchStaffUserInitiateProcess :: Not sufficient arguments, please pass Conatnsts.props ProcessLogconfig.props StaffUserFile");
                return;
            }
            final File constantsFile = new File(arg[0]);
            if (!constantsFile.exists())// check file (Constants.props) exist or
            // not
            {
                System.out.println("BatchStaffUserInitiateProcess" + " Constants File Not Found at the path : " + arg[0]);
                return;
            }
            final File logconfigFile = new File(arg[1]);
            if (!logconfigFile.exists())// check file (ProcessLogConfig.props)
            // exist or not
            {
                System.out.println("BatchStaffUserInitiateProcess" + " ProcessLogConfig File Not Found at the path : " + arg[1]);
                return;
            }
            final File staffFile = new File(arg[2]);
            if (!staffFile.exists())// check file (ProcessLogConfig.props) exist
            // or not
            {
                System.out.println("BatchStaffUserInitiateProcess" + "  Staff user creation File Not Found at the path : " + arg[2]);
                return;
            }
            if (!(arg[2]).toUpperCase().endsWith("XLS") && !(arg[2]).toUpperCase().endsWith("XLSX")) {
                System.out.println("BatchStaffUserInitiateProcess" + "  Staff user creation File Must be XLS extention : " + arg[2]);
                return;
            }
            error1 = arg[3];
            error2 = arg[4];

            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            NetworkPrefixCache.loadNetworkPrefixesAtStartup();
        } catch (Exception e) {
            _log.error("main", "Main: Error in loading the Cache information.." + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "BatchStaffUserInitiateProcess[main]", "", "", "",
                "  Error in loading the Cache information");
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        Connection con = null;MComConnectionI mcomCon = null;
        try {
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_log.isDebugEnabled()) {
                    _log.debug("main", " DATABASE Connection is NULL ");
                }
                throw new BTSLBaseException("", "main", "Not able to get the connection");
            }
            final BatchStaffUserInitiateProcess BatchStaffUserInitiateProcess = new BatchStaffUserInitiateProcess();
            BatchStaffUserInitiateProcess.processUploadedFile(con, arg[2]);
        } catch (BTSLBaseException be) {
            _log.error("main", "BTSLBaseException :" + be.getMessage());
            _log.errorTrace(METHOD_NAME, be);
            // event handle
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchStaffUserInitiateProcess[main]", "", "", "",
                "BTSLBaseException:" + be.getMessage());
        } catch (Exception e) {
            _log.error("main", "Exception :" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            // event handle
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchStaffUserInitiateProcess[main]", "", "", "",
                "Exception:" + e.getMessage());
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    private void processUploadedFile(Connection p_con, String p_file) throws BTSLBaseException, SQLException {
        if (_log.isDebugEnabled()) {
            _log.debug("processUploadedFile", "Entered");
        }
        final String METHOD_NAME = "processUploadedFile";
        int rows = 0;
        int cols = 0;
        final int totColsinXls = 17;
        String[][] excelArr = null;
        boolean fileValidationErrorExists = false;
        ArrayList<ListValueVO> fileErrorList = new ArrayList<ListValueVO>();
        final ArrayList<ChannelUserVO> userList = new ArrayList<ChannelUserVO>();
        final Date currentDate = new Date();
        ChannelUserVO staffUserVO = null;
        ChannelUserVO parentChannelUserVO = null;
        final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
        String filteredParentMsisdn = null;
        String filteredMsisdn = null;
        UserPhoneVO userPhoneVO = null;
        ListValueVO errorVO = null;
        PrintWriter out = null;
        try {
            // Open the uploaded XLS file parse row by row and validate the file
            final BatchUserCreationExcelRW excelRW = new BatchUserCreationExcelRW();
            excelArr = excelRW.readExcel("Batch Staff User", p_file);
            try {
                cols = excelArr[0].length;
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
                throw new BTSLBaseException(this, "processUploadedFile", e.getMessage());
            }
            rows = excelArr.length; // rows include the headings
            final int rowOffset = 1;
            final int maxRowSize = 65000;
            if (rows > maxRowSize) {
                throw new BTSLBaseException(this, "processUploadedFile", "more than " + maxRowSize + " row in file=" + p_file);
            }

            /*
             * 1. Parent msisdn
             * 2. Staff user name
             * 3. Staff msisdn
             * 4. PIN
             * 5. login id
             * 6. password
             * 7. Staff Short name
             * 8. Subscriber code
             * 9. Contact No
             * 10. SSN
             * 11. Designation
             * 12. Address1
             * 13. Address2
             * 14. City
             * 15. State
             * 16. Country
             * 17. E-Mail
             */

            if (cols == totColsinXls) {
                for (int r = rowOffset; r < rows; r++) {
                    if (!BTSLUtil.isNullString(excelArr[r][0])) // 1. Parent
                    // msisdn
                    {
                        excelArr[r][0] = excelArr[r][0].trim();
                        filteredParentMsisdn = PretupsBL.getFilteredMSISDN(excelArr[r][0]);
                        excelArr[r][0] = filteredParentMsisdn;
                        if (!BTSLUtil.isValidMSISDN(filteredParentMsisdn)) {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), "INvalid parent MSISDM");
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    } else {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), "Parent MSISDM is blank");
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }

                    if (BTSLUtil.isNullString(excelArr[r][1])) // 2. Staff user
                    // name
                    {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), "Name is mondatory");
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    } else {
                        excelArr[r][1] = excelArr[r][1].trim();
                        // Check User Name length
                        if (excelArr[r][1].length() > 80) {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), "Name is more than 80 character");
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    }

                    if (!BTSLUtil.isNullString(excelArr[r][2])) // 3. Staff
                    // msisdn
                    {
                        // Check MSISDN length
                        excelArr[r][2] = excelArr[r][2].trim();
                        filteredMsisdn = PretupsBL.getFilteredMSISDN(excelArr[r][2]);
                        excelArr[r][2] = filteredMsisdn;
                        if (!BTSLUtil.isValidMSISDN(filteredMsisdn)) {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), "invalide MSISDN ");
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    } else {
                        excelArr[r][2] = PretupsI.NOT_AVAILABLE;
                    }

                    if (!BTSLUtil.isNullString(excelArr[r][3])) // 4. PIN
                    {
                        excelArr[r][3] = excelArr[r][3].trim();
                        if (excelArr[r][3].length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SMS_PIN_LENGTH))).intValue() || excelArr[r][3].length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SMS_PIN_LENGTH))).intValue()) {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), "INvalid pin");
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }

                    } else {
                        excelArr[r][3] = PretupsI.NOT_AVAILABLE;
                    }

                    if (BTSLUtil.isNullString(excelArr[r][4])) // 5. login id
                    {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), "login ID is mondatory");
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    } else {
                        excelArr[r][4] = excelArr[r][4].trim();
                        // Check User Name length
                        if (excelArr[r][4].length() > 20) {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), "lOGIN ID more than 20 character");
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    }
                    if (!BTSLUtil.isNullString(excelArr[r][5])) // 6. Password
                    {
                        excelArr[r][5] = excelArr[r][5].trim();
                        // password length
                        if (excelArr[r][5].length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH))).intValue() || excelArr[r][5].length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH))).intValue()) {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), "password  more than 60 character");
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    } else {
                        excelArr[r][5] = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_PASSWORD));
                    }

                    if (!BTSLUtil.isNullString(excelArr[r][6])) // 7. Staff
                    // Short name
                    {
                        excelArr[r][6] = excelArr[r][6].trim();
                        // Staff Short code length
                        if (excelArr[r][6].length() > 15) {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), "Staff Short code  more than 15 character");
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    }

                    if (!BTSLUtil.isNullString(excelArr[r][7])) // 8. Subscriber
                    // code
                    {
                        excelArr[r][7] = excelArr[r][7].trim();
                        // User code length
                        if (excelArr[r][7].length() > 15) {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), "Subscriber code  more than 15 character");
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    }

                    if (!BTSLUtil.isNullString(excelArr[r][8])) // * 9. Contact
                    // No
                    {
                        excelArr[r][8] = excelArr[r][8].trim();
                        // Contact No length
                        if (excelArr[r][8].length() > 50) {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), "Contact No  more than 50 character");
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    }
                    if (!BTSLUtil.isNullString(excelArr[r][9])) // * * 10. SSN
                    {
                        excelArr[r][9] = excelArr[r][9].trim();
                        // SSN Length
                        if (excelArr[r][9].length() > 15) {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), "SSN  more than 15 character");
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    }
                    if (!BTSLUtil.isNullString(excelArr[r][10])) // * 11.
                    // Designation
                    {
                        excelArr[r][10] = excelArr[r][10].trim();
                        // Designation Length
                        if (excelArr[r][10].length() > 15) {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), "Designation  more than 15 character");
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    }
                    if (!BTSLUtil.isNullString(excelArr[r][11]))// * 12.
                    // Address1
                    {
                        excelArr[r][11] = excelArr[r][11].trim();
                        // Address1 Length
                        if (excelArr[r][11].length() > 15) {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), "Address1  more than 15 character");
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    }
                    if (!BTSLUtil.isNullString(excelArr[r][12]))// * 13.
                    // Address2
                    {
                        excelArr[r][12] = excelArr[r][12].trim();
                        // Address2 Length
                        if (excelArr[r][12].length() > 15) {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), "Address2  more than 15 character");
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    }

                    if (!BTSLUtil.isNullString(excelArr[r][13]))// * 14. City
                    {
                        excelArr[r][13] = excelArr[r][13].trim();
                        // City Length
                        if (excelArr[r][13].length() > 15) {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), "City  more than 15 character");
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    }
                    if (!BTSLUtil.isNullString(excelArr[r][14]))// * * 15. State
                    {
                        excelArr[r][14] = excelArr[r][14].trim();
                        // State Length
                        if (excelArr[r][14].length() > 15) {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), "State  more than 15 character");
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    }

                    if (!BTSLUtil.isNullString(excelArr[r][15]))// * * 16.
                    // Country
                    {
                        excelArr[r][15] = excelArr[r][15].trim();
                        // Country Length
                        if (excelArr[r][15].length() > 15) {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), "Country  more than 15 character");
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    }
                    if (!BTSLUtil.isNullString(excelArr[r][16]))// * * 17.
                    // E-Mail
                    {
                        excelArr[r][16] = excelArr[r][16].trim();
                        // E-Mail Length
                        if (excelArr[r][16].length() > 60) {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), "E-Mail  more than 60 character");
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }
                    }
                    parentChannelUserVO = channelUserDAO.loadChannelUserDetails(p_con, excelArr[r][0]);
                    if (parentChannelUserVO != null) {
                        if (PretupsI.USER_STATUS_ACTIVE.equalsIgnoreCase(parentChannelUserVO.getStatus())) {
                            staffUserVO = new ChannelUserVO();
                            userPhoneVO = new UserPhoneVO();

                            /*
                             * 1. Parent msisdn
                             * 2. Staff user name
                             * 3. Staff msisdn
                             * 4. PIN
                             * 5. login id
                             * 6. password
                             * 7. Staff Short name
                             * 8. Subscriber code
                             * 9. Contact No
                             * 10. SSN
                             * 11. Designation
                             * 12. Address1
                             * 13. Address2
                             * 14. City
                             * 15. State
                             * 16. Country
                             * 17. E-Mail
                             */
                            staffUserVO.setUserID(generateUserId(parentChannelUserVO.getNetworkID(), parentChannelUserVO.getCategoryVO().getUserIdPrefix()));
                            staffUserVO.setUserType(PretupsI.STAFF_USER_TYPE);
                            staffUserVO.setNetworkID(parentChannelUserVO.getNetworkID());
                            staffUserVO.setCategoryCode(parentChannelUserVO.getCategoryCode());
                            staffUserVO.setParentID(parentChannelUserVO.getUserID());
                            staffUserVO.setOwnerID(parentChannelUserVO.getOwnerID());
                            staffUserVO.setUserGrade(parentChannelUserVO.getUserGrade());
                            staffUserVO.setTransferProfileID(parentChannelUserVO.getTransferProfileID());
                            staffUserVO.setCommissionProfileSetID(parentChannelUserVO.getCommissionProfileSetID());
                            staffUserVO.setAssociatedServiceTypeList(parentChannelUserVO.getAssociatedServiceTypeList());
                            staffUserVO.setGeographicalCode(parentChannelUserVO.getGeographicalCode());

                            staffUserVO.setModifiedBy(PretupsI.SYSTEM);
                            staffUserVO.setLastLoginOn(currentDate);
                            staffUserVO.setPasswordModifiedOn(currentDate);
                            staffUserVO.setCreatedOn(currentDate);
                            staffUserVO.setCreatedBy(PretupsI.SYSTEM);
                            staffUserVO.setModifiedOn(currentDate);
                            staffUserVO.setStatus(PretupsI.USER_STATUS_ACTIVE);// Y
                            // Active
                            staffUserVO.setPreviousStatus(PretupsI.USER_STATUS_ACTIVE);// Y
                            // Active
                            staffUserVO.setUserProfileID(staffUserVO.getUserID());
                            staffUserVO.setUserNamePrefix("MR");

                            staffUserVO.setUserName(excelArr[r][1]);
                            userPhoneVO.setUserPhonesId(String.valueOf(IDGenerator.getNextID("PHONE_ID", TypesI.ALL)));
                            userPhoneVO.setUserId(staffUserVO.getUserID());
                            if (PretupsI.NOT_AVAILABLE.equalsIgnoreCase(excelArr[r][2])) {
                                userPhoneVO.setMsisdn(excelArr[r][2]);
                                userPhoneVO.setPrefixID(parentChannelUserVO.getUserPhoneVO().getPrefixID());
                                staffUserVO.setMsisdn("");
                            } else {
                                final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(excelArr[r][2]));
                                userPhoneVO.setPrefixID(prefixVO.getPrefixID());
                                userPhoneVO.setMsisdn(excelArr[r][2]);
                                staffUserVO.setMsisdn(excelArr[r][2]);
                            }

                            if (PretupsI.NOT_AVAILABLE.equalsIgnoreCase(excelArr[r][3])) {
                                userPhoneVO.setSmsPin(PretupsI.NOT_AVAILABLE);
                            } else {
                                userPhoneVO.setSmsPin(BTSLUtil.encryptText(excelArr[r][3]));
                            }

                            staffUserVO.setLoginID(excelArr[r][4]);
                            staffUserVO.setPassword(BTSLUtil.encryptText(excelArr[r][5]));
                            staffUserVO.setShortName(excelArr[r][6]);
                            staffUserVO.setPasswordModifyFlag(true);
                            staffUserVO.setEmpCode(excelArr[r][7]);
                            staffUserVO.setContactNo(excelArr[r][8]);
                            staffUserVO.setSsn(excelArr[r][9]);
                            staffUserVO.setDesignation(excelArr[r][10]);
                            staffUserVO.setAddress1(excelArr[r][11]);
                            staffUserVO.setAddress2(excelArr[r][12]);
                            staffUserVO.setCity(excelArr[r][13]);
                            staffUserVO.setState(excelArr[r][14]);
                            staffUserVO.setCountry(excelArr[r][15]);
                            staffUserVO.setEmail(excelArr[r][16]);
                            staffUserVO.setInSuspend("N");
                            staffUserVO.setOutSuspened("N");

                            userPhoneVO.setPhoneProfile(parentChannelUserVO.getUserPhoneVO().getPhoneProfile());
                            userPhoneVO.setPrimaryNumber(PretupsI.YES);
                            userPhoneVO.setPinModifiedOn(currentDate);
                            userPhoneVO.setPinModifyFlag(true);
                            userPhoneVO.setCreatedOn(currentDate);
                            userPhoneVO.setCreatedBy(PretupsI.SYSTEM);
                            userPhoneVO.setModifiedOn(currentDate);
                            userPhoneVO.setModifiedBy(PretupsI.SYSTEM);
                            userPhoneVO.setPinRequired("Y");
                            userPhoneVO.setCountry((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                            userPhoneVO.setPhoneLanguage((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)));
                            staffUserVO.setUserPhoneVO(userPhoneVO);
                            staffUserVO.setRecordNumber(String.valueOf(r + 1));
                        } else {
                            errorVO = new ListValueVO("", String.valueOf(r + 1), "Parent user =" + excelArr[r][0] + " not active");
                            fileErrorList.add(errorVO);
                            fileValidationErrorExists = true;
                            continue;
                        }

                    } else {
                        errorVO = new ListValueVO("", String.valueOf(r + 1), "Parent MSISDM=" + excelArr[r][0] + " not exist");
                        fileErrorList.add(errorVO);
                        fileValidationErrorExists = true;
                        continue;
                    }
                    userList.add(staffUserVO);

                } // for loop end
            } // if end
            else {
                errorVO = new ListValueVO("", "0", "File column not match");
                fileErrorList.add(errorVO);
                fileValidationErrorExists = true;

            }
            if (fileValidationErrorExists && !fileErrorList.isEmpty()) {
                final File newFile = new File(error1);
                out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
                for (int i = 0; i < fileErrorList.size(); i++) {
                    out.println("[ Record Number= " + fileErrorList.get(i).getOtherInfo() + " Error reason : " + fileErrorList.get(i).getOtherInfo2() + "]");
                }
                out.close();

            } else {
                fileErrorList = addBatchStaff(p_con, userList);
                if (fileErrorList != null && !fileErrorList.isEmpty()) {
                    final File newFile = new File(error2);
                    out = new PrintWriter(new BufferedWriter(new FileWriter(newFile)));
                    for (int i = 0; i < fileErrorList.size(); i++) {
                        out.println("[ Record Number= " + fileErrorList.get(i).getOtherInfo() + " Error reason : " + fileErrorList.get(i).getOtherInfo2() + "]");
                    }
                    out.close();

                }
                p_con.commit();
            }
        } catch (Exception e) {
            p_con.rollback();
            _log.error("processUploadedFile", "Exception:e=" + e);
            throw new BTSLBaseException(this, METHOD_NAME, "Exception in processing uploaded file");
        } finally {
        	try{
        		if(out!=null){
        			out.close();	
        		}
        	}catch(Exception e){
        		 _log.errorTrace("processUploadedFile", e);
        	}
            if (_log.isDebugEnabled()) {
                _log.debug("processUploadedFile", "Exiting:tslb userList=" + userList.size() + "  fileErrorList=" + fileErrorList.size());
            }
        }
    }

    private ArrayList<ListValueVO> addBatchStaff(Connection p_con, ArrayList<ChannelUserVO> p_userList) throws SQLException, BTSLBaseException {
        // insert data into users table
        // int userCount = userDAO.addUser(con,staffUserVO);
        // int userChannelCount =
        // channelUserDAO.addChannelUser(con,staffUserVO);
        // int phoneCount =
        // p_userDAO.addUserPhoneList(p_con,channelUserVO.getMsisdnList());
        // int geographyCount =
        // geographicalDAO.addUserGeographyList(p_con,geographyList);
        // int roleCount =
        // rolesDAO.addUserRolesList(p_con,channelUserVO.getUserID(),theForm.getRoleFlag());
        // int servicesCount =
        // servicesDAO.addUserServicesList(p_con,channelUserVO.getUserID(),theForm.getServicesTypes(),PretupsI.YES);
        final String METHOD_NAME = "addBatchStaff";
        PreparedStatement psmtUserInsert = null;
        PreparedStatement psmtChnlUserInsert = null;
        PreparedStatement psmtUserPhoneInsert = null;
        PreparedStatement psmtUserServicesInsert = null;
        PreparedStatement psmtUserRolesInsert = null;
        PreparedStatement psmtUserGeoInsert = null;
        PreparedStatement psmtRolesSelect = null;
        PreparedStatement psmtGroupRolesSelect=null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        int insertCount = 0;
        UserPhoneVO userPhoneVO = null;
        String recordNumgerStr = null;
        ListValueVO errorVO = null;

        final ArrayList<ListValueVO> errorList = new ArrayList<ListValueVO>();
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("INSERT INTO users (user_id,user_name,network_code,");
            strBuff.append("login_id,password,category_code,parent_id,");
            strBuff.append("owner_id,allowed_ip,allowed_days,");
            strBuff.append("from_time,to_time,employee_code,");
            strBuff.append("status,email,contact_no,");
            strBuff.append("designation,division,department,msisdn,user_type,");
            strBuff.append("created_by,created_on,modified_by,modified_on,address1, ");
            strBuff.append("address2,city,state,country,ssn,user_name_prefix, ");
            strBuff.append("external_code,short_name,appointment_date,previous_status,pswd_reset) ");
            strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            String insertQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("addBatchStaff", "Query sqlInsert:" + insertQuery);
            }

            psmtUserInsert = (PreparedStatement) p_con.prepareStatement(insertQuery);

            strBuff = new StringBuffer();
            strBuff.append("INSERT INTO channel_users (user_id,user_grade,");
            strBuff.append("contact_person,transfer_profile_id, comm_profile_set_id,");
            strBuff.append("in_suspend, out_suspend,outlet_code,suboutlet_code,activated_on, ");
            strBuff.append(" user_profile_id, mcommerce_service_allow, mpay_profile_id, low_bal_alert_allow )");
            strBuff.append(" VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            insertQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("addBatchStaff", "Query sqlInsert:" + insertQuery);
            }

            psmtChnlUserInsert = p_con.prepareStatement(insertQuery);

            strBuff = new StringBuffer();
            strBuff.append("INSERT INTO user_phones (user_phones_id,msisdn,user_id,");
            strBuff.append("description,primary_number,sms_pin,pin_required,");
            strBuff.append("phone_profile,phone_language,country,invalid_pin_count,");
            strBuff.append("last_transaction_status,last_transaction_on,pin_modified_on,");
            strBuff.append("created_by,created_on,modified_by,modified_on, last_transfer_id,");
            strBuff.append(" prefix_id,last_transfer_type,pin_reset) values ");
            strBuff.append("(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            insertQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("addBatchStaff", "Query sqlInsert:" + insertQuery);
            }
            psmtUserPhoneInsert = p_con.prepareStatement(insertQuery);

            strBuff = new StringBuffer();
            strBuff.append("INSERT INTO user_geographies (user_id,grph_domain_code) values (?,?)");

            insertQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("addBatchStaff", "Query sqlInsert:" + insertQuery);
            }
            psmtUserGeoInsert = p_con.prepareStatement(insertQuery);

            strBuff = new StringBuffer();
            strBuff.append("INSERT INTO user_services (user_id,service_type) values (?,?)");
            insertQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("addBatchStaff", "Query sqlInsert:" + insertQuery);
            }
            psmtUserServicesInsert = p_con.prepareStatement(insertQuery);

            strBuff = new StringBuffer();
            strBuff.append("INSERT INTO user_roles (user_id,role_code) values (?,?)");
            insertQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("addBatchStaff", "Query sqlInsert:" + insertQuery);
            }
            psmtUserRolesInsert = p_con.prepareStatement(insertQuery);

            strBuff = new StringBuffer();
            strBuff.append("select USER_ID, roles.ROLE_CODE,GROUP_ROLE from user_roles,roles where USER_ID=? and user_roles.role_code=roles.role_code");
            insertQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("addBatchStaff", "Query sqlInsert:" + insertQuery);
            }
            psmtRolesSelect = p_con.prepareStatement(insertQuery);
            strBuff = new StringBuffer();
            strBuff.append("select ROLE_CODE from GROUP_ROLES where GROUP_ROLE_CODE=?");
            final String groupRoleQuery = strBuff.toString();
            if (_log.isDebugEnabled()) {
                _log.debug("addBatchStaff", "Query groupRoleQuery :" + insertQuery);
            }
            psmtGroupRolesSelect = p_con.prepareStatement(groupRoleQuery);
            ArrayList list = null;
            for (final ChannelUserVO channelUserVO : p_userList) {
                try {
                    recordNumgerStr = channelUserVO.getRecordNumber();
                    // /Users insert
                    psmtUserInsert.setString(1, channelUserVO.getUserID());
                    // psmtUserInsert.setFormOfUse(2,
                    // PreparedStatement.FORM_NCHAR);
                    psmtUserInsert.setString(2, channelUserVO.getUserName());
                    psmtUserInsert.setString(3, channelUserVO.getNetworkID());
                    psmtUserInsert.setString(4, channelUserVO.getLoginID());
                    psmtUserInsert.setString(5, channelUserVO.getPassword());
                    psmtUserInsert.setString(6, channelUserVO.getCategoryCode());
                    psmtUserInsert.setString(7, channelUserVO.getParentID());
                    psmtUserInsert.setString(8, channelUserVO.getOwnerID());
                    psmtUserInsert.setString(9, channelUserVO.getAllowedIps());
                    psmtUserInsert.setString(10, channelUserVO.getAllowedDays());
                    psmtUserInsert.setString(11, channelUserVO.getFromTime());
                    psmtUserInsert.setString(12, channelUserVO.getToTime());
                    psmtUserInsert.setString(13, channelUserVO.getEmpCode());
                    psmtUserInsert.setString(14, channelUserVO.getStatus());
                    psmtUserInsert.setString(15, channelUserVO.getEmail());
                    psmtUserInsert.setString(16, channelUserVO.getContactNo());
                    // psmtUserInsert.setFormOfUse(17,
                    // PreparedStatement.FORM_NCHAR);
                    psmtUserInsert.setString(17, channelUserVO.getDesignation());
                    psmtUserInsert.setString(18, channelUserVO.getDivisionCode());
                    psmtUserInsert.setString(19, channelUserVO.getDepartmentCode());
                    psmtUserInsert.setString(20, channelUserVO.getMsisdn());
                    psmtUserInsert.setString(21, channelUserVO.getUserType());
                    psmtUserInsert.setString(22, channelUserVO.getCreatedBy());
                    psmtUserInsert.setTimestamp(23, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getCreatedOn()));
                    psmtUserInsert.setString(24, channelUserVO.getModifiedBy());
                    psmtUserInsert.setTimestamp(25, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getModifiedOn()));
                    // psmtUserInsert.setFormOfUse(26,
                    // PreparedStatement.FORM_NCHAR);
                    psmtUserInsert.setString(26, channelUserVO.getAddress1());
                    // psmtUserInsert.setFormOfUse(27,
                    // PreparedStatement.FORM_NCHAR);
                    psmtUserInsert.setString(27, channelUserVO.getAddress2());
                    // psmtUserInsert.setFormOfUse(28,
                    // PreparedStatement.FORM_NCHAR);
                    psmtUserInsert.setString(28, channelUserVO.getCity());
                    // psmtUserInsert.setFormOfUse(29,
                    // PreparedStatement.FORM_NCHAR);
                    psmtUserInsert.setString(29, channelUserVO.getState());
                    // psmtUserInsert.setFormOfUse(30,
                    // PreparedStatement.FORM_NCHAR);
                    psmtUserInsert.setString(30, channelUserVO.getCountry());
                    psmtUserInsert.setString(31, channelUserVO.getSsn());
                    // psmtUserInsert.setFormOfUse(32,
                    // PreparedStatement.FORM_NCHAR);
                    psmtUserInsert.setString(32, channelUserVO.getUserNamePrefix());
                    psmtUserInsert.setString(33, channelUserVO.getExternalCode());
                    // psmtUserInsert.setFormOfUse(34,
                    // PreparedStatement.FORM_NCHAR);
                    psmtUserInsert.setString(34, channelUserVO.getShortName());
                    // psmtUserInsert.setString(35,
                    // channelUserVO.getUserCode());

                    if (channelUserVO.getAppointmentDate() != null) {
                        psmtUserInsert.setTimestamp(35, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getAppointmentDate()));
                    } else {
                        psmtUserInsert.setTimestamp(35, null);
                    }
                    psmtUserInsert.setString(36, channelUserVO.getPreviousStatus());
                    psmtUserInsert.setString(37, PretupsI.YES);
                    insertCount = psmtUserInsert.executeUpdate();
                    psmtUserInsert.clearParameters();
                    if (insertCount <= 0) {
                        errorVO = new ListValueVO("", recordNumgerStr, "Not insert to Users");
                        errorList.add(errorVO);
                    }

                    insertCount = 0;
                    // /Channel Users insert
                    psmtChnlUserInsert.setString(1, channelUserVO.getUserID());
                    psmtChnlUserInsert.setString(2, channelUserVO.getUserGrade());
                    psmtChnlUserInsert.setString(3, channelUserVO.getContactPerson());
                    psmtChnlUserInsert.setString(4, channelUserVO.getTransferProfileID());
                    psmtChnlUserInsert.setString(5, channelUserVO.getCommissionProfileSetID());
                    psmtChnlUserInsert.setString(6, channelUserVO.getInSuspend());
                    psmtChnlUserInsert.setString(7, channelUserVO.getOutSuspened());
                    psmtChnlUserInsert.setString(8, channelUserVO.getOutletCode());
                    psmtChnlUserInsert.setString(9, channelUserVO.getSubOutletCode());
                    if (channelUserVO.getActivatedOn() != null) {
                        psmtChnlUserInsert.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(channelUserVO.getActivatedOn()));
                    } else {
                        psmtChnlUserInsert.setTimestamp(10, null);
                    }

                    psmtChnlUserInsert.setString(11, channelUserVO.getUserProfileID());
                    psmtChnlUserInsert.setString(12, PretupsI.NO);
                    psmtChnlUserInsert.setString(13, channelUserVO.getMpayProfileID());
                    psmtChnlUserInsert.setString(14, PretupsI.NO);

                    insertCount = psmtChnlUserInsert.executeUpdate();
                    psmtChnlUserInsert.clearParameters();
                    if (insertCount <= 0) {
                        errorVO = new ListValueVO("", recordNumgerStr, "Not insert  to Channel users");
                        errorList.add(errorVO);
                    }

                    // User phones insert
                    insertCount = 0;
                    userPhoneVO = channelUserVO.getUserPhoneVO();
                    psmtUserPhoneInsert.setString(1, userPhoneVO.getUserPhonesId());
                    psmtUserPhoneInsert.setString(2, userPhoneVO.getMsisdn());
                    psmtUserPhoneInsert.setString(3, userPhoneVO.getUserId());
                    psmtUserPhoneInsert.setString(4, userPhoneVO.getDescription());
                    psmtUserPhoneInsert.setString(5, userPhoneVO.getPrimaryNumber());
                    psmtUserPhoneInsert.setString(6, userPhoneVO.getSmsPin());
                    psmtUserPhoneInsert.setString(7, userPhoneVO.getPinRequired());
                    psmtUserPhoneInsert.setString(8, userPhoneVO.getPhoneProfile());
                    psmtUserPhoneInsert.setString(9, userPhoneVO.getPhoneLanguage());
                    psmtUserPhoneInsert.setString(10, userPhoneVO.getCountry());
                    psmtUserPhoneInsert.setInt(11, userPhoneVO.getInvalidPinCount());
                    psmtUserPhoneInsert.setString(12, userPhoneVO.getLastTransactionStatus());
                    if (userPhoneVO.getLastTransactionOn() != null) {
                        psmtUserPhoneInsert.setTimestamp(13, BTSLUtil.getTimestampFromUtilDate(userPhoneVO.getLastTransactionOn()));
                    } else {
                        psmtUserPhoneInsert.setTimestamp(13, null);
                    }
                    psmtUserPhoneInsert.setTimestamp(14, BTSLUtil.getTimestampFromUtilDate(userPhoneVO.getPinModifiedOn()));
                    psmtUserPhoneInsert.setString(15, userPhoneVO.getCreatedBy());
                    psmtUserPhoneInsert.setTimestamp(16, BTSLUtil.getTimestampFromUtilDate(userPhoneVO.getCreatedOn()));
                    psmtUserPhoneInsert.setString(17, userPhoneVO.getModifiedBy());
                    psmtUserPhoneInsert.setTimestamp(18, BTSLUtil.getTimestampFromUtilDate(userPhoneVO.getModifiedOn()));
                    psmtUserPhoneInsert.setString(19, userPhoneVO.getLastTransferID());
                    psmtUserPhoneInsert.setLong(20, userPhoneVO.getPrefixID());
                    psmtUserPhoneInsert.setString(21, userPhoneVO.getLastTransferType());
                    psmtUserPhoneInsert.setString(22, PretupsI.YES);

                    insertCount = psmtUserPhoneInsert.executeUpdate();
                    psmtUserPhoneInsert.clearParameters();
                    if (insertCount <= 0) {
                        errorVO = new ListValueVO("", recordNumgerStr, "Not insert  to User phones");
                        errorList.add(errorVO);
                    }

                    // insert user geography
                    insertCount = 0;
                    psmtUserGeoInsert.setString(1, channelUserVO.getUserID());
                    psmtUserGeoInsert.setString(2, channelUserVO.getGeographicalCode());
                    insertCount = psmtUserGeoInsert.executeUpdate();
                    psmtUserGeoInsert.clearParameters();
                    if (insertCount <= 0) {
                        errorVO = new ListValueVO("", recordNumgerStr, "Not insert  to User geography");
                        errorList.add(errorVO);
                    }

                    // insert user services

                    list = channelUserVO.getAssociatedServiceTypeList();
                    for (int i = 0, j = list.size(); i < j; i++) {
                        insertCount = 0;
                        errorVO = (ListValueVO) list.get(i);
                        psmtUserServicesInsert.setString(1, channelUserVO.getUserID());
                        psmtUserServicesInsert.setString(2, errorVO.getValue());
                        insertCount = psmtUserServicesInsert.executeUpdate();
                        psmtUserServicesInsert.clearParameters();

                        if (insertCount <= 0) {
                            errorVO = new ListValueVO("", recordNumgerStr, "Error to User servises=" + errorVO.getValue());
                            errorList.add(errorVO);
                        }
                    }
                    psmtRolesSelect.setString(1, channelUserVO.getParentID());
                    rs = psmtRolesSelect.executeQuery();
                    boolean groupRole = false;
                    String roleCode = null;
                    while (rs.next()) {
                        if ("Y".equals(rs.getString("GROUP_ROLE"))) {
                            groupRole = true;
                            roleCode = rs.getString("role_code");
                            break;
                        }
                        psmtUserRolesInsert.setString(1, channelUserVO.getUserID());
                        psmtUserRolesInsert.setString(2, rs.getString("role_code"));
                        insertCount = psmtUserRolesInsert.executeUpdate();
                        psmtUserRolesInsert.clearParameters();

                        if (insertCount <= 0) {
                            errorVO = new ListValueVO("", recordNumgerStr, "Error to User roles=");
                            errorList.add(errorVO);
                        }
                    }
                    if (groupRole) {
                        psmtGroupRolesSelect.setString(1, roleCode);
                        rs1 = null;
                        rs1= psmtGroupRolesSelect.executeQuery();
                        while (rs1.next()) {
                            psmtUserRolesInsert.setString(1, channelUserVO.getUserID());
                            psmtUserRolesInsert.setString(2, rs1.getString("role_code"));
                            insertCount = psmtUserRolesInsert.executeUpdate();
                            psmtUserRolesInsert.clearParameters();

                            if (insertCount <= 0) {
                                errorVO = new ListValueVO("", recordNumgerStr, "Error to User roles=");
                                errorList.add(errorVO);
                            }
                        }
                    }
                    p_con.commit();
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                    errorVO = new ListValueVO("", recordNumgerStr, "Exception =" + e.getMessage());
                    errorList.add(errorVO);
                    p_con.rollback();
                }

            } // for loop
        } // end of try
        catch (SQLException sqle) {
            _log.error("addBatchStaff", "SQLException: " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[addBatchStaff]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(sqle);
        } // end of catch
        catch (Exception e) {
            _log.error("addBatchStaff", "Exception: " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserDAO[addBatchStaff]", "", "", "", "Exception:" + e
                .getMessage());
            throw new BTSLBaseException(e);
        } // end of catch
        finally {
        	try {
                if (rs != null) {
                	rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
        	try {
                if (rs1 != null) {
                	rs1.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
        	try {
                if (psmtChnlUserInsert != null) {
                	psmtChnlUserInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
        	try {
                if (psmtGroupRolesSelect != null) {
                	psmtGroupRolesSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtUserInsert != null) {
                    psmtUserInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtUserInsert != null) {
                    psmtUserInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtUserPhoneInsert != null) {
                    psmtUserPhoneInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtUserServicesInsert != null) {
                    psmtUserServicesInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtUserRolesInsert != null) {
                    psmtUserRolesInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtUserGeoInsert != null) {
                    psmtUserGeoInsert.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmtRolesSelect != null) {
                    psmtRolesSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("addBatchStaff", "Exiting: insertCount=" + insertCount);
            }

        } // end of finally
        return errorList;
    }

    /**
     * Method to generate the userId while inserting new record
     * 
     * @param p_networkCode
     *            String
     * @param p_prefix
     *            String
     * @return String
     */
    public String generateUserId(String p_networkCode, String p_prefix) throws Exception {
        if (_log.isDebugEnabled()) {
            _log.debug("generateUserId", "Entered p_networkCode=" + p_networkCode + " p_prefix=" + p_prefix);
        }
        final int length = Integer.parseInt(Constants.getProperty("USER_PADDING_LENGTH"));
        String id = BTSLUtil.padZeroesToLeft((IDGenerator.getNextID(TypesI.USERID, TypesI.ALL, p_networkCode)) + "", length);

        // id =
        // p_networkCode+Constants.getProperty("SEPARATOR_FORWARD_SLASH")+p_prefix+id;
        id = p_networkCode + p_prefix + id;
        if (_log.isDebugEnabled()) {
            _log.debug("generateUserId", "Exiting id=" + id);
        }
        return id;
    }

}

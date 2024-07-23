package com.btsl.purging;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.SqlParameterEncoder;
import com.ibm.icu.util.Calendar;

/**
 * @(#)ArchivePretupsUtil
 *                        Copyright(c) 2004, Bharti Telesoft Ltd.
 *                        All Rights Reserved
 *                        Used for Logging Information
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Author Date History
 *                        ------------------------------------------------------
 *                        -------------------------------------------
 *                        Ankit Singhal 03/Nov/2005 Initial Creation
 *                        ------------------------------------------------------
 *                        ------------------------------------------
 * 
 **/
public class ArchivePretupsUtil {

    private static String _FILEFOOTER = "ENDOFFILE=";
    private static String _ARCHIVEFILETYPE = "ARC";
    private static int _FILEFOOTERLENGTH = 50;
    private static Log _logger = LogFactory.getLog(ArchivePretupsUtil.class.getName());

    /**
	 * to ensure no class instantiation 
	 */
    private ArchivePretupsUtil() {
        
    }

    /**
     * @param p_dbUser
     * @param p_dbPass
     * @return
     * @throws Exception
     */
    public static boolean checkUserPass(String p_dbUser, String p_dbPass){
        final String methodName = "checkUserPass";
        String dbUser = null;
        String dbPass = null;
        String userNameInFile = null;
        String userPassInFile = null;
        try {
            userNameInFile = Constants.getProperty("userid");
            userPassInFile = Constants.getProperty("passwd");
            dbUser = BTSLUtil.encryptText(p_dbUser);
            dbPass = BTSLUtil.encryptText(p_dbPass);
            if ((dbUser.equalsIgnoreCase(userNameInFile) && dbPass.equalsIgnoreCase(userPassInFile))) {
                return true;
            }
            return false;
        } catch (Exception e) {
            _logger.errorTrace(methodName, e);
            return false;
        }
    }

    /**
     * @param displayString
     * @return
     */
    public static String dataFromUser(String displayString) {
        String data = "";
        boolean flag = true;
        while (flag) {

            data = getUserInputFromConsole();
            if (!BTSLUtil.isNullString(data)) {
                flag = false;
            }
        }
        return data;
    }

    /**
     * @param p_isDelete
     * @return
     */
    public static String[] intractiveParameterCheck() {
        final String methodName = "intractiveParameterCheck";
        if (_logger.isDebugEnabled()) {
            _logger.debug("", "intractiveParameterCheck Entered");
        }
        String userInput[] = new String[10];
        boolean flag = true;
        Date userEnterToDate = null;
        Date userEnterFromDate = null;

        userInput[0] = dataFromUser(" Enter User Name = ").trim();// user name
        userInput[1] = dataFromUser(" Enter User Password = ").trim();// passwoed
        boolean isValid;
        try {
            isValid = checkUserPass(userInput[0], userInput[1]);
        } catch (Exception e1) {
            isValid = false;
            _logger.errorTrace(methodName, e1);
        }
        if (!isValid) {
            _logger.info(methodName, " User Name/Password is wrong (Returning) ");
            return null;
        }
        flag = true;
        while (flag) {
            userInput[2] = dataFromUser(" Network = ");// network
            if (!"ALL".equalsIgnoreCase(userInput[2])) {
                _logger.info(methodName, " Network_Code (Supported Value 'ALL' for Archive) ");
            } else {
                flag = false;
            }
        }

        flag = true;
        boolean isValidDate = true;
        while (isValidDate) {
            int mon = 0;
            while (flag) {
                userInput[3] = dataFromUser(" Month = ");// month
                try {
                    mon = Integer.parseInt(userInput[3]);
                    if (mon > 12 || mon < 1 || userInput[3].length() != 2) {
                        _logger.info(methodName, " Month (Supported Values 01-12 : Syntax 'MM') ");
                    } else {
                        flag = false;
                    }
                } catch (Exception e) {
                    _logger.info(methodName, " Month (Supported Values 01-12 : Syntax 'MM') ");
                    _logger.errorTrace(methodName, e);
                }
            }
            flag = true;
            int year = 0;
            while (flag) {
                userInput[4] = dataFromUser(" Year = ");// year
                try {
                    year = Integer.parseInt(userInput[4]);
                    if (year < 2000 || userInput[4].length() != 4) {
                        _logger.info(methodName, " Year Format YYYY and should be greater than 2000 ");
                    } else {
                        flag = false;
                    }
                } catch (Exception e) {
                    _logger.info(methodName, " Year Format YYYY and should be greater than 2000 ");
                    _logger.errorTrace(methodName, e);
                }
            }
            flag = false;
            while (!flag) {
                userInput[5] = dataFromUser(" From Date = ");// From date
                try {
                    userEnterFromDate = BTSLUtil.getDateFromDateString(userInput[5]);
                    flag = true;
                } catch (Exception e) {
                    flag = false;
                    _logger.errorTrace(methodName, e);
                }
                if (!flag) {
                    _logger.info(methodName, " Date Format Should be DD/MM/YY ");
                }
            }
            flag = false;
            while (!flag) {
                userInput[6] = dataFromUser(" To Date = ");// To date
                try {
                    userEnterToDate = BTSLUtil.getDateFromDateString(userInput[6]);
                    flag = true;
                } catch (Exception e) {
                    flag = false;
                    _logger.errorTrace(methodName, e);
                }
                if (!flag) {
                    _logger.info(methodName, " Date Format Should be DD/MM/YY ");
                }
            }

            try {
                userEnterToDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate((userEnterToDate)));
            } catch (ParseException e) {
                _logger.errorTrace(methodName, e);
            }
            try {
                userEnterFromDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(userEnterFromDate));
            } catch (ParseException e1) {
                _logger.errorTrace(methodName, e1);
            }

            // To date should be after From date
            if (userEnterToDate.after(userEnterFromDate)) {
                // From and To date difference should be less than 30
                if (BTSLUtil.getDifferenceInUtilDates(userEnterFromDate, userEnterToDate) <= 30) {
                    // All the three months should be same
                    if (userInput[5].substring(3, 5).equals(userInput[6].substring(3, 5)) && userInput[5].substring(3, 5).equals(userInput[3])) {
                        // All the three years should be same
                        if (userInput[5].substring(6).equals(userInput[6].substring(6)) && userInput[5].substring(6).equals(userInput[4].substring(2))) {
                            isValidDate = false;
                        } else {
                            _logger.info(methodName, "Year in the From and To date should be same as Year enteed. Please enter date values again");
                        }
                    } else {
                        _logger.info(methodName, "Month in the From and To date should be same as Month entered. Please enter date values again");
                    }
                } else {
                    _logger.info(methodName, "Date difference cannot be greater than 30 days. Please enter date values again");
                }
            } else {
                _logger.info(methodName, " From Date Can't be greater than To Date. Please Enter date values again");
            }
        }

        flag = true;
        return userInput;
    }

    /**
     * @param args
     */
    public static void displayedUserInfo(String args[]) {
        int i = 0;
        final String methodName = "displayedUserInfo";
        _logger.info(methodName, " User Information Starts.........................");
        _logger.debug(methodName, " User Name    = " + args[i++]);
        i++;// used to avoid password
        _logger.debug(methodName, " Network Code = " + args[i++]);
        _logger.debug(methodName, " Month        = " + args[i++]);
        _logger.debug(methodName, " Year         = " + args[i++]);
        _logger.debug(methodName, " From Date    = " + args[i++]);
        _logger.debug(methodName, " To Date      = " + args[i++]);
        _logger.info(methodName, " User Information Ends.........................");
    }

    /**
     * @param fileNameLike
     * @param path
     * @return
     */
    public static boolean isFileExists(String fileNameLike, String path) {
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
        String rough = BTSLUtil.NullToString(fileNameLike).trim();
        for (int i = 0; i < directoryList.length; i++) {
            if (directoryList[i].indexOf(rough) != -1) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * @param p_directoyPath
     * @param p_year
     * @param p_month
     * @param p_fromday
     * @param p_toDay
     * @return
     */

    public static String createMasterDirectory(String p_directoyPath, String p_year, String p_month, String p_fromday, String p_toDay) {
        String month = "";
        String year = "";
        if (month.length() == 1) {
            month = "0" + p_month;
        } else {
            month = "" + p_month;
        }
        int length = p_year.length();
        if (length < 4) {
            if (length == 1) {
                year = "0" + p_year;
            } else {
                year = "" + p_year;
            }
        } else {
            year = "" + p_year.substring(length - 2);
        }
        File parentDir = new File(p_directoyPath);
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        String dirName = p_directoyPath + File.separator + year + "_" + month + "_" + p_fromday + "_" + p_toDay;
        File newDir = new File(dirName);
        if (!newDir.exists()) {
            newDir.mkdirs();
        }
        return dirName;
    }

    /**
     * @param year
     * @param month
     * @param separator
     * @return
     */
    public static String partialFileName(String year, String month, String separator) {
        String fileName = "";
        SimpleDateFormat sdf = null;
        Calendar cal = BTSLDateUtil.getInstance();
        sdf = new SimpleDateFormat(separator + "yy" + separator + "MM" + separator);
        cal.set(Integer.parseInt(year), Integer.parseInt(month) - 1, 1);
        fileName = sdf.format(cal.getTime());
        return fileName;
    }

    /**
     * @return
     */
    public static String getUserInputFromConsole() {
        final String methodName = "getUserInputFromConsole";
        String line = "";
        InputStreamReader stdin = new InputStreamReader(System.in);
        BufferedReader console = null;
        try {
        	console = new BufferedReader(stdin);
            line = console.readLine();
        } catch (IOException ioex) {

            _logger.errorTrace(methodName, ioex);

        }finally {
        	try {
                if (console != null) {
                	console.close();
                }
            } catch (Exception e1) {
            	_logger.errorTrace(methodName, e1);
            }
        }
        return line;
    }

    /**
     * This method loads the network list present in the system
     * 
     * @param p_con
     * @param p_locType
     *            String
     * @param p_networkCode
     *            String
     * @return ArrayList
     * @throws Exception
     */
    public static ArrayList getNetworks(Connection p_con, String p_locType) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String network = null;
        java.util.ArrayList networkList = null;
        StringBuilder strBuff = new StringBuilder(" SELECT network_code,network_name ");
        strBuff.append(" FROM networks  WHERE network_type=case ? when ? then network_type else ? end");
        strBuff.append(" AND status<>'N'");

        final String methodName = "getNetworks";
        try {
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, " Query :: " + strBuff.toString());
            }
            pstmt = p_con.prepareStatement(strBuff.toString());
            pstmt.setString(1, p_locType);
            pstmt.setString(2, PretupsI.ALL);
            pstmt.setString(3, p_locType);
            rs = pstmt.executeQuery();
            if (rs != null) {
                networkList = new java.util.ArrayList();
            }
            while (rs.next()) {
                network = SqlParameterEncoder.encodeParams(rs.getString("network_code")) + "#" + 
                		  SqlParameterEncoder.encodeParams(rs.getString("network_name"));
                networkList.add(network);
            }
            return networkList;
        } catch (Exception ex) {
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, " Exception : " + ex.getMessage());
            }
            _logger.errorTrace(methodName, ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(methodName, " Exception Closing RS : " + ex.getMessage());
                }
                _logger.errorTrace(methodName, ex);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(methodName, " Exception Closing Prepared Stmt: " + ex.getMessage());
                }
                _logger.errorTrace(methodName, ex);
            }
        }
		return networkList;
    }

    /**
     * This method is used to constuct file header
     * 
     * @param tableName
     *            String
     * @param fromDate
     *            Date
     * @param toDate
     *            Date
     * @param label
     *            String
     * @param no
     *            long
     * @return String
     */
    public static String constructFileHeader(String tableName, Date fromDate, Date toDate, String label, long fileNo) {
        SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT_DDMMYYYY);
        StringBuilder fileHeaderBuf = new StringBuilder();
        fileHeaderBuf.append(tableName);
        fileHeaderBuf.append("\n");
        fileHeaderBuf.append(" Present Date=" + BTSLDateUtil.getSystemLocaleDate(sdf.format(new Date())));
        fileHeaderBuf.append("\n");
        fileHeaderBuf.append(" From Date   =" + BTSLDateUtil.getSystemLocaleDate(sdf.format(fromDate)));
        fileHeaderBuf.append("\n");
        fileHeaderBuf.append(" To Date     =" + BTSLDateUtil.getSystemLocaleDate(sdf.format(toDate)));
        fileHeaderBuf.append("\n");
        fileHeaderBuf.append(" File Type = Archive ");
        fileHeaderBuf.append("\n");
        fileHeaderBuf.append(" File Number =" + fileNo);
        fileHeaderBuf.append("\n");
        fileHeaderBuf.append(label);
        fileHeaderBuf.append("\n");
        return fileHeaderBuf.toString();
    }

    /**
     * This method is used to constuct file footer
     * 
     * @param noOFRecords
     *            long
     * @return String
     */
    public static String constructFileFooter(long noOfRecords, boolean isLog) {

        StringBuilder fileHeaderBuf = null;
        if (isLog) {
            fileHeaderBuf = new StringBuilder(" ");
        } else {
            fileHeaderBuf = new StringBuilder("\n");
        }
        fileHeaderBuf.append(formatter(_FILEFOOTER + noOfRecords, _FILEFOOTERLENGTH));
        return fileHeaderBuf.toString();
    }

    /**
     * This method is used format String
     * 
     * @param s
     *            String
     * @param value
     *            int
     * @return String
     */

    public static String constructFileName(String fileStartName, int year, int month, String networkCode, int counter, String ext, String separator) {
        String fileName = "";
        SimpleDateFormat sdf = null;
        Calendar cal = BTSLDateUtil.getInstance();
        sdf = new SimpleDateFormat(separator + "yy" + separator + "MM" + separator);
        cal.set(year, month - 1, 1);
        fileName = fileStartName + sdf.format(cal.getTime()) + _ARCHIVEFILETYPE + separator + networkCode + separator + counter + ext;
        return fileName;
    }

    /**
     * @param s
     * @param value
     * @return
     */
    public static String formatter(String s, int value) {
        String newValue = BTSLUtil.NullToString(s);
        String retuned = "";
        int length = newValue.length();
        if (length < value) {
            for (int i = 0; i < value - length; i++) {
                retuned += " ";
            }
            retuned += newValue;
        } else {
            retuned = newValue;
        }
        return retuned;
    }

    public static String formatter(String str) {
        return str;
    }

    /**
     * This method converts input date to X no of months before
     * ex. 15/8/04 -4 --> 31/3/04
     * ex 30/8/04 -4 --> 30/4/04
     * 
     * @param presentDate
     *            Date
     * @param months
     *            int
     * @return Date
     */

    public static Date constructXBeforeDate(Date p_presentDate, int months) {
        Date presentDate = p_presentDate;

        Calendar calendar = BTSLDateUtil.getInstance();
        Calendar calendarRef = BTSLDateUtil.getInstance();
        calendar.setTime(presentDate); // Setting the calander instance with the
                                       // From date
        int day = calendar.get(Calendar.DATE);// getting day of from date
        int month = calendar.get(Calendar.MONTH);// getting month of from date
        int year = calendar.get(Calendar.YEAR);// getting year of from date
        if (months >= month) {
            calendarRef.set(Calendar.YEAR, year - 1);
            calendarRef.set(Calendar.MONTH, month + 12 - months);
            calendarRef.set(Calendar.DATE, day);
        } else {
            calendarRef.set(Calendar.YEAR, year);
            calendarRef.set(Calendar.MONTH, month - months);
            calendarRef.set(Calendar.DATE, day);
        }
        
        Date dateToReturn = calendarRef.getTime();
        return dateToReturn;
    }

    /**
     * This method converts input date to one month after date
     * ex. 15/2/04--> 31/3/04
     * ex 29/3/04-->30/4/04
     * 
     * @param archivedDate
     *            Date
     * @return Date
     */
    public static Date constructOneMonthAfterDate(Date archivedDate) {
        Date presentDate = archivedDate;
        Calendar calendar = BTSLDateUtil.getInstance();
        Calendar calendarRef = BTSLDateUtil.getInstance();
        calendar.setTime(presentDate); // Setting the calander instance with the
                                       // From date
        int month = 0;
        if (calendar.get(Calendar.DAY_OF_MONTH) != calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
            month = calendar.get(Calendar.MONTH) - 1;// getting month of from
                                                     // date
        } else {
            month = calendar.get(Calendar.MONTH);// getting month of from date
        }
        int year = calendar.get(Calendar.YEAR);// getting year of from date
        if (month >= 11) {
            calendarRef.set(Calendar.YEAR, year + 1);
            calendarRef.set(Calendar.MONTH, 0);
            calendarRef.set(Calendar.DATE, 31);
        } else {
            calendarRef.set(Calendar.YEAR, year);
            calendarRef.set(Calendar.MONTH, month + 1);
            int maxDays = calendarRef.getActualMaximum(Calendar.DAY_OF_MONTH);
            calendarRef.set(Calendar.DATE, maxDays);
        }
        Date dateToReturn = calendarRef.getTime();
        return dateToReturn;
    }

    /**
     * @param date
     * @return
     */
    public static int getDay(Date date) {
        int day = 0;
        Calendar cal = BTSLDateUtil.getInstance();
        cal.setTime(date);
        day = cal.get(Calendar.DATE);
        return day;
    }

    public static void main(String args[]) {
    }

    /**
     * @param fromTime
     * @param toTime
     * @return
     */
    public static boolean archivalTime(String fromTime, String toTime) {
        final String methodName = "archivalTime";
        Calendar cal = BTSLDateUtil.getInstance();
        int presentHour = cal.get(Calendar.HOUR_OF_DAY);
        int presentMin = cal.get(Calendar.MINUTE);
        boolean isTrue = false;
        int toHour = 0;
        int toMin = 0;
        int fromHour = 0;
        int fromMin = 0;
        try {
            toHour = Integer.parseInt(toTime.substring(0, 2));
            toMin = Integer.parseInt(toTime.substring(2, 4));
            fromHour = Integer.parseInt(fromTime.substring(0, 2));
            fromMin = Integer.parseInt(fromTime.substring(2, 4));
        } catch (Exception e) {
            toHour = 3;
            toMin = 0;
            fromHour = 0;
            fromMin = 0;
            _logger.errorTrace(methodName, e);
        }
        if (fromHour > presentHour) {
            return isTrue;
        } else if (fromHour == presentHour) {
            if (fromMin > presentMin) {
                return isTrue;
            }
        }

        if (toHour < presentHour) {
            return isTrue;
        } else if (toHour == presentHour) {
            if (toMin < presentMin) {
                return isTrue;
            }
        }
        return true;
    }

    public static Timestamp dateStringToTimestamp(String date) {
        java.sql.Timestamp ts = null;
        if (date != null && date.length() > 0) {
            java.text.SimpleDateFormat dtFormat = new java.text.SimpleDateFormat(PretupsI.TIMESTAMP_DATESPACEHHMMSS);
            Date dt = dtFormat.parse(date, new java.text.ParsePosition(0));
            ts = new java.sql.Timestamp(dt.getTime());
        }
        return ts;
    }

    public static ArrayList getArchiveDoneNetworks(Connection p_con, String p_table1, String p_table2) throws Exception {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        java.util.ArrayList networkList = null;

        String strBuff = new String("SELECT network_code from archival_done_date WHERE table_name=? or table_name=?");
        final String methodName = "getArchiveDoneNetworks";
        try {
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, " Query :: " + strBuff.toString());
            }
            pstmt = p_con.prepareStatement(strBuff);
            pstmt.setString(1, p_table1);
            pstmt.setString(2, p_table2);
            rs = pstmt.executeQuery();
            if (rs != null) {
                networkList = new java.util.ArrayList();
            }
            while (rs.next()) {
                networkList.add(rs.getString("network_code"));
            }
            return networkList;
        } catch (Exception ex) {
            if (_logger.isDebugEnabled()) {
                _logger.debug(methodName, " Exception : " + ex.getMessage());
            }
            _logger.errorTrace(methodName, ex);
            throw new BTSLBaseException(ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(methodName, " Exception Closing RS : " + ex.getMessage());
                }
                _logger.errorTrace(methodName, ex);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ex) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(methodName, " Exception Closing Prepared Stmt: " + ex.getMessage());
                }
                _logger.errorTrace(methodName, ex);
            }
        }
    }

    /**
     * @param fileNameList
     *            Arraylist
     */
    public static void deleteAllFile(ArrayList fileNameList) {
        final String methodName = "deleteAllFile";
        int size = 0;
        if (fileNameList != null) {
            size = fileNameList.size();
        }
        if (_logger.isDebugEnabled()) {
            _logger.debug("", "deleteAllFile :: Entered .........Size of Files to be deleted " + size);
        }
        String fileName = null;
        File newFile = null;
        for (int i = 0; i < size; i++) {
            try {
                fileName = (String) fileNameList.get(i);
                newFile = new File(fileName);
                boolean isDeleted = newFile.delete();
                if(isDeleted){
                	_logger.debug(methodName, "File deleted successfully");
                }
                if (_logger.isDebugEnabled()) {
                    _logger.debug("", fileName + " file deleted");
                }
            } catch (Exception e) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("", fileName + " " + e);
                }
                _logger.errorTrace(methodName, e);
            }
        }// end of for loop
        if (_logger.isDebugEnabled()) {
            _logger.debug("", "deleteAllFile :: Exiting.............................");
        }
    }
}
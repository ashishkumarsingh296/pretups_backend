/*
 * COPYRIGHT: Mahindra Comviva Technologies Pvt. Ltd.
 * 
 * This software is the sole property of Comviva and is protected
 * by copyright law and international treaty provisions. Unauthorized
 * reproduction or redistribution of this program, or any portion of
 * it may result in severe civil and criminal penalties and will be
 * prosecuted to the maximum extent possible under the law.
 * Comviva reserves all rights not expressly granted. You may not
 * reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted
 * by applicable law notwithstanding this limitation.
 * 
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE. YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY
 * AND THE USE OF THIS SOFTWARE. Comviva SHALL NOT BE LIABLE FOR
 * ANY DAMAGES WHATSOEVER ARISING OUT OF THE USE OF OR INABILITY TO
 * USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.btsl.pretups.processes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.btsl.util.SqlParameterEncoder;
import com.ibm.icu.util.Calendar;

/**
 * @author mallesham.k
 * 
 */
public class MonthlyRedemptionProcess {
    private static Log _logger = LogFactory.getLog(MonthlyRedemptionProcess.class.getName());
    private static ProcessStatusVO _processStatusVO;
    private static ProcessBL _processBL = null;
    private static OperatorUtilI calculatorI = null;
    private static Properties _monthlyProperties = new Properties();
    private static HttpURLConnection _urlConnection = null;
    private static HttpURLConnection _urlConnectionChnl = null;
    private static String _paymentType = null;
    private static String _trfCategory = null;
    private static String _netwrkCode = null;
    private static String _url = null;
    private static long counter = 1;
    private static ArrayList<String> statusList = new ArrayList<String>();

    /**
     * to ensure no class instantiation 
     */
    private MonthlyRedemptionProcess(){
    	
    }
    
    public static void main(String[] args) {
        final String METHOD_NAME = "main";
        try {
            if (args.length < 2 || args.length > 3) {
                _logger.info(METHOD_NAME, "Usage : MonthlyRedemptionProcess [Constants file] [LogConfig file]");
                return;
            }
            final File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                _logger.info(METHOD_NAME, " Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists()) {
                _logger.info(METHOD_NAME, " Logconfig File Not Found .............");
                return;
            }
            final File batOprtnConfigFile = new File(args[2]);
            if (!batOprtnConfigFile.exists()) {
                System.out.println("MonthlyRedemptionProcess" + " BatchOperationConfig.props File Not Found .............");
                return;
            }
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            try(FileInputStream fileInputStream = new FileInputStream(batOprtnConfigFile))
            {
            _monthlyProperties.load(fileInputStream);
            }
        } catch (Exception ex) {
            _logger.errorTrace(METHOD_NAME, ex);
            ConfigServlet.destroyProcessCache();
            return;
        }
        try {
            process();
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.info(METHOD_NAME, " Exiting");
            }
            ConfigServlet.destroyProcessCache();
        }
    }

    private static void process() throws BTSLBaseException {
        final String METHOD_NAME = "process";
        Date processedUpto = null;
        Date currentDate = null;
        Connection con = null;
        String processId = null;
        boolean statusOk = false;
        SimpleDateFormat sdf = null;
        List<ChannelUserVO> list = null;
        Iterator chnlUsrItrtr = null;
        ProcessStatusVO _processStatusMISVO = null;
        Date processedUptoMIS = null;
        String ts = null;
        String _oldTs = null;
        try {
            sdf = new SimpleDateFormat("MM/dd/yyyy");
            final String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
            try {
                calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MonthlyRedemptionProcess[process]", "", "", "",
                    "Exception while loading the class at the call:" + e.getMessage());
            }
            _logger.debug(METHOD_NAME, "Memory at startup: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            currentDate = new Date();
            _oldTs = (((BTSLUtil.getDateTimeStringFromDate(currentDate)).replace("/", "")).replace(":", "")).replace(" ", "");
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, " DATABASE Connection is NULL ");
                }
                EventHandler.handle(EventIDI.DATABASE_CONECTION_PROBLEM, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MonthlyRedemptionProcess[process]",
                    "", "", "", "DATABASE Connection is NULL");
                return;
            }
            _processBL = new ProcessBL();
            // Process should not execute until the MIS has not executed
            // successfully for previous day
            _processStatusMISVO = _processBL.checkProcessUnderProcess(con, ProcessI.C2SMIS);
            processedUptoMIS = _processStatusMISVO.getExecutedUpto();
            if (processedUptoMIS != null) {
                con.rollback();
                final Calendar cal4CurrentDate = BTSLDateUtil.getInstance();
                final Calendar cal14MisExecutedUpTo = BTSLDateUtil.getInstance();
                final Date currentDate1 = cal4CurrentDate.getTime(); // Current
                // Date
                cal14MisExecutedUpTo.setTime(processedUptoMIS);
                final Calendar cal24CurrentDate = BTSLDateUtil.getCalendar(cal4CurrentDate);
                final Calendar cal34MisExecutedUpTo = BTSLDateUtil.getCalendar(cal14MisExecutedUpTo);
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "(currentDate - 1) = " + currentDate1 + " processedUptoMIS = " + processedUptoMIS);
                }
                if (cal24CurrentDate.compareTo(cal34MisExecutedUpTo) != 0) {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug(METHOD_NAME, "The MIS has not been executed for the previous month.");
                    }
                    EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LmsPointsRedemptionProcess[process]", "", "",
                        "", "The MIS has not been executed for the previous day.");
                    throw new BTSLBaseException("MonthlyRedemptionProcess", METHOD_NAME, PretupsErrorCodesI.LMS_MIS_DEPENDENCY);
                }
            } else {
                throw new BTSLBaseException("MonthlyRedemptionProcess", METHOD_NAME, PretupsErrorCodesI.LMS_MIS_DEPENDENCY);
            }
            processId = ProcessI.MONTHLY_RED_PROCESSID;
            // method call to check status of the process
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            if (statusOk) {
                con.commit();
                // method call to find maximum date till which process has been
                // executed
                processedUpto = _processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    if (sdf.format(processedUpto).equals(sdf.format(currentDate))) {
                        EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "LmsPointsRedemption[process]", "", "", "",
                            "Activation Bonus already run for the date =" + String.valueOf(currentDate));
                        throw new BTSLBaseException("MonthlyRedemptionProcess", METHOD_NAME, "Activation Bonus already run for the date =" + String.valueOf(currentDate));
                    }
                    _processStatusVO.setStartDate(currentDate);
                    list = fetchUsersForRedemption(con, currentDate);
                    if (list != null && !(list.isEmpty())) {
                        chnlUsrItrtr = list.iterator();
                        String urlToSend = null;
                        final String httpURLPrefix = "http://";
                        URL url = null;
                        URL url1 = null;
                        PrintWriter out = null;
                        BufferedReader in = null;
                        String responseStr = null;
                        String requestMessage = null;
                        PrintWriter chOut = null;
                        BufferedReader chIn = null;
                        String chResponseStr = null;
                        String chRequestMessage = null;

                        final String formattedDate = BTSLUtil.getDateTimeStringFromDate(currentDate, "dd-MM-yyyy hh:MM:ss");

                        _paymentType = SqlParameterEncoder.encodeParams(_monthlyProperties.getProperty("PAYMENTTYPE"));
                        _trfCategory = SqlParameterEncoder.encodeParams(_monthlyProperties.getProperty("F_TRFCATEGORY"));
                        _netwrkCode = SqlParameterEncoder.encodeParams(_monthlyProperties.getProperty("NTWRKCODE"));
                        _url = SqlParameterEncoder.encodeParams(_monthlyProperties.getProperty("URL"));

                        urlToSend = httpURLPrefix + _url + SqlParameterEncoder.encodeParams(_monthlyProperties.getProperty("C2S_RECEIVER"));
                        urlToSend = urlToSend + "?REQUEST_GATEWAY_CODE=" + SqlParameterEncoder.encodeParams(_monthlyProperties.getProperty("LMS_REQUEST_GATEWAY_CODE")) + "&REQUEST_GATEWAY_TYPE=" + 
                        		SqlParameterEncoder.encodeParams(_monthlyProperties.getProperty("REQUEST_GATEWAY_TYPE"));
                        urlToSend = urlToSend + "&SERVICE_PORT=" + SqlParameterEncoder.encodeParams(_monthlyProperties.getProperty("SERVICE_PORT")) + "&LOGIN=" +
                        		SqlParameterEncoder.encodeParams(_monthlyProperties.getProperty("LOGIN"));
                        urlToSend = urlToSend + "&PASSWORD=" + SqlParameterEncoder.encodeParams(_monthlyProperties.getProperty("PASSWORD"))
                        		+ "&SOURCE_TYPE=" + SqlParameterEncoder.encodeParams(_monthlyProperties.getProperty("SOURCE_TYPE"));
                        while (chnlUsrItrtr.hasNext()) {
                            final ChannelUserVO chnlUserVO = (ChannelUserVO) chnlUsrItrtr.next();
                            int p = 0;
                            if ("PREPAID".equalsIgnoreCase(chnlUserVO.getAssType())) {
                                p = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.M_PRE_PERCENTAGE))).intValue();
                            } else if ("SLAVEPF".equalsIgnoreCase(chnlUserVO.getAssType())) {
                                p = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.M_SLAVE_PERCENTAGE))).intValue();
                            }
                            final Double transAMount = (Double.parseDouble(PretupsBL.getDisplayAmount(chnlUserVO.getMonthlyTransAmt())) * p) / 100;
                            try {
                                ts = (((BTSLUtil.getDateTimeStringFromDate(currentDate)).replace("/", "")).replace(":", "")).replace(" ", "");
                            } catch (ParseException pe) {
                                _logger.error(METHOD_NAME, pe);
                            }
                            if (!_oldTs.equals(ts)) {
                                counter = 1;
                            }

                            requestMessage = "<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XMLCommand1.0//EN\"\"xml/command.dtd\"><COMMAND><TYPE>O2CINTREQ</TYPE><EXTNWCODE>" + _netwrkCode + "</EXTNWCODE>" + "<MSISDN>" + chnlUserVO
                                .getMsisdn() + "</MSISDN><PIN></PIN><EXTCODE></EXTCODE><EXTTXNNUMBER>" + (ts + counter) + "</EXTTXNNUMBER>" + "<EXTTXNDATE>" + formattedDate + "</EXTTXNDATE><PRODUCTS><PRODUCTCODE>" +
                                SqlParameterEncoder.encodeParams(_monthlyProperties.getProperty("PRE_PROD_CODE")) + "</PRODUCTCODE>" + "<QTY>" + transAMount + "</QTY></PRODUCTS><TRFCATEGORY>" + _trfCategory + "</TRFCATEGORY><REFNUMBER></REFNUMBER><PAYMENTDETAILS>" + "<PAYMENTTYPE>" + _paymentType + "</PAYMENTTYPE><PAYMENTINSTNUMBER></PAYMENTINSTNUMBER><PAYMENTDATE>" + formattedDate + "</PAYMENTDATE></PAYMENTDETAILS><REMARKS></REMARKS></COMMAND>";

                            url = new URL(urlToSend);
                            _urlConnection = (HttpURLConnection) url.openConnection();
                            _urlConnection.setConnectTimeout(10000);
                            _urlConnection.setReadTimeout(10000);
                            _urlConnection.setDoOutput(true);
                            _urlConnection.setDoInput(true);
                            _urlConnection.addRequestProperty("Content-Type", "text/xml");
                            _urlConnection.setRequestMethod("POST");
                            final StringBuffer buffer = new StringBuffer();
                            String respStr = "";
                            try {
                                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_urlConnection.getOutputStream())), true);
                                if (_logger.isDebugEnabled()) {
                                    _logger.debug(METHOD_NAME, "Request sent   =" + requestMessage);
                                }
                                out.println(requestMessage);
                                out.flush();
                                in = new BufferedReader(new InputStreamReader(_urlConnection.getInputStream()));
                                while ((respStr = in.readLine()) != null) {
                                    buffer.append(respStr);
                                }
                            } catch (Exception e) {
                                _logger.errorTrace(METHOD_NAME, e);
                                _logger.error(METHOD_NAME, "Exception in reading or writing  e:" + e.getMessage());
                            }// end of catch-Exception
                            finally {
                                try {
                                    if (out != null) {
                                        out.close();
                                    }
                                } catch (Exception e) {
                                    _logger.errorTrace(METHOD_NAME, e);
                                }
                                try {
                                    if (in != null) {
                                        in.close();
                                    }
                                } catch (Exception e) {
                                    _logger.errorTrace(METHOD_NAME, e);
                                }
                            }// end of finally
                            responseStr = buffer.toString();
                            if (_logger.isDebugEnabled()) {
                                _logger.debug(METHOD_NAME, "Response Received   =" + responseStr);
                            }
                            String rType = "";
                            if (isFocSuccess(responseStr)) {
                                final String refNo = ts + (counter + 1);
                                if ("PREPAID".equals(chnlUserVO.getAssType())) {
                                    rType = "C2S";
                                    chRequestMessage = "<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XMLCommand1.0//EN\"\"xml/command.dtd\"><COMMAND><TYPE>EXRCTRFREQ</TYPE><DATE>" + formattedDate + "</DATE>" + "<EXTNWCODE>" + _netwrkCode + "</EXTNWCODE><MSISDN>" + chnlUserVO
                                        .getMsisdn() + "</MSISDN><PIN>" + BTSLUtil.decryptText(chnlUserVO.getUserPhoneVO().getSmsPin()) + "</PIN>" + "<LOGINID></LOGINID><PASSWORD></PASSWORD><EXTCODE></EXTCODE><EXTREFNUM>" + refNo + "</EXTREFNUM>" + "<MSISDN2>" + chnlUserVO
                                        .getAssoMsisdn() + "</MSISDN2><AMOUNT>" + transAMount + "</AMOUNT><LANGUAGE1>0</LANGUAGE1><LANGUAGE2>0</LANGUAGE2><SELECTOR>S1</SELECTOR></COMMAND>";
                                } else {
                                    rType = "C2C";
                                    chRequestMessage = "<?xml version=\"1.0\"?><!DOCTYPE COMMAND PUBLIC \"-//Ocam//DTD XMLCommand1.0//EN\"\"xml/command.dtd\"><COMMAND><TYPE>EXC2CTRFREQ</TYPE><DATE>" + formattedDate + "</DATE>" + "<EXTNWCODE>" + _netwrkCode + "</EXTNWCODE><MSISDN1>" + chnlUserVO
                                        .getMsisdn() + "</MSISDN1><PIN>" + BTSLUtil.decryptText(chnlUserVO.getUserPhoneVO().getSmsPin()) + "</PIN>" + "<LOGINID></LOGINID><PASSWORD></PASSWORD><EXTCODE></EXTCODE><EXTREFNUM>" + refNo + "</EXTREFNUM>" + "<MSISDN2>" + chnlUserVO
                                        .getAssoMsisdn() + "</MSISDN2><EXTCODE2></EXTCODE2><LOGINID2></LOGINID2><PRODUCTS><PRODUCTCODE>" +
                                        SqlParameterEncoder.encodeParams(_monthlyProperties.getProperty("PRE_PROD_CODE")) + "</PRODUCTCODE>" + "<QTY>" + transAMount + "</QTY></PRODUCTS><CELLID>12345</CELLID><SWITCHID>6789</SWITCHID><LANGUAGE1>0</LANGUAGE1></COMMAND>";
                                }
                                url1 = new URL(urlToSend);
                                _urlConnectionChnl = (HttpURLConnection) url1.openConnection();
                                _urlConnectionChnl.setConnectTimeout(10000);
                                _urlConnectionChnl.setReadTimeout(10000);
                                _urlConnectionChnl.setDoOutput(true);
                                _urlConnectionChnl.setDoInput(true);
                                _urlConnectionChnl.addRequestProperty("Content-Type", "text/xml");
                                _urlConnectionChnl.setRequestMethod("POST");
                                final StringBuffer chnlBuffer = new StringBuffer();
                                String chRespStr = "";
                                try {
                                    chOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(_urlConnectionChnl.getOutputStream())), true);
                                    if (_logger.isDebugEnabled()) {
                                        _logger.debug(METHOD_NAME, "Request sent   =" + chRequestMessage);
                                    }
                                    chOut.println(chRequestMessage);
                                    chOut.flush();
                                    chIn = new BufferedReader(new InputStreamReader(_urlConnectionChnl.getInputStream()));
                                    while ((chRespStr = chIn.readLine()) != null) {
                                        chnlBuffer.append(chRespStr);
                                    }
                                } catch (Exception e) {
                                    _logger.errorTrace(METHOD_NAME, e);
                                    _logger.error(METHOD_NAME, "Exception in reading or writing  e:" + e.getMessage());
                                }// end of catch-Exception
                                finally {
                                    try {
                                        if (chOut != null) {
                                            chOut.close();
                                        }
                                    } catch (Exception e) {
                                        _logger.errorTrace(METHOD_NAME, e);
                                    }
                                    try {
                                        if (chIn != null) {
                                            chIn.close();
                                        }
                                    } catch (Exception e) {
                                        _logger.errorTrace(METHOD_NAME, e);
                                    }
                                }// end of finally
                                chResponseStr = chnlBuffer.toString();
                                parseResponse(chResponseStr, chRequestMessage, rType, chnlUserVO.getUserID(), PretupsBL.getDisplayAmount(chnlUserVO.getMonthlyTransAmt()));
                                if (_logger.isDebugEnabled()) {
                                    _logger.debug(METHOD_NAME, "Response Received   =" + chResponseStr);
                                }
                            }

                            _oldTs = ts;
                            counter++;
                        }
                    }
                }
            }

        } catch (BTSLBaseException be) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            _logger.error("process", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception e) {
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (Exception ex) {
                _logger.errorTrace(METHOD_NAME, ex);
            }
            _logger.error("process", "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "MonthlyPointsRedemption[process]", "", "", ""," MonthlyPointsRedemption process could not be executed successfully.");
            throw new BTSLBaseException("MonthlyPointsRedemption", "process", PretupsErrorCodesI.ADDCOMMDDT_ERROR_EXCEPTION);
        } finally {
            // if the status was marked as under process by this method call,
            // only then it is marked as complete on termination
            if (statusOk) {
                try {
                    if (markProcessStatusAsComplete(con, processId) == 1) {
                        try {
                            if (con != null) {
                                con.commit();
                            }
                        } catch (Exception e) {
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                    } else {
                        try {
                            if (con != null) {
                                con.rollback();
                            }
                        } catch (Exception e) {
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                    }
                } catch (Exception e) {
                    _logger.errorTrace(METHOD_NAME, e);
                }
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (Exception ex) {
                    _logger.errorTrace(METHOD_NAME, ex);
                }
            }
            final String finalDirectroyPath = createDirectory();
            writeToFile(finalDirectroyPath, statusList, "MonthlyRedemption");
            _logger.debug(METHOD_NAME, "Memory at end: Total:" + Runtime.getRuntime().totalMemory() / 1049576 + " Free:" + Runtime.getRuntime().freeMemory() / 1049576);
            _logger.debug(METHOD_NAME, "Exiting..... ");
        }
    }

    private static int markProcessStatusAsComplete(Connection p_con, String p_processId) throws BTSLBaseException {
        final String METHOD_NAME = "markProcessStatusAsComplete";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entered:  p_processId:" + p_processId);
        }
        int updateCount = 0;
        final Date currentDate = new Date();
        final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        _processStatusVO.setProcessID(p_processId);
        _processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        _processStatusVO.setStartDate(currentDate);
        _processStatusVO.setExecutedUpto(currentDate);
        _processStatusVO.setExecutedOn(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetail(p_con, _processStatusVO);
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error(METHOD_NAME, "Exception= " + e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MonthlyPointsRedemption[markProcessStatusAsComplete]",
                "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("MonthlyPointsRedemption", METHOD_NAME, PretupsErrorCodesI.ADDCOMMDDT_ERROR_EXCEPTION);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }

    public static List<ChannelUserVO> fetchUsersForRedemption(Connection p_con, Date p_date) throws BTSLBaseException {
        final String METHOD_NAME = "fetchUsersForRedemption";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<ChannelUserVO> list = null;
        ChannelUserVO channelUserVO = null;
        UserPhoneVO userPhoneVO = null;

        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entered:  date:" + p_date);
        }
        list = new ArrayList<ChannelUserVO>();
        try {

            boolean isAssociated = false;
            if ("Y".equalsIgnoreCase(SqlParameterEncoder.encodeParams(_monthlyProperties.getProperty("IS_ASS_MSISDN")))) {
                isAssociated = true;
            }
            final String[] catList =SqlParameterEncoder.encodeParams(_monthlyProperties.getProperty("CATEGORY_LIST")).split(",");
            pstmt = p_con.prepareStatement(getQuery(isAssociated, catList, p_date));
            rs = pstmt.executeQuery();
            while (rs.next()) {
                channelUserVO = new ChannelUserVO();
                userPhoneVO = new UserPhoneVO();
                channelUserVO.setUserID(SqlParameterEncoder.encodeParams(rs.getString("user_id")));
                channelUserVO.setFirstName(SqlParameterEncoder.encodeParams(rs.getString("firstname")));
                channelUserVO.setFirstName(SqlParameterEncoder.encodeParams(rs.getString("lastname")));
                channelUserVO.setUserName(SqlParameterEncoder.encodeParams(rs.getString("user_name")));
                channelUserVO.setCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("category_code")));
                channelUserVO.setMsisdn(SqlParameterEncoder.encodeParams(rs.getString("msisdn")));
                channelUserVO.setNetworkCode(SqlParameterEncoder.encodeParams(rs.getString("network_code")));
                channelUserVO.setExternalCode(SqlParameterEncoder.encodeParams(rs.getString("external_code")));
                channelUserVO.setAssoMsisdn(SqlParameterEncoder.encodeParams(rs.getString("associated_msisdn")));
                channelUserVO.setAssType(SqlParameterEncoder.encodeParams(rs.getString("associated_msisdn_type")));
                channelUserVO.setEmpCode(SqlParameterEncoder.encodeParams(rs.getString("employee_code")));
                channelUserVO.setStatus(SqlParameterEncoder.encodeParams(rs.getString("status")));
                channelUserVO.setMonthlyTransAmt(Long.parseLong(SqlParameterEncoder.encodeParams(rs.getString("transaction_amount"))));
                userPhoneVO.setSmsPin(SqlParameterEncoder.encodeParams(rs.getString("sms_pin")));
                channelUserVO.setUserPhoneVO(userPhoneVO);
                list.add(channelUserVO);
            }
            System.out.println("rs.next()" + rs.next());
        } catch (SQLException sqe) {
            _logger.error(METHOD_NAME, "SQLException : " + sqe);
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MonthlyPointsRedemption[fetchUsers]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException("LMSMonthlyRedemptionProcess", METHOD_NAME, "error.general.sql.processing");
        } catch (Exception ex) {
            _logger.error(METHOD_NAME, "Exception : " + ex);
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "MonthlyPointsRedemption[fetchUsers]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException("MonthlyRedemptionProcess", METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }

            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting:  arrayList Size =" + list.size());
            }
        }
        return list;
    }

    public static String getQuery(boolean isAssociated, String[] catList, Date p_date) {
        final String METHOD_NAME = "getQuery";
        Date currentDate = null;
        try {
            currentDate = BTSLUtil.getDateFromDateString(BTSLUtil.getDateStringFromDate(p_date));
        } catch (ParseException e) {
            _logger.errorTrace(METHOD_NAME, e);
        }
        final Calendar c = BTSLDateUtil.getInstance();
        c.setTime(currentDate);
        final StringBuffer sb = new StringBuffer();

        sb.append("SELECT m.user_id, m.transaction_amount, u.category_code, cu.associated_msisdn_type, ");
        sb.append("cu.associated_msisdn, u.user_name, u.network_code, u.login_id, u.employee_code, ");
        sb.append("u.status, u.msisdn, u.external_code, u.firstname, u.lastname, up.sms_pin ");
        sb.append("FROM monthly_c2s_trans_details m, ");
        sb.append("channel_users cu, USERS u, user_phones up ");
        sb.append("WHERE m.trans_date = to_date(" + c.get(Calendar.MONTH) + ",'mm') ");
        sb.append("AND u.CATEGORY_CODE IN (");
        if (catList != null) {
            for (int i = 0; i < catList.length; i++) {
                sb.append("'" + catList[i] + "'");
                if (i < catList.length - 1) {
                    sb.append(",");
                }
            }
        }
        sb.append(") ");
        if (isAssociated) {
            sb.append("AND cu.associated_msisdn IS NOT NULL ");
        }
        sb.append("AND m.user_id = cu.user_id ");
        sb.append("AND up.user_id = u.user_id ");
        sb.append("AND cu.USER_ID = u.USER_ID ");
        System.out.println("Query::" + sb);
        return sb.toString();
    }

    public static void parseResponse(String response, String request, String type, String user_id, String actualAmount) {
        String txnStatus = null;
        String txnId = null;
        String refNumber = null;
        String finalResponse = "";
        String tranAmount = null;
        String senderMsisdn = null;
        String recMsisdn = null;

        txnStatus = response.substring(response.indexOf("<TXNSTATUS>") + "<TXNSTATUS>".length(), response.indexOf("</TXNSTATUS>"));
        txnId = response.substring(response.indexOf("<TXNID>") + "<TXNID>".length(), response.indexOf("</TXNID>"));
        refNumber = response.substring(response.indexOf("<EXTREFNUM>") + "<EXTREFNUM>".length(), response.indexOf("</EXTREFNUM>"));
        if (txnId == null) {
            txnId = "";
        }

        if (type.equals("C2S")) {
            tranAmount = request.substring(request.indexOf("<AMOUNT>") + "<AMOUNT>".length(), request.indexOf("</AMOUNT>"));
            senderMsisdn = request.substring(request.indexOf("<MSISDN>") + "<MSISDN>".length(), request.indexOf("</MSISDN>"));
        } else {
            tranAmount = request.substring(request.indexOf("<QTY>") + "<QTY>".length(), request.indexOf("</QTY>"));
            senderMsisdn = request.substring(request.indexOf("<MSISDN1>") + "<MSISDN1>".length(), request.indexOf("</MSISDN1>"));
        }
        recMsisdn = request.substring(request.indexOf("<MSISDN2>") + "<MSISDN2>".length(), request.indexOf("</MSISDN2>"));

        finalResponse = txnId + "|" + txnStatus + "|" + refNumber + "|" + tranAmount + "|" + type + "|" + senderMsisdn + "|" + recMsisdn + "|" + user_id + "|" + actualAmount;

        statusList.add(finalResponse);

    }

    public static void writeToFile(String finalDirectoryPath, ArrayList<String> statusList, String fileName) {
        final String METHOD_NAME = "writeToFile";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, "Entered with FinalDirectoryPath ::" + finalDirectoryPath + "statusList size" + statusList.size() + " File Name :: " + fileName);
        }
        String sucFileName = null;
        String failFileName = null;
        String message = null;
        PrintWriter sucWriter = null;
        PrintWriter failWriter = null;
        final String dir = Constants.getProperty("FinalMonthlyRedemptionFilePath");
        sucFileName = finalDirectoryPath + "/" + fileName + "_Success.txt";
        failFileName = dir + "/" + fileName + "_Fail.txt";
         File files=new File(failFileName);
        try {
            String status = null;
            sucWriter = new PrintWriter(sucFileName, "UTF-8");
             int statusListsizes=statusList.size();
            for (int statusCount = 0; statusCount < statusListsizes; statusCount++) {
                status = statusList.get(statusCount).split("[|]")[1];
                if ("200".equalsIgnoreCase(status)) {
                    message = statusList.get(statusCount).split("[|]")[7] + "|" + statusList.get(statusCount).split("[|]")[8] + "|" + statusList.get(statusCount).split("[|]")[3] + "|" + statusList
                        .get(statusCount).split("[|]")[6] + "|" + statusList.get(statusCount).split("[|]")[4];
                    sucWriter.println(message);
                } else {
                    if (!(files.exists())) {
                        failWriter = new PrintWriter(failFileName, "UTF-8");
                    }
                    message = statusList.get(statusCount).split("[|]")[0] + "|" + statusList.get(statusCount).split("[|]")[1] + "|" + statusList.get(statusCount).split("[|]")[2] + "|" + statusList
                        .get(statusCount).split("[|]")[3] + "|" + statusList.get(statusCount).split("[|]")[4] + "|" + statusList.get(statusCount).split("[|]")[5] + "|" + statusList
                        .get(statusCount).split("[|]")[6];
                    failWriter.println(message);
                }
            }
        } catch (UnsupportedEncodingException e) {
            _logger.errorTrace(METHOD_NAME, e);
        } catch (FileNotFoundException e) {
            _logger.errorTrace(METHOD_NAME, e);
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
        } finally {
            sucWriter.close();
            if (failWriter != null) {
                failWriter.close();
            }
        }
    }

    public static String createDirectory() {
        final String METHOD_NAME = "createDirectory";
        final String dir = Constants.getProperty("FinalMonthlyRedemptionFilePath");
        String dirName = null;
        String completeFinalDirPath = null;
        try {
            dirName = (((BTSLUtil.getDateTimeStringFromDate(new Date())).replace("/", "")).replace(":", "")).replace(" ", "");
            completeFinalDirPath = dir + dirName;
            final File file = new File(completeFinalDirPath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (ParseException e) {
            _logger.errorTrace(METHOD_NAME, e);
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting.. finalDirectoryName :: " + completeFinalDirPath);
            }
        }
        return completeFinalDirPath;
    }

    public static boolean isFocSuccess(String response) {
        String txnStatus = null;
        txnStatus = response.substring(response.indexOf("<TXNSTATUS>") + "<TXNSTATUS>".length(), response.indexOf("</TXNSTATUS>"));
        if ("200".equals(txnStatus)) {
            return true;
        } else {
            return false;
        }
    }
}

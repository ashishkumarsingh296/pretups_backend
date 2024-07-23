package com.btsl.pretups.processes;

/**
 * @(#)DailyReportAnalysis.java
 *                              Copyright(c) 2006, Bharti Telesoft Ltd. All
 *                              Rights Reserved
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Author Date History
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 *                              Ved Prakash Sharma 20/09/2006 Initial Creation
 *                              ------------------------------------------------
 *                              --
 *                              -----------------------------------------------
 */
import java.io.File;
import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.EMailSender;
import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.DailyReportAnalysisDAO;
import com.btsl.pretups.processes.businesslogic.DailyReportVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.ibm.icu.util.Calendar;

/**
 * @author ved.sharma TODO To change the template for this generated type
 *         comment go to Window - Preferences - Java - Code Style - Code
 *         Templates
 */
public class DailyReportAnalysis {
    private static final Log _logger = LogFactory.getLog(DailyReportAnalysis.class.getName());
    private static Locale _locale = null;
    private static String finalFileName = null;
    private static Date _reportDate = null;
    private static Date _fromDate = null;

    /**
     * @param arg
     *            arg[0]=Conatnsts.props
     *            arg[1]=ProcessLogconfig.props
     *            arg[2]=Locale(0 or 1)
     *            arg[3]=Network (ALL or MO or DL...)
     *            arg[4]=report Date
     **/
    public static void main(String[] arg) {
        final String METHOD_NAME = "main";
        try {
            if (arg.length > 5 || arg.length < 4)// check the argument length
            {
                _logger.info(METHOD_NAME, "DailyReportAnalysis :: Not sufficient arguments, please pass Conatnsts.props ProcessLogconfig.props Locale Network ReportDate");
                return;
            }
            final File constantsFile = Constants.validateFilePath(arg[0]);
            if (!constantsFile.exists())// check file (Constants.props) exist or
            // not
            {
                _logger.debug(METHOD_NAME, "DailyReportAnalysis" + " Constants File Not Found at the provided path.");
                return;
            }
            final File logconfigFile = Constants.validateFilePath(arg[1]);
            if (!logconfigFile.exists())// check file (ProcessLogConfig.props)
            // exist or not
            {
                _logger.debug(METHOD_NAME, "DailyReportAnalysis" + " ProcessLogConfig File Not Found at the provided path.");
                return;
            }
            if (BTSLUtil.isNullString(arg[2]))// Locale check
            {
                _logger.info(METHOD_NAME, "DailyReportAnalysis :: Locale is missing ");
                return;
            }
            if (!BTSLUtil.isNumeric(arg[2]))// check the Process Interval is
            // numeric
            {
                _logger.debug(METHOD_NAME, "DailyReportAnalysis :: Invalid Locale " + arg[2] + " It should be 0 or 1");
                return;
            }
            if (Integer.parseInt(arg[2]) > 1 && Integer.parseInt(arg[2]) < 0) {
                _logger.debug(METHOD_NAME, "DailyReportAnalysis :: Invalid Locale " + arg[2] + " It should be 0 or 1");
                return;
            }
            if (BTSLUtil.isNullString(arg[3]))// Network code check
            {
                _logger.info(METHOD_NAME, "DailyReportAnalysis :: Network code is missing ");
                return;
            }
            if (arg.length == 5 && !BTSLUtil.isNullString(arg[4])) {
                try {
                    _reportDate = BTSLUtil.getDateFromDateString(arg[4], PretupsI.DATE_FORMAT);
                } catch (ParseException e1) {
                    _logger.info(METHOD_NAME, "DailyReportAnalysis :: Report date format should be dd/MM/yy");
                    _logger.errorTrace(METHOD_NAME, e1);
                    return;
                }
            } else {
                _reportDate = new Date();
            }
            _reportDate = BTSLUtil.addDaysInUtilDate(_reportDate, -1);
            final Calendar g = BTSLDateUtil.getCalendar(_reportDate.getYear() + 1900, _reportDate.getMonth(), 1);
            _fromDate = g.getTime();
            // use to load the Constants.props and ProcessLogConfig.props files
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
            LookupsCache.loadLookAtStartup();
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Main: Error in loading the Cache information.." + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, "DailyReportAnalysis[main]", "", "", "",
                "  Error in loading the Cache information");
            ConfigServlet.destroyProcessCache();
            return;
        }// end of catch
        try {
            _locale = LocaleMasterCache.getLocaleFromCodeDetails(arg[2]);
            if (_locale == null) {
                _locale = LocaleMasterCache.getLocaleFromCodeDetails("0");
                if (_logger.isDebugEnabled()) {
                    _logger.debug(METHOD_NAME, "Error : Invalid Locale " + arg[2] + " It should be 0 or 1, thus using default locale code 0");
                }
                EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "DailyReportAnalysis[main]", "", "", "",
                    "  Message:  Invalid Locale " + arg[2] + " It should be 0 (EN) or 1 (OTH) ");
            }
        } catch (Exception e) {
            _logger.error(METHOD_NAME, " Invalid locale : " + arg[2] + " Exception:" + e.getMessage());
            _locale = new Locale("en", "US");
            _logger.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_INFO, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MINOR, "DailyReportAnalysis[main]", "", "", "",
                "  Message:  Not able to get the locale");
        }
        try {
            final DailyReportAnalysis dailyReportAnalysis = new DailyReportAnalysis();
            dailyReportAnalysis.process(arg[3]);
        } catch (BTSLBaseException be) {
            _logger.error(METHOD_NAME, "BTSLBaseException :" + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            // event handle
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysis[main]", "", "", "",
                "BTSLBaseException:" + be.getMessage());
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception :" + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            // event handle
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysis[main]", "", "", "",
                "Exception:" + e.getMessage());
        } finally {
            ConfigServlet.destroyProcessCache();
        }
    }

    /**
     * method process
     * This method start the process for daily report.
     * This report load the DAO value put in HashMap and pass it XLS write.
     * 
     * @param p_networkCode
     * @throws BTSLBaseException
     */
    private void process(String p_networkCode) throws BTSLBaseException {
        final String METHOD_NAME = "process";
        if (_logger.isDebugEnabled()) {
            _logger.debug("process", " Entered:  p_networkCode=" + p_networkCode);
        }
        Connection con = null;
        String filePath = null;
        try {
            filePath = Constants.getProperty("DAILY_REPORT_FILE_PATH");
            final File fileDir = new File(filePath);
            if (!fileDir.isDirectory()) {
                fileDir.mkdirs();
            }
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            _logger.error("process", "Exception " + e.getMessage());
            throw new BTSLBaseException(this, "process", "Unable to create directory =" + filePath, "error");
        }
        try {
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("process", " DATABASE Connection is NULL ");
                }
                throw new BTSLBaseException(this, "process", "Not able to get the connection");
            }
            final HashMap writeINxlsHM = new HashMap();
            final DailyReportAnalysisDAO dailyReportAnalysisDAO = new DailyReportAnalysisDAO();

            writeINxlsHM.put("NETWORK_CODE", p_networkCode);

            final ArrayList networkList = dailyReportAnalysisDAO.loadNetworkList(con);
            writeINxlsHM.put("NETWORK_LIST", networkList);

            final ArrayList loadC2SProductList = dailyReportAnalysisDAO.loadProductListByModuleCode(con, PretupsI.C2S_MODULE);
            writeINxlsHM.put("C2S_PRODUCT_LIST", loadC2SProductList);

            final ArrayList loadP2PProductList = dailyReportAnalysisDAO.loadProductListByModuleCode(con, PretupsI.P2P_MODULE);
            writeINxlsHM.put("P2P_PRODUCT_LIST", loadP2PProductList);

            final ArrayList loadC2SSerTypList = dailyReportAnalysisDAO.loadServiceTypeList(con, PretupsI.YES, PretupsI.C2S_MODULE);
            final ArrayList loadP2PSerTypList = dailyReportAnalysisDAO.loadServiceTypeList(con, PretupsI.YES, PretupsI.P2P_MODULE);

            final ArrayList loadC2SFailRechargeList = new ArrayList();
            final ArrayList loadP2PFailRechargeList = new ArrayList();
            final ArrayList loadTotalC2SRecharge = new ArrayList();
            final ArrayList loadTotalP2PRecharge = new ArrayList();
            final ArrayList loadTotalC2SRequestRechargeList = new ArrayList();
            final ArrayList loadTotalP2PRequestRechargeList = new ArrayList();
            final ArrayList loadC2STransferSummaryProduct = new ArrayList();
            final ArrayList loadP2PTransferSummaryProduct = new ArrayList();
            // Added for mgt summary report
            ArrayList loadC2SRechargeList = new ArrayList();
            ArrayList loadP2PRechargeList = new ArrayList();
            ArrayList loadInterfaceWiseC2SRechargeList = new ArrayList();
            ArrayList loadInterfaceWiseP2PRechargeList = new ArrayList();

            ArrayList temp = null;
            DailyReportVO dailyReportVO = null;
            ListValueVO listValueVO = null;

            // load c2s fail recharge for all network
            if (PretupsI.ALL.equalsIgnoreCase(p_networkCode))// for multile
            // network
            {
                NetworkVO networkVO = null;
                for (int k = 0, l = networkList.size(); k < l; k++) {
                    networkVO = (NetworkVO) networkList.get(k);
                    for (int i = 0, j = loadC2SSerTypList.size(); i < j; i++) {
                        listValueVO = null;
                        listValueVO = (ListValueVO) loadC2SSerTypList.get(i);
                        temp = null;
                        temp = dailyReportAnalysisDAO.loadC2SFailRecharge(con, _fromDate, _reportDate, networkVO.getNetworkCode(), listValueVO.getValue());
                        if (temp != null && temp.size() > 0) {
                            loadC2SFailRechargeList.addAll(temp);
                        } else {
                            dailyReportVO = new DailyReportVO();
                            dailyReportVO.setNetworkCode(networkVO.getNetworkCode());
                            dailyReportVO.setNetworkName(getNetworkName(networkVO.getNetworkCode(), networkList));
                            dailyReportVO.setServiceType(listValueVO.getValue());
                            dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                            dailyReportVO.setErrorDesc("N.A.");
                            loadC2SFailRechargeList.add(dailyReportVO);
                        }
                    }
                }
            } else {
                for (int i = 0, j = loadC2SSerTypList.size(); i < j; i++) {
                    listValueVO = null;
                    listValueVO = (ListValueVO) loadC2SSerTypList.get(i);
                    temp = null;
                    temp = dailyReportAnalysisDAO.loadC2SFailRecharge(con, _fromDate, _reportDate, p_networkCode, listValueVO.getValue());
                    if (temp != null && temp.size() > 0) {
                        loadC2SFailRechargeList.addAll(temp);
                    } else {
                        dailyReportVO = new DailyReportVO();
                        dailyReportVO.setNetworkCode(p_networkCode);
                        dailyReportVO.setNetworkName(getNetworkName(p_networkCode, networkList));
                        dailyReportVO.setServiceType(listValueVO.getValue());
                        dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                        dailyReportVO.setErrorDesc("N.A.");
                        loadC2SFailRechargeList.add(dailyReportVO);
                    }
                }
            }
            Collections.sort(loadC2SFailRechargeList);
            writeINxlsHM.put("C2S_FAIL_RECHARGE", loadC2SFailRechargeList);

            // load c2s summary for all network
            if (PretupsI.ALL.equalsIgnoreCase(p_networkCode))// for multile
            // network
            {
                NetworkVO networkVO = null;
                for (int k = 0, l = networkList.size(); k < l; k++) {
                    networkVO = (NetworkVO) networkList.get(k);
                    for (int i = 0, j = loadC2SSerTypList.size(); i < j; i++) {
                        listValueVO = null;
                        listValueVO = (ListValueVO) loadC2SSerTypList.get(i);
                        temp = null;
                        temp = dailyReportAnalysisDAO.loadTotalC2SRecharge(con, _fromDate, _reportDate, networkVO.getNetworkCode(), listValueVO.getValue());
                        if (temp != null && temp.size() > 0) {
                            loadTotalC2SRecharge.addAll(temp);
                        } else {
                            dailyReportVO = new DailyReportVO();
                            dailyReportVO.setNetworkCode(networkVO.getNetworkCode());
                            dailyReportVO.setNetworkName(getNetworkName(networkVO.getNetworkCode(), networkList));
                            dailyReportVO.setServiceType(listValueVO.getValue());
                            dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                            loadTotalC2SRecharge.add(dailyReportVO);
                        }
                    }
                }
            } else {
                for (int i = 0, j = loadC2SSerTypList.size(); i < j; i++) {
                    listValueVO = null;
                    listValueVO = (ListValueVO) loadC2SSerTypList.get(i);
                    temp = null;
                    temp = dailyReportAnalysisDAO.loadTotalC2SRecharge(con, _fromDate, _reportDate, p_networkCode, listValueVO.getValue());
                    if (temp != null && temp.size() > 0) {
                        loadTotalC2SRecharge.addAll(temp);
                    } else {
                        dailyReportVO = new DailyReportVO();
                        dailyReportVO.setNetworkCode(p_networkCode);
                        dailyReportVO.setNetworkName(getNetworkName(p_networkCode, networkList));
                        dailyReportVO.setServiceType(listValueVO.getValue());
                        dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                        loadTotalC2SRecharge.add(dailyReportVO);
                    }
                }
            }
            Collections.sort(loadTotalC2SRecharge);

            if (PretupsI.ALL.equalsIgnoreCase(p_networkCode))// for multile
            // network
            {
                NetworkVO networkVO = null;
                for (int k = 0, l = networkList.size(); k < l; k++) {
                    networkVO = (NetworkVO) networkList.get(k);
                    for (int i = 0, j = loadC2SSerTypList.size(); i < j; i++) {
                        listValueVO = null;
                        listValueVO = (ListValueVO) loadC2SSerTypList.get(i);
                        temp = null;
                        temp = dailyReportAnalysisDAO.loadC2STransferSummaryProduct(con, _fromDate, _reportDate, networkVO.getNetworkCode(), listValueVO.getValue());
                        if (temp != null && temp.size() > 0) {
                            loadC2STransferSummaryProduct.addAll(temp);
                        } else {
                            dailyReportVO = new DailyReportVO();
                            dailyReportVO.setNetworkCode(networkVO.getNetworkCode());
                            dailyReportVO.setNetworkName(getNetworkName(networkVO.getNetworkCode(), networkList));
                            dailyReportVO.setServiceType(listValueVO.getValue());
                            dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                            loadC2STransferSummaryProduct.add(dailyReportVO);
                        }
                    }
                }
            } else {
                for (int i = 0, j = loadC2SSerTypList.size(); i < j; i++) {
                    listValueVO = null;
                    listValueVO = (ListValueVO) loadC2SSerTypList.get(i);
                    temp = null;
                    temp = dailyReportAnalysisDAO.loadC2STransferSummaryProduct(con, _fromDate, _reportDate, p_networkCode, listValueVO.getValue());
                    if (temp != null && temp.size() > 0) {
                        loadC2STransferSummaryProduct.addAll(temp);
                    } else {
                        dailyReportVO = new DailyReportVO();
                        dailyReportVO.setNetworkCode(p_networkCode);
                        dailyReportVO.setNetworkName(getNetworkName(p_networkCode, networkList));
                        dailyReportVO.setServiceType(listValueVO.getValue());
                        dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                        dailyReportVO.setErrorDesc("N.A.");
                        loadC2STransferSummaryProduct.add(dailyReportVO);
                    }
                }
            }
            Collections.sort(loadC2STransferSummaryProduct);
            writeINxlsHM.put("C2S_TRANSFER_SUMMARY_PRODUCT", loadC2STransferSummaryProduct);

            // load p2p fail recharge for all network
            if (PretupsI.ALL.equalsIgnoreCase(p_networkCode))// for multile
            // network
            {
                NetworkVO networkVO = null;
                for (int k = 0, l = networkList.size(); k < l; k++) {
                    networkVO = (NetworkVO) networkList.get(k);
                    for (int i = 0, j = loadP2PSerTypList.size(); i < j; i++) {
                        listValueVO = null;
                        listValueVO = (ListValueVO) loadP2PSerTypList.get(i);
                        temp = null;
                        temp = dailyReportAnalysisDAO.loadP2PFailRecharge(con, _fromDate, _reportDate, networkVO.getNetworkCode(), listValueVO.getValue());
                        if (temp != null && temp.size() > 0) {
                            loadP2PFailRechargeList.addAll(temp);
                        } else {
                            dailyReportVO = new DailyReportVO();
                            dailyReportVO.setNetworkCode(networkVO.getNetworkCode());
                            dailyReportVO.setNetworkName(getNetworkName(networkVO.getNetworkCode(), networkList));
                            dailyReportVO.setServiceType(listValueVO.getValue());
                            dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                            dailyReportVO.setErrorDesc("N.A.");
                            loadP2PFailRechargeList.add(dailyReportVO);
                        }
                    }
                }
            } else {
                for (int i = 0, j = loadP2PSerTypList.size(); i < j; i++) {
                    listValueVO = null;
                    listValueVO = (ListValueVO) loadP2PSerTypList.get(i);
                    temp = null;
                    temp = dailyReportAnalysisDAO.loadP2PFailRecharge(con, _fromDate, _reportDate, p_networkCode, listValueVO.getValue());
                    if (temp != null && temp.size() > 0) {
                        loadP2PFailRechargeList.addAll(temp);
                    } else {
                        dailyReportVO = new DailyReportVO();
                        dailyReportVO.setNetworkCode(p_networkCode);
                        dailyReportVO.setNetworkName(getNetworkName(p_networkCode, networkList));
                        dailyReportVO.setServiceType(listValueVO.getValue());
                        dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                        dailyReportVO.setErrorDesc("N.A.");
                        loadP2PFailRechargeList.add(dailyReportVO);
                    }
                }
            }
            Collections.sort(loadP2PFailRechargeList);
            writeINxlsHM.put("P2P_FAIL_RECHARGE", loadP2PFailRechargeList);

            // load p2p summary for all network
            if (PretupsI.ALL.equalsIgnoreCase(p_networkCode))// for multile
            // network
            {
                NetworkVO networkVO = null;
                for (int k = 0, l = networkList.size(); k < l; k++) {
                    networkVO = (NetworkVO) networkList.get(k);
                    for (int i = 0, j = loadP2PSerTypList.size(); i < j; i++) {
                        listValueVO = null;
                        listValueVO = (ListValueVO) loadP2PSerTypList.get(i);
                        temp = null;
                        temp = dailyReportAnalysisDAO.loadTotalP2PRecharge(con, _fromDate, _reportDate, networkVO.getNetworkCode(), listValueVO.getValue());
                        if (temp != null && temp.size() > 0) {
                            loadTotalP2PRecharge.addAll(temp);
                        } else {
                            dailyReportVO = new DailyReportVO();
                            dailyReportVO.setNetworkCode(networkVO.getNetworkCode());
                            dailyReportVO.setNetworkName(getNetworkName(networkVO.getNetworkCode(), networkList));
                            dailyReportVO.setServiceType(listValueVO.getValue());
                            dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                            dailyReportVO.setErrorDesc("N.A.");
                            loadTotalP2PRecharge.add(dailyReportVO);
                        }
                    }
                }
            } else {
                for (int i = 0, j = loadP2PSerTypList.size(); i < j; i++) {
                    listValueVO = null;
                    listValueVO = (ListValueVO) loadP2PSerTypList.get(i);
                    temp = null;
                    temp = dailyReportAnalysisDAO.loadTotalP2PRecharge(con, _fromDate, _reportDate, p_networkCode, listValueVO.getValue());
                    if (temp != null && temp.size() > 0) {
                        loadTotalP2PRecharge.addAll(temp);
                    } else {
                        dailyReportVO = new DailyReportVO();
                        dailyReportVO.setNetworkCode(p_networkCode);
                        dailyReportVO.setNetworkName(getNetworkName(p_networkCode, networkList));
                        dailyReportVO.setServiceType(listValueVO.getValue());
                        dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                        dailyReportVO.setErrorDesc("N.A.");
                        loadTotalP2PRecharge.add(dailyReportVO);
                    }
                }
            }
            Collections.sort(loadTotalP2PRecharge);

            if (PretupsI.ALL.equalsIgnoreCase(p_networkCode))// for multile
            // network
            {
                NetworkVO networkVO = null;
                for (int k = 0, l = networkList.size(); k < l; k++) {
                    networkVO = (NetworkVO) networkList.get(k);
                    for (int i = 0, j = loadP2PSerTypList.size(); i < j; i++) {
                        listValueVO = null;
                        listValueVO = (ListValueVO) loadP2PSerTypList.get(i);
                        temp = null;
                        temp = dailyReportAnalysisDAO.loadP2PTransferSummaryProduct(con, _fromDate, _reportDate, networkVO.getNetworkCode(), listValueVO.getValue());
                        if (temp != null && temp.size() > 0) {
                            loadP2PTransferSummaryProduct.addAll(temp);
                        } else {
                            dailyReportVO = new DailyReportVO();
                            dailyReportVO.setNetworkCode(networkVO.getNetworkCode());
                            dailyReportVO.setNetworkName(getNetworkName(networkVO.getNetworkCode(), networkList));
                            dailyReportVO.setServiceType(listValueVO.getValue());
                            dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                            loadP2PTransferSummaryProduct.add(dailyReportVO);
                        }
                    }
                }
            } else {
                for (int i = 0, j = loadP2PSerTypList.size(); i < j; i++) {
                    listValueVO = null;
                    listValueVO = (ListValueVO) loadP2PSerTypList.get(i);
                    temp = null;
                    temp = dailyReportAnalysisDAO.loadP2PTransferSummaryProduct(con, _fromDate, _reportDate, p_networkCode, listValueVO.getValue());
                    if (temp != null && temp.size() > 0) {
                        loadP2PTransferSummaryProduct.addAll(temp);
                    } else {
                        dailyReportVO = new DailyReportVO();
                        dailyReportVO.setNetworkCode(p_networkCode);
                        dailyReportVO.setNetworkName(getNetworkName(p_networkCode, networkList));
                        dailyReportVO.setServiceType(listValueVO.getValue());
                        dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                        dailyReportVO.setErrorDesc("N.A.");
                        loadP2PTransferSummaryProduct.add(dailyReportVO);
                    }
                }
            }
            Collections.sort(loadP2PTransferSummaryProduct);
            writeINxlsHM.put("P2P_TRANSFER_SUMMARY_PRODUCT", loadP2PTransferSummaryProduct);

            final ArrayList loadC2SRechargeHourly = new ArrayList();
            if (PretupsI.ALL.equalsIgnoreCase(p_networkCode))// for multile
            // network
            {
                NetworkVO networkVO = null;
                for (int k = 0, l = networkList.size(); k < l; k++) {
                    networkVO = (NetworkVO) networkList.get(k);
                    for (int i = 0, j = loadC2SSerTypList.size(); i < j; i++) {
                        listValueVO = null;
                        listValueVO = (ListValueVO) loadC2SSerTypList.get(i);
                        temp = null;
                        temp = dailyReportAnalysisDAO.loadC2SRechargeHourly(con, _reportDate, networkVO.getNetworkCode(), listValueVO.getValue());
                        if (temp != null && temp.size() > 0) {
                            loadC2SRechargeHourly.addAll(temp);
                        } else {
                            dailyReportVO = new DailyReportVO();
                            dailyReportVO.setNetworkCode(networkVO.getNetworkCode());
                            dailyReportVO.setNetworkName(getNetworkName(networkVO.getNetworkCode(), networkList));
                            dailyReportVO.setServiceType(listValueVO.getValue());
                            dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                            loadC2SRechargeHourly.add(dailyReportVO);
                        }
                    }
                }
            } else {
                for (int i = 0, j = loadC2SSerTypList.size(); i < j; i++) {
                    listValueVO = null;
                    listValueVO = (ListValueVO) loadC2SSerTypList.get(i);
                    temp = null;
                    temp = dailyReportAnalysisDAO.loadC2SRechargeHourly(con, _reportDate, p_networkCode, listValueVO.getValue());
                    if (temp != null && temp.size() > 0) {
                        loadC2SRechargeHourly.addAll(temp);
                    } else {
                        dailyReportVO = new DailyReportVO();
                        dailyReportVO.setNetworkCode(p_networkCode);
                        dailyReportVO.setNetworkName(getNetworkName(p_networkCode, networkList));
                        dailyReportVO.setServiceType(listValueVO.getValue());
                        dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                        loadC2SRechargeHourly.add(dailyReportVO);
                    }
                }
            }
            Collections.sort(loadC2SRechargeHourly);
            writeINxlsHM.put("C2S_RECHARGE_HOURLY", loadC2SRechargeHourly);

            final ArrayList loadP2PRechargeHourly = new ArrayList();
            if (PretupsI.ALL.equalsIgnoreCase(p_networkCode))// for multile
            // network
            {
                NetworkVO networkVO = null;
                for (int k = 0, l = networkList.size(); k < l; k++) {
                    networkVO = (NetworkVO) networkList.get(k);
                    for (int i = 0, j = loadP2PSerTypList.size(); i < j; i++) {
                        listValueVO = null;
                        listValueVO = (ListValueVO) loadP2PSerTypList.get(i);
                        temp = null;
                        temp = dailyReportAnalysisDAO.loadP2PRechargeHourly(con, _reportDate, networkVO.getNetworkCode(), listValueVO.getValue());
                        if (temp != null && !temp.isEmpty()) {
                            loadP2PRechargeHourly.addAll(temp);
                        } else {
                            dailyReportVO = new DailyReportVO();
                            dailyReportVO.setNetworkCode(networkVO.getNetworkCode());
                            dailyReportVO.setNetworkName(getNetworkName(networkVO.getNetworkCode(), networkList));
                            dailyReportVO.setServiceType(listValueVO.getValue());
                            dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                            loadP2PRechargeHourly.add(dailyReportVO);
                        }
                    }
                }
            } else {
                for (int i = 0, j = loadP2PSerTypList.size(); i < j; i++) {
                    listValueVO = null;
                    listValueVO = (ListValueVO) loadP2PSerTypList.get(i);
                    temp = null;
                    temp = dailyReportAnalysisDAO.loadP2PRechargeHourly(con, _reportDate, p_networkCode, listValueVO.getValue());
                    if (temp != null && !temp.isEmpty()) {
                        loadP2PRechargeHourly.addAll(temp);
                    } else {
                        dailyReportVO = new DailyReportVO();
                        dailyReportVO.setNetworkCode(p_networkCode);
                        dailyReportVO.setNetworkName(getNetworkName(p_networkCode, networkList));
                        dailyReportVO.setServiceType(listValueVO.getValue());
                        dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                        loadP2PRechargeHourly.add(dailyReportVO);
                    }
                }
            }
            Collections.sort(loadP2PRechargeHourly);
            writeINxlsHM.put("P2P_RECHARGE_HOURLY", loadP2PRechargeHourly);

            writeINxlsHM.put("LOAD_TEST", Constants.getProperty("LOAD_TEST"));
            writeINxlsHM.put("REPORT_DATE", _reportDate);

            if (PretupsI.YES.equalsIgnoreCase(Constants.getProperty("LOAD_TEST"))) {
                final ArrayList loadC2SSerTypNotExtList = dailyReportAnalysisDAO.loadServiceTypeList(con, PretupsI.NO, PretupsI.C2S_MODULE);
                final ArrayList loadP2PSerTypNotExtList = dailyReportAnalysisDAO.loadServiceTypeList(con, PretupsI.NO, PretupsI.P2P_MODULE);

                final ArrayList loadC2SRequestRechargeList = new ArrayList();
                final ArrayList loadP2PRequestRechargeList = new ArrayList();

                if (PretupsI.ALL.equalsIgnoreCase(p_networkCode))// for multile
                // network
                {
                    NetworkVO networkVO = null;
                    for (int k = 0, l = networkList.size(); k < l; k++) {
                        networkVO = (NetworkVO) networkList.get(k);
                        for (int i = 0, j = loadC2SSerTypNotExtList.size(); i < j; i++) {
                            listValueVO = null;
                            listValueVO = (ListValueVO) loadC2SSerTypNotExtList.get(i);
                            temp = null;
                            temp = dailyReportAnalysisDAO.loadC2SRecevierRequest(con, _fromDate, _reportDate, networkVO.getNetworkCode(), listValueVO.getValue());
                            if (temp != null && !temp.isEmpty()) {
                                loadC2SRequestRechargeList.addAll(temp);
                            } else {
                                dailyReportVO = new DailyReportVO();
                                dailyReportVO.setNetworkCode(networkVO.getNetworkCode());
                                dailyReportVO.setNetworkName(getNetworkName(networkVO.getNetworkCode(), networkList));
                                dailyReportVO.setServiceType(listValueVO.getValue());
                                dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                                dailyReportVO.setErrorDesc("N.A.");
                                loadC2SRequestRechargeList.add(dailyReportVO);
                            }
                        }
                    }
                } else {
                    for (int i = 0, j = loadC2SSerTypNotExtList.size(); i < j; i++) {
                        listValueVO = null;
                        listValueVO = (ListValueVO) loadC2SSerTypNotExtList.get(i);
                        temp = null;
                        temp = dailyReportAnalysisDAO.loadC2SRecevierRequest(con, _fromDate, _reportDate, p_networkCode, listValueVO.getValue());
                        if (temp != null && !temp.isEmpty()) {
                            loadC2SRequestRechargeList.addAll(temp);
                        } else {
                            dailyReportVO = new DailyReportVO();
                            dailyReportVO.setNetworkCode(p_networkCode);
                            dailyReportVO.setNetworkName(getNetworkName(p_networkCode, networkList));
                            dailyReportVO.setServiceType(listValueVO.getValue());
                            dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                            dailyReportVO.setErrorDesc("N.A.");
                            loadC2SRequestRechargeList.add(dailyReportVO);
                        }
                    }
                }
                Collections.sort(loadC2SRequestRechargeList);
                writeINxlsHM.put("C2S_RECEIVER_REQUESTS", loadC2SRequestRechargeList);

                if (PretupsI.ALL.equalsIgnoreCase(p_networkCode))// for multile
                // network
                {
                    NetworkVO networkVO = null;
                    for (int k = 0, l = networkList.size(); k < l; k++) {
                        networkVO = (NetworkVO) networkList.get(k);
                        for (int i = 0, j = loadC2SSerTypNotExtList.size(); i < j; i++) {
                            listValueVO = null;
                            listValueVO = (ListValueVO) loadC2SSerTypNotExtList.get(i);
                            temp = null;
                            temp = dailyReportAnalysisDAO.loadTotalC2SRecevierRequest(con, _fromDate, _reportDate, networkVO.getNetworkCode(), listValueVO.getValue());
                            if (temp != null && !temp.isEmpty()) {
                                loadTotalC2SRequestRechargeList.addAll(temp);
                            } else {
                                dailyReportVO = new DailyReportVO();
                                dailyReportVO.setNetworkCode(networkVO.getNetworkCode());
                                dailyReportVO.setNetworkName(getNetworkName(networkVO.getNetworkCode(), networkList));
                                dailyReportVO.setNetworkName(getNetworkName(p_networkCode, networkList));
                                dailyReportVO.setServiceType(listValueVO.getValue());
                                dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                                loadTotalC2SRequestRechargeList.add(dailyReportVO);
                            }
                        }
                    }
                } else {
                    for (int i = 0, j = loadC2SSerTypNotExtList.size(); i < j; i++) {
                        listValueVO = null;
                        listValueVO = (ListValueVO) loadC2SSerTypNotExtList.get(i);
                        temp = null;
                        temp = dailyReportAnalysisDAO.loadTotalC2SRecevierRequest(con, _fromDate, _reportDate, p_networkCode, listValueVO.getValue());
                        if (temp != null && !temp.isEmpty()) {
                            loadTotalC2SRequestRechargeList.addAll(temp);
                        } else {
                            dailyReportVO = new DailyReportVO();
                            dailyReportVO.setNetworkCode(p_networkCode);
                            dailyReportVO.setNetworkName(getNetworkName(p_networkCode, networkList));
                            dailyReportVO.setServiceType(listValueVO.getValue());
                            dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                            loadTotalC2SRequestRechargeList.add(dailyReportVO);
                        }
                    }
                }
                Collections.sort(loadTotalC2SRequestRechargeList);

                if (PretupsI.ALL.equalsIgnoreCase(p_networkCode))// for multile
                // network
                {
                    NetworkVO networkVO = null;
                    for (int k = 0, l = networkList.size(); k < l; k++) {
                        networkVO = (NetworkVO) networkList.get(k);
                        for (int i = 0, j = loadP2PSerTypNotExtList.size(); i < j; i++) {
                            listValueVO = null;
                            listValueVO = (ListValueVO) loadP2PSerTypNotExtList.get(i);
                            temp = null;
                            temp = dailyReportAnalysisDAO.loadP2PRecevierRequest(con, _fromDate, _reportDate, networkVO.getNetworkCode(), listValueVO.getValue());
                            if (temp != null && !temp.isEmpty()) {
                                loadP2PRequestRechargeList.addAll(temp);
                            } else {
                                dailyReportVO = new DailyReportVO();
                                dailyReportVO.setNetworkCode(networkVO.getNetworkCode());
                                dailyReportVO.setNetworkName(getNetworkName(networkVO.getNetworkCode(), networkList));
                                dailyReportVO.setServiceType(listValueVO.getValue());
                                dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                                dailyReportVO.setErrorDesc("N.A.");
                                loadP2PRequestRechargeList.add(dailyReportVO);
                            }
                        }
                    }
                } else {
                    for (int i = 0, j = loadP2PSerTypNotExtList.size(); i < j; i++) {
                        listValueVO = null;
                        listValueVO = (ListValueVO) loadP2PSerTypNotExtList.get(i);
                        temp = null;
                        temp = dailyReportAnalysisDAO.loadP2PRecevierRequest(con, _fromDate, _reportDate, p_networkCode, listValueVO.getValue());
                        if (temp != null && !temp.isEmpty()) {
                            loadP2PRequestRechargeList.addAll(temp);
                        } else {
                            dailyReportVO = new DailyReportVO();
                            dailyReportVO.setNetworkCode(p_networkCode);
                            dailyReportVO.setNetworkName(getNetworkName(p_networkCode, networkList));
                            dailyReportVO.setServiceType(listValueVO.getValue());
                            dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                            dailyReportVO.setErrorDesc("N.A.");
                            loadP2PRequestRechargeList.add(dailyReportVO);
                        }
                    }
                }
                Collections.sort(loadP2PRequestRechargeList);
                writeINxlsHM.put("P2P_RECEIVER_REQUESTS", loadP2PRequestRechargeList);

                if (PretupsI.ALL.equalsIgnoreCase(p_networkCode))// for multile
                // network
                {
                    NetworkVO networkVO = null;
                    for (int k = 0, l = networkList.size(); k < l; k++) {
                        networkVO = (NetworkVO) networkList.get(k);
                        for (int i = 0, j = loadP2PSerTypNotExtList.size(); i < j; i++) {
                            listValueVO = null;
                            listValueVO = (ListValueVO) loadP2PSerTypNotExtList.get(i);
                            temp = null;
                            temp = dailyReportAnalysisDAO.loadTotalP2PRecevierRequest(con, _fromDate, _reportDate, networkVO.getNetworkCode(), listValueVO.getValue());
                            if (temp != null && !temp.isEmpty()) {
                                loadTotalP2PRequestRechargeList.addAll(temp);
                            } else {
                                dailyReportVO = new DailyReportVO();
                                dailyReportVO.setNetworkCode(networkVO.getNetworkCode());
                                dailyReportVO.setNetworkName(getNetworkName(networkVO.getNetworkCode(), networkList));
                                dailyReportVO.setServiceType(listValueVO.getValue());
                                dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                                dailyReportVO.setErrorDesc("N.A.");
                                loadTotalP2PRequestRechargeList.add(dailyReportVO);
                            }
                        }
                    }
                } else {
                    for (int i = 0, j = loadP2PSerTypNotExtList.size(); i < j; i++) {
                        listValueVO = null;
                        listValueVO = (ListValueVO) loadP2PSerTypNotExtList.get(i);
                        temp = null;
                        temp = dailyReportAnalysisDAO.loadTotalP2PRecevierRequest(con, _fromDate, _reportDate, p_networkCode, listValueVO.getValue());
                        if (temp != null && !temp.isEmpty()) {
                            loadTotalP2PRequestRechargeList.addAll(temp);
                        } else {
                            dailyReportVO = new DailyReportVO();
                            dailyReportVO.setNetworkCode(p_networkCode);
                            dailyReportVO.setNetworkName(getNetworkName(p_networkCode, networkList));
                            dailyReportVO.setServiceType(listValueVO.getValue());
                            dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                            dailyReportVO.setErrorDesc("N.A.");
                            loadTotalP2PRequestRechargeList.add(dailyReportVO);
                        }
                    }
                }
                Collections.sort(loadTotalP2PRequestRechargeList);

                final ArrayList loadC2SReceiverRequestHourly = new ArrayList();
                if (PretupsI.ALL.equalsIgnoreCase(p_networkCode))// for multile
                // network
                {
                    NetworkVO networkVO = null;
                    for (int k = 0, l = networkList.size(); k < l; k++) {
                        networkVO = (NetworkVO) networkList.get(k);
                        for (int i = 0, j = loadC2SSerTypNotExtList.size(); i < j; i++) {
                            listValueVO = null;
                            listValueVO = (ListValueVO) loadC2SSerTypNotExtList.get(i);
                            temp = null;
                            temp = dailyReportAnalysisDAO.loadC2SReceiverRequestHourly(con, _reportDate, networkVO.getNetworkCode(), listValueVO.getValue());
                            if (temp != null && !temp.isEmpty()) {
                                loadC2SReceiverRequestHourly.addAll(temp);
                            } else {
                                dailyReportVO = new DailyReportVO();
                                dailyReportVO.setNetworkCode(networkVO.getNetworkCode());
                                dailyReportVO.setNetworkName(getNetworkName(networkVO.getNetworkCode(), networkList));
                                dailyReportVO.setServiceType(listValueVO.getValue());
                                dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                                loadC2SReceiverRequestHourly.add(dailyReportVO);
                            }
                        }
                    }
                } else {
                    for (int i = 0, j = loadC2SSerTypNotExtList.size(); i < j; i++) {
                        listValueVO = null;
                        listValueVO = (ListValueVO) loadC2SSerTypNotExtList.get(i);
                        temp = null;
                        temp = dailyReportAnalysisDAO.loadC2SReceiverRequestHourly(con, _reportDate, p_networkCode, listValueVO.getValue());
                        if (temp != null && !temp.isEmpty()) {
                            loadC2SReceiverRequestHourly.addAll(temp);
                        } else {
                            dailyReportVO = new DailyReportVO();
                            dailyReportVO.setNetworkCode(p_networkCode);
                            dailyReportVO.setNetworkName(getNetworkName(p_networkCode, networkList));
                            dailyReportVO.setServiceType(listValueVO.getValue());
                            dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                            loadC2SReceiverRequestHourly.add(dailyReportVO);
                        }
                    }
                }
                Collections.sort(loadC2SReceiverRequestHourly);
                writeINxlsHM.put("C2S_RECEIVER_REQUEST_HOURLY", loadC2SReceiverRequestHourly);

                final ArrayList loadP2PReceiverRequestHourly = new ArrayList();
                if (PretupsI.ALL.equalsIgnoreCase(p_networkCode))// for multile
                // network
                {
                    NetworkVO networkVO = null;
                    for (int k = 0, l = networkList.size(); k < l; k++) {
                        networkVO = (NetworkVO) networkList.get(k);
                        for (int i = 0, j = loadP2PSerTypNotExtList.size(); i < j; i++) {
                            listValueVO = null;
                            listValueVO = (ListValueVO) loadP2PSerTypNotExtList.get(i);
                            temp = null;
                            temp = dailyReportAnalysisDAO.loadP2PReceiverRequestHourly(con, _reportDate, networkVO.getNetworkCode(), listValueVO.getValue());
                            if (temp != null && !temp.isEmpty()) {
                                loadP2PReceiverRequestHourly.addAll(temp);
                            } else {
                                dailyReportVO = new DailyReportVO();
                                dailyReportVO.setNetworkCode(networkVO.getNetworkCode());
                                dailyReportVO.setNetworkName(getNetworkName(networkVO.getNetworkCode(), networkList));
                                dailyReportVO.setServiceType(listValueVO.getValue());
                                dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                                loadP2PReceiverRequestHourly.add(dailyReportVO);
                            }
                        }
                    }
                } else {
                    for (int i = 0, j = loadP2PSerTypNotExtList.size(); i < j; i++) {
                        listValueVO = null;
                        listValueVO = (ListValueVO) loadP2PSerTypNotExtList.get(i);
                        temp = null;
                        temp = dailyReportAnalysisDAO.loadP2PReceiverRequestHourly(con, _reportDate, p_networkCode, listValueVO.getValue());
                        if (temp != null && !temp.isEmpty()) {
                            loadP2PReceiverRequestHourly.addAll(temp);
                        } else {
                            dailyReportVO = new DailyReportVO();
                            dailyReportVO.setNetworkCode(p_networkCode);
                            dailyReportVO.setNetworkName(getNetworkName(p_networkCode, networkList));
                            dailyReportVO.setServiceType(listValueVO.getValue());
                            dailyReportVO.setServiceTypeName(listValueVO.getLabel());
                            loadP2PReceiverRequestHourly.add(dailyReportVO);
                        }
                    }
                }
                Collections.sort(loadP2PReceiverRequestHourly);
                writeINxlsHM.put("P2P_RECEIVER_REQUEST_HOURLY", loadP2PReceiverRequestHourly);
            }// end of
             // if(PretupsI.YES.equalsIgnoreCase(Constants.getProperty("LOAD_TEST")))

            final ArrayList c2sSummaryList = new ArrayList();
            final ArrayList p2pSummaryList = new ArrayList();

            c2sSummaryList.addAll(loadTotalC2SRecharge);
            c2sSummaryList.addAll(loadTotalC2SRequestRechargeList);
            Collections.sort(c2sSummaryList);
            writeINxlsHM.put("C2S_SUMMARY", c2sSummaryList);

            p2pSummaryList.addAll(loadTotalP2PRecharge);
            p2pSummaryList.addAll(loadTotalP2PRequestRechargeList);
            Collections.sort(p2pSummaryList);
            writeINxlsHM.put("P2P_SUMMARY", p2pSummaryList);

            // added for mgt summary report
            // load c2s recharge for all network
            loadC2SRechargeList = dailyReportAnalysisDAO.loadC2SRecharge(con, _fromDate, _reportDate);
            if (loadC2SRechargeList != null && !loadC2SRechargeList.isEmpty()) {
                Collections.sort(loadC2SRechargeList);
            }
            writeINxlsHM.put("C2S_RECHARGE", loadC2SRechargeList);

            // load p2p recharge for all network
            loadP2PRechargeList = dailyReportAnalysisDAO.loadP2PRecharge(con, _fromDate, _reportDate);
            if (loadP2PRechargeList != null && !loadP2PRechargeList.isEmpty()) {
                Collections.sort(loadP2PRechargeList);
            }
            writeINxlsHM.put("P2P_RECHARGE", loadP2PRechargeList);

            // load C2S recharge for all network interface wise
            loadInterfaceWiseC2SRechargeList = dailyReportAnalysisDAO.loadInterfaceWiseC2SRecharge(con, _fromDate, _reportDate);
            if (loadInterfaceWiseC2SRechargeList != null && !loadInterfaceWiseC2SRechargeList.isEmpty()) {
                Collections.sort(loadInterfaceWiseC2SRechargeList);
            }
            writeINxlsHM.put("C2S_INTERFACE_RECHARGE", loadInterfaceWiseC2SRechargeList);

            // load P2P recharge for all network interface wise
            loadInterfaceWiseP2PRechargeList = dailyReportAnalysisDAO.loadInterfaceWiseP2PRecharge(con, _fromDate, _reportDate);
            if (loadInterfaceWiseP2PRechargeList != null && !loadInterfaceWiseP2PRechargeList.isEmpty()) {
                Collections.sort(loadInterfaceWiseP2PRechargeList);
            }
            writeINxlsHM.put("P2P_INTERFACE_RECHARGE", loadInterfaceWiseP2PRechargeList);

            final String fileName = Constants.getProperty("DAILY_REPORT_FILE_PRIFIX") + BTSLUtil.getFileNameStringFromDate(new Date()) + ".xls";
            finalFileName = filePath + fileName;
            // Pass hashMap to write in XLS file
            final DailyReportWriteInXL dailyReportWriteInXL = new DailyReportWriteInXL();
            dailyReportWriteInXL.writeExcel(writeINxlsHM, _locale, finalFileName);

            if (PretupsI.YES.equalsIgnoreCase(Constants.getProperty("DAILY_REPORT_MAIL_SEND"))) {
                // start to send mail sending process
                String to = Constants.getProperty("DAILY_REPORT_MAIL_" + p_networkCode);
                if (BTSLUtil.isNullString(to)) {
                    to = Constants.getProperty("DAILY_REPORT_MAIL_DEFAULT");
                }
                final String from = Constants.getProperty("DAILY_REPORT_MAIL_FROM");
                final String subject = Constants.getProperty("DAILY_REPORT_MAIL_SUBJECT");
                final String bcc = Constants.getProperty("DAILY_REPORT_MAIL_BCC");
                final String cc = Constants.getProperty("DAILY_REPORT_MAIL_CC");
                final String msg = Constants.getProperty("DAILY_REPORT_MAIL_MESSAGE");
                // Send mail
                EMailSender.sendMail(to, from, bcc, cc, subject, msg, true, finalFileName, fileName);
                
                // send the message as SMS
                PushMessage pushMessage = null;
                final Locale locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
                final BTSLMessages message = new BTSLMessages(PretupsErrorCodesI.PROCESS_ADMIN_MESSAGE);
                final String msisdnString = Constants.getProperty("adminmobile");
                final String[] msisdn = msisdnString.split(",");
                for (int i = 0; i < msisdn.length; i++) {
                    pushMessage = new PushMessage(msisdn[i], message, "", "", locale,"");
                    pushMessage.push();
                }
                
            }
        } catch (BTSLBaseException be) {
            _logger.error("process", "BTSLBaseException : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw be;
        } catch (Exception be) {
            _logger.error("process", "Exception : " + be.getMessage());
            _logger.errorTrace(METHOD_NAME, be);
            throw new BTSLBaseException(this, "process", "Exception=" + be.getMessage());
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug("process", "Exiting..... ");
            }
        }
    }

    /**
     * @param p_networkCode
     * @param p_networkList
     * @return
     * @throws BTSLBaseException
     */
    public String getNetworkName(String p_networkCode, ArrayList p_networkList) throws BTSLBaseException {
        final String METHOD_NAME = "getNetworkName";
        if (_logger.isDebugEnabled()) {
            _logger.debug(METHOD_NAME, " Entered:  p_networkCode=" + p_networkCode + " p_networkList.size()=" + p_networkList.size());
        }
        String networkName = null;
        try {
            NetworkVO networkVO = null;
            for (int i = 0, j = p_networkList.size(); i < j; i++) {
                networkVO = (NetworkVO) p_networkList.get(i);
                if (p_networkCode.equals(networkVO.getNetworkCode())) {
                    networkName = networkVO.getNetworkName();
                    break;
                }

            }
        } catch (Exception e) {
            _logger.error(METHOD_NAME, "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw new BTSLBaseException(this, METHOD_NAME, "Exception=" + e.getMessage());
        }
        return networkName;
    }
}
package com.btsl.pretups.processes;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.pretups.processes.businesslogic.RoamRechargeVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.ConfigServlet;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.ibm.icu.util.Calendar;

public class MonthlyC2SRoamRechargeReport {

    public static String message = "";
    private static ProcessBL _processBL = null;
    private static ProcessStatusVO _processStatusVO;
    private static Log _logger = LogFactory.getLog(MonthlyC2SRoamRechargeReport.class.getName());
    private static Locale _locale = null;
    private static Date _fromDate = null;
    private static Date _reportDate = null;
    private static int monthDiff=0;

    public static void main(String[] args) {

        Connection con = null;
        final String METHOD_NAME = "main";

        try {
        	
            if (args.length != 3) {
            	_logger.info("MonthlyC2SRoamRechargeReport[main]","Usage : MonthlyC2SRoamRechargeReport [Constants file] [LogConfig file]");
                return;
            }
            final File constantsFile = new File(args[0]);
            if (!constantsFile.exists()) {
                System.out.println(" Constants File Not Found .............");
                return;
            }
            final File logconfigFile = new File(args[1]);
            if (!logconfigFile.exists()) {
                System.out.println(" Logconfig File Not Found .............");
                return;
            }
            String month=args[2];
            if(!BTSLUtil.isNullString(month) && BTSLUtil.isNumeric(month))
            {
            	monthDiff=Integer.parseInt(month);	
            	if(monthDiff > 6)
            	{
            		_logger.info("MonthlyC2SRoamRechargeReport[main]"," Invalid month difference .............");
                    return;
            	}
            }
            else{
            	_logger.info("MonthlyC2SRoamRechargeReport[main]"," Invalid month difference .............");
                 return;
            	
            }
            	
            ConfigServlet.loadProcessCache(constantsFile.toString(), logconfigFile.toString());
        } catch (Exception e) {
            System.out.println("Exception thrown in MonthlyC2SRoamRechargeReport: Not able to load files" + e);
            ConfigServlet.destroyProcessCache();
            _logger.errorTrace(METHOD_NAME, e);
            return;
        }

        try {
            // Make Connection
            con = OracleUtil.getSingleConnection();
            if (con == null) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("MonthlyC2SRoamRechargeReport[main]", "Not able to get Connection for RoamRechargeReport: ");
                }
                throw new SQLException();
            }
            _locale = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));

            // Calculate Previous Month Date
            calculatePreviousMonthDate();

            final MonthlyC2SRoamRechargeReport rechageReport = new MonthlyC2SRoamRechargeReport();
            rechageReport.monthlyC2sRoamExecution(con);
        } catch (Exception e) {
            if (_logger.isDebugEnabled()) {
                _logger.debug("MonthlyC2SRoamRechargeReport[main]", "Exception thrown in MonthlyC2SRoamRechargeReport: Not able to load files" + e);
            }
            _logger.errorTrace(METHOD_NAME, e);
            ConfigServlet.destroyProcessCache();
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("MonthlyC2SRoamRechargeReport[main]", "Exiting");
            }
            try {
                if (con != null) {
                    con.close();
                }
            }catch (SQLException ex) {
              	_logger.debug(METHOD_NAME, ex);
              } 
             catch (Exception e) {
                 _logger.errorTrace(METHOD_NAME, e);
            }
            ConfigServlet.destroyProcessCache();
        }
    }
 public void monthlyC2sRoamExecution(Connection con) throws ParseException{
        final String METHOD_NAME = "monthlyC2sRoamExecution";
        if (_logger.isDebugEnabled()) {
            _logger.debug("monthlyC2sRoamExecution", " Entered:");
        }
        Date currentDate = null;
        String reportTo = null;
        String prevDateStr = null;
        Date processedUpto = null;
        String processId = null;
        boolean statusOk = false;
        int beforeInterval = 0;
        try {

            processId = ProcessI.C2S_ROAM_RECHARGE_MONTH_PROCESS_ID;
            _processBL = new ProcessBL();
            _processStatusVO = _processBL.checkProcessUnderProcess(con, processId);
            statusOk = _processStatusVO.isStatusOkBool();
            //beforeInterval = (int) _processStatusVO.getBeforeInterval() / (60 * 24);
            beforeInterval = BTSLUtil.parseLongToInt(_processStatusVO.getBeforeInterval()) / (60 * 24);
            
            if (statusOk) {
                con.commit();
                processedUpto = _processStatusVO.getExecutedUpto();
                if (processedUpto != null) {
                    processedUpto = BTSLUtil.addDaysInUtilDate(processedUpto, 1);
                    final Calendar cal = BTSLDateUtil.getInstance();
                    currentDate = cal.getTime(); // Current Date
                    currentDate = BTSLUtil.addDaysInUtilDate(currentDate, -beforeInterval);
                } else {
                    if (_logger.isDebugEnabled()) {
                        _logger.debug("MonthlyC2SRoamRechargeReport[monthlyC2sRoamExecution]", " Date till which process has been executed is not found.");
                    }
                    return;
                }
            }
            try {
                final SimpleDateFormat sdf = new SimpleDateFormat(PretupsI.DATE_FORMAT);
                sdf.setLenient(false); // this is required else it will convert
                reportTo = sdf.format(currentDate); // Current Date
                prevDateStr = sdf.format(processedUpto);// Last MIS Done Date +1
            } catch (Exception e) {
                reportTo = "";
                prevDateStr = "";
                _logger.errorTrace(METHOD_NAME, e);
                
            }

            if (_logger.isDebugEnabled()) {
                _logger.debug("MonthlyC2SRoamRechargeReport[monthlyC2sRoamExecution]",
                    "From date=" + prevDateStr + " To Date=" + reportTo + " processedUpto.compareTo(currentDate)=" + processedUpto.compareTo(currentDate));
            }
            if (statusOk) {
                if (markProcessStatusAsUnderProcess(currentDate, con, processId) == 1) {
                    try {
                        con.commit();
                    } catch (Exception e) {
                        _logger.errorTrace(METHOD_NAME, e);
                    }
                } else {
                    try {
                        con.rollback();
                    } catch (Exception e) {
                        _logger.errorTrace(METHOD_NAME, e);
                    }
                }
            }
            List _networkList= loadNetworkDetails(con);
            if(_networkList!=null)
            {
            	int networkListSizes=_networkList.size();
            	for(int i=0 ; i<networkListSizes;i++)
            	{	
            		String networkCode=(String)_networkList.get(i);
		            final List _monthlyReportsData = loadC2SRoamRechargeDetails(con, _fromDate, _reportDate,networkCode);
		            writeToCSV(_monthlyReportsData,networkCode);
            	}
            }
        }

        catch (Exception e) {
            try {
                con.rollback();
            } catch (Exception sqlex) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("MonthlyC2SRoamRechargeReport", "MonthlyC2SRoamRechargeReport[monthlyC2sRoamExecution]::Exception while roll back" + sqlex);
                }
                _logger.errorTrace(METHOD_NAME, sqlex);
            }
            message = e.getMessage();
            // send the message as SMS
            _logger.errorTrace(METHOD_NAME, e);
        } finally {
            try {
                if (statusOk) {
                    if (markProcessStatusAsComplete(con, processId) == 1) {
                        try {
                            con.commit();
                        } catch (Exception e) {
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                    } else {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            _logger.errorTrace(METHOD_NAME, e);
                        }
                    }
                }
            } catch (Exception ex) {
                if (_logger.isDebugEnabled()) {
                    _logger.debug("MonthlyC2SRoamRechargeReport", "Exception while closing statement in MonthlyC2SRoamRechargeReport[monthlyC2sRoamExecution] method ");
                }
                _logger.errorTrace(METHOD_NAME, ex);
            }
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
        }

    }

    private static void calculatePreviousMonthDate() {
        final String METHOD_NAME = "calculatePreviousMonthDate";
        try {
            final Calendar cal = BTSLDateUtil.getInstance();
            cal.add(Calendar.MONTH, -(monthDiff));
            cal.set(Calendar.DATE, 1);
            _fromDate = cal.getTime();
            if (_logger.isDebugEnabled()) {
                _logger.debug("calculatePreviousMonthDate", " firstDateOfPreviousMonth: " + _fromDate);
            }

            cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));
            _reportDate = cal.getTime();
            if (_logger.isDebugEnabled()) {
                _logger.debug("calculatePreviousMonthDate", "Info : firstDateOfPreviousMonth =  " + _fromDate + "  | lastDateOfPreviousMonth = " + _reportDate);
            }
        } catch (RuntimeException e) {
            _logger.error("calculatePreviousMonthDate", "Exception : " + e.getMessage());
            _logger.errorTrace(METHOD_NAME, e);
            throw e;
        }

    }

    private static int markProcessStatusAsUnderProcess(Date processedUpto, Connection p_con, String p_processId) {
        final String METHOD_NAME = "markProcessStatusAsUnderProcess";
        if (_logger.isDebugEnabled()) {
            _logger.debug("markProcessStatusAsUnderProcess", " Entered:  p_processId:" + p_processId);
        }
        int updateCount = 0;
        final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        _processStatusVO.setProcessID(p_processId);
        _processStatusVO.setProcessStatus(ProcessI.STATUS_UNDERPROCESS);
        _processStatusVO.setExecutedUpto(processedUpto);
        _processStatusVO.setExecutedOn(new Date());
        try {
            updateCount = processStatusDAO.updateProcessDetail(p_con, _processStatusVO);
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            if (_logger.isDebugEnabled()) {
                _logger.debug("markProcessStatusAsUnderProcess", "Exception= " + e.getMessage());
            }
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("markProcessStatusAsUnderProcess", "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;
    }

    private static int markProcessStatusAsComplete(Connection p_con, String p_processId) {
        final String METHOD_NAME = "markProcessStatusAsComplete";
        if (_logger.isDebugEnabled()) {
            _logger.debug("markProcessStatusAsComplete", " Entered:  p_processId:" + p_processId);
        }
        int updateCount = 0;
        final Date currentDate = new Date();
        final ProcessStatusDAO processStatusDAO = new ProcessStatusDAO();
        _processStatusVO.setProcessID(p_processId);
        _processStatusVO.setProcessStatus(ProcessI.STATUS_COMPLETE);
        _processStatusVO.setStartDate(currentDate);
        try {
            updateCount = processStatusDAO.updateProcessDetailForMis(p_con, _processStatusVO);
        } catch (Exception e) {
            _logger.errorTrace(METHOD_NAME, e);
            if (_logger.isDebugEnabled()) {
                _logger.debug("markProcessStatusAsComplete", "Exception= " + e.getMessage());
            }
        } finally {
            if (_logger.isDebugEnabled()) {
                _logger.debug("markProcessStatusAsComplete", "Exiting: updateCount=" + updateCount);
            }
        } // end of finally
        return updateCount;

    }

    private List loadC2SRoamRechargeDetails(Connection con, Date p_fromDate, Date p_toDate, String networkCode) throws BTSLBaseException {
        final String METHOD_NAME = "loadC2SRoamRechargeDetails";
        if (_logger.isDebugEnabled()) {
            _logger.debug("loadC2SRoamRechargeDetails", "loading the details p_fromDate::"+p_fromDate+"p_toDate::"+p_toDate);
        }
        PreparedStatement psmt = null;
        ResultSet rs = null;
        final List _c2sRoamRechargeList = new ArrayList();

        MonthlyC2SRoamRechargeReportQry monthlyC2SQry = (MonthlyC2SRoamRechargeReportQry)ObjectProducer.getObject(QueryConstants.MONTHLY_C2SROAM_RECHARGE_REPORT_QRY, QueryConstants.QUERY_PRODUCER);
       
        String qry= monthlyC2SQry.loadC2SRoamRechargeDetails();
         _logger.debug("loadC2SRoamRechargeDetails Query is",qry);
        try {
            psmt = con.prepareStatement(qry);
            psmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
            psmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
            psmt.setString(3,networkCode);
            rs = psmt.executeQuery();
            RoamRechargeVO rechargeVO = null;
            while (rs.next()) {
                rechargeVO = new RoamRechargeVO();
                rechargeVO.setRetailerUniqueId(rs.getString("USER_ID"));
                rechargeVO.setRetailerName(rs.getString("USER_NAME"));
                rechargeVO.setRetailerMsisdn(rs.getString("MSISDN"));
                rechargeVO.setRetailerPenaltyAmount(rs.getLong("PENALTY"));
                rechargeVO.setDistributorPenaltyAmount(rs.getLong("OWNER_PENALTY"));
                rechargeVO.setDistributorMsisdn(rs.getString("OWNER_MSISDN"));
                rechargeVO.setDistributorName(rs.getString("OWNER_NAME"));
                rechargeVO.setDistributorUniqueId(rs.getString("OWNER_ID"));
                rechargeVO.setNetworkCode(rs.getString("NETWORK_CODE"));
                rechargeVO.setUserCategoryCode("CATEGORY_CODE");
                rechargeVO.setRoamRechargeAmount(rs.getLong("ROAM_AMOUNT"));
                rechargeVO.setOwnerCategoryCode(rs.getString("OWNER_CATEGORY"));
                rechargeVO.setOwnerNetworkCode(rs.getString("OWNER_NW"));
                rechargeVO.setTransferDate(rs.getDate("TRANS_DATE"));
                String serviceType=rs.getString("SERVICE_TYPE");
                if(serviceType.equals("RC") || serviceType.equals("RRC") )
                	rechargeVO.setServiceType("C2S Prepaid Recharge");
                else
                	rechargeVO.setServiceType("C2S Prepaid Recharge Reversal");
                _c2sRoamRechargeList.add(rechargeVO);
            }
        } catch (SQLException sqe) {
            _logger.error(METHOD_NAME, "SQLException : " + sqe);
            _logger.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "MonthlyC2SRoamRechargeReport[loadC2SRoamRechargeDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException("MonthlyC2SRoamRechargeReport", METHOD_NAME, "error.general.sql.processing");
        } catch (Exception ex) {
            _logger.error("", "Exception : " + ex);
            _logger.errorTrace(METHOD_NAME, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                "MonthlyC2SRoamRechargeReport[loadC2SRoamRechargeDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException("MonthlyC2SRoamRechargeReport", METHOD_NAME, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            try {
                if (psmt != null) {
                    psmt.close();
                }
            } catch (Exception e) {
                _logger.errorTrace(METHOD_NAME, e);
            }
            if (_logger.isDebugEnabled()) {
                _logger.debug(METHOD_NAME, "Exiting:  arrayList Size =" + _c2sRoamRechargeList.size());
            }
        }
        return _c2sRoamRechargeList;
    }

    public boolean writeToCSV(List _montlyReportsData, String networkCode) {
        final String methodName = "writeToCSV";
        boolean fileWrite = false;
        try {

            final String filePath = Constants.getProperty("MONTHLY_REPORT_FILE_PATH");
            if (!BTSLUtil.isNullString(filePath)) {
                final File fileDir = new File(filePath);
                if (!fileDir.isDirectory()) {
                    fileDir.mkdirs();
                }
            }
            if (_montlyReportsData.size() > 0) {
                final String fileName = getNextAutoReconnNumber()+"_"+networkCode+ "_Report.csv";
                fileWrite = createcsvfile(fileName, _montlyReportsData,networkCode);
                if (fileWrite == false) {
                    _logger.debug(methodName, " Unable to generate the file please contact adminstrator");
                }
            } else {
                fileWrite = false;
                _logger.debug(methodName, " No record found from recharges table for write to the file");
            }
        } catch (Exception e) {
        	_logger.error(methodName, "Exception " + e.getMessage());
        	_logger.errorTrace(methodName,e);
            fileWrite = false;
            _logger.debug(methodName, " Unable to Write to the file");
        }
        return fileWrite;
    }

    private String getNextAutoReconnNumber() {
        final String methodName = "getNextAutoReconnNumber";
        _logger.debug(methodName, " Entered");
        final SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyHHmmssSSS", Locale.ENGLISH);
        String autoReconnNo = "";
        String datePrefix = "";
        datePrefix = sdf.format(new Date());
        autoReconnNo = Constants.getProperty("MONTHLY_REPORT_FILE_PATH") + Constants.getProperty("MONTHLY_REPORT_ROAM_RECHARGE_PREFIX") + "_" + datePrefix;
        _logger.debug(methodName, " Exiting, autoReconnNo=" + autoReconnNo);
        return autoReconnNo;
    }

    private boolean createcsvfile(String fileName, List _monthlyReportsData, String networkCode) {
        final String methodName = "createcsvfile";
        _logger.debug(methodName, " Entered here====fileName=" + fileName);
        boolean flag = false;
        final StringBuilder filecontent = new StringBuilder();
        try {
            filecontent.append(BTSLUtil.getMessage(_locale, "dailyroam.report.summary.c2s.date", null));
            final String currentDate = BTSLDateUtil.getInstance().get(Calendar.DAY_OF_MONTH) + "/" + (BTSLDateUtil.getInstance().get(Calendar.MONTH) + 1) + "/" + String.valueOf(
                BTSLDateUtil.getInstance().get(Calendar.YEAR)).substring(2);
            filecontent.append(",");
            filecontent.append(currentDate);
            filecontent.append("\n");
            filecontent.append(BTSLUtil.getMessage(_locale, "dailyroam.report.summary.c2s.circle", null));
            filecontent.append(",");
            filecontent.append(networkCode);
            filecontent.append("\n");
            filecontent.append(BTSLUtil.getMessage(_locale, "dailyroam.report.summary.c2s.norec", null));
            filecontent.append(",");
            filecontent.append(String.valueOf(_monthlyReportsData.size()));
            filecontent.append("\n");
            filecontent.append(BTSLUtil.getMessage(_locale, "dailyroam.report.summary.c2s.service", null));
            filecontent.append(",");
            filecontent.append(BTSLUtil.getMessage(_locale, "dailyroam.report.summary.c2s.retailername", null));
            filecontent.append(",");
            filecontent.append(BTSLUtil.getMessage(_locale, "dailyroam.report.summary.c2s.retailermsisdn", null));
            filecontent.append(",");
            filecontent.append(BTSLUtil.getMessage(_locale, "dailyroam.report.summary.c2s.distributorname", null));
            filecontent.append(",");
            filecontent.append(BTSLUtil.getMessage(_locale, "dailyroam.report.summary.c2s.distributormsisdn", null));
            filecontent.append(",");
            filecontent.append(BTSLUtil.getMessage(_locale, "monthlyroam.report.summary.c2s.roamamount", null));
            filecontent.append(",");
            filecontent.append(BTSLUtil.getMessage(_locale, "monthlyroam.report.summary.c2s.retailerpenalty.amount", null));
            filecontent.append(",");
            filecontent.append(BTSLUtil.getMessage(_locale, "monthlyroam.report.summary.c2s.distributorpenalty.amount", null));
            filecontent.append("\n");

            for (int i = 0, j = _monthlyReportsData.size(); i < j; i++) {
                final RoamRechargeVO rechargeVO = (RoamRechargeVO) _monthlyReportsData.get(i);
                filecontent.append(BTSLUtil.NullToString(rechargeVO.getServiceType()));
                filecontent.append(",");                
                filecontent.append(BTSLUtil.NullToString(rechargeVO.getRetailerName()));
                filecontent.append(",");
                filecontent.append(BTSLUtil.NullToString(rechargeVO.getRetailerMsisdn()));
                filecontent.append(",");
                filecontent.append(BTSLUtil.NullToString(rechargeVO.getDistributorName()));
                filecontent.append(",");
                filecontent.append(BTSLUtil.NullToString(rechargeVO.getDistributorMsisdn()));
                ;
                filecontent.append(",");
                filecontent.append(PretupsBL.getDisplayAmount(rechargeVO.getRoamRechargeAmount()));
                filecontent.append(",");
                filecontent.append(PretupsBL.getDisplayAmount(rechargeVO.getRetailerPenaltyAmount()));
                filecontent.append(",");
                filecontent.append(PretupsBL.getDisplayAmount(rechargeVO.getDistributorPenaltyAmount()));
                filecontent.append("\n");
            }
            final RandomAccessFile raf = new RandomAccessFile(new File(fileName), "rw");
            raf.writeBytes(filecontent.toString());
            raf.close();
            flag = true;
            _logger.debug(methodName, " File write successfully, Exit here====fileName=" + fileName);
        } catch (IOException e) {// Catch exception if any
        	_logger.error(methodName, "IOException " + e.getMessage());
        	_logger.errorTrace(methodName,e);
            _logger.debug(methodName, "Exception " + e.getMessage());
            flag = false;
        } catch (Exception e) {
        	_logger.error(methodName, "Exception " + e.getMessage());
        	_logger.errorTrace(methodName,e);
            _logger.debug(methodName, "Exception " + e.getMessage());
            flag = false;
        }
        return flag;
    }
    
    private List loadNetworkDetails( Connection con) throws BTSLBaseException
    {
    	final String METHOD_NAME = "loadNetworkDetails";
        if (_logger.isDebugEnabled()) {
            _logger.debug("getNetworkDetails", "loading the network details");
        }    	
    	  PreparedStatement psmt = null;
          ResultSet rs = null;
          final List _networkList = new ArrayList();
          
          final StringBuilder strBuff = new StringBuilder("SELECT network_code from networks");
          _logger.debug(METHOD_NAME, " Query details is  are::  " + strBuff.toString());
          try {
              psmt = con.prepareStatement(strBuff.toString());
              rs = psmt.executeQuery();              
              while (rs.next()) {
            	  String networkCode= rs.getString("network_code");
            	  _networkList.add(networkCode);
            	  }              
          }
          catch (SQLException sqe) {
              _logger.error(METHOD_NAME, "SQLException : " + sqe);
              _logger.errorTrace(METHOD_NAME, sqe);
              EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                  "MonthlyC2SRoamRechargeReport[loadNetworkDetails]", "", "", "", "SQL Exception:" + sqe.getMessage());
              throw new BTSLBaseException("MonthlyC2SRoamRechargeReport", METHOD_NAME, "error.general.sql.processing");
          } catch (Exception ex) {
              _logger.error("", "Exception : " + ex);
              _logger.errorTrace(METHOD_NAME, ex);
              EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                  "MonthlyC2SRoamRechargeReport[loadNetworkDetails]", "", "", "", "Exception:" + ex.getMessage());
              throw new BTSLBaseException("MonthlyC2SRoamRechargeReport", METHOD_NAME, "error.general.processing");
          } finally {
              try {
                  if (rs != null) {
                      rs.close();
                  }
              } catch (SQLException ex) {
              	_logger.debug(METHOD_NAME, ex);
              } 
              catch (Exception e) {
                  _logger.errorTrace(METHOD_NAME, e);
              }
              try {
                  if (psmt != null) {
                      psmt.close();
                  }
              }catch (SQLException ex) {
                	_logger.debug(METHOD_NAME, ex);
                } 
              catch (Exception e) {
                  _logger.errorTrace(METHOD_NAME, e);
              }
              if (_logger.isDebugEnabled()) {
                  _logger.debug(METHOD_NAME, "Exiting:  arrayList Size =" + _networkList.size());
              }
          }          
          return _networkList;
    }

}


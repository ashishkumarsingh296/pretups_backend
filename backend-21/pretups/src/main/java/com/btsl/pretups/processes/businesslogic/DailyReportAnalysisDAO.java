package com.btsl.pretups.processes.businesslogic;

/**
 * @(#)DailyReportAnalysisDAO.java
 *                                 Copyright(c) 2006, Bharti Telesoft Ltd. All
 *                                 Rights Reserved
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Author Date History
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 *                                 Ved Prakash Sharma 20/09/2006 Initial
 *                                 Creation
 *                                 --------------------------------------------
 *                                 --
 *                                 --------------------------------------------
 *                                 -------
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;

/**
 * @author ved.sharma
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class DailyReportAnalysisDAO {
    private static final Log LOG = LogFactory.getLog(DailyReportAnalysisDAO.class.getName());
    private DailyReportAnalysisQry dailyReportAnalysisQry;
    private static final String SQL_EXCEPTION = "SQL EXCEPTION: ";
    private static final String EXCEPTION = "EXCEPTION: ";
    private static final String QUERY_KEY = "QUERY: ";
    
    /**
     * Created for Implementing Oracle and Postgres query support mechanism
     */
    public DailyReportAnalysisDAO(){
    	dailyReportAnalysisQry = (DailyReportAnalysisQry)ObjectProducer.getObject(QueryConstants.DAILY_REPORT_ANALYSIS, QueryConstants.QUERY_PRODUCER);
    }

    /**
     * method loadNetworkList
     * This method load the active network list.
     * 
     * @param p_con
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadNetworkList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadNetworkList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
        	LOG.debug(methodName, loggerValue);
        }
        ArrayList networkList = new ArrayList();
         
         
        try {
            StringBuffer qrySelect = new StringBuffer("SELECT network_code, network_name, network_short_name, company_name, report_header_name,");
            qrySelect.append("erp_network_code, address1, address2, city, state, zip_code, country, network_type, status, remarks, language_1_message, language_2_message,");
            qrySelect.append("text_1_value, text_2_value, country_prefix_code, mis_done_date, created_on, created_by, modified_on, modified_by, service_set_id ");
            qrySelect.append("FROM networks WHERE status=? ");
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(qrySelect);
    			LOG.debug(methodName, loggerValue);
    		}
            try(PreparedStatement prepSelect = p_con.prepareStatement(qrySelect.toString());)
            {
            prepSelect.setString(1, PretupsI.YES);
            try(ResultSet rs = prepSelect.executeQuery();)
            {
            NetworkVO networkVO = null;

            while (rs.next()) {
                networkVO = new NetworkVO();
                networkVO.setNetworkCode(rs.getString("network_code"));
                networkVO.setNetworkName(rs.getString("network_name"));
                networkList.add(networkVO);
            }
            }
            }
        } catch (SQLException sql) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sql.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadNetworkList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sql);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadNetworkList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadC2SFailRecharge", "error.general.processing",e);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: networkList.size():");
             	loggerValue.append(networkList.size());
             	LOG.debug(methodName, loggerValue);
             }
        }
        return networkList;
    }

    /**
     * Method for loading Products list on the basis of NetworkCode and Module
     * Code.
     * 
     * @author ved prakash
     * @param p_con
     *            java.sql.Connection
     * @param p_moduleCode
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadProductListByModuleCode(Connection p_con, String p_moduleCode) throws BTSLBaseException {
        final String methodName = "loadProductListByNetIdANDModuleCode";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_moduleCode=");
        	loggerValue.append(p_moduleCode);
        	LOG.debug(methodName, loggerValue);
        }

        ArrayList list = new ArrayList();
         
      

        StringBuffer strBuff = new StringBuffer(" SELECT P.product_code ,P.product_name FROM products P ");
        strBuff.append(" WHERE P.status =? and P.module_code=? ");
        String sqlSelect = strBuff.toString();
        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
           
            pstmt.setString(1, PretupsI.PRODUCT_STATUS);
            pstmt.setString(2, p_moduleCode);
            try( ResultSet rs = pstmt.executeQuery();)
            {
            DailyReportVO dailyReportVO = null;
            while (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setProductCode(rs.getString("product_code"));
                dailyReportVO.setProductName(rs.getString("product_name"));
                list.add(dailyReportVO);
            }
            return list;
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadProductListByNetIdANDModuleCode]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadProductListByNetIdANDModuleCode]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: Product.size():");
             	loggerValue.append(list.size());
             	LOG.debug(methodName, loggerValue);
             }
        }
    }

    /**
     * @param p_con
     * @param p_extInterface
     * @param p_module
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadServiceTypeList(Connection p_con, String p_extInterface, String p_module) throws BTSLBaseException {
        final String methodName = "loadServiceTypeList";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_extInterface=");
        	loggerValue.append(p_extInterface);
        	loggerValue.append("p_module=");
        	loggerValue.append(p_module);
        	LOG.debug(methodName, loggerValue);
        }
        ArrayList serviceList = new ArrayList();
         
         
        try {
            StringBuffer qrySelect = new StringBuffer("SELECT ST.service_type, ST.name FROM service_type ST ");
            qrySelect.append(" WHERE ST.module = ? AND ST.external_interface = ? AND ST.status=? AND ST.service_type<>'ADM'");
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(qrySelect);
    			LOG.debug(methodName, loggerValue);
    		}
            try(PreparedStatement prepSelect = p_con.prepareStatement(qrySelect.toString());)
            {
            prepSelect.setString(1, p_module);
            prepSelect.setString(2, p_extInterface);
            prepSelect.setString(3, PretupsI.YES);
           try(ResultSet rs = prepSelect.executeQuery();)
           {

            while (rs.next()) {
                serviceList.add(new ListValueVO(rs.getString("name"), rs.getString("service_type")));
            }
        } 
            }
        }catch (SQLException sql) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sql.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadServiceTypeList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sql);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadServiceTypeList]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadC2SFailRecharge", "error.general.processing",e);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: serviceList.size():");
             	loggerValue.append(serviceList.size());
             	LOG.debug(methodName, loggerValue);
             }
        }
        return serviceList;
    }

    /**
     * method loadC2SFailRecharge
     * This method load C2S failed transaction (daily and monthly) on the date
     * range
     * and netrwork and service allowed external interface basis.
     * 
     * @param p_con
     * @param p_fromDate
     * @param p_toDate
     * @param p_networkCode
     *            TODO
     * @param p_serviceCode
     *            TODO
     * @param p_services
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadC2SFailRecharge(Connection p_con, Date p_fromDate, Date p_toDate, String p_networkCode, String p_serviceCode) throws BTSLBaseException {
        final String methodName = "loadC2SFailRecharge";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_fromDate=");
        	loggerValue.append(p_fromDate);
        	loggerValue.append("p_toDate=");
        	loggerValue.append(p_toDate);
        	loggerValue.append("p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append("p_serviceCode=");
        	loggerValue.append(p_serviceCode);
        	LOG.debug(methodName, loggerValue);
        }
        ArrayList failRecharge = new ArrayList();
        
        try {
            StringBuilder qrySelect = dailyReportAnalysisQry.loadC2SFailRechargeQry();
            LogFactory.printLog(methodName, "Select qrySelect:" + qrySelect, LOG);
           try(PreparedStatement prepSelect = p_con.prepareStatement(qrySelect.toString());)
           {
            int i = 1;
            java.sql.Date fromDate = BTSLUtil.getSQLDateFromUtilDate(p_fromDate);
            java.sql.Date toDate = BTSLUtil.getSQLDateFromUtilDate(p_toDate);
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, fromDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsI.C2S_ERRCODE_VALUS);
            i++;
            prepSelect.setString(i, p_serviceCode);
            i++;
            prepSelect.setString(i, p_networkCode);
            i++;
            prepSelect.setString(i, p_networkCode);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_FAIL);
            i++;
            try(ResultSet rs = prepSelect.executeQuery();)
            {
            DailyReportVO dailyReportVO = null;

            while (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setNetworkCode(rs.getString("network_code"));
                dailyReportVO.setNetworkName(rs.getString("network_name"));
                dailyReportVO.setServiceTypeName(rs.getString("service_type_name"));
                dailyReportVO.setServiceType(rs.getString("service_type"));
                dailyReportVO.setErrorDesc(rs.getString("error_code"));
                dailyReportVO.setProductName(rs.getString("product_name"));
                dailyReportVO.setProductCode(rs.getString("product_code"));
                dailyReportVO.setMonthFailCount(rs.getLong("month_count"));
                dailyReportVO.setDailyFailCount(rs.getLong("day_count"));
                dailyReportVO.setMonthFailAmount(rs.getLong("month_amount"));
                dailyReportVO.setDailyFailAmount(rs.getLong("day_amount"));
                dailyReportVO.setMonthFailAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getMonthFailAmount()));
                dailyReportVO.setDailyFailAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailyFailAmount()));

                failRecharge.add(dailyReportVO);
            }
        } 
           }
        }catch (SQLException sql) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sql.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadC2SFailRecharge]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sql);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadC2SFailRecharge]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: failRecharge.size():");
             	loggerValue.append(failRecharge.size());
             	LOG.debug(methodName, loggerValue);
             }
            
        }
        return failRecharge;
    }

    /**
     * method loadTotalC2SRecharge
     * This method load summary of C2S success transaction (daily and monthly)
     * on
     * the date range and netrwork and service allowed external interface basis.
     * 
     * @param p_con
     * @param p_fromDate
     * @param p_toDate
     * @param p_networkCode
     *            TODO
     * @param p_services
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadTotalC2SRecharge(Connection p_con, Date p_fromDate, Date p_toDate, String p_networkCode, String p_services) throws BTSLBaseException {
        final String methodName = "loadTotalC2SRecharge";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_fromDate=");
        	loggerValue.append(p_fromDate);
        	loggerValue.append("p_toDate=");
        	loggerValue.append(p_toDate);
        	loggerValue.append("p_networkCode=");
        	loggerValue.append(p_networkCode);
        	LOG.debug(methodName, loggerValue);
        }
        ArrayList rechargeList = new ArrayList();
        
         
        try {
        	
        	StringBuilder qrySelect = dailyReportAnalysisQry.loadTotalC2SRechargeQry();
            LogFactory.printLog(methodName, "Select qrySelect:" + qrySelect, LOG);
            try(PreparedStatement prepSelect = p_con.prepareStatement(qrySelect.toString());)
            {
            int i = 1;
            java.sql.Date fromDate = BTSLUtil.getSQLDateFromUtilDate(p_fromDate);
            java.sql.Date toDate = BTSLUtil.getSQLDateFromUtilDate(p_toDate);
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, fromDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, p_networkCode);
            i++;
            prepSelect.setString(i, p_networkCode);
            i++;
            prepSelect.setString(i, p_services);
            i++;
            try(ResultSet rs = prepSelect.executeQuery();)
            {
            DailyReportVO dailyReportVO = null;
            // ArrayList productList = new ArrayList();
            while (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setNetworkCode(rs.getString("network_code"));
                dailyReportVO.setNetworkName(rs.getString("network_name"));
                dailyReportVO.setServiceType(rs.getString("service_type"));
                dailyReportVO.setServiceTypeName(rs.getString("service_type_name"));
                dailyReportVO.setMonthTotalCount(rs.getLong("monthly_total_count"));
                dailyReportVO.setDailyTotalCount(rs.getLong("daily_total_count"));
                dailyReportVO.setMonthSuccessCount(rs.getLong("monthly_success_count"));
                dailyReportVO.setDailySuccessCount(rs.getLong("daily_success_count"));
                rechargeList.add(dailyReportVO);
            }
        } 
            }
        }catch (SQLException sql) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sql.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadTotalC2SRecharge]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sql);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadTotalC2SRecharge]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: rechargeList.size():");
             	loggerValue.append(rechargeList.size());
             	LOG.debug(methodName, loggerValue);
             }
        }
        return rechargeList;
    }

    /**
     * method loadC2SRecevierRequest
     * This method load C2S services count (daily and monthly) on
     * the date range and netrwork and service not allowed external interface
     * basis.
     * 
     * @param p_con
     * @param p_fromDate
     * @param p_toDate
     * @param p_networkCode
     *            TODO
     * @param p_service
     *            TODO
     * @param p_services
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadC2SRecevierRequest(Connection p_con, Date p_fromDate, Date p_toDate, String p_networkCode, String p_service) throws BTSLBaseException {
        final String methodName = "loadC2SRecevierRequest";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_fromDate=");
        	loggerValue.append(p_fromDate);
        	loggerValue.append("p_toDate=");
        	loggerValue.append(p_toDate);
        	loggerValue.append("p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append("p_service=");
        	loggerValue.append(p_service);
        	LOG.debug(methodName, loggerValue);
        }
        ArrayList totalRequest = new ArrayList();
        PreparedStatement prepSelect = null;
        ResultSet rs = null;
        try {
        	prepSelect = dailyReportAnalysisQry.loadC2SRecevierRequestQry(p_con,p_fromDate, p_toDate, p_networkCode, p_service );
            
            rs = prepSelect.executeQuery();
            DailyReportVO dailyReportVO = null;
            while (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setNetworkCode(rs.getString("network_code"));
                dailyReportVO.setNetworkName(rs.getString("network_name"));
                dailyReportVO.setServiceType(rs.getString("service_type"));
                dailyReportVO.setServiceTypeName(rs.getString("service_type_name"));
                dailyReportVO.setErrorDesc(rs.getString("error_code"));
                dailyReportVO.setMonthTotalCount(rs.getLong("month_count"));
                dailyReportVO.setDailyTotalCount(rs.getLong("day_count"));
                totalRequest.add(dailyReportVO);
            }
        } catch (SQLException sql) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sql.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadC2SRecevierRequest]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sql);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadC2SRecevierRequest]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: totalRequest.size():");
             	loggerValue.append(totalRequest.size());
             	LOG.debug(methodName, loggerValue);
             }
            try {
                if (rs != null) {
                    rs.close();
                }
                if (prepSelect != null) {
                    prepSelect.close();
                }
            } catch (SQLException e1) {
                LOG.errorTrace(methodName, e1);
            }
        }
        return totalRequest;
    }

    /**
     * method loadTotalC2SRecevierRequest
     * This method load summary C2S services count (daily and monthly) on
     * the date range and netrwork and service not allowed external interface
     * basis.
     * 
     * @param p_con
     * @param p_fromDate
     * @param p_toDate
     * @param p_networkCode
     *            TODO
     * @param p_services
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadTotalC2SRecevierRequest(Connection p_con, Date p_fromDate, Date p_toDate, String p_networkCode, String p_services) throws BTSLBaseException {
        final String methodName = "loadTotalC2SRecevierRequest";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_fromDate=");
        	loggerValue.append(p_fromDate);
        	loggerValue.append("p_toDate=");
        	loggerValue.append(p_toDate);
        	loggerValue.append("p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append("p_services=");
        	loggerValue.append(p_services);
        	LOG.debug(methodName, loggerValue);
        }
        ArrayList totalRequest = new ArrayList();
       
        
        try {
        	StringBuilder qrySelect = dailyReportAnalysisQry.loadTotalC2SRecevierRequestQry();
            
            LogFactory.printLog(methodName, "Select qrySelect:" + qrySelect, LOG);
            try( PreparedStatement prepSelect = p_con.prepareStatement(qrySelect.toString());)
            {
            int i = 1;
            java.sql.Date fromDate = BTSLUtil.getSQLDateFromUtilDate(p_fromDate);
            java.sql.Date toDate = BTSLUtil.getSQLDateFromUtilDate(p_toDate);
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, p_services);
            i++;
            prepSelect.setString(i, p_networkCode);
            i++;
            prepSelect.setString(i, p_networkCode);
            i++;
            prepSelect.setDate(i, fromDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            try(ResultSet rs = prepSelect.executeQuery();)
            {
            DailyReportVO dailyReportVO = null;
            while (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setNetworkCode(rs.getString("network_code"));
                dailyReportVO.setNetworkName(rs.getString("network_name"));
                dailyReportVO.setServiceType(rs.getString("service_type"));
                dailyReportVO.setServiceTypeName(rs.getString("service_type_name"));
                dailyReportVO.setMonthTotalCount(rs.getLong("total_month_count"));
                dailyReportVO.setDailyTotalCount(rs.getLong("total_day_count"));
                dailyReportVO.setMonthSuccessCount(rs.getLong("success_month_count"));
                dailyReportVO.setDailySuccessCount(rs.getLong("success_day_count"));
                dailyReportVO.setMonthFailCount(rs.getLong("fail_month_count"));
                dailyReportVO.setDailyFailCount(rs.getLong("fail_day_count"));
                totalRequest.add(dailyReportVO);
            }
        }
            }
        }catch (SQLException sql) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sql.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadTotalC2SRecevierRequest]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sql);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadTotalC2SRecevierRequest]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: totalRequest.size():");
             	loggerValue.append(totalRequest.size());
             	LOG.debug(methodName, loggerValue);
             }
        }
        return totalRequest;
    }

    /**
     * method loadP2PFailRecharge
     * This method load P2P failed transaction (daily and monthly) on
     * the date range and netrwork and service allowed external interface basis.
     * 
     * @param p_con
     * @param p_fromDate
     * @param p_toDate
     * @param p_networkCode
     *            TODO
     * @param p_serviceType
     *            TODO
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadP2PFailRecharge(Connection p_con, Date p_fromDate, Date p_toDate, String p_networkCode, String p_serviceType) throws BTSLBaseException {
        final String methodName = "loadP2PFailRecharge";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_fromDate=");
        	loggerValue.append(p_fromDate);
        	loggerValue.append("p_toDate=");
        	loggerValue.append(p_toDate);
        	loggerValue.append("p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append("p_serviceType=");
        	loggerValue.append(p_serviceType);
        	LOG.debug(methodName, loggerValue);
        }
        ArrayList failRecharge = new ArrayList();
         
        ResultSet rs = null;
        try {
            StringBuilder qrySelect = dailyReportAnalysisQry.loadP2PFailRechargeQry();
            LogFactory.printLog(methodName, "Select qrySelect:" + qrySelect, LOG);
           try(PreparedStatement prepSelect = p_con.prepareStatement(qrySelect.toString());)
           {
            int i = 1;
            java.sql.Date fromDate = BTSLUtil.getSQLDateFromUtilDate(p_fromDate);
            java.sql.Date toDate = BTSLUtil.getSQLDateFromUtilDate(p_toDate);
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsI.P2P_ERRCODE_VALUS);
            i++;
            prepSelect.setString(i, p_serviceType);
            i++;
            prepSelect.setString(i, p_networkCode);
            i++;
            prepSelect.setString(i, p_networkCode);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_FAIL);
            i++;
            prepSelect.setDate(i, fromDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            rs = prepSelect.executeQuery();
            DailyReportVO dailyReportVO = null;
            while (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setNetworkCode(rs.getString("network_code"));
                dailyReportVO.setNetworkName(rs.getString("network_name"));
                dailyReportVO.setServiceType(rs.getString("service_type"));
                dailyReportVO.setServiceTypeName(rs.getString("service_type_name"));
                dailyReportVO.setErrorDesc(rs.getString("error_code"));
                dailyReportVO.setProductName(rs.getString("product_name"));
                dailyReportVO.setProductCode(rs.getString("product_code"));
                dailyReportVO.setMonthFailCount(rs.getLong("month_count"));
                dailyReportVO.setDailyFailCount(rs.getLong("day_count"));
                dailyReportVO.setMonthFailAmountStr(rs.getString("month_amount"));
                dailyReportVO.setDailyFailAmountStr(rs.getString("day_amount"));
                // dailyReportVO.setMonthFailAmount(rs.getLong("month_amount"));
                // dailyReportVO.setDailyFailAmount(rs.getLong("day_amount"));
                // dailyReportVO.setMonthFailAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getMonthFailAmount()));
                // dailyReportVO.setDailyFailAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailyFailAmount()));
                failRecharge.add(dailyReportVO);
            }
        } 
        }catch (SQLException sql) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sql.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadP2PFailRecharge]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sql);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadP2PFailRecharge]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: failRecharge.size():");
             	loggerValue.append(failRecharge.size());
             	LOG.debug(methodName, loggerValue);
             }
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		LOG.error("An error occurred closing result set.", e);
        	}
          
        }
        return failRecharge;
    }

    /**
     * method loadP2PRecevierRequest
     * This method load P2P service count (daily and monthly) on
     * the date range and netrwork and service not allowed external interface
     * basis.
     * 
     * @param p_con
     * @param p_fromDate
     * @param p_toDate
     * @param p_networkCode
     *            TODO
     * @param p_service
     *            TODO
     * @param p_services
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadP2PRecevierRequest(Connection p_con, Date p_fromDate, Date p_toDate, String p_networkCode, String p_service) throws BTSLBaseException {
        final String methodName = "loadP2PRecevierRequest";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_fromDate=");
        	loggerValue.append(p_fromDate);
        	loggerValue.append("p_toDate=");
        	loggerValue.append(p_toDate);
        	loggerValue.append("p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append("p_service=");
        	loggerValue.append(p_service);
        	LOG.debug(methodName, loggerValue);
        }
        ArrayList totalRequest = new ArrayList();
        PreparedStatement prepSelect = null;
        ResultSet rs = null;
        try {
        	prepSelect = dailyReportAnalysisQry.loadP2PRecevierRequestQry(p_con, p_fromDate, p_toDate, p_networkCode, p_service);
            
            rs = prepSelect.executeQuery();
            DailyReportVO dailyReportVO = null;
            while (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setNetworkCode(rs.getString("network_code"));
                dailyReportVO.setNetworkName(rs.getString("network_name"));
                dailyReportVO.setServiceType(rs.getString("service_type"));
                dailyReportVO.setServiceTypeName(rs.getString("service_type_name"));
                dailyReportVO.setErrorDesc(rs.getString("error_code"));
                dailyReportVO.setMonthTotalCount(rs.getLong("month_count"));
                dailyReportVO.setDailyTotalCount(rs.getLong("day_count"));
                totalRequest.add(dailyReportVO);
            }
        } catch (SQLException sql) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sql.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadP2PRecevierRequest]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sql);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadP2PRecevierRequest]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: totalRequest.size():");
             	loggerValue.append(totalRequest.size());
             	LOG.debug(methodName, loggerValue);
             }
            try {
                if (rs != null) {
                    rs.close();
                }
                if (prepSelect != null) {
                    prepSelect.close();
                }
            } catch (SQLException e1) {
                LOG.errorTrace(methodName, e1);
            }
        }
        return totalRequest;
    }

    /**
     * method loadTotalP2PRecevierRequest
     * This method load summary P2P (daily and monthly) on
     * the date range and netrwork and service not allowed external interface
     * basis.
     * 
     * @param p_con
     * @param p_fromDate
     * @param p_toDate
     * @param p_networkCode
     *            TODO
     * @param p_services
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadTotalP2PRecevierRequest(Connection p_con, Date p_fromDate, Date p_toDate, String p_networkCode, String p_services) throws BTSLBaseException {
        final String methodName = "loadTotalP2PRecevierRequest";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_fromDate=");
        	loggerValue.append(p_fromDate);
        	loggerValue.append("p_toDate=");
        	loggerValue.append(p_toDate);
        	loggerValue.append("p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append("p_services=");
        	loggerValue.append(p_services);
        	LOG.debug(methodName, loggerValue);
        }
        ArrayList totalRequest = new ArrayList();
        
        
        try {
        	StringBuilder qrySelect = dailyReportAnalysisQry.loadTotalP2PRecevierRequestQry();
            
            LogFactory.printLog(methodName, "Select qrySelect:" + qrySelect, LOG);
            try(PreparedStatement prepSelect = p_con.prepareStatement(qrySelect.toString());)
            {
            int i = 1;
            java.sql.Date fromDate = BTSLUtil.getSQLDateFromUtilDate(p_fromDate);
            java.sql.Date toDate = BTSLUtil.getSQLDateFromUtilDate(p_toDate);
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, p_services);
            i++;
            prepSelect.setString(i, p_networkCode);
            i++;
            prepSelect.setString(i, p_networkCode);
            i++;
            prepSelect.setDate(i, fromDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            try(ResultSet rs = prepSelect.executeQuery();)
            {
            DailyReportVO dailyReportVO = null;
            while (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setNetworkCode(rs.getString("network_code"));
                dailyReportVO.setNetworkName(rs.getString("network_name"));
                dailyReportVO.setServiceType(rs.getString("service_type"));
                dailyReportVO.setServiceTypeName(rs.getString("service_type_name"));
                dailyReportVO.setMonthTotalCount(rs.getLong("total_month_count"));
                dailyReportVO.setDailyTotalCount(rs.getLong("total_day_count"));
                dailyReportVO.setMonthSuccessCount(rs.getLong("success_month_count"));
                dailyReportVO.setDailySuccessCount(rs.getLong("success_day_count"));
                dailyReportVO.setMonthFailCount(rs.getLong("fail_month_count"));
                dailyReportVO.setDailyFailCount(rs.getLong("fail_day_count"));
                totalRequest.add(dailyReportVO);
            }
        }
            }
        }catch (SQLException sql) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sql.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadTotalP2PRecevierRequest]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sql);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadTotalP2PRecevierRequest]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: totalRequest.size():");
             	loggerValue.append(totalRequest.size());
             	LOG.debug(methodName, loggerValue);
             }   
        }
        return totalRequest;
    }

    /**
     * method loadTotalP2PRecharge
     * This method load summary P2P success transaction (daily and monthly) on
     * the date range and netrwork and service allowed external interface basis.
     * 
     * @param p_con
     * @param p_fromDate
     * @param p_toDate
     * @param p_networkCode
     *            TODO
     * @param p_services
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadTotalP2PRecharge(Connection p_con, Date p_fromDate, Date p_toDate, String p_networkCode, String p_services) throws BTSLBaseException {
        final String methodName = "loadTotalP2PRecharge";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_fromDate=");
        	loggerValue.append(p_fromDate);
        	loggerValue.append("p_toDate=");
        	loggerValue.append(p_toDate);
        	loggerValue.append("p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append("p_services=");
        	loggerValue.append(p_services);
        	LOG.debug(methodName, loggerValue);
        }
        ArrayList rechargeList = new ArrayList();
        
       
        try {
        	StringBuilder qrySelect = dailyReportAnalysisQry.loadTotalP2PRechargeQry();
            LogFactory.printLog(methodName, "Select qrySelect:" + qrySelect, LOG);
            try(PreparedStatement prepSelect = p_con.prepareStatement(qrySelect.toString());)
            {
            int i = 1;
            java.sql.Date fromDate = BTSLUtil.getSQLDateFromUtilDate(p_fromDate);
            java.sql.Date toDate = BTSLUtil.getSQLDateFromUtilDate(p_toDate);
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, p_networkCode);
            i++;
            prepSelect.setString(i, p_networkCode);
            i++;
            prepSelect.setString(i, p_services);
            i++;
            prepSelect.setDate(i, fromDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            try(ResultSet rs = prepSelect.executeQuery();)
            {
            DailyReportVO dailyReportVO = null;
            // ArrayList productList = new ArrayList();
            while (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setNetworkCode(rs.getString("network_code"));
                dailyReportVO.setNetworkName(rs.getString("network_name"));
                dailyReportVO.setServiceType(rs.getString("service_type"));
                dailyReportVO.setServiceTypeName(rs.getString("service_type_name"));
                dailyReportVO.setMonthTotalCount(rs.getLong("monthly_total_count"));
                dailyReportVO.setDailyTotalCount(rs.getLong("daily_total_count"));
                dailyReportVO.setMonthSuccessCount(rs.getLong("monthly_success_count"));
                dailyReportVO.setDailySuccessCount(rs.getLong("daily_success_count"));
                rechargeList.add(dailyReportVO);
            }
        }
            }
        }
        catch (SQLException sql) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sql.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadTotalP2PRecharge]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sql);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadTotalP2PRecharge]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: rechargeList.size():");
             	loggerValue.append(rechargeList.size());
             	LOG.debug(methodName, loggerValue);
             }
        }
        return rechargeList;
    }

    /**
     * method loadC2SRechargeHourly
     * This method load C2S success transaction (hourly) on
     * the date and netrwork and service allowed external interface basis.
     * 
     * @param p_con
     * @param p_date
     * @param p_networkCode
     * @param p_services
     *            TODO
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadC2SRechargeHourly(Connection p_con, Date p_date, String p_networkCode, String p_services) throws BTSLBaseException {
    	//local_index_implemented
        final String methodName = "loadC2SRechargeHourly";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_date=");
        	loggerValue.append(p_date);
        	loggerValue.append("p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append("p_services=");
        	loggerValue.append(p_services);
        	LOG.debug(methodName, loggerValue);
        }
        ArrayList rechargeList = new ArrayList();
         
       
        try {
            String dateStr = BTSLUtil.getDateStringFromDate(p_date);
            String format = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT));
            if (BTSLUtil.isNullString(format)) {
                format = PretupsI.DATE_FORMAT;
            }

            StringBuffer qrySelect = new StringBuffer("SELECT ");
            qrySelect.append("SUM(CASE CT.transfer_date WHEN TO_DATE('" + dateStr + "','" + format + "')  THEN 1 ELSE 0 END ) total_count, ");
            qrySelect.append("SUM(CASE CT.transfer_status WHEN '" + PretupsErrorCodesI.TXN_STATUS_SUCCESS + "' THEN (CASE CT.transfer_date WHEN TO_DATE('" + dateStr + "','" + format + "')THEN 1 ELSE 0 END ) ELSE 0 END) success_count, ");
            String count = "";
            String count1 = "";
            format = format + " hh24:mi:ss";
            for (int i = 0; i < 24; i++) {
                if (i < 10) {
                    count = "0" + i;
                } else {
                    count = "" + i;
                }
                if ((i + 1) < 10) {
                    count1 = "0" + (i + 1);
                } else if (i == 23) {
                    count1 = "" + (i);
                } else {
                    count1 = "" + (i + 1);
                }
                if (i < 23) {
                    qrySelect.append("SUM(CASE WHEN ((CT.transfer_date_time >= TO_DATE('" + dateStr + " " + count + ":00:01', '" + format + "')) AND (CT.transfer_date_time<=TO_DATE('" + dateStr + " " + count1 + ":00:00', '" + format + "'))) THEN 1 ELSE 0 END ) total_count" + (i + 1) + ", ");
                    qrySelect.append("SUM(CASE CT.transfer_status WHEN '" + PretupsErrorCodesI.TXN_STATUS_SUCCESS + "' THEN (CASE WHEN ((CT.transfer_date_time >= TO_DATE('" + dateStr + " " + count + ":00:01', '" + format + "')) AND (CT.transfer_date_time<=TO_DATE('" + dateStr + " " + count1 + ":00:00' ,'" + format + "'))) THEN 1 ELSE 0 END ) ELSE 0 END) success_count" + (i + 1) + ", ");
                } else {
                    qrySelect.append("SUM(CASE WHEN ((CT.transfer_date_time >= TO_DATE('" + dateStr + " " + count + ":00:01', '" + format + "')) AND (CT.transfer_date_time<=TO_DATE('" + dateStr + " " + count1 + ":59:59', '" + format + "'))) THEN 1 ELSE 0 END ) total_count" + (i + 1) + ", ");
                    qrySelect.append("SUM(CASE CT.transfer_status WHEN '" + PretupsErrorCodesI.TXN_STATUS_SUCCESS + "' THEN (CASE WHEN ((CT.transfer_date_time >= TO_DATE('" + dateStr + " " + count + ":00:01', '" + format + "')) AND (CT.transfer_date_time<=TO_DATE('" + dateStr + " " + count1 + ":59:59' ,'" + format + "'))) THEN 1 ELSE 0 END ) ELSE 0 END) success_count" + (i + 1) + ", ");
                }
            }
            qrySelect.append(" CT.network_code, N.network_name, CT.service_type,ST.name service_type_name ");
            qrySelect.append(" FROM c2s_transfers CT, service_type ST, networks N ");
            qrySelect.append("WHERE CT.transfer_date=? AND CT.service_type=ST.service_type AND CT.network_code=N.network_code ");
            qrySelect.append("AND CT.service_type=? ");
            qrySelect.append("AND CT.network_code=CASE ?  WHEN '" + PretupsI.ALL + "' THEN CT.network_code ELSE ?  END ");
            qrySelect.append(" GROUP BY CT.network_code, N.network_name, ST.name,CT.service_type ");

            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(qrySelect);
    			LOG.debug(methodName, loggerValue);
    		}
            try(PreparedStatement prepSelect = p_con.prepareStatement(qrySelect.toString());)
            {
            int i = 1;
            java.sql.Date date = BTSLUtil.getSQLDateFromUtilDate(p_date);
            
            prepSelect.setDate(i, date);
            i++;
            prepSelect.setString(i, p_services);
            i++;
            prepSelect.setString(i, p_networkCode);
            i++;
            prepSelect.setString(i, p_networkCode);
            i++;
            try(ResultSet rs = prepSelect.executeQuery();)
            {
            DailyReportVO dailyReportVO = null;
            // ArrayList productList = new ArrayList();
            while (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setNetworkCode(rs.getString("network_code"));
                dailyReportVO.setNetworkName(rs.getString("network_name"));
                dailyReportVO.setServiceType(rs.getString("service_type"));
                dailyReportVO.setServiceTypeName(rs.getString("service_type_name"));
                dailyReportVO.setDailySuccessCount(rs.getLong("success_count"));
                dailyReportVO.setDailyTotalCount(rs.getLong("total_count"));

                for (int k = 0; k < 24; k++) {
                    dailyReportVO.setDailySuccessCount(k, rs.getLong("success_count" + (k + 1)));
                    dailyReportVO.setDailyTotalCount(k, rs.getLong("total_count" + (k + 1)));
                }
                rechargeList.add(dailyReportVO);
            }
        } 
        }
        }catch (SQLException sql) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sql.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadC2SRechargeHourly]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sql);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadC2SRechargeHourly]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: rechargeList.size():");
             	loggerValue.append(rechargeList.size());
             	LOG.debug(methodName, loggerValue);
             }
        }
        return rechargeList;
    }

    /**
     * method loadP2PRechargeHourly
     * This method load P2P success transaction (hourly) on
     * the date and netrwork and service allowed external interface basis.
     * 
     * @param p_con
     * @param p_date
     * @param p_networkCode
     * @param p_services
     *            TODO
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadP2PRechargeHourly(Connection p_con, Date p_date, String p_networkCode, String p_services) throws BTSLBaseException {
        final String methodName = "loadP2PRechargeHourly";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_date=");
        	loggerValue.append(p_date);
        	loggerValue.append("p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append("p_services=");
        	loggerValue.append(p_services);
        	LOG.debug(methodName, loggerValue);
        }
        ArrayList rechargeList = new ArrayList();
         
        
        try {
            String dateStr = BTSLUtil.getDateStringFromDate(p_date);
            String format = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.SYSTEM_DATE_FORMAT));
            if (BTSLUtil.isNullString(format)) {
                format = PretupsI.DATE_FORMAT;
            }

            StringBuffer qrySelect = new StringBuffer("SELECT ");
            qrySelect.append("SUM(CASE CT.transfer_date WHEN TO_DATE('" + dateStr + "','" + format + "')  THEN 1 ELSE 0 END ) total_count, ");
            qrySelect.append("SUM(CASE CT.transfer_status WHEN '" + PretupsErrorCodesI.TXN_STATUS_SUCCESS + "' THEN (CASE CT.transfer_date WHEN TO_DATE('" + dateStr + "','" + format + "')THEN 1 ELSE 0 END ) ELSE 0 END) success_count, ");
            String count = "";
            String count1 = "";
            format = format + " hh24:mi:ss";
            for (int i = 0; i < 24; i++) {
                if (i < 10) {
                    count = "0" + i;
                } else {
                    count = "" + i;
                }
                if ((i + 1) < 10) {
                    count1 = "0" + (i + 1);
                } else if (i == 23) {
                    count1 = "" + (i);
                } else {
                    count1 = "" + (i + 1);
                }

                if (i < 23) {
                    qrySelect.append("SUM(CASE WHEN ((CT.transfer_date_time >= TO_DATE('" + dateStr + " " + count + ":00:01', '" + format + "')) AND (CT.transfer_date_time<=TO_DATE('" + dateStr + " " + count1 + ":00:00', '" + format + "'))) THEN 1 ELSE 0 END ) total_count" + (i + 1) + ", ");
                    qrySelect.append("SUM(CASE CT.transfer_status WHEN '" + PretupsErrorCodesI.TXN_STATUS_SUCCESS + "' THEN (CASE WHEN ((CT.transfer_date_time >= TO_DATE('" + dateStr + " " + count + ":00:01', '" + format + "')) AND (CT.transfer_date_time<=TO_DATE('" + dateStr + " " + count1 + ":00:00' ,'" + format + "'))) THEN 1 ELSE 0 END ) ELSE 0 END) success_count" + (i + 1) + ", ");
                } else {
                    qrySelect.append("SUM(CASE WHEN ((CT.transfer_date_time >= TO_DATE('" + dateStr + " " + count + ":00:01', '" + format + "')) AND (CT.transfer_date_time<=TO_DATE('" + dateStr + " " + count1 + ":59:59', '" + format + "'))) THEN 1 ELSE 0 END ) total_count" + (i + 1) + ", ");
                    qrySelect.append("SUM(CASE CT.transfer_status WHEN '" + PretupsErrorCodesI.TXN_STATUS_SUCCESS + "' THEN (CASE WHEN ((CT.transfer_date_time >= TO_DATE('" + dateStr + " " + count + ":00:01', '" + format + "')) AND (CT.transfer_date_time<=TO_DATE('" + dateStr + " " + count1 + ":59:59' ,'" + format + "'))) THEN 1 ELSE 0 END ) ELSE 0 END) success_count" + (i + 1) + ", ");
                }
            }
            qrySelect.append(" CT.network_code, N.network_name, CT.service_type,ST.name service_type_name ");
            qrySelect.append(" FROM subscriber_transfers CT, service_type ST, networks N ");
            qrySelect.append("WHERE  CT.service_type=ST.service_type AND CT.network_code=N.network_code ");
            qrySelect.append("AND CT.service_type=? ");
            qrySelect.append("AND CT.network_code=CASE ?  WHEN '" + PretupsI.ALL + "' THEN CT.network_code ELSE ?  END ");
            qrySelect.append("AND CT.transfer_date=? GROUP BY CT.network_code, N.network_name, ST.name,CT.service_type ");
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(qrySelect);
    			LOG.debug(methodName, loggerValue);
    		}
            try(PreparedStatement prepSelect = p_con.prepareStatement(qrySelect.toString());)
            {
            int i = 1;
            java.sql.Date date = BTSLUtil.getSQLDateFromUtilDate(p_date);
            prepSelect.setString(i, p_services);
            i++;
            prepSelect.setString(i, p_networkCode);
            i++;
            prepSelect.setString(i, p_networkCode);
            i++;
            prepSelect.setDate(i, date);
            i++;
            try(ResultSet rs = prepSelect.executeQuery();)
            {
            DailyReportVO dailyReportVO = null;
            // ArrayList productList = new ArrayList();
            while (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setNetworkCode(rs.getString("network_code"));
                dailyReportVO.setNetworkName(rs.getString("network_name"));
                dailyReportVO.setServiceType(rs.getString("service_type"));
                dailyReportVO.setServiceTypeName(rs.getString("service_type_name"));
                dailyReportVO.setDailySuccessCount(rs.getLong("success_count"));
                dailyReportVO.setDailyTotalCount(rs.getLong("total_count"));

                for (int k = 0; k < 24; k++) {
                    dailyReportVO.setDailySuccessCount(k, rs.getLong("success_count" + (k + 1)));
                    dailyReportVO.setDailyTotalCount(k, rs.getLong("total_count" + (k + 1)));
                }
                rechargeList.add(dailyReportVO);
            }
        }
            }
        }catch (SQLException sql) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sql.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadP2PRechargeHourly]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sql);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadP2PRechargeHourly]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: rechargeList.size():");
             	loggerValue.append(rechargeList.size());
             	LOG.debug(methodName, loggerValue);
             }
        }
        return rechargeList;
    }

    /**
     * method loadC2SReceiverRequestHourly
     * This method load C2S success services count (hourly) on
     * the date and netrwork and service not allowed external interface basis.
     * 
     * @param p_con
     * @param p_date
     * @param p_networkCode
     * @param p_services
     *            TODO
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadC2SReceiverRequestHourly(Connection p_con, Date p_date, String p_networkCode, String p_services) throws BTSLBaseException {
        final String methodName = "loadC2SReceiverRequestHourly";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_date=");
        	loggerValue.append(p_date);
        	loggerValue.append("p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append("p_services=");
        	loggerValue.append(p_services);
        	LOG.debug(methodName, loggerValue);
        }
        ArrayList rechargeList = new ArrayList();
        try {
            
            StringBuilder qrySelect = dailyReportAnalysisQry.loadC2SReceiverRequestHourlyQry(p_date);

            LogFactory.printLog(methodName, "Select qrySelect:" + qrySelect, LOG);
            try(PreparedStatement prepSelect = p_con.prepareStatement(qrySelect.toString());)
            {
            int i = 1;
            java.sql.Date date = BTSLUtil.getSQLDateFromUtilDate(p_date);

            prepSelect.setString(i, p_services);
            i++;
            prepSelect.setString(i, p_networkCode);
            i++;
            prepSelect.setString(i, p_networkCode);
            i++;
            prepSelect.setDate(i, date);
            i++;
            try(ResultSet rs = prepSelect.executeQuery();)
            {
            DailyReportVO dailyReportVO = null;
            // ArrayList productList = new ArrayList();
            while (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setNetworkCode(rs.getString("network_code"));
                dailyReportVO.setNetworkName(rs.getString("network_name"));
                dailyReportVO.setServiceType(rs.getString("service_type"));
                dailyReportVO.setServiceTypeName(rs.getString("service_type_name"));
                dailyReportVO.setDailySuccessCount(rs.getLong("success_count"));
                dailyReportVO.setDailyTotalCount(rs.getLong("total_count"));
                for (int k = 0; k < 24; k++) {
                    dailyReportVO.setDailySuccessCount(k, rs.getLong("success_count" + (k + 1)));
                    dailyReportVO.setDailyTotalCount(k, rs.getLong("total_count" + (k + 1)));
                }
                rechargeList.add(dailyReportVO);
            }
        } 
            }
        }catch (SQLException sql) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sql.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadC2SReceiverRequestHourly]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sql);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadC2SReceiverRequestHourly]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: rechargeList.size():");
             	loggerValue.append(rechargeList.size());
             	LOG.debug(methodName, loggerValue);
             }
          
        }
        return rechargeList;
    }

    /**
     * method loadP2PReceiverRequestHourly
     * This method load C2S success services count (hourly) on
     * the date and netrwork and service not allowed external interface basis.
     * 
     * @param p_con
     * @param p_date
     * @param p_networkCode
     * @param p_services
     *            TODO
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadP2PReceiverRequestHourly(Connection p_con, Date p_date, String p_networkCode, String p_services) throws BTSLBaseException {
        final String methodName = "loadP2PReceiverRequestHourly";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_date=");
        	loggerValue.append(p_date);
        	loggerValue.append("p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append("p_services=");
        	loggerValue.append(p_services);
        	LOG.debug(methodName, loggerValue);
        }
        ArrayList rechargeList = new ArrayList();
         
      
        try {
            StringBuilder qrySelect = dailyReportAnalysisQry.loadP2PReceiverRequestHourlyQry(p_date);
            if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(qrySelect);
    			LOG.debug(methodName, loggerValue);
    		}
            try(PreparedStatement prepSelect = p_con.prepareStatement(qrySelect.toString());)
            {
            int i = 1;
            java.sql.Date date = BTSLUtil.getSQLDateFromUtilDate(p_date);

            prepSelect.setString(i, p_services);
            i++;
            prepSelect.setString(i, p_networkCode);
            i++;
            prepSelect.setString(i, p_networkCode);
            i++;
            prepSelect.setDate(i, date);
            i++;
            try(ResultSet rs = prepSelect.executeQuery();)
            {
            DailyReportVO dailyReportVO = null;
            // ArrayList productList = new ArrayList();
            while (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setNetworkCode(rs.getString("network_code"));
                dailyReportVO.setNetworkName(rs.getString("network_name"));
                dailyReportVO.setServiceType(rs.getString("service_type"));
                dailyReportVO.setServiceTypeName(rs.getString("service_type_name"));
                dailyReportVO.setDailySuccessCount(rs.getLong("success_count"));
                dailyReportVO.setDailyTotalCount(rs.getLong("total_count"));
                for (int k = 0; k < 24; k++) {
                    dailyReportVO.setDailySuccessCount(k, rs.getLong("success_count" + (k + 1)));
                    dailyReportVO.setDailyTotalCount(k, rs.getLong("total_count" + (k + 1)));
                }
                rechargeList.add(dailyReportVO);
            }
        }
            }
        }catch (SQLException sql) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sql.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadP2PReceiverRequestHourly]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sql);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadP2PReceiverRequestHourly]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: rechargeList.size():");
             	loggerValue.append(rechargeList.size());
             	LOG.debug(methodName, loggerValue);
             }
        }
        return rechargeList;
    }

    /**
     * method loadC2STransferSummaryProduct
     * This method load C2S transaction summary (daily/monthly) on
     * the date and netrwork and service allowed external interface basis.
     * 
     * @param p_con
     * @param p_fromDate
     * @param p_toDate
     * @param p_networkCode
     * @param p_service
     *            TODO
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadC2STransferSummaryProduct(Connection p_con, Date p_fromDate, Date p_toDate, String p_networkCode, String p_service) throws BTSLBaseException {
        final String methodName = "loadC2STransferSummaryProduct";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_fromDate=");
        	loggerValue.append(p_fromDate);
        	loggerValue.append("p_toDate=");
        	loggerValue.append(p_toDate);
        	loggerValue.append("p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append("p_service=");
        	loggerValue.append(p_service);
        	LOG.debug(methodName, loggerValue);
        }
        ArrayList totalRequest = new ArrayList();
         
        
        try {
        	StringBuilder qrySelect = dailyReportAnalysisQry.loadC2STransferSummaryProductQry();
        	 if(LOG.isDebugEnabled()){
     			loggerValue.setLength(0);
     			loggerValue.append(QUERY_KEY);
     			loggerValue.append(qrySelect);
     			LOG.debug(methodName, loggerValue);
     		}
            try(PreparedStatement prepSelect = p_con.prepareStatement(qrySelect.toString());)
            {
            int i = 1;
            java.sql.Date fromDate = BTSLUtil.getSQLDateFromUtilDate(p_fromDate);
            java.sql.Date toDate = BTSLUtil.getSQLDateFromUtilDate(p_toDate);
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_FAIL);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_FAIL);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_FAIL);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_FAIL);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, fromDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, p_service);
            i++;
            prepSelect.setString(i, p_networkCode);
            i++;
            prepSelect.setString(i, p_networkCode);
            i++;
           try(ResultSet  rs = prepSelect.executeQuery();)
           {
            DailyReportVO dailyReportVO = null;
            while (rs.next()) {
                dailyReportVO = new DailyReportVO();

                dailyReportVO.setNetworkCode(rs.getString("network_code"));
                dailyReportVO.setNetworkName(rs.getString("network_name"));
                dailyReportVO.setServiceType(rs.getString("service_type"));
                dailyReportVO.setServiceTypeName(rs.getString("service_type_name"));
                dailyReportVO.setProductCode(rs.getString("product_code"));
                dailyReportVO.setProductName(rs.getString("product_name"));

                dailyReportVO.setDailyTotalCount(rs.getLong("total_day_count"));
                dailyReportVO.setDailyTotalAmount(rs.getLong("total_day_amount"));
                dailyReportVO.setDailyTotalAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailyTotalAmount()));
                dailyReportVO.setMonthTotalCount(rs.getLong("total_month_count"));
                dailyReportVO.setMonthTotalAmount(rs.getLong("total_month_amount"));
                dailyReportVO.setMonthTotalAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getMonthTotalAmount()));

                dailyReportVO.setDailySuccessCount(rs.getLong("success_day_count"));
                dailyReportVO.setDailySuccessAmount(rs.getLong("success_day_amount"));
                dailyReportVO.setDailySuccessAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailySuccessAmount()));
                dailyReportVO.setMonthSuccessCount(rs.getLong("success_month_count"));
                dailyReportVO.setMonthSuccessAmount(rs.getLong("success_month_amount"));
                dailyReportVO.setMonthSuccessAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getMonthSuccessAmount()));

                dailyReportVO.setDailyFailCount(rs.getLong("fail_day_count"));
                dailyReportVO.setDailyFailAmount(rs.getLong("fail_day_amount"));
                dailyReportVO.setDailyFailAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailyFailAmount()));
                dailyReportVO.setMonthFailCount(rs.getLong("fail_month_count"));
                dailyReportVO.setMonthFailAmount(rs.getLong("fail_month_amount"));
                dailyReportVO.setMonthFailAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getMonthFailAmount()));

                dailyReportVO.setDailyAmbigousCount(rs.getLong("ambigous_day_count"));
                dailyReportVO.setDailyAmbigousAmount(rs.getLong("ambigous_day_amount"));
                dailyReportVO.setDailyAmbigousAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailyAmbigousAmount()));
                dailyReportVO.setMonthAmbigousCount(rs.getLong("ambigous_month_count"));
                dailyReportVO.setMonthAmbigousAmount(rs.getLong("ambigous_month_amount"));
                dailyReportVO.setMonthAmbigousAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getMonthAmbigousAmount()));

                dailyReportVO.setDailyUnderProcessCount(rs.getLong("underprocess_day_count"));
                dailyReportVO.setDailyUnderProcessAmount(rs.getLong("underprocess_day_amount"));
                dailyReportVO.setDailyUnderProcessAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailyUnderProcessAmount()));
                dailyReportVO.setMonthUnderProcessCount(rs.getLong("underprocess_month_count"));
                dailyReportVO.setMonthUnderProcessAmount(rs.getLong("underprocess_month_amount"));
                dailyReportVO.setMonthUnderProcessAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getMonthUnderProcessAmount()));

                totalRequest.add(dailyReportVO);
            }
        }
        }
        }catch (SQLException sql) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sql.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadC2STransferSummaryProduct]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sql);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadC2STransferSummaryProduct]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: totalRequest.size():");
             	loggerValue.append(totalRequest.size());
             	LOG.debug(methodName, loggerValue);
             }
        }
        return totalRequest;
        
    }

    /**
     * method loadP2PTransferSummaryProduct
     * This method load P2P transaction summary (daily/monthly) on
     * the date and netrwork and service allowed external interface basis.
     * 
     * @param p_con
     * @param p_fromDate
     * @param p_toDate
     * @param p_networkCode
     * @param p_service
     *            TODO
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadP2PTransferSummaryProduct(Connection p_con, Date p_fromDate, Date p_toDate, String p_networkCode, String p_service) throws BTSLBaseException {
        final String methodName = "loadP2PTransferSummaryProduct";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_fromDate=");
        	loggerValue.append(p_fromDate);
        	loggerValue.append("p_toDate=");
        	loggerValue.append(p_toDate);
        	loggerValue.append("p_networkCode=");
        	loggerValue.append(p_networkCode);
        	loggerValue.append("p_service=");
        	loggerValue.append(p_service);
        	LOG.debug(methodName, loggerValue);
        }
        ArrayList totalRequest = new ArrayList();
         
        
        try {
            
        	StringBuilder qrySelect = dailyReportAnalysisQry.loadP2PTransferSummaryProductQry();
        	 if(LOG.isDebugEnabled()){
     			loggerValue.setLength(0);
     			loggerValue.append(QUERY_KEY);
     			loggerValue.append(qrySelect);
     			LOG.debug(methodName, loggerValue);
     		}
           try(PreparedStatement prepSelect = p_con.prepareStatement(qrySelect.toString());)
           {
            int i = 1;
            java.sql.Date fromDate = BTSLUtil.getSQLDateFromUtilDate(p_fromDate);
            java.sql.Date toDate = BTSLUtil.getSQLDateFromUtilDate(p_toDate);
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_FAIL);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_FAIL);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_FAIL);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_FAIL);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_AMBIGUOUS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsErrorCodesI.TXN_STATUS_UNDER_PROCESS);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, p_service);
            i++;
            prepSelect.setString(i, p_networkCode);
            i++;
            prepSelect.setString(i, p_networkCode);
            i++;
            prepSelect.setDate(i, fromDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            try(ResultSet rs = prepSelect.executeQuery();)
            {
            DailyReportVO dailyReportVO = null;
            while (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setNetworkCode(rs.getString("network_code"));
                dailyReportVO.setNetworkName(rs.getString("network_name"));
                dailyReportVO.setServiceType(rs.getString("service_type"));
                dailyReportVO.setServiceTypeName(rs.getString("service_type_name"));
                dailyReportVO.setProductCode(rs.getString("product_code"));
                dailyReportVO.setProductName(rs.getString("product_name"));

                dailyReportVO.setDailyTotalCount(rs.getLong("total_day_count"));
                dailyReportVO.setDailyTotalAmountStr(rs.getString("total_day_amount"));
                // dailyReportVO.setDailyTotalAmount(rs.getLong("total_day_amount"));
                // dailyReportVO.setDailyTotalAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailyTotalAmount()));
                dailyReportVO.setMonthTotalCount(rs.getLong("total_month_count"));
                dailyReportVO.setMonthTotalAmountStr(rs.getString("total_month_amount"));
                // dailyReportVO.setMonthTotalAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getMonthTotalAmount()));

                dailyReportVO.setDailySuccessCount(rs.getLong("success_day_count"));
                dailyReportVO.setDailySuccessAmount(rs.getLong("success_day_amount"));
                dailyReportVO.setDailySuccessAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailySuccessAmount()));
                dailyReportVO.setMonthSuccessCount(rs.getLong("success_month_count"));
                dailyReportVO.setMonthSuccessAmount(rs.getLong("success_month_amount"));
                dailyReportVO.setMonthSuccessAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getMonthSuccessAmount()));

                dailyReportVO.setDailyFailCount(rs.getLong("fail_day_count"));
                // dailyReportVO.setDailyFailAmount(rs.getLong("fail_day_amount"));
                // dailyReportVO.setDailyFailAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailyFailAmount()));
                dailyReportVO.setDailyFailAmountStr(rs.getString("fail_day_amount"));
                dailyReportVO.setMonthFailCount(rs.getLong("fail_month_count"));
                // dailyReportVO.setMonthFailAmount(rs.getLong("fail_month_amount"));
                // dailyReportVO.setMonthFailAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getMonthFailAmount()));
                dailyReportVO.setMonthFailAmountStr(rs.getString("fail_month_amount"));

                dailyReportVO.setDailyAmbigousCount(rs.getLong("ambigous_day_count"));
                dailyReportVO.setDailyAmbigousAmount(rs.getLong("ambigous_day_amount"));
                dailyReportVO.setDailyAmbigousAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailyAmbigousAmount()));
                dailyReportVO.setMonthAmbigousCount(rs.getLong("ambigous_month_count"));
                dailyReportVO.setMonthAmbigousAmount(rs.getLong("ambigous_month_amount"));
                dailyReportVO.setMonthAmbigousAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getMonthAmbigousAmount()));

                dailyReportVO.setDailyUnderProcessCount(rs.getLong("underprocess_day_count"));
                dailyReportVO.setDailyUnderProcessAmount(rs.getLong("underprocess_day_amount"));
                dailyReportVO.setDailyUnderProcessAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailyUnderProcessAmount()));
                dailyReportVO.setMonthUnderProcessCount(rs.getLong("underprocess_month_count"));
                dailyReportVO.setMonthUnderProcessAmount(rs.getLong("underprocess_month_amount"));
                dailyReportVO.setMonthUnderProcessAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getMonthUnderProcessAmount()));

                totalRequest.add(dailyReportVO);
            }
        } 
        }
        }catch (SQLException sql) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sql.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadP2PTransferSummaryProduct]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sql);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadP2PTransferSummaryProduct]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: totalRequest.size():");
             	loggerValue.append(totalRequest.size());
             	LOG.debug(methodName, loggerValue);
             }
        }
        return totalRequest;
    }

    /**
     * @param p_con
     * @param p_ntwkCode
     * @param p_prdctCode
     * @param p_srvcType
     * @param p_fromDate
     * @param p_toDate
     * @return
     * @throws BTSLBaseException
     */
    public DailyReportVO loadCountsForNtwrkTransfer(Connection p_con, String p_ntwkCode, String p_prdctCode, String p_srvcType, Date p_fromDate, Date p_toDate) throws BTSLBaseException {
        final String methodName = "loadCountsForNtwrkTransfer";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_fromDate=");
        	loggerValue.append(p_fromDate);
        	loggerValue.append("p_toDate=");
        	loggerValue.append(p_toDate);
        	loggerValue.append("p_ntwkCode=");
        	loggerValue.append(p_ntwkCode);
        	loggerValue.append("p_srvcType=");
        	loggerValue.append(p_srvcType);
        	loggerValue.append("p_prdctCode=");
        	loggerValue.append(p_prdctCode);
        	LOG.debug(methodName, loggerValue);
        }

        
        ResultSet rs = null;
        DailyReportVO dailyReportVO = null;
        StringBuilder strBuff = dailyReportAnalysisQry.loadCountsForNtwrkTransferQry();

        String sqlSelect = strBuff.toString();

        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
           
            pstmt.setString(1, p_prdctCode);
            pstmt.setString(2, PretupsI.NETWORK_STOCK_TXN_STATUS_CLOSE);
            pstmt.setString(3, p_srvcType);
            pstmt.setString(4, p_ntwkCode);
            pstmt.setString(5, p_ntwkCode);
            pstmt.setDate(6, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
            pstmt.setDate(7, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
            rs = pstmt.executeQuery();
            if (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setProdAmount(rs.getLong("amt"));
                dailyReportVO.setProdCount(rs.getLong("cnt"));
            } else {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setProdAmount(0);
                dailyReportVO.setProdCount(0);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LogFactory.printError(methodName, "SQLException : " + sqe, LOG);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadCountsForNtwrkTransfer]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadCountsForNtwrkTransfer]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: dailyReportVO:");
             	loggerValue.append(dailyReportVO);
             	LOG.debug(methodName, loggerValue);
             }
        }
        return dailyReportVO;
    }

    /**
     * @param p_con
     * @param p_ntwkCode
     * @param p_prdctCode
     * @param p_srvcType
     * @param p_subService
     * @param p_fromDate
     * @param p_toDate
     * @return
     * @throws BTSLBaseException
     */
    public DailyReportVO loadChannelServiceCounts(Connection p_con, String p_ntwkCode, String p_prdctCode, String p_srvcType, String p_subService, Date p_fromDate, Date p_toDate) throws BTSLBaseException {
        final String methodName = "loadChannelServiceCounts";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_fromDate=");
        	loggerValue.append(p_fromDate);
        	loggerValue.append("p_toDate=");
        	loggerValue.append(p_toDate);
        	loggerValue.append("p_ntwkCode=");
        	loggerValue.append(p_ntwkCode);
        	loggerValue.append("p_srvcType=");
        	loggerValue.append(p_srvcType);
        	loggerValue.append("p_prdctCode=");
        	loggerValue.append(p_prdctCode);
        	LOG.debug(methodName, loggerValue);
        }

        DailyReportVO dailyReportVO = null;
        StringBuilder strBuff = dailyReportAnalysisQry.loadChannelServiceCountsQry();

        String sqlSelect = strBuff.toString();

        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
            pstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
            pstmt.setString(3, p_prdctCode);
            pstmt.setString(4, p_ntwkCode);
            pstmt.setString(5, PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE);
            pstmt.setString(6, p_srvcType);
            pstmt.setString(7, p_subService);

            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setProdAmount(rs.getLong("amt"));
                dailyReportVO.setProdCount(rs.getLong("cnt"));
            } else {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setProdAmount(0);
                dailyReportVO.setProdCount(0);
            }
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LogFactory.printError(methodName, "SQLException : " + sqe, LOG);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReport[loadChannelServiceCounts]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReport[loadChannelServiceCounts]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: dailyReportVO:");
             	loggerValue.append(dailyReportVO);
             	LOG.debug(methodName, loggerValue);
             }
        }
        return dailyReportVO;
    }

    /**
     * @param p_con
     * @param p_ntwkCode
     * @param p_prdctCode
     * @param p_srvcType
     * @param p_fromDate
     * @param p_toDate
     * @return
     * @throws BTSLBaseException
     */
    public DailyReportVO loadC2SServiceCounts(Connection p_con, String p_ntwkCode, String p_prdctCode, String p_srvcType, Date p_fromDate, Date p_toDate) throws BTSLBaseException {
        final String methodName = "loadC2SServiceCounts";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_fromDate=");
        	loggerValue.append(p_fromDate);
        	loggerValue.append("p_toDate=");
        	loggerValue.append(p_toDate);
        	loggerValue.append("p_ntwkCode=");
        	loggerValue.append(p_ntwkCode);
        	loggerValue.append("p_srvcType=");
        	loggerValue.append(p_srvcType);
        	LOG.debug(methodName, loggerValue);
        }

         
        ResultSet rs = null;
        DailyReportVO dailyReportVO = null;
        StringBuilder strBuff = dailyReportAnalysisQry.loadC2SServiceCountsQry();

        String sqlSelect = strBuff.toString();

        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
        	pstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
            pstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
            pstmt.setString(3, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            pstmt.setString(4, p_ntwkCode);
            pstmt.setString(5, p_prdctCode);
            pstmt.setString(6, p_srvcType);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setProdAmount(rs.getLong("amt"));
                dailyReportVO.setProdCount(rs.getLong("cnt"));
            } else {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setProdAmount(0);
                dailyReportVO.setProdCount(0);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LogFactory.printError(methodName, "SQLException : " + sqe, LOG);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReport[loadC2SServiceCounts]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReport[loadC2SServiceCounts]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: dailyReportVO:");
             	loggerValue.append(dailyReportVO);
             	LOG.debug(methodName, loggerValue);
             }
        }
        return dailyReportVO;
    }

    /**
     * @param p_con
     * @param p_ntwkCode
     * @param p_prdctCode
     * @param p_srvcType
     * @param p_fromDate
     * @param p_toDate
     * @return
     * @throws BTSLBaseException
     */
    public DailyReportVO loadCountsForP2PServices(Connection p_con, String p_ntwkCode, String p_prdctCode, String p_srvcType, Date p_fromDate, Date p_toDate) throws BTSLBaseException {
        final String methodName = "loadCountsForP2PServices";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_fromDate=");
        	loggerValue.append(p_fromDate);
        	loggerValue.append("p_toDate=");
        	loggerValue.append(p_toDate);
        	loggerValue.append("p_ntwkCode=");
        	loggerValue.append(p_ntwkCode);
        	loggerValue.append("p_srvcType=");
        	loggerValue.append(p_srvcType);
        	loggerValue.append("p_prdctCode=");
        	loggerValue.append(p_prdctCode);
        	LOG.debug(methodName, loggerValue);
        }
         
        ResultSet rs = null;
        DailyReportVO dailyReportVO = null;
        StringBuilder strBuff = dailyReportAnalysisQry.loadCountsForP2PServicesQry();

        String sqlSelect = strBuff.toString();

        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
           
            pstmt.setString(1, PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            pstmt.setString(2, p_ntwkCode);
            pstmt.setString(3, p_prdctCode);
            pstmt.setString(4, p_srvcType);
            pstmt.setDate(5, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
            pstmt.setDate(6, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
            rs = pstmt.executeQuery();
            if (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setProdAmount(rs.getLong("amt"));
                dailyReportVO.setProdCount(rs.getLong("cnt"));
            } else {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setProdAmount(0);
                dailyReportVO.setProdCount(0);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReport[loadCountsForP2PServices]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReport[loadCountsForP2PServices]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: dailyReportVO:");
             	loggerValue.append(dailyReportVO);
             	LOG.debug(methodName, loggerValue);
             }
        }
        return dailyReportVO;
    }

    /**
     * @param p_con
     * @param p_ntwkCode
     * @param p_fromDate
     * @param p_toDate
     * @return
     * @throws BTSLBaseException
     */
    public DailyReportVO loadChannelActivUserCounts(Connection p_con, String p_ntwkCode, Date p_fromDate, Date p_toDate) throws BTSLBaseException {
        final String methodName = "loadChannelActivUserCounts";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_fromDate=");
        	loggerValue.append(p_fromDate);
        	loggerValue.append("p_toDate=");
        	loggerValue.append(p_toDate);
        	loggerValue.append("p_ntwkCode=");
        	loggerValue.append(p_ntwkCode);
        	LOG.debug(methodName, loggerValue);
        }
         
         
        DailyReportVO dailyReportVO = null;
        StringBuilder strBuff = dailyReportAnalysisQry.loadChannelActivUserCountsQry(p_fromDate,p_toDate);

        String sqlSelect = strBuff.toString();

        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_ntwkCode);
            pstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
            pstmt.setDate(3, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
            try(ResultSet rs = pstmt.executeQuery();)
            {
            if (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setProdCount(rs.getLong("cnt"));
            } else {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setProdCount(0);
            }
        } 
        }catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
        	LogFactory.printError(methodName, "SQLException : " + sqe, LOG);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReport[loadChannelActivUserCounts]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReport[loadChannelActivUserCounts]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: dailyReportVO:");
             	loggerValue.append(dailyReportVO);
             	LOG.debug(methodName, loggerValue);
             }
        }
        return dailyReportVO;
    }

    /**
     * @param p_con
     * @param p_ntwkCode
     * @param p_fromDate
     * @param p_toDate
     * @return
     * @throws BTSLBaseException
     */
    public DailyReportVO loadP2PActivUserCounts(Connection p_con, String p_ntwkCode, Date p_fromDate, Date p_toDate) throws BTSLBaseException {
        final String methodName = "loadP2PActivUserCounts";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_fromDate=");
        	loggerValue.append(p_fromDate);
        	loggerValue.append("p_toDate=");
        	loggerValue.append(p_toDate);
        	loggerValue.append("p_ntwkCode=");
        	loggerValue.append(p_ntwkCode);
        	LOG.debug(methodName, loggerValue);
        }
        
        ResultSet rs = null;
        DailyReportVO dailyReportVO = null;
        StringBuffer strBuff = new StringBuffer(" SELECT count(DISTINCT(sender_id)) cnt FROM subscriber_transfers ");
        strBuff.append(" WHERE (transfer_date >=?  AND  transfer_date <=? ) AND transfer_status=? AND network_code=? ");
        String sqlSelect = strBuff.toString();

        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
           
            pstmt.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_fromDate));
            pstmt.setDate(2, BTSLUtil.getSQLDateFromUtilDate(p_toDate));
            pstmt.setString(3, PretupsI.TXN_STATUS_SUCCESS);
            pstmt.setString(4, p_ntwkCode);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setProdCount(rs.getLong("cnt"));
            } else {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setProdCount(0);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LogFactory.printError(methodName, "SQLException : " + sqe, LOG);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReport[loadP2PActivUserCounts]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReport[loadP2PActivUserCounts]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: dailyReportVO:");
             	loggerValue.append(dailyReportVO);
             	LOG.debug(methodName, loggerValue);
             }
        }
        return dailyReportVO;
    }

    /**
     * @param p_con
     * @param p_ntwkCode
     * @param p_prdctCode
     * @return
     * @throws BTSLBaseException
     */
    public DailyReportVO loadNetworkStockCount(Connection p_con, String p_ntwkCode, String p_prdctCode) throws BTSLBaseException {
        final String methodName = "loadNetworkStockCount";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
        	loggerValue.append("p_ntwkCode=");
        	loggerValue.append(p_ntwkCode);
        	loggerValue.append("p_prdctCode=");
        	loggerValue.append(p_prdctCode);
        	LOG.debug(methodName, loggerValue);
        }

         
        ResultSet rs = null;
        DailyReportVO dailyReportVO = null;
        StringBuffer strBuff = new StringBuffer(" SELECT ns.wallet_balance FROM network_stocks ns WHERE ns.network_code=? ");
        strBuff.append(" AND ns.product_code=? and ns.WALLET_TYPE='SAL' ");

        String sqlSelect = strBuff.toString();

        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_ntwkCode);
            pstmt.setString(2, p_prdctCode);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setProdAmount(rs.getLong("wallet_balance"));
            } else {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setProdAmount(0);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LogFactory.printError(methodName, "SQLException : " + sqe, LOG);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReport[loadNetworkStockCount]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReport[loadNetworkStockCount]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: dailyReportVO:");
             	loggerValue.append(dailyReportVO);
             	LOG.debug(methodName, loggerValue);
             }
        }
        return dailyReportVO;
    }

    /**
     * This method loads the details for Total User Balance.
     * 
     * @param p_con
     * @param p_ntwkCode
     * @param p_prdctCode
     * @return
     * @throws BTSLBaseException
     */
    public DailyReportVO loadTotalUserBalance(Connection p_con, String p_ntwkCode, String p_prdctCode) throws BTSLBaseException {
        final String methodName = "loadTotalUserBalance";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
        	loggerValue.append("p_ntwkCode=");
        	loggerValue.append(p_ntwkCode);
        	loggerValue.append("p_prdctCode=");
        	loggerValue.append(p_prdctCode);
        	LOG.debug(methodName, loggerValue);
        }

        
        ResultSet rs = null;
        DailyReportVO dailyReportVO = null;
        StringBuffer strBuff = new StringBuffer(" SELECT sum(UB.balance) amt FROM user_balances UB WHERE UB.network_code=? AND UB.product_code=? ");
        String sqlSelect = strBuff.toString();

        if(LOG.isDebugEnabled()){
			loggerValue.setLength(0);
			loggerValue.append(QUERY_KEY);
			loggerValue.append(sqlSelect);
			LOG.debug(methodName, loggerValue);
		}
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
            
            pstmt.setString(1, p_ntwkCode);
            pstmt.setString(2, p_prdctCode);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setProdAmount(rs.getLong("amt"));
            } else {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setProdAmount(0);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			LOG.error(methodName, loggerValue);
            LogFactory.printError(methodName, "SQLException : " + sqe, LOG);
            LOG.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReport[loadTotalUserBalance]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqe);
        } catch (Exception ex) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReport[loadTotalUserBalance]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: dailyReportVO:");
             	loggerValue.append(dailyReportVO);
             	LOG.debug(methodName, loggerValue);
             }
        }
        return dailyReportVO;
    }

    /**
     * method loadC2SRecharge
     * This method loads total, success, fail and ambiguous transaction count
     * and amounts (daily and monthly)
     * on the date range for all the interfaces, service types and categories.
     * 
     * @param p_con
     * @param p_fromDate
     * @param p_toDate
     * @return Arraylist
     * @throws BTSLBaseException
     */
    public ArrayList loadC2SRecharge(Connection p_con, Date p_fromDate, Date p_toDate) throws BTSLBaseException {
        final String methodName = "loadC2SRecharge";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
        	loggerValue.append("p_fromDate=");
        	loggerValue.append(p_fromDate);
        	loggerValue.append("p_toDate=");
        	loggerValue.append(p_toDate);
        	LOG.debug(methodName, loggerValue);
        }
        ArrayList c2sRecharge = new ArrayList();
        
        
        try {
        	StringBuilder qrySelect = dailyReportAnalysisQry.loadC2SRechargeQry();
        	
         
        	if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(qrySelect);
    			LOG.debug(methodName, loggerValue);
    		}
            try(PreparedStatement prepSelect = p_con.prepareStatement(qrySelect.toString());)
            {
            java.sql.Date fromDate = BTSLUtil.getSQLDateFromUtilDate(p_fromDate);
            java.sql.Date toDate = BTSLUtil.getSQLDateFromUtilDate(p_toDate);

            int i = 1;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, fromDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            try(ResultSet rs = prepSelect.executeQuery();)
            {
            DailyReportVO dailyReportVO = null;

            while (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setNetworkCode(rs.getString("network_code"));
                dailyReportVO.setNetworkName(rs.getString("network_name"));
                dailyReportVO.setServiceType(rs.getString("service_type"));
                dailyReportVO.setServiceTypeName(rs.getString("service_type_name"));
                dailyReportVO.setCategoryCode(rs.getString("category_code"));
                dailyReportVO.setCategoryName(rs.getString("category_name"));
                dailyReportVO.setReceiverInterfaceCode(rs.getString("interface_id"));
                dailyReportVO.setReceiverInterfaceName(rs.getString("interface_description"));
                dailyReportVO.setDailyTotalCount(rs.getLong("daily_total_count"));
                dailyReportVO.setDailySuccessCount(rs.getLong("daily_success_count"));
                dailyReportVO.setDailyFailCount(rs.getLong("daily_fail_count"));
                dailyReportVO.setDailyAmbigousCount(rs.getLong("daily_ambiguous_count"));
                dailyReportVO.setMonthTotalCount(rs.getLong("monthly_total_count"));
                dailyReportVO.setMonthSuccessCount(rs.getLong("monthly_success_count"));
                dailyReportVO.setMonthFailCount(rs.getLong("monthly_fail_count"));
                dailyReportVO.setMonthAmbigousCount(rs.getLong("monthly_ambiguous_count"));
                dailyReportVO.setDailyTotalAmount(rs.getLong("daily_total_amount"));
                dailyReportVO.setDailySuccessAmount(rs.getLong("daily_success_amount"));
                dailyReportVO.setDailyFailAmount(rs.getLong("daily_fail_amount"));
                dailyReportVO.setDailyAmbigousAmount(rs.getLong("daily_ambiguous_amount"));
                dailyReportVO.setMonthTotalAmount(rs.getLong("monthly_total_amount"));
                dailyReportVO.setMonthSuccessAmount(rs.getLong("monthly_success_amount"));
                dailyReportVO.setMonthFailAmount(rs.getLong("monthly_fail_amount"));
                dailyReportVO.setMonthAmbigousAmount(rs.getLong("monthly_ambiguous_amount"));
                dailyReportVO.setDailyTotalAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailyTotalAmount()));
                dailyReportVO.setDailySuccessAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailySuccessAmount()));
                dailyReportVO.setDailyFailAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailyFailAmount()));
                dailyReportVO.setDailyAmbigousAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailyAmbigousAmount()));
                dailyReportVO.setMonthTotalAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getMonthTotalAmount()));
                dailyReportVO.setMonthSuccessAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getMonthSuccessAmount()));
                dailyReportVO.setMonthFailAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getMonthFailAmount()));
                dailyReportVO.setMonthAmbigousAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getMonthAmbigousAmount()));
                c2sRecharge.add(dailyReportVO);
            }
        }
            }
        }catch (SQLException sql) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sql.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadC2SRecharge]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sql);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadC2SRecharge]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: c2sRecharge.size:");
             	loggerValue.append(c2sRecharge.size());
             	LOG.debug(methodName, loggerValue);
             }
        }
        return c2sRecharge;
    }

    /**
     * method loadP2PRecharge
     * This method loads total, success, fail and ambiguous transaction count
     * and amounts (daily and monthly)
     * on the date range for all the interfaces, service types and categories.
     * 
     * @param p_con
     * @param p_fromDate
     * @param p_toDate
     * @return Arraylist
     * @throws BTSLBaseException
     */
    public ArrayList loadP2PRecharge(Connection p_con, Date p_fromDate, Date p_toDate) throws BTSLBaseException {
        final String methodName = "loadP2PRecharge";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
        	loggerValue.append("p_fromDate=");
        	loggerValue.append(p_fromDate);
        	loggerValue.append("p_toDate=");
        	loggerValue.append(p_toDate);
        	LOG.debug(methodName, loggerValue);
        }
        ArrayList p2pRecharge = new ArrayList();
         
         
        try {
        	StringBuilder qrySelect = dailyReportAnalysisQry.loadP2PRechargeQry();
            
        	if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(qrySelect);
    			LOG.debug(methodName, loggerValue);
    		}
            try(PreparedStatement prepSelect = p_con.prepareStatement(qrySelect.toString());)
            {
            java.sql.Date fromDate = BTSLUtil.getSQLDateFromUtilDate(p_fromDate);
            java.sql.Date toDate = BTSLUtil.getSQLDateFromUtilDate(p_toDate);

            int i = 1;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsI.USER_TYPE_SENDER);
            i++;
            prepSelect.setString(i, PretupsI.USER_TYPE_RECEIVER);
            i++;
            // prepSelect.setString(i++,p_networkCode);
            // prepSelect.setString(i++,p_networkCode);
            prepSelect.setDate(i, fromDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            try(ResultSet rs = prepSelect.executeQuery();)
            {
            DailyReportVO dailyReportVO = null;

            while (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setNetworkCode(rs.getString("network_code"));
                dailyReportVO.setNetworkName(rs.getString("network_name"));
                dailyReportVO.setServiceType(rs.getString("service_type"));
                dailyReportVO.setServiceTypeName(rs.getString("service_type_name"));
                dailyReportVO.setSenderInterfaceCode(rs.getString("sender_interface_id"));
                dailyReportVO.setSenderInterfaceName(rs.getString("sender_interface_desc"));
                dailyReportVO.setReceiverInterfaceCode(rs.getString("receiver_interface_id"));
                dailyReportVO.setReceiverInterfaceName(rs.getString("receiver_interface_desc"));
                dailyReportVO.setDailyTotalCount(rs.getLong("daily_total_count"));
                dailyReportVO.setDailySuccessCount(rs.getLong("daily_success_count"));
                dailyReportVO.setDailyFailCount(rs.getLong("daily_fail_count"));
                dailyReportVO.setDailyAmbigousCount(rs.getLong("daily_ambiguous_count"));
                dailyReportVO.setMonthTotalCount(rs.getLong("monthly_total_count"));
                dailyReportVO.setMonthSuccessCount(rs.getLong("monthly_success_count"));
                dailyReportVO.setMonthFailCount(rs.getLong("monthly_fail_count"));
                dailyReportVO.setMonthAmbigousCount(rs.getLong("monthly_ambiguous_count"));
                dailyReportVO.setDailyTotalAmount(rs.getLong("daily_total_amount"));
                dailyReportVO.setDailySuccessAmount(rs.getLong("daily_success_amount"));
                dailyReportVO.setDailyFailAmount(rs.getLong("daily_fail_amount"));
                dailyReportVO.setDailyAmbigousAmount(rs.getLong("daily_ambiguous_amount"));
                dailyReportVO.setMonthTotalAmount(rs.getLong("monthly_total_amount"));
                dailyReportVO.setMonthSuccessAmount(rs.getLong("monthly_success_amount"));
                dailyReportVO.setMonthFailAmount(rs.getLong("monthly_fail_amount"));
                dailyReportVO.setMonthAmbigousAmount(rs.getLong("monthly_ambiguous_amount"));
                dailyReportVO.setDailyTotalAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailyTotalAmount()));
                dailyReportVO.setDailySuccessAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailySuccessAmount()));
                dailyReportVO.setDailyFailAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailyFailAmount()));
                dailyReportVO.setDailyAmbigousAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailyAmbigousAmount()));
                dailyReportVO.setMonthTotalAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getMonthTotalAmount()));
                dailyReportVO.setMonthSuccessAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getMonthSuccessAmount()));
                dailyReportVO.setMonthFailAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getMonthFailAmount()));
                dailyReportVO.setMonthAmbigousAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getMonthAmbigousAmount()));
                p2pRecharge.add(dailyReportVO);
            }
        }
            }
        }catch (SQLException sql) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sql.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadP2PRecharge]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sql);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadP2PRecharge]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: p2pRecharge.size:");
             	loggerValue.append(p2pRecharge.size());
             	LOG.debug(methodName, loggerValue);
             }
        }
        return p2pRecharge;
    }

    /**
     * method loadInterfaceWiseC2SRecharge
     * This method loads total and ambiguous transaction count (daily and
     * monthly)
     * on the date range for all the interfaces.
     * 
     * @param p_con
     * @param p_fromDate
     * @param p_toDate
     * @param p_networkCode
     * @return Arraylist
     * @throws BTSLBaseException
     */
    public ArrayList loadInterfaceWiseC2SRecharge(Connection p_con, Date p_fromDate, Date p_toDate) throws BTSLBaseException {
        final String methodName = "loadInterfaceWiseC2SRecharge";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
        	loggerValue.append("p_fromDate=");
        	loggerValue.append(p_fromDate);
        	loggerValue.append("p_toDate=");
        	loggerValue.append(p_toDate);
        	LOG.debug(methodName, loggerValue);
        }
        ArrayList interfaceC2sRecharge = new ArrayList();
        
       
        try {
           
        	StringBuilder qrySelect = dailyReportAnalysisQry.loadInterfaceWiseC2SRechargeQry();
        	if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(qrySelect);
    			LOG.debug(methodName, loggerValue);
    		}
            try(PreparedStatement prepSelect = p_con.prepareStatement(qrySelect.toString());)
            {
            java.sql.Date fromDate = BTSLUtil.getSQLDateFromUtilDate(p_fromDate);
            java.sql.Date toDate = BTSLUtil.getSQLDateFromUtilDate(p_toDate);

            int i = 1;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            // prepSelect.setString(i++,PretupsI.USER_TYPE_RECEIVER);
            prepSelect.setDate(i, fromDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            try(ResultSet rs = prepSelect.executeQuery();)
            {
            DailyReportVO dailyReportVO = null;

            while (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setReceiverInterfaceCode(rs.getString("interface_id"));
                dailyReportVO.setReceiverInterfaceName(rs.getString("interface_description"));
                dailyReportVO.setDailyTotalReceiverValCount(rs.getLong("daily_total_validation"));
                dailyReportVO.setDailyTotalCreditCount(rs.getLong("daily_total_credit"));
                dailyReportVO.setMonthlyTotalReceiverValCount(rs.getLong("monthly_total_validation"));
                dailyReportVO.setMonthlyTotalCreditCount(rs.getLong("monthly_total_credit"));
                dailyReportVO.setDailyFailReceiverValCount(rs.getLong("daily_fail_validation"));
                dailyReportVO.setDailyFailCreditCount(rs.getLong("daily_fail_credit"));
                dailyReportVO.setMonthlyFailReceiverValCount(rs.getLong("monthly_fail_validation"));
                dailyReportVO.setMonthlyFailCreditCount(rs.getLong("monthly_fail_credit"));
                interfaceC2sRecharge.add(dailyReportVO);
            }
        }
            }
        }catch (SQLException sql) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sql.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadInterfaceWiseC2SRecharge]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sql);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadInterfaceWiseC2SRecharge]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: interfaceC2sRecharge.size:");
             	loggerValue.append(interfaceC2sRecharge.size());
             	LOG.debug(methodName, loggerValue);
             }
        }
        return interfaceC2sRecharge;
    }

    /**
     * method loadInterfaceWiseP2PRecharge
     * This method loads total and fail transaction count (daily and monthly)
     * on the date range for all the interfaces.
     * 
     * @param p_con
     * @param p_fromDate
     * @param p_toDate
     * @return Arraylist
     * @throws BTSLBaseException
     */
    public ArrayList loadInterfaceWiseP2PRecharge(Connection p_con, Date p_fromDate, Date p_toDate) throws BTSLBaseException {
        final String methodName = "loadInterfaceWiseP2PRecharge";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
        	loggerValue.append("p_fromDate=");
        	loggerValue.append(p_fromDate);
        	loggerValue.append("p_toDate=");
        	loggerValue.append(p_toDate);
        	LOG.debug(methodName, loggerValue);
        }
        ArrayList interfaceP2pRecharge = new ArrayList();
        
       
        try {
        	StringBuilder qrySelect = dailyReportAnalysisQry.loadInterfaceWiseP2PRechargeQry();
        	           

        	if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(qrySelect);
    			LOG.debug(methodName, loggerValue);
    		}
            try(PreparedStatement prepSelect = p_con.prepareStatement(qrySelect.toString());)
            {
            java.sql.Date fromDate = BTSLUtil.getSQLDateFromUtilDate(p_fromDate);
            java.sql.Date toDate = BTSLUtil.getSQLDateFromUtilDate(p_toDate);

            int i = 1;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsI.USER_TYPE_SENDER);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsI.USER_TYPE_SENDER);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsI.USER_TYPE_SENDER);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsI.USER_TYPE_SENDER);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsI.USER_TYPE_RECEIVER);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsI.USER_TYPE_RECEIVER);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsI.USER_TYPE_RECEIVER);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsI.USER_TYPE_RECEIVER);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsI.USER_TYPE_SENDER);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsI.USER_TYPE_SENDER);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsI.USER_TYPE_SENDER);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsI.USER_TYPE_SENDER);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsI.USER_TYPE_RECEIVER);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsI.USER_TYPE_RECEIVER);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsI.USER_TYPE_RECEIVER);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            prepSelect.setString(i, PretupsI.USER_TYPE_RECEIVER);
            i++;
            prepSelect.setDate(i, fromDate);
            i++;
            prepSelect.setDate(i, toDate);
            i++;
            try(ResultSet rs = prepSelect.executeQuery();)
            {
            DailyReportVO dailyReportVO = null;

            while (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setReceiverInterfaceCode(rs.getString("interface_id"));
                dailyReportVO.setReceiverInterfaceName(rs.getString("interface_description"));
                dailyReportVO.setDailyTotalSenderValCount(rs.getLong("daily_total_sender_val"));
                dailyReportVO.setDailyTotalDebitCount(rs.getLong("daily_total_sender_debit"));
                dailyReportVO.setDailyTotalReceiverValCount(rs.getLong("daily_total_rec_val"));
                dailyReportVO.setDailyTotalCreditCount(rs.getLong("daily_total_rec_credit"));
                dailyReportVO.setMonthlyTotalSenderValCount(rs.getLong("monthly_total_sender_val"));
                dailyReportVO.setMonthlyTotalDebitCount(rs.getLong("monthly_total_sender_debit"));
                dailyReportVO.setMonthlyTotalReceiverValCount(rs.getLong("monthly_total_rec_val"));
                dailyReportVO.setMonthlyTotalCreditCount(rs.getLong("monthly_total_rec_credit"));
                dailyReportVO.setDailyFailSenderValCount(rs.getLong("daily_fail_sender_val"));
                dailyReportVO.setDailyFailDebitCount(rs.getLong("daily_fail_sender_debit"));
                dailyReportVO.setDailyFailReceiverValCount(rs.getLong("daily_fail_rec_val"));
                dailyReportVO.setDailyFailCreditCount(rs.getLong("daily_fail_rec_credit"));
                dailyReportVO.setMonthlyFailSenderValCount(rs.getLong("monthly_fail_sender_val"));
                dailyReportVO.setMonthlyFailDebitCount(rs.getLong("monthly_fail_sender_debit"));
                dailyReportVO.setMonthlyFailReceiverValCount(rs.getLong("monthly_fail_rec_val"));
                dailyReportVO.setMonthlyFailCreditCount(rs.getLong("monthly_fail_rec_credit"));
                interfaceP2pRecharge.add(dailyReportVO);
            }
        }
            }
        }catch (SQLException sql) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sql.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadInterfaceWiseP2PRecharge]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadInterfaceWiseP2PRecharge", "error.general.sql.processing",sql);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadInterfaceWiseP2PRecharge]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadInterfaceWiseP2PRecharge", "error.general.processing",e);
        } finally {
            if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: interfaceP2pRecharge.size:");
             	loggerValue.append(interfaceP2pRecharge.size());
             	LOG.debug(methodName, loggerValue);
             }
        }
        return interfaceP2pRecharge;
    }

    /**
     * method loadC2SServiceInterfaceRecharge
     * This method loads total, success, fail, ambiguous and underprocess
     * transaction counts and amounts
     * for a specific date for all the interfaces and service wise.
     * 
     * @param p_con
     * @param p_date
     * @param p_networkCode
     * @return Arraylist
     * @throws BTSLBaseException
     */
    public ArrayList loadC2SServiceInterfaceRecharge(Connection p_con, Date p_date, String p_networkCode) throws BTSLBaseException {
        final String methodName = "loadC2SServiceInterfaceRecharge";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
        	loggerValue.append("p_date=");
        	loggerValue.append(p_date);
        	loggerValue.append("p_networkCode=");
        	loggerValue.append(p_networkCode);
        	LOG.debug(methodName, loggerValue);
        }
        ArrayList c2sRecharge = new ArrayList();
        
         
        try {
        	StringBuilder qrySelect = dailyReportAnalysisQry.loadC2SServiceInterfaceRechargeQry();

        	if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(qrySelect);
    			LOG.debug(methodName, loggerValue);
    		}
            try(PreparedStatement prepSelect = p_con.prepareStatement(qrySelect.toString());)
            {
            java.sql.Date date = BTSLUtil.getSQLDateFromUtilDate(p_date);

            prepSelect.setDate(1, date);
            prepSelect.setString(2, p_networkCode);
            prepSelect.setString(3, p_networkCode);
            try(ResultSet rs = prepSelect.executeQuery();)
            {
            DailyReportVO dailyReportVO = null;

            while (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setNetworkCode(rs.getString("network_code"));
                dailyReportVO.setNetworkName(rs.getString("network_name"));
                dailyReportVO.setServiceType(rs.getString("service_type"));
                dailyReportVO.setServiceTypeName(rs.getString("service_type_name"));
                dailyReportVO.setReceiverInterfaceCode(rs.getString("interface_id"));
                dailyReportVO.setReceiverInterfaceName(rs.getString("interface_description"));
                dailyReportVO.setDailyTotalCount(rs.getLong("daily_total_count"));
                dailyReportVO.setDailySuccessCount(rs.getLong("daily_success_count"));
                dailyReportVO.setDailyFailCount(rs.getLong("daily_fail_count"));
                dailyReportVO.setDailyAmbigousCount(rs.getLong("daily_ambiguous_count"));
                dailyReportVO.setDailyUnderProcessCount(rs.getLong("daily_underprocess_count"));
                dailyReportVO.setDailyTotalAmount(rs.getLong("daily_total_amount"));
                dailyReportVO.setDailySuccessAmount(rs.getLong("daily_success_amount"));
                dailyReportVO.setDailyFailAmount(rs.getLong("daily_fail_amount"));
                dailyReportVO.setDailyAmbigousAmount(rs.getLong("daily_ambiguous_amount"));
                dailyReportVO.setDailyUnderProcessAmount(rs.getLong("daily_underprocess_amount"));
                dailyReportVO.setDailyTotalAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailyTotalAmount()));
                dailyReportVO.setDailySuccessAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailySuccessAmount()));
                dailyReportVO.setDailyFailAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailyFailAmount()));
                dailyReportVO.setDailyAmbigousAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailyAmbigousAmount()));
                dailyReportVO.setDailyUnderProcessAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailyUnderProcessAmount()));
                c2sRecharge.add(dailyReportVO);
            }
        } 
            }
        }catch (SQLException sql) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sql.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadC2SServiceInterfaceRecharge]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sql);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadC2SServiceInterfaceRecharge]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
            if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: c2sRecharge.size:");
             	loggerValue.append(c2sRecharge.size());
             	LOG.debug(methodName, loggerValue);
             }
        }
        return c2sRecharge;
    }

    /**
     * method loadP2PServiceInterfaceRecharge
     * This method loads total, success, fail, ambiguous and underprocess
     * transaction count and amounts
     * on a particular date for all the interfaces and service types.
     * 
     * @param p_con
     * @param p_date
     * @param p_networkCode
     * @return Arraylist
     * @throws BTSLBaseException
     */
    public ArrayList loadP2PServiceInterfaceRecharge(Connection p_con, Date p_date, String p_networkCode) throws BTSLBaseException {
        final String methodName = "loadP2PServiceInterfaceRecharge";
        StringBuilder loggerValue= new StringBuilder();
        if (LOG.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: ");
        	loggerValue.append("p_date=");
        	loggerValue.append(p_date);
        	loggerValue.append("p_networkCode=");
        	loggerValue.append(p_networkCode);
        	LOG.debug(methodName, loggerValue);
        }
        ArrayList p2pRecharge = new ArrayList();
        
         
        try {
            
        	StringBuilder qrySelect = dailyReportAnalysisQry.loadP2PServiceInterfaceRechargeQry();
        	if(LOG.isDebugEnabled()){
    			loggerValue.setLength(0);
    			loggerValue.append(QUERY_KEY);
    			loggerValue.append(qrySelect);
    			LOG.debug(methodName, loggerValue);
    		}
           try(PreparedStatement prepSelect = p_con.prepareStatement(qrySelect.toString());)
           {
            java.sql.Date date = BTSLUtil.getSQLDateFromUtilDate(p_date);

            int i = 1;
            prepSelect.setString(i, PretupsI.USER_TYPE_SENDER);
            i++;
            prepSelect.setString(i, PretupsI.USER_TYPE_RECEIVER);
            i++;
            prepSelect.setDate(i, date);
            i++;
            prepSelect.setString(i, p_networkCode);
            i++;
            prepSelect.setString(i, p_networkCode);
            i++;
            try(ResultSet rs = prepSelect.executeQuery();)
            {
            DailyReportVO dailyReportVO = null;

            while (rs.next()) {
                dailyReportVO = new DailyReportVO();
                dailyReportVO.setNetworkCode(rs.getString("network_code"));
                dailyReportVO.setNetworkName(rs.getString("network_name"));
                dailyReportVO.setServiceType(rs.getString("service_type"));
                dailyReportVO.setServiceTypeName(rs.getString("service_type_name"));
                dailyReportVO.setSenderInterfaceCode(rs.getString("sender_interface_id"));
                dailyReportVO.setSenderInterfaceName(rs.getString("sender_interface_desc"));
                dailyReportVO.setReceiverInterfaceCode(rs.getString("receiver_interface_id"));
                dailyReportVO.setReceiverInterfaceName(rs.getString("receiver_interface_desc"));
                dailyReportVO.setDailyTotalCount(rs.getLong("daily_total_count"));
                dailyReportVO.setDailySuccessCount(rs.getLong("daily_success_count"));
                dailyReportVO.setDailyFailCount(rs.getLong("daily_fail_count"));
                dailyReportVO.setDailyAmbigousCount(rs.getLong("daily_ambiguous_count"));
                dailyReportVO.setDailyUnderProcessCount(rs.getLong("daily_underprocess_count"));
                dailyReportVO.setDailyTotalAmount(rs.getLong("daily_total_amount"));
                dailyReportVO.setDailySuccessAmount(rs.getLong("daily_success_amount"));
                dailyReportVO.setDailyFailAmount(rs.getLong("daily_fail_amount"));
                dailyReportVO.setDailyAmbigousAmount(rs.getLong("daily_ambiguous_amount"));
                dailyReportVO.setDailyUnderProcessAmount(rs.getLong("daily_underprocess_amount"));
                dailyReportVO.setDailyTotalAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailyTotalAmount()));
                dailyReportVO.setDailySuccessAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailySuccessAmount()));
                dailyReportVO.setDailyFailAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailyFailAmount()));
                dailyReportVO.setDailyAmbigousAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailyAmbigousAmount()));
                dailyReportVO.setDailyUnderProcessAmountStr(PretupsBL.getDisplayAmount(dailyReportVO.getDailyUnderProcessAmount()));
                p2pRecharge.add(dailyReportVO);
            }
        } 
           }
        }catch (SQLException sql) {
        	loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sql.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, sql);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadP2PServiceInterfaceRecharge]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sql);
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			LOG.error(methodName, loggerValue);
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "DailyReportAnalysisDAO[loadP2PServiceInterfaceRecharge]", "", "", "", loggerValue.toString());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
        	if (LOG.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: p2pRecharge.size:");
             	loggerValue.append(p2pRecharge.size());
             	LOG.debug(methodName, loggerValue);
             }
        }
        return p2pRecharge;
    }
}
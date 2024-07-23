/*
 * #UserReportDAO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Sep 16, 2005 Mayank Baranwal Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
import com.btsl.util.SqlParameterEncoder;

public class UserReportDAO {

    private final Log log = LogFactory.getFactory().getInstance(UserReportDAO.class.getName());
    
    private String errorGeneralSqlProcess = "error.general.sql.processing";
    private String errorGeneralProcess = "error.general.processing";
    private String exception = "Exception: ";
    private String sqlException = "SQL Exception: ";
    private String query = "Query: ";

    /**
     * Method for loading Division List.
     * 
     * Used in(Users Action)
     * 
     * @param con
     *            java.sql.Connection
     * @param divDeptType
     *            String
     * @param divDept
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadDivisionDeptList(Connection con, String divDeptType, String divDept) throws BTSLBaseException {
        final String methodName = "loadDivisionDeptList";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_divDeptType=");
        	loggerValue.append(divDeptType);
        	loggerValue.append(" p_divDept=");
        	loggerValue.append(divDept);
            log.debug(methodName, loggerValue );
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT divdept_id,divdept_name,parent_id ");
        strBuff.append("FROM division_department WHERE divdept_type = ? and divdept = ? ");
        strBuff.append(" ORDER BY divdept_name");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append(query);
        	loggerValue.append(sqlSelect);
            log.debug(methodName,  loggerValue );
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, divDeptType);
            pstmt.setString(2, divDept);
            rs = pstmt.executeQuery();
            ListValueVO listVO = null;
            while (rs.next()) {
                listVO = new ListValueVO(rs.getString("divdept_name"), rs.getString("divdept_id") + ":" + rs.getString("parent_id"));
                list.add(listVO);
            }

        } catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
            loggerValue.append(sqlException);
            loggerValue.append(sqe.getMessage());
            
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserReportDAO[loadDivisionDeptList]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcess);
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            loggerValue.setLength(0);
            loggerValue.append(sqlException);
            loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserReportDAO[loadDivisionDeptList]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, methodName, errorGeneralProcess);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }

            if (log.isDebugEnabled()) {
            	 loggerValue.setLength(0);
                 loggerValue.append("Exiting: divisionDeptList size=");
                 loggerValue.append(list.size());
                log.debug(methodName, loggerValue );
            }
        }
        return list;
    }

    /**
     * Method for loading Report Type List.
     * by ved.sharma
     * Used in(Users Action)
     * 
     * @param con
     *            java.sql.Connection
     * @param periodType
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadReportTypeList(Connection con, String periodType) throws BTSLBaseException {
        final String methodName = "loadReportTypeList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_periodType=" + periodType);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT period_id, period_name ");
        strBuff.append(" FROM period_master WHERE period_type = ? ");
        strBuff.append(" ORDER BY period_id");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName,query + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, periodType);

            rs = pstmt.executeQuery();
            ListValueVO listVO = null;
            while (rs.next()) {
                listVO = new ListValueVO(rs.getString("period_name"), rs.getString("period_id"));
                list.add(listVO);
            }

        } catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserReportDAO[loadReportTypeList]", "", "", "",
                sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcess);
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserReportDAO[loadReportTypeList]", "", "", "",
                exception + ex.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralProcess);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: reportType size=" + list.size());
            }
        }
        return list;
    }

    /**
     * Method for loading Product Type List.
     * by zafar.abbas
     * Used in(Users Reports Action)
     * 
     * @param con
     *            java.sql.Connection
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadProductTypeList(Connection con) throws BTSLBaseException {
        final String methodName = "loadProductTypeList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT lookup_code , lookup_name FROM LOOKUPS");
        strBuff.append(" WHERE lookup_type='PDTYP' AND status='Y'");
        strBuff.append(" ORDER BY lookup_name");

        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, query + sqlSelect);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = con.prepareStatement(sqlSelect);
            rs = pstmt.executeQuery();
            ListValueVO listVO = null;
            while (rs.next()) {
                listVO = new ListValueVO(rs.getString("lookup_name"), rs.getString("lookup_code"));
                list.add(listVO);
            }
        } catch (SQLException sqe) {
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserReportDAO[loadProductTypeList]", "", "", "",
                sqlException + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralSqlProcess);
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserReportDAO[loadProductTypeList]", "", "", "",
                exception + ex.getMessage());
            throw new BTSLBaseException(this, methodName, errorGeneralProcess);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: Product Type List size=" + list.size());
            }
        }
        return list;
    }

    /**
     * 
     */

    public ArrayList<UserClosingBalanceVO> loadUserClosingBalance(Connection con, String networkCode, String zone, String domainCode, String categoryCode, String userId, String loginUserId, Date formDate, Date toDate, String fromAmt, String toAmt, String userType) throws BTSLBaseException {
        final String methodName = "loadUserClosingBalance";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered with ");
        	loggerValue.append(" p_networkCode ");
        	loggerValue.append(networkCode);
        	loggerValue.append(" p_zone ");
        	loggerValue.append(zone);
        	loggerValue.append(" p_domainCode ");
        	loggerValue.append(domainCode);
        	loggerValue.append(" p_categoryCode ");
        	loggerValue.append(categoryCode);
        	loggerValue.append(" p_userId ");
        	loggerValue.append(userId);
        	loggerValue.append(" p_loginUserId");
        	loggerValue.append(loginUserId);
        	loggerValue.append(" p_formDate ");
        	loggerValue.append(formDate);
        	loggerValue.append(" p_toDate ");
        	loggerValue.append(toDate);
        	loggerValue.append(" p_fromAmt ");
        	loggerValue.append(fromAmt);
        	loggerValue.append(" p_toAmt");
        	loggerValue.append(toAmt);
            log.debug(methodName,loggerValue );
        }
        ArrayList<UserClosingBalanceVO> userVOList = null;
        PreparedStatement pstmt = null;
        ResultSet rsUserBal = null;
        UserClosingBalanceVO balanceVO = null;
        try {
            userVOList = new ArrayList<>();
           UserReportQry userReportQry = (UserReportQry)ObjectProducer.getObject(QueryConstants.USER_REPORT_QRY, QueryConstants.QUERY_PRODUCER);
           pstmt = userReportQry.loadUserClosingBalanceQry(con, networkCode, zone, domainCode, categoryCode, userId, loginUserId, formDate, toDate, fromAmt, toAmt, userType);
           rsUserBal = pstmt.executeQuery();
            while (rsUserBal.next()) {
                balanceVO = new UserClosingBalanceVO();
                balanceVO.setUserId(SqlParameterEncoder.encodeParams(rsUserBal.getString("user_id")));
                balanceVO.setUserName(SqlParameterEncoder.encodeParams(rsUserBal.getString("user_name")));
                balanceVO.setUserMSISDN(SqlParameterEncoder.encodeParams(rsUserBal.getString("user_msisdn")));
                balanceVO.setUserCategory(SqlParameterEncoder.encodeParams(rsUserBal.getString("user_category")));
                balanceVO.setUserGeography(SqlParameterEncoder.encodeParams(rsUserBal.getString("user_geography")));
                balanceVO.setParentUserName(SqlParameterEncoder.encodeParams(rsUserBal.getString("parent_name")));
                balanceVO.setParentUserMSISDN(SqlParameterEncoder.encodeParams(rsUserBal.getString("parent_msisdn")));
                balanceVO.setOwnerUserName(SqlParameterEncoder.encodeParams(rsUserBal.getString("owner_user")));
                balanceVO.setOwnerUserMSISDN(SqlParameterEncoder.encodeParams(rsUserBal.getString("owner_msisdn")));
                balanceVO.setGrandUserName(SqlParameterEncoder.encodeParams(rsUserBal.getString("grand_parent")));
                balanceVO.setGrandUserMSISDN(SqlParameterEncoder.encodeParams(rsUserBal.getString("gp_msisdn")));
                balanceVO.setBalanceString(SqlParameterEncoder.encodeParams(rsUserBal.getString("userbal")));
                userVOList.add(balanceVO);
            }
            pstmt.clearParameters();

        } catch (Exception e) {
            log.errorTrace(methodName, e);
            log.error(methodName, e.getMessage());
        } finally {

            try {
                if (rsUserBal != null) {
                    rsUserBal.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }

        }
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exiting: =");
        	loggerValue.append(userVOList.size());
            log.debug(methodName,  loggerValue);
        }
        return userVOList;
    }

}

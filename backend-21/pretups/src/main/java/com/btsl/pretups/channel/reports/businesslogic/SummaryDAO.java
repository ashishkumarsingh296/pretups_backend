package com.btsl.pretups.channel.reports.businesslogic;

import java.sql.Connection;
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

/**
 * @(#)SummaryDAO.java
 *                     Copyright(c) 2006, Bharti Telesoft Ltd.
 *                     All Rights Reserved
 * 
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Author Date History
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     Siddhartha Srivastava 20/12/2006 Initial Creation
 *                     --------------------------------------------------------
 *                     -----------------------------------------
 *                     This class is used for loading the details related to the
 *                     O2CTransferSummary, C2CTransferSummary and
 *                     C2STransferSummary.Based on these details the sumamry
 *                     report is generated
 * 
 */

public class SummaryDAO {
    private Log _log = LogFactory.getLog(this.getClass().getName());
    private SummaryQry summaryQry = (SummaryQry)ObjectProducer.getObject(QueryConstants.SUMMARY_QRY, QueryConstants.QUERY_PRODUCER);
    /**
     * This method load the list of geographical domain type based on the passed
     * network code and the user id.
     * The network code and user id are of the logged in user
     * 
     * @param p_con
     * @param p_networkCode
     * @param p_userID
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadGeoDomainTypeList(Connection p_con, String p_networkCode, String p_userID) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_networkCode:= ");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_userID:= " );
        	loggerValue.append(p_userID);
            _log.debug("loadGeoDomainTypeList",  loggerValue);
        }
        final String METHOD_NAME = "loadGeoDomainTypeList";
        ArrayList geoDomainTypeList = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        final String queryStr = summaryQry.loadGeoDomainTypeListQry();

        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("QUERY queryStr=");
        	loggerValue.append(queryStr);
            _log.debug("loadGeoDomainTypeList", loggerValue );
        }

        try {
            pstmt = p_con.prepareStatement(queryStr);
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, p_userID);

            rs = pstmt.executeQuery();

            geoDomainTypeList = new ArrayList();
            ListValueVO listValueVO = null;
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("grph_domain_type_name"), rs.getString("grph_domain_parent") + ":" + rs.getString("grph_domain_type"));
                geoDomainTypeList.add(listValueVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            _log.error("loadGeoDomainTypeList",  loggerValue );
            _log.errorTrace(METHOD_NAME, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SummaryDAO[loadGeoDomainTypeList]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, "loadGeoDomainTypeList", "error.general.sql.processing");
        } catch (Exception ex) {
        	 loggerValue.setLength(0);
         	loggerValue.append("Exception : ");
         	loggerValue.append(ex);
            _log.error("loadGeoDomainTypeList", loggerValue);
            _log.errorTrace(METHOD_NAME, ex);
            loggerValue.setLength(0);
         	loggerValue.append("Exception:");
         	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SummaryDAO[loadGeoDomainTypeList]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, "loadGeoDomainTypeList", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadGeoDomainTypeList", "Exiting: ");
            }
        }
        return geoDomainTypeList;
    }

    /**
     * This method loads the parent Geographical domain list on the basis of
     * passed network code and the user id
     * 
     * @param p_con
     * @param p_networkCode
     * @param p_userID
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList loadParentGeoDomainList(Connection p_con, String p_networkCode, String p_userID) throws BTSLBaseException {
        final String METHOD_NAME = "loadParentGeoDomainList";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_networkCode:= ");
        	loggerValue.append(p_networkCode);
        	loggerValue.append(" p_userID:= ");
        	loggerValue.append(p_userID);
            _log.debug("loadParentGeoDomainList",  loggerValue);
        }
        ArrayList parentGeoDomainList = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {

            final String queryStr = summaryQry.loadParentGeoDomainListQry();

            if (_log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("QUERY queryStr=");
            	loggerValue.append(" p_userID:= ");
                _log.debug("loadParentGeoDomainList", loggerValue );
            }

            pstmt = p_con.prepareStatement(queryStr);
            pstmt.setString(1, p_networkCode);
            pstmt.setString(2, p_userID);
            parentGeoDomainList = new ArrayList();
            rs = pstmt.executeQuery();
            ListValueVO listValueVO = null;
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("grph_domain_name"), rs.getString("grph_domain_type") + ":" + rs.getString("grph_domain_code"));
                parentGeoDomainList.add(listValueVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            _log.error("loadParentGeoDomainList",  loggerValue);
            _log.errorTrace(METHOD_NAME, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:" );
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SummaryDAO[loadParentGeoDomainList]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, "loadParentGeoDomainList", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception:" );
        	loggerValue.append(ex);
            _log.error("loadParentGeoDomainList", loggerValue);
            _log.errorTrace(METHOD_NAME, ex);
            loggerValue.setLength(0);
        	loggerValue.append( "Exception:" );
        	loggerValue.append( ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SummaryDAO[loadParentGeoDomainList]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, "loadParentGeoDomainList", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadParentGeoDomainList", "Exiting: ");
            }
        }

        return parentGeoDomainList;
    }

    /**
     * This method is called when the user clicks on the search geographical
     * domain.It return the list of geographical
     * domains which are set in the form.The search is based on the parent
     * geographical domain , geographical domain type
     * and network code
     * 
     * @param p_con
     * @param p_geoDomainType
     * @param p_parentGeoDomain
     * @param p_geoDomain
     * @param p_networkCode
     * @throws BTSLBaseException
     */
    public ArrayList loadGeogDomainList(Connection p_con, String p_parentGeoDomain, String p_geoDomain, String p_networkCode) throws BTSLBaseException {
        final String METHOD_NAME = "loadGeogDomainList";
        StringBuilder loggerValue= new StringBuilder(); 
        if (_log.isDebugEnabled()) {
        	
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: p_parentGeoDomain:= ");
        	loggerValue.append(p_parentGeoDomain);
        	loggerValue.append(" p_geoDomain:= ");
        	loggerValue.append(p_geoDomain);
        	loggerValue.append(" p_networkCode=");
        	loggerValue.append(p_networkCode);
            _log.debug("loadGeogDomainList",  loggerValue );
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList loadGeogDomainList = null;
        try {
            final StringBuffer qryBuff = new StringBuffer();
            qryBuff.append("SELECT GD.grph_domain_code, GD.grph_domain_name ");
            qryBuff.append("FROM geographical_domains GD ");
            qryBuff.append("WHERE GD.parent_grph_domain_code=? ");
            qryBuff.append("AND GD.network_code=? ");
            qryBuff.append("AND GD.status IN ('Y','S') ");
            qryBuff.append("AND UPPER(GD.grph_domain_name) LIKE UPPER(?)");

            final String queryStr = qryBuff.toString();

            pstmt = p_con.prepareStatement(queryStr);
            pstmt.setString(1, p_parentGeoDomain);
            pstmt.setString(2, p_networkCode);
            pstmt.setString(3, p_geoDomain);
            rs = pstmt.executeQuery();

            loadGeogDomainList = new ArrayList();
            ListValueVO listValueVO = null;
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("grph_domain_name"), rs.getString("grph_domain_code"));
                loadGeogDomainList.add(listValueVO);
            }
        } catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException : ");
        	loggerValue.append(sqe);
            _log.error("loadGeogDomainList", loggerValue);
            _log.errorTrace(METHOD_NAME, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:" );
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SummaryDAO[loadGeogDomainList]", "", "", "",
            		loggerValue.toString());
            
            throw new BTSLBaseException(this, "loadGeogDomainList", "error.general.sql.processing");
        } catch (Exception ex) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception : ");
        	loggerValue.append(ex);
            _log.error("loadGeogDomainList", loggerValue );
            _log.errorTrace(METHOD_NAME, ex);
            loggerValue.setLength(0);
        	loggerValue.append( "Exception:");
        	loggerValue.append(ex.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SummaryDAO[loadGeogDomainList]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, "loadGeogDomainList", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadGeogDomainList", "Exiting: ");
            }
        }
        return loadGeogDomainList;

    }
}

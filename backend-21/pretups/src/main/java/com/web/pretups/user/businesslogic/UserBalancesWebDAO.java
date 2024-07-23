package com.web.pretups.user.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.query.businesslogic.C2sBalanceQueryVO;

public class UserBalancesWebDAO {

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method loadUserDetails.
     * This method loads all of the products available in the specified network.
     * 
     * @param p_con
     *            Connection
     * @param p_category
     *            String
     * @param p_grphDomainCode
     *            String
     * @param p_searchStr
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList<ListValueVO> loadUserDetails(Connection p_con, String p_category, String p_grphDomainCode, String p_searchStr) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
        	StringBuilder sb=new StringBuilder("");
        	sb.append("Entered p_category=");
        	sb.append(p_category);
        	sb.append("p_grphDomainCode=");
        	sb.append(p_grphDomainCode);
        	sb.append("p_searchStr=");
        	sb.append(p_searchStr);
        	String msg = sb.toString();
            _log.debug("loadUserDetails",msg);
        }
        final String METHOD_NAME = "loadUserDetails";
        final ArrayList<ListValueVO> userList = new ArrayList<ListValueVO>();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            final StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT u.user_name,u.user_id FROM users u,user_geographies ug");
            selectQuery.append(" WHERE u.category_code=? AND u.user_id=ug.user_id AND ug.grph_domain_code=?");
            selectQuery.append(" AND UPPER(u.user_name) LIKE UPPER(?) ");
            selectQuery.append(" AND u.status <> 'N' AND u.status <> 'C' ORDER BY u.user_name");
            if (_log.isDebugEnabled()) {
                _log.debug("loadUserDetails", "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, p_category);
            pstmtSelect.setString(2, p_grphDomainCode);
            pstmtSelect.setString(3, p_searchStr);
            rs = pstmtSelect.executeQuery();
            ListValueVO listValueVO = null;
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("user_name") + "(" + rs.getString("user_id") + ")", rs.getString("user_id"));
                userList.add(listValueVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadUserDetails", "SQLException:" + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesWebDAO[loadUserDetails]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUserDetails", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadUserDetails", "Exception:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesWebDAO[loadUserDetails]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadUserDetails", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadUserDetails", "Exiting:list size=" + userList.size());
            }
        }
        return userList;
    }

    /**
     * Method loadUserBalancesForMsisdn.
     * This method loads the balnaces of the user corresponding to the msisdn
     * entered
     * 
     * @param p_con
     *            Connection
     * @param p_msisdn
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList<C2sBalanceQueryVO> loadUserBalancesForMsisdn(Connection p_con, String p_msisdn) throws BTSLBaseException {
        if (_log.isDebugEnabled()) {
            _log.debug("loadUserBalancesForMsisdn", "Entered p_msisdn=" + p_msisdn);
        }
        final String METHOD_NAME = "loadUserBalancesForMsisdn";
        final ArrayList<C2sBalanceQueryVO> userList = new ArrayList<C2sBalanceQueryVO>();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            final StringBuffer selectQuery = new StringBuffer();
            selectQuery.append("SELECT ub.balance,ub.prev_balance,p.product_short_code,p.product_name,c.category_name,");
            selectQuery.append("d.domain_name,gd.grph_domain_name,u.user_name,u.address1 FROM users u,user_balances ub,products p,");
            selectQuery.append("user_phones up,categories c,domains d,user_geographies ug,geographical_domains gd ");
            selectQuery.append("WHERE ub.product_code=p.product_code AND u.user_id=ub.user_id ");
            selectQuery.append("AND c.category_code=u.category_code AND d.domain_code=c.domain_code ");
            selectQuery.append("AND ug.user_id=up.user_id AND ug.grph_domain_code=gd.grph_domain_code ");
            selectQuery.append("AND up.user_id=ub.user_id AND up.msisdn=? AND u.status<>'N' AND u.status<>'C'");
            if (_log.isDebugEnabled()) {
                _log.debug("loadUserBalancesForMsisdn", "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, p_msisdn);
            rs = pstmtSelect.executeQuery();
            C2sBalanceQueryVO balanceVO = null;
            while (rs.next()) {
                balanceVO = new C2sBalanceQueryVO();
                balanceVO.setBalance(rs.getLong("balance"));
                balanceVO.setProductShortCode(rs.getString("product_short_code"));
                balanceVO.setProductName(rs.getString("product_name"));
                balanceVO.setGrphDomainName(rs.getString("grph_domain_name"));
                balanceVO.setDomainName(rs.getString("domain_name"));
                balanceVO.setCategoryName(rs.getString("category_name"));
                balanceVO.setUserName(rs.getString("user_name"));
                balanceVO.setAddress(rs.getString("address1"));
                userList.add(balanceVO);
            }
        } catch (SQLException sqe) {
            _log.error("loadUserBalancesForMsisdn", "SQLException:" + sqe.getMessage());
            _log.errorTrace(METHOD_NAME, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesWebDAO[loadUserBalancesForMsisdn]", "",
                "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadUserBalancesForMsisdn", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error("loadUserBalancesForMsisdn", "Exception:" + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserBalancesWebDAO[loadUserBalancesForMsisdn]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadUserBalancesForMsisdn", "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(METHOD_NAME, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug("loadUserBalancesForMsisdn", "Exiting:list size=" + userList.size());
            }
        }
        return userList;
    }
}

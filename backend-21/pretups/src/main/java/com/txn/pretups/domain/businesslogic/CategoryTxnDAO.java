package com.txn.pretups.domain.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;

public class CategoryTxnDAO {

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getFactory().getInstance(CategoryTxnDAO.class.getName());

    /**
     * Constructor for categoryTxnDAO.
     */
    public CategoryTxnDAO() {
        super();
    }

    // 01-APR-2104
    /**
     * Method loadCategoryDetails.
     * This method is used to load category details according to domain code
     * from Categories Table
     * 
     * @param p_con
     *            Connection
     * @param p_categoryCode
     *            String
     * @return categoryList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadOptCategoryDetailsUsingCategoryCode(Connection p_con, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "loadOptCategoryDetailsUsingCategoryCode";
        if (_log.isDebugEnabled()) {
            _log.debug("loadCategoryDetailsUsingCategoryCode", "Entered p_categoryCode=" + p_categoryCode);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        ArrayList categoryList = new ArrayList();
        StringBuffer strBuff = new StringBuffer(" SELECT D.domain_name,D.domain_type_code,C.modify_allowed,C.category_code,");
        strBuff.append("C.category_name,C.domain_code,C.sequence_no,C.grph_domain_type,C.multiple_grph_domains,");
        strBuff.append("C.web_interface_allowed,C.sms_interface_allowed,C.fixed_roles,C.multiple_login_allowed,");
        strBuff.append("C.view_on_network_block,C.max_login_count,C.status,C.created_on,C.created_by,C.modified_on,");
        strBuff.append("C.modified_by,C.display_allowed,C.modify_allowed,C.product_types_allowed,C.services_allowed,");
        strBuff.append("C.domain_allowed,C.fixed_domains,C.max_txn_msisdn,C.uncntrl_transfer_allowed, C.transfertolistonly,C.authentication_type, ");
        // Added on 13/07/07 for Low balance alert allow
        strBuff.append("C.low_bal_alert_allow,");
        // End of low balance alert allow
        strBuff.append("C.cp2p_payee_status,C.cp2p_payer_status,C.c2s_payee_status ,C.cp2p_within_list,C.cp2p_within_list_level,");
        strBuff.append("C.scheduled_transfer_allowed,C.restricted_msisdns,C.parent_category_code,C.user_id_prefix,C.outlets_allowed ,C.category_type,C.agent_allowed,C.hierarchy_allowed ");
        strBuff.append(" FROM categories C, Domains D ,LOOKUPS LK WHERE C.category_code=? AND C.domain_code=D.domain_Code ");
        strBuff.append(" AND C.status <>? ");

        String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query= " + sqlSelect);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_categoryCode);
            pstmtSelect.setString(2, PretupsI.CATEGORY_STATUS_DELETE);
            rs = pstmtSelect.executeQuery();
            CategoryVO categoryVO = null;
            if (rs.next()) {
                categoryVO = new CategoryVO();
                categoryVO.setDomainName(rs.getString("domain_name"));
                categoryVO.setDomainTypeCode(rs.getString("domain_type_code"));
                categoryVO.setCategoryCode(rs.getString("category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
                categoryVO.setParentCategoryCode(rs.getString("parent_category_code"));
                categoryVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                categoryVO.setCategoryStatus(rs.getString("status"));
                categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
                categoryVO.setMultipleGrphDomains(rs.getString("multiple_grph_domains"));
                categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
                categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
                categoryVO.setFixedRoles(rs.getString("fixed_roles"));
                categoryVO.setMultipleLoginAllowed(rs.getString("multiple_login_allowed"));
                categoryVO.setViewOnNetworkBlock(rs.getString("view_on_network_block"));
                categoryVO.setMaxLoginCount(rs.getLong("max_login_count"));
                categoryVO.setDisplayAllowed(rs.getString("display_allowed"));
                categoryVO.setModifyAllowed(rs.getString("modify_allowed"));
                categoryVO.setProductTypeAssociationAllowed(rs.getString("product_types_allowed"));
                categoryVO.setServiceAllowed(rs.getString("services_allowed"));
                categoryVO.setMaxTxnMsisdn(rs.getString("max_txn_msisdn"));
                categoryVO.setUnctrlTransferAllowed(rs.getString("uncntrl_transfer_allowed"));
                // added by Abhilasha for Authentication Type
                categoryVO.setAuthenticationType(rs.getString("authentication_type"));
                categoryVO.setScheduledTransferAllowed(rs.getString("scheduled_transfer_allowed"));
                categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
                categoryVO.setParentCategoryCode(rs.getString("parent_category_code"));
                categoryVO.setUserIdPrefix(rs.getString("user_id_prefix"));
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                categoryVO.setDomainAllowed(rs.getString("domain_allowed"));
                categoryVO.setFixedDomains(rs.getString("fixed_domains"));
                categoryVO.setOutletsAllowed(rs.getString("outlets_allowed"));
                // categoryVO.setCategoryType(rs.getString("lookup_name"));
                categoryVO.setCategoryTypeCode(rs.getString("category_type"));
                categoryVO.setAgentAllowed(rs.getString("agent_allowed"));
                categoryVO.setHierarchyAllowed(rs.getString("hierarchy_allowed"));
                categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
                // Added on 13/07/07 for Low balance alert allow
                categoryVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
                // End of Low balance alert allow
                categoryVO.setRechargeByParentOnly(rs.getString("c2s_payee_status"));
                categoryVO.setCp2pPayee(rs.getString("cp2p_payee_status"));
                categoryVO.setCp2pPayer(rs.getString("cp2p_payer_status"));
                categoryVO.setCp2pWithinList(rs.getString("cp2p_within_list"));
                categoryVO.setParentOrOwnerRadioValue(rs.getString("cp2p_within_list_level"));
                categoryList.add(categoryVO);
            }
        }

        catch (SQLException sqe) {
            _log.error(methodName, "SQL Exception" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryTxnDAO[loadCategoryDetailsUsingCategoryCode]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, " Exception" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryTxnDAO[loadCategoryDetailsUsingCategoryCode]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting size=" + categoryList.size());
            }
        }

        return categoryList;
    }

    /**
     * This method checks whether category code exists in DB or not,if
     * yes,returns CatgeoryVO
     * 
     * @author ankur.dhawan
     * @param p_con
     *            Connection
     * @param p_categoryCode
     *            java.lang.String
     * @return boolean
     * @throws BTSLBaseException
     */

    public CategoryVO isValidCategoryCode(Connection p_con, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "isValidCategoryCode";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered params p_categoryVO::" + p_categoryCode);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        CategoryVO catVO = new CategoryVO();
        boolean found = false;
        StringBuffer sqlBuff = new StringBuffer("SELECT sequence_no,domain_code FROM categories WHERE category_code=? and status=? ");
        String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Select Query::" + selectQuery);
        }
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_categoryCode.toUpperCase());
            pstmtSelect.setString(2, PretupsI.YES);
            rs = pstmtSelect.executeQuery();

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed::" + selectQuery);
            }

            if (rs.next()) {
                catVO.setSequenceNumber(rs.getInt("sequence_no"));
                catVO.setDomainCodeforCategory(rs.getString("domain_code"));
            }
        } catch (SQLException sqle) {
            _log.error(methodName, "SQLException " + sqle.getMessage());
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryTxnDAO[isValidCategoryCode]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryTxnDAO[isValidCategoryCode]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Exiting isValid found=" + found);
            }
        }
        return catVO;
    }

}

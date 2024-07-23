package com.web.pretups.domain.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
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
import com.btsl.pretups.domain.businesslogic.CategoryRoleDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SqlParameterEncoder;
import com.web.pretups.roles.businesslogic.UserRolesWebDAO;

public class CategoryWebDAO {

    /**
     * Field _log.
     */
    private Log _log = LogFactory.getFactory().getInstance(CategoryWebDAO.class.getName());
    private static CategoryWebQry categoryWebQry;
    /**
     * Constructor for CategoryWebDAO.
     */
    public CategoryWebDAO() {
        super();
        categoryWebQry = (CategoryWebQry) ObjectProducer.getObject(QueryConstants.CATEGORY_WEB_QRY,QueryConstants.QUERY_PRODUCER);
    }

    /**
     * Method loadCategoryDetails.
     * This method is used to load category details according to domain code
     * from Categories Table
     *
     * @param p_con
     *            Connection
     * @param p_domainCode
     *            String
     * @return categoryList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadCategoryDetails(Connection p_con, String p_domainCode) throws BTSLBaseException {
        final String methodName = "loadCategoryDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_domainCode =");
            loggerValue.append(p_domainCode);
            _log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList categoryList = new ArrayList();
        final StringBuffer strBuff = new StringBuffer(" SELECT D.num_of_categories,C.modify_allowed,C.category_code,");
        strBuff.append("C.category_name,C.domain_code,C.sequence_no,C.grph_domain_type,C.multiple_grph_domains,");
        strBuff.append("C.web_interface_allowed,C.sms_interface_allowed,C.fixed_roles,C.multiple_login_allowed,");
        strBuff.append("C.view_on_network_block,C.max_login_count,C.status,C.created_on,C.created_by,C.modified_on,");
        strBuff.append("C.modified_by,C.display_allowed,C.modify_allowed,C.product_types_allowed,C.services_allowed,");
        strBuff.append("C.domain_allowed,C.fixed_domains,C.max_txn_msisdn,C.uncntrl_transfer_allowed,C.transfertolistonly,");
        // Added on 13/07/07 for Low balance alert allow
        strBuff.append("C.low_bal_alert_allow,");
        // End of low balance alert allow
        // added by santanu
        strBuff.append("C.cp2p_payee_status,C.cp2p_payer_status,C.c2s_payee_status ,C.cp2p_within_list,C.cp2p_within_list_level,");
        strBuff
                .append("C.scheduled_transfer_allowed,C.restricted_msisdns,C.parent_category_code,C.user_id_prefix,C.outlets_allowed ,C.category_type,C.agent_allowed,C.hierarchy_allowed,LK.lookup_name,c.AUTHENTICATION_TYPE");
        strBuff.append(" FROM categories C,domains D ,LOOKUPS LK WHERE D.domain_code=? AND D.domain_code=C.domain_code");
        strBuff.append(" AND C.status <>? AND C.display_allowed=? AND C.modify_allowed=? AND LK.lookup_code=C.category_type AND LK.lookup_type=? ORDER BY C.sequence_no");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlSelect);
            _log.debug(methodName, loggerValue);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_domainCode);
            pstmtSelect.setString(2, PretupsI.CATEGORY_STATUS_DELETE);
            pstmtSelect.setString(3, PretupsI.CATEGORY_DISPLAY_ALLOWED);
            pstmtSelect.setString(4, PretupsI.CATEGORY_MODIFY_ALLOWED);
            pstmtSelect.setString(5, PretupsI.CATEGORY_TYPE_CODE);
            rs = pstmtSelect.executeQuery();
            int radioIndex = 0;
            CategoryVO categoryVO = null;

            while (rs.next()) {
                categoryVO = new CategoryVO();
                categoryVO.setCategoryCode(rs.getString("category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
                categoryVO.setParentCategoryCode(rs.getString("parent_category_code"));
                categoryVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                categoryVO.setRecordCount(rs.getInt("num_of_categories"));
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
                categoryVO.setScheduledTransferAllowed(rs.getString("scheduled_transfer_allowed"));
                categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
                categoryVO.setParentCategoryCode(rs.getString("parent_category_code"));
                categoryVO.setUserIdPrefix(rs.getString("user_id_prefix"));
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                categoryVO.setDomainAllowed(rs.getString("domain_allowed"));
                categoryVO.setFixedDomains(rs.getString("fixed_domains"));
                categoryVO.setOutletsAllowed(rs.getString("outlets_allowed"));
                categoryVO.setCategoryType(rs.getString("lookup_name"));
                categoryVO.setCategoryTypeCode(rs.getString("category_type"));
                categoryVO.setAgentAllowed(rs.getString("agent_allowed"));
                categoryVO.setHierarchyAllowed(rs.getString("hierarchy_allowed"));
                categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
                // Added on 13/07/07 for Low balance alert allow
                categoryVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
                // End of low balance alert allow
                // add
                categoryVO.setRechargeByParentOnly(rs.getString("c2s_payee_status"));
                categoryVO.setCp2pPayee(rs.getString("cp2p_payee_status"));
                categoryVO.setCp2pPayer(rs.getString("cp2p_payer_status"));
                categoryVO.setCp2pWithinList(rs.getString("cp2p_within_list"));
                categoryVO.setParentOrOwnerRadioValue(rs.getString("cp2p_within_list_level"));
                categoryVO.setAuthenticationType(rs.getString("AUTHENTICATION_TYPE"));
                categoryVO.setRadioIndex(radioIndex);
                categoryList.add(categoryVO);
                radioIndex++;
            }
        }

        catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryDetails]", "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryDetails]", "", "", "",
                    "Exception:" + e.getMessage());
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: size : " + categoryList.size());
                _log.debug(methodName, msg.toString());
            }
        }

        return categoryList;
    }

    /**
     * Method loadCategoryVOList
     * This method is used to load (category code:domain code) and category name
     * in the
     * list value bean this method is used for dynamically select other combo in
     * the
     * basis of previous one.
     *
     * @param p_con
     *            Connection
     * @return categoryList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadCategoryVOList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadCategoryVOList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        StringBuilder loggerValue = new StringBuilder();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList categoryListVO = new ArrayList<ListValueVO>();
        final StringBuffer strBuff = new StringBuffer(" SELECT domain_code,category_code,");
        strBuff.append("category_name FROM categories WHERE status=? AND display_allowed=?");
        strBuff.append(" AND modify_allowed=? ORDER BY sequence_no");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlSelect);
            _log.debug(methodName, loggerValue);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, PretupsI.CATEGORY_STATUS_ACTIVE);
            pstmtSelect.setString(2, PretupsI.CATEGORY_DISPLAY_ALLOWED);
            pstmtSelect.setString(3, PretupsI.CATEGORY_MODIFY_ALLOWED);
            rs = pstmtSelect.executeQuery();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed= " + sqlSelect);
            }
            while (rs.next()) {
                categoryListVO.add(new ListValueVO(rs.getString("category_name"), rs.getString("domain_code") + ":" + rs.getString("category_code")));
            }
        }

        catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryVOList]", "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryVOList]", "", "", "",
                    "Exception:" + e.getMessage());
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: size : " + categoryListVO.size());
                _log.debug(methodName, msg.toString());
            }
        }

        return categoryListVO;
    }

    /**
     * Method loadCategoryForSmsVO
     * This method is used to load category code and name for which sms
     * interface allowed
     * and the user type is not operator type
     *
     * @param p_con
     *            Connection
     * @return categoryList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadCategoryForSmsVO(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadCategoryForSmsVO";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        StringBuilder loggerValue = new StringBuilder();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList categoryListVO = new ArrayList();
        final StringBuffer strBuff = new StringBuffer(" SELECT category_code,category_name");
        strBuff.append(" FROM categories WHERE status=? AND domain_code!=?");
        strBuff.append(" AND sms_interface_allowed=? ORDER BY sequence_no");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlSelect);
            _log.debug(methodName, loggerValue);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, PretupsI.CATEGORY_STATUS_ACTIVE);
            pstmtSelect.setString(2, PretupsI.OPERATOR_TYPE_OPT);
            pstmtSelect.setString(3, PretupsI.SMS_INTERFACE_ALLOWED);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                categoryListVO.add(new ListValueVO(rs.getString("category_name"), rs.getString("category_code")));
            }
        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryForSmsVO]", "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryForSmsVO]", "", "", "",
                    "Exception:" + e.getMessage());
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: size : " + categoryListVO.size());
                _log.debug(methodName, msg.toString());
            }
        }

        return categoryListVO;
    }

    /**
     * Method loadCategoryCodeNameListVO
     * This method is used to load category code and category name in the list
     * value
     * bean this method is used for generating arraylist of list value VO's
     * that contains category_code and category_name
     *
     * @param p_con
     *            Connection
     * @return categoryList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadCategoryCodeNameListVO(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadCategoryCodeNameListVO";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        StringBuilder loggerValue = new StringBuilder();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList categoryListVO = new ArrayList();
        final StringBuffer strBuff = new StringBuffer(" SELECT category_code,category_name ");
        strBuff.append("FROM categories WHERE status=? AND display_allowed=?");
        strBuff.append(" AND modify_allowed=? ORDER BY sequence_no");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlSelect);
            _log.debug(methodName, loggerValue);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, PretupsI.CATEGORY_STATUS_ACTIVE);
            pstmtSelect.setString(2, PretupsI.CATEGORY_DISPLAY_ALLOWED);
            pstmtSelect.setString(3, PretupsI.CATEGORY_MODIFY_ALLOWED);

            rs = pstmtSelect.executeQuery();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed= " + sqlSelect);
            }

            while (rs.next()) {
                categoryListVO.add(new ListValueVO(rs.getString("category_name"), rs.getString("category_code")));
            }
        }

        catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryCodeNameListVO]", "", "",
                    "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryCodeNameListVO]", "", "",
                    "", "Exception:" + e.getMessage());
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: size : " + categoryListVO.size());
                _log.debug(methodName, msg.toString());
            }
        }

        return categoryListVO;
    }

    /**
     * Method loadAvalibleCategoryForDomain.
     * This method is used to load avaliable categories for the domain selected
     *
     * @param p_con
     *            Connection
     * @param p_domainCode
     *            String
     * @return maxSequenceNumber int
     * @throws BTSLBaseException
     */

    public int loadAvalibleCategoryForDomain(Connection p_con, String p_domainCode) throws BTSLBaseException {
        final String methodName = "loadAvalibleCategoryForDomain";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_domainCode =");
            loggerValue.append(p_domainCode);
            _log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        int avaliableCategories = -1;
        final StringBuffer strBuff = new StringBuffer("SELECT count(*) as max FROM categories WHERE domain_code=? AND status<>'N'");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlSelect);
            _log.debug(methodName, loggerValue);
        }
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_domainCode);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                avaliableCategories = rs.getInt("max");
            }
        }

        catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadAvalibleCategoryForDomain]", "",
                    "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadAvalibleCategoryForDomain]", "",
                    "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");

        }

        finally {
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: avaliableCategories : " + avaliableCategories);
                _log.debug(methodName, msg.toString());
            }
        }

        return avaliableCategories;
    }

    /**
     * Method saveCategory.
     * This method is used to save the Category information in categories table.
     *
     * @param p_con
     *            Connection
     * @param p_categoryVO
     *            CategoryVO
     * @return addCount int
     * @throws BTSLBaseException
     */

    public int saveCategory(Connection p_con, CategoryVO p_categoryVO) throws BTSLBaseException {
        final String methodName = "saveCategory";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: VO =");
            loggerValue.append(p_categoryVO);
            _log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtInsert = null;
        int addCount = -1;
        final String sqlInsert=categoryWebQry.saveCategoryQry();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Insert Query =");
            loggerValue.append(sqlInsert);
            _log.debug(methodName, loggerValue);
        }
        try {
            pstmtInsert = (PreparedStatement) p_con.prepareStatement(sqlInsert);
            pstmtInsert.setString(1, p_categoryVO.getCategoryCode());
            pstmtInsert.setString(2, p_categoryVO.getCategoryName());
            pstmtInsert.setString(3, p_categoryVO.getDomainCodeforCategory());
            pstmtInsert.setInt(4, p_categoryVO.getSequenceNumber());
            pstmtInsert.setString(5, p_categoryVO.getGrphDomainType());
            pstmtInsert.setString(6, p_categoryVO.getMultipleGrphDomains());
            pstmtInsert.setString(7, p_categoryVO.getWebInterfaceAllowed());
            pstmtInsert.setString(8, p_categoryVO.getSmsInterfaceAllowed());
            pstmtInsert.setString(9, p_categoryVO.getFixedRoles());
            pstmtInsert.setString(10, p_categoryVO.getMultipleLoginAllowed());
            pstmtInsert.setString(11, p_categoryVO.getViewOnNetworkBlock());
            pstmtInsert.setLong(12, p_categoryVO.getMaxLoginCount());
            pstmtInsert.setString(13, p_categoryVO.getCategoryStatus());
            pstmtInsert.setTimestamp(14, BTSLUtil.getTimestampFromUtilDate(p_categoryVO.getCreatedOn()));
            pstmtInsert.setString(15, p_categoryVO.getCreatedBy());
            pstmtInsert.setTimestamp(16, BTSLUtil.getTimestampFromUtilDate(p_categoryVO.getModifiedOn()));
            pstmtInsert.setString(17, p_categoryVO.getModifiedBy());
            pstmtInsert.setString(18, p_categoryVO.getDisplayAllowed());
            pstmtInsert.setString(19, p_categoryVO.getModifyAllowed());
            pstmtInsert.setString(20, p_categoryVO.getProductTypeAssociationAllowed());
            pstmtInsert.setString(21, p_categoryVO.getServiceAllowed());
            pstmtInsert.setString(22, p_categoryVO.getMaxTxnMsisdn());
            pstmtInsert.setString(23, p_categoryVO.getUnctrlTransferAllowed());
            pstmtInsert.setString(24, p_categoryVO.getScheduledTransferAllowed());
            pstmtInsert.setString(25, p_categoryVO.getRestrictedMsisdns());
            pstmtInsert.setString(26, p_categoryVO.getParentCategoryCode());
            pstmtInsert.setString(27, p_categoryVO.getUserIdPrefix().toUpperCase());
            pstmtInsert.setString(28, p_categoryVO.getOutletsAllowed());
            pstmtInsert.setString(29, p_categoryVO.getAgentAllowed());
            pstmtInsert.setString(30, p_categoryVO.getHierarchyAllowed());
            pstmtInsert.setString(31, p_categoryVO.getCategoryType());
            pstmtInsert.setString(32, p_categoryVO.getTransferToListOnly());
            // Added for low balance alert
            pstmtInsert.setString(33, p_categoryVO.getLowBalAlertAllow());
            pstmtInsert.setString(34, p_categoryVO.getRechargeByParentOnly());
            pstmtInsert.setString(35, p_categoryVO.getCp2pPayee());
            pstmtInsert.setString(36, p_categoryVO.getCp2pPayer());
            pstmtInsert.setString(37, p_categoryVO.getCp2pWithinList());
            pstmtInsert.setString(38, p_categoryVO.getParentOrOwnerRadioValue());
            // changes done for Authentication Type
            if (!BTSLUtil.isNullString(p_categoryVO.getAuthenticationType())) {
                pstmtInsert.setString(39, p_categoryVO.getAuthenticationType());
            } else {
                pstmtInsert.setString(39, PretupsI.NOT_APPLICABLE);
            }

            addCount = pstmtInsert.executeUpdate();
            if (addCount > 0) {
                addCount = this.addStkProfile(p_con, p_categoryVO);
            }
        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[saveCategory]", "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[saveCategory]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");

        } finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: addCount : " + addCount);
                _log.debug(methodName, msg.toString());
            }
        }
        return addCount;
    }




    /**
     * Method saveCategory.
     * This method is used to save the Category information in categories table.
     *
     * @param p_con
     *            Connection
     * @param p_categoryVO
     *            CategoryVO
     * @return addCount int
     * @throws BTSLBaseException
     */

    public int saveAgentCategory(Connection p_con, CategoryVO p_categoryVO) throws BTSLBaseException {
        final String methodName = "saveCategory";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: VO =");
            loggerValue.append(p_categoryVO);
            _log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtInsert = null;
        int addCount = -1;
        final String sqlInsert=categoryWebQry.saveCategoryQry();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Insert Query =");
            loggerValue.append(sqlInsert);
            _log.debug(methodName, loggerValue);
        }
        try {
            pstmtInsert = (PreparedStatement) p_con.prepareStatement(sqlInsert);
            pstmtInsert.setString(1, p_categoryVO.getCategoryCode());
            pstmtInsert.setString(2, p_categoryVO.getCategoryName());
            pstmtInsert.setString(3, p_categoryVO.getDomainCodeforCategory());
            pstmtInsert.setInt(4, p_categoryVO.getSequenceNumber());
            pstmtInsert.setString(5, p_categoryVO.getGrphDomainType());
            pstmtInsert.setString(6, p_categoryVO.getMultipleGrphDomains());
            pstmtInsert.setString(7, p_categoryVO.getWebInterfaceAllowed());
            pstmtInsert.setString(8, p_categoryVO.getSmsInterfaceAllowed());
            pstmtInsert.setString(9, p_categoryVO.getFixedRoles());
            pstmtInsert.setString(10, p_categoryVO.getMultipleLoginAllowed());
            pstmtInsert.setString(11, p_categoryVO.getViewOnNetworkBlock());
            pstmtInsert.setLong(12, p_categoryVO.getMaxLoginCount());
            pstmtInsert.setString(13, p_categoryVO.getCategoryStatus());
            pstmtInsert.setTimestamp(14, BTSLUtil.getTimestampFromUtilDate(p_categoryVO.getCreatedOn()));
            pstmtInsert.setString(15, p_categoryVO.getCreatedBy());
            pstmtInsert.setTimestamp(16, BTSLUtil.getTimestampFromUtilDate(p_categoryVO.getModifiedOn()));
            pstmtInsert.setString(17, p_categoryVO.getModifiedBy());
            pstmtInsert.setString(18, p_categoryVO.getDisplayAllowed());
            pstmtInsert.setString(19, p_categoryVO.getModifyAllowed());
            pstmtInsert.setString(20, p_categoryVO.getProductTypeAssociationAllowed());
            pstmtInsert.setString(21, p_categoryVO.getServiceAllowed());
            pstmtInsert.setString(22, p_categoryVO.getMaxTxnMsisdn());
            pstmtInsert.setString(23, p_categoryVO.getUnctrlTransferAllowed());
            pstmtInsert.setString(24, p_categoryVO.getScheduledTransferAllowed());
            pstmtInsert.setString(25, p_categoryVO.getRestrictedMsisdns());
            pstmtInsert.setString(26, p_categoryVO.getParentCategoryCode());
            pstmtInsert.setString(27, p_categoryVO.getUserIdPrefix());
            pstmtInsert.setString(28, p_categoryVO.getOutletsAllowed());
            pstmtInsert.setString(29, p_categoryVO.getAgentAllowed());
            pstmtInsert.setString(30, p_categoryVO.getHierarchyAllowed());
            pstmtInsert.setString(31, p_categoryVO.getCategoryType());
            pstmtInsert.setString(32, p_categoryVO.getTransferToListOnly());
            // Added for low balance alert
            pstmtInsert.setString(33, p_categoryVO.getLowBalAlertAllow());
            pstmtInsert.setString(34, p_categoryVO.getRechargeByParentOnly());
            pstmtInsert.setString(35, p_categoryVO.getCp2pPayee());
            pstmtInsert.setString(36, p_categoryVO.getCp2pPayer());
            pstmtInsert.setString(37, p_categoryVO.getCp2pWithinList());
            pstmtInsert.setString(38, p_categoryVO.getParentOrOwnerRadioValue());
            // changes done for Authentication Type
            if (!BTSLUtil.isNullString(p_categoryVO.getAuthenticationType())) {
                pstmtInsert.setString(39, p_categoryVO.getAuthenticationType());
            } else {
                pstmtInsert.setString(39, PretupsI.NOT_APPLICABLE);
            }

            addCount = pstmtInsert.executeUpdate();
            if (addCount > 0) {
                addCount = this.addStkProfile(p_con, p_categoryVO);
            }
        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[saveCategory]", "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[saveCategory]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");

        } finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: addCount : " + addCount);
                _log.debug(methodName, msg.toString());
            }
        }
        return addCount;
    }




    /**
     * Method addStkProfile
     * This method is used to Modify the Details of Categories in the categories
     * table
     *
     * @param p_con
     *            Connection
     * @param p_categoryVO
     *            CategoryVO
     * @return int
     * @throws BTSLBaseException
     */

    private int addStkProfile(Connection p_con, CategoryVO p_categoryVO) throws BTSLBaseException {

        final String methodName = "addStkProfile";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: VO =");
            loggerValue.append(p_categoryVO);
            _log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtInsert = null;
        int addCount = -1;
        final StringBuffer strBuff = new StringBuffer("INSERT INTO stk_profiles (profile_code,");
        strBuff.append("category_code,profile_name,status,");
        strBuff.append("created_on,created_by,modified_on,modified_by)");
        strBuff.append(" VALUES(UPPER(?),UPPER(?),?,?,?,?,?,?)");
        final String sqlInsert = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Insert Query =");
            loggerValue.append(sqlInsert);
            _log.debug(methodName, loggerValue);
        }

        try {
            pstmtInsert = (PreparedStatement) p_con.prepareStatement(sqlInsert);
            pstmtInsert.setString(1, p_categoryVO.getCategoryCode());
            pstmtInsert.setString(2, p_categoryVO.getCategoryCode());
            pstmtInsert.setString(3, p_categoryVO.getCategoryName());
            pstmtInsert.setString(4, p_categoryVO.getCategoryStatus());
            pstmtInsert.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(p_categoryVO.getCreatedOn()));
            pstmtInsert.setString(6, p_categoryVO.getCreatedBy());
            pstmtInsert.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(p_categoryVO.getModifiedOn()));
            pstmtInsert.setString(8, p_categoryVO.getModifiedBy());
            addCount = pstmtInsert.executeUpdate();
        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[addStkProfile]", "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[addStkProfile]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");

        } finally {
            try {
                if (pstmtInsert != null) {
                    pstmtInsert.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            if (_log.isDebugEnabled()) {
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: addCount : " + addCount);
                _log.debug(methodName, msg.toString());
            }
        }
        return addCount;
    }

    /**
     * Method modifyCategory.
     * This method is used to Modify the Details of Categories in the categories
     * table
     *
     * @param p_con
     *            Connection
     * @param p_categoryVO
     *            CategoryVO
     * @return int
     * @throws BTSLBaseException
     */

    public int modifyCategory(Connection p_con, CategoryVO p_categoryVO) throws BTSLBaseException {

        final String methodName = "modifyCategory";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: VO =");
            loggerValue.append(p_categoryVO);
            _log.debug(methodName, loggerValue);
        }

        int updateCount = -1;
        PreparedStatement pstmtUpdate = null;

        try {
            final StringBuffer updateQueryBuff = new StringBuffer("UPDATE categories SET");
            updateQueryBuff.append(" category_name=?,grph_domain_type=?,multiple_grph_domains=?,");
            updateQueryBuff.append("web_interface_allowed=?,sms_interface_allowed=?,fixed_roles=?,");
            updateQueryBuff.append("multiple_login_allowed=?,view_on_network_block=?,max_login_count=?,");
            updateQueryBuff.append("status=?,modified_on=?,modified_by=?,display_allowed=?,");
            updateQueryBuff.append("modify_allowed=?,product_types_allowed=?,services_allowed=?,max_txn_msisdn=?,uncntrl_transfer_allowed=?,");
            updateQueryBuff.append("scheduled_transfer_allowed=?,restricted_msisdns=?,parent_category_code=?,");
            updateQueryBuff.append("user_id_prefix=?,outlets_allowed=?,agent_allowed=?,hierarchy_allowed=?, transfertolistonly=?, low_bal_alert_allow=?,");
            updateQueryBuff.append(" c2s_payee_status=?,cp2p_payee_status=?,cp2p_payer_status=?,cp2p_within_list=?,cp2p_within_list_level=?,authentication_type=? ");
            updateQueryBuff.append("WHERE category_code=? ");

            final String insertQuery = updateQueryBuff.toString();
            pstmtUpdate = (PreparedStatement) p_con.prepareStatement(insertQuery);
            pstmtUpdate.setString(1, p_categoryVO.getCategoryName());
            pstmtUpdate.setString(2, p_categoryVO.getGrphDomainType());
            pstmtUpdate.setString(3, p_categoryVO.getMultipleGrphDomains());
            pstmtUpdate.setString(4, p_categoryVO.getWebInterfaceAllowed());
            pstmtUpdate.setString(5, p_categoryVO.getSmsInterfaceAllowed());
            pstmtUpdate.setString(6, p_categoryVO.getFixedRoles());
            pstmtUpdate.setString(7, p_categoryVO.getMultipleLoginAllowed());
            pstmtUpdate.setString(8, p_categoryVO.getViewOnNetworkBlock());
            pstmtUpdate.setLong(9, p_categoryVO.getMaxLoginCount());
            pstmtUpdate.setString(10, p_categoryVO.getCategoryStatus());
            pstmtUpdate.setTimestamp(11, BTSLUtil.getTimestampFromUtilDate(p_categoryVO.getModifiedOn()));
            pstmtUpdate.setString(12, p_categoryVO.getModifiedBy());
            pstmtUpdate.setString(13, p_categoryVO.getDisplayAllowed());
            pstmtUpdate.setString(14, p_categoryVO.getModifyAllowed());
            pstmtUpdate.setString(15, p_categoryVO.getProductTypeAssociationAllowed());
            pstmtUpdate.setString(16, p_categoryVO.getServiceAllowed());
            pstmtUpdate.setLong(17, Long.parseLong(p_categoryVO.getMaxTxnMsisdn()));
            pstmtUpdate.setString(18, p_categoryVO.getUnctrlTransferAllowed());
            pstmtUpdate.setString(19, p_categoryVO.getScheduledTransferAllowed());
            pstmtUpdate.setString(20, p_categoryVO.getRestrictedMsisdns());
            String parentCategoryCode =   p_categoryVO.getCategoryCode().substring(0, p_categoryVO.getCategoryCode().length() - 1);
            pstmtUpdate.setString(21,parentCategoryCode.toUpperCase());
            pstmtUpdate.setString(22, p_categoryVO.getUserIdPrefix());
            pstmtUpdate.setString(23, p_categoryVO.getOutletsAllowed());
            pstmtUpdate.setString(24, p_categoryVO.getAgentAllowed());
            pstmtUpdate.setString(25, p_categoryVO.getHierarchyAllowed());
            pstmtUpdate.setString(26, p_categoryVO.getTransferToListOnly());
            // Added for low balance alert
            pstmtUpdate.setString(27, p_categoryVO.getLowBalAlertAllow());
            // added
            pstmtUpdate.setString(28, p_categoryVO.getRechargeByParentOnly());
            pstmtUpdate.setString(29, p_categoryVO.getCp2pPayee());
            pstmtUpdate.setString(30, p_categoryVO.getCp2pPayer());
            pstmtUpdate.setString(31, p_categoryVO.getCp2pWithinList());
            pstmtUpdate.setString(32, p_categoryVO.getParentOrOwnerRadioValue());
            // Added for Authentication Type
            pstmtUpdate.setString(33, p_categoryVO.getAuthenticationType());
            if (BTSLUtil.isNullString(p_categoryVO.getAuthenticationType())) {
                pstmtUpdate.setString(33, "NA");
            } else {
                pstmtUpdate.setString(33, p_categoryVO.getAuthenticationType());
            }
            pstmtUpdate.setString(34, p_categoryVO.getCategoryCode());

            final boolean modified = this.recordModified(p_con, p_categoryVO.getCategoryCode(), p_categoryVO.getLastModifiedTime(), methodName);
            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }
            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount > 0) {
                if (this.isExistStkProfile(p_con, p_categoryVO.getCategoryCode())) {
                    updateCount = this.updateStkProfile(p_con, p_categoryVO);
                } else {
                    ;
                }
            }

        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            throw new BTSLBaseException(this, methodName, "error.modify.true");
        }

        catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[modifyCategory]", "", "", "",
                    "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[modifyCategory]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: updateCount : " + updateCount);
                _log.debug(methodName, msg.toString());
            }
        }

        return updateCount;
    }

    /**
     * Method modifyCategory.
     * This method is used to Modify the Details of Categories in the categories
     * table
     *
     * @param p_con
     *            Connection
     * @param p_categoryVO
     *            CategoryVO
     * @return int
     * @throws BTSLBaseException
     */

    public int modifyCategoryAndAgent(Connection p_con, CategoryVO p_categoryVO) throws BTSLBaseException {

        final String methodName = "modifyCategoryAndAgent";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: VO =");
            loggerValue.append(p_categoryVO);
            _log.debug(methodName, loggerValue);
        }

        int updateCount = -1;
        PreparedStatement pstmtUpdate = null;

        try {
            final StringBuffer updateQueryBuff = new StringBuffer("UPDATE categories SET");
            updateQueryBuff.append(" category_name=?,grph_domain_type=?,multiple_grph_domains=?,");
            updateQueryBuff.append("web_interface_allowed=?,sms_interface_allowed=?,fixed_roles=?,");
            updateQueryBuff.append("multiple_login_allowed=?,view_on_network_block=?,max_login_count=?,");
            updateQueryBuff.append("status=?,modified_on=?,modified_by=?,display_allowed=?,");
            updateQueryBuff.append("modify_allowed=?,product_types_allowed=?,services_allowed=?,max_txn_msisdn=?,uncntrl_transfer_allowed=?,");
            updateQueryBuff.append("scheduled_transfer_allowed=?,restricted_msisdns=?,parent_category_code=?,");
            updateQueryBuff.append("user_id_prefix=?,outlets_allowed=?,agent_allowed=?,hierarchy_allowed=?, transfertolistonly=?, low_bal_alert_allow=?,");
            updateQueryBuff.append(" c2s_payee_status=?,cp2p_payee_status=?,cp2p_payer_status=?,cp2p_within_list=?,cp2p_within_list_level=?,authentication_type=? ");
            updateQueryBuff.append("WHERE category_code=? ");

            final String insertQuery = updateQueryBuff.toString();
            pstmtUpdate = (PreparedStatement) p_con.prepareStatement(insertQuery);
            pstmtUpdate.setString(1, p_categoryVO.getCategoryName());
            pstmtUpdate.setString(2, p_categoryVO.getGrphDomainType());
            pstmtUpdate.setString(3, p_categoryVO.getMultipleGrphDomains());
            pstmtUpdate.setString(4, p_categoryVO.getWebInterfaceAllowed());
            pstmtUpdate.setString(5, p_categoryVO.getSmsInterfaceAllowed());
            pstmtUpdate.setString(6, p_categoryVO.getFixedRoles());
            pstmtUpdate.setString(7, p_categoryVO.getMultipleLoginAllowed());
            pstmtUpdate.setString(8, p_categoryVO.getViewOnNetworkBlock());
            pstmtUpdate.setLong(9, p_categoryVO.getMaxLoginCount());
            pstmtUpdate.setString(10, p_categoryVO.getCategoryStatus());
            pstmtUpdate.setTimestamp(11, BTSLUtil.getTimestampFromUtilDate(p_categoryVO.getModifiedOn()));
            pstmtUpdate.setString(12, p_categoryVO.getModifiedBy());
            pstmtUpdate.setString(13, p_categoryVO.getDisplayAllowed());
            pstmtUpdate.setString(14, p_categoryVO.getModifyAllowed());
            pstmtUpdate.setString(15, p_categoryVO.getProductTypeAssociationAllowed());
            pstmtUpdate.setString(16, p_categoryVO.getServiceAllowed());
            pstmtUpdate.setLong(17, Long.parseLong(p_categoryVO.getMaxTxnMsisdn()));
            pstmtUpdate.setString(18, p_categoryVO.getUnctrlTransferAllowed());
            pstmtUpdate.setString(19, p_categoryVO.getScheduledTransferAllowed());
            pstmtUpdate.setString(20, p_categoryVO.getRestrictedMsisdns());
            String parentCategoryCode =   p_categoryVO.getCategoryCode().substring(0, p_categoryVO.getCategoryCode().length() - 1);
            pstmtUpdate.setString(21,parentCategoryCode.toUpperCase());
            pstmtUpdate.setString(22, p_categoryVO.getUserIdPrefix().toUpperCase());
            pstmtUpdate.setString(23, p_categoryVO.getOutletsAllowed());
            pstmtUpdate.setString(24, p_categoryVO.getAgentAllowed());
            pstmtUpdate.setString(25, p_categoryVO.getHierarchyAllowed());
            pstmtUpdate.setString(26, p_categoryVO.getTransferToListOnly());
            // Added for low balance alert
            pstmtUpdate.setString(27, p_categoryVO.getLowBalAlertAllow());
            // added
            pstmtUpdate.setString(28, p_categoryVO.getRechargeByParentOnly());
            pstmtUpdate.setString(29, p_categoryVO.getCp2pPayee());
            pstmtUpdate.setString(30, p_categoryVO.getCp2pPayer());
            pstmtUpdate.setString(31, p_categoryVO.getCp2pWithinList());
            pstmtUpdate.setString(32, p_categoryVO.getParentOrOwnerRadioValue());
            // Added for Authentication Type
            pstmtUpdate.setString(33, p_categoryVO.getAuthenticationType());
            if (BTSLUtil.isNullString(p_categoryVO.getAuthenticationType())) {
                pstmtUpdate.setString(33, "NA");
            } else {
                pstmtUpdate.setString(33, p_categoryVO.getAuthenticationType());
            }
            pstmtUpdate.setString(34, p_categoryVO.getCategoryCode());

            final boolean modified = this.recordModified(p_con, p_categoryVO.getCategoryCode(), p_categoryVO.getLastModifiedTime(), methodName);
            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }
            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount > 0) {
                if (this.isExistStkProfile(p_con, p_categoryVO.getCategoryCode())) {
                    updateCount = this.updateStkProfile(p_con, p_categoryVO);
                } else {
                    ;
                }
            }

        } catch (BTSLBaseException be) {
            _log.errorTrace(methodName, be);
            throw new BTSLBaseException(this, methodName, "error.modify.true");
        }

        catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[modifyCategoryAndAgent]", "", "", "",
                    "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
        }

        catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[modifyCategoryAndAgent]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
        }

        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: updateCount : " + updateCount);
                _log.debug(methodName, msg.toString());
            }
        }

        return updateCount;
    }

    /**
     * Method recordModified
     * This method is used to check whether the record in the database is
     * modified or not
     *
     * @param con
     *            Connection
     * @param p_Code
     *            String
     * @param oldLastModified
     *            Long
     * @param p_fromFunction
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */

    public boolean recordModified(Connection con, String p_Code, long oldLastModified, String p_fromFunction) throws BTSLBaseException {

        final String methodName = "recordModified";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_Code =");
            loggerValue.append(p_Code);
            loggerValue.append("oldLastModified=");
            loggerValue.append(oldLastModified);
            _log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean modified = false;
        String sqlRecordModified = null;
        if ("deleteCategory".equalsIgnoreCase(p_fromFunction) || "modifyCategory".equalsIgnoreCase(p_fromFunction)) {
            sqlRecordModified = "SELECT modified_on FROM categories WHERE category_code=?";
        }

        Timestamp newLastModified = null;
        if ((oldLastModified) == 0) {
            return false;
        }

        try {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlRecordModified);
            _log.debug(methodName, loggerValue);
            pstmt = con.prepareStatement(sqlRecordModified);
            pstmt.setString(1, p_Code.toUpperCase());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                newLastModified = rs.getTimestamp("modified_on");
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " old=" + oldLastModified);
                if (newLastModified != null) {
                    _log.debug(methodName, " new=" + newLastModified.getTime());
                } else {
                    _log.debug(methodName, " new=null");
                }
            }
            if (newLastModified != null && newLastModified.getTime() != oldLastModified) {
                modified = true;
            }
        }

        catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[recordModified]", "", "", "",
                    "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[recordModified]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception ex) {
                _log.errorTrace(methodName, ex);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: modified : " + modified);
                _log.debug(methodName, msg.toString());
            }
        }
        return modified;
    }

    /**
     * Method deleteCategory.
     * This method is used to delete(set the status to 'N') the category
     *
     * @param p_con
     *            Connection
     * @param p_categoryVO
     *            CategoryVO
     * @return int
     * @throws BTSLBaseException
     */

    public int deleteCategory(Connection p_con, CategoryVO p_categoryVO) throws BTSLBaseException {
        final String methodName = "deleteCategory";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: VO =");
            loggerValue.append(p_categoryVO);
            _log.debug(methodName, loggerValue);
        }
        int updateCount = -1;
        PreparedStatement pstmtUpdate = null;
        try {
            final StringBuffer updateQueryBuff = new StringBuffer("UPDATE categories SET status=?,");
            updateQueryBuff.append("modified_on=?,modified_by=? WHERE category_code=?");
            final String updateQuery = updateQueryBuff.toString();
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setString(1, PretupsI.CATEGORY_STATUS_DELETE);
            pstmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_categoryVO.getModifiedOn()));
            pstmtUpdate.setString(3, p_categoryVO.getModifiedBy());
            pstmtUpdate.setString(4, p_categoryVO.getCategoryCode());
            final boolean modified = this.recordModified(p_con, p_categoryVO.getCategoryCode(), p_categoryVO.getLastModifiedTime(), methodName);
            if (modified) {
                throw new BTSLBaseException(this, methodName, "error.modify.true");
            }
            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount > 0) {
                updateCount = this.deleteStkProfile(p_con, p_categoryVO);
                if (PretupsI.NO.equals(p_categoryVO.getFixedRoles())) {
                    if (!(this.deleteGroupRole(p_con, p_categoryVO))) {
                        updateCount = -1;
                    }
                }
            }
        } catch (BTSLBaseException be) {
            throw be;
        } catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[deleteCategory]", "", "", "",
                    "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[deleteCategory]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: updateCount : " + updateCount);
                _log.debug(methodName, msg.toString());
            }
        }

        return updateCount;
    }

    /**
     * Method :isExistStkProfile this method check the StkProfile axists or not
     * for category
     * param p_con Connection
     *
     * @param p_categoryCode
     * @return flag boolean
     * @throws BTSLBaseException
     */

    public boolean isExistStkProfile(Connection p_con, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "isExistStkProfile";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_categoryCode =");
            loggerValue.append(p_categoryCode);
            _log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean isExist = false;
        final StringBuffer strBuff = new StringBuffer(" SELECT 1 FROM stk_profiles");
        strBuff.append(" WHERE profile_code=?");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlSelect);
            _log.debug(methodName, loggerValue);
        }
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_categoryCode.toUpperCase());
            rs = pstmtSelect.executeQuery();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlSelect= " + sqlSelect);
            }
            if (rs.next()) {
                isExist = true;
            }
        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isExistStkProfile]", "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isExistStkProfile]", "", "", "",
                    "Exception:" + e.getMessage());
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: isExist : " + isExist);
                _log.debug(methodName, msg.toString());
            }
        }
        return isExist;
    }

    /**
     * Method updateStkProfile
     * This method is used to Modify the Details of Categories in the categories
     * table
     *
     * @param p_con
     *            Connection
     * @param p_categoryVO
     *            CategoryVO
     * @return updateCount int
     * @throws BTSLBaseException
     */

    private int updateStkProfile(Connection p_con, CategoryVO p_categoryVO) throws BTSLBaseException {

        final String methodName = "updateStkProfile";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_categoryVO =");
            loggerValue.append(p_categoryVO.toString());
            _log.debug(methodName, loggerValue);
        }

        int updateCount = -1;
        PreparedStatement pstmtUpdate = null;
        try {
            final StringBuffer updateQueryBuff = new StringBuffer("UPDATE stk_profiles SET");
            updateQueryBuff.append(" profile_name=?,status=?,");
            updateQueryBuff.append(" modified_on=?,modified_by=?  WHERE profile_code=?");
            final String updateQuery = updateQueryBuff.toString();
            pstmtUpdate = (PreparedStatement) p_con.prepareStatement(updateQuery);
            pstmtUpdate.setString(1, p_categoryVO.getCategoryName());
            pstmtUpdate.setString(2, p_categoryVO.getCategoryStatus());
            pstmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(p_categoryVO.getModifiedOn()));
            pstmtUpdate.setString(4, p_categoryVO.getModifiedBy());
            pstmtUpdate.setString(5, p_categoryVO.getCategoryCode().toUpperCase());

            updateCount = pstmtUpdate.executeUpdate();
        } catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[updateStkProfile]", "", "", "",
                    "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[updateStkProfile]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: updateCount : " + updateCount);
                _log.debug(methodName, msg.toString());
            }
        }
        return updateCount;
    }

    /**
     * Method updateSequenceNumber.
     * This method is used to update the sequence number of the category
     *
     * @param p_con
     *            Connection
     * @param p_categoryVO
     *            CategoryVO
     *
     * @return int
     * @throws BTSLBaseException
     */

    public int updateSequenceNumber(Connection p_con, CategoryVO p_categoryVO) throws BTSLBaseException {

        final String methodName = "updateSequenceNumber";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_categoryVO =");
            loggerValue.append(p_categoryVO);
            _log.debug(methodName, loggerValue);
        }
        int updateCount = -1;
        PreparedStatement pstmtUpdate = null;
        try {
            final StringBuffer updateQueryBuff = new StringBuffer("UPDATE categories SET sequence_no=sequence_no+1,");
            updateQueryBuff.append("modified_on=?,modified_by=? WHERE sequence_no>=? AND category_code<>? AND domain_code=?");
            final String updateQuery = updateQueryBuff.toString();
            pstmtUpdate = p_con.prepareStatement(updateQuery);

            pstmtUpdate.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(p_categoryVO.getModifiedOn()));
            pstmtUpdate.setString(2, p_categoryVO.getModifiedBy());
            pstmtUpdate.setInt(3, p_categoryVO.getSequenceNumber());
            pstmtUpdate.setString(4, p_categoryVO.getCategoryCode());
            pstmtUpdate.setString(5, p_categoryVO.getDomainCodeforCategory());
            updateCount = pstmtUpdate.executeUpdate();
            if (updateCount > 0) {
                updateCount = this.deleteStkProfile(p_con, p_categoryVO);
            }
        } catch (BTSLBaseException be) {
            throw be;
        }

        catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[deleteCategory]", "", "", "",
                    "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteCategory", "error.general.sql.processing");
        }

        catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[deleteCategory]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: updateCount : " + updateCount);
                _log.debug(methodName, msg.toString());
            }
        }

        return updateCount;
    }

    /**
     * Method deleteStkProfile
     * This method is used to delete(set the status to 'N') the category
     *
     * @param p_con
     *            Connection
     * @param p_categoryVO
     *            CategoryVO
     * @return updateCount int
     * @throws BTSLBaseException
     */

    public int deleteStkProfile(Connection p_con, CategoryVO p_categoryVO) throws BTSLBaseException {

        final String METHOD_NAME = "deleteStkProfile";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_categoryVO =");
            loggerValue.append(p_categoryVO.toString());
            _log.debug(METHOD_NAME, loggerValue);
        }

        int updateCount = -1;
        PreparedStatement pstmtUpdate = null;

        try {
            final StringBuffer updateQueryBuff = new StringBuffer("UPDATE stk_profiles SET status=?,");
            updateQueryBuff.append(" modified_on=?,modified_by=? WHERE profile_code=?");
            final String updateQuery = updateQueryBuff.toString();
            pstmtUpdate = p_con.prepareStatement(updateQuery);
            pstmtUpdate.setString(1, PretupsI.CATEGORY_STATUS_DELETE);
            pstmtUpdate.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(p_categoryVO.getModifiedOn()));
            pstmtUpdate.setString(3, p_categoryVO.getModifiedBy());
            pstmtUpdate.setString(4, p_categoryVO.getCategoryCode().toUpperCase());

            updateCount = pstmtUpdate.executeUpdate();
        } catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error("deleteStkProfile", loggerValue);
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[deleteStkProfile]", "", "", "",
                    "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteStkProfile", "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error("deleteStkProfile", loggerValue);
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[deleteStkProfile]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "deleteStkProfile", "error.general.processing");
        } finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(METHOD_NAME, e);
            }
            if (_log.isDebugEnabled()) {
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: updateCount : " + updateCount);
                _log.debug("deleteStkProfile", msg.toString());
            }
        }
        return updateCount;
    }

    /**
     * Method deleteGroupRole
     * This method is used to delete the group roles info [ group_roles,roles &
     * category_roles table ]
     *
     * @param p_con
     *            Connection
     * @param p_categoryVO
     *            CategoryVO
     * @return deleteFlag boolean
     * @throws BTSLBaseException
     */

    public boolean deleteGroupRole(Connection p_con, CategoryVO p_categoryVO) throws BTSLBaseException {
        final String methodName = "deleteGroupRole";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: CategoryCode =");
            loggerValue.append(p_categoryVO.getCategoryCode());
            _log.debug(methodName, loggerValue);
        }
        boolean deleteFlag = false;
        PreparedStatement pstmtDeleteGrpRoles = null;
        PreparedStatement pstmtDeleteRoles = null;
        PreparedStatement pstmtDeleteCatRoles = null;
        try {
            final String deleteGrpRoles = "delete FROM group_roles WHERE group_role_code=?";
            final String deleteRoles = "delete FROM roles WHERE role_code=?";
            final String deleteCatRoles = "delete FROM category_roles WHERE category_code=?";
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "deleteGrpRoles::" + deleteGrpRoles);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "deleteRoles::" + deleteRoles);
            }
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "deleteCatRoles::" + deleteCatRoles);
            }

            final String categoryCode = p_categoryVO.getCategoryCode();
            pstmtDeleteGrpRoles = p_con.prepareStatement(deleteGrpRoles);
            pstmtDeleteGrpRoles.setString(1, categoryCode);
            if (pstmtDeleteGrpRoles.executeUpdate() >= 0) {
                pstmtDeleteRoles = p_con.prepareStatement(deleteRoles);
                pstmtDeleteRoles.setString(1, categoryCode);
                if (pstmtDeleteRoles.executeUpdate() >= 0) {
                    pstmtDeleteCatRoles = p_con.prepareStatement(deleteCatRoles);
                    pstmtDeleteCatRoles.setString(1, categoryCode);
                    if (pstmtDeleteCatRoles.executeUpdate() >= 0) {
                        deleteFlag = true;
                    }
                }
            }
        } catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[deleteGroupRole]", "", "", "",
                    "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[deleteGroupRole]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtDeleteGrpRoles != null) {
                    pstmtDeleteGrpRoles.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtDeleteRoles != null) {
                    pstmtDeleteRoles.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtDeleteCatRoles != null) {
                    pstmtDeleteCatRoles.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: updateCount : " + deleteFlag);
                _log.debug(methodName, msg.toString());
            }
        }
        return deleteFlag;
    }

    /**
     * This method is used before deleting the category it will search for the
     * user
     * associated with the category if any associated user is found then this
     * method
     * return true else return false.
     *
     * @param p_con
     *            Connection
     * @param p_categoryVO
     *            CategoryVO
     * @return boolean
     * @throws BTSLBaseException
     */

    public boolean isUserExists(Connection p_con, CategoryVO p_categoryVO) throws BTSLBaseException {

        final String methodName = "isUserExists";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: CategoryVO =");
            loggerValue.append(p_categoryVO);
            _log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM users WHERE category_code=? AND status NOT IN('C','N') ");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(selectQuery);
            _log.debug(methodName, loggerValue);
        }

        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_categoryVO.getCategoryCode().toUpperCase());
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isUserExists]", "", "", "",
                    "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isUserExists]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: isExists found : " + found);
                _log.debug(methodName, msg.toString());
            }
        }
        return found;
    }

    /**
     * This method is used before adding the record in the categories table
     * it will check for the uniqueness of the category_code column
     * if the user enterd the category code that exists in the database
     * the method will return true and record will not inserted in the
     * categories table.
     *
     * @param p_con
     *            Connection
     * @param p_categoryCode
     *            java.lang.String
     * @return boolean
     * @throws BTSLBaseException
     */

    public boolean isExistsCategoryCodeForAdd(Connection p_con, String p_categoryCode) throws BTSLBaseException {

        final String methodName = "isExistsCategoryCodeForAdd";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: Category Code =");
            loggerValue.append(p_categoryCode);
            _log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM categories WHERE category_code=?");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(selectQuery);
            _log.debug(methodName, loggerValue);
        }

        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_categoryCode.toUpperCase());
            rs = pstmtSelect.executeQuery();

            if (_log.isDebugEnabled()) {
                loggerValue.setLength(0);
                loggerValue.append("SQL select : ");
                loggerValue.append(selectQuery);
                _log.error(methodName, loggerValue);
            }

            if (rs.next()) {
                found = true;
            }
        }

        catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isExistsCategoryCodeForAdd]", "", "",
                    "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isExistsCategoryCodeForAdd]", "", "",
                    "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: isExists found : " + found);
                _log.debug(methodName, msg.toString());
            }
        }

        return found;
    }

    /**
     * This method is used before adding the record in the categories table
     * it will check for the uniqueness of the category_name column
     * if the user enterd the category code that exists in the database
     * the method will return true and record will not inserted in the
     * categories table.
     *
     * @param p_con
     *            Connection
     * @param p_categoryName
     *            java.lang.String
     * @return boolean
     * @throws BTSLBaseException
     */

    public boolean isExistsCategoryNameForAdd(Connection p_con, String p_categoryName) throws BTSLBaseException {

        final String methodName = "isExistsCategoryNameForAdd";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: Category Name =");
            loggerValue.append(p_categoryName);
            _log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM categories WHERE UPPER(category_name)=? AND status<>'N'");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(selectQuery);
            _log.debug(methodName, loggerValue);
        }

        try {
            pstmtSelect = (PreparedStatement) p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_categoryName.toUpperCase());
            rs = pstmtSelect.executeQuery();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed::" + selectQuery);
            }

            if (rs.next()) {
                found = true;
            }
        }

        catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isExistsCategoryNameForAdd]", "", "",
                    "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isExistsCategoryCodeForAdd", "error.general.sql.processing");
        }

        catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isExistsCategoryNameForAdd]", "", "",
                    "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: isExists found : " + found);
                _log.debug(methodName, msg.toString());
            }
        }

        return found;
    }

    /**
     * This method is used before modifying the record in the categories table
     * it will check for the uniqueness of the category_name column
     * if the user enterd the category code that exists in the database
     * the method will return true and record will not inserted in the
     * categories table.
     *
     * @param p_con
     *            Connection
     * @param p_categoryVO
     *            CategoryVO
     * @return boolean
     * @throws BTSLBaseException
     */

    public boolean isExistsCategoryNameForModify(Connection p_con, CategoryVO p_categoryVO) throws BTSLBaseException {

        final String methodName = "isExistsCategoryNameForModify";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: Category VO =");
            loggerValue.append(p_categoryVO);
            _log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM categories WHERE UPPER(category_name)=? AND category_code!=? AND status<>'N'");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(selectQuery);
            _log.debug(methodName, loggerValue);
        }
        try {
            pstmtSelect = (PreparedStatement) p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_categoryVO.getCategoryName().toUpperCase());
            pstmtSelect.setString(2, p_categoryVO.getCategoryCode().toUpperCase());
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isExistsCategoryNameForModify]", "",
                    "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isExistsCategoryNameForModify]", "",
                    "", "", "Exception:" + e.getMessage());
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: isExists found : " + found);
                _log.debug(methodName, msg.toString());
            }
        }

        return found;
    }

    /**
     * This method is used before adding the record in the categories table
     * it will check for the uniqueness of the user_id_prefix column
     * if the user enterd the useridprefix that exists in the database
     * the method will return true and record will not inserted in the
     * categories table.
     *
     * @param p_con
     *            Connection
     * @param p_userIdPrefix
     *            java.lang.String
     * @return boolean
     * @throws BTSLBaseException
     */

    public boolean isExistsUserIdPrefixForAdd(Connection p_con, String p_userIdPrefix) throws BTSLBaseException {

        final String methodName = "isExistsUserIdPrefixForAdd";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_userIdPrefix=");
            loggerValue.append(p_userIdPrefix);
            _log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM categories WHERE user_id_prefix=? AND status<>'N'");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(selectQuery);
            _log.debug(methodName, loggerValue);
        }

        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_userIdPrefix.toUpperCase());
            rs = pstmtSelect.executeQuery();

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed::" + selectQuery);
            }

            if (rs.next()) {
                found = true;
            }
        }

        catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isExistsUserIdPrefixForAdd]", "", "",
                    "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isExistsUserIdPrefixForAdd]", "", "",
                    "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isExistsCategoryNameForAdd", "error.general.processing");
        }

        finally {
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: isExists found : " + found);
                _log.debug(methodName, msg.toString());
            }
        }

        return found;
    }

    /**
     * This method is used before modifying the record in the categories table
     * it will check for the uniqueness of the user_id_prefix column
     * if the user enterd the useridprefix that exists in the database
     * the method will return true and record will not inserted in the
     * categories table.
     *
     * @param p_con
     *            Connection
     * @param p_categoryVO
     *            CategoryVO
     * @return boolean
     * @throws BTSLBaseException
     */

    public boolean isExistsUserIdPrefixForModify(Connection p_con, CategoryVO p_categoryVO) throws BTSLBaseException {

        final String methodName = "isExistsUserIdPrefixForModify";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_categoryVO=");
            loggerValue.append(p_categoryVO);
            _log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM categories WHERE user_id_prefix=? AND category_code!=? AND status<>'N'");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(selectQuery);
            _log.debug(methodName, loggerValue);
        }
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_categoryVO.getUserIdPrefix().toUpperCase());
            pstmtSelect.setString(2, p_categoryVO.getCategoryCode().toUpperCase());
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isExistsUserIdPrefixForModify]", "",
                    "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isExistsUserIdPrefixForModify]", "",
                    "", "", "Exception:" + e.getMessage());
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: isExists found : " + found);
                _log.debug(methodName, msg.toString());
            }
        }
        return found;
    }

    /**
     * Method isHigherSequenceCategoriesExists
     * This method is used before deleting the category.It will check if any
     * higher
     * sequence non deleted categories are present under that category if yes it
     * will return true else return false
     *
     * @param p_con
     *            Connection
     * @param p_categoryVO
     *            CategoryVO
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isHigherSequenceCategoriesExists(Connection p_con, CategoryVO p_categoryVO) throws BTSLBaseException {

        final String methodName = "isHigherSequenceCategoriesExists";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: domainCode=");
            loggerValue.append(p_categoryVO.getDomainCodeforCategory());
            loggerValue.append(" seqNo=");
            loggerValue.append(p_categoryVO.getSequenceNumber());
            _log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM categories WHERE sequence_no>? AND domain_code=? AND status<>'N'AND category_type<>?");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(selectQuery);
            _log.debug(methodName, loggerValue);
        }
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setInt(1, p_categoryVO.getSequenceNumber());
            pstmtSelect.setString(2, p_categoryVO.getDomainCodeforCategory());
            pstmtSelect.setString(3, PretupsI.AGENTCATEGORY);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isHigherSequenceCategoriesExists]", "",
                    "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isHigherSequenceCategoriesExists]", "",
                    "", "", "Exception:" + e.getMessage());
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: isExists found : " + found);
                _log.debug(methodName, msg.toString());
            }
        }
        return found;
    }

    /**
     * Method isCategoriesExistsInTransferProfile
     * This method is used before deleting the category.It will check if any
     * Transfer
     * profile is associated with the category if yes it will return true
     *
     * @param p_con
     *            Connection
     * @param p_categoryVO
     *            CategoryVO
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isCategoriesExistsInTransferProfile(Connection p_con, CategoryVO p_categoryVO) throws BTSLBaseException {

        final String methodName = "isCategoriesExistsInTransferProfile";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: categoryCode=");
            loggerValue.append(p_categoryVO.getCategoryCode());
            _log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM transfer_profile where category_code=? and status<>'N'");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(selectQuery);
            _log.debug(methodName, loggerValue);
        }
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_categoryVO.getCategoryCode());
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isCategoriesExistsInTransferProfile]",
                    "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isCategoriesExistsInTransferProfile]",
                    "", "", "", "Exception:" + e.getMessage());
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: isExists found : " + found);
                _log.debug(methodName, msg.toString());
            }
        }
        return found;
    }

    /**
     * Method isCategoriesExistsInCommisionProfile
     * This method is used before deleting the category.It will check if any
     * Commission
     * profile is associated with the category if yes it will return true
     *
     * @param p_con
     *            Connection
     * @param p_categoryVO
     *            CategoryVO
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isCategoriesExistsInCommisionProfile(Connection p_con, CategoryVO p_categoryVO) throws BTSLBaseException {

        final String methodName = "isCategoriesExistsInCommisionProfile";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: categoryCode=");
            loggerValue.append(p_categoryVO.getCategoryCode());
            _log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM commission_profile_set where category_code=? and status<>'N'");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(selectQuery);
            _log.debug(methodName, loggerValue);
        }
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_categoryVO.getCategoryCode());
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isCategoriesExistsInCommisionProfile]",
                    "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isCategoriesExistsInCommisionProfile]",
                    "", "", "", "Exception:" + e.getMessage());
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: isExists found : " + found);
                _log.debug(methodName, msg.toString());
            }
        }
        return found;
    }

    /**
     * Method isCategoriesExistsInTransferRule
     * This method is used before deleting the category.It will check if any
     * Transfer
     * rule is associated with the category if yes it will return true
     *
     * @param p_con
     *            Connection
     * @param p_categoryVO
     *            CategoryVO
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isCategoriesExistsInTransferRule(Connection p_con, CategoryVO p_categoryVO) throws BTSLBaseException {

        final String methodName = "isCategoriesExistsInTransferRule";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: categoryCode=");
            loggerValue.append(p_categoryVO.getCategoryCode());
            _log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM chnl_transfer_rules where from_category=? OR to_category=? and status<>'N'");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(selectQuery);
            _log.debug(methodName, loggerValue);
        }
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_categoryVO.getCategoryCode());
            pstmtSelect.setString(2, p_categoryVO.getCategoryCode());
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isCategoriesExistsInTransferRule]", "",
                    "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isCategoriesExistsInTransferRule]", "",
                    "", "", "Exception:" + e.getMessage());
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: isExists found : " + found);
                _log.debug(methodName, msg.toString());
            }
        }
        return found;
    }

    /**
     * Method loadCategoryList.
     * This method loads all the categories of the selected domain type
     *
     * @param p_con
     *            Connection
     * @param p_domainCode
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadCategoryList(Connection p_con, String p_domainCode) throws BTSLBaseException {
        final String methodName = "loadCategoryList";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered:p_domainCode=");
            loggerValue.append(p_domainCode);
            _log.debug(methodName, loggerValue);
        }
        final ArrayList categoryList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
            final StringBuffer selectQuery = new StringBuffer();

            selectQuery.append("SELECT category_code, category_name, sequence_no,uncntrl_transfer_allowed, ");
            selectQuery.append("hierarchy_allowed,restricted_msisdns ");
            selectQuery.append("FROM categories ");
            selectQuery.append("WHERE status=? AND domain_code=? ");
            selectQuery.append("ORDER BY sequence_no ");
            if (_log.isDebugEnabled()) {
                loggerValue.setLength(0);
                loggerValue.append("Select Query =");
                loggerValue.append(selectQuery);
                _log.debug(methodName, loggerValue);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, PretupsI.DOMAIN_STATUS_ACTIVE);
            pstmtSelect.setString(2, p_domainCode);
            rs = pstmtSelect.executeQuery();
            ListValueVO listValueVO = null;
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("category_name"), rs.getString("sequence_no") + ":" + rs.getString("category_code") + ":" + rs
                        .getString("hierarchy_allowed") + ":" + rs.getString("uncntrl_transfer_allowed") + ":" + rs.getString("restricted_msisdns"));
                categoryList.add(listValueVO);
            }
        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryList]", "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryList]", "", "", "",
                    "Exception:" + e.getMessage());
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: list size : " + categoryList.size());
                _log.debug(methodName, msg.toString());
            }
        }
        return categoryList;
    }

    /**
     * Method for loading category List By domain Code.
     *
     * Used in(ChannelUserAction)
     *
     * @author mohit.goel
     *
     * @param p_con
     *            java.sql.Connection
     * @param p_domainCode
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadCategorListByDomainCode(Connection p_con, String p_domainCode) throws BTSLBaseException {
        final String methodName = "loadCategorListByDomainCode";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered:p_domainCode=");
            loggerValue.append(p_domainCode);
            _log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT c.category_code,c.category_name,C.category_type,");
        strBuff.append("c.domain_code,c.sequence_no,c.grph_domain_type, ");
        strBuff.append("c.multiple_grph_domains,c.web_interface_allowed, c.sms_interface_allowed, ");
        strBuff.append("c.fixed_roles,c.status,c.multiple_login_allowed, c.transfertolistonly, ");
        strBuff.append("c.view_on_network_block,c.max_login_count,c.user_id_prefix,c.product_types_allowed,");
        strBuff.append("c.services_allowed,c.domain_allowed,c.fixed_domains,c.max_txn_msisdn,c.outlets_allowed,c.restricted_msisdns, ");
        strBuff.append(" gdt.grph_domain_type_name,gdt.sequence_no grph_sequence_no ");
        // Added for low balance alert
        strBuff.append(",c.low_bal_alert_allow ");
        strBuff.append(",c.cp2p_payee_status,c.cp2p_payer_status,c.c2s_payee_status ,c.cp2p_within_list,c.cp2p_within_list_level,c.authentication_type ");
        strBuff.append("FROM categories c,geographical_domain_types gdt WHERE c.status <> 'N' AND c.domain_code = ? ");
        strBuff.append("AND gdt.grph_domain_type = c.grph_domain_type ORDER BY c.sequence_no");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlSelect);
            _log.debug(methodName, loggerValue);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_domainCode);

            rs = pstmt.executeQuery();
            CategoryVO categoryVO = null;
            while (rs.next()) {
                categoryVO = new CategoryVO();
                categoryVO.setCategoryCode(rs.getString("category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
                categoryVO.setMultipleGrphDomains(rs.getString("multiple_grph_domains"));
                categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
                categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
                categoryVO.setFixedRoles(rs.getString("fixed_roles"));
                categoryVO.setCategoryStatus(rs.getString("status"));
                categoryVO.setMultipleLoginAllowed(rs.getString("multiple_login_allowed"));
                categoryVO.setViewOnNetworkBlock(rs.getString("view_on_network_block"));
                categoryVO.setMaxLoginCount(rs.getInt("max_login_count"));
                categoryVO.setUserIdPrefix(rs.getString("user_id_prefix"));
                categoryVO.setServiceAllowed(rs.getString("services_allowed"));
                categoryVO.setDomainAllowed(rs.getString("domain_allowed"));
                categoryVO.setFixedDomains(rs.getString("fixed_domains"));
                categoryVO.setGrphDomainSequenceNo(rs.getInt("grph_sequence_no"));
                categoryVO.setProductTypeAllowed(rs.getString("product_types_allowed"));
                categoryVO.setMaxTxnMsisdnInt(rs.getInt("max_txn_msisdn"));
                categoryVO.setOutletsAllowed(rs.getString("outlets_allowed"));
                categoryVO.setGrphDomainTypeName(rs.getString("grph_domain_type_name"));
                categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
                categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
                categoryVO.setCategoryType(rs.getString("category_type"));
                // Added for low balance alert
                categoryVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));

                categoryVO.setRechargeByParentOnly(rs.getString("c2s_payee_status"));
                categoryVO.setCp2pPayee(rs.getString("cp2p_payee_status"));
                categoryVO.setCp2pPayer(rs.getString("cp2p_payer_status"));
                categoryVO.setCp2pWithinList(rs.getString("cp2p_within_list"));
                categoryVO.setParentOrOwnerRadioValue(rs.getString("cp2p_within_list_level"));
                categoryVO.setAuthenticationType(rs.getString("authentication_type"));
                list.add(categoryVO);
            }

        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategorListByDomainCode]", "", "",
                    "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(ex.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategorListByDomainCode]", "", "",
                    "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: categoryList size : " + list.size());
                _log.debug(methodName, msg.toString());
            }
        }
        return list;
    }

    /**
     * Method for loading category List other than operators.
     *
     * Used in(ChannelUserAction)
     *
     * @author mohit.goel
     *
     * @param p_con
     *            java.sql.Connection
     * @param p_fixedRoles
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadCategoryListForGroupRole(Connection p_con, String p_fixedRoles) throws BTSLBaseException {
        final String methodName = "loadCategoryListForGroupRole";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_fixedRoles=");
            loggerValue.append(p_fixedRoles);
            _log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT c.category_code,c.category_name,");
        strBuff.append("c.domain_code,c.sequence_no,c.grph_domain_type, ");
        strBuff.append("c.multiple_grph_domains,c.web_interface_allowed,c.sms_interface_allowed, ");
        strBuff.append("c.fixed_roles,c.status,c.multiple_login_allowed, ");
        strBuff.append("c.view_on_network_block,c.max_login_count,c.user_id_prefix,c.product_types_allowed,c.services_allowed,");
        strBuff.append("c.domain_allowed,c.fixed_domains,c.max_txn_msisdn,c.outlets_allowed,gdt.sequence_no grph_sequence_no ");
        // added by sandeep goel ID CAT001
        // web_interface_allowed='Y' is added as only those category should be
        // available for the group role management
        // which have web access allowed.

        strBuff.append("FROM categories c,geographical_domain_types gdt WHERE c.status <> 'N' AND c.fixed_roles = ? AND web_interface_allowed='Y' ");
        strBuff.append("AND gdt.grph_domain_type = c.grph_domain_type ORDER BY c.sequence_no");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlSelect);
            _log.debug(methodName, loggerValue);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_fixedRoles);

            rs = pstmt.executeQuery();
            CategoryVO categoryVO = null;
            while (rs.next()) {
                categoryVO = new CategoryVO();
                categoryVO.setCategoryCode(rs.getString("category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
                categoryVO.setMultipleGrphDomains(rs.getString("multiple_grph_domains"));
                categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
                categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
                categoryVO.setFixedRoles(rs.getString("fixed_roles"));
                categoryVO.setCategoryStatus(rs.getString("status"));
                categoryVO.setMultipleLoginAllowed(rs.getString("multiple_login_allowed"));
                categoryVO.setViewOnNetworkBlock(rs.getString("view_on_network_block"));
                categoryVO.setMaxLoginCount(rs.getInt("max_login_count"));
                categoryVO.setUserIdPrefix(rs.getString("user_id_prefix"));
                categoryVO.setGrphDomainSequenceNo(rs.getInt("grph_sequence_no"));
                categoryVO.setProductTypeAllowed(rs.getString("product_types_allowed"));
                categoryVO.setServiceAllowed(rs.getString("services_allowed"));
                categoryVO.setDomainAllowed(rs.getString("domain_allowed"));
                categoryVO.setFixedDomains(rs.getString("fixed_domains"));
                categoryVO.setOutletsAllowed(rs.getString("outlets_allowed"));
                categoryVO.setMaxTxnMsisdnInt(rs.getInt("max_txn_msisdn"));

                list.add(categoryVO);
            }

        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryListForGroupRole]", "", "",
                    "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(ex.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryListForGroupRole]", "", "",
                    "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: categoryList size : " + list.size());
                _log.debug(methodName, msg.toString());
            }
        }
        return list;
    }

    /**
     * Method addGroupRole
     * This method is used to add the Group Role
     *
     * @param p_con
     *            Connection
     * @param p_categoryVO
     *            CategoryVO
     * @return int
     * @throws BTSLBaseException
     */

    public int addGroupRole(Connection p_con, CategoryVO p_categoryVO, String[] p_roles) throws BTSLBaseException {
        final String methodName = "addGroupRole";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: VO=");
            loggerValue.append(p_categoryVO.toString());
            loggerValue.append("p_roles=");
            loggerValue.append(p_roles);
            _log.debug(methodName, loggerValue);
        }
        int addCount = 0;
        try {
            // If the type of the role is dynamic then the entry updated in
            // roles,group_roles and category_roles
            final UserRolesWebDAO rolesWebDAO = new UserRolesWebDAO();
            final UserRolesVO userRoleVO = new UserRolesVO();
            userRoleVO.setDomainType(p_categoryVO.getDomainTypeCode());
            userRoleVO.setRoleCode(p_categoryVO.getCategoryCode());
            userRoleVO.setRoleName(p_categoryVO.getCategoryName());
            userRoleVO.setGroupName(p_categoryVO.getCategoryName());
            userRoleVO.setStatus(PretupsI.YES);
            userRoleVO.setRoleType(PretupsI.ROLE_TYPE_FOR_GROUP_ROLE);
            userRoleVO.setGroupRole(PretupsI.YES);

            // Added by Anjali
            userRoleVO.setDefaultType(PretupsI.YES);
            // End
            if (rolesWebDAO.addRole(p_con, userRoleVO) > 0) {
                // Group role code will be same as category co
                if (rolesWebDAO.addGroupRoles(p_con, p_categoryVO.getCategoryCode(), p_roles) > 0) {
                    final DomainDAO domainDAO = new DomainDAO();
                    // Insert in category_roles:: Category code & group role
                    // code
                    final String arr[] = { p_categoryVO.getCategoryCode() };
                    addCount = new CategoryRoleDAO().addCategoryRoles(p_con, p_categoryVO.getCategoryCode(), arr);
                }
            }
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[addGroupRole]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");

        } finally {
            if (_log.isDebugEnabled()) {
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: addCount : " + addCount);
                _log.debug(methodName, msg.toString());
            }
        }
        return addCount;
    }

    /**
     * Method deleteGroupRoleForModify
     * This method is used to delete the group roles info [ group_roles,roles
     * table ]
     *
     * @param p_con
     *            Connection
     * @param p_categoryVO
     *            CategoryVO
     * @return deleteFlag boolean
     * @throws BTSLBaseException
     */

    public boolean deleteGroupRoleForModify(Connection p_con, CategoryVO p_categoryVO) throws BTSLBaseException {
        final String methodName = "deleteGroupRoleForModify";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: CategoryCode=");
            loggerValue.append(p_categoryVO.getCategoryCode());
            _log.debug(methodName, loggerValue);
        }
        boolean deleteFlag = false;
        PreparedStatement pstmtDeleteGrpRoles = null;
        PreparedStatement pstmtDeleteRoles = null;

        try {
            final String deleteGrpRoles = "delete FROM group_roles WHERE group_role_code=?";
            final String deleteRoles = "delete FROM roles WHERE role_code=?";

            if (_log.isDebugEnabled()) {
                loggerValue.setLength(0);
                loggerValue.append("Delete GrpRoles Query =");
                loggerValue.append(deleteGrpRoles);
                _log.debug(methodName, loggerValue);
            }
            if (_log.isDebugEnabled()) {
                loggerValue.setLength(0);
                loggerValue.append("Delete Roles Query =");
                loggerValue.append(deleteRoles);
                _log.debug(methodName, loggerValue);
            }

            final String categoryCode = p_categoryVO.getCategoryCode();
            pstmtDeleteGrpRoles = p_con.prepareStatement(deleteGrpRoles);
            pstmtDeleteGrpRoles.setString(1, categoryCode);
            if (pstmtDeleteGrpRoles.executeUpdate() >= 0) {
                pstmtDeleteRoles = p_con.prepareStatement(deleteRoles);
                pstmtDeleteRoles.setString(1, categoryCode);
                if (pstmtDeleteRoles.executeUpdate() >= 0) {
                    deleteFlag = true;
                }
            }
        } catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[deleteGroupRoleForModify]", "", "", "",
                    "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteGroupRole", "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[deleteGroupRoleForModify]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtDeleteGrpRoles != null) {
                    pstmtDeleteGrpRoles.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtDeleteRoles != null) {
                    pstmtDeleteRoles.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: updateCount : " + deleteFlag);
                _log.debug(methodName, msg.toString());
            }
        }
        return deleteFlag;
    }

    /**
     * Method loadStkProfileListVO
     * This method is used to load (category code:Profile code)
     * and Profile name in stkProfileList
     *
     * @param p_con
     *            Connection
     * @return stkProfileList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadStkProfileListVO(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadStkProfileListVO";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        StringBuilder loggerValue = new StringBuilder();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList stkProfileListVO = new ArrayList();
        final StringBuffer strBuff = new StringBuffer(" SELECT profile_code,category_code,");
        strBuff.append("profile_name FROM stk_profiles WHERE status=?  ");
        strBuff.append(" ORDER BY profile_code,profile_name");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlSelect);
            _log.debug(methodName, loggerValue);
        }
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, PretupsI.STK_PROFILE_ACTIVE);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                stkProfileListVO.add(new ListValueVO(rs.getString("profile_name"), rs.getString("category_code") + ":" + rs.getString("profile_code")));
            }
        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadStkProfileListVO]", "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadCategoryListVO", "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadStkProfileListVO]", "", "", "",
                    "Exception:" + e.getMessage());
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: stkProfileListVO size : " + stkProfileListVO.size());
                _log.debug(methodName, msg.toString());
            }
        }
        return stkProfileListVO;
    }

    /**
     * Load Parent Category of selected domain
     *
     * @param p_con
     * @param p_domianCode
     * @return CategoryVO
     * @throws BTSLBaseException
     */
    public CategoryVO loadOwnerCategory(Connection p_con, String p_domianCode) throws BTSLBaseException {

        final String methodName = "loadOwnerCategory";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: Domain Code=");
            loggerValue.append(p_domianCode);
            _log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT c1.category_code, c1.category_name, c1.domain_code, ");
        strBuff.append("  c1.parent_category_code FROM categories c1 where ");
        strBuff.append("  c1.domain_code=? ");
        strBuff.append("  AND c1.sequence_no = 1 AND c1.status <> 'N' ORDER BY c1.sequence_no");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlSelect);
            _log.debug(methodName, loggerValue);
        }
        CategoryVO categoryVO = null;
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_domianCode);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                categoryVO = new CategoryVO();
                categoryVO.setCategoryCode(rs.getString("category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
                categoryVO.setParentCategoryCode(rs.getString("parent_category_code"));

            }

        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadOwnerCategory]", "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(ex.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadOwnerCategory]", "", "", "",
                    "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                StringBuffer msg=new StringBuffer("");
                if (categoryVO != null) {
                    msg.append("Exiting: categoryVO : " + categoryVO);
                    _log.debug(methodName, msg.toString());
                } else {
                    _log.debug(methodName, "Exiting:  categoryVO =null");
                }
            }
        }
        return categoryVO;
    }

    /**
     * Method loadParentCategoryList.
     * This method load all the parent categories.
     *
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_leafRequired
     *            boolean
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadParentCategoryList(Connection p_con, boolean p_leafRequired) throws BTSLBaseException {
        final String methodName = "loadParentCategoryList";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_leafRequired=");
            loggerValue.append(p_leafRequired);
            _log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList categoryListVO = new ArrayList();
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT domain_code,category_code,category_name,sequence_no,hierarchy_allowed ");
        strBuff.append("FROM categories ");
        strBuff.append("WHERE status=? AND display_allowed=? ");
        if (!p_leafRequired) {
            strBuff.append("AND sequence_no <= (SELECT Max(sequence_no) FROM categories WHERE status=? ) ");
        }

        strBuff.append("ORDER BY domain_code,sequence_no");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlSelect);
            _log.debug(methodName, loggerValue);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, PretupsI.CATEGORY_STATUS_ACTIVE);
            pstmtSelect.setString(2, PretupsI.CATEGORY_DISPLAY_ALLOWED);
            if (!p_leafRequired) {
                pstmtSelect.setString(3, PretupsI.CATEGORY_STATUS_ACTIVE);
            }
            rs = pstmtSelect.executeQuery();
            ListValueVO listValueVO = null;
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("category_name"), rs.getString("domain_code") + ":" + rs.getString("sequence_no") + "|" + rs
                        .getString("category_code"));
                listValueVO.setOtherInfo(rs.getString("hierarchy_allowed"));
                categoryListVO.add(listValueVO);
            }

        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadParentCategoryList]", "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadParentCategoryList]", "", "", "",
                    "Exception:" + e.getMessage());
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: size : " + categoryListVO.size());
                _log.debug(methodName, msg.toString());
            }
        }
        return categoryListVO;
    }

    /**
     * Method loadCategoryReportList.
     * This method load all .
     *
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param p_leafRequired
     *            boolean
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadCategoryReportList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadCategoryReportList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }
        StringBuilder loggerValue = new StringBuilder();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList categoryListVO = new ArrayList();
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT domain_code,category_code,category_name,sequence_no ");
        strBuff.append("FROM categories ");
        strBuff.append("WHERE display_allowed=? AND status=? ");
        strBuff.append("ORDER BY domain_code,sequence_no,category_name ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlSelect);
            _log.debug(methodName, loggerValue);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, PretupsI.CATEGORY_DISPLAY_ALLOWED);
            pstmtSelect.setString(2, TypesI.YES);
            rs = pstmtSelect.executeQuery();

            while (rs.next()) {
                categoryListVO.add(new ListValueVO(rs.getString("category_name"), rs.getString("domain_code") + ":" + rs.getString("sequence_no") + "|" + rs
                        .getString("category_code")));
            }
        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryReportList]", "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryReportList]", "", "", "",
                    "Exception:" + e.getMessage());
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: size : " + categoryListVO.size());
                _log.debug(methodName, msg.toString());
            }
        }
        return categoryListVO;
    }

    /**
     * Method loadCategoryReporSeqtList.
     * This method load all .
     *
     * @author sandeep.goel
     * @param p_con
     *            Connection
     * @param loginSeqNo
     *            int
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadCategoryReporSeqtList(Connection p_con, int loginSeqNo) throws BTSLBaseException {
        final String methodName = "loadCategoryReporSeqtList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered ");
        }
        StringBuilder loggerValue = new StringBuilder();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList categoryListVO = new ArrayList();
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT domain_code,category_code,category_name,sequence_no ");
        strBuff.append("FROM categories ");
        strBuff.append("WHERE display_allowed=? ");
        strBuff.append(" AND sequence_no >=? ");
        strBuff.append("ORDER BY domain_code,sequence_no,category_name ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlSelect);
            _log.debug(methodName, loggerValue);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, PretupsI.CATEGORY_DISPLAY_ALLOWED);
            pstmtSelect.setInt(2, loginSeqNo);
            rs = pstmtSelect.executeQuery();

            while (rs.next()) {
                categoryListVO.add(new ListValueVO(rs.getString("category_name"), rs.getString("domain_code") + ":" + rs.getString("sequence_no") + "|" + rs
                        .getString("category_code")));
            }
        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryReportList]", "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryReportList]", "", "", "",
                    "Exception:" + e.getMessage());
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: size : " + categoryListVO.size());
                _log.debug(methodName, msg.toString());
            }
        }
        return categoryListVO;
    }

    /**
     * Method loadCategoryList.
     * This method loads categories list by checking the condition that
     * "p_category must be the TO_CATEGORY in any
     * of the transfer rule and must have ASSOCIATION_TRANSFER_ALLOWED='Y' then
     * loads the categories having the
     * FROM_CATEGORY as the category code"
     *
     * @param p_con
     *            Connection
     * @param p_networkCode
     *            String
     * @param p_category
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadTransferRulesCategoryList(Connection p_con, String p_networkCode, String p_category) throws BTSLBaseException {
        final String methodName = "loadTransferRulesCategoryList";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_networkCode=");
            loggerValue.append(p_networkCode);
            loggerValue.append(" p_category=");
            loggerValue.append(p_category);
            _log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList categoryListVO = new ArrayList();
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT C.category_code,C.category_name,C.sequence_no ");
        strBuff.append("FROM categories C ,chnl_transfer_rules CTR ");
        strBuff.append("WHERE CTR.network_code=? AND C.status=? AND C.display_allowed=? ");
        strBuff.append("AND CTR.to_category =? AND CTR.parent_association_allowed='Y' ");
        strBuff.append("AND CTR.status= ? AND CTR.from_category=C.category_code ");
        strBuff.append("ORDER BY C.sequence_no");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlSelect);
            _log.debug(methodName, loggerValue);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmtSelect.setString(i++, p_networkCode);
            pstmtSelect.setString(i++, PretupsI.CATEGORY_STATUS_ACTIVE);
            pstmtSelect.setString(i++, PretupsI.CATEGORY_DISPLAY_ALLOWED);
            pstmtSelect.setString(i++, p_category);
            pstmtSelect.setString(i++, PretupsI.TRANSFER_RULE_STATUS_ACTIVE);
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                categoryListVO.add(new ListValueVO(rs.getString("category_name"), rs.getString("sequence_no") + ":" + rs.getString("category_code")));
            }
        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadTransferRulesCategoryList]", "",
                    "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadTransferRulesCategoryList]", "",
                    "", "", "Exception:" + e.getMessage());
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: size : " + categoryListVO.size());
                _log.debug(methodName, msg.toString());
            }
        }
        return categoryListVO;
    }

    /**
     * Method loadCategoryGeographicalSequenceVO
     * This method is used to load sequence number for category and for
     * geographical
     * domain
     *
     * @param p_con
     *            Connection
     * @param p_domainCode
     *            String
     * @return CategoryVO
     * @throws BTSLBaseException
     */

    public CategoryVO loadCategoryGeographicalSequenceVO(Connection p_con, String p_domainCode) throws BTSLBaseException {
        final String methodName = "loadCategoryGeographicalSequenceVO";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        StringBuilder loggerValue = new StringBuilder();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        CategoryVO categoryVO = null;
        final StringBuffer strBuff = new StringBuffer(
                "SELECT C.sequence_no CAT_SEQ,G.sequence_no GRPH_SEQ, D.num_of_categories, C.AGENT_ALLOWED agent_al,C.AUTHENTICATION_TYPE ");
        strBuff.append("FROM categories C,geographical_domain_types G, domains D WHERE C.domain_code=? AND C.domain_code=D.domain_code AND ");
        strBuff.append("C.grph_domain_type=G.grph_domain_type AND C.status <> 'N' AND ");
        strBuff.append("C.sequence_no = (SELECT max(sequence_no) FROM categories WHERE ");
        strBuff.append("domain_code=C.domain_code AND status<>'N')");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlSelect);
            _log.debug(methodName, loggerValue);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_domainCode);
            rs = pstmtSelect.executeQuery();

            if (rs.next()) {
                categoryVO = new CategoryVO();
                categoryVO.setAgentAllowed(rs.getString("agent_al")); // Added
                // by
                // deepika
                // aggarwal
                categoryVO.setCategorySequenceNumber(rs.getInt("CAT_SEQ"));
                categoryVO.setGeographicalDomainSeqNo(rs.getInt("GRPH_SEQ"));
                categoryVO.setNumberOfCategoryForDomain(rs.getInt("num_of_categories"));
                categoryVO.setAuthenticationType(rs.getString("AUTHENTICATION_TYPE"));
            } else {
                categoryVO = new CategoryVO();
                categoryVO.setCategorySequenceNumber(0);
                categoryVO.setGeographicalDomainSeqNo(2);
            }
        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryGeographicalSequenceVO]",
                    "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadCategoryForSmsVO", "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryGeographicalSequenceVO]",
                    "", "", "", "Exception:" + e.getMessage());
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: CategorySequenceNumber : " + categoryVO.getCategorySequenceNumber());
                msg.append(", GeographicalDomainSeqNo : " + categoryVO.getGeographicalDomainSeqNo());
                _log.debug(methodName, msg.toString());
            }
        }

        return categoryVO;
    }

    /************************ loadAgentCategoryInfo ******************************/
    /* this is used to get info based on sequence number */

    /**
     * Method loadAgentCategoryDetails.
     * This method is used to load category details according to domain code
     * from Categories Table
     *
     * @param p_con
     *            Connection
     * @param p_sequenceNumber
     *            int
     * @return categoryList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadAgentCategoryDetails(Connection p_con, int p_sequenceNumber, String p_domainCode, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "loadAgentCategoryDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_sequenceNumber=");
            loggerValue.append(p_sequenceNumber);
            _log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList categoryList = new ArrayList();
        final StringBuffer strBuff = new StringBuffer(" SELECT C.modify_allowed,C.category_code,");
        strBuff.append("C.category_name,C.domain_code,C.sequence_no,C.grph_domain_type,C.multiple_grph_domains,");
        strBuff.append("C.web_interface_allowed,C.sms_interface_allowed,C.fixed_roles,C.multiple_login_allowed,");
        strBuff.append("C.view_on_network_block,C.max_login_count,C.status,C.created_on,C.created_by,C.modified_on,");
        strBuff.append("C.modified_by,C.display_allowed,C.modify_allowed,C.product_types_allowed,C.services_allowed,");
        strBuff.append("C.domain_allowed,C.fixed_domains,C.max_txn_msisdn,C.uncntrl_transfer_allowed, C.transfertolistonly,");
        // added on 13/07/07 for low balance alert allow
        strBuff.append("C.low_bal_alert_allow,");
        // end of low balance alert allow
        strBuff.append("C.cp2p_payee_status,C.cp2p_payer_status,C.c2s_payee_status ,C.cp2p_within_list,C.cp2p_within_list_level,");
        strBuff
                .append("C.scheduled_transfer_allowed,C.restricted_msisdns,C.parent_category_code,C.user_id_prefix,C.outlets_allowed ,C.category_type,C.agent_allowed,C.hierarchy_allowed");
        strBuff.append(" FROM categories C WHERE C.sequence_no=? AND C.category_type=? AND C.domain_code=? AND category_code=?");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlSelect);
            _log.debug(methodName, loggerValue);
        }
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setInt(1, p_sequenceNumber);
            pstmtSelect.setString(2, PretupsI.AGENTCATEGORY);
            pstmtSelect.setString(3, p_domainCode);
            pstmtSelect.setString(4, p_categoryCode);
            rs = pstmtSelect.executeQuery();
            CategoryVO categoryVO = null;
            while (rs.next()) {
                categoryVO = new CategoryVO();
                categoryVO.setCategoryCode(rs.getString("category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
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
                categoryVO.setScheduledTransferAllowed(rs.getString("scheduled_transfer_allowed"));
                categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
                categoryVO.setParentCategoryCode(rs.getString("parent_category_code"));
                categoryVO.setUserIdPrefix(rs.getString("user_id_prefix"));
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                categoryVO.setDomainAllowed(rs.getString("domain_allowed"));
                categoryVO.setFixedDomains(rs.getString("fixed_domains"));
                categoryVO.setOutletsAllowed(rs.getString("outlets_allowed"));
                categoryVO.setAgentAllowed(rs.getString("agent_allowed"));
                categoryVO.setHierarchyAllowed(rs.getString("hierarchy_allowed"));
                categoryVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
                // Added on 13/07/07 for Low balance alert allow
                categoryVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
                // End of low balance alert allow
                categoryVO.setRechargeByParentOnly(rs.getString("c2s_payee_status"));
                categoryVO.setCp2pPayee(rs.getString("cp2p_payee_status"));
                categoryVO.setCp2pPayer(rs.getString("cp2p_payer_status"));
                categoryVO.setCp2pWithinList(rs.getString("cp2p_within_list"));
                categoryVO.setParentOrOwnerRadioValue(rs.getString("cp2p_within_list_level"));

                categoryList.add(categoryVO);
            }
        }

        catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryDetails]", "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadCategoryDetails", "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryDetails]", "", "", "",
                    "Exception:" + e.getMessage());
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: size : " + categoryList.size());
                _log.debug(methodName, msg.toString());
            }
        }

        return categoryList;
    }

    /**
     * this mathod move from transferProfileDAO
     * Method :isUncontrolTransferAllowed this method check the
     * uncontroltransfer allowed or not for category
     * param p_con Connection
     *
     * @param p_domainCode
     * @param p_categoryCode
     * @return flag boolean
     * @throws BTSLBaseException
     */

    public boolean isUncontrolTransferAllowed(Connection p_con, String p_domainCode, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "isUncontrolTransferAllowed";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_domainCode=");
            loggerValue.append(p_domainCode);
            loggerValue.append(" p_categoryCode=");
            loggerValue.append(p_categoryCode);
            _log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean flag = false;
        final StringBuffer strBuff = new StringBuffer(" SELECT uncntrl_transfer_allowed FROM categories");
        strBuff.append(" WHERE domain_code=? AND category_code=? ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlSelect);
            _log.debug(methodName, loggerValue);
        }
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_domainCode);
            pstmtSelect.setString(2, p_categoryCode);
            rs = pstmtSelect.executeQuery();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query sqlSelect= " + sqlSelect);
            }
            String unctrltransferallowed = null;
            if (rs.next()) {
                unctrltransferallowed = rs.getString("uncntrl_transfer_allowed");
            }
            if ("Y".equalsIgnoreCase(unctrltransferallowed)) {
                flag = true;
            }
        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isUncontrolTransferAllowed]", "", "",
                    "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isUncontrolTransferAllowed]", "", "",
                    "", "Exception:" + e.getMessage());
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: flag : " + flag);
                _log.debug(methodName, msg.toString());
            }
        }
        return flag;
    }

    /**
     * this method is used to load the list of category
     * the categories are ordered according to domain
     * within same domain categories are ordered by sequence number
     *
     * @param p_con
     *            Connection
     * @return HashMap
     *
     */
    public HashMap loadcategoryListForView(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadcategoryListForView";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        StringBuilder loggerValue = new StringBuilder();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        HashMap map = null;
        final StringBuffer strBuff = new StringBuffer(
                "SELECT D.domain_name,C.category_code,C.category_name,C.domain_code,C.user_id_prefix,C.sequence_no,C.category_type,LK.lookup_name ");
        strBuff.append(" FROM categories C, domains D, LOOKUPS LK ");
        strBuff.append(" WHERE C.domain_code=D.domain_code AND C.status<>? AND LK.lookup_code=C.category_type AND LK.lookup_type=? ORDER BY C.domain_code,C.sequence_no ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlSelect);
            _log.debug(methodName, loggerValue);
        }
        int count = 0;
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, PretupsI.STATUS_DELETE);
            pstmtSelect.setString(2, PretupsI.CATEGORY_TYPE_CODE);
            rs = pstmtSelect.executeQuery();
            CategoryVO categoryVO = null;
            ArrayList list = null;
            if (rs != null) {
                map = new HashMap();
                while (rs.next()) {
                    categoryVO = new CategoryVO();
                    categoryVO.setDomainName(rs.getString("domain_name"));
                    categoryVO.setCategoryCode(rs.getString("category_code"));
                    categoryVO.setCategoryName(rs.getString("category_name"));
                    categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
                    categoryVO.setUserIdPrefix(rs.getString("user_id_prefix"));
                    categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                    categoryVO.setCategoryTypeCode(rs.getString("category_type"));
                    categoryVO.setCategoryType(rs.getString("lookup_name"));
                    if (map.containsKey(categoryVO.getDomainName())) {
                        list.add(categoryVO);
                        map.put(categoryVO.getDomainName(), list);
                    } else {
                        list = new ArrayList();
                        list.add(categoryVO);
                        map.put(categoryVO.getDomainName(), list);
                    }

                    count++;
                }
            }

        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadcategoryListForView]", "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(ex.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadcategoryListForView]", "", "", "",
                    "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: categorylist size : " + count);
                _log.debug(methodName, msg.toString());
            }
        }
        return map;
    }

    /**
     * Method loadRestrictedCatList
     * This method is used to load Categories with restricted msisdn flag set to
     * "Y"
     *
     * @param p_con
     *            Connection
     * @param p_domainStr
     *            String
     * @param p_ownerOnly
     *            boolean
     * @param p_isRestricted
     *            TODO
     * @param p_isScheduled
     *            TODO
     * @return categoryList ArrayList
     * @throws BTSLBaseException
     * @author Amit Ruwali
     *         modify the query for making it compatible for normal users also
     */
    public ArrayList loadRestrictedCatList(Connection p_con, String p_domainStr, boolean p_ownerOnly, boolean p_isRestricted, boolean p_isScheduled) throws BTSLBaseException {
        final String methodName = "loadRestrictedCatList";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_domainStr=");
            loggerValue.append(p_domainStr);
            loggerValue.append(" p_ownerOnly=");
            loggerValue.append(p_ownerOnly);
            loggerValue.append(" p_isRestricted=");
            loggerValue.append(p_isRestricted);
            loggerValue.append(" p_isScheduled=");
            loggerValue.append(p_isScheduled);
            _log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList categoryListVO = new ArrayList();

        final String domainStr = p_domainStr.replaceAll("'", "");
        final String ss = domainStr.replaceAll("\" ", "");
        final String m_domainStr[] = ss.split(",");
        final StringBuffer strBuff = new StringBuffer("SELECT c.category_name,c.category_code,c.domain_code ");
        strBuff.append("FROM categories c ");
        strBuff.append("WHERE c.status = 'Y'  AND c.domain_code IN (");
        for (int i = 0; i < m_domainStr.length; i++) {
            strBuff.append(" ?");
            if (i != m_domainStr.length - 1) {
                strBuff.append(",");
            }
        }
        strBuff.append(")");
        if (p_isRestricted) {
            strBuff.append(" AND c.restricted_msisdns ='Y' ");
        }
        if (p_isScheduled) {
            strBuff.append(" AND c.scheduled_transfer_allowed ='Y' ");
        }
        if (p_ownerOnly) {
            strBuff.append("AND c.sequence_no ='1' ");
        }
        strBuff.append(" ORDER BY c.sequence_no");
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(strBuff.toString());
            _log.debug(methodName, loggerValue);
        }
        try {
            int i = 0;
            pstmtSelect = p_con.prepareStatement(strBuff.toString());
            for (int x = 0; x < m_domainStr.length; x++) {
                pstmtSelect.setString(++i, m_domainStr[x]);
            }
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                categoryListVO.add(new ListValueVO(rs.getString("category_name"), rs.getString("domain_code") + ":" + rs.getString("category_code")));
            }
        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadRestrictedCatList]", "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadCategoryVOList", "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadRestrictedCatList]", "", "", "",
                    "Exception:" + e.getMessage());
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: categorylist size : " + categoryListVO.size());
                _log.debug(methodName, msg.toString());
            }
        }
        return categoryListVO;
    }

    /**
     * This method is used before modify the category it will search for the
     * user
     * this methos added for CR00043
     *
     * @param p_con
     *            Connection
     * @param p_categoryVO
     *            CategoryVO
     * @return boolean
     * @throws BTSLBaseException
     */

    public boolean isUserHierarchyExists(Connection p_con, CategoryVO p_categoryVO) throws BTSLBaseException {

        final String methodName = "isUserHierarchyExists";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_categoryVO=");
            loggerValue.append(p_categoryVO);
            _log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM users WHERE parent_id IN ");
        sqlBuff.append("(SELECT user_id FROM users WHERE category_code=? AND status NOT IN('C','N')) ");
        sqlBuff.append("AND status NOT IN('C','N')");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(selectQuery);
            _log.debug(methodName, loggerValue);
        }

        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_categoryVO.getCategoryCode().toUpperCase());
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isUserHierarchyExists]", "", "", "",
                    "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isUserExists", "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isUserHierarchyExists]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: isExists found : " + found);
                _log.debug(methodName, msg.toString());
            }
        }
        return found;
    }

    /**
     * This method is used before modify the category to check either the low
     * balance alert is true or false
     *
     * @param p_con
     *            Connection
     * @param p_categoryCode
     *            String
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isLowBalAlertUserExists(Connection p_con, String p_categoryCode) throws BTSLBaseException {

        final String methodName = "isLowBalAlertUserExists";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_categoryCode=");
            loggerValue.append(p_categoryCode);
            _log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM channel_users CU, users U WHERE U.user_id=CU.user_id ");
        sqlBuff.append("AND U.status NOT IN('C','N') AND CU.low_bal_alert_allow='Y' AND U.category_code=? ");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(selectQuery);
            _log.debug(methodName, loggerValue);
        }

        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_categoryCode);
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isLowBalAlertUserExists]", "", "", "",
                    "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isUserExists", "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isLowBalAlertUserExists]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: isExists found : " + found);
                _log.debug(methodName, msg.toString());
            }
        }
        return found;
    }

    // ADDED FOR BATCH ADD COMMISSION PROFILE

    /**
     * Method :loadMasterCategoryList
     * This method load list of Categories on the basis of domain code
     *
     * @param p_con
     *            java.sql.Connection
     * @param p_domainCode
     *            java.lang.String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadMasterCategoryList(Connection p_con, String p_domainCode) throws BTSLBaseException {
        final String methodName = "loadMasterCategoryList";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_domainCode=");
            loggerValue.append(p_domainCode);
            _log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer("SELECT category_code, category_name, sequence_no, ");
        strBuff.append("domain_code, web_interface_allowed FROM categories ");
        strBuff.append("WHERE domain_code= CASE ? WHEN 'ALL' THEN domain_code ELSE ? END ");
        strBuff.append("AND domain_code <> ? AND status='Y' ORDER BY domain_code,sequence_no");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlSelect);
            _log.debug(methodName, loggerValue);
        }
        ArrayList categoryList = null;
        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            int i = 1;
            pstmtSelect.setString(i++, p_domainCode);
            pstmtSelect.setString(i++, p_domainCode);
            pstmtSelect.setString(i++, PretupsI.OPERATOR_TYPE_OPT);
            rs = pstmtSelect.executeQuery();
            if (!rs.equals(null)) {
                categoryList = new ArrayList();
            }
            CategoryVO categoryVO = null;
            while (rs.next()) {
                categoryVO = new CategoryVO();
                categoryVO.setCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("category_code")));
                categoryVO.setCategoryName(SqlParameterEncoder.encodeParams(rs.getString("category_name")));
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                categoryVO.setDomainCodeforCategory(SqlParameterEncoder.encodeParams(rs.getString("domain_code")));
                categoryVO.setWebInterfaceAllowed(SqlParameterEncoder.encodeParams(rs.getString("web_interface_allowed")));
                categoryList.add(categoryVO);
            }
        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadMasterCategoryList]", "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(ex.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserDAO[loadMasterCategoryList]", "", "", "",
                    "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: categoryList size : " + categoryList.size());
                _log.debug(methodName, msg.toString());
            }
        }
        return categoryList;
    }

    /**
     * Method for loading category List For Channel Admin.
     *
     * Used in UsersAction
     *
     * @author mohit.goel
     *
     * @param p_con
     *            java.sql.Connection
     * @param p_domainCode
     *            String
     * @param p_lookType
     *            String
     * @return java.util.ArrayList
     * @exception BTSLBaseException
     */
    public ArrayList loadCategoryListForChannelAdmin(Connection p_con, String p_domainCode, String p_lookType) throws BTSLBaseException {
        final String methodName = "loadCategoryListForChannelAdmin";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_domainCode=");
            loggerValue.append(p_domainCode);
            loggerValue.append(" p_lookType=");
            loggerValue.append(p_lookType);
            _log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();
        strBuff.append(" SELECT c.category_code,c.category_name,");
        strBuff.append("c.domain_code,c.sequence_no,c.grph_domain_type, ");
        strBuff.append("c.multiple_grph_domains,c.web_interface_allowed,c.sms_interface_allowed, ");
        strBuff.append("c.fixed_roles,c.status,c.multiple_login_allowed, ");
        strBuff.append("c.view_on_network_block,c.max_login_count,c.user_id_prefix,c.product_types_allowed,");
        strBuff.append("c.services_allowed,c.domain_allowed,c.fixed_domains,c.outlets_allowed,c.max_txn_msisdn,");
        strBuff.append(" gdt.grph_domain_type_name,gdt.sequence_no grph_sequence_no ");
        strBuff.append("FROM categories c,lookups l, geographical_domain_types gdt WHERE c.domain_code = ? and l.lookup_type = ? and c.category_code <>'BCU'");
        strBuff.append(" AND l.lookup_code = c.category_code and c.status <> 'N' and l.status='Y' ");
        strBuff.append(" AND gdt.grph_domain_type = c.grph_domain_type ORDER BY c.sequence_no");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlSelect);
            _log.debug(methodName, loggerValue);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_domainCode);
            pstmt.setString(2, p_lookType);
            rs = pstmt.executeQuery();
            CategoryVO categoryVO = null;
            while (rs.next()) {
                categoryVO = new CategoryVO();
                categoryVO.setCategoryCode(rs.getString("category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
                categoryVO.setMultipleGrphDomains(rs.getString("multiple_grph_domains"));
                categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
                categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
                categoryVO.setFixedRoles(rs.getString("fixed_roles"));
                categoryVO.setCategoryStatus(rs.getString("status"));
                categoryVO.setMultipleLoginAllowed(rs.getString("multiple_login_allowed"));
                categoryVO.setViewOnNetworkBlock(rs.getString("view_on_network_block"));
                categoryVO.setMaxLoginCount(rs.getInt("max_login_count"));
                categoryVO.setUserIdPrefix(rs.getString("user_id_prefix"));
                categoryVO.setServiceAllowed(rs.getString("services_allowed"));
                categoryVO.setGrphDomainSequenceNo(rs.getInt("grph_sequence_no"));
                categoryVO.setProductTypeAllowed(rs.getString("product_types_allowed"));
                categoryVO.setDomainAllowed(rs.getString("domain_allowed"));
                categoryVO.setFixedDomains(rs.getString("fixed_domains"));
                categoryVO.setOutletsAllowed(rs.getString("outlets_allowed"));
                categoryVO.setMaxTxnMsisdnInt(rs.getInt("max_txn_msisdn"));
                categoryVO.setGrphDomainTypeName(rs.getString("grph_domain_type_name"));

                list.add(categoryVO);
            }

        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryListForChannelAdmin]", "",
                    "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(ex.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryListForChannelAdmin]", "",
                    "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: categoryList size : " + list.size());
                _log.debug(methodName, msg.toString());
            }
        }
        return list;
    }

    /**
     * Method loadRestrictedCatList
     * This method is used to load Categories with restricted msisdn flag set to
     * "Y"
     *
     * @param p_con
     *            Connection
     * @param p_domainStr
     *            String
     * @param p_ownerOnly
     *            boolean
     * @param p_isRestricted
     *            TODO
     * @param p_isScheduled
     *            TODO
     * @return categoryList ArrayList
     * @throws BTSLBaseException
     * @author Amit Ruwali
     *         modify the query for making it compatible for normal users also
     *         praveen
     */

    public ArrayList loadAutoC2CCatList(Connection p_con, String p_domainStr, boolean p_ownerOnly, boolean p_isRestricted, boolean p_isScheduled) throws BTSLBaseException {
        final String methodName = "loadAutoC2CCatList";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_domainStr=");
            loggerValue.append(p_domainStr);
            loggerValue.append(" p_ownerOnly=");
            loggerValue.append(p_ownerOnly);
            loggerValue.append(" p_isRestricted=");
            loggerValue.append(p_isRestricted);
            loggerValue.append(" p_isScheduled=");
            loggerValue.append(p_isScheduled);
            _log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList categoryListVO = new ArrayList();
        final String domainStr = p_domainStr.replaceAll("'", "");
        final String ss = domainStr.replaceAll("\" ", "");
        final String m_domainStr[] = ss.split(",");
        final StringBuffer strBuff = new StringBuffer("SELECT category_name,category_code,domain_code ");
        strBuff.append("FROM categories ");
        strBuff.append("WHERE status = 'Y' AND domain_code IN (");
        for (int i = 0; i < m_domainStr.length; i++) {
            strBuff.append(" ?");
            if (i != m_domainStr.length - 1) {
                strBuff.append(",");
            }
        }
        strBuff.append(")");

        if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_C2C_SOS_CAT_ALLOWED))).booleanValue()){
            strBuff.append(" AND category_code like ? and category_code not in (Select control_code from control_preferences where preference_code = ? and upper(value) = ? ) ");
        }else{
            strBuff.append(" AND category_code IN (Select control_code from control_preferences where preference_code = ? and lower(value) = ? )");
        }

        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(strBuff.toString());
            _log.debug(methodName, loggerValue);
        }
        try {
            int i = 0;
            pstmtSelect = p_con.prepareStatement(strBuff.toString());
            for (int x = 0; x < m_domainStr.length; x++) {
                pstmtSelect.setString(++i, m_domainStr[x]);
            }
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_C2C_SOS_CAT_ALLOWED))).booleanValue()){
                pstmtSelect.setString(++i,"%");
                pstmtSelect.setString(++i, PretupsI.AUTO_C2C_SOS_CAT_ALLOWED);
                pstmtSelect.setString(++i, PretupsI.FALSE);
            }else{
                pstmtSelect.setString(++i, PretupsI.AUTO_C2C_SOS_CAT_ALLOWED);
                pstmtSelect.setString(++i, PretupsI.AUTO_C2C_TRUE);

            }
            rs = pstmtSelect.executeQuery();
            while (rs.next()) {
                categoryListVO.add(new ListValueVO(rs.getString("category_name"), rs.getString("domain_code") + ":" + rs.getString("category_code")));
            }
        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadRestrictedCatList]", "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadCategoryVOList", "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadRestrictedCatList]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadRestrictedCatList", "error.general.processing");
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: size : " + categoryListVO.size());
                _log.debug(methodName, msg.toString());
            }
        }
        return categoryListVO;
    }

    /**
     * Method :loadMasterGradeList
     * This method load list of Grades on the basis of category code
     *
     * @param p_con
     *            java.sql.Connection
     * @param p_categoryCode
     *            java.lang.String
     * @return HashMap<String,GradeVO>
     * @throws BTSLBaseException
     * @author shashank.gaur
     */
    public ArrayList<GradeVO> loadMasterGradeList(Connection p_con, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "loadMasterGradeList";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_categoryCode=");
            loggerValue.append(p_categoryCode);
            _log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer("SELECT category_code, GRADE_CODE, GRADE_NAME ");
        strBuff.append(" FROM channel_grades WHERE category_code =CASE ? WHEN 'ALL' THEN category_code ELSE ? END AND status=?");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlSelect);
            _log.debug(methodName, loggerValue);
        }
        ArrayList<GradeVO> gradeList = null;
        try {
            int i = 1;
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(i++, p_categoryCode);
            pstmtSelect.setString(i++, p_categoryCode);
            pstmtSelect.setString(i++, PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE);
            rs = pstmtSelect.executeQuery();
            GradeVO gradeVO = null;
            gradeList = new ArrayList<GradeVO>();
            while (rs.next()) {
                gradeVO = new GradeVO();
                gradeVO.setCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("category_code")));
                gradeVO.setGradeCode(SqlParameterEncoder.encodeParams(rs.getString("GRADE_CODE")));
                gradeVO.setGradeName(SqlParameterEncoder.encodeParams(rs.getString("GRADE_NAME")));
                gradeList.add(gradeVO);
            }
        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadMasterGradeList]", "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(ex.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadMasterGradeList]", "", "", "",
                    "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: categoryList size : " + gradeList.size());
                _log.debug(methodName, msg.toString());
            }
        }
        return gradeList;
    }
    /**
     * Method loadCategoryVOList
     * This method is used to load (category code:domain code) and category name
     * in the
     * list value bean this method is used for dynamically select other combo in
     * the
     * basis of previous one.
     *
     * @param p_con
     *            Connection
     * @return categoryList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadCategoryVOListForUserTransfer(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadCategoryVOListForUserTransfer";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered");
        }
        StringBuilder loggerValue = new StringBuilder();
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList categoryListVO = new ArrayList();


        final StringBuffer strBuff = new StringBuffer("  SELECT c.domain_code,c. category_code, ");
        strBuff.append(" c.category_name FROM categories c,(SELECT domain_code,max(sequence_no) sequence_no FROM categories WHERE status='Y' AND display_allowed='Y' ");
        strBuff.append("  AND modify_allowed='Y'  group by domain_code ");
        strBuff.append("  ) X WHERE c.status=? AND c.display_allowed=? ");
        strBuff.append("  AND c.modify_allowed=? and  c.sequence_no = X.sequence_no and c.domain_code = X.domain_code ");


        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlSelect);
            _log.debug(methodName, loggerValue);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, PretupsI.CATEGORY_STATUS_ACTIVE);
            pstmtSelect.setString(2, PretupsI.CATEGORY_DISPLAY_ALLOWED);
            pstmtSelect.setString(3, PretupsI.CATEGORY_MODIFY_ALLOWED);
            rs = pstmtSelect.executeQuery();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed= " + sqlSelect);
            }
            while (rs.next()) {
                categoryListVO.add(new ListValueVO(rs.getString("category_name"), rs.getString("domain_code") + ":" + rs.getString("category_code")));
            }
        }

        catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryVOListForUserTransfer]", "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryVOListForUserTransfer]", "", "", "",
                    "Exception:" + e.getMessage());
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: size : " + categoryListVO.size());
                _log.debug(methodName, msg.toString());
            }
        }

        return categoryListVO;
    }







    /**
     * Method loadCategoryDetails.
     * This method is used to load category details according to domain code
     * from Categories Table
     *
     * @param p_con
     *            Connection
     * @param p_domainCode
     *            String
     * @return categoryList ArrayList
     * @throws BTSLBaseException
     */

    public ArrayList loadCategoryDetailsInfo(Connection p_con, String p_domainCode,String p_categoryCode) throws BTSLBaseException {
        final String methodName = "loadCategoryDetails";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_domainCode =");
            loggerValue.append(p_domainCode);
            _log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList categoryList = new ArrayList();
        final StringBuffer strBuff = new StringBuffer(" SELECT D.num_of_categories,C.modify_allowed,C.category_code,");
        strBuff.append("C.category_name,C.domain_code,C.sequence_no,C.grph_domain_type,C.multiple_grph_domains,");
        strBuff.append("C.web_interface_allowed,C.sms_interface_allowed,C.fixed_roles,C.multiple_login_allowed,");
        strBuff.append("C.view_on_network_block,C.max_login_count,C.status,C.created_on,C.created_by,C.modified_on,");
        strBuff.append("C.modified_by,C.display_allowed,C.modify_allowed,C.product_types_allowed,C.services_allowed,");
        strBuff.append("C.domain_allowed,C.fixed_domains,C.max_txn_msisdn,C.uncntrl_transfer_allowed,C.transfertolistonly,");
        // Added on 13/07/07 for Low balance alert allow
        strBuff.append("C.low_bal_alert_allow,");
        // End of low balance alert allow
        // added by santanu
        strBuff.append("C.cp2p_payee_status,C.cp2p_payer_status,C.c2s_payee_status ,C.cp2p_within_list,C.cp2p_within_list_level,");
        strBuff
                .append("C.scheduled_transfer_allowed,C.restricted_msisdns,C.parent_category_code,C.user_id_prefix,C.outlets_allowed ,C.category_type,C.agent_allowed,C.hierarchy_allowed,LK.lookup_name,c.AUTHENTICATION_TYPE");
        strBuff.append(" FROM categories C,domains D ,LOOKUPS LK WHERE D.domain_code=? AND D.domain_code=C.domain_code");
        strBuff.append("  AND category_code = ? AND C.status <>? AND C.display_allowed=? AND C.modify_allowed=? AND LK.lookup_code=C.category_type AND LK.lookup_type=? ORDER BY C.sequence_no");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlSelect);
            _log.debug(methodName, loggerValue);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_domainCode);
            pstmtSelect.setString(2, p_categoryCode);
            pstmtSelect.setString(3, PretupsI.CATEGORY_STATUS_DELETE);
            pstmtSelect.setString(4, PretupsI.CATEGORY_DISPLAY_ALLOWED);
            pstmtSelect.setString(5, PretupsI.CATEGORY_MODIFY_ALLOWED);
            pstmtSelect.setString(6, PretupsI.CATEGORY_TYPE_CODE);
            rs = pstmtSelect.executeQuery();
            int radioIndex = 0;
            CategoryVO categoryVO = null;

            while (rs.next()) {
                categoryVO = new CategoryVO();
                categoryVO.setCategoryCode(rs.getString("category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
                categoryVO.setParentCategoryCode(rs.getString("parent_category_code"));
                categoryVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
                categoryVO.setRecordCount(rs.getInt("num_of_categories"));
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
                categoryVO.setScheduledTransferAllowed(rs.getString("scheduled_transfer_allowed"));
                categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
                categoryVO.setParentCategoryCode(rs.getString("parent_category_code"));
                categoryVO.setUserIdPrefix(rs.getString("user_id_prefix"));
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                categoryVO.setDomainAllowed(rs.getString("domain_allowed"));
                categoryVO.setFixedDomains(rs.getString("fixed_domains"));
                categoryVO.setOutletsAllowed(rs.getString("outlets_allowed"));
                categoryVO.setCategoryType(rs.getString("lookup_name"));
                categoryVO.setCategoryTypeCode(rs.getString("category_type"));
                categoryVO.setAgentAllowed(rs.getString("agent_allowed"));
                categoryVO.setHierarchyAllowed(rs.getString("hierarchy_allowed"));
                categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
                // Added on 13/07/07 for Low balance alert allow
                categoryVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
                // End of low balance alert allow
                // add
                categoryVO.setRechargeByParentOnly(rs.getString("c2s_payee_status"));
                categoryVO.setCp2pPayee(rs.getString("cp2p_payee_status"));
                categoryVO.setCp2pPayer(rs.getString("cp2p_payer_status"));
                categoryVO.setCp2pWithinList(rs.getString("cp2p_within_list"));
                categoryVO.setParentOrOwnerRadioValue(rs.getString("cp2p_within_list_level"));
                categoryVO.setAuthenticationType(rs.getString("AUTHENTICATION_TYPE"));
                categoryVO.setRadioIndex(radioIndex);
                categoryList.add(categoryVO);
                radioIndex++;
            }
        }

        catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryDetails]", "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryDetails]", "", "", "",
                    "Exception:" + e.getMessage());
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: size : " + categoryList.size());
                _log.debug(methodName, msg.toString());
            }
        }

        return categoryList;
    }





    public int updateUnassignedDomains(Connection p_con, CategoryVO p_categoryVO) throws BTSLBaseException {

        final String methodName = "updateUnassignedDomains";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_categoryVO =");
            loggerValue.append(p_categoryVO);
            _log.debug(methodName, loggerValue);
        }
        int updateCount = -1;
        PreparedStatement pstmtUpdate = null;
        try {
            final StringBuffer updateQueryBuff = new StringBuffer("UPDATE categories SET domain_code = ? , parent_category_Code = ?, ");
            updateQueryBuff.append(" status =? WHERE  UPPER(category_code) = ? ");
            final String updateQuery = updateQueryBuff.toString();
            pstmtUpdate = p_con.prepareStatement(updateQuery);

            pstmtUpdate.setString(1, p_categoryVO.getDomainCodeforCategory());
            pstmtUpdate.setString(2, p_categoryVO.getCategoryCode().toUpperCase());
            pstmtUpdate.setString(3, PretupsI.YES);
            pstmtUpdate.setString(4, p_categoryVO.getCategoryCode().toUpperCase());

            updateCount = pstmtUpdate.executeUpdate();

        }
        catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[updateUnassignedDomains]", "", "", "",
                    "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "deleteCategory", "error.general.sql.processing");
        }

        catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[updateUnassignedDomains]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (pstmtUpdate != null) {
                    pstmtUpdate.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            if (_log.isDebugEnabled()) {
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: updateCount : " + updateCount);
                _log.debug(methodName, msg.toString());
            }
        }

        return updateCount;
    }



    public void cleanupCategoryunAssgndData() throws BTSLBaseException, SQLException {

        final String methodName = "cleanupCategoryunAssgndData";
        StringBuilder loggerValue = new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_categoryVO =");
            // loggerValue.append(p_categoryVO);
            _log.debug(methodName, loggerValue);
        }

        Connection con = null;
        MComConnectionI mcomCon = null;

        int deleteCount = -1;
        PreparedStatement pstmtDeletCateogryRole = null;
        PreparedStatement pstmtDeletCateogryReqGatWTypes = null;
        PreparedStatement pstmtDeleteStkProfile = null;
        PreparedStatement pstmtDeleteGroupRoles = null;
        PreparedStatement pstmtDeleteCateogry = null;

        try {

            mcomCon = new MComConnection();
            con = mcomCon.getConnection();

            final String deleteCategoryRoleQuery = "delete from  category_roles where category_Code = ?";
            final String deleteCategoryReqGatwTypes = "delete from  category_req_gtw_types where category_Code = ?";
            final String deletestk_profiles = "delete from stk_profiles where category_Code = ?";
            final String deleteGroupRoles = "delete from group_roles where group_role_Code = ?";
            final String deleteCategoryQuery = "delete from  categories where category_Code = ?";

            ArrayList<CategoryVO> list = loadCategoryDetailsUnassignedDomain(con, PretupsI.DOMAIN_UNASIGNED_CATEGORY);

            long difference_In_Time = 0;
            long difference_In_Hours = 0;
            if (!BTSLUtil.isNullOrEmptyList(list)) {

                for (int i = 0; i < list.size(); i++) {
                    CategoryVO cateogryVO = list.get(i);
                    Calendar categoryCreatedOn = Calendar.getInstance();
                    categoryCreatedOn.setTime(cateogryVO.getCreatedOn());

                    Calendar currentDate = Calendar.getInstance();

                    difference_In_Time = currentDate.getTimeInMillis() - categoryCreatedOn.getTimeInMillis();
                    difference_In_Hours =(difference_In_Time / 1000) / 60 / 60 % 24;


                    if (difference_In_Hours >= 2) {
                        pstmtDeletCateogryRole = con.prepareStatement(deleteCategoryRoleQuery);
                        pstmtDeletCateogryRole.setString(1, cateogryVO.getCategoryCode());
                        deleteCount = pstmtDeletCateogryRole.executeUpdate();

                        _log.info(methodName, "Clean up record in Category Role table for cateogry code ::  "
                                + cateogryVO.getCategoryCode() + " delete count" + deleteCount);

                        pstmtDeletCateogryReqGatWTypes = con.prepareStatement(deleteCategoryReqGatwTypes);
                        pstmtDeletCateogryReqGatWTypes.setString(1, cateogryVO.getCategoryCode());
                        deleteCount = pstmtDeletCateogryReqGatWTypes.executeUpdate();

                        _log.info(methodName, "Clean up record in category_req_gtw_types table for cateogry code ::  "
                                + cateogryVO.getCategoryCode() + " delete count" + deleteCount);

                        pstmtDeleteStkProfile = con.prepareStatement(deletestk_profiles);
                        pstmtDeleteStkProfile.setString(1, cateogryVO.getCategoryCode());
                        deleteCount = pstmtDeleteStkProfile.executeUpdate();

                        _log.info(methodName, "Clean up record in stk_profiles table for cateogry code ::  "
                                + cateogryVO.getCategoryCode() + " delete count" + deleteCount);

                        pstmtDeleteGroupRoles = con.prepareStatement(deleteGroupRoles);
                        pstmtDeleteGroupRoles.setString(1, cateogryVO.getCategoryCode());
                        deleteCount = pstmtDeleteGroupRoles.executeUpdate();

                        _log.info(methodName, "Clean up record in Group_Roles table for cateogry code ::  "
                                + cateogryVO.getCategoryCode() + " delete count" + deleteCount);

                        pstmtDeleteCateogry = con.prepareStatement(deleteCategoryQuery);
                        pstmtDeleteCateogry.setString(1, cateogryVO.getCategoryCode());
                        deleteCount = pstmtDeleteCateogry.executeUpdate();

                        _log.info(methodName, "Clean up record in Categories table for cateogry code ::  "
                                + cateogryVO.getCategoryCode() + " delete count" + deleteCount);

                    }

                }
                con.commit();
            }
        }

        catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "categoryWebDAO[updateUnassignedDomains]", "", "", "", "SQL Exception:" + sqle.getMessage());
            con.rollback();
            throw new BTSLBaseException(this, "deleteCategory", "error.general.sql.processing");
        }

        catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "categoryWebDAO[updateUnassignedDomains]", "", "", "", "Exception:" + e.getMessage());
            con.rollback();
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
            try {
                if (pstmtDeletCateogryRole != null) {
                    pstmtDeletCateogryRole.close();
                }

                if (pstmtDeletCateogryReqGatWTypes != null) {
                    pstmtDeletCateogryReqGatWTypes.close();
                }

                if (pstmtDeleteStkProfile != null) {
                    pstmtDeleteStkProfile.close();
                }

                if (pstmtDeleteGroupRoles != null) {
                    pstmtDeleteGroupRoles.close();
                }

                if (pstmtDeleteCateogry != null) {
                    pstmtDeleteCateogry.close();
                }

                if (mcomCon != null) {
                    mcomCon.close("");
                    mcomCon = null;
                }
                if(con != null)	con.close();

            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

        }

    }







    public ArrayList loadCategoryDetailsUnassignedDomain(Connection p_con, String p_domainCode) throws BTSLBaseException {
        final String methodName = "loadCategoryDetailsUnassignedDomain";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_domainCode =");
            loggerValue.append(p_domainCode);
            _log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        final ArrayList categoryList = new ArrayList();
        final StringBuffer strBuff = new StringBuffer(" SELECT C.category_code,");
        strBuff.append("C.category_name,C.domain_code,C.CREATED_ON ");
        strBuff.append(" FROM categories C WHERE C.parent_category_code = ? ");
        strBuff.append(" ");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlSelect);
            _log.debug(methodName, loggerValue);
        }

        try {
            pstmtSelect = p_con.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, p_domainCode);
            rs = pstmtSelect.executeQuery();
            CategoryVO categoryVO = null;

            while (rs.next()) {
                categoryVO = new CategoryVO();
                categoryVO.setCategoryCode(rs.getString("category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
                categoryVO.setCreatedOn(rs.getDate("CREATED_ON"));
                categoryList.add(categoryVO);

            }
        }

        catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryDetailsUnassignedDomain]", "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryDetailsUnassignedDomain]", "", "", "",
                    "Exception:" + e.getMessage());
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: size : " + categoryList.size());
                _log.debug(methodName, msg.toString());
            }
        }

        return categoryList;
    }


    //domaincode and name.
    public ArrayList loadCategorListByDomainCodewithName(Connection p_con, String p_domainCode) throws BTSLBaseException {
        final String methodName = "loadCategorListByDomainCode";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered:p_domainCode=");
            loggerValue.append(p_domainCode);
            _log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT c.category_code,c.category_name,C.category_type,");
        strBuff.append("c.domain_code,c.sequence_no,c.grph_domain_type, ");
        strBuff.append("c.multiple_grph_domains,c.web_interface_allowed, c.sms_interface_allowed, ");
        strBuff.append("c.fixed_roles,c.status,c.multiple_login_allowed, c.transfertolistonly, ");
        strBuff.append("c.view_on_network_block,c.max_login_count,c.user_id_prefix,c.product_types_allowed,");
        strBuff.append("c.services_allowed,c.domain_allowed,c.fixed_domains,c.max_txn_msisdn,c.outlets_allowed,c.restricted_msisdns, ");
        strBuff.append(" gdt.grph_domain_type_name,gdt.sequence_no grph_sequence_no ");
        // Added for low balance alert
        strBuff.append(",c.low_bal_alert_allow ");
        strBuff.append(",c.cp2p_payee_status,c.cp2p_payer_status,c.c2s_payee_status ,c.cp2p_within_list,c.cp2p_within_list_level,c.authentication_type,d.domain_name ");
        strBuff.append("FROM categories c,domains d,geographical_domain_types gdt WHERE c.status <> 'N' AND c.domain_code = ? ");
        strBuff.append("AND gdt.grph_domain_type = c.grph_domain_type  and c.domain_code= d.domain_code   ORDER BY c.sequence_no");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlSelect);
            _log.debug(methodName, loggerValue);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, p_domainCode);

            rs = pstmt.executeQuery();
            CategoryVO categoryVO = null;
            while (rs.next()) {
                categoryVO = new CategoryVO();
                categoryVO.setCategoryCode(rs.getString("category_code"));
                categoryVO.setDomainName(rs.getString("domain_name"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
                categoryVO.setMultipleGrphDomains(rs.getString("multiple_grph_domains"));
                categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
                categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
                categoryVO.setFixedRoles(rs.getString("fixed_roles"));
                categoryVO.setCategoryStatus(rs.getString("status"));
                categoryVO.setMultipleLoginAllowed(rs.getString("multiple_login_allowed"));
                categoryVO.setViewOnNetworkBlock(rs.getString("view_on_network_block"));
                categoryVO.setMaxLoginCount(rs.getInt("max_login_count"));
                categoryVO.setUserIdPrefix(rs.getString("user_id_prefix"));
                categoryVO.setServiceAllowed(rs.getString("services_allowed"));
                categoryVO.setDomainAllowed(rs.getString("domain_allowed"));
                categoryVO.setFixedDomains(rs.getString("fixed_domains"));
                categoryVO.setGrphDomainSequenceNo(rs.getInt("grph_sequence_no"));
                categoryVO.setProductTypeAllowed(rs.getString("product_types_allowed"));
                categoryVO.setMaxTxnMsisdnInt(rs.getInt("max_txn_msisdn"));
                categoryVO.setOutletsAllowed(rs.getString("outlets_allowed"));
                categoryVO.setGrphDomainTypeName(rs.getString("grph_domain_type_name"));
                categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
                categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));
                categoryVO.setCategoryType(rs.getString("category_type"));
                // Added for low balance alert
                categoryVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));

                categoryVO.setRechargeByParentOnly(rs.getString("c2s_payee_status"));
                categoryVO.setCp2pPayee(rs.getString("cp2p_payee_status"));
                categoryVO.setCp2pPayer(rs.getString("cp2p_payer_status"));
                categoryVO.setCp2pWithinList(rs.getString("cp2p_within_list"));
                categoryVO.setParentOrOwnerRadioValue(rs.getString("cp2p_within_list_level"));
                categoryVO.setAuthenticationType(rs.getString("authentication_type"));
                list.add(categoryVO);
            }

        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategorListByDomainCode]", "", "",
                    "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(ex.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategorListByDomainCode]", "", "",
                    "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: categoryList size : " + list.size());
                _log.debug(methodName, msg.toString());
            }
        }
        return list;
    }

    public boolean isExistsCategoryCodeForAddForSTK(Connection p_con, String p_categoryCode) throws BTSLBaseException {

        final String methodName = "isExistsCategoryCodeForAddForSTK";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: Category Code =");
            loggerValue.append(p_categoryCode);
            _log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM stk_profiles WHERE profile_code=?");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(selectQuery);
            _log.debug(methodName, loggerValue);
        }

        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_categoryCode.toUpperCase());
            rs = pstmtSelect.executeQuery();

            if (_log.isDebugEnabled()) {
                loggerValue.setLength(0);
                loggerValue.append("SQL select : ");
                loggerValue.append(selectQuery);
                _log.error(methodName, loggerValue);
            }

            if (rs.next()) {
                found = true;
            }
        }

        catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isExistsCategoryCodeForAdd]", "", "",
                    "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
        }

        catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isExistsCategoryCodeForAdd]", "", "",
                    "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
        }

        finally {
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: isExists found : " + found);
                _log.debug(methodName, msg.toString());
            }
        }

        return found;
    }

    public boolean isExistsCategoryCodeAndGatewayType(Connection p_con, String p_categoryCode, String gatewayType ) throws BTSLBaseException {

        final String methodName = "isExistsCategoryCodeAndGatewayType";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: Category Code =");
            loggerValue.append(p_categoryCode);
            _log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM category_req_gtw_types WHERE category_code=? and gateway_type=?");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(selectQuery);
            _log.debug(methodName, loggerValue);
        }

        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_categoryCode.toUpperCase());
            pstmtSelect.setString(2, gatewayType.toUpperCase());
            rs = pstmtSelect.executeQuery();

            if (_log.isDebugEnabled()) {
                loggerValue.setLength(0);
                loggerValue.append("SQL select : ");
                loggerValue.append(selectQuery);
                _log.error(methodName, loggerValue);
            }

            if (rs.next()) {
                found = true;
            }
        }

        catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isExistsCategoryCodeAndGatewayType]", "", "",
                    "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
        }

        catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isExistsCategoryCodeAndGatewayType]", "", "",
                    "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
        }

        finally {
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: isExists found : " + found);
                _log.debug(methodName, msg.toString());
            }
        }

        return found;
    }

    /**
     * This method is used before adding the record in the categories table
     * it will check for the uniqueness of the category_name column
     * if the user enterd the category code that exists in the database
     * the method will return true and record will not inserted in the
     * categories table.
     *
     * @param p_con
     *            Connection
     * @param p_categoryName
     *            java.lang.String
     * @return boolean
     * @throws BTSLBaseException
     */

    public boolean isExistsCategoryNameForAgentAdd(Connection p_con, String p_categoryName, String p_parentCategoryCode) throws BTSLBaseException {

        final String methodName = "isExistsCategoryNameForAdd";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: Category Name =");
            loggerValue.append(p_categoryName);
            _log.debug(methodName, loggerValue);
        }

        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM categories WHERE UPPER(category_name)=? AND category_code=?");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(selectQuery);
            _log.debug(methodName, loggerValue);
        }

        try {
            pstmtSelect = (PreparedStatement) p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_categoryName.toUpperCase());
            pstmtSelect.setString(2, p_parentCategoryCode.toUpperCase());
            rs = pstmtSelect.executeQuery();
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed::" + selectQuery);
            }

            if (rs.next()) {
                found = true;
            }
        }

        catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isExistsCategoryNameForAgentAdd]", "", "",
                    "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isExistsCategoryNameForAgentAdd", "error.general.sql.processing");
        }

        catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isExistsCategoryNameForAdd]", "", "",
                    "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: isExists found : " + found);
                _log.debug(methodName, msg.toString());
            }
        }

        return found;
    }

    /**
     * This method is used before adding the record in the categories table
     * it will check for the uniqueness of the user_id_prefix column
     * if the user enterd the useridprefix that exists in the database
     * the method will return true and record will not inserted in the
     * categories table.
     *
     * @param p_con
     *            Connection
     * @param p_userIdPrefix
     *            java.lang.String
     * @return boolean
     * @throws BTSLBaseException
     */

    public boolean isExistsUserIdPrefixForAgentAdd(Connection p_con, String p_userIdPrefix, String p_parentCategoryCode) throws BTSLBaseException {

        final String methodName = "isExistsUserIdPrefixForAgentAdd";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_userIdPrefix=");
            loggerValue.append(p_userIdPrefix);
            _log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM categories WHERE user_id_prefix=? AND category_code=?");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(selectQuery);
            _log.debug(methodName, loggerValue);
        }

        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_userIdPrefix.toUpperCase());
            pstmtSelect.setString(2, p_parentCategoryCode.toUpperCase());
            rs = pstmtSelect.executeQuery();

            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query Executed::" + selectQuery);
            }

            if (rs.next()) {
                found = true;
            }
        }

        catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isExistsUserIdPrefixForAgentAdd]", "", "",
                    "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isExistsUserIdPrefixForAgentAdd]", "", "",
                    "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isExistsUserIdPrefixForAgentAdd", "error.general.processing");
        }

        finally {
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: isExists found : " + found);
                _log.debug(methodName, msg.toString());
            }
        }

        return found;
    }

    public ArrayList loadCategoryListForGradmanagement(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadCategoryListForGradmanagement";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: p_fixedRoles=");
            _log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer();

        strBuff.append(" SELECT c.category_code,c.category_name,");
        strBuff.append("c.domain_code,c.sequence_no,c.grph_domain_type, ");
        strBuff.append("c.multiple_grph_domains,c.web_interface_allowed,c.sms_interface_allowed, ");
        strBuff.append("c.fixed_roles,c.status,c.multiple_login_allowed, ");
        strBuff.append("c.view_on_network_block,c.max_login_count,c.user_id_prefix,c.product_types_allowed,c.services_allowed,");
        strBuff.append("c.domain_allowed,c.fixed_domains,c.max_txn_msisdn,c.outlets_allowed ");
        strBuff.append(" FROM categories C,domains D ,LOOKUPS LK WHERE D.domain_code=C.domain_code");
        strBuff.append(" AND C.status <>? AND C.display_allowed=? AND C.modify_allowed=? AND LK.lookup_code=C.category_type AND LK.lookup_type=? ORDER BY C.sequence_no");

        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(sqlSelect);
            _log.debug(methodName, loggerValue);
        }
        final ArrayList list = new ArrayList();
        try {
            pstmt = p_con.prepareStatement(sqlSelect);

            pstmt.setString(1, PretupsI.CATEGORY_STATUS_DELETE);
            pstmt.setString(2, PretupsI.CATEGORY_DISPLAY_ALLOWED);
            pstmt.setString(3, PretupsI.CATEGORY_MODIFY_ALLOWED);
            pstmt.setString(4, PretupsI.CATEGORY_TYPE_CODE);
            rs = pstmt.executeQuery();
            CategoryVO categoryVO = null;
            while (rs.next()) {
                categoryVO = new CategoryVO();
                categoryVO.setCategoryCode(rs.getString("category_code"));
                categoryVO.setCategoryName(rs.getString("category_name"));
                categoryVO.setDomainCodeforCategory(rs.getString("domain_code"));
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                categoryVO.setGrphDomainType(rs.getString("grph_domain_type"));
                categoryVO.setMultipleGrphDomains(rs.getString("multiple_grph_domains"));
                categoryVO.setWebInterfaceAllowed(rs.getString("web_interface_allowed"));
                categoryVO.setSmsInterfaceAllowed(rs.getString("sms_interface_allowed"));
                categoryVO.setFixedRoles(rs.getString("fixed_roles"));
                categoryVO.setCategoryStatus(rs.getString("status"));
                categoryVO.setMultipleLoginAllowed(rs.getString("multiple_login_allowed"));
                categoryVO.setViewOnNetworkBlock(rs.getString("view_on_network_block"));
                categoryVO.setMaxLoginCount(rs.getInt("max_login_count"));
                categoryVO.setUserIdPrefix(rs.getString("user_id_prefix"));
                //              categoryVO.setGrphDomainSequenceNo(rs.getInt("grph_sequence_no"));
                categoryVO.setProductTypeAllowed(rs.getString("product_types_allowed"));
                categoryVO.setServiceAllowed(rs.getString("services_allowed"));
                categoryVO.setDomainAllowed(rs.getString("domain_allowed"));
                categoryVO.setFixedDomains(rs.getString("fixed_domains"));
                categoryVO.setOutletsAllowed(rs.getString("outlets_allowed"));
                categoryVO.setMaxTxnMsisdnInt(rs.getInt("max_txn_msisdn"));
                list.add(categoryVO);
            }

        } catch (SQLException sqe) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqe.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryListForGroupRole]", "", "",
                    "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
        } catch (Exception ex) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(ex.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[loadCategoryListForGroupRole]", "", "",
                    "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception e) {
                _log.errorTrace(methodName, e);
            }

            if (_log.isDebugEnabled()) {
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: categoryList size : " + list.size());
                _log.debug(methodName, msg.toString());
            }
        }
        return list;
    }

    /**
     * Method isGradeExistsForCategory
     * This method is used before deleting the category.It will check if any
     * Grade is associated with the category if yes it will return true
     *
     * @param p_con
     *            Connection
     * @param p_categoryVO
     *            CategoryVO
     * @return boolean
     * @throws BTSLBaseException
     */
    public boolean isGradeExistsForCategory(Connection p_con, CategoryVO p_categoryVO) throws BTSLBaseException {

        final String methodName = "isGradeExistsForCategory";
        StringBuilder loggerValue= new StringBuilder();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Entered: categoryCode=");
            loggerValue.append(p_categoryVO.getCategoryCode());
            _log.debug(methodName, loggerValue);
        }
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        boolean found = false;
        final StringBuffer sqlBuff = new StringBuffer("SELECT * FROM categories c JOIN channel_grades cg ON c.category_code = cg.category_code WHERE c.category_code = ? and cg.status ='Y'");
        final String selectQuery = sqlBuff.toString();
        if (_log.isDebugEnabled()) {
            loggerValue.setLength(0);
            loggerValue.append("Select Query =");
            loggerValue.append(selectQuery);
            _log.debug(methodName, loggerValue);
        }
        try {
            pstmtSelect = p_con.prepareStatement(selectQuery);
            pstmtSelect.setString(1, p_categoryVO.getCategoryCode());
            rs = pstmtSelect.executeQuery();
            if (rs.next()) {
                found = true;
            }
        } catch (SQLException sqle) {
            loggerValue.setLength(0);
            loggerValue.append("SQL Exception : ");
            loggerValue.append(sqle.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isGradeExistsForCategory]",
                    "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            loggerValue.setLength(0);
            loggerValue.append(" Exception : ");
            loggerValue.append(e.getMessage());
            _log.error(methodName, loggerValue);
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryWebDAO[isGradeExistsForCategory]",
                    "", "", "", "Exception:" + e.getMessage());
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
                StringBuffer msg=new StringBuffer("");
                msg.append("Exiting: isExists found : " + found);
                _log.debug(methodName, msg.toString());
            }
        }
        return found;
    }

}

package com.btsl.pretups.domain.businesslogic;

/*
 * @# CategoryDAO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * Aug 5, 2005 Amit Ruwali Initial creation
 * Aug 22,2005 Manoj Kumar Modified
 * Aug 10,2006 Sandeep Goel Modification ID CAT001
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CategoryData;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.SqlParameterEncoder;
/**
 * Class CategoryDAO
 */
public class CategoryDAO {

    /**
     * Field log.
     */
    private Log log = LogFactory.getFactory().getInstance(CategoryDAO.class.getName());

    /**
     * Constructor for CategoryDAO.
     */
    public CategoryDAO() {
        super();
    }

    /************************ Methods added by Mohit for Users **************** */

    /*
     * @param p_con Connection
     * 
     * @param p_domainCode String
     * 
     * @param p_lookType String
     * 
     * @param p_con Connection
     * 
     * @param p_domainCode String
     * 
     * @param p_lookType String
     * 
     * @return ArrayList
     * 
     * @throws BTSLBaseException
     */

    /**
     * Method for loading category List.
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
    public ArrayList loadCategoryList(Connection p_con, String p_domainCode, String p_lookType) throws BTSLBaseException {
        final String methodName = "loadCategoryList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_domainCode=" + p_domainCode + " p_lookType=" + p_lookType);
        }
        
        StringBuilder strBuff = new StringBuilder();
        strBuff.append(" SELECT c.category_code,c.category_name,");
        strBuff.append("c.domain_code,c.sequence_no,c.grph_domain_type, ");
        strBuff.append("c.multiple_grph_domains,c.web_interface_allowed,c.sms_interface_allowed, ");
        strBuff.append("c.fixed_roles,c.status,c.multiple_login_allowed, ");
        strBuff.append("c.view_on_network_block,c.max_login_count,c.user_id_prefix,c.product_types_allowed,");
        strBuff.append("c.services_allowed,c.domain_allowed,c.fixed_domains,c.outlets_allowed,c.max_txn_msisdn,");
        strBuff.append(" gdt.grph_domain_type_name,gdt.sequence_no grph_sequence_no,c.authentication_type ");
        strBuff.append("FROM categories c,lookups l, geographical_domain_types gdt WHERE c.domain_code = ? and l.lookup_type = ? ");
        strBuff.append(" AND l.lookup_code = c.category_code and c.status <> 'N' and l.status='Y' ");
        strBuff.append(" AND gdt.grph_domain_type = c.grph_domain_type ORDER BY c.sequence_no");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY SqlSelect =" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
           
            pstmt.setString(1, p_domainCode);
            pstmt.setString(2, p_lookType);
            try(ResultSet rs = pstmt.executeQuery();)
            {
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
                categoryVO.setAuthenticationType(rs.getString("authentication_type"));
                list.add(categoryVO);
            }
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryDAO[loadCategoryList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryDAO[loadCategoryList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: categoryList size=" + list.size());
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
     * @param p_domainCode
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadOtherCategorList(Connection p_con, String p_domainCode) throws BTSLBaseException {
        final String methodName = "loadOtherCategorList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_domainCode=" + p_domainCode);
        }
        
        StringBuilder strBuff = new StringBuilder();

        strBuff.append(" SELECT c.category_code,c.category_name,");
        strBuff.append("c.domain_code,c.sequence_no,c.grph_domain_type, ");
        strBuff.append("c.multiple_grph_domains,c.web_interface_allowed,c.sms_interface_allowed, ");
        strBuff.append("c.fixed_roles,c.status,c.multiple_login_allowed, c.transfertolistonly, ");
        strBuff.append("c.view_on_network_block,c.max_login_count,c.user_id_prefix,c.product_types_allowed,c.services_allowed,");
        strBuff.append("c.domain_allowed,c.fixed_domains,c.max_txn_msisdn,c.outlets_allowed,c.restricted_msisdns, ");
        strBuff.append(" gdt.grph_domain_type_name,gdt.sequence_no grph_sequence_no,c.authentication_type, ");
        // add
        strBuff.append(" c.cp2p_payee_status,c.cp2p_payer_status,c.c2s_payee_status ,c.cp2p_within_list,c.cp2p_within_list_level, ");
        // for Zebra and Tango by Sanjeew date 11/07/07
        strBuff.append(" c.low_bal_alert_allow ");
        // End Zebra and Tango

        strBuff.append("FROM categories c,geographical_domain_types gdt WHERE c.status <> 'N' AND c.domain_code != ? ");
        strBuff.append("AND gdt.grph_domain_type = c.grph_domain_type ORDER BY c.sequence_no");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
           
            pstmt.setString(1, p_domainCode);

            try(ResultSet rs = pstmt.executeQuery();)
            {
            CategoryVO categoryVO = null;
            while (rs.next()) {
                categoryVO = new CategoryVO();
                categoryVO.setCategoryCode(SqlParameterEncoder.encodeParams(rs.getString("category_code")));
                categoryVO.setCategoryName(SqlParameterEncoder.encodeParams(rs.getString("category_name")));
                categoryVO.setDomainCodeforCategory(SqlParameterEncoder.encodeParams(rs.getString("domain_code")));
                categoryVO.setSequenceNumber(rs.getInt("sequence_no"));
                categoryVO.setGrphDomainType(SqlParameterEncoder.encodeParams(rs.getString("grph_domain_type")));
                categoryVO.setMultipleGrphDomains(SqlParameterEncoder.encodeParams(rs.getString("multiple_grph_domains")));
                categoryVO.setWebInterfaceAllowed(SqlParameterEncoder.encodeParams(rs.getString("web_interface_allowed")));
                categoryVO.setSmsInterfaceAllowed(SqlParameterEncoder.encodeParams(rs.getString("sms_interface_allowed")));
                categoryVO.setFixedRoles(SqlParameterEncoder.encodeParams(rs.getString("fixed_roles")));
                categoryVO.setCategoryStatus(SqlParameterEncoder.encodeParams(rs.getString("status")));
                categoryVO.setMultipleLoginAllowed(SqlParameterEncoder.encodeParams(rs.getString("multiple_login_allowed")));
                categoryVO.setViewOnNetworkBlock(SqlParameterEncoder.encodeParams(rs.getString("view_on_network_block")));
                categoryVO.setMaxLoginCount(rs.getInt("max_login_count"));
                categoryVO.setUserIdPrefix(SqlParameterEncoder.encodeParams(rs.getString("user_id_prefix")));
                categoryVO.setGrphDomainSequenceNo(rs.getInt("grph_sequence_no"));
                categoryVO.setProductTypeAllowed(SqlParameterEncoder.encodeParams(rs.getString("product_types_allowed")));
                categoryVO.setServiceAllowed(SqlParameterEncoder.encodeParams(rs.getString("services_allowed")));
                categoryVO.setDomainAllowed(SqlParameterEncoder.encodeParams(rs.getString("domain_allowed")));
                categoryVO.setFixedDomains(SqlParameterEncoder.encodeParams(rs.getString("fixed_domains")));
                categoryVO.setOutletsAllowed(SqlParameterEncoder.encodeParams(rs.getString("outlets_allowed")));
                categoryVO.setMaxTxnMsisdnInt(rs.getInt("max_txn_msisdn"));
                categoryVO.setGrphDomainTypeName(SqlParameterEncoder.encodeParams(rs.getString("grph_domain_type_name")));
                categoryVO.setRestrictedMsisdns(SqlParameterEncoder.encodeParams(rs.getString("restricted_msisdns")));
                categoryVO.setTransferToListOnly(SqlParameterEncoder.encodeParams(rs.getString("transfertolistonly")));

                // for Zebra and Tango by Sanjeew date 11/07/07
                categoryVO.setLowBalAlertAllow(SqlParameterEncoder.encodeParams(rs.getString("low_bal_alert_allow")));

                categoryVO.setRechargeByParentOnly(SqlParameterEncoder.encodeParams(rs.getString("c2s_payee_status")));
                categoryVO.setCp2pPayee(SqlParameterEncoder.encodeParams(rs.getString("cp2p_payee_status")));
                categoryVO.setCp2pPayer(SqlParameterEncoder.encodeParams(rs.getString("cp2p_payer_status")));
                categoryVO.setCp2pWithinList(SqlParameterEncoder.encodeParams(rs.getString("cp2p_within_list")));
                categoryVO.setParentOrOwnerRadioValue(SqlParameterEncoder.encodeParams(rs.getString("cp2p_within_list_level")));
                // Added for Authentication Type
                categoryVO.setAuthenticationType(SqlParameterEncoder.encodeParams(rs.getString("authentication_type")));
                // End Zebra and Tango
                list.add(categoryVO);

                 }

        } 
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryDAO[loadOtherCategorList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryDAO[loadOtherCategorList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: categoryList size=" + list.size());
            }
        }
        return list;
    }
    
    

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

    public ArrayList loadCategoryDetailsUsingCategoryCode(Connection p_con, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "loadCategoryDetailsUsingCategoryCode";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_categoryCode=" + p_categoryCode);
        }

         
        ArrayList categoryList = new ArrayList();
        StringBuilder strBuff = new StringBuilder(" SELECT D.domain_name,D.domain_type_code,C.modify_allowed,C.category_code,");
        strBuff.append("C.category_name,C.domain_code,C.sequence_no,C.grph_domain_type,C.multiple_grph_domains,");
        strBuff.append("C.web_interface_allowed,C.sms_interface_allowed,C.fixed_roles,C.multiple_login_allowed,");
        strBuff.append("C.view_on_network_block,C.max_login_count,C.status,C.created_on,C.created_by,C.modified_on,");
        strBuff.append("C.modified_by,C.display_allowed,C.modify_allowed,C.product_types_allowed,C.services_allowed,");
        strBuff.append("C.domain_allowed,C.fixed_domains,C.max_txn_msisdn,C.uncntrl_transfer_allowed, C.transfertolistonly,C.authentication_type, ");
        // Added on 13/07/07 for Low balance alert allow
        strBuff.append("C.low_bal_alert_allow,");
        // End of low balance alert allow
        strBuff.append("C.cp2p_payee_status,C.cp2p_payer_status,C.c2s_payee_status ,C.cp2p_within_list,C.cp2p_within_list_level,");
        strBuff.append("C.scheduled_transfer_allowed,C.restricted_msisdns,C.parent_category_code,C.user_id_prefix,C.outlets_allowed ,C.category_type,C.agent_allowed,C.hierarchy_allowed,LK.lookup_name");
        strBuff.append(" FROM categories C, Domains D ,LOOKUPS LK WHERE C.category_code=? AND C.domain_code=D.domain_Code ");
        strBuff.append(" AND C.status <>? AND C.display_allowed=? AND LK.lookup_code=C.category_type AND LK.lookup_type=? ");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Select Query= " + sqlSelect);
        }

        try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
           
            pstmtSelect.setString(1, p_categoryCode);
            pstmtSelect.setString(2, PretupsI.CATEGORY_STATUS_DELETE);
            pstmtSelect.setString(3, PretupsI.CATEGORY_DISPLAY_ALLOWED);
            pstmtSelect.setString(4, PretupsI.CATEGORY_TYPE_CODE);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
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
                categoryVO.setCategoryType(rs.getString("lookup_name"));
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
        }

        catch (SQLException sqe) {
            log.error(methodName, "SQL Exception" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[loadCategoryDetailsUsingCategoryCode]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, " Exception" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[loadCategoryDetailsUsingCategoryCode]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");

        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting size=" + categoryList.size());
            }
        }

        return categoryList;
    }

    /**
     * Method loadCategoryHierarchyList
     * This method is used to load Categories on the basis of domain
     * code and sequence no. of logged-in user
     * 
     * @author Amit Singh
     * @param p_con
     *            Connection
     * @param p_domainCode
     *            String
     * @param p_sequenceNo
     *            int
     * @return list ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadCategoryHierarchyList(Connection p_con, String p_domainCode, int p_sequenceNo) throws BTSLBaseException {
        final String methodName = "loadCategoryHierarchyList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_domainCode = " + p_domainCode + ", p_sequenceNo = " + p_sequenceNo);
        }

         
        StringBuilder strBuff = new StringBuilder(" SELECT C.category_code, C.category_name, C.domain_code, C.sequence_no ");
        strBuff.append("FROM categories C ");
        strBuff.append("WHERE C.domain_code=? AND C.status <> 'N' AND C.display_allowed='Y' ");
        strBuff.append("AND C.sequence_no>= ? ORDER BY C.sequence_no");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        ArrayList list = new ArrayList();
        try(PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
           
            pstmt.setString(1, p_domainCode);
            pstmt.setInt(2, p_sequenceNo);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                list.add(new ListValueVO(rs.getString("category_name"), rs.getString("category_code")));
            }

        } 
        }catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryDAO[loadCategoryHierarchyList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryDAO[loadCategoryHierarchyList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: categoryList size=" + list.size());
            }
        }
        return list;
    }// end of loadCategoryHierarchyList

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

    public CategoryVO loadCategoryDetailsByCategoryCode(Connection p_con, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "loadCategoryDetailsByCategoryCode";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_categoryCode=" + p_categoryCode);
        }

        
        CategoryVO categoryVO = null;
       
        StringBuilder strBuff = new StringBuilder(" SELECT D.domain_name,D.domain_type_code,C.modify_allowed,C.category_code,");
        strBuff.append("C.category_name,C.domain_code,C.sequence_no,C.grph_domain_type,C.multiple_grph_domains,");
        strBuff.append("C.web_interface_allowed,C.sms_interface_allowed,C.fixed_roles,C.multiple_login_allowed,");
        strBuff.append("C.view_on_network_block,C.max_login_count,C.status,C.created_on,C.created_by,C.modified_on,");
        strBuff.append("C.modified_by,C.display_allowed,C.modify_allowed,C.product_types_allowed,C.services_allowed,");
        strBuff.append("C.domain_allowed,C.fixed_domains,C.max_txn_msisdn,C.uncntrl_transfer_allowed, C.transfertolistonly, ");
        // Added on 13/07/07 for Low balance alert allow
        strBuff.append("C.low_bal_alert_allow,");
        // End of low balance alert allow
        strBuff.append("C.cp2p_payee_status,C.cp2p_payer_status,C.c2s_payee_status ,C.cp2p_within_list,C.cp2p_within_list_level,");
        strBuff.append("C.scheduled_transfer_allowed,C.restricted_msisdns,C.parent_category_code,C.user_id_prefix,C.outlets_allowed ,C.category_type,C.agent_allowed,C.hierarchy_allowed,LK.lookup_name,C.authentication_type");
        strBuff.append(" FROM categories C, Domains D ,LOOKUPS LK WHERE C.category_code=? AND C.domain_code=D.domain_Code ");
        strBuff.append(" AND C.status <> ? AND LK.lookup_code=C.category_type AND LK.lookup_type=? ");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Select Query= " + sqlSelect);
        }

        try(  PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
          
            pstmtSelect.setString(1, p_categoryCode);
            pstmtSelect.setString(2, PretupsI.CATEGORY_STATUS_DELETE);
            pstmtSelect.setString(3, PretupsI.CATEGORY_TYPE_CODE);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
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
                // End of Low balance alert allow
                categoryVO.setRechargeByParentOnly(rs.getString("c2s_payee_status"));
                categoryVO.setCp2pPayee(rs.getString("cp2p_payee_status"));
                categoryVO.setCp2pPayer(rs.getString("cp2p_payer_status"));
                categoryVO.setCp2pWithinList(rs.getString("cp2p_within_list"));
                categoryVO.setParentOrOwnerRadioValue(rs.getString("cp2p_within_list_level"));
                // added for Authentication Type
                categoryVO.setAuthenticationType(rs.getString("authentication_type"));
            }
        }
        }

        catch (SQLException sqe) {
            log.error(methodName, "SQL Exception" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[loadCategoryDetailsUsingCategoryCode]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, " Exception" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[loadCategoryDetailsUsingCategoryCode]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");

        } finally {
        	
           
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting ");
            }
        }

        return categoryVO;
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
     * @param p_domainCode
     *            String
     * @return java.util.ArrayList
     * @throws BTSLBaseException
     */
    public List<CategoryVO> loadAllCategoryOfDomains(Connection p_con, List<ListValueVO> domainList) throws BTSLBaseException {
        final String methodName = "loadOtherCategorList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_domainCode = " + domainList);
        }
        
        StringBuilder inClause = new StringBuilder();
        for(int i=0;i<domainList.size(); i++){
        	ListValueVO vo = (ListValueVO)domainList.get(i);
        //	inClause.append("'").append(vo.getValue()).append("'").append(",");
        	inClause.append("?").append(",");
        }
        inClause.deleteCharAt(inClause.length()-1);   
        
        
        StringBuilder strBuff = new StringBuilder("SELECT c.category_code,c.category_name,");
        strBuff.append("c.domain_code,c.sequence_no,c.grph_domain_type, ");
        strBuff.append("c.multiple_grph_domains,c.web_interface_allowed,c.sms_interface_allowed, ");
        strBuff.append("c.fixed_roles,c.status,c.multiple_login_allowed, c.transfertolistonly, ");
        strBuff.append("c.view_on_network_block,c.max_login_count,c.user_id_prefix,c.product_types_allowed,c.services_allowed,");
        strBuff.append("c.domain_allowed,c.fixed_domains,c.max_txn_msisdn,c.outlets_allowed,c.restricted_msisdns, ");
        strBuff.append(" gdt.grph_domain_type_name,gdt.sequence_no grph_sequence_no,c.authentication_type, ");
        strBuff.append(" c.cp2p_payee_status,c.cp2p_payer_status,c.c2s_payee_status ,c.cp2p_within_list,c.cp2p_within_list_level, ");// for Zebra and Tango by Sanjeew date 11/07/07
        strBuff.append(" c.low_bal_alert_allow,c.uncntrl_transfer_allowed ");// End Zebra and Tango
        strBuff.append("FROM categories c,geographical_domain_types gdt WHERE c.status <> 'N' AND c.domain_code in ("+inClause.toString()+") ");
        strBuff.append("AND gdt.grph_domain_type = c.grph_domain_type ORDER BY c.sequence_no");
        final String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        List<CategoryVO> list = new ArrayList<CategoryVO>(); 
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
        	 pstmt = p_con.prepareStatement(sqlSelect);
        		
        		
        		
        		 for(int i=0;i<domainList.size(); i++){
        	        	ListValueVO vo = (ListValueVO)domainList.get(i);
        	        	pstmt.setString((i+1), vo.getValue());
        	        }
        		 
        		 
        		
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
                categoryVO.setGrphDomainTypeName(rs.getString("grph_domain_type_name"));
                categoryVO.setRestrictedMsisdns(rs.getString("restricted_msisdns"));
                categoryVO.setTransferToListOnly(rs.getString("transfertolistonly"));// for Zebra and Tango by Sanjeew date 11/07/07
                categoryVO.setLowBalAlertAllow(rs.getString("low_bal_alert_allow"));
                categoryVO.setRechargeByParentOnly(rs.getString("c2s_payee_status"));
                categoryVO.setCp2pPayee(rs.getString("cp2p_payee_status"));
                categoryVO.setCp2pPayer(rs.getString("cp2p_payer_status"));
                categoryVO.setCp2pWithinList(rs.getString("cp2p_within_list"));
                categoryVO.setParentOrOwnerRadioValue(rs.getString("cp2p_within_list_level")); // Added for Authentication Type
                categoryVO.setAuthenticationType(rs.getString("authentication_type"));// End Zebra and Tango
                categoryVO.setUnctrlTransferAllowed(rs.getString("uncntrl_transfer_allowed"));
                list.add(categoryVO); 
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryDAO[loadOtherCategorList]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "CategoryDAO[loadOtherCategorList]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	try{rs.close();
        	
        	pstmt.close();
        	}catch(Exception e) {log.debug(methodName, "Failed to close Preparedstatement and resultset=" + e);}
        	
           
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: categoryList size=" + list.size());
            }
        }
        return list;
    }
    /**
	 * Method loadCategoryList. This method loads categories list by checking
	 * the condition that "p_category must be the TO_CATEGORY in any of the
	 * transfer rule and must have ASSOCIATION_TRANSFER_ALLOWED='Y' then loads
	 * the categories having the FROM_CATEGORY as the category code"
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
	public ArrayList loadTransferRulesCategoryListXml(Connection p_con,
			String p_networkCode, String p_category) throws BTSLBaseException {
		final String methodName = "loadTransferRulesCategoryList";
		if (log.isDebugEnabled())
            log.debug(methodName, "Entered p_networkCode=" + p_networkCode
                    + ", p_category=" + p_category);

        
        ArrayList categoryListVO = new ArrayList();
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("SELECT C.category_code,C.category_name,C.sequence_no ");
        strBuff.append("FROM categories C ,chnl_transfer_rules CTR ");
		strBuff.append("WHERE CTR.network_code=? AND C.status=? AND C.display_allowed=? ");
		strBuff.append("AND CTR.to_category =? AND CTR.parent_association_allowed='Y' ");
		strBuff.append("AND CTR.status= ? AND CTR.from_category=C.category_code ");
		strBuff.append("ORDER BY C.sequence_no");

		String sqlSelect = strBuff.toString();
		if (log.isDebugEnabled())
			log.debug(methodName, "Select Query= " + sqlSelect);

		try (PreparedStatement  pstmtSelect = p_con.prepareStatement(sqlSelect);){
			
			int i = 1;
			pstmtSelect.setString(i++, p_networkCode);
			pstmtSelect.setString(i++, PretupsI.CATEGORY_STATUS_ACTIVE);
			pstmtSelect.setString(i++, PretupsI.CATEGORY_DISPLAY_ALLOWED);
			pstmtSelect.setString(i++, p_category);
			pstmtSelect.setString(i++, PretupsI.TRANSFER_RULE_STATUS_ACTIVE);
			try(ResultSet rs = pstmtSelect.executeQuery();)
			{
			while (rs.next())
				categoryListVO.add(rs.getString("category_code"));
		} 
		}catch (SQLException sqe) {
			log.error(methodName, "SQL Exception" + sqe.getMessage());
			log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"categoryDAO[loadTransferRulesCategoryList]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		} catch (Exception e) {
			log.error(methodName, " Exception" + e.getMessage());
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"categoryDAO[loadTransferRulesCategoryList]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");

		} finally {
			
			
			if (log.isDebugEnabled())
				log.debug(methodName, "Exiting size=" + categoryListVO.size());
		}
		return categoryListVO;
	}
    
    public int loadSequenceNo(Connection con,String categoryCode, String domainCode) throws BTSLBaseException {
        int seq_no = 0;
        final String methodName = "loadSequenceNo";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered categoryCode=" + categoryCode + ", domainCode=" + domainCode);

        
         
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("select sequence_no from categories where category_code = ? and domain_code = ? and Status=? ");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled())
            log.debug(methodName, "Select Query= " + sqlSelect);
        try (PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);){
            
            int i = 1;
            pstmtSelect.setString(i++, categoryCode);
            pstmtSelect.setString(i++, domainCode);
            pstmtSelect.setString(i++, PretupsI.CATEGORY_STATUS_ACTIVE);
           
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next())
                seq_no = rs.getInt(1);
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQL Exception" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
                    EventStatusI.RAISED, EventLevelI.FATAL,
                    "categoryDAO[loadSequenceNo]", "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName,
                    "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, " Exception" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
                    EventStatusI.RAISED, EventLevelI.FATAL,
                    "categoryDAO[loadSequenceNo]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName,
                    "error.general.processing");

        } finally {
            
            
            if (log.isDebugEnabled())
                log.debug(methodName, "Exiting Seq_no=" + seq_no);
        }
        return seq_no;
    }
	
	/**
	 * Method loadCategoryReportList.
	 * This method load all .
	 * @author sandeep.goel
	 * @param p_con Connection
	 * @param p_leafRequired boolean
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public ArrayList loadCategoryReportList(Connection p_con)throws BTSLBaseException 
	{
		final String methodName = "loadCategoryReportList";
 		if (log.isDebugEnabled())
 		log.debug(methodName,"Entered ");
   		
   		PreparedStatement pstmtSelect=null;
   		ResultSet rs=null;
   		ArrayList categoryListVO = new ArrayList();
		StringBuffer strBuff= new StringBuffer();
		strBuff.append("SELECT domain_code,category_code,category_name,sequence_no ");
		strBuff.append("FROM categories ");
		strBuff.append("WHERE display_allowed=? AND status=? ");		
		strBuff.append("ORDER BY domain_code,sequence_no,category_name ");
		String sqlSelect=strBuff.toString();  
		if (log.isDebugEnabled()) 
		    log.debug(methodName,"Select Query= "+sqlSelect);
		
	    try
		{
	        pstmtSelect=p_con.prepareStatement(sqlSelect);
	        pstmtSelect.setString(1,PretupsI.CATEGORY_DISPLAY_ALLOWED);	
	        pstmtSelect.setString(2,TypesI.YES);		
	        rs=pstmtSelect.executeQuery();

			while(rs.next())
	            categoryListVO.add(new ListValueVO(rs.getString("category_name"),rs.getString("domain_code")+":"+rs.getString("sequence_no")+"|" + rs.getString("category_code")));
 		}
		catch(SQLException sqe)
 		{
	        log.error(methodName,"SQL Exception"+sqe.getMessage());
 			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"categoryDAO[loadCategoryReportList]","","","","SQL Exception:"+sqe.getMessage());
 			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
 		}
 		catch(Exception e)
 		{
 	       log.error(methodName," Exception"+e.getMessage());
 			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"categoryDAO[loadCategoryReportList]","","","","Exception:"+e.getMessage());
 			throw new BTSLBaseException(this, methodName, "error.general.processing");
 			
 		}
 		finally
 		{
 			try { if(rs!=null) rs.close(); } catch(Exception ex){}
 			try { if(pstmtSelect !=null) pstmtSelect.close(); } catch(Exception ex){}
 			if (log.isDebugEnabled())
 			log.debug(methodName,"Exiting size="+ categoryListVO.size());
 		}
 		return categoryListVO;
	}
	
	/**
	 * This method is same as that of loadMessageGatewayTypeList but only store the Gateway Type in ArrayList
	 * @param p_con
	 * @param p_categoryCode
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public ArrayList loadMessageGatewayTypeListForCategory(Connection p_con,String p_categoryCode)	throws BTSLBaseException
	{
		final String methodName = "loadMessageGatewayTypeListForCategory";
		if (log.isDebugEnabled()) 	log.debug(methodName, "Entered: with p_categoryCode="+p_categoryCode);
		PreparedStatement pstmtSelect = null;
		ResultSet rs = null;
		ArrayList messageGatewayTypeList=null;
		try
		{
			StringBuffer selectQuery = new StringBuffer(" SELECT gateway_type  ");
			selectQuery.append(" FROM category_req_gtw_types  ");
			selectQuery.append(" WHERE category_code=? ");
			if (log.isDebugEnabled())
				log.debug(methodName, "Query=" + selectQuery);

			pstmtSelect = p_con.prepareStatement(selectQuery.toString());
			pstmtSelect.setString(1,p_categoryCode);
			rs = pstmtSelect.executeQuery();
			messageGatewayTypeList=new ArrayList(); 
			while (rs.next())
			{
				messageGatewayTypeList.add(rs.getString("gateway_type"));
			}
		}
		catch (SQLException sqe)
		{
			log.error(methodName, "SQLException:"+ sqe.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"CategoryDAO[loadMessageGatewayTypeListForCategory]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName,"error.general.sql.processing");
		}
		catch (Exception e)
		{
			log.error(methodName, "Exception:"+ e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"CategoryDAO[loadMessageGatewayTypeListForCategory]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, methodName,"error.general.processing");
		}
		finally
		{
			try{if (rs != null)rs.close();}catch (Exception ex){}
			try{if (pstmtSelect != null)pstmtSelect.close();}catch (Exception ex){}
			if (log.isDebugEnabled())
				log.debug(methodName, "Exiting:messageGatewayTypeList size="+ messageGatewayTypeList.size());
		}
		return messageGatewayTypeList;
	}
	
	
	/**
	 * This method is same as that of loadMessageGatewayTypeList but only store the Gateway Type in ArrayList
	 * @param p_con
	 * @param p_categoryCode
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public ArrayList<CategoryData> loadCategoryHierarchyUnderCategory(Connection p_con,String p_categoryCode,String domainCode)	throws BTSLBaseException
	{
		final String methodName = "loadCategoryHierarchyUnderCategory";
		if (log.isDebugEnabled()) 	log.debug(methodName, "Entered: with p_categoryCode="+p_categoryCode);
		PreparedStatement pstmtSelect = null;
		ResultSet rs = null;
		ArrayList categoryList=null;
		try
		{
			StringBuffer selectQuery = new StringBuffer("");
			selectQuery.append(" select category_code, category_name from categories where SEQUENCE_NO >=  ");
			selectQuery.append(" (select sequence_no from categories where category_code =? ) ");
			selectQuery.append("  AND DOMAIN_CODE =? ORDER BY SEQUENCE_NO");
			if (log.isDebugEnabled())
				log.debug(methodName, "Query=" + selectQuery);

			pstmtSelect = p_con.prepareStatement(selectQuery.toString());
			pstmtSelect.setString(1,p_categoryCode);
			pstmtSelect.setString(2,domainCode);
			rs = pstmtSelect.executeQuery();
			categoryList=new ArrayList(); 
			CategoryData  categoryData  = new CategoryData();
			while (rs.next())
			{
				categoryData= new CategoryData();
				categoryData.setCategoryCode(rs.getString("category_code"));
				categoryData.setCategoryName(rs.getString("category_name"));
				categoryList.add(categoryData);
			}
		}
		catch (SQLException sqe)
		{
			log.error(methodName, "SQLException:"+ sqe.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"CategoryDAO[loadCategoryHierarchyUnderCategory]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName,"error.general.sql.processing");
		}
		catch (Exception e)
		{
			log.error(methodName, "Exception:"+ e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"CategoryDAO[loadCategoryHierarchyUnderCategory]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, methodName,"error.general.processing");
		}
		finally
		{
			try{if (rs != null)rs.close();}catch (Exception ex){}
			try{if (pstmtSelect != null)pstmtSelect.close();}catch (Exception ex){}
			if (log.isDebugEnabled())
				log.debug(methodName, "Exiting:messageGatewayTypeList size="+ categoryList.size());
		}
		return categoryList;
	}

	
	
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

    public ArrayList loadCategoryDetailsOPTCategoryCode(Connection p_con, String p_categoryCode) throws BTSLBaseException {
        final String methodName = "loadCategoryDetailsOPTCategoryCode";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_categoryCode=" + p_categoryCode);
        }

         
        ArrayList categoryList = new ArrayList();
        StringBuilder strBuff = new StringBuilder(" SELECT D.domain_name,D.domain_type_code,C.modify_allowed,C.category_code,");
        strBuff.append("C.category_name,C.domain_code,C.sequence_no,C.grph_domain_type,C.multiple_grph_domains,");
        strBuff.append("C.web_interface_allowed,C.sms_interface_allowed,C.fixed_roles,C.multiple_login_allowed,");
        strBuff.append("C.view_on_network_block,C.max_login_count,C.status,C.created_on,C.created_by,C.modified_on,");
        strBuff.append("C.modified_by,C.display_allowed,C.modify_allowed,C.product_types_allowed,C.services_allowed,");
        strBuff.append("C.domain_allowed,C.fixed_domains,C.max_txn_msisdn,C.uncntrl_transfer_allowed, C.transfertolistonly,C.authentication_type, ");
        // Added on 13/07/07 for Low balance alert allow21	
        strBuff.append("C.low_bal_alert_allow,");
        // End of low balance alert allow
        strBuff.append("C.cp2p_payee_status,C.cp2p_payer_status,C.c2s_payee_status ,C.cp2p_within_list,C.cp2p_within_list_level,");
        strBuff.append("C.scheduled_transfer_allowed,C.restricted_msisdns,C.parent_category_code,C.user_id_prefix,C.outlets_allowed ,C.category_type,C.agent_allowed,C.hierarchy_allowed ");
        strBuff.append(" FROM categories C, Domains D  WHERE C.category_code=? AND C.domain_code=D.domain_Code ");
        strBuff.append(" AND C.status <>? ");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Select Query= " + sqlSelect);
        }

        try(PreparedStatement pstmtSelect = p_con.prepareStatement(sqlSelect);) {
           
            pstmtSelect.setString(1, p_categoryCode);
            pstmtSelect.setString(2, PretupsI.CATEGORY_STATUS_DELETE);
            
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
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
                categoryVO.setProductTypeAllowed(rs.getString("product_types_allowed"));
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
        }

        catch (SQLException sqe) {
            log.error(methodName, "SQL Exception" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[loadCategoryDetailsUsingCategoryCode]", "", "", "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TXN_STATUS_FAIL);
        } catch (Exception e) {
            log.error(methodName, " Exception" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "categoryDAO[loadCategoryDetailsUsingCategoryCode]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.TXN_STATUS_FAIL);

        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting size=" + categoryList.size());
            }
        }

        return categoryList;
    }


    
    public int loadParentCategorySequenceNo(Connection con,String categoryCode) throws BTSLBaseException {
        int seq_no = 0;
        final String methodName = "loadSequenceNo";
        if (log.isDebugEnabled())
            log.debug(methodName, "Entered categoryCode=" + categoryCode );

        
         
        StringBuilder strBuff = new StringBuilder();
        strBuff.append("select sequence_no from categories where UPPER(category_code) = ?   ");

        String sqlSelect = strBuff.toString();
        if (log.isDebugEnabled())
            log.debug(methodName, "Select Query= " + sqlSelect);
        try (PreparedStatement pstmtSelect = con.prepareStatement(sqlSelect);){
            
            int i = 1;
            pstmtSelect.setString(i++, categoryCode.toUpperCase());
           
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next())
                seq_no = rs.getInt(1);
            }
        } catch (SQLException sqe) {
            log.error(methodName, "SQL Exception" + sqe.getMessage());
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
                    EventStatusI.RAISED, EventLevelI.FATAL,
                    "categoryDAO[loadSequenceNo]", "", "", "",
                    "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName,
                    "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, " Exception" + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
                    EventStatusI.RAISED, EventLevelI.FATAL,
                    "categoryDAO[loadSequenceNo]", "", "", "",
                    "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName,
                    "error.general.processing");

        } finally {
            
            
            if (log.isDebugEnabled())
                log.debug(methodName, "Exiting Seq_no=" + seq_no);
        }
        return seq_no;
    }
	
}

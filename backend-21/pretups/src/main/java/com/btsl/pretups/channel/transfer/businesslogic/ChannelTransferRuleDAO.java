/**
 * @# ChannelTransferRuleDAO.java
 * 
 *    Created on Created by History
 *    --------------------------------------------------------------------------
 *    ------
 *    Aug 1, 2005 Sandeep Goel Initial creation
 *    May 12, 2006 Sandeep Goel Modification
 *    --------------------------------------------------------------------------
 *    ------
 *    Copyright(c) 2005 Bharti Telesoft Ltd.
 */
package com.btsl.pretups.channel.transfer.businesslogic;

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
import com.btsl.pretups.common.PretupsI;

/**
 * * @author sandeep.goel
 * 
 * @version $Revision: 1.0 $
 */

public class ChannelTransferRuleDAO {
    /**
     * Field _log. This field is used to display the logs for debugging purpose.
     */
    private Log _log = LogFactory.getLog(this.getClass().getName());

    /**
     * Load the transfer rules for transfer
     * 
     * @param p_con
     * @param p_networkCode
     * @param p_domainCode
     * @param p_fromCategory
     * @param p_toCategory
     * @param p_ruleType
     *            String
     * @param isLoadItemsList
     *            TODO
     * @return ChannelTransferRuleVO
     * @throws BTSLBaseException
     */
    public ChannelTransferRuleVO loadTransferRule(Connection p_con, String p_networkCode, String p_domainCode, String p_fromCategory, String p_toCategory, String p_ruleType, boolean isLoadItemsList) throws BTSLBaseException {
        final String methodName = "loadTransferRule";
        if (_log.isDebugEnabled()) {
            _log.debug(
                methodName,
                "Entered  From Category Code " + p_fromCategory + " Network Code " + p_networkCode + " domaiCode " + p_domainCode + " toCategory " + p_toCategory + " p_ruleType: " + p_ruleType);
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        final StringBuffer strBuff = new StringBuffer(" SELECT transfer_rule_id, parent_association_allowed, ");
        strBuff.append("direct_transfer_allowed,transfer_chnl_bypass_allowed, withdraw_allowed, ");
        strBuff.append("withdraw_chnl_bypass_allowed,return_allowed, return_chnl_bypass_allowed, ");
        strBuff.append("approval_required,first_approval_limit, second_approval_limit,CHNLTRF.uncntrl_transfer_allowed, ");
        strBuff.append("transfer_type, transfer_allowed, foc_transfer_type, foc_allowed,CHNLTRF.restricted_msisdn_access, ");
        strBuff.append("to_domain_code, uncntrl_transfer_level, cntrl_transfer_level, restricted_recharge_allowed,  ");
        strBuff.append("fixed_transfer_level, fixed_transfer_category, uncntrl_return_allowed, ");
        strBuff.append("uncntrl_return_level, cntrl_return_level, fixed_return_level, ");
        strBuff.append("fixed_return_category, uncntrl_withdraw_allowed, uncntrl_withdraw_level, ");
        strBuff.append("cntrl_withdraw_level, fixed_withdraw_level, fixed_withdraw_category, ");
        strBuff.append("CAT1.sequence_no from_seq_no,CAT2.sequence_no to_seq_no ");
        strBuff.append("FROM chnl_transfer_rules CHNLTRF,categories CAT1,categories CAT2 ");
        strBuff.append("WHERE ");
        strBuff.append("CHNLTRF.network_code = ? AND CHNLTRF.domain_code = ? AND from_category = ? AND to_category = ? AND ");
        strBuff.append("CHNLTRF.status = 'Y' AND type = ? AND CHNLTRF.from_category=CAT1.category_code ");
        strBuff.append("AND CHNLTRF.to_category=CAT2.category_code ");
        final String sqlSelect = strBuff.toString();
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
        }
        ChannelTransferRuleVO rulesVO = null;
        try {
            pstmt = p_con.prepareStatement(sqlSelect);
            int i = 0;
            pstmt.setString(++i, p_networkCode);
            pstmt.setString(++i, p_domainCode);
            pstmt.setString(++i, p_fromCategory);
            pstmt.setString(++i, p_toCategory);
            pstmt.setString(++i, p_ruleType);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                rulesVO = new ChannelTransferRuleVO();
                rulesVO.setNetworkCode(p_networkCode);
                rulesVO.setDomainCode(p_domainCode);
                rulesVO.setToCategory(p_toCategory);
                rulesVO.setFromCategory(p_fromCategory);
                rulesVO.setTransferRuleID(rs.getString("transfer_rule_id"));
                rulesVO.setParentAssocationAllowed(rs.getString("parent_association_allowed"));
                rulesVO.setDirectTransferAllowed(rs.getString("direct_transfer_allowed"));
                rulesVO.setTransferChnlBypassAllowed(rs.getString("transfer_chnl_bypass_allowed"));
                rulesVO.setWithdrawAllowed(rs.getString("withdraw_allowed"));
                rulesVO.setWithdrawChnlBypassAllowed(rs.getString("withdraw_chnl_bypass_allowed"));
                rulesVO.setReturnAllowed(rs.getString("return_allowed"));
                rulesVO.setReturnChnlBypassAllowed(rs.getString("return_chnl_bypass_allowed"));
                rulesVO.setApprovalRequired(rs.getString("approval_required"));
                rulesVO.setFirstApprovalLimit(rs.getLong("first_approval_limit"));
                rulesVO.setSecondApprovalLimit(rs.getLong("second_approval_limit"));
                rulesVO.setUncntrlTransferAllowed(rs.getString("uncntrl_transfer_allowed"));
                rulesVO.setTransferType(rs.getString("transfer_type"));
                rulesVO.setTransferAllowed(rs.getString("transfer_allowed"));
                rulesVO.setFocAllowed(rs.getString("foc_allowed"));
                rulesVO.setFocTransferType(rs.getString("foc_transfer_type"));
                rulesVO.setRestrictedMsisdnAccess(rs.getString("restricted_msisdn_access"));
                rulesVO.setRestrictedRechargeAccess(rs.getString("restricted_recharge_allowed"));
                rulesVO.setFromSeqNo(rs.getInt("from_seq_no"));
                rulesVO.setToSeqNo(rs.getInt("to_seq_no"));

                // new fields added in the table
                rulesVO.setToDomainCode(rs.getString("to_domain_code"));

                rulesVO.setUncntrlTransferLevel(rs.getString("uncntrl_transfer_level"));
                rulesVO.setCntrlTransferLevel(rs.getString("cntrl_transfer_level"));
                rulesVO.setFixedTransferLevel(rs.getString("fixed_transfer_level"));
                rulesVO.setFixedTransferCategory(rs.getString("fixed_transfer_category"));

                rulesVO.setUncntrlReturnAllowed(rs.getString("uncntrl_return_allowed"));
                rulesVO.setUncntrlReturnLevel(rs.getString("uncntrl_return_level"));
                rulesVO.setCntrlReturnLevel(rs.getString("cntrl_return_level"));
                rulesVO.setFixedReturnLevel(rs.getString("fixed_return_level"));
                rulesVO.setFixedReturnCategory(rs.getString("fixed_return_category"));

                rulesVO.setUncntrlWithdrawAllowed(rs.getString("uncntrl_withdraw_allowed"));
                rulesVO.setUncntrlWithdrawLevel(rs.getString("uncntrl_withdraw_level"));
                rulesVO.setCntrlWithdrawLevel(rs.getString("cntrl_withdraw_level"));
                rulesVO.setFixedWithdrawLevel(rs.getString("fixed_withdraw_level"));
                rulesVO.setFixedWithdrawCategory(rs.getString("fixed_withdraw_category"));
                // ends here
                if (isLoadItemsList) {
                    rulesVO.setProductVOList(this.loadProductVOList(p_con, rulesVO.getTransferRuleID()));
                }
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException : " + sqe);
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleDAO[loadTransferRule]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            _log.error(methodName, "Exception : " + ex);
            _log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleDAO[loadTransferRule]", "", "", "",
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
                _log.debug(methodName, "Exiting:  ChannelTransferRuleVO =" + rulesVO);
            }
        }
        return rulesVO;
    }

    /**
     * Constructor for ChannelTransferRuleDAO.
     */
    public ChannelTransferRuleDAO() {
        super();
    }

    /**
     * Method loadProductVOList.
     * This method loads all the products corresponds to the transferRuleID.
     * 
     * @param p_con
     *            Connection
     * @param p_transferRuleID
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadProductVOList(Connection p_con, String p_transferRuleID) throws BTSLBaseException {
        final String methodName = "loadProductVOList";
        if (_log.isDebugEnabled()) {
            _log.debug(methodName, "Entered:p_transferRuleID=" + p_transferRuleID);
        }
        final ArrayList productVOList = new ArrayList();
        PreparedStatement pstmtSelect = null;
        ResultSet rsSelect = null;
        try {
            final StringBuffer selectQuery = new StringBuffer("SELECT PROD.product_code,PROD.product_name ");
            selectQuery.append("FROM products PROD,chnl_transfer_rules_products TRFPROD,network_product_mapping NPM, ");
            selectQuery.append("chnl_transfer_rules CTR ");
            selectQuery.append("WHERE TRFPROD.transfer_rule_id=? AND TRFPROD.product_code=PROD.product_code ");
            selectQuery.append("AND CTR.transfer_rule_id=TRFPROD.transfer_rule_id AND CTR.network_code=NPM.network_code ");
            selectQuery.append("AND NPM.product_code=TRFPROD.product_code AND NPM.status='Y' ");
            selectQuery.append("ORDER BY PROD.product_name ");
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "Query=" + selectQuery);
            }
            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
            pstmtSelect.setString(1, p_transferRuleID);
            rsSelect = pstmtSelect.executeQuery();
            ListValueVO listValueVO = null;
            while (rsSelect.next()) {
                listValueVO = new ListValueVO(rsSelect.getString("product_name"), rsSelect.getString("product_code"));
                productVOList.add(listValueVO);
            }
        } catch (SQLException sqe) {
            _log.error(methodName, "SQLException:" + sqe.getMessage());
            _log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleDAO[loadProductVOList]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(methodName, "Exception:" + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleDAO[loadProductVOList]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (rsSelect != null) {
                    rsSelect.close();
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
                _log.debug(methodName, "Exiting:list size=" + productVOList.size());
            }
        }
        return productVOList;
    }
	
	/**
	 * Load the categories with associated transfer rules
	 * @param p_con
	 * @param p_networkCode
	 * @param p_fromCategory
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public ArrayList loadTransferRulesCategoryListForOperator(Connection p_con, String p_networkCode, String p_fromCategory) throws BTSLBaseException
	{
		final String methodName = "loadTransferRulesCategoryListForOperator";
		if (_log.isDebugEnabled())
			_log.debug(methodName, "Entered  From Category Code "+p_fromCategory+" Network Code "+p_networkCode);
		PreparedStatement pstmtSelect = null;
		ResultSet rsSelect = null;
		StringBuffer strBuff = new StringBuffer(" SELECT ctr.domain_code, ctr.from_category, ctr.to_category, ctr.parent_association_allowed, ");
		strBuff.append(" ctr.direct_transfer_allowed, ctr.transfer_chnl_bypass_allowed,ctr.withdraw_allowed, "); 
		strBuff.append(" ctr.withdraw_chnl_bypass_allowed, ctr.return_allowed, ctr.return_chnl_bypass_allowed, "); 
		strBuff.append(" ctr.approval_required, ctr.first_approval_limit, ctr.second_approval_limit,transfer_rule_id, ");
		strBuff.append(" ctr.transfer_type, ctr.transfer_allowed, ctr.foc_transfer_type, ctr.foc_allowed ,ctr.restricted_msisdn_access, restricted_recharge_allowed, ");
		strBuff.append(" cf.category_name as fcategory_name,ct.category_name as tcategory_name,df.domain_name as fdomain_name,dt.domain_name as tdomain_name,ctr.uncntrl_transfer_allowed,ctr.to_domain_code,ctr.uncntrl_return_level, ctr.cntrl_return_level ");  
		strBuff.append(" FROM "); 
		strBuff.append(" chnl_transfer_rules ctr ,categories cf, categories ct, domains df, domains dt "); 
		strBuff.append(" WHERE "); 
		strBuff.append(" ctr.from_category <> ? AND ctr.network_code=?  AND "); 
		strBuff.append(" ctr.status = 'Y' and cf.status= 'Y' and ct.status='Y'  and df.status='Y' and dt.status='Y' and df.domain_code=dt.domain_code AND ctr.domain_code = df.domain_code AND ctr.from_category = cf.category_code AND ctr.to_category = ct.category_code");
		strBuff.append(" ORDER BY ctr.domain_code, to_domain_code, cf.sequence_no ");
		String sqlSelect = strBuff.toString();
		if (_log.isDebugEnabled())
		    _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		ArrayList arrayList = new ArrayList();
		try
		{
			pstmtSelect = p_con.prepareStatement(sqlSelect);
			int i=1;
			pstmtSelect.setString(i++,p_fromCategory);
            pstmtSelect.setString(i++,p_networkCode);
			rsSelect = pstmtSelect.executeQuery();
			ChannelTransferRuleVO rulesVO = null; 
			while (rsSelect.next())
			{
				rulesVO = new ChannelTransferRuleVO();
				rulesVO.setDomainCode(rsSelect.getString("domain_code"));
				rulesVO.setFromCategory(rsSelect.getString("from_category"));
				rulesVO.setToDomainCode(rsSelect.getString("to_domain_code"));
				rulesVO.setToCategory(rsSelect.getString("to_category"));
				//Descriptions
				rulesVO.setFromDomainDes(rsSelect.getString("fdomain_name"));
				rulesVO.setFromCategoryDes(rsSelect.getString("fcategory_name"));
				rulesVO.setToDomainDes(rsSelect.getString("tdomain_name"));
				rulesVO.setToCategoryDes(rsSelect.getString("tcategory_name"));
				//
				rulesVO.setParentAssocationAllowed(rsSelect.getString("parent_association_allowed"));
				rulesVO.setDirectTransferAllowed(rsSelect.getString("direct_transfer_allowed"));
				rulesVO.setTransferChnlBypassAllowed(rsSelect.getString("transfer_chnl_bypass_allowed"));
				rulesVO.setWithdrawAllowed(rsSelect.getString("withdraw_allowed"));
				rulesVO.setWithdrawChnlBypassAllowed(rsSelect.getString("withdraw_chnl_bypass_allowed"));
				rulesVO.setReturnAllowed(rsSelect.getString("return_allowed"));
				rulesVO.setReturnChnlBypassAllowed(rsSelect.getString("return_chnl_bypass_allowed"));
				rulesVO.setApprovalRequired(rsSelect.getString("approval_required"));
				rulesVO.setFirstApprovalLimit(rsSelect.getLong("first_approval_limit"));
				rulesVO.setSecondApprovalLimit(rsSelect.getLong("second_approval_limit"));
				rulesVO.setUncntrlTransferAllowed(rsSelect.getString("uncntrl_transfer_allowed"));
				rulesVO.setTransferType(rsSelect.getString("transfer_type"));
				rulesVO.setTransferAllowed(rsSelect.getString("transfer_allowed"));
				rulesVO.setFocAllowed(rsSelect.getString("foc_allowed"));
				rulesVO.setFocTransferType(rsSelect.getString("foc_transfer_type"));
				rulesVO.setRestrictedMsisdnAccess(rsSelect.getString("restricted_msisdn_access"));
				rulesVO.setRestrictedRechargeAccess(rsSelect.getString("restricted_recharge_allowed"));
				rulesVO.setUncntrlReturnLevel(rsSelect.getString("uncntrl_return_level"));
				rulesVO.setCntrlReturnLevel(rsSelect.getString("cntrl_return_level"));
				rulesVO.setTransferRuleID(rsSelect.getString("transfer_rule_id"));
				arrayList.add(rulesVO);
			}	
			
		} 
		catch (SQLException sqe)
		{
			_log.error(methodName, "SQLException : " + sqe.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelTransferRuleDAO[loadTransferRulesCategoryListForOperator]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex)
		{
			_log.error(methodName, "Exception : " + ex.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelTransferRuleDAO[loadTransferRulesCategoryListForOperator]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally
		{
		    try{if (rsSelect != null){rsSelect.close();}} catch (Exception e){}
			try{if (pstmtSelect != null){pstmtSelect.close();}} catch (Exception e){}
	
			if (_log.isDebugEnabled())
			{
				_log.debug(methodName, "Exiting:  arrayList Size =" + arrayList.size());
			}
		}
		return arrayList;
	}
	
	/**
	 * Load the categories with associated transfer rules
	 * @param p_con
	 * @param p_networkCode
	 * @param p_fromCategory
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public ArrayList loadTransferRulesCategoryListEnquiryForOperator(Connection p_con, String p_networkCode, String p_fromCategory) throws BTSLBaseException
	{
		final String methodName = "loadTransferRulesCategoryListEnquiryForOperator";
		if (_log.isDebugEnabled())
			_log.debug(methodName, "Entered  From Category Code "+p_fromCategory+" Network Code "+p_networkCode);
		PreparedStatement pstmtSelect = null;
		ResultSet rsSelect = null;
		StringBuffer strBuff = new StringBuffer(" SELECT distinct ctr.to_category, ct.category_name AS tcategory_name, dt.domain_name AS tdomain_name,  ctr.to_domain_code ");
		strBuff.append(" FROM "); 
		strBuff.append(" chnl_transfer_rules ctr , categories ct,  domains dt "); 
		strBuff.append(" WHERE "); 
		strBuff.append(" ctr.from_category <> ?  and ctr.to_category<> ? AND ctr.network_code=?  "); 
		strBuff.append(" AND ctr.status = 'Y' and ct.status='Y' and dt.status='Y' and dt.domain_code=to_domain_code  AND ctr.TYPE='CHANNEL'  AND ctr.to_category = ct.category_code ");
		//strBuff.append(" AND ctr.direct_transfer_allowed='Y' and ctr.transfer_chnl_bypass_allowed='Y' ");
		strBuff.append(" ORDER BY to_domain_code");
		String sqlSelect = strBuff.toString();
		if (_log.isDebugEnabled())
		    _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		ArrayList arrayList = new ArrayList();
		try
		{
			pstmtSelect = p_con.prepareStatement(sqlSelect);
			int i=1;
			pstmtSelect.setString(i++,PretupsI.OPERATOR_TYPE_OPT);
			pstmtSelect.setString(i++,PretupsI.OPERATOR_TYPE_OPT);
            pstmtSelect.setString(i++,p_networkCode);
			rsSelect = pstmtSelect.executeQuery();
			ChannelTransferRuleVO rulesVO = null; 
			while (rsSelect.next())
			{
				rulesVO = new ChannelTransferRuleVO();
				rulesVO.setToDomainCode(rsSelect.getString("to_domain_code"));
				rulesVO.setToCategory(rsSelect.getString("to_category"));
				//Descriptions
				rulesVO.setToDomainDes(rsSelect.getString("tdomain_name"));
				rulesVO.setToCategoryDes(rsSelect.getString("tcategory_name"));
				//
				rulesVO.setDirectTransferAllowed(PretupsI.YES);
				rulesVO.setTransferChnlBypassAllowed(PretupsI.YES);
				arrayList.add(rulesVO);
			}	
			
		} 
		catch (SQLException sqe)
		{
			_log.error(methodName, "SQLException : " + sqe.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelTransferRuleDAO[loadTransferRulesCategoryListForOperator]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex)
		{
			_log.error(methodName, "Exception : " + ex.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelTransferRuleDAO[loadTransferRulesCategoryListForOperator]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally
		{
		    try{if (rsSelect != null){rsSelect.close();}} catch (Exception e){}
			try{if (pstmtSelect != null){pstmtSelect.close();}} catch (Exception e){}
	
			if (_log.isDebugEnabled())
			{
				_log.debug(methodName, "Exiting:  arrayList Size =" + arrayList.size());
			}
		}
		return arrayList;
	}
	
	/**
	 * Load the categories with associated transfer rules
	 * @param p_con
	 * @param p_networkCode
	 * @param p_fromCategory
	 * @return ArrayList
	 * @throws BTSLBaseException
	 */
	public ArrayList loadTransferRulesCategoryList(Connection p_con, String p_networkCode, String p_fromCategory) throws BTSLBaseException
	{
		final String methodName = "loadTransferRulesCategoryList";
		if (_log.isDebugEnabled())
			_log.debug(methodName, "Entered  From Category Code "+p_fromCategory+" Network Code "+p_networkCode);
		PreparedStatement pstmtSelect = null;
		ResultSet rsSelect = null;
		StringBuffer strBuff = new StringBuffer(" SELECT ctr.domain_code, ctr.to_category, ctr.parent_association_allowed, ");
		strBuff.append(" ctr.direct_transfer_allowed, ctr.transfer_chnl_bypass_allowed,ctr.withdraw_allowed, "); 
		strBuff.append(" ctr.withdraw_chnl_bypass_allowed, ctr.return_allowed, ctr.return_chnl_bypass_allowed, "); 
		strBuff.append(" ctr.approval_required, ctr.first_approval_limit, ctr.second_approval_limit,transfer_rule_id, ");
		strBuff.append(" ctr.transfer_type, ctr.transfer_allowed, ctr.foc_transfer_type, ctr.foc_allowed ,ctr.restricted_msisdn_access, restricted_recharge_allowed, ");
		strBuff.append(" c.category_name,ctr.uncntrl_transfer_allowed,ctr.to_domain_code,ctr.uncntrl_return_level, ctr.cntrl_return_level ");  
		strBuff.append(" FROM "); 
		strBuff.append(" chnl_transfer_rules ctr ,categories c "); 
		strBuff.append(" WHERE "); 
		strBuff.append(" ctr.from_category = ? AND ctr.network_code=?  AND "); 
		strBuff.append(" ctr.status = 'Y' AND ctr.to_category = c.category_code ");
		strBuff.append(" ORDER BY to_domain_code, c.sequence_no ");
		String sqlSelect = strBuff.toString();
		if (_log.isDebugEnabled())
		    _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		ArrayList arrayList = new ArrayList();
		try
		{
			pstmtSelect = p_con.prepareStatement(sqlSelect);
			int i=1;
			pstmtSelect.setString(i++,p_fromCategory);
            pstmtSelect.setString(i++,p_networkCode);
			rsSelect = pstmtSelect.executeQuery();
			ChannelTransferRuleVO rulesVO = null; 
			while (rsSelect.next())
			{
				rulesVO = new ChannelTransferRuleVO();
				rulesVO.setDomainCode(rsSelect.getString("domain_code"));
				rulesVO.setToCategory(rsSelect.getString("to_category"));
				rulesVO.setParentAssocationAllowed(rsSelect.getString("parent_association_allowed"));
				rulesVO.setDirectTransferAllowed(rsSelect.getString("direct_transfer_allowed"));
				rulesVO.setTransferChnlBypassAllowed(rsSelect.getString("transfer_chnl_bypass_allowed"));
				rulesVO.setWithdrawAllowed(rsSelect.getString("withdraw_allowed"));
				rulesVO.setWithdrawChnlBypassAllowed(rsSelect.getString("withdraw_chnl_bypass_allowed"));
				rulesVO.setReturnAllowed(rsSelect.getString("return_allowed"));
				rulesVO.setReturnChnlBypassAllowed(rsSelect.getString("return_chnl_bypass_allowed"));
				rulesVO.setApprovalRequired(rsSelect.getString("approval_required"));
				rulesVO.setFirstApprovalLimit(rsSelect.getLong("first_approval_limit"));
				rulesVO.setSecondApprovalLimit(rsSelect.getLong("second_approval_limit"));
				rulesVO.setToCategoryDes(rsSelect.getString("category_name"));
				rulesVO.setUncntrlTransferAllowed(rsSelect.getString("uncntrl_transfer_allowed"));
				rulesVO.setTransferType(rsSelect.getString("transfer_type"));
				rulesVO.setTransferAllowed(rsSelect.getString("transfer_allowed"));
				rulesVO.setFocAllowed(rsSelect.getString("foc_allowed"));
				rulesVO.setFocTransferType(rsSelect.getString("foc_transfer_type"));
				rulesVO.setRestrictedMsisdnAccess(rsSelect.getString("restricted_msisdn_access"));
				rulesVO.setRestrictedRechargeAccess(rsSelect.getString("restricted_recharge_allowed"));
				rulesVO.setToDomainCode(rsSelect.getString("to_domain_code"));
				rulesVO.setUncntrlReturnLevel(rsSelect.getString("uncntrl_return_level"));
				rulesVO.setCntrlReturnLevel(rsSelect.getString("cntrl_return_level"));
				rulesVO.setTransferRuleID(rsSelect.getString("transfer_rule_id"));
				arrayList.add(rulesVO);
			}	
			
		} 
		catch (SQLException sqe)
		{
			_log.error(methodName, "SQLException : " + sqe.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelTransferRuleDAO[loadTransferRulesCategoryList]","","","","SQL Exception:"+sqe.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception ex)
		{
			_log.error(methodName, "Exception : " + ex.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelTransferRuleDAO[loadTransferRulesCategoryList]","","","","Exception:"+ex.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		} finally
		{
		    try{if (rsSelect != null){rsSelect.close();}} catch (Exception e){}
			try{if (pstmtSelect != null){pstmtSelect.close();}} catch (Exception e){}
	
			if (_log.isDebugEnabled())
			{
				_log.debug(methodName, "Exiting:  arrayList Size =" + arrayList.size());
			}
		}
		return arrayList;
	}
}

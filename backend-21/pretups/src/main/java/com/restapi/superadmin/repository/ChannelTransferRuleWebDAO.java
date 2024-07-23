package com.restapi.superadmin.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.stereotype.Component;

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
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.util.BTSLUtil;
import com.restapi.superadminVO.ChannelTransferRuleVO;


@Component
public class ChannelTransferRuleWebDAO {

	
	
	
	
	    /**
	     * Field _log. This field is used to display the logs for debugging purpose.
	     */
	    private Log _log = LogFactory.getLog(this.getClass().getName());

	    /**
	     * Constructor for ChannelTransferRuleWebDAO.
	     */
	    public ChannelTransferRuleWebDAO() {
	        super();
	    }

	    /**
	     * Method loadProductList.
	     * This method loads all of the products available in the specified network.
	     * 
	     * @param p_con
	     *            Connection
	     * @param p_networkCode
	     *            String
	     * @param p_moduleCode
	     *            String
	     * @return ArrayList
	     * @throws BTSLBaseException
	     */
	    public ArrayList loadProductList(Connection p_con, String p_networkCode, String p_moduleCode) throws BTSLBaseException {
	        final String methodName = "loadProductList";
	        StringBuilder loggerValue= new StringBuilder();
	        
	        if (_log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append(" Entered:p_networkCode= ");
	        	loggerValue.append(p_networkCode);
	        	loggerValue.append(", p_moduleCode= ");
	        	loggerValue.append(p_moduleCode);
	            _log.debug(methodName, loggerValue);
	        }
	        final ArrayList productList = new ArrayList();
	        PreparedStatement pstmtSelect = null;
	        ResultSet rsSelect = null;
	        try {
	            final StringBuffer selectQuery = new StringBuffer("SELECT PROD.product_code,PROD.product_name ");
	            selectQuery.append("FROM products PROD,network_product_mapping NPM ");
	            selectQuery.append("WHERE NPM.network_code=? AND NPM.product_code=PROD.product_code ");
	            selectQuery.append("AND PROD.module_code=? AND PROD.status=? AND NPM.status=? ");
	            selectQuery.append("ORDER BY PROD.product_name ");
	            if (_log.isDebugEnabled()) {
	            	loggerValue.setLength(0);
	            	loggerValue.append("Query=");
	            	loggerValue.append(selectQuery);
	                _log.debug(methodName, loggerValue);
	            }
	            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
	            pstmtSelect.setString(1, p_networkCode);
	            pstmtSelect.setString(2, p_moduleCode);
	            pstmtSelect.setString(3, PretupsI.PRODUCT_STATUS);
	            pstmtSelect.setString(4, PretupsI.PRODUCT_STATUS);
	            rsSelect = pstmtSelect.executeQuery();
	            ListValueVO listValueVO = null;
	            while (rsSelect.next()) {
	                listValueVO = new ListValueVO(rsSelect.getString("product_name"), rsSelect.getString("product_code"));
	                productList.add(listValueVO);
	            }
	        } catch (SQLException sqe) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("SQLException:");
	        	loggerValue.append(sqe.getMessage());
	            _log.error(methodName, loggerValue);
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[loadProductList]", "", "",
	                "", loggerValue.toString());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception e) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("SQLException:");
	        	loggerValue.append(e.getMessage());
	            _log.error(methodName, loggerValue);
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[loadProductList]", "", "",
	                "", loggerValue.toString());
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
	            	loggerValue.setLength(0);
	            	loggerValue.append("Exiting:list size=");
	            	loggerValue.append(productList.size());
	                _log.debug(methodName, loggerValue);
	            }
	        }
	        return productList;
	    }

	    /**
	     * Method addChannelTransferRule.
	     * This method adds the transfer rule and also adds the transferrule and
	     * product mapping for all of the procduct
	     * associated with the transferrule.
	     * 
	     * @param p_con
	     *            Connection
	     * @param p_channelTransferRuleVO
	     *            ChannelTransferRuleVO
	     * @return int
	     * @throws BTSLBaseException
	     */
	    public int addChannelTransferRule(Connection p_con, ChannelTransferRuleVO p_channelTransferRuleVO) throws BTSLBaseException {
	        final String methodName = "addChannelTransferRule";
	        StringBuilder loggerValue= new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append(" Entered:p_channelTransferRuleVO= ");
	        	loggerValue.append(p_channelTransferRuleVO);
	            _log.debug(methodName, loggerValue);
	        }
	        PreparedStatement pstmtInsert = null;
	        int addCount = 0;
	        try {
	            final StringBuffer insertQuery = new StringBuffer("INSERT INTO chnl_transfer_rules(transfer_rule_id, domain_code, network_code, ");
	            insertQuery.append("from_category, to_category, parent_association_allowed,direct_transfer_allowed, transfer_chnl_bypass_allowed, ");
	            insertQuery.append("withdraw_allowed, withdraw_chnl_bypass_allowed, return_allowed, return_chnl_bypass_allowed, ");
	            insertQuery.append("approval_required, first_approval_limit, second_approval_limit, created_by, created_on, ");
	            insertQuery.append("modified_by, modified_on,type,uncntrl_transfer_allowed,transfer_type, ");
	            insertQuery.append("transfer_allowed, foc_transfer_type, foc_allowed,restricted_msisdn_access, ");
	            insertQuery.append("to_domain_code, uncntrl_transfer_level, cntrl_transfer_level, fixed_transfer_level, ");
	            insertQuery.append("fixed_transfer_category, uncntrl_return_allowed, uncntrl_return_level, ");
	            insertQuery.append("cntrl_return_level, fixed_return_level, fixed_return_category, ");
	            insertQuery.append("uncntrl_withdraw_allowed, uncntrl_withdraw_level, cntrl_withdraw_level, ");
	            insertQuery.append("fixed_withdraw_level, fixed_withdraw_category, restricted_recharge_allowed,direct_payout_allowed  ) ");
	            insertQuery.append("VALUES(UPPER(?),UPPER(?),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
	            final String query = insertQuery.toString();
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Query=" + query);
	            }
	            pstmtInsert = p_con.prepareStatement(query);
	            int i = 1;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getTransferRuleID());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getDomainCode());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getNetworkCode());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getFromCategory());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getToCategory());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getParentAssocationAllowed());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getDirectTransferAllowed());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getTransferChnlBypassAllowed());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getWithdrawAllowed());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getWithdrawChnlBypassAllowed());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getReturnAllowed());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getReturnChnlBypassAllowed());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getApprovalRequired());
	            i++;
	            pstmtInsert.setLong(i, p_channelTransferRuleVO.getFirstApprovalLimit());
	            i++;
	            pstmtInsert.setLong(i, p_channelTransferRuleVO.getSecondApprovalLimit());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getCreatedBy());
	            i++;
	            pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferRuleVO.getCreatedOn()));
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getModifiedBy());
	            i++;
	            pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferRuleVO.getModifiedOn()));
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getType());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getUncntrlTransferAllowed());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getTransferType());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getTransferAllowed());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getFocTransferType());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getFocAllowed());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getRestrictedMsisdnAccess());
	            i++;
	            // new fields added
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getToDomainCode());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getUncntrlTransferLevel());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getCntrlTransferLevel());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getFixedTransferLevel());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getFixedTransferCategory());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getUncntrlReturnAllowed());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getUncntrlReturnLevel());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getCntrlReturnLevel());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getFixedReturnLevel());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getFixedReturnCategory());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getUncntrlWithdrawAllowed());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getUncntrlWithdrawLevel());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getCntrlWithdrawLevel());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getFixedWithdrawLevel());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getFixedWithdrawCategory());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getRestrictedRechargeAccess());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getDpAllowed());
	            i++;
	            // ends here

	            addCount = pstmtInsert.executeUpdate();
	            // add the product information corresponds to the transfer rule.
	            if (addCount > 0 && p_channelTransferRuleVO.getProductArray() != null && p_channelTransferRuleVO.getProductArray().length > 0) {
	                // if(p_channelTransferRuleVO.getProductArray()[0]!=null &&
	                // p_channelTransferRuleVO.getProductArray()[0].length()>0)
	                addCount = this.addTransferRuleProductMapping(p_con, p_channelTransferRuleVO.getTransferRuleID(), p_channelTransferRuleVO.getProductArray());
	            }
	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[addChannelTransferRule]",
	                "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[addChannelTransferRule]",
	                "", "", "", "Exception:" + e.getMessage());
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
	                _log.debug(methodName, "Exiting addCount=" + addCount);
	            }
	        }
	        return addCount;
	    }

	    /**
	     * Method addTransferRuleProductMapping.
	     * This method is used to add the transferrule and product information.
	     * 
	     * @param p_con
	     *            Connection
	     * @param p_transferRuleID
	     *            String
	     * @param p_productArray
	     *            String[]
	     * @return int
	     * @throws BTSLBaseException
	     */
	    private int addTransferRuleProductMapping(Connection p_con, String p_transferRuleID, String[] p_productArray) throws BTSLBaseException {
	        final String methodName = "addTransferRuleProductMapping";
	        StringBuilder loggerValue= new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered:p_transferRuleID=");
	        	loggerValue.append(p_transferRuleID);
	        	loggerValue.append(",p_productArray=");
	        	loggerValue.append(p_productArray);
	            _log.debug(methodName, loggerValue);
	        }
	        PreparedStatement pstmtInsert = null;
	        int addCount = 0;
	        try {
	            final StringBuffer insertQuery = new StringBuffer("INSERT INTO chnl_transfer_rules_products(transfer_rule_id, product_code) ");
	            insertQuery.append("VALUES(?,?)");
	            final String query = insertQuery.toString();
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Query=" + query);
	            }
	            pstmtInsert = p_con.prepareStatement(query);
	            for (int i = 0, j = p_productArray.length; i < j; i++) {
	                pstmtInsert.setString(1, p_transferRuleID);
	                pstmtInsert.setString(2, p_productArray[i]);
	                addCount = pstmtInsert.executeUpdate();
	                if (addCount == 0) {
	                    throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	                }
	                // clearing the parameters
	                pstmtInsert.clearParameters();
	            }
	        }// end of try
	        catch (BTSLBaseException e) {
	            _log.error(methodName, "BTSLBaseException" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[addTransferRuleProductMapping]", "", "", "", "BTSLBaseException:" + e.getMessage());
	            throw e;
	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[addTransferRuleProductMapping]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[addTransferRuleProductMapping]", "", "", "", "Exception:" + e.getMessage());
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
	                _log.debug(methodName, "Exiting addCount=" + addCount);
	            }
	        }
	        return addCount;
	    }

	    /**
	     * Method updateChannelTransferRule.
	     * This method update the transfer rule and its products information.
	     * To update the product information, in this method we first delete all the
	     * product associated with this transfer
	     * rule and then insert new specified products informaiton.
	     * 
	     * @param p_con
	     *            Connection
	     * @param p_channelTransferRuleVO
	     *            ChannelTransferRuleVO
	     * @return int
	     * @throws BTSLBaseException
	     */
	    public int updateChannelTransferRule(Connection p_con, ChannelTransferRuleVO p_channelTransferRuleVO) throws BTSLBaseException {
	        final String methodName = "updateChannelTransferRule";
	        StringBuilder loggerValue= new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered:p_channelTransferRuleVO=");
	        	loggerValue.append(p_channelTransferRuleVO);
	            _log.debug(methodName, loggerValue);
	        }
	        PreparedStatement pstmtUpdate = null;
	        int updateCount = 0;
	        try {
	            final StringBuffer updateQuery = new StringBuffer("UPDATE chnl_transfer_rules SET from_category=?, to_category=?, ");
	            updateQuery.append("parent_association_allowed=?,direct_transfer_allowed=?,transfer_chnl_bypass_allowed=?,  ");
	            updateQuery.append("withdraw_allowed=?, withdraw_chnl_bypass_allowed=?, return_allowed=?,return_chnl_bypass_allowed=?,  ");
	            updateQuery.append("approval_required=?, first_approval_limit=?, second_approval_limit=?, modified_by=?, ");
	            updateQuery.append("modified_on=?,uncntrl_transfer_allowed=?,transfer_type=?, transfer_allowed=?, ");
	            updateQuery.append("foc_transfer_type=?, foc_allowed=?,restricted_msisdn_access=?, ");
	            updateQuery.append("to_domain_code=? , uncntrl_transfer_level=? , cntrl_transfer_level=? , ");
	            updateQuery.append("fixed_transfer_level=? , fixed_transfer_category=? , uncntrl_return_allowed=? , ");
	            updateQuery.append("uncntrl_return_level=? , cntrl_return_level=? , fixed_return_level=? , ");
	            updateQuery.append("fixed_return_category=? , uncntrl_withdraw_allowed=? , uncntrl_withdraw_level=? , ");
	            updateQuery.append("cntrl_withdraw_level=? , fixed_withdraw_level=? , fixed_withdraw_category=?, restricted_recharge_allowed = ?,direct_payout_allowed=? ");
	            updateQuery.append("WHERE transfer_rule_id=?");
	            final String query = updateQuery.toString();
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Query=" + query);
	            }
	            pstmtUpdate = p_con.prepareStatement(query);
	            int i = 1;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getFromCategory());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getToCategory());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getParentAssocationAllowed());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getDirectTransferAllowed());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getTransferChnlBypassAllowed());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getWithdrawAllowed());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getWithdrawChnlBypassAllowed());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getReturnAllowed());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getReturnChnlBypassAllowed());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getApprovalRequired());
	            i++;
	            pstmtUpdate.setLong(i, p_channelTransferRuleVO.getFirstApprovalLimit());
	            i++;
	            pstmtUpdate.setLong(i, p_channelTransferRuleVO.getSecondApprovalLimit());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getModifiedBy());
	            i++;
	            pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferRuleVO.getModifiedOn()));
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getUncntrlTransferAllowed());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getTransferType());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getTransferAllowed());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getFocTransferType());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getFocAllowed());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getRestrictedMsisdnAccess());
	            i++;
	            // new fields added
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getToDomainCode());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getUncntrlTransferLevel());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getCntrlTransferLevel());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getFixedTransferLevel());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getFixedTransferCategory());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getUncntrlReturnAllowed());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getUncntrlReturnLevel());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getCntrlReturnLevel());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getFixedReturnLevel());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getFixedReturnCategory());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getUncntrlWithdrawAllowed());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getUncntrlWithdrawLevel());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getCntrlWithdrawLevel());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getFixedWithdrawLevel());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getFixedWithdrawCategory());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getRestrictedRechargeAccess());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getDpAllowed());
	            i++;
	            // ends here

	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getTransferRuleID());
	            i++;
	            // for the checking is the record modified during the transaction.
	            final boolean modified = this.isRecordModified(p_con, p_channelTransferRuleVO.getLastModifiedTime(), p_channelTransferRuleVO.getTransferRuleID());
	            if (modified) {
	                throw new BTSLBaseException(this, methodName, "error.modify.true");
	            }
	            updateCount = pstmtUpdate.executeUpdate();
	            if (updateCount > 0) {
	                // delete existing porduct information from the mapping table
	                if (p_channelTransferRuleVO.getProductVOList() != null && p_channelTransferRuleVO.getProductVOList().size() > 0) {
	                    updateCount = this.deleteTransferRuleProductMapping(p_con, p_channelTransferRuleVO.getTransferRuleID());
	                }

	                // add new information of the products
	                if (p_channelTransferRuleVO.getProductArray() != null && p_channelTransferRuleVO.getProductArray().length > 0) {
	                    // if(p_channelTransferRuleVO.getProductArray()[0]!=null &&
	                    // p_channelTransferRuleVO.getProductArray()[0].length()>0)
	                    updateCount = this.addTransferRuleProductMapping(p_con, p_channelTransferRuleVO.getTransferRuleID(), p_channelTransferRuleVO.getProductArray());
	                }
	            }
	        } catch (BTSLBaseException be) {
	            throw be;
	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[updateChannelTransferRule]",
	                "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[updateChannelTransferRule]",
	                "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } finally {
	            try {
	                if (pstmtUpdate != null) {
	                    pstmtUpdate.close();
	                }
	            } catch (Exception ex) {
	                _log.errorTrace(methodName, ex);
	            }
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Exiting updateCount=" + updateCount);
	            }
	        }
	        return updateCount;
	    }

	    /**
	     * Method isRecordModified.
	     * 
	     * @param p_con
	     *            Connection
	     * @param p_oldlastModified
	     *            long
	     * @param p_transferRuleID
	     *            String
	     * @return boolean
	     * @throws BTSLBaseException
	     */
	    private boolean isRecordModified(Connection p_con, long p_oldlastModified, String p_transferRuleID) throws BTSLBaseException {
	        final String methodName = "isRecordModified";
	        StringBuilder loggerValue = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered:p_oldlastModified=");
	        	loggerValue.append(p_oldlastModified);
	        	loggerValue.append(",p_transferRuleID=");
	        	loggerValue.append(p_transferRuleID);
	            _log.debug(methodName, loggerValue);
	        }
	        PreparedStatement pstmtSelect = null;
	        ResultSet rs = null;
	        boolean modified = false;
	        final StringBuffer sqlRecordModified = new StringBuffer("SELECT modified_on FROM chnl_transfer_rules ");
	        sqlRecordModified.append("WHERE transfer_rule_id=? ");
	        java.sql.Timestamp newlastModified = null;
	        if (p_oldlastModified == 0) {
	            return false;
	        }
	        try {
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "QUERY=" + sqlRecordModified);
	            }
	            final String query = sqlRecordModified.toString();
	            pstmtSelect = p_con.prepareStatement(query);
	            pstmtSelect.setString(1, p_transferRuleID);
	            rs = pstmtSelect.executeQuery();
	            if (rs.next()) {
	                newlastModified = rs.getTimestamp("modified_on");
	            }
	            // The record is not present because the record is modified by other
	            // person and the
	            // modification is done on the value of the primary key.
	            else {
	                modified = true;
	                return true;
	            }
	            if (newlastModified.getTime() != p_oldlastModified) {
	                modified = true;
	            }
	        }// end of try
	        catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[isRecordModified]", "", "",
	                "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        }// end of catch
	        catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[isRecordModified]", "", "",
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
	                _log.debug(methodName, "Exititng:modified=" + modified);
	            }
	        }// end of finally
	        return modified;
	    }// end recordModified

	    /**
	     * Method deleteTransferRuleProductMapping.
	     * This method is used to delete the transferrule and product information.
	     * 
	     * @param p_con
	     *            Connection
	     * @param p_transferRuleID
	     *            String
	     * @return int
	     * @throws BTSLBaseException
	     */
	    private int deleteTransferRuleProductMapping(Connection p_con, String p_transferRuleID) throws BTSLBaseException {
	        final String methodName = "deleteTransferRuleProductMapping";
	        StringBuilder loggerValue = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered:p_transferRuleID=");
	        	loggerValue.append(p_transferRuleID);
	            _log.debug(methodName, loggerValue);
	        }
	        PreparedStatement pstmtDelete = null;
	        int deleteCount = 0;
	        try {
	            final StringBuffer insertQuery = new StringBuffer("DELETE FROM chnl_transfer_rules_products WHERE transfer_rule_id=? ");
	            final String query = insertQuery.toString();
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Query=" + query);
	            }
	            pstmtDelete = p_con.prepareStatement(query);
	            pstmtDelete.setString(1, p_transferRuleID);
	            deleteCount = pstmtDelete.executeUpdate();
	            if (deleteCount == 0) {
	                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	            }
	        }// end of try
	        catch (BTSLBaseException e) {
	            _log.error(methodName, "BTSLBaseException" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[deleteTransferRuleProductMapping]", "", "", "", "BTSLBaseException:" + e.getMessage());
	            throw e;
	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[deleteTransferRuleProductMapping]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[deleteTransferRuleProductMapping]", "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } finally {
	            try {
	                if (pstmtDelete != null) {
	                    pstmtDelete.close();
	                }
	            } catch (Exception ex) {
	                _log.errorTrace(methodName, ex);
	            }
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Exiting deleteCount=" + deleteCount);
	            }
	        }
	        return deleteCount;
	    }

	    /**
	     * Method deleteChannelTransferRule.
	     * This method for the deletion(soft delete) of the transfer rule.
	     * 
	     * @param p_con
	     *            Connection
	     * @param p_channelTransferRuleVO
	     *            ChannelTransferRuleVO
	     * @return int
	     * @throws BTSLBaseException
	     */
	    public int deleteChannelTransferRule(Connection p_con, ChannelTransferRuleVO p_channelTransferRuleVO) throws BTSLBaseException {
	        final String methodName = "deleteChannelTransferRule";
	        StringBuilder loggerValue = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered:p_channelTransferRuleVO=");
	        	loggerValue.append(p_channelTransferRuleVO);
	            _log.debug(methodName, loggerValue);
	        }
	        PreparedStatement pstmtDelete = null;
	        int deleteCount = 0;
	        try {
	            final StringBuffer insertQuery = new StringBuffer("DELETE FROM chnl_transfer_rules WHERE transfer_rule_id=?");
	            final String query = insertQuery.toString();
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Query=" + query);
	            }
	            pstmtDelete = p_con.prepareStatement(query);
	            int i = 1;
	            pstmtDelete.setString(i, p_channelTransferRuleVO.getTransferRuleID());
	            i++;
	            // for the checking is the record modified during the transaction.
	            final boolean modified = this.isRecordModified(p_con, p_channelTransferRuleVO.getLastModifiedTime(), p_channelTransferRuleVO.getTransferRuleID());
	            if (modified) {
	                throw new BTSLBaseException(this, methodName, "error.modify.true");
	            }
	            deleteCount = pstmtDelete.executeUpdate();
	        } catch (BTSLBaseException be) {
	            throw be;
	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[deleteChannelTransferRule]",
	                "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[deleteChannelTransferRule]",
	                "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } finally {
	            try {
	                if (pstmtDelete != null) {
	                    pstmtDelete.close();
	                }
	            } catch (Exception ex) {
	                _log.errorTrace(methodName, ex);
	            }
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Exiting deleteCount=" + deleteCount);
	            }
	        }
	        return deleteCount;
	    }

	    /**
	     * Load the categories with associated transfer rules for FOC Transfer
	     * 
	     * @param p_con
	     *            Connection
	     * @param p_networkCode
	     *            String
	     * @param p_fromCategory
	     *            String
	     * @param p_focAllowed
	     *            String
	     * @param p_transferType
	     *            String
	     * 
	     * @return ArrayList
	     * @throws BTSLBaseException
	     */
	    public ArrayList loadTransferRulesCategoryListForFOC(Connection p_con, String p_networkCode, String p_fromCategory, String p_focAllowed, String p_transferType) throws BTSLBaseException {

	        final String methodName = "loadTransferRulesCategoryListForFOC";
	        StringBuilder loggerValue = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	loggerValue.setLength(0);
	        	loggerValue.append("Entered  From Category Code ");
	        	loggerValue.append(p_fromCategory);
	        	loggerValue.append(" Network Code= ");
	        	loggerValue.append(p_networkCode);
	        	loggerValue.append(" FOC Allowed=");
	        	loggerValue.append(p_focAllowed);
	        	loggerValue.append(" Transfer Type=");
	        	loggerValue.append(p_transferType);
	            _log.debug(methodName,loggerValue);
	        }
	        PreparedStatement pstmtSelect = null;
	        ResultSet rsSelect = null;
	        final StringBuffer strBuff = new StringBuffer(" SELECT ctr.domain_code, ctr.to_category, ctr.parent_association_allowed, ");
	        strBuff.append(" ctr.direct_transfer_allowed, ctr.transfer_chnl_bypass_allowed,ctr.withdraw_allowed, ");
	        strBuff.append(" ctr.withdraw_chnl_bypass_allowed, ctr.return_allowed, ctr.return_chnl_bypass_allowed, ");
	        strBuff.append(" ctr.approval_required, ctr.first_approval_limit, ctr.second_approval_limit, ");
	        strBuff.append(" ctr.transfer_type, ctr.transfer_allowed, ctr.foc_transfer_type, ctr.foc_allowed , ");
	        strBuff.append(" c.category_name,ctr.uncntrl_transfer_allowed,ctr.restricted_msisdn_access, ctr.restricted_recharge_allowed  ");
	        strBuff.append(" FROM chnl_transfer_rules ctr ,categories c ");
	        strBuff.append(" WHERE ctr.from_category = ? AND ctr.network_code=?  AND ");
	        strBuff.append(" ctr.status = 'Y' AND ctr.to_category = c.category_code ");
	        strBuff.append(" AND ctr.foc_allowed = ? AND ctr.type = ? ORDER BY  c.sequence_no ");
	        final String sqlSelect = strBuff.toString();

	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
	        }
	        final ArrayList arrayList = new ArrayList();
	        try {
	            pstmtSelect = p_con.prepareStatement(sqlSelect);
	            int i = 1;
	            pstmtSelect.setString(i, p_fromCategory);
	            i++;
	            pstmtSelect.setString(i, p_networkCode);
	            i++;
	            pstmtSelect.setString(i, p_focAllowed);
	            i++;
	            pstmtSelect.setString(i, p_transferType);
	            i++;
	            rsSelect = pstmtSelect.executeQuery();
	            ChannelTransferRuleVO rulesVO = null;
	            while (rsSelect.next()) {

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
	                arrayList.add(rulesVO);
	            }

	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException : " + sqe);
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[loadTransferRulesCategoryListForFOC]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception ex) {
	            _log.error(methodName, "Exception : " + ex);
	            _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[loadTransferRulesCategoryListForFOC]", "", "", "", "Exception:" + ex.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } finally {
	            try {
	                if (rsSelect != null) {
	                    rsSelect.close();
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
	                _log.debug(methodName, "Exiting:  arrayList Size =" + arrayList.size());
	            }
	        }
	        return arrayList;
	    }

	    /**
	     * Method loadChannelTransferRuleVOList
	     * This method loads the list of the transfer rules in the given network for
	     * the specified form domain and the
	     * to domain code of the specified rule type.
	     * 
	     * @param p_con
	     * @param p_networkCode
	     * @param p_fromDomainCode
	     * @param p_toDomainCode
	     * @param p_ruleType
	     * @return
	     * @throws BTSLBaseException
	     *             ArrayList
	     */
	    public ArrayList loadChannelTransferRuleVOList(Connection p_con, String p_networkCode, String p_fromDomainCode, String p_toDomainCode, String p_ruleType) throws BTSLBaseException {
	        final String methodName = "loadChannelTransferRuleVOList";
	        StringBuilder logger = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	logger.setLength(0);
	        	logger.append("Entered:p_networkCode=");
	        	logger.append(p_networkCode);
	        	logger.append(",p_fromDomainCode=");
	        	logger.append(p_fromDomainCode);
	        	logger.append(",p_toDomainCode =");
	        	logger.append(p_toDomainCode);
	        	logger.append(", p_ruleType=");
	        	logger.append(p_ruleType);
	            _log.debug(methodName,logger);
	        }
	        final ArrayList channelTransferRuleVOList = new ArrayList();
	        PreparedStatement pstmtSelect = null;
	        ResultSet rs = null;
	        try {
	            final StringBuffer selectQuery = new StringBuffer("SELECT  CAT1.sequence_no from_seq_no,CAT2.sequence_no to_seq_no,network_code, ");
	            selectQuery.append("CHNLTRF.domain_code,transfer_rule_id, from_category,CAT1.category_name from_name, ");
	            selectQuery.append("to_category,CAT2.category_name to_name,parent_association_allowed,direct_transfer_allowed, ");
	            selectQuery.append("transfer_chnl_bypass_allowed, withdraw_allowed,withdraw_chnl_bypass_allowed,return_allowed, ");
	            selectQuery.append("return_chnl_bypass_allowed,approval_required, first_approval_limit,second_approval_limit, ");
	            selectQuery.append("transfer_type, transfer_allowed, foc_transfer_type, foc_allowed,direct_payout_allowed,restricted_msisdn_access, CHNLTRF.modified_on, ");
	            selectQuery.append("CHNLTRF.type,CHNLTRF.uncntrl_transfer_allowed,CAT2.uncntrl_transfer_allowed to_cat_uncntrl, ");
	            selectQuery.append("to_domain_code, uncntrl_transfer_level, cntrl_transfer_level, ");
	            selectQuery.append("fixed_transfer_level, fixed_transfer_category, uncntrl_return_allowed, ");
	            selectQuery.append("uncntrl_return_level, cntrl_return_level, fixed_return_level, ");
	            selectQuery.append("fixed_return_category, uncntrl_withdraw_allowed, uncntrl_withdraw_level, ");
	            selectQuery.append("cntrl_withdraw_level, fixed_withdraw_level, fixed_withdraw_category, restricted_recharge_allowed ");
	            selectQuery.append("FROM chnl_transfer_rules CHNLTRF,categories CAT1,categories CAT2 ");
	            selectQuery.append("WHERE CHNLTRF.network_code=? ");
	            selectQuery.append("AND CHNLTRF.domain_code=? AND CHNLTRF.to_domain_code=? ");
	            selectQuery.append("AND CHNLTRF.status=? ");
	            selectQuery.append("AND CHNLTRF.type=? AND CHNLTRF.from_category=CAT1.category_code ");
	            selectQuery.append("AND CHNLTRF.to_category=CAT2.category_code ");
	            selectQuery.append("ORDER BY CHNLTRF.domain_code, CAT1.sequence_no, CAT2.sequence_no ");
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Query=" + selectQuery);
	            }
	            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
	            int i = 0;
	            ++i;
	            pstmtSelect.setString(i, p_networkCode);
	            ++i;
	            pstmtSelect.setString(i, p_fromDomainCode);
	            ++i;
	            pstmtSelect.setString(i, p_toDomainCode);
	            ++i;
	            pstmtSelect.setString(i, PretupsI.CHNL_TRANSFER_RULE_STATUS_ACTIVE);
	            ++i;
	            pstmtSelect.setString(i, p_ruleType);
	            rs = pstmtSelect.executeQuery();
	            ChannelTransferRuleVO channelTransferRuleVO = null;
	            while (rs.next()) {
	                channelTransferRuleVO = new ChannelTransferRuleVO();
	                channelTransferRuleVO.setNetworkCode(rs.getString("network_code"));
	                channelTransferRuleVO.setDomainCode(rs.getString("domain_code"));
	                channelTransferRuleVO.setTransferRuleID(rs.getString("transfer_rule_id"));
	                channelTransferRuleVO.setFromCategory(rs.getString("from_category"));
	                channelTransferRuleVO.setFromCategoryDes(rs.getString("from_name"));
	                channelTransferRuleVO.setFromSeqNo(rs.getInt("from_seq_no"));
	                channelTransferRuleVO.setToCategory(rs.getString("to_category"));
	                channelTransferRuleVO.setToCategoryDes(rs.getString("to_name"));
	                channelTransferRuleVO.setToSeqNo(rs.getInt("to_seq_no"));
	                channelTransferRuleVO.setParentAssocationAllowed(rs.getString("parent_association_allowed"));
	                channelTransferRuleVO.setDirectTransferAllowed(rs.getString("direct_transfer_allowed"));
	                channelTransferRuleVO.setTransferChnlBypassAllowed(rs.getString("transfer_chnl_bypass_allowed"));
	                channelTransferRuleVO.setWithdrawAllowed(rs.getString("withdraw_allowed"));
	                channelTransferRuleVO.setWithdrawChnlBypassAllowed(rs.getString("withdraw_chnl_bypass_allowed"));
	                channelTransferRuleVO.setReturnAllowed(rs.getString("return_allowed"));
	                channelTransferRuleVO.setReturnChnlBypassAllowed(rs.getString("return_chnl_bypass_allowed"));
	                channelTransferRuleVO.setApprovalRequired(rs.getString("approval_required"));
	                channelTransferRuleVO.setFirstApprovalLimit(rs.getLong("first_approval_limit"));
	                channelTransferRuleVO.setSecondApprovalLimit(rs.getLong("second_approval_limit"));
	                channelTransferRuleVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
	                channelTransferRuleVO.setType(rs.getString("type"));
	                channelTransferRuleVO.setUncntrlTransferAllowed(rs.getString("uncntrl_transfer_allowed"));
	                channelTransferRuleVO.setUncntrlTransferAllowedTmp(rs.getString("to_cat_uncntrl"));
	                channelTransferRuleVO.setTransferType(rs.getString("transfer_type"));
	                channelTransferRuleVO.setTransferAllowed(rs.getString("transfer_allowed"));
	                channelTransferRuleVO.setFocAllowed(rs.getString("foc_allowed"));
	                channelTransferRuleVO.setFocTransferType(rs.getString("foc_transfer_type"));
	                channelTransferRuleVO.setDpAllowed(rs.getString("direct_payout_allowed"));
	                channelTransferRuleVO.setRestrictedMsisdnAccess(rs.getString("restricted_msisdn_access"));
	                channelTransferRuleVO.setRestrictedRechargeAccess(rs.getString("restricted_recharge_allowed"));

	                // new fields added in the table
	                channelTransferRuleVO.setToDomainCode(rs.getString("to_domain_code"));

	                channelTransferRuleVO.setUncntrlTransferLevel(rs.getString("uncntrl_transfer_level"));
	                channelTransferRuleVO.setCntrlTransferLevel(rs.getString("cntrl_transfer_level"));
	                channelTransferRuleVO.setFixedTransferLevel(rs.getString("fixed_transfer_level"));
	                channelTransferRuleVO.setFixedTransferCategory(rs.getString("fixed_transfer_category"));

	                channelTransferRuleVO.setUncntrlReturnAllowed(rs.getString("uncntrl_return_allowed"));
	                channelTransferRuleVO.setUncntrlReturnLevel(rs.getString("uncntrl_return_level"));
	                channelTransferRuleVO.setCntrlReturnLevel(rs.getString("cntrl_return_level"));
	                channelTransferRuleVO.setFixedReturnLevel(rs.getString("fixed_return_level"));
	                channelTransferRuleVO.setFixedReturnCategory(rs.getString("fixed_return_category"));

	                channelTransferRuleVO.setUncntrlWithdrawAllowed(rs.getString("uncntrl_withdraw_allowed"));
	                channelTransferRuleVO.setUncntrlWithdrawLevel(rs.getString("uncntrl_withdraw_level"));
	                channelTransferRuleVO.setCntrlWithdrawLevel(rs.getString("cntrl_withdraw_level"));
	                channelTransferRuleVO.setFixedWithdrawLevel(rs.getString("fixed_withdraw_level"));
	                channelTransferRuleVO.setFixedWithdrawCategory(rs.getString("fixed_withdraw_category"));
	                // ends here

	                channelTransferRuleVOList.add(channelTransferRuleVO);
	            }
	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[loadChannelTransferRuleVOList]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[loadChannelTransferRuleVOList]", "", "", "", "Exception:" + e.getMessage());
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
	                _log.debug(methodName, "Exiting:list size=" + channelTransferRuleVOList.size());
	            }
	        }
	        return channelTransferRuleVOList;
	    }

	    /**
	     * This method is used before modify the category it will search for
	     * transfer rule
	     * with parent association Y for the category.
	     * 
	     * This methos added for CR00043
	     * 
	     * @param p_con
	     *            Connection
	     * @param p_categoryVO
	     *            CategoryVO
	     * @return boolean
	     * @throws BTSLBaseException
	     */
	    public boolean isTransferRuleExists(Connection p_con, CategoryVO p_categoryVO) throws BTSLBaseException {

	        final String methodName = "isTransferRuleExists";
	        StringBuilder logger = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	logger.setLength(0);
	        	logger.append("Entered params  p_categoryVO::");
	        	logger.append(p_categoryVO);
	            _log.debug(methodName, logger);
	        }

	        PreparedStatement pstmtSelect = null;
	        ResultSet rs = null;
	        boolean found = false;
	        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM chnl_transfer_rules ");
	        sqlBuff.append("WHERE from_category=? AND type=? AND status='Y' AND parent_association_allowed=? ");
	        final String selectQuery = sqlBuff.toString();
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, "Select Query::" + selectQuery);
	        }

	        try {
	            pstmtSelect = p_con.prepareStatement(selectQuery);
	            pstmtSelect.setString(1, p_categoryVO.getCategoryCode().toUpperCase());
	            pstmtSelect.setString(2, PretupsI.TRANSFER_RULE_TYPE_CHANNEL);
	            pstmtSelect.setString(3, PretupsI.YES);
	            rs = pstmtSelect.executeQuery();
	            if (rs.next()) {
	                found = true;
	            }
	        } catch (SQLException sqle) {
	            _log.error(methodName, "SQLException " + sqle.getMessage());
	            _log.errorTrace(methodName, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[isTransferRuleExists]", "",
	                "", "", "SQL Exception:" + sqle.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception " + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[isTransferRuleExists]", "",
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
	            } catch (Exception e) {
	                _log.errorTrace(methodName, e);
	            }
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Exiting isExists found=" + found);
	            }
	        }
	        return found;
	    }

	    /**
	     * This method is used before modify the category it will search for
	     * transfer rule
	     * with uncontrol transfer allowed Y.
	     * 
	     * This methos added for CR00043
	     * 
	     * @param p_con
	     *            Connection
	     * @param p_categoryVO
	     *            CategoryVO
	     * @return boolean
	     * @throws BTSLBaseException
	     */
	    public boolean isUncontrolTransferAllowedTransferRuleExists(Connection p_con, CategoryVO p_categoryVO) throws BTSLBaseException {

	        final String methodName = "isUncontrolTransferAllowedTransferRuleExists";
	        StringBuilder logger = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	logger.setLength(0);
	        	logger.append("Entered params  p_categoryVO::");
	        	logger.append(p_categoryVO);
	            _log.debug(methodName, logger);
	        }

	        PreparedStatement pstmtSelect = null;
	        ResultSet rs = null;
	        boolean found = false;
	        final StringBuffer sqlBuff = new StringBuffer("SELECT 1 FROM chnl_transfer_rules ");
	        sqlBuff.append("WHERE uncntrl_transfer_allowed=? AND (to_category=? OR from_category=? ) AND status='Y' AND type=? ");
	        final String selectQuery = sqlBuff.toString();
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, "Select Query::" + selectQuery);
	        }

	        try {
	            pstmtSelect = p_con.prepareStatement(selectQuery);
	            pstmtSelect.setString(1, PretupsI.YES);
	            pstmtSelect.setString(2, p_categoryVO.getCategoryCode().toUpperCase());
	            pstmtSelect.setString(3, p_categoryVO.getCategoryCode().toUpperCase());
	            pstmtSelect.setString(4, PretupsI.TRANSFER_RULE_TYPE_CHANNEL);

	            rs = pstmtSelect.executeQuery();
	            if (rs.next()) {
	                found = true;
	            }
	        } catch (SQLException sqle) {
	            _log.error(methodName, "SQLException " + sqle.getMessage());
	            _log.errorTrace(methodName, sqle);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[isUncontrolTransferAllowedTransferRuleExists]", "", "", "", "SQL Exception:" + sqle.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception " + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[isUncontrolTransferAllowedTransferRuleExists]", "", "", "", "Exception:" + e.getMessage());
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
	                _log.debug(methodName, "Exiting isExists found=" + found);
	            }
	        }
	        return found;
	    }

	    /**
	     * Method loadTransferRuleVOList
	     * This method loads the list of the transfer rules in the given network for
	     * the specified form domain and the
	     * to domain code of the specified rule type.
	     * 
	     * @param p_con
	     * @param p_networkCode
	     * @param p_fromDomainCode
	     * @param p_toDomainCode
	     * @param p_ruleType
	     * @return ArrayList
	     * @throws BTSLBaseException
	     *             ArrayList
	     */
	    public ArrayList loadTransferRuleVOList(Connection p_con, String p_networkCode, String p_fromDomainCode, String p_toDomainCode, String p_ruleType) throws BTSLBaseException {
	        final String methodName = "loadTransferRuleVOList";
	        StringBuilder logger = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	logger.setLength(0);
	        	logger.append("Entered:p_networkCode=");
	        	logger.append(p_networkCode);
	        	logger.append(",p_fromDomainCode=");
	        	logger.append(p_fromDomainCode);
	        	logger.append(",p_toDomainCode =");
	        	logger.append(p_toDomainCode);
	        	logger.append(", p_ruleType=");
	        	logger.append(p_ruleType);
	            _log.debug(methodName, logger);
	        }
	        final ArrayList channelTransferRuleVOList = new ArrayList();
	        PreparedStatement pstmtSelect = null;
	        ResultSet rs = null;
	        try {
	            final StringBuffer selectQuery = new StringBuffer("SELECT from_category,to_category, ");
	            selectQuery.append("transfer_rule_id,CHNLTRF.status,CAT1.category_name from_name,CAT2.category_name to_name,CHNLTRF.uncntrl_transfer_allowed ");
	            selectQuery.append("FROM chnl_transfer_rules CHNLTRF,categories CAT1,categories CAT2 ");
	            selectQuery.append("WHERE CHNLTRF.network_code=? ");
	            selectQuery.append("AND CHNLTRF.domain_code=? AND CHNLTRF.to_domain_code=? ");
	            selectQuery.append("AND CHNLTRF.type=? AND CHNLTRF.from_category=CAT1.category_code ");
	            selectQuery.append("AND CHNLTRF.to_category=CAT2.category_code ");
	            selectQuery.append("ORDER BY CHNLTRF.domain_code, CAT1.sequence_no, CAT2.sequence_no ");
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Query=" + selectQuery);
	            }
	            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
	            int i = 0;
	            ++i;
	            pstmtSelect.setString(i, p_networkCode);
	            ++i;
	            pstmtSelect.setString(i, p_fromDomainCode);
	            ++i;
	            pstmtSelect.setString(i, p_toDomainCode);
	            ++i;
	            pstmtSelect.setString(i, p_ruleType);
	            rs = pstmtSelect.executeQuery();
	            ChannelTransferRuleVO channelTransferRuleVO = null;
	            while (rs.next()) {
	                channelTransferRuleVO = new ChannelTransferRuleVO();
	                channelTransferRuleVO.setTransferRuleID(rs.getString("transfer_rule_id"));
	                channelTransferRuleVO.setFromCategory(rs.getString("from_category"));
	                channelTransferRuleVO.setToCategory(rs.getString("to_category"));
	                channelTransferRuleVO.setFromCategoryDes(rs.getString("from_name"));
	                channelTransferRuleVO.setToCategoryDes(rs.getString("to_name"));
	                channelTransferRuleVO.setStatus(rs.getString("status"));
	                channelTransferRuleVO.setUncntrlTransferAllowed(rs.getString("uncntrl_transfer_allowed"));
	                // ends here
	                channelTransferRuleVOList.add(channelTransferRuleVO);
	            }
	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CTransferRuleDAO[loadTransferRuleVOList]", "", "",
	                "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CTransferRuleDAO[loadTransferRuleVOList]", "", "",
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
	                _log.debug(methodName, "Exiting:list size=" + channelTransferRuleVOList.size());
	            }
	        }
	        return channelTransferRuleVOList;
	    }

	    /**
	     * Method setStatus
	     * This method updates the status in the database.
	     * 
	     * @param p_con
	     * @param String
	     *            p_transferRuleID
	     * @return int updateCount
	     * @throws BTSLBaseException
	     */
	    public int setStatus(Connection p_con, String p_status, String p_transferRuleID) throws BTSLBaseException {
	        final String methodName = "setStatus";
	        StringBuilder logger = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	logger.setLength(0);
	        	logger.append("Entered:p_status=");
	        	logger.append(p_status);
	        	logger.append(",p_transferRuleID=");
	        	logger.append(p_transferRuleID);
	            _log.debug(methodName, logger);
	        }
	        int updateCount = 0;
	        PreparedStatement pstmtUpdate = null;
	        final StringBuffer strBuff = new StringBuffer(" UPDATE chnl_transfer_rules SET status=?");
	        strBuff.append(" WHERE transfer_rule_id =? ");

	        final String updateQuery = strBuff.toString();
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, "UpdateQuery sqlUpdate = " + updateQuery);
	        }
	        try {
	            pstmtUpdate = p_con.prepareStatement(updateQuery);
	            pstmtUpdate.setString(1, p_status);
	            pstmtUpdate.setString(2, p_transferRuleID);
	            updateCount = pstmtUpdate.executeUpdate();
	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CTransferRuleDAO[setStatus]", "", "", "",
	                "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "O2CTransferRuleDAO[setStatus]", "", "", "",
	                "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } finally {

	            try {
	                if (pstmtUpdate != null) {
	                    pstmtUpdate.close();
	                }
	            } catch (Exception ex) {
	                _log.errorTrace(methodName, ex);
	            }
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Exiting:updateCount=" + updateCount);
	            }
	        }
	        return updateCount;
	    }

	    /**
	     * Load the categories with associated transfer rules for FOC Transfer
	     * Method :loadTransferRulesCategoryListForDP
	     * 
	     * @author Lohit Audhkhasi
	     * @param p_con
	     *            Connection
	     * @param p_networkCode
	     *            String
	     * @param p_fromCategory
	     *            String
	     * @param p_transferType
	     *            String
	     * @return ArrayList
	     * @throws BTSLBaseException
	     */
	    // public ArrayList loadTransferRulesCategoryListForDP(Connection p_con,
	    // String p_networkCode, String p_fromCategory, String p_transferType)
	    // throws BTSLBaseException
	    public ArrayList loadTransferRulesCategoryListForDP(Connection p_con, String p_networkCode, String p_fromCategory, String p_dpAllowed, String p_transferType) throws BTSLBaseException {

	        final String methodName = "loadTransferRulesCategoryListForDP";
	        StringBuilder logger = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	logger.setLength(0);
	        	logger.append("Entered  From Category Code ");
	        	logger.append(p_fromCategory);
	        	logger.append(" Network Code ");
	        	logger.append(p_networkCode);
	        	logger.append(" Transfer Type=");
	        	logger.append(p_transferType);
	            _log.debug(methodName, logger);
	        }
	        PreparedStatement pstmtSelect = null;
	        ResultSet rsSelect = null;
	        final StringBuffer strBuff = new StringBuffer(" SELECT ctr.domain_code, ctr.to_category, ctr.parent_association_allowed, ");
	        strBuff.append(" ctr.direct_transfer_allowed, ctr.transfer_chnl_bypass_allowed,ctr.withdraw_allowed, ");
	        strBuff.append(" ctr.withdraw_chnl_bypass_allowed, ctr.return_allowed, ctr.return_chnl_bypass_allowed, ");
	        strBuff.append(" ctr.approval_required, ctr.first_approval_limit, ctr.second_approval_limit, ");
	        strBuff.append(" ctr.transfer_type, ctr.transfer_allowed, ctr.foc_transfer_type, ctr.direct_payout_allowed , ");
	        strBuff.append(" c.category_name,ctr.uncntrl_transfer_allowed,ctr.restricted_msisdn_access, ctr.restricted_recharge_allowed  ");
	        strBuff.append(" FROM chnl_transfer_rules ctr ,categories c ");
	        strBuff.append(" WHERE ctr.from_category = ? AND ctr.network_code=?  AND ");
	        strBuff.append(" ctr.status = 'Y' AND ctr.to_category = c.category_code ");
	        strBuff.append(" AND ctr.direct_payout_allowed = ? AND ctr.type = ? ORDER BY  c.sequence_no ");
	        final String sqlSelect = strBuff.toString();

	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
	        }
	        final ArrayList arrayList = new ArrayList();
	        try {
	            pstmtSelect = p_con.prepareStatement(sqlSelect);
	            int i = 1;
	            pstmtSelect.setString(i, p_fromCategory);
	            i++;
	            pstmtSelect.setString(i, p_networkCode);
	            i++;
	            pstmtSelect.setString(i, p_dpAllowed);
	            i++;
	            pstmtSelect.setString(i, p_transferType);
	            i++;
	            rsSelect = pstmtSelect.executeQuery();
	            ChannelTransferRuleVO rulesVO = null;
	            while (rsSelect.next()) {

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
	                rulesVO.setDpAllowed(rsSelect.getString("direct_payout_allowed"));
	                rulesVO.setFocTransferType(rsSelect.getString("foc_transfer_type"));
	                rulesVO.setRestrictedMsisdnAccess(rsSelect.getString("restricted_msisdn_access"));
	                rulesVO.setRestrictedRechargeAccess(rsSelect.getString("restricted_recharge_allowed"));
	                arrayList.add(rulesVO);
	            }

	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException : " + sqe);
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[loadTransferRulesCategoryListForFOC]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception ex) {
	            _log.error(methodName, "Exception : " + ex);
	            _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[loadTransferRulesCategoryListForFOC]", "", "", "", "Exception:" + ex.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } finally {
	            try {
	                if (rsSelect != null) {
	                    rsSelect.close();
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
	                _log.debug(methodName, "Exiting:  arrayList Size =" + arrayList.size());
	            }
	        }
	        return arrayList;
	    }

	    // //////////////////////////////////////////////////////
	    // added by nilesh : for O2C transfer rule : on 27/09/11
	    /**
	     * loading transfer rule details
	     * Method :loadChannelTransferRuleNewVOList
	     * 
	     * @author Nilesh kumar
	     * @param p_con
	     *            Connection
	     * @param p_networkCode
	     *            String
	     * @param p_fromDomainCode
	     *            String
	     * @param p_toDomainCode
	     *            String
	     * @param p_ruleType
	     *            String
	     * @return ArrayList
	     * @throws BTSLBaseException
	     */

	    public ArrayList loadChannelTransferRuleNewVOList(Connection p_con, String p_networkCode, String p_fromDomainCode, String p_toDomainCode, String p_status, String p_ruleType) throws BTSLBaseException {
	        final String methodName = "loadChannelTransferRuleNewVOList";
	        StringBuilder logger = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	logger.setLength(0);
	        	logger.append("Entered:p_networkCode=");
	        	logger.append(p_networkCode);
	        	logger.append(",p_fromDomainCode=");
	        	logger.append(p_fromDomainCode);
	        	logger.append(",p_toDomainCode =");
	        	logger.append(p_toDomainCode);
	        	logger.append(",p_status =");
	        	logger.append(p_status);
	        	logger.append(" p_ruleType=");
	        	logger.append(p_ruleType);
	            _log.debug(
	                methodName, logger);
	        }
	        final ArrayList channelTransferRuleVOList = new ArrayList();
	        PreparedStatement pstmtSelect = null;
	        ResultSet rs = null;
	        try {
	            final StringBuffer selectQuery = new StringBuffer("SELECT  CAT1.sequence_no from_seq_no,CAT2.sequence_no to_seq_no,network_code, ");
	            selectQuery.append("CHNLTRF.domain_code,transfer_rule_id, from_category,CAT1.category_name from_name, ");
	            selectQuery.append("to_category,CAT2.category_name to_name,parent_association_allowed,direct_transfer_allowed, ");
	            selectQuery.append("transfer_chnl_bypass_allowed, withdraw_allowed,withdraw_chnl_bypass_allowed,return_allowed, ");
	            selectQuery.append("return_chnl_bypass_allowed,approval_required, first_approval_limit,second_approval_limit, ");
	            selectQuery
	                .append("transfer_type, CHNLTRF.transfer_allowed, foc_transfer_type, foc_allowed,direct_payout_allowed,restricted_msisdn_access, CHNLTRF.modified_on, ");
	            selectQuery.append("CHNLTRF.type,CHNLTRF.uncntrl_transfer_allowed,CHNLTRF.status,CAT2.uncntrl_transfer_allowed to_cat_uncntrl, ");
	            selectQuery.append("to_domain_code, uncntrl_transfer_level, cntrl_transfer_level, ");
	            selectQuery.append("fixed_transfer_level, fixed_transfer_category, uncntrl_return_allowed, ");
	            selectQuery.append("uncntrl_return_level, cntrl_return_level, fixed_return_level, ");
	            selectQuery.append("fixed_return_category, uncntrl_withdraw_allowed, uncntrl_withdraw_level, ");
	            selectQuery.append("cntrl_withdraw_level, fixed_withdraw_level, fixed_withdraw_category, restricted_recharge_allowed,L.lookup_name,L.lookup_code ");
	            selectQuery.append("FROM chnl_transfer_rules CHNLTRF,categories CAT1,categories CAT2, lookups L ");
	            selectQuery.append("WHERE CHNLTRF.network_code=? ");
	            selectQuery.append("AND CHNLTRF.domain_code=? AND CHNLTRF.to_domain_code=? ");
	            // new
	            selectQuery.append("AND L.lookup_type=? ");
	            selectQuery.append("AND L.lookup_code=CHNLTRF.status ");
	            //
	            // selectQuery.append("AND CHNLTRF.status=? ");
	            selectQuery.append(" AND CHNLTRF.status IN (" + p_status + ") ");

	            selectQuery.append("AND CHNLTRF.type=? AND CHNLTRF.from_category=CAT1.category_code ");
	            selectQuery.append("AND CHNLTRF.to_category=CAT2.category_code ");
	            selectQuery.append("ORDER BY CHNLTRF.domain_code, CAT1.sequence_no, CAT2.sequence_no ");
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Query=" + selectQuery);
	            }
	            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
	            int i = 0;
	            ++i;
	            pstmtSelect.setString(i, p_networkCode);
	            ++i;
	            pstmtSelect.setString(i, p_fromDomainCode);
	            ++i;
	            pstmtSelect.setString(i, p_toDomainCode);
	            ++i;
	            pstmtSelect.setString(i, PretupsI.TRANS_TYPE);
	            // pstmtSelect.setString(++i,
	            // PretupsI.CHNL_TRANSFER_RULE_STATUS_ACTIVE);
	            ++i;
	            pstmtSelect.setString(i, p_ruleType);
	            rs = pstmtSelect.executeQuery();
	            ChannelTransferRuleVO channelTransferRuleVO = null;
	            while (rs.next()) {
	                channelTransferRuleVO = new ChannelTransferRuleVO();
	                channelTransferRuleVO.setNetworkCode(rs.getString("network_code"));
	                channelTransferRuleVO.setDomainCode(rs.getString("domain_code"));
	                channelTransferRuleVO.setTransferRuleID(rs.getString("transfer_rule_id"));
	                channelTransferRuleVO.setFromCategory(rs.getString("from_category"));
	                channelTransferRuleVO.setFromCategoryDes(rs.getString("from_name"));
	                channelTransferRuleVO.setFromSeqNo(rs.getInt("from_seq_no"));
	                channelTransferRuleVO.setToCategory(rs.getString("to_category"));
	                channelTransferRuleVO.setToCategoryDes(rs.getString("to_name"));
	                channelTransferRuleVO.setToSeqNo(rs.getInt("to_seq_no"));

	                channelTransferRuleVO.setParentAssocationAllowed(rs.getString("parent_association_allowed"));
	                channelTransferRuleVO.setDirectTransferAllowed(rs.getString("direct_transfer_allowed"));
	                channelTransferRuleVO.setTransferChnlBypassAllowed(rs.getString("transfer_chnl_bypass_allowed"));
	                channelTransferRuleVO.setWithdrawAllowed(rs.getString("withdraw_allowed"));
	                channelTransferRuleVO.setWithdrawChnlBypassAllowed(rs.getString("withdraw_chnl_bypass_allowed"));
	                channelTransferRuleVO.setReturnAllowed(rs.getString("return_allowed"));
	                channelTransferRuleVO.setReturnChnlBypassAllowed(rs.getString("return_chnl_bypass_allowed"));
	                channelTransferRuleVO.setApprovalRequired(rs.getString("approval_required"));
	                channelTransferRuleVO.setFirstApprovalLimit(rs.getLong("first_approval_limit"));
	                channelTransferRuleVO.setSecondApprovalLimit(rs.getLong("second_approval_limit"));
	                channelTransferRuleVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
	                channelTransferRuleVO.setType(rs.getString("type"));
	                channelTransferRuleVO.setUncntrlTransferAllowed(rs.getString("uncntrl_transfer_allowed"));
	                // channelTransferRuleVO.setStatus(rs.getString("status"));
	                channelTransferRuleVO.setStatus(rs.getString("lookup_code"));
	                channelTransferRuleVO.setStatusDesc(rs.getString("lookup_name"));
	                channelTransferRuleVO.setUncntrlTransferAllowedTmp(rs.getString("to_cat_uncntrl"));
	                channelTransferRuleVO.setTransferType(rs.getString("transfer_type"));
	                channelTransferRuleVO.setTransferAllowed(rs.getString("transfer_allowed"));
	                channelTransferRuleVO.setFocAllowed(rs.getString("foc_allowed"));
	                channelTransferRuleVO.setFocTransferType(rs.getString("foc_transfer_type"));
	                channelTransferRuleVO.setDpAllowed(rs.getString("direct_payout_allowed"));
	                channelTransferRuleVO.setRestrictedMsisdnAccess(rs.getString("restricted_msisdn_access"));
	                channelTransferRuleVO.setRestrictedRechargeAccess(rs.getString("restricted_recharge_allowed"));

	                // new fields added in the table
	                channelTransferRuleVO.setToDomainCode(rs.getString("to_domain_code"));

	                channelTransferRuleVO.setUncntrlTransferLevel(rs.getString("uncntrl_transfer_level"));
	                channelTransferRuleVO.setCntrlTransferLevel(rs.getString("cntrl_transfer_level"));
	                channelTransferRuleVO.setFixedTransferLevel(rs.getString("fixed_transfer_level"));
	                channelTransferRuleVO.setFixedTransferCategory(rs.getString("fixed_transfer_category"));

	                channelTransferRuleVO.setUncntrlReturnAllowed(rs.getString("uncntrl_return_allowed"));
	                channelTransferRuleVO.setUncntrlReturnLevel(rs.getString("uncntrl_return_level"));
	                channelTransferRuleVO.setCntrlReturnLevel(rs.getString("cntrl_return_level"));
	                channelTransferRuleVO.setFixedReturnLevel(rs.getString("fixed_return_level"));
	                channelTransferRuleVO.setFixedReturnCategory(rs.getString("fixed_return_category"));

	                channelTransferRuleVO.setUncntrlWithdrawAllowed(rs.getString("uncntrl_withdraw_allowed"));
	                channelTransferRuleVO.setUncntrlWithdrawLevel(rs.getString("uncntrl_withdraw_level"));
	                channelTransferRuleVO.setCntrlWithdrawLevel(rs.getString("cntrl_withdraw_level"));
	                channelTransferRuleVO.setFixedWithdrawLevel(rs.getString("fixed_withdraw_level"));
	                channelTransferRuleVO.setFixedWithdrawCategory(rs.getString("fixed_withdraw_category"));
	                // ends here

	                channelTransferRuleVOList.add(channelTransferRuleVO);
	            }
	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[loadChannelTransferRuleVOList]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, "loadChannelTransferRuleVOList", "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[loadChannelTransferRuleVOList]", "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, "loadChannelTransferRuleVOList", "error.general.processing");
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
	                _log.debug("loadChannelTransferRuleVOList", "Exiting:list size=" + channelTransferRuleVOList.size());
	            }
	        }
	        return channelTransferRuleVOList;
	    }

	    /**
	     * Method suspendChannelTransferRule.
	     * This method for the suspension Request of the transfer rule.
	     * 
	     * @author nilesh.kumar
	     * @param p_con
	     *            Connection
	     * @param p_channelTransferRuleVO
	     *            ChannelTransferRuleVO
	     * @return int
	     * @throws BTSLBaseException
	     */
	    public int suspendRequestChannelTransferRule(Connection p_con, ChannelTransferRuleVO p_channelTransferRuleVO) throws BTSLBaseException {
	        final String methodName = "suspendRequestChannelTransferRule";
	        StringBuilder logger = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	logger.setLength(0);
	        	logger.append("Entered:p_channelTransferRuleVO=");
	        	logger.append(p_channelTransferRuleVO);
	            _log.debug(methodName, logger);
	        }
	        PreparedStatement pstmtSuspendRequest = null;
	        int suspendRequestCount = 0;
	        try {
	            final StringBuffer insertQuery = new StringBuffer("UPDATE chnl_transfer_rules set status=?,previous_status=? WHERE transfer_rule_id=?");
	            final String query = insertQuery.toString();
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Query=" + query);
	            }
	            pstmtSuspendRequest = p_con.prepareStatement(query);
	            int i = 1;
	            pstmtSuspendRequest.setString(i, PretupsI.CHNL_TRANSFER_RULE_STATUS_SUSPEND_REQUEST);
	            i++;
	            pstmtSuspendRequest.setString(i, p_channelTransferRuleVO.getStatus());
	            i++;
	            pstmtSuspendRequest.setString(i, p_channelTransferRuleVO.getTransferRuleID());
	            i++;
	            // for the checking is the record modified during the transaction.
	            final boolean modified = this.isRecordModified(p_con, p_channelTransferRuleVO.getLastModifiedTime(), p_channelTransferRuleVO.getTransferRuleID());
	            if (modified) {
	                throw new BTSLBaseException(this, methodName, "error.modify.true");
	            }
	            suspendRequestCount = pstmtSuspendRequest.executeUpdate();
	        } catch (BTSLBaseException be) {
	            throw be;
	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[deleteChannelTransferRule]",
	                "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[deleteChannelTransferRule]",
	                "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } finally {
	            try {
	                if (pstmtSuspendRequest != null) {
	                    pstmtSuspendRequest.close();
	                }
	            } catch (Exception ex) {
	                _log.errorTrace(methodName, ex);
	            }
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Exiting suspendRequestCount=" + suspendRequestCount);
	            }
	        }
	        return suspendRequestCount;
	    }

	    /**
	     * Method suspendChannelTransferRule.
	     * This method for the suspension Request of the transfer rule.
	     * 
	     * @author nilesh.kumar
	     * @param p_con
	     *            Connection
	     * @param p_channelTransferRuleVO
	     *            ChannelTransferRuleVO
	     * @return int
	     * @throws BTSLBaseException
	     */
	    public int resumeRequestChannelTransferRule(Connection p_con, ChannelTransferRuleVO p_channelTransferRuleVO) throws BTSLBaseException {
	        final String methodName = "resumeRequestChannelTransferRule";
	        StringBuilder logger = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	logger.setLength(0);
	        	logger.append("Entered:p_channelTransferRuleVO=");
	        	logger.append(p_channelTransferRuleVO);
	            _log.debug(methodName, "Entered:p_channelTransferRuleVO=" + p_channelTransferRuleVO);
	        }
	        PreparedStatement pstmtSuspendRequest = null;
	        int suspendRequestCount = 0;
	        try {
	            final StringBuffer insertQuery = new StringBuffer("UPDATE chnl_transfer_rules set status=?,previous_status=?  WHERE transfer_rule_id=?");
	            final String query = insertQuery.toString();
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Query=" + query);
	            }
	            pstmtSuspendRequest = p_con.prepareStatement(query);
	            int i = 1;
	            pstmtSuspendRequest.setString(i, PretupsI.CHNL_TRANSFER_RULE_STATUS_RESUME_REQUEST);
	            i++;
	            pstmtSuspendRequest.setString(i, p_channelTransferRuleVO.getPreviousStatus());
	            i++;
	            pstmtSuspendRequest.setString(i, p_channelTransferRuleVO.getTransferRuleID());
	            i++;
	            // for the checking is the record modified during the transaction.
	            final boolean modified = this.isRecordModified(p_con, p_channelTransferRuleVO.getLastModifiedTime(), p_channelTransferRuleVO.getTransferRuleID());
	            if (modified) {
	                throw new BTSLBaseException(this, methodName, "error.modify.true");
	            }
	            suspendRequestCount = pstmtSuspendRequest.executeUpdate();
	        } catch (BTSLBaseException be) {
	            throw be;
	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[deleteChannelTransferRule]",
	                "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[deleteChannelTransferRule]",
	                "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } finally {
	            try {
	                if (pstmtSuspendRequest != null) {
	                    pstmtSuspendRequest.close();
	                }
	            } catch (Exception ex) {
	                _log.errorTrace(methodName, ex);
	            }
	            if (_log.isDebugEnabled()) {
	                _log.debug("suspendRequestChannelTransferRule", "Exiting suspendRequestCount=" + suspendRequestCount);
	            }
	        }
	        return suspendRequestCount;
	    }

	    // added by nilesh : for O2C transfer rule : on 27/09/11
	    /**
	     * loading transfer rule details
	     * Method :loadChannelTransferRuleNewVOList
	     * 
	     * @author Nilesh kumar
	     * @param p_con
	     *            Connection
	     * @param p_networkCode
	     *            String
	     * @param p_fromDomainCode
	     *            String
	     * @param p_toDomainCode
	     *            String
	     * @param p_ruleType
	     *            String
	     * @return ArrayList
	     * @throws BTSLBaseException
	     */

	    public ArrayList loadApprovalChannelTransferRuleNewVOList(Connection p_con, String p_fromCategoryCode, String p_status, String p_ruleType) throws BTSLBaseException {
	        final String methodName = "loadApprovalChannelTransferRuleNewVOList";
	        StringBuilder logger = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	logger.setLength(0);
	        	logger.append("Entered:p_fromCategoryCode=");
	        	logger.append(p_fromCategoryCode);
	        	logger.append(",p_status=");
	        	logger.append(p_status);
	        	logger.append(" p_ruleType=");
	        	logger.append(p_ruleType);
	            _log.debug(methodName, "Entered:p_fromCategoryCode=" + p_fromCategoryCode + ",p_status=" + p_status + " p_ruleType=" + p_ruleType);
	        }
	        final ArrayList channelTransferRuleVOList = new ArrayList();
	        PreparedStatement pstmtSelect = null;
	        ResultSet rs = null;
	        try {
	            final StringBuffer selectQuery = new StringBuffer("SELECT  CAT1.sequence_no from_seq_no,CAT2.sequence_no to_seq_no,network_code, ");
	            selectQuery.append("CHNLTRF.domain_code,transfer_rule_id, from_category,CAT1.category_name from_name, ");
	            selectQuery.append("to_category,CAT2.category_name to_name,parent_association_allowed,direct_transfer_allowed, ");
	            selectQuery.append("transfer_chnl_bypass_allowed, withdraw_allowed,withdraw_chnl_bypass_allowed,return_allowed, ");
	            selectQuery.append("return_chnl_bypass_allowed,approval_required, first_approval_limit,second_approval_limit, ");
	            selectQuery.append("transfer_type, transfer_allowed, foc_transfer_type, foc_allowed,direct_payout_allowed,restricted_msisdn_access, CHNLTRF.modified_on, ");
	            selectQuery.append("CHNLTRF.type,CHNLTRF.uncntrl_transfer_allowed,CHNLTRF.status,CAT2.uncntrl_transfer_allowed to_cat_uncntrl, ");
	            selectQuery.append("to_domain_code, uncntrl_transfer_level, cntrl_transfer_level, ");
	            selectQuery.append("fixed_transfer_level, fixed_transfer_category, uncntrl_return_allowed, ");
	            selectQuery.append("uncntrl_return_level, cntrl_return_level, fixed_return_level, ");
	            selectQuery.append("fixed_return_category, uncntrl_withdraw_allowed, uncntrl_withdraw_level, ");
	            selectQuery.append("cntrl_withdraw_level, fixed_withdraw_level, fixed_withdraw_category, restricted_recharge_allowed,L.lookup_code,L.lookup_name,CHNLTRF.previous_status ");
	            selectQuery.append("FROM chnl_transfer_rules CHNLTRF,categories CAT1,categories CAT2, lookups L ");
	            selectQuery.append("WHERE CHNLTRF.from_category=? ");
	            selectQuery.append("AND CHNLTRF.type=? ");
	            selectQuery.append("AND L.lookup_type=? ");
	            selectQuery.append("AND L.lookup_code=CHNLTRF.status ");
	            selectQuery.append(" AND CHNLTRF.status IN (" + p_status + ") ");
	            selectQuery.append("AND CHNLTRF.from_category=CAT1.category_code ");
	            selectQuery.append("AND CHNLTRF.to_category=CAT2.category_code ");
	            selectQuery.append("ORDER BY CHNLTRF.domain_code, CAT1.sequence_no, CAT2.sequence_no ");
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Query=" + selectQuery);
	            }
	            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
	            int i = 0;
	            ++i;
	            pstmtSelect.setString(i, p_fromCategoryCode);
	            ++i;
	            pstmtSelect.setString(i, p_ruleType);
	            ++i;
	            pstmtSelect.setString(i, PretupsI.TRANS_TYPE);
	            rs = pstmtSelect.executeQuery();
	            ChannelTransferRuleVO channelTransferRuleVO = null;
	            while (rs.next()) {
	                channelTransferRuleVO = new ChannelTransferRuleVO();
	                channelTransferRuleVO.setNetworkCode(rs.getString("network_code"));
	                channelTransferRuleVO.setDomainCode(rs.getString("domain_code"));
	                channelTransferRuleVO.setTransferRuleID(rs.getString("transfer_rule_id"));
	                channelTransferRuleVO.setFromCategory(rs.getString("from_category"));
	                channelTransferRuleVO.setFromCategoryDes(rs.getString("from_name"));
	                channelTransferRuleVO.setFromSeqNo(rs.getInt("from_seq_no"));
	                channelTransferRuleVO.setToCategory(rs.getString("to_category"));
	                channelTransferRuleVO.setToCategoryDes(rs.getString("to_name"));
	                channelTransferRuleVO.setToSeqNo(rs.getInt("to_seq_no"));
	                channelTransferRuleVO.setParentAssocationAllowed(rs.getString("parent_association_allowed"));
	                channelTransferRuleVO.setDirectTransferAllowed(rs.getString("direct_transfer_allowed"));
	                channelTransferRuleVO.setTransferChnlBypassAllowed(rs.getString("transfer_chnl_bypass_allowed"));
	                channelTransferRuleVO.setWithdrawAllowed(rs.getString("withdraw_allowed"));
	                channelTransferRuleVO.setWithdrawChnlBypassAllowed(rs.getString("withdraw_chnl_bypass_allowed"));
	                channelTransferRuleVO.setReturnAllowed(rs.getString("return_allowed"));
	                channelTransferRuleVO.setReturnChnlBypassAllowed(rs.getString("return_chnl_bypass_allowed"));
	                channelTransferRuleVO.setApprovalRequired(rs.getString("approval_required"));
	                channelTransferRuleVO.setFirstApprovalLimit(rs.getLong("first_approval_limit"));
	                channelTransferRuleVO.setSecondApprovalLimit(rs.getLong("second_approval_limit"));
	                channelTransferRuleVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
	                channelTransferRuleVO.setType(rs.getString("type"));
	                channelTransferRuleVO.setUncntrlTransferAllowed(rs.getString("uncntrl_transfer_allowed"));
	                // channelTransferRuleVO.setStatus(rs.getString("status"));
	                channelTransferRuleVO.setStatus(rs.getString("lookup_code"));
	                channelTransferRuleVO.setStatusDesc(rs.getString("lookup_name"));
	                channelTransferRuleVO.setUncntrlTransferAllowedTmp(rs.getString("to_cat_uncntrl"));
	                channelTransferRuleVO.setTransferType(rs.getString("transfer_type"));
	                channelTransferRuleVO.setTransferAllowed(rs.getString("transfer_allowed"));
	                channelTransferRuleVO.setFocAllowed(rs.getString("foc_allowed"));
	                channelTransferRuleVO.setFocTransferType(rs.getString("foc_transfer_type"));
	                channelTransferRuleVO.setDpAllowed(rs.getString("direct_payout_allowed"));
	                channelTransferRuleVO.setRestrictedMsisdnAccess(rs.getString("restricted_msisdn_access"));
	                channelTransferRuleVO.setRestrictedRechargeAccess(rs.getString("restricted_recharge_allowed"));

	                // new fields added in the table
	                channelTransferRuleVO.setToDomainCode(rs.getString("to_domain_code"));

	                channelTransferRuleVO.setUncntrlTransferLevel(rs.getString("uncntrl_transfer_level"));
	                channelTransferRuleVO.setCntrlTransferLevel(rs.getString("cntrl_transfer_level"));
	                channelTransferRuleVO.setFixedTransferLevel(rs.getString("fixed_transfer_level"));
	                channelTransferRuleVO.setFixedTransferCategory(rs.getString("fixed_transfer_category"));

	                channelTransferRuleVO.setUncntrlReturnAllowed(rs.getString("uncntrl_return_allowed"));
	                channelTransferRuleVO.setUncntrlReturnLevel(rs.getString("uncntrl_return_level"));
	                channelTransferRuleVO.setCntrlReturnLevel(rs.getString("cntrl_return_level"));
	                channelTransferRuleVO.setFixedReturnLevel(rs.getString("fixed_return_level"));
	                channelTransferRuleVO.setFixedReturnCategory(rs.getString("fixed_return_category"));

	                channelTransferRuleVO.setUncntrlWithdrawAllowed(rs.getString("uncntrl_withdraw_allowed"));
	                channelTransferRuleVO.setUncntrlWithdrawLevel(rs.getString("uncntrl_withdraw_level"));
	                channelTransferRuleVO.setCntrlWithdrawLevel(rs.getString("cntrl_withdraw_level"));
	                channelTransferRuleVO.setFixedWithdrawLevel(rs.getString("fixed_withdraw_level"));
	                channelTransferRuleVO.setFixedWithdrawCategory(rs.getString("fixed_withdraw_category"));
	                channelTransferRuleVO.setPreviousStatus(rs.getString("previous_status"));
	                // ends here

	                channelTransferRuleVOList.add(channelTransferRuleVO);
	            }
	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[loadChannelTransferRuleVOList]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, "loadChannelTransferRuleVOList", "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[loadChannelTransferRuleVOList]", "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, "loadChannelTransferRuleVOList", "error.general.processing");
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
	                _log.debug(methodName, "Exiting:list size=" + channelTransferRuleVOList.size());
	            }
	        }
	        return channelTransferRuleVOList;
	    }

	    /**
	     * Method suspendChannelTransferRule.
	     * This method for the suspension Request of the transfer rule.
	     * 
	     * @author nilesh.kumar
	     * @param p_con
	     *            Connection
	     * @param p_channelTransferRuleVO
	     *            ChannelTransferRuleVO
	     * @return int
	     * @throws BTSLBaseException
	     */
	    public int approveChannelTransferRule(Connection p_con, String p_status, ChannelTransferRuleVO p_channelTransferRuleVO) throws BTSLBaseException {
	        final String methodName = "approveChannelTransferRule";
	        StringBuilder logger = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	logger.setLength(0);
	        	logger.append("Entered:p_status=");
	        	logger.append(p_status);
	        	logger.append("p_channelTransferRuleVO=");
	        	logger.append(p_channelTransferRuleVO);
	            _log.debug(methodName, logger);
	        }
	        PreparedStatement pstmtapprove = null;
	        int approveCount = 0;
	        try {
	            final StringBuffer insertQuery = new StringBuffer("UPDATE chnl_transfer_rules set status=?,previous_status=?  WHERE transfer_rule_id=?");
	            final String query = insertQuery.toString();
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Query=" + query);
	            }
	            pstmtapprove = p_con.prepareStatement(query);
	            int i = 1;

	            pstmtapprove.setString(i, p_status);
	            i++;
	            pstmtapprove.setString(i, p_channelTransferRuleVO.getStatus());
	            i++;
	            pstmtapprove.setString(i, p_channelTransferRuleVO.getTransferRuleID());
	            i++;
	            // for the checking is the record modified during the transaction.
	            final boolean modified = this.isRecordModified(p_con, p_channelTransferRuleVO.getLastModifiedTime(), p_channelTransferRuleVO.getTransferRuleID());
	            if (modified) {
	                throw new BTSLBaseException(this, methodName, "error.modify.true");
	            }
	            approveCount = pstmtapprove.executeUpdate();
	        } catch (BTSLBaseException be) {
	            throw be;
	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[deleteChannelTransferRule]",
	                "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[deleteChannelTransferRule]",
	                "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } finally {
	            try {
	                if (pstmtapprove != null) {
	                    pstmtapprove.close();
	                }
	            } catch (Exception ex) {
	                _log.errorTrace(methodName, ex);
	            }
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Exiting approveCount=" + approveCount);
	            }
	        }
	        return approveCount;
	    }

	    /**
	     * Method rejectChannelTransferRule.
	     * This method for the rejection of the transfer rule.
	     * 
	     * @author nilesh.kumar
	     * @param p_con
	     *            Connection
	     * @param p_channelTransferRuleVO
	     *            ChannelTransferRuleVO
	     * @return int
	     * @throws BTSLBaseException
	     */
	    public int rejectChannelTransferRule(Connection p_con, ChannelTransferRuleVO p_channelTransferRuleVO) throws BTSLBaseException {
	        final String methodName = "rejectChannelTransferRule";
	        StringBuilder logger = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	logger.setLength(0);
	        	logger.append("Entered:p_channelTransferRuleVO=");
	        	logger.append(p_channelTransferRuleVO);
	            _log.debug(methodName, logger);
	        }
	        PreparedStatement pstmtReject = null;
	        int rejectCount = 0;
	        try {
	            final StringBuffer insertQuery = new StringBuffer("DELETE FROM chnl_transfer_rules WHERE transfer_rule_id=?");
	            final String query = insertQuery.toString();
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Query=" + query);
	            }
	            pstmtReject = p_con.prepareStatement(query);
	            int i = 1;
	            pstmtReject.setString(i, p_channelTransferRuleVO.getTransferRuleID());
	            i++;
	            // for the checking is the record modified during the transaction.
	            final boolean modified = this.isRecordModified(p_con, p_channelTransferRuleVO.getLastModifiedTime(), p_channelTransferRuleVO.getTransferRuleID());
	            if (modified) {
	                throw new BTSLBaseException(this, methodName, "error.modify.true");
	            }
	            rejectCount = pstmtReject.executeUpdate();
	        } catch (BTSLBaseException be) {
	            throw be;
	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[deleteChannelTransferRule]",
	                "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[deleteChannelTransferRule]",
	                "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } finally {
	            try {
	                if (pstmtReject != null) {
	                    pstmtReject.close();
	                }
	            } catch (Exception ex) {
	                _log.errorTrace(methodName, ex);
	            }
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Exiting deleteCount=" + rejectCount);
	            }
	        }
	        return rejectCount;
	    }

	    /**
	     * Method updateChannelTransferRule.
	     * This method update the transfer rule and its products information.
	     * To update the product information, in this method we first delete all the
	     * product associated with this transfer
	     * rule and then insert new specified products informaiton.
	     * 
	     * @param p_con
	     *            Connection
	     * @param p_channelTransferRuleVO
	     *            ChannelTransferRuleVO
	     * @return int
	     * @throws BTSLBaseException
	     */
	    public int modifyChannelTransferRule(Connection p_con, ChannelTransferRuleVO p_channelTransferRuleVO) throws BTSLBaseException {
	        final String methodName = "updateChannelTransferRule";
	        StringBuilder logger = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	logger.setLength(0);
	        	logger.append("Entered:p_channelTransferRuleVO=");
	        	logger.append(p_channelTransferRuleVO);
	            _log.debug(methodName, logger);
	        }
	        PreparedStatement pstmtUpdate = null;
	        int updateCount = 0;
	        try {
	            final StringBuffer updateQuery = new StringBuffer("UPDATE chnl_transfer_rules SET from_category=?, to_category=?, ");
	            updateQuery.append("parent_association_allowed=?,direct_transfer_allowed=?,transfer_chnl_bypass_allowed=?,  ");
	            updateQuery.append("withdraw_allowed=?, withdraw_chnl_bypass_allowed=?, return_allowed=?,return_chnl_bypass_allowed=?,  ");
	            updateQuery.append("approval_required=?, first_approval_limit=?, second_approval_limit=?, modified_by=?, ");
	            updateQuery.append("modified_on=?,status=?,uncntrl_transfer_allowed=?,transfer_type=?, transfer_allowed=?, ");
	            updateQuery.append("foc_transfer_type=?, foc_allowed=?,restricted_msisdn_access=?, ");
	            updateQuery.append("to_domain_code=? , uncntrl_transfer_level=? , cntrl_transfer_level=? , ");
	            updateQuery.append("fixed_transfer_level=? , fixed_transfer_category=? , uncntrl_return_allowed=? , ");
	            updateQuery.append("uncntrl_return_level=? , cntrl_return_level=? , fixed_return_level=? , ");
	            updateQuery.append("fixed_return_category=? , uncntrl_withdraw_allowed=? , uncntrl_withdraw_level=? , ");
	            updateQuery
	                .append("cntrl_withdraw_level=? , fixed_withdraw_level=? , fixed_withdraw_category=?, restricted_recharge_allowed = ?,direct_payout_allowed=?,previous_status=? ");
	            updateQuery.append("WHERE transfer_rule_id=?");
	            final String query = updateQuery.toString();
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Query=" + query);
	            }
	            pstmtUpdate = p_con.prepareStatement(query);
	            int i = 1;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getFromCategory());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getToCategory());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getParentAssocationAllowed());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getDirectTransferAllowed());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getTransferChnlBypassAllowed());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getWithdrawAllowed());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getWithdrawChnlBypassAllowed());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getReturnAllowed());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getReturnChnlBypassAllowed());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getApprovalRequired());
	            i++;
	            pstmtUpdate.setLong(i, p_channelTransferRuleVO.getFirstApprovalLimit());
	            i++;
	            pstmtUpdate.setLong(i, p_channelTransferRuleVO.getSecondApprovalLimit());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getModifiedBy());
	            i++;
	            pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferRuleVO.getModifiedOn()));
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getStatus());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getUncntrlTransferAllowed());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getTransferType());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getTransferAllowed());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getFocTransferType());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getFocAllowed());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getRestrictedMsisdnAccess());
	            i++;
	            // new fields added
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getToDomainCode());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getUncntrlTransferLevel());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getCntrlTransferLevel());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getFixedTransferLevel());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getFixedTransferCategory());
	            i++;

	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getUncntrlReturnAllowed());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getUncntrlReturnLevel());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getCntrlReturnLevel());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getFixedReturnLevel());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getFixedReturnCategory());
	            i++;

	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getUncntrlWithdrawAllowed());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getUncntrlWithdrawLevel());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getCntrlWithdrawLevel());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getFixedWithdrawLevel());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getFixedWithdrawCategory());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getRestrictedRechargeAccess());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getDpAllowed());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getPreviousStatus());
	            i++;
	            // ends here

	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getTransferRuleID());
	            i++;
	            // for the checking is the record modified during the transaction.
	            final boolean modified = this.isRecordModified(p_con, p_channelTransferRuleVO.getLastModifiedTime(), p_channelTransferRuleVO.getTransferRuleID());
	            if (modified) {
	                throw new BTSLBaseException(this, methodName, "error.modify.true");
	            }
	            if (updateCount > 0) {
	                // delete existing product information from the mapping table
	                if (p_channelTransferRuleVO.getProductVOList() != null && p_channelTransferRuleVO.getProductVOList().size() > 0) {
	                    updateCount = this.deleteTransferRuleProductMapping(p_con, p_channelTransferRuleVO.getTransferRuleID());
	                    updateCount = pstmtUpdate.executeUpdate();
	                }

	                // add new information of the products
	                if (p_channelTransferRuleVO.getProductArray() != null && p_channelTransferRuleVO.getProductArray().length > 0) {
	                    // if(p_channelTransferRuleVO.getProductArray()[0]!=null &&
	                    // p_channelTransferRuleVO.getProductArray()[0].length()>0)
	                    updateCount = this.addTransferRuleProductMapping(p_con, p_channelTransferRuleVO.getTransferRuleID(), p_channelTransferRuleVO.getProductArray());
	                }
	            }
	        } catch (BTSLBaseException be) {
	            throw be;
	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[updateChannelTransferRule]",
	                "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[updateChannelTransferRule]",
	                "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } finally {
	            try {
	                if (pstmtUpdate != null) {
	                    pstmtUpdate.close();
	                }
	            } catch (Exception ex) {
	                _log.errorTrace(methodName, ex);
	            }
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Exiting updateCount=" + updateCount);
	            }
	        }
	        return updateCount;
	    }

	    /**
	     * Method addChannelTransferRule.
	     * This method adds the transfer rule and also adds the transferrule and
	     * product mapping for all of the procduct
	     * associated with the transferrule.
	     * 
	     * @param p_con
	     *            Connection
	     * @param p_channelTransferRuleVO
	     *            ChannelTransferRuleVO
	     * @return int
	     * @throws BTSLBaseException
	     */
	    public int addChannelTrfRule(Connection p_con, ChannelTransferRuleVO p_channelTransferRuleVO) throws BTSLBaseException {
	        final String methodName = "addChannelTrfRule";
	        StringBuilder logger = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	logger.setLength(0);
	        	logger.append("Entered:p_channelTransferRuleVO=");
	        	logger.append(p_channelTransferRuleVO);
	            _log.debug(methodName, logger);
	        }
	        PreparedStatement pstmtInsert = null;
	        int addCount = 0;
	        try {
	            final StringBuffer insertQuery = new StringBuffer("INSERT INTO chnl_transfer_rules(transfer_rule_id, domain_code, network_code, ");
	            insertQuery.append("from_category, to_category, parent_association_allowed,direct_transfer_allowed, transfer_chnl_bypass_allowed, ");
	            insertQuery.append("withdraw_allowed, withdraw_chnl_bypass_allowed, return_allowed, return_chnl_bypass_allowed, ");
	            insertQuery.append("approval_required, first_approval_limit, second_approval_limit, created_by, created_on, ");
	            insertQuery.append("modified_by, modified_on,status,type,uncntrl_transfer_allowed,transfer_type, ");
	            insertQuery.append("transfer_allowed, foc_transfer_type, foc_allowed,restricted_msisdn_access, ");
	            insertQuery.append("to_domain_code, uncntrl_transfer_level, cntrl_transfer_level, fixed_transfer_level, ");
	            insertQuery.append("fixed_transfer_category, uncntrl_return_allowed, uncntrl_return_level, ");
	            insertQuery.append("cntrl_return_level, fixed_return_level, fixed_return_category, ");
	            insertQuery.append("uncntrl_withdraw_allowed, uncntrl_withdraw_level, cntrl_withdraw_level, ");
	            insertQuery.append("fixed_withdraw_level, fixed_withdraw_category, restricted_recharge_allowed,direct_payout_allowed,previous_status  ) ");
	            insertQuery.append("VALUES(UPPER(?),UPPER(?),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
	            final String query = insertQuery.toString();
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Query=" + query);
	            }
	            pstmtInsert = p_con.prepareStatement(query);
	            int i = 1;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getTransferRuleID());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getDomainCode());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getNetworkCode());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getFromCategory());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getToCategory());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getParentAssocationAllowed());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getDirectTransferAllowed());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getTransferChnlBypassAllowed());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getWithdrawAllowed());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getWithdrawChnlBypassAllowed());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getReturnAllowed());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getReturnChnlBypassAllowed());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getApprovalRequired());
	            i++;
	            pstmtInsert.setLong(i, p_channelTransferRuleVO.getFirstApprovalLimit());
	            i++;
	            pstmtInsert.setLong(i, p_channelTransferRuleVO.getSecondApprovalLimit());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getCreatedBy());
	            i++;
	            pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferRuleVO.getCreatedOn()));
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getModifiedBy());
	            i++;
	            pstmtInsert.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_channelTransferRuleVO.getModifiedOn()));
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getStatus());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getType());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getUncntrlTransferAllowed());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getTransferType());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getTransferAllowed());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getFocTransferType());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getFocAllowed());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getRestrictedMsisdnAccess());
	            i++;
	            // new fields added
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getToDomainCode());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getUncntrlTransferLevel());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getCntrlTransferLevel());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getFixedTransferLevel());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getFixedTransferCategory());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getUncntrlReturnAllowed());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getUncntrlReturnLevel());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getCntrlReturnLevel());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getFixedReturnLevel());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getFixedReturnCategory());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getUncntrlWithdrawAllowed());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getUncntrlWithdrawLevel());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getCntrlWithdrawLevel());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getFixedWithdrawLevel());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getFixedWithdrawCategory());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getRestrictedRechargeAccess());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getDpAllowed());
	            i++;
	            pstmtInsert.setString(i, p_channelTransferRuleVO.getPreviousStatus());
	            i++;
	            // ends here

	            addCount = pstmtInsert.executeUpdate();
	            // add the product information corresponds to the transfer rule.
	            if (addCount > 0 && p_channelTransferRuleVO.getProductArray() != null && p_channelTransferRuleVO.getProductArray().length > 0) {
	                // if(p_channelTransferRuleVO.getProductArray()[0]!=null &&
	                // p_channelTransferRuleVO.getProductArray()[0].length()>0)
	                addCount = this.addTransferRuleProductMapping(p_con, p_channelTransferRuleVO.getTransferRuleID(), p_channelTransferRuleVO.getProductArray());
	            }
	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[addChannelTransferRule]",
	                "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[addChannelTransferRule]",
	                "", "", "", "Exception:" + e.getMessage());
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
	                _log.debug(methodName, "Exiting addCount=" + addCount);
	            }
	        }
	        return addCount;
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
	    public ArrayList loadProductVONewList(Connection p_con, String p_transferRuleID) throws BTSLBaseException {
	        final String methodName = "loadProductVOList";
	        StringBuilder logger = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	logger.setLength(0);
	        	logger.append("Entered:p_transferRuleID=");
	        	logger.append(p_transferRuleID);
	            _log.debug(methodName, logger);
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

	            final String selectQueStr = selectQuery.toString();
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Query=" + selectQueStr);
	            }
	            pstmtSelect = p_con.prepareStatement(selectQueStr);
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
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[loadProductVOList]", "", "",
	                "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[loadProductVOList]", "", "",
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
	     * loading transfer rule details
	     * Method :loadChannelTrfRuleNewVOList
	     * 
	     * @author Nilesh kumar
	     * @param p_con
	     *            Connection
	     * @param p_networkCode
	     *            String
	     * @param p_status
	     *            String
	     * @param p_ruleType
	     *            String
	     * @return ArrayList
	     * @throws BTSLBaseException
	     */

	    public ArrayList loadApprovalChannelTrfRuleNewVOList(Connection p_con, String p_status, String p_ruleType) throws BTSLBaseException {
	        final String methodName = "loadApprovalChannelTransferRuleNewVOList";
	        StringBuilder logger = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	logger.setLength(0);
	        	logger.append("Entered:p_status=");
	        	logger.append(p_status);
	        	logger.append(" p_ruleType=");
	        	logger.append(p_ruleType);
	            _log.debug(methodName, "Entered:p_status=" + p_status + " p_ruleType=" + p_ruleType);
	        }
	        final ArrayList channelTransferRuleVOList = new ArrayList();
	        PreparedStatement pstmtSelect = null;
	        ResultSet rs = null;
	        try {
	            final StringBuffer selectQuery = new StringBuffer("SELECT  CAT1.sequence_no from_seq_no,CAT2.sequence_no to_seq_no,network_code, ");
	            selectQuery.append("CHNLTRF.domain_code,transfer_rule_id, from_category,CAT1.category_name from_name, ");
	            selectQuery.append("to_category,CAT2.category_name to_name,parent_association_allowed,direct_transfer_allowed, ");
	            selectQuery.append("transfer_chnl_bypass_allowed, withdraw_allowed,withdraw_chnl_bypass_allowed,return_allowed, ");
	            selectQuery.append("return_chnl_bypass_allowed,approval_required, first_approval_limit,second_approval_limit, ");
	            selectQuery.append("transfer_type, transfer_allowed, foc_transfer_type, foc_allowed,direct_payout_allowed,restricted_msisdn_access, CHNLTRF.modified_on, ");
	            selectQuery.append("CHNLTRF.type,CHNLTRF.uncntrl_transfer_allowed,CHNLTRF.status,CAT2.uncntrl_transfer_allowed to_cat_uncntrl, ");
	            selectQuery.append("to_domain_code, uncntrl_transfer_level, cntrl_transfer_level, ");
	            selectQuery.append("fixed_transfer_level, fixed_transfer_category, uncntrl_return_allowed, ");
	            selectQuery.append("uncntrl_return_level, cntrl_return_level, fixed_return_level, ");
	            selectQuery.append("fixed_return_category, uncntrl_withdraw_allowed, uncntrl_withdraw_level, ");
	            selectQuery.append("cntrl_withdraw_level, fixed_withdraw_level, fixed_withdraw_category, restricted_recharge_allowed,L.lookup_name,L.lookup_code,CHNLTRF.previous_status ");
	            selectQuery.append("FROM chnl_transfer_rules CHNLTRF,categories CAT1,categories CAT2, lookups L ");
	            selectQuery.append("WHERE ");
	            selectQuery.append("CHNLTRF.type=? ");
	            selectQuery.append("AND L.lookup_type=? ");
	            selectQuery.append("AND L.lookup_code=CHNLTRF.status ");
	            selectQuery.append(" AND CHNLTRF.status IN (" + p_status + ") ");
	            selectQuery.append("AND CHNLTRF.from_category=CAT1.category_code ");
	            selectQuery.append("AND CHNLTRF.to_category=CAT2.category_code ");
	            selectQuery.append("ORDER BY CHNLTRF.domain_code, CAT1.sequence_no, CAT2.sequence_no ");
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Query=" + selectQuery);
	            }
	            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
	            int i = 0;
	            // pstmtSelect.setString(++i, p_fromCategoryCode);
	            ++i;
	            pstmtSelect.setString(i, p_ruleType);
	            ++i;
	            pstmtSelect.setString(i, PretupsI.TRANS_TYPE);
	            rs = pstmtSelect.executeQuery();
	            ChannelTransferRuleVO channelTransferRuleVO = null;
	            while (rs.next()) {
	                channelTransferRuleVO = new ChannelTransferRuleVO();
	                channelTransferRuleVO.setNetworkCode(rs.getString("network_code"));
	                channelTransferRuleVO.setDomainCode(rs.getString("domain_code"));
	                channelTransferRuleVO.setTransferRuleID(rs.getString("transfer_rule_id"));
	                channelTransferRuleVO.setFromCategory(rs.getString("from_category"));
	                channelTransferRuleVO.setFromCategoryDes(rs.getString("from_name"));
	                channelTransferRuleVO.setFromSeqNo(rs.getInt("from_seq_no"));
	                channelTransferRuleVO.setToCategory(rs.getString("to_category"));
	                channelTransferRuleVO.setToCategoryDes(rs.getString("to_name"));
	                channelTransferRuleVO.setToSeqNo(rs.getInt("to_seq_no"));
	                channelTransferRuleVO.setParentAssocationAllowed(rs.getString("parent_association_allowed"));
	                channelTransferRuleVO.setDirectTransferAllowed(rs.getString("direct_transfer_allowed"));
	                channelTransferRuleVO.setTransferChnlBypassAllowed(rs.getString("transfer_chnl_bypass_allowed"));
	                channelTransferRuleVO.setWithdrawAllowed(rs.getString("withdraw_allowed"));
	                channelTransferRuleVO.setWithdrawChnlBypassAllowed(rs.getString("withdraw_chnl_bypass_allowed"));
	                channelTransferRuleVO.setReturnAllowed(rs.getString("return_allowed"));
	                if (PretupsI.YES.equals(channelTransferRuleVO.getReturnAllowed())) {
	                    channelTransferRuleVO.setRuleType(PretupsI.CHNL_TRANSFER_TYPE_RET);
	                } else {
	                    channelTransferRuleVO.setRuleType(PretupsI.CHNL_TRANSFER_TYPE_TRF_WITD);
	                }
	                channelTransferRuleVO.setReturnChnlBypassAllowed(rs.getString("return_chnl_bypass_allowed"));
	                channelTransferRuleVO.setApprovalRequired(rs.getString("approval_required"));
	                channelTransferRuleVO.setFirstApprovalLimit(rs.getLong("first_approval_limit"));
	                channelTransferRuleVO.setSecondApprovalLimit(rs.getLong("second_approval_limit"));
	                channelTransferRuleVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
	                channelTransferRuleVO.setType(rs.getString("type"));
	                channelTransferRuleVO.setUncntrlTransferAllowed(rs.getString("uncntrl_transfer_allowed"));
	                channelTransferRuleVO.setStatus(rs.getString("lookup_code"));
	                channelTransferRuleVO.setStatusDesc(rs.getString("lookup_name"));
	                channelTransferRuleVO.setUncntrlTransferAllowedTmp(rs.getString("to_cat_uncntrl"));
	                channelTransferRuleVO.setTransferType(rs.getString("transfer_type"));
	                channelTransferRuleVO.setTransferAllowed(rs.getString("transfer_allowed"));
	                channelTransferRuleVO.setFocAllowed(rs.getString("foc_allowed"));
	                channelTransferRuleVO.setFocTransferType(rs.getString("foc_transfer_type"));
	                channelTransferRuleVO.setDpAllowed(rs.getString("direct_payout_allowed"));
	                channelTransferRuleVO.setRestrictedMsisdnAccess(rs.getString("restricted_msisdn_access"));
	                channelTransferRuleVO.setRestrictedRechargeAccess(rs.getString("restricted_recharge_allowed"));

	                // new fields added in the table
	                channelTransferRuleVO.setToDomainCode(rs.getString("to_domain_code"));

	                channelTransferRuleVO.setUncntrlTransferLevel(rs.getString("uncntrl_transfer_level"));
	                channelTransferRuleVO.setCntrlTransferLevel(rs.getString("cntrl_transfer_level"));
	                channelTransferRuleVO.setFixedTransferLevel(rs.getString("fixed_transfer_level"));
	                channelTransferRuleVO.setFixedTransferCategory(rs.getString("fixed_transfer_category"));

	                channelTransferRuleVO.setUncntrlReturnAllowed(rs.getString("uncntrl_return_allowed"));
	                channelTransferRuleVO.setUncntrlReturnLevel(rs.getString("uncntrl_return_level"));
	                channelTransferRuleVO.setCntrlReturnLevel(rs.getString("cntrl_return_level"));
	                channelTransferRuleVO.setFixedReturnLevel(rs.getString("fixed_return_level"));
	                channelTransferRuleVO.setFixedReturnCategory(rs.getString("fixed_return_category"));

	                channelTransferRuleVO.setUncntrlWithdrawAllowed(rs.getString("uncntrl_withdraw_allowed"));
	                channelTransferRuleVO.setUncntrlWithdrawLevel(rs.getString("uncntrl_withdraw_level"));
	                channelTransferRuleVO.setCntrlWithdrawLevel(rs.getString("cntrl_withdraw_level"));
	                channelTransferRuleVO.setFixedWithdrawLevel(rs.getString("fixed_withdraw_level"));
	                channelTransferRuleVO.setFixedWithdrawCategory(rs.getString("fixed_withdraw_category"));
	                channelTransferRuleVO.setPreviousStatus(rs.getString("previous_status"));
	                // ends here

	                channelTransferRuleVOList.add(channelTransferRuleVO);
	            }
	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[loadChannelTransferRuleVOList]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, "loadChannelTransferRuleVOList", "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[loadChannelTransferRuleVOList]", "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, "loadChannelTransferRuleVOList", "error.general.processing");
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
	                _log.debug(methodName, "Exiting:list size=" + channelTransferRuleVOList.size());
	            }
	        }
	        return channelTransferRuleVOList;
	    }

	    /**
	     * loading transfer rule details
	     * Method :loadChannelTransferRuleNewVOList
	     * 
	     * @author Nilesh kumar
	     * @param p_con
	     *            Connection
	     * @param p_networkCode
	     *            String
	     * @param p_fromDomainCode
	     *            String
	     * @param p_toDomainCode
	     *            String
	     * @param p_ruleType
	     *            String
	     * @return ArrayList
	     * @throws BTSLBaseException
	     */

	    public ArrayList loadC2CTransferRuleNewVOList(Connection p_con, String p_networkCode, String p_fromDomainCode, String p_toDomainCode, String p_status, String p_ruleType, String p_returnAllowed) throws BTSLBaseException {
	        final String methodName = "loadChannelTransferRuleNewVOList";
	        StringBuilder logger = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	logger.setLength(0);
	        	logger.append("Entered:p_networkCode=");
	        	logger.append(p_networkCode);
	        	logger.append(",p_fromDomainCode=");
	        	logger.append(p_fromDomainCode);
	        	logger.append(",p_toDomainCode =");
	        	logger.append(p_fromDomainCode);
	            _log.debug(
	                methodName,
	                "Entered:p_networkCode=" + p_networkCode + ",p_fromDomainCode=" + p_fromDomainCode + ",p_toDomainCode =" + p_toDomainCode + ",p_status =" + p_status + " p_ruleType=" + p_ruleType + "p_returnAllowed" + p_returnAllowed);
	        }
	        final ArrayList channelTransferRuleVOList = new ArrayList();
	        PreparedStatement pstmtSelect = null;
	        ResultSet rs = null;
	        try {
	            final StringBuffer selectQuery = new StringBuffer("SELECT  CAT1.sequence_no from_seq_no,CAT2.sequence_no to_seq_no,network_code, ");
	            selectQuery.append("CHNLTRF.domain_code,transfer_rule_id, from_category,CAT1.category_name from_name, ");
	            selectQuery.append("to_category,CAT2.category_name to_name,parent_association_allowed,direct_transfer_allowed, ");
	            selectQuery.append("transfer_chnl_bypass_allowed, withdraw_allowed,withdraw_chnl_bypass_allowed,return_allowed, ");
	            selectQuery.append("return_chnl_bypass_allowed,approval_required, first_approval_limit,second_approval_limit, ");
	            selectQuery.append("transfer_type, transfer_allowed, foc_transfer_type, foc_allowed,direct_payout_allowed,restricted_msisdn_access, CHNLTRF.modified_on, ");
	            selectQuery.append("CHNLTRF.type,CHNLTRF.uncntrl_transfer_allowed,CHNLTRF.status,CAT2.uncntrl_transfer_allowed to_cat_uncntrl, ");
	            selectQuery.append("to_domain_code, uncntrl_transfer_level, cntrl_transfer_level, ");
	            selectQuery.append("fixed_transfer_level, fixed_transfer_category, uncntrl_return_allowed, ");
	            selectQuery.append("uncntrl_return_level, cntrl_return_level, fixed_return_level, ");
	            selectQuery.append("fixed_return_category, uncntrl_withdraw_allowed, uncntrl_withdraw_level, ");
	            selectQuery.append("cntrl_withdraw_level, fixed_withdraw_level, fixed_withdraw_category, restricted_recharge_allowed,L.lookup_name,L.lookup_code ");
	            selectQuery.append("FROM chnl_transfer_rules CHNLTRF,categories CAT1,categories CAT2, lookups L ");
	            selectQuery.append("WHERE CHNLTRF.network_code=? ");
	            selectQuery.append("AND CHNLTRF.domain_code=? AND CHNLTRF.to_domain_code=? ");
	            // new
	            selectQuery.append("AND CHNLTRF.return_allowed=? ");
	            selectQuery.append("AND L.lookup_type=? ");
	            selectQuery.append("AND L.lookup_code=CHNLTRF.status ");
	            //
	            // selectQuery.append("AND CHNLTRF.status=? ");
	            selectQuery.append(" AND CHNLTRF.status IN (" + p_status + ") ");

	            selectQuery.append("AND CHNLTRF.type=? AND CHNLTRF.from_category=CAT1.category_code ");
	            selectQuery.append("AND CHNLTRF.to_category=CAT2.category_code ");
	            selectQuery.append("ORDER BY CHNLTRF.domain_code, CAT1.sequence_no, CAT2.sequence_no ");
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Query=" + selectQuery);
	            }
	            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
	            int i = 0;
	            ++i;
	            pstmtSelect.setString(i, p_networkCode);
	            ++i;
	            pstmtSelect.setString(i, p_fromDomainCode);
	            ++i;
	            pstmtSelect.setString(i, p_toDomainCode);
	            ++i;
	            pstmtSelect.setString(i, p_returnAllowed);
	            ++i;
	            pstmtSelect.setString(i, PretupsI.TRANS_TYPE);
	            // pstmtSelect.setString(++i,
	            // PretupsI.CHNL_TRANSFER_RULE_STATUS_ACTIVE);
	            ++i;
	            pstmtSelect.setString(i, p_ruleType);
	            rs = pstmtSelect.executeQuery();
	            ChannelTransferRuleVO channelTransferRuleVO = null;
	            while (rs.next()) {
	                channelTransferRuleVO = new ChannelTransferRuleVO();
	                channelTransferRuleVO.setNetworkCode(rs.getString("network_code"));
	                channelTransferRuleVO.setDomainCode(rs.getString("domain_code"));
	                channelTransferRuleVO.setTransferRuleID(rs.getString("transfer_rule_id"));
	                channelTransferRuleVO.setFromCategory(rs.getString("from_category"));
	                channelTransferRuleVO.setFromCategoryDes(rs.getString("from_name"));
	                channelTransferRuleVO.setFromSeqNo(rs.getInt("from_seq_no"));
	                channelTransferRuleVO.setToCategory(rs.getString("to_category"));
	                channelTransferRuleVO.setToCategoryDes(rs.getString("to_name"));
	                channelTransferRuleVO.setToSeqNo(rs.getInt("to_seq_no"));

	                channelTransferRuleVO.setParentAssocationAllowed(rs.getString("parent_association_allowed"));
	                channelTransferRuleVO.setDirectTransferAllowed(rs.getString("direct_transfer_allowed"));
	                channelTransferRuleVO.setTransferChnlBypassAllowed(rs.getString("transfer_chnl_bypass_allowed"));
	                channelTransferRuleVO.setWithdrawAllowed(rs.getString("withdraw_allowed"));
	                channelTransferRuleVO.setWithdrawChnlBypassAllowed(rs.getString("withdraw_chnl_bypass_allowed"));
	                channelTransferRuleVO.setReturnAllowed(rs.getString("return_allowed"));
	                channelTransferRuleVO.setReturnChnlBypassAllowed(rs.getString("return_chnl_bypass_allowed"));
	                channelTransferRuleVO.setApprovalRequired(rs.getString("approval_required"));
	                channelTransferRuleVO.setFirstApprovalLimit(rs.getLong("first_approval_limit"));
	                channelTransferRuleVO.setSecondApprovalLimit(rs.getLong("second_approval_limit"));
	                channelTransferRuleVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
	                channelTransferRuleVO.setType(rs.getString("type"));
	                channelTransferRuleVO.setUncntrlTransferAllowed(rs.getString("uncntrl_transfer_allowed"));
	                // channelTransferRuleVO.setStatus(rs.getString("status"));
	                channelTransferRuleVO.setStatusDesc(rs.getString("lookup_name"));
	                channelTransferRuleVO.setStatus(rs.getString("lookup_code"));
	                channelTransferRuleVO.setUncntrlTransferAllowedTmp(rs.getString("to_cat_uncntrl"));
	                channelTransferRuleVO.setTransferType(rs.getString("transfer_type"));
	                channelTransferRuleVO.setTransferAllowed(rs.getString("transfer_allowed"));
	                channelTransferRuleVO.setFocAllowed(rs.getString("foc_allowed"));
	                channelTransferRuleVO.setFocTransferType(rs.getString("foc_transfer_type"));
	                channelTransferRuleVO.setDpAllowed(rs.getString("direct_payout_allowed"));
	                channelTransferRuleVO.setRestrictedMsisdnAccess(rs.getString("restricted_msisdn_access"));
	                channelTransferRuleVO.setRestrictedRechargeAccess(rs.getString("restricted_recharge_allowed"));

	                // new fields added in the table
	                channelTransferRuleVO.setToDomainCode(rs.getString("to_domain_code"));

	                channelTransferRuleVO.setUncntrlTransferLevel(rs.getString("uncntrl_transfer_level"));
	                channelTransferRuleVO.setCntrlTransferLevel(rs.getString("cntrl_transfer_level"));
	                channelTransferRuleVO.setFixedTransferLevel(rs.getString("fixed_transfer_level"));
	                channelTransferRuleVO.setFixedTransferCategory(rs.getString("fixed_transfer_category"));

	                channelTransferRuleVO.setUncntrlReturnAllowed(rs.getString("uncntrl_return_allowed"));
	                channelTransferRuleVO.setUncntrlReturnLevel(rs.getString("uncntrl_return_level"));
	                channelTransferRuleVO.setCntrlReturnLevel(rs.getString("cntrl_return_level"));
	                channelTransferRuleVO.setFixedReturnLevel(rs.getString("fixed_return_level"));
	                channelTransferRuleVO.setFixedReturnCategory(rs.getString("fixed_return_category"));

	                channelTransferRuleVO.setUncntrlWithdrawAllowed(rs.getString("uncntrl_withdraw_allowed"));
	                channelTransferRuleVO.setUncntrlWithdrawLevel(rs.getString("uncntrl_withdraw_level"));
	                channelTransferRuleVO.setCntrlWithdrawLevel(rs.getString("cntrl_withdraw_level"));
	                channelTransferRuleVO.setFixedWithdrawLevel(rs.getString("fixed_withdraw_level"));
	                channelTransferRuleVO.setFixedWithdrawCategory(rs.getString("fixed_withdraw_category"));
	                // ends here

	                channelTransferRuleVOList.add(channelTransferRuleVO);
	            }
	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[loadChannelTransferRuleVOList]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, "loadChannelTransferRuleVOList", "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[loadChannelTransferRuleVOList]", "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, "loadChannelTransferRuleVOList", "error.general.processing");
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
	                _log.debug("loadChannelTransferRuleVOList", "Exiting:list size=" + channelTransferRuleVOList.size());
	            }
	        }
	        return channelTransferRuleVOList;
	    }

	    /**
	     * Method modifyChannelTransferRuleReject.
	     * 
	     * @param p_con
	     *            Connection
	     * @param p_channelTransferRuleVO
	     *            ChannelTransferRuleVO
	     * @return int
	     * @throws BTSLBaseException
	     * @author nilesh.kumar
	     */
	    public int modifyChannelTransferRuleReject(Connection p_con, ChannelTransferRuleVO p_channelTransferRuleVO) throws BTSLBaseException {
	        final String methodName = "modifyChannelTransferRuleReject";
	        StringBuilder logger = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	logger.setLength(0);
	        	logger.append("Entered:p_channelTransferRuleVO=");
	        	logger.append(p_channelTransferRuleVO);
	            _log.debug(methodName, logger);
	        }
	        PreparedStatement pstmtUpdate = null;
	        PreparedStatement pstmtSelect = null;
	        int updateCount = 0;
	        ResultSet rs = null;
	        try {
	            final StringBuffer selectQuery = new StringBuffer("SELECT  from_category, to_category, ");
	            selectQuery.append("parent_association_allowed,direct_transfer_allowed,transfer_chnl_bypass_allowed,   ");
	            selectQuery.append("withdraw_allowed, withdraw_chnl_bypass_allowed, return_allowed,return_chnl_bypass_allowed, ");
	            selectQuery.append("approval_required, first_approval_limit, second_approval_limit, modified_by, ");
	            selectQuery.append("modified_on,status,uncntrl_transfer_allowed,transfer_type, transfer_allowed, ");
	            selectQuery.append("foc_transfer_type, foc_allowed,restricted_msisdn_access, ");
	            selectQuery.append("to_domain_code, uncntrl_transfer_level, cntrl_transfer_level, ");
	            selectQuery.append("fixed_transfer_level, fixed_transfer_category, uncntrl_return_allowed, ");
	            selectQuery.append("uncntrl_return_level, cntrl_return_level, fixed_return_level, ");
	            selectQuery.append("fixed_return_category, uncntrl_withdraw_allowed, uncntrl_withdraw_level, ");
	            selectQuery.append("fixed_return_category, uncntrl_withdraw_allowed, uncntrl_withdraw_level, ");
	            selectQuery.append("cntrl_withdraw_level, fixed_withdraw_level, fixed_withdraw_category,previous_status,direct_payout_allowed ");
	            selectQuery.append("FROM chnl_transfer_rules_history CHNLTRFH ");
	            selectQuery.append("WHERE CHNLTRFH.transfer_rule_id=? ");
	            /*
	             * Modified by lalit 
	             * placed space before "and" 
	             */
	            selectQuery.append("AND CHNLTRFH.operation_performed in ('I','U','D') and entry_date=(select max(entry_date) FROM chnl_transfer_rules_history where transfer_rule_id = ? and  entry_date <> (select max(entry_date) FROM chnl_transfer_rules_history where transfer_rule_id = ? )) and status in ('R','P','Q','D','Y') ");
	            if (_log.isDebugEnabled()) {
	                _log.debug("loadChannelTransferRuleNewVOList", "Query=" + selectQuery);
	            }
	            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
	            int j = 0;
	            ++j;
	            pstmtSelect.setString(j, p_channelTransferRuleVO.getTransferRuleID());
	            pstmtSelect.setString(++j,p_channelTransferRuleVO.getTransferRuleID());
				pstmtSelect.setString(++j,p_channelTransferRuleVO.getTransferRuleID());
	            // pstmtSelect.setString(++j,PretupsI.DB_FLAG_INSERT);
	            rs = pstmtSelect.executeQuery();
	            StringBuffer updateQuery = new StringBuffer();
	            while (rs.next()) {
	            	updateQuery.setLength(0);
	            	updateQuery.append("UPDATE chnl_transfer_rules SET from_category=?, to_category=?, ");
	                updateQuery.append("parent_association_allowed=?,direct_transfer_allowed=?,transfer_chnl_bypass_allowed=?,  ");
	                updateQuery.append("withdraw_allowed=?, withdraw_chnl_bypass_allowed=?, return_allowed=?,return_chnl_bypass_allowed=?,  ");
	                updateQuery.append("approval_required=?, first_approval_limit=?, second_approval_limit=?, modified_by=?, ");
	                updateQuery.append("modified_on=?,status=?,uncntrl_transfer_allowed=?,transfer_type=?, transfer_allowed=?, ");
	                updateQuery.append("foc_transfer_type=?, foc_allowed=?,restricted_msisdn_access=?, ");
	                updateQuery.append("to_domain_code=? , uncntrl_transfer_level=? , cntrl_transfer_level=? , ");
	                updateQuery.append("fixed_transfer_level=? , fixed_transfer_category=? , uncntrl_return_allowed=? , ");
	                updateQuery.append("uncntrl_return_level=? , cntrl_return_level=? , fixed_return_level=? , ");
	                updateQuery.append("fixed_return_category=? , uncntrl_withdraw_allowed=? , uncntrl_withdraw_level=? , ");
	                updateQuery.append("cntrl_withdraw_level=? , fixed_withdraw_level=? , fixed_withdraw_category=?,previous_status=?,direct_payout_allowed=? ");
	                updateQuery.append("WHERE transfer_rule_id=? ");
	                final String query = updateQuery.toString();
	                if (_log.isDebugEnabled()) {
	                    _log.debug(methodName, "Query is=" + query);
	                }
	                pstmtUpdate = p_con.prepareStatement(query);
	                int i = 0;
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("from_category"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("to_category"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("parent_association_allowed"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("direct_transfer_allowed"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("transfer_chnl_bypass_allowed"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("withdraw_allowed"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("withdraw_chnl_bypass_allowed"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("return_allowed"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("return_chnl_bypass_allowed"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("approval_required"));
	                ++i;
	                pstmtUpdate.setLong(i, rs.getLong("first_approval_limit"));
	                ++i;
	                pstmtUpdate.setLong(i, rs.getLong("second_approval_limit"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("modified_by"));
	                ++i;
	                pstmtUpdate.setTimestamp(i, rs.getTimestamp("modified_on"));
	                ++i;
	                pstmtUpdate.setString(i, p_channelTransferRuleVO.getStatus());// status
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("uncntrl_transfer_allowed"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("transfer_type"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("transfer_allowed"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("foc_transfer_type"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("foc_allowed"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("restricted_msisdn_access"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("to_domain_code"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("uncntrl_transfer_level"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("cntrl_transfer_level"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("fixed_transfer_level"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("fixed_transfer_category"));

	                // new fields added
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("uncntrl_return_allowed"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("uncntrl_return_level"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("cntrl_return_level"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("fixed_return_level"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("fixed_return_category"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("uncntrl_withdraw_allowed"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("uncntrl_withdraw_level"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("cntrl_withdraw_level"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("fixed_withdraw_level"));
	                ++i;
	                pstmtUpdate.setString(i, rs.getString("fixed_withdraw_category"));
	                ++i;
	                pstmtUpdate.setString(i, p_channelTransferRuleVO.getPreviousStatus());// previous
	                ++i; // status
	                pstmtUpdate.setString(i, rs.getString("direct_payout_allowed"));
	                // ends here
	                ++i;
	                pstmtUpdate.setString(i, p_channelTransferRuleVO.getTransferRuleID());
	                if (_log.isDebugEnabled()) {
	                    _log.debug("loadProductVOList", "Query=" + query);
	                }
	                // for the checking is the record modified during the
	                // transaction.
	                final boolean modified = this.isRecordModified(p_con, p_channelTransferRuleVO.getLastModifiedTime(), p_channelTransferRuleVO.getTransferRuleID());
	                if (modified) {
	                    throw new BTSLBaseException(this, methodName, "error.modify.true");
	                }
	                updateCount = pstmtUpdate.executeUpdate();
	            }

	            if (updateCount > 0) {
	                // delete existing product information from the mapping table
	                if (p_channelTransferRuleVO.getProductVOList() != null && p_channelTransferRuleVO.getProductVOList().size() > 0) {
	                    updateCount = this.deleteTransferRuleProductMapping(p_con, p_channelTransferRuleVO.getTransferRuleID());
	                }

	                // add new information of the products
	                if (p_channelTransferRuleVO.getProductArray() != null && p_channelTransferRuleVO.getProductArray().length > 0) {
	                    // if(p_channelTransferRuleVO.getProductArray()[0]!=null &&
	                    // p_channelTransferRuleVO.getProductArray()[0].length()>0)
	                    updateCount = this.addTransferRuleProductMapping(p_con, p_channelTransferRuleVO.getTransferRuleID(), p_channelTransferRuleVO.getProductArray());
	                }
	            }
	        } catch (BTSLBaseException be) {
	            throw be;
	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[modifyChannelTransferRuleReject]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[modifyChannelTransferRuleReject]", "", "", "", "Exception:" + e.getMessage());
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
	            try {
	                if (pstmtUpdate != null) {
	                    pstmtUpdate.close();
	                }
	            } catch (Exception ex) {
	                _log.errorTrace(methodName, ex);
	            }
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Exiting updateCount=" + updateCount);
	            }
	        }
	        return updateCount;
	    }

	    /**
	     * Method checkUserUnderToCategory.
	     * 
	     * @param p_con
	     *            Connection
	     * @param p_channelTransferRuleVO
	     *            ChannelTransferRuleVO
	     * @return boolean
	     * @throws BTSLBaseException
	     * @author nilesh.kumar
	     */
	    public boolean checkUserUnderToCategory(Connection p_con, ChannelTransferRuleVO p_channelTransferRuleVO) throws BTSLBaseException {
	        final String methodName = "checkUserUnderToCategory";
	        StringBuilder logger = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	logger.setLength(0);
	        	logger.append("Entered:ToCategory::=");
	        	logger.append(p_channelTransferRuleVO.getToCategory());
	            _log.debug(methodName, logger);
	        }
	        
	        boolean isUserExistUnderToCat = false;
	        ResultSet userResultSet = null;
	        try {
	            final StringBuffer insertQuery = new StringBuffer("SELECT U.user_id from ");
	            insertQuery.append("users U ,chnl_transfer_rules CHL ");
	            insertQuery.append("where U.category_code=? ");
	            insertQuery.append("AND U.category_code= CHL.to_category ");
	            insertQuery.append("AND U.status not in('N','C') ");
	            final String query = insertQuery.toString();
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Query=" + query);
	            }
	           try(PreparedStatement pstmtSelect = p_con.prepareStatement(query);)
	           {
	            pstmtSelect.setString(1, p_channelTransferRuleVO.getToCategory());
	            userResultSet = pstmtSelect.executeQuery();
	            while (userResultSet.next()) {
	                isUserExistUnderToCat = true;
	                break;
	            }
	        }
	        }catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[checkUserUnderToCategory]",
	                "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, "checkUserUnderFromCategory", "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelTransferRuleWebDAO[checkUserUnderToCategory]",
	                "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, "checkUserUnderFromCategory", "error.general.processing");
	        } finally {
	        	try {
	                if (userResultSet != null) {
	                	userResultSet.close();
	                }
	            } catch (Exception e) {
	                _log.errorTrace(methodName, e);
	            }
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Exiting isUserExistUnderToCat=" + isUserExistUnderToCat);
	            }
	        }
	        return isUserExistUnderToCat;
	    }

	    /**
	     * Method requestChannelTransferRuleDeletion.
	     * 
	     * @param p_con
	     *            Connection
	     * @param p_channelTransferRuleVO
	     *            ChannelTransferRuleVO
	     * @return int
	     * @throws BTSLBaseException
	     * @author nilesh.kumar
	     */
	    public int requestChannelTransferRuleDeletion(Connection p_con, ChannelTransferRuleVO p_channelTransferRuleVO) throws BTSLBaseException {
	        final String methodName = "requestChannelTransferRuleDeletion";
	        StringBuilder logger = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	logger.setLength(0);
	        	logger.append("Entered:p_channelTransferRuleVO=");
	        	logger.append(p_channelTransferRuleVO);
	            _log.debug(methodName, "Entered:p_channelTransferRuleVO=" + p_channelTransferRuleVO);
	        }
	        PreparedStatement pstmtUpdate = null;
	        final ResultSet updateStatusResultSet = null;
	        int statusUpdateCount = 0;
	        try {
	            final StringBuffer insertQuery = new StringBuffer("UPDATE chnl_transfer_rules set status=?,previous_status=?  WHERE transfer_rule_id=?");
	            final String query = insertQuery.toString();
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Query=" + query);
	            }
	            pstmtUpdate = p_con.prepareStatement(query);
	            int i = 1;
	            pstmtUpdate.setString(i, PretupsI.DELETE_TRANSFER_RULE_REQ_STATUS);
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getPreviousStatus());
	            i++;
	            pstmtUpdate.setString(i, p_channelTransferRuleVO.getTransferRuleID());
	            i++;
	            // for the checking is the record modified during the transaction.
	            statusUpdateCount = pstmtUpdate.executeUpdate();
	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[requestChannelTransferRuleDeletion]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, "resumeRequestChannelTransferRule", "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[requestChannelTransferRuleDeletion]", "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } finally {
	            try {
	                if (pstmtUpdate != null) {
	                    pstmtUpdate.close();
	                }
	            } catch (Exception ex) {
	                _log.errorTrace(methodName, ex);
	            }
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Exiting statusUpdateCount=" + statusUpdateCount);
	            }
	        }
	        return statusUpdateCount;
	    }

	    /**
	     * Method checkUserUnderFromCategory.
	     * 
	     * @param p_con
	     *            Connection
	     * @param p_channelTransferRuleVO
	     *            ChannelTransferRuleVO
	     * @return boolean
	     * @throws BTSLBaseException
	     * @author nilesh.kumar
	     */
	    public boolean checkUserUnderFromCategory(Connection p_con, ChannelTransferRuleVO p_channelTransferRuleVO) throws BTSLBaseException {
	        final String methodName = "checkUserUnderFromCategory";
	        StringBuilder logger = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	logger.setLength(0);
	        	logger.append("Entered:ToCategory::=");
	        	logger.append(p_channelTransferRuleVO.getToCategory());
	            _log.debug(methodName, logger);
	        }
	       
	        boolean isUserExistUnderFromCat = false;
	        ResultSet userResultSet = null;
	        try {
	            final StringBuffer insertQuery = new StringBuffer("SELECT U.user_id from ");
	            insertQuery.append("users U ,chnl_transfer_rules CHL ");
	            insertQuery.append("where U.category_code=? ");
	            insertQuery.append("AND U.category_code= CHL.from_category ");
	            insertQuery.append("AND U.status not in('N','C') ");
	            final String query = insertQuery.toString();
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Query=" + query);
	            }
	           try(PreparedStatement pstmtSelect = p_con.prepareStatement(query);)
	           {
	            pstmtSelect.setString(1, p_channelTransferRuleVO.getFromCategory());
	            userResultSet = pstmtSelect.executeQuery();
	            while (userResultSet.next()) {
	                isUserExistUnderFromCat = true;
	                break;
	            }
	        }
	        }catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[checkUserUnderFromCategory]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[checkUserUnderFromCategory]", "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } finally {
	        	try {
	                if (userResultSet != null) {
	                	userResultSet.close();
	                }
	            } catch (Exception e) {
	                _log.errorTrace(methodName, e);
	            }
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Exiting checkUserUnderFromCategory=" + isUserExistUnderFromCat);
	            }
	        }
	        return isUserExistUnderFromCat;
	    }
	    public String getTransferRuleID(Connection p_con, ChannelTransferRuleVO p_channelTransferRuleVO) throws BTSLBaseException {
	        final String methodName = "getTransferRuleID";
	        StringBuilder logger = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	logger.setLength(0);
	        	logger.append("Entered:ToCategory::=");
	        	logger.append(p_channelTransferRuleVO.getToCategory());
	            _log.debug(methodName, logger);
	        }
	       
	        String transferRuleID = null;
	        ResultSet userResultSet = null;
	        ResultSet rsSelect = null;

	        try {
	            final StringBuffer insertQuery = new StringBuffer("SELECT CHL.transfer_rule_id from ");
	            insertQuery.append("chnl_transfer_rules CHL ");
	            insertQuery.append("where CHL.from_category=? ");
	            insertQuery.append("and CHL.to_category=? ");
	            insertQuery.append("and CHL.domain_code=? ");
	            insertQuery.append("and CHL.to_domain_code=? ");
	            insertQuery.append("and CHL.network_code=? ");
	            
	            
	            final String query = insertQuery.toString();
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Query=" + query);
	            }
	            PreparedStatement pstmtSelect = null;
	           
		            pstmtSelect = p_con.prepareStatement(query);
		            int i = 1;
		            pstmtSelect.setString(i, p_channelTransferRuleVO.getFromCategory());
		            i++;
		            pstmtSelect.setString(i, p_channelTransferRuleVO.getToCategory());
		            i++;
		            pstmtSelect.setString(i, p_channelTransferRuleVO.getDomainCode());
		            i++;
		            pstmtSelect.setString(i, p_channelTransferRuleVO.getToDomainCode());
		            i++;
		            pstmtSelect.setString(i, p_channelTransferRuleVO.getNetworkCode());
		            i++;
		           
		            rsSelect = pstmtSelect.executeQuery();
		           
		            while (rsSelect.next()) {
		            transferRuleID =	rsSelect.getString("transfer_rule_id");
		           
		                
		            }
	        
	        }catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[getTransferRuleID]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[getTransferRuleID]", "", "", "", "Exception:" + e.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } finally {
	        	try {
	                if (userResultSet != null) {
	                	userResultSet.close();
	                }
	            } catch (Exception e) {
	                _log.errorTrace(methodName, e);
	            }
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Exiting checkUserUnderFromCategory=" + transferRuleID);
	            }
	        }
	        return transferRuleID;
	    }

	    /**
	     * Load the categories with associated transfer rules for O2C Transfer
	     * 
	     * @param p_con
	     *            Connection
	     * @param p_networkCode
	     *            String
	     * @param p_fromCategory
	     *            String
	     * @param p_focAllowed
	     *            String
	     * @param p_transferType
	     *            String
	     * 
	     * @return ArrayList
	     * @throws BTSLBaseException
	     */
	    public ArrayList loadTransferRulesCategoryListForO2C(Connection p_con, String p_networkCode, String p_fromCategory, String p_focAllowed, String p_transferType) throws BTSLBaseException {

	        final String methodName = "loadTransferRulesCategoryListForO2C";
	        StringBuilder logger = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	logger.setLength(0);
	        	logger.append("Entered  From Category Code ");
	        	logger.append(p_fromCategory);
	        	logger.append(" Network Code ");
	        	logger.append(p_networkCode);
	        	logger.append(" O2C Allowed=");
	        	logger.append(p_focAllowed);
	        	logger.append(" Transfer Type=");
	        	logger.append(p_transferType);
	            _log.debug(methodName, logger);
	        }
	        PreparedStatement pstmtSelect = null;
	        ResultSet rsSelect = null;

	        final StringBuffer strBuff = new StringBuffer(" SELECT ctr.domain_code, ctr.to_category, c.category_name ");
	        strBuff.append(" FROM chnl_transfer_rules ctr ,categories c ");
	        strBuff.append(" WHERE ctr.from_category = ? AND ctr.network_code=?  AND ");
	        strBuff.append(" ctr.status = 'Y' AND ctr.to_category = c.category_code ");
	        strBuff.append(" AND ctr.withdraw_allowed = ? AND ctr.type = ? ORDER BY  c.sequence_no ");

	        final String sqlSelect = strBuff.toString();

	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
	        }
	        final ArrayList arrayList = new ArrayList();
	        try {
	            pstmtSelect = p_con.prepareStatement(sqlSelect);
	            int i = 1;
	            pstmtSelect.setString(i, p_fromCategory);
	            i++;
	            pstmtSelect.setString(i, p_networkCode);
	            i++;
	            pstmtSelect.setString(i, p_focAllowed);
	            i++;
	            pstmtSelect.setString(i, p_transferType);
	            i++;
	            rsSelect = pstmtSelect.executeQuery();
	            ChannelTransferRuleVO rulesVO = null;
	            while (rsSelect.next()) {

	                rulesVO = new ChannelTransferRuleVO();

	                rulesVO.setDomainCode(rsSelect.getString("domain_code"));
	                rulesVO.setToCategoryDes(rsSelect.getString("category_name"));
	                rulesVO.setToCategory(rsSelect.getString("to_category"));
	                arrayList.add(rulesVO);
	            }

	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException : " + sqe);
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[loadTransferRulesCategoryListForO2C]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception ex) {
	            _log.error(methodName, "Exception : " + ex);
	            _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[loadTransferRulesCategoryListForO2C]", "", "", "", "Exception:" + ex.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } finally {
	            try {
	                if (rsSelect != null) {
	                    rsSelect.close();
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
	                _log.debug(methodName, "Exiting:  arrayList Size =" + arrayList.size());
	            }
	        }
	        return arrayList;
	    }

	    /**
	     * Method loadTrfRuleCatListForRestrictedMsisdn
	     * Load the categories with associated transfer rules
	     * 
	     * @param p_con
	     * @param p_networkCode
	     * @param p_fromCategory
	     * @param p_ownerOnly
	     * @param p_isRestricted
	     *            TODO
	     * @return ArrayList
	     * @throws BTSLBaseException
	     * @author Amit Ruwali
	     * 
	     */
	    public ArrayList loadTrfRuleCatListForRestrictedMsisdn(Connection p_con, String p_networkCode, String p_fromCategory, boolean p_ownerOnly, boolean p_isRestricted) throws BTSLBaseException {
	        final String methodName = "loadTrfRuleCatListForRestrictedMsisdn";
	        StringBuilder logger = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	logger.setLength(0);
	        	logger.append("Entered  From Category Code ");
	        	logger.append(p_fromCategory);
	        	logger.append(" Network Code ");
	        	logger.append(p_networkCode);
	        	logger.append(", p_ownerOnly=");
	        	logger.append(p_ownerOnly);
	        	logger.append(" p_isRestricted=");
	        	logger.append(p_isRestricted);
	            _log.debug(methodName, logger);
	        }
	        PreparedStatement pstmtSelect = null;
	        ResultSet rsSelect = null;
	        final StringBuffer strBuff = new StringBuffer("SELECT ctr.domain_code,ctr.to_category,c.category_name,ctr.restricted_msisdn_access, ctr.restricted_recharge_allowed ");
	        strBuff.append("FROM chnl_transfer_rules ctr,categories c  ");
	        strBuff.append("WHERE ctr.to_category = c.category_code ");
	        if (p_isRestricted) {
	            strBuff.append("AND ctr.restricted_msisdn_access='Y' ");
	        }
	        strBuff.append("AND ctr.status = 'Y' AND c.status = 'Y' AND ctr.from_category = ? AND ctr.network_code=? ");
	        if (p_ownerOnly) {
	            strBuff.append(" AND c.sequence_no ='1' ");
	        }
	        strBuff.append(" AND ctr.to_category!= ctr.from_category ORDER BY  c.sequence_no ");
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, "QUERY =" + strBuff);
	        }
	        final ArrayList arrayList = new ArrayList();
	        try {
	            pstmtSelect = p_con.prepareStatement(strBuff.toString());
	            int i = 1;
	            pstmtSelect.setString(i, p_fromCategory);
	            i++;
	            pstmtSelect.setString(i, p_networkCode);
	            i++;
	            rsSelect = pstmtSelect.executeQuery();
	            ChannelTransferRuleVO rulesVO = null;
	            while (rsSelect.next()) {
	                rulesVO = new ChannelTransferRuleVO();
	                rulesVO.setDomainCode(rsSelect.getString("domain_code"));
	                rulesVO.setToCategory(rsSelect.getString("domain_code") + ":" + rsSelect.getString("to_category"));
	                rulesVO.setToCategoryDes(rsSelect.getString("category_name"));
	                rulesVO.setRestrictedMsisdnAccess(rsSelect.getString("restricted_msisdn_access"));
	                rulesVO.setRestrictedRechargeAccess(rsSelect.getString("restricted_recharge_allowed"));
	                arrayList.add(rulesVO);
	            }
	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException : " + sqe);
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[loadTrfRuleCatListForRestrictedMsisdn]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception ex) {
	            _log.error(methodName, "Exception : " + ex);
	            _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[loadTrfRuleCatListForRestrictedMsisdn]", "", "", "", "Exception:" + ex.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } finally {
	            try {
	                if (rsSelect != null) {
	                    rsSelect.close();
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
	                _log.debug(methodName, "Exiting:  arrayList Size =" + arrayList.size());
	            }
	        }
	        return arrayList;
	    }

	    /**
	     * Load the categories with associated transfer rules
	     * 
	     * @param p_con
	     * @param p_networkCode
	     * @param p_fromCategory
	     * @return ArrayList
	     * @throws BTSLBaseException
	     */
	    public ArrayList loadTransferRulesCategoryList(Connection p_con, String p_networkCode, String p_fromCategory) throws BTSLBaseException {

	        final String methodName = "loadTransferRulesCategoryList";
	        StringBuilder logger = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	logger.setLength(0);
	        	logger.append("Entered  From Category Code ");
	        	logger.append(p_fromCategory);
	        	logger.append(" Network Code ");
	        	logger.append(p_networkCode);
	            _log.debug(methodName, logger);
	        }
	        PreparedStatement pstmtSelect = null;
	        ResultSet rsSelect = null;
	        final StringBuffer strBuff = new StringBuffer(" SELECT ctr.domain_code, ctr.to_category, ctr.parent_association_allowed, ");
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
	        final String sqlSelect = strBuff.toString();
	        if (_log.isDebugEnabled()) {
	            _log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
	        }
	        final ArrayList arrayList = new ArrayList();
	        try {
	            pstmtSelect = p_con.prepareStatement(sqlSelect);
	            int i = 1;
	            pstmtSelect.setString(i, p_fromCategory);
	            i++;
	            pstmtSelect.setString(i, p_networkCode);
	            i++;
	            rsSelect = pstmtSelect.executeQuery();
	            ChannelTransferRuleVO rulesVO = null;
	            while (rsSelect.next()) {
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

	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException : " + sqe);
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[loadTransferRulesCategoryList]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception ex) {
	            _log.error(methodName, "Exception : " + ex);
	            _log.errorTrace(methodName, ex);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[loadTransferRulesCategoryList]", "", "", "", "Exception:" + ex.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.processing");
	        } finally {
	            try {
	                if (rsSelect != null) {
	                    rsSelect.close();
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
	                _log.debug(methodName, "Exiting:  arrayList Size =" + arrayList.size());
	            }
	        }
	        return arrayList;
	    }

	    /**
	     * Method loadChannelTransferRuleVOList.
	     * This method loads all the transfer rules of the selected category domain
	     * type and also select some of the
	     * other informative data such as sequence no of the from_category and
	     * to_category field.
	     * 
	     * @param p_con
	     *            Connection
	     * @param p_networkCode
	     *            String
	     * @param p_domainCode
	     *            String
	     * @param p_ruleType
	     *            String
	     * @return ArrayList
	     * @throws BTSLBaseException
	     */
	    public ArrayList<ChannelTransferRuleVO> loadChannelTransferRuleVOList(Connection p_con, String p_networkCode, String p_domainCode, String p_ruleType) throws BTSLBaseException {
	        final String methodName = "loadChannelTransferRuleVOList";
	        StringBuilder logger = new StringBuilder();
	        if (_log.isDebugEnabled()) {
	        	logger.setLength(0);
	        	logger.append("Entered:p_networkCode=");
	        	logger.append(p_networkCode);
	        	logger.append(",p_domainCode=");
	        	logger.append(p_domainCode);
	        	logger.append(", p_ruleType=");
	        	logger.append(p_ruleType);
	            _log.debug(methodName, "Entered:p_networkCode=" + p_networkCode + ",p_domainCode=" + p_domainCode + ", p_ruleType=" + p_ruleType);
	        }
	        final ArrayList channelTransferRuleVOList = new ArrayList();
	        PreparedStatement pstmtSelect = null;
	        ResultSet rs = null;
	        try {
	            final StringBuffer selectQuery = new StringBuffer("SELECT  CAT1.sequence_no from_seq_no,CAT2.sequence_no to_seq_no,network_code, ");
	            selectQuery.append("CHNLTRF.domain_code,transfer_rule_id, from_category,CAT1.category_name from_name, ");
	            selectQuery.append("to_category,CAT2.category_name to_name,parent_association_allowed,direct_transfer_allowed, ");
	            selectQuery.append("transfer_chnl_bypass_allowed, withdraw_allowed,withdraw_chnl_bypass_allowed,return_allowed, ");
	            selectQuery.append("return_chnl_bypass_allowed,approval_required, first_approval_limit,second_approval_limit, ");
	            selectQuery.append("transfer_type, transfer_allowed, foc_transfer_type, foc_allowed,restricted_msisdn_access,restricted_recharge_allowed, CHNLTRF.modified_on, ");
	            selectQuery.append("CHNLTRF.type,CHNLTRF.uncntrl_transfer_allowed,CAT2.uncntrl_transfer_allowed to_cat_uncntrl ");
	            selectQuery.append("FROM chnl_transfer_rules CHNLTRF,categories CAT1,categories CAT2 ");
	            selectQuery.append("WHERE CHNLTRF.network_code=? ");
	            if (!BTSLUtil.isNullString(p_domainCode)) {
	                selectQuery.append("AND CHNLTRF.domain_code=? ");
	            }
	            selectQuery.append("AND CHNLTRF.status=? ");
	            selectQuery.append("AND CHNLTRF.type=? AND CHNLTRF.from_category=CAT1.category_code ");
	            selectQuery.append("AND CHNLTRF.to_category=CAT2.category_code ");
	            // selectQuery.append("ORDER BY transfer_rule_id ");
	            selectQuery.append("ORDER BY CHNLTRF.domain_code, CAT1.sequence_no, CAT2.sequence_no ");
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "Query=" + selectQuery);
	            }
	            pstmtSelect = p_con.prepareStatement(selectQuery.toString());
	            int i = 0;
	            ++i;
	            pstmtSelect.setString(i, p_networkCode);
	            if (!BTSLUtil.isNullString(p_domainCode)) {
	                ++i;
	                pstmtSelect.setString(i, p_domainCode);
	            }
	            ++i;
	            pstmtSelect.setString(i, PretupsI.CHNL_TRANSFER_RULE_STATUS_ACTIVE);
	            ++i;
	            pstmtSelect.setString(i, p_ruleType);
	            rs = pstmtSelect.executeQuery();
	            ChannelTransferRuleVO channelTransferRuleVO = null;
	            while (rs.next()) {
	                channelTransferRuleVO = new ChannelTransferRuleVO();
	                channelTransferRuleVO.setNetworkCode(rs.getString("network_code"));
	                channelTransferRuleVO.setDomainCode(rs.getString("domain_code"));
	                channelTransferRuleVO.setTransferRuleID(rs.getString("transfer_rule_id"));
	                channelTransferRuleVO.setFromCategory(rs.getString("from_category"));
	                channelTransferRuleVO.setFromCategoryDes(rs.getString("from_name"));
	                channelTransferRuleVO.setFromSeqNo(rs.getInt("from_seq_no"));
	                channelTransferRuleVO.setToCategory(rs.getString("to_category"));
	                channelTransferRuleVO.setToCategoryDes(rs.getString("to_name"));
	                channelTransferRuleVO.setToSeqNo(rs.getInt("to_seq_no"));
	                channelTransferRuleVO.setParentAssocationAllowed(rs.getString("parent_association_allowed"));
	                channelTransferRuleVO.setDirectTransferAllowed(rs.getString("direct_transfer_allowed"));
	                channelTransferRuleVO.setTransferChnlBypassAllowed(rs.getString("transfer_chnl_bypass_allowed"));
	                channelTransferRuleVO.setWithdrawAllowed(rs.getString("withdraw_allowed"));
	                channelTransferRuleVO.setWithdrawChnlBypassAllowed(rs.getString("withdraw_chnl_bypass_allowed"));
	                channelTransferRuleVO.setReturnAllowed(rs.getString("return_allowed"));
	                channelTransferRuleVO.setReturnChnlBypassAllowed(rs.getString("return_chnl_bypass_allowed"));
	                channelTransferRuleVO.setApprovalRequired(rs.getString("approval_required"));
	                channelTransferRuleVO.setFirstApprovalLimit(rs.getLong("first_approval_limit"));
	                channelTransferRuleVO.setSecondApprovalLimit(rs.getLong("second_approval_limit"));
	                channelTransferRuleVO.setLastModifiedTime(rs.getTimestamp("modified_on").getTime());
	                channelTransferRuleVO.setType(rs.getString("type"));
	                channelTransferRuleVO.setUncntrlTransferAllowed(rs.getString("uncntrl_transfer_allowed"));
	                channelTransferRuleVO.setUncntrlTransferAllowedTmp(rs.getString("to_cat_uncntrl"));
	                channelTransferRuleVO.setTransferType(rs.getString("transfer_type"));
	                channelTransferRuleVO.setTransferAllowed(rs.getString("transfer_allowed"));
	                channelTransferRuleVO.setFocAllowed(rs.getString("foc_allowed"));
	                channelTransferRuleVO.setFocTransferType(rs.getString("foc_transfer_type"));
	                channelTransferRuleVO.setRestrictedMsisdnAccess(rs.getString("restricted_msisdn_access"));
	                channelTransferRuleVO.setRestrictedRechargeAccess(rs.getString("restricted_recharge_allowed"));

	                channelTransferRuleVOList.add(channelTransferRuleVO);
	            }
	        } catch (SQLException sqe) {
	            _log.error(methodName, "SQLException:" + sqe.getMessage());
	            _log.errorTrace(methodName, sqe);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[loadChannelTransferRuleVOList]", "", "", "", "SQL Exception:" + sqe.getMessage());
	            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	        } catch (Exception e) {
	            _log.error(methodName, "Exception:" + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
	                "ChannelTransferRuleWebDAO[loadChannelTransferRuleVOList]", "", "", "", "Exception:" + e.getMessage());
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
	                _log.debug(methodName, "Exiting:list size=" + channelTransferRuleVOList.size());
	            }
	        }
	        return channelTransferRuleVOList;
	    }
	

}

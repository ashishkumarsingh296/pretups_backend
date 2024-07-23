/*
 * @# C2sBalanceQueryDAO.java
 * 
 * Created on Created by History
 * ------------------------------------------------------------------------------
 * --
 * September 22, 2005 Amit Ruwali Initial creation
 * ------------------------------------------------------------------------------
 * --
 * Copyright(c) 2005 Bharti Telesoft Ltd.
 */

package com.btsl.pretups.channel.query.businesslogic;

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

/**
 * @author
 *
 */
public class C2sBalanceQueryDAO {
    private Log log = LogFactory.getLog(this.getClass().getName());

    /**
     * Method loadUserDetails.
     * This method loads all of the products available in the specified network.
     * 
     * @param pCon
     *            Connection
     * @param p_networkCode
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUserDetails(Connection pCon, String pCategory, String pGrphDomainCode, String pSearchStr) throws BTSLBaseException {
        final String methodName = "loadUserDetails";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered pCategory=");
        	loggerValue.append (pCategory);
        	loggerValue.append("pGrphDomainCode=");
        	loggerValue.append (pGrphDomainCode);
        	loggerValue.append("pSearchStr=");
        	loggerValue.append(pSearchStr);
            log.debug("loadUserDetails", loggerValue );
        }
        final ArrayList userList = new ArrayList();
        
        
     
            final StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT u.user_name,u.user_id FROM users u,user_geographies ug");
            selectQuery.append(" WHERE u.category_code=? AND u.user_id=ug.user_id AND ug.grph_domain_code=?");
            selectQuery.append(" AND UPPER(u.user_name) LIKE UPPER(?) ORDER BY u.user_name");
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query=");
            	loggerValue.append (selectQuery);
                log.debug("loadUserDetails", loggerValue);
            }
           try(PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery.toString());)
           {
            pstmtSelect.setString(1, pCategory);
            pstmtSelect.setString(2, pGrphDomainCode);
            pstmtSelect.setString(3, pSearchStr);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            ListValueVO listValueVO = null;
            while (rs.next()) {
                listValueVO = new ListValueVO(rs.getString("user_name") + "(" + rs.getString("user_id") + ")", rs.getString("user_id"));
                userList.add(listValueVO);
            }
        } 
           }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException:");
        	loggerValue.append (sqe.getMessage());
        	
            log.error("loadUserDetails", loggerValue);
            log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
        	loggerValue.append( "SQL Exception:");
        	loggerValue.append (sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2sBalanceQueryDAO[loadUserDetails]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, "loadUserDetails", "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append( "Exception:");
        	loggerValue.append(e.getMessage());
            log.error("loadUserDetails", loggerValue );
            log.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append(  "Exception:" );
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2sBalanceQueryDAO[loadUserDetails]", "", "", "",
            		loggerValue.toString() );
            throw new BTSLBaseException(this, "loadUserDetails", "error.general.processing");
        } finally {
       
        	
         
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:list size=" );
            	loggerValue.append(userList.size());
                log.debug("loadUserDetails",  loggerValue );
            }
        }
        return userList;
    }

    /**
     * Method loadUserBalances.
     * This method loads the balnaces of the user corresponding to the user id
     * 
     * @param pCon
     *            Connection
     * @param p_networkCode
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUserBalances(Connection pCon, String pUserId) throws BTSLBaseException {
        final String methodName = "loadUserBalances";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered pUserId=" );
        	loggerValue.append(pUserId );
            log.debug("loadUserBalances", loggerValue);
        }
        final ArrayList userList = new ArrayList();
        
       
        
            final StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT ub.balance,ub.prev_balance,p.product_short_code,p.product_name,u.msisdn,u.user_name,u.address1 ");
            selectQuery.append("FROM users u,user_balances ub,products p WHERE ub.product_code=p.product_code ");
            selectQuery.append("AND u.user_id=ub.user_id AND ub.user_id=? AND u.status<>'N' AND u.status<>'C' ");
            if (log.isDebugEnabled()) {
                log.debug("loadUserBalances", "Query=" + selectQuery);
            }
           try(PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery.toString());)
           {
            pstmtSelect.setString(1, pUserId);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            C2sBalanceQueryVO balanceVO = null;
            while (rs.next()) {
                balanceVO = new C2sBalanceQueryVO();
                balanceVO.setBalance(rs.getLong("balance"));
                balanceVO.setProductShortCode(rs.getString("product_short_code"));
                balanceVO.setProductName(rs.getString("product_name"));
                balanceVO.setUserName(rs.getString("user_name"));
                balanceVO.setAddress(rs.getString("address1"));
                balanceVO.setMsisdn(rs.getString("msisdn"));
                userList.add(balanceVO);
            }
        }
           }
           catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException:");
        	loggerValue.append(sqe.getMessage() );
            log.error("loadUserBalances", loggerValue );
            log.errorTrace(methodName, sqe);
            StringBuilder handle= new StringBuilder(); 
            handle.setLength(0);
            handle.append("SQL Exception:");
            handle.append( sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2sBalanceQueryDAO[loadUserBalances]", "", "", "",
            		handle.toString());
            throw new BTSLBaseException(this, "loadUserBalances", "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(e.getMessage() );
            log.error("loadUserBalances", loggerValue );
            log.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(e.getMessage() );
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2sBalanceQueryDAO[loadUserBalances]", "", "", "",
            		loggerValue.toString());
            throw new BTSLBaseException(this, "loadUserBalances", "error.general.processing");
        } finally {
        	
        	
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Exiting:list size=" );
            	loggerValue.append(userList.size() );
                log.debug("loadUserBalances", loggerValue );
            }
        }
        return userList;
    }

    /**
     * Method loadUserBalancesForMsisdn.
     * This method loads the balances of the user corresponding to the msisdn
     * entered
     * 
     * @param pCon
     *            Connection
     * @param p_networkCode
     *            String
     * @return ArrayList
     * @throws BTSLBaseException
     */
    public ArrayList loadUserBalancesForMsisdn(Connection pCon, String pMsisdn) throws BTSLBaseException {
        final String methodName = "loadUserBalancesForMsisdn";
        StringBuilder loggerValue= new StringBuilder(); 
        if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered pMsisdn=");
        	loggerValue.append(pMsisdn);
            log.debug("loadUserBalancesForMsisdn",  loggerValue );
        }
        final ArrayList userList = new ArrayList();
        
       
            final StringBuilder selectQuery = new StringBuilder();
            selectQuery.append("SELECT ub.balance,ub.prev_balance,p.product_short_code,p.product_name,c.category_name,");
            selectQuery.append("d.domain_name,gd.grph_domain_name,u.user_name,u.address1 FROM users u,user_balances ub,products p,");
            selectQuery.append("user_phones up,categories c,domains d,user_geographies ug,geographical_domains gd ");
            selectQuery.append("WHERE ub.product_code=p.product_code AND u.user_id=ub.user_id ");
            selectQuery.append("AND c.category_code=u.category_code AND d.domain_code=c.domain_code ");
            selectQuery.append("AND ug.user_id=up.user_id AND ug.grph_domain_code=gd.grph_domain_code ");
            selectQuery.append("AND up.user_id=ub.user_id AND up.msisdn=? AND u.status<>'N' AND u.status<>'C'");
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append("Query=" );
            	loggerValue.append(selectQuery);
                log.debug("loadUserBalancesForMsisdn", loggerValue );
            }
            try(PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery.toString());)
            {
            pstmtSelect.setString(1, pMsisdn);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
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
        } 
            }catch (SQLException sqe) {
        	loggerValue.setLength(0);
        	loggerValue.append("SQLException:");
        	loggerValue.append(sqe.getMessage());
            log.error("loadUserBalancesForMsisdn",  loggerValue );
            log.errorTrace(methodName, sqe);
            loggerValue.setLength(0);
        	loggerValue.append("SQL Exception:");
        	loggerValue.append(sqe.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2sBalanceQueryDAO[loadUserBalancesForMsisdn]", "",
                "", "",  loggerValue.toString() );
            throw new BTSLBaseException(this, "loadUserBalancesForMsisdn", "error.general.sql.processing");
        } catch (Exception e) {
        	loggerValue.setLength(0);
        	loggerValue.append("Exception:");
        	loggerValue.append(e.getMessage());
        	
            log.error("loadUserBalancesForMsisdn",  loggerValue);
            log.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append( "Exception:" );
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2sBalanceQueryDAO[loadUserBalancesForMsisdn]", "",
                "", "", loggerValue.toString());
            throw new BTSLBaseException(this, "loadUserBalancesForMsisdn", "error.general.processing");
        } finally {
        	
            if (log.isDebugEnabled()) {
            	loggerValue.setLength(0);
            	loggerValue.append( "Exiting:list size=");
            	loggerValue.append(userList.size());
                log.debug("loadUserBalancesForMsisdn", loggerValue );
            }
        }
        return userList;
    }
}

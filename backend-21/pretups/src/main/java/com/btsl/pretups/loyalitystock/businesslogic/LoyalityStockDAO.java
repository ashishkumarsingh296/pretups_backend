/**
 * @(#)LoyalityStockDAO.java
 */

package com.btsl.pretups.loyalitystock.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.ObjectProducer;
import com.btsl.db.util.QueryConstants;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;


/**
 * @author 
 *
 */
public class LoyalityStockDAO {
    private Log log = LogFactory.getLog(this.getClass().getName());

    public ArrayList loadProductsForStock(Connection pCon, String pNetworkCode, String pNetworkFor, String pModule) throws BTSLBaseException {
        
    	final String methodName = "loadProductsForStock";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered :: pNetworkCode=" + pNetworkCode + ",pNetworkFor=" + pNetworkFor + " pModule=" + pModule);
        }
        
        PreparedStatement pstmtSelect = null;
        int size=0;
        ResultSet rs = null;
        LoyalityStockTxnItemsVO loyalityStockTxnItemsVO = null;
        ArrayList productList = null;
        
        try {
           
        	LoyalityStockQry loyalityStockQry = (LoyalityStockQry) ObjectProducer.getObject(QueryConstants.LOYALITY_STOCK_QRY, QueryConstants.QUERY_PRODUCER);
        	pstmtSelect = loyalityStockQry.loadProductsForStockQry(pCon, pNetworkCode, pNetworkFor, pModule);
            rs = pstmtSelect.executeQuery();
            productList = new ArrayList();
            while (rs.next()) {
                loyalityStockTxnItemsVO = new LoyalityStockTxnItemsVO();
                loyalityStockTxnItemsVO.setProductCode(rs.getString("product_code"));
                loyalityStockTxnItemsVO.setProductName(rs.getString("product_name"));
                loyalityStockTxnItemsVO.setStock(rs.getLong("loyalty_stock"));
                productList.add(loyalityStockTxnItemsVO);
            }
           
            if(!productList.isEmpty())
            {
            	size = productList.size();
            	
            }
        } catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityStockDAO[loadProductsForStock]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this,methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            log.error("loadProductsForStock", "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityStockDAO[loadProductsForStock]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
            try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
            try{
                if (pstmtSelect!= null){
                	pstmtSelect.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting productList=" + size);
            }
        }
        return productList;
    }

    /**
     * @param pCon
     * @param loyalityStockTxnVO
     * @return
     * @throws BTSLBaseException
     */
    public int addNewStock(Connection pCon, LoyalityStockTxnVO loyalityStockTxnVO) throws BTSLBaseException {
        
    	 final String methodName = "addNewStock";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered :: ");
        }
       
        
        ResultSet rs = null;
        int updateCount = 0;

        StringBuilder strBuff = new StringBuilder();
        strBuff.append("insert into loyalty_stock_transaction (  ");

        strBuff.append("TXN_NO,NETWORK_CODE,REQUESTED_POINTS,CREATED_ON,CREATED_BY,TXN_STATUS");
        strBuff.append(") values(?,?,?,?,?,?)");

        try {
            Date date = new Date();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query :: " + strBuff.toString());
            }
            try(PreparedStatement pstmtSelect = pCon.prepareStatement(strBuff.toString());)
            {
            pstmtSelect.setString(1, loyalityStockTxnVO.getTxnNo());
            pstmtSelect.setString(2, loyalityStockTxnVO.getNetworkCode());
            pstmtSelect.setLong(3, loyalityStockTxnVO.getRequestedQuantity());
            pstmtSelect.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(date));
            pstmtSelect.setString(5, loyalityStockTxnVO.getCreatedBy());
            pstmtSelect.setString(6, loyalityStockTxnVO.getTxnStatus());
            updateCount = pstmtSelect.executeUpdate();

        } 
        }catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityStockDAO[addNewStock]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this,methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityStockDAO[addNewStock]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
            
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting updateCount=" + updateCount);
            }
        }
        return updateCount;
    }

    public ArrayList loadStockTransactionList(Connection pCon, String pStatus, String pNetworkCode, String pNetworkType) throws BTSLBaseException {
        
    	final String methodName = "loadStockTransactionList";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered  pStatus=" + pStatus + " pNetworkCode=" + pNetworkCode + "  pNetworkType=" + pNetworkType);
        }
        
        
        ArrayList stockList = new ArrayList();
       
        LoyalityStockQry loyalityStockQry = (LoyalityStockQry)ObjectProducer.getObject(QueryConstants.LOYALITY_STOCK_QRY, QueryConstants.QUERY_PRODUCER);
        String strBuff = loyalityStockQry.loadStockTransactionListQry(pStatus);
        if (log.isDebugEnabled()) {
            log.debug(methodName, "QUERY sqlSelect=" + strBuff);
        }
        try(PreparedStatement pstmtSelect = pCon.prepareStatement(strBuff.toString());) {
            int i = 0;
            
            pstmtSelect.setString(++i, pNetworkCode);
            pstmtSelect.setString(++i, pNetworkCode);
            pstmtSelect.setString(++i, PretupsI.TRANSFER_STATUS);
            pstmtSelect.setString(++i, PretupsI.NEW);

            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            LoyalityStockTxnVO loyalityStockTxnVO = null;
            while (rs.next()) {
                loyalityStockTxnVO = new LoyalityStockTxnVO();


                loyalityStockTxnVO.setRequestedPoints(rs.getLong("REQUESTED_QUANTITY"));
                loyalityStockTxnVO.setInitiatedBy(rs.getString("REQUESTER"));

                loyalityStockTxnVO.setTxnNo(rs.getString("REFNO"));
                loyalityStockTxnVO.setTxnDate(rs.getDate("TRANSACTIONDATE"));
                loyalityStockTxnVO.setInitiaterName(rs.getString("REQUESTER_NAME"));

                loyalityStockTxnVO.setTxnStatus(rs.getString("STOCK_TRANSACTIONSTATUS"));

                loyalityStockTxnVO.setNetworkName(rs.getString("NETWORKNAME"));
                loyalityStockTxnVO.setNetworkCode(pNetworkCode);

                stockList.add(loyalityStockTxnVO);

            }
            }
        } catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityStockDAO[loadStockTransactionList]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityStockDAO[loadStockTransactionList]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
          
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting orderList size=" + stockList.size());
            }
        }
        return stockList;
    }

    /**
     * @param pCon
     * @param loyalityStockTxnVO
     * @return
     * @throws BTSLBaseException
     */
    public int addApproveNewStock(Connection pCon, LoyalityStockTxnVO loyalityStockTxnVO) throws BTSLBaseException {
       
    	  final String methodName = "addApproveNewStock";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered :: ");
        }
      
        
        ResultSet rs = null;
        int updateCount = 0;

        StringBuilder strBuff = new StringBuilder();
        strBuff.append("update loyalty_stock_transaction set  ");

        strBuff.append("txn_status=? where TXN_NO=?");

        try {
            Date date = new Date();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query :: " + strBuff.toString());
            }
            try(PreparedStatement pstmtSelect = pCon.prepareStatement(strBuff.toString());)
            {
            pstmtSelect.setString(1, PretupsI.YES);
            pstmtSelect.setString(2, loyalityStockTxnVO.getTxnNo());

            updateCount = pstmtSelect.executeUpdate();

        } 
        }catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityStockDAO[addApproveNewStock]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this,methodName, "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            log.error("loadProductsForStock", "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityStockDAO[addApproveNewStock]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
         
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
        
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting updateCount=" + updateCount);
            }
        }
        return updateCount;
    }

    /**
     * @param pCon
     * @param loyalityStockTxnVO
     * @return
     * @throws BTSLBaseException
     */
    public int updateLoyalityStock(Connection pCon, LoyalityStockTxnVO loyalityStockTxnVO) throws BTSLBaseException {
       
    	  final String methodName = "updateLoyalityStock";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered :: ");
        }
      
        
        ResultSet rs = null;
        int updateCount = 0;
        long stock = 0;
        long approvedstock = 0;
        long stockAvailable = 0;
        StringBuilder strBuff1 = new StringBuilder();
        StringBuilder strBuff = new StringBuilder();
        strBuff1.append("select LOYALTY_STOCK_AVAILABLE,APPROVED_QUANTITY,LOYALTY_STOCK from LOYALTY_STOCK where PRODUCT_CODE=? and network_code=?");

        strBuff.append("update loyalty_stock set loyalty_stock=? ,PREVIOUS_LOYALTY_STOCK=? ,MODIFIED_ON=?,MODIFIED_BY=?,LOYALTY_STOCK_AVAILABLE=?,APPROVED_QUANTITY=?  ");

        strBuff.append("where PRODUCT_CODE=? and network_code=?");

        try {
            Date date = new Date();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query :: " + strBuff1.toString());
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Query :: " + strBuff.toString());
            }

            try(PreparedStatement pstmtSelect = pCon.prepareStatement(strBuff.toString());PreparedStatement pstmtSelect1 = pCon.prepareStatement(strBuff1.toString());)
            {
            pstmtSelect1.setString(1, "ETOPUP");
            pstmtSelect1.setString(2, loyalityStockTxnVO.getNetworkCode());
            rs = pstmtSelect1.executeQuery();
            if (rs.next()) {
                stock = rs.getLong("LOYALTY_STOCK");
                stockAvailable = rs.getLong("LOYALTY_STOCK_AVAILABLE");
                approvedstock = rs.getLong("APPROVED_QUANTITY");
            }

            pstmtSelect.setLong(1, stock + loyalityStockTxnVO.getApprovedQuantity());
            pstmtSelect.setLong(2, stock);
            pstmtSelect.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(date));
            pstmtSelect.setString(4, loyalityStockTxnVO.getModifiedBy());
            pstmtSelect.setLong(5, stockAvailable - loyalityStockTxnVO.getApprovedQuantity());
            pstmtSelect.setLong(6, approvedstock + loyalityStockTxnVO.getApprovedQuantity());
            pstmtSelect.setString(7, "ETOPUP");
            pstmtSelect.setString(8, loyalityStockTxnVO.getNetworkCode());

            updateCount = pstmtSelect.executeUpdate();

        } 
        }catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityStockDAO[updateLoyalityStock]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addNewStock", "error.general.sql.processing");
        }// end of catch
        catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityStockDAO[updateLoyalityStock]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }// end of catch
        finally {
         
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
           
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting updateCount=" + updateCount);
            }
        }
        return updateCount;
    }

}

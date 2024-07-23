package com.btsl.pretups.loyaltymgmt.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.loyalitystock.businesslogic.LoyalityStockTxnVO;
import com.btsl.pretups.loyalitystock.businesslogic.LoyalityStockVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SqlParameterEncoder;

/**
 * @author 
 *
 */
public class LoyaltyPointsRedemptionDAO {
    private final Log log = LogFactory.getLog(LoyaltyPointsRedemptionDAO.class.getName());
    private LoyaltyPointsRedemptionQry loyaltyPointsRedemptionQry = (LoyaltyPointsRedemptionQry) ObjectProducer.getObject(QueryConstants.LOYALTY_POINTS_REDEMPTION_QRY, QueryConstants.QUERY_PRODUCER);
    
    public LoyaltyPointsRedemptionVO loadLMSUserDetails(Connection pCon, String pMsisdn, String p_productCode) throws BTSLBaseException {

        final String methodName = "loadLMSUserDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered pMsisdn:" + pMsisdn+",p_productCode="+p_productCode);
        }
        
        LoyaltyPointsRedemptionVO loyaltyPointsRedemptionVO = null;
        
        try {
            final StringBuilder selectQueryBuff = new StringBuilder(" SELECT u.user_id,u.user_name, u.network_code,u.login_id,u.parent_id, u.owner_id, u.msisdn,");
            selectQueryBuff.append(" u.employee_code, u.status userstatus, u.user_type, u.external_code, u.user_code, cat.category_code, cat.category_name,");
            selectQueryBuff.append(" ub.product_code,ub.ACCUMULATED_POINTS ");// ,p.product_short_code

            selectQueryBuff.append(" FROM users u,categories cat,user_phones uphones,bonus ub ");
            selectQueryBuff.append(" WHERE uphones.msisdn=?  AND uphones.user_id=u.user_id and u.status in('Y','S') and u.user_id=ub.user_id_or_msisdn and u.category_code=cat.category_code ");
			selectQueryBuff.append(" AND ub.product_code=? ");
            final String selectQuery = selectQueryBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "select query:" + selectQuery);
            }

            try(PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, pMsisdn);
			pstmtSelect.setString(2, p_productCode);

            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {
                loyaltyPointsRedemptionVO = new LoyaltyPointsRedemptionVO();
                loyaltyPointsRedemptionVO.setUserID(rs.getString("user_id"));
                loyaltyPointsRedemptionVO.setUserName(rs.getString("user_name"));
                loyaltyPointsRedemptionVO.setNetworkID(rs.getString("network_code"));
                loyaltyPointsRedemptionVO.setLoginID(rs.getString("login_id"));
                loyaltyPointsRedemptionVO.setParentID(rs.getString("parent_id"));
                loyaltyPointsRedemptionVO.setOwnerID(rs.getString("owner_id"));
                loyaltyPointsRedemptionVO.setMsisdn(rs.getString("msisdn"));
                loyaltyPointsRedemptionVO.setEmpCode(rs.getString("employee_code"));
                loyaltyPointsRedemptionVO.setStatus(rs.getString("userstatus"));
                loyaltyPointsRedemptionVO.setUserType(rs.getString("user_type"));
                loyaltyPointsRedemptionVO.setExternalCode(rs.getString("external_code"));
                loyaltyPointsRedemptionVO.setUserCode(rs.getString("user_code"));
                loyaltyPointsRedemptionVO.setCategoryCode(rs.getString("category_code"));
                loyaltyPointsRedemptionVO.setCategoryName(rs.getString("category_name"));
                loyaltyPointsRedemptionVO.setProductCode(rs.getString("product_code"));
                loyaltyPointsRedemptionVO.setCurrentLoyaltyPoints(rs.getString("ACCUMULATED_POINTS"));
                loyaltyPointsRedemptionVO.setProductShortCode(loyaltyPointsRedemptionVO.getProductCode());

            }
            return loyaltyPointsRedemptionVO;
        } 
            }
        }catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLMSUserDetails]", "",
                            "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLMSUserDetails]", "",
                            "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadChannelUserDetails", "error.general.processing");
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting loyaltyPointsRedemptionVO: " + loyaltyPointsRedemptionVO);
            }
        }
    }

    public ArrayList loadGiftItemsDetail(Connection pCon) throws BTSLBaseException {

        final String methodName = "loadGiftItemsDetail";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        
        LoyalityStockVO loyalityStockVO = null;
        ArrayList stockItemList = null;
        
        int size=0;
        try {
            final StringBuilder selectQueryBuff = new StringBuilder(
                            "select item_code,item_name, points,stock_available,status from loyality_items where status <> 'N' and stock_available > '0' order by item_code");

            final String selectQuery = selectQueryBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "select query:" + selectQuery);
            }

            try(PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery);ResultSet rs = pstmtSelect.executeQuery();)
            {
            stockItemList = new ArrayList();
            while (rs.next()) {
                loyalityStockVO = new LoyalityStockVO();
                loyalityStockVO.setItemCode(rs.getString("item_code"));
                loyalityStockVO.setItemName(rs.getString("item_name"));
                loyalityStockVO.setItemStockAvailable(rs.getInt("stock_available"));
                loyalityStockVO.setPerItemPoints(rs.getInt("points"));
                loyalityStockVO.setStatus(rs.getString("status"));
                stockItemList.add(loyalityStockVO);
            }
            
            if(!stockItemList.isEmpty())
            {
            	size=stockItemList.size();
            }
        }
        }catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadGiftItemsDetail]", "",
                            "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error("loadLMSUserDetails", "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadGiftItemsDetail]", "",
                            "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting stockItemList size: " + size);
            }
        }
        return stockItemList;
    }

    /**
     * @param pNetworkCode
     * @return
     * @throws BTSLBaseException
     */
    public LoyalityStockTxnVO checkLoyaltyStockDetails(String pNetworkCode) throws BTSLBaseException {
        final String methodName = "checkLoyaltyStockDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        PreparedStatement pstmt = null;
        LoyalityStockTxnVO loyalityStockTxnVO = null;
        ResultSet rs = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_STOCK_REQUIRED))).booleanValue()) {
                final StringBuilder selectStrBuff = new StringBuilder("SELECT LOYALTY_STOCK,PREVIOUS_LOYALTY_STOCK FROM LOYALTY_STOCK WHERE NETWORK_CODE=? AND PRODUCT_CODE= ?");
                final String sqlSelect = selectStrBuff.toString();
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "sqlSelect " + sqlSelect);
                }
                mcomCon = new MComConnection();
                con=mcomCon.getConnection();
                pstmt = con.prepareStatement(sqlSelect);
                pstmt.setString(1, pNetworkCode);
                pstmt.setString(2, "ETOPUP");
                rs = pstmt.executeQuery();
               
                while (rs.next()) {
                    loyalityStockTxnVO = new LoyalityStockTxnVO();
                    loyalityStockTxnVO.setPreviousStock(rs.getLong("PREVIOUS_LOYALTY_STOCK"));
                    loyalityStockTxnVO.setPostStock(rs.getLong("LOYALTY_STOCK"));
                }
            } else {
                loyalityStockTxnVO = new LoyalityStockTxnVO();
            }
        } catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[checkLoyaltyStockDetails]",
                            "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error("loadLMSUserDetails", "Exception " + ex.getMessage());
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[checkLoyaltyStockDetails]",
                            "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_STOCK_REQUIRED))).booleanValue()) {
            	try{
            		if (rs!= null){
            			rs.close();
            		}
            	}
            	catch (SQLException e){
            		log.error("An error occurred closing result set.", e);
            	}
            }
            try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
			if (mcomCon != null) {
				mcomCon.close("LoyaltyPointsRedemptionDAO#checkLoyaltyStockDetails");
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting ");
            }
        }
        return loyalityStockTxnVO;
    }

    /**
     * @param pCon
     * @param pLoyalityStockTxnVO
     * @return
     * @throws BTSLBaseException
     */
    public int updateLoyaltyStockDetails(Connection pCon, LoyalityStockTxnVO pLoyalityStockTxnVO) throws BTSLBaseException {
        final String methodName = "updateLoyaltyStockDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        
        int count = 0;
        try {
            final StringBuilder updateBuff = new StringBuilder(
                            "UPDATE LOYALTY_STOCK SET LOYALTY_STOCK= ?,PREVIOUS_LOYALTY_STOCK= ?, MODIFIED_BY=?, MODIFIED_ON=? WHERE NETWORK_CODE=? ");
            final String updateQuery = updateBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "sqlSelect " + updateQuery);
            }
            try(PreparedStatement pstmt = pCon.prepareStatement(updateQuery);)
            {
            pstmt.setLong(1, pLoyalityStockTxnVO.getPostStock());
            pstmt.setLong(2, pLoyalityStockTxnVO.getPreviousStock());
            pstmt.setString(3, pLoyalityStockTxnVO.getModifiedBy());
            pstmt.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(pLoyalityStockTxnVO.getModifiedOn()));
            pstmt.setString(5, pLoyalityStockTxnVO.getNetworkCode());

            count = pstmt.executeUpdate();

        }
        }catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "LoyaltyPointsRedemptionDAO[updateLoyaltyStockDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception " + ex.getMessage());
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "LoyaltyPointsRedemptionDAO[updateLoyaltyStockDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
           
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting ");
            }
        }
        return count;
    }

    /**
     * @param pLoyaltyPointsRedemptionVO
     * @param pRedepType
     * @return
     * @throws BTSLBaseException
     */
    public int updateUserLoyaltyPointsDetail(LoyaltyPointsRedemptionVO pLoyaltyPointsRedemptionVO, String pRedepType) throws BTSLBaseException {
        final String methodName = "updateUserLoyaltyPointsDetail";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        ResultSet rs = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        int count = 0;
        try {
            
            final String selectQuery = loyaltyPointsRedemptionQry.updateUserLoyaltyPointsDetailQry();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "selectQuery " + selectQuery);
            }
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            short index = 1;
            pstmt = con.prepareStatement(selectQuery);
            pstmt.setString(index++, pLoyaltyPointsRedemptionVO.getUserID());
            pstmt.setString(index++, pLoyaltyPointsRedemptionVO.getUserID());
			pstmt.setString(index++, pLoyaltyPointsRedemptionVO.getProductCode());
			pstmt.setString(index++, pLoyaltyPointsRedemptionVO.getProductCode());
            rs = pstmt.executeQuery();
            long accuPoint = 0L;
            if (rs.next()) {
                accuPoint = rs.getLong("ACCUMULATED_POINTS");
            }

            pstmt.clearParameters();

            long currAccuPoint = Long.parseLong(String.valueOf(pLoyaltyPointsRedemptionVO.getRedempLoyaltyPoint()));
            currAccuPoint = accuPoint - currAccuPoint;
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Point redeemed for user : " + pLoyaltyPointsRedemptionVO.getRedempLoyaltyPoint() + " , currently user have points = " + accuPoint);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, " current Points which user has after perfomring redeemption: " + currAccuPoint);
            }

            final StringBuilder updateBuff = new StringBuilder("UPDATE bonus SET ACCUMULATED_POINTS= ?,LAST_REDEMPTION_ID= ?, LAST_REDEMPTION_ON=?  , POINTS=?, TRANSFER_ID=? ");
			updateBuff.append(" WHERE USER_ID_OR_MSISDN=? and POINTS_DATE=(SELECT MAX(POINTS_DATE) FROM BONUS WHERE USER_ID_OR_MSISDN=? AND PRODUCT_CODE= ? ) ");
			updateBuff.append(" AND PRODUCT_CODE= ? ");
            final String updateQuery = updateBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "sqlSelect " + updateQuery);
            }
            pstmt1 = con.prepareStatement(updateQuery);
            index = 1;
            pstmt1.setLong(index++, currAccuPoint);
            pstmt1.setString(index++, pLoyaltyPointsRedemptionVO.getRedemptionID());
            pstmt1.setTimestamp(index++, BTSLUtil.getTimestampFromUtilDate(pLoyaltyPointsRedemptionVO.getModifiedOn()));
			pstmt1.setLong(index++, -(Long.parseLong(String.valueOf(pLoyaltyPointsRedemptionVO.getRedempLoyaltyPoint()))));
            pstmt1.setString(index++, pLoyaltyPointsRedemptionVO.getTransactionId());
            pstmt1.setString(index++, pLoyaltyPointsRedemptionVO.getUserID());
            pstmt1.setString(index++, pLoyaltyPointsRedemptionVO.getUserID());
			pstmt1.setString(index++, pLoyaltyPointsRedemptionVO.getProductCode());
			pstmt1.setString(index++, pLoyaltyPointsRedemptionVO.getProductCode());
            count = pstmt1.executeUpdate();
            if (count > 0) {
                con.commit();
            } else {
                con.rollback();
            }
        } catch (SQLException sqle) {
            try {
            	if(con != null){
            		con.rollback();
            	}
                
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "LoyaltyPointsRedemptionDAO[updateUserLoyaltyPointsDetail]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            try {
            	 if(con != null){
                 	con.rollback();
                 }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.error(methodName, "Exception " + ex.getMessage());
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "LoyaltyPointsRedemptionDAO[updateUserLoyaltyPointsDetail]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	if (mcomCon != null) {
				mcomCon.close("LoyaltyPointsRedemptionDAO#updateLoyaltyStockDetails");
				mcomCon = null;
			}
        	try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        	try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        	try{
                if (pstmt1!= null){
                	pstmt1.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
            if (log.isDebugEnabled()) {
                log.debug("updateLoyaltyStockDetails", "Exiting count= " + count);
            }
        }
        return count;
    }

    /**
     * @param pLoyalityStockVO
     * @param pUserId
     * @param pCurrentDate
     * @return
     * @throws BTSLBaseException
     */
    public boolean createItemBufferStock(LoyalityStockVO pLoyalityStockVO, String pUserId, Date pCurrentDate) throws BTSLBaseException {
        final String methodName = "createItemBufferStock";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        PreparedStatement pstmt = null;
        final PreparedStatement pstmtUsr = null;
        int itemCheck = 0;
        boolean bufferCreated = false;
        try {
            final StringBuilder updateItemBuffer = new StringBuilder("UPDATE LOYALITY_ITEMS SET STOCK_AVAILABLE= ? WHERE ITEM_CODE=? ");
            final String itemBufferQuery = updateItemBuffer.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "itemBufferQuery " + itemBufferQuery);
            }

            mcomCon = new MComConnection();
            con=mcomCon.getConnection();

            pstmt = con.prepareStatement(itemBufferQuery);
            final int updatedStock = pLoyalityStockVO.getItemStockAvailable() - pLoyalityStockVO.getStockItemBuffer();
            pstmt.setInt(1, updatedStock);
            pstmt.setString(2, pLoyalityStockVO.getItemCode());
            itemCheck = pstmt.executeUpdate();
            if (itemCheck > 0) {
                con.commit();
                bufferCreated = true;
            } else {
                con.rollback();
                bufferCreated = false;
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
        } catch (SQLException sqle) {
            try {
            	 if(con != null){
                 	con.rollback();
                 }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[createItemBufferStock]",
                            "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            try {
                if(con != null){
                	con.rollback();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            log.error(methodName, "Exception " + ex.getMessage());
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[createItemBufferStock]",
                            "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
			try{
		        if (pstmtUsr!= null){
		        	pstmtUsr.close();
		        }
		      }
		      catch (SQLException e){
		    	  log.error("An error occurred closing statement.", e);
		      }
			if (mcomCon != null) {
				mcomCon.close("LoyaltyPointsRedemptionDAO#createItemBufferStock");
				mcomCon = null;
			}
			   if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting  bufferCreated= " + bufferCreated);
            }
        }
        return bufferCreated;
    }

    /**
     * @param pLoyalityStockVO
     * @param pUserId
     * @param pCurrentDate
     * @return
     * @throws BTSLBaseException
     */
    public boolean createItemBufferRelease(LoyalityStockVO pLoyalityStockVO, String pUserId, Date pCurrentDate) throws BTSLBaseException {
        final String methodName = "createItemBufferRelease";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        PreparedStatement pstmtselect = null;
        PreparedStatement pstmt = null;
        final PreparedStatement pstmtUsr = null;
        int itemCheck = 0;
        ResultSet rs = null;
        boolean bufferCreated = false;
        String currentStockValue = null;
        try {
            final StringBuilder selectItemBuff = new StringBuilder("SELECT STOCK_AVAILABLE FROM LOYALITY_ITEMS WHERE ITEM_CODE=? ");
            final String selectBufferQuery = selectItemBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "selectBufferQuery " + selectBufferQuery);
            }

            final StringBuilder updateItemBuffer = new StringBuilder("UPDATE LOYALITY_ITEMS SET STOCK_AVAILABLE= ? WHERE ITEM_CODE=? ");
            final String itemBufferQuery = updateItemBuffer.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "itemBufferQuery " + itemBufferQuery);
            }

            mcomCon = new MComConnection();
            con=mcomCon.getConnection();

            pstmtselect = con.prepareStatement(selectBufferQuery);
            pstmtselect.setString(1, pLoyalityStockVO.getItemCode());
            rs = pstmtselect.executeQuery();
            if (rs.next()) {
                currentStockValue = rs.getString("STOCK_AVAILABLE");
            }
            if (BTSLUtil.isNullString(currentStockValue)) {
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
            pstmt = con.prepareStatement(itemBufferQuery);
            final int updatedStock = Integer.parseInt(currentStockValue) + pLoyalityStockVO.getStockItemBuffer();
            pstmt.setInt(1, updatedStock);
            pstmt.setString(2, pLoyalityStockVO.getItemCode());
            itemCheck = pstmt.executeUpdate();

            if (itemCheck > 0) {
                con.commit();
                bufferCreated = true;
            } else {
                bufferCreated = false;
                con.rollback();
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }

        } catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[createItemBufferRelease]",
                            "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception " + ex.getMessage());
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[createItemBufferRelease]",
                            "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        	try{
                if (pstmtselect!= null){
                	pstmtselect.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        	try{
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
        	try{
                if (pstmtUsr!= null){
                	pstmtUsr.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing statement.", e);
              }
			if (mcomCon != null) {
				mcomCon.close("LoyaltyPointsRedemptionDAO#updateItemCountDetail");
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
                log.debug("updateItemCountDetail", "Exiting  bufferCreated= " + bufferCreated);
            }
        }
        return bufferCreated;
    }

    /**
     * @param pCon
     * @param pLoyaltyPointsRedemptionVO
     * @return
     * @throws BTSLBaseException
     */
    public int updateRedemptionTxnDetails(Connection pCon, LoyaltyPointsRedemptionVO pLoyaltyPointsRedemptionVO) throws BTSLBaseException {
        final String methodName = "updateRedemptionTxnDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
       
        int count = 0;
        try {
            final StringBuilder insertBuff = new StringBuilder("Insert into LOYALTY_REDEMPTION (USER_MSISDN, USER_ID,");
            insertBuff.append(" REDEMPTION_ID,REDEMPTION_TYPE, REDEMPTION_DATE,POINTS_REDEEMED,");
            insertBuff.append(" STOCK_TRANSFERED, POINT_STOCK_MULT_FACTOR,CREATED_ON, CREATED_BY,");
            insertBuff.append(" STATUS, ERROR_CODE, ITEM_CODE, ITEM_QUANTITY,REFERENCE_NO ) Values  ");
            insertBuff.append(" ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )");
            final String insertQuery = insertBuff.toString();

            if (log.isDebugEnabled()) {
                log.debug(methodName, "insertQuery " + insertQuery);
            }

            try(PreparedStatement pstmt = pCon.prepareStatement(insertQuery);)
            {
            pstmt.setString(1, pLoyaltyPointsRedemptionVO.getMsisdn());
            pstmt.setString(2, pLoyaltyPointsRedemptionVO.getUserID());
            pstmt.setString(3, pLoyaltyPointsRedemptionVO.getRedemptionID());
            pstmt.setString(4, pLoyaltyPointsRedemptionVO.getRedempType());
            pstmt.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(pLoyaltyPointsRedemptionVO.getRedemptionDate()));
            pstmt.setInt(6, pLoyaltyPointsRedemptionVO.getRedempLoyaltyPoint());
            if (pLoyaltyPointsRedemptionVO.getRedempType().equalsIgnoreCase(PretupsI.REDEMP_TYPE_STOCK)) {
                pstmt.setInt(7, Integer.parseInt(pLoyaltyPointsRedemptionVO.getRedempLoyaltyAmount()));
            } else {
                pstmt.setInt(7, 0);
            }
            pstmt.setInt(8, Integer.parseInt(pLoyaltyPointsRedemptionVO.getMultFactor()));
            pstmt.setTimestamp(9, BTSLUtil.getTimestampFromUtilDate(pLoyaltyPointsRedemptionVO.getCreatedOn()));
            pstmt.setString(10, pLoyaltyPointsRedemptionVO.getCreatedBy());
            pstmt.setString(11, pLoyaltyPointsRedemptionVO.getRedempStatus());
            pstmt.setString(12, pLoyaltyPointsRedemptionVO.getErrorCode());
            pstmt.setString(13, pLoyaltyPointsRedemptionVO.getItemCode());
            pstmt.setInt(14, pLoyaltyPointsRedemptionVO.getRedempItemQuantity());
            pstmt.setString(15, pLoyaltyPointsRedemptionVO.getReferenceNo());
            count = pstmt.executeUpdate();
            }
        } catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "LoyaltyPointsRedemptionDAO[updateRedemptionTxnDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception " + ex.getMessage());
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "LoyaltyPointsRedemptionDAO[updateRedemptionTxnDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
           
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting count= " + count);
            }
        }
        return count;
    }

    /**
     * @param pCon
     * @param pItemCode
     * @return
     * @throws BTSLBaseException
     */
    public LoyalityStockVO loadLatestGiftItemsDetail(Connection pCon, String pItemCode) throws BTSLBaseException {

        final String methodName = "loadLatestGiftItemsDetail";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        
        LoyalityStockVO loyalityStockVO = null;
        
        try {
            final StringBuilder selectQueryBuff = new StringBuilder(
                            "select item_code,item_name, points,stock_available,status from loyality_items where status <> 'N' AND item_code=? ");

            final String selectQuery = selectQueryBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "select query:" + selectQuery);
            }

            try(PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, pItemCode);
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            while (rs.next()) {
                loyalityStockVO = new LoyalityStockVO();
                loyalityStockVO.setItemCode(rs.getString("item_code"));
                loyalityStockVO.setItemName(rs.getString("item_name"));
                loyalityStockVO.setItemStockAvailable(rs.getInt("stock_available"));
                loyalityStockVO.setPerItemPoints(rs.getInt("points"));
                loyalityStockVO.setStatus(rs.getString("status"));
            }
            }
            }
        } catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "LoyaltyPointsRedemptionDAO[loadLatestGiftItemsDetail]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error("loadLMSUserDetails", "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "LoyaltyPointsRedemptionDAO[loadLatestGiftItemsDetail]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting loyalityStockVO: " + loyalityStockVO);
            }
        }
        return loyalityStockVO;
    }

    /**
     * @param pCon
     * @param loyaltyPointsRedemptionVO
     * @return
     * @throws BTSLBaseException
     */
    public LoyaltyPointsRedemptionVO loaduserProfileRelatedDetails(Connection pCon, LoyaltyPointsRedemptionVO loyaltyPointsRedemptionVO) throws BTSLBaseException {

        final String methodName = "loaduserProfileRelatedDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        
        try {
        	
            final String selectQuery = loyaltyPointsRedemptionQry.loaduserProfileRelatedDetailsQry();
            if (log.isDebugEnabled()) {
                log.debug("loadLMSUserDetails", "select query:" + selectQuery);
            }

            try(PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, loyaltyPointsRedemptionVO.getUserID());
			pstmtSelect.setString(2, loyaltyPointsRedemptionVO.getProductCode() );
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {

                loyaltyPointsRedemptionVO.setParentContribution(rs.getInt("PRT_CONTRIBUTION"));
                loyaltyPointsRedemptionVO.setOperatorContribution(rs.getInt("OPT_CONTRIBUTION"));
                loyaltyPointsRedemptionVO.setParentID(rs.getString("PARENT_ID"));
                loyaltyPointsRedemptionVO.setSetId(rs.getString("set_id"));
                loyaltyPointsRedemptionVO.setVersion(rs.getString("version"));
				loyaltyPointsRedemptionVO.setUserLanguage(rs.getString("lang"));
				loyaltyPointsRedemptionVO.setUserCountry(rs.getString("country"));
			} else {
				//Independent of LMS Profile for redemption 
				loyaltyPointsRedemptionVO.setParentContribution(0);
				loyaltyPointsRedemptionVO.setOperatorContribution(100);
				loyaltyPointsRedemptionVO.setSetId("1");
				loyaltyPointsRedemptionVO.setVersion("1");
				loyaltyPointsRedemptionVO.setUserLanguage((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)));
				loyaltyPointsRedemptionVO.setUserCountry((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            }
            return loyaltyPointsRedemptionVO;
        } 
            }
        }catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLMSUserDetails]", "",
                            "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLMSUserDetails]", "",
                            "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting loyaltyPointsRedemptionVO: " + loyaltyPointsRedemptionVO);
            }
        }
    }

    /**
     * @param pCon
     * @param loyaltyPointsRedemptionVO
     * @return
     * @throws BTSLBaseException
     */
    public LoyaltyPointsRedemptionVO loadLMSParentUserDetails(Connection pCon, LoyaltyPointsRedemptionVO loyaltyPointsRedemptionVO) throws BTSLBaseException {

        final String methodName = "loadLMSParentUserDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        
        try {
            final StringBuilder selectQueryBuff = new StringBuilder(
                            "select u.msisdn,u.user_id,up.sms_pin,u.EXTERNAL_CODE from users u ,user_phones up where  u.user_id=? and u.user_id=up.user_id ");

            final String selectQuery = selectQueryBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "select query:" + selectQuery);
            }

            try(PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, loyaltyPointsRedemptionVO.getParentID());
            try(ResultSet rs = pstmtSelect.executeQuery();)
            {

            if (rs.next()) {

                loyaltyPointsRedemptionVO.setParentMsisdn(rs.getString("msisdn"));
                loyaltyPointsRedemptionVO.setParentEncryptedPin(rs.getString("sms_pin"));
                loyaltyPointsRedemptionVO.setExternalCode(rs.getString("EXTERNAL_CODE"));
            }
            return loyaltyPointsRedemptionVO;
            
           
        } 
            }
        }catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLMSUserDetails]", "",
                            "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "loaduserProfileRelatedDetails", "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLMSUserDetails]", "",
                            "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loaduserProfileRelatedDetails", "error.general.processing");
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting loyaltyPointsRedemptionVO: " + loyaltyPointsRedemptionVO);
            }
        }
    }

    /**
     * @param pCon
     * @param ArrayList<LoyaltyPointsRedemptionVO>
     * @return
     * @throws BTSLBaseException
     */
    public ArrayList<LoyaltyPointsRedemptionVO> loaduserProfileBonusDetails(Connection pCon, LoyaltyPointsRedemptionVO loyaltyPointsRedemptionVO) throws BTSLBaseException {

        final String methodName = "loaduserProfileBonusDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        PreparedStatement pstmtSelect = null;
        PreparedStatement pstmtSelect1 = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
		ArrayList<LoyaltyPointsRedemptionVO> arrayList=  new ArrayList<LoyaltyPointsRedemptionVO>(5);
        try {
            final StringBuilder selectQueryBuffer = new StringBuilder(" select cu.lms_profile from channel_users cu where cu.user_id=? ");// version
            // added
            // by
            // brajesh
            String selectQuery = selectQueryBuffer.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect = pCon.prepareStatement(selectQuery);
            pstmtSelect.setString(1, SqlParameterEncoder.encodeParams(loyaltyPointsRedemptionVO.getUserID()));
            rs1 = pstmtSelect.executeQuery();
            if (rs1.next()) {
                loyaltyPointsRedemptionVO.setSetId(SqlParameterEncoder.encodeParams(rs1.getString("lms_profile")));

            }
            final String setId = loyaltyPointsRedemptionVO.getSetId();
			if (log.isDebugEnabled()){
				log.debug(methodName, "setId = " + setId);
			 }
            final StringBuilder selectQueryBuff = new StringBuilder(
                            " SELECT distinct bn.ACCUMULATED_POINTS,pd.PRODUCT_SHORT_CODE,pd.PRODUCT_CODE,bn.version,psv.PRT_CONTRIBUTION,psv.OPT_CONTRIBUTION ");// version
            // added
            // by
            // brajesh
            selectQueryBuff.append(" FROM BONUS bn,PROFILE_SET_VERSION psv,products pd WHERE bn.USER_ID_OR_MSISDN=? ");
			if(!BTSLUtil.isNullString(loyaltyPointsRedemptionVO.getProductCode()) && loyaltyPointsRedemptionVO.getProductCode().contains(","))
				selectQueryBuff.append(" AND bn.POINTS_DATE IN ((SELECT MAX(POINTS_DATE) FROM BONUS  WHERE USER_ID_OR_MSISDN=? AND PRODUCT_CODE IN("+loyaltyPointsRedemptionVO.getProductCode()+") group by product_code))");
			else
				selectQueryBuff.append(" AND bn.POINTS_DATE=(SELECT MAX(POINTS_DATE) FROM BONUS  WHERE USER_ID_OR_MSISDN=? AND PRODUCT_CODE = ?)");
			selectQueryBuff.append(" AND bn.PRODUCT_CODE=pd.PRODUCT_CODE ");
            if (!BTSLUtil.isNullString(setId) && !"null".equalsIgnoreCase(setId)) {
                selectQueryBuff.append(" and psv.set_id=?");
            }
			if(!BTSLUtil.isNullString(loyaltyPointsRedemptionVO.getProductCode()) && loyaltyPointsRedemptionVO.getProductCode().contains(","))
				selectQueryBuff.append(" AND bn.product_code IN ("+loyaltyPointsRedemptionVO.getProductCode()+") ");
			else
				selectQueryBuff.append(" AND bn.product_code=? ");
            selectQuery = selectQueryBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "select query:" + selectQuery);
            }
            pstmtSelect1 = pCon.prepareStatement(selectQuery);
			int index = 1;
            pstmtSelect1.setString(index++, loyaltyPointsRedemptionVO.getUserID());
            pstmtSelect1.setString(index++, loyaltyPointsRedemptionVO.getUserID());
			if(!BTSLUtil.isNullString(loyaltyPointsRedemptionVO.getProductCode()) && loyaltyPointsRedemptionVO.getProductCode().contains(","))
				pstmtSelect1.setString(index++,loyaltyPointsRedemptionVO.getProductCode());
			else 
				pstmtSelect1.setString(index++,PretupsI.PRODUCT_ETOPUP);
            if (!BTSLUtil.isNullString(setId) && !"null".equalsIgnoreCase(setId)) {
				pstmtSelect1.setString(index++,setId);
            }
			if(!BTSLUtil.isNullString(loyaltyPointsRedemptionVO.getProductCode()) && loyaltyPointsRedemptionVO.getProductCode().contains(","))
				pstmtSelect1.setString(index++,loyaltyPointsRedemptionVO.getProductCode());
			else 
				pstmtSelect1.setString(index++,PretupsI.PRODUCT_ETOPUP);
            rs = pstmtSelect1.executeQuery();

            if (rs.next()) {
                loyaltyPointsRedemptionVO.setProductCode(rs.getString("product_code"));
                loyaltyPointsRedemptionVO.setCurrentLoyaltyPoints(rs.getString("ACCUMULATED_POINTS"));

                loyaltyPointsRedemptionVO.setProductShortCode(rs.getString("product_short_code"));
                loyaltyPointsRedemptionVO.setVersion(rs.getString("version"));
                loyaltyPointsRedemptionVO.setParentContribution(rs.getInt("PRT_CONTRIBUTION"));
                loyaltyPointsRedemptionVO.setOperatorContribution(rs.getInt("OPT_CONTRIBUTION"));
				LoyaltyPointsRedemptionVO loyaltyPointsRedemptionVO2 = new LoyaltyPointsRedemptionVO();
				loyaltyPointsRedemptionVO2.setProductCode(rs.getString("product_code"));
				loyaltyPointsRedemptionVO2.setCurrentLoyaltyPoints(rs.getString("ACCUMULATED_POINTS"));
				loyaltyPointsRedemptionVO2.setProductShortCode(rs.getString("product_short_code"));
				loyaltyPointsRedemptionVO2.setVersion(rs.getString("version"));
				loyaltyPointsRedemptionVO2.setParentContribution(rs.getInt("PRT_CONTRIBUTION"));
				loyaltyPointsRedemptionVO2.setOperatorContribution(rs.getInt("OPT_CONTRIBUTION"));
				if (log.isDebugEnabled()) {
					log.debug(methodName, "Product:Bonus = " + loyaltyPointsRedemptionVO2.getProductCode()+":"+loyaltyPointsRedemptionVO2.getCurrentLoyaltyPoints());
				}
				arrayList.add(loyaltyPointsRedemptionVO2);
					loyaltyPointsRedemptionVO2=null;
				}
				return arrayList;
        } catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "LoyaltyPointsRedemptionDAO[loaduserProfileBonusDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "LoyaltyPointsRedemptionDAO[loaduserProfileBonusDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	try{
                if (rs!= null){
                	rs.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
        	try{
                if (rs1!= null){
                	rs1.close();
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
        	try{
                if (pstmtSelect1!= null){
                	pstmtSelect1.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting loyaltyPointsRedemptionVO: " + loyaltyPointsRedemptionVO);
            }
        }
    }

    /**
     * @param pCon
     * @param loyaltyPointsRedemptionVO
     * @return
     * @throws BTSLBaseException
     */
    public int insertRedemtionDetails(Connection pCon, LoyaltyPointsRedemptionVO loyaltyPointsRedemptionVO) throws BTSLBaseException {

        final String methodName = "loaduserProfileDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        
        final ResultSet rs = null;
        int count = 0;
        int index = 1;
        try {
            final StringBuilder selectQueryBuff = new StringBuilder("INSERT INTO REDEMPTIONS(REFERENCE_ID,PROFILE_TYPE, REDEMPTION_TYPE, ");
            selectQueryBuff.append(" REDEMPTION_ID, REDEMPTION_DATE, PRODUCT_CODE,  POINTS_REDEEMED,");
            selectQueryBuff.append(" AMOUNT_TRANSFERED, CREATED_ON, CREATED_BY, MODIFIED_ON,MODIFIED_BY,USER_ID_OR_MSISDN)");
            selectQueryBuff.append(" values (?,?,?,?,?,?,?,?,?,?,?,?,?)");
            final String selectQuery = selectQueryBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug("loadLMSUserDetails", "select query:" + selectQuery);
            }

            try(PreparedStatement psmtInsertRedemption = pCon.prepareStatement(selectQuery);)
            {
            if (("STOCK").equals(loyaltyPointsRedemptionVO.getRedempType())) {
                psmtInsertRedemption.setString(index, loyaltyPointsRedemptionVO.getReferenceNo());
                index++;
            } else {
                psmtInsertRedemption.setString(index, loyaltyPointsRedemptionVO.getItemCode());
                index++;
            }
            psmtInsertRedemption.setString(index++, PretupsI.LMS_PROFILE_TYPE);
            psmtInsertRedemption.setString(index++, loyaltyPointsRedemptionVO.getRedempType());
            psmtInsertRedemption.setString(index++, loyaltyPointsRedemptionVO.getRedemptionID());
            psmtInsertRedemption.setDate(index++, BTSLUtil.getSQLDateFromUtilDate(loyaltyPointsRedemptionVO.getRedemptionDate()));
            psmtInsertRedemption.setString(index++, loyaltyPointsRedemptionVO.getProductShortCode());
            psmtInsertRedemption.setDouble(index++, Long.valueOf(loyaltyPointsRedemptionVO.getRedempLoyaltyPoint()));
            psmtInsertRedemption.setDouble(index++, loyaltyPointsRedemptionVO.getSumAmount());
            psmtInsertRedemption.setTimestamp(index++, BTSLUtil.getTimestampFromUtilDate(loyaltyPointsRedemptionVO.getRedemptionDate()));
            psmtInsertRedemption.setString(index++, loyaltyPointsRedemptionVO.getCreatedBy());
            psmtInsertRedemption.setTimestamp(index++, BTSLUtil.getTimestampFromUtilDate(loyaltyPointsRedemptionVO.getRedemptionDate()));
            psmtInsertRedemption.setString(index++, loyaltyPointsRedemptionVO.getCreatedBy());
            psmtInsertRedemption.setString(index++, loyaltyPointsRedemptionVO.getUserID());

            count = psmtInsertRedemption.executeUpdate();

            return count;
        } 
        }catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLMSUserDetails]", "",
                            "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("loaduserProfileRelatedDetails", "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLMSUserDetails]", "",
                            "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("loaduserProfileRelatedDetails", "error.general.processing");
        } finally {
        	try{
        		if (rs!= null){
        			rs.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting loyaltyPointsRedemptionVO: " + loyaltyPointsRedemptionVO);
            }
        }
    }

    /**
     * @param pCon
     * @param loyaltyPointsRedemptionVO
     * @return
     * @throws BTSLBaseException
     */
    public boolean isUserActive(Connection pCon, LoyaltyPointsRedemptionVO loyaltyPointsRedemptionVO) throws BTSLBaseException {

        final String methodName = "isUserActive";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered userid:" + loyaltyPointsRedemptionVO.getUserID());
        }
        
        boolean isuseractive = false;
        try {
            final StringBuilder selectQueryBuff = new StringBuilder(" SELECT u.user_id,u.user_name from users u ");
            selectQueryBuff.append(" where u.user_id=? and u.status='Y' ");

            final String selectQuery = selectQueryBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "select query:" + selectQuery);
            }

            try(PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, loyaltyPointsRedemptionVO.getUserID());


            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {

                isuseractive = true;

            }
            return isuseractive;
        }
            }
        }catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLMSUserDetails]", "",
                            "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLMSUserDetails]", "",
                            "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadChannelUserDetails", "error.general.processing");
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting loyaltyPointsRedemptionVO: " + loyaltyPointsRedemptionVO);
            }
        }
    }

    /**
     * @param pCon
     * @param loyaltyPointsRedemptionVO
     * @return
     * @throws BTSLBaseException
     */
    public boolean isUserInSuspended(Connection pCon, LoyaltyPointsRedemptionVO loyaltyPointsRedemptionVO) throws BTSLBaseException {

        if (log.isDebugEnabled()) {
            log.debug("isUserInSuspended", "Entered userid:" + loyaltyPointsRedemptionVO.getUserID());
        }
        
        boolean isuseractive = false;
        final String methodName = "isUserActive";
        try {
            final StringBuilder selectQueryBuff = new StringBuilder(" SELECT u.IN_SUSPEND from channel_users u ");
            selectQueryBuff.append(" where u.user_id=?  ");

            final String selectQuery = selectQueryBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug("isUserInSuspended", "select query:" + selectQuery);
            }

            try(PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, loyaltyPointsRedemptionVO.getUserID());


            try(ResultSet rs = pstmtSelect.executeQuery();)
            {
            if (rs.next()) {

                if ("Y".equals(rs.getString("IN_SUSPEND"))) {
                    isuseractive = true;
                }

            }
            return isuseractive;
        }
            }
        }catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLMSUserDetails]", "",
                            "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "isUserInSuspended", "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, "Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLMSUserDetails]", "",
                            "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "isUserInSuspended", "error.general.processing");
        } finally {
        	
            if (log.isDebugEnabled()) {
                log.debug("isUserInSuspended", "Exiting loyaltyPointsRedemptionVO: " + loyaltyPointsRedemptionVO);
            }
        }
    }

}

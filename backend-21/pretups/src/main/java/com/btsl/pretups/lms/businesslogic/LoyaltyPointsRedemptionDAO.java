package com.btsl.pretups.lms.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
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
import com.btsl.util.BTSLUtil;

/**
 * @author anubhav.pandey1
 *
 */
public class LoyaltyPointsRedemptionDAO {
    private static Log log = LogFactory.getLog(LoyaltyPointsRedemptionDAO.class.getName());

    /**
     * @param pCon
     * @param pMsisdn
     * @return
     * @throws BTSLBaseException
     */
    public LoyaltyPointsRedemptionVO loadLMSUserDetails(Connection pCon, String pMsisdn) throws BTSLBaseException {

        final String methodName = "loadLMSUserDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered pMsisdn:" + pMsisdn);
        }
         
        LoyaltyPointsRedemptionVO loyaltyPointsRedemptionVO = null;
         
        try {
            StringBuilder selectQueryBuff = new StringBuilder(" SELECT u.user_id,u.user_name, u.network_code,u.login_id,u.parent_id, u.owner_id, u.msisdn,");
            selectQueryBuff.append(" u.employee_code, u.status userstatus, u.user_type, u.external_code, u.user_code, cat.category_code, cat.category_name, ");
            selectQueryBuff.append(" ub.product_code,ub.LOYALTY_POINT, ub.PREVIOUS_LOYALTY_POINT,p.product_short_code,ub.ACCUMULATED_POINTS ");
            selectQueryBuff.append(" FROM users u,categories cat, user_balances ub, user_phones uphones, products p ");
            selectQueryBuff.append(" WHERE uphones.msisdn=? AND uphones.user_id=u.user_id AND ub.user_id = u.user_id AND u.status <> ? AND u.status <> ? ");
            selectQueryBuff.append(" AND ub.product_code= p.product_code AND u.category_code = cat.category_code ");

            String selectQuery = selectQueryBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "select query:" + selectQuery);
            }

            try(PreparedStatement pstmtSelect = pCon.prepareStatement(selectQuery);)
            {
            pstmtSelect.setString(1, pMsisdn);
            pstmtSelect.setString(2, PretupsI.USER_STATUS_DELETED);
            pstmtSelect.setString(3, PretupsI.USER_STATUS_CANCELED);

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
                loyaltyPointsRedemptionVO.setCurrentLoyaltyPoints(rs.getString("LOYALTY_POINT"));
                loyaltyPointsRedemptionVO.setPreviousLoyaltyPoints(rs.getString("PREVIOUS_LOYALTY_POINT"));
                loyaltyPointsRedemptionVO.setProductShortCode(rs.getString("product_short_code"));
    			loyaltyPointsRedemptionVO.setProductCode(rs.getString("product_code"));
                loyaltyPointsRedemptionVO.setCurrentLoyaltyPoints(rs.getString("ACCUMULATED_POINTS"));
                loyaltyPointsRedemptionVO.setProductShortCode(loyaltyPointsRedemptionVO.getProductCode());

            }
            return loyaltyPointsRedemptionVO;
        }
            }
        }catch (SQLException sqle) {
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLMSUserDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLMSUserDetails]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "loadChannelUserDetails", "error.general.processing",e);
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
        
        try {
            StringBuilder selectQueryBuff = new StringBuilder("select item_code,item_name, points,stock_available,status from loyality_items where status <> 'N' order by item_code");

            String selectQuery = selectQueryBuff.toString();
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
        }
        }catch (SQLException sqle) {
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadGiftItemsDetail]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadGiftItemsDetail]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
          
          
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting stockItemList size: " + stockItemList.size());
            }
        }
        return stockItemList;
    }

    public LoyalityStockTxnVO checkLoyaltyStockDetails(String pNetworkCode) throws BTSLBaseException {
        final String methodName = "checkLoyaltyStockDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        
        LoyalityStockTxnVO loyalityStockTxnVO = null;
        
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            StringBuilder selectStrBuff = new StringBuilder("SELECT LOYALTY_STOCK,PREVIOUS_LOYALTY_STOCK FROM LOYALTY_STOCK WHERE NETWORK_CODE=? AND PRODUCT_CODE= ?");
            String sqlSelect = selectStrBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "sqlSelect " + sqlSelect);
            }
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            try(PreparedStatement pstmt = con.prepareStatement(sqlSelect);)
            {
            pstmt.setString(1, pNetworkCode);
            pstmt.setString(2, "ETOPUP");
            try( ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                loyalityStockTxnVO = new LoyalityStockTxnVO();
                loyalityStockTxnVO.setPreviousStock(rs.getLong("PREVIOUS_LOYALTY_STOCK"));
                loyalityStockTxnVO.setPostStock(rs.getLong("LOYALTY_STOCK"));
            }
        } 
            }
        }catch (SQLException sqle) {
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[checkLoyaltyStockDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[checkLoyaltyStockDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
           
           
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
            StringBuilder updateBuff = new StringBuilder("UPDATE LOYALTY_STOCK SET LOYALTY_STOCK= ?,PREVIOUS_LOYALTY_STOCK= ?, MODIFIED_BY=?, MODIFIED_ON=? WHERE NETWORK_CODE=? ");
            String updateQuery = updateBuff.toString();
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
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[updateLoyaltyStockDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[updateLoyaltyStockDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
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
        

        Connection con = null;
        MComConnectionI mcomCon = null;
        int count = 0;
        try {
            StringBuilder updateBuff = new StringBuilder("UPDATE USER_BALANCES SET LOYALTY_POINT= ?,PREVIOUS_LOYALTY_POINT= ?, LOYALITY_POINT_MODIFIED_ON=? ");
            if (pRedepType.equalsIgnoreCase(PretupsI.REDEMP_TYPE_OTHER)) {
                updateBuff.append(",ITEM_BUFFER=? ");
            }
            updateBuff.append("WHERE USER_ID=? ");

            String updateQuery = updateBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "sqlSelect " + updateQuery);
            }
            mcomCon = new MComConnection();
            con=mcomCon.getConnection();
            try(PreparedStatement pstmt = con.prepareStatement(updateQuery);)
            {

            pstmt.setString(1, pLoyaltyPointsRedemptionVO.getCurrentLoyaltyPoints());
            pstmt.setString(2, pLoyaltyPointsRedemptionVO.getPreviousLoyaltyPoints());
            pstmt.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(pLoyaltyPointsRedemptionVO.getModifiedOn()));
            if (pRedepType.equalsIgnoreCase(PretupsI.REDEMP_TYPE_OTHER)) {
                pstmt.setInt(4, 0);
                pstmt.setString(5, pLoyaltyPointsRedemptionVO.getUserID());
            } else {
                pstmt.setString(4, pLoyaltyPointsRedemptionVO.getUserID());
            }
            count = pstmt.executeUpdate();
            if (count > 0) {
            	mcomCon.finalCommit();
            } else {
            	mcomCon.finalRollback();
            }
        }
        }catch (SQLException sqle) {
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[updateUserLoyaltyPointsDetail]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[updateUserLoyaltyPointsDetail]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
           
			if (mcomCon != null) {
				mcomCon.close("LoyaltyPointsRedemptionDAO#updateLoyaltyStockDetails");
				mcomCon = null;
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
        
        int itemCheck = 0;
        int usrCheck = 0;
        boolean bufferCreated = false;
        try {
            StringBuilder updateItemBuffer = new StringBuilder("UPDATE LOYALITY_ITEMS SET STOCK_AVAILABLE= ? WHERE ITEM_CODE=? ");
            String itemBufferQuery = updateItemBuffer.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "itemBufferQuery " + itemBufferQuery);
            }

            StringBuilder updateUserBuffer = new StringBuilder("UPDATE USER_BALANCES SET ITEM_BUFFER= ?, LOYALITY_POINT_MODIFIED_ON=? WHERE USER_ID=? ");
            String userBufferQuery = updateUserBuffer.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "sqlSelect " + userBufferQuery);
            }

            mcomCon = new MComConnection();
            con=mcomCon.getConnection();

            try(PreparedStatement pstmt = con.prepareStatement(itemBufferQuery);)
            {
            int updatedStock = pLoyalityStockVO.getItemStockAvailable() - pLoyalityStockVO.getStockItemBuffer();
            pstmt.setInt(1, updatedStock);
            pstmt.setString(2, pLoyalityStockVO.getItemCode());
            itemCheck = pstmt.executeUpdate();

            if (itemCheck > 0) {
                try (PreparedStatement pstmtUsr = con.prepareStatement(userBufferQuery);)
                {
                pstmtUsr.setInt(1, pLoyalityStockVO.getStockItemBuffer());
                pstmtUsr.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(pCurrentDate));
                pstmtUsr.setString(3, pUserId);
                usrCheck = pstmtUsr.executeUpdate();
            }
            }else {
            	mcomCon.finalRollback();
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
            if (usrCheck > 0) {
            	mcomCon.finalCommit();
                bufferCreated = true;
            } else {
            	mcomCon.finalRollback();
                bufferCreated = false;
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
        } 
        }catch (SQLException sqle) {
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[createItemBufferStock]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[createItemBufferStock]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
          
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

    public boolean createItemBufferRelease(LoyalityStockVO pLoyalityStockVO, String pUserId, Date pCurrentDate) throws BTSLBaseException {
        final String methodName = "createItemBufferRelease";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered ");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
         
        int itemCheck = 0;
        int usrCheck = 0;
        
        boolean bufferCreated = false;
        String currentStockValue = null;
        try {
            StringBuilder selectItemBuff = new StringBuilder("SELECT STOCK_AVAILABLE FROM LOYALITY_ITEMS WHERE ITEM_CODE=? ");
            String selectBufferQuery = selectItemBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "selectBufferQuery " + selectBufferQuery);
            }

            StringBuilder updateItemBuffer = new StringBuilder("UPDATE LOYALITY_ITEMS SET STOCK_AVAILABLE= ? WHERE ITEM_CODE=? ");
            String itemBufferQuery = updateItemBuffer.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "itemBufferQuery " + itemBufferQuery);
            }

            StringBuilder updateUserBuffer = new StringBuilder("UPDATE USER_BALANCES SET ITEM_BUFFER= ?, LOYALITY_POINT_MODIFIED_ON=? WHERE USER_ID=? ");
            String userBufferQuery = updateUserBuffer.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "sqlSelect " + userBufferQuery);
            }

            mcomCon = new MComConnection();
            con=mcomCon.getConnection();

            try(PreparedStatement pstmtselect = con.prepareStatement(selectBufferQuery);)
            {
            pstmtselect.setString(1, pLoyalityStockVO.getItemCode());
            try(ResultSet rs = pstmtselect.executeQuery();)
            {
            if (rs.next()) {
                currentStockValue = rs.getString("STOCK_AVAILABLE");
            }
            if (BTSLUtil.isNullString(currentStockValue)) {
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
            try(PreparedStatement pstmt = con.prepareStatement(itemBufferQuery);)
            {
            int updatedStock = Integer.parseInt(currentStockValue) + pLoyalityStockVO.getStockItemBuffer();
            pstmt.setInt(1, updatedStock);
            pstmt.setString(2, pLoyalityStockVO.getItemCode());
            itemCheck = pstmt.executeUpdate();

            if (itemCheck > 0) {
                try(PreparedStatement pstmtUsr = con.prepareStatement(userBufferQuery);)
                {
                pstmtUsr.setInt(1, 0);
                pstmtUsr.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(pCurrentDate));
                pstmtUsr.setString(3, pUserId);
                usrCheck = pstmtUsr.executeUpdate();
            }
            }else {
            	mcomCon.finalRollback();
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
            if (usrCheck > 0) {
            	mcomCon.finalCommit();
                bufferCreated = true;
            } else {
            	mcomCon.finalRollback();
                bufferCreated = false;
                throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
            }
        }
            }
            }
        }catch (SQLException sqle) {
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[createItemBufferRelease]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[createItemBufferRelease]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
        } finally {
           
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
            StringBuilder insertBuff = new StringBuilder("Insert into LOYALTY_REDEMPTION (USER_MSISDN, USER_ID,");
            insertBuff.append(" REDEMPTION_ID,REDEMPTION_TYPE, REDEMPTION_DATE,POINTS_REDEEMED,");
            insertBuff.append(" STOCK_TRANSFERED, POINT_STOCK_MULT_FACTOR,CREATED_ON, CREATED_BY,");
            insertBuff.append(" STATUS, ERROR_CODE, ITEM_CODE, ITEM_QUANTITY,REFERENCE_NO ) Values  ");
            insertBuff.append(" ( ?,?,?,?,?,?,?,?,?,?,?,?,?,?,? )");
            String insertQuery = insertBuff.toString();

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
        }catch (SQLException sqle) {
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[updateRedemptionTxnDetails]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[updateRedemptionTxnDetails]", "", "", "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",ex);
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
            StringBuilder selectQueryBuff = new StringBuilder("select item_code,item_name, points,stock_available,status from loyality_items where status <> 'N' AND item_code=? ");

            String selectQuery = selectQueryBuff.toString();
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
        }catch (SQLException sqle) {
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLatestGiftItemsDetail]", "", "", "", "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing",sqle);
        } catch (Exception e) {
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyaltyPointsRedemptionDAO[loadLatestGiftItemsDetail]", "", "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing",e);
        } finally {
           
          
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting loyalityStockVO: " + loyalityStockVO);
            }
        }
        return loyalityStockVO;
    }

}

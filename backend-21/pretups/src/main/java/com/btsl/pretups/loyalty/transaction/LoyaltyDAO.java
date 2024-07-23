package com.btsl.pretups.loyalty.transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;

/**
 * @author 
 *
 */
public class LoyaltyDAO {

    private Log log = LogFactory.getLog(this.getClass().getName());

    /**
     * @param con
     * @param loyaltyVO
     * @return
     * @throws BTSLBaseException
     */
    public int updateLoyalityPointForExpiry(Connection con, LoyaltyVO loyaltyVO) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("getLoyalityPointForExpiry Method.....", "");
        }
        final String methodName = "updateLoyalityPointForExpiry";
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        Date date = null;
        date = new Date();
        int count = 0;
        long pLoyaltypoint = 0;
        long updatedloyaltypoint = 0;
        ResultSet rs = null;
        try {

            StringBuilder selectstrBuff = new StringBuilder("select sum(b.accumulated_points) from bonus b, bonus ba ");
            selectstrBuff.append("where b.USER_ID_OR_MSISDN=ba.USER_ID_OR_MSISDN and b.POINTS_DATE=ba.POINTS_DATE and b.POINTS_DATE= ");
            selectstrBuff.append("(select max(POINTS_DATE) from bonus where USER_ID_OR_MSISDN=b.USER_ID_OR_MSISDN ");
            selectstrBuff.append("and USER_ID_OR_MSISDN=ba.USER_ID_OR_MSISDN )");

            StringBuilder updateStrBuffStock = new StringBuilder("UPDATE LOYALTY_STOCK SET EXPIRED_POINTS= EXPIRED_POINTS + ?,EXPIRED_POINTS_MODIFIED_ON= ? ");
            StringBuilder updateStrBuffUsers = new StringBuilder("UPDATE bonus SET ACCUMULATED_POINTS=0 where PROFILE_TYPE='LMS'");

            String sqlSelect = selectstrBuff.toString();
            String sqlUpdateStock = updateStrBuffStock.toString();
            String sqlUpdateUsers = updateStrBuffUsers.toString();
            if (log.isDebugEnabled()) {
                log.debug("getLoyalityPointForExpiry Method.....sqlSelect ", "" + sqlSelect);
            }

            pstmt = con.prepareStatement(sqlSelect);

            rs = pstmt.executeQuery();
            while (rs.next()) {

                loyaltyVO.setTotalLoyalityPointsSum(rs.getLong(1));
            }
            if (log.isDebugEnabled()) {
                log.debug("getLoyalityPointForExpiry Method.....sqlUpdate ", "" + sqlUpdateStock + "previous loyaltypoint " + pLoyaltypoint + "CurrentLoyaltypoint " + updatedloyaltypoint);
            }

            pstmt1 = con.prepareStatement(sqlUpdateStock);
            pstmt1.setLong(1, loyaltyVO.getTotalLoyalityPointsSum());
            pstmt1.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(date));

            count = pstmt1.executeUpdate();
            if (log.isDebugEnabled()) {
                log.debug("getLoyalityPointForExpiry Method.....sqlUpdate ", "" + sqlUpdateUsers);
            }
            pstmt2 = con.prepareStatement(sqlUpdateUsers);
            count = pstmt2.executeUpdate();
            if (count < 1) {
                throw new BTSLBaseException(this, "getLoyalityPointForExpiry", PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);
            }
        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            throw new BTSLBaseException(this, "getLoyalityPointForExpiry", PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);

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
               if (pstmt!= null){
            	   pstmt.close();
               }
             }
             catch (SQLException e){
           	  log.error("An error occurred closing result set.", e);
             }
           try{
               if (pstmt1!= null){
            	   pstmt1.close();
               }
             }
             catch (SQLException e){
           	  log.error("An error occurred closing result set.", e);
             }
           try{
               if (pstmt2!= null){
            	   pstmt2.close();
               }
             }
             catch (SQLException e){
           	  log.error("An error occurred closing result set.", e);
             }
            if (log.isDebugEnabled()) {
                log.debug("getLoyalityPointForExpiry", "Exiting with Count :" + count);
            }
        }
        return count;

    }

    /**
     * @param con
     * @param loyaltyVO
     * @throws BTSLBaseException
     */
    public void getLoyalityPointsUsedStatus(Connection con, LoyaltyVO loyaltyVO) throws BTSLBaseException {
        if (log.isDebugEnabled()) {
            log.debug("getLoyalityPointForExpiry Method.....", "");
        }
        final String methodName = "getLoyalityPointsUsedStatus";
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        Date date = null;
        date = new Date();
        int count = 0;
        long pLoyaltypoint = 0;
        long c_loyaltypoint = 0;
        long updatedloyaltypoint = 0;
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        try {

            StringBuilder updateStrBuffStock = new StringBuilder("select loyalty_stock,LOYALTY_STOCK_AVAILABLE,APPROVED_QUANTITY,EXPIRED_POINTS  from loyalty_stock");
            StringBuilder updateStrBuffUsr = new StringBuilder("select sum(b.accumulated_points) from bonus b, bonus ba where b.USER_ID_OR_MSISDN=ba.USER_ID_OR_MSISDN and b.POINTS_DATE=ba.POINTS_DATE and b.POINTS_DATE=(select max(POINTS_DATE) from bonus where USER_ID_OR_MSISDN=b.USER_ID_OR_MSISDN and USER_ID_OR_MSISDN=ba.USER_ID_OR_MSISDN )");
            StringBuilder updateStrBuffRed = new StringBuilder("Select sum(POINTS_REDEEMED) from REDEMPTIONS where PROFILE_TYPE='LMS'");

            String sqlUpdateStock = updateStrBuffStock.toString();
            String sqlUpdateUsr = updateStrBuffUsr.toString();
            String sqlUpdateRed = updateStrBuffRed.toString();
            if (log.isDebugEnabled()) {
                log.debug("getLoyalityPointForExpiry Method.....sqlSelect ", "" + sqlUpdateStock);
            }

            pstmt = con.prepareStatement(sqlUpdateStock);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                loyaltyVO.setLoyalityPointsStock(rs.getLong("loyalty_stock"));
                loyaltyVO.setLoyalityPointsInitiated(rs.getLong("APPROVED_QUANTITY"));
                loyaltyVO.setLoyalityPointsExpired(rs.getLong("EXPIRED_POINTS"));
            }

         
            if (log.isDebugEnabled()) {
                log.debug("getLoyalityPointForExpiry Method.....sqlSelect ", "" + sqlUpdateUsr);
            }
            pstmt1 = con.prepareStatement(sqlUpdateUsr);
            rs1 = pstmt1.executeQuery();
            while (rs1.next()) {
                loyaltyVO.setLoyalityPointsUsers(rs1.getLong(1));
            }
          
            if (log.isDebugEnabled()) {
                log.debug("getLoyalityPointForExpiry Method.....sqlSelect ", "" + sqlUpdateRed);
            }

            pstmt2 = con.prepareStatement(sqlUpdateRed);
            rs2 = pstmt2.executeQuery();
            while (rs2.next()) {
                loyaltyVO.setLoyalityPointsRedempted(rs2.getLong(1));
            }
          

        } catch (Exception ex) {
            log.errorTrace(methodName, ex);
            throw new BTSLBaseException(this, "getLoyalityPointForExpiry", PretupsErrorCodesI.LOYALTY_PROCESSING_EXCEPTION);

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
               if (rs2!= null){
            	   rs2.close();
               }
             }
             catch (SQLException e){
           	  log.error("An error occurred closing result set.", e);
             }
           try{
               if (pstmt!= null){
            	   pstmt.close();
               }
             }
             catch (SQLException e){
           	  log.error("An error occurred closing result set.", e);
             }
           try{
               if (pstmt1!= null){
            	   pstmt1.close();
               }
             }
             catch (SQLException e){
           	  log.error("An error occurred closing result set.", e);
             }
           try{
               if (pstmt2!= null){
            	   pstmt2.close();
               }
             }
             catch (SQLException e){
           	  log.error("An error occurred closing result set.", e);
             }
            if (log.isDebugEnabled()) {
                log.debug("getLoyalityPointForExpiry", "Exiting with Count :" + count);
            }
        }

    }

   
    /**
     * @param pLoyaltyVO
     * @return
     */
    public LoyaltyVO validateUserAndUploadPoints(LoyaltyVO pLoyaltyVO) {
       
    	 final String methodName = "validateUserAndUploadPoints";
    	if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: pLoyaltyVO" + pLoyaltyVO);
        }
       
        Connection con = null;
        String userId = null;
        String currentLoyaltyPoints = null;
        String previousLoyaltyPoints = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        PreparedStatement psmtUpdate = null;
        int updateCount = 0;
        String errorComment = null;
        Date currentDate = new Date();
        try {
            StringBuilder selectBuff = new StringBuilder();
            selectBuff.append("SELECT UP.USER_ID,UB.LOYALTY_POINT,UB.PREVIOUS_LOYALTY_POINT FROM ");
            selectBuff.append("USER_BALANCES UB, USER_PHONES UP, USERS U WHERE ");
            selectBuff.append("UP.USER_ID=UB.USER_ID AND U.USER_ID=UP.USER_ID AND ");
            selectBuff.append("U.STATUS <> 'N' AND UP.MSISDN=? ");
            String sqlSelect = selectBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }

            StringBuilder updateBuff = new StringBuilder();
            updateBuff.append("UPDATE USER_BALANCES SET LOYALTY_POINT= ?,PREVIOUS_LOYALTY_POINT= ?, LOYALITY_POINT_MODIFIED_ON=? ");
            updateBuff.append(" WHERE USER_ID=?");
            String sqlUpdate = updateBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY sqlUpdate=" + sqlUpdate);
            }

            con = OracleUtil.getSingleConnection();
            pstmt = con.prepareStatement(sqlSelect);
            pstmt.setString(1, pLoyaltyVO.getReciverMsisdn());
            rs = pstmt.executeQuery();
            if (rs.next()) {
                userId = rs.getString("USER_ID");
                currentLoyaltyPoints = rs.getString("LOYALTY_POINT");
                previousLoyaltyPoints = rs.getString("PREVIOUS_LOYALTY_POINT");
            } else {
                if (!BTSLUtil.isNullString(pLoyaltyVO.getComments())) {
                    errorComment = pLoyaltyVO.getComments() + " No active channel user exist with corresponding MSISDN";
                    pLoyaltyVO.setComments(errorComment);
                    pLoyaltyVO.setErrorFlag(true);
                } else {
                    pLoyaltyVO.setComments("No active channel user exist with corresponding MSISDN");
                    pLoyaltyVO.setErrorFlag(true);
                }
                return pLoyaltyVO;
            }

            previousLoyaltyPoints = currentLoyaltyPoints;
            currentLoyaltyPoints = String.valueOf(Integer.parseInt(currentLoyaltyPoints.trim()) + Integer.parseInt(pLoyaltyVO.getLoyaltyPoint().trim()));

            psmtUpdate = con.prepareStatement(sqlUpdate);
            psmtUpdate.setString(1, currentLoyaltyPoints);
            psmtUpdate.setString(2, previousLoyaltyPoints);
            psmtUpdate.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(currentDate));
            psmtUpdate.setString(4, userId);
            updateCount = psmtUpdate.executeUpdate();
            if (updateCount > 0) {
                con.commit();
                pLoyaltyVO.setComments("Processed Succussfully");
                pLoyaltyVO.setErrorFlag(false);
            } else {
                con.rollback();
                pLoyaltyVO.setComments("Failed");
                pLoyaltyVO.setErrorFlag(true);
            }
        } catch (Exception ex) {
            log.error(methodName, "SQLException: " + ex.getMessage());
            log.errorTrace(methodName, ex);
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
                if (pstmt!= null){
                	pstmt.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
            try{
                if (psmtUpdate!= null){
                	psmtUpdate.close();
                }
              }
              catch (SQLException e){
            	  log.error("An error occurred closing result set.", e);
              }
           OracleUtil.closeQuietly(con);
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exit");
            }
        }
        return pLoyaltyVO;
    }

}

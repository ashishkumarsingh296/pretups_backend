package com.web.pretups.lms.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.lms.businesslogic.LoyalityVO;
import com.btsl.pretups.lms.businesslogic.RewardDetailsVO;
import com.btsl.pretups.lms.web.LMSForm;
import com.btsl.pretups.loyalty.transaction.LoyaltyVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;


public class LoyalityWebDAO {
    private static Log log = LogFactory.getFactory().getInstance(LoyalityWebDAO.class.getName());
    private Date date = new Date();

    public int addPromotionDetails(Connection p_con, LMSForm lmsForm) throws BTSLBaseException {
        int addCount = -1;
        final String methodName = "addPromotionDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entering VO ");
        }
        PreparedStatement pstmtInsert = null;
        final StringBuilder insertQueryBuff = new StringBuilder("INSERT INTO promotion_details ");
        insertQueryBuff.append("(PROMOTION_ID,PROMOTION_NAME,PROMOTION_START_DATE,");
        insertQueryBuff.append("PROMOTION_END_DATE,SERVICE_TYPE,STATUS, ");
        insertQueryBuff.append("CREATED_BY,CREATED_ON,MODIFIED_BY,");
        insertQueryBuff.append("MODIFIED_ON,PAYEE_ALLOWED,PAYEE_HIERARCHY_ALLOWED,PAYEE_REGISTERER_ALLOWED,PAYER_ALLOWED,PAYER_HIERARCHY_ALLOWED,PAYER_REGISTERER_ALLOWED) ");
        insertQueryBuff.append(" VALUES(?, ?, ? ,? ,? ,? ,? ,? ,? ,?,?,?,?,?,?,?)");
        final String insertQuery = insertQueryBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Insert Query= " + insertQuery);
        }
        try {
            pstmtInsert =p_con.prepareStatement(insertQuery);

            pstmtInsert.setString(1, lmsForm.getPromotionID());
            pstmtInsert.setString(2, lmsForm.getPromotionName());
            pstmtInsert.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(lmsForm.getApplicableFromDate()));
            pstmtInsert.setTimestamp(4, BTSLUtil.getTimestampFromUtilDate(lmsForm.getApplicableToDate()));
            pstmtInsert.setString(5, lmsForm.getServiceType());
            pstmtInsert.setString(6, PretupsI.NEW);
            pstmtInsert.setString(7, lmsForm.getCreatedBy());
            pstmtInsert.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(date));
            pstmtInsert.setString(9, lmsForm.getModifiedBy());
            pstmtInsert.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(date));
            pstmtInsert.setString(11, lmsForm.getPayee());
            pstmtInsert.setString(12, lmsForm.getPayeeHierarchy());
            pstmtInsert.setString(13, lmsForm.getRegisteresPayee());
            pstmtInsert.setString(14, lmsForm.getPayer());
            pstmtInsert.setString(15, lmsForm.getPayerHierarchy());
            pstmtInsert.setString(16, lmsForm.getRegisteresPayer());
            addCount = pstmtInsert.executeUpdate();
        }

        catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[addPromotionDetails]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            log.error("addInterfaceDetails", " Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[addPromotionDetails]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
        	try{
        		if (pstmtInsert!= null){
        			pstmtInsert.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting addCount " + addCount);
            }
        }

        return addCount;
    }

    public int addRewardDetails(Connection p_con, LMSForm lmsForm) throws BTSLBaseException {
        int addCount = -1;
        final String methodName = "addRewardDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entering VO ");
        }
        PreparedStatement pstmtInsert = null;
        final StringBuilder insertQueryBuff = new StringBuilder("INSERT INTO REWARD_DEFINITION ");
        insertQueryBuff.append("(REWARD_ID,PROMOTION_ID,STATUS,");
        insertQueryBuff.append("CREATED_BY,CREATED_ON,MODIFIED_BY,");
        insertQueryBuff.append("MODIFIED_ON) ");
        insertQueryBuff.append(" VALUES(?, ?, ? ,? ,? ,? ,? )");
        final String insertQuery = insertQueryBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Insert Query= " + insertQuery);
        }
        try {
            pstmtInsert =p_con.prepareStatement(insertQuery);

            pstmtInsert.setString(1, lmsForm.getRewardID());
            pstmtInsert.setString(2, lmsForm.getPromotionID());

            pstmtInsert.setString(3, PretupsI.YES);
            pstmtInsert.setString(4, lmsForm.getModifiedBy());
            pstmtInsert.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(date));
            pstmtInsert.setString(6, lmsForm.getModifiedBy());
            pstmtInsert.setTimestamp(7, BTSLUtil.getTimestampFromUtilDate(date));

            addCount = pstmtInsert.executeUpdate();
        }

        catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[addInterfaceDetails]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addInterfaceDetails", "error.general.sql.processing");
        }

        catch (Exception e) {
            log.error(methodName, " Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[addInterfaceDetails]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addInterfaceDetails", "error.general.processing");
        }

        finally {
        	try{
        		if (pstmtInsert!= null){
        			pstmtInsert.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting addCount " + addCount);
            }
        }

        return addCount;
    }

    public int addRangeForReward(Connection p_con, ArrayList slablist, String rewardID) throws BTSLBaseException {
        final int length = 10;
        int addCount = -1;
        final String methodName = "addRangeForReward";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entering VO ");
        }
        PreparedStatement pstmtInsert = null;
        final StringBuilder insertQueryBuff = new StringBuilder("INSERT INTO REWARD_RANGE ");
        insertQueryBuff.append("(REWARD_ID,REWARD_RANGE_ID,FROM_AMOUNT,");
        insertQueryBuff.append("TO_AMOUNT,REWARDED_TO,VALUE,TYPE )");
        insertQueryBuff.append(" VALUES(?, ?, ? ,? ,? ,?,? )");
        final String insertQuery = insertQueryBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Insert Query= " + insertQuery);
        }
        try {
            pstmtInsert =p_con.prepareStatement(insertQuery);

            RewardDetailsVO detailVO = null;
            ArrayList list;
            list = new ArrayList();
            for (int i = 0, j = slablist.size(); i < j; i++) {
                detailVO = (RewardDetailsVO) slablist.get(i);
                pstmtInsert.setString(1, rewardID);
                pstmtInsert.setString(2, BTSLUtil.padZeroesToLeft((IDGenerator.getNextID(PretupsI.RWD_RANGE, PretupsI.ALL)) + "", length));
                pstmtInsert.setLong(3, detailVO.getStartRange());
                pstmtInsert.setLong(4, detailVO.getEndRange());
                pstmtInsert.setString(5, detailVO.getRewardedTo());
                pstmtInsert.setDouble(6, detailVO.getCommRate());
                pstmtInsert.setString(7, detailVO.getCommType());

                addCount = pstmtInsert.executeUpdate();

                pstmtInsert.clearParameters();
                // check the status of the insert
                if (addCount <= 0) {
                    throw new BTSLBaseException(this, "addCommissionProfileDetailsList", "error.general.sql.processing");
                }
            }

        }

        catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[addRangeForReward]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            log.error(methodName, " Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[addRangeForReward]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
        	try{
        		if (pstmtInsert!= null){
        			pstmtInsert.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting addCount " + addCount);
            }
        }

        return addCount;
    }

    public int updatePromotionDetails(Connection p_con, LMSForm lmsForm) throws BTSLBaseException {
        int addCount = -1;
        final String methodName = "updatePromotionDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entering VO ");
        }
        PreparedStatement pstmtInsert = null;
        final StringBuilder insertQueryBuff = new StringBuilder("update  promotion_details SET ");
        insertQueryBuff.append("PROMOTION_NAME=?,PROMOTION_START_DATE=?,");
        insertQueryBuff.append("PROMOTION_END_DATE=?,SERVICE_TYPE=?, ");
        insertQueryBuff.append("MODIFIED_BY=?,");

        insertQueryBuff
            .append("MODIFIED_ON=?,PAYEE_ALLOWED=?,PAYEE_HIERARCHY_ALLOWED=?,PAYEE_REGISTERER_ALLOWED=?,PAYER_ALLOWED=?,PAYER_HIERARCHY_ALLOWED=?,PAYER_REGISTERER_ALLOWED=? ");

        insertQueryBuff.append(" where promotion_id=? ");
        final String insertQuery = insertQueryBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Insert Query= " + insertQuery);
        }
        try {
            pstmtInsert = p_con.prepareStatement(insertQuery);

            pstmtInsert.setString(1, lmsForm.getPromotionName());
            pstmtInsert.setTimestamp(2, BTSLUtil.getTimestampFromUtilDate(lmsForm.getApplicableFromDate()));
            pstmtInsert.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(lmsForm.getApplicableToDate()));
            pstmtInsert.setString(4, lmsForm.getServiceType());

            pstmtInsert.setString(5, lmsForm.getModifiedBy());
            pstmtInsert.setTimestamp(6, BTSLUtil.getTimestampFromUtilDate(date));
            pstmtInsert.setString(7, lmsForm.getPayee());
            pstmtInsert.setString(8, lmsForm.getPayeeHierarchy());
            pstmtInsert.setString(9, lmsForm.getRegisteresPayee());
            pstmtInsert.setString(10, lmsForm.getPayer());
            pstmtInsert.setString(11, lmsForm.getPayerHierarchy());
            pstmtInsert.setString(12, lmsForm.getRegisteresPayer());
            pstmtInsert.setString(13, lmsForm.getPromotionID());
            addCount = pstmtInsert.executeUpdate();
        }

        catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[updatePromotionDetails]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addInterfaceDetails", "error.general.sql.processing");
        }

        catch (Exception e) {
            log.error("addInterfaceDetails", " Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[updatePromotionDetails]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addInterfaceDetails", "error.general.processing");
        }

        finally {
        	try{
        		if (pstmtInsert!= null){
        			pstmtInsert.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting addCount " + addCount);
            }
        }

        return addCount;
    }

    public ArrayList loadpromotionList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadpromotionList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered:");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        LoyalityVO loyalityVO = null;
        ArrayList list = null;
        ArrayList list1 = null;
        ListValueVO listValueVO = null;
        listValueVO = new ListValueVO();
        try {
            final StringBuilder strBuff = new StringBuilder();
            list = new ArrayList();
            list1 = new ArrayList();
            strBuff.append("SELECT promotion_id,promotion_name,PROMOTION_START_DATE, ");
            strBuff.append("PROMOTION_END_DATE,SERVICE_TYPE,STATUS, ");
            strBuff.append("PAYEE_ALLOWED,PAYEE_HIERARCHY_ALLOWED,PAYEE_REGISTERER_ALLOWED,PAYER_ALLOWED,PAYER_HIERARCHY_ALLOWED,PAYER_REGISTERER_ALLOWED");
            strBuff.append(" from promotion_details where status not in (?)");

            final String sqlSelect = strBuff.toString();

            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.NO);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                list1 = new ArrayList();
                loyalityVO = new LoyalityVO();
                loyalityVO.setPromotionID(rs.getString("promotion_id"));
                loyalityVO.setPromotionName(rs.getString("promotion_name"));
                loyalityVO.setServiceType(rs.getString("SERVICE_TYPE"));
                loyalityVO.setApplicableFromDate(rs.getTimestamp("PROMOTION_START_DATE"));
                loyalityVO.setApplicableToDate(rs.getTimestamp("PROMOTION_END_DATE"));
                loyalityVO.setPayee(rs.getString("PAYEE_ALLOWED"));
                loyalityVO.setPayeeHierarchy(rs.getString("PAYEE_HIERARCHY_ALLOWED"));
                loyalityVO.setPayer(rs.getString("PAYER_ALLOWED"));
                loyalityVO.setPayerHierarchy(rs.getString("PAYER_HIERARCHY_ALLOWED"));
                loyalityVO.setRegisteresPayee(rs.getString("PAYEE_REGISTERER_ALLOWED"));
                loyalityVO.setRegisteresPayer(rs.getString("PAYER_REGISTERER_ALLOWED"));
                loyalityVO.setStatus(rs.getString("STATUS"));
                if (!BTSLUtil.isNullString(loyalityVO.getPayee())) {
                    if ("Y".equals(loyalityVO.getPayee())) {
                        listValueVO = new ListValueVO(PretupsI.PAYEE, PretupsI.PAYEE);
                        list1.add(listValueVO);
                    }
                }
                if (!BTSLUtil.isNullString(loyalityVO.getPayeeHierarchy())) {
                    if ("Y".equals(loyalityVO.getPayeeHierarchy())) {
                        listValueVO = new ListValueVO(PretupsI.PAYEE_HIERARCHY, PretupsI.PAYEE_HIERARCHY);
                        list1.add(listValueVO);
                    }
                }
                if (!BTSLUtil.isNullString(loyalityVO.getRegisteresPayee())) {
                    if ("Y".equals(loyalityVO.getRegisteresPayee())) {
                        listValueVO = new ListValueVO(PretupsI.PAYEE_REGISTERER, PretupsI.PAYEE_REGISTERER);
                        list1.add(listValueVO);
                    }
                }
                if (!BTSLUtil.isNullString(loyalityVO.getPayer())) {
                    if ("Y".equals(loyalityVO.getPayer())) {
                        listValueVO = new ListValueVO(PretupsI.PAYER, PretupsI.PAYER);
                        list1.add(listValueVO);
                    }
                }
                if (!BTSLUtil.isNullString(loyalityVO.getPayerHierarchy())) {
                    if ("Y".equals(loyalityVO.getPayerHierarchy())) {
                        listValueVO = new ListValueVO(PretupsI.PAYER_HIERARCHY, PretupsI.PAYER_HIERARCHY);
                        list1.add(listValueVO);
                    }
                }
                if (!BTSLUtil.isNullString(loyalityVO.getRegisteresPayer())) {
                    if ("Y".equals(loyalityVO.getRegisteresPayer())) {
                        listValueVO = new ListValueVO(PretupsI.PAYER_REGISTERER, PretupsI.PAYER_REGISTERER);
                        list1.add(listValueVO);
                    }
                }
                loyalityVO.setApplicableList(list1);
                list.add(loyalityVO);
            }

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[loadpromotionList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[loadpromotionList]", "", "", "",
                "Exception:" + ex.getMessage());
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
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	log.error("An error occurred closing statement.", e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: ");
            }
        }

        return list;
    }

    public ArrayList loadPromotionAssociation(Connection p_con, String promotion_id) throws BTSLBaseException {
        final String methodName = "loadPromotionAssociation";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered:");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        LoyaltyVO loyaltyVO = null;
        ArrayList list = null;
        ArrayList list1 = null;
        ListValueVO listValueVO = null;
        listValueVO = new ListValueVO();
        try {
            final StringBuilder strBuff = new StringBuilder();
            list = new ArrayList();
            list1 = new ArrayList();
            strBuff
                .append("SELECT PA.PROMOTION_ASSN_ID, PA.PROMOTION_ID, PA.ASSOCIATION_FOR, PA.ASSOCIATED_CATEGORY, PA.ASSOCIATED_DOMAIN, PA.STATUS ,PD.PROMOTION_START_DATE,PD.PROMOTION_END_DATE,PD.SERVICE_TYPE ");

            strBuff.append(" from PROMOTION_ASSOCIATION PA ,PROMOTION_DETAILS PD where PA.PROMOTION_ID=? and PA.PROMOTION_ID=PD.PROMOTION_ID ");

            final String sqlSelect = strBuff.toString();

            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, promotion_id);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                list1 = new ArrayList();
                loyaltyVO = new LoyaltyVO();
                loyaltyVO.setPromoid(rs.getLong("PROMOTION_ID"));
                loyaltyVO.setPromoAssoid(rs.getLong("PROMOTION_ASSN_ID"));
                loyaltyVO.setCategory(rs.getString("ASSOCIATED_CATEGORY"));
                loyaltyVO.setAssociationfor(rs.getString("ASSOCIATION_FOR"));
                loyaltyVO.setDomain(rs.getString("ASSOCIATED_DOMAIN"));
                loyaltyVO.setFromDate(rs.getDate("PROMOTION_START_DATE"));
                loyaltyVO.setToDate(rs.getDate("PROMOTION_END_DATE"));
                list.add(loyaltyVO);
            }

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[loadPromotionAssociation]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadpromotionList", "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[loadPromotionAssociation]", "", "", "",
                "Exception:" + ex.getMessage());
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
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	log.error("An error occurred closing statement.", e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: ");
            }
        }

        return list;
    }

    public ArrayList loadpromotionSuspendList(Connection p_con, String Status) throws BTSLBaseException {
        final String methodName = "loadpromotionSuspendList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered:");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        LoyalityVO loyalityVO = null;
        ArrayList list = null;
        ArrayList list1 = null;
        try {
            final StringBuilder strBuff = new StringBuilder();
            list = new ArrayList();
            list1 = new ArrayList();
            strBuff.append("SELECT promotion_id,promotion_name,PROMOTION_START_DATE, ");
            strBuff.append("PROMOTION_END_DATE,SERVICE_TYPE,STATUS, ");
            strBuff.append("PAYEE_ALLOWED,PAYEE_HIERARCHY_ALLOWED,PAYEE_REGISTERER_ALLOWED,PAYER_ALLOWED,PAYER_HIERARCHY_ALLOWED,PAYER_REGISTERER_ALLOWED");
            strBuff.append(" from promotion_details where status=?");

            final String sqlSelect = strBuff.toString();

            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }

            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, Status);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                loyalityVO = new LoyalityVO();
                loyalityVO.setPromotionID(rs.getString("promotion_id"));
                loyalityVO.setPromotionName(rs.getString("promotion_name"));
                loyalityVO.setServiceType(rs.getString("SERVICE_TYPE"));
                loyalityVO.setApplicableFromDate(rs.getTimestamp("PROMOTION_START_DATE"));
                loyalityVO.setApplicableToDate(rs.getTimestamp("PROMOTION_END_DATE"));
                loyalityVO.setPayee(rs.getString("PAYEE_ALLOWED"));
                loyalityVO.setPayeeHierarchy(rs.getString("PAYEE_HIERARCHY_ALLOWED"));
                loyalityVO.setPayer(rs.getString("PAYER_ALLOWED"));
                loyalityVO.setPayerHierarchy(rs.getString("PAYER_HIERARCHY_ALLOWED"));
                loyalityVO.setRegisteresPayee(rs.getString("PAYEE_REGISTERER_ALLOWED"));
                loyalityVO.setRegisteresPayer(rs.getString("PAYER_REGISTERER_ALLOWED"));
                if (!BTSLUtil.isNullString(loyalityVO.getPayee())) {
                    if ("Y".equals(loyalityVO.getPayee())) {
                        list1.add("payee");
                    }
                }
                if (!BTSLUtil.isNullString(loyalityVO.getPayeeHierarchy())) {
                    if ("Y".equals(loyalityVO.getPayeeHierarchy())) {
                        list1.add("payeehierarchy");
                    }
                }
                if (!BTSLUtil.isNullString(loyalityVO.getRegisteresPayee())) {
                    if ("Y".equals(loyalityVO.getRegisteresPayee())) {
                        list1.add("payeeregisterer");
                    }
                }
                if (!BTSLUtil.isNullString(loyalityVO.getPayer())) {
                    if ("Y".equals(loyalityVO.getPayer())) {
                        list1.add("payer");
                    }
                }
                if (!BTSLUtil.isNullString(loyalityVO.getPayerHierarchy())) {
                    if ("Y".equals(loyalityVO.getPayerHierarchy())) {
                        list1.add("payerhierarchy");
                    }
                }
                if (!BTSLUtil.isNullString(loyalityVO.getRegisteresPayer())) {
                    if ("Y".equals(loyalityVO.getRegisteresPayer())) {
                        list1.add("payerregisterer");
                    }
                }
                loyalityVO.setApplicableList(list1);
                list.add(loyalityVO);
            }

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[loadpromotionSuspendList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadpromotionList", "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[loadpromotionSuspendList]", "", "", "",
                "Exception:" + ex.getMessage());
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
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	log.error("An error occurred closing statement.", e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: ");
            }
        }

        return list;
    }

    public ArrayList loadAppPromotionList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadAppPromotionList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered:");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        LoyalityVO loyalityVO = null;
        ArrayList list = null;
        ArrayList list1 = null;
        try {
            final StringBuilder strBuff = new StringBuilder();
            list = new ArrayList();
            list1 = new ArrayList();
            strBuff.append("SELECT promotion_id,promotion_name,PROMOTION_START_DATE, ");
            strBuff.append("PROMOTION_END_DATE,SERVICE_TYPE,STATUS, ");
            strBuff.append("PAYEE_ALLOWED,PAYEE_HIERARCHY_ALLOWED,PAYEE_REGISTERER_ALLOWED,PAYER_ALLOWED,PAYER_HIERARCHY_ALLOWED,PAYER_REGISTERER_ALLOWED");
            strBuff.append(" from promotion_details where status=?");

            final String sqlSelect = strBuff.toString();

            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.NEW);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                loyalityVO = new LoyalityVO();
                loyalityVO.setPromotionID(rs.getString("promotion_id"));
                loyalityVO.setPromotionName(rs.getString("promotion_name"));
                loyalityVO.setServiceType(rs.getString("SERVICE_TYPE"));
                loyalityVO.setApplicableFromDate(rs.getTimestamp("PROMOTION_START_DATE"));
                loyalityVO.setApplicableToDate(rs.getTimestamp("PROMOTION_END_DATE"));
                loyalityVO.setPayee(rs.getString("PAYEE_ALLOWED"));
                loyalityVO.setPayeeHierarchy(rs.getString("PAYEE_HIERARCHY_ALLOWED"));
                loyalityVO.setPayer(rs.getString("PAYER_ALLOWED"));
                loyalityVO.setPayerHierarchy(rs.getString("PAYER_HIERARCHY_ALLOWED"));
                loyalityVO.setRegisteresPayee(rs.getString("PAYEE_REGISTERER_ALLOWED"));
                loyalityVO.setRegisteresPayer(rs.getString("PAYER_REGISTERER_ALLOWED"));
                if (!BTSLUtil.isNullString(loyalityVO.getPayee())) {
                    if ("Y".equals(loyalityVO.getPayee())) {
                        list1.add("payee");
                    }
                }
                if (!BTSLUtil.isNullString(loyalityVO.getPayeeHierarchy())) {
                    if ("Y".equals(loyalityVO.getPayeeHierarchy())) {
                        list1.add("payeehierarchy");
                    }
                }
                if (!BTSLUtil.isNullString(loyalityVO.getRegisteresPayee())) {
                    if ("Y".equals(loyalityVO.getRegisteresPayee())) {
                        list1.add("payeeregisterer");
                    }
                }
                if (!BTSLUtil.isNullString(loyalityVO.getPayer())) {
                    if ("Y".equals(loyalityVO.getPayer())) {
                        list1.add("payer");
                    }
                }
                if (!BTSLUtil.isNullString(loyalityVO.getPayerHierarchy())) {
                    if ("Y".equals(loyalityVO.getPayerHierarchy())) {
                        list1.add("payerhierarchy");
                    }
                }
                if (!BTSLUtil.isNullString(loyalityVO.getRegisteresPayer())) {
                    if ("Y".equals(loyalityVO.getRegisteresPayer())) {
                        list1.add("payerregisterer");
                    }
                }
                loyalityVO.setApplicableList(list1);
                list.add(loyalityVO);
            }

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[loadAppPromotionList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadpromotionList", "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[loadAppPromotionList]", "", "", "",
                "Exception:" + ex.getMessage());
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
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	log.error("An error occurred closing statement.", e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: ");
            }
        }

        return list;
    }

    public int approvePromotion(Connection p_con, LMSForm lmsForm, String p_status) throws BTSLBaseException {
        int addCount = -1;
        final String methodName = "approvePromotion";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entering VO ");
        }
        PreparedStatement pstmtInsert = null;
        final StringBuilder insertQueryBuff = new StringBuilder("UPDATE promotion_details ");
        insertQueryBuff.append("SET status=?,MODIFIED_BY=?,MODIFIED_ON=?,APPROVED_BY=?,APPROVED_ON=?,APPROVAL_REMARKS=? ");
        insertQueryBuff.append(" where promotion_id=? ");

        final String insertQuery = insertQueryBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Insert Query= " + insertQuery);
        }
        try {
            pstmtInsert = p_con.prepareStatement(insertQuery);

            pstmtInsert.setString(1, p_status);

            pstmtInsert.setString(2, lmsForm.getModifiedBy());
            pstmtInsert.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(date));
            pstmtInsert.setString(4, lmsForm.getModifiedBy());
            pstmtInsert.setTimestamp(5, BTSLUtil.getTimestampFromUtilDate(date));
            pstmtInsert.setString(6, lmsForm.getApprovalremarks());
            pstmtInsert.setString(7, lmsForm.getSelectedPromotion());
            addCount = pstmtInsert.executeUpdate();
        }

        catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[approvePromotion]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            log.error(methodName, " Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[approvePromotion]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
        	try{
        		if (pstmtInsert!= null){
        			pstmtInsert.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting addCount " + addCount);
            }
        }

        return addCount;
    }

    public int modifySuspendPromotion(Connection p_con, LMSForm lmsForm, String p_status) throws BTSLBaseException {
        int addCount = -1;
        final String methodName = "modifySuspendPromotion";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entering VO ");
        }
        PreparedStatement pstmtInsert = null;
        final StringBuilder insertQueryBuff = new StringBuilder("UPDATE promotion_details ");
        insertQueryBuff.append("SET status=?,MODIFIED_BY=?,MODIFIED_ON=? ");
        insertQueryBuff.append(" where promotion_id=? ");

        final String insertQuery = insertQueryBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Insert Query= " + insertQuery);
        }
        try {
            pstmtInsert =p_con.prepareStatement(insertQuery);

            pstmtInsert.setString(1, p_status);

            pstmtInsert.setString(2, lmsForm.getModifiedBy());
            pstmtInsert.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(date));

            pstmtInsert.setString(4, lmsForm.getPromotionID());
            addCount = pstmtInsert.executeUpdate();
        }

        catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[modifySuspendPromotion]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "approvePromotion", "error.general.sql.processing");
        }

        catch (Exception e) {
            log.error(methodName, " Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[modifySuspendPromotion]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "approvePromotion", "error.general.processing");
        }

        finally {
        	try{
        		if (pstmtInsert!= null){
        			pstmtInsert.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting addCount " + addCount);
            }
        }

        return addCount;
    }

    public int addLoyalityItem(Connection p_con, LMSForm lmsForm) throws BTSLBaseException {
        int addCount = -1;
        final String methodName = "addLoyalityItem";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entering VO ");
        }

        PreparedStatement pstmtInsert = null;
        final StringBuilder insertQueryBuff = new StringBuilder("INSERT INTO LOYALITY_ITEMS ");
        insertQueryBuff.append("(ITEM_CODE,ITEM_NAME,POINTS,");
        insertQueryBuff.append("STOCK_AVAILABLE,STATUS,MULTIPLICATION_FACTOR ");
        insertQueryBuff.append(") ");
        insertQueryBuff.append(" VALUES(?, ?, ? ,? ,? ,? )");
        final String insertQuery = insertQueryBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Insert Query= " + insertQuery);
        }

        try {
            pstmtInsert =p_con.prepareStatement(insertQuery);

            pstmtInsert.setString(1, lmsForm.getItemCode());
            pstmtInsert.setString(2, lmsForm.getItemName());
            pstmtInsert.setLong(3, lmsForm.getItemPoints());
            pstmtInsert.setLong(4, lmsForm.getItemQuantity());
            pstmtInsert.setString(5, PretupsI.YES);
            pstmtInsert.setLong(6, Long.parseLong(Constants.getProperty("MULTIPLICATION_FACTOR")) );
            addCount = pstmtInsert.executeUpdate();
        }

        catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[addLoyalityItem]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            log.error(methodName, " Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[addLoyalityItem]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "addInterfaceDetails", "error.general.processing");
        }

        finally {
        	try{
        		if (pstmtInsert!= null){
        			pstmtInsert.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting addCount " + addCount);
            }
        }

        return addCount;
    }

    public int addpromotionAssociation(Connection p_con, LMSForm lmsForm) throws BTSLBaseException {
        int addCount = -1;
        final String methodName = "addpromotionAssociation";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entering VO ");
        }
        PreparedStatement pstmtInsert = null;
        final StringBuilder insertQueryBuff = new StringBuilder("INSERT INTO PROMOTION_ASSOCIATION ");
        insertQueryBuff.append("(PROMOTION_ID,PROMOTION_ASSN_ID,ASSOCIATION_FOR,");
        insertQueryBuff.append("ASSOCIATED_CATEGORY,ASSOCIATED_DOMAIN,STATUS, ");
        insertQueryBuff.append("CREATED_BY,CREATED_ON,MODIFIED_BY,");
        insertQueryBuff.append("MODIFIED_ON) ");
        insertQueryBuff.append(" VALUES(?, ?, ? ,? ,? ,? ,? ,? ,? ,?)");
        final String insertQuery = insertQueryBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Insert Query= " + insertQuery);
        }
        try {
            pstmtInsert = p_con.prepareStatement(insertQuery);

            pstmtInsert.setString(1, lmsForm.getPromotionID());
            pstmtInsert.setString(2, lmsForm.getPromotionAsscId());
            pstmtInsert.setString(3, lmsForm.getApplicableTo());
            pstmtInsert.setString(4, lmsForm.getCategoryCode());
            pstmtInsert.setString(5, lmsForm.getDomainCode());
            pstmtInsert.setString(6, PretupsI.YES);

            pstmtInsert.setString(7, lmsForm.getModifiedBy());
            pstmtInsert.setTimestamp(8, BTSLUtil.getTimestampFromUtilDate(date));
            pstmtInsert.setString(9, lmsForm.getModifiedBy());
            pstmtInsert.setTimestamp(10, BTSLUtil.getTimestampFromUtilDate(date));

            addCount = pstmtInsert.executeUpdate();
        }

        catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[addpromotionAssociation]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "addInterfaceDetails", "error.general.sql.processing");
        }

        catch (Exception e) {
            log.error(methodName, " Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[addpromotionAssociation]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
        	try{
        		if (pstmtInsert!= null){
        			pstmtInsert.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting addCount " + addCount);
            }
        }

        return addCount;
    }

    public ArrayList loadModifyDeleteItemList(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadpromotionList";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered:");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        LoyalityVO loyalityVO = null;
        ArrayList list = null;
        ArrayList list1 = null;
        ListValueVO listValueVO = null;
        listValueVO = new ListValueVO();
        try {
            StringBuilder strBuff = new StringBuilder();
            list = new ArrayList();
            list1 = new ArrayList();
            strBuff = new StringBuilder("select  ");
            strBuff.append(" ITEM_CODE,ITEM_NAME,POINTS,");
            strBuff.append("STOCK_AVAILABLE from LOYALITY_ITEMS where status not in(?)");

            final String sqlSelect = strBuff.toString();

            if (log.isDebugEnabled()) {
                log.debug("loadModifyDeleteItemList", "QUERY sqlSelect=" + sqlSelect);
            }
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.NO);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                list1 = new ArrayList();
                loyalityVO = new LoyalityVO();
                loyalityVO.setItemCode(rs.getString("ITEM_CODE"));
                loyalityVO.setItemName(rs.getString("ITEM_NAME"));
                loyalityVO.setItemPointsAsString(rs.getString("POINTS"));
                loyalityVO.setItemQuantityAsString(rs.getString("STOCK_AVAILABLE"));
                loyalityVO.setItemPoints(rs.getLong("POINTS"));
                loyalityVO.setItemQuantity(rs.getLong("STOCK_AVAILABLE"));

                list.add(loyalityVO);
            }

        } catch (SQLException sqe) {
            log.error("loadModifyDeleteItemList", "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[loadModifyDeleteItemList]", "", "", "",
                "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error("loadModifyDeleteItemList", "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[loadModifyDeleteItemList]", "", "", "",
                "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadModifyDeleteItemList", "error.general.processing");
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
            	log.error("An error occurred closing statement.", e);
            }
            if (log.isDebugEnabled()) {
                log.debug("loadModifyDeleteItemList", "Exiting: ");
            }
        }

        return list;
    }

    public int deleteItem(Connection p_con, LMSForm lmsForm, String p_status) throws BTSLBaseException {
        int addCount = -1;
        final String methodName = "deleteItem";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entering VO ");
        }
        PreparedStatement pstmtInsert = null;
        final StringBuilder insertQueryBuff = new StringBuilder("UPDATE  LOYALITY_ITEMS");
        insertQueryBuff.append(" SET status=? ");
        insertQueryBuff.append(" where item_code=? ");

        final String insertQuery = insertQueryBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Insert Query= " + insertQuery);
        }
        try {
            pstmtInsert =p_con.prepareStatement(insertQuery);

            pstmtInsert.setString(1, p_status);

            pstmtInsert.setString(2, lmsForm.getSelectedPromotion());

            addCount = pstmtInsert.executeUpdate();
        }

        catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[deleteItem]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, "approvePromotion", "error.general.sql.processing");
        }

        catch (Exception e) {
            log.error(methodName, " Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[deleteItem]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, "approvePromotion", "error.general.processing");
        }

        finally {
        	try{
        		if (pstmtInsert!= null){
        			pstmtInsert.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting addCount " + addCount);
            }
        }

        return addCount;
    }

    public int saveModifyItem(Connection p_con, LMSForm lmsForm) throws BTSLBaseException {
        int addCount = -1;
        final String methodName = "saveModifyItem";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entering VO ");
        }
        PreparedStatement pstmtInsert = null;
        final StringBuilder insertQueryBuff = new StringBuilder("update LOYALITY_ITEMS set ");
        insertQueryBuff.append(" ITEM_CODE=?,ITEM_NAME=?,POINTS=?,");
        insertQueryBuff.append("STOCK_AVAILABLE=? where item_code=? ");

        final String insertQuery = insertQueryBuff.toString();
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Insert Query= " + insertQuery);
        }
        try {
            pstmtInsert =p_con.prepareStatement(insertQuery);

            pstmtInsert.setString(1, lmsForm.getItemCode());
            pstmtInsert.setString(2, lmsForm.getItemName());
            pstmtInsert.setLong(3, lmsForm.getItemPoints());
            pstmtInsert.setLong(4, lmsForm.getItemQuantity());
            pstmtInsert.setString(5, lmsForm.getItemCode());
            addCount = pstmtInsert.executeUpdate();
        }

        catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[saveModifyItem]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        }

        catch (Exception e) {
            log.error(methodName, " Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[saveModifyItem]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        }

        finally {
        	try{
        		if (pstmtInsert!= null){
        			pstmtInsert.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting addCount " + addCount);
            }
        }

        return addCount;
    }

    public ArrayList loadRewardAssociationDetails(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadRewardAssociationDetails";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered:");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        LoyaltyVO loyaltyVO = null;
        ArrayList list = null;
        ArrayList list1 = null;
        ListValueVO listValueVO = null;
        listValueVO = new ListValueVO();
        try {
            final StringBuilder strBuff = new StringBuilder();
            list = new ArrayList();
            list1 = new ArrayList();
            strBuff.append("select  rg.REWARD_RANGE_ID, rg.FROM_AMOUNT,rg.TO_AMOUNT ,pa.PROMOTION_ID,pd.PROMOTION_START_DATE,pd.PROMOTION_END_DATE,pd.SERVICE_TYPE ,");
            strBuff.append("pa.PROMOTION_ASSN_ID,pa.ASSOCIATION_FOR,pa.ASSOCIATED_DOMAIN,pa.ASSOCIATED_CATEGORY ");

            strBuff.append("From REWARD_RANGE rg inner join REWARD_DEFINITION rd");
            strBuff.append(" on rg.REWARD_ID=rd.REWARD_ID inner join promotion_details PD on rd.promotion_id=pd.PROMOTION_ID ");
            strBuff.append(" inner join promotion_ASSOCIATION pa ON pa.promotion_id=pd.promotion_id where pd.STATUS=?");

            final String sqlSelect = strBuff.toString();

            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.YES);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                list1 = new ArrayList();
                loyaltyVO = new LoyaltyVO();
                loyaltyVO.setPromoid(rs.getLong("PROMOTION_ID"));
                loyaltyVO.setPromoAssoid(rs.getLong("PROMOTION_ASSN_ID"));
                loyaltyVO.setCategory(rs.getString("ASSOCIATED_CATEGORY"));
                loyaltyVO.setAssociationfor(rs.getString("ASSOCIATION_FOR"));
                loyaltyVO.setDomain(rs.getString("ASSOCIATED_DOMAIN"));
                loyaltyVO.setFromamt(rs.getLong("FROM_AMOUNT"));
                loyaltyVO.setToamt(rs.getLong("TO_AMOUNT"));
                loyaltyVO.setFromDate(rs.getDate("PROMOTION_START_DATE"));
                loyaltyVO.setToDate(rs.getDate("PROMOTION_END_DATE"));
                loyaltyVO.setServiceType(rs.getString("SERVICE_TYPE"));

                list.add(loyaltyVO);
            }

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[loadRewardAssociationDetails]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, "loadpromotionList", "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[loadRewardAssociationDetails]", "", "",
                "", "Exception:" + ex.getMessage());
            throw new BTSLBaseException(this, "loadpromotionList", "error.general.processing");
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
            	log.error("An error occurred closing statement.", e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: ");
            }
        }

        return list;
    }

    public ArrayList loadPromotionListForReward(Connection p_con) throws BTSLBaseException {
        final String methodName = "loadPromotionListForReward";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered:");
        }
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        LoyalityVO loyalityVO = null;
        ArrayList list = null;
        ArrayList list1 = null;
        ListValueVO listValueVO = null;
        listValueVO = new ListValueVO();
        try {
            final StringBuilder strBuff = new StringBuilder();
            list = new ArrayList();
            list1 = new ArrayList();
            strBuff.append("SELECT promotion_id,promotion_name,PROMOTION_START_DATE, ");
            strBuff.append("PROMOTION_END_DATE,SERVICE_TYPE,STATUS, ");
            strBuff.append("PAYEE_ALLOWED,PAYEE_HIERARCHY_ALLOWED,PAYEE_REGISTERER_ALLOWED,PAYER_ALLOWED,PAYER_HIERARCHY_ALLOWED,PAYER_REGISTERER_ALLOWED");
            strBuff.append(" from promotion_details where status not in (?) and promotion_id not in(select promotion_id from reward_definition)");

            final String sqlSelect = strBuff.toString();

            if (log.isDebugEnabled()) {
                log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
            }
            pstmt = p_con.prepareStatement(sqlSelect);
            pstmt.setString(1, PretupsI.NO);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                list1 = new ArrayList();
                loyalityVO = new LoyalityVO();
                loyalityVO.setPromotionID(rs.getString("promotion_id"));
                loyalityVO.setPromotionName(rs.getString("promotion_name"));
                loyalityVO.setServiceType(rs.getString("SERVICE_TYPE"));
                loyalityVO.setApplicableFromDate(rs.getTimestamp("PROMOTION_START_DATE"));
                loyalityVO.setApplicableToDate(rs.getTimestamp("PROMOTION_END_DATE"));
                loyalityVO.setPayee(rs.getString("PAYEE_ALLOWED"));
                loyalityVO.setPayeeHierarchy(rs.getString("PAYEE_HIERARCHY_ALLOWED"));
                loyalityVO.setPayer(rs.getString("PAYER_ALLOWED"));
                loyalityVO.setPayerHierarchy(rs.getString("PAYER_HIERARCHY_ALLOWED"));
                loyalityVO.setRegisteresPayee(rs.getString("PAYEE_REGISTERER_ALLOWED"));
                loyalityVO.setRegisteresPayer(rs.getString("PAYER_REGISTERER_ALLOWED"));
                loyalityVO.setStatus(rs.getString("STATUS"));
                if (!BTSLUtil.isNullString(loyalityVO.getPayee())) {
                    if ("Y".equals(loyalityVO.getPayee())) {
                        listValueVO = new ListValueVO(PretupsI.PAYEE, PretupsI.PAYEE);
                        list1.add(listValueVO);
                    }
                }
                if (!BTSLUtil.isNullString(loyalityVO.getPayeeHierarchy())) {
                    if ("Y".equals(loyalityVO.getPayeeHierarchy())) {
                        listValueVO = new ListValueVO(PretupsI.PAYEE_HIERARCHY, PretupsI.PAYEE_HIERARCHY);
                        list1.add(listValueVO);
                    }
                }
                if (!BTSLUtil.isNullString(loyalityVO.getRegisteresPayee())) {
                    if ("Y".equals(loyalityVO.getRegisteresPayee())) {
                        listValueVO = new ListValueVO(PretupsI.PAYEE_REGISTERER, PretupsI.PAYEE_REGISTERER);
                        list1.add(listValueVO);
                    }
                }
                if (!BTSLUtil.isNullString(loyalityVO.getPayer())) {
                    if ("Y".equals(loyalityVO.getPayer())) {
                        listValueVO = new ListValueVO(PretupsI.PAYER, PretupsI.PAYER);
                        list1.add(listValueVO);
                    }
                }
                if (!BTSLUtil.isNullString(loyalityVO.getPayerHierarchy())) {
                    if ("Y".equals(loyalityVO.getPayerHierarchy())) {
                        listValueVO = new ListValueVO(PretupsI.PAYER_HIERARCHY, PretupsI.PAYER_HIERARCHY);
                        list1.add(listValueVO);
                    }
                }
                if (!BTSLUtil.isNullString(loyalityVO.getRegisteresPayer())) {
                    if ("Y".equals(loyalityVO.getRegisteresPayer())) {
                        listValueVO = new ListValueVO(PretupsI.PAYER_REGISTERER, PretupsI.PAYER_REGISTERER);
                        list1.add(listValueVO);
                    }
                }
                loyalityVO.setApplicableList(list1);
                list.add(loyalityVO);
            }

        } catch (SQLException sqe) {
            log.error(methodName, "SQLException : " + sqe);
            log.errorTrace(methodName, sqe);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[loadPromotionListForReward]", "", "",
                "", "SQL Exception:" + sqe.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception ex) {
            log.error(methodName, "Exception : " + ex);
            log.errorTrace(methodName, ex);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[loadPromotionListForReward]", "", "",
                "", "Exception:" + ex.getMessage());
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
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	log.error("An error occurred closing statement.", e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting: ");
            }
        }

        return list;
    }

    public int addLoyalityMessage(Connection p_con, LMSForm lmsForm) throws BTSLBaseException {
        final String methodName = "addLoyalityMessage";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered.. ");
        }
        int addCount = -1;
        StringBuilder scheduleString = new StringBuilder();
        PreparedStatement pstmtInsert = null;
        final String insertQuery = "UPDATE promotion_details SET MESSAGE1=? ,MESSAGE2=? ,MESSAGE_SCHEDULE=? where PROMOTION_ID=? ";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Insert Query= " + insertQuery);
        }
        try {
            if (lmsForm.getDaily() != null) {
                scheduleString = scheduleString.append(lmsForm.getDaily());
                if (lmsForm.getWeekly() != null) {
                    scheduleString.append("_");
                    scheduleString = scheduleString.append(lmsForm.getWeekly());
                    if (lmsForm.getMonthly() != null) {
                        scheduleString.append("_");
                        scheduleString = scheduleString.append(lmsForm.getMonthly());
                    }
                } else {
                    if (lmsForm.getMonthly() != null) {
                        scheduleString.append("_");
                        scheduleString = scheduleString.append(lmsForm.getMonthly());
                    }
                }
            } else {
                if (lmsForm.getWeekly() != null) {
                    scheduleString.append("_");
                    scheduleString = scheduleString.append(lmsForm.getWeekly());
                    if (lmsForm.getMonthly() != null) {
                        scheduleString.append("_");
                        scheduleString = scheduleString.append(lmsForm.getMonthly());
                    }
                } else {
                    if (lmsForm.getMonthly() != null) {
                        scheduleString.append("_");
                        scheduleString = scheduleString.append(lmsForm.getMonthly());
                    }
                }

            }

            pstmtInsert = p_con.prepareStatement(insertQuery);
            pstmtInsert.setString(1, BTSLUtil.NullToString(lmsForm.getRemarks1()));
            pstmtInsert.setString(2, BTSLUtil.NullToString(lmsForm.getRemarks2()));
            pstmtInsert.setString(3, BTSLUtil.NullToString(scheduleString.toString()));
            pstmtInsert.setInt(4, Integer.parseInt(lmsForm.getSelectedPromotion()));
            addCount = pstmtInsert.executeUpdate();
        } catch (SQLException sqle) {
            log.error(methodName, "SQLException " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[addLoyalityMessage]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } catch (Exception e) {
            log.error(methodName, " Exception " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LoyalityWebDAO[addLoyalityMessage]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	try{
        		if (pstmtInsert!= null){
        			pstmtInsert.close();
        		}
        	}
        	catch (SQLException e){
        		log.error("An error occurred closing result set.", e);
        	}
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exiting addCount " + addCount);
            }
        }

        return addCount;
    }

    public boolean isItemCodeExists(Connection p_con, String itemCode) throws BTSLBaseException {
        final String methodName = "isItemCodeExists";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered p_setID= " + itemCode);
        }
        boolean isexists = false;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            final StringBuilder sbf = new StringBuilder();
            sbf.append(" select item_name from LOYALITY_ITEMS where item_code=? ");

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Sql query = " + sbf.toString());
            }
            final String selectQuery = sbf.toString();
            pstmt = p_con.prepareStatement(selectQuery);
            pstmt.setString(1, itemCode);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                isexists = true;
            }
        } catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[isprofileAssociated]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[isprofileSingleVersionExist]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
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
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	log.error("An error occurred closing statement.", e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting isexists=" + isexists);
            }
        }
        return isexists;
    }

    public boolean isItemNameExists(Connection p_con, String itemname) throws BTSLBaseException {
        final String methodName = "isItemNameExists";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered item name= " + itemname);
        }
        boolean isexists = false;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            final StringBuilder sbf = new StringBuilder();
            sbf.append(" select item_name from LOYALITY_ITEMS where item_name=? and status='Y' ");

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Sql query = " + sbf.toString());
            }
            final String selectQuery = sbf.toString();
            pstmt = p_con.prepareStatement(selectQuery);
            pstmt.setString(1, itemname);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                isexists = true;
            }
        } catch (SQLException sqle) {
            log.error(methodName, "SQLException: " + sqle.getMessage());
            log.errorTrace(methodName, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[isprofileAssociated]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
        } // end of catch
        catch (Exception e) {
            log.error(methodName, "Exception: " + e.getMessage());
            log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[isprofileSingleVersionExist]", "",
                "", "", "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } // end of catch
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
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	log.error("An error occurred closing statement.", e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting isexists=" + isexists);
            }
        }
        return isexists;
    }
    
    /**
	 * @author diwakar
	 * @param p_con
	 * @param itemName
	 * @param itemCode
	 * @return
	 * @throws BTSLBaseException
	 */
	public boolean isItemNameExists(Connection p_con,String itemName,String itemCode) throws BTSLBaseException {
		final String methodName = "isItemNameExists";
		if(log.isDebugEnabled()) {
			log.debug(methodName ,"Entered item name="+itemName+",itemCode="+itemCode);
		}
		boolean isexists=false;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		try {
			StringBuilder sbf=new StringBuilder();
			sbf.append(" select item_name,item_code from LOYALITY_ITEMS where item_name=? and item_code not in (?) and status= ? ");
			
			if(log.isDebugEnabled()) {
				log.debug(methodName ,"Sql query = "+sbf.toString());
			}
			String selectQuery=sbf.toString();
			pstmt=p_con.prepareStatement(selectQuery);
			int index=0;
			pstmt.setString(++index,itemName);
			pstmt.setString(++index,itemCode);
			pstmt.setString(++index,PretupsI.YES);
			rs=pstmt.executeQuery();
			if(rs.next()) {
				isexists=true;
			}
		} catch (SQLException sqle) {
			log.error(methodName, "SQLException: " + sqle.getMessage());
			log.errorTrace(methodName,sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"LoyalityDAO["+methodName+"]","","","","SQL Exception:"+sqle.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
		} catch (Exception e) {
			log.error(methodName, "Exception: " + e.getMessage());
			log.errorTrace(methodName,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"LoyalityDAO["+methodName+"]","","","","Exception:"+e.getMessage());
			throw new BTSLBaseException(this, methodName, "error.general.processing");
		}  finally {
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
            	log.error("An error occurred closing statement.", e);
            }
			if(log.isDebugEnabled()){
				log.debug(methodName ,"Exiting isexists="+isexists);
			}
		}
		return isexists;
	}
	public ArrayList<LoyalityVO> itemList(Connection p_con, String itemname, String itemCode) throws BTSLBaseException {
	    final String methodName = "itemList";
	    if (log.isDebugEnabled()) {
	        log.debug(methodName, "Entered item name= " + itemname);
	    }
	    ArrayList<LoyalityVO> list = new ArrayList();
	    LoyalityVO loyalityVO = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    try {
	        final StringBuilder sbf = new StringBuilder();
	        sbf.append(" select item_name from LOYALITY_ITEMS where item_name=? and status='Y' and item_code!=? ");

	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Sql query = " + sbf.toString());
	        }
	        final String selectQuery = sbf.toString();
	        pstmt = p_con.prepareStatement(selectQuery);
	        pstmt.setString(1, itemname);
	        pstmt.setString(2, itemCode);
	        rs = pstmt.executeQuery();
	        while (rs.next()) {
	        	loyalityVO= new LoyalityVO();
	        	loyalityVO.setItemName(rs.getString("item_name"));
	        	list.add(loyalityVO);
	        
	    } 
	    }
	    catch (SQLException sqle) {
	        log.error(methodName, "SQLException: " + sqle.getMessage());
	        log.errorTrace(methodName, sqle);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[isprofileAssociated]", "", "", "",
	            "SQL Exception:" + sqle.getMessage());
	        throw new BTSLBaseException(this, methodName, "error.general.sql.processing");
	    } // end of catch
	    catch (Exception e) {
	        log.error(methodName, "Exception: " + e.getMessage());
	        log.errorTrace(methodName, e);
	        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ActivationBonusDAO[isprofileSingleVersionExist]", "",
	            "", "", "Exception:" + e.getMessage());
	        throw new BTSLBaseException(this, methodName, "error.general.processing");
	    } // end of catch
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
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	log.error("An error occurred closing statement.", e);
            }
	        if (log.isDebugEnabled()) {
	            log.debug(methodName, "Exiting list=" + list);
	        }
	    }
	    return list;
	}
}

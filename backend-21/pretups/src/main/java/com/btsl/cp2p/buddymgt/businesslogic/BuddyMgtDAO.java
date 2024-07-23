package com.btsl.cp2p.buddymgt.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.btsl.common.BTSLBaseException;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.p2p.subscriber.businesslogic.BuddyVO;


public class BuddyMgtDAO {
    private Log log = LogFactory.getLog(this.getClass().getName());

    /**
     * This method loads the Card group details like the access fee, tax etc for
     * the slab in which the requested value lies
     * 
     * @param p_con
     * @param p_subscriberID
     * @return ArrayList
     * @throws BTSLBaseException
     * @throws SQLException
     * @throws Exception
     */
    public ArrayList loadBuddyList(Connection p_con, String p_subscriberID) throws BTSLBaseException, SQLException {
        if (log.isDebugEnabled()) {	
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append(" Entered ");
        	loggerValue.append(" ");
        	loggerValue.append("p_subscriberID= ");
        	loggerValue.append(p_subscriberID);
            log.debug("loadBuddyList", loggerValue);
        }
        final String METHOD_NAME = "loadBuddyList";
        
        BuddyVO buddyVO = null;
        ArrayList buddyVOList = null;
        try {
            buddyVOList = new ArrayList();
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("SELECT ppb.buddy_name,ppb.buddy_msisdn,ppb.preferred_amount,ppb.buddy_total_transfer,ppb.buddy_total_transfer_amt ");
            strBuff.append("FROM p2p_buddies ppb ");
            strBuff.append("WHERE ppb.parent_id=? ORDER BY ppb.buddy_name");
            String selQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug("loadBuddyList", " Select Query " + selQuery);
            }
            try(PreparedStatement pstmt = p_con.prepareStatement(selQuery);)
            {
            pstmt.setString(1, p_subscriberID);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                buddyVO = new BuddyVO();
                buddyVO.setName(rs.getString("buddy_name"));
                buddyVO.setBuddyMsisdn(rs.getString("buddy_msisdn"));
                buddyVO.setPreferredAmount(BuddyMgtBL.getDisplayAmount(rs.getLong("preferred_amount")));
                buddyVO.setBuddyTotalTransfers(rs.getLong("buddy_total_transfer"));
                buddyVO.setBuddyTotalTransferAmount(BuddyMgtBL.getDisplayAmount(rs.getLong("buddy_total_transfer_amt")));
                buddyVOList.add(buddyVO);
            }
        }
            }
        }catch (SQLException sqle) {
            log.errorTrace(METHOD_NAME, sqle);
        } finally {
            log.debug(METHOD_NAME,"exiting..");
        }
        if (log.isDebugEnabled()) {
            log.debug("loadBuddyList", " Exiting " + buddyVOList.size());
        }
        return buddyVOList;
    }

    /**
     * This method loads the Card group details like the access fee, tax etc for
     * the slab in which the requested value lies
     * 
     * @param p_con
     * @param p_subscriberID
     * @return ArrayList
     * @throws BTSLBaseException
     * @throws SQLException
     * @throws Exception
     */
    public BuddyVO loadBuddyDetails(Connection p_con, String p_subscriberID, String p_buddyName, String p_buddyMsisdn) throws BTSLBaseException, SQLException {
        if (log.isDebugEnabled()) {
        	StringBuilder loggerValue= new StringBuilder();
        	loggerValue.setLength(0);
        	loggerValue.append(" Entered ");
        	loggerValue.append(" ");
        	loggerValue.append("p_subscriberID= ");
        	loggerValue.append(p_subscriberID);
            log.debug("loadBuddyDetails", loggerValue);
        }
        final String METHOD_NAME = "loadBuddyDetails";
       
        BuddyVO buddyVO = null;
        try {
            StringBuffer strBuff = new StringBuffer();
            strBuff.append("SELECT pb.buddy_msisdn, pb.parent_id, pb.buddy_seq_num,pb.buddy_name,pb.status,ps.user_name, ");
            strBuff.append("pb.buddy_last_transfer_id, pb.buddy_last_transfer_on,pb.buddy_last_transfer_type, ");
            strBuff.append("pb.buddy_total_transfer, pb.buddy_total_transfer_amt, pb.created_on, pb.created_by,pb.modified_on, ");
            strBuff.append("pb.modified_by,pb.last_transfer_amount,pb.prefix_id,pb.preferred_amount ");
            strBuff.append("FROM p2p_buddies pb,p2p_subscribers ps ");
            strBuff.append("WHERE parent_id = ? AND ( upper(buddy_name) = upper(?)  OR buddy_msisdn = ? )");
            String selQuery = strBuff.toString();
            if (log.isDebugEnabled()) {
                log.debug("loadBuddyDetails", " Select Query " + selQuery);
            }
            try(PreparedStatement pstmt = p_con.prepareStatement(selQuery);)
            {
            	
            
            int i = 1;
            pstmt.setString(i, p_subscriberID.trim());
            i++;
            pstmt.setString(i, p_buddyName.trim());
            i++;
            pstmt.setString(i, p_buddyMsisdn);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                buddyVO = new BuddyVO();
                buddyVO.setName(rs.getString("buddy_name"));
                buddyVO.setOwnerUser(rs.getString("parent_id"));
                buddyVO.setBuddyMsisdn(rs.getString("buddy_msisdn"));
                buddyVO.setSeqNumber(rs.getInt("buddy_seq_num"));
                buddyVO.setStatus(rs.getString("status"));
                buddyVO.setLastTransferID(rs.getString("buddy_last_transfer_id"));
                if (rs.getTimestamp("buddy_last_transfer_on") != null)
                    buddyVO.setLastTransferOn(rs.getTimestamp("buddy_last_transfer_on"));
                buddyVO.setLastTransferType(rs.getString("buddy_last_transfer_type"));
                buddyVO.setBuddyTotalTransfers(rs.getLong("buddy_total_transfer"));
                buddyVO.setBuddyTotalTransferAmount(rs.getLong("buddy_total_transfer_amt"));
                if (rs.getTimestamp("created_on") != null)
                    buddyVO.setCreatedOn(rs.getTimestamp("created_on"));
                buddyVO.setCreatedBy(rs.getString("created_by"));
                if (rs.getTimestamp("modified_on") != null)
                    buddyVO.setModifiedOn(rs.getTimestamp("modified_on"));
                buddyVO.setModifiedBy(rs.getString("modified_by"));
                buddyVO.setLastTransferAmount(rs.getLong("last_transfer_amount"));
                buddyVO.setPrefixID(rs.getLong("prefix_id"));
                buddyVO.setPreferredAmount(BuddyMgtBL.getDisplayAmount(rs.getLong("preferred_amount")));
                buddyVO.setOwnerName(rs.getString("user_name"));
            }
        } 
        }
        }catch (SQLException sqle) {
            log.errorTrace(METHOD_NAME, sqle);
        } finally {
            
        }
        if (log.isDebugEnabled()) {
            log.debug("loadBuddyDetails", " Exiting ");
        }
        return buddyVO;
    }

    /**
     * This method loads the Card group details like the access fee, tax etc for
     * the slab in which the requested value lies
     * 
     * @param p_con
     * @param p_subscriberID
     * @return ArrayList
     * @throws BTSLBaseException
     * @throws SQLException
     * @throws Exception
     */
    public BuddyVO loadBuddyDetailsByName(Connection p_con, String p_buddyName, String p_subscriberID) throws BTSLBaseException, SQLException, Exception {
        final String METHOD_NAME = "loadBuddyDetailsByName";
        if (log.isDebugEnabled()) {
            log.debug("loadBuddyDetails", " Entered ");
        }
        BuddyVO buddVO = null;
       
        StringBuffer strBuff = new StringBuffer();
        strBuff.append("SELECT pb.buddy_msisdn,pb.status,pb.preferred_amount ");
        strBuff.append("FROM p2p_buddies pb ");
        strBuff.append("WHERE upper(buddy_name)=upper(?) and pb.parent_id=?");
        String selQuery = strBuff.toString();
        try(PreparedStatement pstmt = p_con.prepareStatement(selQuery);) {
            pstmt.setString(1, p_buddyName);
            pstmt.setString(2, p_subscriberID);
            try(ResultSet rs = pstmt.executeQuery();)
            {
            while (rs.next()) {
                buddVO = new BuddyVO();
                buddVO.setBuddyMsisdn(rs.getString("buddy_msisdn"));
                buddVO.setStatus(rs.getString("status"));
                buddVO.setPreferredAmount(BuddyMgtBL.getDisplayAmount(rs.getLong("preferred_amount")));
            }
        } 
        }catch (Exception e) {
            log.errorTrace(METHOD_NAME, e);
        } finally {
        if (log.isDebugEnabled()) {
            log.debug("loadBuddyDetails", " Exiting ");
        }
        }
        return buddVO;
    }
}

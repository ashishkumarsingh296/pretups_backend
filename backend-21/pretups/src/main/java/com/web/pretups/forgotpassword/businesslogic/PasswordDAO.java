package com.web.pretups.forgotpassword.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

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
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

@Repository
@Lazy
@Scope("session")
public class PasswordDAO {

    private static final Log LOG = LogFactory.getLog(PasswordDAO.class.getName());
    private PasswordQry passwordQry;
    public List<String> storedPassword;
    public PasswordDAO() {
        super();
        storedPassword=new ArrayList<>();
        passwordQry = (PasswordQry)ObjectProducer.getObject(QueryConstants.PASSWORD_QRY, QueryConstants.QUERY_PRODUCER);
    }

    public ArrayList<UserVO> loadUserDetails(String loginID, Connection p_con) throws BTSLBaseException {
        final String methodName = "loadUserDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered loginId:" + loginID);
        }
        UserVO userVO = new UserVO();
        ArrayList<UserVO> userList = new ArrayList<>();
        try {
            
            final String query = passwordQry.loadUserDetailsQry();
            
            try(PreparedStatement pstmtSelect = p_con.prepareStatement(query);) {
            	
            	pstmtSelect.setString(1, loginID);
            	
            	try (ResultSet rs = pstmtSelect.executeQuery();)
            	{
                    while (rs.next()) {
                    	userVO.setEmail(rs.getString("email"));
                    	userVO.setMsisdn(rs.getString("msisdn"));
                    	userVO.setNetworkID(rs.getString("network_code"));
                    	userVO.setLanguage(rs.getString("phone_language"));
                    	userVO.setCountry(rs.getString("country"));
                    	userVO.setUserName(rs.getString("user_name"));
                    	userVO.setUserID(rs.getString("user_id"));
                    	userList.add(userVO);
                    }
            	}
            }
            

        } catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PasswordDAO[loadUserDetails]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            // UserVO userVO2 = (UserVO)jdbcTemplate.queryForObject(query, new
            // Object[]{loginID},new BeanPropertyRowMapper(UserVO.class));
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exited");
            }
        }
        return userList;
    }

    public boolean checkPasswordHistory(String newPassword, String userId, Connection p_con) throws BTSLBaseException {

        final String methodName = "checkPasswordHistory";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered userId:" + userId);
        }
        boolean isExist = false;
        String password = null;
        PreparedStatement pstmtSelect = null;
        ResultSet rs = null;
        try {
           
            final String sqlSelect = passwordQry.checkPasswordHistoryQry();
             pstmtSelect = p_con.prepareStatement(sqlSelect);
            	pstmtSelect.setString(1, userId);
            	pstmtSelect.setLong(2,((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PIN_NOT_ALLOW))).intValue());
            	rs = pstmtSelect.executeQuery();
            	while(rs.next())
            	{
            		password = rs.getString("pin_or_password");
            		storedPassword=new ArrayList<String>();
            		
            		storedPassword.add(password);
            		if (password.equals(BTSLUtil.encryptText(newPassword))) {
                        isExist = true;
                        break;
                    }
            	}
        } catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PasswordDAO[checkPasswordHistory]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
        	 try {
                 if (rs != null) {
                     rs.close();
                 }
             } catch (Exception ex) {
                 LOG.errorTrace(methodName, ex);
             }
        	try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exited passwordExists: " + isExist);
            }
            }
           

        return isExist;
    }

    public int updatePassword(String newPassword, String userId, Connection p_con) throws BTSLBaseException {
        final String methodName = "updatePassword";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered userId:" + userId);
        }
        int count = 0;
        PreparedStatement pstmtSelect = null;
        try {
            final Date currentDate = new Date();
            final StringBuffer strBuff = new StringBuffer();
            strBuff.append("UPDATE users SET pswd_modified_on=?,password=?,modified_on= ?,");
            strBuff.append(" modified_by= ?, PSWD_RESET=? WHERE user_id = ? ");
            final String queryUpdate = strBuff.toString();
            pstmtSelect = p_con.prepareStatement(queryUpdate);
        	pstmtSelect.setTimestamp(1, BTSLUtil.getTimestampFromUtilDate(currentDate));
        	pstmtSelect.setString(2,BTSLUtil.encryptText(newPassword));
        	pstmtSelect.setTimestamp(3, BTSLUtil.getTimestampFromUtilDate(currentDate));
        	pstmtSelect.setString(4, userId);
        	pstmtSelect.setString(5,"N");
        	pstmtSelect.setString(6,userId);
        	count = pstmtSelect.executeUpdate();
        	
        } catch (Exception e) {
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "PasswordDAO[updatePassword]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException(this, methodName, "error.general.processing");
        } finally {
            try {
                if (pstmtSelect != null) {
                    pstmtSelect.close();
                }
            } catch (Exception ex) {
                LOG.errorTrace(methodName, ex);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exited updatecount: " + count);
            }
        }
        
        return count;
    }
}

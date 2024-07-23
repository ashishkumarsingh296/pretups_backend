package com.btsl.pretups.channel.transfer.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.beanutils.BeanUtils;

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
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.AutoC2CTransfer;
import com.btsl.pretups.processes.businesslogic.LowBalanceAlertVO;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.user.businesslogic.UserPhonesDAO;
import com.btsl.util.OracleUtil;

public class AutoC2CBL {
    private static Log _log = LogFactory.getLog(AutoC2CBL.class.getName());

    public void realTimeAutoC2C(LowBalanceAlertVO vo) throws BTSLBaseException {
        final String METHOD_NAME = "realTimeAutoC2C";
        if (_log.isDebugEnabled()) {
            _log.info(METHOD_NAME, " Enter LowBalanceAlertVO: " + vo);
        }
        
        try {
            final ArrayList<LowBalanceAlertVO> list = new ArrayList<LowBalanceAlertVO>();
            final LowBalanceAlertVO vo1 = new LowBalanceAlertVO();
            BeanUtils.copyProperties(vo1, vo);
            list.add(vo1);
            final AutoC2CThread mrt = new AutoC2CThread(vo1);
            final Thread t = new Thread(mrt);
            t.start();
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
        } finally {
            if (_log.isDebugEnabled()) {
                _log.info(METHOD_NAME, " End of Main Thread... ");
            }
        }
    }
}

class AutoC2CThread implements Runnable {
    private static Log _log = LogFactory.getLog(AutoC2CBL.class.getName());
    private LowBalanceAlertVO vo = null;

    public AutoC2CThread(LowBalanceAlertVO vo1) {
        this.vo = vo1;
    }

    public void run(){
        final String METHOD_NAME = "run";
        LogFactory.printLog(METHOD_NAME, "Enter vo: " + vo, _log);
        Connection con = null;MComConnectionI mcomCon = null;
        boolean check = false;
        UserPhonesDAO userPhonesDao = new UserPhonesDAO();
        try {
        	Thread.sleep(1000);
        	mcomCon = new MComConnection();con=mcomCon.getConnection();
            ArrayList<LowBalanceAlertVO> list = loadMinBalanceUsers(con, vo);
            check = userPhonesDao.previousAutoC2CStatus(con, vo);
            if (check){
                int updateCount=userPhonesDao.updateAutoC2CStatus(con, vo, ProcessI.STATUS_UNDERPROCESS);
                if (updateCount>0){
                    OracleUtil.commit(con);
					}
                if (!list.isEmpty()) {
                    new AutoC2CTransfer().process(con, list);
				}
			}else{
                OracleUtil.rollbackConnection(con,"AutoC2CThread",METHOD_NAME);
                EventHandler.handle(EventIDI.SYSTEM_INFO,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.INFO,"AutoC2CThread[run]","","","",
                        "Auto C2C is already running User VO="+ vo.getMsisdn());
			}
		} catch(Exception e){
            try{
                OracleUtil.rollbackConnection(con,"AutoC2CThread",METHOD_NAME);
			}catch(Exception e1){
                _log.errorTrace(METHOD_NAME, e1);
			}
            _log.errorTrace(METHOD_NAME, e);
		} finally {
            try {
                if (check) {
                    int updateCount=userPhonesDao.updateAutoC2CStatus(con, vo,ProcessI.STATUS_COMPLETE);
                    if (updateCount>0){
                        OracleUtil.commit(con);
					}
				}
			} catch (SQLException sqle) {
                try {
                    OracleUtil.rollbackConnection(con,"AutoC2CThread",METHOD_NAME);
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);
                }
                _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
                _log.errorTrace(METHOD_NAME, sqle);
                EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM, EventStatusI.RAISED,EventLevelI.FATAL, "AutoC2CThread[run]", "", "", "",
                     "SQL Exception:" + sqle.getMessage());
			} catch (BTSLBaseException e1) {
                try{
                    OracleUtil.rollbackConnection(con,"AutoC2CThread",METHOD_NAME);
                } catch (Exception e) {
                    _log.errorTrace(METHOD_NAME, e);	
				}
                _log.error(METHOD_NAME, "SQLException " + e1.getMessage());
                _log.errorTrace(METHOD_NAME, e1);
			} finally {
				if (mcomCon != null) {
					mcomCon.close("AutoC2CBL#run");
					mcomCon = null;
				}
			}
		}
	}

    public static ArrayList<LowBalanceAlertVO> loadMinBalanceUsers(Connection p_con, LowBalanceAlertVO vo) throws BTSLBaseException {

        final String METHOD_NAME = "loadMinBalanceUsers";
        if (_log.isDebugEnabled()) {
            _log.info(METHOD_NAME, "Entered");
        }
        PreparedStatement pstmt = null;
        ResultSet rst = null;
        ArrayList<LowBalanceAlertVO> list = null;
        try {
            final StringBuilder queryBuf = new StringBuilder("select distinct u.user_id, U.msisdn, U.parent_id, U.network_code,U.category_code,");
            queryBuf.append(" CU.transfer_profile_id,UB.balance,CU.auto_c2c_quantity ");
            queryBuf.append(" from users U, channel_users CU, user_balances UB ");
            queryBuf.append(" where u.user_id=? AND u.network_code=? AND CU.user_id=U.user_id AND ub.product_code=? ");
            queryBuf.append(" AND UB.user_id=U.user_id AND U.status <> ? AND U.parent_id <> ? AND CU.auto_c2C_allow='Y' and CU.auto_c2c_quantity>0 ");
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_C2C_SOS_CAT_ALLOWED))).booleanValue()){
                queryBuf.append(" AND category_code like ? and category_code not in (Select control_code from control_preferences "
                       + "where preference_code = ? and upper(value) = ? ) ");
            }else{
                queryBuf.append(" AND category_code IN (Select control_code from control_preferences where preference_code = ? and lower(value) = ? )");	
            }

            final String query = queryBuf.toString();
            if (_log.isDebugEnabled()) {
                _log.debug(METHOD_NAME, "Query:" + query);
            }
            pstmt = p_con.prepareStatement(query.toString());
            pstmt.setString(1, vo.getUserId());
            pstmt.setString(2, vo.getNetworkCode());
            pstmt.setString(3, vo.getProductCode());
            pstmt.setString(4, PretupsI.NO);
            pstmt.setString(5, PretupsI.ROOT_PARENT_ID);
            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_C2C_SOS_CAT_ALLOWED))).booleanValue()){
                pstmt.setString(6,"%");
                pstmt.setString(7, PretupsI.AUTO_C2C_SOS_CAT_ALLOWED);
                pstmt.setString(8, PretupsI.FALSE);
            }else{
                pstmt.setString(6, PretupsI.AUTO_C2C_SOS_CAT_ALLOWED);
                pstmt.setString(7, PretupsI.AUTO_C2C_TRUE);  	
            }
            rst = pstmt.executeQuery();
            list = new ArrayList<LowBalanceAlertVO>();
            while (rst.next()) {
                vo.setMsisdn(rst.getString("msisdn"));
                vo.setParentUserId(rst.getString("parent_id"));
                vo.setNetworkCode(rst.getString("network_code"));
                vo.setCategoryCode(rst.getString("category_code"));
                vo.setProfileID(rst.getString("transfer_profile_id"));
                vo.setBalance(rst.getLong("balance"));
                vo.setAutoc2cquantity(rst.getString("auto_c2c_quantity"));
                list.add(vo);
            }
        } catch (SQLException sqle) {
            _log.error(METHOD_NAME, "SQLException " + sqle.getMessage());
            _log.errorTrace(METHOD_NAME, sqle);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoC2CThread[loadMinBalanceUsers]", "", "", "",
                "SQL Exception:" + sqle.getMessage());
            throw new BTSLBaseException("AutoC2CThread", METHOD_NAME, "error.general.sql.processing");
        } catch (Exception e) {
            _log.error(METHOD_NAME, "Exception " + e.getMessage());
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "AutoC2CThread[loadMinBalanceUsers]", "", "", "",
                "Exception:" + e.getMessage());
            throw new BTSLBaseException("AutoC2CThread", METHOD_NAME, "error.general.processing");
        } finally {
            try{
            	if (rst!= null){
            		rst.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing result set.", e);
            }
            try{
            	if (pstmt!= null){
            		pstmt.close();
            	}
            }
            catch (SQLException e){
            	_log.error("An error occurred closing statement.", e);
            }
            if (_log.isDebugEnabled()) {
                _log.info(METHOD_NAME, " Exiting list size " + list.size());
            }
        }
        return list;
    }
    
}

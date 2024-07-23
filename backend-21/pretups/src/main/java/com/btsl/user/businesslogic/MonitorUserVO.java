package com.btsl.user.businesslogic;

/* monitorUserVO.java
 * Name                                 Date            History
 *------------------------------------------------------------------------
 * Sanjeev SHarma                    1/09/2009         Initial Creation
 * Vikas Jauhari					 11/07/2011		   Modification
 *------------------------------------------------------------------------
 * Copyright (c) 2009 Bharti Telesoft Ltd.
 */
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.loadcontroller.InstanceLoadVO;
import com.btsl.loadcontroller.LoadControllerDAO;
import com.btsl.loadcontroller.NetworkLoadVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SessionCounter;
import org.spring.custom.action.Globals;

public class MonitorUserVO implements Serializable {

	private static Log _log = LogFactory.getLog(MonitorUserVO.class.getName());
	private String _userID;
	private String _userName;
	private String _networkID;
	private String _loginID;
	private String _password;
	private String _categoryCode;
	private String _allowedIps;
	private String _status;
	private String _email;
	private Date _passwordModifiedOn = null;
	private String _viewRole;
	private String _adminViewRole;
	private ArrayList _webInstancesList = null;
	private ArrayList _smsrInstancesList = null;
	private ArrayList _smspInstancesList = null;
	private ArrayList _oamInstancesList = null;
	private String _userType;
	private int _invalidPasswordCount = 0;
	private Date _passwordCountUpdatedOn;
	private String _modifiedBy;
	private Date _modifiedOn;
	private static OperatorUtilI _operatorUtil = null;

	// Add by Vikas Jauhari
	private java.util.Date _lastLoginOn;
	private String _passwordReset = null;
	private int _validStatus = 0;
	private long _maxLoginCount;
	private String _multipleLoginAllowed = null;
	private boolean _duplicateLogin = false;
	private String _duplicateHost;
	private String _remoteAddr;
	private String _sessionID;
	private int counter = 0;
	private Object list;
	private boolean _maxLoginCountReached = false;
	private ArrayList _networkDetailList = null;
	private ArrayList _networkList = null;

	// Loads operator specific class
	static {
		String utilClass = (String) PreferenceCache
				.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		try {
			_operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
		} catch (ClassNotFoundException e) {
			_log.errorTrace("static", e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChangePinController[initialize]","","","","Exception while loading the class at the call:"+ e.getMessage());
		} catch (InstantiationException e) {
			_log.errorTrace("static", e);
			EventHandler.handle(
					EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChangePinController[initialize]","","","","Exception while loading the class at the call:"+ e.getMessage());
		} catch (IllegalAccessException e) {
			_log.errorTrace("static", e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChangePinController[initialize]","","","","Exception while loading the class at the call:"+ e.getMessage());
		} catch (Exception e) {
			_log.errorTrace("static", e);
			EventHandler.handle(
					EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChangePinController[initialize]","","","","Exception while loading the class at the call:"+ e.getMessage());
		}
	}

	public void semiFlush() {
		_remoteAddr = null;
		_duplicateHost = null;
		_duplicateLogin = false;
	}

	/**
	 * Commons Logging instance.
	 */

	/**
	 * @return the userType
	 */
	public String getUserType() {
		return _userType;
	}

	/**
	 * @param userType
	 *            the userType to set
	 */
	public void setUserType(String userType) {
		_userType = userType;
	}

	public String getUserID() {
		return _userID;
	}

	public void setUserID(String userID) {
		_userID = userID;
	}

	public String getUserName() {
		return _userName;
	}

	public void setUserName(String userName) {
		_userName = userName;
	}

	public String getNetworkID() {
		return _networkID;
	}

	public void setNetworkID(String networkID) {
		_networkID = networkID;
	}

	public String getLoginID() {
		return _loginID;
	}

	public void setLoginID(String loginID) {
		_loginID = loginID;
	}

	public String getPassword() {
		return _password;
	}

	public void setPassword(String password) {
		_password = password;
	}

	public String getCategoryCode() {
		return _categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		_categoryCode = categoryCode;
	}

	public String getAllowedIps() {
		return _allowedIps;
	}

	public void setAllowedIps(String allowedIps) {
		_allowedIps = allowedIps;
	}

	public String getStatus() {
		return _status;
	}

	public void setStatus(String status) {
		_status = status;
	}

	public String getEmail() {
		return _email;
	}

	public void setEmail(String email) {
		_email = email;
	}

	public Date getPasswordModifiedOn() {
		return _passwordModifiedOn;
	}

	public void setPasswordModifiedOn(Date passwordModifiedOn) {
		_passwordModifiedOn = passwordModifiedOn;
	}

	public String getViewRole() {
		return _viewRole;
	}

	public void setViewRole(String viewRole) {
		_viewRole = viewRole;
	}

	public String getAdminViewRole() {
		return _adminViewRole;
	}

	public void setAdminViewRole(String adminViewRole) {
		_adminViewRole = adminViewRole;
	}

	public ArrayList getWebInstancesList() {
		return _webInstancesList;
	}

	public void setWebInstancesList(ArrayList webInstancesList) {
		_webInstancesList = webInstancesList;
	}

	public ArrayList getSmsrInstancesList() {
		return _smsrInstancesList;
	}

	public void setSmsrInstancesList(ArrayList smsrInstancesList) {
		_smsrInstancesList = smsrInstancesList;
	}

	public ArrayList getSmspInstancesList() {
		return _smspInstancesList;
	}

	public void setSmspInstancesList(ArrayList smspInstancesList) {
		_smspInstancesList = smspInstancesList;
	}

	public ArrayList getOamInstancesList() {
		return _oamInstancesList;
	}

	public void setOamInstancesList(ArrayList oamInstancesList) {
		_oamInstancesList = oamInstancesList;
	}

	public int getInvalidPasswordCount() {
		return _invalidPasswordCount;
	}

	public void setInvalidPasswordCount(int passwordCount) {
		_invalidPasswordCount = passwordCount;
	}

	public Date getPasswordCountUpdatedOn() {
		return _passwordCountUpdatedOn;
	}

	public void setPasswordCountUpdatedOn(Date countUpdatedOn) {
		_passwordCountUpdatedOn = countUpdatedOn;
	}

	public String getModifiedBy() {
		return _modifiedBy;
	}

	public void setModifiedBy(String by) {
		_modifiedBy = by;
	}

	public Date getModifiedOn() {
		return _modifiedOn;
	}

	public void setModifiedOn(Date on) {
		_modifiedOn = on;
	}

	/**
	 * @return Returns the lastLoginOn.
	 */
	public java.util.Date getLastLoginOn() {
		return _lastLoginOn;
	}

	public void setLastLoginOn(java.util.Date lastLoginOn) {
		_lastLoginOn = lastLoginOn;
	}

	public String getPasswordReset() {
		return _passwordReset;
	}

	public void setPasswordReset(String passwordReset) {
		_passwordReset = passwordReset;
	}

	public int getValidStatus() {
		return _validStatus;
	}

	public void setValidStatus(int validStatus) {
		_validStatus = validStatus;
	}

	public int getCounter() {
		return counter;
	}

	/**
	 * @return
	 */
	public Object getList() {
		return list;
	}

	/**
	 * @param i
	 */
	public void setCounter(int i) {
		counter = i;
	}

	/**
	 * @param list
	 */
	public void setList(Object list) {
		this.list = list;
	}

	public synchronized void incrementCounter() {
		counter++;
	}

	public synchronized void decrementCounter() {
		counter--;
	}

	public long getMaxLoginCount() {
		return _maxLoginCount;
	}

	public void setMaxLoginCount(long maxLoginCount) {
		_maxLoginCount = maxLoginCount;
	}

	public String getMultipleLoginAllowed() {
		return _multipleLoginAllowed;
	}

	public void setMultipleLoginAllowed(String multipleLoginAllowed) {
		_multipleLoginAllowed = multipleLoginAllowed;
	}

	public boolean isDuplicateLogin() {
		return _duplicateLogin;
	}

	public void setDuplicateLogin(boolean duplicateLogin) {
		_duplicateLogin = duplicateLogin;
	}

	public String getDuplicateHost() {
		return _duplicateHost;
	}

	public void setDuplicateHost(String duplicateHost) {
		_duplicateHost = duplicateHost;
	}

	public String getRemoteAddr() {
		return _remoteAddr;
	}

	public void setRemoteAddr(String remoteAddr) {
		_remoteAddr = remoteAddr;
	}

	public String getSessionID() {
		return _sessionID;
	}

	public void setSessionID(String sessionID) {
		_sessionID = sessionID;
	}

	public boolean isMaxloginCountReached() {
		return _maxLoginCountReached;
	}

	public void setMaxloginCountReached(boolean maxLoginCountReached) {
		_maxLoginCountReached = maxLoginCountReached;
	}

	/**
	 * Method loadMonitorUserDetails.
	 * 
	 * @param String
	 *            p_login_Id
	 * @param String
	 *            p_password
	 * @param MonitorUserVO
	 *            p_monitoruservo
	 * @throws BTSLBaseException
	 */

	public boolean loadMonitorUserDetails(String p_login_Id, String p_password,
			MonitorUserVO p_monitoruservo, HttpServletRequest request) {
		final String methodName = "loadMonitorUserDetails";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,
					"Entered in loadMonitorUserDetails to validate the user with p_login_Id ::"
							+ p_login_Id + " & p_password ::" + p_password
							+ " & p_monitoruservo ::" + p_monitoruservo);
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String loginid = p_login_Id;
		String decrypt_pass = null;
		boolean flag = false;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			StringBuffer strBuff = new StringBuffer();
			strBuff.append("select usr.user_id,usr.user_name,usr.network_code,usr.login_id,");
			strBuff.append("usr.password,usr.category_code,usr.status,usr.last_login_on,");
			strBuff.append("usr.pswd_modified_on,usr.user_type,usr.invalid_password_count,");
			strBuff.append("usr.password_count_updated_on,usr.pswd_reset,cat.multiple_login_allowed, cat.max_login_count ");
			strBuff.append("FROM users usr, categories cat WHERE login_id=? ");
			strBuff.append("AND usr.category_code ='MONTR' AND usr.category_code= cat.category_code AND usr.status='Y'");
			String sqlSelect = strBuff.toString();
			if (_log.isDebugEnabled()) {
				_log.debug("validatePasswordDetails", "Select Query= "
						+ sqlSelect);
			}

			pst = con.prepareStatement(sqlSelect);
			pst.setString(1, loginid);
			rs = pst.executeQuery();
			while (rs.next()) {
				p_monitoruservo.setUserID(rs.getString("user_id"));
				p_monitoruservo.setUserName(rs.getString("user_name"));
				p_monitoruservo.setNetworkID(rs.getString("network_code"));
				p_monitoruservo.setLoginID(rs.getString("login_id"));
				decrypt_pass = rs.getString("password");
				p_monitoruservo.setPassword(decrypt_pass);
				p_monitoruservo.setCategoryCode(rs.getString("category_code"));
				p_monitoruservo.setStatus(rs.getString("status"));
				if (rs.getTimestamp("last_login_on") != null) {
					p_monitoruservo.setLastLoginOn(BTSLUtil
							.getTimestampFromUtilDate(rs
									.getTimestamp("last_login_on")));
				}
				if (rs.getTimestamp("pswd_modified_on") != null) {
					p_monitoruservo.setPasswordModifiedOn(BTSLUtil
							.getTimestampFromUtilDate(rs
									.getTimestamp("pswd_modified_on")));
				}
				p_monitoruservo.setUserType(rs.getString("user_type"));
				p_monitoruservo.setInvalidPasswordCount(rs
						.getInt("invalid_password_count"));
				if (rs.getTimestamp("password_count_updated_on") != null) {
					p_monitoruservo
					.setPasswordCountUpdatedOn(BTSLUtil.getTimestampFromUtilDate(rs
							.getTimestamp("password_count_updated_on")));
				}
				p_monitoruservo.setPasswordReset(rs.getString("pswd_reset"));
				p_monitoruservo.setMultipleLoginAllowed(rs
						.getString("multiple_login_allowed"));
				p_monitoruservo.setMaxLoginCount(rs.getLong("max_login_count"));
				flag = true;
			}
			p_monitoruservo.setSessionID((String) request
					.getAttribute("sessionID"));
			p_monitoruservo.setRemoteAddr((String) request
					.getAttribute("ipAddress"));

			recreateSession(p_monitoruservo, request);

			if (decrypt_pass != null
					&& !decrypt_pass.equalsIgnoreCase(BTSLUtil
							.encryptText(p_password))) {
				flag = false;
			} else if (p_monitoruservo.getInvalidPasswordCount() >= ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_PASSWORD_BLOCK_COUNT))).intValue()) {
				flag = false;
			} else if ((TypesI.NO).equalsIgnoreCase(p_monitoruservo.getMultipleLoginAllowed())) {
				p_monitoruservo = checkDuplicateLogin(p_monitoruservo);
				if (p_monitoruservo.isDuplicateLogin()) {
					flag = false;
				}
			} else {
				if (p_monitoruservo.getInvalidPasswordCount() > 0) {
					Date currentDate = new Date();
					p_monitoruservo.setInvalidPasswordCount(0);
					p_monitoruservo.setModifiedOn(currentDate);
					p_monitoruservo.setPasswordCountUpdatedOn(currentDate);
					int updated = updatePasswordCounter(con, p_monitoruservo);
					if (updated > 0) {
						mcomCon.finalCommit();
					} else {
						mcomCon.finalRollback();
					}
				}
			}
			if (_log.isDebugEnabled()) {
				_log.debug("loadMonitorUserDetails()",
						" Calling SessionCounter to checkMaxLocationTypeMonitorUsers in MonitorUserVO");
			}
			try {
				SessionCounter
				.checkMaxLocationTypeMonitorUsers(p_monitoruservo);
			} catch (BTSLBaseException e) {
				_log.errorTrace(methodName, e);
				flag = false;
				p_monitoruservo.setMaxloginCountReached(true);
				throw new BTSLBaseException(this, methodName,
						"user.sessioncounter.mesage.errormaxtypecountreached",
						"welcomeHome");
			}
		} catch (SQLException sqexp) {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Got exception" + sqexp.getMessage());
			}
			flag = false;
			_log.errorTrace(methodName, sqexp);
		} catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Got exception" + e.getMessage());
			}
			flag = false;
			_log.errorTrace(methodName, e);
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				_log.errorTrace(methodName, e);
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException e) {
				_log.errorTrace(methodName, e);
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			if (mcomCon != null) {
				mcomCon.close("MonitorUserVO#loadMonitorUserDetails");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting Value of Flag :: " + flag);
			}
		}// end of finally
		return flag;
	}

	public void instanceDetails() {
		final String methodName = "instanceDetails";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered to load instance Details");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		ArrayList list = null;
		ArrayList web_instances_list = new ArrayList();
		ArrayList smsr_instances_list = new ArrayList();
		ArrayList smsp_instances_list = new ArrayList();
		ArrayList oam_instances_list = new ArrayList();

		InstanceLoadVO instanceLoadVO = null;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			LoadControllerDAO controllerDAO = new LoadControllerDAO();
			list = controllerDAO.loadInstanceLoadDetails(con);

			if (list != null && !(list.isEmpty())) {
				for (int i = 0, j = list.size(); i < j; i++) {
					instanceLoadVO = (InstanceLoadVO) list.get(i);

					if ("WEB".equals(instanceLoadVO.getInstanceType())) {
						web_instances_list.add(instanceLoadVO);
					}
					if ("SMS".equals(instanceLoadVO.getInstanceType())
							&& "C2S".equalsIgnoreCase(instanceLoadVO
									.getModule())) {
						smsr_instances_list.add(instanceLoadVO);
					}
					if ("SMS".equals(instanceLoadVO.getInstanceType())
							&& "P2P".equalsIgnoreCase(instanceLoadVO
									.getModule())) {
						smsp_instances_list.add(instanceLoadVO);
					}
					if ("OAM".equals(instanceLoadVO.getInstanceType())) {
						oam_instances_list.add(instanceLoadVO);
					}
				}
				setWebInstancesList(web_instances_list);
				setSmsrInstancesList(smsr_instances_list);
				setSmspInstancesList(smsp_instances_list);
				setOamInstancesList(oam_instances_list);
			}

			this.loadNetworkDetails(con);

		} catch (BTSLBaseException e) {
			_log.errorTrace(methodName, e);
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Got exception" + e.getMessage());
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Got exception" + e.getMessage());
			}
		}// end of catch
		finally {
			if (mcomCon != null) {
				mcomCon.close("MonitorUserVO#instanceDetails");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting");
			}
		}// end of finally
	}

	public ArrayList<String> getData(HashMap AMap) {
		Set commonKeys = new HashSet(AMap.keySet());
		ArrayList<String> ruleGroupIds = new ArrayList<String>(commonKeys);
		for (Object key : commonKeys) {
			ArrayList<String> value = (ArrayList<String>) AMap.get(key);
			ruleGroupIds.addAll(value);
		}
		if (_log.isDebugEnabled()) {
			_log.debug("getData", "Exiting with ruleGroupIds :: "
					+ ruleGroupIds);
		}

		return ruleGroupIds;
	}

	public int changePassword(MonitorUserVO p_monitoruservo, String p_old_pswd,
			String p_new_pswd, String p_conf_pswd) {
		final String methodName = "changePassword";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered for change password");
		}
		PreparedStatement psmt = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		int status = -1;
		HashMap messageMap;
		boolean isHistory = false;
		try {
			if (p_old_pswd.length() == 0 || p_new_pswd.length() == 0
					|| p_conf_pswd.length() == 0) {
				return status = 4;
			}

			messageMap = _operatorUtil.validatePassword(
					p_monitoruservo.getLoginID(), p_new_pswd);
			if (!(messageMap.isEmpty())) {
				return status = 5;
			}
			p_old_pswd = BTSLUtil.encryptText(p_old_pswd);
			p_new_pswd = BTSLUtil.encryptText(p_new_pswd);
			p_conf_pswd = BTSLUtil.encryptText(p_conf_pswd);
			if (!p_monitoruservo.getPassword().equals(p_old_pswd)) {
				return status = 1;
			} else if (p_old_pswd.equals(p_new_pswd)) {
				return status = 2;
			} else if (p_new_pswd == null || p_conf_pswd == null
					|| !p_new_pswd.equals(p_conf_pswd)) {
				return status = 3;
			} else {
				mcomCon = new MComConnection();
				con=mcomCon.getConnection();
				isHistory = checkPasswordHistory(con,
						PretupsI.USER_PASSWORD_MANAGEMENT,
						p_monitoruservo.getLoginID(), p_new_pswd);
				if (isHistory) {

					return status = 6;
				}
				Date currentDate = new Date();
				String queryString = "UPDATE USERS SET PASSWORD=?, PSWD_MODIFIED_ON=?, PSWD_RESET=? where LOGIN_ID=? and CATEGORY_CODE='MONTR' AND status='Y'";
				psmt = con.prepareStatement(queryString);
				psmt.setString(1, p_new_pswd);
				psmt.setTimestamp(2,
						BTSLUtil.getSQLDateTimeFromUtilDate(currentDate));
				psmt.setString(3, TypesI.NO);
				psmt.setString(4, p_monitoruservo.getLoginID());
				int i = psmt.executeUpdate();
				if (i > 0) {
					status = 0;
					mcomCon.finalCommit();
				} else {
					status = -1;
					mcomCon.finalRollback();
				}
				return status;
			}
		} catch (BTSLBaseException be) {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Got exception" + be.getMessage());
			}
			_log.errorTrace(methodName, be);
			return status = 5;
		} catch (SQLException sqexp) {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Got exception" + sqexp.getMessage());
			}
			_log.errorTrace(methodName, sqexp);
			return status = 5;
		} catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Got exception" + e.getMessage());
			}
			_log.errorTrace(methodName, e);
			return status = -1;
		} finally {
			try {
				if (psmt != null) {
					psmt.close();
				}
			} catch (SQLException e) {
				_log.errorTrace(methodName, e);
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			if (mcomCon != null) {
				mcomCon.close("MonitorUserVO#changePassword");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting with status ::" + status);
			}
		}// end of finally
	}

	public boolean updatePasswordInvalidCount(MonitorUserVO p_monitoruservo) {
		final String methodName = "updatePasswordInvalidCount";
		if (_log.isDebugEnabled()) {
			_log.debug(
					methodName,
					"Entered in updatePasswordInvalidCount to check invalid password count for the user");
		}

		Connection con = null;
		MComConnectionI mcomCon = null;
		boolean blocked = false;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			Date currentDate = new Date();
			int passCounter = 0;
			p_monitoruservo.setPasswordCountUpdatedOn(currentDate);
			p_monitoruservo.setModifiedOn(currentDate);
			if (!(p_monitoruservo.getInvalidPasswordCount() >= ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_PASSWORD_BLOCK_COUNT))).intValue())) {
				p_monitoruservo.setInvalidPasswordCount(p_monitoruservo
						.getInvalidPasswordCount() + 1);
				passCounter = updatePasswordCounter(con, p_monitoruservo);
				if (passCounter > 0) {
					mcomCon.finalCommit();
					if (p_monitoruservo.getInvalidPasswordCount() == ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_PASSWORD_BLOCK_COUNT))).intValue()) {
						blocked = true;
					}
				} else {
					mcomCon.finalRollback();
					blocked = false;
				}
			} else {
				blocked = true;
			}

		} catch (BTSLBaseException e) {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Got exception" + e.getMessage());
			}
			blocked = false;
			_log.errorTrace(methodName, e);
		} catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Got exception" + e.getMessage());
			}
			blocked = false;
			_log.errorTrace(methodName, e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("MonitorUserVO#updatePasswordInvalidCount");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting");
			}
		}// end of finally
		return blocked;
	}

	/**
	 * Method for checking Pasword or already exist in Pin_Password_history
	 * table or not.
	 * 
	 * @param p_con
	 *            java.sql.Connection
	 * @param p_modificationType
	 *            String
	 * @param p_loginId
	 *            String
	 * @param p_newPassword
	 *            String
	 * @return flag boolean
	 * @throws BTSLBaseException
	 */
	private boolean checkPasswordHistory(Connection p_con,
			String p_modificationType, String p_loginId, String p_newPassword)
					throws BTSLBaseException {
		final String methodName = "checkPasswordHistory";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered: p_modification_type="
					+ p_modificationType + "p_loginId=" + p_loginId
					);
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean existFlag = false;
		StringBuffer strBuff = new StringBuffer();

		strBuff.append(" SELECT pin_or_password,modified_on FROM (SELECT pin_or_password,modified_on,  row_number()  over (ORDER BY modified_on DESC) rn  ");
		strBuff.append(" FROM pin_password_history WHERE modification_type= ? AND msisdn_or_loginid=? ) X  WHERE rn <= ? ");
		strBuff.append(" ORDER BY modified_on DESC ");
		
		String sqlSelect = strBuff.toString();
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "QUERY sqlSelect=" + sqlSelect);
		}
		try {

			pstmt = p_con.prepareStatement(sqlSelect);
			pstmt.setString(1, p_modificationType);
			pstmt.setString(2, p_loginId);
			pstmt.setInt(3, ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PASS_NOT_ALLOW))).intValue());
			rs = pstmt.executeQuery();
			while (rs.next()) {
				if (rs.getString("Pin_or_Password").equals(p_newPassword)) {
					existFlag = true;
					break;
				}
			}
			return existFlag;
		} catch (SQLException sqe) {
			_log.error(methodName, "SQLException : " + sqe);
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"UserDAO[checkPasswordHistory]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		} catch (Exception ex) {
			_log.error(methodName, "Exception : " + ex);
			_log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"UserDAO[checkPasswordHistory]", "", "", "", "Exception:"
							+ ex.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				_log.errorTrace(methodName, e);
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "QUERY pstmt=   " + pstmt);
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (SQLException e) {
				_log.errorTrace(methodName, e);
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting: existFlag=" + existFlag);
			}
		}
	}

	/**
	 * Method updatePasswordCounter.
	 * 
	 * @param p_con
	 *            Connection
	 * @param p_userVO
	 *            UserVO
	 * @return int
	 * @throws BTSLBaseException
	 */
	private int updatePasswordCounter(Connection p_con,
			MonitorUserVO p_monitoruservo) throws BTSLBaseException {
		final String methodName = "updatePasswordCounter";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered p_monitoruservo :"
					+ p_monitoruservo);
		}
		PreparedStatement pstmtUpdate = null;
		int updateCount = 0;
		try {
			int i = 1;
			StringBuffer updateQueryBuff = new StringBuffer(
					"UPDATE users SET invalid_password_count = ?, password_count_updated_on=? , modified_on =?   ");
			updateQueryBuff
			.append("WHERE login_id=? and category_code='MONTR' and status='Y' ");
			String selectUpdate = updateQueryBuff.toString();
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "select query:" + selectUpdate);
			}
			pstmtUpdate = p_con.prepareStatement(selectUpdate);
			pstmtUpdate.setInt(i, p_monitoruservo.getInvalidPasswordCount());
			i++;
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Update Password Count" + "  "
						+ p_monitoruservo.getInvalidPasswordCount());
			}
			if (p_monitoruservo.getPasswordCountUpdatedOn() != null) {
				pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_monitoruservo.getPasswordCountUpdatedOn()));
				i++;
			} else {
				pstmtUpdate.setNull(i, Types.TIMESTAMP);
				i++;
			}
			pstmtUpdate.setTimestamp(i, BTSLUtil.getTimestampFromUtilDate(p_monitoruservo.getModifiedOn()));
			i++;
			pstmtUpdate.setString(i, p_monitoruservo.getLoginID());
			i++;
			updateCount = pstmtUpdate.executeUpdate();
			return updateCount;
		}// end of try
		catch (SQLException sqle) {
			_log.error(methodName, "SQLException " + sqle.getMessage());
			_log.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"LoginDAO[updatePasswordCounter]", "", "", "",
					"SQL Exception:" + sqle.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		}// end of catch
		catch (Exception e) {
			_log.error(methodName, "Exception " + e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"LoginDAO[updatePasswordCounter]", "", "", "", "Exception:"
							+ e.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		}// end of catch
		finally {
			try {
				if (pstmtUpdate != null) {
					pstmtUpdate.close();
				}
			} catch (SQLException e) {
				_log.errorTrace(methodName, e);
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting updateCount:" + updateCount);
			}
		}// end of finally
	}

	/**
	 * Method validateuserId.
	 * 
	 * @param String
	 *            p_login_Id
	 * @return boolean
	 * @throws BTSLBaseException
	 */
	public boolean validateuserId(String p_login_Id) throws BTSLBaseException {
		final String methodName = "validateuserId";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,
					"Entered in validatePasswordDetails to validate the user with p_login_Id ::"
							+ p_login_Id);
		}
		MonitorUserVO monitorUserVO = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String loginid = p_login_Id;
		boolean isValidUserId = false;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			StringBuffer strBuff = new StringBuffer();
			strBuff.append("select user_id  FROM users WHERE login_id=? AND category_code ='MONTR' AND status='Y'");
			String sqlSelect = strBuff.toString();
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Select Query= " + sqlSelect);
			}

			pst = con.prepareStatement(sqlSelect);
			pst.setString(1, loginid);
			rs = pst.executeQuery();
			monitorUserVO = new MonitorUserVO();
			while (rs.next()) {
				monitorUserVO.setUserID(rs.getString("user_id"));
				isValidUserId = true;
			}

		} catch (BTSLBaseException btslex) {
			_log.error(methodName, "SQLException : " + btslex);
			_log.errorTrace(methodName, btslex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"MonitorUserVO[validatePasswordDetails]", "", "", "",
					"BTSL Exception:" + btslex.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		} catch (SQLException sqe) {
			_log.error(methodName, "SQLException : " + sqe);
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"MonitorUserVO[validatePasswordDetails]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		} catch (Exception ex) {
			_log.error(methodName, "Exception : " + ex);
			_log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"MonitorUserVO[validatePasswordDetails]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				_log.errorTrace(methodName, e);
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException e) {
				_log.errorTrace(methodName, e);
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			if (mcomCon != null) {
				mcomCon.close("MonitorUserVO#validateuserId");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, " isValidUserId=" + isValidUserId);
			}
		}
		return isValidUserId;
	}

	/**
	 * Method validatePasswordDetails.
	 * 
	 * @param String
	 *            p_login_Id
	 * @param String
	 *            p_password
	 * @return boolean
	 * @throws BTSLBaseException
	 */
	public boolean validatePasswordDetails(String p_login_Id, String p_password)
			throws BTSLBaseException {
		final String methodName = "validatePasswordDetails";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,
					"Entered in validatePasswordDetails to validate the user with p_login_Id ::"
							+ p_login_Id + " & p_password ::" + p_password);
		}
		MonitorUserVO monitorUserVO = null;
		Connection con = null;
		MComConnectionI mcomCon = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		String loginid = p_login_Id;
		String decrypt_pass = null;
		boolean isFirstTimeLogin = false;
		boolean changePassword = false;
		long passwordTimeOutDaysValue = 0;
		long noDaysAfterPwsdModificationRqd = 0;
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			StringBuffer strBuff = new StringBuffer();
			strBuff.append("select user_id,user_name,network_code,login_id,password,category_code,status,last_login_on,");
			strBuff.append("pswd_modified_on,user_type,invalid_password_count,password_count_updated_on,pswd_reset");
			strBuff.append(" FROM users WHERE login_id=? AND category_code ='MONTR' AND status='Y'");
			String sqlSelect = strBuff.toString();
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Select Query= " + sqlSelect);
			}

			pst = con.prepareStatement(sqlSelect);
			pst.setString(1, loginid);
			rs = pst.executeQuery();
			monitorUserVO = new MonitorUserVO();
			while (rs.next()) {
				monitorUserVO.setUserID(rs.getString("user_id"));
				monitorUserVO.setUserName(rs.getString("user_name"));
				monitorUserVO.setNetworkID(rs.getString("network_code"));
				monitorUserVO.setLoginID(rs.getString("login_id"));
				decrypt_pass = rs.getString("password");
				monitorUserVO.setPassword(decrypt_pass);
				monitorUserVO.setCategoryCode(rs.getString("category_code"));
				monitorUserVO.setStatus(rs.getString("status"));
				if (rs.getTimestamp("last_login_on") != null) {
					monitorUserVO.setLastLoginOn(BTSLUtil
							.getTimestampFromUtilDate(rs
									.getTimestamp("last_login_on")));
				}
				if (rs.getTimestamp("pswd_modified_on") != null) {
					monitorUserVO.setPasswordModifiedOn(BTSLUtil
							.getTimestampFromUtilDate(rs
									.getTimestamp("pswd_modified_on")));
				}
				monitorUserVO.setUserType(rs.getString("user_type"));
				monitorUserVO.setInvalidPasswordCount(rs
						.getInt("invalid_password_count"));
				if (rs.getTimestamp("password_count_updated_on") != null) {
					monitorUserVO
					.setPasswordCountUpdatedOn(BTSLUtil.getTimestampFromUtilDate(rs
							.getTimestamp("password_count_updated_on")));
				}
				monitorUserVO.setPasswordReset(rs.getString("pswd_reset"));
			}

			if (PretupsI.USER_STATUS_BLOCK.equals(monitorUserVO.getStatus())) {
				throw new BTSLBaseException(this, methodName,
						"login.index.error.userblocked");
			} else if (PretupsI.USER_STATUS_DELETED.equals(monitorUserVO
					.getStatus())) {
				throw new BTSLBaseException(this, methodName,
						"login.index.error.userdeleted");
			}
			if (!(monitorUserVO.getPasswordModifiedOn() == null)) {
				java.util.Date date1 = monitorUserVO.getPasswordModifiedOn();
				java.util.Date date2 = new java.util.Date();
				long dt1 = date1.getTime();
				long dt2 = date2.getTime();
				noDaysAfterPwsdModificationRqd = (dt2 - dt1)
						/ (1000 * 60 * 60 * 24);
				try {
					passwordTimeOutDaysValue = ((Integer) PreferenceCache
							.getControlPreference(
									PreferenceI.DAYS_AFTER_CHANGE_PASSWORD,
									monitorUserVO.getNetworkID(),
									monitorUserVO.getCategoryCode()))
									.intValue();
				} catch (Exception e) {
					_log.error(methodName, "Exception : " + e);
					_log.errorTrace(methodName, e);
					throw new BTSLBaseException(this, methodName,
							"error.general.processing");
				}
			}
			if (monitorUserVO.getLastLoginOn() == null
					&& monitorUserVO.getPasswordModifiedOn() == null) {
				isFirstTimeLogin = true;
				changePassword = true;
			} else if (monitorUserVO.getLastLoginOn() == null
					|| isFirstTimeLogin
					|| monitorUserVO.getPasswordModifiedOn() == null) {
				changePassword = true;
			} else if (noDaysAfterPwsdModificationRqd > passwordTimeOutDaysValue) {
				changePassword = true;
			} else {
				changePassword = false;
			}

			monitorUserVO.setLastLoginOn(new Date());
			int count = 0;
			try {
				count = updateUserLoginDetails(con, monitorUserVO);
			} catch (Exception e) {
				_log.error(methodName, "Exception : " + e);
				_log.errorTrace(methodName, e);
				throw new BTSLBaseException(this, methodName,
						"error.general.processing");
			}
			if (count > 0) {
				mcomCon.finalCommit();
			} else {
				mcomCon.finalRollback();
			}
		} catch (BTSLBaseException btslex) {
			_log.error(methodName, "SQLException : " + btslex);
			_log.errorTrace(methodName, btslex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"MonitorUserVO[validatePasswordDetails]", "", "", "",
					"BTSL Exception:" + btslex.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		} catch (SQLException sqe) {
			_log.error(methodName, "SQLException : " + sqe);
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"MonitorUserVO[validatePasswordDetails]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.sql.processing");
		} catch (Exception ex) {
			_log.error(methodName, "Exception : " + ex);
			_log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"MonitorUserVO[validatePasswordDetails]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException e) {
				_log.errorTrace(methodName, e);
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			try {
				if (pst != null) {
					pst.close();
				}
			} catch (SQLException e) {
				_log.errorTrace(methodName, e);
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			if (mcomCon != null) {
				mcomCon.close("MonitorUserVO#validatePasswordDetails");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting: QUERY pstmt=   " + pst
						+ " existFlag=" + changePassword);
			}
		}
		return changePassword;
	}

	/**
	 * Method updateUserLoginDetails.
	 * 
	 * @param Connection
	 *            p_con
	 * @param MonitorUserVO
	 *            monitorUserVO
	 * @return int
	 * @throws BTSLBaseException
	 */
	public int updateUserLoginDetails(java.sql.Connection p_con,
			MonitorUserVO p_monitoruservo) throws BTSLBaseException {
		final String methodName = "updateUserLoginDetails";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, " Entered..............");
		}
		PreparedStatement pstmtU = null;
		int count = 0;
		try {
			String updateUsers = "UPDATE users SET last_login_on=? WHERE user_id = ?";
			if (_log.isDebugEnabled()) {
				_log.info("updateUserLoginDetails ::", " Query updateUsers : "
						+ updateUsers);
			}
			pstmtU = p_con.prepareStatement(updateUsers);
			pstmtU.setDate(1, BTSLUtil.getSQLDateFromUtilDate(p_monitoruservo
					.getLastLoginOn()));
			pstmtU.setString(2, p_monitoruservo.getUserID());
			count = pstmtU.executeUpdate();
		} catch (SQLException sqe) {
			_log.error("updateUserLoginDetails ::", " Exception : " + sqe);
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"LoginDAO[updateUserLoginDetails]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		} catch (Exception ex) {
			_log.error("updateUserLoginDetails ::", " Exception : " + ex);
			_log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"LoginDAO[updateUserLoginDetails]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, methodName,
					"error.general.processing");
		} finally {
			try {
				if (pstmtU != null) {
					pstmtU.close();
				}
			} catch (Exception ex) {
				_log.error("updateUserLoginDetails ::",
						" Exception : in closing preparedstatement for Update"
								+ ex);
			}
			if (_log.isDebugEnabled()) {
				_log.debug("updateUserLoginDetails ::",
						" Exiting count=" + count + "     for user id="
								+ p_monitoruservo.getUserID());
			}
		}
		return count;
	}

	/**
	 * Method checkDuplicateLogin.
	 * 
	 * @param MonitorUserVO
	 *            p_monitoruservo
	 * @throws BTSLBaseException
	 */

	private MonitorUserVO checkDuplicateLogin(MonitorUserVO p_monitoruservo) {
		final String methodName = "checkDuplicateLogin";
		try {

			if (_log.isDebugEnabled()) {
				_log.debug(methodName,
						" Entered p_loginId:" + p_monitoruservo.getLoginID());
			}
			Hashtable h = SessionCounter.getActiveSessionsHash();
			HttpSession tempSession = null;
			Enumeration enumUser = h.keys();
			tempSession = null;
			MonitorUserVO monitoruservo = null;
			String key = null;
			while (enumUser.hasMoreElements()) {
				key = (String) enumUser.nextElement();
				if (_log.isDebugEnabled()) {
					_log.debug(methodName, "Value of key : :" + key);
				}
				tempSession = (HttpSession) h.get(key);
				if (_log.isDebugEnabled()) {
					_log.debug(methodName, "Value of tempSession : :"
							+ tempSession);
				}
				if (tempSession != null) {
					try {
						Object objectType = tempSession
								.getAttribute("loggedMonitorUser");
						if (objectType instanceof MonitorUserVO) {
							monitoruservo = (MonitorUserVO) objectType;
							if ((p_monitoruservo.getLoginID().trim())
									.equalsIgnoreCase(monitoruservo
											.getLoginID().trim())) {
								p_monitoruservo
								.setDuplicateHost(p_monitoruservo
										.getRemoteAddr());
								p_monitoruservo.setDuplicateLogin(true);
							} else {
								p_monitoruservo.setDuplicateLogin(false);
							}
						}

					} catch (java.lang.IllegalStateException e) {
						_log.errorTrace(methodName, e);
						_log.info(methodName,
								"removing due to illegal state exception key: "
										+ key);
						h.remove(key);
					}
				}
			}
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug(
						"checkDuplicateLogin ",
						" Exiting Value of isDuplicateLogin:"
								+ p_monitoruservo.isDuplicateLogin());
			}
		}
		return p_monitoruservo;
	}

	/**
	 * Method recreateSession.
	 * 
	 * @param MonitorUserVO
	 *            p_monitoruservo
	 * @throws BTSLBaseException
	 */

	private void recreateSession(MonitorUserVO p_monitoruservo,
			HttpServletRequest request) {
		final String methodName = "recreateSession";
		try {
			if (_log.isDebugEnabled()) {
				_log.debug("Entered in recreateSession",
						"Got Login ID in Login from monitorUserVO as ="
								+ p_monitoruservo.getLoginID());
			}
			HttpSession session1 = request.getSession();
			String cRandomValue = (String) session1.getAttribute("cRandom");
			if (_log.isDebugEnabled()) {
				_log.debug(methodName,
						"Invalidating for as =" + p_monitoruservo.getLoginID()
						+ " ID=" + session1.getId());
			}
			Locale locale = (Locale) session1
					.getAttribute(Globals.LOCALE_KEY);
			try {
				session1.invalidate();
			} catch (Exception e) {
				_log.errorTrace(methodName, e);
			}
			request.getSession(true);
			HttpSession session3 = request.getSession();
			session3.setAttribute("monitoruserVO", p_monitoruservo);
			session3.setAttribute(Globals.LOCALE_KEY, locale);
			session3.setAttribute("cRandom", cRandomValue);
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug("Exiting from recreateSession", "Got For ="
						+ p_monitoruservo.getLoginID() + " Session ID as="
						+ request.getSession().getId());
			}
		}
	}

	public ArrayList getNetworkDetailList() {
		return _networkDetailList;
	}

	public void setNetworkDetailList(ArrayList detailList) {
		_networkDetailList = detailList;
	}

	public ArrayList getNetworkList() {
		return _networkList;
	}

	public void setNetworkList(ArrayList list) {
		_networkList = list;
	}

	public void loadNetworkDetails(Connection p_con) {
		final String methodName = "loadNetworkDetails";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, " Entered..............");
		}
		PreparedStatement pstmtU = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		ArrayList<NetworkLoadVO> list = new ArrayList<NetworkLoadVO>();
		ArrayList<NetworkVO> networkList = new ArrayList<NetworkVO>();

		try {
			String selectNetworkDetail = "select distinct nk.network_code,n.network_name,ild.instance_id from network_load nk,instance_load ild,networks n  where nk.network_code=n.network_code and n.status='Y' and nk.instance_id=ild.instance_id order by nk.network_code ";
			String selectOnlyActiveNetworks = "select nk.network_code,nk.network_name from networks nk where status='Y' order by nk.network_code ";
			if (_log.isDebugEnabled()) {
				_log.info("loadNetworkDetails ::",
						" Query selectNetworkDetail : " + selectNetworkDetail);
			}
			try{
			pstmtU = p_con.prepareStatement(selectNetworkDetail);

			rs = pstmtU.executeQuery();
			while (rs.next()) {
				NetworkLoadVO networkLoadVO = new NetworkLoadVO();
				networkLoadVO.setInstanceID(rs.getString("instance_id"));
				networkLoadVO.setNetworkCode(rs.getString("network_code"));
				list.add(networkLoadVO);
			}
			pstmtU.clearParameters();
			}
			finally{
				
				if(pstmtU!=null)
					pstmtU.close();
			}
			pstmtU = p_con.prepareStatement(selectOnlyActiveNetworks);
			rs1 = pstmtU.executeQuery();

			while (rs1.next()) {
				NetworkVO networkVO = new NetworkVO();
				networkVO.setNetworkName(rs1.getString("network_name"));
				networkVO.setNetworkCode(rs1.getString("network_code"));
				networkList.add(networkVO);
			}
			this.setNetworkList(networkList);

			this.setNetworkDetailList(list);
		} catch (SQLException sqe) {
			_log.error("loadNetworkDetails ::", " Exception : " + sqe);
			_log.errorTrace(methodName, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"LoginDAO[loadNetworkDetails]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
		} catch (Exception ex) {
			_log.error("loadNetworkDetails ::", " Exception : " + ex);
			_log.errorTrace(methodName, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM,
					EventStatusI.RAISED, EventLevelI.FATAL,
					"LoginDAO[loadNetworkDetails]", "", "", "", "Exception:"
							+ ex.getMessage());
		} finally {
			try {
				if (pstmtU != null) {
					pstmtU.close();
				}
				if (rs != null) {
					rs.close();
				}
				if (rs1 != null) {
					rs1.close();
				}
			} catch (SQLException ex) {
				_log.error("loadNetworkDetails ::",
						" Exception : in closing preparedstatement for Update"
								+ ex);
			} catch (Exception ex) {
				_log.error("loadNetworkDetails ::",
						" Exception : in closing preparedstatement for Update"
								+ ex);
			}

		}

	}
}
package com.btsl.oauth.businesslogic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

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
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.UserPhonesDAO;
import com.btsl.user.businesslogic.OAuthRefTokenRequest;
import com.btsl.user.businesslogic.OAuthTokenRequest;
import com.btsl.user.businesslogic.OAuthTokenRes;
import com.btsl.user.businesslogic.OAuthUser;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.JWebTokenUtil;

@Repository
public class OAuthDao {

    private final Log _log = LogFactory.getLog(this.getClass().getName());
    private static final String EXCEPTION = "EXCEPTION: ";
    private static final String SQL_EXCEPTION = "SQL EXCEPTION: ";
    private static final String QUERY_KEY = "Query: ";

    
    private OAuthQry oAuthQry = (OAuthQry)ObjectProducer.getObject(QueryConstants.OAUTH_QRY, QueryConstants.QUERY_PRODUCER);
    
	/**
	 * Method : Generate new token and persist into database
	 * 
	 * @param p_con
	 * @param oAuthRefTokenReq
	 * @param encodedTokenNew
	 * @param encodedTokenRefNew
	 * @param expires
	 * @return
	 * @throws BTSLBaseException
	 */
	public int refreshToken(Connection p_con, OAuthRefTokenRequest oAuthRefTokenReq, String encodedTokenNew,
			String encodedTokenRefNew, Long[] expires) throws BTSLBaseException, SQLException {
		// commented for DB2OraclePreparedStatement psmtInsert = null;

		int insertCount = 0;
		final String methodName = "refreshToken";
		StringBuilder loggerValue = new StringBuilder();
		Date currentDate = new Date();

		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered: oAuthRefTokenReq=");
			loggerValue.append(oAuthRefTokenReq);
			_log.debug(methodName, loggerValue);
		}
		try {

			String query = oAuthQry.loadRefreshTokenQry() ; 

			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append(QUERY_KEY);
				loggerValue.append(query);
				_log.debug(methodName, loggerValue);
			}
			try (PreparedStatement pstmt = p_con.prepareStatement(query);) {

				String tokenId = JWebTokenUtil.retrieveTokenId(oAuthRefTokenReq.getRefreshToken());
				
				pstmt.setString(1, tokenId);

				try (ResultSet rs = pstmt.executeQuery();) {
					if (rs.next()) {


						String tokenId1  = JWebTokenUtil.retrieveTokenId(encodedTokenNew);
						
						
						Calendar calMod = Calendar.getInstance();
						java.util.Date modifiedTime = rs.getTimestamp("modified_on");
						calMod.setTime(modifiedTime);
						calMod.add(Calendar.SECOND, rs.getInt("expires"));
						Date dMod = calMod.getTime();

						Calendar calCur = Calendar.getInstance();
						Date dCur = calCur.getTime();

						if (dCur.before(dMod) == false) {
							//throw new Exception("Refresh Token has been expired!");
							throw new BTSLBaseException(this, "refreshToken", "refresh.token.expired");
						}

						// String query2 = oAuthQry.updateOAuthAccessTokenQry();
						String query2 = oAuthQry.persistAccessToken();
						if (_log.isDebugEnabled()) {
							loggerValue.setLength(0);
							loggerValue.append(QUERY_KEY);
							loggerValue.append(query2);
							_log.debug(methodName, loggerValue);
						}
						try (PreparedStatement pstmt2 = p_con.prepareStatement(query2);) {
							/*
							 * int ind = 1; pstmt2.setString(ind, encodedTokenNew);
							 * 
							 * ind++; pstmt2.setTimestamp(ind,
							 * BTSLUtil.getTimestampFromUtilDate(currentDate));
							 * 
							 * 
							 * ind++; pstmt2.setString(ind, tokenId1);
							 * 
							 * ind++; pstmt2.setString(ind, tokenId);
							 */
							int ind = 1;
							pstmt2.setString(ind, encodedTokenNew);

							ind++;
							pstmt2.setString(ind, oAuthRefTokenReq.getScope());

							ind++;
							pstmt2.setTimestamp(ind, BTSLUtil.getTimestampFromUtilDate(currentDate));

							ind++;
							pstmt2.setTimestamp(ind, BTSLUtil.getTimestampFromUtilDate(currentDate));

							ind++;
							pstmt2.setString(ind, expires[0].toString());

							ind++;
							pstmt2.setString(ind, oAuthRefTokenReq.getClientId());

							ind++;
							pstmt2.setString(ind, tokenId1);

							pstmt2.executeUpdate();

						}

						// query2 = oAuthQry.updateOAuthRefreshTokenQry();
						query2 = oAuthQry.persistRefreshToken();

						if (_log.isDebugEnabled()) {
							loggerValue.setLength(0);
							loggerValue.append(QUERY_KEY);
							loggerValue.append(query2);
							_log.debug(methodName, loggerValue);
						}
						try (PreparedStatement pstmt2 = p_con.prepareStatement(query2);) {
							/*
							 * int ind = 1; pstmt2.setString(ind, encodedTokenRefNew);
							 * 
							 * ind++; pstmt2.setTimestamp(ind,
							 * BTSLUtil.getTimestampFromUtilDate(currentDate));
							 * 
							 * ind++; pstmt2.setString(ind, tokenId1);
							 * 
							 * 
							 * ind++; pstmt2.setString(ind, tokenId);
							 */
							
							int ind = 1;
							pstmt2.setString(ind, encodedTokenRefNew);

							ind++;
							pstmt2.setTimestamp(ind, BTSLUtil.getTimestampFromUtilDate(currentDate));

							ind++;
							pstmt2.setTimestamp(ind, BTSLUtil.getTimestampFromUtilDate(currentDate));

							ind++;
							pstmt2.setString(ind, expires[0].toString());

							ind++;
							pstmt2.setString(ind, tokenId1);

							
							pstmt2.executeUpdate();
							p_con.commit();
						}
						
						 
						
						query2 = oAuthQry.queryOAuthUserInfo();

						if (_log.isDebugEnabled()) {
							loggerValue.setLength(0);
							loggerValue.append(QUERY_KEY);
							loggerValue.append(query2);
							_log.debug(methodName, loggerValue);
						}
						try (PreparedStatement pstmt2 = p_con.prepareStatement(query2);) {
												
							int ind = 1;
							pstmt2.setString(ind, tokenId);

							ResultSet rsetUpd  = pstmt2.executeQuery() ; 
							
							if(rsetUpd!=null) {
								while(rsetUpd.next()) {
									OAuthTokenRequest oAuthTokenReq = new OAuthTokenRequest();
									oAuthTokenReq.setReqGatewayType(rsetUpd.getString("REQUEST_GATEWAY_TYPE"));
									oAuthTokenReq.setReqGatewayCode(rsetUpd.getString("REQUEST_GATEWAY_CODE"));
									oAuthTokenReq.setReqGatewayLoginId(rsetUpd.getString("REQUEST_GATEWAY_LOGIN_ID"));
									oAuthTokenReq.setServicePort(rsetUpd.getString("SERVICE_PORT"));
									if ("SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
										oAuthTokenReq.setReqGatewayPassword(rsetUpd.getString("REQUEST_GATEWAY_PASSWORD"));
									}else {
										oAuthTokenReq.setReqGatewayPassword(BTSLUtil.decryptText(rsetUpd.getString("REQUEST_GATEWAY_PASSWORD")));
									}

									
									
									updateOAuthUsers(p_con, oAuthTokenReq, rsetUpd.getString("LOGIN_ID"), rsetUpd.getString("MSISDN"), rsetUpd.getString("EXT_CODE"), rsetUpd.getString("USER_ID"), tokenId1) ; 
								}
							}

							
							
							p_con.commit();
						}

					} else {

						//throw new Exception("Invalid Refresh Token!");
						throw new BTSLBaseException(this, "refreshToken", "refresh.token.invalid");

					}
				}

			}

		} // end of try
		catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, sqle);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserDAO[generateToken]", "", "", "", loggerValue.toString());
			throw sqle;
		} // end of catch
		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"UserDAO[generateToken]", "", "", "", loggerValue.toString());
			//throw e;
			throw new BTSLBaseException(this, "refreshToken", "refresh.token.error");
		} // end of catch
		finally {

			LogFactory.printLog(methodName, "Exiting: insertCount=" + insertCount, _log);

		} // end of finally

		return insertCount;
	}

	public String retreiveOAuthAccessTokenId(Connection p_con) throws Exception {

		final String methodName = "validateClient";
		StringBuilder loggerValue = new StringBuilder();

		boolean existFlag = false;

		String sqlSelect = oAuthQry.generateNewTokenId();

		try (PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {
			try (ResultSet rs = pstmt.executeQuery();) {
				if (rs.next()) {
					return (rs.getLong(1) + 1) + "";

				}

			}
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, sqe);
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing", sqe);
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(this, methodName, "error.general.processing", ex);
		} finally {

			LogFactory.printLog(methodName, "Exiting: existFlag=" + existFlag, _log);
		}

		return null;
	}

	public int updateOAuthAccessTokens(Connection p_con, OAuthTokenRequest oAuthTokenReq, OAuthTokenRes oAuthTokenRes,
			Long tExp, String loginId, String scope, String clientId, String tokenId) throws BTSLBaseException, SQLException {
		// commented for DB2OraclePreparedStatement psmtInsert = null;

		int insertCount = 0;
		final String methodName = "updateOAuthAccessTokens";
		StringBuilder loggerValue = new StringBuilder();
		Date currentDate = new Date();

		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered: oAuthTokenReq=");
			loggerValue.append(oAuthTokenReq);
			_log.debug(methodName, loggerValue);
		}
		try {
			String insertQuery = oAuthQry.persistAccessToken();
			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append(QUERY_KEY);
				loggerValue.append(insertQuery);
				_log.debug(methodName, loggerValue);
			}
			try (PreparedStatement psmtInsert = p_con.prepareStatement(insertQuery);) {
				// TODO: i
				int ind = 1;
				psmtInsert.setString(ind, oAuthTokenRes.getToken());

				ind++;
				psmtInsert.setString(ind, scope);

				ind++;
				psmtInsert.setTimestamp(ind, BTSLUtil.getTimestampFromUtilDate(currentDate));

				ind++;
				psmtInsert.setTimestamp(ind, BTSLUtil.getTimestampFromUtilDate(currentDate));

				ind++;
				psmtInsert.setString(ind, tExp.toString());

				ind++;
				psmtInsert.setString(ind, clientId);

				ind++;
				psmtInsert.setString(ind, tokenId);

				insertCount = psmtInsert.executeUpdate();

			}
		} // end of try
		catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, sqle);

			throw sqle;
		} // end of catch
		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, e);

			//throw e;
			throw new BTSLBaseException(this, "updateOAuthAccessTokens", "error.token.update");
		} // end of catch
		finally {

			LogFactory.printLog(methodName, "Exiting: insertCount=" + insertCount, _log);

		} // end of finally

		return insertCount;
	}

	public int updateOAuthRefreshTokens(Connection p_con, OAuthTokenRequest oAuthTokenReq, OAuthTokenRes oAuthTokenRes,
			Long rtExp, String loginId, String scope, String tokenId) throws BTSLBaseException, SQLException {
		// commented for DB2OraclePreparedStatement psmtInsert = null;

		int insertCount = 0;
		final String methodName = "updateOAuthRefreshTokens";
		StringBuilder loggerValue = new StringBuilder();
		Date currentDate = new Date();

		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered: oAuthTokenReq=");
			loggerValue.append(oAuthTokenReq);
			_log.debug(methodName, loggerValue);
		}
		try {


			String insertQuery = oAuthQry.persistRefreshToken();
			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append(QUERY_KEY);
				loggerValue.append(insertQuery);
				_log.debug(methodName, loggerValue);
			}
			try (PreparedStatement psmtInsert = p_con.prepareStatement(insertQuery);) {
				// TODO: i
				int ind = 1;
				psmtInsert.setString(ind, oAuthTokenRes.getRefreshToken());

				ind++;
				psmtInsert.setTimestamp(ind, BTSLUtil.getTimestampFromUtilDate(currentDate));

				ind++;
				psmtInsert.setTimestamp(ind, BTSLUtil.getTimestampFromUtilDate(currentDate));

				ind++;
				psmtInsert.setString(ind, rtExp.toString());

				ind++;
				psmtInsert.setString(ind, tokenId);

				insertCount = psmtInsert.executeUpdate();

			}
		} // end of try
		catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, sqle);

			throw sqle;
		} // end of catch
		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, e);

			//throw e;
			throw new BTSLBaseException(this, "updateOAuthRefreshTokens", "error.token.update");
		} // end of catch
		finally {

			LogFactory.printLog(methodName, "Exiting: insertCount=" + insertCount, _log);

		} // end of finally

		return insertCount;
	}

    /**
     * This method will insert new records
     * 
     * @param p_con
     * @param oAuthTokenReq
     * @param loginId
     * @param msisdn
     * @param extCode
     * @param userId
     * @param tokenId
     * @return
     * @throws Exception
     */
	public int updateOAuthUsers(Connection p_con, OAuthTokenRequest oAuthTokenReq,
			String loginId, String msisdn, String extCode, String userId, String tokenId) throws BTSLBaseException, SQLException {
		// commented for DB2OraclePreparedStatement psmtInsert = null;

		int insertCount = 0;
		final String methodName = "updateOAuthUsers";
		StringBuilder loggerValue = new StringBuilder();
		Date currentDate = new Date();		

		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered: oAuthTokenReq=");
			loggerValue.append(oAuthTokenReq);
			loggerValue.append(", loginId=");
			loggerValue.append(loginId);
			loggerValue.append(", msisdn=");
			loggerValue.append(msisdn);
			loggerValue.append(", extCode=");
			loggerValue.append(extCode);
			loggerValue.append(", userId=");
			loggerValue.append(userId);
			loggerValue.append(", tokenId=");
			loggerValue.append(tokenId);
			
			_log.debug(methodName, loggerValue);
		}
		try {
			String insertQuery = oAuthQry.persistOAuthUserInfo();
			
			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append(QUERY_KEY);
				loggerValue.append(insertQuery);
				_log.debug(methodName, loggerValue);
			}
			
			try (PreparedStatement psmtInsert = p_con.prepareStatement(insertQuery);) {
				// TODO: i
				int ind = 1;
				psmtInsert.setString(ind, loginId);

				ind++;
				psmtInsert.setString(ind, msisdn);

				ind++;
				psmtInsert.setString(ind, oAuthTokenReq.getReqGatewayType());

				ind++;
				psmtInsert.setString(ind, oAuthTokenReq.getReqGatewayCode());

				ind++;
				psmtInsert.setString(ind, oAuthTokenReq.getReqGatewayLoginId());

				ind++;
				psmtInsert.setString(ind, oAuthTokenReq.getServicePort());

				ind++;
				if ("SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
					psmtInsert.setString(ind, oAuthTokenReq.getReqGatewayPassword());
				}else {
					psmtInsert.setString(ind, BTSLUtil.encryptText(oAuthTokenReq.getReqGatewayPassword()));
				}
				ind++;
				psmtInsert.setString(ind, "JSON");

				ind++;
				psmtInsert.setTimestamp(ind, BTSLUtil.getTimestampFromUtilDate(currentDate));

				ind++;
				psmtInsert.setTimestamp(ind, BTSLUtil.getTimestampFromUtilDate(currentDate));

				ind++;
				psmtInsert.setString(ind, extCode);
				
				ind++;
				psmtInsert.setString(ind, userId);
				
				ind++;
				psmtInsert.setString(ind, tokenId);

				insertCount = psmtInsert.executeUpdate();
			}
		
		} // end of try
		catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, sqle);

			throw sqle;
		} // end of catch
		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
    		_log.errorTrace(methodName, e);

			throw new BTSLBaseException(this, "updateOAuthUsers", "error.token.update");
		} // end of catch
		finally {

			LogFactory.printLog(methodName, "Exiting: insertCount=" + insertCount, _log);

		} // end of finally

		return insertCount;
	}
	
	
	/**
	 *Method : generate new token and persist into database
	 *  
	 * @param p_con
	 * @param oAuthTokenReq
	 * @param oAuthTokenRes
	 * @param expires
	 * @param loginId
	 * @param msisdn
	 * @param extCode
	 * @param userId
	 * @param scope
	 * @param clientId
	 * @throws Exception
	 */
	public void generateToken(Connection p_con, OAuthTokenRequest oAuthTokenReq, OAuthTokenRes oAuthTokenRes,
			Long[] expires, String loginId, String msisdn, String extCode, String userId, String scope, String clientId)
			throws BTSLBaseException, SQLException {
		// commented for DB2OraclePreparedStatement psmtInsert = null;

		int insertCount = 0;
		final String methodName = "generateToken";
		StringBuilder loggerValue = new StringBuilder();
		Date currentDate = new Date();

		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered: oAuthTokenReq=");
			loggerValue.append(oAuthTokenReq);
			loggerValue.append(" oAuthTokenRes=");
			loggerValue.append(oAuthTokenRes);
			loggerValue.append(" expires=");
			loggerValue.append(expires);
			loggerValue.append(" loginId=");
			loggerValue.append(loginId);
			loggerValue.append(" msisdn=");
			loggerValue.append(msisdn);
			loggerValue.append(" extCode=");
			loggerValue.append(extCode);
			loggerValue.append(" userId=");
			loggerValue.append(userId);
			loggerValue.append(" scope=");
			loggerValue.append(scope);
			loggerValue.append(" clientId=");
			loggerValue.append(clientId);
			
			_log.debug(methodName, loggerValue);
		}
		try {

			String tokenId = JWebTokenUtil.retrieveTokenId(oAuthTokenRes.getToken());

			if (tokenId != null) {

				updateOAuthAccessTokens(p_con, oAuthTokenReq, oAuthTokenRes, expires[0], loginId, scope, clientId,
						tokenId);
				updateOAuthRefreshTokens(p_con, oAuthTokenReq, oAuthTokenRes, expires[1], loginId, scope, tokenId);
				updateOAuthUsers(p_con, oAuthTokenReq, loginId, msisdn, extCode, userId, tokenId);
				p_con.commit();

			}

		} // end of try
		catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, sqle);

			throw sqle;
		} // end of catch
		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, e);
			
			throw new BTSLBaseException(this, "generateToken", "error.token.generation");
		} // end of catch
		finally {

			LogFactory.printLog(methodName, "Exiting", _log);

		} // end of finally

	}

	
	public void validateToken(Connection p_con, String token) throws BTSLBaseException, SQLException {

		int insertCount = 0;
		final String methodName = "validateToken";
		StringBuilder loggerValue = new StringBuilder();

		try {


			String query = oAuthQry.validateTokenQry();
			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append(QUERY_KEY);
				loggerValue.append(query);
				_log.debug(methodName, loggerValue);
			}
			try (PreparedStatement pstmt = p_con.prepareStatement(query);) {

				pstmt.setString(1, JWebTokenUtil.retrieveTokenId(token));

				try (ResultSet rs = pstmt.executeQuery();) {
					if (rs.next()) {

						Calendar calMod = Calendar.getInstance();
						java.util.Date modifiedTime = rs.getTimestamp("modified_on");
						calMod.setTime(modifiedTime);
						calMod.add(Calendar.SECOND, rs.getInt("expires"));
						Date dMod = calMod.getTime();

						Calendar calCur = Calendar.getInstance();
						Date dCur = calCur.getTime();

						if (dCur.before(dMod) == false) {
							throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.MAPP_TOKEN_EXPIRED,
									PretupsI.RESPONSE_FAIL, null);
						}

					} else {

						throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.MAPP_INVALID_TOKEN,
								PretupsI.RESPONSE_FAIL, null);

					}
				}

			}
		} // end of try
		catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, sqle);

			throw sqle;
		} // end of catch
		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, e);

			//throw e;
			throw new BTSLBaseException(this, "validateToken", "error.token.validate");
		} // end of catch
		finally {

			LogFactory.printLog(methodName, "Exiting: insertCount=" + insertCount, _log);

		} // end of finally

	}

	public void validateToken(Connection p_con, OAuthUser oAuthUser, String token) throws BTSLBaseException, SQLException {
		// commented for DB2OraclePreparedStatement psmtInsert = null;

		int insertCount = 0;
		final String methodName = "validateToken";
		StringBuilder loggerValue = new StringBuilder();
		if (_log.isDebugEnabled()) {
			loggerValue.setLength(0);
			loggerValue.append("Entered: p_userVO=");
			loggerValue.append(oAuthUser);
			loggerValue.append(" ,token=");
			loggerValue.append(token);
			_log.debug(methodName, loggerValue);
		}
		try {

			String query = oAuthQry.validateTokenQuery();
			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append(QUERY_KEY);
				loggerValue.append(query);
				_log.debug(methodName, loggerValue);
			}
			try (PreparedStatement pstmt = p_con.prepareStatement(query);) {
				_log.debug("tokenid",JWebTokenUtil.retrieveTokenId(token));
				int itr = 0;
				pstmt.setString(++itr, JWebTokenUtil.retrieveTokenId(token));
//				pstmt.setString(++itr, oAuthUser.getData().getLoginid());

				try (ResultSet rs = pstmt.executeQuery();) {
					if (rs.next()) {

						Calendar calMod = Calendar.getInstance();
						java.util.Date modifiedTime = rs.getTimestamp("modified_on");
						calMod.setTime(modifiedTime);
						calMod.add(Calendar.SECOND, rs.getInt("expires"));
						Date dMod = calMod.getTime();

						Calendar calCur = Calendar.getInstance();
						Date dCur = calCur.getTime();

						if (dCur.before(dMod) == false) {
							throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.MAPP_TOKEN_EXPIRED);
						}

						oAuthUser.setReqGatewayType(rs.getString("request_gateway_type"));
						oAuthUser.setReqGatewayCode(rs.getString("request_gateway_code"));
						oAuthUser.setReqGatewayLoginId(rs.getString("request_gateway_login_id"));
						oAuthUser.setServicePort(rs.getString("service_port"));
						oAuthUser.setReqGatewayPassword(BTSLUtil.decryptText(rs.getString("request_gateway_password")));
						oAuthUser.setSourceType(rs.getString("source_type"));

						oAuthUser.getData().setLoginid(rs.getString("login_id"));
						oAuthUser.getData().setMsisdn(rs.getString("msisdn"));
						oAuthUser.getData().setExtcode(rs.getString("ext_code"));
						oAuthUser.getData().setUserid(rs.getString("user_id"));
						UserDAO userDAO = new UserDAO();
						UserVO userVO = userDAO.getPinPassword(p_con, rs.getString("login_id"));
						oAuthUser.getData().setPassword(BTSLUtil.decryptText(userVO.getPassword()));
						
						if(rs.getString("msisdn")==null) {//then this is case of staff w/o msisdn
							ChannelUserDAO channelUserDAO = new ChannelUserDAO();
							UserPhonesDAO userPhonesDAO = new UserPhonesDAO();
							String parentMsisdn=channelUserDAO.loadParentUserMsisdn(p_con,rs.getString("login_id"),"LOGINID");
							oAuthUser.getData().setMsisdn(parentMsisdn);
							String parentMsisdnPin = userPhonesDAO.loadPin(p_con, parentMsisdn);
							oAuthUser.getData().setPin(parentMsisdnPin);
						}else	oAuthUser.getData().setPin(BTSLUtil.decryptText(userVO.getActiveUserPin()));

					} else {

						throw new BTSLBaseException("PretupsBL", methodName, PretupsErrorCodesI.MAPP_INVALID_TOKEN);

					}
				}

			}
		} // end of try
		catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, sqle);

			throw sqle;
		} // end of catch
		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, e);

			//throw e;
			throw new BTSLBaseException(this, "validateToken", "error.token.validate");
		} // end of catch
		finally {

			LogFactory.printLog(methodName, "Exiting: insertCount=" + insertCount, _log);

		} // end of finally

	}

	/**
	 * Method : Validate Client
	 * 
	 * @param p_con
	 * @param oAuthUser
	 * @return
	 * @throws BTSLBaseException
	 */
	public boolean validateClient(Connection p_con, OAuthTokenRequest oAuthTokenReq, Long[] expires) throws Exception {

		final String methodName = "validateClient";
		StringBuilder loggerValue = new StringBuilder();

		boolean existFlag = false;

		String sqlSelect = oAuthQry.validateClient();

		try (PreparedStatement pstmt = p_con.prepareStatement(sqlSelect);) {

			pstmt.setString(1, oAuthTokenReq.getClientId());
			pstmt.setString(2, oAuthTokenReq.getClientSecret());
			pstmt.setString(3, oAuthTokenReq.getScope());
			try (ResultSet rs = pstmt.executeQuery();) {
				if (rs.next()) {
					expires[0] = rs.getLong("access_token_validity");
					expires[1] = rs.getLong("refresh_token_validity");

					return true;
				}
				return false;
			}
		} catch (SQLException sqe) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqe.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, sqe);
			throw new BTSLBaseException(this, methodName, "error.general.sql.processing", sqe);
		} catch (Exception ex) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(ex.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, ex);
			throw new BTSLBaseException(this, methodName, "error.general.processing", ex);
		} finally {

			LogFactory.printLog(methodName, "Exiting: existFlag=" + existFlag, _log);
		}

	}

	
	
	
	public void deleteToken(Connection p_con, String loginId, String tokenIdInput, String token) throws BTSLBaseException, SQLException {

		int insertCount = 0;
		final String methodName = "deleteToken";
		StringBuilder loggerValue = new StringBuilder();

		try {

			boolean singleToken = false;
			String tokenId = null;
			
			if(token !=null){
				
				tokenId  = JWebTokenUtil.retrieveTokenId(token);
				singleToken = true;
			}
			else if(loginId != null) {
				
			/*String query = oAuthQry.getAccessTokenId();
			if (_log.isDebugEnabled()) {
				loggerValue.setLength(0);
				loggerValue.append(QUERY_KEY);
				loggerValue.append(query);
				_log.debug(methodName, loggerValue);
			}*/
			//try (PreparedStatement pstmt = p_con.prepareStatement(query);) {

				//pstmt.setString(1, loginId);
				//try (ResultSet rs = pstmt.executeQuery();) {
					//while (rs.next()) {
					//	tokenId = rs.getString(1);
						
							
						
						

						String query1 = oAuthQry.deleteFromOAuthAccessToken();
						try (PreparedStatement pstmt1 = p_con.prepareStatement(query1);) {
							pstmt1.setString(1, loginId);
							pstmt1.executeUpdate();
						}

						String query2 = oAuthQry.deleteFromOAuthRefreshToken();
						try (PreparedStatement pstmt2 = p_con.prepareStatement(query2);) {
							pstmt2.setString(1, loginId);
							pstmt2.executeUpdate();
						}

						String query3 = oAuthQry.deleteFromUserLoginInfo();
						try (PreparedStatement pstmt3 = p_con.prepareStatement(query3);) {
							pstmt3.setString(1, loginId);
							pstmt3.executeUpdate();
						}
					
						
						
						
					//}
				//}
				
				
				
				
				//}
			}	else if(tokenIdInput !=null){
			
				tokenId = tokenIdInput;
				singleToken = true;
			}

			if (singleToken) {
				String query1 = oAuthQry.deleteFromOAuthAccessToken();
				try (PreparedStatement pstmt1 = p_con.prepareStatement(query1);) {
					pstmt1.setString(1, tokenId);
					pstmt1.executeUpdate();
				}

				String query2 = oAuthQry.deleteFromOAuthRefreshToken();
				try (PreparedStatement pstmt2 = p_con.prepareStatement(query2);) {
					pstmt2.setString(1, tokenId);
					pstmt2.executeUpdate();
				}

				String query3 = oAuthQry.deleteFromUserLoginInfo();
				try (PreparedStatement pstmt3 = p_con.prepareStatement(query3);) {
					pstmt3.setString(1, tokenId);
					pstmt3.executeUpdate();
				}
			}
			
		} // end of try
		catch (SQLException sqle) {
			loggerValue.setLength(0);
			loggerValue.append(SQL_EXCEPTION);
			loggerValue.append(sqle.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, sqle);

			throw sqle;
		} // end of catch
		catch (Exception e) {
			loggerValue.setLength(0);
			loggerValue.append(EXCEPTION);
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, e);

			//throw e;
			throw new BTSLBaseException(this, "validateToken", "error.token.validate");
		} // end of catch
		finally {

			LogFactory.printLog(methodName, "Exiting: insertCount=" + insertCount, _log);

		} // end of finally

	}
	
	
}

package com.btsl.pretups.user.businesslogic;

import java.io.PrintWriter;
import java.sql.Connection;
// import oracle.jdbc.OraclePreparedStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*import org.apache.struts.action.Action;
//import org.apache.struts.action.ActionForm;
//import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;*/
import org.json.simple.JSONObject;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonClient;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.module.InterfaceErrorCodesI;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleCache;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.util.BTSLUtil;

public class FetchUserData /*extends Action */{
	private static Log _log = LogFactory.getLog(FetchUserData.class.getName());
	private String _intModCommunicationTypeS;
	private String _intModIPS;
	private int _intModPortS;
	private String _intModClassNameS;
	private String _transferID;
	private HashMap<String, String> responseMap = null;
	private static int _transactionIDCounter = 0;
	private static SimpleDateFormat _sdfCompare = new SimpleDateFormat("mm");
	private static int _prevMinut = 0;
	private static OperatorUtilI _operatorUtil = null;
	// Loads operator specific class
	static {
		final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		try {
			_operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
		} catch (Exception e) {
			_log.errorTrace("ststic", e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FetchUserData[initialize]", "", "", "",
					"Exception while loading the class at the call:" + e.getMessage());
		}
	}

	/*public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		final String METHOD_NAME = "execute";
		final PrintWriter out = response.getWriter();
		try {
			String extCode = null;

			final String networkCode = (String) request.getParameter("networkCode");

			extCode = (String) request.getParameter("extCode");
			response.setContentType("text/xml");
			response.setHeader("Cache-Control", "no-cache");

			responseMap = processRequest(extCode, networkCode);
			if (responseMap == null) {
				response.setStatus(Integer.parseInt(InterfaceErrorCodesI.INVALID_RESPONSE));
				out.write("");
				out.flush();
			} else if (responseMap.get("STATUS").equalsIgnoreCase(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND)) {
				response.setStatus(Integer.parseInt(InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND));
				out.write("");
				out.flush();
			} else if (responseMap.get("STATUS").equalsIgnoreCase(InterfaceErrorCodesI.SUCCESS)) {
				response.setStatus(Integer.parseInt(InterfaceErrorCodesI.SUCCESS));
				final JSONObject value = new JSONObject();
				value.putAll(responseMap);
				out.write(value.toJSONString());
				out.flush();
			} else {
				response.setStatus(Integer.parseInt(InterfaceErrorCodesI.INVALID_RESPONSE));
				out.write("");
				out.flush();
			}

		} catch (Exception e) {
			_log.error("execute", "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			response.setStatus(Integer.parseInt(InterfaceErrorCodesI.ERROR_RESPONSE));
			out.write("");
			out.flush();
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FetchUserData[execute]", "", "", "", "Exception:" + e
					.getMessage());
			throw new BTSLBaseException(this, "execute", e.getMessage());
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug("execute", "Exiting with response" + responseMap);
			}
		}// end of finally
		return (null);
	}*/

	private HashMap<String, String> processRequest(String extCode, String networkCode) throws BTSLBaseException, Exception {
		final String METHOD_NAME = "processRequest";
		Connection con = null;
		MComConnectionI mcomCon = null;
		TransferItemVO transferItemVO = null;
		final String type = PretupsI.CUINFO;
		String receiverValResponse = null;
		final HashMap<String, String> map = null;
		HashMap<String, String> responseMap = null;
		Date mydate = null;
		String minut2Compare = null;
		try {

			mydate = new Date();
			minut2Compare = _sdfCompare.format(mydate);
			final int currentMinut = Integer.parseInt(minut2Compare);

			if (currentMinut != _prevMinut) {
				_transactionIDCounter = 1;
				_prevMinut = currentMinut;
			} else if (_transactionIDCounter >= 65535) {
				_transactionIDCounter = 1;
			} else {
				_transactionIDCounter++;
			}
			if (_transactionIDCounter == 0) {
				throw new BTSLBaseException("C2SPrepaidController", "generateC2STransferID", PretupsErrorCodesI.NOT_GENERATE_TRASNFERID);
			}
			if (_log.isDebugEnabled()) {
				_log.debug("processRequest", "Entered for Fetching User Info with External Code" + extCode);
			}
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			final NetworkInterfaceModuleVO networkInterfaceModuleVOS = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(PretupsI.C2S_MODULE, networkCode,
					PretupsI.INTERFACE_CATEGORY_PRE);
			transferItemVO = loadUserInterfaceDetails(con, type);
			_intModCommunicationTypeS = networkInterfaceModuleVOS.getCommunicationType();
			_intModIPS = networkInterfaceModuleVOS.getIP();
			_intModPortS = networkInterfaceModuleVOS.getPort();
			_intModClassNameS = networkInterfaceModuleVOS.getClassName();
			final TransferVO transferVO = new TransferVO();
			transferVO.setCreatedOn(mydate);

			_transferID = _operatorUtil.formatChnlUserTransferID(transferVO, _transactionIDCounter);
			final String requestStr = getReceiverValidateStr(con, transferItemVO, extCode);
			final CommonClient commonClient = new CommonClient();
			receiverValResponse = commonClient.process(requestStr, _transferID, networkInterfaceModuleVOS.getCommunicationType(), networkInterfaceModuleVOS.getIP(),
					networkInterfaceModuleVOS.getPort(), networkInterfaceModuleVOS.getClassName());

			if (_log.isDebugEnabled()) {
				_log.debug("processRequest", "", "Got the user Information In response =" + receiverValResponse);
			}

			try {
				responseMap = updateForReceiverValidateResponse(receiverValResponse);
			} catch (BTSLBaseException be) {
				_log.errorTrace(METHOD_NAME, be);
				responseMap = null;

			} catch (Exception e) {
				_log.errorTrace(METHOD_NAME, e);
				_log.error("processRequest", "Exception " + e.getMessage());
				responseMap = null;
			}

		} catch (Exception e) {
			_log.error("processRequest", "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			if (con != null) {
				con.rollback();
			}
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FetchUserData[processRequest]", "", "", "",
					"Exception:" + e.getMessage());
			responseMap = null;
		} finally {
			if(mcomCon != null)
			{
				mcomCon.close("FetchUserData#processRequest");
				mcomCon=null;
			}
			con = null;
			if (_log.isDebugEnabled()) {
				_log.debug("processRequest", "Exiting with response=" + responseMap);
			}

		}
		return responseMap;

	}

	/**
	 * Method loadUserInterfaceDetails() is used to load interface details
	 * for XML Authentication
	 * 
	 * @param p_con
	 * @param p_type
	 * @throws BTSLBaseException
	 * @author abhilasha
	 */
	private TransferItemVO loadUserInterfaceDetails(Connection p_con, String p_type) throws BTSLBaseException {
		if (_log.isDebugEnabled()) {
			_log.debug("loadAuthenticateUserDetails", "p_type ::" + p_type);
		}
		final String METHOD_NAME = "loadUserInterfaceDetails";
		TransferItemVO transferItemVO = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		final StringBuffer strBuff = new StringBuffer();

		strBuff.append(" SELECT i.external_id, i.status, i.message_language1, i.message_language2,");
		strBuff.append(" i.status_type statustype, i.single_state_transaction, ");
		strBuff.append(" im.handler_class, im.underprocess_msg_reqd ,");
		strBuff.append(" im.interface_type_id,i.INTERFACE_ID ");
		strBuff.append(" FROM interfaces i, interface_types im ");
		strBuff.append(" WHERE i.interface_type_id = ? and i.interface_type_id = im.interface_type_id AND i.status <> 'N'");
		final String sqlSelect = strBuff.toString();
		if (_log.isDebugEnabled()) {
			_log.debug("loadUserInterfaceDetails", "QUERY sqlSelect=" + sqlSelect);
		}
		try {
			pstmt = (PreparedStatement) p_con.prepareStatement(sqlSelect);
			if (!BTSLUtil.isNullString(p_type)) {
				pstmt.setString(1, p_type);
			}
			rs = pstmt.executeQuery();
			while (rs.next()) {
				transferItemVO = new TransferItemVO();
				transferItemVO.setInterfaceID(rs.getString("INTERFACE_ID"));
				transferItemVO.setInterfaceHandlerClass(rs.getString("handler_class"));
			}
		} catch (SQLException sqe) {
			_log.error("loadUserInterfaceDetails", "SQLException : " + sqe);
			_log.errorTrace(METHOD_NAME, sqe);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FetchUserData[loadUserInterfaceDetails]", "", "", "",
					"SQL Exception:" + sqe.getMessage());
			throw new BTSLBaseException(this, "loadUserInterfaceDetails", "error.general.sql.processing");
		} catch (Exception ex) {
			_log.error("", "Exception : " + ex);
			_log.errorTrace(METHOD_NAME, ex);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FetchUserData[loadUserInterfaceDetails]", "", "", "",
					"Exception:" + ex.getMessage());
			throw new BTSLBaseException(this, "loadUserInterfaceDetails", "error.general.processing");
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception e) {
				_log.errorTrace(METHOD_NAME, e);
			}
			try {
				if (pstmt != null) {
					pstmt.close();
				}
			} catch (Exception e) {
				_log.errorTrace(METHOD_NAME, e);
			}

			if (_log.isDebugEnabled()) {
				_log.debug("loadUserInterfaceDetails", "Exiting: ************* ");
			}
		}
		return transferItemVO;
	}

	/**
	 * Method to get the reciever validate String
	 * 
	 * @return
	 * @throws BTSLBaseException
	 */
	private String getReceiverValidateStr(Connection con, TransferItemVO transferItemVO, String extCode) throws BTSLBaseException {
		final String METHOD_NAME = "getReceiverValidateStr";
		StringBuffer strBuff = null;
		try {
			strBuff = new StringBuffer(getReceiverCommonString(transferItemVO, extCode));
			strBuff.append("&INTERFACE_ACTION=" + PretupsI.INTERFACE_VALIDATE_ACTION);
			return strBuff.toString();
		}

		catch (Exception e) {
			_log.error("getReceiverValidateStr", "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FetchUserData[getReceiverValidateStr]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "getReceiverValidateStr", e.getMessage());
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug("getReceiverValidateStr", "Exiting strbuff" + strBuff.toString());
			}
		}// end of finally
	}

	/**
	 * Method to get the string to be sent to the interface for topup
	 * 
	 * @return
	 * @throws BTSLBaseException
	 */
	private String getReceiverCommonString(TransferItemVO transferItemVO, String extCode) throws BTSLBaseException {
		final String METHOD_NAME = "getReceiverCommonString";
		StringBuffer strBuff = null;

		try {
			strBuff = new StringBuffer();
			strBuff.append("TRANSACTION_ID=" + _transferID);
			strBuff.append("&INTERFACE_ID=" + transferItemVO.getInterfaceID());
			strBuff.append("&INTERFACE_ID=" + transferItemVO.getInterfaceID());
			strBuff.append("&INTERFACE_HANDLER=" + transferItemVO.getInterfaceHandlerClass());
			strBuff.append("&INT_MOD_COMM_TYPE=" + _intModCommunicationTypeS);
			strBuff.append("&INT_MOD_IP=" + _intModIPS);
			strBuff.append("&INT_MOD_PORT=" + _intModPortS);
			strBuff.append("&INT_MOD_CLASSNAME=" + _intModClassNameS);
			strBuff.append("&MODULE=" + PretupsI.CUINFO);
			strBuff.append("&USER_TYPE=R");
			strBuff.append("&CUEXTCODE=" + extCode);

		} catch (Exception e) {
			_log.error("getReceiverCommonString", "Exception " + e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "FetchUserData[getReceiverCommonString]", "", "", "",
					"Exception:" + e.getMessage());
			throw new BTSLBaseException(this, "getReceiverCommonString", e.getMessage());
		} // end of catch
		finally {
			if (_log.isDebugEnabled()) {
				_log.debug("getReceiverCommonString", "Exiting strbuff" + strBuff.toString());
			}
		}// end of finally

		return strBuff.toString();
	}

	/**
	 * Method to process the response of the receiver validation from IN
	 * 
	 * @param str
	 * @throws BTSLBaseException
	 */
	private HashMap<String, String> updateForReceiverValidateResponse(String receiverValResponse) throws BTSLBaseException {
		final String METHOD_NAME = "updateForReceiverValidateResponse";
		final HashMap responseMap = new HashMap<String, String>();

		final HashMap map = BTSLUtil.getStringToHashNew(receiverValResponse, "&", "=");

		final String status = (String) map.get("TRANSACTION_STATUS");

		// If we get the MSISDN not found on interface error then perform
		// interface routing
		if (InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status)) {
			responseMap.put("STATUS", InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND);
		} else if (!InterfaceErrorCodesI.INTERFACE_MSISDN_NOT_FOUND.equals(status) && !InterfaceErrorCodesI.SUCCESS.equals(status)) {
			responseMap.put("STATUS", InterfaceErrorCodesI.INVALID_RESPONSE);
		} else if (InterfaceErrorCodesI.SUCCESS.equals(status)) {
			responseMap.put("STATUS", InterfaceErrorCodesI.SUCCESS);
			try {

				if (map!=null && map.get("CHANNELUSER_CITY")!=null) {
					responseMap.put("CHANNELUSER_CITY", map.get("CHANNELUSER_CITY"));
				} else {
					responseMap.put("CHANNELUSER_CITY", "");
				}
				if (map!=null && map.get("CHANNELUSER_ADDRESS")!=null) {
					responseMap.put((String) "CHANNELUSER_ADDRESS", map.get("CHANNELUSER_ADDRESS"));
				} else {
					responseMap.put("CHANNELUSER_ADDRESS", "");
				}
				if (map!=null && map.get("CHANNELUSER_NAME")!=null) {
					responseMap.put("CHANNELUSER_NAME", map.get("CHANNELUSER_NAME"));
				} else {
					responseMap.put("CHANNELUSER_NAME", "");
				}
				if (map!=null && map.get("CHANNELUSER_TELEPHONE")!=null) {
					responseMap.put("CHANNELUSER_TELEPHONE", map.get("CHANNELUSER_TELEPHONE"));
				} else {
					responseMap.put("CHANNELUSER_TELEPHONE", "");
				}
				if (map!=null && map.get("CHANNELUSER_EMAIL")!=null) {
					responseMap.put("CHANNELUSER_EMAIL", map.get("CHANNELUSER_EMAIL"));
				} else {
					responseMap.put("CHANNELUSER_EMAIL", "");
				}
				if (map!=null && map.get("CHANNELUSER_STATE")!=null) {
					responseMap.put("CHANNELUSER_STATE", map.get("CHANNELUSER_STATE"));
				} else {
					responseMap.put("CHANNELUSER_STATE", "");
				}
				if (map!=null && map.get("CHANNELUSER_COUNTRY")!=null) {
					responseMap.put("CHANNELUSER_COUNTRY", map.get("CHANNELUSER_COUNTRY"));
				} else {
					responseMap.put("CHANNELUSER_COUNTRY", "");
				}
				if (map!=null && map.get("CHANNELUSER_EMP_CODE")!=null) {
					responseMap.put("CHANNELUSER_EMP_CODE", map.get("CHANNELUSER_EMP_CODE"));
				} else {
					responseMap.put("CHANNELUSER_EMP_CODE", "");
				}
			} catch (Exception e) {
				_log.errorTrace(METHOD_NAME, e);
				_log.error("updateForReceiverValidateResponse", "Exception e:" + e.getMessage());
				responseMap.put("STATUS", InterfaceErrorCodesI.INVALID_RESPONSE);
			}

		}

		return responseMap;
	}
}

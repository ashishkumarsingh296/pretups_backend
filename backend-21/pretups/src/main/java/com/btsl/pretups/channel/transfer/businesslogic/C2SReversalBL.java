package com.btsl.pretups.channel.transfer.businesslogic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.loadcontroller.InstanceLoadVO;
import com.btsl.loadcontroller.LoadControllerCache;
import com.btsl.loadcontroller.NetworkLoadVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayCache;
import com.btsl.pretups.gateway.businesslogic.MessageGatewayVO;
import com.btsl.pretups.master.businesslogic.LocaleMasterDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.privaterecharge.businesslogic.PrivateRchrgDAO;
import com.btsl.pretups.privaterecharge.businesslogic.PrivateRchrgVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordCacheVO;
import com.btsl.pretups.servicekeyword.businesslogic.ServiceKeywordDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.channel.transfer.businesslogic.C2STransferWebDAO;
import com.web.pretups.channel.transfer.web.C2SReversalModel;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

/**
 * @author jashobanta.mahapatra
 * 
 * This class contains all the c2s reversal business logic
 *
 */
public class C2SReversalBL {

	public static final Log _log = LogFactory.getLog(C2SReversalBL.class.getName());
	public static OperatorUtilI _operatorUtil = null;

	static {
        final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
        try {
            _operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
        } catch (Exception e) {
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SReversalBL", "", "", "",
                "Exception while loading the class at the call:" + e.getMessage());
        }
    }
	/**
	 * @param loginId
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * 
	 * This method retuns required c2s reversal inputs to be captured
	 */
	public C2SReversalModel c2sReversal(String loginId) throws BTSLBaseException, SQLException{

		final String methodName = "C2SReversalBL : c2sReversal";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered");
		}
		C2SReversalModel c2sReversalModel = new C2SReversalModel();
		Connection con = null;MComConnectionI mcomCon = null;
		try {
			mcomCon = new MComConnection();con=mcomCon.getConnection();
			final UserDAO userDAO = new UserDAO();
			final ChannelUserVO channelUserVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			final List assignedserviceList = channelUserVO.getAssociatedServiceTypeList();
			if(!(PretupsI.BCU_USER.equalsIgnoreCase(channelUserVO.getCategoryCode()) || PretupsI.CUSTOMER_CARE.equalsIgnoreCase(channelUserVO.getCategoryCode()) || TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(channelUserVO.getCategoryCode()) || TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(channelUserVO.getCategoryCode())))
			{
				if (assignedserviceList == null || assignedserviceList.isEmpty()) {
					throw new BTSLBaseException(this, methodName, "c2stranfer.c2srecharge.error.noserviceassign",Integer.parseInt(PretupsErrorCodesI.C2S_REVERSAL_NO_SERVICE_ASSIGNED),"");
				}
			}
			if (!BTSLUtil.isNullString(channelUserVO.getActiveUserMsisdn()) && !PretupsI.NOT_AVAILABLE.equals(channelUserVO.getActiveUserMsisdn()) && !channelUserVO
					.getMsisdn().equals(channelUserVO.getActiveUserMsisdn())) {
				c2sReversalModel.setDispalyMsisdn(channelUserVO.getActiveUserMsisdn());
			} else {
				c2sReversalModel.setDispalyMsisdn(channelUserVO.getMsisdn());
			}
			if (!PretupsI.YES.equals(channelUserVO.getPinRequired())) {
				c2sReversalModel.setPinRequired(PretupsI.NO);
				c2sReversalModel.setPin(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_SMSPIN)));
			}

			final List<ServiceKeywordCacheVO> serviceKeywordList = new ArrayList<ServiceKeywordCacheVO>();
			List serviceTypeList = null;

			final List serviceKeywordCacheList = new ServiceKeywordDAO().loadServiceCache(con);
			Iterator iterator = serviceKeywordCacheList.iterator();
			String key = null;

			serviceTypeList = new ArrayList();

			int i = 0;
			ServiceKeywordCacheVO serviceKeywordCacheVO2 = null;
			while (iterator.hasNext()) {
				serviceKeywordCacheVO2 = (ServiceKeywordCacheVO) iterator.next();
				if (PretupsI.C2S_MODULE.equals(serviceKeywordCacheVO2.getModule()) && PretupsI.YES.equals(serviceKeywordCacheVO2.getExternalInterface()) && PretupsI.GATEWAY_TYPE_WEB
						.equals(serviceKeywordCacheVO2.getRequestInterfaceType())) {
					serviceKeywordList.add(serviceKeywordCacheVO2);
				}
			}

			iterator = serviceKeywordList.iterator();
			final int assignServiceSize = assignedserviceList.size();
			ListValueVO listVO = null;
			while (iterator.hasNext()) {
				serviceKeywordCacheVO2 = (ServiceKeywordCacheVO) iterator.next();
				if(!(PretupsI.BCU_USER.equalsIgnoreCase(channelUserVO.getCategoryCode()) || PretupsI.CUSTOMER_CARE.equalsIgnoreCase(channelUserVO.getCategoryCode()) || TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(channelUserVO.getCategoryCode()) || TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(channelUserVO.getCategoryCode())))
				{
					for (i = 0; i < assignServiceSize; i++) {

						key = serviceKeywordCacheVO2.getServiceType() + ":" + serviceKeywordCacheVO2.getUseInterfaceLanguage() + ":" + serviceKeywordCacheVO2.getType();

						if (key.split(":")[0].equalsIgnoreCase(((ListValueVO) assignedserviceList.get(i)).getValue())) {
							listVO = new ListValueVO(serviceKeywordCacheVO2.getName(), key);

							// listVO.setOtherInfo(((String)serviceTypeMap.get(key)).split(":")[1]);
							if ((listVO.getValue().split(":")[0]).equals(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL)) {
								serviceTypeList.add(listVO);
								// serviceTypeList.add(listVO);
							}
						}
					}
				}
				else
				{
					key = serviceKeywordCacheVO2.getServiceType() + ":" + serviceKeywordCacheVO2.getUseInterfaceLanguage() + ":" + serviceKeywordCacheVO2.getType();
					listVO = new ListValueVO(serviceKeywordCacheVO2.getName(), key);
					if ((listVO.getValue().split(":")[0]).equals(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL)) 
						serviceTypeList.add(listVO);
				}
			}
			c2sReversalModel.setServiceKeywordList(serviceKeywordList);

			c2sReversalModel.setServiceTypeList(serviceTypeList);
			c2sReversalModel.setModuleTypeList(LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_TYPE_REVERSE_LOOKUP_TYPE, true));
			final String showbalance = new ChannelUserWebDAO().loadChannelUserBalanceServiceWise(channelUserVO.getUserID());
			c2sReversalModel.setShowBalance(showbalance);
			String[] serviceBal = null;

			if(serviceTypeList!=null && !serviceTypeList.isEmpty() )
				c2sReversalModel.setServiceType(((ListValueVO) serviceTypeList.get(0)).getValue());
			else 
				throw new BTSLBaseException(this, methodName, "c2stranfer.c2sreversal.error.serviceinvalid",Integer.parseInt(PretupsErrorCodesI.C2S_REVERSAL_SERVICE_INVALID),"");	

			final String service = c2sReversalModel.getServiceType().split(":")[0];
			if (!BTSLUtil.isNullString(showbalance)) {

				serviceBal = showbalance.split(",");
				for (int a = 0; a < serviceBal.length; a++) {
					final String[] serviceType = serviceBal[a].split(":");
					if (serviceType[0].equals(service)) {
						c2sReversalModel.setCurrentBalance(BTSLUtil.parseStringToLong(serviceType[1]));
						c2sReversalModel.setDisplayTransferMRP(Double.toString(Double.parseDouble(serviceType[1])));
						break;
					}

				}
			} else {
				c2sReversalModel.setCurrentBalance(new Long(0));
			}
		}  finally {
			if (mcomCon != null) {
				mcomCon.close("C2SReversalBL#c2sReversal");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.error(methodName, "Exiting");
			}
		}
		return c2sReversalModel;
	}

	/**
	 * @param c2sreversalform
	 * @param loginId
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * 
	 * This method returns list of reversal transaction based on some c2s data
	 */
	public C2SReversalModel confirmC2SReversal(C2SReversalModel c2sreversalform, String loginId) throws BTSLBaseException, SQLException{
		Connection con = null;MComConnectionI mcomCon = null;
		final String methodName = "C2SReversalBL: confirmC2SReversal";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered: loginId="+ BTSLUtil.maskParam(loginId));
		}

		try {
			final UserDAO userDAO = new UserDAO();
			final C2STransferWebDAO c2STransferwebDAO = new C2STransferWebDAO();
			final ChannelTransferVO channeltransferVO = new ChannelTransferVO();
			mcomCon = new MComConnection();con=mcomCon.getConnection();
			final ChannelUserVO channelUserVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			if (!(PretupsI.BCU_USER.equalsIgnoreCase(channelUserVO.getCategoryCode()) || PretupsI.CUSTOMER_CARE.equalsIgnoreCase(channelUserVO.getCategoryCode()) || TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(channelUserVO.getCategoryCode())|| TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(channelUserVO.getCategoryCode()))) {
				if (!channelUserVO.getSmsPin().equals(BTSLUtil.encryptText(c2sreversalform.getPin()))) {
					throw new BTSLBaseException(this, methodName, "c2stranfer.c2sreversal.error.invalidpin",Integer.parseInt(PretupsErrorCodesI.C2S_ERROR_INVALID_PIN), "c2sreversal");
				}
			}

			String senderMsisdn = null;
			if (PretupsI.BCU_USER.equalsIgnoreCase(channelUserVO.getCategoryCode()) || PretupsI.CUSTOMER_CARE.equalsIgnoreCase(channelUserVO.getCategoryCode()) || TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(channelUserVO.getCategoryCode()) || TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(channelUserVO.getCategoryCode())) {
				senderMsisdn = c2sreversalform.getSenderMsisdn();
			} else {
				senderMsisdn = channelUserVO.getMsisdn();
			}
			
			boolean flag=false;
            PrivateRchrgVO prvo = null;
            if ((prvo = _operatorUtil.getPrivateRechargeDetails(con,c2sreversalform.getSubscriberMsisdn() )) != null) {
                channeltransferVO.setToUserMsisdn(prvo.getMsisdn());
                channeltransferVO.setSubSid(c2sreversalform.getSubscriberMsisdn());
                c2sreversalform.setSubSid(c2sreversalform.getSubscriberMsisdn());
                c2sreversalform.setTempSubMsisdn(prvo.getMsisdn());
                flag=true;
            }
            else{
            	channeltransferVO.setToUserMsisdn(c2sreversalform.getSubscriberMsisdn());
            }
			channeltransferVO.setTransferID(c2sreversalform.getTxID());

			List<ChannelTransferVO> al = new ArrayList<ChannelTransferVO>();
			al = c2STransferwebDAO.getReversalTransactions(con, channeltransferVO, senderMsisdn, channelUserVO.getCategoryCode());
			c2sreversalform.setUserRevlist(al);

		} finally {

			if (mcomCon != null) {
				mcomCon.close("C2SReversalBL#confirmC2SReversal");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exit");
			}
		}
		return c2sreversalform;

	}

	/**
	 * @param thisForm
	 * @param loginId
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws IOException
	 * @throws Exception
	 * 
	 * This method does reverse of a recharge based on recharge txn data
	 */
	public C2SReversalModel reverse(C2SReversalModel thisForm, String loginId) throws BTSLBaseException , SQLException, IOException , Exception {

		String msisdnPrefix = null;
		InstanceLoadVO instanceLoadVO = null;
		ChannelTransferVO channeltransferVO = null;
		ChannelUserVO channelUserVO = null;
		HttpURLConnection _con = null;
		String httpURLPrefix = "http://";
		Map _map = null;
		BufferedReader in = null;
		String urlToSend = null;
		Connection con = null;MComConnectionI mcomCon = null;
		Map map = null;
		String txn_id = null;
		String _subscriberMsisdn = null;
		final String methodName = "C2SReversalBL: reverse";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered");
		}

		try {         
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			final UserDAO userDAO = new UserDAO();
			//final int index = Integer.parseInt(thisForm.getSelectIndex());
			//_subscriberMsisdn = thisForm.getUserRevlist().get(index).getToUserMsisdn();

			final C2STransferWebDAO c2sTransferWebDAO = new C2STransferWebDAO();
			channeltransferVO = c2sTransferWebDAO.loadChannelTransferVOByTransferId(con, thisForm.getTxID());
			_subscriberMsisdn = channeltransferVO.getToUserMsisdn();
			
			final MessageGatewayVO messageGatewayVO = MessageGatewayCache.getObject(PretupsI.GATEWAY_TYPE_WEB);
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "messageGatewayVO: " + messageGatewayVO);
			}
			if (messageGatewayVO == null) {
				_log.error(methodName, "**************Message Gateway not found in cache**************");
				throw new BTSLBaseException(this, methodName, "c2stranfer.c2srecharge.error.sessiondatanotfound",
						Integer.parseInt(PretupsErrorCodesI.C2S_REVERSAL_SESSION_DATA_NOT_FOUND) , "c2sreversal");
			}
			if (!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getStatus())) {
				throw new BTSLBaseException(this, methodName, "c2stranfer.c2srecharge.error.messagegatewaynotactive",
						Integer.parseInt(PretupsErrorCodesI.C2S_REVERSAL_MESSAGE_GATEAWAY_NOT_ACTIVE) ,"c2sreversal");
			} else if (!PretupsI.STATUS_ACTIVE.equals(messageGatewayVO.getRequestGatewayVO().getStatus())) {
				throw new BTSLBaseException(this, methodName, "c2stranfer.c2srecharge.error.reqmessagegatewaynotactive",
						Integer.parseInt(PretupsErrorCodesI.C2S_REVERSAL_REQ_MESSAGE_GATEAWAY_NOT_ACTIVE) ,"c2sreversal");
			}
			String separator = null;
			if (!BTSLUtil.isNullString((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR))) {
				separator = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
			} else {
				separator = " ";
			}

			String msgGWPass = null;
			if (messageGatewayVO.getReqpasswordtype().equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
				msgGWPass = BTSLUtil.decryptText(messageGatewayVO.getRequestGatewayVO().getPassword());
			} else {
				msgGWPass = messageGatewayVO.getRequestGatewayVO().getPassword();
			}
			String filteredMsisdn = null;
			filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(_subscriberMsisdn);
			msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);

			final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
			String[] strArr = null;

			if (networkPrefixVO == null) {
				strArr = new String[] { _subscriberMsisdn };
				throw new BTSLBaseException("C2SReversalBL", methodName, "c2s.reversal.receiver.failed.network.not.found",
						Integer.parseInt(PretupsErrorCodesI.CHNL_ERROR_RECR_NOTFOUND_RECEIVERNETWORK), strArr, "");
				
			}
			final String networkCode = networkPrefixVO.getNetworkCode();
			String smsInstanceID = null;

			if (LoadControllerCache.getNetworkLoadHash() != null && LoadControllerCache.getNetworkLoadHash().containsKey(
					LoadControllerCache.getInstanceID() + "_" + networkCode)) {
				smsInstanceID = ((NetworkLoadVO) (LoadControllerCache.getNetworkLoadHash().get(LoadControllerCache.getInstanceID() + "_" + networkCode)))
						.getC2sInstanceID();
			} else {
				_log.error("C2SReversalBL", " Not able to get the instance ID for the network=" + networkCode + " where the request for recharge needs to be send");
				throw new BTSLBaseException(this, methodName, "c2stranfer.c2srecharge.error.unsuccess", Integer.parseInt(PretupsErrorCodesI.FAILED_TO_LOAD_INSTANCE_ID),"c2sreversal");
			}
			instanceLoadVO = LoadControllerCache.getInstanceLoadForNetworkHash(smsInstanceID + "_" + networkCode + "_" + PretupsI.REQUEST_SOURCE_TYPE_SMS);
			if (instanceLoadVO == null) {
				instanceLoadVO = LoadControllerCache.getInstanceLoadForNetworkHash(smsInstanceID + "_" + networkCode + "_" + PretupsI.REQUEST_SOURCE_TYPE_WEB);
			}
			if (instanceLoadVO == null) {
				instanceLoadVO = LoadControllerCache.getInstanceLoadForNetworkHash(smsInstanceID + "_" + networkCode + "_" + PretupsI.REQUEST_SOURCE_TYPE_DUMMY);
			}

			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.HTTPS_ENABLE)).booleanValue()) {
				httpURLPrefix = "https://";
			}

			//  final Locale senderLanguage = (Locale) request.getSession().getAttribute(Globals.LOCALE_KEY);
			final Locale senderLanguage = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
		
			final LocaleMasterDAO localeMasterDAO = new LocaleMasterDAO();
			final String senderlanguageCode = localeMasterDAO.loadLocaleMasterCode(con, senderLanguage.getLanguage(), senderLanguage.getCountry());
			final String receiverLanguage = localeMasterDAO.loadLocaleMasterCode(con, (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
	
			channelUserVO = userDAO.loadAllUserDetailsByLoginID(con, loginId);
			
			if (!(PretupsI.BCU_USER.equalsIgnoreCase(channelUserVO.getCategoryCode()) || PretupsI.CUSTOMER_CARE.equalsIgnoreCase(channelUserVO.getCategoryCode()) || TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(channelUserVO.getCategoryCode()) || TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(channelUserVO.getCategoryCode()))) {
				thisForm.setSenderMsisdn(channelUserVO.getMsisdn());
			}
		//	channeltransferVO = thisForm.getUserRevlist().get(Integer.parseInt(thisForm.getSelectIndex()));

			urlToSend = httpURLPrefix + instanceLoadVO.getHostAddress() + ":" + instanceLoadVO.getHostPort() + Constants.getProperty("CHANNEL_WEB_RECHARGE_SERVLET") + "?MSISDN=";
			if (!(PretupsI.BCU_USER.equalsIgnoreCase(channelUserVO.getCategoryCode()) || PretupsI.CUSTOMER_CARE.equalsIgnoreCase(channelUserVO.getCategoryCode()) || TypesI.SUPER_CHANNEL_ADMIN.equalsIgnoreCase(channelUserVO.getCategoryCode()) || TypesI.SUPER_CUSTOMER_CARE.equalsIgnoreCase(channelUserVO.getCategoryCode()))) {
				urlToSend = urlToSend + channeltransferVO.getUserMsisdn() + "&MESSAGE=" + URLEncoder
						.encode(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL + separator + _subscriberMsisdn + separator + thisForm.getTxID() + separator + senderlanguageCode + separator + receiverLanguage + separator + thisForm
								.getPin());
			} else {
				urlToSend = urlToSend + channeltransferVO.getUserMsisdn() + "&MESSAGE=" + URLEncoder
						.encode(PretupsI.SERVICE_TYPE_C2S_PREPAID_REVERSAL + separator + _subscriberMsisdn + separator + thisForm.getTxID() + separator + senderlanguageCode + separator + receiverLanguage + separator + channelUserVO
								.getCategoryCode()+ separator + channelUserVO.getUserID());
			}
			urlToSend = urlToSend + "&REQUEST_GATEWAY_CODE=" + messageGatewayVO.getGatewayCode() + "&REQUEST_GATEWAY_TYPE=" + messageGatewayVO.getGatewayType();
			urlToSend = urlToSend + "&SERVICE_PORT=" + messageGatewayVO.getRequestGatewayVO().getServicePort() + "&LOGIN=" + messageGatewayVO.getRequestGatewayVO()
					.getLoginID();
			urlToSend = urlToSend + "&PASSWORD=" + msgGWPass + "&SOURCE_TYPE=" + PretupsI.GATEWAY_TYPE_WEB + "&ACTIVE_USER_ID=" + channelUserVO.getActiveUserID();
			URL url = null;
			url = new URL(urlToSend);
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "URL: =" + url);
			}

			try {
				if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.HTTPS_ENABLE)).booleanValue()) {
					_con = BTSLUtil.getConnection(url);
				} else {
					_con = (HttpURLConnection) url.openConnection();
				}
				_con.setDoInput(true);
				_con.setDoOutput(true);
				_con.setRequestMethod("GET");
				in = new BufferedReader(new InputStreamReader(_con.getInputStream()));
			} catch (Exception e) {
				_log.error(methodName, e.getMessage());
				_log.errorTrace(methodName, e);
				final String arr[] = new String[2];
				arr[0] = instanceLoadVO.getHostAddress();
				arr[1] = instanceLoadVO.getHostPort();
				EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "C2SReversalBL[reverse]", "", "", "",
						"Exception:" + e.getMessage());
				throw new BTSLBaseException(this, methodName, "c2stranfer.c2srecharge.error.connectionfailed", 
						Integer.parseInt(PretupsErrorCodesI.C2S_REVERSAL_CONNECTION_FAILED), arr, "confirmC2SReversal");
			}
			String responseStr = null;
			String finalResponse = "";
			while ((responseStr = in.readLine()) != null) {
				finalResponse = finalResponse + responseStr;
			}

			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Response: =" + finalResponse);
			}

			if (!BTSLUtil.isNullString(finalResponse)) {

				_map = BTSLUtil.getStringToHash(finalResponse, "&", "=");
				finalResponse = URLDecoder.decode((String) _map.get("MESSAGE"), "UTF16");
				map = BTSLUtil.getStringToHash(finalResponse, "&", "=");

				txn_id = (String) _map.get("TXN_ID");
				final String txn_status = (String) _map.get("TXN_STATUS");
				thisForm.setTxnid(txn_id);
				if (_log.isDebugEnabled()) {
					_log.debug(methodName, "message=" + finalResponse + ",TXN_ID=" + txn_id + ",TXN_STATUS=" + txn_status);
				}

				if (!BTSLUtil.isNullString(finalResponse) && finalResponse.indexOf("mclass^") > -1 && finalResponse.indexOf(":") > -1) {
					finalResponse = finalResponse.substring(finalResponse.indexOf(":") + 1);
				}
				final String[] arr = new String[1];
				arr[0] = URLDecoder.decode(finalResponse, "UTF16");
				thisForm.setC2sReverseResponseMessage(arr[0]);
				// BTSLMessages btslMessage = null;
				if (PretupsI.TXN_STATUS_SUCCESS.equals(txn_status)) {
					//  forward = notification(mapping, form, request, response);
					// final String path = forward.getName();
					// btslMessage = new BTSLMessages("btsl.blank.message", arr, path);

					final C2STransferDAO c2STransferDAO = new C2STransferDAO();
					final C2STransferVO transferVO = c2STransferDAO.loadC2STransferDetails(con, thisForm.getTxID() );
					boolean flag=false;
					PrivateRchrgVO prvo = null;
					PrivateRchrgDAO prdao= new PrivateRchrgDAO();
		            if ((prvo = prdao.loadSubscriberSIDDetails(con,_subscriberMsisdn )) != null) {
		            	if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SID_ENCRYPTION_ALLOWED))).booleanValue())
		            	{ 
		            		transferVO.setReceiverMsisdn(BTSLUtil.decrypt3DesAesText(prvo.getUserSID()));
		            	}else{
		            		transferVO.setReceiverMsisdn(prvo.getUserSID());
		            	}
		                flag=true;
		            }
					thisForm.setTransferVO(transferVO);

					thisForm.setC2sReverseResponseStatus(true);
				} else {
					//btslMessage = new BTSLMessages("btsl.blank.message", arr, "c2sreversal");
					thisForm.setC2sReverseResponseStatus(false);
				}
				//  forward = super.handleMessage(btslMessage, request, mapping);
			} else {
				throw new BTSLBaseException(this,methodName, "c2stranfer.c2srecharge.error.unsuccess", Integer.parseInt(PretupsErrorCodesI.C2S_RECEIVER_FAIL_KEY), "c2sreversal");
			}


		} finally {
        	try {
        		if (in != null) {
        			in.close();
        		}
        	} catch (Exception e) {
        		_log.errorTrace(methodName, e);
        	}
			if (mcomCon != null) {
				mcomCon.close("C2SReversalBL#reverse");
				mcomCon = null;
			}
        	try {
        		if (_con != null) {
        			_con.disconnect();
        		}
        	} catch (Exception e) {
        		_log.errorTrace(methodName, e);
        	}
        	if (_log.isDebugEnabled()) {
        		_log.debug(methodName, "Exiting : " );
        	}
        }
		return thisForm;
	}

	/**
	 * @param c2sReversalModel
	 * @param loginId
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * 
	 * This method is to check the status of reversal by transfer id
	 */
	public C2SReversalModel reverseStatus(C2SReversalModel c2sReversalModel,
			String loginId) throws BTSLBaseException, SQLException {
		final String methodName = "C2SReversalBL:reverseStatus";
		Connection con = null;MComConnectionI mcomCon = null;
		try{
			mcomCon = new MComConnection();con=mcomCon.getConnection();
		final C2STransferDAO c2STransferDAO = new C2STransferDAO();
		final C2STransferVO transferVO = c2STransferDAO.loadC2STransferDetails(con, c2sReversalModel.getTxID());
		c2sReversalModel.setTransferVO(transferVO);
		}
		 finally {
	        	
			if (mcomCon != null) {
				mcomCon.close("C2SReversalBL#reverseStatus");
				mcomCon = null;
			}
		 }
		return c2sReversalModel;
	}

}

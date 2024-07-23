package com.btsl.pretups.channel.transfer.requesthandler;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import org.spring.custom.action.action.ActionErrors;
import org.spring.custom.action.action.ActionMessage;
import org.spring.custom.action.action.ActionMessages;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.EMailSender;
import com.btsl.common.IDGenerator;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferBL;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelVoucherItemsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.OneLineTXNLog;
import com.btsl.pretups.master.businesslogic.LocaleMasterCache;
import com.btsl.pretups.master.businesslogic.LocaleMasterVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.CorePreferenceI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.processes.TargetBasedCommissionMessages;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.BarredUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelSoSVO;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.btsl.util.OracleUtil;
import com.btsl.util.XmlTagValueConstant;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.btsl.voms.voucher.businesslogic.VomsBatchVO;
import com.btsl.voms.voucher.businesslogic.VoucherChangeUserId;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.txn.pretups.user.businesslogic.ChannelUserTxnDAO;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferWebDAO;


public class VoucherC2CController implements ServiceKeywordControllerI {

	private static Log log = LogFactory.getLog(VoucherC2CController.class.getName());
	
	//Base64 file upload functionality
	private C2CFileUploadVO c2cFileUploadVO;
	
	public static OperatorUtilI _operatorUtil = null;
	static {
		final String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		try {
			_operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
		} catch (Exception e) {
			log.errorTrace("static", e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					" VoucherC2CController [initialize]", "", "", "",
					"Exception while loading the class at the call:" + e.getMessage());
		}
	}

	public void process(RequestVO p_requestVO) {
 		final String methodName = "process";
		if (log.isDebugEnabled()) {
			log.debug("process", "Entered p_requestVO: " + p_requestVO);
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		final Date curDate = new Date();
		String requestMessage = p_requestVO.getRequestMessage();
		ObjectMapper objectMapper = new ObjectMapper();
					
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			final HashMap requestMap = p_requestVO.getRequestMap();
			final ChannelUserVO senderVO = (ChannelUserVO) p_requestVO.getSenderVO();
    		UserPhoneVO userPhoneVO = null;
    		if (!senderVO.isStaffUser()) {
    			userPhoneVO = senderVO.getUserPhoneVO();
    		} else {
    			userPhoneVO = senderVO.getStaffUserDetails().getUserPhoneVO();
    		}

    		if (log.isDebugEnabled()) {
    			log.debug("process", "Entered Sender VO: " + senderVO);
    		}
    		
    		ChannelUserVO receiverChannelUserVO = null;
    		ChannelUserTxnDAO channelUserTxnDAO = new ChannelUserTxnDAO();
    		boolean isUserDetailLoad = false;
			UserPhoneVO PrimaryPhoneVO_R = null;
			final UserDAO userDAO = new UserDAO();
			UserPhoneVO phoneVO = null;
		    ArrayList<VomsBatchVO> vomsBatchList = new ArrayList<VomsBatchVO>();
		   
			
		    if (requestMessage != null && p_requestVO.getSourceType().equals("SMSC")) {
				String[] voucherDetailsArr = requestMessage.split(" ");
				if(voucherDetailsArr != null && voucherDetailsArr.length >= 8) {
						VomsBatchVO vomsBatchVO = new VomsBatchVO();
						if(!BTSLUtil.isDecimalValue(voucherDetailsArr[2])){
							throw new BTSLBaseException("VoucherC2CController", "process",
									PretupsErrorCodesI.ERROR_INVALID_AMOUNT, 0,
									new String[] { p_requestVO.getActualMessageFormat() }, null);
						}
					   if (!BTSLUtil.isNumeric(voucherDetailsArr[3])){
						   throw new BTSLBaseException("VoucherC2CController", "process",
									PretupsErrorCodesI.FROM_SERIAL_NOT_NUMERIC, 0,
									new String[] { p_requestVO.getActualMessageFormat() }, null);		
						   }
					   
					   if (!BTSLUtil.isNumeric(voucherDetailsArr[4])){
						   throw new BTSLBaseException("VoucherC2CController", "process",
									PretupsErrorCodesI.TO_SERIAL_NOT_NUMERIC, 0,
									new String[] { p_requestVO.getActualMessageFormat() }, null);	
					   }
					   
					   PretupsBL.validateVoucherType(con, voucherDetailsArr[5]);
					   PretupsBL.validateVoucherSegment(voucherDetailsArr[6]);
					   
					   //set values in VO
						vomsBatchVO.setDenomination(voucherDetailsArr[2]);
			            vomsBatchVO.setFromSerialNo(voucherDetailsArr[3]);
			            vomsBatchVO.setToSerialNo(voucherDetailsArr[4]);
			            vomsBatchList.add(vomsBatchVO);
			            p_requestVO.setVoucherType(String.valueOf(voucherDetailsArr[5]));
			 			p_requestVO.setVoucherSegment(String.valueOf(voucherDetailsArr[6]));
			 			
			 			 switch (voucherDetailsArr.length) {
			 			  case 8:
		                	{
		                		if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
		                            try {
		                                ChannelUserBL.validatePIN(con, senderVO, voucherDetailsArr[voucherDetailsArr.length-1]);
		                            } catch (BTSLBaseException be) {
		                                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
		                                    .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
		                                    con.commit();
		                                }
		                                throw be;
		                            }
		                        }
		                		break;
		 			   		}
			                case 9:
			                	{
			                		if (userPhoneVO.getPinRequired().equals(PretupsI.YES)) {
			                            try {
			                                ChannelUserBL.validatePIN(con, senderVO, voucherDetailsArr[voucherDetailsArr.length-1]);
			                            } catch (BTSLBaseException be) {
			                                if (be.isKey() && ((be.getMessageKey().equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_INVALID_PIN)) || (be.getMessageKey()
			                                    .equals(PretupsErrorCodesI.CHNL_ERROR_SNDR_PINBLOCK)))) {
			                                    con.commit();
			                                }
			                                throw be;
			                            }
			                        }
			                		p_requestVO.setSenderLocale(new Locale(voucherDetailsArr[7], (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))));
			                		break;
			 			   		}
			                default:
			                	throw new BTSLBaseException("VoucherC2CController", "process",PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT_C2C, 0,
										new String[] { p_requestVO.getActualMessageFormat() }, null);
		 			   }
			 			 
			 			
			 			String [] messagearr =  {voucherDetailsArr[0],voucherDetailsArr[1],voucherDetailsArr[voucherDetailsArr.length-1]};
			 			p_requestVO.setRequestMessageArray(messagearr);
					}
				else {
						throw new BTSLBaseException("VoucherC2CController", "process",
								PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT_C2C, 0,
								new String[] { p_requestVO.getActualMessageFormat() }, null);
					}
			}
		    else{
		    	if(p_requestVO.getSourceType().equals("REST")){
		    	if(BTSLUtil.isNullString(p_requestVO.getReceiverMsisdn()) && BTSLUtil.isNullString(p_requestVO.getReceiverExtCode()) && BTSLUtil.isNullString(p_requestVO.getReceiverLoginID())){
					throw new BTSLBaseException("VoucherC2CInitiateController", "process",
							PretupsErrorCodesI.INVALID_RECIEVER_CREDENTIALS, 0, null);
				}
		    	}
				if(requestMap != null && !BTSLUtil.isNullObject(requestMap.get("VOUCHERDETAILS"))) {
					String voucherDetails = requestMap.get("VOUCHERDETAILS").toString();
					  String[] voucherDetailsArr = voucherDetails.split(",");
				       for (String voucherDetailsArrObj : voucherDetailsArr) {
				           VomsBatchVO vomsBatchVO = new VomsBatchVO();
			               if (voucherDetailsArrObj != null && voucherDetailsArrObj.trim().length() > 0) {
			            	   int index = voucherDetailsArrObj.indexOf(":");
			            	   int lastIndex = voucherDetailsArrObj.lastIndexOf(":");
			            	   if(index != lastIndex){
				                    vomsBatchVO.setDenomination(voucherDetailsArrObj.substring(0, index));
				                    vomsBatchVO.setFromSerialNo(voucherDetailsArrObj.substring(index+1, lastIndex));
				                    vomsBatchVO.setToSerialNo(voucherDetailsArrObj.substring(lastIndex+1, voucherDetailsArrObj.length()));
				                    vomsBatchList.add(vomsBatchVO);
			            	    }
				             }
				        }
					} else if(requestMap != null && p_requestVO.getSourceType().equals("PLAIN")) { //MOBILE APP 
						VomsBatchVO vomsBatchVO = new VomsBatchVO();
						  vomsBatchVO.setDenomination((String) requestMap.get("DENOMINATION"));
		                    vomsBatchVO.setFromSerialNo((String) requestMap.get("FROMSERIALNO"));
		                    vomsBatchVO.setToSerialNo((String) requestMap.get("TOSERIALNO"));
		                    vomsBatchList.add(vomsBatchVO);
					
				}
				else {	
					JsonNode requestNode = (JsonNode) PretupsRestUtil.convertJSONToObject(requestMessage, new TypeReference<JsonNode>() {});
			        JsonNode vomsBatchListNode =  requestNode.get("voucherDetails");
		            vomsBatchList = new ArrayList<>(Arrays.asList(objectMapper.readValue(vomsBatchListNode.toString(), VomsBatchVO[].class)));
		            
		            
		            //Base64 File upload functionality
		            if(!BTSLUtil.isNullorEmpty(requestNode.get("fileAttachment")) && !BTSLUtil.isNullorEmpty(requestNode.get("fileName"))
		            		&& !BTSLUtil.isNullorEmpty(requestNode.get("fileType")) && !BTSLUtil.isNullorEmpty(requestNode.get("fileUploaded")) ) {
		            	
		            	    c2cFileUploadVO = new C2CFileUploadVO();
		            	    c2cFileUploadVO.setFileAttachment(requestNode.get("fileAttachment").textValue());
				            c2cFileUploadVO.setFileName(requestNode.get("fileName").textValue());
				            c2cFileUploadVO.setFileType(requestNode.get("fileType").textValue());
				            c2cFileUploadVO.setFileUploaded(requestNode.get("fileUploaded").textValue());
		            }
		           
		            
				}
		    }
			
		    
			if (!BTSLUtil.isNullString(p_requestVO.getReceiverExtCode())) {
				receiverChannelUserVO = channelUserTxnDAO.loadChannelUserDetailsForTransferIfReqExtgw(con,
						p_requestVO.getReceiverExtCode(), null, curDate);
				if (receiverChannelUserVO == null) {
					if(!p_requestVO.getSourceType().equals("WEB")){
					throw new BTSLBaseException("VoucherC2CController", "process",
							PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE, 0, null);} else {
								throw new BTSLBaseException("VoucherC2CController", "process",
										"c2c.vouchers.invalidextcode.error", 0, null);
							}
				}
				isUserDetailLoad = true;
			} else if (!BTSLUtil.isNullString(p_requestVO.getReceiverLoginID())) {
				receiverChannelUserVO = channelUserTxnDAO.loadChannelUserDetailsForTransferIfReqExtgw(con, null,
						p_requestVO.getReceiverLoginID(), curDate);
				if (receiverChannelUserVO == null) {
					if(!p_requestVO.getSourceType().equals("WEB")){
					throw new BTSLBaseException("VoucherC2CController", "process",
							PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID, 0, null);} else {
								
								throw new BTSLBaseException("VoucherC2CController", "process",
										"c2c.vouchers.invalidloginid.error", 0, null);
							}
				}
				isUserDetailLoad = true;
			}
			
			if (!(receiverChannelUserVO == null) && isUserDetailLoad) {
				if (!BTSLUtil.isNullString(p_requestVO.getReceiverExtCode())) {
					if (!p_requestVO.getReceiverExtCode().equalsIgnoreCase(receiverChannelUserVO.getExternalCode())) {
						if(!p_requestVO.getSourceType().equals("WEB")){
						throw new BTSLBaseException("VoucherC2CController", "process",
								PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE, 0, null); } else {throw new BTSLBaseException("VoucherC2CController", "process",
										"c2c.vouchers.invalidextcode.error", 0, null);}
					}
				}
				if (!BTSLUtil.isNullString(p_requestVO.getReceiverLoginID())) {
					if (!p_requestVO.getReceiverLoginID().equalsIgnoreCase(receiverChannelUserVO.getLoginID())) {
						if(!p_requestVO.getSourceType().equals("WEB")){
						throw new BTSLBaseException("VoucherC2CController", "process",
								PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID, 0, null);} else{throw new BTSLBaseException("VoucherC2CController", "process",
										"c2c.vouchers.invalidloginid.error", 0, null);}
					}
				}
				if (!BTSLUtil.isNullString(p_requestVO.getReceiverMsisdn())) {
					if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
						phoneVO = userDAO.loadUserAnyPhoneVO(con, p_requestVO.getReceiverMsisdn());
						if (phoneVO != null && ((receiverChannelUserVO.getUserID()).equals(phoneVO.getUserId()))) {
							if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue()
									&& ("N".equalsIgnoreCase(phoneVO.getPrimaryNumber()))) {
								PrimaryPhoneVO_R = userDAO.loadUserAnyPhoneVO(con, receiverChannelUserVO.getMsisdn());
							}
							receiverChannelUserVO.setPrimaryMsisdn(receiverChannelUserVO.getMsisdn());
							receiverChannelUserVO.setMsisdn(p_requestVO.getReceiverMsisdn());
						} else {
							if(!p_requestVO.getSourceType().equals("WEB")){
							throw new BTSLBaseException("VoucherC2CController", "process",
									PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN, 0, null);} else {
										throw new BTSLBaseException("VoucherC2CController", "process",
												"c2c.vouchers.invalidmsisdn.error", 0, null);
										}
						}
					} else if (!p_requestVO.getReceiverMsisdn().equalsIgnoreCase(receiverChannelUserVO.getMsisdn())) {
						if(!p_requestVO.getSourceType().equals("WEB")){
						throw new BTSLBaseException("VoucherC2CController", "process",
								PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN, 0, null);} else{throw new BTSLBaseException("VoucherC2CController", "process",
										"c2c.vouchers.invalidmsisdn.error", 0, null);}
					}
				}

				// To set the msisdn in the request message array...
				if (BTSLUtil.isNullString(p_requestVO.getReceiverMsisdn())
						&& BTSLUtil.isNullString((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR))) {
					final String message[] = p_requestVO.getRequestMessageArray();
					final String[] newMessageArr = new String[message.length + 1];
					for (int j = 0; j < newMessageArr.length - 1; j++) {
						newMessageArr[j] = message[j];
					}
					for (int i = newMessageArr.length; i > 0; i--) {
						String temp;
						if (i < newMessageArr.length - 1) {
							temp = newMessageArr[i];
							newMessageArr[i + 1] = newMessageArr[i];
							newMessageArr[i] = temp;
						}
					}
					newMessageArr[1] = receiverChannelUserVO.getMsisdn();
					p_requestVO.setRequestMessageArray(newMessageArr);
				} 
				else {
					final String[] mesgArr = p_requestVO.getRequestMessageArray();
					mesgArr[1] = receiverChannelUserVO.getMsisdn();
					p_requestVO.setRequestMessageArray(mesgArr);
				}
			}
			
    		final String messageArr[] = p_requestVO.getRequestMessageArray();
			if (messageArr.length < 2) {
				if(!p_requestVO.getSourceType().equals("WEB")){
				throw new BTSLBaseException("VoucherC2CController", "process",
						PretupsErrorCodesI.ERROR_INVALID_REQUESTFORMAT, 0,
						new String[] { p_requestVO.getActualMessageFormat() }, null);} else {
							throw new BTSLBaseException("VoucherC2CController", "process",
									"c2c.vouchers.invalidmsgformat.error", 0, null);
							
						}
			}
			
			 // validate the channel user
	         BarredUserDAO barredUserDAO = new BarredUserDAO();
	 		// check that the channel user is barred or not
	 		if(barredUserDAO.isExists(con, PretupsI.C2S_MODULE, senderVO.getNetworkID(), senderVO.getMsisdn(), PretupsI.USER_TYPE_SENDER, null)){
	 			if(!p_requestVO.getSourceType().equals("WEB")){
	 			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_SENDER_BAR);
	 			} else {throw new BTSLBaseException("VoucherC2CController", "process",
						"c2c.vouchers.barreduser.error", 0, null);
	 				}
	 		}

	 		// check that the channel user should not be out suspended
	 		if (senderVO.getOutSuspened().equalsIgnoreCase(PretupsI.USER_TRANSFER_OUT_STATUS_SUSPEND)) {
	 			if(!p_requestVO.getSourceType().equals("WEB")){
	 			throw new BTSLBaseException(ChannelTransferBL.class, methodName, PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_OUT_SUSPENDED,new String[]{senderVO.getUserCode()});
	 			} else {
	 				throw new BTSLBaseException("VoucherC2CController", "process",
							"c2c.vouchers.channelsendersuspended.error", 0, null);
	 				
	 			}
	 		}
	 		
			
			final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			if (!BTSLUtil.isNumeric(messageArr[1])) {
				if(!p_requestVO.getSourceType().equals("WEB")){
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_USER_CODE_FORMAT);
				throw new BTSLBaseException("VoucherC2CController", "process",
						PretupsErrorCodesI.ERROR_INVALID_USER_CODE_FORMAT);} else {
							p_requestVO.setMessageCode("c2c.vouchers.invalidusercode.error");
							throw new BTSLBaseException("VoucherC2CController", "process",
									"c2c.vouchers.invalidusercode.error");
						}
			}

			// Validate the receiver
			String receiverUserCode = messageArr[1];
			receiverUserCode = _operatorUtil.addRemoveDigitsFromMSISDN(PretupsBL.getFilteredMSISDN(receiverUserCode));
			if (!BTSLUtil.isValidMSISDN(receiverUserCode)) {
				if(!p_requestVO.getSourceType().equals("WEB")){
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_REC_USERCODE);
				throw new BTSLBaseException("VoucherC2CController", "process",
						PretupsErrorCodesI.ERROR_INVALID_REC_USERCODE);} else {
							p_requestVO.setMessageCode("c2c.vouchers.invalidrecivermsisdn.error");
							throw new BTSLBaseException("VoucherC2CController", "process",
									"c2c.vouchers.invalidrecivermsisdn.error");}
			}
			final String msisdnPrefix = PretupsBL.getMSISDNPrefix(receiverUserCode);
			// Getting network details
			final NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
			if (networkPrefixVO == null) {
				if(!p_requestVO.getSourceType().equals("WEB")){
				throw new BTSLBaseException("VoucherC2CController", "process",
						PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_UNSUPPORTED_NETWORK, 0,
						new String[] { receiverUserCode }, null);} else {throw new BTSLBaseException("VoucherC2CController", "process",
								"c2c.vouchers.userfromunsupportednetwork.error", 0,
								new String[] { receiverUserCode }, null);}
			}

			if (barredUserDAO.isExists(con, PretupsI.C2S_MODULE, networkPrefixVO.getNetworkCode(), receiverUserCode,
					PretupsI.USER_TYPE_RECEIVER, null)) {
				if(!p_requestVO.getSourceType().equals("WEB")){ 
				throw new BTSLBaseException("VoucherC2CController", "process",
						PretupsErrorCodesI.ERROR_USER_TRANSFER_CHANNEL_RECEIVER_BAR, 0,
						new String[] { receiverUserCode }, null);} else { 
							throw new BTSLBaseException("VoucherC2CController", "process",
									"c2c.vouchers.userbarredasreciver.error", 0,
									new String[] { receiverUserCode }, null);}
			}

			if (phoneVO == null) {
				phoneVO = userDAO.loadUserAnyPhoneVO(con, receiverUserCode);
			}
			if (!((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
				receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, receiverUserCode,
						true, curDate, false);
			} else {
				if (phoneVO != null && !("Y".equalsIgnoreCase(phoneVO.getPrimaryNumber()))) {
					receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con,
							phoneVO.getUserId(), false, curDate, false);
					if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue()) {
						PrimaryPhoneVO_R = userDAO.loadUserAnyPhoneVO(con, receiverChannelUserVO.getMsisdn());
					}
					receiverChannelUserVO.setPrimaryMsisdn(receiverChannelUserVO.getMsisdn());
					receiverChannelUserVO.setMsisdn(receiverUserCode);
				} else {
					receiverChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, receiverUserCode,
							true, curDate, false);
				}
			}
		
			ChannelTransferBL.c2cTransferUserValidateReceiver(con, receiverChannelUserVO, p_requestVO, curDate, receiverUserCode);


			if(p_requestVO.getSourceType().equals("SMSC")){
				for(VomsBatchVO vomsBatchVO : vomsBatchList){
					if (!BTSLUtil.isNullString(vomsBatchVO.getToSerialNo()) && !BTSLUtil.isNullString(vomsBatchVO.getFromSerialNo()) && BTSLUtil.isNumeric(vomsBatchVO.getToSerialNo()) && BTSLUtil.isNumeric(vomsBatchVO.getFromSerialNo())) {
	                    if (Long.parseLong(vomsBatchVO.getToSerialNo()) < Long.parseLong(vomsBatchVO.getFromSerialNo())) {
	    					throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.C2C_VOMS_INVALID_SNO);
	                    }
	                    ChannelTransferWebDAO channelTransferWebDAO = new ChannelTransferWebDAO();
	                    long quantity = Long.parseLong(vomsBatchVO.getToSerialNo()) - Long.parseLong(vomsBatchVO.getFromSerialNo()) + 1;
	    				if(!channelTransferWebDAO.areAllVouchersAssociated(con, vomsBatchVO.getFromSerialNo(), vomsBatchVO.getToSerialNo(), quantity, senderVO.getUserID(),vomsBatchVO.getVoucherType(),vomsBatchVO.getVouchersegment(),vomsBatchVO.getDenomination(),p_requestVO.getRequestNetworkCode())){
	    					String[] msg= {vomsBatchVO.getFromSerialNo(), vomsBatchVO.getToSerialNo()}; 
	    					throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.C2C_VOMS_NOT_ASSOCIATED,msg);
	    				}else if(channelTransferWebDAO.doesRangeContainMultipleProfiles(con, vomsBatchVO.getFromSerialNo(), vomsBatchVO.getToSerialNo(), quantity, senderVO.getUserID(),vomsBatchVO.getVoucherType(),vomsBatchVO.getVouchersegment(),vomsBatchVO.getDenomination(),p_requestVO.getRequestNetworkCode())){
	    					String[] msg= {vomsBatchVO.getFromSerialNo(), vomsBatchVO.getToSerialNo()}; 
	    					throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.C2C_VOMS_MULTIPLE_PROFILES,msg);
	    				}else{
	    					vomsBatchVO.setQuantity(String.valueOf(quantity));
	    				}
					}
					
					Date currentDate = new Date();
					vomsBatchVO.setCreatedBy(senderVO.getUserID());
    				vomsBatchVO.set_NetworkCode(senderVO.getNetworkCode());
    				vomsBatchVO.setCreatedDate(currentDate);
    				vomsBatchVO.setModifiedDate(currentDate);
    				vomsBatchVO.setModifiedOn(currentDate);
    				vomsBatchVO.setCreatedOn(currentDate);
    				vomsBatchVO.setToUserID(receiverChannelUserVO.getUserID()); 
    				vomsBatchVO.setVoucherType(vomsBatchVO.getVoucherType());
    				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOUCHER_EN_ON_TRACKING))).booleanValue()) {
    						vomsBatchVO.setBatchType(VOMSI.BATCH_ENABLED);
    					} 
    				else {
    						vomsBatchVO.setBatchType(VOMSI.VOMS_PRE_ACTIVE_STATUS);
    					}
    				ChannelTransferWebDAO channelTransferWebDAO = new ChannelTransferWebDAO();
    				String batch_no = String.valueOf(IDGenerator.getNextID(VOMSI.VOMS_BATCHES_DOC_TYPE, String.valueOf(BTSLUtil.getFinancialYear()), VOMSI.ALL));
    				vomsBatchVO.setBatchNo(new VomsUtil().formatVomsBatchID(vomsBatchVO, batch_no));
    				if (channelTransferWebDAO.validateVoucherSerialNoC2C(con, vomsBatchVO.getFromSerialNo(),"null",PretupsI.TRANSFER_TYPE_C2C)) {
    					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOMS_C2C_FROM_SERIAL_PENDING_APPROVAL);
    				}
    				if (channelTransferWebDAO.validateVoucherSerialNoC2C(con, vomsBatchVO.getToSerialNo(), "null",PretupsI.TRANSFER_TYPE_C2C)) {
    					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOMS_C2C_FROM_SERIAL_PENDING_APPROVAL);
    				}
    				final ArrayList<VomsBatchVO> usedBatches = channelTransferWebDAO.validateBatch(con, vomsBatchVO);
    				if (usedBatches != null && !usedBatches.isEmpty()) {
    					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOMS_C2C_INVALID_BATCH);
    				}
				}
			}
			//validate voucher details
			else
				{
				ActionErrors errors =  this.validateVoucher(con, vomsBatchList, senderVO.getUserID(),receiverChannelUserVO.getUserID(),networkPrefixVO.getNetworkCode(),p_requestVO);
	            if(errors != null && !errors.isEmpty())
	            {
	            	Iterator<ActionMessage> iterator = errors.get();
	            	ActionMessage error = iterator.next();
					throw new BTSLBaseException(this, methodName,error.getKey());
	            }
            
				}
			final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(p_requestVO.getLocale());
			if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
				senderVO.setCommissionProfileSuspendMsg(senderVO.getCommissionProfileLang1Msg());
				receiverChannelUserVO
						.setCommissionProfileSuspendMsg(receiverChannelUserVO.getCommissionProfileLang1Msg());
			} else {
				senderVO.setCommissionProfileSuspendMsg(senderVO.getCommissionProfileLang2Msg());
				receiverChannelUserVO
						.setCommissionProfileSuspendMsg(receiverChannelUserVO.getCommissionProfileLang2Msg());
			}
			
			final boolean isOutsideHierarchy = ChannelTransferBL.validateSenderAndReceiverWithXfrRule(con, senderVO,
					receiverChannelUserVO, true, null, false, PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER);
			final ArrayList<ChannelTransferItemsVO> ChannelTransferItemsList = ChannelTransferBL.validateVomsReqstProdsWithDefinedProdsForXFR(con, senderVO, messageArr, curDate, p_requestVO.getLocale(), receiverChannelUserVO.getCommissionProfileSetID(),vomsBatchList);
			KeyArgumentVO keyArgumentVO = null;
			UserPhoneVO primaryPhoneVO_S = null;
			
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()) {
				if (!(senderVO.getMsisdn()).equalsIgnoreCase(p_requestVO.getFilteredMSISDN())) {
					senderVO.setPrimaryMsisdn(senderVO.getMsisdn());
					senderVO.setMsisdn(p_requestVO.getFilteredMSISDN());
					if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue()) {
						primaryPhoneVO_S = userDAO.loadUserAnyPhoneVO(con, senderVO.getPrimaryMsisdn());
					}
				}
				receiverChannelUserVO.setUserCode(receiverUserCode);
			}
			ChannelTransferVO channelTransferVO = this.prepareTransferProfileVO(requestMap, requestMessage, con, senderVO, receiverChannelUserVO,
					ChannelTransferItemsList, curDate,vomsBatchList,p_requestVO);
	
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL))).booleanValue()){
			channelTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
			channelTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
			channelTransferVO.setToUserMsisdn(receiverChannelUserVO.getMsisdn());
			}
			if (log.isDebugEnabled()) {
				log.debug("process", "Calculate Tax of products Start ");
			}

			ChannelTransferBL.loadAndCalculateTaxOnDenominations(con, receiverChannelUserVO.getCommissionProfileSetID(), receiverChannelUserVO.getCommissionProfileSetVersion(), channelTransferVO, false, null, PretupsI.TRANSFER_TYPE_C2C);
			if (isOutsideHierarchy) {
				channelTransferVO.setControlTransfer(PretupsI.NO);
			} else {
				channelTransferVO.setControlTransfer(PretupsI.YES);
			}
			channelTransferVO.setSource(p_requestVO.getSourceType());
			channelTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
			channelTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
			channelTransferVO.setReferenceNum(p_requestVO.getExternalReferenceNum());
			channelTransferVO.setCellId(p_requestVO.getCellId());
			channelTransferVO.setSwitchId(p_requestVO.getSwitchId());
		      String recLastC2CId="";
	            String recLastC2CAmount="";
	            String recLastC2CSenderMSISDN="";
	            String recLastC2CPostStock="";
	            String recLastC2CProductName="";
	            Date recLastC2CTime=null ;
	            
	            boolean lastInFoFlag=false;
	            if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_C2C_ENQ_MSG_REQ))).booleanValue()){
	                ArrayList transfersList =null;
	    	        
	            try{
	            	int xLastTxn =1;
	            	String serviceType="C2C:V";
	            	int noDays=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LAST_X_TRF_DAYS_NO))).intValue();		//fetch only data for last these days.
	            	ChannelTransferDAO channelTransferDAO=new ChannelTransferDAO();
	            	transfersList=channelTransferDAO.loadLastXTransfersForReceiver(con,receiverChannelUserVO.getUserID(),xLastTxn, serviceType, noDays);
	            	if(transfersList!=null && transfersList.size()>0){
		            	Iterator transfersListIte=transfersList.iterator();
		            	while (transfersListIte.hasNext()) {
		            		C2STransferVO p_c2sTransferVO = (C2STransferVO) transfersListIte.next();
		            		recLastC2CId=p_c2sTransferVO.getTransferID();
		            		recLastC2CAmount=Double.toString(p_c2sTransferVO.getQuantity()/100);
		            		recLastC2CSenderMSISDN=p_c2sTransferVO.getSenderMsisdn();
		            		recLastC2CTime=p_c2sTransferVO.getTransferDate();
		            		recLastC2CProductName=p_c2sTransferVO.getProductName();
		            	}
		            	lastInFoFlag=true;
	            	}
	            }catch (Exception e) {
	            	lastInFoFlag=false;
	            	log.error("process", "Not able to fetch info Exception: "+e.getMessage());
				}
	            }
			if (log.isDebugEnabled()) {
				log.debug("process", "Start Transfer Process ");
			}
			ChannelTransferItemsVO channelTransferItemsVO = new ChannelTransferItemsVO();

			final Boolean isTagReq = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_TRANSFERS_INFO_REQUIRED))).booleanValue();
			if (requestMap != null && isTagReq) {
				final String remarks = (String) requestMap.get("REMARKS");
				channelTransferVO.setChannelRemarks(remarks);
				final String info1 = (String) requestMap.get("INFO1");
				final String info2 = (String) requestMap.get("INFO2");
				channelTransferVO.setInfo1(info1);
				channelTransferVO.setInfo2(info2);
			}
			
			// generate transfer ID for the C2C transfer
            ChannelTransferBL.genrateChnnlToChnnlTrfID(channelTransferVO);
            
            // set the transfer ID in each ChannelTransferItemsVO of productList
            for (int i = 0, j = ChannelTransferItemsList.size(); i < j; i++) {
                channelTransferItemsVO = (ChannelTransferItemsVO) ChannelTransferItemsList.get(i);
                channelTransferItemsVO.setTransferID(channelTransferVO.getTransferID());
                channelTransferItemsVO.setSenderDebitQty(0);
                channelTransferItemsVO.setReceiverCreditQty(0);
                channelTransferItemsVO.setSenderPostStock(0);
                channelTransferItemsVO.setReceiverPostStock(0);
            }

            final int insertCount = approveC2CVoucherTransfer(con, channelTransferVO, isOutsideHierarchy, null,vomsBatchList, p_requestVO);
            if (!senderVO.isStaffUser()) {
				(senderVO.getUserPhoneVO()).setLastTransferID(channelTransferVO.getTransferID());
				(senderVO.getUserPhoneVO()).setLastTransferType(PretupsI.TRANSFER_TYPE_C2C);
			} else {
				(senderVO.getStaffUserDetails().getUserPhoneVO()).setLastTransferID(channelTransferVO.getTransferID());
				(senderVO.getStaffUserDetails().getUserPhoneVO()).setLastTransferType(PretupsI.TRANSFER_TYPE_C2C);
			}
            if (insertCount > 0) {
				if (log.isDebugEnabled()) {
					log.debug("process", "Commit the data ");
				}
				if (mcomCon != null) {
					mcomCon.finalCommit();
				}
				
				PushMessage pushMessage= null;
				ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
				if (channelTransferVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_NEW)) {	
					 String[] msgArgs = { channelTransferVO.getTransferID() };
						sendEmailNotificationTrf(con, "C2CVCTRFAPR1",channelTransferVO.getTransferID() ,"c2c.vouchers.transfer.initiate.email.notification", channelTransferVO.getFromUserID());	
						  String smsKey = PretupsErrorCodesI.C2C_VOMS_TRANSFER_APPROVAL;
							final String[] array1 = {channelTransferVO.getTransferID() };
							BTSLMessages messages1 = new BTSLMessages(smsKey, array1);
							String msisdns = channelTransferDAO.getMsisdnOfApprovers(con, "C2CVCTRFAPR1", channelTransferVO.getFromUserID());
							if(!BTSLUtil.isNullString(msisdns))
							{
								String[] arrSplit = msisdns.split(",");
								for(int i=0;i<arrSplit.length;i++)
								{
									String msisdn = arrSplit[i];
									pushMessage = new PushMessage(msisdn, messages1,channelTransferVO.getTransferID(),"",p_requestVO.getLocale(),p_requestVO.getNetworkCode());
									pushMessage.push();
								}
							}
							p_requestVO.setMessageArguments(array1);
							p_requestVO.setMessageCode(PretupsErrorCodesI.VOMS_C2C_SUCCESSFUL);
							p_requestVO.setTransactionID(channelTransferVO.getTransferID());
							return;
					
				}
				if (channelTransferVO.getStatus().equals(PretupsI.CHANNEL_TRANSFER_ORDER_CLOSE)) {
					
					sendMsisdnEmail(con, channelTransferVO, senderVO);
					
					
					try {
						PretupsBL.chkAllwdStatusToBecomeActive(con, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_SENDER_USER_STATUS_CHANG)),
								senderVO.getUserID(), senderVO.getStatus());
						PretupsBL.chkAllwdStatusToBecomeActive(con, ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_RECEIVER_USER_STATUS_CHANG)),
								receiverChannelUserVO.getUserID(), receiverChannelUserVO.getStatus());
						 String[] msgArgs = { channelTransferVO.getTransferID() };
				            p_requestVO.setMessageCode(PretupsErrorCodesI.VOMS_C2C_SUCCESSFUL);
				            p_requestVO.setMessageArguments(msgArgs);
				            p_requestVO.setTransactionID(channelTransferVO.getTransferID());
					} catch (Exception ex) {
						log.error("process", "Exception while changing user state to active  " + ex.getMessage());
						log.errorTrace(methodName, ex);
					} finally {
						if (mcomCon != null) {
							mcomCon.finalCommit();
						}
					}
				}
			    p_requestVO.setSuccessTxn(true);
            }
           
		} catch (BTSLBaseException be) {
			p_requestVO.setSuccessTxn(false);
			try {
				if (mcomCon != null) {
					mcomCon.finalRollback();
				}
			}catch (SQLException esql) {
				log.error(methodName,"SQLException : ", esql.getMessage());
			}
			log.error("process", "BTSLBaseException " + be.getMessage());
			if (be.getMessageList() != null && be.getMessageList().size() > 0) {
				final String[] array = {
						BTSLUtil.getMessage(p_requestVO.getLocale(), (ArrayList) be.getMessageList()) };
				p_requestVO.setMessageArguments(array);
			}
			if (be.getArgs() != null) {
				p_requestVO.setMessageArguments(be.getArgs());
			}

			if (be.getMessageKey() != null) {
				p_requestVO.setMessageCode(be.getMessageKey());
			} else {
				p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_USER_TRANSFER);
			}
			log.errorTrace(methodName, be);
			return;
		} catch (Exception e) {
			log.error(methodName, "Exception " + e);
			log.errorTrace(methodName, e);
			p_requestVO.setSuccessTxn(false);
			try {
				if (mcomCon != null) {
					mcomCon.finalRollback();
				}
			} 
			
			catch (SQLException esql) {
				log.error(methodName,"SQLException : ", esql.getMessage());
			}
			log.error("process", "BTSLBaseException " + e.getMessage());
			log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"VoucherC2CController[process]", "", "", "", "Exception:" + e.getMessage());
			p_requestVO.setMessageCode(e.getMessage());
			return;
		} finally {
			if (mcomCon != null) {
				mcomCon.close("VoucherC2CController#process");
				mcomCon = null;
			}
			p_requestVO.setSenderReturnMessage(BTSLUtil.getMessage(p_requestVO.getLocale(),p_requestVO.getMessageCode(),p_requestVO.getMessageArguments()));
			if (log.isDebugEnabled()) {
				log.debug("process", " Exited ");
			}
		} // end of finally
	}// end of process

	/**
	 * Method prepareTransferProfileVO This method construct the VO for the Txn
	 * 
	 * @param con
	 * @param p_senderVO
	 * @param p_receiverVO
	 * @param p_productList
	 * @param p_curDate
	 * @return ChannelTransferVO
	 * @throws BTSLBaseException
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	private ChannelTransferVO prepareTransferProfileVO(HashMap requestMap, String requestMessage, Connection con, ChannelUserVO p_senderVO, ChannelUserVO p_receiverVO,
			 ArrayList<ChannelTransferItemsVO> channelTransferItemsList, Date p_curDate,ArrayList<VomsBatchVO> vomsBatchList, RequestVO p_requestVO) throws BTSLBaseException, JsonParseException, JsonMappingException, IOException {

		if (log.isDebugEnabled()) {
			StringBuilder loggerValue = new StringBuilder();
			loggerValue.setLength(0);
        	loggerValue.append("Entered p_senderVO: ");
        	loggerValue.append(p_senderVO);
        	loggerValue.append("p_receiverVO: ");
        	loggerValue.append(p_receiverVO);
        	loggerValue.append("channelTransferItemsList.size(): ");
        	loggerValue.append(channelTransferItemsList.size());
        	loggerValue.append("p_curDate: ");
        	loggerValue.append(p_curDate);
        	loggerValue.append("vomsBatchList.size(): ");
        	loggerValue.append(vomsBatchList.size());

            log.debug("prepareTransferProfileVO",loggerValue );
		}

		final ChannelTransferVO channelTransferVO = new ChannelTransferVO();
		final C2CFileUploadService c2CFileUploadService =  new C2CFileUploadService();
		JsonNode requestNode = null;
		String paymentCode = null;
		String paymentinstnum = null;
		Date paymentDate = null;
		
		if(requestMap != null && !BTSLUtil.isNullObject(requestMap.get("VOUCHERDETAILS")) ) {
			paymentCode = requestMap.get("PAYMENTINSTCODE").toString();
			paymentinstnum = requestMap.get("PAYMENTINSTNUM").toString();
		
				try {
					long paymentDateL = Long.parseLong(requestMap.get("PAYMENTDATE").toString());
					paymentDate = new Date(paymentDateL);

				} catch (Exception e) {

					String paymentDateStr = requestNode.get("paymentdate").toString();
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
					try {
						paymentDate = sdf.parse(paymentDateStr);
					} catch (ParseException pe) {
						log.error("prepareTransferProfileVO", "Exception while parsing payment date " + pe);
					}

				}

			} 
		else if(requestMap != null && p_requestVO.getSourceType().equals("PLAIN")){
			paymentCode = (String) requestMap.get("PAYMENTINSTCODE");
			paymentinstnum = (String) requestMap.get("PAYMENTINSTNUM");
		
				try {
					 paymentDate = BTSLUtil.getDateFromDateString((String)(requestMap.get("PAYMENTDATE").toString()));
				} catch (Exception e) {
					String paymentDateStr = requestNode.get("paymentdate").toString();
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
					try {
						paymentDate = sdf.parse(paymentDateStr);
					} catch (ParseException pe) {
						log.error("prepareTransferProfileVO", "Exception while parsing payment date " + pe);
					}

				}

		}
		else {
			requestNode = (JsonNode) PretupsRestUtil.convertJSONToObject(requestMessage, new TypeReference<JsonNode>() {});			
			paymentCode = requestNode.get("paymentinstcode").textValue();
			paymentinstnum = requestNode.get("paymentinstnum").textValue();

			try {
				long paymentDateL = Long.parseLong(requestMap.get("PAYMENTDATE").toString());
				paymentDate = new Date(paymentDateL);

			} catch (Exception e) {

				String paymentDateStr = requestNode.get("paymentinstdate").textValue();
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
				try {
					paymentDate = sdf.parse(paymentDateStr);
				} catch (ParseException pe) {
					log.error("prepareTransferProfileVO", "Exception while parsing payment date " + pe);
				}

			}
	
		}
		if(!BTSLUtil.isPaymentTypeValid(paymentCode)){
			throw new BTSLBaseException("VoucherC2CController", "process",
					PretupsErrorCodesI.INVALID_PAYMENT_INST_TYPE, 0, null);
		}
		if(!PretupsI.PAYMENT_INSTRUMENT_TYPE_CASH.equalsIgnoreCase(paymentCode)){
			if(BTSLUtil.isNullString(paymentinstnum))
			{
				p_requestVO.setMessageArguments(new String[] {XmlTagValueConstant.TAG_PAYMENTINSTNUMBER});
				p_requestVO.setMessageCode(PretupsErrorCodesI.EXTSYS_BLANK);
				throw new BTSLBaseException("VoucherC2CController", "process",
						PretupsErrorCodesI.EXTSYS_BLANK, 0, null);
			}
		}
		channelTransferVO.setPayInstrumentType(paymentCode);
		channelTransferVO.setPayInstrumentDate(BTSLUtil.getSQLDateFromUtilDate(paymentDate));
		channelTransferVO.setPayInstrumentNum(paymentinstnum);
		if(PretupsI.REQUEST_SOURCE_TYPE_REST.equals(p_requestVO.getRequestGatewayCode())){
		if (requestNode.get("filepath") != null) {
			String fileName = requestNode.get("filepath").textValue();

			channelTransferVO.setUploadedFilePath(Constants.getProperty("UploadResAssociateMSISDNFilePath") + fileName);

			channelTransferVO.setUploadedFileName(fileName);
			channelTransferVO.setIsFileUploaded(true);

		} else if(requestNode != null && !BTSLUtil.isNullorEmpty(requestNode.get("fileUploaded"))) {
			// Base64 file Upload
			c2CFileUploadService.uploadFileToServer( c2cFileUploadVO, channelTransferVO);
		} else {
			channelTransferVO.setIsFileUploaded(false);
		}
		}
		
		channelTransferVO.setNetworkCode(p_senderVO.getNetworkID());
		channelTransferVO.setNetworkCodeFor(p_senderVO.getNetworkID());
		channelTransferVO.setGraphicalDomainCode(p_senderVO.getGeographicalCode());
		channelTransferVO.setDomainCode(p_senderVO.getDomainID());
		channelTransferVO.setCategoryCode(p_senderVO.getCategoryCode());
		channelTransferVO.setSenderGradeCode(p_senderVO.getUserGrade());
		channelTransferVO.setReceiverGradeCode(p_receiverVO.getUserGrade());
		channelTransferVO.setFromUserID(p_senderVO.getUserID());
		channelTransferVO.setToUserID(p_receiverVO.getUserID());
		channelTransferVO.setTransferDate(p_curDate);
		channelTransferVO.setCommProfileSetId(p_receiverVO.getCommissionProfileSetID());
		channelTransferVO.setCommProfileVersion(p_receiverVO.getCommissionProfileSetVersion());
		channelTransferVO.setDualCommissionType(p_receiverVO.getDualCommissionType());
		channelTransferVO.setStatus(PretupsI.CHANNEL_TRANSFER_ORDER_NEW);
		channelTransferVO.setTransferType(PretupsI.CHANNEL_TRANSFER_TYPE_ALLOCATION);
		channelTransferVO.setTransferInitatedBy(p_senderVO.getUserID());
		channelTransferVO.setSenderTxnProfile(p_senderVO.getTransferProfileID());
		channelTransferVO.setReceiverTxnProfile(p_receiverVO.getTransferProfileID());
		channelTransferVO.setReceiverCategoryCode(p_receiverVO.getCategoryCode());
		channelTransferVO.setTransferCategory(p_senderVO.getTransferCategory());
		channelTransferVO.setReceiverGgraphicalDomainCode(p_receiverVO.getGeographicalCode());
		channelTransferVO.setReceiverDomainCode(p_receiverVO.getDomainID());
		channelTransferVO.setToUserCode(PretupsBL.getFilteredMSISDN(p_receiverVO.getUserCode()));
		channelTransferVO.setFromUserCode(PretupsBL.getFilteredMSISDN(p_senderVO.getMsisdn()));
		channelTransferVO.setToUserID(p_receiverVO.getUserID());
		channelTransferVO.setOtfFlag(false);
		long totRequestQty = 0, totMRP = 0, totPayAmt = 0, totNetPayAmt = 0, totTax1 = 0, totTax2 = 0, totTax3 = 0;
		long commissionQty = 0;
		String productCode = null;
		String productType = null;
		for(ChannelTransferItemsVO channelTransferItemsVO :channelTransferItemsList){
			totRequestQty += PretupsBL.getSystemAmount(channelTransferItemsVO.getRequestedQuantity());
			if (PretupsI.COMM_TYPE_POSITIVE.equals(p_receiverVO.getDualCommissionType())) {
				totMRP += (channelTransferItemsVO.getReceiverCreditQty())
						* Long.parseLong(PretupsBL.getDisplayAmount(channelTransferItemsVO.getUnitValue()));
			} else {
				totMRP += (Double.parseDouble(channelTransferItemsVO.getRequestedQuantity())
						* channelTransferItemsVO.getUnitValue());
			}
			totPayAmt += channelTransferItemsVO.getPayableAmount();
			totNetPayAmt += channelTransferItemsVO.getNetPayableAmount();
			totTax1 += channelTransferItemsVO.getTax1Value();
			totTax2 += channelTransferItemsVO.getTax2Value();
			totTax3 += channelTransferItemsVO.getTax3Value();
			commissionQty += channelTransferItemsVO.getCommQuantity();
			productCode = channelTransferItemsVO.getProductCode();
			productType = channelTransferItemsVO.getProductType();
		}
		channelTransferVO.setRequestedQuantity(totRequestQty);
		channelTransferVO.setTransferMRP(totMRP);
		channelTransferVO.setPayableAmount(totPayAmt);
		channelTransferVO.setNetPayableAmount(totNetPayAmt);
		channelTransferVO.setTotalTax1(totTax1);
		channelTransferVO.setTotalTax2(totTax2);
		channelTransferVO.setTotalTax3(totTax3);
		channelTransferVO.setProductCode(productCode);
		channelTransferVO.setChannelTransferitemsVOList(channelTransferItemsList);
		channelTransferVO.setActiveUserId(p_senderVO.getActiveUserID());
		channelTransferVO.setCreatedOn(p_curDate);
		channelTransferVO.setCreatedBy(p_senderVO.getActiveUserID());
		channelTransferVO.setModifiedOn(p_curDate);
		channelTransferVO.setModifiedBy(p_senderVO.getActiveUserID());
		channelTransferVO.setProductType(productType);
		final ArrayList<ChannelVoucherItemsVO> channelVoucherItemsVOList = new ArrayList<ChannelVoucherItemsVO>();
		for(VomsBatchVO vomsBatchVO : vomsBatchList){
	        ChannelVoucherItemsVO channelVoucherItemsVO = new ChannelVoucherItemsVO();
			channelVoucherItemsVO.setTransferId(channelTransferVO.getTransferID());
			channelVoucherItemsVO.setTransferDate(p_curDate);
			//channelVoucherItemsVO.setTransferMRP((long)Double.parseDouble(vomsBatchVO.getDenomination()));
			channelVoucherItemsVO.setTransferMRP(BTSLUtil.parseDoubleToLong(Double.parseDouble(vomsBatchVO.getDenomination())));
			
			
			channelVoucherItemsVO.setRequiredQuantity(BTSLUtil.parseDoubleToLong(Double.parseDouble(vomsBatchVO.getQuantity())));
			channelVoucherItemsVO.setFromSerialNum(vomsBatchVO.getFromSerialNo());
			channelVoucherItemsVO.setToSerialNum(vomsBatchVO.getToSerialNo());
			
			if(vomsBatchVO.getProductID() != null) {
				channelVoucherItemsVO.setProductId(vomsBatchVO.getProductID());
			}else {				
				String productId =  (new ChannelTransferDAO()).retreiveProductId(con, vomsBatchVO.getFromSerialNo());
				channelVoucherItemsVO.setProductId(productId);				
			}
			channelVoucherItemsVO.setProductName(vomsBatchVO.getProductName());
			channelVoucherItemsVO.setNetworkCode(channelTransferVO.getNetworkCode());
			channelVoucherItemsVO.setFromUser(p_senderVO.getUserID());
			channelVoucherItemsVO.setToUser(p_receiverVO.getUserID());
			channelVoucherItemsVO.setType(PretupsI.CHANNEL_TYPE_C2C);
			channelVoucherItemsVO.setVoucherType(vomsBatchVO.getVoucherType());
			channelVoucherItemsVO.setSegment(vomsBatchVO.getVouchersegment());
			channelVoucherItemsVO.setRequiredQuantity(vomsBatchVO.getQuantityLong());
			channelVoucherItemsVOList.add(channelVoucherItemsVO);
		}
		channelTransferVO.setChannelVoucherItemsVoList(channelVoucherItemsVOList);
		channelTransferVO.setType(PretupsI.CHANNEL_TYPE_C2C);
		channelTransferVO.setTransferSubType(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER);
		channelTransferVO.setCommQty(PretupsBL.getSystemAmount(commissionQty));
		channelTransferVO.setSenderDrQty(0);
		channelTransferVO.setReceiverCrQty(0);
		
		if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue()) {
			ArrayList<ChannelSoSVO> chnlSoSVOList = new ArrayList<>();
			chnlSoSVOList.add(new ChannelSoSVO(p_senderVO.getUserID(), p_senderVO.getMsisdn(),
					p_senderVO.getSosAllowed(), p_senderVO.getSosAllowedAmount(), p_senderVO.getSosThresholdLimit()));
			chnlSoSVOList.add(
					new ChannelSoSVO(p_receiverVO.getUserID(), p_receiverVO.getMsisdn(), p_receiverVO.getSosAllowed(),
							p_receiverVO.getSosAllowedAmount(), p_receiverVO.getSosThresholdLimit()));
			channelTransferVO.setChannelSoSVOList(chnlSoSVOList);
		}

		if (log.isDebugEnabled()) {
			log.debug("prepareTransferProfileVO", " Exited: channelTransferVO=  " + channelTransferVO);
		}

		return channelTransferVO;
	}// end of prepareTransferProfileVO
	
	
	
	private void sendMsisdnEmail(Connection con,ChannelTransferVO channelTransferVO, ChannelUserVO channelUserVO){
		
		try {
		boolean _receiverMessageSendReq = false;
		String _serviceType = PretupsI.SERVICE_TYPE_CHNL_C2C_INTR;
		_receiverMessageSendReq = ((Boolean) PreferenceCache.getControlPreference(
				PreferenceI.REC_MSG_SEND_ALLOW_C2C, channelUserVO.getNetworkCode(), _serviceType))
						.booleanValue();

		
		final UserDAO userDAO = new UserDAO();

		String toUserMsisdn = userDAO.retrieveMsisdn(channelTransferVO.getToUserID(), con);
		String fromUserMsisdn = userDAO.retrieveMsisdn(channelTransferVO.getFromUserID(), con);

		String[] msisdnArr = { fromUserMsisdn, toUserMsisdn };

		int msisdnCounter = 0;
		for (String msisdn : msisdnArr) {

			msisdnCounter++;
			if (msisdn != null) {

				String p_subject = null;
				String senderOrReceiver = null;
				String emailId = null;
				
				if (msisdnCounter == 1) {
					p_subject = PretupsI.C2C_VOUCHER_COMPLETION_SUBJECT_SENDER;
					senderOrReceiver = "SENDER";
					emailId = userDAO.retrieveEmail(channelTransferVO.getFromUserID(), con);
				} else {
					p_subject = PretupsI.C2C_VOUCHER_COMPLETION_SUBJECT_RECEIVER;
					senderOrReceiver = "RECEIVER";
					emailId = userDAO.retrieveEmail(channelTransferVO.getToUserID(), con);
				}

				sendEmailNotification(con, emailId, channelTransferVO.getTransferID(), p_subject, senderOrReceiver, channelTransferVO);

				UserPhoneVO primaryPhoneVO = null;
				if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue()
						&& (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.MESSAGE_TO_PRIMARY_REQUIRED)).booleanValue())) {
					primaryPhoneVO = userDAO.loadUserAnyPhoneVO(con, msisdn);
				}
				final UserPhoneVO phoneVO = userDAO.loadUserAnyPhoneVO(con, msisdn);
				String country = (String) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY);
				String language = (String) PreferenceCache
						.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE);

				if (_receiverMessageSendReq) {

					String messageKey = null;

					if (msisdnCounter == 1) {
						messageKey = PretupsErrorCodesI.C2C_TRANSFER_VOUCHER_APPROVED_SENDER;
					} else {
						messageKey = PretupsErrorCodesI.C2C_TRANSFER_VOUCHER_APPROVED_RECEIVER;
					}
					if (primaryPhoneVO != null) {
						country = primaryPhoneVO.getCountry();
						language = primaryPhoneVO.getPhoneLanguage();
						final Locale locale = new Locale(language, country);
						// final Object[] smsListArr = prepareSMSMessageListForVoucher(con,
						// channelTransferVO);
						final String[] array = { channelTransferVO.getNetPayableAmountAsString(),
								channelTransferVO.getTransferID() };
						final BTSLMessages messages = new BTSLMessages(messageKey, array);
						final PushMessage pushMessage = new PushMessage(primaryPhoneVO.getMsisdn(),
								messages, channelTransferVO.getTransferID(), null, locale,
								channelTransferVO.getNetworkCode());
						pushMessage.push();
					}
					if (phoneVO != null) {
						country = phoneVO.getCountry();
						language = phoneVO.getPhoneLanguage();
						final Locale locale = new Locale(language, country);
						// final Object[] smsListArr = prepareSMSMessageListForVoucher(con,
						// channelTransferVO);
						final String[] array = { channelTransferVO.getNetPayableAmountAsString(),
								channelTransferVO.getTransferID() };
						final BTSLMessages messages = new BTSLMessages(messageKey, array);
						final PushMessage pushMessage = new PushMessage(phoneVO.getMsisdn(), messages,
								channelTransferVO.getTransferID(), null, locale,
								channelTransferVO.getNetworkCode());
						pushMessage.push();
					}
				}
			}
		}

		}catch(Exception e){
			log.error("sendMsisdnEmail ", " Email sending failed" + e.getMessage());
		}

	
	}

	/**
	 * @param p_con
	 * @param p_roleCode
	 * @param transferID
	 * @param p_subject
	 */
	private void sendEmailNotification(Connection p_con, String to, String transferID, String p_subject, String senderOrReceiver, ChannelTransferVO channelTransferVO) {
		final String methodName = "sendEmailNotification";
		if (log.isDebugEnabled()) {
			StringBuilder loggerValue = new StringBuilder();
			loggerValue.setLength(0);
			loggerValue.append("transferID: ");
			loggerValue.append(transferID);
			loggerValue.append("p_subject: ");
			loggerValue.append(p_subject);
			log.debug(methodName, loggerValue);
		}
		try {

			String cc = PretupsI.EMPTY;
			final String bcc = "";
			ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
			final boolean isAttachment = false;
			final String pathofFile = "";
			final String fileNameTobeDisplayed = "";
			String preMessages = null;
			String messages = null;
			String postMessages = null;
			String middleMessage = "";
			Locale locale = BTSLUtil.getSystemLocaleForEmail();
			String from = BTSLUtil.getMessage(locale,"email.notification.c2c.log.file.from");

			if (senderOrReceiver != null && senderOrReceiver.equals("SENDER")) {
				preMessages = PretupsRestUtil
						.getMessageString("c2c.voucher.completion.initiate.email.notification.content.sender.pre");
				
				messages = PretupsRestUtil
						.getMessageString("c2c.voucher.completion.initiate.email.notification.content.sender");
			} else {
				preMessages = PretupsRestUtil
						.getMessageString("c2c.voucher.completion.initiate.email.notification.content.receiver.pre");
				
				messages = PretupsRestUtil
						.getMessageString("c2c.voucher.completion.initiate.email.notification.content.receiver");
			}
			
			postMessages = PretupsRestUtil
					.getMessageString("c2c.voucher.completion.initiate.email.notification.content.senderreceiver.post");
			
			
			log.debug("preMessages ", preMessages);
			log.debug("messages ", messages);
			log.debug("postMessages ", postMessages);
			
			final ChannelTransferDAO channelTransferDAOI = new ChannelTransferDAO();
			channelTransferDAOI.loadChannelTransfersVO(p_con, channelTransferVO);
			
			
			ArrayList<ChannelVoucherItemsVO> channelVoucherItemsVOList =  channelTransferVO.getChannelVoucherItemsVoList();
			
			for(ChannelVoucherItemsVO channelVoucherItemsVoObj: channelVoucherItemsVOList  ) {

				
				middleMessage = middleMessage+""+messages;
				middleMessage = middleMessage.replaceAll("\\[Transfer ID\\]", channelTransferVO.getTransferID());
				middleMessage = middleMessage.replaceAll("\\[Sender Category\\]", channelTransferVO.getDomainCode());
				middleMessage = middleMessage.replaceAll("\\[Sender Name\\]", channelTransferVO.getFromUserName());
				middleMessage = middleMessage.replaceAll("\\[Receiver Category\\]", channelTransferVO.getReceiverCategoryCode());
				middleMessage = middleMessage.replaceAll("\\[Receiver Name\\]", channelTransferVO.getToUserName());//
				middleMessage = middleMessage.replaceAll("\\[Denomination\\]", channelVoucherItemsVoObj.getTransferMrp()+"");
				middleMessage = middleMessage.replaceAll("\\[Requested quantity\\]", channelVoucherItemsVoObj.getInitiatedQuantity()+"");
				
				long quantity = Long.parseLong(channelVoucherItemsVoObj.getToSerialNum()) - Long.parseLong(channelVoucherItemsVoObj.getFromSerialNum()) + 1;
				
				middleMessage = middleMessage.replaceAll("\\[Transferred quantity\\]", quantity + "");
				middleMessage = middleMessage.replaceAll("\\[Net receivable amount\\]", channelTransferVO.getNetPayableAmount()+"");
				middleMessage = middleMessage.replaceAll("\\[Net payablee amount\\]", channelTransferVO.getNetPayableAmount()+"");//
				
				
				
				middleMessage = middleMessage.replaceAll("\\[Status\\]", channelTransferVO.getStatus());
				middleMessage = middleMessage.replaceAll("\\[Approval 1 quantity\\]", channelVoucherItemsVoObj.getFirstLevelApprovedQuantity()+"");
				middleMessage = middleMessage.replaceAll("\\[Approval 2 quantity\\]", channelVoucherItemsVoObj.getSecondLevelApprovedQuantity()+"");
				middleMessage = middleMessage.replaceAll("\\[Approval 3 quantity\\]", quantity + "");

				
				
			}
			
			log.debug("messages ", middleMessage);
			log.debug("compete messages ", preMessages+""+middleMessage+postMessages);
		
				EMailSender.sendMail(to, from, bcc, cc, p_subject, preMessages+""+middleMessage+postMessages, isAttachment, pathofFile,
						fileNameTobeDisplayed);

			if (log.isDebugEnabled()) {
				log.debug("MAIL CONTENT ", messages);
			}
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.error("sendEmailNotification ", " Email sending failed" + e.getMessage());
			}
			log.errorTrace(methodName, e);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting ....");
		}
	}

	
	/**
	 * @param p_con
	 * @param p_roleCode
	 * @param transferID
	 * @param p_subject
	 */
	private void sendEmailNotificationTrf(Connection p_con,String p_roleCode,String transferID,String p_subject, String parentId) {
		final String methodName = "sendEmailNotificationTrf";
		if (log.isDebugEnabled()) {
			StringBuilder loggerValue = new StringBuilder();
			loggerValue.setLength(0);
        	loggerValue.append("Entered p_roleCode: ");
        	loggerValue.append(p_roleCode);
        	loggerValue.append("transferID: ");
        	loggerValue.append(transferID);
        	loggerValue.append("p_subject: ");
        	loggerValue.append(p_subject);
            log.debug(methodName,loggerValue );
		}
		try {
			
			String cc = PretupsI.EMPTY;
			final String bcc = "";
			String subject = "";
			ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
			String to = channelTransferDAO.getEmailIdOfApprovers(p_con, p_roleCode, parentId);
			final boolean isAttachment = false;
			final String pathofFile = "";
			final String fileNameTobeDisplayed = "";
			String messages = null;
			subject =PretupsRestUtil.getMessageString(p_subject);

			Locale locale = BTSLUtil.getSystemLocaleForEmail();
			String from = BTSLUtil.getMessage(locale,"email.notification.c2c.log.file.from");
				
			
			messages = PretupsRestUtil.getMessageString("c2c.voucher.transfer.initiate.email.notification.content") + " " + transferID +" "+PretupsRestUtil.getMessageString("c2c.voucher.transfer.initiate.email.notification.content1");
			if (!BTSLUtil.isNullString(p_roleCode)) {
				EMailSender.sendMail(to, from, bcc, cc, subject, messages, isAttachment, pathofFile, fileNameTobeDisplayed);
			}
			if (log.isDebugEnabled()) {
				log.debug("MAIL CONTENT ", messages);
			}
		} catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.error("sendEmailNotificationTrf ", " Email sending failed" + e.getMessage());
			}
			log.errorTrace(methodName, e);
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting ....");
		}
	}
	
	/**
	 * @param con
	 * @param vomsBatchList
	 * @param userId
	 * @param voucherType
	 * @param voucherSegment
	 * @param toUserId
	 * @param networkCode
	 * @param p_requestVO 
	 * @return
	 * @throws BTSLBaseException
	 */
	public ActionErrors validateVoucher(Connection con,ArrayList<VomsBatchVO> vomsBatchList,String userId,String toUserId,String networkCode, RequestVO p_requestVO) throws BTSLBaseException{
		ActionErrors errors = null;
		String methodName = "validateVoucher";
		if (log.isDebugEnabled()) {
			StringBuilder loggerValue = new StringBuilder();
			loggerValue.setLength(0);
        	loggerValue.append("Entered userId: ");
        	loggerValue.append(userId);
        	loggerValue.append("vomsBatchList.size(): ");
        	loggerValue.append(vomsBatchList.size());
        	loggerValue.append("toUserId: ");
        	loggerValue.append(toUserId);
        	loggerValue.append("networkCode: ");
        	loggerValue.append(networkCode);
            log.debug(methodName,loggerValue );
		}
		try{
			errors = new ActionErrors();
			ChannelTransferWebDAO channelTransferWebDAO = new ChannelTransferWebDAO();
			if(vomsBatchList == null || vomsBatchList.isEmpty()){
				if(!p_requestVO.getSourceType().equals("WEB")){
				throw new BTSLBaseException("validateVoucher", "process",
						PretupsErrorCodesI.C2C_TRF_VOMS_LIST);} else {
							throw new BTSLBaseException("validateVoucher", "process",
									"c2c.vouchers.productcantbenull.error");
						}
			}
			final Map<String, String> denomMap = new HashMap<String, String>();
			int i =1;
			Date currentDate = new Date();
			for(VomsBatchVO vomsBatchVO : vomsBatchList){
				long quantity = 0;
				if(BTSLUtil.isNullString(vomsBatchVO.getDenomination())){
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("c2c.vouchers.transferdetails.error.denominationreq", i + 1 + ""));
				}
				else if(!BTSLUtil.isDecimalValue(vomsBatchVO.getDenomination())){
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("c2c.vouchers.transferdetails.error.denominationumeric", i + 1 + ""));
				}
				
			   if (!BTSLUtil.isNullString(vomsBatchVO.getDenomination()) && BTSLUtil.isNullString(vomsBatchVO.getFromSerialNo())){
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("c2c.vouchers.transferdetails.error.FromSNOreq", i + 1 + ""));
				}
			   else if (!BTSLUtil.isNullString(vomsBatchVO.getFromSerialNo()) && !BTSLUtil.isNumeric(vomsBatchVO.getFromSerialNo())){
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("c2c.vouchers.transferdetails.error.fromSnonumericone", i + 1 + ""));
				}
			   if (!BTSLUtil.isNullString(vomsBatchVO.getDenomination()) && BTSLUtil.isNullString(vomsBatchVO.getToSerialNo())) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("c2c.vouchers.transferdetails.error.TOSNOreq", i + 1 + ""));
				}
			   else if (!BTSLUtil.isNullString(vomsBatchVO.getToSerialNo()) && !BTSLUtil.isNumeric(vomsBatchVO.getToSerialNo())) {
					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("c2c.vouchers.transferdetails.error.toSnonumericone", i + 1 + ""));
				}
				if (!BTSLUtil.isNullString(vomsBatchVO.getToSerialNo()) && !BTSLUtil.isNullString(vomsBatchVO.getFromSerialNo()) 
						&& BTSLUtil.isNumeric(vomsBatchVO.getToSerialNo()) && BTSLUtil.isNumeric(vomsBatchVO.getFromSerialNo()) 
						&& !BTSLUtil.isNullString(vomsBatchVO.getDenomination()) && BTSLUtil.isDecimalValue(vomsBatchVO.getDenomination())) {
                    if (Long.parseLong(vomsBatchVO.getToSerialNo()) < Long.parseLong(vomsBatchVO.getFromSerialNo())) {
                        errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("c2c.vouchers.transferdetails.error.FromToSNO", i + 1 + ""));
                    }
                    
                    quantity = Long.parseLong(vomsBatchVO.getToSerialNo()) - Long.parseLong(vomsBatchVO.getFromSerialNo()) + 1;
    				if(!channelTransferWebDAO.areAllVouchersAssociated(con, vomsBatchVO.getFromSerialNo(), vomsBatchVO.getToSerialNo(), quantity, userId,vomsBatchVO.getVoucherType(),vomsBatchVO.getVouchersegment(),vomsBatchVO.getDenomination(),networkCode)){
    					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("c2c.vouchers.validate.error.invalid",i + 1, vomsBatchVO.getFromSerialNo(), vomsBatchVO.getToSerialNo()));
    				}
    				else if(channelTransferWebDAO.doesRangeContainMultipleProfiles(con, vomsBatchVO.getFromSerialNo(), vomsBatchVO.getToSerialNo(), quantity, userId,vomsBatchVO.getVoucherType(),vomsBatchVO.getVouchersegment(),vomsBatchVO.getDenomination(),networkCode)){
    					errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("c2c.vouchers.validate.error.invalid.multiple.profiles",i + 1, vomsBatchVO.getFromSerialNo(), vomsBatchVO.getToSerialNo()));
    				}
    				
    				else{
    					vomsBatchVO.setQuantity(String.valueOf(quantity));
    				}
    				  if (denomMap.containsKey(vomsBatchVO.getDenomination())) {
                          if (Long.parseLong(vomsBatchVO.getFromSerialNo()) <= Long.parseLong(denomMap.get(vomsBatchVO.getDenomination()))) {
                              errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("c2c.voucher.completion.initiate.error.sequential", i + 1 + ""));
                          } else {
                              denomMap.put(vomsBatchVO.getDenomination(), vomsBatchVO.getToSerialNo());
                          }
                      } else {
                          denomMap.put(vomsBatchVO.getDenomination(), vomsBatchVO.getToSerialNo());
                      }
    				
    				vomsBatchVO.setCreatedBy(userId);
    				vomsBatchVO.set_NetworkCode(networkCode);
    				vomsBatchVO.setCreatedDate(currentDate);
    				vomsBatchVO.setModifiedDate(currentDate);
    				vomsBatchVO.setModifiedOn(currentDate);
    				vomsBatchVO.setCreatedOn(currentDate);
    				vomsBatchVO.setToUserID(toUserId); 
    				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOUCHER_EN_ON_TRACKING))).booleanValue()) {
    						vomsBatchVO.setBatchType(VOMSI.BATCH_ENABLED);
    					} 
    				else {
    						vomsBatchVO.setBatchType(VOMSI.VOMS_PRE_ACTIVE_STATUS);
    					}
    				String batch_no = String.valueOf(IDGenerator.getNextID(VOMSI.VOMS_BATCHES_DOC_TYPE, String.valueOf(BTSLUtil.getFinancialYear()), VOMSI.ALL));
    				vomsBatchVO.setBatchNo(new VomsUtil().formatVomsBatchID(vomsBatchVO, batch_no));
    				if (channelTransferWebDAO.validateVoucherSerialNoC2C(con, vomsBatchVO.getFromSerialNo(),"null",PretupsI.TRANSFER_TYPE_C2C)) {
    					if(!p_requestVO.getSourceType().equals("WEB")){
    					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOMS_FROM_SERIAL_C2C_PENDING_APPROVAL);} else {
    						throw new BTSLBaseException(this, methodName, "c2c.vouchers.transfer.error.fromserialapprovalpending");
    					}
    				}
    				if (channelTransferWebDAO.validateVoucherSerialNoC2C(con, vomsBatchVO.getToSerialNo(), "null",PretupsI.TRANSFER_TYPE_C2C)) {
    					if(!p_requestVO.getSourceType().equals("WEB")){
    					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOMS_TO_SERIAL_C2C_PENDING_APPROVAL);} else {
    						throw new BTSLBaseException(this, methodName, "c2c.vouchers.transfer.error.toserialapprovalpending");
    					}
    				}
    				final ArrayList<VomsBatchVO> usedBatches = channelTransferWebDAO.validateBatch(con, vomsBatchVO);
    				if (usedBatches != null && !usedBatches.isEmpty()) {
    					if(!p_requestVO.getSourceType().equals("WEB")){
    					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.VOMS_INVALID_BATCH_C2C);}
    					else {
    						throw new BTSLBaseException(this, methodName, "channeltransfer.transferdetails.error.invalidbatch");
    					}
    				}
    				
                 }
				i++;
			  }
		}catch (Exception e) {
			if (log.isDebugEnabled()) {
				log.error(methodName, "Exception in validation of voucher list" + e.getMessage());
			}
			  log.errorTrace(methodName, e);
	          throw new BTSLBaseException(this, methodName, e.getMessage());
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting ....");
		}
		return errors;
	}
		
    /**
     * @param p_con
     * @param p_channelTransferVO
     * @param p_isOutSideHierarchy
     * @param p_fromWEB
     * @param p_forwardPath
     * @param p_curDate
     * @return
     * @throws BTSLBaseException
     */
    public  int approveC2CVoucherTransfer(Connection p_con, ChannelTransferVO p_channelTransferVO, boolean p_isOutSideHierarchy, Date p_curDate,ArrayList<VomsBatchVO> vomsBatchList, RequestVO p_requestVO) throws BTSLBaseException {
    	StringBuilder loggerValue= new StringBuilder(); 
    	if (log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered p_channelTransferVO: ");
        	loggerValue.append(p_channelTransferVO);
        	loggerValue.append("p_isOutSideHirearchy: ");
        	loggerValue.append(p_isOutSideHierarchy);
        	loggerValue.append("p_curDate: ");
        	loggerValue.append(p_curDate);
        	loggerValue.append("vomsBatchList: ");
        	loggerValue.append(vomsBatchList);
            log.debug("approveChannelToChannelTransfer",loggerValue );
        }
        final String methodName = "approveC2CVoucherTransfer";
        // insert the TXN data in the parent and child tables.
        final ChannelTransferDAO channelTransferDAO = new ChannelTransferDAO();
        int insertCount = 0;
        int level = ((Integer) PreferenceCache.getControlPreference(CorePreferenceI.MAX_APPROVAL_LEVEL_C2C_VOUCHER_TRANSFER, p_channelTransferVO.getNetworkCode(),p_channelTransferVO.getCategoryCode())).intValue();

        
        
        if(PretupsI.CACHE_ALL.equals(String.valueOf(level))){
        	p_channelTransferVO.setStatus(PretupsI.C2C_VOUCHER_STATUS_CLOSE);
        	 insertCount = channelTransferDAO.addChannelTransfer(p_con, p_channelTransferVO);

 			final ChannelTransferWebDAO channelTransferWebDAO = new ChannelTransferWebDAO();
 			ArrayList<VomsBatchVO> batchVO_list = new ArrayList<VomsBatchVO>();
 			
			for (VomsBatchVO vomsBatchVO : vomsBatchList) {

				final int insert_count = channelTransferWebDAO.insertVomsBatches(p_con, vomsBatchVO);
				
				if (insert_count > 0) {
					batchVO_list.add(vomsBatchVO);

				}
			}

			VoucherChangeUserId voucherChangeStatus = new VoucherChangeUserId(batchVO_list);
			voucherChangeStatus.start();

        }
        else{
       	 insertCount = channelTransferDAO.addChannelTransfer(p_con, p_channelTransferVO);
        }
        if (insertCount < 0) {
        	OracleUtil.rollbackConnection(p_con, VoucherC2CController.class.getName(), methodName);
            throw new BTSLBaseException(VoucherC2CController.class, methodName, PretupsErrorCodesI.ERROR_UPDATING_DATABASE);
        }
        try{

            if (insertCount == 1 && "CLOSE".equals(p_channelTransferVO.getStatus())) {
                boolean statusChangeRequired = false;
                int updatecount1 = 0;
                int updatecount2 = 0;
                if (!PretupsI.USER_STATUS_ACTIVE.equals(p_channelTransferVO.getFromChannelUserStatus())) {

                    final String str[] = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_SENDER_USER_STATUS_CHANG)).split(",");
                    String newStatus[] = null;
                    int strs=str.length;
                    for (int i = 0; i <strs ; i++) {
                        newStatus = str[i].split(":");
                        if (newStatus[0].equals(p_channelTransferVO.getFromChannelUserStatus())) {
                            statusChangeRequired = true;
                            updatecount1 = _operatorUtil.changeUserStatusToActive(p_con, p_channelTransferVO.getFromUserID(), p_channelTransferVO.getFromChannelUserStatus(),
                                newStatus[1]);
                            break;
                        }
                    }

                }

                if (!PretupsI.USER_STATUS_ACTIVE.equals(p_channelTransferVO.getToChannelUserStatus())) {

                    final String str[] = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.TXN_RECEIVER_USER_STATUS_CHANG)).split(","); // "CH:Y,EX:Y".split(",");
                    String newStatus[] = null;
                    int st=str.length;
                    for (int i = 0; i <st ; i++) {
                        newStatus = str[i].split(":");
                        if (newStatus[0].equals(p_channelTransferVO.getToChannelUserStatus())) {
                            statusChangeRequired = true;
                            updatecount2 = _operatorUtil.changeUserStatusToActive(p_con, p_channelTransferVO.getToUserID(), p_channelTransferVO.getToChannelUserStatus(),
                                newStatus[1]);
                            break;
                        }
                    }

                }
                if (statusChangeRequired && (updatecount1 > 1 || updatecount2 > 1)) {
                	insertCount = 0;
                }
                
            	if((Boolean)PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_BASE_COMMISSION,p_channelTransferVO.getNetworkCode()))
        		{
                	if(p_channelTransferVO.isTargetAchieved())
        			{
        				TargetBasedCommissionMessages tbcm =new TargetBasedCommissionMessages();
        				tbcm.loadBaseCommissionProfileDetailsForTargetMessages(p_con,p_channelTransferVO.getToUserID(),p_channelTransferVO.getMessageArgumentList());
        			}
        		} 
            }
        
        }catch (Exception e) {
        	
            log.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append("Exception during status update to active ");
        	loggerValue.append(e.getMessage());
            log.error(methodName,  loggerValue );
            throw new BTSLBaseException(VoucherC2CController.class, methodName, e.getMessage());
        }
        OneLineTXNLog.log(p_channelTransferVO, null);
        if (log.isDebugEnabled()) {
        	 loggerValue.setLength(0);
         	loggerValue.append("Exit insertCount =");
         	loggerValue.append(insertCount);
            log.debug(methodName,  loggerValue);
        }
        
    	return insertCount;
    }
}

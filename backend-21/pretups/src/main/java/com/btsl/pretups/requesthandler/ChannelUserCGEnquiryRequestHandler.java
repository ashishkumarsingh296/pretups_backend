/*@(#)CardGroupEnquiryRequestHandler.java
 * Name                                 Date            History
 *------------------------------------------------------------------------
 * Harsh Dixit				  			Dec 30,2015		
 *------------------------------------------------------------------------
 * Copyright (c) 2015 MComviva Ltd.
 * Handler class for card group enquiry 
 */
package com.btsl.pretups.requesthandler;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDAO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDetailsVO;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.ServiceClassDAO;
import com.btsl.pretups.master.businesslogic.ServiceClassVO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.master.businesslogic.ServiceTypeSelectorMappingDAO;
import com.btsl.pretups.master.businesslogic.ServiceTypeSelectorMappingVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.transfer.businesslogic.TransferRulesVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * @author Vipan Kumar
 *
 * This controller is designed to fetch the ALL card group slabs for that user wrt to service type.
 * It is used for the both pre-paid and post-paid subscribers.
 */
public class ChannelUserCGEnquiryRequestHandler implements ServiceKeywordControllerI
{
	private  static Log _log = LogFactory.getLog(ChannelUserCGEnquiryRequestHandler.class.getName());

	private  String _slabDetails="";
	private String _slabDetailsForAmount="";
	private ArrayList<CardGroupDetailsVO> _cardGroupDetailsVOList = null;
	private TransferVO _transferVO=null;
	private CardGroupDetailsVO _cardGroupDetailsVO=null;
	private C2STransferVO _c2sTransferVO=null;
	private String _senderMSISDN;
	private String _receiverMSISDN;
	private ChannelUserVO _channelUserVO;
	private ReceiverVO _receiverVO;
	private String _senderNetworkCode;
	private Date _currentDate=null;

	private boolean _isCounterDecreased=false;
	private RequestVO _requestVO=null;
	public static OperatorUtilI _operatorUtil=null;

	Locale _senderLocale=null;
	Locale _receiverLocale=null;
	private String _finalMessage="";
	private String slabValueSeparator=null;
	//	Loads operator specific class
	static
	{
		final String METHOD_NAME = "static";
		String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		try
		{
			_operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
		}
		catch(Exception e)
		{
			_log.errorTrace(METHOD_NAME,e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"C2SEnquiryHandler[initialize]","","","","Exception while loading the class at the call:"+e.getMessage());
		}
	}
	/**
	 * Method to process the request of the CardGroup enquiry
	 * @param object of the RequestVO
	 */	
	public void process(RequestVO p_requestVO) 
	{
		final String METHOD_NAME = "process";
		Connection con=null;MComConnectionI mcomCon = null;
		String spaceSeperator =" ";

		String _selector=null;
		ArrayList<TransferRulesVO> trfRuleList=new ArrayList<TransferRulesVO>();
		TransferRulesVO transferRulesVO=null;
		p_requestVO.setSenderMessageRequired(false);
		try {
			String SeperatorValues = Constants.getProperty("CARDGROUP_ENQUIRY_SEPARATOR");
			if(SeperatorValues.contains(",")){
				String[] separators=SeperatorValues.split(",");
				spaceSeperator=separators[0];
				slabValueSeparator=separators[1];
			}else{
				spaceSeperator=SeperatorValues;
			}

			if(BTSLUtil.isNullString(spaceSeperator)||"null".equalsIgnoreCase(spaceSeperator)){
				if(!BTSLUtil.isNullString((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR)))
					spaceSeperator = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
				else 
					spaceSeperator =" ";
			}
			if(BTSLUtil.isNullString(slabValueSeparator)){
				slabValueSeparator=":";
			}

		} catch (RuntimeException e) {
			if (_log.isDebugEnabled()) _log.debug(METHOD_NAME," "+e.getMessage());
			_log.errorTrace(METHOD_NAME, e);
			if(!BTSLUtil.isNullString((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR)))
				spaceSeperator = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHNL_PLAIN_SMS_SEPARATOR);
			else 
				spaceSeperator =" ";
			slabValueSeparator=":";
		}

		if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,p_requestVO.getRequestIDStr(),"Entered for Request ID="+p_requestVO.getRequestID()+" MSISDN="+p_requestVO.getFilteredMSISDN());

		try
		{
			_requestVO=p_requestVO;
			_channelUserVO=(ChannelUserVO)p_requestVO.getSenderVO();
			_senderLocale=p_requestVO.getSenderLocale();
			_senderNetworkCode=_channelUserVO.getNetworkID();
			_currentDate = new Date();
			_c2sTransferVO = new C2STransferVO();
			_transferVO = new TransferVO();
			_transferVO.setTransferDateTime(_currentDate);


			populateVOFromRequest(p_requestVO);

			//Getting oracle connection
			mcomCon = new MComConnection();con=mcomCon.getConnection();

			_operatorUtil.validateCardGroupEnquiryRequest(con,_c2sTransferVO,p_requestVO);



			_receiverLocale=p_requestVO.getReceiverLocale();
			_senderLocale=p_requestVO.getSenderLocale();

			_receiverVO=(ReceiverVO)_c2sTransferVO.getReceiverVO();


			if(!BTSLUtil.isNullString(p_requestVO.getEnquirySubService())){
				_selector=p_requestVO.getEnquirySubService();
			}else{
				ServiceSelectorMappingVO serviceSelectorMappingVO=ServiceSelectorMappingCache.getDefaultSelectorForServiceType(p_requestVO.getEnquiryServiceType());
				if(serviceSelectorMappingVO!=null) {
					_selector=serviceSelectorMappingVO.getSelectorCode();
				}
			}
			p_requestVO.setReqSelector(_selector);
			_receiverVO.setMsisdnPrefix(PretupsBL.getMSISDNPrefix(_receiverVO.getMsisdn()));


			_receiverVO.setModule(_c2sTransferVO.getModule());
			_receiverVO.setCreatedDate(_currentDate);
			_receiverVO.setLastTransferOn(_currentDate);

			_senderMSISDN=(_channelUserVO.getUserPhoneVO()).getMsisdn();
			_receiverMSISDN=((ReceiverVO)_c2sTransferVO.getReceiverVO()).getMsisdn();
			_c2sTransferVO.setReceiverMsisdn(_receiverMSISDN);
			_c2sTransferVO.setReceiverNetworkCode(_receiverVO.getNetworkCode());
			_c2sTransferVO.setGrphDomainCode(_channelUserVO.getGeographicalCode());
			_c2sTransferVO.setSubService(p_requestVO.getReqSelector());
			_c2sTransferVO.setRequestStartTime(p_requestVO.getRequestStartTime());

			//check if receiver barred in PreTUPS or not, user should not be barred.
			try
			{
				PretupsBL.checkMSISDNBarred(con,_receiverMSISDN,_receiverVO.getNetworkCode(),_c2sTransferVO.getModule(),PretupsI.USER_TYPE_RECEIVER);
			}
			catch(BTSLBaseException be)
			{
				_log.errorTrace(METHOD_NAME,be);
				if(be.isKey() && (be.getMessageKey().equals(PretupsErrorCodesI.ERROR_RECEIVER_USERBARRED)))
					_c2sTransferVO.setReceiverReturnMsg(new BTSLMessages(PretupsErrorCodesI.ERROR_USERBARRED_R,new String[]{}));
				throw be;
			}	



			try
			{
				//ArrayList interfaceList=new InterfaceDAO().loadInterfaceList(con, _requestVO.getNetworkCode(), PretupsI.INTERFACE_CATEGORY, null);
				
				ArrayList serviceclassList=new ArrayList();

				String interfaceId="";
				if(Constants.getProperty(_requestVO.getEnquiryServiceType()+"_INTERFACEID").equalsIgnoreCase("")){
					interfaceId="INTID00001";

				}else{
					interfaceId=Constants.getProperty(_requestVO.getEnquiryServiceType()+"_INTERFACEID");
				}


				serviceclassList.addAll(new ServiceClassDAO().loadServiceClassDetails(con, interfaceId));

				if(serviceclassList!=null && serviceclassList.size()>0){

					ArrayList<ServiceTypeSelectorMappingVO> selectorList=null;
					ServiceTypeSelectorMappingDAO mappingDAO=new ServiceTypeSelectorMappingDAO();
					selectorList= mappingDAO.loadServiceSelectorMappingDetails(con,_requestVO.getEnquiryServiceType());

					TransferRulesVO rulesVO=null;
					for (Iterator iterator = selectorList.iterator(); iterator.hasNext();) {
						ServiceTypeSelectorMappingVO serviceTypeSelectorMappingVO = (ServiceTypeSelectorMappingVO) iterator.next();
						_c2sTransferVO.setSubService(serviceTypeSelectorMappingVO.getSelectorCode());
						rulesVO=null;
						try{
							for (Iterator iterator1 = serviceclassList.iterator(); iterator1.hasNext();) {
								ServiceClassVO serviceClassVO1 = (ServiceClassVO) iterator1.next();

								if(serviceClassVO1.getServiceClassCode().equalsIgnoreCase(PretupsI.ALL)){
									_receiverVO.setServiceClassCode(serviceClassVO1.getServiceClassId());
									rulesVO=PretupsBL.getServiceTransferRule(con,_c2sTransferVO,PretupsI.C2S_MODULE);

									if(rulesVO!=null)
										trfRuleList.add(rulesVO);
								}
							}

						}catch (Exception e) {
							rulesVO=null;
						}
					}
					boolean slabFlag=false;
					Iterator<TransferRulesVO> trfRuleItr=trfRuleList.iterator();
					while(trfRuleItr.hasNext())
					{
						transferRulesVO=trfRuleItr.next();
						if((transferRulesVO!=null && transferRulesVO.getCardGroupSetID()!=null)  )
						{
							if(transferRulesVO!=null)
								_transferVO.setCardGroupSetID(transferRulesVO.getCardGroupSetID());
							CardGroupDAO cardGroupDAO = new CardGroupDAO();
							_cardGroupDetailsVOList= cardGroupDAO.loadALLCardGroupSlab(con,_transferVO,_requestVO);

							try
							{
								if(_cardGroupDetailsVOList.size() > 0)
								{
									Iterator<CardGroupDetailsVO> itr = _cardGroupDetailsVOList.iterator();
									prepareFinalMessage(_cardGroupDetailsVOList);
									String[] arr=new String[1];
									String temp = "";
									while(itr.hasNext())
									{
										_cardGroupDetailsVO = (CardGroupDetailsVO)itr.next();
										if(_cardGroupDetailsVO!=null)
										{
											slabFlag=true;
											temp=_cardGroupDetailsVO.getCardName();
											_slabDetails =_slabDetails+temp+",";
											temp="";
										}
									}
									if (!BTSLUtil.isNullString(_slabDetails))
									{
										p_requestVO.setSlab(true);
										p_requestVO.setSlabDetails(_slabDetails);
										p_requestVO.setSlabAmount(_slabDetailsForAmount);
										p_requestVO.setSenderReturnMessage(_finalMessage);
										arr[0]=_slabDetails;
										p_requestVO.setMessageArguments(arr);
										p_requestVO.setMessageCode(PretupsErrorCodesI.CARDGROUP_ENQUIRY_SUCCESS);
									}
								}
							}
							catch(Exception e)
							{
								_log.errorTrace(METHOD_NAME, e);
							}

						} 
						else{
							p_requestVO.setMessageCode(PretupsErrorCodesI.CARD_GROUP_SET_IDNOT_FOUND);
							p_requestVO.setSuccessTxn(false);
							throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.CARD_GROUP_SET_IDNOT_FOUND);
						}

					}

					if(!slabFlag){
						p_requestVO.setMessageCode(PretupsErrorCodesI.CARD_GROUP_SET_IDNOT_FOUND);
						p_requestVO.setSuccessTxn(false);
						throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.CARD_GROUP_SET_IDNOT_FOUND);
					}
				}else{
					p_requestVO.setMessageCode(PretupsErrorCodesI.SERVICE_CLASS_CODE_NOT_FOUND_IN_SYSTEM);
					p_requestVO.setSuccessTxn(false);
					throw new BTSLBaseException(this,METHOD_NAME,PretupsErrorCodesI.SERVICE_CLASS_CODE_NOT_FOUND_IN_SYSTEM);
				}

			}
			catch(BTSLBaseException be)
			{
				_log.errorTrace(METHOD_NAME,be);        

				if(BTSLUtil.isNullString(_c2sTransferVO.getErrorCode()))
				{
					if(be.isKey())
						_c2sTransferVO.setErrorCode(be.getMessageKey());
					else
						_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
				}
				_log.error("ChannelUserCGEnquiryRequestHandler["+METHOD_NAME+"]","Getting BTSL Base Exception:"+be.getMessage());
				p_requestVO.setSuccessTxn(false);
				_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
				if(!BTSLUtil.isNullString(_c2sTransferVO.getSenderReturnMessage()))
					p_requestVO.setSenderReturnMessage(_c2sTransferVO.getSenderReturnMessage());            

				if(be.isKey())  //checking if base exception has key
				{
					if(_c2sTransferVO.getErrorCode()==null)
						_c2sTransferVO.setErrorCode(be.getMessageKey());
					p_requestVO.setMessageCode(be.getMessageKey());
					p_requestVO.setMessageArguments(be.getArgs());
				}
				else 
					_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);

				_log.errorTrace(METHOD_NAME,be);
				throw be;
			}
			catch(Exception e)
			{
				_log.errorTrace(METHOD_NAME,e);
				throw (BTSLBaseException)e;
			}

		}
		catch (BTSLBaseException be)
		{
			_log.errorTrace(METHOD_NAME,be);	
			if(BTSLUtil.isNullString(_c2sTransferVO.getErrorCode()))
			{
				if(be.isKey())
					_c2sTransferVO.setErrorCode(be.getMessageKey());
				else
					_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			}
			_log.error("ChannelUserCGEnquiryRequestHandler["+METHOD_NAME+"]","Getting BTSL Base Exception:"+be.getMessage());

			p_requestVO.setSuccessTxn(false);
			_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);

			if(!BTSLUtil.isNullString(_c2sTransferVO.getSenderReturnMessage()))
				p_requestVO.setSenderReturnMessage(_c2sTransferVO.getSenderReturnMessage());		

			if(be.isKey())  //checking if base exception has key
			{
				if(_c2sTransferVO.getErrorCode()==null)
					_c2sTransferVO.setErrorCode(be.getMessageKey());

				p_requestVO.setMessageCode(be.getMessageKey());
				p_requestVO.setMessageArguments(be.getArgs());
			}
			else //setting default error code if message and key is not found
				_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);

		}
		catch (Exception e)
		{
			_log.errorTrace(METHOD_NAME,e);
			if(BTSLUtil.isNullString(_c2sTransferVO.getErrorCode()))
				_c2sTransferVO.setErrorCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			p_requestVO.setSuccessTxn(false);
			_c2sTransferVO.setTransferStatus(PretupsErrorCodesI.TXN_STATUS_FAIL);
			p_requestVO.setMessageCode(PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
			_log.error(METHOD_NAME,"Exception:"+e.getMessage());
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"ChannelUserCGEnquiryRequestHandler["+METHOD_NAME+"]",_senderMSISDN,_senderMSISDN,_senderNetworkCode,"Exception:"+e.getMessage());

		} 
		finally
		{

			if (mcomCon != null) {
				mcomCon.close("ChannelUserCGEnquiryRequestHandler#process");
				mcomCon = null;
			}

			if(BTSLUtil.isNullString(p_requestVO.getMessageCode()))
				p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			if(_isCounterDecreased)
				p_requestVO.setDecreaseLoadCounters(false);			
			if (_log.isDebugEnabled())
				_log.debug(METHOD_NAME, " Exited ");


		}

	}//end of process


	private void populateVOFromRequest(RequestVO p_requestVO)
	{
		final String METHOD_NAME = "populateVOFromRequest";
		if(_log.isDebugEnabled()) _log.debug(METHOD_NAME,p_requestVO);
		_c2sTransferVO.setSenderVO(_channelUserVO);
		_c2sTransferVO.setRequestID(p_requestVO.getRequestIDStr());
		_c2sTransferVO.setModule(p_requestVO.getModule());
		_c2sTransferVO.setRequestGatewayCode(p_requestVO.getRequestGatewayCode());
		_c2sTransferVO.setRequestGatewayType(p_requestVO.getRequestGatewayType());
		_c2sTransferVO.setServiceType(p_requestVO.getEnquiryServiceType());
		_c2sTransferVO.setSourceType(p_requestVO.getSourceType());
		_c2sTransferVO.setCreatedOn(_currentDate);
		_c2sTransferVO.setCreatedBy(_channelUserVO.getUserID());
		_c2sTransferVO.setModifiedOn(_currentDate);
		_c2sTransferVO.setModifiedBy(_channelUserVO.getUserID());
		_c2sTransferVO.setTransferDateTime(_currentDate);
		_c2sTransferVO.setSenderMsisdn((_channelUserVO.getUserPhoneVO()).getMsisdn());
		_c2sTransferVO.setSenderID(_channelUserVO.getUserID());
		_c2sTransferVO.setNetworkCode(_channelUserVO.getNetworkID());
		_c2sTransferVO.setLocale(_senderLocale);
		_c2sTransferVO.setLanguage(_c2sTransferVO.getLocale().getLanguage());
		_c2sTransferVO.setCountry(_c2sTransferVO.getLocale().getCountry());
		_c2sTransferVO.setMsgGatewayFlowType(p_requestVO.getMessageGatewayVO().getFlowType());
		_c2sTransferVO.setMsgGatewayResponseType(p_requestVO.getMessageGatewayVO().getResponseType());
		_c2sTransferVO.setMsgGatewayTimeOutValue(p_requestVO.getMessageGatewayVO().getTimeoutValue());
		(_channelUserVO.getUserPhoneVO()).setLocale(_senderLocale);
		_c2sTransferVO.setReferenceID(p_requestVO.getExternalReferenceNum());	
	}


	public void prepareFinalMessage(ArrayList<CardGroupDetailsVO> p_arrayList) 
	{
		if (_log.isDebugEnabled())
			_log.debug("prepareFinalMessage", "Entered   p_arrayList " +p_arrayList );
		CardGroupDetailsVO _cardGroupDetailsVO=null;
		String sbf="";

		String subMsg;

		for(int i=0;i<p_arrayList.size();i++)
		{
			_cardGroupDetailsVO=(CardGroupDetailsVO)p_arrayList.get(i);
			sbf=sbf+_cardGroupDetailsVO.getCardGroupCode()+":"+_cardGroupDetailsVO.getStartRange()+":"+_cardGroupDetailsVO.getEndRange()+":"+_cardGroupDetailsVO.getCardName()+":"+_cardGroupDetailsVO.getServiceTypeSelector()+":"+_cardGroupDetailsVO.getCardGroupSubServiceId()+":"+_cardGroupDetailsVO.getValidityPeriodAsString()+":"+_cardGroupDetailsVO.getReversalPermitted()+",";	
		}

		subMsg=sbf.toString();
		if (subMsg!=null)
		{
			_finalMessage=_finalMessage+subMsg+",";
			_finalMessage=_finalMessage.substring(0, _finalMessage.length()-1);
			_finalMessage=_finalMessage.trim();
		}

		if (_log.isDebugEnabled())
			_log.debug("prepareFinalMessage", "Exited   _finalMessage " +_finalMessage );
	}


}

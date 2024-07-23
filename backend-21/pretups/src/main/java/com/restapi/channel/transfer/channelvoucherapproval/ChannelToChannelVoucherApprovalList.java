package com.restapi.channel.transfer.channelvoucherapproval;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.transfer.requesthandler.O2CTransferInitiateMappController;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.p2p.subscriber.requesthandler.TransfersReportController;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserStatusCache;
import com.btsl.user.businesslogic.UserStatusVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferRuleWebDAO;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;


public class ChannelToChannelVoucherApprovalList implements ServiceKeywordControllerI {
	 private static Log _log = LogFactory.getLog(TransfersReportController.class.getName());
	 private String _requestID = null;
	@SuppressWarnings("unchecked")
	@Override
	public void process(RequestVO p_requestVO) {
		
		 _requestID = p_requestVO.getRequestIDStr();
	        if (_log.isDebugEnabled()) {
	            _log.debug("process", _requestID, "Entered p_requestVO: " + p_requestVO);
	        }

	        final String methodName = "process";
			Connection con = null;
			MComConnectionI mcomCon = null;
			String approvalLevel = null ;
			String msisdn2 = null;
			String geographicalDomain = null;
			String Domain = null ;
			String Category = null;
			String pageNumber=null;
			String requestType=null;
			String entriesPerPage=null;
			String userNameSearch=null;
			String transactionId = "" ;
	        String requestMessage = p_requestVO.getRequestMessage();
	        
	        if(requestMessage != null && requestMessage.contains("\"transactionId\"") && !requestMessage.contains("\"transactionId\":null") && !requestMessage.contains("\"transactionId\":\"\"")) {
	        	transactionId =  requestMessage.substring(requestMessage.indexOf("\"transactionId\""));
	        	transactionId = transactionId.substring("\"transactionId\":\"".length());
	        	transactionId = transactionId.substring(0, transactionId.indexOf("\""));
	        }
	        
	        
	        Boolean fromUserCodeFlag = false;
	    	String allUser = "A";
	    	String allOrder = "ALL";
	    	String args[] = null;
	    	ArrayList<ChannelTransferVO> channelTransfersList = null;
			try {
				ChannelUserVO searchUserVO = new ChannelUserVO();
				mcomCon = new MComConnection();
				con = mcomCon.getConnection();
				String transferSubType = null;
				final HashMap requestMap = p_requestVO.getRequestMap();
				pageNumber=(String) requestMap.get("PAGENUMBER");
				requestType=(String) requestMap.get("REQUESTTYPE");
				entriesPerPage=(String) requestMap.get("ENTRIESPERPAGE");
				userNameSearch=(String) requestMap.get("USERNAMETOSEARCH");
				if(BTSLUtil.isNullString(requestType)) {
					requestType = "ALL";
				}
				    // getting deatils of the requester 
				final ChannelUserVO senderVO = (ChannelUserVO) p_requestVO.getSenderVO();
				GeographicalDomainDAO _geographyDAO = new GeographicalDomainDAO();
		            // load the geographies info from the user_geographies
		        ArrayList geographyList = _geographyDAO.loadUserGeographyList(con, senderVO.getUserID(), senderVO.getNetworkID());
		        senderVO.setGeographicalAreaList(geographyList);

		            // load the domain of the user that are associated with it
		        DomainDAO domainDAO = new DomainDAO();
		        senderVO.setDomainList(domainDAO.loadDomainListByUserId(con, senderVO.getUserID()));
				approvalLevel = (String) requestMap.get("APPROVALLEVEL");
				if(BTSLUtil.isNullString(approvalLevel)) {
					throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.C2C_ERROR_INVAILD_APPLEVEL);
				}
				msisdn2 = (String) requestMap.get("MSISDN2");
				transferSubType = (String) requestMap.get("TRANSFERTYPE");
				if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equalsIgnoreCase(transferSubType)){
					transferSubType = PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER;
				} else if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equalsIgnoreCase(transferSubType)) {
					transferSubType = PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER;
				}else {		
					throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.INVAILD_TRANSFERSUB_TYPE);
				}
				if(!BTSLUtil.isNullString(msisdn2)) {
					fromUserCodeFlag = true ;
				} else {
					geographicalDomain = (String) requestMap.get("GEOGRAPHICALDOMAIN");
					Domain = (String) requestMap.get("DOMAIN");
					Category = (String) requestMap.get("CATEGORY");
					if(BTSLUtil.isNullString(geographicalDomain) || BTSLUtil.isNullString(Domain) || BTSLUtil.isNullString(Category)) {
						throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.INVAILD_SEARCH_CRITERIA);
					}
					searchUserVO.setGeographicalDesc(geographicalDomain);
					if(PretupsI.ALL.equals(Domain)){
						searchUserVO.setDomainID(Domain);
					} else {
						searchUserVO.setDomainID(Domain);
						searchUserVO.setDomainName(Domain);
					}
                   if(PretupsI.ALL.equals(Category)){
                	   searchUserVO.setCategoryCode(Category);
					} else {
						 searchUserVO.setCategoryCode(Category);
						 searchUserVO.setCategoryCodeDesc(Category);
					}
				}
				// if user entered the mobile number then load the personal
				// information and the associated orders.
				if (fromUserCodeFlag) {
					searchUserVO = loadUserWithUserCode(msisdn2);
						 // to check the domain of the user with the domain of the logged in
						// user
						if (!BTSLUtil.isNullString(senderVO.getDomainID())) {
							if (!searchUserVO.getDomainID().equals(senderVO.getDomainID())) {
								args = new String[] { msisdn2 };
								throw new BTSLBaseException(this, methodName, "message.channeltransfer.transfernotallowed.usernotindomain",args);
							}
						} else {
							ListValueVO listValueVO = null;
							boolean domainfound = false;
							final ArrayList domainList = senderVO.getDomainList();
							for (int i = 0, j = domainList.size(); i < j; i++) {
								listValueVO = (ListValueVO) domainList.get(i);
								if (searchUserVO.getDomainID().equals(listValueVO.getValue())) {
									domainfound = true;
									break;
								}
							}
							if (!domainfound) {
							args = new String[] { msisdn2 };
							throw new BTSLBaseException(this, methodName, "message.channeltransfer.transfernotallowed.usernotindomain",args);
							}
						}
						
					
						// now check that is user down in the geographical domain of the
						// loggin user or not.
						
						final GeographicalDomainDAO geographicalDomainDAO = new GeographicalDomainDAO();
						if (!geographicalDomainDAO.isGeoDomainExistInHierarchy(con, searchUserVO.getGeographicalCode(), senderVO.getUserID())) {
							args = new String[] { msisdn2 };
							throw new BTSLBaseException(this, methodName, "message.channeltransfer.transfernotallowed.usernotdowngeogrphy",args);
						}
						//valiation for pagination
						if(!BTSLUtil.isNullString(pageNumber)&&!BTSLUtil.isNullString(entriesPerPage)) {
							if(!BTSLUtil.isNumeric(pageNumber) || !BTSLUtil.isNumeric(entriesPerPage) ){
								
								throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PAGINATION_VALUES_INVALID);

							}
							
							int pageNumber2=Integer.parseInt(pageNumber);
							int entriesPerPage2=Integer.parseInt(entriesPerPage);
							
							if(pageNumber2<0 || entriesPerPage2 <0 ) {
								
								throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PAGINATION_VALUES_INVALID);
								
							}
						}else {
							if(BTSLUtil.isNullString(pageNumber)&&!BTSLUtil.isNullString(entriesPerPage)) {
								
								throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PAGINATION_VALUES_REQ);

							}
							else if(BTSLUtil.isNullString(entriesPerPage)&&!BTSLUtil.isNullString(pageNumber)) {
							
								throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PAGINATION_VALUES_REQ);

							}
							
						}

						channelTransfersList = loadTransferApprovalListLevelN(transactionId, fromUserCodeFlag,approvalLevel,allOrder, allUser,transferSubType, searchUserVO,p_requestVO,pageNumber,entriesPerPage,userNameSearch,requestType);
						if(!BTSLUtil.isNullString(pageNumber)&&!BTSLUtil.isNullString(entriesPerPage) &BTSLUtil.isNullOrEmptyList(channelTransfersList)) {
							if(!BTSLUtil.isNullString(userNameSearch)) {
								throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_DO_NOT_EXIST,new String[]{"userName",userNameSearch});

							}
							else {
								throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_RECORD_PAGE,new String[]{pageNumber});

							}
						}
						p_requestVO.setChannelTransfersList(channelTransfersList);
	            		if(p_requestVO.getChannelTransfersList().size() >= 1){
	            			p_requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
	            		} else {
	            			if(!BTSLUtil.isNullString(userNameSearch)) {
								throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_DO_NOT_EXIST,new String[]{"userName",userNameSearch});

							}
	            			p_requestVO.setMessageCode(PretupsErrorCodesI.C2C_APPLIST_EMPTY);
	            		}
				}
				else {
					fromUserCodeFlag = false;
					// Setting all the search Users Related info 
					setSearchUserInfo(searchUserVO,senderVO,allUser);
					final CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
					if (!searchUserVO.getDomainID().equalsIgnoreCase(PretupsI.ALL)) {/*
						if(BTSLUtil.getOptionKey(searchUserVO.getDomainName(), searchUserVO.getDomainList()).getValue() == null){
							args = new String[] { searchUserVO.getDomainName() };
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVAILD_DOMAIN_NAME,args);
						}
						
					*/}
                    if (!searchUserVO.getCategoryCode().equalsIgnoreCase(PretupsI.ALL)) {/*
                    	if(BTSLUtil.getOptionKey(searchUserVO.getCategoryCodeDesc(), (List) searchUserVO.getCategoryList()).getValue() == null){
                    		args = new String[] { searchUserVO.getCategoryCodeDesc() };
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVAILD_CATEGORY_NAME,args);
                    	}
					*/}
                   
					if (searchUserVO.getDomainList() != null) {
						searchUserVO.setDomainName((BTSLUtil.getOptionDesc(searchUserVO.getDomainID(), searchUserVO.getDomainList())).getLabel());
					}
					if (searchUserVO.getCategoryList() != null) {
						searchUserVO.setCategoryName((BTSLUtil.getOptionDesc(searchUserVO.getCategoryCode(), (List) searchUserVO.getCategoryList())).getLabel());
					}
					if (searchUserVO.getGeographicalList() != null) {
						if(searchUserVO.getGeographicalDesc() != null && !searchUserVO.getGeographicalDesc().equalsIgnoreCase(BTSLUtil.getOptionKey(searchUserVO.getCategoryCodeDesc(), (List) searchUserVO.getGeographicalList()).getLabel())){
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVAILD_GEOGRAPHY_NAME);
						}
						searchUserVO.setGeographicalCode(BTSLUtil.getOptionKey(searchUserVO.getCategoryCodeDesc(), (List) searchUserVO.getGeographicalList()).getValue());
						searchUserVO.setGeographicalDesc((BTSLUtil.getOptionDesc(searchUserVO.getGeographicalCode(), searchUserVO.getGeographicalList())).getLabel());
						searchUserVO.setgeographicalCodeforNewuser((BTSLUtil.getOptionDesc(searchUserVO.getGeographicalCode(), searchUserVO.getGeographicalList())).getLabel());
					}
					
					
					//valiation for pagination
					if(!BTSLUtil.isNullString(pageNumber)&&!BTSLUtil.isNullString(entriesPerPage)) {
						if(!BTSLUtil.isNumeric(pageNumber) || !BTSLUtil.isNumeric(entriesPerPage) ){
							
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PAGINATION_VALUES_INVALID);

						}
						
						int pageNumber2=Integer.parseInt(pageNumber);
						int entriesPerPage2=Integer.parseInt(entriesPerPage);
						
						if(pageNumber2<0 || entriesPerPage2 <0 ) {
							
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PAGINATION_VALUES_INVALID);
							
						}
					}else {
						if(BTSLUtil.isNullString(pageNumber)&&!BTSLUtil.isNullString(entriesPerPage)) {
							
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PAGINATION_VALUES_REQ);

						}
						else if(BTSLUtil.isNullString(entriesPerPage)&&!BTSLUtil.isNullString(pageNumber)) {
						
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.PAGINATION_VALUES_REQ);

						}
						
					}
					
					if (!searchUserVO.getDomainID().equalsIgnoreCase(PretupsI.ALL)) {
						final CategoryVO categoryVO = categoryWebDAO.loadOwnerCategory(con, searchUserVO.getDomainID());
						String catg = null;
						if (searchUserVO.getCategoryCode() != null && searchUserVO.getCategoryCode().indexOf(":") > 0) {
							catg = searchUserVO.getCategoryCode().substring(searchUserVO.getCategoryCode().indexOf(":") + 1);
						}
						if (categoryVO != null) {
							searchUserVO.setOwnerCategoryName(categoryVO.getCategoryCode());
							//searchUserVO.setChannelOwnerCategoryDesc(categoryVO.getCategoryName());
						}
						searchUserVO.setUserName(null);
					//	searchUserVO.setChannelOwnerCategoryUserName(null);
						/*
						 * Note: if selected category is himself owner of channel
						 * then only one search will be appear
						 * on the screen this will control by owner same flag
						 */
						if (categoryVO != null && categoryVO.getCategoryCode().equals(catg)) {
							//searchUserVO.setOwnerSame(true);
							searchUserVO.setOwnerName(PretupsI.ALL);
						}
					} else {
						searchUserVO.setOwnerCategoryName(PretupsI.ALL);
						//searchUserVO.setChannelOwnerCategoryDesc(PretupsI.ALL);

					}
					/*
					 * if all user is selected then no need to go to the search user
					 * jsp go directly on the jsp which
					 * contains all orders of all the users of the selected
					 * category.
					 */

					if ("A".equals(allUser)) {
						allOrder = PretupsI.ALL;
					   // searchUserVO.setAllOrder(PretupsI.ALL);
						searchUserVO.setUserName(PretupsI.ALL);
						searchUserVO.setUserID(PretupsI.ALL);
						searchUserVO.setOwnerID(null);
						
						}
					channelTransfersList = loadTransferApprovalListLevelN(transactionId, fromUserCodeFlag,approvalLevel,allOrder, allUser,transferSubType, searchUserVO,p_requestVO,pageNumber,entriesPerPage,userNameSearch,requestType);
					if(!BTSLUtil.isNullString(pageNumber)&&!BTSLUtil.isNullString(entriesPerPage) &BTSLUtil.isNullOrEmptyList(channelTransfersList)) {
						if(!BTSLUtil.isNullString(userNameSearch)) {
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_DO_NOT_EXIST,new String[]{"userName",userNameSearch});

						}
						else {
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_RECORD_PAGE,new String[]{pageNumber});

						}

						
						
					}
					p_requestVO.setChannelTransfersList(channelTransfersList);
					
            		if(p_requestVO.getChannelTransfersList().size() >= 1){
            			p_requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
            		} else {
            			if(!BTSLUtil.isNullString(userNameSearch)) {
							throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.USER_DO_NOT_EXIST,new String[]{"userName",userNameSearch});

						}
            			p_requestVO.setMessageCode(PretupsErrorCodesI.C2C_APPLIST_EMPTY);
            		}
				}
		       
			} catch (BTSLBaseException e) {
				p_requestVO.setSuccessTxn(false);
	            p_requestVO.setMessageCode(e.getMessageKey());
	            _log.error("process", "BTSLBaseException " + e.getMessage());
	            _log.errorTrace(methodName, e);
	            if (e.isKey()) {
	                p_requestVO.setMessageCode(e.getMessageKey());
	                p_requestVO.setMessageArguments(e.getArgs());
	            } else {
	                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
	                return;
	            }
			} catch (Exception e) {
			    p_requestVO.setSuccessTxn(false);
	            _log.error("process", "BTSLBaseException " + e.getMessage());
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "SubscriberVoucherInquiryController[process]", "", "", "",
	                            "Exception:" + e.getMessage());
	            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
			}
			finally 
			{
				if (mcomCon != null) {
					mcomCon.close("TransfersReportController#process");
					mcomCon = null;
				}
	            if (_log.isDebugEnabled()) {
	                _log.debug("process", _requestID, " Exited ");
	            }
			}
			
}
	/**
	 * Method setSearchUserInfo.
	 * This is the authorization method of the level1 approval.
	 * @param allUser 
	 * 
	 * @param mapping
	 *            
	 * @param form
	 *            
	 * @param request
	 *            
	 * @param response
	 *            
	 * @return 
	 * @throws BTSLBaseException 
	 */
	public void setSearchUserInfo(ChannelUserVO searchUserVO,ChannelUserVO senderVO, String allUser) throws BTSLBaseException {
		final String methodName = "setSearchUserInfo";
		if (_log.isDebugEnabled()) {
			_log.debug("setSearchUserInfo", "Entered");
		}
		Connection con = null;MComConnectionI mcomCon = null;
		try {
			final ArrayList userGeoList = senderVO.getGeographicalAreaList();
			final ArrayList geoList = new ArrayList();
			UserGeographiesVO geographyVO = null;
			ListValueVO listValueVO = null;
			searchUserVO.setNetworkCode(senderVO.getNetworkCode());
			searchUserVO.setNetworkName(senderVO.getNetworkName());
			
			for (int i = 0, k = userGeoList.size(); i < k; i++) {
				geographyVO = (UserGeographiesVO) userGeoList.get(i);
				geoList.add(new ListValueVO(geographyVO.getGraphDomainName(), geographyVO.getGraphDomainCode()));
			}
			// if there is only one geographies associated with user then there
			// will be
			// no drop down will appear on the screen. just dispaly the
			// geography
			if (geoList.size() == 1) {
				listValueVO = (ListValueVO) geoList.get(0);
				searchUserVO.setGeographicalCode(listValueVO.getValue());
				searchUserVO.setGeographicalDesc(listValueVO.getLabel());
				searchUserVO.setgeographicalCodeforNewuser(listValueVO.getLabel());
				if(searchUserVO.getGeographicalDesc() != null && !searchUserVO.getGeographicalDesc().equalsIgnoreCase(listValueVO.getLabel())){
					throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.INVAILD_GEOGRAPHY_NAME);
				}
			} else {
				searchUserVO.setGeographicalList(geoList);
			}

			// if there is only one domains associated with user then there will
			// be
			// no drop down will appear on the screen. just dispaly the domian
			// load the user domain list
			mcomCon = new MComConnection();con=mcomCon.getConnection();
			ArrayList domList = senderVO.getDomainList();
			// if domain list is empty or null then check if domains are allowed
			// and having fixed domains
			// then load the domains list form the session
			if ((domList == null || domList.isEmpty()) && PretupsI.YES.equals(senderVO.getCategoryVO().getDomainAllowed()) && PretupsI.DOMAINS_FIXED.equals(senderVO
					.getCategoryVO().getFixedDomains())) {
				domList = new DomainDAO().loadCategoryDomainList(con);
			}
			boolean loadFilteredCategory = false;
			domList = BTSLUtil.displayDomainList(domList);
			if (domList.size() == 1) {
				listValueVO = (ListValueVO) domList.get(0);
				searchUserVO.setDomainID(listValueVO.getValue());
				searchUserVO.setDomainName(listValueVO.getLabel());
				loadFilteredCategory = true;
			} else {
				searchUserVO.setDomainList(domList);
			}

			final ChannelTransferRuleWebDAO channelTransferRuleWebDAO = new ChannelTransferRuleWebDAO();
			// load the category list we have to load all user category with in
			// that network.
			final ArrayList catgList = channelTransferRuleWebDAO.loadTransferRulesCategoryList(con, senderVO.getNetworkID(), PretupsI.OPERATOR_TYPE_OPT);
			final ArrayList catgeoryList = new ArrayList();
			// if there is only one domain then all associated category will be
			// populated earlier
			// else they will reflect there when domain changes
			ChannelTransferRuleVO rulesVO = null;
			for (int i = 0, k = catgList.size(); i < k; i++) {
				rulesVO = (ChannelTransferRuleVO) catgList.get(i);
				// get only those categories having transfer allowed YES.
				if (PretupsI.YES.equals(rulesVO.getTransferAllowed())) {
					if (loadFilteredCategory) {
						if (rulesVO.getDomainCode().equals(searchUserVO.getDomainID())) {
							catgeoryList.add(new ListValueVO(rulesVO.getToCategoryDes(), rulesVO.getDomainCode() + ":" + rulesVO.getToCategory()));
						}
					} else {
						catgeoryList.add(new ListValueVO(rulesVO.getToCategoryDes(), rulesVO.getDomainCode() + ":" + rulesVO.getToCategory()));
					}
				}
			}
			allUser = "A";
			searchUserVO.setCategoryList(catgeoryList);

		} catch (BTSLBaseException e) {
			_log.error("setSearchUserInfo", "BTSLBaseException:e=" + e);
			_log.errorTrace(methodName, e);
			throw e;
		}  catch (Exception e) {
			_log.error("setSearchUserInfo", "Exception:e=" + e);
			_log.errorTrace(methodName, e);
			throw new BTSLBaseException(e);
		} finally {
			if (mcomCon != null) {
				mcomCon.close("ChannelToChannelVoucherApprovalList#setSearchUserInfo");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug("setSearchUserInfo", "Exiting ");
			}
		}
	}
	
	
	/**
	 * Method loadUserWithUserCode.
	 * 
	 *            
	 * @param ChannelUserVO
	 *            
	 * @param String
	 *            
	 * @return void
	 * @throws BTSLBaseException 
	 */
	private ChannelUserVO loadUserWithUserCode(String userCode) throws BTSLBaseException {
		final String methodName = "loadUserWithUserCode";
		if (_log.isDebugEnabled()) {
			_log.debug("loadUSerWithUserCode", "Entered");
		}
		Connection con = null;
		MComConnectionI mcomCon = null;
		int receiverStatusAllowed = 0;
		ChannelUserVO channelUserVO = new ChannelUserVO();
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			final Date curDate = new Date();
			final UserDAO userDAO = new UserDAO();
			UserPhoneVO phoneVO = null;
			String args[] = null;
			 Boolean isPrimaryNumber = true;
			// Added by Amit Raheja for NNP changes
			
			// Addition ends
			if (!BTSLUtil.isNullString(userCode)) {
				phoneVO = userDAO.loadUserAnyPhoneVO(con, userCode);
			}
			if (phoneVO == null) {
				args = new String[] {userCode};
				throw new BTSLBaseException(this, methodName, "message.channeltransfer.userdetailnotfound.msg",args);
			}
			if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SECONDARY_NUMBER_ALLOWED)).booleanValue() && ("N".equalsIgnoreCase(phoneVO.getPrimaryNumber()))) {
				channelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, phoneVO.getUserId(), false, curDate,false);
				if (channelUserVO == null) {
					args = new String[] { userCode };
					throw new BTSLBaseException(this, methodName,"message.channeltransfer.userdetailnotfound.msg",args);
				}
				channelUserVO.setPrimaryMsisdn(channelUserVO.getMsisdn());
				channelUserVO.setMsisdn(userCode);
				isPrimaryNumber = false;
			} else {
				channelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, userCode, true, curDate,false);
			}
			if (channelUserVO == null) {
				args = new String[] { userCode };
				throw new BTSLBaseException(this, methodName,"message.channeltransfer.userdetailnotfound.msg",args);
			} else {
				final UserStatusVO userStatusVO = (UserStatusVO) UserStatusCache.getObject(channelUserVO.getNetworkID(), channelUserVO.getCategoryCode(), channelUserVO
						.getUserType(), PretupsI.REQUEST_SOURCE_TYPE_WEB);
				if (userStatusVO != null) {
					final String userStatusAllowed = userStatusVO.getUserReceiverAllowed();
					final String status[] = userStatusAllowed.split(",");
					for (int i = 0; i < status.length; i++) {
						if (status[i].equals(channelUserVO.getStatus())) {
							receiverStatusAllowed = 1;
						}
					}
				} else {
					args = new String[] { userCode };
					throw new BTSLBaseException(this, methodName,"message.channeltransfer.usernotallowed.msg",args);
				}
			}
			if (receiverStatusAllowed == 0) {
				args = new String[] { userCode };
				throw new BTSLBaseException(this, methodName,"message.channeltransfer.usersuspended.msg",args);
			} else if (channelUserVO.getCommissionProfileApplicableFrom().after(curDate)) {
				args = new String[] { userCode };
				throw new BTSLBaseException(this, methodName,"message.channeltransfer.usernocommprofileapplicable.msg",args);
			} else if (!PretupsI.YES.equals(channelUserVO.getCommissionProfileStatus())) {
				/*final Locale locale = BTSLUtil.getBTSLLocale(request);
				args = new String[] { userCode, channelUserVO.getCommissionProfileLang2Msg() };
				final LocaleMasterVO localeVO = LocaleMasterCache.getLocaleDetailsFromlocale(locale);
				if (PretupsI.LANG1_MESSAGE.equals(localeVO.getMessage())) {
					args = new String[] { userCode, channelUserVO.getCommissionProfileLang1Msg() };
				}
				throw new BTSLBaseException(this, methodName,"commissionprofile.notactive.msg");*/
			} else if (!PretupsI.YES.equals(channelUserVO.getTransferProfileStatus())) {
				args = new String[] { userCode };
				throw new BTSLBaseException(this, methodName,"transferprofile.notactive.msg",args);
			}

			// to check user status
			if (channelUserVO.getInSuspend() != null && PretupsI.USER_TRANSFER_IN_STATUS_SUSPEND.equals(channelUserVO.getInSuspend())) {
				final BTSLMessages messages = new BTSLMessages("transferprofile.notactive.msg", "searchadomain");
			}
		} catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.error(methodName, "Exception in Getting User Information With User Code" + e.getMessage());
			}
			_log.errorTrace(methodName, e);
	          throw new BTSLBaseException(this, methodName, e.getMessage());
		} finally {
			if (mcomCon != null) {
				mcomCon.close("ChannelToChannelVoucherApprovalList#loadUserWithUserCode");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Exiting ....");
			}
		}
		return channelUserVO;
	}
	/**
	 * Load the transfer Approval list for level one
	 * @param p_requestVO 
	 * @param searchUserVO 
	 * @param approvalLevel 
	 * @param fromUserCodeFlag 
	 * @param requestType 
	 * @return 
	 * @return 
	 * @throws BTSLBaseException 
	 */
	public  ArrayList<ChannelTransferVO> loadTransferApprovalListLevelN(String transactionId, Boolean fromUserCodeFlag, String approvalLevel,String allOrder,String allUser,String transferSubType, ChannelUserVO searchUserVO, RequestVO p_requestVO,String pageNumber,String entriesPerPage,String userNameSearch, String requestType) throws BTSLBaseException {
 		final String methodName = "loadTransferApprovalListLevelN";
		if (_log.isDebugEnabled()) {
			
			_log.debug("loadTransferApprovalListLevelN", "Entered");
		}
		Connection con = null;MComConnectionI mcomCon = null;
		String currentApprovalLevel = null;
		ArrayList channelTransferVOList = new ArrayList();
		ArrayList list = null;
		
		try {
			mcomCon = new MComConnection();con=mcomCon.getConnection();
			final ChannelUserVO senderVO = (ChannelUserVO) p_requestVO.getSenderVO();
			if (!fromUserCodeFlag && !"A".equals(allUser) /*&& !theForm.getApprovalDone() )*/) {
				validateSearchedUser(con,searchUserVO,allOrder,allUser,senderVO);
			}
			 if(BTSLUtil.isNullString(requestType)) {
		        	requestType = "ALL";
		        }
		
			searchUserVO.setNetworkName(senderVO.getNetworkName());

			final ChannelTransferDAO transferDAO = new ChannelTransferDAO();
			// load the channeltransfer list whose status is new and apprv1
			// in this we donnot load the the items list of all items. items of
			// selcted transfer
			// will load only when we view the order or on the confirmation
			// screen
			String catg = searchUserVO.getCategoryCode();
			if (searchUserVO.getCategoryCode() != null && searchUserVO.getCategoryCode().indexOf(":") > 0) {
				catg = searchUserVO.getCategoryCode().substring(searchUserVO.getCategoryCode().indexOf(":") + 1);
			}
			String userID = searchUserVO.getUserID();
			if (PretupsI.ALL.equals(allOrder)) {
				userID = PretupsI.ALL;
			}
			String ownerID = searchUserVO.getOwnerID();
		//	String ownerID = searchUserVO.getChannelOwnerCategoryUserID();  <--- verify it 
			if (BTSLUtil.isNullString(ownerID) || "NA".equalsIgnoreCase(ownerID)) {
				ownerID = PretupsI.ALL;
			}
			String geoCode = null;
			if (BTSLUtil.isNullString(searchUserVO.getUserCode())) {
				geoCode = searchUserVO.getGeographicalCode();
			}
			searchUserVO.setDomainName(senderVO.getDomainName());
			searchUserVO.setCategoryName(senderVO.getCategoryVO().getCategoryName());
			// ends here
			
			if(PretupsI.CHANNEL_TRANSFER_SUB_TYPE_TRANSFER.equals(transferSubType)) {
				switch (Integer.parseInt(approvalLevel)) {
			case 1:
				if(transactionId == null || transactionId.trim().equalsIgnoreCase("")) {
				list = transferDAO.loadChannelC2CStockTransfersList(con, userID, PretupsI.CHANNEL_TRANSFER_ORDER_NEW, senderVO.getNetworkID(), senderVO
						.getNetworkID(), searchUserVO.getDomainID(), geoCode, senderVO.getUserID(), PretupsI.TRANSFER_CATEGORY_SALE, catg, ownerID,pageNumber,entriesPerPage,userNameSearch);
				}else {
					list = transferDAO.loadChannelC2CStockTransfersListTransactionId(con, transactionId, userID, PretupsI.CHANNEL_TRANSFER_ORDER_NEW, senderVO.getNetworkID(), senderVO
							.getNetworkID(), searchUserVO.getDomainID(), geoCode, senderVO.getUserID(), PretupsI.TRANSFER_CATEGORY_SALE, catg, ownerID,pageNumber,entriesPerPage,userNameSearch);
					
				}
				break;
			case 2:
				if(transactionId == null || transactionId.trim().equalsIgnoreCase("")) {

				list = transferDAO.loadChannelC2CStockTransfersList(con, userID, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1, senderVO.getNetworkID(), senderVO
						.getNetworkID(), searchUserVO.getDomainID(), geoCode, senderVO.getUserID(), PretupsI.TRANSFER_CATEGORY_SALE, catg, ownerID,pageNumber,entriesPerPage,userNameSearch);
				}else {
					list = transferDAO.loadChannelC2CStockTransfersListTransactionId(con, transactionId, userID, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1, senderVO.getNetworkID(), senderVO
							.getNetworkID(), searchUserVO.getDomainID(), geoCode, senderVO.getUserID(), PretupsI.TRANSFER_CATEGORY_SALE, catg, ownerID,pageNumber,entriesPerPage,userNameSearch);
					
				}
				break;
			case 3:
				if(transactionId == null || transactionId.trim().equalsIgnoreCase("")) {

				list = transferDAO.loadChannelC2CStockTransfersList(con, userID, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2, senderVO.getNetworkID(), senderVO
						.getNetworkID(), searchUserVO.getDomainID(), geoCode, senderVO.getUserID(), PretupsI.TRANSFER_CATEGORY_SALE, catg, ownerID,pageNumber,entriesPerPage,userNameSearch);
				}else {
					list = transferDAO.loadChannelC2CStockTransfersListTransactionId(con, transactionId, userID, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2, senderVO.getNetworkID(), senderVO
							.getNetworkID(), searchUserVO.getDomainID(), geoCode, senderVO.getUserID(), PretupsI.TRANSFER_CATEGORY_SALE, catg, ownerID,pageNumber,entriesPerPage,userNameSearch);
					
				}break;
		    default:
		    	throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.C2C_ERROR_INVAILD_APPLEVEL);
				}
								
			} else if (PretupsI.CHANNEL_TRANSFER_SUB_TYPE_VOUCHER.equals(transferSubType)) {
			switch (Integer.parseInt(approvalLevel)) {
			case 1:
				if(transactionId == null || transactionId.trim().equalsIgnoreCase("")) {
				list = transferDAO.loadChannelC2CVoucherTransfersList(con, userID, PretupsI.CHANNEL_TRANSFER_ORDER_NEW, senderVO.getNetworkID(), senderVO
						.getNetworkID(), searchUserVO.getDomainID(), geoCode, senderVO.getUserID(), PretupsI.TRANSFER_CATEGORY_SALE, catg, ownerID,pageNumber,entriesPerPage,userNameSearch);
				}else {
					list = transferDAO.loadChannelC2CVoucherTransfersListTransactionId(con, transactionId, userID, PretupsI.CHANNEL_TRANSFER_ORDER_NEW, senderVO.getNetworkID(), senderVO
							.getNetworkID(), searchUserVO.getDomainID(), geoCode, senderVO.getUserID(), PretupsI.TRANSFER_CATEGORY_SALE, catg, ownerID,pageNumber,entriesPerPage,userNameSearch);
						
				}
				
				
				break;
			case 2:
				if(transactionId == null || transactionId.trim().equalsIgnoreCase("")) {
				list = transferDAO.loadChannelC2CVoucherTransfersList(con, userID, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1, senderVO.getNetworkID(), senderVO
						.getNetworkID(), searchUserVO.getDomainID(), geoCode, senderVO.getUserID(), PretupsI.TRANSFER_CATEGORY_SALE, catg, ownerID,pageNumber,entriesPerPage,userNameSearch);
				}else {
					list = transferDAO.loadChannelC2CVoucherTransfersListTransactionId(con, transactionId, userID, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE1, senderVO.getNetworkID(), senderVO
							.getNetworkID(), searchUserVO.getDomainID(), geoCode, senderVO.getUserID(), PretupsI.TRANSFER_CATEGORY_SALE, catg, ownerID,pageNumber,entriesPerPage,userNameSearch);
					
				}
				break;
			case 3:
				if(transactionId == null || transactionId.trim().equalsIgnoreCase("")) {
				list = transferDAO.loadChannelC2CVoucherTransfersList(con, userID, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2, senderVO.getNetworkID(), senderVO
						.getNetworkID(), searchUserVO.getDomainID(), geoCode, senderVO.getUserID(), PretupsI.TRANSFER_CATEGORY_SALE, catg, ownerID,pageNumber,entriesPerPage,userNameSearch);
				}else {
					list = transferDAO.loadChannelC2CVoucherTransfersListTransactionId(con, transactionId, userID, PretupsI.CHANNEL_TRANSFER_ORDER_APPROVE2, senderVO.getNetworkID(), senderVO
							.getNetworkID(), searchUserVO.getDomainID(), geoCode, senderVO.getUserID(), PretupsI.TRANSFER_CATEGORY_SALE, catg, ownerID,pageNumber,entriesPerPage,userNameSearch);
					
				}
				
				break;
		    default:
		    	throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.C2C_ERROR_INVAILD_APPLEVEL);
					
			} 
		}
			ChannelTransferVO transferVO = null;
			if("ALL".equalsIgnoreCase(requestType)) {
				return list;
			}
			
			if(list != null && list.size() >0 ) {
				 channelTransferVOList = new ArrayList();
			for(int i = 0 ; i<list.size();i++) {
				transferVO = (ChannelTransferVO) list.get(i);
				if("BUY".equalsIgnoreCase(requestType) && "BUY".equalsIgnoreCase(transferVO.getTransferType())) {
	                channelTransferVOList.add(transferVO);
	                } else if ("TRANSFER".equalsIgnoreCase(requestType) && "TRANSFER".equalsIgnoreCase(transferVO.getTransferType())) {
	                	channelTransferVOList.add(transferVO);
	                }
				}
				 
			}
		} catch (Exception e) {
			_log.error("loadTransferApprovalListLevelN", "Exception:e=" + e);
			_log.errorTrace(methodName, e);
	          throw new BTSLBaseException(this, methodName, e.getMessage() );
		} finally {
			if (mcomCon != null) {
				mcomCon.close("ChannelTransferApprovalAction#loadTransferApprovalListLevelN");
				mcomCon = null;
			}
			if (_log.isDebugEnabled()) {
				_log.debug("loadTransferApprovalListLevelN", "Exiting " );
			}
		}
		return channelTransferVOList;
	}
	/**
	 * Method validateSearchedUser.
	 * @param allUser 
	 * @param allOrder 
	 * @param searchUserVO 
	 * @param senderVO 
	 * 
	 * @param mapping
	 *            ActionMapping
	 * @param form
	 *            ActionForm
	 * @param request
	 *            HttpServletRequest
	 * @return ActionForward
	 */
	private void validateSearchedUser(Connection p_con, ChannelUserVO searchUserVO, String allOrder, String allUser, ChannelUserVO senderVO) {/*
		if (_log.isDebugEnabled()) {
			_log.debug("validateSearchedUser", "Entered");
		}
		final //ActionForward forward = null;
		final String methodName = "validateSearchedUser";
		ChannelUserWebDAO channelUserWebDAO = null;
		try {
			channelUserWebDAO = new ChannelUserWebDAO();

			
			 * if user directly enter the user name . then first check that
			 * owner search is required or not.
			 * if required then first owner user then after it search the user.
			 * if more than one user is there then display the error message.
			 
			ListValueVO listValueVO = null;
			String userName = null;
			ArrayList userList = null;
			if (!theForm.getOwnerSame()) {
				userName = theForm.getChannelOwnerCategoryUserName();
				if (!BTSLUtil.isNullString(userName)) {
					userName = "%" + userName + "%";
				}
				userList = channelUserWebDAO.loadCategoryUsersWithinGeoDomainHirearchy(p_con, theForm.getChannelOwnerCategory(), theForm.getNetworkCode(), userName, null,
						theForm.getGeographicDomainCode(), sessionUserVO.getUserID());
				if (userList.size() == 1) {
					listValueVO = (ListValueVO) userList.get(0);
					theForm.setChannelOwnerCategoryUserName(listValueVO.getLabel());
					theForm.setChannelOwnerCategoryUserID(listValueVO.getValue());
				} else if (userList.size() > 1) {
					boolean isExist = false;
					if (!BTSLUtil.isNullString(theForm.getChannelOwnerCategoryUserID())) {
						for (int i = 0, k = userList.size(); i < k; i++) {
							listValueVO = (ListValueVO) userList.get(i);
							if (listValueVO.getValue().equals(theForm.getChannelOwnerCategoryUserID()) && (theForm.getChannelOwnerCategoryUserName().compareTo(
									listValueVO.getLabel()) == 0)) {
								theForm.setChannelOwnerCategoryUserName(listValueVO.getLabel());
								theForm.setChannelOwnerCategoryUserID(listValueVO.getValue());
								isExist = true;
								break;
							}
						}
					} else {
						ListValueVO listValueNextVO = null;
						for (int i = 0, k = userList.size(); i < k; i++) {
							listValueVO = (ListValueVO) userList.get(i);
							if (theForm.getChannelOwnerCategoryUserName().compareTo(listValueVO.getLabel()) == 0) {
								if ((i + 1) < k) {
									listValueNextVO = (ListValueVO) userList.get(i + 1);
									if (theForm.getChannelOwnerCategoryUserName().compareTo(listValueNextVO.getLabel()) == 0) {
										isExist = false;
										break;
									}
									theForm.setChannelOwnerCategoryUserName(listValueVO.getLabel());
									theForm.setChannelOwnerCategoryUserID(listValueVO.getValue());
									isExist = true;
									break;
								}
								theForm.setChannelOwnerCategoryUserName(listValueVO.getLabel());
								theForm.setChannelOwnerCategoryUserID(listValueVO.getValue());
								isExist = true;
								break;
							}
						}
					}
					if (!isExist) {
						final String arr[] = { theForm.getChannelOwnerCategoryUserName() };
						final BTSLMessages messages = new BTSLMessages("message.channeltransfer.usermorethanoneexist.msg", arr, "usersearch");
						return super.handleMessage(messages, request, mapping);
					}

				} else {
					final BTSLMessages messages = new BTSLMessages("message.channeltransfer.usernotfound.msg", new String[] { theForm.getChannelOwnerCategoryUserName() },
							"usersearch");
					return super.handleMessage(messages, request, mapping);
				}
			}
			// now check the user
			String catg = null;
			if (theForm.getCategoryCode() != null && theForm.getCategoryCode().indexOf(":") > 0) {
				catg = theForm.getCategoryCode().substring(theForm.getCategoryCode().indexOf(":") + 1);
			}
			userName = theForm.getUserName();
			if (!BTSLUtil.isNullString(userName)) {
				if (userName.equalsIgnoreCase(PretupsI.ALL)) {
					userName = "%";
				} else {
					userName = "%" + userName + "%";
				}
			}
			
			 * As disscussed with AC, GSB,Sanjay Sir we have to load
			 * owner/parent user of status Y, S, SR
			 * but the child user must be of status Y. this is handled in the
			 * DAO's method of loading users.
			 * But if child user is the owner user then it should be only of
			 * status Y so for this we are
			 * assignning the NA value to the owner user ID
			 
			if (theForm.getOwnerSame() && BTSLUtil.isNullString(theForm.getChannelOwnerCategoryUserID())) {
				theForm.setChannelOwnerCategoryUserID("NA");
			}

			userList = channelUserWebDAO.loadCategoryUsersWithinGeoDomainHirearchy(p_con, catg, theForm.getNetworkCode(), userName, theForm.getChannelOwnerCategoryUserID(),
					theForm.getGeographicDomainCode(), sessionUserVO.getUserID());
			boolean isExist = false;
			if (userList.size() == 1) {
				listValueVO = (ListValueVO) userList.get(0);
				theForm.setUserID(listValueVO.getValue());
				theForm.setUserName(listValueVO.getLabel());
			} else if (userList.size() > 1) {
				if (PretupsI.ALL.equalsIgnoreCase(theForm.getUserName())) {
					theForm.setUserID(PretupsI.ALL);
					theForm.setAllOrder(PretupsI.ALL);
					isExist = true;
				} else {
					if (!BTSLUtil.isNullString(theForm.getUserID())) {
						for (int i = 0, k = userList.size(); i < k; i++) {
							listValueVO = (ListValueVO) userList.get(i);
							if (listValueVO.getValue().equals(theForm.getUserID()) && (theForm.getUserName().compareTo(listValueVO.getLabel()) == 0)) {
								theForm.setUserID(listValueVO.getValue());
								theForm.setUserName(listValueVO.getLabel());
								isExist = true;
								break;
							}
						}
					} else {
						ListValueVO listValueNextVO = null;
						for (int i = 0, k = userList.size(); i < k; i++) {
							listValueVO = (ListValueVO) userList.get(i);
							if (theForm.getUserName().compareTo(listValueVO.getLabel()) == 0) {
								if ((i + 1) < k) {
									listValueNextVO = (ListValueVO) userList.get(i + 1);
									if (theForm.getUserName().compareTo(listValueNextVO.getLabel()) == 0) {
										isExist = false;
										break;
									}
									theForm.setUserID(listValueVO.getValue());
									theForm.setUserName(listValueVO.getLabel());
									isExist = true;
									break;
								}
								theForm.setUserID(listValueVO.getValue());
								theForm.setUserName(listValueVO.getLabel());
								isExist = true;
								break;
							}
						}
					}
				}
				if (!isExist) {
					final String arr[] = { theForm.getUserName() };
					final BTSLMessages messages = new BTSLMessages("message.channeltransfer.usermorethanoneexist.msg", arr, "usersearch");
					return super.handleMessage(messages, request, mapping);
				}
			} else {
				final BTSLMessages messages = new BTSLMessages("message.channeltransfer.usernotfound.msg", new String[] { theForm.getUserName() }, "usersearch");
				return super.handleMessage(messages, request, mapping);
			}

		} catch (Exception e) {
			_log.error("validateSearchedUser", "Exception:e=" + e);
			_log.errorTrace(methodName, e);
			return super.handleError(this, "validateSearchedUser", e, request, mapping);
		} finally {
			if (_log.isDebugEnabled()) {
				_log.debug("validateSearchedUser", "Exiting forward=" + forward);
			}
		}
		return forward;
	*/}

	
}

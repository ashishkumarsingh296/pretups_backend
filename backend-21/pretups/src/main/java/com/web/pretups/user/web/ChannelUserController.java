package com.web.pretups.user.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.CommonController;
import com.btsl.common.EmailSendToUser;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.channel.transfer.businesslogic.UserDeletionBL;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.logging.ChannelUserLog;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserBalancesDAO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.user.requesthandler.ChannelSOSSettlementHandler;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserEventRemarksVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.user.service.ChannelUserService;
import com.web.user.businesslogic.UserWebDAO;
import com.web.user.web.UserModel;

/**
 * @author pankaj.rawat
  * This class provides methods for delete/suspend channel Users
 */

@Controller
public class ChannelUserController extends CommonController implements PretupsI 
{
		public static final Log LOG = LogFactory.getLog(ChannelUserController.class.getName());
		
		private static final String SESSION_OWNER_ID = "sessionOwnerID";
		private static final String PRNT_DOMAIN_CODE = "prntDomainCode";
		
		private static final String DATA_LIST = "dataList";
		private static final String FORM_SUBMITTED = "formSubmitted";
		private static final String PANEL_NAME="formNumber";
		private static final String REQUEST_TYPE="typeofrequest";
		
		@Autowired
		private ChannelUserService channeluserservice;
		private static final String CLASS_NAME = "ChannelUserController";
		private static final String FIRST_PAGE="user/selectChannelUserForDeleteSuspendView";
		private static final String SECOND_PAGE="user/deleteSuspendChannelUserView";

		private static final String PATH = "/user/web/channeluserDelete.form";
		private static final String USER_MODEL = "userModel";
		/**
		 * 
		 * @param userModel
		 * @param model
		 * @param request
		 * @param response
		 * @return
		 * @throws BTSLBaseException
		 * @throws IOException 
		 * @throws ServletException 
		 * @throws SQLException 
		 */
		@RequestMapping(value = {"/user/web/channeluserDelete.form","/user/web/channeluserSuspend.form"}, method = RequestMethod.GET)
		public String deleteSuspendChannelUser(@ModelAttribute("userModel") UserModel userModel,final Model model, HttpServletRequest request,HttpServletResponse response) 
				throws BTSLBaseException, ServletException, IOException, SQLException
		{
			final String methodName = "#deleteSuspendChannelUser";
			HttpSession session = null;
			try {
				session = request.getSession(false);
			} catch (Exception e) {
				LOG.debug(methodName, e);
			}
			if (LOG.isDebugEnabled()) {
				LOG.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
			}

			if (session.getAttribute(FORM_SUBMITTED) != null) {
				session.removeAttribute(FORM_SUBMITTED);
			}
			
			String requestType = request.getServletPath(); 
			
			if(requestType.equalsIgnoreCase(PATH) )
			{
				 this.authorise(request, response, "DCUSR001", false);
				 userModel.setRequestType("delete");
				 session.setAttribute(REQUEST_TYPE, "delete");
			}
			
			else
			{
				this.authorise(request, response, "SCUSR001", false);
				userModel.setRequestType("suspend");
				session.setAttribute(REQUEST_TYPE, "suspend");
			}
			
			model.addAttribute(PANEL_NAME, "Panel-One");
			session.setAttribute(PANEL_NAME, "Panel-One");
			final ChannelUserVO channelUserSessionVO = (ChannelUserVO) getUserFormSession(request);
			
			channeluserservice.loadDomainList(channelUserSessionVO,userModel, model, request);
			
			model.addAttribute(USER_MODEL,userModel);
			if(!BTSLUtil.isNullObject(session)){
				session.setAttribute(USER_MODEL, userModel);
			}
			return FIRST_PAGE;	
		}
		
		@RequestMapping(value = "/user/web/changeCategory.form", method = RequestMethod.GET)
		@ResponseBody
		public List loadCategory(@RequestParam(value="domainC",required=true)String domainCode, @ModelAttribute("userModel") UserModel userModel){
			final String methodName = "#loadCategory";

			if (LOG.isDebugEnabled()) {
				LOG.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
			}
			userModel.setDomainCode(domainCode);
		
			channeluserservice.loadCategoryList(userModel);
			
			return userModel.getCategoryList();
		}		
		
		@RequestMapping(value = "/user/web/deleteSuspendChannelUser.form", method = RequestMethod.POST)
		public String confirmChannelUserDeleteSuspend(@ModelAttribute("userModel") UserModel userModel,final Model model, HttpServletRequest request,HttpServletResponse response,BindingResult bindingResult) 
				throws BTSLBaseException, ServletException, IOException, SQLException
        {
			final String methodName = "#confirmChannelUserDeleteSuspend";

 			if (LOG.isDebugEnabled()) {
				LOG.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
			}
 			HttpSession session = request.getSession(false);
			UserModel finalModel = (UserModel) session.getAttribute(USER_MODEL);
			if (userModel.getOwnerID() != null) {
				String ownername=userModel.getOwnerID();
				String[] parts1=ownername.split("\\(");		
				ownername = parts1[0];
				finalModel.setOwnerName(ownername);
			}
			
			finalModel.setEventRemarks(userModel.getEventRemarks());
			finalModel.setSearchMsisdn(userModel.getSearchMsisdn());
			finalModel.setUserId(userModel.getUserId());
			 
			ChannelUserVO sessionChannelUserVO = (ChannelUserVO) this.getUserFormSession(request);

			// code for autorization

			if ("delete".equals(session.getAttribute(REQUEST_TYPE)))
			{
				this.authorise(request, response, "DCUSR002", false);
				session.setAttribute("module", "delete");
			}

			else
			{
				this.authorise(request, response, "SCUSR002", false);
				session.setAttribute("module", "suspend");
			}

			session.setAttribute(USER_MODEL, finalModel);

			if (userModel.getSearchMsisdn() != null) 
			{
				session.setAttribute(PANEL_NAME, "Panel-One");
				finalModel.setSearchMsisdn(userModel.getSearchMsisdn());
			} 
			else if (userModel.getSearchLoginId() != null) 
			{
				session.setAttribute(PANEL_NAME, "Panel-Two");
				finalModel.setSearchLoginId(userModel.getSearchLoginId());
			} 
			else 
			{
				session.setAttribute(PANEL_NAME, "Panel-Three");
				finalModel.setChannelCategoryCode(userModel	.getChannelCategoryCode());
				finalModel.setDomainCode(userModel.getDomainCode());				
				finalModel.setParentDomainDesc(userModel.getParentDomainDesc());
				finalModel.setUserId(userModel.getUserId());
				finalModel.setOtherInfo(userModel.getUserId());
				finalModel.setOwnerID(userModel.getOwnerID());
				finalModel.setOwnerNameAndID(userModel.getOwnerID());
			}			
		
			boolean result = channeluserservice.loadChnlUserDetails(model,sessionChannelUserVO, finalModel, bindingResult, request);
			channeluserservice.getCategoryList(finalModel);	
		
			
			
			if(userModel.getDomainCodeDesc() != null || !userModel.getDomainCodeDesc().isEmpty())
			{				
				for(int i=0; i<finalModel.getOrigCategoryList().size(); i++){
					CategoryVO categoryVO;
					categoryVO = (CategoryVO) finalModel.getOrigCategoryList().get(i);

					if (categoryVO.getCategoryCode().equals(finalModel.getChannelCategoryCode())) {
						finalModel.setChannelCategoryDesc(categoryVO.getCategoryName());
					}
				}
				
				
				if(finalModel.getSelectDomainList() != null)
				{ 
					for(int i=0; i<finalModel.getSelectDomainList().size(); i++)
					{	
						ListValueVO listValueVO;
						listValueVO = (ListValueVO) finalModel.getSelectDomainList().get(i);
                     
						if (listValueVO.getValue().equals(finalModel.getDomainCode()))
						{
							finalModel.setDomainCodeDesc(listValueVO.getLabel());
						
						}
					}
				}
			}
			
			if (result){	
			
				session.setAttribute(DATA_LIST,finalModel.getMsisdnList());
				session.setAttribute(USER_MODEL, finalModel);

				model.addAttribute(USER_MODEL, finalModel);
				return SECOND_PAGE;
			}
			session.setAttribute(USER_MODEL, finalModel);
			return FIRST_PAGE;
        }		
		
		
		@RequestMapping(value = "/user/web/deleteSuspendChBackUser.form", method = RequestMethod.GET)		
		public String loadBackForm(final Model model, HttpServletRequest request,HttpServletResponse response) throws BTSLBaseException, ServletException, IOException, SQLException 
		{
			final String methodName = "#loadBackForm";

			if (LOG.isDebugEnabled()) {
				LOG.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
			}
			HttpSession session = request.getSession(false);
			UserModel finalModel = (UserModel) session.getAttribute(USER_MODEL);			
		
			channeluserservice.loadCategoryList(finalModel);
			
			session.setAttribute(USER_MODEL, finalModel);
			model.addAttribute(USER_MODEL, finalModel);
			return FIRST_PAGE;
		}
		
		
		@RequestMapping(value = "/user/web/SearchUser.form", method = RequestMethod.GET)
		@ResponseBody
		public List<Map<String, String>> userList(Model model,HttpServletRequest request,@ModelAttribute("userModel") UserModel userModel,HttpServletResponse response) 
				throws BTSLBaseException
		{
			
			final String methodName = "#userList";
			List<Map<String, String>> list = new ArrayList<>();
			String userName = request.getParameter("query");
			userName = userName + "%";
			String categorycode = request.getParameter("category");
			String ownerID = request.getParameter("owner");
			HttpSession session = request.getSession(false);
			if(!ownerID.isEmpty() && ownerID != null){
			session.setAttribute(SESSION_OWNER_ID, ownerID);
			try{
				String[] parts = ownerID.split("\\(");
				String[] part = parts[1].split("\\)");
				ownerID = part[0];
				}
				 catch(Exception e){
	            	 LOG.error(methodName, "No Such Owner Exists  " + e);
	            	 LOG.errorTrace(methodName, e);
	            }
		
			}
			session.setAttribute("ownerID", ownerID);
			String domainCode = request.getParameter("domainCode");
			String prntDomainCode = request.getParameter(PRNT_DOMAIN_CODE);
			String index = request.getParameter("index");
			session.setAttribute("index", index);
			session.setAttribute(PRNT_DOMAIN_CODE, prntDomainCode);
			final UserVO userVO =  getUserFormSession(request);
			
			if(BTSLUtil.isNullString(domainCode))
			{
				Map<String, String> map = new HashMap<>();
				map.put(LOGIN_ID, PretupsRestUtil.getMessageString("pretups.user.deletesuspend.domain.code.is.required"));
				map.put(USER_ID, "");
				list.add(map);
				return list;
			}

			if(BTSLUtil.isNullString(prntDomainCode))
			{
				Map<String, String> map = new HashMap<>();
				map.put(LOGIN_ID, PretupsRestUtil.getMessageString("pretups.user.deletesuspend.geography.code.is.required"));
				map.put(USER_ID, "");
				list.add(map);
				return list;
			}
			
			if(BTSLUtil.isNullString(categorycode))
			{
				Map<String, String> map = new HashMap<>();
				map.put(LOGIN_ID, PretupsRestUtil.getMessageString("pretups.user.deletesuspend.category.code.is.required"));
				map.put(USER_ID, "");
				list.add(map);
				return list;
			}

			if(!("1".equalsIgnoreCase(index)) && !("CHANNEL".equalsIgnoreCase(userVO.getUserType())) && BTSLUtil.isNullString(ownerID))
			{
				Map<String, String> map = new HashMap<>();
				map.put(LOGIN_ID, PretupsRestUtil.getMessageString("pretups.user.deletesuspend.ownername.is.required"));
				map.put(USER_ID, "");
				list.add(map);
				return list;

			}
			
			List<UserVO> userList = channeluserservice.loadUserList(userVO, categorycode, ownerID, userName, domainCode, prntDomainCode, request, index);

			Iterator<UserVO> itr = userList.iterator();
			
			if (userList.isEmpty())
			{
				Map<String, String> map = new HashMap<>();
				map.put(LOGIN_ID, "NO DATA FOUND");
				list.add(map);
			}
			
			while (itr.hasNext())
			{
				UserVO object = itr.next();
				Map<String, String> map = new HashMap<>();
				String loginId = object.getUserName() + "(" + object.getUserID() + ")";
				map.put(LOGIN_ID, loginId);
				map.put(USER_ID, object.getUserID());
				list.add(map);
			}

			return list;

		}
		
		
		@RequestMapping(value = "/user/web/searchOwner.form", method = RequestMethod.GET)
		@ResponseBody
		public List<Map<String, String>> ownerList(Model model,HttpServletRequest request,@ModelAttribute("userModel") UserModel userModel,HttpServletResponse response) 
				throws BTSLBaseException 
		{
			List<Map<String, String>> list = new ArrayList<>();
			String ownerName = request.getParameter("query");
			ownerName = ownerName + "%";
			String domainCode = request.getParameter("domainCode");
			String prntDomainCode = request.getParameter(PRNT_DOMAIN_CODE);
			final UserVO userVO = getUserFormSession(request);
			
			if(BTSLUtil.isNullString(domainCode))
			{
				Map<String, String> map = new HashMap<>();
				map.put(LOGIN_ID, PretupsRestUtil.getMessageString("pretups.user.deletesuspend.domain.code.is.required"));
				map.put(USER_ID, "");
				list.add(map);
				return list;
			}

			if(BTSLUtil.isNullString(prntDomainCode))
			{
				Map<String, String> map = new HashMap<>();
				map.put(LOGIN_ID, PretupsRestUtil.getMessageString("pretups.user.deletesuspend.geography.code.is.required"));
				map.put(USER_ID, "");
				list.add(map);
				return list;
			}

			List<UserVO> userList = channeluserservice.loadOwnerList(userVO, prntDomainCode, ownerName, domainCode, request);
			Iterator<UserVO> itr = userList.iterator();
			
			
			if (userList.isEmpty())
			{
				Map<String, String> map = new HashMap<>();
				map.put(LOGIN_ID, "NO DATA FOUND");
				list.add(map);
			}
			
			while (itr.hasNext())
			{
				UserVO object = itr.next();
				Map<String, String> map = new HashMap<>();
				String loginId = object.getUserName() + "(" + object.getUserID() + ")";
				map.put(LOGIN_ID, loginId);
				map.put(USER_ID, object.getLoginID());
				list.add(map);
			}

			return list;

		}
		
		@RequestMapping(value = "/user/web/submit-delete-suspend.form", method = RequestMethod.POST)
		
		public String saveDeleteSuspend(Model model, @ModelAttribute("userModel") UserModel userModel, HttpServletRequest request, HttpServletResponse response)
		   throws BTSLBaseException, ServletException, IOException, SQLException		  			
		{
	        final String methodName = "saveDeleteSuspend";
	        if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, "Entered");
	        }

	        Connection con = null;
	        MComConnectionI mcomCon = null;
	        int deleteCount = 0;
	        HttpSession session = null; 
	        try {
	        	session = request.getSession(false);
	            final UserWebDAO userwebDAO = new UserWebDAO();
				UserModel finalModel = (UserModel) session.getAttribute(USER_MODEL);

	                final UserVO sessionUserVO = getUserFormSession(request);
	                final UserVO userVO = new UserVO();
	                mcomCon = new MComConnection();
	                con=mcomCon.getConnection();
	                final UserDAO userDAO = new UserDAO();
	                final Date currentDate = new Date();
	                ArrayList<UserEventRemarksVO> deleteSuspendRemarkList = null;
	                UserEventRemarksVO userRemarksVO = null;
	                
	                //Implement Security CSRF starts here    


	                if ("delete".equals(finalModel.getRequestType()))// for delete
	                {
	                    /*
	                     * Before deleting three checks will be perform
	                     * a)Check whether the child user is active or not
	                     * b)Check the balance of the deleted user
	                     * c)Check for no O2C Transfer pending (closed and canceled
	                     * Txn)
	                     */

	                    boolean isBalanceFlag = false;
	                    boolean isO2CPendingFlag = false;
	                    boolean isSOSPendingFlag = false;
	                    boolean isLRPendingFlag = false;
	                    final boolean isChildFlag = userDAO.isChildUserActive(con, finalModel.getUserId());

	                    if (isChildFlag) {
	                    	model.addAttribute("fail", PretupsRestUtil.getMessageString("pretups.user.deletesuspendchanneluser.error.childuserexist"));
	                    	model.addAttribute(USER_MODEL, finalModel);
	                    	session.setAttribute(USER_MODEL, finalModel);
	                    	return SECOND_PAGE;
	                    }

	                    else {
	                    	// Checking SOS Pending transactions
	                    	if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue()){
						        ChannelSOSSettlementHandler channelSOSSettlementHandler = new ChannelSOSSettlementHandler();
						        isSOSPendingFlag = channelSOSSettlementHandler.validateSOSPending(con, finalModel.getUserId());
							}
	                    }
	                    if(isSOSPendingFlag){
	                    	model.addAttribute("fail", PretupsRestUtil.getMessageString("pretups.user.deletesuspendchanneluser.error.userSOSpendingexist"));
	                    	model.addAttribute(USER_MODEL, finalModel);
	                    	session.setAttribute(USER_MODEL, finalModel);
	                    	return SECOND_PAGE;
	                    }else {
	                    	// Checking for pending LR transactions
	                		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED))).booleanValue()){
	                			UserTransferCountsVO userTrfCntVO = new UserTransferCountsVO();
	                			UserTransferCountsDAO userTrfCntDAO = new UserTransferCountsDAO();
	                			userTrfCntVO = userTrfCntDAO.selectLastLRTxnID(finalModel.getUserId(), con, false, null);
	                			if (userTrfCntVO!=null) 
	                				isLRPendingFlag = true;
	                		}
	                    }
	                    if(isLRPendingFlag){
	                    	model.addAttribute("fail", PretupsRestUtil.getMessageString("user.deletesuspendchanneluser.error.user.LR.pendingexist"));
	                    	model.addAttribute(USER_MODEL, finalModel);
	                    	session.setAttribute(USER_MODEL, finalModel);
	                    	return SECOND_PAGE;
	                    }else{ 
	                    	// Checking O2C Pending transactions
	                        final ChannelTransferDAO transferDAO = new ChannelTransferDAO();
	                        isO2CPendingFlag = transferDAO.isPendingTransactionExist(con, finalModel.getUserId());
	                    }	                    
	                  
	                    boolean isRestrictedMsisdnFlag = false;
	                    boolean isbatchFocPendingTxn = false;
	                    if (isO2CPendingFlag) {
	                    	model.addAttribute("fail", PretupsRestUtil.getMessageString("pretups.user.deletesuspendchanneluser.error.usero2cpendingexist"));
	                    	model.addAttribute(USER_MODEL, finalModel);
	                    	session.setAttribute(USER_MODEL, finalModel);
	                    	return SECOND_PAGE;
	                    } else {
	                        // Checking Batch FOC Pending transactions Ved -
	                        // 07/08/06
	                        final FOCBatchTransferDAO batchTransferDAO = new FOCBatchTransferDAO();
	                        isbatchFocPendingTxn = batchTransferDAO.isPendingTransactionExist(con, finalModel.getUserId());
	                    }
	                    if (isbatchFocPendingTxn) {
	                    	model.addAttribute("fail", PretupsRestUtil.getMessageString("pretups.user.deletesuspendchanneluser.error.userbatchfocpendingtxnexist"));
	                    	model.addAttribute(USER_MODEL, finalModel);
	                    	session.setAttribute(USER_MODEL, finalModel);
	                    	return SECOND_PAGE;
	                    } else {
	                       if (PretupsI.STATUS_ACTIVE.equals(finalModel.getCategoryVO().getRestrictedMsisdns())) {
	                            final RestrictedSubscriberDAO restrictedSubscriberDAO = new RestrictedSubscriberDAO();
	                            isRestrictedMsisdnFlag = restrictedSubscriberDAO.isSubscriberExistByChannelUser(con, finalModel.getUserId());
	                        }
	                    }
	                    if (isRestrictedMsisdnFlag) {
	                    	model.addAttribute("fail", PretupsRestUtil.getMessageString("pretups.user.deletesuspendchanneluser.error.userrestrictedexist"));
	                    	model.addAttribute(USER_MODEL, finalModel);
	                    	session.setAttribute(USER_MODEL, finalModel);
	                    	return SECOND_PAGE;
	                    }
	                    userVO.setUserID(finalModel.getUserId());

	                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.REQ_CUSER_DELETION_APPROVAL)).booleanValue()) {
	                        userVO.setStatus(PretupsI.USER_STATUS_DELETE_REQUEST);
	                    } else {
	                        isBalanceFlag = userDAO.isUserBalanceExist(con, finalModel.getUserId());
	                        userVO.setStatus(PretupsI.USER_STATUS_DELETED);
	                        if (isBalanceFlag) {
	                            // 6.5
	                            final UserBalancesDAO userBalancesDAO = new UserBalancesDAO();
	                            ArrayList<UserBalancesVO> userBal = null;
	                            UserBalancesVO userBalancesVO = null;
	                            final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
	                            final ChannelUserVO fromChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, userVO.getUserID(), false, currentDate,false);
	                            fromChannelUserVO.setGateway(PretupsI.REQUEST_SOURCE_TYPE_WEB);
	                            final ChannelUserVO toChannelUserVO = channelUserDAO.loadChannelUserDetailsForTransfer(con, fromChannelUserVO.getOwnerID(), false, currentDate,false);
	                            userBal = userBalancesDAO.loadUserBalanceForDelete(con, fromChannelUserVO.getUserID());// user
	                            // to
	                            // be
	                            // deleted
	                            Iterator<UserBalancesVO> itr = userBal.iterator();
	                            itr = userBal.iterator();
	                            boolean sendMsgToOwner = false;
	                            long totBalance = 0;
	                            while (itr.hasNext()) {
	                                userBalancesVO = itr.next();
	                                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.RETURN_TO_OPERATOR_STOCK)).booleanValue() || fromChannelUserVO
	                                                .getOwnerID().equals(userVO.getUserID())) {
	                                    UserDeletionBL.updateBalNChnlTransfersNItemsO2C(con, fromChannelUserVO, toChannelUserVO, PretupsI.REQUEST_SOURCE_TYPE_WEB,
	                                                    PretupsI.REQUEST_SOURCE_TYPE_WEB, userBalancesVO);
	                                } else {

	                                	if(!PretupsI.USER_STATUS_SUSPEND.equalsIgnoreCase(toChannelUserVO.getStatus()))
	                                	{
	                                    UserDeletionBL.updateBalNChnlTransfersNItemsC2C(con, fromChannelUserVO, toChannelUserVO, sessionUserVO.getUserID(),
	                                                    PretupsI.REQUEST_SOURCE_TYPE_WEB, userBalancesVO);
	                                    sendMsgToOwner = true; 
	                                    totBalance += userBalancesVO.getBalance();
	                                	}
	                                	else
	                                	{
	                                		model.addAttribute("fail", PretupsRestUtil.getMessageString("pretups.user.channeluser.deletion.parentsuspended"));
	                                		model.addAttribute(USER_MODEL, finalModel);
	            	                    	session.setAttribute(USER_MODEL, finalModel);
	                                		return SECOND_PAGE;
	                                	}
	                                		
	                                }
	                            }
	                            //ASHU
	                            if(sendMsgToOwner) {
	                            	ChannelUserVO prntChnlUserVO = new ChannelUserDAO().loadChannelUserByUserID(con, fromChannelUserVO.getOwnerID());
	                                String[] msgArr = {fromChannelUserVO.getMsisdn(),PretupsBL.getDisplayAmount(totBalance)};
	                                final BTSLMessages sendBtslMessageToOwner = new BTSLMessages(PretupsErrorCodesI.OWNER_USR_BALCREDIT,msgArr);
	                                final PushMessage pushMessageToOwner = new PushMessage(prntChnlUserVO.getMsisdn(), sendBtslMessageToOwner, "", "", new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),
	                                                (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), fromChannelUserVO.getNetworkID());
	                                pushMessageToOwner.push();   
	                            } 
	                        }
	                    }

	                    userVO.setLastModified(finalModel.getLastModified());
	                    userVO.setModifiedBy(sessionUserVO.getActiveUserID());
	                    userVO.setModifiedOn(currentDate);
	                    userVO.setUserID(finalModel.getUserId());
	                    /*
	                     * set the old status value into the previous status
	                     */
	                    userVO.setPreviousStatus(finalModel.getStatus());
	                    final ArrayList list = new ArrayList();
	                    list.add(userVO);
	                    deleteCount = userDAO.deleteSuspendUser(con, list);
	                    if (deleteCount <= 0) {
	                        con.rollback();
	                        LOG.error(methodName, "Error: while Deleting User");
	                        
	                        throw new BTSLBaseException(this, "save", "error.general.processing");
	                    }

	                    if(((Boolean)PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_EVENT_REMARKS)).booleanValue()){
		                    
		                    if (deleteCount > 0) {
		                        int deleteRemarkCount = 0;
		                        deleteSuspendRemarkList = new ArrayList<UserEventRemarksVO>();
		                        userRemarksVO = new UserEventRemarksVO();
		                        userRemarksVO.setCreatedBy(sessionUserVO.getCreatedBy());
		                        userRemarksVO.setCreatedOn(currentDate);
		                        
		                        userRemarksVO.setEventType(PretupsI.DELETE_REQUEST_EVENT);
		                        userRemarksVO.setMsisdn(finalModel.getMsisdn());
		                        userRemarksVO.setRemarks(finalModel.getEventRemarks());
		                        userRemarksVO.setUserID(finalModel.getUserId());
		                        userRemarksVO.setUserType(finalModel.getUserType());
		                        userRemarksVO.setModule(PretupsI.C2S_MODULE);
		                        deleteSuspendRemarkList.add(userRemarksVO);
		                        deleteRemarkCount = userwebDAO.insertEventRemark(con, deleteSuspendRemarkList);
		                        if (deleteRemarkCount <= 0) {
		                            con.rollback();
		                            LOG.error(methodName, "Error: while inserting into userEventRemarks Table");
		                            throw new BTSLBaseException(this, "save", "error.general.processing");
		                        }
		                    }
	                    }

	                    con.commit();
	                    final String[] arr = { finalModel.getChannelUserName() };
	                    BTSLMessages btslMessage = null;
	                    if (PretupsI.USER_STATUS_DELETED.equals(userVO.getStatus())) {
	                    	model.addAttribute("success", PretupsRestUtil.getMessageString("pretups.user.deletesuspendchanneluser.deletesuccessmessage",arr));

	                    	btslMessage = new BTSLMessages("user.deletesuspendchanneluser.deletesuccessmessage", arr, "DeleteSuccess");

	                        final BTSLMessages sendBtslMessage = new BTSLMessages(PretupsErrorCodesI.CHNL_USER_DEREGISTER);
	                        final PushMessage pushMessage = new PushMessage(finalModel.getMsisdn(), sendBtslMessage, "", "", new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)),
	                                        (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY))), sessionUserVO.getNetworkID());
	                        pushMessage.push();
	                        // Email for pin & password
	                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.EMAIL_SERVICE_ALLOW))).booleanValue() && !BTSLUtil.isNullString(userVO.getEmail())) {
	                            final String subject=PretupsRestUtil.getMessageString("pretups.user.subject.eachuser.email.delete.message", arr);
	                            final EmailSendToUser emailSendToUser = new EmailSendToUser(subject, sendBtslMessage,BTSLUtil.getBTSLLocale(request),
									userVO.getNetworkID(),"Email has ben delivered recently", userVO,sessionUserVO);
                                   
                                emailSendToUser.sendMail();
	                        }
	                        
	                        
	                    } else {
	                    	model.addAttribute("success", PretupsRestUtil.getMessageString("pretups.user.deletesuspendchanneluser.deleterequestsuccessmessage",arr));

	                        btslMessage = new BTSLMessages("user.deletesuspendchanneluser.deleterequestsuccessmessage", arr, "DeleteSuccess");
	                    }
	                 

	                    userVO.setLoginID(finalModel.getWebLoginID());
	                    userVO.setUserName(finalModel.getChannelUserName());
	                    userVO.setMsisdn(finalModel.getMsisdn());

	                    if (userVO.getStatus().equals(PretupsI.USER_STATUS_DELETE_REQUEST)) {
	                        ChannelUserLog.log("DELREQCHNLUSR", userVO, sessionUserVO, false, null);
	                    } else if (userVO.getStatus().equals(PretupsI.USER_STATUS_DELETED)) {
	                        ChannelUserLog.log("DELCHNLUSR", userVO, sessionUserVO, false, null);
	                    }
	                } 
	                
	                else if ("suspend".equals(finalModel.getRequestType())) // for suspend
	                {              

	                    if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.REQ_CUSER_SUSPENSION_APPROVAL)).booleanValue()) {
	                        userVO.setStatus(PretupsI.USER_STATUS_SUSPEND_REQUEST);
	                    } else {
	                        userVO.setStatus(PretupsI.USER_STATUS_SUSPEND);
	                    }

	                    userVO.setUserID(finalModel.getUserId());
	                    userVO.setLastModified(finalModel.getLastModified());
	                    userVO.setModifiedBy(sessionUserVO.getActiveUserID());
	                    userVO.setModifiedOn(currentDate);
	                    /*
	                     * set the old status value into the previous status
	                     */
	                    userVO.setPreviousStatus(finalModel.getStatus());
	                    final ArrayList list = new ArrayList();
	                    list.add(userVO);
	                    deleteCount = userDAO.deleteSuspendUser(con, list);

	                    if (deleteCount <= 0) {
	                        con.rollback();
	                        LOG.error(methodName, "Error: while Suspending User");
	                        
	                        throw new BTSLBaseException(this, "save", "error.general.processing");
	                    }

	                    
	                    else if (deleteCount > 0) {
	                            int suspendRemarkCount = 0;
	                            deleteSuspendRemarkList = new ArrayList<UserEventRemarksVO>();
	                            userRemarksVO = new UserEventRemarksVO();
	                            userRemarksVO.setCreatedBy(sessionUserVO.getCreatedBy());
	                            userRemarksVO.setCreatedOn(currentDate);
	                        
	                            userRemarksVO.setEventType(PretupsI.SUSPEND_REQUEST_EVENT);
	                            userRemarksVO.setMsisdn(finalModel.getMsisdn());
	                            userRemarksVO.setRemarks(finalModel.getEventRemarks());
	                            userRemarksVO.setUserID(finalModel.getUserId());
	                            userRemarksVO.setUserType(finalModel.getUserType());
	                            userRemarksVO.setModule("C2S");
	                            deleteSuspendRemarkList.add(userRemarksVO);
	                            suspendRemarkCount = userwebDAO.insertEventRemark(con, deleteSuspendRemarkList);
	                            if (suspendRemarkCount <= 0)
	                            {
	                                con.rollback();
	                                LOG.error(methodName, "Error: while inserting into userEventRemarks Table");
	                                throw new BTSLBaseException(this, "save", "error.general.processing");
	                            }
	                        }
	                    
	                    con.commit();
	                    
	                    final String[] arr = { finalModel.getChannelUserName() };
	                    BTSLMessages btslMessage = null;
	                    if (PretupsI.USER_STATUS_SUSPEND.equals(userVO.getStatus())) {
	                    	model.addAttribute("success", PretupsRestUtil.getMessageString("pretups.user.deletesuspendchanneluser.suspendsuccessmessage",arr));
	                        btslMessage = new BTSLMessages("user.deletesuspendchanneluser.suspendsuccessmessage", arr, "SuspendSuccess");
	                    } else {
                    		model.addAttribute("success", PretupsRestUtil.getMessageString("pretups.user.deletesuspendchanneluser.suspendrequestsuccessmessage",arr));
	                        btslMessage = new BTSLMessages("user.deletesuspendchanneluser.suspendrequestsuccessmessage", arr, "SuspendSuccess");
	                        
	                    }

	                    userVO.setLoginID(finalModel.getWebLoginID());
	                    userVO.setUserName(finalModel.getChannelUserName());
	                    userVO.setMsisdn(finalModel.getMsisdn());
	                    if (userVO.getStatus().equals(PretupsI.USER_STATUS_SUSPEND_REQUEST)) {
	                        ChannelUserLog.log("SUSPREQCHNLUSR", userVO, sessionUserVO, false, null);
	                    } else if (userVO.getStatus().equals(PretupsI.USER_STATUS_SUSPEND)) {
	                        ChannelUserLog.log("SUSPCHNLUSR", userVO, sessionUserVO, false, null);
	                    }
	                }
	            
	        } catch (Exception e) {
	            LOG.errorTrace(methodName, e);
	            
	        } finally {
	        	if(mcomCon != null)
	        	{
	        		mcomCon.close("ChannelUserAction#saveDeleteSuspend");
	        		mcomCon=null;
	        		}
	            if (LOG.isDebugEnabled()) {
	                LOG.debug(methodName, "Exiting");
	            }
	        }
	        
	        return FIRST_PAGE;	       
		}
}

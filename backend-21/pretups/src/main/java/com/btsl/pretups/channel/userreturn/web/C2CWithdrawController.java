package com.btsl.pretups.channel.userreturn.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferItemsVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.channel.userreturn.service.C2CWithdrawService;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SqlParameterEncoder;

/*
 * This class provides method for loading UI for C2C Withdraw as well as
 * processing data for C2C Withdraw request
 */
@Controller
public class C2CWithdrawController extends CommonController {
	public static final Boolean flag1=true;
	public static final Boolean flag2=false;
	public static final Boolean flag3=false;
	public static final Boolean flag4=false;

	@Autowired
	private C2CWithdrawService c2cWithdrawService;
	
	@RequestMapping(value = "/master/C2CWithdrawFromBCU.form", method = RequestMethod.GET)
	public String loadC2CWithdrawForm(final Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception{
		String methodName="C2CWithdrawController#loadC2cWithdrawForm";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered:");
								
		}
		model.addAttribute("flag1", flag1);
		model.addAttribute("flag2", flag2);
		model.addAttribute("flag3", flag3);
		model.addAttribute("flag4", flag4);
		
		UserVO userVO = this.getUserFormSession(request);
		authorise(request, response, "C2CWCU001A", false);
		List<ListValueVO> domainList = c2cWithdrawService.loadDomain();
		model.addAttribute("domainList", domainList);
		request.getSession().setAttribute("category", null);
		List<ListValueVO> geographyList = new ArrayList<>();

		if (userVO.getGeographicalAreaList() != null) {

			for (int i = 0, j = userVO.getGeographicalAreaList().size(); i < j; i++) {

				geographyList.add(new ListValueVO(((UserGeographiesVO) userVO
						.getGeographicalAreaList().get(i)).getGraphDomainName()
						, ((UserGeographiesVO) userVO
						.getGeographicalAreaList().get(i)).getGraphDomainCode()
						));

			}
			model.addAttribute("geographyList", geographyList);

		} else {
			log.debug(methodName,
					"geographyList is empty , problem with UserVO");
		}
		
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting with geography list: "+geographyList);
		}
		request.getSession().setAttribute("domainList", domainList);
		request.getSession().setAttribute("geographyList",geographyList);
		return "userreturn/C2CWithdraw";
	}

	@RequestMapping(value = "/master/load-category.form", method = RequestMethod.POST)
	public @ResponseBody List loadCategory(
			@RequestParam("domain") String domain, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception

	{
		domain = SqlParameterEncoder.encodeParams(domain);
		UserVO userVO = this.getUserFormSession(request);
		final String methodName = "LoadCategory";

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered : domain =" + domain
					+ " and networkId: " + userVO.getNetworkID());
		}
		List categoryList;

		categoryList = c2cWithdrawService.loadCategory(domain,
				userVO.getNetworkID());
		
		model.addAttribute("categoryList", categoryList);
		request.getSession().setAttribute("categoryList", categoryList);
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting with category list: "
					+ categoryList);
		}
		return categoryList;
	}

	// Owner User
	@RequestMapping(value = "/master/load-user-list.form", method = RequestMethod.GET)
	public String loadUserList(
			@RequestParam("domain") String domain,
			@RequestParam("category") String category,
			@RequestParam("geography") String geography,
			@RequestParam("owner") String user, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws BTSLBaseException,IOException {

		final String methodName = "loadUserList";

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered with : DOMAIN=" + domain
					+ ",CATEGORY=" + category + ",GEOGRAPHY=" + geography
					+ ",User=" + user);
		}

		List<ListValueVO> userList ;
		userList = c2cWithdrawService.loadUserData(domain, category, geography,
				user);
		model.addAttribute("userList", userList);
		model.addAttribute("domain", domain);
		model.addAttribute("category", category);
		model.addAttribute("geography", geography);
		model.addAttribute("user", user);
		request.getSession().setAttribute("selectedcategory", request.getParameter("category"));
		request.getSession().setAttribute("geography", request.getParameter("geography"));
		request.getSession().setAttribute("owner", request.getParameter("owner"));
		request.getSession().setAttribute("domain", request.getParameter("domain"));
		request.getSession().setAttribute("userList", request.getParameter("userList"));
		model.addAttribute("userList", userList);
		log.info(methodName, "SSSSSSS : "+userList.size());
		if (userList.isEmpty()) {
			if (log.isDebugEnabled()) {
				log.debug(methodName, "List is null");
			}
			model.addAttribute("fail", PretupsRestUtil.getMessageString(
					"userreturn.c2cwithdraw.nouserexist.msg",new String[]{user}));
		}
		else{
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited."+userList);
		}
		}
		return "userreturn/usersearch";
	}

	@RequestMapping(value = "/master/select-search-user.form", method = RequestMethod.GET)
	public String selectSearchedUser(Model model,HttpServletRequest request) {

		final String methodName = "#selectSearchedUser";

			if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered: ");
		}
			if(!BTSLUtil.isNullString(request.getParameter("senderUserList")))
				request.getSession().setAttribute("senderUserList", request.getParameter("senderUserList"));
			if(!BTSLUtil.isNullString(request.getParameter("userList")))
				request.getSession().setAttribute("userList", request.getParameter("userList"));
			log.debug(methodName, request.getParameter("category"));
			
			
			if (log.isDebugEnabled()) {
				log.debug(methodName, request.getSession().getAttribute("selectedcategory")+" - "+ request.getSession().getAttribute("geography")+" - "+ request.getSession().getAttribute("userList")+" - "+ request.getSession().getAttribute("domain"));
				log.debug(methodName, "Exited: ");
			}
				return "userreturn/addTemp";
	}
	
	// Submit Click
	@RequestMapping(value = "/master/submit-c2c-withdraw.form", method = RequestMethod.POST)
	public String submitc2cWithdraw(@ModelAttribute("c2cWithdraw") C2CWithdrawVO c2cWithdrawVO,
			final Model model, HttpServletRequest request,
			HttpServletResponse response) throws BTSLBaseException,IOException,Exception {
		String methodName="C2CWithdrawController#ReceiverDetails";
		ChannelUserVO channelUserVO=null;
		String geography;
		String tomsisdn=request.getParameter("toMsisdn");
			 geography=request.getParameter("geography");
			 String user=request.getParameter("toUserName");
			String toUserId;
			List<ListValueVO> geographyList=(List<ListValueVO>)request.getSession().getAttribute("geographyList");
			model.addAttribute("geographyList", geographyList);
			List<ListValueVO> domainList=(List<ListValueVO>)request.getSession().getAttribute("domainList");
			request.getSession().setAttribute("toMsisdn",tomsisdn);
			model.addAttribute("domainList", domainList);
			if (log.isDebugEnabled()) {
				log.debug(methodName,"Entered User: "+user+
						" category: " + c2cWithdrawVO.getToCategory()+" ownerUser: "+c2cWithdrawVO.getToUserId() +" geography: "+geography
						+" Geography: "+ c2cWithdrawVO.getGeography()+" domainName: "+c2cWithdrawVO.getDomainName()+" domainCode: "+c2cWithdrawVO.getDomainCode()+
						" toMsisdn: "+c2cWithdrawVO.getToMsisdn()+" userName: "+c2cWithdrawVO.getToUserName());
			}
			if (!BTSLUtil.isNullString(tomsisdn) && BTSLUtil.isValidMSISDN(tomsisdn)) {
				channelUserVO=c2cWithdrawService.loadUserDetails(c2cWithdrawVO,true);
			}
			else if(!BTSLUtil.isNullString(c2cWithdrawVO.getToUserId())){
				
				channelUserVO=c2cWithdrawService.loadUserDetails(c2cWithdrawVO,false);
			}
			else
			{
				model.addAttribute("fail", PretupsRestUtil.getMessageString(PretupsErrorCodesI.USER_TRANSFER_INITIATED_USER_SEARCH));
				return loadC2CWithdrawForm(model, request, response);
			}
			if(channelUserVO!=null)
			{
				c2cWithdrawVO.setDomainName(channelUserVO.getDomainName());
				c2cWithdrawVO.setToUserId(channelUserVO.getUserID());
				c2cWithdrawVO.setToCategory(channelUserVO.getCategoryCode());
				c2cWithdrawVO.setGeography(geography);
				c2cWithdrawVO.setToUserName(channelUserVO.getUserName());
				c2cWithdrawVO.setDomainCode(channelUserVO.getDomainID());
				request.getSession().setAttribute("domainCode",c2cWithdrawVO.getDomainCode());
				request.getSession().setAttribute("toCategory", c2cWithdrawVO.getToCategory());
				List catList;
				// load category list(sender) by transfer rule
				catList = c2cWithdrawService.loadCatListByTrfRule(
						channelUserVO.getDomainID(),channelUserVO.getCategoryVO().getSequenceNumber());
				model.addAttribute("catList", catList);
				model.addAttribute("flag2",true);
				model.addAttribute("flag1",false );
				model.addAttribute("c2cWithdrawVO",c2cWithdrawVO);
				request.getSession().setAttribute("c2cWithdrawVO", c2cWithdrawVO);
				request.getSession().setAttribute("catList", catList);
				return "userreturn/C2CWithdraw";
			}
			else {
				model.addAttribute("fail", PretupsRestUtil.getMessageString(
						"message.channeltransfer.userdetailnotfound.msg",
						new String[] { tomsisdn }));
				return loadC2CWithdrawForm(model, request, response);
			}
	}

	// User list sender
	@RequestMapping(value = "/master/load-user-list-sender.form", method = RequestMethod.GET)
	public String loadChannelUserListSender(
			@RequestParam("fromCat") String fromCat,
			@RequestParam("toCat") String toCat,
			@RequestParam("toUserId") String toUserId,
			@RequestParam("sender") String user, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws BTSLBaseException,IOException {
			
		final String methodName = "loadChannelUserListSender";
		UserVO userVO = this.getUserFormSession(request);
		String domain=(String)request.getSession().getAttribute("domainCode");
		String category=(String) request.getSession().getAttribute("toCategory");
		String networkId=userVO.getNetworkID();
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered with : From_CATEGORY= " + fromCat
					+ ",To_Cat= " + toCat+ ",Domain= " + domain
					+ ",networkId= " + networkId + ", toUserID= " + toUserId
					+ " ,User=" + user+"-"+category);
		}
		List<ListValueVO> channelUserListSender;
		
		channelUserListSender = c2cWithdrawService.loadUserListSender(fromCat,
				toCat, domain, networkId, toUserId, userVO, user);
		model.addAttribute("fromCat",fromCat);
		model.addAttribute("toCat",toCat );
		model.addAttribute("toUserId",toUserId );
		model.addAttribute("senderUserList", channelUserListSender);
		request.getSession().setAttribute("senderUserList", request.getParameter("senderUserList"));
		request.getSession().setAttribute("fromCat",fromCat);
		if (channelUserListSender == null || channelUserListSender.isEmpty()) {
			if (log.isDebugEnabled()) {
				log.debug(methodName, "List is null");
			}
			model.addAttribute("fail", PretupsRestUtil.getMessageString(
					"userreturn.c2cwithdraw.nouserexist.msg",new String[]{user}));
			return "userreturn/channelusersearch";
		}
		

		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited.");
			log.debug(methodName, "DATA :" + channelUserListSender);
		}
		return "userreturn/channelusersearch";
	}
	
	/**
	 * Handle back button functionality for view Barred List
	 * @return String the path of view for showing messages
	 * @param request HttpServletRequest Object
	 */
	@RequestMapping(value="/master/back-to-the-receiver-details-list.form", method=RequestMethod.GET)
	public String backToTheReceiver(HttpServletRequest request,Model model,HttpServletResponse response) throws Exception{
		model.addAttribute("flag1",true);
		model.addAttribute("flag2",false);
		request.getSession().removeAttribute("senderUserList");
		return loadC2CWithdrawForm(model, request, response);
	}
	

	// Second Submit

	@RequestMapping(value = "/master/submit-c2c-withdraw1.form", method = RequestMethod.POST)
	public String submitc2cWithdraw1(Model model, HttpServletRequest request,@ModelAttribute("c2cWithdrawVO") C2CWithdrawVO c2cwithdrawVO,
			HttpServletResponse response) throws Exception {
		final String methodName = "C2CWithdrawController#SenderDetails";
		UserVO userVO = this.getUserFormSession(request);
		String fromUserID;
		String sender=request.getParameter("fromUserName");
		String fromMsisdn=request.getParameter("fromMsisdn");
		request.getSession().setAttribute("fromMsisdn",fromMsisdn );
		String toMsisdn=request.getParameter("toMsisdn");
		PretupsResponse<C2CWithdrawVO> pretupsResponse = null;
		ChannelUserVO channelUserVO;
		if (!BTSLUtil.isNullString(toMsisdn) && BTSLUtil.isValidMSISDN(toMsisdn)) {
			channelUserVO = c2cWithdrawService.loadUserDetails(c2cwithdrawVO, true);
			c2cwithdrawVO.setDomainName(channelUserVO.getDomainName());
			c2cwithdrawVO.setToUserId(channelUserVO.getUserID());
			c2cwithdrawVO.setToCategory(channelUserVO.getCategoryCode());
			c2cwithdrawVO.setToUserName(channelUserVO.getUserName());
			c2cwithdrawVO.setDomainCode(channelUserVO.getDomainID());
		}
		if (!BTSLUtil.isNullString(fromMsisdn) && BTSLUtil.isValidMSISDN(fromMsisdn)) {
			pretupsResponse = c2cWithdrawService.validateUser(c2cwithdrawVO,userVO,true);
		} else if(!BTSLUtil.isNullString(c2cwithdrawVO.getFromUserId())) {
			request.getSession().setAttribute("catList", request.getSession().getAttribute("catList"));
	
				pretupsResponse = c2cWithdrawService.validateUser(
						c2cwithdrawVO, userVO, false);
			}
		else
		{
			model.addAttribute("fail", PretupsRestUtil.getMessageString(PretupsErrorCodesI.USER_TRANSFER_INITIATED_USER_SEARCH));
			model.addAttribute("flag1",false);
			model.addAttribute("flag2",true);
			model.addAttribute("flag3",false);
			return "userreturn/C2CWithdraw";
		}
		C2CWithdrawVO c2cWithdrawVO2=pretupsResponse.getDataObject();
		if (pretupsResponse.getFormError() == null) {
			model.addAttribute("flag1",false);
			model.addAttribute("flag2",false);
			model.addAttribute("flag3",true);
			model.addAttribute("c2cWithdrawVO",c2cWithdrawVO2);
			request.getSession().setAttribute("c2cWithdrawVO", c2cWithdrawVO2);
			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exited successfully."+pretupsResponse);
			}
			return "userreturn/C2CWithdraw";
		}
		 else {
			if (pretupsResponse.getParameters() != null)
				model.addAttribute(
						"fail",
						PretupsRestUtil.getMessageString(
								pretupsResponse.getFormError(),
								pretupsResponse.getParameters()));
			else
				model.addAttribute("fail", PretupsRestUtil
						.getMessageString(pretupsResponse.getFormError()));

			if (log.isDebugEnabled()) {
				log.debug(methodName, "Exited with errors.");
			}
			model.addAttribute("flag1",false);
			model.addAttribute("flag2",true);
			model.addAttribute("flag3",false);
			return "userreturn/C2CWithdraw";
		}
}
	/**
	 * Handle back button functionality for view Barred List
	 * @return String the path of view for showing messages
	 * @param request HttpServletRequest Object
	 */
	@RequestMapping(value="/master/back-to-the-sender-details-list.form", method=RequestMethod.GET)
	public String backToTheSender(HttpServletRequest request,Model model,HttpServletResponse response) throws Exception{
		model.addAttribute("flag1",false);
		model.addAttribute("flag2",true);
		model.addAttribute("flag3",false);
		return "userreturn/C2CWithdraw";
	}
	/**
	 * Handle back button functionality for view Barred List
	 * @return String the path of view for showing messages
	 * @param request HttpServletRequest Object
	 */
	@RequestMapping(value="/master/back-to-the-product-details-list.form", method=RequestMethod.GET)
	public String backToTheViewProduct(HttpServletRequest request,Model model,HttpServletResponse response) throws Exception{
		model.addAttribute("flag1",false);
		model.addAttribute("flag2",false);
		model.addAttribute("flag3",true);
		model.addAttribute("flag4",false);
		return "userreturn/C2CWithdraw";
	}


	@RequestMapping(value = "/master/submit-c2c-withdraw2.form", method = RequestMethod.POST)
	public String submitc2cWithdraw2(Model model,HttpServletRequest request,@ModelAttribute("c2cWithdrawVO") C2CWithdrawVO c2cwithdrawVO,HttpServletResponse response)
			throws BTSLBaseException,IOException,Exception {
		UserVO userVO = this.getUserFormSession(request);
		C2CWithdrawVO withdraw=(C2CWithdrawVO) request.getSession().getAttribute("c2cWithdrawVO");
		withdraw.setAmount(c2cwithdrawVO.getAmount());
		withdraw.setRemarks(c2cwithdrawVO.getRemarks());
		final String methodName = "submitc2cWithdraw2";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered with amount: " + withdraw.getAmount()
					+ " and remarks: " + withdraw.getRemarks() + " and AdmUserID: "+userVO.getUserID()+
					" Product List: "+withdraw.getProductList()+" SenderVO: "+withdraw.getSenderVO()+" From UserId: "+withdraw.getSenderVO().getUserID()+" To UserId: "+ withdraw.getToUserId());
		}
		
		model.addAttribute("c2cWithdrawVO",withdraw);
		PretupsResponse<ChannelTransferItemsVO> pretupsResponse ;
		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SHOW_CAPTCHA))).booleanValue())
        {
               String parm = request.getParameter("captchaVerificationCode");
			boolean isCaptchaValidated;
               if (parm != null && !parm.isEmpty()) {
            	   isCaptchaValidated = BTSLUtil.validateCaptcha(request);
            	   if (!isCaptchaValidated) {
            		   log.info(methodName, "Captcha not correct. ");
            		   model.addAttribute("fail", PretupsRestUtil.getMessageString("captcha.error.wrongentry"));
                	   model.addAttribute("flag1",false);
       				   model.addAttribute("flag2",false);
       				   model.addAttribute("flag3",true);
       				   model.addAttribute("flag4",false);
       				return "userreturn/C2CWithdraw";
            	   }
               }
               else if(parm==null || parm.isEmpty())
               {
            	 log.info(methodName,"Captcha field empty.");
            	   model.addAttribute("fail", PretupsRestUtil.getMessageString("captcha.error.wrongentry"));
            	   model.addAttribute("flag1",false);
   				   model.addAttribute("flag2",false);
   				   model.addAttribute("flag3",true);
   				   model.addAttribute("flag4",false);
   				return "userreturn/C2CWithdraw";
               }
               
               
       }
		pretupsResponse=c2cWithdrawService.confirmWithdraw(withdraw,userVO);
		if (pretupsResponse.getFormError() == null) {
			ChannelTransferItemsVO itemsVO=pretupsResponse.getDataObject();
			if (log.isDebugEnabled()) {
				log.debug(methodName, "ItemsVO: "+itemsVO.getRequiredQuantity()+" - "+itemsVO.getProductTotalMRP()+" - "+itemsVO.getSenderDebitQty()+
						" - "+itemsVO.getReceiverCreditQty());
			}
			int transferMRP=0;
			if(PretupsI.COMM_TYPE_POSITIVE.equals(withdraw.getSenderVO().getDualCommissionType()))
                transferMRP += (itemsVO.getReceiverCreditQty()) * Long.parseLong(PretupsBL.getDisplayAmount(itemsVO.getUnitValue()));
            else
                transferMRP += itemsVO.getUnitValue() * Double.parseDouble(itemsVO.getRequestedQuantity());
			withdraw.setTransferMRP(PretupsBL.getDisplayAmount(transferMRP));
			withdraw.setTotalReqQuantity(PretupsBL.getDisplayAmount(itemsVO.getRequiredQuantity()));
			withdraw.setPayableAmount(PretupsBL.getDisplayAmount(itemsVO.getPayableAmount()));
			withdraw.setNetPayableAmount(PretupsBL.getDisplayAmount(itemsVO.getNetPayableAmount()));
			withdraw.setTotalTax1(PretupsBL.getDisplayAmount(itemsVO.getTax1Value()));
			withdraw.setTotalTax2(PretupsBL.getDisplayAmount(itemsVO.getTax2Value()));
			withdraw.setTotalTax3(PretupsBL.getDisplayAmount(itemsVO.getTax3Value()));
			withdraw.setTotalComm(PretupsBL.getDisplayAmount(itemsVO.getCommValue()));
			withdraw.setTotalStock(PretupsBL.getDisplayAmount(itemsVO.getBalance()));
			
			model.addAttribute("c2cWithdrawVO",withdraw);
			model.addAttribute("itemsVO",itemsVO);
			request.getSession().setAttribute("channelTransferItemsVO", itemsVO);
			model.addAttribute("flag1",false);
			model.addAttribute("flag2",false);
			model.addAttribute("flag3",false);
			model.addAttribute("flag4",true);
		}
		 else {
				if (pretupsResponse.getParameters() != null)
					model.addAttribute(
							"fail",
							PretupsRestUtil.getMessageString(
									pretupsResponse.getFormError(),
									pretupsResponse.getParameters()));
				else
					model.addAttribute("fail", PretupsRestUtil
							.getMessageString(pretupsResponse.getFormError()));

				if (log.isDebugEnabled()) {
					log.debug(methodName, "Exited with errors.");
				}
				model.addAttribute("flag1",false);
				model.addAttribute("flag2",false);
				model.addAttribute("flag3",true);
				model.addAttribute("flag4",false);
				
			}
		return "userreturn/C2CWithdraw";
	}

	@RequestMapping(value = "/master/submit-c2c-withdraw3.form", method = RequestMethod.POST)
	public String submitc2cWithdraw3(Model model, HttpServletRequest request,@ModelAttribute("c2cWithdrawVO") C2CWithdrawVO c2cwithdrawVO,
			HttpServletResponse response) throws BTSLBaseException,Exception {
		UserVO userVO = this.getUserFormSession(request);
		final String methodName = "submitc2cWithdraw3";
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Entered with AdmUserID: "+userVO.getUserID());
		}
		if(csrfcheck(request, model)){
			 return "common/csrfmessage";
		}
		ChannelTransferItemsVO itemsVO=(ChannelTransferItemsVO)request.getSession().getAttribute("channelTransferItemsVO");
		C2CWithdrawVO withdrawVO=(C2CWithdrawVO) request.getSession().getAttribute("c2cWithdrawVO");
		withdrawVO.setAmount(c2cwithdrawVO.getAmount());
		withdrawVO.setRemarks(c2cwithdrawVO.getRemarks());
		ChannelTransferVO channelTransferVO;
		channelTransferVO = c2cWithdrawService.withdraw(withdrawVO, userVO, itemsVO);
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exited with channelTransferVO:"
					+ channelTransferVO);
		}
		if (channelTransferVO == null)
			model.addAttribute("fail", "Transaction Failed");
		else
			model.addAttribute("success", PretupsRestUtil.getMessageString(
					"userreturn.c2cWithdraw.transaction.successful",
					new String[] { channelTransferVO.getTransferID(),
							channelTransferVO.getFromUserCode(),
							channelTransferVO.getToUserCode(),
							channelTransferVO.getReceiverPostStock() }));
		return loadC2CWithdrawForm(model, request, response);
	}

}

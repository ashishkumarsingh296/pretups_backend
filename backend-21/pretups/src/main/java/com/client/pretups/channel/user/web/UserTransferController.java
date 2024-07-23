package com.client.pretups.channel.user.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.client.pretups.channel.user.service.UserTransferService;



/**
 * @author akanksha.gupta
 * This class provides method for loading UI for User Transfer as well as 
 * processing data for User Transfer request
 *
 */
@Controller
public class UserTransferController extends CommonController{

	@Autowired
	private UserTransferService userTransferService;
	private static final  String CLASSNAME = "UserTransferController";

	/**
	 * UserTransferController.java
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 * @throws ServletException
	 * @throws IOException
	 * @throws Exception
	 * String
	 * akanksha.gupta
	 * 25-Aug-2016 5:57:50 pm
	 */
	@RequestMapping(value = "/usertransfer/usertransfer.form", method = RequestMethod.GET)
	public String loadUserTrfDetails(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException, ServletException,  IOException
	{
		final String methodName = "#loadUserTrfDetails";
		
		if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+methodName, PretupsI.ENTERED);
		}

		
		UserVO userVO = this.getUserFormSession(request);
		authorise(request, response, "USRTRF01A", false);

		if("USRTRF001".equalsIgnoreCase(request.getParameter("pageCode")) && request.getSession().getAttribute("reloading") == null){
			request.getSession().removeAttribute("geography");
			request.getSession().removeAttribute("domain");
			request.getSession().removeAttribute("category");
			request.getSession().removeAttribute("userList");
			request.getSession().removeAttribute("channelUserList");
		}
		request.getSession().removeAttribute("reloading");
		List<ListValueVO> domainList = userTransferService.loadDomain();
	
		Iterator<ListValueVO> itr = domainList.iterator(); 
		while (itr.hasNext())
		{	 ListValueVO domainVO = itr.next(); 
		if (!userVO.getDomainID().equals(domainVO.getValue()))
			itr.remove(); 
		}

		model.addAttribute("domainList" , domainList);
		request.getSession().setAttribute("sessionDomainList", domainList);
		
		
		List<ListValueVO> geographyList = new ArrayList<>();

		if (userVO.getGeographicalAreaList() != null) {

			for (int i = 0, j = userVO.getGeographicalAreaList().size(); i < j; i++) {

				geographyList.add(new ListValueVO(((UserGeographiesVO) userVO.getGeographicalAreaList().get(i)).getGraphDomainName(), ((UserGeographiesVO) userVO.getGeographicalAreaList().get(i)).getGraphDomainCode()));

			}
			model.addAttribute("geographyList" , geographyList);
			request.getSession().setAttribute("sessiongeographyList", geographyList);

		}
		else{
			log.debug(CLASSNAME+methodName, "geographyList is empty , problem with UserVO");
		}

		if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+methodName, PretupsI.EXITED);
		}
		return "usertransfer/usertransferselect";
	}

	/**
	 * UserTransferController.java
	 * @param domain
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 * @throws ServletException
	 * @throws IOException
	 * @throws Exception
	 * List<ListValueVO>
	 * akanksha.gupta
	 * 25-Aug-2016 5:58:00 pm
	 */
	@RequestMapping(value = "/usertransfer/load-category.form", method = RequestMethod.POST)
	public @ResponseBody List<ListValueVO> loadCategory(@RequestParam("domain") String domain,  Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException,IOException

	{
		final String methodName = "#loadCategory";
		
		if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+methodName, PretupsI.ENTERED);
		}
		List<ListValueVO> categoryList ;


		categoryList = userTransferService.loadCategory();

		if(!domain.equals(PretupsI.ALL)){
			ListValueVO listValueVO ;
			if (categoryList != null && !categoryList.isEmpty()) {
				for (int i = 0, j = categoryList.size(); i < j; i++) {
					listValueVO = categoryList.get(i);

					if (!(listValueVO.getValue().split(":")[0]).equals(domain)) {
						categoryList.remove(i);
						i--;
						j--;
					}
				}
			}
		}
		request.getSession().setAttribute("category", categoryList);
		if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+methodName, PretupsI.EXITED);
		}
		return categoryList;
	}

	/**
	 * UserTransferController.java
	 * @param domain
	 * @param category
	 * @param geography
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * List<UserVO>
	 * akanksha.gupta
	 * 25-Aug-2016 5:58:06 pm
	 */ 
	@RequestMapping(value = "/usertransfer/load-user-list.form", method = RequestMethod.GET)
	public String loadUserList(@RequestParam("domain") String domain,@RequestParam("category") String category,@RequestParam("geography") String geography,
			@RequestParam("owner") String owner, Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException,IOException {

		final String methodName = "#loadUserList";

		if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+methodName, PretupsI.ENTERED+" with : DOMAIN=" + domain +",CATEGORY="+category+",GEOGRAPHY="+geography+",owner="+owner);
		}
		UserVO userVO = this.getUserFormSession(request);
		List<ListValueVO> userList ;
		userList = userTransferService.loadUserData(domain,category.split(":")[1].trim(),userVO.getNetworkID(),owner,userVO.getUserID());

		model.addAttribute("userList", userList);
		model.addAttribute("domain", domain);
		model.addAttribute("category", category);
		model.addAttribute("geography", geography);
		model.addAttribute("user", owner);
		request.getSession().setAttribute("selectedcategory", request.getParameter("category"));
		request.getSession().setAttribute("geography", request.getParameter("geography"));
		request.getSession().setAttribute("owner", request.getParameter("owner"));
		request.getSession().setAttribute("domain", request.getParameter("domain"));
		request.getSession().setAttribute("userList", request.getParameter("userList"));

		

		if(userList == null )
		{
			if (log.isDebugEnabled()) {
				log.debug(methodName, "List is null");
			}
			model.addAttribute("fail" ,PretupsRestUtil.getMessageString("no.data.found"));
			}
		else if(userList.isEmpty())
		{

			if (log.isDebugEnabled()) {
				log.debug(methodName, "List is null");
			}
			model.addAttribute("fail" ,PretupsRestUtil.getMessageString("no.data.found"));
		
		}

		if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+methodName, PretupsI.EXITED);

		}
		return "usertransfer/usersearch";
	}


	/**
	 * UserTransferController.java
	 * @param category
	 * @param ownerId
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * List<UserVO>
	 * akanksha.gupta
	 * 25-Aug-2016 5:58:10 pm
	 */
	@RequestMapping(value = "/usertransfer/load-channel-user.form", method = RequestMethod.GET)
	public String loadChannelUserList(@RequestParam("category") String category,@RequestParam("ownerId") String ownerId,
			@RequestParam("user") String user,Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException,IOException {

		final String methodName = "#loadChannelUserList";

		if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+methodName, PretupsI.ENTERED+" with : CATEGORY="+category+",OWNER_ID="+ownerId+"user="+user);
		}

		List<ListValueVO> channelUserList ;
		
		channelUserList = userTransferService.loadChannelUserData(category.split(":")[1].trim(),ownerId,user);


		if(channelUserList == null)
		{
			if (log.isDebugEnabled()) {
				log.debug(methodName, "List is null");
			}
			model.addAttribute("fail" ,PretupsRestUtil.getMessageString("no.data.found"));
		}
		else if(channelUserList.isEmpty())
		{

			if (log.isDebugEnabled()) {
				log.debug(methodName, "List is null");
			}
			model.addAttribute("fail" ,PretupsRestUtil.getMessageString("no.data.found"));
		
		}
		model.addAttribute("channelUserList", channelUserList);
		model.addAttribute("user", user);
		model.addAttribute("category", category);
		model.addAttribute("ownerId", ownerId);
		
		
		if(channelUserList != null && !channelUserList.isEmpty())
		{
			if (log.isDebugEnabled()) {
				log.debug(CLASSNAME+methodName, PretupsI.EXITED+ "DATA :"+channelUserList.get(0).toString());
			}
		}
		return "usertransfer/channelusersearch";
	}


	/**
	 * UserTransferController.java
	 * @param userID
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * String
	 * akanksha.gupta
	 * 25-Aug-2016 5:58:14 pm
	 */
	@RequestMapping(value = "/usertransfer/submit-user-transfer.form", method = RequestMethod.POST)
	public String submituserTransfer(
			Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException,IOException {
 
final String methodName = "#submituserTransfer";
		
		if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+methodName, PretupsI.ENTERED);
		}
		UserVO userVO = this.getUserFormSession(request);

		 PretupsResponse<ChannelUserVO> serviceResponse = null;
		 String userID = request.getParameter("userID");
		 String msisdn = request.getParameter("msisdn");

	if(BTSLUtil.isNullString(msisdn) && ! BTSLUtil.isNullString(userID))
		 serviceResponse =userTransferService.confirmUserDetails(userID,false,userVO);
	else if(!BTSLUtil.isNullString(msisdn))
		 serviceResponse =userTransferService.confirmUserDetails(msisdn,true,userVO);
	else 
		model.addAttribute("fail", PretupsRestUtil.getMessageString(PretupsErrorCodesI.USER_TRANSFER_INITIATED_USER_SEARCH));
	
	if(serviceResponse !=null)
	{
		if(serviceResponse.getStatus())
		{
			model.addAttribute("message", PretupsRestUtil.getMessageString(serviceResponse.getSuccessMsg(),serviceResponse.getParameters()));

			request.getSession().setAttribute("categoryCode","");
			request.getSession().removeAttribute("selectedcategory");
			request.getSession().removeAttribute("geography");
			request.getSession().removeAttribute("owner");
			request.getSession().removeAttribute("domain");
			request.getSession().removeAttribute("userList");
			request.getSession().removeAttribute("channelUserList");
			request.getSession().removeAttribute("ownerId");

			
			
		}else{
			if (log.isDebugEnabled()) {
				log.debug(CLASSNAME+methodName, "serviceResponse.getParameters() "+serviceResponse.getParameters());
			}
			if(serviceResponse.getParameters()!=null)
				model.addAttribute("fail", PretupsRestUtil.getMessageString( serviceResponse.getFormError(),serviceResponse.getParameters()));
				else
					model.addAttribute("fail", PretupsRestUtil.getMessageString( serviceResponse.getFormError()));



		}

	}
	model.addAttribute("domainList", (List<ListValueVO>)request.getSession().getAttribute("sessionDomainList"));
	model.addAttribute("geographyList", (List<ListValueVO>)request.getSession().getAttribute("sessiongeographyList"));
		if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+methodName, PretupsI.EXITED);
		}

		return "usertransfer/usertransferselect";
	}





	/**
	 * UserTransferController.java
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 * @throws ServletException
	 * @throws IOException
	 * @throws Exception
	 * String
	 * akanksha.gupta
	 * 25-Aug-2016 5:58:19 pm
	 */
	@RequestMapping(value = "/usertransfer/userTransferConfirm.form", method = RequestMethod.GET)
	public String loadUserTrfInitiatedDetails(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException, Exception
	{
		final String methodName = "#loadUserTrfInitiatedDetails";
	
	if (log.isDebugEnabled()) {
		log.debug(CLASSNAME+methodName, PretupsI.ENTERED);
	}

		UserVO userVO = this.getUserFormSession(request);
		authorise(request, response, "USRTRF01A", false);


		List<ListValueVO> domainList = userTransferService.loadDomain();


		Iterator<ListValueVO> itr = domainList.iterator(); 
		while (itr.hasNext())
		{	 ListValueVO domainVO = itr.next(); 
		if (!userVO.getDomainID().equals(domainVO.getValue()))
			itr.remove(); 
		}

		model.addAttribute("domainList" , domainList);
		request.getSession().setAttribute("sessiondomainList", domainList);
		request.getSession().removeAttribute("category");
		List<ListValueVO> geographyList = new ArrayList<>();

		if (userVO.getGeographicalAreaList() != null) {

			for (int i = 0, j = userVO.getGeographicalAreaList().size(); i < j; i++) {

				geographyList.add(new ListValueVO(((UserGeographiesVO) userVO.getGeographicalAreaList().get(i)).getGraphDomainName() , ((UserGeographiesVO) userVO.getGeographicalAreaList().get(i)).getGraphDomainCode().toString()));

			}

			model.addAttribute("geographyList" , geographyList);
			request.getSession().setAttribute("sessiongeographyList", geographyList);
		}
		else{
			log.debug(CLASSNAME+methodName, "geographyList is empty , problem with UserVO");
		}

		if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+methodName, PretupsI.EXITED);
		}
		return "usertransfer/usertransferconfirm";
	}


	/**
	 * UserTransferController.java
	 * @param domain
	 * @param category
	 * @param geography
	 * @param msisdn
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * List<ChannelUserTransferVO>
	 * akanksha.gupta
	 * 25-Aug-2016 5:58:25 pm
	 */
	@RequestMapping(value = "/usertransfer/load-initiated-user-transfer.form", method = RequestMethod.POST)
	public String loadInitiatedUserTransfer(@RequestParam("domain") String domain,@RequestParam("category") String category,@RequestParam("geography") String geography
			,@RequestParam("msisdn") String msisdn,Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException,IOException {

		final String methodName = "#loadInitiatedUserTransfer";
		List<ChannelUserTransferVO> userList ;

		if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+methodName, PretupsI.ENTERED);
		}

		String geoGraphicalDomainName = request.getParameter("geoGraphicalDomainName");
		String DomainName = request.getParameter("DomainName");
		String CategoryName = request.getParameter("CategoryName");
		UserVO userVO = this.getUserFormSession(request);
		String forward ="usertransfer/initiatedUserTransferDetails";
		
		if(BTSLUtil.isNullString(msisdn))
		{
			
			request.getSession().setAttribute("categoryCode", category.split(":")[1]);
			
			userList = userTransferService.loadInitiatedUserTransfererList(domain,category.split(":")[1].trim(),geography,userVO.getUserID());
			if(userList == null)
			{
			
				model.addAttribute("fail" ,PretupsRestUtil.getMessageString("no.data.found"));
				return "usertransfer/usertransferconfirm";
			}
			model.addAttribute("userList", userList);
			model.addAttribute("geoGraphicalDomainDesc", geoGraphicalDomainName);
			model.addAttribute("DomainDesc", DomainName);
			model.addAttribute("CategoryDesc", CategoryName);

		
		}
		else
		{
			userList  = userTransferService.loadInitiatedUserTransferDetailMsisdn(msisdn,userVO.getUserID());
			if(userList == null)
			{
				if (log.isDebugEnabled()) {
					log.debug(methodName, "List is null");
				}
				model.addAttribute("fail" ,PretupsRestUtil.getMessageString("no.data.found"));
				return "usertransfer/usertransferconfirm";
			}
			ChannelUserTransferVO channelUserTransferVO = userList.get(0);
			if(channelUserTransferVO != null)
			{
				request.getSession().setAttribute("categoryCode", userList.get(0).getUserCategoryCode());
				request.getSession().setAttribute("userID", userList.get(0).getUserID());
			}
			forward="usertransfer/usertransferotp";
		}

		
		if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+methodName, PretupsI.EXITED);
		}
		return forward;

	}


	/**
	 * UserTransferController.java
	 * @param msisdn
	 * @param request
	 * @return
	 * @throws Exception
	 * Map<String,Object>
	 * akanksha.gupta
	 * 01-Sep-2016 3:48:02 pm
	 */
	@RequestMapping(value = "/usertransfer/validate-msisdn.form", method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> validateMsisdn(@RequestParam("msisdn") String msisdn, HttpServletRequest request) throws BTSLBaseException,IOException {

		final String methodName = "#validateMsisdn";
		List<ChannelUserTransferVO> userList ;
	
			if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+methodName, PretupsI.ENTERED+" msisdn:"+msisdn);
		}

		UserVO userVO = this.getUserFormSession(request);
		Map<String, Object> map = new HashMap<>();
		if(!BTSLUtil.isNullString(msisdn))
		{
			userList  = userTransferService.loadInitiatedUserTransferDetailMsisdn(msisdn,userVO.getUserID());
			if(userList == null)
			{
				if (log.isDebugEnabled()) {
					log.debug(methodName, "List is null");
				}
					map.put("status",false);
					map.put("message",PretupsRestUtil.getMessageString("no.data.found"));

				}

			else{

				ChannelUserTransferVO channelUserTransferVO = userList.get(0);
				if(channelUserTransferVO != null)
				{
					request.getSession().setAttribute("categoryCode", userList.get(0).getUserCategoryCode());
					request.getSession().setAttribute("userID", userList.get(0).getUserID());
				}


				map.put("status",true);

			}
			


 }
		if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+methodName, PretupsI.EXITED);
		}

		return map;


	}



	/**
	 * UserTransferController.java
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 * String
	 * akanksha.gupta
	 * 01-Sep-2016 3:47:56 pm
	 */
	@RequestMapping(value = "/usertransfer/validate-otp.form", method = RequestMethod.GET)
	public String showOTPForm(Model model,HttpServletRequest request) throws BTSLBaseException,IOException {

		final String methodName = "#showOTPForm";

			if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+methodName, PretupsI.ENTERED);
		}
		UserVO userVO = this.getUserFormSession(request);
		List<ChannelUserTransferVO> userList ;

		
		String userid = request.getParameter("userid");
		if(userid== null)
		{
			String msisdn =request.getParameter("msisdn");
		
			userList  = userTransferService.loadInitiatedUserTransferDetailMsisdn(msisdn,userVO.getUserID());
			if(userList == null)
			{
				if (log.isDebugEnabled()) {
					log.debug(methodName, "List is null");
				}
				model.addAttribute("fail" ,PretupsRestUtil.getMessageString("no.data.found"));
				return "usertransfer/usertransferconfirm";
			}
			ChannelUserTransferVO channelUserTransferVO = userList.get(0);
			if(channelUserTransferVO != null)
			{
				request.getSession().setAttribute("categoryCode", userList.get(0).getUserCategoryCode());
				request.getSession().setAttribute("userID", userList.get(0).getUserID());
			}
			
	
		}
		else
			request.getSession().setAttribute("userID", request.getParameter("userid"));
		
		if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+methodName, PretupsI.EXITED);
		}
		return "usertransfer/usertransferotp";


	}




	/**
	 * UserTransferController.java
	 * @param otp
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 * String
	 * akanksha.gupta
	 * 01-Sep-2016 2:50:45 pm
	 */
	@RequestMapping(value = "/usertransfer/confirm-initiated-user-transfer.form", method = RequestMethod.POST)
	public String confirmOTPForm(@RequestParam("otp") String otp,Model model, HttpServletRequest request) throws BTSLBaseException ,IOException{

		final String methodName = "#confirmOTPForm";
		if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+methodName, PretupsI.ENTERED+"otp:"+otp);
		}
		String userID =(String)request.getSession().getAttribute("userID");
		String categoryCode =(String)request.getSession().getAttribute("categoryCode");
		
		
		PretupsResponse<Object> serviceResponse;

		UserVO userVO = this.getUserFormSession(request);
		
			serviceResponse = userTransferService.confirmUserTransfer(userID,userVO.getUserID(),otp,categoryCode);

		if(serviceResponse.getStatus())
		{
			log.debug(CLASSNAME+methodName, "Entered serviceResponse.getSuccessMsg():"+serviceResponse.getSuccessMsg()+"serviceResponse.getStatus():"+serviceResponse.getStatus());

			model.addAttribute("message", PretupsRestUtil.getMessageString(serviceResponse.getSuccessMsg(),serviceResponse.getParameters()));

		}else{

			log.debug(CLASSNAME+methodName, "Entered  serviceResponse.getFormError():"+ serviceResponse.getFormError()+"serviceResponse.getStatus():"+serviceResponse.getStatus());

			model.addAttribute("fail", PretupsRestUtil.getMessageString( serviceResponse.getFormError()));


		

	
		if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+methodName, PretupsI.EXITED+" otp:"+"******"+"userID:"+userID  );
		}


	
	}

		model.addAttribute("domainList", (List<ListValueVO>)request.getSession().getAttribute("sessiondomainList"));
		model.addAttribute("geographyList", (List<ListValueVO>)request.getSession().getAttribute("sessiondomainList"));
		return "usertransfer/usertransferotp";

	}
	

	/**
	 * UserTransferController.java
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 * String
	 * akanksha.gupta
	 * 01-Sep-2016 3:47:56 pm
	 */
	@RequestMapping(value = "/usertransfer/select-search-user.form", method = RequestMethod.GET)
	public String selectSearchedUser(Model model,HttpServletRequest request) {

		final String methodName = "#selectSearchedUser";

			if (log.isDebugEnabled()) {
			log.debug(CLASSNAME+methodName, PretupsI.ENTERED);
		}

			if(!BTSLUtil.isNullString(request.getParameter("channelUserList")))
				request.getSession().setAttribute("channelUserList", request.getParameter("channelUserList"));
			if(!BTSLUtil.isNullString(request.getParameter("userList")))
				request.getSession().setAttribute("userList", request.getParameter("userList"));
			
			
			if (log.isDebugEnabled()) {
				log.debug(CLASSNAME+methodName, request.getSession().getAttribute("selectedcategory")+"-"+request.getSession().getAttribute("channelUserList")+"-"+ request.getSession().getAttribute("geography")+"-"+ request.getSession().getAttribute("userList")+"-"+ request.getSession().getAttribute("domain"));
				log.debug(CLASSNAME+methodName, PretupsI.EXITED);
			}
				return "usertransfer/addTemp";


	}
	
	@RequestMapping(value = "/usertransfer/close_popup_window.form", method = RequestMethod.GET)
	public String closePopup(){
		return "usertransfer/closePopUp";
	}

}

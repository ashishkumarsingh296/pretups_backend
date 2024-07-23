package com.web.pretups.channel.transfer.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.validator.ValidatorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.transfer.service.O2CTransferEnquiryService;

///channelUserTransferEnquiryAction.do?method=channelUserEnquiry
@Controller
public class OperatorToChannelTransferEnquiryController extends
		CommonController {

	@Autowired
	private O2CTransferEnquiryService o2cTransferEnquiryService;

	@Autowired
	private static final String RETURN_PAGE = "channeltransfer/enquiryChannelUserView";
	@Autowired
	private static final String MAIN_PAGE   = "channeltransfer/O2CenquiryTransferView";

	private static final String LIST_PAGE   = "channeltransfer/O2CenquiryTransferList";

	private static final String FAIL_KEY    = "fail";
	
	private static final String MSG_WHEN_NO_CAT_SEL4_USER_SEARCH="Select Category Name First";

	/**
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 * @throws IOException
	 * @throws ParseException
	 * @throws ServletException 
	 */
	@RequestMapping(value = "/channeltransfer/O2Cenquiry.form", method = RequestMethod.GET)
	public String loadChannelUser(final Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws BTSLBaseException, IOException, ParseException, ServletException {
		if (log.isDebugEnabled()) {
			log.debug("loadChannelUser", PretupsI.ENTERED);
		}
		authorise(request, response, "O2CENQ006A", false);
		request.getSession().removeAttribute("o2ctransferenquiry");
		ChannelTransferEnquiryModel channelTransferEnquiryModel = new ChannelTransferEnquiryModel();
		//UserVO UserVO = this.getUserFormSession(request);
		final ChannelUserVO sessionUser = (ChannelUserVO) getUserFormSession(request);
        
		o2cTransferEnquiryService.channelUserEnquiry(sessionUser, model,
				channelTransferEnquiryModel);
		model.addAttribute("formNumber", "Panel-One");
		request.getSession().setAttribute("chanTransEnqMod", channelTransferEnquiryModel);
		//request.getSession().setAttribute("sessionUser", sessionUser);
		return RETURN_PAGE;

	}

	/**
	 * @param channelTransferEnquiryModel
	 * @param model
	 * @param request
	 * @param response
	 * @param bindingResult
	 * @return
	 * @throws BTSLBaseException
	 * @throws IOException
	 * @throws ParseException
	 * @throws ValidatorException
	 * @throws SAXException
	 */
	@RequestMapping(value = "/channeltransfer/enquirySearch.form", method = RequestMethod.POST)
	public String loadEnquirySearch(
			@ModelAttribute("O2CEnquiry") ChannelTransferEnquiryModel channelTransferEnquiryModel,
			final Model model, HttpServletRequest request,
			HttpServletResponse response,BindingResult bindingResult) throws BTSLBaseException,
			IOException, ParseException, ValidatorException, SAXException {
		if (log.isDebugEnabled()) {
			log.debug("enquirySearch", PretupsI.ENTERED);
		}
		//final ChannelUserVO sessionUserVO = (ChannelUserVO) getUserFormSession(request);
		final ChannelUserVO channelUserVO = (ChannelUserVO) this
				.getUserFormSession(request);

 		boolean bool = o2cTransferEnquiryService.enquirySearch(channelUserVO, model, channelTransferEnquiryModel, request,bindingResult);

		if (bool) {

			String Next_page = (String) request.getSession().getAttribute(
					"RETURN");
			if (request.getParameter("submitMSISDN") != null) {

				request.getSession().setAttribute("choose", "submitMSISDN");
				return Next_page;
			} else if (request.getParameter("submitUserSearch") != null) {

				request.getSession().setAttribute("choose", "submitUserSearch");
				return Next_page;
			} else {
				return Next_page;
			}
		} else {
			/*
			ChannelTransferEnquiryModel channelTransferEnqModel = (ChannelTransferEnquiryModel) request
					.getSession().getAttribute("chanTransEnqMod");
			
			model.addAttribute("o2ctransferenquiry", channelTransferEnqModel);*/
			
			ChannelTransferEnquiryModel channelTransferEnqModel = (ChannelTransferEnquiryModel) request
					.getSession().getAttribute("o2ctransferenquiry");
			
			model.addAttribute("o2ctransferenquiry", channelTransferEnqModel);
			return RETURN_PAGE;
		}

	}

	/**
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/channeltransfer/view.form", method = RequestMethod.GET)
	public String viewForm(final Model model, HttpServletRequest request,
			HttpServletResponse response) throws BTSLBaseException,
			ParseException {
		String transferID = request.getParameter("transferID");
		String selectedIndex = request.getParameter("index");
		ChannelTransferEnquiryModel chanModel = (ChannelTransferEnquiryModel) request
				.getSession().getAttribute("o2ctransferenquiry");
		chanModel.setTransferNum(transferID);
		chanModel.setSelectedIndex(selectedIndex);
		// o2cTransferEnquiryService.enquiryDetail1(model,chanModel);
		o2cTransferEnquiryService.enquiryDetail(chanModel);
		model.addAttribute("O2CTransferModel", chanModel);

		return MAIN_PAGE;
	}

	/**
	 * @param model
	 * @param request
	 * @param response
	 * @throws BTSLBaseException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/channeltransfer/downloadFile.form", method = RequestMethod.GET)
	public void downloadFileForEnq(final Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws BTSLBaseException, ParseException {
		String methodName= "downloadFileForEnq";
		ChannelTransferEnquiryModel chanModel = (ChannelTransferEnquiryModel) request
				.getSession().getAttribute("o2ctransferenquiry");
		InputStream is = null;
		OutputStream os = null;
		try {
			String fileLocation = o2cTransferEnquiryService.downloadFileforEnq(
					chanModel, request);
			File file = new File(fileLocation);
			is = new FileInputStream(file);
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=\""
					+ file.getName() + "\"");
			os = response.getOutputStream();

			byte[] buffer = new byte[1024];
			int len;
			while ((len = is.read(buffer)) != -1) {
				os.write(buffer, 0, len);
			}
			os.flush();
		} catch (IOException e) {
			throw new BTSLBaseException(e);
		} finally {
			try{
        		if(is!=null){
        			is.close();	
        		}
        	}catch(Exception e){
        		 log.errorTrace(methodName, e);
        	}
        	try{
        		if(os!=null){
        			os.close();	
        		}
        	}catch(Exception e){
        		 log.errorTrace(methodName, e);
        	}
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	}

	/**
	 * @param channelTransferEnquiryModel
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/channeltransfer/SearchUser.form", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, String>> userList(
			@ModelAttribute("O2CEnquiry") ChannelTransferEnquiryModel channelTransferEnquiryModel,
			Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception

	{
		
		List<Map<String, String>> list = new ArrayList<>();
		String UserName = request.getParameter("query");
		UserName = UserName + "%";
		String categorycode = request.getParameter("category");

		String userType = request.getParameter("categoryType");
		
		if(BTSLUtil.isNullString(categorycode)){
			 Map<String, String> map = new HashMap<>();
				map.put("loginId", MSG_WHEN_NO_CAT_SEL4_USER_SEARCH);
				map.put("userId", "");
				list.add(map);
				return list;
		 }
		final UserVO userVO = (UserVO) getUserFormSession(request);
		final ChannelUserVO channelUserVO = (ChannelUserVO) this
				.getUserFormSession(request);
		final ChannelUserVO sessionUserVO = (ChannelUserVO) getUserFormSession(request);

		
		ChannelTransferEnquiryModel channelTransferEnqModel = (ChannelTransferEnquiryModel) request
				.getSession().getAttribute("chanTransEnqMod");
		List<ListValueVO> userList = o2cTransferEnquiryService.loadUserList(
				userType, userVO, categorycode, UserName, channelUserVO,
				sessionUserVO, channelTransferEnqModel);
		if (userList.isEmpty()) {

			model.addAttribute(
					FAIL_KEY,
					PretupsRestUtil
							.getMessageString("message.channeltransfer.result.notfound"));

		}

		Iterator<ListValueVO> itr = userList.iterator();
		//List<Map<String, String>> list = new ArrayList<>();
		if (userList.size() == 0) {
			Map<String, String> map = new HashMap<>();
			map.put("loginId", "NO DATA FOUND");
			list.add(map);
		}
		while (itr.hasNext()) {
			ListValueVO object = itr.next();
			Map<String, String> map = new HashMap<>();
			 String loginId = object.getLabel() + "(" + object.getValue() +
			")";
			/*String loginId = object.getLabel();*/
			map.put("loginId", loginId);
			map.put("userId", object.getLabel());
			list.add(map);
		}

		return list;

	}
	
	/**
	 * @param model
	 * @param request
	 * @return
	 * @throws ParseException 
	 * @throws BTSLBaseException 
	 * @throws IOException 
	 */
	@RequestMapping(value="/channeltransfer/O2CenquiryBack.form",method = RequestMethod.GET)
	public String loadChannelUser(Model model ,HttpServletRequest request) throws IOException, BTSLBaseException, ParseException
	{
		  request.getSession().getAttribute("o2ctransferenquiry");
		  
		  String JSP_Screen=null;
	
		if((request.getSession().getAttribute("formNumber")).equals("Panel-One"))
			JSP_Screen= RETURN_PAGE;
		else {
			model.addAttribute("O2CTransferModel",request.getSession().getAttribute("o2ctransferenquiry"));
			
			if(((ChannelTransferEnquiryModel)request.getSession().getAttribute("o2ctransferenquiry")).getTransferList().size()>1)
				JSP_Screen= LIST_PAGE;
			
			else
				{
				JSP_Screen = RETURN_PAGE;  
				}
		}
		request.getSession().removeAttribute("chanTransEnqMod");
		ChannelTransferEnquiryModel chanTransEnqMod = new ChannelTransferEnquiryModel();
		o2cTransferEnquiryService.channelUserEnquiry((ChannelUserVO)getUserFormSession(request), model,chanTransEnqMod);
		request.getSession().setAttribute("chanTransEnqMod", chanTransEnqMod);
		return JSP_Screen;
	}
	@RequestMapping(value="/channeltransfer/O2CenquiryBackMainPage.form",method = RequestMethod.GET)
	public String loadChannelUserMainPage(Model model ,HttpServletRequest request)
	{	
		((ChannelTransferEnquiryModel)request.getSession().getAttribute("o2ctransferenquiry")).setChannelCategoryUserName((String)request.getSession().getAttribute("userName"));
		return RETURN_PAGE;
		
	}
	
}

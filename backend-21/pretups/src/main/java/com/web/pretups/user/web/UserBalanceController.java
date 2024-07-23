package com.web.pretups.user.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
import com.btsl.common.CommonController;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.user.service.UserBalanceService;
import com.web.user.web.UserModel;

@Controller
public class UserBalanceController extends CommonController{

	@Autowired
	private UserBalanceService userBalanceService;
	public static final Log _log = LogFactory
			.getLog(UserBalanceController.class.getName());
	public static final String CLASS_NAME = "UserBalanceController";
	private static final String CHANNEL_INFO = "userVO";
	private static final String USER_MODEL = "userModel";
	private static final String FORM_NO = "formNumber";
	private static final String LOGIN_ID = "loginId";
	private static final String USER_ID = "userId";
	private static final String SESSION_OWNER_ID = "sessionOwnerID";
	
	
	
	/**
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value="/balances/selfBalance.form", method=RequestMethod.GET)
	public String loadSelfBalanceForm(final Model model, HttpServletRequest request, HttpServletResponse response){
		final String methodName = "#loadSelfBalanceForm";
		
		if (_log.isDebugEnabled()) {
			_log.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
		}
		
		final String FIRST_PAGE = "user/SelfBalanceView";
		try {
			ChannelUserVO channelUserSessionVO = (ChannelUserVO) getUserFormSession(request);
			UserVO userVO = getUserFormSession(request);
			request.getSession().removeAttribute("module");
		    userVO = userBalanceService.loadSelfBalance(channelUserSessionVO, userVO);
		    model.addAttribute(CHANNEL_INFO, userVO);
		    request.getSession().setAttribute("sessionUser", userVO);
            if(userVO.getUserBalanceList().size() == 0){
            	model.addAttribute("errorMessage", PretupsRestUtil.getMessageString("pretups.user.channeluserviewbalances.label.nodatafound"));
            }
           
		    	
		    
		} catch (BTSLBaseException e) {
			
			if (_log.isDebugEnabled()) {
				_log.debug(CLASS_NAME+methodName, e);
			}
			_log.error(methodName, "BTSLBaseException:" + e.getMessage());
			_log.errorTrace(methodName, e);
		}
		
		return FIRST_PAGE;
		
	}
	
	/**
	 * @param model
	 * @param request
	 * @param response
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value="/balances/downloadFileForEnq.form", method=RequestMethod.GET)
	public void downloadFileForEnq(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{
		final String methodName = "#downloadFileForEnq";
		
		if (_log.isDebugEnabled()) {
			_log.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
		}
		
		UserVO userVO = (UserVO) request.getSession().getAttribute("sessionUser");
		InputStream is = null;
		OutputStream os = null;
		try{
			String fileLocation = userBalanceService.downloadFileForEnq(userVO, request);
			File file = new File(fileLocation);
	        is = new FileInputStream(file);
	        response.setContentType("application/vnd.ms-excel");
	        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
	        os = response.getOutputStream();
	        
	        byte[] buffer = new byte[1024];
	        int len;
	        while ((len = is.read(buffer)) != -1) {
	            os.write(buffer, 0, len);
	        }
	        os.flush();
		}catch(IOException e){
			throw new BTSLBaseException(e);
		}finally{
			try {
                if (is != null) {
                	is.close();
                }
            } catch (Exception e1) {
                _log.errorTrace(methodName, e1);
            }
        	try {
                if (os != null) {
                	os.close();
                }
            } catch (Exception e1) {
                _log.errorTrace(methodName, e1);
            }
			LogFactory.printLog(methodName, PretupsI.EXITED, _log);
		}

		

		
	}
	
	/**
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value="/balances/userBalance.form", method=RequestMethod.GET)
	public String loadUserbalanceForm(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException, ServletException, IOException{
		final String methodName = "#loadUserbalanceForm";
		final String FIRST_PAGE = "user/UserBalanceView";
		
		
		if (_log.isDebugEnabled()) {
			_log.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
		}
		
		this.authorise(request, response, "CUSRBALV01", false);
		UserVO userVO = getUserFormSession(request);
		request.getSession().removeAttribute(USER_MODEL);
		UserModel userModel = userBalanceService.loadCategory(userVO);
		userModel.setDomainName(userModel.getDomainCodeDesc());
		request.getSession().removeAttribute("formNumber");
		model.addAttribute(FORM_NO,"Panel-One");
		model.addAttribute(USER_MODEL,userModel);
		
		
		//request.getSession().setAttribute(USER_MODEL, userModel);
            if (userModel.getCategoryList().size() <= 0) {
            	model.addAttribute("errorMessage", PretupsRestUtil.getMessageString("pretups.user.loaddomainlist.error.noagentcategoryfound"));
               }


        
		return FIRST_PAGE;

		

		
	}
	
	/**
	 * 
	 * @param model
	 * @param request
	 * @param userModel
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/balances/SearchUser.form", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, String>> userList(Model model,
			HttpServletRequest request,
			@ModelAttribute("userModel") UserModel userModel,
			HttpServletResponse response) throws Exception

			{
		List<Map<String, String>> list = new ArrayList<>();
		String userName = request.getParameter("query");
		userName = userName + "%";
		String categorycode = request.getParameter("category");
		String ownerID = request.getParameter("owner");
	
		if(!("").equals(ownerID) && !(BTSLUtil.isNullString(ownerID))){
		request.getSession().setAttribute("sessionOwnerID", ownerID);
		String[] parts = ownerID.split("\\(");
		String[] part = parts[1].split("\\)");
		ownerID = part[0];
		}
		request.getSession().setAttribute("ownerID", ownerID);
		String domainCode = request.getParameter("domainCode");
		String prntDomainCode = request.getParameter("prntDomainCode");
		String index = request.getParameter("index");
		request.getSession().setAttribute("index", index);
		request.getSession().setAttribute("prntDomainCode", prntDomainCode);
		final UserVO userVO = (UserVO) getUserFormSession(request);
		
		if(BTSLUtil.isNullString(domainCode)){
			Map<String, String> map = new HashMap<>();
			map.put(LOGIN_ID, PretupsRestUtil
					.getMessageString("pretups.chnagepin.select.domain"));
			map.put(USER_ID, "");
			list.add(map);
			return list;
		}

		if(BTSLUtil.isNullString(prntDomainCode)){
			Map<String, String> map = new HashMap<>();
			map.put(LOGIN_ID, PretupsRestUtil
					.getMessageString("pretups.chnagepin.select.geography"));
			map.put(USER_ID, "");
			list.add(map);
			return list;
		}
		
		if(BTSLUtil.isNullString(categorycode)){
			Map<String, String> map = new HashMap<>();
			map.put(LOGIN_ID, PretupsRestUtil
					.getMessageString("pretups.chnagepin.select.category"));
			map.put(USER_ID, "");
			list.add(map);
			return list;
		}

		if(!("1".equalsIgnoreCase(index)) && !("CHANNEL".equalsIgnoreCase(userVO.getUserType())) && BTSLUtil.isNullString(ownerID)){

			Map<String, String> map = new HashMap<>();
			map.put(LOGIN_ID, PretupsRestUtil
					.getMessageString("pretups.chnagepin.select.owner"));
			map.put(USER_ID, "");
			list.add(map);
			return list;

		}

		List<UserVO> userList = userBalanceService.loadUserList(userVO, categorycode, ownerID, userName, domainCode, prntDomainCode, request, index);

		Iterator<UserVO> itr = userList.iterator();
		
		if (userList.isEmpty()) {
			Map<String, String> map = new HashMap<>();
			map.put("loginId", "NO DATA FOUND");
			list.add(map);
		}
		while (itr.hasNext()) {
			UserVO object = itr.next();
			Map<String, String> map = new HashMap<>();
			String loginId = object.getUserName() + "(" + object.getUserID()
					+ ")";
			map.put("loginId", loginId);
			map.put("userId", object.getUserID());
			list.add(map);
		}

		return list;

			}

	
	/**
	 * @param model
	 * @param request
	 * @param response
	 * @param userModel
	 * @param bindingResult
	 * @return
	 * @throws BTSLBaseException 
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping(value="/balances/submit-user-balance.form", method=RequestMethod.POST)
	public String loadUserBalanceForm(Model model, HttpServletRequest request, HttpServletResponse response,  @ModelAttribute("userModel") UserModel userModel, BindingResult bindingResult) throws ServletException, IOException, BTSLBaseException{
		final String methodName = "#loadUserBalanceForm";
		
		if (_log.isDebugEnabled()) {
			_log.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
		}
		
		final String FIRST_PAGE = "user/SelfBalanceView";
		try {
			ChannelUserVO channelUserSessionVO = (ChannelUserVO) getUserFormSession(request);
			UserVO userVO = getUserFormSession(request);
			request.getSession().setAttribute("module", "userBalance");
			UserModel finalModel = userBalanceService.loadCategory(userVO);
			finalModel.setDomainName(finalModel.getDomainCodeDesc()); 
			String parentDomain = finalModel.getParentDomainDesc();
			if(userModel.getSearchLoginId() != null){
				finalModel.setSearchLoginId(userModel.getSearchLoginId());
			}
			
			if(userModel.getSearchMsisdn() != null){
				finalModel.setSearchMsisdn(userModel.getSearchMsisdn());
			}
			if(userModel.getDomainCodeDesc() != null){
				finalModel.setDomainCodeDesc(userModel.getDomainCodeDesc());
			}
			if(userModel.getChannelCategoryCode() != null){
				finalModel.setChannelCategoryCode(userModel.getChannelCategoryCode());
			}
			if(userModel.getParentDomainDesc() != null){
				finalModel.setParentDomainDesc(userModel.getParentDomainDesc());
			}
			if(userModel.getUserId() != null){
				finalModel.setUserId(userModel.getUserId());
			}

			boolean result = userBalanceService.loadUserBalance(model, finalModel, userVO, channelUserSessionVO, bindingResult, request);
			if(request.getSession().getAttribute(SESSION_OWNER_ID) != null){
				String ownerID = request.getSession().getAttribute(SESSION_OWNER_ID).toString();
				finalModel.setOwnerID(ownerID);
			}	
			String domainCode = finalModel.getDomainCode();
			finalModel.setDomainCode(finalModel.getDomainCodeDesc());
			if(finalModel.getDomainList() != null){
				userBalanceService.getCategoryList(finalModel);
			}
			finalModel.setDomainCode(domainCode);
			if(finalModel.getAssociatedGeographicalList() == null){
				finalModel.setParentDomainDesc(parentDomain);
			}
   		 if (result) {
   			 return FIRST_PAGE;
            
			}
		    
		} catch (BTSLBaseException e) {
			
			if (_log.isDebugEnabled()) {
				_log.debug(CLASS_NAME+methodName, e);
			}
			
			_log.error(methodName, "BTSLBaseException:" + e.getMessage());
			_log.errorTrace(methodName, e);
		}
		
		
		return "user/UserBalanceView";
	}
	
	/**
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value="/balances/BackUserBalance.form", method=RequestMethod.GET)
	public String loadBackUserbalanceForm(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{
		final String methodName = "#loadBackUserbalanceForm";
		final String FIRST_PAGE = "user/UserBalanceView";
		
		
		if (_log.isDebugEnabled()) {
			_log.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
		}
		
	
		return FIRST_PAGE;
	
	}	
	
	@RequestMapping(value = "/balances/changeCategory.form", method = RequestMethod.GET)
	public @ResponseBody List loadCategory(@RequestParam(value="domainC",required=true)String domainCode, @ModelAttribute("userModel") UserModel userModel){
		final String methodName = "#loadCategory";

		if (_log.isDebugEnabled()) {
			_log.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
		}
		userModel.setDomainCode(domainCode);
		userBalanceService.getCategoryList(userModel);
		return userModel.getCategoryList();
	}
	
	@RequestMapping(value = "/balances/searchOwner.form", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, String>> ownerList(Model model,
			HttpServletRequest request,
			@ModelAttribute("userModel") UserModel userModel,
			HttpServletResponse response) throws Exception

			{
		List<Map<String, String>> list = new ArrayList<>();
		String ownerName = request.getParameter("query");
		ownerName = ownerName + "%";
		String domainCode = request.getParameter("domainCode");
		String prntDomainCode = request.getParameter("prntDomainCode");
		final UserVO userVO = (UserVO) getUserFormSession(request);
		
		if(BTSLUtil.isNullString(domainCode)){
			Map<String, String> map = new HashMap<>();
			map.put(LOGIN_ID, PretupsRestUtil
					.getMessageString("pretups.chnagepin.select.domain"));
			map.put(USER_ID, "");
			list.add(map);
			return list;
		}

		if(BTSLUtil.isNullString(prntDomainCode)){
			Map<String, String> map = new HashMap<>();
			map.put(LOGIN_ID, PretupsRestUtil
					.getMessageString("pretups.chnagepin.select.geography"));
			map.put(USER_ID, "");
			list.add(map);
			return list;
		}

		List<UserVO> userList = userBalanceService.loadOwnerList(userVO, prntDomainCode, ownerName, domainCode, request);

		Iterator<UserVO> itr = userList.iterator();
		
		if (userList.isEmpty()) {
			Map<String, String> map = new HashMap<>();
			map.put("loginId", "NO DATA FOUND");
			list.add(map);
		}
		while (itr.hasNext()) {
			UserVO object = itr.next();
			Map<String, String> map = new HashMap<>();
			String loginId = object.getUserName() + "(" + object.getUserID()
					+ ")";
			map.put("loginId", loginId);
			map.put("userId", object.getLoginID());
			list.add(map);
		}

		return list;

	}

	
}

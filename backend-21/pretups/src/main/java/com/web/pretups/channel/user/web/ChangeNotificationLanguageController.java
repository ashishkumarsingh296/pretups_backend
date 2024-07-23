package com.web.pretups.channel.user.web;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.reports.web.UsersReportModel;
import com.web.pretups.channel.user.service.ChangeNotificationLanguageService;



/**
 * @author rahul.arya
 *
 */
@Controller
public class ChangeNotificationLanguageController extends
CommonController {

	
	/**
	 * Log used
	 */
	public static final Log log = LogFactory.getLog(ChangeNotificationLanguageController.class.getName());
	@Autowired
	private ChangeNotificationLanguageService changeNotificationLanguageService;
	
	private static final String MSG_WHEN_NO_CAT_SEL4_USER_SEARCH = "SELECT CATEGORY FIRST";
	
	private static final String PANEL_NO = "PanelNo";
	
    private static final String LOGIN_ID = "loginId";
	
	private static final String USER_ID = "userId";
	
	private static final String FIRST_PAGE="channeluser/SelfInformationView";
	
	private static final String SECOND_PAGE="channeluser/selflanguageView";
	
	private static final String CHANGE_NOTIFY_MODEL="changeNotifyModel";
	
	private static final String CHANGE_NOTIF="changeNotif";
	
	private static final String CHANGE_NOT="changeNot";
	
	@RequestMapping(value = "/channeluser/changeNotificationLanguage.form", method = RequestMethod.GET)
	public String loadChangeNotif(final Model model, HttpServletRequest request, HttpServletResponse response)
			throws BTSLBaseException, ServletException, IOException {
		if (log.isDebugEnabled()) {
        	log.debug("ChangeNotificationLanguageController#loadChangeNotif", PretupsI.ENTERED);
        }
		
		request.getSession().removeAttribute(PANEL_NO);
		this.authorise(request, response, "CHNOLG00A", false);
		 final UserVO userVO = this.getUserFormSession(request);
		ChangeLocaleModel changeLocaleModel = new ChangeLocaleModel();
		model.addAttribute(PANEL_NO,"Panel-One");
		request.getSession().removeAttribute(CHANGE_NOTIFY_MODEL);
		request.getSession().removeAttribute(CHANGE_NOT);
		if(changeNotificationLanguageService.loadSelfinfo(request,userVO,changeLocaleModel,model) == 1)
		{
			request.getSession().setAttribute(CHANGE_NOTIF, changeLocaleModel);
			return FIRST_PAGE;
		}
		else
		{
			request.getSession().setAttribute(CHANGE_NOTIF, changeLocaleModel);
			model.addAttribute("session", "sessionTrue");
			return SECOND_PAGE;
		}

				
		
	}
	
	@RequestMapping(value = "/channeluser/enquirySearchNext.form", method = RequestMethod.POST)
	public String loadChangeNotifNext(final Model model, HttpServletRequest request, HttpServletResponse response,@ModelAttribute("changeNotifyModel")ChangeLocaleModel changeLocaleModel,BindingResult bindingResult)
			throws BTSLBaseException, ServletException, IOException {
		if (log.isDebugEnabled()) {
        	log.debug("ChangeNotificationLanguageController#loadChangeNotifNext", PretupsI.ENTERED);
        }
		
		final UserVO userVO = this.getUserFormSession(request);
		
		ChangeLocaleModel changeLocaleModelNew = (ChangeLocaleModel) request.getSession().getAttribute(CHANGE_NOTIF);
		
		changeLocaleModelNew.setMsisdn(changeLocaleModel.getMsisdn());
		
		changeLocaleModelNew.setCategoryCode(changeLocaleModel.getCategoryCode());
		
		changeLocaleModelNew.setUserName(changeLocaleModel.getUserName());
		
		if(changeNotificationLanguageService.loadUserPhoneDetails(changeLocaleModelNew,request,response,model,userVO,bindingResult))
		{
			model.addAttribute(CHANGE_NOTIFY_MODEL, changeLocaleModelNew);
			return SECOND_PAGE;
		}
		else
		{
			
			model.addAttribute(CHANGE_NOTIFY_MODEL, changeLocaleModelNew);
			return FIRST_PAGE;
		}
		
		
	}
	
	@RequestMapping(value = "/channeluser/submit-change-lang.form", method = RequestMethod.POST)
	public String loadChangeNotifNextLanguage(final Model model, HttpServletRequest request, HttpServletResponse response,@ModelAttribute("changeNoti")ChangeLocaleModel changeLocaleModel,BindingResult bindingResult)
			throws BTSLBaseException, ServletException, IOException, SQLException
			{
		if (log.isDebugEnabled()) {
        	log.debug("ChangeNotificationLanguageController#loadChangeNotifNextLanguage", PretupsI.ENTERED);
        }
		ChangeLocaleModel changeLocaleModelNew;
		final UserVO userVO = this.getUserFormSession(request);
		request.getSession().removeAttribute(CHANGE_NOT);
		if((ChangeLocaleModel) request.getSession().getAttribute(CHANGE_NOTIFY_MODEL)== null)
		{
			changeLocaleModelNew = (ChangeLocaleModel) request.getSession().getAttribute(CHANGE_NOTIF);
		}
		else
		{
			changeLocaleModelNew = (ChangeLocaleModel) request.getSession().getAttribute(CHANGE_NOTIFY_MODEL);
		}
		for(int i=0;i<changeLocaleModelNew.getUserPhoneInfoListSize();i++)
		{
			changeLocaleModelNew.getUserPhoneInfoList().get(i).setLanguageCode(changeLocaleModel.getUserPhoneInfoList().get(i).getLanguageCode());
			changeLocaleModelNew.getUserPhoneInfoList().get(i).setStatus(changeLocaleModel.getUserPhoneInfoList().get(i).getStatus());
		}
		
		changeNotificationLanguageService.loadconfirmSelfLang(changeLocaleModelNew,request,response,model,userVO,bindingResult);
			
		model.addAttribute(CHANGE_NOTIFY_MODEL, changeLocaleModelNew);
		
		request.getSession().setAttribute(CHANGE_NOT, changeLocaleModelNew);
		
		
		return SECOND_PAGE;
			
			}
	
	@RequestMapping(value="/channeluser/backChangePin.form", method = RequestMethod.GET)
	public String enquirySearchBack(final Model model,@ModelAttribute UsersReportModel usersReportModel,
			HttpServletRequest request, HttpServletResponse response)
			throws BTSLBaseException, IOException, ParseException, ServletException {
		
		
		request.getSession().removeAttribute(CHANGE_NOT);
		return FIRST_PAGE;

	}
	
    /**
     * @param model
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/channeluser/SearchFromUser.form", method = RequestMethod.GET)
    public @ResponseBody List<Map<String, String>> loadFromUserList(Model model, HttpServletRequest request,HttpServletResponse response) throws Exception {
                    List<Map<String, String>> list = new ArrayList<>();
                    String userName = request.getParameter("userName");
                    userName = userName + "%";
                  
                    String categoryCode = request.getParameter("categoryCode");
                    final UserVO userVO = this.getUserFormSession(request);
                    
                    ChangeLocaleModel changeLocaleModel = (ChangeLocaleModel) request.getSession().getAttribute(CHANGE_NOTIF);
                    
                 if (BTSLUtil.isNullString(categoryCode)) {
                                    Map<String, String> map = new HashMap<>();
                                    map.put(LOGIN_ID,MSG_WHEN_NO_CAT_SEL4_USER_SEARCH );
                                    map.put(USER_ID, "");
                                    list.add(map);
                                    return list;
                    }
                 List<UserVO> fromUserList = changeNotificationLanguageService.loadUserList(changeLocaleModel,userVO,categoryCode,userName,request);
                    Iterator<UserVO> itr = fromUserList.iterator();

                    
                    if (fromUserList.isEmpty()) {
                                    Map<String, String> map = new HashMap<>();
                                    map.put(LOGIN_ID, "NO_DATA_FOUND");
                                    map.put(USER_ID, "");
                                    list.add(map);

                    }
                    
                    
                    while (itr.hasNext()) {
            			UserVO object = itr.next();
            			Map<String, String> map = new HashMap<>();
            			 String loginId = object.getUserName() + "(" + object.getUserID() +
            			")";
            			
            			map.put(LOGIN_ID, loginId);
            			map.put(USER_ID, object.getUserID());
            			list.add(map);
            		}
                    
                    

                    return list;

    }
}

package com.web.user.web;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserVO;
import com.web.user.service.ViewSelfDetailsService;

@Controller
public class ViewSelfDetailsController extends CommonController {
	
	private static final String CLASS_NAME = "ViewSelfDetailsController";
	
	@Autowired
	private ViewSelfDetailsService viewSelfDetailsService;
	
	private static final String SUPER_ADMIN = "SUADM";
	private static final String MODEL_KEY = "viewSelfDetails";
	private static final String USER_LIST = "UserVO";
	

	/**
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/user/user_Operator_View_Action.form", method = RequestMethod.GET)
	public String loadViewObject(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException, ServletException, IOException
	 {
		final String methodName = "#loadSelfDetails";
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
		}
		UserVO userVO = this.getUserFormSession(request);
		model.addAttribute(MODEL_KEY, new UserVO());
		UserVO userVO1 = viewSelfDetailsService.loadData(userVO.getLoginID());
		userVO.setStaffUserDetails(userVO1.getStaffUserDetails());
		model.addAttribute(USER_LIST, userVO1);
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME+methodName, PretupsI.EXITED);
		}
		return "user/viewSelfDetailSpring";
	 }
	

}

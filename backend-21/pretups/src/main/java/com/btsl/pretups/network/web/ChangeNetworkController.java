package com.btsl.pretups.network.web;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.common.TypesI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.network.service.ChangeNetworkService;
import com.btsl.user.businesslogic.UserVO;


@Controller
public class ChangeNetworkController extends CommonController{
	
    
	public static final Log LOG = LogFactory.getLog(ChangeNetworkController.class.getName());
	
	@Autowired
	private ChangeNetworkService changeNetworkService;
	private static final String CLASS_NAME = "loadChangeNetwork";
	private static final String RETURN_PAGE = "network/ChangeNetworkSpring";
	private static final String DATA_LIST = "dataList";
	
	
	@RequestMapping(value="/network/change-network.form", method=RequestMethod.GET)
	public String loadChangeNetwork(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{
		final String methodName = "#loadChangeNetwork";
		
		if (LOG.isDebugEnabled()) {
			LOG.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
		}
		
		try {
          
			HttpSession session = request.getSession();
            String leftMenu = (String) session.getAttribute("leftMenu");
            if (!TypesI.NO.equals(leftMenu)) {
                this.authorise(request, response, "CHNW001", false);
            }

            UserVO userVO = getUserFormSession(request); 
           

            String loginId=userVO.getLoginID();        
             List <NetworkVO> list = changeNetworkService.loadData(loginId,model);
        	if(list == null){
				
        		model.addAttribute("fail", true);
        		
			}
			else {
				
				session.setAttribute(DATA_LIST, list);
				return RETURN_PAGE;
			}
            
            
		} catch (ServletException | IOException | BTSLBaseException exception) {
			if (LOG.isDebugEnabled()) {
				LOG.debug(CLASS_NAME+methodName, exception);
			}
			throw new BTSLBaseException(exception);
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug(CLASS_NAME+methodName, PretupsI.EXITED);
		}
		return RETURN_PAGE;
		
	
	}
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/network/submit-change-network.form", method=RequestMethod.POST)
	public String processChangeNetwork(final Model model, HttpServletRequest request, HttpServletResponse response, @ModelAttribute("networkVO") NetworkVO networkVO) throws BTSLBaseException{
	    
		final String methodName = "#processChangeNetwork";
		if (LOG.isDebugEnabled()) {
			LOG.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
		}
		try {
            
			 UserVO userVO = getUserFormSession(request); 
		
			changeNetworkService.processData(userVO,networkVO.getNetworkCode(), model);
			HttpSession session = request.getSession();
            if(userVO.getUserID() == null)
            {
            	model.addAttribute("fail", true);
            	return RETURN_PAGE;	
            }
            List<NetworkVO> list = (List<NetworkVO>) request.getSession().getAttribute(DATA_LIST);
            model.addAttribute(DATA_LIST, list);
			session.setAttribute("user", userVO);
            
            
		} catch (BTSLBaseException exception) {
			if (LOG.isDebugEnabled()) {
				LOG.debug(CLASS_NAME+methodName, exception);
			}
			throw new BTSLBaseException(exception);
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug(CLASS_NAME+methodName, PretupsI.EXITED);
		}
		
		
		return RETURN_PAGE;	
	}

}

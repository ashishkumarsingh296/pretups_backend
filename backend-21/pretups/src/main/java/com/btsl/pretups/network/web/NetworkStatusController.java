package com.btsl.pretups.network.web;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.validator.ValidatorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.WebRequest;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.network.service.NetworkStatusService;
import com.btsl.user.businesslogic.UserVO;


/**
 * @author 
 *
 */
@Controller
public class NetworkStatusController extends CommonController {
	
	
	
	@Autowired
	private NetworkStatusService networkStatusService;
	private static final String CLASS_NAME = "NetworkStatusController";
	private static final String DATA_LIST = "dataList";
	private static final String NETWORKVO = "networkVO";
	private static final String RETURN_PAGE = "network/network_Status_Spring";
	
	/**
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value="/network/network_Status.form", method=RequestMethod.GET)
	public String loadnetworkStatusForm(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{
		
		final String methodName = "#loadnetworkStatusForm";
	
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
		}
		
		try {
            // authorize the user for role NS001(jsp name is network_Status_Spring.jsp)
            this.authorise(request, response, "NS001", false);
            
            UserVO userVO =  getUserFormSession(request);
            NetworkVO networkVO = new NetworkVO();
            List<NetworkVO> dataList = null; 
            String loginId=userVO.getLoginID();
            try {
				dataList= networkStatusService.loadData(loginId);
			} catch (SQLException  |ValidatorException |SAXException e) {
			
				if (log.isDebugEnabled()) {
					log.debug(CLASS_NAME+methodName, e);
				}
				throw new BTSLBaseException(e);
			} 
            
            
            networkVO.setDataList(dataList);
            model.addAttribute(DATA_LIST, dataList);
            
            
		} catch (ServletException | IOException | BTSLBaseException exception) {
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME+methodName, exception);
			}
			throw new BTSLBaseException(exception);
		}
		
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME+methodName, PretupsI.EXITED);
		}
		return RETURN_PAGE;
		

}
	/**
	 * @param model
	 * @param request
	 * @param response
	 * @param networkVO
	 * @return
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value="/network/save-network-status.form", method=RequestMethod.POST)
	public String saveNetworkStatusForm(final Model model, HttpServletRequest request, HttpServletResponse response, @ModelAttribute("networkVO") NetworkVO networkVO, WebRequest req) throws BTSLBaseException {
		
		
		String[] language1message = req.getParameterValues("newLanguage1Message");
		String[] language2message = req.getParameterValues("newLanguage2Message");
		networkVO.setNewLanguage1Message(language1message);
		networkVO.setNewLanguage2Message(language2message);
		final String methodName = "#savenetworkStatusForm";
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
		}
		 UserVO userVO =  getUserFormSession(request);
	
		 model.addAttribute(NETWORKVO, networkVO);
		  boolean status=false;
		 try {
			 if(csrfcheck(request, model)){
				 return "common/csrfmessage";
			 }
			 
			 List<NetworkVO> list = (List<NetworkVO>) request.getSession().getAttribute(DATA_LIST);
			 String[] checkedNC = networkVO.getNewNetworkStatus();
			
			 status = networkStatusService.processData(networkVO,userVO.getLoginID(),model);

				if(status){
				
					return RETURN_PAGE;
				}
				else {
					
					model.addAttribute("fail", true);
				}
			
				   
		} catch ( BTSLBaseException | NoSuchAlgorithmException | ServletException e) {
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME+methodName, e);
			}
		
		}
		
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME+methodName, PretupsI.EXITED);
		}
		
         
	
		 return RETURN_PAGE;
	
}
}
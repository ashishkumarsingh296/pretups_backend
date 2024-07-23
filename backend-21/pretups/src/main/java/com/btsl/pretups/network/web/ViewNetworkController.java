package com.btsl.pretups.network.web;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.common.TypesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkVO;
import com.btsl.pretups.network.service.ShowNetworkService;
import com.btsl.pretups.network.service.ViewNetworkService;
import com.btsl.user.businesslogic.UserVO;

/**
 * @author 
 *
 */
@Controller
public class ViewNetworkController extends CommonController  {
	
	private static final String CLASS_NAME = "ViewNetworkController";
	
	@Autowired
	private ViewNetworkService viewNetworkService;
	@Autowired
	private ShowNetworkService showNetworkService;
	private static final String SUPER_ADMIN = "SUADM";
	private static final String MODEL_KEY = "viewNetwork";
	private static final String STATUS_TYPE = "STAT";
	private static final String NETWORK_TYPE = "NTTYP";
	private static final String DATA_LIST = "dataType";
	private static final String STATUS_LIST = "statusList";
	private static final String NETWORK_TYPE_LIST = "networkTypeList";
	private static final String SERVICE_SET_LIST = "serviceSetList";
	
	
	/**
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "/network/network_view_action.form", method = RequestMethod.GET)
	public String loadViewObject(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException, ServletException, IOException
	 {
		final String methodName = "#loadViewObject";
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
		}
		
		try {
			authorise(request, response, "NW3001", false);
			
			UserVO userVO = this.getUserFormSession(request);

			model.addAttribute(MODEL_KEY, new NetworkVO());
			String status = TypesI.NO;
			List<NetworkVO> dataList = viewNetworkService.loadData(userVO.getLoginID(),status,userVO.getNetworkID());
			NetworkVO networkVO=new NetworkVO();
			model.addAttribute(DATA_LIST, dataList);
			//model.addAttribute("NetworkVO",dataList);
			if("NWADM".equalsIgnoreCase(userVO.getCategoryVO().getCategoryCode())){
				networkVO=dataList.get(0);
				model.addAttribute("NetworkVO",networkVO);
				return "network/viewNetworkListSpring";
			}
			if("SSADM".equalsIgnoreCase(userVO.getCategoryVO().getCategoryCode())){
				networkVO=dataList.get(0);
				model.addAttribute("NetworkVO",networkVO);
				return "network/viewNetworkListSpring";
			}
			if("SUNADM".equalsIgnoreCase(userVO.getCategoryVO().getCategoryCode())){
				networkVO=dataList.get(0);
				model.addAttribute("NetworkVO",networkVO);
				return "network/viewNetworkListSpring";
			}
			
		} catch (ServletException | IOException | BTSLBaseException exception) {
			
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME+methodName, exception);
			}
			throw new BTSLBaseException(exception);
		}
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME+methodName, PretupsI.EXITED);
		}
		return "network/viewNetworkDetailSpring";
	 }
	
	
	@RequestMapping(value = "/network/viewNetworkListSpring.form", method = RequestMethod.POST)
	public String showViewObject(@ModelAttribute("viewNetworkForm") NetworkVO networkVO,final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException, ServletException, IOException
	{
		final String methodName = "showNetworkDetail";
		if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
		try {
			authorise(request, response, "NW3002", false);
			
			UserVO userVO = this.getUserFormSession(request);

			
			NetworkVO networkVOObj = showNetworkService.showData(networkVO.getNetworkCode());
			
			
			model.addAttribute("NetworkVO", networkVOObj);
			
			
			
		} catch (ServletException | IOException | BTSLBaseException exception) {
			
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME+methodName, exception);
			}
			throw new BTSLBaseException(exception);
		}
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME+methodName, PretupsI.EXITED);
		}
       
		
		return "network/viewNetworkListSpring";
	}
	
	
	
	
	
	

}

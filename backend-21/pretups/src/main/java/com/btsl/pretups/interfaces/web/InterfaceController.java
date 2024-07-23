package com.btsl.pretups.interfaces.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.pretups.interfaces.businesslogic.InterfaceVO;
import com.btsl.pretups.interfaces.service.InterfaceManagementService;
import com.btsl.user.businesslogic.UserVO;

@Controller
public class InterfaceController extends CommonController {

	@Autowired
	private InterfaceManagementService interfaceManagementService;
	
	/**
	 * Load Interface Category form 
	 *
	 * @param request  The HttpServletRequest object
	 * @param response The HttpServletResponse object
	 * @param model The Model object
	 * @return String the path of view also store user type and module in model object
	 * @throws BTSLBaseException 
	 * @throws IOException 
	 * @throws ServletException 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/masters/loadInterfaceDetails.form", method = RequestMethod.GET)
	public String loadInterfaceCategorySelectForm(final Model model, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BTSLBaseException{
		
		if (log.isDebugEnabled()) {
			log.debug("InterfaceController#loadInterfaceCategorySelectForm", "Entered ");
		}
		authorise(request, response, "IF001A", false);
		List<ListValueVO> lookupCategory = null;
		if(request.getSession().getAttribute("lookupCategory") == null){
			lookupCategory = interfaceManagementService.loadInterfaceCategory();
			request.getSession().setAttribute("lookupCategory", lookupCategory);
		}else{
			if (log.isDebugEnabled()) {
				log.debug("InterfaceController#loadInterfaceCategorySelectForm", "Loading Category from session");
			}
			lookupCategory = (List<ListValueVO>) request.getSession().getAttribute("lookupCategory");
		}
		model.addAttribute("lookupCategory", lookupCategory);
		model.addAttribute("interfaceCategoryVO", new InterfaceVO());
		if (log.isDebugEnabled()) {
			log.debug("InterfaceController#loadInterfaceCategorySelectForm", "Exiting");
		}
		return "interfaces/selectInterfaceCategory";
	}
	
	
	/**
	 * Load Interface Category Details
	 * @param request  The HttpServletRequest object
	 * @param response The HttpServletResponse object
	 * @param model The Model object
	 * @return String the path of view also store user type and module in model object
	 * @throws BTSLBaseException 
	 * @throws RuntimeException 
	 * @throws IOException 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/masters/loadInterfaceDetails.form", method = RequestMethod.POST)
	public String loadInterfaceDetails(@ModelAttribute("interfaceCategoryVO") InterfaceVO interfaceCategoryVO, BindingResult bindingResult,
			Model model, HttpServletRequest request) throws BTSLBaseException, IOException, RuntimeException{
		
		if (log.isDebugEnabled()) {
			log.debug("InterfaceController#loadInterfaceDetails", "Entered ");
		}
		UserVO userVO = this.getUserFormSession(request);
		List<InterfaceVO> list = new ArrayList<InterfaceVO>();
		if(!interfaceManagementService.loadInterfaceDetails(interfaceCategoryVO, userVO, bindingResult, list)){
			if (bindingResult.hasGlobalErrors()) {
				model.addAttribute("fail", true);
				model.addAttribute("lookupCategory", (List<ListValueVO>)request.getSession().getAttribute("lookupCategory"));
			}
			return "interfaces/selectInterfaceCategory";
		}
		model.addAttribute("interfaceDetailList", list);
		model.addAttribute("interfaceCategory", list.get(0).getInterfaceCategory());
		if (log.isDebugEnabled()) {
			log.debug("InterfaceController#loadInterfaceDetails", "Exiting");
		}
		return "interfaces/interfaceDetails";
	}
	
	
	/**
	 * Delete Interface 
	 * @param request  The HttpServletRequest object
	 * @param response The HttpServletResponse object
	 * @param model The Model object
	 * @return String the path of view also store user type and module in model object
	 * @throws BTSLBaseException 
	 * @throws RuntimeException 
	 * @throws IOException 
	 */

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/masters/delete-interface.form", method = RequestMethod.POST)
	public String deleteInterface(@ModelAttribute("interfaceCategoryVO") InterfaceVO interfaceCategoryVO, BindingResult bindingResult, final Model model, HttpServletRequest request) throws BTSLBaseException, IOException, RuntimeException{
		
		if (log.isDebugEnabled()) {
			log.debug("InterfaceController#deleteInterface", "Entered ");
		}
		
		UserVO userVO = this.getUserFormSession(request);
		
		String[] splitedInterfaceIdAndType = interfaceCategoryVO.getSelectedInterface().split("@");
		if(!interfaceManagementService.deleteInterface(splitedInterfaceIdAndType[0], splitedInterfaceIdAndType[1], bindingResult, userVO)){
			if (bindingResult.hasGlobalErrors()) {
				model.addAttribute("fail", true);
				model.addAttribute("lookupCategory", (List<ListValueVO>)request.getSession().getAttribute("lookupCategory"));
			}
		}else{
			model.addAttribute("success", PretupsRestUtil.getMessageString("interfaces.addmodify.delete.success"));
		}
		
		if (log.isDebugEnabled()) {
			log.debug("InterfaceController#deleteInterface", "Exiting");
		}
		return "interfaces/selectInterfaceCategory";
	}
	
	
	/**
	 * Load Add Interface form 
	 * @param request  The HttpServletRequest object
	 * @param response The HttpServletResponse object
	 * @param model The Model object
	 * @return String the path of view also store user type and module in model object
	 * @throws BTSLBaseException 
	 * @throws RuntimeException 
	 * @throws IOException 
	 */
	@RequestMapping(value = "/masters/add-interface.form", method = RequestMethod.GET)
	public String loadAddInterfaceForm(final Model model, HttpServletRequest request, HttpServletResponse response){
		if (log.isDebugEnabled()) {
			log.debug("InterfaceController#loadAddInterfaceForm", "Entered ");
		}
		
		if (log.isDebugEnabled()) {
			log.debug("InterfaceController#loadAddInterfaceForm", "Exiting ");
		}
		return "interfaces/addInterface";
	}
	
}

package com.web.pretups.channel.query.web;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.query.Service.C2STransferEnquiryService;
/**
 * 
 * @author Deepa.Shyam
 * This class provides method for C2S transfer Enquiry for channel users and Operator Users
 */
@Controller
public class C2STransferEnquiryController extends CommonController implements PretupsI{
	
	public static final String CLASS_NAME = "C2STransferEnquiryController";
	public static final String C2SEnquiry_MODEL= "C2STransferEnquiryModel";
	public static final String C2SEnquiry_MODEL_JSP= "C2SEnquiryModel";
	public static final String COMMON_GLOBAL_ERROR = "common/globalError";
	private static final String PANEL_NAME="formNumber";
	private static final String PretupsI_CHANNEL_USER_TYPE=PretupsI.CHANNEL_USER_TYPE;
	private static final String PretupsI_YES= PretupsI.YES;
	
	
	@Autowired
	private C2STransferEnquiryService c2sTransferEnquiryService;
	private static final String FAIL="fail";
	private static final String C2S_TRANSFER_ENQUIRY = "c2s/c2sTransferEnquiryView";
	private static final String C2S_TRANSFER_ENQUIRY_DETAILS = "c2s/c2sTransferEnquiryDetailsView";
	private static final String C2S_TRANSFER_ENQUIRY_ITEMS_DETAILS = "c2s/c2sTransferItemsDetailsView";

	/**
	 * The loadC2sTransferEnquiryForm method will load all the data that is required when c2s transfer enquiry is cliked
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value="/c2sTransfer/c2s-Transfer-Enquiry.form", method = RequestMethod.GET)
	public String loadC2sTransferEnquiryForm(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{
		
		final String  methodName = "loadC2sTransferEnquiryForm";
		if (log.isDebugEnabled()) {
			log.debug(methodName ,PretupsI.ENTERED);
			}
		try {    
			request.getSession().removeAttribute(C2SEnquiry_MODEL);
			request.getSession().removeAttribute(PANEL_NAME);
			authorise(request, response, "C2STENQ01A", false);
			UserVO userVO = this.getUserFormSession(request);
			C2STransferEnquiryModel c2sTransferEnquiryModel = new C2STransferEnquiryModel();
			c2sTransferEnquiryService.loadList(userVO, c2sTransferEnquiryModel, model);
			if(model.containsAttribute(FAIL)){
				return COMMON_GLOBAL_ERROR;
			}
			model.addAttribute(C2SEnquiry_MODEL, c2sTransferEnquiryModel);
			model.addAttribute(PANEL_NAME, "Panel-One");
			request.getSession().setAttribute(C2SEnquiry_MODEL_JSP, c2sTransferEnquiryModel);
			request.getSession().setAttribute(PANEL_NAME, "Panel-One");
			request.getSession().setAttribute(PretupsI_CHANNEL_USER_TYPE, PretupsI.CHANNEL_USER_TYPE);
			request.getSession().setAttribute(PretupsI_YES, "Y");
			
		} catch (BTSLBaseException | ServletException | IOException e) {
			throw new BTSLBaseException(e);
		}	
		if (log.isDebugEnabled()) {
			log.debug(methodName ,PretupsI.EXITED);
			}
		return C2S_TRANSFER_ENQUIRY;
	}
	
	/**
	 * The loadC2sTransferEnquiryDetailsForm method will fetche the data from db when submit button is clicked.
	 * @param c2sjspModel
	 * @param model
	 * @param bindingResult
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value="/c2sTransfer/submit-C2Stransfer-Enquiry.form", method =RequestMethod.POST)
	public String loadC2sTransferEnquiryDetailsForm(@ModelAttribute("c2sTransfer")  C2STransferEnquiryModel c2sjspModel,final Model model,BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{
		final String  methodName = "loadC2sTransferEnquiryDetailsForm";
		if (log.isDebugEnabled()) {
			log.debug(methodName ,PretupsI.ENTERED);
			}
		try{
			UserVO userVO = this.getUserFormSession(request);
			request.getSession().setAttribute("sessionUser", userVO);
			C2STransferEnquiryModel c2sTransferEnquiryModel=(C2STransferEnquiryModel)request.getSession().getAttribute(C2SEnquiry_MODEL_JSP);
			c2sTransferEnquiryModel.setServiceType(c2sjspModel.getServiceType());
			c2sTransferEnquiryModel.setCurrentDateFlag(c2sjspModel.getCurrentDateFlag());
			c2sTransferEnquiryModel.setFromDate(c2sjspModel.getFromDate());
			c2sTransferEnquiryModel.setToDate(c2sjspModel.getToDate());
			if(!BTSLUtil.isNullString(c2sjspModel.getTransferID()))
			{
				c2sTransferEnquiryModel.setTransferID(c2sjspModel.getTransferID());
				c2sTransferEnquiryModel.setSenderMsisdn("");
				c2sTransferEnquiryModel.setReceiverMsisdn("");
			}
			else if(!BTSLUtil.isNullString(c2sjspModel.getSenderMsisdn())){
				c2sTransferEnquiryModel.setSenderMsisdn(c2sjspModel.getSenderMsisdn());
				c2sTransferEnquiryModel.setTransferID("");
				c2sTransferEnquiryModel.setReceiverMsisdn("");
			}
			else if(!BTSLUtil.isNullString(c2sjspModel.getReceiverMsisdn()))
			{
				c2sTransferEnquiryModel.setReceiverMsisdn(c2sjspModel.getReceiverMsisdn());
				c2sTransferEnquiryModel.setTransferID("");
				c2sTransferEnquiryModel.setSenderMsisdn("");
			}
			if(c2sTransferEnquiryService.loadTransferEnquiryListFromData(userVO, c2sTransferEnquiryModel,bindingResult, request, model))
			{
				model.addAttribute(C2SEnquiry_MODEL, c2sTransferEnquiryModel);
				request.getSession().setAttribute(C2SEnquiry_MODEL_JSP, c2sTransferEnquiryModel);
			}
			else{
				request.getSession().setAttribute(C2SEnquiry_MODEL, c2sTransferEnquiryModel);
				return C2S_TRANSFER_ENQUIRY;
			}
		
			/*if(model.containsAttribute(FAIL)){
				return COMMON_GLOBAL_ERROR;
			}*/
		} catch (Exception e) {
			throw new BTSLBaseException(e);
		}	
		if (log.isDebugEnabled()) {
			log.debug(methodName ,PretupsI.EXITED);
			}
		return C2S_TRANSFER_ENQUIRY_DETAILS;
		}
	
	/**
	 * The downloadFileForEnq method performs download file that contains the c2s transfers list.
	 * the file contains data in xls format
	 * @param model
	 * @param request
	 * @param response
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value="c2sTransfer/download-C2Stransfer-Enquiry.form", method =RequestMethod.GET)
    public void downloadFileForEnq(final Model model,HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException {
		
		 final String methodName = "downloadFileForEnq";
		 if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
			}
			C2STransferEnquiryModel c2sTransferEnquiryModel=(C2STransferEnquiryModel)request.getSession().getAttribute(C2SEnquiry_MODEL_JSP);
			InputStream is = null;
			OutputStream os = null;
			try{
				String fileLocation = c2sTransferEnquiryService.downloadFileForEnq(c2sTransferEnquiryModel,request,model);
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
		        if(model.containsAttribute(FAIL)){
				//	return COMMON_GLOBAL_ERROR;
				}
			}catch(IOException e){
				throw new BTSLBaseException(e);
			}finally{
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
				if (log.isDebugEnabled()) {
					log.debug(methodName ,PretupsI.EXITED,log);
					}
			}

	}
	
	/**
	 * The loadTransferItemsForID method gives the details about a particular transaction.
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value="/c2sTransfer/IdClicked-C2Stransfer-Enquiry.form", method =RequestMethod.GET)
	public String loadTransferItemsForID(final Model model,HttpServletRequest request,HttpServletResponse response) throws BTSLBaseException {
		 final String methodName = "loadTransferItemsForID";
		 if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
			}
		    UserVO userVO = this.getUserFormSession(request);
			request.getSession().setAttribute("sessionUser", userVO);
			C2STransferEnquiryModel c2sTransferEnquiryModel=(C2STransferEnquiryModel)request.getSession().getAttribute(C2SEnquiry_MODEL_JSP);
			String panel=(String)request.getSession().getAttribute(PANEL_NAME);
			try{
				c2sTransferEnquiryService.loadTransferItemsVOList(c2sTransferEnquiryModel, request);
				if(model.containsAttribute(FAIL)){
					return COMMON_GLOBAL_ERROR;
				}
				model.addAttribute(C2SEnquiry_MODEL,c2sTransferEnquiryModel);
				model.addAttribute(PANEL_NAME, panel);
				
			} catch (Exception e) {
				throw new BTSLBaseException(e);
			}	
			if (log.isDebugEnabled()) {
				log.debug(methodName ,PretupsI.EXITED);
				}
			return C2S_TRANSFER_ENQUIRY_ITEMS_DETAILS;
		
	}
	
	/**
	 * The backClicked takes user back to the first screen 
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value="/c2sTransfer/back-C2Stransfer-Enquiry.form", method =RequestMethod.GET)
	 public String backClicked(final Model model,HttpServletRequest request, HttpServletResponse response)throws BTSLBaseException  {
	        final String methodName = "backClicked";
	        if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
			}
            C2STransferEnquiryModel c2sTransferEnquiryModel=(C2STransferEnquiryModel)request.getSession().getAttribute(C2SEnquiry_MODEL_JSP);;
            String panel=(String)request.getSession().getAttribute(PANEL_NAME);
	        try {
	        	if(model.containsAttribute(FAIL)){
					return COMMON_GLOBAL_ERROR;
				}
				model.addAttribute(C2SEnquiry_MODEL,c2sTransferEnquiryModel);
				model.addAttribute(PANEL_NAME, panel);
	        } catch (Exception e) {
				throw new BTSLBaseException(e);
			}	
			if (log.isDebugEnabled()) {
				log.debug(methodName ,PretupsI.EXITED);
				}
			request.getSession().setAttribute(C2SEnquiry_MODEL, c2sTransferEnquiryModel);
			return C2S_TRANSFER_ENQUIRY;
	    }
	
	/**
	 * The backClickedfromItemdetails method takes user previous screen from detail page.
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value="/c2sTransfer/back-C2Stransfer-Items-Enquiry.form", method =RequestMethod.GET)
	 public String backClickedfromItemdetails(final Model model,HttpServletRequest request, HttpServletResponse response)throws BTSLBaseException  {
	        final String methodName = "backClicked";
	        if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
			}
           C2STransferEnquiryModel c2sTransferEnquiryModel=(C2STransferEnquiryModel)request.getSession().getAttribute(C2SEnquiry_MODEL_JSP);;
           String panel=(String)request.getSession().getAttribute(PANEL_NAME);
	        try {
	        	if(model.containsAttribute(FAIL)){
					return COMMON_GLOBAL_ERROR;
				}
				model.addAttribute(C2SEnquiry_MODEL,c2sTransferEnquiryModel);
				model.addAttribute(PANEL_NAME, panel);
	        } catch (Exception e) {
				throw new BTSLBaseException(e);
			}	
			if (log.isDebugEnabled()) {
				log.debug(methodName ,PretupsI.EXITED);
				}
			return C2S_TRANSFER_ENQUIRY_DETAILS;
	    }
    
}

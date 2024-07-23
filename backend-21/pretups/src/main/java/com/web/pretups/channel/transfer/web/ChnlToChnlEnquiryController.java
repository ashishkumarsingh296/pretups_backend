package com.web.pretups.channel.transfer.web;




import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.channel.transfer.service.ChnlToChnlEnquiryService;





/**
 * @author Himanshu.Kumar
 *
 */
@Controller
public class ChnlToChnlEnquiryController extends CommonController {
	
	@Autowired
	private ChnlToChnlEnquiryService chnlToChnlEnquiryService;
	private String chnlTochnlModel="ChnlToChnlEnquiryModel";

	
	/**
	 * @param chnlToChnlEnquiryModel
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value = "/channeltransfer/chnlToChnlEnquiry.form", method = RequestMethod.GET)
	public String C2CTransfer(@ModelAttribute("chnlToChnlEnquiryForm") ChnlToChnlEnquiryModel chnlToChnlEnquiryModel, final Model model, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BTSLBaseException
	 {	
		// load the user
        final UserVO userVO = getUserFormSession(request);
		this.authorise(request, response, "C2CENQ001A", false);
		request.getSession().removeAttribute(chnlTochnlModel);
		chnlToChnlEnquiryService.loadTransferTypeList(chnlToChnlEnquiryModel,userVO,model,request);
		model.addAttribute("formNumber","Panel-One");
		 request.getSession().setAttribute("chnlTochnlModelSession", chnlToChnlEnquiryModel); 
		return "channeltransfer/c2cEnquirySearchAttributeView";
		
	 }
	
	
	/**
	 * @param chnlToChnlEnquiryModel
	 * @param model
	 * @param request
	 * @param response
	 * @param bindingResult
	 * @return
	 * @throws BTSLBaseException
	 * @throws IOException
	 * @throws ValidatorException
	 * @throws SAXException
	 */
	@RequestMapping(value = "/channeltransfer/submit-c2c-enquiry.form", method = RequestMethod.POST)
	public String  C2CEnquirySubmit(@ModelAttribute("chnlToChnlEnquiryForm") ChnlToChnlEnquiryModel chnlToChnlEnquiryModel, final Model model, HttpServletRequest request, HttpServletResponse response,BindingResult bindingResult) throws BTSLBaseException, IOException, ValidatorException, SAXException {
		// load the user
        final UserVO userVO = getUserFormSession(request);
        
        if(!(chnlToChnlEnquiryService.showEnquiryDetails(userVO,chnlToChnlEnquiryModel, model, bindingResult, request,response))){
        	 request.getSession().setAttribute(chnlTochnlModel, chnlToChnlEnquiryModel);
        	return "channeltransfer/c2cEnquirySearchAttributeView";
        }
        model.addAttribute("c2cTransferList",chnlToChnlEnquiryModel);

        request.getSession().setAttribute("chnlTochnlModelSession", chnlToChnlEnquiryModel);
		return chnlToChnlEnquiryModel.getJspPath();
	}
	
	
	/**
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 * @throws IOException
	 * @throws ValidatorException
	 * @throws SAXException
	 */
	@RequestMapping(value = "/channeltransfer/view-c2c-enquiry-details.form", method =RequestMethod.GET)
	public String  viewC2CEnquiryDetails(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException, IOException, ValidatorException, SAXException {
		String transferID = request.getParameter("transferID");
		String index = request.getParameter("index");
		ChnlToChnlEnquiryModel chnlToChnlEnquiryModel = (ChnlToChnlEnquiryModel) request.getSession().getAttribute("c2cTransferList");
		chnlToChnlEnquiryModel.setTransferNumber(transferID);
		chnlToChnlEnquiryModel.setSelectedIndex(index);
		chnlToChnlEnquiryService.enquiryDetail(chnlToChnlEnquiryModel,request);
		return chnlToChnlEnquiryModel.getJspPath();
	}
	
	
	/**
	 * @param model
	 * @param request
	 * @param response
	 * @throws BTSLBaseException
	 * @throws IOException
	 */
	@RequestMapping(value = "/channeltransfer/downloadFileForEnq.form", method = RequestMethod.GET)
	public void downloadFileForEnquiry(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException,IOException {
		String methodName= "downloadFileForEnquiry";
		
		ChnlToChnlEnquiryModel theForm = (ChnlToChnlEnquiryModel)request.getSession().getAttribute("chnlTochnlModelSession");	
		InputStream is = null;
		OutputStream os = null;
        try{
        	String fileLocation = chnlToChnlEnquiryService.downloadFileForEnq(theForm, model, request, response);
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
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 * @throws IOException
	 */
	@RequestMapping(value = "/channeltransfer/backFromEnquiryPage.form", method = RequestMethod.GET)
	public String backFromEnquiryPage(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException, IOException
			 {
				final String methodName = "backFromEnquiryPage";
				ChnlToChnlEnquiryModel chnlEnquiryModel = (ChnlToChnlEnquiryModel)request.getSession().getAttribute("chnlTochnlModelSession");
				request.getSession().setAttribute(chnlTochnlModel,chnlEnquiryModel);	
				return "channeltransfer/c2cEnquirySearchAttributeView";
			 }
	
	@RequestMapping(value = "/channeltransfer/backFromDetails.form", method = RequestMethod.GET)
	public String backFromDetailsPage(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException, IOException
			 {
				final String methodName = "backFromEnquiryPage";
				String JSP_Screen=null;
				ChnlToChnlEnquiryModel chnlEnquiryModel = (ChnlToChnlEnquiryModel)request.getSession().getAttribute("chnlTochnlModelSession");
				if("formNumber".equals("Panel-One"))
				{
					JSP_Screen= "channeltransfer/c2cEnquirySearchAttributeView";
				}
				else {
					if(chnlEnquiryModel.getTransferList().size() > 1 ){
						JSP_Screen = "channeltransfer/c2cEnquiryTransferListView";	
					}
					else{
						JSP_Screen= "channeltransfer/c2cEnquirySearchAttributeView";
					}
				}
				request.getSession().setAttribute(chnlTochnlModel,chnlEnquiryModel);	
				return JSP_Screen;	
				
			 }

	
}

package com.web.pretups.channel.transfer.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

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
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserVO;
import com.web.pretups.channel.transfer.service.O2cTransferAckService;

/**
 *  This controller class is used to
 *  control the request for O2CTransferAcknowlegement Report which is accessed from
 *   channel Reports- O2C
 * 
 *
 */
@Controller
public class TransferAckController extends
CommonController  {
	
	@Autowired
	O2cTransferAckService o2cTransferAckService;


	public static final String CLASS_NAME = "TransferAckController";
	private static final Log _LOGS = LogFactory
			.getLog(TransferAckController.class.getName());
	private static final String RETURN_PAGE = "channeltransfer/transferAcknowledgementView";
	private static final String NEXT_PAGE = "channeltransfer/transferAcknowledgementDetails";
	private static final String INET_PAGE = "channeltransfer/transferNumberAckReports";

	/**
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/channeltransfer/o2cTransferAckAction.form", method = RequestMethod.GET)
	public String o2cTransferAcknowledgement(final Model model,
			HttpServletRequest request, HttpServletResponse response)
			 {
		final String methodName = "O2cTransferAcknowledgement";

		ChannelTransferAckModel channelTraAnkModel = new ChannelTransferAckModel();
		if (_LOGS.isDebugEnabled()) {
			_LOGS.debug(CLASS_NAME +methodName, PretupsI.ENTERED);
		}
		try {
			authorise(request, response, "O2CACK001", false);
		}  catch (BTSLBaseException | ServletException |IOException e) {
			
			_LOGS.errorTrace(methodName, e);
		} 
		request.getSession().removeAttribute("O2CTransferAskModel"); 
		

		request.getSession().setAttribute("usersTrsAsk", channelTraAnkModel);
		return RETURN_PAGE;
	}

	
	/**
	 * @param model
	 * @param channelTransferAckModel
	 * @param response
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/channeltransfer/O2CTransferAskBackMainPage.form", method = RequestMethod.GET)
	 public String o2ctrsfarAskSearchBack(final Model model,
			@ModelAttribute ChannelTransferAckModel channelTransferAckModel,
			HttpServletRequest request, HttpServletResponse response)
			{

		request.setAttribute("BackButton", true);
			      return RETURN_PAGE;

	}


	/**
	 * @param model
	 * @param channelTransferAckModel
	 * @param response
	 * @param request
	 * @param bindingResult
	 * @return
	 */
	@RequestMapping(value = "/channeltransfer/enquirySearcTransferAsk.form", method = RequestMethod.POST)
	public String enquirySearchNext(
			final Model model,
			@ModelAttribute("o2cTransferFormAck") ChannelTransferAckModel channelTransferAckModel,
			HttpServletRequest request, HttpServletResponse response,
			BindingResult bindingResult)  {

		 final String method="enquirySearchNext";
		 
		UserVO userVO = null;
		try {
			userVO = this.getUserFormSession(request);
		} catch (BTSLBaseException e) {
			 _LOGS.errorTrace(method, e);
			
		}

		ChannelTransferAckModel channelTransferAckModels = (ChannelTransferAckModel) request.getSession().getAttribute("usersTrsAsk");
		try {
			if (o2cTransferAckService.loadTransferAckList(request, response,channelTransferAckModel, userVO, model, bindingResult)) {
				model.addAttribute("O2CTransferAskModel", channelTransferAckModel);
				if (_LOGS.isDebugEnabled()) {
					_LOGS.debug("Data model channelTransferAckModels ", channelTransferAckModel.getTransferList().get(0));
				}
				request.getSession().setAttribute("O2CTransferAskModel",channelTransferAckModel);

				if (request.getParameter("submitUserSearch") != null) {
					return NEXT_PAGE;
				} else {
					return INET_PAGE;
				}
			}

			else {
				return RETURN_PAGE;
			}
		}
		 catch(ValidatorException|IOException|SAXException e){
			 _LOGS.errorTrace(method, e);
		 }
		return RETURN_PAGE;
	}
	
	/**
	 * @param model
	 * @param request
	 * @param response
	 * @throws BTSLBaseException
	 * @throws ServletException
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws SQLException
	 */
	@RequestMapping(value = "/channeltransfer/downloadFiles.form", method = RequestMethod.GET)
    public void downloadCSVReportFiles(final Model model, HttpServletRequest request, HttpServletResponse response)
                  throws BTSLBaseException, ServletException, IOException, InterruptedException, SQLException {
		
		String methodName= "downloadFileForEnq";
		ChannelTransferAckModel channelTransferAckModel =  (ChannelTransferAckModel) request.getSession().getAttribute("O2CTransferAskModel");
		InputStream is = null;
		OutputStream os = null;
		try {
			String fileLocation = o2cTransferAckService.downloadFileforAck(channelTransferAckModel);
			File file = new File(fileLocation);
			is = new FileInputStream(file);
			response.setContentType("text/csv");
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
	

}
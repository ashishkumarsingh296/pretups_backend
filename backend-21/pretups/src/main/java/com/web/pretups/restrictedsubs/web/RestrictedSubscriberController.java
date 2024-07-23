package com.web.pretups.restrictedsubs.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.restrictedsubs.service.BatchRechageRescheduleServiceImpl;
import com.btsl.pretups.restrictedsubs.service.RestrictedSubscriberService;
import com.btsl.pretups.restrictedsubs.service.RestrictedSubscriberServiceImpl;
import com.btsl.pretups.scheduletopup.businesslogic.ScheduleBatchMasterVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;

/**
 * This class handle schedule recharges functionality
 * @author lalit.chattar
 *
 */

@Controller
public class RestrictedSubscriberController extends CommonController {
	
	
	public static final String CLASS_NAME = "RestrictedSubscriberController";
	public static final String REST_SUBSCRIBER_MODEL= "restrictedSubscriberModel";
	public static final String REST_SUBSCRIBER_MODEL_NEW="restrictedSubscriberModel1";
	public static final String REST_SUBS_MODEL_OBJ = "restrictedSubscriberModelObject";
	public static final String VIEW_SCHEDULE_TOPUP = "restrictedsubs/viewSingleTransferSchedule";
	public static final String COMMON_GLOBAL_ERROR = "common/globalError";
	public static final String REST_SUBSCRIBER_MODEL_BACK="restrictedSubscriberModelBack";
	public static final String REST_SUBSCRIBER_MODEL_BACK_NEW="restrictedSubscriberModelBack1";
	@Autowired
	private RestrictedSubscriberService restrictedSubscriberService;
	private static final String SCHLIST="schList";
	private static final String CNCL_SINGLE_RECHARGE="restrictedsubs/cancelSingleRecharge";
	private static final String CNCL_BATCH_RECHARGE="restrictedsubs/cancelBatchRecharge";
	private static final String FAIL="fail";
	private static final String SCHEDULE_TOPUP = "restrictedsubs/scheduleTopUp";
	private static final String VIEW_RC_BATCH_LIST="restrictedsubs/viewSchRcBatchList";
	private static final String CANCEL_SINGLE_FIRST = "/pretups/restrictedsubs/cancel_schedule_recharge.form";
	private static final String CANCEL_SINGLE_FIRST_BACK = "/pretups/restrictedsubs/back-to-cancel-batch-first-schedule.form";
	private static final String CANCEL_FINAL_SINGLE_FIRST_BACK = "/pretups/restrictedsubs/cancel_schedule_recharge_selected_batch.form";
	private static final String CANCEL_FINAL_NO_BATCH_FIRST_BACK ="/pretups/restrictedsubs/cancel_batch_schedule_recharge.form";
	
	/**
	 * This method loads Schedule Recharges Screen
	 * @param model
	 * @param request
	 * @param response
	 * @return String url on the redirect page
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value="/schedule/scheduleTopUp.form", method = RequestMethod.GET)
	public String loadBatchRechargeForm(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{
		
		final String  methodName = "loadBatchRechargeForm";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		try {
			
			authorise(request, response, "SCHTOPUP01", false);
			UserVO userVO = this.getUserFormSession(request);
			RestrictedSubscriberModel restrictedSubscriberModel = new RestrictedSubscriberModel();
			restrictedSubscriberService.loadloadBatchRechargeFormData(userVO, restrictedSubscriberModel, request, model);
			if(model.containsAttribute("fail")){
				return COMMON_GLOBAL_ERROR;
			}
			model.addAttribute(REST_SUBSCRIBER_MODEL, restrictedSubscriberModel);
			model.addAttribute("restrictedSubVO", new RestrictedSubscriberModel());
			request.getSession().setAttribute(REST_SUBSCRIBER_MODEL, restrictedSubscriberModel);
			//request.getSession().removeAttribute("userVO");
		} catch (BTSLBaseException | ServletException | IOException e) {
			throw new BTSLBaseException(e);
		}	
		LogFactory.printLog(methodName, PretupsI.EXITED, log);
		return SCHEDULE_TOPUP;
	}
	
	/**
	 * Display next form for uploading file
	 * @param restrictedSubscriberModel
	 * @param model
	 * @param request
	 * @param response
	 * @param bindingResult
	 * @return
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value="/schedule/upload-file-for-schedule-recharge.form", method = RequestMethod.POST)
	public String showUploadFileForRestrictedUserForm(@ModelAttribute("restrictedSubVO") RestrictedSubscriberModel restrictedSubscriberModel, BindingResult bindingResult,  final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{
		final String  methodName = "#loadBatchRechargeForm";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		
		try{
			RestrictedSubscriberModel modelObj = (RestrictedSubscriberModel) request.getSession().getAttribute(REST_SUBSCRIBER_MODEL);
			model.addAttribute("restrictedSubVOObj", restrictedSubscriberModel);
			model.addAttribute(REST_SUBSCRIBER_MODEL, modelObj);
			restrictedSubscriberService.validateRequestDataForBatchRecharge(bindingResult, restrictedSubscriberModel);
			if(bindingResult.hasFieldErrors() || bindingResult.hasGlobalErrors()){
				return SCHEDULE_TOPUP;
			}
			restrictedSubscriberModel.setFileType(modelObj.getFileType());
			request.getSession().setAttribute(REST_SUBS_MODEL_OBJ, restrictedSubscriberModel);
			List<ListValueVO> frequencies = restrictedSubscriberService.loadFrequency();
			restrictedSubscriberModel.setFrequency(frequencies);
		}catch(BTSLBaseException e){
			throw e;
		}finally{
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
		
		return "restrictedsubs/scheduleTopUpDetails";
	}
	
	
	/**
	 * For downloading templaet file
	 * @param request
	 * @param response
	 * @throws BTSLBaseException
	 * @throws IOException
	 */
	@RequestMapping(value="/schedule/download-schedule-topup-template.form", method = RequestMethod.GET)
	 public void downloadScheduleTopupTemplate(@RequestParam("fileType") String fileType, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{
		InputStream is = null;
		OutputStream os = null;
		
		String methodName = "downloadScheduleTopupTemplate";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		
		RestrictedSubscriberModel restrictedSubscriberModel = (RestrictedSubscriberModel) request.getSession().getAttribute(REST_SUBS_MODEL_OBJ);
		try{
			String fileLocation = restrictedSubscriberService.getFileLocationForTemplate(restrictedSubscriberModel, fileType.toUpperCase());
			File file = new File(fileLocation);
	        is = new FileInputStream(file);
	        response.setContentType("text/csv");
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
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (os != null) {
                	os.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	 }
	
	
	/**
	 * For uploading file
	 * @param restrictedSubscriberModel
	 * @param bindingResult
	 * @param request
	 * @param model
	 * @return
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value="/schedule/upload-schedule-topup-file.form", method = RequestMethod.POST)
	public String uploadScheduleTopupFile( Model model, @ModelAttribute(REST_SUBSCRIBER_MODEL) RestrictedSubscriberModel restrictedSubscriberModel,  BindingResult bindingResult, HttpServletRequest request) throws BTSLBaseException{
		String methodName = "uploadScheduleTopupFile";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		try{
			RestrictedSubscriberModel modelObj = (RestrictedSubscriberModel) request.getSession().getAttribute(REST_SUBSCRIBER_MODEL);
			RestrictedSubscriberModel modelObj2 = (RestrictedSubscriberModel) request.getSession().getAttribute(REST_SUBS_MODEL_OBJ);
			model.addAttribute(REST_SUBSCRIBER_MODEL, modelObj);
			model.addAttribute("restrictedSubVOObj", modelObj2);
			if(!restrictedSubscriberService.processUploadedFile(restrictedSubscriberModel, bindingResult, request, model)){
				return "restrictedsubs/scheduleTopUpDetails";
			}else{
				return SCHEDULE_TOPUP;
			}
			
			
		}catch(BTSLBaseException e){
			throw e;
		}finally{
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}

	}
	
	/**
	 * For downloading template file
	 * @param request
	 * @param response
	 * @param filePath
	 * @throws BTSLBaseException
	 * @throws IOException
	 */
	@RequestMapping(value="/schedule/download-error-log-file.form", method = RequestMethod.GET)
	 public void downloadErrorLogFile(@RequestParam("file") String filePath, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{
		InputStream is = null;
		OutputStream os = null;
		
		String methodName = "downloadErrorLogFile";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		
		try{
			String fileLocation = BTSLUtil.decryptText(filePath);
			File file = new File(fileLocation);
	        is = new FileInputStream(file);
	        response.setContentType("text/csv");
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
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
            try {
                if (os != null) {
                	os.close();
                }
            } catch (Exception e) {
                log.errorTrace(methodName, e);
            }
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
	 }

	
	//added by satakshi
	/**
	 * This method loads View Schedule Transfer Screen
	 * @param model
	 * @param request
	 * @param response
	 * @return String url on the redirect page
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value="/scheduleTopup/viewScheduleTopUp.form", method = RequestMethod.GET)
	public String loadViewScheduleTopupForm(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{
		
		final String  methodName = "loadViewScheduleTopupForm";
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
		}
		
		try {
			
			authorise(request, response, "VIEWSCH001", false);
			UserVO userVO = this.getUserFormSession(request);
			RestrictedSubscriberModel restrictedSubscriberModel = new RestrictedSubscriberModel();
			restrictedSubscriberService.loadViewScheduleRechargeFormData(userVO, restrictedSubscriberModel, request, model);
			if(model.containsAttribute("fail")){
				return COMMON_GLOBAL_ERROR;
			}
			model.addAttribute(REST_SUBSCRIBER_MODEL, restrictedSubscriberModel);
			request.getSession().setAttribute(REST_SUBSCRIBER_MODEL, restrictedSubscriberModel);
			
		} catch (BTSLBaseException | ServletException | IOException e) {
			throw new BTSLBaseException(e);
		}	
		
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + methodName, PretupsI.EXITED);
		}
		return VIEW_SCHEDULE_TOPUP;
	}
	
	/**
	 * This method loads View Schedule Transfer Screen With data (if found any)
	 * @param subscriberModel
	 * @param bindingResult
	 * @param model
	 * @param request
	 * @return String url on the redirect page
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value = "/scheduleTopup/submit-view-schedule-topup.form", method = RequestMethod.POST)
	public String processViewScheduleTopup(@ModelAttribute("scheduledUser") RestrictedSubscriberModel subscriberModel, BindingResult bindingResult,
			final Model model, HttpServletRequest request) throws BTSLBaseException, NoSuchMessageException, IOException, NoSuchAlgorithmException, ServletException {

		if (log.isDebugEnabled()) {
			log.debug("processViewScheduleTopupForm : processViewScheduleTopup", PretupsI.ENTERED);
		}
		
		UserVO userVO = this.getUserFormSession(request);
		Boolean flag = restrictedSubscriberService.viewScheduleList(subscriberModel, userVO, model, bindingResult);
		
		request.getSession().setAttribute(REST_SUBSCRIBER_MODEL_NEW, subscriberModel);
		if(flag){
			model.addAttribute(REST_SUBSCRIBER_MODEL, subscriberModel);
			return "restrictedsubs/viewSingleScheduleRecharge";
		}else{
			if (bindingResult.hasGlobalErrors()) {
				model.addAttribute(REST_SUBSCRIBER_MODEL, request.getSession().getAttribute(REST_SUBSCRIBER_MODEL));
				model.addAttribute("restModel", request.getSession().getAttribute(REST_SUBSCRIBER_MODEL_NEW));
				model.addAttribute("fail", true);
			}
		}
		
		
		return VIEW_SCHEDULE_TOPUP;

	}
	
	/**
	 * This method loads View Schedule Transfer Screen when back button is pressed
	 * @param request
	 * @param model
	 * @return String url on the redirect page
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value="/scheduleTopup/back-to-the-view-schedule-topup.form", method=RequestMethod.GET)
	public String backToTheViewScheduleTopup(HttpServletRequest request,final Model model){
		model.addAttribute("restModel", request.getSession().getAttribute(REST_SUBSCRIBER_MODEL_NEW));
		request.getSession().setAttribute(PretupsI.BACK_BUTTON_CLICKED, true);
		return VIEW_SCHEDULE_TOPUP;
	}


	/**
	 * This method loads cancel Schedule recharge form
	 * @param request
	 * @param model
	 * @param response
	 * @return String url on the redirect page
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value={"/restrictedsubs/cancel_schedule_recharge.form","/restrictedsubs/cancel_batch_schedule_recharge.form"}, method=RequestMethod.GET)
	public String loadCancelSchduleRechargeForm(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{
		RestrictedSubscriberModel restrictedSubscriberModel = new RestrictedSubscriberModel();
		final String methodName = "#loadCancelSchduleRechargeForm";
		
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME+methodName, PretupsI.ENTERED);
		}

		try {
			if(request.getRequestURI().equals(CANCEL_SINGLE_FIRST)){
			authorise(request, response, "CNCLSCH001", false);
			}else{
				authorise(request, response, "CNSCHTR01", false);
			}
		    UserVO userVO  = this.getUserFormSession(request);
		    model.addAttribute("cancelScheduleRecharge", restrictedSubscriberModel);
		    restrictedSubscriberService.updateCancelInfo(userVO, restrictedSubscriberModel, request, model);
			if(model.containsAttribute("fail")){
				return COMMON_GLOBAL_ERROR;
			}
		    	model.addAttribute(REST_SUBSCRIBER_MODEL, restrictedSubscriberModel);
		        request.getSession().setAttribute("datarestrictedSubscriberModel", restrictedSubscriberModel);
		    }
		
		
        catch (ServletException | IOException | BTSLBaseException exception) {
			
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME+methodName, exception);
			}
			throw new BTSLBaseException(exception);
		}
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME+methodName, PretupsI.EXITED);
		}
		if(request.getRequestURI().equals(CANCEL_SINGLE_FIRST)){
			return CNCL_SINGLE_RECHARGE;
			}else{
				return CNCL_BATCH_RECHARGE;
			}
		
	}
	
	/**
	 * Go to back Batch Recharge Page
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value="/schedule/back-to-batch-recharge-schedule.form", method=RequestMethod.GET)
	public String goToBackShceduleTopupPage(final Model model, HttpServletRequest request){
		model.addAttribute("restrictedSubVOObj", request.getSession().getAttribute(REST_SUBS_MODEL_OBJ));
		return SCHEDULE_TOPUP;
		
	}
	
	/**
	 * This method loads cancel Schedule Transfer Screen when back button is pressed
	 * @param request
	 * @param model
	 * @return String url on the redirect page
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value={"/restrictedsubs/back-to-cancel-schedule.form","/restrictedsubs/back-to-cancel-batch-first-schedule.form"}, method=RequestMethod.GET)
	public String backCancelSchdulRechargeForm(final Model model, HttpServletRequest request) throws BTSLBaseException{
		RestrictedSubscriberModel backrestrictedSubscriberModel = (RestrictedSubscriberModel)request.getSession().getAttribute("sessionrestrictedSubscriberModel");
		RestrictedSubscriberModel detailsrestrictedSubscriberModel = (RestrictedSubscriberModel)request.getSession().getAttribute("datarestrictedSubscriberModel");
			model.addAttribute(REST_SUBSCRIBER_MODEL, detailsrestrictedSubscriberModel);
			model.addAttribute("selectedSubscriberModel", backrestrictedSubscriberModel);
			if(request.getRequestURI().equals(CANCEL_SINGLE_FIRST_BACK)||request.getRequestURI().equals(CANCEL_FINAL_SINGLE_FIRST_BACK)||request.getRequestURI().equals(CANCEL_FINAL_NO_BATCH_FIRST_BACK)){
				return CNCL_BATCH_RECHARGE;
				}else{
					return CNCL_SINGLE_RECHARGE;					
				}
		
	}
	/**
	 * This method loads cancel Schedule recharge Screen 
	 * @param request
	 * @param model
	 * @param restrictedSubscriberModel
	 * @param bindingResult
	 * @param response
	 * @return String url on the redirect page
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value={"/restrictedsubs/cancel_schedule_recharge.form","/restrictedsubs/cancel_batch_schedule_recharge.form"}, method=RequestMethod.POST)
	public String loadCancelSchdulRechargeForm(@ModelAttribute(REST_SUBSCRIBER_MODEL) RestrictedSubscriberModel restrictedSubscriberModel,BindingResult bindingResult, final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{
		RestrictedSubscriberServiceImpl cancelScheduleRechargeServiceImpl = new RestrictedSubscriberServiceImpl();
		final String methodName = "#loadCancelSchdulRechargeForm";
		try{
			if(request.getRequestURI().equals(CANCEL_SINGLE_FIRST)){
				authorise(request, response, "CNCLSCH004", false);
				}else{
					authorise(request, response, "CNSCHTR02", false);
				}
		    UserVO userVO  = this.getUserFormSession(request);
		    request.getSession().setAttribute("sessionrestrictedSubscriberModel", restrictedSubscriberModel);
		    
		    cancelScheduleRechargeServiceImpl.loadBatchDetailsforSingle(restrictedSubscriberModel,bindingResult, userVO, model);
		
		if(model.containsAttribute(FAIL)){
			model.addAttribute(FAIL,model.asMap().get(FAIL).toString());
			return backCancelSchdulRechargeForm(model,request);
		}
		else{
			model.addAttribute(REST_SUBSCRIBER_MODEL,restrictedSubscriberModel);
			request.getSession().setAttribute("batchListrestrictedSubscriberModel",restrictedSubscriberModel);
			if(request.getRequestURI().equals(CANCEL_SINGLE_FIRST)){
				return "restrictedsubs/selectcancelSingleRecharge";
				}else{
					return "restrictedsubs/viewcancelBatchRechargeDetails";
				}
			
		 }
	} catch (IOException | ServletException e) {
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME+methodName,e);
		}
           throw new BTSLBaseException(e);
		}
		
	}
	/**
	 * This method is used to go back from cancel schedule screen 
	 * @param request
	 * @param model
	 * @return String url on the redirect page
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value="/restrictedsubs/back-to-cancel-batch-schedule.form", method=RequestMethod.GET)
	public String backCancelSchdulMobileForm(final Model model, HttpServletRequest request) throws BTSLBaseException{
		RestrictedSubscriberModel batchListrestrictedSubscriberModel = (RestrictedSubscriberModel)request.getSession().getAttribute("batchListrestrictedSubscriberModel");
		RestrictedSubscriberModel backrestrictedSubscriberModel = (RestrictedSubscriberModel)request.getSession().getAttribute("batchNmobileNumbersSubscriberModel");
			model.addAttribute(REST_SUBSCRIBER_MODEL, batchListrestrictedSubscriberModel);
			model.addAttribute("backrestrictedSubscriberModel", backrestrictedSubscriberModel);
			return "restrictedsubs/selectcancelSingleRecharge";
		
	}
	/**
	 * This method loads view cancel selected subscriber 
	 * @param request
	 * @param model
	 * @param restrictedSubscriberModel
	 * @param bindingResult
	 * @param response
	 * @return String url on the redirect page
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value="/restrictedsubs/cancel_schedule_recharge_details.form", method=RequestMethod.POST)
	public String viewCancelledSelectedSubscriber(@ModelAttribute(REST_SUBSCRIBER_MODEL) RestrictedSubscriberModel restrictedSubscriberModel , BindingResult bindingResult, final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{
		RestrictedSubscriberServiceImpl cancelScheduleRechargeServiceImpl = new RestrictedSubscriberServiceImpl();
		
		final String methodName = "#viewCancelledSelectedSubscriber";
		try {
			authorise(request, response, "CNCLSCH002", false);
			UserVO userVO  = this.getUserFormSession(request);
			request.getSession().setAttribute("batchNmobileNumbersSubscriberModel", restrictedSubscriberModel);
			cancelScheduleRechargeServiceImpl.loadDetailsForSingle(restrictedSubscriberModel, userVO, model);
			if(model.containsAttribute(FAIL)){
				model.addAttribute(FAIL,model.asMap().get(FAIL).toString());
				return backCancelSchdulMobileForm(model,request);
			}
			else{
				request.getSession().setAttribute("sessionBatchDetails",model.asMap().get("restrictedSubscriberModeldetails"));
				model.addAttribute(REST_SUBSCRIBER_MODEL, restrictedSubscriberModel);
				return "restrictedsubs/viewcancelSingleRechargeMsisdnDetails";
			}
		} catch (ServletException | IOException e) {
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME+methodName,e);
			}
	           throw new BTSLBaseException(e);
			}
		}
	/**
	 * This method loads view batch detail 
	 * @param request
	 * @param model
	 * @param restrictedSubscriberModel
	 * @param bindingResult
	 * @param response
	 * @param bID
	 * @param loginID
	 * @return String url on the redirect page
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value="/restrictedsubs/cancel_schedule_recharge_details.form", method=RequestMethod.GET)
	public String viewBatchDetail(@RequestParam("batchID") String bID,@RequestParam("loginId") String loginID,@ModelAttribute(REST_SUBSCRIBER_MODEL) RestrictedSubscriberModel restrictedSubscriberModel , BindingResult bindingResult, final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{

		RestrictedSubscriberServiceImpl cancelScheduleRechargeServiceImpl = new RestrictedSubscriberServiceImpl();
		final String methodName = "#viewBatchDetail";
		try {
			UserVO userVO  = this.getUserFormSession(request);
			RestrictedSubscriberModel resModeldetailsBatch =  cancelScheduleRechargeServiceImpl.viewCancelledScheduleSubscriber(restrictedSubscriberModel,bID, userVO, model);
				model.addAttribute(REST_SUBSCRIBER_MODEL, resModeldetailsBatch);
				return "restrictedsubs/viewcancelBatchDetails";
			
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME+methodName,e);
			}
	           throw new BTSLBaseException(e);
			}
		}
	/**
	 * This method loads view cancel selected subscriber 
	 * @param request
	 * @param model
	 * @param restrictedSubscriberModel
	 * @param bindingResult
	 * @param response
	 * @return String url on the redirect page
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value="/restrictedsubs/cancel_schedule_recharge_viewMsisdn.form", method=RequestMethod.POST)
	public String viewCancelledScheduleSubscriber(@ModelAttribute(REST_SUBSCRIBER_MODEL) RestrictedSubscriberModel restrictedSubscriberModel , BindingResult bindingResult, final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{
		RestrictedSubscriberServiceImpl cancelScheduleRechargeServiceImpl = new RestrictedSubscriberServiceImpl();
			RestrictedSubscriberModel sessionRestrictedSubscriberModeldetails = (RestrictedSubscriberModel)request.getSession().getAttribute("sessionBatchDetails");
		
		final String methodName = "#viewCancelledScheduleSubscriber";
		try {
			UserVO userVO  = this.getUserFormSession(request);
			cancelScheduleRechargeServiceImpl.loadDetailsForSelected(sessionRestrictedSubscriberModeldetails,restrictedSubscriberModel.getChecklist(),userVO, model);
			
			if(model.containsAttribute(FAIL)){
				model.addAttribute(FAIL,PretupsRestUtil.getMessageString(model.asMap().get(FAIL).toString()));
				return backCancelSchdulMobileForm(model,request);
				
			}
			else{
				cancelScheduleRechargeServiceImpl.deleteDetailsForSelected(sessionRestrictedSubscriberModeldetails,userVO, model);
				if(model.containsAttribute(FAIL)){
					model.addAttribute(FAIL,PretupsRestUtil.getMessageString(model.asMap().get(FAIL).toString()));
					return backCancelSchdulMobileForm(model,request);
				}
				else{
					model.addAttribute("success", PretupsRestUtil.getMessageString("restrictedsubs.displaydetailsforcancelsinglesub.msg.success"));
					return backCancelSchdulRechargeForm(model, request);
				}
				
			}
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME+methodName,e);
			}
	           throw new BTSLBaseException(e);
			}
		}
	
	/**
	 * This method processes selected mobile number for batch 
	 * @param request
	 * @param model
	 * @param restrictedSubscriberModel
	 * @param bindingResult
	 * @param response
	 * @return String url on the redirect page
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value="/restrictedsubs/cancel_schedule_recharge_viewSelectedMsisdn.form", method=RequestMethod.POST)
	public String processSelectedMsisdnForBatchForm(@ModelAttribute(REST_SUBSCRIBER_MODEL) RestrictedSubscriberModel restrictedSubscriberModel , BindingResult bindingResult, final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{
		RestrictedSubscriberServiceImpl cancelScheduleRechargeServiceImpl = new RestrictedSubscriberServiceImpl();
		
		RestrictedSubscriberModel sessionRestrictedSubscriberModelDeleteDetails = (RestrictedSubscriberModel)request.getSession().getAttribute("sessionDeleteList");
		
		final String methodName = "#processSelectedMsisdnForBatchForm";
		try {
			UserVO userVO  = this.getUserFormSession(request);
			cancelScheduleRechargeServiceImpl.deleteDetailsForSelected(sessionRestrictedSubscriberModelDeleteDetails,userVO, model);
			if(model.containsAttribute(PretupsI.FAIL)){
				return "restrictedsubs/viewcancelSingleRechargeMsisdnDetails";
			}
			else{
				return CNCL_SINGLE_RECHARGE;
			}
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME+methodName,e);
			}
	           throw new BTSLBaseException(e);
			}
		}
		
	/**
	 * This method loads View Schedule Transfer in Batch Screen 
	 * @param request,response,model
	 * @return String url on the redirect page
	 * @throws BTSLBaseException
	 */
	
	@RequestMapping(value="/restrictedsubs/view_schedule_rc_batch.form", method=RequestMethod.GET)
	public String viewScheduleRCBatchForm(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException, ParseException, ServletException{
		
		final String methodName = "viewScheduleRCBatch";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);

		try {
			authorise(request, response, "VWSCHTR1A", false);
			UserVO userVO = this.getUserFormSession(request);
			
			RestrictedSubscriberModel restrictedSubscriberModel = new RestrictedSubscriberModel();
			restrictedSubscriberService.loadScheduleBatchMasterList(userVO, restrictedSubscriberModel, request, model);
			List<ListValueVO> statusList =restrictedSubscriberService.loadStatus();
			if (statusList != null) {
                List<ListValueVO> tesmpScheduleStatusList = new ArrayList<ListValueVO>();
                ListValueVO listValueVO ;
                int j = statusList.size();
                for (int i = 0; i < j; i++) {
                    listValueVO =  statusList.get(i);
                    if(PretupsI.SCHEDULE_STATUS_CANCELED.equalsIgnoreCase(listValueVO.getValue()) || PretupsI.SCHEDULE_STATUS_EXECUTED.equalsIgnoreCase(listValueVO.getValue()) || PretupsI.SCHEDULE_STATUS_SCHEDULED.equalsIgnoreCase(listValueVO.getValue())) 
                          tesmpScheduleStatusList.add(listValueVO); 
                   }
                restrictedSubscriberModel.setScheduleStatusList(tesmpScheduleStatusList);
            }
			
			if(model.containsAttribute("fail")){
				return COMMON_GLOBAL_ERROR;
			}
			
			
			model.addAttribute(REST_SUBSCRIBER_MODEL, restrictedSubscriberModel);
			request.getSession().setAttribute(REST_SUBSCRIBER_MODEL, restrictedSubscriberModel);

			
		} catch ( IOException | BTSLBaseException exception) {
			
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME+methodName, exception);
			}
			throw new BTSLBaseException(exception); 
		}
		LogFactory.printLog(methodName, PretupsI.EXITED, log);
		return "restrictedsubs/viewSchRCBatch";
		
	}
	
	
	/**
	 * This method is called on submit button from first screen of View Schedule Transfer in Batch 
	 * @param request
	 * @param restrictedSubscriberModel
	 * @param model
	 * @param bindingResult
	 * @return String url on the redirect page
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value="/restrictedsubs/process-viewschedulercbatch.form", method=RequestMethod.POST)
	public String processviewScheduleRCBatchForm(@ModelAttribute("viewscrcbatchuser") RestrictedSubscriberModel restrictedSubscriberModel,BindingResult bindingResult, final Model model, HttpServletRequest request) throws BTSLBaseException{
		final String methodName = "processviewScheduleRCBatchForm";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);

		try {
			UserVO userVO = this.getUserFormSession(request);
			List<ScheduleBatchMasterVO> schList = new ArrayList<>();
			Boolean flag=restrictedSubscriberService.processviewScheduleRCBatchForm(restrictedSubscriberModel,bindingResult, model ,userVO,schList);
			request.getSession().setAttribute(REST_SUBSCRIBER_MODEL_BACK, restrictedSubscriberModel);
			if(flag)
			{
				request.getSession().setAttribute(SCHLIST, schList);
				model.addAttribute(REST_SUBSCRIBER_MODEL_NEW,restrictedSubscriberModel );
				model.addAttribute(REST_SUBSCRIBER_MODEL,request.getSession().getAttribute(REST_SUBSCRIBER_MODEL));
				request.getSession().setAttribute(REST_SUBSCRIBER_MODEL_NEW, restrictedSubscriberModel);
				return VIEW_RC_BATCH_LIST;
			}
			else{
				
				if(bindingResult.hasGlobalErrors()){
					model.addAttribute("fail",true);
					model.addAttribute(REST_SUBSCRIBER_MODEL_BACK_NEW,request.getSession().getAttribute(REST_SUBSCRIBER_MODEL_BACK));
					model.addAttribute(REST_SUBSCRIBER_MODEL, request.getSession().getAttribute(REST_SUBSCRIBER_MODEL));
				}
				return "restrictedsubs/viewSchRCBatch";
			}
			
			
		} catch (BTSLBaseException | NoSuchMessageException e) {
			throw new BTSLBaseException(e);
		}finally{
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
		
	}
	
	
	/**
	 * This method is called on clicking the hyperlink on batch id from second screen of View Schedule Transfer in Batch 
	 * @param request
	 * @param response
	 * @param restrictedSubscriberModel
	 * @param model
	 * @param bindingResult
	 * @param bid
	 * @param scheduleDate
	 * @param fileType
	 * @param scheduledDateAsString
	 * @return String url on the redirect page
	 * @throws BTSLBaseException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/restrictedsubs/detailed-view-barred-list.form", method=RequestMethod.GET)
	public String detailedViewList(@RequestParam("batchID")String bid,@RequestParam("fileType")String fileType,@RequestParam("scheduledDateAsString")String scheduledDateAsString,RestrictedSubscriberModel restrictedSubscriberModel,BindingResult bindingResult,final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{
		
		final String methodName = "detailedViewList";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		try{
			
			List<ScheduleBatchMasterVO> b1=new ArrayList<>();
			b1=(List<ScheduleBatchMasterVO>) request.getSession().getAttribute(SCHLIST);
			if(restrictedSubscriberService.detailedviewScheduleRCBatchForm(restrictedSubscriberModel,bindingResult, model , b1,bid))
			{
				model.addAttribute("batchID",bid);
				model.addAttribute("fileType",fileType);
				model.addAttribute("scheduledDateAsString",scheduledDateAsString);
				model.addAttribute("restrictedSubscriberModel2",request.getSession().getAttribute("restrictedSubscriberModel1"));
				return "restrictedsubs/viewSchRcBatchListDetail";
			}
			else
				return VIEW_RC_BATCH_LIST;
			
		}
		catch(Exception e)
		{
			throw new BTSLBaseException(e);
		}
		finally{
			LogFactory.printLog(methodName, PretupsI.EXITED, log);
		}
		
		
	}
	
	/**
	 * This method returns to view schedule recharge batch list page
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/restrictedsubs/back-to-process-view.form", method=RequestMethod.GET)
	public String backtoProcessView(final Model model, HttpServletRequest request, HttpServletResponse response) {
		
		final String methodName = "backtoProcessView";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		request.getSession().getAttribute(REST_SUBSCRIBER_MODEL);
		return VIEW_RC_BATCH_LIST;
		
	}
	/**
	 * This method returns back to view schedule recharge batch form
	 * @param request
	 * @return String url on the redirect page
	 */
	@RequestMapping(value="/restrictedsubs/back-to-view-schedule-rc-batch.form", method=RequestMethod.GET)
	public String backToviewScheduleRCBatchForm(HttpServletRequest request,final Model model){
		request.getSession().setAttribute(PretupsI.BACK_BUTTON_CLICKED, true);
		request.getSession().getAttribute(REST_SUBSCRIBER_MODEL);
		model.addAttribute(REST_SUBSCRIBER_MODEL_BACK_NEW,request.getSession().getAttribute(REST_SUBSCRIBER_MODEL_BACK));
		model.addAttribute(REST_SUBSCRIBER_MODEL, request.getSession().getAttribute(REST_SUBSCRIBER_MODEL));
		return "restrictedsubs/viewSchRCBatch";
	}
	/**
	 * This method returns to view schedule recharge batch form
	 * @param request
	 * @return String url on the redirect page
	 */
	@RequestMapping(value="/restrictedsubs/back-to-process-viewschedulercbatch.form", method=RequestMethod.GET)
	public String backToprocessviewScheduleRCBatchForm(HttpServletRequest request){
		request.getSession().setAttribute(PretupsI.BACK_BUTTON_CLICKED, true);
		return "redirect:/restrictedsubs/back-to-process-view.form";
	}
	
	
	/**
	 * @param categoryCode
	 * @param userName
	 * @param geoDomain
	 * @param domain
	 * @param restrictedSubscriberModel
	 * @param bindingResult
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value="/*/cancel_schedule_recharge_userdetails.form", method=RequestMethod.GET)
	public String loadUserDetail(@RequestParam("categoryCode") String categoryCode,@RequestParam("userName") String userName,@RequestParam("geoDomain") String geoDomain,@RequestParam("domain") String domain,@ModelAttribute(REST_SUBSCRIBER_MODEL) RestrictedSubscriberModel restrictedSubscriberModel , BindingResult bindingResult, final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{
		RestrictedSubscriberServiceImpl cancelScheduleRechargeServiceImpl = new RestrictedSubscriberServiceImpl();
		final String methodName = "loadUserDetail";
		LogFactory.printLog(methodName, PretupsI.ENTERED, log);
		UserVO userVO  = this.getUserFormSession(request);
		try {
			PretupsResponse<List<UserVO>> responseObj = cancelScheduleRechargeServiceImpl.loadUserDetailsSearch(restrictedSubscriberModel,categoryCode,userName, geoDomain,domain,userVO, model);
				if(responseObj.hasFormError()){
					model.addAttribute("fail", PretupsRestUtil.getMessageString(responseObj.getFormError()));
				}else{
					model.addAttribute("userList", responseObj.getDataObject());
					request.getSession().setAttribute("userListSession", responseObj.getDataObject());
				}
				return "restrictedsubs/UserSearch";
		
		} catch (Exception e) {
			throw new BTSLBaseException(e);
		}
		}
	
	
	/**
	 * @param loginID
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 */
	@SuppressWarnings("unchecked")
	//@RequestMapping(value="/**/select-search-user.form", method=RequestMethod.POST)
	public String loadUserDetailCancelRecharge(@RequestParam("loginID") String loginID ,  final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{
		RestrictedSubscriberServiceImpl restrictedSubscriberServiceImpl = new RestrictedSubscriberServiceImpl();
		List<UserVO> userList = (List<UserVO>) request.getSession().getAttribute("userListSession");
			
		UserVO userVO =  restrictedSubscriberServiceImpl.loadUserVO(userList, loginID);
		request.getSession().setAttribute("userVO", userVO);
		return "restrictedsubs/addTemp";
	}
	

	@Autowired
	private BatchRechageRescheduleServiceImpl batchRechageRescheduleServiceImpl;
	private String rescheduleTopUpJsp = "restrictedsubs/rescheduleTopUp";
	private String rescheduleTopUpDetailsJsp = "restrictedsubs/rescheduleTopUpDetails";

	/**
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws BTSLBaseException
	 * the methods loads reschedule recharge 1st page
	 */
	@RequestMapping(value="/batch-reschedule/rescheduleTopUp.form", method = RequestMethod.GET)
	public String loadBatchRescheduleForm(final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{
		final String  methodName = ":loadBatchRechargeForm";
		log.debug(CLASS_NAME + methodName, PretupsI.ENTERED);
		try {
			authorise(request, response, "RSHTOPUP1A", false);
			UserVO userVO = this.getUserFormSession(request);
			RestrictedSubscriberModel restrictedSubscriberModel = new RestrictedSubscriberModel();
			restrictedSubscriberService.loadloadBatchRechargeFormData(userVO, restrictedSubscriberModel, request, model);
			if(model.containsAttribute("fail")){
				return COMMON_GLOBAL_ERROR;
			}
			model.addAttribute(REST_SUBSCRIBER_MODEL, restrictedSubscriberModel);
			request.getSession().setAttribute(REST_SUBSCRIBER_MODEL, restrictedSubscriberModel);
		} catch (BTSLBaseException | ServletException | IOException e) {
			log.error(methodName, PretupsI.EXCEPTION + e);
			throw new BTSLBaseException(e);
		}	
		log.debug(CLASS_NAME + methodName, PretupsI.EXITED);
		return rescheduleTopUpJsp;
	}
	/**
	 * @param thisForm
	 * @param request
	 * @param modelMap
	 * @param bindingResult
	 * @return
	 * the method loads reschedule recharge 2nd page -  reschedule recharge submit
	 * @throws BTSLBaseException 
	 */
	@RequestMapping(value="/batch-reschedule/load-batch-list.form", method=RequestMethod.POST)
	public String loadScheduledBatchList( RestrictedSubscriberModel thisForm, HttpServletRequest request,ModelMap modelMap, BindingResult bindingResult) throws BTSLBaseException{
		final String methodName = CLASS_NAME+": loadScheduledBatchList";
		log.debug(methodName,  PretupsI.ENTERED);
		String returnPage = rescheduleTopUpDetailsJsp;
		try{
			RestrictedSubscriberModel sessionModel = (RestrictedSubscriberModel)request.getSession().getAttribute(REST_SUBSCRIBER_MODEL);
			sessionModel.setGeoDomainCode(thisForm.getGeoDomainCode());
			sessionModel.setDomainCode(thisForm.getDomainCode());
			sessionModel.setCategoryCode(thisForm.getCategoryCode());
			sessionModel.setServiceCode(thisForm.getServiceCode());
			sessionModel.setUserID(thisForm.getUserID());
			request.getSession().setAttribute(REST_SUBSCRIBER_MODEL, sessionModel);
			
			batchRechageRescheduleServiceImpl.loadScheduledBatchList(thisForm , getUserFormSession(request).getLoginID(), modelMap, bindingResult);
			if(bindingResult.hasErrors() || null != modelMap.get("fail") ){
				modelMap.addAttribute(REST_SUBSCRIBER_MODEL, (RestrictedSubscriberModel)request.getSession().getAttribute(REST_SUBSCRIBER_MODEL));
				returnPage = rescheduleTopUpJsp;
			}
			else{
				List<ListValueVO> frequencies = restrictedSubscriberService.loadFrequency();
				thisForm = (RestrictedSubscriberModel)modelMap.get(REST_SUBSCRIBER_MODEL);
				thisForm.setFrequency(frequencies);
				returnPage = rescheduleTopUpDetailsJsp;
			}
		} 
		catch (BTSLBaseException e) {
			log.error(methodName, PretupsI.EXCEPTION + e);
			modelMap.put("fail", PretupsRestUtil.getMessageString(e.getBtslMessages().getMessageKey()));
			throw new BTSLBaseException(e);
		}
		log.debug(methodName,  PretupsI.EXITED);
		return returnPage;
	}
	
	/**
	 * @param batchID
	 * @param fileType
	 * @param serviceTypeCode
	 * @param userID
	 * @param request
	 * @param response
	 * @param modelMap
	 * @return
	 * This method downloads batch file -  reschedule recharge 2nd page 
	 * @throws BTSLBaseException 
	 */
	@RequestMapping(value="/batch-reschedule/download-batch-file.form", method=RequestMethod.GET)
	public String downloadBatchFile( @RequestParam("batchID")String batchID,@RequestParam("fileType")String fileType,@RequestParam("serviceTypeCode")String serviceTypeCode,
			@RequestParam("userID")String userID, HttpServletRequest request,HttpServletResponse response, ModelMap modelMap,@ModelAttribute("restrictedSubscriberModel")RestrictedSubscriberModel model , BindingResult bindingResult) throws BTSLBaseException{
		final String methodName =CLASS_NAME+ ": downloadBatchFile";
		log.debug(methodName, PretupsI.ENTERED);
		try{
			model.setBatchID(batchID);
			model.setFileType(fileType);
			model.setServiceCode(serviceTypeCode);
			model.setUserID(userID);
			batchRechageRescheduleServiceImpl.downloadBatchFile(model, getUserFormSession(request).getLoginID(), modelMap, response, bindingResult );
			batchRechageRescheduleServiceImpl.loadScheduledBatchList(model, getUserFormSession(request));
			List<ListValueVO> frequencies = restrictedSubscriberService.loadFrequency();
			model.setFrequency(frequencies);
			
		} 
		catch (BTSLBaseException e) {
			log.error(methodName, PretupsI.EXCEPTION + e);
			modelMap.put("fail", PretupsRestUtil.getMessageString(e.getBtslMessages().getMessageKey()));
			throw new BTSLBaseException(e);
		}
		log.debug(methodName, PretupsI.EXITED);
		return rescheduleTopUpDetailsJsp;
	}	

	/**
	 * @param thisForm
	 * @param uploadedFile
	 * @param request
	 * @param modelMap
	 * @param bindingResult
	 * @return
	 * @throws BTSLBaseException 
	 * @throws IOException
	 * This method process the uploaded batch file -  reschedule recharge 2nd page 
	 */
	@RequestMapping(value="/batch-reschedule/process-batch-reschedule.form", method=RequestMethod.POST)
	public String uploadAndProcessBatchReshedule( RestrictedSubscriberModel thisForm, HttpServletRequest request, ModelMap modelMap,BindingResult bindingResult) throws BTSLBaseException {
		final String methodName =CLASS_NAME+ ": uploadAndProcessBatchReshedule";
		String returnPage = rescheduleTopUpDetailsJsp;
		log.debug(methodName, PretupsI.ENTERED);
		try{
			thisForm.setRequestFor(PretupsI.RESCHEDULE);
			batchRechageRescheduleServiceImpl.uploadAndProcessBatchReshedule(thisForm, getUserFormSession(request).getLoginID(), modelMap, bindingResult);
			if(bindingResult.hasErrors() || null != modelMap.get("fail")){
				batchRechageRescheduleServiceImpl.loadScheduledBatchList(thisForm, getUserFormSession(request));
				returnPage = rescheduleTopUpDetailsJsp;
			}
			else{
				modelMap.addAttribute(REST_SUBSCRIBER_MODEL, (RestrictedSubscriberModel)request.getSession().getAttribute(REST_SUBSCRIBER_MODEL));
				returnPage = rescheduleTopUpJsp;
			}
		} 
		catch (BTSLBaseException e) {
			log.error(methodName, PretupsI.EXCEPTION + e);
			modelMap.put("fail", PretupsRestUtil.getMessageString(e.getBtslMessages().getMessageKey()));
			throw new BTSLBaseException(e);
		}
		log.debug(methodName, PretupsI.EXITED);
		return  returnPage;
	}

	
	/**
	 * @param thisForm
	 * @param request
	 * @param modelMap
	 * @param bindingResult
	 * @return
	 * load reschedule 1st page on click of back button from 2nd page
	 * @throws BTSLBaseException 
	 */
	@RequestMapping(value="/batch-reschedule/reschedule.form", method=RequestMethod.GET)
	public String loadRescheduleTopUpPage( HttpServletRequest request, ModelMap modelMap) throws BTSLBaseException {
		final String methodName =CLASS_NAME+ ": loadRescheduleTopUpPage";
		log.debug(methodName, PretupsI.ENTERED);
		try{
			modelMap.addAttribute(REST_SUBSCRIBER_MODEL, (RestrictedSubscriberModel)request.getSession().getAttribute(REST_SUBSCRIBER_MODEL));
		} 
		catch (Exception e) {
			log.error(methodName, PretupsI.EXCEPTION + e);
			//modelMap.put("fail", PretupsRestUtil.getMessageString(e.getBtslMessages().getMessageKey()));
			throw new BTSLBaseException(e);
		}
		log.debug(methodName, PretupsI.EXITED);
		return  rescheduleTopUpJsp;
	}

	
	@RequestMapping(value="/restrictedsubs/cancel_schedule_recharge_selected_batch.form", method=RequestMethod.POST)
	public String processSelectedBatchForCancel(@ModelAttribute(REST_SUBSCRIBER_MODEL) RestrictedSubscriberModel restrictedSubscriberModel , BindingResult bindingResult, final Model model, HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException{
		RestrictedSubscriberServiceImpl cancelScheduleRechargeServiceImpl = new RestrictedSubscriberServiceImpl();

	

		

		RestrictedSubscriberModel sessionRestrictedSubscriberModelDeleteDetails = (RestrictedSubscriberModel)request.getSession().getAttribute("sessionDeleteList");
		
		final String methodName = "#processSelectedBatchForCancel";
		try {
			UserVO userVO  = this.getUserFormSession(request);
			cancelScheduleRechargeServiceImpl.cancelSelectedBatch(restrictedSubscriberModel,userVO, model);
			if(model.containsAttribute(PretupsI.FAIL)){
				return "restrictedsubs/viewcancelBatchRechargeDetails";
			}
			else{
				
				return backCancelSchdulRechargeForm(model, request);
			}
		} catch (IOException e) {
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME+methodName,e);
			}
	           throw new BTSLBaseException(e);
			}
		}
	

}
	
	
		




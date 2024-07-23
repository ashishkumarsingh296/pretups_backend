package com.web.pretups.channel.transfer.web;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Locale;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.validator.ValidatorException;
import org.spring.custom.action.Globals;
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
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.inter.util.VOMSVoucherVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.CryptoUtil;
import com.web.pretups.channel.transfer.service.RechargeService;

/**
 * @author parul.nagpal
 */
@Controller 
public class RechargeController  extends CommonController{
	
	public static final Log LOG = LogFactory.getLog(RechargeController.class.getName());
	public static final String CLASS_NAME = "RechargeController";
	private  final String RC_MODEL="c2sRechargeModel";
	private   final String RC_MODEL_NEW="c2sRechargeModel1";
	private  final String RC_MODEL_BACK="c2sRechargeBack";
	private   final String RC_MODEL_NEW_BACK="c2sRechargeModelBack";
	private static final String SUCCESS_KEY = "success";
	private static final String FAIL_KEY = "fail";
	
	@Autowired
	private RechargeService rechargeService;
	
	/**
	 * This method loads the first screen of recharge
	 * @throws Exception 
	 * @throws ServletException 
	 * 
	 */
	@RequestMapping(value="/transfer/c2sRecharge.form", method=RequestMethod.GET)
	public String c2sRechargeAuthorize(final Model model, HttpServletRequest request, HttpServletResponse response) throws  Exception{
		final String methodName = "c2sRechargeAuthorize";
		LogFactory.printLog(methodName, PretupsI.ENTERED, LOG);

		try {
			authorise(request, response, "C2SRECHR1A", false);
			ChannelUserVO userVO = (ChannelUserVO)this.getUserFormSession(request);
			C2SRechargeModel c2sRechargeModel =new C2SRechargeModel();
			c2sRechargeModel=rechargeService.loadServicesBalance(userVO,c2sRechargeModel,model);
			if(!model.containsAttribute(FAIL_KEY)){
				model.addAttribute(RC_MODEL,c2sRechargeModel);
				model.addAttribute("service_list", c2sRechargeModel.getServiceTypeList());
				model.addAttribute("subservice_list", c2sRechargeModel.getSubServiceTypeList());
				request.getSession().setAttribute(RC_MODEL, c2sRechargeModel);
			}else{
				 model.addAttribute("breadcrumb", PretupsRestUtil.getMessageString("pretups.c2srecharge.module.breadcrumb"));
		         return "common/commonViewForInOutSuspendedUser";
			}
		} catch (BTSLBaseException | ServletException | IOException e) {
			throw new BTSLBaseException(e); 
		}
		LogFactory.printLog(methodName, PretupsI.EXITED, LOG);
		
	
		return "c2stransfer/c2sRechargeView";
		
	}
	
	
	/**
	 * checks if user is barred as sender or not and forwards to next page
	 * @return
	 * @throws BTSLBaseException 
	 * @throws SAXException 
	 * @throws IOException 
	 * @throws ValidatorException 
	 * @throws ServletException 
	 * @throws NoSuchAlgorithmException 
	 */
	@RequestMapping(value="/transfer/process-c2sRecharge.form", method=RequestMethod.POST)
	public String confirmC2SRecharge(@ModelAttribute("transfer") C2SRechargeModel c2sRechargeModel,BindingResult bindingResult, final Model model, HttpServletRequest request) throws BTSLBaseException, ValidatorException, IOException, SAXException, NoSuchAlgorithmException, ServletException{
		final String methodName = "confirmC2SRecharge";
		LogFactory.printLog(methodName, PretupsI.ENTERED, LOG);
		final Locale senderLanguage = (Locale) request.getSession().getAttribute(Globals.LOCALE_KEY);
	
		
		try{
		if(csrfcheck(request, model)){
				 return "common/csrfmessage";
		}	
		ChannelUserVO userVO = (ChannelUserVO)this.getUserFormSession(request);
		
		c2sRechargeModel.setServiceTypeList(((C2SRechargeModel)request.getSession().getAttribute(RC_MODEL)).getServiceTypeList());
		c2sRechargeModel.setSubServiceTypeList(((C2SRechargeModel)request.getSession().getAttribute(RC_MODEL)).getSubServiceTypeList());
		c2sRechargeModel.setDenominationList(((C2SRechargeModel)request.getSession().getAttribute(RC_MODEL)).getDenominationList());
		c2sRechargeModel.setLanguageList(((C2SRechargeModel)request.getSession().getAttribute(RC_MODEL)).getLanguageList());
		c2sRechargeModel.setCurrencyList(((C2SRechargeModel)request.getSession().getAttribute(RC_MODEL)).getCurrencyList());
		c2sRechargeModel.setLoginUserMsisdn(((C2SRechargeModel)request.getSession().getAttribute(RC_MODEL)).getLoginUserMsisdn());
		c2sRechargeModel.setCountryCode(((C2SRechargeModel)request.getSession().getAttribute(RC_MODEL)).getCountryCode());
		c2sRechargeModel.setCurrentBalance(((C2SRechargeModel)request.getSession().getAttribute(RC_MODEL)).getCurrentBalance());
		request.getSession().setAttribute(RC_MODEL_BACK, c2sRechargeModel);
		
		if(rechargeService.confirmC2SRecharge(userVO,c2sRechargeModel,request,model,bindingResult)){
			c2sRechargeModel=rechargeService.recharge(userVO,c2sRechargeModel,senderLanguage,request,model);
			request.getSession().setAttribute(RC_MODEL_BACK, c2sRechargeModel);
			if(model.containsAttribute(FAIL_KEY)){
				model.addAttribute(RC_MODEL_NEW_BACK,request.getSession().getAttribute(RC_MODEL_BACK));
    			return "c2stransfer/c2sRechargeView";
			}
			else{
					request.getSession().setAttribute(RC_MODEL_NEW, c2sRechargeModel);
					final String[] arr = new String[1];
					arr[0] = URLDecoder.decode(c2sRechargeModel.getFinalResponse(), "UTF16");
					if (PretupsI.TXN_STATUS_SUCCESS.equals(c2sRechargeModel.getTxnStatus())) {
						model.addAttribute(SUCCESS_KEY, PretupsRestUtil.getMessageString("btsl.blank.message", arr));
						return notification(request, c2sRechargeModel, model);
						
					} else {
						model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("btsl.blank.message", arr));
						model.addAttribute(RC_MODEL_NEW_BACK,request.getSession().getAttribute(RC_MODEL_BACK));
						return "c2stransfer/c2sRechargeView";
					}
			}
		}
		else{
			model.addAttribute(RC_MODEL_NEW_BACK,request.getSession().getAttribute(RC_MODEL_BACK));
			return "c2stransfer/c2sRechargeView";
		}

		}catch (BTSLBaseException | UnsupportedEncodingException e) {
			throw new BTSLBaseException(e); 
		}finally{
			LogFactory.printLog(methodName, PretupsI.EXITED, LOG);
		}
	}
	
	
	/**
	 * Method called from second and third screen to redirect control to first screen
	 * @param request
	 * @param model
	 * @return
	 */
	@RequestMapping(value="/transfer/process-backc2srecharge.form", method=RequestMethod.GET)
	public String backForm(HttpServletRequest request,final Model model){
		request.getSession().setAttribute(PretupsI.BACK_BUTTON_CLICKED, true);
		/*C2SRechargeModel c2sRechargeModel = (C2SRechargeModel)request.getSession().getAttribute(RC_MODEL_FINAL) ;
		((C2SRechargeModel)request.getSession().getAttribute(RC_MODEL_BACK)).setCurrentBalance(c2sRechargeModel.getCurrentBalance());
		((C2SRechargeModel)request.getSession().getAttribute(RC_MODEL_BACK)).setIatIntRecharge(false);
		((C2SRechargeModel)request.getSession().getAttribute(RC_MODEL_BACK)).setIatRoamRecharge(false);
         if (!BTSLUtil.isNullString(((C2SRechargeModel)request.getSession().getAttribute(RC_MODEL_BACK)).getPin())) {
        	 ((C2SRechargeModel)request.getSession().getAttribute(RC_MODEL_BACK)).setPin(null);
         }*/
		model.addAttribute(RC_MODEL_NEW_BACK,request.getSession().getAttribute(RC_MODEL_BACK));
		return "c2stransfer/c2sRechargeView";
	}
	
	/**
	 * this method is called when link is clicked from recharge screen
	 * @param request
	 * @param model
	 * @return
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value="/transfer/notify.form",method=RequestMethod.GET)
	public String notifyRecharge(HttpServletRequest request,final Model model) throws BTSLBaseException{
		
		final String methodName="notifyRecharge";
		LogFactory.printLog(methodName, PretupsI.ENTERED, LOG);
		String jspScreen=null;
		try {
			if (request.getParameter("btnBack") != null) {
                return "c2stransfer/c2sRechargeView";
            } else {
        	ChannelUserVO userVO = (ChannelUserVO)this.getUserFormSession(request);
			final C2SRechargeModel theForm = (C2SRechargeModel) request.getSession().getAttribute(RC_MODEL_NEW);
			theForm.setFinalMesasge(true);
			jspScreen= rechargeService.notify(request,theForm,userVO);
		
            }
		}catch(Exception e)
		{
			LOG.errorTrace(methodName, e);
			throw new BTSLBaseException(e);
		}
		finally{
			LogFactory.printLog(methodName, PretupsI.EXITED, LOG);
		}
    
		return jspScreen;
	}
	
	
	/***
	 * Called for MVD download utility
	 * @param request
	 * @param model
	 * @return
	 * @throws BTSLBaseException
	 */
	@RequestMapping(value="/transfer/downloadFileForMultipleVoucher.form",method=RequestMethod.GET)
	public void downloadFileForMultipleVoucher(HttpServletRequest request,final Model model,HttpServletResponse response) throws BTSLBaseException{
		
        final String methodName = "downloadFileForMultipleVoucher";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        PrintWriter out = null;
        File encryptedVoucherFile = null;
        VOMSVoucherVO voucherVO = null;
        CryptoUtil cryptoUtil = null;
        InputStream is = null;
		OutputStream os = null;
        try {
                final C2SRechargeModel theForm = (C2SRechargeModel) request.getSession().getAttribute(RC_MODEL_NEW);
                String filePath = Constants.getProperty("DownloadFilePathForMultipleVoucherDownload");
                final String fileName = Constants.getProperty("DownloadMultipleVoucherFileNamePrefix") + BTSLUtil.getFileNameStringFromDate(new Date()) + ".csv";
                try {
                    encryptedVoucherFile = new File(filePath + fileName);
                    if (!(encryptedVoucherFile.getParentFile()).isDirectory()) {
                        (encryptedVoucherFile.getParentFile()).mkdirs();
                    }
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                    LOG.error(methodName, "Exception" + e.getMessage());
                    model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("downloadfile.error.dirnotcreated"));
                    //return "c2stransfer/c2sRechargeView";
                }
                out = new PrintWriter(new BufferedWriter(new FileWriter(encryptedVoucherFile)));
                cryptoUtil = new CryptoUtil();
                String writeString = null;
                out.println("Amount,Serial Number,Pin,Invoice Number,Expiry Date,Validity Period");
                int theForVoucherSerialAndPinLists=theForm.getVoucherSerialAndPinList().size();
                for (int i = 0, j = theForVoucherSerialAndPinLists; i < j; i++) {
                    writeString = "";
                    voucherVO = (VOMSVoucherVO) theForm.getVoucherSerialAndPinList().get(i);
                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_ENCRIPTION_ALLOWED))).booleanValue()) {
                        writeString = voucherVO.getMRP() + "," + voucherVO.getSerialNo() + ",|" + cryptoUtil.encrypt(voucherVO.getPinNo(), theForm.getDecryptionKey()) + "|," + voucherVO
                            .getGenerationBatchNo() + "," + BTSLUtil.getDateTimeStringFromDate(voucherVO.getExpiryDate(), "yyyyMMdd") + "," + voucherVO.getValidity() + ",1";
                    } else {
                        writeString = voucherVO.getMRP() + "," + voucherVO.getSerialNo() + ",|" + voucherVO.getPinNo() + "|," + voucherVO.getGenerationBatchNo() + "," + BTSLUtil
                            .getDateTimeStringFromDate(voucherVO.getExpiryDate(), "yyyyMMdd") + "," + voucherVO.getValidity() + ",1";
                    }
                    out.println(writeString);
                }
                
                if (out != null) {
                    out.close();
                }
                
                is = new FileInputStream(filePath + fileName);
                response.setContentType("text/csv");
    	        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
                os = response.getOutputStream();
    	        
    	        byte[] buffer = new byte[1024];
    	        int len;
    	        while ((len = is.read(buffer)) != -1) {
    	            os.write(buffer, 0, len);
    	        }
                
    	        os.flush();
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.error(methodName, "Exceptin:e=" + e);
            }
            LOG.errorTrace(methodName, e);
        } finally {
        	try{
        		if(out!=null){
        			out.close();	
        		}
        	}catch(Exception e){
        		 LOG.errorTrace(methodName, e);
        	}
			try{
        		if(is!=null){
        			is.close();	
        		}
        	}catch(Exception e){
        		 LOG.errorTrace(methodName, e);
        	}
        	try{
        		if(os!=null){
        			os.close();	
        		}
        	}catch(Exception e){
        		 LOG.errorTrace(methodName, e);
        	}
			LogFactory.printLog(methodName, PretupsI.EXITED, LOG);
		}
		
		
	}
	
	/**
	 * Method is called on successful recharge notification
	 * @param request
	 * @param c2sRechargeModel
	 * @param model
	 * @return
	 * @throws BTSLBaseException
	 */
	private String notification(HttpServletRequest request,C2SRechargeModel c2sRechargeModel,final Model model) throws BTSLBaseException{

        final String methodName = "notification";
        LogFactory.printLog(methodName, PretupsI.ENTERED, LOG);
        String jspScreen=null;
		ChannelUserVO userVO = (ChannelUserVO)this.getUserFormSession(request);

        try {
            if (request.getParameter("btnBack") != null) {
            	jspScreen="c2stransfer/c2sRechargeView";
            } else {
            	final C2SRechargeModel theForm =  c2sRechargeModel;
    			theForm.setFinalMesasge(true);
    			jspScreen= rechargeService.notify(request,theForm,userVO);
    			request.getSession().removeAttribute(RC_MODEL_BACK);
    			request.getSession().setAttribute(RC_MODEL_BACK, theForm);
                model.addAttribute(RC_MODEL_NEW	,theForm); 
                request.getSession().setAttribute(RC_MODEL_NEW, theForm);
                
            }
        } catch (Exception e) {
        	LOG.errorTrace(methodName, e);
        	throw new BTSLBaseException(e);
        } finally {
            LogFactory.printLog(methodName, PretupsI.EXITED, LOG);
        }
        return jspScreen;
	}
	
}

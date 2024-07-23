package com.web.pretups.programcategory.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.common.CommonValidationInterface;
import com.btsl.common.CommonValidatorVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.SqlParameterEncoder;
import com.web.pretups.programcategory.businesslogic.ProgramCategoryBL;
import com.web.pretups.programcategory.businesslogic.ProgramCategoryVO;

@SessionAttributes("progCategoryForm")
@Controller
@Lazy
@Scope("session")
public class ProgramCategoryController extends CommonController{

  
   private ProgramCategoryForm progCategoryForm;

    @Autowired
    private ProgramCategoryBL programCategoryBL;

    @Autowired
    private ProgramCategoryVO programCategoryVO;

    @RequestMapping(value = "/ProgramManagement/userProgramManagement.form", method = RequestMethod.GET)
    public String loadProgramCategory(Map<String, Object> model, HttpServletRequest request,BindingResult result, HttpServletResponse response) {
        final String methodName = "loadProgramCategory";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }

        try {
        	UserVO userVO;
    		userVO = getUserFormSession(request);
    		authorise(request, response, "PRGMT001", false);
            progCategoryForm = new ProgramCategoryForm();
            programCategoryBL.loadProgramCategory(progCategoryForm);
            model.put("progCategoryForm", progCategoryForm);
            final ProgramCategoryVO programCategoryVO = new ProgramCategoryVO();
            programCategoryVO.setProgramList(progCategoryForm.getProgramList());

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exited");
            }
        }
        catch (BTSLBaseException e) {
            log.error(methodName, "Exceptin:e=" + e);
            log.errorTrace(methodName, e);
            handleError(this,methodName,e, result);
        }
        catch (Exception e) {
            log.errorTrace("ConfirmProgramCategory", e);
            return "security/unAuthorisedAccessF";
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited");
        }

        return "programmanagement/programCategory";
    }

    @RequestMapping(value = "/ProgramManagement/userProgramManagementRules.form", method = RequestMethod.POST)
    public String defineProgramCategory(@ModelAttribute("progCategoryForm") ProgramCategoryForm progCategoryForm, Map<String, Object> model, HttpServletRequest request) {
        final String methodName = "defineProgramCategory";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        ArrayList<ProgramCategoryVO> List = new ArrayList<ProgramCategoryVO>();
        try {

            ProgramCategoryVO programCategoryVO = new ProgramCategoryVO();
            programCategoryVO.setProgramType(progCategoryForm.getProgramType());
            programCategoryVO.setProgramList(progCategoryForm.getProgramList());
            progCategoryForm = new ProgramCategoryForm();
            progCategoryForm.setProgramList(programCategoryVO.getProgramList());
            programCategoryBL.loadProgramEarningType(progCategoryForm);
            programCategoryBL.loadProgramRewardType(progCategoryForm);
            programCategoryBL.loadRedempFrequencyType(progCategoryForm);
            programCategoryBL.loadRedempWalletType(progCategoryForm);
            progCategoryForm.setProgramType(programCategoryVO.getProgramType());
            model.put("progCategoryForm", progCategoryForm);
            List = programCategoryBL.checkIfRuleSetExists(programCategoryVO);
            if (List.size() > 0) {
                programCategoryVO = (ProgramCategoryVO) List.get(0);
                progCategoryForm.setRewardType(programCategoryVO.getRewardType());
                progCategoryForm.setMaxPeriod(programCategoryVO.getMaxPeriod());
                progCategoryForm.setMinPeriod(programCategoryVO.getMinPeriod());
                progCategoryForm.setPointsForParent(programCategoryVO.isPointsForParent());
                progCategoryForm.setAutoRedempAll(programCategoryVO.isAutoRedempAll());
                progCategoryForm.setRedempFrequency(programCategoryVO.getRedempFrequency());
                progCategoryForm.setRedempWalletType(programCategoryVO.getRedempWalletType());
                progCategoryForm.setProgramEarningType(programCategoryVO.getProgramEarningType());
                progCategoryForm.setRuleListExist(programCategoryVO.getRuleListExist());
                model.put("progCategoryForm", progCategoryForm);
                return "programmanagement/viewProgramCategoryRules";
            } else {
                return "programmanagement/programCategoryRules";
            }

        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited");
        }

        return "programmanagement/programCategoryRules";
    }

    @RequestMapping(value = "/ProgramManagement/userProgramManagementDefineRules.form", method = RequestMethod.POST, params = "modify")
    public String ConfirmProgramCategory(@ModelAttribute("progCategoryForm") ProgramCategoryForm progCategoryForm, BindingResult result, Map<String, Object> model, HttpServletRequest request) {
        final String methodName = "ConfirmProgramCategory";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        try {
            progCategoryForm.setProgramEarningSelList((Arrays.asList(progCategoryForm.getProgramEarningType().split(","))));
            progCategoryForm.setRewardTypeSelList((Arrays.asList(progCategoryForm.getRewardType().split(","))));
            progCategoryForm.setRedempFreqSelList((Arrays.asList(progCategoryForm.getRedempFrequency().split(","))));
            progCategoryForm.setRedempWalletSelList((Arrays.asList(progCategoryForm.getRedempWalletType().split(","))));
            model.put("progCategoryForm", progCategoryForm);
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited");
        }
        return "programmanagement/programCategoryRules";

    }

    @RequestMapping(value = "/ProgramManagement/userProgramManagementConfirmRules.form", method = RequestMethod.POST, params = "submit")
    public String ModifyProgramCategory(@ModelAttribute("progCategoryForm") ProgramCategoryForm progCategoryForm, BindingResult result, Map<String, Object> model, HttpServletRequest request) {
        final String methodName = "ModifyProgramCategory";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        try {
            progCategoryForm.setProgramEarningType(StringUtils.join(progCategoryForm.getProgramEarningSelList(), ","));
            progCategoryForm.setRedempFrequency(SqlParameterEncoder.encodeParams(StringUtils.join(progCategoryForm.getRedempFreqSelList(), ",")));
            progCategoryForm.setRewardType(SqlParameterEncoder.encodeParams(StringUtils.join(progCategoryForm.getRewardTypeSelList(), ",")));

            final String Configpath = "configfiles/MessageResources";
            final String validationXMLpath = "configfiles/categoryManagement/validator-programcategory.xml";
            final CommonValidationInterface commonValidationInterface = new CommonValidationInterface();
            final ArrayList errorMessageList = commonValidationInterface.checkErrorListForWeb(progCategoryForm, Configpath, validationXMLpath);
         
            if (errorMessageList.size() > 0) {
                for (int i = 0; i <  errorMessageList.size(); i++) {
                    final CommonValidatorVO pVO = (CommonValidatorVO) errorMessageList.get(i);

                    final ObjectError error = new ObjectError(pVO.getPropertyName(), pVO.getPropertyMessage());
                    result.addError(error);
                }
            }
            if (result.hasErrors()) {
                return "programmanagement/programCategoryRules";
            }
            try {
                programCategoryBL.businessValidation(progCategoryForm);
            } catch (BTSLBaseException e) {
                log.errorTrace(methodName, e);
                result.reject(e.getMessage());
                return "programmanagement/programCategoryRules";
            }

        } catch (BTSLBaseException e) {
            result.reject(e.getMessage());
            log.errorTrace(methodName, e);
            return "programmanagement/programCategoryRules";
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited");
        }

        return "programmanagement/programViewConfirm";
    }

    @RequestMapping(value = "/ProgramManagement/userProgramManagementSaveRules.form", method = RequestMethod.POST, params = "confirm")
    public String SaveProgramCategory(@ModelAttribute("progCategoryForm") ProgramCategoryForm progCategoryForm, BindingResult result, Map<String, Object> model, HttpServletRequest request) {
        final String methodName = "SaveProgramCategory";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        try {
            programCategoryVO.setProgramType(progCategoryForm.getProgramType());
            programCategoryVO.setMaxPeriod(progCategoryForm.getMaxPeriod());
            programCategoryVO.setRewardType(progCategoryForm.getRewardType());
            programCategoryVO.setMinPeriod(progCategoryForm.getMinPeriod());
            programCategoryVO.setPointsForParent(progCategoryForm.isPointsForParent());
            programCategoryVO.setAutoRedempAll(progCategoryForm.isAutoRedempAll());
            programCategoryVO.setRedempFrequency(progCategoryForm.getRedempFrequency());
            programCategoryVO.setRedempWalletType(progCategoryForm.getRedempWalletType());
            programCategoryVO.setProgramEarningType(progCategoryForm.getProgramEarningType());
            if (("Y").equals(progCategoryForm.getRuleListExist())) {
                programCategoryBL.updateProgramRules(programCategoryVO);
            } else {
                programCategoryBL.saveProgramRules(programCategoryVO);
            }
        } catch (BTSLBaseException e) {
            result.reject(e.getMessage());
            log.errorTrace(methodName, e);
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited");
        }

        return "programmanagement/programCategory";
    }

    @RequestMapping(value = "/ProgramManagement/userProgramManagementDefineRules.form", method = RequestMethod.POST, params = "back")
    public String backViewRules(@ModelAttribute("progCategoryForm") ProgramCategoryForm progCategoryForm, BindingResult result, Map<String, Object> model, HttpServletRequest request) {
        final String methodName = "backViewRules";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited");
        }
        return "programmanagement/programCategory";

    }

    @RequestMapping(value = "/ProgramManagement/userProgramManagementConfirmRules.form", method = RequestMethod.POST, params = "back")
    public String backDefineRules(@ModelAttribute("progCategoryForm") ProgramCategoryForm progCategoryForm, BindingResult result, Map<String, Object> model, HttpServletRequest request) {
        final String methodName = "backDefineRules";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        try {
            if (("Y").equals(progCategoryForm.getRuleListExist())) {
                programCategoryVO.setProgramType(progCategoryForm.getProgramType());
                final ArrayList<ProgramCategoryVO> List = programCategoryBL.checkIfRuleSetExists(programCategoryVO);
                programCategoryVO = (ProgramCategoryVO) List.get(0);
                progCategoryForm.setRewardType(programCategoryVO.getRewardType());
                progCategoryForm.setMaxPeriod(programCategoryVO.getMaxPeriod());
                progCategoryForm.setMinPeriod(programCategoryVO.getMinPeriod());
                progCategoryForm.setPointsForParent(programCategoryVO.isPointsForParent());
                progCategoryForm.setAutoRedempAll(programCategoryVO.isAutoRedempAll());
                progCategoryForm.setRedempFrequency(programCategoryVO.getRedempFrequency());
                progCategoryForm.setRedempWalletType(programCategoryVO.getRedempWalletType());
                progCategoryForm.setProgramEarningType(programCategoryVO.getProgramEarningType());
                return "programmanagement/viewProgramCategoryRules";
            }
        } catch (Exception e) {
            log.errorTrace(methodName, e);
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited");
        }
        return "programmanagement/programCategory";
    }

    @RequestMapping(value = "/ProgramManagement/userProgramManagementSaveRules.form", method = RequestMethod.POST, params = "back")
    public String backSaveRules(@ModelAttribute("progCategoryForm") ProgramCategoryForm progCategoryForm, BindingResult result, Map<String, Object> model, HttpServletRequest request) {
        final String methodName = "backSaveRules";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited");
        }
        return "programmanagement/programCategoryRules";
    }

    @RequestMapping(value = "/ProgramManagement/userProgramManagementContDefineRules.form", method = RequestMethod.POST, params = "continue")
    public String continueDefineRules(@ModelAttribute("progCategoryForm") ProgramCategoryForm progCategoryForm, BindingResult result, Map<String, Object> model, HttpServletRequest request) {
        final String methodName = "continueDefineRules";
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited");
        }
        return "programmanagement/programCategory";

    }
}

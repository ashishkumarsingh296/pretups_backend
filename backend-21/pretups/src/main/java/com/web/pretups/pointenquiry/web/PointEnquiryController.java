package com.web.pretups.pointenquiry.web;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.common.CommonValidationInterface;
import com.btsl.common.CommonValidatorVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.user.businesslogic.UserVO;
import com.google.gson.Gson;
import com.web.pretups.pointenquiry.businesslogic.PointEnquiryBL;
import com.web.pretups.pointenquiry.businesslogic.PointEnquiryVO;
import com.web.pretups.programcategory.web.ProgramCategoryController;

@Controller
@SessionAttributes("pointEnquiryForm")
@Lazy
@Scope("session")
public class PointEnquiryController extends CommonController {
    private static final Log LOG = LogFactory.getLog(ProgramCategoryController.class.getName());
    /*
     * @Autowired PointEnquiryForm pointEnquiryForm;
     */

    @Autowired
    private PointEnquiryBL pointEnquiryBL;

    @Autowired
    private PointEnquiryVO pointEnquiryVO;
    @SuppressWarnings("rawtypes")
    private Map jsonData = new LinkedHashMap();

    @SuppressWarnings("rawtypes")
    public Map getJsonData() {
        return jsonData;
    }

    @SuppressWarnings("rawtypes")
    public void setJsonData(Map jsonData) {
        this.jsonData = jsonData;
    }

    /**
     * This method is called for the first time when the user clicks on channel
     * enquiry link. In this method we load the geography and domain for the
     * user to search on its basis
     * 
     * @param pointEnquiryForm
     * @param model
     * @param request
     * @return String(jsp path)
     */
    @RequestMapping(value = "/PointEnquiry/bonuspointenquiry.form", method = RequestMethod.GET)
    public String load(Map<String, Object> model, HttpServletRequest request) {
        final String methodName = "load";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        // UserVO userVO = null;
        Connection con = null;
        try {
            PointEnquiryForm pointEnquiryForm = new PointEnquiryForm();
            con = loadUserSearchDetail(pointEnquiryForm, request);
            model.put("pointEnquiryForm", pointEnquiryForm);

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exited");
            }

        } catch (Exception e) {
            LOG.errorTrace(methodName, e);

        } finally {
            try {
                if (con != null) {
                    con.close();
                }

            } catch (Exception e) {
                LOG.errorTrace(methodName, e);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting");
            }
        }
        return "pointenquiry/selectEnquiry";
    }

    /**
     * @param pointEnquiryForm
     * @param request
     * @return
     * @throws BTSLBaseException
     */
    private Connection loadUserSearchDetail(PointEnquiryForm pointEnquiryForm, HttpServletRequest request)
            throws BTSLBaseException {
        UserVO userVO;
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userVO = getUserFormSession(request);
            pointEnquiryBL.setDetailsForUserSearch(pointEnquiryForm, userVO, con);
            return con;
        } catch (Exception e) {
            LOG.errorTrace("loadUserSearchDetail", e);
            return con;
        } finally {
            if (mcomCon != null) {
                mcomCon.close("PointEnquiryController#loadUserSearchDetail");
                mcomCon = null;
            }
        }

    }

    /**
     * This method is called when the user either enters the msisdn or selects
     * domain,category,geography.
     * 
     * @param pointEnquiryForm
     * @param model
     * @param result
     * @param request
     * @return String(jsp path)
     */
    @RequestMapping(value = "/PointEnquiry/displayEnquiry.form", method = RequestMethod.POST)
    public String displayDetails(@ModelAttribute("pointEnquiryForm") PointEnquiryForm pointEnquiryForm,
            Map<String, Object> model, HttpServletRequest request, BindingResult result) {
        final String methodName = "displayDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }

        UserVO userVO = null;
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            userVO = getUserFormSession(request);
            if (!(pointEnquiryForm.getMsisdn().isEmpty())) {
                pointEnquiryBL.displayDetailsUsingMsisdn(con, pointEnquiryForm);
            } else {
                if ("NONE".equals(pointEnquiryForm.getGeography().replaceAll(",", ""))
                        || "NONE".equals(pointEnquiryForm.getDomain()) || "NONE".equals(pointEnquiryForm.getCategory())) {
                    final CommonValidationInterface commonValidationInterface = new CommonValidationInterface();

                    final ArrayList<CommonValidatorVO> errorMessageList = commonValidationInterface
                            .checkErrorListForWeb(pointEnquiryForm, "configfiles/MessageResources",
                                    "configfiles/pointenquiry/validator-pointEnquiry.xml");
                    int errorMessageSize = errorMessageList.size();
                    if (errorMessageList.size() > 0) {
                        for (int i = 0; i < errorMessageSize; i++) {
                            final CommonValidatorVO pVO = errorMessageList.get(i);

                            final ObjectError error = new ObjectError(pVO.getPropertyName(), pVO.getPropertyMessage());
                            result.addError(error);
                        }

                    }
                    if (result.hasErrors()) {

                        model.put("pointEnquiryForm", pointEnquiryForm);
                        return "pointenquiry/selectEnquiry";
                    }
                }

                pointEnquiryForm.setNetworkCode(userVO.getNetworkID());
                pointEnquiryBL.displayDetailsUsingUserSearch(con, pointEnquiryForm, userVO);
            }
            model.put("pointEnquiryForm", pointEnquiryForm);

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exited");
            }

        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            result.reject(be.getMessage());
            return "pointenquiry/selectEnquiry";

        }

        catch (Exception e) {
            LOG.errorTrace(methodName, e);

        } finally {
            if (mcomCon != null) {
                mcomCon.close("PointEnquiryController#displayDetails");
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting");
            }

        }
        if (!(pointEnquiryForm.getMsisdn().isEmpty())) {
            return "pointenquiry/displayEnquiry";
        } else {
            return "pointenquiry/searchUser";
        }

        // }
    }

    @RequestMapping(value = "/PointEnquiry/backrequest.form", method = RequestMethod.POST)
    public String back(@ModelAttribute("pointEnquiryForm") PointEnquiryForm pointEnquiryForm,
            Map<String, Object> model, HttpServletRequest request) {
        final String methodName = "back";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        try {

            if (request.getParameter("cancel") != null) {
                pointEnquiryForm.setMsisdn(null);
                return "pointenquiry/selectEnquiry";
            }
            if (request.getParameter("back") != null) {

                return "pointenquiry/selectEnquiry";
            }

        } catch (Exception e) {
            LOG.errorTrace(methodName, e);

        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exited");
            }
        }

        return null;
    }

    /**
     * This method is called to load category on the the selection on Domain.
     * 
     * @param pointEnquiryForm
     * @param model
     * @param request
     * @return json
     */
    @RequestMapping(value = "/PointEnquiry/selectCategory.form", method = RequestMethod.POST)
    public @ResponseBody String loadCategories(@ModelAttribute("pointEnquiryForm") PointEnquiryForm pointEnquiryForm,
            Map<String, Object> model, HttpServletRequest request) {
        final String methodName = "loadCategories";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        String json = null;
        try {

            mcomCon = new MComConnection();
            con = mcomCon.getConnection();
            pointEnquiryBL.loadCategory(pointEnquiryForm, con, pointEnquiryForm.getDomain());
            model.put("pointEnquiryForm", pointEnquiryForm);

            json = new Gson().toJson(pointEnquiryForm.getCategoryList());

        } catch (Exception e) {
            LOG.errorTrace(methodName, e);

        } finally {
            if (mcomCon != null) {
                mcomCon.close("PointEnquiryController#loadCategories");
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting");
            }
        }
        return json;
    }

    @RequestMapping(value = "/PointEnquiry/showsearchedetail.form", method = RequestMethod.POST)
    public String showSearchedDetail(@ModelAttribute("pointEnquiryForm") PointEnquiryForm pointEnquiryForm,
            Map<String, Object> model, HttpServletRequest request, BindingResult result) {
        final String methodName = "showSearchedDetail";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        Connection con = null;
        MComConnectionI mcomCon = null;
        try {
            if (request.getParameter("backak") != null) {
                return "pointenquiry/searchUser";
            }
            if (request.getParameter("back2") != null) {
                pointEnquiryForm.setGeography(pointEnquiryForm.getGeography());
                return "pointenquiry/selectEnquiry";
            }

            if (request.getParameter("cancel") != null || request.getParameter("canceluser") != null) {
                pointEnquiryForm.setCategoryList(null);
                pointEnquiryForm.setDomainList(null);
                pointEnquiryForm.setDomain(null);
                // load(pointEnquiryForm, model, request);

                loadUserSearchDetail(pointEnquiryForm, request);
                model.put("pointEnquiryForm", pointEnquiryForm);
                return "pointenquiry/selectEnquiry";
            }
            mcomCon = new MComConnection();
            con = mcomCon.getConnection();

            pointEnquiryForm.getUserID();

            pointEnquiryBL.displayDetailUsingUserID(pointEnquiryForm, con);

            model.put("pointEnquiryForm", pointEnquiryForm);

            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exited");
            }

        } catch (BTSLBaseException be) {
            LOG.errorTrace(methodName, be);
            result.reject(be.getMessage());
            return "pointenquiry/searchUser";

        }

        catch (Exception e) {
            LOG.errorTrace(methodName, e);

        } finally {
            if (mcomCon != null) {
                mcomCon.close("PointEnquiryController#"+methodName);
                mcomCon = null;
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting");
            }
        }
        return "pointenquiry/displaydetailUserSearch";
    }

}

package com.web.pretups.channel.reports.web;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.text.ParseException;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonController;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.pretups.channel.reports.businesslogic.ChannelUserReportDAO;
import com.btsl.pretups.channel.reports.businesslogic.DownloadReportVO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserGeographiesVO;
//import com.btsl.pretups.channel.reports.service.ChannelUserReportService;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.reports.service.ChannelUserReportService;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferRuleWebDAO;

/**
 * This class provides method for loading UI for Bar user as well as processing
 * data for Bar user request
 */
@Controller
public class ChannelUserReportController extends CommonController {

    @Autowired
    private ChannelUserReportService channelUserReportService;

    private static final String MODEL_KEY = "barredUser";
    private static final String SESSION_KEY = "unbarUserObject";
    private static final String VIEW_BAR_USER_KEY = "viewBarredUserObject";
    private static final String MODULE_LIST = "moduleList";
    private static final String USER_TYPE = "userType";
    private static final String BAR_TYPE = "barredType";
    private static final String UNBAR_URL = "subscriber/unbarUser";

    private List<ListValueVO> domainListGlobal = new ArrayList<>();
    private List<ListValueVO> categoryListGlobal = new ArrayList<>();
    private List<ListValueVO> userListGlobal = new ArrayList<>();
    private List<ListValueVO> TxnSubTypeListGlobal = new ArrayList<>();
    private List<ListValueVO> trfCatListGlobal = new ArrayList<>();

    private static ChannelUserReportsVO channelUserReportVO = new ChannelUserReportsVO();

    private String catName;

    /**
     * Load bar user UI as well as modules and user type
     *
     * @param request
     *            The HttpServletRequest object
     * @param response
     *            The HttpServletResponse object
     * @param model
     *            The Model object
     * @return String the path of view also store user type and module in model
     *         object
     * @throws BTSLBaseException
     * @throws IOException
     * @throws ServletException
     * @throws Exception
     */
    @RequestMapping(value = "/reports/loadreportsform.form", method = RequestMethod.GET)
    public String loadChannelUserForm(final Model model, HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String methodName = "ChannelUserReportController#loadChannelUserForm";

        if (log.isDebugEnabled()) {
            log.debug("ChannelUserReportController#loadChannelUserForm", PretupsI.ENTERED);
        }

        authorise(request, response, "O2CRPT001A", false);

        UserVO userVO = this.getUserFormSession(request);


        List<ListValueVO> domainList = new ArrayList<>();

        if (PretupsI.OPT_MODULE.equalsIgnoreCase(userVO.getDomainID())) {
            domainList = channelUserReportService.loadDomain();
            domainList.add(0, new ListValueVO(PretupsI.ALL, PretupsI.ALL));
        } else {
            domainList.add(0, new ListValueVO(userVO.getDomainName(), userVO.getDomainID()));
        }
        domainListGlobal = domainList;
        model.addAttribute("domainList", domainList);
        request.getSession().setAttribute("category", null);

        List<ListValueVO> TxnSubType = channelUserReportService.loadTxnSubType();
        model.addAttribute("TxnSubType", TxnSubType);
        TxnSubTypeListGlobal = TxnSubType;

        request.getSession().setAttribute("TxnSubType", TxnSubType);

        List<ListValueVO> geographyList = new ArrayList<>();

        if (userVO.getGeographicalAreaList() != null) {

            for (int i = 0, j = userVO.getGeographicalAreaList().size(); i < j; i++) {

                geographyList.add(new ListValueVO(
                        ((UserGeographiesVO) userVO.getGeographicalAreaList().get(i)).getGraphDomainName(),
                        ((UserGeographiesVO) userVO.getGeographicalAreaList().get(i)).getGraphDomainCode()));

            }
            model.addAttribute("geographyList", geographyList);

        } else {
            log.debug(methodName, "geographyList is empty , problem with UserVO");
        }

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting with geography list: " + geographyList + " " + userVO.getUserType() + " "
                    + userVO.getDomainID() + " " + userVO.getDomainName());
        }
        request.getSession().setAttribute("domainList", domainList);
        request.getSession().setAttribute("geographyList", geographyList);

        model.addAttribute(MODEL_KEY, new DownloadReportVO());

        if (log.isDebugEnabled()) {
            log.debug("ChannelUserReportController#loadChannelUserForm", PretupsI.EXITED + domainList);
        }
        return "c2s/o2ctransferNEW";
    }

    @RequestMapping(value = "/reports/load-category.form", method = RequestMethod.POST)
    public @ResponseBody List loadCategory(@RequestParam("domain") String domain, Model model,
                                           HttpServletRequest request, HttpServletResponse response) throws Exception

    {
        UserVO userVO = this.getUserFormSession(request);
        final String methodName = "LoadCategory";

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered : domain =" + domain + " and networkId: " + userVO.getNetworkID());
        }
        List<ListValueVO> categoryList = null;

        if (domain != null && domain.trim().equalsIgnoreCase("ALL")) {
            categoryList = new ArrayList<ListValueVO>();
            categoryList.add(new ListValueVO("ALL", "ALL:ALL"));

        } else {

            categoryList = channelUserReportService.loadCategory();
        }

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting::::::" + categoryList);
        }

        if (!domain.equals(PretupsI.ALL)) {
            ListValueVO listValueVO = null;
            if (categoryList != null && !categoryList.isEmpty()) {
                for (int i = 0, j = categoryList.size(); i < j; i++) {
                    listValueVO = (ListValueVO) categoryList.get(i);

                    if (!(listValueVO.getValue().split(":")[0].toString()).equals(domain)) {
                        categoryList.remove(i);
                        i--;
                        j--;
                    }
                }
            }

        }

        channelUserReportVO.setCategoryList(categoryList);
        categoryListGlobal = categoryList;

        model.addAttribute("categoryList", categoryList);
        request.getSession().setAttribute("categoryList", categoryList);

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting FINAL::::" + categoryList);
        }

        channelUserReportVO.setDomainCode(domain);

        channelUserReportVO.setDomainList(domainListGlobal);

        if (channelUserReportVO.getDomainCode().equals(PretupsI.ALL)) {

            if (channelUserReportVO.getDomainList() != null && !channelUserReportVO.getDomainList().isEmpty()) {
                String domainCode = "";
                for (int i = 0, j = channelUserReportVO.getDomainList().size(); i < j; i++) {
                    ListValueVO listValueVO1;
                    listValueVO1 = (ListValueVO) channelUserReportVO.getDomainList().get(i);
                    domainCode = domainCode + listValueVO1.getValue() + "','";
                }
                domainCode = domainCode.substring(0, domainCode.length() - 3);
                channelUserReportVO.setDomainListString(domainCode);
            }
            channelUserReportVO.setDomainName(PretupsI.ALL);
        }

        else {
            ListValueVO listValueVO = BTSLUtil.getOptionDesc(channelUserReportVO.getDomainCode(),
                    (ArrayList) channelUserReportVO.getDomainList());
            channelUserReportVO.setDomainName(listValueVO.getLabel());
            channelUserReportVO.setDomainListString(channelUserReportVO.getDomainCode());
        }

        if (!domain.equals(PretupsI.ALL))
            categoryList.add(0, new ListValueVO(PretupsI.ALL, PretupsI.ALL + ":" + PretupsI.ALL));
        return categoryList;

    }

    @RequestMapping(value = "/reports/load-user-list.form", method = RequestMethod.GET)
    public String loadUserList(@RequestParam("domain") String domain, @RequestParam("category") String category,
                               @RequestParam("geography") String geography, @RequestParam("owner") String user, Model model,
                               HttpServletRequest request, HttpServletResponse response) throws BTSLBaseException, IOException {

        final String methodName = "loadUserList";
        UserVO userVO = this.getUserFormSession(request);

        String catCode = request.getParameter("category").split(":")[1];
        String catDomainCode = request.getParameter("category").toString();

        if (log.isDebugEnabled()) {
            log.debug(methodName,
                    "Entered with : DOMAIN=" + domain + ",CATEGORY=" + category + ",GEOGRAPHY=" + geography + ",User="
                            + user + "category code::" + catCode + "category list global::" + categoryListGlobal);
        }
        log.debug(methodName, "catDomainCode " + catDomainCode + " : hannelUserReportVO.getCategoryList()"
                + channelUserReportVO.getCategoryList() + " : ");

        log.debug(methodName, "USERVO CATCODE:::" + userVO.getCategoryCode());

        ListValueVO catgoryListValueVO = BTSLUtil.getOptionDesc(catDomainCode,
                (ArrayList) channelUserReportVO.getCategoryList());

        catName = catgoryListValueVO.getLabel();

        log.debug(methodName, "catgoryListValueVO" + catgoryListValueVO + " : "
                + channelUserReportVO.getFromtransferCategoryName() + " : " + catgoryListValueVO.getLabel());

        log.debug(methodName, "catgoryListValueVO" + catgoryListValueVO + " : "
                + channelUserReportVO.getFromtransferCategoryName() + " : " + catgoryListValueVO.getLabel());

        List<ListValueVO> userList = new ArrayList<ListValueVO>();

        if (catCode.equalsIgnoreCase(userVO.getCategoryCode())) {
            userList.add(new ListValueVO(userVO.getUserName(), userVO.getUserID() + ":" + userVO.getUserName()));
        }

        else {
            if (PretupsI.ALL.equalsIgnoreCase(catCode)) {

                userList.add(new ListValueVO("ALL", "ALL:ALL"));
            } else {
                userList = channelUserReportService.loadUserData(domain, category, geography, user, userVO.getUserID(),
                        userVO.getDomainID());
                if (log.isDebugEnabled()) {
                    log.debug(methodName, "USER LIST::" + userList);
                }
            }

        }
        userListGlobal = userList;
        model.addAttribute("userList", userList);
        model.addAttribute("domain", domain);
        model.addAttribute("category", category);
        model.addAttribute("geography", geography);
        model.addAttribute("user", user);
        request.getSession().setAttribute("selectedcategory", request.getParameter("category"));
        request.getSession().setAttribute("geography", request.getParameter("geography"));
        request.getSession().setAttribute("owner", request.getParameter("owner"));
        request.getSession().setAttribute("domain", request.getParameter("domain"));
        request.getSession().setAttribute("userList", request.getParameter("userList"));
        model.addAttribute("userList", userList);
        System.out.println("SSSSSSS : " + userList.size());
        if (userList.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "List is null");
            }
            model.addAttribute("fail",
                    PretupsRestUtil.getMessageString("userreturn.c2cwithdraw.nouserexist.msg", new String[] { user }));
        } else {
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exited." + userList);
            }
        }

        if (log.isDebugEnabled()) {
            log.debug(methodName,
                    "selectedcategoryselectedcategoryselectedcategory" + request.getParameter("category"));
        }

        return "c2s/usersearch";
    }

    @RequestMapping(value = "/reports/select-search-user.form", method = RequestMethod.GET)
    public String selectSearchedUser(Model model, HttpServletRequest request) throws BTSLBaseException, IOException {

        final String methodName = "#selectSearchedUser";

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered: " + request.getParameter("userList"));
        }

        UserVO userVO = this.getUserFormSession(request);

        if (!BTSLUtil.isNullString(request.getParameter("userList")))
            request.getSession().setAttribute("userList", request.getParameter("userList"));
        log.debug(methodName, request.getParameter("category"));

        if (log.isDebugEnabled()) {
            log.debug(methodName,
                    request.getSession().getAttribute("category") + " - "
                            + request.getSession().getAttribute("geography") + " - "
                            + request.getSession().getAttribute("userList") + " - "
                            + request.getSession().getAttribute("domain"));
            log.debug(methodName, "Exited: ");
        }

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited: ");
        }
        return "c2s/addTemp";
    }

    @RequestMapping(value = "/reports/load-transfer-category.form", method = RequestMethod.POST)
    public @ResponseBody List loadTransferCategory(@RequestParam("TxnSubType") String TxnSubType, Model model,
                                                   HttpServletRequest request, HttpServletResponse response) throws Exception {

        final String methodName = "loadTransferCategory";
        List<ListValueVO> trfCatList = null;
        if (TxnSubType.equalsIgnoreCase("T"))
            trfCatList = channelUserReportService.loadTransfercategory();
        else if (PretupsI.ALL.equalsIgnoreCase(TxnSubType)) {
            trfCatList = new ArrayList<ListValueVO>();
            trfCatList.add(new ListValueVO("ALL", "ALL"));
        }
        request.getSession().setAttribute("trfCatList", trfCatList);
        trfCatListGlobal = trfCatList;

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered with : TRAF CATEGORY=" + TxnSubType);
        }

        if (log.isDebugEnabled()) {
            log.debug(methodName, "Exited.");
        }
        channelUserReportVO.setTxnSubType(TxnSubType);
        channelUserReportVO.setTxnSubTypeList(TxnSubTypeListGlobal);
        ListValueVO listValueVO = BTSLUtil.getOptionDesc(channelUserReportVO.getTxnSubType(),
                (ArrayList) channelUserReportVO.getTxnSubTypeList());

        return trfCatList;
    }

    @RequestMapping(value = "/reports/submit-user-report.form", method = RequestMethod.POST)
    public String submitUserReport(@ModelAttribute("channelUserReport") ChannelUserReportsVO formVO, Model model,
                                   HttpServletRequest request, HttpServletResponse response) throws Exception {

        final String methodName = "submitUserReport";
        Connection con = null;MComConnectionI mcomCon = null;
        // List<ListValueVO> trfCatList = null;

        final Date currentDate = new Date();

        ArrayList userList = null;

        log.debug(methodName, "FORMVO : " + formVO.toString());
        try{
            String domain = request.getParameter("domainCode");
            String categoryWhole = request.getParameter("category");
            String category = categoryWhole.split(":")[1];
            String categoryName = categoryWhole.split(":")[0];
            String geography = request.getParameter("geography");
            String userName = request.getParameter("toUserName");
            String TxnSubType = request.getParameter("TxnSubType");
            String fromDate = request.getParameter("fromDate");
            String toDate = request.getParameter("toDate");
            String transferCategory = request.getParameter("transferCategory");
            String userId = request.getParameter("toUserId");

            String zoneName = request.getParameter("zoneName2");

            log.debug(methodName, "USER ID::" + userId);
            channelUserReportVO.setUserId(userId);

            channelUserReportVO.setUserName(userName);
            channelUserReportVO.setZoneName(zoneName);

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Entered with : CATEGORY  WHOLE =" + categoryWhole);
            }

            mcomCon = new MComConnection();con=mcomCon.getConnection();
            final ChannelUserReportDAO channelUserDAO = new ChannelUserReportDAO();
            final UserVO userVO = this.getUserFormSession(request);

            Date frDate = null;
            Date tDate = null;

            final ArrayList fromCatList = new ArrayList();
            final ArrayList toCatList = new ArrayList();
            ArrayList transferRulCatList = null;

            ListValueVO listValueVO = null;

            final ChannelTransferRuleWebDAO channelTransferRuleWebDAO = new ChannelTransferRuleWebDAO();

            channelUserReportVO.setFromDate(fromDate);
            channelUserReportVO.setToDate(toDate);
            channelUserReportVO.setCategory(category);
            channelUserReportVO.setGeography(geography);
            if (!BTSLUtil.isNullString(channelUserReportVO.getFromDate())) {
                frDate = BTSLUtil.getDateFromDateString(channelUserReportVO.getFromDate());
            }
            if (!BTSLUtil.isNullString(channelUserReportVO.getToDate())) {
                tDate = BTSLUtil.getDateFromDateString(channelUserReportVO.getToDate());
            }

            channelUserReportVO.setZoneCode(geography);

            channelUserReportVO.setTransferCategory(transferCategory);

            channelUserReportVO.setTransferCategoryList(trfCatListGlobal);

            ListValueVO listValueVONew = BTSLUtil.getOptionDesc(channelUserReportVO.getTransferCategory(),
                    (ArrayList) channelUserReportVO.getTransferCategoryList());
            channelUserReportVO.setTransferCategoryName(listValueVONew.getLabel());

            channelUserReportVO.setNetworkCode(userVO.getNetworkID());
            channelUserReportVO.setNetworkName(userVO.getNetworkName());
            channelUserReportVO.setReportHeaderName(userVO.getReportHeaderName());
            channelUserReportVO.setUserType(userVO.getUserType());
            channelUserReportVO.setCategorySeqNo(((userVO.getCategoryVO().getSequenceNumber()) + "").trim());

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Entered: channelUserReportVO : " + channelUserReportVO.toString());
            }

            channelUserReportVO.setLoggedInUserCategoryCode(userVO.getCategoryVO().getCategoryCode());
            if (userVO.isStaffUser()) {
                channelUserReportVO.setLoggedInUserName(userVO.getParentName());
            } else {
                channelUserReportVO.setLoggedInUserName(userVO.getUserName());
            }
            channelUserReportVO.setLoginUserID(userVO.getUserID());

            if (PretupsI.ALL.equalsIgnoreCase(channelUserReportVO.getDomainCode()))
                transferRulCatList = channelTransferRuleWebDAO.loadChannelTransferRuleVOList(con,
                        channelUserReportVO.getNetworkCode(), null, PretupsI.TRANSFER_RULE_TYPE_OPT);
            else
                transferRulCatList = channelTransferRuleWebDAO.loadChannelTransferRuleVOList(con,
                        channelUserReportVO.getNetworkCode(), channelUserReportVO.getDomainCode(),
                        PretupsI.TRANSFER_RULE_TYPE_OPT);

            ChannelTransferRuleVO channelTransferRuleVO = null;
            boolean isForAllCategory = true;
            String categoryCode = null;
            if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
                isForAllCategory = false;
                categoryCode = userVO.getCategoryCode();
            }

            boolean isCatMatched = false;

            for (int i = 0, k = transferRulCatList.size(); i < k; i++) {
                channelTransferRuleVO = (ChannelTransferRuleVO) transferRulCatList.get(i);

                fromCatList.add(new ListValueVO(channelTransferRuleVO.getFromCategoryDes(),
                        channelTransferRuleVO.getDomainCode() + ":" + channelTransferRuleVO.getFromCategory()));

                toCatList.add(new ListValueVO(channelTransferRuleVO.getToCategoryDes(),
                        channelTransferRuleVO.getFromCategory() + ":" + channelTransferRuleVO.getToCategory()));
            }

            ListValueVO listValueVONext = null;

            final ArrayList tempFromCat = new ArrayList();
            boolean flag = true;
            for (int i = 0, j = fromCatList.size(); i < j; i++) {
                listValueVO = (ListValueVO) fromCatList.get(i);
                flag = true;
                for (int k = i + 1, l = fromCatList.size(); k < l; k++) {
                    listValueVONext = (ListValueVO) fromCatList.get(k);
                    if (listValueVO.getValue().equals(listValueVONext.getValue())) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    tempFromCat.add(new ListValueVO(listValueVO.getLabel(), listValueVO.getValue()));
                }
            }

            listValueVONext = null;
            for (int i = 0, j = toCatList.size(); i < j - 1;) {
                listValueVO = (ListValueVO) toCatList.get(i);
                listValueVONext = (ListValueVO) toCatList.get(i + 1);
                if (listValueVO.getValue().equals(listValueVONext.getValue())) {
                    toCatList.remove(i + 1);
                    j--;
                } else {
                    i++;
                }

            }

            channelUserReportVO.setToCategoryList(toCatList);

            channelUserReportVO.setFromCategoryList(tempFromCat);
            channelUserReportVO.setFromCategoryList(fromCatList);

            String tempfromCatCode = "";
            String temptoCatCode = "";

            tempfromCatCode = categoryWhole;
            temptoCatCode = categoryWhole;

            if (log.isDebugEnabled()) {
                log.debug(methodName, "Entered: " + tempfromCatCode + temptoCatCode);
            }
            commonReportDateFormatNew(channelUserReportVO, methodName);

            channelUserReportVO.setFromtransferCategoryCode(category);

            channelUserReportVO.setTotransferCategoryCode(category);

            displayO2CHistoryNew(channelUserReportVO, userVO, listValueVO, request);

            channelUserReportVO.setZoneName(zoneName);
            channelUserReportVO.setFromtransferCategoryName(catName);

            log.debug(methodName, "Final VO : " + channelUserReportVO.toString());

            model.addAttribute("channelUserReportVO", channelUserReportVO);

            model.addAttribute("failReport", PretupsRestUtil.getMessageString("label.message.new"));

            request.getSession().setAttribute("channelUserReportVO", channelUserReportVO);
            if (PretupsI.OPT_MODULE.equalsIgnoreCase(userVO.getUserType())) {
                if (!BTSLUtil.isNullString(channelUserReportVO.getUserName())
                        && PretupsI.ALL.equals(channelUserReportVO.getUserName())) {
                    channelUserReportVO.setUserId(PretupsI.ALL);
                    return "c2s/o2cTransferDetailsReportNew";
                }

                else if (!BTSLUtil.isNullString(channelUserReportVO.getUserName()) && userListGlobal.size() == 1) {
                    final ListValueVO channelUserTransferVO = (ListValueVO) userListGlobal.get(0);
                    channelUserReportVO.setUserName(channelUserTransferVO.getLabel());
                    channelUserReportVO.setUserId(channelUserTransferVO.getValue());
                    // forward = mapping.findForward(methodName);
                    return "c2s/o2cTransferDetailsReportNew";
                } else if (!BTSLUtil.isNullString(channelUserReportVO.getUserName()) && userListGlobal.size() > 1) {

                    /*
                     * This is the case when userList size greater than 1 if user
                     * click the submit button(selectcategoryForEdit.jsp) after
                     * performing search through searchUser and select one form the
                     * shown list at that time we set the userid on the form(becs
                     * two user have the same name but different id) so here we
                     * check the userId is null or not it is not null iterate the
                     * list and open the screen in edit mode corresponding to the
                     * userid
                     */

                    boolean flag1 = true;
                    if (!BTSLUtil.isNullString(channelUserReportVO.getUserId())) {
                        for (int i = 0, j = userListGlobal.size(); i < j; i++) {
                            final ListValueVO channelUserTransferVO = (ListValueVO) userListGlobal.get(i);
                            if (channelUserReportVO.getUserId().equals(channelUserTransferVO.getValue())) {
                                channelUserReportVO.setUserName(channelUserTransferVO.getLabel());
                                flag1 = false;
                                return "c2s/o2cTransferDetailsReportNew";

                            }
                        }
                    }
                }
            }
        }finally{
            if(mcomCon != null){mcomCon.close("ChannelUserReportController#submitUserReport");mcomCon=null;}
        }
        return "c2s/o2cTransferDetailsReportNew";
    }

    public void commonReportDateFormatNew(ChannelUserReportsVO channelUserReportVO, String methodName) throws ParseException {
        Date frDate = null;
        Date tDate = null;
        Date temptDate = null;
        String fromdate = null;
        String todate = null;
        String temptodate = null;

        if (!BTSLUtil.isNullString(channelUserReportVO.getFromDate())) {
            frDate = BTSLUtil.getDateFromDateString(channelUserReportVO.getFromDate());
        }
        if (!BTSLUtil.isNullString(channelUserReportVO.getToDate())) {
            tDate = BTSLUtil.getDateFromDateString(channelUserReportVO.getToDate());
        }
        if (frDate != null) {
            fromdate = BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(frDate));
        }
        if (tDate != null) {
            todate = BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(tDate));
        }
        if (!BTSLUtil.isNullString(fromdate)) {
            channelUserReportVO.setRptfromDate(BTSLUtil.reportDateFormat(fromdate));
        }
        if (!BTSLUtil.isNullString(todate)) {
            channelUserReportVO.setRpttoDate(BTSLUtil.reportDateFormat(todate));
        }

        if(!BTSLUtil.isNullString(channelUserReportVO.getDailyDate()))
        {
            temptDate = BTSLUtil.getDateFromDateString(channelUserReportVO.getDailyDate());
        }

        if(temptDate != null)
        {
            temptodate=BTSLUtil.sqlDateToDateYYYYString(BTSLUtil.getSQLDateFromUtilDate(temptDate));
            channelUserReportVO.setRptcurrentDate(BTSLUtil.reportDateFormat(temptodate)); // report format date
        }

    }

    public void displayO2CHistoryNew(ChannelUserReportsVO channelUserReportVO, UserVO userVO, ListValueVO listValueVO, HttpServletRequest request) {
        channelUserReportVO.setNetworkCode(userVO.getNetworkID());
        channelUserReportVO.setNetworkName(userVO.getNetworkName());
        channelUserReportVO.setReportHeaderName(userVO.getReportHeaderName());
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        if (!BTSLUtil.isNullString(channelUserReportVO.getTransferInOrOut())) {
            if (channelUserReportVO.getTransferInOrOut().equalsIgnoreCase(PretupsI.ALL)) {
            	String allMsg = RestAPIStringParser.getMessage(locale,
    					PretupsErrorCodesI.ALL_LIST, null);
    		
                channelUserReportVO.setTransferInOrOutName(allMsg);
            } else if (channelUserReportVO.getTransferInOrOut().equalsIgnoreCase(PretupsI.IN)) {
            	String inMsg = RestAPIStringParser.getMessage(locale,
    					PretupsErrorCodesI.C2S_REPORTS_O2C_AND_C2C_RETURN_WITHDRAW_COMBO_IN, null);
                channelUserReportVO.setTransferInOrOutName(inMsg);
            } else if (channelUserReportVO.getTransferInOrOut().equalsIgnoreCase(PretupsI.OUT)) {
            	String outMsg = RestAPIStringParser.getMessage(locale,
    					PretupsErrorCodesI.C2S_REPORTS_O2C_AND_C2C_RETURN_WITHDRAW_COMBO_OUT, null);
            	
                 channelUserReportVO.setTransferInOrOutName(outMsg);
            }
        }
        if (channelUserReportVO.getZoneCode().equals(PretupsI.ALL)) {
          String allMsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.ALL_LIST, null);
		
            channelUserReportVO.setZoneName(allMsg);
        } else {
            listValueVO = BTSLUtil.getOptionDesc(channelUserReportVO.getZoneCode(), channelUserReportVO.getZoneList());
            channelUserReportVO.setZoneName(listValueVO.getLabel());
        }

        if (channelUserReportVO.getFromtransferCategoryCode().equals(PretupsI.ALL)) {
          String allMsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.ALL_LIST, null);
		
            channelUserReportVO.setFromtransferCategoryName(allMsg);
        } else {
            listValueVO = BTSLUtil.getOptionDesc(channelUserReportVO.getFromtransferCategoryCode(), channelUserReportVO.getFromCategoryList());
            channelUserReportVO.setFromtransferCategoryName(listValueVO.getLabel());

        }
    }


}

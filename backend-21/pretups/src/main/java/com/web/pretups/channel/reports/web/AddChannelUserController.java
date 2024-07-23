package com.web.pretups.channel.reports.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.CommonController;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.TransferProfileDAO;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryGradeDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.roles.businesslogic.UserRolesDAO;
import com.btsl.pretups.roles.businesslogic.UserRolesVO;
import com.btsl.pretups.routing.subscribermgmt.businesslogic.NumberPortDAO;
import com.btsl.pretups.servicekeyword.businesslogic.ServicesTypeDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.channel.reports.service.AddChannelUserService;
import com.web.pretups.master.businesslogic.GeographicalDomainWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;
import com.web.user.web.UserModel;

/**
 * @author tarun.kumar
 *
 */
@Controller
public class AddChannelUserController extends CommonController {

	
	private static final String PANEL_NO = "PanelNo";
	
	private static final String MODEL_KEY = "usersReportModel";
	
    private static final String FAIL_KEY    = "fail";    
	
	private static final String FIRST_SCREEN ="user/selectChannelCategoryView";
	
	private static final String SCREEN ="user/selectChannelCategoryView";
	
	private static final String LOGIN_ID = "loginId";

	private static final String USER_ID = "userId";
	
	private static final String MSG_WHEN_NO_CAT_SEL4_USER_SEARCH = "Select Channel Category";
	
   private static final String MSG_WHEN_NO_ZONE_SEL4_USER_SEARCH = "Select Zone First";
	
	private static final String MSG_WHEN_NO_DOMAIN_SEL4_USER_SEARCH = "Select Domain First";

	@Autowired
	private AddChannelUserService addChannelUserService;
	
	/**
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/pretups/userChannelCategoryAction.form", method = RequestMethod.GET)
	public String loadAddChannelUserForm(final Model model,HttpServletRequest request, HttpServletResponse response){
		if (log.isDebugEnabled()) {
			log.debug("AddChannelUserController#loadAddChannelUserForm",PretupsI.ENTERED);
		}
		final String methodName ="loadAddChannelUserForm";
		request.getSession().removeAttribute(PANEL_NO);
		request.getSession().removeAttribute(MODEL_KEY);
		request.getSession().removeAttribute("usersReport");
		
		UsersReportModel usersReportModel = new UsersReportModel();
		UserVO userVO = null;
		try {
			authorise(request, response, "ADDCUSR001", false);		
			userVO = this.getUserFormSession(request);									
			addChannelUserService.loadDomainList(request,response, usersReportModel, userVO, model);
			usersReportModel.setSuccessFlag(false);
			//usersReportModel.setSuccessMsg("false");
		} catch (IOException | BTSLBaseException | ServletException e) {
			 log.errorTrace(methodName, e);
		}
			model.addAttribute(PANEL_NO, "Panel-One");
			model.addAttribute(MODEL_KEY, usersReportModel);
			model.addAttribute("user","false");
			request.getSession().setAttribute(MODEL_KEY, usersReportModel);
			request.getSession().setAttribute("usersReport", usersReportModel);
		if (log.isDebugEnabled()) {
			
			log.debug(	"AddChannelUserController#loadAddChannelUserForm",PretupsI.EXITED);
		}
		return FIRST_SCREEN;
	}
	
	
	/**
	 * @param categoryCode
	 * @param domainCode
	 * @param seqId
	 * @param userModel
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/pretups/loadParentCategoryList.form", method = RequestMethod.GET)
	public @ResponseBody List<CategoryVO> loadParentCategoryList(@RequestParam(value="categoryCode",required=true)String categoryCode,
			@RequestParam(value="domainCode",required=true)String domainCode,
			@RequestParam(value="seqId",required=true)String seqId,
			@ModelAttribute("userModel") UserModel userModel,HttpServletRequest request, HttpServletResponse response){
		final String methodName = "#loadParentCategoryList";
		enteredMethod();
		Connection con = null;
        MComConnectionI mcomCon = null;
       
        final UserDAO userDAO = new UserDAO();
        UsersReportModel usersReportModel = (UsersReportModel) request.getSession().getAttribute("usersReport");
		userModel.setDomainCode(domainCode);   				
		ArrayList<CategoryVO> list = new ArrayList<>();
        try {   
        	mcomCon = new MComConnection();   		
			con=mcomCon.getConnection();
            final ChannelUserVO channelUserSessionVO = (ChannelUserVO) getUserFormSession(request);
            final String categoryID =seqId;                       
            if (usersReportModel.getOrigParentCategoryList() != null && !BTSLUtil.isNullString(categoryCode)) {
                CategoryVO categoryVO ;
                ChannelTransferRuleVO channelTransferRuleVO ;
                for (int i = 0, j = usersReportModel.getOrigCategoryList().size(); i < j; i++) {
                    categoryVO = (CategoryVO) usersReportModel.getCatList().get(i);
                    /*
                     * If Sequence No == 1 means root owner is adding(suppose
                     * Distributor)
                     * at this time pagentCategory and category both will be
                     * same, just add
                     * the categoryVO into the parentCategoryList
                     */
                    if ("1".equals(categoryID) && categoryCode.equals(categoryVO.getCategoryCode())) {
                        list = new ArrayList<>();
                        list.add(categoryVO);
                        break;
                    }
                    /*
                     * In Case of channel admin No need to check the sequence
                     * number
                     * In Case of channel user we need to check the sequence
                     * number
                     */
                    if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO.getDomainID())) {
                        for (int m = 0, n = usersReportModel.getOrigParentCategoryList().size(); m < n; m++) {
                            channelTransferRuleVO = (ChannelTransferRuleVO) usersReportModel.getOrigParentCategoryList().get(m);
                            /*
                             * Here three checks are checking
                             * Add those category into the list where
                             * a)FormCategory(origPatentList) =
                             * categoryCode(origcategoryList)
                             * b)selectedCategory(categoryID[0] =
                             * ToCategory(origParentCategoryList)
                             * c)selectedCategory(categoryID[0] !=
                             * FromCategory(origParentCategoryList)
                             */
                            checkCategoryCode(categoryCode, list, categoryVO,channelTransferRuleVO);
                        }
                    } else {
                        for (int m = 0, n = usersReportModel.getOrigParentCategoryList().size(); m < n; m++) {
                            channelTransferRuleVO = (ChannelTransferRuleVO) usersReportModel.getOrigParentCategoryList().get(m);
                            /*
                             * Here three checks are checking
                             * Add those category into the list where
                             * a)FormCategory(origPatentList) =
                             * categoryCode(origcategoryList)
                             * b)selectedCategory(categoryID[0] =
                             * ToCategory(origParentCategoryList)
                             * c)selectedCategory(categoryID[0] !=
                             * FromCategory(origParentCategoryList)
                             */

                            if (categoryVO.getCategoryCode().equals(channelTransferRuleVO.getFromCategory()) && categoryCode.equals(channelTransferRuleVO.getToCategory()) && !categoryCode.equals(channelTransferRuleVO.getFromCategory())) {
                                checkSecqNo(list, channelUserSessionVO,categoryVO);
                            }
                        }
                    }
                }
                emptyList(usersReportModel, list);
            }


            if (usersReportModel.getCatList() != null) {//getOrigCategoryList()
                CategoryVO vo = null;
                // parentID is the combination of categoryCode, Domain Code
                // and sequenceNo
                usersReportModel.setCategoryCode(categoryCode);
                for (int i = 0, j = usersReportModel.getCatList().size(); i < j; i++) {
                    vo =  (CategoryVO) usersReportModel.getCatList().get(i);

                    if (vo.getCategoryCode().equalsIgnoreCase(categoryCode)) {
                    	usersReportModel.setCategoryVO(vo);
                        break;
                    }
                }
            }
            usersReportModel.setParentCategoryList(list);
            if (usersReportModel.getMsisdnList() != null && usersReportModel.getMsisdnList().size() > 0) {
                 usersReportModel.getMsisdnList().clear();
            }
           
            
            request.getSession().setAttribute("usersReport", usersReportModel);
        } catch (BTSLBaseException | SQLException  e) {
            log.errorTrace(methodName, e);
        }finally {
			if (mcomCon != null) {
				mcomCon.close("Add Channel User#loadParentCategoryList");
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting");
            }
        }
        exitMethod(methodName);       
		return list;
	}


	private void emptyList(UsersReportModel usersReportModel,
			ArrayList<CategoryVO> list) {
		if (list.isEmpty()) {
			usersReportModel.setParentCategoryList(list);
		    new BTSLMessages("user.selectchannelcategory.msg.notransferruledefined", "SelectCategoryForAdd");
		}
	}


	private void exitMethod(final String methodName) {
		if (log.isDebugEnabled()) {
            log.debug(methodName, "Exiting");
        }
	}


	private void enteredMethod() {
		if (log.isDebugEnabled()) {
			log.debug("AddChannelUserController#loadParentCategoryList",PretupsI.ENTERED);
		}
	}


	private void checkCategoryCode(String categoryCode,
			ArrayList<CategoryVO> list, CategoryVO categoryVO,
			ChannelTransferRuleVO channelTransferRuleVO) {
		if (categoryVO.getCategoryCode().equals(channelTransferRuleVO.getFromCategory()) && categoryCode.equals(channelTransferRuleVO.getToCategory()) && !categoryCode.equals(channelTransferRuleVO.getFromCategory())) {
		    list.add(categoryVO);
		}
	}


	private void checkSecqNo(ArrayList<CategoryVO> list,
			final ChannelUserVO channelUserSessionVO, CategoryVO categoryVO) {
		if (categoryVO.getSequenceNumber() >= channelUserSessionVO.getCategoryVO().getSequenceNumber()) {
		    list.add(categoryVO);
		}
	}
	/**
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/pretups/getAddChannelUserList.form", method = RequestMethod.GET)
	public @ResponseBody List<Map<String, String>> loadAddChannelUserList(Model model,HttpServletRequest request, HttpServletResponse response){
		
				 
		final String methodName ="loadAddChannelUserList";
        UserVO userVO = null;		
		try {
			userVO = this.getUserFormSession(request);
		} catch (BTSLBaseException e) {
			 log.errorTrace(methodName, e);
		}
		
		List<Map<String, String>> list = new ArrayList<>();
		String userName = request.getParameter("query");
		userName = userName + "%";		
		String zoneCode = request.getParameter("zoneCode");
		String domainCode = request.getParameter("domainCode");
		String parentCategoryCode = request.getParameter("categorycode");
		
		if(BTSLUtil.isNullString(zoneCode)){
			 Map<String, String> map = new HashMap<>();
				map.put(LOGIN_ID, MSG_WHEN_NO_ZONE_SEL4_USER_SEARCH);
				map.put(USER_ID, "");
				list.add(map);
				return list;
		 }
		if(BTSLUtil.isNullString(domainCode)){
			 Map<String, String> map = new HashMap<>();
				map.put(LOGIN_ID, MSG_WHEN_NO_DOMAIN_SEL4_USER_SEARCH);
				map.put(USER_ID, "");
				list.add(map);
				return list;
		 }
		if(BTSLUtil.isNullString(parentCategoryCode)){   
			 Map<String, String> map = new HashMap<>();
				map.put(LOGIN_ID, MSG_WHEN_NO_CAT_SEL4_USER_SEARCH);
				map.put(USER_ID, "");
				list.add(map);
				return list;
		 }else if (TypesI.ALL.equalsIgnoreCase(parentCategoryCode)){
			    Map<String, String> map = new HashMap<>();
			    map.put(LOGIN_ID, TypesI.ALL);
				map.put(USER_ID, TypesI.ALL);
				list.add(map);
				return list;
		 }		  
		 List<ListValueVO> userList = addChannelUserService.getAddChannelUserList(userVO, zoneCode, domainCode,userName,parentCategoryCode,request,response);
		if (userList.isEmpty()) {
			model.addAttribute(	FAIL_KEY,PretupsRestUtil.getMessageString("c2s.reports.msg.datanotfound"));
		}

		Iterator<ListValueVO> itr = userList.iterator();
		
		if (userList.isEmpty()) {
			Map<String, String> map = new HashMap<>();
			map.put(LOGIN_ID, "NO DATA FOUND");
			list.add(map);
		}
		while (itr.hasNext()) {
			ListValueVO object = itr.next();
			Map<String, String> map = new HashMap<>();
			String loginId = object.getLabel()+":"+object.getValue();
			map.put(LOGIN_ID, loginId);
			map.put(USER_ID, object.getLabel());
			list.add(map);
		}

				
		return list;

	}
	
	/**
	 * @param primarynumber
	 * @param number
	 * @param smspin
	 * @param confirmsmspin
	 * @param profile
	 * @param description
	 * @param userModel
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("null")
	@RequestMapping(value = "/pretups/assignPhone.form", method = RequestMethod.GET)
	public @ResponseBody Map<String,String> assignPhone(@RequestParam(value="primarynumber",required=true)String primarynumber,
			@RequestParam(value="number",required=true)String number,
			@RequestParam(value="smspin",required=true)String smspin,
			@RequestParam(value="confirmsmspin",required=true)String confirmsmspin,
			@RequestParam(value="profile",required=true)String profile,
			@RequestParam(value="description",required=true)String description,
			@ModelAttribute("userModel") UserModel userModel,Model model,HttpServletRequest request, HttpServletResponse response){
		
		final String methodName = "#assignPhone";
		
        if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
        }
		Connection con = null;
        MComConnectionI mcomCon = null;      
        OperatorUtilI operatorUtili = null;               

		UsersReportModel usersReportModel = (UsersReportModel) request.getSession().getAttribute("usersReport");		
		List<UserPhoneVO> list=new ArrayList<>();
		Map<String, String> responseMap=new HashMap<>();
		try {   
        	mcomCon = new MComConnection();   		
			con=mcomCon.getConnection();
			          
            final UserVO userVO = getUserFormSession(request);

            /*
             * 1)set the primaryRadio button into the msisdn list
             * This primaryRadio varaible will give the index of the row
             * that the user have selected(Index of the Primary Number row)
             * so we set the primaryNumber = "Y" into the indexed position
             * of the list.
             * 2)Check where primary Number = Y MSISDN can't be blank
             * 3)Check msisdn belongs to same network or not
             * 4)Check the MSISDN is already assigned to another user or not
             * 5)Check the Duplicate entry of the MSISDN in the list
             * 6)If SMS PIN changed by the user than set it into the list(from
             * the showSmsPin to smsPin)
             * 7)check pin exist from
             */
            int rowIndex = -1;// returns the row number where primaryNumbey = Y
            if (usersReportModel.getMsisdnList() != null) {
                
                final UserDAO userDAO = new UserDAO();
                final HashMap<String, String> mp = new HashMap<>();
                NetworkPrefixVO prefixVO = null;
                UserPhoneVO phoneVO = null;
                ListValueVO listVO = null;
                String randomPin = null;
              
                final String []pprimarynumber= primarynumber.split("\\:");
                final String []pnumber= number.split("\\:");
                final String []psmspin= smspin.split("\\:");
                final String []pconfirmsmspin= confirmsmspin.split("\\:");              
                final String []pdescription= description.split("\\:");
                final String []pprofile= profile.split("\\:");
                
                for (int i = 0, j = usersReportModel.getMsisdnList().size(); i < j; i++) {
                    phoneVO =  usersReportModel.getMsisdnList().get(i);
                    phoneVO.setMsisdn(pnumber[i]);
                    phoneVO.setSmsPin(psmspin[i]);
                    phoneVO.setConfirmSmsPin(pconfirmsmspin[i]);                                      
                    phoneVO.setPhoneProfile(pprofile[0]);
                   
                    if(pdescription.length>0){
                    	phoneVO.setDescription(pdescription[i]);
                    }                                      
                    // set the phoneProfile dropdown description
                    if (!BTSLUtil.isNullString(phoneVO.getPhoneProfile())) {
                        listVO = BTSLUtil.getOptionDesc(phoneVO.getPhoneProfile(), usersReportModel.getPhoneProfileList());
                        phoneVO.setPhoneProfileDesc(listVO.getLabel());
                    }

                    /*if (usersReportModel.getPrimaryRadio() != null && usersReportModel.getPrimaryRadio().trim().length() > 0 && i == Integer.parseInt(usersReportModel.getPrimaryRadio())) {
                        phoneVO.setPrimaryNumber("Y");

                        // 2
                        if (phoneVO.getMsisdn() == null || phoneVO.getMsisdn().trim().length() == 0) {
                            rowIndex = i;
                        }
                    } else {
                        phoneVO.setPrimaryNumber("N");
                    }*/
                    
                    if("true".equalsIgnoreCase(pprimarynumber[i])) {
                       phoneVO.setPrimaryNumber("Y");
                   
	                } else {
	                    phoneVO.setPrimaryNumber("N");
	                }
                    if (!BTSLUtil.isNullString(phoneVO.getMsisdn())) {
                        final String[] arr = { phoneVO.getMsisdn() };
                        final String filterMsisdn = PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn());
                        // 3
                        prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(filterMsisdn));
                        if (prefixVO == null || !prefixVO.getNetworkCode().equals(userVO.getNetworkID())) {
                            final String[] arr1 = { phoneVO.getMsisdn(), userVO.getNetworkName() };
                            log.error(methodName, "Error: MSISDN Number" + phoneVO.getMsisdn() + " not belongs to " + userVO.getNetworkName() + "network");
                             responseMap.put(FAIL_KEY, PretupsRestUtil.getMessageString("user.assignphone.error.msisdnnotinsamenetwork", arr1));
                             return responseMap;
                        }
                        /*
                         *  Code Added for MNP
                         * Preference to check whether MNP is allowed in system
                         * or not.
                         * If yes then check whether Number has not been ported
                         * out, If yes then throw error, else continue
                         */
                        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MNP_ALLOWED))).booleanValue()) {
                            boolean numberAllowed = false;
                            if (prefixVO.getOperator().equals(PretupsI.OPERATOR_TYPE_PORT)) {
                                numberAllowed = new NumberPortDAO().isExists(con, filterMsisdn, "", PretupsI.PORTED_IN);
                                if (!numberAllowed) {
                                    
                                	responseMap.put(FAIL_KEY, PretupsRestUtil.getMessageString("user.assignphone.error.msisdnnotinsamenetwork", new String[] { filterMsisdn, userVO.getNetworkName() }));
                                    return responseMap;
                                }
                            } else {
                                numberAllowed = new NumberPortDAO().isExists(con, filterMsisdn, "", PretupsI.PORTED_OUT);
                                if (numberAllowed) {
                                    
                                    responseMap.put(FAIL_KEY, PretupsRestUtil.getMessageString("user.assignphone.error.msisdnnotinsamenetwork", new String[] { filterMsisdn, userVO.getNetworkName() }));
                                    return responseMap;
                                }
                            }
                        }
                        //  MNP Code End
                      
                        if (userDAO.isMSISDNExist(con, PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn()), phoneVO.getUserId())) {
                            log.error(methodName, "Error: MSISDN Number is already assigned to another user");
                           
                            responseMap.put(FAIL_KEY, PretupsRestUtil.getMessageString("user.assignphone.error.msisdnallreadyexist", arr));
                            return responseMap;
                        }

                      
                        if (mp.containsKey(PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn()))) {
                            log.error(methodName, "Error: Duplicate entry of the MSISDN Number in the list");
                          
                            responseMap.put(FAIL_KEY, PretupsRestUtil.getMessageString("user.assignphone.error.duplicatemsisdn", arr));
                            return responseMap;
                        } else {
                            mp.put(PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn()), PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn()));
                        }
                    }
                    // generate random pin for user
                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.AUTO_PIN_GENERATE_ALLOW))).booleanValue()) {
                        if ("edit".equalsIgnoreCase(usersReportModel.getRequestType()) && phoneVO.getPinGenerateAllow().equals(PretupsI.YES)) {
                            randomPin = operatorUtili.generateRandomPin();
                            phoneVO.setShowSmsPin(randomPin);
                            phoneVO.setConfirmSmsPin(randomPin);
                        } else if ((!BTSLUtil.isNullString(phoneVO.getMsisdn())) && (!"edit".equalsIgnoreCase(usersReportModel.getRequestType()))) {
                            randomPin = operatorUtili.generateRandomPin();
                            phoneVO.setShowSmsPin(randomPin);
                            phoneVO.setConfirmSmsPin(randomPin);
                        } else if ("edit".equalsIgnoreCase(usersReportModel.getRequestType()) && "D".equals(phoneVO.getPinGenerateAllow())) {
                            phoneVO.setShowSmsPin(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_PASSWORD)));
                            phoneVO.setConfirmSmsPin(((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_PASSWORD)));
                        }
                    }
                         
                    if (!BTSLUtil.isNullString(phoneVO.getShowSmsPin())) {
                        /*
                         * First check whether the smspin is already assigned to
                         * the user or not
                         * if not already assigned to the user just set the pin
                         * into the smsPin field
                         * else
                         * check the smspin is changed by the user or not if
                         * changed then update the smsPin field
                         */
                        if (BTSLUtil.isNullString(phoneVO.getSmsPin())) {
                            // while updating encrypt the password
                            final String password = BTSLUtil.encryptText(phoneVO.getShowSmsPin());
                            phoneVO.setSmsPin(password);
                        } else if (!(phoneVO.getShowSmsPin().equals(BTSLUtil.getDefaultPasswordNumeric(BTSLUtil.decryptText(phoneVO.getSmsPin()))))) {
                            // while updating encrypt the password
                            final String password = BTSLUtil.encryptText(phoneVO.getShowSmsPin());
                            if ("edit".equals(usersReportModel.getRequestType())) {
                                if (userDAO.checkPasswordHistory(con, PretupsI.USER_PIN_MANAGEMENT, phoneVO.getUserId(), PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn()),
                                                BTSLUtil.encryptText(phoneVO.getShowSmsPin()))) {
                                    log.error(methodName, "Error: Pin exist in password_history table");
                                                                  
                                    responseMap.put(FAIL_KEY, PretupsRestUtil.getMessageString("channeluser.changepin.error.pinhistory", new String[] { String
                                              .valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.PREV_PIN_NOT_ALLOW))).intValue()), PretupsBL.getFilteredMSISDN(phoneVO.getMsisdn()) }));
                                    return responseMap;
                                }
                            }
                            phoneVO.setSmsPin(password);
                        }
                    } else {
                        phoneVO.setSmsPin(null);
                    }
                }
            }
            if (rowIndex > -1) {
                final String[] arr = { (rowIndex + 1) + "" };
                log.error(methodName, "Error: MSISDN Number is empty where primary Number is 'Y'(Row where Radio button checked)");
              
                responseMap.put(FAIL_KEY, PretupsRestUtil.getMessageString("user.asignPhone.error.primarynumber", arr));
              return responseMap;
            }

            usersReportModel.setMsisdnList(usersReportModel.getMsisdnList());              
            request.getSession().setAttribute("usersReport", usersReportModel);
            list.addAll(usersReportModel.getMsisdnList());
        } catch (BTSLBaseException | SQLException e) {
        	 log.errorTrace(methodName, e);
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserUpdateAction[processUploadedFile]", "", "",
                             "", "Exception while loading the class at the call:" + e.getMessage());
            log.errorTrace(methodName, e);
        }finally {
			if (mcomCon != null) {
				mcomCon.close("Add Channel User#assignPhone");
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting");
            }
        }
        exitMethod(methodName);       

        return responseMap;
	}
	
	/**
	 * @param usrId
	 * @param userModel
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/pretups/assignGeographies.form", method = RequestMethod.GET)
	public @ResponseBody List<UserGeographiesVO> assignGeographies(@RequestParam(value="userId",required=true)String usrId,@RequestParam(value="parentDomainCode",required=true)String parentDomainCode,
			
			@ModelAttribute("userModel") UserModel userModel,HttpServletRequest request, HttpServletResponse response){
		
		    final String methodName = "#assignGeographies";
		
             if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
           }
			Connection con = null;
	        MComConnectionI mcomCon = null;      
			UsersReportModel usersReportModel = (UsersReportModel) request.getSession().getAttribute("usersReport");		
			usersReportModel.setParentDomainCode(parentDomainCode);
			List<UserGeographiesVO> list=new ArrayList<>();
		try {   
        	mcomCon = new MComConnection();   		
			con=mcomCon.getConnection();						                       
            final GeographicalDomainDAO geographyDAO = new GeographicalDomainDAO();
            final GeographicalDomainWebDAO geographicalDomainWebDAO = new GeographicalDomainWebDAO();
            final UserVO userSessionVO = getUserFormSession(request);          
            List<UserGeographiesVO> geographyList = null;
           
            if (usersReportModel.getCategoryVO().getGrphDomainSequenceNo() == 1) {
                UserGeographiesVO geographyVO = null;
                geographyList = new ArrayList<>();
                geographyVO = new UserGeographiesVO();
                geographyVO.setGraphDomainCode(userSessionVO.getNetworkID());
                geographyVO.setGraphDomainName(userSessionVO.getNetworkName());
                geographyVO.setGraphDomainTypeName(userSessionVO.getCategoryVO().getGrphDomainTypeName());
                usersReportModel.setGrphDomainTypeName(userSessionVO.getCategoryVO().getGrphDomainTypeName());
                geographyList.add(geographyVO);
                usersReportModel.setGeographicalList(geographyList);
            } // 2
           // else if (usersReportModel.getSearchList() == null || usersReportModel.getSearchList().isEmpty()) {
            else if (usrId == null || usrId.isEmpty()) {
            // a
                if (userSessionVO.getCategoryVO().getGrphDomainType().equals(usersReportModel.getCategoryVO().getGrphDomainType())) {
                    geographyList = userSessionVO.getGeographicalAreaList();
                    usersReportModel.setGeographicalList(geographyList);
                    if (geographyList != null && geographyList.size() > 0) {
                        /*
                         * set the grphDoaminTypeName on the form
                         * GrphDomainTypeName is same for all VO's in list
                         */
                        final UserGeographiesVO geographyVO = (UserGeographiesVO) geographyList.get(0);
                        usersReportModel.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());
                    }
                }
                // b
                else if ((userSessionVO.getCategoryVO().getGrphDomainSequenceNo() + 1) == usersReportModel.getCategoryVO().getGrphDomainSequenceNo()) {
                    geographyList = geographicalDomainWebDAO.loadGeographyList(con, userSessionVO.getNetworkID(), usersReportModel.getParentDomainCode(), "%");
                    usersReportModel.setGeographicalList(geographyList);
                    if (geographyList != null && geographyList.size() > 0) {
                        /*
                         * set the grphDoaminTypeName on the form
                         * GrphDomainTypeName is same for all VO's in list
                         */
                        final UserGeographiesVO geographyVO = (UserGeographiesVO) geographyList.get(0);
                        usersReportModel.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());
                    }
                }
                // c
                else {
                    ArrayList arrayList = geographicalDomainWebDAO.loadDomainTypes(con, userSessionVO.getCategoryVO().getGrphDomainSequenceNo(), usersReportModel.getCategoryVO()
                                    .getGrphDomainSequenceNo());


                }
            } 
            
            
            else {
                String parentId = usrId;                                                           
                final List<UserGeographiesVO> parentUserGeographyList = geographyDAO.loadUserGeographyList(con, parentId, userSessionVO.getNetworkID());

                UserGeographiesVO geographyVO = null;
                if (parentUserGeographyList != null && parentUserGeographyList.size() > 0) {
                    for (int i = 0, j = parentUserGeographyList.size(); i < j; i++) {
                        geographyVO =  parentUserGeographyList.get(i);
                       
                        if (geographyVO.getGraphDomainCode().equals(usersReportModel.getParentDomainCode())) {
                            break;
                        }
                    }
                    if (geographyVO.getGraphDomainType().equals(usersReportModel.getCategoryVO().getGrphDomainType())) {
                    	usersReportModel.setGeographicalList(parentUserGeographyList);                       
                    	usersReportModel.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());

                    }else if ((geographyVO.getGraphDomainSequenceNumber() + 1) == usersReportModel.getCategoryVO().getGrphDomainSequenceNo()) {
                        geographyList = geographicalDomainWebDAO.loadGeographyList(con, userSessionVO.getNetworkID(), geographyVO.getGraphDomainCode(), "%");
                        usersReportModel.setGeographicalList(geographyList);
                        if (geographyList != null && geographyList.size() > 0) {                          
                            geographyVO =  geographyList.get(0);
                            usersReportModel.setGrphDomainTypeName(geographyVO.getGraphDomainTypeName());
                        }
                    }else {
                        final ArrayList arlist = geographicalDomainWebDAO.loadDomainTypes(con, geographyVO.getGraphDomainSequenceNumber(), usersReportModel.getCategoryVO()
                                .getGrphDomainSequenceNo());

			                if (arlist != null && arlist.size() > 0) {
			                    //theForm.setParentDomainCode(geographyVO.getGraphDomainCode());
			                    //theForm.setDomainSearchList(list);
			                    //theForm.setSearchDomainTextArrayCount();
			                    //theForm.setSearchDomainCodeCount();
			                }
                    }
                    
                    //
                }else {
                    //final BTSLMessages btslMessage = new BTSLMessages("user.assigngeography.error.parentgeographynotexist", "AssignGeography");
                    //return super.handleMessage(btslMessage, request, mapping);
                }
            }
            if ("add".equals(usersReportModel.getRequestType()) && usersReportModel.getGeographicalList().size() > 0) {
                final UserGeographiesVO geographyVO =  usersReportModel.getGeographicalList().get(0);
                usersReportModel.setGeographicalCode(geographyVO.getGraphDomainCode());
            }
            
            request.getSession().setAttribute("usersReport", usersReportModel);
            list.addAll(usersReportModel.getGeographicalList());
        } catch (BTSLBaseException | SQLException e) {
        	 log.errorTrace(methodName, e);
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserUpdateAction[processUploadedFile]", "", "",
                             "", "Exception while loading the class at the call:" + e.getMessage());
            log.errorTrace(methodName, e);
        }finally {
			if (mcomCon != null) {
				mcomCon.close("Add Channel User#assignGeographies");
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting");
            }
        }
        exitMethod(methodName);       
		return list;
	}
	
	
	
	/**
	 * @param geographicalCode
	 * @param userModel
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/pretups/addGeographies.form", method = RequestMethod.GET)
	public @ResponseBody List<CommissionProfileSetVO> addGeographies(@RequestParam(value="geographicalCode",required=true)String geographicalCode,
			
			@ModelAttribute("userModel") UserModel userModel,HttpServletRequest request, HttpServletResponse response){		
		    final String methodName = "#addGeographies";
		
             if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
           }
			Connection con = null;
	        MComConnectionI mcomCon = null; 

            final UserWebDAO userwebDAO = new UserWebDAO();                       
			UsersReportModel usersReportModel = (UsersReportModel) request.getSession().getAttribute("usersReport");		
			List<CommissionProfileSetVO> list=new ArrayList<>();
			
		try {   
        	mcomCon = new MComConnection();   		
			con=mcomCon.getConnection();						                       
			final ChannelUserVO userChannelSessionVO = (ChannelUserVO) getUserFormSession(request);
			usersReportModel.setCommissionProfileList(userwebDAO.loadCommisionProfileListByCategoryIDandGeography(con, usersReportModel.getCategoryCode(), userChannelSessionVO.getNetworkID(),
					geographicalCode));
			
            request.getSession().setAttribute("usersReport", usersReportModel);
            list.addAll(usersReportModel.getCommissionProfileList());
        } catch (BTSLBaseException | SQLException e) {
        	 log.errorTrace(methodName, e);
             EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BatchUserUpdateAction[processUploadedFile]", "", "",
                             "", "Exception while loading the class at the call:" + e.getMessage());
            log.errorTrace(methodName, e);
        }finally {
			if (mcomCon != null) {
				mcomCon.close("Add Channel User#addGeographies");
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting");
            }
        }
        exitMethod(methodName);       
		return list;
	}
	
	/**
	 * @param roleType
	 * @param userModel
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/pretups/assignRoles.form", method = RequestMethod.GET)
	public @ResponseBody Map<String, List> assignRoles(@RequestParam(value="roleType",required=true)String roleType,
			
			@ModelAttribute("userModel") UserModel userModel,HttpServletRequest request, HttpServletResponse response){		
		    final String methodName = "#assignRoles";
		
             if (log.isDebugEnabled()) {
            log.debug(methodName, "Entered");
           }
			Connection con = null;
	        MComConnectionI mcomCon = null; 

			UsersReportModel usersReportModel = (UsersReportModel) request.getSession().getAttribute("usersReport");		
			Map<String, List> rolesMapNew=new HashMap<>(); 
			
		try {   
        	mcomCon = new MComConnection();   		
			con=mcomCon.getConnection();						                       
			final UserRolesDAO rolesDAO = new UserRolesDAO();
           
            Map<?, ?> rolesMap =rolesDAO.loadRolesListByGroupRole(con, usersReportModel.getCategoryCode(), roleType);
           			
            Set<?> rolesKeys = rolesMap.keySet();
            List<UserRolesVO> rolesListNew=new ArrayList<>();
            Iterator<?> keyiter = rolesKeys.iterator();
            while(keyiter.hasNext()){
            
                  String rolename=(String)keyiter.next();
                  List<UserRolesVO> rolesVOList=(List<UserRolesVO>) rolesMap.get(rolename);
                  rolesListNew=new ArrayList<>();
                  Iterator<UserRolesVO> i=rolesVOList.iterator();
                  while(i.hasNext()){
                        UserRolesVO rolesVO=i.next();
                        if("Y".equalsIgnoreCase(rolesVO.getStatus())){
                        
                              rolesListNew.add(rolesVO);
                        }
                  }
                 
                  if(!rolesListNew.isEmpty()){
                	  rolesMapNew.put(rolename, rolesListNew);
                  }
            }
            usersReportModel.setRolesMap(rolesMapNew);  
			
            request.getSession().setAttribute("usersReport", usersReportModel);

        } catch (BTSLBaseException | SQLException e) {
        	 log.errorTrace(methodName, e);
            
           
        }finally {
			if (mcomCon != null) {
				mcomCon.close("Add Channel User#assignRoles");
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting");
            }
        }
        exitMethod(methodName);       
		return rolesMapNew;
	}
	
	/**
	 * @param roleFlag
	 * @param userModel
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/pretups/addRoles.form", method = RequestMethod.GET)
	public @ResponseBody Map<String, List> addRoles(@RequestParam(value="roles[]",required=true)String []roleFlag,
			
			@ModelAttribute("userModel") UserModel userModel,HttpServletRequest request, HttpServletResponse response){		
		    final String methodName = "#addRoles";
		
             if (log.isDebugEnabled()) {
                  log.debug(methodName, "Entered");
               }
			 
			UsersReportModel usersReportModel = (UsersReportModel) request.getSession().getAttribute("usersReport");
			usersReportModel.setRoleFlag(roleFlag);			
			Map<String, List> rolesMapNew=new HashMap<>(); 
			final Map<String, List> mp = usersReportModel.getRolesMap();
	        final Map<String, List> newSelectedMap = new HashMap<>();
	        final Iterator it = mp.entrySet().iterator();
	        String key = null;
	        List list = null;
	        List listNew = null;
	        UserRolesVO roleVO = null;
	        Map.Entry pairs = null;
	        boolean foundFlag = false;
		try {          					                                
			
	        while (it.hasNext()) {
	            pairs = (Map.Entry) it.next();
	            key = (String) pairs.getKey();
	            list = new ArrayList((ArrayList) pairs.getValue());
	            listNew = new ArrayList();
	            foundFlag = false;
	            if (list != null) {
	                for (int i = 0, j = list.size(); i < j; i++) {
	                    roleVO = (UserRolesVO) list.get(i);
	                    if (roleFlag != null && roleFlag.length > 0) {
	                        for (int k = 0; k < roleFlag.length; k++) {
	                            if (roleVO.getRoleCode().equals(roleFlag[k])) {
	                                listNew.add(roleVO);
	                                foundFlag = true;	                               
	                                usersReportModel.setRoleType(roleVO.getGroupRole());
	                            }
	                        }
	                    }
	                }
	            }
	            if (foundFlag) {
	                newSelectedMap.put(key, listNew);
	            }
	        }
	        if (newSelectedMap.size() > 0) {
	        	usersReportModel.setRolesMapSelected(newSelectedMap);
	        } else {
	            // by default set Role Type = N(means System Role radio button will
	            // be checked in edit mode if no role assigned yet)
	            if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_USER_ROLE_TYPE_DISPLAY)).equalsIgnoreCase(PretupsI.SYSTEM)) {
	            	usersReportModel.setRoleType("N");
	            } else if (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_USER_ROLE_TYPE_DISPLAY)).equalsIgnoreCase(PretupsI.GROUP)) {
	            	usersReportModel.setRoleType("Y");

	            } else {
	            	usersReportModel.setRoleType("N");
	            }
	            usersReportModel.setRolesMapSelected(null);
	        }
		
            request.getSession().setAttribute("usersReport", usersReportModel);

        } catch (Exception e) {
        	 log.errorTrace(methodName, e);
            
           
        }
        exitMethod(methodName);       
		return rolesMapNew;
	}
	
	
	/**
	 * @param roleType
	 * @param userModel
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/pretups/assignServices.form", method = RequestMethod.GET)
	public @ResponseBody List<ListValueVO> assignServices(@RequestParam(value="roleType",required=true)String roleType,
			
			@ModelAttribute("userModel") UserModel userModel,HttpServletRequest request, HttpServletResponse response){
		
		    final String methodName = "#assignServices";
		
             if (log.isDebugEnabled()) {
                 log.debug(methodName, "Entered");
             }
			Connection con = null;
	        MComConnectionI mcomCon = null;      
			UsersReportModel usersReportModel = (UsersReportModel) request.getSession().getAttribute("usersReport");		
			List<ListValueVO> list=new ArrayList<>();
		try {   
        	mcomCon = new MComConnection();   		
			con=mcomCon.getConnection();						                       
           
            final ChannelUserVO channelUserSessionVO = (ChannelUserVO) getUserFormSession(request);
            final ServicesTypeDAO servicesDAO = new ServicesTypeDAO();
           
            usersReportModel.setServicesList(servicesDAO.loadServicesList(con, channelUserSessionVO.getNetworkID(), PretupsI.C2S_MODULE, usersReportModel.getCategoryCode(), false));
                          
            request.getSession().setAttribute("usersReport", usersReportModel);
            list.addAll(usersReportModel.getServicesList());
        } catch (BTSLBaseException | SQLException e) {
        	 log.errorTrace(methodName, e);
            
        }finally {
			if (mcomCon != null) {
				mcomCon.close("Add Channel User#assignServices");
				mcomCon = null;
			}
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Exiting");
            }
        }
        exitMethod(methodName);       
		return list;
	}
	
	/**
	 * @param serviceFlag
	 * @param userModel
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/pretups/addServices.form", method = RequestMethod.GET)
	public @ResponseBody List<ListValueVO> addServices(@RequestParam(value="services[]",required=true)String []serviceFlag,
			
			@ModelAttribute("userModel") UserModel userModel,HttpServletRequest request, HttpServletResponse response){
		
		    final String methodName = "#addServices";
		
             if (log.isDebugEnabled()) {
                 log.debug(methodName, "Entered");
             }
     
			UsersReportModel usersReportModel = (UsersReportModel) request.getSession().getAttribute("usersReport");
			usersReportModel.setServicesTypes(serviceFlag);
			
			List<ListValueVO> list=new ArrayList<>();
			List<ListValueVO> listNew=new ArrayList<>();
			ListValueVO listValueVO=null;
		try {   
						                                           
            list=usersReportModel.getServicesList();
            if (list != null) {
                for (int i = 0, j = list.size(); i < j; i++) {
                	listValueVO=  list.get(i);
                    if (serviceFlag != null && serviceFlag.length > 0) {
                        for (int k = 0; k < serviceFlag.length; k++) {
                            if (listValueVO.getValue().equals(serviceFlag[k])) {
                                listNew.add(listValueVO);

                            }
                        }
                    }
                }
            }
            
            usersReportModel.setServicesListSelected(listNew);
            request.getSession().setAttribute("usersReport", usersReportModel);
            listNew.addAll(usersReportModel.getServicesListSelected());
        } catch (Exception e) {
        	 log.errorTrace(methodName, e);
            
        }finally {
			     
            if (log.isDebugEnabled()) {
                log.debug(methodName, "Add Channel User#addServices ::Exiting");
            }
        }
        exitMethod(methodName);       
		return listNew;
	}
	
	/**
	 * @param usersReportModel
	 * @param bindingResult
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/pretups/addChannelUserSubmit.form", method = RequestMethod.POST)
	public String addChannelUserSubmit(@ModelAttribute("usersReportModel") UsersReportModel usersReportModel,BindingResult bindingResult, final Model model,HttpServletRequest request, HttpServletResponse response){

		if (log.isDebugEnabled()) {
			log.debug("add Channel user Controller#addChannelUserSubmit.form",PretupsI.ENTERED);
		}		
		final String methodName ="addChannelUserSubmit";
		UserVO userVO = null;
		boolean flag=false;
		String successMessage = null;
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_FNAME_LNAME_ALLOWED))).booleanValue()) {
            
            if (!BTSLUtil.isNullString(usersReportModel.getLastName())) {
                
            	successMessage=usersReportModel.getFirstName() + " " + usersReportModel.getLastName();
            } else {
            	successMessage=usersReportModel.getFirstName();
            
            }
        } else {
        	
        	successMessage=usersReportModel.getChannelUserName();
        }
		
	    UsersReportModel usersReportModelNew = (UsersReportModel) request.getSession().getAttribute("usersReport");
		try {
			userVO = this.getUserFormSession(request);
			setusersReportModel(usersReportModelNew,usersReportModel);													
			flag=addChannelUserService.addChannelUserSubmit(request, response, usersReportModel,usersReportModelNew, userVO, model,bindingResult);			    			      													
				
			 if(flag){				 				 
				    request.getSession().removeAttribute(PANEL_NO);
				    request.getSession().removeAttribute(MODEL_KEY);
				    request.getSession().removeAttribute("usersReport");
				    
					UsersReportModel urm = new UsersReportModel();										

					addChannelUserService.loadDomainList(request,response, urm, userVO, model);
					urm.setSuccessFlag(true);
					urm.setSuccessMsg("successMessage");
					model.addAttribute(PANEL_NO, "Panel-One");
					model.addAttribute("user", successMessage);
					model.addAttribute(MODEL_KEY, urm);
					request.getSession().setAttribute(MODEL_KEY, urm);
					request.getSession().setAttribute("usersReport", urm);					
					return FIRST_SCREEN;
			 }else{								    

					model.addAttribute(MODEL_KEY, usersReportModel);
					request.getSession().setAttribute(MODEL_KEY, usersReportModelNew);
					request.getSession().setAttribute("usersReport", usersReportModelNew);
				    return SCREEN;
			 }
				
		} catch ( BTSLBaseException  e) {
			   log.errorTrace(methodName, e);
		}		return FIRST_SCREEN;
	}
	

	private void setusersReportModel(UsersReportModel usersReportModelNew,UsersReportModel usersReportModel) {
		//1
		usersReportModelNew.setDomainCode(usersReportModel.getDomainCode());
		usersReportModelNew.setFromtransferCategoryCode(usersReportModel.getFromtransferCategoryCode());
		usersReportModelNew.setFromtransferParentCategoryCode(usersReportModel.getFromtransferParentCategoryCode());
		usersReportModelNew.setParentDomainCode(usersReportModel.getParentDomainCode());
		usersReportModelNew.setUserName(usersReportModel.getUserName()); 
		//2
		usersReportModelNew.setFirstName(usersReportModel.getFirstName());
		usersReportModelNew.setLastName(usersReportModel.getLastName());
		usersReportModelNew.setShortName(usersReportModel.getShortName());
		usersReportModelNew.setUserNamePrefixCode(usersReportModel.getUserNamePrefixCode());
		usersReportModelNew.setEmpCode(usersReportModel.getEmpCode());
		usersReportModelNew.setExternalCode(usersReportModel.getExternalCode());
		usersReportModelNew.setContactPerson(usersReportModel.getContactPerson());
		usersReportModelNew.setContactNo(usersReportModel.getContactNo());
		usersReportModelNew.setSsn(usersReportModel.getSsn());
		usersReportModelNew.setDesignation(usersReportModel.getDesignation());
		usersReportModelNew.setAddress1(usersReportModel.getAddress1());
		usersReportModelNew.setAddress2(usersReportModel.getAddress2());
		usersReportModelNew.setCity(usersReportModel.getCity());
		usersReportModelNew.setState(usersReportModel.getState());
		usersReportModelNew.setCountry(usersReportModel.getCountry());
		usersReportModelNew.setEmail(usersReportModel.getEmail());
		usersReportModelNew.setCompany(usersReportModel.getCompany());
		usersReportModelNew.setFax(usersReportModel.getFax());
		usersReportModelNew.setAppointmentDate(usersReportModel.getAppointmentDate());
		usersReportModelNew.setUserLanguage(usersReportModel.getUserLanguage());
		usersReportModelNew.setOtherEmail(usersReportModel.getOtherEmail());
		usersReportModelNew.setLongitude(usersReportModel.getLongitude());
		usersReportModelNew.setLatitude(usersReportModel.getLatitude());
		usersReportModelNew.setDocumentType(usersReportModel.getDocumentType());
		usersReportModelNew.setDocumentNo(usersReportModel.getDocumentNo());
		usersReportModelNew.setPaymentType(usersReportModel.getPaymentType());
	    //3
		usersReportModelNew.setWebLoginID(usersReportModel.getWebLoginID());
		usersReportModelNew.setAllowedIPs(usersReportModel.getAllowedIPs());
		usersReportModelNew.setAllowedFormTime(usersReportModel.getAllowedFormTime());
		usersReportModelNew.setAllowedToTime(usersReportModel.getAllowedToTime());
	   //4
		usersReportModelNew.setNumber(usersReportModel.getNumber());
		usersReportModelNew.setProfile(usersReportModel.getProfile());
		usersReportModelNew.setDescription(usersReportModel.getDescription());
		usersReportModelNew.setPrimarynumber(usersReportModel.getPrimarynumber());
		//5
		usersReportModelNew.setGeographicalCode(usersReportModel.getGeographicalCode());
	   //6
		usersReportModelNew.setRoleType(usersReportModel.getRoleType());
	   //7 null
		//8 
		usersReportModelNew.setGradeCode(usersReportModel.getGradeCode());
		usersReportModelNew.setCombinedKey(usersReportModel.getCombinedKey());
		usersReportModelNew.setValue(usersReportModel.getValue());
	}


	@RequestMapping(value = "/pretups/loadGradeAndProfileList.form", method = RequestMethod.GET)
	public @ResponseBody Map<String, UsersReportModel> loadGradeAndProfileLms(@RequestParam(value="categoryCode",required=true)String categoryCode,
			
			@ModelAttribute("userModel") UserModel userModel,HttpServletRequest request, HttpServletResponse response){		
		    final String methodName = "#loadGradeAndProfileLms";
		
             if (log.isDebugEnabled()) {
                  log.debug(methodName, "Entered");
               }
             Connection con = null;
			 MComConnectionI mcomCon = null; 
			 UserWebDAO userwebDAO = null;	
			 ChannelUserWebDAO channelUserWebDAO = null;
			Map<String, UsersReportModel> gradeAndProfileLms=new HashMap<>(); 
			UserVO userVO = null;				
									
		try {
			mcomCon = new MComConnection();			
			con=mcomCon.getConnection();
			userwebDAO = new UserWebDAO();
			channelUserWebDAO = new ChannelUserWebDAO();
			userVO = this.getUserFormSession(request);
			UsersReportModel theForm = (UsersReportModel) request.getSession().getAttribute("usersReport");
			
			if (((Integer)PreferenceCache.getControlPreference(PreferenceI.USER_APPROVAL_LEVEL, userVO.getNetworkID(), theForm.getCategoryCode()))
                    .intValue() == 0 || !((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.APPROVER_CAN_EDIT))).booleanValue()) {

    	
        // load the Commision profile dropdown/*****/
        theForm.setCommissionProfileList(userwebDAO.loadCommisionProfileListByCategoryIDandGeography(con, theForm.getCategoryCode(), userVO.getNetworkID(),null));

        final CategoryGradeDAO categoryGradeDAO = new CategoryGradeDAO();
        // load the User Grade dropdown
        theForm.setUserGradeList(categoryGradeDAO.loadGradeList(con, theForm.getCategoryCode()));

        // load the Transfer Profile dropdown
        final TransferProfileDAO profileDAO = new TransferProfileDAO();
        theForm.setTrannferProfileList(profileDAO.loadTransferProfileByCategoryID(con, userVO.getNetworkID(), theForm.getCategoryCode(),
                        PretupsI.PARENT_PROFILE_ID_USER));
        // load the Transfer Rule Type at User level
        final boolean isTrfRuleTypeAllow = ((Boolean) PreferenceCache.getControlPreference(PreferenceI.TRF_RULE_USER_LEVEL_ALLOW, userVO
                        .getNetworkID(), theForm.getCategoryCode())).booleanValue();
        if (isTrfRuleTypeAllow) {
            theForm.setTrannferRuleTypeList(LookupsCache.loadLookupDropDown(PretupsI.TRANSFER_RULE_AT_USER_LEVEL, true));
        }

        
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_APPL)).booleanValue()) {
            theForm.setLmsProfileList(channelUserWebDAO.getLmsProfileList(con, userVO.getNetworkID()));
        }
    }
    
     request.getSession().setAttribute("usersReport", theForm);
			gradeAndProfileLms.put("data",theForm);
            request.getSession().setAttribute("usersReport", theForm);

        } catch (BTSLBaseException | SQLException e) {
        	 log.errorTrace(methodName, e);
            
           
        }finally {
			if(mcomCon != null){
				mcomCon.close("#loadGradeAndProfileLms");
				mcomCon=null;
			}
			
		}
        exitMethod(methodName);       
		return gradeAndProfileLms;
	}
	
	@RequestMapping(value = "/pretups/loadAssignPhone.form", method = RequestMethod.GET)
	public @ResponseBody Map<String, UsersReportModel> loadAssignPhone(@RequestParam(value="categoryCode",required=true)String categoryCode,
			
			@ModelAttribute("userModel") UserModel userModel,HttpServletRequest request, HttpServletResponse response){		
		    final String methodName = "#loadAssignPhone";
		
             if (log.isDebugEnabled()) {
                  log.debug(methodName, "Entered");
               }
             Connection con = null;
			 MComConnectionI mcomCon = null; 
			 UserWebDAO userwebDAO = null;	
			 ChannelUserWebDAO channelUserWebDAO = null;
			Map<String, UsersReportModel> loadAssignPhone=new HashMap<>(); 
			UserVO userVO = null;				
			 final UserDAO userDAO = new UserDAO();						
		try {
			mcomCon = new MComConnection();			
			con=mcomCon.getConnection();
			userwebDAO = new UserWebDAO();
			channelUserWebDAO = new ChannelUserWebDAO();
			userVO = this.getUserFormSession(request);
			UsersReportModel usersReportModel = (UsersReportModel) request.getSession().getAttribute("usersReport");
			
			   if (usersReportModel.getMsisdnList() != null && usersReportModel.getMsisdnList().size() > 0) {
            UserPhoneVO phoneVO = null;
            final ArrayList<UserPhoneVO> list1 = new ArrayList<>();
            for (int i = 0, j = usersReportModel.getMsisdnList().size(); i < j; i++) {
                phoneVO = new UserPhoneVO((UserPhoneVO) usersReportModel.getMsisdnList().get(i));
                phoneVO.setPinGenerateAllow(PretupsI.NO);
                list1.add(phoneVO);
            }
            usersReportModel.setMsisdnList(list1);
        } else {
        	usersReportModel.setMsisdnList(null);
        }
        UserPhoneVO userPhoneVO = null;


        int length = 1;
        if (usersReportModel.getCategoryVO() != null) {
            length = usersReportModel.getCategoryVO().getMaxTxnMsisdnInt();
        }
        usersReportModel.setPhoneProfileList(userDAO.loadPhoneProfileList(con, usersReportModel.getCategoryVO().getCategoryCode()));
        if ("add".equals(usersReportModel.getRequestType())) {
            if (usersReportModel.getMsisdnList() == null || usersReportModel.getMsisdnList().isEmpty()) {
                final List<UserPhoneVO> list2 = new ArrayList<>();

                for (int i = 0; i < length; i++) {
                    userPhoneVO = new UserPhoneVO();
                    userPhoneVO.setRowIndex(i + 1);
                    list2.add(userPhoneVO);
                }
                usersReportModel.setMsisdnList(list2);
                // set the first radio buuton is checked by default
                usersReportModel.setPrimaryRadio("0");
            }
        } else// request for edit
        {
            // this is the case when no list assigned yet
            if (usersReportModel.getMsisdnList() == null || usersReportModel.getMsisdnList().isEmpty()) {
                final List<UserPhoneVO> list2 = new ArrayList<>();
                for (int i = 0; i < length; i++) {
                    userPhoneVO = new UserPhoneVO();
                    userPhoneVO.setRowIndex(i + 1);
                    list2.add(userPhoneVO);
                }
                usersReportModel.setMsisdnList(list2);
                // set the first radio buuton is checked by default
                usersReportModel.setPrimaryRadio("0");
            } else {
                
                  //this is the case, suppose two phone already assigned and
                 // assign phone length
                // is 4 then two empty PhoneVOs added into the existing list
                 
                for (int i = usersReportModel.getMsisdnList().size(); i < length; i++) {
                    userPhoneVO = new UserPhoneVO();
                    usersReportModel.getMsisdnList().add(userPhoneVO);
                }

                // set the radiobutton = checked where primaryNumber = "Y"
                for (int i = 0; i < length; i++) {
                    userPhoneVO =  usersReportModel.getMsisdnList().get(i);
                    if (TypesI.YES.equals(userPhoneVO.getPrimaryNumber())) {
                    	usersReportModel.setPrimaryRadio(i + "");
                    }
                    userPhoneVO.setRowIndex(i + 1);
                }
            }
        }
    
            request.getSession().setAttribute("usersReport", usersReportModel);
            loadAssignPhone.put("data",usersReportModel);
            request.getSession().setAttribute("usersReport", usersReportModel);

        } catch (BTSLBaseException | SQLException e) {
        	 log.errorTrace(methodName, e);
            
           
        }finally {
			if(mcomCon != null){
				mcomCon.close("#loadAssignPhone");
				mcomCon=null;
			}
			
		}
        exitMethod(methodName);       
		return loadAssignPhone;
	}
	
}

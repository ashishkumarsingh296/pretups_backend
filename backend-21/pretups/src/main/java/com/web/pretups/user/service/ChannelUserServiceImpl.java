package com.web.pretups.user.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonValidator;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.C2STransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;
import com.web.user.businesslogic.UserWebDAO;
import com.web.user.web.UserModel;



/**
 * @author pankaj.rawat
 *This class is used to delete channel users
 */
@Service("ChannelUserService")
public class ChannelUserServiceImpl implements ChannelUserService {
	
	private static final String USER_SAME_LEVEL_ERROR = "Error: User are at the same level";
	private static final String USER_NOT_IN_SAME_DOMAIN_ERROR = "Error: User not in the same domain";
	private static final String USER_NOT_EXIST_ERROR = "Error: User not exist";
	private static final String DATA_LIST = "dataList";
	private static final String EMAIL = "email";
	private static final String MODULE = "module";
	private static final String CHNLUSER_DELETESUSPEND = "ChannelUserDeleteSuspend";
	private static final String CLASS_NAME="ChannelUserServiceImpl";
	private static final String VALIDATOR_DELETE_SUS_USER = "configfiles/user/validator-usersuspendDeleteUser.xml";
	private static final String PANEL_NAME="formNumber";
	private static final Log log = LogFactory.getLog(ChannelUserServiceImpl.class.getName());
	
	@Override	
	public void loadDomainList(ChannelUserVO channelUserVO, UserModel userModel,final Model model, HttpServletRequest request) throws SQLException
	{
		 Connection con = null;
	     MComConnectionI mcomCon = null;
	     ChannelUserWebDAO channelUserWebDAO = null;
	     ArrayList channelUserTypeList = null;
	     final String methodName="loadDomainList";
	     final UserModel theForm =  userModel;
		try{	
			mcomCon = new MComConnection();
	            con=mcomCon.getConnection();
	            
	            channelUserWebDAO = new ChannelUserWebDAO();
	            channelUserTypeList = channelUserWebDAO.loadChannelUserTypeList(con);
	            theForm.setChannelUserTypeList(channelUserTypeList);
	            ChannelUserVO channelUserSessionVO=channelUserVO;
	            theForm.setNetworkCode(channelUserSessionVO.getNetworkID());
	            theForm.setNetworkName(channelUserSessionVO.getNetworkName());
	            final CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
	            /*
	             * check the type of user
	             * If user Domain_Code = "OPT"
	             * a)load the domain list that are associated with the user
	             * b)load the category list where domain_code != OPT
	             * else
	             * a)no need to load the domainList get the domain_code and
	             * domainName from the session
	             * b)load the category list where domain_code = userDomainCode and
	             * sequenceNo > logged In User SequenceNo
	             */
	            if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO.getDomainID())) 
	            {
	                theForm.setSelectDomainList(BTSLUtil.displayDomainList(channelUserSessionVO.getDomainList()));
	                if (theForm.getSelectDomainList() != null && !(theForm.getSelectDomainList().isEmpty()))
	                {
	                    theForm.setDomainShowFlag(true);
	                } 
	                else 
	                {
	                    theForm.setDomainShowFlag(false);
	                }

	            } else {
	                ifNotOPTType(con, methodName, theForm,channelUserSessionVO, categoryWebDAO);
	            }

	            final C2STransferDAO c2STransferDAO = new C2STransferDAO();
	            /*
	             * Here we load all transfer rules(from_category and to category),
	             * parent category list will populate on the basis of
	             * category drop down value by calling a method of the same class
	             * populateParentCategoryList
	             */
	          theForm.setOrigParentCategoryList(c2STransferDAO.loadC2SRulesListForChannelUserAssociation(con, channelUserSessionVO.getNetworkID()));
	            theForm.setParentCategoryList(null);

	            final ArrayList list = channelUserSessionVO.getGeographicalAreaList();
	            /*
	             * if list size greater than 1, means user associated with multiple
	             * geographies
	             * e.g BCU assosciated with multiple zones so first we need to
	             * select the zone
	             * first, so here we set on the form for user selction
	             */
	            if (list != null && list.size() > 1) {
	                theForm.setAssociatedGeographicalList(list);
	                final UserGeographiesVO vo = (UserGeographiesVO) list.get(0);
	                theForm.setParentDomainTypeDesc(vo.getGraphDomainTypeName());
	            } else if (list != null && list.size() == 1) {
	                theForm.setAssociatedGeographicalList(null);
	                final UserGeographiesVO vo = (UserGeographiesVO) list.get(0);
	                theForm.setParentDomainCode(vo.getGraphDomainCode());
	                theForm.setParentDomainDesc(vo.getGraphDomainName());
	                theForm.setParentDomainTypeDesc(vo.getGraphDomainTypeName());
	                theForm.setOwnerName(channelUserVO.getUserName());
	            } else {
	                theForm.setAssociatedGeographicalList(null);
	            }

	        } catch (BTSLBaseException  e) {
	        	log.errorTrace(methodName, e);
	           
	        } finally {
				if (mcomCon != null) {
					mcomCon.close(CLASS_NAME+"#"+methodName);
					mcomCon = null;
				}
	            if (log.isDebugEnabled()) {
	            	log.debug(methodName, "Exiting");
	            }
	        }
	
	    }

	private void ifNotOPTType(Connection con, final String methodName,
			final UserModel theForm, ChannelUserVO channelUserSessionVO,
			final CategoryWebDAO categoryWebDAO) throws BTSLBaseException {
		theForm.setDomainCode(channelUserSessionVO.getDomainID());
		theForm.setDomainCodeDesc(channelUserSessionVO.getDomainName());
		theForm.setDomainShowFlag(true);
            
		final ArrayList categoryList = categoryWebDAO.loadCategorListByDomainCode(con, channelUserSessionVO.getDomainID());
		theForm.setOrigCategoryList(categoryList);
		if (categoryList != null) {
		    CategoryVO categoryVO;
		    final ArrayList list = new ArrayList();
		    for (int i = 0, j = categoryList.size(); i < j; i++) {
		        categoryVO = (CategoryVO) categoryList.get(i);
		       // PRF_ASSOCIATE_AGENT flag is true then user can modify
		        // his agent only )
		        // if value of flag is false then user can modify all
		        // the user's below in the hierarchy
		        
		        if ((("associate".equals(theForm.getRequestType())) || ("associateOther".equals(theForm.getRequestType()))) && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PROFILEASSOCIATE_AGENT_PREFERENCES)).booleanValue()){
		            if((categoryVO.getSequenceNumber() == channelUserSessionVO.getCategoryVO().getSequenceNumber() + 1) && PretupsI.AGENTCATEGORY.equals(categoryVO.getCategoryType()))
		            {
		                list.add(categoryVO);
		            }

		        }

		        else if (categoryVO.getSequenceNumber() > channelUserSessionVO.getCategoryVO().getSequenceNumber())
		        {
		            list.add(categoryVO);
		        }
		    }
		    theForm.setCategoryList(list);
		    if ((("associate".equals(theForm.getRequestType())) || ("associateOther".equals(theForm.getRequestType()))) && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PROFILEASSOCIATE_AGENT_PREFERENCES)).booleanValue())
		    {
		         if(list.isEmpty()){
		    		throw new BTSLBaseException(this, methodName, "user.loaddomainlist.error.noagentcategoryfound");
		        }
		    }
		}
	}
	
	@Override
	public void loadCategoryList(UserModel userModel) {
		final String methodName = "loadCategoryList";
		final ArrayList<CategoryVO> list = new ArrayList<>();
		Connection con = null;
		MComConnectionI mcomCon = null;
		final CategoryDAO categoryDAO = new CategoryDAO();
		try{
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();

			userModel.setOrigCategoryList(categoryDAO.loadOtherCategorList(con, PretupsI.OPERATOR_TYPE_OPT));
			if (userModel.getOrigCategoryList() != null && !BTSLUtil.isNullString(userModel.getDomainCode())) {
				CategoryVO categoryVO = null;
				for (int i = 0, j = userModel.getOrigCategoryList().size(); i < j; i++) {
					categoryVO = (CategoryVO) userModel.getOrigCategoryList().get(i);

					if (categoryVO.getDomainCodeforCategory().equals(userModel.getDomainCode())) {
						list.add(categoryVO);
					}
				}
			}
			userModel.setCategoryList(list);
		}catch(Exception e){

			log.error(methodName, "Exception:e=" + e);
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME + methodName, e);
			}
		}
		finally{
			if(mcomCon != null)
			{
				mcomCon.close(CLASS_NAME+"#"+methodName);
				mcomCon=null;
			}
		}
		}
	
	
	
@Override
public boolean loadChnlUserDetails(Model model, ChannelUserVO channelUserSessionVO, UserModel userModel, BindingResult bindingResult, HttpServletRequest request)
{
 	final String methodName = "#loadChnlUserDetails";
	if (log.isDebugEnabled()) {
		log.debug(methodName, "Entered");
	}

	Connection con = null;
	MComConnectionI mcomCon = null;
	UserWebDAO userwebDAO = null;

	ArrayList<UserPhoneVO> userPhoneList = null;
	try {
		userwebDAO = new UserWebDAO();

		userModel.setSearchList(null);
		String status = "";
		String statusUsed = "";

		
		if(request.getParameter("submitMsisdn")!=null){
			CommonValidator commonValidator=new CommonValidator(VALIDATOR_DELETE_SUS_USER, userModel, "UserModelMsisdn");
			Map<String, String> errorMessages = commonValidator.validateModel();
			PretupsRestUtil pru=new PretupsRestUtil();
			pru.processFieldError(errorMessages, bindingResult);
			request.getSession().setAttribute(PANEL_NAME, "Panel-One");
		}
		if(request.getParameter("submitLoginId")!=null){
			CommonValidator commonValidator=new CommonValidator(VALIDATOR_DELETE_SUS_USER, userModel, "UserModelLoginId");
			Map<String, String> errorMessages = commonValidator.validateModel();
			PretupsRestUtil pru=new PretupsRestUtil();
			pru.processFieldError(errorMessages, bindingResult); 
			request.getSession().setAttribute(PANEL_NAME, "Panel-Two");
		}
		if(request.getParameter("submitUser")!=null){
			CommonValidator commonValidator=new CommonValidator(VALIDATOR_DELETE_SUS_USER, userModel, "UserModelUserName");
			Map<String, String> errorMessages = commonValidator.validateModel();
			PretupsRestUtil pru=new PretupsRestUtil();
			pru.processFieldError(errorMessages, bindingResult);
			request.getSession().setAttribute(PANEL_NAME, "Panel-Three");
		}
		if(bindingResult.hasFieldErrors()){

			return false;
		}
		
		status = PretupsBL.userStatusNotIn() + ",'" + PretupsI.USER_STATUS_DELETE_REQUEST + "'";
		statusUsed = PretupsI.STATUS_NOTIN;

		String[] arr = null;
		if (BTSLUtil.isNullString(userModel.getSearchMsisdn()) && BTSLUtil.isNullString(userModel.getDomainCode()) && BTSLUtil.isNullString(userModel
				.getChannelCategoryCode()) && BTSLUtil.isNullString(userModel.getSearchLoginId())) {
			model.addAttribute("fail", PretupsRestUtil.getMessageString("pretups.user.selectchanneluserforview.error.search.required"));
			return false;
		} 
		
		else if (BTSLUtil.isNullString(userModel.getSearchMsisdn()) && BTSLUtil.isNullString(userModel.getSearchLoginId()) )
		{ 
			if (BTSLUtil.isNullString(userModel.getDomainCode())) {
				model.addAttribute("fail", PretupsRestUtil.getMessageString("pretups.user.selectchanneluserforview.error.domaincode.required"));
				return false;
			}
			if (BTSLUtil.isNullString(userModel.getChannelCategoryCode())) {
				model.addAttribute("fail", PretupsRestUtil.getMessageString("pretups.user.selectchanneluserforview.error.channelcategorycode.required"));
				return false;
			}

		}
		if (!BTSLUtil.isNullString(userModel.getSearchMsisdn()) && !BTSLUtil.isValidMSISDN(userModel.getSearchMsisdn())) {

			
				arr = new String[1];
				arr[0] = userModel.getSearchMsisdn();
				model.addAttribute("fail", PretupsRestUtil.getMessageString("pretups.deletesuspend.msisdn.error.length", arr));
				return false;
			
		}
		if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USER_EVENT_REMARKS))).booleanValue() && BTSLUtil.isNullString(userModel.getEventRemarks())) {
			
				model.addAttribute("fail", PretupsRestUtil.getMessageString("pretups.user.deletesuspend.error.remarkrequired", arr));
				return false;
			
		}
		
		
		ChannelUserDAO dao=new ChannelUserDAO();
		

		if (!BTSLUtil.isNullString(userModel.getSearchMsisdn())){

			userModel.setSearchCriteria("M");
			final NetworkPrefixVO prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(userModel
					.getSearchMsisdn())));
			if (prefixVO == null || !prefixVO.getNetworkCode().equals(channelUserSessionVO.getNetworkID())) {
				final String[] arr1 = { userModel.getSearchMsisdn(), channelUserSessionVO.getNetworkName() };
				log.error(methodName, "Error: MSISDN Number" + userModel.getSearchMsisdn() + " not belongs to " + channelUserSessionVO.getNetworkName() + "network");
				model.addAttribute("fail", PretupsRestUtil.getMessageString("pretups.user.deletesuspend.error.msisdnnotinsamenetwork",arr1));
				return false;
			}

			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			final UserDAO userDAO = new UserDAO();


			final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			final String filteredMSISDN = PretupsBL.getFilteredMSISDN(userModel.getSearchMsisdn());
			ChannelUserVO channelUserVO = null;

			if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO.getDomainID())) {
				channelUserVO = channelUserDAO.loadUsersDetails(con, filteredMSISDN, null, statusUsed, status);
			} else {
				String userID = channelUserSessionVO.getUserID();
				channelUserVO = channelUserDAO.loadUsersDetails(con, filteredMSISDN, userID, statusUsed, status);
			}

			if (channelUserVO != null) {
				request.getSession().setAttribute("delSusUserVO", channelUserVO);
				boolean rsaRequired = false;
				rsaRequired = BTSLUtil.isRsaRequired(channelUserVO);
				userModel.setRsaRequired(rsaRequired);
				if (channelUserSessionVO.getCategoryVO().getCategoryCode().equals(channelUserVO.getCategoryVO().getCategoryCode()) && !PretupsI.USER_TYPE_STAFF.equals(channelUserVO.getUserType()))
				{
					final String[] arr2 = { userModel.getSearchMsisdn() };
					log.error(methodName, USER_SAME_LEVEL_ERROR);
					model.addAttribute("fail", PretupsRestUtil.getMessageString("pretups.user.deletesuspend.error.usermsisdnatsamelevel", arr2));
					return false;
				}

				if (userModel.getSelectDomainList() != null) {
					final boolean isDomainFlag = this.isExistDomain(userModel.getSelectDomainList(), channelUserVO);
					final ListValueVO listValueVO = BTSLUtil.getOptionDesc(channelUserVO.getCategoryVO().getDomainCodeforCategory(), userModel.getSelectDomainList());
					userModel.setDomainCodeDesc(listValueVO.getLabel());
					if (!isDomainFlag) {

						final String[] arr2 = { userModel.getSearchMsisdn() };
						log.error(methodName, USER_NOT_IN_SAME_DOMAIN_ERROR);
						model.addAttribute("fail", PretupsRestUtil.getMessageString("pretups.user.deletesuspend.error.usermsisdnnotinsamedomain", arr2));
						return false;

					}
				}
				final boolean isGeoDomainFlag = userwebDAO.isUserInSameGRPHDomain(con, channelUserVO.getUserID(), channelUserVO.getCategoryVO().getGrphDomainType(),
						channelUserSessionVO.getUserID(), channelUserSessionVO.getCategoryVO().getGrphDomainType());
				if (isGeoDomainFlag) {
					userModel.setCategoryVO(channelUserVO.getCategoryVO());
					userModel.setCategoryCode(userModel.getCategoryVO().getCategoryCode());
					userModel.setChannelCategoryCode(userModel.getCategoryVO().getCategoryCode());
					userModel.setCategoryCodeDesc(userModel.getCategoryVO().getCategoryName());
					userModel.setChannelCategoryDesc(userModel.getCategoryVO().getCategoryName());
                    userModel.setChannelUserName(channelUserVO.getFirstName());
					userModel.setParentDomainDesc(channelUserVO.getGeographicalDesc());
					userModel.setUserId(channelUserVO.getUserID());
					userModel.setEmail(channelUserVO.getEmail());
					userPhoneList = userDAO.loadUserPhoneList(con,channelUserVO.getUserID());
					for(int i =0; i < userPhoneList.size(); i++)
					{
						userPhoneList.get(i).setShowSmsPin("");
						userPhoneList.get(i).setConfirmSmsPin("");
					}
					userModel.setMsisdnList(userPhoneList);
					request.getSession().setAttribute(DATA_LIST, userPhoneList);
					request.getSession().setAttribute(MODULE, CHNLUSER_DELETESUSPEND);
					request.getSession().setAttribute(EMAIL, userModel.getEmail());
					
					
					calculateUserTotBal(userModel, con, dao,channelUserVO);
					return true;
				} 
				
				else
				{
					final String[] arr2 = { userModel.getSearchMsisdn() };
					log.error(methodName, USER_NOT_IN_SAME_DOMAIN_ERROR);

					model.addAttribute("fail", PretupsRestUtil.getMessageString("pretups.user.deletesuspend.error.usermsisdnnotinsamegeodomain",arr2));
					return false;
				}

			} else {

				final String[] arr2 = { userModel.getSearchMsisdn() };
				log.error(methodName, USER_NOT_EXIST_ERROR);

				model.addAttribute("fail", PretupsRestUtil.getMessageString("pretups.user.deletesuspend.error.usermsisdnnotexist", arr2));
				return false;
			}

		}

		else if (!BTSLUtil.isNullString(userModel.getSearchLoginId()) &&  BTSLUtil.isNullString(userModel.getUserId())){


			userModel.setSearchCriteria("L");
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			final UserDAO userDAO = new UserDAO();


			final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			ChannelUserVO channelUserVO = null;

			if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO.getDomainID())) {
				channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(con, userModel.getSearchLoginId(), null, statusUsed, status);
			} else {
				String userID = channelUserSessionVO.getUserID();


				channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(con, userModel.getSearchLoginId(), userID, statusUsed, status);
			}

			if (channelUserVO != null) {

				boolean rsaRequired = false;
				rsaRequired = BTSLUtil.isRsaRequired(channelUserVO);
				userModel.setRsaRequired(rsaRequired);

				if (channelUserSessionVO.getCategoryVO().getCategoryCode().equals(channelUserVO.getCategoryVO().getCategoryCode()) && !PretupsI.USER_TYPE_STAFF.equals(channelUserVO.getUserType())) 
				{
					final String[] arr2 = { userModel.getSearchLoginId() };
					log.error(methodName, USER_SAME_LEVEL_ERROR);

					model.addAttribute("fail", PretupsRestUtil.getMessageString("pretups.user.deletesuspend.error.userloginidatsamelevel", arr2));
					return false;
				}

				if (userModel.getSelectDomainList() != null) {
					final boolean isDomainFlag = this.isExistDomain(userModel.getSelectDomainList(), channelUserVO);
					final ListValueVO listValueVO = BTSLUtil.getOptionDesc(channelUserVO.getCategoryVO().getDomainCodeforCategory(), userModel.getSelectDomainList());
					userModel.setDomainCodeDesc(listValueVO.getLabel());
					if (!isDomainFlag) {
						final String[] arr2 = { userModel.getSearchLoginId() };
						log.error(methodName, USER_NOT_IN_SAME_DOMAIN_ERROR);

						model.addAttribute("fail", PretupsRestUtil.getMessageString("pretups.user.deletesuspend.error.userloginidnotinsamedomain", arr2));
						return false;
					}
				}


				final boolean isGeoDomainFlag = userwebDAO.isUserInSameGRPHDomain(con, channelUserVO.getUserID(), channelUserVO.getCategoryVO().getGrphDomainType(),
						channelUserSessionVO.getUserID(), channelUserSessionVO.getCategoryVO().getGrphDomainType());

				if (isGeoDomainFlag) {
					 userModel.setChannelUserName(channelUserVO.getFirstName());
					userModel.setCategoryVO(channelUserVO.getCategoryVO());
					userModel.setCategoryCode(userModel.getCategoryVO().getCategoryCode());
					userModel.setChannelCategoryCode(userModel.getCategoryVO().getCategoryCode());
					userModel.setCategoryCodeDesc(userModel.getCategoryVO().getCategoryName());
					userModel.setChannelCategoryDesc(userModel.getCategoryVO().getCategoryName());
					userModel.setParentDomainDesc(channelUserVO.getGeographicalDesc());
					userModel.setEmail(channelUserVO.getEmail());
					userModel.setUserId(channelUserVO.getUserID());
					userPhoneList = userDAO.loadUserPhoneList(con,channelUserVO.getUserID());
					for(int i =0; i < userPhoneList.size(); i++){
						userPhoneList.get(i).setShowSmsPin("");
						userPhoneList.get(i).setConfirmSmsPin("");
					}
					userModel.setMsisdnList(userPhoneList);
					request.getSession().setAttribute(MODULE, CHNLUSER_DELETESUSPEND);
					request.getSession().setAttribute(EMAIL, userModel.getEmail());
					calculateUserTotBal(userModel, con, dao,channelUserVO);
					return true;
				} 
				 else
				 {

					final String[] arr2 = { userModel.getSearchLoginId() };
					log.error(methodName, USER_NOT_IN_SAME_DOMAIN_ERROR);

					model.addAttribute("fail", PretupsRestUtil.getMessageString("pretups.user.deletesuspend.error.userloginidnotinsamegeodomain", arr2));
					return false;
				 }

			} else {
				final String[] arr2 = { userModel.getSearchLoginId() };
				log.error(methodName, USER_NOT_EXIST_ERROR);
				model.addAttribute("fail", PretupsRestUtil.getMessageString("pretups.user.deletesuspend.error.userloginidnotexist", arr2)); 
				return false;
			}
		}
		else if(!BTSLUtil.isNullString(userModel.getUserId())){
			
			String userName=userModel.getUserId();
			userModel.setOtherInfo(userName);
			String[] parts=userName.split("\\(");
			String userId;			
		    userName = parts[0];
			userModel.setUserName(userName);
			if (request.getSession().getAttribute("ownerID") != null) {
				userModel.setOwnerID(request.getSession().getAttribute("ownerID").toString());
			}
			
			else
			{
				userModel.setOwnerName(userName);
			}
			userModel.setSearchCriteria("D");
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			final UserDAO userDAO = new UserDAO();


			
			UserVO channelUserVO = null;
			
			

        	String index = request.getSession().getAttribute("index").toString();
        	String prntDomainCode = request.getSession().getAttribute("prntDomainCode").toString();
        	ChannelUserServiceImpl   channelUserService= new ChannelUserServiceImpl();
			
			List<UserVO> userList = channelUserService.loadUserList(channelUserSessionVO, userModel.getChannelCategoryCode(), userModel.getOwnerID(), userName,  userModel.getDomainCode(), prntDomainCode, request, index);
		   
			if(userList.size() == 1){

				channelUserVO  = userList.get(0); 
				userId = channelUserVO.getUserID();
				boolean rsaRequired = false;
				rsaRequired = BTSLUtil.isRsaRequired(channelUserVO);
				userModel.setRsaRequired(rsaRequired);

				if (channelUserSessionVO.getCategoryVO().getCategoryCode().equals(channelUserVO.getCategoryCode()) && !PretupsI.USER_TYPE_STAFF	.equals(channelUserVO.getUserType())) {
					final String[] arr2 = { userModel.getUserId() };
					log.error(methodName, USER_SAME_LEVEL_ERROR);

					model.addAttribute("fail", PretupsRestUtil.getMessageString("pretups.user.deletesuspend.error.usernameatsamelevel", arr2));
					userModel.setUserId(userId);
					return false;
				}
				
					userModel.setEmail(channelUserVO.getEmail());
					userModel.setUserId(channelUserVO.getUserID());
					userPhoneList = userDAO.loadUserPhoneList(con,channelUserVO.getUserID());
					for(int i =0; i < userPhoneList.size(); i++){
						userPhoneList.get(i).setShowSmsPin("");
						userPhoneList.get(i).setConfirmSmsPin("");
					}
					userModel.setMsisdnList(userPhoneList);
					request.getSession().setAttribute(MODULE, CHNLUSER_DELETESUSPEND);
					request.getSession().setAttribute(EMAIL, userModel.getEmail());
					calculateUserTotBal(userModel, con, dao, channelUserVO);

					return true;
				

			}
			
			//added on 04/05/2018
			else if(userList.size()>1){

                boolean isExist = false;
                userId = channelUserVO.getUserID();
                if (!BTSLUtil.isNullString(userId)) {
                    for (int i = 0, k = userList.size(); i < k; i++) {
                    	channelUserVO =  userList.get(i);
                        if (channelUserVO.getUserID().equals(userId) && userModel.getUserName().compareTo(channelUserVO.getUserName()) == 0) {
                            userModel.setUserId(channelUserVO.getUserID());
                            userModel.setUserName(channelUserVO.getUserName());
                            isExist = true;
                            break;
                        }
                    }
                    
                    

                } else {
                	ChannelUserVO listValueNextVO = null;
                    for (int i = 0, k = userList.size(); i < k; i++) {
                    	channelUserVO =  userList.get(i);
                        if (userModel.getUserName().compareTo(channelUserVO.getUserName()) == 0) {
                            if (((i + 1) < k)) {
                                listValueNextVO = (ChannelUserVO) userList.get(i + 1);
                                if (userModel.getUserName().compareTo(listValueNextVO.getUserName()) == 0) {
                                    isExist = false;
                                    break;
                                }
                                userModel.setUserId(channelUserVO.getUserID());
                                userModel.setUserName(channelUserVO.getUserName());
                                
                                isExist = true;
                                break;
                            }
                            userModel.setUserId(channelUserVO.getUserID());
                            userModel.setUserName(channelUserVO.getUserName());
                         
                            isExist = true;
                            break;
                        }
                    }
                }
                if (!isExist) {
                   model.addAttribute("fail",
							PretupsRestUtil.getMessageString("pretups.channeltransfer.chnltochnlsearchuser.usermorethanoneexist.msg"));
					return false;
               
                }
            
			}
			else {
				final String[] arr2 = { userModel.getUserId() };
				log.error(methodName, USER_NOT_EXIST_ERROR);
				model.addAttribute("fail", PretupsRestUtil.getMessageString("pretups.user.deletesuspend.error.usernamenotexist", arr2));
				return false;
			}


		}else{
			model.addAttribute("fail", PretupsRestUtil.getMessageString("pretups.user.choose.at.least.one.criteria")); 
			return false;

		}

	}catch(Exception e){

		try {
			if (con != null) {
				con.rollback();
			}
		} catch (Exception se) {
			log.errorTrace(methodName, se);
		}
		log.error(methodName, "Exception:e= " + e);
		log.errorTrace(methodName, e);
	}finally {
		if (mcomCon != null) {
			mcomCon.close(CLASS_NAME+"#"+methodName);
			mcomCon = null;
		}
		if (log.isDebugEnabled()) {
			log.debug(methodName, "Exiting: "+methodName);
		}
	}
	return true;        
}

	private void calculateUserTotBal(UserModel userModel, Connection con,ChannelUserDAO dao, UserVO channelUserVO)
			throws BTSLBaseException
	{
		long balance=0;
		userModel.setUserBalanceList(dao.loadUserBalances(con,channelUserVO.getNetworkID(), channelUserVO.getNetworkID(),channelUserVO.getUserID()));
		ArrayList balList = userModel.getUserBalanceList();
		
		//Aggregating every type of balance user has
		if(!(balList==null ||balList.isEmpty())){
		for (int i = 0; i < balList.size(); i++)
		{
			UserBalancesVO ubVO = (UserBalancesVO) (userModel.getUserBalanceList().get(i));
			balance += ubVO.getBalance();
		}
		}
		userModel.setTotalBal(PretupsBL.getDisplayAmount(balance));
	}

private boolean isExistDomain(ArrayList p_domainList, UserVO p_channelUserVO) throws BTSLBaseException {
	final String methodName = "isExistDomain";
	if (log.isDebugEnabled()) {
		log.debug(methodName, "Entered p_domainList.size()=" + p_domainList.size() + ", p_channelUserVO=" + p_channelUserVO);
	}
	if (p_domainList == null || p_domainList.isEmpty()) {
		return true;
	}
	boolean isDomainExist = false;
	try {
		ListValueVO listValueVO;
		for (int i = 0, j = p_domainList.size(); i < j; i++) {
			listValueVO = (ListValueVO) p_domainList.get(i);
			if (listValueVO.getValue().equals(p_channelUserVO.getCategoryVO().getDomainCodeforCategory())) {
				isDomainExist = true;
				break;
			}
		}
	} catch (Exception e) {
		log.errorTrace(methodName, e);
		throw new BTSLBaseException(e);
	}
	if (log.isDebugEnabled()) {
		log.debug(methodName, "Exiting isDomainExist=" + isDomainExist);
	}
	return isDomainExist;
}

@Override
public List<UserVO> loadUserList(UserVO userVO, String categorycode, String ownerId,
		String userName, String domainCode, String prntDomainCode, HttpServletRequest request, String index) {
	final String METHOD_NAME = "loadUserList";
	StringBuilder statusSbf = null;
	StringBuilder statusUsedSbf = null;
	String status = null;
	String statusUsed = null;
	ArrayList<UserVO> userList = new ArrayList<UserVO>();
	UserWebDAO userwebDAO = new UserWebDAO();
	Connection con = null;
	MComConnectionI mcomCon = null;
	try {
		mcomCon = new MComConnection();
		try{
			con = mcomCon.getConnection();
		}
		catch(SQLException e){
			log.error(METHOD_NAME,  "SQLException"+ e.getMessage());
			if (log.isDebugEnabled()) {
				log.debug(CLASS_NAME + METHOD_NAME, e);
			}
		}
		String requestType = (String) request.getSession().getAttribute("requestType");
		statusSbf = new StringBuilder();
		statusUsedSbf = new StringBuilder();

		if ("1".equalsIgnoreCase(index) && !"CHANNEL".equalsIgnoreCase(userVO.getUserType())) {
			if ("changeSmsPin".equals(requestType)) {
				statusSbf.append(PretupsBL.userStatusNotIn());
				statusUsedSbf.append(PretupsI.STATUS_NOTIN);
			}

			else{
				statusSbf.append(PretupsBL.userStatusIn() + ",'" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'");
				statusUsedSbf.append(PretupsI.STATUS_IN);
			}

			status = statusSbf.toString();
			statusUsed = statusUsedSbf.toString();
			
			userList = userwebDAO.loadOwnerUserList(con, prntDomainCode, "%" + userName + "%", domainCode, statusUsed,
					status);
		} else {
			

				statusSbf.append(PretupsBL.userStatusNotIn());
				statusUsedSbf.append(PretupsI.STATUS_NOTIN);
				status = statusSbf.toString();
				statusUsed = statusUsedSbf.toString();

				if (PretupsI.OPERATOR_TYPE_OPT.equals(userVO.getDomainID())) {
					userList = userwebDAO.loadUsersListByNameAndOwnerId(con, categorycode, "%" + userName + "%", ownerId, null, statusUsed, status, "CHANNEL");
				} else {
					String userID = userVO.getUserID();


					userList = userwebDAO.loadUsersListByNameAndOwnerId(con, categorycode, "%" + userName + "%", userID, userID, statusUsed, status, "CHANNEL");
				}
			
		}
	} catch (BTSLBaseException e) {

		log.error(METHOD_NAME, "BTSLBaseException:" + e.getMessage());
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + METHOD_NAME, e);
		}
	}finally{
		if(mcomCon != null)
		{
			mcomCon.close(CLASS_NAME+"#"+METHOD_NAME);
			mcomCon=null;
		}
	}

	return userList;
}


@Override
public List<UserVO> loadOwnerList(UserVO userVO, String prntDomaincode, String ownerName, String domainCode, HttpServletRequest request){
	Connection con = null;
	MComConnectionI mcomCon = null;
	final UserWebDAO userwebDAO = new UserWebDAO();
	final String methodName = "loadOwnerList";
	ArrayList userList = null;
	try{
		mcomCon = new MComConnection();
		con=mcomCon.getConnection();

		if (!BTSLUtil.isNullString(ownerName)) {

			String status = null;
			String statusUsed = null;

			StringBuilder statusSbf = new StringBuilder();
			StringBuilder statusUsedSbf  = new StringBuilder();

			String requestType = (String) request.getSession().getAttribute(
					"requestType");
			if ("changeSmsPin".equals(requestType)) {
				statusSbf.append(PretupsBL.userStatusIn() + ",'" + PretupsI.USER_STATUS_SUSPEND_REQUEST + "'");
				statusUsedSbf.append(PretupsI.STATUS_IN);
			}
			else{
				statusSbf.append(PretupsBL.userStatusNotIn());
				statusUsedSbf.append(PretupsI.STATUS_NOTIN);
			}


			status = statusSbf.toString();
			statusUsed = statusUsedSbf.toString();
			userList = userwebDAO.loadOwnerUserList(con, prntDomaincode, "%" + ownerName + "%", domainCode, statusUsed,
					status);

		}

	}catch(Exception e){

		log.error(methodName, "Exception:e=" + e);
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + methodName, e);
		}
	}
	finally{
		if(mcomCon != null)
		{
			mcomCon.close(CLASS_NAME+"#"+methodName);
			mcomCon=null;
		}
	}
	return userList;
}
@Override
public void getCategoryList(UserModel userModel){
	final String methodName = "getCategoryList";
	final ArrayList<CategoryVO> list = new ArrayList<>();
	Connection con = null;
	MComConnectionI mcomCon = null;
	final CategoryDAO categoryDAO = new CategoryDAO();
	try{
		mcomCon = new MComConnection();
		con = mcomCon.getConnection();

		userModel.setOrigCategoryList(categoryDAO.loadOtherCategorList(con, PretupsI.OPERATOR_TYPE_OPT));
		if (userModel.getOrigCategoryList() != null && !BTSLUtil.isNullString(userModel.getDomainCode())) {
			CategoryVO categoryVO = null;
			for (int i = 0, j = userModel.getOrigCategoryList().size(); i < j; i++) {
				categoryVO = (CategoryVO) userModel.getOrigCategoryList().get(i);

				if (categoryVO.getDomainCodeforCategory().equals(userModel.getDomainCode())) {
					list.add(categoryVO);
				}
			}
		}
		userModel.setCategoryList(list);
	}catch(Exception e){

		log.error(methodName, PretupsI.EXCEPTION + e);
		if (log.isDebugEnabled()) {
			log.debug(CLASS_NAME + methodName, e);
		}
	}
	finally{
		if(mcomCon != null)
		{
			mcomCon.close(CLASS_NAME+"#"+methodName);
			mcomCon=null;
		}
	}
	
}


}
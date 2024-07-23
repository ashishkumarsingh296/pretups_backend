package com.web.pretups.user.service;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonValidator;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.UserBalancesVO;
import com.btsl.pretups.common.ExcelFileIDI;
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
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.user.businesslogic.UserWebDAO;
import com.web.user.web.UserModel;


@Service("userBalanceService")
public class UserBalanceServiceImpl implements UserBalanceService {

	public static final Log _log = LogFactory.getLog(UserBalanceServiceImpl.class.getName());
	
	private static final String FAIL_KEY = "fail";
	private static final String CHANNEL = "CHANNEL";



	@Override
	public UserVO loadSelfBalance(ChannelUserVO channelUserSessionVO, UserVO userVO) {

		if (_log.isDebugEnabled()) {
			_log.debug("UserBalanceServiceImpl#loadSelfBalance", PretupsI.ENTERED);
		}
		final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		String status = PretupsBL.userStatusNotIn();
		String statusUsed = PretupsI.STATUS_NOTIN;
		ChannelUserVO channelUserVO = new ChannelUserVO();
		Connection con = null;
		MComConnectionI mcomCon = null;
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();

			if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO.getDomainID())) {
				channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(con, channelUserSessionVO.getParentLoginID(), null, statusUsed, status);
			} else {

				if (channelUserSessionVO.isStaffUser()) {
					userVO = channelUserDAO.loadUsersDetailsByLoginId(con, channelUserSessionVO.getParentLoginID(), null, statusUsed, status);
				} else {
					channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(con, channelUserSessionVO.getLoginID(), null, statusUsed, status);
				}
			}


			userVO = this.loadBalance(channelUserVO, channelUserVO);
			userVO.setNetworkName(channelUserSessionVO.getNetworkName());

		}catch(Exception e){

			_log.errorTrace("loadSelfBalance", e);
		}
		finally{
			if(mcomCon != null)
			{
				mcomCon.close("UserBalanceServiceImpl#loadSelfBalance");
				mcomCon=null;
			}
		}
		if (_log.isDebugEnabled()) {
			_log.debug("UserBalanceServiceImpl#loadSelfBalance", PretupsI.EXITED);
		}		
		return userVO;
	}

	@Override
	@SuppressWarnings("unchecked")
	public String downloadFileForEnq(UserVO userVO, HttpServletRequest request) {

		final String methodName = "downloadFileForEnq";
		if (_log.isDebugEnabled())
			_log.debug(methodName, "Entered");
		String filePath=null;
		String fileName=null;
		String filelocation =null;
		String fileArr[][]=null;
		String headingArr[][]=null;

		try{


			List<UserBalancesVO> balList = userVO.getUserBalanceList();
			filePath = Constants.getProperty("DownloadUserBalEnqPath");
			try{
				File fileDir = new File(filePath);
				if(!fileDir.isDirectory())
					fileDir.mkdirs();
			}catch(SecurityException e){
				_log.debug(methodName, "Exception"+e.getMessage());
				_log.errorTrace(methodName, e);
				throw new BTSLBaseException(this,methodName,"pretups.downloadfile.error.dirnotcreated","error");
			}
			fileName = Constants.getProperty("DownloadUserBalEnqtFileName")+BTSLUtil.getTimestampFromUtilDate(new Date()).getTime()+".xls";
			int cols=4;

			int rows=balList.size()+1;
			fileArr=new String[rows][cols];
			int i=0;
			int j=0;
			String heading="pretups.channel.user.bal.xls.enq.fileheading";
			headingArr=new String[2][8];
			headingArr[0][j]="pretups.channel.user.bal.xls.enq.userid";
			j=j+1;
			headingArr[0][j]="pretups.channel.user.bal.xls.enq.username";
			j=j+1;
			headingArr[0][j]="pretups.channel.user.bal.xls.enq.msisdn";
			j=j+1;
			headingArr[0][j]="pretups.channel.user.bal.xls.enq.usercode";
			j=j+1;
			headingArr[0][j]="pretups.channel.user.bal.xls.enq.loginid";
			j=j+1;
			headingArr[0][j]="pretups.channel.user.bal.xls.enq.usertype";
			j=j+1;
			headingArr[0][j]="pretups.channel.user.bal.xls.enq.nw";
			j=j+1;
			headingArr[0][j]="pretups.channel.user.bal.xls.enq.cat";
			j=j+1;

			fileArr[0][i]="pretups.channel.user.bal.xls.enq.prodname";
			i=i+1;
			fileArr[0][i]="pretups.channel.user.bal.xls.enq.prodcode";
			i=i+1;
			fileArr[0][i]="pretups.channel.user.bal.xls.enq.prodstcode";
			i=i+1;
			fileArr[0][i]="pretups.channel.user.bal.xls.enq.usrbal";
			i=i+1;
			if(userVO.getAgentBalanceList().size()>0)
			{
				fileArr[0][i]="pretups.channel.user.bal.xls.enq.agentbalance";
				i=i+1;
			}	        
			fileArr=this.convertTo2dArray(fileArr,balList,rows,userVO);
			headingArr=this.convertTo2dArrayHeader(headingArr,userVO);
			PretupsRestUtil pretupsRestUtil = new PretupsRestUtil();
			pretupsRestUtil.writeExcel(ExcelFileIDI.USER_BAL_ENQ,fileArr, headingArr, heading, 2, BTSLUtil.getBTSLLocale(request),filePath+""+fileName);
			filelocation = filePath + fileName;


		}catch(Exception e){
			_log.debug(methodName, "Exception"+e.getMessage());
			_log.errorTrace(methodName, e);

		}

		return filelocation;
	}

	@SuppressWarnings("rawtypes")
	private String[][] convertTo2dArray(String [][]p_fileArr,List<UserBalancesVO> p_balList,int p_rows,UserVO userVO)
	{
		String methodName= "convertTo2dArray";
		if (_log.isDebugEnabled())
			_log.debug(methodName, "Entered p_fileArr="+p_fileArr.length+" p_balList.size()="+p_balList.size()+" p_rows"+p_rows);
		try{

			Iterator iterator=p_balList.iterator();
			int rows=0;
			int cols;
			UserBalancesVO balVO=null;
			while (iterator.hasNext())
			{
				balVO = (UserBalancesVO)iterator.next();
				rows++;
				cols=0;
				p_fileArr[rows][cols]=balVO.getProductName();
				cols=cols+1;
				p_fileArr[rows][cols]=balVO.getProductCode();
				cols=cols+1;
				p_fileArr[rows][cols]=balVO.getProductShortCode();
				cols=cols+1;
				p_fileArr[rows][cols]=balVO.getBalanceStr();
				cols=cols+1;
				if(userVO.getAgentBalanceList().size()>0){
					p_fileArr[rows][cols]=balVO.getAgentBalanceStr();
					cols=cols+1;
				}
			}
		}catch(Exception e){
			_log.debug(methodName, "Exception"+e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, 
					"ChannelUserAction[convertTo2dArray]", "", "", "", "Exception:" + e.getMessage());
		}
		if (_log.isDebugEnabled())
			_log.debug(methodName, "Exit p_fileArr="+p_fileArr);

		return p_fileArr;
	}

	private String[][] convertTo2dArrayHeader(String [][]p_fileArr,UserVO userVO)
	{
		final String methodName = "convertTo2dArrayHeader";
		if (_log.isDebugEnabled())
			_log.debug(methodName, "Entered p_fileArr="+p_fileArr.length+" p_form="+userVO);
		try
		{
			int rows=1;
			int cols=0;
			p_fileArr[rows][cols]=userVO.getUserID();
			cols=cols+1;
			p_fileArr[rows][cols]=userVO.getUserName();
			cols=cols+1;
			p_fileArr[rows][cols]=userVO.getMsisdn();
			cols=cols+1;
			p_fileArr[rows][cols]=userVO.getUserCode();
			cols=cols+1;
			p_fileArr[rows][cols]=userVO.getLoginID();
			cols=cols+1;
			p_fileArr[rows][cols]=userVO.getUserType();
			cols=cols+1;
			p_fileArr[rows][cols]=userVO.getNetworkName();
			cols=cols+1;
			p_fileArr[rows][cols]=userVO.getCategoryVO().getCategoryName();
			cols=cols+1;

		}catch(Exception e){
			_log.debug(methodName, "Exception"+e.getMessage());
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "ChannelUserAction[convertTo2dArrayHeader]", "", "", "", "Exception:" + e.getMessage());
		}
		if (_log.isDebugEnabled())
			_log.debug(methodName, "Exit p_fileArr="+p_fileArr);
		return p_fileArr;
	}

	@SuppressWarnings("unchecked")
	private UserVO loadBalance(UserVO userVO, ChannelUserVO channelUserVO){

		if (_log.isDebugEnabled()) {
			_log.debug("UserBalanceServiceImpl#loadBalance", PretupsI.ENTERED);
		}
		final ChannelUserDAO channelUserDAO = new ChannelUserDAO();
		List<UserBalancesVO> userBalanceList = new ArrayList<UserBalancesVO>();
		List<UserBalancesVO> agentBalanceList = new ArrayList<UserBalancesVO>();

		Connection con = null;
		MComConnectionI mcomCon = null;
		try {
			mcomCon = new MComConnection();
			con=mcomCon.getConnection();


			if(PretupsI.AGENT_ALLOWED_YES.equalsIgnoreCase(channelUserVO.getCategoryVO().getAgentAllowed())) {
				// load teh balance of agents
				agentBalanceList = channelUserDAO.loadUserAgentsBalance(con, channelUserVO.getUserID());
				if (agentBalanceList != null && agentBalanceList.size() > 0) {
					userVO.setAgentBalanceList(agentBalanceList);
				}
			}

			userBalanceList = channelUserDAO.loadUserBalances(con, channelUserVO.getNetworkID(), channelUserVO.getNetworkID(), channelUserVO.getUserID());
			
			if (userBalanceList != null && userBalanceList.size() > 0) {
				if (agentBalanceList != null && agentBalanceList.size() > 0) {
					int agentBalanceListSize = agentBalanceList.size();
					for (int index = 0; index < agentBalanceListSize; index++) {
						for (int index1 = 0; index1 < userBalanceList.size(); index1++) {
							if (((UserBalancesVO) userBalanceList.get(index1)).getProductShortCode().equals(
									((UserBalancesVO) agentBalanceList.get(index)).getProductShortCode())) {
								((UserBalancesVO) userBalanceList.get(index1)).setAgentBalanceStr(PretupsBL.getDisplayAmount(((UserBalancesVO) agentBalanceList.get(index))
										.getBalance()));
							}
						}
					}
				}

			}
			userVO.setAgentBalanceList(agentBalanceList);
			userVO.setUserBalanceList(userBalanceList);  

		}catch(Exception e){

			_log.errorTrace("loadSelfBalance", e);
		}
		finally{
			if(mcomCon != null)
			{
				mcomCon.close("UserBalanceServiceImpl#loadSelfBalance");
				mcomCon=null;
			}
		}
		if (_log.isDebugEnabled()) {
			_log.debug("UserBalanceServiceImpl#loadSelfBalance", PretupsI.EXITED);
		}		
		return userVO;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public UserModel loadCategory(UserVO userVO) {

		final String methodName = "loadCategory";
		if (_log.isDebugEnabled()) {
			_log.debug("UserBalanceServiceImpl#loadCategory", PretupsI.ENTERED);
		}

		UserModel userModel = new UserModel();
		
		userModel.setNetworkCode(userVO.getNetworkID());
		userModel.setNetworkName(userVO.getNetworkName());
		userModel.setLoginUserID(userVO.getOwnerName()+"("+userVO.getOwnerID()+")");
		Connection con = null;
		MComConnectionI mcomCon = null;
		CategoryWebDAO categoryWebDAO =  new CategoryWebDAO();
		final CategoryDAO categoryDAO = new CategoryDAO();
		final ArrayList list = new ArrayList();
		try{
			 mcomCon = new MComConnection();
		     con = mcomCon.getConnection();
	    if (PretupsI.OPERATOR_TYPE_OPT.equals(userVO.getDomainID())) {
	    	userModel.setDomainCode(userVO.getDomainID());
	    	userModel.setDomainList(BTSLUtil.displayDomainList(userVO.getDomainList()));   
	    	if(!userModel.getDomainList().isEmpty()){
	    		userModel.setDomainListTSize(userVO.getDomainList().size());
	    		}
	    	 if (userModel.getDomainList() != null && !userModel.getDomainList().isEmpty()) {
	    		 userModel.setDomainShowFlag(true);
             } else {
            	 userModel.setDomainShowFlag(false);
             }
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
        } else {
        	userModel.setDomainCode(userVO.getDomainID());
        	userModel.setDomainCodeDesc(userVO.getDomainName());
        	userModel.setDomainShowFlag(true);
            final ArrayList categoryList = categoryWebDAO.loadCategorListByDomainCode(con, userVO.getDomainID());
            userModel.setOrigCategoryList(categoryList);
            if (categoryList != null) {
                CategoryVO categoryVO = null;
                for (int i = 0, j = categoryList.size(); i < j; i++) {
                    categoryVO = (CategoryVO) categoryList.get(i);
                    
                    if ("associate".equals(userModel.getRequestType()) && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PROFILEASSOCIATE_AGENT_PREFERENCES)).booleanValue() || "associateOther".equals(userModel.getRequestType()) && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PROFILEASSOCIATE_AGENT_PREFERENCES)).booleanValue()) {
                        if ((categoryVO.getSequenceNumber() == userVO.getCategoryVO().getSequenceNumber() + 1) && PretupsI.AGENTCATEGORY.equals(categoryVO
                                        .getCategoryType())) {
                            list.add(categoryVO);
                        }

                    }

                     else if (categoryVO.getSequenceNumber() > userVO.getCategoryVO().getSequenceNumber()) {
                        list.add(categoryVO);
                    }
                }
                userModel.setCategoryList(list);
                if ("associate".equals(userModel.getRequestType()) && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PROFILEASSOCIATE_AGENT_PREFERENCES)).booleanValue() || "associateOther".equals(userModel.getRequestType()) && ((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.PROFILEASSOCIATE_AGENT_PREFERENCES)).booleanValue()) {
                    if (list.isEmpty()) {
                        throw new BTSLBaseException(this, methodName, "user.loaddomainlist.error.noagentcategoryfound");
                    }
                }

            }
        }
	    final ArrayList geoList = userVO.getGeographicalAreaList();
        if (geoList != null && geoList.size() > 1) {
        	userModel.setAssociatedGeographicalList(geoList);
        	userModel.setGeoDomainSize(geoList.size());
            final UserGeographiesVO vo = (UserGeographiesVO) geoList.get(0);
            userModel.setParentDomainTypeDesc(vo.getGraphDomainTypeName());
        } else if (geoList != null && geoList.size() == 1) {
        	userModel.setGeoDomainSize(geoList.size());
        	userModel.setAssociatedGeographicalList(null);
            final UserGeographiesVO vo = (UserGeographiesVO) geoList.get(0);
            userModel.setParentDomainCode(vo.getGraphDomainCode());
            userModel.setParentDomainDesc(vo.getGraphDomainName());
            userModel.setParentDomainTypeDesc(vo.getGraphDomainTypeName());
        } else {
        	userModel.setAssociatedGeographicalList(null);
        }
		}catch(Exception e){

			_log.errorTrace(methodName, e);
		}
		finally{
			if(mcomCon != null)
			{
				mcomCon.close("UserBalanceServiceImpl#"+methodName);
				mcomCon=null;
			}
		}
		if (_log.isDebugEnabled()) {
			_log.debug("UserBalanceServiceImpl#loadCategory", PretupsI.EXITED);
		}	

		return userModel;
	}

	@SuppressWarnings("unchecked")
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
				_log.error(METHOD_NAME,  "SQLException"+ e.getMessage());
				if (_log.isDebugEnabled()) {
					_log.debug("UserBalanceServiceImpl" + METHOD_NAME, e);
				}
			}
			String requestType = (String) request.getSession().getAttribute(
					"requestType");
			statusSbf = new StringBuilder();
			statusUsedSbf = new StringBuilder();

			if ("1".equalsIgnoreCase(index) && !CHANNEL.equalsIgnoreCase(userVO.getUserType())) {
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
						userList = userwebDAO.loadUsersListByNameAndOwnerId(con, categorycode, "%" + userName + "%", ownerId, null, statusUsed, status, CHANNEL);
					} else {
						String userID = userVO.getUserID();


						userList = userwebDAO.loadUsersListByNameAndOwnerId(con, categorycode, "%" + userName + "%", userID, userID, statusUsed, status, CHANNEL);
					}
				
			}
		} catch (BTSLBaseException e) {

			_log.error(METHOD_NAME, "BTSLBaseException:" + e.getMessage());
			if (_log.isDebugEnabled()) {
				_log.debug("UserBalanceServiceImpl" + METHOD_NAME, e);
			}
		}finally{
			if(mcomCon != null)
			{
				mcomCon.close("UserBalanceServiceImpl#loadUserList");
				mcomCon=null;
			}
		}

		return userList;
	}

	@Override
	public boolean loadUserBalance(Model model, UserModel userModel,
			UserVO userVO, ChannelUserVO channelUserSessionVO, BindingResult bindingResult, HttpServletRequest request) {

		String methodName = "loadUserBalance";
		Connection con = null;
		MComConnectionI mcomCon = null;

		try {

			mcomCon = new MComConnection();
			con=mcomCon.getConnection();
			final ChannelUserDAO channelUserDAO = new ChannelUserDAO();

			ChannelUserVO channelUserVO = null;
			String status = PretupsBL.userStatusNotIn();
			String statusUsed = PretupsI.STATUS_NOTIN;
			UserWebDAO userwebDAO = new UserWebDAO();
			String[] args = new String[2];
			boolean check = false;
			request.getSession().setAttribute("userModel", userModel);

			if(request.getParameter("submitMsisdn")!=null){
				CommonValidator commonValidator=new CommonValidator("configfiles/user/validator-userBalance.xml", userModel, "UserModelMsisdn");
				Map<String, String> errorMessages = commonValidator.validateModel();
				PretupsRestUtil pru=new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult);
				model.addAttribute("formNumber", "Panel-One");
				request.getSession().setAttribute("formNumber", "Panel-One");
			}
			if(request.getParameter("submitLoginId")!=null){
				CommonValidator commonValidator=new CommonValidator("configfiles/user/validator-userBalance.xml", userModel, "UserModelLoginId");
				Map<String, String> errorMessages = commonValidator.validateModel();
				PretupsRestUtil pru=new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult); 
				model.addAttribute("formNumber", "Panel-Two");
				request.getSession().setAttribute("formNumber", "Panel-Two");
			}
			if(request.getParameter("submitUser")!=null){
				CommonValidator commonValidator=new CommonValidator("configfiles/user/validator-userBalance.xml", userModel, "UserModelUserName");
				Map<String, String> errorMessages = commonValidator.validateModel();
				PretupsRestUtil pru=new PretupsRestUtil();
				pru.processFieldError(errorMessages, bindingResult); 
				model.addAttribute("formNumber", "Panel-Three");
				request.getSession().setAttribute("formNumber", "Panel-Three");
			}
			if(bindingResult.hasFieldErrors()){

				return false;
			}
			if (!BTSLUtil.isNullString(userModel.getSearchMsisdn())) {

				if (!BTSLUtil.isValidMSISDN(userModel.getSearchMsisdn())) {
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.btsl.msisdn.error.length"));
					return false;
				}

				final String filteredMSISDN = PretupsBL.getFilteredMSISDN(userModel.getSearchMsisdn());
				NetworkPrefixVO prefixVO;
				prefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(PretupsBL.getFilteredMSISDN(userModel.getSearchMsisdn())));

				if (prefixVO == null || !prefixVO.getNetworkCode().equals(channelUserSessionVO.getNetworkID())) {

					_log.error("loadUserBalance", "Error: MSISDN Number" + userModel.getSearchMsisdn() + " not belongs to " + channelUserSessionVO.getNetworkName() + "network");

					args[0] = userModel.getSearchMsisdn();
					args[1] = userVO.getNetworkName();

					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.user.assignphone.error.msisdnnotinsamenetwork",args));
					return false;
				}


				if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO.getDomainID())) {
					channelUserVO = channelUserDAO.loadUsersDetails(con, filteredMSISDN, null, statusUsed, status);
				} else {
					String userID = channelUserSessionVO.getUserID();

					if (PretupsI.CATEGORY_TYPE_AGENT.equals(channelUserSessionVO.getCategoryVO().getCategoryType())) {
						userID = channelUserSessionVO.getParentID();
					}
					channelUserVO = channelUserDAO.loadUsersDetails(con, filteredMSISDN, userID, statusUsed, status);
				}

				if(channelUserVO!= null){
					final boolean isDomainFlag = userwebDAO.isUserInSameGRPHDomain(con, channelUserVO.getUserID(), channelUserVO.getCategoryVO().getGrphDomainType(),
							channelUserSessionVO.getUserID(), channelUserSessionVO.getCategoryVO().getGrphDomainType());
					if(!isDomainFlag){
						args = new String[] {userModel.getSearchMsisdn() };
						model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.user.selectchanneluserforview.error.usermsisdnnotinsamegeodomain", args));
						return false;
					}
					else{

						userVO = this.loadBalance(channelUserVO, channelUserVO);
						userVO.setNetworkName(channelUserSessionVO.getNetworkName());
					}


				}else{

					args = new String[] {userModel.getSearchMsisdn() };
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.user.selectchanneluserforview.error.usermsisdnnotexist", args));
					return false;

				}


			}
			else if (!BTSLUtil.isNullString(userModel.getSearchLoginId())){

				if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO.getDomainID())) {
					channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(con, userModel.getSearchLoginId(), null, statusUsed, status);
				} else {

					String userID = channelUserSessionVO.getUserID();
					if (PretupsI.CATEGORY_TYPE_AGENT.equals(channelUserSessionVO.getCategoryVO().getCategoryType())) {
						userID = channelUserSessionVO.getParentID();
					}
					channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(con, userModel.getSearchLoginId(), userID, statusUsed, status);


				}

				if(channelUserVO != null){


					if (channelUserSessionVO.getCategoryVO().getCategoryCode().equals(channelUserVO.getCategoryVO().getCategoryCode())) {

						args = new String[] {userModel.getSearchLoginId() };
						model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.user.selectchanneluserforview.error.userloginidatsamelevel", args));
						return false;

					}

					final boolean isDomainFlag = userwebDAO.isUserInSameGRPHDomain(con, channelUserVO.getUserID(), channelUserVO.getCategoryVO().getGrphDomainType(),
							channelUserSessionVO.getUserID(), channelUserSessionVO.getCategoryVO().getGrphDomainType());

					if (isDomainFlag) {


						userVO = this.loadBalance(channelUserVO, channelUserVO);
						userVO.setNetworkName(channelUserSessionVO.getNetworkName());


					} else {

						args = new String[] {userModel.getSearchLoginId() };
						model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.user.selectchanneluserforview.error.userloginidnotinsamegeodomain", args));
						return false;
					}




				}else{

					args = new String[] {userModel.getSearchLoginId() };  
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.user.selectchanneluserforview.error.userloginidnotexist", args));
					return false;

				}



			}else if(!BTSLUtil.isNullString(userModel.getUserId())){

				String userName=userModel.getUserId();
				String dummyUserName = userName;
				String[] userIDParts = null;
				String userID = null;
				String channelUserId = null;
				String[] parts=userName.split("\\(");
				userName = parts[0];
				if(parts.length != 2){
					final String[] arr2 = { userModel.getUserId() };
					_log.error(methodName, "Error: User not exist");
					model.addAttribute("fail", PretupsRestUtil
							.getMessageString("pretups.user.changepin.error.usernamenotexist", arr2));
					return false;
				}
				if(!BTSLUtil.isNullString(parts[1])){
					userIDParts = parts[1].split("\\)");
					userID = userIDParts[0];
				}
				userModel.setUserName(userName);
				if(request.getSession().getAttribute("ownerID") != null){
					userModel.setOwnerID(request.getSession().getAttribute("ownerID").toString());
					}
				String index = request.getSession().getAttribute("index").toString();
	        	String prntDomainCode = request.getSession().getAttribute("prntDomainCode").toString();

	        	UserVO channlUserVO = null;
				List<UserVO> userList = this.loadUserList(channelUserSessionVO, userModel.getChannelCategoryCode(), userModel.getOwnerID(), userName, userModel.getDomainCodeDesc(), prntDomainCode, request, index);
				
				
				if(userList.size() == 1){

					channlUserVO  = userList.get(0); 
					channelUserId = channlUserVO.getUserID();
					boolean rsaRequired = false;
					rsaRequired = BTSLUtil.isRsaRequired(channlUserVO);
					userModel.setRsaRequired(rsaRequired);
					userModel.setUserId(channelUserId);	
				}else if(userList.size()>1){
                    boolean isExist = false;
                    if (!BTSLUtil.isNullString(userID)) {
                    	int userListSize = userList.size();
                        for (int i = 0, k = userListSize; i < k; i++) {
                        	channlUserVO =  userList.get(i);
                            if (channlUserVO.getUserID().equals(userID) && userModel.getUserName().compareTo(channlUserVO.getUserName()) == 0) {
                                userModel.setUserId(channlUserVO.getUserID());
                                userModel.setUserName(channlUserVO.getUserName());
                                isExist = true;
                                break;
                            }
                        }

                    } else {
                    	ChannelUserVO listValueNextVO = null;
                        for (int i = 0, k = userList.size(); i < k; i++) {
                        	channlUserVO = userList.get(i);
                            if (userModel.getUserName().compareTo(channlUserVO.getUserName()) == 0) {
                                if (((i + 1) < k)) {
                                    listValueNextVO = (ChannelUserVO) userList.get(i + 1);
                                    if (userModel.getUserName().compareTo(listValueNextVO.getUserName()) == 0) {
                                        isExist = false;
                                        break;
                                    }
                                    userModel.setUserId(channlUserVO.getUserID());
                                    userModel.setUserName(channlUserVO.getUserName());
                                    
                                    isExist = true;
                                    break;
                                }
                                userModel.setUserId(channlUserVO.getUserID());
                                userModel.setUserName(channlUserVO.getUserName());
                             
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
				else{
					args = new String[] { userModel.getUserId() };
					model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.user.selectparentuser.error.usernotexist",args));
					return false;
				}
				
				if (PretupsI.OPERATOR_TYPE_OPT.equals(channelUserSessionVO.getDomainID())) {
					channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(con, channlUserVO.getLoginID(), null, statusUsed, status);
				} else {
					String userId = channelUserSessionVO.getUserID();
					if (PretupsI.CATEGORY_TYPE_AGENT.equals(channelUserSessionVO.getCategoryVO().getCategoryType())) {
						userId = channelUserSessionVO.getParentID();
					}
					channelUserVO = channelUserDAO.loadUsersDetailsByLoginId(con, channlUserVO.getLoginID(), userId, statusUsed, status);
				}
					final ChannelUserDAO channlUserDAO = new ChannelUserDAO();
					List<UserBalancesVO> userBalanceList = new ArrayList<>();
					List<UserBalancesVO> agentBalanceList = new ArrayList<>();

					userVO = channelUserVO;
					if(channlUserVO.getCategoryVO() != null){
					
						if(PretupsI.AGENT_ALLOWED_YES.equalsIgnoreCase(channlUserVO.getCategoryVO().getAgentAllowed())) {
							
							agentBalanceList = channlUserDAO.loadUserAgentsBalance(con, channlUserVO.getUserID());
							if (agentBalanceList != null && agentBalanceList.isEmpty()) {
								userVO.setAgentBalanceList(agentBalanceList);
							}
						}
					}
						userBalanceList = channlUserDAO.loadUserBalances(con, channelUserSessionVO.getNetworkID(), channelUserSessionVO.getNetworkID(), channlUserVO.getUserID());
						
						if (userBalanceList != null && userBalanceList.isEmpty()) {
							if (agentBalanceList != null && agentBalanceList.isEmpty()) {
								int agentBalanceListSize = agentBalanceList.size();
								for (int i = 0; i < agentBalanceListSize; i++) {
									int userBalanceListSize = userBalanceList.size();
									for (int index1 = 0; index1 < userBalanceListSize; index1++) {
										if (( userBalanceList.get(index1)).getProductShortCode().equals(
												( agentBalanceList.get(i)).getProductShortCode())) {
											( userBalanceList.get(index1)).setAgentBalanceStr(PretupsBL.getDisplayAmount(( agentBalanceList.get(i))
													.getBalance()));
										}
									}
								}
							}

						}
						userVO.setAgentBalanceList(agentBalanceList);
						userVO.setUserBalanceList(userBalanceList);
					
						userVO.setNetworkName(channelUserSessionVO.getNetworkName());
						userModel.setUserId(dummyUserName);
			}
			else{
				model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.user.choose.at.least.one.criteria"));
				return false;
			}

		} catch(Exception e){

			_log.errorTrace("loadUserBalance", e);
		}finally{
			if(mcomCon != null)
			{
				mcomCon.close("UserBalanceServiceImpl#loadUserBalance");
				mcomCon=null;
			}
		}





		if(userVO.getUserBalanceList().size() == 0){
			model.addAttribute(FAIL_KEY, PretupsRestUtil.getMessageString("pretups.user.channeluserviewbalances.label.nodatafound"));
		}    

		request.getSession().setAttribute("sessionUser", userVO);
		model.addAttribute("userVO", userVO);      
		model.addAttribute("module", "userBalance");



		return true;
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

			_log.error(methodName, "Exception:e=" + e);
			if (_log.isDebugEnabled()) {
				_log.debug("UserBalanceServiceImpl" + methodName, e);
			}
		}
		finally{
			if(mcomCon != null)
			{
				mcomCon.close("UserBalanceServiceImpl#"+methodName);
				mcomCon=null;
			}
		}
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

			final UserDAO userDAO = new UserDAO();
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

			_log.error(methodName, "Exception:e=" + e);
			if (_log.isDebugEnabled()) {
				_log.debug("UserBalanceServiceImpl" + methodName, e);
			}
		}
		finally{
			if(mcomCon != null)
			{
				mcomCon.close("UserBalanceServiceImpl#"+methodName);
				mcomCon=null;
			}
		}
		return userList;
	}
}

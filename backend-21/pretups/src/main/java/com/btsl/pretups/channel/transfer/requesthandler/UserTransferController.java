package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.ListValueVO;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.FOCBatchTransferDAO;
import com.btsl.pretups.channel.transfer.businesslogic.UserTransferCountsVO;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferDAO;
import com.btsl.pretups.channel.user.businesslogic.ChannelUserTransferVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.gateway.businesslogic.PushMessage;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.user.businesslogic.UserCategoryVO;
import com.btsl.pretups.user.businesslogic.UserGeoDomainVO;
import com.btsl.pretups.user.businesslogic.UserMessageVO;
import com.btsl.pretups.user.businesslogic.UserMigrationDAO;
import com.btsl.pretups.user.businesslogic.UserMigrationVO;
import com.btsl.pretups.user.businesslogic.UserTransferCountsDAO;
import com.btsl.pretups.user.requesthandler.ChannelSOSSettlementHandler;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserPhoneVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

public class UserTransferController  implements ServiceKeywordControllerI{
	
	private  Log _log = LogFactory.getLog(UserTransferController.class.getName());

	private Connection con=null;
	private MComConnectionI mcomCon = null;
	 	
	 	
	private ChannelUserDAO channelUserDAO= null;
	private ChannelUserTransferVO channelUserTransferVO=null;
	private ChannelUserVO channelToParentDetailVO=null;
	private ChannelUserVO channelUserVO=null;
	private ChannelUserVO channelUserDetailVO=null;
	private	String filteredMsisdn=null;
	private	String[] arr=null;
	private	CategoryDAO categoryDAO=null;
	private	UserDAO userDAO = null;
	private String networkCode;
	private ArrayList domainList=null;
	private boolean domainFound=false;
	private	UserMigrationVO userMigrationVO=null;
	private HashMap<String, UserCategoryVO> _catCodeMap=null;
	private HashMap<String, UserGeoDomainVO> _userGeoDomCodeMap=null;
	private HashMap<String, HashMap<String, UserMessageVO>> _profileGradeMap=null;
	private	HashMap<String, String> _migrationDetailMap=null;
	private	UserMigrationDAO userMigrationDAO=null;
	private	boolean isPendingTxnFound=false;
	private	List<ListValueVO>  errorListMig=null;
	private UserMigrationDAO userMigDAO=null;
	private	List<UserMigrationVO> successUserList=null;
	private String errorMessage=null;
	private	boolean isStatusChangeDone;
	private	boolean isUserStatusupdate=false;
	private String defaultGeoCode ="";
		// parentGeo_Code_DR is added for DIST -- RET Scenario to store the GEOCODE of the SE
	private String parentGeo_Code_DR ="";
	private	CategoryVO _categoryVO=null;
	private	CategoryVO _parentCategoryVO=null;
	private	CategoryVO chlidCategoryVO=null;
	private	HashMap requestHashMap =new HashMap();
		
	 public void process(RequestVO p_requestVO)
		{
		 	final String methodName="process";
			if (_log.isDebugEnabled())
				_log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString());
	
			try
			{
				mcomCon = new MComConnection();con=mcomCon.getConnection();
				channelUserDAO= new ChannelUserDAO();
				channelUserDetailVO=new ChannelUserVO(); 
				channelToParentDetailVO=new ChannelUserVO();
				channelUserTransferVO= new ChannelUserTransferVO();
				userMigrationVO =new UserMigrationVO();
				ArrayList<UserMigrationVO> userMigrationList= new ArrayList<UserMigrationVO>();
				categoryDAO=new CategoryDAO();
				//_extUserDao=new ExtUserDAO();
				 userDAO = new UserDAO();
				userMigrationDAO=new UserMigrationDAO();
				userMigDAO=new UserMigrationDAO();
				 isStatusChangeDone=false;
				//load channel admin detail
				channelUserVO = (ChannelUserVO) p_requestVO.getSenderVO();
				//String userMsisdn=(String)p_requestVO.getRequestMap().get("FROM_USER_MSISDN");	
				//String userMsisdn=(String)p_requestVO.getFromUserMsisdn();
				String networkCode=(String)p_requestVO.getRequestMap().get("NETWORKCODE");
				String status = "'"+PretupsI.USER_STATUS_ACTIVE+"','"+PretupsI.USER_STATUS_SUSPEND+"', '"+PretupsI.USER_STATUS_SUSPEND_REQUEST+"'";
				String statusUsed = PretupsI.STATUS_IN;
				String geocode="";
				String fromUserId="";
					
				
				if(!BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("FROM_USER_MSISDN")))
				{
					String userMsisdn=(String)p_requestVO.getRequestMap().get("FROM_USER_MSISDN");	
					channelUserVO= channelUserDAO.loadChannelUserDetails(con, userMsisdn);
					fromUserId=channelUserVO.getUserID();
					if(channelUserVO==null)
					 {
						throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_MSISDN);
					 }
					
					if (!BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("FROM_USER_LOGINID")))
					{
						if (!channelUserVO.getLoginID().equalsIgnoreCase((String)p_requestVO.getRequestMap().get("FROM_USER_LOGINID")))
						{
							throw new BTSLBaseException(this, "process", PretupsErrorCodesI.USER_DETAIL_NOT_EXIST);
						}
					}
					
					if (!BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("FROM_USER_EXTCODE")))
					{
						if (!channelUserVO.getExternalCode().equalsIgnoreCase((String)p_requestVO.getRequestMap().get("FROM_USER_EXTCODE")))
						{
							throw new BTSLBaseException(this, "process", PretupsErrorCodesI.USER_DETAIL_NOT_EXIST);
						}
					}
					
					
					
				}
				else
				{	
					String fromLoginId=(String)p_requestVO.getRequestMap().get("FROM_USER_LOGINID");	
					channelUserVO=channelUserDAO.loadChnlUserDetailsByLoginID(con, fromLoginId);
					fromUserId=channelUserVO.getUserID();
					
					if(channelUserVO==null)
					 {
						throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
					 }
					
					if (!BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("FROM_USER_EXTCODE")))
					{
						if (!channelUserVO.getExternalCode().equalsIgnoreCase((String)p_requestVO.getRequestMap().get("FROM_USER_EXTCODE")))
						{
							throw new BTSLBaseException(this, "process", PretupsErrorCodesI.USER_DETAIL_NOT_EXIST);
						}
					}
					
					
				}
				   
				   p_requestVO.setNetworkCode(channelUserVO.getNetworkID());
			     
			        //load geographical area
			     GeographicalDomainDAO _geographyDAO = new GeographicalDomainDAO();
			     channelUserVO.setGeographicalAreaList(_geographyDAO.loadUserGeographyList(con,channelUserVO.getUserID(),channelUserVO.getNetworkID()));
			     
			     UserGeographiesVO geodomin = (UserGeographiesVO)channelUserVO.getGeographicalAreaList().get(0);
			     
				channelUserDetailVO=loadAndValidateUserDetails(con,p_requestVO,channelUserDetailVO, status, statusUsed);
				channelToParentDetailVO=loadAndValidateToParentDetails(con,p_requestVO,channelToParentDetailVO,channelUserDetailVO,status,statusUsed);
				// Check the lowest hirerachy in the domain				
				/*String category_cd=channelUserDetailVO.getCategoryCode();
				Boolean isAllowed=categoryDAO.isLowestHierarchyInDomain(con,category_cd);
				if(!isAllowed){
					throw new BTSLBaseException("UserTransferController", "process", PretupsErrorCodesI.INVALID_CATEGORY_NOTLOWEST_IN_HIERARCHY);
				}*/
				
				//UserGeographiesVO geodomin = (UserGeographiesVO)channelUserVO.getGeographicalAreaList().get(0);
				String to_parentGeo_Code=channelToParentDetailVO.getGeographicalCode();
				String to_parentGeo_Code_new = channelToParentDetailVO.getGeographicalCode();
				
				
				
				//UserGeographiesVO geodomin = (UserGeographiesVO)channelToParentDetailVO.getGeographicalAreaList().get(0);
				
				if (_log.isDebugEnabled())
				{
					_log.debug(methodName, "Entered ParentGraphDomainCode: " + to_parentGeo_Code + "to_parentGeo_Code_new "+to_parentGeo_Code_new);
					_log.debug(methodName, "Entered grpDomainType: " + geodomin.getGraphDomainType());
				}
				
				// Default Geo_code Addition by Naveen Beniwal
				boolean isValid_GeoCode=false;
				String actual_networkcode=channelUserVO.getNetworkID();
				p_requestVO.setUserCategory(channelUserDetailVO.getCategoryCode());
				
				if (_log.isDebugEnabled())
				{
					_log.debug(methodName, "Entered p_requestVO.setUserCategory: " + p_requestVO.getUserCategory());
					_log.debug(methodName, "Entered channelToParentDetailVO.getCategoryCode(): " + channelToParentDetailVO.getCategoryCode());
				}
				
				
				//Load Category List for parent
				ArrayList catListparent=new CategoryDAO().loadCategoryDetailsUsingCategoryCode(con,channelToParentDetailVO.getCategoryCode());
				//if null category does not exist
				if(catListparent==null ||catListparent.isEmpty() )
					throw new BTSLBaseException("UserAddController","process",PretupsErrorCodesI.EXTSYS_REQ_USERCATCODE_INVALID);
				//If channel user is not owner of it's category, then it's parent details should not be blank.
				_parentCategoryVO=(CategoryVO)catListparent.get(0);
				
				
				//Load category for user
				ArrayList catList=new CategoryDAO().loadCategoryDetailsUsingCategoryCode(con,p_requestVO.getUserCategory());
				//if null category does not exist
				if(catList==null ||catList.isEmpty() )
					throw new BTSLBaseException("UserAddController","process",PretupsErrorCodesI.EXTSYS_REQ_USERCATCODE_INVALID);
				//If channel user is not owner of it's category, then it's parent details should not be blank.
				_categoryVO=(CategoryVO)catList.get(0);
				
				if (_log.isDebugEnabled())
				{
					_log.debug(methodName, "Entered _categoryVO.getSequenceNumber(): " + _categoryVO.getSequenceNumber());
					_log.debug(methodName, "Entered _parentCategoryVO.getSequenceNumber(): " + _parentCategoryVO.getSequenceNumber());
				}
				
				
				//if (channelToParentDetailVO.getCategoryCode().equalsIgnoreCase("DIST") && p_requestVO.getUserCategory().equalsIgnoreCase("RET"))
				/*if ((_categoryVO.getSequenceNumber() - _parentCategoryVO.getSequenceNumber()) > 1)
				{
					parentGeo_Code_DR_Flag=true;
					parentGeo_Code_DR = _geographyDAO.loadDefaultGeographyUnderParent(con, to_parentGeo_Code, actual_networkcode);
				}
				
				
				if (parentGeo_Code_DR_Flag)
				{
					defaultGeoCode=_extUserDao.getDefaultGeoCodeDtlBasedOnNetworkAndParentGeoCode(con, p_requestVO,actual_networkcode,parentGeo_Code_DR);
				}
				else
				{
					defaultGeoCode=_extUserDao.getDefaultGeoCodeDtlBasedOnNetworkAndParentGeoCode(con, p_requestVO,actual_networkcode,to_parentGeo_Code);
				}*/
				
				// code start getting the default geocode
				
				if ((_categoryVO.getSequenceNumber() - _parentCategoryVO.getSequenceNumber()) == 0)
				{
					if (_log.isDebugEnabled())
						_log.debug(methodName, "parent and child sequence are same");
					defaultGeoCode=to_parentGeo_Code;
				}
				else
				{
					if (_log.isDebugEnabled())
						_log.debug(methodName, "parent and child sequence are different");
					int count = _categoryVO.getSequenceNumber() - _parentCategoryVO.getSequenceNumber();
					if (_log.isDebugEnabled())
						_log.debug(methodName, "Entered count: " + count + " to_parentGeo_Code_new" +to_parentGeo_Code_new);
					for(int i=1;i<= count ;i++)
					{
						parentGeo_Code_DR = _geographyDAO.loadDefaultGeographyUnderParent(con, to_parentGeo_Code_new, actual_networkcode);
						to_parentGeo_Code_new = parentGeo_Code_DR;
						if (_log.isDebugEnabled())
							_log.debug(methodName, "to_parentGeo_Code_new: " + to_parentGeo_Code_new);
					}
					defaultGeoCode=to_parentGeo_Code_new;
					if (_log.isDebugEnabled())
						_log.debug(methodName, "defaultGeoCode after loop: " + defaultGeoCode);
				}
				// code end for getting the default geocode
				
				if (_log.isDebugEnabled())
				_log.debug(methodName, "Entered defaultGeoCode: " + defaultGeoCode);
				
				if(BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("TO_USER_GEOGRAPHICAL_CODE"))){
					//p_requestVO.setToUserGeoCode(defaultGeoCode);
					requestHashMap.put("TO_USER_GEOGRAPHICAL_CODE",defaultGeoCode);
					geocode=defaultGeoCode;
					isValid_GeoCode=true;
				}else
				{
					// logic to validate the passed geocode
					
					//GP_USERADDAPI_DEFAULT_GEOCODE flag is added to move user to default geography in case wrong geography is given
					String userAddGeoDefault =Constants.getProperty("GP_USERADDAPI_DEFAULT_GEOCODE").trim();
					
					if (_log.isDebugEnabled())
						_log.debug(methodName, "Entered grpDomainType " +geodomin.getGraphDomainType());
					
					ArrayList<String> _geoDomainListUnderParent= _geographyDAO.loadGeographyHierarchyUnderParent(con,to_parentGeo_Code,channelUserVO.getNetworkID(),geodomin.getGraphDomainType());
					if(_geoDomainListUnderParent!=null && !_geoDomainListUnderParent.isEmpty())
					{
						if(_geoDomainListUnderParent.contains((String)p_requestVO.getRequestMap().get("TO_USER_GEOGRAPHICAL_CODE")))
						{
							isValid_GeoCode=true;
							geocode=(String)p_requestVO.getRequestMap().get("TO_USER_GEOGRAPHICAL_CODE");
						}
						else
						{
							if(userAddGeoDefault.equalsIgnoreCase((PretupsI.YES)))
									{
										isValid_GeoCode=true;
										geocode=defaultGeoCode;
									}
						}
					}					
				}
				/*if((!BTSLUtil.isNullString(p_requestVO.getToUserGeoCode()) && !p_requestVO.getToUserGeoCode().equals(defaultGeoCode))|| BTSLUtil.isNullString(defaultGeoCode)){
					//throw new BTSLBaseException("UserTransferController", "process", PretupsErrorCodesI.TO_USER_GEOGRAPHY_INVALID);	
				}	
					
				else{
				if(!BTSLUtil.isNullString(defaultGeoCode)){
					//p_requestVO.setToUserGeoCode(defaultGeoCode);
				    }
				}		*/				
				// is domain code is same as user doamin
				if(channelUserDetailVO.getDomainName().equals(channelToParentDetailVO.getDomainName()))
				{
					// check whether from and to category code and geo code are equal
					if(channelUserDetailVO.getCategoryCode().equals((String)p_requestVO.getRequestMap().get("TO_USER_CATEGORY_CODE")))
					{
						/*if(!channelUserDetailVO.getGeographicalCode().equals(p_requestVO.getToUserGeoCode()))
						{
							p_requestVO.setMessageCode(PretupsErrorCodesI.TO_USER_GEOGRAPHY_INVALID);
							throw new BTSLBaseException("UserTransferController", "loadAndValidateUserDetails", PretupsErrorCodesI.TO_USER_GEOGRAPHY_INVALID);
						}*/
						if(!isValid_GeoCode)
						{
							p_requestVO.setMessageCode(PretupsErrorCodesI.TO_USER_GEOGRAPHY_INVALID);
							throw new BTSLBaseException("UserTransferController", "loadAndValidateUserDetails", PretupsErrorCodesI.TO_USER_GEOGRAPHY_INVALID);
						}
					}
					else
					{
						p_requestVO.setMessageCode(PretupsErrorCodesI.TO_USER_CATEGORY_INVALID);
						throw new BTSLBaseException("UserTransferController", "loadAndValidateUserDetails", PretupsErrorCodesI.TO_USER_CATEGORY_INVALID);
					}
					channelUserDetailVO.setPreviousStatus(channelUserDetailVO.getStatus());
					
					/*if(channelUserDetailVO.getStatus().equals(PretupsI.STATUS_ACTIVE)){
					//p_con=OracleUtil.getConnection();
					isUserStatusupdate=userDAO.userStatusUpdate(con,channelUserDetailVO.getUserID(),channelUserDetailVO.getStatus());
					//p_con.close();
					}*/
					
					if(isUserStatusupdate){
						isStatusChangeDone=true;
					}
									 // load transfer user hierarchy
					arr = new String[1];
					arr[0]=channelUserDetailVO.getUserID();
					channelUserTransferVO.setUserHierarchyList(channelUserDAO.loadUserHierarchyListForTransfer(con,arr,PretupsI.SINGLE,statusUsed,status,channelUserDetailVO.getCategoryCode()));
					
				
				ArrayList userHierarchyList=channelUserTransferVO.getUserHierarchyList();
				
				if (_log.isDebugEnabled())
					_log.debug(methodName, "userHierarchyList size " +userHierarchyList.size());
					
			 
			// now check that any pending O2C/FOC or Batch FOC transaction is exist
				if(userHierarchyList!=null && !userHierarchyList.isEmpty())
				{
					channelUserTransferVO.setIsOperationNotAllow(false);
					 int userHierarchySize = userHierarchyList.size();
					ChannelUserVO channelUserVO2 = null;
					ChannelTransferDAO channelTransferDAO=new ChannelTransferDAO();
					FOCBatchTransferDAO batchTransferDAO = new FOCBatchTransferDAO();
					for(int i=0,j= userHierarchySize;i<j;i++)
					{
						channelUserVO2=(ChannelUserVO)userHierarchyList.get(i);
						if(((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_SOS_ENABLE)).booleanValue())
						{
					        // Checking SOS Pending transactions
					        ChannelSOSSettlementHandler channelSOSSettlementHandler = new ChannelSOSSettlementHandler();
					        boolean isSOSPendingFlag = channelSOSSettlementHandler.validateSOSPending(con,channelUserVO2.getUserID());
					        if (isSOSPendingFlag) {
								channelUserTransferVO.setIsOperationNotAllow(true);
								p_requestVO.setMessageCode(PretupsErrorCodesI.SOS_SETTLEMENT_PENDING);
								throw new BTSLBaseException("UserTransferController", "process", PretupsErrorCodesI.SOS_SETTLEMENT_PENDING);
					        }
						}
						if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LR_ENABLED))).booleanValue()){
		                    UserTransferCountsVO userTrfCntVO = new UserTransferCountsVO();
		                    UserTransferCountsDAO userTrfCntDAO = new UserTransferCountsDAO();
		                    userTrfCntVO = userTrfCntDAO.selectLastLRTxnID(channelUserVO2.getUserID(), con, false, null);
		                    if (userTrfCntVO!=null) {
								channelUserTransferVO.setIsOperationNotAllow(true);
								p_requestVO.setMessageCode(PretupsErrorCodesI.LR_SETTLEMENT_PENDING);
								throw new BTSLBaseException("UserTransferController", "process", PretupsErrorCodesI.LR_SETTLEMENT_PENDING);
								}
						}
						if(channelTransferDAO.isPendingTransactionExist(con,channelUserVO2.getUserID()))
						{
							isPendingTxnFound=true;
							break;
						}
						else if(batchTransferDAO.isPendingTransactionExist(con,channelUserVO2.getUserID()))
						{
							isPendingTxnFound=true;
							break;
						}
					}
					if(isPendingTxnFound)
					{
						channelUserTransferVO.setIsOperationNotAllow(true);
						p_requestVO.setMessageCode(PretupsErrorCodesI.PENDING_TXN_FOR_USER_HIERARCHY);
						throw new BTSLBaseException("UserTransferController", "process", PretupsErrorCodesI.PENDING_TXN_FOR_USER_HIERARCHY);
					}
				}
				// Suspend the complete user hierarchy that is any non suspended user exist in the hierarcy if so suspend them
				
				if(userHierarchyList!=null && !userHierarchyList.isEmpty())
				{
					int updateCount=0;
					//p_con_s=OracleUtil.getConnection();
					ChannelUserWebDAO channelUserWebDAO= new ChannelUserWebDAO();
					channelUserTransferVO.setIsOperationNotAllow(false);
				
					ChannelUserVO channelUserVO1 = null;
					ChannelUserVO channelUserVO3 = new ChannelUserVO();
					//String defGeoCodeUndrparent = _geographyDAO.loadDefaultGeographyUnderParent(con, geocode, actual_networkcode);
					
					/*if (_log.isDebugEnabled())
						_log.debug(methodName, "defGeoCodeUndrparent " +defGeoCodeUndrparent);*/
					
					Date currentDate = new Date();
					channelUserVO3.setModifiedBy("SYSTEM");
					channelUserVO3.setModifiedOn(currentDate);
					
					// change status to suspend
					updateCount=channelUserWebDAO.changeChannelUserStatus(con,userHierarchyList,channelUserVO3, PretupsI.USER_STATUS_SUSPEND);
					if (con != null)
					{

	    		        if (updateCount > 0) 
	    	            {
	    		        	con.commit();
							
	    	            }
	    	            else 
	    	            {
	    	            	con.rollback();
	    	                //BTSLMessages btslMessage = new BTSLMessages("channeluser.userhierarchyaction.msg.suspendfail","selectfromowner");
	    					//forward = this.handleMessage(btslMessage,request,mapping);
	    					p_requestVO.setMessageCode(PretupsErrorCodesI.COMPLETE_USER_HIERARCHY_NOT_SUSPENDED);
							throw new BTSLBaseException("UserTransferController", "loadAndValidateUserDetails", PretupsErrorCodesI.COMPLETE_USER_HIERARCHY_NOT_SUSPENDED);
	    			    }
					}
                   int userListsSize = userHierarchyList.size();
					for(int i=0,j= userListsSize;i<j;i++)
					{
						String childGeoCode="";
						String childGeoCodeNew="";
						channelUserVO1=(ChannelUserVO)userHierarchyList.get(i);
						
						// check if SE then its geocode added else SE default geo code is added for child
						/*if (channelUserVO1.getParentID().equals(channelUserVO1.getOwnerID()))
						{
							System.out.println("channelUserVO1.getParentID()"+channelUserVO1.getParentID() + "channelUserVO1.getOwnerID() " +channelUserVO1.getOwnerID() );
							channelUserVO1.setGeographicalCode(geocode);
							System.out.println("geocode1111"+geocode);
							//geocode=geocode;
							
						}
						else
						{
							channelUserVO1.setGeographicalCode(defGeoCodeUndrparent);
							System.out.println("defGeoCodeUndrparent===="+defGeoCodeUndrparent);
							//geocode=defGeoCodeUndrparent;
						}*/
						
						// code changes to get geocode for hierarchy
						
						// compare the from_user with the user in hierarchy list
						
						if (channelUserVO1.getUserID().equalsIgnoreCase(fromUserId))
						{
							channelUserVO1.setGeographicalCode(geocode);
							
							if (_log.isDebugEnabled())
								_log.debug(methodName, "from user geocode"+geocode);
						}
						else
						{
							
							//Load Category List for childs
							ArrayList catListForChild=new CategoryDAO().loadCategoryDetailsUsingCategoryCode(con,channelUserVO1.getCategoryCode());
							//if null category does not exist
							if(catListForChild==null ||catListForChild.isEmpty() )
								throw new BTSLBaseException("UserAddController","process",PretupsErrorCodesI.EXTSYS_REQ_USERCATCODE_INVALID);
							
							chlidCategoryVO=(CategoryVO)catListForChild.get(0);
							childGeoCodeNew=geocode;
							
							if (_log.isDebugEnabled())
								_log.debug(methodName, "parent and childs in hierarchy sequence are different" + _categoryVO.getSequenceNumber() +"chlidCategoryVO.getSequenceNumber() " +chlidCategoryVO.getSequenceNumber());
							int count = chlidCategoryVO.getSequenceNumber() - _categoryVO.getSequenceNumber();
							if (_log.isDebugEnabled())
								_log.debug(methodName, "Entered count: " + count + " childGeoCode" +childGeoCode);
							for(int m=1;m<=count;m++)
							{
								childGeoCode = _geographyDAO.loadDefaultGeographyUnderParent(con, childGeoCodeNew, actual_networkcode);
								childGeoCodeNew = childGeoCode;
								if (_log.isDebugEnabled())
									_log.debug(methodName, "childGeoCodeNew: " + childGeoCodeNew);
							}
							
							channelUserVO1.setGeographicalCode(childGeoCodeNew);
							if (_log.isDebugEnabled())
								_log.debug(methodName, "childGeoCodeNew after loop: " + childGeoCodeNew);
							
						}
					}
					//p_con_s.close();
					
				}
				
				
				// Construct Common VO containing details of user and to_parent
				channelUserTransferVO.setUserCategoryCode((String)p_requestVO.getRequestMap().get("TO_USER_CATEGORY_CODE"));
				channelUserTransferVO.setToOwnerID(channelToParentDetailVO.getOwnerID());
				channelUserTransferVO.setToParentID(channelToParentDetailVO.getUserID());
				channelUserTransferVO.setFromOwnerID(channelUserDetailVO.getOwnerID());
				channelUserTransferVO.setFromParentID(channelUserDetailVO.getParentID());
				channelUserTransferVO.setDomainCode(channelUserDetailVO.getCategoryVO().getDomainCodeforCategory());
				channelUserTransferVO.setZoneCode(channelUserDetailVO.getGeographicalCode());
				channelUserTransferVO.setParentUserName(channelUserDetailVO.getParentName());
				channelUserTransferVO.setToParentUserName(channelToParentDetailVO.getUserName());
				
				channelUserTransferVO.setGeographicalCode(geocode);
				if (_log.isDebugEnabled())
					_log.debug(methodName, "channelUserTransferVO  GeographicalCode " + geocode);
				
								
				// call transfer
				this.transferUser(con,channelUserVO,channelUserTransferVO,p_requestVO);
				requestHashMap.put("ACROSS_DOMAIN",false);
				p_requestVO.setRequestMap(requestHashMap);
				
				}
				else
				{										// User Transfer across domain 
					
					if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.USR_MOV_ACROSS_DOM_ALLOW))).booleanValue())
					{
						
						requestHashMap.put("ACROSS_DOMAIN",true);
						//load required maps
						_profileGradeMap=new HashMap<String, HashMap<String, UserMessageVO>>();
						_migrationDetailMap=new HashMap<String, String>();
						_catCodeMap=userMigrationDAO.loadCategoryMap(con, _profileGradeMap,channelUserVO.getNetworkID());
						_userGeoDomCodeMap=userMigrationDAO.loadGeoDomainCode(con);
											
						userMigrationVO =new UserMigrationVO();	
						userMigrationVO.setDomainID(channelUserDetailVO.getDomainID());
						userMigrationVO.setNetworkID(channelUserVO.getNetworkID());
						userMigrationVO.setFromParentMsisdn(channelUserDetailVO.getParentMsisdn());//From Parent MSISDN 
						userMigrationVO.setFromParentGeoCode(channelUserDetailVO.getParentGeographyCode());//From Parent geographical domain code.
						userMigrationVO.setFromUserMsisdn(channelUserDetailVO.getMsisdn());//From user MSISDN
						userMigrationVO.setFromUserCatCode(channelUserDetailVO.getCategoryCode().toUpperCase());//From user category code
						userMigrationVO.setToParentMsisdn(channelToParentDetailVO.getMsisdn());//To parent MSISDN
						userMigrationVO.setToParentGeoCode(channelToParentDetailVO.getGeographicalCode());//To parent geographical domain code
						userMigrationVO.setToParentCatCode(channelToParentDetailVO.getCategoryCode());//To parent category code
						userMigrationVO.setToUserGeoCode((String)p_requestVO.getRequestMap().get("TO_USER_GEOGRAPHICAL_CODE"));//To user geographical code
						userMigrationVO.setToUserCatCode((String)p_requestVO.getRequestMap().get("TO_USER_CATEGORY_CODE"));//To user category code.
						userMigrationList.add(userMigrationVO);	
						
						if(userMigrationList != null && !userMigrationList.isEmpty())
						{
							successUserList =userMigrationDAO.validateFromUsers(con, userMigrationList, p_requestVO);
							if(successUserList==null || successUserList.isEmpty() )
								throw new BTSLBaseException("UserTransferController", "process", PretupsErrorCodesI.FROM_USER_VALIDATION_FAIL);
						
							 //mark user hierarchy as NP
							ArrayList<UserMigrationVO> npusers=new ArrayList<UserMigrationVO>() ;
							
								UserMigrationVO marknpuserMigrationVO = null;
								int userMigrationSize = userMigrationList.size();
								for(int j=0;j< userMigrationSize;j++)
								{
									marknpuserMigrationVO = (UserMigrationVO)userMigrationList.get(j);
									userMigrationDAO.markNpUsers(con, marknpuserMigrationVO.getFromUserID(), marknpuserMigrationVO.getFromUserMsisdn());	//Marking Users for NP status.
									npusers.add(marknpuserMigrationVO);
								}
								
								errorListMig = migrateUsers(con, userMigrationList);//Calling Migration Process
								if(errorListMig!=null && !errorListMig.isEmpty())
								{	
									java.util.Iterator<ListValueVO> it = errorListMig.iterator(); //transfer fail and show error msg
									while(it.hasNext())
									{
										 errorMessage+=it.next().getOtherInfo2();
									}
									p_requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_FAIL);
									requestHashMap.put("ERROR_MSG",errorMessage);
									throw new BTSLBaseException("UserTransferController", "process", PretupsErrorCodesI.USER_TRANSFERE_FAIL);
								}
								else
								{
									//success
									if (_log.isDebugEnabled())
										_log.debug(methodName , PretupsErrorCodesI.USER_TRANSFERE_SUCCESS);
								 p_requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
								requestHashMap.put("ERROR_MSG","User is successfully transfered.");
						
							}
						}
						p_requestVO.setRequestMap(requestHashMap);
					}
		 
				}
			}
			catch(BTSLBaseException be)
			{
				p_requestVO.setSuccessTxn(false);				
				try	{if (con != null){con.rollback();}}
				catch (Exception e){
					_log.errorTrace(methodName,e);
				}
				_log.error("process", "BTSLBaseException " + be.getMessage());
				_log.errorTrace(methodName,be);
				if(be.isKey())
				{
					p_requestVO.setMessageCode(be.getMessageKey());
					p_requestVO.setMessageArguments(be.getArgs());
				}
				else
				{
					p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
					return;
				}
			}
			catch(Exception e)
			{				
				p_requestVO.setSuccessTxn(false);
				try	{if (con != null){con.rollback();}}catch (Exception ee){
					_log.errorTrace(methodName,ee);
				}
				_log.error("process", "Exception " + e.getMessage());
				_log.errorTrace(methodName,e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UserTransferController[process]","","","","Exception:"+e.getMessage());
				p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
				return;
			}
			finally
			{
				 if (_log.isDebugEnabled())
					 _log.debug(methodName," Exiting");
				 if(!p_requestVO.isSuccessTxn()){
					 if(isStatusChangeDone){
							try {
								//p_con=OracleUtil.getConnection();
								userDAO.userStatusUpdate(con,channelUserDetailVO.getUserID(),PretupsI.STATUS_SUSPEND);
								//p_con.close();
							}/*catch (SQLException e) {
								e.printStackTrace();
								_log.error(methodName, "Exception " + e.getMessage());
							}*/ 
							catch (Exception e) {
							
								_log.error(methodName, "Exception:e=" + e.getMessage());
								_log.errorTrace(methodName, e);
							}
						} 
			}
			if (mcomCon != null) {
				mcomCon.close("UserTransferController#process");
				mcomCon = null;
			}
			}
		}
	 
	
	public ChannelUserVO loadAndValidateUserDetails(Connection con,RequestVO p_requestVO,ChannelUserVO channelUserDetailVO,String status,String statusUsed) throws Exception 
	{
		final String methodName="loadAndValidateUserDetails";
		if (_log.isDebugEnabled())
			_log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString()+"channelUserDetailVO"+channelUserDetailVO.toString()+"status"+status+"statusUsed"+statusUsed);
		
		try 
		{
		if(BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("FROM_USER_MSISDN")))
		{
			/*String msisdn = (String)p_requestVO.getRequestMap().get("fromUserExtCode");
			filteredMsisdn=PretupsBL.getFilteredMSISDN(msisdn);*/
			
			
			String fromUserLoginId = (String)p_requestVO.getRequestMap().get("FROM_USER_LOGINID");
			channelUserVO=channelUserDAO.loadChnlUserDetailsByLoginID(con, fromUserLoginId);
			
			if(channelUserVO==null)
			 {
				throw new BTSLBaseException(this, "process", PretupsErrorCodesI.EXT_XML_ERROR_INVALID_LOGINID);
			 }
			
			String msisdn = channelUserVO.getMsisdn();
			filteredMsisdn=PretupsBL.getFilteredMSISDN(msisdn);
			
			if (_log.isDebugEnabled())
				_log.debug("loadAndValidateUserDetails", "filteredMsisdn===="+filteredMsisdn);
			
			
						
		}else
		{
		String msisdn = (String)p_requestVO.getRequestMap().get("FROM_USER_MSISDN");
		filteredMsisdn=PretupsBL.getFilteredMSISDN(msisdn);
		}
		String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
		ListValueVO listValueVO=null;
		NetworkPrefixVO networkPrefixVO=null;
		channelUserDetailVO=channelUserDAO.loadUsersDetails(con,filteredMsisdn,null,statusUsed,status);
		if(channelUserDetailVO==null)
		{
			p_requestVO.setMessageCode(PretupsErrorCodesI.USER_DETAIL_NOT_EXIST);
			throw new BTSLBaseException("UserTransferController", "loadAndValidateUserDetails", PretupsErrorCodesI.USER_DETAIL_NOT_EXIST);
		}
		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_USER_MNP_ALLOW))).booleanValue())
		{			 
			 listValueVO=PretupsBL.validateChannelUserForMNP(filteredMsisdn);	
			 if(listValueVO!=null)
				{
				networkPrefixVO=new NetworkPrefixVO();
				networkPrefixVO.setNetworkCode(listValueVO.getCodeName());
				networkPrefixVO.setListValueVO(listValueVO);
				}			 
		}
		
		if(listValueVO==null){
            networkPrefixVO=(NetworkPrefixVO)NetworkPrefixCache.getObject(msisdnPrefix);
		}
		if(networkPrefixVO==null)
		{
			p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_NETWORK);
			throw new BTSLBaseException("UserTransferController", "loadAndValidateUserDetails", PretupsErrorCodesI.INVALID_NETWORK);
		}
		networkCode=networkPrefixVO.getNetworkCode();
		if(networkCode==null || !networkCode.equals(channelUserVO.getNetworkID()))
		{
			p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_NETWORK);
			throw new BTSLBaseException("UserTransferController", "loadAndValidateUserDetails", PretupsErrorCodesI.INVALID_NETWORK);
		}
		//check user domain found in logging user domain list 
		domainList=channelUserVO.getDomainList();
        if(domainList!=null && !domainList.isEmpty())
		{
			ListValueVO listValVO=null;
			domainFound=false;
			
			for(int i=0,j=domainList.size();i<j;i++)
			{
				listValVO=(ListValueVO)domainList.get(i);
				if(channelUserDetailVO.getCategoryVO().getDomainCodeforCategory().equals(listValVO.getValue()))
				{
					domainFound=true;
					break;
				}
			}
			if(!domainFound)
			{
				p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_DOMAIN);
				throw new BTSLBaseException("UserTransferController", "loadAndValidateUserDetails", PretupsErrorCodesI.INVALID_DOMAIN);
			}
		}
		
		//now check that is user down in the geographical domain of the loggin user or not.
		/*GeographicalDomainDAO geographicalDomainDAO =new GeographicalDomainDAO();
		
		if(!geographicalDomainDAO.isGeoDomainExistInHierarchy(con,channelUserDetailVO.getGeographicalCode(),channelUserVO.getUserID()))
		{
			p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_GEOGRAPHY);
			throw new BTSLBaseException("UserTransferController", "loadAndValidateUserDetails", PretupsErrorCodesI.INVALID_GEOGRAPHY);
		}*/
		}

		catch(BTSLBaseException be)
		{
			_log.errorTrace(methodName, be);
			throw be;
		}
		catch(Exception e)
		{
			_log.error(methodName, "Exception " + e.getMessage());
			   _log.errorTrace(methodName, e);
		
			
		}
		finally
		{
			if (_log.isDebugEnabled())
				_log.debug("loadUserDetail", "Exit");
		}
		return channelUserDetailVO;
	}
	
	public ChannelUserVO loadAndValidateToParentDetails(Connection con,RequestVO p_requestVO,ChannelUserVO channelToParentDetailVO,ChannelUserVO channelUserDetailVO,String status,String statusUsed) throws BTSLBaseException 
	{
		final String methodName="loadAndValidateToParentDetails";
		if (_log.isDebugEnabled())
			_log.debug(methodName, "Entered p_requestVO: " + p_requestVO.toString() +"channelToParentDetailVO"+ channelToParentDetailVO.toString()+"channelUserDetailVO"+ channelUserDetailVO.toString()+"status"+status+"statusUsed"+statusUsed);
		try
		{
			if(BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("TO_PARENT_MSISDN")))
			{
					//String msisdn = (String)p_requestVO.getRequestMap().get("FROM_USER_EXTCODE");
					String toParentLoginId = (String)p_requestVO.getRequestMap().get("TO_PARENT_LOGINID");
					channelUserVO=channelUserDAO.loadChnlUserDetailsByLoginID(con, toParentLoginId);
					
					if(channelUserVO==null)
					 {
						throw new BTSLBaseException(this, "process", PretupsErrorCodesI.PARENT_DETAIL_NOT_EXIST);
					 }
					
					String msisdn = channelUserVO.getMsisdn();
					filteredMsisdn=PretupsBL.getFilteredMSISDN(msisdn);
					
					if (_log.isDebugEnabled())
						_log.debug("loadAndValidateToParentDetails", "filteredMsisdn===="+filteredMsisdn);
			}else
			if(!BTSLUtil.isNullString( (String)p_requestVO.getRequestMap().get("TO_PARENT_MSISDN"))) 
			{
			String msisdn = (String)p_requestVO.getRequestMap().get("TO_PARENT_MSISDN");
			filteredMsisdn=PretupsBL.getFilteredMSISDN(msisdn);
			}
			String msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn);
			ListValueVO listValueVO=null;
			NetworkPrefixVO networkPrefixVO=null;
			channelToParentDetailVO=channelUserDAO.loadUsersDetails(con,filteredMsisdn,null,statusUsed,status);
			if(channelToParentDetailVO==null)
			{
				p_requestVO.setMessageCode(PretupsErrorCodesI.PARENT_DETAIL_NOT_EXIST);
				throw new BTSLBaseException("UserTransferController", "loadAndValidateToParentDetails", PretupsErrorCodesI.PARENT_DETAIL_NOT_EXIST);
			}
			
			if (!BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("TO_PARENT_LOGINID")))
			{
				if (!channelToParentDetailVO.getLoginID().equalsIgnoreCase((String)p_requestVO.getRequestMap().get("TO_PARENT_LOGINID")))
				{
					throw new BTSLBaseException(this, "process", PretupsErrorCodesI.PARENT_DETAIL_NOT_EXIST);
				}
			}
			
			if (!BTSLUtil.isNullString((String)p_requestVO.getRequestMap().get("TO_PARENT_EXTCODE")))
			{
				if (!channelToParentDetailVO.getExternalCode().equalsIgnoreCase((String)p_requestVO.getRequestMap().get("TO_PARENT_EXTCODE")))
				{
					throw new BTSLBaseException(this, "process", PretupsErrorCodesI.PARENT_DETAIL_NOT_EXIST);
				}
			}
			
			
			
			if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CHANNEL_USER_MNP_ALLOW))).booleanValue())
			{			 
				 listValueVO=PretupsBL.validateChannelUserForMNP(filteredMsisdn);	
				 if(listValueVO!=null)
					{
					networkPrefixVO=new NetworkPrefixVO();
					networkPrefixVO.setNetworkCode(listValueVO.getCodeName());
					networkPrefixVO.setListValueVO(listValueVO);
					}			 
			}
			
			if(listValueVO==null){
	            networkPrefixVO=(NetworkPrefixVO)NetworkPrefixCache.getObject(msisdnPrefix);
			}
			//parent belongs to same network as of logging user
			if(networkPrefixVO==null)
			{
				p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_NETWORK);
				throw new BTSLBaseException("UserTransferController", "loadAndValidateToParentDetails", PretupsErrorCodesI.INVALID_NETWORK);
			}
			 networkCode=networkPrefixVO.getNetworkCode();
			if(networkCode==null || !networkCode.equals(channelUserVO.getNetworkID()))
			{
				p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_NETWORK);
				throw new BTSLBaseException("UserTransferController", "loadAndValidateToParentDetails", PretupsErrorCodesI.INVALID_NETWORK);
			}
			//check domain code is under logging user
			 domainList=channelUserVO.getDomainList();
			if(domainList!=null && !domainList.isEmpty())
			{
				ListValueVO listValVO=null;
				domainFound=false;
				
				for(int i=0,j=domainList.size();i<j;i++)
				{
					listValVO=(ListValueVO)domainList.get(i);
					if(channelToParentDetailVO.getCategoryVO().getDomainCodeforCategory().equals(listValVO.getValue()))
					{
						domainFound=true;
						break;
					}
				}
				if(!domainFound)
				{
					p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_DOMAIN);
					throw new BTSLBaseException("UserTransferController", "loadAndValidateToParentDetails", PretupsErrorCodesI.INVALID_DOMAIN);
				}
			}
			
			/*//now check that is user down in the geographical domain of the loggin user or not.
			GeographicalDomainDAO geographicalDomainDAO =new GeographicalDomainDAO();
			if(!geographicalDomainDAO.isGeoDomainExistInHierarchy(con,channelUserDetailVO.getGeographicalCode(),channelUserVO.getUserID()))
			{
				p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_GEOGRAPHY);
				throw new BTSLBaseException("UserTransferController", "loadAndValidateToParentDetails", PretupsErrorCodesI.INVALID_GEOGRAPHY);
			}*/
			//loading the parent categories under which the users have to be transfered.

			ArrayList toParentCategoryList=categoryDAO.loadTransferRulesCategoryListXml(con,channelUserVO.getNetworkID(),(String)p_requestVO.getRequestMap().get("TO_USER_CATEGORY_CODE"));
			if(toParentCategoryList==null || toParentCategoryList.isEmpty())
			{
				p_requestVO.setMessageCode(PretupsErrorCodesI.USER_CANNOT_BE_TRANSFER);
				throw new BTSLBaseException("UserTransferController", "loadAndValidateToParentDetails", PretupsErrorCodesI.USER_CANNOT_BE_TRANSFER);
			}
			
			if(!toParentCategoryList.contains(channelToParentDetailVO.getCategoryCode()))
			{
				p_requestVO.setMessageCode(PretupsErrorCodesI.TOPARENT_INVALID);
				throw new BTSLBaseException("UserTransferController", "loadAndValidateToParentDetails", PretupsErrorCodesI.TOPARENT_INVALID);
			}
			// check if user parent is same as toParent
			
			if(channelUserDetailVO.getParentID().equals(channelToParentDetailVO.getUserID()))
			{
				p_requestVO.setMessageCode(PretupsErrorCodesI.FROM_PARENT_SAME_TO_PARENT);
				 throw new BTSLBaseException("UserTransferController", "loadAndValidateToParentDetails", PretupsErrorCodesI.FROM_PARENT_SAME_TO_PARENT);
			}
			
			
		}
		catch(BTSLBaseException be)
		{
			_log.error(methodName, "Exception " + be);
			   _log.errorTrace(methodName, be);
			
			throw be;
		}
		catch(Exception e)
		{
			_log.error(methodName, "Exception " + e.getMessage());
		}
		finally
		{
			if (_log.isDebugEnabled())
				_log.debug("loadAndValidateToParentDetails", "Exit");
		}
		return channelToParentDetailVO;
	}
	
	
    /**
     * Method transferUser.
     * This is the method which is called to transfer the users form one parent to other parent.
     * @param mapping ActionMapping
     * @param form ActionForm
     * @param request HttpServletRequest
     * @return ActionForward
     * @throws BTSLBaseException 
     */
    private void transferUser(Connection con,ChannelUserVO channelUserVO,ChannelUserTransferVO channelUserTransferVO,RequestVO p_requestVO) throws BTSLBaseException 
    {
    	final String methodName="transferUser";
        if (_log.isDebugEnabled())
			_log.debug(methodName,"Entered : channelUserVO"+channelUserVO.toString()+"channelUserTransferVO"+channelUserTransferVO.toString());
		ChannelUserTransferDAO channelUserTransferDAO=null;;
		int updateCount=0;
        try 
        {
			
			Date currentDate = new Date();
			channelUserTransferVO.setCreatedBy(channelUserVO.getUserID());
			channelUserTransferVO.setCreatedOn(currentDate);
			channelUserTransferVO.setModifiedBy(channelUserVO.getUserID());
			channelUserTransferVO.setModifiedOn(currentDate);
			channelUserTransferVO.setNetworkCode(channelUserVO.getNetworkID());
			channelUserTransferDAO = new ChannelUserTransferDAO();
			getNewGeographicalDomainCode(con,channelUserTransferVO);
			
			if(channelUserTransferDAO.isMSISDNExist(con,channelUserTransferVO))
        	{
		            _log.error("transferUser","Error: User already transfered");
		            p_requestVO.setMessageCode(PretupsErrorCodesI.USER_ALREADY_MOVED);
		            throw new BTSLBaseException("UserTransferController", "loadAndValidateToParentDetails", PretupsErrorCodesI.USER_ALREADY_MOVED);
          	}
			
			updateCount=channelUserTransferDAO.transferChannelUser(con,channelUserTransferVO);
			if (con != null)
			{
				if (updateCount> 0)
				{
					con.commit();
				
					//sending the SMS to all of the users of their transfer from one parent to other parent.
					ChannelUserVO userVO = null;
					ArrayList userHierarchyList = channelUserTransferVO.getUserHierarchyList();
					String networkCode=channelUserVO.getNetworkID();
					String []arr=new String[2];
					arr[0]=channelUserTransferVO.getParentUserName();
					arr[1] =channelUserTransferVO.getToParentUserName();
					StringBuffer sbf = new StringBuffer();
					for(int i=0,j=userHierarchyList.size();i<j;i++)
					{
						userVO=(ChannelUserVO)userHierarchyList.get(i);
						if(userVO.getUserlevel().equals("1"))
						{
							sbf.append(userVO.getUserName());
							sbf.append(",");
							Locale locale= new Locale(userVO.getUserPhoneVO().getPhoneLanguage(),userVO.getUserPhoneVO().getCountry());
							BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.CHANNELUSER_TRANSFERUSERHIERARCHY_MSG_SUCCESS_WITHOUTPRODUCT,arr);
							PushMessage pushMessage=new PushMessage(userVO.getMsisdn(),btslMessage,null,null,locale,networkCode); 
							pushMessage.push();
						}
					}	
					
					//load user Phone Vo of parent users
					
					//UserDAO userDAO = new UserDAO();
					UserPhoneVO userPhoneVO=null;
					userPhoneVO=userDAO.loadUserPhoneVO(con,channelUserTransferVO.getFromParentID());
					if(userPhoneVO!=null)
					{
						Locale locale2 = new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry());
						BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.CHANNELUSER_TRANSFERUSERHIERARCHY_SENDER_PARENT,new String[]{sbf.substring(0,sbf.length()-1)});
						PushMessage pushMessage=new PushMessage(userPhoneVO.getMsisdn(),btslMessage,null,null,locale2,networkCode); 
						pushMessage.push();
					}
					
					userPhoneVO=userDAO.loadUserPhoneVO(con,channelUserTransferVO.getToParentID());
					if(userPhoneVO!=null)
					{
						Locale locale3 = new Locale(userPhoneVO.getPhoneLanguage(),userPhoneVO.getCountry());
						BTSLMessages btslMessage = new BTSLMessages(PretupsErrorCodesI.CHANNELUSER_TRANSFERUSERHIERARCHY_RECEIVER_PARENT,new String[]{sbf.substring(0,sbf.length()-1)});
						PushMessage pushMessage=new PushMessage(userPhoneVO.getMsisdn(),btslMessage,null,null,locale3,networkCode); 
						pushMessage.push();
					}										
					 if (_log.isDebugEnabled())
							_log.debug(methodName , PretupsErrorCodesI.USER_TRANSFERE_SUCCESS);
					 p_requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				}
				else
				{
					con.rollback();
					 p_requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_FAIL);
					throw new BTSLBaseException("UserTransferController", "loadAndValidateToParentDetails", PretupsErrorCodesI.USER_TRANSFERE_FAIL);
				}
			}
		} 
        catch(BTSLBaseException be)
		{
			_log.errorTrace(methodName, be);;
			throw be;
		}
        catch (Exception e)
        {
        	try{
        		if(con!=null) 
        			con.rollback();
        		}
        	catch(Exception e1){
        		 _log.error(methodName,"Exceptin:e="+e1);
        	}
            _log.error(methodName,"Exceptin:e="+e);
            
        } 
        finally
        {
            if (_log.isDebugEnabled())
				_log.debug(methodName , "Exiting");
        }
       
    }
    
    
    /**
	 * Methodget NewGeographicalDomainCode
	 * method to load the geographical domain list of to parent user, for updating the user geography info.
	 * @param Connection p_con
	 * @param ChannelUserTransferForm p_channelUserTransferForm
	 * @param ChannelUserTransferVO p_channelUserTransferVO
	 * @return void
	 */
	private void getNewGeographicalDomainCode(Connection p_con,ChannelUserTransferVO p_channelUserTransferVO) throws BTSLBaseException
	{
		final String methodName="getNewGeographicalDomainCode";
		if (_log.isDebugEnabled())
			_log.debug(methodName,"Entered : channelUserTransferVO="+p_channelUserTransferVO.toString());
		
		String geoGraphicalDomainType=null;
		ChannelUserTransferDAO chnlUserTransferDAO=new ChannelUserTransferDAO();
		try
		{
			ArrayList geoList=chnlUserTransferDAO.loadGeogphicalHierarchyListByToParentId(p_con, p_channelUserTransferVO.getToParentID());
			ArrayList userList=p_channelUserTransferVO.getUserHierarchyList();
			
			
			if(userList!=null && !userList.isEmpty())
			{
				int userListSize = userList.size();
				for(int j=0; j< userListSize;j++)
				{
					ChannelUserVO chnlUserVO=(ChannelUserVO)userList.get(j);
					if (_log.isDebugEnabled())
						_log.debug(methodName,"Entered : chnlUserVO Geocode="+chnlUserVO.getGeographicalCode());
					
					geoGraphicalDomainType=chnlUserVO.getCategoryVO().getGrphDomainType();
					if(geoList!=null && !geoList.isEmpty())
					{
						int geoListSize = geoList.size();
						for(int i=0;i< geoListSize;i++)
						{
							GeographicalDomainVO geoDomainVO=(GeographicalDomainVO) geoList.get(i);
							
							if (_log.isDebugEnabled())
								_log.debug(methodName,"Entered : GrphDomainCode() "+geoDomainVO.getGrphDomainCode());
							
							
							if((geoDomainVO.getGrphDomainType().equals(geoGraphicalDomainType)) && (geoDomainVO.getGrphDomainCode().equals(p_channelUserTransferVO.getGeographicalCode())))
							{
								if (_log.isDebugEnabled())
									_log.debug(methodName,"compare succussful");
								
								chnlUserVO.setGeographicalCode(geoDomainVO.getGrphDomainCode());
								break;
							}
						}
					}
				}
			}
			
		}
		catch(BTSLBaseException be)
		{
			_log.error(methodName, "Exception:e=" + be);
			_log.errorTrace(methodName, be);
			
		}
		finally
		{
			if (_log.isDebugEnabled())
			 _log.debug(methodName,"Exiting ");
		}
		
	}
	
	
			
			/**
			 * Method migrateUsers.
			 * This method is used to delete the uploaded file if any error occurs during file processing
			 * @param p_con Connection
			 * @param List<UserMigrationVO> Connection
			 * @return List<ListValueVO>
			 * @throws Exception
			 */
				public List<ListValueVO> migrateUsers(Connection p_con,List<UserMigrationVO> p_userMigrationList) throws BTSLBaseException,Exception
				{	 
					_log.debug("migrateUsers","", "Entered with User List Size="+p_userMigrationList.size());

					ArrayList<ListValueVO> errorListAfterMig= new ArrayList<ListValueVO>() ;
					ArrayList<UserMigrationVO> finalMigList=(ArrayList<UserMigrationVO>)p_userMigrationList;
					UserMigrationDAO userMigDAO=null;

					try
					{
						userMigDAO=new UserMigrationDAO();
						errorListAfterMig=userMigDAO.userMigrationProcess(p_con, finalMigList, _catCodeMap, _userGeoDomCodeMap, _profileGradeMap, _migrationDetailMap);
					}
					catch(BTSLBaseException be)
					{
						_log.debug("migrateUsers","", "BTSLBaseException error be: "+be);						
						_log.error("migrateUsers", "Exception: " + be.getMessage());
			            _log.errorTrace("migrateUsers", be);
						throw be;
					}
					finally
					{
						_log.debug("migrateUsers","", "Exiting with error List size="+errorListAfterMig.size());
					}
					return errorListAfterMig;
				}
		
}
				


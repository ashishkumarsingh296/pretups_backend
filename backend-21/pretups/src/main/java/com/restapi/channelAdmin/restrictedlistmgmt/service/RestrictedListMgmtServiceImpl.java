package com.restapi.channelAdmin.restrictedlistmgmt.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import jakarta.activation.MimetypesFileTypeMap;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.restapi.channelAdmin.restrictedlistmgmt.requestVO.*;
import com.restapi.channelAdmin.restrictedlistmgmt.responseVO.*;
import com.restapi.channelAdmin.restrictedlistmgmt.responseVO.ApprovalRestrictedListResponseVO;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
//import org.apache.struts.action.ActionForward;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.logging.BlackListLog;
import com.btsl.pretups.logging.RestrictedMsisdnLog;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.restrictedsubs.businesslogic.RestrictedSubscriberVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserGeographiesVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.KeyArgumentVO;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.channelAdmin.restrictedlistmgmt.VO.LoadSubscriberListForBlackListSingleVO;
import com.restapi.channelAdmin.restrictedlistmgmt.serviceI.RestrictedListMgmtServiceI;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.domain.businesslogic.DomainWebDAO;
import com.web.pretups.restrictedsubs.businesslogic.RestrictedSubscriberWebDAO;
import com.web.pretups.user.businesslogic.ChannelUserWebDAO;

import static com.btsl.pretups.common.PretupsErrorCodesI.*;
import static com.btsl.pretups.common.PretupsErrorCodesI.RESTRICTEDSUBS_BLACKLISTING_ERROR_FILENOMOVE;


@Service("RestrictedListMgmtServiceI")
public class RestrictedListMgmtServiceImpl implements RestrictedListMgmtServiceI{

    public static final Log LOG = LogFactory.getLog(RestrictedListMgmtServiceImpl.class.getName());
	public static final String classname = "RestrictedListMgmtServiceImpl";
	
	
	
	@Override
	public LoadDropdownsResponseVO loadDropdowns(MultiValueMap<String, String> headers,
			HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con,
			MComConnectionI mcomCon, Locale locale, UserVO userVO, LoadDropdownsResponseVO response) throws BTSLBaseException {
		
		final String METHOD_NAME = "loadDropdowns";	
		if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		
		try {

			 GeographicalDomainDAO _geographyDAO = new GeographicalDomainDAO();
			 ArrayList geographicalAreaList = _geographyDAO.loadUserGeographyList(con, userVO.getUserID(), userVO.getNetworkID());
			  
		 
		 	if (!geographicalAreaList.isEmpty()) {
			 response.setGeoDomainList(geographicalAreaList);
			}
		 	
			if (!userVO.getDomainList().isEmpty()) {
				response.setDomainList(userVO.getDomainList()); 
				response.setDomainListSize(userVO.getDomainList().size());
			}

			if ((BTSLUtil.isNullOrEmptyList(geographicalAreaList)) && (BTSLUtil.isNullOrEmptyList(userVO.getDomainList()))) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
			}
			 
				
				
			// If the logged in user is operator type then only
             // that domains should be loaded for which restricted msisdn is
             // Y
			 
			 
			// /
            // Load the corporate category
            // /
			ArrayList domainList = null;
            ArrayList catList = null;
            ListValueVO listValueVO = null;
            domainList = response.getDomainList();
            StringBuffer domainStr = new StringBuffer();
            
            for (int i = 0, j = domainList.size(); i < j; i++) {
                domainStr.append("'");
                listValueVO = (ListValueVO) domainList.get(i);
                domainStr.append(listValueVO.getValue());
                domainStr.append("',");
            }

             if (PretupsI.OPERATOR_CATEGORY.equalsIgnoreCase(userVO.getCategoryCode())) {
                 ArrayList newDomainList = null;
                 DomainWebDAO domainWebDAO = new DomainWebDAO();
                 newDomainList = domainWebDAO.loadRestrictedMsisdnsDomainList(con, domainStr.substring(0, (domainStr.length() - 1)), false, userVO.getUserID());
                 response.setDomainList(newDomainList);
                 domainStr = new StringBuffer();
                 for (int i = 0, j = newDomainList.size(); i < j; i++) {
                     domainStr.append("'");
                     listValueVO = (ListValueVO) newDomainList.get(i);
                     domainStr.append(listValueVO.getValue());
                     domainStr.append("',");
                 }
             }
			 if (domainStr != null && domainStr.length() > 0) {
                 catList = new CategoryWebDAO().loadRestrictedCatList(con, domainStr.substring(0, (domainStr.length() - 1)), true, true, false);
                 response.setCategoryList(catList);
             } else {
            	 throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_CORPORATE_DOMAIN_ASSOCIATED, 0, null);
             }
			 

             if (catList == null || catList.isEmpty()) {
                 throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_CATEGORY_EXIST, 0, null);
             }
			 

             
            response.setStatus((HttpStatus.SC_OK));
 			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
 			response.setMessage(resmsg);
 			response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);
		}
		finally {
			if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
		}
		return response;
	}


	
	
	
	@Override
	public SearchUserListBasedOnkeywordResponseVO searchUserListBasedOnkeyword(MultiValueMap<String, String> headers,
			HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con,
			MComConnectionI mcomCon, Locale locale, UserVO userVO, SearchUserListBasedOnkeywordResponseVO response,
			String userName, String categoryCode,String geoDomain) throws BTSLBaseException {
		
		final String METHOD_NAME = "searchUserListBasedOnkeyword";
		if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		ArrayList userList = null;
		ChannelUserWebDAO channelUserWebDAO = null;
		
		try {
			channelUserWebDAO = new ChannelUserWebDAO();
			userName = "%" + userName + "%";
			String catCode[] = categoryCode.split(":"); // domainCode:CategoryCode
            

            if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
                userList = channelUserWebDAO.loadChannelUserListHierarchy(con, catCode[1], catCode[0], geoDomain, userName, userVO.getUserID());
            } else {
                userList = channelUserWebDAO.loadChannelUserList(con, catCode[1], catCode[0], geoDomain, userName, userVO.getUserID());
            }
            
            if (userList.isEmpty()) {
            	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_USER_FOUND, 0, null);
            }
            
            response.setUserList(userList);
            response.setUserListSize(userList.size());
            response.setStatus((HttpStatus.SC_OK));
 			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
 			response.setMessage(resmsg);
 			response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);
		}
		finally {
			if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
		}
		return response;
	}


	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public LoadSubscriberListForDeleteResponseVO loadSubscriberListForDelete(MultiValueMap<String, String> headers,
			HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con,
			MComConnectionI mcomCon, Locale locale, UserVO userVO, LoadSubscriberListForDeleteResponseVO response,
			String msisdnStr, String ownerID) throws BTSLBaseException {
		final String METHOD_NAME = "loadSubscriberListForDelete";
		if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		ArrayList userList = null;
		ChannelUserWebDAO channelUserWebDAO = null;
		
		RestrictedSubscriberWebDAO restrictedSubscriberWebDAO = null;
		ArrayList msisdnList = new ArrayList();
        StringBuffer invalidMsisdnsBuff = new StringBuffer();
        String msisdn;
        String filteredMsisdn;
        String msisdnPrefix;
        NetworkPrefixVO networkPrefixVO = null;
        String networkCode;
		try {
			restrictedSubscriberWebDAO = new RestrictedSubscriberWebDAO();
			// use StringTokenizer totake out the comma seperated value of
            // msisdn
            StringTokenizer tokenizer = new StringTokenizer(msisdnStr, ",");
            while (tokenizer.hasMoreTokens()) {
            	
            	 msisdn = tokenizer.nextToken().trim();
                 // Change ID=ACCOUNTID
                 // FilteredMSISDN is replaced by getFilteredIdentificationNumber
                 // This is done because this field can contains msisdn or
                 // account id
                 filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(msisdn); // before
                                                                                     // process
                                                                                     // MSISDN
                                                                                     // filter
                                                                                     // each-one

                 // check for valid MSISDN
                 // Change ID=ACCOUNTID
                 // isValidMsisdn is replaced by isValidIdentificationNumber
                 // This is done because this field can contains msisdn or
                 // account id
                 if (!BTSLUtil.isValidIdentificationNumber(filteredMsisdn)) {
                     if (LOG.isDebugEnabled()) {
                         LOG.debug("loadSubsListForDelete", "Not a valid MSISDN " + msisdn);
                     }
                     invalidMsisdnsBuff.append(msisdn);
                     invalidMsisdnsBuff.append(",");
                     continue;
                 }
                 // check prefix of the MSISDN
                 msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn); // get
                                                                           // the
                                                                           // prefix
                                                                           // of
                                                                           // the
                                                                           // MSISDN

                 networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                 if (networkPrefixVO == null) {
                     if (LOG.isDebugEnabled()) {
                         LOG.debug("loadSubsListForDelete", "No Network prefix found " + msisdn);
                     }
                     invalidMsisdnsBuff.append(msisdn);
                     invalidMsisdnsBuff.append(",");
                     continue;
                 }
                 
                 
              // Check for network code
                 networkCode = networkPrefixVO.getNetworkCode();
                 if (!networkCode.equals(userVO.getNetworkID())) {
                     if (LOG.isDebugEnabled()) {
                         LOG.debug("loadSubsListForDelete", "Not supporting Network" + msisdn);
                     }
                     invalidMsisdnsBuff.append(msisdn);
                     invalidMsisdnsBuff.append(",");
                     continue;
                 }
                 msisdnList.add(filteredMsisdn);
            }
            
         // If all the entered msisdn are not form the logged in user network
            // then show the error message
            if (msisdnList == null || msisdnList.isEmpty()) {
//                btslMessage = new BTSLMessages("restrictedsubs.sublistingfordelete.msg.msisdninvalid", "userSelect");
//                forward = super.handleMessage(btslMessage, request, mapping);
//                return forward;
            	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_MSISDN_INVALID, 0, null);
            }
            ArrayList deletedList = restrictedSubscriberWebDAO.loadSubsListForDelete(con, msisdnList, ownerID, invalidMsisdnsBuff);

            // Checking whether the subscriber is existing or not
            if (deletedList == null || deletedList.isEmpty()) {
            	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_MSISDN_NOT_FOUND, 0, null);
            }
            response.setListForDelete(deletedList);

            // To display the message for invalid msisdn
            if (invalidMsisdnsBuff.length() > 0) {
                String invalidStr = invalidMsisdnsBuff.substring(0, invalidMsisdnsBuff.length() - 1);
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_RESTRICTED_MSISDNS, 0, new String[] { invalidStr }, null);
            } else {
                //forward = mapping.findForward("loadSubsListForDelete");
            	response.setStatus((HttpStatus.SC_OK));
     			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
     			response.setMessage(resmsg);
     			response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);
            }
			
		}
		finally {
			if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
		}
		return response;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	@Override
	public BaseResponse deleteRestrictedSubscriber(MultiValueMap<String, String> headers,
			HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con,
			MComConnectionI mcomCon, Locale locale, UserVO userVO, BaseResponse response,
			DeleteRestrictedSubscriberRequestVO requestVO) throws SQLException, BTSLBaseException {
		
		final String METHOD_NAME = "deleteRestrictedSubscriber";
		if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		ArrayList userList = null;
		ChannelUserWebDAO channelUserWebDAO = null;
		

        RestrictedSubscriberWebDAO restrictedSubscriberWebDAO = null;
		
		try {
			
			if (requestVO.getAllSubscriberCheckboxSelected()) {
                restrictedSubscriberWebDAO = new RestrictedSubscriberWebDAO();
                BTSLMessages btslMessage = null;
                String deletedMsisdnList = restrictedSubscriberWebDAO.deleteResSubscriberBulk(con, requestVO.getConfirmListForDelete(), requestVO.getOwnerID());
                // Check for the successful deletion
                if (deletedMsisdnList.length() == 0) {
                	response.setStatus((HttpStatus.SC_OK));
         			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_DELETION_SUCCESS, null);
         			response.setMessage(resmsg);
         			response.setMessageCode(PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_DELETION_SUCCESS);
                } else // displays message if the deletion is unsuccessful
                {
                	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_RESTRICTED_MSISDNS, 0, new String[] { deletedMsisdnList.substring(0, deletedMsisdnList.length() - 1) }, null);
                }
                mcomCon.finalCommit();
            }
			// this condition is implemented in case of deleting all the
            // subscriber for the given owner id
            // when the checkbox is checked
            if (requestVO.getAllSubscriberCheckboxSelected() == false) {
                int deletedCount;
                restrictedSubscriberWebDAO = new RestrictedSubscriberWebDAO();
                deletedCount = (restrictedSubscriberWebDAO.deleteRestrictedBulk(con, requestVO.getOwnerID()));
                // To display the message for deleting the all subscribers
                if (deletedCount > 0) {
                	mcomCon.finalCommit();
                	response.setStatus((HttpStatus.SC_OK));
         			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_DELETION_SUCCESS, null);
         			response.setMessage(resmsg);
         			response.setMessageCode(PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_DELETION_SUCCESS);
                }
                // if no subscriber is found
                else if (deletedCount == 0) {
                	mcomCon.finalRollback();
                	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_SUBSCRIBER_FOUND, 0, null);
                }
            }
			
			
		}
		finally {
			if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
		}
		return response;
		
		
	}
	
	
	
	public UserValidationResponseVO loadUserList(Connection con, UserVO reqUserVO, String userName, String categoryCode, String geoDomain ) throws BTSLBaseException, Exception
	{ 
		final String METHOD_NAME = "loadUserList";
	
        if (LOG.isDebugEnabled()) {
            LOG.debug("loadUserList", "Entered");
        }
        UserValidationResponseVO response = new UserValidationResponseVO();
        
        ArrayList userList = new ArrayList();
        ChannelUserWebDAO channelUserWebDAO = null;
        
            channelUserWebDAO = new ChannelUserWebDAO();
          
           String searchUserName = "%" + userName + "%";
            String catCode[] = categoryCode.split(":"); // domainCode:CategoryCode
            
            if (reqUserVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
                userList = channelUserWebDAO.loadChannelUserListHierarchy(con, catCode[1], catCode[0], geoDomain, searchUserName, reqUserVO.getUserID());
            } else {
                userList = channelUserWebDAO.loadChannelUserList(con, catCode[1], catCode[0], geoDomain, searchUserName, reqUserVO.getUserID());
            }

                 
            UserVO userVO = null;
            if (userList.size() == 1) {
                userVO = (UserVO) userList.get(0);
                response.setUserID(userVO.getUserID());
                response.setUserName(userVO.getUserName());
                response.setOwnerID(userVO.getOwnerID());
                response.setOwnerName(userVO.getOwnerName());
            } else if (userList.size() > 1) {
                boolean isExist = false;
                if (!BTSLUtil.isNullString(reqUserVO.getUserID())) {
                    for (int i = 0, k = userList.size(); i < k; i++) {
                        userVO = (UserVO) userList.get(i);
                        if ( (userName.compareTo(userVO.getUserName()) == 0)) {
                            response.setUserID(userVO.getUserID());
                            response.setUserName(userVO.getUserName());
                            response.setOwnerID(userVO.getOwnerID());
                            response.setOwnerName(userVO.getOwnerName());
                            isExist = true;
                            break;
                        }
                    }
                } else {
                    UserVO nextUserVO = null;
                    for (int i = 0, k = userList.size(); i < k; i++) {
                        userVO = (UserVO) userList.get(i);
                        if (reqUserVO.getUserName().compareTo(userVO.getUserName()) == 0) {
                            if (((i + 1) < k)) {
                                nextUserVO = (UserVO) userList.get(i + 1);
                                if (reqUserVO.getUserName().compareTo(nextUserVO.getUserName()) == 0) {
                                    isExist = false;
                                    break;
                                }
                                response.setUserName(userVO.getUserName());
                                response.setUserID(userVO.getUserID());
                                response.setOwnerID(userVO.getOwnerID());
                                response.setOwnerName(userVO.getOwnerName());
                                isExist = true;
                                break;
                            }
                            response.setUserName(userVO.getUserName());
                            response.setUserID(userVO.getUserID());
                            response.setOwnerID(userVO.getOwnerID());
                            response.setOwnerName(userVO.getOwnerName());
                            isExist = true;
                            break;
                        }
                    }
                }
                
                     if (!isExist) {
                    	 String arr[] = { reqUserVO.getUserName() };
                        throw new BTSLBaseException(classname, METHOD_NAME,PretupsErrorCodesI.NO_USER_EXIST , arr);
                     }
                 } else {
                	 throw new BTSLBaseException(classname, METHOD_NAME,PretupsErrorCodesI.SUBSCRIBER_NOT_FOUND , "");
                	 
                 }
                  return response;
        }
	public void validateDomainAndCategory(Connection con, UserVO userVO,String geoDomain,  String Category)throws BTSLBaseException, Exception {
		 final String METHOD_NAME = "validateDomainAndCategory";
	        if (LOG.isDebugEnabled()) {
	            LOG.debug("validateDomainAndCategory", "Entered");
	        }
	        GeographicalDomainDAO   geographyDAO = new GeographicalDomainDAO();
	          ArrayList geographicalAreaList = geographyDAO.loadUserGeographyList(con, userVO.getUserID(), userVO.getNetworkID());
	          Boolean geoIsPresent = false;
	          for(int i =0;i<geographicalAreaList.size();i++) {
	        	  UserGeographiesVO geoVO = (UserGeographiesVO)geographicalAreaList.get(i);
	        	  if(geoVO.getGraphDomainCode().equals(geoDomain)) {
	        		  geoIsPresent = true;
	        	  }
	          }
	          if(!geoIsPresent) {
	        	  throw new BTSLBaseException(classname, METHOD_NAME,PretupsErrorCodesI.GEO_CODE_NOT_FOUND , "");
            	 
	          }   
		StringBuffer domainStr = new StringBuffer();
        ArrayList domainList = userVO.getDomainList();
        ArrayList catList = null;
        ListValueVO listValueVO = null;
        
        for (int i = 0, j = domainList.size(); i < j; i++) {
            domainStr.append("'");
            listValueVO = (ListValueVO) domainList.get(i);
            domainStr.append(listValueVO.getValue());
            domainStr.append("',");
        }

         if (PretupsI.OPERATOR_CATEGORY.equalsIgnoreCase(userVO.getCategoryCode())) {
             ArrayList newDomainList = null;
             DomainWebDAO domainWebDAO = new DomainWebDAO();
             newDomainList = domainWebDAO.loadRestrictedMsisdnsDomainList(con, domainStr.substring(0, (domainStr.length() - 1)), false, userVO.getUserID());
         
             domainStr = new StringBuffer();
             for (int i = 0, j = newDomainList.size(); i < j; i++) {
                 domainStr.append("'");
                 listValueVO = (ListValueVO) newDomainList.get(i);
                 domainStr.append(listValueVO.getValue());
                 domainStr.append("',");
             }
         }
		 if (domainStr != null && domainStr.length() > 0) {
             catList = new CategoryWebDAO().loadRestrictedCatList(con, domainStr.substring(0, (domainStr.length() - 1)), true, true, false);
            
		 } else {
        	 throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_CORPORATE_DOMAIN_ASSOCIATED, 0, null);
         }
		 

         if (catList == null || catList.isEmpty()) {
             throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_CATEGORY_EXIST, 0, null);
         }
         ListValueVO catValueVO= null;
         boolean isCatExists = false;
         for(int i =0;i<catList.size();i++) {
        	 catValueVO =(ListValueVO) catList.get(i);
        	 if(Category.equals(catValueVO.getValue())) {
        		 isCatExists = true;
        	 }
         }
         if(!isCatExists)
        	 throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_CATEGORY_EXIST, 0, null);
	}
	
	 public ArrayList<ViewRestrictedResponseVO> loadRestrictedSubs(Connection con, UserVO userVO, String userName, String categoryCode, String geoDomain,String fromDateStr, String toDateStr ) throws BTSLBaseException, Exception {
	        final String METHOD_NAME = "loadRestrictedSubs";
	        if (LOG.isDebugEnabled()) {
	            LOG.debug("loadRestrictedSubs", "Entered");
	        }
	        	try {
	        		this.validateDomainAndCategory(con, userVO, geoDomain,  categoryCode);
	        	}
	        	catch(BTSLBaseException ex) {
	        		LOG.error(METHOD_NAME, "Exceptin:e=" + ex);
		            LOG.errorTrace(METHOD_NAME, ex);
		            throw ex;
		        } catch (Exception e) {
		            LOG.error(METHOD_NAME, "Exceptin:e=" + e);
		            LOG.errorTrace(METHOD_NAME, e);
		            throw e;
		        } 	        		
	        	ArrayList<RestrictedSubscriberVO> subscriberList = new ArrayList<>();
	            UserValidationResponseVO validateUser= this.loadUserList(con,userVO, userName,categoryCode,geoDomain);
	            RestrictedSubscriberWebDAO restrictedSubscriberWebDAO = new RestrictedSubscriberWebDAO();
	 
	            Date fromDate= null;
	            Date toDate = null;;
	            try {
	            	fromDate = BTSLUtil.getDateFromDateString(fromDateStr);
	               toDate = BTSLUtil.getDateFromDateString(toDateStr);
	           }catch(Exception e) {
	     		   throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_DATE_TIME_FORMATE);
	     	   }
	            
	           // Change is done by Ankit Zindal
	                // Date 27/12/06
	                // Reason is that we have to pass filtred msisdn in DAO.
	               
	                if (userVO.getUserType().equals(PretupsI.CHANNEL_USER_TYPE)) {
	                	subscriberList=  restrictedSubscriberWebDAO.loadResSubsDetails(con, null, validateUser.getUserID(), false, fromDate, toDate);
	                }
	                else {
	                    subscriberList = restrictedSubscriberWebDAO.loadResSubsDetails(con, null, validateUser.getOwnerID(), true, fromDate, toDate);
	                }
	                if (subscriberList.size() <= 0)// error
	                                                                  // message for
	                                                                  // no data
	                                                                  // found
	                {
	                	throw new BTSLBaseException(classname, METHOD_NAME,PretupsErrorCodesI.NO_USER_EXIST , "");
	                }
	                ArrayList<ViewRestrictedResponseVO> responseList = new ArrayList<>();
	                for(RestrictedSubscriberVO vo:subscriberList) {
	                	ViewRestrictedResponseVO response= new ViewRestrictedResponseVO();
	                	response.setBlockListStatus(vo.getBlackListStatusDesc());
	                	response.setMonthlyTransactionLimit(vo.getMonthlyLimitForDisp());
	                	response.setMonthlyTransactionAmount(vo.getMonthlyTransferAmountStr());
	                	response.setMaxTransactionAmount(vo.getMaxTxnAmtForDisp());
	                	response.setMinTransactionAmount(vo.getMinTxnAmtForDisp());
	                	response.setRechargeAllow(vo.getRechargeThroughParent());
	                	response.setStatus(vo.getStatusDes());
	                	response.setSubscriberMobileNumber(vo.getMsisdn());
	                	response.setTotalTransactionAmount(String.valueOf(vo.getTotalTransferAmount()));
	                	response.setTotalTransactionCount(String.valueOf(vo.getTotalTxnCount()));
	                	response.setSubscriberCode(vo.getEmployeeCode());
	                	response.setSubscriberName(vo.getEmployeeName());
	                	    	responseList.add(response);
	                }
	           if (LOG.isDebugEnabled()) {
	                LOG.debug("loadRestrictedSubs", "Exiting forward :" + METHOD_NAME);
	            }
	           
	           
	       return responseList;
	    } // end of loadRestrictedSubs








	  public UploadFileResponseVO uploadRestrictedList( Connection con,UserVO userVO, String userName, String categoryCode, String geoDomain, String domain,String subscriberType,UploadFileRequestVO uploadFileRequestVO, HttpServletRequest httpRequest, UploadFileResponseVO response,HttpServletResponse response1 )throws BTSLBaseException, Exception{
		 
		  	  
		  final String METHOD_NAME = "uploadRestrictedList";
	        if (LOG.isDebugEnabled()) {
	            LOG.debug("uploadRestrictedList", "Entered");
	        }
	        try {
        		this.validateDomainAndCategory(con, userVO, geoDomain,  categoryCode);
        	}
        	catch(BTSLBaseException ex) {
        		LOG.error(METHOD_NAME, "Exceptin:e=" + ex);
	            LOG.errorTrace(METHOD_NAME, ex);
	            throw ex;
	        } catch (Exception e) {
	            LOG.error(METHOD_NAME, "Exceptin:e=" + e);
	            LOG.errorTrace(METHOD_NAME, e);
	            throw e;
	        } 	        		
        
	        UserValidationResponseVO validateUser= this.loadUserList(con,userVO, userName,categoryCode,geoDomain);
	        ArrayList subScriberTypeList =LookupsCache.loadLookupDropDown(PretupsI.SUBSRICBER_TYPE, true);
	        boolean isSubScriberTypeExists = false;
	        for(int i =0; i<subScriberTypeList.size();i++) {
	        	ListValueVO lookup = (ListValueVO)subScriberTypeList.get(i);
	        	if(subscriberType.equals(lookup.getValue()))
	        	{
	        		isSubScriberTypeExists = true;
	        	}
	        }
	        if(!isSubScriberTypeExists) {
	        	 throw new BTSLBaseException(classname, METHOD_NAME,PretupsErrorCodesI.INVALID_RECEIVER_SUBSCRIBER_TYPE , "");
            	 
	        }
	        
	        ListValueVO listValueVO = null;
	        BufferedReader br = null;
	        String line = null;
	        InputStream is = null;
	        InputStreamReader inputStreamReader = null;
	        
	        String fileName = uploadFileRequestVO.getFileName();// accessing name
        // of the file
	      boolean message = BTSLUtil.isValideFileName(fileName);// validating
           // name of the
           // file
	      // if not a valid file name then throw exception
	      if (!message || !fileName.endsWith(".txt")) {
	    	  throw new BTSLBaseException(classname, METHOD_NAME,PretupsErrorCodesI.FILE_COULD_NOT_BE_UPLOADED_TRY_A_VALID_FILE , "");
	      }
	      // Cross site Scripting removal
	      ReadGenericFileUtil fileUtil = new ReadGenericFileUtil();
	      byte[] data  =fileUtil.decodeFile(uploadFileRequestVO.getFileAttachment());     ;// ak
	      is = new ByteArrayInputStream(data);
        inputStreamReader = new InputStreamReader(is);
	      br = new BufferedReader(inputStreamReader);
	      while ((line = br.readLine()) != null) {
	    	  boolean isFileContentValid = BTSLUtil.isFileContentValid(line);
	    	  if (!isFileContentValid) {
	    		  throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_CONTENT_IS_IN_VALID, "");
	    	  }
	      }// Cross site scripting Removal
	      
	        boolean isFileUploaded = false;
	           
	           
	            // Set the Form file name in the formBean.
	           
//	            file = thisForm.getFile();

	            String dir = com.btsl.util.Constants.getProperty("bulkRegistrationFilePath");
	            
	            String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_PLAIN_TEXT);
	            if(uploadFileRequestVO.getFileType() == null ||!contentType.equals(uploadFileRequestVO.getFileType())) {
	            	 throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_TYPE, "");
	         	    
	            }
	            String fileSize = Constants.getProperty("OTHER_FILE_SIZE");
	            if (BTSLUtil.isNullString(fileSize)) {
	                if (LOG.isDebugEnabled()) {
	                    LOG.debug("uploadAndProcessFile", "Other File size is not defined in Constant Property file");
	                }
	                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.OTHER_FILE_SIZE_IS_MISSING_IN_CONTENT_PROPERTY_FILE, "");
	            }

	            File requestToFile = new File(fileName);
	           
	            FileUtils.writeByteArrayToFile(requestToFile, data);
	            isFileUploaded=   uploadFileToServer( requestToFile ,data, dir,  contentType,  Long.parseLong(fileSize));
	            if (isFileUploaded)
	            // now process uploaded file{}
	            {
	                // call this private method to process the uploaded file
	            	 response	= this.processUploadedFileForBulkReg( con, userVO,validateUser, requestToFile,fileName, subscriberType,  httpRequest, response, response1);
	            } else {
	                throw new BTSLBaseException(classname, METHOD_NAME,PretupsErrorCodesI.FILE_NOT_UPLOADED , "");
	            }
	       
	        return response;
	    }// end of uploadAndProcessFile

	
	
	  private UploadFileResponseVO processUploadedFileForBulkReg(Connection con,UserVO userVO,UserValidationResponseVO  validateUser, File formFile,String fileName,String subscriberType, HttpServletRequest request, UploadFileResponseVO responseVO, HttpServletResponse response1) throws Exception {
	        final String METHOD_NAME = "processUploadedFileForBulkReg";
	        if (LOG.isDebugEnabled()) {
	            LOG.debug("processUploadedFileForBulkReg", "Entered");
	        }
	        String filePath = Constants.getProperty("bulkRegistrationFilePath");
	        String contentsSize = Constants.getProperty("MAX_RESTRICTED_LIST_SIZE");
	        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
	        FileReader fileReader = null;
	        BufferedReader bufferReader = null;
	        String tempStr = null;
	        ArrayList fileContents = null;
	        int countMsisdn = 0;
	        long noOfRec = 0;
	        String msisdn = null;
	        String filteredMsisdn = null;
	        String msisdnPrefix = null;
	        NetworkPrefixVO networkPrefixVO = null;
	        String networkCode = null;
			
	        boolean fileMoved = false;
	        ArrayList finalList = new ArrayList();
	        File file = null;
	        BTSLMessages btslMessage = null;
	        long failCount = 0;
	        long duplicateRecs = 0;
	        int newLines = 0;
	        RestrictedSubscriberVO errorVO = new RestrictedSubscriberVO();
	        // for error log messages
	        String duplicateMsisdnMsg = null;
	        String invalidMsisdnMsg = null;
	        String noPrefixMsg = null;
	        String unsupportedNwMsg = null;
	        String alreadyExistMsg = null;
	        String canNotRegMsg = null;
	        String netSubsId = null;
	        String PrefixNotMatch = null;
	        OperatorUtilI operatorUtil = null;
	        if (BTSLUtil.isNullString(filePath)) {
	            if (LOG.isDebugEnabled()) {
	                LOG.debug("processUploadedFileForBulkReg", "File path not defined in Constant Property file");
	            }
	            throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.UNABLE_TO_CREATE_UPLOAD_FILE_DIRECTORY, "");
	        }
	        if (BTSLUtil.isNullString(contentsSize)) {
	            if (LOG.isDebugEnabled()) {
	                LOG.debug("processUploadedFileForBulkReg", "MAX_RESTRICTED_LIST_SIZE not defined in Constant Property file");
	            }
	            throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.OTHER_FILE_SIZE_IS_MISSING_IN_CONTENT_PROPERTY_FILE, "");
	        }

	        
	        String filePathAndFileName = filePath + fileName; // path if the file
	                                                          // with file name
	        String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
	        try {
	            operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
	        } catch (Exception e) {
	            LOG.errorTrace(METHOD_NAME, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,classname+"[processUploadedFileForBulkReg]", "", "", "", "Exception while loading the class at the call:" + e.getMessage());
	        }

	        try {
	            if (LOG.isDebugEnabled()) {
	                LOG.debug("processUploadedFileForBulkReg", "Initializing the fileReader, filepath : " + filePathAndFileName);
	            }

	            fileReader = new FileReader(filePathAndFileName);
	            bufferReader = new BufferedReader(fileReader);
	            fileContents = new ArrayList();

	            boolean isStartFound = false;
	            boolean isEndFound = false;

	            if (bufferReader.ready()) // If File Not Blank Read line by line
	            {
	                while ((tempStr = bufferReader.readLine()) != null) // read the
	                                                                    // file
	                                                                    // until it
	                                                                    // reaches
	                                                                    // to end
	                {
	                    if (tempStr.trim().length() != 0) // remove blank lines
	                    {
	                        if ("[START]".equalsIgnoreCase(tempStr)) {
	                            isStartFound = true;
	                            fileContents.add("NEWLINE"); // treat [START} as a
	                                                         // new line
	                            newLines++;
	                        } else if ("[END]".equalsIgnoreCase(tempStr)) {
	                            isEndFound = true; // no processing after end of
	                                               // file
	                            break;
	                        } else if (isStartFound) {
	                            fileContents.add(tempStr.trim());
	                        } else // treat new lines before start if any
	                        {
	                            fileContents.add("NEWLINE");
	                            newLines++;
	                        }
	                    }// end of if(tempStr.trim().length()!=0)
	                    else // add the new lines position in arraylist for logging
	                         // purpose
	                    {
	                        fileContents.add("NEWLINE");
	                        newLines++;
	                    }
	                }// end of while((tempStr = bufferReader.readLine()) != null)

	                if (!isStartFound) {
	                    if (LOG.isDebugEnabled()) {
	                        LOG.debug("processUploadedFileForBulkReg", "No [START] tag found");
	                    }
	                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.START_TAG_NOT_FOUND_IN_THE_FILE, "");
	                }

	                if (!isEndFound) {
	                    if (LOG.isDebugEnabled()) {
	                        LOG.debug("processUploadedFileForBulkReg", "No [END] tag found");
	                    }
	                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.END_TAG_NOT_FOUND_IN_THE , "");
	                }
	                long records = fileContents.size();
	                noOfRec = records - newLines;

	                // it can not be allowed to process the file if MSISDN's are
	                // more than the defined Limit
	                if ((noOfRec) > Integer.parseInt(contentsSize)) {
	                    if (LOG.isDebugEnabled()) {
	                        LOG.debug("processUploadedFileForBulkReg", "File contents size of the file is not valid in constant properties file : " + fileContents.size());
	                    }
	                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.FILE_SIZE_SHOULD_NOT_BE_MORE_THAN_BYTE, new String[] { contentsSize });
	                }
	                if(noOfRec == 0) {
	                	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NUMBER_OF_RECORDS_MUST_BE_GREATER_THAN_ZERO, "");
	                }

	              
	                // read from message resource file for error log
	                duplicateMsisdnMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.RSC_DUPLICATE_MSISDN, null);
	                invalidMsisdnMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.RSC_INVALID_MSISDN, null);
	                noPrefixMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NETWORK_PREFIX_NOT_FOUND, null);
	                unsupportedNwMsg =RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UNSUPPORTED_NETWORK, null); 
	                alreadyExistMsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MOBILE_NUMBER_ALREADY_EXISTS_UNDER_THE_OWNER_USER , null);
	                canNotRegMsg =RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.CANNOT_REGISTER_THE_SUBSCRIBER, null);
	                PrefixNotMatch = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MSISDN_SERIES_IS_NOT_FOUND_OF_SELECTED_TYPE, null);
	                // Check for the duplicate mobile numbers in the list and
	                // put the subscriber's informations in the VO.
	                Date currDate = new Date();
	               // userVO = this.getUserFormSession(request);
	                if (fileContents != null && records > 0) {
	                    for (int i = 0; i < records; i++) {
	                        msisdn = (String) fileContents.get(i);
	                        try {
	                            // Change ID=ACCOUNTID
	                            // FilteredMSISDN is replaced by
	                            // getFilteredIdentificationNumber
	                            // This is done because this field can contains
	                            // msisdn or account id
	                            filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(msisdn); // before
	                                                                                                // process
	                                                                                                // MSISDN
	                                                                                                // filter
	                                                                                                // each-one
	                        } catch (Exception e) {
	                            if (LOG.isDebugEnabled()) {
	                                LOG.debug("processUploadedFileForBulkReg", "The MSISDN can not be filtered now");
	                            }
	                            LOG.errorTrace(METHOD_NAME, e);
	                            throw new BTSLBaseException(classname, METHOD_NAME,PretupsErrorCodesI.MOBILE_NUMBER_CANNOT_BE_FILETERED_NOW_PLEASE_CONTACT_SYSTEM_ADMIN, "");
	                        }
	                        errorVO = new RestrictedSubscriberVO();

	                        // if(!msisdn.equals("NEWLINE") &&
	                        // !finalList.contains(msisdn))
	                        if (!("NEWLINE".equals(msisdn)) && !isContain(finalList, filteredMsisdn)) {
	                            errorVO.setLineNumber(String.valueOf(i + 1));

	                            // Store the information for registering the
	                            // subscribers
	                            errorVO.setMsisdn(filteredMsisdn);
	                            netSubsId = operatorUtil.getRestrictedSubscriberID(String.valueOf(IDGenerator.getNextID("RESM", TypesI.ALL, userVO.getNetworkID())), userVO.getNetworkID());
	                            //
	                            errorVO.setSubscriberID(netSubsId);
	                            errorVO.setOwnerID(validateUser.getOwnerID()); // need to modify
	                            errorVO.setNetworkCode(userVO.getNetworkID());
	                            errorVO.setStatus(PretupsI.RES_MSISDN_STATUS_NEW);
	                            errorVO.setCreatedBy(userVO.getActiveUserID());
	                            errorVO.setCreatedOn(currDate);
	                            errorVO.setModifiedBy(userVO.getActiveUserID());
	                            errorVO.setModifiedOn(currDate);
	                            errorVO.setSubscriberType(subscriberType);
	                            errorVO.setBlackListStatus(PretupsI.RES_MSISDN_UNBLACKLIST_STATUS);

	                            finalList.add(errorVO);

	                        }// end of if(!msisdn.equals("NEWLINE") &&
	                         // !isContain(finalList,filteredMsisdn))
	                        else if (!("NEWLINE".equals(msisdn))) {
	                            errorVO.setLineNumber(String.valueOf(i + 1));
	                            errorVO.setMsisdn(msisdn);
	                            errorVO.setErrorCode(duplicateMsisdnMsg);
	                            finalList.add(errorVO);
	                            RestrictedMsisdnLog.log(fileName, msisdn, "Duplicate MSISDN", "Fail", "Logged In UserID : " + userVO.getUserID());
	                            duplicateRecs++;
	                        }// end of else if(!msisdn.equals("NEWLINE"))
	                        else if ("NEWLINE".equals(msisdn))
	                        // for tracing the line number for log purpose
	                        {
	                            errorVO.setLineNumber(String.valueOf(i + 1));
	                            errorVO.setErrorCode("NEWLINE");
	                            errorVO.setMsisdn("NEWLINE");
	                            finalList.add(errorVO);
	                            RestrictedMsisdnLog.log(fileName, msisdn, "New line in the file", "Fail", "Logged In UserID : " + userVO.getUserID());
	                        }// end of else if(msisdn.equals("NEWLINE"))
	                    }// end of for(int i=0;i<records;i++)
	                }// end of if(fileContents!=null && records>0)
	            }// end of if(bufferReader != null || bufferReader.ready())

	            // Check for Valid Msisdn's & process the MSISDN's from the Array
	            // List
	            long finalListSize = finalList.size();
	            RestrictedSubscriberVO errVO = null;
	            while (finalListSize != countMsisdn) {
	                errVO = (RestrictedSubscriberVO) finalList.get(countMsisdn);
	                msisdn = errVO.getMsisdn();
	                countMsisdn++;
	                if (errVO.getErrorCode() == null) {
	                    if (LOG.isDebugEnabled()) {
	                        LOG.debug("processUploadedFileForBulkReg", "Processing starts for MSISDN's " + msisdn);
	                    }
	                    // check for valid MSISDN
	                    // Change ID=ACCOUNTID
	                    // isValidMsisdn is replaced by isValidIdentificationNumber
	                    // This is done because this field can contains msisdn or
	                    // account id
	                    if (!BTSLUtil.isValidIdentificationNumber(msisdn)) {
	                        if (LOG.isDebugEnabled()) {
	                            LOG.debug("processUploadedFileForBulkReg", "Not a valid MSISDN " + msisdn);
	                        }
	                        failCount++;
	                        RestrictedMsisdnLog.log(formFile.getName(), msisdn, "Not a valid MSISDN", "Fail", "Logged In UserID : " + userVO.getUserID());
	                        errVO.setLineNumber(String.valueOf(countMsisdn));
	                        errVO.setMsisdn(msisdn);
	                        errVO.setErrorCode(invalidMsisdnMsg);
	                        finalList.remove(countMsisdn - 1);
	                        finalList.add(countMsisdn - 1, errVO);
	                        continue;
	                    }// end of if(!BTSLUtil.isValidMSISDN(msisdn))
	                     // check prefix of the MSISDN
	                    msisdnPrefix = PretupsBL.getMSISDNPrefix(msisdn); // get the
	                                                                      // prefix
	                                                                      // of the
	                                                                      // MSISDN
	                    networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
	                    if (networkPrefixVO == null) {
	                        if (LOG.isDebugEnabled()) {
	                            LOG.debug("processUploadedFileForBulkReg", "Not Network prefix found " + msisdn);
	                        }
	                        failCount++;
	                        RestrictedMsisdnLog.log(fileName, msisdn, "Not Network prefix found", "Fail", "Logged In UserID : " + userVO.getUserID());
	                        errVO.setLineNumber(String.valueOf(countMsisdn));
	                        errVO.setMsisdn(msisdn);
	                        errVO.setErrorCode(noPrefixMsg);
	                        finalList.remove(countMsisdn - 1);
	                        finalList.add(countMsisdn - 1, errVO);
	                        continue;
	                    }// end of if(networkPrefixVO == null)
	                    else if (!networkPrefixVO.getSeriesType().equals(subscriberType)) {
	                        if (LOG.isDebugEnabled()) {
	                            LOG.debug("processUploadedFileForBulkReg", "Not Network prefix found is of selected type " + msisdn);
	                        }
	                        failCount++;
	                        RestrictedMsisdnLog.log(null, msisdn, "Not Network prefix found is of selected type", "Fail", "Logged In UserID : " + userVO.getUserID());
	                        errVO.setLineNumber(String.valueOf(countMsisdn));
	                        errVO.setMsisdn(msisdn);
	                        errVO.setErrorCode(PrefixNotMatch);
	                        finalList.remove(countMsisdn - 1);
	                        finalList.add(countMsisdn - 1, errVO);
	                        continue;
	                    }
	                    // check network support of the MSISDN
	                    networkCode = networkPrefixVO.getNetworkCode();
	                    if (!networkCode.equals(userVO.getNetworkID())) {
	                        if (LOG.isDebugEnabled()) {
	                            LOG.debug("processUploadedFileForBulkReg", "Not supporting Network" + msisdn);
	                        }
	                        failCount++;
	                        RestrictedMsisdnLog.log(formFile.getName(), msisdn, "Not supporting Network", "Fail", "Logged In UserID : " + userVO.getUserID());
	                        errVO.setLineNumber(String.valueOf(countMsisdn));
	                        errVO.setMsisdn(msisdn);
	                        errVO.setErrorCode(unsupportedNwMsg);
	                        finalList.remove(countMsisdn - 1);
	                        finalList.add(countMsisdn - 1, errVO);
	                        continue;
	                    }// end of if(!networkCode.equals(userVO.getNetworkID()))
	                }// end of if(errVO.getErrorCode()==null)
	            }// end of while(finalListSize != countMsisdn)

	            // get a connection from the connection pool
				 RestrictedSubscriberWebDAO restrictedSubscriberWebDAO = new RestrictedSubscriberWebDAO();

	            // call DAO's method to upload the mobile numbers
	            restrictedSubscriberWebDAO.subsBulkRegistration(con, finalList, validateUser.getUserID(), userVO.getUserID(), formFile.getName(), canNotRegMsg, alreadyExistMsg);// call
	                                                                                                                                                                             // list
	            RestrictedSubscriberVO restrictedSubscriberVO = (RestrictedSubscriberVO) finalList.get(finalList.size() - 1);
	            long failCountFromDao = restrictedSubscriberVO.getFailCount();
	            responseVO.setErrorList(finalList);

	            responseVO.setTotalFailCount(String.valueOf(failCountFromDao + failCount));
	            responseVO.setProcessedRecs(noOfRec - duplicateRecs - (failCountFromDao + failCount)+"");
	            bufferReader.close();
	            fileReader.close();
	            // make Archive file on the server.
	            fileMoved = this.moveFileToArchive(filePathAndFileName, fileName);
	            if (!fileMoved) {
	                throw new BTSLBaseException(classname, "processUploadedFileForBulkReg", "restrictedsubs.seluserforbulkreg.error.filenomove", "userSelect");
	            }

	            if ((failCountFromDao + failCount + duplicateRecs) == 0) {
	            	con.commit();
	            	String msg = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.FILE_UPLOADED_SUCCESSFULLY, new String[] { "" });
                	responseVO.setMessage(msg);
                	responseVO.setStatus((HttpStatus.SC_OK));
                    response1.setStatus(HttpStatus.SC_OK);
                    responseVO.setErrorFlag(PretupsI.FALSE);
                 	responseVO.setNoOfRecords(String.valueOf(noOfRec));
	            	
                	final AdminOperationVO adminOperationVO = new AdminOperationVO();
                    adminOperationVO.setSource(PretupsI.LOGGER_RESTRICTED_LIST);
                    adminOperationVO.setDate(new Date());
                    adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
                    adminOperationVO
                        .setInfo(msg);
                    adminOperationVO.setLoginID(userVO.getLoginID());
                    adminOperationVO.setUserID(userVO.getUserID());
                    adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                    adminOperationVO.setNetworkCode(userVO.getNetworkID());
                    adminOperationVO.setMsisdn(userVO.getMsisdn());
                    AdminOperationLog.log(adminOperationVO);
	            	return responseVO;
	            }
	            if(Integer.parseInt(responseVO.getTotalFailCount()) == noOfRec) {
	            	con.rollback();
	            	responseVO.setNoOfRecords(String.valueOf(noOfRec));
	            	responseVO.getErrorList();
	            	responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
                	response1.setStatus(PretupsI.RESPONSE_FAIL);
	            	downloadErrorLogFile(userVO, responseVO);
	            	responseVO.setErrorFlag(PretupsI.TRUE);
	            	String msg = RestAPIStringParser.getMessage(locale,
                			PretupsErrorCodesI.FILE_UPLOAD_FAILED, new String[] { "" });
                	responseVO.setMessage(msg);
                	responseVO.setMessageCode(PretupsErrorCodesI.FILE_UPLOAD_FAILED);
                	throw new BTSLBaseException(classname, METHOD_NAME,PretupsErrorCodesI.FILE_UPLOAD_FAILED, "");
	            }
	            else {
	            	con.commit();
	            	String msg = RestAPIStringParser.getMessage(locale,
							PretupsErrorCodesI.UPLOAD_RESTRICTED_LIST_PARTIALLY_SUCCESSFULLY, new String[] { "" });
                	responseVO.setMessage(msg);
                	responseVO.setMessageCode(PretupsErrorCodesI.UPLOAD_RESTRICTED_LIST_PARTIALLY_SUCCESSFULLY);
                	responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
                	response1.setStatus(PretupsI.RESPONSE_SUCCESS);
                	responseVO.setErrorFlag(PretupsI.TRUE);
					final AdminOperationVO adminOperationVO = new AdminOperationVO();
                    adminOperationVO.setSource(PretupsI.LOGGER_RESTRICTED_LIST);
                    adminOperationVO.setDate(new Date());
                    adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
                    adminOperationVO
                        .setInfo(msg);
                    adminOperationVO.setLoginID(userVO.getLoginID());
                    adminOperationVO.setUserID(userVO.getUserID());
                    adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                    adminOperationVO.setNetworkCode(userVO.getNetworkID());
                    adminOperationVO.setMsisdn(userVO.getMsisdn());
                    AdminOperationLog.log(adminOperationVO);
                    downloadErrorLogFile(userVO, responseVO);
	            	
                    return responseVO;
	            }
	        } catch (BTSLBaseException e) {
	            LOG.error("processUploadedFileForBulkReg", "Exceptin:e=" + e);
	            LOG.errorTrace(METHOD_NAME, e);
	            file = new File(filePath, fileName);
	            boolean isDeleted = file.delete();
	            if(isDeleted){
	             LOG.debug(METHOD_NAME, "File deleted successfully");
	            }
	            if (LOG.isDebugEnabled()) {
	                LOG.debug("processUploadedFileForBulkReg", "The file is deleted");
	            }
	            throw e;
	        } catch (Exception e) {
	            LOG.error("processUploadedFileForBulkReg", "Exceptin:e=" + e);
	            LOG.errorTrace(METHOD_NAME, e);
	            file = new File(filePath, fileName);
	            boolean isDeleted = file.delete();
	            if(isDeleted){	             LOG.debug(METHOD_NAME, "File deleted successfully");
	            }
	            if (LOG.isDebugEnabled()) {
	                LOG.debug("processUploadedFileForBulkReg", "The file is deleted");
	            }
	            throw e;
	        } finally {
				try {
	                if (bufferReader != null) {
	                    bufferReader.close();
	                }
	            } catch (Exception e) {
	                LOG.errorTrace(METHOD_NAME, e);
	            }
	            try {
	                if (fileReader != null) {
	                    fileReader.close();
	                }
	            } catch (Exception e) {
	                LOG.errorTrace(METHOD_NAME, e);
	            }
	            try {
	                if (file != null) {
	                    file = null;
	                }
	            } catch (Exception e) {
	                LOG.errorTrace(METHOD_NAME, e);
	            }
	            if (LOG.isDebugEnabled()) {
	                LOG.debug("processUploadedFileForBulkReg", "Exit:forward="  );
	            }
	        }
	    }// end of processUploadedFileForBulkReg

	  private boolean isContain(ArrayList p_finalList, String p_msisdn) {
	        if (LOG.isDebugEnabled()) {
	            LOG.debug("isContain", " Entered p_finalList :" + p_finalList + "p_msisdn : " + p_msisdn);
	        }
	        boolean flag = false;
	        if (p_finalList != null) {
	            RestrictedSubscriberVO restrictedSubscriberVO;
	            int size = p_finalList.size();
	            for (int i = 0; i < size; i++) {
	                restrictedSubscriberVO = (RestrictedSubscriberVO) p_finalList.get(i);
	                if (restrictedSubscriberVO.getMsisdn().equals(p_msisdn)) {
	                    flag = true;
	                    break;
	                }
	            }// end of for(int i=0;i<size;i++)
	        }// end of if(p_finalList!=null)
	        if (LOG.isDebugEnabled()) {
	            LOG.debug("isContain", " Exiting File Moved=" + flag);
	        }
	        return flag;
	    }// end of isContain

	   
	  private boolean moveFileToArchive(String p_filePathAndFileName, String p_fileName) {
	        if (LOG.isDebugEnabled()) {
	            LOG.debug("moveFileToArchive", " Entered : p_filePathAndFileName : " + p_filePathAndFileName + "p_fileName: " + p_fileName);
	        }
	        File fileRead = new File(p_filePathAndFileName);
	        File fileArchive = new File(String.valueOf(Constants.getProperty("archiveFilePathForBulkRegistration")));
	        if (!fileArchive.isDirectory()) {
	            fileArchive.mkdirs();
	        }
	        fileArchive = new File(String.valueOf(Constants.getProperty("archiveFilePathForBulkRegistration") + p_fileName + "." + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime())); // to
	                                                                                                                                                                                          // make
	                                                                                                                                                                                          // the
	                                                                                                                                                                                          // new
	                                                                                                                                                                                          // file
	                                                                                                                                                                                          // name
	        boolean flag = fileRead.renameTo(fileArchive);
	        if (LOG.isDebugEnabled()) {
	            LOG.debug("moveFileToArchive", " Exiting File Moved=" + flag);
	        }
	        return flag;
	    }// end of moveFileToArchive

	
	
	
	    public static boolean uploadFileToServer(File p_formFile,byte []data, String p_dirPath, String p_contentType,  long p_fileSize) throws BTSLBaseException {
	        if (LOG.isDebugEnabled()) {
	            LOG.debug("uploadFileToServer",
	                "Entered :p_formFile=" + p_formFile + ", p_dirPath=" + p_dirPath + ", p_contentType=" + p_contentType  + ", p_fileSize=" + p_fileSize);
	        }
	        FileOutputStream outputStream = null;
	        boolean returnValue = false;
	        final String METHOD_NAME = "uploadFileToServer";
	        // modified by Manisha(18/01/08) use singal try catch
	        MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
	        try {
	            final File fileDir = new File(p_dirPath);
	            if (!fileDir.isDirectory()) {
	                fileDir.mkdirs();
	            }
	            if (!fileDir.exists()) {
	                LOG.debug("uploadFileToServer", "Directory does not exist: " + fileDir + " ");
	                throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", PretupsErrorCodesI.DIR_NOT_CREATED, "");
	            }

	            final File fileName = new File(p_dirPath, p_formFile.getName());
	            Path path = Paths.get( p_formFile.getName());
	            long fileSize = Files.size(path);
	            File file = new File(fileDir+ p_formFile.getName());
	            String    mimeType = mimeTypesMap.getContentType(file);
	            // if file already exist then show the error message.
	            if (p_formFile != null) {
	                if (fileSize <= 0) {
	                    throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", PretupsErrorCodesI.FILE_SIZE_ZERO, "");
	                } else if (fileSize > p_fileSize) {
	                    throw new BTSLBaseException("BTSLUtil", "uploadFileToServer",PretupsErrorCodesI.FILE_SIZE_LARGE, 0, new String[] { String.valueOf(p_fileSize) }, "");
	                }

	                boolean contentTypeExist = false;
	                if (p_contentType.contains(",")) {
	                    final String temp[] = p_contentType.split(",");
	                    for (int i = 0, j = temp.length; i < j; i++) {
	                        if (mimeType.equalsIgnoreCase(temp[i].trim())) {
	                            contentTypeExist = true;
	                            break;
	                        }
	                    }
	                } else if (mimeType.equalsIgnoreCase(p_contentType)) {
	                    contentTypeExist = true;
	                }

	                if (contentTypeExist) {
	                    if (fileName.exists()) {
	                        throw new BTSLBaseException("BTSLUtil", "uploadFileToServer", PretupsErrorCodesI.FILE_ALREADY_EXISTS, "");
	                    }
	                    outputStream = new FileOutputStream(fileName);
	                    outputStream.write(data);
	                    returnValue = true;
	                    if (LOG.isDebugEnabled()) {
	                        LOG.debug("uploadFileToServer", "File Uploaded Successfully");
	                    }
	                }
	                // if file is not a text file show error message
	                else {
	                    if (LOG.isDebugEnabled()) {
	                        LOG.debug(
	                            "uploadFileToServer",
	                            "Invalid content type: " + mimeType + " required is p_contentType: " + p_contentType + " p_formFile.getFileName(): " + p_formFile
	                                .getName());
	                    }
	                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_FILE_CONTENT, "");
	                }
	            }
	            // if there is no such file then show the error message
	            else {
	                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_FILE_EXIST, "");
	            }
	        } catch (BTSLBaseException be) {
	            throw be;
	        } catch (Exception e) {
	            LOG.error("uploadFileToServer", "Exception " + e.getMessage());
	            LOG.errorTrace(METHOD_NAME, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "BTSLUtil[uploadFileToServer]", "", "", "",
	                "Exception:" + e.getMessage());
	            throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, "");
	        } finally {
	        	try{
	                if (outputStream!= null){
	                	outputStream.close();
	                }
	              }
	              catch (IOException e){
	            	  LOG.error("An error occurred closing outputStream.", e);
	              }
	            if (LOG.isDebugEnabled()) {
	                LOG.debug("uploadFileToServer", "Exit :returnValue=" + returnValue);
	            }

	        }
	        return returnValue;
	    }





		@Override
		public ApprovalRestrictedListResponseVO approvalRestrictedList(Connection con, UserVO userVO, String userName,
				String categoryCode, String geoDomain, ApprovalRestrictedListResponseVO response)
				throws BTSLBaseException, Exception {
			        final String METHOD_NAME = "approvalRestrictedList";
			        if (LOG.isDebugEnabled()) {
			            LOG.debug("approvalRestrictedList", "Entered");
			        }
			        
			        ArrayList restrictedSubsList = null;
			        try {
		        		this.validateDomainAndCategory(con, userVO, geoDomain,  categoryCode);
		        	}
		        	catch(BTSLBaseException ex) {
		        		LOG.error(METHOD_NAME, "Exceptin:e=" + ex);
			            LOG.errorTrace(METHOD_NAME, ex);
			            throw ex;
			        } catch (Exception e) {
			            LOG.error(METHOD_NAME, "Exceptin:e=" + e);
			            LOG.errorTrace(METHOD_NAME, e);
			            throw e;
			        } 	        		
		        
			        String ownerID = null;
					
			            // First the User is validated.
			        	  UserValidationResponseVO validateUser= this.loadUserList(con,userVO, userName,categoryCode,geoDomain);
	
			        	  List<ApprovalRestrictedDeatils> approvalRestrictedDetailsList = new ArrayList<>();
			                ownerID = validateUser.getOwnerID();
			                RestrictedSubscriberWebDAO restrictedSubscriberWebDAO = new RestrictedSubscriberWebDAO();
			                // Load the details of subscribers based on the OwnerID and the
			                // status of subscriber is 'W-NEW'
			                restrictedSubsList = restrictedSubscriberWebDAO.loadSubsDetailForApproval(con, ownerID);
			                if (restrictedSubsList == null || restrictedSubsList.isEmpty()) {
			                	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_SUBSCRIBER_FOUND, "");

			                } else {
			                    // Set the ArrayList to the FormBean.Pass the controll to
			                    // show the restricted msisdn with information.
			                	for(int i =0; i<restrictedSubsList.size();i++) {
			                		RestrictedSubscriberVO rscSub= (RestrictedSubscriberVO)restrictedSubsList.get(i);
			                		ApprovalRestrictedDeatils appSub= new ApprovalRestrictedDeatils();
			                		appSub.setOwnerID(rscSub.getOwnerID());
			                		appSub.setSubscriberMobileNumber(rscSub.getMsisdn());
			                		appSub.setRegisterOn(rscSub.getCreatedOnAsString());
			                		ArrayList list =LookupsCache.loadLookupDropDown(PretupsI.SUBSRICBER_TYPE, true);
			                		for(int j=0;j<list.size();j++) {
			                			ListValueVO valueVO = (ListValueVO)list.get(j);
			                			if(valueVO.getValue().equals(rscSub.getSubscriberType())) {
			                				appSub.setSubscriberType(valueVO.getLabel());
			                			}
			                		}
			                		appSub.setStatusDes(rscSub.getStatusDes());
			                		appSub.setStatus(rscSub.getStatus());
			                		
			                		approvalRestrictedDetailsList.add(appSub);
			                		
			                	}
			                	response.setApprovalRestrictedDetailsList(approvalRestrictedDetailsList);
			                }
			                // Check if there is not any ,'N-New'records for approval create
			                // message and forward it to searchUserForApp.jsp otherwise load
			                // the subscriber details.
			                RestrictedSubscriberVO restrictedSubsVO = (RestrictedSubscriberVO) restrictedSubsList.get(0);
			                response.setStatusDes(restrictedSubsVO.getStatusDes());
			            
			            if (LOG.isDebugEnabled()) {
			                LOG.debug(classname, "Exit : "+ METHOD_NAME);
			            }
			        
			return response;
		}
	
	
		  public Integer updateApprovalSubscriberList(Connection con ,UserVO userVO,List<SubscriberDetailsRequestVO> requestVOList)throws BTSLBaseException, Exception {
		        final String METHOD_NAME = "updateApprovalSubscriberList";
		        if (LOG.isDebugEnabled()) {
		            LOG.debug("updateApprovalSubscriberList", "Entered ");
		        }
		        ArrayList<RestrictedSubscriberVO> confirmRestrictedList = new ArrayList();
		        SubscriberDetailsRequestVO restrictedSubscriberVO = null;
				RestrictedSubscriberWebDAO restrictedSubscriberWebDAO = null;
				   for (int iterateStart = 0, size = requestVOList.size(); iterateStart < size; iterateStart++) {
					   RestrictedSubscriberVO rscVO= new RestrictedSubscriberVO();
		                restrictedSubscriberVO =  requestVOList.get(iterateStart);
		                if (restrictedSubscriberVO.getStatus().equalsIgnoreCase(PretupsI.RES_MSISDN_STATUS_APPROVED) || restrictedSubscriberVO.getStatus().equalsIgnoreCase(PretupsI.RES_MSISDN_STATUS_REJECT) || restrictedSubscriberVO.getStatus().equalsIgnoreCase(PretupsI.RES_MSISDN_STATUS_DISCARD)) {
		                	rscVO.setApprovedBy(userVO.getActiveUserID());
		                	rscVO.setModifiedBy(userVO.getActiveUserID());
		                	rscVO.setMsisdn(restrictedSubscriberVO.getMsisdn());
		                	rscVO.setOwnerID(restrictedSubscriberVO.getOwnerID());
		                	rscVO.setStatus(restrictedSubscriberVO.getStatus());
		                	confirmRestrictedList.add(rscVO);
		                    
		                } else {
		                	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.STATUS_INVALID, "");
 
		                }
		            }
		            int updateCount = 0;
		          
		            restrictedSubscriberWebDAO = new RestrictedSubscriberWebDAO();
		            updateCount = restrictedSubscriberWebDAO.updateSubsListForApproval(con, confirmRestrictedList);
		            if(updateCount == requestVOList.size()) {
		            	final AdminOperationVO adminOperationVO = new AdminOperationVO();
	                    Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		            
		            	String msg= null;
		            	if(restrictedSubscriberVO.getStatus().equalsIgnoreCase(PretupsI.RES_MSISDN_STATUS_APPROVED)){
		            		msg = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_LIST_APPROVED_SUCCESSFULLY, new String[] { "" });
		            		 adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_APPROVE);
			                   
		            	}
		            	else {
		            		msg = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_LIST_REJECTED_SUCCESSFULLY, new String[] { "" });
		            		 adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_REJECT);
			                   
		            	}
		            	adminOperationVO.setSource(PretupsI.LOGGER_RESTRICTED_LIST);
	                    adminOperationVO.setDate(new Date());
	                    adminOperationVO
	                        .setInfo(msg);
	                    adminOperationVO.setLoginID(userVO.getLoginID());
	                    adminOperationVO.setUserID(userVO.getUserID());
	                    adminOperationVO.setCategoryCode(userVO.getCategoryCode());
	                    adminOperationVO.setNetworkCode(userVO.getNetworkID());
	                    adminOperationVO.setMsisdn(userVO.getMsisdn());
	                    AdminOperationLog.log(adminOperationVO);
	                
		            	con.commit();
		            }
		            else if(updateCount>0) {
		            	Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		            	final AdminOperationVO adminOperationVO = new AdminOperationVO();
	                    
		            	String msg= null;
		            	if(restrictedSubscriberVO.getStatus().equalsIgnoreCase(PretupsI.RES_MSISDN_STATUS_APPROVED)){
		            		msg = RestAPIStringParser.getMessage(locale,
								PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_LIST_APPROVED_PARTIALLY_SUCCESSFULLY, new String[] { "" });
		            		adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_APPROVE);
				                
		            	}
		            	else {
		            		msg = RestAPIStringParser.getMessage(locale,
									PretupsErrorCodesI.RESTRICTED_SUBSCRIBER_LIST_REJECTED_PARTIALLY_SUCCESSFULLY, new String[] { "" });
		           		 	adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_REJECT);
					        
		            	}
		            	adminOperationVO.setSource(PretupsI.LOGGER_RESTRICTED_LIST);
	                    adminOperationVO.setDate(new Date());
	                    adminOperationVO
	                        .setInfo(msg);
	                    adminOperationVO.setLoginID(userVO.getLoginID());
	                    adminOperationVO.setUserID(userVO.getUserID());
	                    adminOperationVO.setCategoryCode(userVO.getCategoryCode());
	                    adminOperationVO.setNetworkCode(userVO.getNetworkID());
	                    adminOperationVO.setMsisdn(userVO.getMsisdn());
	                    AdminOperationLog.log(adminOperationVO);
	                
		            	con.commit();
		            }
		            else {
						con.rollback();
					}
		            return updateCount;
		    }





			 //Unblack api's code starts
	    
	    @Override
		public LoadSubscriberListForUnBlackResponseVO loadSubscriberListForUnBlack(MultiValueMap<String, String> headers,
				HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con,
				MComConnectionI mcomCon, Locale locale, UserVO userVO, LoadSubscriberListForUnBlackResponseVO response,
				String msisdnStr, String ownerID, String cp2pPayer,String cp2pPayee,String c2sPayee) throws BTSLBaseException {
			final String METHOD_NAME = "loadSubscriberListForUnblack";
			if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
			}
			
			StringTokenizer tokenizer = null;
	        String delimiter = Constants.getProperty("DelimiterForUnBlack");
	        RestrictedSubscriberWebDAO restrictedSubscriberWebDAO = null;
	        
	        ArrayList listForSuspendOrResume = null;
	        ArrayList validMsisdnList = null;
	        String msisdn;
	        String filteredMsisdn;
	        String msisdnPrefix;
	        NetworkPrefixVO networkPrefixVO = null;
	        String networkCode;
	        KeyArgumentVO keyArgumentVO = null;
	        ArrayList errorList = null;
			
			try {
				
				
				errorList = new ArrayList();
				
				//validation starts here
				if (BTSLUtil.isNullString(msisdnStr)) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.MSISDN_REQ, 0, null);
	            }
				
				if (BTSLUtil.isNullString(ownerID)) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.OWNER_ID_REQ, 0, null);
	            }
				
				if (BTSLUtil.isNullString(cp2pPayer)) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.P2P_PAYER_REQ, 0, null);
	            }
				
				if (BTSLUtil.isNullString(cp2pPayee)) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.P2P_PAYEE_REQ, 0, null);
	            }
				
				if (BTSLUtil.isNullString(c2sPayee)) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.C2S_PAYEE_REQ, 0, null);
	            }
				
				
				if (msisdnStr.length() > 500) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.MSISDN_STRING_LENGTH_EXCEEDED, 0, null);
	            }
				
				//validation ends here
				
	            tokenizer = new StringTokenizer(msisdnStr, delimiter);
	            validMsisdnList = new ArrayList();
	            
	            
	            if (tokenizer != null) {
	                while (tokenizer.hasMoreTokens()) {
	                    msisdn = tokenizer.nextToken().trim();
	                    keyArgumentVO = new KeyArgumentVO();
	                    String[] msisdnArr = new String[1];
	                    msisdnArr[0] = msisdn;
	                    // Change ID=ACCOUNTID
	                    // FilteredMSISDN is replaced by
	                    // getFilteredIdentificationNumber
	                    // This is done because this field can contains msisdn or
	                    // account id
	                    filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(msisdn); // before
	                                                                                        // process
	                                                                                        // MSISDN
	                                                                                        // filter
	                                                                                        // each-one
	                    // check for valid MSISDN
	                    // Change ID=ACCOUNTID
	                    // isValidMsisdn is replaced by isValidIdentificationNumber
	                    // This is done because this field can contains msisdn or
	                    // account id
	                    if (!BTSLUtil.isValidIdentificationNumber(filteredMsisdn)) {
	                        if (LOG.isDebugEnabled()) {
	                            LOG.debug("loadSubsForUnBlack", "Not a valid MSISDN " + msisdn);
	                        }
	                        //BlackListLog.log("UNBLACKLIST", thisForm.getFileName(), msisdn, "Not a valid MSISDN", "Fail", "Logged In UserID : " + userVO.getUserID());
	                        keyArgumentVO.setArguments(msisdnArr);
	                        //keyArgumentVO.setKey("restrictedsubs.loadsubsforunblack.err.msg.novalidmsidn");
	                        keyArgumentVO.setKey(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NO_VALID_MSISDN, keyArgumentVO.getArguments()));
	                        errorList.add(keyArgumentVO);
	                        continue;
	                    }
	                    // check prefix of the MSISDN
	                    //get the prefix of the msisdn
	                    msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn); 
	                    networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
	                    if (networkPrefixVO == null) {
	                        if (LOG.isDebugEnabled()) {
	                            LOG.debug("loadSubsForUnBlack", "Not Network prefix found " + msisdn);
	                        }
	                        keyArgumentVO.setArguments(msisdnArr);
	                        keyArgumentVO.setKey(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NO_PREFIX_FOUND, keyArgumentVO.getArguments()));
	                        errorList.add(keyArgumentVO);
	                        continue;
	                    }
	                    // check network support of the MSISDN
	                    networkCode = networkPrefixVO.getNetworkCode();
	                    if (!networkCode.equals(userVO.getNetworkID())) {
	                        keyArgumentVO.setArguments(msisdnArr);
	                        keyArgumentVO.setKey(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NO_ETS_SUPPORT, keyArgumentVO.getArguments()));
	                        errorList.add(keyArgumentVO);
	                        continue;
	                    }

	                    // Check for duplicate entry in the MSISDN's list
	                    if (validMsisdnList.contains(filteredMsisdn)) {
	                        if (LOG.isDebugEnabled()) {
	                            LOG.debug("loadSubsForUnBlack", "Duplicate MSISDN : " + msisdn);
	                        }
	                        //BlackListLog.log("UNBLACKLIST", thisForm.getFileName(), msisdn, "Duplicate MSISDN", "Fail", "Logged In UserID : " + userVO.getUserID());
	                        keyArgumentVO.setArguments(msisdnArr);
	                        //keyArgumentVO.setKey("restrictedsubs.loadsubsforunblack.err.msg.duplicatemsisdn");
	                        keyArgumentVO.setKey(RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.DUPLICATED_MSISDN, keyArgumentVO.getArguments()));
	                        errorList.add(keyArgumentVO);
	                        continue;
	                    }
	                    validMsisdnList.add(filteredMsisdn);
	                }// end of while loop
	            }
	            // Call DAO's method to load the list of subscriber
	            // addded for C2S Payee and Cp2P Payee Date 04/02/08
	            // cp2p_payer,cp2p_payee,c2s_payee
	            
	            boolean rset_Status = false;
	            String cp2p_payer = null;
	            String cp2p_payee = null;
	            String c2s_payee = null;
	            
	            if (!cp2pPayer.equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
	                cp2p_payer = null;
	            } else {
	                cp2p_payer = PretupsI.RES_MSISDN_UNBLACKLIST_STATUS;
	                rset_Status = true;
	            }

	            if (!cp2pPayee.equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
	                cp2p_payee = null;
	            } else {
	                cp2p_payee = PretupsI.RES_MSISDN_UNBLACKLIST_STATUS;
	                rset_Status = true;
	            }
	            if (!c2sPayee.equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
	                c2s_payee = null;
	            } else {
	                c2s_payee = PretupsI.RES_MSISDN_UNBLACKLIST_STATUS;
	                rset_Status = true;
	            }
	            if (!rset_Status) {
	                cp2p_payer = PretupsI.RES_MSISDN_UNBLACKLIST_STATUS;
	            }
	            // end of C2S Payee and Cp2P Payee
	            if (validMsisdnList != null && !validMsisdnList.isEmpty()) {
	                
	                restrictedSubscriberWebDAO = new RestrictedSubscriberWebDAO();
	                response.setListForUnBlack(restrictedSubscriberWebDAO.loadSubscribersListForUnblack(con, validMsisdnList, errorList, ownerID, PretupsI.MULTIPLE_SUBSCRIBER_SELECTED, userVO.getUserID(), cp2p_payer, cp2p_payee, c2s_payee,response,locale));
	                
	                response.setStatus((HttpStatus.SC_OK));
	    			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
	    			response.setMessage(resmsg);
	    			response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);
	            } else {
	            	response.setErrorList(errorList);
	                throw new BTSLBaseException(this, "loadSubsForUnBlack", errorList, "multipleSubsSel");
	            }
				
				
			}
			finally {
				if (LOG.isDebugEnabled()) {
                    LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
				}
			}
			return response;
		}





		@Override
		public BaseResponse unBlackListAllSubscriber(MultiValueMap<String, String> headers,
				HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con,
				MComConnectionI mcomCon, Locale locale, UserVO userVO, BaseResponse response,
				UnBlackListAllSubscriberRequestVO requestVO) throws BTSLBaseException, SQLException {
			final String METHOD_NAME = "unBlackListAllSubscriber";
			if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
			}
			
			RestrictedSubscriberVO resSubsVO = new RestrictedSubscriberVO();
            Date currentDate = new Date();
            
			try {
				
				
				//validation starts here
				
				if (BTSLUtil.isNullString(requestVO.getOwnerID())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.OWNER_ID_REQ, 0, null);
	            }
				
				if (BTSLUtil.isNullString(requestVO.getCp2pPayer())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.P2P_PAYER_REQ, 0, null);
	            }
				
				if (BTSLUtil.isNullString(requestVO.getCp2pPayee())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.P2P_PAYEE_REQ, 0, null);
	            }
				
				if (BTSLUtil.isNullString(requestVO.getC2sPayee())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.C2S_PAYEE_REQ, 0, null);
	            }
				
				//validation ends here
				
				resSubsVO.setModifiedOn(currentDate);
	            resSubsVO.setModifiedBy(userVO.getUserID());
	            resSubsVO.setOwnerID(requestVO.getOwnerID());
				
				// addded for C2S Payee and Cp2P Payee
	            boolean rset_Status = false;
	            if (!requestVO.getCp2pPayer().equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
	                resSubsVO.setBlackListStatus(null);
	            } else {
	                resSubsVO.setBlackListStatus(PretupsI.RES_MSISDN_UNBLACKLIST_STATUS);
	                rset_Status = true;
	            }

	            if (!requestVO.getCp2pPayee().equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
	                resSubsVO.setCp2pPayeeStatus(null);
	            } else {
	                resSubsVO.setCp2pPayeeStatus(PretupsI.RES_MSISDN_UNBLACKLIST_STATUS);
	                rset_Status = true;
	            }
	            if (!requestVO.getC2sPayee().equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
	                resSubsVO.setC2sPayeeStatus(null);
	            } else {
	                resSubsVO.setC2sPayeeStatus(PretupsI.RES_MSISDN_UNBLACKLIST_STATUS);
	                rset_Status = true;
	            }
	            if (!rset_Status) {
	                resSubsVO.setBlackListStatus(PretupsI.RES_MSISDN_UNBLACKLIST_STATUS);
	            }
	            // end of C2S Payee and Cp2P Payee
				
                RestrictedSubscriberWebDAO resSubsWebDAO = new RestrictedSubscriberWebDAO();
                if (resSubsWebDAO.isSubscriberExist(con, resSubsVO.getOwnerID())) {
                    if (resSubsWebDAO.changeBlackListStatusForAll(con, resSubsVO) > 0) {
                    	mcomCon.finalCommit();

                        // this code change for logs entry for the successfully
                        // unblack listed MSISDNs
                      
                        
                        response.setStatus((HttpStatus.SC_OK));
            			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UNBLACK_SUCCESS, null);
            			response.setMessage(resmsg);
            			response.setMessageCode(PretupsErrorCodesI.UNBLACK_SUCCESS);
            			
                    } else {
                    	mcomCon.finalRollback();
                    	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.UNBLACK_FAIL, 0, null);
                    }
                } else {
                	mcomCon.finalRollback();
                	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.NO_SUBS_FOUND, 0, null);
                }
				
				
			}
			finally {
				if (LOG.isDebugEnabled()) {
                    LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
				}
			}
			return response;
		}





		@Override
		public BaseResponse unBlackListSelectedSubscriber(MultiValueMap<String, String> headers,
				HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con,
				MComConnectionI mcomCon, Locale locale, UserVO userVO, BaseResponse response,
				UnBlackListSelectedSubscriberRequestVO requestVO) throws SQLException, BTSLBaseException {
			final String METHOD_NAME = "unBlackListSelectedSubscriber";
			if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
			}
			try {
				//validation starts here
				if (requestVO.getConfirmListForUnBlack() == null || requestVO.getConfirmListForUnBlack().isEmpty()) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.SUBSCRIBER_LIST_EMPTY, 0, null);
	            }
				
				if (BTSLUtil.isNullString(requestVO.getOwnerID())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.OWNER_ID_REQ, 0, null);
	            }
				
				if (BTSLUtil.isNullString(requestVO.getCp2pPayer())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.P2P_PAYER_REQ, 0, null);
	            }
				
				if (BTSLUtil.isNullString(requestVO.getCp2pPayee())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.P2P_PAYEE_REQ, 0, null);
	            }
				
				if (BTSLUtil.isNullString(requestVO.getC2sPayee())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.C2S_PAYEE_REQ, 0, null);
	            }
				//validation ends here
				
				 RestrictedSubscriberWebDAO restrictedSubscriberWebDAO = new RestrictedSubscriberWebDAO();
				 ArrayList confirmList = requestVO.getConfirmListForUnBlack();
	                
	             // addded for C2S Payee and Cp2P Payee Date 04/02/08
	                String cp2p_payer = null;
	                String cp2p_payee = null;
	                String c2s_payee = null;
	                if (!(PretupsI.SELECT_CHECKBOX).equalsIgnoreCase(requestVO.getCp2pPayer())) {
	                    cp2p_payer = null;
	                } else {
	                    cp2p_payer = PretupsI.RES_MSISDN_UNBLACKLIST_STATUS;
	                }
	                if (!(PretupsI.SELECT_CHECKBOX).equalsIgnoreCase(requestVO.getCp2pPayee())) {
	                    cp2p_payee = null;
	                } else {
	                    cp2p_payee = PretupsI.RES_MSISDN_UNBLACKLIST_STATUS;
	                }
	                if (!(PretupsI.SELECT_CHECKBOX).equalsIgnoreCase(requestVO.getC2sPayee())) {
	                    c2s_payee = null;
	                } else {
	                    c2s_payee = PretupsI.RES_MSISDN_UNBLACKLIST_STATUS;
	                }
	                if (((BTSLUtil.isNullString(cp2p_payer)) && (BTSLUtil.isNullString(cp2p_payee))) && (BTSLUtil.isNullString(c2s_payee))) {
	                    cp2p_payer = PretupsI.RES_MSISDN_UNBLACKLIST_STATUS;
	                    // end of C2S Payee and Cp2P Payee
	                }
	                
	                Date date = new Date();
	                String str = restrictedSubscriberWebDAO.unBlackSelSubscriber(con, confirmList, userVO.getUserID(), date, requestVO.getOwnerID(), cp2p_payer, cp2p_payee, c2s_payee);
	                if (str.length() == 0) {
	                    if (LOG.isDebugEnabled()) {
	                        LOG.debug("blackListAllSubs", "Specified MSISDN(s) has been unblack listed successfully.");
	                    }
	                    mcomCon.finalCommit();
	              
	                    response.setStatus((HttpStatus.SC_OK));
            			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UNBLACK_SUCCESS, null);
            			response.setMessage(resmsg);
            			response.setMessageCode(PretupsErrorCodesI.UNBLACK_SUCCESS);
	                } else {
	                	mcomCon.finalRollback();
	                	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.UNBLACK_SELECTED_FAILURE, 0, null);
	                }
			
			
			}
			finally {
				if (LOG.isDebugEnabled()) {
                    LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
				}
			}
			return response;
		}
	  
	    //Unblack api's code ends
	
		   public void downloadErrorLogFile(UserVO userVO, UploadFileResponseVO response) {
				final String METHOD_NAME = "downloadErrorLogFile";
				if (LOG.isDebugEnabled())
					LOG.debug(METHOD_NAME, "Entered");
				//ActionForward forward = null;
				try {
					ArrayList errorList = response.getErrorList();
					String filePath = Constants.getProperty("bulkRegistrationFilePath");
					try {
						File fileDir = new File(filePath);
						if (!fileDir.isDirectory())
							fileDir.mkdirs();
					} catch (Exception e) {
						LOG.errorTrace(METHOD_NAME, e);
						LOG.error(METHOD_NAME, "Exception" + e.getMessage());
						throw new BTSLBaseException(classname, METHOD_NAME, "directory not created", 0, null);
					}
					String fileName = "downloadErrorLogFile"
							
							+ BTSLUtil.getFileNameStringFromDate(new Date()) + ".csv";
					
					this.writeDataMsisdnInFileDownload(errorList, fileName, filePath, userVO.getNetworkID(),
							fileName, true);
					 
					File error = new File(filePath+fileName);
					byte[] fileContent = FileUtils.readFileToByteArray(error);
					String encodedString = Base64.getEncoder().encodeToString(fileContent);
					response.setFileAttachment(encodedString);
					response.setFileName(fileName);
					response.setFileType("csv");
				} catch (Exception e) {
					LOG.error(METHOD_NAME, "Exception:e=" + e);
					LOG.errorTrace(METHOD_NAME, e);

				} /*finally {
					if (LOG.isDebugEnabled())
						LOG.debug(METHOD_NAME, "Exiting:forward=" + forward);
				}*/

			}
		    
		    public void writeDataMsisdnInFileDownload(ArrayList errorList,String _fileName,String filePath,String _networkCode, String uploadedFileNamePath,Boolean headval) 

		    {
		    	   	final String methodName = "writeDataMsisdnInFileDownload";
		            String[] splitFileName = uploadedFileNamePath.split("/");
		            String uploadedFileName = splitFileName[(splitFileName.length)-1];
		        	if (LOG.isDebugEnabled()){
		        		LOG.debug(methodName,"Entered: "+methodName);
		            }       
		            Writer out =null;
		            File newFile = null;
		            File newFile1 = null;
		            String fileHeader=null;
		            String fileName=null;
		            try
		            {
		                 
		                Date date= new Date();
		                newFile1=new File(filePath);
		                if(! newFile1.isDirectory())
		            	 newFile1.mkdirs();
		                fileName=filePath+_fileName;
		                LOG.debug(methodName,"fileName := "+fileName);
		                if(headval){
		                	fileHeader=Constants.getProperty("ERROR_FILE_HEADER_MOVEUSER");
		                }
		                else{
		                fileHeader=Constants.getProperty("ERROR_FILE_HEADER_PAYOUT");
		                }
		                newFile = new File(fileName);
		                out = new OutputStreamWriter(new FileOutputStream(newFile));
		                out.write(fileHeader +"\n");
		                List<RestrictedSubscriberVO> filterList = (List<RestrictedSubscriberVO>) errorList.stream().filter(o->((((RestrictedSubscriberVO)o).getErrorCode()!=null ) && !((RestrictedSubscriberVO)o).getErrorCode().equals("NEWLINE")) ).collect(Collectors.toList());
		                filterList.sort ((o1,o2)->Integer.parseInt(((RestrictedSubscriberVO) o1).getLineNumber())-(Integer.parseInt(((RestrictedSubscriberVO) o2).getLineNumber())));
		                
		                for (Iterator<RestrictedSubscriberVO> iterator = filterList.iterator(); iterator.hasNext();) {
		    				
		                	RestrictedSubscriberVO listValueVO =iterator.next();
		                		out.write(listValueVO.getLineNumber().concat(","));
		                		out.write(listValueVO.getMsisdn().concat(","));
		                		
		                    	out.write(listValueVO.getErrorCode()+",");
		                	
		                	out.write(",");
		                	out.write("\n");
		                }
		    			out.write("End");
		    			
		            }
		            catch(Exception e)
		            {
		               
		            	LOG.errorTrace(methodName, e);
		                EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"writeDataInFile[writeDataInFile]","","","","Exception:= "+e.getMessage());
		                 }
		            finally
		            {
		            	if (LOG.isDebugEnabled()){
		            		LOG.debug(methodName,"Exiting... ");
		            	}
		                if (out!=null)
		                	try{
		                		out.close();
		                		}
		                catch(Exception e){
		                	LOG.errorTrace(methodName, e);
		                }
		                	
		            }
		    	}


//**********BlackList api's code starts********//
		
		@Override
		public LoadSubscriberListForBlackListSingleResponseVO loadSubscriberListForBlackListSingle(MultiValueMap<String, String> headers,
				HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con,
				MComConnectionI mcomCon, Locale locale, UserVO userVO, LoadSubscriberListForBlackListSingleResponseVO response, String msisdnStr,
				String ownerID, String cp2pPayer,String cp2pPayee,String c2sPayee) throws BTSLBaseException {
			final String METHOD_NAME = "loadSubscriberListForBlackListSingle";
			if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
			}
			
			LoadSubscriberListForBlackListSingleVO loadSubscriberListForBlackListSingleVO = null;
			try {
				
				//validation starts here
				if (BTSLUtil.isNullString(msisdnStr)) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.MSISDN_REQ, 0, null);
	            }
				
				if (BTSLUtil.isNullString(ownerID)) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.OWNER_ID_REQ, 0, null);
	            }
				
				if (BTSLUtil.isNullString(cp2pPayer)) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.P2P_PAYER_REQ, 0, null);
	            }
				
				if (BTSLUtil.isNullString(cp2pPayee)) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.P2P_PAYEE_REQ, 0, null);
	            }
				
				if (BTSLUtil.isNullString(c2sPayee)) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.C2S_PAYEE_REQ, 0, null);
	            }
				
				
				if (msisdnStr.length() > 500) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.MSISDN_STRING_LENGTH_EXCEEDED, 0, null);
	            }
				
				//validation ends here
				
				
				loadSubscriberListForBlackListSingleVO = new LoadSubscriberListForBlackListSingleVO();
				
				// Check Msisdn prefix and for supported network
                // Change ID=ACCOUNTID
                // FilteredMSISDN is replaced by getFilteredIdentificationNumber
                // This is done because this field can contains msisdn or
                // account id
                String msisdn = PretupsBL.getFilteredIdentificationNumber(msisdnStr);
                NetworkPrefixVO networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(PretupsBL.getMSISDNPrefix(msisdn));
                if (networkPrefixVO == null) {
                	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.INVALID_PREFIX, 0, null);
                }
                String networkCode = networkPrefixVO.getNetworkCode();
                
                if (networkCode == null || !networkCode.equals(userVO.getNetworkID())) {
                	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.UNSUPPORTED_NETWORK_ERROR, 0, null);
                }
                
                
                
                RestrictedSubscriberWebDAO restrictedSubscriberWebDAO = new RestrictedSubscriberWebDAO();
                // Changed by ankit zindal
                // Reason is that previously unfiltered msisdn is passed in DAO.
                // Change ID=ACCOUNTID
                // Date =27/12/06
                RestrictedSubscriberVO resSubsVO = restrictedSubscriberWebDAO.loadResSubsDetails(con, msisdn, ownerID);
                
                
               
             // addded for C2S Payee and Cp2P Payee Date 04/02/08
                boolean restSubscriber = false;
                boolean blacklistStatusSelected = false;
                boolean Cp2pPayerSelect = false;
                boolean Cp2pPayeeSelect = false;
                boolean C2sPayeeSelect = false;

                loadSubscriberListForBlackListSingleVO.setCp2pPayerStatusFlag(true);
                loadSubscriberListForBlackListSingleVO.setCp2pPayeeStatusFlag(true);
                loadSubscriberListForBlackListSingleVO.setC2sPayeeStatusFlag(true);
                // End of C2S Payee and Cp2P Payee

                if (resSubsVO != null) {
                    // addded for C2S Payee and Cp2P Payee Date 04/02/08
                    if (cp2pPayer.equals(PretupsI.SELECT_CHECKBOX)) {
                    	loadSubscriberListForBlackListSingleVO.setCp2pPayerStatusFlag(false);
                        blacklistStatusSelected = true;
                        if (PretupsI.RES_MSISDN_BLACKLIST_STATUS.equals(resSubsVO.getBlackListStatus())) {
                            Cp2pPayerSelect = true;
                            restSubscriber = true;
                        }
                    }
                    if (cp2pPayee.equals(PretupsI.SELECT_CHECKBOX)) {
                    	loadSubscriberListForBlackListSingleVO.setCp2pPayeeStatusFlag(false);
                        blacklistStatusSelected = true;
                        if (PretupsI.RES_MSISDN_BLACKLIST_STATUS.equals(resSubsVO.getCp2pPayeeStatus())) {
                            Cp2pPayeeSelect = true;
                            restSubscriber = true;
                        }
                    }
                    if (c2sPayee.equals(PretupsI.SELECT_CHECKBOX)) {
                    	loadSubscriberListForBlackListSingleVO.setC2sPayeeStatusFlag(false);
                        blacklistStatusSelected = true;
                        if (PretupsI.RES_MSISDN_BLACKLIST_STATUS.equals(resSubsVO.getC2sPayeeStatus())) {
                            C2sPayeeSelect = true;
                            restSubscriber = true;
                        }
                    }
                    if (!blacklistStatusSelected) {
                        if (PretupsI.RES_MSISDN_BLACKLIST_STATUS.equals(resSubsVO.getBlackListStatus())) {
                            restSubscriber = true;
                            if (!(cp2pPayer).equals(resSubsVO.getBlackListStatus())) {
                            	loadSubscriberListForBlackListSingleVO.setCp2pPayerStatusFlag(true);
                            }
                        } else {
                            restSubscriber = false;
                        }
                    } else {
                        if ((loadSubscriberListForBlackListSingleVO.isCp2pPayerStatusFlag() == false) && ((loadSubscriberListForBlackListSingleVO.isCp2pPayeeStatusFlag() == false) && (loadSubscriberListForBlackListSingleVO.isC2sPayeeStatusFlag() == false))) {
                            if (Cp2pPayerSelect && (Cp2pPayeeSelect && C2sPayeeSelect)) {
                                restSubscriber = true;
                            } else {
                                restSubscriber = false;
                            }
                        } else if ((loadSubscriberListForBlackListSingleVO.isCp2pPayerStatusFlag() == false) && (loadSubscriberListForBlackListSingleVO.isCp2pPayeeStatusFlag() == false)) {
                            if (Cp2pPayerSelect && Cp2pPayeeSelect) {
                                restSubscriber = true;
                            } else {
                                restSubscriber = false;
                            }
                        } else if ((loadSubscriberListForBlackListSingleVO.isCp2pPayerStatusFlag() == false) && (loadSubscriberListForBlackListSingleVO.isC2sPayeeStatusFlag() == false)) {
                            if (Cp2pPayerSelect && C2sPayeeSelect) {
                                restSubscriber = true;
                            } else {
                                restSubscriber = false;
                            }
                        } else if ((loadSubscriberListForBlackListSingleVO.isCp2pPayeeStatusFlag() == false) && (loadSubscriberListForBlackListSingleVO.isC2sPayeeStatusFlag() == false)) {
                            if (Cp2pPayeeSelect && C2sPayeeSelect) {
                                restSubscriber = true;
                            } else {
                                restSubscriber = false;
                            }
                        }
                        // End of C2S Payee and Cp2P Payee
                    }
                    if (restSubscriber) {
                        response.setIsBlackListed(PretupsI.TRUE);
                        response.setCp2pPayer(resSubsVO.getBlackListStatus());
                        // addded for C2S Payee and Cp2P Payee Date 04/02/08
                        response.setCp2pPayee(resSubsVO.getCp2pPayeeStatus());
                        response.setC2sPayee(resSubsVO.getC2sPayeeStatus());
                        // End of C2S Payee and Cp2P Payee
                        
                        
                      //getting amounts for display data starts
                        if(resSubsVO.getMinTxnAmount() == 0) {
                        	resSubsVO.setMinTxnAmtForDisp(PretupsI.ZERO);
                        }else {
                        	resSubsVO.setMinTxnAmtForDisp(String.valueOf(resSubsVO.getMinTxnAmount()));
                        }
                        
                        if(resSubsVO.getMaxTxnAmount() == 0) {
                        	resSubsVO.setMaxTxnAmtForDisp(PretupsI.ZERO);
                        }else {
                        	resSubsVO.setMaxTxnAmtForDisp(String.valueOf(resSubsVO.getMaxTxnAmount()));
                        }
                        
                        if(resSubsVO.getTotalTxnCount() == 0) {
                        	resSubsVO.setTotalTxnCountForDisp(PretupsI.ZERO);
                        }else {
                        	resSubsVO.setTotalTxnCountForDisp(String.valueOf(resSubsVO.getTotalTxnCount()));
                        }
                        
                        if(resSubsVO.getTotalTransferAmount() == 0) {
                        	resSubsVO.setTotalTransferAmountForDisp(PretupsI.ZERO);
                        }else {
                        	resSubsVO.setTotalTransferAmountForDisp(String.valueOf(resSubsVO.getTotalTransferAmount()));
                        }
                        
                        if(resSubsVO.getMonthlyLimit() == 0) {
                        	resSubsVO.setMonthlyLimitForDisp(PretupsI.ZERO);
                        }else {
                        	resSubsVO.setMonthlyLimitForDisp(String.valueOf(resSubsVO.getMonthlyLimit()));
                        }
                      //getting amounts for display data ends

                        
                        
                        response.setSubscriberCode(resSubsVO.getEmployeeCode());
                        response.setSubscriberName(resSubsVO.getEmployeeName());
                        response.setMinTransferAmount(resSubsVO.getMinTxnAmtForDisp());
                        response.setMaxTransferAmount(resSubsVO.getMaxTxnAmtForDisp());
                        response.setTotalTxnCount(resSubsVO.getTotalTxnCountForDisp());
                        response.setTotalTxnAmount(resSubsVO.getTotalTransferAmountForDisp());
                        response.setMonthlyTransferLimit(resSubsVO.getMonthlyLimitForDisp());
                        response.setSubscriberStatus(resSubsVO.getStatus());
                        response.setRegisteredOn(resSubsVO.getCreatedOnAsString());
                        
                        
                        String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUBSCRIBER_ALREADY_BLACKLISTED, null);
                        response.setStatus((HttpStatus.SC_OK));
                        response.setMessage(resmsg);
    	    			response.setMessageCode(PretupsErrorCodesI.SUBSCRIBER_ALREADY_BLACKLISTED);
                        
                    } else {
                        // addded for C2S Payee and Cp2P Payee Date 04/02/08
                        if (PretupsI.RES_MSISDN_BLACKLIST_STATUS.equals(resSubsVO.getBlackListStatus())) {
                            response.setCp2pPayer(resSubsVO.getBlackListStatus());
                        }
                        if (PretupsI.RES_MSISDN_BLACKLIST_STATUS.equals(resSubsVO.getCp2pPayeeStatus())) {
                            response.setCp2pPayee(resSubsVO.getCp2pPayeeStatus());
                        }
                        if (PretupsI.RES_MSISDN_BLACKLIST_STATUS.equals(resSubsVO.getC2sPayeeStatus())) {
                            response.setC2sPayee(resSubsVO.getC2sPayeeStatus());
                        }
                        // end of C2S Payee and Cp2P Payee
                        
                        
                        //getting amounts for display data starts
                        
                        if(resSubsVO.getMinTxnAmount() == 0) {
                        	resSubsVO.setMinTxnAmtForDisp(PretupsI.ZERO);
                        }else {
                        	resSubsVO.setMinTxnAmtForDisp(String.valueOf(resSubsVO.getMinTxnAmount()));
                        }
                        
                        if(resSubsVO.getMaxTxnAmount() == 0) {
                        	resSubsVO.setMaxTxnAmtForDisp(PretupsI.ZERO);
                        }else {
                        	resSubsVO.setMaxTxnAmtForDisp(String.valueOf(resSubsVO.getMaxTxnAmount()));
                        }
                        
                        if(resSubsVO.getTotalTxnCount() == 0) {
                        	resSubsVO.setTotalTxnCountForDisp(PretupsI.ZERO);
                        }else {
                        	resSubsVO.setTotalTxnCountForDisp(String.valueOf(resSubsVO.getTotalTxnCount()));
                        }
                        
                        if(resSubsVO.getTotalTransferAmount() == 0) {
                        	resSubsVO.setTotalTransferAmountForDisp(PretupsI.ZERO);
                        }else {
                        	resSubsVO.setTotalTransferAmountForDisp(String.valueOf(resSubsVO.getTotalTransferAmount()));
                        }
                        
                        if(resSubsVO.getMonthlyLimit() == 0) {
                        	resSubsVO.setMonthlyLimitForDisp(PretupsI.ZERO);
                        }else {
                        	resSubsVO.setMonthlyLimitForDisp(String.valueOf(resSubsVO.getMonthlyLimit()));
                        }
                      //getting amounts for display data ends
                        
                        response.setSubscriberCode(resSubsVO.getEmployeeCode());
                        response.setSubscriberName(resSubsVO.getEmployeeName());
                        response.setMinTransferAmount(resSubsVO.getMinTxnAmtForDisp());
                        response.setMaxTransferAmount(resSubsVO.getMaxTxnAmtForDisp());
                        response.setTotalTxnCount(resSubsVO.getTotalTxnCountForDisp());
                        response.setTotalTxnAmount(resSubsVO.getTotalTransferAmountForDisp());
                        response.setMonthlyTransferLimit(resSubsVO.getMonthlyLimitForDisp());
                        
                        response.setSubscriberStatus(resSubsVO.getStatus());
                        response.setRegisteredOn(resSubsVO.getCreatedOnAsString());
                        
                        
                        response.setIsBlackListed(PretupsI.FALSE);
                        response.setStatus((HttpStatus.SC_OK));
    	    			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
    	    			response.setMessage(resmsg);
    	    			response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);
                        
                    }
                }       
                else {                
                	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
                }
                
                
                
				
			}
		
			finally {
				if (LOG.isDebugEnabled()) {
                    LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
				}
			}
			return response;
		}





		@Override
		public BaseResponse blackListSingleSubscriber(MultiValueMap<String, String> headers,
				HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con,
				MComConnectionI mcomCon, Locale locale, UserVO userVO, BaseResponse response,
				BlackListSingleSubscriberRequestVO requestVO) throws BTSLBaseException, SQLException {
			final String METHOD_NAME = "blackListSingleSubscriber";
			if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
			}
			
			
            
			try {	
				
				//validation starts here
				if (BTSLUtil.isNullString(requestVO.getMsisdn())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.MSISDN_REQ, 0, null);
	            }
				
				if (BTSLUtil.isNullString(requestVO.getOwnerID())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.OWNER_ID_REQ, 0, null);
	            }
				
				if (BTSLUtil.isNullString(requestVO.getCp2pPayer())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.P2P_PAYER_REQ, 0, null);
	            }
				
				if (BTSLUtil.isNullString(requestVO.getCp2pPayee())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.P2P_PAYEE_REQ, 0, null);
	            }
				
				if (BTSLUtil.isNullString(requestVO.getC2sPayee())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.C2S_PAYEE_REQ, 0, null);
	            }
				
				if (BTSLUtil.isNullString(requestVO.getUserName())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USER_NAME_REQ, 0, null);
	            }
				
				
				if (requestVO.getMsisdn().length() > 500) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.MSISDN_STRING_LENGTH_EXCEEDED, 0, null);
	            }
				
				//validation ends here
				
				
				RestrictedSubscriberVO resSubsVO = new RestrictedSubscriberVO();
                Date currentDate = new Date();
                resSubsVO.setModifiedOn(currentDate);
                
                resSubsVO.setModifiedBy(userVO.getUserID());
                //resSubsVO = constructVOFromForm(thisForm, resSubsVO);
                if (!BTSLUtil.isNullString(requestVO.getMsisdn())) {
                	resSubsVO.setMsisdn(PretupsBL.getFilteredIdentificationNumber(requestVO.getMsisdn()));
                }
                resSubsVO.setOwnerID(requestVO.getOwnerID());
    
                //resSubsVO.setLastModifiedTime(thisForm.getRestrictedSubsVO().getLastModifiedTime());

				
                
             // addded for C2S Payee and Cp2P Payee Date 04/02/08
                boolean rset_Status = false;
                if (requestVO.getCp2pPayer().equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
                    resSubsVO.setBlackListStatus(PretupsI.RES_MSISDN_BLACKLIST_STATUS);
                    rset_Status = true;
                } else {
                    resSubsVO.setBlackListStatus(null);
                }

                if (requestVO.getCp2pPayee().equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
                    resSubsVO.setCp2pPayeeStatus(PretupsI.RES_MSISDN_BLACKLIST_STATUS);
                    resSubsVO.setBlackListStatus(PretupsI.RES_MSISDN_BLACKLIST_STATUS);
                    rset_Status = true;
                } else {
                    resSubsVO.setCp2pPayeeStatus(null);
                }
                if (requestVO.getC2sPayee().equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
                    resSubsVO.setC2sPayeeStatus(PretupsI.RES_MSISDN_BLACKLIST_STATUS);
                    resSubsVO.setBlackListStatus(PretupsI.RES_MSISDN_BLACKLIST_STATUS);
                    rset_Status = true;
                } else {
                    resSubsVO.setC2sPayeeStatus(null);
                }
                if (!rset_Status) {
                    resSubsVO.setBlackListStatus(PretupsI.RES_MSISDN_BLACKLIST_STATUS);
                    resSubsVO.setCp2pPayeeStatus(null);
                    resSubsVO.setC2sPayeeStatus(null);
                }
                // end of C2S Payee and Cp2P Payee

                String args[] = { requestVO.getMsisdn(), requestVO.getUserName() };
                
                RestrictedSubscriberWebDAO restrictedSubscriberWebDAO = new RestrictedSubscriberWebDAO();
                if (restrictedSubscriberWebDAO.blackListSingleSubs(con, resSubsVO) > 0) {
                	mcomCon.finalCommit();
                    //BTSLMessages btslMessage = new BTSLMessages("restrictedsubs.blacklistsinglesubs.message.success", args, "successPage");
                    BlackListLog.log("BLACKLIST", "MSISDN Entered By User", requestVO.getMsisdn(), "Msisdn black listed successfully ", "Success", "Logged In UserID : " + userVO.getUserID());
                    
                    
                    response.setStatus((HttpStatus.SC_OK));
	    			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUBSCRIBER_BLACKLIST_SUCCESS, null);
	    			response.setMessage(resmsg);
	    			response.setMessageCode(PretupsErrorCodesI.SUBSCRIBER_BLACKLIST_SUCCESS);
                } else {
                	mcomCon.finalRollback();
                    BlackListLog.log("BLACKLIST", "MSISDN Entered By User", requestVO.getMsisdn(), "Cannot Black List the subscriber ", "Fail", "Logged In UserID : " + userVO.getUserID());

                    throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.BLACKLIST_SINGLE_SUB_ERROR, 0,args , null);
                }
				
			}
			finally {
				if (LOG.isDebugEnabled()) {
                    LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
				}
			}
			return response;
		}





		@Override
		public BaseResponse blackListAllSubscriber(MultiValueMap<String, String> headers,
				HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con,
				MComConnectionI mcomCon, Locale locale, UserVO userVO, BaseResponse response,
				BlackListAllSubscriberRequestVO requestVO) throws SQLException, BTSLBaseException {
			final String METHOD_NAME = "blackListSingleSubscriber";
			if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
			}
			
			            
			try {	
				
				//validation starts here
				
				if (BTSLUtil.isNullString(requestVO.getOwnerID())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.OWNER_ID_REQ, 0, null);
	            }
				
				if (BTSLUtil.isNullString(requestVO.getCp2pPayer())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.P2P_PAYER_REQ, 0, null);
	            }
				
				if (BTSLUtil.isNullString(requestVO.getCp2pPayee())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.P2P_PAYEE_REQ, 0, null);
	            }
				
				if (BTSLUtil.isNullString(requestVO.getC2sPayee())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.C2S_PAYEE_REQ, 0, null);
	            }
				
				if (BTSLUtil.isNullString(requestVO.getUserName())) {
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.USER_NAME_REQ, 0, null);
	            }
				
				//validation ends here
				
				
				RestrictedSubscriberVO resSubsVO = new RestrictedSubscriberVO();
                Date currentDate = new Date();
                resSubsVO.setModifiedOn(currentDate);
                resSubsVO.setModifiedBy(userVO.getUserID());
                //resSubsVO = constructVOFromForm(thisForm, resSubsVO);
                
                resSubsVO.setOwnerID(requestVO.getOwnerID());
                // resSubsVO.setBlackListStatus(PretupsI.RES_MSISDN_BLACKLIST_STATUS);
                // addded for C2S Payee and Cp2P Payee Date 04/02/08
                
                boolean rset_Status = false;
                
                if (requestVO.getCp2pPayer().equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
                    resSubsVO.setBlackListStatus(PretupsI.RES_MSISDN_BLACKLIST_STATUS);
                    rset_Status = true;
                } else {
                    resSubsVO.setBlackListStatus(null);
                }

                if (requestVO.getCp2pPayee().equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
                    resSubsVO.setCp2pPayeeStatus(PretupsI.RES_MSISDN_BLACKLIST_STATUS);
                    resSubsVO.setBlackListStatus(PretupsI.RES_MSISDN_BLACKLIST_STATUS);
                    rset_Status = true;
                } else {
                    resSubsVO.setCp2pPayeeStatus(null);
                }
                if (requestVO.getC2sPayee().equalsIgnoreCase(PretupsI.SELECT_CHECKBOX)) {
                    resSubsVO.setC2sPayeeStatus(PretupsI.RES_MSISDN_BLACKLIST_STATUS);
                    resSubsVO.setBlackListStatus(PretupsI.RES_MSISDN_BLACKLIST_STATUS);
                    rset_Status = true;
                } else {
                    resSubsVO.setC2sPayeeStatus(null);
                }
                if (!rset_Status) {
                    resSubsVO.setBlackListStatus(PretupsI.RES_MSISDN_BLACKLIST_STATUS);
                }
                // end of C2S Payee and Cp2P Payee
                String args[] = { requestVO.getUserName() };
                
                RestrictedSubscriberWebDAO resSubsWebDAO = new RestrictedSubscriberWebDAO();
                if (resSubsWebDAO.isSubscriberExist(con, resSubsVO.getOwnerID())) {
                    if (resSubsWebDAO.changeBlackListStatusForAll(con, resSubsVO) > 0) {
                    	mcomCon.finalCommit();
                        // this code change for logs entry for the successfully
                        // black listed MSISDNs
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("blackListAllSubs", "All the mobile numbers have been black listed successfully");
                        }

                        
                        response.setStatus((HttpStatus.SC_OK));
    	    			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUBSCRIBER_BLACKLIST_SUCCESS, null);
    	    			response.setMessage(resmsg);
    	    			response.setMessageCode(PretupsErrorCodesI.SUBSCRIBER_BLACKLIST_SUCCESS);
                    } else {
                    	mcomCon.finalRollback();

                    	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.BLACKLIST_ALL_SUB_ERROR, 0, args , null);
                    }
                } else {
                	mcomCon.finalRollback();
                	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.BLACKLIST_ALL_SUBS_NO_SUBS_FOUND, 0, args , null);
                }
				
			}
			finally {
				if (LOG.isDebugEnabled()) {
                    LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
				}
			}
			return response;
		}

    @Override
    public BlackListMultipleSubscriberResponseVO uploadAndProcessBlackListMultipleSubscriberFile(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, BlacklistMultipleSubscribersRequestVO requestVO) throws Exception {
        final String methodName = "uploadAndProcessBlackListMultipleSubscriberFile";
        if (LOG.isDebugEnabled()) {
            LOG.debug("uploadAndProcessBlackListMultipleSubscriberFile", "Entered");
        }
        BlackListMultipleSubscriberResponseVO response = new BlackListMultipleSubscriberResponseVO();
        boolean isFileUploaded = false;
        String fileName = requestVO.getUploadFileRequestVO().getFileName();
//		UserValidationResponseVO validateUser= this.loadUserList(con,userVO, requestVO.getUserName(),requestVO.getCategory(),requestVO.getGeographicalDomain());
        ListValueVO listValueVO = null;
        BufferedReader br = null;
        String line = null;
        InputStream is = null;
        InputStreamReader inputStreamReader = null;


        boolean message = BTSLUtil.isValideFileName(fileName);// validating
        // name of the
        // file
        // if not a valid file name then throw exception
        if (!message) {
            throw new BTSLBaseException(classname, methodName,PretupsErrorCodesI.FILE_COULD_NOT_BE_UPLOADED_TRY_A_VALID_FILE , "");
        }
        ReadGenericFileUtil fileUtil = new ReadGenericFileUtil();

        byte[] data  =fileUtil.decodeFile(requestVO.getUploadFileRequestVO().getFileAttachment());
        is = new ByteArrayInputStream(data);
        inputStreamReader = new InputStreamReader(is);
        br = new BufferedReader(inputStreamReader);
        while ((line = br.readLine()) != null) {
            boolean isFileContentValid = BTSLUtil.isFileContentValid(line);
            if (!isFileContentValid) {
                throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.FILE_CONTENT_IS_IN_VALID, "");
            }
        }
        String dir = Constants.getProperty("blackListFilePath");

        String contentType = BTSLUtil.getFileContentType(PretupsI.FILE_CONTENT_TYPE_PLAIN_TEXT);
        String fileSize = Constants.getProperty("OTHER_FILE_SIZE");
        if (BTSLUtil.isNullString(fileSize)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("uploadAndProcessFile", "Other File size is not defined in Constant Property file");
            }
            throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.OTHER_FILE_SIZE_IS_MISSING_IN_CONTENT_PROPERTY_FILE, "");
        }

        // upload file to server
        File file = new File(fileName);
        FileUtils.writeByteArrayToFile(file, data);
        isFileUploaded = uploadFileToServer(file, data, dir, contentType, Long.parseLong(fileSize));
        if (isFileUploaded) {
            // now process uploaded file
            response = this.processUploadBlackListMultipleSubscriber(headers,httpServletRequest,response1,con,mcomCon,locale,userVO,requestVO, response);
        } else {
            throw new BTSLBaseException(this, methodName, RESTRICTEDSUBS_BLACKLISTING_ERROR_FILENOTUPLOADED);
        }
        return response;
    }


    public BlackListMultipleSubscriberResponseVO processUploadBlackListMultipleSubscriber(MultiValueMap<String, String> headers, HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, BlacklistMultipleSubscribersRequestVO requestVO, BlackListMultipleSubscriberResponseVO response) throws SQLException, BTSLBaseException, IOException {
        final String methodName = "processUploadBlackListMultipleSubscriber";
        if (LOG.isDebugEnabled()) {
            LOG.debug("processUploadBlackListMultipleSubscriber", "Entered");
        }
        String filePath = null;
        String contentsSize = null;
        FileReader fileReader = null; // file reader
        String tempStr = null;
        BufferedReader bufferReader = null;


        ArrayList fileContents = null;
        int countMsisdn = 0;
        String msisdn;
        String filteredMsisdn;
        String msisdnPrefix;
        NetworkPrefixVO networkPrefixVO = null;
        String networkCode;
        long noOfRec = 0;

        boolean fileMoved = false;

        ArrayList finalList = new ArrayList();
        File file = null;
        BTSLMessages btslMessage = null;
        long failCount = 0;
        long duplicateRecs = 0;
        int newLines = 0;
        int contentSize = 0;
        RestrictedSubscriberVO errVOForLogs = null;// used to show the logs
        // entries
        String msisdnForLog = null;// used to show the logs entries
        RestrictedSubscriberVO errorVO = new RestrictedSubscriberVO();
        filePath = Constants.getProperty("blackListFilePath");
        if (BTSLUtil.isNullString(filePath)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "File path not defined in Constant Property file");
            }
            throw new BTSLBaseException(this, methodName, RESTRICTEDSUBS_BLACKLISTING_ERROR_FILEPATHMISSINGINCONS);
        }
        contentsSize = Constants.getProperty("MAX_BLACK_LIST_SIZE");
        if (BTSLUtil.isNullString(contentsSize)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("processUploadedBlackListFile", "MAX_BLACK_LIST_SIZE not defined in Constant Property file");
            }
            throw new BTSLBaseException(this, methodName, RESTRICTEDSUBS_BLACKLISTING_ERROR_MAXBLACKLISTMISSINGINCONS);
        }
        try {
            contentSize = Integer.parseInt(contentsSize);
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            LOG.debug("processUploadedBlackListFile", "MAX_BLACK_LIST_SIZE is invalid in Constant Property file");
            throw new BTSLBaseException(this, methodName, RESTRICTEDSUBS_BLACKLISTING_ERROR_BLACKLISTSIZEINVALID);
        }
        String fileName = requestVO.getUploadFileRequestVO().getFileName();
        String filePathAndFileName = filePath + fileName; // path if the file with file name

        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("processUploadedBlackListFile", "Initializing the fileReader, filepath : " + filePathAndFileName);
            }

            fileReader = new FileReader(filePathAndFileName);
            if (fileReader != null) {
                bufferReader = new BufferedReader(fileReader);
            }

            fileContents = new ArrayList();
            boolean isStartFound = false;
            boolean isEndFound = false;
            String limit[] = new String[1];
            if (bufferReader != null && bufferReader.ready()){
                // If File Not Blank Read line by line
                while ((tempStr = bufferReader.readLine()) != null){
                    // read the file until it reaches to end

                    if (tempStr.trim().length() != 0) // remove blank lines
                    {
                        if ("[START]".equalsIgnoreCase(tempStr)) {
                            isStartFound = true;
                            fileContents.add("NEWLINE"); // treat [START} as a
                            // new line
                            newLines++;
                        } else if ("[END]".equalsIgnoreCase(tempStr)) {
                            isEndFound = true; // no processing after end of
                            // file
                            break;
                        } else if (isStartFound) {
                            fileContents.add(tempStr.trim());
                        } else // treat new lines before start if any
                        {
                            fileContents.add("NEWLINE");
                            newLines++;
                        }
                    } else // add the new lines position in arraylist for
                    // logging purpose
                    {
                        fileContents.add("NEWLINE");
                        newLines++;
                    }
                }

                // There must be valid [START] and [END] tag in a file.
                // only entries between [START] and [END] will be validated.
                if (!isStartFound) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("processUploadedBlackListFile", "No [START] tag found");
                    }
                    throw new BTSLBaseException(this, methodName, RESTRICTEDSUBS_BLACKLISTING_ERROR_NOSTART);
                }
                if (!isEndFound) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("processUploadedBlackListFile", "No [END] tag found");
                    }
                    throw new BTSLBaseException(this, methodName, RESTRICTEDSUBS_BLACKLISTING_ERROR_NOEND);
                }
                long records = fileContents.size();
                noOfRec = records - newLines;
                // it can not be allowed to process the file if MSISDN's are
                // more than the defined Limit
                if ((records - newLines) > contentSize) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("processUploadedBlackListFile", "File contents size of the file is not valid in constant properties file : " + fileContents.size());
                    }
                    limit[0] = contentsSize;
                    throw new BTSLBaseException(this, methodName, RESTRICTEDSUBS_BLACKLISTING_ERROR_MAXSIZEREACHED, limit);
                }

                if(noOfRec == 0) {
                    throw new BTSLBaseException(classname, methodName, PretupsErrorCodesI.NUMBER_OF_RECORDS_MUST_BE_GREATER_THAN_ZERO, "");
                }

                // Check for the duplicate mobile numbers in the list

                if (fileContents != null && records > 0) {
                    for (int i = 0; i < records; i++) {
                        msisdn = (String) fileContents.get(i);
                        errorVO = new RestrictedSubscriberVO();
                        if (!"NEWLINE".equals(msisdn) && !isContain(finalList, msisdn)) {
                            errorVO.setLineNumber((i + 1) + "");
                            errorVO.setMsisdn(msisdn);
                            finalList.add(errorVO);
                        } else if (!"NEWLINE".equals(msisdn)) {
                            errorVO.setLineNumber((i + 1) + "");
                            errorVO.setMsisdn(msisdn + "(D)");
                            errorVO.setErrorCode(RestAPIStringParser.getMessage(locale,
                                    PretupsErrorCodesI.RESTRICTEDSUBS_PROCESSUPLOADEDBLACKLISTFILE_ERROR_DULPLICATEMSISDN, new String[] {""}));
                            finalList.add(errorVO);
                            duplicateRecs++;
                        } else if ("NEWLINE".equals(msisdn)) // for tracing the
                        // line number for
                        // log purpose
                        {
                            errorVO.setLineNumber((i + 1) + "");
                            errorVO.setErrorCode("NEWLINE");
                            errorVO.setMsisdn("NEWLINE");
                            finalList.add(errorVO);
                        }
                    }
                }
            } else {
                throw new BTSLBaseException(this, methodName, RESTRICTEDSUBS_BLACKLISTING_ERROR_ERRORINITIAZING);
            }
            // Check for Valid Msisdn's & process the MSISDN's from the Array
            // List
            long finalListSize = finalList.size();
            RestrictedSubscriberVO errVO = null;

            // Take the error corresponding to keys from message resources
            RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.RESTRICTEDSUBS_PROCESSUPLOADEDBLACKLISTFILE_ERROR_MSISDNALREADYBLACKLIST, null);
            String invalidMsisdnErr = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.RESTRICTEDSUBS_PROCESSUPLOADEDBLACKLISTFILE_ERROR_INVALIDMSISDN, null);
            String netPrefixErr = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.NETWORK_PREFIX_NOT_FOUND, null);
            String unSupportNetworkErr = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.UNSUPPORTED_NETWORK, null);
            String msisdnNotExistsErr = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.RESTRICTEDSUBS_PROCESSUPLOADEDBLACKLISTFILE_ERROR_MSISDNNOTEXISTS, null);
            String alredayBlackListErr = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.RESTRICTEDSUBS_PROCESSUPLOADEDBLACKLISTFILE_ERROR_MSISDNALREADYBLACKLIST, null);

            while (finalListSize != countMsisdn) {
                errVO = (RestrictedSubscriberVO) finalList.get(countMsisdn);
                msisdn = errVO.getMsisdn();
                countMsisdn++;
                if (errVO.getErrorCode() == null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("processUploadedBlackListFile", "Processing starts for MSISDN's " + msisdn);
                    }
                    // Change ID=ACCOUNTID
                    // FilteredMSISDN is replaced by
                    // getFilteredIdentificationNumber
                    // This is done because this field can contains msisdn or
                    // account id
                    filteredMsisdn = PretupsBL.getFilteredIdentificationNumber(msisdn); // before
                    // process
                    // MSISDN
                    // filter
                    // each-one
                    // check for valid MSISDN
                    // Change ID=ACCOUNTID
                    // isValidMsisdn is replaced by isValidIdentificationNumber
                    // This is done because this field can contains msisdn or
                    // account id
                    if (!BTSLUtil.isValidIdentificationNumber(filteredMsisdn)) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("processUploadedBlackListFile", "Not a valid MSISDN " + msisdn);
                        }
                        BlackListLog.log("BLACKLIST", requestVO.getUploadFileRequestVO().getFileName(), msisdn, "Not a valid MSISDN", "Fail", "Logged In UserID : " + userVO.getUserID());
                        failCount++;
                        errVO.setLineNumber(countMsisdn + "");
                        errVO.setMsisdn(filteredMsisdn);
                        errVO.setErrorCode(invalidMsisdnErr);
                        finalList.remove(countMsisdn - 1);
                        finalList.add(countMsisdn - 1, errVO);
                        continue;
                    }
                    // check prefix of the MSISDN
                    msisdnPrefix = PretupsBL.getMSISDNPrefix(filteredMsisdn); // get
                    // the
                    // prefix
                    // of
                    // the
                    // MSISDN
                    networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
                    if (networkPrefixVO == null) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("processUploadedBlackListFile", "No Network prefix found " + msisdn);
                        }
                        BlackListLog.log("BLACKLIST", requestVO.getUploadFileRequestVO().getFileName(), msisdn, "No Network prefix found ", "Fail", "Logged In UserID : " + userVO.getUserID());
                        failCount++;
                        errVO.setLineNumber(countMsisdn + "");
                        errVO.setMsisdn(filteredMsisdn);
                        errVO.setErrorCode(netPrefixErr);
                        finalList.remove(countMsisdn - 1);
                        finalList.add(countMsisdn - 1, errVO);
                        continue;
                    }

                    // check network support of the MSISDN
                    networkCode = networkPrefixVO.getNetworkCode();
                    if (!networkCode.equals(userVO.getNetworkID())) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("processUploadedBlackListFile", "Not supporting Network" + msisdn);
                        }
                        BlackListLog.log("BLACKLIST", requestVO.getUploadFileRequestVO().getFileName(), msisdn, "Not supporting Network ", "Fail", "Logged In UserID : " + userVO.getUserID());
                        failCount++;
                        errVO.setLineNumber(countMsisdn + "");
                        errVO.setMsisdn(filteredMsisdn);
                        errVO.setErrorCode(unSupportNetworkErr);
                        finalList.remove(countMsisdn - 1);
                        finalList.add(countMsisdn - 1, errVO);
                        continue;
                    }
                }
            }
            RestrictedSubscriberWebDAO restrictedSubscriberWebDAO = new RestrictedSubscriberWebDAO();

            // addded for C2S Payee and Cp2P Payee Date 04/02/08
            boolean rset_Status = false;
            String cp2p_payer = null;
            String cp2p_payee = null;
            String c2s_payee = null;
            if ((PretupsI.SELECT_CHECKBOX).equalsIgnoreCase(requestVO.getP2pPayer())) {
                cp2p_payer = PretupsI.RES_MSISDN_BLACKLIST_STATUS;
            } else {
                cp2p_payer = null;
            }
            if ((PretupsI.SELECT_CHECKBOX).equalsIgnoreCase(requestVO.getP2pPayee())) {
                cp2p_payee = PretupsI.RES_MSISDN_BLACKLIST_STATUS;
            } else {
                cp2p_payee = null;
            }
            if ((PretupsI.SELECT_CHECKBOX).equalsIgnoreCase(requestVO.getC2sPayee())) {
                c2s_payee = PretupsI.RES_MSISDN_BLACKLIST_STATUS;
            } else {
                c2s_payee = null;
            }
            if (((BTSLUtil.isNullString(cp2p_payer)) && (BTSLUtil.isNullString(cp2p_payee))) && (BTSLUtil.isNullString(c2s_payee))) {
                cp2p_payer = PretupsI.RES_MSISDN_BLACKLIST_STATUS;
                // end of C2S Payee and Cp2P Payee
            }

            ArrayList arrList = restrictedSubscriberWebDAO.blackListSubscriberBulk(con, finalList, requestVO.getUserID(), new Date(), userVO.getUserID(), requestVO.getUploadFileRequestVO().getFileName(), requestVO.getUserName(), msisdnNotExistsErr, alredayBlackListErr, cp2p_payer, cp2p_payee, c2s_payee);
            RestrictedSubscriberVO resSubsVO = (RestrictedSubscriberVO) arrList.get(arrList.size() - 1);
            long failCnt = resSubsVO.getFailCount();
            BlacklistSubscriberErrorList blacklistErrorlist=null;
            ArrayList errorList = new ArrayList();
            for(int i = 0; i<arrList.size(); i++){
                RestrictedSubscriberVO restSubs = (RestrictedSubscriberVO) arrList.get(i);
                if(restSubs.getErrorCode() != null) {
                    if(!restSubs.getErrorCode().equals("NEWLINE") && restSubs.getErrorCode() != null){
                        blacklistErrorlist = new BlacklistSubscriberErrorList();
                        blacklistErrorlist.setErrorCode(restSubs.getErrorCode());
                        blacklistErrorlist.setLineNumber(restSubs.getLineNumber());
                        blacklistErrorlist.setFailCount(restSubs.getFailCount());
                        blacklistErrorlist.setMsisdn(restSubs.getMsisdn());
                        errorList.add(blacklistErrorlist);
                    }
                }
            }
            bufferReader.close();
            fileReader.close();
            // make Archive file on the server.
            fileMoved = this.moveBlacklistFileToArchive(filePathAndFileName, fileName);
            if (!fileMoved) {
                con.rollback();
                throw new BTSLBaseException(this, methodName, RESTRICTEDSUBS_BLACKLISTING_ERROR_FILENOMOVE);
            }

            if ((failCnt + failCount + duplicateRecs) == 0) {
                // this code change to show all the successfully black listed
                // MSISDNs in the logs
                if (LOG.isDebugEnabled()) {
                    LOG.debug("processUploadedBlackListFile", "Mobile numbers specified in the file have been black listed successfully, and the no.s are as follows:--> ");
                }
                String msg = RestAPIStringParser.getMessage(locale,
                        PretupsErrorCodesI.RESTRICTEDSUBS_BLACKLISTING_MESSAGE_SUCCESS, new String[] { "" });
                response.setMessage(msg);
                response.setStatus(HttpStatus.SC_OK);
                response.setMessageCode(PretupsErrorCodesI.RESTRICTEDSUBS_BLACKLISTING_MESSAGE_SUCCESS);
                response.setErrorFlag(PretupsI.FALSE);
                response.setNumberOfRecords(String.valueOf(noOfRec));
                response.setTotalFailCount(String.valueOf(failCnt + failCount));

                final AdminOperationVO adminOperationVO = new AdminOperationVO();
                adminOperationVO.setSource(PretupsI.LOGGER_BLACKLIST_SUBSCRIBER);
                adminOperationVO.setDate(new Date());
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
                adminOperationVO
                        .setInfo(msg);
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
                AdminOperationLog.log(adminOperationVO);
            }else if ((failCnt + failCount + duplicateRecs) > 0){
                String msg = RestAPIStringParser.getMessage(locale,
                        PretupsErrorCodesI.FILE_UPLOAD_AND_PROCCESS_PARTIALLY_SUCCESSFULL, new String[] { "" });
                response.setErrorList(errorList);
                response.setTotalFailCount(String.valueOf(failCnt + failCount));
                response.setProcessedRecords(String.valueOf(noOfRec  - duplicateRecs - (failCnt + failCount)));
                response.setMessage(msg);
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                response.setMessageCode(PretupsErrorCodesI.FILE_UPLOAD_AND_PROCCESS_PARTIALLY_SUCCESSFULL);
                response.setErrorFlag(PretupsI.TRUE);

                final AdminOperationVO adminOperationVO = new AdminOperationVO();
                adminOperationVO.setSource(PretupsI.LOGGER_BLACKLIST_SUBSCRIBER);
                adminOperationVO.setDate(new Date());
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
                adminOperationVO
                        .setInfo(msg);
                adminOperationVO.setLoginID(userVO.getLoginID());
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
                AdminOperationLog.log(adminOperationVO);
                downloadErrorLogFileForBlacklistMultipleSubscriber(userVO, response);
            }

            if(Integer.parseInt(response.getTotalFailCount()) == noOfRec) {
                response.setNumberOfRecords(String.valueOf(noOfRec));
                response.setErrorList(errorList);
                response.setTotalFailCount(String.valueOf(failCnt + failCount));
                response.setProcessedRecords(String.valueOf(noOfRec  - duplicateRecs - (failCnt + failCount)));
                response.setStatus((HttpStatus.SC_BAD_REQUEST));
                response1.setStatus(HttpStatus.SC_BAD_REQUEST);
                String resmsg = RestAPIStringParser.getMessage(
                        new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
                        PretupsErrorCodesI.BLACKLIST_SUBSCRIBER_FAILED, null);
                response.setMessage(resmsg);
                response.setMessageCode(PretupsErrorCodesI.BLACKLIST_SUBSCRIBER_FAILED);
                response.setErrorFlag(PretupsI.TRUE);
                final AdminOperationVO adminOperationVO = new AdminOperationVO();
                adminOperationVO.setSource(PretupsI.LOGGER_BLACKLIST_SUBSCRIBER);
                adminOperationVO.setDate(new Date());
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
                adminOperationVO
                        .setInfo(resmsg);
                adminOperationVO.setLoginID(userVO.getLoginID());
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
                AdminOperationLog.log(adminOperationVO);
                downloadErrorLogFileForBlacklistMultipleSubscriber(userVO, response);
            }

        } finally {
            if(bufferReader!=null)
                bufferReader.close();

            if(fileReader!= null)
                fileReader.close();
            if (LOG.isDebugEnabled()) {
                LOG.debug(methodName, "Exiting:=" + methodName);
            }
        }
        return response;
    }

    private boolean moveBlacklistFileToArchive(String p_filePathAndFileName, String p_fileName) throws BTSLBaseException, IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("moveFileToArchive", " Entered p_filePathAndFileName=" + p_filePathAndFileName + " p_fileName=" + p_fileName);
        }
        File fileRead = new File(p_filePathAndFileName);
        String archivalPath = Constants.getProperty("ArchiveblackListFilePath");
        if (BTSLUtil.isNullString(archivalPath)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("processUploadedBlackListFile", "Archival File path not defined in Constant Property file");
            }
            throw new BTSLBaseException(this, "moveFileToArchive", "restrictedsubs.blacklisting.error.archivalpathnotfound", "multipleSubsSel");
        }
        File fileArchive = new File(String.valueOf(Constants.getProperty("ArchiveblackListFilePath")));
        if (!fileArchive.isDirectory()) {
            fileArchive.mkdirs();
        }
        fileArchive = new File(String.valueOf( Constants.getProperty("ArchiveblackListFilePath")) + p_fileName + "." + BTSLUtil.getTimestampFromUtilDate(new Date()).getTime()); // to
        
        // make
        // the
        // new
        // file
        // name
        File archfile = new File(archivalPath+p_fileName);
        Path sourceDir = Paths.get(p_filePathAndFileName);
        Path distDir = Paths.get(archivalPath+p_fileName);
        Files.copy(sourceDir, distDir, StandardCopyOption.REPLACE_EXISTING);
        boolean flag = archfile.renameTo(fileArchive);
        if (LOG.isDebugEnabled()) {
            LOG.debug("moveFileToArchive", " Exiting File Moved=" + flag);
        }
        return flag;
    }// end of moveFileToArchive

    public void downloadErrorLogFileForBlacklistMultipleSubscriber(UserVO userVO, BlackListMultipleSubscriberResponseVO response) {
        final String METHOD_NAME = "downloadErrorLogFileForBlacklistMultipleSubscriber";
        if (LOG.isDebugEnabled())
            LOG.debug(METHOD_NAME, "Entered");
        //ActionForward forward = null;
        try {
            ArrayList errorList = response.getErrorList();
            String filePath = Constants.getProperty("blackListFilePath");
            try {
                File fileDir = new File(filePath);
                if (!fileDir.isDirectory())
                    fileDir.mkdirs();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
                LOG.error(METHOD_NAME, "Exception" + e.getMessage());
                throw new BTSLBaseException(classname, METHOD_NAME, "directory not created", 0, null);
            }
            String fileName = "downloadErrorLogFile"

                    + BTSLUtil.getFileNameStringFromDate(new Date()) + ".csv";

            this.writeBlacklistListErrorLogFile(errorList, fileName, filePath, userVO.getNetworkID(),
                    fileName, true);

            File error = new File(filePath+fileName);
            byte[] fileContent = FileUtils.readFileToByteArray(error);
            String encodedString = Base64.getEncoder().encodeToString(fileContent);
            response.setFileAttachment(encodedString);
            response.setFileName(fileName);
            response.setFileType("csv");
        } catch (Exception e) {
            LOG.error(METHOD_NAME, "Exception:e=" + e);
            LOG.errorTrace(METHOD_NAME, e);
        } /*finally {
            if (LOG.isDebugEnabled())
                LOG.debug(METHOD_NAME, "Exiting:forward=" + forward);
        }*/
    }

    public void writeBlacklistListErrorLogFile(ArrayList errorList,String _fileName,String filePath,String _networkCode, String uploadedFileNamePath,Boolean headval)

    {
        final String methodName = "writeBlacklistListErrorLogFile";
        String[] splitFileName = uploadedFileNamePath.split("/");
        String uploadedFileName = splitFileName[(splitFileName.length)-1];
        if (LOG.isDebugEnabled()){
            LOG.debug(methodName,"Entered: "+methodName);
        }
        Writer out =null;
        File newFile = null;
        File newFile1 = null;
        String fileHeader=null;
        String fileName=null;
        try
        {
            Date date= new Date();
            newFile1=new File(filePath);
            if(! newFile1.isDirectory())
                newFile1.mkdirs();
            fileName=filePath+_fileName;
            LOG.debug(methodName,"fileName := "+fileName);
            if(headval){
                fileHeader=Constants.getProperty("ERROR_FILE_HEADER_MOVEUSER");
            }
            else{
                fileHeader=Constants.getProperty("ERROR_FILE_HEADER_PAYOUT");
            }
            newFile = new File(fileName);
            out = new OutputStreamWriter(new FileOutputStream(newFile));
            out.write(fileHeader +"\n");
            List<BlacklistSubscriberErrorList> filterList = (List<BlacklistSubscriberErrorList>) errorList.stream().filter(o->((((BlacklistSubscriberErrorList)o).getErrorCode()!=null ) && !((BlacklistSubscriberErrorList)o).getErrorCode().equals("NEWLINE")) ).collect(Collectors.toList());
            filterList.sort ((o1,o2)->Integer.parseInt(((BlacklistSubscriberErrorList) o1).getLineNumber())-(Integer.parseInt(((BlacklistSubscriberErrorList) o2).getLineNumber())));

            for (Iterator<BlacklistSubscriberErrorList> iterator = filterList.iterator(); iterator.hasNext();) {

                BlacklistSubscriberErrorList listValueVO =iterator.next();
                out.write(listValueVO.getLineNumber().concat(","));
                out.write(listValueVO.getMsisdn().concat(","));

                out.write(listValueVO.getErrorCode()+",");

                out.write(",");
                out.write("\n");
            }
            out.write("End");

        }
        catch(Exception e)
        {
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"writeDataInFile[writeDataInFile]","","","","Exception:= "+e.getMessage());
        }
        finally
        {
            if (LOG.isDebugEnabled()){
                LOG.debug(methodName,"Exiting... ");
            }
            if (out!=null)
                try{
                    out.close();
                }
                catch(Exception e){
                    LOG.errorTrace(methodName, e);
                }

        }
    }
		
		
		//**********BlackList api's code ends********//
		
	
	
	

	
	
	
	
	
}

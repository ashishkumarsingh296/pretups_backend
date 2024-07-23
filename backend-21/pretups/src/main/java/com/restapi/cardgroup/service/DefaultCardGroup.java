package com.restapi.cardgroup.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import javax.ws.rs.core.MediaType;


import com.annotation.RestEasyAnnotation;
import org.apache.commons.validator.ValidatorException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.login.LoginDAO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDAO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetDAO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVersionDAO;
import com.btsl.pretups.channel.profile.businesslogic.DefaultCardGroupVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.SwaggerAPIDescriptionI;
import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RestEasyAnnotation
@RequestMapping(value = "/cardGroup")
@Tag(name = "${DefaultCardGroup.name}", description = "${DefaultCardGroup.desc}")//@Api(value="Set default voucher card group")
public class DefaultCardGroup {
	
	public static final Log log = LogFactory.getLog(DefaultCardGroup.class.getName());
	
	@PostMapping(value = "/setDefaultCardGroupSet", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
	//@ApiOperation(value = "Set default voucher card group", response = PretupsResponse.class)
	public PretupsResponse<JsonNode> defaultCardGroup(
			@Parameter(description = SwaggerAPIDescriptionI.DEFAULT_CARD_GROUP, required = true)
			@RequestBody DefaultCardGroupVO requestVO) throws ValidatorException, SAXException {
		    String  methodName="defaultCardGroup";
		    if (log.isDebugEnabled()) {
				log.debug(methodName, "Entered ");
			}
			PretupsResponse<JsonNode> response = new PretupsResponse<>();
		    Connection con = null;
	        MComConnectionI mcomCon = null;
	        final CardGroupDAO cardGroupDAO = new CardGroupDAO();
	        final CardGroupSetVersionDAO cardGroupSetVersionDAO = new CardGroupSetVersionDAO();
            final CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
            try 
            {
            	mcomCon = new MComConnection();
	            con=mcomCon.getConnection();
            	String newCardGroupSetId=null;
            	String serviceTypeCode=null;
            	String subServiceTypeCode=null;
            	String moduleCode = null;
            	boolean isCardGroupFound = false ;

            	/*DefaultCardGroupVO defaultCardGroupVO = (DefaultCardGroupVO) PretupsRestUtil
    					.convertJSONToObject(dataObject.get("data").toString(), new TypeReference<DefaultCardGroupVO>() {
    					});
            	
            	validateRequestData(PretupsRestI.DEFAULT_CARDGROUP, response, defaultCardGroupVO,
    					"DefaultCardGroupVO");

    			if (response.hasFieldError()) {
    				response.setStatus(false);
    				response.setStatusCode(PretupsI.RESPONSE_FAIL);
    				return response;
    			}*/
    			
    			String categories = ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.CARD_GROUP_ALLOWED_CATEGORIES));
    			String[] allowedCategories = categories.split(",");
    			PretupsRestUtil.validateLoggedInUser(requestVO.getIdentifierType(), requestVO.getIdentifierValue(), con,
    					response, allowedCategories);
    			if (response.hasFormError()) {
    				response.setStatus(false);
    				response.setMessageCode("user.unauthorized");
    				response.setStatusCode(PretupsI.RESPONSE_FAIL);
    				return response;
    			}
    		    
                LoginDAO _loginDAO = new LoginDAO();
                ChannelUserVO channelUserVO = _loginDAO.loadUserDetails(con, requestVO.getIdentifierType().trim(), requestVO.getIdentifierValue(), BTSLUtil.getSystemLocale());
    			
                
              
	            newCardGroupSetId =requestVO.getCardGroupSetId();
			    serviceTypeCode =requestVO.getServiceTypeId();
				subServiceTypeCode=requestVO.getSubServiceTypeId();
				moduleCode = requestVO.getModuleCode();
				if(BTSLUtil.isNullString(subServiceTypeCode) ||BTSLUtil.isNullString(newCardGroupSetId) ||BTSLUtil.isNullString(serviceTypeCode) ) {
					response.setStatus(false);
					response.setStatusCode(PretupsI.RESPONSE_FAIL);
					response.setMessageCode("cardgroup.cardgroupp2pdetails.error.manadatoryfieldblank");
					//response.setResponse(PretupsI.RESPONSE_FAIL,false,"cardgroup.cardgroupp2pdetails.error.isdefault");
					return response;
				} else {
					if(!subServiceTypeCode.contains(":")) {
						subServiceTypeCode = serviceTypeCode+":"+subServiceTypeCode;
					}
				}
				String defaultCardGroupSetId=null;
				if(BTSLUtil.isNullString(requestVO.getNetworkCode()) || !channelUserVO.getNetworkID().equalsIgnoreCase(requestVO.getNetworkCode())  )
				{
					response.setStatus(false);
					response.setStatusCode(PretupsI.RESPONSE_FAIL);
					response.setMessageCode("cardgroup.cardgroupp2pdetails.error.networkcodeinvalidblank");
					//response.setResponse(PretupsI.RESPONSE_FAIL,false,"cardgroup.cardgroupp2pdetails.error.isdefault");
					return response;
				} else {
					requestVO.setNetworkCode(channelUserVO.getNetworkID());
				}
				if(!PretupsI.P2P_MODULE.equals(moduleCode) && !PretupsI.C2S_MODULE.equals(moduleCode) ) {
					response.setStatus(false);
					response.setStatusCode(PretupsI.RESPONSE_FAIL);
					response.setMessageCode("cardgroup.cardgroupp2pdetails.error.modulecodeinvaild");
					//response.setResponse(PretupsI.RESPONSE_FAIL,false,"cardgroup.cardgroupp2pdetails.error.isdefault");
					return response;
				}
				
				  ArrayList serviceTypeList = cardGroupDAO.loadServiceTypeList(con, requestVO.getNetworkCode(), moduleCode); 
	               filterServiceTypeList(serviceTypeList, PretupsI.CARD_GROUP_VMS);
	                if (serviceTypeList.isEmpty()) {
	                    throw new BTSLBaseException(this, "loadDefaultCardGroupSetNames", "cardgroup.cardgroupdetails.error.noservicesexists");
	                }
	                if (serviceTypeList.size() == 1) {
	                	if(!(((ListValueVO) serviceTypeList.get(0)).getValue()).equals(serviceTypeCode)) {
	                		response.setStatus(false);
	    					response.setStatusCode(PretupsI.RESPONSE_FAIL);
	    					response.setMessageCode("cardgroup.cardgroupdetails.error.noservicesexists");
	    					//response.setResponse(PretupsI.RESPONSE_FAIL,false,"cardgroup.cardgroupp2pdetails.error.isdefault");
	    					return response;
	                	} 
	                } else {
	                	if (BTSLUtil.getOptionDesc(serviceTypeCode, serviceTypeList).getValue() == null) {
	                		response.setStatus(false);
	    					response.setStatusCode(PretupsI.RESPONSE_FAIL);
	    					response.setMessageCode("cardgroup.cardgroupdetails.error.noservicesexists");
	    					//response.setResponse(PretupsI.RESPONSE_FAIL,false,"cardgroup.cardgroupp2pdetails.error.isdefault");
	    					return response;
	                	}
	                }
	                ArrayList subServiceTypeList  = ServiceSelectorMappingCache.loadSelectorDropDownForCardGroup();
	                if (BTSLUtil.getOptionDesc(subServiceTypeCode, subServiceTypeList).getValue() == null) {
                		response.setStatus(false);
    					response.setStatusCode(PretupsI.RESPONSE_FAIL);
    					response.setMessageCode("cardgroup.cardgroupdetails.error.subserviceinvalid");
    					//response.setResponse(PretupsI.RESPONSE_FAIL,false,"cardgroup.cardgroupp2pdetails.error.isdefault");
    					return response;
                	}
	                subServiceTypeCode = BTSLUtil.getOptionDesc(subServiceTypeCode, subServiceTypeList).getValue().split(":")[1];
			     	ArrayList<String> defaultCardGroup=cardGroupDAO.loadDefaultCardGroup(con,BTSLUtil.getOptionDesc(serviceTypeCode, serviceTypeList).getValue(),subServiceTypeCode,PretupsI.YES,requestVO.getNetworkCode());
				
				if(defaultCardGroup.size()!=0)
				 defaultCardGroupSetId = defaultCardGroup.get(0);
				if ((newCardGroupSetId.equals(defaultCardGroupSetId))) {
					response.setStatus(false);
					response.setStatusCode(PretupsI.RESPONSE_FAIL);
					response.setMessageCode("cardgroup.cardgroupp2pdetails.error.isdefault");
					//response.setResponse(PretupsI.RESPONSE_FAIL,false,"cardgroup.cardgroupp2pdetails.error.isdefault");
					return response;
				}
				ArrayList<CardGroupSetVO> cardGroupList=cardGroupDAO.loadCardGroupSet(con, requestVO.getNetworkCode(), requestVO.getModuleCode());
				CardGroupSetVO newCardGroupSetVO =null;
                if (cardGroupList != null && !cardGroupList.isEmpty()) {
                    for (int i = 0, l = cardGroupList.size(); i < l; i++) {
                    	newCardGroupSetVO = (CardGroupSetVO) cardGroupList.get(i);
                        if ((newCardGroupSetVO.getCardGroupSetID()).equals(newCardGroupSetId)) {
                        	isCardGroupFound = true ;
                            break;
                        }
                    }
                }
                if(!isCardGroupFound) {
                	response.setStatus(false);
					response.setStatusCode(PretupsI.RESPONSE_FAIL);
					response.setMessageCode("cardgroup.cardgroupp2pdetails.error.invaildcardgroupcombination");
					//response.setResponse(PretupsI.RESPONSE_FAIL,false,"cardgroup.cardgroupp2pdetails.error.isdefault.suspended");
	    			return response;
                }
				if (newCardGroupSetVO != null && !((PretupsI.YES).equals(newCardGroupSetVO.getStatus()))) {
					response.setStatus(false);
					response.setStatusCode(PretupsI.RESPONSE_FAIL);
					response.setMessageCode("cardgroup.cardgroupp2pdetails.error.isdefault.suspended");
					//response.setResponse(PretupsI.RESPONSE_FAIL,false,"cardgroup.cardgroupp2pdetails.error.isdefault.suspended");
	    			return response;
	             } 
				if(!newCardGroupSetVO.getServiceType().equals(serviceTypeCode) || !newCardGroupSetVO.getSubServiceType().equals(subServiceTypeCode) ||!newCardGroupSetVO.getModuleCode().equals(moduleCode) ) {
					response.setStatus(false);
					response.setStatusCode(PretupsI.RESPONSE_FAIL);
					response.setMessageCode("cardgroup.cardgroupp2pdetails.error.invaildcardgroupcombination");
					//response.setResponse(PretupsI.RESPONSE_FAIL,false,"cardgroup.cardgroupp2pdetails.error.isdefault.suspended");
	    			return response;
				}
				final Date currentTime = new Date();
				boolean isUpdated = false;
            	if (!cardGroupSetVersionDAO.isApplicableNow(con, currentTime, newCardGroupSetId)) {
            		response.setStatus(false);
					response.setStatusCode(PretupsI.RESPONSE_FAIL);
					response.setMessageCode("cardgroup.cardgroupp2pdetails.error.nocurrentversion");
					//response.setResponse(PretupsI.RESPONSE_FAIL,false,"cardgroup.cardgroupp2pdetails.error.nocurrentversion");
            		return response;
            	}
             try 
             {
            	 isUpdated = cardGroupSetDAO.updateAsDefault(con, defaultCardGroupSetId, newCardGroupSetId, requestVO.getUserId(), currentTime);
             } catch (Exception e) {
                 mcomCon.finalRollback();
             	response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setMessageCode("cardgroup.cardgroupp2pdetails.error.unabletomakedefault");
				 //response.setResponse(PretupsI.RESPONSE_FAIL,false,"cardgroup.cardgroupp2pdetails.error.unabletomakedefault");
				 return response;
             }
             if(isUpdated)
             {
            	 mcomCon.finalCommit();  
            	 response.setResponse(PretupsI.RESPONSE_SUCCESS,true,"cardgroup.cardgroupp2pdetails.successdefaultmessage");
				 return response;
             }
             else
             {
            	mcomCon.finalRollback();
            	response.setStatus(false);
				response.setStatusCode(PretupsI.RESPONSE_FAIL);
				response.setMessageCode("cardgroup.cardgroupp2pdetails.error.unabletomakedefault");
				//response.setResponse(PretupsI.RESPONSE_FAIL,false,"cardgroup.cardgroupp2pdetails.error.unabletomakedefault");
				return response;	 
             }
			
		
		} 
           catch (SQLException e1) {
			log.error(methodName, "Exceptin:e=" + e1);
            log.errorTrace(methodName, e1);
		} catch (BTSLBaseException e1) {
			log.error(methodName, "Exceptin:e=" + e1);
            log.errorTrace(methodName, e1);
		}
		finally {
            try {
            	if (mcomCon != null) {
    				mcomCon.close("defaultCardGroup#" + "DefaultCardGroup");
    				mcomCon = null;
    			}
            } catch (Exception e) {
            	log.error(methodName, "Exceptin:e=" + e);
                log.errorTrace(methodName, e);
            }
            if (log.isDebugEnabled()) {
                log.debug(methodName, " Exited ");
            }
        }
		return response;
	}
	  private ArrayList filterServiceTypeList(ArrayList list, String cardGroupType) {
	    	final String methodName = "filterServiceTypeList";
	    	if(list != null & list.size() > 0) {
	    		Iterator itr = list.iterator(); 
	            while (itr.hasNext()) 
	            { 
	            	ListValueVO vo = (ListValueVO) itr.next();
	            	String codeName = vo.getValue();
	            	if(!BTSLUtil.isNullString(cardGroupType) && !BTSLUtil.isNullString(codeName)) {
	            		if(PretupsI.CARD_GROUP_VMS.equals(cardGroupType) && !BTSLUtil.isVoucherService(codeName) || 
	            				!PretupsI.CARD_GROUP_VMS.equals(cardGroupType) && BTSLUtil.isVoucherService(codeName)) {
	            			itr.remove(); 
	            		}
	            	}
	            } 
	    	}
	        if (log.isDebugEnabled()) {
	        	log.debug(methodName, "Exited : list.size()="+list.size());
	        }
	        return list;
	    }
	
/*	public void validateRequestData(String type, PretupsResponse<?> response, DefaultCardGroupVO defaultCardGroupVO, String referenceName) throws ValidatorException, IOException, SAXException {
		
		WebServiceKeywordCacheVO webServiceKeywordCacheVO = ServiceKeywordCache
				.getWebServiceTypeObject(type);
		String validationXMLpath = webServiceKeywordCacheVO.getValidatorName();
		CommonValidator commonValidator = new CommonValidator(validationXMLpath, defaultCardGroupVO, referenceName);
		Map<String, String> errorMessages = commonValidator.validateModel();
		response.setFieldError(errorMessages);

	}*/
}

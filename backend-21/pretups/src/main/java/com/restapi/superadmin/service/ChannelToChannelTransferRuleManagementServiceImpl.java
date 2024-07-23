package com.restapi.superadmin.service;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferRuleDAO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.LookupsVO;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.util.BTSLUtil;
import com.restapi.superadmin.DomainListResponseVO;
import com.restapi.superadmin.repository.ChannelTransferRuleWebDAO;
import com.restapi.superadmin.requestVO.ChannelTransferRuleRequestVO;
import com.restapi.superadmin.responseVO.ChannelTransferRuleViewResponseVO;
import com.restapi.superadmin.serviceI.ChannelToChannelTransferRuleManagementServiceI;
import com.restapi.superadmin.util.ChannelTransferRuleCommonConstants;
import com.restapi.superadminVO.ChannelTransferRuleVO;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
@Service("ChannelToChannelTransferRuleManagementServiceI")
public class ChannelToChannelTransferRuleManagementServiceImpl implements ChannelToChannelTransferRuleManagementServiceI{
	public static final Long LAST_MODIFY = 0L;
	public static final Log LOG = LogFactory.getLog(ChannelToChannelTransferRuleManagementServiceImpl.class.getName());
	public static final String classname = "ChannelToChannelTransferRuleManagementServiceImpl";
	
	
	
	@Override
	public DomainListResponseVO viewDomainList(Connection con) throws BTSLBaseException {
		
		final String METHOD_NAME = "viewDomainList";
		
		
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		DomainListResponseVO response = new DomainListResponseVO();
		
		DomainDAO domainDAO = new DomainDAO();
		ArrayList<ListValueVO> domainList = new ArrayList<ListValueVO>();
		
		try {

			
			domainList = domainDAO.loadCategoryDomainList(con);
			response.setDomainTypeList(domainList);
			
			
	   }

		

		catch (BTSLBaseException be) {
			LOG.error(METHOD_NAME, "Exception:e=" + be);
			LOG.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {

				throw be;
			}

		}

		catch (Exception e) {
			LOG.error(METHOD_NAME, "Exception:e=" + e);
			LOG.errorTrace(METHOD_NAME, e);
			throw e;
		}

		return response;

	}



	@Override
	public ArrayList loadChannelTransferRuleVOList(Connection con, String networkCode,
			String domainCode, String toDomainCode, String type) throws BTSLBaseException {
		final String METHOD_NAME = "loadChannelTransferRuleVOList";
		if (LOG.isDebugEnabled()) {
			LOG.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		ChannelTransferRuleDAO channelTransferRuleDAO = null;
        ChannelTransferRuleWebDAO channelTransferRuleWebDAO = null;
        // CategoryDAO categoryDAO=null;
       
        channelTransferRuleDAO = new ChannelTransferRuleDAO();
        channelTransferRuleWebDAO = new ChannelTransferRuleWebDAO();
        ChannelTransferRuleVO channelTransferRuleVO = null;
        ArrayList productVOList = null;
        ArrayList transferRulesList = null;
        // loading the all transfer rules for the corresponds to the
        // selected category domain code
        try {
        	transferRulesList = channelTransferRuleWebDAO.loadChannelTransferRuleVOList(con, networkCode,
            domainCode, toDomainCode, type);
        	
        	
        	
        	
        	if (transferRulesList != null && !transferRulesList.isEmpty()) {
             for (int i = 0, j = transferRulesList.size(); i < j; i++) {
                 channelTransferRuleVO = (ChannelTransferRuleVO) transferRulesList.get(i);

                 // loading the list of products associated with the transfer
                 // rule.
                productVOList = channelTransferRuleDAO.loadProductVOList(con, channelTransferRuleVO.getTransferRuleID());
                 channelTransferRuleVO.setProductVOList(productVOList);
             } 
        	}
  
        }
        catch (BTSLBaseException e) {
            LOG.error("loadChannelTransferRuleList", "BTSLBaseException:e=" + e);
            LOG.errorTrace(METHOD_NAME, e);
           throw e;
		}
        

		return transferRulesList;
	}



	@Override
	public Integer updateChannelTransferRule(Connection con, ChannelTransferRuleVO channelTransferRuleVO) throws BTSLBaseException, Exception{
		final String METHOD_NAME = "updateChannelTransferRule";
        
        final ChannelTransferRuleWebDAO channelTransferRuleWebDAO = new ChannelTransferRuleWebDAO();
       Integer updateCount=0;
	try {
		updateCount = channelTransferRuleWebDAO.updateChannelTransferRule(con, channelTransferRuleVO);
	} catch (BTSLBaseException e) {
		LOG.error(METHOD_NAME, "Exception:e=" + e);
        LOG.errorTrace(METHOD_NAME, e);
		throw e;
	}
	catch (Exception e) {
		LOG.error(METHOD_NAME, "Exception:e=" + e);
        LOG.errorTrace(METHOD_NAME, e);
		throw e;
	}

		return updateCount ;
	}



	

	@Override
	public Integer deleteChannelTransferRule(Connection con, ChannelTransferRuleVO channelTransferRuleVO) throws BTSLBaseException, Exception {
		final String METHOD_NAME = "deleteChannelTransferRule";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        int deleteCount = 0;
        boolean isUserExist = false;
        String networkID = null;
        ChannelTransferRuleWebDAO channelTransferRuleWebDAO = new ChannelTransferRuleWebDAO();
        ArrayList transferRulesList = null;
        ChannelTransferRuleVO responsechannelTransferRuleVO = null;
        try {
        if (PretupsI.OPERATOR_TYPE_OPT.equalsIgnoreCase(channelTransferRuleVO.getType())) {
           isUserExist = channelTransferRuleWebDAO.checkUserUnderToCategory(con, channelTransferRuleVO);
        } else {
           isUserExist = channelTransferRuleWebDAO.checkUserUnderFromCategory(con, channelTransferRuleVO);
        }
        if (isUserExist) {
            throw new BTSLBaseException(this, "deleteChannelTransferRule", PretupsErrorCodesI.EXIST_UNDER_TO_CATEGORY_TRANSFER_RULE_CANNOT_BE_DELETE, "displaytrfrule");
        }
        else{
        	
        	transferRulesList = channelTransferRuleWebDAO.loadChannelTransferRuleVOList(con, channelTransferRuleVO.getNetworkCode(),
        			channelTransferRuleVO.getDomainCode(), channelTransferRuleVO.getToDomainCode(), channelTransferRuleVO.getType());
                
                	if (transferRulesList != null && !transferRulesList.isEmpty()) {
                     for (int i = 0, j = transferRulesList.size(); i < j; i++) {
                    	 responsechannelTransferRuleVO = (ChannelTransferRuleVO) transferRulesList.get(i);
                         if(channelTransferRuleVO.getDomainCode().equals(responsechannelTransferRuleVO.getDomainCode()) 
                        		 && channelTransferRuleVO.getToDomainCode().equals(responsechannelTransferRuleVO.getToDomainCode() )
                        		 && channelTransferRuleVO.getFromCategory().equals(responsechannelTransferRuleVO.getFromCategory())
                        		 && channelTransferRuleVO.getToCategory().equals(responsechannelTransferRuleVO.getToCategory())) {
                        	 networkID = responsechannelTransferRuleVO.getTransferRuleID();
                         } 
                     }
                	}
                	if(networkID != null) {
                		channelTransferRuleVO.setTransferRuleID(networkID);
                		deleteCount = channelTransferRuleWebDAO.deleteChannelTransferRule(con, channelTransferRuleVO);
                	}
        }
        }
        catch (BTSLBaseException e) {
            LOG.error(METHOD_NAME, "BTSLBaseException:e=" + e);
            LOG.errorTrace(METHOD_NAME, e);
            throw e;
        } 
        
        catch (Exception e) {
            LOG.error(METHOD_NAME, "BTSLBaseException:e=" + e);
            LOG.errorTrace(METHOD_NAME, e);
            throw e;
        }
        return deleteCount;
        }

	@Override
	public int addChannelTransferRule(Connection con, ChannelTransferRuleVO channelTransferRuleVO) throws BTSLBaseException, Exception {
		final String METHOD_NAME = "addChannelTransferRule";
        if (LOG.isDebugEnabled()) {
            LOG.debug("addChannelTransferRule", "Entered");
        }
        int addCount =0;
        try {
        	ChannelTransferRuleWebDAO channelTransferRuleWebDAO = new ChannelTransferRuleWebDAO();
        	addCount = channelTransferRuleWebDAO.addChannelTransferRule(con, channelTransferRuleVO);
        }
        
		catch (BTSLBaseException be) {
   			LOG.error(METHOD_NAME, "Exception:e=" + be);
					throw be;
		}

		return addCount;
	}



	@Override
	public ArrayList loadProductList(Connection con, String networkCode, String c2sModule) throws BTSLBaseException, Exception {
		
		final String METHOD_NAME = "loadProductList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
		ChannelTransferRuleWebDAO  channelTransferRuleWebDAO= new ChannelTransferRuleWebDAO();

        // loading the list of all products associated to the loging user
        // network.
		 ArrayList productList = null;
		try {
			productList = channelTransferRuleWebDAO.loadProductList(con, networkCode, PretupsI.C2S_MODULE);
		}
		catch(BTSLBaseException e) {
			throw e;
		}
		catch(Exception e) {
			throw e;
		}
		return productList;
	}



	@Override
	public ArrayList loadCategoryList(Connection con, String domainCode) throws BTSLBaseException, Exception {
		 final CategoryWebDAO categoryWebDAO = new CategoryWebDAO();
	     ArrayList categoryList = null; 
		 try {
	    	 categoryList =  categoryWebDAO.loadCategoryList(con, domainCode);
	       }
	       catch(BTSLBaseException e) {
	    	   throw e;
	       }
	       catch(Exception e) {
	    	   throw e;
	       }
		return categoryList;
	}



	
	@Override
	 public  ListValueVO getListValueVOFromLookupsVO(String p_lookupType, String p_lookupCode) throws BTSLBaseException// This
	    
	{
	        if (LOG.isDebugEnabled()) {
	            LOG.debug("getListValueVOFromLookupsVO", "Entered p_lookupType=" + p_lookupType + ", p_lookupCode=" + p_lookupCode);
	        }
	        final LookupsVO lookupsVO = (LookupsVO) LookupsCache.getObject(p_lookupType, p_lookupCode);
	        final ListValueVO listValueVO = new ListValueVO(lookupsVO.getLookupName(), lookupsVO.getLookupCode());
	        if (LOG.isDebugEnabled()) {
	            LOG.debug("getListValueVOFromLookupsVO", "Exiting listValueVO =" + listValueVO);
	        }
	        return listValueVO;
	    }


	
    public static ListValueVO getOptionDesc(String p_code, List p_list) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("getOptionDesc", "Entered: p_code=" + p_code + " p_list=" + p_list);
        }
        ListValueVO vo = null;
        boolean flag = false;
        if (p_list != null && !p_list.isEmpty()) {
            for (int i = 0, j = p_list.size(); i < j; i++) {
                vo = (ListValueVO) p_list.get(i);
                if (vo.getValue().equalsIgnoreCase(p_code)) {
                    flag = true;
                    break;
                }
            }
        }
        if (!flag) {
            vo = new ListValueVO();
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("getOptionDesc", "Exited: vo=" + vo);
        }
        return vo;
    }



	@Override
	public ChannelTransferRuleVO requestVOToChangeDAOVO(ChannelTransferRuleRequestVO requestVO) {
		ChannelTransferRuleVO channelTransferRuleVO = new ChannelTransferRuleVO();
		channelTransferRuleVO.setApprovalRequired(ChannelTransferRuleCommonConstants.NO);
		channelTransferRuleVO.setCntrlReturnLevel(requestVO.getCntrlReturnLevel());
		channelTransferRuleVO.setCntrlTransferLevel(requestVO.getCntrlTransferLevel());
		channelTransferRuleVO.setCntrlWithdrawLevel(requestVO.getCntrlWithdrawLevel());
		channelTransferRuleVO.setDirectTransferAllowed(requestVO.getDirectTransferAllowed());
		channelTransferRuleVO.setDomainCode(requestVO.getDomainCode());
		channelTransferRuleVO.setToDomainCode(requestVO.getToDomainCode());
		channelTransferRuleVO.setDirectTransferAllowed(requestVO.getDirectTransferAllowed());
		channelTransferRuleVO.setDpAllowed(ChannelTransferRuleCommonConstants.NO);
		channelTransferRuleVO.setFirstApprovalLimit(Long.parseLong(PretupsI.C2C_TRANSFER_RULE_FIRST_APPROVAL_LIMIT));
		channelTransferRuleVO.setSecondApprovalLimit(Long.parseLong(PretupsI.C2C_TRANSFER_RULE_SECOND_APPROVAL_LIMIT));
		channelTransferRuleVO.setFixedReturnCategory(requestVO.getFixedReturnCategory());
		channelTransferRuleVO.setFixedReturnLevel(requestVO.getFixedReturnLevel());
		channelTransferRuleVO.setFixedTransferCategory(requestVO.getFixedTransferCategory());
		channelTransferRuleVO.setFixedTransferLevel(requestVO.getFixedTransferLevel());
		channelTransferRuleVO.setFixedWithdrawCategory(requestVO.getFixedWithdrawCategory());
		channelTransferRuleVO.setFixedWithdrawLevel(requestVO.getFixedWithdrawLevel());
		channelTransferRuleVO.setFocAllowed(ChannelTransferRuleCommonConstants.NO);
		channelTransferRuleVO.setFromCategory(requestVO.getFromCategory());
		channelTransferRuleVO.setToCategory(requestVO.getToCategory());
		channelTransferRuleVO.setFromSeqNo(0);
		channelTransferRuleVO.setParentAssocationAllowed(requestVO.getParentAssocationAllowed());
		channelTransferRuleVO.setProductArray(requestVO.getProductArray());
		channelTransferRuleVO.setRestrictedMsisdnAccess(requestVO.getRestrictedMsisdnAccess());
		channelTransferRuleVO.setRestrictedRechargeAccess(requestVO.getRestrictedRechargeAccess());
		channelTransferRuleVO.setReturnAllowed(requestVO.getReturnAllowed());
		channelTransferRuleVO.setReturnChnlBypassAllowed(requestVO.getReturnChnlBypassAllowed());
		channelTransferRuleVO.setCntrlReturnLevel(requestVO.getCntrlReturnLevel());
		channelTransferRuleVO.setTransferAllowed(ChannelTransferRuleCommonConstants.YES);
		channelTransferRuleVO.setTransferChnlBypassAllowed(requestVO.getTransferChnlBypassAllowed());
		channelTransferRuleVO.setFocAllowed(ChannelTransferRuleCommonConstants.NO);
		channelTransferRuleVO.setFocTransferType(ChannelTransferRuleCommonConstants.FOCTRANSFERTYPE);
		channelTransferRuleVO.setTransferType(requestVO.getTransferType());
		
		channelTransferRuleVO.setType(ChannelTransferRuleCommonConstants.TYPE);
		channelTransferRuleVO.setUncntrlReturnAllowed(requestVO.getUncntrlReturnAllowed());
		channelTransferRuleVO.setUncntrlReturnLevel(requestVO.getUncntrlReturnLevel());
		channelTransferRuleVO.setUncntrlTransferAllowed(requestVO.getUncntrlTransferAllowed());
		channelTransferRuleVO.setUncntrlTransferLevel(requestVO.getUncntrlTransferLevel());
		channelTransferRuleVO.setUncntrlWithdrawAllowed(requestVO.getUncntrlWithdrawAllowed());
		channelTransferRuleVO.setWithdrawChnlBypassAllowed(requestVO.getWithdrawChnlBypassAllowed());
		channelTransferRuleVO.setWithdrawAllowed(requestVO.getWithdrawAllowed());
		channelTransferRuleVO.setUncntrlWithdrawLevel(requestVO.getUncntrlWithdrawLevel());
		channelTransferRuleVO.setDpAllowed(ChannelTransferRuleCommonConstants.NO);
		channelTransferRuleVO.setFirstApprovalLimit(Long.parseLong(PretupsI.C2C_TRANSFER_RULE_FIRST_APPROVAL_LIMIT));
		channelTransferRuleVO.setSecondApprovalLimit(Long.parseLong(PretupsI.C2C_TRANSFER_RULE_FIRST_APPROVAL_LIMIT));
		channelTransferRuleVO.setLastModifiedTime(LAST_MODIFY);
		
		channelTransferRuleVO.setRestrictedRechargeAccess(requestVO.getRestrictedRechargeAccess());
		ArrayList productList = new ArrayList();
		productList.addAll(Arrays.asList(requestVO.getProductArray()));
		channelTransferRuleVO.setProductVOList(productList);
		
		
		return channelTransferRuleVO;
	}
	@Override
	public ChannelTransferRuleViewResponseVO responseVOToChangerequestVO(ChannelTransferRuleViewResponseVO response, ChannelTransferRuleVO channelTransferRuleVO) {
		
		
		
		response.setCntrlReturnLevel(channelTransferRuleVO.getCntrlReturnLevel());
		response.setCntrlTransferLevel(channelTransferRuleVO.getCntrlTransferLevel());
		response.setCntrlWithdrawLevel(channelTransferRuleVO.getCntrlWithdrawLevel());
		response.setDirectTransferAllowed(channelTransferRuleVO.getDirectTransferAllowed());
		response.setDomainCode(channelTransferRuleVO.getDomainCode());
		response.setToDomainCode(channelTransferRuleVO.getToDomainCode());
		response.setDirectTransferAllowed(channelTransferRuleVO.getDirectTransferAllowed());
		
		response.setFixedReturnCategory(channelTransferRuleVO.getFixedReturnCategory());
		response.setFixedReturnLevel(channelTransferRuleVO.getFixedReturnLevel());
		response.setFixedTransferCategory(channelTransferRuleVO.getFixedTransferCategory());
		response.setFixedTransferLevel(channelTransferRuleVO.getFixedTransferLevel());
		response.setFixedWithdrawCategory(channelTransferRuleVO.getFixedWithdrawCategory());
		response.setFixedWithdrawLevel(channelTransferRuleVO.getFixedWithdrawLevel());
		
		response.setFromCategory(channelTransferRuleVO.getFromCategory());
		response.setToCategory(channelTransferRuleVO.getToCategory());
		
		response.setParentAssocationAllowed(channelTransferRuleVO.getParentAssocationAllowed());
		response.setRestrictedMsisdnAccess(channelTransferRuleVO.getRestrictedMsisdnAccess());
		response.setRestrictedRechargeAccess(channelTransferRuleVO.getRestrictedRechargeAccess());
		response.setReturnAllowed(channelTransferRuleVO.getReturnAllowed());
		response.setReturnChnlBypassAllowed(channelTransferRuleVO.getReturnChnlBypassAllowed());
		response.setCntrlReturnLevel(channelTransferRuleVO.getCntrlReturnLevel());
		
		response.setTransferChnlBypassAllowed(channelTransferRuleVO.getTransferChnlBypassAllowed());
		
		response.setTransferType(channelTransferRuleVO.getTransferType());
		
		
		response.setUncntrlReturnAllowed(channelTransferRuleVO.getUncntrlReturnAllowed());
		response.setUncntrlReturnLevel(channelTransferRuleVO.getUncntrlReturnLevel());
		response.setUncntrlTransferAllowed(channelTransferRuleVO.getUncntrlTransferAllowed());
		response.setUncntrlTransferLevel(channelTransferRuleVO.getUncntrlTransferLevel());
		response.setUncntrlWithdrawAllowed(channelTransferRuleVO.getUncntrlWithdrawAllowed());
		response.setUncntrlWithdrawLevel(channelTransferRuleVO.getUncntrlWithdrawLevel());
		
		response.setWithdrawAllowed(channelTransferRuleVO.getWithdrawAllowed());
		response.setWithdrawChnlBypassAllowed(channelTransferRuleVO.getWithdrawChnlBypassAllowed());
		
		
		response.setRestrictedRechargeAccess(channelTransferRuleVO.getRestrictedRechargeAccess());
		String []str = new String[2];
		for(int i=0; i<channelTransferRuleVO.getProductVOList().size(); i++) {
			str[i]= channelTransferRuleVO.getProductVOList().get(i).toString();
		}
		response.setProductArray(str);
				
		
		return response;
		
	}
 
}

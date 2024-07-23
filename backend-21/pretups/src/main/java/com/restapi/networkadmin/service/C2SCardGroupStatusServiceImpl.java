package com.restapi.networkadmin.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.httpclient.HttpStatus;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupCache;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDAO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetDAO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.requestVO.SaveC2ScardGroupStatusListRequestVO;
import com.restapi.networkadmin.requestVO.SaveCardGroupStatusRequestVO;
import com.restapi.networkadmin.requestVO.CardGroupStatusRequestVO;
import com.restapi.networkadmin.requestVO.LoadCardGroupStatusListRequestVO;
import com.restapi.networkadmin.responseVO.C2SCardGroupStatusResponseVO;
import com.restapi.networkadmin.responseVO.C2SCardGroupStatusSaveResponseVO;
import com.restapi.networkadmin.serviceI.C2SCardGroupStatusServiceI;

@Service("C2SCardGroupStatusServiceI")
public class C2SCardGroupStatusServiceImpl implements C2SCardGroupStatusServiceI {


	public static final Log LOG = LogFactory.getLog(C2SCardGroupStatusServiceImpl.class.getName());
	public static final String classname = "C2SCardGroupStatusServiceImpl";
	@Override
	public C2SCardGroupStatusResponseVO loadC2SCardGroupStatusList(Connection con, UserVO userVO, LoadCardGroupStatusListRequestVO requestVO) throws Exception {
		   final String methodName = "loadC2SCardGroupStatusList";
	        if (LOG.isDebugEnabled()) {
	            LOG.debug(methodName, "Entered");
	        }
	        C2SCardGroupStatusResponseVO response = new C2SCardGroupStatusResponseVO();

			  Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
			
	            final CardGroupDAO cardGroupDAO = new CardGroupDAO();
	            List<CardGroupSetVO> filterdResponseDate= new ArrayList<>();
	            // load the card group set names
	            List<CardGroupSetVO> cardGroupSetVOList =cardGroupDAO.loadCardGroupSet(con, userVO.getNetworkID(), PretupsI.C2S_MODULE);
	            for(CardGroupStatusRequestVO requestdata:requestVO.getRequestVOList()) {
	            	for(CardGroupSetVO responseDate: cardGroupSetVOList) {
	            		if(requestdata.getCardGroupSetName().equals(responseDate.getCardGroupSetName())&& requestdata.getServiceType().equals(responseDate.getServiceType()) && requestdata.getSubServiceType().equals(responseDate.getSubServiceType())&& requestdata.getVersion().equals(responseDate.getLastVersion()) )
           				{
	            			filterdResponseDate.add(responseDate);
            			}
       				}
	            	
	            }
	          
	            response.setCardGroupStatusList(filterdResponseDate);
	            String msg = RestAPIStringParser.getMessage(locale,
						PretupsErrorCodesI.SUCCESSFULLY_LOAD_CARD_GROUP_LIST_FOR_CARD_GROUP_STATUS, null);
	            response.setMessage(msg);
	            response.setStatus((HttpStatus.SC_OK));
	            response.setStatus(HttpStatus.SC_OK);
	            response.setMessageCode(PretupsErrorCodesI.SUCCESSFULLY_LOAD_CARD_GROUP_LIST_FOR_CARD_GROUP_STATUS);
		return response;
	}
	@Override
	public C2SCardGroupStatusSaveResponseVO saveC2SCardGroupStatusList(Connection con,MComConnectionI mcomCon, UserVO userVO,
			SaveC2ScardGroupStatusListRequestVO requestVO) throws BTSLBaseException, SQLException {
		final String methodName = "saveC2SCardGroupStatusList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        C2SCardGroupStatusSaveResponseVO response = new C2SCardGroupStatusSaveResponseVO();
        final CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
			
			  Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
				List<CardGroupSetVO> updatedSetVOList = new ArrayList<>();
			  for(SaveCardGroupStatusRequestVO vO :requestVO.getCardGroupStatusList()) {
				  CardGroupSetVO setVO = new CardGroupSetVO();
				  setVO.setCardGroupSetID(vO.getCardGroupSetID());
				  setVO.setServiceType(vO.getServiceType());
				  setVO.setSubServiceType(vO.getSubServiceType());
				  setVO.setSetType(vO.getSetType());
				  setVO.setVersion(vO.getVersion());
				  setVO.setStatus(vO.getStatus());
				  setVO.setLastModifiedOn(vO.getLastModifiedOn());
				  setVO.setLanguage1Message(vO.getLanguage1Message());
				  setVO.setLanguage2Message(vO.getLanguage2Message());
				  setVO.setSetTypeName(vO.getSetTypeName());
				  updatedSetVOList.add(setVO);
			  }
			  
           
            final Date currentDate = new Date();
            CardGroupSetVO cardGroupSetVO = null;
            

            // set the default values
            for (int i = 0, j = updatedSetVOList.size(); i < j; i++) {
                cardGroupSetVO = (CardGroupSetVO) updatedSetVOList.get(i);
                cardGroupSetVO.setModifiedOn(currentDate);
                cardGroupSetVO.setModifiedBy(userVO.getUserID());
            }
            // Delete Commission Profile Set
            final int updateCount = cardGroupSetDAO.suspendCardGroupSetList(con, updatedSetVOList);
            if (updateCount <= 0) {
                try {
                	mcomCon.finalRollback();
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                }
                LOG.error(methodName, PretupsErrorCodesI.SUSPEND_CARD_GROUP);
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
            }
            // if above method execute successfully commit the transaction
            mcomCon.finalCommit();
            // Added to update the cache after changes in card group table
            CardGroupCache.loadCardGroupMapAtStartup();
        
            String msg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.SUCCESSFULLY_SAVE_CARD_GROUP_LIST_FOR_CARD_GROUP_STATUS, null);
            response.setMessage(msg);
            response.setStatus((HttpStatus.SC_OK));
            response.setStatus(HttpStatus.SC_OK);
            response.setMessageCode(PretupsErrorCodesI.SUCCESSFULLY_SAVE_CARD_GROUP_LIST_FOR_CARD_GROUP_STATUS);
            final AdminOperationVO adminOperationVO = new AdminOperationVO();
            adminOperationVO.setSource(PretupsI.LOGGER_OPERATION_CHANGE_STATUS);
            adminOperationVO.setDate(currentDate);
            adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
            adminOperationVO
                .setInfo(msg);
            adminOperationVO.setLoginID(userVO.getLoginID());
            adminOperationVO.setUserID(userVO.getUserID());
            adminOperationVO.setCategoryCode(userVO.getCategoryCode());
            adminOperationVO.setNetworkCode(userVO.getNetworkID());
            adminOperationVO.setMsisdn(userVO.getMsisdn());
            AdminOperationLog.log(adminOperationVO);
            
        
      	return response;
	}
}

package com.btsl.pretups.user.requesthandler;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.LoanProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.LoanProfileDetailsVO;
//import com.btsl.pretups.channel.profile.web.LoanProfileAction;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserDAO;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.user.businesslogic.UserLoanDAO;
import com.btsl.user.businesslogic.UserLoanVO;
import com.btsl.util.BTSLUtil;


public class LastLoanEnqRequestHandler implements ServiceKeywordControllerI{

	private static Log _log = LogFactory.getLog(LastLoanEnqRequestHandler.class.getName());
	
	public static OperatorUtilI calculatorI = null;

	// calculate the tax
	static {
		final String taxClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
		try {
			calculatorI = (OperatorUtilI) Class.forName(taxClass).newInstance();
		} catch (Exception e) {
			_log.errorTrace(LastLoanEnqRequestHandler.class, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LastLoanEnqRequestHandler[initialize]", "", "", "",
					"Exception while loading the class at the call:" + e.getMessage());
		}
	}
	
	public void process(RequestVO p_requestVO) {
		final String methodName = "process";
		if(_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered: ");
		}
		
		Connection con = null;MComConnectionI mcomCon = null;
		
		try {
			mcomCon = new MComConnection();
			con = mcomCon.getConnection();
			ChannelUserVO channelUserVO = new ChannelUserVO();
			ChannelUserDAO channelUserDAO = new ChannelUserDAO();
			String userId = p_requestVO.getActiverUserId();
			
			
			//need to check pin in case of USSD
			if(p_requestVO.getRequestGatewayType().equalsIgnoreCase(PretupsI.REQUEST_SOURCE_TYPE_USSD)) {
				System.out.println("Checking pin");
				
				String msisdn = (String) p_requestVO.getRequestMap().get("MSISDN");
		     	String pin = (String) p_requestVO.getRequestMap().get("PIN");
		     	
		         if (!BTSLUtil.isNullString(msisdn) && !BTSLUtil.isNullString(pin)) {
		             channelUserVO = channelUserDAO.loadChannelUserDetails(con, msisdn);
		         }
		         
		         if (channelUserVO != null) {
		          	if ((!BTSLUtil.isNullString(pin)) && !pin.equals(BTSLUtil.decrypt3DesAesText(channelUserVO.getUserPhoneVO().getSmsPin()))) {
		                  throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.ERROR_INVALID_PIN);
		              }
		         }
		     	
			}
			
			UserLoanDAO userLoanDAO = new UserLoanDAO();
			//UserLoanVO userLoanVO = (UserLoanVO)userLoanDAO.loadUserLoanInfoByUserId(con, userId);
			ArrayList<UserLoanVO> userLoanList = userLoanDAO.loadUserLoanInfoByUserId(con, userId);
			ArrayList list = new ArrayList();

			if(userLoanList == null || userLoanList.size() == 0) {
				p_requestVO.setMessageCode(PretupsErrorCodesI.NO_LOAN_INFO);
			}
			else {
				p_requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				
				ArrayList<LoanProfileDetailsVO>  loanProfileList = null;
				long premium=0;
				
				for(UserLoanVO vo: userLoanList) {
		        	//calculate premium till date
					LoanProfileDAO  loanProfileDAO= new LoanProfileDAO();
				
		        	// set settlement status
	                if(vo.getLoan_given().equals(PretupsI.YES)) {
	                	if(vo.getProfile_id()==0)
						{
							premium=0;
						}
						else
						{

							loanProfileList = loanProfileDAO.loadLoanProfileSlabs(con, String.valueOf(vo.getProfile_id()));
							premium = calculatorI.calculatePremium(vo, loanProfileList);
			            
						}
			        	//end
			        	
			        	vo.setCalculatedPremium(premium);
			        	
			        	
			        	vo.setTotalAmountDue(vo.getLoan_given_amount()+premium);
			        	
	                	
	                	vo.setSettlementStatus(PretupsI.NO);
	                	
	                }
	                else {
	                	vo.setSettlementStatus(PretupsI.YES);
	                	
	                }
		        	
	                
		        	// set loan eligibility
		        	if(vo.getLoan_amount()>0 && vo.getOptinout_allowed().equals("Y")) {
		        		vo.setLoanEligibility(PretupsI.YES);
		        	}
		        	else {
		        		vo.setLoanEligibility(PretupsI.NO);
		        	}
	            	
	            	list.add(vo);
				}
				
				
			}
			
			
        	HashMap map = p_requestVO.getRequestMap();
            if (map == null) {
                map = new HashMap();
            }
            map.put("userLoanList", list);
            


            p_requestVO.setSuccessTxn(true);
			
			
		}
		catch (BTSLBaseException be) {
            p_requestVO.setSuccessTxn(false);
            _log.error(methodName, "BTSLBaseException " + be.getMessage());
            _log.errorTrace(methodName, be);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LastLoanEnqRequestHandler[process]", "", "", "",
                            "BTSL Exception:" + be.getMessage());
            if (be.isKey()) {
                p_requestVO.setMessageCode(be.getMessageKey());
                p_requestVO.setMessageArguments(be.getArgs());
            } else {
                p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
                return;
            }
        } catch (Exception e) {
            p_requestVO.setSuccessTxn(false);
            _log.error(methodName, "BTSLBaseException " + e.getMessage());
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "LastLoanEnqRequestHandler[process]", "", "", "",
                            "Exception:" + e.getMessage());
            p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
        	if(mcomCon != null)
        	{
        		mcomCon.close("LastLoanEnqRequestHandler#process");
        		mcomCon=null;
        		}
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, " Exited ");
            }
        }// end finally
		
	}

}

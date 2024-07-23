package com.btsl.pretups.requesthandler.clientrequesthandler;
//added by shashank for channel user authentication

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import com.btsl.common.BTSLBaseException;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserBL;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtilI;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.OracleUtil;
import com.btsl.util.Constants;

/**
* com.btsl.pretups.requesthandler.UserAuthorizationHandler.java
*  
*  This handler works to handle XML request of user Authorization. when XML request for Authentication 
*  of channel user on the basis of MSISDN ,LOGIN ID,PIN and PASSWORD.
*  1.process() 
*  this method is called the 2. validateUserAuthorization() method  
*  
*  which decide the calls and authentication of user for various combination 
*  MSISDN-PIN,LOGIN-PASSWORD,LOGIN-PASSWORD-PIN-MSISDN,OTHERS Combinations
*  
*
*  3.validateUserMsisdn() 
*  4.validateUserMsisdnPin()
*  5.validateUserMsisdnPswdPin(()
*  6.validateUserForOtherCase()
*  7.validateUserLoginPassword()
* 
*/
public class UserAuthorizationHandler implements ServiceKeywordControllerI {
   
   private Log _log = LogFactory.getLog(this.getClass().getName());
   private HashMap<String ,String> _requestMap = null;
   private final String LOGINID_STR = "USERLOGINID";
   private final String PIN_STR = "PIN";
   private final String PASSWORD_STR ="USERPASSWORD";
   private final String EXTCODE_STR ="EXTCODE";
   private final String EXTNWCODE_STR="EXTNWCODE";
   public static OperatorUtilI _operatorUtil=null;
      
   static
   {
		String utilClass = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPERATOR_UTIL_CLASS);
	    try
		{
			_operatorUtil = (OperatorUtilI) Class.forName(utilClass).newInstance();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"PushMessage[initialize]","","","","Exception while loading the class at the call:"+e.getMessage());
		}
   } 
   /**
    * This method is the entry point in the class and is declared in the Interface ServiceKeywordControllerI
    * This method processes the request for the MSISDN OR USERLOGINID 
    * calls the validate() for validating MSISDN, USERLOGINID
    * calls the loadChannelUserDetails() that sets the Channel User details in the channelUserVO
    * and sets the Channel User details in the p_requestVO
    * @param p_requestVO RequestVO
    */
   public void process (RequestVO p_requestVO) 
   {
       if(_log.isDebugEnabled())_log.debug("process","Entered.....p_requestVO="+p_requestVO);
      // Connection con =null;
      
       _requestMap = p_requestVO.getRequestMap();
 	
       try 
       {
       //    con = OracleUtil.getConnection();
	
           this.validateUserAuthorization(p_requestVO);
          
       } 
       catch (BTSLBaseException be) 
       {
    	 
       //    try{if (con != null){con.rollback();}} catch (Exception e){}
           _log.error("process", p_requestVO.getRequestIDStr(),"BTSLBaseException " + be.getMessage());
           if(be.isKey())
           {
               p_requestVO.setMessageCode(be.getMessageKey());
               String[] args=be.getArgs();
               p_requestVO.setMessageArguments(args);
               p_requestVO.setTxnAuthStatus(be.getMessageKey());
           }
           else
           {
               p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
               p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.REQ_NOT_PROCESS);
           }
           
       }
       catch (Exception e)
       {
    	  
      //     try{if (con != null){con.rollback();}} catch (Exception ee){}
           _log.error("process", p_requestVO.getRequestIDStr(),"Exception " + e.getMessage());
           e.printStackTrace();
           EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"UserAuthorizationHandler[process]","","","","Exception:"+e.getMessage());
           p_requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
           p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.REQ_NOT_PROCESS);
       }
       finally
       {
 	
       //    try	{if (con != null)con.close();} catch (Exception e){}
       
           p_requestVO.setRequestMap(_requestMap);
           if (_log.isDebugEnabled())_log.debug("process",p_requestVO.getRequestIDStr(),"Exited.....p_requestVO="+p_requestVO);
       }			
   }

   /**
 * @param p_con
 * @param p_requestVO
 * @throws BTSLBaseException
 * @author vikas.kumar
 * validateUserAuthorization() is used for authorization and implement 
 * the encryption and decryption logic as per requirement 
 */
   private void validateUserAuthorization(RequestVO p_requestVO)throws BTSLBaseException
   {
	   if(_log.isDebugEnabled())_log.debug("validateUserAuthorization","Entered.....");
       String msisdn=null;
       String loginId=null;
       String pin =null;
       String pinUser=null;
       String password=null;
       boolean isValidMSISDN =false;
       String extCode = null;
       String networkCode = null;
       Connection con =null;
       try
       {
    	  // msisdn = (String) _requestMap.get(MSISDN_STR);
 		   con = OracleUtil.getConnection();
	       msisdn = p_requestVO.getFilteredMSISDN();
	       ChannelUserVO channeluserVO=(ChannelUserVO)p_requestVO.getSenderVO();
	       pinUser=channeluserVO.getUserPhoneVO().getSmsPin();
           loginId = (String) _requestMap.get(LOGINID_STR);
           extCode = (String)_requestMap.get(EXTCODE_STR);
           networkCode = (String)_requestMap.get(EXTNWCODE_STR);
           if(!BTSLUtil.isNullString((String)_requestMap.get(PASSWORD_STR)))
        	   password= BTSLUtil.encryptText((String)_requestMap.get(PASSWORD_STR));
           if(!BTSLUtil.isNullString((String)_requestMap.get(PIN_STR)))
        	   pin=BTSLUtil.encryptText((String)_requestMap.get(PIN_STR));
           
           if(BTSLUtil.isNullString(msisdn)&&BTSLUtil.isNullString(loginId)&&BTSLUtil.isNullString(password)&&BTSLUtil.isNullString(pin) && BTSLUtil.isNullString(extCode))
           {
        	   p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
         	   p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
           }
           else if(BTSLUtil.isNullString(msisdn)&&BTSLUtil.isNullString(loginId) && BTSLUtil.isNullString(extCode))
           {
        	   p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
            	   p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
           }
           else if(BTSLUtil.isNullString(pinUser)&&BTSLUtil.isNullString(password) && BTSLUtil.isNullString(extCode))
           {
        	   p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
             	   p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
           }
           else
           {	   
        	   if(!BTSLUtil.isNullString(msisdn)){
					if(BTSLUtil.NullToString(Constants.getProperty("USERAUTH_INVALIDPIN_BAR")).equals("Y")){
       				 	  PretupsBL.checkMSISDNBarred(con,msisdn,networkCode,p_requestVO.getModule(),PretupsI.USER_TYPE_SENDER);
}
             	  isValidMSISDN=  this.validateUserMsisdn(msisdn,p_requestVO);
        	   }
               if(isValidMSISDN)
               {
		   if((!BTSLUtil.isNullString(msisdn))&&(!BTSLUtil.isNullString(pin))&&(!BTSLUtil.isNullString(loginId))&&(!BTSLUtil.isNullString(password)))
             	   {
             		
             		   this.validateUserMsisdnPswdPin(loginId,password,msisdn,pin,p_requestVO);
             	   }
             	  else if((!BTSLUtil.isNullString(msisdn)&& (!BTSLUtil.isNullString(pin))))
             	  {
               		  this.validateUserMsisdnPin(msisdn,pin,p_requestVO);
             	  }
             	 else if((!BTSLUtil.isNullString(loginId)&& (!BTSLUtil.isNullString(password))))
            	  {
              		  this.validateUserForOtherCase(loginId,password,msisdn,pin,p_requestVO);
            	  }
             	 else if (!BTSLUtil.isNullString(extCode))
             	 {
             		 this.validateUserExtCode(extCode, p_requestVO);
            	  }
            	  else
             	  {
            		  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
            		  p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALIDREQUESTFORMAT);
			
             	  }
               }
               else if((!BTSLUtil.isNullString(loginId))&&(!BTSLUtil.isNullString(password)))
              {
             	 this.validateUserLoginPassword(loginId,password,p_requestVO);
              }
           } 
       }
       catch (BTSLBaseException e) 
       {
 		  _log.error("validateUserAuthorization", "BTSLBaseException " + e.getMessage()); 
           throw e;
       }
       catch(Exception e)
       {
     	  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.REQ_NOT_PROCESS);
     	  _log.error("validateUserAuthorization", "Exception " + e.getMessage()); 
           e.printStackTrace();
           EventHandler.handle( EventIDI.SYSTEM_ERROR,  EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserAuthorizationHandler[validateUserAuthorization]", "", "", "", "Exception:" + e.getMessage());
           throw new BTSLBaseException(this, "validateUserAuthorization", PretupsErrorCodesI.REQ_NOT_PROCESS);
       }	
       finally 
		{
			p_requestVO.setSenderMessageRequired(false);		
			try{if (con != null)con.close();}catch (Exception e){}
	        if (_log.isDebugEnabled())
	               _log.debug("validateUserAuthorization","Exiting  ********");
		}
	   
   }
   
  /**
 * @param p_con
 * @param p_loginId
 * @param p_password
 * @param p_requestVO
 * @throws BTSLBaseException
 * @author vikas.kumar
 * Method validateUserLoginPassword() is used to validate loginid and password
 * 
 */
private void validateUserLoginPassword(String p_loginId,String p_password,RequestVO p_requestVO)throws BTSLBaseException
  {
	  if(_log.isDebugEnabled())_log.debug("validateUserLoginPassword","Entered.....p_loginId::"+p_loginId +"p_requestVO ::"+p_requestVO.toString());
	  
	//  ChannelUserDAO channelUserDAO = new ChannelUserDAO();
	  ChannelUserVO channelUserVO =null;
	  try
	  {
		//  channelUserVO= channelUserDAO.loadAuthenticateUserDetails(p_loginId, null);
		  channelUserVO=(ChannelUserVO)p_requestVO.getSenderVO();
		  if (channelUserVO==null)
		  {
			  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.ERROR_INVALID_LOGIN);
			  p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_LOGIN);
			  throw new BTSLBaseException(this,"validateUserLoginPassword",PretupsErrorCodesI.ERROR_INVALID_LOGIN);
		  }
		  else
		  {	
			  if(BTSLUtil.isNullString(channelUserVO.getPassword()))
			  {
				  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.XML_PASSWORD_NOT_FOUND);
				  p_requestVO.setMessageCode(PretupsErrorCodesI.XML_PASSWORD_NOT_FOUND);
				  throw new BTSLBaseException(this,"validateUserLoginPassword",PretupsErrorCodesI.XML_PASSWORD_NOT_FOUND);
			  }
			  else
			  {		  
				  if(channelUserVO.getPassword().equals(p_password))
				  {
					  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
					  p_requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				  }
				  else
				  {
					  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.XML_ERROR_INVALID_PSWD);
					  p_requestVO.setMessageCode(PretupsErrorCodesI.XML_ERROR_INVALID_PSWD);
					  throw new BTSLBaseException(this,"validateUserLoginPassword",PretupsErrorCodesI.XML_ERROR_INVALID_PSWD);
				  }
			  }
		  }
	  }
	  catch (BTSLBaseException e) 
      {
		  _log.error("validateUserLoginPassword", "BTSLBaseException " + e.getMessage()); 
          throw e;
      }
      catch(Exception e)
      {
    	  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.REQ_NOT_PROCESS);
    	  _log.error("validateUserLoginPassword", "Exception " + e.getMessage()); 
          e.printStackTrace();
          EventHandler.handle( EventIDI.SYSTEM_ERROR,  EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserAuthorizationHandler[validateUserLoginPassword]", "", "", "", "Exception:" + e.getMessage());
          throw new BTSLBaseException(this, "validateUserLoginPassword", PretupsErrorCodesI.REQ_NOT_PROCESS);
      }	
      if(_log.isDebugEnabled())_log.debug("validateUserLoginPassword","Exiting  ********");
  }
  /**
 * @param p_con
 * @param p_msisdn
 * @param p_pin
 * @param p_requestVO
 * @throws BTSLBaseException
 * @author vikas.kumar
 * validateUserMsisdnPin() method is used for validation pin and msisdn 
 * 
 */
private void validateUserMsisdnPin(String p_msisdn,String p_pin,RequestVO p_requestVO)throws BTSLBaseException
  {
	  if(_log.isDebugEnabled())_log.debug("validateUserMsisdnPin","Entered.....p_msisdn::"+p_msisdn +"p_requestVO ::"+p_requestVO.toString());
	  
	//  ChannelUserDAO channelUserDAO = null;
	  ChannelUserVO channelUserVO =null;
	  Connection con = OracleUtil.getConnection();
	  Date currentDate=new Date();
	  try
	  {
	//	  channelUserDAO=  new ChannelUserDAO();
	//	  channelUserVO = channelUserDAO.loadAuthenticateUserDetails(p_con, null, p_msisdn);
		//channelUserVO=ChannelUserCache.getChannelUserVO(p_msisdn);
		  channelUserVO=(ChannelUserVO)p_requestVO.getSenderVO();
		  if (channelUserVO==null)
		  {
			  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.USER_NOT_EXIST);
			  p_requestVO.setMessageCode(PretupsErrorCodesI.USER_NOT_EXIST);  
			  throw new BTSLBaseException(this,"validateUserMsisdnPin",PretupsErrorCodesI.USER_NOT_EXIST);
		  }
		  else
		  {
			//  if(BTSLUtil.isNullString(channelUserVO.getPinRequired()))
			  if(BTSLUtil.isNullString(channelUserVO.getUserPhoneVO().getSmsPin())) 
			  {
				  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.PIN_NOT_FOUND);
				  p_requestVO.setMessageCode(PretupsErrorCodesI.PIN_NOT_FOUND);
				  throw new BTSLBaseException(this,"validateUserMsisdnPin",PretupsErrorCodesI.PIN_NOT_FOUND);
			  }
			  else
			  {
		/*	//	  if(BTSLUtil.decryptText(channelUserVO.getPinRequired()).equals(p_pin))
				  System.out.println("Pin from database"+channelUserVO.getUserPhoneVO().getSmsPin());
				  if(p_pin.equals(channelUserVO.getUserPhoneVO().getSmsPin())) 
				  {
					  	p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
						p_requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				  }
				  else
				  {
					  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.INVALID_PIN);
						p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_PIN);
						throw new BTSLBaseException(this,"validateUserMsisdnPin",PretupsErrorCodesI.INVALID_PIN);
				  
}
*/				  _operatorUtil.validatePIN(con, channelUserVO, BTSLUtil.decryptText(p_pin));
				  if(channelUserVO.getUserPhoneVO().isBarUserForInvalidPin())
					{
						ChannelUserBL.barSenderMSISDN(con,channelUserVO,PretupsI.BARRED_TYPE_PIN_INVALID,currentDate,PretupsI.C2S_MODULE);
						con.commit();     
					}
			  }
		  }
			p_requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
 			p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
	
	  }
	  catch (BTSLBaseException e) 
      {
		  _log.error("validateUserMsisdnPin", "BTSLBaseException " + e.getMessage()); 
          throw e;
      }
      catch(Exception e)
      {
    	  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.REQ_NOT_PROCESS);
    	  _log.error("validateUserMsisdnPin", "Exception " + e.getMessage()); 
          e.printStackTrace();
          EventHandler.handle( EventIDI.SYSTEM_ERROR,  EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserAuthorizationHandler[validateUserMsisdnPin]", "", "", "", "Exception:" + e.getMessage());
          throw new BTSLBaseException(this, "validateUserMsisdnPin", PretupsErrorCodesI.REQ_NOT_PROCESS);
      }	
      finally 
		{
			try{if (con != null)con.close();}catch (Exception e){}
	        if (_log.isDebugEnabled())
	               _log.debug("validateUserMsisdnPin","Exiting  ********");
		}
  }
   
  /**
 * @param p_msisdn
 * @param p_requestVO
 * @return
 * @throws BTSLBaseException
 * @author vikas.kumar
 * Method validateUserMsisdnPswdPin() is used for Validation check for all cases 
 */
  private void validateUserMsisdnPswdPin(String p_loginId,String p_password,String p_msisdn,String p_pin,RequestVO p_requestVO)throws BTSLBaseException
  {
	  if(_log.isDebugEnabled())_log.debug("validateUserMsisdnPswdPin","Entered.....p_loginId::"+p_loginId+"p_msisdn"+p_msisdn);
	  //ChannelUserDAO channelUserDAO = new ChannelUserDAO();
	  ChannelUserVO channeluserVO =null;
	  Connection con = OracleUtil.getConnection();
	  Date currentDate=new Date();
	  try
	  {
	//	  channeluserVO = channelUserDAO.loadAuthenticateUserDetails(p_con, p_loginId,null);
		  channeluserVO=(ChannelUserVO)p_requestVO.getSenderVO();
	
		  if(channeluserVO==null)
		  {
			  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.USER_NOT_EXIST);
			  p_requestVO.setMessageCode(PretupsErrorCodesI.USER_NOT_EXIST); 
			  if(_log.isDebugEnabled()) _log.debug("validateUserMsisdnPswdPin","authentication failed login_id or password .Login ID:: ="+p_loginId);
			  throw new BTSLBaseException(this,"validateUserMsisdnPswdPin",PretupsErrorCodesI.USER_NOT_EXIST);
		  }
		  else
		  {
			  if(BTSLUtil.isNullString(channeluserVO.getPassword()))
			  {
				  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.ERROR_INVALID_PSWD);
				  p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_PSWD);
				  throw new BTSLBaseException(this,"validateUserMsisdnPswdPin",PretupsErrorCodesI.ERROR_INVALID_PSWD);
			  }
			  else
			  {
				 if(p_password.equals(channeluserVO.getPassword())) 
				 {
					  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
					  p_requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);  
				 }
				 else
				 {
					  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.ERROR_INVALID_PSWD);
					  p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_PSWD);
					  throw new BTSLBaseException(this,"validateUserMsisdnPswdPin",PretupsErrorCodesI.ERROR_INVALID_PSWD);
				 }
			  }
			 if(BTSLUtil.isNullString(channeluserVO.getMsisdn()))
			  {
				 p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.ERROR_INVALID_MSISDN);
				 p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_MSISDN);
				 throw new BTSLBaseException(this,"validateUserMsisdnPswdPin",PretupsErrorCodesI.ERROR_INVALID_MSISDN);
			  }
			 else
			 {
				 if(channeluserVO.getMsisdn().equals(p_msisdn))
				 {
					  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS); 
					  p_requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);  
				 }
				 else
				 {
					 p_requestVO.setMessageArguments(new String[]{p_requestVO.getRequestMSISDN()});
					 p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.ERROR_INVALID_MSISDN);
					 p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_MSISDN);
					 throw new BTSLBaseException(this,"validateUserMsisdnPswdPin",PretupsErrorCodesI.ERROR_INVALID_MSISDN);
				 }
			 }
			   if(BTSLUtil.isNullString(channeluserVO.getUserPhoneVO().getPinRequired()))//changed by Shashank for bug removal
			  {
				   p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.PIN_NOT_FOUND); 
				   p_requestVO.setMessageCode(PretupsErrorCodesI.PIN_NOT_FOUND);
				   throw new BTSLBaseException(this,"validateUserMsisdnPswdPin",PretupsErrorCodesI.PIN_NOT_FOUND);
			  }
			   else
			   {
				   _operatorUtil.validatePIN(con, channeluserVO, BTSLUtil.decryptText(p_pin));
					  if(channeluserVO.getUserPhoneVO().isBarUserForInvalidPin())
						{
							ChannelUserBL.barSenderMSISDN(con,channeluserVO,PretupsI.BARRED_TYPE_PIN_INVALID,currentDate,PretupsI.C2S_MODULE);
							con.commit();     
						}
			   }
			   if(BTSLUtil.isNullString(channeluserVO.getLoginID()))
				  {
					 p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.ERROR_INVALID_LOGIN);
					 p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_LOGIN);
					 throw new BTSLBaseException(this,"validateUserMsisdnPswdPin",PretupsErrorCodesI.ERROR_INVALID_LOGIN);
				  }
				 else
				 {
					 if(channeluserVO.getLoginID().equals(p_loginId))
					 {
						  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS); 
						  p_requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);  
					 }
					 else
					 {
						 p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.ERROR_INVALID_LOGIN);
						 p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_LOGIN);
						 throw new BTSLBaseException(this,"validateUserMsisdnPswdPin",PretupsErrorCodesI.ERROR_INVALID_LOGIN);
					 }
				 } 
		  }
		 
	  }
	  catch (BTSLBaseException e) 
      {
		  _log.error("validateUserMsisdnPswdPin", "BTSLBaseException " + e.getMessage()); 
          throw e;
      }
      catch(Exception e)
      {
    	  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.REQ_NOT_PROCESS);
    	  _log.error("validateUserMsisdnPswdPin", "Exception " + e.getMessage()); 
          e.printStackTrace();
          EventHandler.handle( EventIDI.SYSTEM_ERROR,  EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserAuthorizationHandler[validateUserMsisdnPswdPin]", "", "", "", "Exception:" + e.getMessage());
          throw new BTSLBaseException(this, "validateUserMsisdnPswdPin", PretupsErrorCodesI.REQ_NOT_PROCESS);
      }	
      finally 
		{
			try{if (con != null)con.close();}catch (Exception e){}
	        if (_log.isDebugEnabled())
	               _log.debug("validateUserMsisdnPswdPin","Exiting  ********");
		}
     
	  
  }
  
/**
 * @param p_msisdn
 * @param p_requestVO
 * @return
 * @throws BTSLBaseException
 * @author vikas.kumar
 * Method validateUserMsisdn() for validate userMsisdn belongs to particular network or not .
 */
  private boolean  validateUserMsisdn(String p_msisdn,RequestVO p_requestVO)throws BTSLBaseException
  {
	  if(_log.isDebugEnabled())_log.debug("validateUserMsisdn","Entered.....p_msisdn::"+p_msisdn);
	  String filteredMsisdn=null;
	  String msisdnPrefix=null;
      NetworkPrefixVO networkPrefixVO=null;
      String networkCode=null;
      boolean isMSISDNValid = true;
	  
	 try
	 {
		 if(!BTSLUtil.isValidMSISDN(p_msisdn))
		 {
			 _requestMap.put("RES_ERR_KEY",p_requestVO.getFilteredMSISDN());
			 isMSISDNValid =false;
			 throw new BTSLBaseException(this,"validateUserMsisdn",PretupsErrorCodesI.CCE_ERROR_INVALID_MSISDN);
		 }
		 filteredMsisdn = PretupsBL.getFilteredMSISDN(p_msisdn);
		 //get prefix of the MSISDN
		 msisdnPrefix = PretupsBL.getMSISDNPrefix (filteredMsisdn); // get the prefix of the MSISDN
		 networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
		 if(networkPrefixVO == null)
		 {
			 isMSISDNValid =false;
			 if(_log.isDebugEnabled()) _log.debug("validateUserMsisdn","No Network prefix found for msisdn="+p_msisdn);
			 	throw new BTSLBaseException(this,"validateUserMsisdn",PretupsErrorCodesI.CCE_XML_ERROR_UNSUPPORTED_NETWORK);
		 }
		 // check network support of the MSISDN
		 networkCode=networkPrefixVO.getNetworkCode();
		 if(!networkCode.equals(((UserVO)p_requestVO.getSenderVO()).getNetworkID()))
		 {
			 isMSISDNValid =false;
			 if(_log.isDebugEnabled()) _log.debug("validateUserMsisdn","No supporting Network for msisdn="+p_msisdn);
			 throw new BTSLBaseException(this,"validateUserMsisdn",PretupsErrorCodesI.CCE_XML_ERROR_NETWORK_NOT_MATCHING_REQUEST);
		 }
	 }
      catch (BTSLBaseException e) 
      {
    	  isMSISDNValid =false;
          _log.error("validateUserMsisdn", "BTSLBaseException " + e.getMessage()); 
          throw e;
      }
      catch(Exception e)
      {
    	  isMSISDNValid =false;
    	  _log.error("validateUserMsisdn", "Exception " + e.getMessage()); 
          e.printStackTrace();
          EventHandler.handle( EventIDI.SYSTEM_ERROR,  EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserAuthorizationHandler[validateUserMsisdn]", "", "", "", "Exception:" + e.getMessage());
          throw new BTSLBaseException(this, "validateUserMsisdn", PretupsErrorCodesI.REQ_NOT_PROCESS);
      }	
      if(_log.isDebugEnabled())_log.debug("validateUserMsisdn","Exiting  ::::isMSISDNValid "+isMSISDNValid);
      
      return isMSISDNValid; 
  }
  /**
 * @param p_con
 * @param p_loginId
 * @param p_password
 * @param p_msisdn
 * @param p_pin
 * @param p_requestVO
 * @throws BTSLBaseException
 * @author vikas.kumar
 * Method validateUserForOtherCase() for other cases if Msisdn is present 
 */
private void validateUserForOtherCase(String p_loginId,String p_password,String p_msisdn,String p_pin,RequestVO p_requestVO)throws BTSLBaseException
  {
	  if(_log.isDebugEnabled())_log.debug("validateUserForOtherCase","Entered.....p_loginId::"+p_loginId+"p_msisdn"+p_msisdn+"p_password"+p_password+"p_pin"+p_pin);
	//  ChannelUserDAO channelUserDAO = new ChannelUserDAO();
	  ChannelUserVO channeluserVO =null;
	  Connection con = OracleUtil.getConnection();
	  Date currentDate=new Date();
	  try
	  {
		  //channeluserVO = channelUserDAO.loadAuthenticateUserDetails(null,p_msisdn);
		  channeluserVO=(ChannelUserVO)p_requestVO.getSenderVO();
		  
	
		  if(channeluserVO==null)
		  {
			  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.USER_NOT_EXIST);
			  p_requestVO.setMessageCode(PretupsErrorCodesI.USER_NOT_EXIST); 
			  if(_log.isDebugEnabled()) _log.debug("validateUserForOtherCase","authentication failed login_id or password .Login ID:: ="+p_loginId);
			  throw new BTSLBaseException(this,"validateUserMsisdn",PretupsErrorCodesI.USER_NOT_EXIST);
		  }
		  else
		  {
			  if(!BTSLUtil.isNullString(p_password))
			  {
			  
				  if(BTSLUtil.isNullString(channeluserVO.getPassword()))
				  {
					  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.ERROR_INVALID_PSWD);
					  p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_PSWD);
					  throw new BTSLBaseException(this,"validateUserForOtherCase",PretupsErrorCodesI.ERROR_INVALID_PSWD);
				  }
				  else
				  {
					  if(p_password.equals(channeluserVO.getPassword())) 
					  {
						  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
						  p_requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);  
					  }
					  else
					  {
						  if(_log.isDebugEnabled()) _log.debug("validateUserForOtherCase","authentication failed invalid  password "+channeluserVO.getPassword()+".p_password:: ="+p_password);
						  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.ERROR_INVALID_PSWD);
						  p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_PSWD);
						  throw new BTSLBaseException(this,"validateUserForOtherCase",PretupsErrorCodesI.ERROR_INVALID_PSWD);
					
					  }
				  }
			  }
			  if(!BTSLUtil.isNullString(p_loginId))
			  {
				  if(BTSLUtil.isNullString(channeluserVO.getLoginID()))
				  {
					  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.ERROR_INVALID_LOGIN);
					  p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_LOGIN);
					  throw new BTSLBaseException(this,"validateUserForOtherCase",PretupsErrorCodesI.ERROR_INVALID_LOGIN);
				  }
				  else
				  {
					  if(channeluserVO.getLoginID().equals(p_loginId))
					  {
						  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS); 
						  p_requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);  
					  }
					  else
					  {
						  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.ERROR_INVALID_LOGIN);
						  p_requestVO.setMessageCode(PretupsErrorCodesI.ERROR_INVALID_LOGIN);
						 
						  
						  throw new BTSLBaseException(this,"validateUserForOtherCase",PretupsErrorCodesI.ERROR_INVALID_LOGIN);
					  }
				  }
			  }
			 
			 if(!BTSLUtil.isNullString(p_pin))
			 {
			   if(BTSLUtil.isNullString(channeluserVO.getPinRequired()))
			  {
				   p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.PIN_NOT_FOUND); 
				   p_requestVO.setMessageCode(PretupsErrorCodesI.PIN_NOT_FOUND);
				   throw new BTSLBaseException(this,"validateUserForOtherCase",PretupsErrorCodesI.PIN_NOT_FOUND);
			  }
			   else
			   {
				   _operatorUtil.validatePIN(con, channeluserVO, BTSLUtil.decryptText(p_pin));
					  if(channeluserVO.getUserPhoneVO().isBarUserForInvalidPin())
						{
							ChannelUserBL.barSenderMSISDN(con,channeluserVO,PretupsI.BARRED_TYPE_PIN_INVALID,currentDate,PretupsI.C2S_MODULE);
							con.commit();     
						}
			   }
			   
		  }
			 }
		 
	  }
	  catch (BTSLBaseException e) 
      {
		  _log.error("validateUserForOtherCase", "BTSLBaseException " + e.getMessage()); 
          throw e;
      }
      catch(Exception e)
      {
    	  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.REQ_NOT_PROCESS);
    	  _log.error("validateUserForOtherCase", "Exception " + e.getMessage()); 
          e.printStackTrace();
          EventHandler.handle( EventIDI.SYSTEM_ERROR,  EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserAuthorizationHandler[validateUserForOtherCase]", "", "", "", "Exception:" + e.getMessage());
          throw new BTSLBaseException(this, "validateUserForOtherCase", PretupsErrorCodesI.REQ_NOT_PROCESS);
      }	
      finally 
		{
			try{if (con != null)con.close();}catch (Exception e){}
	        if (_log.isDebugEnabled())
	               _log.debug("validateUserForOtherCase","Exiting  ********");
		}
     
	  
  }

private void validateUserExtCode(String p_extCode,RequestVO p_requestVO)throws BTSLBaseException
{
	  if(_log.isDebugEnabled())_log.debug("validateUserExtCode","Entered.....p_msisdn::"+p_extCode +"p_requestVO ::"+p_requestVO.toString());
	  ChannelUserVO channelUserVO =null;
	  try
	  {
		  channelUserVO=(ChannelUserVO)p_requestVO.getSenderVO();
		  if (channelUserVO==null)
		  {
			  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.USER_NOT_EXIST);
			  p_requestVO.setMessageCode(PretupsErrorCodesI.USER_NOT_EXIST);  
			  throw new BTSLBaseException(this,"validateUserExtCode",PretupsErrorCodesI.USER_NOT_EXIST);
		  }
		  else
		  {
			  if(BTSLUtil.isNullString(channelUserVO.getExternalCode()))
			  {
				  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
				  p_requestVO.setMessageCode(PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
				  throw new BTSLBaseException(this,"validateUserExtCode",PretupsErrorCodesI.EXT_XML_ERROR_INVALID_EXTCODE);
			  }
			  else
			  {
				  if(channelUserVO.getExternalCode().equals(p_extCode)) 
				  {
					  	p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
						p_requestVO.setMessageCode(PretupsErrorCodesI.TXN_STATUS_SUCCESS);
				  }
				  else
				  {
					  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.INVALID_EXTCODE);
						p_requestVO.setMessageCode(PretupsErrorCodesI.INVALID_EXTCODE);
						throw new BTSLBaseException(this,"validateUserExtCode",PretupsErrorCodesI.INVALID_EXTCODE);
				  }
			  }
		  }
	  }
	  catch (BTSLBaseException e) 
    {
		  _log.error("validateUserExtCode", "BTSLBaseException " + e.getMessage()); 
        throw e;
    }
    catch(Exception e)
    {
  	  p_requestVO.setTxnAuthStatus(PretupsErrorCodesI.REQ_NOT_PROCESS);
  	  _log.error("validateUserMsisdnPin", "Exception " + e.getMessage()); 
        e.printStackTrace();
        EventHandler.handle( EventIDI.SYSTEM_ERROR,  EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "UserAuthorizationHandler[validateUserMsisdnPin]", "", "", "", "Exception:" + e.getMessage());
        throw new BTSLBaseException(this, "validateUserMsisdnPin", PretupsErrorCodesI.REQ_NOT_PROCESS);
    }	
    if(_log.isDebugEnabled())_log.debug("validateUserMsisdnPin","Exiting  ********");
}
}


package com.btsl.pretups.util.clientutils;

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
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

public class VietnamUtil extends OperatorUtil {
	private  final Log _log = LogFactory.getLog(this.getClass().getName());	
	@Override
    public HashMap validatePassword(String p_loginID, String p_password) {
		LogFactory.printLog("validatePassword", "Entered" ,_log);
        final HashMap messageMap = new HashMap();

        String defaultPin = BTSLUtil.getDefaultPasswordNumeric(p_password);

        if (defaultPin.equals(p_password)) {
            return messageMap;
        }
        defaultPin = BTSLUtil.getDefaultPasswordText(p_password);

        if (defaultPin.equals(p_password)) {
            return messageMap;
        }
        
        if (p_password.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH))).intValue() || p_password.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH))).intValue()) {
            final String[] args = { String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH))).intValue()), String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH))).intValue()) };
            messageMap.put("operatorutil.validatepassword.error.passwordlenerr", args);
        }
        final int result = BTSLUtil.isSMSPinValid(p_password);// for consecutive
                                                              // and
        // same characters
        if (result == -1) {
            messageMap.put("operatorutil.validatepassword.error.passwordsamedigit", null);
        } else if (result == 1) {
            messageMap.put("operatorutil.validatepassword.error.passwordconsecutive", null);
        }        
        if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.LOGIN_PASSWORD_ALLOWED))).booleanValue() && p_password.contains(" ")){
        	messageMap.put("user.adduser.error.password.space.not.allowed", null);
        }
        
          // For OCI Password Should contains atleast one character
          if(!BTSLUtil.containsChar(p_password)) messageMap.put(
          "operatorutil.validatepassword.error.passwordnotcontainschar",null);
          
          // for special character String
          String specialChar=Constants.getProperty("SPECIAL_CHARACTER_PASSWORD_VALIDATION");
          if(!BTSLUtil.isNullString(specialChar))
          {
              String[] specialCharArray={specialChar};
              String[] passwordCharArray=specialChar.split(",");
              boolean specialCharFlag=false;
              for(int i=0,j=passwordCharArray.length;i<j;i++)
              {
                  if(p_password.contains(passwordCharArray[i]))
                  {
                      specialCharFlag=true;
                      break;
                  }
              }
              if(!specialCharFlag)
                  messageMap.put("operatorutil.validatepassword.error.passwordspecialchar",specialCharArray);
          }  // for number
          String[]passwordNumberStrArray={"0","1","2","3","4","5","6","7","8","9"};
          boolean numberStrFlag=false;
          for(int i=0,j=passwordNumberStrArray.length;i<j;i++)
          {
              if(p_password.contains(passwordNumberStrArray[i]))
              {
                  numberStrFlag=true;
                  break;
              }
          }
          if(!numberStrFlag)
              messageMap.put("operatorutil.validatepassword.error.passwordnumberchar",null);
          
          LogFactory.printLog("validatePassword","Exiting ",_log);
          return messageMap;
      }
	
	@Override
	public String formatRequestXMLString(String p_requestXML) throws BTSLBaseException{

		String requestStr="";
		try{
			requestStr=p_requestXML;

			if(requestStr.contains("encoding")){
				requestStr=requestStr.replaceAll("encoding=\"UTF-8\"", "");
				requestStr=requestStr.replaceAll("encoding=\"UTF-16\"", "");	
			}
			requestStr=requestStr.replaceAll("<(\\w+)( [^/>]*)?/>","<$1$2></$1>");
		}
		catch (Exception e) {
			requestStr=p_requestXML;
		}
		return requestStr;

	}
	public boolean validateTransactionPassword(ChannelUserVO p_channelUserVO, String p_password) throws BTSLBaseException {
        final String obj = "validateTransactionPassword";
        LogFactory.printLog(obj, " Entered p_channelUserVO=:" + p_channelUserVO + " p_password=" + p_password,_log);
        boolean passwordValidation = true;
        try {
            if (p_channelUserVO != null) {
                /*
                 * change done by ashishT for hashing implementation comparing
                 * the password hashvlue from db to the password sent by user.
                 */
                // if(!BTSLUtil.isNullString(p_channelUserVO.getPassword()) &&
                // (PretupsI.FALSE.equalsIgnoreCase(BTSLUtil.compareHash2String(p_channelUserVO.getPassword(),
                // p_password))))
                if ("SHA".equalsIgnoreCase((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PINPAS_EN_DE_CRYPTION_TYPE))) {
                    boolean checkpassword;
                    if (p_password.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH))).intValue()) {
                        checkpassword = BTSLUtil.decryptText(p_channelUserVO.getPassword()).equals(p_password);
                    } else {
                        checkpassword = (!PretupsI.FALSE.equalsIgnoreCase(BTSLUtil.compareHash2String(p_channelUserVO.getPassword(), p_password)));
                    }
                    if (!BTSLUtil.isNullString(p_channelUserVO.getPassword()) && (!checkpassword)) {
                        passwordValidation = false;
                    }
                } else {
                	 if (BTSLUtil.isNullString(p_password)){
                		 passwordValidation = false;
                	 }
                	 if (!BTSLUtil.isNullString(p_channelUserVO.getPassword()) && (PretupsI.FALSE.equalsIgnoreCase(BTSLUtil.compareHash2String(p_channelUserVO.getPassword(),
                        p_password)))) {
                        passwordValidation = false;
                    }
                }

                // if(!BTSLUtil.isNullString(p_password) &&
                // ((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.C2S_DEFAULT_PASSWORD)).equals(p_password))
                // throw new BTSLBaseException(this, "loadValidateUserDetails",
                // PretupsErrorCodesI.XML_ERROR_CHANGE_DEFAULT_PASSWD)
            } else {
                throw new BTSLBaseException("OperatorUtil", obj, PretupsErrorCodesI.XML_ERROR_NO_SUCH_USER);
            }

        } catch (BTSLBaseException bex) {
            throw new BTSLBaseException(bex);
        } catch (Exception e) {
            _log.errorTrace(obj, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[validateTransactionPassword]", "", "",
                "", "Exception:" + e.getMessage());
            throw new BTSLBaseException("OperatorUtil", obj, PretupsErrorCodesI.ERROR_EXCEPTION);
        } finally {
        	LogFactory.printLog(obj, " Exiting passwordValidation=" + passwordValidation,_log);
        }
        return passwordValidation;
    }
}

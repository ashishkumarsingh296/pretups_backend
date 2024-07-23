/**
 * @(#)ClaroUtil.java
 *                    Copyright(c) 2010, Comviva Technologies Ltd.
 *                    All Rights Reserved
 * 
 *                    Claro Util class
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Author Date History
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 *                    Zeeshan Aleem July 27, 2016 Initital Creation
 *                    ----------------------------------------------------------
 *                    ---------------------------------------
 * 
 */

package com.btsl.pretups.util.clientutils;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Random;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.CommonClient;
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
import com.btsl.pretups.channel.transfer.businesslogic.ChannelTransferVO;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleCache;
import com.btsl.pretups.network.businesslogic.NetworkInterfaceModuleVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.product.businesslogic.ProductVO;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.transfer.businesslogic.TransferItemVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.CryptoUtil;
import com.ibm.icu.util.Calendar;
import com.web.pretups.channel.transfer.businesslogic.ChannelTransferWebDAO;

public class RightelUtil extends OperatorUtil {
    private  final Log _log = LogFactory.getLog(this.getClass().getName());
    private long seed;
    private static String ENCKEY = null;
    
    public String formatVomsSerialnum(long p_counter, String p_activeproductid, String segment, String nwCode) {
        String returnStr = null;
        int maxserialnumlength = 12;// by default
        int year = 0;
        int month = 0;
        String strYear;
        final String METHOD_NAME = "formatVomsSerialnum";
        try {
            maxserialnumlength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MAX_LENGTH))).intValue();
            if (maxserialnumlength == 0) {
                maxserialnumlength = 12;
            }
            String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(Long.toString(p_counter), maxserialnumlength - 7);
            p_activeproductid = BTSLUtil.padZeroesToLeft(p_activeproductid, 3);
            Calendar currDate = BTSLDateUtil.getInstance();
            year = currDate.get(Calendar.YEAR);
            month = currDate.get(Calendar.MONTH);
            strYear = String.valueOf(year);
            month=month+1;
            strYear = strYear.substring(2, 4);
            String monthtr = BTSLUtil.padZeroesToLeft(Long.toString(month),2);
            returnStr =  p_activeproductid +strYear+monthtr +paddedTransferIDStr;// 2+013+00000000001
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "formatVomsSerialnum[]", "", "", "", "Not able to generate Voms Serial num:" + e.getMessage());
            returnStr = null;
        }
        return returnStr;
    }
    

    
    public ArrayList generatePin(String location_code,String product_code,long totalCount,int seq)throws IOException
    {
            _log.debug("generatePin", "Entered "+location_code+"product_code"+"totalCount"+totalCount);
            // It requires two parameter
            // No of PINs to be generated
            // Length of PIN
            // Length of PIN decide Number of bits. like as
            // for 16 digit number, number of bits is 53
            // for 18 digit number, number of bits iv 59
            // for 20 digit number, number of bits are 64
            long tmpPIN = 0;
            int length=0;
            String actualPIN ;

	ArrayList vPinArraylist = new ArrayList();
	LinkedHashSet treesetAll = new LinkedHashSet();
	
	//ArrayList tempArray = new ArrayList();
	try
	{
		int year=0;
		String strYear=null;
		java.util.Calendar currDate=java.util.Calendar.getInstance();
		year=currDate.get(Calendar.YEAR);
		_log.debug("generatePin","Year :"+year);
		strYear=String.valueOf(year);
		strYear=strYear.substring(3,4);
	    String seqID= Integer.toString(seq);
        if(seqID.length()==1&&((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
        	seqID='0'+seqID;
        }
        
		_log.debug("generatePin","strYear"+strYear);
		int pValue=0;
		int pinLength=0;
		int randomDigitLength=0;
		try{ pValue=Integer.parseInt(Constants.getProperty("RAND_PIN_GEN"));}catch(Exception e){pValue=46;}
		pinLength=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MAX_LENGTH))).intValue();
		if(pinLength==0)
			pinLength=16;
		
		 if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
			 pinLength = pinLength-seqID.length()  ;//1 for year 3 for product code 2 for sequence id
	       }
	            randomDigitLength = pinLength ;
		//long startTime=System.nanoTime();
		for (int Count= 0; Count <totalCount ; )
		{
			//for 16 digit random number
			//BigInteger bigint = new BigInteger(53,objRandom);
			//For 13 digit random number
			//BigInteger bigint = new BigInteger(44,objRandom);
			//For 12 digit random number
			//BigInteger bigint = new BigInteger(40,objRandom);
			//BigInteger bigint = new BigInteger(32,objRandom);
			//for 10 digit random number
			//for 11 digit random number
			// BigInteger bigint = new BigInteger(36,objRandom);

			Random objRandom =new Random();
			seed=generateSeed();
			objRandom.setSeed(seed);
			//pValue=46 for 14 Random
			BigInteger bigint = new BigInteger(pValue,objRandom);
			tmpPIN = bigint.longValue() ;
			actualPIN = String.valueOf(tmpPIN);
			length = actualPIN.length();
			boolean flag=true;
			if (length < randomDigitLength)
			{
				actualPIN=this.padRandomToLeft(actualPIN, randomDigitLength);
				
				 if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
					 actualPIN=seqID+actualPIN;
				 }
				 
				 
				 
				if(flag){
					if(treesetAll.contains(actualPIN))
					{
						flag=false;
					}
					if(flag){
						treesetAll.add(actualPIN);
						Count++;
					}
				}

			}
			else if(length==randomDigitLength)
			{
				if(flag){

					if(treesetAll.contains(actualPIN))
					{

						flag=false;
					}
					if(flag){
						treesetAll.add(actualPIN);
						Count++;
					}
				}
			}
			else
			{
			}
		}
		ENCKEY = (Constants.KEY);
		_log.debug("generatePin","Pins Generated");
		vPinArraylist=encryptList(treesetAll,ENCKEY);
		_log.debug("generatePin","Pins Encripted vPinArraylist.size()"+vPinArraylist.size());
		return vPinArraylist;
	}
	catch(Exception e)
	{
		_log.error("generatePin","System generated Error in pin generation :"+e);
	}
	return vPinArraylist;
}
    
    private long generateSeed() {
        SecureRandom objrandom = new SecureRandom();
        BigInteger bigint = new BigInteger(24, objrandom);
        Date date = new Date();
        seed = date.getTime() + bigint.longValue();
        return seed;
    }

	
	public static String padRandomToLeft(String p_strValue, int p_strLength)
	{
		int cntr = p_strLength - p_strValue.length();
		Random r=new Random();
		if(cntr > 0)
		{
			for(int i=0; i<cntr; i++)
			{
				int j = r.nextInt(9	) ;

				p_strValue = j+ p_strValue;

			}
		}
		return p_strValue;
	}

	 
	private ArrayList encryptList(LinkedHashSet p_list,String p_key)
	{
		ArrayList cipherList = new ArrayList();
		String plainText=null,cipherText;
		CryptoUtil util=null;
		try
		{
			util=new CryptoUtil();

                        Iterator itr=p_list.iterator();
                        int i=0;
                        while(itr.hasNext()){
                                plainText=(String)itr.next();
                                cipherText = util.encrypt(plainText,Constants.KEY);
                                cipherList.add(i,(String)cipherText);
                                i++;

                        }

                }
                catch (Exception e)
                {
                        e.printStackTrace();
                }

                return cipherList;
        }
	
	public HashMap validatePassword(String p_loginID, String p_password)
	{
		_log.debug("validatePassword","Entered, p_userID= ",new String(p_loginID+", Password= "+p_password));
		HashMap messageMap=new HashMap();
		String defaultPin = BTSLUtil.getDefaultPasswordNumeric(p_password);
		if(defaultPin.equals(p_password))
			return messageMap;
		defaultPin = BTSLUtil.getDefaultPasswordText(p_password);
		if(defaultPin.equals(p_password))
			return messageMap;
		if (p_password.length() < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH))).intValue() || p_password.length() > ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH))).intValue())
		{
			String[] args={String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH))).intValue()),String.valueOf(((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_LOGIN_PWD_LENGTH))).intValue())};
			messageMap.put("operatorutil.validatepassword.error.passwordlenerr", args);
		}
		int result=BTSLUtil.isSMSPinValid(p_password);//for consecutive and same characters
		if(result==-1)
			messageMap.put("operatorutil.validatepassword.error.passwordsamedigit",null);
		else if(result==1)
			messageMap.put("operatorutil.validatepassword.error.passwordconsecutive",null);
		if(!BTSLUtil.containsChar(p_password))
			messageMap.put("operatorutil.validatepassword.error.passwordnotcontainschar",null);
		// for special character
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

		}   	
		// for number
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
		if(p_loginID.equals(p_password))
			messageMap.put("operatorutil.validatepassword.error.sameusernamepassword",null);
		if(_log.isDebugEnabled()) _log.debug("validatePassword","Exiting ");
		return messageMap;
	}

	/**
	 * Method generateRandomPassword.
	 * @return String
	 */
	public String generateRandomPassword()
	{
		final String METHOD_NAME = "generateRandomPassword";

		if(_log.isDebugEnabled()) 
			_log.debug(METHOD_NAME,"Entered in to GuatemalaUtil");
		String returnStr=null;
		String specialStr="";
		String numberStr=null;
		String alphaStr=null;
		String finalStr=null;
		String SPECIAL_CHARACTERS=null;
		int decreseCounter=0;
		try
		{
			String specialChar=Constants.getProperty("SPECIAL_CHARACTER_PASSWORD_VALIDATION");
			if(!BTSLUtil.isNullString(specialChar))
			{
				decreseCounter=1;
				specialChar=specialChar.replace(",","");
				SPECIAL_CHARACTERS= specialChar;//"~!@#$%^&";
				specialStr=BTSLUtil.generateRandomPIN(SPECIAL_CHARACTERS,decreseCounter);
			}
			final String DIGITS= "0123456789";
			numberStr=BTSLUtil.generateRandomPIN(DIGITS,1);
			decreseCounter++;
			final String LOCASE_CHARACTERS    = "abcdefghijklmnopqrstuvwxyz";
			final String UPCASE_CHARACTERS    = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; 			
			final String PRINTABLE_CHARACTERS =LOCASE_CHARACTERS+UPCASE_CHARACTERS;
			int minLength=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_LOGIN_PWD_LENGTH))).intValue();
			while (true){   
				alphaStr=BTSLUtil.generateRandomPIN(PRINTABLE_CHARACTERS,minLength-decreseCounter);
				int  result=BTSLUtil.isSMSPinValid(alphaStr);
				if(result==-1) 
					continue;
				else if(result==1)
					continue;
				else 
					break;	            	            
			}
			finalStr=specialStr+alphaStr+numberStr;
			returnStr=BTSLUtil.generateRandomPIN(finalStr,minLength);
		}
		catch(Exception e)
		{
			_log.errorTrace(METHOD_NAME, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"OperatorUtil[generateRandomPassword]","","","","Exception generate Random Password="+e.getMessage());
			returnStr=null;
		}
		if (_log.isDebugEnabled())
			_log.debug("generateRandomPassword","Exiting from GuatemalaUtil = " + returnStr);
		return returnStr;
	
	}
	
	
	  @Override
	    public String formatSOSTransferID(TransferVO p_transferVO, long p_tempTransferID) {
	        final String methodName = "formatSOSTransferID";
	        String returnStr = null;
	        try {
	        	
	        	 final String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(Long.toHexString(p_tempTransferID), P2P_TRANSFER_ID_PAD_LENGTH);
	             returnStr = "E" + currentDateTimeFormatString(p_transferVO.getCreatedOn()) + "." + currentTimeFormatString(p_transferVO.getCreatedOn()) + "." + Constants
	                 .getProperty("INSTANCE_ID") + paddedTransferIDStr;
	            p_transferVO.setTransferID(returnStr);
	        } catch (Exception e) {
	            _log.errorTrace(methodName, e);
	            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OperatorUtil[formatSOSTransferID]", "", "", "",
	                "Not able to generate Transfer ID:" + e.getMessage());
	            returnStr = null;
	        }
	        return returnStr;
	    }
	  

		public  String generateRandomSID() throws BTSLBaseException 
		{
			String sid=null;
			long sidLong=0;
			long minrang=0;
			long maxrang=0;
			final String obj = "generateRandomSID";
			try{
				if (_log.isDebugEnabled()) {
					_log.debug(obj, " Entered str");
				} 
				int min=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_SID_LENGTH))).intValue();
				int max=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MAX_SID_LENGTH))).intValue();
				if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SID_ISNUMERIC))).booleanValue())
				{
					//String code = Constants.getProperty("COUNTRY_CODE");
					String code = (((String) PreferenceCache.getSystemPreferenceValue(PreferenceI.PRVT_RC_MSISDN_PREFIX_LIST)).split(","))[0];// rahuld 
					int len=code.length();
					max=max-len;

					maxrang=rangeSid(max);

					if(len<min)
					{
						minrang=1;
					}
					else
					{
						if(min==len){
							min=0;
						} else {
							throw new BTSLBaseException ("OperatorUtil","SID generation is imposible, Minimum SID length must be lessthen 10.");
						}
					}

					sidLong =rand(minrang, maxrang);
					sid = new Long(sidLong).toString();
					sid=code.concat(sid);
					
					if(sid.length()<min){
						
						int length =min-sid.length();
						
						for(int a=0;a<length;a++)
						{
							sid= sid+'0';
						}
					}
				}

				else
				{
					Random m_generator = new Random();

					int randomSidLen = BTSLUtil.parseDoubleToInt((m_generator.nextDouble() * (max - min + 1) )) + min;
					String alphabet="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"; 
					int character= BTSLUtil.parseDoubleToInt((m_generator.nextDouble()*52)); 
					String startingAlphabet=alphabet.substring(character, character+1);
					final String DIGITS = "0123456789";
					final String LOCASE_CHARACTERS = "abcdefghijklmnopqrstuvwxyz";
					final String UPCASE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";                   
					final String VALID_CHARACTERS =LOCASE_CHARACTERS+UPCASE_CHARACTERS+DIGITS;
					sid = generateAlphaNumericSID(VALID_CHARACTERS,randomSidLen,startingAlphabet);      
				}

			}
			catch (BTSLBaseException be)
			{
				_log.errorTrace(obj,be);
				_log.error(obj, "BTSLBaseException " + be.getMessage());
			}
			catch (Exception e)
			{
				_log.errorTrace(obj, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"BTSLUtil[generateRandomSID]","","","","Exception:"+e.getMessage());
			} 
			finally
			{
				if (_log.isDebugEnabled()) {
					_log.debug(obj, " Exited ");
				}
			}
			return sid;
		}

		@Override
		public ArrayList removeVMSProductCodeList(ArrayList allproductList)  {

			final String obj = "loadProductCodeList";

			ArrayList productList=new ArrayList();
			
			try{
				ArrayList vmsProducts= LookupsCache.loadLookupDropDown(PretupsI.VMS_PRODUCT_TYPE, true);
				
				if(vmsProducts!=null && vmsProducts.size()>0)
				{
					
					boolean flag=false;
					for (Iterator iterator = allproductList.iterator(); iterator.hasNext();) {
						flag=false;
						Object objectValueVO = (Object) iterator.next();
						try{
							ListValueVO listValueVO=(ListValueVO)objectValueVO;
							for (Iterator iterator1 = vmsProducts.iterator(); iterator1.hasNext();) {
								ListValueVO listValueVO1 = (ListValueVO) iterator1.next();
								if(listValueVO.getValue().equalsIgnoreCase(listValueVO1.getValue()))
								{
									flag=true;	
									break;
								}
							}	
							
							if(flag==false){
								productList.add(listValueVO);
							}
						}catch(Exception e)
						{
							ProductVO listValueVO = (ProductVO) objectValueVO;
							for (Iterator iterator1 = vmsProducts.iterator(); iterator1.hasNext();) {
								ListValueVO listValueVO1 = (ListValueVO) iterator1.next();
								if(listValueVO.getProductCode().equalsIgnoreCase(listValueVO1.getValue()))
								{
									flag=true;	
									break;
								}
							}	
							
							if(flag==false){
								productList.add(listValueVO);
							}
						}
						
						

						
					}
				}else{
					productList=allproductList;
				}

			}catch(Exception e)
			{
				productList=allproductList;
				_log.errorTrace(obj, e);
				EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"BTSLUtil[loadProductCodeList]","","","","Exception:"+e.getMessage());

			}
			return productList;

		}
		
		public RequestVO parsePaymentMessage(RequestVO p_requestVO,String reqMessage) {

			try{
				_log.debug("parseRequestForMessage() ,PG Data=", reqMessage);
				String[] stringEach=reqMessage.split("\\|");
				String message="";
				String refNo="";
				String statusCode="";
				String statusCodeN="";
				String remarks="";
				String txnID="";
				for (int i = 0; i < stringEach.length; i++) {
					if(stringEach[i].contains("MESSAGE="))
					{
						String messageOnly=stringEach[i].toString();
						String[] stringEachnew=messageOnly.split("\\&");
						for (int j = 0; j < stringEachnew.length; j++) {
							String[] stringEachnew1=stringEachnew[j].split("=");
							if(stringEachnew1[0].equalsIgnoreCase("MESSAGE"))
							{
								message=stringEachnew1[1].toString();
							}if(stringEachnew1[0].equalsIgnoreCase("StateCode"))
							{
								statusCodeN=stringEachnew1[1].toString();
								if(stringEachnew1[1].toString().equalsIgnoreCase("0")){
									statusCode=PretupsI.SUCCESS;
								}else {
									statusCode=PretupsI.FAIL;
								}
								p_requestVO.setInfo2(statusCodeN);
							}if(stringEachnew1[0].equalsIgnoreCase("State"))
							{
								remarks=stringEachnew1[1].toString();
								p_requestVO.setInfo3(remarks);
							}
							if(stringEachnew1[0].equalsIgnoreCase("RefNum"))
							{
								if(stringEachnew1.length>1){
									refNo=stringEachnew1[1].toString();	
								}else{
									refNo="NULL";
								}
							}
							if(stringEachnew1[0].equalsIgnoreCase("ResNum"))
							{
								txnID=stringEachnew1[1].toString();

							}
							if(stringEachnew1[0].equalsIgnoreCase("RRN"))
							{
								if(stringEachnew1.length>1){
									p_requestVO.setInfo4(stringEachnew1[1].toString());
								}else{
									p_requestVO.setInfo4("");
								}
							}
							if(stringEachnew1[0].equalsIgnoreCase("TRACENO"))
							{
								if(stringEachnew1.length>1){
									p_requestVO.setInfo5(stringEachnew1[1].toString());
								}else{
									p_requestVO.setInfo5("");
								
								}
							}
							if(stringEachnew1[0].equalsIgnoreCase("CID"))
							{
								if(stringEachnew1.length>1){
									p_requestVO.setInfo6(stringEachnew1[1].toString());
								}else{
									p_requestVO.setInfo6("");
								}
							}
						}

					}
				}

				message=message.replace("STATUS", statusCode);
				if(refNo.equalsIgnoreCase("NULL"))
				{
					message=message.replace("REFNO", txnID+"R");
				}else{
					message=message.replace("REFNO", refNo);
				}

				p_requestVO.setRequestMessage(message);
				if (!BTSLUtil.isNullString(message)) 
					p_requestVO.setRequestMessageArray(message.split(" "));
				
			}catch(Exception ex)
			{
				p_requestVO.setRequestMessage(reqMessage);
			}
			return p_requestVO;
		}
		@Override
	    public Map validatePaymentRefId(ChannelTransferVO channelTransferVO) {
			
			final ChannelTransferWebDAO channelTransferWebDAO = new ChannelTransferWebDAO();
			
			final NetworkInterfaceModuleVO networkInterfaceModuleVOS = (NetworkInterfaceModuleVO) NetworkInterfaceModuleCache.getObject(PretupsI.C2S_MODULE, channelTransferVO.getNetworkCode(), PretupsI.INTERFACE_CATEGORY_PG);
			TransferItemVO transferItemVO;
			Connection con = null;
			MComConnectionI mcomCon = null;
			Map retunMap = new HashMap();
			
			try {
				mcomCon = new MComConnection();   
				con = mcomCon.getConnection();
	            
				transferItemVO = channelTransferWebDAO.loadUserInterfaceDetails(con, channelTransferVO.getPaymentInstSource());
				StringBuffer strBuff =  new StringBuffer();
		        strBuff.append("INTERFACE_ACTION=" + PretupsI.INTERFACE_VALIDATE_ACTION);
		        strBuff.append("&TRANSACTION_ID=" + channelTransferVO.getTransferID());
		        strBuff.append("&INTERFACE_ID=" + transferItemVO.getInterfaceID());
		        strBuff.append("&INTERFACE_HANDLER=" + transferItemVO.getInterfaceHandlerClass());
		        strBuff.append("&INTERFACE_AMOUNT=" + PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()));
		        strBuff.append("&NETWORK_CODE=" + channelTransferVO.getNetworkCode());
		        strBuff.append("&MSISDN=" + channelTransferVO.getToUserMsisdn());
		        strBuff.append("&REFNO=" + channelTransferVO.getPayInstrumentNum());
		        strBuff.append("&USER_TYPE=R");
		        strBuff.append("&MODULE=" + PretupsI.INTERFACE_CATEGORY_PG);
		        strBuff.append("&INT_MOD_COMM_TYPE=" + networkInterfaceModuleVOS.getCommunicationType());
				strBuff.append("&INT_MOD_IP=" + networkInterfaceModuleVOS.getIP());
				strBuff.append("&INT_MOD_PORT=" + networkInterfaceModuleVOS.getPort());
				strBuff.append("&INT_MOD_CLASSNAME=" + networkInterfaceModuleVOS.getClassName());
				String requestStr = strBuff.toString();
		        final CommonClient commonClient = new CommonClient();
		        Map map = commonClient.process1(requestStr, channelTransferVO.getTransferID(), networkInterfaceModuleVOS.getCommunicationType(), networkInterfaceModuleVOS.getIP(), networkInterfaceModuleVOS.getPort(), networkInterfaceModuleVOS.getClassName());
		        if(map.get("TRANSACTION_STATUS")!=null && map.get("RESULT_CODE") !=null)
	            {
	            	if(map.get("RESULT_CODE").toString().equalsIgnoreCase(PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()))){
	            		retunMap.put("STATUS", PretupsI.SUCCESS);
	            	}else{
	            		retunMap.put("STATUS", PretupsI.FAIL);
	            		retunMap.put("VALIDATION_STATUS",map.get("RESULT_CODE"));
	            		StringBuffer strBuffnew =  new StringBuffer();
	            		strBuffnew.append("INTERFACE_ACTION=" + PretupsI.INTERFACE_CREDIT_ACTION);
	            		strBuffnew.append("&TRANSACTION_ID=" + channelTransferVO.getTransferID());
	            		strBuffnew.append("&INTERFACE_ID=" + transferItemVO.getInterfaceID());
	            		strBuffnew.append("&INTERFACE_HANDLER=" + transferItemVO.getInterfaceHandlerClass());
	            		strBuffnew.append("&INTERFACE_AMOUNT=" + PretupsBL.getDisplayAmount(channelTransferVO.getNetPayableAmount()));
	            		strBuffnew.append("&NETWORK_CODE=" + channelTransferVO.getNetworkCode());
	            		strBuffnew.append("&MSISDN=" + channelTransferVO.getToUserMsisdn());
	            		strBuffnew.append("&REFNO=" + channelTransferVO.getPayInstrumentNum());
	            		strBuffnew.append("&USER_TYPE=R");
	            		strBuffnew.append("&MODULE=" + PretupsI.INTERFACE_CATEGORY_PG);
	            		strBuffnew.append("&INT_MOD_COMM_TYPE=" + networkInterfaceModuleVOS.getCommunicationType());
	            		strBuffnew.append("&INT_MOD_IP=" + networkInterfaceModuleVOS.getIP());
	            		strBuffnew.append("&INT_MOD_PORT=" + networkInterfaceModuleVOS.getPort());
	            		strBuffnew.append("&ADJUST=Y" );
	            		strBuffnew.append("&INT_MOD_CLASSNAME=" + networkInterfaceModuleVOS.getClassName());
	            		map = commonClient.process1(requestStr, channelTransferVO.getTransferID(), networkInterfaceModuleVOS.getCommunicationType(), networkInterfaceModuleVOS.getIP(), networkInterfaceModuleVOS.getPort(), networkInterfaceModuleVOS.getClassName());
	            		if(map.get("TRANSACTION_STATUS")!=null && map.get("RESULT_CODE") !=null)
	                    {
	                    	if(!map.get("RESULT_CODE").toString().equalsIgnoreCase("1")){
	                    	 	retunMap.put("REVERSE_STATUS",map.get("RESULT_CODE"));
	                       }
	            		}else{
	                    	retunMap.put("STATUS", PretupsI.TXN_STATUS_AMBIGIOUS);
	                    }
	            	}
	            	
	           	}else{
	           		retunMap.put("STATUS", PretupsI.TXN_STATUS_AMBIGIOUS);
	           	}
			} catch (BTSLBaseException e) {
			    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "validatePaymentRefId[]", "", "", "", "Not able to Validate Ref IN:" + e.getMessage());
	       		retunMap.put("STATUS", PretupsI.TXN_STATUS_AMBIGIOUS);

			    e.printStackTrace();
			}catch (Exception e) {
			    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "validatePaymentRefId[]", "", "", "", "Not able to Validate Ref IN:" + e.getMessage());
	       		retunMap.put("STATUS", PretupsI.TXN_STATUS_AMBIGIOUS);
			    e.printStackTrace();
			}finally{
				if (mcomCon != null) {
					mcomCon.close("O2CTransferApprovalController#process");
					mcomCon = null;
				}
			}
			return retunMap;
		}

}

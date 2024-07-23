package com.client.pretups.util.clientutils;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;


import com.btsl.common.BTSLBaseException;
import com.btsl.common.IDGenerator;
import com.btsl.common.TypesI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.CryptoUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.ibm.icu.util.Calendar;

public class QvantelUtil extends OperatorUtil{

	private  final Log _log = LogFactory.getLog(this.getClass().getName());
    private long seed;
    private static String ENCKEY = null;
	public String formatVomsSerialnum(long p_counter, String p_activeproductid, String segment, String nwCode) {
        String returnStr = null;
        int maxserialnumlength = 12;// by default
        int year = 0;
        String strYear;
        String networkPrefix = "";
        int prefixLen = 0;
        final String methodName = "formatVomsSerialnum";
        Boolean isVomsNationalLocalPrefixEnable = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_NATIONAL_LOCAL_PREFIX_ENABLE);
        String nwNationalPrefix = (String) PreferenceCache.getSystemPreferenceValue(PreferenceI.NW_NATIONAL_PREFIX);
        Integer vomsSerialNoMaxLength = (Integer) PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MAX_LENGTH);
        try {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "segment=" + segment);
                _log.debug(methodName, "nwCode=" + nwCode);
            }
            
            maxserialnumlength = (int)vomsSerialNoMaxLength;
            if (maxserialnumlength == 0) {
                maxserialnumlength = 12;
            }
            if(isVomsNationalLocalPrefixEnable) {
	            if(VOMSI.VOUCHER_SEGMENT_NATIONAL.equals(segment)) {
	            	networkPrefix = nwNationalPrefix;
	            } else {
	            	networkPrefix = BTSLUtil.getPrefixCodeUsingNwCode(nwCode);            	
	            }
	            if(!BTSLUtil.isNullString(networkPrefix)) {
	            	prefixLen = networkPrefix.length();
	            }
	            if (_log.isDebugEnabled()) {
	                _log.debug(methodName, "networkPrefix=" + networkPrefix + " prefixLen = "+prefixLen);
	            }
            }
            String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(Long.toString(p_counter), maxserialnumlength - (4 + prefixLen));
            p_activeproductid = BTSLUtil.padZeroesToLeft(p_activeproductid, 3);
            Calendar currDate = BTSLDateUtil.getInstance();
            year = currDate.get(Calendar.YEAR);
            strYear = String.valueOf(year);
            strYear = strYear.substring(3, 4);
            returnStr = networkPrefix + strYear + p_activeproductid + paddedTransferIDStr;// 2+013+00000000001
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "formatVomsSerialnum[]", "", "", "", "Not able to generate Voms Serial num:" + e.getMessage());
            returnStr = null;
        }
        return returnStr;
    }
    

    
    public ArrayList generatePin(String location_code,String product_code,long totalCount,int seq)throws IOException
    {
            _log.debug("generatePin", "Entered "+location_code+"product_code"+product_code+"totalCount"+totalCount);
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
		//Added for VIL VMS 16 digit PIN , 1st digit of last of year + 15 digit random 
		String lastDigitofYear=strYear;
	    String seqID= Integer.toString(seq);
	    if(seqID.length()==1&&((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
        	seqID='0'+seqID;
        }
        
		_log.debug("generatePin","strYear"+strYear);
		int pValue=0;
		int pinLength=0;
		int randomDigitLength=0;
		int AValue=0;
		try{ pValue=Integer.parseInt(Constants.getProperty("RAND_PIN_GEN"));}catch(Exception e){pValue=46;}
		pinLength=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MAX_LENGTH))).intValue();
		if(pinLength==0)
			pinLength=16;
		
		 if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
			 pinLength = pinLength-seqID.length();
	       }
		
		 randomDigitLength = pinLength ;
		//long startTime=System.nanoTime();
		for (int Count= 0; Count <totalCount ; )
		{
			_log.debug("generatePin","Count="+Count);
			
		     
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

/*			Random objRandom =new Random();
			seed=generateSeed();
			objRandom.setSeed(seed);
			//pValue=46 for 14 Random
			BigInteger bigint = new BigInteger(pValue,objRandom);
			tmpPIN = bigint.longValue() ;
			actualPIN = String.valueOf(tmpPIN);*/
			if(AValue==0)
				{
				 _log.debug("generatePin", "AValue "+AValue);
			       
				//	System.out.println("Generating with 16 Digits");
				SecureRandom objRandom = new SecureRandom();
				seed=generateSeed();
				objRandom.setSeed(seed);
				BigInteger bigint = new BigInteger(pValue,objRandom);
  				tmpPIN = bigint.longValue(); 
					if (AValue>=2)
					AValue=0;
					else
					AValue++;
				}
				else if (AValue==1)
				{
					 _log.debug("generatePin", "AValue "+AValue);
					    
				//	System.out.println("Generating with 10+6 Digits");
					SecureRandom objRandom = new SecureRandom();
					seed=generateSeed();
					objRandom.setSeed(seed);
					tmpPIN=Long.valueOf(String.valueOf(objRandom.nextInt(999999999)+100000000)+String.valueOf(objRandom.nextInt(9999999)+1000000));
					if (AValue>=2)
						AValue=0;
						else
						AValue++;
				}
				else if(AValue==2)
				
				{
					 _log.debug("generatePin", "AValue "+AValue);
					    
				//	System.out.println("Generating with 5+5+6 Digits");
					SecureRandom objRandom = new SecureRandom();
					seed=generateSeed();
					objRandom.setSeed(seed);
				tmpPIN=Long.valueOf(String.valueOf(objRandom.nextInt(99999)+10000)+String.valueOf(objRandom.nextInt(999999)+100000)+String.valueOf(objRandom.nextInt(99999)+10000));
						if (AValue>=2)
						AValue=0;
						else
						AValue++;
				
				}
				else
				{

					SecureRandom objRandom = new SecureRandom();
					seed=generateSeed();
					objRandom.setSeed(seed);

					//pValue=46 for 14 Random
					BigInteger bigint = new BigInteger(pValue,objRandom);
					//System.out.println(" bigint " +bigint );
					
					tmpPIN = bigint.longValue(); 
					AValue=0;
				}
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
				//	actualPIN=lastDigitofYear+product_code+actualPIN;
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
		String ENCKEY = (Constants.KEY);
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
        int pValue = 0;
        try{ pValue=Integer.parseInt(Constants.getProperty("RAND_PIN_GEN"));}catch(Exception e){pValue=54;}
        BigInteger bigint = new BigInteger(pValue, objrandom);
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
	/**
	 * This method is used to check where current user is allowed to change CU status to EN status.
	 * @param currentUserMsisdn
	 * @return boolean
	 */
	public boolean canChangeConsumeVoucherStatus(String currentUserMsisdn)
	{
		String msidns=Constants.getProperty("VOUCHERCONSUMTIONALLOWEDFORUSER");
		
		_log.debug("canChangeConsumeVoucherStatus", msidns);
		_log.debug("canChangeConsumeVoucherStatus", currentUserMsisdn);
		if(!BTSLUtil.isNullString(msidns))
		{
			return msidns.contains(currentUserMsisdn);
		}
		return false;
	}
	
	public String formatVoucherExpiryChangeID(){
		String methodName = "formatVoucherExpiryChangeID";
		if(_log.isDebugEnabled()){
			_log.debug(methodName, "Entered");
		}
		StringBuffer batchID = new StringBuffer("V");
		String number = null;
		Date date = new Date();  
	    SimpleDateFormat formatter = new SimpleDateFormat("ddMMyy");  
	    String strDate= formatter.format(date); 
	    batchID.append(strDate);
	    batchID.append(".");
		try {
			number = String.valueOf(IDGenerator.getNextID(PretupsI.VOMS_EXPIRY_BATCH_ID, TypesI.ALL));
		} catch (BTSLBaseException e) {
			e.printStackTrace();
		}
		number = BTSLUtil.padZeroesToLeft(number, 4);
		batchID.append(number);
		if(_log.isDebugEnabled()){
			_log.debug(methodName, "Exiting with batchID = "+batchID.toString());
		}
		return batchID.toString();
	}
	
	public LinkedHashSet generatePinERP(LinkedHashSet pinsList, String batchID, long totalCount) throws IOException {
		{
			final String methodName = "generatePinERP";
			_log.error(methodName, PretupsI.ENTERED + " pinsList.size():" +pinsList.size() + 
					"batchID:"+batchID+" totalCount:"+totalCount);
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
			String ENCKEY = (Constants.KEY);
			LinkedHashSet newTreeset = new LinkedHashSet();

			//ArrayList tempArray = new ArrayList();
			try
			{
				int year=0;
				String strYear=null;
				java.util.Calendar currDate=java.util.Calendar.getInstance();
				year=currDate.get(Calendar.YEAR);
				_log.debug(methodName,"Year :"+year);
				strYear=String.valueOf(year);
				strYear=strYear.substring(3,4);
				//Added for VIL VMS 16 digit PIN , 1st digit of last of year + 15 digit random 
				String lastDigitofYear=strYear;


				_log.debug(methodName,"strYear"+strYear);
				int pValue=0;
				int pinLength=0;
				int randomDigitLength=0;
				int dayOfYear=0;
				int AValue=0;
				int AValueArr[] = new int[3];
				Arrays.fill(AValueArr,0);
				String dayOfYearStr=null;

				try{ pValue=Integer.parseInt(Constants.getProperty("RAND_PIN_GEN"));}catch(Exception e){pValue=46;}
				pinLength=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MAX_LENGTH))).intValue();
				if(pinLength==0)
					pinLength=16;

				dayOfYear = currDate.get(Calendar.DAY_OF_YEAR);
				dayOfYearStr = BTSLUtil.padZeroesToLeft(Integer.toString(dayOfYear), 3);

				// pinLength = pinLength - 1 - 3 - batchID.length(); 	// (>=5) = 1 + 3 + 1 (lastDigitofYear+dayOfYearStr+batchID)

				randomDigitLength = pinLength ;


				_log.error(methodName,"#dayOfYear:"+dayOfYear);
				_log.error(methodName,"#batchID:"+batchID);
				//long startTime=System.nanoTime();
				int duplicateCounter=0;
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

					/*Random objRandom =new Random();
					seed=generateSeed();
					objRandom.setSeed(seed);
					//pValue=46 for 14 Random
					BigInteger bigint = new BigInteger(pValue,objRandom);
					tmpPIN = bigint.longValue() ;
					actualPIN = String.valueOf(tmpPIN);*/
					
					//new
					
					
					if(AValue==0)
					{
					//	System.out.println("Generating with 16 Digits");
					SecureRandom objRandom = new SecureRandom();
					seed=generateSeed();
					objRandom.setSeed(seed);
					BigInteger bigint = new BigInteger(pValue,objRandom);
	  				tmpPIN = bigint.longValue(); 
						if (AValue>=2)
						AValue=0;
						else
						AValue++;
					}
					else if (AValue==1)
					{
					//	System.out.println("Generating with 10+6 Digits");
						SecureRandom objRandom = new SecureRandom();
						seed=generateSeed();
						objRandom.setSeed(seed);
//						tmpPIN=Long.valueOf(String.valueOf(objRandom.nextInt(999999999)+100000000)+String.valueOf(objRandom.nextInt(9999999)+1000000));
						tmpPIN=Long.valueOf(String.valueOf(objRandom.nextInt(99999999)+10000000)+String.valueOf(objRandom.nextInt(99999999)+10000000));
						if (AValue>=2)
							AValue=0;
							else
							AValue++;
					}
					else if(AValue==2)
					
					{
					//	System.out.println("Generating with 5+5+6 Digits");
						SecureRandom objRandom = new SecureRandom();
						seed=generateSeed();
						objRandom.setSeed(seed);
					tmpPIN=Long.valueOf(String.valueOf(objRandom.nextInt(99999)+10000)+String.valueOf(objRandom.nextInt(999999)+100000)+String.valueOf(objRandom.nextInt(99999)+10000));
							if (AValue>=2)
							AValue=0;
							else
							AValue++;
					
					}
					else
					{

						SecureRandom objRandom = new SecureRandom();
						seed=generateSeed();
						objRandom.setSeed(seed);

						//pValue=46 for 14 Random
						BigInteger bigint = new BigInteger(pValue,objRandom);
						//System.out.println(" bigint " +bigint );
						
						tmpPIN = bigint.longValue(); 
						AValue=0;
					}
				
					
					actualPIN = String.valueOf(tmpPIN);
					length = actualPIN.length();
					boolean flag=true;
					if (length < randomDigitLength)
					{
						actualPIN=this.padRandomToLeft(actualPIN, randomDigitLength);

						//					 actualPIN=lastDigitofYear+product_code+actualPIN;
//						actualPIN=lastDigitofYear+dayOfYearStr+batchID+actualPIN;

						if(flag){
							if(pinsList.contains(actualPIN))
							{
								flag=false;
								duplicateCounter++;
							}
							if(flag){
								AValueArr[AValue]++;
								pinsList.add(actualPIN);
								newTreeset.add(actualPIN);
								Count++;
							}
						}
					}
					else if(length==randomDigitLength)
					{
						if(flag){
							//					actualPIN=lastDigitofYear+product_code+actualPIN;
//							actualPIN=lastDigitofYear+dayOfYearStr+batchID+actualPIN;
							if(pinsList.contains(actualPIN))
							{
								flag=false;
								duplicateCounter++;
							}
							if(flag){
								AValueArr[AValue]++;
								pinsList.add(actualPIN);
								newTreeset.add(actualPIN);
								Count++;
							}
						}
					}
					else
					{
					}
					if(Count%1000 == 0) {
						_log.error(methodName, "actualPIN-" + actualPIN +" @@@@ duplicateCounter:" + duplicateCounter + " @@ Count:" 
								+ Count + " @@Distribution: " + Arrays.toString(AValueArr));
					}
				}
				_log.debug(methodName,"Pins Generated");
				//vPinArraylist=encryptList(newTreeset,ENCKEY);
				_log.error(methodName,"Pins Encripted newTreeset.size()"+newTreeset.size() +"Original List Size = "+pinsList.size());
				return newTreeset;
			}
			catch(Exception e)
			{
				_log.error(methodName,"System generated Error in pin generation :"+e);
			}
			return newTreeset;}
	}

	public ArrayList encryptListPublic(LinkedHashSet p_list,String p_key)
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
}

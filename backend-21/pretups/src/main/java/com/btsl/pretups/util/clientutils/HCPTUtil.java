package com.btsl.pretups.util.clientutils;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.CryptoUtil;
import com.ibm.icu.util.Calendar;

public class HCPTUtil extends OperatorUtil{

	private  final Log _log = LogFactory.getLog(this.getClass().getName());
    private long seed;
    private static String ENCKEY = null;
    
    public String formatVomsSerialnum(long p_counter, String p_activeproductid, String segment, String nwCode) {
	_log.debug("formatVomsSerialnum", "Entered "+"p_counter"+p_counter+"p_activeproductid"+p_activeproductid);
        String returnStr = null;
        int maxserialnumlength = 12;// by default
        int productIdLen = 0;
        final String METHOD_NAME = "formatVomsSerialnum";
        try {
            maxserialnumlength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MAX_LENGTH))).intValue();
            if (maxserialnumlength == 0) {
                maxserialnumlength = 12;
            }
            productIdLen = p_activeproductid.length();
            String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(Long.toString(p_counter), maxserialnumlength - productIdLen);
            p_activeproductid = BTSLUtil.padZeroesToLeft(p_activeproductid, 3);
            returnStr =  p_activeproductid + paddedTransferIDStr;// 2+013+00000000001
        } catch (Exception e) {
            _log.errorTrace(METHOD_NAME, e);
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
		try{ pValue=Integer.parseInt(Constants.getProperty("RAND_PIN_GEN"));}catch(Exception e){pValue=46;}
		pinLength=((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_PIN_MAX_LENGTH))).intValue();
		if(pinLength==0)
			pinLength=16;
		
		 if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.SEQUENCE_ID_ENABLE))).booleanValue()){
			 pinLength = pinLength-seqID.length()-product_code.length()-1  ;//1 for year 3 for product code 2 for sequence id
	       }
		 else {
			 pinLength=pinLength-product_code.length()-1;// reducing 1 for first digit year.
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
					 actualPIN=lastDigitofYear+seqID+actualPIN;
				 }
				 else {
					 actualPIN=lastDigitofYear+product_code+actualPIN;
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
					actualPIN=lastDigitofYear+product_code+actualPIN;
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
}

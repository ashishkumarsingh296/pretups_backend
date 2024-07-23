package com.client.pretups.util.clientutils;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Random;

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
import com.btsl.pretups.network.businesslogic.NetworkDAO;
import com.btsl.pretups.network.businesslogic.NetworkPrefixCache;
import com.btsl.pretups.network.businesslogic.NetworkPrefixVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.subscriber.businesslogic.ReceiverVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.pretups.util.OperatorUtil;
import com.btsl.pretups.util.PretupsBL;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.voms.util.VomsUtil;
import com.btsl.voms.vomscommon.VOMSI;
import com.ibm.icu.util.Calendar;

public class VILUtil extends OperatorUtil {
	private  final Log _log = LogFactory.getLog(this.getClass().getName());	
	private long seed;

	/* (non-Javadoc)
	 * @see com.btsl.pretups.util.OperatorUtil#getNetworkDetails(java.lang.String, java.lang.String)
	 * @author Ashish Gupta
	 * For VIL
	 */
	
	public NetworkPrefixVO getNetworkDetails(String p_filteredMSISDN, String p_subscriberType) throws BTSLBaseException {
        final String methodName = "getNetworkDetails";
        StringBuilder loggerValue= new StringBuilder();
        String msisdnPrefix = null;
        if (_log.isDebugEnabled()) {
        	loggerValue.setLength(0);
        	loggerValue.append("Entered: filteredMSISDN=");
        	loggerValue.append(p_filteredMSISDN);
        	loggerValue.append("p_subscriberType=");
        	loggerValue.append(p_subscriberType);
        	_log.debug(methodName, loggerValue);
        }
        NetworkPrefixVO networkPrefixVO = null;
        if(BTSLUtil.isNullString(p_filteredMSISDN))
    	{
        	if(SystemPreferences.SNDR_NETWORK_IDENTIFY_ON_IMSI_BASIS){
        		throw new BTSLBaseException("VILUtil",methodName, PretupsErrorCodesI.C2S_ERROR_MISSING_IMSI_INFORMATION);
        	}else{
        		throw new BTSLBaseException("VILUtil",methodName, PretupsErrorCodesI.C2S_ERROR_INVALID_SENDER_MSISDN);
        	}
    		 
    	}
        try {
            if(SystemPreferences.SNDR_NETWORK_IDENTIFY_ON_IMSI_BASIS){
            	msisdnPrefix = new NetworkDAO().getSeriesBasedOnIMSI(p_filteredMSISDN);
            	msisdnPrefix = msisdnPrefix.substring(0,msisdnPrefix.indexOf("_"));
            }else{
            	msisdnPrefix = PretupsBL.getMSISDNPrefix(p_filteredMSISDN);
            }
        	if (p_subscriberType.equals(PretupsI.USER_TYPE_SENDER)) {
                networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix);
            } else {
                networkPrefixVO = (NetworkPrefixVO) NetworkPrefixCache.getObject(msisdnPrefix, false);
            }

            if (networkPrefixVO == null && p_subscriberType.equals(PretupsI.USER_TYPE_SENDER)) {
                throw new BTSLBaseException("VILUtil", methodName, PretupsErrorCodesI.ERROR_NETWORK_NOTFOUND);
            } else if (networkPrefixVO == null && p_subscriberType.equals(PretupsI.USER_TYPE_RECEIVER)) {
                throw new BTSLBaseException("VILUtil", methodName, PretupsErrorCodesI.ERROR_REC_NETWORK_NOTFOUND, 0, new String[] { p_filteredMSISDN }, null);
            }
            return networkPrefixVO;
        } catch (BTSLBaseException be) {
            throw be;
        } catch (Exception e) {
        	loggerValue.setLength(0);
			loggerValue.append("EXCEPTION: ");
			loggerValue.append(e.getMessage());
			_log.error(methodName, loggerValue);
			_log.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "OpertorUtil[getNetworkDetails]", "", p_filteredMSISDN,
                "", loggerValue.toString());
            throw new BTSLBaseException("VILUtil", methodName, PretupsErrorCodesI.ERROR_EXCEPTION,e);
        } finally {
        	if (_log.isDebugEnabled()) {
             	loggerValue.setLength(0);
             	loggerValue.append("Exiting: networkPrefixVO:");
             	loggerValue.append(networkPrefixVO);
             	_log.debug(methodName, loggerValue);
             }
        }
    }
	
	public void validateVoucherPin(Connection p_con, RequestVO p_requestVO, TransferVO p_transferVO) throws BTSLBaseException, Exception {
        final String[] requestMessageArray = p_requestVO.getRequestMessageArray();
        final int i = 1;
        String receiverMSISDN = requestMessageArray[i];
        String senderMSISDN = p_requestVO.getRequestMSISDN();
        senderMSISDN = PretupsBL.getFilteredMSISDN(senderMSISDN);
        receiverMSISDN = addRemoveDigitsFromMSISDN(PretupsBL.getFilteredMSISDN(receiverMSISDN));

        if (!BTSLUtil.isValidMSISDN(receiverMSISDN)) {
            throw new BTSLBaseException(this, "checkAfterPaymentMethod", PretupsErrorCodesI.ERROR_INVALID_MSISDN, 0, new String[] { receiverMSISDN }, null);
        }
        final ReceiverVO _receiverVO = new ReceiverVO();
        if(!BTSLUtil.isNullString(p_requestVO.getReceiverMsisdn()) && !p_requestVO.getReceiverMsisdn().equals(receiverMSISDN))
        {
        	 _receiverVO.setMsisdn(p_requestVO.getReceiverMsisdn()); 	
        }else{
        	 _receiverVO.setMsisdn(receiverMSISDN);
        }
        NetworkPrefixVO networkPrefixVO = null;
        if(!BTSLUtil.isNullString(receiverMSISDN) && receiverMSISDN.equalsIgnoreCase(senderMSISDN) && SystemPreferences.SNDR_NETWORK_IDENTIFY_ON_IMSI_BASIS){
        	networkPrefixVO = getNetworkDetails(p_requestVO.getImsi(), PretupsI.USER_TYPE_RECEIVER);
        }else{
        	networkPrefixVO = PretupsBL.getNetworkDetails(receiverMSISDN, PretupsI.USER_TYPE_RECEIVER);
        }
        if (networkPrefixVO == null) {
            throw new BTSLBaseException(this, "checkAfterPaymentMethod", PretupsErrorCodesI.ERROR_NOTFOUND_RECEIVERNETWORK, 0, new String[] { receiverMSISDN }, null);
        }
        _receiverVO.setNetworkCode(networkPrefixVO.getNetworkCode());
        _receiverVO.setPrefixID(networkPrefixVO.getPrefixID());
        _receiverVO.setSubscriberType(networkPrefixVO.getSeriesType());
        p_transferVO.setReceiverVO(_receiverVO);
    }
	
	public String formatVoucherTransferID(TransferVO p_transferVO, long p_tempTransferID) {
    	final String methodName = "formatVoucherTransferID";
    	String returnStr = null;
    	try {
    		// ReceiverVO receiverVO=(ReceiverVO)p_transferVO.getReceiverVO()
    		// String currentYear=BTSLUtil.getFinancialYearLastDigits(2)
    		final String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(String.valueOf(p_tempTransferID), P2P_TRANSFER_ID_PAD_LENGTH);
    		ReceiverVO _receiverVO = (ReceiverVO) p_transferVO.getReceiverVO();
    		final String _networkCode = _receiverVO.getNetworkCode();
    		// returnStr=receiverVO.getNetworkCode()+"/"+currentYear+"/"+paddedTransferIDStr
    		// returnStr="C"+currentDateTimeFormatString(p_transferVO.getCreatedOn())+"."+currentTimeFormatString(p_transferVO.getCreatedOn())+"."+paddedTransferIDStr
    		returnStr = _networkCode+"V" + currentDateTimeFormatString(p_transferVO.getCreatedOn())+ currentTimeFormatString(p_transferVO.getCreatedOn())+Constants.getProperty("INSTANCE_ID")+ paddedTransferIDStr;
    		//returnStr = _networkCode+"V" + currentDateTimeFormatString(p_transferVO.getCreatedOn()) + "." + currentTimeFormatString(p_transferVO.getCreatedOn()) + "." + Constants.getProperty("INSTANCE_ID") + paddedTransferIDStr;
    		p_transferVO.setTransferID(returnStr);
    	} catch (Exception e) {
    		_log.errorTrace(methodName, e);
    		EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, "VILUtil[]", "", "", "",
    				"Not able to generate Transfer ID:" + e.getMessage());
    		returnStr = null;
    	}
    	return returnStr;
    }
	
	
	public String formatVomsSerialnum(long p_counter, String p_activeproductid, String segment, String nwCode) {
        String returnStr = null;
        int maxserialnumlength = 12;// by default
        int year = 0;
        String strYear;
        String networkPrefix = "";
        int prefixLen = 0;
        final String methodName = "formatVomsSerialnum";
        try {
            if (_log.isDebugEnabled()) {
                _log.debug(methodName, "segment=" + segment);
                _log.debug(methodName, "nwCode=" + nwCode);
            }

            maxserialnumlength = ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.VOMS_SERIAL_NO_MAX_LENGTH))).intValue();
            if (maxserialnumlength == 0) {
                maxserialnumlength = 12;
            }
            if(SystemPreferences.VOMS_NATIONAL_LOCAL_PREFIX_ENABLE) {
	            if(VOMSI.VOUCHER_SEGMENT_NATIONAL.equals(segment)) {
	            	networkPrefix = SystemPreferences.NW_NATIONAL_PREFIX;
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
            
            p_activeproductid = BTSLUtil.padZeroesToLeft(p_activeproductid, 3);
            Calendar currDate = BTSLDateUtil.getInstance();
            year = currDate.get(Calendar.YEAR);
            strYear = String.valueOf(year);
            strYear = strYear.substring(3, 4);
            String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(Long.toString(p_counter), maxserialnumlength - (strYear.length()+prefixLen));
            returnStr = networkPrefix + strYear+ paddedTransferIDStr;// 2+013+00000000001
        } catch (Exception e) {
            _log.errorTrace(methodName, e);
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
			 pinLength = pinLength-seqID.length()-1  ;//1 for year 3 for product code 2 for sequence id
	       }
		 else {
			 pinLength=pinLength-1;// reducing 1 for first digit year.
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
					 actualPIN=lastDigitofYear+actualPIN;
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
					actualPIN=lastDigitofYear+actualPIN;
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
	
	private ArrayList encryptList(LinkedHashSet p_list,String p_key)
	{
		ArrayList cipherList = new ArrayList();
		String plainText=null,cipherText;
		try
		{
                        Iterator itr=p_list.iterator();
                        int i=0;
                        while(itr.hasNext()){
                                plainText=(String)itr.next();
                                cipherText = VomsUtil.encryptText(plainText);
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
	
	
	public long calculateCardGroupTax1(String p_type, double p_rate, long p_requestValue) throws BTSLBaseException {
		double calculatedTax1Value = 0;
		if (p_type.equalsIgnoreCase(PretupsI.SYSTEM_AMOUNT)) {
			calculatedTax1Value = (long) p_rate;
		} else if (p_type.equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
			calculatedTax1Value = ((p_rate * p_requestValue) / (100 + p_rate));
			calculatedTax1Value = roundTo(calculatedTax1Value / 100, 2) * 100;
			calculatedTax1Value = Math.round(calculatedTax1Value * 100) / 100;
		
		//	calculatedTax1Value=Math.ceil(calculatedTax1Value);
		} else {
			if (_log.isDebugEnabled()) {
				_log.debug("calculateCardGroupTax1()", "Exception p_type is not define in the system p_type=" + p_type);
			}
			throw new BTSLBaseException(this, "calculateCardGroupTax1", "error.invalid.ratetype");
		}
		return (long)calculatedTax1Value;
	}
	/**
	 * Method calculateCardGroupTax2.
	 * 
	 * @param p_type
	 *            String
	 * @param p_rate
	 *            double
	 * @param p_requestValue
	 *            long
	 * @return long
	 * @throws BTSLBaseException
	 * @see com.btsl.pretups.util.OperatorUtilI#calculateCardGroupTax2(String,
	 *      double, long)
	 */
	public long calculateCardGroupTax2(String p_type, double p_rate, long p_requestValue) throws BTSLBaseException {
		double calculatedTax2Value = 0;
		if (p_type.equalsIgnoreCase(PretupsI.SYSTEM_AMOUNT)) {
			calculatedTax2Value = (long) p_rate;
		} else if (p_type.equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
			calculatedTax2Value = ((p_rate * p_requestValue) / (100 + p_rate));
			calculatedTax2Value = roundTo(calculatedTax2Value / 100, 2) * 100;
			calculatedTax2Value = Math.round(calculatedTax2Value * 100) / 100;
		//	calculatedTax2Value=Math.ceil(calculatedTax2Value);
		} else {
			if (_log.isDebugEnabled()) {
				_log.debug("calculateCardGroupTax2()", "Exception p_type is not define in the system p_type=" + p_type);
			}
			throw new BTSLBaseException(this, "calculateCardGroupTax2", "error.invalid.ratetype");
		}
		return (long)calculatedTax2Value;
	}
	
	/**
	 * Method calculateAccessFee.
	 * 
	 * @param p_accessFeeValue
	 *            double
	 * @param p_accessFeeType
	 *            String
	 * @param p_requestedValue
	 *            long
	 * @param p_minAccessFee
	 *            long
	 * @param p_maxAccessFee
	 *            long
	 * @return long
	 * @throws BTSLBaseException
	 * @see com.btsl.pretups.util.OperatorUtilI#calculateAccessFee(double,
	 *      String, long, long, long)
	 */
	public long calculateAccessFee(double p_accessFeeValue, String p_accessFeeType, long p_requestedValue,
			long p_minAccessFee, long p_maxAccessFee) throws BTSLBaseException {
		final String methodName = "calculateAccessFee";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered with p_accessFeeValue=" + p_accessFeeValue + " p_accessFeeType="
					+ p_accessFeeType + " p_minAccessFee=" + p_minAccessFee + " p_minAccessFee=" + p_minAccessFee);
		}
		double calculatedAccessFee = 0;
		try {
			if (p_accessFeeType.equalsIgnoreCase(PretupsI.SYSTEM_AMOUNT)) {
				calculatedAccessFee = (long) p_accessFeeValue;
			} else if (p_accessFeeType.equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
				// calculatedAccessFee=(long)((p_accessFeeValue*p_requestedValue)/(100+p_accessFeeValue));
				calculatedAccessFee = ((p_accessFeeValue * p_requestedValue) / (100));

				calculatedAccessFee = roundTo(calculatedAccessFee / 100, 2) * 100;
				calculatedAccessFee = Math.round(calculatedAccessFee * 100) / 100;
				//	calculatedAccessFee=Math.ceil(calculatedAccessFee);
			} else {
				if (_log.isDebugEnabled()) {
					_log.debug("calculateAccessFee()",
							"Exception p_accessFeeType is not define in the system p_accessFeeType=" + p_accessFeeType);
				}
				throw new BTSLBaseException(this, methodName, "error.invalid.ratetype");
			}
			
			if (calculatedAccessFee < p_minAccessFee) {
				calculatedAccessFee = p_minAccessFee;
			} else if (calculatedAccessFee > p_maxAccessFee) {
				calculatedAccessFee = p_maxAccessFee;
			}
			
		} catch (Exception e) {
			_log.errorTrace(methodName, e);
			EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
					"CardGroupBL[calculateAccessFee]", "", "", " ",
					"Not able to calculate the access fee applicable getting Exception=" + e.getMessage());
			throw new BTSLBaseException("OperatorUtil", methodName, PretupsErrorCodesI.C2S_ERROR_EXCEPTION);
		}
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting with calculatedAccessFee=" + calculatedAccessFee);
		}
		return (long)calculatedAccessFee;
	}
	/**
	 * Method calculateCardGroupBonus.
	 * 
	 * @param p_type
	 *            String
	 * @param p_rate
	 *            double
	 * @param p_requestValue
	 *            long
	 * @return long
	 * @throws BTSLBaseException
	 * @see com.btsl.pretups.util.OperatorUtilI#calculateCardGroupBonus(String,
	 *      double, long)
	 */
	public long calculateCardGroupBonus(String p_type, double p_rate, long p_requestValue) throws BTSLBaseException {
		double calculatedBonusValue = 0;
		if (p_type.equalsIgnoreCase(PretupsI.SYSTEM_AMOUNT)) {
			calculatedBonusValue = (long) p_rate;
		} else if (p_type.equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
			calculatedBonusValue = ((p_requestValue * ((p_rate) / 100)));
			calculatedBonusValue=roundTo(calculatedBonusValue/100, 2)*100;
		//	calculatedBonusValue=Math.ceil(calculatedBonusValue);
		} else {
			if (_log.isDebugEnabled()) {
				_log
						.debug("calculateCardGroupBonus()", "Exception p_type is not define in the system p_type="
								+ p_type);
			}
			throw new BTSLBaseException(this, "calculateCardGroupBonus", "error.invalid.ratetype");
		}
		return (long)calculatedBonusValue;
	}
	/**
	 * Round the decimal number to required decimal places
	 * Creation date: (28/11/01 3:21:03 PM)
	 * @return java.lang.String
	 * @param number double
	 * @param afterDecimal int
	 */
	public static double roundTo(double number, int afterDecimal)
	{
		double result = 0.0;
		try
		{
			String formatStr = "##########0.0";
			for(int i=1; i<afterDecimal; i++) formatStr += "#";
			DecimalFormat decFormat = new DecimalFormat(formatStr,new DecimalFormatSymbols(Locale.ENGLISH));//comment by abhay
			String tempNum = decFormat.format(number);
			//System.out.println("BTSLUtil inside roundTo tempNum:"+tempNum);
			result = Double.parseDouble(tempNum);
			//System.out.println("BTSLUtil inside roundTo result:"+result);
		}
		catch(Exception ex){System.out.println("BTSLUtil roundTO Exception ex="+ex.getMessage());}
		return result;
	}
	public long calculateAmount( long p_requestValue1,long p_requestValue2){
		long calculatedAmount = 0;
		calculatedAmount=p_requestValue1-p_requestValue2;
		return calculatedAmount;
	}
	public  double calculateCardGroupTaxDouble(String p_type, double p_rate, long p_requestValue) throws BTSLBaseException {
		double calculatedTax1Value = 0;
		if (p_type.equalsIgnoreCase(PretupsI.SYSTEM_AMOUNT)) {
			calculatedTax1Value =p_rate;
		}
		else if (p_type.equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
			calculatedTax1Value = p_requestValue * (p_rate / (100 + p_rate));
		}
		else {
            if (_log.isDebugEnabled()) {
                    _log.debug("calculateCardGroupTaxDouble()", "Exception p_type is not define in the system p_type="+ p_type);
            }
            throw new BTSLBaseException(this, "calculateCardGroupTaxDouble", "error.invalid.ratetype");
    }
		return calculatedTax1Value;
	}
	public  double calculateAmount(double p_requestValue1, double p_requestValue2) {
		double calculatedAmount = 0;
		calculatedAmount = p_requestValue1 - p_requestValue2;

		// calculatedAmount = Math.round(calculatedAmount * 100) / 100;

		return calculatedAmount;
	}
	public long calculateCardGroupBonusForDouble(String p_type, double p_rate, double p_requestValue) throws BTSLBaseException {
        double calculatedBonusValue = 0;
     if (p_type.equalsIgnoreCase(PretupsI.SYSTEM_AMOUNT)) {
             calculatedBonusValue = (long) p_rate;
     } else if (p_type.equalsIgnoreCase(PretupsI.AMOUNT_TYPE_PERCENTAGE)) {
             calculatedBonusValue = ((p_requestValue * p_rate) / 100);
             calculatedBonusValue=roundTo(calculatedBonusValue/100, 2)*100;
             calculatedBonusValue = Math.round(calculatedBonusValue * 100) / 100;
     //      calculatedBonusValue=Math.ceil(calculatedBonusValue);
     } else {
             if (_log.isDebugEnabled()) {
                     _log.debug("calculateCardGroupBonus()", "Exception p_type is not define in the system p_type="+ p_type);
             }
             throw new BTSLBaseException(this, "calculateCardGroupBonus", "error.invalid.ratetype");
     }
     return (long)calculatedBonusValue;
}
}

package com.btsl.user.businesslogic;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.util.BTSLUtil;
import com.ibm.icu.util.Calendar;

@Component("OperatorUtil")
public class OperatorUtil implements OperatorUtilI {

    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(OperatorUtil.class);

    @Autowired
    private VMSCacheRepository vmsCacheRepository;
    private long seed;
    private SecureRandom random = new SecureRandom();

    // private static String ENCKEY = null;

    @Override
    public String formatVomsSerialnum(long pcounter, String pactiveproductid, String segment, String nwCode) {
        LOGGER.debug(
                MessageFormat.format(
                        "Serial No count :: {0} , activeProductid :: {1}  , Segment :: {2} , new code :: {3}", pcounter,
                        pactiveproductid, segment, nwCode));
        final String methodName = "formatVomsSerialnum";
        String returnStr = null;
        int maxserialnumlength = NumberConstants.N12.getIntValue();// by default
        int year = 0;
        String strYear;
        String networkPrefix = "";
        int prefixLen = 0;
        try {
            LOGGER.debug( MessageFormat.format("segment= {0}", segment));
            LOGGER.debug( MessageFormat.format("nwCode= {0}", nwCode));
            String maxserlen = vmsCacheRepository
                    .getSystemPreferenceValue(SystemPreferenceConstants.VOMS_SERIAL_NO_MAX_LENGTH.getType());
            if (maxserlen != null) {
                maxserialnumlength = Integer.parseInt(vmsCacheRepository
                        .getSystemPreferenceValue(SystemPreferenceConstants.VOMS_SERIAL_NO_MAX_LENGTH.getType()));
            }
            if (maxserialnumlength == 0) {
                maxserialnumlength = NumberConstants.N12.getIntValue();
            }
            boolean vomsNationLocPreEnable = Boolean.valueOf(vmsCacheRepository
                    .getSystemPreferenceValue(SystemPreferenceConstants.VOMS_NATIONAL_LOCAL_PREFIX_ENABLE.getType()));

            if (vomsNationLocPreEnable) {
                if (Constants.VOUCHER_SEGMENT_NATIONAL.getStrValue().equals(segment)) {
                    networkPrefix = vmsCacheRepository
                            .getSystemPreferenceValue(SystemPreferenceConstants.NW_NATIONAL_PREFIX.getType());
                } else {
                    networkPrefix = BTSLUtil.getPrefixCodeUsingNwCode(nwCode);
                }
                if (!BTSLUtil.isNullString(networkPrefix)) {
                    prefixLen = networkPrefix.length();
                }

                LOGGER.debug(
                        MessageFormat.format("networkPrefix= {0}  prefixLen = {1} ", networkPrefix, prefixLen));

            }
            String paddedTransferIDStr = BTSLUtil.padZeroesToLeft(Long.toString(pcounter),
                    maxserialnumlength - (NumberConstants.FOUR.getIntValue() + prefixLen));
            pactiveproductid = BTSLUtil.padZeroesToLeft(pactiveproductid, NumberConstants.THREE.getIntValue());
            Calendar currDate = BTSLDateUtil.getInstance();
            year = currDate.get(Calendar.YEAR);
            strYear = String.valueOf(year);
            strYear = strYear.substring(NumberConstants.THREE.getIntValue(), NumberConstants.FOUR.getIntValue());
            returnStr = networkPrefix + strYear + pactiveproductid + paddedTransferIDStr;
        } catch (Exception e) {
            LOGGER.trace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                    "formatVomsSerialnum[]", "", "", "", "Not able to generate Voms Serial num:" + e.getMessage());
            returnStr = null;
        }
        return returnStr;

    }

    @SuppressWarnings("rawtypes")
    @Override
    public List generatePin(String locationCode, String productCode, long totalCount, Integer seq) {
        LOGGER.info("generatePin Entered " + locationCode + "product_code" + "totalCount" + totalCount);

        ArrayList vPinArraylist = new ArrayList();
        LinkedHashSet treesetAll = new LinkedHashSet();

        try {
            int year = 0;
            String strYear = null;
            java.util.Calendar currDate = java.util.Calendar.getInstance();
            year = currDate.get(Calendar.YEAR);
            LOGGER.debug(MessageFormat.format("generatePin Year : {0}", year));

            strYear = String.valueOf(year);
            strYear = strYear.substring(NumberConstants.THREE.getIntValue(), NumberConstants.FOUR.getIntValue());
            if (seq == null) {
                seq = 0;
            }
            String seqID = Integer.toString(seq);
            boolean seqidEnable = Boolean.valueOf(vmsCacheRepository
                    .getSystemPreferenceValue(SystemPreferenceConstants.SEQUENCE_ID_ENABLE.getType()));
            LOGGER.debug("SequenceID Enable : " + seqidEnable);

            LOGGER.debug(MessageFormat.format("generatePin Sequence ID enabled :: {0}", seqidEnable));
            if (seqID.length() == 1 && seqidEnable) {
                seqID = '0' + seqID;
            }
            LOGGER.debug(MessageFormat.format("generatePin strYear :: {0}", strYear));
            int pinLength;
			int randomDigitLength;
			int pValue = getPValue();
            pinLength = getPinLength(seqID, seqidEnable);
            randomDigitLength = pinLength;
            LOGGER.debug(MessageFormat.format("generatePin random Pin length :: {0}", randomDigitLength));
            LOGGER.debug(MessageFormat.format("generatePin Forloop :: total {0}", totalCount));
            createPin(totalCount, treesetAll, seqID, seqidEnable, randomDigitLength, pValue);
            LOGGER.debug("generatePin :: Pins Generated");
            vPinArraylist = encryptList(treesetAll);
            LOGGER.debug("generatePin :: Pins Encripted size ::" + vPinArraylist.size());
            return vPinArraylist;

        } catch (Exception ex) {
            LOGGER.error("generatePin System generated Error in pin generation :", ex);
        }

        return vPinArraylist;
    }
    
    private int getPValue() {
		int pValue = 0;
		try {
		    pValue = Integer.parseInt(com.btsl.util.Constants.getProperty("RAND_PIN_GEN"));
		} catch (RuntimeException e) {
		    pValue = NumberConstants.N46.getIntValue();
		    LOGGER.debug("Changing Pvalue incase of exception ", e);
		}
		return pValue;
	}
    
	private int getPinLength(String seqID, boolean seqidEnable) {
		int pinLength;
		pinLength = Integer.parseInt(vmsCacheRepository
		        .getSystemPreferenceValue(SystemPreferenceConstants.VOMS_PIN_MAX_LENGTH.getType()));
		LOGGER.debug(MessageFormat.format("generatePin Pin length :: {0}", pinLength));
		if (pinLength == 0)
		    pinLength = NumberConstants.N16.getIntValue();
		if (seqidEnable) {
		    pinLength = pinLength - seqID.length();// 1 for year 3 for
		                                           // product code 2 for
		                                           // sequence id
		}
		return pinLength;
	}

	private void createPin(long totalCount, LinkedHashSet treesetAll, String seqID, boolean seqidEnable,
			int randomDigitLength, int pValue) {
		long tmpPIN;
		int length;
		String actualPIN;
		for (int count = 0; count < totalCount;) {
		    seed = generateSeed();
		    random.setSeed(seed);

		    // pValue=46 for 14 Random
		    BigInteger bigint = getBigIntegerRandomNumber(pValue);
		    //BigInteger bigint = new BigInteger(pValue, random);
		    tmpPIN = bigint.longValue();
		    actualPIN = String.valueOf(tmpPIN);
		    length = actualPIN.length();
		    boolean flag = true;
		    // LOGGER.debug(MessageFormat.format("generatePin Actual Pin ::
		    // {0}", actualPIN));

		    if (length < randomDigitLength) {
		        count = checkAndPadActualPin(actualPIN, treesetAll, seqID, seqidEnable, randomDigitLength, count,
						flag);
		    } else if (length == randomDigitLength) {
		        count = checkActualPinExists(actualPIN, treesetAll, count, flag);
		    }
		}
	}

	private BigInteger getBigIntegerRandomNumber(int pValue) {
		BigInteger bigint = new BigInteger(pValue, random);
		return bigint;
	}

	private int checkAndPadActualPin(String actualPIN, LinkedHashSet treesetAll, String seqID, boolean seqidEnable,
			int randomDigitLength, int count, boolean flag) {
		// LOGGER.debug(MessageFormat.format("generatePin :: length
		// {0} -> randomDigitLength {1}",
		// length,randomDigitLength));
		actualPIN = this.padRandomToLeft(actualPIN, randomDigitLength,random);

		if (seqidEnable) {
		    actualPIN = seqID + actualPIN;
		}
		// LOGGER.debug(MessageFormat.format("generatePin ::
		// actualPin {0}", actualPIN));

		if (flag) {
		    if (treesetAll.contains(actualPIN)) {
		        flag = false;
		    }
		    if (flag) {
		        treesetAll.add(actualPIN);
		        count++;
		        if (count % NumberConstants.N200.getIntValue() == 0) {
		            LOGGER.debug(MessageFormat.format("generatePin Total pins generated : {0}", count));
		        }
		    }
		}
		return count;
	}
	
	private int checkActualPinExists(String actualPIN, LinkedHashSet treesetAll, int count, boolean flag) {
		if (flag) {

		    if (treesetAll.contains(actualPIN)) {

		        flag = false;
		    }
		    if (flag) {
		        treesetAll.add(actualPIN);
		        count++;
		    }
		}
		return count;
	}

    private ArrayList encryptList(LinkedHashSet p_list) {
        ArrayList cipherList = new ArrayList();
        String plainText = null, cipherText;
        String algoType = null;
        try {

            algoType = vmsCacheRepository
                    .getSystemPreferenceValue(SystemPreferenceConstants.VMSPIN_EN_DE_CRYPTION_TYPE.getType());
            cipherList=CommonUtils.encryptionList(p_list,algoType);
        } catch (RuntimeException e) {
            LOGGER.trace("generatePin System generated Error in pin generation :", e);
        }

        return cipherList;
    }

 
	private long generateSeed() {
        SecureRandom objrandom = new SecureRandom();
        BigInteger bigint = new BigInteger(NumberConstants.N1024.getIntValue(), objrandom);
        Date date = new Date();
        seed = date.getTime() + bigint.longValue();
        return seed;
    }

    private String padRandomToLeft(String p_strValue, int p_strLength,SecureRandom r) {
    	StringBuilder sb = new StringBuilder(p_strValue);
        int cntr = p_strLength - p_strValue.length();
        if (cntr > 0) {
            for (int i = 0; i < cntr; i++) {
                int j = r.nextInt(NumberConstants.NINE.getIntValue());
                sb.insert(0, j);
            }
        }
        return sb.toString();
    }

    /**
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * /**
     * 
     * This method will convert system specific msisdn to operater specific
     * msisdn
     * 
     * @param p_msisdn
     * @return
     */
    @Override
    public String getOperatorFilteredMSISDN(String msisdn) {
        return com.btsl.util.Constants.getProperty("COUNTRY_CODE") + msisdn;
    }

    /**
     * Date : Jul 23, 2007 Discription : Method : validateTransactionPassword
     * 
     * @param pchannelUserVO
     * @param ppassword
     * @throws BTSLBaseException
     * @return void
     * @author ved.sharma
     */
    @Override
    public boolean validateTransactionPassword(ChannelUserVO pchannelUserVO, String ppassword) {
        LOGGER.debug( MessageFormat.format(" Entered pchannelUserVO=:{0} pawd={1}", pchannelUserVO,ppassword));
        boolean passwordValidation = true;
        if (!CommonUtils.isNullorEmpty(pchannelUserVO)) {
            String pinpasCryption = vmsCacheRepository
                    .getSystemPreferenceValue(SystemPreferenceConstants.PINPAS_EN_DE_CRYPTION_TYPE.getType());
            int passwordLength = Integer.valueOf(vmsCacheRepository
                    .getSystemPreferenceValue(SystemPreferenceConstants.MAX_LOGIN_PWD_LENGTH.getType()));
            if ("SHA".equalsIgnoreCase(pinpasCryption)) {
                boolean checkpassword =checkPasswordValidation(ppassword,passwordLength,pchannelUserVO);
                if (!BTSLUtil.isNullString(pchannelUserVO.getPword()) && (!checkpassword)) {
                    passwordValidation = false;
                }
            } else {
                if (!BTSLUtil.isNullString(pchannelUserVO.getPword()) && (PretupsI.FALSE
                        .equalsIgnoreCase(BTSLUtil.compareHash2String(pchannelUserVO.getPword(), ppassword)))) {
                    passwordValidation = false;
                }
            }

        } else {
            throw new ValidationException("exception", MessageCodes.ERROR_EXCEPTION_ERROR_1999.getStrValue());
        }

        return passwordValidation;
    }





private  boolean checkPasswordValidation(String pPassword ,int passwordLength,ChannelUserVO pchannelUserVO) {
	boolean checkpassword;
	 if (pPassword.length() > passwordLength) {
         checkpassword = BTSLUtil.decryptText(pchannelUserVO.getPword()).equals(pPassword);
     } else {
         checkpassword = (!PretupsI.FALSE
                 .equalsIgnoreCase(BTSLUtil.compareHash2String(pchannelUserVO.getPword(), pPassword)));
     }
	 return checkpassword;
}


/**
 * To check the period after which the created password (on first time
 * creation) will be expired.
 */
@Override
public boolean checkPasswordPeriodToResetAfterCreation(Date modifiedOn, ChannelUserVO channelUserVO) {
    if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("checkPasswordPeriodToResetAfterCreation", "Entered with _categoryCode:" + channelUserVO.getCategoryCode() + " , _networkId =" + channelUserVO
            .getNetworkId() + " modifiedDate=" + modifiedOn + ", _loginTime=" + channelUserVO.getLoginTime());
    }
    boolean passwordResetFlag = false;
    Date passwordExpiredTime = null;
    final Calendar cal = Calendar.getInstance();
    cal.setTime(modifiedOn);
    final int passwordExpiredInHours = Integer.parseInt(vmsCacheRepository.getControlPreferenceValue(channelUserVO.getCategoryCode(), channelUserVO.getNetworkId(),
    		SystemPreferenceConstants.PSWD_EXP_TIME_IN_HOUR_AFTER_CREATION.getType()));
    cal.add(Calendar.HOUR, passwordExpiredInHours);
    passwordExpiredTime = cal.getTime();
    if (passwordExpiredInHours == 0) {
        passwordResetFlag = false;
    } else if ("Y".equals(channelUserVO.getPswdReset()) && channelUserVO.getLoginTime().after(passwordExpiredTime)) {
        passwordResetFlag = true;
    }
    if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("checkPasswordPeriodToResetAfterCreation", "Exited with passwordResetFlag:" + passwordResetFlag);
    }

    return passwordResetFlag;
}
}

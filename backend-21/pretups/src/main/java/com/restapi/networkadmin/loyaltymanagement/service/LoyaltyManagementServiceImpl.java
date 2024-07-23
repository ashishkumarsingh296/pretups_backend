package com.restapi.networkadmin.loyaltymanagement.service;

import com.btsl.common.*;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.ActivationProfileCombinedLMSVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.ProfileSetDetailsLMSVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.ProfileSetLMSVO;
import com.btsl.pretups.loyaltymgmt.businesslogic.ProfileSetVersionLMSVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.networkadmin.loyaltymanagement.requestVO.*;
import com.restapi.networkadmin.loyaltymanagement.responseVO.*;
import com.restapi.networkadmin.loyaltymanagement.serviceI.LoyaltyManagementServiceI;
import com.restapi.networkadmin.loyaltymanagement.requestVO.SuspendRequestVO;
import com.web.pretups.loyaltymgmt.businesslogic.ActivationBonusLMSWebDAO;
import org.apache.commons.httpclient.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LoyaltyManagementServiceImpl implements LoyaltyManagementServiceI {
    public static final Log LOG = LogFactory.getLog(LoyaltyManagementServiceImpl.class.getName());
    public static final String CLASS_NAME = "LoyalityManagementServiceImpl";

    @Override
    public ProfileDetailsVersionsResponseVO loadProfileDetailsVersionsList(Connection con, UserVO userVO, String promotionType, String status, String applicableFrom, String applicableTo, ProfileDetailsVersionsResponseVO response) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "loadProfileDetailsVersionsList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        String format = Constants.getProperty("LMS_PROFILE_DATE_FORMAT");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(format);

        List promotionTypeList = new ArrayList<>();
        promotionTypeList.add(PretupsI.ALL);
        ArrayList<ListValueVO> promotypeFromLookupList = LookupsCache.loadLookupDropDown(PretupsI.LMS_PROMOTION_TYPE, true);
        for (ListValueVO valueVO : promotypeFromLookupList) {
            promotionTypeList.add(valueVO.getValue());
        }

        List statusList = new ArrayList<>();
        statusList.add(PretupsI.ALL);
        statusList.add(PretupsI.YES);
        statusList.add(PretupsI.STATUS_NEW);
        statusList.add(PretupsI.SUSPEND);
        statusList.add(PretupsI.RES_MSISDN_STATUS_REJECT);
        if (promotionType == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.PROMOTIONAL_TYPE_IS_NULL);
        }
        if (BTSLUtil.isEmpty(promotionType)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.PROMOTIONAL_TYPE_IS_EMPTY);
        }
        if (status == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.STATUS_IS_NULL);
        }
        if (BTSLUtil.isEmpty(status)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.STATUS_FIELD_IS_EMPTY);
        }
        if (!promotionTypeList.contains(promotionType)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.PROMOTIONAL_TYPE_IS_INVALID);
        }
        if (!statusList.contains(status)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.STATUS_IS_INVALID);
        }
        if (!BTSLUtil.isEmpty(applicableFrom)) {
            try {
                LocalDate.parse(applicableFrom, DateTimeFormatter.ofPattern(format));
            } catch (Exception ex) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.APPLICABLE_FROM_INVALID_FORMAT);
            }
        }
        if (!BTSLUtil.isEmpty(applicableTo)) {
            try {
                LocalDate.parse(applicableTo, DateTimeFormatter.ofPattern(format));
            } catch (Exception ex) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.APPLICABLE_TO_INVALID_FORMAT);
            }

        }
        if (!BTSLUtil.isEmpty(applicableFrom) && !BTSLUtil.isEmpty(applicableTo)) {
            Date applicableFromDate = outputDateFormat.parse(applicableFrom);
            Date applicableToDate = outputDateFormat.parse(applicableTo);
            if (!(applicableToDate.toInstant()).isAfter(applicableFromDate.toInstant())) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.APPLICABLE_FROM_DATE_AND_TIME_MUST_BE_LESS_THAN_APPLICABLE_TILL_DATE_AND_TIME);
            }
        }
        if ((!BTSLUtil.isEmpty(applicableFrom) && BTSLUtil.isEmpty(applicableTo)) || (BTSLUtil.isEmpty(applicableFrom) && !BTSLUtil.isEmpty(applicableTo))) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.APPLICABLE_FROM_AND_APPLICABLE_TO_REQUIRED);
        }
        ActivationBonusLMSWebDAO activationBonusLMSWebDAO = new ActivationBonusLMSWebDAO();
        SimpleDateFormat inputDateTimeDateFormat = new SimpleDateFormat(PretupsI.TIMESTAMP_FORMAT);
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

        ArrayList<ProfileSetLMSVO> profileList = null;
        if (!promotionType.equals(PretupsI.ALL) && !status.equals(PretupsI.ALL)) {
            profileList = activationBonusLMSWebDAO.loadProfileDetailsVersionList(con, userVO.getNetworkID(), promotionType, status, new ArrayList());
        } else if (promotionType.equals(PretupsI.ALL) && !status.equals(PretupsI.ALL)) {
            profileList = activationBonusLMSWebDAO.loadProfileDetailsVersionList(con, userVO.getNetworkID(), null, status, new ArrayList());
        } else if (!promotionType.equals(PretupsI.ALL) && status.equals(PretupsI.ALL)) {
            profileList = activationBonusLMSWebDAO.loadProfileDetailsVersionList(con, userVO.getNetworkID(), promotionType, null, new ArrayList());
        } else if (promotionType.equals(PretupsI.ALL) && status.equals(PretupsI.ALL)) {
            profileList = activationBonusLMSWebDAO.loadProfileDetailsVersionList(con, userVO.getNetworkID(), null, null, new ArrayList());
        }
        List<ProfileDetailsSet> profileDetailsSetList = new ArrayList<>();
        if ((BTSLUtil.isEmpty(applicableFrom) && BTSLUtil.isEmpty(applicableTo))) {
            for (ProfileSetLMSVO profileSetLMSVO : profileList) {

                ProfileDetailsSet profileDetailsSet = new ProfileDetailsSet();
                profileDetailsSet.setProfileName(profileSetLMSVO.getSetName());
                profileDetailsSet.setSetId(profileSetLMSVO.getSetId());
                profileDetailsSet.setStatus(profileSetLMSVO.getStatus());
                profileDetailsSet.setVersion(profileSetLMSVO.getLastVersion());
                profileDetailsSet.setApplicableFrom(outputDateFormat.format(inputDateTimeDateFormat.parse(profileSetLMSVO.getApplicableFromDate())));
                profileDetailsSet.setApplicableTo(outputDateFormat.format(inputDateTimeDateFormat.parse(profileSetLMSVO.getApplicableToDate())));
                profileDetailsSet.setShortCode(profileSetLMSVO.getShortCode());
                profileDetailsSet.setMessageConfig(profileSetLMSVO.getMsgConfEnableFlag());
                profileDetailsSet.setMessageConfigDes((profileSetLMSVO.getMsgConfEnableFlag() != null && profileSetLMSVO.getMsgConfEnableFlag().equalsIgnoreCase(PretupsI.YES))?PretupsI.YES_DES:PretupsI.NO_DES);
                for (ListValueVO valueVO : promotypeFromLookupList) {
                    if (valueVO.getValue().equals(profileSetLMSVO.getPromotionType())) {
                        profileDetailsSet.setPromotionTypeDesc(valueVO.getLabel());
                    }
                }
                profileDetailsSet.setPromotionType(profileSetLMSVO.getPromotionType());
                profileDetailsSetList.add(profileDetailsSet);
            }
            response.setProfileDetailsSetList(profileDetailsSetList);
        } else if (!BTSLUtil.isEmpty(applicableFrom) && !BTSLUtil.isEmpty(applicableTo)) {

            for (ProfileSetLMSVO profileSetLMSVO : profileList) {
                outputDateFormat.parse(applicableFrom);
                Date formattedFromDate = outputDateFormat.parse(outputDateFormat.format(inputDateTimeDateFormat.parse(profileSetLMSVO.getApplicableFromDate())));
                Date formattedToDate = outputDateFormat.parse(outputDateFormat.format(inputDateTimeDateFormat.parse(profileSetLMSVO.getApplicableToDate())));
                if ((formattedFromDate.toInstant().isAfter(outputDateFormat.parse(applicableFrom).toInstant())) && ((formattedToDate.toInstant()).isBefore(outputDateFormat.parse(applicableTo).toInstant()))) {
                    ProfileDetailsSet profileDetailsSet = new ProfileDetailsSet();
                    profileDetailsSet.setProfileName(profileSetLMSVO.getSetName());
                    profileDetailsSet.setSetId(profileSetLMSVO.getSetId());
                    profileDetailsSet.setStatus(profileSetLMSVO.getStatus());
                    profileDetailsSet.setVersion(profileSetLMSVO.getLastVersion());
                    profileDetailsSet.setApplicableFrom(outputDateFormat.format(inputDateTimeDateFormat.parse(profileSetLMSVO.getApplicableFromDate())));
                    profileDetailsSet.setApplicableTo(outputDateFormat.format(inputDateTimeDateFormat.parse(profileSetLMSVO.getApplicableToDate())));
                    profileDetailsSet.setShortCode(profileSetLMSVO.getShortCode());
                    profileDetailsSet.setPromotionType(profileSetLMSVO.getPromotionType());
                    profileDetailsSet.setMessageConfig(profileSetLMSVO.getMsgConfEnableFlag());
                    profileDetailsSet.setMessageConfigDes((profileSetLMSVO.getMsgConfEnableFlag() != null && profileSetLMSVO.getMsgConfEnableFlag().equalsIgnoreCase(PretupsI.YES))?PretupsI.YES_DES:PretupsI.NO_DES);
                    for (ListValueVO valueVO : promotypeFromLookupList) {
                        if (valueVO.getValue().equals(profileSetLMSVO.getPromotionType())) {
                            profileDetailsSet.setPromotionTypeDesc(valueVO.getLabel());
                        }

                    }
                    profileDetailsSetList.add(profileDetailsSet);
                }
            }
            response.setProfileDetailsSetList(profileDetailsSetList);
        }
        response.setStatus((HttpStatus.SC_OK));
        String resmsg = RestAPIStringParser.getMessage(locale,
                PretupsErrorCodesI.LOAD_PROFILE_DETAILS_SUCCESSFULLY, null);
        response.setMessage(resmsg);
        response.setMessageCode(PretupsErrorCodesI.LOAD_PROFILE_DETAILS_SUCCESSFULLY);
        if (response.getProfileDetailsSetList().size() == 0) {
            response.setStatus(HttpStatus.SC_OK);
            String resmsg2 = RestAPIStringParser.getMessage(locale,
                    PretupsErrorCodesI.NO_DATA_FOUND_FOR_FILTERS_CRITERIA, null);
            response.setMessage(resmsg2);
            response.setMessageCode(PretupsErrorCodesI.NO_DATA_FOUND_FOR_FILTERS_CRITERIA);
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_DATA_FOUND_FOR_FILTERS_CRITERIA);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Exiting");
        }

        return response;
    }

    @Override
    public VersionsResponseVO loadVersionsList(Connection con, UserVO userVO, String setID, String validUpToDate, VersionsResponseVO response) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "loadVersionsList";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }

        if (setID == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_SET_ID_NULL);
        }
        if (BTSLUtil.isEmpty(setID)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_SET_ID_INVALID);
        }
        if (BTSLUtil.isEmpty(validUpToDate)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.VALID_UP_TO_DATE);
        }

        String format = Constants.getProperty("LMS_PROFILE_DATE_FORMAT");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(format);
        Date validToDate = null;

        try {
            validToDate = outputDateFormat.parse(validUpToDate);
        } catch (Exception ex) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.INVALID_END_DATE_AND_TIME);
        }

        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        ActivationBonusLMSWebDAO activationBonusLMSWebDAO = new ActivationBonusLMSWebDAO();
        ArrayList<ProfileSetVersionLMSVO> versionDetailsList = activationBonusLMSWebDAO.loadVersionsList(con, setID, validToDate);
        ArrayList versionList = new ArrayList();
        for (ProfileSetVersionLMSVO profileSetVersionLMSVO : versionDetailsList) {
            response.setSetId(profileSetVersionLMSVO.getSetId());
            versionList.add(profileSetVersionLMSVO.getVersion());
        }
        response.setVersions(versionList);

        response.setStatus((HttpStatus.SC_OK));
        String resmsg = RestAPIStringParser.getMessage(locale,
                PretupsErrorCodesI.LOAD_LOYALITY_MANAGEMENT_VERSIONS_SUCCESSFULLY, null);
        response.setMessage(resmsg);
        response.setMessageCode(PretupsErrorCodesI.LOAD_LOYALITY_MANAGEMENT_VERSIONS_SUCCESSFULLY);
        if (response.getVersions().size() == 0) {
            response.setStatus(HttpStatus.SC_OK);
            String resmsg2 = RestAPIStringParser.getMessage(locale,
                    PretupsErrorCodesI.NO_DATA_FOUND_FOR_FILTERS_CRITERIA, null);
            response.setMessage(resmsg2);
            response.setMessageCode(PretupsErrorCodesI.NO_DATA_FOUND_FOR_FILTERS_CRITERIA);
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_DATA_FOUND_FOR_FILTERS_CRITERIA);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Exiting");
        }

        return response;
    }

    @Override
    public ProfileDetailsVO loadProfileDetails(Connection con, UserVO userVO, String setID, String version, ProfileDetailsVO response) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "loadProfileDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }

        if (setID == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SET_ID_IS_NULL);
        }
        if (BTSLUtil.isEmpty(setID)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SET_ID_IS_EMPTY);
        }


        if (version == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.VERSION_IS_NULL);
        }
        if (BTSLUtil.isEmpty(version)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.VERSION_IS_EMPTY);
        }

        if (!setID.matches(PretupsI.NUMERIC_TYPE)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SET_ID_IS_INVALID_FORMAT);
        }
        if (!version.matches(PretupsI.NUMERIC_TYPE)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.VERSION_IS_INVALID_FORMAT);
        }


        // arraylist to store transactionand volume based profile
        // entries slabs
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        ArrayList transactionProfileList = new ArrayList();
        ArrayList volumeProfileList = new ArrayList();
        ArrayList profileDeatilsList = null;
        ArrayList<ProfileSetDetailsLMSVO> transDetailVOList = null;
        ArrayList countDetailVOList = null;
        ArrayList<ProfileSetDetailsLMSVO> amountDetailVOList = null;
        ActivationBonusLMSWebDAO activationBonusLMSWebDAO = new ActivationBonusLMSWebDAO();
        ListSorterUtil sort = new ListSorterUtil();
        NetworkProductDAO networkProductDAO = new NetworkProductDAO();
        ArrayList productList = networkProductDAO.loadProductList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE);
        ArrayList moduleList = LookupsCache.loadLookupDropDown(PretupsI.LMS_SERVICE, true);
        ArrayList subscriberList = LookupsCache.loadLookupDropDown(PretupsI.ACTIVATION_SUBSCRIBER_TYPE, true);
        ArrayList targetTypeList = LookupsCache.loadLookupDropDown(PretupsI.TARGET_TYPE, true);
        ArrayList periodTypeList = LookupsCache.loadLookupDropDown(PretupsI.PERIOD_TYPE, true);
        ArrayList pointTypeList = LookupsCache.loadLookupDropDown(PretupsI.AMOUNT_TYPE, true);
        ArrayList allServiceList = activationBonusLMSWebDAO.loadLookupServicesList(con);
        ActivationProfileCombinedLMSVO detailsVO = null;
        ActivationProfileCombinedLMSVO tempDetailsVO = null;
        // ActivationProfileCombinedLMSVO versionDetailVO=null;
        ProfileSetDetailsLMSVO transDetailVO = null;
        ProfileSetDetailsLMSVO countDetailVO = null;
        ListValueVO vo = null;
        Iterator listSeparatorItr = null;
        // get the profile's version details
        ActivationProfileCombinedLMSVO versionDetailVO = activationBonusLMSWebDAO.loadProfileVersionDetails(con, setID, version, userVO.getNetworkID());
        if (versionDetailVO == null) {
            response.setStatus(String.valueOf(HttpStatus.SC_OK));
            String resmsg2 = RestAPIStringParser.getMessage(locale,
                    PretupsErrorCodesI.NO_DATA_FOUND_FOR_FILTERS_CRITERIA, null);
            response.setMessage(resmsg2);
            response.setMessageCode(PretupsErrorCodesI.NO_DATA_FOUND_FOR_FILTERS_CRITERIA);
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_DATA_FOUND_FOR_FILTERS_CRITERIA);
        }
        // populate details of activation profile for selected version
        response.setProfileName(versionDetailVO.getSetName());
        response.setShortCode(versionDetailVO.getShortCode());
        Date currentDate = new Date(System.currentTimeMillis());
        String format = Constants.getProperty("LMS_PROFILE_DATE_FORMAT");
        String currentDateString = new SimpleDateFormat(format).format(currentDate);
        response.setCurrentServerDateAndTime(currentDateString);
        response.setOptInOutService(versionDetailVO.getOptInOut());
        if (versionDetailVO.getOptInOut().equals(PretupsI.YES)) {
            response.setOptInOutServiceDes(PretupsI.YES_DES);
        } else response.setOptInOutServiceDes(PretupsI.NO_DES);
        response.setOperatorContribution(versionDetailVO.getOptContribution());
        response.setParentContribution(versionDetailVO.getPrtContribution());
        response.setApplicableFromDate(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(versionDetailVO.getApplicableFrom())));
        // to keep track for new version
        response.setOldApplicableFromDate(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(versionDetailVO.getApplicableFrom())));
        // set applicable from time
        int hour = BTSLUtil.getHour(versionDetailVO.getApplicableFrom());
        int minute = BTSLUtil.getMinute(versionDetailVO.getApplicableFrom());
        String time = BTSLUtil.getTimeinHHMM(hour, minute);
        /*
         * old applicable from is used to compare with new one so as to
         * create a new version or modify the previously
         * existing version
         */
        response.setApplicableFromHour(time);
        response.setOldApplicableFromHour(time);

        response.setApplicableToDate(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(versionDetailVO.getApplicableTo())));
        // to keep track for new version
        response.setOldApplicableToDate(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(versionDetailVO.getApplicableTo())));
        // set applicable from time
        int tohour = BTSLUtil.getHour(versionDetailVO.getApplicableTo());
        int tominute = BTSLUtil.getMinute(versionDetailVO.getApplicableTo());
        String totime = BTSLUtil.getTimeinHHMM(tohour, tominute);
        /*
         * old applicable from is used to compare with new one so as to
         * create a new version or modify the previously
         * existing version
         */
        response.setApplicableToHour(totime);
        response.setOldApplicableToHour(totime);
        response.setValidUpToDate(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(versionDetailVO.getApplicableTo())));
        response.setNetworkName(userVO.getNetworkName());
        response.setPromotionType(versionDetailVO.getPromotionType());
        response.setPromotionTypeName(versionDetailVO.getPromotionTypeName());
        response.setReferenceBased(versionDetailVO.getReferenceBasedFlag());
        if (versionDetailVO.getReferenceBasedFlag().equals(PretupsI.YES))
            response.setReferenceBasedDes(PretupsI.YES_DES);
        else
            response.setReferenceBasedDes(PretupsI.NO_DES);
        if (!(versionDetailVO.getRefApplicableFrom() == null)) {
            response.setReferencefromDate(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(versionDetailVO.getRefApplicableFrom())));
        }
        if (!(versionDetailVO.getRefApplicableTo() == null)) {
            response.setReferenceToDate(BTSLDateUtil.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(versionDetailVO.getRefApplicableTo())));
        }

        if (versionDetailVO.getBonusDuration() == 0) {
            response.setBonusDuration(null);
        } else {
            response.setBonusDuration(Long.toString(versionDetailVO.getBonusDuration()));
        }
        response.setBonusPerActivation(Long.toString(versionDetailVO.getOneTimeBonus()));
        // response.setProductCode(versionDetailVO.getProductCode());
        //response.setProductCodeDesc(BTSLUtil.getOptionDesc(response.getProductCode(), productList).getLabel());
        response.setLastVersion(versionDetailVO.getLastVersion());
        response.setVersion(Integer.parseInt(version));
        // brajesh
        response.setMsgConfigEnabled(versionDetailVO.getMsgConfEnableFlag());
        if (versionDetailVO.getMsgConfEnableFlag().equals(PretupsI.YES)) {
            response.setMsgConfigEnabledDes(PretupsI.YES_DES);
        } else {
            response.setMsgConfigEnabledDes(PretupsI.NO_DES);
        }
        response.setProfileExpiredFlag(versionDetailVO.getLmsProfileExpiredFlag());
        response.setSetId(versionDetailVO.getSetId());
        // type of services that exist for particular/selected setid and
        // version
        List<VolumeProfileDetailsVO> volumeProfileDetailsVOList = new ArrayList<>();
        ArrayList serviceList = activationBonusLMSWebDAO.loadActivationProfileServicesList(con, setID, version);
        // iterate the servicelist
        int serviceLists = serviceList.size();
        for (int i = 0; i < serviceLists; i++) {
            vo = (ListValueVO) serviceList.get(i);
            /*
             * profileDeatilsList contains the slabs for particular
             * service code of selected profile and setid
             * it can contain both transaction and volume slabs for same
             * service code
             */
            VolumeProfileDetailsVO volumeProfileDetailsVO = new VolumeProfileDetailsVO();
            profileDeatilsList = activationBonusLMSWebDAO.loadActivationProfileDetailList(con, vo.getValue(), setID, version);
            profileDeatilsList = (ArrayList) sort.doSort("startRange", null, profileDeatilsList);
            listSeparatorItr = profileDeatilsList.iterator();
            detailsVO = new ActivationProfileCombinedLMSVO();
            tempDetailsVO = new ActivationProfileCombinedLMSVO();
            transDetailVOList = new ArrayList();
            countDetailVOList = new ArrayList();
            amountDetailVOList = new ArrayList();
            List<VolumeVO> volumeVOList = new ArrayList<>();

            while (listSeparatorItr.hasNext()) {
                transDetailVO = (ProfileSetDetailsLMSVO) listSeparatorItr.next();
                /*
                 * differentiate slabs on basis of transaction and
                 * volume based for particular serice code
                 * and add to particular list.
                 */
//                response.setProductCode(transDetailVO.getProductCode());
                if (transDetailVO.getDetailType().equalsIgnoreCase(PretupsI.PROFILE_TRANS)) {
                    /*
                     * if detail type is trans add to transaction list
                     * otherwise to count or amount slabs of volume
                     * profile list
                     */


                    transDetailVOList.add(transDetailVO);
                    response.setTransactionProfileSubType(transDetailVO.getSubscriberType());
                    //               response.setModuleType(transDetailVO.getType());
                    volumeProfileDetailsVO.setService(transDetailVO.getServiceCode());
                    volumeProfileDetailsVO.setServiceDesc(BTSLUtil.getOptionDesc(transDetailVO.getServiceCode(), serviceList).getLabel());
                    volumeProfileDetailsVO.setModule(transDetailVO.getType());
                    volumeProfileDetailsVO.setModuleDesc(BTSLUtil.getOptionDesc(transDetailVO.getType(), moduleList).getLabel());
                    volumeProfileDetailsVO.setProduct(transDetailVO.getProductCode());
                    volumeProfileDetailsVO.setProductDesc(BTSLUtil.getOptionDesc(transDetailVO.getProductCode(), productList).getLabel());

                    VolumeVO volumeVO = new VolumeVO();
                    volumeVO.setFromRange(transDetailVO.getStartRange());
                    volumeVO.setToRange(transDetailVO.getEndRange());
                    volumeVO.setPoints(transDetailVO.getPoints());
                    volumeVO.setRewardsType(transDetailVO.getPointsTypeCode());
                    volumeVO.setRewardsTypeDesc(BTSLUtil.getOptionDesc(transDetailVO.getPointsTypeCode(), pointTypeList).getLabel());
                    volumeVOList.add(volumeVO);

                } else {
                    //                 response.setModuleType(transDetailVO.getType());
                    response.setVolumeProfileSubType(transDetailVO.getSubscriberType());
                    transDetailVO.setTargetType(transDetailVO.getDetailSubType());
                    if (transDetailVO.getDetailSubType().equalsIgnoreCase(PretupsI.USER_SUB_TYPE_COUNT)) {
                        volumeProfileDetailsVO.setService(transDetailVO.getServiceCode());
                        volumeProfileDetailsVO.setServiceDesc(BTSLUtil.getOptionDesc(transDetailVO.getServiceCode(), serviceList).getLabel());
                        volumeProfileDetailsVO.setModule(transDetailVO.getType());
                        volumeProfileDetailsVO.setModuleDesc(BTSLUtil.getOptionDesc(transDetailVO.getType(), moduleList).getLabel());
                        volumeProfileDetailsVO.setProduct(transDetailVO.getProductCode());
                        volumeProfileDetailsVO.setProductDesc(BTSLUtil.getOptionDesc(transDetailVO.getProductCode(), productList).getLabel());
                        VolumeVO volumeVO = new VolumeVO();
                        volumeVO.setPoints(transDetailVO.getPoints());
                        volumeVO.setTargetType(transDetailVO.getTargetType());
                        volumeVO.setTargetTypeDesc(BTSLUtil.getOptionDesc(transDetailVO.getTargetType(), targetTypeList).getLabel());
                        volumeVO.setRewardsType(transDetailVO.getPointsTypeCode());
                        volumeVO.setRewardsTypeDesc(BTSLUtil.getOptionDesc(transDetailVO.getPointsTypeCode(), pointTypeList).getLabel());
                        volumeVO.setTarget(transDetailVO.getStartRange());
                        volumeVO.setFrequency(transDetailVO.getPeriodId());
                        volumeVO.setFrequencyDesc(BTSLUtil.getOptionDesc(transDetailVO.getPeriodId(), periodTypeList).getLabel());
                        volumeVOList.add(volumeVO);
                        countDetailVOList.add(transDetailVO);
                    } else {
                        volumeProfileDetailsVO.setService(transDetailVO.getServiceCode());
                        volumeProfileDetailsVO.setServiceDesc(BTSLUtil.getOptionDesc(transDetailVO.getServiceCode(), serviceList).getLabel());
                        volumeProfileDetailsVO.setModule(transDetailVO.getType());
                        volumeProfileDetailsVO.setModuleDesc(BTSLUtil.getOptionDesc(transDetailVO.getType(), moduleList).getLabel());
                        volumeProfileDetailsVO.setProduct(transDetailVO.getProductCode());
                        volumeProfileDetailsVO.setProductDesc(BTSLUtil.getOptionDesc(transDetailVO.getProductCode(), productList).getLabel());
                        VolumeVO volumeVO = new VolumeVO();
                        volumeVO.setPoints(transDetailVO.getPoints());
                        volumeVO.setTargetType(transDetailVO.getTargetType());
                        volumeVO.setTargetTypeDesc(BTSLUtil.getOptionDesc(transDetailVO.getTargetType(), targetTypeList).getLabel());
                        volumeVO.setRewardsType(transDetailVO.getPointsTypeCode());
                        volumeVO.setRewardsTypeDesc(BTSLUtil.getOptionDesc(transDetailVO.getPointsTypeCode(), pointTypeList).getLabel());
                        volumeVO.setTarget(transDetailVO.getStartRange());
                        volumeVO.setFrequency(transDetailVO.getPeriodId());
                        volumeVO.setFrequencyDesc(BTSLUtil.getOptionDesc(transDetailVO.getPeriodId(), periodTypeList).getLabel());
                        volumeVOList.add(volumeVO);
                        amountDetailVOList.add(transDetailVO);
                    }
                }
            }
            volumeProfileDetailsVO.setVolumeVOList(volumeVOList);
            volumeProfileDetailsVOList.add(volumeProfileDetailsVO);
        }

        if (response.getPromotionType().equals(PretupsI.STOCK)) {
            response.setVolumeProfileList(volumeProfileDetailsVOList);
        } else {
            response.setTransDetailsVOList(volumeProfileDetailsVOList);
        }
        response.setStatus(String.valueOf(HttpStatus.SC_OK));
        String resmsg = RestAPIStringParser.getMessage(locale,
                PretupsErrorCodesI.LOAD_LOYALITY_MANAGEMENT_PROFILE_DETAILS_SUCCESSFULLY, null);
        response.setMessage(resmsg);
        response.setMessageCode(PretupsErrorCodesI.LOAD_LOYALITY_MANAGEMENT_PROFILE_DETAILS_SUCCESSFULLY);
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Exited");
        }
        return response;
    }

    @Override
    public ModulesAndServiceResponseVO loadModulesAndServices(Connection con, UserVO userVO, ModulesAndServiceResponseVO response) throws BTSLBaseException, Exception {

        final String METHOD_NAME = "loadModulesAndServices";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        NetworkProductDAO networkProductDAO = new NetworkProductDAO();
        ActivationBonusLMSWebDAO activationBonusLMSWebDAO = new ActivationBonusLMSWebDAO();
        ArrayList moduleList = LookupsCache.loadLookupDropDown(PretupsI.LMS_SERVICE, true);
        ArrayList subscriberList = LookupsCache.loadLookupDropDown(PretupsI.ACTIVATION_SUBSCRIBER_TYPE, true);
        ArrayList targetTypeList = LookupsCache.loadLookupDropDown(PretupsI.TARGET_TYPE, true);
        ArrayList periodTypeList = LookupsCache.loadLookupDropDown(PretupsI.PERIOD_TYPE, true);
        ArrayList pointTypeList = LookupsCache.loadLookupDropDown(PretupsI.AMOUNT_TYPE, true);
        ArrayList allServiceList = activationBonusLMSWebDAO.loadLookupServicesList(con);
        ArrayList prodServiceMapList = networkProductDAO.loadProductServiceMapping();
        ArrayList productList = networkProductDAO.loadProductList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE);
        ArrayList finalList = new ArrayList();
        ListValueVO lv = null;
        int theFormProductServiceLists = allServiceList.size();
        for (int count = 0; count < theFormProductServiceLists; count++) {
            ListValueVO vo = (ListValueVO) allServiceList.get(count);
            if (PretupsI.ALL.equalsIgnoreCase(vo.getValue().split("[:]")[0]) || PretupsI.C2C_MODULE.equalsIgnoreCase(vo.getValue().split("[:]")[0]) || PretupsI.O2C_MODULE.equalsIgnoreCase(vo.getValue().split("[:]")[0])) {
                finalList.add(vo);
            } else {
                int prodServicesMapLists = prodServiceMapList.size();
                for (int count1 = 0; count1 < prodServicesMapLists; count1++) {
                    if (vo.getValue().split("[:]")[0].equalsIgnoreCase(((ListValueVO) prodServiceMapList.get(count1)).getLabel())) {
                        String tempString = vo.getValue();
                        String tempStringCustom = ((ListValueVO) prodServiceMapList.get(count1)).getValue().split("[:]")[1];
                        tempString = tempString + ":" + tempStringCustom;
                        lv = new ListValueVO(vo.getLabel(), tempString);

                        finalList.add(lv);
                        break;
                    }
                }
            }
        }

        response.setServicesList(finalList);
        response.setModulesList(moduleList);
        response.setProductList(productList);
        response.setSubscriberList(subscriberList);
        response.setPointTypeList(pointTypeList);
        response.setTargetTypeList(targetTypeList);
        response.setPeriodTypeList(periodTypeList);
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        String resmsg = RestAPIStringParser.getMessage(locale,
                PretupsErrorCodesI.LOAD_PROFILE_MODULE_DETAILS_SUCCESSFULLY, null);
        response.setStatus((HttpStatus.SC_OK));
        response.setMessage(resmsg);
        response.setMessageCode(PretupsErrorCodesI.LOAD_PROFILE_MODULE_DETAILS_SUCCESSFULLY);

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Exited");
        }
        return response;
    }

    @Override
    public BaseResponse addProfileDetails(Connection con, UserVO userVO, AddProfileDetailsRequestVO requestVO, BaseResponse response) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "addProfileDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }

        this.addProfileValidation(con, userVO, requestVO, PretupsI.ADD_LMS);

        boolean isnameexists = new ActivationBonusLMSWebDAO().isprofileNmaeExist(con, requestVO.getProfileName());
        if (isnameexists) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_INVALID_PROFILENAME);
        }
        if (requestVO.getTransDetailsVOList().isEmpty()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.AT_LEAST_ONE_SLAB_IS_REQUIRED);

        }

        if (!BTSLUtil.isNullString(requestVO.getOperatorContribution())) {
            requestVO.setOperatorContribution(requestVO.getOperatorContribution());
        }
        if (requestVO.getPromotionType().equals(PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT)) {
            requestVO.setParentContribution(PretupsI.ZERO);
            requestVO.setOperatorContribution("100");
        }

        Date currentDate = new Date();
        boolean updateVersion = false;
        String format = Constants.getProperty("LMS_PROFILE_DATE_FORMAT");
        // check whether the Activation Short Code is already exist or
        // not
        int insertMessagesUpdateCount = 0;
        char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 5; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();
        ActivationBonusLMSWebDAO lmsMessageDAO = new ActivationBonusLMSWebDAO();
        String setID = String.valueOf(IDGenerator.getNextID(PretupsI.ACTIVATION_PROFILE_SETID, TypesI.ALL));
        ProfileSetLMSVO profileSetVO = new ProfileSetLMSVO();
        profileSetVO.setSetName(requestVO.getProfileName());
        profileSetVO.setSetId(setID);
        profileSetVO.setLastVersion(PretupsI.BUCKET_ONE);
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_PROF_APR_ALLOWED)).booleanValue()) {
            profileSetVO.setStatus(PretupsI.USER_STATUS_NEW);
        } else {
            profileSetVO.setStatus(PretupsI.STATUS_ACTIVE);
        }
        profileSetVO.setCreatedBy(userVO.getUserID());
        profileSetVO.setCreatedOn(currentDate);
        profileSetVO.setModifiedBy(userVO.getUserID());
        profileSetVO.setModifiedOn(currentDate);
        profileSetVO.setProfileType(PretupsI.LMS_PROFILE_TYPE);
        profileSetVO.setShortCode(output);
        profileSetVO.setNetworkCode(userVO.getNetworkID());
        profileSetVO.setPromotionType(requestVO.getPromotionType());
        profileSetVO.setRefBasedAllow(requestVO.getReferenceBased());
        profileSetVO.setMsgConfEnableFlag(requestVO.getMsgConfigEnabled());
        profileSetVO.setApplicableFromDate(requestVO.getApplicableFromDate());
        profileSetVO.setApplicableToDate(requestVO.getApplicableToDate());
        profileSetVO.setNetworkCode(userVO.getNetworkID());
        // OPT_IN/OPT_OUT Service
        if (requestVO.getOptInOutService() != null && requestVO.getOptInOutService().equals(PretupsI.YES)) {
            profileSetVO.setOptInOut(requestVO.getOptInOutService());
        } else {
            profileSetVO.setOptInOut(PretupsI.NO);
        }
        ActivationBonusLMSWebDAO activationBonusLMSWebDAO = new ActivationBonusLMSWebDAO();

        int insertUpdateSetCount = activationBonusLMSWebDAO.addActivationBonusSet(con, profileSetVO, PretupsI.ADD_LMS);

        if (insertUpdateSetCount <= 0) {
            try {
                con.rollback();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            LOG.error(METHOD_NAME, "Error: while Inserting Activation Profile Set");
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
        }

        if ("Y".equals(profileSetVO.getMsgConfEnableFlag())) {
            Locale locale1 = new Locale("en", "US");
            Locale locale2 = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            LOG.debug(METHOD_NAME, "ME HERE : " + profileSetVO.getPromotionType());
            String Message_code = null;
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue() && PretupsI.LMS_PROMOTION_TYPE_STOCK.equalsIgnoreCase(profileSetVO.getPromotionType()) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(profileSetVO.getOptInOut())) {
                Message_code = PretupsI.OPTINOUT_WEL_MESSAGE + "_" + profileSetVO.getSetId();
            } else if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue() && PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(profileSetVO.getPromotionType()) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(profileSetVO.getOptInOut())) {
                Message_code = PretupsI.OPTINOUT_TRA_WEL_MSG + "_" + profileSetVO.getSetId();
            } else if (PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(profileSetVO.getPromotionType())) {
                Message_code = PretupsI.TRA_WEL_MESSAGE + "_" + profileSetVO.getSetId();
            } else {
                Message_code = PretupsI.WEL_MESSAGE + "_" + profileSetVO.getSetId();
            }
            profileSetVO.setMessageCode(Message_code);
            if (!activationBonusLMSWebDAO.isMessageExists(con, Message_code)) // to
            // check
            // whether
            // message
            // is
            // already
            // configured
            // or
            // not
            {
                String defaultMessage = null;
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue() && PretupsI.LMS_PROMOTION_TYPE_STOCK.equalsIgnoreCase(profileSetVO.getPromotionType()) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(profileSetVO.getOptInOut())) {
                    defaultMessage = BTSLUtil.getMessage(locale1, PretupsI.OPTINOUT_WEL_MESSAGE);
                } else if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue() && PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(profileSetVO.getPromotionType()) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(profileSetVO.getOptInOut())) {
                    defaultMessage = BTSLUtil.getMessage(locale1, PretupsI.OPTINOUT_TRA_WEL_MSG);
                } else if (PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(profileSetVO.getPromotionType())) {
                    defaultMessage = BTSLUtil.getMessage(locale1, PretupsI.TRA_WEL_MESSAGE);
                } else {
                    defaultMessage = BTSLUtil.getMessage(locale1, PretupsI.WEL_MESSAGE);
                }
                if (BTSLUtil.isNullString(defaultMessage)) {

                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.DEFAULT_MESSAGE_IS_NULL);
                } else {
                    profileSetVO.setdefaultMessage(defaultMessage);
                }

                String message1 = null;
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue() && PretupsI.LMS_PROMOTION_TYPE_STOCK.equalsIgnoreCase(profileSetVO.getPromotionType()) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(profileSetVO.getOptInOut())) {
                    message1 = BTSLUtil.getMessage(locale1, PretupsI.OPTINOUT_WEL_MESSAGE_LANG1);
                } else if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue() && PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(profileSetVO.getPromotionType()) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(profileSetVO.getOptInOut())) {
                    message1 = BTSLUtil.getMessage(locale1, PretupsI.OPTINOUT_TRA_WEL_MSG_LANG1);
                } else if (PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(profileSetVO.getPromotionType())) {
                    message1 = BTSLUtil.getMessage(locale1, PretupsI.TRA_WEL_MSG_LANG1);
                } else {
                    message1 = BTSLUtil.getMessage(locale1, PretupsI.WEL_MESSAGE_LANG1);
                }
                if (BTSLUtil.isNullString(message1)) {
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.DEFAULT_MESSAGE_IS_NULL);
                } else {
                    profileSetVO.setMessage1(message1);
                }

                String message2 = null;
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue() && PretupsI.LMS_PROMOTION_TYPE_STOCK.equalsIgnoreCase(profileSetVO.getPromotionType()) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(profileSetVO.getOptInOut())) {
                    message2 = BTSLUtil.getMessage(locale2, PretupsI.OPTINOUT_WEL_MESSAGE_LANG2);
                } else if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue() && PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(profileSetVO.getPromotionType()) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(profileSetVO.getOptInOut())) {
                    message2 = BTSLUtil.getMessage(locale2, PretupsI.OPTINOUT_TRA_WEL_MSG_LANG2);
                } else if (PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(profileSetVO.getPromotionType())) {
                    message2 = BTSLUtil.getMessage(locale2, PretupsI.TRA_WEL_MSG_LANG2);
                } else {
                    message2 = BTSLUtil.getMessage(locale2, PretupsI.WEL_MESSAGE_LANG2);
                }
                if (BTSLUtil.isNullString(message2)) {
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsErrorCodesI.DEFAULT_MESSAGE_IS_NULL);
                } else {
                    profileSetVO.setMessage2(message2);
                }

                insertMessagesUpdateCount = activationBonusLMSWebDAO.addActivationBonusMessages(con, profileSetVO, PretupsI.ADD_LMS);


                if (insertMessagesUpdateCount <= 0) {
                    try {
                        con.rollback();
                    } catch (Exception e) {
                        LOG.errorTrace(METHOD_NAME, e);
                    }
                    LOG.error(METHOD_NAME, "Error: while Inserting Activation Profile Set");
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
                }
                if (!"modifyactprofile".equals("addactprofile")) {
                    String[] messageArgArraySuc = null;
                    if (!PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(profileSetVO.getPromotionType())) {
                        Message_code = PretupsI.SUCCESS_MESSAGE + "_" + profileSetVO.getSetId();
                        profileSetVO.setMessageCode(Message_code);
                        defaultMessage = BTSLUtil.getMessage(locale1, PretupsI.SUCCESS_MESSAGE);
                        if (BTSLUtil.isNullString(defaultMessage)) {
                            throw new BTSLBaseException(this, METHOD_NAME, "");
                        } else {
                            profileSetVO.setdefaultMessage(defaultMessage);
                        }
                        message1 = BTSLUtil.getMessage(locale1, PretupsI.SUCCESS_MESSAGE_LANG1);
                        if (BTSLUtil.isNullString(message1)) {
                            throw new BTSLBaseException(this, METHOD_NAME, "");
                        } else {
                            profileSetVO.setMessage1(message1);
                        }
                        message2 = BTSLUtil.getMessage(locale2, PretupsI.SUCCESS_MESSAGE_LANG2);
                        if (BTSLUtil.isNullString(message2)) {
                            throw new BTSLBaseException(this, METHOD_NAME, "");
                        } else {
                            profileSetVO.setMessage2(message2);
                        }
                        insertMessagesUpdateCount = activationBonusLMSWebDAO.addActivationBonusMessages(con, profileSetVO, PretupsI.ADD_LMS);
                        if (insertMessagesUpdateCount <= 0) {
                            try {
                                con.rollback();
                            } catch (Exception e) {
                                LOG.errorTrace(METHOD_NAME, e);
                            }
                            LOG.error(METHOD_NAME, "Error: while Inserting Activation Profile Set");
                            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
                        }
                        String[] messageArgArrayFail = null;

                        Message_code = PretupsI.FAILURE_MESSAGE + "_" + profileSetVO.getSetId();
                        profileSetVO.setMessageCode(Message_code);
                        messageArgArraySuc = new String[]{profileSetVO.getSetName(), profileSetVO.getApplicableFromDate(), profileSetVO.getApplicableToDate()};
                        defaultMessage = BTSLUtil.getMessage(locale1, PretupsI.FAILURE_MESSAGE);
                        if (BTSLUtil.isNullString(defaultMessage)) {
                            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.DEFAULT_MESSAGE_IS_NULL);
                        } else {
                            profileSetVO.setdefaultMessage(defaultMessage);
                        }
                        message1 = BTSLUtil.getMessage(locale1, PretupsI.FAILURE_MESSAGE_LANG1);
                        if (BTSLUtil.isNullString(message1)) {
                            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.LANGUAGE_MESSAGE_IS_NULL);
                        } else {
                            profileSetVO.setMessage1(message1);
                        }
                        message2 = BTSLUtil.getMessage(locale2, PretupsI.FAILURE_MESSAGE_LANG2);
                        if (BTSLUtil.isNullString(message2)) {
                            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.LANGUAGE_MESSAGE_IS_NULL);
                        } else {
                            profileSetVO.setMessage2(message2);
                        }
                        insertMessagesUpdateCount = activationBonusLMSWebDAO.addActivationBonusMessages(con, profileSetVO, PretupsI.ADD_LMS);
                        if (insertMessagesUpdateCount <= 0) {
                            try {
                                con.rollback();
                            } catch (Exception e) {
                                LOG.errorTrace(METHOD_NAME, e);
                            }
                            LOG.error(METHOD_NAME, "Error: while Inserting Activation Profile Set");
                            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
                        }
                    }
                }
            }
        } else if ("N".equals(profileSetVO.getMsgConfEnableFlag()) && !(PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT).equals(requestVO.getPromotionType())) {

            ProfileSetLMSVO tempProfileSetVO = null;
            tempProfileSetVO = new ProfileSetLMSVO();
            tempProfileSetVO = lmsMessageDAO.loadMessageList(con, profileSetVO.getSetId());
            if (!BTSLUtil.isNullString(tempProfileSetVO.getMessageCode())) {
                int deleteCount = lmsMessageDAO.deleteMessages(con, tempProfileSetVO.getSetId());
                if (deleteCount <= 0) {
                    try {
                        con.rollback();
                    } catch (Exception e) {
                        LOG.errorTrace(METHOD_NAME, e);
                    }
                    LOG.error(METHOD_NAME, "Error: while Deleting Messages");
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
                }
            }
        }
        // add data version and details
        if (!updateVersion) {
            this.addVersion(requestVO, userVO, con, activationBonusLMSWebDAO, currentDate, profileSetVO);
        }
        this.addProfileDetails(requestVO, userVO, con, activationBonusLMSWebDAO, currentDate, profileSetVO, updateVersion);
        con.commit();

        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        String arr[] = {requestVO.getProfileName()};
        String resmsg = RestAPIStringParser.getMessage(locale,
                PretupsErrorCodesI.LOYALTY_PROFILE_INITIATED, arr);
        final AdminOperationVO adminOperationVO = new AdminOperationVO();
        adminOperationVO.setSource(PretupsI.LOGGER_LMS_SOURCE);
        adminOperationVO.setDate(currentDate);
        adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
        adminOperationVO.setInfo(resmsg);
        adminOperationVO.setLoginID(userVO.getLoginID());
        adminOperationVO.setUserID(userVO.getUserID());
        adminOperationVO.setCategoryCode(userVO.getCategoryCode());
        adminOperationVO.setNetworkCode(userVO.getNetworkID());
        adminOperationVO.setMsisdn(userVO.getMsisdn());
        AdminOperationLog.log(adminOperationVO);
        response.setStatus((HttpStatus.SC_OK));
        response.setMessage(resmsg);
        response.setMessageCode(PretupsErrorCodesI.LOYALTY_PROFILE_INITIATED);
        return response;
    }

    private void addVersion(AddProfileDetailsRequestVO requestVO, UserVO userVO, Connection con, ActivationBonusLMSWebDAO activationBonusLMSWebDAO, Date currentDate, ProfileSetLMSVO profileSetVO) throws Exception {
        final String METHOD_NAME = "addVersion";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered:profileSetVO" + profileSetVO.toString());
        }
        // populating CommissionProfileSetVersionVO from the request Parameters
        ProfileSetVersionLMSVO profileSetVersionVO = new ProfileSetVersionLMSVO();
        profileSetVersionVO.setVersion(profileSetVO.getLastVersion());
        profileSetVersionVO.setSetId(profileSetVO.getSetId());
        String format = Constants.getProperty("LMS_PROFILE_DATE_FORMAT");
        Date newDate = BTSLUtil.getDateFromDateString(requestVO.getApplicableFromDate(), format);
        profileSetVersionVO.setApplicableFrom(newDate);
        Date newToDate = BTSLUtil.getDateFromDateString(requestVO.getApplicableToDate(), format);
        profileSetVersionVO.setApplicableTo(newToDate);
        if (!BTSLUtil.isNullString(requestVO.getReferencefromDate())) {
            profileSetVersionVO.setRefApplicableFrom(BTSLUtil.getDateFromDateString(requestVO.getReferencefromDate()));
        }
        if (!BTSLUtil.isNullString(requestVO.getReferenceToDate())) {
            profileSetVersionVO.setRefApplicableTo(BTSLUtil.getDateFromDateString(requestVO.getReferenceToDate()));
        }
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_PROF_APR_ALLOWED)).booleanValue()) {
            profileSetVersionVO.setStatus(PretupsI.USER_STATUS_NEW);
        } else {
            profileSetVersionVO.setStatus(PretupsI.STATUS_ACTIVE);
        }
        profileSetVersionVO.setOptContribution(requestVO.getOperatorContribution());
        profileSetVersionVO.setPrtContribution(requestVO.getParentContribution());
        profileSetVersionVO.setCreatedBy(userVO.getUserID());
        profileSetVersionVO.setCreatedOn(currentDate);
        profileSetVersionVO.setModifiedBy(userVO.getUserID());
        profileSetVersionVO.setModifiedOn(currentDate);

        // insert Card_Group_Set_Version
        int insertVersionCount = activationBonusLMSWebDAO.addActivationBonusVersion(con, profileSetVersionVO);
        if (insertVersionCount <= 0) {
            try {
                con.rollback();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            LOG.error(METHOD_NAME, "Error: while Inserting Activation_Profile_Set_Version");
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Exiting:insertVersionCount" + insertVersionCount);
        }
    }

    private void addProfileDetails(AddProfileDetailsRequestVO requestVO, UserVO userVO, Connection con, ActivationBonusLMSWebDAO activationBonusLMSWebDAO, Date currentDate, ProfileSetLMSVO profileSetVO, boolean updateVersion) throws Exception {
        final String METHOD_NAME = "addProfileDetails";

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }

        ArrayList list = new ArrayList();
        ArrayList<ActivationProfileCombinedLMSVO> combinedList = new ArrayList<>();
        if ((requestVO.getTransDetailsVOList() != null && !requestVO.getTransDetailsVOList().isEmpty()) && requestVO.getPromotionType().equalsIgnoreCase(PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT)) {


            for (int i = 0; i < requestVO.getTransDetailsVOList().size(); i++) {
                ActivationProfileCombinedLMSVO activationProfileCombinedVO = new ActivationProfileCombinedLMSVO();
                AddVolumeProfileDetailsVO volumeProfileDetailsVO = (AddVolumeProfileDetailsVO) requestVO.getTransDetailsVOList().get(i);
                ArrayList slabList = new ArrayList();
                ArrayList tempList = new ArrayList();
                int slabCount = 1;
                ProfileSetDetailsLMSVO setDetailsVO = new ProfileSetDetailsLMSVO();
                for (AddVolumeVO volumeVO : volumeProfileDetailsVO.getVolumeVOList()) {
                    ProfileSetDetailsLMSVO slabVO = new ProfileSetDetailsLMSVO();
                    if (!"".equals((volumeVO.getFromRange()))) {
                        slabVO.setSetId(profileSetVO.getSetId());
                        slabVO.setStartRange(volumeVO.getFromRange());
                        slabVO.setEndRange(volumeVO.getToRange());
                        slabVO.setPoints(volumeVO.getPoints());
                        slabVO.setType(volumeProfileDetailsVO.getModule());
                        slabVO.setServiceCode(volumeProfileDetailsVO.getService());
                        slabVO.setVersion(profileSetVO.getLastVersion());
                        slabVO.setDetailType(PretupsI.PROFILE_TRANS);
                        slabVO.setSubscriberType(PretupsI.SERVICE_TYPE_PRE);
                        slabVO.setUserType(PretupsI.USER_TYPE_CHANNEL);
                        slabVO.setPointsTypeCode(volumeVO.getRewardsType());
                        slabVO.setDetailSubType(PretupsI.NO);
                        slabVO.setMaxLimit(0);
                        slabVO.setMinLimit(0);
                        slabVO.setSlabNo(slabCount++);
                        slabVO.setPeriodId(PretupsI.NO);
                        slabVO.setProductCode(volumeProfileDetailsVO.getProduct());
                        tempList.add(slabVO);
                    }

                }


                ListSorterUtil sort = new ListSorterUtil();
                slabList = (ArrayList) sort.doSort("startRange", null, tempList);
                if (slabList != null && !slabList.isEmpty()) {
                    ProfileSetDetailsLMSVO preSlabVO = (ProfileSetDetailsLMSVO) slabList.get(0);
                    ProfileSetDetailsLMSVO nextSlabVO = null;
                    for (int k = 1, j = slabList.size(); k < j; k++) {
                        nextSlabVO = (ProfileSetDetailsLMSVO) slabList.get(k);
                        if (nextSlabVO.getStartRange() <= preSlabVO.getEndRange()) {
                            String startRangeLabel = PretupsI.START_RANGE;
                            String endRangeLabel = PretupsI.END_RANGE;
                            String[] arr = {startRangeLabel, String.valueOf(nextSlabVO.getSlabNo()), endRangeLabel, String.valueOf(preSlabVO.getSlabNo())};
                            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.VOUCHER_FROMTO_SERIALNO_INVALID, arr);
                        }
                        preSlabVO = nextSlabVO;
                    }
                }

                activationProfileCombinedVO.setSlabsList(slabList);
                combinedList.add(activationProfileCombinedVO);
            }


        }


        if ((requestVO.getTransDetailsVOList() != null && !requestVO.getTransDetailsVOList().isEmpty()) && requestVO.getPromotionType().equalsIgnoreCase(PretupsI.LMS_PROMOTION_TYPE_STOCK)) {
            int theFormVolumeProfileLists = requestVO.getTransDetailsVOList().size();
            for (int i = 0; i < theFormVolumeProfileLists; i++) {
                ActivationProfileCombinedLMSVO activationProfileCombinedVO = new ActivationProfileCombinedLMSVO();
                AddVolumeProfileDetailsVO volumeProfileDetailsVO = (AddVolumeProfileDetailsVO) requestVO.getTransDetailsVOList().get(i);
                ProfileSetDetailsLMSVO preSlabVO = null;
                ProfileSetDetailsLMSVO setDetailsVO = null;
                ProfileSetDetailsLMSVO nextSlabVO = null;
                ArrayList slabList = new ArrayList();
                ArrayList volSlabList = new ArrayList();
                int slabCount = 1;
                for (AddVolumeVO volumeVO : volumeProfileDetailsVO.getVolumeVOList()) {
                    ProfileSetDetailsLMSVO slabVO = new ProfileSetDetailsLMSVO();
                    if (requestVO.getReferenceBased() != null && requestVO.getReferenceBased().equals(PretupsI.NO)) {
                        if (!(BTSLUtil.isNullString(String.valueOf(volumeVO.getFromRange())))) {
                            slabVO.setSetId(profileSetVO.getSetId());
                            slabVO.setStartRange(volumeVO.getTarget());
                            slabVO.setEndRange(volumeVO.getTarget());
                            slabVO.setPoints(volumeVO.getPoints());
                            slabVO.setType(volumeProfileDetailsVO.getModule());
                            slabVO.setServiceCode(volumeProfileDetailsVO.getService());
                            slabVO.setPeriodType(volumeVO.getFrequency());
                            slabVO.setPeriodId(volumeVO.getFrequency());
                            slabVO.setVersion(profileSetVO.getLastVersion());
                            slabVO.setDetailType(PretupsI.PROFILE_VOL);
                            slabVO.setSubscriberType(PretupsI.SERVICE_TYPE_PRE);
                            slabVO.setUserType(PretupsI.USER_TYPE_CHANNEL);
                            slabVO.setDetailSubType(PretupsI.AMOUNT_TYPE_AMOUNT);
                            slabVO.setPointsTypeCode(volumeVO.getRewardsType());
                            slabVO.setTargetType(volumeVO.getTargetType());
                            slabVO.setMaxLimit(0);
                            slabVO.setMinLimit(0);
                            slabVO.setProductCode(volumeProfileDetailsVO.getProduct());
                            slabVO.setSlabNo(slabCount++);
                            slabList.add(slabVO);
                        }
                    } else {
                        if (!(BTSLUtil.isNullString(String.valueOf(volumeVO.getFromRange())))) {
                            slabVO.setSetId(profileSetVO.getSetId());
                            slabVO.setEndRangeAsString(String.valueOf(volumeVO.getTarget()));
                            slabVO.setStartRange(volumeVO.getTarget());
                            slabVO.setEndRange(volumeVO.getTarget());
                            slabVO.setPoints(volumeVO.getPoints());
                            slabVO.setServiceCode(volumeProfileDetailsVO.getService());
                            slabVO.setType(volumeProfileDetailsVO.getModule());
                            slabVO.setVersion(profileSetVO.getLastVersion());
                            slabVO.setPeriodType(volumeVO.getFrequency());
                            slabVO.setPeriodId(volumeVO.getFrequency());
                            slabVO.setDetailType(PretupsI.PROFILE_VOL);
                            slabVO.setSubscriberType(PretupsI.SERVICE_TYPE_PRE);
                            slabVO.setUserType(PretupsI.USER_TYPE_CHANNEL);
                            slabVO.setPointsTypeCode(volumeVO.getRewardsType());
                            if ("Y".equals(requestVO.getReferenceBased())) {
                                slabVO.setDetailSubType(volumeVO.getTargetType());
                            } else {
                                slabVO.setDetailSubType(PretupsI.AMOUNT_TYPE_AMOUNT);
                            }

                            slabVO.setMaxLimit(0);
                            slabVO.setMinLimit(0);
                            slabVO.setProductCode(volumeProfileDetailsVO.getProduct());
                            slabVO.setSlabNo(slabCount++);
                            volSlabList.add(slabVO);
                        }
                    }
                }
                if (((slabList == null || slabList.isEmpty()) && (volSlabList == null || volSlabList.isEmpty()))) {
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.AT_LEAST_ONE_SLAB_ENTRY_IS_REQUIRED);
                }
                ListSorterUtil periodIdSort = new ListSorterUtil();
                ListSorterUtil startRangeSort = new ListSorterUtil();
                slabList = (ArrayList) periodIdSort.doSort("periodId", null, slabList);
                /*
                 * now we have slabs list sorted on the period
                 * type(daily,weekly,monthly,unlimited) of the slabs
                 * we compare the period id s of prevslabVO and nextslabVO
                 */

                ArrayList tempList = new ArrayList();
                ArrayList correctedList = new ArrayList();
                if (slabList != null && !slabList.isEmpty()) {
                    // first slab
                    preSlabVO = (ProfileSetDetailsLMSVO) slabList.get(0);
                }
                /* add first slab to temp list */
                tempList.add(preSlabVO);
                int slaLists = slabList.size();
                for (int k = 1, j = slaLists; k < j; k++) {
                    nextSlabVO = (ProfileSetDetailsLMSVO) slabList.get(k);
                    if (nextSlabVO.getPeriodId().equals(preSlabVO.getPeriodId())) {
                        /*
                         * add slabs to slabs list until period type of next slab is
                         * different
                         */
                        tempList.add(nextSlabVO);
                    } else {
                        /*
                         * periodtype of the next slab is different now we have all
                         * slabs of one period id in templist
                         * sort on basis of start range and add to corrected list
                         */
                        tempList = (ArrayList) startRangeSort.doSort("startRange", null, tempList);
                        correctedList.addAll(tempList);
                        tempList = new ArrayList();
                        tempList.add(nextSlabVO);
                    }
                    if ((k + 1) == j) {
                        tempList = (ArrayList) startRangeSort.doSort("startRange", null, tempList);
                        correctedList.addAll(tempList);
                    }
                    preSlabVO = nextSlabVO;
                }
                if (slabList.size() > 1) {
                    slabList = correctedList;
                }
                // for total count slabs check start range and end range don't
                // overlap of two different slabs
                if (slabList != null && !slabList.isEmpty()) {
                    preSlabVO = (ProfileSetDetailsLMSVO) slabList.get(0);
                    for (int n = 1, j = slabList.size(); n < j; n++) {
                        nextSlabVO = (ProfileSetDetailsLMSVO) slabList.get(n);
                        if (nextSlabVO.getStartRange() <= preSlabVO.getEndRange()) {
                            /*
                             * throw exception only if periodids of the prev and
                             * next slabs match
                             */
                            if (nextSlabVO.getPeriodId().equals(preSlabVO.getPeriodId())) {
                                String startRangeLabel = PretupsI.START_RANGE;
                                String endRangeLabel = PretupsI.END_RANGE;
                                String[] arr = {startRangeLabel, String.valueOf(nextSlabVO.getSlabNo()), endRangeLabel, String.valueOf(preSlabVO.getSlabNo())};
                                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.COUNT_SLAB_SHOULD_BE_GREATER_THAN_BEFORE, arr);
                            }
                        }
                        preSlabVO = nextSlabVO;
                    }
                }
                volSlabList = (ArrayList) periodIdSort.doSort("periodId", null, volSlabList);
                /*
                 * now we have slabs list sorted on the period
                 * type(daily,weekly,monthly,unlimited) of the slabs
                 * we compare the period id s of prevslabVO and nextslabVO
                 */
                tempList = new ArrayList();
                correctedList = new ArrayList();
                if (volSlabList != null && !volSlabList.isEmpty()) {
                    preSlabVO = (ProfileSetDetailsLMSVO) volSlabList.get(0);
                }
                tempList.add(preSlabVO);
                int volSlabsLists = volSlabList.size();
                for (int l = 1, j = volSlabsLists; l < j; l++) {
                    nextSlabVO = (ProfileSetDetailsLMSVO) volSlabList.get(l);
                    if (nextSlabVO.getPeriodId().equals(preSlabVO.getPeriodId())) {
                        tempList.add(nextSlabVO);
                    } else {
                        tempList = (ArrayList) startRangeSort.doSort("startRange", null, tempList);
                        correctedList.addAll(tempList);
                        tempList = new ArrayList();
                        tempList.add(nextSlabVO);
                    }
                    if ((l + 1) == j) {
                        tempList = (ArrayList) startRangeSort.doSort("startRange", null, tempList);
                        correctedList.addAll(tempList);
                    }
                    preSlabVO = nextSlabVO;
                }
                if (volSlabList.size() > 1) {
                    volSlabList = correctedList;
                }
                // for total count slabs check start range and end range don't
                // overlap of two different slabs
                if (volSlabList != null && !volSlabList.isEmpty()) {
                    preSlabVO = (ProfileSetDetailsLMSVO) volSlabList.get(0);
                    for (int m = 1, j = volSlabList.size(); m < j; m++) {
                        nextSlabVO = (ProfileSetDetailsLMSVO) volSlabList.get(m);
                        if (nextSlabVO.getStartRange() <= preSlabVO.getEndRange()) {
                            if (nextSlabVO.getPeriodId().equals(preSlabVO.getPeriodId())) {
                                String startRangeLabel = PretupsI.START_RANGE;
                                String endRangeLabel = PretupsI.END_RANGE;
                                String[] arr = {startRangeLabel, String.valueOf(nextSlabVO.getSlabNo()), endRangeLabel, String.valueOf(preSlabVO.getSlabNo())};
                                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.AMOUNT_SLAB_SHOULD_BE_GREATER_THAN_BEFORE, arr);
                            }
                        }
                        preSlabVO = nextSlabVO;
                    }
                }
                activationProfileCombinedVO.setCountSlabsList(slabList);
                activationProfileCombinedVO.setAmountSlabsList(volSlabList);
                combinedList.add(activationProfileCombinedVO);
            }
        }
        for (ActivationProfileCombinedLMSVO combinedLMSVO : combinedList) {
            if (combinedLMSVO.getAmountSlabsList() != null) {
                list.addAll(combinedLMSVO.getAmountSlabsList());
            }
            if (combinedLMSVO.getCountSlabsList() != null) {
                list.addAll(combinedLMSVO.getCountSlabsList());
            }
            if (combinedLMSVO.getSlabsList() != null) {
                list.addAll(combinedLMSVO.getSlabsList());
            }
        }


        int insertdetailCount = activationBonusLMSWebDAO.addActivationBonusSetDetail(con, list, profileSetVO, updateVersion);

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Exiting:insertdetailCount" + insertdetailCount);
        }
    }

    private void addProfileValidation(Connection con, UserVO userVO, AddProfileDetailsRequestVO requestVO, String module) throws Exception {

        final String METHOD_NAME = "addProfileValidation";

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        NetworkProductDAO networkProductDAO = new NetworkProductDAO();
        ActivationBonusLMSWebDAO activationBonusLMSWebDAO = new ActivationBonusLMSWebDAO();
        ArrayList moduleList = LookupsCache.loadLookupDropDown(PretupsI.LMS_SERVICE, true);
        ArrayList subscriberList = LookupsCache.loadLookupDropDown(PretupsI.ACTIVATION_SUBSCRIBER_TYPE, true);
        ArrayList targetTypeList = LookupsCache.loadLookupDropDown(PretupsI.TARGET_TYPE, true);
        ArrayList periodTypeList = LookupsCache.loadLookupDropDown(PretupsI.PERIOD_TYPE, true);
        ArrayList pointTypeList = LookupsCache.loadLookupDropDown(PretupsI.AMOUNT_TYPE, true);
        ArrayList<ListValueVO> allServiceList = activationBonusLMSWebDAO.loadLookupServicesList(con);
        ArrayList prodServiceMapList = networkProductDAO.loadProductServiceMapping();
        ArrayList productList = networkProductDAO.loadProductList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE);
        List promotionList = List.of(PretupsI.STOCK, PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT);
        String format = Constants.getProperty("LMS_PROFILE_DATE_FORMAT");
        Date fromDate = BTSLUtil.getDateFromDateString(requestVO.getApplicableFromDate(), format);
        Date toDate = BTSLUtil.getDateFromDateString(requestVO.getApplicableToDate(), format);
        Date currentDate= new Date(System.currentTimeMillis());

        final long diff = toDate.getTime() - fromDate.getTime();
        if (diff / (60 * 60 * 1000) < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_HRDIF_ST_ED_LMS))).intValue()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.FROM_DATE_VALIDATION);
        }
        final long diff1 = fromDate.getTime() - BTSLUtil.getTimestampFromUtilDate(currentDate).getTime();
        if (diff1 / (60 * 60 * 1000) < ((Integer) (PreferenceCache.getSystemPreferenceValue(PreferenceI.MIN_HRDIF_CR_ST_LMS))).intValue()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.CURRENT_DATE_VALIDATION);
        }
        if (module.equals(PretupsI.ADD_LMS)) {
            if (BTSLUtil.isEmpty(requestVO.getPromotionType())) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.PROMOTION_TYPE, new String[]{PretupsI.REQIRED});
            }
            if (!promotionList.contains(requestVO.getPromotionType())) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.PROMOTION_TYPE, new String[]{PretupsI.INVALID});
            }
            if (BTSLUtil.isEmpty(requestVO.getProfileName())) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.PROFILE_NAME_IS_REQUIRED);
            }
            if (requestVO.getOptInOutService() != null && !requestVO.getOptInOutService().isEmpty()) {

                if (!requestVO.getOptInOutService().equals(PretupsI.YES) && !requestVO.getOptInOutService().equals(PretupsI.NO)) {
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.OPTINOUTTARGET, new String[]{PretupsI.INVALID});
                }
            }
        } else {

        }
        if (BTSLUtil.isEmpty(requestVO.getApplicableFromDate())) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.APPLICABLEFROM_DATE, new String[]{PretupsI.REQIRED});
        }
        if (!BTSLUtil.isEmpty(requestVO.getApplicableFromDate())) {
            try {
                LocalDate.parse(requestVO.getApplicableFromDate(), DateTimeFormatter.ofPattern(format));
            } catch (Exception ex) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.APPLICABLEFROM_DATE, new String[]{PretupsI.INVALID});
            }
        }
        if (BTSLUtil.isEmpty(requestVO.getApplicableToDate())) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.APPLICABLETO_DATE, new String[]{PretupsI.REQIRED});
        }
        if (!BTSLUtil.isEmpty(requestVO.getApplicableToDate())) {
            try {
                LocalDate.parse(requestVO.getApplicableToDate(), DateTimeFormatter.ofPattern(format));
            } catch (Exception ex) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.APPLICABLETO_DATE, new String[]{PretupsI.INVALID});
            }
        }

        if (requestVO.getMsgConfigEnabled() != null && !requestVO.getMsgConfigEnabled().isEmpty()) {

            if (!requestVO.getMsgConfigEnabled().equals(PretupsI.YES) && !requestVO.getMsgConfigEnabled().equals(PretupsI.NO)) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MSGCONFIGENABLED, new String[]{PretupsI.INVALID});
            }
        }
        if (requestVO.getReferenceBased() != null && !requestVO.getReferenceBased().isEmpty()) {

            if (!requestVO.getReferenceBased().equals(PretupsI.YES) && !requestVO.getReferenceBased().equals(PretupsI.NO)) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.REFERENCEBASED, new String[]{PretupsI.INVALID});
            }
        }
        if ((requestVO.getTransDetailsVOList() != null && !requestVO.getTransDetailsVOList().isEmpty()) && requestVO.getPromotionType().equals(PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT )) {
            for (AddVolumeProfileDetailsVO vo : requestVO.getTransDetailsVOList()) {
                if (BTSLUtil.isEmpty(vo.getModule())) {
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MODULE, new String[]{PretupsI.REQIRED});
                }
                if (BTSLUtil.getOptionDesc(vo.getModule(), moduleList).getValue() == null) {
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MODULE, new String[]{PretupsI.INVALID});

                }
                if (BTSLUtil.isEmpty(vo.getService())) {
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE, new String[]{PretupsI.REQIRED});
                }
                boolean isServicepresent = false;
                for (ListValueVO serviceVO : allServiceList) {
                    if (serviceVO.getValue().split(":")[0].equals(vo.getService())) {
                        isServicepresent = true;
                        break;
                    }
                }
                if (!isServicepresent) {
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE, new String[]{PretupsI.INVALID});
                }
                if (BTSLUtil.isEmpty(vo.getProduct())) {
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.PRODUCT, new String[]{PretupsI.REQIRED});
                }
                if (BTSLUtil.getOptionDesc(vo.getProduct(), productList).getValue() == null) {
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.PRODUCT, new String[]{PretupsI.INVALID});
                }
                for (AddVolumeVO volumeVO : vo.getVolumeVOList()) {
                    if (BTSLUtil.isEmpty(volumeVO.getRewardsType())) {
                        throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.REWARDSTYPE, new String[]{PretupsI.REQIRED});
                    }
                    if (BTSLUtil.getOptionDesc(volumeVO.getRewardsType(), pointTypeList).getValue() == null) {
                        throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.REWARDSTYPE, new String[]{PretupsI.INVALID});
                    }
                    if(volumeVO.getFromRange()>volumeVO.getToRange()){
                        throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.BULKCOMMPROFILE_PROCESSUPLOADEDFILE_ERROR_START_RANGEVALUE);
                    }
                }
            }
        }
        if ((requestVO.getTransDetailsVOList() != null && !requestVO.getTransDetailsVOList().isEmpty()) && requestVO.getPromotionType().equals(PretupsI.LMS_PROMOTION_TYPE_STOCK )) {
            for (AddVolumeProfileDetailsVO vo : requestVO.getTransDetailsVOList()) {
                if (BTSLUtil.isEmpty(vo.getModule())) {
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MODULE, new String[]{PretupsI.REQIRED});
                }
                if (BTSLUtil.getOptionDesc(vo.getModule(), moduleList).getValue() == null) {
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MODULE, new String[]{PretupsI.INVALID});

                }
                if (BTSLUtil.isEmpty(vo.getService())) {
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE, new String[]{PretupsI.REQIRED});
                }
                boolean isServicePresent = false;
                for (int i = 0; i < allServiceList.size(); i++) {
                    ListValueVO valueVO = (ListValueVO) allServiceList.get(i);
                    if (valueVO.getValue().split(":")[0].equals(vo.getService())) {
                        isServicePresent = true;
                    }
                    if (vo.getService().equals(PretupsI.ALL))
                        isServicePresent = true;
                }
                if (!isServicePresent) {
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE, new String[]{PretupsI.INVALID});
                }
                if (BTSLUtil.isEmpty(vo.getProduct())) {
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.PRODUCT, new String[]{PretupsI.REQIRED});
                }
                if (BTSLUtil.getOptionDesc(vo.getProduct(), productList).getValue() == null) {
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.PRODUCT, new String[]{PretupsI.INVALID});
                }
                for (AddVolumeVO volumeVO : vo.getVolumeVOList()) {
                    if (BTSLUtil.isEmpty(volumeVO.getRewardsType())) {
                        throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.REWARDSTYPE, new String[]{PretupsI.REQIRED});
                    }
                    if (BTSLUtil.getOptionDesc(volumeVO.getRewardsType(), pointTypeList).getValue() == null) {
                        throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.REWARDSTYPE, new String[]{PretupsI.INVALID});
                    }
                    if (BTSLUtil.isEmpty(volumeVO.getFrequency())) {
                        throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.FREQUENCY, new String[]{PretupsI.REQIRED});
                    }
                    if (BTSLUtil.getOptionDesc(volumeVO.getFrequency(), periodTypeList).getValue() == null) {
                        throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.FREQUENCY, new String[]{PretupsI.INVALID});
                    }
                    if (BTSLUtil.isEmpty(volumeVO.getTargetType())) {
                        throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.TARGETTYPE, new String[]{PretupsI.REQIRED});
                    }
                    if (BTSLUtil.getOptionDesc(volumeVO.getTargetType(), targetTypeList).getValue() == null) {
                        throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.TARGETTYPE, new String[]{PretupsI.INVALID});
                    }

                }
            }
        }
    }


    @Override
    public BaseResponse deleteLmsProfile(Connection con, MComConnectionI mcomCon, UserVO userVO, DeleteLmsProfileRequestVO requestVO, BaseResponse response) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "deleteLmsProfile";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }

        ActivationBonusLMSWebDAO activationBonusLMSWebDAO;
        boolean deleteProfileSet = false;
        boolean deleteProfileVersion = false;

        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        try {
            activationBonusLMSWebDAO = new ActivationBonusLMSWebDAO();

            boolean isProfileAssociated = false;
            String set_ID = requestVO.getSetId();
            String set_ID_version = requestVO.getSetIdVersion();

            if (activationBonusLMSWebDAO.isprofileSingleVersionExist(con, set_ID) == 1) {
                deleteProfileSet = true;
            } else {
                deleteProfileVersion = true;
            }

            if (activationBonusLMSWebDAO.isprofileAssociated(con, set_ID)) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.PROFILE_ALREADY_ASSOCIATED, 0, null);
            } else {
                boolean deleteDatabaseSuccess = false;

                deleteDatabaseSuccess = activationBonusLMSWebDAO.deleteProfileSetVersion(con, set_ID, set_ID_version, userVO, deleteProfileSet, deleteProfileVersion);
                if (!deleteDatabaseSuccess) {
                    mcomCon.partialRollback();

                } else {
                    mcomCon.partialCommit();

                    String arr[] = new String[1];
                    arr[0] = requestVO.getSetId();

                    response.setStatus((HttpStatus.SC_OK));
                    String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.PROFILE_DELETED_SUCCESSFULLY, null);
                    response.setMessage(resmsg);
                    response.setMessageCode(PretupsErrorCodesI.PROFILE_DELETED_SUCCESSFULLY);


                }
            }
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
            }
        }
        return response;
    }

    @Override
    public BaseResponse suspendProfileDetails(Connection con, UserVO userVO, SuspendRequestVO requestVO, BaseResponse response) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "suspendProfileDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        if (requestVO.getSetId() == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SET_ID_IS_NULL);
        }
        if (BTSLUtil.isEmpty(requestVO.getSetId())) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SET_ID_IS_EMPTY);
        }


        if (requestVO.getVersion() == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.VERSION_IS_NULL);
        }
        if (BTSLUtil.isEmpty(requestVO.getVersion())) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.VERSION_IS_EMPTY);
        }

        if (!requestVO.getSetId().matches(PretupsI.NUMERIC_TYPE)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SET_ID_IS_INVALID_FORMAT);
        }
        if (!requestVO.getVersion().matches(PretupsI.NUMERIC_TYPE)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.VERSION_IS_INVALID_FORMAT);
        }

        ActivationBonusLMSWebDAO activationBonusLMSWebDAO = new ActivationBonusLMSWebDAO();

        ArrayList<ProfileSetVersionLMSVO> versionList = activationBonusLMSWebDAO.loadVersionsList(con, requestVO.getSetId(), null);
        if (versionList.isEmpty()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_SET_ID_INVALID);
        }
        String version = String.valueOf(requestVO.getVersion());
        boolean isVersionpresent = false;
        for (ProfileSetVersionLMSVO profileSetVersionLMSVO : versionList) {
            if (profileSetVersionLMSVO.getVersion().equals(version)) {
                isVersionpresent = true;
                break;
            }
        }
        ProfileSetLMSVO profileSetLMSVO = activationBonusLMSWebDAO.loadProfileDetails(con, userVO.getNetworkID(), requestVO.getSetId(), version);

        if (!isVersionpresent ) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.VERSION_INVALID);
        }
        if (profileSetLMSVO == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SET_ID_INVALID);
        }
        boolean suspendDatabaseSuccess = false;
        int onlySingleVersionExist = 0;
        boolean deleteProfileSet = true;
       boolean deleteProfileVersion = true;

        if (activationBonusLMSWebDAO.isprofileExpired(con, requestVO.getSetId(),version )) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.PROFILE_CANNT_SUSPENDED_IT_IS_EXPIRED);
        }
        if (activationBonusLMSWebDAO.isprofileActive(con,requestVO.getSetId(), version)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.PROFILE_CANNT_SUSPENDED_IT_IS_NOT_ACTIVE);
        }
        onlySingleVersionExist = activationBonusLMSWebDAO.isprofileSingleVersionExist(con, requestVO.getSetId());
        if (onlySingleVersionExist > 1) {
            deleteProfileSet = false;
        }

        suspendDatabaseSuccess = activationBonusLMSWebDAO.suspendProfileSetVersion(con, requestVO.getSetId(), version, userVO, deleteProfileSet, deleteProfileVersion);

        if (!suspendDatabaseSuccess) {
            con.rollback();
           throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR);

        } else {
            Date currentDate = new Date(System.currentTimeMillis());
            con.commit();
            Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
            String arr[] = {profileSetLMSVO.getSetName()};
            String resmsg = RestAPIStringParser.getMessage(locale,
                    PretupsErrorCodesI.PROFILE_SUSPENDED_SUCCESSFULLY, arr);
            final AdminOperationVO adminOperationVO = new AdminOperationVO();
            adminOperationVO.setSource(PretupsI.LOGGER_LMS_SOURCE);
            adminOperationVO.setDate(currentDate);
            adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_SUSPENDED);
            adminOperationVO.setInfo(resmsg);
            adminOperationVO.setLoginID(userVO.getLoginID());
            adminOperationVO.setUserID(userVO.getUserID());
            adminOperationVO.setCategoryCode(userVO.getCategoryCode());
            adminOperationVO.setNetworkCode(userVO.getNetworkID());
            adminOperationVO.setMsisdn(userVO.getMsisdn());
            AdminOperationLog.log(adminOperationVO);
            response.setStatus((HttpStatus.SC_OK));
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.PROFILE_MODIFIED_SUCCESSFULLY);
            return response;

        }

    }

    @Override
    public BaseResponse approveProfileDetails(Connection con, UserVO userVO, ApproveProfileRequestVO requestVO, BaseResponse response) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "approveProfileDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }

        if (requestVO.getSetId() == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SET_ID_IS_NULL);
        }
        if (BTSLUtil.isEmpty(requestVO.getSetId())) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SET_ID_IS_EMPTY);
        }


        if (requestVO.getVersion() == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.VERSION_IS_NULL);
        }
        if (BTSLUtil.isEmpty(requestVO.getVersion())) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.VERSION_IS_EMPTY);
        }

        if (!requestVO.getSetId().matches(PretupsI.NUMERIC_TYPE)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SET_ID_IS_INVALID_FORMAT);
        }
        if (!requestVO.getVersion().matches(PretupsI.NUMERIC_TYPE)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.VERSION_IS_INVALID_FORMAT);
        }
        boolean rejectProfileVersion =false;
        Date currentDate = new Date(System.currentTimeMillis());
        if(!requestVO.isApproveStatus()) {
            rejectProfileVersion = true;
        }
        ActivationBonusLMSWebDAO activationBonusLMSWebDAO= new ActivationBonusLMSWebDAO();
        ArrayList<ProfileSetVersionLMSVO> versionList = activationBonusLMSWebDAO.loadVersionsList(con, requestVO.getSetId(), null);
        if (versionList.isEmpty()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_SET_ID_INVALID);
        }
        String version = String.valueOf(requestVO.getVersion());
        boolean isVersionpresent = false;
        for (ProfileSetVersionLMSVO profileSetVersionLMSVO : versionList) {
            if (profileSetVersionLMSVO.getVersion().equals(version)) {
                isVersionpresent = true;
                break;
            }
        }
        ProfileSetLMSVO profileSetLMSVO = activationBonusLMSWebDAO.loadProfileDetails(con, userVO.getNetworkID(), requestVO.getSetId(), version);

        if (!isVersionpresent ) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.VERSION_INVALID);
        }
        if (profileSetLMSVO == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SET_ID_INVALID);
        }
        boolean approveSuccess = false;
        String profileSetId = requestVO.getSetId();
        String profileVersionID = String.valueOf(requestVO.getVersion());
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        String arr[] = {profileSetLMSVO.getSetName()};
        approveSuccess = activationBonusLMSWebDAO.approveRejectProfileSet(con, profileSetId, profileVersionID, userVO, requestVO.isApproveStatus(), rejectProfileVersion);
        if (!approveSuccess) {
            con.rollback();
            throw  new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR);
        } else {
            final AdminOperationVO adminOperationVO = new AdminOperationVO();
            String resmsg = null;
            if(requestVO.isApproveStatus()) {
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_APPROVE);
                resmsg = RestAPIStringParser.getMessage(locale,
                        PretupsErrorCodesI.PROFILE_APPROVE_SUCCESSFULLY, arr);
                response.setMessageCode(PretupsErrorCodesI.PROFILE_APPROVE_SUCCESSFULLY);
                
            }
            else{
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_REJECT);
                resmsg = RestAPIStringParser.getMessage(locale,
                        PretupsErrorCodesI.PROFILE_REJECTED_SUCCESSFULLY, arr);
                response.setMessageCode(PretupsErrorCodesI.PROFILE_REJECTED_SUCCESSFULLY);
                

            }
            con.commit();
            adminOperationVO.setSource(PretupsI.LOGGER_LMS_SOURCE);
            adminOperationVO.setDate(currentDate);
            adminOperationVO.setInfo(resmsg);
            adminOperationVO.setLoginID(userVO.getLoginID());
            adminOperationVO.setUserID(userVO.getUserID());
            adminOperationVO.setCategoryCode(userVO.getCategoryCode());
            adminOperationVO.setNetworkCode(userVO.getNetworkID());
            adminOperationVO.setMsisdn(userVO.getMsisdn());
            AdminOperationLog.log(adminOperationVO);
            response.setStatus((HttpStatus.SC_OK));
            response.setMessage(resmsg);
            return response;
        }
    }

    @Override
    public ProfileMessageDetailsVO getProfileMessageDetails(Connection con, UserVO userVO, SuspendRequestVO requestVO, ProfileMessageDetailsVO response) throws BTSLBaseException, Exception {

        final String METHOD_NAME = "getProfileMessageDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        Date currentDate = new Date(System.currentTimeMillis());
        if (requestVO.getSetId() == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SET_ID_IS_NULL);
        }
        if (BTSLUtil.isEmpty(requestVO.getSetId())) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SET_ID_IS_EMPTY);
        }


        if (requestVO.getVersion() == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.VERSION_IS_NULL);
        }
        if (BTSLUtil.isEmpty(requestVO.getVersion())) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.VERSION_IS_EMPTY);
        }

        if (!requestVO.getSetId().matches(PretupsI.NUMERIC_TYPE)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SET_ID_IS_INVALID_FORMAT);
        }
        if (!requestVO.getVersion().matches(PretupsI.NUMERIC_TYPE)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.VERSION_IS_INVALID_FORMAT);
        }
        ActivationBonusLMSWebDAO activationBonusLMSWebDAO= new ActivationBonusLMSWebDAO();
        ArrayList<ProfileSetVersionLMSVO> versionList = activationBonusLMSWebDAO.loadVersionsList(con, requestVO.getSetId(), null);
        if (versionList.isEmpty()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_SET_ID_INVALID);
        }
        String version = String.valueOf(requestVO.getVersion());
        boolean isVersionpresent = false;
        for (ProfileSetVersionLMSVO profileSetVersionLMSVO: versionList) {
            if (profileSetVersionLMSVO.getVersion().equals(version)) {
                isVersionpresent = true;
                break;
            }
        }
        ProfileSetLMSVO profileSet = activationBonusLMSWebDAO.loadProfileDetails(con, userVO.getNetworkID(), requestVO.getSetId(), version);

        if (!isVersionpresent ) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.VERSION_INVALID);
        }
        if (profileSet == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SET_ID_INVALID);
        }
        ProfileSetLMSVO profileSetLMSVO = activationBonusLMSWebDAO.loadMessageList(con, requestVO.getSetId());
        if(profileSetLMSVO.getMsgConfEnableFlag() != null && profileSetLMSVO.getMsgConfEnableFlag().equalsIgnoreCase(PretupsI.YES)) {
            String appender = PretupsI.M_CLASS_AND_P_ID;
            String lang1WelMessage = null;
            if (profileSetLMSVO.getLang1welcomemsg().contains(appender)) {
                lang1WelMessage = profileSetLMSVO.getLang1welcomemsg();
            }
            response.setWelcomeMesage1(lang1WelMessage);

            //appender = PretupsI.M_CLASS_AND_P_ID + PretupsI.WEL_MESSAGE_LANG2 + PretupsI.COLON;

            String lang2WelMessage = null;
            if (profileSetLMSVO.getLang2welcomemsg().contains(appender)) {
                lang2WelMessage = profileSetLMSVO.getLang2welcomemsg();
            }
            response.setWelcomeMesage2(lang2WelMessage);
            if (profileSetLMSVO.getPromotionType().equals(PretupsI.STOCK)) {
                appender = PretupsI.M_CLASS_AND_P_ID + PretupsI.SUCCESS_MESSAGE_LANG1 + PretupsI.COLON;
                final String lang1SucMessage = profileSetLMSVO.getLang1seccessmsg().replace(appender, "");
                response.setSuccessMessage1(lang1SucMessage);

                appender = PretupsI.M_CLASS_AND_P_ID + PretupsI.SUCCESS_MESSAGE_LANG2 + PretupsI.COLON;
                final String lang2SucMessage = profileSetLMSVO.getLang2seccessmsg().replace(appender, "");
                response.setSuccessMessage2(lang2SucMessage);

                appender = PretupsI.M_CLASS_AND_P_ID + PretupsI.FAILURE_MESSAGE_LANG1 + PretupsI.COLON;
                final String lang1FailMessage = profileSetLMSVO.getLang1failuremsg().replace(appender, "");
                response.setFailureMessage1(lang1FailMessage);

                appender = PretupsI.M_CLASS_AND_P_ID + PretupsI.FAILURE_MESSAGE_LANG2 + PretupsI.COLON;
                final String lang2FailMessage = profileSetLMSVO.getLang2failuremsg().replace(appender, "");
                response.setFailureMessage2(lang2FailMessage);
            }
            response.setVersion(String.valueOf(requestVO.getVersion()));
            response.setSetID(requestVO.getSetId());
            response.setProfileName(profileSet.getSetName());
            response.setStatus((HttpStatus.SC_OK));
            Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
            String resmsg = RestAPIStringParser.getMessage(locale,
                    PretupsErrorCodesI.LOAD_PROFILE_MESSAGES_SUCCESSFULLY, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.LOAD_PROFILE_MESSAGES_SUCCESSFULLY);
        }
        else {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.MSG_CONFIG_NOT_ENBLE, new String[]{profileSet.getSetName()});
        }
        return response;
    }

    @Override
    public BaseResponse updateMessageDetails(Connection con, UserVO userVO, UpdateMessageProfileRequestVO requestVO, BaseResponse response) throws BTSLBaseException, Exception {

        final String METHOD_NAME = "updateMessageDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        if (requestVO.getSetID() == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SET_ID_IS_NULL);
        }
        if (BTSLUtil.isEmpty(requestVO.getSetID())) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SET_ID_IS_EMPTY);
        }

        if (!requestVO.getSetID().matches(PretupsI.NUMERIC_TYPE)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SET_ID_IS_INVALID_FORMAT);
        }

        ActivationBonusLMSWebDAO activationBonusLMSWebDAO= new ActivationBonusLMSWebDAO();
        ArrayList<ProfileSetVersionLMSVO> versionList = activationBonusLMSWebDAO.loadVersionsList(con, requestVO.getSetID(), null);
        if (versionList.isEmpty()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_SET_ID_INVALID);
        }

        Date currentDate = new Date(System.currentTimeMillis());
        final HashMap<String, String> profileDetails = activationBonusLMSWebDAO.fetchLMSProfleDetails(con, requestVO.getSetID());
        ProfileSetLMSVO profileSetLMSVO = new ProfileSetLMSVO();
        String promotionType = null;
        String optInOutEnabled = null;
        String messageManagementEnabled = null;
        if (profileDetails != null) {
            promotionType = profileDetails.get("PROMOTION_TYPE");
            optInOutEnabled = profileDetails.get("OPT_IN_OUT_ENABLED");
            messageManagementEnabled = profileDetails.get("MESSAGE_MANAGEMENT_ENABLED");
            profileSetLMSVO.setPromotionType(promotionType);
            profileSetLMSVO.setOptInOut(optInOutEnabled);
            profileSetLMSVO.setMsgConfEnableFlag(messageManagementEnabled);
        }
        if (PretupsI.LMS_PROMOTION_TYPE_STOCK.equals(promotionType)) {
            if (BTSLUtil.isNullString(requestVO.getWelcomeMesage1()) || BTSLUtil.isNullString(requestVO.getWelcomeMesage2()) || BTSLUtil.isNullString(requestVO
                    .getSuccessMessage1()) || BTSLUtil.isNullString(requestVO.getSuccessMessage2()) || BTSLUtil.isNullString(requestVO.getFailureMessage1()) || BTSLUtil
                    .isNullString(requestVO.getFailureMessage2())) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME,PretupsErrorCodesI.ENTER_MESSAGE_BODY);

            }
        } else if (PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equals(promotionType)) {
            if (BTSLUtil.isNullString(requestVO.getWelcomeMesage1()) || BTSLUtil.isNullString(requestVO.getWelcomeMesage2())) {
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.ENTER_MESSAGE_BODY);
            }
        }
        StringBuffer appender = new StringBuffer(PretupsI.M_CLASS_AND_P_ID + PretupsI.WEL_MESSAGE_LANG1 + PretupsI.COLON);
        StringBuffer appender2 = new StringBuffer(PretupsI.M_CLASS_AND_P_ID + PretupsI.OPTINOUT_WEL_MESSAGE_LANG1 + PretupsI.COLON);
        StringBuffer appender3 = new StringBuffer(PretupsI.M_CLASS_AND_P_ID + PretupsI.OPTINOUT_TRA_WEL_MSG_LANG1 + PretupsI.COLON);
        StringBuffer appender4 = new StringBuffer(PretupsI.M_CLASS_AND_P_ID + PretupsI.TRA_WEL_MSG_LANG1 + PretupsI.COLON);
        String lang1WelMessage = null;
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue() && PretupsI.LMS_PROMOTION_TYPE_STOCK.equalsIgnoreCase(promotionType) && PretupsI.SELECT_CHECKBOX
                .equalsIgnoreCase(optInOutEnabled) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(messageManagementEnabled)) {
            appender2.append(requestVO.getWelcomeMesage1());
            lang1WelMessage = appender2.toString();
        } else if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue() && PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(promotionType) && PretupsI.SELECT_CHECKBOX
                .equalsIgnoreCase(optInOutEnabled) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(messageManagementEnabled)) {
            appender3.append(requestVO.getWelcomeMesage1());
            lang1WelMessage = appender3.toString();
        } else if (PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(promotionType) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(messageManagementEnabled)) {
            appender4.append(requestVO.getWelcomeMesage1());
            lang1WelMessage = appender4.toString();
        } else {
            appender.append(requestVO.getWelcomeMesage1());
            lang1WelMessage = appender.toString();
        }
        profileSetLMSVO.setLang1welcomemsg(lang1WelMessage);

        appender = new StringBuffer(PretupsI.M_CLASS_AND_P_ID + PretupsI.WEL_MESSAGE_LANG2 + PretupsI.COLON);
        appender2 = new StringBuffer(PretupsI.M_CLASS_AND_P_ID + PretupsI.OPTINOUT_WEL_MESSAGE_LANG2 + PretupsI.COLON);
        appender3 = new StringBuffer(PretupsI.M_CLASS_AND_P_ID+ PretupsI.OPTINOUT_TRA_WEL_MSG_LANG2 + PretupsI.COLON);
        appender4 = new StringBuffer(PretupsI.M_CLASS_AND_P_ID + PretupsI.TRA_WEL_MSG_LANG2 + PretupsI.COLON);
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME,
                    "profileSetLMSVO.getLang1welcomemsg() = " + profileSetLMSVO.getLang1welcomemsg() + " profileSetLMSVO.getLang2welcomemsg()" + profileSetLMSVO
                            .getLang2welcomemsg());
        }
        String lang2WelMessage = null;
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue() && PretupsI.LMS_PROMOTION_TYPE_STOCK.equalsIgnoreCase(promotionType) && PretupsI.SELECT_CHECKBOX
                .equalsIgnoreCase(optInOutEnabled) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(messageManagementEnabled)) {
            appender2.append(requestVO.getWelcomeMesage2());
            lang2WelMessage = appender2.toString();
        } else if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue() && PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(promotionType) && PretupsI.SELECT_CHECKBOX
                .equalsIgnoreCase(optInOutEnabled) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(messageManagementEnabled)) {
            appender3.append(requestVO.getWelcomeMesage2());
            lang2WelMessage = appender3.toString();
        } else if (PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(promotionType) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(messageManagementEnabled)) {
            appender4.append(requestVO.getWelcomeMesage2());
            lang2WelMessage = appender4.toString();
        } else {
            appender.append(requestVO.getWelcomeMesage2());
            lang2WelMessage = appender.toString();
        }
        profileSetLMSVO.setLang2welcomemsg(lang2WelMessage);

        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME,
                    "profileSetLMSVO.getLang1welcomemsg() = " + profileSetLMSVO.getLang1welcomemsg() + " profileSetLMSVO.getLang2welcomemsg()" + profileSetLMSVO
                            .getLang2welcomemsg());
        }
        if (!PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equals(promotionType) && !BTSLUtil.isNullString(requestVO.getWelcomeMesage1()) && !BTSLUtil.isNullString(requestVO.getWelcomeMesage2()) && !BTSLUtil.isNullString(requestVO.getSuccessMessage1()) && !BTSLUtil.isNullString(requestVO.getSuccessMessage2()) && !BTSLUtil
                .isNullString(requestVO.getFailureMessage1()) && !BTSLUtil.isNullString(requestVO.getFailureMessage2())) {
            appender = new StringBuffer(PretupsI.M_CLASS_AND_P_ID + PretupsI.SUCCESS_MESSAGE_LANG1 + PretupsI.COLON);
            appender.append(requestVO.getSuccessMessage1());
            final String lang1SucMessage = appender.toString();
            profileSetLMSVO.setLang1seccessmsg(lang1SucMessage);

            appender = new StringBuffer(PretupsI.M_CLASS_AND_P_ID + PretupsI.SUCCESS_MESSAGE_LANG2 + PretupsI.COLON);
            appender.append(requestVO.getSuccessMessage2());
            final String lang2SucMessage = appender.toString();
            profileSetLMSVO.setLang2seccessmsg(lang2SucMessage);

            appender = new StringBuffer(PretupsI.M_CLASS_AND_P_ID + PretupsI.FAILURE_MESSAGE_LANG1 + PretupsI.COLON);
            appender.append(requestVO.getFailureMessage1());
            final String lang1FailMessage = appender.toString();
            profileSetLMSVO.setLang1failuremsg(lang1FailMessage);

            appender = new StringBuffer(PretupsI.M_CLASS_AND_P_ID + PretupsI.FAILURE_MESSAGE_LANG2 + PretupsI.COLON);
            appender.append(requestVO.getFailureMessage2());
            final String lang2FailMessage = appender.toString();
            profileSetLMSVO.setLang2failuremsg(lang2FailMessage);
        }

        final boolean flag = activationBonusLMSWebDAO.updateMessageList(con, profileSetLMSVO, requestVO.getSetID());
        if (flag == true) {
            con.commit();
            response.setStatus((HttpStatus.SC_OK));
            Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
            String resmsg = RestAPIStringParser.getMessage(locale,
                    PretupsErrorCodesI.MODIFY_MESSAGES_SUCCESSFULLY, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.MODIFY_MESSAGES_SUCCESSFULLY);
            final AdminOperationVO adminOperationVO = new AdminOperationVO();
            adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
            adminOperationVO.setSource(PretupsI.LOGGER_LMS_SOURCE);
            adminOperationVO.setDate(currentDate);
            adminOperationVO.setInfo(resmsg);
            adminOperationVO.setLoginID(userVO.getLoginID());
            adminOperationVO.setUserID(userVO.getUserID());
            adminOperationVO.setCategoryCode(userVO.getCategoryCode());
            adminOperationVO.setNetworkCode(userVO.getNetworkID());
            adminOperationVO.setMsisdn(userVO.getMsisdn());
            AdminOperationLog.log(adminOperationVO);
            return response;
        }
        else
        {
            con.rollback();
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR);
        }

    }

    @Override
    public BaseResponse resumePofileDetails(Connection con, UserVO userVO, SuspendRequestVO requestVO, BaseResponse response) throws BTSLBaseException, Exception {

        final String METHOD_NAME = "resumePofileDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }

        if (requestVO.getSetId() == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SET_ID_IS_NULL);
        }
        if (BTSLUtil.isEmpty(requestVO.getSetId())) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SET_ID_IS_EMPTY);
        }


        if (requestVO.getVersion() == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.VERSION_IS_NULL);
        }
        if (BTSLUtil.isEmpty(requestVO.getVersion())) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.VERSION_IS_EMPTY);
        }

        if (!requestVO.getSetId().matches(PretupsI.NUMERIC_TYPE)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SET_ID_IS_INVALID_FORMAT);
        }
        if (!requestVO.getVersion().matches(PretupsI.NUMERIC_TYPE)) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.VERSION_IS_INVALID_FORMAT);
        }
        Date currentDate = new Date(System.currentTimeMillis());

        ActivationBonusLMSWebDAO activationBonusLMSWebDAO= new ActivationBonusLMSWebDAO();
        ArrayList<ProfileSetLMSVO> versionList = activationBonusLMSWebDAO.loadResumeProfileList(con, userVO.getNetworkID(), new ArrayList<>());
        if (versionList.isEmpty()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_SET_ID_INVALID);
        }
        String version = String.valueOf(requestVO.getVersion());
        boolean isVersionpresent = false;
        for (ProfileSetLMSVO profileSetVersionLMSVO : versionList) {
            if (profileSetVersionLMSVO.getSetId().equals(requestVO.getSetId())) {
                isVersionpresent = true;
                break;
            }
        }
        ProfileSetLMSVO profileSet = activationBonusLMSWebDAO.loadProfileDetails(con, userVO.getNetworkID(), requestVO.getSetId(), version);

        if (!isVersionpresent ) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SET_ID_INVALID);
        }
        if (profileSet == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SET_ID_INVALID);
        }
       boolean resumeSuccess = activationBonusLMSWebDAO.resumeProfileSet(con, requestVO.getSetId(), String.valueOf(requestVO.getVersion()), userVO, true);
        if (!resumeSuccess) {
            con.rollback();
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.GENERAL_PROCESSING_ERROR);
        } else {
            con.commit();
            response.setStatus((HttpStatus.SC_OK));
            Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
            String resmsg = RestAPIStringParser.getMessage(locale,
                    PretupsErrorCodesI.RESUME_SUCCESS, null);
            response.setMessage(resmsg);
            response.setMessageCode(PretupsErrorCodesI.RESUME_SUCCESS);
            final AdminOperationVO adminOperationVO = new AdminOperationVO();
            adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_CHANGE_STATUS);
            adminOperationVO.setSource(PretupsI.LOGGER_LMS_SOURCE);
            adminOperationVO.setDate(currentDate);
            adminOperationVO.setInfo(resmsg);
            adminOperationVO.setLoginID(userVO.getLoginID());
            adminOperationVO.setUserID(userVO.getUserID());
            adminOperationVO.setCategoryCode(userVO.getCategoryCode());
            adminOperationVO.setNetworkCode(userVO.getNetworkID());
            adminOperationVO.setMsisdn(userVO.getMsisdn());
            AdminOperationLog.log(adminOperationVO);
            return response;
        }
    }

    @Override
    public ApproveProfilesAndVersionsResponseVO loadApprovePofilesAndVersionsDetails(Connection con, UserVO userVO, ApproveProfilesAndVersionsResponseVO response) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "loadApprovePofilesAndVersionsDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        Date currentDate = new Date(System.currentTimeMillis());

        ActivationBonusLMSWebDAO activationBonusLMSWebDAO= new ActivationBonusLMSWebDAO();
        ArrayList<ProfileSetLMSVO> profileList = new ArrayList();
        profileList = activationBonusLMSWebDAO.loadApprovalProfileList(con, userVO.getNetworkID(), profileList);
        Map<String, List<SuspendRequestVO>> filterProfileMap= profileList.stream().collect(Collectors.groupingBy(ProfileSetLMSVO:: getSetName, Collectors.mapping(profile->new SuspendRequestVO(profile.getSetId(), profile.getLastVersion()), Collectors.toList())));
         List<ApproveProfilesAndVersionsVO> approveProfilesAndVersionsVOList= filterProfileMap.entrySet().stream().map(entry-> new ApproveProfilesAndVersionsVO(entry.getKey(), entry.getValue())).collect(Collectors.toList());
        response.setApproveProfilesAndVersionsVOList(approveProfilesAndVersionsVOList);
        response.setStatus((HttpStatus.SC_OK));
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        String resmsg = RestAPIStringParser.getMessage(locale,
                PretupsErrorCodesI.LOAD_LOYALITY_MANAGEMENT_PROFILE_DETAILS_SUCCESSFULLY, null);
        response.setMessage(resmsg);
        response.setMessageCode(PretupsErrorCodesI.LOAD_LOYALITY_MANAGEMENT_PROFILE_DETAILS_SUCCESSFULLY);
        return response;
    }

    @Override
    public ProfileDetailsVersionsResponseVO loadApprovePofilesDetails(Connection con, UserVO userVO, SuspendRequestVO requestVO, ProfileDetailsVersionsResponseVO response) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "loadApprovePofilesDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        ActivationBonusLMSWebDAO activationBonusLMSWebDAO= new ActivationBonusLMSWebDAO();
        ArrayList<ProfileSetLMSVO> profileList = new ArrayList();
        ArrayList<ListValueVO> promotypeFromLookupList = LookupsCache.loadLookupDropDown(PretupsI.LMS_PROMOTION_TYPE, true);

        if (requestVO.getSetId() == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SET_ID_IS_NULL);
        }
        if (BTSLUtil.isEmpty(requestVO.getSetId())) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SET_ID_IS_EMPTY);
        }


        if (requestVO.getVersion() == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.VERSION_IS_NULL);
        }
        if (BTSLUtil.isEmpty(requestVO.getVersion())) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.VERSION_IS_EMPTY);
        }

        if((!BTSLUtil.isNullorEmpty(requestVO.getSetId())&& !BTSLUtil.isNullorEmpty(requestVO.getVersion())&&(!requestVO.getSetId().equals(PretupsI.ALL)))) {

            if(!requestVO.getVersion().equals(PretupsI.ALL)) {
                profileList = activationBonusLMSWebDAO.loadApprovalProfileListWithApplicableDate(con, userVO.getNetworkID(), requestVO.getSetId(), requestVO.getVersion(), profileList);
            }
            else
                profileList = activationBonusLMSWebDAO.loadApprovalProfileListWithApplicableDate(con, userVO.getNetworkID(), requestVO.getSetId(), null, profileList);

        }
        else if((!BTSLUtil.isNullorEmpty(requestVO.getSetId())&& !BTSLUtil.isNullorEmpty(requestVO.getVersion())&&(requestVO.getSetId().equals(PretupsI.ALL) && requestVO.getVersion().equals(PretupsI.ALL)))) {
            profileList = activationBonusLMSWebDAO.loadApprovalProfileListWithApplicableDate(con, userVO.getNetworkID(), null, null, profileList);
        }

        if(profileList.isEmpty()){
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.NO_DATA_FOUND_FOR_FILTERS_CRITERIA);
        }
        List<ProfileDetailsSet> profileDetailsSetList = new ArrayList<>();
        String format = Constants.getProperty("LMS_PROFILE_DATE_FORMAT");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat(format);
        SimpleDateFormat inputDateTimeDateFormat = new SimpleDateFormat(PretupsI.TIMESTAMP_FORMAT);

        for (ProfileSetLMSVO profileSetLMSVO : profileList) {
            ProfileDetailsSet profileDetailsSet = new ProfileDetailsSet();
            profileDetailsSet.setProfileName(profileSetLMSVO.getSetName());
            profileDetailsSet.setSetId(profileSetLMSVO.getSetId());
            profileDetailsSet.setStatus(profileSetLMSVO.getStatus());
            profileDetailsSet.setVersion(profileSetLMSVO.getLastVersion());
            profileDetailsSet.setApplicableFrom(outputDateFormat.format(inputDateTimeDateFormat.parse(profileSetLMSVO.getApplicableFromDate())));
            profileDetailsSet.setApplicableTo(outputDateFormat.format(inputDateTimeDateFormat.parse(profileSetLMSVO.getApplicableToDate())));
            profileDetailsSet.setShortCode(profileSetLMSVO.getShortCode());
            profileDetailsSet.setMessageConfig(profileSetLMSVO.getMsgConfEnableFlag());
            if(profileSetLMSVO.getMsgConfEnableFlag().equals(PretupsI.YES))
                profileDetailsSet.setMessageConfigDes(PretupsI.YES_DES);
            else
                profileDetailsSet.setMessageConfigDes(PretupsI.NO_DES);
            for (ListValueVO valueVO : promotypeFromLookupList) {
                if (valueVO.getValue().equals(profileSetLMSVO.getPromotionType())) {
                    profileDetailsSet.setPromotionTypeDesc(valueVO.getLabel());
                }

            }
            profileDetailsSet.setPromotionType(profileSetLMSVO.getPromotionType());
            profileDetailsSetList.add(profileDetailsSet);
        }
        response.setStatus((HttpStatus.SC_OK));
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        String resmsg = RestAPIStringParser.getMessage(locale,
                PretupsErrorCodesI.LOAD_LOYALITY_MANAGEMENT_PROFILE_DETAILS_SUCCESSFULLY, null);
        response.setMessage(resmsg);
        response.setMessageCode(PretupsErrorCodesI.LOAD_LOYALITY_MANAGEMENT_PROFILE_DETAILS_SUCCESSFULLY);
        response.setProfileDetailsSetList(profileDetailsSetList);
        return response;

    }

    @Override
    public BaseResponse modifyProfileDetails(Connection con, UserVO userVO, ModifyProfileDetailsRequestVO requestVO, BaseResponse response) throws BTSLBaseException, Exception {
        final String METHOD_NAME = "modifyProfileDetails";
        if (LOG.isDebugEnabled()) {
            LOG.debug(METHOD_NAME, "Entered");
        }
        this.addProfileValidation(con, userVO, requestVO, PretupsI.MOD_LMS);
        String version = String.valueOf(requestVO.getLastVersion());
        ActivationBonusLMSWebDAO activationBonusLMSWebDAO = new ActivationBonusLMSWebDAO();
        ArrayList<ProfileSetVersionLMSVO> versionList = activationBonusLMSWebDAO.loadVersionsList(con, requestVO.getSetId(), null);
        if (versionList.isEmpty()) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SERVICE_SET_ID_INVALID);
        }
        boolean isVersionpresent = false;
        for (ProfileSetVersionLMSVO profileSetVersionLMSVO : versionList) {
            if (profileSetVersionLMSVO.getVersion().equals(version)) {
                isVersionpresent = true;
                break;
            }
        }
        ProfileSetLMSVO profileSetLMSVO = activationBonusLMSWebDAO.loadProfileDetails(con, userVO.getNetworkID(), requestVO.getSetId(), version);

        if (!isVersionpresent ) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.VERSION_INVALID);
        }
        if (profileSetLMSVO == null) {
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.SET_ID_INVALID);
        }

        if (!BTSLUtil.isNullString(requestVO.getOperatorContribution())) {
            requestVO.setOperatorContribution(requestVO.getOperatorContribution());
        }
        if (profileSetLMSVO.getPromotionType().equals(PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT)) {
            requestVO.setParentContribution(PretupsI.ZERO);
            requestVO.setOperatorContribution("100");
        }

        Date currentDate = new Date();
        boolean updateVersion = false;
        String format = Constants.getProperty("LMS_PROFILE_DATE_FORMAT");
        // check whether the Activation Short Code is already exist or
        // not
        int insertMessagesUpdateCount = 0;
        requestVO.setSetId(profileSetLMSVO.getSetId());
        ProfileSetLMSVO profileSetVO = new ProfileSetLMSVO();
        profileSetVO.setSetName(profileSetLMSVO.getSetName());
        Date newDate = BTSLUtil.getDateFromDateString(requestVO.getApplicableFromDate(), format);
        SimpleDateFormat inputDateTimeDateFormat = new SimpleDateFormat(PretupsI.TIMESTAMP_FORMAT);
        Date oldDateTime = inputDateTimeDateFormat.parse(profileSetLMSVO.getApplicableFromDate());
        if ((oldDateTime.getTime() < newDate.getTime()))// we need to
        // insert the
        // new version
        {
          int latestVersion= Integer.parseInt( profileSetLMSVO.getLastVersion())+1;
            profileSetVO.setLastVersion( String.valueOf(latestVersion));
        } else// we need to update the same version
        {
            updateVersion = true;
            profileSetVO.setLastVersion(profileSetLMSVO.getLastVersion());
        }
        if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.LMS_PROF_APR_ALLOWED)).booleanValue()) {
            profileSetVO.setStatus(PretupsI.USER_STATUS_NEW);
        } else {
            profileSetVO.setStatus(PretupsI.STATUS_ACTIVE);
        }
        profileSetVO.setSetId(requestVO.getSetId());
        profileSetVO.setModifiedBy(userVO.getUserID());
        profileSetVO.setModifiedOn(currentDate);
        profileSetVO.setProfileType(PretupsI.LMS_PROFILE_TYPE);
        profileSetVO.setShortCode(profileSetLMSVO.getShortCode());
        profileSetVO.setNetworkCode(userVO.getNetworkID());
        profileSetVO.setPromotionType(profileSetLMSVO.getPromotionType());
        profileSetVO.setRefBasedAllow(requestVO.getReferenceBased());
        profileSetVO.setMsgConfEnableFlag(requestVO.getMsgConfigEnabled());
        profileSetVO.setApplicableFromDate(requestVO.getApplicableFromDate());
        profileSetVO.setApplicableToDate(requestVO.getApplicableToDate());
        profileSetVO.setNetworkCode(userVO.getNetworkID());
        // OPT_IN/OPT_OUT Service
        if (requestVO.getOptInOutService() != null && requestVO.getOptInOutService().equals(PretupsI.YES)) {
            profileSetVO.setOptInOut(requestVO.getOptInOutService());
        } else {
            profileSetVO.setOptInOut(PretupsI.NO);
        }

        int insertUpdateSetCount = activationBonusLMSWebDAO.addActivationBonusSet(con, profileSetVO, PretupsI.MOD_LMS);

        if (insertUpdateSetCount <= 0) {
            try {
                con.rollback();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
            }
            LOG.error(METHOD_NAME, "Error: while Inserting Activation Profile Set");
            throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
        }

        if (PretupsI.YES.equals(profileSetVO.getMsgConfEnableFlag())) {
            Locale locale1 = new Locale(PretupsI.LOCALE_LANGAUGE_EN, PretupsI.SORTTYPE_USER_STATUS);
            Locale locale2 = new Locale((String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_LANGUAGE)), (String) (PreferenceCache.getSystemPreferenceValue(PreferenceI.DEFAULT_COUNTRY)));
            LOG.debug(METHOD_NAME, "ME HERE : " + profileSetVO.getPromotionType());
            String Message_code = null;
            if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue() && PretupsI.LMS_PROMOTION_TYPE_STOCK.equalsIgnoreCase(profileSetVO.getPromotionType()) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(profileSetVO.getOptInOut())) {
                Message_code = PretupsI.OPTINOUT_WEL_MESSAGE + PretupsI.UNDERSCORE + profileSetVO.getSetId();
            } else if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue() && PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(profileSetVO.getPromotionType()) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(profileSetVO.getOptInOut())) {
                Message_code = PretupsI.OPTINOUT_TRA_WEL_MSG + PretupsI.UNDERSCORE + profileSetVO.getSetId();
            } else if (PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(profileSetVO.getPromotionType())) {
                Message_code = PretupsI.TRA_WEL_MESSAGE + PretupsI.UNDERSCORE + profileSetVO.getSetId();
            } else {
                Message_code = PretupsI.WEL_MESSAGE + PretupsI.UNDERSCORE + profileSetVO.getSetId();
            }
            profileSetVO.setMessageCode(Message_code);
            if (!activationBonusLMSWebDAO.isMessageExists(con, Message_code))
            {
                String defaultMessage = null;
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue() && PretupsI.LMS_PROMOTION_TYPE_STOCK.equalsIgnoreCase(profileSetVO.getPromotionType()) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(profileSetVO.getOptInOut())) {
                    defaultMessage = BTSLUtil.getMessage(locale1, PretupsI.OPTINOUT_WEL_MESSAGE);
                } else if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue() && PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(profileSetVO.getPromotionType()) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(profileSetVO.getOptInOut())) {
                    defaultMessage = BTSLUtil.getMessage(locale1, PretupsI.OPTINOUT_TRA_WEL_MSG);
                } else if (PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(profileSetVO.getPromotionType())) {
                    defaultMessage = BTSLUtil.getMessage(locale1, PretupsI.TRA_WEL_MESSAGE);
                } else {
                    defaultMessage = BTSLUtil.getMessage(locale1, PretupsI.WEL_MESSAGE);
                }
                if (BTSLUtil.isNullString(defaultMessage)) {

                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.DEFAULT_MESSAGE_IS_NULL);
                } else {
                    profileSetVO.setdefaultMessage(defaultMessage);
                }

                String message1 = null;
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue() && PretupsI.LMS_PROMOTION_TYPE_STOCK.equalsIgnoreCase(profileSetVO.getPromotionType()) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(profileSetVO.getOptInOut())) {
                    message1 = BTSLUtil.getMessage(locale1, PretupsI.OPTINOUT_WEL_MESSAGE_LANG1);
                } else if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue() && PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(profileSetVO.getPromotionType()) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(profileSetVO.getOptInOut())) {
                    message1 = BTSLUtil.getMessage(locale1, PretupsI.OPTINOUT_TRA_WEL_MSG_LANG1);
                } else if (PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(profileSetVO.getPromotionType())) {
                    message1 = BTSLUtil.getMessage(locale1, PretupsI.TRA_WEL_MSG_LANG1);
                } else {
                    message1 = BTSLUtil.getMessage(locale1, PretupsI.WEL_MESSAGE_LANG1);
                }
                if (BTSLUtil.isNullString(message1)) {
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.DEFAULT_MESSAGE_IS_NULL);
                } else {
                    profileSetVO.setMessage1(message1);
                }

                String message2 = null;
                if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue() && PretupsI.LMS_PROMOTION_TYPE_STOCK.equalsIgnoreCase(profileSetVO.getPromotionType()) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(profileSetVO.getOptInOut())) {
                    message2 = BTSLUtil.getMessage(locale2, PretupsI.OPTINOUT_WEL_MESSAGE_LANG2);
                } else if (((Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.OPT_IN_OUT_ALLOW)).booleanValue() && PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(profileSetVO.getPromotionType()) && PretupsI.SELECT_CHECKBOX.equalsIgnoreCase(profileSetVO.getOptInOut())) {
                    message2 = BTSLUtil.getMessage(locale2, PretupsI.OPTINOUT_TRA_WEL_MSG_LANG2);
                } else if (PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equalsIgnoreCase(profileSetVO.getPromotionType())) {
                    message2 = BTSLUtil.getMessage(locale2, PretupsI.TRA_WEL_MSG_LANG2);
                } else {
                    message2 = BTSLUtil.getMessage(locale2, PretupsI.WEL_MESSAGE_LANG2);
                }
                if (BTSLUtil.isNullString(message2)) {
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.DEFAULT_MESSAGE_IS_NULL);
                } else {
                    profileSetVO.setMessage2(message2);
                }

                insertMessagesUpdateCount = activationBonusLMSWebDAO.addActivationBonusMessages(con, profileSetVO, PretupsI.MOD_LMS);


                if (insertMessagesUpdateCount <= 0) {
                    try {
                        con.rollback();
                    } catch (Exception e) {
                        LOG.errorTrace(METHOD_NAME, e);
                    }
                    LOG.error(METHOD_NAME, "Error: while Inserting Activation Profile Set");
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
                }

            }
        } else if (PretupsI.NO.equals(profileSetVO.getMsgConfEnableFlag()) && !PretupsI.LMS_PROMOTION_TYPE_LOYALTYPOINT.equals(profileSetLMSVO.getPromotionType())) {

            ProfileSetLMSVO tempProfileSetVO = null;
            tempProfileSetVO = new ProfileSetLMSVO();
            tempProfileSetVO = activationBonusLMSWebDAO.loadMessageList(con, profileSetVO.getSetId());
            if (!BTSLUtil.isNullString(tempProfileSetVO.getMessageCode())) {
                int deleteCount = activationBonusLMSWebDAO.deleteMessages(con, tempProfileSetVO.getSetId());
                if (deleteCount <= 0) {
                    try {
                        con.rollback();
                    } catch (Exception e) {
                        LOG.errorTrace(METHOD_NAME, e);
                    }
                    LOG.error(METHOD_NAME, "Error: while Deleting Messages");
                    throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
                }
            }
        }
        // add data version and details
        if (!updateVersion) {
            this.addVersion(requestVO, userVO, con, activationBonusLMSWebDAO, currentDate, profileSetVO);
        } else {

            // update version and delete prodfile details to add new
            // details
            ProfileSetVersionLMSVO profileSetVersionVO = new ProfileSetVersionLMSVO();


            Date updatedNewDate = BTSLUtil.getDateFromDateString(requestVO.getApplicableFromDate(), format);
            profileSetVersionVO.setApplicableFrom(updatedNewDate);
            Date updatedNewToDate = BTSLUtil.getDateFromDateString(requestVO.getApplicableToDate(), format);
            profileSetVersionVO.setApplicableTo(updatedNewToDate);

            profileSetVersionVO.setOptContribution(requestVO.getOptInOutService());
            profileSetVersionVO.setPrtContribution(requestVO.getParentContribution());
            boolean isreferenceBaesd = false;
            if (PretupsI.YES.equals(requestVO.getReferenceBased())) {
                profileSetVersionVO.setRefApplicableFrom(BTSLUtil.getDateFromDateString(requestVO.getReferencefromDate()));
                profileSetVersionVO.setRefApplicableTo(BTSLUtil.getDateFromDateString(requestVO.getReferenceToDate()));
                isreferenceBaesd = true;
            }

            insertUpdateSetCount = activationBonusLMSWebDAO.updateProfileVersionDetail(con, version, userVO.getUserID(), requestVO.getSetId(), currentDate, profileSetVersionVO, isreferenceBaesd);

        }
        if(updateVersion){
            profileSetVO.setLastVersion(version);
        }
        this.addProfileDetails(requestVO, userVO, con, activationBonusLMSWebDAO, currentDate, profileSetVO, updateVersion);
        con.commit();

        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        String arr[] = {profileSetLMSVO.getSetName()};
        String resmsg = RestAPIStringParser.getMessage(locale,
                PretupsErrorCodesI.PROFILE_MODIFIED_SUCCESSFULLY, arr);
        final AdminOperationVO adminOperationVO = new AdminOperationVO();
        adminOperationVO.setSource(PretupsI.LOGGER_LMS_SOURCE);
        adminOperationVO.setDate(currentDate);
        adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
        adminOperationVO.setInfo(resmsg);
        adminOperationVO.setLoginID(userVO.getLoginID());
        adminOperationVO.setUserID(userVO.getUserID());
        adminOperationVO.setCategoryCode(userVO.getCategoryCode());
        adminOperationVO.setNetworkCode(userVO.getNetworkID());
        adminOperationVO.setMsisdn(userVO.getMsisdn());
        AdminOperationLog.log(adminOperationVO);
        response.setStatus((HttpStatus.SC_OK));
        response.setMessage(resmsg);
        response.setMessageCode(PretupsErrorCodesI.PROFILE_MODIFIED_SUCCESSFULLY);
        return response;
    }


}

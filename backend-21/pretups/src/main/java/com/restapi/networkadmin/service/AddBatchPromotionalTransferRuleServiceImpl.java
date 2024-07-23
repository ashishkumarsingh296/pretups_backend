package com.restapi.networkadmin.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpStatus;
import org.spring.custom.action.Globals;
//import org.apache.struts.action.ActionForward;
import com.btsl.util.MessageResources;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupDAO;
import com.btsl.pretups.cardgroup.businesslogic.CardGroupSetDAO;
import com.btsl.pretups.cellidmgt.businesslogic.CellIdMgmtDAO;
import com.btsl.pretups.cellidmgt.businesslogic.CellIdVO;
import com.btsl.pretups.common.ExcelFileIDI;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryDAO;
import com.btsl.pretups.domain.businesslogic.CategoryGradeDAO;
import com.btsl.pretups.domain.businesslogic.CategoryVO;
import com.btsl.pretups.domain.businesslogic.DomainDAO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.servicegpmgt.businesslogic.ServiceGpMgmtDAO;
import com.btsl.pretups.servicegpmgt.businesslogic.ServiceGpMgmtVO;
import com.btsl.pretups.transfer.businesslogic.TransferRulesVO;
import com.btsl.pretups.transfer.businesslogic.TransferVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.networkadmin.repositary.NTWTransferWebDAO;
import com.restapi.networkadmin.requestVO.AddBatchPromotionalTransferRuleFileProcessingRequestVO;
import com.restapi.networkadmin.responseVO.DomainAndCategoryResponseVO;
import com.restapi.networkadmin.responseVO.DownloadFileResponseVO;
import com.restapi.networkadmin.responseVO.UploadAndProcessFileResponseVO;
import com.restapi.networkadmin.serviceI.AddBatchPromotionalTransferRuleServiceI;
import com.restapi.networkadmin.utils.BatchTransferRuleExcelRW;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.master.businesslogic.GeographicalDomainWebDAO;
import com.web.pretups.master.businesslogic.ServiceClassWebDAO;
import com.web.pretups.transfer.businesslogic.TransferWebDAO;

@Service("AddBatchPromotionalTransferRuleServiceI")
public class AddBatchPromotionalTransferRuleServiceImpl implements AddBatchPromotionalTransferRuleServiceI {
    public static final Log LOG = LogFactory.getLog(AddBatchPromotionalTransferRuleServiceImpl.class.getName());
    public static final String CLASS_NAME = "AddBatchPromotionalTransferRuleServiceImpl";

    private DomainDAO domainDAO = new DomainDAO() ;

    private CategoryDAO categoryDAO = new CategoryDAO() ;

    private ServiceClassWebDAO serviceClasswebDAO = new ServiceClassWebDAO();

    private CardGroupDAO cardGroupDAO = new CardGroupDAO();

    private GeographicalDomainWebDAO geographicalDomainWebDAO = new GeographicalDomainWebDAO() ;
    @Override
    public ArrayList loadPromotionalLevel()  {
        final String methodName = "loadPromotionalLevel";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered:=" + methodName);

        }
        String str = PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW;
        boolean serviceProviderPromoAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW);
        ArrayList promotionalLevel = null;
        promotionalLevel = LookupsCache.loadLookupDropDown(PretupsI.PROMOTIONAL_LEVEL, true);
        if (serviceProviderPromoAllow) {
            for (int i = 0; i < promotionalLevel.size(); i++) {
                final ListValueVO list = (ListValueVO) promotionalLevel.get(i);
                if ("SRV".equalsIgnoreCase(list.getValue())) {
                    promotionalLevel.remove(i);
                    if (i == promotionalLevel.size() - 1) {
                        i--;
                    }
                }
            }

        }


        return promotionalLevel;
    }

    @Override
    public DomainAndCategoryResponseVO loadSearchCriteria(String promotionLevel, Connection con, UserVO userVO) throws BTSLBaseException {
        final String methodName = "loadSearchCriteria";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered:=" + methodName);
        }
        DomainAndCategoryResponseVO domainAndCategoryResponseVO = new DomainAndCategoryResponseVO();
        ArrayList cellGroupList = null;
        ArrayList serviceGroupList = null;

        if (BTSLUtil.isNullString(promotionLevel)) {
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_PROMOTIONALLEVEL);
        }
        ArrayList domainList = null;
        ArrayList catlist = null;
        ArrayList catValueList = null;

        if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_USR)) {
            domainList = null;
            if (TypesI.YES.equals(userVO.getCategoryVO().getDomainAllowed()) && PretupsI.DOMAINS_FIXED.equals(userVO.getCategoryVO().getFixedDomains())) {

                domainList = domainDAO.loadDomainList(con, PretupsI.DOMAIN_TYPE_CODE);
                if (domainList == null || domainList.isEmpty()) {
                    throw new BTSLBaseException(this, "loadSearchCriteria", PretupsErrorCodesI.NO_DOMAIN_LIST);
                }
                domainAndCategoryResponseVO.setDomainList(BTSLUtil.displayDomainList(domainList));
            } else {
                domainList = userVO.getDomainList();
                if (domainList == null || domainList.isEmpty()) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_DOMAIN_LIST);
                }
                domainAndCategoryResponseVO.setDomainList(BTSLUtil.displayDomainList(domainList));
            }
            catlist = new ArrayList();
            catlist = categoryDAO.loadOtherCategorList(con, PretupsI.OPERATOR_TYPE_OPT);
            if (catlist == null || catlist.isEmpty()) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_CATEGORY_LIST);
            }
            catValueList = new ArrayList();
            CategoryVO categoryVO = null;
            int catlistSize = catlist.size();
            for (int loop = 0, listSize = catlistSize; loop < listSize; loop++) {
                categoryVO = (CategoryVO) catlist.get(loop);
                catValueList.add(new ListValueVO(categoryVO.getCategoryName(), categoryVO.getCombinedKey()));
            }
            domainAndCategoryResponseVO.setCategoryList(catValueList);

            //geographicalDomainWebDAO = new GeographicalDomainWebDAO();
            final ArrayList newGeoType = geographicalDomainWebDAO.loadDomainTypeList(con);
            if (newGeoType == null || newGeoType.isEmpty()) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_GEOTYPE);
            }
            domainAndCategoryResponseVO.setGeoType(newGeoType);
            ArrayList geoDomain = new ArrayList();
            geoDomain = geographicalDomainWebDAO.loadGeoDomainList(con, userVO.getNetworkID());
            if (geoDomain == null || geoDomain.isEmpty()) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_GEODOMAIN);
            }
            final ArrayList geoTypeValueList = new ArrayList();
            GeographicalDomainVO geoListObj = null;
            int geoDomainSize = geoDomain.size();
            for (int loop = 0, listSize = geoDomainSize; loop < listSize; loop++) {
                geoListObj = (GeographicalDomainVO) geoDomain.get(loop);
                geoTypeValueList.add(new ListValueVO(geoListObj.getGrphDomainName(), geoListObj.getCombinedKey()));
            }
            domainAndCategoryResponseVO.setGeoDomain(geoTypeValueList);

        } else if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_GRD)) {
            domainList = null;
            if (TypesI.YES.equals(userVO.getCategoryVO().getDomainAllowed()) && PretupsI.DOMAINS_FIXED.equals(userVO.getCategoryVO().getFixedDomains())) {
                domainDAO = new DomainDAO();
                domainList = domainDAO.loadDomainList(con, PretupsI.DOMAIN_TYPE_CODE);
                if (domainList == null || domainList.isEmpty()) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_DOMAIN_LIST);
                }
                domainAndCategoryResponseVO.setDomainList(BTSLUtil.displayDomainList(domainList));
            } else {
                domainList = userVO.getDomainList();
                if (domainList == null || domainList.isEmpty()) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_DOMAIN_LIST);
                }
                domainAndCategoryResponseVO.setDomainList(BTSLUtil.displayDomainList(domainList));
            }

            categoryDAO = new CategoryDAO();
            catlist = new ArrayList();
            catlist = categoryDAO.loadOtherCategorList(con, PretupsI.OPERATOR_TYPE_OPT);
            if (catlist == null || catlist.isEmpty()) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_CATEGORY_LIST);
            }
            catValueList = new ArrayList();
            CategoryVO categoryVO = null;
            int catlistSize = catlist.size();
            for (int loop = 0, listSize = catlistSize; loop < listSize; loop++) {
                categoryVO = (CategoryVO) catlist.get(loop);
                catValueList.add(new ListValueVO(categoryVO.getCategoryName(), categoryVO.getCombinedKey()));
            }
            domainAndCategoryResponseVO.setCategoryList(catValueList);

        } else if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_CAT)) {
            domainList = null;
            if (TypesI.YES.equals(userVO.getCategoryVO().getDomainAllowed()) && PretupsI.DOMAINS_FIXED.equals(userVO.getCategoryVO().getFixedDomains())) {
                domainDAO = new DomainDAO();
                domainList = domainDAO.loadDomainList(con, PretupsI.DOMAIN_TYPE_CODE);
                if (domainList == null || domainList.isEmpty()) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_DOMAIN_LIST);
                }
                domainAndCategoryResponseVO.setDomainList(BTSLUtil.displayDomainList(domainList));
            } else {
                domainList = userVO.getDomainList();
                if (domainList == null || domainList.isEmpty()) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_DOMAIN_LIST);
                }
                domainAndCategoryResponseVO.setDomainList(BTSLUtil.displayDomainList(domainList));
            }

        } else if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_GRP)) {
            geographicalDomainWebDAO = new GeographicalDomainWebDAO();
            final ArrayList newGeoType = geographicalDomainWebDAO.loadDomainTypeList(con);
            if (newGeoType == null || newGeoType.isEmpty()) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_GEOTYPE);
            }
            domainAndCategoryResponseVO.setGeoType(newGeoType);
            domainAndCategoryResponseVO.setGeoTypeDesc((BTSLUtil.getOptionDesc(domainAndCategoryResponseVO.getGeoTypeCode(), domainAndCategoryResponseVO.getGeoType())).getLabel());

            final ArrayList geoDomain = geographicalDomainWebDAO.loadGeoDomainList(con, userVO.getNetworkID());
            if (geoDomain == null || geoDomain.isEmpty()) {
                throw new BTSLBaseException(this, "loadSearchCriteria", PretupsErrorCodesI.NO_GEODOMAIN);
            }

            final ArrayList geoTypeValueList = new ArrayList();
            GeographicalDomainVO geoListObj = null;
            for (int loop = 0, listSize = geoDomain.size(); loop < listSize; loop++) {
                geoListObj = (GeographicalDomainVO) geoDomain.get(loop);
                geoTypeValueList.add(new ListValueVO(geoListObj.getGrphDomainName(), geoListObj.getCombinedKey()));
            }
            domainAndCategoryResponseVO.setGeoDomain(geoTypeValueList);

        } else if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_CEL)) {
            final CellIdMgmtDAO cellGroupDAO = new CellIdMgmtDAO();
            cellGroupList = cellGroupDAO.getCellGroupList(con, userVO.getNetworkID());
            if (cellGroupList == null || cellGroupList.isEmpty()) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_CELLGROUP_LIST);
            }
            final ArrayList cellGroupValueList = new ArrayList();
            int cellGroupListSize = cellGroupList.size();
            for (int loop = 0; loop < cellGroupListSize; loop++) {
                final CellIdVO cellGroupVO = (CellIdVO) cellGroupList.get(loop);
                cellGroupValueList.add(new ListValueVO(cellGroupVO.getGroupName(), cellGroupVO.getCombinedKey()));
            }
            domainAndCategoryResponseVO.setCellGroupList(cellGroupValueList);


        } else if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_SERVICE)) {
            final ServiceGpMgmtDAO serviceGroupDAO = new ServiceGpMgmtDAO();
            serviceGroupList = serviceGroupDAO.getServiceGroupList(con, userVO.getNetworkID());
            if (serviceGroupList == null || serviceGroupList.isEmpty()) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_SERVICE_GROUP_LIST);
            }
            final ArrayList serviceGroupValueList = new ArrayList();
            int serviceListSize = serviceGroupList.size();
            for (int loop = 0; loop < serviceListSize; loop++) {
                final ServiceGpMgmtVO serviceGroupVO = (ServiceGpMgmtVO) serviceGroupList.get(loop);
                serviceGroupValueList.add(new ListValueVO(serviceGroupVO.getGroupName(), serviceGroupVO.getCombinedKey()));
            }
            domainAndCategoryResponseVO.setServiceGroupList(serviceGroupValueList);


        }

        return domainAndCategoryResponseVO;


    }

    @Override
    public DownloadFileResponseVO loadDownloadFile(Connection con, UserVO userVO, HttpServletRequest request, String promotionLevel, String domainCode, String categoryCode,
                                                   String geographicalCode,String cellGroupCode, String selectType) throws BTSLBaseException, ParseException {

        final String methodName = "loadDownLoadFile";

        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered:=" + methodName);
        }
        ServiceClassWebDAO serviceClasswebDAO = null;
        CardGroupDAO cardGroupDAO = null;
        ArrayList cardGroupList = null;
        DownloadFileResponseVO downloadFileResponseVO = new DownloadFileResponseVO();
        TransferWebDAO transferwebDAO = null;
        final HashMap masterDataMap = new HashMap();
        boolean serviceProviderPromoAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW);



        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        transferwebDAO = new TransferWebDAO();



        if (promotionLevel == null) {
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_PROMOTIONALLEVEL);
        }
        CategoryWebDAO categoryWebDAO = null;
        GeographicalDomainWebDAO geographicalDomainWebDAO = null;
        ArrayList geoDomainList = null;


        // load subscriber type list
        final ArrayList subscriberTypeList = LookupsCache.loadLookupDropDown(PretupsI.SUBSRICBER_TYPE, true);
        if (subscriberTypeList == null || subscriberTypeList.isEmpty()) {
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_SUBSCRIBER);
        }
        downloadFileResponseVO.setSubscriberTypeList(subscriberTypeList);
        // load service class type list
        serviceClasswebDAO = new ServiceClassWebDAO();
        final String interfaceCategory = "'" + PretupsI.INTERFACE_CATEGORY_PREPAID + "','" + PretupsI.INTERFACE_CATEGORY_POSTPAID + "','" + PretupsI.INTERFACE_CATEGORY_VOMS + "'";
        final ArrayList serviceClassList = serviceClasswebDAO.loadServiceClassList(con, interfaceCategory);
        if (serviceClassList == null || serviceClassList.isEmpty()) {
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_SERVICE_TYPE);
        }
        downloadFileResponseVO.setSubscriberServiceTypeList(serviceClassList);

        // load the cardgroup set list

        cardGroupDAO = new CardGroupDAO();
        final CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
        cardGroupList = cardGroupSetDAO.loadCardGroupSetForTransferRule(con, userVO.getNetworkID(), PretupsI.C2S_MODULE, PretupsI.TRANSFER_RULE_PROMOTIONAL);
        if (cardGroupList == null || cardGroupList.isEmpty()) {
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_CARD_GROUP_SET);
        }
        downloadFileResponseVO.setCardGroupIdList(cardGroupList);

        // set serviceTypeList
        downloadFileResponseVO.setServiceTypeList(cardGroupDAO.loadServiceTypeList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE));

        // load sub service type id list
        final ArrayList subServiceTypeIdList = ServiceSelectorMappingCache.loadSelectorDropDownForTrfRule();
        if (subServiceTypeIdList == null || subServiceTypeIdList.isEmpty()) {
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_SUBSERVICE);
        }
        downloadFileResponseVO.setSubServiceTypeIdList(subServiceTypeIdList);
        final ServiceGpMgmtDAO serviceGroupDAO = new ServiceGpMgmtDAO();
        final ArrayList serviceGroupValueList = new ArrayList();

        if (serviceProviderPromoAllow) {
            // load subscriberTypeList list
            ArrayList subscriberStatusList = new ArrayList();
            subscriberStatusList = transferwebDAO.getSubscriberStatusList(con, PretupsI.LOOKUP_TYPE_SUBSCRIBER_STATUS);
            if (subscriberStatusList == null || subscriberStatusList.isEmpty()) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_SERVICE_GROUP_LIST);
            }
            final ArrayList subscriberStatusValueList = new ArrayList();
            int subscriberStatusListSize = subscriberStatusList.size();
            for (int loop = 0; loop < subscriberStatusListSize; loop++) {
                final TransferVO transferVO = (TransferVO) subscriberStatusList.get(loop);
                downloadFileResponseVO.setSubscriberStatus(transferVO.getSubscriberStatus());
                subscriberStatusValueList.add(new ListValueVO(transferVO.getSubscriberStatus(), transferVO.getCombinedKey()));
            }
            downloadFileResponseVO.setSubscriberStatusList(subscriberStatusValueList);

            // load serviceProviderGroup list

            ArrayList serviceGroupList = new ArrayList();
            serviceGroupList = serviceGroupDAO.getServiceGroupList(con, userVO.getNetworkID());
            if (serviceGroupList == null || serviceGroupList.isEmpty()) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_SERVICE_GROUP_LIST);
            }
            int serviceGroupListSize = serviceGroupList.size();
            for (int loop = 0; loop < serviceGroupListSize; loop++) {
                final ServiceGpMgmtVO serviceGroupVO = (ServiceGpMgmtVO) serviceGroupList.get(loop);
                serviceGroupValueList.add(new ListValueVO(serviceGroupVO.getGroupName(), serviceGroupVO.getCombinedKey()));
            }
            downloadFileResponseVO.setServiceGroupList(serviceGroupValueList);
            // set serviceProviderGroupList.
            masterDataMap.put(PretupsI.SERVICE_GROUP_TYPE_ID, serviceGroupList);
            // set subscriberStatusList
            masterDataMap.put(PretupsI.TRANSFER_RULE_SUBSCRIBER_STATUS, subscriberStatusList);

        }

        // load subscriber type list
        masterDataMap.put(PretupsI.SUBSRICBER_TYPE, LookupsCache.loadLookupDropDown(PretupsI.SUBSRICBER_TYPE, true));
        // load service class type list
        masterDataMap.put(PretupsI.PROMOTIONAL_INTERFACE_CATEGORY_CLASS, serviceClassList);
        // load the cardgroup set list
        masterDataMap.put(PretupsI.TRANSFER_RULE_PROMOTIONAL, cardGroupList);
        // set serviceTypeList
        masterDataMap.put(PretupsI.C2S_MODULE, cardGroupDAO.loadServiceTypeList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE));
        // load sub service type id list
        masterDataMap.put(PretupsI.SUB_SERVICES_FOR_TRANSFERRULE, subServiceTypeIdList);

        String f_name = domainCode;
        if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_GRD)) {
            ArrayList gradeList = new ArrayList();
            final CategoryGradeDAO categoryGradeDAO = new CategoryGradeDAO();
            gradeList = categoryGradeDAO.loadGradeList(con);
            if (gradeList == null || gradeList.isEmpty()) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_GRADE);
            }
            final ArrayList gradeTypeValueList = new ArrayList();
            GradeVO geoListObj = null;
            int gradeListSize = gradeList.size();
            for (int loop = 0, listSize = gradeListSize; loop < listSize; loop++) {
                geoListObj = (GradeVO) gradeList.get(loop);
                if (geoListObj.getCategoryCode().equals(categoryCode)) {
                    gradeTypeValueList.add(new ListValueVO(geoListObj.getGradeName(), geoListObj.getCombinedKey()));
                }
            }
            downloadFileResponseVO.setGradeList(gradeTypeValueList);
            masterDataMap.put(PretupsI.PROMOTIONAL_LEVEL_GRADE, gradeTypeValueList);

        } else if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_CAT)) {

            if (domainCode == null) {
                throw new BTSLBaseException(this, methodName,PretupsErrorCodesI.NO_PROMOTIONALLEVEL);
            }

            categoryWebDAO = new CategoryWebDAO();
            ArrayList catDetList = new ArrayList();
            catDetList = categoryWebDAO.loadCategoryDetails(con, domainCode);
            if (catDetList == null || catDetList.isEmpty()) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_CATEGORY_LIST);
            }

            downloadFileResponseVO.setCategoryList(catDetList);
            masterDataMap.put(PretupsI.PROMOTIONAL_LEVEL_CATEGORY, catDetList);
        } else if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_GRP)) {

            f_name = geographicalCode;
            geographicalDomainWebDAO = new GeographicalDomainWebDAO();
            geoDomainList = geographicalDomainWebDAO.loadGeographicalDomainCodebyNetwork(con, geographicalCode,userVO.getNetworkID());
            if (geoDomainList == null || geoDomainList.isEmpty()) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_GEOTYPE);
            }
            downloadFileResponseVO.setGeoDomainCodeList(geoDomainList);
            masterDataMap.put(PretupsI.PROMOTIONAL_LEVEL_GEOGRAPHY, geoDomainList);

        } else if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_CEL)) {

            if (cellGroupCode == null) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_PROMOTIONALLEVEL);
            }

            f_name = cellGroupCode;

            final CellIdMgmtDAO cellGroupDAO = new CellIdMgmtDAO();
            final ArrayList cellGroupList = cellGroupDAO.getCellGroupList(con, userVO.getNetworkID());
            if (cellGroupList == null || cellGroupList.isEmpty()) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_CELLGROUP_LIST);
            }

            final ArrayList cellGroupValueList = new ArrayList();
            int cellGroupListSize = cellGroupList.size();
            for (int loop = 0; loop < cellGroupListSize; loop++) {
                final CellIdVO cellGroupVO = (CellIdVO) cellGroupList.get(loop);
                if (cellGroupVO.getGroupCode().equals(cellGroupCode)) {
                    cellGroupValueList.add(new ListValueVO(cellGroupVO.getGroupName(), cellGroupVO.getCombinedKey()));
                }
            }
            downloadFileResponseVO.setCellGroupList(cellGroupValueList);
            masterDataMap.put(PretupsI.PROMOTIONAL_LEVEL_CELLGROUP, cellGroupValueList);
        }

        String filePath =Constants.getProperty("DownloadBulkPromotionalTrfRulePath");

        try {
            final File fileDir = new File(filePath);
            if (!fileDir.isDirectory()) {
                fileDir.mkdirs();
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
            LOG.error(methodName, "Exception" + e.getMessage());
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.DIR_NOT_CREATED, "selectDomainForInitiate");
        }
        downloadFileResponseVO.setExelMasterData(masterDataMap);
        final String fileName = f_name +"_promotionLevel_" + promotionLevel + BTSLUtil
                .getFileNameStringFromDate(new Date()) + ".xls";
        final BatchTransferRuleExcelRW excelRW = new BatchTransferRuleExcelRW();

        excelRW.writeExcel(PretupsI.PROMOTIONAL_BATCH_TRF_RULE, masterDataMap, ((MessageResources) request.getAttribute(Globals.MESSAGES_KEY)), locale, promotionLevel, filePath + fileName, selectType);
        File fileNew = new File(filePath + fileName);

        try {
            byte[] fileContent = FileUtils.readFileToByteArray(fileNew);
            String encodedString = Base64.getEncoder().encodeToString(fileContent);
            downloadFileResponseVO.setFileAttachment(encodedString);
            downloadFileResponseVO.setFileName(fileName);
            downloadFileResponseVO.setFileType("xls");

        }
        catch(Exception e) {
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.DIR_NOT_CREATED, "selectDomainForInitiate");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Exiting:forward=" );
        }

        return downloadFileResponseVO;
    }

    @Override
    public UploadAndProcessFileResponseVO uploadAndProcessFile(Connection con, HttpServletRequest httpServletRequest,HttpServletResponse response1, UserVO userVO, String promotionLevel,
                                                               String domainCode, String categoryCode, String geographicalCode, String cellGroupCode,
                                                               String selectType, AddBatchPromotionalTransferRuleFileProcessingRequestVO fileRequest) throws Exception {

        final String methodName = "uploadAndProcessFile";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
        UploadAndProcessFileResponseVO responseVO = new UploadAndProcessFileResponseVO();

        boolean success = false;
        String dir = null;


        String[][] excelArr = null;


        String rulLevel = null;
        TransferWebDAO transferwebDAO = null;
        NTWTransferWebDAO networkadmintransferwebDAO =null;
        ArrayList catDetList = null;
        ArrayList gradeTypeValueList = null;
        ArrayList geoDomainList = null;
        CategoryWebDAO categoryWebDAO = null;
        String category_code = null;
        ArrayList cellGroupList = null;
        ArrayList cellGroupValueList = null;
        ArrayList serviceGroupList = null;
        ArrayList serviceGroupValueList = null;
        GeographicalDomainWebDAO geographicalDomainWebDAO = null;
        dir = Constants.getProperty("UploadBatchPromotionalTrfFilePath"); // Upload
        HashMap<String, String> fileDetailsMap = new HashMap<>();
        final MessageResources messageResources = (MessageResources) httpServletRequest.getAttribute(Globals.MESSAGES_KEY);
        final HashMap<String, String> map = new HashMap<String, String>();
        boolean serviceProviderPromoAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW);
        boolean cellGroupRequired = ((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.CELL_GROUP_REQUIRED))).booleanValue();

        String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.GRADE_MANAGEMENT_DOMAIN_CATEGORY_LIST_FOUND, null);
        try {

            transferwebDAO = new TransferWebDAO();
            networkadmintransferwebDAO = new NTWTransferWebDAO();
            if (serviceProviderPromoAllow) {
                final ServiceGpMgmtDAO serviceGroupDAO = new ServiceGpMgmtDAO();
                serviceGroupList = serviceGroupDAO.getServiceGroupList(con, userVO.getNetworkID());
                if (serviceGroupList == null || serviceGroupList.isEmpty()) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_SERVICE_GROUP_LIST);
                }
            }
            if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_USR)) {

                rulLevel = PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_USR;
                category_code = categoryCode;

            } else if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_CAT)) {


                if (BTSLUtil.isNullString(domainCode)) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_PROMOTIONALLEVEL, "NoDomainCode");
                }
                categoryWebDAO = new CategoryWebDAO();
                catDetList = categoryWebDAO.loadCategoryDetails(con, domainCode);
                rulLevel = PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_CAT;
            } else if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_GRP)) {


                geographicalDomainWebDAO = new GeographicalDomainWebDAO();
                geoDomainList = geographicalDomainWebDAO.loadGeographicalDomainCode(con, geographicalCode);
                rulLevel = PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_GRP;
            } else if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_GRD)) {

                category_code = categoryCode;
                final CategoryGradeDAO categoryGradeDAO = new CategoryGradeDAO();
                final ArrayList gradeList = categoryGradeDAO.loadGradeList(con);
                if (gradeList == null || gradeList.isEmpty()) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_GRADE, "");
                }
                gradeTypeValueList = new ArrayList();
                GradeVO geoListObj = null;
                int gradeListSize = gradeList.size();
                for (int loop = 0, listSize = gradeListSize; loop < listSize; loop++) {
                    geoListObj = (GradeVO) gradeList.get(loop);
                    if (geoListObj.getCategoryCode().equals(category_code)) {
                        gradeTypeValueList.add(new ListValueVO(geoListObj.getGradeName(), geoListObj.getCombinedKey()));
                    }
                }
                rulLevel = PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_GRD;
            } else if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_CEL)) {
                cellGroupList = new ArrayList();

                final String cellgroup_code = cellGroupCode;
                final CellIdMgmtDAO cellGroupDAO = new CellIdMgmtDAO();
                cellGroupList = cellGroupDAO.getCellGroupList(con, userVO.getNetworkID());
                if (cellGroupList == null || cellGroupList.isEmpty()) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_CELLGROUP_LIST);
                }
                cellGroupValueList = new ArrayList();
                int cellGroupListSize = cellGroupList.size();
                for (int loop = 0; loop < cellGroupListSize; loop++) {
                    final CellIdVO cellGroupVO = (CellIdVO) cellGroupList.get(loop);
                    if (cellGroupVO.getGroupCode().equals(cellgroup_code)) {
                        cellGroupValueList.add(new ListValueVO(cellGroupVO.getGroupName(), cellGroupVO.getCombinedKey()));
                    }
                }
                rulLevel = PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_CEL;
            }


            // file
            // path
            if (BTSLUtil.isNullString(dir)) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.DIR_NOT_CREATED, "addbatchtrfrulesservicegroup");
            }
            final File dirtest = new File(dir);
            if (!dirtest.exists()) {
                if (!(dirtest.mkdirs())) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.DIR_NOT_CREATED, "addbatchtrfrulesservicegroup");
                }
            }
            final String contentType = PretupsI.FILE_CONTENT_TYPE_XLS;
            String fileSize = Constants.getProperty("MAX_XLS_FILE_SIZE_FOR_BULKPROMOTRFRULE");
            if (BTSLUtil.isNullString(fileSize)) {
                fileSize = "0";
                EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.INFO,
                        "PromotionalTransferRulesAction[uploadAndProcessFile]", "", "", "",
                        "Missing entry for MAX_XLS_FILE_SIZE_FOR_BULKPROMOTRFRULE. Taking 0 as default value.");
            }

            // Cross site Scripting removal
            if (!BTSLUtil.isNullString(fileRequest.getFileAttachment())
                    && !BTSLUtil.isNullString(fileRequest.getFileName())&& !BTSLUtil.isNullString(fileRequest.getFileType())) {
                boolean message = BTSLUtil.isValideFileName(fileRequest.getFileName());

                if (!message) {
                    throw new BTSLBaseException(CLASS_NAME, methodName, PretupsErrorCodesI.INVALID_FILE_NAME1, 0, null);
                }
            } else {
                LOG.error("validateFileInput", "FILENAME/FILEATTACHMENT IS NULL");
                throw new BTSLBaseException(this, "validateFileInput", PretupsErrorCodesI.INVALID_FILE_INPUT,
                        PretupsI.RESPONSE_FAIL, null);

            }


            if (!((fileRequest.getFileType()).equalsIgnoreCase("xls"))) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_XLSFILE, "addbatchtrfrulesservicegroup");
            }
            final String format = Constants.getProperty("PROMOTIONAL_TRANSFER_DATE_FORMAT");
            // check for the format if exists else throw exception
            ReadGenericFileUtil fileUtil = new ReadGenericFileUtil();

            final byte[] data =fileUtil.decodeFile(fileRequest.getFileAttachment());
            ByteArrayInputStream is = new ByteArrayInputStream(data);
            InputStreamReader inputStreamReader = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(inputStreamReader);


            fileDetailsMap.put(PretupsI.FILE_NAME, fileRequest.getFileName());
            fileDetailsMap.put(PretupsI.FILE_ATTACHMENT, fileRequest.getFileAttachment());
            fileDetailsMap.put(PretupsI.FILE_TYPE, fileRequest.getFileType());
            success = BTSLUtil.uploadCsvFileToServerWithHashMapForXLS(fileDetailsMap, dir,
                    contentType, "addbatchtrfrulesservicegroup", data, Long.parseLong(fileSize));

            if (success) // now process uploaded file
            {
                final BatchTransferRuleExcelRW excelRW = new BatchTransferRuleExcelRW();
                final int leftHeaderLinesForEachSheet = 3;
                final boolean readLastSheet = false;
                excelArr = excelRW.readMultipleExcelSheet(ExcelFileIDI.PROMOTIONAL_BATCH_TRF_RULE, dir + fileRequest.getFileName()+".xls", readLastSheet, leftHeaderLinesForEachSheet, map);
                int rows = 0;
                int cols = 0;
                try {
                    cols = excelArr[0].length;
                } catch (Exception e) {
                    LOG.errorTrace(methodName, e);
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_RECORD_FOUND_IN_FILE, "addbatchtrfrulesservicegroup");
                }
                rows = excelArr.length; // rows do not include the headings
                final int rowOffset = 0;
                int maxRowSize = 0;
                if (cols < 10) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_RECORD_FOUND_IN_FILE, "addbatchtrfrulesservicegroup");
                }
                if (rows <= rowOffset) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_RECORD_FOUND_IN_FILE, "addbatchtrfrulesservicegroup");
                }
                // Check the Max Row Size of the XLS file. if it is greater than
                // the specified size throw err.
                try {
                    maxRowSize = Integer.parseInt(Constants.getProperty("MAX_RECORDS_IN_BULKPROMOTRFRULE"));
                } catch (Exception e) {
                    LOG.error(methodName, "Exception:e=" + e);
                    LOG.errorTrace(methodName, e);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL,
                            "promotionaltransferrule[uploadandprocessfile]", "", "", "", "Exception:" + e.getMessage());
                }
                HashMap masterServiceClass = new HashMap();

                HashMap masterCardGroup = new HashMap();
                final ServiceClassWebDAO serviceClasswebDAO = new ServiceClassWebDAO();
                final String interfaceCategory = "'" + PretupsI.INTERFACE_CATEGORY_PREPAID + "','" + PretupsI.INTERFACE_CATEGORY_POSTPAID + "','" + PretupsI.INTERFACE_CATEGORY_VOMS + "'";
                masterServiceClass = serviceClassMap(serviceClasswebDAO.loadServiceClassList(con, interfaceCategory));
                final CardGroupSetDAO cardGroupSetDAO = new CardGroupSetDAO();
                masterCardGroup = cardGroupMap(cardGroupSetDAO.loadCardGroupSetForTransferRule(con, userVO.getNetworkID(), PretupsI.C2S_MODULE,
                        PretupsI.TRANSFER_RULE_PROMOTIONAL));

                final ArrayList cardGroupList = cardGroupSetDAO.loadCardGroupSetForTransferRule(con, userVO.getNetworkID(), PretupsI.C2S_MODULE,
                        PretupsI.TRANSFER_RULE_PROMOTIONAL);
                if (cardGroupList == null || cardGroupList.isEmpty()) {
                    throw new BTSLBaseException(this, "loadDownLoadFile", PretupsErrorCodesI.NO_CARD_GROUP_SET);
                }

                // load sub service type id list
                final ArrayList subServiceTypeIdList = ServiceSelectorMappingCache.loadSelectorDropDownForTrfRule();
                if (subServiceTypeIdList == null || subServiceTypeIdList.isEmpty()) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.NO_SUBSERVICE);
                }
                responseVO.setSubServiceTypeIdList(subServiceTypeIdList);

                // load subscriberStatusList list
                ArrayList subscriberStatusList = new ArrayList();
                if (serviceProviderPromoAllow) {
                    subscriberStatusList = transferwebDAO.getSubscriberStatusList(con, PretupsI.LOOKUP_TYPE_SUBSCRIBER_STATUS);
                    if (subscriberStatusList == null || subscriberStatusList.isEmpty()) {
                        throw new BTSLBaseException(this, "loadC2SContentList", PretupsErrorCodesI.NO_SERVICE_GROUP_LIST);
                    }
                    final ArrayList subscriberStatusValueList = new ArrayList();
                    int subscriberStatusListSize = subscriberStatusList.size();
                    for (int loop = 0; loop < subscriberStatusListSize; loop++) {
                        final TransferVO transferVO = (TransferVO) subscriberStatusList.get(loop);
                        responseVO.setSubscriberStatus(transferVO.getSubscriberStatus());
                        subscriberStatusValueList.add(new ListValueVO(transferVO.getSubscriberStatus(), transferVO.getCombinedKey()));
                    }
                    responseVO.setSubscriberStatusList(subscriberStatusValueList);
                }

                String error = null;
                final ArrayList fileErrorList = new ArrayList();
                final ArrayList fileValiedList = new ArrayList();
                int blankLines = 0;
                ListValueVO errorVO = null;
                TransferRulesVO transferRulesVO = null;

                final String module = PretupsI.PROMOTIONAL_BATCH_TRF_MODULE;
                final String ruleType = PretupsI.PROMOTIONAL_BATCH_TRF_RULE_TYP;
                final String network_code = userVO.getNetworkID();
                final String staus = PretupsI.PROMOTIONAL_BATCH_TRF_RULE_STATUS;
                final String senderServiceClassID = PretupsI.PROMOTIONAL_BATCH_TRF_RULE_SENDER_SERVICE_CLASS_ID;
                final Date currentDate = new Date();
                boolean fileValidationErrorExists = false;
                if (rows > maxRowSize) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.MAX_LIMIT_OF_RECS_REACHED, 0,
                            new String[] { String.valueOf(maxRowSize) }, "addbatchtrfrulesservicegroup");
                } else {
                    for (int i = 1; i < rows; i++) {
                        if (BTSLUtil.isNullString(excelArr[i][0])) {
                            if (BTSLUtil.isNullArray(excelArr[i])) {
                                blankLines++;
                                continue;
                            }
                        }
                        transferRulesVO = new TransferRulesVO();
                        transferRulesVO.setRowID(new Integer(i+leftHeaderLinesForEachSheet).toString());
                        if (cellGroupRequired || serviceProviderPromoAllow) {
                            error = valiedExelDataForCLGroup(excelArr, i, masterServiceClass, masterCardGroup, messageResources, locale, format, subServiceTypeIdList,
                                    cardGroupList, selectType, subscriberStatusList, promotionLevel, serviceGroupList);
                        } else {
                            error = valiedExelData(excelArr, i, masterServiceClass, masterCardGroup, messageResources, locale, format, subServiceTypeIdList, cardGroupList,
                                    selectType);
                        }

                        if (error == null) {
                            if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_USR)) {
                                if (!BTSLUtil.isValidMSISDN(excelArr[i][0])) {
                                    error = PretupsI.PROMOTIONAL_BATCH_TRF_RULE_MSISDN_NOTVALIED;
                                }
                            } else if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_CAT)) {
                                error = valiedCategoryCode(excelArr[i][0], catDetList, messageResources, locale);
                            } else if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_GRD)) {
                                error = valiedGradeCode(excelArr[i][0], gradeTypeValueList, messageResources, locale);
                            } else if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_GRP)) {
                                error = valiedGeogDomainCode(excelArr[i][0], geoDomainList, messageResources, locale);
                            } else if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_CEL)) {
                                error = validateCellGroupCode(excelArr[i][0], cellGroupValueList, messageResources, locale);
                            } else if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_SERVICE)) {
                                error = validateServiceGroupCode(excelArr[i][0], serviceGroupValueList, messageResources, locale);
                            }

                        }

                        if (error == null) {
                            if (cellGroupRequired || serviceProviderPromoAllow) {
                                transferRulesVO.setSenderSubscriberType(excelArr[i][0]);
                                transferRulesVO.setModule(module);
                                transferRulesVO.setNetworkCode(network_code);
                                transferRulesVO.setReceiverSubscriberType(excelArr[i][1].toUpperCase());
                                transferRulesVO.setSenderServiceClassID(senderServiceClassID);
                                transferRulesVO.setReceiverServiceClassID(excelArr[i][2].toUpperCase());
                                if (serviceProviderPromoAllow) {
                                    transferRulesVO.setSubscriberStatus(excelArr[i][3].toUpperCase());
                                } else {
                                    transferRulesVO.setSubscriberStatus(PretupsI.ALL);
                                }
                                transferRulesVO.setCreatedOn(currentDate);
                                transferRulesVO.setCreatedBy(userVO.getUserID());
                                transferRulesVO.setModifiedOn(currentDate);
                                transferRulesVO.setModifiedBy(userVO.getUserID());
                                transferRulesVO.setStatus(staus);
                                transferRulesVO.setSelectRangeType(selectType);
                                if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_SERVICE)) {
                                    transferRulesVO.setServiceType(excelArr[i][4].toUpperCase());
                                    transferRulesVO.setSubServiceTypeId(excelArr[i][5].toUpperCase());
                                    transferRulesVO.setCardGroupSetID(excelArr[i][6].toUpperCase());
                                } else {
                                    if (serviceProviderPromoAllow) {
                                        transferRulesVO.setServiceGroupCode(excelArr[i][4].toUpperCase());
                                    } else {
                                        transferRulesVO.setServiceGroupCode(PretupsI.ALL);
                                    }
                                    transferRulesVO.setServiceType(excelArr[i][5].toUpperCase());
                                    transferRulesVO.setSubServiceTypeId(excelArr[i][6].toUpperCase());
                                    transferRulesVO.setCardGroupSetID(excelArr[i][7].toUpperCase());

                                }
                                try {
                                    if ("Y".equalsIgnoreCase(transferRulesVO.getSelectRangeType())) {
                                        if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_SERVICE)) {

                                            transferRulesVO.setMultipleSlab(excelArr[i][8] + "-" + excelArr[i][10]);
                                            /**Convert into GregorianDates starts**/
                                            transferRulesVO.setStartTime(BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][7])));
                                            transferRulesVO.setEndTime(BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][9])));
                                            /**Convert into GregorianDates ends**/
                                        } else {
                                            StringBuilder multipleSlab = new StringBuilder();
                                            if(excelArr[i][9].contains(",") || excelArr[i][11].contains(",")) {
                                                String startTimeArr[]=excelArr[i][9].split(",");
                                                String endTimeArr[] = excelArr[i][11].split(",");
                                                if(startTimeArr.length == endTimeArr.length) {
                                                    for(int k =0; k<startTimeArr.length;k++) {
                                                        multipleSlab.append( startTimeArr[k]);
                                                        multipleSlab.append("-");
                                                        multipleSlab.append(endTimeArr[k]);

                                                        if(!(k+1== startTimeArr.length)) {
                                                            multipleSlab.append(",");
                                                        }
                                                    }
                                                    transferRulesVO.setMultipleSlab(multipleSlab.toString());

                                                }
                                                else {
                                                    errorVO = new ListValueVO("", transferRulesVO.getRowID(), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_TIME_SLAB, null));
                                                    fileErrorList.add(errorVO);
                                                    continue;

                                                }



                                            }else {
                                                if(!excelArr[i][9].isEmpty() && !excelArr[i][11].isEmpty() )
                                                    transferRulesVO.setMultipleSlab(excelArr[i][9] + "-" + excelArr[i][11]);
                                                else
                                                    transferRulesVO.setMultipleSlab(null);
                                            }
                                            /**Convert into GregorianDates starts**/
                                            try {
                                                transferRulesVO.setStartTime(BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][8])));
                                            }
                                            catch(Exception ex) {
                                                errorVO = new ListValueVO("", transferRulesVO.getRowID(), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_START_DATE_FORMAT, null));
                                                fileErrorList.add(errorVO);
                                                continue;
                                            }
                                            try {
                                                transferRulesVO.setEndTime(BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][10])));
                                            }
                                            catch(Exception ex) {
                                                errorVO = new ListValueVO("", transferRulesVO.getRowID(), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_END_DATE_FORMAT, null));
                                                fileErrorList.add(errorVO);
                                                continue;
                                            }
                                            /**Convert into GregorianDates ends**/
                                        }
                                    } else {
                                        if (promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_SERVICE)) {
                                            transferRulesVO.setMultipleSlab(excelArr[i][9] + "-" + excelArr[i][10]);
                                            /**Convert into GregorianDates starts**/
                                            transferRulesVO.setStartTime(BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][7])));
                                            transferRulesVO.setEndTime(BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][8])));
                                            /**Convert into GregorianDates ends**/
                                        } else {
                                            transferRulesVO.setMultipleSlab(excelArr[i][10] + "-" + excelArr[i][11]);
                                            /**Convert into GregorianDates starts**/
                                            transferRulesVO.setStartTime(BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][8])));
                                            transferRulesVO.setEndTime(BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][9])));
                                            /**Convert into GregorianDates ends**/
                                        }
                                    }

                                } catch (ParseException e1) {
                                    errorVO = new ListValueVO("", transferRulesVO.getRowID(), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_DATE_FORMAT, null));
                                    fileErrorList.add(errorVO);
                                    LOG.errorTrace(methodName, e1);
                                    continue;

                                }

                            } else {
                                transferRulesVO.setSenderSubscriberType(excelArr[i][0]);
                                transferRulesVO.setModule(module);
                                transferRulesVO.setNetworkCode(network_code);
                                transferRulesVO.setReceiverSubscriberType(excelArr[i][1].toUpperCase());
                                transferRulesVO.setSenderServiceClassID(senderServiceClassID);
                                transferRulesVO.setReceiverServiceClassID(excelArr[i][2].toUpperCase());
                                transferRulesVO.setCreatedOn(currentDate);
                                transferRulesVO.setCreatedBy(userVO.getUserID());
                                transferRulesVO.setModifiedOn(currentDate);
                                transferRulesVO.setModifiedBy(userVO.getUserID());
                                transferRulesVO.setStatus(staus);
                                transferRulesVO.setServiceType(excelArr[i][3].toUpperCase());
                                transferRulesVO.setSubServiceTypeId(excelArr[i][4].toUpperCase());
                                transferRulesVO.setCardGroupSetID(excelArr[i][5].toUpperCase());
                                transferRulesVO.setSelectRangeType(selectType);

                                try {
                                    if ("Y".equalsIgnoreCase(transferRulesVO.getSelectRangeType())) {
                                        transferRulesVO.setMultipleSlab(excelArr[i][7] + "-" + excelArr[i][9]);
                                        /**Convert into GregorianDates starts**/
                                        transferRulesVO.setStartTime(BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][6])));
                                        transferRulesVO.setEndTime(BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][8])));
                                        /**Convert into GregorianDates ends**/
                                    } else {
                                        transferRulesVO.setMultipleSlab(excelArr[i][8] + "-" + excelArr[i][9]);
                                        /**Convert into GregorianDates starts**/
                                        transferRulesVO.setStartTime(BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][6])));
                                        transferRulesVO.setEndTime(BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][7])));
                                        /**Convert into GregorianDates ends**/
                                    }

                                } catch (ParseException e1) {
                                    errorVO = new ListValueVO("", transferRulesVO.getRowID(), RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.INVALID_START_DATE,null));
                                    fileErrorList.add(errorVO);
                                    LOG.errorTrace(methodName, e1);
                                    continue;
                                }
                            }

                            transferRulesVO.setRuleType(ruleType);
                            transferRulesVO.setRuleLevel(rulLevel);
                            fileValiedList.add(transferRulesVO);
                        } else {
                            errorVO = new ListValueVO("", transferRulesVO.getRowID(), error);
                            fileErrorList.add(errorVO);
                        }
                    }

                    if (!(fileValiedList.isEmpty())) {
                        networkadmintransferwebDAO.addPromotionalTransferRuleFile(con, fileValiedList, fileErrorList, locale, promotionLevel, category_code, geographicalCode);
                        con.commit();
                    }

                    if (!(fileErrorList.isEmpty())) {
                        // error page link
                        for (int i = 0; i < fileErrorList.size(); i++) {
                            errorVO = (ListValueVO) fileErrorList.get(i);
                            // errorVO.setOtherInfo(map.get(errorVO.getOtherInfo()));
                            fileErrorList.set(i, errorVO);
                        }
                        responseVO.setTotalRecords((rows-1)-blankLines);
                        responseVO.setValidRecords((rows-1)-blankLines-fileErrorList.size());


                    } if(fileErrorList.size() == 0){
                        // success message
                        String msg = RestAPIStringParser.getMessage(locale,
                                PretupsErrorCodesI.UPLOAD_AND_PROCESS_FILE_SUCCESS, null);
                        responseVO.setMessage(msg);
                        responseVO.setStatus((HttpStatus.SC_OK));
                        response1.setStatus(HttpStatus.SC_OK);
                        responseVO.setMessageCode(PretupsErrorCodesI.UPLOAD_AND_PROCESS_FILE_SUCCESS);
                        final AdminOperationVO adminOperationVO = new AdminOperationVO();
                        adminOperationVO.setSource(PretupsI.LOGGER_TRANSFER_RULE_SOURCE);
                        adminOperationVO.setDate(currentDate);
                        adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
                        adminOperationVO
                                .setInfo("add batch promotional Transfer rule has added successfully  ");
                        adminOperationVO.setLoginID(userVO.getLoginID());
                        adminOperationVO.setUserID(userVO.getUserID());
                        adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                        adminOperationVO.setNetworkCode(userVO.getNetworkID());
                        adminOperationVO.setMsisdn(userVO.getMsisdn());
                        AdminOperationLog.log(adminOperationVO);
                        downloadErrorLogFile(userVO, responseVO);
                        responseVO.setErrorFlag(PretupsI.FALSE);


                    }

                    else if(fileErrorList.size() == (rows-1)-blankLines) {
                        String msg = RestAPIStringParser.getMessage(locale,
                                PretupsErrorCodesI.UPLOAD_AND_PROCESS_FILE_FAIL, new String[] { "" });
                        responseVO.setMessage(msg);
                        responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
                        response1.setStatus(PretupsI.RESPONSE_FAIL);
                        responseVO.setMessageCode(PretupsErrorCodesI.UPLOAD_AND_PROCESS_FILE_FAIL);
                        responseVO.setErrorList(fileErrorList);
                        deleteFile(dir, fileRequest, userVO);
                        downloadErrorLogFile(userVO, responseVO);
                        responseVO.setErrorFlag(PretupsI.TRUE);
                    }
                    else if(fileErrorList.size()> 0  ){
                        String msg = RestAPIStringParser.getMessage(locale,
                                PretupsErrorCodesI.FILE_UPLOAD_AND_PROCCESS_PARTIALLY_SUCCESSFULL, new String[] { "" });
                        responseVO.setMessage(msg);
                        responseVO.setStatus(HttpStatus.SC_BAD_REQUEST);
                        response1.setStatus(PretupsI.RESPONSE_SUCCESS);
                        responseVO.setMessageCode(PretupsErrorCodesI.UPLOAD_CONTAIN_ERRORS);
                        responseVO.setErrorList(fileErrorList);
                        responseVO.setErrorFlag(PretupsI.TRUE);

                        final AdminOperationVO adminOperationVO = new AdminOperationVO();
                        adminOperationVO.setSource(PretupsI.LOGGER_TRANSFER_RULE_SOURCE);
                        adminOperationVO.setDate(currentDate);
                        adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
                        adminOperationVO
                                .setInfo("add batch promotional Transfer rule has partially successfull  ");
                        adminOperationVO.setLoginID(userVO.getLoginID());
                        adminOperationVO.setUserID(userVO.getUserID());
                        adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                        adminOperationVO.setNetworkCode(userVO.getNetworkID());
                        adminOperationVO.setMsisdn(userVO.getMsisdn());
                        AdminOperationLog.log(adminOperationVO);
                        downloadErrorLogFile(userVO, responseVO);

                    }

                }
            } else {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.UPLOAD_AND_PROCESS_FILE_FAIL, "");
            }
        }catch(Exception e) {

            throw  e;

        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("uploadAndProcessFile", "Exiting:forward=" );
        }

        return responseVO;

    }


    private String validateCellGroupCode(String p_cellGroupCode, ArrayList p_cellGroupList, MessageResources p_messages, Locale p_locale) {
        final String methodName = "validateCellGroupCode";
        if (LOG.isDebugEnabled()) {
            LOG.debug(methodName, "Entered");
        }
        String error = null;
        try {
            error = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SENDER_CELL_GROUP_CODE_IS_INVALID, null);
            ListValueVO listValueVO = null;
            if (p_cellGroupList != null) {
                for (int i = 0, listSize = p_cellGroupList.size(); i < listSize; i++) {
                    listValueVO = (ListValueVO) p_cellGroupList.get(i);
                    if ((p_cellGroupCode.trim()).equalsIgnoreCase((listValueVO.getValue()).split(":")[0])) {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("validateCellGroupCode", "Exiting:error=" + error);
            }
        }
        return error;
    }


    private String validateServiceGroupCode(String p_serviceGroupCode, ArrayList p_serviceGroupList, MessageResources p_messages, Locale p_locale) {
        final String methodName = "validateServiceGroupCode";
        if (LOG.isDebugEnabled()) {
            LOG.debug("validateServiceGroupCode", "Entered");
        }
        String error = null;
        try {
            error =RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.EXCEL_NOT_VALID,null);
            ListValueVO listValueVO = null;
            if (p_serviceGroupList != null) {
                for (int i = 0, listSize = p_serviceGroupList.size(); i < listSize; i++) {
                    listValueVO = (ListValueVO) p_serviceGroupList.get(i);
                    if ((p_serviceGroupCode.trim()).equalsIgnoreCase((listValueVO.getValue()).split(":")[0])) {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("validateServiceGroupCode", "Exiting:error=" + error);
            }
        }
        return error;
    }


    public String valiedExelData(String[][] excelArr, int i, HashMap masterServiceClass, HashMap masterCardGroup, MessageResources p_messages, Locale p_locale, String p_format, ArrayList p_subServiceTypeIdList, ArrayList p_CardGroupList, String p_dateRange) {
        final String methodName = "valiedExelData";
        if (LOG.isDebugEnabled()) {
            LOG.debug("valiedExelData", "Entered");
        }
        String key1 = null;
        String key2 = null;
        Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);

        try {

            final Date currentDate = new Date();

            for (int j = 0; j < 10; j++) {
                if (excelArr[i][j] == null || excelArr[i][j].isEmpty()) {

                    return excelArr[0][j] + " " +RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.IS_BLANK, null);
                }
            }
            try {
                if ("Y".equalsIgnoreCase(p_dateRange)) {
                    if ((BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][6]) + " " + excelArr[i][7], p_format)).before(currentDate)) {

                        return excelArr[0][6] + " " + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SHOULD_BE_GREATER_THEN_CURRENT_DATE, null);
                    }
                } else {
                    if ((BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][6]) + " " + excelArr[i][8], p_format)).before(currentDate)) {
                        return excelArr[0][6] + " " + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SHOULD_BE_GREATER_THEN_CURRENT_DATE, null);
                    }
                }
            } catch (ParseException e1) {
                LOG.errorTrace(methodName, e1);
                return excelArr[0][6] + " " + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.INVALID_START_DATE_AND_TIME, null);
            }

            try {
                if ("Y".equalsIgnoreCase(p_dateRange)) {
                    if ((BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][8]) + " " + excelArr[i][9], p_format)).before(currentDate)) {
                        return excelArr[0][8] + " " + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SHOULD_BE_GREATER_THEN_CURRENT_DATE, null);
                    }
                } else {
                    if ((BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][7]) + " " + excelArr[i][9], p_format)).before(currentDate)) {
                        return excelArr[0][8] + " " + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SHOULD_BE_GREATER_THEN_CURRENT_DATE, null);
                    }
                }
            } catch (ParseException e1) {
                LOG.errorTrace(methodName, e1);
                return excelArr[0][8] + " " + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.INVALID_END_DATE_AND_TIME, null);
            }

            Date fromDate = null;
            Date tillDate = null;

            if ("Y".equalsIgnoreCase(p_dateRange)) {
                fromDate = BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][6]) + " " + excelArr[i][7], p_format);
                tillDate = BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][8]) + " " + excelArr[i][9], p_format);

            } else {
                fromDate = BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][6]) + " " + excelArr[i][8], p_format);
                tillDate = BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][7]) + " " + excelArr[i][9], p_format);
            }

            if (!tillDate.equals(fromDate)) {
                if ((tillDate.before(fromDate))) {
                    return  RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.APPLICABLE_FROM_DATE_AND_TIME_MUST_BE_LESS_THAN_APPLICABLE_TILL_DATE_AND_TIME, null);
                }
            }

            String str = null;
            boolean subTypFlag = false;
            boolean servClassIdFlag = false;
            Set set = masterServiceClass.keySet();
            Iterator itr = set.iterator();
            while (itr.hasNext()) {
                str = (String) itr.next();
                if (!((str.split("_")[0]).equalsIgnoreCase(excelArr[i][1].trim()))) {
                    subTypFlag = false;
                    continue;
                } else {
                    subTypFlag = true;
                    break;
                }
            }
            if (!subTypFlag) {
                return RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.INVALID_RECEIVER_SUBSCRIBER_TYPE, null);
            }

            itr = null;
            str = null;
            itr = set.iterator();
            while (itr.hasNext()) {
                str = (String) itr.next();
                if (!((str.split("_")[1]).equalsIgnoreCase(excelArr[i][2].trim()))) {
                    servClassIdFlag = false;
                    continue;
                } else {
                    servClassIdFlag = true;
                    break;
                }
            }
            if (!servClassIdFlag) {
                return RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.INVALID_RECEIVER_SERVICE_CLASS_ID, null);
            }

            set = null;
            set = masterCardGroup.keySet();
            itr = null;
            str = null;
            boolean srvTypeFlag = false;
            itr = set.iterator();
            while (itr.hasNext()) {
                str = (String) itr.next();
                if (!((str.split("_")[1]).equalsIgnoreCase(excelArr[i][3].trim()))) {
                    srvTypeFlag = false;
                    continue;
                } else {
                    srvTypeFlag = true;
                    break;
                }
            }
            if (!srvTypeFlag) {
                return RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.INVALID_SERVICE_TYPE, null);
            }

            itr = null;
            str = null;
            boolean subSrvCodeFlag = false;
            itr = set.iterator();
            while (itr.hasNext()) {
                str = (String) itr.next();
                if (!((str.split("_")[2]).equalsIgnoreCase(excelArr[i][4].trim()))) {
                    subSrvCodeFlag = false;
                    continue;
                } else {
                    subSrvCodeFlag = true;
                    break;
                }
            }
            if (!subSrvCodeFlag) {
                return RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.INVALID_SUB_SERVICE_CODE, null);
            }

            ListValueVO listValueVO = null;
            boolean isValid = false;
            for (int j = 0, size = p_subServiceTypeIdList.size(); j < size; j++) {
                listValueVO = (ListValueVO) p_subServiceTypeIdList.get(j);
                if (listValueVO.getValue().split(":")[1].equals(excelArr[i][1].trim()) && listValueVO.getValue().split(":")[3].equals(excelArr[i][3].trim()) && listValueVO
                        .getValue().split(":")[2].equals(excelArr[i][4].trim())) {
                    isValid = true;
                    break;
                }

            }
            if (!isValid) {
                return RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SELECTOR_VALUE_IS_NOT_VALID_FOR_SUBSCRIBER_TYPE,
                        new String[] { excelArr[i][1].trim() });
            }

            listValueVO = null;
            isValid = false;
            for (int j = 0, size = p_CardGroupList.size(); j < size; j++) {
                listValueVO = (ListValueVO) p_CardGroupList.get(j);
                if (listValueVO.getValue().split(":")[0].equals(excelArr[i][4].trim()) && listValueVO.getValue().split(":")[1].equals(excelArr[i][5].trim()) && listValueVO
                        .getValue().split(":")[2].equals(excelArr[i][3].trim())) {
                    isValid = true;
                    break;
                }

            }
            if (!isValid) {
                return RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.INVALID_CARD_GROUP_SET_ID, null);
            }

            key1 = excelArr[i][1] + "_" + excelArr[i][2];
            key2 = excelArr[i][1] + "_" + excelArr[i][3] + "_" + excelArr[i][4] + "_" + excelArr[i][5];

            if (!(masterServiceClass.containsKey(key1) && masterCardGroup.containsKey(key2))) {
                return RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.INVALID_TRANSFER_RULE, null);
            }

        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("valiedExelData", "Exiting");
            }
        }
        return null;

    }


    public String valiedExelDataForCLGroup(String[][] excelArr, int i, HashMap masterServiceClass, HashMap masterCardGroup, MessageResources p_messages, Locale p_locale, String p_format, ArrayList p_subServiceTypeIdList, ArrayList p_CardGroupList, String p_dateRange, ArrayList p_subsciberStatusList, String p_promotionLevel, ArrayList p_serviceGroupList) {
        final String methodName = "valiedExelDataForCLGroup";
        if (LOG.isDebugEnabled()) {
            LOG.debug("valiedExelDataForCLGroup", "Entered p_promotionLevel : " + p_promotionLevel);
        }
        String key1 = null;
        String key2 = null;
        boolean serviceProviderPromoAllow = (boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.SERVICE_PROVIDER_PROMO_ALLOW);
        try {
            if (p_promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_SERVICE)) {
                final Date currentDate = new Date();
                for (int j = 0; j < 11; j++) {
                    if (excelArr[i][j] == null || excelArr[i][j].isEmpty()) {
                        return excelArr[2][j] + " " +RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.IS_BLANK, null);
                    }
                }
                try {

                    if ("Y".equalsIgnoreCase(p_dateRange)) {
                        if ((BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][7]) + " " + excelArr[i][8], p_format)).before(currentDate)) {
                            return excelArr[0][7] + " " +RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SHOULD_BE_GREATER_THEN_CURRENT_DATE, null); //p_messages.getMessage(p_locale,
                            //"promotionaltransferrule.addbatchpromotionaltransferrule.valiedExelData.lessthencurrentdate");
                        }
                    } else {
                        if ((BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][7]) + " " + excelArr[i][9], p_format)).before(currentDate)) {
                            return excelArr[0][7] + " " + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SHOULD_BE_GREATER_THEN_CURRENT_DATE, null);// p_messages.getMessage(p_locale,
                            //"promotionaltransferrule.addbatchpromotionaltransferrule.valiedExelData.lessthencurrentdate");
                        }
                    }
                } catch (ParseException e1) {
                    LOG.errorTrace(methodName, e1);
                    return excelArr[0][7] + " " +RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.INVALID_START_DATE_AND_TIME, null);// p_messages.getMessage(p_locale, "promotionaltransferrule.addbatchpromotionaltransferrule.valiedExelData.invaliedstartdate");
                }
                try {
                    if ("Y".equalsIgnoreCase(p_dateRange)) {
                        if ((BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][9]) + " " + excelArr[i][10], p_format)).before(currentDate)) {
                            return excelArr[0][9] + " " + excelArr[0][10] + " "  + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SHOULD_BE_GREATER_THEN_CURRENT_DATE, null);
                        }
                    } else {
                        if ((BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][8]) + " " + excelArr[i][10], p_format)).before(currentDate)) {
                            return excelArr[0][9] + " " + excelArr[0][10] + " " + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SHOULD_BE_GREATER_THEN_CURRENT_DATE, null);
                        }
                    }
                } catch (ParseException e1) {
                    LOG.errorTrace(methodName, e1);
                    return excelArr[0][9] + " " + excelArr[0][10] + " " + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.INVALID_END_DATE_AND_TIME, null);
                }
                Date fromDate = null;
                Date tillDate = null;

                if ("Y".equalsIgnoreCase(p_dateRange)) {
                    fromDate = BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][7]) + " " + excelArr[i][8], p_format);
                    tillDate = BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][9]) + " " + excelArr[i][10], p_format);

                } else {
                    fromDate = BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][7]) + " " + excelArr[i][9], p_format);
                    tillDate = BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][8]) + " " + excelArr[i][10], p_format);
                }

                if (!tillDate.equals(fromDate)) {
                    if ((tillDate.before(fromDate))) {
                        return RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.APPLICABLE_FROM_DATE_AND_TIME_MUST_BE_LESS_THAN_APPLICABLE_TILL_DATE_AND_TIME, null);
                    }
                }

                String str = null;
                boolean subTypFlag = false;
                boolean servClassIdFlag = false;
                Set set = masterServiceClass.keySet();
                Iterator itr = set.iterator();
                while (itr.hasNext()) {
                    str = (String) itr.next();
                    if (!((str.split("_")[0]).equalsIgnoreCase(excelArr[i][1].trim()))) {
                        subTypFlag = false;
                        continue;
                    } else {
                        subTypFlag = true;
                        break;
                    }
                }
                if (!subTypFlag) {
                    return RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.INVALID_RECEIVER_SUBSCRIBER_TYPE, null);
                }

                itr = null;
                str = null;
                itr = set.iterator();
                while (itr.hasNext()) {
                    str = (String) itr.next();
                    if (!((str.split("_")[1]).equalsIgnoreCase(excelArr[i][2].trim()))) {
                        servClassIdFlag = false;
                        continue;
                    } else {
                        servClassIdFlag = true;
                        break;
                    }
                }
                if (!servClassIdFlag) {
                    return RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.INVALID_RECEIVER_SERVICE_CLASS_ID, null);
                }

                set = null;
                set = masterCardGroup.keySet();
                itr = null;
                str = null;
                boolean srvTypeFlag = false;
                itr = set.iterator();
                while (itr.hasNext()) {
                    str = (String) itr.next();
                    if (!((str.split("_")[1]).equalsIgnoreCase(excelArr[i][4].trim()))) {
                        srvTypeFlag = false;
                        continue;
                    } else {
                        srvTypeFlag = true;
                        break;
                    }
                }
                if (!srvTypeFlag) {
                    return RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.INVALID_SERVICE_TYPE, null);
                }

                itr = null;
                str = null;
                boolean subSrvCodeFlag = false;
                itr = set.iterator();
                while (itr.hasNext()) {
                    str = (String) itr.next();
                    if (!((str.split("_")[2]).equalsIgnoreCase(excelArr[i][5].trim()))) {
                        subSrvCodeFlag = false;
                        continue;
                    } else {
                        subSrvCodeFlag = true;
                        break;
                    }
                }
                if (!subSrvCodeFlag) {
                    return RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.INVALID_SUB_SERVICE_CODE, null);
                }

                ListValueVO listValueVO = null;
                boolean isValid = false;
                for (int j = 0, size = p_subServiceTypeIdList.size(); j < size; j++) {
                    listValueVO = (ListValueVO) p_subServiceTypeIdList.get(j);
                    if (listValueVO.getValue().split(":")[1].equals(excelArr[i][1].trim()) && listValueVO.getValue().split(":")[3].equals(excelArr[i][4].trim()) && listValueVO
                            .getValue().split(":")[2].equals(excelArr[i][5].trim())) {
                        isValid = true;
                        break;
                    }

                }
                if (!isValid) {
                    return RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SELECTOR_VALUE_IS_NOT_VALID_FOR_SUBSCRIBER_TYPE,
                            new String[] { excelArr[i][1].trim() });
                }

                listValueVO = null;
                isValid = false;
                for (int j = 0, size = p_CardGroupList.size(); j < size; j++) {
                    listValueVO = (ListValueVO) p_CardGroupList.get(j);
                    if (listValueVO.getValue().split(":")[0].equals(excelArr[i][5].trim()) && listValueVO.getValue().split(":")[1].equals(excelArr[i][6].trim()) && listValueVO
                            .getValue().split(":")[2].equals(excelArr[i][4].trim())) {
                        isValid = true;
                        break;
                    }

                }
                if (!isValid) {
                    return RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.INVALID_CARD_GROUP_SET_ID, null);
                }

                key1 = excelArr[i][1] + "_" + excelArr[i][2];
                key2 = excelArr[i][1] + "_" + excelArr[i][4] + "_" + excelArr[i][5] + "_" + excelArr[i][6];

                if (!(masterServiceClass.containsKey(key1) && masterCardGroup.containsKey(key2))) {
                    return RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.INVALID_TRANSFER_RULE, null);
                }
            }

            else if (!p_promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_SERVICE)) {
                final Date currentDate = new Date();
                for (int j = 0; j < 12; j++) {
                    if (!serviceProviderPromoAllow) {
                        if (j == 3 || j == 4) {
                            continue;
                        }
                    }
                    if (excelArr[i][j] == null || excelArr[i][j].isEmpty()) {
                        String isBlank = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.IS_BLANK, null);
                        String keyName = null;
                        if(j==0) {
                            if (p_promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_GRD)) {
                                // Sender Grade
                                keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SENDER_GRADE_CODE, null);
                            }

                            if (p_promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_CAT)) {
                                // Sender Category code
                                keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SENDER_CATEGORY_CODE, null);

                            }
                            if (p_promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_GRP)) {
                                // Sender Geographical domain code
                                keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SENDER_GEOGRAPHICAL_DOMAIN_CODE, null);

                            }
                            if (p_promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_USR)) {
                                // Sender Mobile No
                                keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.MOBILE_NUMBER, null);

                            }
                            if (p_promotionLevel.equalsIgnoreCase(PretupsI.PROMOTIONAL_BATCH_TRF_RULE_RULE_LEVEL_CEL)) {
                                keyName = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.CELL_GROUP_CODE, null);

                            }

                            return keyName +" "+ isBlank;

                        }

                        if(j== 8)
                            return RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.APPLICABLE_FROM_DATE_1, null) + " " + isBlank;

                        if(j== 10)
                            return RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.APPLICABLE_TILL_DATE_1, null) + " " + isBlank;


                    }
                }
                try {
                    if ("Y".equalsIgnoreCase(p_dateRange)) {
                        String str=null;
                        if(excelArr[i][9] == null||excelArr[i][9].isEmpty()) {
                            str= "00:00";
                        }else {
                            str = excelArr[i][9];
                        }

                        if ((BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][8]) + " " + str, p_format)).before(currentDate)) {
                            return excelArr[i][8] + " " + excelArr[i][9] + " " + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SHOULD_BE_GREATER_THEN_CURRENT_DATE, null);
                        }
                    } else {
                        if ((BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][8]) + " " + excelArr[i][10], p_format)).before(currentDate)) {
                            return excelArr[i][8] + " " + excelArr[i][9] + " " + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SHOULD_BE_GREATER_THEN_CURRENT_DATE, null);
                        }
                    }
                } catch (ParseException e1) {
                    LOG.errorTrace(methodName, e1);
                    return excelArr[i][8] + " " + excelArr[i][9] + " " +RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.INVALID_START_DATE_AND_TIME, null);
                }
                try {
                    if ("Y".equalsIgnoreCase(p_dateRange)) {
                        String str = null;
                        if(excelArr[i][11] == null||excelArr[i][11].isEmpty()) {
                            str= "00:00";
                        }
                        else {
                            str = excelArr[i][11];
                        }
                        if ((BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][10]) + " " + str, p_format)).before(currentDate)) {
                            return excelArr[i][10] + " " + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SHOULD_BE_GREATER_THEN_CURRENT_DATE, null);
                        }
                    } else {
                        if ((BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][9]) + " " + excelArr[i][11], p_format)).before(currentDate)) {
                            return excelArr[i][10] + " " + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SHOULD_BE_GREATER_THEN_CURRENT_DATE, null);
                        }
                    }
                } catch (ParseException e1) {
                    LOG.errorTrace(methodName, e1);
                    return excelArr[i][10] + " " + RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.INVALID_END_DATE_AND_TIME, null);
                }
                Date fromDate = null;
                Date tillDate = null;

                if ("Y".equalsIgnoreCase(p_dateRange)) {
                    String startTime=null;
                    if(excelArr[i][9] == null||excelArr[i][9].isEmpty()) {
                        startTime= "00:00";
                    }else {
                        startTime = excelArr[i][9];
                    }
                    String endTime = null;
                    if(excelArr[i][11] == null||excelArr[i][11].isEmpty()) {
                        endTime= "00:00";
                    }
                    else {
                        endTime = excelArr[i][11];
                    }

                    fromDate = BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][8]) + " " + startTime, p_format);
                    tillDate = BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][10]) + " " + endTime, p_format);

                } else {
                    fromDate = BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][8]) + " " + excelArr[i][10], p_format);
                    tillDate = BTSLUtil.getDateFromDateString(BTSLDateUtil.getGregorianDateInString(excelArr[i][9]) + " " + excelArr[i][11], p_format);
                }

                if (!tillDate.equals(fromDate)) {
                    if ((tillDate.before(fromDate))) {

                        return RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.APPLICABLE_FROM_DATE_AND_TIME_MUST_BE_LESS_THAN_APPLICABLE_TILL_DATE_AND_TIME, null);

                    }
                }
                String str = null;
                boolean subTypFlag = false;
                boolean servClassIdFlag = false;
                Set set = masterServiceClass.keySet();
                Iterator itr = set.iterator();
                if(!excelArr[i][1].isEmpty()) {
                    while (itr.hasNext()) {
                        str = (String) itr.next();
                        if (!((str.split("_")[0]).equalsIgnoreCase(excelArr[i][1].trim()))) {
                            subTypFlag = false;
                            continue;
                        } else {
                            subTypFlag = true;
                            break;
                        }
                    }
                    if (!subTypFlag) {
                        return RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.INVALID_RECEIVER_SUBSCRIBER_TYPE, null);
                    }
                }
                TransferVO transferVO = null;
                boolean isValid1 = false;
                if (serviceProviderPromoAllow) {
                    if(!excelArr[i][1].isEmpty()) {
                        for (int j = 0, size = p_subsciberStatusList.size(); j < size; j++) {
                            transferVO = (TransferVO) p_subsciberStatusList.get(j);
                            if (transferVO.getServiceType().equalsIgnoreCase(excelArr[i][1].trim())) {
                                if (transferVO.getSubscriberStatus().equalsIgnoreCase(excelArr[i][3].toUpperCase())) {
                                    isValid1 = true;
                                    break;
                                }
                            }
                        }
                        if (!isValid1) {
                            return RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.INVALID_SUBSCRIBER_STATUS,
                                    new String[] { excelArr[i][3].trim() });
                        }

                        ServiceGpMgmtVO servicemgmtVO = null;
                        isValid1 = false;
                        for (int j = 0, size = p_serviceGroupList.size(); j < size; j++) {
                            servicemgmtVO = (ServiceGpMgmtVO) p_serviceGroupList.get(j);
                            if (transferVO.getServiceType().equalsIgnoreCase(excelArr[i][1].trim())) {
                                if (servicemgmtVO.getGroupId().equalsIgnoreCase(excelArr[i][4].toUpperCase())) {
                                    isValid1 = true;
                                    break;
                                }
                            }
                        }
                        if (!isValid1) {
                            return (RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.INVALID_SERVICE_PROVIDER_GROUP,
                                    new String[] { excelArr[i][4].trim() }));
                        }
                    }
                }
                itr = null;
                str = null;
                if(!excelArr[i][2].isEmpty()) {
                    itr = set.iterator();
                    while (itr.hasNext()) {
                        str = (String) itr.next();
                        if (!((str.split("_")[1]).equalsIgnoreCase(excelArr[i][2].trim()))) {
                            servClassIdFlag = false;
                            continue;
                        } else {
                            servClassIdFlag = true;
                            break;
                        }
                    }
                    if (!servClassIdFlag) {
                        return RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.INVALID_RECEIVER_SERVICE_CLASS_ID, null);
                    }
                }
                set = null;
                set = masterCardGroup.keySet();
                itr = null;
                str = null;
                if(!excelArr[i][5].isEmpty()) {
                    boolean srvTypeFlag = false;
                    itr = set.iterator();
                    while (itr.hasNext()) {
                        str = (String) itr.next();
                        if (!((str.split("_")[1]).equalsIgnoreCase(excelArr[i][5].trim()))) {
                            srvTypeFlag = false;
                            continue;
                        } else {
                            srvTypeFlag = true;
                            break;
                        }
                    }
                    if (!srvTypeFlag) {
                        return RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.INVALID_SERVICE_TYPE, null);
                    }
                }
                itr = null;
                str = null;
                if(!excelArr[i][6].isEmpty()) {
                    boolean subSrvCodeFlag = false;
                    itr = set.iterator();
                    while (itr.hasNext()) {
                        str = (String) itr.next();
                        if (!((str.split("_")[2]).equalsIgnoreCase(excelArr[i][6].trim()))) {
                            subSrvCodeFlag = false;
                            continue;
                        } else {
                            subSrvCodeFlag = true;
                            break;
                        }
                    }
                    if (!subSrvCodeFlag) {
                        return RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.INVALID_SUB_SERVICE_CODE, null);
                    }
                }
                ListValueVO listValueVO = null;
                boolean isValid = false;
                if(!excelArr[i][1].isEmpty()&& !excelArr[i][5].isEmpty()&& !excelArr[i][6].isEmpty()) {

                    for (int j = 0, size = p_subServiceTypeIdList.size(); j < size; j++) {
                        listValueVO = (ListValueVO) p_subServiceTypeIdList.get(j);
                        if (listValueVO.getValue().split(":")[1].equals(excelArr[i][1].trim()) && listValueVO.getValue().split(":")[3].equals(excelArr[i][5].trim()) && listValueVO
                                .getValue().split(":")[2].equals(excelArr[i][6].trim())) {
                            isValid = true;
                            break;
                        }

                    }
                    if (!isValid) {
                        return (RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SELECTOR_VALUE_IS_NOT_VALID_FOR_SUBSCRIBER_TYPE,
                                new String[] { excelArr[i][1].trim() }));
                    }
                }
                listValueVO = null;
                isValid = false;
                if(!excelArr[i][1].isEmpty()&&!excelArr[i][5].isEmpty()&& !excelArr[i][6].isEmpty()&& !excelArr[i][7].isEmpty()) {

                    for (int j = 0, size = p_CardGroupList.size(); j < size; j++) {
                        listValueVO = (ListValueVO) p_CardGroupList.get(j);
                        if (listValueVO.getValue().split(":")[0].equals(excelArr[i][6].trim()) && listValueVO.getValue().split(":")[1].equals(excelArr[i][7].trim()) && listValueVO
                                .getValue().split(":")[2].equals(excelArr[i][5].trim())) {
                            isValid = true;
                            break;
                        }

                    }
                    if (!isValid) {
                        return (RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.INVALID_CARD_GROUP_SET_ID, null));
                    }

                    key1 = excelArr[i][1] + "_" + excelArr[i][2];
                    key2 = excelArr[i][1] + "_" + excelArr[i][5] + "_" + excelArr[i][6] + "_" + excelArr[i][7];

                    if (!(masterServiceClass.containsKey(key1) && masterCardGroup.containsKey(key2))) {
                        return RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.INVALID_TRANSFER_RULE, null);
                    }
                }
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("valiedExelDataForCLGroup", "Exiting");
            }
        }
        return null;

    }



    private HashMap serviceClassMap(ArrayList p_list_Pre) {
        final String methodName = "serviceClassMap";
        if (LOG.isDebugEnabled()) {
            LOG.debug("serviceClassMap", "Entered");
        }
        HashMap servviceClass = null;
        try {
            servviceClass = new HashMap();
            int pos = 0;
            String temp = null;
            ListValueVO listValueVO = null;
            for (int i = 0, listSize = p_list_Pre.size(); i < listSize; i++) {
                listValueVO = (ListValueVO) p_list_Pre.get(i);
                temp = listValueVO.getValue();
                String subscriberType = temp.substring(0, (pos = temp.indexOf(":")));
                subscriberType = subscriberType + "_" + temp.substring(++pos);
                servviceClass.put(subscriberType, subscriberType);
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("serviceClassMap", "Exiting:servviceClass=" + servviceClass);
            }
        }
        return servviceClass;
    }


    private HashMap cardGroupMap(ArrayList p_cardGroupList)

    {
        final String methodName = "cardGroupMap";
        if (LOG.isDebugEnabled()) {
            LOG.debug("cardGroupMap", "Entered");
        }
        HashMap cardGroup = null;
        try {
            final ArrayList subscriberTypeList = LookupsCache.loadLookupDropDown(PretupsI.SUBSRICBER_TYPE, true);
            final ArrayList subServiceList = ServiceSelectorMappingCache.loadSelectorDropDownForTrfRule();
            ListValueVO subcriberTypeVO = null;
            ListValueVO listValueVO = null;
            cardGroup = new HashMap();
            if (p_cardGroupList != null) {
                String cardSubServiceId = null;
                String cardServiceType = null;
                String cardGroupId = null;
                String card = null;
                String cardGroupKey = null;
                String arr[] = null;
                int cPos = 0;
                for (int s1 = 0, subscriberTypeListSize = subscriberTypeList.size(); s1 < subscriberTypeListSize; s1++) {
                    subcriberTypeVO = (ListValueVO) subscriberTypeList.get(s1);
                    //
                    for (int s = 0, listSize = p_cardGroupList.size(); s < listSize; s++) {
                        cardGroupKey = null;
                        listValueVO = (ListValueVO) p_cardGroupList.get(s);
                        card = listValueVO.getValue();
                        cardSubServiceId = card.substring(0, (cPos = card.indexOf(":")));
                        cardGroupId = card.substring(++cPos, (cPos = card.indexOf(":", cPos)));
                        cardServiceType = card.substring(++cPos, card.length());
                        for (int i = 0, subServiceSize = subServiceList.size(); i < subServiceSize; i++) {

                            listValueVO = (ListValueVO) subServiceList.get(i);

                            arr = listValueVO.getValue().split(":");
                            if (subcriberTypeVO.getValue().equalsIgnoreCase(arr[1]) && cardSubServiceId.equalsIgnoreCase(arr[2]) && cardServiceType.equalsIgnoreCase(arr[3])) {
                                cardGroupKey = subcriberTypeVO.getValue() + "_" + cardServiceType + "_" + cardSubServiceId;
                                cardGroupKey = cardGroupKey + "_" + cardGroupId;
                                cardGroup.put(cardGroupKey, cardGroupKey);
                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("cardGroupMap", "Exiting:cardGroup=" + cardGroup);
            }
        }
        return cardGroup;
    }


    private String valiedCategoryCode(String p_catCod, ArrayList p_catCodList, MessageResources p_messages, Locale p_locale) {
        final String methodName = "valiedCategoryCode";
        if (LOG.isDebugEnabled()) {
            LOG.debug("valiedCategoryCode", "Entered");
        }
        String error = null;

        try {
            error = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SENDER_CATEGORY_CODE_IS_INVALID, new String[] {""});
            CategoryVO categoryVO = null;
            if (p_catCodList != null) {
                for (int i = 0, p_catCodsize = p_catCodList.size(); i < p_catCodsize; i++) {
                    categoryVO = (CategoryVO) p_catCodList.get(i);
                    if ((p_catCod.trim()).equalsIgnoreCase(categoryVO.getCategoryCode())) {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("valiedCategoryCode", "Exiting:error=" + error);
            }
        }
        return error;
    }



    private String valiedGradeCode(String p_gradCod, ArrayList p_gradgradList, MessageResources p_messages, Locale p_locale) {
        final String methodName = "valiedGradeCode";
        if (LOG.isDebugEnabled()) {
            LOG.debug("valiedGradeCode", "Entered");
        }
        String error = null;
        try {
            error = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SENDER_GRADE_CODE_IS_INVALID, null);
            ListValueVO listValueVO = null;
            if (p_gradgradList != null) {
                for (int i = 0, listSize = p_gradgradList.size(); i < listSize; i++) {
                    listValueVO = (ListValueVO) p_gradgradList.get(i);
                    if ((p_gradCod.trim()).equalsIgnoreCase((listValueVO.getValue()).split(":")[1])) {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("valiedGradeCode", "Exiting:error=" + BTSLUtil.logForgingReqParam(error));
            }
        }
        return error;
    }


    private String valiedGeogDomainCode(String p_geogDomCod, ArrayList p_geoDomList, MessageResources p_messages, Locale p_locale) {
        final String methodName = "valiedGeogDomainCode";
        if (LOG.isDebugEnabled()) {
            LOG.debug("valiedGeogDomainCode", "Entered");
        }
        String error = null;
        try {
            error = RestAPIStringParser.getMessage(p_locale, PretupsErrorCodesI.SENDER_GEOGDOMAIN_CODE_IS_INVALID, null);
            GeographicalDomainVO geographDomainVO = null;
            if (p_geoDomList != null) {
                for (int i = 0, listSize = p_geoDomList.size(); i < listSize; i++) {
                    geographDomainVO = (GeographicalDomainVO) p_geoDomList.get(i);
                    if ((p_geogDomCod.trim()).equalsIgnoreCase(geographDomainVO.getGrphDomainCode())) {
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            LOG.errorTrace(methodName, e);
        } finally {
            if (LOG.isDebugEnabled()) {
                LOG.debug("valiedGeogDomainCode", "Exiting:error=" + error);
            }
        }
        return error;
    }


    private void deleteFile(String fileStr,  AddBatchPromotionalTransferRuleFileProcessingRequestVO request,
                            UserVO userVO) {

        final String methodName = "deleteFile";
        fileStr = fileStr + request.getFileName()+".xls";
        final File f = new File(fileStr);
        if (f.exists()) {
            try {
                f.delete();
            } catch (Exception e) {

                LOG.errorTrace(methodName, e);
                LOG.error(methodName, "Error in deleting the uploaded file" + f.getName()
                        + " as file validations are failed Exception::" + e);

                final AdminOperationVO adminOperationVO = new AdminOperationVO();
                adminOperationVO.setSource(PretupsI.LOGGER_TRANSFER_RULE_SOURCE);
                adminOperationVO.setDate(new Date());
                adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_DELETE);
                adminOperationVO
                        .setInfo("FAIL : Error in deleting the file, FILE NAME=" + f.getName()  );
                adminOperationVO.setLoginID(userVO.getLoginID());
                adminOperationVO.setUserID(userVO.getUserID());
                adminOperationVO.setCategoryCode(userVO.getCategoryCode());
                adminOperationVO.setNetworkCode(userVO.getNetworkID());
                adminOperationVO.setMsisdn(userVO.getMsisdn());
                AdminOperationLog.log(adminOperationVO);

            }
        }
    }

    public void downloadErrorLogFile(UserVO userVO, UploadAndProcessFileResponseVO response) {
        final String METHOD_NAME = "downloadErrorLogFile";
        if (LOG.isDebugEnabled())
            LOG.debug(METHOD_NAME, "Entered");
        //ActionForward forward = null;
        try {
            ArrayList errorList = response.getErrorList();
            String filePath = Constants.getProperty("UploadBatchPromotionalTrfFilePath");
            try {
                File fileDir = new File(filePath);
                if (!fileDir.isDirectory())
                    fileDir.mkdirs();
            } catch (Exception e) {
                LOG.errorTrace(METHOD_NAME, e);
                LOG.error(METHOD_NAME, "Exception" + e.getMessage());
                throw new BTSLBaseException(CLASS_NAME, METHOD_NAME, "directory not created", 0, null);
            }
            String fileName = "downloadErrorLogFile"

                    + BTSLUtil.getFileNameStringFromDate(new Date()) + ".csv";

            this.writeDataMsisdnInFileDownload(errorList, fileName, filePath, userVO.getNetworkID(),
                    fileName, true);

            File error = new File(filePath+fileName);
            byte[] fileContent = FileUtils.readFileToByteArray(error);
            String encodedString = Base64.getEncoder().encodeToString(fileContent);
            response.setFileAttachment(encodedString);
            response.setFileName(fileName);
            response.setFileType("csv");
        } catch (Exception e) {
            LOG.error(METHOD_NAME, "Exception:e=" + e);
            LOG.errorTrace(METHOD_NAME, e);

        } //finally {
           // if (LOG.isDebugEnabled())
                //LOG.debug(METHOD_NAME, "Exiting:forward=" + forward);
        //}

    }

    public void writeDataMsisdnInFileDownload(ArrayList errorList,String _fileName,String filePath,String _networkCode, String uploadedFileNamePath,Boolean headval)

    {
        final String methodName = "writeDataMsisdnInFileDownload";
        String[] splitFileName = uploadedFileNamePath.split("/");
        String uploadedFileName = splitFileName[(splitFileName.length)-1];
        if (LOG.isDebugEnabled()){
            LOG.debug(methodName,"Entered: "+methodName);
        }
        Writer out =null;
        File newFile = null;
        File newFile1 = null;
        String fileHeader=null;
        String fileName=null;
        try
        {

            Date date= new Date();
            newFile1=new File(filePath);
            if(! newFile1.isDirectory())
                newFile1.mkdirs();
            fileName=filePath+_fileName;
            LOG.debug(methodName,"fileName := "+fileName);
            if(headval){
                fileHeader=Constants.getProperty("ERROR_FILE_HEADER_MOVEUSER");
            }
            else{
                fileHeader=Constants.getProperty("ERROR_FILE_HEADER_PAYOUT");
            }
            newFile = new File(fileName);
            out = new OutputStreamWriter(new FileOutputStream(newFile));
            out.write(fileHeader +"\n");
            errorList.sort ((o1,o2)->Integer.parseInt(((ListValueVO) o1).getOtherInfo())-(Integer.parseInt(((ListValueVO) o2).getOtherInfo())));

            for (Iterator<ListValueVO> iterator = errorList.iterator(); iterator.hasNext();) {

                ListValueVO listValueVO =iterator.next();
                out.write(listValueVO.getOtherInfo()+",");
                if(!headval){
                    out.write(listValueVO.getCodeName()+",");
                }
                out.write(listValueVO.getOtherInfo2()+",");

                out.write(",");
                out.write("\n");
            }
            out.write("End");

        }
        catch(Exception e)
        {

            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR,EventComponentI.SYSTEM,EventStatusI.RAISED,EventLevelI.FATAL,"writeDataInFile[writeDataInFile]","","","","Exception:= "+e.getMessage());
        }
        finally
        {
            if (LOG.isDebugEnabled()){
                LOG.debug(methodName,"Exiting... ");
            }
            if (out!=null)
                try{
                    out.close();
                }
                catch(Exception e){
                    LOG.errorTrace(methodName, e);
                }

        }
    }


}


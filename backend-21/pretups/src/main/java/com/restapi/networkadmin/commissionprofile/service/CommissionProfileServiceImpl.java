package com.restapi.networkadmin.commissionprofile.service;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BTSLMessages;
import com.btsl.common.BaseResponse;
import com.btsl.common.IDGenerator;
import com.btsl.common.ListSorterUtil;
import com.btsl.common.ListValueVO;
import com.btsl.common.TypesI;
import com.btsl.db.util.MComConnectionI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.channel.profile.businesslogic.AdditionalProfileCombinedVO;
import com.btsl.pretups.channel.profile.businesslogic.AdditionalProfileDeatilsVO;
import com.btsl.pretups.channel.profile.businesslogic.AdditionalProfileServicesVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileCombinedVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDAO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileDeatilsVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileProductsVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVO;
import com.btsl.pretups.channel.profile.businesslogic.CommissionProfileSetVersionVO;
import com.btsl.pretups.channel.profile.businesslogic.OTFDetailsVO;
import com.btsl.pretups.channel.profile.businesslogic.OtfProfileCombinedVO;
import com.btsl.pretups.channel.profile.businesslogic.OtfProfileVO;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.domain.businesslogic.CategoryGradeDAO;
import com.btsl.pretups.domain.businesslogic.GradeVO;
import com.btsl.pretups.gateway.util.RestAPIStringParser;
import com.btsl.pretups.logging.AdminOperationLog;
import com.btsl.pretups.logging.AdminOperationVO;
import com.btsl.pretups.master.businesslogic.LookupsCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingCache;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingDAO;
import com.btsl.pretups.master.businesslogic.ServiceSelectorMappingVO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.preference.businesslogic.SystemPreferences;
import com.btsl.pretups.product.businesslogic.NetworkProductDAO;
import com.btsl.user.businesslogic.UserDAO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLDateUtil;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.commissionProfileMainResponseVO.LoadVersionListBasedOnDateResponseVO;
import com.restapi.networkadmin.commissionprofile.requestVO.AddCommissionProfileRequestVO;
import com.restapi.networkadmin.commissionprofile.requestVO.AdditionalProfileCombinedVONew;
import com.restapi.networkadmin.commissionprofile.requestVO.ChangeStatusForCommissionProfileRequestVO;
import com.restapi.networkadmin.commissionprofile.requestVO.ChangeStatusForCommissionProfileVO;
import com.restapi.networkadmin.commissionprofile.requestVO.CommissionProfileCombinedVONew;
import com.restapi.networkadmin.commissionprofile.requestVO.LoadVersionListBasedOnDateRequestVO;
import com.restapi.networkadmin.commissionprofile.requestVO.ModifyCommissionProfileRequestVO;
import com.restapi.networkadmin.commissionprofile.requestVO.OtfProfileCombinedVONew;
import com.restapi.networkadmin.commissionprofile.requestVO.SusResCommProfileSetRequestVO;
import com.restapi.networkadmin.commissionprofile.responseVO.CommissionProfileGatewayListResponseVO;
import com.restapi.networkadmin.commissionprofile.responseVO.CommissionProfileMainResponseVO;
import com.restapi.networkadmin.commissionprofile.responseVO.CommissionProfileProductListResponseVO;
import com.restapi.networkadmin.commissionprofile.responseVO.CommissionProfileSubServiceListResponseVO;
import com.restapi.networkadmin.commissionprofile.responseVO.CommissionProfileViewListResponseVO;
import com.restapi.networkadmin.commissionprofile.responseVO.CommissionProfileViewResponseVO;
import com.web.pretups.channel.profile.businesslogic.CommissionProfileWebDAO;
import com.web.pretups.domain.businesslogic.CategoryWebDAO;
import com.web.pretups.domain.businesslogic.DomainWebDAO;
import com.web.pretups.gateway.businesslogic.MessageGatewayWebDAO;


@Service("CommissionProfileServiceI")
public class CommissionProfileServiceImpl implements CommissionProfileServiceI{

	public static final Log log = LogFactory.getLog(CommissionProfileServiceImpl.class.getName());
	public static final String classname = "CommissionProfileServiceImpl";
	
	
	@Override
	public BaseResponse addCommissionProfile(MultiValueMap<String, String> headers,HttpServletRequest httpServletRequest, HttpServletResponse response1,
			Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO, BaseResponse response,
			AddCommissionProfileRequestVO addCommissionProfileRequestVO)  throws Exception {
		
		
		final String METHOD_NAME = "addCommissionProfile";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		
        
        try {
        	final Date currentDate = new Date();
            final CommissionProfileDAO commissionDAO = new CommissionProfileDAO();
            final CommissionProfileWebDAO commissionProfileWebDAO = new CommissionProfileWebDAO();
        	
        	
            // check whether the Commission Profile Name is already
            // exist or not

            if (commissionProfileWebDAO.isCommissionProfileSetNameExist(con, userVO.getNetworkID(), addCommissionProfileRequestVO.getProfileName(), null)) {
            	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.COMM_PROF_SET_NAME_EXISTS, 0, null);
            }
            // check whether the Commission Short Code is already exist
            // or not
            if (commissionDAO.isCommissionProfileShortCodeExist(con, userVO.getNetworkID(), addCommissionProfileRequestVO.getShortCode(), null)) {
            	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.SHORT_CODE_EXISTS, 0, null);
            }

            // populating CommissionProfileSetVO from the form
            // parameters
            final CommissionProfileSetVO commissionProfileSetVO = new CommissionProfileSetVO();
            commissionProfileSetVO.setCommProfileSetId(String.valueOf(IDGenerator.getNextID(PretupsI.COMMISSION_PROFILE_SET_ID, TypesI.ALL)));
            commissionProfileSetVO.setCommProfileSetName(addCommissionProfileRequestVO.getProfileName());
            commissionProfileSetVO.setCategoryCode(addCommissionProfileRequestVO.getCategoryCode());
            commissionProfileSetVO.setNetworkCode(userVO.getNetworkID());
            commissionProfileSetVO.setCommLastVersion(addCommissionProfileRequestVO.getVersion());
            commissionProfileSetVO.setCreatedOn(currentDate);
            commissionProfileSetVO.setCreatedBy(userVO.getUserID());
            commissionProfileSetVO.setModifiedOn(currentDate);
            commissionProfileSetVO.setModifiedBy(userVO.getUserID());
            commissionProfileSetVO.setShortCode(addCommissionProfileRequestVO.getShortCode());
            commissionProfileSetVO.setStatus(PretupsI.STATUS_ACTIVE);
            //Handling of GrphDomainCode
            if(BTSLUtil.isNullString(addCommissionProfileRequestVO.getGrphDomainCode())) {
            	addCommissionProfileRequestVO.setGrphDomainCode(PretupsI.ALL);
            }
            commissionProfileSetVO.setGrphDomainCode(addCommissionProfileRequestVO.getGrphDomainCode());
            //Handling of GradeCode
            if(BTSLUtil.isNullString(addCommissionProfileRequestVO.getGradeCode())) {
            	addCommissionProfileRequestVO.setGradeCode(PretupsI.ALL);
            }
            commissionProfileSetVO.setGradeCode(addCommissionProfileRequestVO.getGradeCode());
            commissionProfileSetVO.setDualCommissionType(addCommissionProfileRequestVO.getDualCommType());
            // insert Commission_Profile_Set
            final int insertSetCount = commissionDAO.addCommissionProfileSet(con, commissionProfileSetVO);

            if (insertSetCount <= 0) {
                try {
                    mcomCon.finalRollback();
                } catch (Exception e) {
                    log.errorTrace(METHOD_NAME, e);
                }
                log.error(METHOD_NAME, "Error: while Inserting Commission Profile Set");
                throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GENERAL_ERROR_CODE);
            }
        	
            
            /*
             * insert data into the
             * commission_profile_set_version,commission_profile_products
             * commission_profile_details,
             * additional_commission_profile_details table
             */
            this.addVersion(addCommissionProfileRequestVO, userVO, con, commissionDAO, commissionProfileSetVO, currentDate);
            this.add(addCommissionProfileRequestVO, httpServletRequest, userVO, con, commissionDAO, commissionProfileSetVO, currentDate);
            
        	
        	
        	
        	
        	
        	
            // if above method execute successfully commit the
            // transaction
            mcomCon.finalCommit();
        	
        	
        	//adding logs starts
            final AdminOperationVO adminOperationVO = new AdminOperationVO();
            adminOperationVO.setSource(PretupsI.LOGGER_COMMISSION_PROFILE_SOURCE);
            adminOperationVO.setDate(currentDate);
            adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_ADD);
            adminOperationVO
                .setInfo("Commission Profile for Category(" + commissionProfileSetVO.getCategoryCode() + "), Name(" + commissionProfileSetVO.getCommProfileSetName() + "), ID(" + commissionProfileSetVO
                        .getCommProfileSetId() + ") has been successfully Added");
            adminOperationVO.setLoginID(userVO.getLoginID());
            adminOperationVO.setUserID(userVO.getUserID());
            adminOperationVO.setCategoryCode(userVO.getCategoryCode());
            adminOperationVO.setNetworkCode(userVO.getNetworkID());
            adminOperationVO.setMsisdn(userVO.getMsisdn());
            AdminOperationLog.log(adminOperationVO);
            //adding logs end
	        
			response1.setStatus(HttpStatus.SC_OK);
			response.setStatus((HttpStatus.SC_OK));
			
			final String[] arr = { addCommissionProfileRequestVO.getProfileName() };
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.COMM_PRF_ADD_SUCCESS, arr);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.COMM_PRF_ADD_SUCCESS);

            response.setTransactionId(commissionProfileSetVO.getCommProfileSetId());
        
        }
        finally {
        	if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
        }
		return response;
		
	}

	
	
	


	private void addVersion(AddCommissionProfileRequestVO addCommissionProfileRequestVO, UserVO userVO, Connection con,
			CommissionProfileDAO commissionProfileDAO, CommissionProfileSetVO commissionProfileSetVO, Date currentDate)  throws Exception {
		final String METHOD_NAME = "addVersion";
       
        // populating CommissionProfileSetVersionVO from the from Parameters
        final CommissionProfileSetVersionVO commissionProfileSetVersionVO = new CommissionProfileSetVersionVO();
        commissionProfileSetVersionVO.setCommProfileSetId(commissionProfileSetVO.getCommProfileSetId());
        commissionProfileSetVersionVO.setCommProfileSetVersion(addCommissionProfileRequestVO.getVersion());
        final String format = Constants.getProperty("COMMISSION_DATE_FORMAT");
        String fromHour = null;
        if (BTSLUtil.isNullString(addCommissionProfileRequestVO.getApplicableFromHour())) {
            fromHour = "00:00";
        } else {
            fromHour = addCommissionProfileRequestVO.getApplicableFromHour();
        }
        final Date newDate = BTSLUtil.getDateFromDateString(addCommissionProfileRequestVO.getApplicableFromDate() + " " + fromHour, format);
        commissionProfileSetVersionVO.setApplicableFrom(newDate);
        commissionProfileSetVersionVO.setCreatedBy(userVO.getUserID());
        commissionProfileSetVersionVO.setCreatedOn(currentDate);
        commissionProfileSetVersionVO.setModifiedBy(userVO.getUserID());
        commissionProfileSetVersionVO.setModifiedOn(currentDate);
        commissionProfileSetVersionVO.setDualCommissionType(commissionProfileSetVO.getDualCommissionType());
		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL))).booleanValue())
			commissionProfileSetVersionVO.setOtherCommissionProfileSetID(addCommissionProfileRequestVO.getOtherCommissionProfile());
        // insert Card_Group_Set_Version
        final int insertVersionCount = commissionProfileDAO.addCommissionProfileSetVersion(con, commissionProfileSetVersionVO);

        if (insertVersionCount <= 0) {
            try {
                con.rollback();
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            log.error("add", "Error: while Inserting Commission_Profile_Set_Version");
            throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GENERAL_ERROR_CODE);
        }
	}
	
	
	
	
	
	
	
	private void add(AddCommissionProfileRequestVO addCommissionProfileRequestVO, HttpServletRequest request,
			UserVO userVO, Connection con, CommissionProfileDAO commissionProfileDAO,
			CommissionProfileSetVO commissionProfileSetVO, Date currentDate) throws Exception {
		final String METHOD_NAME = "add";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
		
        /*
         * Insert Data into Commission_Profile_Products and
         * Commission_Profile_Details table
         */
        if (addCommissionProfileRequestVO.getCommissionProfileList() != null && !addCommissionProfileRequestVO.getCommissionProfileList().isEmpty()) {
            CommissionProfileCombinedVONew commissionProfileCombinedVONew = null;
            CommissionProfileProductsVO commissionProfileProductsVO = null;
            for (int i = 0, j = addCommissionProfileRequestVO.getCommissionProfileList().size(); i < j; i++) {
                commissionProfileCombinedVONew = (CommissionProfileCombinedVONew) addCommissionProfileRequestVO.getCommissionProfileList().get(i);

                // set the default valus in the CommissionProfileProductsVO VO
                commissionProfileProductsVO = commissionProfileCombinedVONew.getCommissionProfileProductVO();
                commissionProfileProductsVO.setCommProfileProductID(String.valueOf(IDGenerator.getNextID(PretupsI.COMMISSION_PROFILE_PRODUCT_ID, TypesI.ALL)));
                commissionProfileProductsVO.setCommProfileSetID(commissionProfileSetVO.getCommProfileSetId());
                commissionProfileProductsVO.setVersion(addCommissionProfileRequestVO.getVersion());
				commissionProfileProductsVO.setTransferMultipleOffInDouble(commissionProfileProductsVO.getTransferMultipleOff());
                // insert Commission_Profile_Product
                final int insertProductCount = commissionProfileDAO.addCommissionProfileProduct(con, commissionProfileProductsVO);

                if (insertProductCount <= 0) {
                    try {
                        con.rollback();
                    } catch (Exception e) {
                        log.errorTrace(METHOD_NAME, e);
                    }
                    log.error(METHOD_NAME, "Error: while Inserting Commission_Profile_Product");
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GENERAL_ERROR_CODE);
                }

                // Insert data into Commission_Profile_Details Table
                if (commissionProfileCombinedVONew != null && !commissionProfileCombinedVONew.getSlabsList().isEmpty()) {
                    CommissionProfileDeatilsVO commissionProfileDeatilsVO = null;
                    // set the default values
                    for (int m = 0, n = commissionProfileCombinedVONew.getSlabsList().size(); m < n; m++) {
                        commissionProfileDeatilsVO = (CommissionProfileDeatilsVO) commissionProfileCombinedVONew.getSlabsList().get(m);
                        commissionProfileDeatilsVO.setCommProfileProductsID(commissionProfileProductsVO.getCommProfileProductID());
                        commissionProfileDeatilsVO.setCommProfileDetailID(String.valueOf(IDGenerator.getNextID(PretupsI.COMMISSION_PROFILE_DETAIL_ID, TypesI.ALL)));
                    }
                    // insert Commission_Pofile_Detail List
//                    setDateInVoForm(theForm);
                  
                    final int insertDetailCount = commissionProfileDAO.addCommissionProfileDetailsList(con, commissionProfileCombinedVONew.getSlabsList(),userVO.getNetworkID());

                    if (insertDetailCount <= 0) {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            log.errorTrace(METHOD_NAME, e);
                        }
                        //setDateInVoForm(theForm, request);
                        log.error(METHOD_NAME, "Error: while Inserting Commission_Profile_Details");
                        throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GENERAL_ERROR_CODE);
                    }
                }
            }    

        }
		
        
        /*
         * 
         * Insert Data into commission_profile_Otf and profile_otf_details table
         * 
         */
        this.addOtf(addCommissionProfileRequestVO, con, commissionProfileDAO, commissionProfileSetVO);
        
        
        /*
         * Insert Data into Additional_Commission_Profile_Details and
         * Comm_Profile_Service_Type table
         */
        if (addCommissionProfileRequestVO.getAdditionalProfileList() != null && !addCommissionProfileRequestVO.getAdditionalProfileList().isEmpty()) {
            AdditionalProfileCombinedVONew additionalProfileCombinedVONew = null;
            AdditionalProfileServicesVO additionalProfileServicesVO = null;
            for (int i = 0, j = addCommissionProfileRequestVO.getAdditionalProfileList().size(); i < j; i++) {
                additionalProfileCombinedVONew = (AdditionalProfileCombinedVONew) addCommissionProfileRequestVO.getAdditionalProfileList().get(i);

                // set the default valus in the AdditionalProfileServicesVO VO
                additionalProfileServicesVO = additionalProfileCombinedVONew.getAdditionalProfileServicesVO();
                additionalProfileServicesVO.setCommProfileServiceTypeID(String.valueOf(IDGenerator.getNextID(PretupsI.ADDITIONAL_COMMISSION_SERVICE_ID, TypesI.ALL)));
                additionalProfileServicesVO.setCommProfileSetID(commissionProfileSetVO.getCommProfileSetId());
                additionalProfileServicesVO.setCommProfileSetVersion(addCommissionProfileRequestVO.getVersion());

                // insert Comm_Profile_Service_Type table
                final int insertServiceCount = commissionProfileDAO.addAdditionalProfileService(con, additionalProfileServicesVO);

                if (insertServiceCount <= 0) {
                    try {
                        con.rollback();
                    } catch (Exception e) {
                        log.errorTrace(METHOD_NAME, e);
                    }
                    log.error(METHOD_NAME, "Error: while Inserting Commission_Profile_Service_Type");
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GENERAL_ERROR_CODE);
                }

                // Insert data into Commission_Profile_Details Table
                if (additionalProfileCombinedVONew != null && !additionalProfileCombinedVONew.getSlabsList().isEmpty()) {
                    AdditionalProfileDeatilsVO additionalProfileDeatilsVO = null;
                    int insertDetailCount = 0;
                    // set the default values
                    for (int m = 0, n = additionalProfileCombinedVONew.getSlabsList().size(); m < n; m++) {
                        additionalProfileDeatilsVO = (AdditionalProfileDeatilsVO) additionalProfileCombinedVONew.getSlabsList().get(m);
                        additionalProfileDeatilsVO.setAddtnlComStatus(additionalProfileCombinedVONew.getAdditionalProfileServicesVO().getAddtnlComStatus());
                        additionalProfileDeatilsVO.setCommProfileServiceTypeID(additionalProfileServicesVO.getCommProfileServiceTypeID());
                        additionalProfileDeatilsVO.setAddCommProfileDetailID(String.valueOf(IDGenerator.getNextID(PretupsI.ADDITIONAL_COMMISSION_PROFILE_ID, TypesI.ALL)));
                    }
                    // insert Commission_Pofile_Detail List
                        insertDetailCount = commissionProfileDAO.addAdditionalProfileDetailsList(con, additionalProfileCombinedVONew.getSlabsList(), additionalProfileDeatilsVO.getAddtnlComStatus(),userVO.getNetworkID());
                        if (insertDetailCount <= 0) {
                            try {
                                con.rollback();
                            } catch (Exception e) {
                                log.errorTrace(METHOD_NAME, e);
                            }
                            log.error(METHOD_NAME, "Error: while Inserting Additional_Commission_Profile_Details");
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GENERAL_ERROR_CODE);
                        }

                }
            }
        }
        
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Exiting");
        }
		
	}
	
	
	
	/**
     * @param form
     * @param con
     * @param commissionProfileDAO
     * @param commissionProfileSetVO
     * @throws Exception
     */
    private void addOtf(AddCommissionProfileRequestVO addCommissionProfileRequestVO, Connection con, CommissionProfileDAO commissionProfileDAO, CommissionProfileSetVO commissionProfileSetVO) throws Exception {
        final String METHOD_NAME = "addOtf";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }

        //final CommissionProfileForm theForm = (CommissionProfileForm) form;
        
        /*
         * Insert Data into Comm_Profile_Otf 
         */
        if (addCommissionProfileRequestVO.getOtfProfileList() != null && !addCommissionProfileRequestVO.getOtfProfileList().isEmpty()) {
            OtfProfileCombinedVONew otfProfileCombinedVONew = null;
            for (int i = 0, j = addCommissionProfileRequestVO.getOtfProfileList().size(); i < j; i++) {
            	otfProfileCombinedVONew = (OtfProfileCombinedVONew) addCommissionProfileRequestVO.getOtfProfileList().get(i);
            	
            	//set the values in VO
            	OtfProfileVO otfProfileVO = otfProfileCombinedVONew.getOtfProfileVO();
            	OTFDetailsVO otfDetailsVO = null;
                ArrayList<OTFDetailsVO> slabsList = otfProfileCombinedVONew.getSlabsList();
                otfProfileVO.setCommProfileOtfID(String.valueOf(IDGenerator.getNextID(PretupsI.ADDITIONAL_COMMISSION_PROFILE_ID, TypesI.ALL)));
                otfProfileVO.setCommProfileSetID(commissionProfileSetVO.getCommProfileSetId());
                otfProfileVO.setCommProfileSetVersion(addCommissionProfileRequestVO.getVersion());
                otfProfileVO.setOtfDetails(slabsList);
                 
                 
                //Insert into COMMISSION_PROFILE_OTF
                final int insertCount = commissionProfileDAO.addCommissionProfileOtf(con, otfProfileVO);
                 
                if (insertCount > 0){
                	final int insertCountOtf = commissionProfileDAO.addProfileOtfDetails(con, otfProfileVO);
                }
                else{
                     try {
                         con.rollback();
                     } catch (Exception e) {
                         log.errorTrace(METHOD_NAME, e);
                     }
                     log.error(METHOD_NAME, "Error: while Inserting COMMISSION_PROFILE_OTF");
                     throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GENERAL_ERROR_CODE);
                 }
            }
        }
    }
	
	
    
    
    
    
    
    
    @Override
	public BaseResponse modifyCommissionProfile(MultiValueMap<String, String> headers,
			HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con,
			MComConnectionI mcomCon, Locale locale, UserVO userVO, BaseResponse response,
			ModifyCommissionProfileRequestVO modifyCommissionProfileRequestVO) throws Exception {
		
		final String METHOD_NAME = "modifyCommissionProfile";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		boolean newVersionFlag = false;
		String currentVersion;
		
		try {
			
			final CommissionProfileWebDAO commissionProfileWebDAO = new CommissionProfileWebDAO();
			ArrayList selectCommProfileSetList = new ArrayList();
			ArrayList selectCommProfileVersionList = new ArrayList();
			final Date currentDate = new Date();
			final CommissionProfileDAO commissionDAO = new CommissionProfileDAO();
			
			
			selectCommProfileSetList = commissionProfileWebDAO.loadCommissionProfileSet(con, userVO.getNetworkID(), modifyCommissionProfileRequestVO.getCategoryCode(),
					modifyCommissionProfileRequestVO.getGradeCode(), modifyCommissionProfileRequestVO.getGrphDomainCode());
			selectCommProfileVersionList = commissionProfileWebDAO.loadCommissionProfileSetVersion(con, userVO.getNetworkID(), 
					modifyCommissionProfileRequestVO.getCategoryCode(),currentDate);
			
			

			CommissionProfileSetVO setVO = null;
            // get the selected card group set from the
            // CardGroupSetNameList
			for (int i = 0, j = selectCommProfileSetList.size(); i < j; i++) {
                setVO = (CommissionProfileSetVO) selectCommProfileSetList.get(i);
                if (modifyCommissionProfileRequestVO.getSelectCommProfileSetID().equals(setVO.getCommProfileSetId())) {
                    break;
                }
            }
			
            // check whether the Commission Profile Name is already
            // exist or not
            if (commissionProfileWebDAO.isCommissionProfileSetNameExist(con, userVO.getNetworkID(), modifyCommissionProfileRequestVO.getProfileName(), setVO.getCommProfileSetId())) {
            	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.COMM_PROF_SET_NAME_EXISTS, 0, null);
            }
            // check whether the Commission Short Code is already exist
            // or not
            if (commissionDAO.isCommissionProfileShortCodeExist(con, userVO.getNetworkID(), modifyCommissionProfileRequestVO.getShortCode(), setVO.getCommProfileSetId())) {
            	throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.SHORT_CODE_EXISTS, 0, null);
            }

            /*
             * if the below conditon is true means user change the
             * applicable from date and we
             * need to insert a new version
             */
            final String format = Constants.getProperty("COMMISSION_DATE_FORMAT");
            String fromHour = null;
            if (BTSLUtil.isNullString(modifyCommissionProfileRequestVO.getApplicableFromHour())) {
                fromHour = "00:00";
            } else {
                fromHour = modifyCommissionProfileRequestVO.getApplicableFromHour();
            }
            int version = 0;
            final Date newDate = BTSLUtil.getDateFromDateString(modifyCommissionProfileRequestVO.getApplicableFromDate() + " " + fromHour, format);
            // check whether the Commission Profile is already exist
            // with the same applicable date
            if (commissionProfileWebDAO.isCommissionProfileAlreadyExist(con, newDate, setVO.getCommProfileSetId(), modifyCommissionProfileRequestVO.getVersion())) {
                final String[] arr = { BTSLDateUtil.getLocaleTimeStamp( BTSLUtil.getDateTimeStringFromDate(newDate)) };
                throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.COMM_PROF_ALREADY_EXISTS, arr);
            }
			
            final Date oldDate = BTSLUtil.getDateFromDateString(modifyCommissionProfileRequestVO.getOldApplicableFromDate() + " " + modifyCommissionProfileRequestVO.getOldApplicableFromHour(), format);
            if (oldDate.getTime() != newDate.getTime())// we need to
            // insert the new
            // version
            {
            	currentVersion=modifyCommissionProfileRequestVO.getVersion();
                version = Integer.parseInt(setVO.getCommLastVersion()) + 1;
                modifyCommissionProfileRequestVO.setVersion(String.valueOf(version));
                newVersionFlag = true;
            } else// we need to update the same version
            {
                version = Integer.parseInt(setVO.getCommLastVersion());
                currentVersion=modifyCommissionProfileRequestVO.getVersion();
            }
            
            
            // update Name,shortCode,version in Commission_Profile_Set
            // table
            setVO.setCommProfileSetName(modifyCommissionProfileRequestVO.getProfileName());
            setVO.setShortCode(modifyCommissionProfileRequestVO.getShortCode());
            setVO.setCommLastVersion(String.valueOf(version));
            setVO.setModifiedOn(currentDate);
            setVO.setModifiedBy(userVO.getUserID());
            setVO.setGrphDomainCode(modifyCommissionProfileRequestVO.getGrphDomainCode());
            setVO.setGradeCode(modifyCommissionProfileRequestVO.getGradeCode());
            setVO.setDualCommissionType(modifyCommissionProfileRequestVO.getDualCommType());
            final int updateCount = commissionProfileWebDAO.updateCommissionProfileSet(con, setVO);
            if (updateCount <= 0) {
                try {
                    mcomCon.finalRollback();
                } catch (Exception e) {
                    log.errorTrace(METHOD_NAME, e);
                }
                log.error(METHOD_NAME, "Error: while Updating CommissionProfile_Set");
                throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GENERAL_ERROR_CODE);
            }
            
            
            if (oldDate.getTime() != newDate.getTime())// we need to
            // insert the new
            // version
            {
                /*
                 * insert data into the commission_profile_set_version,
                 * commission_profile_products
                 * commission_profile_details,
                 * additional_commission_profile_details table
                 */
                this.addVersion(modifyCommissionProfileRequestVO, userVO, con, commissionDAO, setVO, currentDate);
                this.add(modifyCommissionProfileRequestVO, httpServletRequest, userVO, con, commissionDAO, setVO, currentDate);
            } else// we need to update the same version
            {
                // update the same version
                this.updateSetVersion(modifyCommissionProfileRequestVO, userVO, con, commissionDAO, currentDate, selectCommProfileVersionList);
                /*
                 * Delete data from Commission_Profile_Products and
                 * Comm_profile_Service_Types,
                 * Commission_Profile_Details and
                 * Additional_Commission_Profile_Details then insert
                 * the new data
                 */
                this.delete(modifyCommissionProfileRequestVO, userVO, con, commissionDAO, setVO);
                this.add(modifyCommissionProfileRequestVO, httpServletRequest, userVO, con, commissionDAO, setVO, currentDate);
            }

            // if above method execute successfully commit the
            // transaction
            mcomCon.finalCommit();
            final BTSLMessages btslMessage = new BTSLMessages("profile.addadditionalprofile.message.successeditmessage", "EditSuccess");
        
	        response1.setStatus(HttpStatus.SC_OK);
	        response.setStatus((HttpStatus.SC_OK));
	        
	      //adding logs starts
            final AdminOperationVO adminOperationVO = new AdminOperationVO();
            adminOperationVO.setSource(PretupsI.LOGGER_COMMISSION_PROFILE_SOURCE);
            adminOperationVO.setDate(currentDate);
            adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MODIFY);
            adminOperationVO
                .setInfo("Commission Profile for Category(" + modifyCommissionProfileRequestVO.getCategoryCode() + "), Name(" + modifyCommissionProfileRequestVO.getProfileName() + "), ID(" + modifyCommissionProfileRequestVO
                        .getSelectCommProfileSetID() + ") has been successfully Updated");
            adminOperationVO.setLoginID(userVO.getLoginID());
            adminOperationVO.setUserID(userVO.getUserID());
            adminOperationVO.setCategoryCode(userVO.getCategoryCode());
            adminOperationVO.setNetworkCode(userVO.getNetworkID());
            adminOperationVO.setMsisdn(userVO.getMsisdn());
            AdminOperationLog.log(adminOperationVO);
            //adding logs end
	        
            if(newVersionFlag) {
            	final String[] arr = { modifyCommissionProfileRequestVO.getProfileName(), currentVersion, String.valueOf(version) };
    			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.COMM_PRF_UPDATE_SUCCESS_WITH_NEW_VERSION, arr);
    			response.setMessage(resmsg);
    			response.setMessageCode(PretupsErrorCodesI.COMM_PRF_UPDATE_SUCCESS_WITH_NEW_VERSION);
            }else {
            	final String[] arr = { modifyCommissionProfileRequestVO.getProfileName(), currentVersion };
    			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.COMM_PRF_UPDATE_SUCCESS, arr);
    			response.setMessage(resmsg);
    			response.setMessageCode(PretupsErrorCodesI.COMM_PRF_UPDATE_SUCCESS);
            }
	        	
		}
		finally {
			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
		}
		
		
		return response;
	}
	
	
	
	private void addVersion(ModifyCommissionProfileRequestVO modifyCommissionProfileRequestVO, UserVO userVO, Connection con,
			CommissionProfileDAO commissionProfileDAO, CommissionProfileSetVO commissionProfileSetVO, Date currentDate)  throws Exception {
		final String METHOD_NAME = "addVersion";
        
        // populating CommissionProfileSetVersionVO from the from Parameters
        final CommissionProfileSetVersionVO commissionProfileSetVersionVO = new CommissionProfileSetVersionVO();
        commissionProfileSetVersionVO.setCommProfileSetId(commissionProfileSetVO.getCommProfileSetId());
        commissionProfileSetVersionVO.setCommProfileSetVersion(modifyCommissionProfileRequestVO.getVersion());
        final String format = Constants.getProperty("COMMISSION_DATE_FORMAT");
        String fromHour = null;
        if (BTSLUtil.isNullString(modifyCommissionProfileRequestVO.getApplicableFromHour())) {
            fromHour = "00:00";
        } else {
            fromHour = modifyCommissionProfileRequestVO.getApplicableFromHour();
        }
        final Date newDate = BTSLUtil.getDateFromDateString(modifyCommissionProfileRequestVO.getApplicableFromDate() + " " + fromHour, format);
        commissionProfileSetVersionVO.setApplicableFrom(newDate);
        commissionProfileSetVersionVO.setCreatedBy(userVO.getUserID());
        commissionProfileSetVersionVO.setCreatedOn(currentDate);
        commissionProfileSetVersionVO.setModifiedBy(userVO.getUserID());
        commissionProfileSetVersionVO.setModifiedOn(currentDate);
        commissionProfileSetVersionVO.setDualCommissionType(commissionProfileSetVO.getDualCommissionType());
		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL))).booleanValue())
			commissionProfileSetVersionVO.setOtherCommissionProfileSetID(modifyCommissionProfileRequestVO.getOtherCommissionProfile());
        // insert Card_Group_Set_Version
        final int insertVersionCount = commissionProfileDAO.addCommissionProfileSetVersion(con, commissionProfileSetVersionVO);

        if (insertVersionCount <= 0) {
            try {
                con.rollback();
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            log.error("add", "Error: while Inserting Commission_Profile_Set_Version");
            throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GENERAL_ERROR_CODE);
        }
	}
	
	
	
	
	private void add(ModifyCommissionProfileRequestVO modifyCommissionProfileRequestVO, HttpServletRequest request,
			UserVO userVO, Connection con, CommissionProfileDAO commissionProfileDAO,
			CommissionProfileSetVO commissionProfileSetVO, Date currentDate) throws Exception {
		final String METHOD_NAME = "add";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
		
        /*
         * Insert Data into Commission_Profile_Products and
         * Commission_Profile_Details table
         */
        if (modifyCommissionProfileRequestVO.getCommissionProfileList() != null && !modifyCommissionProfileRequestVO.getCommissionProfileList().isEmpty()) {
            CommissionProfileCombinedVONew commissionProfileCombinedVONew = null;
            CommissionProfileProductsVO commissionProfileProductsVO = null;
            for (int i = 0, j = modifyCommissionProfileRequestVO.getCommissionProfileList().size(); i < j; i++) {
                commissionProfileCombinedVONew = (CommissionProfileCombinedVONew) modifyCommissionProfileRequestVO.getCommissionProfileList().get(i);

                // set the default valus in the CommissionProfileProductsVO VO
                commissionProfileProductsVO = commissionProfileCombinedVONew.getCommissionProfileProductVO();
                commissionProfileProductsVO.setCommProfileProductID(String.valueOf(IDGenerator.getNextID(PretupsI.COMMISSION_PROFILE_PRODUCT_ID, TypesI.ALL)));
                commissionProfileProductsVO.setCommProfileSetID(commissionProfileSetVO.getCommProfileSetId());
                commissionProfileProductsVO.setVersion(modifyCommissionProfileRequestVO.getVersion());
				commissionProfileProductsVO.setTransferMultipleOffInDouble(commissionProfileProductsVO.getTransferMultipleOff());
                // insert Commission_Profile_Product
                final int insertProductCount = commissionProfileDAO.addCommissionProfileProduct(con, commissionProfileProductsVO);

                if (insertProductCount <= 0) {
                    try {
                        con.rollback();
                    } catch (Exception e) {
                        log.errorTrace(METHOD_NAME, e);
                    }
                    log.error(METHOD_NAME, "Error: while Inserting Commission_Profile_Product");
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GENERAL_ERROR_CODE);
                }

                // Insert data into Commission_Profile_Details Table
                if (commissionProfileCombinedVONew != null && !commissionProfileCombinedVONew.getSlabsList().isEmpty()) {
                    CommissionProfileDeatilsVO commissionProfileDeatilsVO = null;
                    // set the default values
                    for (int m = 0, n = commissionProfileCombinedVONew.getSlabsList().size(); m < n; m++) {
                        commissionProfileDeatilsVO = (CommissionProfileDeatilsVO) commissionProfileCombinedVONew.getSlabsList().get(m);
                        commissionProfileDeatilsVO.setCommProfileProductsID(commissionProfileProductsVO.getCommProfileProductID());
                        commissionProfileDeatilsVO.setCommProfileDetailID(String.valueOf(IDGenerator.getNextID(PretupsI.COMMISSION_PROFILE_DETAIL_ID, TypesI.ALL)));
                    }
                    // insert Commission_Pofile_Detail List
//                    setDateInVoForm(theForm);
                  
                    final int insertDetailCount = commissionProfileDAO.addCommissionProfileDetailsList(con, commissionProfileCombinedVONew.getSlabsList(),userVO.getNetworkID());

                    if (insertDetailCount <= 0) {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            log.errorTrace(METHOD_NAME, e);
                        }
                        //setDateInVoForm(theForm, request);
                        log.error(METHOD_NAME, "Error: while Inserting Commission_Profile_Details");
                        throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GENERAL_ERROR_CODE);
                    }
                }
            }    

        }
		
        
        /*
         * 
         * Insert Data into commission_profile_Otf and profile_otf_details table
         * 
         */
        this.addOtf(modifyCommissionProfileRequestVO, con, commissionProfileDAO, commissionProfileSetVO);
        
        
        /*
         * Insert Data into Additional_Commission_Profile_Details and
         * Comm_Profile_Service_Type table
         */
        if (modifyCommissionProfileRequestVO.getAdditionalProfileList() != null && !modifyCommissionProfileRequestVO.getAdditionalProfileList().isEmpty()) {
            AdditionalProfileCombinedVONew additionalProfileCombinedVONew = null;
            AdditionalProfileServicesVO additionalProfileServicesVO = null;
            for (int i = 0, j = modifyCommissionProfileRequestVO.getAdditionalProfileList().size(); i < j; i++) {
                additionalProfileCombinedVONew = (AdditionalProfileCombinedVONew) modifyCommissionProfileRequestVO.getAdditionalProfileList().get(i);

                // set the default valus in the AdditionalProfileServicesVO VO
                additionalProfileServicesVO = additionalProfileCombinedVONew.getAdditionalProfileServicesVO();
                additionalProfileServicesVO.setCommProfileServiceTypeID(String.valueOf(IDGenerator.getNextID(PretupsI.ADDITIONAL_COMMISSION_SERVICE_ID, TypesI.ALL)));
                additionalProfileServicesVO.setCommProfileSetID(commissionProfileSetVO.getCommProfileSetId());
                additionalProfileServicesVO.setCommProfileSetVersion(modifyCommissionProfileRequestVO.getVersion());

                // insert Comm_Profile_Service_Type table
                final int insertServiceCount = commissionProfileDAO.addAdditionalProfileService(con, additionalProfileServicesVO);

                if (insertServiceCount <= 0) {
                    try {
                        con.rollback();
                    } catch (Exception e) {
                        log.errorTrace(METHOD_NAME, e);
                    }
                    log.error(METHOD_NAME, "Error: while Inserting Commission_Profile_Service_Type");
                    throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GENERAL_ERROR_CODE);
                }

                // Insert data into Commission_Profile_Details Table
                if (additionalProfileCombinedVONew != null && !additionalProfileCombinedVONew.getSlabsList().isEmpty()) {
                    AdditionalProfileDeatilsVO additionalProfileDeatilsVO = null;
                    int insertDetailCount = 0;
                    // set the default values
                    for (int m = 0, n = additionalProfileCombinedVONew.getSlabsList().size(); m < n; m++) {
                        additionalProfileDeatilsVO = (AdditionalProfileDeatilsVO) additionalProfileCombinedVONew.getSlabsList().get(m);
                        additionalProfileDeatilsVO.setAddtnlComStatus(additionalProfileCombinedVONew.getAdditionalProfileServicesVO().getAddtnlComStatus());
                        additionalProfileDeatilsVO.setCommProfileServiceTypeID(additionalProfileServicesVO.getCommProfileServiceTypeID());
                        additionalProfileDeatilsVO.setAddCommProfileDetailID(String.valueOf(IDGenerator.getNextID(PretupsI.ADDITIONAL_COMMISSION_PROFILE_ID, TypesI.ALL)));
                    }
                    // insert Commission_Pofile_Detail List
                        insertDetailCount = commissionProfileDAO.addAdditionalProfileDetailsList(con, additionalProfileCombinedVONew.getSlabsList(), additionalProfileDeatilsVO.getAddtnlComStatus(),userVO.getNetworkID());
                        if (insertDetailCount <= 0) {
                            try {
                                con.rollback();
                            } catch (Exception e) {
                                log.errorTrace(METHOD_NAME, e);
                            }
                            log.error(METHOD_NAME, "Error: while Inserting Additional_Commission_Profile_Details");
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GENERAL_ERROR_CODE);
                        }

                }
            }
        }
        
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Exiting");
        }
		
	}
	
	
	
	/**
     * @param form
     * @param con
     * @param commissionProfileDAO
     * @param commissionProfileSetVO
     * @throws Exception
     */
    private void addOtf(ModifyCommissionProfileRequestVO modifyCommissionProfileRequestVO, Connection con, CommissionProfileDAO commissionProfileDAO, CommissionProfileSetVO commissionProfileSetVO) throws Exception {
        final String METHOD_NAME = "addOtf";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }

        //final CommissionProfileForm theForm = (CommissionProfileForm) form;
        
        /*
         * Insert Data into Comm_Profile_Otf 
         */
        if (modifyCommissionProfileRequestVO.getOtfProfileList() != null && !modifyCommissionProfileRequestVO.getOtfProfileList().isEmpty()) {
            OtfProfileCombinedVONew otfProfileCombinedVONew = null;
            for (int i = 0, j = modifyCommissionProfileRequestVO.getOtfProfileList().size(); i < j; i++) {
            	otfProfileCombinedVONew = (OtfProfileCombinedVONew) modifyCommissionProfileRequestVO.getOtfProfileList().get(i);
            	
            	//set the values in VO
            	OtfProfileVO otfProfileVO = otfProfileCombinedVONew.getOtfProfileVO();
            	OTFDetailsVO otfDetailsVO = null;
                ArrayList<OTFDetailsVO> slabsList = otfProfileCombinedVONew.getSlabsList();
                otfProfileVO.setCommProfileOtfID(String.valueOf(IDGenerator.getNextID(PretupsI.ADDITIONAL_COMMISSION_PROFILE_ID, TypesI.ALL)));
                otfProfileVO.setCommProfileSetID(commissionProfileSetVO.getCommProfileSetId());
                otfProfileVO.setCommProfileSetVersion(modifyCommissionProfileRequestVO.getVersion());
                otfProfileVO.setOtfDetails(slabsList);
                 
                 
                //Insert into COMMISSION_PROFILE_OTF
                final int insertCount = commissionProfileDAO.addCommissionProfileOtf(con, otfProfileVO);
                 
                if (insertCount > 0){
                	final int insertCountOtf = commissionProfileDAO.addProfileOtfDetails(con, otfProfileVO);
                }
                else{
                     try {
                         con.rollback();
                     } catch (Exception e) {
                         log.errorTrace(METHOD_NAME, e);
                     }
                     log.error(METHOD_NAME, "Error: while Inserting COMMISSION_PROFILE_OTF");
                     throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GENERAL_ERROR_CODE);
                 }
            }
        }
    }
	
	
	
	
    /*
     * This method update data into Commission_Profile_Set_Versions
     * 
     * @param form ActionForm
     * 
     * @param userVO UserVO
     * 
     * @param con Connection
     * 
     * @param commissionProfileDAO CommissionProfileDAO
     * 
     * @param commissionProfileSetVO CommissionProfileSetVO
     * 
     * @return void
     * 
     * @throws Exception
     */
    private void updateSetVersion(ModifyCommissionProfileRequestVO modifyCommissionProfileRequestVO, UserVO userVO, Connection con, CommissionProfileDAO commissionProfileDAO, Date currentDate, ArrayList selectCommProfileVersionList) throws Exception {
        final String METHOD_NAME = "updateSetVersion";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }
        //final CommissionProfileForm theForm = (CommissionProfileForm) form;

        /*
         * Commission Profile Set Version drop down key = combination of setId
         * and version
         * so we spilt the id and version
         */
        final String[] arr = modifyCommissionProfileRequestVO.getSelectCommProifleVersionID().split(":");

        // set the Commission Profile Set Version Info
        CommissionProfileSetVersionVO setVersionVO = null;
        for (int i = 0, j = selectCommProfileVersionList.size(); i < j; i++) {
            setVersionVO = (CommissionProfileSetVersionVO) selectCommProfileVersionList.get(i);
            // get the selected version info from the versionList
            if (arr[0].equals(setVersionVO.getCommProfileSetId()) && arr[1].equals(setVersionVO.getCommProfileSetVersion())) {
                break;
            }
        }

        setVersionVO.setModifiedBy(userVO.getUserID());
        setVersionVO.setModifiedOn(currentDate);
		if(((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL))).booleanValue())
			setVersionVO.setOtherCommissionProfileSetID(modifyCommissionProfileRequestVO.getOtherCommissionProfile());

        final int updateCount = commissionProfileDAO.updateCommissionProfileSetVersion(con, setVersionVO);
        if (updateCount <= 0) {
            try {
                con.rollback();
            } catch (Exception e) {
                log.errorTrace(METHOD_NAME, e);
            }
            log.error(METHOD_NAME, "Error: while Updating Commission_Profile_Set_Versions");
            throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GENERAL_ERROR_CODE);
        }

        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Exiting");
        }
    }
    
    
    
    
    
    /*
     * This method delete data into Commission_Profile_Products and
     * Comm_profile_Service_Types,
     * Commission_Profile_Details and Additional_Commission_Profile_Details
     * 
     * @param form ActionForm
     * 
     * @param userVO UserVO
     * 
     * @param con Connection
     * 
     * @param commissionProfileDAO CommissionProfileDAO
     * 
     * @param commissionProfileSetVO CommissionProfileSetVO
     * 
     * @return void
     * 
     * @throws Exception
     */
    private void delete(ModifyCommissionProfileRequestVO modifyCommissionProfileRequestVO, UserVO userVO, Connection con, CommissionProfileDAO commissionProfileDAO, CommissionProfileSetVO commissionProfileSetVO) throws Exception {
        final String METHOD_NAME = "delete";
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Entered");
        }

        /*
         * Delete Data from Commission_Profile_Products and
         * Commission_Profile_Details table
         */
        if (modifyCommissionProfileRequestVO.getCommissionProfileList() != null && !modifyCommissionProfileRequestVO.getCommissionProfileList().isEmpty()) {
            CommissionProfileCombinedVONew commissionProfileCombinedVONew = null;
            CommissionProfileProductsVO commissionProfileProductsVO = null;
            CommissionProfileDeatilsVO commissionProfileDeatilsVO ;
            int theFormsCommissionProfileLists=modifyCommissionProfileRequestVO.getCommissionProfileList().size();
            for (int i = 0, j = theFormsCommissionProfileLists; i < j; i++) {
                commissionProfileCombinedVONew = (CommissionProfileCombinedVONew) modifyCommissionProfileRequestVO.getCommissionProfileList().get(i);
                commissionProfileProductsVO = commissionProfileCombinedVONew.getCommissionProfileProductVO();

                // delete Commission_Profile_Product
                if (!BTSLUtil.isNullString(commissionProfileProductsVO.getCommProfileProductID())) {
                    final int deleteProductCount = commissionProfileDAO.deleteCommissionProfileProducts(con, commissionProfileProductsVO.getCommProfileProductID());
                    if (deleteProductCount <= 0) {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            log.errorTrace(METHOD_NAME, e);
                        }
                        log.error(METHOD_NAME, "Error: while Deleting Commission_Profile_Product");
                        throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GENERAL_ERROR_CODE);
                    }

                    // Delete data from Commission_Profile_Deatils
                    final int deleteDetailCount = commissionProfileDAO.deleteCommissionProfileDetails(con, commissionProfileProductsVO.getCommProfileProductID());
                    if (deleteDetailCount <= 0) {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            log.errorTrace(METHOD_NAME, e);
                        }
                        log.error(METHOD_NAME, "Error: while Deleting data from Commission_Profile_Details");
                        throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GENERAL_ERROR_CODE);
                    }
                    int commissionProfilesCombinedVOSlabsLists=commissionProfileCombinedVONew.getSlabsList().size();
                    for(int k =0;k<commissionProfilesCombinedVOSlabsLists;k++){
                    	commissionProfileDeatilsVO = (CommissionProfileDeatilsVO)commissionProfileCombinedVONew.getSlabsList().get(k);
                    	final int deleteotfCount = commissionProfileDAO.deleteProfileOTFDetails(con,commissionProfileDeatilsVO.getCommProfileDetailID(),PretupsI.COMM_TYPE_BASECOMM );
                    	if (deleteotfCount < 0) {
                            try {
                                con.rollback();
                            } catch (Exception e) {
                                log.errorTrace(METHOD_NAME, e);
                            }
                            log.error(METHOD_NAME, "Error: while Deleting data from Addnl_Comm_Profile_otf_Details");
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GENERAL_ERROR_CODE);
                        }
                    }
                    
                }
            }
        }

        /*
         * Delete Data from Comm_profile_Service_Types and
         * Additional_Commission_Profile_Details table
         */
        if (modifyCommissionProfileRequestVO.getAdditionalProfileList() != null && !modifyCommissionProfileRequestVO.getAdditionalProfileList().isEmpty()) {
            AdditionalProfileCombinedVONew additionalProfileCombinedVONew = null;
            AdditionalProfileServicesVO additionalProfileServicesVO = null;
            AdditionalProfileDeatilsVO additionalProfileDeatilsVO ;
            int theFormAdditionalProfileLists=modifyCommissionProfileRequestVO.getAdditionalProfileList().size();
            for (int i = 0, j =theFormAdditionalProfileLists ; i < j; i++) {
                additionalProfileCombinedVONew = (AdditionalProfileCombinedVONew) modifyCommissionProfileRequestVO.getAdditionalProfileList().get(i);
                additionalProfileServicesVO = additionalProfileCombinedVONew.getAdditionalProfileServicesVO();
                
                if (!BTSLUtil.isNullString(additionalProfileServicesVO.getCommProfileServiceTypeID())) {
                    // Delete Comm_Profile_Service Type
                    final int deleteServiceCount = commissionProfileDAO.deleteAdditionalProfileServiceTypes(con, additionalProfileServicesVO.getCommProfileServiceTypeID());
                    if (deleteServiceCount <= 0) {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            log.errorTrace(METHOD_NAME, e);
                        }
                        log.error(METHOD_NAME, "Error: while Updating Commission_Profile_Service_Type");
                        throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GENERAL_ERROR_CODE);
                    }

                    // Delete data from Addnl_Comm_Profile_Deatils
                    final int deleteDetailCount = commissionProfileDAO.deleteAdditionalProfileDetails(con, additionalProfileServicesVO.getCommProfileServiceTypeID());
                    if (deleteDetailCount <= 0) {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            log.errorTrace(METHOD_NAME, e);
                        }
                        log.error(METHOD_NAME, "Error: while Deleting data from Addnl_Comm_Profile_Details");
                        throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GENERAL_ERROR_CODE);
                    }
                    int additionalProfilesCombinedVOSlabsLists=additionalProfileCombinedVONew.getSlabsList().size();
                    for(int k =0;k<additionalProfilesCombinedVOSlabsLists;k++){
                    	additionalProfileDeatilsVO = (AdditionalProfileDeatilsVO)additionalProfileCombinedVONew.getSlabsList().get(k);
                    	final int deleteotfCount = commissionProfileDAO.deleteProfileOTFDetails(con,additionalProfileDeatilsVO.getAddCommProfileDetailID(),PretupsI.COMM_TYPE_ADNLCOMM );
                    	if (deleteotfCount < 0) {
                            try {
                                con.rollback();
                            } catch (Exception e) {
                                log.errorTrace(METHOD_NAME, e);
                            }
                            log.error(METHOD_NAME, "Error: while Deleting data from Addnl_Comm_Profile_otf_Details");
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GENERAL_ERROR_CODE);
                        }
                    }
                    
                }
            }
        }
        
        if (modifyCommissionProfileRequestVO.getOtfProfileList() != null && !modifyCommissionProfileRequestVO.getOtfProfileList().isEmpty()) {
        	OtfProfileCombinedVONew otfProfileCombinedVONew = null;
            OtfProfileVO otfProfileVO = null;
            OTFDetailsVO otfDetailsVO = null ;
            int theFormOtfProfileLists=modifyCommissionProfileRequestVO.getOtfProfileList().size();
            for (int i = 0, j =theFormOtfProfileLists ; i < j; i++) {
            	otfProfileCombinedVONew = (OtfProfileCombinedVONew) modifyCommissionProfileRequestVO.getOtfProfileList().get(i);
            	otfProfileVO = otfProfileCombinedVONew.getOtfProfileVO();
                
                if (!BTSLUtil.isNullString(otfProfileVO.getCommProfileOtfID())) {
                    // Delete COMMISSION_PROFILE_OTF
                    final int deleteServiceCount = commissionProfileDAO.deleteOtfProfileList(con, otfProfileVO.getCommProfileOtfID());
                    if (deleteServiceCount <= 0) {
                        try {
                            con.rollback();
                        } catch (Exception e) {
                            log.errorTrace(METHOD_NAME, e);
                        }
                        log.error(METHOD_NAME, "Error: while Updating COMMISSION_PROFILE_OTF");
                        throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GENERAL_ERROR_CODE);
                    }

                    // Delete data from COMMISSION_PROFILE_OTF
                    int slabsListSize=otfProfileCombinedVONew.getSlabsList().size();
                    for(int k =0;k<slabsListSize;k++){
                    	otfDetailsVO = (OTFDetailsVO)otfProfileCombinedVONew.getSlabsList().get(k);
                    	final int deleteotfCount = commissionProfileDAO.deleteProfileOtfDetails(con, otfProfileVO.getCommProfileOtfID());
                    	if (deleteotfCount < 0) {
                            try {
                                con.rollback();
                            } catch (Exception e) {
                                log.errorTrace(METHOD_NAME, e);
                            }
                            log.error(METHOD_NAME, "Error: while Deleting data from COMMISSION_PROFILE_OTF");
                            throw new BTSLBaseException(this, METHOD_NAME, PretupsI.GENERAL_ERROR_CODE);
                        }
                    }
                    
                }
            }
        }
        if (log.isDebugEnabled()) {
            log.debug(METHOD_NAME, "Exiting");
        }
    }
    
	
	//anand code of suspend resume, load version list
    @Override
	public BaseResponse deleteCommissionProfileSet(MultiValueMap<String, String> headers, HttpServletResponse response1,
			Connection con,MComConnectionI mcomCon, Locale locale, UserVO userVO, BaseResponse response, String commProfileSetID, String commProfileName) {
		final String METHOD_NAME = "deleteCommissionProfileSet";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		final String arr[] = new String[1];
		
		try {
			 final CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
             final CommissionProfileWebDAO commissionProfileWebDAO = new CommissionProfileWebDAO();
             
             // check whether the Commission Profile is Associated with any
             // user or not
             final String status = "'" + PretupsI.STATUS_DELETE + "','" + PretupsI.STATUS_CANCELED + "'";
             if (commissionProfileWebDAO.isCommissionProfileSetAssociated(con, commProfileSetID , status)) {
                 //throw new BTSLBaseException(this, "loadDetail", "profile.addadditionalprofile.error.commissionassociatedwithuser", "SelectCommProfileName");
            	 throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.COMMISSION_ASSOCIATED_WITH_USER, 0, null);
             }
             
             //final UserVO userVO = getUserFormSession(request);
             final Date currentDate = new Date();
             final CommissionProfileSetVO commissionProfileSetVO = new CommissionProfileSetVO();
             commissionProfileSetVO.setCommProfileSetId(commProfileSetID);
             commissionProfileSetVO.setModifiedOn(currentDate);
             commissionProfileSetVO.setModifiedBy(userVO.getUserID());
             commissionProfileSetVO.setStatus(PretupsI.STATUS_DELETE);
			
             arr[0] = String.valueOf(commProfileName);
             // Delete Commission Profile Set
             final int deleteCount = commissionProfileWebDAO.deleteCommissionProfileSet(con, commissionProfileSetVO);
             if (deleteCount <= 0) {
                 try {
                     mcomCon.finalRollback();
                 } catch (Exception e) {
                     log.errorTrace(METHOD_NAME, e);
                 }
                 log.error(METHOD_NAME, "Error: while Deleting Commission_Profile_Set");
                 //throw new BTSLBaseException(this, "loadDetail", "error.general.processing");
                 throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.ERROR_DELETE_COMMISSION_PROFILE_SET, 0, null);
             }
             // if above method execute successfully commit the transaction
             mcomCon.finalCommit();
             //final BTSLMessages btslMessage = new BTSLMessages("profile.addadditionalprofile.message.successdeletemessage", "EditSuccess");
             
           //adding logs starts
             final AdminOperationVO adminOperationVO = new AdminOperationVO();
             adminOperationVO.setSource(PretupsI.LOGGER_COMMISSION_PROFILE_SOURCE);
             adminOperationVO.setDate(currentDate);
             adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_DELETE);
             adminOperationVO
                 .setInfo("Commission Profile (" + commProfileName + "), ID(" + commissionProfileSetVO
                         .getCommProfileSetId() + ") has been successfully Deleted");
             adminOperationVO.setLoginID(userVO.getLoginID());
             adminOperationVO.setUserID(userVO.getUserID());
             adminOperationVO.setCategoryCode(userVO.getCategoryCode());
             adminOperationVO.setNetworkCode(userVO.getNetworkID());
             adminOperationVO.setMsisdn(userVO.getMsisdn());
             AdminOperationLog.log(adminOperationVO);
             //adding logs end
             
             String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.COMMISSION_DELETE_SUCCESS, arr);
             
             response.setMessageCode(Integer.toString(HttpStatus.SC_OK));
 			 response.setMessage(resmsg);
 			 response1.setStatus(HttpStatus.SC_OK);
 			 response.setStatus(HttpStatus.SC_OK);
             
		}
		catch (BTSLBaseException be) {
			log.error(METHOD_NAME, "Exception:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		}
		catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.ERROR_DELETE_COMMISSION_PROFILE_SET, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.ERROR_DELETE_COMMISSION_PROFILE_SET);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		return response;
		
		
	}

	
	
	
	
	@Override
	public BaseResponse suspendResumeCommissionProfileSet(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO,
			BaseResponse response,SusResCommProfileSetRequestVO susResCommProfileSetRequestVO) {
		final String METHOD_NAME = "suspendResumeCommissionProfileSet";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		
		String s="";
		String[] sArr=new String[2];
		String logOperation="";
		
		try {
			 final CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
             final CommissionProfileWebDAO commissionProfileWebDAO = new CommissionProfileWebDAO();
             
             if(susResCommProfileSetRequestVO.getStatus().equals("S")) {
            	 s=PretupsI.SUSPENDED;
            	 logOperation = TypesI.LOGGER_OPERATION_SUSPENDED;
             }else {
            	 s=PretupsI.RESUMED;
            	 logOperation = TypesI.LOGGER_OPERATION_ACTIVATED;
             }
             sArr[0]=susResCommProfileSetRequestVO.getCommProfileName();
             sArr[1]=s;
             
             if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue()) {
                 if (susResCommProfileSetRequestVO.getDefaultProfile()=="Y") {
                     //throw new BTSLBaseException(this, "saveSuspend", "profile.addadditionalprofile.message.successsuspenderrormessage");
                	 throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DEFAULT_PROFILE_SUSPEND, 0, null);
                 }
             }
             
             final Date currentDate = new Date();
             final CommissionProfileSetVO commissionProfileSetVO = new CommissionProfileSetVO();
             commissionProfileSetVO.setCommProfileSetId(susResCommProfileSetRequestVO.getCommProfileSetId());
             commissionProfileSetVO.setModifiedOn(currentDate);
             commissionProfileSetVO.setModifiedBy(userVO.getUserID());
             commissionProfileSetVO.setStatus(susResCommProfileSetRequestVO.getStatus());
             
             if((susResCommProfileSetRequestVO.getLanguage1Message()==null || susResCommProfileSetRequestVO.getLanguage1Message()=="") && susResCommProfileSetRequestVO.getStatus().equals("S")) {
            	 throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LANGUAGE_ONE_MESS_REQ, 0, null);
             }
             
             if((susResCommProfileSetRequestVO.getLanguage2Message()==null || susResCommProfileSetRequestVO.getLanguage2Message()=="") && susResCommProfileSetRequestVO.getStatus().equals("S")) {
            	 throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LANGUAGE_TWO_MESS_REQ, 0, null);
             }
             
             commissionProfileSetVO.setLanguage1Message(susResCommProfileSetRequestVO.getLanguage1Message());
             commissionProfileSetVO.setLanguage2Message(susResCommProfileSetRequestVO.getLanguage2Message());
			
             // Suspend resume Commission Profile Set
             final int updateCount = commissionProfileWebDAO.suspendResumeCommissionProfileSet(con, commissionProfileSetVO);
             if (updateCount <= 0) {
                 try {
                     mcomCon.finalRollback();
                 } catch (Exception e) {
                     log.errorTrace(METHOD_NAME, e);
                 }
                 log.error(METHOD_NAME, "Error: while Suspending Commission_Profile_Set");
                 //throw new BTSLBaseException(this, "loadDetail", "error.general.processing");
                 throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.ERROR_SUS_RES_COMMISSION_PROFILE_SET, 0, s);
             }
             // if above method execute successfully commit the transaction
             mcomCon.finalCommit();
             //final BTSLMessages btslMessage = new BTSLMessages("profile.addadditionalprofile.message.successdeletemessage", "EditSuccess");
             
           //adding logs starts
             final AdminOperationVO adminOperationVO = new AdminOperationVO();
             adminOperationVO.setSource(PretupsI.LOGGER_COMMISSION_PROFILE_SOURCE);
             adminOperationVO.setDate(currentDate);
             adminOperationVO.setOperation(logOperation);
             adminOperationVO
                 .setInfo("Commission Profile (" + susResCommProfileSetRequestVO.getCommProfileName() + "), ID(" + commissionProfileSetVO
                         .getCommProfileSetId() + ") has been successfully (" + s + ")");
             adminOperationVO.setLoginID(userVO.getLoginID());
             adminOperationVO.setUserID(userVO.getUserID());
             adminOperationVO.setCategoryCode(userVO.getCategoryCode());
             adminOperationVO.setNetworkCode(userVO.getNetworkID());
             adminOperationVO.setMsisdn(userVO.getMsisdn());
             AdminOperationLog.log(adminOperationVO);
             //adding logs end
             
             String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.COMMISSION_SUS_RES_SUCCESS, sArr);
             
             response.setMessageCode(Integer.toString(HttpStatus.SC_OK));
 			 response.setMessage(resmsg);
 			 response1.setStatus(HttpStatus.SC_OK);
 			 response.setStatus(HttpStatus.SC_OK);
             
		}
		catch (BTSLBaseException be) {
			log.error(METHOD_NAME, "Exception:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		}
		catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.ERROR_SUS_RES_COMMISSION_PROFILE_SET, sArr);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.ERROR_SUS_RES_COMMISSION_PROFILE_SET);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		return response;
	}





	
	@Override
	public LoadVersionListBasedOnDateResponseVO loadVersionListBasedOnDate(MultiValueMap<String, String> headers,
			HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale, UserVO userVO,
			LoadVersionListBasedOnDateResponseVO response,
			LoadVersionListBasedOnDateRequestVO loadVersionListBasedOnDateRequestVO) {
		
		final String METHOD_NAME = "loadVersionListBasedOnDate";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		

		
		try {
			 final CommissionProfileDAO commissionProfileDAO = new CommissionProfileDAO();
             final CommissionProfileWebDAO commissionProfileWebDAO = new CommissionProfileWebDAO();
             
             SimpleDateFormat formatter=new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
             Date currentDate=formatter.parse(loadVersionListBasedOnDateRequestVO.getDate()); 
             
             final ArrayList versionList = commissionProfileWebDAO.loadCommissionProfileSetVersionTwo(con, userVO.getNetworkID(),
            		 loadVersionListBasedOnDateRequestVO.getCategoryCode(), currentDate, loadVersionListBasedOnDateRequestVO.getCommProfileSetId());
             
             
             if(BTSLUtil.isNullObject(versionList)){
            	 throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.VERSION_LIST_FAIL, 0, null);
             }
             
             response.setVersionList(versionList);
             String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.VERSION_LIST_SUCCESS, null);
             response.setMessageCode(Integer.toString(HttpStatus.SC_OK));
 			 response.setMessage(resmsg);
 			 response1.setStatus(HttpStatus.SC_OK);
 			 response.setStatus(HttpStatus.SC_OK);
		}
		catch (BTSLBaseException be) {
			log.error(METHOD_NAME, "Exception:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}
		}
		catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.VERSION_LIST_FAIL, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.VERSION_LIST_FAIL);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}
		
		return response;
		
	}	
	
	
	
	//harshita code 
	
	public CommissionProfileMainResponseVO viewGeoGradeList(Connection con, String loginID, String categoryCode,
			HttpServletResponse response1) {

		final String METHOD_NAME = "viewDivisionList";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		CommissionProfileMainResponseVO response = new CommissionProfileMainResponseVO();
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		DomainWebDAO domainWebDAO = new DomainWebDAO();
		ArrayList geoList;
		ArrayList gradeList;

		try {

			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			geoList = domainWebDAO.loadGeographyListForCategory(con, userVO.getNetworkID(), categoryCode);
			gradeList = domainWebDAO.loadGradeListForCategory(con, categoryCode);
			if (!geoList.isEmpty()) {
				response.setGeoList(geoList);
			}
			if (!gradeList.isEmpty()) {
				response.setGradeList(gradeList);
			}

			if ((BTSLUtil.isNullOrEmptyList(gradeList)) && (BTSLUtil.isNullOrEmptyList(geoList))) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
			}

			if (!((BTSLUtil.isNullOrEmptyList(gradeList)) && (BTSLUtil.isNullOrEmptyList(geoList)))) {
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);
			}
		}

		catch (BTSLBaseException be) {
			log.error(METHOD_NAME, "Exception:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.LIST_NOT_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LIST_NOT_FOUND);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

	public CommissionProfileViewListResponseVO viewList(Connection con, String loginID, String categoryCode,
			String gradeCode, String geoCode, String status, HttpServletResponse response1) {
		final String METHOD_NAME = "viewList";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		CommissionProfileViewListResponseVO response = new CommissionProfileViewListResponseVO();
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		ArrayList viewList;
		Date currentDate = new Date();
		CommissionProfileWebDAO commissionProfileWebDAO = new CommissionProfileWebDAO();

		try {

			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			viewList = commissionProfileWebDAO.loadCommissionProfile(con, userVO.getNetworkID(), categoryCode, geoCode,
					gradeCode, status, currentDate);
			if (viewList.isEmpty()) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
			}
			if ((!BTSLUtil.isNullOrEmptyList(viewList))) {
				response.setViewList(viewList);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);
			}
		} catch (BTSLBaseException be) {
			log.error(METHOD_NAME, "Exception:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.LIST_NOT_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LIST_NOT_FOUND);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

	public CommissionProfileProductListResponseVO viewProductList(Connection con, String loginID,
			HttpServletResponse response1) {

		final String METHOD_NAME = "viewProductList";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		CommissionProfileProductListResponseVO response = new CommissionProfileProductListResponseVO();
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		ArrayList viewList;
		NetworkProductDAO networkProductDAO = new NetworkProductDAO();
		try {

			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			viewList = networkProductDAO.loadProductList(con, userVO.getNetworkID(), PretupsI.C2S_MODULE);
			if (viewList.isEmpty()) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
			}
			if ((!BTSLUtil.isNullOrEmptyList(viewList))) {
				response.setProductList(viewList);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);
			}
		} catch (BTSLBaseException be) {
			log.error(METHOD_NAME, "Exception:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.LIST_NOT_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LIST_NOT_FOUND);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

	public CommissionProfileGatewayListResponseVO viewGatewayList(Connection con, String loginID, String categoryCode,
			HttpServletResponse response1) {
		final String METHOD_NAME = "viewGatewayList";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		CommissionProfileGatewayListResponseVO response = new CommissionProfileGatewayListResponseVO();
		UserDAO userDAO = new UserDAO();
		UserVO userVO = new UserVO();
		ArrayList viewList;
		MessageGatewayWebDAO mgwebdao = new MessageGatewayWebDAO();

		try {

			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			viewList = mgwebdao.loadGatewayList(con, userVO.getNetworkID(), categoryCode);
			if (viewList.isEmpty()) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
			}
			if ((!BTSLUtil.isNullOrEmptyList(viewList))) {
				response.setGatewayList(viewList);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);
			}
		} catch (BTSLBaseException be) {
			log.error(METHOD_NAME, "Exception:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.LIST_NOT_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LIST_NOT_FOUND);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}

	public CommissionProfileSubServiceListResponseVO viewSubServiceList(Connection con, String loginID,
			String serviceCode, HttpServletResponse response1) {
		final String METHOD_NAME = "viewSubServiceList";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}

		Locale locale = new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY);
		CommissionProfileSubServiceListResponseVO response = new CommissionProfileSubServiceListResponseVO();
		ArrayList viewList;
		ServiceSelectorMappingDAO serviceSelectorMappingDAO = new ServiceSelectorMappingDAO();

		try {

			viewList = serviceSelectorMappingDAO.loadServiceSelectorMappingDetails(con, serviceCode);
			if (viewList.isEmpty()) {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);
			}
			if ((!BTSLUtil.isNullOrEmptyList(viewList))) {
				response.setSubServiceList(viewList);
				response.setStatus((HttpStatus.SC_OK));
				String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.LIST_FOUND, null);
				response.setMessage(resmsg);
				response.setMessageCode(PretupsErrorCodesI.LIST_FOUND);
			}
		} catch (BTSLBaseException be) {
			log.error(METHOD_NAME, "Exception:e=" + be);
			log.errorTrace(METHOD_NAME, be);
			if (!BTSLUtil.isNullString(be.getMessage())) {
				String msg = RestAPIStringParser.getMessage(locale, be.getMessage(), null);
				response.setMessageCode(be.getMessage());
				response.setMessage(msg);
				response.setStatus(HttpStatus.SC_BAD_REQUEST);
				response1.setStatus(HttpStatus.SC_BAD_REQUEST);
			}

		}

		catch (Exception e) {
			log.error(METHOD_NAME, "Exception:e=" + e);
			log.errorTrace(METHOD_NAME, e);
			response.setStatus((HttpStatus.SC_BAD_REQUEST));
			String resmsg = RestAPIStringParser.getMessage(
					new Locale(SystemPreferences.DEFAULT_LANGUAGE, SystemPreferences.DEFAULT_COUNTRY),
					PretupsErrorCodesI.LIST_NOT_FOUND, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.LIST_NOT_FOUND);
			response1.setStatus(HttpStatus.SC_BAD_REQUEST);
		}

		return response;
	}
	
	
	@Override
	public BaseResponse chnageStatusForCommissionProfile(String categorCode, String loginUserID,
			HttpServletResponse response1, Connection con, MComConnectionI mcomCon, Locale locale,
			BaseResponse response,
			ChangeStatusForCommissionProfileRequestVO requestVO)
			throws BTSLBaseException, Exception {
		final String METHOD_NAME = "chnageStatusForCommissionProfile";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		try {
			final CommissionProfileWebDAO commissionProfileWebDAO = new CommissionProfileWebDAO();

			UserVO userVO = new UserVO();
			UserDAO userDAO = new UserDAO();
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginUserID);
			final Date currentDate = new Date();
			ChangeStatusForCommissionProfileVO commissionProfileSetVO = null;

			boolean stopUpdate = false;
			// set the default values
			ArrayList statusChangeList = requestVO.getChangeStatusListForCommissionProfile();
			if (statusChangeList != null && !statusChangeList.isEmpty()) {
				for (int i = 0, j = statusChangeList.size(); i < j; i++) {
					commissionProfileSetVO = (ChangeStatusForCommissionProfileVO) statusChangeList.get(i);

					if ((BTSLUtil.isNullorEmpty(commissionProfileSetVO.getLanguage1Message()))
							&& PretupsI.SUSPEND.equalsIgnoreCase(commissionProfileSetVO.getStatus())) {
						throw new BTSLBaseException(classname, METHOD_NAME,
								PretupsErrorCodesI.COMMISSION_PROFILE_SET_LANGUAGE_ERROR, 0, null);
					}

					if ((BTSLUtil.isNullorEmpty(commissionProfileSetVO.getLanguage2Message()))
							&& PretupsI.SUSPEND.equalsIgnoreCase(commissionProfileSetVO.getStatus())) {
						throw new BTSLBaseException(classname, METHOD_NAME,
								PretupsErrorCodesI.COMMISSION_PROFILE_SET_LANGUAGE_ERROR, 0, null);
					}
					commissionProfileSetVO.setCommProfileSetId(commissionProfileSetVO.getCommProfileSetId());
					commissionProfileSetVO.setModifiedOn(currentDate);
					commissionProfileSetVO.setModifiedBy(userVO.getUserID());
					commissionProfileSetVO.setStatus(commissionProfileSetVO.getStatus());
					if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE)))
							.booleanValue()) {
						if (PretupsI.YES.equalsIgnoreCase(commissionProfileSetVO.getDefaultProfile())
								&& PretupsI.SUSPEND.equalsIgnoreCase(commissionProfileSetVO.getStatus())) {
							stopUpdate = true;
						}
					}

				}
				if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE)))
						.booleanValue()) {
					if (stopUpdate) {
						throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.DEFAULT_PROFILE_SUSPEND,
								0, null);
					}
				}
				// Delete Commission Profile Set
				final int updateCount = commissionProfileWebDAO.suspendCommissionProfileListFromRestAPI(con,
						statusChangeList);
				if (updateCount <= 0) {
					try {
						mcomCon.finalRollback();
					} catch (Exception e) {
						log.errorTrace(METHOD_NAME, e);
					}
					log.error(METHOD_NAME, "Error: while Suspending Commission_Profile_Set");
					throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING, 0,
							null);
				}
				// if above method execute successfully commit the transaction
				mcomCon.finalCommit();
				// Adding the admin logs for the suspending and resuming the
				// commission profiles
				AdminOperationVO adminOperationVO = null;
				for (int i = 0, j = statusChangeList.size(); i < j; i++) {
					commissionProfileSetVO = (ChangeStatusForCommissionProfileVO) statusChangeList.get(i);
					// log the data in adminOperationLog.log
					adminOperationVO = new AdminOperationVO();
					adminOperationVO.setSource(PretupsI.LOGGER_COMMISSION_PROFILE_SOURCE);
					adminOperationVO.setDate(currentDate);
					adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_CHANGE_STATUS);
					adminOperationVO.setInfo("Commission Profile for Category(" + categorCode + "), Name("
							+ commissionProfileSetVO.getCommProfileSetName() + "), ID("
							+ commissionProfileSetVO.getCommProfileSetId() + ") has successfully updated with Status ("
							+ commissionProfileSetVO.getStatus() + ")");
					adminOperationVO.setLoginID(userVO.getLoginID());
					adminOperationVO.setUserID(userVO.getUserID());
					adminOperationVO.setCategoryCode(userVO.getCategoryCode());
					adminOperationVO.setNetworkCode(userVO.getNetworkID());
					adminOperationVO.setMsisdn(userVO.getMsisdn());
					AdminOperationLog.log(adminOperationVO);
				}

			} else {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.LIST_NOT_FOUND, 0, null);

			}
			// ends here
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale,
					PretupsErrorCodesI.PROFILE_ADDITIONAL_SUCCESS_SUSPENORACTIVATE_MESSAGE, null);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.PROFILE_ADDITIONAL_SUCCESS_SUSPENORACTIVATE_MESSAGE);

		} finally {
			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
		}

		return response;
	}

	@Override
	public BaseResponse makeDefaultCommissionProfile(String loginUserID, HttpServletResponse response1, Connection con,
			MComConnectionI mcomCon, Locale locale, String commissionProfileSetId, String categoryCode,
			String networkCode, String commissionProfileName) throws BTSLBaseException, Exception {
		final String METHOD_NAME = "makeDefaultCommissionProfile";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		BaseResponse response = new BaseResponse();
		final String arr[] = new String[1];
		try {
			final CommissionProfileWebDAO commissionProfileWebDAO = new CommissionProfileWebDAO();
			final Date currentDate = new Date();

			UserVO userVO = new UserVO();
			UserDAO userDAO = new UserDAO();
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginUserID);

			int defaultProfCount = 0;
			CommissionProfileSetVO commissionProfileSetVO = commissionProfileWebDAO
					.getCommissionProfileSetOldIdForDefault(con, categoryCode, networkCode);
			if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue()
					&& commissionProfileSetId != null) {
				if (BTSLUtil.isNullObject(commissionProfileSetVO)) {
					commissionProfileSetVO = new CommissionProfileSetVO();
					commissionProfileSetVO.setCommProfileSetId(commissionProfileSetId);

				}
				defaultProfCount = commissionProfileWebDAO.updateDefaultCommission(con, commissionProfileSetId,
						categoryCode, commissionProfileSetVO.getCommProfileSetId(), networkCode);

			} else {
				defaultProfCount = 1;
			}

			if (defaultProfCount <= 0) {
				try {
					mcomCon.finalRollback();
				} catch (Exception e) {
					log.errorTrace(METHOD_NAME, e);
				}
				log.error(METHOD_NAME, "Error: while making default Commission_Profile_Set");
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.GENERAL_ERROR_PROCESSING);
			}
			// if above method execute successfully commit the transaction
			mcomCon.finalCommit();
						
			arr[0] = String.valueOf(commissionProfileName);
			//adding logs starts
            final AdminOperationVO adminOperationVO = new AdminOperationVO();
            adminOperationVO.setSource(PretupsI.LOGGER_COMMISSION_PROFILE_SOURCE);
            adminOperationVO.setDate(currentDate);
            adminOperationVO.setOperation(TypesI.LOGGER_OPERATION_MAKE_DEFAULT);
            adminOperationVO
                .setInfo("Commission Profile With Name(" + commissionProfileName + "), ID(" + commissionProfileSetVO
                        .getCommProfileSetId() + ") has been successfully Made Default");
            adminOperationVO.setLoginID(userVO.getLoginID());
            adminOperationVO.setUserID(userVO.getUserID());
            adminOperationVO.setCategoryCode(userVO.getCategoryCode());
            adminOperationVO.setNetworkCode(userVO.getNetworkID());
            adminOperationVO.setMsisdn(userVO.getMsisdn());
            AdminOperationLog.log(adminOperationVO);
            //adding logs end

			// ends here
			response.setStatus((HttpStatus.SC_OK));
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.MAKE_DEFAULI_SUCCESS_MESSAGE,
					arr);
			response.setMessage(resmsg);
			response.setMessageCode(PretupsErrorCodesI.MAKE_DEFAULI_SUCCESS_MESSAGE);

		} finally
		{
			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
		}

		return response;
	}

	
	public CommissionProfileViewResponseVO viewCommissionProfileDetails(
			Connection con, Locale locale, String loginID, String domainCode, String commissionType,
			String categoryCode, String commProfileSetId, String gradeCode, String grphDomainCode, String networkCode,
			String commProfileSetVersionId, HttpServletResponse response1) throws BTSLBaseException, Exception {
		final String METHOD_NAME = "viewCommissionProfileDetails";
		if (log.isDebugEnabled()) {
			log.debug(METHOD_NAME, "Entered:=" + METHOD_NAME);
		}
		CommissionProfileViewResponseVO response = new CommissionProfileViewResponseVO();
		CommissionProfileWebDAO commissionProfileWebDAO = null;
		CommissionProfileDAO commissionProfileDAO = null;
		try {
			final ListSorterUtil sort = new ListSorterUtil();

			commissionProfileDAO = new CommissionProfileDAO();
			commissionProfileWebDAO = new CommissionProfileWebDAO();
			UserVO userVO = new UserVO();
			UserDAO userDAO = new UserDAO();
			userVO = userDAO.loadAllUserDetailsByLoginID(con, loginID);
			response.setSequenceNo(commissionProfileDAO.loadsequenceNo(con, categoryCode));
			// set the Commission Profile Set Name, short Code and status
			CommissionProfileSetVO setVO = null;
			// load the Commisssion Profile Set Names
			final ArrayList<CommissionProfileSetVO> commissionProfileSetList = commissionProfileWebDAO
					.loadCommissionProfileSet(con, userVO.getNetworkID(), categoryCode, gradeCode, grphDomainCode);
			if (commissionProfileSetList != null && !commissionProfileSetList.isEmpty()) {
				int theFormSelectCommProfileSetLists = commissionProfileSetList.size();
				for (int i = 0, j = theFormSelectCommProfileSetLists; i < j; i++) {
					setVO = (CommissionProfileSetVO) commissionProfileSetList.get(i);
					if (commProfileSetId.equals(setVO.getCommProfileSetId())) {
						response.setProfileName(setVO.getCommProfileSetName());
						response.setShortCode(setVO.getShortCode());
						response.setDefaultProfile(setVO.getDefaultProfile());
						break;
					}
				}
			} else {
				throw new BTSLBaseException(classname, METHOD_NAME, PretupsErrorCodesI.COMMISSION_PROFILE_NOT_FOUND, 0,
						null);

			}
			final String commProfileSetVersionID = commProfileSetVersionId;
			CommissionProfileSetVersionVO setVersionVO = null;
			// load the Commission Profile Set Version
			final Date currentDate = new Date();
			ArrayList selectedCommProfileVersionList = commissionProfileWebDAO.loadCommissionProfileSetVersionForViewDetail(con,
					userVO.getNetworkID(), categoryCode,commProfileSetId,commProfileSetVersionId);
			final ArrayList versionList = new ArrayList();
			if (selectedCommProfileVersionList != null && !selectedCommProfileVersionList.isEmpty()) {
				CommissionProfileSetVersionVO commissionProfileSetVersionVO = null;

				for (int i = 0, j = selectedCommProfileVersionList.size(); i < j; i++) {
					commissionProfileSetVersionVO = (CommissionProfileSetVersionVO) selectedCommProfileVersionList
							.get(i);
					if (commissionProfileSetVersionVO.getCommProfileSetId().equals(commProfileSetId)) {
						versionList.add(commissionProfileSetVersionVO);
					}
				}
			}

			if (versionList != null && !versionList.isEmpty()) {
				int theFormselectCommProfilesVersionLists = versionList.size();
				for (int i = 0, j = theFormselectCommProfilesVersionLists; i < j; i++) {
					setVersionVO = (CommissionProfileSetVersionVO) versionList.get(i);
					// get the selected version info from the versionList
					if (commProfileSetId.equals(setVersionVO.getCommProfileSetId())
							&& commProfileSetVersionID.equals(setVersionVO.getCommProfileSetVersion())) {

						response.setApplicableFromDate(BTSLDateUtil
								.getSystemLocaleDate(BTSLUtil.getDateStringFromDate(setVersionVO.getApplicableFrom())));

						final int hour = BTSLUtil.getHour(setVersionVO.getApplicableFrom());
						final int minute = BTSLUtil.getMinute(setVersionVO.getApplicableFrom());
						final String time = BTSLUtil.getTimeinHHMM(hour, minute);
						response.setApplicableFromHour(time);
						response.setOldApplicableFromDate(
								BTSLUtil.getDateStringFromDate(setVersionVO.getApplicableFrom()));
						response.setOldApplicableFromHour(time);
						response.setVersion(setVersionVO.getCommProfileSetVersion());
						final ArrayList<ListValueVO> dualCommissionTypeList = LookupsCache
								.loadLookupDropDown(PretupsI.DUAL_COMM_TYPE, true);

						for (int a = 0; a < dualCommissionTypeList.size(); a++) {
							if (setVersionVO.getDualCommissionType().equals(dualCommissionTypeList.get(a).getValue())) {
								response.setDualCommTypeDesc(dualCommissionTypeList.get(a).getLabel());
							}
						}
						response.setDualCommType(setVersionVO.getDualCommissionType());
						if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.OTH_COM_CHNL)))
								.booleanValue()) {
							ArrayList otherCommissionTypeList = LookupsCache
									.loadLookupDropDown(PretupsI.OTHER_COMMISSION_TYPE, true);
							int othersCommissionTypeLists = otherCommissionTypeList.size();
							for (int count = 0; count < othersCommissionTypeLists; count++) {
								if (((ListValueVO) otherCommissionTypeList.get(count)).getValue()
										.equalsIgnoreCase(setVersionVO.getCommissionType())) {
									response.setCommissionTypeAsString(
											((ListValueVO) otherCommissionTypeList.get(count)).getLabel());
									break;
								}
							}
							response.setOtherCommissionProfileAsString(setVersionVO.getOtherCommissionName());
							response.setOtherCommissionProfile(
									setVersionVO.getCommissionType() + ":" + setVersionVO.getCommissionTypeValue() + ":"
											+ setVersionVO.getOtherCommissionProfileSetID());
							response.setCommissionType(setVersionVO.getCommissionType());
							if (PretupsI.OTHER_COMMISSION_TYPE_CATEGORY.equalsIgnoreCase(commissionType)) {
								response.setOtherCategoryCode(setVersionVO.getCommissionTypeValue());
								String formCategoryCode = categoryCode.split(":")[0];
								ArrayList otherCategoryListLocal = new CategoryWebDAO().loadCategoryList(con,
										domainCode);
								int otherCategoryListLocals = otherCategoryListLocal.size();
								for (int count = 0; count < otherCategoryListLocals; count++) {
									if (formCategoryCode
											.equalsIgnoreCase(((ListValueVO) otherCategoryListLocal.get(count))
													.getValue().split(":")[1])) {
										response.setCommissionTypeValueAsString(
												((ListValueVO) otherCategoryListLocal.get(count)).getLabel());
									}
								}
							} else if (PretupsI.OTHER_COMMISSION_TYPE_GATEWAY.equalsIgnoreCase(commissionType)) {
								response.setGatewayCode(setVersionVO.getCommissionTypeValue());
								response.setCommissionTypeValueAsString(setVersionVO.getCommissionTypeValue());
							} else if (PretupsI.OTHER_COMMISSION_TYPE_GRADE.equalsIgnoreCase(commissionType)) {
								response.setGradeCode(setVersionVO.getCommissionTypeValue());
								ArrayList gradeList = new CategoryGradeDAO().loadGradeList(con);
								for (int count = 0; count < gradeList.size(); count++) {
									if ((((GradeVO) gradeList.get(count)).getGradeCode())
											.equalsIgnoreCase(setVersionVO.getCommissionTypeValue())) {
										response.setCommissionTypeValueAsString(
												((GradeVO) gradeList.get(count)).getGradeName());
										break;
									}
								}
							}
							response.setCommissionTypeValue(setVersionVO.getCommissionTypeValue());
						}
					}
				}
			}
			// end of loop

			// load the commission profile products info
			final ArrayList productList = commissionProfileDAO.loadCommissionProfileProductsList(con, commProfileSetId,
					commProfileSetVersionId);

			if (productList != null && !productList.isEmpty()) {
				CommissionProfileProductsVO commissionProfileProductsVO = null;
				CommissionProfileCombinedVO commissionProfileCombinedVO = null;
				final ArrayList commissionList = new ArrayList();
				CommissionProfileDeatilsVO cpdvo = null;
				ArrayList commProfileDetailList = null;
				int productsLists = productList.size();
				for (int i = 0, j = productsLists; i < j; i++) {
					commissionProfileProductsVO = (CommissionProfileProductsVO) productList.get(i);
					commProfileDetailList = commissionProfileWebDAO.loadCommissionProfileDetailList(con,
							commissionProfileProductsVO.getCommProfileProductID(), networkCode);
					commissionProfileCombinedVO = new CommissionProfileCombinedVO();
					commissionProfileCombinedVO.setCommissionProfileProductVO(commissionProfileProductsVO);

					commProfileDetailList = (ArrayList) sort.doSort("startRange", null, commProfileDetailList);
					commissionProfileCombinedVO.setSlabsList(commProfileDetailList);
					commissionList.add(commissionProfileCombinedVO);
				}
				response.setCommissionProfileList(commissionList);
			}

			// load OTF(CBC) profile list.
			final ArrayList<OtfProfileCombinedVO> otfProfileList = new ArrayList<OtfProfileCombinedVO>();
			ArrayList otfProfileVOList = commissionProfileDAO.loadOtfProfileVOList(con, commProfileSetId,
					commProfileSetVersionId);
			if (otfProfileVOList != null && !otfProfileVOList.isEmpty()) {
				ArrayList<OTFDetailsVO> slabList = null;
				for (int i = 0; i < otfProfileVOList.size(); i++) {
					OtfProfileCombinedVO otfProfileCombinedVO = new OtfProfileCombinedVO();
					OtfProfileVO otfProfileVO = (OtfProfileVO) otfProfileVOList.get(i);
					slabList = new ArrayList<OTFDetailsVO>();
					slabList = commissionProfileDAO.loadProfileOtfDetails(con, otfProfileVO.getCommProfileOtfID());
					otfProfileVO.setOtfApplicableFrom(
							BTSLDateUtil.getSystemLocaleDate(otfProfileVO.getOtfApplicableFrom()));
					otfProfileVO
							.setOtfApplicableTo(BTSLDateUtil.getSystemLocaleDate(otfProfileVO.getOtfApplicableTo()));
					otfProfileCombinedVO.setOtfProfileVO(otfProfileVO);
					otfProfileCombinedVO.setSlabsList(slabList);
					otfProfileList.add(otfProfileCombinedVO);
				}
				response.setOtfProfileList(otfProfileList);
			}

			// load the additional profile services info
			final ArrayList serviceList = commissionProfileWebDAO.loadAdditionalProfileServicesList(con,
					commProfileSetId, commProfileSetVersionId);
			if (serviceList != null && !serviceList.isEmpty()) {
				AdditionalProfileServicesVO additionalProfileServicesVO = null;
				AdditionalProfileCombinedVO additionalProfileCombinedVO = null;
				AdditionalProfileDeatilsVO additionalProfileDeatilsVO = null;
				final ArrayList additionalList = new ArrayList();
				ArrayList addProfileDetailList = null;
				List otfDetailList = null;
				AdditionalProfileDeatilsVO aprdvo = null;
				int servicesList = serviceList.size();
				for (int i = 0, j = servicesList; i < j; i++) {
					additionalProfileServicesVO = (AdditionalProfileServicesVO) serviceList.get(i);

					// load Additional Commission Profile Details
					addProfileDetailList = commissionProfileWebDAO.loadAdditionalProfileDetailList(con,
							additionalProfileServicesVO.getCommProfileServiceTypeID(), networkCode);
					if (!addProfileDetailList.isEmpty()) {
						/*
						 * commissionProfile list consist of commissionProfileCombinedVO's so here we
						 * set the values in the VO and add that vo into the list
						 */
						final ArrayList finalSelectorList = new ArrayList();
						final ServiceSelectorMappingCache srvcSelectorMappingCache = new ServiceSelectorMappingCache();
						final ArrayList selectorList = srvcSelectorMappingCache
								.getSelectorListForServiceType(additionalProfileServicesVO.getServiceType());
						ServiceSelectorMappingVO ssmVO = new ServiceSelectorMappingVO();
						int selectors = selectorList.size();
						for (int k = 0; k < selectors; k++) {
							ssmVO = (ServiceSelectorMappingVO) selectorList.get(k);
							final ListValueVO listVO = new ListValueVO(ssmVO.getSelectorName(),
									ssmVO.getSelectorCode());
							finalSelectorList.add(listVO);
						}
						ListValueVO selectorVO = null;
						if (!BTSLUtil.isNullString(additionalProfileServicesVO.getSubServiceCode())) {
							selectorVO = BTSLUtil.getOptionDesc(additionalProfileServicesVO.getSubServiceCode(),
									finalSelectorList);
							additionalProfileServicesVO.setSubServiceDesc(selectorVO.getLabel());
						}
						additionalProfileCombinedVO = new AdditionalProfileCombinedVO();
						additionalProfileCombinedVO.setAdditionalProfileServicesVO(additionalProfileServicesVO);
						// sort the list by startrange
						if ((Boolean) PreferenceCache.getNetworkPrefrencesValue(PreferenceI.TARGET_BASED_COMMISSION,
								networkCode)) {
							int addProfilesDetailLists = addProfileDetailList.size();
							for (int k = 0; k < addProfilesDetailLists; k++) {
								aprdvo = (AdditionalProfileDeatilsVO) addProfileDetailList.get(k);
								addProfileDetailList.remove(k);
								otfDetailList = commissionProfileWebDAO.loadProfileOtfDetailList(con,
										aprdvo.getAddCommProfileDetailID(), aprdvo.getOtfType(),
										PretupsI.COMM_TYPE_ADNLCOMM);
								aprdvo.setOtfDetails(otfDetailList);
								aprdvo.setOtfDetailsSize(otfDetailList.size());
								addProfileDetailList.add(k, aprdvo);

							}
						}
						try {
							addProfileDetailList = (ArrayList) sort.doSort("startRange", null, addProfileDetailList);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						response.setSlabList(addProfileDetailList);
						additionalProfileCombinedVO.setSlabsList(addProfileDetailList);
						additionalProfileDeatilsVO = new AdditionalProfileDeatilsVO();
						additionalProfileDeatilsVO = (AdditionalProfileDeatilsVO) addProfileDetailList.get(0);
						additionalProfileServicesVO.setAddtnlComStatus(additionalProfileDeatilsVO.getAddtnlComStatus());
						additionalProfileServicesVO
								.setAddtnlComStatusName(additionalProfileDeatilsVO.getAddtnlComStatusName());
						response.setAddtnlComStatus(additionalProfileDeatilsVO.getAddtnlComStatus());
						response.setAddtnlComStatusName(additionalProfileDeatilsVO.getAddtnlComStatusName());
						additionalList.add(additionalProfileCombinedVO);
						if (!BTSLUtil.isNullString(additionalProfileServicesVO.getSubServiceCode())) {
							response.setSubServiceCode(additionalProfileServicesVO.getSubServiceCode());

						}
					}
				}
				response.setAdditionalProfileList(additionalList);
			}

			final boolean value = (Boolean) PreferenceCache.getSystemPreferenceValue(PreferenceI.ALLOW_ROAM_ADDCOMM);
			if (value) {
				response.setRoamRecharge(PretupsI.YES);
			}
			response.setStatus((HttpStatus.SC_OK));
			response.setMessageCode(PretupsErrorCodesI.SUCCESS);
			String resmsg = RestAPIStringParser.getMessage(locale, PretupsErrorCodesI.SUCCESS, null);
			response.setMessage(resmsg);

		} finally {
			if (log.isDebugEnabled()) {
				log.debug(METHOD_NAME, "Exiting:=" + METHOD_NAME);
			}
		}
		return response;
	}

}

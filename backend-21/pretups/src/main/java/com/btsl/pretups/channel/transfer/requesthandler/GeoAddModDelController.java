package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.db.util.MComConnectionI;
import com.btsl.event.EventComponentI;
import com.btsl.event.EventHandler;
import com.btsl.event.EventIDI;
import com.btsl.event.EventLevelI;
import com.btsl.event.EventStatusI;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsErrorCodesI;
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.master.businesslogic.GeographicalDomainDAO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainTypeVO;
import com.btsl.pretups.master.businesslogic.GeographicalDomainVO;
import com.btsl.pretups.master.businesslogic.UserGeographiesDAO;
import com.btsl.pretups.preference.businesslogic.PreferenceCache;
import com.btsl.pretups.preference.businesslogic.PreferenceI;
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.btsl.util.OracleUtil;
import com.web.pretups.master.businesslogic.GeographicalDomainWebDAO;

/**
 * Controller for Geography Add, Modify and Delete API
 * @author ankit.agarwal
 * @since 31/07/2017
 *
 */
public class GeoAddModDelController implements ServiceKeywordControllerI{
    private static final Log LOG = LogFactory.getLog(GeoAddModDelController.class.getName());
    private ChannelUserVO senderVO = null;
    private GeographicalDomainVO geoDomainVO = null;
    private Map masterDataMap = null;
    private static final String PROCESS = "GeoAddModDelController[process]";
    private static final String CLASSNAME = "GeoAddModDelController";

    /**
     * Method Process
     * Process Method , Processes external channel user registration request
     * 
     * @param requestVO
     */
    public void process(RequestVO requestVO) {
        final String methodName = "process";
        LogFactory.printLog(methodName, "Entered requestVO=" + requestVO, LOG);
        Connection con = null;MComConnectionI mcomCon = null;
        final HashMap requestMap = requestVO.getRequestMap();
        StringBuilder loggerValue= new StringBuilder(); 
        geoDomainVO = new GeographicalDomainVO();
        masterDataMap = new HashMap();
        final String colon = ":";
        
        try {
        	mcomCon = new MComConnection();con=mcomCon.getConnection();

            senderVO = new ChannelUserVO();
            
            String networkCode = (String) requestMap.get("EXTNWCODE");
            String geoType = (String) requestMap.get("GEOGRAPHYTYPE");
            String parentGeoCode = (String) requestMap.get("PARENTGEOGRAPHYCODE");
            String geoCode = (String) requestMap.get("GEOCODE");
            String geoName = (String) requestMap.get("GEOGRAPHYNAME");
            String shortName = (String) requestMap.get("GEOGRAPHYSHORTNAME");
            String description = (String) requestMap.get("GEOGRAPHYDESCRIPTION");
            String isDefault = (String) requestMap.get("GEOGRAPHYDEFAULTFLAG");
            String action = (String) requestMap.get("GEOGRAPHYACTION");

            senderVO.setNetworkID(networkCode);
            geoDomainVO.setParentDomainCode(parentGeoCode);
            geoDomainVO.setGrphDomainType(geoType);
            geoDomainVO.setGrphDomainCode(geoCode);
            geoDomainVO.setGrphDomainName(geoName);
            geoDomainVO.setGrphDomainShortName(shortName);
            geoDomainVO.setDescription(description);
            geoDomainVO.setIsDefault(isDefault);
            
            if(PretupsI.ADD_ACTION.equalsIgnoreCase(action) || PretupsI.MODIFY_ACTION.equalsIgnoreCase(action)) {
                geoDomainVO.setStatus(PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE);
            } else if (PretupsI.DELETE_ACTION.equalsIgnoreCase(action)) {
                geoDomainVO.setStatus(PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_DELETE);
            }
            geoDomainVO.setNetworkCode(networkCode);
            geoDomainVO.setCreatedOn(new Date());
            geoDomainVO.setCreatedBy(PretupsI.SYSTEM);
            geoDomainVO.setModifiedOn(new Date());
            geoDomainVO.setModifiedBy(PretupsI.SYSTEM);
            
            masterDataMap = loadContentList(con, senderVO);

            
            List geographicalDomainTypeList = (List)masterDataMap.get("geographicalDomainTypeList");
            List geographicalDomainDetailsList = (List)masterDataMap.get("geographicalDomainDetailsList");
            List geographicalDomainDeletedList = (List)masterDataMap.get("geographicalDomainDeletedList");
            Map geoTypeHashmap=new HashMap();
            Map geoSequenceHashmap=new HashMap();
            Map geoDomainShortNameCodeMap=new HashMap();
            Map geoDomainNameCodeMap=new HashMap();
            Map geoDomainCodeHashmap=new HashMap();
            Map geoDomainNameHashmap=new HashMap();
            Map geoDomainShortNameHashmap=new HashMap();
            
            Map geoDomainCodeDeletedHashmap = new HashMap();
            
            Map geoDomainCodeNameShortname = new HashMap();
               int geographicalDomainTypeListsizes=geographicalDomainTypeList.size();
            for(int i=0;i<geographicalDomainTypeListsizes;i++) {
                GeographicalDomainTypeVO typeVO = (GeographicalDomainTypeVO)geographicalDomainTypeList.get(i);
                geoTypeHashmap.put(typeVO.getGrphDomainType() , typeVO.getGrphDomainSequenceNo());
                geoSequenceHashmap.put(typeVO.getGrphDomainSequenceNo() , typeVO.getGrphDomainType());
            }
            for(int i=0;i<geographicalDomainDetailsList.size();i++) {
                GeographicalDomainVO domainVO = (GeographicalDomainVO)geographicalDomainDetailsList.get(i);
                geoDomainNameCodeMap.put(domainVO.getGrphDomainName(),domainVO.getGrphDomainCode());
                geoDomainShortNameCodeMap.put(domainVO.getGrphDomainShortName(),domainVO.getGrphDomainCode());
                geoDomainCodeHashmap.put(domainVO.getGrphDomainCode() , domainVO.getGrphDomainType());
                geoDomainNameHashmap.put(domainVO.getGrphDomainName() , domainVO.getGrphDomainType());
                geoDomainShortNameHashmap.put(domainVO.getGrphDomainShortName() , domainVO.getGrphDomainType());
                geoDomainCodeNameShortname.put(domainVO.getGrphDomainCode(), domainVO.getGrphDomainName()+colon+domainVO.getGrphDomainShortName());
            }
            
            if(geographicalDomainDeletedList !=null){
                for(int i=0;i<geographicalDomainDeletedList.size();i++) {
                    GeographicalDomainVO domainVO = (GeographicalDomainVO)geographicalDomainDeletedList.get(i);
                    geoDomainCodeDeletedHashmap.put(domainVO.getGrphDomainCode() , domainVO.getGrphDomainType());
                }
            }
            
            int parentSequence = -1;
            int geoDomainSequence = -1;
            
            
            //===================== Field Number 1: start of Geographical Domain Type validation =====================
            if (!geoTypeHashmap.containsKey(geoType) || !geoTypeHashmap.containsKey(geoType.toUpperCase())){
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEO_DOMAIN_TYPE_INVALID);
            }else if (geoTypeHashmap.containsKey(geoType)) {
                //to check if geographical domain is added under correct parent
                geoDomainSequence = Integer.parseInt(geoTypeHashmap.get(geoType).toString());
            }
            
            //===================== Field Number 1: End of Geographical Domain Type validation =====================   
            
            //===================== Field Number 2: start of Parent Geographical Code validation =====================
            
            if(BTSLUtil.isNullString(parentGeoCode) && geoDomainSequence>2 && PretupsI.ADD_ACTION.equalsIgnoreCase(action.trim())) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEO_PARENT_CODE_BLANK);
            }else if(!BTSLUtil.isNullString(parentGeoCode)){
                if(!geoDomainCodeHashmap.containsKey(parentGeoCode)|| !geoDomainCodeHashmap.containsKey(parentGeoCode.toUpperCase()) ) {
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEO_PARENT_CODE_INVALID);
                } else {
                    String parentType = geoDomainCodeHashmap.get(parentGeoCode).toString();
                    parentSequence = Integer.parseInt(geoTypeHashmap.get(parentType).toString());
                    if( (geoDomainSequence - parentSequence) != 1) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEO_PARENT_CODE_INVALID);
                    }
                }
            }
            //===================== Field Number 2: end of Parent Geographical Code validation =====================
           
            //===================== Field Number 3: start of Geographical Domain Code validation =====================
            if(BTSLUtil.isNullString(geoCode)){
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEO_CODE_BLANK);
            }else if (geoCode.length() > (Integer.valueOf(Constants.getProperty("GRPH_DOMAIN_CODE_LEN")))){
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEO_CODE_LENGTH);
            }else if (geoDomainCodeDeletedHashmap.containsKey(geoCode) || geoDomainCodeDeletedHashmap.containsKey(geoCode.toUpperCase())
            		|| geoDomainCodeHashmap.containsKey(geoCode) || geoDomainCodeHashmap.containsKey(geoCode.toUpperCase() )){
                if(PretupsI.ADD_ACTION.equalsIgnoreCase(action.trim())){
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEO_CODE_ALREADY_EXISTS);
            	}
            }else if ((!geoDomainCodeHashmap.containsKey(geoCode) || !geoDomainCodeHashmap.containsKey(geoCode.toUpperCase())) 
            		&& PretupsI.MODIFY_ACTION.equalsIgnoreCase(action.trim())) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEO_CODE_NOT_FOUND);
            }else if ((!geoDomainCodeHashmap.containsKey(geoCode) || !geoDomainCodeHashmap.containsKey(geoCode.toUpperCase())) && 
                PretupsI.DELETE_ACTION.equalsIgnoreCase(action.trim())) {                
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEO_CODE_NOT_FOUND);
            }
            
            //===================== Field Number 3: End of Geographical Domain Code validation =====================               

            //===================== Field Number 4: Geographical Domain Name validation starts here =====================
            if(BTSLUtil.isNullString(geoName) && (PretupsI.ADD_ACTION.equalsIgnoreCase(action.trim()) || PretupsI.MODIFY_ACTION.equalsIgnoreCase(action.trim()))){
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEO_NAME_BLANK);
            }else if (!BTSLUtil.isNullString(geoName) && (geoDomainNameHashmap.containsKey(geoName) || geoDomainNameHashmap.containsKey(geoName.toUpperCase()))){
                if(PretupsI.ADD_ACTION.equalsIgnoreCase(action.trim())){
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEO_NAME_ALREADY_EXISTS); 
                }else if(PretupsI.MODIFY_ACTION.equalsIgnoreCase(action.trim()) && !geoCode.equals(geoDomainNameCodeMap.get(geoName))){
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEO_NAME_ALREADY_EXISTS); 
            	}     
            }else if (!BTSLUtil.isNullString(geoName) && (geoName.length() > (Integer.valueOf(Constants.getProperty("GRPH_DOMAIN_NAME_LEN"))))){
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEO_NAME_LENGTH_EXCEEDS);
            }
            //===================== Field Number 4: End of Geographical Domain Name validation =====================    
            
            //===================== Field Number 5: Geographical Domain Short Name validation starts here =====================
            if(BTSLUtil.isNullString(shortName) && (PretupsI.ADD_ACTION.equalsIgnoreCase(action.trim()) || PretupsI.MODIFY_ACTION.equalsIgnoreCase(action.trim()))){
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEO_SHORTNAME_BLANK);
            }else if (!BTSLUtil.isNullString(shortName) && (geoDomainShortNameHashmap.containsKey(shortName) || 
            		geoDomainShortNameHashmap.containsKey(shortName.toUpperCase()))) {
                if(PretupsI.ADD_ACTION.equalsIgnoreCase(action.trim())){
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEO_SHORTNAME_ALREADY_EXISTS);
                }else if(PretupsI.MODIFY_ACTION.equalsIgnoreCase(action.trim()) && !geoCode.equals(geoDomainShortNameCodeMap.get(shortName))){
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEO_SHORTNAME_ALREADY_EXISTS);
                }
                
            }else if (!BTSLUtil.isNullString(shortName) && (shortName.length() > (Integer.valueOf(Constants.getProperty("GRPH_DOMAIN_SHORT_NAME_LEN"))))) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEO_SHORTNAME_LENGTH_EXCEEDS);
            }
            //===================== Field Number 5: End of Geographical Domain Short Name validation =====================    

            //===================== Field Number 6: Description validation (not needed) starts here =====================
            description=description.trim();
            if (!BTSLUtil.isNullString(description) && description.length() > (Integer.valueOf(Constants.getProperty("GRPH_DOMAIN_DESCRIPTION_LEN"))) ) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEO_DESCRIPTION_LENGTH_EXCEEDS);
            }
            //===================== Field Number 6: End of Description validation =====================    

          //===================== Field Number 7: IsDefault validation starts here =====================
            
            
            
          //===================== Field Number 7: IsDefault validation ends here =====================
            if(BTSLUtil.isNullString(isDefault) && PretupsI.ADD_ACTION.equalsIgnoreCase(action.trim())){
                geoDomainVO.setIsDefault(PretupsI.NO);
            }else if(!BTSLUtil.isNullString(isDefault) && (!PretupsI.YES.equalsIgnoreCase(isDefault) && !PretupsI.NO.equalsIgnoreCase(isDefault))) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEO_ISDEFAULT_INVALID);
            }
            
            //===================== Field Number 8: Action(A/M/D/Blank validation starts here =====================
            if( !PretupsI.ADD_ACTION.equalsIgnoreCase(action) && !PretupsI.MODIFY_ACTION.equalsIgnoreCase(action) && !PretupsI.DELETE_ACTION.equalsIgnoreCase(action) ) {
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEO_ACTION_INVALID);
            }
            
            //===================== Field Number 8: End of Action(A/M/D/Blank validation =====================    
            
            GeographicalDomainWebDAO geographicalDomainWebDAO = new GeographicalDomainWebDAO();
            
            if(PretupsI.ADD_ACTION.equalsIgnoreCase(action)){
                int defaultCount = 0;
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue() && PretupsI.YES.equals(isDefault)) {
                    if(geoDomainSequence>2){
                        defaultCount = geographicalDomainWebDAO.updatedeDefaultGeography(con, senderVO.getNetworkID(), parentGeoCode);
                    }else{
                        defaultCount = geographicalDomainWebDAO.updatedeDefaultGeography(con, senderVO.getNetworkID(), senderVO.getNetworkID());
                    }
                    if(defaultCount < 0){
                        OracleUtil.rollbackConnection(con, CLASSNAME, methodName);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, PROCESS, "", "", "",
                            "Exception:defaultCount <=0 ");
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEOGRAPHY_ADD_FAILED);
                    }
                }
                final int addCount = geographicalDomainWebDAO.addGeographicalDomain(con, geoDomainVO);
                
                if (addCount <= 0) {
                    OracleUtil.rollbackConnection(con, CLASSNAME, methodName);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, PROCESS, "", "", "",
                        "Exception:add count <=0 ");
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEOGRAPHY_ADD_FAILED);
                }

                LogFactory.printLog(methodName, "After Insert in Geographical_Domains " + geoDomainVO, LOG);
            }else if(PretupsI.MODIFY_ACTION.equalsIgnoreCase(action)){
                GeographicalDomainVO oldGeoDomainVO = new GeographicalDomainDAO().loadGeoDomainVO(con, networkCode, geoType, geoCode);
                if(oldGeoDomainVO==null){
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEO_CODE_NOT_FOUND);
                }
                final int updateCount = geographicalDomainWebDAO.updateGeographicalDomain(con, geoDomainVO);
                int defaultCount = 0;
                if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue() && PretupsI.YES.equals(isDefault) && !PretupsI.YES.equals(oldGeoDomainVO.getIsDefault())){
                    defaultCount = geographicalDomainWebDAO.updatedeDefaultGeography(con, senderVO.getNetworkID(), oldGeoDomainVO.getParentDomainCode());
                    if(defaultCount < 0){
                        OracleUtil.rollbackConnection(con, CLASSNAME, methodName);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, PROCESS, "", "", "",
                            "Exception:defaultCount <=0 ");
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEOGRAPHY_UPDATE_FAILED);
                    }
                }else if (PretupsI.NO.equals(isDefault) && PretupsI.YES.equals(oldGeoDomainVO.getIsDefault())){
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.DEFAULT_GEO_MODIFIED);
                }
                if (updateCount <= 0){
                    OracleUtil.rollbackConnection(con, CLASSNAME, methodName);
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, PROCESS, "", "", "",
                        "Exception:Update count <=0 ");
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEOGRAPHY_UPDATE_FAILED);
                }

                LogFactory.printLog(methodName, "After Update in Geographical_Domains " + geoDomainVO, LOG);
            }else{
                geoDomainVO = new GeographicalDomainDAO().loadGeoDomainVO(con, networkCode, geoType, geoCode);
            	
                if (geoDomainVO != null) {
                    if (((Boolean) (PreferenceCache.getSystemPreferenceValue(PreferenceI.IS_DEFAULT_PROFILE))).booleanValue() && PretupsI.YES.equals(geoDomainVO.getIsDefault())) {
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEO_IS_DEFAULT);
                    }

                    // if requested Domain has active children then show the error
                    // message.
                    if (geographicalDomainWebDAO.isGeographicalDomainActive(con, geoDomainVO.getGrphDomainCode())) {
                        LogFactory.printLog(methodName, "isgeographicalDomainActive=true", LOG);
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEO_CHILD_EXISTS);
                    }

                    // if any active channel user is associated with the requested
                    // Domain then show the error message.
                    if (new UserGeographiesDAO().isActiveUserAssociatedWithGrphDomain(con, geoDomainVO.getGrphDomainCode())) {
                        LogFactory.printLog(methodName, "isActiveUserAssociatedWithGrphDomain=true", LOG);
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEO_ALREADY_ASSOCIATED);
                    }

                    geoDomainVO.setModifiedOn(new Date());
                    geoDomainVO.setModifiedBy(PretupsI.SYSTEM);            	
                    geoDomainVO.setStatus(PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_DELETE);
                    final int deleteCount = geographicalDomainWebDAO.updateGeographicalDomain(con, geoDomainVO);
                    if (deleteCount <= 0) {
                        OracleUtil.rollbackConnection(con, CLASSNAME, methodName);
                        EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, PROCESS, "", "", "",
                            "Exception:Update count <=0 ");
                        throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEOGRAPHY_UPDATE_FAILED);
                    }
                }else{
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEOGRAPHY_NOT_EXISTS);
                }
            }
 
            if(mcomCon != null){mcomCon.close("GeoAddModDelController#process");mcomCon=null;}

            requestVO.setMessageCode(PretupsI.TXN_STATUS_SUCCESS);
            requestVO.setValueObject(geoDomainVO);
            
        }catch (BTSLBaseException be) {
            requestVO.setSuccessTxn(false);

            try {
                mcomCon.finalRollback();
            } catch (Exception ee) {
                LOG.errorTrace(methodName, ee);
            }
            loggerValue.setLength(0);
        	loggerValue.append("BTSLBaseException ");
        	loggerValue.append(be.getMessage());
            LOG.error(methodName,  loggerValue );
            LOG.errorTrace(methodName, be);
            if (be.isKey()) {
                requestVO.setMessageCode(be.getMessageKey());
                requestVO.setMessageArguments(be.getArgs());
            } else {
                requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
                return;
            }
        } catch (Exception e) {
            requestVO.setSuccessTxn(false);
            try {
                mcomCon.finalRollback();
            } catch (Exception ee) {
                LOG.errorTrace(methodName, ee);
            }
            loggerValue.setLength(0);
        	loggerValue.append("Exception ");
        	loggerValue.append(e.getMessage());
            LOG.error(methodName,  loggerValue);
            LOG.errorTrace(methodName, e);
            loggerValue.setLength(0);
        	loggerValue.append( "Exception:");
        	loggerValue.append(e.getMessage());
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, PROCESS, "", "", "",
            		loggerValue.toString());
            requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
            requestMap.put("GEODOMAINVO", geoDomainVO);
            requestVO.setRequestMap(requestMap);
            geoDomainVO = null;
            if(mcomCon != null){mcomCon.close("GeoAddModDelController#process");mcomCon=null;}
            LogFactory.printLog(methodName, " Exited ", LOG);
        }
    }

    /**
     * This method load geography information in the hash maps
     * @param con
     * @param channelUserVO
     * @throws BTSLBaseException
     * @return newMasterDataMap
     */
    public Map<String, List> loadContentList(Connection con, ChannelUserVO channelUserVO) throws BTSLBaseException {
        final String methodName = "BatchGeographicalDomainAction[loadContentList()]";
        LogFactory.printLog(methodName, "Entered", LOG);
        List geographicalDomainTypeList = null;
        List geographicalDomainDetailList = null;
        List geographicalDomainDeletedList = null;
        Map newMasterDataMap = null;
        
        
        try{            
            GeographicalDomainDAO geoDomainDao = new GeographicalDomainDAO();
            geographicalDomainTypeList = geoDomainDao.loadDomainTypes(con, -1 , 6);
            geographicalDomainDetailList = geoDomainDao.loadGeoDomainList(con, channelUserVO.getNetworkID(),PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_DELETE);
            geographicalDomainDeletedList = geoDomainDao.loadGeoDomainList(con, channelUserVO.getNetworkID(),PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_ACTIVE);
            
            newMasterDataMap = new HashMap();
            newMasterDataMap.put("geographicalDomainTypeList",geographicalDomainTypeList);
            newMasterDataMap.put("geographicalDomainDetailsList",geographicalDomainDetailList);
            newMasterDataMap.put("geographicalDomainDeletedList",geographicalDomainDeletedList);
                        
        }catch (Exception e){
            LOG.errorTrace(methodName,e);
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_SERVICES_NOT_FOUND);
        }finally{
            LogFactory.printLog(methodName, "Exiting", LOG);
        }
        return newMasterDataMap;
    }
    
}

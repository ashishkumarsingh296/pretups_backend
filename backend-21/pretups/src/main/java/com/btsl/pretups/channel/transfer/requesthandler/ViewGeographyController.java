package com.btsl.pretups.channel.transfer.requesthandler;

import java.sql.Connection;
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
import com.btsl.pretups.receiver.RequestVO;
import com.btsl.pretups.servicekeyword.requesthandler.ServiceKeywordControllerI;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;

/**
 * Controller for View Geography API
 * @author ankit.agarwal
 * @since 31/07/2017
 *
 */
public class ViewGeographyController implements ServiceKeywordControllerI{
    private static final Log LOG = LogFactory.getLog(ViewGeographyController.class.getName());
    private GeographicalDomainVO geoDomainVO = null;
    private Map masterDataMap = null;
    private static final String PROCESS = "ViewGeographyController[process]";

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
        geoDomainVO = new GeographicalDomainVO();
        GeographicalDomainDAO geoDomainDAO = new GeographicalDomainDAO();
        masterDataMap = new HashMap();
        
        try {
        	mcomCon = new MComConnection();con=mcomCon.getConnection();
            
            String networkCode = (String) requestMap.get("EXTNWCODE");
            String geoType = (String) requestMap.get("GEOGRAPHYTYPE");
            String geoCode = (String) requestMap.get("GEOCODE");
            String geoName = (String) requestMap.get("GEOGRAPHYNAME");

            requestVO.setNetworkCode(networkCode);
            geoDomainVO.setGrphDomainType(geoType);
            geoDomainVO.setGrphDomainCode(geoCode);
            geoDomainVO.setNetworkCode(networkCode);
            
            masterDataMap = loadContentList(con, requestVO);
            
            List geographicalDomainTypeList = (List)masterDataMap.get("geographicalDomainTypeList");
            Map geoTypeHashmap=new HashMap();
            int geographicalListSize = geographicalDomainTypeList.size();
            for(int i=0;i< geographicalListSize;i++) {
                GeographicalDomainTypeVO typeVO = (GeographicalDomainTypeVO)geographicalDomainTypeList.get(i);
                geoTypeHashmap.put(typeVO.getGrphDomainType() , typeVO.getGrphDomainSequenceNo());
            }
            
            
            //===================== Field Number 1: start of Geographical Domain Type validation =====================
            if (!geoTypeHashmap.containsKey(geoType) || !geoTypeHashmap.containsKey(geoType.toUpperCase())){
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEO_DOMAIN_TYPE_INVALID);
            }
           
            //===================== Field Number 2: start of Geographical Domain Code validation =====================
            if(!BTSLUtil.isNullString(geoCode)){
                if (geoCode.length() > (Integer.valueOf(Constants.getProperty("GRPH_DOMAIN_CODE_LEN")))){
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEO_CODE_LENGTH);
                }
                
                GeographicalDomainVO newGeoDomainVO = new GeographicalDomainVO();
                newGeoDomainVO = geoDomainDAO.loadGeoDomainVO(con, geoDomainVO.getNetworkCode(), geoDomainVO.getGrphDomainType(), geoDomainVO.getGrphDomainCode());
                if (newGeoDomainVO.getGrphDomainCode()==null) {
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, PROCESS, "", "", "",
                        "Exception:no details found ");
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEOGRAPHY_NO_DETAILS_FOUND);
                }
                geoDomainVO=newGeoDomainVO;
                LogFactory.printLog(methodName, "After loading geography details" + geoDomainVO, LOG);
                requestVO.setMessageCode(PretupsI.TXN_STATUS_SUCCESS);
                requestVO.setValueObject(geoDomainVO);
            }else if (BTSLUtil.isNullString(geoName)){
                throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEOGRAPHY_CODE_AND_NAME_BLANK);
            }else{
                boolean geographyFound = false;
                List geoDomainList = geoDomainDAO.loadGeoDomainList(con, networkCode,PretupsI.GEOGRAPHICAL_DOMAIN_STATUS_DELETE);
                int geoDomainListSize = geoDomainList.size();
                for(int i=0; i< geoDomainListSize;i++){
                    geoDomainVO=(GeographicalDomainVO)geoDomainList.get(i);
                    if(geoName.equalsIgnoreCase(geoDomainVO.getGrphDomainName())){
                        geographyFound=true;
                        requestVO.setMessageCode(PretupsI.TXN_STATUS_SUCCESS);
                        requestVO.setValueObject(geoDomainVO);
                        break;
            		}
            	}
                if(!geographyFound){
                    EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.MAJOR, PROCESS, "", "", "",
                            "Exception:no details found ");
                    throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.GEOGRAPHY_NO_DETAILS_FOUND);
            	}
            }
            
            
        }catch (BTSLBaseException be) {
            requestVO.setSuccessTxn(false);
            LOG.error(methodName, "BTSLBaseException " + be.getMessage());
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
            LOG.error(methodName, "Exception " + e.getMessage());
            LOG.errorTrace(methodName, e);
            EventHandler.handle(EventIDI.SYSTEM_ERROR, EventComponentI.SYSTEM, EventStatusI.RAISED, EventLevelI.FATAL, PROCESS, "", "", "",
                "Exception:" + e.getMessage());
            requestVO.setMessageCode(PretupsErrorCodesI.REQ_NOT_PROCESS);
            return;
        } finally {
            requestMap.put("GEODOMAINVO", geoDomainVO);
            requestVO.setRequestMap(requestMap);
            geoDomainVO = null;
            if(mcomCon != null){mcomCon.close("ViewGeographyController#process");mcomCon=null;}
            LogFactory.printLog(methodName, " Exited ", LOG);
        }
    }

    /**
     * This method load geography information in the hash maps
     * @param con
     * @param requestVO
     * @throws BTSLBaseException
     * @return newMasterDataMap
     */
    public Map loadContentList(Connection con, RequestVO requestVO) throws BTSLBaseException {
        final String methodName = "BatchGeographicalDomainAction[loadContentList()]";
        LogFactory.printLog(methodName, "Entered", LOG);
        List geographicalDomainTypeList = null;
        Map newMasterDataMap = null;
        
        
        try{            
            GeographicalDomainDAO geoDomainDao = new GeographicalDomainDAO();
            geographicalDomainTypeList = geoDomainDao.loadDomainTypes(con, -1 , 6);
            
            newMasterDataMap = new HashMap();
            newMasterDataMap.put("geographicalDomainTypeList",geographicalDomainTypeList);
                        
        }catch (Exception e){
            LOG.errorTrace(methodName,e);
            throw new BTSLBaseException(this, methodName, PretupsErrorCodesI.EXTSYS_REQ_USR_SERVICES_NOT_FOUND);
        }finally{
            LogFactory.printLog(methodName, "Exiting", LOG);
        }
        return newMasterDataMap;
    }
    
}

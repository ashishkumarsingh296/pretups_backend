package com.btsl.pretups.master.businesslogic;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
/*
 * This class implements LookupsRestService and provides basic method to load Look and sublookups
 */
public class LookupsRestServiceImpl implements LookupsRestService {

	public static final Log _log = LogFactory.getLog(LookupsRestServiceImpl.class.getName());
	
	/**
	 * This method load lookup from cache basis on lookup type
	 * @param requestData Json string of lookup type
	 * @return  PretupsResponse<List<ListValueVO>>
	 * @throws IOException, Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<List<ListValueVO>> loadLookupDropDown(String requestData) throws IOException {
		if (_log.isDebugEnabled()) {
			_log.debug("LookupsRestServiceImpl#loadLookupDropDown", "Entered ");
		}
		PretupsResponse<List<ListValueVO>> response = new PretupsResponse<List<ListValueVO>>();
		JsonNode requestNode = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
		JsonNode dataNode =  requestNode.get("data");
		if(!dataNode.has("lookupType") || !dataNode.has("active") || dataNode.size() == 0){
			 response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, new ArrayList<ListValueVO>());
			 return response;
		}
		List<ListValueVO> list = LookupsCache.loadLookupDropDown(dataNode.get("lookupType").textValue(), dataNode.get("active").asBoolean());
		if (_log.isDebugEnabled()) {
			_log.debug("LookupsRestServiceImpl#loadLookupDropDown", "Exiting");
		}
		response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,list);
		return response;
	}

	/**
	 * This method load sub-lookup from cache basis on lookup type
	 * @param requestData Json string of lookup type
	 * @return  PretupsResponse<List<SubLookUpVO>>
	 * @throws IOException, Exception
	 */
	@Override
	public PretupsResponse<List<SubLookUpVO>> loadSublookupVOList(String requestData) throws IOException, SQLException, BTSLBaseException{
		if (_log.isDebugEnabled()) {
			_log.debug("LookupsRestServiceImpl#loadSublookupVOList", "Entered ");
		}
		PretupsResponse<List<SubLookUpVO>> response = new PretupsResponse<List<SubLookUpVO>>();
		JsonNode requestNode = (JsonNode) PretupsRestUtil.convertJSONToObject(requestData, new TypeReference<JsonNode>() {});
		JsonNode dataNode =  requestNode.get("data");
		if(!dataNode.has("lookupType") || dataNode.size() == 0){
			 response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, new ArrayList<SubLookUpVO>());
			return response;
		}
		SubLookUpDAO subLookUpDAO = new SubLookUpDAO();
		if (_log.isDebugEnabled()) {
			_log.debug("LookupsRestServiceImpl#loadSublookupVOList", "Exiting");
		}
		response.setDataObject(PretupsI.RESPONSE_SUCCESS, true,subLookUpDAO.loadSublookupVOList(dataNode.get("lookupType").textValue()));
		return response;
		
	}
}

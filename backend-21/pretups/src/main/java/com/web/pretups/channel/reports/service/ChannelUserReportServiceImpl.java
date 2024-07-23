package com.web.pretups.channel.reports.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.ListValueVO;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestClient;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.fasterxml.jackson.core.type.TypeReference;

@Service("channelUserReportService")

public class ChannelUserReportServiceImpl implements ChannelUserReportService {

	public static final Log _log = LogFactory.getLog(ChannelUserReportServiceImpl.class.getName());

	@Autowired
	private PretupsRestClient pretupsRestClient;

	/**
	 * * Load Domain for Channel User * @return List The list of lookup filtered
	 * from DB * @throws IOException, Exception
	 */

	@SuppressWarnings("unchecked")

	public List<ListValueVO> loadDomain() throws Exception {
		final String methodName = "loadDomain";
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}
		Map<String, Object> data = new HashMap();
		data.put("excludeUserType", PretupsI.DOMAIN_TYPE_CODE);

		Map<String, Object> object = new HashMap();
		object.put("data", data);
		String responseString = pretupsRestClient.postJSONRequest(object, PretupsI.GREETMSGDOMAIN);

		PretupsResponse<List<ListValueVO>> response = (PretupsResponse<List<ListValueVO>>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<ListValueVO>>>() {
				});
		List<ListValueVO> list = response.getDataObject();
		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting");
		}
		return list;
	}

	/**
	 * * Load Category for Channel User(Receiver) * @return List The list of
	 * lookup filtered from DB * @throws IOException, Exception
	 */

	@SuppressWarnings("unchecked")
	@Override
	public List<ListValueVO> loadCategory() throws BTSLBaseException, Exception {
		final String methodName = "loadCategory";

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}

		Map<String, Object> data = new HashMap<String, Object>();

		Map<String, Object> object = new HashMap<String, Object>();
		object.put("data", data);
		String responseString = pretupsRestClient.postJSONRequest(object, PretupsI.GREETMSGCAT);

		PretupsResponse<List<ListValueVO>> response = (PretupsResponse<List<ListValueVO>>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<ListValueVO>>>() {
				});

		List<ListValueVO> list = (List<ListValueVO>) response.getDataObject();
		processListValueVOValue(list, "OPT");

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting SERVICE IMPL:::" + list);
		}
		return list;

	}

	@SuppressWarnings("unchecked")
	public List<ListValueVO> loadUserData(String domain, String category, String geography, String user, String userId,
			String domID) throws BTSLBaseException, IOException {
		final String methodName = "loadUserData";
		String responseString;
		if (_log.isDebugEnabled()) {
			_log.debug(methodName,
					"Entered : domain : " + domain + " category : " + category + " geography : " + geography);
		}
		Map<String, Object> data = new HashMap();
		data.put("domainCode", domain);
		data.put("userCat", category);
		data.put("zoneCode", geography);
		data.put("ownerName", "%" + user + "%");
		data.put("userId", userId);
		data.put("domID", domID);
		Map<String, Object> object = new HashMap();
		object.put("data", data);

		responseString = pretupsRestClient.postJSONRequest(object, "LOADCHNUSRLIST");

		PretupsResponse<List<ListValueVO>> response = (PretupsResponse<List<ListValueVO>>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<ListValueVO>>>() {
				});

		List<ListValueVO> list = (List<ListValueVO>) response.getDataObject();

		List<ListValueVO> dropDown = new ArrayList<>();

		if (list != null && !list.isEmpty()) {
			list.forEach(userVO -> {
				ListValueVO listVO = new ListValueVO(userVO.getLabel(), userVO.getValue() + ":" + userVO.getLabel());
				dropDown.add(listVO);
			});
		}

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting with list:" + dropDown);
		}

		return dropDown;

	}

	/**
	 * * Load Channel User details for Channel User(Receiver) * @return List The
	 * list of lookup filtered from DB * @throws IOException, Exception
	 */

	@Override
	public <T> void processListValueVOValue(List<T> listObject, String type) throws Exception {
		ListValueVO listValueVO = null;
		if (listObject != null && !listObject.isEmpty()) {
			for (int i = 0, j = listObject.size(); i < j; i++) {
				listValueVO = (ListValueVO) listObject.get(i);
				if ((listValueVO.getValue().split(":")[0].toString()).equals(type)) {
					listObject.remove(i);
					i--;
					j--;
				}
			}
		}
	}

	@Override
	public List<ListValueVO> loadTxnSubType() throws Exception {
		// TODO Auto-generated method stub
		final String methodName = "loadTxnSubType";

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}

		Map<String, Object> data = new HashMap<String, Object>();

		data.put("lookupType", PretupsI.TRANSFER_TYPE);
		data.put("active", PretupsI.YES);

		Map<String, Object> object = new HashMap<String, Object>();
		object.put("data", data);
		String responseString = pretupsRestClient.postJSONRequest(object, PretupsI.LOOKUP);

		PretupsResponse<List<ListValueVO>> response = (PretupsResponse<List<ListValueVO>>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<ListValueVO>>>() {
				});

		List<ListValueVO> list = (List<ListValueVO>) response.getDataObject();

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting with list : " + list);
		}
		return list;

	}

	@Override
	public List<ListValueVO> loadTransfercategory() throws Exception {
		// TODO Auto-generated method stub

		final String methodName = "loadCategory";

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered ");
		}

		Map<String, Object> data = new HashMap<String, Object>();

		data.put("lookupType", PretupsI.C2C_TRANSFER_TYPE);
		data.put("active", PretupsI.YES);

		Map<String, Object> object = new HashMap<String, Object>();
		object.put("data", data);
		String responseString = pretupsRestClient.postJSONRequest(object, PretupsI.LOOKUP);

		PretupsResponse<List<ListValueVO>> response = (PretupsResponse<List<ListValueVO>>) PretupsRestUtil
				.convertJSONToObject(responseString, new TypeReference<PretupsResponse<List<ListValueVO>>>() {
				});

		List<ListValueVO> list = (List<ListValueVO>) response.getDataObject();

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Exiting list : " + list);
		}

		return list;
	}

}

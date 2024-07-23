package com.btsl.pretups.channel.reports.businesslogic;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.PretupsResponse;
import com.btsl.common.PretupsRestUtil;
import com.btsl.logging.Log;
import com.btsl.logging.LogFactory;
import com.btsl.pretups.common.PretupsI;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.OracleUtil;
import com.fasterxml.jackson.core.type.TypeReference;

/*
 * This class implements ChannelUserReportRestService and provides basic method to load Channel User List
 */

public class ChannelUserReportRestServiceImpl implements ChannelUserReportRestService {

	public static final Log _log = LogFactory.getLog(ChannelUserReportRestServiceImpl.class.getName());
	
	/**
	 * This method load Channel User data from DB
	 * 
	 * @param requestData
	 *            Json string of Of User Type
	 * @return PretupsResponse<List<UserVO>>
	 * @throws BTSLBaseException
	 *             , Exception
	 */

	@SuppressWarnings("unchecked")
	@Override
	public PretupsResponse<List<UserVO>> loadUserListData(String requestData) throws BTSLBaseException, IOException {
		final String methodName = "ChannelUserReportRestServiceImpl#loadUserListData";

		ChannelUserReportDAO channelUserDAO = null;
		ArrayList userList = null;

		if (_log.isDebugEnabled()) {
			_log.debug(methodName, "Entered:");
		}
		Connection con = null;
		PretupsResponse<List<UserVO>> response = new PretupsResponse<>();
		Map<String, Object> dataMap = (Map<String, Object>) PretupsRestUtil.convertJSONToObject(requestData,
				new TypeReference<Map<String, Object>>() {
				});
		Map<String, String> map = (Map<String, String>) dataMap.get("data");

		map.get("zoneCode").toString();
		map.get("ownerName").toString();
		map.get("userId").toString();
		map.get("domainCode").toString();
		map.get("userCat").toString();
		map.get("domID").toString();

		if (map.get("zoneCode") == null || map.get("ownerName") == null || map.get("userId") == null) {
			if (_log.isDebugEnabled()) {
				_log.debug(methodName, "Somethiong NUll");
			}
			response.setDataObject(PretupsI.RESPONSE_FAIL, true, null);
			return response;

		}
		try {
			if (_log.isDebugEnabled()) {
				StringBuilder loggerValue= new StringBuilder(); 
				loggerValue.setLength(0);
				loggerValue.append("Entered in data:");
				loggerValue.append(map.get("userCat"));
				loggerValue.append(map.get("zoneCode"));
				loggerValue.append(map.get("ownerName"));
				loggerValue.append(": ");
				loggerValue.append(map.get("domainCode"));
				_log.debug(methodName,  loggerValue);
			}
			con = OracleUtil.getConnection();
			channelUserDAO = new ChannelUserReportDAO();
			final String[] arr = map.get("userCat").split(":");

			if ("OPT".equalsIgnoreCase(map.get("domID").toString())) {
				if (map.get("userCat").equals(PretupsI.ALL)) {
					userList = channelUserDAO.loadUserListOnZoneDomainCategory(con, PretupsI.ALL,
							map.get("zoneCode").toString(), null, "%" + map.get("ownerName").toString() + "%",
							map.get("userId").toString(), arr[0]);
				} else {
					userList = channelUserDAO.loadUserListOnZoneDomainCategory(con, arr[1], map.get("zoneCode"), null,
							"%" + map.get("ownerName") + "%", map.get("userId"), map.get("domainCode"));
				}
			} else {
				if (map.get("userCat").equals(PretupsI.ALL)) {
					userList = channelUserDAO.loadUserListOnZoneCategoryHierarchy(con, PretupsI.ALL,
							map.get("zoneCode").toString(), "%" + map.get("ownerName").toString() + "%",
							map.get("userId").toString(), arr[0]);
				} else {
					userList = channelUserDAO.loadUserListOnZoneCategoryHierarchy(con, arr[1], map.get("zoneCode"),
							"%" + map.get("ownerName") + "%", map.get("userId"), map.get("domainCode"));
				}

			}

			if (_log.isDebugEnabled()) {
				StringBuilder loggerValue= new StringBuilder(); 
				loggerValue.setLength(0);
				loggerValue.append("Exiting: with list:");
				loggerValue.append(userList);
				_log.debug(methodName,  loggerValue );
			}
			response.setDataObject(PretupsI.RESPONSE_SUCCESS, true, userList);
		} finally {

			OracleUtil.closeQuietly(con);
			if (_log.isDebugEnabled())
				_log.debug(methodName, "Exiting:");
		}

		return response;
	}

}

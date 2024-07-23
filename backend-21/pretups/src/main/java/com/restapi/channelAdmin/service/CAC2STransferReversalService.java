package com.restapi.channelAdmin.service;

import java.sql.Connection;
import java.sql.SQLException;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.util.MultiValueMap;

import com.btsl.common.BaseResponseMultiple;
import com.restapi.channelAdmin.requestVO.CAC2STransferReversalConfirmVO;
import com.restapi.channelAdmin.requestVO.CAC2STransferReversalListRequestVO;
import com.restapi.channelAdmin.responseVO.CAC2STransferReversalResponseVO;

public interface CAC2STransferReversalService {

	/**
	 * @author sarthak.saini
	 * @param con
	 * @param loginID
	 * @param requestVO
	 * @return
	 * @throws SQLException 
	 */
	public CAC2STransferReversalResponseVO getTransferReversalList(Connection con, String loginID,
			CAC2STransferReversalListRequestVO requestVO) throws SQLException;

	/**
	 * @author sarthak.saini
	 * @param con
	 * @param requestVO
	 * @param headers 
	 * @param loginID 
	 * @param responseSwag 
	 * @param requestIDStr 
	 * @return
	 */
	public BaseResponseMultiple confirmTransferReversal(Connection con, CAC2STransferReversalConfirmVO requestVO, MultiValueMap<String, String> headers, String loginID, String requestIDStr, HttpServletResponse responseSwag);

}

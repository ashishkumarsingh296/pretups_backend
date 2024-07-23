package com.restapi.networkadmin.serviceI;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.restapi.networkadmin.requestVO.ModifyBatchC2SCardGroupRequestVO;
import com.restapi.networkadmin.requestVO.ModifyC2SCardGroupFileRequestVO;
import com.restapi.networkadmin.responseVO.CardGroupNameResponseVO;
import com.restapi.networkadmin.responseVO.ServiceTypeResponseVO;
import com.restapi.networkadmin.responseVO.UploadAndProcessFileResponseVO;
import com.restapi.user.service.FileDownloadResponse;

@Service
public interface ModifyBatchC2SCardGroupServiceI {

	public ServiceTypeResponseVO getServiceTypeList(Connection connection, String loginUserID, String module)
			throws BTSLBaseException , SQLException;
	
	public CardGroupNameResponseVO getCardGroupNameList(Connection connection, String loginUserID, String module)
			throws BTSLBaseException, SQLException;

	/**
	 * @author sarthak.saini
	 * @param con
	 * @param loginUserID
	 * @param requestVO
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws ParseException 
	 */
	public FileDownloadResponse downloadCardGroupFile(Connection con, String loginUserID,
			ModifyC2SCardGroupFileRequestVO requestVO) throws BTSLBaseException,SQLException,IOException, ParseException;

	/**
	 * @author sarthak.saini
	 * @param con
	 * @param loginUserID
	 * @param requestVO
	 * @param response1 
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 * @throws IOException
	 * @throws ParseException
	 * @throws Exception 
	 */
	public UploadAndProcessFileResponseVO modifyC2SCardGroup(Connection con, String loginUserID,
			ModifyBatchC2SCardGroupRequestVO requestVO, HttpServletResponse response1) throws BTSLBaseException,SQLException,IOException, ParseException, Exception;

}

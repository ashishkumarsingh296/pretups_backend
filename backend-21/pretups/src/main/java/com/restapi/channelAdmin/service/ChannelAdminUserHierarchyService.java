package com.restapi.channelAdmin.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.common.MasterErrorList;
import com.btsl.db.util.MComConnectionI;
import com.btsl.pretups.user.businesslogic.ChannelUserVO;
import com.btsl.user.businesslogic.OAuthUserData;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.channelAdmin.ChannelAdminTransferVO;
import com.restapi.channelAdmin.requestVO.ApprovalBarredForDltRequestVO;
import com.restapi.channelAdmin.requestVO.BarredusersrequestVO;
import com.restapi.channelAdmin.requestVO.BulkModifyUserRequestVO;
import com.restapi.channelAdmin.requestVO.SuspendResumeUserHierarchyRequestVO;
import com.restapi.channelAdmin.responseVO.BulkModifyUserResponseVO;
import com.restapi.channeluser.service.ActionOnUserReqVo;
import com.restapi.channeluser.service.ChannelUserSearchReqVo;
import com.restapi.user.service.FileDownloadResponseMulti;
import com.restapi.user.service.UserHierachyCARequestVO;
import com.restapi.user.service.UserHierarchyUIResponseData;
import com.web.pretups.channel.user.web.BatchUserForm;

public interface ChannelAdminUserHierarchyService {

	/**
	 * 
	 * @param con
	 * @param loginID
	 * @param requestVO
	 * @param responseVO
	 * @param responseSwag
	 * @return
	 * @throws SQLException
	 * @throws BTSLBaseException
	 */
	public int getUserHierarchyListCA(Connection con, String loginID, UserHierachyCARequestVO requestVO, List<UserHierarchyUIResponseData> responseVO, HttpServletResponse responseSwag) throws SQLException, BTSLBaseException;
	
	/**
	 * 
	 * @param con
	 * @param loginID
	 * @param requestVO
	 * @param responseVO
	 * @param responseSwag
	 * @return
	 * @throws SQLException
	 * @throws BTSLBaseException
	 */
	public void suspendResumeUserHierarchyListCA(Connection con, String loginID, SuspendResumeUserHierarchyRequestVO requestVO,  HttpServletResponse responseSwag) throws SQLException, BTSLBaseException;

	/**
	 * 
	 * @param con
	 * @param mcomCon
	 * @param response
	 * @param response1
	 * @param userList
	 * @param channelUserVO
	 * @param sessionUserVO
	 * @param requestVO
	 * @return
	 * @throws BTSLBaseException
	 * @throws SQLException
	 */
	public BaseResponse confirmChannelUserTransfer(Connection con,MComConnectionI mcomCon, BaseResponse response, HttpServletResponse response1,
			ArrayList userList, ChannelUserVO channelUserVO, UserVO sessionUserVO, ChannelAdminTransferVO requestVO) throws BTSLBaseException, SQLException;
	
	/**
	 * 
	 * @param headers
	 * @param domainType
	 * @param categoryType
	 * @param geoDomainType
	 * @param form
     * @param userVO	 
     * @param response
	 * @return
	 * @throws SQLException
	 * @throws BTSLBaseException
	 * @throws IOException
	 */
	public void downloadBulkModifyUsersList(MultiValueMap<String, String> headers, String domainType, String categoryType,
			String geoDomainType,BatchUserForm form, UserVO userVO, 
			FileDownloadResponseMulti response,Connection con) throws BTSLBaseException, SQLException, IOException;
	/**
	 *
	 * @param request
	 * @param reponse
	 * @param domainType
	 * @param categoryType
	 * @param geoDomainType
	 * @param locale
	 * @param inputValidations
	*/
	public ArrayList<MasterErrorList> basicFileValidations(BulkModifyUserRequestVO request, BulkModifyUserResponseVO response, String domainType, String categoryType, String geoDomainType, Locale locale, ArrayList<MasterErrorList> inputValidations);
	public  List<ChannelUserVO>  fetchChannelUsersByStatusForBarredfrdltReq(Connection con,BarredusersrequestVO requestVO)throws SQLException, BTSLBaseException;
	
	 public boolean approvalOrRejectBarredUser(Connection con,ApprovalBarredForDltRequestVO approvalBarredForDltRequestVO,OAuthUserData oauthUserData)throws SQLException, BTSLBaseException;

	public boolean uploadAndValidateModifyBulkUserFile(Connection con,MComConnectionI mcomCon, ChannelUserVO userVO, BulkModifyUserRequestVO request, BulkModifyUserResponseVO response, ArrayList fileErrorList) throws BTSLBaseException, SQLException;
	public boolean processUploadedModifyBulkUserFile(Connection con,MComConnectionI mcomCon, ChannelUserVO userVO, String categoryType, String geoDomainType, BulkModifyUserRequestVO request, BulkModifyUserResponseVO response, HttpServletResponse responseSwag, ArrayList fileErrorList, int emptyRowCount) throws BTSLBaseException, SQLException,FileNotFoundException, IOException;
}

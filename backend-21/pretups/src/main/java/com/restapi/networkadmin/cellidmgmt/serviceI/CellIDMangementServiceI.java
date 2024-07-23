package com.restapi.networkadmin.cellidmgmt.serviceI;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.channelAdmin.restrictedlistmgmt.requestVO.UploadFileRequestVO;
import com.restapi.networkadmin.cellidmgmt.requestVO.AddCellIdMgmtRequestVO;
import com.restapi.networkadmin.cellidmgmt.requestVO.ModifyCellIdMgmtRequestVO;
import com.restapi.networkadmin.cellidmgmt.responseVO.CellGroupManagementListResponseVO;
import com.restapi.user.service.FileDownloadResponse;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Locale;

@Service
public interface CellIDMangementServiceI {
    /**
     * @param con
     * @param userVO
     * @param locale
     * @return
     */
    public CellGroupManagementListResponseVO getCellGroupList(Connection con, UserVO userVO, Locale locale) throws BTSLBaseException;

    /**
     * @param con
     * @param userVO
     * @param locale
     * @param requestVO
     * @return
     */
    BaseResponse addCellGroupId(Connection con, UserVO userVO, Locale locale, AddCellIdMgmtRequestVO requestVO) throws BTSLBaseException, SQLException;

    /**
     *
     * @param con
     * @param userVO
     * @param locale
     * @param requestVO
     * @return
     */
    BaseResponse modifyCellGroupId(Connection con, UserVO userVO, Locale locale, ModifyCellIdMgmtRequestVO requestVO) throws Throwable;

    /**
     *
     * @param con
     * @param userVO
     * @param locale
     * @param groupId
     * @return
     */
    BaseResponse deleteCellGroupId(Connection con, UserVO userVO, Locale locale, String groupId) throws Throwable;


    /**
     *
     * @param con
     * @param userVO
     * @param locale
     * @return
     */
    FileDownloadResponse getCellIdAssociateTemplate(Connection con, UserVO userVO, Locale locale) throws Throwable;

    /**
     * @param con
     * @param userVO
     * @param locale
     * @param requestVO
     * @return
     */
    FileDownloadResponse associateCellGroupID(Connection con, UserVO userVO, Locale locale, UploadFileRequestVO requestVO) throws Throwable;

    /**
     *
     * @param con
     * @param userVO
     * @param locale
     * @return
     */
    FileDownloadResponse getCellIdReassociateTemplate(Connection con, UserVO userVO, Locale locale) throws Throwable;

    /**
     *
     * @param con
     * @param userVO
     * @param locale
     * @param requestVO
     * @return
     */
    FileDownloadResponse reassociateCellGroupID(Connection con, UserVO userVO, Locale locale, UploadFileRequestVO requestVO) throws Throwable;
}

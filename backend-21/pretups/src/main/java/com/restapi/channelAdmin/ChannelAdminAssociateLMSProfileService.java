package com.restapi.channelAdmin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;

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
import com.btsl.pretups.common.PretupsI;
import com.btsl.pretups.processes.businesslogic.ProcessBL;
import com.btsl.pretups.processes.businesslogic.ProcessI;
import com.btsl.pretups.processes.businesslogic.ProcessStatusDAO;
import com.btsl.pretups.processes.businesslogic.ProcessStatusVO;
import com.btsl.user.businesslogic.UserVO;
import com.btsl.util.BTSLUtil;
import com.btsl.util.Constants;
import com.restapi.c2sservices.service.ReadGenericFileUtil;
import com.restapi.channelAdmin.requestVO.BulkUserUploadRequestVO;
import com.restapi.channelAdmin.responseVO.BulkUserUploadResponseVO;
import com.restapi.channelAdmin.service.ChannelAdminService;
import com.restapi.networkadmin.messagemanagement.requestVO.MessageUploadRequestVO;
import com.restapi.networkadmin.messagemanagement.responseVO.MessagesBulkResponseVO;
import com.restapi.superadmin.networkmanagement.responseVO.NetworkListResponseVO;

import jakarta.servlet.http.HttpServletResponse;

public interface ChannelAdminAssociateLMSProfileService {

	public static final Log log = LogFactory.getLog(ChannelAdminService.class.getName());
	
	/**
     * @param con
     * @param loginId
     * @param responseSwag
     * @return
     * @throws BTSLBaseException
     * @throws SQLException
     */


    public ProfileListResponseVO viewProfileList(Connection con, String loginId, HttpServletResponse responseSwag)
    		throws BTSLBaseException, SQLException;
    
    /**
     * @param con
     * @param categoryCode
     * @param loginId
     * @param responseSwag
     * @return
     * @throws Exception
     * @throws SQLException
     */
    public FileAssocationResponseVO downloadFileAssocation(Connection con,String categoryCode,String domainCode,String gradeCode,String geographyCode, Locale locale, String loginID, HttpServletResponse responseSwagger) throws Exception;
    
    /**
     * @param con
     * @param locale
     * @param loginId
     * @param responseSwag
     * @return
     * @throws Exception
     * @throws SQLException
     */
    public FileAssocationResponseVO uploadFileAssocation(Connection con, Locale locale, String loginID, HttpServletResponse responseSwagger, FileAssociationUploadRequestVO request,String setID,String categoryCode,String geographyCode,String gradeCode) throws Exception;
    
    /**
     * @param con
     * @param locale
     * @param loginId
     * @param responseSwag
     * @return
     * @throws Exception
     * @throws SQLException
     */
    public FileAssocationResponseVO addAssociatePromotions(Connection con, Locale locale, String loginID, HttpServletResponse responseSwagger, FileAssociationUploadRequestVO request,String setID,String domainCode,String categoryCode,String geographyCode,String gradeCode) throws Exception;

}

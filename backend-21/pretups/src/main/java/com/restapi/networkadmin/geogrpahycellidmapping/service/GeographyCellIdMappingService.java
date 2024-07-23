package com.restapi.networkadmin.geogrpahycellidmapping.service;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnection;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.geogrpahycellidmapping.requestVO.GegraphicalCellIdFileRequestVO;
import com.restapi.networkadmin.geogrpahycellidmapping.responseVO.DownloadTemplateGeographyCellIdMappingRespVO;
import com.restapi.networkadmin.geogrpahycellidmapping.responseVO.UploadFileToAssociateCellIdResponseVO;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.Connection;
import java.util.Locale;

@Service
public interface GeographyCellIdMappingService {


    DownloadTemplateGeographyCellIdMappingRespVO downloadTemplateToGeographyCellIdMapping(Connection con, UserVO userVO, Locale locale) throws BTSLBaseException, IOException;

    public UploadFileToAssociateCellIdResponseVO uploadFileToAssociatecellId(Connection con, MComConnection mcomCon, UserVO userVO, GegraphicalCellIdFileRequestVO requestVO, UploadFileToAssociateCellIdResponseVO responseVO,Locale locale) throws BTSLBaseException, Exception;

}
package com.restapi.networkadmin.cardgroup.serviceI;

import java.sql.Connection;
import java.util.Date;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.db.util.MComConnectionI;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.cardgroup.requestVO.C2SAddCardGroupSaveRequestVO;
import com.restapi.networkadmin.cardgroup.requestVO.CardGroupCalculateC2STransferValueRequestVO;
import com.restapi.networkadmin.cardgroup.requestVO.CardGroupDetailsRequestVO;
import com.restapi.networkadmin.cardgroup.requestVO.DefaultCardGroupRequestVO;
import com.restapi.networkadmin.cardgroup.responseVO.AddTempCardGroupListResponseVO;
import com.restapi.networkadmin.cardgroup.responseVO.C2SAddCardGroupSaveResponseVO;
import com.restapi.networkadmin.cardgroup.responseVO.C2SCardGroupSetNameListResponseVO;
import com.restapi.networkadmin.cardgroup.responseVO.C2SCardGroupVersionNumbersListResponseVO;
import com.restapi.networkadmin.cardgroup.responseVO.CardGroupCalculateC2STransferValueResponseVO;
import com.restapi.networkadmin.cardgroup.responseVO.ChangeDefaultCardGroupResponseVO;
import com.restapi.networkadmin.cardgroup.responseVO.LoadC2SCardGroupListResponseVO;
import com.restapi.networkadmin.cardgroup.responseVO.LoadC2SCardGroupResponseVO;
import com.restapi.networkadmin.cardgroup.responseVO.LoadC2SCardGroupVersionListResponseVO;
import com.restapi.networkadmin.cardgroup.responseVO.LoadCardGroupTransferValuesResponseVO;
import com.restapi.networkadmin.cardgroup.responseVO.UpdateSaveC2SCardGroupResponseVO;
import com.restapi.networkadmin.cardgroup.responseVO.ViewC2SCardGroupResponseVO;

@Service
public interface C2SCardGroupServiceI {
	//add c2s CardGroup list methods
	public LoadC2SCardGroupResponseVO loadServiceAndSubServiceList(Connection con, String networkID)throws BTSLBaseException;
	public LoadC2SCardGroupListResponseVO loadC2SCardGroupList(Connection con, String networkID,  String subService)throws BTSLBaseException;
	public AddTempCardGroupListResponseVO addTempList(Connection con,HttpServletRequest request, UserVO userVO, CardGroupDetailsRequestVO requestVO) throws BTSLBaseException, Exception ;	
	public C2SAddCardGroupSaveResponseVO saveC2SCardGroup(Connection con, HttpServletRequest request,C2SAddCardGroupSaveResponseVO response, UserVO userVO, C2SAddCardGroupSaveRequestVO requestVO) throws Exception;
	
	public  C2SCardGroupSetNameListResponseVO loadC2SCardGroupSetNameList(Connection con, String networkID, String serviceType, String SubserviceType)throws BTSLBaseException, Exception;
	public LoadC2SCardGroupVersionListResponseVO loadVersionList(Connection con, String networkID,String service, String subService, String cardGroupSetType, Date dateTime ) throws Exception;
	public C2SCardGroupVersionNumbersListResponseVO loadVersionListBasedOnCardGroupSetIDAndDate(Connection con, String cardGroupSetID, Date dateTime ) throws Exception ;
	public ViewC2SCardGroupResponseVO viewC2SCardGroupDetails(Connection con, String networkID, String selectCardGroupSetId, String version)throws BTSLBaseException, Exception;
	
	public AddTempCardGroupListResponseVO addModifyC2SCardGroupTempList(Connection con,HttpServletRequest httpServletRequest, UserVO userVO, CardGroupDetailsRequestVO requestVO)throws Exception;
	
	public Integer deleteTempC2SCardGroupList(Connection con,MComConnectionI mcomCon, String selectCardGroupSetId, String version, String oldApplicableFromDate, String oldApplicableFromHour ) throws Exception;
	public UpdateSaveC2SCardGroupResponseVO modifySaveC2SCardGroup(Connection con, HttpServletRequest request, UserVO userVO, C2SAddCardGroupSaveRequestVO requestVO) throws BTSLBaseException, Exception ;
	public CardGroupCalculateC2STransferValueResponseVO getCardGroupTransferRuleValue(Connection con, UserVO userVO,CardGroupCalculateC2STransferValueResponseVO response, CardGroupCalculateC2STransferValueRequestVO requestVO) throws BTSLBaseException,Exception;
	public LoadCardGroupTransferValuesResponseVO loadCardGroupCalculateTransferRuleValueDropDown(Connection con, UserVO userVO) throws BTSLBaseException, Exception;	
	public ChangeDefaultCardGroupResponseVO changeDefaultCardGroup(Connection con, UserVO userVO, DefaultCardGroupRequestVO requestVO)throws BTSLBaseException,Exception;
	public String getCardGroupSetNameById(Connection con, String cardGroupSetId, String version) throws BTSLBaseException;
}

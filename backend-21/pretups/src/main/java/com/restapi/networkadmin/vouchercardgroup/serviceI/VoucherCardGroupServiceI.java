package com.restapi.networkadmin.vouchercardgroup.serviceI;

import java.sql.Connection;
import java.util.Date;
import java.util.List;

import com.restapi.networkadmin.vouchercardgroup.request.*;
import org.springframework.stereotype.Service;

import com.btsl.common.BTSLBaseException;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.requestVO.CardGroupStatusRequestVO;
import com.restapi.networkadmin.responseVO.C2SCardGroupStatusSaveResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.AddVoucherGroupDropDownResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.CalculateTransferValueResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.DefaultVoucherCardGroupResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.DenaminationDetailsDropdownsResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.LoadVoucherCardGroupServicesResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.SaveVoucherGroupResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.ViewVoucherCardGroupResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.VoucherCardGroupStatusResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.VoucherCardGroupVersionNumberListResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.VoucherCardGroupVersionResponseVO;
import com.restapi.networkadmin.vouchercardgroup.response.VoucherTransferValueResponseVO;

@Service
public interface VoucherCardGroupServiceI {
 public LoadVoucherCardGroupServicesResponseVO loadServiceAndSubServiceList(Connection con, String networkID)throws BTSLBaseException, Exception;
 public ViewVoucherCardGroupResponseVO viewVoucherCardGroupDetails(Connection con, UserVO userVO, String cardGroupSetId, String version) throws BTSLBaseException, Exception;
 public VoucherCardGroupVersionResponseVO loadVoucherCardGroupversionList(Connection con, UserVO userVO,String service, String subService, String cardGroupSetType, Date applicableFromDateAndTime) throws BTSLBaseException, Exception;
 public AddVoucherGroupDropDownResponseVO addVoucherGroupDropDown(Connection con, UserVO userVO,String cardGroupSubServiceID)throws BTSLBaseException, Exception;
 public DenaminationDetailsDropdownsResponseVO denominationDetailsList(Connection con,UserVO userVO)throws BTSLBaseException, Exception;
 public SaveVoucherGroupResponseVO saveVoucherGroup(Connection con,SaveVoucherGroupResponseVO response, UserVO userVO, VoucherGroupDetailsRequestVO request) throws BTSLBaseException, Exception;
 public SaveVoucherGroupResponseVO modifyVoucherGroup(Connection con,UserVO userVO, SaveVoucherGroupResponseVO response, ModifyVoucherCardGroupDetailsRequestVO request) throws BTSLBaseException, Exception;
 public DefaultVoucherCardGroupResponseVO changeDefaultVoucherCardGroup(Connection con, UserVO userVO, DefaultVoucherCardGroupRequestVO requestVO)throws BTSLBaseException, Exception;
 public VoucherCardGroupVersionNumberListResponseVO loadVersionListBasedOnCardGroupSetIDAndDate(Connection con, String cardGroupSetId, Date date )throws BTSLBaseException, Exception;
 public int deleteVoucherCardGroup(Connection con,UserVO userVO,String cardGroupSetId, String serviceTypeDesc,String subServiceDesc, String setType)throws BTSLBaseException, Exception;
 public VoucherCardGroupStatusResponseVO loadVoucherCardGroupStatusList(Connection con, UserVO userVO, List<CardGroupStatusRequestVO> requestVO)throws BTSLBaseException, Exception;
 public C2SCardGroupStatusSaveResponseVO changeVoucherCardGroupStatusList(Connection con, UserVO userVO, ChangeVoucherCardGroupStatusListRequestVO requestVO)throws BTSLBaseException, Exception;
 public VoucherTransferValueResponseVO viewVoucherTransferValue(Connection con, UserVO userVO, VoucherCardGroupTransferValueRequestVO request)throws BTSLBaseException, Exception;
 public CalculateTransferValueResponseVO calculateTransferValue(Connection con, UserVO userVO,CalculateTransferValueResponseVO response, VoucherGroupDetailsRequestVO requestVO)  throws BTSLBaseException, Exception;
}

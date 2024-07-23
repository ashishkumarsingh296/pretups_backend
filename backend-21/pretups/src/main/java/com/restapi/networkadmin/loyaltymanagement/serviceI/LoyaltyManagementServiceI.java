package com.restapi.networkadmin.loyaltymanagement.serviceI;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnectionI;
import com.btsl.user.businesslogic.UserVO;
import com.restapi.networkadmin.loyaltymanagement.requestVO.*;
import com.restapi.networkadmin.loyaltymanagement.responseVO.*;
import org.springframework.stereotype.Service;

import java.sql.Connection;

@Service
public interface LoyaltyManagementServiceI {
    public ProfileDetailsVersionsResponseVO loadProfileDetailsVersionsList(Connection con, UserVO userVO, String promotionType, String status, String applicableFrom, String applicableTo, ProfileDetailsVersionsResponseVO response) throws BTSLBaseException, Exception;

    public VersionsResponseVO loadVersionsList(Connection con, UserVO userVO, String setID, String validUpToDate, VersionsResponseVO response) throws BTSLBaseException, Exception;

    public ProfileDetailsVO loadProfileDetails(Connection con, UserVO userVO, String setID, String version, ProfileDetailsVO response) throws BTSLBaseException, Exception;

    public ModulesAndServiceResponseVO loadModulesAndServices(Connection con, UserVO userVO, ModulesAndServiceResponseVO response) throws BTSLBaseException, Exception;

    public BaseResponse addProfileDetails(Connection con, UserVO userVO, AddProfileDetailsRequestVO requestVO, BaseResponse response) throws BTSLBaseException, Exception;

    public BaseResponse modifyProfileDetails(Connection con, UserVO userVO, ModifyProfileDetailsRequestVO requestVO, BaseResponse response) throws BTSLBaseException, Exception;

    public BaseResponse deleteLmsProfile(Connection con, MComConnectionI mcomCon, UserVO userVO, DeleteLmsProfileRequestVO requestVO, BaseResponse response) throws BTSLBaseException, Exception;
    public BaseResponse suspendProfileDetails(Connection con, UserVO userVO, SuspendRequestVO requestVO, BaseResponse baseResponse) throws BTSLBaseException, Exception;
    public BaseResponse approveProfileDetails(Connection con, UserVO userVO, ApproveProfileRequestVO requestVO, BaseResponse baseResponse) throws BTSLBaseException, Exception;
    public ProfileMessageDetailsVO getProfileMessageDetails(Connection con, UserVO userVO, SuspendRequestVO requestVO, ProfileMessageDetailsVO response) throws BTSLBaseException, Exception;
    public BaseResponse updateMessageDetails(Connection con, UserVO userVO, UpdateMessageProfileRequestVO requestVO, BaseResponse response) throws BTSLBaseException, Exception;
    public BaseResponse resumePofileDetails(Connection con, UserVO userVO, SuspendRequestVO requestVO, BaseResponse response) throws BTSLBaseException, Exception;
    public ApproveProfilesAndVersionsResponseVO loadApprovePofilesAndVersionsDetails(Connection con, UserVO userVO, ApproveProfilesAndVersionsResponseVO response) throws BTSLBaseException, Exception;
    public ProfileDetailsVersionsResponseVO loadApprovePofilesDetails(Connection con, UserVO userVO, SuspendRequestVO requestVO, ProfileDetailsVersionsResponseVO response) throws BTSLBaseException, Exception;

}

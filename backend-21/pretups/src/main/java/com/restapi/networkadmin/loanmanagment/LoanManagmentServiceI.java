package com.restapi.networkadmin.loanmanagment;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnectionI;
import com.btsl.pretups.channel.profile.businesslogic.LoanProfileCombinedVO;
import com.btsl.user.businesslogic.UserVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.restapi.networkadmin.loanmanagment.LoanListResponseVO;
import com.restapi.networkadmin.loanmanagment.LoanProductListResponseVO;
import com.restapi.networkadmin.loanmanagment.ModifyLoanProfileRequestVO;
import com.restapi.networkadmin.loanmanagment.AddLoanProfileRequestVO;
@Service
public interface LoanManagmentServiceI {

        /**
         * author : Arpita Kashyap
     * @param con
     * @param networkName
     * @param categoryCode
     * @param responseSwag
     * @return
     * @throws BTSLBaseException
     * @throws SQLException
     */


    public LoanListResponseVO loadLoanProfileList(Connection con, String networkName,String catgeoryCode,String domainCode, HttpServletResponse responseSwag)
            throws BTSLBaseException, SQLException;

    public LoanListResponseVO viewLoanProfileByID(Connection con, String profileID, HttpServletResponse responseSwag)
            throws BTSLBaseException, SQLException;
    
    public LoanListResponseVO deleteLoanProfileByID(Connection con, String profileID, HttpServletResponse responseSwag)
            throws BTSLBaseException, SQLException;
    public LoanProductListResponseVO viewProductList(Connection con, String loginID, HttpServletResponse response1) 
    		throws BTSLBaseException, SQLException;
	public BaseResponse modifyLoanProfileDetail(MultiValueMap<String, String> headers,HttpServletRequest httpServletRequest, HttpServletResponse response1, Connection con,
			MComConnectionI mcomCon, Locale locale, UserVO userVO, BaseResponse response,ModifyLoanProfileRequestVO requestVO) throws Exception ;
    public BaseResponse addLoanProfile(MultiValueMap<String, String> headers,HttpServletRequest httpServletRequest, HttpServletResponse response1,
	Connection con,MComConnectionI mcomCon, Locale locale, UserVO userVO, BaseResponse response,AddLoanProfileRequestVO addLoanProfileRequestVO)  throws Exception;
}

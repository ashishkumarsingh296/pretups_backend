package com.restapi.networkadmin.networkStock;

import com.btsl.common.BTSLBaseException;
import com.btsl.common.BaseResponse;
import com.btsl.db.util.MComConnectionI;
import org.springframework.util.MultiValueMap;

import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;

public interface NetworkStockServiceI {
    ViewCurrentStockResponseVO getList(MultiValueMap<String, String> headers, HttpServletResponse response1, Connection con, MComConnectionI mcomCon, String loginID) throws Exception, BTSLBaseException;

    BaseResponse confirmStockAuthorise(Connection con, MComConnectionI mcomCon, String categoryType, String loginID, HttpServletResponse response1, MultiValueMap<String, String> headers, NetworkInitiateStockDeductionRequestVO requestVO) throws BTSLBaseException, Exception;

    NetworkStockTxnVO1 initiateStockDeduction(Connection con, MComConnectionI mcomCon, String categoryType, String loginID, HttpServletResponse response1, MultiValueMap<String, String> headers, NetworkInitiateStockDeductionRequestVO requestVO) throws BTSLBaseException, Exception;

    NetworkStockInitiateDeductionResponseVO getStockAuthorise(Connection con, MComConnectionI mcomCon, String categoryType, String loginID, HttpServletResponse response1, MultiValueMap<String, String> headers, String walletType) throws BTSLBaseException, Exception;

    public ApprovalStockResponseVO getStockDeductionTransactionList(Connection con, String loginId, HttpServletResponse httpServeletResponse, MultiValueMap<String, String> headers)
            throws BTSLBaseException, Exception;

    public ApprovalStockDetailsResponseVO getStockDeductionTransactionDetails(Connection con, String loginId, HttpServletResponse httpServeletResponse, MultiValueMap<String, String> headers, String transactionNo)
            throws BTSLBaseException, Exception;

    public BaseResponse approve(ApproveStockDeductionRequestVO requestVO, HttpServletResponse response, MultiValueMap<String, String> headers,String loginID) throws BTSLBaseException, Exception;

    public BaseResponse reject(RejectStockDeductionRequestVO requestVO, HttpServletResponse response, MultiValueMap<String, String> headers,String loginID) throws BTSLBaseException, Exception;

}


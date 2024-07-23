package com.restapi.networkadmin.responseVO;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RejectStockTxnResponseVO extends BaseResponse{
	private String TxnNo; 
}

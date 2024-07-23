package com.restapi.networkadmin.vouchercardgroup.response;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class AddVoucherGroupDropDownResponseVO extends BaseResponse{
 private ArrayList amountTypeList;
 private ArrayList validityTypeList;
 private ArrayList bonusBundleList;
 private ArrayList tempAccList;
}

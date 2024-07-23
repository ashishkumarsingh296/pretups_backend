package com.restapi.networkadmin.loyaltymanagement.responseVO;

import com.btsl.common.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModulesAndServiceResponseVO extends BaseResponse {
 private List modulesList;
 private List servicesList;
 private List productList;
 private List subscriberList;
 private List periodTypeList;
 private List targetTypeList;
 private List pointTypeList;


}

package com.restapi.networkadmin.networkproductmap.responseVO;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.product.businesslogic.NetworkProductVO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Map;

@Getter
@Setter
public class NetworkProductMappingResponseVO extends BaseResponse {

    private ArrayList<NetworkProductVO> networkProductList = new ArrayList<>();

    private ArrayList usageList = new ArrayList();

    private Map dataListStatusOld;

}

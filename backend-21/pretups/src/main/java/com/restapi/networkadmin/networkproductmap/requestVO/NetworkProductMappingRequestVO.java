package com.restapi.networkadmin.networkproductmap.requestVO;

import com.btsl.pretups.product.businesslogic.NetworkProductVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Map;

@Getter
@Setter
@ToString
public class NetworkProductMappingRequestVO {
    private ArrayList<NetworkProductVO> networkProductList;
//    private String[] dataListStatusOld;
    private Map dataListStatusOld;
}

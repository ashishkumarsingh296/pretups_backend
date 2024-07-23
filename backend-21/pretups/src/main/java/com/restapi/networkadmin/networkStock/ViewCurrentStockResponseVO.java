package com.restapi.networkadmin.networkStock;

import com.btsl.common.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ViewCurrentStockResponseVO extends BaseResponse {

    private ArrayList stockList;
}

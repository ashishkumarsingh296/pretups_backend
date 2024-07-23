package com.restapi.o2c.service;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.common.BaseResponseMultiple;

public class O2CProductsResponseVO extends BaseResponseMultiple{

	 ArrayList<O2CProductResponseData> productsList;

		public ArrayList<O2CProductResponseData> getProductsList() {
			return productsList;
		}

		public void setProductsList(ArrayList<O2CProductResponseData> productsList) {
			this.productsList = productsList;
		}

		@Override
		public String toString() {
			return "O2CProductsResponseVO [productsList=" + productsList + "]";
		}
}

package com.restapi.channelAdmin.responseVO;

import java.util.ArrayList;
import java.util.List;

import com.btsl.common.BaseResponseMultiple;
import com.btsl.common.ListValueVO;

public class OwnerListAndCUListO2cTxnRevResponseVO extends BaseResponseMultiple{

	List<ListValueVO> list = new ArrayList<>();
	int size;
	
	public List<ListValueVO> getList() {
		return list;
	}
	public void setList(List<ListValueVO> list) {
		this.list = list;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
	
}

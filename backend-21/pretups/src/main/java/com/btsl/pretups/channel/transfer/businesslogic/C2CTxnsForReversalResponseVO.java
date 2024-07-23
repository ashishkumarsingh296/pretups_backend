package com.btsl.pretups.channel.transfer.businesslogic;

import java.util.ArrayList;

import com.btsl.common.BaseResponseMultiple;

public class C2CTxnsForReversalResponseVO extends BaseResponseMultiple{

	private ArrayList c2cReverseTxnList;

	public ArrayList getC2cReverseTxnList() {
		return c2cReverseTxnList;
	}

	public void setC2cReverseTxnList(ArrayList c2cReverseTxnList) {
		this.c2cReverseTxnList = c2cReverseTxnList;
	}

	@Override
	public String toString() {
		return "C2CTxnsForReversalResponseVO [c2cReverseTxnList=" + c2cReverseTxnList + "]";
	}
	
	
	
}

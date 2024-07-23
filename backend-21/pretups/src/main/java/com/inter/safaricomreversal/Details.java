package com.inter.safaricomreversal;

import java.io.Serializable;

public class Details  implements Serializable{

	
private static final long serialVersionUID = 1L;
long AdjustmentAmount;

public Details(long adjustmentAmount) {
		super();
		AdjustmentAmount = adjustmentAmount;
	}

@Override
public String toString() {
	return "Details [AdjustmentAmount=" + AdjustmentAmount + "]";
}

public void setAdjustmentAmount(long adjustmentAmount) {
	AdjustmentAmount = adjustmentAmount;
}

public long getAdjustmentAmount() {
	return AdjustmentAmount;
}
	

}

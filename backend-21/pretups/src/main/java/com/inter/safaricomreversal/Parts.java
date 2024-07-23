package com.inter.safaricomreversal;

import java.io.Serializable;

public class Parts  implements Serializable{

	AdjustmentSpecification AdjustmentSpecification;

	private static final long serialVersionUID = 1L;

	
	@Override
	public String toString() {
		return "Parts [AdjustmentSpecification=" + AdjustmentSpecification + "]";
	}

	public Parts(AdjustmentSpecification AdjustmentSpecification) {
		super();
		this.AdjustmentSpecification = AdjustmentSpecification;
	}

	public AdjustmentSpecification getAdjustmentSpecification() {
		return AdjustmentSpecification;
	}

	public void setAdjustmentSpecification(AdjustmentSpecification AdjustmentSpecification) {
		this.AdjustmentSpecification = AdjustmentSpecification;
	}

}

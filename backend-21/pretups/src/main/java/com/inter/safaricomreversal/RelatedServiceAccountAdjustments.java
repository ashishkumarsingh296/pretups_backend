package com.inter.safaricomreversal;

import java.io.Serializable;
import java.util.Arrays;

public class RelatedServiceAccountAdjustments implements Serializable{
	

	private static final long serialVersionUID = 1L;

	RelatedServiceAccountAdjustment[] RelatedServiceAccountAdjustment;

	@Override
	public String toString() {
		return "RelatedServiceAccountAdjustments [RelatedServiceAccountAdjustment="
				+ Arrays.toString(RelatedServiceAccountAdjustment) + "]";
	}

	public RelatedServiceAccountAdjustments(RelatedServiceAccountAdjustment[] relatedServiceAccountAdjustment) {
		super();
		this.RelatedServiceAccountAdjustment = relatedServiceAccountAdjustment;
	}

	public RelatedServiceAccountAdjustment[] getRelatedServiceAccountAdjustment() {
		return RelatedServiceAccountAdjustment;
	}

	public void setRelatedServiceAccountAdjustment(RelatedServiceAccountAdjustment[] relatedServiceAccountAdjustments) {
		this.RelatedServiceAccountAdjustment = relatedServiceAccountAdjustments;
	}
	
}

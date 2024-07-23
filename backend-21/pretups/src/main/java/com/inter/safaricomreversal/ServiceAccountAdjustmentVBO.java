package com.inter.safaricomreversal;

import java.io.Serializable;

public class ServiceAccountAdjustmentVBO  implements Serializable{
	
	private static final long serialVersionUID = 1L;
	IDs IDs;
	ValidityPeriod ValidityPeriod;
	Details Details;
	Parts Parts;
	RelatedServiceAccountAdjustments RelatedServiceAccountAdjustments;

	@Override
	public String toString() {
		return "ServiceAccountAdjustmentVBO [IDs=" + IDs + ", ValidityPeriod=" + ValidityPeriod + ", Details=" + Details
				+ ", Parts=" + Parts + ", RelatedServiceAccountAdjustments=" + RelatedServiceAccountAdjustments + "]";
	}


	public ServiceAccountAdjustmentVBO(IDs IDs, ValidityPeriod validityPeriod, Details details, Parts parts,
			RelatedServiceAccountAdjustments relatedServiceAccountAdjustments) {
		super();
		this.IDs = IDs;
		this.ValidityPeriod = validityPeriod;
		this.Details = details;
		this.Parts = parts;
		this.RelatedServiceAccountAdjustments = relatedServiceAccountAdjustments;
	}


	public IDs getIDS() {
		return IDs;
	}

	public void setIDS(IDs IDs) {
		this.IDs = IDs;
	}

	public ValidityPeriod getValidityPeriod() {
		return ValidityPeriod;
	}

	public void setValidityPeriod(ValidityPeriod validityPeriod) {
		this.ValidityPeriod = validityPeriod;
	}

	public Details getDetails() {
		return Details;
	}

	public void setDetails(Details details) {
		this.Details = details;
	}

	public Parts getParts() {
		return Parts;
	}

	public void setParts(Parts parts) {
		this.Parts = parts;
	}

	public RelatedServiceAccountAdjustments getRelatedServiceAccountAdjustments() {
		return RelatedServiceAccountAdjustments;
	}

	public void setRelatedServiceAccountAdjustments(RelatedServiceAccountAdjustments relatedServiceAccountAdjustments) {
		this.RelatedServiceAccountAdjustments = relatedServiceAccountAdjustments;
	}



}

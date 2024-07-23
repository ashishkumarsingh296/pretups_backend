package com.btsl.pretups.jigsaw.tcp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotNull;


public class TransferProfileWrapperDTO {

	@NotNull
	@io.swagger.v3.oas.annotations.media.Schema(description = "Resume Service Request Id", example = "037abfef-767e-4543-beff-9bf7519cb1ea", required = true/*, position = 0*/)
	private String resumeServiceRequestId;
	
	String finddata;


	TransferProfileDTO childTransferProfile;
	TransferProfileDTO parentTransferProfile;

	TransferProfileProductDTO childTransferProfileProduct;
	TransferProfileProductDTO parentTransferProfileProduct;

	public TransferProfileDTO getChildTransferProfile() {
		return childTransferProfile;
	}

	public void setChildTransferProfile(TransferProfileDTO childTransferProfile) {
		this.childTransferProfile = childTransferProfile;
	}

	public TransferProfileDTO getParentTransferProfile() {
		return parentTransferProfile;
	}

	public void setParentTransferProfile(TransferProfileDTO parentTransferProfile) {
		this.parentTransferProfile = parentTransferProfile;
	}

	public TransferProfileProductDTO getChildTransferProfileProduct() {
		return childTransferProfileProduct;
	}

	public void setChildTransferProfileProduct(TransferProfileProductDTO childTransferProfileProduct) {
		this.childTransferProfileProduct = childTransferProfileProduct;
	}

	public TransferProfileProductDTO getParentTransferProfileProduct() {
		return parentTransferProfileProduct;
	}

	public void setParentTransferProfileProduct(TransferProfileProductDTO parentTransferProfileProduct) {
		this.parentTransferProfileProduct = parentTransferProfileProduct;
	}

	public String getResumeServiceRequestId() {
		return resumeServiceRequestId;
	}

	public void setResumeServiceRequestId(String resumeServiceRequestId) {
		this.resumeServiceRequestId = resumeServiceRequestId;
	}
	
	public String getFinddata() {
		return finddata;
	}

	public void setFinddata(String finddata) {
		this.finddata = finddata;
	}

}

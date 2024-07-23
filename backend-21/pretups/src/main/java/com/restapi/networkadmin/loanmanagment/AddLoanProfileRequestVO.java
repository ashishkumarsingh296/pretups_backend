package com.restapi.networkadmin.loanmanagment;

import java.util.ArrayList;

import com.btsl.common.BaseResponse;
import com.btsl.pretups.channel.profile.businesslogic.LoanProfileDetailsVO;
import com.restapi.networkadmin.loanmanagment.LoanProfileDetailsNewVO;




public class AddLoanProfileRequestVO{

     	private String profileName;
		private String profileType;
		private String profileID;
		private String productCode;
		private String categoryCode;
		private String networkCode;
		private LoanProfileDetailsNewVO loanProfileDetailsList; //LoanProfileDetailsVO
		public LoanProfileDetailsNewVO getLoanProfileDetailsList() {
			return loanProfileDetailsList;
		}

		public void setLoanProfileDetailsList(LoanProfileDetailsNewVO loanProfileDetailsList) {
			this.loanProfileDetailsList = loanProfileDetailsList;
		}

		public String getNetworkCode() {
			return networkCode;
		}

		public void setNetworkCode(String networkCode) {
			this.networkCode = networkCode;
		}

		public String getProfileType() {
			return profileType;
		}

		public void setProfileType(String profileType) {
			this.profileType = profileType;
		}

		public String getProfileID() {
			return profileID;
		}

		public void setProfileID(String profileID) {
			this.profileID = profileID;
		}

		public String getProductCode() {
			return productCode;
		}

		public void setProductCode(String productCode) {
			this.productCode = productCode;
		}

		public String getCategoryCode() {
			return categoryCode;
		}

		public void setCategoryCode(String categoryCode) {
			this.categoryCode = categoryCode;
		}

		public void setProfileName(String profileName) {
			this.profileName = profileName;
		}
		
		public String getProfileName() {
			return profileName;
		}

}

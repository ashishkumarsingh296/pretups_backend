package com.btsl.pretups.channel.profile.businesslogic;

import com.fasterxml.jackson.annotation.JsonProperty;



public class UserPinChangeReqVO {
	
	
		@JsonProperty("remarks")
		String remarks;
		
		@JsonProperty("msisdn")
		String msisdn;
		
		@JsonProperty("oldPin")
		String oldPin;
		
		@JsonProperty("newPin")
		String newPin;
		
		@JsonProperty("newPin2")
		String newPin2;

		@JsonProperty("remarks")
		@io.swagger.v3.oas.annotations.media.Schema(example = "Remarks for API", required = true/* , defaultValue = "" */,description="Remarks")
		public String getRemarks() {
			return remarks;
		}

		public void setRemarks(String remarks) {
			this.remarks = remarks;
		}

		@JsonProperty("msisdn")
		@io.swagger.v3.oas.annotations.media.Schema(example = "720651561", required = true/* , defaultValue = "" */, description="Msisdn")
		public String getMsisdn() {
			return msisdn;
		}

		public void setMsisdn(String msisdn) {
			this.msisdn = msisdn;
		}

		
		@JsonProperty("oldPin")
		@io.swagger.v3.oas.annotations.media.Schema(example = "720651561", required = true/* , defaultValue = "" */, description="Old pin of msisdn")
		public String getOldPin() {
			return oldPin;
		}

		public void setOldPin(String oldPin) {
			this.oldPin = oldPin;
		}

		@JsonProperty("newPin")
		@io.swagger.v3.oas.annotations.media.Schema(example = "720651561", required = true/* , defaultValue = "" */, description="New pin of msisdn")
		public String getNewPin() {
			return newPin;
		}

		public void setNewPin(String newPin) {
			this.newPin = newPin;
		}

		@JsonProperty("newPin2")
		@io.swagger.v3.oas.annotations.media.Schema(example = "720651561", required = true/* , defaultValue = "" */, description="Confirm new pin of msisdn")
		public String getNewPin2() {
			return newPin2;
		}

		public void setNewPin2(String newPin2) {
			this.newPin2 = newPin2;
		}

		@Override
		public String toString() {
			return "UserPinChangeReqVO [remarks=" + remarks + ", msisdn=" + msisdn + ", oldPin=" + oldPin + ", newPin="
					+ newPin + ", newPin2=" + newPin2 + "]";
		}

		

}

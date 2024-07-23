package com.btsl.pretups.channel.profile.businesslogic;

public class CardGroupSetVO {

	private String cardGroupSetName;
	
	private String serviceTypeDesc;
	
	private String subServiceTypeDescription;
	
	private String modifiedBy;
	
	private String language1Message;
	
	private String language2Message;
	
	private String status;
	
	public String getCardGroupSetName() {
		return cardGroupSetName;
	}

	public void setCardGroupSetName(String cardGroupSetName) {
		this.cardGroupSetName = cardGroupSetName;
	}

	public String getServiceTypeDesc() {
		return serviceTypeDesc;
	}

	public void setServiceTypeDesc(String serviceTypeDesc) {
		this.serviceTypeDesc = serviceTypeDesc;
	}

	public String getSubServiceTypeDescription() {
		return subServiceTypeDescription;
	}

	public void setSubServiceTypeDescription(String subServiceTypeDescription) {
		this.subServiceTypeDescription = subServiceTypeDescription;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getLanguage1Message() {
		return language1Message;
	}

	public void setLanguage1Message(String language1Message) {
		this.language1Message = language1Message;
	}

	public String getLanguage2Message() {
		return language2Message;
	}

	public void setLanguage2Message(String language2Message) {
		this.language2Message = language2Message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
	    StringBuilder sb = new StringBuilder();
	    sb.append("class UserPasswordManagementVO {\n");
	    sb.append("    cardGroupSetName: ").append(cardGroupSetName).append("\n");
	    sb.append("    serviceTypeDesc: ").append(serviceTypeDesc).append("\n");
	    sb.append("    subServiceTypeDescription: ").append(subServiceTypeDescription).append("\n");
	    sb.append("    modifiedBy: ").append(modifiedBy).append("\n");
	    sb.append("    language1Message: ").append(language1Message).append("\n");
	    sb.append("    language2Message: ").append(language2Message).append("\n");
	    sb.append("    status: ").append(status);

	    return sb.toString();
	 }
	
}

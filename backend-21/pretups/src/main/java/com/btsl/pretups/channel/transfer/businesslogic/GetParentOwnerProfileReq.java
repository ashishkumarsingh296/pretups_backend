
package com.btsl.pretups.channel.transfer.businesslogic;

public class GetParentOwnerProfileReq extends BaseRequestdata {
   
	private String userId;
	private String categoryCode;
	private String domainCode;
	private String geography;
	

	public String getCategoryCode() {
		return categoryCode;
	}


	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}


	public String getDomainCode() {
		return domainCode;
	}


	public void setDomainCode(String domainCode) {
		this.domainCode = domainCode;
	}


	public String getGeography() {
		return geography;
	}


	public void setGeography(String geography) {
		this.geography = geography;
	}


		@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("UserNameAutoSearchReqDTO [ ");
		
		sb.append("categoryCode: ");
		sb.append( categoryCode ).append(" ,");
		sb.append(",domain: ");
		sb.append( domainCode).append(" ,");
		sb.append(",geography: ");
		sb.append( geography).append(" ,");
		sb.append(",userID: ");
		sb.append( userId).append(" ,");
	
		sb.append(" ]");
		return sb.toString();
	}


		public String getUserId() {
			return userId;
		}


		public void setUserId(String userId) {
			this.userId = userId;
		}	

}

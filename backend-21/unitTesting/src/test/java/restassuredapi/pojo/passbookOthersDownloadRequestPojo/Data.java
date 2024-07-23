package restassuredapi.pojo.passbookOthersDownloadRequestPojo;

import java.util.List;

public class Data {

	private String category;
	private List<DisplayHeaderColumnList> dispHeaderColumnList;
	private String domain;
	private String fileType;
	private String fromDate;
	private String geography;
	private String product;
	private String toDate;
	private String user;
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public List<DisplayHeaderColumnList> getDispHeaderColumnList() {
		return dispHeaderColumnList;
	}
	public void setDispHeaderColumnList(List<DisplayHeaderColumnList> dispHeaderColumnList) {
		this.dispHeaderColumnList = dispHeaderColumnList;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public String getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}
	public String getGeography() {
		return geography;
	}
	public void setGeography(String geography) {
		this.geography = geography;
	}
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	public String getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = toDate;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	
}

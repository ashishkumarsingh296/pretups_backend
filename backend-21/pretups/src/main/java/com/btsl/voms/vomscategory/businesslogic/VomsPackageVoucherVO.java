package com.btsl.voms.vomscategory.businesslogic;

public class VomsPackageVoucherVO {//details of each voucher in a bundle
	private long bundleID;
	private String bundleName;
	private String productID;
	private String productName;
	private String productType;
	private int quantity;
	private double price;//product mrp
	private long bundleRetailPrice;
	private long bundleLastSequence;
	private String bundlePrefix;
	private long bundleCount;
	private String transferID;
	

	
	@Override
	public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VomsPackageVoucherVO [bundleID=" + bundleID);
        sb.append(", bundleName=" + bundleName);
        sb.append(", productID=" + productID);
        sb.append(", productName=" + productName); 
        sb.append(", productType=" + productType);
        sb.append(", quantity=" + quantity);
        sb.append(", price=" + price);
        sb.append(", bundleRetailPrice=" + bundleRetailPrice);
        sb.append(", bundleLastSequence=" + bundleLastSequence);
        sb.append(", bundlePrefix=" + bundlePrefix);
        sb.append(", bundleCount=" + bundleCount);
        sb.append(", transferID=" + transferID + "]");        
        return sb.toString();         
  }
	public long getBundleID() {
		return bundleID;
	}
	public void setBundleID(long bundleID) {
		this.bundleID = bundleID;
	}
	public String getBundleName() {
		return bundleName;
	}
	public void setBundleName(String bundleName) {
		this.bundleName = bundleName;
	}
	public String getProductID() {
		return productID;
	}
	public void setProductID(String productID) {
		this.productID = productID;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public long getBundleRetailPrice() {
		return bundleRetailPrice;
	}
	public void setBundleRetailPrice(long bundleRetailPrice) {
		this.bundleRetailPrice = bundleRetailPrice;
	}
	public String getBundlePrefix() {
		return bundlePrefix;
	}
	public void setBundlePrefix(String bundlePrefix) {
		this.bundlePrefix = bundlePrefix;
	}
	public long getBundleLastSequence() {
		return bundleLastSequence;
	}
	public void setBundleLastSequence(long bundleLastSequence) {
		this.bundleLastSequence = bundleLastSequence;
	}
	public long getBundleCount() {
		return bundleCount;
	}
	public void setBundleCount(long bundlecount) {
		this.bundleCount = bundlecount;
	}
	public String getTransferID() {
		return transferID;
	}
	public void setTransferID(String transferID) {
		this.transferID = transferID;
	}
}

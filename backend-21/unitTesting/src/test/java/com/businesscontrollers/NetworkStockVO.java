package com.businesscontrollers;

import java.util.HashMap;

public class NetworkStockVO {

	private HashMap<String, String> focPreBalances;
	private HashMap<String, String> incentivePreBalances;
	private HashMap<String, String> salePostBalances;
	private HashMap<String, String> focPostBalances;
	private HashMap<String, String> incentivePostBalances;
	
	public HashMap<String, String> getFocPreBalances() {
		return focPreBalances;
	}
	public void setFocPreBalances(HashMap<String, String> focPreBalances) {
		this.focPreBalances = focPreBalances;
	}
	public HashMap<String, String> getIncentivePreBalances() {
		return incentivePreBalances;
	}
	public void setIncentivePreBalances(HashMap<String, String> incentivePreBalances) {
		this.incentivePreBalances = incentivePreBalances;
	}
	public HashMap<String, String> getSalePostBalances() {
		return salePostBalances;
	}
	public void setSalePostBalances(HashMap<String, String> salePostBalances) {
		this.salePostBalances = salePostBalances;
	}
	public HashMap<String, String> getFocPostBalances() {
		return focPostBalances;
	}
	public void setFocPostBalances(HashMap<String, String> focPostBalances) {
		this.focPostBalances = focPostBalances;
	}
	public HashMap<String, String> getIncentivePostBalances() {
		return incentivePostBalances;
	}
	public void setIncentivePostBalances(HashMap<String, String> incentivePostBalances) {
		this.incentivePostBalances = incentivePostBalances;
	}
		
}
